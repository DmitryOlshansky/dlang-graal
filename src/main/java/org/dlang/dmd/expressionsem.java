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
    static int visitnest = 0;
    static ByteSlice visitcompMsg = new ByteSlice("==");

    static boolean LOGSEMANTIC = false;
    public static boolean expressionsToString(OutBuffer buf, Ptr<Scope> sc, Ptr<DArray<Expression>> exps) {
        Ref<OutBuffer> buf_ref = ref(buf);
        if (exps == null)
            return false;
        {
            Slice<Expression> __r1352 = (exps.get()).opSlice().copy();
            int __key1353 = 0;
            for (; (__key1353 < __r1352.getLength());__key1353 += 1) {
                Expression ex = __r1352.get(__key1353);
                if (ex == null)
                    continue;
                Ptr<Scope> sc2 = (sc.get()).startCTFE();
                Expression e2 = expressionSemantic(ex, sc2);
                Expression e3 = resolveProperties(sc2, e2);
                (sc2.get()).endCTFE();
                Expression e4 = ctfeInterpretForPragmaMsg(e3);
                if ((e4 == null) || ((e4.op.value & 0xFF) == 127))
                    return true;
                {
                    TupleExp te = e4.isTupleExp();
                    if ((te) != null)
                    {
                        if (expressionsToString(buf_ref, sc, te.exps.value))
                            return true;
                        continue;
                    }
                }
                IntegerExp ie = e4.isIntegerExp();
                int ty = (ie != null) && (ie.type.value != null) ? (ie.type.value.ty.value & 0xFF) : 34;
                if ((ty == 31) || (ty == 32) || (ty == 33))
                {
                    TypeSArray tsa = new TypeSArray(ie.type.value, new IntegerExp(1L));
                    e4 = new ArrayLiteralExp(ex.loc.value, tsa, ie);
                }
                {
                    StringExp se = e4.toStringExp();
                    if ((se) != null)
                        buf_ref.value.writestring(se.toUTF8(sc).peekSlice());
                    else
                        buf_ref.value.writestring(e4.asString());
                }
            }
        }
        return false;
    }

    public static StringExp semanticString(Ptr<Scope> sc, Expression exp, BytePtr s) {
        sc = (sc.get()).startCTFE();
        exp = expressionSemantic(exp, sc);
        exp = resolveProperties(sc, exp);
        sc = (sc.get()).endCTFE();
        if (((exp.op.value & 0xFF) == 127))
            return null;
        Expression e = exp;
        if (exp.type.value.isString())
        {
            e = e.ctfeInterpret();
            if (((e.op.value & 0xFF) == 127))
                return null;
        }
        StringExp se = e.toStringExp();
        if (se == null)
        {
            exp.error(new BytePtr("`string` expected for %s, not `(%s)` of type `%s`"), s, exp.toChars(), exp.type.value.toChars());
            return null;
        }
        return se;
    }

    public static Expression extractOpDollarSideEffect(Ptr<Scope> sc, UnaExp ue) {
        Ref<Expression> e0 = ref(null);
        Expression e1 = Expression.extractLast(ue.e1.value, e0);
        if (!isTrivialExp(e1))
        {
            e1 = extractSideEffect(sc, new BytePtr("__dop"), e0, e1, false);
            assert(((e1.op.value & 0xFF) == 26));
            VarExp ve = (VarExp)e1;
            ve.var.value.storage_class.value |= 140737488355328L;
        }
        ue.e1.value = e1;
        return e0.value;
    }

    public static Expression resolveOpDollar(Ptr<Scope> sc, ArrayExp ae, Ptr<Expression> pe0) {
        assert(ae.lengthVar.value == null);
        pe0.set(0, null);
        AggregateDeclaration ad = isAggregate(ae.e1.value.type.value);
        Dsymbol slice = search_function(ad, Id.slice.value);
        {
            Slice<Expression> __r1355 = (ae.arguments.value.get()).opSlice().copy();
            int __key1354 = 0;
        L_outer1:
            for (; (__key1354 < __r1355.getLength());__key1354 += 1) {
                Expression e = __r1355.get(__key1354);
                int i = __key1354;
                if ((i == 0))
                    pe0.set(0, extractOpDollarSideEffect(sc, ae));
                if (((e.op.value & 0xFF) == 231) && !((slice != null) && (slice.isTemplateDeclaration() != null)))
                {
                /*Lfallback:*/
                    if (((ae.arguments.value.get()).length.value == 1))
                        return null;
                    ae.error(new BytePtr("multi-dimensional slicing requires template `opSlice`"));
                    return new ErrorExp();
                }
                ArrayScopeSymbol sym = new ArrayScopeSymbol(sc, ae);
                sym.parent.value = (sc.get()).scopesym.value;
                sc = (sc.get()).push(sym);
                ae.lengthVar.value = null;
                ae.currentDimension = i;
                e = expressionSemantic(e, sc);
                e = resolveProperties(sc, e);
                if ((ae.lengthVar.value != null) && ((sc.get()).func.value != null))
                {
                    Expression de = new DeclarationExp(ae.loc.value, ae.lengthVar.value);
                    de = expressionSemantic(de, sc);
                    pe0.set(0, Expression.combine(pe0.get(), de));
                }
                sc = (sc.get()).pop();
                if (((e.op.value & 0xFF) == 231))
                {
                    IntervalExp ie = (IntervalExp)e;
                    Ptr<DArray<RootObject>> tiargs = refPtr(new DArray<RootObject>());
                    Expression edim = new IntegerExp(ae.loc.value, (long)i, Type.tsize_t.value);
                    edim = expressionSemantic(edim, sc);
                    (tiargs.get()).push(edim);
                    Ptr<DArray<Expression>> fargs = refPtr(new DArray<Expression>(2));
                    fargs.get().set(0, ie.lwr.value);
                    fargs.get().set(1, ie.upr.value);
                    int xerrors = global.startGagging();
                    sc = (sc.get()).push();
                    FuncDeclaration fslice = resolveFuncCall(ae.loc.value, sc, slice, tiargs, ae.e1.value.type.value, fargs, FuncResolveFlag.quiet);
                    sc = (sc.get()).pop();
                    global.endGagging(xerrors);
                    if (fslice == null)
                        /*goto Lfallback*/throw Dispatch0.INSTANCE;
                    e = new DotTemplateInstanceExp(ae.loc.value, ae.e1.value, slice.ident.value, tiargs);
                    e = new CallExp(ae.loc.value, e, fargs);
                    e = expressionSemantic(e, sc);
                }
                if (e.type.value == null)
                {
                    ae.error(new BytePtr("`%s` has no value"), e.toChars());
                    e = new ErrorExp();
                }
                if (((e.op.value & 0xFF) == 127))
                    return e;
                ae.arguments.value.get().set(i, e);
            }
        }
        return ae;
    }

    public static Expression resolveOpDollar(Ptr<Scope> sc, ArrayExp ae, IntervalExp ie, Ptr<Expression> pe0) {
        if (ie == null)
            return ae;
        VarDeclaration lengthVar = ae.lengthVar.value;
        ArrayScopeSymbol sym = new ArrayScopeSymbol(sc, ae);
        sym.parent.value = (sc.get()).scopesym.value;
        sc = (sc.get()).push(sym);
        {
            int __key1356 = 0;
            int __limit1357 = 2;
            for (; (__key1356 < __limit1357);__key1356 += 1) {
                int i = __key1356;
                Expression e = (i == 0) ? ie.lwr.value : ie.upr.value;
                e = expressionSemantic(e, sc);
                e = resolveProperties(sc, e);
                if (e.type.value == null)
                {
                    ae.error(new BytePtr("`%s` has no value"), e.toChars());
                    return new ErrorExp();
                }
                ((i == 0) ? ptr(ie.lwr) : ptr(ie.upr)).set(0, e);
            }
        }
        if ((!pequals(lengthVar, ae.lengthVar.value)) && ((sc.get()).func.value != null))
        {
            Expression de = new DeclarationExp(ae.loc.value, ae.lengthVar.value);
            de = expressionSemantic(de, sc);
            pe0.set(0, Expression.combine(pe0.get(), de));
        }
        sc = (sc.get()).pop();
        return ae;
    }

    public static boolean arrayExpressionSemantic(Ptr<DArray<Expression>> exps, Ptr<Scope> sc, boolean preserveErrors) {
        boolean err = false;
        if (exps != null)
        {
            {
                Slice<Expression> __r1358 = (exps.get()).opSlice().copy();
                int __key1359 = 0;
                for (; (__key1359 < __r1358.getLength());__key1359 += 1) {
                    Expression e = __r1358.get(__key1359);
                    if (e != null)
                    {
                        Expression e2 = expressionSemantic(e, sc);
                        if (((e2.op.value & 0xFF) == 127))
                            err = true;
                        if (preserveErrors || ((e2.op.value & 0xFF) != 127))
                            e = e2;
                    }
                }
            }
        }
        return err;
    }

    // defaulted all parameters starting with #3
    public static boolean arrayExpressionSemantic(Ptr<DArray<Expression>> exps, Ptr<Scope> sc) {
        return arrayExpressionSemantic(exps, sc, false);
    }

    public static boolean checkPropertyCall(Expression e) {
        e = lastComma(e);
        if (((e.op.value & 0xFF) == 18))
        {
            CallExp ce = (CallExp)e;
            TypeFunction tf = null;
            if (ce.f.value != null)
            {
                tf = (TypeFunction)ce.f.value.type.value;
                if ((tf.deco.value == null) && (ce.f.value.semanticRun.value < PASS.semanticdone))
                {
                    dsymbolSemantic(ce.f.value, null);
                    tf = (TypeFunction)ce.f.value.type.value;
                }
            }
            else if (((ce.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tfunction))
                tf = (TypeFunction)ce.e1.value.type.value;
            else if (((ce.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tdelegate))
                tf = (TypeFunction)ce.e1.value.type.value.nextOf();
            else if (((ce.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((ce.e1.value.type.value.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction))
                tf = (TypeFunction)ce.e1.value.type.value.nextOf();
            else
                throw new AssertionError("Unreachable code!");
        }
        return false;
    }

    public static Expression searchUFCS(Ptr<Scope> sc, UnaExp ue, Identifier ident) {
        Ref<Ptr<Scope>> sc_ref = ref(sc);
        Ref<Identifier> ident_ref = ref(ident);
        Loc loc = ue.loc.value.copy();
        Function1<Integer,Dsymbol> searchScopes = new Function1<Integer,Dsymbol>(){
            public Dsymbol invoke(Integer flags) {
                IntRef flags_ref = ref(flags);
                Ref<Dsymbol> s = ref(null);
                {
                    Ref<Ptr<Scope>> scx = ref(sc_ref.value);
                    for (; scx.value != null;scx.value = (scx.value.get()).enclosing.value){
                        if ((scx.value.get()).scopesym.value == null)
                            continue;
                        if ((scx.value.get()).scopesym.value.isModule() != null)
                            flags_ref.value |= 32;
                        s.value = (scx.value.get()).scopesym.value.search(loc, ident_ref.value, flags_ref.value);
                        if (s.value != null)
                        {
                            if (s.value.isOverloadSet() != null)
                                break;
                            {
                                Ref<AliasDeclaration> ad = ref(s.value.isAliasDeclaration());
                                if ((ad.value) != null)
                                {
                                    if (ad.value._import.value != null)
                                        break;
                                }
                            }
                            Ref<Dsymbol> p = ref(s.value.toParent2());
                            if ((p.value != null) && (p.value.isModule() != null))
                                break;
                        }
                        s.value = null;
                        if (((scx.value.get()).scopesym.value.isModule() != null) && !(((scx.value.get()).enclosing.value != null) && (((scx.value.get()).enclosing.value.get()).enclosing.value == null)))
                            break;
                    }
                }
                return s.value;
            }
        };
        int flags = 0;
        Dsymbol s = null;
        if (((sc_ref.value.get()).flags.value & 512) != 0)
            flags |= 128;
        s = searchScopes.invoke(flags | 8);
        if (s == null)
        {
            s = searchScopes.invoke(flags | 16);
        }
        if (s == null)
            return getProperty(ue.e1.value.type.value.Type, loc, ident_ref.value, 0);
        FuncDeclaration f = s.isFuncDeclaration();
        if (f != null)
        {
            TemplateDeclaration td = getFuncTemplateDecl(f);
            if (td != null)
            {
                if (td.overroot.value != null)
                    td = td.overroot.value;
                s = td;
            }
        }
        if (((ue.op.value & 0xFF) == 29))
        {
            DotTemplateInstanceExp dti = (DotTemplateInstanceExp)ue;
            TemplateInstance ti = new TemplateInstance(loc, s.ident.value, dti.ti.tiargs.value);
            if (!ti.updateTempDecl(sc_ref.value, s))
                return new ErrorExp();
            return new ScopeExp(loc, ti);
        }
        else
        {
            return new DsymbolExp(loc, s, true);
        }
    }

    public static Expression resolveUFCS(Ptr<Scope> sc, CallExp ce) {
        Loc loc = ce.loc.value.copy();
        Expression eleft = null;
        Expression e = null;
        if (((ce.e1.value.op.value & 0xFF) == 28))
        {
            DotIdExp die = (DotIdExp)ce.e1.value;
            Identifier ident = die.ident;
            Expression ex = semanticX(die, sc);
            if ((!pequals(ex, die)))
            {
                ce.e1.value = ex;
                return null;
            }
            eleft = die.e1.value;
            Type t = eleft.type.value.toBasetype();
            if (((t.ty.value & 0xFF) == ENUMTY.Tarray) || ((t.ty.value & 0xFF) == ENUMTY.Tsarray) || ((t.ty.value & 0xFF) == ENUMTY.Tnull) || (t.isTypeBasic() != null) && ((t.ty.value & 0xFF) != ENUMTY.Tvoid))
            {
            }
            else if (((t.ty.value & 0xFF) == ENUMTY.Taarray))
            {
                if ((pequals(ident, Id.remove)))
                {
                    if ((ce.arguments.value == null) || ((ce.arguments.value.get()).length.value != 1))
                    {
                        ce.error(new BytePtr("expected key as argument to `aa.remove()`"));
                        return new ErrorExp();
                    }
                    if (!eleft.type.value.isMutable())
                    {
                        ce.error(new BytePtr("cannot remove key from `%s` associative array `%s`"), MODtoChars(t.mod.value), eleft.toChars());
                        return new ErrorExp();
                    }
                    Expression key = (ce.arguments.value.get()).get(0);
                    key = expressionSemantic(key, sc);
                    key = resolveProperties(sc, key);
                    TypeAArray taa = (TypeAArray)t;
                    key = key.implicitCastTo(sc, taa.index.value);
                    if (key.checkValue())
                        return new ErrorExp();
                    semanticTypeInfo(sc, taa.index.value);
                    return new RemoveExp(loc, eleft, key);
                }
            }
            else
            {
                if (arrayExpressionSemantic(ce.arguments.value, sc, false))
                    return new ErrorExp();
                {
                    Expression ey = semanticY(die, sc, 1);
                    if ((ey) != null)
                    {
                        if (((ey.op.value & 0xFF) == 127))
                            return ey;
                        ce.e1.value = ey;
                        if (isDotOpDispatch(ey))
                        {
                            int errors = global.startGagging();
                            e = expressionSemantic(ce.syntaxCopy(), sc);
                            if (!global.endGagging(errors))
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
                if ((pequals(ident, Id.remove)))
                {
                    Expression alias_e = resolveAliasThis(sc, die.e1.value, true);
                    if ((alias_e != null) && (!pequals(alias_e, die.e1.value)))
                    {
                        die.e1.value = alias_e;
                        CallExp ce2 = (CallExp)ce.syntaxCopy();
                        ce2.e1.value = die;
                        e = (CallExp)trySemantic(ce2, sc);
                        if (e != null)
                            return e;
                    }
                }
                searchUFCS(sc, die, ident);
            }
        }
        else if (((ce.e1.value.op.value & 0xFF) == 29))
        {
            DotTemplateInstanceExp dti = (DotTemplateInstanceExp)ce.e1.value;
            {
                Expression ey = semanticY(dti, sc, 1);
                if ((ey) != null)
                {
                    ce.e1.value = ey;
                    return null;
                }
            }
            eleft = dti.e1.value;
            e = searchUFCS(sc, dti, dti.ti.name.value);
        }
        else
            return null;
        ce.e1.value = e;
        if (ce.arguments.value == null)
            ce.arguments.value = refPtr(new DArray<Expression>());
        (ce.arguments.value.get()).shift(eleft);
        return null;
    }

    public static Expression resolveUFCSProperties(Ptr<Scope> sc, Expression e1, Expression e2) {
        Loc loc = e1.loc.value.copy();
        Expression eleft = null;
        Expression e = null;
        if (((e1.op.value & 0xFF) == 28))
        {
            DotIdExp die = (DotIdExp)e1;
            eleft = die.e1.value;
            e = searchUFCS(sc, die, die.ident);
        }
        else if (((e1.op.value & 0xFF) == 29))
        {
            DotTemplateInstanceExp dti = null;
            dti = (DotTemplateInstanceExp)e1;
            eleft = dti.e1.value;
            e = searchUFCS(sc, dti, dti.ti.name.value);
        }
        else
            return null;
        if ((e == null))
            return null;
        if (e2 != null)
        {
            e2 = expressionSemantic(e2, sc);
            Expression ex = e.copy();
            Ptr<DArray<Expression>> a1 = refPtr(new DArray<Expression>(1));
            a1.get().set(0, eleft);
            ex = new CallExp(loc, ex, a1);
            ex = trySemantic(ex, sc);
            Ptr<DArray<Expression>> a2 = refPtr(new DArray<Expression>(2));
            a2.get().set(0, eleft);
            a2.get().set(1, e2);
            e = new CallExp(loc, e, a2);
            if (ex != null)
            {
                e = trySemantic(e, sc);
                if (e == null)
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
            Ptr<DArray<Expression>> arguments = refPtr(new DArray<Expression>(1));
            arguments.get().set(0, eleft);
            e = new CallExp(loc, e, arguments);
            e = expressionSemantic(e, sc);
            checkPropertyCall(e);
            return expressionSemantic(e, sc);
        }
    }

    // defaulted all parameters starting with #3
    public static Expression resolveUFCSProperties(Ptr<Scope> sc, Expression e1) {
        return resolveUFCSProperties(sc, e1, null);
    }

    public static Expression resolvePropertiesOnly(Ptr<Scope> sc, Expression e1) {
        OverloadSet os = null;
        FuncDeclaration fd = null;
        TemplateDeclaration td = null;
        if (((e1.op.value & 0xFF) == 97))
        {
            DotExp de = (DotExp)e1;
            if (((de.e2.value.op.value & 0xFF) == 214))
            {
                os = ((OverExp)de.e2.value).vars;
                /*goto Los*//*unrolled goto*/
            /*Los:*/
                assert(os != null);
                {
                    Slice<Dsymbol> __r1360 = os.a.opSlice().copy();
                    int __key1361 = 0;
                    for (; (__key1361 < __r1360.getLength());__key1361 += 1) {
                        Dsymbol s = __r1360.get(__key1361);
                        fd = s.isFuncDeclaration();
                        td = s.isTemplateDeclaration();
                        if (fd != null)
                        {
                            if (((TypeFunction)fd.type.value).isproperty.value)
                                return resolveProperties(sc, e1);
                        }
                        else if ((td != null) && (td.onemember.value != null) && ((fd = td.onemember.value.isFuncDeclaration()) != null))
                        {
                            if (((TypeFunction)fd.type.value).isproperty.value || ((fd.storage_class2 & 4294967296L) != 0) || (((td._scope.value.get()).stc.value & 4294967296L) != 0))
                            {
                                return resolveProperties(sc, e1);
                            }
                        }
                    }
                }
            }
        }
        else if (((e1.op.value & 0xFF) == 214))
        {
            os = ((OverExp)e1).vars;
        /*Los:*/
            assert(os != null);
            {
                Slice<Dsymbol> __r1360 = os.a.opSlice().copy();
                int __key1361 = 0;
                for (; (__key1361 < __r1360.getLength());__key1361 += 1) {
                    Dsymbol s = __r1360.get(__key1361);
                    fd = s.isFuncDeclaration();
                    td = s.isTemplateDeclaration();
                    if (fd != null)
                    {
                        if (((TypeFunction)fd.type.value).isproperty.value)
                            return resolveProperties(sc, e1);
                    }
                    else if ((td != null) && (td.onemember.value != null) && ((fd = td.onemember.value.isFuncDeclaration()) != null))
                    {
                        if (((TypeFunction)fd.type.value).isproperty.value || ((fd.storage_class2 & 4294967296L) != 0) || (((td._scope.value.get()).stc.value & 4294967296L) != 0))
                        {
                            return resolveProperties(sc, e1);
                        }
                    }
                }
            }
        }
        else if (((e1.op.value & 0xFF) == 29))
        {
            DotTemplateInstanceExp dti = (DotTemplateInstanceExp)e1;
            if ((dti.ti.tempdecl.value != null) && ((td = dti.ti.tempdecl.value.isTemplateDeclaration()) != null))
                /*goto Ltd*//*unrolled goto*/
            /*Ltd:*/
                assert(td != null);
                if ((td.onemember.value != null) && ((fd = td.onemember.value.isFuncDeclaration()) != null))
                {
                    if (((TypeFunction)fd.type.value).isproperty.value || ((fd.storage_class2 & 4294967296L) != 0) || (((td._scope.value.get()).stc.value & 4294967296L) != 0))
                    {
                        return resolveProperties(sc, e1);
                    }
                }
        }
        else if (((e1.op.value & 0xFF) == 37))
        {
            td = ((DotTemplateExp)e1).td.value;
            /*goto Ltd*//*unrolled goto*/
        /*Ltd:*/
            assert(td != null);
            if ((td.onemember.value != null) && ((fd = td.onemember.value.isFuncDeclaration()) != null))
            {
                if (((TypeFunction)fd.type.value).isproperty.value || ((fd.storage_class2 & 4294967296L) != 0) || (((td._scope.value.get()).stc.value & 4294967296L) != 0))
                {
                    return resolveProperties(sc, e1);
                }
            }
        }
        else if (((e1.op.value & 0xFF) == 203))
        {
            Dsymbol s = ((ScopeExp)e1).sds.value;
            TemplateInstance ti = s.isTemplateInstance();
            if ((ti != null) && (ti.semanticRun.value == 0) && (ti.tempdecl.value != null))
            {
                if (((td = ti.tempdecl.value.isTemplateDeclaration()) != null))
                    /*goto Ltd*//*unrolled goto*/
                /*Ltd:*/
                    assert(td != null);
                    if ((td.onemember.value != null) && ((fd = td.onemember.value.isFuncDeclaration()) != null))
                    {
                        if (((TypeFunction)fd.type.value).isproperty.value || ((fd.storage_class2 & 4294967296L) != 0) || (((td._scope.value.get()).stc.value & 4294967296L) != 0))
                        {
                            return resolveProperties(sc, e1);
                        }
                    }
            }
        }
        else if (((e1.op.value & 0xFF) == 36))
        {
            td = ((TemplateExp)e1).td.value;
        /*Ltd:*/
            assert(td != null);
            if ((td.onemember.value != null) && ((fd = td.onemember.value.isFuncDeclaration()) != null))
            {
                if (((TypeFunction)fd.type.value).isproperty.value || ((fd.storage_class2 & 4294967296L) != 0) || (((td._scope.value.get()).stc.value & 4294967296L) != 0))
                {
                    return resolveProperties(sc, e1);
                }
            }
        }
        else if (((e1.op.value & 0xFF) == 27) && ((e1.type.value.ty.value & 0xFF) == ENUMTY.Tfunction))
        {
            DotVarExp dve = (DotVarExp)e1;
            fd = dve.var.value.isFuncDeclaration();
            /*goto Lfd*//*unrolled goto*/
        /*Lfd:*/
            assert(fd != null);
            if (((TypeFunction)fd.type.value).isproperty.value)
                return resolveProperties(sc, e1);
        }
        else if (((e1.op.value & 0xFF) == 26) && (e1.type.value != null) && ((e1.type.value.ty.value & 0xFF) == ENUMTY.Tfunction) && ((sc.get()).intypeof.value != 0) || !((VarExp)e1).var.value.needThis())
        {
            fd = ((VarExp)e1).var.value.isFuncDeclaration();
        /*Lfd:*/
            assert(fd != null);
            if (((TypeFunction)fd.type.value).isproperty.value)
                return resolveProperties(sc, e1);
        }
        return e1;
    }

    public static Expression symbolToExp(Dsymbol s, Loc loc, Ptr<Scope> sc, boolean hasOverloads) {
        while(true) try {
        /*Lagain:*/
            Expression e = null;
            Dsymbol olds = s;
            Declaration d = s.isDeclaration();
            if ((d != null) && ((d.storage_class.value & 262144L) != 0))
            {
                s = s.toAlias();
            }
            else
            {
                if (s.isFuncDeclaration() == null)
                {
                    s.checkDeprecated(loc, sc);
                    if (d != null)
                        d.checkDisabled(loc, sc, false);
                }
                s = s.toAlias();
                if ((!pequals(s, olds)) && (s.isFuncDeclaration() == null))
                {
                    s.checkDeprecated(loc, sc);
                    if (d != null)
                        d.checkDisabled(loc, sc, false);
                }
            }
            {
                EnumMember em = s.isEnumMember();
                if ((em) != null)
                {
                    return em.getVarExp(loc, sc);
                }
            }
            {
                VarDeclaration v = s.isVarDeclaration();
                if ((v) != null)
                {
                    if (((sc.get()).intypeof.value == 1) && (v.inuse.value == 0))
                        dsymbolSemantic(v, sc);
                    if ((v.type.value == null) || (v.type.value.deco.value == null) && (v.inuse.value != 0))
                    {
                        if (v.inuse.value != 0)
                            error(loc, new BytePtr("circular reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                        else
                            error(loc, new BytePtr("forward reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                        return new ErrorExp();
                    }
                    if (((v.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                        return new ErrorExp();
                    if (((v.storage_class.value & 8388608L) != 0) && (v._init.value != null))
                    {
                        if (v.inuse.value != 0)
                        {
                            error(loc, new BytePtr("circular initialization of %s `%s`"), v.kind(), v.toPrettyChars(false));
                            return new ErrorExp();
                        }
                        e = v.expandInitializer(loc);
                        v.inuse.value++;
                        e = expressionSemantic(e, sc);
                        v.inuse.value--;
                        return e;
                    }
                    if (v.checkNestedReference(sc, loc))
                        return new ErrorExp();
                    if (v.needThis() && (hasThis(sc) != null))
                        e = new DotVarExp(loc, new ThisExp(loc), v, true);
                    else
                        e = new VarExp(loc, v, true);
                    e = expressionSemantic(e, sc);
                    return e;
                }
            }
            {
                FuncLiteralDeclaration fld = s.isFuncLiteralDeclaration();
                if ((fld) != null)
                {
                    e = new FuncExp(loc, fld);
                    return expressionSemantic(e, sc);
                }
            }
            {
                FuncDeclaration f = s.isFuncDeclaration();
                if ((f) != null)
                {
                    f = f.toAliasFunc();
                    if (!f.functionSemantic())
                        return new ErrorExp();
                    if (!hasOverloads && f.checkForwardRef(loc))
                        return new ErrorExp();
                    FuncDeclaration fd = s.isFuncDeclaration();
                    fd.type.value = f.type.value;
                    return new VarExp(loc, fd, hasOverloads);
                }
            }
            {
                OverDeclaration od = s.isOverDeclaration();
                if ((od) != null)
                {
                    e = new VarExp(loc, od, true);
                    e.type.value = Type.tvoid.value;
                    return e;
                }
            }
            {
                OverloadSet o = s.isOverloadSet();
                if ((o) != null)
                {
                    return new OverExp(loc, o);
                }
            }
            {
                Import imp = s.isImport();
                if ((imp) != null)
                {
                    if (imp.pkg.value == null)
                    {
                        error(loc, new BytePtr("forward reference of import `%s`"), imp.toChars());
                        return new ErrorExp();
                    }
                    ScopeExp ie = new ScopeExp(loc, imp.pkg.value);
                    return expressionSemantic(ie, sc);
                }
            }
            {
                dmodule.Package pkg = s.isPackage();
                if ((pkg) != null)
                {
                    ScopeExp ie = new ScopeExp(loc, pkg);
                    return expressionSemantic(ie, sc);
                }
            }
            {
                dmodule.Module mod = s.isModule();
                if ((mod) != null)
                {
                    ScopeExp ie = new ScopeExp(loc, mod);
                    return expressionSemantic(ie, sc);
                }
            }
            {
                Nspace ns = s.isNspace();
                if ((ns) != null)
                {
                    ScopeExp ie = new ScopeExp(loc, ns);
                    return expressionSemantic(ie, sc);
                }
            }
            {
                Type t = s.getType();
                if ((t) != null)
                {
                    return expressionSemantic(new TypeExp(loc, t), sc);
                }
            }
            {
                TupleDeclaration tup = s.isTupleDeclaration();
                if ((tup) != null)
                {
                    if (tup.needThis() && (hasThis(sc) != null))
                        e = new DotVarExp(loc, new ThisExp(loc), tup, true);
                    else
                        e = new TupleExp(loc, tup);
                    e = expressionSemantic(e, sc);
                    return e;
                }
            }
            {
                TemplateInstance ti = s.isTemplateInstance();
                if ((ti) != null)
                {
                    dsymbolSemantic(ti, sc);
                    if ((ti.inst.value == null) || ti.errors.value)
                        return new ErrorExp();
                    s = ti.toAlias();
                    if (s.isTemplateInstance() == null)
                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                    e = new ScopeExp(loc, ti);
                    e = expressionSemantic(e, sc);
                    return e;
                }
            }
            {
                TemplateDeclaration td = s.isTemplateDeclaration();
                if ((td) != null)
                {
                    Dsymbol p = td.toParentLocal();
                    FuncDeclaration fdthis = hasThis(sc);
                    AggregateDeclaration ad = p != null ? p.isAggregateDeclaration() : null;
                    if ((fdthis != null) && (ad != null) && (pequals(fdthis.isMemberLocal(), ad)) && (((td._scope.value.get()).stc.value & 1L) == 0L))
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

    public static Expression getRightThis(Loc loc, Ptr<Scope> sc, AggregateDeclaration ad, Expression e1, Dsymbol var, int flag) {
        while(true) try {
        /*L1:*/
            Type t = e1.type.value.toBasetype();
            if (((e1.op.value & 0xFF) == 235))
            {
                return e1;
            }
            else if ((ad != null) && (ad.isClassDeclaration() != null) && (ad.isClassDeclaration().classKind.value == ClassKind.objc) && (var.isFuncDeclaration() != null) && var.isFuncDeclaration().isStatic() && (var.isFuncDeclaration().selector.value != null))
            {
                return new ObjcClassReferenceExp(e1.loc.value, (ClassDeclaration)ad);
            }
            if (((e1.op.value & 0xFF) == 123))
            {
                FuncDeclaration f = hasThis(sc);
                if ((f != null) && f.isThis2.value)
                {
                    if (followInstantiationContextAggregateDeclaration(f, ad))
                    {
                        e1 = new VarExp(loc, f.vthis.value, true);
                        e1 = new PtrExp(loc, e1);
                        e1 = new IndexExp(loc, e1, literal_356A192B7913B04C());
                        e1 = getThisSkipNestedFuncs(loc, sc, f.toParent2(), ad, e1, t, var, false);
                        if (((e1.op.value & 0xFF) == 127))
                            return e1;
                        /*goto L1*/throw Dispatch0.INSTANCE;
                    }
                }
            }
            if ((ad != null) && !(((t.ty.value & 0xFF) == ENUMTY.Tpointer) && ((t.nextOf().ty.value & 0xFF) == ENUMTY.Tstruct) && (pequals(((TypeStruct)t.nextOf()).sym.value, ad))) && !(((t.ty.value & 0xFF) == ENUMTY.Tstruct) && (pequals(((TypeStruct)t).sym.value, ad))))
            {
                ClassDeclaration cd = ad.isClassDeclaration();
                ClassDeclaration tcd = t.isClassHandle();
                if ((cd == null) || (tcd == null) || !((pequals(tcd, cd)) || cd.isBaseOf(tcd, null)))
                {
                    if ((tcd != null) && tcd.isNested())
                    {
                        VarDeclaration vthis = followInstantiationContextAggregateDeclaration(tcd, ad) ? tcd.vthis2.value : tcd.vthis.value;
                        e1 = new DotVarExp(loc, e1, vthis, true);
                        e1.type.value = vthis.type.value;
                        e1.type.value = e1.type.value.addMod(t.mod.value);
                        e1 = getThisSkipNestedFuncs(loc, sc, toParentPAggregateDeclaration(tcd, ad), ad, e1, t, var, false);
                        if (((e1.op.value & 0xFF) == 127))
                            return e1;
                        /*goto L1*/throw Dispatch0.INSTANCE;
                    }
                    if (flag != 0)
                        return null;
                    e1.error(new BytePtr("`this` for `%s` needs to be type `%s` not type `%s`"), var.toChars(), ad.toChars(), t.toChars());
                    return new ErrorExp();
                }
            }
            return e1;
            break;
        } catch(Dispatch0 __d){}
    }

    // defaulted all parameters starting with #6
    public static Expression getRightThis(Loc loc, Ptr<Scope> sc, AggregateDeclaration ad, Expression e1, Dsymbol var) {
        return getRightThis(loc, sc, ad, e1, var, 0);
    }

    public static Expression resolvePropertiesX(Ptr<Scope> sc, Expression e1, Expression e2) {
        Loc loc = e1.loc.value.copy();
        OverloadSet os = null;
        Dsymbol s = null;
        Ptr<DArray<RootObject>> tiargs = null;
        Type tthis = null;
        try {
            try {
                if (((e1.op.value & 0xFF) == 97))
                {
                    DotExp de = (DotExp)e1;
                    if (((de.e2.value.op.value & 0xFF) == 214))
                    {
                        tiargs = null;
                        tthis = de.e1.value.type.value;
                        os = ((OverExp)de.e2.value).vars;
                        /*goto Los*//*unrolled goto*/
                    /*Los:*/
                        assert(os != null);
                        FuncDeclaration fd = null;
                        if (e2 != null)
                        {
                            e2 = expressionSemantic(e2, sc);
                            if (((e2.op.value & 0xFF) == 127))
                                return new ErrorExp();
                            e2 = resolveProperties(sc, e2);
                            Ref<DArray<Expression>> a = ref(new DArray<Expression>());
                            try {
                                a.value.push(e2);
                                {
                                    int i = 0;
                                    for (; (i < os.a.length.value);i++){
                                        {
                                            FuncDeclaration f = resolveFuncCall(loc, sc, os.a.get(i), tiargs, tthis, ptr(a), FuncResolveFlag.quiet);
                                            if ((f) != null)
                                            {
                                                if (f.errors.value)
                                                    return new ErrorExp();
                                                fd = f;
                                                assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
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
                                for (; (i < os.a.length.value);i++){
                                    {
                                        FuncDeclaration f = resolveFuncCall(loc, sc, os.a.get(i), tiargs, tthis, null, FuncResolveFlag.quiet);
                                        if ((f) != null)
                                        {
                                            if (f.errors.value)
                                                return new ErrorExp();
                                            fd = f;
                                            assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                                            TypeFunction tf = (TypeFunction)fd.type.value;
                                            if (!tf.isref.value && (e2 != null))
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
                else if (((e1.op.value & 0xFF) == 214))
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
                        if (((e2.op.value & 0xFF) == 127))
                            return new ErrorExp();
                        e2 = resolveProperties(sc, e2);
                        Ref<DArray<Expression>> a = ref(new DArray<Expression>());
                        try {
                            a.value.push(e2);
                            {
                                int i = 0;
                                for (; (i < os.a.length.value);i++){
                                    {
                                        FuncDeclaration f = resolveFuncCall(loc, sc, os.a.get(i), tiargs, tthis, ptr(a), FuncResolveFlag.quiet);
                                        if ((f) != null)
                                        {
                                            if (f.errors.value)
                                                return new ErrorExp();
                                            fd = f;
                                            assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
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
                            for (; (i < os.a.length.value);i++){
                                {
                                    FuncDeclaration f = resolveFuncCall(loc, sc, os.a.get(i), tiargs, tthis, null, FuncResolveFlag.quiet);
                                    if ((f) != null)
                                    {
                                        if (f.errors.value)
                                            return new ErrorExp();
                                        fd = f;
                                        assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                                        TypeFunction tf = (TypeFunction)fd.type.value;
                                        if (!tf.isref.value && (e2 != null))
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
                else if (((e1.op.value & 0xFF) == 29))
                {
                    DotTemplateInstanceExp dti = (DotTemplateInstanceExp)e1;
                    if (!dti.findTempDecl(sc))
                        /*goto Leprop*/throw Dispatch0.INSTANCE;
                    if (!dti.ti.semanticTiargs(sc))
                        /*goto Leprop*/throw Dispatch0.INSTANCE;
                    tiargs = dti.ti.tiargs.value;
                    tthis = dti.e1.value.type.value;
                    if (((os = dti.ti.tempdecl.value.isOverloadSet()) != null))
                        /*goto Los*/throw Dispatch0.INSTANCE;
                    if (((s = dti.ti.tempdecl.value) != null))
                        /*goto Lfd*//*unrolled goto*/
                    /*Lfd:*/
                        assert(s != null);
                        if (e2 != null)
                        {
                            e2 = expressionSemantic(e2, sc);
                            if (((e2.op.value & 0xFF) == 127))
                                return new ErrorExp();
                            e2 = resolveProperties(sc, e2);
                            Ref<DArray<Expression>> a = ref(new DArray<Expression>());
                            try {
                                a.value.push(e2);
                                FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, ptr(a), FuncResolveFlag.quiet);
                                if ((fd != null) && (fd.type.value != null))
                                {
                                    if (fd.errors.value)
                                        return new ErrorExp();
                                    assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                                    Expression e = new CallExp(loc, e1, e2);
                                    return expressionSemantic(e, sc);
                                }
                            }
                            finally {
                            }
                        }
                        {
                            FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, null, FuncResolveFlag.quiet);
                            if ((fd != null) && (fd.type.value != null))
                            {
                                if (fd.errors.value)
                                    return new ErrorExp();
                                assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                                TypeFunction tf = (TypeFunction)fd.type.value;
                                if ((e2 == null) || tf.isref.value)
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
                            if ((fd) != null)
                            {
                                assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                                Expression e = new CallExp(loc, e1, e2);
                                return expressionSemantic(e, sc);
                            }
                        }
                        if (e2 != null)
                            /*goto Leprop*/throw Dispatch0.INSTANCE;
                }
                else if (((e1.op.value & 0xFF) == 37))
                {
                    DotTemplateExp dte = (DotTemplateExp)e1;
                    s = dte.td.value;
                    tiargs = null;
                    tthis = dte.e1.value.type.value;
                    /*goto Lfd*//*unrolled goto*/
                /*Lfd:*/
                    assert(s != null);
                    if (e2 != null)
                    {
                        e2 = expressionSemantic(e2, sc);
                        if (((e2.op.value & 0xFF) == 127))
                            return new ErrorExp();
                        e2 = resolveProperties(sc, e2);
                        Ref<DArray<Expression>> a = ref(new DArray<Expression>());
                        try {
                            a.value.push(e2);
                            FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, ptr(a), FuncResolveFlag.quiet);
                            if ((fd != null) && (fd.type.value != null))
                            {
                                if (fd.errors.value)
                                    return new ErrorExp();
                                assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                                Expression e = new CallExp(loc, e1, e2);
                                return expressionSemantic(e, sc);
                            }
                        }
                        finally {
                        }
                    }
                    {
                        FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, null, FuncResolveFlag.quiet);
                        if ((fd != null) && (fd.type.value != null))
                        {
                            if (fd.errors.value)
                                return new ErrorExp();
                            assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                            TypeFunction tf = (TypeFunction)fd.type.value;
                            if ((e2 == null) || tf.isref.value)
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
                        if ((fd) != null)
                        {
                            assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                            Expression e = new CallExp(loc, e1, e2);
                            return expressionSemantic(e, sc);
                        }
                    }
                    if (e2 != null)
                        /*goto Leprop*/throw Dispatch0.INSTANCE;
                }
                else if (((e1.op.value & 0xFF) == 203))
                {
                    s = ((ScopeExp)e1).sds.value;
                    TemplateInstance ti = s.isTemplateInstance();
                    if ((ti != null) && (ti.semanticRun.value == 0) && (ti.tempdecl.value != null))
                    {
                        if (!ti.semanticTiargs(sc))
                            /*goto Leprop*/throw Dispatch0.INSTANCE;
                        tiargs = ti.tiargs.value;
                        tthis = null;
                        if (((os = ti.tempdecl.value.isOverloadSet()) != null))
                            /*goto Los*/throw Dispatch0.INSTANCE;
                        if (((s = ti.tempdecl.value) != null))
                            /*goto Lfd*//*unrolled goto*/
                        /*Lfd:*/
                            assert(s != null);
                            if (e2 != null)
                            {
                                e2 = expressionSemantic(e2, sc);
                                if (((e2.op.value & 0xFF) == 127))
                                    return new ErrorExp();
                                e2 = resolveProperties(sc, e2);
                                Ref<DArray<Expression>> a = ref(new DArray<Expression>());
                                try {
                                    a.value.push(e2);
                                    FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, ptr(a), FuncResolveFlag.quiet);
                                    if ((fd != null) && (fd.type.value != null))
                                    {
                                        if (fd.errors.value)
                                            return new ErrorExp();
                                        assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                                        Expression e = new CallExp(loc, e1, e2);
                                        return expressionSemantic(e, sc);
                                    }
                                }
                                finally {
                                }
                            }
                            {
                                FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, null, FuncResolveFlag.quiet);
                                if ((fd != null) && (fd.type.value != null))
                                {
                                    if (fd.errors.value)
                                        return new ErrorExp();
                                    assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                                    TypeFunction tf = (TypeFunction)fd.type.value;
                                    if ((e2 == null) || tf.isref.value)
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
                                if ((fd) != null)
                                {
                                    assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                                    Expression e = new CallExp(loc, e1, e2);
                                    return expressionSemantic(e, sc);
                                }
                            }
                            if (e2 != null)
                                /*goto Leprop*/throw Dispatch0.INSTANCE;
                    }
                }
                else if (((e1.op.value & 0xFF) == 36))
                {
                    s = ((TemplateExp)e1).td.value;
                    tiargs = null;
                    tthis = null;
                    /*goto Lfd*//*unrolled goto*/
                /*Lfd:*/
                    assert(s != null);
                    if (e2 != null)
                    {
                        e2 = expressionSemantic(e2, sc);
                        if (((e2.op.value & 0xFF) == 127))
                            return new ErrorExp();
                        e2 = resolveProperties(sc, e2);
                        Ref<DArray<Expression>> a = ref(new DArray<Expression>());
                        try {
                            a.value.push(e2);
                            FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, ptr(a), FuncResolveFlag.quiet);
                            if ((fd != null) && (fd.type.value != null))
                            {
                                if (fd.errors.value)
                                    return new ErrorExp();
                                assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                                Expression e = new CallExp(loc, e1, e2);
                                return expressionSemantic(e, sc);
                            }
                        }
                        finally {
                        }
                    }
                    {
                        FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, null, FuncResolveFlag.quiet);
                        if ((fd != null) && (fd.type.value != null))
                        {
                            if (fd.errors.value)
                                return new ErrorExp();
                            assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                            TypeFunction tf = (TypeFunction)fd.type.value;
                            if ((e2 == null) || tf.isref.value)
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
                        if ((fd) != null)
                        {
                            assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                            Expression e = new CallExp(loc, e1, e2);
                            return expressionSemantic(e, sc);
                        }
                    }
                    if (e2 != null)
                        /*goto Leprop*/throw Dispatch0.INSTANCE;
                }
                else if (((e1.op.value & 0xFF) == 27) && (e1.type.value != null) && ((e1.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tfunction))
                {
                    DotVarExp dve = (DotVarExp)e1;
                    s = dve.var.value.isFuncDeclaration();
                    tiargs = null;
                    tthis = dve.e1.value.type.value;
                    /*goto Lfd*//*unrolled goto*/
                /*Lfd:*/
                    assert(s != null);
                    if (e2 != null)
                    {
                        e2 = expressionSemantic(e2, sc);
                        if (((e2.op.value & 0xFF) == 127))
                            return new ErrorExp();
                        e2 = resolveProperties(sc, e2);
                        Ref<DArray<Expression>> a = ref(new DArray<Expression>());
                        try {
                            a.value.push(e2);
                            FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, ptr(a), FuncResolveFlag.quiet);
                            if ((fd != null) && (fd.type.value != null))
                            {
                                if (fd.errors.value)
                                    return new ErrorExp();
                                assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                                Expression e = new CallExp(loc, e1, e2);
                                return expressionSemantic(e, sc);
                            }
                        }
                        finally {
                        }
                    }
                    {
                        FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, null, FuncResolveFlag.quiet);
                        if ((fd != null) && (fd.type.value != null))
                        {
                            if (fd.errors.value)
                                return new ErrorExp();
                            assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                            TypeFunction tf = (TypeFunction)fd.type.value;
                            if ((e2 == null) || tf.isref.value)
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
                        if ((fd) != null)
                        {
                            assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                            Expression e = new CallExp(loc, e1, e2);
                            return expressionSemantic(e, sc);
                        }
                    }
                    if (e2 != null)
                        /*goto Leprop*/throw Dispatch0.INSTANCE;
                }
                else if (((e1.op.value & 0xFF) == 26) && (e1.type.value != null) && ((e1.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tfunction))
                {
                    s = ((VarExp)e1).var.value.isFuncDeclaration();
                    tiargs = null;
                    tthis = null;
                /*Lfd:*/
                    assert(s != null);
                    if (e2 != null)
                    {
                        e2 = expressionSemantic(e2, sc);
                        if (((e2.op.value & 0xFF) == 127))
                            return new ErrorExp();
                        e2 = resolveProperties(sc, e2);
                        Ref<DArray<Expression>> a = ref(new DArray<Expression>());
                        try {
                            a.value.push(e2);
                            FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, ptr(a), FuncResolveFlag.quiet);
                            if ((fd != null) && (fd.type.value != null))
                            {
                                if (fd.errors.value)
                                    return new ErrorExp();
                                assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                                Expression e = new CallExp(loc, e1, e2);
                                return expressionSemantic(e, sc);
                            }
                        }
                        finally {
                        }
                    }
                    {
                        FuncDeclaration fd = resolveFuncCall(loc, sc, s, tiargs, tthis, null, FuncResolveFlag.quiet);
                        if ((fd != null) && (fd.type.value != null))
                        {
                            if (fd.errors.value)
                                return new ErrorExp();
                            assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                            TypeFunction tf = (TypeFunction)fd.type.value;
                            if ((e2 == null) || tf.isref.value)
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
                        if ((fd) != null)
                        {
                            assert(((fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                            Expression e = new CallExp(loc, e1, e2);
                            return expressionSemantic(e, sc);
                        }
                    }
                    if (e2 != null)
                        /*goto Leprop*/throw Dispatch0.INSTANCE;
                }
                if (((e1.op.value & 0xFF) == 26))
                {
                    VarExp ve = (VarExp)e1;
                    VarDeclaration v = ve.var.value.isVarDeclaration();
                    if ((v != null) && ve.checkPurity(sc, v))
                        return new ErrorExp();
                }
                if (e2 != null)
                    return null;
                if ((e1.type.value != null) && ((e1.op.value & 0xFF) != 20))
                {
                    if (((e1.op.value & 0xFF) == 26))
                    {
                        VarExp ve = (VarExp)e1;
                        if ((ve.var.value.storage_class.value & 8192L) != 0)
                        {
                            Expression e = new CallExp(loc, e1);
                            return expressionSemantic(e, sc);
                        }
                    }
                    else if (((e1.op.value & 0xFF) == 27))
                    {
                        if (checkUnsafeAccess(sc, e1, true, true))
                            return new ErrorExp();
                    }
                    else if (((e1.op.value & 0xFF) == 97))
                    {
                        e1.error(new BytePtr("expression has no value"));
                        return new ErrorExp();
                    }
                    else if (((e1.op.value & 0xFF) == 18))
                    {
                        CallExp ce = (CallExp)e1;
                        if (checkUnsafeAccess(sc, ce.e1.value, true, true))
                            return new ErrorExp();
                    }
                }
                if (e1.type.value == null)
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

    // defaulted all parameters starting with #3
    public static Expression resolvePropertiesX(Ptr<Scope> sc, Expression e1) {
        return resolvePropertiesX(sc, e1, null);
    }

    public static Expression resolveProperties(Ptr<Scope> sc, Expression e) {
        e = resolvePropertiesX(sc, e, null);
        if (e.checkRightThis(sc))
            return new ErrorExp();
        return e;
    }

    public static boolean arrayExpressionToCommonType(Ptr<Scope> sc, Ptr<DArray<Expression>> exps, Ptr<Type> pt) {
        IntegerExp integerexp = literal_B6589FC6AB0DC82C();
        CondExp condexp = new CondExp(Loc.initial.value, integerexp, null, null);
        Type t0 = null;
        Expression e0 = null;
        int j0 = -1;
        boolean foundType = false;
        {
            int i = 0;
            for (; (i < (exps.get()).length.value);i++){
                Expression e = (exps.get()).get(i);
                if (e == null)
                    continue;
                e = resolveProperties(sc, e);
                if (e.type.value == null)
                {
                    e.error(new BytePtr("`%s` has no value"), e.toChars());
                    t0 = Type.terror.value;
                    continue;
                }
                if (((e.op.value & 0xFF) == 20))
                {
                    foundType = true;
                    e.checkValue();
                    t0 = Type.terror.value;
                    continue;
                }
                if (((e.type.value.ty.value & 0xFF) == ENUMTY.Tvoid))
                {
                    continue;
                }
                if (checkNonAssignmentArrayOp(e, false))
                {
                    t0 = Type.terror.value;
                    continue;
                }
                e = doCopyOrMove(sc, e, null);
                if (!foundType && (t0 != null) && !t0.equals(e.type.value))
                {
                    condexp.type.value = null;
                    condexp.e1.value = e0;
                    condexp.e2.value = e;
                    condexp.loc.value = e.loc.value.copy();
                    Expression ex = expressionSemantic(condexp, sc);
                    if (((ex.op.value & 0xFF) == 127))
                        e = ex;
                    else
                    {
                        exps.get().set(j0, condexp.e1.value);
                        e = condexp.e2.value;
                    }
                }
                j0 = i;
                e0 = e;
                t0 = e.type.value;
                if (((e.op.value & 0xFF) != 127))
                    exps.get().set(i, e);
            }
        }
        if (t0 == null)
            t0 = Type.tvoid.value;
        else if (((t0.ty.value & 0xFF) != ENUMTY.Terror))
        {
            {
                int i = 0;
                for (; (i < (exps.get()).length.value);i++){
                    Expression e = (exps.get()).get(i);
                    if (e == null)
                        continue;
                    e = e.implicitCastTo(sc, t0);
                    if (((e.op.value & 0xFF) == 127))
                    {
                        t0 = Type.terror.value;
                        break;
                    }
                    exps.get().set(i, e);
                }
            }
        }
        if (pt != null)
            pt.set(0, t0);
        return pequals(t0, Type.terror.value);
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
        assert(((exp.e1.value.op.value & 0xFF) == 32));
        ArrayLengthExp ale = (ArrayLengthExp)exp.e1.value;
        if (((ale.e1.value.op.value & 0xFF) == 26))
        {
            e = opAssignToOp(exp.loc.value, exp.op.value, ale, exp.e2.value);
            e = new AssignExp(exp.loc.value, ale.syntaxCopy(), e);
        }
        else
        {
            VarDeclaration tmp = copyToTemp(0L, new BytePtr("__arraylength"), new AddrExp(ale.loc.value, ale.e1.value));
            Expression e1 = new ArrayLengthExp(ale.loc.value, new PtrExp(ale.loc.value, new VarExp(ale.loc.value, tmp, true)));
            Expression elvalue = e1.syntaxCopy();
            e = opAssignToOp(exp.loc.value, exp.op.value, e1, exp.e2.value);
            e = new AssignExp(exp.loc.value, elvalue, e);
            e = new CommaExp(exp.loc.value, new DeclarationExp(ale.loc.value, tmp), e, true);
        }
        return e;
    }

    public static boolean preFunctionParameters(Ptr<Scope> sc, Ptr<DArray<Expression>> exps) {
        boolean err = false;
        if (exps != null)
        {
            expandTuples(exps);
            {
                int i = 0;
                for (; (i < (exps.get()).length.value);i++){
                    Expression arg = (exps.get()).get(i);
                    arg = resolveProperties(sc, arg);
                    if (((arg.op.value & 0xFF) == 20))
                    {
                        arg = resolveAliasThis(sc, arg, false);
                        if (((arg.op.value & 0xFF) == 20))
                        {
                            arg.error(new BytePtr("cannot pass type `%s` as a function argument"), arg.toChars());
                            arg = new ErrorExp();
                            err = true;
                        }
                    }
                    else if (((arg.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tfunction))
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
                    exps.get().set(i, arg);
                }
            }
        }
        return err;
    }

    public static boolean checkDefCtor(Loc loc, Type t) {
        t = t.baseElemOf();
        if (((t.ty.value & 0xFF) == ENUMTY.Tstruct))
        {
            StructDeclaration sd = ((TypeStruct)t).sym.value;
            if (sd.noDefaultCtor.value)
            {
                sd.error(loc, new BytePtr("default construction is disabled"));
                return true;
            }
        }
        return false;
    }

    public static boolean functionParameters(Loc loc, Ptr<Scope> sc, TypeFunction tf, Expression ethis, Type tthis, Ptr<DArray<Expression>> arguments, FuncDeclaration fd, Ptr<Type> prettype, Ptr<Expression> peprefix) {
        assert(arguments != null);
        assert((fd != null) || (tf.next.value != null));
        IntRef nargs = ref(arguments != null ? (arguments.get()).length.value : 0);
        int nparams = tf.parameterList.length();
        int olderrors = global.errors.value;
        boolean err = false;
        prettype.set(0, Type.terror.value);
        Expression eprefix = null;
        peprefix.set(0, null);
        if ((nargs.value > nparams) && (tf.parameterList.varargs.value == VarArg.none))
        {
            error(loc, new BytePtr("expected %llu arguments, not %llu for non-variadic function type `%s`"), (long)nparams, (long)nargs.value, tf.toChars());
            return true;
        }
        if ((tf.next.value == null) && fd.inferRetType)
        {
            fd.functionSemantic();
        }
        else if ((fd != null) && (fd.parent.value != null))
        {
            TemplateInstance ti = fd.parent.value.isTemplateInstance();
            if ((ti != null) && (ti.tempdecl.value != null))
            {
                fd.functionSemantic3();
            }
        }
        boolean isCtorCall = (fd != null) && fd.needThis() && (fd.isCtorDeclaration() != null);
        int n = (nargs.value > nparams) ? nargs.value : nparams;
        Ref<Byte> wildmatch = ref((tthis != null) && !isCtorCall ? (byte)(tthis.deduceWild(tf, false) & 0xFF) : (byte)0);
        boolean done = false;
        {
            int __key1364 = 0;
            int __limit1365 = n;
        L_outer4:
            for (; (__key1364 < __limit1365);__key1364 += 1) {
                int i = __key1364;
                Expression arg = (i < nargs.value) ? (arguments.get()).get(i) : null;
                if ((i < nparams))
                {
                    Function0<Boolean> errorArgs = new Function0<Boolean>(){
                        public Boolean invoke() {
                            error(loc, new BytePtr("expected %llu function arguments, not %llu"), (long)nparams, (long)nargs.value);
                            return true;
                        }
                    };
                    Parameter p = tf.parameterList.get(i);
                    if (arg == null)
                    {
                        if (p.defaultArg.value == null)
                        {
                            if ((tf.parameterList.varargs.value == VarArg.typesafe) && (i + 1 == nparams))
                                /*goto L2*//*unrolled goto*/
                            /*L2:*/
                                Type tb = p.type.value.toBasetype();
                                switch ((tb.ty.value & 0xFF))
                                {
                                    case 1:
                                    case 0:
                                        Type tbn = ((TypeArray)tb).next.value;
                                        Type tret = p.isLazyArray();
                                        Ptr<DArray<Expression>> elements = refPtr(new DArray<Expression>(nargs.value - i));
                                        {
                                            int __key1366 = 0;
                                            int __limit1367 = (elements.get()).length.value;
                                            for (; (__key1366 < __limit1367);__key1366 += 1) {
                                                int u = __key1366;
                                                Expression a = (arguments.get()).get(i + u);
                                                if ((tret != null) && (a.implicitConvTo(tret) != 0))
                                                {
                                                    a = toDelegate(a.implicitCastTo(sc, tret).optimize(0, false), tret, sc);
                                                }
                                                else
                                                    a = a.implicitCastTo(sc, tbn);
                                                a = a.addDtorHook(sc);
                                                elements.get().set(u, a);
                                            }
                                        }
                                        arg = new ArrayLiteralExp(loc, tbn.sarrayOf((long)(nargs.value - i)), elements);
                                        if (((tb.ty.value & 0xFF) == ENUMTY.Tarray))
                                        {
                                            arg = new SliceExp(loc, arg, null, null);
                                            arg.type.value = p.type.value;
                                        }
                                        break;
                                    case 7:
                                        Ptr<DArray<Expression>> args = refPtr(new DArray<Expression>(nargs.value - i));
                                        {
                                            int __key1368 = i;
                                            int __limit1369 = nargs.value;
                                            for (; (__key1368 < __limit1369);__key1368 += 1) {
                                                int u_1 = __key1368;
                                                args.get().set(u_1 - i, (arguments.get()).get(u_1));
                                            }
                                        }
                                        arg = new NewExp(loc, null, null, p.type.value, args);
                                        break;
                                    default:
                                    if (arg == null)
                                    {
                                        error(loc, new BytePtr("not enough arguments"));
                                        return true;
                                    }
                                    break;
                                }
                                arg = expressionSemantic(arg, sc);
                                (arguments.get()).setDim(i + 1);
                                arguments.get().set(i, arg);
                                nargs.value = i + 1;
                                done = true;
                            return errorArgs.invoke();
                        }
                        arg = p.defaultArg.value;
                        arg = inlineCopy(arg, sc);
                        arg = arg.resolveLoc(loc, sc);
                        (arguments.get()).push(arg);
                        nargs.value++;
                    }
                    else
                    {
                        if (((arg.op.value & 0xFF) == 190))
                        {
                            arg = arg.resolveLoc(loc, sc);
                            arguments.get().set(i, arg);
                        }
                    }
                    try {
                        if ((tf.parameterList.varargs.value == VarArg.typesafe) && (i + 1 == nparams))
                        {
                            try {
                                {
                                    int m = MATCH.nomatch;
                                    if (((m = arg.implicitConvTo(p.type.value)) > MATCH.nomatch))
                                    {
                                        if ((p.type.value.nextOf() != null) && (arg.implicitConvTo(p.type.value.nextOf()) >= m))
                                            /*goto L2*/throw Dispatch0.INSTANCE;
                                        else if ((nargs.value != nparams))
                                            return errorArgs.invoke();
                                        /*goto L1*/throw Dispatch0.INSTANCE;
                                    }
                                }
                            }
                            catch(Dispatch0 __d){}
                        /*L2:*/
                            Type tb = p.type.value.toBasetype();
                            switch ((tb.ty.value & 0xFF))
                            {
                                case 1:
                                case 0:
                                    Type tbn = ((TypeArray)tb).next.value;
                                    Type tret = p.isLazyArray();
                                    Ptr<DArray<Expression>> elements = refPtr(new DArray<Expression>(nargs.value - i));
                                    {
                                        int __key1366 = 0;
                                        int __limit1367 = (elements.get()).length.value;
                                        for (; (__key1366 < __limit1367);__key1366 += 1) {
                                            int u = __key1366;
                                            Expression a = (arguments.get()).get(i + u);
                                            if ((tret != null) && (a.implicitConvTo(tret) != 0))
                                            {
                                                a = toDelegate(a.implicitCastTo(sc, tret).optimize(0, false), tret, sc);
                                            }
                                            else
                                                a = a.implicitCastTo(sc, tbn);
                                            a = a.addDtorHook(sc);
                                            elements.get().set(u, a);
                                        }
                                    }
                                    arg = new ArrayLiteralExp(loc, tbn.sarrayOf((long)(nargs.value - i)), elements);
                                    if (((tb.ty.value & 0xFF) == ENUMTY.Tarray))
                                    {
                                        arg = new SliceExp(loc, arg, null, null);
                                        arg.type.value = p.type.value;
                                    }
                                    break;
                                case 7:
                                    Ptr<DArray<Expression>> args = refPtr(new DArray<Expression>(nargs.value - i));
                                    {
                                        int __key1368 = i;
                                        int __limit1369 = nargs.value;
                                        for (; (__key1368 < __limit1369);__key1368 += 1) {
                                            int u_1 = __key1368;
                                            args.get().set(u_1 - i, (arguments.get()).get(u_1));
                                        }
                                    }
                                    arg = new NewExp(loc, null, null, p.type.value, args);
                                    break;
                                default:
                                if (arg == null)
                                {
                                    error(loc, new BytePtr("not enough arguments"));
                                    return true;
                                }
                                break;
                            }
                            arg = expressionSemantic(arg, sc);
                            (arguments.get()).setDim(i + 1);
                            arguments.get().set(i, arg);
                            nargs.value = i + 1;
                            done = true;
                        }
                    }
                    catch(Dispatch0 __d){}
                /*L1:*/
                    if (!(((p.storageClass.value & 8192L) != 0) && ((p.type.value.ty.value & 0xFF) == ENUMTY.Tvoid)))
                    {
                        boolean isRef = (p.storageClass.value & 2101248L) != 0L;
                        {
                            byte wm = arg.type.value.deduceWild(p.type.value, isRef);
                            if ((wm) != 0)
                            {
                                wildmatch.value = wildmatch.value != 0 ? MODmerge(wildmatch.value, wm) : wm;
                            }
                        }
                    }
                }
                if (done)
                    break;
            }
        }
        if (((wildmatch.value & 0xFF) == MODFlags.mutable) || ((wildmatch.value & 0xFF) == MODFlags.immutable_) && (tf.next.value != null) && (tf.next.value.hasWild() != 0) && tf.isref.value || (tf.next.value.implicitConvTo(tf.next.value.immutableOf()) == 0))
        {
            Function1<Byte,Boolean> errorInout = new Function1<Byte,Boolean>(){
                public Boolean invoke(Byte wildmatch) {
                    Ref<Byte> wildmatch_ref = ref(wildmatch);
                    Ref<BytePtr> s = ref(pcopy(((wildmatch_ref.value & 0xFF) == MODFlags.mutable) ? new BytePtr("mutable") : MODtoChars(wildmatch_ref.value)));
                    error(loc, new BytePtr("modify `inout` to `%s` is not allowed inside `inout` function"), s.value);
                    return true;
                }
            };
            if (fd != null)
            {
                Function1<Dsymbol,Boolean> checkEnclosingWild = new Function1<Dsymbol,Boolean>(){
                    public Boolean invoke(Dsymbol s) {
                        Function1<Dsymbol,Boolean> checkWild = new Function1<Dsymbol,Boolean>(){
                            public Boolean invoke(Dsymbol s) {
                                Ref<Dsymbol> s_ref = ref(s);
                                if (s_ref.value == null)
                                    return false;
                                {
                                    Ref<AggregateDeclaration> ad = ref(s_ref.value.isAggregateDeclaration());
                                    if ((ad.value) != null)
                                    {
                                        if (ad.value.isNested())
                                            return checkEnclosingWild.invoke(s_ref.value);
                                    }
                                    else {
                                        Ref<FuncDeclaration> ff = ref(s_ref.value.isFuncDeclaration());
                                        if ((ff.value) != null)
                                        {
                                            if (((TypeFunction)ff.value.type.value).iswild.value != 0)
                                                return errorInout.invoke(wildmatch.value);
                                            if (ff.value.isNested() || (ff.value.isThis() != null))
                                                return checkEnclosingWild.invoke(s_ref.value);
                                        }
                                    }
                                }
                                return false;
                            }
                        };
                        Ref<Dsymbol> ctx0 = ref(s.toParent2());
                        Ref<Dsymbol> ctx1 = ref(s.toParentLocal());
                        if (checkWild.invoke(ctx0.value))
                            return true;
                        if ((!pequals(ctx0.value, ctx1.value)))
                            return checkWild.invoke(ctx1.value);
                        return false;
                    }
                };
                if ((fd.isThis() != null) || fd.isNested() && checkEnclosingWild.invoke(fd))
                    return true;
            }
            else if (tf.isWild())
                return errorInout.invoke(wildmatch.value);
        }
        Expression firstArg = (tf.next.value != null) && ((tf.next.value.ty.value & 0xFF) == ENUMTY.Tvoid) || isCtorCall && (tthis != null) && tthis.isMutable() && ((tthis.toBasetype().ty.value & 0xFF) == ENUMTY.Tstruct) && tthis.hasPointers() ? ethis : null;
        assert((nargs.value >= nparams));
        {
            Slice<Expression> __r1371 = (arguments.get()).opSlice(0, nargs.value).copy();
            int __key1370 = 0;
            for (; (__key1370 < __r1371.getLength());__key1370 += 1) {
                Expression arg = __r1371.get(__key1370);
                int i = __key1370;
                assert(arg != null);
                if ((i < nparams))
                {
                    Parameter p = tf.parameterList.get(i);
                    Type targ = arg.type.value;
                    if (!(((p.storageClass.value & 8192L) != 0) && ((p.type.value.ty.value & 0xFF) == ENUMTY.Tvoid)))
                    {
                        Type tprm = p.type.value.hasWild() != 0 ? p.type.value.substWildTo((wildmatch.value & 0xFF)) : p.type.value;
                        boolean hasCopyCtor = ((arg.type.value.ty.value & 0xFF) == ENUMTY.Tstruct) && ((TypeStruct)arg.type.value).sym.value.hasCopyCtor;
                        if (!(hasCopyCtor || tprm.equals(arg.type.value)))
                        {
                            arg = arg.implicitCastTo(sc, tprm);
                            arg = arg.optimize(0, (p.storageClass.value & 2101248L) != 0L);
                        }
                    }
                    if ((p.storageClass.value & 2097152L) != 0)
                    {
                        if (global.params.rvalueRefParam && !arg.isLvalue() && isCopyable(targ))
                        {
                            VarDeclaration v = copyToTemp(0L, new BytePtr("__rvalue"), arg);
                            Expression ev = new DeclarationExp(arg.loc.value, v);
                            ev = new CommaExp(arg.loc.value, ev, new VarExp(arg.loc.value, v, true), true);
                            arg = expressionSemantic(ev, sc);
                        }
                        arg = arg.toLvalue(sc, arg);
                        (err ? 1 : 0) |= (checkUnsafeAccess(sc, arg, false, true) ? 1 : 0);
                    }
                    else if ((p.storageClass.value & 4096L) != 0)
                    {
                        Type t = arg.type.value;
                        if (!t.isMutable() || !t.isAssignable())
                        {
                            arg.error(new BytePtr("cannot modify struct `%s` with immutable members"), arg.toChars());
                            err = true;
                        }
                        else
                        {
                            (err ? 1 : 0) |= (checkUnsafeAccess(sc, arg, false, true) ? 1 : 0);
                            (err ? 1 : 0) |= (checkDefCtor(arg.loc.value, t) ? 1 : 0);
                        }
                        arg = arg.toLvalue(sc, arg);
                    }
                    else if ((p.storageClass.value & 8192L) != 0)
                    {
                        Type t = ((p.type.value.ty.value & 0xFF) == ENUMTY.Tvoid) ? p.type.value : arg.type.value;
                        arg = toDelegate(arg, t, sc);
                    }
                    if ((firstArg != null) && ((p.storageClass.value & 17592186044416L) != 0))
                    {
                        if (global.params.vsafe.value)
                            (err ? 1 : 0) |= (checkParamArgumentReturn(sc, firstArg, arg, false) ? 1 : 0);
                    }
                    else if (tf.parameterEscapes(tthis, p))
                    {
                        if (global.params.vsafe.value)
                            (err ? 1 : 0) |= (checkParamArgumentEscape(sc, fd, p, arg, false) ? 1 : 0);
                    }
                    else
                    {
                        Expression a = arg;
                        if (((a.op.value & 0xFF) == 12))
                            a = ((CastExp)a).e1.value;
                        if (((a.op.value & 0xFF) == 161))
                        {
                            FuncExp fe = (FuncExp)a;
                            fe.fd.value.tookAddressOf.value = 0;
                        }
                        else if (((a.op.value & 0xFF) == 160))
                        {
                            DelegateExp de = (DelegateExp)a;
                            if (((de.e1.value.op.value & 0xFF) == 26))
                            {
                                VarExp ve = (VarExp)de.e1.value;
                                FuncDeclaration f = ve.var.value.isFuncDeclaration();
                                if (f != null)
                                {
                                    f.tookAddressOf.value--;
                                }
                            }
                        }
                    }
                    arg = arg.optimize(0, (p.storageClass.value & 2101248L) != 0L);
                    if ((i == 0) && (tthis == null) && ((p.storageClass.value & 2101248L) != 0) && (p.type.value != null) && (tf.next.value != null) && ((tf.next.value.ty.value & 0xFF) == ENUMTY.Tvoid) || isCtorCall)
                    {
                        Type tb = p.type.value.baseElemOf();
                        if (tb.isMutable() && tb.hasPointers())
                        {
                            firstArg = arg;
                        }
                    }
                }
                else
                {
                    if ((tf.linkage.value != LINK.d))
                    {
                        arg = integralPromotions(arg, sc);
                        switch ((arg.type.value.ty.value & 0xFF))
                        {
                            case 21:
                                arg = arg.castTo(sc, Type.tfloat64.value);
                                break;
                            case 24:
                                arg = arg.castTo(sc, Type.timaginary64.value);
                                break;
                            default:
                            break;
                        }
                        if ((tf.parameterList.varargs.value == VarArg.variadic))
                        {
                            BytePtr p = pcopy((tf.linkage.value == LINK.c) ? new BytePtr("extern(C)") : new BytePtr("extern(C++)"));
                            if (((arg.type.value.ty.value & 0xFF) == ENUMTY.Tarray))
                            {
                                arg.error(new BytePtr("cannot pass dynamic arrays to `%s` vararg functions"), p);
                                err = true;
                            }
                            if (((arg.type.value.ty.value & 0xFF) == ENUMTY.Tsarray))
                            {
                                arg.error(new BytePtr("cannot pass static arrays to `%s` vararg functions"), p);
                                err = true;
                            }
                        }
                    }
                    if (arg.type.value.needsDestruction())
                    {
                        arg.error(new BytePtr("cannot pass types that need destruction as variadic arguments"));
                        err = true;
                    }
                    Type tb = arg.type.value.toBasetype();
                    if (((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
                    {
                        TypeSArray ts = (TypeSArray)tb;
                        Type ta = ts.next.value.arrayOf();
                        if ((ts.size(arg.loc.value) == 0L))
                            arg = new NullExp(arg.loc.value, ta);
                        else
                            arg = arg.castTo(sc, ta);
                    }
                    if (((tb.ty.value & 0xFF) == ENUMTY.Tstruct))
                    {
                    }
                    if (((arg.op.value & 0xFF) == 25))
                    {
                        SymOffExp se = (SymOffExp)arg;
                        if (se.hasOverloads.value && !se.var.value.isFuncDeclaration().isUnique())
                        {
                            arg.error(new BytePtr("function `%s` is overloaded"), arg.toChars());
                            err = true;
                        }
                    }
                    if (arg.checkValue())
                        err = true;
                    arg = arg.optimize(0, false);
                }
                arguments.get().set(i, arg);
            }
        }
        {
            boolean leftToRight = true;
            if (false)
                assert((nargs.value == nparams));
            int start = 0;
            int end = nargs.value;
            int step = 1;
            int lastthrow = -1;
            int firstdtor = -1;
            {
                int i = 0;
                for (; (i != end);i += 1){
                    Expression arg = (arguments.get()).get(i);
                    if (canThrow(arg, (sc.get()).func.value, false))
                        lastthrow = i;
                    if ((firstdtor == -1) && arg.type.value.needsDestruction())
                    {
                        Parameter p = (i >= nparams) ? null : tf.parameterList.get(i);
                        if (!((p != null) && ((p.storageClass.value & 2109440L) != 0)))
                            firstdtor = i;
                    }
                }
            }
            boolean needsPrefix = (firstdtor >= 0) && (lastthrow >= 0) && ((lastthrow - firstdtor) > 0);
            VarDeclaration gate = null;
            if (needsPrefix)
            {
                Identifier idtmp = Identifier.generateId(new BytePtr("__gate"));
                gate = new VarDeclaration(loc, Type.tbool.value, idtmp, null, 0L);
                gate.storage_class.value |= 9964324126720L;
                dsymbolSemantic(gate, sc);
                DeclarationExp ae = new DeclarationExp(loc, gate);
                eprefix = expressionSemantic(ae, sc);
            }
            {
                int i = 0;
                for (; (i != end);i += 1){
                    Expression arg = (arguments.get()).get(i);
                    Parameter parameter = (i >= nparams) ? null : tf.parameterList.get(i);
                    boolean isRef = (parameter != null) && ((parameter.storageClass.value & 2101248L) != 0);
                    boolean isLazy = (parameter != null) && ((parameter.storageClass.value & 8192L) != 0);
                    if (isLazy)
                        continue;
                    if (gate != null)
                    {
                        boolean needsDtor = !isRef && arg.type.value.needsDestruction() && (i != lastthrow);
                        VarDeclaration tmp = copyToTemp(0L, needsDtor ? new BytePtr("__pfx") : new BytePtr("__pfy"), !isRef ? arg : arg.addressOf());
                        dsymbolSemantic(tmp, sc);
                        if (!needsDtor)
                        {
                            if (tmp.edtor.value != null)
                            {
                                assert((i == lastthrow));
                                tmp.edtor.value = null;
                            }
                        }
                        else
                        {
                            assert(tmp.edtor.value != null);
                            Expression e = tmp.edtor.value;
                            e = new LogicalExp(e.loc.value, TOK.orOr, new VarExp(e.loc.value, gate, true), e);
                            tmp.edtor.value = expressionSemantic(e, sc);
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
                        if ((i == lastthrow))
                        {
                            AssignExp e = new AssignExp(gate.loc.value, new VarExp(gate.loc.value, gate, true), new IntegerExp(gate.loc.value, 1L, Type.tbool.value));
                            eprefix = Expression.combine(eprefix, expressionSemantic(e, sc));
                            gate = null;
                        }
                    }
                    else
                    {
                        Type tv = arg.type.value.baseElemOf();
                        if (!isRef && ((tv.ty.value & 0xFF) == ENUMTY.Tstruct))
                            arg = doCopyOrMove(sc, arg, parameter != null ? parameter.type.value : null);
                    }
                    arguments.get().set(i, arg);
                }
            }
        }
        if ((tf.linkage.value == LINK.d) && (tf.parameterList.varargs.value == VarArg.variadic))
        {
            assert(((arguments.get()).length.value >= nparams));
            Ptr<DArray<Parameter>> args = refPtr(new DArray<Parameter>((arguments.get()).length.value - nparams));
            {
                int i = 0;
                for (; (i < (arguments.get()).length.value - nparams);i++){
                    Parameter arg = new Parameter(2048L, (arguments.get()).get(nparams + i).type.value, null, null, null);
                    args.get().set(i, arg);
                }
            }
            TypeTuple tup = new TypeTuple(args);
            Expression e = expressionSemantic(new TypeidExp(loc, tup), sc);
            (arguments.get()).insert(0, e);
        }
        Type tret = tf.next.value;
        if (isCtorCall)
        {
            if (tthis == null)
            {
                assert(((sc.get()).intypeof.value != 0) || (global.errors.value != 0));
                tthis = fd.isThis().type.value.addMod(fd.type.value.mod.value);
            }
            if (tf.isWild() && !fd.isReturnIsolated())
            {
                if (wildmatch.value != 0)
                    tret = tret.substWildTo((wildmatch.value & 0xFF));
                IntRef offset = ref(0);
                if ((tret.implicitConvTo(tthis) == 0) && !(MODimplicitConv(tret.mod.value, tthis.mod.value) && tret.isBaseOf(tthis, ptr(offset)) && (offset.value == 0)))
                {
                    BytePtr s1 = pcopy(tret.isNaked() ? new BytePtr(" mutable") : tret.modToChars());
                    BytePtr s2 = pcopy(tthis.isNaked() ? new BytePtr(" mutable") : tthis.modToChars());
                    error(loc, new BytePtr("`inout` constructor `%s` creates%s object, not%s"), fd.toPrettyChars(false), s1, s2);
                    err = true;
                }
            }
            tret = tthis;
        }
        else if ((wildmatch.value != 0) && (tret != null))
        {
            tret = tret.substWildTo((wildmatch.value & 0xFF));
        }
        prettype.set(0, tret);
        peprefix.set(0, eprefix);
        return err || (olderrors != global.errors.value);
    }

    public static dmodule.Package resolveIsPackage(Dsymbol sym) {
        dmodule.Package pkg = null;
        {
            Import imp = sym.isImport();
            if ((imp) != null)
            {
                if ((imp.pkg.value == null))
                {
                    error(sym.loc.value, new BytePtr("Internal Compiler Error: unable to process forward-referenced import `%s`"), imp.toChars());
                    throw new AssertionError("Unreachable code!");
                }
                pkg = imp.pkg.value;
            }
            else
                pkg = sym.isPackage();
        }
        if (pkg != null)
            pkg.resolvePKGunknown();
        return pkg;
    }

    public static dmodule.Module loadStdMath() {
        if (expressionsem.loadStdMathimpStdMath == null)
        {
            Ptr<DArray<Identifier>> a = refPtr(new DArray<Identifier>());
            (a.get()).push(Id.std);
            Import s = new Import(Loc.initial.value, a, Id.math, null, 0);
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
        public Ref<Ptr<Scope>> sc = ref(null);
        public Ref<Expression> result = ref(null);
        public  ExpressionSemanticVisitor(Ptr<Scope> sc) {
            this.sc.value = sc;
        }

        public  void setError() {
            this.result.value = new ErrorExp();
        }

        public  void visit(Expression e) {
            if (e.type.value != null)
                e.type.value = typeSemantic(e.type.value, e.loc.value, this.sc.value);
            else
                e.type.value = Type.tvoid.value;
            this.result.value = e;
        }

        public  void visit(IntegerExp e) {
            assert(e.type.value != null);
            if (((e.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                this.setError();
                return ;
            assert(e.type.value.deco.value != null);
            e.setInteger(e.getInteger());
            this.result.value = e;
        }

        public  void visit(RealExp e) {
            if (e.type.value == null)
                e.type.value = Type.tfloat64.value;
            else
                e.type.value = typeSemantic(e.type.value, e.loc.value, this.sc.value);
            this.result.value = e;
        }

        public  void visit(ComplexExp e) {
            if (e.type.value == null)
                e.type.value = Type.tcomplex80;
            else
                e.type.value = typeSemantic(e.type.value, e.loc.value, this.sc.value);
            this.result.value = e;
        }

        public  void visit(IdentifierExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            Ref<Dsymbol> scopesym = ref(null);
            Dsymbol s = (this.sc.value.get()).search(exp.loc.value, exp.ident.value, ptr(scopesym), 0);
            if (s != null)
            {
                if (s.errors.value)
                    this.setError();
                    return ;
                Expression e = null;
                WithScopeSymbol withsym = scopesym.value.isWithScopeSymbol();
                if ((withsym != null) && (withsym.withstate.wthis != null))
                {
                    Ptr<Scope> scwith = this.sc.value;
                    for (; (!pequals((scwith.get()).scopesym.value, scopesym.value));){
                        scwith = (scwith.get()).enclosing.value;
                        assert(scwith != null);
                    }
                    {
                        Ptr<Scope> scx = scwith;
                        for (; (scx != null) && (pequals((scx.get()).func.value, (scwith.get()).func.value));scx = (scx.get()).enclosing.value){
                            Dsymbol s2 = null;
                            if (((scx.get()).scopesym.value != null) && ((scx.get()).scopesym.value.symtab != null) && ((s2 = (scx.get()).scopesym.value.symtab.lookup(s.ident.value)) != null) && (!pequals(s, s2)))
                            {
                                exp.error(new BytePtr("with symbol `%s` is shadowing local symbol `%s`"), s.toPrettyChars(false), s2.toPrettyChars(false));
                                this.setError();
                                return ;
                            }
                        }
                    }
                    s = s.toAlias();
                    e = new VarExp(exp.loc.value, withsym.withstate.wthis, true);
                    e = new DotIdExp(exp.loc.value, e, exp.ident.value);
                    e = expressionSemantic(e, this.sc.value);
                }
                else
                {
                    if (withsym != null)
                    {
                        {
                            TypeExp t = withsym.withstate.exp.value.isTypeExp();
                            if ((t) != null)
                            {
                                e = new TypeExp(exp.loc.value, t.type.value);
                                e = new DotIdExp(exp.loc.value, e, exp.ident.value);
                                this.result.value = expressionSemantic(e, this.sc.value);
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
                            if (td.overroot.value != null)
                                td = td.overroot.value;
                            e = new TemplateExp(exp.loc.value, td, f);
                            e = expressionSemantic(e, this.sc.value);
                            this.result.value = e;
                            return ;
                        }
                    }
                    if (global.params.fixAliasThis.value)
                    {
                        ExpressionDsymbol expDsym = scopesym.value.isExpressionDsymbol();
                        if (expDsym != null)
                        {
                            this.result.value = expressionSemantic(expDsym.exp, this.sc.value);
                            return ;
                        }
                    }
                    e = symbolToExp(s, exp.loc.value, this.sc.value, true);
                }
                this.result.value = e;
                return ;
            }
            if (!global.params.fixAliasThis.value && (hasThis(this.sc.value) != null))
            {
                {
                    AggregateDeclaration ad = (this.sc.value.get()).getStructClassScope();
                    for (; ad != null;){
                        if (ad.aliasthis.value != null)
                        {
                            Expression e = null;
                            e = new ThisExp(exp.loc.value);
                            e = new DotIdExp(exp.loc.value, e, ad.aliasthis.value.ident.value);
                            e = new DotIdExp(exp.loc.value, e, exp.ident.value);
                            e = trySemantic(e, this.sc.value);
                            if (e != null)
                            {
                                this.result.value = e;
                                return ;
                            }
                        }
                        ClassDeclaration cd = ad.isClassDeclaration();
                        if ((cd != null) && (cd.baseClass.value != null) && (!pequals(cd.baseClass.value, ClassDeclaration.object.value)))
                        {
                            ad = cd.baseClass.value;
                            continue;
                        }
                        break;
                    }
                }
            }
            if ((pequals(exp.ident.value, Id.ctfe.value)))
            {
                if (((this.sc.value.get()).flags.value & 128) != 0)
                {
                    exp.error(new BytePtr("variable `__ctfe` cannot be read at compile time"));
                    this.setError();
                    return ;
                }
                VarDeclaration vd = new VarDeclaration(exp.loc.value, Type.tbool.value, Id.ctfe.value, null, 0L);
                vd.storage_class.value |= 1099511627776L;
                vd.semanticRun.value = PASS.semanticdone;
                Expression e = new VarExp(exp.loc.value, vd, true);
                e = expressionSemantic(e, this.sc.value);
                this.result.value = e;
                return ;
            }
            {
                Ptr<Scope> sc2 = this.sc.value;
                for (; sc2 != null;sc2 = (sc2.get()).enclosing.value){
                    if ((sc2.get()).scopesym.value == null)
                        continue;
                    {
                        WithScopeSymbol ss = (sc2.get()).scopesym.value.isWithScopeSymbol();
                        if ((ss) != null)
                        {
                            if (ss.withstate.wthis != null)
                            {
                                Expression e = null;
                                e = new VarExp(exp.loc.value, ss.withstate.wthis, true);
                                e = new DotIdExp(exp.loc.value, e, exp.ident.value);
                                e = trySemantic(e, this.sc.value);
                                if (e != null)
                                {
                                    this.result.value = e;
                                    return ;
                                }
                            }
                            else if ((ss.withstate.exp.value != null) && ((ss.withstate.exp.value.op.value & 0xFF) == 20))
                            {
                                {
                                    Type t = ss.withstate.exp.value.isTypeExp().type.value;
                                    if ((t) != null)
                                    {
                                        Expression e = null;
                                        e = new TypeExp(exp.loc.value, t);
                                        e = new DotIdExp(exp.loc.value, e, exp.ident.value);
                                        e = trySemantic(e, this.sc.value);
                                        if (e != null)
                                        {
                                            this.result.value = e;
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
                ByteSlice n = importHint(exp.ident.value.asString()).copy();
                if ((n).getLength() != 0)
                    exp.error(new BytePtr("`%s` is not defined, perhaps `import %.*s;` is needed?"), exp.ident.value.toChars(), n.getLength(), toBytePtr(n));
                else {
                    Dsymbol s2 = (this.sc.value.get()).search_correct(exp.ident.value);
                    if ((s2) != null)
                        exp.error(new BytePtr("undefined identifier `%s`, did you mean %s `%s`?"), exp.ident.value.toChars(), s2.kind(), s2.toChars());
                    else {
                        BytePtr p = pcopy(Scope.search_correct_C(exp.ident.value));
                        if ((p) != null)
                            exp.error(new BytePtr("undefined identifier `%s`, did you mean `%s`?"), exp.ident.value.toChars(), p);
                        else
                            exp.error(new BytePtr("undefined identifier `%s`"), exp.ident.value.toChars());
                    }
                }
            }
            this.result.value = new ErrorExp();
        }

        public  void visit(DsymbolExp e) {
            this.result.value = symbolToExp(e.s.value, e.loc.value, this.sc.value, e.hasOverloads);
        }

        public  void visit(ThisExp e) {
            if (e.type.value != null)
            {
                this.result.value = e;
                return ;
            }
            FuncDeclaration fd = hasThis(this.sc.value);
            AggregateDeclaration ad = null;
            try {
                if ((fd == null) && ((this.sc.value.get()).intypeof.value == 1))
                {
                    {
                        Dsymbol s = (this.sc.value.get()).getStructClassScope();
                    L_outer5:
                        for (; 1 != 0;s = s.parent.value){
                            if (s == null)
                            {
                                e.error(new BytePtr("`%s` is not in a class or struct scope"), e.toChars());
                                /*goto Lerr*/throw Dispatch0.INSTANCE;
                            }
                            ClassDeclaration cd = s.isClassDeclaration();
                            if (cd != null)
                            {
                                e.type.value = cd.type.value;
                                this.result.value = e;
                                return ;
                            }
                            StructDeclaration sd = s.isStructDeclaration();
                            if (sd != null)
                            {
                                e.type.value = sd.type.value;
                                this.result.value = e;
                                return ;
                            }
                        }
                    }
                }
                if (fd == null)
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                assert(fd.vthis.value != null);
                e.var.value = fd.vthis.value;
                assert(e.var.value.parent.value != null);
                ad = fd.isMemberLocal();
                if (ad == null)
                    ad = fd.isMember2();
                assert(ad != null);
                e.type.value = ad.type.value.addMod(e.var.value.type.value.mod.value);
                if (e.var.value.checkNestedReference(this.sc.value, e.loc.value))
                    this.setError();
                    return ;
                this.result.value = e;
                return ;
            }
            catch(Dispatch0 __d){}
        /*Lerr:*/
            e.error(new BytePtr("`this` is only defined in non-static member functions, not `%s`"), (this.sc.value.get()).parent.value.toChars());
            this.result.value = new ErrorExp();
        }

        public  void visit(SuperExp e) {
            if (e.type.value != null)
            {
                this.result.value = e;
                return ;
            }
            FuncDeclaration fd = hasThis(this.sc.value);
            ClassDeclaration cd = null;
            Dsymbol s = null;
            try {
                if ((fd == null) && ((this.sc.value.get()).intypeof.value == 1))
                {
                    {
                        s = (this.sc.value.get()).getStructClassScope();
                    L_outer6:
                        for (; 1 != 0;s = s.parent.value){
                            if (s == null)
                            {
                                e.error(new BytePtr("`%s` is not in a class scope"), e.toChars());
                                /*goto Lerr*/throw Dispatch0.INSTANCE;
                            }
                            cd = s.isClassDeclaration();
                            if (cd != null)
                            {
                                cd = cd.baseClass.value;
                                if (cd == null)
                                {
                                    e.error(new BytePtr("class `%s` has no `super`"), s.toChars());
                                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                                }
                                e.type.value = cd.type.value;
                                this.result.value = e;
                                return ;
                            }
                        }
                    }
                }
                if (fd == null)
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                e.var.value = fd.vthis.value;
                assert((e.var.value != null) && (e.var.value.parent.value != null));
                s = fd.toParentDecl();
                if (s.isTemplateDeclaration() != null)
                    s = s.toParent();
                assert(s != null);
                cd = s.isClassDeclaration();
                if (cd == null)
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                if (cd.baseClass.value == null)
                {
                    e.error(new BytePtr("no base class for `%s`"), cd.toChars());
                    e.type.value = cd.type.value.addMod(e.var.value.type.value.mod.value);
                }
                else
                {
                    e.type.value = cd.baseClass.value.type.value;
                    e.type.value = e.type.value.castMod(e.var.value.type.value.mod.value);
                }
                if (e.var.value.checkNestedReference(this.sc.value, e.loc.value))
                    this.setError();
                    return ;
                this.result.value = e;
                return ;
            }
            catch(Dispatch0 __d){}
        /*Lerr:*/
            e.error(new BytePtr("`super` is only allowed in non-static class member functions"));
            this.result.value = new ErrorExp();
        }

        public  void visit(NullExp e) {
            if (e.type.value != null)
            {
                this.result.value = e;
                return ;
            }
            e.type.value = Type.tnull;
            this.result.value = e;
        }

        public  void visit(StringExp e) {
            if (e.type.value != null)
            {
                this.result.value = e;
                return ;
            }
            OutBuffer buffer = new OutBuffer();
            try {
                int newlen = 0;
                BytePtr p = null;
                IntRef u = ref(0);
                int c = 0x0ffff;
                {
                    int __dispatch4 = 0;
                    dispatched_4:
                    do {
                        switch (__dispatch4 != 0 ? __dispatch4 : (e.postfix.value & 0xFF))
                        {
                            case 100:
                                {
                                    u.value = 0;
                                    for (; (u.value < e.len.value);){
                                        p = pcopy(utf_decodeChar(e.string.value, e.len.value, u, c));
                                        if (p != null)
                                        {
                                            e.error(new BytePtr("%s"), p);
                                            this.setError();
                                            return ;
                                        }
                                        else
                                        {
                                            buffer.write4(c);
                                            newlen++;
                                        }
                                    }
                                }
                                buffer.write4(0);
                                e.dstring.value = pcopy((toIntPtr(buffer.extractData())));
                                e.len.value = newlen;
                                e.sz.value = (byte)4;
                                e.type.value = new TypeDArray(Type.tdchar.immutableOf());
                                e.committed.value = (byte)1;
                                break;
                            case 119:
                                {
                                    u.value = 0;
                                    for (; (u.value < e.len.value);){
                                        p = pcopy(utf_decodeChar(e.string.value, e.len.value, u, c));
                                        if (p != null)
                                        {
                                            e.error(new BytePtr("%s"), p);
                                            this.setError();
                                            return ;
                                        }
                                        else
                                        {
                                            buffer.writeUTF16(c);
                                            newlen++;
                                            if ((c >= 65536))
                                                newlen++;
                                        }
                                    }
                                }
                                buffer.writeUTF16(0);
                                e.wstring.value = pcopy((toCharPtr(buffer.extractData())));
                                e.len.value = newlen;
                                e.sz.value = (byte)2;
                                e.type.value = new TypeDArray(Type.twchar.immutableOf());
                                e.committed.value = (byte)1;
                                break;
                            case 99:
                                e.committed.value = (byte)1;
                                /*goto default*/ { __dispatch4 = -1; continue dispatched_4; }
                            default:
                            __dispatch4 = 0;
                            e.type.value = new TypeDArray(Type.tchar.immutableOf());
                            break;
                        }
                    } while(__dispatch4 != 0);
                }
                e.type.value = typeSemantic(e.type.value, e.loc.value, this.sc.value);
                this.result.value = e;
            }
            finally {
            }
        }

        public  void visit(TupleExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            if (exp.e0.value != null)
                exp.e0.value = expressionSemantic(exp.e0.value, this.sc.value);
            boolean err = false;
            {
                int i = 0;
                for (; (i < (exp.exps.value.get()).length.value);i++){
                    Expression e = (exp.exps.value.get()).get(i);
                    e = expressionSemantic(e, this.sc.value);
                    if (e.type.value == null)
                    {
                        exp.error(new BytePtr("`%s` has no value"), e.toChars());
                        err = true;
                    }
                    else if (((e.op.value & 0xFF) == 127))
                        err = true;
                    else
                        exp.exps.value.get().set(i, e);
                }
            }
            if (err)
                this.setError();
                return ;
            expandTuples(exp.exps.value);
            exp.type.value = new TypeTuple(exp.exps.value);
            exp.type.value = typeSemantic(exp.type.value, exp.loc.value, this.sc.value);
            this.result.value = exp;
        }

        public  void visit(ArrayLiteralExp e) {
            if (e.type.value != null)
            {
                this.result.value = e;
                return ;
            }
            if (e.basis.value != null)
                e.basis.value = expressionSemantic(e.basis.value, this.sc.value);
            if (arrayExpressionSemantic(e.elements.value, this.sc.value, false) || (e.basis.value != null) && ((e.basis.value.op.value & 0xFF) == 127))
                this.setError();
                return ;
            expandTuples(e.elements.value);
            Ref<Type> t0 = ref(null);
            if (e.basis.value != null)
                (e.elements.value.get()).push(e.basis.value);
            boolean err = arrayExpressionToCommonType(this.sc.value, e.elements.value, ptr(t0));
            if (e.basis.value != null)
                e.basis.value = (e.elements.value.get()).pop();
            if (err)
                this.setError();
                return ;
            e.type.value = t0.value.arrayOf();
            e.type.value = typeSemantic(e.type.value, e.loc.value, this.sc.value);
            if (((e.elements.value.get()).length.value > 0) && ((t0.value.ty.value & 0xFF) == ENUMTY.Tvoid))
            {
                e.error(new BytePtr("`%s` of type `%s` has no value"), e.toChars(), e.type.value.toChars());
                this.setError();
                return ;
            }
            if (global.params.useTypeInfo.value && (Type.dtypeinfo.value != null))
                semanticTypeInfo(this.sc.value, e.type.value);
            this.result.value = e;
        }

        public  void visit(AssocArrayLiteralExp e) {
            if (e.type.value != null)
            {
                this.result.value = e;
                return ;
            }
            boolean err_keys = arrayExpressionSemantic(e.keys.value, this.sc.value, false);
            boolean err_vals = arrayExpressionSemantic(e.values.value, this.sc.value, false);
            if (err_keys || err_vals)
                this.setError();
                return ;
            expandTuples(e.keys.value);
            expandTuples(e.values.value);
            if (((e.keys.value.get()).length.value != (e.values.value.get()).length.value))
            {
                e.error(new BytePtr("number of keys is %u, must match number of values %u"), (e.keys.value.get()).length.value, (e.values.value.get()).length.value);
                this.setError();
                return ;
            }
            Ref<Type> tkey = ref(null);
            Ref<Type> tvalue = ref(null);
            err_keys = arrayExpressionToCommonType(this.sc.value, e.keys.value, ptr(tkey));
            err_vals = arrayExpressionToCommonType(this.sc.value, e.values.value, ptr(tvalue));
            if (err_keys || err_vals)
                this.setError();
                return ;
            if ((pequals(tkey.value, Type.terror.value)) || (pequals(tvalue.value, Type.terror.value)))
                this.setError();
                return ;
            e.type.value = new TypeAArray(tvalue.value, tkey.value);
            e.type.value = typeSemantic(e.type.value, e.loc.value, this.sc.value);
            semanticTypeInfo(this.sc.value, e.type.value);
            if (global.params.vsafe.value)
            {
                if (checkAssocArrayLiteralEscape(this.sc.value, e, false))
                    this.setError();
                    return ;
            }
            this.result.value = e;
        }

        public  void visit(StructLiteralExp e) {
            if (e.type.value != null)
            {
                this.result.value = e;
                return ;
            }
            e.sd.size(e.loc.value);
            if ((e.sd.sizeok.value != Sizeok.done))
                this.setError();
                return ;
            if (arrayExpressionSemantic(e.elements.value, this.sc.value, false))
                this.setError();
                return ;
            expandTuples(e.elements.value);
            if (!e.sd.fit(e.loc.value, this.sc.value, e.elements.value, e.stype.value))
                this.setError();
                return ;
            if (!e.sd.fill(e.loc.value, e.elements.value, false))
            {
                global.increaseErrorCount();
                this.setError();
                return ;
            }
            if (checkFrameAccess(e.loc.value, this.sc.value, e.sd, (e.elements.value.get()).length.value))
                this.setError();
                return ;
            e.type.value = e.stype.value != null ? e.stype.value : e.sd.type.value;
            this.result.value = e;
        }

        public  void visit(TypeExp exp) {
            if (((exp.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                this.setError();
                return ;
            Ref<Expression> e = ref(null);
            Ref<Type> t = ref(null);
            Ref<Dsymbol> s = ref(null);
            resolve(exp.type.value, exp.loc.value, this.sc.value, ptr(e), ptr(t), ptr(s), true);
            if (e.value != null)
            {
                e.value = expressionSemantic(e.value, this.sc.value);
            }
            else if (t.value != null)
            {
                exp.type.value = typeSemantic(t.value, exp.loc.value, this.sc.value);
                e.value = exp;
            }
            else if (s.value != null)
            {
                e.value = symbolToExp(s.value, exp.loc.value, this.sc.value, true);
            }
            else
                throw new AssertionError("Unreachable code!");
            if (global.params.vcomplex)
                exp.type.value.checkComplexTransition(exp.loc.value, this.sc.value);
            this.result.value = e.value;
        }

        public  void visit(ScopeExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            ScopeDsymbol sds2 = exp.sds.value;
            TemplateInstance ti = sds2.isTemplateInstance();
            for (; ti != null;){
                Ref<WithScopeSymbol> withsym = ref(null);
                if (!ti.findTempDecl(this.sc.value, ptr(withsym)) || !ti.semanticTiargs(this.sc.value))
                    this.setError();
                    return ;
                if ((withsym.value != null) && (withsym.value.withstate.wthis != null))
                {
                    Expression e = new VarExp(exp.loc.value, withsym.value.withstate.wthis, true);
                    e = new DotTemplateInstanceExp(exp.loc.value, e, ti);
                    this.result.value = expressionSemantic(e, this.sc.value);
                    return ;
                }
                if (ti.needsTypeInference(this.sc.value, 0))
                {
                    {
                        TemplateDeclaration td = ti.tempdecl.value.isTemplateDeclaration();
                        if ((td) != null)
                        {
                            Dsymbol p = td.toParentLocal();
                            FuncDeclaration fdthis = hasThis(this.sc.value);
                            AggregateDeclaration ad = p != null ? p.isAggregateDeclaration() : null;
                            if ((fdthis != null) && (ad != null) && (pequals(fdthis.isMemberLocal(), ad)) && (((td._scope.value.get()).stc.value & 1L) == 0L))
                            {
                                Expression e = new DotTemplateInstanceExp(exp.loc.value, new ThisExp(exp.loc.value), ti.name.value, ti.tiargs.value);
                                this.result.value = expressionSemantic(e, this.sc.value);
                                return ;
                            }
                        }
                        else {
                            OverloadSet os = ti.tempdecl.value.isOverloadSet();
                            if ((os) != null)
                            {
                                FuncDeclaration fdthis = hasThis(this.sc.value);
                                AggregateDeclaration ad = os.parent.value.isAggregateDeclaration();
                                if ((fdthis != null) && (ad != null) && (pequals(fdthis.isMemberLocal(), ad)))
                                {
                                    Expression e = new DotTemplateInstanceExp(exp.loc.value, new ThisExp(exp.loc.value), ti.name.value, ti.tiargs.value);
                                    this.result.value = expressionSemantic(e, this.sc.value);
                                    return ;
                                }
                            }
                        }
                    }
                    exp.sds.value = ti;
                    exp.type.value = Type.tvoid.value;
                    this.result.value = exp;
                    return ;
                }
                dsymbolSemantic(ti, this.sc.value);
                if ((ti.inst.value == null) || ti.errors.value)
                    this.setError();
                    return ;
                Dsymbol s = ti.toAlias();
                if ((pequals(s, ti)))
                {
                    exp.sds.value = ti;
                    exp.type.value = Type.tvoid.value;
                    this.result.value = exp;
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
                    if ((v) != null)
                    {
                        if (v.type.value == null)
                        {
                            exp.error(new BytePtr("forward reference of %s `%s`"), v.kind(), v.toChars());
                            this.setError();
                            return ;
                        }
                        if (((v.storage_class.value & 8388608L) != 0) && (v._init.value != null))
                        {
                            if (ti.inuse != 0)
                            {
                                exp.error(new BytePtr("recursive expansion of %s `%s`"), ti.kind(), ti.toPrettyChars(false));
                                this.setError();
                                return ;
                            }
                            v.checkDeprecated(exp.loc.value, this.sc.value);
                            Expression e = v.expandInitializer(exp.loc.value);
                            ti.inuse++;
                            e = expressionSemantic(e, this.sc.value);
                            ti.inuse--;
                            this.result.value = e;
                            return ;
                        }
                    }
                }
                Expression e = symbolToExp(s, exp.loc.value, this.sc.value, true);
                this.result.value = e;
                return ;
            }
            dsymbolSemantic(sds2, this.sc.value);
            {
                Type t = sds2.getType();
                if ((t) != null)
                {
                    this.result.value = expressionSemantic(new TypeExp(exp.loc.value, t), this.sc.value);
                    return ;
                }
            }
            {
                TemplateDeclaration td = sds2.isTemplateDeclaration();
                if ((td) != null)
                {
                    this.result.value = expressionSemantic(new TemplateExp(exp.loc.value, td, null), this.sc.value);
                    return ;
                }
            }
            exp.sds.value = sds2;
            exp.type.value = Type.tvoid.value;
            this.result.value = exp;
        }

        public  void visit(NewExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            Expression edim = null;
            if ((exp.arguments.value == null) && ((exp.newtype.value.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                edim = ((TypeSArray)exp.newtype.value).dim.value;
                exp.newtype.value = ((TypeNext)exp.newtype.value).next.value;
            }
            ClassDeclaration cdthis = null;
            if (exp.thisexp.value != null)
            {
                exp.thisexp.value = expressionSemantic(exp.thisexp.value, this.sc.value);
                if (((exp.thisexp.value.op.value & 0xFF) == 127))
                    this.setError();
                    return ;
                cdthis = exp.thisexp.value.type.value.isClassHandle();
                if (cdthis == null)
                {
                    exp.error(new BytePtr("`this` for nested class must be a class type, not `%s`"), exp.thisexp.value.type.value.toChars());
                    this.setError();
                    return ;
                }
                this.sc.value = (this.sc.value.get()).push(cdthis);
                exp.type.value = typeSemantic(exp.newtype.value, exp.loc.value, this.sc.value);
                this.sc.value = (this.sc.value.get()).pop();
            }
            else
            {
                exp.type.value = typeSemantic(exp.newtype.value, exp.loc.value, this.sc.value);
            }
            if (((exp.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                this.setError();
                return ;
            if (edim != null)
            {
                if (((exp.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Ttuple))
                {
                    exp.type.value = new TypeSArray(exp.type.value, edim);
                    exp.type.value = typeSemantic(exp.type.value, exp.loc.value, this.sc.value);
                    if (((exp.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                        this.setError();
                        return ;
                }
                else
                {
                    exp.arguments.value = refPtr(new DArray<Expression>());
                    (exp.arguments.value.get()).push(edim);
                    exp.type.value = exp.type.value.arrayOf();
                }
            }
            exp.newtype.value = exp.type.value;
            Type tb = exp.type.value.toBasetype();
            if (arrayExpressionSemantic(exp.newargs.value, this.sc.value, false) || preFunctionParameters(this.sc.value, exp.newargs.value))
            {
                this.setError();
                return ;
            }
            if (arrayExpressionSemantic(exp.arguments.value, this.sc.value, false) || preFunctionParameters(this.sc.value, exp.arguments.value))
            {
                this.setError();
                return ;
            }
            if ((exp.thisexp.value != null) && ((tb.ty.value & 0xFF) != ENUMTY.Tclass))
            {
                exp.error(new BytePtr("`.new` is only for allocating nested classes, not `%s`"), tb.toChars());
                this.setError();
                return ;
            }
            int nargs = exp.arguments.value != null ? (exp.arguments.value.get()).length.value : 0;
            Ref<Expression> newprefix = ref(null);
            if (((tb.ty.value & 0xFF) == ENUMTY.Tclass))
            {
                ClassDeclaration cd = ((TypeClass)tb).sym.value;
                cd.size(exp.loc.value);
                if ((cd.sizeok.value != Sizeok.done))
                    this.setError();
                    return ;
                if (cd.ctor.value == null)
                    cd.ctor.value = cd.searchCtor();
                if (cd.noDefaultCtor.value && (nargs == 0) && (cd.defaultCtor == null))
                {
                    exp.error(new BytePtr("default construction is disabled for type `%s`"), cd.type.value.toChars());
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
                        for (; (i < cd.vtbl.value.length.value);i++){
                            FuncDeclaration fd = cd.vtbl.value.get(i).isFuncDeclaration();
                            if ((fd != null) && fd.isAbstract())
                            {
                                errorSupplemental(exp.loc.value, new BytePtr("function `%s` is not implemented"), fd.toFullSignature());
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
                        if ((cdn) != null)
                        {
                            if (cdthis == null)
                            {
                                exp.thisexp.value = new ThisExp(exp.loc.value);
                                {
                                    Dsymbol sp = (this.sc.value.get()).parent.value;
                                    for (; 1 != 0;sp = sp.toParentLocal()){
                                        if (sp == null)
                                        {
                                            exp.error(new BytePtr("outer class `%s` `this` needed to `new` nested class `%s`"), cdn.toChars(), cd.toChars());
                                            this.setError();
                                            return ;
                                        }
                                        ClassDeclaration cdp = sp.isClassDeclaration();
                                        if (cdp == null)
                                            continue;
                                        if ((pequals(cdp, cdn)) || cdn.isBaseOf(cdp, null))
                                            break;
                                        exp.thisexp.value = new DotIdExp(exp.loc.value, exp.thisexp.value, Id.outer.value);
                                    }
                                }
                                exp.thisexp.value = expressionSemantic(exp.thisexp.value, this.sc.value);
                                if (((exp.thisexp.value.op.value & 0xFF) == 127))
                                    this.setError();
                                    return ;
                                cdthis = exp.thisexp.value.type.value.isClassHandle();
                            }
                            if ((!pequals(cdthis, cdn)) && !cdn.isBaseOf(cdthis, null))
                            {
                                exp.error(new BytePtr("`this` for nested class must be of type `%s`, not `%s`"), cdn.toChars(), exp.thisexp.value.type.value.toChars());
                                this.setError();
                                return ;
                            }
                            if (!MODimplicitConv(exp.thisexp.value.type.value.mod.value, exp.newtype.value.mod.value))
                            {
                                exp.error(new BytePtr("nested type `%s` should have the same or weaker constancy as enclosing type `%s`"), exp.newtype.value.toChars(), exp.thisexp.value.type.value.toChars());
                                this.setError();
                                return ;
                            }
                        }
                        else if (exp.thisexp.value != null)
                        {
                            exp.error(new BytePtr("`.new` is only for allocating nested classes"));
                            this.setError();
                            return ;
                        }
                        else {
                            FuncDeclaration fdn = s.isFuncDeclaration();
                            if ((fdn) != null)
                            {
                                if (!ensureStaticLinkTo((this.sc.value.get()).parent.value, fdn))
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
                else if (exp.thisexp.value != null)
                {
                    exp.error(new BytePtr("`.new` is only for allocating nested classes"));
                    this.setError();
                    return ;
                }
                if (cd.vthis2.value != null)
                {
                    {
                        AggregateDeclaration ad2 = cd.isMember2();
                        if ((ad2) != null)
                        {
                            Expression te = expressionSemantic(new ThisExp(exp.loc.value), this.sc.value);
                            if (((te.op.value & 0xFF) != 127))
                                te = getRightThis(exp.loc.value, this.sc.value, ad2, te, cd, 0);
                            if (((te.op.value & 0xFF) == 127))
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
                    Expression e = new IntegerExp(exp.loc.value, cd.size(exp.loc.value), Type.tsize_t.value);
                    if (exp.newargs.value == null)
                        exp.newargs.value = refPtr(new DArray<Expression>());
                    (exp.newargs.value.get()).shift(e);
                    FuncDeclaration f = resolveFuncCall(exp.loc.value, this.sc.value, cd.aggNew, null, tb, exp.newargs.value, FuncResolveFlag.standard);
                    if ((f == null) || f.errors.value)
                        this.setError();
                        return ;
                    checkFunctionAttributes(exp, this.sc.value, f);
                    checkAccess((AggregateDeclaration)cd, exp.loc.value, this.sc.value, (Dsymbol)f);
                    TypeFunction tf = (TypeFunction)f.type.value;
                    Ref<Type> rettype = ref(null);
                    if (functionParameters(exp.loc.value, this.sc.value, tf, null, null, exp.newargs.value, f, ptr(rettype), ptr(newprefix)))
                        this.setError();
                        return ;
                    exp.allocator.value = f.isNewDeclaration();
                    assert(exp.allocator.value != null);
                }
                else
                {
                    if ((exp.newargs.value != null) && ((exp.newargs.value.get()).length.value != 0))
                    {
                        exp.error(new BytePtr("no allocator for `%s`"), cd.toChars());
                        this.setError();
                        return ;
                    }
                }
                if (cd.ctor.value != null)
                {
                    FuncDeclaration f = resolveFuncCall(exp.loc.value, this.sc.value, cd.ctor.value, null, tb, exp.arguments.value, FuncResolveFlag.standard);
                    if ((f == null) || f.errors.value)
                        this.setError();
                        return ;
                    checkFunctionAttributes(exp, this.sc.value, f);
                    checkAccess((AggregateDeclaration)cd, exp.loc.value, this.sc.value, (Dsymbol)f);
                    TypeFunction tf = (TypeFunction)f.type.value;
                    if (exp.arguments.value == null)
                        exp.arguments.value = refPtr(new DArray<Expression>());
                    if (functionParameters(exp.loc.value, this.sc.value, tf, null, exp.type.value, exp.arguments.value, f, ptr(exp.type), ptr(exp.argprefix)))
                        this.setError();
                        return ;
                    exp.member.value = f.isCtorDeclaration();
                    assert(exp.member.value != null);
                }
                else
                {
                    if (nargs != 0)
                    {
                        exp.error(new BytePtr("no constructor for `%s`"), cd.toChars());
                        this.setError();
                        return ;
                    }
                    {
                        ClassDeclaration c = cd;
                        for (; c != null;c = c.baseClass.value){
                            {
                                Slice<VarDeclaration> __r1372 = c.fields.opSlice().copy();
                                int __key1373 = 0;
                                for (; (__key1373 < __r1372.getLength());__key1373 += 1) {
                                    VarDeclaration v = __r1372.get(__key1373);
                                    if ((v.inuse.value != 0) || (v._scope.value == null) || (v._init.value == null) || (v._init.value.isVoidInitializer() != null))
                                        continue;
                                    v.inuse.value++;
                                    v._init.value = initializerSemantic(v._init.value, v._scope.value, v.type.value, NeedInterpret.INITinterpret);
                                    v.inuse.value--;
                                }
                            }
                        }
                    }
                }
            }
            else if (((tb.ty.value & 0xFF) == ENUMTY.Tstruct))
            {
                StructDeclaration sd = ((TypeStruct)tb).sym.value;
                sd.size(exp.loc.value);
                if ((sd.sizeok.value != Sizeok.done))
                    this.setError();
                    return ;
                if (sd.ctor.value == null)
                    sd.ctor.value = sd.searchCtor();
                if (sd.noDefaultCtor.value && (nargs == 0))
                {
                    exp.error(new BytePtr("default construction is disabled for type `%s`"), sd.type.value.toChars());
                    this.setError();
                    return ;
                }
                if (sd.aggNew != null)
                {
                    Expression e = new IntegerExp(exp.loc.value, sd.size(exp.loc.value), Type.tsize_t.value);
                    if (exp.newargs.value == null)
                        exp.newargs.value = refPtr(new DArray<Expression>());
                    (exp.newargs.value.get()).shift(e);
                    FuncDeclaration f = resolveFuncCall(exp.loc.value, this.sc.value, sd.aggNew, null, tb, exp.newargs.value, FuncResolveFlag.standard);
                    if ((f == null) || f.errors.value)
                        this.setError();
                        return ;
                    checkFunctionAttributes(exp, this.sc.value, f);
                    checkAccess((AggregateDeclaration)sd, exp.loc.value, this.sc.value, (Dsymbol)f);
                    TypeFunction tf = (TypeFunction)f.type.value;
                    Ref<Type> rettype = ref(null);
                    if (functionParameters(exp.loc.value, this.sc.value, tf, null, null, exp.newargs.value, f, ptr(rettype), ptr(newprefix)))
                        this.setError();
                        return ;
                    exp.allocator.value = f.isNewDeclaration();
                    assert(exp.allocator.value != null);
                }
                else
                {
                    if ((exp.newargs.value != null) && ((exp.newargs.value.get()).length.value != 0))
                    {
                        exp.error(new BytePtr("no allocator for `%s`"), sd.toChars());
                        this.setError();
                        return ;
                    }
                }
                if ((sd.ctor.value != null) && (nargs != 0))
                {
                    FuncDeclaration f = resolveFuncCall(exp.loc.value, this.sc.value, sd.ctor.value, null, tb, exp.arguments.value, FuncResolveFlag.standard);
                    if ((f == null) || f.errors.value)
                        this.setError();
                        return ;
                    checkFunctionAttributes(exp, this.sc.value, f);
                    checkAccess((AggregateDeclaration)sd, exp.loc.value, this.sc.value, (Dsymbol)f);
                    TypeFunction tf = (TypeFunction)f.type.value;
                    if (exp.arguments.value == null)
                        exp.arguments.value = refPtr(new DArray<Expression>());
                    if (functionParameters(exp.loc.value, this.sc.value, tf, null, exp.type.value, exp.arguments.value, f, ptr(exp.type), ptr(exp.argprefix)))
                        this.setError();
                        return ;
                    exp.member.value = f.isCtorDeclaration();
                    assert(exp.member.value != null);
                    if (checkFrameAccess(exp.loc.value, this.sc.value, sd, sd.fields.length.value))
                        this.setError();
                        return ;
                }
                else
                {
                    if (exp.arguments.value == null)
                        exp.arguments.value = refPtr(new DArray<Expression>());
                    if (!sd.fit(exp.loc.value, this.sc.value, exp.arguments.value, tb))
                        this.setError();
                        return ;
                    if (!sd.fill(exp.loc.value, exp.arguments.value, false))
                        this.setError();
                        return ;
                    if (checkFrameAccess(exp.loc.value, this.sc.value, sd, exp.arguments.value != null ? (exp.arguments.value.get()).length.value : 0))
                        this.setError();
                        return ;
                    if (global.params.vsafe.value)
                    {
                        {
                            Slice<Expression> __r1374 = (exp.arguments.value.get()).opSlice().copy();
                            int __key1375 = 0;
                            for (; (__key1375 < __r1374.getLength());__key1375 += 1) {
                                Expression arg = __r1374.get(__key1375);
                                if ((arg != null) && checkNewEscape(this.sc.value, arg, false))
                                    this.setError();
                                    return ;
                            }
                        }
                    }
                }
                exp.type.value = exp.type.value.pointerTo();
            }
            else if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) && (nargs != 0))
            {
                Type tn = tb.nextOf().baseElemOf();
                Dsymbol s = tn.toDsymbol(this.sc.value);
                AggregateDeclaration ad = s != null ? s.isAggregateDeclaration() : null;
                if ((ad != null) && ad.noDefaultCtor.value)
                {
                    exp.error(new BytePtr("default construction is disabled for type `%s`"), tb.nextOf().toChars());
                    this.setError();
                    return ;
                }
                {
                    int i = 0;
                    for (; (i < nargs);i++){
                        if (((tb.ty.value & 0xFF) != ENUMTY.Tarray))
                        {
                            exp.error(new BytePtr("too many arguments for array"));
                            this.setError();
                            return ;
                        }
                        Expression arg = (exp.arguments.value.get()).get(i);
                        arg = resolveProperties(this.sc.value, arg);
                        arg = arg.implicitCastTo(this.sc.value, Type.tsize_t.value);
                        if (((arg.op.value & 0xFF) == 127))
                            this.setError();
                            return ;
                        arg = arg.optimize(0, false);
                        if (((arg.op.value & 0xFF) == 135) && ((long)arg.toInteger() < 0L))
                        {
                            exp.error(new BytePtr("negative array index `%s`"), arg.toChars());
                            this.setError();
                            return ;
                        }
                        exp.arguments.value.get().set(i, arg);
                        tb = ((TypeDArray)tb).next.value.toBasetype();
                    }
                }
            }
            else if (tb.isscalar())
            {
                if (nargs == 0)
                {
                }
                else if ((nargs == 1))
                {
                    Expression e = (exp.arguments.value.get()).get(0);
                    e = e.implicitCastTo(this.sc.value, tb);
                    exp.arguments.value.get().set(0, e);
                }
                else
                {
                    exp.error(new BytePtr("more than one argument for construction of `%s`"), exp.type.value.toChars());
                    this.setError();
                    return ;
                }
                exp.type.value = exp.type.value.pointerTo();
            }
            else
            {
                exp.error(new BytePtr("new can only create structs, dynamic arrays or class objects, not `%s`'s"), exp.type.value.toChars());
                this.setError();
                return ;
            }
            semanticTypeInfo(this.sc.value, exp.type.value);
            if (newprefix.value != null)
            {
                this.result.value = Expression.combine(newprefix.value, (Expression)exp);
                return ;
            }
            this.result.value = exp;
        }

        public  void visit(NewAnonClassExp e) {
            Expression d = new DeclarationExp(e.loc.value, e.cd);
            this.sc.value = (this.sc.value.get()).push();
            (this.sc.value.get()).flags.value &= -129;
            d = expressionSemantic(d, this.sc.value);
            this.sc.value = (this.sc.value.get()).pop();
            if (!e.cd.errors.value && ((this.sc.value.get()).intypeof.value != 0) && !(this.sc.value.get()).parent.value.inNonRoot())
            {
                ScopeDsymbol sds = (this.sc.value.get()).tinst != null ? (this.sc.value.get()).tinst : (this.sc.value.get())._module.value;
                if (sds.members.value == null)
                    sds.members.value = refPtr(new DArray<Dsymbol>());
                (sds.members.value.get()).push(e.cd);
            }
            Expression n = new NewExp(e.loc.value, e.thisexp, e.newargs, e.cd.type.value, e.arguments);
            Expression c = new CommaExp(e.loc.value, d, n, true);
            this.result.value = expressionSemantic(c, this.sc.value);
        }

        public  void visit(SymOffExp e) {
            if (e.type.value == null)
                e.type.value = e.var.value.type.value.pointerTo();
            {
                VarDeclaration v = e.var.value.isVarDeclaration();
                if ((v) != null)
                {
                    if (v.checkNestedReference(this.sc.value, e.loc.value))
                        this.setError();
                        return ;
                }
                else {
                    FuncDeclaration f = e.var.value.isFuncDeclaration();
                    if ((f) != null)
                    {
                        if (f.checkNestedReference(this.sc.value, e.loc.value))
                            this.setError();
                            return ;
                    }
                }
            }
            this.result.value = e;
        }

        public  void visit(VarExp e) {
            VarDeclaration vd = e.var.value.isVarDeclaration();
            FuncDeclaration fd = e.var.value.isFuncDeclaration();
            if (fd != null)
            {
                if (!fd.functionSemantic())
                    this.setError();
                    return ;
            }
            if (e.type.value == null)
                e.type.value = e.var.value.type.value;
            if ((e.type.value != null) && (e.type.value.deco.value == null))
            {
                Declaration decl = e.var.value.isDeclaration();
                if (decl != null)
                    decl.inuse.value++;
                e.type.value = typeSemantic(e.type.value, e.loc.value, this.sc.value);
                if (decl != null)
                    decl.inuse.value--;
            }
            if (vd != null)
            {
                if (vd.checkNestedReference(this.sc.value, e.loc.value))
                    this.setError();
                    return ;
            }
            else if (fd != null)
            {
                if (fd.checkNestedReference(this.sc.value, e.loc.value))
                    this.setError();
                    return ;
            }
            else {
                OverDeclaration od = e.var.value.isOverDeclaration();
                if ((od) != null)
                {
                    e.type.value = Type.tvoid.value;
                }
            }
            this.result.value = e;
        }

        public  void visit(FuncExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            Expression e = exp;
            int olderrors = 0;
            this.sc.value = (this.sc.value.get()).push();
            (this.sc.value.get()).flags.value &= -129;
            (this.sc.value.get()).protection.value = new Prot(Prot.Kind.public_).copy();
            exp.genIdent(this.sc.value);
            if ((exp.fd.value.treq.value != null) && (exp.fd.value.type.value.nextOf() == null))
            {
                TypeFunction tfv = null;
                if (((exp.fd.value.treq.value.ty.value & 0xFF) == ENUMTY.Tdelegate) || ((exp.fd.value.treq.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((exp.fd.value.treq.value.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction))
                    tfv = (TypeFunction)exp.fd.value.treq.value.nextOf();
                if (tfv != null)
                {
                    TypeFunction tfl = (TypeFunction)exp.fd.value.type.value;
                    tfl.next.value = tfv.nextOf();
                }
            }
            try {
                if (exp.td.value != null)
                {
                    assert((exp.td.value.parameters != null) && ((exp.td.value.parameters.get()).length.value != 0));
                    dsymbolSemantic(exp.td.value, this.sc.value);
                    exp.type.value = Type.tvoid.value;
                    if (exp.fd.value.treq.value != null)
                    {
                        Ref<FuncExp> fe = ref(null);
                        if ((exp.matchType(exp.fd.value.treq.value, this.sc.value, ptr(fe), 0) > MATCH.nomatch))
                            e = fe.value;
                        else
                            e = new ErrorExp();
                    }
                    /*goto Ldone*/throw Dispatch0.INSTANCE;
                }
                olderrors = global.errors.value;
                dsymbolSemantic(exp.fd.value, this.sc.value);
                if ((olderrors == global.errors.value))
                {
                    semantic2(exp.fd.value, this.sc.value);
                    if ((olderrors == global.errors.value))
                        semantic3(exp.fd.value, this.sc.value);
                }
                if ((olderrors != global.errors.value))
                {
                    if ((exp.fd.value.type.value != null) && ((exp.fd.value.type.value.ty.value & 0xFF) == ENUMTY.Tfunction) && (exp.fd.value.type.value.nextOf() == null))
                        ((TypeFunction)exp.fd.value.type.value).next.value = Type.terror.value;
                    e = new ErrorExp();
                    /*goto Ldone*/throw Dispatch0.INSTANCE;
                }
                if (exp.fd.value.isNested() && ((exp.fd.value.tok.value & 0xFF) == 160) || ((exp.tok.value & 0xFF) == 0) && (exp.fd.value.treq.value != null) && ((exp.fd.value.treq.value.ty.value & 0xFF) == ENUMTY.Tdelegate))
                {
                    exp.type.value = new TypeDelegate(exp.fd.value.type.value);
                    exp.type.value = typeSemantic(exp.type.value, exp.loc.value, this.sc.value);
                    exp.fd.value.tok.value = TOK.delegate_;
                }
                else
                {
                    exp.type.value = new TypePointer(exp.fd.value.type.value);
                    exp.type.value = typeSemantic(exp.type.value, exp.loc.value, this.sc.value);
                    if ((exp.fd.value.treq.value != null) && ((exp.fd.value.treq.value.ty.value & 0xFF) == ENUMTY.Tpointer))
                    {
                        exp.fd.value.tok.value = TOK.function_;
                        exp.fd.value.vthis.value = null;
                    }
                }
                exp.fd.value.tookAddressOf.value++;
            }
            catch(Dispatch0 __d){}
        /*Ldone:*/
            this.sc.value = (this.sc.value.get()).pop();
            this.result.value = e;
        }

        public  Expression callExpSemantic(FuncExp exp, Ptr<Scope> sc, Ptr<DArray<Expression>> arguments) {
            if ((exp.type.value == null) || (pequals(exp.type.value, Type.tvoid.value)) && (exp.td.value != null) && (arguments != null) && ((arguments.get()).length.value != 0))
            {
                {
                    int k = 0;
                    for (; (k < (arguments.get()).length.value);k++){
                        Expression checkarg = (arguments.get()).get(k);
                        if (((checkarg.op.value & 0xFF) == 127))
                            return checkarg;
                    }
                }
                exp.genIdent(sc);
                assert((exp.td.value.parameters != null) && ((exp.td.value.parameters.get()).length.value != 0));
                dsymbolSemantic(exp.td.value, sc);
                TypeFunction tfl = (TypeFunction)exp.fd.value.type.value;
                int dim = tfl.parameterList.length();
                if (((arguments.get()).length.value < dim))
                {
                    Parameter p = tfl.parameterList.get((arguments.get()).length.value);
                    if (p.defaultArg.value != null)
                        dim = (arguments.get()).length.value;
                }
                if ((tfl.parameterList.varargs.value == VarArg.none) && ((arguments.get()).length.value == dim) || (tfl.parameterList.varargs.value != VarArg.none) && ((arguments.get()).length.value >= dim))
                {
                    Ptr<DArray<RootObject>> tiargs = refPtr(new DArray<RootObject>());
                    (tiargs.get()).reserve((exp.td.value.parameters.get()).length.value);
                    {
                        int i = 0;
                        for (; (i < (exp.td.value.parameters.get()).length.value);i++){
                            TemplateParameter tp = (exp.td.value.parameters.get()).get(i);
                            {
                                int u = 0;
                                for (; (u < dim);u++){
                                    Parameter p = tfl.parameterList.get(u);
                                    if (((p.type.value.ty.value & 0xFF) == ENUMTY.Tident) && (pequals(((TypeIdentifier)p.type.value).ident.value, tp.ident.value)))
                                    {
                                        Expression e = (arguments.get()).get(u);
                                        (tiargs.get()).push(e.type.value);
                                        u = dim;
                                    }
                                }
                            }
                        }
                    }
                    TemplateInstance ti = new TemplateInstance(exp.loc.value, exp.td.value, tiargs);
                    return expressionSemantic(new ScopeExp(exp.loc.value, ti), sc);
                }
                exp.error(new BytePtr("cannot infer function literal type"));
                return new ErrorExp();
            }
            return expressionSemantic(exp, sc);
        }

        public  void visit(CallExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            Type t1 = null;
            Ptr<DArray<RootObject>> tiargs = null;
            Expression ethis = null;
            Type tthis = null;
            Expression e1org = exp.e1.value;
            if (((exp.e1.value.op.value & 0xFF) == 99))
            {
                CommaExp ce = (CommaExp)exp.e1.value;
                exp.e1.value = ce.e2.value;
                ce.e2.value = exp;
                this.result.value = expressionSemantic(ce, this.sc.value);
                return ;
            }
            if (((exp.e1.value.op.value & 0xFF) == 160))
            {
                DelegateExp de = (DelegateExp)exp.e1.value;
                exp.e1.value = new DotVarExp(de.loc.value, de.e1.value, de.func.value, de.hasOverloads.value);
                this.visit(exp);
                return ;
            }
            if (((exp.e1.value.op.value & 0xFF) == 161))
            {
                if (arrayExpressionSemantic(exp.arguments.value, this.sc.value, false) || preFunctionParameters(this.sc.value, exp.arguments.value))
                    this.setError();
                    return ;
                FuncExp fe = (FuncExp)exp.e1.value;
                exp.e1.value = this.callExpSemantic(fe, this.sc.value, exp.arguments.value);
                if (((exp.e1.value.op.value & 0xFF) == 127))
                {
                    this.result.value = exp.e1.value;
                    return ;
                }
            }
            {
                Expression ex = resolveUFCS(this.sc.value, exp);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            try {
                if (((exp.e1.value.op.value & 0xFF) == 203))
                {
                    ScopeExp se = (ScopeExp)exp.e1.value;
                    TemplateInstance ti = se.sds.value.isTemplateInstance();
                    if (ti != null)
                    {
                        Ref<WithScopeSymbol> withsym = ref(null);
                        if (!ti.findTempDecl(this.sc.value, ptr(withsym)) || !ti.semanticTiargs(this.sc.value))
                            this.setError();
                            return ;
                        if ((withsym.value != null) && (withsym.value.withstate.wthis != null))
                        {
                            exp.e1.value = new VarExp(exp.e1.value.loc.value, withsym.value.withstate.wthis, true);
                            exp.e1.value = new DotTemplateInstanceExp(exp.e1.value.loc.value, exp.e1.value, ti);
                            /*goto Ldotti*/throw Dispatch0.INSTANCE;
                        }
                        if (ti.needsTypeInference(this.sc.value, 1))
                        {
                            tiargs = ti.tiargs.value;
                            assert(ti.tempdecl.value != null);
                            {
                                TemplateDeclaration td = ti.tempdecl.value.isTemplateDeclaration();
                                if ((td) != null)
                                    exp.e1.value = new TemplateExp(exp.loc.value, td, null);
                                else {
                                    OverDeclaration od = ti.tempdecl.value.isOverDeclaration();
                                    if ((od) != null)
                                        exp.e1.value = new VarExp(exp.loc.value, od, true);
                                    else
                                        exp.e1.value = new OverExp(exp.loc.value, ti.tempdecl.value.isOverloadSet());
                                }
                            }
                        }
                        else
                        {
                            Expression e1x = expressionSemantic(exp.e1.value, this.sc.value);
                            if (((e1x.op.value & 0xFF) == 127))
                            {
                                this.result.value = e1x;
                                return ;
                            }
                            exp.e1.value = e1x;
                        }
                    }
                }
            }
            catch(Dispatch0 __d){}
        /*Ldotti:*/
            if (((exp.e1.value.op.value & 0xFF) == 29) && (exp.e1.value.type.value == null))
            {
                DotTemplateInstanceExp se = (DotTemplateInstanceExp)exp.e1.value;
                TemplateInstance ti = se.ti;
                {
                    if (!se.findTempDecl(this.sc.value) || !ti.semanticTiargs(this.sc.value))
                        this.setError();
                        return ;
                    if (ti.needsTypeInference(this.sc.value, 1))
                    {
                        tiargs = ti.tiargs.value;
                        assert(ti.tempdecl.value != null);
                        {
                            TemplateDeclaration td = ti.tempdecl.value.isTemplateDeclaration();
                            if ((td) != null)
                                exp.e1.value = new DotTemplateExp(exp.loc.value, se.e1.value, td);
                            else {
                                OverDeclaration od = ti.tempdecl.value.isOverDeclaration();
                                if ((od) != null)
                                {
                                    exp.e1.value = new DotVarExp(exp.loc.value, se.e1.value, od, true);
                                }
                                else
                                    exp.e1.value = new DotExp(exp.loc.value, se.e1.value, new OverExp(exp.loc.value, ti.tempdecl.value.isOverloadSet()));
                            }
                        }
                    }
                    else
                    {
                        Expression e1x = expressionSemantic(exp.e1.value, this.sc.value);
                        if (((e1x.op.value & 0xFF) == 127))
                        {
                            this.result.value = e1x;
                            return ;
                        }
                        exp.e1.value = e1x;
                    }
                }
            }
            while(true) try {
            /*Lagain:*/
                exp.f.value = null;
                if (((exp.e1.value.op.value & 0xFF) == 123) || ((exp.e1.value.op.value & 0xFF) == 124))
                {
                }
                else
                {
                    if (((exp.e1.value.op.value & 0xFF) == 28))
                    {
                        DotIdExp die = (DotIdExp)exp.e1.value;
                        exp.e1.value = expressionSemantic(die, this.sc.value);
                        if (((exp.e1.value.op.value & 0xFF) == 29) && (exp.e1.value.type.value == null))
                        {
                            /*goto Ldotti*/throw Dispatch0.INSTANCE;
                        }
                    }
                    else
                    {
                        if (((expressionsem.visitnest += 1) > 500))
                        {
                            exp.error(new BytePtr("recursive evaluation of `%s`"), exp.toChars());
                            expressionsem.visitnest -= 1;
                            this.setError();
                            return ;
                        }
                        Expression ex = unaSemantic(exp, this.sc.value);
                        expressionsem.visitnest -= 1;
                        if (ex != null)
                        {
                            this.result.value = ex;
                            return ;
                        }
                    }
                    if (((exp.e1.value.op.value & 0xFF) == 26))
                    {
                        VarExp ve = (VarExp)exp.e1.value;
                        if ((ve.var.value.storage_class.value & 8192L) != 0)
                        {
                            Type tw = ve.var.value.type.value;
                            Type tc = ve.var.value.type.value.substWildTo(1);
                            TypeFunction tf = new TypeFunction(new ParameterList(null, VarArg.none), tc, LINK.d, 8657043456L);
                            (tf = (TypeFunction)typeSemantic(tf, exp.loc.value, this.sc.value)).next.value = tw;
                            TypeDelegate t = new TypeDelegate(tf);
                            ve.type.value = typeSemantic(t, exp.loc.value, this.sc.value);
                        }
                        VarDeclaration v = ve.var.value.isVarDeclaration();
                        if ((v != null) && ve.checkPurity(this.sc.value, v))
                            this.setError();
                            return ;
                    }
                    if (((exp.e1.value.op.value & 0xFF) == 25) && ((SymOffExp)exp.e1.value).hasOverloads.value)
                    {
                        SymOffExp se = (SymOffExp)exp.e1.value;
                        exp.e1.value = new VarExp(se.loc.value, se.var.value, true);
                        exp.e1.value = expressionSemantic(exp.e1.value, this.sc.value);
                    }
                    else if (((exp.e1.value.op.value & 0xFF) == 97))
                    {
                        DotExp de = (DotExp)exp.e1.value;
                        if (((de.e2.value.op.value & 0xFF) == 214))
                        {
                            ethis = de.e1.value;
                            tthis = de.e1.value.type.value;
                            exp.e1.value = de.e2.value;
                        }
                    }
                    else if (((exp.e1.value.op.value & 0xFF) == 24) && ((exp.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tfunction))
                    {
                        exp.e1.value = ((PtrExp)exp.e1.value).e1.value;
                    }
                }
                t1 = exp.e1.value.type.value != null ? exp.e1.value.type.value.toBasetype() : null;
                if (((exp.e1.value.op.value & 0xFF) == 127))
                {
                    this.result.value = exp.e1.value;
                    return ;
                }
                if (arrayExpressionSemantic(exp.arguments.value, this.sc.value, false) || preFunctionParameters(this.sc.value, exp.arguments.value))
                    this.setError();
                    return ;
                if (t1 != null)
                {
                    if (((t1.ty.value & 0xFF) == ENUMTY.Tstruct))
                    {
                        StructDeclaration sd = ((TypeStruct)t1).sym.value;
                        sd.size(exp.loc.value);
                        if ((sd.sizeok.value != Sizeok.done))
                            this.setError();
                            return ;
                        if (sd.ctor.value == null)
                            sd.ctor.value = sd.searchCtor();
                        if (sd.ctor.value != null)
                        {
                            CtorDeclaration ctor = sd.ctor.value.isCtorDeclaration();
                            if ((ctor != null) && ctor.isCpCtor && ctor.generated)
                                sd.ctor.value = null;
                        }
                        try {
                            if (((exp.e1.value.op.value & 0xFF) == 20) && (sd.ctor.value != null))
                            {
                                if (!sd.noDefaultCtor.value && !((exp.arguments.value != null) && ((exp.arguments.value.get()).length.value != 0)))
                                    /*goto Lx*/throw Dispatch0.INSTANCE;
                                StructLiteralExp sle = new StructLiteralExp(exp.loc.value, sd, null, exp.e1.value.type.value);
                                if (!sd.fill(exp.loc.value, sle.elements.value, true))
                                    this.setError();
                                    return ;
                                if (checkFrameAccess(exp.loc.value, this.sc.value, sd, (sle.elements.value.get()).length.value))
                                    this.setError();
                                    return ;
                                sle.type.value = exp.e1.value.type.value;
                                sle.useStaticInit.value = false;
                                Expression e = sle;
                                {
                                    CtorDeclaration cf = sd.ctor.value.isCtorDeclaration();
                                    if ((cf) != null)
                                    {
                                        e = new DotVarExp(exp.loc.value, e, cf, true);
                                    }
                                    else {
                                        TemplateDeclaration td = sd.ctor.value.isTemplateDeclaration();
                                        if ((td) != null)
                                        {
                                            e = new DotIdExp(exp.loc.value, e, td.ident.value);
                                        }
                                        else {
                                            OverloadSet os = sd.ctor.value.isOverloadSet();
                                            if ((os) != null)
                                            {
                                                e = new DotExp(exp.loc.value, e, new OverExp(exp.loc.value, os));
                                            }
                                            else
                                                throw new AssertionError("Unreachable code!");
                                        }
                                    }
                                }
                                e = new CallExp(exp.loc.value, e, exp.arguments.value);
                                e = expressionSemantic(e, this.sc.value);
                                this.result.value = e;
                                return ;
                            }
                            if (search_function(sd, Id.call.value) != null)
                                /*goto L1*/if (((exp.e1.value.op.value & 0xFF) != 20))
                            {
                                if ((sd.aliasthis.value != null) && (!pequals(exp.e1.value.type.value, exp.att1.value)))
                                {
                                    if ((exp.att1.value == null) && exp.e1.value.type.value.checkAliasThisRec())
                                        exp.att1.value = exp.e1.value.type.value;
                                    exp.e1.value = resolveAliasThis(this.sc.value, exp.e1.value, false);
                                    /*goto Lagain*/throw Dispatch1.INSTANCE;
                                }
                                exp.error(new BytePtr("%s `%s` does not overload ()"), sd.kind(), sd.toChars());
                                this.setError();
                                return ;
                            }
                        }
                        catch(Dispatch0 __d){}
                    /*Lx:*/
                        Expression e = new StructLiteralExp(exp.loc.value, sd, exp.arguments.value, exp.e1.value.type.value);
                        e = expressionSemantic(e, this.sc.value);
                        this.result.value = e;
                        return ;
                    }
                    else if (((t1.ty.value & 0xFF) == ENUMTY.Tclass))
                    {
                    /*L1:*/
                        Expression e = new DotIdExp(exp.loc.value, exp.e1.value, Id.call.value);
                        e = new CallExp(exp.loc.value, e, exp.arguments.value);
                        e = expressionSemantic(e, this.sc.value);
                        this.result.value = e;
                        return ;
                    }
                    else if (((exp.e1.value.op.value & 0xFF) == 20) && t1.isscalar())
                    {
                        Expression e = null;
                        if (((exp.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tenum))
                        {
                            t1 = exp.e1.value.type.value;
                        }
                        if ((exp.arguments.value == null) || ((exp.arguments.value.get()).length.value == 0))
                        {
                            e = t1.defaultInitLiteral(exp.loc.value);
                        }
                        else if (((exp.arguments.value.get()).length.value == 1))
                        {
                            e = (exp.arguments.value.get()).get(0);
                            e = e.implicitCastTo(this.sc.value, t1);
                            e = new CastExp(exp.loc.value, e, t1);
                        }
                        else
                        {
                            exp.error(new BytePtr("more than one argument for construction of `%s`"), t1.toChars());
                            this.setError();
                            return ;
                        }
                        e = expressionSemantic(e, this.sc.value);
                        this.result.value = e;
                        return ;
                    }
                }
                Function6<Loc,Ptr<Scope>,OverloadSet,Ptr<DArray<RootObject>>,Type,Ptr<DArray<Expression>>,FuncDeclaration> resolveOverloadSet = new Function6<Loc,Ptr<Scope>,OverloadSet,Ptr<DArray<RootObject>>,Type,Ptr<DArray<Expression>>,FuncDeclaration>(){
                    public FuncDeclaration invoke(Loc loc, Ptr<Scope> sc, OverloadSet os, Ptr<DArray<RootObject>> tiargs, Type tthis, Ptr<DArray<Expression>> arguments) {
                        Ref<Ptr<Scope>> sc_ref = ref(sc);
                        Ref<Ptr<DArray<RootObject>>> tiargs_ref = ref(tiargs);
                        Ref<Type> tthis_ref = ref(tthis);
                        Ref<Ptr<DArray<Expression>>> arguments_ref = ref(arguments);
                        Ref<FuncDeclaration> f = ref(null);
                        {
                            Ref<Slice<Dsymbol>> __r1376 = ref(os.a.opSlice().copy());
                            IntRef __key1377 = ref(0);
                            for (; (__key1377.value < __r1376.value.getLength());__key1377.value += 1) {
                                Ref<Dsymbol> s = ref(__r1376.value.get(__key1377.value));
                                if ((tiargs_ref.value != null) && (s.value.isFuncDeclaration() != null))
                                    continue;
                                {
                                    Ref<FuncDeclaration> f2 = ref(resolveFuncCall(loc, sc_ref.value, s.value, tiargs_ref.value, tthis_ref.value, arguments_ref.value, FuncResolveFlag.quiet));
                                    if ((f2.value) != null)
                                    {
                                        if (f2.value.errors.value)
                                            return null;
                                        if (f.value != null)
                                        {
                                            ScopeDsymbol.multiplyDefined(loc, f.value, f2.value);
                                        }
                                        else
                                            f.value = f2.value;
                                    }
                                }
                            }
                        }
                        if (f.value == null)
                            error(loc, new BytePtr("no overload matches for `%s`"), os.toChars());
                        else if (f.value.errors.value)
                            f.value = null;
                        return f.value;
                    }
                };
                boolean isSuper = false;
                if (((exp.e1.value.op.value & 0xFF) == 27) && ((t1.ty.value & 0xFF) == ENUMTY.Tfunction) || ((exp.e1.value.op.value & 0xFF) == 37))
                {
                    UnaExp ue = (UnaExp)exp.e1.value;
                    Expression ue1 = ue.e1.value;
                    Expression ue1old = ue1;
                    VarDeclaration v = null;
                    if (((ue1.op.value & 0xFF) == 26) && ((v = ((VarExp)ue1).var.value.isVarDeclaration()) != null) && v.needThis())
                    {
                        ue.e1.value = new TypeExp(ue1.loc.value, ue1.type.value);
                        ue1 = null;
                    }
                    DotVarExp dve = null;
                    DotTemplateExp dte = null;
                    Dsymbol s = null;
                    if (((exp.e1.value.op.value & 0xFF) == 27))
                    {
                        dve = (DotVarExp)exp.e1.value;
                        dte = null;
                        s = dve.var.value;
                        tiargs = null;
                    }
                    else
                    {
                        dve = null;
                        dte = (DotTemplateExp)exp.e1.value;
                        s = dte.td.value;
                    }
                    exp.f.value = resolveFuncCall(exp.loc.value, this.sc.value, s, tiargs, ue1 != null ? ue1.type.value : null, exp.arguments.value, FuncResolveFlag.standard);
                    if ((exp.f.value == null) || exp.f.value.errors.value || ((exp.f.value.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                        this.setError();
                        return ;
                    if (exp.f.value.interfaceVirtual != null)
                    {
                        Ptr<BaseClass> b = exp.f.value.interfaceVirtual;
                        ClassDeclaration ad2 = (b.get()).sym.value;
                        ue.e1.value = ue.e1.value.castTo(this.sc.value, ad2.type.value.addMod(ue.e1.value.type.value.mod.value));
                        ue.e1.value = expressionSemantic(ue.e1.value, this.sc.value);
                        ue1 = ue.e1.value;
                        int vi = exp.f.value.findVtblIndex(ptr(ad2.vtbl), ad2.vtbl.value.length.value, true);
                        assert((vi >= 0));
                        exp.f.value = ad2.vtbl.value.get(vi).isFuncDeclaration();
                        assert(exp.f.value != null);
                    }
                    if (exp.f.value.needThis())
                    {
                        AggregateDeclaration ad = exp.f.value.toParentLocal().isAggregateDeclaration();
                        ue.e1.value = getRightThis(exp.loc.value, this.sc.value, ad, ue.e1.value, exp.f.value, 0);
                        if (((ue.e1.value.op.value & 0xFF) == 127))
                        {
                            this.result.value = ue.e1.value;
                            return ;
                        }
                        ethis = ue.e1.value;
                        tthis = ue.e1.value.type.value;
                        if (!(((exp.f.value.type.value.ty.value & 0xFF) == ENUMTY.Tfunction) && ((TypeFunction)exp.f.value.type.value).isscope.value))
                        {
                            if (global.params.vsafe.value && checkParamArgumentEscape(this.sc.value, exp.f.value, null, ethis, false))
                                this.setError();
                                return ;
                        }
                    }
                    if (((this.sc.value.get()).func.value != null) && ((this.sc.value.get()).func.value.isInvariantDeclaration() != null) && ((ue.e1.value.op.value & 0xFF) == 123) && exp.f.value.addPostInvariant())
                    {
                        exp.error(new BytePtr("cannot call `public`/`export` function `%s` from invariant"), exp.f.value.toChars());
                        this.setError();
                        return ;
                    }
                    checkFunctionAttributes(exp, this.sc.value, exp.f.value);
                    checkAccess(exp.loc.value, this.sc.value, ue.e1.value, exp.f.value);
                    if (!exp.f.value.needThis())
                    {
                        exp.e1.value = Expression.combine(ue.e1.value, new VarExp(exp.loc.value, exp.f.value, false));
                    }
                    else
                    {
                        if (ue1old.checkRightThis(this.sc.value))
                            this.setError();
                            return ;
                        if (((exp.e1.value.op.value & 0xFF) == 27))
                        {
                            dve.var.value = exp.f.value;
                            exp.e1.value.type.value = exp.f.value.type.value;
                        }
                        else
                        {
                            exp.e1.value = new DotVarExp(exp.loc.value, dte.e1.value, exp.f.value, false);
                            exp.e1.value = expressionSemantic(exp.e1.value, this.sc.value);
                            if (((exp.e1.value.op.value & 0xFF) == 127))
                                this.setError();
                                return ;
                            ue = (UnaExp)exp.e1.value;
                        }
                        AggregateDeclaration ad = exp.f.value.isThis();
                        ClassDeclaration cd = ue.e1.value.type.value.isClassHandle();
                        if ((ad != null) && (cd != null) && (ad.isClassDeclaration() != null))
                        {
                            if (((ue.e1.value.op.value & 0xFF) == 30))
                            {
                                ue.e1.value = ((DotTypeExp)ue.e1.value).e1.value;
                                exp.directcall = true;
                            }
                            else if (((ue.e1.value.op.value & 0xFF) == 124))
                                exp.directcall = true;
                            else if (((cd.storage_class & 8L) != 0L))
                                exp.directcall = true;
                            if ((!pequals(ad, cd)))
                            {
                                ue.e1.value = ue.e1.value.castTo(this.sc.value, ad.type.value.addMod(ue.e1.value.type.value.mod.value));
                                ue.e1.value = expressionSemantic(ue.e1.value, this.sc.value);
                            }
                        }
                    }
                    if (((exp.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((exp.e1.value.type.value.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction))
                    {
                        Expression e = new PtrExp(exp.loc.value, exp.e1.value);
                        e.type.value = exp.e1.value.type.value.nextOf();
                        exp.e1.value = e;
                    }
                    t1 = exp.e1.value.type.value;
                }
                else if (((exp.e1.value.op.value & 0xFF) == 124) || ((exp.e1.value.op.value & 0xFF) == 123))
                {
                    AggregateDeclaration ad = (this.sc.value.get()).func.value != null ? (this.sc.value.get()).func.value.isThis() : null;
                    ClassDeclaration cd = ad != null ? ad.isClassDeclaration() : null;
                    isSuper = (exp.e1.value.op.value & 0xFF) == 124;
                    if (isSuper)
                    {
                        if ((cd == null) || (cd.baseClass.value == null) || ((this.sc.value.get()).func.value.isCtorDeclaration() == null))
                        {
                            exp.error(new BytePtr("super class constructor call must be in a constructor"));
                            this.setError();
                            return ;
                        }
                        if (cd.baseClass.value.ctor.value == null)
                        {
                            exp.error(new BytePtr("no super class constructor for `%s`"), cd.baseClass.value.toChars());
                            this.setError();
                            return ;
                        }
                    }
                    else
                    {
                        if ((ad == null) || ((this.sc.value.get()).func.value.isCtorDeclaration() == null))
                        {
                            exp.error(new BytePtr("constructor call must be in a constructor"));
                            this.setError();
                            return ;
                        }
                        {
                            Slice<FieldInit> __r1378 = (this.sc.value.get()).ctorflow.fieldinit.copy();
                            int __key1379 = 0;
                            for (; (__key1379 < __r1378.getLength());__key1379 += 1) {
                                FieldInit field = __r1378.get(__key1379).copy();
                                field.csx.value |= 65;
                            }
                        }
                    }
                    if (((this.sc.value.get()).intypeof.value == 0) && (((this.sc.value.get()).ctorflow.callSuper.value & 32) == 0))
                    {
                        if ((this.sc.value.get()).inLoop || (((this.sc.value.get()).ctorflow.callSuper.value & 4) != 0))
                            exp.error(new BytePtr("constructor calls not allowed in loops or after labels"));
                        if (((this.sc.value.get()).ctorflow.callSuper.value & 3) != 0)
                            exp.error(new BytePtr("multiple constructor calls"));
                        if ((((this.sc.value.get()).ctorflow.callSuper.value & 8) != 0) && (((this.sc.value.get()).ctorflow.callSuper.value & 16) == 0))
                            exp.error(new BytePtr("an earlier `return` statement skips constructor"));
                        (this.sc.value.get()).ctorflow.callSuper.value |= 16 | (isSuper ? 2 : 1);
                    }
                    tthis = ad.type.value.addMod((this.sc.value.get()).func.value.type.value.mod.value);
                    Dsymbol ctor = isSuper ? cd.baseClass.value.ctor.value : ad.ctor.value;
                    {
                        OverloadSet os = ctor.isOverloadSet();
                        if ((os) != null)
                            exp.f.value = resolveOverloadSet.invoke(exp.loc.value, this.sc.value, os, null, tthis, exp.arguments.value);
                        else
                            exp.f.value = resolveFuncCall(exp.loc.value, this.sc.value, ctor, null, tthis, exp.arguments.value, FuncResolveFlag.standard);
                    }
                    if ((exp.f.value == null) || exp.f.value.errors.value)
                        this.setError();
                        return ;
                    checkFunctionAttributes(exp, this.sc.value, exp.f.value);
                    checkAccess(exp.loc.value, this.sc.value, null, exp.f.value);
                    exp.e1.value = new DotVarExp(exp.e1.value.loc.value, exp.e1.value, exp.f.value, false);
                    exp.e1.value = expressionSemantic(exp.e1.value, this.sc.value);
                    t1 = exp.e1.value.type.value;
                    if ((pequals(exp.f.value, (this.sc.value.get()).func.value)))
                    {
                        exp.error(new BytePtr("cyclic constructor call"));
                        this.setError();
                        return ;
                    }
                }
                else if (((exp.e1.value.op.value & 0xFF) == 214))
                {
                    OverloadSet os = ((OverExp)exp.e1.value).vars;
                    exp.f.value = resolveOverloadSet.invoke(exp.loc.value, this.sc.value, os, tiargs, tthis, exp.arguments.value);
                    if (exp.f.value == null)
                        this.setError();
                        return ;
                    if (ethis != null)
                        exp.e1.value = new DotVarExp(exp.loc.value, ethis, exp.f.value, false);
                    else
                        exp.e1.value = new VarExp(exp.loc.value, exp.f.value, false);
                    /*goto Lagain*/throw Dispatch1.INSTANCE;
                }
                else if (t1 == null)
                {
                    exp.error(new BytePtr("function expected before `()`, not `%s`"), exp.e1.value.toChars());
                    this.setError();
                    return ;
                }
                else if (((t1.ty.value & 0xFF) == ENUMTY.Terror))
                {
                    this.setError();
                    return ;
                }
                else if (((t1.ty.value & 0xFF) != ENUMTY.Tfunction))
                {
                    TypeFunction tf = null;
                    BytePtr p = null;
                    Dsymbol s = null;
                    exp.f.value = null;
                    if (((exp.e1.value.op.value & 0xFF) == 161))
                    {
                        assert(((FuncExp)exp.e1.value).fd.value != null);
                        exp.f.value = ((FuncExp)exp.e1.value).fd.value;
                        tf = (TypeFunction)exp.f.value.type.value;
                        p = pcopy(new BytePtr("function literal"));
                    }
                    else if (((t1.ty.value & 0xFF) == ENUMTY.Tdelegate))
                    {
                        TypeDelegate td = (TypeDelegate)t1;
                        assert(((td.next.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                        tf = (TypeFunction)td.next.value;
                        p = pcopy(new BytePtr("delegate"));
                    }
                    else if (((t1.ty.value & 0xFF) == ENUMTY.Tpointer) && ((((TypePointer)t1).next.value.ty.value & 0xFF) == ENUMTY.Tfunction))
                    {
                        tf = (TypeFunction)((TypePointer)t1).next.value;
                        p = pcopy(new BytePtr("function pointer"));
                    }
                    else if (((exp.e1.value.op.value & 0xFF) == 27) && (((DotVarExp)exp.e1.value).var.value.isOverDeclaration() != null))
                    {
                        DotVarExp dve = (DotVarExp)exp.e1.value;
                        exp.f.value = resolveFuncCall(exp.loc.value, this.sc.value, dve.var.value, tiargs, dve.e1.value.type.value, exp.arguments.value, FuncResolveFlag.overloadOnly);
                        if (exp.f.value == null)
                            this.setError();
                            return ;
                        if (exp.f.value.needThis())
                        {
                            dve.var.value = exp.f.value;
                            dve.type.value = exp.f.value.type.value;
                            dve.hasOverloads = false;
                            /*goto Lagain*/throw Dispatch1.INSTANCE;
                        }
                        exp.e1.value = new VarExp(dve.loc.value, exp.f.value, false);
                        Expression e = new CommaExp(exp.loc.value, dve.e1.value, exp, true);
                        this.result.value = expressionSemantic(e, this.sc.value);
                        return ;
                    }
                    else if (((exp.e1.value.op.value & 0xFF) == 26) && (((VarExp)exp.e1.value).var.value.isOverDeclaration() != null))
                    {
                        s = ((VarExp)exp.e1.value).var.value;
                        /*goto L2*//*unrolled goto*/
                    /*L2:*/
                        exp.f.value = resolveFuncCall(exp.loc.value, this.sc.value, s, tiargs, null, exp.arguments.value, FuncResolveFlag.standard);
                        if ((exp.f.value == null) || exp.f.value.errors.value)
                            this.setError();
                            return ;
                        if (exp.f.value.needThis())
                        {
                            if (hasThis(this.sc.value) != null)
                            {
                                exp.e1.value = new DotVarExp(exp.loc.value, expressionSemantic(new ThisExp(exp.loc.value), this.sc.value), exp.f.value, false);
                                /*goto Lagain*/throw Dispatch1.INSTANCE;
                            }
                            else if (isNeedThisScope(this.sc.value, exp.f.value))
                            {
                                exp.error(new BytePtr("need `this` for `%s` of type `%s`"), exp.f.value.toChars(), exp.f.value.type.value.toChars());
                                this.setError();
                                return ;
                            }
                        }
                        exp.e1.value = new VarExp(exp.e1.value.loc.value, exp.f.value, false);
                        /*goto Lagain*/throw Dispatch1.INSTANCE;
                    }
                    else if (((exp.e1.value.op.value & 0xFF) == 36))
                    {
                        s = ((TemplateExp)exp.e1.value).td.value;
                    /*L2:*/
                        exp.f.value = resolveFuncCall(exp.loc.value, this.sc.value, s, tiargs, null, exp.arguments.value, FuncResolveFlag.standard);
                        if ((exp.f.value == null) || exp.f.value.errors.value)
                            this.setError();
                            return ;
                        if (exp.f.value.needThis())
                        {
                            if (hasThis(this.sc.value) != null)
                            {
                                exp.e1.value = new DotVarExp(exp.loc.value, expressionSemantic(new ThisExp(exp.loc.value), this.sc.value), exp.f.value, false);
                                /*goto Lagain*/throw Dispatch1.INSTANCE;
                            }
                            else if (isNeedThisScope(this.sc.value, exp.f.value))
                            {
                                exp.error(new BytePtr("need `this` for `%s` of type `%s`"), exp.f.value.toChars(), exp.f.value.type.value.toChars());
                                this.setError();
                                return ;
                            }
                        }
                        exp.e1.value = new VarExp(exp.e1.value.loc.value, exp.f.value, false);
                        /*goto Lagain*/throw Dispatch1.INSTANCE;
                    }
                    else
                    {
                        exp.error(new BytePtr("function expected before `()`, not `%s` of type `%s`"), exp.e1.value.toChars(), exp.e1.value.type.value.toChars());
                        this.setError();
                        return ;
                    }
                    Ref<BytePtr> failMessage = ref(null);
                    Slice<Expression> fargs = exp.arguments.value != null ? (exp.arguments.value.get()).opSlice() : new Slice<Expression>().copy();
                    if (tf.callMatch(null, fargs, 0, ptr(failMessage), this.sc.value) == 0)
                    {
                        Ref<OutBuffer> buf = ref(new OutBuffer());
                        try {
                            buf.value.writeByte(40);
                            argExpTypesToCBuffer(ptr(buf), exp.arguments.value);
                            buf.value.writeByte(41);
                            if (tthis != null)
                                tthis.modToBuffer(ptr(buf));
                            error(exp.loc.value, new BytePtr("%s `%s%s` is not callable using argument types `%s`"), p, exp.e1.value.toChars(), parametersTypeToChars(tf.parameterList), buf.value.peekChars());
                            if (failMessage.value != null)
                                errorSupplemental(exp.loc.value, new BytePtr("%s"), failMessage.value);
                            this.setError();
                            return ;
                        }
                        finally {
                        }
                    }
                    if (exp.f.value != null)
                    {
                        exp.checkPurity(this.sc.value, exp.f.value);
                        exp.checkSafety(this.sc.value, exp.f.value);
                        exp.checkNogc(this.sc.value, exp.f.value);
                        if (exp.f.value.checkNestedReference(this.sc.value, exp.loc.value))
                            this.setError();
                            return ;
                    }
                    else if (((this.sc.value.get()).func.value != null) && ((this.sc.value.get()).intypeof.value != 1) && (((this.sc.value.get()).flags.value & 128) == 0))
                    {
                        boolean err = false;
                        if ((tf.purity.value == 0) && (((this.sc.value.get()).flags.value & 8) == 0) && (this.sc.value.get()).func.value.setImpure())
                        {
                            exp.error(new BytePtr("`pure` %s `%s` cannot call impure %s `%s`"), (this.sc.value.get()).func.value.kind(), (this.sc.value.get()).func.value.toPrettyChars(false), p, exp.e1.value.toChars());
                            err = true;
                        }
                        if (!tf.isnogc.value && (this.sc.value.get()).func.value.setGC() && (((this.sc.value.get()).flags.value & 8) == 0))
                        {
                            exp.error(new BytePtr("`@nogc` %s `%s` cannot call non-@nogc %s `%s`"), (this.sc.value.get()).func.value.kind(), (this.sc.value.get()).func.value.toPrettyChars(false), p, exp.e1.value.toChars());
                            err = true;
                        }
                        if ((tf.trust.value <= TRUST.system) && (this.sc.value.get()).func.value.setUnsafe() && (((this.sc.value.get()).flags.value & 8) == 0))
                        {
                            exp.error(new BytePtr("`@safe` %s `%s` cannot call `@system` %s `%s`"), (this.sc.value.get()).func.value.kind(), (this.sc.value.get()).func.value.toPrettyChars(false), p, exp.e1.value.toChars());
                            err = true;
                        }
                        if (err)
                            this.setError();
                            return ;
                    }
                    if (((t1.ty.value & 0xFF) == ENUMTY.Tpointer))
                    {
                        Expression e = new PtrExp(exp.loc.value, exp.e1.value);
                        e.type.value = tf;
                        exp.e1.value = e;
                    }
                    t1 = tf;
                }
                else if (((exp.e1.value.op.value & 0xFF) == 26))
                {
                    VarExp ve = (VarExp)exp.e1.value;
                    exp.f.value = ve.var.value.isFuncDeclaration();
                    assert(exp.f.value != null);
                    tiargs = null;
                    if (exp.f.value.overnext != null)
                        exp.f.value = resolveFuncCall(exp.loc.value, this.sc.value, exp.f.value, tiargs, null, exp.arguments.value, FuncResolveFlag.overloadOnly);
                    else
                    {
                        exp.f.value = exp.f.value.toAliasFunc();
                        TypeFunction tf = (TypeFunction)exp.f.value.type.value;
                        Ref<BytePtr> failMessage = ref(null);
                        Slice<Expression> fargs = exp.arguments.value != null ? (exp.arguments.value.get()).opSlice() : new Slice<Expression>().copy();
                        if (tf.callMatch(null, fargs, 0, ptr(failMessage), this.sc.value) == 0)
                        {
                            Ref<OutBuffer> buf = ref(new OutBuffer());
                            try {
                                buf.value.writeByte(40);
                                argExpTypesToCBuffer(ptr(buf), exp.arguments.value);
                                buf.value.writeByte(41);
                                error(exp.loc.value, new BytePtr("%s `%s%s` is not callable using argument types `%s`"), exp.f.value.kind(), exp.f.value.toPrettyChars(false), parametersTypeToChars(tf.parameterList), buf.value.peekChars());
                                if (failMessage.value != null)
                                    errorSupplemental(exp.loc.value, new BytePtr("%s"), failMessage.value);
                                exp.f.value = null;
                            }
                            finally {
                            }
                        }
                    }
                    if ((exp.f.value == null) || exp.f.value.errors.value)
                        this.setError();
                        return ;
                    if (exp.f.value.needThis())
                    {
                        if (exp.f.value.checkNestedReference(this.sc.value, exp.loc.value))
                            this.setError();
                            return ;
                        if (hasThis(this.sc.value) != null)
                        {
                            exp.e1.value = new DotVarExp(exp.loc.value, expressionSemantic(new ThisExp(exp.loc.value), this.sc.value), ve.var.value, true);
                            /*goto Lagain*/throw Dispatch1.INSTANCE;
                        }
                        else if (isNeedThisScope(this.sc.value, exp.f.value))
                        {
                            exp.error(new BytePtr("need `this` for `%s` of type `%s`"), exp.f.value.toChars(), exp.f.value.type.value.toChars());
                            this.setError();
                            return ;
                        }
                    }
                    checkFunctionAttributes(exp, this.sc.value, exp.f.value);
                    checkAccess(exp.loc.value, this.sc.value, null, exp.f.value);
                    if (exp.f.value.checkNestedReference(this.sc.value, exp.loc.value))
                        this.setError();
                        return ;
                    ethis = null;
                    tthis = null;
                    if (ve.hasOverloads.value)
                    {
                        exp.e1.value = new VarExp(ve.loc.value, exp.f.value, false);
                        exp.e1.value.type.value = exp.f.value.type.value;
                    }
                    t1 = exp.f.value.type.value;
                }
                assert(((t1.ty.value & 0xFF) == ENUMTY.Tfunction));
                Ref<Expression> argprefix = ref(null);
                if (exp.arguments.value == null)
                    exp.arguments.value = refPtr(new DArray<Expression>());
                if (functionParameters(exp.loc.value, this.sc.value, (TypeFunction)t1, ethis, tthis, exp.arguments.value, exp.f.value, ptr(exp.type), ptr(argprefix)))
                    this.setError();
                    return ;
                if (exp.type.value == null)
                {
                    exp.e1.value = e1org;
                    exp.error(new BytePtr("forward reference to inferred return type of function call `%s`"), exp.toChars());
                    this.setError();
                    return ;
                }
                if ((exp.f.value != null) && (exp.f.value.tintro.value != null))
                {
                    Type t = exp.type.value;
                    IntRef offset = ref(0);
                    TypeFunction tf = (TypeFunction)exp.f.value.tintro.value;
                    if (tf.next.value.isBaseOf(t, ptr(offset)) && (offset.value != 0))
                    {
                        exp.type.value = tf.next.value;
                        this.result.value = Expression.combine(argprefix.value, exp.castTo(this.sc.value, t));
                        return ;
                    }
                }
                if ((exp.f.value != null) && (exp.f.value.isFuncLiteralDeclaration() != null) && ((this.sc.value.get()).func.value != null) && ((this.sc.value.get()).intypeof.value == 0))
                {
                    exp.f.value.tookAddressOf.value = 0;
                }
                this.result.value = Expression.combine(argprefix.value, (Expression)exp);
                if (isSuper)
                {
                    AggregateDeclaration ad = (this.sc.value.get()).func.value != null ? (this.sc.value.get()).func.value.isThis() : null;
                    ClassDeclaration cd = ad != null ? ad.isClassDeclaration() : null;
                    if ((cd != null) && (cd.classKind.value == ClassKind.cpp) && (exp.f.value != null) && (exp.f.value.fbody.value == null))
                    {
                        Loc loc = exp.loc.value.copy();
                        DotIdExp vptr = new DotIdExp(loc, new ThisExp(loc), Id.__vptr.value);
                        VarDeclaration vptrTmpDecl = copyToTemp(0L, new BytePtr("__vptrTmp"), vptr);
                        DeclarationExp declareVptrTmp = new DeclarationExp(loc, vptrTmpDecl);
                        VarDeclaration superTmpDecl = copyToTemp(0L, new BytePtr("__superTmp"), this.result.value);
                        DeclarationExp declareSuperTmp = new DeclarationExp(loc, superTmpDecl);
                        CommaExp declareTmps = new CommaExp(loc, declareVptrTmp, declareSuperTmp, true);
                        AssignExp restoreVptr = new AssignExp(loc, vptr.syntaxCopy(), new VarExp(loc, vptrTmpDecl, true));
                        Expression e = new CommaExp(loc, declareTmps, new CommaExp(loc, restoreVptr, new VarExp(loc, superTmpDecl, true), true), true);
                        this.result.value = expressionSemantic(e, this.sc.value);
                    }
                }
                if ((exp.f.value != null) && exp.f.value.isThis2.value && ((this.sc.value.get()).intypeof.value == 0) && ((this.sc.value.get()).func.value != null))
                {
                    {
                        AggregateDeclaration ad2 = exp.f.value.isMember2();
                        if ((ad2) != null)
                        {
                            Expression te = expressionSemantic(new ThisExp(exp.loc.value), this.sc.value);
                            if (((te.op.value & 0xFF) != 127))
                                te = getRightThis(exp.loc.value, this.sc.value, ad2, te, exp.f.value, 0);
                            if (((te.op.value & 0xFF) == 127))
                            {
                                exp.error(new BytePtr("need `this` of type `%s` to call function `%s`"), ad2.toChars(), exp.f.value.toChars());
                                this.setError();
                                return ;
                            }
                        }
                    }
                    VarDeclaration vthis2 = makeThis2Argument(exp.loc.value, this.sc.value, exp.f.value);
                    exp.vthis2 = vthis2;
                    Expression de = new DeclarationExp(exp.loc.value, vthis2);
                    this.result.value = Expression.combine(de, this.result.value);
                    this.result.value = expressionSemantic(this.result.value, this.sc.value);
                }
                break;
            } catch(Dispatch1 __d){}
        }

        public  void visit(DeclarationExp e) {
            if (e.type.value != null)
            {
                this.result.value = e;
                return ;
            }
            int olderrors = global.errors.value;
            Dsymbol s = e.declaration.value;
            for (; 1 != 0;){
                AttribDeclaration ad = s.isAttribDeclaration();
                if (ad != null)
                {
                    if ((ad.decl.value != null) && ((ad.decl.value.get()).length.value == 1))
                    {
                        s = (ad.decl.value.get()).get(0);
                        continue;
                    }
                }
                break;
            }
            VarDeclaration v = s.isVarDeclaration();
            if (v != null)
            {
                dsymbolSemantic(e.declaration.value, this.sc.value);
                s.parent.value = (this.sc.value.get()).parent.value;
            }
            if (s.ident.value != null)
            {
                if ((this.sc.value.get()).insert(s) == null)
                {
                    e.error(new BytePtr("declaration `%s` is already defined"), s.toPrettyChars(false));
                    this.setError();
                    return ;
                }
                else if ((this.sc.value.get()).func.value != null)
                {
                    if ((s.isFuncDeclaration() != null) || (s.isAggregateDeclaration() != null) || (s.isEnumDeclaration() != null) || (v != null) && v.isDataseg() && ((this.sc.value.get()).func.value.localsymtab.insert(s) == null))
                    {
                        s.parent.value = (this.sc.value.get()).parent.value;
                        Dsymbol originalSymbol = (this.sc.value.get()).func.value.localsymtab.lookup(s.ident.value);
                        assert(originalSymbol != null);
                        e.error(new BytePtr("declaration `%s` is already defined in another scope in `%s` at line `%d`"), s.toPrettyChars(false), (this.sc.value.get()).func.value.toChars(), originalSymbol.loc.value.linnum);
                        this.setError();
                        return ;
                    }
                    else
                    {
                        {
                            Ptr<Scope> scx = (this.sc.value.get()).enclosing.value;
                            for (; (scx != null) && (pequals((scx.get()).func.value, (this.sc.value.get()).func.value));scx = (scx.get()).enclosing.value){
                                Dsymbol s2 = null;
                                if (((scx.get()).scopesym.value != null) && ((scx.get()).scopesym.value.symtab != null) && ((s2 = (scx.get()).scopesym.value.symtab.lookup(s.ident.value)) != null) && (!pequals(s, s2)))
                                {
                                    Declaration decl = s2.isDeclaration();
                                    if ((decl == null) || ((decl.storage_class.value & 2251799813685248L) == 0))
                                    {
                                        e.error(new BytePtr("%s `%s` is shadowing %s `%s`"), s.kind(), s.ident.value.toChars(), s2.kind(), s2.toPrettyChars(false));
                                        this.setError();
                                        return ;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (s.isVarDeclaration() == null)
            {
                Ptr<Scope> sc2 = this.sc.value;
                if (((sc2.get()).stc.value & 4398147174400L) != 0)
                    sc2 = (this.sc.value.get()).push();
                (sc2.get()).stc.value &= -4398147174401L;
                dsymbolSemantic(e.declaration.value, sc2);
                if ((sc2 != this.sc.value))
                    (sc2.get()).pop();
                s.parent.value = (this.sc.value.get()).parent.value;
            }
            if ((global.errors.value == olderrors))
            {
                semantic2(e.declaration.value, this.sc.value);
                if ((global.errors.value == olderrors))
                {
                    semantic3(e.declaration.value, this.sc.value);
                }
            }
            e.type.value = Type.tvoid.value;
            this.result.value = e;
        }

        public  void visit(TypeidExp exp) {
            Ref<Type> ta = ref(isType(exp.obj.value));
            Ref<Expression> ea = ref(isExpression(exp.obj.value));
            Ref<Dsymbol> sa = ref(isDsymbol(exp.obj.value));
            if (ta.value != null)
            {
                resolve(ta.value, exp.loc.value, this.sc.value, ptr(ea), ptr(ta), ptr(sa), true);
            }
            if (ea.value != null)
            {
                {
                    Dsymbol sym = getDsymbol(ea.value);
                    if ((sym) != null)
                        ea.value = symbolToExp(sym, exp.loc.value, this.sc.value, false);
                    else
                        ea.value = expressionSemantic(ea.value, this.sc.value);
                }
                ea.value = resolveProperties(this.sc.value, ea.value);
                ta.value = ea.value.type.value;
                if (((ea.value.op.value & 0xFF) == 20))
                    ea.value = null;
            }
            if (ta.value == null)
            {
                exp.error(new BytePtr("no type for `typeid(%s)`"), ea.value != null ? ea.value.toChars() : sa.value != null ? sa.value.toChars() : new BytePtr(""));
                this.setError();
                return ;
            }
            if (global.params.vcomplex)
                ta.value.checkComplexTransition(exp.loc.value, this.sc.value);
            Expression e = null;
            Type tb = ta.value.toBasetype();
            if ((ea.value != null) && ((tb.ty.value & 0xFF) == ENUMTY.Tclass))
            {
                if ((tb.toDsymbol(this.sc.value).isClassDeclaration().classKind.value == ClassKind.cpp))
                {
                    error(exp.loc.value, new BytePtr("Runtime type information is not supported for `extern(C++)` classes"));
                    e = new ErrorExp();
                }
                else if (Type.typeinfoclass.value == null)
                {
                    error(exp.loc.value, new BytePtr("`object.TypeInfo_Class` could not be found, but is implicitly used"));
                    e = new ErrorExp();
                }
                else
                {
                    ea.value = expressionSemantic(ea.value, this.sc.value);
                    e = new TypeidExp(ea.value.loc.value, ea.value);
                    e.type.value = Type.typeinfoclass.value.type.value;
                }
            }
            else if (((ta.value.ty.value & 0xFF) == ENUMTY.Terror))
            {
                e = new ErrorExp();
            }
            else
            {
                e = new TypeidExp(exp.loc.value, ta.value);
                e.type.value = getTypeInfoType(exp.loc.value, ta.value, this.sc.value);
                semanticTypeInfo(this.sc.value, ta.value);
                if (ea.value != null)
                {
                    e = new CommaExp(exp.loc.value, ea.value, e, true);
                    e = expressionSemantic(e, this.sc.value);
                }
            }
            this.result.value = e;
        }

        public  void visit(TraitsExp e) {
            this.result.value = semanticTraits(e, this.sc.value);
        }

        public  void visit(HaltExp e) {
            e.type.value = Type.tvoid.value;
            this.result.value = e;
        }

        public  void visit(IsExp e) {
            if ((e.id != null) && (((this.sc.value.get()).flags.value & 4) == 0))
            {
                e.error(new BytePtr("can only declare type aliases within `static if` conditionals or `static assert`s"));
                this.setError();
                return ;
            }
            Type tded = null;
            try {
                try {
                    if (((e.tok2 & 0xFF) == 180) || ((e.tok2 & 0xFF) == 34))
                    {
                        Dsymbol sym = e.targ.value.toDsymbol(this.sc.value);
                        if ((sym == null))
                            /*goto Lno*/throw Dispatch1.INSTANCE;
                        dmodule.Package p = resolveIsPackage(sym);
                        if ((p == null))
                            /*goto Lno*/throw Dispatch1.INSTANCE;
                        if (((e.tok2 & 0xFF) == 180) && (p.isModule() != null))
                            /*goto Lno*/throw Dispatch1.INSTANCE;
                        else if (((e.tok2 & 0xFF) == 34) && !((p.isModule() != null) || (p.isPackageMod() != null)))
                            /*goto Lno*/throw Dispatch1.INSTANCE;
                        tded = e.targ.value;
                        /*goto Lyes*/throw Dispatch0.INSTANCE;
                    }
                    {
                        Ptr<Scope> sc2 = (this.sc.value.get()).copy();
                        (sc2.get()).tinst = null;
                        (sc2.get()).minst.value = null;
                        (sc2.get()).flags.value |= 65536;
                        Type t = e.targ.value.trySemantic(e.loc.value, sc2);
                        (sc2.get()).pop();
                        if (t == null)
                            /*goto Lno*/throw Dispatch1.INSTANCE;
                        e.targ.value = t;
                    }
                    if (((e.tok2 & 0xFF) != 0))
                    {
                        {
                            int __dispatch5 = 0;
                            dispatched_5:
                            do {
                                switch (__dispatch5 != 0 ? __dispatch5 : (e.tok2 & 0xFF))
                                {
                                    case 152:
                                        if (((e.targ.value.ty.value & 0xFF) != ENUMTY.Tstruct))
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        if (((TypeStruct)e.targ.value).sym.value.isUnionDeclaration() != null)
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = e.targ.value;
                                        break;
                                    case 155:
                                        if (((e.targ.value.ty.value & 0xFF) != ENUMTY.Tstruct))
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        if (((TypeStruct)e.targ.value).sym.value.isUnionDeclaration() == null)
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = e.targ.value;
                                        break;
                                    case 153:
                                        if (((e.targ.value.ty.value & 0xFF) != ENUMTY.Tclass))
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        if (((TypeClass)e.targ.value).sym.value.isInterfaceDeclaration() != null)
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = e.targ.value;
                                        break;
                                    case 154:
                                        if (((e.targ.value.ty.value & 0xFF) != ENUMTY.Tclass))
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        if (((TypeClass)e.targ.value).sym.value.isInterfaceDeclaration() == null)
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = e.targ.value;
                                        break;
                                    case 171:
                                        if (!e.targ.value.isConst())
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = e.targ.value;
                                        break;
                                    case 182:
                                        if (!e.targ.value.isImmutable())
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = e.targ.value;
                                        break;
                                    case 224:
                                        if (!e.targ.value.isShared())
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = e.targ.value;
                                        break;
                                    case 177:
                                        if (!e.targ.value.isWild())
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = e.targ.value;
                                        break;
                                    case 124:
                                        if (((e.targ.value.ty.value & 0xFF) != ENUMTY.Tclass))
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        else
                                        {
                                            ClassDeclaration cd = ((TypeClass)e.targ.value).sym.value;
                                            Ptr<DArray<Parameter>> args = refPtr(new DArray<Parameter>());
                                            (args.get()).reserve((cd.baseclasses.get()).length.value);
                                            if ((cd.semanticRun.value < PASS.semanticdone))
                                                dsymbolSemantic(cd, null);
                                            {
                                                int i = 0;
                                                for (; (i < (cd.baseclasses.get()).length.value);i++){
                                                    Ptr<BaseClass> b = (cd.baseclasses.get()).get(i);
                                                    (args.get()).push(new Parameter(2048L, (b.get()).type.value, null, null, null));
                                                }
                                            }
                                            tded = new TypeTuple(args);
                                        }
                                        break;
                                    case 156:
                                        if (((e.targ.value.ty.value & 0xFF) != ENUMTY.Tenum))
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        if (e.id != null)
                                            tded = ((TypeEnum)e.targ.value).sym.value.getMemtype(e.loc.value);
                                        else
                                            tded = e.targ.value;
                                        if (((tded.ty.value & 0xFF) == ENUMTY.Terror))
                                            this.setError();
                                            return ;
                                        break;
                                    case 160:
                                        if (((e.targ.value.ty.value & 0xFF) != ENUMTY.Tdelegate))
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = ((TypeDelegate)e.targ.value).next.value;
                                        break;
                                    case 161:
                                    case 212:
                                        if (((e.targ.value.ty.value & 0xFF) != ENUMTY.Tfunction))
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = e.targ.value;
                                        assert(((tded.ty.value & 0xFF) == ENUMTY.Tfunction));
                                        TypeFunction tdedf = tded.isTypeFunction();
                                        int dim = tdedf.parameterList.length();
                                        Ptr<DArray<Parameter>> args_1 = refPtr(new DArray<Parameter>());
                                        (args_1.get()).reserve(dim);
                                        {
                                            int i_1 = 0;
                                            for (; (i_1 < dim);i_1++){
                                                Parameter arg = tdedf.parameterList.get(i_1);
                                                assert((arg != null) && (arg.type.value != null));
                                                if (((e.tok2 & 0xFF) == 212) && (arg.defaultArg.value != null) && ((arg.defaultArg.value.op.value & 0xFF) == 127))
                                                    this.setError();
                                                    return ;
                                                (args_1.get()).push(new Parameter(arg.storageClass.value, arg.type.value, ((e.tok2 & 0xFF) == 212) ? arg.ident.value : null, ((e.tok2 & 0xFF) == 212) ? arg.defaultArg.value : null, arg.userAttribDecl.value));
                                            }
                                        }
                                        tded = new TypeTuple(args_1);
                                        break;
                                    case 195:
                                        if (((e.targ.value.ty.value & 0xFF) == ENUMTY.Tfunction))
                                            tded = ((TypeFunction)e.targ.value).next.value;
                                        else if (((e.targ.value.ty.value & 0xFF) == ENUMTY.Tdelegate))
                                        {
                                            tded = ((TypeDelegate)e.targ.value).next.value;
                                            tded = ((TypeFunction)tded).next.value;
                                        }
                                        else if (((e.targ.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((((TypePointer)e.targ.value).next.value.ty.value & 0xFF) == ENUMTY.Tfunction))
                                        {
                                            tded = ((TypePointer)e.targ.value).next.value;
                                            tded = ((TypeFunction)tded).next.value;
                                        }
                                        else
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        break;
                                    case 209:
                                        tded = target.toArgTypes(e.targ.value);
                                        if (tded == null)
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        break;
                                    case 229:
                                        if (((e.targ.value.ty.value & 0xFF) != ENUMTY.Tvector))
                                            /*goto Lno*/throw Dispatch1.INSTANCE;
                                        tded = ((TypeVector)e.targ.value).basetype.value;
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
                    else if ((e.tspec != null) && (e.id == null) && !((e.parameters != null) && ((e.parameters.get()).length.value != 0)))
                    {
                        e.tspec = typeSemantic(e.tspec, e.loc.value, this.sc.value);
                        if (((e.tok & 0xFF) == 7))
                        {
                            if (e.targ.value.implicitConvTo(e.tspec) != 0)
                                /*goto Lyes*/throw Dispatch0.INSTANCE;
                            else
                                /*goto Lno*/throw Dispatch1.INSTANCE;
                        }
                        else
                        {
                            if (e.targ.value.equals(e.tspec))
                                /*goto Lyes*/throw Dispatch0.INSTANCE;
                            else
                                /*goto Lno*/throw Dispatch1.INSTANCE;
                        }
                    }
                    else if (e.tspec != null)
                    {
                        Identifier tid = e.id != null ? e.id : Identifier.generateId(new BytePtr("__isexp_id"));
                        (e.parameters.get()).insert(0, new TemplateTypeParameter(e.loc.value, tid, null, null));
                        Ref<DArray<RootObject>> dedtypes = ref(dedtypes.value = new DArray<RootObject>((e.parameters.get()).length.value));
                        try {
                            dedtypes.value.zero();
                            int m = deduceType(e.targ.value, this.sc.value, e.tspec, e.parameters, ptr(dedtypes), null, 0, (e.tok & 0xFF) == 58);
                            if ((m <= MATCH.nomatch) || (m != MATCH.exact) && ((e.tok & 0xFF) == 58))
                            {
                                /*goto Lno*/throw Dispatch1.INSTANCE;
                            }
                            else
                            {
                                tded = (Type)dedtypes.value.get(0);
                                if (tded == null)
                                    tded = e.targ.value;
                                Ref<DArray<RootObject>> tiargs = ref(tiargs.value = new DArray<RootObject>(1));
                                try {
                                    tiargs.value.set(0, e.targ.value);
                                    {
                                        int i = 1;
                                    L_outer7:
                                        for (; (i < (e.parameters.get()).length.value);i++){
                                            TemplateParameter tp = (e.parameters.get()).get(i);
                                            Ref<Declaration> s = ref(null);
                                            m = tp.matchArg(e.loc.value, this.sc.value, ptr(tiargs), i, e.parameters, ptr(dedtypes), ptr(s));
                                            if ((m <= MATCH.nomatch))
                                                /*goto Lno*/throw Dispatch1.INSTANCE;
                                            dsymbolSemantic(s.value, this.sc.value);
                                            if ((this.sc.value.get()).insert(s.value) == null)
                                                e.error(new BytePtr("declaration `%s` is already defined"), s.value.toChars());
                                            unSpeculative(this.sc.value, s.value);
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
                        tded = e.targ.value;
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
                        s = new TupleDeclaration(e.loc.value, e.id, ptr(tup.objects));
                    else
                        s = new AliasDeclaration(e.loc.value, e.id, tded);
                    dsymbolSemantic(s, this.sc.value);
                    if ((tup == null) && ((this.sc.value.get()).insert(s) == null))
                        e.error(new BytePtr("declaration `%s` is already defined"), s.toChars());
                    unSpeculative(this.sc.value, s);
                }
                this.result.value = new IntegerExp(e.loc.value, 1L, Type.tbool.value);
                return ;
            }
            catch(Dispatch1 __d){}
        /*Lno:*/
            this.result.value = new IntegerExp(e.loc.value, 0L, Type.tbool.value);
        }

        public  void visit(BinAssignExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            if (((exp.e1.value.op.value & 0xFF) == 32))
            {
                e = rewriteOpAssign(exp);
                e = expressionSemantic(e, this.sc.value);
                this.result.value = e;
                return ;
            }
            if (((exp.e1.value.op.value & 0xFF) == 31) || ((exp.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tarray) || ((exp.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                if (checkNonAssignmentArrayOp(exp.e1.value, false))
                    this.setError();
                    return ;
                if (((exp.e1.value.op.value & 0xFF) == 31))
                    ((SliceExp)exp.e1.value).arrayop = true;
                if (exp.e2.value.implicitConvTo(exp.e1.value.type.value.nextOf()) != 0)
                {
                    exp.e2.value = exp.e2.value.castTo(this.sc.value, exp.e1.value.type.value.nextOf());
                }
                else {
                    Expression ex = typeCombine(exp, this.sc.value);
                    if ((ex) != null)
                    {
                        this.result.value = ex;
                        return ;
                    }
                }
                exp.type.value = exp.e1.value.type.value;
                this.result.value = arrayOp(exp, this.sc.value);
                return ;
            }
            exp.e1.value = expressionSemantic(exp.e1.value, this.sc.value);
            exp.e1.value = exp.e1.value.optimize(0, false);
            exp.e1.value = exp.e1.value.modifiableLvalue(this.sc.value, exp.e1.value);
            exp.type.value = exp.e1.value.type.value;
            {
                AggregateDeclaration ad = isAggregate(exp.e1.value.type.value);
                if ((ad) != null)
                {
                    {
                        Dsymbol s = search_function(ad, Id.opOpAssign.value);
                        if ((s) != null)
                        {
                            error(exp.loc.value, new BytePtr("none of the `opOpAssign` overloads of `%s` are callable for `%s` of type `%s`"), ad.toChars(), exp.e1.value.toChars(), exp.e1.value.type.value.toChars());
                            this.setError();
                            return ;
                        }
                    }
                }
            }
            if (exp.e1.value.checkScalar() || exp.e1.value.checkReadModifyWrite(exp.op.value, exp.e2.value))
                this.setError();
                return ;
            int arith = ((((exp.op.value & 0xFF) == 76) || ((exp.op.value & 0xFF) == 77) || ((exp.op.value & 0xFF) == 81) || ((exp.op.value & 0xFF) == 82) || ((exp.op.value & 0xFF) == 83) || ((exp.op.value & 0xFF) == 227)) ? 1 : 0);
            int bitwise = ((((exp.op.value & 0xFF) == 87) || ((exp.op.value & 0xFF) == 88) || ((exp.op.value & 0xFF) == 89)) ? 1 : 0);
            int shift = ((((exp.op.value & 0xFF) == 66) || ((exp.op.value & 0xFF) == 67) || ((exp.op.value & 0xFF) == 69)) ? 1 : 0);
            if ((bitwise != 0) && ((exp.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tbool))
                exp.e2.value = exp.e2.value.implicitCastTo(this.sc.value, exp.type.value);
            else if (exp.checkNoBool())
                this.setError();
                return ;
            if (((exp.op.value & 0xFF) == 76) || ((exp.op.value & 0xFF) == 77) && ((exp.e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tpointer) && exp.e2.value.type.value.toBasetype().isintegral())
            {
                this.result.value = scaleFactor(exp, this.sc.value);
                return ;
            }
            {
                Expression ex = typeCombine(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            if ((arith != 0) && exp.checkArithmeticBin())
                this.setError();
                return ;
            if ((bitwise != 0) || (shift != 0) && exp.checkIntegralBin())
                this.setError();
                return ;
            if (shift != 0)
            {
                if (((exp.e2.value.type.value.toBasetype().ty.value & 0xFF) != ENUMTY.Tvector))
                    exp.e2.value = exp.e2.value.castTo(this.sc.value, Type.tshiftcnt.value);
            }
            if (!target.isVectorOpSupported(exp.type.value.toBasetype(), exp.op.value, exp.e2.value.type.value.toBasetype()))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            if (((exp.e1.value.op.value & 0xFF) == 127) || ((exp.e2.value.op.value & 0xFF) == 127))
                this.setError();
                return ;
            e = exp.checkOpAssignTypes(this.sc.value);
            if (((e.op.value & 0xFF) == 127))
            {
                this.result.value = e;
                return ;
            }
            assert(((e.op.value & 0xFF) == 90) || (pequals(e, exp)));
            this.result.value = ((BinExp)e).reorderSettingAAElem(this.sc.value);
        }

        public  Expression compileIt(CompileExp exp) {
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                if (expressionsToString(buf, this.sc.value, exp.exps))
                    return null;
                int errors = global.errors.value;
                int len = buf.value.offset.value;
                ByteSlice str = buf.value.extractChars().slice(0,len).copy();
                StderrDiagnosticReporter diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
                try {
                    ParserASTCodegen p = new ParserASTCodegen(exp.loc.value, (this.sc.value.get())._module.value, str, false, diagnosticReporter);
                    try {
                        p.nextToken();
                        Expression e = p.parseExpression();
                        if (p.errors())
                        {
                            assert((global.errors.value != errors));
                            return null;
                        }
                        if (((p.token.value.value & 0xFF) != 11))
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
            if (e == null)
                this.setError();
                return ;
            this.result.value = expressionSemantic(e, this.sc.value);
        }

        public  void visit(ImportExp e) {
            StringExp se = semanticString(this.sc.value, e.e1.value, new BytePtr("file name argument"));
            if (se == null)
                this.setError();
                return ;
            se = se.toUTF8(this.sc.value);
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
            (this.sc.value.get())._module.value.contentImportedFiles.push(name);
            if (global.params.verbose)
                message(new BytePtr("file      %.*s\u0009(%s)"), se.len.value, se.string.value, name);
            if ((global.params.moduleDeps != null))
            {
                Ptr<OutBuffer> ob = global.params.moduleDeps;
                dmodule.Module imod = (this.sc.value.get()).instantiatingModule();
                if (global.params.moduleDepsFile.getLength() == 0)
                    (ob.get()).writestring(new ByteSlice("depsFile "));
                (ob.get()).writestring(imod.toPrettyChars(false));
                (ob.get()).writestring(new ByteSlice(" ("));
                escapePath(ob, imod.srcfile.toChars());
                (ob.get()).writestring(new ByteSlice(") : "));
                if (global.params.moduleDepsFile.getLength() != 0)
                    (ob.get()).writestring(new ByteSlice("string : "));
                (ob.get()).write(se.string.value, se.len.value);
                (ob.get()).writestring(new ByteSlice(" ("));
                escapePath(ob, name);
                (ob.get()).writestring(new ByteSlice(")"));
                (ob.get()).writenl();
            }
            {
                File.ReadResult readResult = File.read(name).copy();
                try {
                    if (!readResult.success)
                    {
                        e.error(new BytePtr("cannot read file `%s`"), name);
                        this.setError();
                        return ;
                    }
                    else
                    {
                        ByteSlice data = readResult.extractData().copy();
                        se = new StringExp(e.loc.value, toBytePtr(data), data.getLength());
                    }
                }
                finally {
                }
            }
            this.result.value = expressionSemantic(se, this.sc.value);
        }

        public  void visit(AssertExp exp) {
            BytePtr assertExpMsg = pcopy(exp.msg != null ? null : exp.toChars());
            {
                Expression ex = unaSemantic(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            exp.e1.value = resolveProperties(this.sc.value, exp.e1.value);
            exp.e1.value = exp.e1.value.optimize(0, false);
            exp.e1.value = exp.e1.value.toBoolean(this.sc.value);
            if ((exp.msg == null) && ((global.params.checkAction & 0xFF) == 3))
            {
                byte tok = exp.e1.value.op.value;
                boolean isEqualsCallExpression = false;
                if (((tok & 0xFF) == 18))
                {
                    CallExp callExp = (CallExp)exp.e1.value;
                    Identifier callExpIdent = callExp.f.value.ident.value;
                    isEqualsCallExpression = (pequals(callExpIdent, Id.__equals)) || (pequals(callExpIdent, Id.eq.value));
                }
                if (((tok & 0xFF) == 58) || ((tok & 0xFF) == 59) || ((tok & 0xFF) == 54) || ((tok & 0xFF) == 55) || ((tok & 0xFF) == 56) || ((tok & 0xFF) == 57) || ((tok & 0xFF) == 60) || ((tok & 0xFF) == 61) || ((tok & 0xFF) == 175) || isEqualsCallExpression)
                {
                    if (!verifyHookExist(exp.loc.value, this.sc.value.get(), Id._d_assert_fail, new ByteSlice("generating assert messages"), Id.object.value))
                        this.setError();
                        return ;
                    Ptr<DArray<Expression>> es = refPtr(new DArray<Expression>(2));
                    Ptr<DArray<RootObject>> tiargs = refPtr(new DArray<RootObject>(3));
                    Loc loc = exp.e1.value.loc.value.copy();
                    if (isEqualsCallExpression)
                    {
                        CallExp callExp = (CallExp)exp.e1.value;
                        Ptr<DArray<Expression>> args = callExp.arguments.value;
                        Expression comp = new StringExp(loc, toBytePtr(toBytePtr(expressionsem.visitcompMsg)));
                        comp = expressionSemantic(comp, this.sc.value);
                        tiargs.get().set(0, comp);
                        tiargs.get().set(1, (args.get()).get(0).type.value);
                        tiargs.get().set(2, (args.get()).get(1).type.value);
                        es.get().set(0, (args.get()).get(0));
                        es.get().set(1, (args.get()).get(1));
                    }
                    else
                    {
                        EqualExp binExp = (EqualExp)exp.e1.value;
                        Expression comp = new StringExp(loc, Token.toChars(exp.e1.value.op.value));
                        comp = expressionSemantic(comp, this.sc.value);
                        tiargs.get().set(0, comp);
                        tiargs.get().set(1, binExp.e1.value.type.value);
                        tiargs.get().set(2, binExp.e2.value.type.value);
                        es.get().set(0, binExp.e1.value);
                        es.get().set(1, binExp.e2.value);
                    }
                    Expression __assertFail = new IdentifierExp(exp.loc.value, Id.empty.value);
                    DotIdExp assertFail = new DotIdExp(loc, __assertFail, Id.object.value);
                    DotTemplateInstanceExp dt = new DotTemplateInstanceExp(loc, assertFail, Id._d_assert_fail, tiargs);
                    CallExp ec = CallExp.create(Loc.initial.value, (Expression)dt, es);
                    exp.msg = ec;
                }
                else
                {
                    OutBuffer buf = new OutBuffer();
                    try {
                        buf.printf(new BytePtr("%s failed"), assertExpMsg);
                        exp.msg = new StringExp(Loc.initial.value, buf.extractChars());
                    }
                    finally {
                    }
                }
            }
            if (exp.msg != null)
            {
                exp.msg = expressionSemantic(exp.msg, this.sc.value);
                exp.msg = resolveProperties(this.sc.value, exp.msg);
                exp.msg = exp.msg.implicitCastTo(this.sc.value, Type.tchar.constOf().arrayOf());
                exp.msg = exp.msg.optimize(0, false);
            }
            if (((exp.e1.value.op.value & 0xFF) == 127))
            {
                this.result.value = exp.e1.value;
                return ;
            }
            if ((exp.msg != null) && ((exp.msg.op.value & 0xFF) == 127))
            {
                this.result.value = exp.msg;
                return ;
            }
            boolean f1 = checkNonAssignmentArrayOp(exp.e1.value, false);
            boolean f2 = (exp.msg != null) && checkNonAssignmentArrayOp(exp.msg, false);
            if (f1 || f2)
                this.setError();
                return ;
            if (exp.e1.value.isBool(false))
            {
                FuncDeclaration fd = (this.sc.value.get()).parent.value.isFuncDeclaration();
                if (fd != null)
                    fd.hasReturnExp |= 4;
                (this.sc.value.get()).ctorflow.orCSX(CSX.halt);
                if (((global.params.useAssert & 0xFF) == 1))
                {
                    Expression e = new HaltExp(exp.loc.value);
                    e = expressionSemantic(e, this.sc.value);
                    this.result.value = e;
                    return ;
                }
            }
            exp.type.value = Type.tvoid.value;
            this.result.value = exp;
        }

        public  void visit(DotIdExp exp) {
            Expression e = semanticY(exp, this.sc.value, 1);
            if ((e != null) && isDotOpDispatch(e))
            {
                int errors = global.startGagging();
                e = resolvePropertiesX(this.sc.value, e, null);
                if (global.endGagging(errors))
                    e = null;
                else
                {
                    this.result.value = e;
                    return ;
                }
            }
            if (e == null)
            {
                e = resolveUFCSProperties(this.sc.value, exp, null);
            }
            this.result.value = e;
        }

        public  void visit(DotTemplateExp e) {
            {
                Expression ex = unaSemantic(e, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            this.result.value = e;
        }

        public  void visit(DotVarExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            exp.var.value = exp.var.value.toAlias().isDeclaration();
            exp.e1.value = expressionSemantic(exp.e1.value, this.sc.value);
            {
                TupleDeclaration tup = exp.var.value.isTupleDeclaration();
                if ((tup) != null)
                {
                    Ref<Expression> e0 = ref(null);
                    Expression ev = (this.sc.value.get()).func.value != null ? extractSideEffect(this.sc.value, new BytePtr("__tup"), e0, exp.e1.value, false) : exp.e1.value;
                    Ptr<DArray<Expression>> exps = refPtr(new DArray<Expression>());
                    (exps.get()).reserve((tup.objects.value.get()).length.value);
                    {
                        int i = 0;
                        for (; (i < (tup.objects.value.get()).length.value);i++){
                            RootObject o = (tup.objects.value.get()).get(i);
                            Expression e = null;
                            if ((o.dyncast() == DYNCAST.expression))
                            {
                                e = (Expression)o;
                                if (((e.op.value & 0xFF) == 41))
                                {
                                    Dsymbol s = ((DsymbolExp)e).s.value;
                                    e = new DotVarExp(exp.loc.value, ev, s.isDeclaration(), true);
                                }
                            }
                            else if ((o.dyncast() == DYNCAST.dsymbol))
                            {
                                e = new DsymbolExp(exp.loc.value, (Dsymbol)o, true);
                            }
                            else if ((o.dyncast() == DYNCAST.type))
                            {
                                e = new TypeExp(exp.loc.value, (Type)o);
                            }
                            else
                            {
                                exp.error(new BytePtr("`%s` is not an expression"), o.toChars());
                                this.setError();
                                return ;
                            }
                            (exps.get()).push(e);
                        }
                    }
                    Expression e = new TupleExp(exp.loc.value, e0.value, exps);
                    e = expressionSemantic(e, this.sc.value);
                    this.result.value = e;
                    return ;
                }
            }
            exp.e1.value = exp.e1.value.addDtorHook(this.sc.value);
            Type t1 = exp.e1.value.type.value;
            {
                FuncDeclaration fd = exp.var.value.isFuncDeclaration();
                if ((fd) != null)
                {
                    if (!fd.functionSemantic())
                        this.setError();
                        return ;
                    if (fd.isNested() && (fd.isThis() == null) || (fd.isFuncLiteralDeclaration() != null))
                    {
                        Expression e = symbolToExp(fd, exp.loc.value, this.sc.value, false);
                        this.result.value = Expression.combine(exp.e1.value, e);
                        return ;
                    }
                    exp.type.value = fd.type.value;
                    assert(exp.type.value != null);
                }
                else {
                    OverDeclaration od = exp.var.value.isOverDeclaration();
                    if ((od) != null)
                    {
                        exp.type.value = Type.tvoid.value;
                    }
                    else
                    {
                        exp.type.value = exp.var.value.type.value;
                        if ((exp.type.value == null) && (global.errors.value != 0))
                            this.setError();
                            return ;
                        assert(exp.type.value != null);
                        if (((t1.ty.value & 0xFF) == ENUMTY.Tpointer))
                            t1 = t1.nextOf();
                        exp.type.value = exp.type.value.addMod(t1.mod.value);
                        Dsymbol vparent = exp.var.value.toParent();
                        AggregateDeclaration ad = vparent != null ? vparent.isAggregateDeclaration() : null;
                        {
                            Expression e1x = getRightThis(exp.loc.value, this.sc.value, ad, exp.e1.value, exp.var.value, 1);
                            if ((e1x) != null)
                                exp.e1.value = e1x;
                            else
                            {
                                Expression e = new VarExp(exp.loc.value, exp.var.value, true);
                                e = expressionSemantic(e, this.sc.value);
                                this.result.value = e;
                                return ;
                            }
                        }
                        checkAccess(exp.loc.value, this.sc.value, exp.e1.value, exp.var.value);
                        VarDeclaration v = exp.var.value.isVarDeclaration();
                        if ((v != null) && v.isDataseg() || ((v.storage_class.value & 8388608L) != 0))
                        {
                            Expression e = expandVar(0, v);
                            if (e != null)
                            {
                                this.result.value = e;
                                return ;
                            }
                        }
                        if ((v != null) && v.isDataseg() || !v.needThis())
                        {
                            checkAccess(exp.loc.value, this.sc.value, exp.e1.value, (Declaration)v);
                            Expression e = new VarExp(exp.loc.value, v, true);
                            e = new CommaExp(exp.loc.value, exp.e1.value, e, true);
                            e = expressionSemantic(e, this.sc.value);
                            this.result.value = e;
                            return ;
                        }
                    }
                }
            }
            this.result.value = exp;
        }

        public  void visit(DotTemplateInstanceExp exp) {
            Expression e = semanticY(exp, this.sc.value, 1);
            if (e == null)
                e = resolveUFCSProperties(this.sc.value, exp, null);
            this.result.value = e;
        }

        public  void visit(DelegateExp e) {
            if (e.type.value != null)
            {
                this.result.value = e;
                return ;
            }
            e.e1.value = expressionSemantic(e.e1.value, this.sc.value);
            e.type.value = new TypeDelegate(e.func.value.type.value);
            e.type.value = typeSemantic(e.type.value, e.loc.value, this.sc.value);
            FuncDeclaration f = e.func.value.toAliasFunc();
            AggregateDeclaration ad = f.toParentLocal().isAggregateDeclaration();
            if (f.needThis())
                e.e1.value = getRightThis(e.loc.value, this.sc.value, ad, e.e1.value, f, 0);
            if (global.params.vsafe.value && ((e.e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tstruct))
            {
                {
                    VarDeclaration v = expToVariable(e.e1.value);
                    if ((v) != null)
                    {
                        if (!checkAddressVar(this.sc.value, e, v))
                            this.setError();
                            return ;
                    }
                }
            }
            if (((f.type.value.ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                TypeFunction tf = (TypeFunction)f.type.value;
                if (MODmethodConv(e.e1.value.type.value.mod.value, f.type.value.mod.value) == 0)
                {
                    Ref<OutBuffer> thisBuf = ref(new OutBuffer());
                    try {
                        Ref<OutBuffer> funcBuf = ref(new OutBuffer());
                        try {
                            MODMatchToBuffer(ptr(thisBuf), e.e1.value.type.value.mod.value, tf.mod.value);
                            MODMatchToBuffer(ptr(funcBuf), tf.mod.value, e.e1.value.type.value.mod.value);
                            e.error(new BytePtr("%smethod `%s` is not callable using a %s`%s`"), funcBuf.value.peekChars(), f.toPrettyChars(false), thisBuf.value.peekChars(), e.e1.value.toChars());
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
            if ((ad != null) && (ad.isClassDeclaration() != null) && (!pequals(ad.type.value, e.e1.value.type.value)))
            {
                e.e1.value = new CastExp(e.loc.value, e.e1.value, ad.type.value);
                e.e1.value = expressionSemantic(e.e1.value, this.sc.value);
            }
            this.result.value = e;
            if (f.isThis2.value && ((this.sc.value.get()).intypeof.value == 0) && ((this.sc.value.get()).func.value != null))
            {
                {
                    AggregateDeclaration ad2 = f.isMember2();
                    if ((ad2) != null)
                    {
                        Expression te = expressionSemantic(new ThisExp(e.loc.value), this.sc.value);
                        if (((te.op.value & 0xFF) != 127))
                            te = getRightThis(e.loc.value, this.sc.value, ad2, te, f, 0);
                        if (((te.op.value & 0xFF) == 127))
                        {
                            e.error(new BytePtr("need `this` of type `%s` to make delegate from function `%s`"), ad2.toChars(), f.toChars());
                            this.setError();
                            return ;
                        }
                    }
                }
                VarDeclaration vthis2 = makeThis2Argument(e.loc.value, this.sc.value, f);
                e.vthis2.value = vthis2;
                Expression de = new DeclarationExp(e.loc.value, vthis2);
                this.result.value = Expression.combine(de, this.result.value);
                this.result.value = expressionSemantic(this.result.value, this.sc.value);
            }
        }

        public  void visit(DotTypeExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            {
                Expression e = unaSemantic(exp, this.sc.value);
                if ((e) != null)
                {
                    this.result.value = e;
                    return ;
                }
            }
            exp.type.value = exp.sym.value.getType().addMod(exp.e1.value.type.value.mod.value);
            this.result.value = exp;
        }

        public  void visit(AddrExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            {
                Expression ex = unaSemantic(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            int wasCond = (((exp.e1.value.op.value & 0xFF) == 100) ? 1 : 0);
            if (((exp.e1.value.op.value & 0xFF) == 29))
            {
                DotTemplateInstanceExp dti = (DotTemplateInstanceExp)exp.e1.value;
                TemplateInstance ti = dti.ti;
                {
                    dsymbolSemantic(ti, this.sc.value);
                    if ((ti.inst.value == null) || ti.errors.value)
                        this.setError();
                        return ;
                    Dsymbol s = ti.toAlias();
                    FuncDeclaration f = s.isFuncDeclaration();
                    if (f != null)
                    {
                        exp.e1.value = new DotVarExp(exp.e1.value.loc.value, dti.e1.value, f, true);
                        exp.e1.value = expressionSemantic(exp.e1.value, this.sc.value);
                    }
                }
            }
            else if (((exp.e1.value.op.value & 0xFF) == 203))
            {
                TemplateInstance ti = ((ScopeExp)exp.e1.value).sds.value.isTemplateInstance();
                if (ti != null)
                {
                    dsymbolSemantic(ti, this.sc.value);
                    if ((ti.inst.value == null) || ti.errors.value)
                        this.setError();
                        return ;
                    Dsymbol s = ti.toAlias();
                    FuncDeclaration f = s.isFuncDeclaration();
                    if (f != null)
                    {
                        exp.e1.value = new VarExp(exp.e1.value.loc.value, f, true);
                        exp.e1.value = expressionSemantic(exp.e1.value, this.sc.value);
                    }
                }
            }
            exp.e1.value = exp.e1.value.toLvalue(this.sc.value, null);
            if (((exp.e1.value.op.value & 0xFF) == 127))
            {
                this.result.value = exp.e1.value;
                return ;
            }
            if (checkNonAssignmentArrayOp(exp.e1.value, false))
                this.setError();
                return ;
            if (exp.e1.value.type.value == null)
            {
                exp.error(new BytePtr("cannot take address of `%s`"), exp.e1.value.toChars());
                this.setError();
                return ;
            }
            Ref<Boolean> hasOverloads = ref(false);
            {
                FuncDeclaration f = isFuncAddress(exp, ptr(hasOverloads));
                if ((f) != null)
                {
                    if (!hasOverloads.value && f.checkForwardRef(exp.loc.value))
                        this.setError();
                        return ;
                }
                else if (exp.e1.value.type.value.deco.value == null)
                {
                    if (((exp.e1.value.op.value & 0xFF) == 26))
                    {
                        VarExp ve = (VarExp)exp.e1.value;
                        Declaration d = ve.var.value;
                        exp.error(new BytePtr("forward reference to %s `%s`"), d.kind(), d.toChars());
                    }
                    else
                        exp.error(new BytePtr("forward reference to `%s`"), exp.e1.value.toChars());
                    this.setError();
                    return ;
                }
            }
            exp.type.value = exp.e1.value.type.value.pointerTo();
            if (((exp.e1.value.op.value & 0xFF) == 27))
            {
                DotVarExp dve = (DotVarExp)exp.e1.value;
                FuncDeclaration f = dve.var.value.isFuncDeclaration();
                if (f != null)
                {
                    f = f.toAliasFunc();
                    if (!dve.hasOverloads)
                        f.tookAddressOf.value++;
                    Expression e = null;
                    if (f.needThis())
                        e = new DelegateExp(exp.loc.value, dve.e1.value, f, dve.hasOverloads, null);
                    else
                        e = new CommaExp(exp.loc.value, dve.e1.value, new AddrExp(exp.loc.value, new VarExp(exp.loc.value, f, dve.hasOverloads)), true);
                    e = expressionSemantic(e, this.sc.value);
                    this.result.value = e;
                    return ;
                }
                if (checkUnsafeAccess(this.sc.value, dve, !exp.type.value.isMutable(), true))
                    this.setError();
                    return ;
                if (global.params.vsafe.value)
                {
                    {
                        VarDeclaration v = expToVariable(dve.e1.value);
                        if ((v) != null)
                        {
                            if (!checkAddressVar(this.sc.value, exp, v))
                                this.setError();
                                return ;
                        }
                    }
                }
            }
            else if (((exp.e1.value.op.value & 0xFF) == 26))
            {
                VarExp ve = (VarExp)exp.e1.value;
                VarDeclaration v = ve.var.value.isVarDeclaration();
                if (v != null)
                {
                    if (!checkAddressVar(this.sc.value, exp, v))
                        this.setError();
                        return ;
                    ve.checkPurity(this.sc.value, v);
                }
                FuncDeclaration f = ve.var.value.isFuncDeclaration();
                if (f != null)
                {
                    if (!ve.hasOverloads.value || f.isNested() && !f.needThis())
                        f.tookAddressOf.value++;
                    if (f.isNested() && !f.needThis())
                    {
                        if (f.isFuncLiteralDeclaration() != null)
                        {
                            if (!f.isNested())
                            {
                                Expression e = new DelegateExp(exp.loc.value, new NullExp(exp.loc.value, Type.tnull), f, ve.hasOverloads.value, null);
                                e = expressionSemantic(e, this.sc.value);
                                this.result.value = e;
                                return ;
                            }
                        }
                        Expression e = new DelegateExp(exp.loc.value, exp.e1.value, f, ve.hasOverloads.value, null);
                        e = expressionSemantic(e, this.sc.value);
                        this.result.value = e;
                        return ;
                    }
                    if (f.needThis())
                    {
                        if (hasThis(this.sc.value) != null)
                        {
                            Expression ethis = new ThisExp(exp.loc.value);
                            Expression e = new DelegateExp(exp.loc.value, ethis, f, ve.hasOverloads.value, null);
                            e = expressionSemantic(e, this.sc.value);
                            this.result.value = e;
                            return ;
                        }
                        if (((this.sc.value.get()).func.value != null) && ((this.sc.value.get()).intypeof.value == 0))
                        {
                            if ((this.sc.value.get()).func.value.setUnsafe() && (((this.sc.value.get()).flags.value & 8) == 0))
                            {
                                exp.error(new BytePtr("`this` reference necessary to take address of member `%s` in `@safe` function `%s`"), f.toChars(), (this.sc.value.get()).func.value.toChars());
                            }
                        }
                    }
                }
            }
            else if (((exp.e1.value.op.value & 0xFF) == 123) || ((exp.e1.value.op.value & 0xFF) == 124) && global.params.vsafe.value)
            {
                {
                    VarDeclaration v = expToVariable(exp.e1.value);
                    if ((v) != null)
                    {
                        if (!checkAddressVar(this.sc.value, exp, v))
                            this.setError();
                            return ;
                    }
                }
            }
            else if (((exp.e1.value.op.value & 0xFF) == 18))
            {
                CallExp ce = (CallExp)exp.e1.value;
                if (((ce.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tfunction))
                {
                    TypeFunction tf = (TypeFunction)ce.e1.value.type.value;
                    if (tf.isref.value && ((this.sc.value.get()).func.value != null) && ((this.sc.value.get()).intypeof.value == 0) && (this.sc.value.get()).func.value.setUnsafe() && (((this.sc.value.get()).flags.value & 8) == 0))
                    {
                        exp.error(new BytePtr("cannot take address of `ref return` of `%s()` in `@safe` function `%s`"), ce.e1.value.toChars(), (this.sc.value.get()).func.value.toChars());
                    }
                }
            }
            else if (((exp.e1.value.op.value & 0xFF) == 62))
            {
                {
                    VarDeclaration v = expToVariable(exp.e1.value);
                    if ((v) != null)
                    {
                        if (global.params.vsafe.value && !checkAddressVar(this.sc.value, exp, v))
                            this.setError();
                            return ;
                        exp.e1.value.checkPurity(this.sc.value, v);
                    }
                }
            }
            else if (wasCond != 0)
            {
                assert(((exp.e1.value.op.value & 0xFF) == 24));
                PtrExp pe = (PtrExp)exp.e1.value;
                assert(((pe.e1.value.op.value & 0xFF) == 100));
                CondExp ce = (CondExp)pe.e1.value;
                assert(((ce.e1.value.op.value & 0xFF) == 19));
                assert(((ce.e2.value.op.value & 0xFF) == 19));
                ce.e1.value.type.value = null;
                ce.e1.value = expressionSemantic(ce.e1.value, this.sc.value);
                ce.e2.value.type.value = null;
                ce.e2.value = expressionSemantic(ce.e2.value, this.sc.value);
            }
            this.result.value = exp.optimize(0, false);
        }

        public  void visit(PtrExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            Type tb = exp.e1.value.type.value.toBasetype();
            {
                int __dispatch6 = 0;
                dispatched_6:
                do {
                    switch (__dispatch6 != 0 ? __dispatch6 : (tb.ty.value & 0xFF))
                    {
                        case 3:
                            exp.type.value = ((TypePointer)tb).next.value;
                            break;
                        case 1:
                        case 0:
                            if (isNonAssignmentArrayOp(exp.e1.value))
                                /*goto default*/ { __dispatch6 = -1; continue dispatched_6; }
                            exp.error(new BytePtr("using `*` on an array is no longer supported; use `*(%s).ptr` instead"), exp.e1.value.toChars());
                            exp.type.value = ((TypeArray)tb).next.value;
                            exp.e1.value = exp.e1.value.castTo(this.sc.value, exp.type.value.pointerTo());
                            break;
                        case 34:
                            __dispatch6 = 0;
                            this.setError();
                            return ;
                        default:
                        __dispatch6 = 0;
                        exp.error(new BytePtr("can only `*` a pointer, not a `%s`"), exp.e1.value.type.value.toChars());
                        /*goto case*/{ __dispatch6 = 34; continue dispatched_6; }
                    }
                } while(__dispatch6 != 0);
            }
            if (exp.checkValue())
                this.setError();
                return ;
            this.result.value = exp;
        }

        public  void visit(NegExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            fix16997(this.sc.value, exp);
            exp.type.value = exp.e1.value.type.value;
            Type tb = exp.type.value.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                if (!isArrayOpValid(exp.e1.value))
                {
                    this.result.value = arrayOpInvalidError(exp);
                    return ;
                }
                this.result.value = exp;
                return ;
            }
            if (!target.isVectorOpSupported(tb, exp.op.value, null))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            if (exp.e1.value.checkNoBool())
                this.setError();
                return ;
            if (exp.e1.value.checkArithmetic())
                this.setError();
                return ;
            this.result.value = exp;
        }

        public  void visit(UAddExp exp) {
            assert(exp.type.value == null);
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            fix16997(this.sc.value, exp);
            if (!target.isVectorOpSupported(exp.e1.value.type.value.toBasetype(), exp.op.value, null))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            if (exp.e1.value.checkNoBool())
                this.setError();
                return ;
            if (exp.e1.value.checkArithmetic())
                this.setError();
                return ;
            this.result.value = exp.e1.value;
        }

        public  void visit(ComExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            fix16997(this.sc.value, exp);
            exp.type.value = exp.e1.value.type.value;
            Type tb = exp.type.value.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                if (!isArrayOpValid(exp.e1.value))
                {
                    this.result.value = arrayOpInvalidError(exp);
                    return ;
                }
                this.result.value = exp;
                return ;
            }
            if (!target.isVectorOpSupported(tb, exp.op.value, null))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            if (exp.e1.value.checkNoBool())
                this.setError();
                return ;
            if (exp.e1.value.checkIntegral())
                this.setError();
                return ;
            this.result.value = exp;
        }

        public  void visit(NotExp e) {
            if (e.type.value != null)
            {
                this.result.value = e;
                return ;
            }
            e.setNoderefOperand();
            {
                Expression ex = unaSemantic(e, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            if (((e.e1.value.op.value & 0xFF) == 20))
                e.e1.value = resolveAliasThis(this.sc.value, e.e1.value, false);
            e.e1.value = resolveProperties(this.sc.value, e.e1.value);
            e.e1.value = e.e1.value.toBoolean(this.sc.value);
            if ((pequals(e.e1.value.type.value, Type.terror.value)))
            {
                this.result.value = e.e1.value;
                return ;
            }
            if (!target.isVectorOpSupported(e.e1.value.type.value.toBasetype(), e.op.value, null))
            {
                this.result.value = e.incompatibleTypes();
            }
            if (checkNonAssignmentArrayOp(e.e1.value, false))
                this.setError();
                return ;
            e.type.value = Type.tbool.value;
            this.result.value = e;
        }

        public  void visit(DeleteExp exp) {
            if (!(this.sc.value.get()).isDeprecated())
            {
                if (!exp.isRAII)
                    deprecation(exp.loc.value, new BytePtr("The `delete` keyword has been deprecated.  Use `object.destroy()` (and `core.memory.GC.free()` if applicable) instead."));
            }
            {
                Expression ex = unaSemantic(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            exp.e1.value = resolveProperties(this.sc.value, exp.e1.value);
            exp.e1.value = exp.e1.value.modifiableLvalue(this.sc.value, null);
            if (((exp.e1.value.op.value & 0xFF) == 127))
            {
                this.result.value = exp.e1.value;
                return ;
            }
            exp.type.value = Type.tvoid.value;
            AggregateDeclaration ad = null;
            Type tb = exp.e1.value.type.value.toBasetype();
            switch ((tb.ty.value & 0xFF))
            {
                case 7:
                    ClassDeclaration cd = ((TypeClass)tb).sym.value;
                    if (cd.isCOMinterface())
                    {
                        exp.error(new BytePtr("cannot `delete` instance of COM interface `%s`"), cd.toChars());
                        this.setError();
                        return ;
                    }
                    ad = cd;
                    break;
                case 3:
                    tb = ((TypePointer)tb).next.value.toBasetype();
                    if (((tb.ty.value & 0xFF) == ENUMTY.Tstruct))
                    {
                        ad = ((TypeStruct)tb).sym.value;
                        DeleteDeclaration f = ad.aggDelete.value;
                        DtorDeclaration fd = ad.dtor.value;
                        if (f == null)
                        {
                            semanticTypeInfo(this.sc.value, tb);
                            break;
                        }
                        Expression ea = null;
                        Expression eb = null;
                        Expression ec = null;
                        VarDeclaration v = null;
                        if ((fd != null) && (f != null))
                        {
                            v = copyToTemp(0L, new BytePtr("__tmpea"), exp.e1.value);
                            dsymbolSemantic(v, this.sc.value);
                            ea = new DeclarationExp(exp.loc.value, v);
                            ea.type.value = v.type.value;
                        }
                        if (fd != null)
                        {
                            Expression e_1 = ea != null ? new VarExp(exp.loc.value, v, true) : exp.e1.value;
                            e_1 = new DotVarExp(Loc.initial.value, e_1, fd, false);
                            eb = new CallExp(exp.loc.value, e_1);
                            eb = expressionSemantic(eb, this.sc.value);
                        }
                        if (f != null)
                        {
                            Type tpv = Type.tvoid.value.pointerTo();
                            Expression e = ea != null ? new VarExp(exp.loc.value, v, true) : exp.e1.value.castTo(this.sc.value, tpv);
                            e = new CallExp(exp.loc.value, new VarExp(exp.loc.value, f, false), e);
                            ec = expressionSemantic(e, this.sc.value);
                        }
                        ea = Expression.combine(ea, eb, ec);
                        assert(ea != null);
                        this.result.value = ea;
                        return ;
                    }
                    break;
                case 0:
                    Type tv = tb.nextOf().baseElemOf();
                    if (((tv.ty.value & 0xFF) == ENUMTY.Tstruct))
                    {
                        ad = ((TypeStruct)tv).sym.value;
                        if (ad.dtor.value != null)
                            semanticTypeInfo(this.sc.value, ad.type.value);
                    }
                    break;
                default:
                exp.error(new BytePtr("cannot delete type `%s`"), exp.e1.value.type.value.toChars());
                this.setError();
                return ;
            }
            boolean err = false;
            if (ad != null)
            {
                if (ad.dtor.value != null)
                {
                    (err ? 1 : 0) |= (exp.checkPurity(this.sc.value, ad.dtor.value) ? 1 : 0);
                    (err ? 1 : 0) |= (exp.checkSafety(this.sc.value, ad.dtor.value) ? 1 : 0);
                    (err ? 1 : 0) |= (exp.checkNogc(this.sc.value, ad.dtor.value) ? 1 : 0);
                }
                if ((ad.aggDelete.value != null) && ((tb.ty.value & 0xFF) != ENUMTY.Tarray))
                {
                    (err ? 1 : 0) |= (exp.checkPurity(this.sc.value, ad.aggDelete.value) ? 1 : 0);
                    (err ? 1 : 0) |= (exp.checkSafety(this.sc.value, ad.aggDelete.value) ? 1 : 0);
                    (err ? 1 : 0) |= (exp.checkNogc(this.sc.value, ad.aggDelete.value) ? 1 : 0);
                }
                if (err)
                    this.setError();
                    return ;
            }
            if (((this.sc.value.get()).intypeof.value == 0) && ((this.sc.value.get()).func.value != null) && !exp.isRAII && (this.sc.value.get()).func.value.setUnsafe() && (((this.sc.value.get()).flags.value & 8) == 0))
            {
                exp.error(new BytePtr("`%s` is not `@safe` but is used in `@safe` function `%s`"), exp.toChars(), (this.sc.value.get()).func.value.toChars());
                err = true;
            }
            if (err)
                this.setError();
                return ;
            this.result.value = exp;
        }

        public  void visit(CastExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            if (exp.to.value != null)
            {
                exp.to.value = typeSemantic(exp.to.value, exp.loc.value, this.sc.value);
                if ((pequals(exp.to.value, Type.terror.value)))
                    this.setError();
                    return ;
                if (!exp.to.value.hasPointers())
                    exp.setNoderefOperand();
                exp.e1.value = inferType(exp.e1.value, exp.to.value, 0);
            }
            {
                Expression e = unaSemantic(exp, this.sc.value);
                if ((e) != null)
                {
                    this.result.value = e;
                    return ;
                }
            }
            if (((exp.e1.value.op.value & 0xFF) == 20))
                exp.e1.value = resolveAliasThis(this.sc.value, exp.e1.value, false);
            Expression e1x = resolveProperties(this.sc.value, exp.e1.value);
            if (((e1x.op.value & 0xFF) == 127))
            {
                this.result.value = e1x;
                return ;
            }
            if (e1x.checkType())
                this.setError();
                return ;
            exp.e1.value = e1x;
            if (exp.e1.value.type.value == null)
            {
                exp.error(new BytePtr("cannot cast `%s`"), exp.e1.value.toChars());
                this.setError();
                return ;
            }
            if (((exp.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Ttuple))
            {
                TupleExp te = exp.e1.value.isTupleExp();
                if (((te.exps.value.get()).length.value == 1))
                    exp.e1.value = (te.exps.value.get()).get(0);
            }
            boolean allowImplicitConstruction = exp.to.value != null;
            if (exp.to.value == null)
            {
                exp.to.value = exp.e1.value.type.value.castMod(exp.mod);
                exp.to.value = typeSemantic(exp.to.value, exp.loc.value, this.sc.value);
                if ((pequals(exp.to.value, Type.terror.value)))
                    this.setError();
                    return ;
            }
            if (((exp.to.value.ty.value & 0xFF) == ENUMTY.Ttuple))
            {
                exp.error(new BytePtr("cannot cast `%s` to tuple type `%s`"), exp.e1.value.toChars(), exp.to.value.toChars());
                this.setError();
                return ;
            }
            if (((exp.to.value.ty.value & 0xFF) == ENUMTY.Tvoid))
            {
                exp.type.value = exp.to.value;
                this.result.value = exp;
                return ;
            }
            if (!exp.to.value.equals(exp.e1.value.type.value) && ((exp.mod & 0xFF) == 255))
            {
                {
                    Expression e = op_overload(exp, this.sc.value, null);
                    if ((e) != null)
                    {
                        this.result.value = e.implicitCastTo(this.sc.value, exp.to.value);
                        return ;
                    }
                }
            }
            Type t1b = exp.e1.value.type.value.toBasetype();
            Type tob = exp.to.value.toBasetype();
            if (allowImplicitConstruction && ((tob.ty.value & 0xFF) == ENUMTY.Tstruct) && !tob.equals(t1b))
            {
                Expression e = new TypeExp(exp.loc.value, exp.to.value);
                e = new CallExp(exp.loc.value, e, exp.e1.value);
                e = trySemantic(e, this.sc.value);
                if (e != null)
                {
                    this.result.value = e;
                    return ;
                }
            }
            if (!t1b.equals(tob) && ((t1b.ty.value & 0xFF) == ENUMTY.Tarray) || ((t1b.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                if (checkNonAssignmentArrayOp(exp.e1.value, false))
                    this.setError();
                    return ;
            }
            if (((tob.ty.value & 0xFF) == ENUMTY.Tvector) && ((t1b.ty.value & 0xFF) != ENUMTY.Tvector))
            {
                this.result.value = new VectorExp(exp.loc.value, exp.e1.value, exp.to.value);
                return ;
            }
            Expression ex = exp.e1.value.castTo(this.sc.value, exp.to.value);
            if (((ex.op.value & 0xFF) == 127))
            {
                this.result.value = ex;
                return ;
            }
            if (((this.sc.value.get()).intypeof.value == 0) && !isSafeCast(ex, t1b, tob) && ((this.sc.value.get()).func.value == null) && (((this.sc.value.get()).stc.value & 8589934592L) != 0) || ((this.sc.value.get()).func.value != null) && (this.sc.value.get()).func.value.setUnsafe() && (((this.sc.value.get()).flags.value & 8) == 0))
            {
                exp.error(new BytePtr("cast from `%s` to `%s` not allowed in safe code"), exp.e1.value.type.value.toChars(), exp.to.value.toChars());
                this.setError();
                return ;
            }
            if (((tob.ty.value & 0xFF) == ENUMTY.Tarray))
            {
                {
                    AggregateDeclaration ad = isAggregate(t1b);
                    if ((ad) != null)
                    {
                        if (ad.aliasthis.value != null)
                        {
                            Expression e = resolveAliasThis(this.sc.value, exp.e1.value, false);
                            e = new CastExp(exp.loc.value, e, exp.to.value);
                            this.result.value = expressionSemantic(e, this.sc.value);
                            return ;
                        }
                    }
                }
                if (((t1b.ty.value & 0xFF) == ENUMTY.Tarray) && ((exp.e1.value.op.value & 0xFF) != 47) && (((this.sc.value.get()).flags.value & 128) == 0))
                {
                    Type tFrom = t1b.nextOf();
                    Type tTo = tob.nextOf();
                    if (((exp.e1.value.op.value & 0xFF) != 121) || ((tTo.ty.value & 0xFF) == ENUMTY.Tarray))
                    {
                        int fromSize = (int)tFrom.size();
                        int toSize = (int)tTo.size();
                        if ((fromSize != toSize))
                        {
                            if (!verifyHookExist(exp.loc.value, this.sc.value.get(), Id.__ArrayCast, new ByteSlice("casting array of structs"), Id.object.value))
                                this.setError();
                                return ;
                            if ((toSize == 0) || (fromSize % toSize != 0))
                            {
                                Expression id = new IdentifierExp(exp.loc.value, Id.empty.value);
                                DotIdExp dotid = new DotIdExp(exp.loc.value, id, Id.object.value);
                                Ptr<DArray<RootObject>> tiargs = refPtr(new DArray<RootObject>());
                                (tiargs.get()).push(tFrom);
                                (tiargs.get()).push(tTo);
                                DotTemplateInstanceExp dt = new DotTemplateInstanceExp(exp.loc.value, dotid, Id.__ArrayCast, tiargs);
                                Ptr<DArray<Expression>> arguments = refPtr(new DArray<Expression>());
                                (arguments.get()).push(exp.e1.value);
                                Expression ce = new CallExp(exp.loc.value, dt, arguments);
                                this.result.value = expressionSemantic(ce, this.sc.value);
                                return ;
                            }
                        }
                    }
                }
            }
            this.result.value = ex;
        }

        public  void visit(VectorExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            exp.e1.value = expressionSemantic(exp.e1.value, this.sc.value);
            exp.type.value = typeSemantic(exp.to, exp.loc.value, this.sc.value);
            if (((exp.e1.value.op.value & 0xFF) == 127) || ((exp.type.value.ty.value & 0xFF) == ENUMTY.Terror))
            {
                this.result.value = exp.e1.value;
                return ;
            }
            Type tb = exp.type.value.toBasetype();
            assert(((tb.ty.value & 0xFF) == ENUMTY.Tvector));
            TypeVector tv = (TypeVector)tb;
            Type te = tv.elementType();
            exp.dim.value = (int)(tv.size(exp.loc.value) / te.size(exp.loc.value));
            Function1<Expression,Boolean> checkElem = new Function1<Expression,Boolean>(){
                public Boolean invoke(Expression elem) {
                    if ((elem.isConst() == 1))
                        return false;
                    exp.error(new BytePtr("constant expression expected, not `%s`"), elem.toChars());
                    return true;
                }
            };
            exp.e1.value = exp.e1.value.optimize(0, false);
            boolean res = false;
            if (((exp.e1.value.op.value & 0xFF) == 47))
            {
                {
                    int __key1382 = 0;
                    int __limit1383 = exp.dim.value;
                    for (; (__key1382 < __limit1383);__key1382 += 1) {
                        int i = __key1382;
                        (res ? 1 : 0) |= (checkElem.invoke(((ArrayLiteralExp)exp.e1.value).getElement(i)) ? 1 : 0);
                    }
                }
            }
            else if (((exp.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tvoid))
                checkElem.invoke(exp.e1.value);
            this.result.value = res ? new ErrorExp() : exp;
        }

        public  void visit(VectorArrayExp e) {
            if (e.type.value == null)
            {
                unaSemantic(e, this.sc.value);
                e.e1.value = resolveProperties(this.sc.value, e.e1.value);
                if (((e.e1.value.op.value & 0xFF) == 127))
                {
                    this.result.value = e.e1.value;
                    return ;
                }
                assert(((e.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tvector));
                e.type.value = e.e1.value.type.value.isTypeVector().basetype.value;
            }
            this.result.value = e;
        }

        public  void visit(SliceExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            {
                Expression ex = unaSemantic(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            exp.e1.value = resolveProperties(this.sc.value, exp.e1.value);
            if (((exp.e1.value.op.value & 0xFF) == 20) && ((exp.e1.value.type.value.ty.value & 0xFF) != ENUMTY.Ttuple))
            {
                if ((exp.lwr.value != null) || (exp.upr.value != null))
                {
                    exp.error(new BytePtr("cannot slice type `%s`"), exp.e1.value.toChars());
                    this.setError();
                    return ;
                }
                Expression e = new TypeExp(exp.loc.value, exp.e1.value.type.value.arrayOf());
                this.result.value = expressionSemantic(e, this.sc.value);
                return ;
            }
            if ((exp.lwr.value == null) && (exp.upr.value == null))
            {
                if (((exp.e1.value.op.value & 0xFF) == 47))
                {
                    Type t1b = exp.e1.value.type.value.toBasetype();
                    Expression e = exp.e1.value;
                    if (((t1b.ty.value & 0xFF) == ENUMTY.Tsarray))
                    {
                        e = e.copy();
                        e.type.value = t1b.nextOf().arrayOf();
                    }
                    this.result.value = e;
                    return ;
                }
                if (((exp.e1.value.op.value & 0xFF) == 31))
                {
                    SliceExp se = (SliceExp)exp.e1.value;
                    if ((se.lwr.value == null) && (se.upr.value == null))
                    {
                        this.result.value = se;
                        return ;
                    }
                }
                if (isArrayOpOperand(exp.e1.value))
                {
                    this.result.value = exp.e1.value;
                    return ;
                }
            }
            if (((exp.e1.value.op.value & 0xFF) == 127))
            {
                this.result.value = exp.e1.value;
                return ;
            }
            if (((exp.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                this.setError();
                return ;
            Type t1b = exp.e1.value.type.value.toBasetype();
            if (((t1b.ty.value & 0xFF) == ENUMTY.Tpointer))
            {
                if (((((TypePointer)t1b).next.value.ty.value & 0xFF) == ENUMTY.Tfunction))
                {
                    exp.error(new BytePtr("cannot slice function pointer `%s`"), exp.e1.value.toChars());
                    this.setError();
                    return ;
                }
                if ((exp.lwr.value == null) || (exp.upr.value == null))
                {
                    exp.error(new BytePtr("need upper and lower bound to slice pointer"));
                    this.setError();
                    return ;
                }
                if (((this.sc.value.get()).func.value != null) && ((this.sc.value.get()).intypeof.value == 0) && (this.sc.value.get()).func.value.setUnsafe() && (((this.sc.value.get()).flags.value & 8) == 0))
                {
                    exp.error(new BytePtr("pointer slicing not allowed in safe functions"));
                    this.setError();
                    return ;
                }
            }
            else if (((t1b.ty.value & 0xFF) == ENUMTY.Tarray))
            {
            }
            else if (((t1b.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                if (!exp.arrayop && global.params.vsafe.value)
                {
                    {
                        VarDeclaration v = expToVariable(exp.e1.value);
                        if ((v) != null)
                        {
                            if (((exp.e1.value.op.value & 0xFF) == 27))
                            {
                                DotVarExp dve = (DotVarExp)exp.e1.value;
                                if (((dve.e1.value.op.value & 0xFF) == 123) || ((dve.e1.value.op.value & 0xFF) == 124) && ((v.storage_class.value & 2097152L) == 0))
                                {
                                    v = null;
                                }
                            }
                            if ((v != null) && !checkAddressVar(this.sc.value, exp, v))
                                this.setError();
                                return ;
                        }
                    }
                }
            }
            else if (((t1b.ty.value & 0xFF) == ENUMTY.Ttuple))
            {
                if ((exp.lwr.value == null) && (exp.upr.value == null))
                {
                    this.result.value = exp.e1.value;
                    return ;
                }
                if ((exp.lwr.value == null) || (exp.upr.value == null))
                {
                    exp.error(new BytePtr("need upper and lower bound to slice tuple"));
                    this.setError();
                    return ;
                }
            }
            else if (((t1b.ty.value & 0xFF) == ENUMTY.Tvector))
            {
                TypeVector tv1 = (TypeVector)t1b;
                t1b = tv1.basetype.value;
                t1b = t1b.castMod(tv1.mod.value);
                exp.e1.value.type.value = t1b;
            }
            else
            {
                exp.error(new BytePtr("`%s` cannot be sliced with `[]`"), ((t1b.ty.value & 0xFF) == ENUMTY.Tvoid) ? exp.e1.value.toChars() : t1b.toChars());
                this.setError();
                return ;
            }
            Ptr<Scope> scx = this.sc.value;
            if (((t1b.ty.value & 0xFF) == ENUMTY.Tsarray) || ((t1b.ty.value & 0xFF) == ENUMTY.Tarray) || ((t1b.ty.value & 0xFF) == ENUMTY.Ttuple))
            {
                ScopeDsymbol sym = new ArrayScopeSymbol(this.sc.value, exp);
                sym.parent.value = (this.sc.value.get()).scopesym.value;
                this.sc.value = (this.sc.value.get()).push(sym);
            }
            if (exp.lwr.value != null)
            {
                if (((t1b.ty.value & 0xFF) == ENUMTY.Ttuple))
                    this.sc.value = (this.sc.value.get()).startCTFE();
                exp.lwr.value = expressionSemantic(exp.lwr.value, this.sc.value);
                exp.lwr.value = resolveProperties(this.sc.value, exp.lwr.value);
                if (((t1b.ty.value & 0xFF) == ENUMTY.Ttuple))
                    this.sc.value = (this.sc.value.get()).endCTFE();
                exp.lwr.value = exp.lwr.value.implicitCastTo(this.sc.value, Type.tsize_t.value);
            }
            if (exp.upr.value != null)
            {
                if (((t1b.ty.value & 0xFF) == ENUMTY.Ttuple))
                    this.sc.value = (this.sc.value.get()).startCTFE();
                exp.upr.value = expressionSemantic(exp.upr.value, this.sc.value);
                exp.upr.value = resolveProperties(this.sc.value, exp.upr.value);
                if (((t1b.ty.value & 0xFF) == ENUMTY.Ttuple))
                    this.sc.value = (this.sc.value.get()).endCTFE();
                exp.upr.value = exp.upr.value.implicitCastTo(this.sc.value, Type.tsize_t.value);
            }
            if ((this.sc.value != scx))
                this.sc.value = (this.sc.value.get()).pop();
            if ((exp.lwr.value != null) && (pequals(exp.lwr.value.type.value, Type.terror.value)) || (exp.upr.value != null) && (pequals(exp.upr.value.type.value, Type.terror.value)))
                this.setError();
                return ;
            if (((t1b.ty.value & 0xFF) == ENUMTY.Ttuple))
            {
                exp.lwr.value = exp.lwr.value.ctfeInterpret();
                exp.upr.value = exp.upr.value.ctfeInterpret();
                long i1 = exp.lwr.value.toUInteger();
                long i2 = exp.upr.value.toUInteger();
                TupleExp te = null;
                TypeTuple tup = null;
                int length = 0;
                if (((exp.e1.value.op.value & 0xFF) == 126))
                {
                    te = (TupleExp)exp.e1.value;
                    tup = null;
                    length = (te.exps.value.get()).length.value;
                }
                else if (((exp.e1.value.op.value & 0xFF) == 20))
                {
                    te = null;
                    tup = (TypeTuple)t1b;
                    length = Parameter.dim(tup.arguments.value);
                }
                else
                    throw new AssertionError("Unreachable code!");
                if ((i2 < i1) || ((long)length < i2))
                {
                    exp.error(new BytePtr("string slice `[%llu .. %llu]` is out of bounds"), i1, i2);
                    this.setError();
                    return ;
                }
                int j1 = (int)i1;
                int j2 = (int)i2;
                Expression e = null;
                if (((exp.e1.value.op.value & 0xFF) == 126))
                {
                    Ptr<DArray<Expression>> exps = refPtr(new DArray<Expression>(j2 - j1));
                    {
                        int i = 0;
                        for (; (i < j2 - j1);i++){
                            exps.get().set(i, (te.exps.value.get()).get(j1 + i));
                        }
                    }
                    e = new TupleExp(exp.loc.value, te.e0.value, exps);
                }
                else
                {
                    Ptr<DArray<Parameter>> args = refPtr(new DArray<Parameter>());
                    (args.get()).reserve(j2 - j1);
                    {
                        int i = j1;
                        for (; (i < j2);i++){
                            Parameter arg = Parameter.getNth(tup.arguments.value, i, null);
                            (args.get()).push(arg);
                        }
                    }
                    e = new TypeExp(exp.e1.value.loc.value, new TypeTuple(args));
                }
                e = expressionSemantic(e, this.sc.value);
                this.result.value = e;
                return ;
            }
            exp.type.value = t1b.nextOf().arrayOf();
            if (exp.type.value.equals(t1b))
                exp.type.value = exp.e1.value.type.value;
            setLengthVarIfKnown(exp.lengthVar.value, t1b);
            if ((exp.lwr.value != null) && (exp.upr.value != null))
            {
                exp.lwr.value = exp.lwr.value.optimize(0, false);
                exp.upr.value = exp.upr.value.optimize(0, false);
                IntRange lwrRange = getIntRange(exp.lwr.value).copy();
                IntRange uprRange = getIntRange(exp.upr.value).copy();
                if (((t1b.ty.value & 0xFF) == ENUMTY.Tsarray) || ((t1b.ty.value & 0xFF) == ENUMTY.Tarray))
                {
                    Expression el = new ArrayLengthExp(exp.loc.value, exp.e1.value);
                    el = expressionSemantic(el, this.sc.value);
                    el = el.optimize(0, false);
                    if (((el.op.value & 0xFF) == 135))
                    {
                        long length = el.toInteger();
                        IntRange bounds = bounds = new IntRange(new SignExtendedNumber(0L, false), new SignExtendedNumber(length, false));
                        exp.upperIsInBounds = bounds.contains(uprRange);
                    }
                    else if (((exp.upr.value.op.value & 0xFF) == 135) && (exp.upr.value.toInteger() == 0L))
                    {
                        exp.upperIsInBounds = true;
                    }
                    else if (((exp.upr.value.op.value & 0xFF) == 26) && (pequals(((VarExp)exp.upr.value).var.value.ident.value, Id.dollar)))
                    {
                        exp.upperIsInBounds = true;
                    }
                }
                else if (((t1b.ty.value & 0xFF) == ENUMTY.Tpointer))
                {
                    exp.upperIsInBounds = true;
                }
                else
                    throw new AssertionError("Unreachable code!");
                exp.lowerIsLessThanUpper = lwrRange.imax.opCmp(uprRange.imin) <= 0;
            }
            this.result.value = exp;
        }

        public  void visit(ArrayLengthExp e) {
            if (e.type.value != null)
            {
                this.result.value = e;
                return ;
            }
            {
                Expression ex = unaSemantic(e, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            e.e1.value = resolveProperties(this.sc.value, e.e1.value);
            e.type.value = Type.tsize_t.value;
            this.result.value = e;
        }

        public  void visit(ArrayExp exp) {
            assert(exp.type.value == null);
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            if (isAggregate(exp.e1.value.type.value) != null)
                exp.error(new BytePtr("no `[]` operator overload for type `%s`"), exp.e1.value.type.value.toChars());
            else if (((exp.e1.value.op.value & 0xFF) == 20) && ((exp.e1.value.type.value.ty.value & 0xFF) != ENUMTY.Ttuple))
                exp.error(new BytePtr("static array of `%s` with multiple lengths not allowed"), exp.e1.value.type.value.toChars());
            else if (isIndexableNonAggregate(exp.e1.value.type.value))
                exp.error(new BytePtr("only one index allowed to index `%s`"), exp.e1.value.type.value.toChars());
            else
                exp.error(new BytePtr("cannot use `[]` operator on expression of type `%s`"), exp.e1.value.type.value.toChars());
            this.result.value = new ErrorExp();
        }

        public  void visit(DotExp exp) {
            exp.e1.value = expressionSemantic(exp.e1.value, this.sc.value);
            exp.e2.value = expressionSemantic(exp.e2.value, this.sc.value);
            if (((exp.e1.value.op.value & 0xFF) == 20))
            {
                this.result.value = exp.e2.value;
                return ;
            }
            if (((exp.e2.value.op.value & 0xFF) == 20))
            {
                this.result.value = exp.e2.value;
                return ;
            }
            if (((exp.e2.value.op.value & 0xFF) == 36))
            {
                TemplateDeclaration td = ((TemplateExp)exp.e2.value).td.value;
                Expression e = new DotTemplateExp(exp.loc.value, exp.e1.value, td);
                this.result.value = expressionSemantic(e, this.sc.value);
                return ;
            }
            if (exp.type.value == null)
                exp.type.value = exp.e2.value.type.value;
            this.result.value = exp;
        }

        public  void visit(CommaExp e) {
            if (e.type.value != null)
            {
                this.result.value = e;
                return ;
            }
            if (e.allowCommaExp)
            {
                CommaExp.allow(e.e1.value);
                CommaExp.allow(e.e2.value);
            }
            {
                Expression ex = binSemanticProp(e, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            e.e1.value = e.e1.value.addDtorHook(this.sc.value);
            if (checkNonAssignmentArrayOp(e.e1.value, false))
                this.setError();
                return ;
            e.type.value = e.e2.value.type.value;
            if ((e.type.value != Type.tvoid.value) && !e.allowCommaExp && !e.isGenerated)
                e.error(new BytePtr("Using the result of a comma expression is not allowed"));
            this.result.value = e;
        }

        public  void visit(IntervalExp e) {
            if (e.type.value != null)
            {
                this.result.value = e;
                return ;
            }
            Expression le = e.lwr.value;
            le = expressionSemantic(le, this.sc.value);
            le = resolveProperties(this.sc.value, le);
            Expression ue = e.upr.value;
            ue = expressionSemantic(ue, this.sc.value);
            ue = resolveProperties(this.sc.value, ue);
            if (((le.op.value & 0xFF) == 127))
            {
                this.result.value = le;
                return ;
            }
            if (((ue.op.value & 0xFF) == 127))
            {
                this.result.value = ue;
                return ;
            }
            e.lwr.value = le;
            e.upr.value = ue;
            e.type.value = Type.tvoid.value;
            this.result.value = e;
        }

        public  void visit(DelegatePtrExp e) {
            if (e.type.value == null)
            {
                unaSemantic(e, this.sc.value);
                e.e1.value = resolveProperties(this.sc.value, e.e1.value);
                if (((e.e1.value.op.value & 0xFF) == 127))
                {
                    this.result.value = e.e1.value;
                    return ;
                }
                e.type.value = Type.tvoidptr;
            }
            this.result.value = e;
        }

        public  void visit(DelegateFuncptrExp e) {
            if (e.type.value == null)
            {
                unaSemantic(e, this.sc.value);
                e.e1.value = resolveProperties(this.sc.value, e.e1.value);
                if (((e.e1.value.op.value & 0xFF) == 127))
                {
                    this.result.value = e.e1.value;
                    return ;
                }
                e.type.value = e.e1.value.type.value.nextOf().pointerTo();
            }
            this.result.value = e;
        }

        public  void visit(IndexExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            if (exp.e1.value.type.value == null)
                exp.e1.value = expressionSemantic(exp.e1.value, this.sc.value);
            assert(exp.e1.value.type.value != null);
            if (((exp.e1.value.op.value & 0xFF) == 20) && ((exp.e1.value.type.value.ty.value & 0xFF) != ENUMTY.Ttuple))
            {
                exp.e2.value = expressionSemantic(exp.e2.value, this.sc.value);
                exp.e2.value = resolveProperties(this.sc.value, exp.e2.value);
                Type nt = null;
                if (((exp.e2.value.op.value & 0xFF) == 20))
                    nt = new TypeAArray(exp.e1.value.type.value, exp.e2.value.type.value);
                else
                    nt = new TypeSArray(exp.e1.value.type.value, exp.e2.value);
                Expression e = new TypeExp(exp.loc.value, nt);
                this.result.value = expressionSemantic(e, this.sc.value);
                return ;
            }
            if (((exp.e1.value.op.value & 0xFF) == 127))
            {
                this.result.value = exp.e1.value;
                return ;
            }
            if (((exp.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                this.setError();
                return ;
            Type t1b = exp.e1.value.type.value.toBasetype();
            if (((t1b.ty.value & 0xFF) == ENUMTY.Tvector))
            {
                TypeVector tv1 = (TypeVector)t1b;
                t1b = tv1.basetype.value;
                t1b = t1b.castMod(tv1.mod.value);
                exp.e1.value.type.value = t1b;
            }
            Ptr<Scope> scx = this.sc.value;
            if (((t1b.ty.value & 0xFF) == ENUMTY.Tsarray) || ((t1b.ty.value & 0xFF) == ENUMTY.Tarray) || ((t1b.ty.value & 0xFF) == ENUMTY.Ttuple))
            {
                ScopeDsymbol sym = new ArrayScopeSymbol(this.sc.value, exp);
                sym.parent.value = (this.sc.value.get()).scopesym.value;
                this.sc.value = (this.sc.value.get()).push(sym);
            }
            if (((t1b.ty.value & 0xFF) == ENUMTY.Ttuple))
                this.sc.value = (this.sc.value.get()).startCTFE();
            exp.e2.value = expressionSemantic(exp.e2.value, this.sc.value);
            exp.e2.value = resolveProperties(this.sc.value, exp.e2.value);
            if (((t1b.ty.value & 0xFF) == ENUMTY.Ttuple))
                this.sc.value = (this.sc.value.get()).endCTFE();
            if (((exp.e2.value.op.value & 0xFF) == 126))
            {
                TupleExp te = (TupleExp)exp.e2.value;
                if ((te.exps.value != null) && ((te.exps.value.get()).length.value == 1))
                    exp.e2.value = Expression.combine(te.e0.value, (te.exps.value.get()).get(0));
            }
            if ((this.sc.value != scx))
                this.sc.value = (this.sc.value.get()).pop();
            if ((pequals(exp.e2.value.type.value, Type.terror.value)))
                this.setError();
                return ;
            if (checkNonAssignmentArrayOp(exp.e1.value, false))
                this.setError();
                return ;
            switch ((t1b.ty.value & 0xFF))
            {
                case 3:
                    if (((((TypePointer)t1b).next.value.ty.value & 0xFF) == ENUMTY.Tfunction))
                    {
                        exp.error(new BytePtr("cannot index function pointer `%s`"), exp.e1.value.toChars());
                        this.setError();
                        return ;
                    }
                    exp.e2.value = exp.e2.value.implicitCastTo(this.sc.value, Type.tsize_t.value);
                    if ((pequals(exp.e2.value.type.value, Type.terror.value)))
                        this.setError();
                        return ;
                    exp.e2.value = exp.e2.value.optimize(0, false);
                    if (((exp.e2.value.op.value & 0xFF) == 135) && (exp.e2.value.toInteger() == 0L))
                    {
                    }
                    else if (((this.sc.value.get()).func.value != null) && (this.sc.value.get()).func.value.setUnsafe() && (((this.sc.value.get()).flags.value & 8) == 0))
                    {
                        exp.error(new BytePtr("safe function `%s` cannot index pointer `%s`"), (this.sc.value.get()).func.value.toPrettyChars(false), exp.e1.value.toChars());
                        this.setError();
                        return ;
                    }
                    exp.type.value = ((TypeNext)t1b).next.value;
                    break;
                case 0:
                    exp.e2.value = exp.e2.value.implicitCastTo(this.sc.value, Type.tsize_t.value);
                    if ((pequals(exp.e2.value.type.value, Type.terror.value)))
                        this.setError();
                        return ;
                    exp.type.value = ((TypeNext)t1b).next.value;
                    break;
                case 1:
                    exp.e2.value = exp.e2.value.implicitCastTo(this.sc.value, Type.tsize_t.value);
                    if ((pequals(exp.e2.value.type.value, Type.terror.value)))
                        this.setError();
                        return ;
                    exp.type.value = t1b.nextOf();
                    break;
                case 2:
                    TypeAArray taa = (TypeAArray)t1b;
                    if (!arrayTypeCompatibleWithoutCasting(exp.e2.value.type.value, taa.index.value))
                    {
                        exp.e2.value = exp.e2.value.implicitCastTo(this.sc.value, taa.index.value);
                        if ((pequals(exp.e2.value.type.value, Type.terror.value)))
                            this.setError();
                            return ;
                    }
                    semanticTypeInfo(this.sc.value, taa);
                    exp.type.value = taa.next.value;
                    break;
                case 37:
                    exp.e2.value = exp.e2.value.implicitCastTo(this.sc.value, Type.tsize_t.value);
                    if ((pequals(exp.e2.value.type.value, Type.terror.value)))
                        this.setError();
                        return ;
                    exp.e2.value = exp.e2.value.ctfeInterpret();
                    long index = exp.e2.value.toUInteger();
                    TupleExp te = null;
                    TypeTuple tup = null;
                    int length = 0;
                    if (((exp.e1.value.op.value & 0xFF) == 126))
                    {
                        te = (TupleExp)exp.e1.value;
                        tup = null;
                        length = (te.exps.value.get()).length.value;
                    }
                    else if (((exp.e1.value.op.value & 0xFF) == 20))
                    {
                        te = null;
                        tup = (TypeTuple)t1b;
                        length = Parameter.dim(tup.arguments.value);
                    }
                    else
                        throw new AssertionError("Unreachable code!");
                    if (((long)length <= index))
                    {
                        exp.error(new BytePtr("array index `[%llu]` is outside array bounds `[0 .. %llu]`"), index, (long)length);
                        this.setError();
                        return ;
                    }
                    Expression e = null;
                    if (((exp.e1.value.op.value & 0xFF) == 126))
                    {
                        e = (te.exps.value.get()).get((int)index);
                        e = Expression.combine(te.e0.value, e);
                    }
                    else
                        e = new TypeExp(exp.e1.value.loc.value, Parameter.getNth(tup.arguments.value, (int)index, null).type.value);
                    this.result.value = e;
                    return ;
                default:
                exp.error(new BytePtr("`%s` must be an array or pointer type, not `%s`"), exp.e1.value.toChars(), exp.e1.value.type.value.toChars());
                this.setError();
                return ;
            }
            setLengthVarIfKnown(exp.lengthVar.value, t1b);
            if (((t1b.ty.value & 0xFF) == ENUMTY.Tsarray) || ((t1b.ty.value & 0xFF) == ENUMTY.Tarray))
            {
                Expression el = new ArrayLengthExp(exp.loc.value, exp.e1.value);
                el = expressionSemantic(el, this.sc.value);
                el = el.optimize(0, false);
                if (((el.op.value & 0xFF) == 135))
                {
                    exp.e2.value = exp.e2.value.optimize(0, false);
                    long length = el.toInteger();
                    if (length != 0)
                    {
                        IntRange bounds = bounds = new IntRange(new SignExtendedNumber(0L, false), new SignExtendedNumber(length - 1L, false));
                        exp.indexIsInBounds = bounds.contains(getIntRange(exp.e2.value));
                    }
                }
            }
            this.result.value = exp;
        }

        public  void visit(PostExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            {
                Expression ex = binSemantic(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Expression e1x = resolveProperties(this.sc.value, exp.e1.value);
            if (((e1x.op.value & 0xFF) == 127))
            {
                this.result.value = e1x;
                return ;
            }
            exp.e1.value = e1x;
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            if (exp.e1.value.checkReadModifyWrite(exp.op.value, null))
                this.setError();
                return ;
            if (((exp.e1.value.op.value & 0xFF) == 31))
            {
                BytePtr s = pcopy(((exp.op.value & 0xFF) == 93) ? new BytePtr("increment") : new BytePtr("decrement"));
                exp.error(new BytePtr("cannot post-%s array slice `%s`, use pre-%s instead"), s, exp.e1.value.toChars(), s);
                this.setError();
                return ;
            }
            exp.e1.value = exp.e1.value.optimize(0, false);
            Type t1 = exp.e1.value.type.value.toBasetype();
            if (((t1.ty.value & 0xFF) == ENUMTY.Tclass) || ((t1.ty.value & 0xFF) == ENUMTY.Tstruct) || ((exp.e1.value.op.value & 0xFF) == 32))
            {
                Expression de = null;
                if (((exp.e1.value.op.value & 0xFF) != 26) && ((exp.e1.value.op.value & 0xFF) != 32))
                {
                    VarDeclaration v = copyToTemp(2097152L, new BytePtr("__postref"), exp.e1.value);
                    de = new DeclarationExp(exp.loc.value, v);
                    exp.e1.value = new VarExp(exp.e1.value.loc.value, v, true);
                }
                VarDeclaration tmp = copyToTemp(0L, new BytePtr("__pitmp"), exp.e1.value);
                Expression ea = new DeclarationExp(exp.loc.value, tmp);
                Expression eb = exp.e1.value.syntaxCopy();
                eb = new PreExp(((exp.op.value & 0xFF) == 93) ? TOK.prePlusPlus : TOK.preMinusMinus, exp.loc.value, eb);
                Expression ec = new VarExp(exp.loc.value, tmp, true);
                if (de != null)
                    ea = new CommaExp(exp.loc.value, de, ea, true);
                e = new CommaExp(exp.loc.value, ea, eb, true);
                e = new CommaExp(exp.loc.value, e, ec, true);
                e = expressionSemantic(e, this.sc.value);
                this.result.value = e;
                return ;
            }
            exp.e1.value = exp.e1.value.modifiableLvalue(this.sc.value, exp.e1.value);
            e = exp;
            if (exp.e1.value.checkScalar())
                this.setError();
                return ;
            if (exp.e1.value.checkNoBool())
                this.setError();
                return ;
            if (((exp.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tpointer))
                e = scaleFactor(exp, this.sc.value);
            else
                exp.e2.value = exp.e2.value.castTo(this.sc.value, exp.e1.value.type.value);
            e.type.value = exp.e1.value.type.value;
            this.result.value = e;
        }

        public  void visit(PreExp exp) {
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            if (((exp.op.value & 0xFF) == 103))
                e = new AddAssignExp(exp.loc.value, exp.e1.value, new IntegerExp(exp.loc.value, 1L, Type.tint32.value));
            else
                e = new MinAssignExp(exp.loc.value, exp.e1.value, new IntegerExp(exp.loc.value, 1L, Type.tint32.value));
            this.result.value = expressionSemantic(e, this.sc.value);
        }

        public  Expression getInitExp(StructDeclaration sd, Loc loc, Ptr<Scope> sc, Type t) {
            if (sd.zeroInit && !sd.isNested())
            {
                return new IntegerExp(loc, 0L, Type.tint32.value);
            }
            if (sd.isNested())
            {
                StructLiteralExp sle = new StructLiteralExp(loc, sd, null, t);
                if (!sd.fill(loc, sle.elements.value, true))
                    return new ErrorExp();
                if (checkFrameAccess(loc, sc, sd, (sle.elements.value.get()).length.value))
                    return new ErrorExp();
                sle.type.value = t;
                return sle;
            }
            return defaultInit(t, loc);
        }

        public  void visit(AssignExp exp) {
            Function2<Expression,Integer,Void> setResult = new Function2<Expression,Integer,Void>(){
                public Void invoke(Expression e, Integer line) {
                    Ref<Expression> e_ref = ref(e);
                    result.value = e_ref.value;
                }
            };
            if (exp.type.value != null)
            {
                setResult.invoke(exp, 7918);
                return ;
            }
            Expression e1old = exp.e1.value;
            {
                CommaExp e2comma = exp.e2.value.isCommaExp();
                if ((e2comma) != null)
                {
                    if (!e2comma.isGenerated)
                        exp.error(new BytePtr("Using the result of a comma expression is not allowed"));
                    Ref<Expression> e0 = ref(null);
                    exp.e2.value = Expression.extractLast(e2comma, e0);
                    Expression e = Expression.combine(e0.value, (Expression)exp);
                    setResult.invoke(expressionSemantic(e, this.sc.value), 7934);
                    return ;
                }
            }
            {
                ArrayExp ae = exp.e1.value.isArrayExp();
                if ((ae) != null)
                {
                    Expression res = null;
                    ae.e1.value = expressionSemantic(ae.e1.value, this.sc.value);
                    ae.e1.value = resolveProperties(this.sc.value, ae.e1.value);
                    Expression ae1old = ae.e1.value;
                    boolean maybeSlice = ((ae.arguments.value.get()).length.value == 0) || ((ae.arguments.value.get()).length.value == 1) && (((ae.arguments.value.get()).get(0).op.value & 0xFF) == 231);
                    IntervalExp ie = null;
                    if (maybeSlice && ((ae.arguments.value.get()).length.value != 0))
                    {
                        assert((((ae.arguments.value.get()).get(0).op.value & 0xFF) == 231));
                        ie = (IntervalExp)(ae.arguments.value.get()).get(0);
                    }
                L_outer8:
                    for (; true;){
                        if (((ae.e1.value.op.value & 0xFF) == 127))
                            setResult.invoke(ae.e1.value, 7962);
                            return ;
                        Ref<Expression> e0 = ref(null);
                        Expression ae1save = ae.e1.value;
                        ae.lengthVar.value = null;
                        Type t1b = ae.e1.value.type.value.toBasetype();
                        AggregateDeclaration ad = isAggregate(t1b);
                        if (ad == null)
                            break;
                        try {
                            if (search_function(ad, Id.indexass) != null)
                            {
                                res = resolveOpDollar(this.sc.value, ae, ptr(e0));
                                if (res == null)
                                    /*goto Lfallback*/throw Dispatch0.INSTANCE;
                                if (((res.op.value & 0xFF) == 127))
                                    setResult.invoke(res, 7979);
                                    return ;
                                res = expressionSemantic(exp.e2.value, this.sc.value);
                                if (((res.op.value & 0xFF) == 127))
                                    setResult.invoke(res, 7983);
                                    return ;
                                exp.e2.value = res;
                                Ptr<DArray<Expression>> a = (ae.arguments.value.get()).copy();
                                (a.get()).insert(0, exp.e2.value);
                                res = new DotIdExp(exp.loc.value, ae.e1.value, Id.indexass);
                                res = new CallExp(exp.loc.value, res, a);
                                if (maybeSlice)
                                    res = trySemantic(res, this.sc.value);
                                else
                                    res = expressionSemantic(res, this.sc.value);
                                if (res != null)
                                    setResult.invoke(Expression.combine(e0.value, res), 7998);
                                    return ;
                            }
                        }
                        catch(Dispatch0 __d){}
                    /*Lfallback:*/
                        if (maybeSlice && (search_function(ad, Id.sliceass) != null))
                        {
                            res = resolveOpDollar(this.sc.value, ae, ie, ptr(e0));
                            if (((res.op.value & 0xFF) == 127))
                                setResult.invoke(res, 8007);
                                return ;
                            res = expressionSemantic(exp.e2.value, this.sc.value);
                            if (((res.op.value & 0xFF) == 127))
                                setResult.invoke(res, 8011);
                                return ;
                            exp.e2.value = res;
                            Ptr<DArray<Expression>> a = refPtr(new DArray<Expression>());
                            (a.get()).push(exp.e2.value);
                            if (ie != null)
                            {
                                (a.get()).push(ie.lwr.value);
                                (a.get()).push(ie.upr.value);
                            }
                            res = new DotIdExp(exp.loc.value, ae.e1.value, Id.sliceass);
                            res = new CallExp(exp.loc.value, res, a);
                            res = expressionSemantic(res, this.sc.value);
                            setResult.invoke(Expression.combine(e0.value, res), 8028);
                            return ;
                        }
                        if ((ad.aliasthis.value != null) && (!pequals(t1b, ae.att1.value)))
                        {
                            if ((ae.att1.value == null) && t1b.checkAliasThisRec())
                                ae.att1.value = t1b;
                            ae.e1.value = resolveAliasThis(this.sc.value, ae1save, true);
                            if (ae.e1.value != null)
                                continue L_outer8;
                        }
                        break;
                    }
                    ae.e1.value = ae1old;
                    ae.lengthVar.value = null;
                }
            }
            {
                Expression e1x = exp.e1.value;
                {
                    DotTemplateInstanceExp dti = e1x.isDotTemplateInstanceExp();
                    if ((dti) != null)
                    {
                        Expression e = semanticY(dti, this.sc.value, 1);
                        if (e == null)
                        {
                            setResult.invoke(resolveUFCSProperties(this.sc.value, e1x, exp.e2.value), 8067);
                            return ;
                        }
                        e1x = e;
                    }
                    else {
                        DotIdExp die = e1x.isDotIdExp();
                        if ((die) != null)
                        {
                            Expression e = semanticY(die, this.sc.value, 1);
                            if ((e != null) && isDotOpDispatch(e))
                            {
                                exp.e2.value = expressionSemantic(exp.e2.value, this.sc.value);
                                int errors = global.startGagging();
                                e = resolvePropertiesX(this.sc.value, e, exp.e2.value);
                                if (global.endGagging(errors))
                                    e = null;
                                else
                                    setResult.invoke(e, 8090);
                                    return ;
                            }
                            if (e == null)
                                setResult.invoke(resolveUFCSProperties(this.sc.value, e1x, exp.e2.value), 8093);
                                return ;
                            e1x = e;
                        }
                        else
                        {
                            {
                                SliceExp se = e1x.isSliceExp();
                                if ((se) != null)
                                    se.arrayop = true;
                            }
                            e1x = expressionSemantic(e1x, this.sc.value);
                        }
                    }
                }
                {
                    Expression e = resolvePropertiesX(this.sc.value, e1x, exp.e2.value);
                    if ((e) != null)
                        setResult.invoke(e, 8111);
                        return ;
                }
                if (e1x.checkRightThis(this.sc.value))
                {
                    this.setError();
                    return ;
                }
                exp.e1.value = e1x;
                assert(exp.e1.value.type.value != null);
            }
            Type t1 = exp.e1.value.type.value.toBasetype();
            {
                Expression e2x = inferType(exp.e2.value, t1.baseElemOf(), 0);
                e2x = expressionSemantic(e2x, this.sc.value);
                e2x = resolveProperties(this.sc.value, e2x);
                if (((e2x.op.value & 0xFF) == 20))
                    e2x = resolveAliasThis(this.sc.value, e2x, false);
                if (((e2x.op.value & 0xFF) == 127))
                    setResult.invoke(e2x, 8133);
                    return ;
                if (e2x.checkValue())
                    this.setError();
                    return ;
                exp.e2.value = e2x;
            }
            {
                Expression e2x = exp.e2.value;
                while(true) try {
                /*Ltupleassign:*/
                    if (((exp.e1.value.op.value & 0xFF) == 126) && ((e2x.op.value & 0xFF) == 126))
                    {
                        TupleExp tup1 = (TupleExp)exp.e1.value;
                        TupleExp tup2 = (TupleExp)e2x;
                        int dim = (tup1.exps.value.get()).length.value;
                        Expression e = null;
                        if ((dim != (tup2.exps.value.get()).length.value))
                        {
                            exp.error(new BytePtr("mismatched tuple lengths, %d and %d"), dim, (tup2.exps.value.get()).length.value);
                            this.setError();
                            return ;
                        }
                        if ((dim == 0))
                        {
                            e = new IntegerExp(exp.loc.value, 0L, Type.tint32.value);
                            e = new CastExp(exp.loc.value, e, Type.tvoid.value);
                            e = Expression.combine(tup1.e0.value, tup2.e0.value, e);
                        }
                        else
                        {
                            Ptr<DArray<Expression>> exps = refPtr(new DArray<Expression>(dim));
                            {
                                int i = 0;
                                for (; (i < dim);i++){
                                    Expression ex1 = (tup1.exps.value.get()).get(i);
                                    Expression ex2 = (tup2.exps.value.get()).get(i);
                                    exps.get().set(i, new AssignExp(exp.loc.value, ex1, ex2));
                                }
                            }
                            e = new TupleExp(exp.loc.value, Expression.combine(tup1.e0.value, tup2.e0.value), exps);
                        }
                        setResult.invoke(expressionSemantic(e, this.sc.value), 8173);
                        return ;
                    }
                    try {
                        if (((exp.e1.value.op.value & 0xFF) == 126))
                        {
                            TupleDeclaration td = isAliasThisTuple(e2x);
                            if (td == null)
                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                            assert(((exp.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Ttuple));
                            TypeTuple tt = (TypeTuple)exp.e1.value.type.value;
                            Ref<Expression> e0 = ref(null);
                            Expression ev = extractSideEffect(this.sc.value, new BytePtr("__tup"), e0, e2x, false);
                            Ptr<DArray<Expression>> iexps = refPtr(new DArray<Expression>());
                            (iexps.get()).push(ev);
                            {
                                int u = 0;
                            L_outer9:
                                for (; (u < (iexps.get()).length.value);u++){
                                    while(true) try {
                                    /*Lexpand:*/
                                        Expression e = (iexps.get()).get(u);
                                        Parameter arg = Parameter.getNth(tt.arguments.value, u, null);
                                        if ((arg == null) || (e.type.value.implicitConvTo(arg.type.value) == 0))
                                        {
                                            if ((expandAliasThisTuples(iexps, u) != -1))
                                            {
                                                if (((iexps.get()).length.value <= u))
                                                    break;
                                                /*goto Lexpand*/throw Dispatch0.INSTANCE;
                                            }
                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                        }
                                        break;
                                    } catch(Dispatch0 __d){}
                                }
                            }
                            e2x = new TupleExp(e2x.loc.value, e0.value, iexps);
                            e2x = expressionSemantic(e2x, this.sc.value);
                            if (((e2x.op.value & 0xFF) == 127))
                            {
                                this.result.value = e2x;
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
            if (((exp.op.value & 0xFF) == 90) && (exp.e1.value.checkModifiable(this.sc.value, 0) == Modifiable.initialization))
            {
                Type t = exp.type.value;
                exp = new ConstructExp(exp.loc.value, exp.e1.value, exp.e2.value);
                exp.type.value = t;
                if (((this.sc.value.get()).func.value.isStaticCtorDeclaration() != null) && ((this.sc.value.get()).func.value.isSharedStaticCtorDeclaration() == null) && exp.e1.value.type.value.isImmutable())
                {
                    deprecation(exp.loc.value, new BytePtr("initialization of `immutable` variable from `static this` is deprecated."));
                    deprecationSupplemental(exp.loc.value, new BytePtr("Use `shared static this` instead."));
                }
                {
                    IndexExp ie1 = exp.e1.value.isIndexExp();
                    if ((ie1) != null)
                    {
                        Expression e1x = ie1.markSettingAAElem();
                        if (((e1x.op.value & 0xFF) == 127))
                        {
                            this.result.value = e1x;
                            return ;
                        }
                    }
                }
            }
            else if (((exp.op.value & 0xFF) == 95) && ((exp.e1.value.op.value & 0xFF) == 26) && ((((VarExp)exp.e1.value).var.value.storage_class.value & 2101248L) != 0))
            {
                exp.memset |= MemorySet.referenceInit;
            }
            if ((exp.memset & MemorySet.referenceInit) != 0)
            {
            }
            else if (((t1.ty.value & 0xFF) == ENUMTY.Tstruct))
            {
                Expression e1x = exp.e1.value;
                Expression e2x = exp.e2.value;
                StructDeclaration sd = ((TypeStruct)t1).sym.value;
                if (((exp.op.value & 0xFF) == 95))
                {
                    Type t2 = e2x.type.value.toBasetype();
                    if (((t2.ty.value & 0xFF) == ENUMTY.Tstruct) && (pequals(sd, ((TypeStruct)t2).sym.value)))
                    {
                        sd.size(exp.loc.value);
                        if ((sd.sizeok.value != Sizeok.done))
                            this.setError();
                            return ;
                        if (sd.ctor.value == null)
                            sd.ctor.value = sd.searchCtor();
                        Expression e2y = lastComma(e2x);
                        CallExp ce = ((e2y.op.value & 0xFF) == 18) ? (CallExp)e2y : null;
                        DotVarExp dve = (ce != null) && ((ce.e1.value.op.value & 0xFF) == 27) ? (DotVarExp)ce.e1.value : null;
                        if ((sd.ctor.value != null) && (ce != null) && (dve != null) && (dve.var.value.isCtorDeclaration() != null) && ((dve.e1.value.op.value & 0xFF) != 27) && (e2y.type.value.implicitConvTo(t1) != 0))
                        {
                            Expression einit = this.getInitExp(sd, exp.loc.value, this.sc.value, t1);
                            if (((einit.op.value & 0xFF) == 127))
                            {
                                this.result.value = einit;
                                return ;
                            }
                            BlitExp ae = new BlitExp(exp.loc.value, exp.e1.value, einit);
                            ae.type.value = e1x.type.value;
                            DotVarExp dvx = (DotVarExp)dve.copy();
                            dvx.e1.value = e1x;
                            CallExp cx = (CallExp)ce.copy();
                            cx.e1.value = dvx;
                            if (global.params.vsafe.value && checkConstructorEscape(this.sc.value, cx, false))
                                this.setError();
                                return ;
                            Ref<Expression> e0 = ref(null);
                            Expression.extractLast(e2x, e0);
                            Expression e = Expression.combine(e0.value, (Expression)ae, (Expression)cx);
                            e = expressionSemantic(e, this.sc.value);
                            this.result.value = e;
                            return ;
                        }
                        if ((sd.postblit.value != null) || sd.hasCopyCtor)
                        {
                            if (((e2x.op.value & 0xFF) == 100))
                            {
                                CondExp econd = (CondExp)e2x;
                                Expression ea1 = new ConstructExp(econd.e1.value.loc.value, e1x, econd.e1.value);
                                Expression ea2 = new ConstructExp(econd.e1.value.loc.value, e1x, econd.e2.value);
                                Expression e = new CondExp(exp.loc.value, econd.econd.value, ea1, ea2);
                                this.result.value = expressionSemantic(e, this.sc.value);
                                return ;
                            }
                            if (e2x.isLvalue())
                            {
                                if (sd.hasCopyCtor)
                                {
                                    Expression einit = new BlitExp(exp.loc.value, exp.e1.value, this.getInitExp(sd, exp.loc.value, this.sc.value, t1));
                                    einit.type.value = e1x.type.value;
                                    Expression e = null;
                                    e = new DotIdExp(exp.loc.value, e1x, Id.ctor.value);
                                    e = new CallExp(exp.loc.value, e, e2x);
                                    e = new CommaExp(exp.loc.value, einit, e, true);
                                    this.result.value = expressionSemantic(e, this.sc.value);
                                    return ;
                                }
                                else
                                {
                                    if (e2x.type.value.implicitConvTo(e1x.type.value) == 0)
                                    {
                                        exp.error(new BytePtr("conversion error from `%s` to `%s`"), e2x.type.value.toChars(), e1x.type.value.toChars());
                                        this.setError();
                                        return ;
                                    }
                                    Expression e = e1x.copy();
                                    e.type.value = e.type.value.mutableOf();
                                    if (e.type.value.isShared() && !sd.type.value.isShared())
                                        e.type.value = e.type.value.unSharedOf();
                                    e = new BlitExp(exp.loc.value, e, e2x);
                                    e = new DotVarExp(exp.loc.value, e, sd.postblit.value, false);
                                    e = new CallExp(exp.loc.value, e);
                                    this.result.value = expressionSemantic(e, this.sc.value);
                                    return ;
                                }
                            }
                            else
                            {
                                e2x = valueNoDtor(e2x);
                            }
                        }
                        if (e2x.implicitConvTo(t1) == 0)
                        {
                            AggregateDeclaration ad2 = isAggregate(e2x.type.value);
                            if ((ad2 != null) && (ad2.aliasthis.value != null) && !((exp.att2.value != null) && (pequals(e2x.type.value, exp.att2.value))))
                            {
                                if ((exp.att2.value == null) && exp.e2.value.type.value.checkAliasThisRec())
                                    exp.att2.value = exp.e2.value.type.value;
                                exp.e2.value = new DotIdExp(exp.e2.value.loc.value, exp.e2.value, ad2.aliasthis.value.ident.value);
                                this.result.value = expressionSemantic(exp, this.sc.value);
                                return ;
                            }
                        }
                    }
                    else if (e2x.implicitConvTo(t1) == 0)
                    {
                        sd.size(exp.loc.value);
                        if ((sd.sizeok.value != Sizeok.done))
                            this.setError();
                            return ;
                        if (sd.ctor.value == null)
                            sd.ctor.value = sd.searchCtor();
                        if (sd.ctor.value != null)
                        {
                            if (((exp.e2.value.op.value & 0xFF) == 22))
                            {
                                NewExp newExp = (NewExp)exp.e2.value;
                                if ((newExp.newtype.value != null) && (pequals(newExp.newtype.value, t1)))
                                {
                                    error(exp.loc.value, new BytePtr("cannot implicitly convert expression `%s` of type `%s` to `%s`"), newExp.toChars(), newExp.type.value.toChars(), t1.toChars());
                                    errorSupplemental(exp.loc.value, new BytePtr("Perhaps remove the `new` keyword?"));
                                    this.setError();
                                    return ;
                                }
                            }
                            Expression einit = new BlitExp(exp.loc.value, e1x, this.getInitExp(sd, exp.loc.value, this.sc.value, t1));
                            einit.type.value = e1x.type.value;
                            Expression e = null;
                            e = new DotIdExp(exp.loc.value, e1x, Id.ctor.value);
                            e = new CallExp(exp.loc.value, e, e2x);
                            e = new CommaExp(exp.loc.value, einit, e, true);
                            e = expressionSemantic(e, this.sc.value);
                            this.result.value = e;
                            return ;
                        }
                        if (search_function(sd, Id.call.value) != null)
                        {
                            e2x = typeDotIdExp(e2x.loc.value, e1x.type.value, Id.call.value);
                            e2x = new CallExp(exp.loc.value, e2x, exp.e2.value);
                            e2x = expressionSemantic(e2x, this.sc.value);
                            e2x = resolveProperties(this.sc.value, e2x);
                            if (((e2x.op.value & 0xFF) == 127))
                            {
                                this.result.value = e2x;
                                return ;
                            }
                            if (e2x.checkValue())
                                this.setError();
                                return ;
                        }
                    }
                    else
                    {
                        AggregateDeclaration ad2 = isAggregate(e2x.type.value);
                        if ((ad2 != null) && (ad2.aliasthis.value != null) && !((exp.att2.value != null) && (pequals(e2x.type.value, exp.att2.value))))
                        {
                            if ((exp.att2.value == null) && exp.e2.value.type.value.checkAliasThisRec())
                                exp.att2.value = exp.e2.value.type.value;
                            exp.e2.value = new DotIdExp(exp.e2.value.loc.value, exp.e2.value, ad2.aliasthis.value.ident.value);
                            this.result.value = expressionSemantic(exp, this.sc.value);
                            return ;
                        }
                    }
                }
                else if (((exp.op.value & 0xFF) == 90))
                {
                    if (((e1x.op.value & 0xFF) == 62) && ((((IndexExp)e1x).e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Taarray))
                    {
                        IndexExp ie = (IndexExp)e1x;
                        Type t2 = e2x.type.value.toBasetype();
                        Ref<Expression> e0 = ref(null);
                        Expression ea = extractSideEffect(this.sc.value, new BytePtr("__aatmp"), e0, ie.e1.value, false);
                        Expression ek = extractSideEffect(this.sc.value, new BytePtr("__aakey"), e0, ie.e2.value, false);
                        Expression ev = extractSideEffect(this.sc.value, new BytePtr("__aaval"), e0, e2x, false);
                        AssignExp ae = (AssignExp)exp.copy();
                        ae.e1.value = new IndexExp(exp.loc.value, ea, ek);
                        ae.e1.value = expressionSemantic(ae.e1.value, this.sc.value);
                        ae.e1.value = ae.e1.value.optimize(0, false);
                        ae.e2.value = ev;
                        Expression e = op_overload(ae, this.sc.value, null);
                        if (e != null)
                        {
                            Ref<Expression> ey = ref(null);
                            if (((t2.ty.value & 0xFF) == ENUMTY.Tstruct) && (pequals(sd, t2.toDsymbol(this.sc.value))))
                            {
                                ey.value = ev;
                            }
                            else if ((ev.implicitConvTo(ie.type.value) == 0) && (sd.ctor.value != null))
                            {
                                ey.value = new StructLiteralExp(exp.loc.value, sd, null, null);
                                ey.value = new DotIdExp(exp.loc.value, ey.value, Id.ctor.value);
                                ey.value = new CallExp(exp.loc.value, ey.value, ev);
                                ey.value = trySemantic(ey.value, this.sc.value);
                            }
                            if (ey.value != null)
                            {
                                Ref<Expression> ex = ref(null);
                                ex.value = new IndexExp(exp.loc.value, ea, ek);
                                ex.value = expressionSemantic(ex.value, this.sc.value);
                                ex.value = ex.value.optimize(0, false);
                                ex.value = ex.value.modifiableLvalue(this.sc.value, ex.value);
                                ey.value = new ConstructExp(exp.loc.value, ex.value, ey.value);
                                ey.value = expressionSemantic(ey.value, this.sc.value);
                                if (((ey.value.op.value & 0xFF) == 127))
                                {
                                    this.result.value = ey.value;
                                    return ;
                                }
                                ex.value = e;
                                Ref<Type> t = ref(null);
                                if (!typeMerge(this.sc.value, TOK.question, ptr(t), ptr(ex), ptr(ey)))
                                {
                                    ex.value = new CastExp(ex.value.loc.value, ex.value, Type.tvoid.value);
                                    ey.value = new CastExp(ey.value.loc.value, ey.value, Type.tvoid.value);
                                }
                                e = new CondExp(exp.loc.value, new InExp(exp.loc.value, ek, ea), ex.value, ey.value);
                            }
                            e = Expression.combine(e0.value, e);
                            e = expressionSemantic(e, this.sc.value);
                            this.result.value = e;
                            return ;
                        }
                    }
                    else
                    {
                        Expression e = op_overload(exp, this.sc.value, null);
                        if (e != null)
                        {
                            this.result.value = e;
                            return ;
                        }
                    }
                }
                else
                    assert(((exp.op.value & 0xFF) == 96));
                exp.e1.value = e1x;
                exp.e2.value = e2x;
            }
            else if (((t1.ty.value & 0xFF) == ENUMTY.Tclass))
            {
                if (((exp.op.value & 0xFF) == 90) && (exp.e2.value.implicitConvTo(exp.e1.value.type.value) == 0))
                {
                    Expression e = op_overload(exp, this.sc.value, null);
                    if (e != null)
                    {
                        this.result.value = e;
                        return ;
                    }
                }
            }
            else if (((t1.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                assert(((exp.e1.value.op.value & 0xFF) != 31));
                Expression e1x = exp.e1.value;
                Expression e2x = exp.e2.value;
                if (e2x.implicitConvTo(e1x.type.value) != 0)
                {
                    if (((exp.op.value & 0xFF) != 96) && ((e2x.op.value & 0xFF) == 31) && ((UnaExp)e2x).e1.value.isLvalue() || ((e2x.op.value & 0xFF) == 12) && ((UnaExp)e2x).e1.value.isLvalue() || ((e2x.op.value & 0xFF) != 31) && e2x.isLvalue())
                    {
                        if (e1x.checkPostblit(this.sc.value, t1))
                            this.setError();
                            return ;
                    }
                    if (isUnaArrayOp(e2x.op.value) || isBinArrayOp(e2x.op.value))
                    {
                        SliceExp sle = new SliceExp(e1x.loc.value, e1x, null, null);
                        sle.arrayop = true;
                        e1x = expressionSemantic(sle, this.sc.value);
                    }
                    else
                    {
                    }
                }
                else
                {
                    if ((e2x.implicitConvTo(t1.nextOf().arrayOf()) > MATCH.nomatch))
                    {
                        long dim1 = ((TypeSArray)t1).dim.value.toInteger();
                        long dim2 = dim1;
                        {
                            ArrayLiteralExp ale = e2x.isArrayLiteralExp();
                            if ((ale) != null)
                            {
                                dim2 = ale.elements.value != null ? (long)(ale.elements.value.get()).length.value : 0L;
                            }
                            else {
                                SliceExp se = e2x.isSliceExp();
                                if ((se) != null)
                                {
                                    Type tx = toStaticArrayType(se);
                                    if (tx != null)
                                        dim2 = ((TypeSArray)tx).dim.value.toInteger();
                                }
                            }
                        }
                        if ((dim1 != dim2))
                        {
                            exp.error(new BytePtr("mismatched array lengths, %d and %d"), (int)dim1, (int)dim2);
                            this.setError();
                            return ;
                        }
                    }
                    if (((exp.op.value & 0xFF) != 90))
                    {
                        int dim = t1.numberOfElems(exp.loc.value);
                        e1x.type.value = t1.baseElemOf().sarrayOf((long)dim);
                    }
                    SliceExp sle = new SliceExp(e1x.loc.value, e1x, null, null);
                    sle.arrayop = true;
                    e1x = expressionSemantic(sle, this.sc.value);
                }
                if (((e1x.op.value & 0xFF) == 127))
                    setResult.invoke(e1x, 8691);
                    return ;
                if (((e2x.op.value & 0xFF) == 127))
                    setResult.invoke(e2x, 8693);
                    return ;
                exp.e1.value = e1x;
                exp.e2.value = e2x;
                t1 = e1x.type.value.toBasetype();
            }
            {
                ArrayLengthExp ale = exp.e1.value.isArrayLengthExp();
                if ((ale) != null)
                {
                    Expression ale1x = ale.e1.value.modifiableLvalue(this.sc.value, exp.e1.value);
                    if (((ale1x.op.value & 0xFF) == 127))
                        setResult.invoke(ale1x, 8707);
                        return ;
                    ale.e1.value = ale1x;
                    Type tn = ale.e1.value.type.value.toBasetype().nextOf();
                    checkDefCtor(ale.loc.value, tn);
                    semanticTypeInfo(this.sc.value, tn);
                }
                else {
                    SliceExp se = exp.e1.value.isSliceExp();
                    if ((se) != null)
                    {
                        Type tn = se.type.value.nextOf();
                        FuncDeclaration fun = (this.sc.value.get()).func.value;
                        if (((exp.op.value & 0xFF) == 90) && !tn.isMutable() && (fun == null) || (fun != null) && (fun.isStaticCtorDeclaration() == null))
                        {
                            exp.error(new BytePtr("slice `%s` is not mutable"), se.toChars());
                            this.setError();
                            return ;
                        }
                        if (((exp.op.value & 0xFF) == 90) && !tn.baseElemOf().isAssignable())
                        {
                            exp.error(new BytePtr("slice `%s` is not mutable, struct `%s` has immutable members"), exp.e1.value.toChars(), tn.baseElemOf().toChars());
                            this.result.value = new ErrorExp();
                            return ;
                        }
                        for (; ((se.e1.value.op.value & 0xFF) == 31);) {
                            se = (SliceExp)se.e1.value;
                        }
                        if (((se.e1.value.op.value & 0xFF) == 100) && ((se.e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray))
                        {
                            se.e1.value = se.e1.value.modifiableLvalue(this.sc.value, exp.e1.value);
                            if (((se.e1.value.op.value & 0xFF) == 127))
                                setResult.invoke(se.e1.value, 8742);
                                return ;
                        }
                    }
                    else
                    {
                        if (((t1.ty.value & 0xFF) == ENUMTY.Tsarray) && ((exp.op.value & 0xFF) == 90))
                        {
                            Type tn = exp.e1.value.type.value.nextOf();
                            if ((tn != null) && !tn.baseElemOf().isAssignable())
                            {
                                exp.error(new BytePtr("array `%s` is not mutable, struct `%s` has immutable members"), exp.e1.value.toChars(), tn.baseElemOf().toChars());
                                this.result.value = new ErrorExp();
                                return ;
                            }
                        }
                        Expression e1x = exp.e1.value;
                        if (((exp.op.value & 0xFF) == 90))
                            e1x = e1x.modifiableLvalue(this.sc.value, e1old);
                        if (((e1x.op.value & 0xFF) != 26))
                            e1x = e1x.optimize(0, false);
                        if (((e1x.op.value & 0xFF) == 127))
                        {
                            this.result.value = e1x;
                            return ;
                        }
                        exp.e1.value = e1x;
                    }
                }
            }
            Expression e2x = exp.e2.value;
            Type t2 = e2x.type.value.toBasetype();
            Type telem = t1;
            for (; ((telem.ty.value & 0xFF) == ENUMTY.Tarray);) {
                telem = telem.nextOf();
            }
            if (((exp.e1.value.op.value & 0xFF) == 31) && (t1.nextOf() != null) && ((telem.ty.value & 0xFF) != ENUMTY.Tvoid) || ((e2x.op.value & 0xFF) == 13) && (e2x.implicitConvTo(t1.nextOf()) != 0))
            {
                exp.memset |= MemorySet.blockAssign;
                e2x = e2x.implicitCastTo(this.sc.value, t1.nextOf());
                if (((exp.op.value & 0xFF) != 96) && e2x.isLvalue() && exp.e1.value.checkPostblit(this.sc.value, t1.nextOf()))
                    this.setError();
                    return ;
            }
            else if (((exp.e1.value.op.value & 0xFF) == 31) && ((t2.ty.value & 0xFF) == ENUMTY.Tarray) || ((t2.ty.value & 0xFF) == ENUMTY.Tsarray) && (t2.nextOf().implicitConvTo(t1.nextOf()) != 0))
            {
                SliceExp se1 = (SliceExp)exp.e1.value;
                TypeSArray tsa1 = (TypeSArray)toStaticArrayType(se1);
                TypeSArray tsa2 = null;
                {
                    ArrayLiteralExp ale = e2x.isArrayLiteralExp();
                    if ((ale) != null)
                        tsa2 = (TypeSArray)t2.nextOf().sarrayOf((long)(ale.elements.value.get()).length.value);
                    else {
                        SliceExp se = e2x.isSliceExp();
                        if ((se) != null)
                            tsa2 = (TypeSArray)toStaticArrayType(se);
                        else
                            tsa2 = t2.isTypeSArray();
                    }
                }
                if ((tsa1 != null) && (tsa2 != null))
                {
                    long dim1 = tsa1.dim.value.toInteger();
                    long dim2 = tsa2.dim.value.toInteger();
                    if ((dim1 != dim2))
                    {
                        exp.error(new BytePtr("mismatched array lengths, %d and %d"), (int)dim1, (int)dim2);
                        this.setError();
                        return ;
                    }
                }
                if (((exp.op.value & 0xFF) != 96) && ((e2x.op.value & 0xFF) == 31) && ((UnaExp)e2x).e1.value.isLvalue() || ((e2x.op.value & 0xFF) == 12) && ((UnaExp)e2x).e1.value.isLvalue() || ((e2x.op.value & 0xFF) != 31) && e2x.isLvalue())
                {
                    if (exp.e1.value.checkPostblit(this.sc.value, t1.nextOf()))
                        this.setError();
                        return ;
                }
                if (false)
                {
                    BytePtr e1str = pcopy(exp.e1.value.toChars());
                    BytePtr e2str = pcopy(e2x.toChars());
                    exp.warning(new BytePtr("explicit element-wise assignment `%s = (%s)[]` is better than `%s = %s`"), e1str, e2str, e1str, e2str);
                }
                Type t2n = t2.nextOf();
                Type t1n = t1.nextOf();
                IntRef offset = ref(0);
                if (t2n.equivalent(t1n) || t1n.isBaseOf(t2n, ptr(offset)) && (offset.value == 0))
                {
                    if (isArrayOpValid(e2x))
                    {
                        e2x = e2x.copy();
                        e2x.type.value = exp.e1.value.type.value.constOf();
                    }
                    else
                        e2x = e2x.castTo(this.sc.value, exp.e1.value.type.value.constOf());
                }
                else
                {
                    if (((e2x.op.value & 0xFF) == 121))
                        e2x = e2x.implicitCastTo(this.sc.value, exp.e1.value.type.value.constOf());
                    else
                        e2x = e2x.implicitCastTo(this.sc.value, exp.e1.value.type.value);
                }
                if (((t1n.toBasetype().ty.value & 0xFF) == ENUMTY.Tvoid) && ((t2n.toBasetype().ty.value & 0xFF) == ENUMTY.Tvoid))
                {
                    if (((this.sc.value.get()).intypeof.value == 0) && ((this.sc.value.get()).func.value != null) && (this.sc.value.get()).func.value.setUnsafe() && (((this.sc.value.get()).flags.value & 8) == 0))
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
                    BytePtr e1str = pcopy(exp.e1.value.toChars());
                    BytePtr e2str = pcopy(e2x.toChars());
                    BytePtr atypestr = pcopy(((exp.e1.value.op.value & 0xFF) == 31) ? new BytePtr("element-wise") : new BytePtr("slice"));
                    exp.warning(new BytePtr("explicit %s assignment `%s = (%s)[]` is better than `%s = %s`"), atypestr, e1str, e2str, e1str, e2str);
                }
                if (((exp.op.value & 0xFF) == 96))
                    e2x = e2x.castTo(this.sc.value, exp.e1.value.type.value);
                else
                {
                    e2x = e2x.implicitCastTo(this.sc.value, exp.e1.value.type.value);
                    if (((e2x.op.value & 0xFF) == 127) && ((exp.op.value & 0xFF) == 95) && ((t1.ty.value & 0xFF) == ENUMTY.Tstruct))
                    {
                        StructDeclaration sd = ((TypeStruct)t1).sym.value;
                        Dsymbol opAssign = search_function(sd, Id.assign.value);
                        if (opAssign != null)
                        {
                            errorSupplemental(exp.loc.value, new BytePtr("`%s` is the first assignment of `%s` therefore it represents its initialization"), exp.toChars(), exp.e1.value.toChars());
                            errorSupplemental(exp.loc.value, new BytePtr("`opAssign` methods are not used for initialization, but for subsequent assignments"));
                        }
                    }
                }
            }
            if (((e2x.op.value & 0xFF) == 127))
            {
                this.result.value = e2x;
                return ;
            }
            exp.e2.value = e2x;
            t2 = exp.e2.value.type.value.toBasetype();
            if (((t2.ty.value & 0xFF) == ENUMTY.Tarray) || ((t2.ty.value & 0xFF) == ENUMTY.Tsarray) && isArrayOpValid(exp.e2.value))
            {
                if (((exp.memset & MemorySet.blockAssign) == 0) && ((exp.e1.value.op.value & 0xFF) == 31) && isUnaArrayOp(exp.e2.value.op.value) || isBinArrayOp(exp.e2.value.op.value))
                {
                    exp.type.value = exp.e1.value.type.value;
                    if (((exp.op.value & 0xFF) == 95))
                        exp.e1.value.type.value = exp.e1.value.type.value.nextOf().mutableOf().arrayOf();
                    this.result.value = arrayOp((BinExp)exp, this.sc.value);
                    return ;
                }
                if (checkNonAssignmentArrayOp(exp.e2.value, ((exp.memset & MemorySet.blockAssign) == 0) && ((exp.op.value & 0xFF) == 90)))
                    this.setError();
                    return ;
            }
            if (((exp.e1.value.op.value & 0xFF) == 26) && ((exp.op.value & 0xFF) == 90))
            {
                VarExp ve = (VarExp)exp.e1.value;
                VarDeclaration vd = ve.var.value.isVarDeclaration();
                if ((vd != null) && vd.onstack || vd.mynew)
                {
                    assert(((t1.ty.value & 0xFF) == ENUMTY.Tclass));
                    exp.error(new BytePtr("cannot rebind scope variables"));
                }
            }
            if (((exp.e1.value.op.value & 0xFF) == 26) && (pequals(((VarExp)exp.e1.value).var.value.ident.value, Id.ctfe.value)))
            {
                exp.error(new BytePtr("cannot modify compiler-generated variable `__ctfe`"));
            }
            exp.type.value = exp.e1.value.type.value;
            assert(exp.type.value != null);
            Expression res = ((exp.op.value & 0xFF) == 90) ? exp.reorderSettingAAElem(this.sc.value) : exp;
            checkAssignEscape(this.sc.value, res, false);
            setResult.invoke(res, 9001);
            return ;
        }

        public  void visit(PowAssignExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            if (exp.e1.value.checkReadModifyWrite(exp.op.value, exp.e2.value))
                this.setError();
                return ;
            assert((exp.e1.value.type.value != null) && (exp.e2.value.type.value != null));
            if (((exp.e1.value.op.value & 0xFF) == 31) || ((exp.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tarray) || ((exp.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                if (checkNonAssignmentArrayOp(exp.e1.value, false))
                    this.setError();
                    return ;
                if (exp.e2.value.implicitConvTo(exp.e1.value.type.value.nextOf()) != 0)
                {
                    exp.e2.value = exp.e2.value.castTo(this.sc.value, exp.e1.value.type.value.nextOf());
                }
                else {
                    Expression ex = typeCombine(exp, this.sc.value);
                    if ((ex) != null)
                    {
                        this.result.value = ex;
                        return ;
                    }
                }
                Type tb1 = exp.e1.value.type.value.nextOf().toBasetype();
                Type tb2 = exp.e2.value.type.value.toBasetype();
                if (((tb2.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb2.ty.value & 0xFF) == ENUMTY.Tsarray))
                    tb2 = tb2.nextOf().toBasetype();
                if (tb1.isintegral() || tb1.isfloating() && tb2.isintegral() || tb2.isfloating())
                {
                    exp.type.value = exp.e1.value.type.value;
                    this.result.value = arrayOp(exp, this.sc.value);
                    return ;
                }
            }
            else
            {
                exp.e1.value = exp.e1.value.modifiableLvalue(this.sc.value, exp.e1.value);
            }
            if (exp.e1.value.type.value.isintegral() || exp.e1.value.type.value.isfloating() && exp.e2.value.type.value.isintegral() || exp.e2.value.type.value.isfloating())
            {
                Ref<Expression> e0 = ref(null);
                e = exp.reorderSettingAAElem(this.sc.value);
                e = Expression.extractLast(e, e0);
                assert((pequals(e, exp)));
                if (((exp.e1.value.op.value & 0xFF) == 26))
                {
                    e = new PowExp(exp.loc.value, exp.e1.value.syntaxCopy(), exp.e2.value);
                    e = new AssignExp(exp.loc.value, exp.e1.value, e);
                }
                else
                {
                    VarDeclaration v = copyToTemp(2097152L, new BytePtr("__powtmp"), exp.e1.value);
                    DeclarationExp de = new DeclarationExp(exp.e1.value.loc.value, v);
                    VarExp ve = new VarExp(exp.e1.value.loc.value, v, true);
                    e = new PowExp(exp.loc.value, ve, exp.e2.value);
                    e = new AssignExp(exp.loc.value, new VarExp(exp.e1.value.loc.value, v, true), e);
                    e = new CommaExp(exp.loc.value, de, e, true);
                }
                e = Expression.combine(e0.value, e);
                e = expressionSemantic(e, this.sc.value);
                this.result.value = e;
                return ;
            }
            this.result.value = exp.incompatibleTypes();
        }

        public  void visit(CatAssignExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            if (((exp.e1.value.op.value & 0xFF) == 31))
            {
                SliceExp se = (SliceExp)exp.e1.value;
                if (((se.e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray))
                {
                    exp.error(new BytePtr("cannot append to static array `%s`"), se.e1.value.type.value.toChars());
                    this.setError();
                    return ;
                }
            }
            exp.e1.value = exp.e1.value.modifiableLvalue(this.sc.value, exp.e1.value);
            if (((exp.e1.value.op.value & 0xFF) == 127))
            {
                this.result.value = exp.e1.value;
                return ;
            }
            if (((exp.e2.value.op.value & 0xFF) == 127))
            {
                this.result.value = exp.e2.value;
                return ;
            }
            if (checkNonAssignmentArrayOp(exp.e2.value, false))
                this.setError();
                return ;
            Type tb1 = exp.e1.value.type.value.toBasetype();
            Type tb1next = tb1.nextOf();
            Type tb2 = exp.e2.value.type.value.toBasetype();
            if (((tb1.ty.value & 0xFF) == ENUMTY.Tarray) && ((tb2.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb2.ty.value & 0xFF) == ENUMTY.Tsarray) && (exp.e2.value.implicitConvTo(exp.e1.value.type.value) != 0) || (tb2.nextOf().implicitConvTo(tb1next) != 0) && (tb2.nextOf().size(Loc.initial.value) == tb1next.size(Loc.initial.value)))
            {
                assert(((exp.op.value & 0xFF) == 71));
                if (exp.e1.value.checkPostblit(this.sc.value, tb1next))
                    this.setError();
                    return ;
                exp.e2.value = exp.e2.value.castTo(this.sc.value, exp.e1.value.type.value);
            }
            else if (((tb1.ty.value & 0xFF) == ENUMTY.Tarray) && (exp.e2.value.implicitConvTo(tb1next) != 0))
            {
                if (((tb2.ty.value & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)tb2).implicitConvToThroughAliasThis(tb1next) != 0))
                    /*goto Laliasthis*//*unrolled goto*/
                    exp = new CatDcharAssignExp(exp.loc.value, exp.type.value, exp.e1.value, exp.e2.value.castTo(this.sc.value, Type.tdchar));
                if (((tb2.ty.value & 0xFF) == ENUMTY.Tclass) && (((TypeClass)tb2).implicitConvToThroughAliasThis(tb1next) != 0))
                    /*goto Laliasthis*//*unrolled goto*/
                    exp = new CatDcharAssignExp(exp.loc.value, exp.type.value, exp.e1.value, exp.e2.value.castTo(this.sc.value, Type.tdchar));
                if (exp.e2.value.checkPostblit(this.sc.value, tb2))
                    this.setError();
                    return ;
                if (checkNewEscape(this.sc.value, exp.e2.value, false))
                    this.setError();
                    return ;
                exp = new CatElemAssignExp(exp.loc.value, exp.type.value, exp.e1.value, exp.e2.value.castTo(this.sc.value, tb1next));
                exp.e2.value = doCopyOrMove(this.sc.value, exp.e2.value, null);
            }
            else if (((tb1.ty.value & 0xFF) == ENUMTY.Tarray) && ((tb1next.ty.value & 0xFF) == ENUMTY.Tchar) || ((tb1next.ty.value & 0xFF) == ENUMTY.Twchar) && ((exp.e2.value.type.value.ty.value & 0xFF) != (tb1next.ty.value & 0xFF)) && (exp.e2.value.implicitConvTo(Type.tdchar) != 0))
            {
                exp = new CatDcharAssignExp(exp.loc.value, exp.type.value, exp.e1.value, exp.e2.value.castTo(this.sc.value, Type.tdchar));
            }
            else
            {
                Function2<BinAssignExp,Ptr<Scope>,Expression> tryAliasThisForLhs = new Function2<BinAssignExp,Ptr<Scope>,Expression>(){
                    public Expression invoke(BinAssignExp exp, Ptr<Scope> sc) {
                        Ref<Ptr<Scope>> sc_ref = ref(sc);
                        Ref<AggregateDeclaration> ad1 = ref(isAggregate(exp.e1.value.type.value));
                        if ((ad1.value == null) || (ad1.value.aliasthis.value == null))
                            return null;
                        if ((exp.att1.value != null) && (pequals(exp.e1.value.type.value, exp.att1.value)))
                            return null;
                        Ref<Expression> e1 = ref(new DotIdExp(exp.loc.value, exp.e1.value, ad1.value.aliasthis.value.ident.value));
                        Ref<BinExp> be = ref((BinExp)exp.copy());
                        if ((be.value.att1.value == null) && exp.e1.value.type.value.checkAliasThisRec())
                            be.value.att1.value = exp.e1.value.type.value;
                        be.value.e1.value = e1.value;
                        return trySemantic(be.value, sc_ref.value);
                    }
                };
                Function2<BinAssignExp,Ptr<Scope>,Expression> tryAliasThisForRhs = new Function2<BinAssignExp,Ptr<Scope>,Expression>(){
                    public Expression invoke(BinAssignExp exp, Ptr<Scope> sc) {
                        Ref<Ptr<Scope>> sc_ref = ref(sc);
                        Ref<AggregateDeclaration> ad2 = ref(isAggregate(exp.e2.value.type.value));
                        if ((ad2.value == null) || (ad2.value.aliasthis.value == null))
                            return null;
                        if ((exp.att2.value != null) && (pequals(exp.e2.value.type.value, exp.att2.value)))
                            return null;
                        Ref<Expression> e2 = ref(new DotIdExp(exp.loc.value, exp.e2.value, ad2.value.aliasthis.value.ident.value));
                        Ref<BinExp> be = ref((BinExp)exp.copy());
                        if ((be.value.att2.value == null) && exp.e2.value.type.value.checkAliasThisRec())
                            be.value.att2.value = exp.e2.value.type.value;
                        be.value.e2.value = e2.value;
                        return trySemantic(be.value, sc_ref.value);
                    }
                };
            /*Laliasthis:*/
                this.result.value = tryAliasThisForLhs.invoke(exp, this.sc.value);
                if (this.result.value != null)
                    return ;
                this.result.value = tryAliasThisForRhs.invoke(exp, this.sc.value);
                if (this.result.value != null)
                    return ;
                exp.error(new BytePtr("cannot append type `%s` to type `%s`"), tb2.toChars(), tb1.toChars());
                this.setError();
                return ;
            }
            if (exp.e2.value.checkValue())
                this.setError();
                return ;
            exp.type.value = exp.e1.value.type.value;
            Expression res = exp.reorderSettingAAElem(this.sc.value);
            if (((exp.op.value & 0xFF) == 72) || ((exp.op.value & 0xFF) == 73) && global.params.vsafe.value)
                checkAssignEscape(this.sc.value, res, false);
            this.result.value = res;
        }

        public  void visit(AddExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            Type tb1 = exp.e1.value.type.value.toBasetype();
            Type tb2 = exp.e2.value.type.value.toBasetype();
            boolean err = false;
            if (((tb1.ty.value & 0xFF) == ENUMTY.Tdelegate) || ((tb1.ty.value & 0xFF) == ENUMTY.Tpointer) && ((tb1.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                (err ? 1 : 0) |= (exp.e1.value.checkArithmetic() ? 1 : 0);
            }
            if (((tb2.ty.value & 0xFF) == ENUMTY.Tdelegate) || ((tb2.ty.value & 0xFF) == ENUMTY.Tpointer) && ((tb2.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                (err ? 1 : 0) |= (exp.e2.value.checkArithmetic() ? 1 : 0);
            }
            if (err)
                this.setError();
                return ;
            if (((tb1.ty.value & 0xFF) == ENUMTY.Tpointer) && exp.e2.value.type.value.isintegral() || ((tb2.ty.value & 0xFF) == ENUMTY.Tpointer) && exp.e1.value.type.value.isintegral())
            {
                this.result.value = scaleFactor(exp, this.sc.value);
                return ;
            }
            if (((tb1.ty.value & 0xFF) == ENUMTY.Tpointer) && ((tb2.ty.value & 0xFF) == ENUMTY.Tpointer))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            {
                Expression ex = typeCombine(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Type tb = exp.type.value.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                if (!isArrayOpValid(exp))
                {
                    this.result.value = arrayOpInvalidError(exp);
                    return ;
                }
                this.result.value = exp;
                return ;
            }
            tb1 = exp.e1.value.type.value.toBasetype();
            if (!target.isVectorOpSupported(tb1, exp.op.value, tb2))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            if (tb1.isreal() && exp.e2.value.type.value.isimaginary() || tb1.isimaginary() && exp.e2.value.type.value.isreal())
            {
                switch ((exp.type.value.toBasetype().ty.value & 0xFF))
                {
                    case 21:
                    case 24:
                        exp.type.value = Type.tcomplex32;
                        break;
                    case 22:
                    case 25:
                        exp.type.value = Type.tcomplex64;
                        break;
                    case 23:
                    case 26:
                        exp.type.value = Type.tcomplex80;
                        break;
                    default:
                    throw new AssertionError("Unreachable code!");
                }
            }
            this.result.value = exp;
        }

        public  void visit(MinExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            Type t1 = exp.e1.value.type.value.toBasetype();
            Type t2 = exp.e2.value.type.value.toBasetype();
            boolean err = false;
            if (((t1.ty.value & 0xFF) == ENUMTY.Tdelegate) || ((t1.ty.value & 0xFF) == ENUMTY.Tpointer) && ((t1.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                (err ? 1 : 0) |= (exp.e1.value.checkArithmetic() ? 1 : 0);
            }
            if (((t2.ty.value & 0xFF) == ENUMTY.Tdelegate) || ((t2.ty.value & 0xFF) == ENUMTY.Tpointer) && ((t2.nextOf().ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                (err ? 1 : 0) |= (exp.e2.value.checkArithmetic() ? 1 : 0);
            }
            if (err)
                this.setError();
                return ;
            if (((t1.ty.value & 0xFF) == ENUMTY.Tpointer))
            {
                if (((t2.ty.value & 0xFF) == ENUMTY.Tpointer))
                {
                    Type p1 = t1.nextOf();
                    Type p2 = t2.nextOf();
                    if (!p1.equivalent(p2))
                    {
                        deprecation(exp.loc.value, new BytePtr("cannot subtract pointers to different types: `%s` and `%s`."), t1.toChars(), t2.toChars());
                    }
                    long stride = 0L;
                    {
                        Expression ex = typeCombine(exp, this.sc.value);
                        if ((ex) != null)
                        {
                            this.result.value = ex;
                            return ;
                        }
                    }
                    exp.type.value = Type.tptrdiff_t;
                    stride = (long)t2.nextOf().size();
                    if ((stride == 0L))
                    {
                        e = new IntegerExp(exp.loc.value, 0L, Type.tptrdiff_t);
                    }
                    else
                    {
                        e = new DivExp(exp.loc.value, exp, new IntegerExp(Loc.initial.value, (long)stride, Type.tptrdiff_t));
                        e.type.value = Type.tptrdiff_t;
                    }
                }
                else if (t2.isintegral())
                    e = scaleFactor(exp, this.sc.value);
                else
                {
                    exp.error(new BytePtr("can't subtract `%s` from pointer"), t2.toChars());
                    e = new ErrorExp();
                }
                this.result.value = e;
                return ;
            }
            if (((t2.ty.value & 0xFF) == ENUMTY.Tpointer))
            {
                exp.type.value = exp.e2.value.type.value;
                exp.error(new BytePtr("can't subtract pointer from `%s`"), exp.e1.value.type.value.toChars());
                this.setError();
                return ;
            }
            {
                Expression ex = typeCombine(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Type tb = exp.type.value.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                if (!isArrayOpValid(exp))
                {
                    this.result.value = arrayOpInvalidError(exp);
                    return ;
                }
                this.result.value = exp;
                return ;
            }
            t1 = exp.e1.value.type.value.toBasetype();
            t2 = exp.e2.value.type.value.toBasetype();
            if (!target.isVectorOpSupported(t1, exp.op.value, t2))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            if (t1.isreal() && t2.isimaginary() || t1.isimaginary() && t2.isreal())
            {
                switch ((exp.type.value.ty.value & 0xFF))
                {
                    case 21:
                    case 24:
                        exp.type.value = Type.tcomplex32;
                        break;
                    case 22:
                    case 25:
                        exp.type.value = Type.tcomplex64;
                        break;
                    case 23:
                    case 26:
                        exp.type.value = Type.tcomplex80;
                        break;
                    default:
                    throw new AssertionError("Unreachable code!");
                }
            }
            this.result.value = exp;
            return ;
        }

        public  void visit(CatExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            Type tb1 = exp.e1.value.type.value.toBasetype();
            Type tb2 = exp.e2.value.type.value.toBasetype();
            boolean f1 = checkNonAssignmentArrayOp(exp.e1.value, false);
            boolean f2 = checkNonAssignmentArrayOp(exp.e2.value, false);
            if (f1 || f2)
                this.setError();
                return ;
            Type tb1next = tb1.nextOf();
            Type tb2next = tb2.nextOf();
            try {
                if ((tb1next != null) && (tb2next != null) && (tb1next.implicitConvTo(tb2next) >= MATCH.constant) || (tb2next.implicitConvTo(tb1next) >= MATCH.constant) || ((exp.e1.value.op.value & 0xFF) == 47) && (exp.e1.value.implicitConvTo(tb2) != 0) || ((exp.e2.value.op.value & 0xFF) == 47) && (exp.e2.value.implicitConvTo(tb1) != 0))
                {
                    /*goto Lpeer*/throw Dispatch0.INSTANCE;
                }
                if (((tb1.ty.value & 0xFF) == ENUMTY.Tsarray) || ((tb1.ty.value & 0xFF) == ENUMTY.Tarray) && ((tb2.ty.value & 0xFF) != ENUMTY.Tvoid))
                {
                    if (((exp.e1.value.op.value & 0xFF) == 47))
                    {
                        exp.e2.value = doCopyOrMove(this.sc.value, exp.e2.value, null);
                    }
                    else if (((exp.e1.value.op.value & 0xFF) == 121))
                    {
                    }
                    else
                    {
                        if (exp.e2.value.checkPostblit(this.sc.value, tb2))
                            this.setError();
                            return ;
                    }
                    if (((exp.e1.value.op.value & 0xFF) == 47) && (exp.e1.value.implicitConvTo(tb2.arrayOf()) != 0))
                    {
                        exp.e1.value = exp.e1.value.implicitCastTo(this.sc.value, tb2.arrayOf());
                        exp.type.value = tb2.arrayOf();
                        /*goto L2elem*//*unrolled goto*/
                    /*L2elem:*/
                        if (((tb2.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb2.ty.value & 0xFF) == ENUMTY.Tsarray))
                        {
                            exp.e2.value = new ArrayLiteralExp(exp.e2.value.loc.value, exp.type.value, exp.e2.value);
                        }
                        else if (checkNewEscape(this.sc.value, exp.e2.value, false))
                            this.setError();
                            return ;
                        this.result.value = exp.optimize(0, false);
                        return ;
                    }
                    if ((exp.e2.value.implicitConvTo(tb1next) >= MATCH.convert))
                    {
                        exp.e2.value = exp.e2.value.implicitCastTo(this.sc.value, tb1next);
                        exp.type.value = tb1next.arrayOf();
                    /*L2elem:*/
                        if (((tb2.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb2.ty.value & 0xFF) == ENUMTY.Tsarray))
                        {
                            exp.e2.value = new ArrayLiteralExp(exp.e2.value.loc.value, exp.type.value, exp.e2.value);
                        }
                        else if (checkNewEscape(this.sc.value, exp.e2.value, false))
                            this.setError();
                            return ;
                        this.result.value = exp.optimize(0, false);
                        return ;
                    }
                }
                if (((tb2.ty.value & 0xFF) == ENUMTY.Tsarray) || ((tb2.ty.value & 0xFF) == ENUMTY.Tarray) && ((tb1.ty.value & 0xFF) != ENUMTY.Tvoid))
                {
                    if (((exp.e2.value.op.value & 0xFF) == 47))
                    {
                        exp.e1.value = doCopyOrMove(this.sc.value, exp.e1.value, null);
                    }
                    else if (((exp.e2.value.op.value & 0xFF) == 121))
                    {
                    }
                    else
                    {
                        if (exp.e1.value.checkPostblit(this.sc.value, tb1))
                            this.setError();
                            return ;
                    }
                    if (((exp.e2.value.op.value & 0xFF) == 47) && (exp.e2.value.implicitConvTo(tb1.arrayOf()) != 0))
                    {
                        exp.e2.value = exp.e2.value.implicitCastTo(this.sc.value, tb1.arrayOf());
                        exp.type.value = tb1.arrayOf();
                        /*goto L1elem*//*unrolled goto*/
                    /*L1elem:*/
                        if (((tb1.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb1.ty.value & 0xFF) == ENUMTY.Tsarray))
                        {
                            exp.e1.value = new ArrayLiteralExp(exp.e1.value.loc.value, exp.type.value, exp.e1.value);
                        }
                        else if (checkNewEscape(this.sc.value, exp.e1.value, false))
                            this.setError();
                            return ;
                        this.result.value = exp.optimize(0, false);
                        return ;
                    }
                    if ((exp.e1.value.implicitConvTo(tb2next) >= MATCH.convert))
                    {
                        exp.e1.value = exp.e1.value.implicitCastTo(this.sc.value, tb2next);
                        exp.type.value = tb2next.arrayOf();
                    /*L1elem:*/
                        if (((tb1.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb1.ty.value & 0xFF) == ENUMTY.Tsarray))
                        {
                            exp.e1.value = new ArrayLiteralExp(exp.e1.value.loc.value, exp.type.value, exp.e1.value);
                        }
                        else if (checkNewEscape(this.sc.value, exp.e1.value, false))
                            this.setError();
                            return ;
                        this.result.value = exp.optimize(0, false);
                        return ;
                    }
                }
            }
            catch(Dispatch0 __d){}
        /*Lpeer:*/
            if (((tb1.ty.value & 0xFF) == ENUMTY.Tsarray) || ((tb1.ty.value & 0xFF) == ENUMTY.Tarray) && ((tb2.ty.value & 0xFF) == ENUMTY.Tsarray) || ((tb2.ty.value & 0xFF) == ENUMTY.Tarray) && (tb1next.mod.value != 0) || (tb2next.mod.value != 0) && ((tb1next.mod.value & 0xFF) != (tb2next.mod.value & 0xFF)))
            {
                Type t1 = tb1next.mutableOf().constOf().arrayOf();
                Type t2 = tb2next.mutableOf().constOf().arrayOf();
                if (((exp.e1.value.op.value & 0xFF) == 121) && (((StringExp)exp.e1.value).committed.value == 0))
                    exp.e1.value.type.value = t1;
                else
                    exp.e1.value = exp.e1.value.castTo(this.sc.value, t1);
                if (((exp.e2.value.op.value & 0xFF) == 121) && (((StringExp)exp.e2.value).committed.value == 0))
                    exp.e2.value.type.value = t2;
                else
                    exp.e2.value = exp.e2.value.castTo(this.sc.value, t2);
            }
            {
                Expression ex = typeCombine(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            exp.type.value = exp.type.value.toHeadMutable();
            Type tb = exp.type.value.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
                exp.type.value = tb.nextOf().arrayOf();
            if (((exp.type.value.ty.value & 0xFF) == ENUMTY.Tarray) && (tb1next != null) && (tb2next != null) && ((tb1next.mod.value & 0xFF) != (tb2next.mod.value & 0xFF)))
            {
                exp.type.value = exp.type.value.nextOf().toHeadMutable().arrayOf();
            }
            {
                Type tbn = tb.nextOf();
                if ((tbn) != null)
                {
                    if (exp.checkPostblit(this.sc.value, tbn))
                        this.setError();
                        return ;
                }
            }
            Type t1 = exp.e1.value.type.value.toBasetype();
            Type t2 = exp.e2.value.type.value.toBasetype();
            if (((t1.ty.value & 0xFF) == ENUMTY.Tarray) || ((t1.ty.value & 0xFF) == ENUMTY.Tsarray) && ((t2.ty.value & 0xFF) == ENUMTY.Tarray) || ((t2.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                e = exp.optimize(0, false);
            }
            else
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            this.result.value = e;
        }

        public  void visit(MulExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            {
                Expression ex = typeCombine(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Type tb = exp.type.value.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                if (!isArrayOpValid(exp))
                {
                    this.result.value = arrayOpInvalidError(exp);
                    return ;
                }
                this.result.value = exp;
                return ;
            }
            if (exp.checkArithmeticBin())
                this.setError();
                return ;
            if (exp.type.value.isfloating())
            {
                Type t1 = exp.e1.value.type.value;
                Type t2 = exp.e2.value.type.value;
                if (t1.isreal())
                {
                    exp.type.value = t2;
                }
                else if (t2.isreal())
                {
                    exp.type.value = t1;
                }
                else if (t1.isimaginary())
                {
                    if (t2.isimaginary())
                    {
                        switch ((t1.toBasetype().ty.value & 0xFF))
                        {
                            case 24:
                                exp.type.value = Type.tfloat32.value;
                                break;
                            case 25:
                                exp.type.value = Type.tfloat64.value;
                                break;
                            case 26:
                                exp.type.value = Type.tfloat80.value;
                                break;
                            default:
                            throw new AssertionError("Unreachable code!");
                        }
                        exp.e1.value.type.value = exp.type.value;
                        exp.e2.value.type.value = exp.type.value;
                        e = new NegExp(exp.loc.value, exp);
                        e = expressionSemantic(e, this.sc.value);
                        this.result.value = e;
                        return ;
                    }
                    else
                        exp.type.value = t2;
                }
                else if (t2.isimaginary())
                {
                    exp.type.value = t1;
                }
            }
            else if (!target.isVectorOpSupported(tb, exp.op.value, exp.e2.value.type.value.toBasetype()))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            this.result.value = exp;
        }

        public  void visit(DivExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            {
                Expression ex = typeCombine(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Type tb = exp.type.value.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                if (!isArrayOpValid(exp))
                {
                    this.result.value = arrayOpInvalidError(exp);
                    return ;
                }
                this.result.value = exp;
                return ;
            }
            if (exp.checkArithmeticBin())
                this.setError();
                return ;
            if (exp.type.value.isfloating())
            {
                Type t1 = exp.e1.value.type.value;
                Type t2 = exp.e2.value.type.value;
                if (t1.isreal())
                {
                    exp.type.value = t2;
                    if (t2.isimaginary())
                    {
                        exp.e2.value.type.value = t1;
                        e = new NegExp(exp.loc.value, exp);
                        e = expressionSemantic(e, this.sc.value);
                        this.result.value = e;
                        return ;
                    }
                }
                else if (t2.isreal())
                {
                    exp.type.value = t1;
                }
                else if (t1.isimaginary())
                {
                    if (t2.isimaginary())
                    {
                        switch ((t1.toBasetype().ty.value & 0xFF))
                        {
                            case 24:
                                exp.type.value = Type.tfloat32.value;
                                break;
                            case 25:
                                exp.type.value = Type.tfloat64.value;
                                break;
                            case 26:
                                exp.type.value = Type.tfloat80.value;
                                break;
                            default:
                            throw new AssertionError("Unreachable code!");
                        }
                    }
                    else
                        exp.type.value = t2;
                }
                else if (t2.isimaginary())
                {
                    exp.type.value = t1;
                }
            }
            else if (!target.isVectorOpSupported(tb, exp.op.value, exp.e2.value.type.value.toBasetype()))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            this.result.value = exp;
        }

        public  void visit(ModExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            {
                Expression ex = typeCombine(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Type tb = exp.type.value.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                if (!isArrayOpValid(exp))
                {
                    this.result.value = arrayOpInvalidError(exp);
                    return ;
                }
                this.result.value = exp;
                return ;
            }
            if (!target.isVectorOpSupported(tb, exp.op.value, exp.e2.value.type.value.toBasetype()))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            if (exp.checkArithmeticBin())
                this.setError();
                return ;
            if (exp.type.value.isfloating())
            {
                exp.type.value = exp.e1.value.type.value;
                if (exp.e2.value.type.value.iscomplex())
                {
                    exp.error(new BytePtr("cannot perform modulo complex arithmetic"));
                    this.setError();
                    return ;
                }
            }
            this.result.value = exp;
        }

        public  void visit(PowExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            {
                Expression ex = typeCombine(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Type tb = exp.type.value.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                if (!isArrayOpValid(exp))
                {
                    this.result.value = arrayOpInvalidError(exp);
                    return ;
                }
                this.result.value = exp;
                return ;
            }
            if (exp.checkArithmeticBin())
                this.setError();
                return ;
            if (!target.isVectorOpSupported(tb, exp.op.value, exp.e2.value.type.value.toBasetype()))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            e = exp.optimize(0, false);
            if (((e.op.value & 0xFF) != 226))
            {
                e = expressionSemantic(e, this.sc.value);
                this.result.value = e;
                return ;
            }
            long intpow = 0L;
            if (((exp.e2.value.op.value & 0xFF) == 135) && ((long)exp.e2.value.toInteger() == 2L) || ((long)exp.e2.value.toInteger() == 3L))
                intpow = (long)exp.e2.value.toInteger();
            else if (((exp.e2.value.op.value & 0xFF) == 140) && (exp.e2.value.toReal() == (double)(long)exp.e2.value.toReal()))
                intpow = (long)exp.e2.value.toReal();
            if ((intpow == 2L) || (intpow == 3L))
            {
                VarDeclaration tmp = copyToTemp(0L, new BytePtr("__powtmp"), exp.e1.value);
                Expression de = new DeclarationExp(exp.loc.value, tmp);
                Expression ve = new VarExp(exp.loc.value, tmp, true);
                Expression me = new MulExp(exp.loc.value, ve, ve);
                if ((intpow == 3L))
                    me = new MulExp(exp.loc.value, me, ve);
                e = new CommaExp(exp.loc.value, de, me, true);
                e = expressionSemantic(e, this.sc.value);
                this.result.value = e;
                return ;
            }
            dmodule.Module mmath = loadStdMath();
            if (mmath == null)
            {
                e.error(new BytePtr("`%s` requires `std.math` for `^^` operators"), e.toChars());
                this.setError();
                return ;
            }
            e = new ScopeExp(exp.loc.value, mmath);
            if (((exp.e2.value.op.value & 0xFF) == 140) && (exp.e2.value.toReal() == CTFloat.half))
            {
                e = new CallExp(exp.loc.value, new DotIdExp(exp.loc.value, e, Id._sqrt), exp.e1.value);
            }
            else
            {
                e = new CallExp(exp.loc.value, new DotIdExp(exp.loc.value, e, Id._pow), exp.e1.value, exp.e2.value);
            }
            e = expressionSemantic(e, this.sc.value);
            this.result.value = e;
            return ;
        }

        public  void visit(ShlExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            if (exp.checkIntegralBin())
                this.setError();
                return ;
            if (!target.isVectorOpSupported(exp.e1.value.type.value.toBasetype(), exp.op.value, exp.e2.value.type.value.toBasetype()))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            exp.e1.value = integralPromotions(exp.e1.value, this.sc.value);
            if (((exp.e2.value.type.value.toBasetype().ty.value & 0xFF) != ENUMTY.Tvector))
                exp.e2.value = exp.e2.value.castTo(this.sc.value, Type.tshiftcnt.value);
            exp.type.value = exp.e1.value.type.value;
            this.result.value = exp;
        }

        public  void visit(ShrExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            if (exp.checkIntegralBin())
                this.setError();
                return ;
            if (!target.isVectorOpSupported(exp.e1.value.type.value.toBasetype(), exp.op.value, exp.e2.value.type.value.toBasetype()))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            exp.e1.value = integralPromotions(exp.e1.value, this.sc.value);
            if (((exp.e2.value.type.value.toBasetype().ty.value & 0xFF) != ENUMTY.Tvector))
                exp.e2.value = exp.e2.value.castTo(this.sc.value, Type.tshiftcnt.value);
            exp.type.value = exp.e1.value.type.value;
            this.result.value = exp;
        }

        public  void visit(UshrExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            if (exp.checkIntegralBin())
                this.setError();
                return ;
            if (!target.isVectorOpSupported(exp.e1.value.type.value.toBasetype(), exp.op.value, exp.e2.value.type.value.toBasetype()))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            exp.e1.value = integralPromotions(exp.e1.value, this.sc.value);
            if (((exp.e2.value.type.value.toBasetype().ty.value & 0xFF) != ENUMTY.Tvector))
                exp.e2.value = exp.e2.value.castTo(this.sc.value, Type.tshiftcnt.value);
            exp.type.value = exp.e1.value.type.value;
            this.result.value = exp;
        }

        public  void visit(AndExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            if (((exp.e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tbool) && ((exp.e2.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tbool))
            {
                exp.type.value = exp.e1.value.type.value;
                this.result.value = exp;
                return ;
            }
            {
                Expression ex = typeCombine(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Type tb = exp.type.value.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                if (!isArrayOpValid(exp))
                {
                    this.result.value = arrayOpInvalidError(exp);
                    return ;
                }
                this.result.value = exp;
                return ;
            }
            if (!target.isVectorOpSupported(tb, exp.op.value, exp.e2.value.type.value.toBasetype()))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            if (exp.checkIntegralBin())
                this.setError();
                return ;
            this.result.value = exp;
        }

        public  void visit(OrExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            if (((exp.e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tbool) && ((exp.e2.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tbool))
            {
                exp.type.value = exp.e1.value.type.value;
                this.result.value = exp;
                return ;
            }
            {
                Expression ex = typeCombine(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Type tb = exp.type.value.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                if (!isArrayOpValid(exp))
                {
                    this.result.value = arrayOpInvalidError(exp);
                    return ;
                }
                this.result.value = exp;
                return ;
            }
            if (!target.isVectorOpSupported(tb, exp.op.value, exp.e2.value.type.value.toBasetype()))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            if (exp.checkIntegralBin())
                this.setError();
                return ;
            this.result.value = exp;
        }

        public  void visit(XorExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            if (((exp.e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tbool) && ((exp.e2.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tbool))
            {
                exp.type.value = exp.e1.value.type.value;
                this.result.value = exp;
                return ;
            }
            {
                Expression ex = typeCombine(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Type tb = exp.type.value.toBasetype();
            if (((tb.ty.value & 0xFF) == ENUMTY.Tarray) || ((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                if (!isArrayOpValid(exp))
                {
                    this.result.value = arrayOpInvalidError(exp);
                    return ;
                }
                this.result.value = exp;
                return ;
            }
            if (!target.isVectorOpSupported(tb, exp.op.value, exp.e2.value.type.value.toBasetype()))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            if (exp.checkIntegralBin())
                this.setError();
                return ;
            this.result.value = exp;
        }

        public  void visit(LogicalExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            exp.setNoderefOperands();
            Expression e1x = expressionSemantic(exp.e1.value, this.sc.value);
            if (((e1x.op.value & 0xFF) == 20))
                e1x = resolveAliasThis(this.sc.value, e1x, false);
            e1x = resolveProperties(this.sc.value, e1x);
            e1x = e1x.toBoolean(this.sc.value);
            if (((this.sc.value.get()).flags.value & 4) != 0)
            {
                e1x = e1x.optimize(0, false);
                if (e1x.isBool((exp.op.value & 0xFF) == 102))
                {
                    this.result.value = new IntegerExp(exp.loc.value, (((exp.op.value & 0xFF) == 102) ? 1 : 0), Type.tbool.value);
                    return ;
                }
            }
            CtorFlow ctorflow = (this.sc.value.get()).ctorflow.clone().copy();
            Expression e2x = expressionSemantic(exp.e2.value, this.sc.value);
            (this.sc.value.get()).merge(exp.loc.value, ctorflow);
            ctorflow.freeFieldinit();
            if (((e2x.op.value & 0xFF) == 20))
                e2x = resolveAliasThis(this.sc.value, e2x, false);
            e2x = resolveProperties(this.sc.value, e2x);
            boolean f1 = checkNonAssignmentArrayOp(e1x, false);
            boolean f2 = checkNonAssignmentArrayOp(e2x, false);
            if (f1 || f2)
                this.setError();
                return ;
            if (((e2x.type.value.ty.value & 0xFF) != ENUMTY.Tvoid))
                e2x = e2x.toBoolean(this.sc.value);
            if (((e2x.op.value & 0xFF) == 20) || ((e2x.op.value & 0xFF) == 203))
            {
                exp.error(new BytePtr("`%s` is not an expression"), exp.e2.value.toChars());
                this.setError();
                return ;
            }
            if (((e1x.op.value & 0xFF) == 127))
            {
                this.result.value = e1x;
                return ;
            }
            if (((e2x.op.value & 0xFF) == 127))
            {
                this.result.value = e2x;
                return ;
            }
            if (((e2x.type.value.ty.value & 0xFF) == ENUMTY.Tvoid))
                exp.type.value = Type.tvoid.value;
            else
                exp.type.value = Type.tbool.value;
            exp.e1.value = e1x;
            exp.e2.value = e2x;
            this.result.value = exp;
        }

        public  void visit(CmpExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            exp.setNoderefOperands();
            {
                Expression ex = binSemanticProp(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Type t1 = exp.e1.value.type.value.toBasetype();
            Type t2 = exp.e2.value.type.value.toBasetype();
            if (((t1.ty.value & 0xFF) == ENUMTY.Tclass) && ((exp.e2.value.op.value & 0xFF) == 13) || ((t2.ty.value & 0xFF) == ENUMTY.Tclass) && ((exp.e1.value.op.value & 0xFF) == 13))
            {
                exp.error(new BytePtr("do not use `null` when comparing class types"));
                this.setError();
                return ;
            }
            Ref<Byte> cmpop = ref(TOK.reserved);
            {
                Expression e = op_overload(exp, this.sc.value, ptr(cmpop));
                if ((e) != null)
                {
                    if (!e.type.value.isscalar() && e.type.value.equals(exp.e1.value.type.value))
                    {
                        exp.error(new BytePtr("recursive `opCmp` expansion"));
                        this.setError();
                        return ;
                    }
                    if (((e.op.value & 0xFF) == 18))
                    {
                        e = new CmpExp(cmpop.value, exp.loc.value, e, new IntegerExp(exp.loc.value, 0L, Type.tint32.value));
                        e = expressionSemantic(e, this.sc.value);
                    }
                    this.result.value = e;
                    return ;
                }
            }
            {
                Expression ex = typeCombine(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            boolean f1 = checkNonAssignmentArrayOp(exp.e1.value, false);
            boolean f2 = checkNonAssignmentArrayOp(exp.e2.value, false);
            if (f1 || f2)
                this.setError();
                return ;
            exp.type.value = Type.tbool.value;
            Expression arrayLowering = null;
            t1 = exp.e1.value.type.value.toBasetype();
            t2 = exp.e2.value.type.value.toBasetype();
            if (((t1.ty.value & 0xFF) == ENUMTY.Tarray) || ((t1.ty.value & 0xFF) == ENUMTY.Tsarray) || ((t1.ty.value & 0xFF) == ENUMTY.Tpointer) && ((t2.ty.value & 0xFF) == ENUMTY.Tarray) || ((t2.ty.value & 0xFF) == ENUMTY.Tsarray) || ((t2.ty.value & 0xFF) == ENUMTY.Tpointer))
            {
                Type t1next = t1.nextOf();
                Type t2next = t2.nextOf();
                if ((t1next.implicitConvTo(t2next) < MATCH.constant) && (t2next.implicitConvTo(t1next) < MATCH.constant) && ((t1next.ty.value & 0xFF) != ENUMTY.Tvoid) && ((t2next.ty.value & 0xFF) != ENUMTY.Tvoid))
                {
                    exp.error(new BytePtr("array comparison type mismatch, `%s` vs `%s`"), t1next.toChars(), t2next.toChars());
                    this.setError();
                    return ;
                }
                if (((t1.ty.value & 0xFF) == ENUMTY.Tarray) || ((t1.ty.value & 0xFF) == ENUMTY.Tsarray) && ((t2.ty.value & 0xFF) == ENUMTY.Tarray) || ((t2.ty.value & 0xFF) == ENUMTY.Tsarray))
                {
                    if (!verifyHookExist(exp.loc.value, this.sc.value.get(), Id.__cmp, new ByteSlice("comparing arrays"), Id.object.value))
                        this.setError();
                        return ;
                    Expression al = new IdentifierExp(exp.loc.value, Id.empty.value);
                    al = new DotIdExp(exp.loc.value, al, Id.object.value);
                    al = new DotIdExp(exp.loc.value, al, Id.__cmp);
                    al = expressionSemantic(al, this.sc.value);
                    Ptr<DArray<Expression>> arguments = refPtr(new DArray<Expression>(2));
                    arguments.get().set(0, exp.e1.value);
                    arguments.get().set(1, exp.e2.value);
                    al = new CallExp(exp.loc.value, al, arguments);
                    al = new CmpExp(exp.op.value, exp.loc.value, al, literal_B6589FC6AB0DC82C());
                    arrayLowering = al;
                }
            }
            else if (((t1.ty.value & 0xFF) == ENUMTY.Tstruct) || ((t2.ty.value & 0xFF) == ENUMTY.Tstruct) || ((t1.ty.value & 0xFF) == ENUMTY.Tclass) && ((t2.ty.value & 0xFF) == ENUMTY.Tclass))
            {
                if (((t2.ty.value & 0xFF) == ENUMTY.Tstruct))
                    exp.error(new BytePtr("need member function `opCmp()` for %s `%s` to compare"), t2.toDsymbol(this.sc.value).kind(), t2.toChars());
                else
                    exp.error(new BytePtr("need member function `opCmp()` for %s `%s` to compare"), t1.toDsymbol(this.sc.value).kind(), t1.toChars());
                this.setError();
                return ;
            }
            else if (t1.iscomplex() || t2.iscomplex())
            {
                exp.error(new BytePtr("compare not defined for complex operands"));
                this.setError();
                return ;
            }
            else if (((t1.ty.value & 0xFF) == ENUMTY.Taarray) || ((t2.ty.value & 0xFF) == ENUMTY.Taarray))
            {
                exp.error(new BytePtr("`%s` is not defined for associative arrays"), Token.toChars(exp.op.value));
                this.setError();
                return ;
            }
            else if (!target.isVectorOpSupported(t1, exp.op.value, t2))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            else
            {
                boolean r1 = exp.e1.value.checkValue();
                boolean r2 = exp.e2.value.checkValue();
                if (r1 || r2)
                    this.setError();
                    return ;
            }
            if (arrayLowering != null)
            {
                arrayLowering = expressionSemantic(arrayLowering, this.sc.value);
                this.result.value = arrayLowering;
                return ;
            }
            this.result.value = exp;
            return ;
        }

        public  void visit(InExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            {
                Expression ex = binSemanticProp(exp, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            Expression e = op_overload(exp, this.sc.value, null);
            if (e != null)
            {
                this.result.value = e;
                return ;
            }
            Type t2b = exp.e2.value.type.value.toBasetype();
            switch ((t2b.ty.value & 0xFF))
            {
                case 2:
                    TypeAArray ta = (TypeAArray)t2b;
                    if (!arrayTypeCompatibleWithoutCasting(exp.e1.value.type.value, ta.index.value))
                    {
                        exp.e1.value = exp.e1.value.implicitCastTo(this.sc.value, ta.index.value);
                    }
                    semanticTypeInfo(this.sc.value, ta.index.value);
                    exp.type.value = ta.nextOf().pointerTo();
                    break;
                case 34:
                    this.setError();
                    return ;
                default:
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            this.result.value = exp;
        }

        public  void visit(RemoveExp e) {
            {
                Expression ex = binSemantic(e, this.sc.value);
                if ((ex) != null)
                {
                    this.result.value = ex;
                    return ;
                }
            }
            this.result.value = e;
        }

        public  void visit(EqualExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            exp.setNoderefOperands();
            {
                Expression e = binSemanticProp(exp, this.sc.value);
                if ((e) != null)
                {
                    this.result.value = e;
                    return ;
                }
            }
            if (((exp.e1.value.op.value & 0xFF) == 20) || ((exp.e2.value.op.value & 0xFF) == 20))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            {
                Type t1 = exp.e1.value.type.value;
                Type t2 = exp.e2.value.type.value;
                if (((t1.ty.value & 0xFF) == ENUMTY.Tenum) && ((t2.ty.value & 0xFF) == ENUMTY.Tenum) && !t1.equivalent(t2))
                    exp.error(new BytePtr("Comparison between different enumeration types `%s` and `%s`; If this behavior is intended consider using `std.conv.asOriginalType`"), t1.toChars(), t2.toChars());
            }
            if (((exp.e1.value.op.value & 0xFF) == 19) && ((exp.e2.value.op.value & 0xFF) == 19))
            {
                AddrExp ae1 = (AddrExp)exp.e1.value;
                AddrExp ae2 = (AddrExp)exp.e2.value;
                if (((ae1.e1.value.op.value & 0xFF) == 26) && ((ae2.e1.value.op.value & 0xFF) == 26))
                {
                    VarExp ve1 = (VarExp)ae1.e1.value;
                    VarExp ve2 = (VarExp)ae2.e1.value;
                    if ((pequals(ve1.var.value, ve2.var.value)))
                    {
                        this.result.value = new IntegerExp(exp.loc.value, (((exp.op.value & 0xFF) == 58) ? 1 : 0), Type.tbool.value);
                        return ;
                    }
                }
            }
            Type t1 = exp.e1.value.type.value.toBasetype();
            Type t2 = exp.e2.value.type.value.toBasetype();
            Function2<Type,Type,Boolean> needsDirectEq = new Function2<Type,Type,Boolean>(){
                public Boolean invoke(Type t1, Type t2) {
                    Ref<Type> t1n = ref(t1.nextOf().toBasetype());
                    Type t2n = t2.nextOf().toBasetype();
                    if (((t1n.value.ty.value & 0xFF) == ENUMTY.Tchar) || ((t1n.value.ty.value & 0xFF) == ENUMTY.Twchar) || ((t1n.value.ty.value & 0xFF) == ENUMTY.Tdchar) && ((t2n.ty.value & 0xFF) == ENUMTY.Tchar) || ((t2n.ty.value & 0xFF) == ENUMTY.Twchar) || ((t2n.ty.value & 0xFF) == ENUMTY.Tdchar) || ((t1n.value.ty.value & 0xFF) == ENUMTY.Tvoid) || ((t2n.ty.value & 0xFF) == ENUMTY.Tvoid))
                    {
                        return false;
                    }
                    if ((!pequals(t1n.value.constOf(), t2n.constOf())))
                        return true;
                    Ref<Type> t = ref(t1n.value);
                    for (; t.value.toBasetype().nextOf() != null;) {
                        t.value = t.value.nextOf().toBasetype();
                    }
                    if (((t.value.ty.value & 0xFF) != ENUMTY.Tstruct))
                        return false;
                    if (global.params.useTypeInfo.value && (Type.dtypeinfo.value != null))
                        semanticTypeInfo(sc.value, t.value);
                    return ((TypeStruct)t.value).sym.value.hasIdentityEquals.value;
                }
            };
            {
                Expression e = op_overload(exp, this.sc.value, null);
                if ((e) != null)
                {
                    this.result.value = e;
                    return ;
                }
            }
            if (!(((t1.ty.value & 0xFF) == ENUMTY.Tarray) && ((t2.ty.value & 0xFF) == ENUMTY.Tarray) && needsDirectEq.invoke(t1, t2)))
            {
                {
                    Expression e = typeCombine(exp, this.sc.value);
                    if ((e) != null)
                    {
                        this.result.value = e;
                        return ;
                    }
                }
            }
            boolean f1 = checkNonAssignmentArrayOp(exp.e1.value, false);
            boolean f2 = checkNonAssignmentArrayOp(exp.e2.value, false);
            if (f1 || f2)
                this.setError();
                return ;
            exp.type.value = Type.tbool.value;
            if (!(((t1.ty.value & 0xFF) == ENUMTY.Tarray) && ((t2.ty.value & 0xFF) == ENUMTY.Tarray) && needsDirectEq.invoke(t1, t2)))
            {
                if (!arrayTypeCompatible(exp.loc.value, exp.e1.value.type.value, exp.e2.value.type.value))
                {
                    if ((!pequals(exp.e1.value.type.value, exp.e2.value.type.value)) && exp.e1.value.type.value.isfloating() && exp.e2.value.type.value.isfloating())
                    {
                        exp.e1.value = exp.e1.value.castTo(this.sc.value, Type.tcomplex80);
                        exp.e2.value = exp.e2.value.castTo(this.sc.value, Type.tcomplex80);
                    }
                }
            }
            if (((t1.ty.value & 0xFF) == ENUMTY.Tarray) && ((t2.ty.value & 0xFF) == ENUMTY.Tarray))
            {
                if (!verifyHookExist(exp.loc.value, this.sc.value.get(), Id.__equals, new ByteSlice("equal checks on arrays"), Id.object.value))
                    this.setError();
                    return ;
                Expression __equals = new IdentifierExp(exp.loc.value, Id.empty.value);
                Identifier id = Identifier.idPool(new ByteSlice("__equals"));
                __equals = new DotIdExp(exp.loc.value, __equals, Id.object.value);
                __equals = new DotIdExp(exp.loc.value, __equals, id);
                Ptr<DArray<Expression>> arguments = refPtr(new DArray<Expression>(2));
                arguments.get().set(0, exp.e1.value);
                arguments.get().set(1, exp.e2.value);
                __equals = new CallExp(exp.loc.value, __equals, arguments);
                if (((exp.op.value & 0xFF) == 59))
                {
                    __equals = new NotExp(exp.loc.value, __equals);
                }
                __equals = expressionSemantic(__equals, this.sc.value);
                this.result.value = __equals;
                return ;
            }
            if (((exp.e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Taarray))
                semanticTypeInfo(this.sc.value, exp.e1.value.type.value.toBasetype());
            if (!target.isVectorOpSupported(t1, exp.op.value, t2))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            this.result.value = exp;
        }

        public  void visit(IdentityExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            exp.setNoderefOperands();
            {
                Expression e = binSemanticProp(exp, this.sc.value);
                if ((e) != null)
                {
                    this.result.value = e;
                    return ;
                }
            }
            {
                Expression e = typeCombine(exp, this.sc.value);
                if ((e) != null)
                {
                    this.result.value = e;
                    return ;
                }
            }
            boolean f1 = checkNonAssignmentArrayOp(exp.e1.value, false);
            boolean f2 = checkNonAssignmentArrayOp(exp.e2.value, false);
            if (f1 || f2)
                this.setError();
                return ;
            if (((exp.e1.value.op.value & 0xFF) == 20) || ((exp.e2.value.op.value & 0xFF) == 20))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            exp.type.value = Type.tbool.value;
            if ((!pequals(exp.e1.value.type.value, exp.e2.value.type.value)) && exp.e1.value.type.value.isfloating() && exp.e2.value.type.value.isfloating())
            {
                exp.e1.value = exp.e1.value.castTo(this.sc.value, Type.tcomplex80);
                exp.e2.value = exp.e2.value.castTo(this.sc.value, Type.tcomplex80);
            }
            Type tb1 = exp.e1.value.type.value.toBasetype();
            Type tb2 = exp.e2.value.type.value.toBasetype();
            if (!target.isVectorOpSupported(tb1, exp.op.value, tb2))
            {
                this.result.value = exp.incompatibleTypes();
                return ;
            }
            if (((exp.e1.value.op.value & 0xFF) == 18))
                exp.e1.value = ((CallExp)exp.e1.value).addDtorHook(this.sc.value);
            if (((exp.e2.value.op.value & 0xFF) == 18))
                exp.e2.value = ((CallExp)exp.e2.value).addDtorHook(this.sc.value);
            if (((exp.e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray) || ((exp.e2.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray))
                exp.deprecation(new BytePtr("identity comparison of static arrays implicitly coerces them to slices, which are compared by reference"));
            this.result.value = exp;
        }

        public  void visit(CondExp exp) {
            if (exp.type.value != null)
            {
                this.result.value = exp;
                return ;
            }
            if (((exp.econd.value.op.value & 0xFF) == 28))
                ((DotIdExp)exp.econd.value).noderef = true;
            Expression ec = expressionSemantic(exp.econd.value, this.sc.value);
            ec = resolveProperties(this.sc.value, ec);
            ec = ec.toBoolean(this.sc.value);
            CtorFlow ctorflow_root = (this.sc.value.get()).ctorflow.clone().copy();
            Expression e1x = expressionSemantic(exp.e1.value, this.sc.value);
            e1x = resolveProperties(this.sc.value, e1x);
            CtorFlow ctorflow1 = (this.sc.value.get()).ctorflow.copy();
            (this.sc.value.get()).ctorflow = ctorflow_root.copy();
            Expression e2x = expressionSemantic(exp.e2.value, this.sc.value);
            e2x = resolveProperties(this.sc.value, e2x);
            (this.sc.value.get()).merge(exp.loc.value, ctorflow1);
            ctorflow1.freeFieldinit();
            if (((ec.op.value & 0xFF) == 127))
            {
                this.result.value = ec;
                return ;
            }
            if ((pequals(ec.type.value, Type.terror.value)))
                this.setError();
                return ;
            exp.econd.value = ec;
            if (((e1x.op.value & 0xFF) == 127))
            {
                this.result.value = e1x;
                return ;
            }
            if ((pequals(e1x.type.value, Type.terror.value)))
                this.setError();
                return ;
            exp.e1.value = e1x;
            if (((e2x.op.value & 0xFF) == 127))
            {
                this.result.value = e2x;
                return ;
            }
            if ((pequals(e2x.type.value, Type.terror.value)))
                this.setError();
                return ;
            exp.e2.value = e2x;
            boolean f0 = checkNonAssignmentArrayOp(exp.econd.value, false);
            boolean f1 = checkNonAssignmentArrayOp(exp.e1.value, false);
            boolean f2 = checkNonAssignmentArrayOp(exp.e2.value, false);
            if (f0 || f1 || f2)
                this.setError();
                return ;
            Type t1 = exp.e1.value.type.value;
            Type t2 = exp.e2.value.type.value;
            if (((t1.ty.value & 0xFF) == ENUMTY.Tvoid) || ((t2.ty.value & 0xFF) == ENUMTY.Tvoid))
            {
                exp.type.value = Type.tvoid.value;
                exp.e1.value = exp.e1.value.castTo(this.sc.value, exp.type.value);
                exp.e2.value = exp.e2.value.castTo(this.sc.value, exp.type.value);
            }
            else if ((pequals(t1, t2)))
                exp.type.value = t1;
            else
            {
                {
                    Expression ex = typeCombine(exp, this.sc.value);
                    if ((ex) != null)
                    {
                        this.result.value = ex;
                        return ;
                    }
                }
                switch ((exp.e1.value.type.value.toBasetype().ty.value & 0xFF))
                {
                    case 27:
                    case 28:
                    case 29:
                        exp.e2.value = exp.e2.value.castTo(this.sc.value, exp.e1.value.type.value);
                        break;
                    default:
                    break;
                }
                switch ((exp.e2.value.type.value.toBasetype().ty.value & 0xFF))
                {
                    case 27:
                    case 28:
                    case 29:
                        exp.e1.value = exp.e1.value.castTo(this.sc.value, exp.e2.value.type.value);
                        break;
                    default:
                    break;
                }
                if (((exp.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tarray))
                {
                    exp.e1.value = exp.e1.value.castTo(this.sc.value, exp.type.value);
                    exp.e2.value = exp.e2.value.castTo(this.sc.value, exp.type.value);
                }
            }
            exp.type.value = exp.type.value.merge2();
            exp.hookDtors(this.sc.value);
            this.result.value = exp;
        }

        public  void visit(FileInitExp e) {
            e.type.value = Type.tstring.value;
            this.result.value = e;
        }

        public  void visit(LineInitExp e) {
            e.type.value = Type.tint32.value;
            this.result.value = e;
        }

        public  void visit(ModuleInitExp e) {
            e.type.value = Type.tstring.value;
            this.result.value = e;
        }

        public  void visit(FuncInitExp e) {
            e.type.value = Type.tstring.value;
            if ((this.sc.value.get()).func.value != null)
            {
                this.result.value = e.resolveLoc(Loc.initial.value, this.sc.value);
                return ;
            }
            this.result.value = e;
        }

        public  void visit(PrettyFuncInitExp e) {
            e.type.value = Type.tstring.value;
            if ((this.sc.value.get()).func.value != null)
            {
                this.result.value = e.resolveLoc(Loc.initial.value, this.sc.value);
                return ;
            }
            this.result.value = e;
        }


        public ExpressionSemanticVisitor() {}

        public ExpressionSemanticVisitor copy() {
            ExpressionSemanticVisitor that = new ExpressionSemanticVisitor();
            that.sc = this.sc;
            that.result = this.result;
            return that;
        }
    }
    public static Expression trySemantic(Expression exp, Ptr<Scope> sc) {
        int errors = global.startGagging();
        Expression e = expressionSemantic(exp, sc);
        if (global.endGagging(errors))
        {
            e = null;
        }
        return e;
    }

    public static Expression unaSemantic(UnaExp e, Ptr<Scope> sc) {
        Expression e1x = expressionSemantic(e.e1.value, sc);
        if (((e1x.op.value & 0xFF) == 127))
            return e1x;
        e.e1.value = e1x;
        return null;
    }

    public static Expression binSemantic(BinExp e, Ptr<Scope> sc) {
        Expression e1x = expressionSemantic(e.e1.value, sc);
        Expression e2x = expressionSemantic(e.e2.value, sc);
        if (((e1x.op.value & 0xFF) == 20))
            e1x = resolveAliasThis(sc, e1x, false);
        if (((e2x.op.value & 0xFF) == 20))
            e2x = resolveAliasThis(sc, e2x, false);
        if (((e1x.op.value & 0xFF) == 127))
            return e1x;
        if (((e2x.op.value & 0xFF) == 127))
            return e2x;
        e.e1.value = e1x;
        e.e2.value = e2x;
        return null;
    }

    public static Expression binSemanticProp(BinExp e, Ptr<Scope> sc) {
        {
            Expression ex = binSemantic(e, sc);
            if ((ex) != null)
                return ex;
        }
        Expression e1x = resolveProperties(sc, e.e1.value);
        Expression e2x = resolveProperties(sc, e.e2.value);
        if (((e1x.op.value & 0xFF) == 127))
            return e1x;
        if (((e2x.op.value & 0xFF) == 127))
            return e2x;
        e.e1.value = e1x;
        e.e2.value = e2x;
        return null;
    }

    public static Expression expressionSemantic(Expression e, Ptr<Scope> sc) {
        ExpressionSemanticVisitor v = new ExpressionSemanticVisitor(sc);
        e.accept(v);
        return v.result.value;
    }

    public static Expression semanticX(DotIdExp exp, Ptr<Scope> sc) {
        {
            Expression ex = unaSemantic(exp, sc);
            if ((ex) != null)
                return ex;
        }
        if ((pequals(exp.ident, Id._mangleof.value)))
        {
            Dsymbol ds = null;
            {
                int __dispatch16 = 0;
                dispatched_16:
                do {
                    switch (__dispatch16 != 0 ? __dispatch16 : (exp.e1.value.op.value & 0xFF))
                    {
                        case 203:
                            ds = ((ScopeExp)exp.e1.value).sds.value;
                            /*goto L1*/{ __dispatch16 = -1; continue dispatched_16; }
                        case 26:
                            ds = ((VarExp)exp.e1.value).var.value;
                            /*goto L1*/{ __dispatch16 = -1; continue dispatched_16; }
                        case 27:
                            ds = ((DotVarExp)exp.e1.value).var.value;
                            /*goto L1*/{ __dispatch16 = -1; continue dispatched_16; }
                        case 214:
                            ds = ((OverExp)exp.e1.value).vars;
                            /*goto L1*/{ __dispatch16 = -1; continue dispatched_16; }
                        case 36:
                            {
                                TemplateExp te = (TemplateExp)exp.e1.value;
                                ds = te.fd != null ? te.fd : te.td.value;
                            }
                        /*L1:*/
                        case -1:
                        __dispatch16 = 0;
                            {
                                assert(ds != null);
                                {
                                    FuncDeclaration f = ds.isFuncDeclaration();
                                    if ((f) != null)
                                    {
                                        if (f.checkForwardRef(exp.loc.value))
                                        {
                                            return new ErrorExp();
                                        }
                                    }
                                }
                                Ref<OutBuffer> buf = ref(new OutBuffer());
                                try {
                                    mangleToBuffer(ds, ptr(buf));
                                    ByteSlice s = buf.value.peekSlice().copy();
                                    Expression e = new StringExp(exp.loc.value, buf.value.extractChars(), s.getLength());
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
        if (((exp.e1.value.op.value & 0xFF) == 26) && ((exp.e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray) && (pequals(exp.ident, Id.length.value)))
        {
            return dotExp(exp.e1.value.type.value, sc, exp.e1.value, exp.ident, exp.noderef ? DotExpFlag.noDeref : 0);
        }
        if (((exp.e1.value.op.value & 0xFF) == 97))
        {
        }
        else
        {
            exp.e1.value = resolvePropertiesX(sc, exp.e1.value, null);
        }
        if (((exp.e1.value.op.value & 0xFF) == 126) && (pequals(exp.ident, Id.offsetof.value)))
        {
            TupleExp te = (TupleExp)exp.e1.value;
            Ptr<DArray<Expression>> exps = refPtr(new DArray<Expression>((te.exps.value.get()).length.value));
            {
                int i = 0;
                for (; (i < (exps.get()).length.value);i++){
                    Expression e = (te.exps.value.get()).get(i);
                    e = expressionSemantic(e, sc);
                    e = new DotIdExp(e.loc.value, e, Id.offsetof.value);
                    exps.get().set(i, e);
                }
            }
            Expression e = new TupleExp(exp.loc.value, null, exps);
            e = expressionSemantic(e, sc);
            return e;
        }
        if (((exp.e1.value.op.value & 0xFF) == 126) && (pequals(exp.ident, Id.length.value)))
        {
            TupleExp te = (TupleExp)exp.e1.value;
            Expression e = new IntegerExp(exp.loc.value, (long)(te.exps.value.get()).length.value, Type.tsize_t.value);
            return e;
        }
        if (((exp.e1.value.op.value & 0xFF) == 37) || ((exp.e1.value.op.value & 0xFF) == 36) && (!pequals(exp.ident, Id.stringof.value)))
        {
            exp.error(new BytePtr("template `%s` does not have property `%s`"), exp.e1.value.toChars(), exp.ident.toChars());
            return new ErrorExp();
        }
        if (exp.e1.value.type.value == null)
        {
            exp.error(new BytePtr("expression `%s` does not have property `%s`"), exp.e1.value.toChars(), exp.ident.toChars());
            return new ErrorExp();
        }
        return exp;
    }

    public static Expression semanticY(DotIdExp exp, Ptr<Scope> sc, int flag) {
        if (((exp.e1.value.op.value & 0xFF) == 123) || ((exp.e1.value.op.value & 0xFF) == 124) && (hasThis(sc) == null))
        {
            {
                AggregateDeclaration ad = (sc.get()).getStructClassScope();
                if ((ad) != null)
                {
                    if (((exp.e1.value.op.value & 0xFF) == 123))
                    {
                        exp.e1.value = new TypeExp(exp.e1.value.loc.value, ad.type.value);
                    }
                    else
                    {
                        ClassDeclaration cd = ad.isClassDeclaration();
                        if ((cd != null) && (cd.baseClass.value != null))
                            exp.e1.value = new TypeExp(exp.e1.value.loc.value, cd.baseClass.value.type.value);
                    }
                }
            }
        }
        Expression e = semanticX(exp, sc);
        if ((!pequals(e, exp)))
            return e;
        Expression eleft = null;
        Expression eright = null;
        if (((exp.e1.value.op.value & 0xFF) == 97))
        {
            DotExp de = (DotExp)exp.e1.value;
            eleft = de.e1.value;
            eright = de.e2.value;
        }
        else
        {
            eleft = null;
            eright = exp.e1.value;
        }
        Type t1b = exp.e1.value.type.value.toBasetype();
        if (((eright.op.value & 0xFF) == 203))
        {
            ScopeExp ie = (ScopeExp)eright;
            int flags = 8;
            if ((ie.sds.value.isModule() != null) && (!pequals(ie.sds.value, (sc.get())._module.value)))
                flags |= 1;
            if (((sc.get()).flags.value & 512) != 0)
                flags |= 128;
            Dsymbol s = ie.sds.value.search(exp.loc.value, exp.ident, flags);
            if ((s != null) && (((sc.get()).flags.value & 512) == 0) && !symbolIsVisible((sc.get())._module.value, s))
            {
                s = null;
            }
            if (s != null)
            {
                dmodule.Package p = s.isPackage();
                if ((p != null) && checkAccess(exp.loc.value, sc, p))
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
                    return em.getVarExp(exp.loc.value, sc);
                }
                VarDeclaration v = s.isVarDeclaration();
                if (v != null)
                {
                    if ((v.type.value == null) || (v.type.value.deco.value == null) && (v.inuse.value != 0))
                    {
                        if (v.inuse.value != 0)
                            exp.error(new BytePtr("circular reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                        else
                            exp.error(new BytePtr("forward reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                        return new ErrorExp();
                    }
                    if (((v.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                        return new ErrorExp();
                    if (((v.storage_class.value & 8388608L) != 0) && (v._init.value != null) && !exp.wantsym)
                    {
                        if (v.inuse.value != 0)
                        {
                            error(exp.loc.value, new BytePtr("circular initialization of %s `%s`"), v.kind(), v.toPrettyChars(false));
                            return new ErrorExp();
                        }
                        e = v.expandInitializer(exp.loc.value);
                        v.inuse.value++;
                        e = expressionSemantic(e, sc);
                        v.inuse.value--;
                        return e;
                    }
                    if (v.needThis())
                    {
                        if (eleft == null)
                            eleft = new ThisExp(exp.loc.value);
                        e = new DotVarExp(exp.loc.value, eleft, v, true);
                        e = expressionSemantic(e, sc);
                    }
                    else
                    {
                        e = new VarExp(exp.loc.value, v, true);
                        if (eleft != null)
                        {
                            e = new CommaExp(exp.loc.value, eleft, e, true);
                            e.type.value = v.type.value;
                        }
                    }
                    e = e.deref();
                    return expressionSemantic(e, sc);
                }
                FuncDeclaration f = s.isFuncDeclaration();
                if (f != null)
                {
                    if (!f.functionSemantic())
                        return new ErrorExp();
                    if (f.needThis())
                    {
                        if (eleft == null)
                            eleft = new ThisExp(exp.loc.value);
                        e = new DotVarExp(exp.loc.value, eleft, f, true);
                        e = expressionSemantic(e, sc);
                    }
                    else
                    {
                        e = new VarExp(exp.loc.value, f, true);
                        if (eleft != null)
                        {
                            e = new CommaExp(exp.loc.value, eleft, e, true);
                            e.type.value = f.type.value;
                        }
                    }
                    return e;
                }
                {
                    TemplateDeclaration td = s.isTemplateDeclaration();
                    if ((td) != null)
                    {
                        if (eleft != null)
                            e = new DotTemplateExp(exp.loc.value, eleft, td);
                        else
                            e = new TemplateExp(exp.loc.value, td, null);
                        e = expressionSemantic(e, sc);
                        return e;
                    }
                }
                {
                    OverDeclaration od = s.isOverDeclaration();
                    if ((od) != null)
                    {
                        e = new VarExp(exp.loc.value, od, true);
                        if (eleft != null)
                        {
                            e = new CommaExp(exp.loc.value, eleft, e, true);
                            e.type.value = Type.tvoid.value;
                        }
                        return e;
                    }
                }
                OverloadSet o = s.isOverloadSet();
                if (o != null)
                {
                    return new OverExp(exp.loc.value, o);
                }
                {
                    Type t = s.getType();
                    if ((t) != null)
                    {
                        return expressionSemantic(new TypeExp(exp.loc.value, t), sc);
                    }
                }
                TupleDeclaration tup = s.isTupleDeclaration();
                if (tup != null)
                {
                    if (eleft != null)
                    {
                        e = new DotVarExp(exp.loc.value, eleft, tup, true);
                        e = expressionSemantic(e, sc);
                        return e;
                    }
                    e = new TupleExp(exp.loc.value, tup);
                    e = expressionSemantic(e, sc);
                    return e;
                }
                ScopeDsymbol sds = s.isScopeDsymbol();
                if (sds != null)
                {
                    e = new ScopeExp(exp.loc.value, sds);
                    e = expressionSemantic(e, sc);
                    if (eleft != null)
                        e = new DotExp(exp.loc.value, eleft, e);
                    return e;
                }
                Import imp = s.isImport();
                if (imp != null)
                {
                    ie = new ScopeExp(exp.loc.value, imp.pkg.value);
                    return expressionSemantic(ie, sc);
                }
                throw new AssertionError("Unreachable code!");
            }
            else if ((pequals(exp.ident, Id.stringof.value)))
            {
                ByteSlice p = ie.asString().copy();
                e = new StringExp(exp.loc.value, toBytePtr(p), p.getLength());
                e = expressionSemantic(e, sc);
                return e;
            }
            if ((ie.sds.value.isPackage() != null) || (ie.sds.value.isImport() != null) || (ie.sds.value.isModule() != null))
            {
                flag = 0;
            }
            if (flag != 0)
                return null;
            s = ie.sds.value.search_correct(exp.ident);
            if (s != null)
            {
                if (s.isPackage() != null)
                    exp.error(new BytePtr("undefined identifier `%s` in %s `%s`, perhaps add `static import %s;`"), exp.ident.toChars(), ie.sds.value.kind(), ie.sds.value.toPrettyChars(false), s.toPrettyChars(false));
                else
                    exp.error(new BytePtr("undefined identifier `%s` in %s `%s`, did you mean %s `%s`?"), exp.ident.toChars(), ie.sds.value.kind(), ie.sds.value.toPrettyChars(false), s.kind(), s.toChars());
            }
            else
                exp.error(new BytePtr("undefined identifier `%s` in %s `%s`"), exp.ident.toChars(), ie.sds.value.kind(), ie.sds.value.toPrettyChars(false));
            return new ErrorExp();
        }
        else if (((t1b.ty.value & 0xFF) == ENUMTY.Tpointer) && ((exp.e1.value.type.value.ty.value & 0xFF) != ENUMTY.Tenum) && (!pequals(exp.ident, Id._init.value)) && (!pequals(exp.ident, Id.__sizeof.value)) && (!pequals(exp.ident, Id.__xalignof.value)) && (!pequals(exp.ident, Id.offsetof.value)) && (!pequals(exp.ident, Id._mangleof.value)) && (!pequals(exp.ident, Id.stringof.value)))
        {
            Type t1bn = t1b.nextOf();
            if (flag != 0)
            {
                AggregateDeclaration ad = isAggregate(t1bn);
                if ((ad != null) && (ad.members.value == null))
                    return null;
            }
            if ((flag != 0) && ((t1bn.ty.value & 0xFF) == ENUMTY.Tvoid))
                return null;
            e = new PtrExp(exp.loc.value, exp.e1.value);
            e = expressionSemantic(e, sc);
            return dotExp(e.type.value, sc, e, exp.ident, flag | (exp.noderef ? DotExpFlag.noDeref : 0));
        }
        else
        {
            if (((exp.e1.value.op.value & 0xFF) == 20) || ((exp.e1.value.op.value & 0xFF) == 36))
                flag = 0;
            e = dotExp(exp.e1.value.type.value, sc, exp.e1.value, exp.ident, flag | (exp.noderef ? DotExpFlag.noDeref : 0));
            if (e != null)
                e = expressionSemantic(e, sc);
            return e;
        }
    }

    public static Expression semanticY(DotTemplateInstanceExp exp, Ptr<Scope> sc, int flag) {
        Function0<Expression> errorExp = new Function0<Expression>(){
            public Expression invoke() {
                return new ErrorExp();
            }
        };
        DotIdExp die = new DotIdExp(exp.loc.value, exp.e1.value, exp.ti.name.value);
        Expression e = semanticX(die, sc);
        if ((pequals(e, die)))
        {
            exp.e1.value = die.e1.value;
            Type t1b = exp.e1.value.type.value.toBasetype();
            if (((t1b.ty.value & 0xFF) == ENUMTY.Tarray) || ((t1b.ty.value & 0xFF) == ENUMTY.Tsarray) || ((t1b.ty.value & 0xFF) == ENUMTY.Taarray) || ((t1b.ty.value & 0xFF) == ENUMTY.Tnull) || (t1b.isTypeBasic() != null) && ((t1b.ty.value & 0xFF) != ENUMTY.Tvoid))
            {
                if (flag != 0)
                    return null;
            }
            e = semanticY(die, sc, flag);
            if (flag != 0)
            {
                if ((e == null) || isDotOpDispatch(e))
                {
                    return null;
                }
            }
        }
        assert(e != null);
        if (((e.op.value & 0xFF) == 127))
            return e;
        try {
            if (((e.op.value & 0xFF) == 27))
            {
                DotVarExp dve = (DotVarExp)e;
                {
                    FuncDeclaration fd = dve.var.value.isFuncDeclaration();
                    if ((fd) != null)
                    {
                        {
                            TemplateDeclaration td = fd.findTemplateDeclRoot();
                            if ((td) != null)
                            {
                                e = new DotTemplateExp(dve.loc.value, dve.e1.value, td);
                                e = expressionSemantic(e, sc);
                            }
                        }
                    }
                    else {
                        OverDeclaration od = dve.var.value.isOverDeclaration();
                        if ((od) != null)
                        {
                            exp.e1.value = dve.e1.value;
                            if (!exp.findTempDecl(sc))
                                /*goto Lerr*/throw Dispatch0.INSTANCE;
                            if (exp.ti.needsTypeInference(sc, 0))
                                return exp;
                            dsymbolSemantic(exp.ti, sc);
                            if ((exp.ti.inst.value == null) || exp.ti.errors.value)
                                return errorExp.invoke();
                            {
                                Declaration v = exp.ti.toAlias().isDeclaration();
                                if ((v) != null)
                                {
                                    if ((v.type.value != null) && (v.type.value.deco.value == null))
                                        v.type.value = typeSemantic(v.type.value, v.loc.value, sc);
                                    return expressionSemantic(new DotVarExp(exp.loc.value, exp.e1.value, v, true), sc);
                                }
                            }
                            return expressionSemantic(new DotExp(exp.loc.value, exp.e1.value, new ScopeExp(exp.loc.value, exp.ti)), sc);
                        }
                    }
                }
            }
            else if (((e.op.value & 0xFF) == 26))
            {
                VarExp ve = (VarExp)e;
                {
                    FuncDeclaration fd = ve.var.value.isFuncDeclaration();
                    if ((fd) != null)
                    {
                        {
                            TemplateDeclaration td = fd.findTemplateDeclRoot();
                            if ((td) != null)
                            {
                                e = expressionSemantic(new TemplateExp(ve.loc.value, td, null), sc);
                            }
                        }
                    }
                    else {
                        OverDeclaration od = ve.var.value.isOverDeclaration();
                        if ((od) != null)
                        {
                            exp.ti.tempdecl.value = od;
                            return expressionSemantic(new ScopeExp(exp.loc.value, exp.ti), sc);
                        }
                    }
                }
            }
            if (((e.op.value & 0xFF) == 37))
            {
                DotTemplateExp dte = (DotTemplateExp)e;
                exp.e1.value = dte.e1.value;
                exp.ti.tempdecl.value = dte.td.value;
                if (!exp.ti.semanticTiargs(sc))
                    return errorExp.invoke();
                if (exp.ti.needsTypeInference(sc, 0))
                    return exp;
                dsymbolSemantic(exp.ti, sc);
                if ((exp.ti.inst.value == null) || exp.ti.errors.value)
                    return errorExp.invoke();
                {
                    Declaration v = exp.ti.toAlias().isDeclaration();
                    if ((v) != null)
                    {
                        if ((v.isFuncDeclaration() != null) || (v.isVarDeclaration() != null))
                        {
                            return expressionSemantic(new DotVarExp(exp.loc.value, exp.e1.value, v, true), sc);
                        }
                    }
                }
                return expressionSemantic(new DotExp(exp.loc.value, exp.e1.value, new ScopeExp(exp.loc.value, exp.ti)), sc);
            }
            else if (((e.op.value & 0xFF) == 36))
            {
                exp.ti.tempdecl.value = ((TemplateExp)e).td.value;
                return expressionSemantic(new ScopeExp(exp.loc.value, exp.ti), sc);
            }
            else if (((e.op.value & 0xFF) == 97))
            {
                DotExp de = (DotExp)e;
                if (((de.e2.value.op.value & 0xFF) == 214))
                {
                    if (!exp.findTempDecl(sc) || !exp.ti.semanticTiargs(sc))
                    {
                        return errorExp.invoke();
                    }
                    if (exp.ti.needsTypeInference(sc, 0))
                        return exp;
                    dsymbolSemantic(exp.ti, sc);
                    if ((exp.ti.inst.value == null) || exp.ti.errors.value)
                        return errorExp.invoke();
                    {
                        Declaration v = exp.ti.toAlias().isDeclaration();
                        if ((v) != null)
                        {
                            if ((v.type.value != null) && (v.type.value.deco.value == null))
                                v.type.value = typeSemantic(v.type.value, v.loc.value, sc);
                            return expressionSemantic(new DotVarExp(exp.loc.value, exp.e1.value, v, true), sc);
                        }
                    }
                    return expressionSemantic(new DotExp(exp.loc.value, exp.e1.value, new ScopeExp(exp.loc.value, exp.ti)), sc);
                }
            }
            else if (((e.op.value & 0xFF) == 214))
            {
                OverExp oe = (OverExp)e;
                exp.ti.tempdecl.value = oe.vars;
                return expressionSemantic(new ScopeExp(exp.loc.value, exp.ti), sc);
            }
        }
        catch(Dispatch0 __d){}
    /*Lerr:*/
        exp.error(new BytePtr("`%s` isn't a template"), e.toChars());
        return errorExp.invoke();
    }

    public static boolean checkAddressVar(Ptr<Scope> sc, UnaExp exp, VarDeclaration v) {
        if (v != null)
        {
            if (!v.canTakeAddressOf())
            {
                exp.error(new BytePtr("cannot take address of `%s`"), exp.e1.value.toChars());
                return false;
            }
            if (((sc.get()).func.value != null) && ((sc.get()).intypeof.value == 0) && !v.isDataseg())
            {
                BytePtr p = pcopy(v.isParameter() ? new BytePtr("parameter") : new BytePtr("local"));
                if (global.params.vsafe.value)
                {
                    v.storage_class.value &= -281474976710657L;
                    v.doNotInferScope = true;
                    if (((v.storage_class.value & 524288L) != 0) && (sc.get()).func.value.setUnsafe() && (((sc.get()).flags.value & 8) == 0))
                    {
                        exp.error(new BytePtr("cannot take address of `scope` %s `%s` in `@safe` function `%s`"), p, v.toChars(), (sc.get()).func.value.toChars());
                        return false;
                    }
                }
                else if ((sc.get()).func.value.setUnsafe() && (((sc.get()).flags.value & 8) == 0))
                {
                    exp.error(new BytePtr("cannot take address of %s `%s` in `@safe` function `%s`"), p, v.toChars(), (sc.get()).func.value.toChars());
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkFunctionAttributes(Expression exp, Ptr<Scope> sc, FuncDeclaration f) {
        {
            boolean error = __withSym.checkDisabled(sc, f);
            (error ? 1 : 0) |= (__withSym.checkDeprecated(sc, f) ? 1 : 0);
            (error ? 1 : 0) |= (__withSym.checkPurity(sc, f) ? 1 : 0);
            (error ? 1 : 0) |= (__withSym.checkSafety(sc, f) ? 1 : 0);
            (error ? 1 : 0) |= (__withSym.checkNogc(sc, f) ? 1 : 0);
            return error;
        }
    }

    public static Expression getThisSkipNestedFuncs(Loc loc, Ptr<Scope> sc, Dsymbol s, AggregateDeclaration ad, Expression e1, Type t, Dsymbol var, boolean flag) {
        int n = 0;
        for (; (s != null) && (s.isFuncDeclaration() != null);){
            FuncDeclaration f = s.isFuncDeclaration();
            if (f.vthis.value != null)
            {
                n++;
                e1 = new VarExp(loc, f.vthis.value, true);
                if (f.isThis2.value)
                {
                    if ((n > 1))
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
        if ((n > 1) || ((e1.op.value & 0xFF) == 62))
            e1 = expressionSemantic(e1, sc);
        if ((s != null) && e1.type.value.equivalent(Type.tvoidptr))
        {
            {
                AggregateDeclaration sad = s.isAggregateDeclaration();
                if ((sad) != null)
                {
                    Type ta = sad.handleType();
                    if (((ta.ty.value & 0xFF) == ENUMTY.Tstruct))
                        ta = ta.pointerTo();
                    e1.type.value = ta;
                }
            }
        }
        e1.type.value = e1.type.value.addMod(t.mod.value);
        return e1;
    }

    // defaulted all parameters starting with #8
    public static Expression getThisSkipNestedFuncs(Loc loc, Ptr<Scope> sc, Dsymbol s, AggregateDeclaration ad, Expression e1, Type t, Dsymbol var) {
        return getThisSkipNestedFuncs(loc, sc, s, ad, e1, t, var, false);
    }

    public static VarDeclaration makeThis2Argument(Loc loc, Ptr<Scope> sc, FuncDeclaration fd) {
        Type tthis2 = Type.tvoidptr.sarrayOf(2L);
        VarDeclaration vthis2 = new VarDeclaration(loc, tthis2, Identifier.generateId(new BytePtr("__this")), null, 0L);
        vthis2.storage_class.value |= 1099511627776L;
        dsymbolSemantic(vthis2, sc);
        vthis2.parent.value = (sc.get()).parent.value;
        assert((sc.get()).func.value != null);
        (sc.get()).func.value.closureVars.push(vthis2);
        vthis2.nestedrefs.push(fd);
        return vthis2;
    }

    public static boolean verifyHookExist(Loc loc, Scope sc, Identifier id, ByteSlice description, Identifier module_) {
        Dsymbol rootSymbol = sc.search(loc, Id.empty.value, null, 0);
        {
            Dsymbol moduleSymbol = rootSymbol.search(loc, module_, 0);
            if ((moduleSymbol) != null)
                if (moduleSymbol.search(loc, id, 0) != null)
                    return true;
        }
        error(loc, new BytePtr("`%s.%s` not found. The current runtime does not support %.*s, or the runtime is corrupt."), module_.toChars(), id.toChars(), description.getLength(), toBytePtr(description));
        return false;
    }

    // defaulted all parameters starting with #5
    public static boolean verifyHookExist(Loc loc, Scope sc, Identifier id, ByteSlice description) {
        return verifyHookExist(loc, sc, id, description, Id.object.value);
    }

}
