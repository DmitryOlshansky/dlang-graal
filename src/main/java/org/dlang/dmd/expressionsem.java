package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.access.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.aliasthis.*;
import static org.dlang.dmd.arrayop.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.astcodegen.*;
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.canthrow.*;
import static org.dlang.dmd.ctorflow.*;
import static org.dlang.dmd.dcast.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.delegatize.*;
import static org.dlang.dmd.denum.*;
import static org.dlang.dmd.dimport.*;
import static org.dlang.dmd.dinterpret.*;
import static org.dlang.dmd.dmangle.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.escape.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.hdrgen.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.imphint.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.initsem.*;
import static org.dlang.dmd.inline.*;
import static org.dlang.dmd.intrange.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.nspace.*;
import static org.dlang.dmd.opover.*;
import static org.dlang.dmd.optimize.*;
import static org.dlang.dmd.parse.*;
import static org.dlang.dmd.safe.*;
import static org.dlang.dmd.semantic2.*;
import static org.dlang.dmd.semantic3.*;
import static org.dlang.dmd.sideeffect.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.traits.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.typinf.*;
import static org.dlang.dmd.utf.*;
import static org.dlang.dmd.utils.*;
import static org.dlang.dmd.visitor.*;

public class expressionsem {
    static Import loadStdMathimpStdMath = null;
    static int visitnest;
    static ByteSlice visitcompMsg = new ByteSlice("==");

    static boolean LOGSEMANTIC = false;
    public static boolean expressionsToString(OutBuffer buf, Scope sc, DArray<Expression> exps) {
        if (exps == null)
            return false;
        {
            Slice<Expression> __r1354 = (exps).opSlice().copy();
            int __key1355 = 0;
            for (; __key1355 < __r1354.getLength();__key1355 += 1) {
                Expression ex = __r1354.get(__key1355);
                if (!(ex != null))
                    continue;
                Scope sc2 = (sc).startCTFE();
                Expression e2 = expressionSemantic(ex, sc2);
                Expression e3 = resolveProperties(sc2, e2);
                (sc2).endCTFE();
                Expression e4 = ctfeInterpretForPragmaMsg(e3);
                if ((!(e4 != null) || (e4.op & 0xFF) == 127))
                    return true;
                {
                    TupleExp te = e4.isTupleExp();
                    if (te != null)
                    {
                        if (expressionsToString(buf, sc, te.exps))
                            return true;
                        continue;
                    }
                }
                IntegerExp ie = e4.isIntegerExp();
                int ty = (ie != null && ie.type != null) ? (ie.type.ty & 0xFF) : 34;
                if (((ty == 31 || ty == 32) || ty == 33))
                {
                    TypeSArray tsa = new TypeSArray(ie.type, new IntegerExp(1L));
                    e4 = new ArrayLiteralExp(ex.loc, tsa, ie);
                }
                {
                    StringExp se = e4.toStringExp();
                    if (se != null)
                        buf.writestring(se.toUTF8(sc).peekSlice());
                    else
                        buf.writestring(e4.asString());
                }
            }
        }
        return false;
    }

    public static StringExp semanticString(Scope sc, Expression exp, BytePtr s) {
        sc = (sc).startCTFE();
        exp = expressionSemantic(exp, sc);
        exp = resolveProperties(sc, exp);
        sc = (sc).endCTFE();
        if ((exp.op & 0xFF) == 127)
            return null;
        Expression e = exp;
        if (exp.type.isString())
        {
            e = e.ctfeInterpret();
            if ((e.op & 0xFF) == 127)
                return null;
        }
        StringExp se = e.toStringExp();
        if (!(se != null))
        {
            exp.error(new BytePtr("`string` expected for %s, not `(%s)` of type `%s`"), s, exp.toChars(), exp.type.toChars());
            return null;
        }
        return se;
    }

    public static Expression extractOpDollarSideEffect(Scope sc, UnaExp ue) {
        Ref<Expression> e0 = ref(null);
        Expression e1 = Expression.extractLast(ue.e1, e0);
        if (!(isTrivialExp(e1)))
        {
            e1 = extractSideEffect(sc, new BytePtr("__dop"), e0, e1, false);
            assert((e1.op & 0xFF) == 26);
            VarExp ve = (VarExp)e1;
            ve.var.storage_class |= 140737488355328L;
        }
        ue.e1 = e1;
        return e0.value;
    }

    public static Expression resolveOpDollar(Scope sc, ArrayExp ae, Ptr<Expression> pe0) {
        assert(!(ae.lengthVar != null));
        pe0.set(0, null);
        AggregateDeclaration ad = isAggregate(ae.e1.type);
        Dsymbol slice = search_function(ad, Id.slice);
        {
            Slice<Expression> __r1357 = (ae.arguments).opSlice().copy();
            int __key1356 = 0;
        L_outer1:
            for (; __key1356 < __r1357.getLength();__key1356 += 1) {
                Expression e = __r1357.get(__key1356);
                int i = __key1356;
                if (i == 0)
                    pe0.set(0, extractOpDollarSideEffect(sc, ae));
                if (((e.op & 0xFF) == 231 && !((slice != null && slice.isTemplateDeclaration() != null))))
                {
                /*Lfallback:*/
                    if ((ae.arguments).length == 1)
                        return null;
                    ae.error(new BytePtr("multi-dimensional slicing requires template `opSlice`"));
                    return new ErrorExp();
                }
                ArrayScopeSymbol sym = new ArrayScopeSymbol(sc, ae);
                sym.parent = (sc).scopesym;
                sc = (sc).push(sym);
                ae.lengthVar = null;
                ae.currentDimension = i;
                e = expressionSemantic(e, sc);
                e = resolveProperties(sc, e);
                if ((ae.lengthVar != null && (sc).func != null))
                {
                    Expression de = new DeclarationExp(ae.loc, ae.lengthVar);
                    de = expressionSemantic(de, sc);
                    pe0.set(0, Expression.combine(pe0.get(), de));
                }
                sc = (sc).pop();
                if ((e.op & 0xFF) == 231)
                {
                    IntervalExp ie = (IntervalExp)e;
                    DArray<RootObject> tiargs = new DArray<RootObject>();
                    Expression edim = new IntegerExp(ae.loc, (long)i, Type.tsize_t);
                    edim = expressionSemantic(edim, sc);
                    (tiargs).push(edim);
                    DArray<Expression> fargs = new DArray<Expression>(2);
                    fargs.set(0, ie.lwr);
                    fargs.set(1, ie.upr);
                    int xerrors = global.startGagging();
                    sc = (sc).push();
                    FuncDeclaration fslice = resolveFuncCall(ae.loc, sc, slice, tiargs, ae.e1.type, fargs, FuncResolveFlag.quiet);
                    sc = (sc).pop();
                    global.endGagging(xerrors);
                    if (!(fslice != null))
                        /*goto Lfallback*/throw Dispatch0.INSTANCE;
                    e = new DotTemplateInstanceExp(ae.loc, ae.e1, slice.ident, tiargs);
                    e = new CallExp(ae.loc, e, fargs);
                    e = expressionSemantic(e, sc);
                }
                if (!(e.type != null))
                {
                    ae.error(new BytePtr("`%s` has no value"), e.toChars());
                    e = new ErrorExp();
                }
                if ((e.op & 0xFF) == 127)
                    return e;
                ae.arguments.set(i, e);
            }
        }
        return ae;
    }

    public static Expression resolveOpDollar(Scope sc, ArrayExp ae, IntervalExp ie, Ptr<Expression> pe0) {
        if (!(ie != null))
            return ae;
        VarDeclaration lengthVar = ae.lengthVar;
        ArrayScopeSymbol sym = new ArrayScopeSymbol(sc, ae);
        sym.parent = (sc).scopesym;
        sc = (sc).push(sym);
        {
            int __key1358 = 0;
            int __limit1359 = 2;
            for (; __key1358 < __limit1359;__key1358 += 1) {
                int i = __key1358;
                Expression e = i == 0 ? ie.lwr : ie.upr;
                e = expressionSemantic(e, sc);
                e = resolveProperties(sc, e);
                if (!(e.type != null))
                {
                    ae.error(new BytePtr("`%s` has no value"), e.toChars());
                    return new ErrorExp();
                }
                (i == 0 ? ie.lwr : ie.upr).set(0, e);
            }
        }
        if ((!pequals(lengthVar, ae.lengthVar) && (sc).func != null))
        {
            Expression de = new DeclarationExp(ae.loc, ae.lengthVar);
            de = expressionSemantic(de, sc);
            pe0.set(0, Expression.combine(pe0.get(), de));
        }
        sc = (sc).pop();
        return ae;
    }

    public static boolean arrayExpressionSemantic(DArray<Expression> exps, Scope sc, boolean preserveErrors) {
        boolean err = false;
        if (exps != null)
        {
            {
                Slice<Expression> __r1360 = (exps).opSlice().copy();
                int __key1361 = 0;
                for (; __key1361 < __r1360.getLength();__key1361 += 1) {
                    Expression e = __r1360.get(__key1361);
                    if (e != null)
                    {
                        Expression e2 = expressionSemantic(e, sc);
                        if ((e2.op & 0xFF) == 127)
                            err = true;
                        if ((preserveErrors || (e2.op & 0xFF) != 127))
                            e = e2;
                    }
                }
            }
        }
        return err;
    }

    public static boolean checkPropertyCall(Expression e) {
        e = lastComma(e);
        if ((e.op & 0xFF) == 18)
        {
            CallExp ce = (CallExp)e;
            TypeFunction tf = null;
            if (ce.f != null)
            {
                tf = (TypeFunction)ce.f.type;
                if ((tf.deco == null && ce.f.semanticRun < PASS.semanticdone))
                {
                    dsymbolSemantic(ce.f, null);
                    tf = (TypeFunction)ce.f.type;
                }
            }
            else if ((ce.e1.type.ty & 0xFF) == ENUMTY.Tfunction)
                tf = (TypeFunction)ce.e1.type;
            else if ((ce.e1.type.ty & 0xFF) == ENUMTY.Tdelegate)
                tf = (TypeFunction)ce.e1.type.nextOf();
            else if (((ce.e1.type.ty & 0xFF) == ENUMTY.Tpointer && (ce.e1.type.nextOf().ty & 0xFF) == ENUMTY.Tfunction))
                tf = (TypeFunction)ce.e1.type.nextOf();
            else
                throw new AssertionError("Unreachable code!");
        }
        return false;
    }

    public static Expression searchUFCS(Scope sc, UnaExp ue, Identifier ident) {
        Ref<Scope> sc_ref = ref(sc);
        Ref<Identifier> ident_ref = ref(ident);
        Loc loc = ue.loc.copy();
        Function1<Integer,Dsymbol> searchScopes = new Function1<Integer,Dsymbol>(){
            public Dsymbol invoke(Integer flags){
                Dsymbol s = null;
                {
                    Scope scx = sc_ref.value;
                    for (; scx != null;scx = (scx).enclosing){
                        if (!((scx).scopesym != null))
                            continue;
                        if ((scx).scopesym.isModule() != null)
                            flags |= 32;
                        s = (scx).scopesym.search(loc, ident_ref.value, flags);
                        if (s != null)
                        {
                            if (s.isOverloadSet() != null)
                                break;
                            {
                                AliasDeclaration ad = s.isAliasDeclaration();
                                if (ad != null)
                                {
                                    if (ad._import != null)
                                        break;
                                }
                            }
                            Dsymbol p = s.toParent2();
                            if ((p != null && p.isModule() != null))
                                break;
                        }
                        s = null;
                        if (((scx).scopesym.isModule() != null && !(((scx).enclosing != null && ((scx).enclosing).enclosing == null))))
                            break;
                    }
                }
                return s;
            }
        };
        int flags = 0;
        Dsymbol s = null;
        if (((sc_ref.value).flags & 512) != 0)
            flags |= 128;
        s = searchScopes.invoke(flags | 8);
        if (!(s != null))
        {
            s = searchScopes.invoke(flags | 16);
        }
        if (!(s != null))
            return getProperty(ue.e1.type.Type, loc, ident_ref.value, 0);
        FuncDeclaration f = s.isFuncDeclaration();
        if (f != null)
        {
            TemplateDeclaration td = getFuncTemplateDecl(f);
            if (td != null)
            {
                if (td.overroot != null)
                    td = td.overroot;
                s = td;
            }
        }
        if ((ue.op & 0xFF) == 29)
        {
            DotTemplateInstanceExp dti = (DotTemplateInstanceExp)ue;
            TemplateInstance ti = new TemplateInstance(loc, s.ident, dti.ti.tiargs);
            if (!(ti.updateTempDecl(sc_ref.value, s)))
                return new ErrorExp();
            return new ScopeExp(loc, ti);
        }
        else
        {
            return new DsymbolExp(loc, s, true);
        }
    }

    public static Expression resolveUFCS(Scope sc, CallExp ce) {
        Loc loc = ce.loc.copy();
        Expression eleft = null;
        Expression e = null;
        if ((ce.e1.op & 0xFF) == 28)
        {
            DotIdExp die = (DotIdExp)ce.e1;
            Identifier ident = die.ident;
            Expression ex = semanticX(die, sc);
            if (!pequals(ex, die))
            {
                ce.e1 = ex;
                return null;
            }
            eleft = die.e1;
            Type t = eleft.type.toBasetype();
            if (((((t.ty & 0xFF) == ENUMTY.Tarray || (t.ty & 0xFF) == ENUMTY.Tsarray) || (t.ty & 0xFF) == ENUMTY.Tnull) || (t.isTypeBasic() != null && (t.ty & 0xFF) != ENUMTY.Tvoid)))
            {
            }
            else if ((t.ty & 0xFF) == ENUMTY.Taarray)
            {
                if (pequals(ident, Id.remove))
                {
                    if ((ce.arguments == null || (ce.arguments).length != 1))
                    {
                        ce.error(new BytePtr("expected key as argument to `aa.remove()`"));
                        return new ErrorExp();
                    }
                    if (!(eleft.type.isMutable()))
                    {
                        ce.error(new BytePtr("cannot remove key from `%s` associative array `%s`"), MODtoChars(t.mod), eleft.toChars());
                        return new ErrorExp();
                    }
                    Expression key = (ce.arguments).get(0);
                    key = expressionSemantic(key, sc);
                    key = resolveProperties(sc, key);
                    TypeAArray taa = (TypeAArray)t;
                    key = key.implicitCastTo(sc, taa.index);
                    if (key.checkValue())
                        return new ErrorExp();
                    semanticTypeInfo(sc, taa.index);
                    return new RemoveExp(loc, eleft, key);
                }
            }
            else
            {
                if (arrayExpressionSemantic(ce.arguments, sc, false))
                    return new ErrorExp();
                {
                    Expression ey = semanticY(die, sc, 1);
                    if (ey != null)
                    {
                        if ((ey.op & 0xFF) == 127)
                            return ey;
                        ce.e1 = ey;
                        if (isDotOpDispatch(ey))
                        {
                            int errors = global.startGagging();
                            e = expressionSemantic(ce.syntaxCopy(), sc);
                            if (!(global.endGagging(errors)))
                                return e;
                        }
                        else
                            return null;
                    }
                }
            }
            int errors = global.startGagging();
            e = searchUFCS(sc, die, ident);
            if (global.endGagging(errors))
            {
                if (pequals(ident, Id.remove))
                {
                    Expression alias_e = resolveAliasThis(sc, die.e1, true);
                    if ((alias_e != null && !pequals(alias_e, die.e1)))
                    {
                        die.e1 = alias_e;
                        CallExp ce2 = (CallExp)ce.syntaxCopy();
                        ce2.e1 = die;
                        e = (CallExp)trySemantic(ce2, sc);
                        if (e != null)
                            return e;
                    }
                }
                searchUFCS(sc, die, ident);
            }
        }
        else if ((ce.e1.op & 0xFF) == 29)
        {
            DotTemplateInstanceExp dti = (DotTemplateInstanceExp)ce.e1;
            {
                Expression ey = semanticY(dti, sc, 1);
                if (ey != null)
                {
                    ce.e1 = ey;
                    return null;
                }
            }
            eleft = dti.e1;
            e = searchUFCS(sc, dti, dti.ti.name);
        }
        else
            return null;
        ce.e1 = e;
        if (ce.arguments == null)
            ce.arguments = new DArray<Expression>();
        (ce.arguments).shift(eleft);
        return null;
    }

    public static Expression resolveUFCSProperties(Scope sc, Expression e1, Expression e2) {
        Loc loc = e1.loc.copy();
        Expression eleft = null;
        Expression e = null;
        if ((e1.op & 0xFF) == 28)
        {
            DotIdExp die = (DotIdExp)e1;
            eleft = die.e1;
            e = searchUFCS(sc, die, die.ident);
        }
        else if ((e1.op & 0xFF) == 29)
        {
            DotTemplateInstanceExp dti = null;
            dti = (DotTemplateInstanceExp)e1;
            eleft = dti.e1;
            e = searchUFCS(sc, dti, dti.ti.name);
        }
        else
            return null;
        if (e == null)
            return null;
        if (e2 != null)
        {
            e2 = expressionSemantic(e2, sc);
            Expression ex = e.copy();
            DArray<Expression> a1 = new DArray<Expression>(1);
            a1.set(0, eleft);
            ex = new CallExp(loc, ex, a1);
            ex = trySemantic(ex, sc);
            DArray<Expression> a2 = new DArray<Expression>(2);
            a2.set(0, eleft);
            a2.set(1, e2);
            e = new CallExp(loc, e, a2);
            if (ex != null)
            {
                e = trySemantic(e, sc);
                if (!(e != null))
                {
                    checkPropertyCall(ex);
                    ex = new AssignExp(loc, ex, e2);
                    return expressionSemantic(ex, sc);
                }
            }
            else
            {
                e = expressionSemantic(e, sc);
            }
            checkPropertyCall(e);
            return e;
        }
        else
        {
            DArray<Expression> arguments = new DArray<Expression>(1);
            arguments.set(0, eleft);
            e = new CallExp(loc, e, arguments);
            e = expressionSemantic(e, sc);
            checkPropertyCall(e);
            return expressionSemantic(e, sc);
        }
    }

    public static Expression resolvePropertiesOnly(Scope sc, Expression e1) {
        OverloadSet os = null;
        FuncDeclaration fd = null;
        TemplateDeclaration td = null;
        if ((e1.op & 0xFF) == 97)
        {
            DotExp de = (DotExp)e1;
            if ((de.e2.op & 0xFF) == 214)
            {
                os = ((OverExp)de.e2).vars;
                /*goto Los*//*unrolled goto*/
            /*Los:*/
                assert(os != null);
                {
                    Slice<Dsymbol> __r1362 = os.a.opSlice().copy();
                    int __key1363 = 0;
                    for (; __key1363 < __r1362.getLength();__key1363 += 1) {
                        Dsymbol s = __r1362.get(__key1363);
                        fd = s.isFuncDeclaration();
                        td = s.isTemplateDeclaration();
                        if (fd != null)
                        {
                            if (((TypeFunction)fd.type).isproperty)
                                return resolveProperties(sc, e1);
                        }
                        else if (((td != null && td.onemember != null) && (fd = td.onemember.isFuncDeclaration()) != null))
                        {
                            if (((((TypeFunction)fd.type).isproperty || (fd.storage_class2 & 4294967296L) != 0) || ((td._scope).stc & 4294967296L) != 0))
                            {
                                return resolveProperties(sc, e1);
                            }
                        }
                    }
                }
            }
        }
        else if ((e1.op & 0xFF) == 214)
        {
            os = ((OverExp)e1).vars;
        /*Los:*/
            assert(os != null);
            {
                Slice<Dsymbol> __r1362 = os.a.opSlice().copy();
                int __key1363 = 0;
                for (; __key1363 < __r1362.getLength();__key1363 += 1) {
                    Dsymbol s = __r1362.get(__key1363);
                    fd = s.isFuncDeclaration();
                    td = s.isTemplateDeclaration();
                    if (fd != null)
                    {
                        if (((TypeFunction)fd.type).isproperty)
                            return resolveProperties(sc, e1);
                    }
                    else if (((td != null && td.onemember != null) && (fd = td.onemember.isFuncDeclaration()) != null))
                    {
                        if (((((TypeFunction)fd.type).isproperty || (fd.storage_class2 & 4294967296L) != 0) || ((td._scope).stc & 4294967296L) != 0))
                        {
                            return resolveProperties(sc, e1);
                        }
                    }
                }
            }
        }
        else if ((e1.op & 0xFF) == 29)
        {
            DotTemplateInstanceExp dti = (DotTemplateInstanceExp)e1;
            if ((dti.ti.tempdecl != null && (td = dti.ti.tempdecl.isTemplateDeclaration()) != null))
                /*goto Ltd*//*unrolled goto*/
            /*Ltd:*/
                assert(td != null);
                if ((td.onemember != null && (fd = td.onemember.isFuncDeclaration()) != null))
                {
                    if (((((TypeFunction)fd.type).isproperty || (fd.storage_class2 & 4294967296L) != 0) || ((td._scope).stc & 4294967296L) != 0))
                    {
                        return resolveProperties(sc, e1);
                    }
                }
        }
        else if ((e1.op & 0xFF) == 37)
        {
            td = ((DotTemplateExp)e1).td;
            /*goto Ltd*//*unrolled goto*/
        /*Ltd:*/
            assert(td != null);
            if ((td.onemember != null && (fd = td.onemember.isFuncDeclaration()) != null))
            {
                if (((((TypeFunction)fd.type).isproperty || (fd.storage_class2 & 4294967296L) != 0) || ((td._scope).stc & 4294967296L) != 0))
                {
                    return resolveProperties(sc, e1);
                }
            }
        }
        else if ((e1.op & 0xFF) == 203)
        {
            Dsymbol s = ((ScopeExp)e1).sds;
            TemplateInstance ti = s.isTemplateInstance();
            if (((ti != null && !((ti.semanticRun) != 0)) && ti.tempdecl != null))
            {
                if ((td = ti.tempdecl.isTemplateDeclaration()) != null)
                    /*goto Ltd*//*unrolled goto*/
                /*Ltd:*/
                    assert(td != null);
                    if ((td.onemember != null && (fd = td.onemember.isFuncDeclaration()) != null))
                    {
                        if (((((TypeFunction)fd.type).isproperty || (fd.storage_class2 & 4294967296L) != 0) || ((td._scope).stc & 4294967296L) != 0))
                        {
                            return resolveProperties(sc, e1);
                        }
                    }
            }
        }
        else if ((e1.op & 0xFF) == 36)
        {
            td = ((TemplateExp)e1).td;
        /*Ltd:*/
            assert(td != null);
            if ((td.onemember != null && (fd = td.onemember.isFuncDeclaration()) != null))
            {
                if (((((TypeFunction)fd.type).isproperty || (fd.storage_class2 & 4294967296L) != 0) || ((td._scope).stc & 4294967296L) != 0))
                {
                    return resolveProperties(sc, e1);
                }
            }
        }
        else if (((e1.op & 0xFF) == 27 && (e1.type.ty & 0xFF) == ENUMTY.Tfunction))
        {
            DotVarExp dve = (DotVarExp)e1;
            fd = dve.var.isFuncDeclaration();
            /*goto Lfd*//*unrolled goto*/
        /*Lfd:*/
            assert(fd != null);
            if (((TypeFunction)fd.type).isproperty)
                return resolveProperties(sc, e1);
        }
        else if (((((e1.op & 0xFF) == 26 && e1.type != null) && (e1.type.ty & 0xFF) == ENUMTY.Tfunction) && (((sc).intypeof) != 0 || !(((VarExp)e1).var.needThis()))))
        {
            fd = ((VarExp)e1).var.isFuncDeclaration();
        /*Lfd:*/
            assert(fd != null);
            if (((TypeFunction)fd.type).isproperty)
                return resolveProperties(sc, e1);
        }
        return e1;
    }

    public static Expression symbolToExp(Dsymbol s, Loc loc, Scope sc, boolean hasOverloads) {
        while(true) try {
        /*Lagain:*/
            Expression e = null;
            Dsymbol olds = s;
            Declaration d = s.isDeclaration();
            if ((d != null && (d.storage_class & 262144L) != 0))
            {
                s = s.toAlias();
            }
            else
            {
                if (!(s.isFuncDeclaration() != null))
                {
                    s.checkDeprecated(loc, sc);
                    if (d != null)
                        d.checkDisabled(loc, sc, false);
                }
                s = s.toAlias();
                if ((!pequals(s, olds) && !(s.isFuncDeclaration() != null)))
                {
                    s.checkDeprecated(loc, sc);
                    if (d != null)
                        d.checkDisabled(loc, sc, false);
                }
            }
            {
                EnumMember em = s.isEnumMember();
                if (em != null)
                {
                    return em.getVarExp(loc, sc);
                }
            }
            {
                VarDeclaration v = s.isVarDeclaration();
                if (v != null)
                {
                    if (((sc).intypeof == 1 && !((v.inuse) != 0)))
                        dsymbolSemantic(v, sc);
                    if ((!(v.type != null) || (v.type.deco == null && (v.inuse) != 0)))
                    {
                        if ((v.inuse) != 0)
                            error(loc, new BytePtr("circular reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                        else
                            error(loc, new BytePtr("forward reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                        return new ErrorExp();
                    }
                    if ((v.type.ty & 0xFF) == ENUMTY.Terror)
                        return new ErrorExp();
                    if (((v.storage_class & 8388608L) != 0 && v._init != null))
                    {
                        if ((v.inuse) != 0)
                        {
                            error(loc, new BytePtr("circular initialization of %s `%s`"), v.kind(), v.toPrettyChars(false));
                            return new ErrorExp();
                        }
                        e = v.expandInitializer(loc);
                        v.inuse++;
                        e = expressionSemantic(e, sc);
                        v.inuse--;
                        return e;
                    }
                    if (v.checkNestedReference(sc, loc))
                        return new ErrorExp();
                    if ((v.needThis() && hasThis(sc) != null))
                        e = new DotVarExp(loc, new ThisExp(loc), v, true);
                    else
                        e = new VarExp(loc, v, true);
                    e = expressionSemantic(e, sc);
                    return e;
                }
            }
            {
                FuncLiteralDeclaration fld = s.isFuncLiteralDeclaration();
                if (fld != null)
                {
                    e = new FuncExp(loc, fld);
                    return expressionSemantic(e, sc);
                }
            }
            {
                FuncDeclaration f = s.isFuncDeclaration();
                if (f != null)
                {
                    f = f.toAliasFunc();
                    if (!(f.functionSemantic()))
                        return new ErrorExp();
                    if ((!(hasOverloads) && f.checkForwardRef(loc)))
                        return new ErrorExp();
                    FuncDeclaration fd = s.isFuncDeclaration();
                    fd.type = f.type;
                    return new VarExp(loc, fd, hasOverloads);
                }
            }
            {
                OverDeclaration od = s.isOverDeclaration();
                if (od != null)
                {
                    e = new VarExp(loc, od, true);
                    e.type = Type.tvoid;
                    return e;
                }
            }
            {
                OverloadSet o = s.isOverloadSet();
                if (o != null)
                {
                    return new OverExp(loc, o);
                }
            }
            {
                Import imp = s.isImport();
                if (imp != null)
                {
                    if (!(imp.pkg != null))
                    {
                        error(loc, new BytePtr("forward reference of import `%s`"), imp.toChars());
                        return new ErrorExp();
                    }
                    ScopeExp ie = new ScopeExp(loc, imp.pkg);
                    return expressionSemantic(ie, sc);
                }
            }
            {
                dmodule.Package pkg = s.isPackage();
                if (pkg != null)
                {
                    ScopeExp ie = new ScopeExp(loc, pkg);
                    return expressionSemantic(ie, sc);
                }
            }
            {
                dmodule.Module mod = s.isModule();
                if (mod != null)
                {
                    ScopeExp ie = new ScopeExp(loc, mod);
                    return expressionSemantic(ie, sc);
                }
            }
            {
                Nspace ns = s.isNspace();
                if (ns != null)
                {
                    ScopeExp ie = new ScopeExp(loc, ns);
                    return expressionSemantic(ie, sc);
                }
            }
            {
                Type t = s.getType();
                if (t != null)
                {
                    return expressionSemantic(new TypeExp(loc, t), sc);
                }
            }
            {
                TupleDeclaration tup = s.isTupleDeclaration();
                if (tup != null)
                {
                    if ((tup.needThis() && hasThis(sc) != null))
                        e = new DotVarExp(loc, new ThisExp(loc), tup, true);
                    else
                        e = new TupleExp(loc, tup);
                    e = expressionSemantic(e, sc);
                    return e;
                }
            }
            {
                TemplateInstance ti = s.isTemplateInstance();
                if (ti != null)
                {
                    dsymbolSemantic(ti, sc);
                    if ((!(ti.inst != null) || ti.errors))
                        return new ErrorExp();
                    s = ti.toAlias();
                    if (!(s.isTemplateInstance() != null))
                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                    e = new ScopeExp(loc, ti);
                    e = expressionSemantic(e, sc);
                    return e;
                }
            }
            {
                TemplateDeclaration td = s.isTemplateDeclaration();
                if (td != null)
                {
                    Dsymbol p = td.toParentLocal();
                    FuncDeclaration fdthis = hasThis(sc);
                    AggregateDeclaration ad = p != null ? p.isAggregateDeclaration() : null;
                    if ((((fdthis != null && ad != null) && pequals(fdthis.isMemberLocal(), ad)) && ((td._scope).stc & 1L) == 0L))
                    {
                        e = new DotTemplateExp(loc, new ThisExp(loc), td);
                    }
                    else
                        e = new TemplateExp(loc, td, null);
                    e = expressionSemantic(e, sc);
                    return e;
                }
            }
            error(loc, new BytePtr("%s `%s` is not a variable"), s.kind(), s.toChars());
            return new ErrorExp();
            break;
        } catch(Dispatch0 __d){}
    }

    public static Expression getRightThis(Loc loc, Scope sc, AggregateDeclaration ad, Expression e1, Dsymbol var, int flag) {
        while(true) try {
        /*L1:*/
            Type t = e1.type.toBasetype();
            if ((e1.op & 0xFF) == 235)
            {
                return e1;
            }
            else if ((((((ad != null && ad.isClassDeclaration() != null) && ad.isClassDeclaration().classKind == ClassKind.objc) && var.isFuncDeclaration() != null) && var.isFuncDeclaration().isStatic()) && var.isFuncDeclaration().selector != null))
            {
                return new ObjcClassReferenceExp(e1.loc, (ClassDeclaration)ad);
            }
            if ((e1.op & 0xFF) == 123)
            {
                FuncDeclaration f = hasThis(sc);
                if ((f != null && f.isThis2))
                {
                    if (followInstantiationContextAggregateDeclaration(f, ad))
                    {
                        e1 = new VarExp(loc, f.vthis, true);
                        e1 = new PtrExp(loc, e1);
                        e1 = new IndexExp(loc, e1, literal1());
                        e1 = getThisSkipNestedFuncs(loc, sc, f.toParent2(), ad, e1, t, var, false);
                        if ((e1.op & 0xFF) == 127)
                            return e1;
                        /*goto L1*/throw Dispatch0.INSTANCE;
                    }
                }
            }
            if (((ad != null && !((((t.ty & 0xFF) == ENUMTY.Tpointer && (t.nextOf().ty & 0xFF) == ENUMTY.Tstruct) && pequals(((TypeStruct)t.nextOf()).sym, ad)))) && !(((t.ty & 0xFF) == ENUMTY.Tstruct && pequals(((TypeStruct)t).sym, ad)))))
            {
                ClassDeclaration cd = ad.isClassDeclaration();
                ClassDeclaration tcd = t.isClassHandle();
                if (((!(cd != null) || !(tcd != null)) || !((pequals(tcd, cd) || cd.isBaseOf(tcd, null)))))
                {
                    if ((tcd != null && tcd.isNested()))
                    {
                        VarDeclaration vthis = followInstantiationContextAggregateDeclaration(tcd, ad) ? tcd.vthis2 : tcd.vthis;
                        e1 = new DotVarExp(loc, e1, vthis, true);
                        e1.type = vthis.type;
                        e1.type = e1.type.addMod(t.mod);
                        e1 = getThisSkipNestedFuncs(loc, sc, toParentPAggregateDeclaration(tcd, ad), ad, e1, t, var, false);
                        if ((e1.op & 0xFF) == 127)
                            return e1;
                        /*goto L1*/throw Dispatch0.INSTANCE;
                    }
                    if ((flag) != 0)
                        return null;
                    e1.error(new BytePtr("`this` for `%s` needs to be type `%s` not type `%s`"), var.toChars(), ad.toChars(), t.toChars());
                    return new ErrorExp();
                }
            }
            return e1;
            break;
        } catch(Dispatch0 __d){}
    }

    public static Expression resolvePropertiesX(Scope sc, Expression e1, Expression e2) {
        Loc loc = e1.loc.copy();
        OverloadSet os = null;
        Dsymbol s = null;
        DArray<RootObject> tiargs = null;
        Type tthis = null;
        try {
            try {
                if ((e1.op & 0xFF) == 97)
                {
                    DotExp de = (DotExp)e1;
                    if ((de.e2.op & 0xFF) == 214)
                    {
                        tiargs = null;
                        tthis = de.e1.type;
                        os = ((OverExp)de.e2).vars;
                        /*goto Los*//*unrolled goto*/
                    /*Los:*/
                        assert(os != null);
                        FuncDeclaration fd = null;
                        if (e2 != null)
                        {
                            e2 = expressionSemantic(e2, sc);
                            if ((e2.op & 0xFF) == 127)
                                return new ErrorExp();
                            e2 = resolveProperties(sc, e2);
                            DArray<Expression> a = new DArray<Expression>();
                            try {
                                a.push(e2);
                                {
                                    int i = 0;
                                    for (; i < os.a.length;i++){
                                        {
                                            FuncDeclaration f = resolveFuncCall(loc, sc, os.a.get(i), tiargs, tthis, a, FuncResolveFlag.quiet);
                                            if (f != null)
                                            {
                                                if (f.errors)
                                                    return new ErrorExp();
                                                fd = f;
                                                assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                                            }
                                        }
                                    }
                                }
                                if (fd != null)
                                {
                                    Expression e = new CallExp(loc, e1, e2);
                                    return expressionSemantic(e, sc);
                                }
                            }
                            finally {
                            }
                        }
                        {
                            {
                                int i = 0;
                            L_outer2:
                                for (; i < os.a.length;i++){
                                    {
                                        FuncDeclaration f = resolveFuncCall(loc, sc, os.a.get(i), tiargs, tthis, null, FuncResolveFlag.quiet);
                                        if (f != null)
                                        {
                                            if (f.errors)
                                                return new ErrorExp();
                                            fd = f;
                                            assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                                            TypeFunction tf = (TypeFunction)fd.type;
                                            if ((!(tf.isref) && e2 != null))
                                                /*goto Leproplvalue*/throw Dispatch1.INSTANCE;
                                        }
                                    }
                                }
                            }
                            if (fd != null)
                            {
                                Expression e = new CallExp(loc, e1);
                                if (e2 != null)
                                    e = new AssignExp(loc, e, e2);
                                return expressionSemantic(e, sc);
                            }
                        }
                        if (e2 != null)
                            /*goto Leprop*/throw Dispatch0.INSTANCE;
                    }
                }
                else if ((e1.op & 0xFF) == 214)
                {
                    tiargs = null;
                    tthis = null;
                    os = ((OverExp)e1).vars;
                /*Los:*/
                    assert(os != null);
                    FuncDeclaration fd = null;
                    if (e2 != null)
                    {
                        e2 = expressionSemantic(e2, sc);
                        if ((e2.op & 0xFF) == 127)
                            return new ErrorExp();
                        e2 = resolveProperties(sc, e2);
                        DArray<Expression> a = new DArray<Expression>();
                        try {
                            a.push(e2);
                            {
                                int i = 0;
                                for (; i < os.a.length;i++){
                                    {
                                        FuncDeclaration f = resolveFuncCall(loc, sc, os.a.get(i), tiargs, tthis, a, FuncResolveFlag.quiet);
                                        if (f != null)
                                        {
                                            if (f.errors)
                                                return new ErrorExp();
                                            fd = f;
                                            assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                                        }
                                    }
                                }
                            }
                            if (fd != null)
                            {
                                Expression e = new CallExp(loc, e1, e2);
                                return expressionSemantic(e, sc);
                            }
                        }
                        finally {
                        }
                    }
                    {
                        {
                            int i = 0;
                        L_outer3:
                            for (; i < os.a.length;i++){
                                {
                                    FuncDeclaration f = resolveFuncCall(loc, sc, os.a.get(i), tiargs, tthis, null, FuncResolveFlag.quiet);
                                    if (f != null)
                                    {
                                        if (f.errors)
                                            return new ErrorExp();
                                        fd = f;
                                        assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                                        TypeFunction tf = (TypeFunction)fd.type;
                                        if ((!(tf.isref) && e2 != null))
                                            /*goto Leproplvalue*/throw Dispatch1.INSTANCE;
                                    }
                                }
                            }
                        }
                        if (fd != null)
                        {
                            Expression e = new CallExp(loc, e1);
                            if (e2 != null)
                                e = new AssignExp(loc, e, e2);
                            return expressionSemantic(e, sc);
                        }
                    }
                    if (e2 != null)
                        /*goto Leprop*/throw Dispatch0.INSTANCE;
                }
                else if ((e1.op & 0xFF) == 29)
                {
                    DotTemplateInstanceExp dti = (DotTemplateInstanceExp)e1;
                    if (!(dti.findTempDecl(sc)))
                        /*goto Leprop*/throw Dispatch0.INSTANCE;
                    if (!(dti.ti.semanticTiargs(sc)))
                        /*goto Leprop*/throw Dispatch0.INSTANCE;
                    tiargs = dti.ti.tiargs;
                    tthis = dti.e1.type;
                    if ((os = dti.ti.tempdecl.isOverloadSet()) != null)
                        /*goto Los*/throw Dispatch0.INSTANCE;
                    if ((s = dti.ti.tempdecl) != null)
                        /*goto Lfd*//*unrolled goto*/
                    /*Lfd:*/
                        assert(s != null);
                        if (e2 != null)
                        {
                            e2 = expressionSemantic(e2, sc);
                            if ((e2.op & 0xFF) == 127)
                                return new ErrorExp();
                            e2 = resolveProperties(sc, e2);
                            DArray<Expression> a = new DArray<Expression>();
                            try {
                                a.push(e2);
                                FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, a, FuncResolveFlag.quiet);
                                if ((fd != null && fd.type != null))
                                {
                                    if (fd.errors)
                                        return new ErrorExp();
                                    assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                                    Expression e = new CallExp(loc, e1, e2);
                                    return expressionSemantic(e, sc);
                                }
                            }
                            finally {
                            }
                        }
                        {
                            FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, null, FuncResolveFlag.quiet);
                            if ((fd != null && fd.type != null))
                            {
                                if (fd.errors)
                                    return new ErrorExp();
                                assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                                TypeFunction tf = (TypeFunction)fd.type;
                                if ((!(e2 != null) || tf.isref))
                                {
                                    Expression e = new CallExp(loc, e1);
                                    if (e2 != null)
                                        e = new AssignExp(loc, e, e2);
                                    return expressionSemantic(e, sc);
                                }
                            }
                        }
                        {
                            FuncDeclaration fd = s.isFuncDeclaration();
                            if (fd != null)
                            {
                                assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                                Expression e = new CallExp(loc, e1, e2);
                                return expressionSemantic(e, sc);
                            }
                        }
                        if (e2 != null)
                            /*goto Leprop*/throw Dispatch0.INSTANCE;
                }
                else if ((e1.op & 0xFF) == 37)
                {
                    DotTemplateExp dte = (DotTemplateExp)e1;
                    s = dte.td;
                    tiargs = null;
                    tthis = dte.e1.type;
                    /*goto Lfd*//*unrolled goto*/
                /*Lfd:*/
                    assert(s != null);
                    if (e2 != null)
                    {
                        e2 = expressionSemantic(e2, sc);
                        if ((e2.op & 0xFF) == 127)
                            return new ErrorExp();
                        e2 = resolveProperties(sc, e2);
                        DArray<Expression> a = new DArray<Expression>();
                        try {
                            a.push(e2);
                            FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, a, FuncResolveFlag.quiet);
                            if ((fd != null && fd.type != null))
                            {
                                if (fd.errors)
                                    return new ErrorExp();
                                assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                                Expression e = new CallExp(loc, e1, e2);
                                return expressionSemantic(e, sc);
                            }
                        }
                        finally {
                        }
                    }
                    {
                        FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, null, FuncResolveFlag.quiet);
                        if ((fd != null && fd.type != null))
                        {
                            if (fd.errors)
                                return new ErrorExp();
                            assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                            TypeFunction tf = (TypeFunction)fd.type;
                            if ((!(e2 != null) || tf.isref))
                            {
                                Expression e = new CallExp(loc, e1);
                                if (e2 != null)
                                    e = new AssignExp(loc, e, e2);
                                return expressionSemantic(e, sc);
                            }
                        }
                    }
                    {
                        FuncDeclaration fd = s.isFuncDeclaration();
                        if (fd != null)
                        {
                            assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                            Expression e = new CallExp(loc, e1, e2);
                            return expressionSemantic(e, sc);
                        }
                    }
                    if (e2 != null)
                        /*goto Leprop*/throw Dispatch0.INSTANCE;
                }
                else if ((e1.op & 0xFF) == 203)
                {
                    s = ((ScopeExp)e1).sds;
                    TemplateInstance ti = s.isTemplateInstance();
                    if (((ti != null && !((ti.semanticRun) != 0)) && ti.tempdecl != null))
                    {
                        if (!(ti.semanticTiargs(sc)))
                            /*goto Leprop*/throw Dispatch0.INSTANCE;
                        tiargs = ti.tiargs;
                        tthis = null;
                        if ((os = ti.tempdecl.isOverloadSet()) != null)
                            /*goto Los*/throw Dispatch0.INSTANCE;
                        if ((s = ti.tempdecl) != null)
                            /*goto Lfd*//*unrolled goto*/
                        /*Lfd:*/
                            assert(s != null);
                            if (e2 != null)
                            {
                                e2 = expressionSemantic(e2, sc);
                                if ((e2.op & 0xFF) == 127)
                                    return new ErrorExp();
                                e2 = resolveProperties(sc, e2);
                                DArray<Expression> a = new DArray<Expression>();
                                try {
                                    a.push(e2);
                                    FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, a, FuncResolveFlag.quiet);
                                    if ((fd != null && fd.type != null))
                                    {
                                        if (fd.errors)
                                            return new ErrorExp();
                                        assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                                        Expression e = new CallExp(loc, e1, e2);
                                        return expressionSemantic(e, sc);
                                    }
                                }
                                finally {
                                }
                            }
                            {
                                FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, null, FuncResolveFlag.quiet);
                                if ((fd != null && fd.type != null))
                                {
                                    if (fd.errors)
                                        return new ErrorExp();
                                    assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                                    TypeFunction tf = (TypeFunction)fd.type;
                                    if ((!(e2 != null) || tf.isref))
                                    {
                                        Expression e = new CallExp(loc, e1);
                                        if (e2 != null)
                                            e = new AssignExp(loc, e, e2);
                                        return expressionSemantic(e, sc);
                                    }
                                }
                            }
                            {
                                FuncDeclaration fd = s.isFuncDeclaration();
                                if (fd != null)
                                {
                                    assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                                    Expression e = new CallExp(loc, e1, e2);
                                    return expressionSemantic(e, sc);
                                }
                            }
                            if (e2 != null)
                                /*goto Leprop*/throw Dispatch0.INSTANCE;
                    }
                }
                else if ((e1.op & 0xFF) == 36)
                {
                    s = ((TemplateExp)e1).td;
                    tiargs = null;
                    tthis = null;
                    /*goto Lfd*//*unrolled goto*/
                /*Lfd:*/
                    assert(s != null);
                    if (e2 != null)
                    {
                        e2 = expressionSemantic(e2, sc);
                        if ((e2.op & 0xFF) == 127)
                            return new ErrorExp();
                        e2 = resolveProperties(sc, e2);
                        DArray<Expression> a = new DArray<Expression>();
                        try {
                            a.push(e2);
                            FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, a, FuncResolveFlag.quiet);
                            if ((fd != null && fd.type != null))
                            {
                                if (fd.errors)
                                    return new ErrorExp();
                                assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                                Expression e = new CallExp(loc, e1, e2);
                                return expressionSemantic(e, sc);
                            }
                        }
                        finally {
                        }
                    }
                    {
                        FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, null, FuncResolveFlag.quiet);
                        if ((fd != null && fd.type != null))
                        {
                            if (fd.errors)
                                return new ErrorExp();
                            assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                            TypeFunction tf = (TypeFunction)fd.type;
                            if ((!(e2 != null) || tf.isref))
                            {
                                Expression e = new CallExp(loc, e1);
                                if (e2 != null)
                                    e = new AssignExp(loc, e, e2);
                                return expressionSemantic(e, sc);
                            }
                        }
                    }
                    {
                        FuncDeclaration fd = s.isFuncDeclaration();
                        if (fd != null)
                        {
                            assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                            Expression e = new CallExp(loc, e1, e2);
                            return expressionSemantic(e, sc);
                        }
                    }
                    if (e2 != null)
                        /*goto Leprop*/throw Dispatch0.INSTANCE;
                }
                else if ((((e1.op & 0xFF) == 27 && e1.type != null) && (e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tfunction))
                {
                    DotVarExp dve = (DotVarExp)e1;
                    s = dve.var.isFuncDeclaration();
                    tiargs = null;
                    tthis = dve.e1.type;
                    /*goto Lfd*//*unrolled goto*/
                /*Lfd:*/
                    assert(s != null);
                    if (e2 != null)
                    {
                        e2 = expressionSemantic(e2, sc);
                        if ((e2.op & 0xFF) == 127)
                            return new ErrorExp();
                        e2 = resolveProperties(sc, e2);
                        DArray<Expression> a = new DArray<Expression>();
                        try {
                            a.push(e2);
                            FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, a, FuncResolveFlag.quiet);
                            if ((fd != null && fd.type != null))
                            {
                                if (fd.errors)
                                    return new ErrorExp();
                                assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                                Expression e = new CallExp(loc, e1, e2);
                                return expressionSemantic(e, sc);
                            }
                        }
                        finally {
                        }
                    }
                    {
                        FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, null, FuncResolveFlag.quiet);
                        if ((fd != null && fd.type != null))
                        {
                            if (fd.errors)
                                return new ErrorExp();
                            assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                            TypeFunction tf = (TypeFunction)fd.type;
                            if ((!(e2 != null) || tf.isref))
                            {
                                Expression e = new CallExp(loc, e1);
                                if (e2 != null)
                                    e = new AssignExp(loc, e, e2);
                                return expressionSemantic(e, sc);
                            }
                        }
                    }
                    {
                        FuncDeclaration fd = s.isFuncDeclaration();
                        if (fd != null)
                        {
                            assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                            Expression e = new CallExp(loc, e1, e2);
                            return expressionSemantic(e, sc);
                        }
                    }
                    if (e2 != null)
                        /*goto Leprop*/throw Dispatch0.INSTANCE;
                }
                else if ((((e1.op & 0xFF) == 26 && e1.type != null) && (e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tfunction))
                {
                    s = ((VarExp)e1).var.isFuncDeclaration();
                    tiargs = null;
                    tthis = null;
                /*Lfd:*/
                    assert(s != null);
                    if (e2 != null)
                    {
                        e2 = expressionSemantic(e2, sc);
                        if ((e2.op & 0xFF) == 127)
                            return new ErrorExp();
                        e2 = resolveProperties(sc, e2);
                        DArray<Expression> a = new DArray<Expression>();
                        try {
                            a.push(e2);
                            FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, a, FuncResolveFlag.quiet);
                            if ((fd != null && fd.type != null))
                            {
                                if (fd.errors)
                                    return new ErrorExp();
                                assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                                Expression e = new CallExp(loc, e1, e2);
                                return expressionSemantic(e, sc);
                            }
                        }
                        finally {
                        }
                    }
                    {
                        FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, null, FuncResolveFlag.quiet);
                        if ((fd != null && fd.type != null))
                        {
                            if (fd.errors)
                                return new ErrorExp();
                            assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                            TypeFunction tf = (TypeFunction)fd.type;
                            if ((!(e2 != null) || tf.isref))
                            {
                                Expression e = new CallExp(loc, e1);
                                if (e2 != null)
                                    e = new AssignExp(loc, e, e2);
                                return expressionSemantic(e, sc);
                            }
                        }
                    }
                    {
                        FuncDeclaration fd = s.isFuncDeclaration();
                        if (fd != null)
                        {
                            assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                            Expression e = new CallExp(loc, e1, e2);
                            return expressionSemantic(e, sc);
                        }
                    }
                    if (e2 != null)
                        /*goto Leprop*/throw Dispatch0.INSTANCE;
                }
                if ((e1.op & 0xFF) == 26)
                {
                    VarExp ve = (VarExp)e1;
                    VarDeclaration v = ve.var.isVarDeclaration();
                    if ((v != null && ve.checkPurity(sc, v)))
                        return new ErrorExp();
                }
                if (e2 != null)
                    return null;
                if ((e1.type != null && (e1.op & 0xFF) != 20))
                {
                    if ((e1.op & 0xFF) == 26)
                    {
                        VarExp ve = (VarExp)e1;
                        if ((ve.var.storage_class & 8192L) != 0)
                        {
                            Expression e = new CallExp(loc, e1);
                            return expressionSemantic(e, sc);
                        }
                    }
                    else if ((e1.op & 0xFF) == 27)
                    {
                        if (checkUnsafeAccess(sc, e1, true, true))
                            return new ErrorExp();
                    }
                    else if ((e1.op & 0xFF) == 97)
                    {
                        e1.error(new BytePtr("expression has no value"));
                        return new ErrorExp();
                    }
                    else if ((e1.op & 0xFF) == 18)
                    {
                        CallExp ce = (CallExp)e1;
                        if (checkUnsafeAccess(sc, ce.e1, true, true))
                            return new ErrorExp();
                    }
                }
                if (!(e1.type != null))
                {
                    error(loc, new BytePtr("cannot resolve type for %s"), e1.toChars());
                    e1 = new ErrorExp();
                }
                return e1;
            }
            catch(Dispatch0 __d){}
        /*Leprop:*/
            error(loc, new BytePtr("not a property %s"), e1.toChars());
            return new ErrorExp();
        }
        catch(Dispatch1 __d){}
    /*Leproplvalue:*/
        error(loc, new BytePtr("%s is not an lvalue"), e1.toChars());
        return new ErrorExp();
    }

    public static Expression resolveProperties(Scope sc, Expression e) {
        e = resolvePropertiesX(sc, e, null);
        if (e.checkRightThis(sc))
            return new ErrorExp();
        return e;
    }

    public static boolean arrayExpressionToCommonType(Scope sc, DArray<Expression> exps, Ptr<Type> pt) {
        IntegerExp integerexp = literal0();
        CondExp condexp = new CondExp(Loc.initial, integerexp, null, null);
        Type t0 = null;
        Expression e0 = null;
        int j0 = -1;
        boolean foundType = false;
        {
            int i = 0;
            for (; i < (exps).length;i++){
                Expression e = (exps).get(i);
                if (!(e != null))
                    continue;
                e = resolveProperties(sc, e);
                if (!(e.type != null))
                {
                    e.error(new BytePtr("`%s` has no value"), e.toChars());
                    t0 = Type.terror;
                    continue;
                }
                if ((e.op & 0xFF) == 20)
                {
                    foundType = true;
                    e.checkValue();
                    t0 = Type.terror;
                    continue;
                }
                if ((e.type.ty & 0xFF) == ENUMTY.Tvoid)
                {
                    continue;
                }
                if (checkNonAssignmentArrayOp(e, false))
                {
                    t0 = Type.terror;
                    continue;
                }
                e = doCopyOrMove(sc, e, null);
                if (((!(foundType) && t0 != null) && !(t0.equals(e.type))))
                {
                    condexp.type = null;
                    condexp.e1 = e0;
                    condexp.e2 = e;
                    condexp.loc = e.loc.copy();
                    Expression ex = expressionSemantic(condexp, sc);
                    if ((ex.op & 0xFF) == 127)
                        e = ex;
                    else
                    {
                        exps.set(j0, condexp.e1);
                        e = condexp.e2;
                    }
                }
                j0 = i;
                e0 = e;
                t0 = e.type;
                if ((e.op & 0xFF) != 127)
                    exps.set(i, e);
            }
        }
        if (!(t0 != null))
            t0 = Type.tvoid;
        else if ((t0.ty & 0xFF) != ENUMTY.Terror)
        {
            {
                int i = 0;
                for (; i < (exps).length;i++){
                    Expression e = (exps).get(i);
                    if (!(e != null))
                        continue;
                    e = e.implicitCastTo(sc, t0);
                    if ((e.op & 0xFF) == 127)
                    {
                        t0 = Type.terror;
                        break;
                    }
                    exps.set(i, e);
                }
            }
        }
        if (pt != null)
            pt.set(0, t0);
        return pequals(t0, Type.terror);
    }

    public static Expression opAssignToOp(Loc loc, byte op, Expression e1, Expression e2) {
        Expression e = null;
        switch ((op & 0xFF))
        {
            case 76:
                e = new AddExp(loc, e1, e2);
                break;
            case 77:
                e = new MinExp(loc, e1, e2);
                break;
            case 81:
                e = new MulExp(loc, e1, e2);
                break;
            case 82:
                e = new DivExp(loc, e1, e2);
                break;
            case 83:
                e = new ModExp(loc, e1, e2);
                break;
            case 87:
                e = new AndExp(loc, e1, e2);
                break;
            case 88:
                e = new OrExp(loc, e1, e2);
                break;
            case 89:
                e = new XorExp(loc, e1, e2);
                break;
            case 66:
                e = new ShlExp(loc, e1, e2);
                break;
            case 67:
                e = new ShrExp(loc, e1, e2);
                break;
            case 69:
                e = new UshrExp(loc, e1, e2);
                break;
            default:
            throw new AssertionError("Unreachable code!");
        }
        return e;
    }

    public static Expression rewriteOpAssign(BinExp exp) {
        Expression e = null;
        assert((exp.e1.op & 0xFF) == 32);
        ArrayLengthExp ale = (ArrayLengthExp)exp.e1;
        if ((ale.e1.op & 0xFF) == 26)
        {
            e = opAssignToOp(exp.loc, exp.op, ale, exp.e2);
            e = new AssignExp(exp.loc, ale.syntaxCopy(), e);
        }
        else
        {
            VarDeclaration tmp = copyToTemp(0L, new BytePtr("__arraylength"), new AddrExp(ale.loc, ale.e1));
            Expression e1 = new ArrayLengthExp(ale.loc, new PtrExp(ale.loc, new VarExp(ale.loc, tmp, true)));
            Expression elvalue = e1.syntaxCopy();
            e = opAssignToOp(exp.loc, exp.op, e1, exp.e2);
            e = new AssignExp(exp.loc, elvalue, e);
            e = new CommaExp(exp.loc, new DeclarationExp(ale.loc, tmp), e, true);
        }
        return e;
    }

    public static boolean preFunctionParameters(Scope sc, DArray<Expression> exps) {
        boolean err = false;
        if (exps != null)
        {
            expandTuples(exps);
            {
                int i = 0;
                for (; i < (exps).length;i++){
                    Expression arg = (exps).get(i);
                    arg = resolveProperties(sc, arg);
                    if ((arg.op & 0xFF) == 20)
                    {
                        arg = resolveAliasThis(sc, arg, false);
                        if ((arg.op & 0xFF) == 20)
                        {
                            arg.error(new BytePtr("cannot pass type `%s` as a function argument"), arg.toChars());
                            arg = new ErrorExp();
                            err = true;
                        }
                    }
                    else if ((arg.type.toBasetype().ty & 0xFF) == ENUMTY.Tfunction)
                    {
                        arg.error(new BytePtr("cannot pass function `%s` as a function argument"), arg.toChars());
                        arg = new ErrorExp();
                        err = true;
                    }
                    else if (checkNonAssignmentArrayOp(arg, false))
                    {
                        arg = new ErrorExp();
                        err = true;
                    }
                    exps.set(i, arg);
                }
            }
        }
        return err;
    }

    public static boolean checkDefCtor(Loc loc, Type t) {
        t = t.baseElemOf();
        if ((t.ty & 0xFF) == ENUMTY.Tstruct)
        {
            StructDeclaration sd = ((TypeStruct)t).sym;
            if (sd.noDefaultCtor)
            {
                sd.error(loc, new BytePtr("default construction is disabled"));
                return true;
            }
        }
        return false;
    }

    public static boolean functionParameters(Loc loc, Scope sc, TypeFunction tf, Expression ethis, Type tthis, DArray<Expression> arguments, FuncDeclaration fd, Ptr<Type> prettype, Ptr<Expression> peprefix) {
        assert(arguments != null);
        assert((fd != null || tf.next != null));
        IntRef nargs = ref(arguments != null ? (arguments).length : 0);
        IntRef nparams = ref(tf.parameterList.length());
        int olderrors = global.errors;
        boolean err = false;
        prettype.set(0, Type.terror);
        Expression eprefix = null;
        peprefix.set(0, null);
        if ((nargs.value > nparams.value && tf.parameterList.varargs == VarArg.none))
        {
            error(loc, new BytePtr("expected %llu arguments, not %llu for non-variadic function type `%s`"), (long)nparams.value, (long)nargs.value, tf.toChars());
            return true;
        }
        if ((!(tf.next != null) && fd.inferRetType))
        {
            fd.functionSemantic();
        }
        else if ((fd != null && fd.parent != null))
        {
            TemplateInstance ti = fd.parent.isTemplateInstance();
            if ((ti != null && ti.tempdecl != null))
            {
                fd.functionSemantic3();
            }
        }
        boolean isCtorCall = ((fd != null && fd.needThis()) && fd.isCtorDeclaration() != null);
        int n = nargs.value > nparams.value ? nargs.value : nparams.value;
        byte wildmatch = (tthis != null && !(isCtorCall)) ? (byte)(tthis.deduceWild(tf, false) & 0xFF) : (byte)0;
        boolean done = false;
        {
            int __key1366 = 0;
            int __limit1367 = n;
        L_outer4:
            for (; __key1366 < __limit1367;__key1366 += 1) {
                int i = __key1366;
                Expression arg = i < nargs.value ? (arguments).get(i) : null;
                if (i < nparams.value)
                {
                    Function0<Boolean> errorArgs = new Function0<Boolean>(){
                        public Boolean invoke(){
                            error(loc, new BytePtr("expected %llu function arguments, not %llu"), (long)nparams.value, (long)nargs.value);
                            return true;
                        }
                    };
                    Parameter p = tf.parameterList.get(i);
                    if (!(arg != null))
                    {
                        if (!(p.defaultArg != null))
                        {
                            if ((tf.parameterList.varargs == VarArg.typesafe && i + 1 == nparams.value))
                                /*goto L2*//*unrolled goto*/
                            /*L2:*/
                                Type tb = p.type.toBasetype();
                                switch ((tb.ty & 0xFF))
                                {
                                    case 1:
                                    case 0:
                                        Type tbn = ((TypeArray)tb).next;
                                        Type tret = p.isLazyArray();
                                        DArray<Expression> elements = new DArray<Expression>(nargs.value - i);
                                        {
                                            int __key1368 = 0;
                                            int __limit1369 = (elements).length;
                                            for (; __key1368 < __limit1369;__key1368 += 1) {
                                                int u = __key1368;
                                                Expression a = (arguments).get(i + u);
                                                if ((tret != null && (a.implicitConvTo(tret)) != 0))
                                                {
                                                    a = toDelegate(a.implicitCastTo(sc, tret).optimize(0, false), tret, sc);
                                                }
                                                else
                                                    a = a.implicitCastTo(sc, tbn);
                                                a = a.addDtorHook(sc);
                                                elements.set(u, a);
                                            }
                                        }
                                        arg = new ArrayLiteralExp(loc, tbn.sarrayOf((long)(nargs.value - i)), elements);
                                        if ((tb.ty & 0xFF) == ENUMTY.Tarray)
                                        {
                                            arg = new SliceExp(loc, arg, null, null);
                                            arg.type = p.type;
                                        }
                                        break;
                                    case 7:
                                        DArray<Expression> args = new DArray<Expression>(nargs.value - i);
                                        {
                                            int __key1370 = i;
                                            int __limit1371 = nargs.value;
                                            for (; __key1370 < __limit1371;__key1370 += 1) {
                                                int u_1 = __key1370;
                                                args.set(u_1 - i, (arguments).get(u_1));
                                            }
                                        }
                                        arg = new NewExp(loc, null, null, p.type, args);
                                        break;
                                    default:
                                    if (!(arg != null))
                                    {
                                        error(loc, new BytePtr("not enough arguments"));
                                        return true;
                                    }
                                    break;
                                }
                                arg = expressionSemantic(arg, sc);
                                (arguments).setDim(i + 1);
                                arguments.set(i, arg);
                                nargs.value = i + 1;
                                done = true;
                            return errorArgs.invoke();
                        }
                        arg = p.defaultArg;
                        arg = inlineCopy(arg, sc);
                        arg = arg.resolveLoc(loc, sc);
                        (arguments).push(arg);
                        nargs.value++;
                    }
                    else
                    {
                        if ((arg.op & 0xFF) == 190)
                        {
                            arg = arg.resolveLoc(loc, sc);
                            arguments.set(i, arg);
                        }
                    }
                    try {
                        if ((tf.parameterList.varargs == VarArg.typesafe && i + 1 == nparams.value))
                        {
                            try {
                                {
                                    int m = MATCH.nomatch;
                                    if ((m = arg.implicitConvTo(p.type)) > MATCH.nomatch)
                                    {
                                        if ((p.type.nextOf() != null && arg.implicitConvTo(p.type.nextOf()) >= m))
                                            /*goto L2*/throw Dispatch0.INSTANCE;
                                        else if (nargs.value != nparams.value)
                                            return errorArgs.invoke();
                                        /*goto L1*/throw Dispatch0.INSTANCE;
                                    }
                                }
                            }
                            catch(Dispatch0 __d){}
                        /*L2:*/
                            Type tb = p.type.toBasetype();
                            switch ((tb.ty & 0xFF))
                            {
                                case 1:
                                case 0:
                                    Type tbn = ((TypeArray)tb).next;
                                    Type tret = p.isLazyArray();
                                    DArray<Expression> elements = new DArray<Expression>(nargs.value - i);
                                    {
                                        int __key1368 = 0;
                                        int __limit1369 = (elements).length;
                                        for (; __key1368 < __limit1369;__key1368 += 1) {
                                            int u = __key1368;
                                            Expression a = (arguments).get(i + u);
                                            if ((tret != null && (a.implicitConvTo(tret)) != 0))
                                            {
                                                a = toDelegate(a.implicitCastTo(sc, tret).optimize(0, false), tret, sc);
                                            }
                                            else
                                                a = a.implicitCastTo(sc, tbn);
                                            a = a.addDtorHook(sc);
                                            elements.set(u, a);
                                        }
                                    }
                                    arg = new ArrayLiteralExp(loc, tbn.sarrayOf((long)(nargs.value - i)), elements);
                                    if ((tb.ty & 0xFF) == ENUMTY.Tarray)
                                    {
                                        arg = new SliceExp(loc, arg, null, null);
                                        arg.type = p.type;
                                    }
                                    break;
                                case 7:
                                    DArray<Expression> args = new DArray<Expression>(nargs.value - i);
                                    {
                                        int __key1370 = i;
                                        int __limit1371 = nargs.value;
                                        for (; __key1370 < __limit1371;__key1370 += 1) {
                                            int u_1 = __key1370;
                                            args.set(u_1 - i, (arguments).get(u_1));
                                        }
                                    }
                                    arg = new NewExp(loc, null, null, p.type, args);
                                    break;
                                default:
                                if (!(arg != null))
                                {
                                    error(loc, new BytePtr("not enough arguments"));
                                    return true;
                                }
                                break;
                            }
                            arg = expressionSemantic(arg, sc);
                            (arguments).setDim(i + 1);
                            arguments.set(i, arg);
                            nargs.value = i + 1;
                            done = true;
                        }
                    }
                    catch(Dispatch0 __d){}
                /*L1:*/
                    if (!(((p.storageClass & 8192L) != 0 && (p.type.ty & 0xFF) == ENUMTY.Tvoid)))
                    {
                        boolean isRef = (p.storageClass & 2101248L) != 0L;
                        {
                            byte wm = arg.type.deduceWild(p.type, isRef);
                            if ((wm) != 0)
                            {
                                wildmatch = (wildmatch) != 0 ? MODmerge(wildmatch, wm) : wm;
                            }
                        }
                    }
                }
                if (done)
                    break;
            }
        }
        if ((((((wildmatch & 0xFF) == MODFlags.mutable || (wildmatch & 0xFF) == MODFlags.immutable_) && tf.next != null) && (tf.next.hasWild()) != 0) && (tf.isref || !((tf.next.implicitConvTo(tf.next.immutableOf())) != 0))))
        {
            Function1<Byte,Boolean> errorInout = new Function1<Byte,Boolean>(){
                public Boolean invoke(Byte wildmatch){
                    BytePtr s = pcopy((wildmatch & 0xFF) == MODFlags.mutable ? new BytePtr("mutable") : MODtoChars(wildmatch));
                    error(loc, new BytePtr("modify `inout` to `%s` is not allowed inside `inout` function"), s);
                    return true;
                }
            };
            if (fd != null)
            {
                Function1<Dsymbol,Boolean> checkEnclosingWild = new Function1<Dsymbol,Boolean>(){
                    public Boolean invoke(Dsymbol s){
                        Function1<Dsymbol,Boolean> checkWild = new Function1<Dsymbol,Boolean>(){
                            public Boolean invoke(Dsymbol s){
                                if (!(s != null))
                                    return false;
                                {
                                    AggregateDeclaration ad = s.isAggregateDeclaration();
                                    if (ad != null)
                                    {
                                        if (ad.isNested())
                                            return checkEnclosingWild.invoke(s);
                                    }
                                    else {
                                        FuncDeclaration ff = s.isFuncDeclaration();
                                        if (ff != null)
                                        {
                                            if ((((TypeFunction)ff.type).iswild) != 0)
                                                return errorInout.invoke(wildmatch);
                                            if ((ff.isNested() || ff.isThis() != null))
                                                return checkEnclosingWild.invoke(s);
                                        }
                                    }
                                }
                                return false;
                            }
                        };
                        Dsymbol ctx0 = s.toParent2();
                        Dsymbol ctx1 = s.toParentLocal();
                        if (checkWild.invoke(ctx0))
                            return true;
                        if (!pequals(ctx0, ctx1))
                            return checkWild.invoke(ctx1);
                        return false;
                    }
                };
                if (((fd.isThis() != null || fd.isNested()) && checkEnclosingWild.invoke(fd)))
                    return true;
            }
            else if (tf.isWild())
                return errorInout.invoke(wildmatch);
        }
        Expression firstArg = ((((((tf.next != null && (tf.next.ty & 0xFF) == ENUMTY.Tvoid) || isCtorCall) && tthis != null) && tthis.isMutable()) && (tthis.toBasetype().ty & 0xFF) == ENUMTY.Tstruct) && tthis.hasPointers()) ? ethis : null;
        assert(nargs.value >= nparams.value);
        {
            Slice<Expression> __r1373 = (arguments).opSlice(0, nargs.value).copy();
            int __key1372 = 0;
            for (; __key1372 < __r1373.getLength();__key1372 += 1) {
                Expression arg = __r1373.get(__key1372);
                int i = __key1372;
                assert(arg != null);
                if (i < nparams.value)
                {
                    Parameter p = tf.parameterList.get(i);
                    Type targ = arg.type;
                    if (!(((p.storageClass & 8192L) != 0 && (p.type.ty & 0xFF) == ENUMTY.Tvoid)))
                    {
                        Type tprm = (p.type.hasWild()) != 0 ? p.type.substWildTo((wildmatch & 0xFF)) : p.type;
                        boolean hasCopyCtor = ((arg.type.ty & 0xFF) == ENUMTY.Tstruct && ((TypeStruct)arg.type).sym.hasCopyCtor);
                        if (!((hasCopyCtor || tprm.equals(arg.type))))
                        {
                            arg = arg.implicitCastTo(sc, tprm);
                            arg = arg.optimize(0, (p.storageClass & 2101248L) != 0L);
                        }
                    }
                    if ((p.storageClass & 2097152L) != 0)
                    {
                        if (((global.params.rvalueRefParam && !(arg.isLvalue())) && isCopyable(targ)))
                        {
                            VarDeclaration v = copyToTemp(0L, new BytePtr("__rvalue"), arg);
                            Expression ev = new DeclarationExp(arg.loc, v);
                            ev = new CommaExp(arg.loc, ev, new VarExp(arg.loc, v, true), true);
                            arg = expressionSemantic(ev, sc);
                        }
                        arg = arg.toLvalue(sc, arg);
                        (err ? 1 : 0) |= (checkUnsafeAccess(sc, arg, false, true) ? 1 : 0);
                    }
                    else if ((p.storageClass & 4096L) != 0)
                    {
                        Type t = arg.type;
                        if ((!(t.isMutable()) || !(t.isAssignable())))
                        {
                            arg.error(new BytePtr("cannot modify struct `%s` with immutable members"), arg.toChars());
                            err = true;
                        }
                        else
                        {
                            (err ? 1 : 0) |= (checkUnsafeAccess(sc, arg, false, true) ? 1 : 0);
                            (err ? 1 : 0) |= (checkDefCtor(arg.loc, t) ? 1 : 0);
                        }
                        arg = arg.toLvalue(sc, arg);
                    }
                    else if ((p.storageClass & 8192L) != 0)
                    {
                        Type t = (p.type.ty & 0xFF) == ENUMTY.Tvoid ? p.type : arg.type;
                        arg = toDelegate(arg, t, sc);
                    }
                    if ((firstArg != null && (p.storageClass & 17592186044416L) != 0))
                    {
                        if (global.params.vsafe)
                            (err ? 1 : 0) |= (checkParamArgumentReturn(sc, firstArg, arg, false) ? 1 : 0);
                    }
                    else if (tf.parameterEscapes(tthis, p))
                    {
                        if (global.params.vsafe)
                            (err ? 1 : 0) |= (checkParamArgumentEscape(sc, fd, p, arg, false) ? 1 : 0);
                    }
                    else
                    {
                        Expression a = arg;
                        if ((a.op & 0xFF) == 12)
                            a = ((CastExp)a).e1;
                        if ((a.op & 0xFF) == 161)
                        {
                            FuncExp fe = (FuncExp)a;
                            fe.fd.tookAddressOf = 0;
                        }
                        else if ((a.op & 0xFF) == 160)
                        {
                            DelegateExp de = (DelegateExp)a;
                            if ((de.e1.op & 0xFF) == 26)
                            {
                                VarExp ve = (VarExp)de.e1;
                                FuncDeclaration f = ve.var.isFuncDeclaration();
                                if (f != null)
                                {
                                    f.tookAddressOf--;
                                }
                            }
                        }
                    }
                    arg = arg.optimize(0, (p.storageClass & 2101248L) != 0L);
                    if (((((i == 0 && !(tthis != null)) && (p.storageClass & 2101248L) != 0) && p.type != null) && ((tf.next != null && (tf.next.ty & 0xFF) == ENUMTY.Tvoid) || isCtorCall)))
                    {
                        Type tb = p.type.baseElemOf();
                        if ((tb.isMutable() && tb.hasPointers()))
                        {
                            firstArg = arg;
                        }
                    }
                }
                else
                {
                    if (tf.linkage != LINK.d)
                    {
                        arg = integralPromotions(arg, sc);
                        switch ((arg.type.ty & 0xFF))
                        {
                            case 21:
                                arg = arg.castTo(sc, Type.tfloat64);
                                break;
                            case 24:
                                arg = arg.castTo(sc, Type.timaginary64);
                                break;
                            default:
                            break;
                        }
                        if (tf.parameterList.varargs == VarArg.variadic)
                        {
                            BytePtr p = pcopy(tf.linkage == LINK.c ? new BytePtr("extern(C)") : new BytePtr("extern(C++)"));
                            if ((arg.type.ty & 0xFF) == ENUMTY.Tarray)
                            {
                                arg.error(new BytePtr("cannot pass dynamic arrays to `%s` vararg functions"), p);
                                err = true;
                            }
                            if ((arg.type.ty & 0xFF) == ENUMTY.Tsarray)
                            {
                                arg.error(new BytePtr("cannot pass static arrays to `%s` vararg functions"), p);
                                err = true;
                            }
                        }
                    }
                    if (arg.type.needsDestruction())
                    {
                        arg.error(new BytePtr("cannot pass types that need destruction as variadic arguments"));
                        err = true;
                    }
                    Type tb = arg.type.toBasetype();
                    if ((tb.ty & 0xFF) == ENUMTY.Tsarray)
                    {
                        TypeSArray ts = (TypeSArray)tb;
                        Type ta = ts.next.arrayOf();
                        if (ts.size(arg.loc) == 0L)
                            arg = new NullExp(arg.loc, ta);
                        else
                            arg = arg.castTo(sc, ta);
                    }
                    if ((tb.ty & 0xFF) == ENUMTY.Tstruct)
                    {
                    }
                    if ((arg.op & 0xFF) == 25)
                    {
                        SymOffExp se = (SymOffExp)arg;
                        if ((se.hasOverloads && !(se.var.isFuncDeclaration().isUnique())))
                        {
                            arg.error(new BytePtr("function `%s` is overloaded"), arg.toChars());
                            err = true;
                        }
                    }
                    if (arg.checkValue())
                        err = true;
                    arg = arg.optimize(0, false);
                }
                arguments.set(i, arg);
            }
        }
        {
            boolean leftToRight = true;
            if (false)
                assert(nargs.value == nparams.value);
            int start = 0;
            int end = nargs.value;
            int step = 1;
            int lastthrow = -1;
            int firstdtor = -1;
            {
                int i = 0;
                for (; i != end;i += 1){
                    Expression arg = (arguments).get(i);
                    if (canThrow(arg, (sc).func, false))
                        lastthrow = i;
                    if ((firstdtor == -1 && arg.type.needsDestruction()))
                    {
                        Parameter p = i >= nparams.value ? null : tf.parameterList.get(i);
                        if (!((p != null && (p.storageClass & 2109440L) != 0)))
                            firstdtor = i;
                    }
                }
            }
            boolean needsPrefix = ((firstdtor >= 0 && lastthrow >= 0) && (lastthrow - firstdtor) > 0);
            VarDeclaration gate = null;
            if (needsPrefix)
            {
                Identifier idtmp = Identifier.generateId(new BytePtr("__gate"));
                gate = new VarDeclaration(loc, Type.tbool, idtmp, null, 0L);
                gate.storage_class |= 9964324126720L;
                dsymbolSemantic(gate, sc);
                DeclarationExp ae = new DeclarationExp(loc, gate);
                eprefix = expressionSemantic(ae, sc);
            }
            {
                int i = 0;
                for (; i != end;i += 1){
                    Expression arg = (arguments).get(i);
                    Parameter parameter = i >= nparams.value ? null : tf.parameterList.get(i);
                    boolean isRef = (parameter != null && (parameter.storageClass & 2101248L) != 0);
                    boolean isLazy = (parameter != null && (parameter.storageClass & 8192L) != 0);
                    if (isLazy)
                        continue;
                    if (gate != null)
                    {
                        boolean needsDtor = ((!(isRef) && arg.type.needsDestruction()) && i != lastthrow);
                        VarDeclaration tmp = copyToTemp(0L, needsDtor ? new BytePtr("__pfx") : new BytePtr("__pfy"), !(isRef) ? arg : arg.addressOf());
                        dsymbolSemantic(tmp, sc);
                        if (!(needsDtor))
                        {
                            if (tmp.edtor != null)
                            {
                                assert(i == lastthrow);
                                tmp.edtor = null;
                            }
                        }
                        else
                        {
                            assert(tmp.edtor != null);
                            Expression e = tmp.edtor;
                            e = new LogicalExp(e.loc, TOK.orOr, new VarExp(e.loc, gate, true), e);
                            tmp.edtor = expressionSemantic(e, sc);
                        }
                        DeclarationExp ae = new DeclarationExp(loc, tmp);
                        eprefix = Expression.combine(eprefix, expressionSemantic(ae, sc));
                        arg = new VarExp(loc, tmp, true);
                        arg = expressionSemantic(arg, sc);
                        if (isRef)
                        {
                            arg = new PtrExp(loc, arg);
                            arg = expressionSemantic(arg, sc);
                        }
                        if (i == lastthrow)
                        {
                            AssignExp e = new AssignExp(gate.loc, new VarExp(gate.loc, gate, true), new IntegerExp(gate.loc, 1L, Type.tbool));
                            eprefix = Expression.combine(eprefix, expressionSemantic(e, sc));
                            gate = null;
                        }
                    }
                    else
                    {
                        Type tv = arg.type.baseElemOf();
                        if ((!(isRef) && (tv.ty & 0xFF) == ENUMTY.Tstruct))
                            arg = doCopyOrMove(sc, arg, parameter != null ? parameter.type : null);
                    }
                    arguments.set(i, arg);
                }
            }
        }
        if ((tf.linkage == LINK.d && tf.parameterList.varargs == VarArg.variadic))
        {
            assert((arguments).length >= nparams.value);
            DArray<Parameter> args = new DArray<Parameter>((arguments).length - nparams.value);
            {
                int i = 0;
                for (; i < (arguments).length - nparams.value;i++){
                    Parameter arg = new Parameter(2048L, (arguments).get(nparams.value + i).type, null, null, null);
                    args.set(i, arg);
                }
            }
            TypeTuple tup = new TypeTuple(args);
            Expression e = expressionSemantic(new TypeidExp(loc, tup), sc);
            (arguments).insert(0, e);
        }
        Type tret = tf.next;
        if (isCtorCall)
        {
            if (!(tthis != null))
            {
                assert((((sc).intypeof) != 0 || (global.errors) != 0));
                tthis = fd.isThis().type.addMod(fd.type.mod);
            }
            if ((tf.isWild() && !(fd.isReturnIsolated())))
            {
                if ((wildmatch) != 0)
                    tret = tret.substWildTo((wildmatch & 0xFF));
                IntRef offset = ref(0);
                if ((!((tret.implicitConvTo(tthis)) != 0) && !(((MODimplicitConv(tret.mod, tthis.mod) && tret.isBaseOf(tthis, ptr(offset))) && offset.value == 0))))
                {
                    BytePtr s1 = pcopy(tret.isNaked() ? new BytePtr(" mutable") : tret.modToChars());
                    BytePtr s2 = pcopy(tthis.isNaked() ? new BytePtr(" mutable") : tthis.modToChars());
                    error(loc, new BytePtr("`inout` constructor `%s` creates%s object, not%s"), fd.toPrettyChars(false), s1, s2);
                    err = true;
                }
            }
            tret = tthis;
        }
        else if (((wildmatch) != 0 && tret != null))
        {
            tret = tret.substWildTo((wildmatch & 0xFF));
        }
        prettype.set(0, tret);
        peprefix.set(0, eprefix);
        return (err || olderrors != global.errors);
    }

    public static dmodule.Package resolveIsPackage(Dsymbol sym) {
        dmodule.Package pkg = null;
        {
            Import imp = sym.isImport();
            if (imp != null)
            {
                if (imp.pkg == null)
                {
                    error(sym.loc, new BytePtr("Internal Compiler Error: unable to process forward-referenced import `%s`"), imp.toChars());
                    throw new AssertionError("Unreachable code!");
                }
                pkg = imp.pkg;
            }
            else
                pkg = sym.isPackage();
        }
        if (pkg != null)
            pkg.resolvePKGunknown();
        return pkg;
    }

    public static dmodule.Module loadStdMath() {
        if (!(expressionsem.loadStdMathimpStdMath != null))
        {
            DArray<Identifier> a = new DArray<Identifier>();
            (a).push(Id.std);
            Import s = new Import(Loc.initial, a, Id.math, null, 0);
            int errors = global.startGagging();
            s.load(null);
            if (s.mod != null)
            {
                s.mod.importAll(null);
                dsymbolSemantic(s.mod, null);
            }
            global.endGagging(errors);
            expressionsem.loadStdMathimpStdMath = s;
        }
        return expressionsem.loadStdMathimpStdMath.mod;
    }

    public static class ExpressionSemanticVisitor extends Visitor
    {
        public Scope sc;
        public Expression result;
        public  ExpressionSemanticVisitor(Scope sc) {
            this.sc = sc;
        }

        public  void setError() {
            this.result = new ErrorExp();
        }

        public  void visit(Expression e) {
            if (e.type != null)
                e.type = typeSemantic(e.type, e.loc, this.sc);
            else
                e.type = Type.tvoid;
            this.result = e;
        }

        public  void visit(IntegerExp e) {
            assert(e.type != null);
            if ((e.type.ty & 0xFF) == ENUMTY.Terror)
                this.setError();
                return ;
            assert(e.type.deco != null);
            e.setInteger(e.getInteger());
            this.result = e;
        }

        public  void visit(RealExp e) {
            if (!(e.type != null))
                e.type = Type.tfloat64;
            else
                e.type = typeSemantic(e.type, e.loc, this.sc);
            this.result = e;
        }

        public  void visit(ComplexExp e) {
            if (!(e.type != null))
                e.type = Type.tcomplex80;
            else
                e.type = typeSemantic(e.type, e.loc, this.sc);
            this.result = e;
        }

        public  void visit(IdentifierExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            Ref<Dsymbol> scopesym = ref(null);
            Dsymbol s = (this.sc).search(exp.loc, exp.ident, ptr(scopesym), 0);
            if (s != null)
            {
                if (s.errors)
                    this.setError();
                    return ;
                Expression e = null;
                WithScopeSymbol withsym = scopesym.value.isWithScopeSymbol();
                if ((withsym != null && withsym.withstate.wthis != null))
                {
                    Scope scwith = this.sc;
                    for (; !pequals((scwith).scopesym, scopesym.value);){
                        scwith = (scwith).enclosing;
                        assert(scwith != null);
                    }
                    {
                        Scope scx = scwith;
                        for (; (scx != null && pequals((scx).func, (scwith).func));scx = (scx).enclosing){
                            Dsymbol s2 = null;
                            if (((((scx).scopesym != null && (scx).scopesym.symtab != null) && (s2 = (scx).scopesym.symtab.lookup(s.ident)) != null) && !pequals(s, s2)))
                            {
                                exp.error(new BytePtr("with symbol `%s` is shadowing local symbol `%s`"), s.toPrettyChars(false), s2.toPrettyChars(false));
                                this.setError();
                                return ;
                            }
                        }
                    }
                    s = s.toAlias();
                    e = new VarExp(exp.loc, withsym.withstate.wthis, true);
                    e = new DotIdExp(exp.loc, e, exp.ident);
                    e = expressionSemantic(e, this.sc);
                }
                else
                {
                    if (withsym != null)
                    {
                        {
                            TypeExp t = withsym.withstate.exp.isTypeExp();
                            if (t != null)
                            {
                                e = new TypeExp(exp.loc, t.type);
                                e = new DotIdExp(exp.loc, e, exp.ident);
                                this.result = expressionSemantic(e, this.sc);
                                return ;
                            }
                        }
                    }
                    FuncDeclaration f = s.isFuncDeclaration();
                    if (f != null)
                    {
                        TemplateDeclaration td = getFuncTemplateDecl(f);
                        if (td != null)
                        {
                            if (td.overroot != null)
                                td = td.overroot;
                            e = new TemplateExp(exp.loc, td, f);
                            e = expressionSemantic(e, this.sc);
                            this.result = e;
                            return ;
                        }
                    }
                    if (global.params.fixAliasThis)
                    {
                        ExpressionDsymbol expDsym = scopesym.value.isExpressionDsymbol();
                        if (expDsym != null)
                        {
                            this.result = expressionSemantic(expDsym.exp, this.sc);
                            return ;
                        }
                    }
                    e = symbolToExp(s, exp.loc, this.sc, true);
                }
                this.result = e;
                return ;
            }
            if ((!(global.params.fixAliasThis) && hasThis(this.sc) != null))
            {
                {
                    AggregateDeclaration ad = (this.sc).getStructClassScope();
                    for (; ad != null;){
                        if (ad.aliasthis != null)
                        {
                            Expression e = null;
                            e = new ThisExp(exp.loc);
                            e = new DotIdExp(exp.loc, e, ad.aliasthis.ident);
                            e = new DotIdExp(exp.loc, e, exp.ident);
                            e = trySemantic(e, this.sc);
                            if (e != null)
                            {
                                this.result = e;
                                return ;
                            }
                        }
                        ClassDeclaration cd = ad.isClassDeclaration();
                        if (((cd != null && cd.baseClass != null) && !pequals(cd.baseClass, ClassDeclaration.object)))
                        {
                            ad = cd.baseClass;
                            continue;
                        }
                        break;
                    }
                }
            }
            if (pequals(exp.ident, Id.ctfe))
            {
                if (((this.sc).flags & 128) != 0)
                {
                    exp.error(new BytePtr("variable `__ctfe` cannot be read at compile time"));
                    this.setError();
                    return ;
                }
                VarDeclaration vd = new VarDeclaration(exp.loc, Type.tbool, Id.ctfe, null, 0L);
                vd.storage_class |= 1099511627776L;
                vd.semanticRun = PASS.semanticdone;
                Expression e = new VarExp(exp.loc, vd, true);
                e = expressionSemantic(e, this.sc);
                this.result = e;
                return ;
            }
            {
                Scope sc2 = this.sc;
                for (; sc2 != null;sc2 = (sc2).enclosing){
                    if (!((sc2).scopesym != null))
                        continue;
                    {
                        WithScopeSymbol ss = (sc2).scopesym.isWithScopeSymbol();
                        if (ss != null)
                        {
                            if (ss.withstate.wthis != null)
                            {
                                Expression e = null;
                                e = new VarExp(exp.loc, ss.withstate.wthis, true);
                                e = new DotIdExp(exp.loc, e, exp.ident);
                                e = trySemantic(e, this.sc);
                                if (e != null)
                                {
                                    this.result = e;
                                    return ;
                                }
                            }
                            else if ((ss.withstate.exp != null && (ss.withstate.exp.op & 0xFF) == 20))
                            {
                                {
                                    Type t = ss.withstate.exp.isTypeExp().type;
                                    if (t != null)
                                    {
                                        Expression e = null;
                                        e = new TypeExp(exp.loc, t);
                                        e = new DotIdExp(exp.loc, e, exp.ident);
                                        e = trySemantic(e, this.sc);
                                        if (e != null)
                                        {
                                            this.result = e;
                                            return ;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            {
                ByteSlice n = importHint(exp.ident.asString()).copy();
                if (n.getLength() != 0)
                    exp.error(new BytePtr("`%s` is not defined, perhaps `import %.*s;` is needed?"), exp.ident.toChars(), n.getLength(), toBytePtr(n));
                else {
                    Dsymbol s2 = (this.sc).search_correct(exp.ident);
                    if (s2 != null)
                        exp.error(new BytePtr("undefined identifier `%s`, did you mean %s `%s`?"), exp.ident.toChars(), s2.kind(), s2.toChars());
                    else {
                        BytePtr p = pcopy(Scope.search_correct_C(exp.ident));
                        if (p != null)
                            exp.error(new BytePtr("undefined identifier `%s`, did you mean `%s`?"), exp.ident.toChars(), p);
                        else
                            exp.error(new BytePtr("undefined identifier `%s`"), exp.ident.toChars());
                    }
                }
            }
            this.result = new ErrorExp();
        }

        public  void visit(DsymbolExp e) {
            this.result = symbolToExp(e.s, e.loc, this.sc, e.hasOverloads);
        }

        public  void visit(ThisExp e) {
            if (e.type != null)
            {
                this.result = e;
                return ;
            }
            FuncDeclaration fd = hasThis(this.sc);
            AggregateDeclaration ad = null;
            try {
                if ((!(fd != null) && (this.sc).intypeof == 1))
                {
                    {
                        Dsymbol s = (this.sc).getStructClassScope();
                    L_outer5:
                        for (; (1) != 0;s = s.parent){
                            if (!(s != null))
                            {
                                e.error(new BytePtr("`%s` is not in a class or struct scope"), e.toChars());
                                /*goto Lerr*/throw Dispatch0.INSTANCE;
                            }
                            ClassDeclaration cd = s.isClassDeclaration();
                            if (cd != null)
                            {
                                e.type = cd.type;
                                this.result = e;
                                return ;
                            }
                            StructDeclaration sd = s.isStructDeclaration();
                            if (sd != null)
                            {
                                e.type = sd.type;
                                this.result = e;
                                return ;
                            }
                        }
                    }
                }
                if (!(fd != null))
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                assert(fd.vthis != null);
                e.var = fd.vthis;
                assert(e.var.parent != null);
                ad = fd.isMemberLocal();
                if (!(ad != null))
                    ad = fd.isMember2();
                assert(ad != null);
                e.type = ad.type.addMod(e.var.type.mod);
                if (e.var.checkNestedReference(this.sc, e.loc))
                    this.setError();
                    return ;
                this.result = e;
                return ;
            }
            catch(Dispatch0 __d){}
        /*Lerr:*/
            e.error(new BytePtr("`this` is only defined in non-static member functions, not `%s`"), (this.sc).parent.toChars());
            this.result = new ErrorExp();
        }

        public  void visit(SuperExp e) {
            if (e.type != null)
            {
                this.result = e;
                return ;
            }
            FuncDeclaration fd = hasThis(this.sc);
            ClassDeclaration cd = null;
            Dsymbol s = null;
            try {
                if ((!(fd != null) && (this.sc).intypeof == 1))
                {
                    {
                        s = (this.sc).getStructClassScope();
                    L_outer6:
                        for (; (1) != 0;s = s.parent){
                            if (!(s != null))
                            {
                                e.error(new BytePtr("`%s` is not in a class scope"), e.toChars());
                                /*goto Lerr*/throw Dispatch0.INSTANCE;
                            }
                            cd = s.isClassDeclaration();
                            if (cd != null)
                            {
                                cd = cd.baseClass;
                                if (!(cd != null))
                                {
                                    e.error(new BytePtr("class `%s` has no `super`"), s.toChars());
                                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                                }
                                e.type = cd.type;
                                this.result = e;
                                return ;
                            }
                        }
                    }
                }
                if (!(fd != null))
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                e.var = fd.vthis;
                assert((e.var != null && e.var.parent != null));
                s = fd.toParentDecl();
                if (s.isTemplateDeclaration() != null)
                    s = s.toParent();
                assert(s != null);
                cd = s.isClassDeclaration();
                if (!(cd != null))
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                if (!(cd.baseClass != null))
                {
                    e.error(new BytePtr("no base class for `%s`"), cd.toChars());
                    e.type = cd.type.addMod(e.var.type.mod);
                }
                else
                {
                    e.type = cd.baseClass.type;
                    e.type = e.type.castMod(e.var.type.mod);
                }
                if (e.var.checkNestedReference(this.sc, e.loc))
                    this.setError();
                    return ;
                this.result = e;
                return ;
            }
            catch(Dispatch0 __d){}
        /*Lerr:*/
            e.error(new BytePtr("`super` is only allowed in non-static class member functions"));
            this.result = new ErrorExp();
        }

        public  void visit(NullExp e) {
            if (e.type != null)
            {
                this.result = e;
                return ;
            }
            e.type = Type.tnull;
            this.result = e;
        }

        public  void visit(StringExp e) {
            if (e.type != null)
            {
                this.result = e;
                return ;
            }
            OutBuffer buffer = new OutBuffer();
            try {
                int newlen = 0;
                BytePtr p = null;
                IntRef u = ref(0);
                IntRef c = ref(0x0ffff);
                {
                    int __dispatch4 = 0;
                    dispatched_4:
                    do {
                        switch (__dispatch4 != 0 ? __dispatch4 : (e.postfix & 0xFF))
                        {
                            case 100:
                                {
                                    u.value = 0;
                                    for (; u.value < e.len;){
                                        p = pcopy(utf_decodeChar(e.string, e.len, u, c));
                                        if (p != null)
                                        {
                                            e.error(new BytePtr("%s"), p);
                                            this.setError();
                                            return ;
                                        }
                                        else
                                        {
                                            buffer.write4(c.value);
                                            newlen++;
                                        }
                                    }
                                }
                                buffer.write4(0);
                                e.dstring = pcopy((toIntPtr(buffer.extractData())));
                                e.len = newlen;
                                e.sz = (byte)4;
                                e.type = new TypeDArray(Type.tdchar.immutableOf());
                                e.committed = (byte)1;
                                break;
                            case 119:
                                {
                                    u.value = 0;
                                    for (; u.value < e.len;){
                                        p = pcopy(utf_decodeChar(e.string, e.len, u, c));
                                        if (p != null)
                                        {
                                            e.error(new BytePtr("%s"), p);
                                            this.setError();
                                            return ;
                                        }
                                        else
                                        {
                                            buffer.writeUTF16(c.value);
                                            newlen++;
                                            if (c.value >= 65536)
                                                newlen++;
                                        }
                                    }
                                }
                                buffer.writeUTF16(0);
                                e.wstring = pcopy((toCharPtr(buffer.extractData())));
                                e.len = newlen;
                                e.sz = (byte)2;
                                e.type = new TypeDArray(Type.twchar.immutableOf());
                                e.committed = (byte)1;
                                break;
                            case 99:
                                e.committed = (byte)1;
                                /*goto default*/ { __dispatch4 = -1; continue dispatched_4; }
                            default:
                            __dispatch4 = 0;
                            e.type = new TypeDArray(Type.tchar.immutableOf());
                            break;
                        }
                    } while(__dispatch4 != 0);
                }
                e.type = typeSemantic(e.type, e.loc, this.sc);
                this.result = e;
            }
            finally {
            }
        }

        public  void visit(TupleExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            if (exp.e0 != null)
                exp.e0 = expressionSemantic(exp.e0, this.sc);
            boolean err = false;
            {
                int i = 0;
                for (; i < (exp.exps).length;i++){
                    Expression e = (exp.exps).get(i);
                    e = expressionSemantic(e, this.sc);
                    if (!(e.type != null))
                    {
                        exp.error(new BytePtr("`%s` has no value"), e.toChars());
                        err = true;
                    }
                    else if ((e.op & 0xFF) == 127)
                        err = true;
                    else
                        exp.exps.set(i, e);
                }
            }
            if (err)
                this.setError();
                return ;
            expandTuples(exp.exps);
            exp.type = new TypeTuple(exp.exps);
            exp.type = typeSemantic(exp.type, exp.loc, this.sc);
            this.result = exp;
        }

        public  void visit(ArrayLiteralExp e) {
            if (e.type != null)
            {
                this.result = e;
                return ;
            }
            if (e.basis != null)
                e.basis = expressionSemantic(e.basis, this.sc);
            if ((arrayExpressionSemantic(e.elements, this.sc, false) || (e.basis != null && (e.basis.op & 0xFF) == 127)))
                this.setError();
                return ;
            expandTuples(e.elements);
            Ref<Type> t0 = ref(null);
            if (e.basis != null)
                (e.elements).push(e.basis);
            boolean err = arrayExpressionToCommonType(this.sc, e.elements, ptr(t0));
            if (e.basis != null)
                e.basis = (e.elements).pop();
            if (err)
                this.setError();
                return ;
            e.type = t0.value.arrayOf();
            e.type = typeSemantic(e.type, e.loc, this.sc);
            if (((e.elements).length > 0 && (t0.value.ty & 0xFF) == ENUMTY.Tvoid))
            {
                e.error(new BytePtr("`%s` of type `%s` has no value"), e.toChars(), e.type.toChars());
                this.setError();
                return ;
            }
            if ((global.params.useTypeInfo && Type.dtypeinfo != null))
                semanticTypeInfo(this.sc, e.type);
            this.result = e;
        }

        public  void visit(AssocArrayLiteralExp e) {
            if (e.type != null)
            {
                this.result = e;
                return ;
            }
            boolean err_keys = arrayExpressionSemantic(e.keys, this.sc, false);
            boolean err_vals = arrayExpressionSemantic(e.values, this.sc, false);
            if ((err_keys || err_vals))
                this.setError();
                return ;
            expandTuples(e.keys);
            expandTuples(e.values);
            if ((e.keys).length != (e.values).length)
            {
                e.error(new BytePtr("number of keys is %u, must match number of values %u"), (e.keys).length, (e.values).length);
                this.setError();
                return ;
            }
            Ref<Type> tkey = ref(null);
            Ref<Type> tvalue = ref(null);
            err_keys = arrayExpressionToCommonType(this.sc, e.keys, ptr(tkey));
            err_vals = arrayExpressionToCommonType(this.sc, e.values, ptr(tvalue));
            if ((err_keys || err_vals))
                this.setError();
                return ;
            if ((pequals(tkey.value, Type.terror) || pequals(tvalue.value, Type.terror)))
                this.setError();
                return ;
            e.type = new TypeAArray(tvalue.value, tkey.value);
            e.type = typeSemantic(e.type, e.loc, this.sc);
            semanticTypeInfo(this.sc, e.type);
            if (global.params.vsafe)
            {
                if (checkAssocArrayLiteralEscape(this.sc, e, false))
                    this.setError();
                    return ;
            }
            this.result = e;
        }

        public  void visit(StructLiteralExp e) {
            if (e.type != null)
            {
                this.result = e;
                return ;
            }
            e.sd.size(e.loc);
            if (e.sd.sizeok != Sizeok.done)
                this.setError();
                return ;
            if (arrayExpressionSemantic(e.elements, this.sc, false))
                this.setError();
                return ;
            expandTuples(e.elements);
            if (!(e.sd.fit(e.loc, this.sc, e.elements, e.stype)))
                this.setError();
                return ;
            if (!(e.sd.fill(e.loc, e.elements, false)))
            {
                global.increaseErrorCount();
                this.setError();
                return ;
            }
            if (checkFrameAccess(e.loc, this.sc, e.sd, (e.elements).length))
                this.setError();
                return ;
            e.type = e.stype != null ? e.stype : e.sd.type;
            this.result = e;
        }

        public  void visit(TypeExp exp) {
            if ((exp.type.ty & 0xFF) == ENUMTY.Terror)
                this.setError();
                return ;
            Ref<Expression> e = ref(null);
            Ref<Type> t = ref(null);
            Ref<Dsymbol> s = ref(null);
            resolve(exp.type, exp.loc, this.sc, ptr(e), ptr(t), ptr(s), true);
            if (e.value != null)
            {
                e.value = expressionSemantic(e.value, this.sc);
            }
            else if (t.value != null)
            {
                exp.type = typeSemantic(t.value, exp.loc, this.sc);
                e.value = exp;
            }
            else if (s.value != null)
            {
                e.value = symbolToExp(s.value, exp.loc, this.sc, true);
            }
            else
                throw new AssertionError("Unreachable code!");
            if (global.params.vcomplex)
                exp.type.checkComplexTransition(exp.loc, this.sc);
            this.result = e.value;
        }

        public  void visit(ScopeExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            ScopeDsymbol sds2 = exp.sds;
            TemplateInstance ti = sds2.isTemplateInstance();
            for (; ti != null;){
                Ref<WithScopeSymbol> withsym = ref(null);
                if ((!(ti.findTempDecl(this.sc, ptr(withsym))) || !(ti.semanticTiargs(this.sc))))
                    this.setError();
                    return ;
                if ((withsym.value != null && withsym.value.withstate.wthis != null))
                {
                    Expression e = new VarExp(exp.loc, withsym.value.withstate.wthis, true);
                    e = new DotTemplateInstanceExp(exp.loc, e, ti);
                    this.result = expressionSemantic(e, this.sc);
                    return ;
                }
                if (ti.needsTypeInference(this.sc, 0))
                {
                    {
                        TemplateDeclaration td = ti.tempdecl.isTemplateDeclaration();
                        if (td != null)
                        {
                            Dsymbol p = td.toParentLocal();
                            FuncDeclaration fdthis = hasThis(this.sc);
                            AggregateDeclaration ad = p != null ? p.isAggregateDeclaration() : null;
                            if ((((fdthis != null && ad != null) && pequals(fdthis.isMemberLocal(), ad)) && ((td._scope).stc & 1L) == 0L))
                            {
                                Expression e = new DotTemplateInstanceExp(exp.loc, new ThisExp(exp.loc), ti.name, ti.tiargs);
                                this.result = expressionSemantic(e, this.sc);
                                return ;
                            }
                        }
                        else {
                            OverloadSet os = ti.tempdecl.isOverloadSet();
                            if (os != null)
                            {
                                FuncDeclaration fdthis = hasThis(this.sc);
                                AggregateDeclaration ad = os.parent.isAggregateDeclaration();
                                if (((fdthis != null && ad != null) && pequals(fdthis.isMemberLocal(), ad)))
                                {
                                    Expression e = new DotTemplateInstanceExp(exp.loc, new ThisExp(exp.loc), ti.name, ti.tiargs);
                                    this.result = expressionSemantic(e, this.sc);
                                    return ;
                                }
                            }
                        }
                    }
                    exp.sds = ti;
                    exp.type = Type.tvoid;
                    this.result = exp;
                    return ;
                }
                dsymbolSemantic(ti, this.sc);
                if ((!(ti.inst != null) || ti.errors))
                    this.setError();
                    return ;
                Dsymbol s = ti.toAlias();
                if (pequals(s, ti))
                {
                    exp.sds = ti;
                    exp.type = Type.tvoid;
                    this.result = exp;
                    return ;
                }
                sds2 = s.isScopeDsymbol();
                if (sds2 != null)
                {
                    ti = sds2.isTemplateInstance();
                    continue;
                }
                {
                    VarDeclaration v = s.isVarDeclaration();
                    if (v != null)
                    {
                        if (!(v.type != null))
                        {
                            exp.error(new BytePtr("forward reference of %s `%s`"), v.kind(), v.toChars());
                            this.setError();
                            return ;
                        }
                        if (((v.storage_class & 8388608L) != 0 && v._init != null))
                        {
                            if ((ti.inuse) != 0)
                            {
                                exp.error(new BytePtr("recursive expansion of %s `%s`"), ti.kind(), ti.toPrettyChars(false));
                                this.setError();
                                return ;
                            }
                            v.checkDeprecated(exp.loc, this.sc);
                            Expression e = v.expandInitializer(exp.loc);
                            ti.inuse++;
                            e = expressionSemantic(e, this.sc);
                            ti.inuse--;
                            this.result = e;
                            return ;
                        }
                    }
                }
                Expression e = symbolToExp(s, exp.loc, this.sc, true);
                this.result = e;
                return ;
            }
            dsymbolSemantic(sds2, this.sc);
            {
                Type t = sds2.getType();
                if (t != null)
                {
                    this.result = expressionSemantic(new TypeExp(exp.loc, t), this.sc);
                    return ;
                }
            }
            {
                TemplateDeclaration td = sds2.isTemplateDeclaration();
                if (td != null)
                {
                    this.result = expressionSemantic(new TemplateExp(exp.loc, td, null), this.sc);
                    return ;
                }
            }
            exp.sds = sds2;
            exp.type = Type.tvoid;
            this.result = exp;
        }

        public  void visit(NewExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            Expression edim = null;
            if ((exp.arguments == null && (exp.newtype.ty & 0xFF) == ENUMTY.Tsarray))
            {
                edim = ((TypeSArray)exp.newtype).dim;
                exp.newtype = ((TypeNext)exp.newtype).next;
            }
            ClassDeclaration cdthis = null;
            if (exp.thisexp != null)
            {
                exp.thisexp = expressionSemantic(exp.thisexp, this.sc);
                if ((exp.thisexp.op & 0xFF) == 127)
                    this.setError();
                    return ;
                cdthis = exp.thisexp.type.isClassHandle();
                if (!(cdthis != null))
                {
                    exp.error(new BytePtr("`this` for nested class must be a class type, not `%s`"), exp.thisexp.type.toChars());
                    this.setError();
                    return ;
                }
                this.sc = (this.sc).push(cdthis);
                exp.type = typeSemantic(exp.newtype, exp.loc, this.sc);
                this.sc = (this.sc).pop();
            }
            else
            {
                exp.type = typeSemantic(exp.newtype, exp.loc, this.sc);
            }
            if ((exp.type.ty & 0xFF) == ENUMTY.Terror)
                this.setError();
                return ;
            if (edim != null)
            {
                if ((exp.type.toBasetype().ty & 0xFF) == ENUMTY.Ttuple)
                {
                    exp.type = new TypeSArray(exp.type, edim);
                    exp.type = typeSemantic(exp.type, exp.loc, this.sc);
                    if ((exp.type.ty & 0xFF) == ENUMTY.Terror)
                        this.setError();
                        return ;
                }
                else
                {
                    exp.arguments = new DArray<Expression>();
                    (exp.arguments).push(edim);
                    exp.type = exp.type.arrayOf();
                }
            }
            exp.newtype = exp.type;
            Type tb = exp.type.toBasetype();
            if ((arrayExpressionSemantic(exp.newargs, this.sc, false) || preFunctionParameters(this.sc, exp.newargs)))
            {
                this.setError();
                return ;
            }
            if ((arrayExpressionSemantic(exp.arguments, this.sc, false) || preFunctionParameters(this.sc, exp.arguments)))
            {
                this.setError();
                return ;
            }
            if ((exp.thisexp != null && (tb.ty & 0xFF) != ENUMTY.Tclass))
            {
                exp.error(new BytePtr("`.new` is only for allocating nested classes, not `%s`"), tb.toChars());
                this.setError();
                return ;
            }
            int nargs = exp.arguments != null ? (exp.arguments).length : 0;
            Ref<Expression> newprefix = ref(null);
            if ((tb.ty & 0xFF) == ENUMTY.Tclass)
            {
                ClassDeclaration cd = ((TypeClass)tb).sym;
                cd.size(exp.loc);
                if (cd.sizeok != Sizeok.done)
                    this.setError();
                    return ;
                if (!(cd.ctor != null))
                    cd.ctor = cd.searchCtor();
                if (((cd.noDefaultCtor && !((nargs) != 0)) && !(cd.defaultCtor != null)))
                {
                    exp.error(new BytePtr("default construction is disabled for type `%s`"), cd.type.toChars());
                    this.setError();
                    return ;
                }
                if (cd.isInterfaceDeclaration() != null)
                {
                    exp.error(new BytePtr("cannot create instance of interface `%s`"), cd.toChars());
                    this.setError();
                    return ;
                }
                if (cd.isAbstract())
                {
                    exp.error(new BytePtr("cannot create instance of abstract class `%s`"), cd.toChars());
                    {
                        int i = 0;
                        for (; i < cd.vtbl.length;i++){
                            FuncDeclaration fd = cd.vtbl.get(i).isFuncDeclaration();
                            if ((fd != null && fd.isAbstract()))
                            {
                                errorSupplemental(exp.loc, new BytePtr("function `%s` is not implemented"), fd.toFullSignature());
                            }
                        }
                    }
                    this.setError();
                    return ;
                }
                if (cd.isNested())
                {
                    Dsymbol s = cd.toParentLocal();
                    {
                        ClassDeclaration cdn = s.isClassDeclaration();
                        if (cdn != null)
                        {
                            if (!(cdthis != null))
                            {
                                exp.thisexp = new ThisExp(exp.loc);
                                {
                                    Dsymbol sp = (this.sc).parent;
                                    for (; (1) != 0;sp = sp.toParentLocal()){
                                        if (!(sp != null))
                                        {
                                            exp.error(new BytePtr("outer class `%s` `this` needed to `new` nested class `%s`"), cdn.toChars(), cd.toChars());
                                            this.setError();
                                            return ;
                                        }
                                        ClassDeclaration cdp = sp.isClassDeclaration();
                                        if (!(cdp != null))
                                            continue;
                                        if ((pequals(cdp, cdn) || cdn.isBaseOf(cdp, null)))
                                            break;
                                        exp.thisexp = new DotIdExp(exp.loc, exp.thisexp, Id.outer);
                                    }
                                }
                                exp.thisexp = expressionSemantic(exp.thisexp, this.sc);
                                if ((exp.thisexp.op & 0xFF) == 127)
                                    this.setError();
                                    return ;
                                cdthis = exp.thisexp.type.isClassHandle();
                            }
                            if ((!pequals(cdthis, cdn) && !(cdn.isBaseOf(cdthis, null))))
                            {
                                exp.error(new BytePtr("`this` for nested class must be of type `%s`, not `%s`"), cdn.toChars(), exp.thisexp.type.toChars());
                                this.setError();
                                return ;
                            }
                            if (!(MODimplicitConv(exp.thisexp.type.mod, exp.newtype.mod)))
                            {
                                exp.error(new BytePtr("nested type `%s` should have the same or weaker constancy as enclosing type `%s`"), exp.newtype.toChars(), exp.thisexp.type.toChars());
                                this.setError();
                                return ;
                            }
                        }
                        else if (exp.thisexp != null)
                        {
                            exp.error(new BytePtr("`.new` is only for allocating nested classes"));
                            this.setError();
                            return ;
                        }
                        else {
                            FuncDeclaration fdn = s.isFuncDeclaration();
                            if (fdn != null)
                            {
                                if (!(ensureStaticLinkTo((this.sc).parent, fdn)))
                                {
                                    exp.error(new BytePtr("outer function context of `%s` is needed to `new` nested class `%s`"), fdn.toPrettyChars(false), cd.toPrettyChars(false));
                                    this.setError();
                                    return ;
                                }
                            }
                            else
                                throw new AssertionError("Unreachable code!");
                        }
                    }
                }
                else if (exp.thisexp != null)
                {
                    exp.error(new BytePtr("`.new` is only for allocating nested classes"));
                    this.setError();
                    return ;
                }
                if (cd.vthis2 != null)
                {
                    {
                        AggregateDeclaration ad2 = cd.isMember2();
                        if (ad2 != null)
                        {
                            Expression te = expressionSemantic(new ThisExp(exp.loc), this.sc);
                            if ((te.op & 0xFF) != 127)
                                te = getRightThis(exp.loc, this.sc, ad2, te, cd, 0);
                            if ((te.op & 0xFF) == 127)
                            {
                                exp.error(new BytePtr("need `this` of type `%s` needed to `new` nested class `%s`"), ad2.toChars(), cd.toChars());
                                this.setError();
                                return ;
                            }
                        }
                    }
                }
                if (cd.aggNew != null)
                {
                    Expression e = new IntegerExp(exp.loc, cd.size(exp.loc), Type.tsize_t);
                    if (exp.newargs == null)
                        exp.newargs = new DArray<Expression>();
                    (exp.newargs).shift(e);
                    FuncDeclaration f = resolveFuncCall(exp.loc, this.sc, cd.aggNew, null, tb, exp.newargs, FuncResolveFlag.standard);
                    if ((!(f != null) || f.errors))
                        this.setError();
                        return ;
                    checkFunctionAttributes(exp, this.sc, f);
                    checkAccess((AggregateDeclaration)cd, exp.loc, this.sc, (Dsymbol)f);
                    TypeFunction tf = (TypeFunction)f.type;
                    Ref<Type> rettype = ref(null);
                    if (functionParameters(exp.loc, this.sc, tf, null, null, exp.newargs, f, ptr(rettype), ptr(newprefix)))
                        this.setError();
                        return ;
                    exp.allocator = f.isNewDeclaration();
                    assert(exp.allocator != null);
                }
                else
                {
                    if ((exp.newargs != null && ((exp.newargs).length) != 0))
                    {
                        exp.error(new BytePtr("no allocator for `%s`"), cd.toChars());
                        this.setError();
                        return ;
                    }
                }
                if (cd.ctor != null)
                {
                    FuncDeclaration f = resolveFuncCall(exp.loc, this.sc, cd.ctor, null, tb, exp.arguments, FuncResolveFlag.standard);
                    if ((!(f != null) || f.errors))
                        this.setError();
                        return ;
                    checkFunctionAttributes(exp, this.sc, f);
                    checkAccess((AggregateDeclaration)cd, exp.loc, this.sc, (Dsymbol)f);
                    TypeFunction tf = (TypeFunction)f.type;
                    if (exp.arguments == null)
                        exp.arguments = new DArray<Expression>();
                    if (functionParameters(exp.loc, this.sc, tf, null, exp.type, exp.arguments, f, exp.type, exp.argprefix))
                        this.setError();
                        return ;
                    exp.member = f.isCtorDeclaration();
                    assert(exp.member != null);
                }
                else
                {
                    if ((nargs) != 0)
                    {
                        exp.error(new BytePtr("no constructor for `%s`"), cd.toChars());
                        this.setError();
                        return ;
                    }
                    {
                        ClassDeclaration c = cd;
                        for (; c != null;c = c.baseClass){
                            {
                                Slice<VarDeclaration> __r1374 = c.fields.opSlice().copy();
                                int __key1375 = 0;
                                for (; __key1375 < __r1374.getLength();__key1375 += 1) {
                                    VarDeclaration v = __r1374.get(__key1375);
                                    if (((((v.inuse) != 0 || v._scope == null) || v._init == null) || v._init.isVoidInitializer() != null))
                                        continue;
                                    v.inuse++;
                                    v._init = initializerSemantic(v._init, v._scope, v.type, NeedInterpret.INITinterpret);
                                    v.inuse--;
                                }
                            }
                        }
                    }
                }
            }
            else if ((tb.ty & 0xFF) == ENUMTY.Tstruct)
            {
                StructDeclaration sd = ((TypeStruct)tb).sym;
                sd.size(exp.loc);
                if (sd.sizeok != Sizeok.done)
                    this.setError();
                    return ;
                if (!(sd.ctor != null))
                    sd.ctor = sd.searchCtor();
                if ((sd.noDefaultCtor && !((nargs) != 0)))
                {
                    exp.error(new BytePtr("default construction is disabled for type `%s`"), sd.type.toChars());
                    this.setError();
                    return ;
                }
                if (sd.aggNew != null)
                {
                    Expression e = new IntegerExp(exp.loc, sd.size(exp.loc), Type.tsize_t);
                    if (exp.newargs == null)
                        exp.newargs = new DArray<Expression>();
                    (exp.newargs).shift(e);
                    FuncDeclaration f = resolveFuncCall(exp.loc, this.sc, sd.aggNew, null, tb, exp.newargs, FuncResolveFlag.standard);
                    if ((!(f != null) || f.errors))
                        this.setError();
                        return ;
                    checkFunctionAttributes(exp, this.sc, f);
                    checkAccess((AggregateDeclaration)sd, exp.loc, this.sc, (Dsymbol)f);
                    TypeFunction tf = (TypeFunction)f.type;
                    Ref<Type> rettype = ref(null);
                    if (functionParameters(exp.loc, this.sc, tf, null, null, exp.newargs, f, ptr(rettype), ptr(newprefix)))
                        this.setError();
                        return ;
                    exp.allocator = f.isNewDeclaration();
                    assert(exp.allocator != null);
                }
                else
                {
                    if ((exp.newargs != null && ((exp.newargs).length) != 0))
                    {
                        exp.error(new BytePtr("no allocator for `%s`"), sd.toChars());
                        this.setError();
                        return ;
                    }
                }
                if ((sd.ctor != null && (nargs) != 0))
                {
                    FuncDeclaration f = resolveFuncCall(exp.loc, this.sc, sd.ctor, null, tb, exp.arguments, FuncResolveFlag.standard);
                    if ((!(f != null) || f.errors))
                        this.setError();
                        return ;
                    checkFunctionAttributes(exp, this.sc, f);
                    checkAccess((AggregateDeclaration)sd, exp.loc, this.sc, (Dsymbol)f);
                    TypeFunction tf = (TypeFunction)f.type;
                    if (exp.arguments == null)
                        exp.arguments = new DArray<Expression>();
                    if (functionParameters(exp.loc, this.sc, tf, null, exp.type, exp.arguments, f, exp.type, exp.argprefix))
                        this.setError();
                        return ;
                    exp.member = f.isCtorDeclaration();
                    assert(exp.member != null);
                    if (checkFrameAccess(exp.loc, this.sc, sd, sd.fields.length))
                        this.setError();
                        return ;
                }
                else
                {
                    if (exp.arguments == null)
                        exp.arguments = new DArray<Expression>();
                    if (!(sd.fit(exp.loc, this.sc, exp.arguments, tb)))
                        this.setError();
                        return ;
                    if (!(sd.fill(exp.loc, exp.arguments, false)))
                        this.setError();
                        return ;
                    if (checkFrameAccess(exp.loc, this.sc, sd, exp.arguments != null ? (exp.arguments).length : 0))
                        this.setError();
                        return ;
                    if (global.params.vsafe)
                    {
                        {
                            Slice<Expression> __r1376 = (exp.arguments).opSlice().copy();
                            int __key1377 = 0;
                            for (; __key1377 < __r1376.getLength();__key1377 += 1) {
                                Expression arg = __r1376.get(__key1377);
                                if ((arg != null && checkNewEscape(this.sc, arg, false)))
                                    this.setError();
                                    return ;
                            }
                        }
                    }
                }
                exp.type = exp.type.pointerTo();
            }
            else if (((tb.ty & 0xFF) == ENUMTY.Tarray && (nargs) != 0))
            {
                Type tn = tb.nextOf().baseElemOf();
                Dsymbol s = tn.toDsymbol(this.sc);
                AggregateDeclaration ad = s != null ? s.isAggregateDeclaration() : null;
                if ((ad != null && ad.noDefaultCtor))
                {
                    exp.error(new BytePtr("default construction is disabled for type `%s`"), tb.nextOf().toChars());
                    this.setError();
                    return ;
                }
                {
                    int i = 0;
                    for (; i < nargs;i++){
                        if ((tb.ty & 0xFF) != ENUMTY.Tarray)
                        {
                            exp.error(new BytePtr("too many arguments for array"));
                            this.setError();
                            return ;
                        }
                        Expression arg = (exp.arguments).get(i);
                        arg = resolveProperties(this.sc, arg);
                        arg = arg.implicitCastTo(this.sc, Type.tsize_t);
                        if ((arg.op & 0xFF) == 127)
                            this.setError();
                            return ;
                        arg = arg.optimize(0, false);
                        if (((arg.op & 0xFF) == 135 && (long)arg.toInteger() < 0L))
                        {
                            exp.error(new BytePtr("negative array index `%s`"), arg.toChars());
                            this.setError();
                            return ;
                        }
                        exp.arguments.set(i, arg);
                        tb = ((TypeDArray)tb).next.toBasetype();
                    }
                }
            }
            else if (tb.isscalar())
            {
                if (!((nargs) != 0))
                {
                }
                else if (nargs == 1)
                {
                    Expression e = (exp.arguments).get(0);
                    e = e.implicitCastTo(this.sc, tb);
                    exp.arguments.set(0, e);
                }
                else
                {
                    exp.error(new BytePtr("more than one argument for construction of `%s`"), exp.type.toChars());
                    this.setError();
                    return ;
                }
                exp.type = exp.type.pointerTo();
            }
            else
            {
                exp.error(new BytePtr("new can only create structs, dynamic arrays or class objects, not `%s`'s"), exp.type.toChars());
                this.setError();
                return ;
            }
            semanticTypeInfo(this.sc, exp.type);
            if (newprefix.value != null)
            {
                this.result = Expression.combine(newprefix.value, (Expression)exp);
                return ;
            }
            this.result = exp;
        }

        public  void visit(NewAnonClassExp e) {
            Expression d = new DeclarationExp(e.loc, e.cd);
            this.sc = (this.sc).push();
            (this.sc).flags &= -129;
            d = expressionSemantic(d, this.sc);
            this.sc = (this.sc).pop();
            if (((!(e.cd.errors) && ((this.sc).intypeof) != 0) && !((this.sc).parent.inNonRoot())))
            {
                ScopeDsymbol sds = (this.sc).tinst != null ? (this.sc).tinst : (this.sc)._module;
                if (sds.members == null)
                    sds.members = new DArray<Dsymbol>();
                (sds.members).push(e.cd);
            }
            Expression n = new NewExp(e.loc, e.thisexp, e.newargs, e.cd.type, e.arguments);
            Expression c = new CommaExp(e.loc, d, n, true);
            this.result = expressionSemantic(c, this.sc);
        }

        public  void visit(SymOffExp e) {
            if (!(e.type != null))
                e.type = e.var.type.pointerTo();
            {
                VarDeclaration v = e.var.isVarDeclaration();
                if (v != null)
                {
                    if (v.checkNestedReference(this.sc, e.loc))
                        this.setError();
                        return ;
                }
                else {
                    FuncDeclaration f = e.var.isFuncDeclaration();
                    if (f != null)
                    {
                        if (f.checkNestedReference(this.sc, e.loc))
                            this.setError();
                            return ;
                    }
                }
            }
            this.result = e;
        }

        public  void visit(VarExp e) {
            VarDeclaration vd = e.var.isVarDeclaration();
            FuncDeclaration fd = e.var.isFuncDeclaration();
            if (fd != null)
            {
                if (!(fd.functionSemantic()))
                    this.setError();
                    return ;
            }
            if (!(e.type != null))
                e.type = e.var.type;
            if ((e.type != null && e.type.deco == null))
            {
                Declaration decl = e.var.isDeclaration();
                if (decl != null)
                    decl.inuse++;
                e.type = typeSemantic(e.type, e.loc, this.sc);
                if (decl != null)
                    decl.inuse--;
            }
            if (vd != null)
            {
                if (vd.checkNestedReference(this.sc, e.loc))
                    this.setError();
                    return ;
            }
            else if (fd != null)
            {
                if (fd.checkNestedReference(this.sc, e.loc))
                    this.setError();
                    return ;
            }
            else {
                OverDeclaration od = e.var.isOverDeclaration();
                if (od != null)
                {
                    e.type = Type.tvoid;
                }
            }
            this.result = e;
        }

        public  void visit(FuncExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            Expression e = exp;
            int olderrors = 0;
            this.sc = (this.sc).push();
            (this.sc).flags &= -129;
            (this.sc).protection = new Prot(Prot.Kind.public_).copy();
            exp.genIdent(this.sc);
            if ((exp.fd.treq != null && !(exp.fd.type.nextOf() != null)))
            {
                TypeFunction tfv = null;
                if (((exp.fd.treq.ty & 0xFF) == ENUMTY.Tdelegate || ((exp.fd.treq.ty & 0xFF) == ENUMTY.Tpointer && (exp.fd.treq.nextOf().ty & 0xFF) == ENUMTY.Tfunction)))
                    tfv = (TypeFunction)exp.fd.treq.nextOf();
                if (tfv != null)
                {
                    TypeFunction tfl = (TypeFunction)exp.fd.type;
                    tfl.next = tfv.nextOf();
                }
            }
            try {
                if (exp.td != null)
                {
                    assert((exp.td.parameters != null && ((exp.td.parameters).length) != 0));
                    dsymbolSemantic(exp.td, this.sc);
                    exp.type = Type.tvoid;
                    if (exp.fd.treq != null)
                    {
                        Ref<FuncExp> fe = ref(null);
                        if (exp.matchType(exp.fd.treq, this.sc, ptr(fe), 0) > MATCH.nomatch)
                            e = fe.value;
                        else
                            e = new ErrorExp();
                    }
                    /*goto Ldone*/throw Dispatch0.INSTANCE;
                }
                olderrors = global.errors;
                dsymbolSemantic(exp.fd, this.sc);
                if (olderrors == global.errors)
                {
                    semantic2(exp.fd, this.sc);
                    if (olderrors == global.errors)
                        semantic3(exp.fd, this.sc);
                }
                if (olderrors != global.errors)
                {
                    if (((exp.fd.type != null && (exp.fd.type.ty & 0xFF) == ENUMTY.Tfunction) && !(exp.fd.type.nextOf() != null)))
                        ((TypeFunction)exp.fd.type).next = Type.terror;
                    e = new ErrorExp();
                    /*goto Ldone*/throw Dispatch0.INSTANCE;
                }
                if (((exp.fd.isNested() && (exp.fd.tok & 0xFF) == 160) || (((exp.tok & 0xFF) == 0 && exp.fd.treq != null) && (exp.fd.treq.ty & 0xFF) == ENUMTY.Tdelegate)))
                {
                    exp.type = new TypeDelegate(exp.fd.type);
                    exp.type = typeSemantic(exp.type, exp.loc, this.sc);
                    exp.fd.tok = TOK.delegate_;
                }
                else
                {
                    exp.type = new TypePointer(exp.fd.type);
                    exp.type = typeSemantic(exp.type, exp.loc, this.sc);
                    if ((exp.fd.treq != null && (exp.fd.treq.ty & 0xFF) == ENUMTY.Tpointer))
                    {
                        exp.fd.tok = TOK.function_;
                        exp.fd.vthis = null;
                    }
                }
                exp.fd.tookAddressOf++;
            }
            catch(Dispatch0 __d){}
        /*Ldone:*/
            this.sc = (this.sc).pop();
            this.result = e;
        }

        public  Expression callExpSemantic(FuncExp exp, Scope sc, DArray<Expression> arguments) {
            if (((((!(exp.type != null) || pequals(exp.type, Type.tvoid)) && exp.td != null) && arguments != null) && ((arguments).length) != 0))
            {
                {
                    int k = 0;
                    for (; k < (arguments).length;k++){
                        Expression checkarg = (arguments).get(k);
                        if ((checkarg.op & 0xFF) == 127)
                            return checkarg;
                    }
                }
                exp.genIdent(sc);
                assert((exp.td.parameters != null && ((exp.td.parameters).length) != 0));
                dsymbolSemantic(exp.td, sc);
                TypeFunction tfl = (TypeFunction)exp.fd.type;
                int dim = tfl.parameterList.length();
                if ((arguments).length < dim)
                {
                    Parameter p = tfl.parameterList.get((arguments).length);
                    if (p.defaultArg != null)
                        dim = (arguments).length;
                }
                if (((tfl.parameterList.varargs == VarArg.none && (arguments).length == dim) || (tfl.parameterList.varargs != VarArg.none && (arguments).length >= dim)))
                {
                    DArray<RootObject> tiargs = new DArray<RootObject>();
                    (tiargs).reserve((exp.td.parameters).length);
                    {
                        int i = 0;
                        for (; i < (exp.td.parameters).length;i++){
                            TemplateParameter tp = (exp.td.parameters).get(i);
                            {
                                int u = 0;
                                for (; u < dim;u++){
                                    Parameter p = tfl.parameterList.get(u);
                                    if (((p.type.ty & 0xFF) == ENUMTY.Tident && pequals(((TypeIdentifier)p.type).ident, tp.ident)))
                                    {
                                        Expression e = (arguments).get(u);
                                        (tiargs).push(e.type);
                                        u = dim;
                                    }
                                }
                            }
                        }
                    }
                    TemplateInstance ti = new TemplateInstance(exp.loc, exp.td, tiargs);
                    return expressionSemantic(new ScopeExp(exp.loc, ti), sc);
                }
                exp.error(new BytePtr("cannot infer function literal type"));
                return new ErrorExp();
            }
            return expressionSemantic(exp, sc);
        }

        public  void visit(CallExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            Type t1 = null;
            DArray<RootObject> tiargs = null;
            Expression ethis = null;
            Type tthis = null;
            Expression e1org = exp.e1;
            if ((exp.e1.op & 0xFF) == 99)
            {
                CommaExp ce = (CommaExp)exp.e1;
                exp.e1 = ce.e2;
                ce.e2 = exp;
                this.result = expressionSemantic(ce, this.sc);
                return ;
            }
            if ((exp.e1.op & 0xFF) == 160)
            {
                DelegateExp de = (DelegateExp)exp.e1;
                exp.e1 = new DotVarExp(de.loc, de.e1, de.func, de.hasOverloads);
                this.visit(exp);
                return ;
            }
            if ((exp.e1.op & 0xFF) == 161)
            {
                if ((arrayExpressionSemantic(exp.arguments, this.sc, false) || preFunctionParameters(this.sc, exp.arguments)))
                    this.setError();
                    return ;
                FuncExp fe = (FuncExp)exp.e1;
                exp.e1 = this.callExpSemantic(fe, this.sc, exp.arguments);
                if ((exp.e1.op & 0xFF) == 127)
                {
                    this.result = exp.e1;
                    return ;
                }
            }
            {
                Expression ex = resolveUFCS(this.sc, exp);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            try {
                if ((exp.e1.op & 0xFF) == 203)
                {
                    ScopeExp se = (ScopeExp)exp.e1;
                    TemplateInstance ti = se.sds.isTemplateInstance();
                    if (ti != null)
                    {
                        Ref<WithScopeSymbol> withsym = ref(null);
                        if ((!(ti.findTempDecl(this.sc, ptr(withsym))) || !(ti.semanticTiargs(this.sc))))
                            this.setError();
                            return ;
                        if ((withsym.value != null && withsym.value.withstate.wthis != null))
                        {
                            exp.e1 = new VarExp(exp.e1.loc, withsym.value.withstate.wthis, true);
                            exp.e1 = new DotTemplateInstanceExp(exp.e1.loc, exp.e1, ti);
                            /*goto Ldotti*/throw Dispatch0.INSTANCE;
                        }
                        if (ti.needsTypeInference(this.sc, 1))
                        {
                            tiargs = ti.tiargs;
                            assert(ti.tempdecl != null);
                            {
                                TemplateDeclaration td = ti.tempdecl.isTemplateDeclaration();
                                if (td != null)
                                    exp.e1 = new TemplateExp(exp.loc, td, null);
                                else {
                                    OverDeclaration od = ti.tempdecl.isOverDeclaration();
                                    if (od != null)
                                        exp.e1 = new VarExp(exp.loc, od, true);
                                    else
                                        exp.e1 = new OverExp(exp.loc, ti.tempdecl.isOverloadSet());
                                }
                            }
                        }
                        else
                        {
                            Expression e1x = expressionSemantic(exp.e1, this.sc);
                            if ((e1x.op & 0xFF) == 127)
                            {
                                this.result = e1x;
                                return ;
                            }
                            exp.e1 = e1x;
                        }
                    }
                }
            }
            catch(Dispatch0 __d){}
        /*Ldotti:*/
            if (((exp.e1.op & 0xFF) == 29 && !(exp.e1.type != null)))
            {
                DotTemplateInstanceExp se = (DotTemplateInstanceExp)exp.e1;
                TemplateInstance ti = se.ti;
                {
                    if ((!(se.findTempDecl(this.sc)) || !(ti.semanticTiargs(this.sc))))
                        this.setError();
                        return ;
                    if (ti.needsTypeInference(this.sc, 1))
                    {
                        tiargs = ti.tiargs;
                        assert(ti.tempdecl != null);
                        {
                            TemplateDeclaration td = ti.tempdecl.isTemplateDeclaration();
                            if (td != null)
                                exp.e1 = new DotTemplateExp(exp.loc, se.e1, td);
                            else {
                                OverDeclaration od = ti.tempdecl.isOverDeclaration();
                                if (od != null)
                                {
                                    exp.e1 = new DotVarExp(exp.loc, se.e1, od, true);
                                }
                                else
                                    exp.e1 = new DotExp(exp.loc, se.e1, new OverExp(exp.loc, ti.tempdecl.isOverloadSet()));
                            }
                        }
                    }
                    else
                    {
                        Expression e1x = expressionSemantic(exp.e1, this.sc);
                        if ((e1x.op & 0xFF) == 127)
                        {
                            this.result = e1x;
                            return ;
                        }
                        exp.e1 = e1x;
                    }
                }
            }
            while(true) try {
            /*Lagain:*/
                exp.f = null;
                if (((exp.e1.op & 0xFF) == 123 || (exp.e1.op & 0xFF) == 124))
                {
                }
                else
                {
                    if ((exp.e1.op & 0xFF) == 28)
                    {
                        DotIdExp die = (DotIdExp)exp.e1;
                        exp.e1 = expressionSemantic(die, this.sc);
                        if (((exp.e1.op & 0xFF) == 29 && !(exp.e1.type != null)))
                        {
                            /*goto Ldotti*/throw Dispatch0.INSTANCE;
                        }
                    }
                    else
                    {
                        if ((expressionsem.visitnest += 1) > 500)
                        {
                            exp.error(new BytePtr("recursive evaluation of `%s`"), exp.toChars());
                            expressionsem.visitnest -= 1;
                            this.setError();
                            return ;
                        }
                        Expression ex = unaSemantic(exp, this.sc);
                        expressionsem.visitnest -= 1;
                        if (ex != null)
                        {
                            this.result = ex;
                            return ;
                        }
                    }
                    if ((exp.e1.op & 0xFF) == 26)
                    {
                        VarExp ve = (VarExp)exp.e1;
                        if ((ve.var.storage_class & 8192L) != 0)
                        {
                            Type tw = ve.var.type;
                            Type tc = ve.var.type.substWildTo(1);
                            TypeFunction tf = new TypeFunction(new ParameterList(null, VarArg.none), tc, LINK.d, 8657043456L);
                            (tf = (TypeFunction)typeSemantic(tf, exp.loc, this.sc)).next = tw;
                            TypeDelegate t = new TypeDelegate(tf);
                            ve.type = typeSemantic(t, exp.loc, this.sc);
                        }
                        VarDeclaration v = ve.var.isVarDeclaration();
                        if ((v != null && ve.checkPurity(this.sc, v)))
                            this.setError();
                            return ;
                    }
                    if (((exp.e1.op & 0xFF) == 25 && ((SymOffExp)exp.e1).hasOverloads))
                    {
                        SymOffExp se = (SymOffExp)exp.e1;
                        exp.e1 = new VarExp(se.loc, se.var, true);
                        exp.e1 = expressionSemantic(exp.e1, this.sc);
                    }
                    else if ((exp.e1.op & 0xFF) == 97)
                    {
                        DotExp de = (DotExp)exp.e1;
                        if ((de.e2.op & 0xFF) == 214)
                        {
                            ethis = de.e1;
                            tthis = de.e1.type;
                            exp.e1 = de.e2;
                        }
                    }
                    else if (((exp.e1.op & 0xFF) == 24 && (exp.e1.type.ty & 0xFF) == ENUMTY.Tfunction))
                    {
                        exp.e1 = ((PtrExp)exp.e1).e1;
                    }
                }
                t1 = exp.e1.type != null ? exp.e1.type.toBasetype() : null;
                if ((exp.e1.op & 0xFF) == 127)
                {
                    this.result = exp.e1;
                    return ;
                }
                if ((arrayExpressionSemantic(exp.arguments, this.sc, false) || preFunctionParameters(this.sc, exp.arguments)))
                    this.setError();
                    return ;
                if (t1 != null)
                {
                    if ((t1.ty & 0xFF) == ENUMTY.Tstruct)
                    {
                        StructDeclaration sd = ((TypeStruct)t1).sym;
                        sd.size(exp.loc);
                        if (sd.sizeok != Sizeok.done)
                            this.setError();
                            return ;
                        if (!(sd.ctor != null))
                            sd.ctor = sd.searchCtor();
                        if (sd.ctor != null)
                        {
                            CtorDeclaration ctor = sd.ctor.isCtorDeclaration();
                            if (((ctor != null && ctor.isCpCtor) && ctor.generated))
                                sd.ctor = null;
                        }
                        try {
                            if (((exp.e1.op & 0xFF) == 20 && sd.ctor != null))
                            {
                                if ((!(sd.noDefaultCtor) && !((exp.arguments != null && ((exp.arguments).length) != 0))))
                                    /*goto Lx*/throw Dispatch0.INSTANCE;
                                StructLiteralExp sle = new StructLiteralExp(exp.loc, sd, null, exp.e1.type);
                                if (!(sd.fill(exp.loc, sle.elements, true)))
                                    this.setError();
                                    return ;
                                if (checkFrameAccess(exp.loc, this.sc, sd, (sle.elements).length))
                                    this.setError();
                                    return ;
                                sle.type = exp.e1.type;
                                sle.useStaticInit = false;
                                Expression e = sle;
                                {
                                    CtorDeclaration cf = sd.ctor.isCtorDeclaration();
                                    if (cf != null)
                                    {
                                        e = new DotVarExp(exp.loc, e, cf, true);
                                    }
                                    else {
                                        TemplateDeclaration td = sd.ctor.isTemplateDeclaration();
                                        if (td != null)
                                        {
                                            e = new DotIdExp(exp.loc, e, td.ident);
                                        }
                                        else {
                                            OverloadSet os = sd.ctor.isOverloadSet();
                                            if (os != null)
                                            {
                                                e = new DotExp(exp.loc, e, new OverExp(exp.loc, os));
                                            }
                                            else
                                                throw new AssertionError("Unreachable code!");
                                        }
                                    }
                                }
                                e = new CallExp(exp.loc, e, exp.arguments);
                                e = expressionSemantic(e, this.sc);
                                this.result = e;
                                return ;
                            }
                            if (search_function(sd, Id.call) != null)
                                /*goto L1*/if ((exp.e1.op & 0xFF) != 20)
                            {
                                if ((sd.aliasthis != null && !pequals(exp.e1.type, exp.att1)))
                                {
                                    if ((!(exp.att1 != null) && exp.e1.type.checkAliasThisRec()))
                                        exp.att1 = exp.e1.type;
                                    exp.e1 = resolveAliasThis(this.sc, exp.e1, false);
                                    /*goto Lagain*/throw Dispatch1.INSTANCE;
                                }
                                exp.error(new BytePtr("%s `%s` does not overload ()"), sd.kind(), sd.toChars());
                                this.setError();
                                return ;
                            }
                        }
                        catch(Dispatch0 __d){}
                    /*Lx:*/
                        Expression e = new StructLiteralExp(exp.loc, sd, exp.arguments, exp.e1.type);
                        e = expressionSemantic(e, this.sc);
                        this.result = e;
                        return ;
                    }
                    else if ((t1.ty & 0xFF) == ENUMTY.Tclass)
                    {
                    /*L1:*/
                        Expression e = new DotIdExp(exp.loc, exp.e1, Id.call);
                        e = new CallExp(exp.loc, e, exp.arguments);
                        e = expressionSemantic(e, this.sc);
                        this.result = e;
                        return ;
                    }
                    else if (((exp.e1.op & 0xFF) == 20 && t1.isscalar()))
                    {
                        Expression e = null;
                        if ((exp.e1.type.ty & 0xFF) == ENUMTY.Tenum)
                        {
                            t1 = exp.e1.type;
                        }
                        if ((exp.arguments == null || (exp.arguments).length == 0))
                        {
                            e = t1.defaultInitLiteral(exp.loc);
                        }
                        else if ((exp.arguments).length == 1)
                        {
                            e = (exp.arguments).get(0);
                            e = e.implicitCastTo(this.sc, t1);
                            e = new CastExp(exp.loc, e, t1);
                        }
                        else
                        {
                            exp.error(new BytePtr("more than one argument for construction of `%s`"), t1.toChars());
                            this.setError();
                            return ;
                        }
                        e = expressionSemantic(e, this.sc);
                        this.result = e;
                        return ;
                    }
                }
                Function6<Loc,Scope,OverloadSet,DArray<RootObject>,Type,DArray<Expression>,FuncDeclaration> resolveOverloadSet = new Function6<Loc,Scope,OverloadSet,DArray<RootObject>,Type,DArray<Expression>,FuncDeclaration>(){
                    public FuncDeclaration invoke(Loc loc, Scope sc, OverloadSet os, DArray<RootObject> tiargs, Type tthis, DArray<Expression> arguments){
                        FuncDeclaration f = null;
                        {
                            Slice<Dsymbol> __r1378 = os.a.opSlice().copy();
                            int __key1379 = 0;
                            for (; __key1379 < __r1378.getLength();__key1379 += 1) {
                                Dsymbol s = __r1378.get(__key1379);
                                if ((tiargs != null && s.isFuncDeclaration() != null))
                                    continue;
                                {
                                    FuncDeclaration f2 = resolveFuncCall(loc, sc, s, tiargs, tthis, arguments, FuncResolveFlag.quiet);
                                    if (f2 != null)
                                    {
                                        if (f2.errors)
                                            return null;
                                        if (f != null)
                                        {
                                            ScopeDsymbol.multiplyDefined(loc, f, f2);
                                        }
                                        else
                                            f = f2;
                                    }
                                }
                            }
                        }
                        if (!(f != null))
                            error(loc, new BytePtr("no overload matches for `%s`"), os.toChars());
                        else if (f.errors)
                            f = null;
                        return f;
                    }
                };
                boolean isSuper = false;
                if ((((exp.e1.op & 0xFF) == 27 && (t1.ty & 0xFF) == ENUMTY.Tfunction) || (exp.e1.op & 0xFF) == 37))
                {
                    UnaExp ue = (UnaExp)exp.e1;
                    Expression ue1 = ue.e1;
                    Expression ue1old = ue1;
                    VarDeclaration v = null;
                    if ((((ue1.op & 0xFF) == 26 && (v = ((VarExp)ue1).var.isVarDeclaration()) != null) && v.needThis()))
                    {
                        ue.e1 = new TypeExp(ue1.loc, ue1.type);
                        ue1 = null;
                    }
                    DotVarExp dve = null;
                    DotTemplateExp dte = null;
                    Dsymbol s = null;
                    if ((exp.e1.op & 0xFF) == 27)
                    {
                        dve = (DotVarExp)exp.e1;
                        dte = null;
                        s = dve.var;
                        tiargs = null;
                    }
                    else
                    {
                        dve = null;
                        dte = (DotTemplateExp)exp.e1;
                        s = dte.td;
                    }
                    exp.f = resolveFuncCall(exp.loc, this.sc, s, tiargs, ue1 != null ? ue1.type : null, exp.arguments, FuncResolveFlag.standard);
                    if (((!(exp.f != null) || exp.f.errors) || (exp.f.type.ty & 0xFF) == ENUMTY.Terror))
                        this.setError();
                        return ;
                    if (exp.f.interfaceVirtual != null)
                    {
                        BaseClass b = exp.f.interfaceVirtual;
                        ClassDeclaration ad2 = (b).sym;
                        ue.e1 = ue.e1.castTo(this.sc, ad2.type.addMod(ue.e1.type.mod));
                        ue.e1 = expressionSemantic(ue.e1, this.sc);
                        ue1 = ue.e1;
                        int vi = exp.f.findVtblIndex(ad2.vtbl, ad2.vtbl.length, true);
                        assert(vi >= 0);
                        exp.f = ad2.vtbl.get(vi).isFuncDeclaration();
                        assert(exp.f != null);
                    }
                    if (exp.f.needThis())
                    {
                        AggregateDeclaration ad = exp.f.toParentLocal().isAggregateDeclaration();
                        ue.e1 = getRightThis(exp.loc, this.sc, ad, ue.e1, exp.f, 0);
                        if ((ue.e1.op & 0xFF) == 127)
                        {
                            this.result = ue.e1;
                            return ;
                        }
                        ethis = ue.e1;
                        tthis = ue.e1.type;
                        if (!(((exp.f.type.ty & 0xFF) == ENUMTY.Tfunction && ((TypeFunction)exp.f.type).isscope)))
                        {
                            if ((global.params.vsafe && checkParamArgumentEscape(this.sc, exp.f, null, ethis, false)))
                                this.setError();
                                return ;
                        }
                    }
                    if (((((this.sc).func != null && (this.sc).func.isInvariantDeclaration() != null) && (ue.e1.op & 0xFF) == 123) && exp.f.addPostInvariant()))
                    {
                        exp.error(new BytePtr("cannot call `public`/`export` function `%s` from invariant"), exp.f.toChars());
                        this.setError();
                        return ;
                    }
                    checkFunctionAttributes(exp, this.sc, exp.f);
                    checkAccess(exp.loc, this.sc, ue.e1, exp.f);
                    if (!(exp.f.needThis()))
                    {
                        exp.e1 = Expression.combine(ue.e1, new VarExp(exp.loc, exp.f, false));
                    }
                    else
                    {
                        if (ue1old.checkRightThis(this.sc))
                            this.setError();
                            return ;
                        if ((exp.e1.op & 0xFF) == 27)
                        {
                            dve.var = exp.f;
                            exp.e1.type = exp.f.type;
                        }
                        else
                        {
                            exp.e1 = new DotVarExp(exp.loc, dte.e1, exp.f, false);
                            exp.e1 = expressionSemantic(exp.e1, this.sc);
                            if ((exp.e1.op & 0xFF) == 127)
                                this.setError();
                                return ;
                            ue = (UnaExp)exp.e1;
                        }
                        AggregateDeclaration ad = exp.f.isThis();
                        ClassDeclaration cd = ue.e1.type.isClassHandle();
                        if (((ad != null && cd != null) && ad.isClassDeclaration() != null))
                        {
                            if ((ue.e1.op & 0xFF) == 30)
                            {
                                ue.e1 = ((DotTypeExp)ue.e1).e1;
                                exp.directcall = true;
                            }
                            else if ((ue.e1.op & 0xFF) == 124)
                                exp.directcall = true;
                            else if ((cd.storage_class & 8L) != 0L)
                                exp.directcall = true;
                            if (!pequals(ad, cd))
                            {
                                ue.e1 = ue.e1.castTo(this.sc, ad.type.addMod(ue.e1.type.mod));
                                ue.e1 = expressionSemantic(ue.e1, this.sc);
                            }
                        }
                    }
                    if (((exp.e1.type.ty & 0xFF) == ENUMTY.Tpointer && (exp.e1.type.nextOf().ty & 0xFF) == ENUMTY.Tfunction))
                    {
                        Expression e = new PtrExp(exp.loc, exp.e1);
                        e.type = exp.e1.type.nextOf();
                        exp.e1 = e;
                    }
                    t1 = exp.e1.type;
                }
                else if (((exp.e1.op & 0xFF) == 124 || (exp.e1.op & 0xFF) == 123))
                {
                    AggregateDeclaration ad = (this.sc).func != null ? (this.sc).func.isThis() : null;
                    ClassDeclaration cd = ad != null ? ad.isClassDeclaration() : null;
                    isSuper = (exp.e1.op & 0xFF) == 124;
                    if (isSuper)
                    {
                        if (((!(cd != null) || !(cd.baseClass != null)) || !((this.sc).func.isCtorDeclaration() != null)))
                        {
                            exp.error(new BytePtr("super class constructor call must be in a constructor"));
                            this.setError();
                            return ;
                        }
                        if (!(cd.baseClass.ctor != null))
                        {
                            exp.error(new BytePtr("no super class constructor for `%s`"), cd.baseClass.toChars());
                            this.setError();
                            return ;
                        }
                    }
                    else
                    {
                        if ((!(ad != null) || !((this.sc).func.isCtorDeclaration() != null)))
                        {
                            exp.error(new BytePtr("constructor call must be in a constructor"));
                            this.setError();
                            return ;
                        }
                        {
                            Slice<FieldInit> __r1380 = (this.sc).ctorflow.fieldinit.copy();
                            int __key1381 = 0;
                            for (; __key1381 < __r1380.getLength();__key1381 += 1) {
                                FieldInit field = __r1380.get(__key1381).copy();
                                field.csx |= 65;
                            }
                        }
                    }
                    if ((!(((this.sc).intypeof) != 0) && !(((this.sc).ctorflow.callSuper & 32) != 0)))
                    {
                        if (((this.sc).inLoop || ((this.sc).ctorflow.callSuper & 4) != 0))
                            exp.error(new BytePtr("constructor calls not allowed in loops or after labels"));
                        if (((this.sc).ctorflow.callSuper & 3) != 0)
                            exp.error(new BytePtr("multiple constructor calls"));
                        if ((((this.sc).ctorflow.callSuper & 8) != 0 && !(((this.sc).ctorflow.callSuper & 16) != 0)))
                            exp.error(new BytePtr("an earlier `return` statement skips constructor"));
                        (this.sc).ctorflow.callSuper |= 16 | (isSuper ? 2 : 1);
                    }
                    tthis = ad.type.addMod((this.sc).func.type.mod);
                    Dsymbol ctor = isSuper ? cd.baseClass.ctor : ad.ctor;
                    {
                        OverloadSet os = ctor.isOverloadSet();
                        if (os != null)
                            exp.f = resolveOverloadSet.invoke(exp.loc, this.sc, os, null, tthis, exp.arguments);
                        else
                            exp.f = resolveFuncCall(exp.loc, this.sc, ctor, null, tthis, exp.arguments, FuncResolveFlag.standard);
                    }
                    if ((!(exp.f != null) || exp.f.errors))
                        this.setError();
                        return ;
                    checkFunctionAttributes(exp, this.sc, exp.f);
                    checkAccess(exp.loc, this.sc, null, exp.f);
                    exp.e1 = new DotVarExp(exp.e1.loc, exp.e1, exp.f, false);
                    exp.e1 = expressionSemantic(exp.e1, this.sc);
                    t1 = exp.e1.type;
                    if (pequals(exp.f, (this.sc).func))
                    {
                        exp.error(new BytePtr("cyclic constructor call"));
                        this.setError();
                        return ;
                    }
                }
                else if ((exp.e1.op & 0xFF) == 214)
                {
                    OverloadSet os = ((OverExp)exp.e1).vars;
                    exp.f = resolveOverloadSet.invoke(exp.loc, this.sc, os, tiargs, tthis, exp.arguments);
                    if (!(exp.f != null))
                        this.setError();
                        return ;
                    if (ethis != null)
                        exp.e1 = new DotVarExp(exp.loc, ethis, exp.f, false);
                    else
                        exp.e1 = new VarExp(exp.loc, exp.f, false);
                    /*goto Lagain*/throw Dispatch1.INSTANCE;
                }
                else if (!(t1 != null))
                {
                    exp.error(new BytePtr("function expected before `()`, not `%s`"), exp.e1.toChars());
                    this.setError();
                    return ;
                }
                else if ((t1.ty & 0xFF) == ENUMTY.Terror)
                {
                    this.setError();
                    return ;
                }
                else if ((t1.ty & 0xFF) != ENUMTY.Tfunction)
                {
                    TypeFunction tf = null;
                    BytePtr p = null;
                    Dsymbol s = null;
                    exp.f = null;
                    if ((exp.e1.op & 0xFF) == 161)
                    {
                        assert(((FuncExp)exp.e1).fd != null);
                        exp.f = ((FuncExp)exp.e1).fd;
                        tf = (TypeFunction)exp.f.type;
                        p = pcopy(new BytePtr("function literal"));
                    }
                    else if ((t1.ty & 0xFF) == ENUMTY.Tdelegate)
                    {
                        TypeDelegate td = (TypeDelegate)t1;
                        assert((td.next.ty & 0xFF) == ENUMTY.Tfunction);
                        tf = (TypeFunction)td.next;
                        p = pcopy(new BytePtr("delegate"));
                    }
                    else if (((t1.ty & 0xFF) == ENUMTY.Tpointer && (((TypePointer)t1).next.ty & 0xFF) == ENUMTY.Tfunction))
                    {
                        tf = (TypeFunction)((TypePointer)t1).next;
                        p = pcopy(new BytePtr("function pointer"));
                    }
                    else if (((exp.e1.op & 0xFF) == 27 && ((DotVarExp)exp.e1).var.isOverDeclaration() != null))
                    {
                        DotVarExp dve = (DotVarExp)exp.e1;
                        exp.f = resolveFuncCall(exp.loc, this.sc, dve.var, tiargs, dve.e1.type, exp.arguments, FuncResolveFlag.overloadOnly);
                        if (!(exp.f != null))
                            this.setError();
                            return ;
                        if (exp.f.needThis())
                        {
                            dve.var = exp.f;
                            dve.type = exp.f.type;
                            dve.hasOverloads = false;
                            /*goto Lagain*/throw Dispatch1.INSTANCE;
                        }
                        exp.e1 = new VarExp(dve.loc, exp.f, false);
                        Expression e = new CommaExp(exp.loc, dve.e1, exp, true);
                        this.result = expressionSemantic(e, this.sc);
                        return ;
                    }
                    else if (((exp.e1.op & 0xFF) == 26 && ((VarExp)exp.e1).var.isOverDeclaration() != null))
                    {
                        s = ((VarExp)exp.e1).var;
                        /*goto L2*//*unrolled goto*/
                    /*L2:*/
                        exp.f = resolveFuncCall(exp.loc, this.sc, s, tiargs, null, exp.arguments, FuncResolveFlag.standard);
                        if ((!(exp.f != null) || exp.f.errors))
                            this.setError();
                            return ;
                        if (exp.f.needThis())
                        {
                            if (hasThis(this.sc) != null)
                            {
                                exp.e1 = new DotVarExp(exp.loc, expressionSemantic(new ThisExp(exp.loc), this.sc), exp.f, false);
                                /*goto Lagain*/throw Dispatch1.INSTANCE;
                            }
                            else if (isNeedThisScope(this.sc, exp.f))
                            {
                                exp.error(new BytePtr("need `this` for `%s` of type `%s`"), exp.f.toChars(), exp.f.type.toChars());
                                this.setError();
                                return ;
                            }
                        }
                        exp.e1 = new VarExp(exp.e1.loc, exp.f, false);
                        /*goto Lagain*/throw Dispatch1.INSTANCE;
                    }
                    else if ((exp.e1.op & 0xFF) == 36)
                    {
                        s = ((TemplateExp)exp.e1).td;
                    /*L2:*/
                        exp.f = resolveFuncCall(exp.loc, this.sc, s, tiargs, null, exp.arguments, FuncResolveFlag.standard);
                        if ((!(exp.f != null) || exp.f.errors))
                            this.setError();
                            return ;
                        if (exp.f.needThis())
                        {
                            if (hasThis(this.sc) != null)
                            {
                                exp.e1 = new DotVarExp(exp.loc, expressionSemantic(new ThisExp(exp.loc), this.sc), exp.f, false);
                                /*goto Lagain*/throw Dispatch1.INSTANCE;
                            }
                            else if (isNeedThisScope(this.sc, exp.f))
                            {
                                exp.error(new BytePtr("need `this` for `%s` of type `%s`"), exp.f.toChars(), exp.f.type.toChars());
                                this.setError();
                                return ;
                            }
                        }
                        exp.e1 = new VarExp(exp.e1.loc, exp.f, false);
                        /*goto Lagain*/throw Dispatch1.INSTANCE;
                    }
                    else
                    {
                        exp.error(new BytePtr("function expected before `()`, not `%s` of type `%s`"), exp.e1.toChars(), exp.e1.type.toChars());
                        this.setError();
                        return ;
                    }
                    Ref<BytePtr> failMessage = ref(null);
                    Slice<Expression> fargs = exp.arguments != null ? (exp.arguments).opSlice() : new Slice<Expression>().copy();
                    if (!((tf.callMatch(null, fargs, 0, ptr(failMessage), this.sc)) != 0))
                    {
                        OutBuffer buf = new OutBuffer();
                        try {
                            buf.writeByte(40);
                            argExpTypesToCBuffer(buf, exp.arguments);
                            buf.writeByte(41);
                            if (tthis != null)
                                tthis.modToBuffer(buf);
                            error(exp.loc, new BytePtr("%s `%s%s` is not callable using argument types `%s`"), p, exp.e1.toChars(), parametersTypeToChars(tf.parameterList), buf.peekChars());
                            if (failMessage.value != null)
                                errorSupplemental(exp.loc, new BytePtr("%s"), failMessage.value);
                            this.setError();
                            return ;
                        }
                        finally {
                        }
                    }
                    if (exp.f != null)
                    {
                        exp.checkPurity(this.sc, exp.f);
                        exp.checkSafety(this.sc, exp.f);
                        exp.checkNogc(this.sc, exp.f);
                        if (exp.f.checkNestedReference(this.sc, exp.loc))
                            this.setError();
                            return ;
                    }
                    else if ((((this.sc).func != null && (this.sc).intypeof != 1) && !(((this.sc).flags & 128) != 0)))
                    {
                        boolean err = false;
                        if (((!((tf.purity) != 0) && !(((this.sc).flags & 8) != 0)) && (this.sc).func.setImpure()))
                        {
                            exp.error(new BytePtr("`pure` %s `%s` cannot call impure %s `%s`"), (this.sc).func.kind(), (this.sc).func.toPrettyChars(false), p, exp.e1.toChars());
                            err = true;
                        }
                        if (((!(tf.isnogc) && (this.sc).func.setGC()) && !(((this.sc).flags & 8) != 0)))
                        {
                            exp.error(new BytePtr("`@nogc` %s `%s` cannot call non-@nogc %s `%s`"), (this.sc).func.kind(), (this.sc).func.toPrettyChars(false), p, exp.e1.toChars());
                            err = true;
                        }
                        if (((tf.trust <= TRUST.system && (this.sc).func.setUnsafe()) && !(((this.sc).flags & 8) != 0)))
                        {
                            exp.error(new BytePtr("`@safe` %s `%s` cannot call `@system` %s `%s`"), (this.sc).func.kind(), (this.sc).func.toPrettyChars(false), p, exp.e1.toChars());
                            err = true;
                        }
                        if (err)
                            this.setError();
                            return ;
                    }
                    if ((t1.ty & 0xFF) == ENUMTY.Tpointer)
                    {
                        Expression e = new PtrExp(exp.loc, exp.e1);
                        e.type = tf;
                        exp.e1 = e;
                    }
                    t1 = tf;
                }
                else if ((exp.e1.op & 0xFF) == 26)
                {
                    VarExp ve = (VarExp)exp.e1;
                    exp.f = ve.var.isFuncDeclaration();
                    assert(exp.f != null);
                    tiargs = null;
                    if (exp.f.overnext != null)
                        exp.f = resolveFuncCall(exp.loc, this.sc, exp.f, tiargs, null, exp.arguments, FuncResolveFlag.overloadOnly);
                    else
                    {
                        exp.f = exp.f.toAliasFunc();
                        TypeFunction tf = (TypeFunction)exp.f.type;
                        Ref<BytePtr> failMessage = ref(null);
                        Slice<Expression> fargs = exp.arguments != null ? (exp.arguments).opSlice() : new Slice<Expression>().copy();
                        if (!((tf.callMatch(null, fargs, 0, ptr(failMessage), this.sc)) != 0))
                        {
                            OutBuffer buf = new OutBuffer();
                            try {
                                buf.writeByte(40);
                                argExpTypesToCBuffer(buf, exp.arguments);
                                buf.writeByte(41);
                                error(exp.loc, new BytePtr("%s `%s%s` is not callable using argument types `%s`"), exp.f.kind(), exp.f.toPrettyChars(false), parametersTypeToChars(tf.parameterList), buf.peekChars());
                                if (failMessage.value != null)
                                    errorSupplemental(exp.loc, new BytePtr("%s"), failMessage.value);
                                exp.f = null;
                            }
                            finally {
                            }
                        }
                    }
                    if ((!(exp.f != null) || exp.f.errors))
                        this.setError();
                        return ;
                    if (exp.f.needThis())
                    {
                        if (exp.f.checkNestedReference(this.sc, exp.loc))
                            this.setError();
                            return ;
                        if (hasThis(this.sc) != null)
                        {
                            exp.e1 = new DotVarExp(exp.loc, expressionSemantic(new ThisExp(exp.loc), this.sc), ve.var, true);
                            /*goto Lagain*/throw Dispatch1.INSTANCE;
                        }
                        else if (isNeedThisScope(this.sc, exp.f))
                        {
                            exp.error(new BytePtr("need `this` for `%s` of type `%s`"), exp.f.toChars(), exp.f.type.toChars());
                            this.setError();
                            return ;
                        }
                    }
                    checkFunctionAttributes(exp, this.sc, exp.f);
                    checkAccess(exp.loc, this.sc, null, exp.f);
                    if (exp.f.checkNestedReference(this.sc, exp.loc))
                        this.setError();
                        return ;
                    ethis = null;
                    tthis = null;
                    if (ve.hasOverloads)
                    {
                        exp.e1 = new VarExp(ve.loc, exp.f, false);
                        exp.e1.type = exp.f.type;
                    }
                    t1 = exp.f.type;
                }
                assert((t1.ty & 0xFF) == ENUMTY.Tfunction);
                Ref<Expression> argprefix = ref(null);
                if (exp.arguments == null)
                    exp.arguments = new DArray<Expression>();
                if (functionParameters(exp.loc, this.sc, (TypeFunction)t1, ethis, tthis, exp.arguments, exp.f, exp.type, ptr(argprefix)))
                    this.setError();
                    return ;
                if (!(exp.type != null))
                {
                    exp.e1 = e1org;
                    exp.error(new BytePtr("forward reference to inferred return type of function call `%s`"), exp.toChars());
                    this.setError();
                    return ;
                }
                if ((exp.f != null && exp.f.tintro != null))
                {
                    Type t = exp.type;
                    IntRef offset = ref(0);
                    TypeFunction tf = (TypeFunction)exp.f.tintro;
                    if ((tf.next.isBaseOf(t, ptr(offset)) && (offset.value) != 0))
                    {
                        exp.type = tf.next;
                        this.result = Expression.combine(argprefix.value, exp.castTo(this.sc, t));
                        return ;
                    }
                }
                if ((((exp.f != null && exp.f.isFuncLiteralDeclaration() != null) && (this.sc).func != null) && !(((this.sc).intypeof) != 0)))
                {
                    exp.f.tookAddressOf = 0;
                }
                this.result = Expression.combine(argprefix.value, (Expression)exp);
                if (isSuper)
                {
                    AggregateDeclaration ad = (this.sc).func != null ? (this.sc).func.isThis() : null;
                    ClassDeclaration cd = ad != null ? ad.isClassDeclaration() : null;
                    if ((((cd != null && cd.classKind == ClassKind.cpp) && exp.f != null) && !(exp.f.fbody != null)))
                    {
                        Loc loc = exp.loc.copy();
                        DotIdExp vptr = new DotIdExp(loc, new ThisExp(loc), Id.__vptr);
                        VarDeclaration vptrTmpDecl = copyToTemp(0L, new BytePtr("__vptrTmp"), vptr);
                        DeclarationExp declareVptrTmp = new DeclarationExp(loc, vptrTmpDecl);
                        VarDeclaration superTmpDecl = copyToTemp(0L, new BytePtr("__superTmp"), this.result);
                        DeclarationExp declareSuperTmp = new DeclarationExp(loc, superTmpDecl);
                        CommaExp declareTmps = new CommaExp(loc, declareVptrTmp, declareSuperTmp, true);
                        AssignExp restoreVptr = new AssignExp(loc, vptr.syntaxCopy(), new VarExp(loc, vptrTmpDecl, true));
                        Expression e = new CommaExp(loc, declareTmps, new CommaExp(loc, restoreVptr, new VarExp(loc, superTmpDecl, true), true), true);
                        this.result = expressionSemantic(e, this.sc);
                    }
                }
                if ((((exp.f != null && exp.f.isThis2) && !(((this.sc).intypeof) != 0)) && (this.sc).func != null))
                {
                    {
                        AggregateDeclaration ad2 = exp.f.isMember2();
                        if (ad2 != null)
                        {
                            Expression te = expressionSemantic(new ThisExp(exp.loc), this.sc);
                            if ((te.op & 0xFF) != 127)
                                te = getRightThis(exp.loc, this.sc, ad2, te, exp.f, 0);
                            if ((te.op & 0xFF) == 127)
                            {
                                exp.error(new BytePtr("need `this` of type `%s` to call function `%s`"), ad2.toChars(), exp.f.toChars());
                                this.setError();
                                return ;
                            }
                        }
                    }
                    VarDeclaration vthis2 = makeThis2Argument(exp.loc, this.sc, exp.f);
                    exp.vthis2 = vthis2;
                    Expression de = new DeclarationExp(exp.loc, vthis2);
                    this.result = Expression.combine(de, this.result);
                    this.result = expressionSemantic(this.result, this.sc);
                }
                break;
            } catch(Dispatch1 __d){}
        }

        public  void visit(DeclarationExp e) {
            if (e.type != null)
            {
                this.result = e;
                return ;
            }
            int olderrors = global.errors;
            Dsymbol s = e.declaration;
            for (; (1) != 0;){
                AttribDeclaration ad = s.isAttribDeclaration();
                if (ad != null)
                {
                    if ((ad.decl != null && (ad.decl).length == 1))
                    {
                        s = (ad.decl).get(0);
                        continue;
                    }
                }
                break;
            }
            VarDeclaration v = s.isVarDeclaration();
            if (v != null)
            {
                dsymbolSemantic(e.declaration, this.sc);
                s.parent = (this.sc).parent;
            }
            if (s.ident != null)
            {
                if (!((this.sc).insert(s) != null))
                {
                    e.error(new BytePtr("declaration `%s` is already defined"), s.toPrettyChars(false));
                    this.setError();
                    return ;
                }
                else if ((this.sc).func != null)
                {
                    if (((((s.isFuncDeclaration() != null || s.isAggregateDeclaration() != null) || s.isEnumDeclaration() != null) || (v != null && v.isDataseg())) && !((this.sc).func.localsymtab.insert(s) != null)))
                    {
                        s.parent = (this.sc).parent;
                        Dsymbol originalSymbol = (this.sc).func.localsymtab.lookup(s.ident);
                        assert(originalSymbol != null);
                        e.error(new BytePtr("declaration `%s` is already defined in another scope in `%s` at line `%d`"), s.toPrettyChars(false), (this.sc).func.toChars(), originalSymbol.loc.linnum);
                        this.setError();
                        return ;
                    }
                    else
                    {
                        {
                            Scope scx = (this.sc).enclosing;
                            for (; (scx != null && pequals((scx).func, (this.sc).func));scx = (scx).enclosing){
                                Dsymbol s2 = null;
                                if (((((scx).scopesym != null && (scx).scopesym.symtab != null) && (s2 = (scx).scopesym.symtab.lookup(s.ident)) != null) && !pequals(s, s2)))
                                {
                                    Declaration decl = s2.isDeclaration();
                                    if ((!(decl != null) || !((decl.storage_class & 2251799813685248L) != 0)))
                                    {
                                        e.error(new BytePtr("%s `%s` is shadowing %s `%s`"), s.kind(), s.ident.toChars(), s2.kind(), s2.toPrettyChars(false));
                                        this.setError();
                                        return ;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (!(s.isVarDeclaration() != null))
            {
                Scope sc2 = this.sc;
                if (((sc2).stc & 4398147174400L) != 0)
                    sc2 = (this.sc).push();
                (sc2).stc &= -4398147174401L;
                dsymbolSemantic(e.declaration, sc2);
                if (sc2 != this.sc)
                    (sc2).pop();
                s.parent = (this.sc).parent;
            }
            if (global.errors == olderrors)
            {
                semantic2(e.declaration, this.sc);
                if (global.errors == olderrors)
                {
                    semantic3(e.declaration, this.sc);
                }
            }
            e.type = Type.tvoid;
            this.result = e;
        }

        public  void visit(TypeidExp exp) {
            Ref<Type> ta = ref(isType(exp.obj));
            Ref<Expression> ea = ref(isExpression(exp.obj));
            Ref<Dsymbol> sa = ref(isDsymbol(exp.obj));
            if (ta.value != null)
            {
                resolve(ta.value, exp.loc, this.sc, ptr(ea), ptr(ta), ptr(sa), true);
            }
            if (ea.value != null)
            {
                {
                    Dsymbol sym = getDsymbol(ea.value);
                    if (sym != null)
                        ea.value = symbolToExp(sym, exp.loc, this.sc, false);
                    else
                        ea.value = expressionSemantic(ea.value, this.sc);
                }
                ea.value = resolveProperties(this.sc, ea.value);
                ta.value = ea.value.type;
                if ((ea.value.op & 0xFF) == 20)
                    ea.value = null;
            }
            if (!(ta.value != null))
            {
                exp.error(new BytePtr("no type for `typeid(%s)`"), ea.value != null ? ea.value.toChars() : sa.value != null ? sa.value.toChars() : new BytePtr(""));
                this.setError();
                return ;
            }
            if (global.params.vcomplex)
                ta.value.checkComplexTransition(exp.loc, this.sc);
            Expression e = null;
            Type tb = ta.value.toBasetype();
            if ((ea.value != null && (tb.ty & 0xFF) == ENUMTY.Tclass))
            {
                if (tb.toDsymbol(this.sc).isClassDeclaration().classKind == ClassKind.cpp)
                {
                    error(exp.loc, new BytePtr("Runtime type information is not supported for `extern(C++)` classes"));
                    e = new ErrorExp();
                }
                else if (!(Type.typeinfoclass != null))
                {
                    error(exp.loc, new BytePtr("`object.TypeInfo_Class` could not be found, but is implicitly used"));
                    e = new ErrorExp();
                }
                else
                {
                    ea.value = expressionSemantic(ea.value, this.sc);
                    e = new TypeidExp(ea.value.loc, ea.value);
                    e.type = Type.typeinfoclass.type;
                }
            }
            else if ((ta.value.ty & 0xFF) == ENUMTY.Terror)
            {
                e = new ErrorExp();
            }
            else
            {
                e = new TypeidExp(exp.loc, ta.value);
                e.type = getTypeInfoType(exp.loc, ta.value, this.sc);
                semanticTypeInfo(this.sc, ta.value);
                if (ea.value != null)
                {
                    e = new CommaExp(exp.loc, ea.value, e, true);
                    e = expressionSemantic(e, this.sc);
                }
            }
            this.result = e;
        }

        public  void visit(TraitsExp e) {
            this.result = semanticTraits(e, this.sc);
        }

        public  void visit(HaltExp e) {
            e.type = Type.tvoid;
            this.result = e;
        }

        public  void visit(IsExp e) {
            if ((e.id != null && !(((this.sc).flags & 4) != 0)))
            {
                e.error(new BytePtr("can only declare type aliases within `static if` conditionals or `static assert`s"));
                this.setError();
                return ;
            }
            Type tded = null;
            try {
                try {
                    if (((e.tok2 & 0xFF) == 180 || (e.tok2 & 0xFF) == 34))
                    {
                        Dsymbol sym = e.targ.toDsymbol(this.sc);
                        if (sym == null)
                            /*goto Lno*/throw Dispatch1.INSTANCE;
                        dmodule.Package p = resolveIsPackage(sym);
                        if (p == null)
                            /*goto Lno*/throw Dispatch1.INSTANCE;
                        if (((e.tok2 & 0xFF) == 180 && p.isModule() != null))
                            /*goto Lno*/throw Dispatch1.INSTANCE;
                        else if (((e.tok2 & 0xFF) == 34 && !((p.isModule() != null || p.isPackageMod() != null))))
                            /*goto Lno*/throw Dispatch1.INSTANCE;
                        tded = e.targ;
                        /*goto Lyes*/throw Dispatch0.INSTANCE;
                    }
                    {
                        Scope sc2 = (this.sc).copy();
                        (sc2).tinst = null;
                        (sc2).minst = null;
                        (sc2).flags |= 65536;
                        Type t = e.targ.trySemantic(e.loc, sc2);
                        (sc2).pop();
                        if (!(t != null))
                            /*goto Lno*/throw Dispatch1.INSTANCE;
                        e.targ = t;
                    }
                    if ((e.tok2 & 0xFF) != 0)
                    {
                        {
                            int __dispatch5 = 0;
                            dispatched_5:
                            do {
                                switch (__dispatch5 != 0 ? __dispatch5 : (e.tok2 & 0xFF))
                                {
                                    case 152:
                                        if ((e.targ.ty & 0xFF) != ENUMTY.Tstruct)
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        if (((TypeStruct)e.targ).sym.isUnionDeclaration() != null)
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = e.targ;
                                        break;
                                    case 155:
                                        if ((e.targ.ty & 0xFF) != ENUMTY.Tstruct)
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        if (!(((TypeStruct)e.targ).sym.isUnionDeclaration() != null))
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = e.targ;
                                        break;
                                    case 153:
                                        if ((e.targ.ty & 0xFF) != ENUMTY.Tclass)
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        if (((TypeClass)e.targ).sym.isInterfaceDeclaration() != null)
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = e.targ;
                                        break;
                                    case 154:
                                        if ((e.targ.ty & 0xFF) != ENUMTY.Tclass)
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        if (!(((TypeClass)e.targ).sym.isInterfaceDeclaration() != null))
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = e.targ;
                                        break;
                                    case 171:
                                        if (!(e.targ.isConst()))
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = e.targ;
                                        break;
                                    case 182:
                                        if (!(e.targ.isImmutable()))
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = e.targ;
                                        break;
                                    case 224:
                                        if (!(e.targ.isShared()))
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = e.targ;
                                        break;
                                    case 177:
                                        if (!(e.targ.isWild()))
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = e.targ;
                                        break;
                                    case 124:
                                        if ((e.targ.ty & 0xFF) != ENUMTY.Tclass)
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        else
                                        {
                                            ClassDeclaration cd = ((TypeClass)e.targ).sym;
                                            DArray<Parameter> args = new DArray<Parameter>();
                                            (args).reserve((cd.baseclasses).length);
                                            if (cd.semanticRun < PASS.semanticdone)
                                                dsymbolSemantic(cd, null);
                                            {
                                                int i = 0;
                                                for (; i < (cd.baseclasses).length;i++){
                                                    BaseClass b = (cd.baseclasses).get(i);
                                                    (args).push(new Parameter(2048L, (b).type, null, null, null));
                                                }
                                            }
                                            tded = new TypeTuple(args);
                                        }
                                        break;
                                    case 156:
                                        if ((e.targ.ty & 0xFF) != ENUMTY.Tenum)
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        if (e.id != null)
                                            tded = ((TypeEnum)e.targ).sym.getMemtype(e.loc);
                                        else
                                            tded = e.targ;
                                        if ((tded.ty & 0xFF) == ENUMTY.Terror)
                                            this.setError();
                                            return ;
                                        break;
                                    case 160:
                                        if ((e.targ.ty & 0xFF) != ENUMTY.Tdelegate)
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = ((TypeDelegate)e.targ).next;
                                        break;
                                    case 161:
                                    case 212:
                                        if ((e.targ.ty & 0xFF) != ENUMTY.Tfunction)
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = e.targ;
                                        assert((tded.ty & 0xFF) == ENUMTY.Tfunction);
                                        TypeFunction tdedf = tded.isTypeFunction();
                                        int dim = tdedf.parameterList.length();
                                        DArray<Parameter> args_1 = new DArray<Parameter>();
                                        (args_1).reserve(dim);
                                        {
                                            int i_1 = 0;
                                            for (; i_1 < dim;i_1++){
                                                Parameter arg = tdedf.parameterList.get(i_1);
                                                assert((arg != null && arg.type != null));
                                                if ((((e.tok2 & 0xFF) == 212 && arg.defaultArg != null) && (arg.defaultArg.op & 0xFF) == 127))
                                                    this.setError();
                                                    return ;
                                                (args_1).push(new Parameter(arg.storageClass, arg.type, (e.tok2 & 0xFF) == 212 ? arg.ident : null, (e.tok2 & 0xFF) == 212 ? arg.defaultArg : null, arg.userAttribDecl));
                                            }
                                        }
                                        tded = new TypeTuple(args_1);
                                        break;
                                    case 195:
                                        if ((e.targ.ty & 0xFF) == ENUMTY.Tfunction)
                                            tded = ((TypeFunction)e.targ).next;
                                        else if ((e.targ.ty & 0xFF) == ENUMTY.Tdelegate)
                                        {
                                            tded = ((TypeDelegate)e.targ).next;
                                            tded = ((TypeFunction)tded).next;
                                        }
                                        else if (((e.targ.ty & 0xFF) == ENUMTY.Tpointer && (((TypePointer)e.targ).next.ty & 0xFF) == ENUMTY.Tfunction))
                                        {
                                            tded = ((TypePointer)e.targ).next;
                                            tded = ((TypeFunction)tded).next;
                                        }
                                        else
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        break;
                                    case 209:
                                        tded = target.toArgTypes(e.targ);
                                        if (!(tded != null))
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        break;
                                    case 229:
                                        if ((e.targ.ty & 0xFF) != ENUMTY.Tvector)
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = ((TypeVector)e.targ).basetype;
                                        break;
                                    default:
                                    throw new AssertionError("Unreachable code!");
                                }
                            } while(__dispatch5 != 0);
                        }
                        if (tded != null)
                            /*goto Lyes*/throw Dispatch0.INSTANCE;
                        /*goto Lno*/throw Dispatch1.INSTANCE;
                    }
                    else if (((e.tspec != null && !(e.id != null)) && !((e.parameters != null && ((e.parameters).length) != 0))))
                    {
                        e.tspec = typeSemantic(e.tspec, e.loc, this.sc);
                        if ((e.tok & 0xFF) == 7)
                        {
                            if ((e.targ.implicitConvTo(e.tspec)) != 0)
                                /*goto Lyes*/throw Dispatch0.INSTANCE;
                            else
                                /*goto Lno*/throw Dispatch1.INSTANCE;
                        }
                        else
                        {
                            if (e.targ.equals(e.tspec))
                                /*goto Lyes*/throw Dispatch0.INSTANCE;
                            else
                                /*goto Lno*/throw Dispatch1.INSTANCE;
                        }
                    }
                    else if (e.tspec != null)
                    {
                        Identifier tid = e.id != null ? e.id : Identifier.generateId(new BytePtr("__isexp_id"));
                        (e.parameters).insert(0, new TemplateTypeParameter(e.loc, tid, null, null));
                        DArray<RootObject> dedtypes = dedtypes = new DArray<RootObject>((e.parameters).length);
                        try {
                            dedtypes.zero();
                            int m = deduceType(e.targ, this.sc, e.tspec, e.parameters, dedtypes, null, 0, (e.tok & 0xFF) == 58);
                            if ((m <= MATCH.nomatch || (m != MATCH.exact && (e.tok & 0xFF) == 58)))
                            {
                                /*goto Lno*/throw Dispatch1.INSTANCE;
                            }
                            else
                            {
                                tded = (Type)dedtypes.get(0);
                                if (!(tded != null))
                                    tded = e.targ;
                                DArray<RootObject> tiargs = tiargs = new DArray<RootObject>(1);
                                try {
                                    tiargs.set(0, e.targ);
                                    {
                                        int i = 1;
                                    L_outer7:
                                        for (; i < (e.parameters).length;i++){
                                            TemplateParameter tp = (e.parameters).get(i);
                                            Ref<Declaration> s = ref(null);
                                            m = tp.matchArg(e.loc, this.sc, tiargs, i, e.parameters, dedtypes, ptr(s));
                                            if (m <= MATCH.nomatch)
                                                /*goto Lno*/throw Dispatch1.INSTANCE;
                                            dsymbolSemantic(s.value, this.sc);
                                            if (!((this.sc).insert(s.value) != null))
                                                e.error(new BytePtr("declaration `%s` is already defined"), s.value.toChars());
                                            unSpeculative(this.sc, s.value);
                                        }
                                    }
                                    /*goto Lyes*/throw Dispatch0.INSTANCE;
                                }
                                finally {
                                }
                            }
                        }
                        finally {
                        }
                    }
                    else if (e.id != null)
                    {
                        tded = e.targ;
                        /*goto Lyes*/throw Dispatch0.INSTANCE;
                    }
                }
                catch(Dispatch0 __d){}
            /*Lyes:*/
                if (e.id != null)
                {
                    Dsymbol s = null;
                    Tuple tup = isTuple(tded);
                    if (tup != null)
                        s = new TupleDeclaration(e.loc, e.id, tup.objects);
                    else
                        s = new AliasDeclaration(e.loc, e.id, tded);
                    dsymbolSemantic(s, this.sc);
                    if ((!(tup != null) && !((this.sc).insert(s) != null)))
                        e.error(new BytePtr("declaration `%s` is already defined"), s.toChars());
                    unSpeculative(this.sc, s);
                }
                this.result = new IntegerExp(e.loc, 1L, Type.tbool);
                return ;
            }
            catch(Dispatch1 __d){}
        /*Lno:*/
            this.result = new IntegerExp(e.loc, 0L, Type.tbool);
        }

        public  void visit(BinAssignExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            if ((exp.e1.op & 0xFF) == 32)
            {
                e = rewriteOpAssign(exp);
                e = expressionSemantic(e, this.sc);
                this.result = e;
                return ;
            }
            if ((((exp.e1.op & 0xFF) == 31 || (exp.e1.type.ty & 0xFF) == ENUMTY.Tarray) || (exp.e1.type.ty & 0xFF) == ENUMTY.Tsarray))
            {
                if (checkNonAssignmentArrayOp(exp.e1, false))
                    this.setError();
                    return ;
                if ((exp.e1.op & 0xFF) == 31)
                    ((SliceExp)exp.e1).arrayop = true;
                if ((exp.e2.implicitConvTo(exp.e1.type.nextOf())) != 0)
                {
                    exp.e2 = exp.e2.castTo(this.sc, exp.e1.type.nextOf());
                }
                else {
                    Expression ex = typeCombine(exp, this.sc);
                    if (ex != null)
                    {
                        this.result = ex;
                        return ;
                    }
                }
                exp.type = exp.e1.type;
                this.result = arrayOp(exp, this.sc);
                return ;
            }
            exp.e1 = expressionSemantic(exp.e1, this.sc);
            exp.e1 = exp.e1.optimize(0, false);
            exp.e1 = exp.e1.modifiableLvalue(this.sc, exp.e1);
            exp.type = exp.e1.type;
            {
                AggregateDeclaration ad = isAggregate(exp.e1.type);
                if (ad != null)
                {
                    {
                        Dsymbol s = search_function(ad, Id.opOpAssign);
                        if (s != null)
                        {
                            error(exp.loc, new BytePtr("none of the `opOpAssign` overloads of `%s` are callable for `%s` of type `%s`"), ad.toChars(), exp.e1.toChars(), exp.e1.type.toChars());
                            this.setError();
                            return ;
                        }
                    }
                }
            }
            if ((exp.e1.checkScalar() || exp.e1.checkReadModifyWrite(exp.op, exp.e2)))
                this.setError();
                return ;
            int arith = ((((((((exp.op & 0xFF) == 76 || (exp.op & 0xFF) == 77) || (exp.op & 0xFF) == 81) || (exp.op & 0xFF) == 82) || (exp.op & 0xFF) == 83) || (exp.op & 0xFF) == 227)) ? 1 : 0);
            int bitwise = (((((exp.op & 0xFF) == 87 || (exp.op & 0xFF) == 88) || (exp.op & 0xFF) == 89)) ? 1 : 0);
            int shift = (((((exp.op & 0xFF) == 66 || (exp.op & 0xFF) == 67) || (exp.op & 0xFF) == 69)) ? 1 : 0);
            if (((bitwise) != 0 && (exp.type.toBasetype().ty & 0xFF) == ENUMTY.Tbool))
                exp.e2 = exp.e2.implicitCastTo(this.sc, exp.type);
            else if (exp.checkNoBool())
                this.setError();
                return ;
            if (((((exp.op & 0xFF) == 76 || (exp.op & 0xFF) == 77) && (exp.e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tpointer) && exp.e2.type.toBasetype().isintegral()))
            {
                this.result = scaleFactor(exp, this.sc);
                return ;
            }
            {
                Expression ex = typeCombine(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            if (((arith) != 0 && exp.checkArithmeticBin()))
                this.setError();
                return ;
            if ((((bitwise) != 0 || (shift) != 0) && exp.checkIntegralBin()))
                this.setError();
                return ;
            if ((shift) != 0)
            {
                if ((exp.e2.type.toBasetype().ty & 0xFF) != ENUMTY.Tvector)
                    exp.e2 = exp.e2.castTo(this.sc, Type.tshiftcnt);
            }
            if (!(target.isVectorOpSupported(exp.type.toBasetype(), exp.op, exp.e2.type.toBasetype())))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            if (((exp.e1.op & 0xFF) == 127 || (exp.e2.op & 0xFF) == 127))
                this.setError();
                return ;
            e = exp.checkOpAssignTypes(this.sc);
            if ((e.op & 0xFF) == 127)
            {
                this.result = e;
                return ;
            }
            assert(((e.op & 0xFF) == 90 || pequals(e, exp)));
            this.result = ((BinExp)e).reorderSettingAAElem(this.sc);
        }

        public  Expression compileIt(CompileExp exp) {
            OutBuffer buf = new OutBuffer();
            try {
                if (expressionsToString(buf, this.sc, exp.exps))
                    return null;
                int errors = global.errors;
                int len = buf.offset;
                ByteSlice str = buf.extractChars().slice(0,len).copy();
                StderrDiagnosticReporter diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
                try {
                    ParserASTCodegen p = new ParserASTCodegen(exp.loc, (this.sc)._module, str, false, diagnosticReporter);
                    try {
                        p.nextToken();
                        Expression e = p.parseExpression();
                        if (p.errors())
                        {
                            assert(global.errors != errors);
                            return null;
                        }
                        if ((p.token.value & 0xFF) != 11)
                        {
                            exp.error(new BytePtr("incomplete mixin expression `%s`"), toBytePtr(str));
                            return null;
                        }
                        return e;
                    }
                    finally {
                    }
                }
                finally {
                }
            }
            finally {
            }
        }

        public  void visit(CompileExp exp) {
            Expression e = this.compileIt(exp);
            if (!(e != null))
                this.setError();
                return ;
            this.result = expressionSemantic(e, this.sc);
        }

        public  void visit(ImportExp e) {
            StringExp se = semanticString(this.sc, e.e1, new BytePtr("file name argument"));
            if (!(se != null))
                this.setError();
                return ;
            se = se.toUTF8(this.sc);
            BytePtr namez = pcopy(toBytePtr(se.toStringz()));
            if (global.filePath == null)
            {
                e.error(new BytePtr("need `-J` switch to import text file `%s`"), namez);
                this.setError();
                return ;
            }
            BytePtr name = pcopy(FileName.safeSearchPath(global.filePath, namez));
            if (name == null)
            {
                e.error(new BytePtr("file `%s` cannot be found or not in a path specified with `-J`"), se.toChars());
                this.setError();
                return ;
            }
            (this.sc)._module.contentImportedFiles.push(name);
            if (global.params.verbose)
                message(new BytePtr("file      %.*s\u0009(%s)"), se.len, se.string, name);
            if (global.params.moduleDeps != null)
            {
                OutBuffer ob = global.params.moduleDeps;
                dmodule.Module imod = (this.sc).instantiatingModule();
                if (!(global.params.moduleDepsFile.getLength() != 0))
                    (ob).writestring(new ByteSlice("depsFile "));
                (ob).writestring(imod.toPrettyChars(false));
                (ob).writestring(new ByteSlice(" ("));
                escapePath(ob, imod.srcfile.toChars());
                (ob).writestring(new ByteSlice(") : "));
                if (global.params.moduleDepsFile.getLength() != 0)
                    (ob).writestring(new ByteSlice("string : "));
                (ob).write(se.string, se.len);
                (ob).writestring(new ByteSlice(" ("));
                escapePath(ob, name);
                (ob).writestring(new ByteSlice(")"));
                (ob).writenl();
            }
            {
                File.ReadResult readResult = File.read(name).copy();
                try {
                    if (!(readResult.success))
                    {
                        e.error(new BytePtr("cannot read file `%s`"), name);
                        this.setError();
                        return ;
                    }
                    else
                    {
                        ByteSlice data = readResult.extractData().copy();
                        se = new StringExp(e.loc, toBytePtr(data), data.getLength());
                    }
                }
                finally {
                }
            }
            this.result = expressionSemantic(se, this.sc);
        }

        public  void visit(AssertExp exp) {
            BytePtr assertExpMsg = pcopy(exp.msg != null ? null : exp.toChars());
            {
                Expression ex = unaSemantic(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            exp.e1 = resolveProperties(this.sc, exp.e1);
            exp.e1 = exp.e1.optimize(0, false);
            exp.e1 = exp.e1.toBoolean(this.sc);
            if ((!(exp.msg != null) && (global.params.checkAction & 0xFF) == 3))
            {
                byte tok = exp.e1.op;
                boolean isEqualsCallExpression = false;
                if ((tok & 0xFF) == 18)
                {
                    CallExp callExp = (CallExp)exp.e1;
                    Identifier callExpIdent = callExp.f.ident;
                    isEqualsCallExpression = (pequals(callExpIdent, Id.__equals) || pequals(callExpIdent, Id.eq));
                }
                if (((((((((((tok & 0xFF) == 58 || (tok & 0xFF) == 59) || (tok & 0xFF) == 54) || (tok & 0xFF) == 55) || (tok & 0xFF) == 56) || (tok & 0xFF) == 57) || (tok & 0xFF) == 60) || (tok & 0xFF) == 61) || (tok & 0xFF) == 175) || isEqualsCallExpression))
                {
                    if (!(verifyHookExist(exp.loc, this.sc, Id._d_assert_fail, new ByteSlice("generating assert messages"), Id.object)))
                        this.setError();
                        return ;
                    DArray<Expression> es = new DArray<Expression>(2);
                    DArray<RootObject> tiargs = new DArray<RootObject>(3);
                    Loc loc = exp.e1.loc.copy();
                    if (isEqualsCallExpression)
                    {
                        CallExp callExp = (CallExp)exp.e1;
                        DArray<Expression> args = callExp.arguments;
                        Expression comp = new StringExp(loc, toBytePtr(toBytePtr(expressionsem.visitcompMsg)));
                        comp = expressionSemantic(comp, this.sc);
                        tiargs.set(0, comp);
                        tiargs.set(1, (args).get(0).type);
                        tiargs.set(2, (args).get(1).type);
                        es.set(0, (args).get(0));
                        es.set(1, (args).get(1));
                    }
                    else
                    {
                        EqualExp binExp = (EqualExp)exp.e1;
                        Expression comp = new StringExp(loc, Token.toChars(exp.e1.op));
                        comp = expressionSemantic(comp, this.sc);
                        tiargs.set(0, comp);
                        tiargs.set(1, binExp.e1.type);
                        tiargs.set(2, binExp.e2.type);
                        es.set(0, binExp.e1);
                        es.set(1, binExp.e2);
                    }
                    Expression __assertFail = new IdentifierExp(exp.loc, Id.empty);
                    DotIdExp assertFail = new DotIdExp(loc, __assertFail, Id.object);
                    DotTemplateInstanceExp dt = new DotTemplateInstanceExp(loc, assertFail, Id._d_assert_fail, tiargs);
                    CallExp ec = CallExp.create(Loc.initial, (Expression)dt, es);
                    exp.msg = ec;
                }
                else
                {
                    OutBuffer buf = new OutBuffer();
                    try {
                        buf.printf(new BytePtr("%s failed"), assertExpMsg);
                        exp.msg = new StringExp(Loc.initial, buf.extractChars());
                    }
                    finally {
                    }
                }
            }
            if (exp.msg != null)
            {
                exp.msg = expressionSemantic(exp.msg, this.sc);
                exp.msg = resolveProperties(this.sc, exp.msg);
                exp.msg = exp.msg.implicitCastTo(this.sc, Type.tchar.constOf().arrayOf());
                exp.msg = exp.msg.optimize(0, false);
            }
            if ((exp.e1.op & 0xFF) == 127)
            {
                this.result = exp.e1;
                return ;
            }
            if ((exp.msg != null && (exp.msg.op & 0xFF) == 127))
            {
                this.result = exp.msg;
                return ;
            }
            boolean f1 = checkNonAssignmentArrayOp(exp.e1, false);
            boolean f2 = (exp.msg != null && checkNonAssignmentArrayOp(exp.msg, false));
            if ((f1 || f2))
                this.setError();
                return ;
            if (exp.e1.isBool(false))
            {
                FuncDeclaration fd = (this.sc).parent.isFuncDeclaration();
                if (fd != null)
                    fd.hasReturnExp |= 4;
                (this.sc).ctorflow.orCSX(CSX.halt);
                if ((global.params.useAssert & 0xFF) == 1)
                {
                    Expression e = new HaltExp(exp.loc);
                    e = expressionSemantic(e, this.sc);
                    this.result = e;
                    return ;
                }
            }
            exp.type = Type.tvoid;
            this.result = exp;
        }

        public  void visit(DotIdExp exp) {
            Expression e = semanticY(exp, this.sc, 1);
            if ((e != null && isDotOpDispatch(e)))
            {
                int errors = global.startGagging();
                e = resolvePropertiesX(this.sc, e, null);
                if (global.endGagging(errors))
                    e = null;
                else
                {
                    this.result = e;
                    return ;
                }
            }
            if (!(e != null))
            {
                e = resolveUFCSProperties(this.sc, exp, null);
            }
            this.result = e;
        }

        public  void visit(DotTemplateExp e) {
            {
                Expression ex = unaSemantic(e, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            this.result = e;
        }

        public  void visit(DotVarExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            exp.var = exp.var.toAlias().isDeclaration();
            exp.e1 = expressionSemantic(exp.e1, this.sc);
            {
                TupleDeclaration tup = exp.var.isTupleDeclaration();
                if (tup != null)
                {
                    Ref<Expression> e0 = ref(null);
                    Expression ev = (this.sc).func != null ? extractSideEffect(this.sc, new BytePtr("__tup"), e0, exp.e1, false) : exp.e1;
                    DArray<Expression> exps = new DArray<Expression>();
                    (exps).reserve((tup.objects).length);
                    {
                        int i = 0;
                        for (; i < (tup.objects).length;i++){
                            RootObject o = (tup.objects).get(i);
                            Expression e = null;
                            if (o.dyncast() == DYNCAST.expression)
                            {
                                e = (Expression)o;
                                if ((e.op & 0xFF) == 41)
                                {
                                    Dsymbol s = ((DsymbolExp)e).s;
                                    e = new DotVarExp(exp.loc, ev, s.isDeclaration(), true);
                                }
                            }
                            else if (o.dyncast() == DYNCAST.dsymbol)
                            {
                                e = new DsymbolExp(exp.loc, (Dsymbol)o, true);
                            }
                            else if (o.dyncast() == DYNCAST.type)
                            {
                                e = new TypeExp(exp.loc, (Type)o);
                            }
                            else
                            {
                                exp.error(new BytePtr("`%s` is not an expression"), o.toChars());
                                this.setError();
                                return ;
                            }
                            (exps).push(e);
                        }
                    }
                    Expression e = new TupleExp(exp.loc, e0.value, exps);
                    e = expressionSemantic(e, this.sc);
                    this.result = e;
                    return ;
                }
            }
            exp.e1 = exp.e1.addDtorHook(this.sc);
            Type t1 = exp.e1.type;
            {
                FuncDeclaration fd = exp.var.isFuncDeclaration();
                if (fd != null)
                {
                    if (!(fd.functionSemantic()))
                        this.setError();
                        return ;
                    if (((fd.isNested() && !(fd.isThis() != null)) || fd.isFuncLiteralDeclaration() != null))
                    {
                        Expression e = symbolToExp(fd, exp.loc, this.sc, false);
                        this.result = Expression.combine(exp.e1, e);
                        return ;
                    }
                    exp.type = fd.type;
                    assert(exp.type != null);
                }
                else {
                    OverDeclaration od = exp.var.isOverDeclaration();
                    if (od != null)
                    {
                        exp.type = Type.tvoid;
                    }
                    else
                    {
                        exp.type = exp.var.type;
                        if ((!(exp.type != null) && (global.errors) != 0))
                            this.setError();
                            return ;
                        assert(exp.type != null);
                        if ((t1.ty & 0xFF) == ENUMTY.Tpointer)
                            t1 = t1.nextOf();
                        exp.type = exp.type.addMod(t1.mod);
                        Dsymbol vparent = exp.var.toParent();
                        AggregateDeclaration ad = vparent != null ? vparent.isAggregateDeclaration() : null;
                        {
                            Expression e1x = getRightThis(exp.loc, this.sc, ad, exp.e1, exp.var, 1);
                            if (e1x != null)
                                exp.e1 = e1x;
                            else
                            {
                                Expression e = new VarExp(exp.loc, exp.var, true);
                                e = expressionSemantic(e, this.sc);
                                this.result = e;
                                return ;
                            }
                        }
                        checkAccess(exp.loc, this.sc, exp.e1, exp.var);
                        VarDeclaration v = exp.var.isVarDeclaration();
                        if ((v != null && (v.isDataseg() || (v.storage_class & 8388608L) != 0)))
                        {
                            Expression e = expandVar(0, v);
                            if (e != null)
                            {
                                this.result = e;
                                return ;
                            }
                        }
                        if ((v != null && (v.isDataseg() || !(v.needThis()))))
                        {
                            checkAccess(exp.loc, this.sc, exp.e1, (Declaration)v);
                            Expression e = new VarExp(exp.loc, v, true);
                            e = new CommaExp(exp.loc, exp.e1, e, true);
                            e = expressionSemantic(e, this.sc);
                            this.result = e;
                            return ;
                        }
                    }
                }
            }
            this.result = exp;
        }

        public  void visit(DotTemplateInstanceExp exp) {
            Expression e = semanticY(exp, this.sc, 1);
            if (!(e != null))
                e = resolveUFCSProperties(this.sc, exp, null);
            this.result = e;
        }

        public  void visit(DelegateExp e) {
            if (e.type != null)
            {
                this.result = e;
                return ;
            }
            e.e1 = expressionSemantic(e.e1, this.sc);
            e.type = new TypeDelegate(e.func.type);
            e.type = typeSemantic(e.type, e.loc, this.sc);
            FuncDeclaration f = e.func.toAliasFunc();
            AggregateDeclaration ad = f.toParentLocal().isAggregateDeclaration();
            if (f.needThis())
                e.e1 = getRightThis(e.loc, this.sc, ad, e.e1, f, 0);
            if ((global.params.vsafe && (e.e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tstruct))
            {
                {
                    VarDeclaration v = expToVariable(e.e1);
                    if (v != null)
                    {
                        if (!(checkAddressVar(this.sc, e, v)))
                            this.setError();
                            return ;
                    }
                }
            }
            if ((f.type.ty & 0xFF) == ENUMTY.Tfunction)
            {
                TypeFunction tf = (TypeFunction)f.type;
                if (!((MODmethodConv(e.e1.type.mod, f.type.mod)) != 0))
                {
                    OutBuffer thisBuf = new OutBuffer();
                    try {
                        OutBuffer funcBuf = new OutBuffer();
                        try {
                            MODMatchToBuffer(thisBuf, e.e1.type.mod, tf.mod);
                            MODMatchToBuffer(funcBuf, tf.mod, e.e1.type.mod);
                            e.error(new BytePtr("%smethod `%s` is not callable using a %s`%s`"), funcBuf.peekChars(), f.toPrettyChars(false), thisBuf.peekChars(), e.e1.toChars());
                            this.setError();
                            return ;
                        }
                        finally {
                        }
                    }
                    finally {
                    }
                }
            }
            if (((ad != null && ad.isClassDeclaration() != null) && !pequals(ad.type, e.e1.type)))
            {
                e.e1 = new CastExp(e.loc, e.e1, ad.type);
                e.e1 = expressionSemantic(e.e1, this.sc);
            }
            this.result = e;
            if (((f.isThis2 && !(((this.sc).intypeof) != 0)) && (this.sc).func != null))
            {
                {
                    AggregateDeclaration ad2 = f.isMember2();
                    if (ad2 != null)
                    {
                        Expression te = expressionSemantic(new ThisExp(e.loc), this.sc);
                        if ((te.op & 0xFF) != 127)
                            te = getRightThis(e.loc, this.sc, ad2, te, f, 0);
                        if ((te.op & 0xFF) == 127)
                        {
                            e.error(new BytePtr("need `this` of type `%s` to make delegate from function `%s`"), ad2.toChars(), f.toChars());
                            this.setError();
                            return ;
                        }
                    }
                }
                VarDeclaration vthis2 = makeThis2Argument(e.loc, this.sc, f);
                e.vthis2 = vthis2;
                Expression de = new DeclarationExp(e.loc, vthis2);
                this.result = Expression.combine(de, this.result);
                this.result = expressionSemantic(this.result, this.sc);
            }
        }

        public  void visit(DotTypeExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            {
                Expression e = unaSemantic(exp, this.sc);
                if (e != null)
                {
                    this.result = e;
                    return ;
                }
            }
            exp.type = exp.sym.getType().addMod(exp.e1.type.mod);
            this.result = exp;
        }

        public  void visit(AddrExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            {
                Expression ex = unaSemantic(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            int wasCond = (((exp.e1.op & 0xFF) == 100) ? 1 : 0);
            if ((exp.e1.op & 0xFF) == 29)
            {
                DotTemplateInstanceExp dti = (DotTemplateInstanceExp)exp.e1;
                TemplateInstance ti = dti.ti;
                {
                    dsymbolSemantic(ti, this.sc);
                    if ((!(ti.inst != null) || ti.errors))
                        this.setError();
                        return ;
                    Dsymbol s = ti.toAlias();
                    FuncDeclaration f = s.isFuncDeclaration();
                    if (f != null)
                    {
                        exp.e1 = new DotVarExp(exp.e1.loc, dti.e1, f, true);
                        exp.e1 = expressionSemantic(exp.e1, this.sc);
                    }
                }
            }
            else if ((exp.e1.op & 0xFF) == 203)
            {
                TemplateInstance ti = ((ScopeExp)exp.e1).sds.isTemplateInstance();
                if (ti != null)
                {
                    dsymbolSemantic(ti, this.sc);
                    if ((!(ti.inst != null) || ti.errors))
                        this.setError();
                        return ;
                    Dsymbol s = ti.toAlias();
                    FuncDeclaration f = s.isFuncDeclaration();
                    if (f != null)
                    {
                        exp.e1 = new VarExp(exp.e1.loc, f, true);
                        exp.e1 = expressionSemantic(exp.e1, this.sc);
                    }
                }
            }
            exp.e1 = exp.e1.toLvalue(this.sc, null);
            if ((exp.e1.op & 0xFF) == 127)
            {
                this.result = exp.e1;
                return ;
            }
            if (checkNonAssignmentArrayOp(exp.e1, false))
                this.setError();
                return ;
            if (!(exp.e1.type != null))
            {
                exp.error(new BytePtr("cannot take address of `%s`"), exp.e1.toChars());
                this.setError();
                return ;
            }
            Ref<Boolean> hasOverloads = ref(false);
            {
                FuncDeclaration f = isFuncAddress(exp, ptr(hasOverloads));
                if (f != null)
                {
                    if ((!(hasOverloads.value) && f.checkForwardRef(exp.loc)))
                        this.setError();
                        return ;
                }
                else if (exp.e1.type.deco == null)
                {
                    if ((exp.e1.op & 0xFF) == 26)
                    {
                        VarExp ve = (VarExp)exp.e1;
                        Declaration d = ve.var;
                        exp.error(new BytePtr("forward reference to %s `%s`"), d.kind(), d.toChars());
                    }
                    else
                        exp.error(new BytePtr("forward reference to `%s`"), exp.e1.toChars());
                    this.setError();
                    return ;
                }
            }
            exp.type = exp.e1.type.pointerTo();
            if ((exp.e1.op & 0xFF) == 27)
            {
                DotVarExp dve = (DotVarExp)exp.e1;
                FuncDeclaration f = dve.var.isFuncDeclaration();
                if (f != null)
                {
                    f = f.toAliasFunc();
                    if (!(dve.hasOverloads))
                        f.tookAddressOf++;
                    Expression e = null;
                    if (f.needThis())
                        e = new DelegateExp(exp.loc, dve.e1, f, dve.hasOverloads, null);
                    else
                        e = new CommaExp(exp.loc, dve.e1, new AddrExp(exp.loc, new VarExp(exp.loc, f, dve.hasOverloads)), true);
                    e = expressionSemantic(e, this.sc);
                    this.result = e;
                    return ;
                }
                if (checkUnsafeAccess(this.sc, dve, !(exp.type.isMutable()), true))
                    this.setError();
                    return ;
                if (global.params.vsafe)
                {
                    {
                        VarDeclaration v = expToVariable(dve.e1);
                        if (v != null)
                        {
                            if (!(checkAddressVar(this.sc, exp, v)))
                                this.setError();
                                return ;
                        }
                    }
                }
            }
            else if ((exp.e1.op & 0xFF) == 26)
            {
                VarExp ve = (VarExp)exp.e1;
                VarDeclaration v = ve.var.isVarDeclaration();
                if (v != null)
                {
                    if (!(checkAddressVar(this.sc, exp, v)))
                        this.setError();
                        return ;
                    ve.checkPurity(this.sc, v);
                }
                FuncDeclaration f = ve.var.isFuncDeclaration();
                if (f != null)
                {
                    if ((!(ve.hasOverloads) || (f.isNested() && !(f.needThis()))))
                        f.tookAddressOf++;
                    if ((f.isNested() && !(f.needThis())))
                    {
                        if (f.isFuncLiteralDeclaration() != null)
                        {
                            if (!(f.isNested()))
                            {
                                Expression e = new DelegateExp(exp.loc, new NullExp(exp.loc, Type.tnull), f, ve.hasOverloads, null);
                                e = expressionSemantic(e, this.sc);
                                this.result = e;
                                return ;
                            }
                        }
                        Expression e = new DelegateExp(exp.loc, exp.e1, f, ve.hasOverloads, null);
                        e = expressionSemantic(e, this.sc);
                        this.result = e;
                        return ;
                    }
                    if (f.needThis())
                    {
                        if (hasThis(this.sc) != null)
                        {
                            Expression ethis = new ThisExp(exp.loc);
                            Expression e = new DelegateExp(exp.loc, ethis, f, ve.hasOverloads, null);
                            e = expressionSemantic(e, this.sc);
                            this.result = e;
                            return ;
                        }
                        if (((this.sc).func != null && !(((this.sc).intypeof) != 0)))
                        {
                            if (((this.sc).func.setUnsafe() && !(((this.sc).flags & 8) != 0)))
                            {
                                exp.error(new BytePtr("`this` reference necessary to take address of member `%s` in `@safe` function `%s`"), f.toChars(), (this.sc).func.toChars());
                            }
                        }
                    }
                }
            }
            else if ((((exp.e1.op & 0xFF) == 123 || (exp.e1.op & 0xFF) == 124) && global.params.vsafe))
            {
                {
                    VarDeclaration v = expToVariable(exp.e1);
                    if (v != null)
                    {
                        if (!(checkAddressVar(this.sc, exp, v)))
                            this.setError();
                            return ;
                    }
                }
            }
            else if ((exp.e1.op & 0xFF) == 18)
            {
                CallExp ce = (CallExp)exp.e1;
                if ((ce.e1.type.ty & 0xFF) == ENUMTY.Tfunction)
                {
                    TypeFunction tf = (TypeFunction)ce.e1.type;
                    if (((((tf.isref && (this.sc).func != null) && !(((this.sc).intypeof) != 0)) && (this.sc).func.setUnsafe()) && !(((this.sc).flags & 8) != 0)))
                    {
                        exp.error(new BytePtr("cannot take address of `ref return` of `%s()` in `@safe` function `%s`"), ce.e1.toChars(), (this.sc).func.toChars());
                    }
                }
            }
            else if ((exp.e1.op & 0xFF) == 62)
            {
                {
                    VarDeclaration v = expToVariable(exp.e1);
                    if (v != null)
                    {
                        if ((global.params.vsafe && !(checkAddressVar(this.sc, exp, v))))
                            this.setError();
                            return ;
                        exp.e1.checkPurity(this.sc, v);
                    }
                }
            }
            else if ((wasCond) != 0)
            {
                assert((exp.e1.op & 0xFF) == 24);
                PtrExp pe = (PtrExp)exp.e1;
                assert((pe.e1.op & 0xFF) == 100);
                CondExp ce = (CondExp)pe.e1;
                assert((ce.e1.op & 0xFF) == 19);
                assert((ce.e2.op & 0xFF) == 19);
                ce.e1.type = null;
                ce.e1 = expressionSemantic(ce.e1, this.sc);
                ce.e2.type = null;
                ce.e2 = expressionSemantic(ce.e2, this.sc);
            }
            this.result = exp.optimize(0, false);
        }

        public  void visit(PtrExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            Type tb = exp.e1.type.toBasetype();
            {
                int __dispatch6 = 0;
                dispatched_6:
                do {
                    switch (__dispatch6 != 0 ? __dispatch6 : (tb.ty & 0xFF))
                    {
                        case 3:
                            exp.type = ((TypePointer)tb).next;
                            break;
                        case 1:
                        case 0:
                            if (isNonAssignmentArrayOp(exp.e1))
                                /*goto default*/ { __dispatch6 = -1; continue dispatched_6; }
                            exp.error(new BytePtr("using `*` on an array is no longer supported; use `*(%s).ptr` instead"), exp.e1.toChars());
                            exp.type = ((TypeArray)tb).next;
                            exp.e1 = exp.e1.castTo(this.sc, exp.type.pointerTo());
                            break;
                        case 34:
                            __dispatch6 = 0;
                            this.setError();
                            return ;
                        default:
                        __dispatch6 = 0;
                        exp.error(new BytePtr("can only `*` a pointer, not a `%s`"), exp.e1.type.toChars());
                        /*goto case*/{ __dispatch6 = 34; continue dispatched_6; }
                    }
                } while(__dispatch6 != 0);
            }
            if (exp.checkValue())
                this.setError();
                return ;
            this.result = exp;
        }

        public  void visit(NegExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            fix16997(this.sc, exp);
            exp.type = exp.e1.type;
            Type tb = exp.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tarray || (tb.ty & 0xFF) == ENUMTY.Tsarray))
            {
                if (!(isArrayOpValid(exp.e1)))
                {
                    this.result = arrayOpInvalidError(exp);
                    return ;
                }
                this.result = exp;
                return ;
            }
            if (!(target.isVectorOpSupported(tb, exp.op, null)))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            if (exp.e1.checkNoBool())
                this.setError();
                return ;
            if (exp.e1.checkArithmetic())
                this.setError();
                return ;
            this.result = exp;
        }

        public  void visit(UAddExp exp) {
            assert(!(exp.type != null));
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            fix16997(this.sc, exp);
            if (!(target.isVectorOpSupported(exp.e1.type.toBasetype(), exp.op, null)))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            if (exp.e1.checkNoBool())
                this.setError();
                return ;
            if (exp.e1.checkArithmetic())
                this.setError();
                return ;
            this.result = exp.e1;
        }

        public  void visit(ComExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            fix16997(this.sc, exp);
            exp.type = exp.e1.type;
            Type tb = exp.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tarray || (tb.ty & 0xFF) == ENUMTY.Tsarray))
            {
                if (!(isArrayOpValid(exp.e1)))
                {
                    this.result = arrayOpInvalidError(exp);
                    return ;
                }
                this.result = exp;
                return ;
            }
            if (!(target.isVectorOpSupported(tb, exp.op, null)))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            if (exp.e1.checkNoBool())
                this.setError();
                return ;
            if (exp.e1.checkIntegral())
                this.setError();
                return ;
            this.result = exp;
        }

        public  void visit(NotExp e) {
            if (e.type != null)
            {
                this.result = e;
                return ;
            }
            e.setNoderefOperand();
            {
                Expression ex = unaSemantic(e, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            if ((e.e1.op & 0xFF) == 20)
                e.e1 = resolveAliasThis(this.sc, e.e1, false);
            e.e1 = resolveProperties(this.sc, e.e1);
            e.e1 = e.e1.toBoolean(this.sc);
            if (pequals(e.e1.type, Type.terror))
            {
                this.result = e.e1;
                return ;
            }
            if (!(target.isVectorOpSupported(e.e1.type.toBasetype(), e.op, null)))
            {
                this.result = e.incompatibleTypes();
            }
            if (checkNonAssignmentArrayOp(e.e1, false))
                this.setError();
                return ;
            e.type = Type.tbool;
            this.result = e;
        }

        public  void visit(DeleteExp exp) {
            if (!((this.sc).isDeprecated()))
            {
                if (!(exp.isRAII))
                    deprecation(exp.loc, new BytePtr("The `delete` keyword has been deprecated.  Use `object.destroy()` (and `core.memory.GC.free()` if applicable) instead."));
            }
            {
                Expression ex = unaSemantic(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            exp.e1 = resolveProperties(this.sc, exp.e1);
            exp.e1 = exp.e1.modifiableLvalue(this.sc, null);
            if ((exp.e1.op & 0xFF) == 127)
            {
                this.result = exp.e1;
                return ;
            }
            exp.type = Type.tvoid;
            AggregateDeclaration ad = null;
            Type tb = exp.e1.type.toBasetype();
            switch ((tb.ty & 0xFF))
            {
                case 7:
                    ClassDeclaration cd = ((TypeClass)tb).sym;
                    if (cd.isCOMinterface())
                    {
                        exp.error(new BytePtr("cannot `delete` instance of COM interface `%s`"), cd.toChars());
                        this.setError();
                        return ;
                    }
                    ad = cd;
                    break;
                case 3:
                    tb = ((TypePointer)tb).next.toBasetype();
                    if ((tb.ty & 0xFF) == ENUMTY.Tstruct)
                    {
                        ad = ((TypeStruct)tb).sym;
                        DeleteDeclaration f = ad.aggDelete;
                        DtorDeclaration fd = ad.dtor;
                        if (!(f != null))
                        {
                            semanticTypeInfo(this.sc, tb);
                            break;
                        }
                        Expression ea = null;
                        Expression eb = null;
                        Expression ec = null;
                        VarDeclaration v = null;
                        if ((fd != null && f != null))
                        {
                            v = copyToTemp(0L, new BytePtr("__tmpea"), exp.e1);
                            dsymbolSemantic(v, this.sc);
                            ea = new DeclarationExp(exp.loc, v);
                            ea.type = v.type;
                        }
                        if (fd != null)
                        {
                            Expression e_1 = ea != null ? new VarExp(exp.loc, v, true) : exp.e1;
                            e_1 = new DotVarExp(Loc.initial, e_1, fd, false);
                            eb = new CallExp(exp.loc, e_1);
                            eb = expressionSemantic(eb, this.sc);
                        }
                        if (f != null)
                        {
                            Type tpv = Type.tvoid.pointerTo();
                            Expression e = ea != null ? new VarExp(exp.loc, v, true) : exp.e1.castTo(this.sc, tpv);
                            e = new CallExp(exp.loc, new VarExp(exp.loc, f, false), e);
                            ec = expressionSemantic(e, this.sc);
                        }
                        ea = Expression.combine(ea, eb, ec);
                        assert(ea != null);
                        this.result = ea;
                        return ;
                    }
                    break;
                case 0:
                    Type tv = tb.nextOf().baseElemOf();
                    if ((tv.ty & 0xFF) == ENUMTY.Tstruct)
                    {
                        ad = ((TypeStruct)tv).sym;
                        if (ad.dtor != null)
                            semanticTypeInfo(this.sc, ad.type);
                    }
                    break;
                default:
                exp.error(new BytePtr("cannot delete type `%s`"), exp.e1.type.toChars());
                this.setError();
                return ;
            }
            boolean err = false;
            if (ad != null)
            {
                if (ad.dtor != null)
                {
                    (err ? 1 : 0) |= (exp.checkPurity(this.sc, ad.dtor) ? 1 : 0);
                    (err ? 1 : 0) |= (exp.checkSafety(this.sc, ad.dtor) ? 1 : 0);
                    (err ? 1 : 0) |= (exp.checkNogc(this.sc, ad.dtor) ? 1 : 0);
                }
                if ((ad.aggDelete != null && (tb.ty & 0xFF) != ENUMTY.Tarray))
                {
                    (err ? 1 : 0) |= (exp.checkPurity(this.sc, ad.aggDelete) ? 1 : 0);
                    (err ? 1 : 0) |= (exp.checkSafety(this.sc, ad.aggDelete) ? 1 : 0);
                    (err ? 1 : 0) |= (exp.checkNogc(this.sc, ad.aggDelete) ? 1 : 0);
                }
                if (err)
                    this.setError();
                    return ;
            }
            if (((((!(((this.sc).intypeof) != 0) && (this.sc).func != null) && !(exp.isRAII)) && (this.sc).func.setUnsafe()) && !(((this.sc).flags & 8) != 0)))
            {
                exp.error(new BytePtr("`%s` is not `@safe` but is used in `@safe` function `%s`"), exp.toChars(), (this.sc).func.toChars());
                err = true;
            }
            if (err)
                this.setError();
                return ;
            this.result = exp;
        }

        public  void visit(CastExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            if (exp.to != null)
            {
                exp.to = typeSemantic(exp.to, exp.loc, this.sc);
                if (pequals(exp.to, Type.terror))
                    this.setError();
                    return ;
                if (!(exp.to.hasPointers()))
                    exp.setNoderefOperand();
                exp.e1 = inferType(exp.e1, exp.to, 0);
            }
            {
                Expression e = unaSemantic(exp, this.sc);
                if (e != null)
                {
                    this.result = e;
                    return ;
                }
            }
            if ((exp.e1.op & 0xFF) == 20)
                exp.e1 = resolveAliasThis(this.sc, exp.e1, false);
            Expression e1x = resolveProperties(this.sc, exp.e1);
            if ((e1x.op & 0xFF) == 127)
            {
                this.result = e1x;
                return ;
            }
            if (e1x.checkType())
                this.setError();
                return ;
            exp.e1 = e1x;
            if (!(exp.e1.type != null))
            {
                exp.error(new BytePtr("cannot cast `%s`"), exp.e1.toChars());
                this.setError();
                return ;
            }
            if ((exp.e1.type.ty & 0xFF) == ENUMTY.Ttuple)
            {
                TupleExp te = exp.e1.isTupleExp();
                if ((te.exps).length == 1)
                    exp.e1 = (te.exps).get(0);
            }
            boolean allowImplicitConstruction = exp.to != null;
            if (!(exp.to != null))
            {
                exp.to = exp.e1.type.castMod(exp.mod);
                exp.to = typeSemantic(exp.to, exp.loc, this.sc);
                if (pequals(exp.to, Type.terror))
                    this.setError();
                    return ;
            }
            if ((exp.to.ty & 0xFF) == ENUMTY.Ttuple)
            {
                exp.error(new BytePtr("cannot cast `%s` to tuple type `%s`"), exp.e1.toChars(), exp.to.toChars());
                this.setError();
                return ;
            }
            if ((exp.to.ty & 0xFF) == ENUMTY.Tvoid)
            {
                exp.type = exp.to;
                this.result = exp;
                return ;
            }
            if ((!(exp.to.equals(exp.e1.type)) && (exp.mod & 0xFF) == 255))
            {
                {
                    Expression e = op_overload(exp, this.sc, null);
                    if (e != null)
                    {
                        this.result = e.implicitCastTo(this.sc, exp.to);
                        return ;
                    }
                }
            }
            Type t1b = exp.e1.type.toBasetype();
            Type tob = exp.to.toBasetype();
            if (((allowImplicitConstruction && (tob.ty & 0xFF) == ENUMTY.Tstruct) && !(tob.equals(t1b))))
            {
                Expression e = new TypeExp(exp.loc, exp.to);
                e = new CallExp(exp.loc, e, exp.e1);
                e = trySemantic(e, this.sc);
                if (e != null)
                {
                    this.result = e;
                    return ;
                }
            }
            if ((!(t1b.equals(tob)) && ((t1b.ty & 0xFF) == ENUMTY.Tarray || (t1b.ty & 0xFF) == ENUMTY.Tsarray)))
            {
                if (checkNonAssignmentArrayOp(exp.e1, false))
                    this.setError();
                    return ;
            }
            if (((tob.ty & 0xFF) == ENUMTY.Tvector && (t1b.ty & 0xFF) != ENUMTY.Tvector))
            {
                this.result = new VectorExp(exp.loc, exp.e1, exp.to);
                return ;
            }
            Expression ex = exp.e1.castTo(this.sc, exp.to);
            if ((ex.op & 0xFF) == 127)
            {
                this.result = ex;
                return ;
            }
            if ((((!(((this.sc).intypeof) != 0) && !(isSafeCast(ex, t1b, tob))) && ((!((this.sc).func != null) && ((this.sc).stc & 8589934592L) != 0) || ((this.sc).func != null && (this.sc).func.setUnsafe()))) && !(((this.sc).flags & 8) != 0)))
            {
                exp.error(new BytePtr("cast from `%s` to `%s` not allowed in safe code"), exp.e1.type.toChars(), exp.to.toChars());
                this.setError();
                return ;
            }
            if ((tob.ty & 0xFF) == ENUMTY.Tarray)
            {
                {
                    AggregateDeclaration ad = isAggregate(t1b);
                    if (ad != null)
                    {
                        if (ad.aliasthis != null)
                        {
                            Expression e = resolveAliasThis(this.sc, exp.e1, false);
                            e = new CastExp(exp.loc, e, exp.to);
                            this.result = expressionSemantic(e, this.sc);
                            return ;
                        }
                    }
                }
                if ((((t1b.ty & 0xFF) == ENUMTY.Tarray && (exp.e1.op & 0xFF) != 47) && ((this.sc).flags & 128) == 0))
                {
                    Type tFrom = t1b.nextOf();
                    Type tTo = tob.nextOf();
                    if (((exp.e1.op & 0xFF) != 121 || (tTo.ty & 0xFF) == ENUMTY.Tarray))
                    {
                        int fromSize = (int)tFrom.size();
                        int toSize = (int)tTo.size();
                        if (fromSize != toSize)
                        {
                            if (!(verifyHookExist(exp.loc, this.sc, Id.__ArrayCast, new ByteSlice("casting array of structs"), Id.object)))
                                this.setError();
                                return ;
                            if ((toSize == 0 || fromSize % toSize != 0))
                            {
                                Expression id = new IdentifierExp(exp.loc, Id.empty);
                                DotIdExp dotid = new DotIdExp(exp.loc, id, Id.object);
                                DArray<RootObject> tiargs = new DArray<RootObject>();
                                (tiargs).push(tFrom);
                                (tiargs).push(tTo);
                                DotTemplateInstanceExp dt = new DotTemplateInstanceExp(exp.loc, dotid, Id.__ArrayCast, tiargs);
                                DArray<Expression> arguments = new DArray<Expression>();
                                (arguments).push(exp.e1);
                                Expression ce = new CallExp(exp.loc, dt, arguments);
                                this.result = expressionSemantic(ce, this.sc);
                                return ;
                            }
                        }
                    }
                }
            }
            this.result = ex;
        }

        public  void visit(VectorExp exp) {
            Ref<VectorExp> exp_ref = ref(exp);
            if (exp_ref.value.type != null)
            {
                this.result = exp_ref.value;
                return ;
            }
            exp_ref.value.e1 = expressionSemantic(exp_ref.value.e1, this.sc);
            exp_ref.value.type = typeSemantic(exp_ref.value.to, exp_ref.value.loc, this.sc);
            if (((exp_ref.value.e1.op & 0xFF) == 127 || (exp_ref.value.type.ty & 0xFF) == ENUMTY.Terror))
            {
                this.result = exp_ref.value.e1;
                return ;
            }
            Type tb = exp_ref.value.type.toBasetype();
            assert((tb.ty & 0xFF) == ENUMTY.Tvector);
            TypeVector tv = (TypeVector)tb;
            Type te = tv.elementType();
            exp_ref.value.dim = (int)(tv.size(exp_ref.value.loc) / te.size(exp_ref.value.loc));
            Function1<Expression,Boolean> checkElem = new Function1<Expression,Boolean>(){
                public Boolean invoke(Expression elem){
                    if (elem.isConst() == 1)
                        return false;
                    exp_ref.value.error(new BytePtr("constant expression expected, not `%s`"), elem.toChars());
                    return true;
                }
            };
            exp_ref.value.e1 = exp_ref.value.e1.optimize(0, false);
            boolean res = false;
            if ((exp_ref.value.e1.op & 0xFF) == 47)
            {
                {
                    int __key1384 = 0;
                    int __limit1385 = exp_ref.value.dim;
                    for (; __key1384 < __limit1385;__key1384 += 1) {
                        int i = __key1384;
                        (res ? 1 : 0) |= (checkElem.invoke(((ArrayLiteralExp)exp_ref.value.e1).getElement(i)) ? 1 : 0);
                    }
                }
            }
            else if ((exp_ref.value.e1.type.ty & 0xFF) == ENUMTY.Tvoid)
                checkElem.invoke(exp_ref.value.e1);
            this.result = res ? new ErrorExp() : exp_ref.value;
        }

        public  void visit(VectorArrayExp e) {
            if (!(e.type != null))
            {
                unaSemantic(e, this.sc);
                e.e1 = resolveProperties(this.sc, e.e1);
                if ((e.e1.op & 0xFF) == 127)
                {
                    this.result = e.e1;
                    return ;
                }
                assert((e.e1.type.ty & 0xFF) == ENUMTY.Tvector);
                e.type = e.e1.type.isTypeVector().basetype;
            }
            this.result = e;
        }

        public  void visit(SliceExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            {
                Expression ex = unaSemantic(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            exp.e1 = resolveProperties(this.sc, exp.e1);
            if (((exp.e1.op & 0xFF) == 20 && (exp.e1.type.ty & 0xFF) != ENUMTY.Ttuple))
            {
                if ((exp.lwr != null || exp.upr != null))
                {
                    exp.error(new BytePtr("cannot slice type `%s`"), exp.e1.toChars());
                    this.setError();
                    return ;
                }
                Expression e = new TypeExp(exp.loc, exp.e1.type.arrayOf());
                this.result = expressionSemantic(e, this.sc);
                return ;
            }
            if ((!(exp.lwr != null) && !(exp.upr != null)))
            {
                if ((exp.e1.op & 0xFF) == 47)
                {
                    Type t1b = exp.e1.type.toBasetype();
                    Expression e = exp.e1;
                    if ((t1b.ty & 0xFF) == ENUMTY.Tsarray)
                    {
                        e = e.copy();
                        e.type = t1b.nextOf().arrayOf();
                    }
                    this.result = e;
                    return ;
                }
                if ((exp.e1.op & 0xFF) == 31)
                {
                    SliceExp se = (SliceExp)exp.e1;
                    if ((!(se.lwr != null) && !(se.upr != null)))
                    {
                        this.result = se;
                        return ;
                    }
                }
                if (isArrayOpOperand(exp.e1))
                {
                    this.result = exp.e1;
                    return ;
                }
            }
            if ((exp.e1.op & 0xFF) == 127)
            {
                this.result = exp.e1;
                return ;
            }
            if ((exp.e1.type.ty & 0xFF) == ENUMTY.Terror)
                this.setError();
                return ;
            Type t1b = exp.e1.type.toBasetype();
            if ((t1b.ty & 0xFF) == ENUMTY.Tpointer)
            {
                if ((((TypePointer)t1b).next.ty & 0xFF) == ENUMTY.Tfunction)
                {
                    exp.error(new BytePtr("cannot slice function pointer `%s`"), exp.e1.toChars());
                    this.setError();
                    return ;
                }
                if ((!(exp.lwr != null) || !(exp.upr != null)))
                {
                    exp.error(new BytePtr("need upper and lower bound to slice pointer"));
                    this.setError();
                    return ;
                }
                if (((((this.sc).func != null && !(((this.sc).intypeof) != 0)) && (this.sc).func.setUnsafe()) && !(((this.sc).flags & 8) != 0)))
                {
                    exp.error(new BytePtr("pointer slicing not allowed in safe functions"));
                    this.setError();
                    return ;
                }
            }
            else if ((t1b.ty & 0xFF) == ENUMTY.Tarray)
            {
            }
            else if ((t1b.ty & 0xFF) == ENUMTY.Tsarray)
            {
                if ((!(exp.arrayop) && global.params.vsafe))
                {
                    {
                        VarDeclaration v = expToVariable(exp.e1);
                        if (v != null)
                        {
                            if ((exp.e1.op & 0xFF) == 27)
                            {
                                DotVarExp dve = (DotVarExp)exp.e1;
                                if ((((dve.e1.op & 0xFF) == 123 || (dve.e1.op & 0xFF) == 124) && !((v.storage_class & 2097152L) != 0)))
                                {
                                    v = null;
                                }
                            }
                            if ((v != null && !(checkAddressVar(this.sc, exp, v))))
                                this.setError();
                                return ;
                        }
                    }
                }
            }
            else if ((t1b.ty & 0xFF) == ENUMTY.Ttuple)
            {
                if ((!(exp.lwr != null) && !(exp.upr != null)))
                {
                    this.result = exp.e1;
                    return ;
                }
                if ((!(exp.lwr != null) || !(exp.upr != null)))
                {
                    exp.error(new BytePtr("need upper and lower bound to slice tuple"));
                    this.setError();
                    return ;
                }
            }
            else if ((t1b.ty & 0xFF) == ENUMTY.Tvector)
            {
                TypeVector tv1 = (TypeVector)t1b;
                t1b = tv1.basetype;
                t1b = t1b.castMod(tv1.mod);
                exp.e1.type = t1b;
            }
            else
            {
                exp.error(new BytePtr("`%s` cannot be sliced with `[]`"), (t1b.ty & 0xFF) == ENUMTY.Tvoid ? exp.e1.toChars() : t1b.toChars());
                this.setError();
                return ;
            }
            Scope scx = this.sc;
            if ((((t1b.ty & 0xFF) == ENUMTY.Tsarray || (t1b.ty & 0xFF) == ENUMTY.Tarray) || (t1b.ty & 0xFF) == ENUMTY.Ttuple))
            {
                ScopeDsymbol sym = new ArrayScopeSymbol(this.sc, exp);
                sym.parent = (this.sc).scopesym;
                this.sc = (this.sc).push(sym);
            }
            if (exp.lwr != null)
            {
                if ((t1b.ty & 0xFF) == ENUMTY.Ttuple)
                    this.sc = (this.sc).startCTFE();
                exp.lwr = expressionSemantic(exp.lwr, this.sc);
                exp.lwr = resolveProperties(this.sc, exp.lwr);
                if ((t1b.ty & 0xFF) == ENUMTY.Ttuple)
                    this.sc = (this.sc).endCTFE();
                exp.lwr = exp.lwr.implicitCastTo(this.sc, Type.tsize_t);
            }
            if (exp.upr != null)
            {
                if ((t1b.ty & 0xFF) == ENUMTY.Ttuple)
                    this.sc = (this.sc).startCTFE();
                exp.upr = expressionSemantic(exp.upr, this.sc);
                exp.upr = resolveProperties(this.sc, exp.upr);
                if ((t1b.ty & 0xFF) == ENUMTY.Ttuple)
                    this.sc = (this.sc).endCTFE();
                exp.upr = exp.upr.implicitCastTo(this.sc, Type.tsize_t);
            }
            if (this.sc != scx)
                this.sc = (this.sc).pop();
            if (((exp.lwr != null && pequals(exp.lwr.type, Type.terror)) || (exp.upr != null && pequals(exp.upr.type, Type.terror))))
                this.setError();
                return ;
            if ((t1b.ty & 0xFF) == ENUMTY.Ttuple)
            {
                exp.lwr = exp.lwr.ctfeInterpret();
                exp.upr = exp.upr.ctfeInterpret();
                long i1 = exp.lwr.toUInteger();
                long i2 = exp.upr.toUInteger();
                TupleExp te = null;
                TypeTuple tup = null;
                int length = 0;
                if ((exp.e1.op & 0xFF) == 126)
                {
                    te = (TupleExp)exp.e1;
                    tup = null;
                    length = (te.exps).length;
                }
                else if ((exp.e1.op & 0xFF) == 20)
                {
                    te = null;
                    tup = (TypeTuple)t1b;
                    length = Parameter.dim(tup.arguments);
                }
                else
                    throw new AssertionError("Unreachable code!");
                if ((i2 < i1 || (long)length < i2))
                {
                    exp.error(new BytePtr("string slice `[%llu .. %llu]` is out of bounds"), i1, i2);
                    this.setError();
                    return ;
                }
                int j1 = (int)i1;
                int j2 = (int)i2;
                Expression e = null;
                if ((exp.e1.op & 0xFF) == 126)
                {
                    DArray<Expression> exps = new DArray<Expression>(j2 - j1);
                    {
                        int i = 0;
                        for (; i < j2 - j1;i++){
                            exps.set(i, (te.exps).get(j1 + i));
                        }
                    }
                    e = new TupleExp(exp.loc, te.e0, exps);
                }
                else
                {
                    DArray<Parameter> args = new DArray<Parameter>();
                    (args).reserve(j2 - j1);
                    {
                        int i = j1;
                        for (; i < j2;i++){
                            Parameter arg = Parameter.getNth(tup.arguments, i, null);
                            (args).push(arg);
                        }
                    }
                    e = new TypeExp(exp.e1.loc, new TypeTuple(args));
                }
                e = expressionSemantic(e, this.sc);
                this.result = e;
                return ;
            }
            exp.type = t1b.nextOf().arrayOf();
            if (exp.type.equals(t1b))
                exp.type = exp.e1.type;
            setLengthVarIfKnown(exp.lengthVar, t1b);
            if ((exp.lwr != null && exp.upr != null))
            {
                exp.lwr = exp.lwr.optimize(0, false);
                exp.upr = exp.upr.optimize(0, false);
                IntRange lwrRange = getIntRange(exp.lwr).copy();
                IntRange uprRange = getIntRange(exp.upr).copy();
                if (((t1b.ty & 0xFF) == ENUMTY.Tsarray || (t1b.ty & 0xFF) == ENUMTY.Tarray))
                {
                    Expression el = new ArrayLengthExp(exp.loc, exp.e1);
                    el = expressionSemantic(el, this.sc);
                    el = el.optimize(0, false);
                    if ((el.op & 0xFF) == 135)
                    {
                        long length = el.toInteger();
                        IntRange bounds = bounds = new IntRange(new SignExtendedNumber(0L, false), new SignExtendedNumber(length, false));
                        exp.upperIsInBounds = bounds.contains(uprRange);
                    }
                    else if (((exp.upr.op & 0xFF) == 135 && exp.upr.toInteger() == 0L))
                    {
                        exp.upperIsInBounds = true;
                    }
                    else if (((exp.upr.op & 0xFF) == 26 && pequals(((VarExp)exp.upr).var.ident, Id.dollar)))
                    {
                        exp.upperIsInBounds = true;
                    }
                }
                else if ((t1b.ty & 0xFF) == ENUMTY.Tpointer)
                {
                    exp.upperIsInBounds = true;
                }
                else
                    throw new AssertionError("Unreachable code!");
                exp.lowerIsLessThanUpper = lwrRange.imax.opCmp(uprRange.imin) <= 0;
            }
            this.result = exp;
        }

        public  void visit(ArrayLengthExp e) {
            if (e.type != null)
            {
                this.result = e;
                return ;
            }
            {
                Expression ex = unaSemantic(e, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            e.e1 = resolveProperties(this.sc, e.e1);
            e.type = Type.tsize_t;
            this.result = e;
        }

        public  void visit(ArrayExp exp) {
            assert(!(exp.type != null));
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            if (isAggregate(exp.e1.type) != null)
                exp.error(new BytePtr("no `[]` operator overload for type `%s`"), exp.e1.type.toChars());
            else if (((exp.e1.op & 0xFF) == 20 && (exp.e1.type.ty & 0xFF) != ENUMTY.Ttuple))
                exp.error(new BytePtr("static array of `%s` with multiple lengths not allowed"), exp.e1.type.toChars());
            else if (isIndexableNonAggregate(exp.e1.type))
                exp.error(new BytePtr("only one index allowed to index `%s`"), exp.e1.type.toChars());
            else
                exp.error(new BytePtr("cannot use `[]` operator on expression of type `%s`"), exp.e1.type.toChars());
            this.result = new ErrorExp();
        }

        public  void visit(DotExp exp) {
            exp.e1 = expressionSemantic(exp.e1, this.sc);
            exp.e2 = expressionSemantic(exp.e2, this.sc);
            if ((exp.e1.op & 0xFF) == 20)
            {
                this.result = exp.e2;
                return ;
            }
            if ((exp.e2.op & 0xFF) == 20)
            {
                this.result = exp.e2;
                return ;
            }
            if ((exp.e2.op & 0xFF) == 36)
            {
                TemplateDeclaration td = ((TemplateExp)exp.e2).td;
                Expression e = new DotTemplateExp(exp.loc, exp.e1, td);
                this.result = expressionSemantic(e, this.sc);
                return ;
            }
            if (!(exp.type != null))
                exp.type = exp.e2.type;
            this.result = exp;
        }

        public  void visit(CommaExp e) {
            if (e.type != null)
            {
                this.result = e;
                return ;
            }
            if (e.allowCommaExp)
            {
                CommaExp.allow(e.e1);
                CommaExp.allow(e.e2);
            }
            {
                Expression ex = binSemanticProp(e, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            e.e1 = e.e1.addDtorHook(this.sc);
            if (checkNonAssignmentArrayOp(e.e1, false))
                this.setError();
                return ;
            e.type = e.e2.type;
            if (((e.type != Type.tvoid && !(e.allowCommaExp)) && !(e.isGenerated)))
                e.error(new BytePtr("Using the result of a comma expression is not allowed"));
            this.result = e;
        }

        public  void visit(IntervalExp e) {
            if (e.type != null)
            {
                this.result = e;
                return ;
            }
            Expression le = e.lwr;
            le = expressionSemantic(le, this.sc);
            le = resolveProperties(this.sc, le);
            Expression ue = e.upr;
            ue = expressionSemantic(ue, this.sc);
            ue = resolveProperties(this.sc, ue);
            if ((le.op & 0xFF) == 127)
            {
                this.result = le;
                return ;
            }
            if ((ue.op & 0xFF) == 127)
            {
                this.result = ue;
                return ;
            }
            e.lwr = le;
            e.upr = ue;
            e.type = Type.tvoid;
            this.result = e;
        }

        public  void visit(DelegatePtrExp e) {
            if (!(e.type != null))
            {
                unaSemantic(e, this.sc);
                e.e1 = resolveProperties(this.sc, e.e1);
                if ((e.e1.op & 0xFF) == 127)
                {
                    this.result = e.e1;
                    return ;
                }
                e.type = Type.tvoidptr;
            }
            this.result = e;
        }

        public  void visit(DelegateFuncptrExp e) {
            if (!(e.type != null))
            {
                unaSemantic(e, this.sc);
                e.e1 = resolveProperties(this.sc, e.e1);
                if ((e.e1.op & 0xFF) == 127)
                {
                    this.result = e.e1;
                    return ;
                }
                e.type = e.e1.type.nextOf().pointerTo();
            }
            this.result = e;
        }

        public  void visit(IndexExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            if (!(exp.e1.type != null))
                exp.e1 = expressionSemantic(exp.e1, this.sc);
            assert(exp.e1.type != null);
            if (((exp.e1.op & 0xFF) == 20 && (exp.e1.type.ty & 0xFF) != ENUMTY.Ttuple))
            {
                exp.e2 = expressionSemantic(exp.e2, this.sc);
                exp.e2 = resolveProperties(this.sc, exp.e2);
                Type nt = null;
                if ((exp.e2.op & 0xFF) == 20)
                    nt = new TypeAArray(exp.e1.type, exp.e2.type);
                else
                    nt = new TypeSArray(exp.e1.type, exp.e2);
                Expression e = new TypeExp(exp.loc, nt);
                this.result = expressionSemantic(e, this.sc);
                return ;
            }
            if ((exp.e1.op & 0xFF) == 127)
            {
                this.result = exp.e1;
                return ;
            }
            if ((exp.e1.type.ty & 0xFF) == ENUMTY.Terror)
                this.setError();
                return ;
            Type t1b = exp.e1.type.toBasetype();
            if ((t1b.ty & 0xFF) == ENUMTY.Tvector)
            {
                TypeVector tv1 = (TypeVector)t1b;
                t1b = tv1.basetype;
                t1b = t1b.castMod(tv1.mod);
                exp.e1.type = t1b;
            }
            Scope scx = this.sc;
            if ((((t1b.ty & 0xFF) == ENUMTY.Tsarray || (t1b.ty & 0xFF) == ENUMTY.Tarray) || (t1b.ty & 0xFF) == ENUMTY.Ttuple))
            {
                ScopeDsymbol sym = new ArrayScopeSymbol(this.sc, exp);
                sym.parent = (this.sc).scopesym;
                this.sc = (this.sc).push(sym);
            }
            if ((t1b.ty & 0xFF) == ENUMTY.Ttuple)
                this.sc = (this.sc).startCTFE();
            exp.e2 = expressionSemantic(exp.e2, this.sc);
            exp.e2 = resolveProperties(this.sc, exp.e2);
            if ((t1b.ty & 0xFF) == ENUMTY.Ttuple)
                this.sc = (this.sc).endCTFE();
            if ((exp.e2.op & 0xFF) == 126)
            {
                TupleExp te = (TupleExp)exp.e2;
                if ((te.exps != null && (te.exps).length == 1))
                    exp.e2 = Expression.combine(te.e0, (te.exps).get(0));
            }
            if (this.sc != scx)
                this.sc = (this.sc).pop();
            if (pequals(exp.e2.type, Type.terror))
                this.setError();
                return ;
            if (checkNonAssignmentArrayOp(exp.e1, false))
                this.setError();
                return ;
            switch ((t1b.ty & 0xFF))
            {
                case 3:
                    if ((((TypePointer)t1b).next.ty & 0xFF) == ENUMTY.Tfunction)
                    {
                        exp.error(new BytePtr("cannot index function pointer `%s`"), exp.e1.toChars());
                        this.setError();
                        return ;
                    }
                    exp.e2 = exp.e2.implicitCastTo(this.sc, Type.tsize_t);
                    if (pequals(exp.e2.type, Type.terror))
                        this.setError();
                        return ;
                    exp.e2 = exp.e2.optimize(0, false);
                    if (((exp.e2.op & 0xFF) == 135 && exp.e2.toInteger() == 0L))
                    {
                    }
                    else if ((((this.sc).func != null && (this.sc).func.setUnsafe()) && !(((this.sc).flags & 8) != 0)))
                    {
                        exp.error(new BytePtr("safe function `%s` cannot index pointer `%s`"), (this.sc).func.toPrettyChars(false), exp.e1.toChars());
                        this.setError();
                        return ;
                    }
                    exp.type = ((TypeNext)t1b).next;
                    break;
                case 0:
                    exp.e2 = exp.e2.implicitCastTo(this.sc, Type.tsize_t);
                    if (pequals(exp.e2.type, Type.terror))
                        this.setError();
                        return ;
                    exp.type = ((TypeNext)t1b).next;
                    break;
                case 1:
                    exp.e2 = exp.e2.implicitCastTo(this.sc, Type.tsize_t);
                    if (pequals(exp.e2.type, Type.terror))
                        this.setError();
                        return ;
                    exp.type = t1b.nextOf();
                    break;
                case 2:
                    TypeAArray taa = (TypeAArray)t1b;
                    if (!(arrayTypeCompatibleWithoutCasting(exp.e2.type, taa.index)))
                    {
                        exp.e2 = exp.e2.implicitCastTo(this.sc, taa.index);
                        if (pequals(exp.e2.type, Type.terror))
                            this.setError();
                            return ;
                    }
                    semanticTypeInfo(this.sc, taa);
                    exp.type = taa.next;
                    break;
                case 37:
                    exp.e2 = exp.e2.implicitCastTo(this.sc, Type.tsize_t);
                    if (pequals(exp.e2.type, Type.terror))
                        this.setError();
                        return ;
                    exp.e2 = exp.e2.ctfeInterpret();
                    long index = exp.e2.toUInteger();
                    TupleExp te = null;
                    TypeTuple tup = null;
                    int length = 0;
                    if ((exp.e1.op & 0xFF) == 126)
                    {
                        te = (TupleExp)exp.e1;
                        tup = null;
                        length = (te.exps).length;
                    }
                    else if ((exp.e1.op & 0xFF) == 20)
                    {
                        te = null;
                        tup = (TypeTuple)t1b;
                        length = Parameter.dim(tup.arguments);
                    }
                    else
                        throw new AssertionError("Unreachable code!");
                    if ((long)length <= index)
                    {
                        exp.error(new BytePtr("array index `[%llu]` is outside array bounds `[0 .. %llu]`"), index, (long)length);
                        this.setError();
                        return ;
                    }
                    Expression e = null;
                    if ((exp.e1.op & 0xFF) == 126)
                    {
                        e = (te.exps).get((int)index);
                        e = Expression.combine(te.e0, e);
                    }
                    else
                        e = new TypeExp(exp.e1.loc, Parameter.getNth(tup.arguments, (int)index, null).type);
                    this.result = e;
                    return ;
                default:
                exp.error(new BytePtr("`%s` must be an array or pointer type, not `%s`"), exp.e1.toChars(), exp.e1.type.toChars());
                this.setError();
                return ;
            }
            setLengthVarIfKnown(exp.lengthVar, t1b);
            if (((t1b.ty & 0xFF) == ENUMTY.Tsarray || (t1b.ty & 0xFF) == ENUMTY.Tarray))
            {
                Expression el = new ArrayLengthExp(exp.loc, exp.e1);
                el = expressionSemantic(el, this.sc);
                el = el.optimize(0, false);
                if ((el.op & 0xFF) == 135)
                {
                    exp.e2 = exp.e2.optimize(0, false);
                    long length = el.toInteger();
                    if ((length) != 0)
                    {
                        IntRange bounds = bounds = new IntRange(new SignExtendedNumber(0L, false), new SignExtendedNumber(length - 1L, false));
                        exp.indexIsInBounds = bounds.contains(getIntRange(exp.e2));
                    }
                }
            }
            this.result = exp;
        }

        public  void visit(PostExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            {
                Expression ex = binSemantic(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Expression e1x = resolveProperties(this.sc, exp.e1);
            if ((e1x.op & 0xFF) == 127)
            {
                this.result = e1x;
                return ;
            }
            exp.e1 = e1x;
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            if (exp.e1.checkReadModifyWrite(exp.op, null))
                this.setError();
                return ;
            if ((exp.e1.op & 0xFF) == 31)
            {
                BytePtr s = pcopy((exp.op & 0xFF) == 93 ? new BytePtr("increment") : new BytePtr("decrement"));
                exp.error(new BytePtr("cannot post-%s array slice `%s`, use pre-%s instead"), s, exp.e1.toChars(), s);
                this.setError();
                return ;
            }
            exp.e1 = exp.e1.optimize(0, false);
            Type t1 = exp.e1.type.toBasetype();
            if ((((t1.ty & 0xFF) == ENUMTY.Tclass || (t1.ty & 0xFF) == ENUMTY.Tstruct) || (exp.e1.op & 0xFF) == 32))
            {
                Expression de = null;
                if (((exp.e1.op & 0xFF) != 26 && (exp.e1.op & 0xFF) != 32))
                {
                    VarDeclaration v = copyToTemp(2097152L, new BytePtr("__postref"), exp.e1);
                    de = new DeclarationExp(exp.loc, v);
                    exp.e1 = new VarExp(exp.e1.loc, v, true);
                }
                VarDeclaration tmp = copyToTemp(0L, new BytePtr("__pitmp"), exp.e1);
                Expression ea = new DeclarationExp(exp.loc, tmp);
                Expression eb = exp.e1.syntaxCopy();
                eb = new PreExp((exp.op & 0xFF) == 93 ? TOK.prePlusPlus : TOK.preMinusMinus, exp.loc, eb);
                Expression ec = new VarExp(exp.loc, tmp, true);
                if (de != null)
                    ea = new CommaExp(exp.loc, de, ea, true);
                e = new CommaExp(exp.loc, ea, eb, true);
                e = new CommaExp(exp.loc, e, ec, true);
                e = expressionSemantic(e, this.sc);
                this.result = e;
                return ;
            }
            exp.e1 = exp.e1.modifiableLvalue(this.sc, exp.e1);
            e = exp;
            if (exp.e1.checkScalar())
                this.setError();
                return ;
            if (exp.e1.checkNoBool())
                this.setError();
                return ;
            if ((exp.e1.type.ty & 0xFF) == ENUMTY.Tpointer)
                e = scaleFactor(exp, this.sc);
            else
                exp.e2 = exp.e2.castTo(this.sc, exp.e1.type);
            e.type = exp.e1.type;
            this.result = e;
        }

        public  void visit(PreExp exp) {
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            if ((exp.op & 0xFF) == 103)
                e = new AddAssignExp(exp.loc, exp.e1, new IntegerExp(exp.loc, 1L, Type.tint32));
            else
                e = new MinAssignExp(exp.loc, exp.e1, new IntegerExp(exp.loc, 1L, Type.tint32));
            this.result = expressionSemantic(e, this.sc);
        }

        public  Expression getInitExp(StructDeclaration sd, Loc loc, Scope sc, Type t) {
            if ((sd.zeroInit && !(sd.isNested())))
            {
                return new IntegerExp(loc, 0L, Type.tint32);
            }
            if (sd.isNested())
            {
                StructLiteralExp sle = new StructLiteralExp(loc, sd, null, t);
                if (!(sd.fill(loc, sle.elements, true)))
                    return new ErrorExp();
                if (checkFrameAccess(loc, sc, sd, (sle.elements).length))
                    return new ErrorExp();
                sle.type = t;
                return sle;
            }
            return defaultInit(t, loc);
        }

        public  void visit(AssignExp exp) {
            Function2<Expression,Integer,Void> setResult = new Function2<Expression,Integer,Void>(){
                public Void invoke(Expression e, Integer line){
                    result = e;
                }
            };
            if (exp.type != null)
            {
                setResult.invoke(exp, 7918);
                return ;
            }
            Expression e1old = exp.e1;
            {
                CommaExp e2comma = exp.e2.isCommaExp();
                if (e2comma != null)
                {
                    if (!(e2comma.isGenerated))
                        exp.error(new BytePtr("Using the result of a comma expression is not allowed"));
                    Ref<Expression> e0 = ref(null);
                    exp.e2 = Expression.extractLast(e2comma, e0);
                    Expression e = Expression.combine(e0.value, (Expression)exp);
                    setResult.invoke(expressionSemantic(e, this.sc), 7934);
                    return ;
                }
            }
            {
                ArrayExp ae = exp.e1.isArrayExp();
                if (ae != null)
                {
                    Expression res = null;
                    ae.e1 = expressionSemantic(ae.e1, this.sc);
                    ae.e1 = resolveProperties(this.sc, ae.e1);
                    Expression ae1old = ae.e1;
                    boolean maybeSlice = ((ae.arguments).length == 0 || ((ae.arguments).length == 1 && ((ae.arguments).get(0).op & 0xFF) == 231));
                    IntervalExp ie = null;
                    if ((maybeSlice && ((ae.arguments).length) != 0))
                    {
                        assert(((ae.arguments).get(0).op & 0xFF) == 231);
                        ie = (IntervalExp)(ae.arguments).get(0);
                    }
                L_outer8:
                    for (; true;){
                        if ((ae.e1.op & 0xFF) == 127)
                            setResult.invoke(ae.e1, 7962);
                            return ;
                        Ref<Expression> e0 = ref(null);
                        Expression ae1save = ae.e1;
                        ae.lengthVar = null;
                        Type t1b = ae.e1.type.toBasetype();
                        AggregateDeclaration ad = isAggregate(t1b);
                        if (!(ad != null))
                            break;
                        try {
                            if (search_function(ad, Id.indexass) != null)
                            {
                                res = resolveOpDollar(this.sc, ae, ptr(e0));
                                if (!(res != null))
                                    /*goto Lfallback*/throw Dispatch0.INSTANCE;
                                if ((res.op & 0xFF) == 127)
                                    setResult.invoke(res, 7979);
                                    return ;
                                res = expressionSemantic(exp.e2, this.sc);
                                if ((res.op & 0xFF) == 127)
                                    setResult.invoke(res, 7983);
                                    return ;
                                exp.e2 = res;
                                DArray<Expression> a = (ae.arguments).copy();
                                (a).insert(0, exp.e2);
                                res = new DotIdExp(exp.loc, ae.e1, Id.indexass);
                                res = new CallExp(exp.loc, res, a);
                                if (maybeSlice)
                                    res = trySemantic(res, this.sc);
                                else
                                    res = expressionSemantic(res, this.sc);
                                if (res != null)
                                    setResult.invoke(Expression.combine(e0.value, res), 7998);
                                    return ;
                            }
                        }
                        catch(Dispatch0 __d){}
                    /*Lfallback:*/
                        if ((maybeSlice && search_function(ad, Id.sliceass) != null))
                        {
                            res = resolveOpDollar(this.sc, ae, ie, ptr(e0));
                            if ((res.op & 0xFF) == 127)
                                setResult.invoke(res, 8007);
                                return ;
                            res = expressionSemantic(exp.e2, this.sc);
                            if ((res.op & 0xFF) == 127)
                                setResult.invoke(res, 8011);
                                return ;
                            exp.e2 = res;
                            DArray<Expression> a = new DArray<Expression>();
                            (a).push(exp.e2);
                            if (ie != null)
                            {
                                (a).push(ie.lwr);
                                (a).push(ie.upr);
                            }
                            res = new DotIdExp(exp.loc, ae.e1, Id.sliceass);
                            res = new CallExp(exp.loc, res, a);
                            res = expressionSemantic(res, this.sc);
                            setResult.invoke(Expression.combine(e0.value, res), 8028);
                            return ;
                        }
                        if ((ad.aliasthis != null && !pequals(t1b, ae.att1)))
                        {
                            if ((!(ae.att1 != null) && t1b.checkAliasThisRec()))
                                ae.att1 = t1b;
                            ae.e1 = resolveAliasThis(this.sc, ae1save, true);
                            if (ae.e1 != null)
                                continue L_outer8;
                        }
                        break;
                    }
                    ae.e1 = ae1old;
                    ae.lengthVar = null;
                }
            }
            {
                Expression e1x = exp.e1;
                {
                    DotTemplateInstanceExp dti = e1x.isDotTemplateInstanceExp();
                    if (dti != null)
                    {
                        Expression e = semanticY(dti, this.sc, 1);
                        if (!(e != null))
                        {
                            setResult.invoke(resolveUFCSProperties(this.sc, e1x, exp.e2), 8067);
                            return ;
                        }
                        e1x = e;
                    }
                    else {
                        DotIdExp die = e1x.isDotIdExp();
                        if (die != null)
                        {
                            Expression e = semanticY(die, this.sc, 1);
                            if ((e != null && isDotOpDispatch(e)))
                            {
                                exp.e2 = expressionSemantic(exp.e2, this.sc);
                                int errors = global.startGagging();
                                e = resolvePropertiesX(this.sc, e, exp.e2);
                                if (global.endGagging(errors))
                                    e = null;
                                else
                                    setResult.invoke(e, 8090);
                                    return ;
                            }
                            if (!(e != null))
                                setResult.invoke(resolveUFCSProperties(this.sc, e1x, exp.e2), 8093);
                                return ;
                            e1x = e;
                        }
                        else
                        {
                            {
                                SliceExp se = e1x.isSliceExp();
                                if (se != null)
                                    se.arrayop = true;
                            }
                            e1x = expressionSemantic(e1x, this.sc);
                        }
                    }
                }
                {
                    Expression e = resolvePropertiesX(this.sc, e1x, exp.e2);
                    if (e != null)
                        setResult.invoke(e, 8111);
                        return ;
                }
                if (e1x.checkRightThis(this.sc))
                {
                    this.setError();
                    return ;
                }
                exp.e1 = e1x;
                assert(exp.e1.type != null);
            }
            Type t1 = exp.e1.type.toBasetype();
            {
                Expression e2x = inferType(exp.e2, t1.baseElemOf(), 0);
                e2x = expressionSemantic(e2x, this.sc);
                e2x = resolveProperties(this.sc, e2x);
                if ((e2x.op & 0xFF) == 20)
                    e2x = resolveAliasThis(this.sc, e2x, false);
                if ((e2x.op & 0xFF) == 127)
                    setResult.invoke(e2x, 8133);
                    return ;
                if (e2x.checkValue())
                    this.setError();
                    return ;
                exp.e2 = e2x;
            }
            {
                Expression e2x = exp.e2;
                while(true) try {
                /*Ltupleassign:*/
                    if (((exp.e1.op & 0xFF) == 126 && (e2x.op & 0xFF) == 126))
                    {
                        TupleExp tup1 = (TupleExp)exp.e1;
                        TupleExp tup2 = (TupleExp)e2x;
                        int dim = (tup1.exps).length;
                        Expression e = null;
                        if (dim != (tup2.exps).length)
                        {
                            exp.error(new BytePtr("mismatched tuple lengths, %d and %d"), dim, (tup2.exps).length);
                            this.setError();
                            return ;
                        }
                        if (dim == 0)
                        {
                            e = new IntegerExp(exp.loc, 0L, Type.tint32);
                            e = new CastExp(exp.loc, e, Type.tvoid);
                            e = Expression.combine(tup1.e0, tup2.e0, e);
                        }
                        else
                        {
                            DArray<Expression> exps = new DArray<Expression>(dim);
                            {
                                int i = 0;
                                for (; i < dim;i++){
                                    Expression ex1 = (tup1.exps).get(i);
                                    Expression ex2 = (tup2.exps).get(i);
                                    exps.set(i, new AssignExp(exp.loc, ex1, ex2));
                                }
                            }
                            e = new TupleExp(exp.loc, Expression.combine(tup1.e0, tup2.e0), exps);
                        }
                        setResult.invoke(expressionSemantic(e, this.sc), 8173);
                        return ;
                    }
                    try {
                        if ((exp.e1.op & 0xFF) == 126)
                        {
                            TupleDeclaration td = isAliasThisTuple(e2x);
                            if (!(td != null))
                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                            assert((exp.e1.type.ty & 0xFF) == ENUMTY.Ttuple);
                            TypeTuple tt = (TypeTuple)exp.e1.type;
                            Ref<Expression> e0 = ref(null);
                            Expression ev = extractSideEffect(this.sc, new BytePtr("__tup"), e0, e2x, false);
                            DArray<Expression> iexps = new DArray<Expression>();
                            (iexps).push(ev);
                            {
                                int u = 0;
                            L_outer9:
                                for (; u < (iexps).length;u++){
                                    while(true) try {
                                    /*Lexpand:*/
                                        Expression e = (iexps).get(u);
                                        Parameter arg = Parameter.getNth(tt.arguments, u, null);
                                        if ((!(arg != null) || !((e.type.implicitConvTo(arg.type)) != 0)))
                                        {
                                            if (expandAliasThisTuples(iexps, u) != -1)
                                            {
                                                if ((iexps).length <= u)
                                                    break;
                                                /*goto Lexpand*/throw Dispatch0.INSTANCE;
                                            }
                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                        }
                                        break;
                                    } catch(Dispatch0 __d){}
                                }
                            }
                            e2x = new TupleExp(e2x.loc, e0.value, iexps);
                            e2x = expressionSemantic(e2x, this.sc);
                            if ((e2x.op & 0xFF) == 127)
                            {
                                this.result = e2x;
                                return ;
                            }
                            /*goto Ltupleassign*/throw Dispatch0.INSTANCE;
                        }
                    }
                    catch(Dispatch1 __d){}
                /*Lnomatch:*/
                    break;
                } catch(Dispatch0 __d){}
            }
            if (((exp.op & 0xFF) == 90 && exp.e1.checkModifiable(this.sc, 0) == Modifiable.initialization))
            {
                Type t = exp.type;
                exp = new ConstructExp(exp.loc, exp.e1, exp.e2);
                exp.type = t;
                if ((((this.sc).func.isStaticCtorDeclaration() != null && !((this.sc).func.isSharedStaticCtorDeclaration() != null)) && exp.e1.type.isImmutable()))
                {
                    deprecation(exp.loc, new BytePtr("initialization of `immutable` variable from `static this` is deprecated."));
                    deprecationSupplemental(exp.loc, new BytePtr("Use `shared static this` instead."));
                }
                {
                    IndexExp ie1 = exp.e1.isIndexExp();
                    if (ie1 != null)
                    {
                        Expression e1x = ie1.markSettingAAElem();
                        if ((e1x.op & 0xFF) == 127)
                        {
                            this.result = e1x;
                            return ;
                        }
                    }
                }
            }
            else if ((((exp.op & 0xFF) == 95 && (exp.e1.op & 0xFF) == 26) && (((VarExp)exp.e1).var.storage_class & 2101248L) != 0))
            {
                exp.memset |= MemorySet.referenceInit;
            }
            if ((exp.memset & MemorySet.referenceInit) != 0)
            {
            }
            else if ((t1.ty & 0xFF) == ENUMTY.Tstruct)
            {
                Expression e1x = exp.e1;
                Expression e2x = exp.e2;
                StructDeclaration sd = ((TypeStruct)t1).sym;
                if ((exp.op & 0xFF) == 95)
                {
                    Type t2 = e2x.type.toBasetype();
                    if (((t2.ty & 0xFF) == ENUMTY.Tstruct && pequals(sd, ((TypeStruct)t2).sym)))
                    {
                        sd.size(exp.loc);
                        if (sd.sizeok != Sizeok.done)
                            this.setError();
                            return ;
                        if (!(sd.ctor != null))
                            sd.ctor = sd.searchCtor();
                        Expression e2y = lastComma(e2x);
                        CallExp ce = (e2y.op & 0xFF) == 18 ? (CallExp)e2y : null;
                        DotVarExp dve = (ce != null && (ce.e1.op & 0xFF) == 27) ? (DotVarExp)ce.e1 : null;
                        if ((((((sd.ctor != null && ce != null) && dve != null) && dve.var.isCtorDeclaration() != null) && (dve.e1.op & 0xFF) != 27) && (e2y.type.implicitConvTo(t1)) != 0))
                        {
                            Expression einit = this.getInitExp(sd, exp.loc, this.sc, t1);
                            if ((einit.op & 0xFF) == 127)
                            {
                                this.result = einit;
                                return ;
                            }
                            BlitExp ae = new BlitExp(exp.loc, exp.e1, einit);
                            ae.type = e1x.type;
                            DotVarExp dvx = (DotVarExp)dve.copy();
                            dvx.e1 = e1x;
                            CallExp cx = (CallExp)ce.copy();
                            cx.e1 = dvx;
                            if ((global.params.vsafe && checkConstructorEscape(this.sc, cx, false)))
                                this.setError();
                                return ;
                            Ref<Expression> e0 = ref(null);
                            Expression.extractLast(e2x, e0);
                            Expression e = Expression.combine(e0.value, (Expression)ae, (Expression)cx);
                            e = expressionSemantic(e, this.sc);
                            this.result = e;
                            return ;
                        }
                        if ((sd.postblit != null || sd.hasCopyCtor))
                        {
                            if ((e2x.op & 0xFF) == 100)
                            {
                                CondExp econd = (CondExp)e2x;
                                Expression ea1 = new ConstructExp(econd.e1.loc, e1x, econd.e1);
                                Expression ea2 = new ConstructExp(econd.e1.loc, e1x, econd.e2);
                                Expression e = new CondExp(exp.loc, econd.econd, ea1, ea2);
                                this.result = expressionSemantic(e, this.sc);
                                return ;
                            }
                            if (e2x.isLvalue())
                            {
                                if (sd.hasCopyCtor)
                                {
                                    Expression einit = new BlitExp(exp.loc, exp.e1, this.getInitExp(sd, exp.loc, this.sc, t1));
                                    einit.type = e1x.type;
                                    Expression e = null;
                                    e = new DotIdExp(exp.loc, e1x, Id.ctor);
                                    e = new CallExp(exp.loc, e, e2x);
                                    e = new CommaExp(exp.loc, einit, e, true);
                                    this.result = expressionSemantic(e, this.sc);
                                    return ;
                                }
                                else
                                {
                                    if (!((e2x.type.implicitConvTo(e1x.type)) != 0))
                                    {
                                        exp.error(new BytePtr("conversion error from `%s` to `%s`"), e2x.type.toChars(), e1x.type.toChars());
                                        this.setError();
                                        return ;
                                    }
                                    Expression e = e1x.copy();
                                    e.type = e.type.mutableOf();
                                    if ((e.type.isShared() && !(sd.type.isShared())))
                                        e.type = e.type.unSharedOf();
                                    e = new BlitExp(exp.loc, e, e2x);
                                    e = new DotVarExp(exp.loc, e, sd.postblit, false);
                                    e = new CallExp(exp.loc, e);
                                    this.result = expressionSemantic(e, this.sc);
                                    return ;
                                }
                            }
                            else
                            {
                                e2x = valueNoDtor(e2x);
                            }
                        }
                        if (!((e2x.implicitConvTo(t1)) != 0))
                        {
                            AggregateDeclaration ad2 = isAggregate(e2x.type);
                            if (((ad2 != null && ad2.aliasthis != null) && !((exp.att2 != null && pequals(e2x.type, exp.att2)))))
                            {
                                if ((!(exp.att2 != null) && exp.e2.type.checkAliasThisRec()))
                                    exp.att2 = exp.e2.type;
                                exp.e2 = new DotIdExp(exp.e2.loc, exp.e2, ad2.aliasthis.ident);
                                this.result = expressionSemantic(exp, this.sc);
                                return ;
                            }
                        }
                    }
                    else if (!((e2x.implicitConvTo(t1)) != 0))
                    {
                        sd.size(exp.loc);
                        if (sd.sizeok != Sizeok.done)
                            this.setError();
                            return ;
                        if (!(sd.ctor != null))
                            sd.ctor = sd.searchCtor();
                        if (sd.ctor != null)
                        {
                            if ((exp.e2.op & 0xFF) == 22)
                            {
                                NewExp newExp = (NewExp)exp.e2;
                                if ((newExp.newtype != null && pequals(newExp.newtype, t1)))
                                {
                                    error(exp.loc, new BytePtr("cannot implicitly convert expression `%s` of type `%s` to `%s`"), newExp.toChars(), newExp.type.toChars(), t1.toChars());
                                    errorSupplemental(exp.loc, new BytePtr("Perhaps remove the `new` keyword?"));
                                    this.setError();
                                    return ;
                                }
                            }
                            Expression einit = new BlitExp(exp.loc, e1x, this.getInitExp(sd, exp.loc, this.sc, t1));
                            einit.type = e1x.type;
                            Expression e = null;
                            e = new DotIdExp(exp.loc, e1x, Id.ctor);
                            e = new CallExp(exp.loc, e, e2x);
                            e = new CommaExp(exp.loc, einit, e, true);
                            e = expressionSemantic(e, this.sc);
                            this.result = e;
                            return ;
                        }
                        if (search_function(sd, Id.call) != null)
                        {
                            e2x = typeDotIdExp(e2x.loc, e1x.type, Id.call);
                            e2x = new CallExp(exp.loc, e2x, exp.e2);
                            e2x = expressionSemantic(e2x, this.sc);
                            e2x = resolveProperties(this.sc, e2x);
                            if ((e2x.op & 0xFF) == 127)
                            {
                                this.result = e2x;
                                return ;
                            }
                            if (e2x.checkValue())
                                this.setError();
                                return ;
                        }
                    }
                    else
                    {
                        AggregateDeclaration ad2 = isAggregate(e2x.type);
                        if (((ad2 != null && ad2.aliasthis != null) && !((exp.att2 != null && pequals(e2x.type, exp.att2)))))
                        {
                            if ((!(exp.att2 != null) && exp.e2.type.checkAliasThisRec()))
                                exp.att2 = exp.e2.type;
                            exp.e2 = new DotIdExp(exp.e2.loc, exp.e2, ad2.aliasthis.ident);
                            this.result = expressionSemantic(exp, this.sc);
                            return ;
                        }
                    }
                }
                else if ((exp.op & 0xFF) == 90)
                {
                    if (((e1x.op & 0xFF) == 62 && (((IndexExp)e1x).e1.type.toBasetype().ty & 0xFF) == ENUMTY.Taarray))
                    {
                        IndexExp ie = (IndexExp)e1x;
                        Type t2 = e2x.type.toBasetype();
                        Ref<Expression> e0 = ref(null);
                        Expression ea = extractSideEffect(this.sc, new BytePtr("__aatmp"), e0, ie.e1, false);
                        Expression ek = extractSideEffect(this.sc, new BytePtr("__aakey"), e0, ie.e2, false);
                        Expression ev = extractSideEffect(this.sc, new BytePtr("__aaval"), e0, e2x, false);
                        AssignExp ae = (AssignExp)exp.copy();
                        ae.e1 = new IndexExp(exp.loc, ea, ek);
                        ae.e1 = expressionSemantic(ae.e1, this.sc);
                        ae.e1 = ae.e1.optimize(0, false);
                        ae.e2 = ev;
                        Expression e = op_overload(ae, this.sc, null);
                        if (e != null)
                        {
                            Ref<Expression> ey = ref(null);
                            if (((t2.ty & 0xFF) == ENUMTY.Tstruct && pequals(sd, t2.toDsymbol(this.sc))))
                            {
                                ey.value = ev;
                            }
                            else if ((!((ev.implicitConvTo(ie.type)) != 0) && sd.ctor != null))
                            {
                                ey.value = new StructLiteralExp(exp.loc, sd, null, null);
                                ey.value = new DotIdExp(exp.loc, ey.value, Id.ctor);
                                ey.value = new CallExp(exp.loc, ey.value, ev);
                                ey.value = trySemantic(ey.value, this.sc);
                            }
                            if (ey.value != null)
                            {
                                Ref<Expression> ex = ref(null);
                                ex.value = new IndexExp(exp.loc, ea, ek);
                                ex.value = expressionSemantic(ex.value, this.sc);
                                ex.value = ex.value.optimize(0, false);
                                ex.value = ex.value.modifiableLvalue(this.sc, ex.value);
                                ey.value = new ConstructExp(exp.loc, ex.value, ey.value);
                                ey.value = expressionSemantic(ey.value, this.sc);
                                if ((ey.value.op & 0xFF) == 127)
                                {
                                    this.result = ey.value;
                                    return ;
                                }
                                ex.value = e;
                                Ref<Type> t = ref(null);
                                if (!(typeMerge(this.sc, TOK.question, ptr(t), ptr(ex), ptr(ey))))
                                {
                                    ex.value = new CastExp(ex.value.loc, ex.value, Type.tvoid);
                                    ey.value = new CastExp(ey.value.loc, ey.value, Type.tvoid);
                                }
                                e = new CondExp(exp.loc, new InExp(exp.loc, ek, ea), ex.value, ey.value);
                            }
                            e = Expression.combine(e0.value, e);
                            e = expressionSemantic(e, this.sc);
                            this.result = e;
                            return ;
                        }
                    }
                    else
                    {
                        Expression e = op_overload(exp, this.sc, null);
                        if (e != null)
                        {
                            this.result = e;
                            return ;
                        }
                    }
                }
                else
                    assert((exp.op & 0xFF) == 96);
                exp.e1 = e1x;
                exp.e2 = e2x;
            }
            else if ((t1.ty & 0xFF) == ENUMTY.Tclass)
            {
                if (((exp.op & 0xFF) == 90 && !((exp.e2.implicitConvTo(exp.e1.type)) != 0)))
                {
                    Expression e = op_overload(exp, this.sc, null);
                    if (e != null)
                    {
                        this.result = e;
                        return ;
                    }
                }
            }
            else if ((t1.ty & 0xFF) == ENUMTY.Tsarray)
            {
                assert((exp.e1.op & 0xFF) != 31);
                Expression e1x = exp.e1;
                Expression e2x = exp.e2;
                if ((e2x.implicitConvTo(e1x.type)) != 0)
                {
                    if (((exp.op & 0xFF) != 96 && ((((e2x.op & 0xFF) == 31 && ((UnaExp)e2x).e1.isLvalue()) || ((e2x.op & 0xFF) == 12 && ((UnaExp)e2x).e1.isLvalue())) || ((e2x.op & 0xFF) != 31 && e2x.isLvalue()))))
                    {
                        if (e1x.checkPostblit(this.sc, t1))
                            this.setError();
                            return ;
                    }
                    if ((isUnaArrayOp(e2x.op) || isBinArrayOp(e2x.op)))
                    {
                        SliceExp sle = new SliceExp(e1x.loc, e1x, null, null);
                        sle.arrayop = true;
                        e1x = expressionSemantic(sle, this.sc);
                    }
                    else
                    {
                    }
                }
                else
                {
                    if (e2x.implicitConvTo(t1.nextOf().arrayOf()) > MATCH.nomatch)
                    {
                        long dim1 = ((TypeSArray)t1).dim.toInteger();
                        long dim2 = dim1;
                        {
                            ArrayLiteralExp ale = e2x.isArrayLiteralExp();
                            if (ale != null)
                            {
                                dim2 = ale.elements != null ? (long)(ale.elements).length : 0L;
                            }
                            else {
                                SliceExp se = e2x.isSliceExp();
                                if (se != null)
                                {
                                    Type tx = toStaticArrayType(se);
                                    if (tx != null)
                                        dim2 = ((TypeSArray)tx).dim.toInteger();
                                }
                            }
                        }
                        if (dim1 != dim2)
                        {
                            exp.error(new BytePtr("mismatched array lengths, %d and %d"), (int)dim1, (int)dim2);
                            this.setError();
                            return ;
                        }
                    }
                    if ((exp.op & 0xFF) != 90)
                    {
                        int dim = t1.numberOfElems(exp.loc);
                        e1x.type = t1.baseElemOf().sarrayOf((long)dim);
                    }
                    SliceExp sle = new SliceExp(e1x.loc, e1x, null, null);
                    sle.arrayop = true;
                    e1x = expressionSemantic(sle, this.sc);
                }
                if ((e1x.op & 0xFF) == 127)
                    setResult.invoke(e1x, 8691);
                    return ;
                if ((e2x.op & 0xFF) == 127)
                    setResult.invoke(e2x, 8693);
                    return ;
                exp.e1 = e1x;
                exp.e2 = e2x;
                t1 = e1x.type.toBasetype();
            }
            {
                ArrayLengthExp ale = exp.e1.isArrayLengthExp();
                if (ale != null)
                {
                    Expression ale1x = ale.e1.modifiableLvalue(this.sc, exp.e1);
                    if ((ale1x.op & 0xFF) == 127)
                        setResult.invoke(ale1x, 8707);
                        return ;
                    ale.e1 = ale1x;
                    Type tn = ale.e1.type.toBasetype().nextOf();
                    checkDefCtor(ale.loc, tn);
                    semanticTypeInfo(this.sc, tn);
                }
                else {
                    SliceExp se = exp.e1.isSliceExp();
                    if (se != null)
                    {
                        Type tn = se.type.nextOf();
                        FuncDeclaration fun = (this.sc).func;
                        if ((((exp.op & 0xFF) == 90 && !(tn.isMutable())) && (!(fun != null) || (fun != null && !(fun.isStaticCtorDeclaration() != null)))))
                        {
                            exp.error(new BytePtr("slice `%s` is not mutable"), se.toChars());
                            this.setError();
                            return ;
                        }
                        if (((exp.op & 0xFF) == 90 && !(tn.baseElemOf().isAssignable())))
                        {
                            exp.error(new BytePtr("slice `%s` is not mutable, struct `%s` has immutable members"), exp.e1.toChars(), tn.baseElemOf().toChars());
                            this.result = new ErrorExp();
                            return ;
                        }
                        for (; (se.e1.op & 0xFF) == 31;) {
                            se = (SliceExp)se.e1;
                        }
                        if (((se.e1.op & 0xFF) == 100 && (se.e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            se.e1 = se.e1.modifiableLvalue(this.sc, exp.e1);
                            if ((se.e1.op & 0xFF) == 127)
                                setResult.invoke(se.e1, 8742);
                                return ;
                        }
                    }
                    else
                    {
                        if (((t1.ty & 0xFF) == ENUMTY.Tsarray && (exp.op & 0xFF) == 90))
                        {
                            Type tn = exp.e1.type.nextOf();
                            if ((tn != null && !(tn.baseElemOf().isAssignable())))
                            {
                                exp.error(new BytePtr("array `%s` is not mutable, struct `%s` has immutable members"), exp.e1.toChars(), tn.baseElemOf().toChars());
                                this.result = new ErrorExp();
                                return ;
                            }
                        }
                        Expression e1x = exp.e1;
                        if ((exp.op & 0xFF) == 90)
                            e1x = e1x.modifiableLvalue(this.sc, e1old);
                        if ((e1x.op & 0xFF) != 26)
                            e1x = e1x.optimize(0, false);
                        if ((e1x.op & 0xFF) == 127)
                        {
                            this.result = e1x;
                            return ;
                        }
                        exp.e1 = e1x;
                    }
                }
            }
            Expression e2x = exp.e2;
            Type t2 = e2x.type.toBasetype();
            Type telem = t1;
            for (; (telem.ty & 0xFF) == ENUMTY.Tarray;) {
                telem = telem.nextOf();
            }
            if (((((exp.e1.op & 0xFF) == 31 && t1.nextOf() != null) && ((telem.ty & 0xFF) != ENUMTY.Tvoid || (e2x.op & 0xFF) == 13)) && (e2x.implicitConvTo(t1.nextOf())) != 0))
            {
                exp.memset |= MemorySet.blockAssign;
                e2x = e2x.implicitCastTo(this.sc, t1.nextOf());
                if ((((exp.op & 0xFF) != 96 && e2x.isLvalue()) && exp.e1.checkPostblit(this.sc, t1.nextOf())))
                    this.setError();
                    return ;
            }
            else if ((((exp.e1.op & 0xFF) == 31 && ((t2.ty & 0xFF) == ENUMTY.Tarray || (t2.ty & 0xFF) == ENUMTY.Tsarray)) && (t2.nextOf().implicitConvTo(t1.nextOf())) != 0))
            {
                SliceExp se1 = (SliceExp)exp.e1;
                TypeSArray tsa1 = (TypeSArray)toStaticArrayType(se1);
                TypeSArray tsa2 = null;
                {
                    ArrayLiteralExp ale = e2x.isArrayLiteralExp();
                    if (ale != null)
                        tsa2 = (TypeSArray)t2.nextOf().sarrayOf((long)(ale.elements).length);
                    else {
                        SliceExp se = e2x.isSliceExp();
                        if (se != null)
                            tsa2 = (TypeSArray)toStaticArrayType(se);
                        else
                            tsa2 = t2.isTypeSArray();
                    }
                }
                if ((tsa1 != null && tsa2 != null))
                {
                    long dim1 = tsa1.dim.toInteger();
                    long dim2 = tsa2.dim.toInteger();
                    if (dim1 != dim2)
                    {
                        exp.error(new BytePtr("mismatched array lengths, %d and %d"), (int)dim1, (int)dim2);
                        this.setError();
                        return ;
                    }
                }
                if (((exp.op & 0xFF) != 96 && ((((e2x.op & 0xFF) == 31 && ((UnaExp)e2x).e1.isLvalue()) || ((e2x.op & 0xFF) == 12 && ((UnaExp)e2x).e1.isLvalue())) || ((e2x.op & 0xFF) != 31 && e2x.isLvalue()))))
                {
                    if (exp.e1.checkPostblit(this.sc, t1.nextOf()))
                        this.setError();
                        return ;
                }
                if (false)
                {
                    BytePtr e1str = pcopy(exp.e1.toChars());
                    BytePtr e2str = pcopy(e2x.toChars());
                    exp.warning(new BytePtr("explicit element-wise assignment `%s = (%s)[]` is better than `%s = %s`"), e1str, e2str, e1str, e2str);
                }
                Type t2n = t2.nextOf();
                Type t1n = t1.nextOf();
                IntRef offset = ref(0);
                if ((t2n.equivalent(t1n) || (t1n.isBaseOf(t2n, ptr(offset)) && offset.value == 0)))
                {
                    if (isArrayOpValid(e2x))
                    {
                        e2x = e2x.copy();
                        e2x.type = exp.e1.type.constOf();
                    }
                    else
                        e2x = e2x.castTo(this.sc, exp.e1.type.constOf());
                }
                else
                {
                    if ((e2x.op & 0xFF) == 121)
                        e2x = e2x.implicitCastTo(this.sc, exp.e1.type.constOf());
                    else
                        e2x = e2x.implicitCastTo(this.sc, exp.e1.type);
                }
                if (((t1n.toBasetype().ty & 0xFF) == ENUMTY.Tvoid && (t2n.toBasetype().ty & 0xFF) == ENUMTY.Tvoid))
                {
                    if ((((!(((this.sc).intypeof) != 0) && (this.sc).func != null) && (this.sc).func.setUnsafe()) && !(((this.sc).flags & 8) != 0)))
                    {
                        exp.error(new BytePtr("cannot copy `void[]` to `void[]` in `@safe` code"));
                        this.setError();
                        return ;
                    }
                }
            }
            else
            {
                if (false)
                {
                    BytePtr e1str = pcopy(exp.e1.toChars());
                    BytePtr e2str = pcopy(e2x.toChars());
                    BytePtr atypestr = pcopy((exp.e1.op & 0xFF) == 31 ? new BytePtr("element-wise") : new BytePtr("slice"));
                    exp.warning(new BytePtr("explicit %s assignment `%s = (%s)[]` is better than `%s = %s`"), atypestr, e1str, e2str, e1str, e2str);
                }
                if ((exp.op & 0xFF) == 96)
                    e2x = e2x.castTo(this.sc, exp.e1.type);
                else
                {
                    e2x = e2x.implicitCastTo(this.sc, exp.e1.type);
                    if ((((e2x.op & 0xFF) == 127 && (exp.op & 0xFF) == 95) && (t1.ty & 0xFF) == ENUMTY.Tstruct))
                    {
                        StructDeclaration sd = ((TypeStruct)t1).sym;
                        Dsymbol opAssign = search_function(sd, Id.assign);
                        if (opAssign != null)
                        {
                            errorSupplemental(exp.loc, new BytePtr("`%s` is the first assignment of `%s` therefore it represents its initialization"), exp.toChars(), exp.e1.toChars());
                            errorSupplemental(exp.loc, new BytePtr("`opAssign` methods are not used for initialization, but for subsequent assignments"));
                        }
                    }
                }
            }
            if ((e2x.op & 0xFF) == 127)
            {
                this.result = e2x;
                return ;
            }
            exp.e2 = e2x;
            t2 = exp.e2.type.toBasetype();
            if ((((t2.ty & 0xFF) == ENUMTY.Tarray || (t2.ty & 0xFF) == ENUMTY.Tsarray) && isArrayOpValid(exp.e2)))
            {
                if (((!((exp.memset & MemorySet.blockAssign) != 0) && (exp.e1.op & 0xFF) == 31) && (isUnaArrayOp(exp.e2.op) || isBinArrayOp(exp.e2.op))))
                {
                    exp.type = exp.e1.type;
                    if ((exp.op & 0xFF) == 95)
                        exp.e1.type = exp.e1.type.nextOf().mutableOf().arrayOf();
                    this.result = arrayOp((BinExp)exp, this.sc);
                    return ;
                }
                if (checkNonAssignmentArrayOp(exp.e2, (!((exp.memset & MemorySet.blockAssign) != 0) && (exp.op & 0xFF) == 90)))
                    this.setError();
                    return ;
            }
            if (((exp.e1.op & 0xFF) == 26 && (exp.op & 0xFF) == 90))
            {
                VarExp ve = (VarExp)exp.e1;
                VarDeclaration vd = ve.var.isVarDeclaration();
                if ((vd != null && (vd.onstack || vd.mynew)))
                {
                    assert((t1.ty & 0xFF) == ENUMTY.Tclass);
                    exp.error(new BytePtr("cannot rebind scope variables"));
                }
            }
            if (((exp.e1.op & 0xFF) == 26 && pequals(((VarExp)exp.e1).var.ident, Id.ctfe)))
            {
                exp.error(new BytePtr("cannot modify compiler-generated variable `__ctfe`"));
            }
            exp.type = exp.e1.type;
            assert(exp.type != null);
            Expression res = (exp.op & 0xFF) == 90 ? exp.reorderSettingAAElem(this.sc) : exp;
            checkAssignEscape(this.sc, res, false);
            setResult.invoke(res, 9001);
            return ;
        }

        public  void visit(PowAssignExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            if (exp.e1.checkReadModifyWrite(exp.op, exp.e2))
                this.setError();
                return ;
            assert((exp.e1.type != null && exp.e2.type != null));
            if ((((exp.e1.op & 0xFF) == 31 || (exp.e1.type.ty & 0xFF) == ENUMTY.Tarray) || (exp.e1.type.ty & 0xFF) == ENUMTY.Tsarray))
            {
                if (checkNonAssignmentArrayOp(exp.e1, false))
                    this.setError();
                    return ;
                if ((exp.e2.implicitConvTo(exp.e1.type.nextOf())) != 0)
                {
                    exp.e2 = exp.e2.castTo(this.sc, exp.e1.type.nextOf());
                }
                else {
                    Expression ex = typeCombine(exp, this.sc);
                    if (ex != null)
                    {
                        this.result = ex;
                        return ;
                    }
                }
                Type tb1 = exp.e1.type.nextOf().toBasetype();
                Type tb2 = exp.e2.type.toBasetype();
                if (((tb2.ty & 0xFF) == ENUMTY.Tarray || (tb2.ty & 0xFF) == ENUMTY.Tsarray))
                    tb2 = tb2.nextOf().toBasetype();
                if (((tb1.isintegral() || tb1.isfloating()) && (tb2.isintegral() || tb2.isfloating())))
                {
                    exp.type = exp.e1.type;
                    this.result = arrayOp(exp, this.sc);
                    return ;
                }
            }
            else
            {
                exp.e1 = exp.e1.modifiableLvalue(this.sc, exp.e1);
            }
            if (((exp.e1.type.isintegral() || exp.e1.type.isfloating()) && (exp.e2.type.isintegral() || exp.e2.type.isfloating())))
            {
                Ref<Expression> e0 = ref(null);
                e = exp.reorderSettingAAElem(this.sc);
                e = Expression.extractLast(e, e0);
                assert(pequals(e, exp));
                if ((exp.e1.op & 0xFF) == 26)
                {
                    e = new PowExp(exp.loc, exp.e1.syntaxCopy(), exp.e2);
                    e = new AssignExp(exp.loc, exp.e1, e);
                }
                else
                {
                    VarDeclaration v = copyToTemp(2097152L, new BytePtr("__powtmp"), exp.e1);
                    DeclarationExp de = new DeclarationExp(exp.e1.loc, v);
                    VarExp ve = new VarExp(exp.e1.loc, v, true);
                    e = new PowExp(exp.loc, ve, exp.e2);
                    e = new AssignExp(exp.loc, new VarExp(exp.e1.loc, v, true), e);
                    e = new CommaExp(exp.loc, de, e, true);
                }
                e = Expression.combine(e0.value, e);
                e = expressionSemantic(e, this.sc);
                this.result = e;
                return ;
            }
            this.result = exp.incompatibleTypes();
        }

        public  void visit(CatAssignExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            if ((exp.e1.op & 0xFF) == 31)
            {
                SliceExp se = (SliceExp)exp.e1;
                if ((se.e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray)
                {
                    exp.error(new BytePtr("cannot append to static array `%s`"), se.e1.type.toChars());
                    this.setError();
                    return ;
                }
            }
            exp.e1 = exp.e1.modifiableLvalue(this.sc, exp.e1);
            if ((exp.e1.op & 0xFF) == 127)
            {
                this.result = exp.e1;
                return ;
            }
            if ((exp.e2.op & 0xFF) == 127)
            {
                this.result = exp.e2;
                return ;
            }
            if (checkNonAssignmentArrayOp(exp.e2, false))
                this.setError();
                return ;
            Type tb1 = exp.e1.type.toBasetype();
            Type tb1next = tb1.nextOf();
            Type tb2 = exp.e2.type.toBasetype();
            if ((((tb1.ty & 0xFF) == ENUMTY.Tarray && ((tb2.ty & 0xFF) == ENUMTY.Tarray || (tb2.ty & 0xFF) == ENUMTY.Tsarray)) && ((exp.e2.implicitConvTo(exp.e1.type)) != 0 || ((tb2.nextOf().implicitConvTo(tb1next)) != 0 && tb2.nextOf().size(Loc.initial) == tb1next.size(Loc.initial)))))
            {
                assert((exp.op & 0xFF) == 71);
                if (exp.e1.checkPostblit(this.sc, tb1next))
                    this.setError();
                    return ;
                exp.e2 = exp.e2.castTo(this.sc, exp.e1.type);
            }
            else if (((tb1.ty & 0xFF) == ENUMTY.Tarray && (exp.e2.implicitConvTo(tb1next)) != 0))
            {
                if (((tb2.ty & 0xFF) == ENUMTY.Tstruct && (((TypeStruct)tb2).implicitConvToThroughAliasThis(tb1next)) != 0))
                    /*goto Laliasthis*//*unrolled goto*/
                    exp = new CatDcharAssignExp(exp.loc, exp.type, exp.e1, exp.e2.castTo(this.sc, Type.tdchar));
                if (((tb2.ty & 0xFF) == ENUMTY.Tclass && (((TypeClass)tb2).implicitConvToThroughAliasThis(tb1next)) != 0))
                    /*goto Laliasthis*//*unrolled goto*/
                    exp = new CatDcharAssignExp(exp.loc, exp.type, exp.e1, exp.e2.castTo(this.sc, Type.tdchar));
                if (exp.e2.checkPostblit(this.sc, tb2))
                    this.setError();
                    return ;
                if (checkNewEscape(this.sc, exp.e2, false))
                    this.setError();
                    return ;
                exp = new CatElemAssignExp(exp.loc, exp.type, exp.e1, exp.e2.castTo(this.sc, tb1next));
                exp.e2 = doCopyOrMove(this.sc, exp.e2, null);
            }
            else if (((((tb1.ty & 0xFF) == ENUMTY.Tarray && ((tb1next.ty & 0xFF) == ENUMTY.Tchar || (tb1next.ty & 0xFF) == ENUMTY.Twchar)) && (exp.e2.type.ty & 0xFF) != (tb1next.ty & 0xFF)) && (exp.e2.implicitConvTo(Type.tdchar)) != 0))
            {
                exp = new CatDcharAssignExp(exp.loc, exp.type, exp.e1, exp.e2.castTo(this.sc, Type.tdchar));
            }
            else
            {
                Function2<BinAssignExp,Scope,Expression> tryAliasThisForLhs = new Function2<BinAssignExp,Scope,Expression>(){
                    public Expression invoke(BinAssignExp exp, Scope sc){
                        AggregateDeclaration ad1 = isAggregate(exp.e1.type);
                        if ((!(ad1 != null) || !(ad1.aliasthis != null)))
                            return null;
                        if ((exp.att1 != null && pequals(exp.e1.type, exp.att1)))
                            return null;
                        Expression e1 = new DotIdExp(exp.loc, exp.e1, ad1.aliasthis.ident);
                        BinExp be = (BinExp)exp.copy();
                        if ((!(be.att1 != null) && exp.e1.type.checkAliasThisRec()))
                            be.att1 = exp.e1.type;
                        be.e1 = e1;
                        return trySemantic(be, sc);
                    }
                };
                Function2<BinAssignExp,Scope,Expression> tryAliasThisForRhs = new Function2<BinAssignExp,Scope,Expression>(){
                    public Expression invoke(BinAssignExp exp, Scope sc){
                        AggregateDeclaration ad2 = isAggregate(exp.e2.type);
                        if ((!(ad2 != null) || !(ad2.aliasthis != null)))
                            return null;
                        if ((exp.att2 != null && pequals(exp.e2.type, exp.att2)))
                            return null;
                        Expression e2 = new DotIdExp(exp.loc, exp.e2, ad2.aliasthis.ident);
                        BinExp be = (BinExp)exp.copy();
                        if ((!(be.att2 != null) && exp.e2.type.checkAliasThisRec()))
                            be.att2 = exp.e2.type;
                        be.e2 = e2;
                        return trySemantic(be, sc);
                    }
                };
            /*Laliasthis:*/
                this.result = tryAliasThisForLhs.invoke(exp, this.sc);
                if (this.result != null)
                    return ;
                this.result = tryAliasThisForRhs.invoke(exp, this.sc);
                if (this.result != null)
                    return ;
                exp.error(new BytePtr("cannot append type `%s` to type `%s`"), tb2.toChars(), tb1.toChars());
                this.setError();
                return ;
            }
            if (exp.e2.checkValue())
                this.setError();
                return ;
            exp.type = exp.e1.type;
            Expression res = exp.reorderSettingAAElem(this.sc);
            if ((((exp.op & 0xFF) == 72 || (exp.op & 0xFF) == 73) && global.params.vsafe))
                checkAssignEscape(this.sc, res, false);
            this.result = res;
        }

        public  void visit(AddExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            Type tb1 = exp.e1.type.toBasetype();
            Type tb2 = exp.e2.type.toBasetype();
            boolean err = false;
            if (((tb1.ty & 0xFF) == ENUMTY.Tdelegate || ((tb1.ty & 0xFF) == ENUMTY.Tpointer && (tb1.nextOf().ty & 0xFF) == ENUMTY.Tfunction)))
            {
                (err ? 1 : 0) |= (exp.e1.checkArithmetic() ? 1 : 0);
            }
            if (((tb2.ty & 0xFF) == ENUMTY.Tdelegate || ((tb2.ty & 0xFF) == ENUMTY.Tpointer && (tb2.nextOf().ty & 0xFF) == ENUMTY.Tfunction)))
            {
                (err ? 1 : 0) |= (exp.e2.checkArithmetic() ? 1 : 0);
            }
            if (err)
                this.setError();
                return ;
            if ((((tb1.ty & 0xFF) == ENUMTY.Tpointer && exp.e2.type.isintegral()) || ((tb2.ty & 0xFF) == ENUMTY.Tpointer && exp.e1.type.isintegral())))
            {
                this.result = scaleFactor(exp, this.sc);
                return ;
            }
            if (((tb1.ty & 0xFF) == ENUMTY.Tpointer && (tb2.ty & 0xFF) == ENUMTY.Tpointer))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            {
                Expression ex = typeCombine(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Type tb = exp.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tarray || (tb.ty & 0xFF) == ENUMTY.Tsarray))
            {
                if (!(isArrayOpValid(exp)))
                {
                    this.result = arrayOpInvalidError(exp);
                    return ;
                }
                this.result = exp;
                return ;
            }
            tb1 = exp.e1.type.toBasetype();
            if (!(target.isVectorOpSupported(tb1, exp.op, tb2)))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            if (((tb1.isreal() && exp.e2.type.isimaginary()) || (tb1.isimaginary() && exp.e2.type.isreal())))
            {
                switch ((exp.type.toBasetype().ty & 0xFF))
                {
                    case 21:
                    case 24:
                        exp.type = Type.tcomplex32;
                        break;
                    case 22:
                    case 25:
                        exp.type = Type.tcomplex64;
                        break;
                    case 23:
                    case 26:
                        exp.type = Type.tcomplex80;
                        break;
                    default:
                    throw new AssertionError("Unreachable code!");
                }
            }
            this.result = exp;
        }

        public  void visit(MinExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            Type t1 = exp.e1.type.toBasetype();
            Type t2 = exp.e2.type.toBasetype();
            boolean err = false;
            if (((t1.ty & 0xFF) == ENUMTY.Tdelegate || ((t1.ty & 0xFF) == ENUMTY.Tpointer && (t1.nextOf().ty & 0xFF) == ENUMTY.Tfunction)))
            {
                (err ? 1 : 0) |= (exp.e1.checkArithmetic() ? 1 : 0);
            }
            if (((t2.ty & 0xFF) == ENUMTY.Tdelegate || ((t2.ty & 0xFF) == ENUMTY.Tpointer && (t2.nextOf().ty & 0xFF) == ENUMTY.Tfunction)))
            {
                (err ? 1 : 0) |= (exp.e2.checkArithmetic() ? 1 : 0);
            }
            if (err)
                this.setError();
                return ;
            if ((t1.ty & 0xFF) == ENUMTY.Tpointer)
            {
                if ((t2.ty & 0xFF) == ENUMTY.Tpointer)
                {
                    Type p1 = t1.nextOf();
                    Type p2 = t2.nextOf();
                    if (!(p1.equivalent(p2)))
                    {
                        deprecation(exp.loc, new BytePtr("cannot subtract pointers to different types: `%s` and `%s`."), t1.toChars(), t2.toChars());
                    }
                    long stride = 0L;
                    {
                        Expression ex = typeCombine(exp, this.sc);
                        if (ex != null)
                        {
                            this.result = ex;
                            return ;
                        }
                    }
                    exp.type = Type.tptrdiff_t;
                    stride = (long)t2.nextOf().size();
                    if (stride == 0L)
                    {
                        e = new IntegerExp(exp.loc, 0L, Type.tptrdiff_t);
                    }
                    else
                    {
                        e = new DivExp(exp.loc, exp, new IntegerExp(Loc.initial, (long)stride, Type.tptrdiff_t));
                        e.type = Type.tptrdiff_t;
                    }
                }
                else if (t2.isintegral())
                    e = scaleFactor(exp, this.sc);
                else
                {
                    exp.error(new BytePtr("can't subtract `%s` from pointer"), t2.toChars());
                    e = new ErrorExp();
                }
                this.result = e;
                return ;
            }
            if ((t2.ty & 0xFF) == ENUMTY.Tpointer)
            {
                exp.type = exp.e2.type;
                exp.error(new BytePtr("can't subtract pointer from `%s`"), exp.e1.type.toChars());
                this.setError();
                return ;
            }
            {
                Expression ex = typeCombine(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Type tb = exp.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tarray || (tb.ty & 0xFF) == ENUMTY.Tsarray))
            {
                if (!(isArrayOpValid(exp)))
                {
                    this.result = arrayOpInvalidError(exp);
                    return ;
                }
                this.result = exp;
                return ;
            }
            t1 = exp.e1.type.toBasetype();
            t2 = exp.e2.type.toBasetype();
            if (!(target.isVectorOpSupported(t1, exp.op, t2)))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            if (((t1.isreal() && t2.isimaginary()) || (t1.isimaginary() && t2.isreal())))
            {
                switch ((exp.type.ty & 0xFF))
                {
                    case 21:
                    case 24:
                        exp.type = Type.tcomplex32;
                        break;
                    case 22:
                    case 25:
                        exp.type = Type.tcomplex64;
                        break;
                    case 23:
                    case 26:
                        exp.type = Type.tcomplex80;
                        break;
                    default:
                    throw new AssertionError("Unreachable code!");
                }
            }
            this.result = exp;
            return ;
        }

        public  void visit(CatExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            Type tb1 = exp.e1.type.toBasetype();
            Type tb2 = exp.e2.type.toBasetype();
            boolean f1 = checkNonAssignmentArrayOp(exp.e1, false);
            boolean f2 = checkNonAssignmentArrayOp(exp.e2, false);
            if ((f1 || f2))
                this.setError();
                return ;
            Type tb1next = tb1.nextOf();
            Type tb2next = tb2.nextOf();
            try {
                if (((tb1next != null && tb2next != null) && (((tb1next.implicitConvTo(tb2next) >= MATCH.constant || tb2next.implicitConvTo(tb1next) >= MATCH.constant) || ((exp.e1.op & 0xFF) == 47 && (exp.e1.implicitConvTo(tb2)) != 0)) || ((exp.e2.op & 0xFF) == 47 && (exp.e2.implicitConvTo(tb1)) != 0))))
                {
                    /*goto Lpeer*/throw Dispatch0.INSTANCE;
                }
                if ((((tb1.ty & 0xFF) == ENUMTY.Tsarray || (tb1.ty & 0xFF) == ENUMTY.Tarray) && (tb2.ty & 0xFF) != ENUMTY.Tvoid))
                {
                    if ((exp.e1.op & 0xFF) == 47)
                    {
                        exp.e2 = doCopyOrMove(this.sc, exp.e2, null);
                    }
                    else if ((exp.e1.op & 0xFF) == 121)
                    {
                    }
                    else
                    {
                        if (exp.e2.checkPostblit(this.sc, tb2))
                            this.setError();
                            return ;
                    }
                    if (((exp.e1.op & 0xFF) == 47 && (exp.e1.implicitConvTo(tb2.arrayOf())) != 0))
                    {
                        exp.e1 = exp.e1.implicitCastTo(this.sc, tb2.arrayOf());
                        exp.type = tb2.arrayOf();
                        /*goto L2elem*//*unrolled goto*/
                    /*L2elem:*/
                        if (((tb2.ty & 0xFF) == ENUMTY.Tarray || (tb2.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            exp.e2 = new ArrayLiteralExp(exp.e2.loc, exp.type, exp.e2);
                        }
                        else if (checkNewEscape(this.sc, exp.e2, false))
                            this.setError();
                            return ;
                        this.result = exp.optimize(0, false);
                        return ;
                    }
                    if (exp.e2.implicitConvTo(tb1next) >= MATCH.convert)
                    {
                        exp.e2 = exp.e2.implicitCastTo(this.sc, tb1next);
                        exp.type = tb1next.arrayOf();
                    /*L2elem:*/
                        if (((tb2.ty & 0xFF) == ENUMTY.Tarray || (tb2.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            exp.e2 = new ArrayLiteralExp(exp.e2.loc, exp.type, exp.e2);
                        }
                        else if (checkNewEscape(this.sc, exp.e2, false))
                            this.setError();
                            return ;
                        this.result = exp.optimize(0, false);
                        return ;
                    }
                }
                if ((((tb2.ty & 0xFF) == ENUMTY.Tsarray || (tb2.ty & 0xFF) == ENUMTY.Tarray) && (tb1.ty & 0xFF) != ENUMTY.Tvoid))
                {
                    if ((exp.e2.op & 0xFF) == 47)
                    {
                        exp.e1 = doCopyOrMove(this.sc, exp.e1, null);
                    }
                    else if ((exp.e2.op & 0xFF) == 121)
                    {
                    }
                    else
                    {
                        if (exp.e1.checkPostblit(this.sc, tb1))
                            this.setError();
                            return ;
                    }
                    if (((exp.e2.op & 0xFF) == 47 && (exp.e2.implicitConvTo(tb1.arrayOf())) != 0))
                    {
                        exp.e2 = exp.e2.implicitCastTo(this.sc, tb1.arrayOf());
                        exp.type = tb1.arrayOf();
                        /*goto L1elem*//*unrolled goto*/
                    /*L1elem:*/
                        if (((tb1.ty & 0xFF) == ENUMTY.Tarray || (tb1.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            exp.e1 = new ArrayLiteralExp(exp.e1.loc, exp.type, exp.e1);
                        }
                        else if (checkNewEscape(this.sc, exp.e1, false))
                            this.setError();
                            return ;
                        this.result = exp.optimize(0, false);
                        return ;
                    }
                    if (exp.e1.implicitConvTo(tb2next) >= MATCH.convert)
                    {
                        exp.e1 = exp.e1.implicitCastTo(this.sc, tb2next);
                        exp.type = tb2next.arrayOf();
                    /*L1elem:*/
                        if (((tb1.ty & 0xFF) == ENUMTY.Tarray || (tb1.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            exp.e1 = new ArrayLiteralExp(exp.e1.loc, exp.type, exp.e1);
                        }
                        else if (checkNewEscape(this.sc, exp.e1, false))
                            this.setError();
                            return ;
                        this.result = exp.optimize(0, false);
                        return ;
                    }
                }
            }
            catch(Dispatch0 __d){}
        /*Lpeer:*/
            if ((((((tb1.ty & 0xFF) == ENUMTY.Tsarray || (tb1.ty & 0xFF) == ENUMTY.Tarray) && ((tb2.ty & 0xFF) == ENUMTY.Tsarray || (tb2.ty & 0xFF) == ENUMTY.Tarray)) && ((tb1next.mod) != 0 || (tb2next.mod) != 0)) && (tb1next.mod & 0xFF) != (tb2next.mod & 0xFF)))
            {
                Type t1 = tb1next.mutableOf().constOf().arrayOf();
                Type t2 = tb2next.mutableOf().constOf().arrayOf();
                if (((exp.e1.op & 0xFF) == 121 && !((((StringExp)exp.e1).committed) != 0)))
                    exp.e1.type = t1;
                else
                    exp.e1 = exp.e1.castTo(this.sc, t1);
                if (((exp.e2.op & 0xFF) == 121 && !((((StringExp)exp.e2).committed) != 0)))
                    exp.e2.type = t2;
                else
                    exp.e2 = exp.e2.castTo(this.sc, t2);
            }
            {
                Expression ex = typeCombine(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            exp.type = exp.type.toHeadMutable();
            Type tb = exp.type.toBasetype();
            if ((tb.ty & 0xFF) == ENUMTY.Tsarray)
                exp.type = tb.nextOf().arrayOf();
            if (((((exp.type.ty & 0xFF) == ENUMTY.Tarray && tb1next != null) && tb2next != null) && (tb1next.mod & 0xFF) != (tb2next.mod & 0xFF)))
            {
                exp.type = exp.type.nextOf().toHeadMutable().arrayOf();
            }
            {
                Type tbn = tb.nextOf();
                if (tbn != null)
                {
                    if (exp.checkPostblit(this.sc, tbn))
                        this.setError();
                        return ;
                }
            }
            Type t1 = exp.e1.type.toBasetype();
            Type t2 = exp.e2.type.toBasetype();
            if ((((t1.ty & 0xFF) == ENUMTY.Tarray || (t1.ty & 0xFF) == ENUMTY.Tsarray) && ((t2.ty & 0xFF) == ENUMTY.Tarray || (t2.ty & 0xFF) == ENUMTY.Tsarray)))
            {
                e = exp.optimize(0, false);
            }
            else
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            this.result = e;
        }

        public  void visit(MulExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            {
                Expression ex = typeCombine(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Type tb = exp.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tarray || (tb.ty & 0xFF) == ENUMTY.Tsarray))
            {
                if (!(isArrayOpValid(exp)))
                {
                    this.result = arrayOpInvalidError(exp);
                    return ;
                }
                this.result = exp;
                return ;
            }
            if (exp.checkArithmeticBin())
                this.setError();
                return ;
            if (exp.type.isfloating())
            {
                Type t1 = exp.e1.type;
                Type t2 = exp.e2.type;
                if (t1.isreal())
                {
                    exp.type = t2;
                }
                else if (t2.isreal())
                {
                    exp.type = t1;
                }
                else if (t1.isimaginary())
                {
                    if (t2.isimaginary())
                    {
                        switch ((t1.toBasetype().ty & 0xFF))
                        {
                            case 24:
                                exp.type = Type.tfloat32;
                                break;
                            case 25:
                                exp.type = Type.tfloat64;
                                break;
                            case 26:
                                exp.type = Type.tfloat80;
                                break;
                            default:
                            throw new AssertionError("Unreachable code!");
                        }
                        exp.e1.type = exp.type;
                        exp.e2.type = exp.type;
                        e = new NegExp(exp.loc, exp);
                        e = expressionSemantic(e, this.sc);
                        this.result = e;
                        return ;
                    }
                    else
                        exp.type = t2;
                }
                else if (t2.isimaginary())
                {
                    exp.type = t1;
                }
            }
            else if (!(target.isVectorOpSupported(tb, exp.op, exp.e2.type.toBasetype())))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            this.result = exp;
        }

        public  void visit(DivExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            {
                Expression ex = typeCombine(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Type tb = exp.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tarray || (tb.ty & 0xFF) == ENUMTY.Tsarray))
            {
                if (!(isArrayOpValid(exp)))
                {
                    this.result = arrayOpInvalidError(exp);
                    return ;
                }
                this.result = exp;
                return ;
            }
            if (exp.checkArithmeticBin())
                this.setError();
                return ;
            if (exp.type.isfloating())
            {
                Type t1 = exp.e1.type;
                Type t2 = exp.e2.type;
                if (t1.isreal())
                {
                    exp.type = t2;
                    if (t2.isimaginary())
                    {
                        exp.e2.type = t1;
                        e = new NegExp(exp.loc, exp);
                        e = expressionSemantic(e, this.sc);
                        this.result = e;
                        return ;
                    }
                }
                else if (t2.isreal())
                {
                    exp.type = t1;
                }
                else if (t1.isimaginary())
                {
                    if (t2.isimaginary())
                    {
                        switch ((t1.toBasetype().ty & 0xFF))
                        {
                            case 24:
                                exp.type = Type.tfloat32;
                                break;
                            case 25:
                                exp.type = Type.tfloat64;
                                break;
                            case 26:
                                exp.type = Type.tfloat80;
                                break;
                            default:
                            throw new AssertionError("Unreachable code!");
                        }
                    }
                    else
                        exp.type = t2;
                }
                else if (t2.isimaginary())
                {
                    exp.type = t1;
                }
            }
            else if (!(target.isVectorOpSupported(tb, exp.op, exp.e2.type.toBasetype())))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            this.result = exp;
        }

        public  void visit(ModExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            {
                Expression ex = typeCombine(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Type tb = exp.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tarray || (tb.ty & 0xFF) == ENUMTY.Tsarray))
            {
                if (!(isArrayOpValid(exp)))
                {
                    this.result = arrayOpInvalidError(exp);
                    return ;
                }
                this.result = exp;
                return ;
            }
            if (!(target.isVectorOpSupported(tb, exp.op, exp.e2.type.toBasetype())))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            if (exp.checkArithmeticBin())
                this.setError();
                return ;
            if (exp.type.isfloating())
            {
                exp.type = exp.e1.type;
                if (exp.e2.type.iscomplex())
                {
                    exp.error(new BytePtr("cannot perform modulo complex arithmetic"));
                    this.setError();
                    return ;
                }
            }
            this.result = exp;
        }

        public  void visit(PowExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            {
                Expression ex = typeCombine(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Type tb = exp.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tarray || (tb.ty & 0xFF) == ENUMTY.Tsarray))
            {
                if (!(isArrayOpValid(exp)))
                {
                    this.result = arrayOpInvalidError(exp);
                    return ;
                }
                this.result = exp;
                return ;
            }
            if (exp.checkArithmeticBin())
                this.setError();
                return ;
            if (!(target.isVectorOpSupported(tb, exp.op, exp.e2.type.toBasetype())))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            e = exp.optimize(0, false);
            if ((e.op & 0xFF) != 226)
            {
                e = expressionSemantic(e, this.sc);
                this.result = e;
                return ;
            }
            long intpow = 0L;
            if (((exp.e2.op & 0xFF) == 135 && ((long)exp.e2.toInteger() == 2L || (long)exp.e2.toInteger() == 3L)))
                intpow = (long)exp.e2.toInteger();
            else if (((exp.e2.op & 0xFF) == 140 && exp.e2.toReal() == (double)(long)exp.e2.toReal()))
                intpow = (long)exp.e2.toReal();
            if ((intpow == 2L || intpow == 3L))
            {
                VarDeclaration tmp = copyToTemp(0L, new BytePtr("__powtmp"), exp.e1);
                Expression de = new DeclarationExp(exp.loc, tmp);
                Expression ve = new VarExp(exp.loc, tmp, true);
                Expression me = new MulExp(exp.loc, ve, ve);
                if (intpow == 3L)
                    me = new MulExp(exp.loc, me, ve);
                e = new CommaExp(exp.loc, de, me, true);
                e = expressionSemantic(e, this.sc);
                this.result = e;
                return ;
            }
            dmodule.Module mmath = loadStdMath();
            if (!(mmath != null))
            {
                e.error(new BytePtr("`%s` requires `std.math` for `^^` operators"), e.toChars());
                this.setError();
                return ;
            }
            e = new ScopeExp(exp.loc, mmath);
            if (((exp.e2.op & 0xFF) == 140 && exp.e2.toReal() == CTFloat.half))
            {
                e = new CallExp(exp.loc, new DotIdExp(exp.loc, e, Id._sqrt), exp.e1);
            }
            else
            {
                e = new CallExp(exp.loc, new DotIdExp(exp.loc, e, Id._pow), exp.e1, exp.e2);
            }
            e = expressionSemantic(e, this.sc);
            this.result = e;
            return ;
        }

        public  void visit(ShlExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            if (exp.checkIntegralBin())
                this.setError();
                return ;
            if (!(target.isVectorOpSupported(exp.e1.type.toBasetype(), exp.op, exp.e2.type.toBasetype())))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            exp.e1 = integralPromotions(exp.e1, this.sc);
            if ((exp.e2.type.toBasetype().ty & 0xFF) != ENUMTY.Tvector)
                exp.e2 = exp.e2.castTo(this.sc, Type.tshiftcnt);
            exp.type = exp.e1.type;
            this.result = exp;
        }

        public  void visit(ShrExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            if (exp.checkIntegralBin())
                this.setError();
                return ;
            if (!(target.isVectorOpSupported(exp.e1.type.toBasetype(), exp.op, exp.e2.type.toBasetype())))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            exp.e1 = integralPromotions(exp.e1, this.sc);
            if ((exp.e2.type.toBasetype().ty & 0xFF) != ENUMTY.Tvector)
                exp.e2 = exp.e2.castTo(this.sc, Type.tshiftcnt);
            exp.type = exp.e1.type;
            this.result = exp;
        }

        public  void visit(UshrExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            if (exp.checkIntegralBin())
                this.setError();
                return ;
            if (!(target.isVectorOpSupported(exp.e1.type.toBasetype(), exp.op, exp.e2.type.toBasetype())))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            exp.e1 = integralPromotions(exp.e1, this.sc);
            if ((exp.e2.type.toBasetype().ty & 0xFF) != ENUMTY.Tvector)
                exp.e2 = exp.e2.castTo(this.sc, Type.tshiftcnt);
            exp.type = exp.e1.type;
            this.result = exp;
        }

        public  void visit(AndExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            if (((exp.e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tbool && (exp.e2.type.toBasetype().ty & 0xFF) == ENUMTY.Tbool))
            {
                exp.type = exp.e1.type;
                this.result = exp;
                return ;
            }
            {
                Expression ex = typeCombine(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Type tb = exp.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tarray || (tb.ty & 0xFF) == ENUMTY.Tsarray))
            {
                if (!(isArrayOpValid(exp)))
                {
                    this.result = arrayOpInvalidError(exp);
                    return ;
                }
                this.result = exp;
                return ;
            }
            if (!(target.isVectorOpSupported(tb, exp.op, exp.e2.type.toBasetype())))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            if (exp.checkIntegralBin())
                this.setError();
                return ;
            this.result = exp;
        }

        public  void visit(OrExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            if (((exp.e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tbool && (exp.e2.type.toBasetype().ty & 0xFF) == ENUMTY.Tbool))
            {
                exp.type = exp.e1.type;
                this.result = exp;
                return ;
            }
            {
                Expression ex = typeCombine(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Type tb = exp.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tarray || (tb.ty & 0xFF) == ENUMTY.Tsarray))
            {
                if (!(isArrayOpValid(exp)))
                {
                    this.result = arrayOpInvalidError(exp);
                    return ;
                }
                this.result = exp;
                return ;
            }
            if (!(target.isVectorOpSupported(tb, exp.op, exp.e2.type.toBasetype())))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            if (exp.checkIntegralBin())
                this.setError();
                return ;
            this.result = exp;
        }

        public  void visit(XorExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            if (((exp.e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tbool && (exp.e2.type.toBasetype().ty & 0xFF) == ENUMTY.Tbool))
            {
                exp.type = exp.e1.type;
                this.result = exp;
                return ;
            }
            {
                Expression ex = typeCombine(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Type tb = exp.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tarray || (tb.ty & 0xFF) == ENUMTY.Tsarray))
            {
                if (!(isArrayOpValid(exp)))
                {
                    this.result = arrayOpInvalidError(exp);
                    return ;
                }
                this.result = exp;
                return ;
            }
            if (!(target.isVectorOpSupported(tb, exp.op, exp.e2.type.toBasetype())))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            if (exp.checkIntegralBin())
                this.setError();
                return ;
            this.result = exp;
        }

        public  void visit(LogicalExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            exp.setNoderefOperands();
            Expression e1x = expressionSemantic(exp.e1, this.sc);
            if ((e1x.op & 0xFF) == 20)
                e1x = resolveAliasThis(this.sc, e1x, false);
            e1x = resolveProperties(this.sc, e1x);
            e1x = e1x.toBoolean(this.sc);
            if (((this.sc).flags & 4) != 0)
            {
                e1x = e1x.optimize(0, false);
                if (e1x.isBool((exp.op & 0xFF) == 102))
                {
                    this.result = new IntegerExp(exp.loc, (((exp.op & 0xFF) == 102) ? 1 : 0), Type.tbool);
                    return ;
                }
            }
            CtorFlow ctorflow = (this.sc).ctorflow.clone().copy();
            Expression e2x = expressionSemantic(exp.e2, this.sc);
            (this.sc).merge(exp.loc, ctorflow);
            ctorflow.freeFieldinit();
            if ((e2x.op & 0xFF) == 20)
                e2x = resolveAliasThis(this.sc, e2x, false);
            e2x = resolveProperties(this.sc, e2x);
            boolean f1 = checkNonAssignmentArrayOp(e1x, false);
            boolean f2 = checkNonAssignmentArrayOp(e2x, false);
            if ((f1 || f2))
                this.setError();
                return ;
            if ((e2x.type.ty & 0xFF) != ENUMTY.Tvoid)
                e2x = e2x.toBoolean(this.sc);
            if (((e2x.op & 0xFF) == 20 || (e2x.op & 0xFF) == 203))
            {
                exp.error(new BytePtr("`%s` is not an expression"), exp.e2.toChars());
                this.setError();
                return ;
            }
            if ((e1x.op & 0xFF) == 127)
            {
                this.result = e1x;
                return ;
            }
            if ((e2x.op & 0xFF) == 127)
            {
                this.result = e2x;
                return ;
            }
            if ((e2x.type.ty & 0xFF) == ENUMTY.Tvoid)
                exp.type = Type.tvoid;
            else
                exp.type = Type.tbool;
            exp.e1 = e1x;
            exp.e2 = e2x;
            this.result = exp;
        }

        public  void visit(CmpExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            exp.setNoderefOperands();
            {
                Expression ex = binSemanticProp(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Type t1 = exp.e1.type.toBasetype();
            Type t2 = exp.e2.type.toBasetype();
            if ((((t1.ty & 0xFF) == ENUMTY.Tclass && (exp.e2.op & 0xFF) == 13) || ((t2.ty & 0xFF) == ENUMTY.Tclass && (exp.e1.op & 0xFF) == 13)))
            {
                exp.error(new BytePtr("do not use `null` when comparing class types"));
                this.setError();
                return ;
            }
            Ref<Byte> cmpop = ref(TOK.reserved);
            {
                Expression e = op_overload(exp, this.sc, ptr(cmpop));
                if (e != null)
                {
                    if ((!(e.type.isscalar()) && e.type.equals(exp.e1.type)))
                    {
                        exp.error(new BytePtr("recursive `opCmp` expansion"));
                        this.setError();
                        return ;
                    }
                    if ((e.op & 0xFF) == 18)
                    {
                        e = new CmpExp(cmpop.value, exp.loc, e, new IntegerExp(exp.loc, 0L, Type.tint32));
                        e = expressionSemantic(e, this.sc);
                    }
                    this.result = e;
                    return ;
                }
            }
            {
                Expression ex = typeCombine(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            boolean f1 = checkNonAssignmentArrayOp(exp.e1, false);
            boolean f2 = checkNonAssignmentArrayOp(exp.e2, false);
            if ((f1 || f2))
                this.setError();
                return ;
            exp.type = Type.tbool;
            Expression arrayLowering = null;
            t1 = exp.e1.type.toBasetype();
            t2 = exp.e2.type.toBasetype();
            if (((((t1.ty & 0xFF) == ENUMTY.Tarray || (t1.ty & 0xFF) == ENUMTY.Tsarray) || (t1.ty & 0xFF) == ENUMTY.Tpointer) && (((t2.ty & 0xFF) == ENUMTY.Tarray || (t2.ty & 0xFF) == ENUMTY.Tsarray) || (t2.ty & 0xFF) == ENUMTY.Tpointer)))
            {
                Type t1next = t1.nextOf();
                Type t2next = t2.nextOf();
                if (((t1next.implicitConvTo(t2next) < MATCH.constant && t2next.implicitConvTo(t1next) < MATCH.constant) && ((t1next.ty & 0xFF) != ENUMTY.Tvoid && (t2next.ty & 0xFF) != ENUMTY.Tvoid)))
                {
                    exp.error(new BytePtr("array comparison type mismatch, `%s` vs `%s`"), t1next.toChars(), t2next.toChars());
                    this.setError();
                    return ;
                }
                if ((((t1.ty & 0xFF) == ENUMTY.Tarray || (t1.ty & 0xFF) == ENUMTY.Tsarray) && ((t2.ty & 0xFF) == ENUMTY.Tarray || (t2.ty & 0xFF) == ENUMTY.Tsarray)))
                {
                    if (!(verifyHookExist(exp.loc, this.sc, Id.__cmp, new ByteSlice("comparing arrays"), Id.object)))
                        this.setError();
                        return ;
                    Expression al = new IdentifierExp(exp.loc, Id.empty);
                    al = new DotIdExp(exp.loc, al, Id.object);
                    al = new DotIdExp(exp.loc, al, Id.__cmp);
                    al = expressionSemantic(al, this.sc);
                    DArray<Expression> arguments = new DArray<Expression>(2);
                    arguments.set(0, exp.e1);
                    arguments.set(1, exp.e2);
                    al = new CallExp(exp.loc, al, arguments);
                    al = new CmpExp(exp.op, exp.loc, al, literal0());
                    arrayLowering = al;
                }
            }
            else if ((((t1.ty & 0xFF) == ENUMTY.Tstruct || (t2.ty & 0xFF) == ENUMTY.Tstruct) || ((t1.ty & 0xFF) == ENUMTY.Tclass && (t2.ty & 0xFF) == ENUMTY.Tclass)))
            {
                if ((t2.ty & 0xFF) == ENUMTY.Tstruct)
                    exp.error(new BytePtr("need member function `opCmp()` for %s `%s` to compare"), t2.toDsymbol(this.sc).kind(), t2.toChars());
                else
                    exp.error(new BytePtr("need member function `opCmp()` for %s `%s` to compare"), t1.toDsymbol(this.sc).kind(), t1.toChars());
                this.setError();
                return ;
            }
            else if ((t1.iscomplex() || t2.iscomplex()))
            {
                exp.error(new BytePtr("compare not defined for complex operands"));
                this.setError();
                return ;
            }
            else if (((t1.ty & 0xFF) == ENUMTY.Taarray || (t2.ty & 0xFF) == ENUMTY.Taarray))
            {
                exp.error(new BytePtr("`%s` is not defined for associative arrays"), Token.toChars(exp.op));
                this.setError();
                return ;
            }
            else if (!(target.isVectorOpSupported(t1, exp.op, t2)))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            else
            {
                boolean r1 = exp.e1.checkValue();
                boolean r2 = exp.e2.checkValue();
                if ((r1 || r2))
                    this.setError();
                    return ;
            }
            if (arrayLowering != null)
            {
                arrayLowering = expressionSemantic(arrayLowering, this.sc);
                this.result = arrayLowering;
                return ;
            }
            this.result = exp;
            return ;
        }

        public  void visit(InExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc, null);
            if (e != null)
            {
                this.result = e;
                return ;
            }
            Type t2b = exp.e2.type.toBasetype();
            switch ((t2b.ty & 0xFF))
            {
                case 2:
                    TypeAArray ta = (TypeAArray)t2b;
                    if (!(arrayTypeCompatibleWithoutCasting(exp.e1.type, ta.index)))
                    {
                        exp.e1 = exp.e1.implicitCastTo(this.sc, ta.index);
                    }
                    semanticTypeInfo(this.sc, ta.index);
                    exp.type = ta.nextOf().pointerTo();
                    break;
                case 34:
                    this.setError();
                    return ;
                default:
                this.result = exp.incompatibleTypes();
                return ;
            }
            this.result = exp;
        }

        public  void visit(RemoveExp e) {
            {
                Expression ex = binSemantic(e, this.sc);
                if (ex != null)
                {
                    this.result = ex;
                    return ;
                }
            }
            this.result = e;
        }

        public  void visit(EqualExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            exp.setNoderefOperands();
            {
                Expression e = binSemanticProp(exp, this.sc);
                if (e != null)
                {
                    this.result = e;
                    return ;
                }
            }
            if (((exp.e1.op & 0xFF) == 20 || (exp.e2.op & 0xFF) == 20))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            {
                Type t1 = exp.e1.type;
                Type t2 = exp.e2.type;
                if ((((t1.ty & 0xFF) == ENUMTY.Tenum && (t2.ty & 0xFF) == ENUMTY.Tenum) && !(t1.equivalent(t2))))
                    exp.error(new BytePtr("Comparison between different enumeration types `%s` and `%s`; If this behavior is intended consider using `std.conv.asOriginalType`"), t1.toChars(), t2.toChars());
            }
            if (((exp.e1.op & 0xFF) == 19 && (exp.e2.op & 0xFF) == 19))
            {
                AddrExp ae1 = (AddrExp)exp.e1;
                AddrExp ae2 = (AddrExp)exp.e2;
                if (((ae1.e1.op & 0xFF) == 26 && (ae2.e1.op & 0xFF) == 26))
                {
                    VarExp ve1 = (VarExp)ae1.e1;
                    VarExp ve2 = (VarExp)ae2.e1;
                    if (pequals(ve1.var, ve2.var))
                    {
                        this.result = new IntegerExp(exp.loc, (((exp.op & 0xFF) == 58) ? 1 : 0), Type.tbool);
                        return ;
                    }
                }
            }
            Type t1 = exp.e1.type.toBasetype();
            Type t2 = exp.e2.type.toBasetype();
            Function2<Type,Type,Boolean> needsDirectEq = new Function2<Type,Type,Boolean>(){
                public Boolean invoke(Type t1, Type t2){
                    Type t1n = t1.nextOf().toBasetype();
                    Type t2n = t2.nextOf().toBasetype();
                    if ((((((t1n.ty & 0xFF) == ENUMTY.Tchar || (t1n.ty & 0xFF) == ENUMTY.Twchar) || (t1n.ty & 0xFF) == ENUMTY.Tdchar) && (((t2n.ty & 0xFF) == ENUMTY.Tchar || (t2n.ty & 0xFF) == ENUMTY.Twchar) || (t2n.ty & 0xFF) == ENUMTY.Tdchar)) || ((t1n.ty & 0xFF) == ENUMTY.Tvoid || (t2n.ty & 0xFF) == ENUMTY.Tvoid)))
                    {
                        return false;
                    }
                    if (!pequals(t1n.constOf(), t2n.constOf()))
                        return true;
                    Type t = t1n;
                    for (; t.toBasetype().nextOf() != null;) {
                        t = t.nextOf().toBasetype();
                    }
                    if ((t.ty & 0xFF) != ENUMTY.Tstruct)
                        return false;
                    if ((global.params.useTypeInfo && Type.dtypeinfo != null))
                        semanticTypeInfo(sc, t);
                    return ((TypeStruct)t).sym.hasIdentityEquals;
                }
            };
            {
                Expression e = op_overload(exp, this.sc, null);
                if (e != null)
                {
                    this.result = e;
                    return ;
                }
            }
            if (!((((t1.ty & 0xFF) == ENUMTY.Tarray && (t2.ty & 0xFF) == ENUMTY.Tarray) && needsDirectEq.invoke(t1, t2))))
            {
                {
                    Expression e = typeCombine(exp, this.sc);
                    if (e != null)
                    {
                        this.result = e;
                        return ;
                    }
                }
            }
            boolean f1 = checkNonAssignmentArrayOp(exp.e1, false);
            boolean f2 = checkNonAssignmentArrayOp(exp.e2, false);
            if ((f1 || f2))
                this.setError();
                return ;
            exp.type = Type.tbool;
            if (!((((t1.ty & 0xFF) == ENUMTY.Tarray && (t2.ty & 0xFF) == ENUMTY.Tarray) && needsDirectEq.invoke(t1, t2))))
            {
                if (!(arrayTypeCompatible(exp.loc, exp.e1.type, exp.e2.type)))
                {
                    if (((!pequals(exp.e1.type, exp.e2.type) && exp.e1.type.isfloating()) && exp.e2.type.isfloating()))
                    {
                        exp.e1 = exp.e1.castTo(this.sc, Type.tcomplex80);
                        exp.e2 = exp.e2.castTo(this.sc, Type.tcomplex80);
                    }
                }
            }
            if (((t1.ty & 0xFF) == ENUMTY.Tarray && (t2.ty & 0xFF) == ENUMTY.Tarray))
            {
                if (!(verifyHookExist(exp.loc, this.sc, Id.__equals, new ByteSlice("equal checks on arrays"), Id.object)))
                    this.setError();
                    return ;
                Expression __equals = new IdentifierExp(exp.loc, Id.empty);
                Identifier id = Identifier.idPool(new ByteSlice("__equals"));
                __equals = new DotIdExp(exp.loc, __equals, Id.object);
                __equals = new DotIdExp(exp.loc, __equals, id);
                DArray<Expression> arguments = new DArray<Expression>(2);
                arguments.set(0, exp.e1);
                arguments.set(1, exp.e2);
                __equals = new CallExp(exp.loc, __equals, arguments);
                if ((exp.op & 0xFF) == 59)
                {
                    __equals = new NotExp(exp.loc, __equals);
                }
                __equals = expressionSemantic(__equals, this.sc);
                this.result = __equals;
                return ;
            }
            if ((exp.e1.type.toBasetype().ty & 0xFF) == ENUMTY.Taarray)
                semanticTypeInfo(this.sc, exp.e1.type.toBasetype());
            if (!(target.isVectorOpSupported(t1, exp.op, t2)))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            this.result = exp;
        }

        public  void visit(IdentityExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            exp.setNoderefOperands();
            {
                Expression e = binSemanticProp(exp, this.sc);
                if (e != null)
                {
                    this.result = e;
                    return ;
                }
            }
            {
                Expression e = typeCombine(exp, this.sc);
                if (e != null)
                {
                    this.result = e;
                    return ;
                }
            }
            boolean f1 = checkNonAssignmentArrayOp(exp.e1, false);
            boolean f2 = checkNonAssignmentArrayOp(exp.e2, false);
            if ((f1 || f2))
                this.setError();
                return ;
            if (((exp.e1.op & 0xFF) == 20 || (exp.e2.op & 0xFF) == 20))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            exp.type = Type.tbool;
            if (((!pequals(exp.e1.type, exp.e2.type) && exp.e1.type.isfloating()) && exp.e2.type.isfloating()))
            {
                exp.e1 = exp.e1.castTo(this.sc, Type.tcomplex80);
                exp.e2 = exp.e2.castTo(this.sc, Type.tcomplex80);
            }
            Type tb1 = exp.e1.type.toBasetype();
            Type tb2 = exp.e2.type.toBasetype();
            if (!(target.isVectorOpSupported(tb1, exp.op, tb2)))
            {
                this.result = exp.incompatibleTypes();
                return ;
            }
            if ((exp.e1.op & 0xFF) == 18)
                exp.e1 = ((CallExp)exp.e1).addDtorHook(this.sc);
            if ((exp.e2.op & 0xFF) == 18)
                exp.e2 = ((CallExp)exp.e2).addDtorHook(this.sc);
            if (((exp.e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray || (exp.e2.type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
                exp.deprecation(new BytePtr("identity comparison of static arrays implicitly coerces them to slices, which are compared by reference"));
            this.result = exp;
        }

        public  void visit(CondExp exp) {
            if (exp.type != null)
            {
                this.result = exp;
                return ;
            }
            if ((exp.econd.op & 0xFF) == 28)
                ((DotIdExp)exp.econd).noderef = true;
            Expression ec = expressionSemantic(exp.econd, this.sc);
            ec = resolveProperties(this.sc, ec);
            ec = ec.toBoolean(this.sc);
            CtorFlow ctorflow_root = (this.sc).ctorflow.clone().copy();
            Expression e1x = expressionSemantic(exp.e1, this.sc);
            e1x = resolveProperties(this.sc, e1x);
            CtorFlow ctorflow1 = (this.sc).ctorflow.copy();
            (this.sc).ctorflow = ctorflow_root.copy();
            Expression e2x = expressionSemantic(exp.e2, this.sc);
            e2x = resolveProperties(this.sc, e2x);
            (this.sc).merge(exp.loc, ctorflow1);
            ctorflow1.freeFieldinit();
            if ((ec.op & 0xFF) == 127)
            {
                this.result = ec;
                return ;
            }
            if (pequals(ec.type, Type.terror))
                this.setError();
                return ;
            exp.econd = ec;
            if ((e1x.op & 0xFF) == 127)
            {
                this.result = e1x;
                return ;
            }
            if (pequals(e1x.type, Type.terror))
                this.setError();
                return ;
            exp.e1 = e1x;
            if ((e2x.op & 0xFF) == 127)
            {
                this.result = e2x;
                return ;
            }
            if (pequals(e2x.type, Type.terror))
                this.setError();
                return ;
            exp.e2 = e2x;
            boolean f0 = checkNonAssignmentArrayOp(exp.econd, false);
            boolean f1 = checkNonAssignmentArrayOp(exp.e1, false);
            boolean f2 = checkNonAssignmentArrayOp(exp.e2, false);
            if (((f0 || f1) || f2))
                this.setError();
                return ;
            Type t1 = exp.e1.type;
            Type t2 = exp.e2.type;
            if (((t1.ty & 0xFF) == ENUMTY.Tvoid || (t2.ty & 0xFF) == ENUMTY.Tvoid))
            {
                exp.type = Type.tvoid;
                exp.e1 = exp.e1.castTo(this.sc, exp.type);
                exp.e2 = exp.e2.castTo(this.sc, exp.type);
            }
            else if (pequals(t1, t2))
                exp.type = t1;
            else
            {
                {
                    Expression ex = typeCombine(exp, this.sc);
                    if (ex != null)
                    {
                        this.result = ex;
                        return ;
                    }
                }
                switch ((exp.e1.type.toBasetype().ty & 0xFF))
                {
                    case 27:
                    case 28:
                    case 29:
                        exp.e2 = exp.e2.castTo(this.sc, exp.e1.type);
                        break;
                    default:
                    break;
                }
                switch ((exp.e2.type.toBasetype().ty & 0xFF))
                {
                    case 27:
                    case 28:
                    case 29:
                        exp.e1 = exp.e1.castTo(this.sc, exp.e2.type);
                        break;
                    default:
                    break;
                }
                if ((exp.type.toBasetype().ty & 0xFF) == ENUMTY.Tarray)
                {
                    exp.e1 = exp.e1.castTo(this.sc, exp.type);
                    exp.e2 = exp.e2.castTo(this.sc, exp.type);
                }
            }
            exp.type = exp.type.merge2();
            exp.hookDtors(this.sc);
            this.result = exp;
        }

        public  void visit(FileInitExp e) {
            e.type = Type.tstring;
            this.result = e;
        }

        public  void visit(LineInitExp e) {
            e.type = Type.tint32;
            this.result = e;
        }

        public  void visit(ModuleInitExp e) {
            e.type = Type.tstring;
            this.result = e;
        }

        public  void visit(FuncInitExp e) {
            e.type = Type.tstring;
            if ((this.sc).func != null)
            {
                this.result = e.resolveLoc(Loc.initial, this.sc);
                return ;
            }
            this.result = e;
        }

        public  void visit(PrettyFuncInitExp e) {
            e.type = Type.tstring;
            if ((this.sc).func != null)
            {
                this.result = e.resolveLoc(Loc.initial, this.sc);
                return ;
            }
            this.result = e;
        }


        public ExpressionSemanticVisitor() {}

        public ExpressionSemanticVisitor copy() {
            ExpressionSemanticVisitor that = new ExpressionSemanticVisitor();
            that.sc = this.sc;
            that.result = this.result;
            return that;
        }
    }
    public static Expression trySemantic(Expression exp, Scope sc) {
        int errors = global.startGagging();
        Expression e = expressionSemantic(exp, sc);
        if (global.endGagging(errors))
        {
            e = null;
        }
        return e;
    }

    public static Expression unaSemantic(UnaExp e, Scope sc) {
        Expression e1x = expressionSemantic(e.e1, sc);
        if ((e1x.op & 0xFF) == 127)
            return e1x;
        e.e1 = e1x;
        return null;
    }

    public static Expression binSemantic(BinExp e, Scope sc) {
        Expression e1x = expressionSemantic(e.e1, sc);
        Expression e2x = expressionSemantic(e.e2, sc);
        if ((e1x.op & 0xFF) == 20)
            e1x = resolveAliasThis(sc, e1x, false);
        if ((e2x.op & 0xFF) == 20)
            e2x = resolveAliasThis(sc, e2x, false);
        if ((e1x.op & 0xFF) == 127)
            return e1x;
        if ((e2x.op & 0xFF) == 127)
            return e2x;
        e.e1 = e1x;
        e.e2 = e2x;
        return null;
    }

    public static Expression binSemanticProp(BinExp e, Scope sc) {
        {
            Expression ex = binSemantic(e, sc);
            if (ex != null)
                return ex;
        }
        Expression e1x = resolveProperties(sc, e.e1);
        Expression e2x = resolveProperties(sc, e.e2);
        if ((e1x.op & 0xFF) == 127)
            return e1x;
        if ((e2x.op & 0xFF) == 127)
            return e2x;
        e.e1 = e1x;
        e.e2 = e2x;
        return null;
    }

    public static Expression expressionSemantic(Expression e, Scope sc) {
        ExpressionSemanticVisitor v = new ExpressionSemanticVisitor(sc);
        e.accept(v);
        return v.result;
    }

    public static Expression semanticX(DotIdExp exp, Scope sc) {
        {
            Expression ex = unaSemantic(exp, sc);
            if (ex != null)
                return ex;
        }
        if (pequals(exp.ident, Id._mangleof))
        {
            Dsymbol ds = null;
            {
                int __dispatch16 = 0;
                dispatched_16:
                do {
                    switch (__dispatch16 != 0 ? __dispatch16 : (exp.e1.op & 0xFF))
                    {
                        case 203:
                            ds = ((ScopeExp)exp.e1).sds;
                            /*goto L1*/{ __dispatch16 = -1; continue dispatched_16; }
                        case 26:
                            ds = ((VarExp)exp.e1).var;
                            /*goto L1*/{ __dispatch16 = -1; continue dispatched_16; }
                        case 27:
                            ds = ((DotVarExp)exp.e1).var;
                            /*goto L1*/{ __dispatch16 = -1; continue dispatched_16; }
                        case 214:
                            ds = ((OverExp)exp.e1).vars;
                            /*goto L1*/{ __dispatch16 = -1; continue dispatched_16; }
                        case 36:
                            {
                                TemplateExp te = (TemplateExp)exp.e1;
                                ds = te.fd != null ? te.fd : te.td;
                            }
                        /*L1:*/
                        case -1:
                        __dispatch16 = 0;
                            {
                                assert(ds != null);
                                {
                                    FuncDeclaration f = ds.isFuncDeclaration();
                                    if (f != null)
                                    {
                                        if (f.checkForwardRef(exp.loc))
                                        {
                                            return new ErrorExp();
                                        }
                                    }
                                }
                                OutBuffer buf = new OutBuffer();
                                try {
                                    mangleToBuffer(ds, buf);
                                    ByteSlice s = buf.peekSlice().copy();
                                    Expression e = new StringExp(exp.loc, buf.extractChars(), s.getLength());
                                    e = expressionSemantic(e, sc);
                                    return e;
                                }
                                finally {
                                }
                            }
                        default:
                        break;
                    }
                } while(__dispatch16 != 0);
            }
        }
        if ((((exp.e1.op & 0xFF) == 26 && (exp.e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray) && pequals(exp.ident, Id.length)))
        {
            return dotExp(exp.e1.type, sc, exp.e1, exp.ident, exp.noderef ? DotExpFlag.noDeref : 0);
        }
        if ((exp.e1.op & 0xFF) == 97)
        {
        }
        else
        {
            exp.e1 = resolvePropertiesX(sc, exp.e1, null);
        }
        if (((exp.e1.op & 0xFF) == 126 && pequals(exp.ident, Id.offsetof)))
        {
            TupleExp te = (TupleExp)exp.e1;
            DArray<Expression> exps = new DArray<Expression>((te.exps).length);
            {
                int i = 0;
                for (; i < (exps).length;i++){
                    Expression e = (te.exps).get(i);
                    e = expressionSemantic(e, sc);
                    e = new DotIdExp(e.loc, e, Id.offsetof);
                    exps.set(i, e);
                }
            }
            Expression e = new TupleExp(exp.loc, null, exps);
            e = expressionSemantic(e, sc);
            return e;
        }
        if (((exp.e1.op & 0xFF) == 126 && pequals(exp.ident, Id.length)))
        {
            TupleExp te = (TupleExp)exp.e1;
            Expression e = new IntegerExp(exp.loc, (long)(te.exps).length, Type.tsize_t);
            return e;
        }
        if ((((exp.e1.op & 0xFF) == 37 || (exp.e1.op & 0xFF) == 36) && !pequals(exp.ident, Id.stringof)))
        {
            exp.error(new BytePtr("template `%s` does not have property `%s`"), exp.e1.toChars(), exp.ident.toChars());
            return new ErrorExp();
        }
        if (!(exp.e1.type != null))
        {
            exp.error(new BytePtr("expression `%s` does not have property `%s`"), exp.e1.toChars(), exp.ident.toChars());
            return new ErrorExp();
        }
        return exp;
    }

    public static Expression semanticY(DotIdExp exp, Scope sc, int flag) {
        if ((((exp.e1.op & 0xFF) == 123 || (exp.e1.op & 0xFF) == 124) && !(hasThis(sc) != null)))
        {
            {
                AggregateDeclaration ad = (sc).getStructClassScope();
                if (ad != null)
                {
                    if ((exp.e1.op & 0xFF) == 123)
                    {
                        exp.e1 = new TypeExp(exp.e1.loc, ad.type);
                    }
                    else
                    {
                        ClassDeclaration cd = ad.isClassDeclaration();
                        if ((cd != null && cd.baseClass != null))
                            exp.e1 = new TypeExp(exp.e1.loc, cd.baseClass.type);
                    }
                }
            }
        }
        Expression e = semanticX(exp, sc);
        if (!pequals(e, exp))
            return e;
        Expression eleft = null;
        Expression eright = null;
        if ((exp.e1.op & 0xFF) == 97)
        {
            DotExp de = (DotExp)exp.e1;
            eleft = de.e1;
            eright = de.e2;
        }
        else
        {
            eleft = null;
            eright = exp.e1;
        }
        Type t1b = exp.e1.type.toBasetype();
        if ((eright.op & 0xFF) == 203)
        {
            ScopeExp ie = (ScopeExp)eright;
            int flags = 8;
            if ((ie.sds.isModule() != null && !pequals(ie.sds, (sc)._module)))
                flags |= 1;
            if (((sc).flags & 512) != 0)
                flags |= 128;
            Dsymbol s = ie.sds.search(exp.loc, exp.ident, flags);
            if (((s != null && !(((sc).flags & 512) != 0)) && !(symbolIsVisible((sc)._module, s))))
            {
                s = null;
            }
            if (s != null)
            {
                dmodule.Package p = s.isPackage();
                if ((p != null && checkAccess(exp.loc, sc, p)))
                {
                    s = null;
                }
            }
            if (s != null)
            {
                s = s.toAlias();
                exp.checkDeprecated(sc, s);
                exp.checkDisabled(sc, s);
                EnumMember em = s.isEnumMember();
                if (em != null)
                {
                    return em.getVarExp(exp.loc, sc);
                }
                VarDeclaration v = s.isVarDeclaration();
                if (v != null)
                {
                    if ((!(v.type != null) || (v.type.deco == null && (v.inuse) != 0)))
                    {
                        if ((v.inuse) != 0)
                            exp.error(new BytePtr("circular reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                        else
                            exp.error(new BytePtr("forward reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                        return new ErrorExp();
                    }
                    if ((v.type.ty & 0xFF) == ENUMTY.Terror)
                        return new ErrorExp();
                    if ((((v.storage_class & 8388608L) != 0 && v._init != null) && !(exp.wantsym)))
                    {
                        if ((v.inuse) != 0)
                        {
                            error(exp.loc, new BytePtr("circular initialization of %s `%s`"), v.kind(), v.toPrettyChars(false));
                            return new ErrorExp();
                        }
                        e = v.expandInitializer(exp.loc);
                        v.inuse++;
                        e = expressionSemantic(e, sc);
                        v.inuse--;
                        return e;
                    }
                    if (v.needThis())
                    {
                        if (!(eleft != null))
                            eleft = new ThisExp(exp.loc);
                        e = new DotVarExp(exp.loc, eleft, v, true);
                        e = expressionSemantic(e, sc);
                    }
                    else
                    {
                        e = new VarExp(exp.loc, v, true);
                        if (eleft != null)
                        {
                            e = new CommaExp(exp.loc, eleft, e, true);
                            e.type = v.type;
                        }
                    }
                    e = e.deref();
                    return expressionSemantic(e, sc);
                }
                FuncDeclaration f = s.isFuncDeclaration();
                if (f != null)
                {
                    if (!(f.functionSemantic()))
                        return new ErrorExp();
                    if (f.needThis())
                    {
                        if (!(eleft != null))
                            eleft = new ThisExp(exp.loc);
                        e = new DotVarExp(exp.loc, eleft, f, true);
                        e = expressionSemantic(e, sc);
                    }
                    else
                    {
                        e = new VarExp(exp.loc, f, true);
                        if (eleft != null)
                        {
                            e = new CommaExp(exp.loc, eleft, e, true);
                            e.type = f.type;
                        }
                    }
                    return e;
                }
                {
                    TemplateDeclaration td = s.isTemplateDeclaration();
                    if (td != null)
                    {
                        if (eleft != null)
                            e = new DotTemplateExp(exp.loc, eleft, td);
                        else
                            e = new TemplateExp(exp.loc, td, null);
                        e = expressionSemantic(e, sc);
                        return e;
                    }
                }
                {
                    OverDeclaration od = s.isOverDeclaration();
                    if (od != null)
                    {
                        e = new VarExp(exp.loc, od, true);
                        if (eleft != null)
                        {
                            e = new CommaExp(exp.loc, eleft, e, true);
                            e.type = Type.tvoid;
                        }
                        return e;
                    }
                }
                OverloadSet o = s.isOverloadSet();
                if (o != null)
                {
                    return new OverExp(exp.loc, o);
                }
                {
                    Type t = s.getType();
                    if (t != null)
                    {
                        return expressionSemantic(new TypeExp(exp.loc, t), sc);
                    }
                }
                TupleDeclaration tup = s.isTupleDeclaration();
                if (tup != null)
                {
                    if (eleft != null)
                    {
                        e = new DotVarExp(exp.loc, eleft, tup, true);
                        e = expressionSemantic(e, sc);
                        return e;
                    }
                    e = new TupleExp(exp.loc, tup);
                    e = expressionSemantic(e, sc);
                    return e;
                }
                ScopeDsymbol sds = s.isScopeDsymbol();
                if (sds != null)
                {
                    e = new ScopeExp(exp.loc, sds);
                    e = expressionSemantic(e, sc);
                    if (eleft != null)
                        e = new DotExp(exp.loc, eleft, e);
                    return e;
                }
                Import imp = s.isImport();
                if (imp != null)
                {
                    ie = new ScopeExp(exp.loc, imp.pkg);
                    return expressionSemantic(ie, sc);
                }
                throw new AssertionError("Unreachable code!");
            }
            else if (pequals(exp.ident, Id.stringof))
            {
                ByteSlice p = ie.asString().copy();
                e = new StringExp(exp.loc, toBytePtr(p), p.getLength());
                e = expressionSemantic(e, sc);
                return e;
            }
            if (((ie.sds.isPackage() != null || ie.sds.isImport() != null) || ie.sds.isModule() != null))
            {
                flag = 0;
            }
            if ((flag) != 0)
                return null;
            s = ie.sds.search_correct(exp.ident);
            if (s != null)
            {
                if (s.isPackage() != null)
                    exp.error(new BytePtr("undefined identifier `%s` in %s `%s`, perhaps add `static import %s;`"), exp.ident.toChars(), ie.sds.kind(), ie.sds.toPrettyChars(false), s.toPrettyChars(false));
                else
                    exp.error(new BytePtr("undefined identifier `%s` in %s `%s`, did you mean %s `%s`?"), exp.ident.toChars(), ie.sds.kind(), ie.sds.toPrettyChars(false), s.kind(), s.toChars());
            }
            else
                exp.error(new BytePtr("undefined identifier `%s` in %s `%s`"), exp.ident.toChars(), ie.sds.kind(), ie.sds.toPrettyChars(false));
            return new ErrorExp();
        }
        else if (((((((((t1b.ty & 0xFF) == ENUMTY.Tpointer && (exp.e1.type.ty & 0xFF) != ENUMTY.Tenum) && !pequals(exp.ident, Id._init)) && !pequals(exp.ident, Id.__sizeof)) && !pequals(exp.ident, Id.__xalignof)) && !pequals(exp.ident, Id.offsetof)) && !pequals(exp.ident, Id._mangleof)) && !pequals(exp.ident, Id.stringof)))
        {
            Type t1bn = t1b.nextOf();
            if ((flag) != 0)
            {
                AggregateDeclaration ad = isAggregate(t1bn);
                if ((ad != null && ad.members == null))
                    return null;
            }
            if (((flag) != 0 && (t1bn.ty & 0xFF) == ENUMTY.Tvoid))
                return null;
            e = new PtrExp(exp.loc, exp.e1);
            e = expressionSemantic(e, sc);
            return dotExp(e.type, sc, e, exp.ident, flag | (exp.noderef ? DotExpFlag.noDeref : 0));
        }
        else
        {
            if (((exp.e1.op & 0xFF) == 20 || (exp.e1.op & 0xFF) == 36))
                flag = 0;
            e = dotExp(exp.e1.type, sc, exp.e1, exp.ident, flag | (exp.noderef ? DotExpFlag.noDeref : 0));
            if (e != null)
                e = expressionSemantic(e, sc);
            return e;
        }
    }

    public static Expression semanticY(DotTemplateInstanceExp exp, Scope sc, int flag) {
        Function0<Expression> errorExp = new Function0<Expression>(){
            public Expression invoke(){
                return new ErrorExp();
            }
        };
        DotIdExp die = new DotIdExp(exp.loc, exp.e1, exp.ti.name);
        Expression e = semanticX(die, sc);
        if (pequals(e, die))
        {
            exp.e1 = die.e1;
            Type t1b = exp.e1.type.toBasetype();
            if ((((((t1b.ty & 0xFF) == ENUMTY.Tarray || (t1b.ty & 0xFF) == ENUMTY.Tsarray) || (t1b.ty & 0xFF) == ENUMTY.Taarray) || (t1b.ty & 0xFF) == ENUMTY.Tnull) || (t1b.isTypeBasic() != null && (t1b.ty & 0xFF) != ENUMTY.Tvoid)))
            {
                if ((flag) != 0)
                    return null;
            }
            e = semanticY(die, sc, flag);
            if ((flag) != 0)
            {
                if ((!(e != null) || isDotOpDispatch(e)))
                {
                    return null;
                }
            }
        }
        assert(e != null);
        if ((e.op & 0xFF) == 127)
            return e;
        try {
            if ((e.op & 0xFF) == 27)
            {
                DotVarExp dve = (DotVarExp)e;
                {
                    FuncDeclaration fd = dve.var.isFuncDeclaration();
                    if (fd != null)
                    {
                        {
                            TemplateDeclaration td = fd.findTemplateDeclRoot();
                            if (td != null)
                            {
                                e = new DotTemplateExp(dve.loc, dve.e1, td);
                                e = expressionSemantic(e, sc);
                            }
                        }
                    }
                    else {
                        OverDeclaration od = dve.var.isOverDeclaration();
                        if (od != null)
                        {
                            exp.e1 = dve.e1;
                            if (!(exp.findTempDecl(sc)))
                                /*goto Lerr*/throw Dispatch0.INSTANCE;
                            if (exp.ti.needsTypeInference(sc, 0))
                                return exp;
                            dsymbolSemantic(exp.ti, sc);
                            if ((!(exp.ti.inst != null) || exp.ti.errors))
                                return errorExp.invoke();
                            {
                                Declaration v = exp.ti.toAlias().isDeclaration();
                                if (v != null)
                                {
                                    if ((v.type != null && v.type.deco == null))
                                        v.type = typeSemantic(v.type, v.loc, sc);
                                    return expressionSemantic(new DotVarExp(exp.loc, exp.e1, v, true), sc);
                                }
                            }
                            return expressionSemantic(new DotExp(exp.loc, exp.e1, new ScopeExp(exp.loc, exp.ti)), sc);
                        }
                    }
                }
            }
            else if ((e.op & 0xFF) == 26)
            {
                VarExp ve = (VarExp)e;
                {
                    FuncDeclaration fd = ve.var.isFuncDeclaration();
                    if (fd != null)
                    {
                        {
                            TemplateDeclaration td = fd.findTemplateDeclRoot();
                            if (td != null)
                            {
                                e = expressionSemantic(new TemplateExp(ve.loc, td, null), sc);
                            }
                        }
                    }
                    else {
                        OverDeclaration od = ve.var.isOverDeclaration();
                        if (od != null)
                        {
                            exp.ti.tempdecl = od;
                            return expressionSemantic(new ScopeExp(exp.loc, exp.ti), sc);
                        }
                    }
                }
            }
            if ((e.op & 0xFF) == 37)
            {
                DotTemplateExp dte = (DotTemplateExp)e;
                exp.e1 = dte.e1;
                exp.ti.tempdecl = dte.td;
                if (!(exp.ti.semanticTiargs(sc)))
                    return errorExp.invoke();
                if (exp.ti.needsTypeInference(sc, 0))
                    return exp;
                dsymbolSemantic(exp.ti, sc);
                if ((!(exp.ti.inst != null) || exp.ti.errors))
                    return errorExp.invoke();
                {
                    Declaration v = exp.ti.toAlias().isDeclaration();
                    if (v != null)
                    {
                        if ((v.isFuncDeclaration() != null || v.isVarDeclaration() != null))
                        {
                            return expressionSemantic(new DotVarExp(exp.loc, exp.e1, v, true), sc);
                        }
                    }
                }
                return expressionSemantic(new DotExp(exp.loc, exp.e1, new ScopeExp(exp.loc, exp.ti)), sc);
            }
            else if ((e.op & 0xFF) == 36)
            {
                exp.ti.tempdecl = ((TemplateExp)e).td;
                return expressionSemantic(new ScopeExp(exp.loc, exp.ti), sc);
            }
            else if ((e.op & 0xFF) == 97)
            {
                DotExp de = (DotExp)e;
                if ((de.e2.op & 0xFF) == 214)
                {
                    if ((!(exp.findTempDecl(sc)) || !(exp.ti.semanticTiargs(sc))))
                    {
                        return errorExp.invoke();
                    }
                    if (exp.ti.needsTypeInference(sc, 0))
                        return exp;
                    dsymbolSemantic(exp.ti, sc);
                    if ((!(exp.ti.inst != null) || exp.ti.errors))
                        return errorExp.invoke();
                    {
                        Declaration v = exp.ti.toAlias().isDeclaration();
                        if (v != null)
                        {
                            if ((v.type != null && v.type.deco == null))
                                v.type = typeSemantic(v.type, v.loc, sc);
                            return expressionSemantic(new DotVarExp(exp.loc, exp.e1, v, true), sc);
                        }
                    }
                    return expressionSemantic(new DotExp(exp.loc, exp.e1, new ScopeExp(exp.loc, exp.ti)), sc);
                }
            }
            else if ((e.op & 0xFF) == 214)
            {
                OverExp oe = (OverExp)e;
                exp.ti.tempdecl = oe.vars;
                return expressionSemantic(new ScopeExp(exp.loc, exp.ti), sc);
            }
        }
        catch(Dispatch0 __d){}
    /*Lerr:*/
        exp.error(new BytePtr("`%s` isn't a template"), e.toChars());
        return errorExp.invoke();
    }

    public static boolean checkAddressVar(Scope sc, UnaExp exp, VarDeclaration v) {
        if (v != null)
        {
            if (!(v.canTakeAddressOf()))
            {
                exp.error(new BytePtr("cannot take address of `%s`"), exp.e1.toChars());
                return false;
            }
            if ((((sc).func != null && !(((sc).intypeof) != 0)) && !(v.isDataseg())))
            {
                BytePtr p = pcopy(v.isParameter() ? new BytePtr("parameter") : new BytePtr("local"));
                if (global.params.vsafe)
                {
                    v.storage_class &= -281474976710657L;
                    v.doNotInferScope = true;
                    if ((((v.storage_class & 524288L) != 0 && (sc).func.setUnsafe()) && !(((sc).flags & 8) != 0)))
                    {
                        exp.error(new BytePtr("cannot take address of `scope` %s `%s` in `@safe` function `%s`"), p, v.toChars(), (sc).func.toChars());
                        return false;
                    }
                }
                else if (((sc).func.setUnsafe() && !(((sc).flags & 8) != 0)))
                {
                    exp.error(new BytePtr("cannot take address of %s `%s` in `@safe` function `%s`"), p, v.toChars(), (sc).func.toChars());
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkFunctionAttributes(Expression exp, Scope sc, FuncDeclaration f) {
        {
            boolean error = __withSym.checkDisabled(sc, f);
            (error ? 1 : 0) |= (__withSym.checkDeprecated(sc, f) ? 1 : 0);
            (error ? 1 : 0) |= (__withSym.checkPurity(sc, f) ? 1 : 0);
            (error ? 1 : 0) |= (__withSym.checkSafety(sc, f) ? 1 : 0);
            (error ? 1 : 0) |= (__withSym.checkNogc(sc, f) ? 1 : 0);
            return error;
        }
    }

    public static Expression getThisSkipNestedFuncs(Loc loc, Scope sc, Dsymbol s, AggregateDeclaration ad, Expression e1, Type t, Dsymbol var, boolean flag) {
        int n = 0;
        for (; (s != null && s.isFuncDeclaration() != null);){
            FuncDeclaration f = s.isFuncDeclaration();
            if (f.vthis != null)
            {
                n++;
                e1 = new VarExp(loc, f.vthis, true);
                if (f.isThis2)
                {
                    if (n > 1)
                        e1 = expressionSemantic(e1, sc);
                    e1 = new PtrExp(loc, e1);
                    int i = (followInstantiationContextAggregateDeclaration(f, ad) ? 1 : 0);
                    e1 = new IndexExp(loc, e1, new IntegerExp((long)i));
                    s = toParentPAggregateDeclaration(f, ad);
                    continue;
                }
            }
            else
            {
                if (flag)
                    return null;
                e1.error(new BytePtr("need `this` of type `%s` to access member `%s` from static function `%s`"), ad.toChars(), var.toChars(), f.toChars());
                e1 = new ErrorExp();
                return e1;
            }
            s = s.toParent2();
        }
        if ((n > 1 || (e1.op & 0xFF) == 62))
            e1 = expressionSemantic(e1, sc);
        if ((s != null && e1.type.equivalent(Type.tvoidptr)))
        {
            {
                AggregateDeclaration sad = s.isAggregateDeclaration();
                if (sad != null)
                {
                    Type ta = sad.handleType();
                    if ((ta.ty & 0xFF) == ENUMTY.Tstruct)
                        ta = ta.pointerTo();
                    e1.type = ta;
                }
            }
        }
        e1.type = e1.type.addMod(t.mod);
        return e1;
    }

    public static VarDeclaration makeThis2Argument(Loc loc, Scope sc, FuncDeclaration fd) {
        Type tthis2 = Type.tvoidptr.sarrayOf(2L);
        VarDeclaration vthis2 = new VarDeclaration(loc, tthis2, Identifier.generateId(new BytePtr("__this")), null, 0L);
        vthis2.storage_class |= 1099511627776L;
        dsymbolSemantic(vthis2, sc);
        vthis2.parent = (sc).parent;
        assert((sc).func != null);
        (sc).func.closureVars.push(vthis2);
        vthis2.nestedrefs.push(fd);
        return vthis2;
    }

    public static boolean verifyHookExist(Loc loc, Scope sc, Identifier id, ByteSlice description, Identifier module_) {
        Dsymbol rootSymbol = sc.search(loc, Id.empty, null, 0);
        {
            Dsymbol moduleSymbol = rootSymbol.search(loc, module_, 0);
            if (moduleSymbol != null)
                if (moduleSymbol.search(loc, id, 0) != null)
                    return true;
        }
        error(loc, new BytePtr("`%s.%s` not found. The current runtime does not support %.*s, or the runtime is corrupt."), module_.toChars(), id.toChars(), description.getLength(), toBytePtr(description));
        return false;
    }

}
