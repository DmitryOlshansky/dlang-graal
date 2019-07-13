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
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.complex.*;
import static org.dlang.dmd.dcast.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.denum.*;
import static org.dlang.dmd.dimport.*;
import static org.dlang.dmd.dmangle.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.hdrgen.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.imphint.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.initsem.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.objc.*;
import static org.dlang.dmd.opover.*;
import static org.dlang.dmd.semantic3.*;
import static org.dlang.dmd.sideeffect.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.traits.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.visitor.*;

public class typesem {
    private static final BytePtr[] initializer_0 = {new BytePtr("as type"), new BytePtr("in alias")};
    static Ref<FuncDeclaration> visitAArrayfeq = ref(null);
    static Ref<FuncDeclaration> visitAArrayfcmp = ref(null);
    static Ref<FuncDeclaration> visitAArrayfhash = ref(null);
    static Slice<BytePtr> visitTraitsctxt = slice(initializer_0);
    static Ref<FuncDeclaration> visitAArrayfd_aaLen = ref(null);
    static IntRef noMembernest = ref(0);

    public static Expression semanticLength(Ptr<Scope> sc, Type t, Expression exp) {
        {
            TypeTuple tt = t.isTypeTuple();
            if ((tt) != null)
            {
                ScopeDsymbol sym = new ArrayScopeSymbol(sc, tt);
                sym.parent.value = (sc.get()).scopesym.value;
                sc = (sc.get()).push(sym);
                sc = (sc.get()).startCTFE();
                exp = expressionSemantic(exp, sc);
                sc = (sc.get()).endCTFE();
                (sc.get()).pop();
            }
            else
            {
                sc = (sc.get()).startCTFE();
                exp = expressionSemantic(exp, sc);
                sc = (sc.get()).endCTFE();
            }
        }
        return exp;
    }

    public static Expression semanticLength(Ptr<Scope> sc, TupleDeclaration tup, Expression exp) {
        ScopeDsymbol sym = new ArrayScopeSymbol(sc, tup);
        sym.parent.value = (sc.get()).scopesym.value;
        sc = (sc.get()).push(sym);
        sc = (sc.get()).startCTFE();
        exp = expressionSemantic(exp, sc);
        sc = (sc.get()).endCTFE();
        (sc.get()).pop();
        return exp;
    }

    public static void resolveTupleIndex(Loc loc, Ptr<Scope> sc, Dsymbol s, Ptr<Expression> pe, Ptr<Type> pt, Ptr<Dsymbol> ps, RootObject oindex) {
        pt.set(0, null);
        ps.set(0, null);
        pe.set(0, null);
        TupleDeclaration tup = s.isTupleDeclaration();
        Ref<Expression> eindex = ref(isExpression(oindex));
        Ref<Type> tindex = ref(isType(oindex));
        Ref<Dsymbol> sindex = ref(isDsymbol(oindex));
        if (tup == null)
        {
            if (tindex.value != null)
            {
                eindex.value = new TypeExp(loc, tindex.value);
            }
            else if (sindex.value != null)
            {
                eindex.value = symbolToExp(sindex.value, loc, sc, false);
            }
            Expression e = new IndexExp(loc, symbolToExp(s, loc, sc, false), eindex.value);
            e = expressionSemantic(e, sc);
            resolveExp(e, pt, pe, ps);
            return ;
        }
        if (tindex.value != null)
        {
            resolve(tindex.value, loc, sc, ptr(eindex), ptr(tindex), ptr(sindex), false);
        }
        if (sindex.value != null)
        {
            eindex.value = symbolToExp(sindex.value, loc, sc, false);
        }
        if (eindex.value == null)
        {
            error(loc, new BytePtr("index `%s` is not an expression"), oindex.toChars());
            pt.set(0, Type.terror.value);
            return ;
        }
        eindex.value = semanticLength(sc, tup, eindex.value);
        eindex.value = eindex.value.ctfeInterpret();
        if (((eindex.value.op.value & 0xFF) == 127))
        {
            pt.set(0, Type.terror.value);
            return ;
        }
        long d = eindex.value.toUInteger();
        if ((d >= (long)(tup.objects.value.get()).length.value))
        {
            error(loc, new BytePtr("tuple index `%llu` exceeds length %u"), d, (tup.objects.value.get()).length.value);
            pt.set(0, Type.terror.value);
            return ;
        }
        RootObject o = (tup.objects.value.get()).get((int)d);
        pt.set(0, isType(o));
        ps.set(0, isDsymbol(o));
        pe.set(0, isExpression(o));
        if (pt.get() != null)
        {
            pt.set(0, typeSemantic(pt.get(), loc, sc));
        }
        if (pe.get() != null)
        {
            resolveExp(pe.get(), pt, pe, ps);
        }
    }

    public static void resolveHelper(TypeQualified mt, Loc loc, Ptr<Scope> sc, Dsymbol s, Dsymbol scopesym, Ptr<Expression> pe, Ptr<Type> pt, Ptr<Dsymbol> ps, boolean intypeid) {
        Ref<TypeQualified> mt_ref = ref(mt);
        Ref<Ptr<Scope>> sc_ref = ref(sc);
        Ref<Dsymbol> s_ref = ref(s);
        Ref<Ptr<Expression>> pe_ref = ref(pe);
        Ref<Ptr<Type>> pt_ref = ref(pt);
        Ref<Ptr<Dsymbol>> ps_ref = ref(ps);
        Ref<Boolean> intypeid_ref = ref(intypeid);
        pe_ref.value.set(0, null);
        pt_ref.value.set(0, null);
        ps_ref.value.set(0, null);
        if (s_ref.value != null)
        {
            Declaration d = s_ref.value.isDeclaration();
            if ((d != null) && ((d.storage_class.value & 262144L) != 0))
            {
                s_ref.value = s_ref.value.toAlias();
            }
            else
            {
                s_ref.value.checkDeprecated(loc, sc_ref.value);
                if (d != null)
                {
                    d.checkDisabled(loc, sc_ref.value, true);
                }
            }
            s_ref.value = s_ref.value.toAlias();
            {
                IntRef i = ref(0);
                for (; (i.value < mt_ref.value.idents.length.value);i.value++){
                    RootObject id = mt_ref.value.idents.get(i.value);
                    if ((id.dyncast() == DYNCAST.expression) || (id.dyncast() == DYNCAST.type))
                    {
                        Ref<Type> tx = ref(null);
                        Ref<Expression> ex = ref(null);
                        Ref<Dsymbol> sx = ref(null);
                        resolveTupleIndex(loc, sc_ref.value, s_ref.value, ptr(ex), ptr(tx), ptr(sx), id);
                        if (sx.value != null)
                        {
                            s_ref.value = sx.value.toAlias();
                            continue;
                        }
                        if (tx.value != null)
                        {
                            ex.value = new TypeExp(loc, tx.value);
                        }
                        assert(ex.value != null);
                        ex.value = typeToExpressionHelper(mt_ref.value, ex.value, i.value + 1);
                        ex.value = expressionSemantic(ex.value, sc_ref.value);
                        resolveExp(ex.value, pt_ref.value, pe_ref.value, ps_ref.value);
                        return ;
                    }
                    Type t = s_ref.value.getType();
                    int errorsave = global.errors.value;
                    int flags = (t == null) ? 8 : 1;
                    Dsymbol sm = s_ref.value.searchX(loc, sc_ref.value, id, flags);
                    if ((sm != null) && (((sc_ref.value.get()).flags.value & 512) == 0) && !symbolIsVisible(sc_ref.value, sm))
                    {
                        error(loc, new BytePtr("`%s` is not visible from module `%s`"), sm.toPrettyChars(false), (sc_ref.value.get())._module.value.toChars());
                        sm = null;
                    }
                    if ((global.errors.value != errorsave))
                    {
                        pt_ref.value.set(0, Type.terror.value);
                        return ;
                    }
                    Function0<Void> helper3 = new Function0<Void>(){
                        public Void invoke() {
                            Ref<Expression> e = ref(null);
                            Ref<VarDeclaration> v = ref(s_ref.value.isVarDeclaration());
                            Ref<FuncDeclaration> f = ref(s_ref.value.isFuncDeclaration());
                            if (intypeid_ref.value || (v.value == null) && (f.value == null))
                            {
                                e.value = symbolToExp(s_ref.value, loc, sc_ref.value, true);
                            }
                            else
                            {
                                e.value = new VarExp(loc, s_ref.value.isDeclaration(), true);
                            }
                            e.value = typeToExpressionHelper(mt_ref.value, e.value, i.value);
                            e.value = expressionSemantic(e.value, sc_ref.value);
                            resolveExp(e.value, pt_ref.value, pe_ref.value, ps_ref.value);
                            return null;
                        }
                    };
                    if (intypeid_ref.value && (t == null) && (sm != null) && sm.needThis())
                    {
                        helper3.invoke();
                        return ;
                    }
                    {
                        VarDeclaration v = s_ref.value.isVarDeclaration();
                        if ((v) != null)
                        {
                            if ((v.type.value == null))
                            {
                                dsymbolSemantic(v, sc_ref.value);
                            }
                            if (((v.storage_class.value & 9437188L) != 0) || v.type.value.isConst() || v.type.value.isImmutable())
                            {
                                if (v.isThisDeclaration() == null)
                                {
                                    helper3.invoke();
                                    return ;
                                }
                            }
                        }
                    }
                    if (sm == null)
                    {
                        if (t == null)
                        {
                            if (s_ref.value.isDeclaration() != null)
                            {
                                t = s_ref.value.isDeclaration().type.value;
                                if ((t == null) && (s_ref.value.isTupleDeclaration() != null))
                                {
                                    helper3.invoke();
                                    return ;
                                }
                            }
                            else if ((s_ref.value.isTemplateInstance() != null) || (s_ref.value.isImport() != null) || (s_ref.value.isPackage() != null) || (s_ref.value.isModule() != null))
                            {
                                helper3.invoke();
                                return ;
                            }
                        }
                        if (t != null)
                        {
                            sm = t.toDsymbol(sc_ref.value);
                            if ((sm != null) && (id.dyncast() == DYNCAST.identifier))
                            {
                                sm = sm.search(loc, (Identifier)id, 1);
                                if (sm == null)
                                {
                                    helper3.invoke();
                                    return ;
                                }
                            }
                            else
                            {
                                helper3.invoke();
                                return ;
                            }
                        }
                        else
                        {
                            if ((id.dyncast() == DYNCAST.dsymbol))
                            {
                                assert(global.errors.value != 0);
                            }
                            else
                            {
                                assert((id.dyncast() == DYNCAST.identifier));
                                sm = s_ref.value.search_correct((Identifier)id);
                                if (sm != null)
                                {
                                    error(loc, new BytePtr("identifier `%s` of `%s` is not defined, did you mean %s `%s`?"), id.toChars(), mt_ref.value.toChars(), sm.kind(), sm.toChars());
                                }
                                else
                                {
                                    error(loc, new BytePtr("identifier `%s` of `%s` is not defined"), id.toChars(), mt_ref.value.toChars());
                                }
                            }
                            pe_ref.value.set(0, (new ErrorExp()));
                            return ;
                        }
                    }
                    s_ref.value = sm.toAlias();
                }
            }
            {
                EnumMember em = s_ref.value.isEnumMember();
                if ((em) != null)
                {
                    pe_ref.value.set(0, em.getVarExp(loc, sc_ref.value));
                    return ;
                }
            }
            {
                VarDeclaration v = s_ref.value.isVarDeclaration();
                if ((v) != null)
                {
                    if ((v.type.value == null) || (v.type.value.deco.value == null) && (v.inuse.value != 0))
                    {
                        if (v.inuse.value != 0)
                        {
                            error(loc, new BytePtr("circular reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                        }
                        else
                        {
                            error(loc, new BytePtr("forward reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                        }
                        pt_ref.value.set(0, Type.terror.value);
                        return ;
                    }
                    if (((v.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                    {
                        pt_ref.value.set(0, Type.terror.value);
                    }
                    else
                    {
                        pe_ref.value.set(0, (new VarExp(loc, v, true)));
                    }
                    return ;
                }
            }
            {
                FuncLiteralDeclaration fld = s_ref.value.isFuncLiteralDeclaration();
                if ((fld) != null)
                {
                    pe_ref.value.set(0, (new FuncExp(loc, fld)));
                    pe_ref.value.set(0, expressionSemantic(pe_ref.value.get(), sc_ref.value));
                    return ;
                }
            }
            Type t = null;
            for (; 1 != 0;){
                t = s_ref.value.getType();
                if (t != null)
                {
                    break;
                }
                {
                    Import si = s_ref.value.isImport();
                    if ((si) != null)
                    {
                        s_ref.value = si.search(loc, s_ref.value.ident.value, 8);
                        if ((s_ref.value != null) && (!pequals(s_ref.value, si)))
                        {
                            continue;
                        }
                        s_ref.value = si;
                    }
                }
                ps_ref.value.set(0, s_ref.value);
                return ;
            }
            {
                TypeInstance ti = t.isTypeInstance();
                if ((ti) != null)
                {
                    if ((!pequals(ti, mt_ref.value)) && (ti.deco.value == null))
                    {
                        if (!ti.tempinst.value.errors.value)
                        {
                            error(loc, new BytePtr("forward reference to `%s`"), ti.toChars());
                        }
                        pt_ref.value.set(0, Type.terror.value);
                        return ;
                    }
                }
            }
            if (((t.ty.value & 0xFF) == ENUMTY.Ttuple))
            {
                pt_ref.value.set(0, t);
            }
            else
            {
                pt_ref.value.set(0, merge(t));
            }
        }
        if (s_ref.value == null)
        {
            BytePtr p = pcopy(mt_ref.value.mutableOf().unSharedOf().toChars());
            Identifier id = Identifier.idPool(p, strlen(p));
            {
                ByteSlice n = importHint(id.asString()).copy();
                if ((n).getLength() != 0)
                {
                    error(loc, new BytePtr("`%s` is not defined, perhaps `import %.*s;` ?"), p, n.getLength(), toBytePtr(n));
                }
                else {
                    Dsymbol s2 = (sc_ref.value.get()).search_correct(id);
                    if ((s2) != null)
                    {
                        error(loc, new BytePtr("undefined identifier `%s`, did you mean %s `%s`?"), p, s2.kind(), s2.toChars());
                    }
                    else {
                        BytePtr q = pcopy(Scope.search_correct_C(id));
                        if ((q) != null)
                        {
                            error(loc, new BytePtr("undefined identifier `%s`, did you mean `%s`?"), p, q);
                        }
                        else
                        {
                            error(loc, new BytePtr("undefined identifier `%s`"), p);
                        }
                    }
                }
            }
            pt_ref.value.set(0, Type.terror.value);
        }
    }

    // defaulted all parameters starting with #9
    public static void resolveHelper(TypeQualified mt, Loc loc, Ptr<Scope> sc, Dsymbol s, Dsymbol scopesym, Ptr<Expression> pe, Ptr<Type> pt, Ptr<Dsymbol> ps) {
        return resolveHelper(mt, loc, sc, s, scopesym, pe, pt, ps, false);
    }

    public static Type stripDefaultArgs(Type t) {
        Function1<Ptr<DArray<Parameter>>,Ptr<DArray<Parameter>>> stripParams = new Function1<Ptr<DArray<Parameter>>,Ptr<DArray<Parameter>>>(){
            public Ptr<DArray<Parameter>> invoke(Ptr<DArray<Parameter>> parameters) {
                Ref<Ptr<DArray<Parameter>>> parameters_ref = ref(parameters);
                Function1<Parameter,Parameter> stripParameter = new Function1<Parameter,Parameter>(){
                    public Parameter invoke(Parameter p) {
                        Ref<Type> t = ref(stripDefaultArgs(p.type.value));
                        return (!pequals(t.value, p.type.value)) || (p.defaultArg.value != null) || (p.ident.value != null) || (p.userAttribDecl.value != null) ? new Parameter(p.storageClass.value, t.value, null, null, null) : null;
                    }
                };
                if (parameters_ref.value != null)
                {
                    {
                        Ref<Slice<Parameter>> __r1648 = ref((parameters_ref.value.get()).opSlice().copy());
                        IntRef __key1647 = ref(0);
                        for (; (__key1647.value < __r1648.value.getLength());__key1647.value += 1) {
                            Ref<Parameter> p = ref(__r1648.value.get(__key1647.value));
                            IntRef i = ref(__key1647.value);
                            Ref<Parameter> ps = ref(stripParameter.invoke(p.value));
                            if (ps.value != null)
                            {
                                Ref<Ptr<DArray<Parameter>>> nparams = ref(refPtr(new DArray<Parameter>((parameters_ref.value.get()).length.value)));
                                {
                                    Ref<Slice<Parameter>> __r1650 = ref((nparams.value.get()).opSlice().copy());
                                    IntRef __key1649 = ref(0);
                                    for (; (__key1649.value < __r1650.value.getLength());__key1649.value += 1) {
                                        Ref<Parameter> np = ref(__r1650.value.get(__key1649.value));
                                        IntRef j = ref(__key1649.value);
                                        Ref<Parameter> pj = ref((parameters_ref.value.get()).get(j.value));
                                        if ((j.value < i.value))
                                        {
                                            np.value = pj.value;
                                        }
                                        else if ((j.value == i.value))
                                        {
                                            np.value = ps.value;
                                        }
                                        else
                                        {
                                            Ref<Parameter> nps = ref(stripParameter.invoke(pj.value));
                                            np.value = nps.value != null ? nps.value : pj.value;
                                        }
                                    }
                                }
                                return nparams.value;
                            }
                        }
                    }
                }
                return parameters_ref.value;
            }
        };
        if ((t == null))
        {
            return t;
        }
        {
            TypeFunction tf = t.isTypeFunction();
            if ((tf) != null)
            {
                Type tret = stripDefaultArgs(tf.next.value);
                Ptr<DArray<Parameter>> params = stripParams.invoke(tf.parameterList.parameters.value);
                if ((pequals(tret, tf.next.value)) && (params == tf.parameterList.parameters.value))
                {
                    return t;
                }
                TypeFunction tr = (TypeFunction)tf.copy();
                tr.parameterList.parameters.value = params;
                tr.next.value = tret;
                return tr;
            }
            else {
                TypeTuple tt = t.isTypeTuple();
                if ((tt) != null)
                {
                    Ptr<DArray<Parameter>> args = stripParams.invoke(tt.arguments.value);
                    if ((args == tt.arguments.value))
                    {
                        return t;
                    }
                    TypeTuple tr = (TypeTuple)t.copy();
                    tr.arguments.value = args;
                    return tr;
                }
                else if (((t.ty.value & 0xFF) == ENUMTY.Tenum))
                {
                    return t;
                }
                else
                {
                    Type tn = t.nextOf();
                    Type n = stripDefaultArgs(tn);
                    if ((pequals(n, tn)))
                    {
                        return t;
                    }
                    TypeNext tr = (TypeNext)t.copy();
                    tr.next.value = n;
                    return tr;
                }
            }
        }
    }

    public static Expression typeToExpression(Type t) {
        Function1<TypeSArray,Expression> visitSArray = new Function1<TypeSArray,Expression>(){
            public Expression invoke(TypeSArray t) {
                {
                    Ref<Expression> e = ref(typeToExpression(t.next.value));
                    if ((e.value) != null)
                    {
                        return new ArrayExp(t.dim.value.loc.value, e.value, t.dim.value);
                    }
                }
                return null;
            }
        };
        Function1<TypeAArray,Expression> visitAArray = new Function1<TypeAArray,Expression>(){
            public Expression invoke(TypeAArray t) {
                {
                    Ref<Expression> e = ref(typeToExpression(t.next.value));
                    if ((e.value) != null)
                    {
                        {
                            Ref<Expression> ei = ref(typeToExpression(t.index.value));
                            if ((ei.value) != null)
                            {
                                return new ArrayExp(t.loc.value, e.value, ei.value);
                            }
                        }
                    }
                }
                return null;
            }
        };
        Function1<TypeIdentifier,Expression> visitIdentifier = new Function1<TypeIdentifier,Expression>(){
            public Expression invoke(TypeIdentifier t) {
                Ref<TypeIdentifier> t_ref = ref(t);
                return typeToExpressionHelper(t_ref.value, new IdentifierExp(t_ref.value.loc, t_ref.value.ident.value), 0);
            }
        };
        Function1<TypeInstance,Expression> visitInstance = new Function1<TypeInstance,Expression>(){
            public Expression invoke(TypeInstance t) {
                Ref<TypeInstance> t_ref = ref(t);
                return typeToExpressionHelper(t_ref.value, new ScopeExp(t_ref.value.loc, t_ref.value.tempinst.value), 0);
            }
        };
        switch ((t.ty.value & 0xFF))
        {
            case 1:
                return visitSArray.invoke((TypeSArray)t);
            case 2:
                return visitAArray.invoke((TypeAArray)t);
            case 6:
                return visitIdentifier.invoke((TypeIdentifier)t);
            case 35:
                return visitInstance.invoke((TypeInstance)t);
            default:
            return null;
        }
    }

    public static Expression typeToExpressionHelper(TypeQualified t, Expression e, int i) {
        {
            Slice<RootObject> __r1651 = t.idents.opSlice(i, t.idents.length.value).copy();
            int __key1652 = 0;
            for (; (__key1652 < __r1651.getLength());__key1652 += 1) {
                RootObject id = __r1651.get(__key1652);
                switch (id.dyncast())
                {
                    case DYNCAST.identifier:
                        e = new DotIdExp(e.loc.value, e, (Identifier)id);
                        break;
                    case DYNCAST.dsymbol:
                        TemplateInstance ti = ((Dsymbol)id).isTemplateInstance();
                        assert(ti != null);
                        e = new DotTemplateInstanceExp(e.loc.value, e, ti.name.value, ti.tiargs.value);
                        break;
                    case DYNCAST.type:
                        e = new ArrayExp(t.loc, e, new TypeExp(t.loc, (Type)id));
                        break;
                    case DYNCAST.expression:
                        e = new ArrayExp(t.loc, e, (Expression)id);
                        break;
                    case DYNCAST.object:
                    case DYNCAST.tuple:
                    case DYNCAST.parameter:
                    case DYNCAST.statement:
                    case DYNCAST.condition:
                    case DYNCAST.templateparameter:
                        throw new AssertionError("Unreachable code!");
                    default:
                    throw SwitchError.INSTANCE;
                }
            }
        }
        return e;
    }

    // defaulted all parameters starting with #3
    public static Expression typeToExpressionHelper(TypeQualified t, Expression e) {
        return typeToExpressionHelper(t, e, 0);
    }

    public static Type typeSemantic(Type t, Loc loc, Ptr<Scope> sc) {
        Ref<Loc> loc_ref = ref(loc);
        Ref<Ptr<Scope>> sc_ref = ref(sc);
        Function0<Type> error = new Function0<Type>(){
            public Type invoke() {
                return Type.terror.value;
            }
        };
        Function1<Type,Type> visitType = new Function1<Type,Type>(){
            public Type invoke(Type t) {
                Ref<Type> t_ref = ref(t);
                if (((t_ref.value.ty.value & 0xFF) == ENUMTY.Tint128) || ((t_ref.value.ty.value & 0xFF) == ENUMTY.Tuns128))
                {
                    error(loc_ref.value, new BytePtr("`cent` and `ucent` types not implemented"));
                    return error.invoke();
                }
                return merge(t_ref.value);
            }
        };
        Function1<TypeVector,Type> visitVector = new Function1<TypeVector,Type>(){
            public Type invoke(TypeVector mtype) {
                Ref<TypeVector> mtype_ref = ref(mtype);
                IntRef errors = ref(global.errors.value);
                mtype_ref.value.basetype.value = typeSemantic(mtype_ref.value.basetype.value, loc_ref.value, sc_ref.value);
                if ((errors.value != global.errors.value))
                {
                    return error.invoke();
                }
                mtype_ref.value.basetype.value = mtype_ref.value.basetype.value.toBasetype().mutableOf();
                if (((mtype_ref.value.basetype.value.ty.value & 0xFF) != ENUMTY.Tsarray))
                {
                    error(loc_ref.value, new BytePtr("T in __vector(T) must be a static array, not `%s`"), mtype_ref.value.basetype.value.toChars());
                    return error.invoke();
                }
                TypeSArray t = (TypeSArray)mtype_ref.value.basetype.value;
                IntRef sz = ref((int)t.size(loc_ref.value));
                switch (target.isVectorTypeSupported(sz.value, t.nextOf()))
                {
                    case 0:
                        break;
                    case 1:
                        error(loc_ref.value, new BytePtr("SIMD vector types not supported on this platform"));
                        return error.invoke();
                    case 2:
                        error(loc_ref.value, new BytePtr("vector type `%s` is not supported on this platform"), mtype_ref.value.toChars());
                        return error.invoke();
                    case 3:
                        error(loc_ref.value, new BytePtr("%d byte vector type `%s` is not supported on this platform"), sz.value, mtype_ref.value.toChars());
                        return error.invoke();
                    default:
                    throw SwitchError.INSTANCE;
                }
                return merge(mtype_ref.value);
            }
        };
        Function1<TypeSArray,Type> visitSArray = new Function1<TypeSArray,Type>(){
            public Type invoke(TypeSArray mtype) {
                Ref<Type> t = ref(null);
                Ref<Expression> e = ref(null);
                Ref<Dsymbol> s = ref(null);
                resolve(mtype.next.value, loc_ref.value, sc_ref.value, ptr(e), ptr(t), ptr(s), false);
                {
                    Ref<TupleDeclaration> tup = ref(s.value != null ? s.value.isTupleDeclaration() : null);
                    if ((tup.value) != null)
                    {
                        mtype.dim.value = semanticLength(sc_ref.value, tup.value, mtype.dim.value);
                        mtype.dim.value = mtype.dim.value.ctfeInterpret();
                        if (((mtype.dim.value.op.value & 0xFF) == 127))
                        {
                            return error.invoke();
                        }
                        Ref<Long> d = ref(mtype.dim.value.toUInteger());
                        if ((d.value >= (long)(tup.value.objects.value.get()).length.value))
                        {
                            error(loc_ref.value, new BytePtr("tuple index %llu exceeds %llu"), d.value, (long)(tup.value.objects.value.get()).length.value);
                            return error.invoke();
                        }
                        RootObject o = (tup.value.objects.value.get()).get((int)d.value);
                        if ((o.dyncast() != DYNCAST.type))
                        {
                            error(loc_ref.value, new BytePtr("`%s` is not a type"), mtype.toChars());
                            return error.invoke();
                        }
                        return ((Type)o).addMod(mtype.mod.value);
                    }
                }
                Ref<Type> tn = ref(typeSemantic(mtype.next.value, loc_ref.value, sc_ref.value));
                if (((tn.value.ty.value & 0xFF) == ENUMTY.Terror))
                {
                    return error.invoke();
                }
                Ref<Type> tbn = ref(tn.value.toBasetype());
                if (mtype.dim.value != null)
                {
                    if (mtype.dim.value.isDotVarExp() != null)
                    {
                        {
                            Ref<Declaration> vd = ref(mtype.dim.value.isDotVarExp().var.value);
                            if ((vd.value) != null)
                            {
                                Ref<FuncDeclaration> fd = ref(vd.value.toAlias().isFuncDeclaration());
                                if (fd.value != null)
                                {
                                    mtype.dim.value = new CallExp(loc_ref.value, fd.value, null);
                                }
                            }
                        }
                    }
                    IntRef errors = ref(global.errors.value);
                    mtype.dim.value = semanticLength(sc_ref.value, tbn.value, mtype.dim.value);
                    if ((errors.value != global.errors.value))
                    {
                        return error.invoke();
                    }
                    mtype.dim.value = mtype.dim.value.optimize(0, false);
                    mtype.dim.value = mtype.dim.value.ctfeInterpret();
                    if (((mtype.dim.value.op.value & 0xFF) == 127))
                    {
                        return error.invoke();
                    }
                    errors.value = global.errors.value;
                    Ref<Long> d1 = ref(mtype.dim.value.toInteger());
                    if ((errors.value != global.errors.value))
                    {
                        return error.invoke();
                    }
                    mtype.dim.value = mtype.dim.value.implicitCastTo(sc_ref.value, Type.tsize_t.value);
                    mtype.dim.value = mtype.dim.value.optimize(0, false);
                    if (((mtype.dim.value.op.value & 0xFF) == 127))
                    {
                        return error.invoke();
                    }
                    errors.value = global.errors.value;
                    Ref<Long> d2 = ref(mtype.dim.value.toInteger());
                    if ((errors.value != global.errors.value))
                    {
                        return error.invoke();
                    }
                    if (((mtype.dim.value.op.value & 0xFF) == 127))
                    {
                        return error.invoke();
                    }
                    Function0<Type> overflowError = new Function0<Type>(){
                        public Type invoke() {
                            error(loc_ref.value, new BytePtr("`%s` size %llu * %llu exceeds 0x%llx size limit for static array"), mtype.toChars(), tbn.value.size(loc_ref.value), d1.value, target.maxStaticDataSize.value);
                            return error.invoke();
                        }
                    };
                    if ((d1.value != d2.value))
                    {
                        return overflowError.invoke();
                    }
                    Type tbx = tbn.value.baseElemOf();
                    if (((tbx.ty.value & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)tbx).sym.value.members.value == null) || ((tbx.ty.value & 0xFF) == ENUMTY.Tenum) && (((TypeEnum)tbx).sym.value.members.value == null))
                    {
                    }
                    else if ((tbn.value.isTypeBasic() != null) || ((tbn.value.ty.value & 0xFF) == ENUMTY.Tpointer) || ((tbn.value.ty.value & 0xFF) == ENUMTY.Tarray) || ((tbn.value.ty.value & 0xFF) == ENUMTY.Tsarray) || ((tbn.value.ty.value & 0xFF) == ENUMTY.Taarray) || ((tbn.value.ty.value & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)tbn.value).sym.value.sizeok.value == Sizeok.done) || ((tbn.value.ty.value & 0xFF) == ENUMTY.Tclass))
                    {
                        Ref<Boolean> overflow = ref(false);
                        if ((mulu(tbn.value.size(loc_ref.value), d2.value, overflow) >= target.maxStaticDataSize.value) || overflow.value)
                        {
                            return overflowError.invoke();
                        }
                    }
                }
                switch ((tbn.value.ty.value & 0xFF))
                {
                    case 37:
                        assert(mtype.dim.value != null);
                        TypeTuple tt = (TypeTuple)tbn.value;
                        Ref<Long> d = ref(mtype.dim.value.toUInteger());
                        if ((d.value >= (long)(tt.arguments.value.get()).length.value))
                        {
                            error(loc_ref.value, new BytePtr("tuple index %llu exceeds %llu"), d.value, (long)(tt.arguments.value.get()).length.value);
                            return error.invoke();
                        }
                        Type telem = (tt.arguments.value.get()).get((int)d.value).type.value;
                        return telem.addMod(mtype.mod.value);
                    case 5:
                    case 11:
                        error(loc_ref.value, new BytePtr("cannot have array of `%s`"), tbn.value.toChars());
                        return error.invoke();
                    default:
                    break;
                }
                if (tbn.value.isscope())
                {
                    error(loc_ref.value, new BytePtr("cannot have array of scope `%s`"), tbn.value.toChars());
                    return error.invoke();
                }
                mtype.next.value = tn.value;
                mtype.transitive();
                return merge(mtype.addMod(tn.value.mod.value));
            }
        };
        Function1<TypeDArray,Type> visitDArray = new Function1<TypeDArray,Type>(){
            public Type invoke(TypeDArray mtype) {
                Ref<TypeDArray> mtype_ref = ref(mtype);
                Ref<Type> tn = ref(typeSemantic(mtype_ref.value.next.value, loc_ref.value, sc_ref.value));
                Ref<Type> tbn = ref(tn.value.toBasetype());
                switch ((tbn.value.ty.value & 0xFF))
                {
                    case 37:
                        return tbn.value;
                    case 5:
                    case 11:
                        error(loc_ref.value, new BytePtr("cannot have array of `%s`"), tbn.value.toChars());
                        return error.invoke();
                    case 34:
                        return error.invoke();
                    default:
                    break;
                }
                if (tn.value.isscope())
                {
                    error(loc_ref.value, new BytePtr("cannot have array of scope `%s`"), tn.value.toChars());
                    return error.invoke();
                }
                mtype_ref.value.next.value = tn.value;
                mtype_ref.value.transitive();
                return merge(mtype_ref.value);
            }
        };
        Function1<TypeAArray,Type> visitAArray = new Function1<TypeAArray,Type>(){
            public Type invoke(TypeAArray mtype) {
                Ref<TypeAArray> mtype_ref = ref(mtype);
                if (mtype_ref.value.deco.value != null)
                {
                    return mtype_ref.value;
                }
                mtype_ref.value.loc.value = loc_ref.value.copy();
                mtype_ref.value.sc.value = sc_ref.value;
                if (sc_ref.value != null)
                {
                    (sc_ref.value.get()).setNoFree();
                }
                if (((mtype_ref.value.index.value.ty.value & 0xFF) == ENUMTY.Tident) || ((mtype_ref.value.index.value.ty.value & 0xFF) == ENUMTY.Tinstance) || ((mtype_ref.value.index.value.ty.value & 0xFF) == ENUMTY.Tsarray) || ((mtype_ref.value.index.value.ty.value & 0xFF) == ENUMTY.Ttypeof) || ((mtype_ref.value.index.value.ty.value & 0xFF) == ENUMTY.Treturn))
                {
                    Ref<Expression> e = ref(null);
                    Ref<Type> t = ref(null);
                    Ref<Dsymbol> s = ref(null);
                    resolve(mtype_ref.value.index.value, loc_ref.value, sc_ref.value, ptr(e), ptr(t), ptr(s), false);
                    if (s.value != null)
                    {
                        {
                            Ref<FuncDeclaration> fd = ref(s.value.toAlias().isFuncDeclaration());
                            if ((fd.value) != null)
                            {
                                e.value = new CallExp(loc_ref.value, fd.value, null);
                            }
                        }
                    }
                    if (e.value != null)
                    {
                        Ref<TypeSArray> tsa = ref(new TypeSArray(mtype_ref.value.next.value, e.value));
                        return typeSemantic(tsa.value, loc_ref.value, sc_ref.value);
                    }
                    else if (t.value != null)
                    {
                        mtype_ref.value.index.value = typeSemantic(t.value, loc_ref.value, sc_ref.value);
                    }
                    else
                    {
                        error(loc_ref.value, new BytePtr("index is not a type or an expression"));
                        return error.invoke();
                    }
                }
                else
                {
                    mtype_ref.value.index.value = typeSemantic(mtype_ref.value.index.value, loc_ref.value, sc_ref.value);
                }
                mtype_ref.value.index.value = mtype_ref.value.index.value.merge2();
                if ((mtype_ref.value.index.value.nextOf() != null) && !mtype_ref.value.index.value.nextOf().isImmutable())
                {
                    mtype_ref.value.index.value = mtype_ref.value.index.value.constOf().mutableOf();
                }
                {
                    int __dispatch5 = 0;
                    dispatched_5:
                    do {
                        switch (__dispatch5 != 0 ? __dispatch5 : (mtype_ref.value.index.value.toBasetype().ty.value & 0xFF))
                        {
                            case 5:
                            case 12:
                            case 11:
                            case 37:
                                error(loc_ref.value, new BytePtr("cannot have associative array key of `%s`"), mtype_ref.value.index.value.toBasetype().toChars());
                                /*goto case*/{ __dispatch5 = 34; continue dispatched_5; }
                            case 34:
                                __dispatch5 = 0;
                                return error.invoke();
                            default:
                            break;
                        }
                    } while(__dispatch5 != 0);
                }
                Ref<Type> tbase = ref(mtype_ref.value.index.value.baseElemOf());
                for (; ((tbase.value.ty.value & 0xFF) == ENUMTY.Tarray);) {
                    tbase.value = tbase.value.nextOf().baseElemOf();
                }
                {
                    Ref<TypeStruct> ts = ref(tbase.value.isTypeStruct());
                    if ((ts.value) != null)
                    {
                        Ref<StructDeclaration> sd = ref(ts.value.sym.value);
                        if ((sd.value.semanticRun.value < PASS.semanticdone))
                        {
                            dsymbolSemantic(sd.value, null);
                        }
                        if ((sd.value.xeq.value != null) && (sd.value.xeq.value._scope.value != null) && (sd.value.xeq.value.semanticRun.value < PASS.semantic3done))
                        {
                            IntRef errors = ref(global.startGagging());
                            semantic3(sd.value.xeq.value, sd.value.xeq.value._scope.value);
                            if (global.endGagging(errors.value))
                            {
                                sd.value.xeq.value = StructDeclaration.xerreq.value;
                            }
                        }
                        Ref<BytePtr> s = ref(pcopy(((mtype_ref.value.index.value.toBasetype().ty.value & 0xFF) != ENUMTY.Tstruct) ? new BytePtr("bottom of ") : new BytePtr("")));
                        if (sd.value.xeq.value == null)
                        {
                        }
                        else if ((pequals(sd.value.xeq.value, StructDeclaration.xerreq.value)))
                        {
                            if (search_function(sd.value, Id.eq.value) != null)
                            {
                                error(loc_ref.value, new BytePtr("%sAA key type `%s` does not have `bool opEquals(ref const %s) const`"), s.value, sd.value.toChars(), sd.value.toChars());
                            }
                            else
                            {
                                error(loc_ref.value, new BytePtr("%sAA key type `%s` does not support const equality"), s.value, sd.value.toChars());
                            }
                            return error.invoke();
                        }
                        else if (sd.value.xhash.value == null)
                        {
                            if (search_function(sd.value, Id.eq.value) != null)
                            {
                                error(loc_ref.value, new BytePtr("%sAA key type `%s` should have `size_t toHash() const nothrow @safe` if `opEquals` defined"), s.value, sd.value.toChars());
                            }
                            else
                            {
                                error(loc_ref.value, new BytePtr("%sAA key type `%s` supports const equality but doesn't support const hashing"), s.value, sd.value.toChars());
                            }
                            return error.invoke();
                        }
                        else
                        {
                            assert((sd.value.xeq.value != null) && (sd.value.xhash.value != null));
                        }
                    }
                    else if (((tbase.value.ty.value & 0xFF) == ENUMTY.Tclass) && (((TypeClass)tbase.value).sym.value.isInterfaceDeclaration() == null))
                    {
                        Ref<ClassDeclaration> cd = ref(((TypeClass)tbase.value).sym.value);
                        if ((cd.value.semanticRun.value < PASS.semanticdone))
                        {
                            dsymbolSemantic(cd.value, null);
                        }
                        if (ClassDeclaration.object.value == null)
                        {
                            error(Loc.initial.value, new BytePtr("missing or corrupt object.d"));
                            fatal();
                        }
                        if (typesem.visitAArrayfeq.value == null)
                        {
                            typesem.visitAArrayfeq.value = search_function(ClassDeclaration.object.value, Id.eq.value).isFuncDeclaration();
                        }
                        if (typesem.visitAArrayfcmp.value == null)
                        {
                            typesem.visitAArrayfcmp.value = search_function(ClassDeclaration.object.value, Id.cmp.value).isFuncDeclaration();
                        }
                        if (typesem.visitAArrayfhash.value == null)
                        {
                            typesem.visitAArrayfhash.value = search_function(ClassDeclaration.object.value, Id.tohash).isFuncDeclaration();
                        }
                        assert((typesem.visitAArrayfcmp.value != null) && (typesem.visitAArrayfeq.value != null) && (typesem.visitAArrayfhash.value != null));
                        if ((typesem.visitAArrayfeq.value.vtblIndex.value < cd.value.vtbl.value.length.value) && (pequals(cd.value.vtbl.value.get(typesem.visitAArrayfeq.value.vtblIndex.value), typesem.visitAArrayfeq.value)))
                        {
                            if ((typesem.visitAArrayfcmp.value.vtblIndex.value < cd.value.vtbl.value.length.value) && (!pequals(cd.value.vtbl.value.get(typesem.visitAArrayfcmp.value.vtblIndex.value), typesem.visitAArrayfcmp.value)))
                            {
                                Ref<BytePtr> s = ref(pcopy(((mtype_ref.value.index.value.toBasetype().ty.value & 0xFF) != ENUMTY.Tclass) ? new BytePtr("bottom of ") : new BytePtr("")));
                                error(loc_ref.value, new BytePtr("%sAA key type `%s` now requires equality rather than comparison"), s.value, cd.value.toChars());
                                errorSupplemental(loc_ref.value, new BytePtr("Please override `Object.opEquals` and `Object.toHash`."));
                            }
                        }
                    }
                }
                mtype_ref.value.next.value = typeSemantic(mtype_ref.value.next.value, loc_ref.value, sc_ref.value).merge2();
                mtype_ref.value.transitive();
                {
                    int __dispatch6 = 0;
                    dispatched_6:
                    do {
                        switch (__dispatch6 != 0 ? __dispatch6 : (mtype_ref.value.next.value.toBasetype().ty.value & 0xFF))
                        {
                            case 5:
                            case 12:
                            case 11:
                            case 37:
                                error(loc_ref.value, new BytePtr("cannot have associative array of `%s`"), mtype_ref.value.next.value.toChars());
                                /*goto case*/{ __dispatch6 = 34; continue dispatched_6; }
                            case 34:
                                __dispatch6 = 0;
                                return error.invoke();
                            default:
                            break;
                        }
                    } while(__dispatch6 != 0);
                }
                if (mtype_ref.value.next.value.isscope())
                {
                    error(loc_ref.value, new BytePtr("cannot have array of scope `%s`"), mtype_ref.value.next.value.toChars());
                    return error.invoke();
                }
                return merge(mtype_ref.value);
            }
        };
        Function1<TypePointer,Type> visitPointer = new Function1<TypePointer,Type>(){
            public Type invoke(TypePointer mtype) {
                Ref<TypePointer> mtype_ref = ref(mtype);
                if (mtype_ref.value.deco.value != null)
                {
                    return mtype_ref.value;
                }
                Ref<Type> n = ref(typeSemantic(mtype_ref.value.next.value, loc_ref.value, sc_ref.value));
                {
                    int __dispatch7 = 0;
                    dispatched_7:
                    do {
                        switch (__dispatch7 != 0 ? __dispatch7 : (n.value.toBasetype().ty.value & 0xFF))
                        {
                            case 37:
                                error(loc_ref.value, new BytePtr("cannot have pointer to `%s`"), n.value.toChars());
                                /*goto case*/{ __dispatch7 = 34; continue dispatched_7; }
                            case 34:
                                __dispatch7 = 0;
                                return error.invoke();
                            default:
                            break;
                        }
                    } while(__dispatch7 != 0);
                }
                if ((!pequals(n.value, mtype_ref.value.next.value)))
                {
                    mtype_ref.value.deco.value = null;
                }
                mtype_ref.value.next.value = n.value;
                if (((mtype_ref.value.next.value.ty.value & 0xFF) != ENUMTY.Tfunction))
                {
                    mtype_ref.value.transitive();
                    return merge(mtype_ref.value);
                }
                mtype_ref.value.deco.value = pcopy(merge(mtype_ref.value).deco.value);
                return mtype_ref.value;
            }
        };
        Function1<TypeReference,Type> visitReference = new Function1<TypeReference,Type>(){
            public Type invoke(TypeReference mtype) {
                Ref<TypeReference> mtype_ref = ref(mtype);
                Ref<Type> n = ref(typeSemantic(mtype_ref.value.next.value, loc_ref.value, sc_ref.value));
                if ((!pequals(n.value, mtype_ref.value.next.value)))
                {
                    mtype_ref.value.deco.value = null;
                }
                mtype_ref.value.next.value = n.value;
                mtype_ref.value.transitive();
                return merge(mtype_ref.value);
            }
        };
        Function1<TypeFunction,Type> visitFunction = new Function1<TypeFunction,Type>(){
            public Type invoke(TypeFunction mtype) {
                Ref<TypeFunction> mtype_ref = ref(mtype);
                if (mtype_ref.value.deco.value != null)
                {
                    return mtype_ref.value;
                }
                Ref<Boolean> errors = ref(false);
                if ((mtype_ref.value.inuse.value > 500))
                {
                    mtype_ref.value.inuse.value = 0;
                    error(loc_ref.value, new BytePtr("recursive type"));
                    return error.invoke();
                }
                Ref<TypeFunction> tf = ref(mtype_ref.value.copy().toTypeFunction());
                if (mtype_ref.value.parameterList.parameters.value != null)
                {
                    tf.value.parameterList.parameters.value = (mtype_ref.value.parameterList.parameters.value.get()).copy();
                    {
                        IntRef i = ref(0);
                        for (; (i.value < (mtype_ref.value.parameterList.parameters.value.get()).length.value);i.value++){
                            Ref<Parameter> p = ref(null);
                            (p.value) = ((mtype_ref.value.parameterList.parameters.value.get()).get(i.value)).copy();
                            tf.value.parameterList.parameters.value.get().set(i.value, p.value);
                        }
                    }
                }
                if (((sc_ref.value.get()).stc.value & 67108864L) != 0)
                {
                    tf.value.purity.value = PURE.fwdref;
                }
                if (((sc_ref.value.get()).stc.value & 33554432L) != 0)
                {
                    tf.value.isnothrow.value = true;
                }
                if (((sc_ref.value.get()).stc.value & 4398046511104L) != 0)
                {
                    tf.value.isnogc.value = true;
                }
                if (((sc_ref.value.get()).stc.value & 2097152L) != 0)
                {
                    tf.value.isref.value = true;
                }
                if (((sc_ref.value.get()).stc.value & 17592186044416L) != 0)
                {
                    tf.value.isreturn.value = true;
                }
                if (((sc_ref.value.get()).stc.value & 4503599627370496L) != 0)
                {
                    tf.value.isreturninferred.value = true;
                }
                if (((sc_ref.value.get()).stc.value & 524288L) != 0)
                {
                    tf.value.isscope.value = true;
                }
                if (((sc_ref.value.get()).stc.value & 562949953421312L) != 0)
                {
                    tf.value.isscopeinferred.value = true;
                }
                if ((tf.value.trust.value == TRUST.default_))
                {
                    if (((sc_ref.value.get()).stc.value & 8589934592L) != 0)
                    {
                        tf.value.trust.value = TRUST.safe;
                    }
                    else if (((sc_ref.value.get()).stc.value & 34359738368L) != 0)
                    {
                        tf.value.trust.value = TRUST.system;
                    }
                    else if (((sc_ref.value.get()).stc.value & 17179869184L) != 0)
                    {
                        tf.value.trust.value = TRUST.trusted;
                    }
                }
                if (((sc_ref.value.get()).stc.value & 4294967296L) != 0)
                {
                    tf.value.isproperty.value = true;
                }
                tf.value.linkage.value = (sc_ref.value.get()).linkage.value;
                Ref<Boolean> wildreturn = ref(false);
                if (tf.value.next.value != null)
                {
                    sc_ref.value = (sc_ref.value.get()).push();
                    (sc_ref.value.get()).stc.value &= -4465259184133L;
                    tf.value.next.value = typeSemantic(tf.value.next.value, loc_ref.value, sc_ref.value);
                    sc_ref.value = (sc_ref.value.get()).pop();
                    (errors.value ? 1 : 0) |= (tf.value.checkRetType(loc_ref.value) ? 1 : 0);
                    if (tf.value.next.value.isscope() && (((sc_ref.value.get()).flags.value & 1) == 0))
                    {
                        error(loc_ref.value, new BytePtr("functions cannot return `scope %s`"), tf.value.next.value.toChars());
                        errors.value = true;
                    }
                    if (tf.value.next.value.hasWild() != 0)
                    {
                        wildreturn.value = true;
                    }
                    if (tf.value.isreturn.value && !tf.value.isref.value && !tf.value.next.value.hasPointers())
                    {
                        tf.value.isreturn.value = false;
                    }
                }
                Ref<Byte> wildparams = ref((byte)0);
                if (tf.value.parameterList.parameters.value != null)
                {
                    Ref<Ptr<Scope>> argsc = ref((sc_ref.value.get()).push());
                    (argsc.value.get()).stc.value = 0L;
                    (argsc.value.get()).protection.value = new Prot(Prot.Kind.public_).copy();
                    (argsc.value.get()).func.value = null;
                    IntRef dim = ref(tf.value.parameterList.length());
                    {
                        IntRef i = ref(0);
                        for (; (i.value < dim.value);i.value++){
                            Parameter fparam = tf.value.parameterList.get(i.value);
                            mtype_ref.value.inuse.value++;
                            fparam.type.value = typeSemantic(fparam.type.value, loc_ref.value, argsc.value);
                            mtype_ref.value.inuse.value--;
                            if (((fparam.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                            {
                                errors.value = true;
                                continue;
                            }
                            fparam.type.value = fparam.type.value.addStorageClass(fparam.storageClass.value);
                            if ((fparam.storageClass.value & 268435713L) != 0)
                            {
                                if (fparam.type.value == null)
                                {
                                    continue;
                                }
                            }
                            Type t = fparam.type.value.toBasetype();
                            if (((t.ty.value & 0xFF) == ENUMTY.Tfunction))
                            {
                                error(loc_ref.value, new BytePtr("cannot have parameter of function type `%s`"), fparam.type.value.toChars());
                                errors.value = true;
                            }
                            else if (((fparam.storageClass.value & 2101248L) == 0) && ((t.ty.value & 0xFF) == ENUMTY.Tstruct) || ((t.ty.value & 0xFF) == ENUMTY.Tsarray) || ((t.ty.value & 0xFF) == ENUMTY.Tenum))
                            {
                                Type tb2 = t.baseElemOf();
                                if (((tb2.ty.value & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)tb2).sym.value.members.value == null) || ((tb2.ty.value & 0xFF) == ENUMTY.Tenum) && (((TypeEnum)tb2).sym.value.memtype.value == null))
                                {
                                    error(loc_ref.value, new BytePtr("cannot have parameter of opaque type `%s` by value"), fparam.type.value.toChars());
                                    errors.value = true;
                                }
                            }
                            else if (((fparam.storageClass.value & 8192L) == 0) && ((t.ty.value & 0xFF) == ENUMTY.Tvoid))
                            {
                                error(loc_ref.value, new BytePtr("cannot have parameter of type `%s`"), fparam.type.value.toChars());
                                errors.value = true;
                            }
                            if (((fparam.storageClass.value & 2149580800L) == 2149580800L))
                            {
                                fparam.storageClass.value |= 17592186044416L;
                            }
                            if ((fparam.storageClass.value & 17592186044416L) != 0)
                            {
                                if ((fparam.storageClass.value & 2101248L) != 0)
                                {
                                    if (false)
                                    {
                                        Ref<Long> stc = ref(fparam.storageClass.value & 2101248L);
                                        error(loc_ref.value, new BytePtr("parameter `%s` is `return %s` but function does not return by `ref`"), fparam.ident.value != null ? fparam.ident.value.toChars() : new BytePtr(""), stcToChars(stc));
                                        errors.value = true;
                                    }
                                }
                                else
                                {
                                    if ((fparam.storageClass.value & 524288L) == 0)
                                    {
                                        fparam.storageClass.value |= 562949953945600L;
                                    }
                                    if (tf.value.isref.value)
                                    {
                                    }
                                    else if ((tf.value.next.value != null) && !tf.value.next.value.hasPointers() && ((tf.value.next.value.toBasetype().ty.value & 0xFF) != ENUMTY.Tvoid))
                                    {
                                        fparam.storageClass.value &= -17592186044417L;
                                    }
                                }
                            }
                            if ((fparam.storageClass.value & 2105344L) != 0)
                            {
                            }
                            else if ((fparam.storageClass.value & 4096L) != 0)
                            {
                                {
                                    Ref<Byte> m = ref((byte)((fparam.type.value.mod.value & 0xFF) & 13));
                                    if ((m.value) != 0)
                                    {
                                        error(loc_ref.value, new BytePtr("cannot have `%s out` parameter of type `%s`"), MODtoChars(m.value), t.toChars());
                                        errors.value = true;
                                    }
                                    else
                                    {
                                        Type tv = t.baseElemOf();
                                        if (((tv.ty.value & 0xFF) == ENUMTY.Tstruct) && ((TypeStruct)tv).sym.value.noDefaultCtor.value)
                                        {
                                            error(loc_ref.value, new BytePtr("cannot have `out` parameter of type `%s` because the default construction is disabled"), fparam.type.value.toChars());
                                            errors.value = true;
                                        }
                                    }
                                }
                            }
                            if (((fparam.storageClass.value & 524288L) != 0) && !fparam.type.value.hasPointers() && ((fparam.type.value.ty.value & 0xFF) != ENUMTY.Ttuple))
                            {
                                fparam.storageClass.value &= -524289L;
                                if (!tf.value.isref.value || (((sc_ref.value.get()).flags.value & 1) != 0))
                                {
                                    fparam.storageClass.value &= -17592186044417L;
                                }
                            }
                            if (t.hasWild() != 0)
                            {
                                wildparams.value |= 1;
                            }
                            if (fparam.defaultArg.value != null)
                            {
                                Ref<Expression> e = ref(fparam.defaultArg.value);
                                long isRefOrOut = fparam.storageClass.value & 2101248L;
                                long isAuto = fparam.storageClass.value & 35184372089088L;
                                if ((isRefOrOut != 0) && (isAuto == 0))
                                {
                                    e.value = expressionSemantic(e.value, argsc.value);
                                    e.value = resolveProperties(argsc.value, e.value);
                                }
                                else
                                {
                                    e.value = inferType(e.value, fparam.type.value, 0);
                                    Ref<Initializer> iz = ref(new ExpInitializer(e.value.loc.value, e.value));
                                    iz.value = initializerSemantic(iz.value, argsc.value, fparam.type.value, NeedInterpret.INITnointerpret);
                                    e.value = initializerToExpression(iz.value, null);
                                }
                                if (((e.value.op.value & 0xFF) == 161))
                                {
                                    FuncExp fe = (FuncExp)e.value;
                                    e.value = new VarExp(e.value.loc.value, fe.fd.value, false);
                                    e.value = new AddrExp(e.value.loc.value, e.value);
                                    e.value = expressionSemantic(e.value, argsc.value);
                                }
                                if ((isRefOrOut != 0) && (isAuto == 0) || e.value.isLvalue() && !MODimplicitConv(e.value.type.value.mod.value, fparam.type.value.mod.value))
                                {
                                    Ref<BytePtr> errTxt = ref(pcopy((fparam.storageClass.value & 2097152L) != 0 ? new BytePtr("ref") : new BytePtr("out")));
                                    error(e.value.loc.value, new BytePtr("expression `%s` of type `%s` is not implicitly convertible to type `%s %s` of parameter `%s`"), e.value.toChars(), e.value.type.value.toChars(), errTxt.value, fparam.type.value.toChars(), fparam.toChars());
                                }
                                e.value = e.value.implicitCastTo(argsc.value, fparam.type.value);
                                if ((isRefOrOut != 0) && (isAuto == 0))
                                {
                                    e.value = e.value.toLvalue(argsc.value, e.value);
                                }
                                fparam.defaultArg.value = e.value;
                                if (((e.value.op.value & 0xFF) == 127))
                                {
                                    errors.value = true;
                                }
                            }
                            {
                                Ref<TypeTuple> tt = ref(t.isTypeTuple());
                                if ((tt.value) != null)
                                {
                                    if ((tt.value.arguments.value != null) && ((tt.value.arguments.value.get()).length.value != 0))
                                    {
                                        IntRef tdim = ref((tt.value.arguments.value.get()).length.value);
                                        Ref<Ptr<DArray<Parameter>>> newparams = ref(refPtr(new DArray<Parameter>(tdim.value)));
                                        {
                                            IntRef j = ref(0);
                                            for (; (j.value < tdim.value);j.value++){
                                                Parameter narg = (tt.value.arguments.value.get()).get(j.value);
                                                Ref<Long> stc = ref(fparam.storageClass.value | narg.storageClass.value);
                                                Ref<Long> stc1 = ref(fparam.storageClass.value & 2109440L);
                                                Ref<Long> stc2 = ref(narg.storageClass.value & 2109440L);
                                                if ((stc1.value != 0) && (stc2.value != 0) && (stc1.value != stc2.value))
                                                {
                                                    Ref<OutBuffer> buf1 = ref(new OutBuffer());
                                                    try {
                                                        stcToBuffer(ptr(buf1), stc1.value | ((stc1.value & 2097152L) != 0 ? fparam.storageClass.value & 256L : 0L));
                                                        Ref<OutBuffer> buf2 = ref(new OutBuffer());
                                                        try {
                                                            stcToBuffer(ptr(buf2), stc2.value);
                                                            error(loc_ref.value, new BytePtr("incompatible parameter storage classes `%s` and `%s`"), buf1.value.peekChars(), buf2.value.peekChars());
                                                            errors.value = true;
                                                            stc.value = stc1.value | stc.value & -2109441L;
                                                        }
                                                        finally {
                                                        }
                                                    }
                                                    finally {
                                                    }
                                                }
                                                Ref<Expression> paramDefaultArg = ref(narg.defaultArg.value);
                                                Ref<TupleExp> te = ref(fparam.defaultArg.value != null ? fparam.defaultArg.value.isTupleExp() : null);
                                                if ((te.value != null) && (te.value.exps.value != null) && ((te.value.exps.value.get()).length.value != 0))
                                                {
                                                    paramDefaultArg.value = (te.value.exps.value.get()).get(j.value);
                                                }
                                                newparams.value.get().set(j.value, new Parameter(stc.value, narg.type.value, narg.ident.value, paramDefaultArg.value, narg.userAttribDecl.value));
                                            }
                                        }
                                        fparam.type.value = new TypeTuple(newparams.value);
                                    }
                                    fparam.storageClass.value = 0L;
                                    dim.value = tf.value.parameterList.length();
                                    i.value--;
                                    continue;
                                }
                            }
                            if ((fparam.storageClass.value & 256L) != 0)
                            {
                                Ref<Expression> farg = ref((mtype_ref.value.fargs.value != null) && (i.value < (mtype_ref.value.fargs.value.get()).length.value) ? (mtype_ref.value.fargs.value.get()).get(i.value) : fparam.defaultArg.value);
                                if ((farg.value != null) && ((fparam.storageClass.value & 2097152L) != 0))
                                {
                                    if (farg.value.isLvalue())
                                    {
                                    }
                                    else
                                    {
                                        fparam.storageClass.value &= -2097153L;
                                    }
                                    fparam.storageClass.value &= -257L;
                                    fparam.storageClass.value |= 35184372088832L;
                                }
                                else if (mtype_ref.value.incomplete.value && ((fparam.storageClass.value & 2097152L) != 0))
                                {
                                    fparam.storageClass.value &= -257L;
                                    fparam.storageClass.value |= 35184372088832L;
                                }
                                else
                                {
                                    error(loc_ref.value, new BytePtr("`auto` can only be used as part of `auto ref` for template function parameters"));
                                    errors.value = true;
                                }
                            }
                            fparam.storageClass.value &= -2685405189L;
                        }
                    }
                    (argsc.value.get()).pop();
                }
                if (tf.value.isWild())
                {
                    wildparams.value |= 2;
                }
                if (wildreturn.value && (wildparams.value == 0))
                {
                    error(loc_ref.value, new BytePtr("`inout` on `return` means `inout` must be on a parameter as well for `%s`"), mtype_ref.value.toChars());
                    errors.value = true;
                }
                tf.value.iswild.value = wildparams.value;
                if (tf.value.isproperty.value && (tf.value.parameterList.varargs.value != VarArg.none) || (tf.value.parameterList.length() > 2))
                {
                    error(loc_ref.value, new BytePtr("properties can only have zero, one, or two parameter"));
                    errors.value = true;
                }
                if ((tf.value.parameterList.varargs.value == VarArg.variadic) && (tf.value.linkage.value != LINK.d) && (tf.value.parameterList.length() == 0))
                {
                    error(loc_ref.value, new BytePtr("variadic functions with non-D linkage must have at least one parameter"));
                    errors.value = true;
                }
                if (errors.value)
                {
                    return error.invoke();
                }
                if (tf.value.next.value != null)
                {
                    tf.value.deco.value = pcopy(merge(tf.value).deco.value);
                }
                return tf.value;
            }
        };
        Function1<TypeDelegate,Type> visitDelegate = new Function1<TypeDelegate,Type>(){
            public Type invoke(TypeDelegate mtype) {
                Ref<TypeDelegate> mtype_ref = ref(mtype);
                if (mtype_ref.value.deco.value != null)
                {
                    return mtype_ref.value;
                }
                mtype_ref.value.next.value = typeSemantic(mtype_ref.value.next.value, loc_ref.value, sc_ref.value);
                if (((mtype_ref.value.next.value.ty.value & 0xFF) != ENUMTY.Tfunction))
                {
                    return error.invoke();
                }
                mtype_ref.value.deco.value = pcopy(merge(mtype_ref.value).deco.value);
                return mtype_ref.value;
            }
        };
        Function1<TypeIdentifier,Type> visitIdentifier = new Function1<TypeIdentifier,Type>(){
            public Type invoke(TypeIdentifier mtype) {
                Ref<TypeIdentifier> mtype_ref = ref(mtype);
                Ref<Type> t = ref(null);
                Ref<Expression> e = ref(null);
                Ref<Dsymbol> s = ref(null);
                resolve(mtype_ref.value, loc_ref.value, sc_ref.value, ptr(e), ptr(t), ptr(s), false);
                if (t.value != null)
                {
                    return t.value.addMod(mtype_ref.value.mod.value);
                }
                else
                {
                    if (s.value != null)
                    {
                        Ref<TemplateDeclaration> td = ref(s.value.isTemplateDeclaration());
                        if ((td.value != null) && (td.value.onemember.value != null) && (td.value.onemember.value.isAggregateDeclaration() != null))
                        {
                            error(loc_ref.value, new BytePtr("template %s `%s` is used as a type without instantiation; to instantiate it use `%s!(arguments)`"), s.value.kind(), s.value.toPrettyChars(false), s.value.ident.value.toChars());
                        }
                        else
                        {
                            error(loc_ref.value, new BytePtr("%s `%s` is used as a type"), s.value.kind(), s.value.toPrettyChars(false));
                        }
                    }
                    else
                    {
                        error(loc_ref.value, new BytePtr("`%s` is used as a type"), mtype_ref.value.toChars());
                    }
                    return error.invoke();
                }
            }
        };
        Function1<TypeInstance,Type> visitInstance = new Function1<TypeInstance,Type>(){
            public Type invoke(TypeInstance mtype) {
                Ref<TypeInstance> mtype_ref = ref(mtype);
                Ref<Type> t = ref(null);
                Ref<Expression> e = ref(null);
                Ref<Dsymbol> s = ref(null);
                {
                    IntRef errors = ref(global.errors.value);
                    resolve(mtype_ref.value, loc_ref.value, sc_ref.value, ptr(e), ptr(t), ptr(s), false);
                    if ((t.value == null) && (errors.value != global.errors.value))
                    {
                        return error.invoke();
                    }
                }
                if (t.value == null)
                {
                    if ((e.value == null) && (s.value != null) && s.value.errors.value)
                    {
                        error(loc_ref.value, new BytePtr("`%s` had previous errors"), mtype_ref.value.toChars());
                    }
                    else
                    {
                        error(loc_ref.value, new BytePtr("`%s` is used as a type"), mtype_ref.value.toChars());
                    }
                    return error.invoke();
                }
                return t.value;
            }
        };
        Function1<TypeTypeof,Type> visitTypeof = new Function1<TypeTypeof,Type>(){
            public Type invoke(TypeTypeof mtype) {
                Ref<TypeTypeof> mtype_ref = ref(mtype);
                Ref<Expression> e = ref(null);
                Ref<Type> t = ref(null);
                Ref<Dsymbol> s = ref(null);
                resolve(mtype_ref.value, loc_ref.value, sc_ref.value, ptr(e), ptr(t), ptr(s), false);
                if ((s.value != null) && ((t.value = s.value.getType()) != null))
                {
                    t.value = t.value.addMod(mtype_ref.value.mod.value);
                }
                if (t.value == null)
                {
                    error(loc_ref.value, new BytePtr("`%s` is used as a type"), mtype_ref.value.toChars());
                    return error.invoke();
                }
                return t.value;
            }
        };
        Function1<TypeTraits,Type> visitTraits = new Function1<TypeTraits,Type>(){
            public Type invoke(TypeTraits mtype) {
                Ref<TypeTraits> mtype_ref = ref(mtype);
                if (((mtype_ref.value.ty.value & 0xFF) == ENUMTY.Terror))
                {
                    return mtype_ref.value;
                }
                if ((!pequals(mtype_ref.value.exp.value.ident.value, Id.allMembers.value)) && (!pequals(mtype_ref.value.exp.value.ident.value, Id.derivedMembers.value)) && (!pequals(mtype_ref.value.exp.value.ident.value, Id.getMember.value)) && (!pequals(mtype_ref.value.exp.value.ident.value, Id.parent.value)) && (!pequals(mtype_ref.value.exp.value.ident.value, Id.getOverloads.value)) && (!pequals(mtype_ref.value.exp.value.ident.value, Id.getVirtualFunctions.value)) && (!pequals(mtype_ref.value.exp.value.ident.value, Id.getVirtualMethods.value)) && (!pequals(mtype_ref.value.exp.value.ident.value, Id.getAttributes.value)) && (!pequals(mtype_ref.value.exp.value.ident.value, Id.getUnitTests.value)) && (!pequals(mtype_ref.value.exp.value.ident.value, Id.getAliasThis.value)))
                {
                    error(mtype_ref.value.loc, new BytePtr("trait `%s` is either invalid or not supported %s"), mtype_ref.value.exp.value.ident.value.toChars(), typesem.visitTraitsctxt.get((mtype_ref.value.inAliasDeclaration.value ? 1 : 0)));
                    mtype_ref.value.ty.value = (byte)34;
                    return mtype_ref.value;
                }
                Ref<Type> result = ref(null);
                {
                    Ref<Expression> e = ref(semanticTraits(mtype_ref.value.exp.value, sc_ref.value));
                    if ((e.value) != null)
                    {
                        switch ((e.value.op.value & 0xFF))
                        {
                            case 27:
                                mtype_ref.value.sym.value = ((DotVarExp)e.value).var.value;
                                break;
                            case 26:
                                mtype_ref.value.sym.value = ((VarExp)e.value).var.value;
                                break;
                            case 161:
                                FuncExp fe = (FuncExp)e.value;
                                mtype_ref.value.sym.value = fe.td.value != null ? fe.td.value : fe.fd.value;
                                break;
                            case 37:
                                mtype_ref.value.sym.value = ((DotTemplateExp)e.value).td.value;
                                break;
                            case 41:
                                mtype_ref.value.sym.value = ((DsymbolExp)e.value).s.value;
                                break;
                            case 36:
                                mtype_ref.value.sym.value = ((TemplateExp)e.value).td.value;
                                break;
                            case 203:
                                mtype_ref.value.sym.value = ((ScopeExp)e.value).sds.value;
                                break;
                            case 126:
                                mtype_ref.value.sym.value = new TupleDeclaration(e.value.loc.value, Identifier.generateId(new BytePtr("__aliastup")), ((Ptr<DArray<RootObject>>)e.value.toTupleExp().exps.value));
                                break;
                            case 30:
                                result.value = isType(((DotTypeExp)e.value).sym.value);
                                break;
                            case 20:
                                result.value = ((TypeExp)e.value).type.value;
                                break;
                            case 214:
                                result.value = ((OverExp)e.value).type.value;
                                break;
                            default:
                            break;
                        }
                    }
                }
                if (result.value != null)
                {
                    result.value = result.value.addMod(mtype_ref.value.mod.value);
                }
                if (!mtype_ref.value.inAliasDeclaration.value && (result.value == null))
                {
                    if (global.errors.value == 0)
                    {
                        error(mtype_ref.value.loc, new BytePtr("`%s` does not give a valid type"), mtype_ref.value.toChars());
                    }
                    return error.invoke();
                }
                return result.value;
            }
        };
        Function1<TypeReturn,Type> visitReturn = new Function1<TypeReturn,Type>(){
            public Type invoke(TypeReturn mtype) {
                Ref<TypeReturn> mtype_ref = ref(mtype);
                Ref<Expression> e = ref(null);
                Ref<Type> t = ref(null);
                Ref<Dsymbol> s = ref(null);
                resolve(mtype_ref.value, loc_ref.value, sc_ref.value, ptr(e), ptr(t), ptr(s), false);
                if ((s.value != null) && ((t.value = s.value.getType()) != null))
                {
                    t.value = t.value.addMod(mtype_ref.value.mod.value);
                }
                if (t.value == null)
                {
                    error(loc_ref.value, new BytePtr("`%s` is used as a type"), mtype_ref.value.toChars());
                    return error.invoke();
                }
                return t.value;
            }
        };
        Function1<TypeStruct,Type> visitStruct = new Function1<TypeStruct,Type>(){
            public Type invoke(TypeStruct mtype) {
                Ref<TypeStruct> mtype_ref = ref(mtype);
                if (mtype_ref.value.deco.value != null)
                {
                    if ((sc_ref.value != null) && ((sc_ref.value.get()).cppmangle.value != CPPMANGLE.def))
                    {
                        if ((mtype_ref.value.cppmangle.value == CPPMANGLE.def))
                        {
                            mtype_ref.value.cppmangle.value = (sc_ref.value.get()).cppmangle.value;
                        }
                    }
                    return mtype_ref.value;
                }
                assert(mtype_ref.value.sym.value.parent.value != null);
                if (((mtype_ref.value.sym.value.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                {
                    return error.invoke();
                }
                if ((sc_ref.value != null) && ((sc_ref.value.get()).cppmangle.value != CPPMANGLE.def))
                {
                    mtype_ref.value.cppmangle.value = (sc_ref.value.get()).cppmangle.value;
                }
                else
                {
                    mtype_ref.value.cppmangle.value = CPPMANGLE.asStruct;
                }
                return merge(mtype_ref.value);
            }
        };
        Function1<TypeEnum,Type> visitEnum = new Function1<TypeEnum,Type>(){
            public Type invoke(TypeEnum mtype) {
                Ref<TypeEnum> mtype_ref = ref(mtype);
                return mtype_ref.value.deco.value != null ? mtype_ref.value : merge(mtype_ref.value);
            }
        };
        Function1<TypeClass,Type> visitClass = new Function1<TypeClass,Type>(){
            public Type invoke(TypeClass mtype) {
                Ref<TypeClass> mtype_ref = ref(mtype);
                if (mtype_ref.value.deco.value != null)
                {
                    if ((sc_ref.value != null) && ((sc_ref.value.get()).cppmangle.value != CPPMANGLE.def))
                    {
                        if ((mtype_ref.value.cppmangle.value == CPPMANGLE.def))
                        {
                            mtype_ref.value.cppmangle.value = (sc_ref.value.get()).cppmangle.value;
                        }
                    }
                    return mtype_ref.value;
                }
                assert(mtype_ref.value.sym.value.parent.value != null);
                if (((mtype_ref.value.sym.value.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                {
                    return error.invoke();
                }
                if ((sc_ref.value != null) && ((sc_ref.value.get()).cppmangle.value != CPPMANGLE.def))
                {
                    mtype_ref.value.cppmangle.value = (sc_ref.value.get()).cppmangle.value;
                }
                else
                {
                    mtype_ref.value.cppmangle.value = CPPMANGLE.asClass;
                }
                return merge(mtype_ref.value);
            }
        };
        Function1<TypeTuple,Type> visitTuple = new Function1<TypeTuple,Type>(){
            public Type invoke(TypeTuple mtype) {
                Ref<TypeTuple> mtype_ref = ref(mtype);
                if (mtype_ref.value.deco.value == null)
                {
                    mtype_ref.value.deco.value = pcopy(merge(mtype_ref.value).deco.value);
                }
                return mtype_ref.value;
            }
        };
        Function1<TypeSlice,Type> visitSlice = new Function1<TypeSlice,Type>(){
            public Type invoke(TypeSlice mtype) {
                Ref<Type> tn = ref(typeSemantic(mtype.next.value, loc_ref.value, sc_ref.value));
                Ref<Type> tbn = ref(tn.value.toBasetype());
                if (((tbn.value.ty.value & 0xFF) != ENUMTY.Ttuple))
                {
                    error(loc_ref.value, new BytePtr("can only slice tuple types, not `%s`"), tbn.value.toChars());
                    return error.invoke();
                }
                TypeTuple tt = (TypeTuple)tbn.value;
                mtype.lwr.value = semanticLength(sc_ref.value, tbn.value, mtype.lwr.value);
                mtype.upr.value = semanticLength(sc_ref.value, tbn.value, mtype.upr.value);
                mtype.lwr.value = mtype.lwr.value.ctfeInterpret();
                mtype.upr.value = mtype.upr.value.ctfeInterpret();
                if (((mtype.lwr.value.op.value & 0xFF) == 127) || ((mtype.upr.value.op.value & 0xFF) == 127))
                {
                    return error.invoke();
                }
                Ref<Long> i1 = ref(mtype.lwr.value.toUInteger());
                Ref<Long> i2 = ref(mtype.upr.value.toUInteger());
                if (!((i1.value <= i2.value) && (i2.value <= (long)(tt.arguments.value.get()).length.value)))
                {
                    error(loc_ref.value, new BytePtr("slice `[%llu..%llu]` is out of range of `[0..%llu]`"), i1.value, i2.value, (long)(tt.arguments.value.get()).length.value);
                    return error.invoke();
                }
                mtype.next.value = tn.value;
                mtype.transitive();
                Ref<Ptr<DArray<Parameter>>> args = ref(refPtr(new DArray<Parameter>()));
                (args.value.get()).reserve((int)(i2.value - i1.value));
                {
                    Ref<Slice<Parameter>> __r1653 = ref((tt.arguments.value.get()).opSlice((int)i1.value, (int)i2.value).copy());
                    IntRef __key1654 = ref(0);
                    for (; (__key1654.value < __r1653.value.getLength());__key1654.value += 1) {
                        Ref<Parameter> arg = ref(__r1653.value.get(__key1654.value));
                        (args.value.get()).push(arg.value);
                    }
                }
                Ref<Type> t = ref(new TypeTuple(args.value));
                return typeSemantic(t.value, loc_ref.value, sc_ref.value);
            }
        };
        switch ((t.ty.value & 0xFF))
        {
            default:
            return visitType.invoke(t);
            case 41:
                return visitVector.invoke((TypeVector)t);
            case 1:
                return visitSArray.invoke((TypeSArray)t);
            case 0:
                return visitDArray.invoke((TypeDArray)t);
            case 2:
                return visitAArray.invoke((TypeAArray)t);
            case 3:
                return visitPointer.invoke((TypePointer)t);
            case 4:
                return visitReference.invoke((TypeReference)t);
            case 5:
                return visitFunction.invoke((TypeFunction)t);
            case 10:
                return visitDelegate.invoke((TypeDelegate)t);
            case 6:
                return visitIdentifier.invoke((TypeIdentifier)t);
            case 35:
                return visitInstance.invoke((TypeInstance)t);
            case 36:
                return visitTypeof.invoke((TypeTypeof)t);
            case 44:
                return visitTraits.invoke((TypeTraits)t);
            case 39:
                return visitReturn.invoke((TypeReturn)t);
            case 8:
                return visitStruct.invoke((TypeStruct)t);
            case 9:
                return visitEnum.invoke((TypeEnum)t);
            case 7:
                return visitClass.invoke((TypeClass)t);
            case 37:
                return visitTuple.invoke((TypeTuple)t);
            case 38:
                return visitSlice.invoke((TypeSlice)t);
        }
    }

    public static Type merge(Type type) {
        {
            int __dispatch10 = 0;
            dispatched_10:
            do {
                switch (__dispatch10 != 0 ? __dispatch10 : (type.ty.value & 0xFF))
                {
                    case 34:
                    case 36:
                    case 6:
                    case 35:
                        return type;
                    case 9:
                        break;
                    case 2:
                        if (merge(((TypeAArray)type).index.value).deco.value == null)
                        {
                            return type;
                        }
                        /*goto default*/ { __dispatch10 = -1; continue dispatched_10; }
                    default:
                    __dispatch10 = 0;
                    if ((type.nextOf() != null) && (type.nextOf().deco.value == null))
                    {
                        return type;
                    }
                    break;
                }
            } while(__dispatch10 != 0);
        }
        if (type.deco.value == null)
        {
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                buf.value.reserve(32);
                mangleToBuffer(type, ptr(buf));
                Ptr<StringValue> sv = Type.stringtable.update(buf.value.extractSlice());
                if ((sv.get()).ptrvalue != null)
                {
                    Type t = ((Type)(sv.get()).ptrvalue);
                    assert(t.deco.value != null);
                    return t;
                }
                else
                {
                    Type t = stripDefaultArgs(type);
                    (sv.get()).ptrvalue = pcopy(((Object)t));
                    type.deco.value = pcopy((t.deco.value = pcopy((sv.get()).toDchars())));
                    return t;
                }
            }
            finally {
            }
        }
        return type;
    }

    public static Expression getProperty(Type t, Loc loc, Identifier ident, int flag) {
        Ref<Identifier> ident_ref = ref(ident);
        IntRef flag_ref = ref(flag);
        Function1<Type,Expression> visitType = new Function1<Type,Expression>(){
            public Expression invoke(Type mt) {
                Ref<Type> mt_ref = ref(mt);
                Ref<Expression> e = ref(null);
                if ((pequals(ident_ref.value, Id.__sizeof.value)))
                {
                    Ref<Long> sz = ref(mt_ref.value.size(loc));
                    if ((sz.value == -1L))
                    {
                        return new ErrorExp();
                    }
                    e.value = new IntegerExp(loc, sz.value, Type.tsize_t.value);
                }
                else if ((pequals(ident_ref.value, Id.__xalignof.value)))
                {
                    IntRef explicitAlignment = ref(mt_ref.value.alignment());
                    int naturalAlignment = mt_ref.value.alignsize();
                    int actualAlignment = (explicitAlignment.value == -1) ? naturalAlignment : explicitAlignment.value;
                    e.value = new IntegerExp(loc, (long)actualAlignment, Type.tsize_t.value);
                }
                else if ((pequals(ident_ref.value, Id._init.value)))
                {
                    Type tb = mt_ref.value.toBasetype();
                    e.value = mt_ref.value.defaultInitLiteral(loc);
                    if (((tb.ty.value & 0xFF) == ENUMTY.Tstruct) && tb.needsNested())
                    {
                        e.value.isStructLiteralExp().useStaticInit.value = true;
                    }
                }
                else if ((pequals(ident_ref.value, Id._mangleof.value)))
                {
                    if (mt_ref.value.deco.value == null)
                    {
                        error(loc, new BytePtr("forward reference of type `%s.mangleof`"), mt_ref.value.toChars());
                        e.value = new ErrorExp();
                    }
                    else
                    {
                        e.value = new StringExp(loc, mt_ref.value.deco.value);
                        Ref<Scope> sc = ref(new Scope().copy());
                        e.value = expressionSemantic(e.value, ptr(sc));
                    }
                }
                else if ((pequals(ident_ref.value, Id.stringof.value)))
                {
                    Ref<BytePtr> s = ref(pcopy(mt_ref.value.toChars()));
                    e.value = new StringExp(loc, s.value);
                    Ref<Scope> sc = ref(new Scope().copy());
                    e.value = expressionSemantic(e.value, ptr(sc));
                }
                else if ((flag_ref.value != 0) && (!pequals(mt_ref.value, Type.terror.value)))
                {
                    return null;
                }
                else
                {
                    Ref<Dsymbol> s = ref(null);
                    if (((mt_ref.value.ty.value & 0xFF) == ENUMTY.Tstruct) || ((mt_ref.value.ty.value & 0xFF) == ENUMTY.Tclass) || ((mt_ref.value.ty.value & 0xFF) == ENUMTY.Tenum))
                    {
                        s.value = mt_ref.value.toDsymbol(null);
                    }
                    if (s.value != null)
                    {
                        s.value = s.value.search_correct(ident_ref.value);
                    }
                    if ((!pequals(mt_ref.value, Type.terror.value)))
                    {
                        if (s.value != null)
                        {
                            error(loc, new BytePtr("no property `%s` for type `%s`, did you mean `%s`?"), ident_ref.value.toChars(), mt_ref.value.toChars(), s.value.toPrettyChars(false));
                        }
                        else
                        {
                            if ((pequals(ident_ref.value, Id.call.value)) && ((mt_ref.value.ty.value & 0xFF) == ENUMTY.Tclass))
                            {
                                error(loc, new BytePtr("no property `%s` for type `%s`, did you mean `new %s`?"), ident_ref.value.toChars(), mt_ref.value.toChars(), mt_ref.value.toPrettyChars(false));
                            }
                            else
                            {
                                error(loc, new BytePtr("no property `%s` for type `%s`"), ident_ref.value.toChars(), mt_ref.value.toChars());
                            }
                        }
                    }
                    e.value = new ErrorExp();
                }
                return e.value;
            }
        };
        Function1<TypeError,Expression> visitError = new Function1<TypeError,Expression>(){
            public Expression invoke(TypeError _param_0) {
                return new ErrorExp();
            }
        };
        Function1<TypeBasic,Expression> visitBasic = new Function1<TypeBasic,Expression>(){
            public Expression invoke(TypeBasic mt) {
                Ref<TypeBasic> mt_ref = ref(mt);
                Function1<Long,Expression> integerValue = new Function1<Long,Expression>(){
                    public Expression invoke(Long i) {
                        Ref<Long> i_ref = ref(i);
                        return new IntegerExp(loc, i_ref.value, mt_ref.value);
                    }
                };
                Function1<Long,Expression> intValue = new Function1<Long,Expression>(){
                    public Expression invoke(Long i) {
                        Ref<Long> i_ref = ref(i);
                        return new IntegerExp(loc, i_ref.value, Type.tint32.value);
                    }
                };
                Function1<Double,Expression> floatValue = new Function1<Double,Expression>(){
                    public Expression invoke(Double r) {
                        Ref<Double> r_ref = ref(r);
                        if (mt_ref.value.isreal() || mt_ref.value.isimaginary())
                        {
                            return new RealExp(loc, r_ref.value, mt_ref.value);
                        }
                        else
                        {
                            return new ComplexExp(loc, new complex_t(r_ref.value, r_ref.value), mt_ref.value);
                        }
                    }
                };
                if ((pequals(ident_ref.value, Id.max.value)))
                {
                    switch ((mt_ref.value.ty.value & 0xFF))
                    {
                        case 13:
                            return integerValue.invoke(127L);
                        case 14:
                            return integerValue.invoke(255L);
                        case 15:
                            return integerValue.invoke(32767L);
                        case 16:
                            return integerValue.invoke(65535L);
                        case 17:
                            return integerValue.invoke(2147483647L);
                        case 18:
                            return integerValue.invoke(4294967295L);
                        case 19:
                            return integerValue.invoke(9223372036854775807L);
                        case 20:
                            return integerValue.invoke(-1L);
                        case 30:
                            return integerValue.invoke(1L);
                        case 31:
                            return integerValue.invoke(255L);
                        case 32:
                            return integerValue.invoke(65535L);
                        case 33:
                            return integerValue.invoke(1114111L);
                        case 27:
                        case 24:
                        case 21:
                            return floatValue.invoke(target.FloatProperties.max.value);
                        case 28:
                        case 25:
                        case 22:
                            return floatValue.invoke(target.DoubleProperties.max.value);
                        case 29:
                        case 26:
                        case 23:
                            return floatValue.invoke(target.RealProperties.max.value);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.min.value)))
                {
                    switch ((mt_ref.value.ty.value & 0xFF))
                    {
                        case 13:
                            return integerValue.invoke(-128L);
                        case 14:
                        case 16:
                        case 18:
                        case 20:
                        case 30:
                        case 31:
                        case 32:
                        case 33:
                            return integerValue.invoke(0L);
                        case 15:
                            return integerValue.invoke(-32768L);
                        case 17:
                            return integerValue.invoke(-2147483648L);
                        case 19:
                            return integerValue.invoke(-9223372036854775808L);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.min_normal.value)))
                {
                    switch ((mt_ref.value.ty.value & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return floatValue.invoke(target.FloatProperties.min_normal.value);
                        case 28:
                        case 25:
                        case 22:
                            return floatValue.invoke(target.DoubleProperties.min_normal.value);
                        case 29:
                        case 26:
                        case 23:
                            return floatValue.invoke(target.RealProperties.min_normal.value);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.nan.value)))
                {
                    switch ((mt_ref.value.ty.value & 0xFF))
                    {
                        case 27:
                        case 28:
                        case 29:
                        case 24:
                        case 25:
                        case 26:
                        case 21:
                        case 22:
                        case 23:
                            return floatValue.invoke(target.RealProperties.nan.value);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.infinity.value)))
                {
                    switch ((mt_ref.value.ty.value & 0xFF))
                    {
                        case 27:
                        case 28:
                        case 29:
                        case 24:
                        case 25:
                        case 26:
                        case 21:
                        case 22:
                        case 23:
                            return floatValue.invoke(target.RealProperties.infinity.value);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.dig.value)))
                {
                    switch ((mt_ref.value.ty.value & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return intValue.invoke((long)target.FloatProperties.dig.value);
                        case 28:
                        case 25:
                        case 22:
                            return intValue.invoke((long)target.DoubleProperties.dig.value);
                        case 29:
                        case 26:
                        case 23:
                            return intValue.invoke((long)target.RealProperties.dig.value);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.epsilon.value)))
                {
                    switch ((mt_ref.value.ty.value & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return floatValue.invoke(target.FloatProperties.epsilon.value);
                        case 28:
                        case 25:
                        case 22:
                            return floatValue.invoke(target.DoubleProperties.epsilon.value);
                        case 29:
                        case 26:
                        case 23:
                            return floatValue.invoke(target.RealProperties.epsilon.value);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.mant_dig.value)))
                {
                    switch ((mt_ref.value.ty.value & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return intValue.invoke((long)target.FloatProperties.mant_dig.value);
                        case 28:
                        case 25:
                        case 22:
                            return intValue.invoke((long)target.DoubleProperties.mant_dig.value);
                        case 29:
                        case 26:
                        case 23:
                            return intValue.invoke((long)target.RealProperties.mant_dig.value);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.max_10_exp.value)))
                {
                    switch ((mt_ref.value.ty.value & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return intValue.invoke((long)target.FloatProperties.max_10_exp.value);
                        case 28:
                        case 25:
                        case 22:
                            return intValue.invoke((long)target.DoubleProperties.max_10_exp.value);
                        case 29:
                        case 26:
                        case 23:
                            return intValue.invoke((long)target.RealProperties.max_10_exp.value);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.max_exp.value)))
                {
                    switch ((mt_ref.value.ty.value & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return intValue.invoke((long)target.FloatProperties.max_exp.value);
                        case 28:
                        case 25:
                        case 22:
                            return intValue.invoke((long)target.DoubleProperties.max_exp.value);
                        case 29:
                        case 26:
                        case 23:
                            return intValue.invoke((long)target.RealProperties.max_exp.value);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.min_10_exp.value)))
                {
                    switch ((mt_ref.value.ty.value & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return intValue.invoke((long)target.FloatProperties.min_10_exp.value);
                        case 28:
                        case 25:
                        case 22:
                            return intValue.invoke((long)target.DoubleProperties.min_10_exp.value);
                        case 29:
                        case 26:
                        case 23:
                            return intValue.invoke((long)target.RealProperties.min_10_exp.value);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.min_exp.value)))
                {
                    switch ((mt_ref.value.ty.value & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return intValue.invoke((long)target.FloatProperties.min_exp.value);
                        case 28:
                        case 25:
                        case 22:
                            return intValue.invoke((long)target.DoubleProperties.min_exp.value);
                        case 29:
                        case 26:
                        case 23:
                            return intValue.invoke((long)target.RealProperties.min_exp.value);
                        default:
                        break;
                    }
                }
                return visitType.invoke(mt_ref.value);
            }
        };
        Function1<TypeVector,Expression> visitVector = new Function1<TypeVector,Expression>(){
            public Expression invoke(TypeVector mt) {
                Ref<TypeVector> mt_ref = ref(mt);
                return visitType.invoke(mt_ref.value);
            }
        };
        Function1<TypeEnum,Expression> visitEnum = new Function1<TypeEnum,Expression>(){
            public Expression invoke(TypeEnum mt) {
                Ref<TypeEnum> mt_ref = ref(mt);
                Ref<Expression> e = ref(null);
                if ((pequals(ident_ref.value, Id.max.value)) || (pequals(ident_ref.value, Id.min.value)))
                {
                    return mt_ref.value.sym.value.getMaxMinValue(loc, ident_ref.value);
                }
                else if ((pequals(ident_ref.value, Id._init.value)))
                {
                    e.value = mt_ref.value.defaultInitLiteral(loc);
                }
                else if ((pequals(ident_ref.value, Id.stringof.value)))
                {
                    Ref<BytePtr> s = ref(pcopy(mt_ref.value.toChars()));
                    e.value = new StringExp(loc, s.value);
                    Ref<Scope> sc = ref(new Scope().copy());
                    e.value = expressionSemantic(e.value, ptr(sc));
                }
                else if ((pequals(ident_ref.value, Id._mangleof.value)))
                {
                    e.value = visitType.invoke(mt_ref.value);
                }
                else
                {
                    e.value = getProperty(mt_ref.value.toBasetype(), loc, ident_ref.value, flag_ref.value);
                }
                return e.value;
            }
        };
        Function1<TypeTuple,Expression> visitTuple = new Function1<TypeTuple,Expression>(){
            public Expression invoke(TypeTuple mt) {
                Ref<Expression> e = ref(null);
                if ((pequals(ident_ref.value, Id.length.value)))
                {
                    e.value = new IntegerExp(loc, (long)(mt.arguments.value.get()).length.value, Type.tsize_t.value);
                }
                else if ((pequals(ident_ref.value, Id._init.value)))
                {
                    e.value = mt.defaultInitLiteral(loc);
                }
                else if (flag_ref.value != 0)
                {
                    e.value = null;
                }
                else
                {
                    error(loc, new BytePtr("no property `%s` for tuple `%s`"), ident_ref.value.toChars(), mt.toChars());
                    e.value = new ErrorExp();
                }
                return e.value;
            }
        };
        switch ((t.ty.value & 0xFF))
        {
            default:
            return t.isTypeBasic() != null ? visitBasic.invoke((TypeBasic)t) : visitType.invoke(t);
            case 34:
                return visitError.invoke((TypeError)t);
            case 41:
                return visitVector.invoke((TypeVector)t);
            case 9:
                return visitEnum.invoke((TypeEnum)t);
            case 37:
                return visitTuple.invoke((TypeTuple)t);
        }
    }

    public static void resolveExp(Expression e, Ptr<Type> pt, Ptr<Expression> pe, Ptr<Dsymbol> ps) {
        pt.set(0, null);
        pe.set(0, null);
        ps.set(0, null);
        Dsymbol s = null;
        {
            int __dispatch24 = 0;
            dispatched_24:
            do {
                switch (__dispatch24 != 0 ? __dispatch24 : (e.op.value & 0xFF))
                {
                    case 127:
                        pt.set(0, Type.terror.value);
                        return ;
                    case 20:
                        pt.set(0, e.type.value);
                        return ;
                    case 26:
                        s = ((VarExp)e).var.value;
                        if (s.isVarDeclaration() != null)
                        {
                            /*goto default*/ { __dispatch24 = -1; continue dispatched_24; }
                        }
                        break;
                    case 36:
                        s = ((TemplateExp)e).td.value;
                        break;
                    case 203:
                        s = ((ScopeExp)e).sds.value;
                        break;
                    case 161:
                        s = getDsymbol(e);
                        break;
                    case 37:
                        s = ((DotTemplateExp)e).td.value;
                        break;
                    default:
                    __dispatch24 = 0;
                    pe.set(0, e);
                    return ;
                }
            } while(__dispatch24 != 0);
        }
        ps.set(0, s);
    }

    public static void resolve(Type mt, Loc loc, Ptr<Scope> sc, Ptr<Expression> pe, Ptr<Type> pt, Ptr<Dsymbol> ps, boolean intypeid) {
        Ref<Loc> loc_ref = ref(loc);
        Ref<Ptr<Scope>> sc_ref = ref(sc);
        Ref<Ptr<Expression>> pe_ref = ref(pe);
        Ref<Ptr<Type>> pt_ref = ref(pt);
        Ref<Ptr<Dsymbol>> ps_ref = ref(ps);
        Ref<Boolean> intypeid_ref = ref(intypeid);
        Function1<Expression,Void> returnExp = new Function1<Expression,Void>(){
            public Void invoke(Expression e) {
                Ref<Expression> e_ref = ref(e);
                pt_ref.value.set(0, null);
                pe_ref.value.set(0, e_ref.value);
                ps_ref.value.set(0, null);
                return null;
            }
        };
        Function1<Type,Void> returnType = new Function1<Type,Void>(){
            public Void invoke(Type t) {
                Ref<Type> t_ref = ref(t);
                pt_ref.value.set(0, t_ref.value);
                pe_ref.value.set(0, null);
                ps_ref.value.set(0, null);
                return null;
            }
        };
        Function1<Dsymbol,Void> returnSymbol = new Function1<Dsymbol,Void>(){
            public Void invoke(Dsymbol s) {
                Ref<Dsymbol> s_ref = ref(s);
                pt_ref.value.set(0, null);
                pe_ref.value.set(0, null);
                ps_ref.value.set(0, s_ref.value);
                return null;
            }
        };
        Function0<Void> returnError = new Function0<Void>(){
            public Void invoke() {
                returnType.invoke(Type.terror.value);
                return null;
            }
        };
        Function1<Type,Void> visitType = new Function1<Type,Void>(){
            public Void invoke(Type mt) {
                Ref<Type> mt_ref = ref(mt);
                Ref<Type> t = ref(typeSemantic(mt_ref.value, loc_ref.value, sc_ref.value));
                assert(t.value != null);
                returnType.invoke(t.value);
                return null;
            }
        };
        Function1<TypeSArray,Void> visitSArray = new Function1<TypeSArray,Void>(){
            public Void invoke(TypeSArray mt) {
                Ref<TypeSArray> mt_ref = ref(mt);
                resolve(mt_ref.value.next.value, loc_ref.value, sc_ref.value, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                if (pe_ref.value.get() != null)
                {
                    {
                        Ref<Dsymbol> s = ref(getDsymbol(pe_ref.value.get()));
                        if ((s.value) != null)
                        {
                            pe_ref.value.set(0, (new DsymbolExp(loc_ref.value, s.value, true)));
                        }
                    }
                    returnExp.invoke(new ArrayExp(loc_ref.value, pe_ref.value.get(), mt_ref.value.dim.value));
                }
                else if (ps_ref.value.get() != null)
                {
                    Dsymbol s = ps_ref.value.get();
                    {
                        Ref<TupleDeclaration> tup = ref(s.isTupleDeclaration());
                        if ((tup.value) != null)
                        {
                            mt_ref.value.dim.value = semanticLength(sc_ref.value, tup.value, mt_ref.value.dim.value);
                            mt_ref.value.dim.value = mt_ref.value.dim.value.ctfeInterpret();
                            if (((mt_ref.value.dim.value.op.value & 0xFF) == 127))
                            {
                                returnError.invoke();
                                return null;
                            }
                            Ref<Long> d = ref(mt_ref.value.dim.value.toUInteger());
                            if ((d.value >= (long)(tup.value.objects.value.get()).length.value))
                            {
                                error(loc_ref.value, new BytePtr("tuple index `%llu` exceeds length %u"), d.value, (tup.value.objects.value.get()).length.value);
                                returnError.invoke();
                                return null;
                            }
                            Ref<RootObject> o = ref((tup.value.objects.value.get()).get((int)d.value));
                            if ((o.value.dyncast() == DYNCAST.dsymbol))
                            {
                                returnSymbol.invoke((Dsymbol)o.value);
                                return null;
                            }
                            if ((o.value.dyncast() == DYNCAST.expression))
                            {
                                Ref<Expression> e = ref((Expression)o.value);
                                if (((e.value.op.value & 0xFF) == 41))
                                {
                                    returnSymbol.invoke(((DsymbolExp)e.value).s.value);
                                    return null;
                                }
                                else
                                {
                                    returnExp.invoke(e.value);
                                    return null;
                                }
                            }
                            if ((o.value.dyncast() == DYNCAST.type))
                            {
                                returnType.invoke(((Type)o.value).addMod(mt_ref.value.mod.value));
                                return null;
                            }
                            Ref<Ptr<DArray<RootObject>>> objects = ref(refPtr(new DArray<RootObject>(1)));
                            objects.value.get().set(0, o.value);
                            returnSymbol.invoke(new TupleDeclaration(loc_ref.value, tup.value.ident.value, objects.value));
                            return null;
                        }
                        else
                        {
                            visitType.invoke(mt_ref.value);
                            return null;
                        }
                    }
                }
                else
                {
                    if ((((pt_ref.value.get()).ty.value & 0xFF) != ENUMTY.Terror))
                    {
                        mt_ref.value.next.value = pt_ref.value.get();
                    }
                    visitType.invoke(mt_ref.value);
                }
            }
        };
        Function1<TypeDArray,Void> visitDArray = new Function1<TypeDArray,Void>(){
            public Void invoke(TypeDArray mt) {
                Ref<TypeDArray> mt_ref = ref(mt);
                resolve(mt_ref.value.next.value, loc_ref.value, sc_ref.value, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                if (pe_ref.value.get() != null)
                {
                    {
                        Ref<Dsymbol> s = ref(getDsymbol(pe_ref.value.get()));
                        if ((s.value) != null)
                        {
                            pe_ref.value.set(0, (new DsymbolExp(loc_ref.value, s.value, true)));
                        }
                    }
                    returnExp.invoke(new ArrayExp(loc_ref.value, pe_ref.value.get(), null));
                }
                else if (ps_ref.value.get() != null)
                {
                    {
                        Ref<TupleDeclaration> tup = ref((ps_ref.value.get()).isTupleDeclaration());
                        if ((tup.value) != null)
                        {
                        }
                        else
                        {
                            visitType.invoke(mt_ref.value);
                        }
                    }
                }
                else
                {
                    if ((((pt_ref.value.get()).ty.value & 0xFF) != ENUMTY.Terror))
                    {
                        mt_ref.value.next.value = pt_ref.value.get();
                    }
                    visitType.invoke(mt_ref.value);
                }
                return null;
            }
        };
        Function1<TypeAArray,Void> visitAArray = new Function1<TypeAArray,Void>(){
            public Void invoke(TypeAArray mt) {
                Ref<TypeAArray> mt_ref = ref(mt);
                if (((mt_ref.value.index.value.ty.value & 0xFF) == ENUMTY.Tident) || ((mt_ref.value.index.value.ty.value & 0xFF) == ENUMTY.Tinstance) || ((mt_ref.value.index.value.ty.value & 0xFF) == ENUMTY.Tsarray))
                {
                    Ref<Expression> e = ref(null);
                    Ref<Type> t = ref(null);
                    Ref<Dsymbol> s = ref(null);
                    resolve(mt_ref.value.index.value, loc_ref.value, sc_ref.value, ptr(e), ptr(t), ptr(s), intypeid_ref.value);
                    if (e.value != null)
                    {
                        Ref<TypeSArray> tsa = ref(new TypeSArray(mt_ref.value.next.value, e.value));
                        tsa.value.mod.value = mt_ref.value.mod.value;
                        resolve(tsa.value, loc_ref.value, sc_ref.value, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                        return null;
                    }
                    else if (t.value != null)
                    {
                        mt_ref.value.index.value = t.value;
                    }
                    else
                    {
                        error(loc_ref.value, new BytePtr("index is not a type or an expression"));
                    }
                }
                visitType.invoke(mt_ref.value);
            }
        };
        Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>(){
            public Void invoke(Dsymbol s) {
                Ref<Dsymbol> s_ref = ref(s);
                semanticOnMixin.invoke(s_ref.value);
                return null;
            }
        };
        Function1<TypeIdentifier,Void> visitIdentifier = new Function1<TypeIdentifier,Void>(){
            public Void invoke(TypeIdentifier mt) {
                Ref<TypeIdentifier> mt_ref = ref(mt);
                if (mt_ref.value.ident.value.equals(Id._super.value) || mt_ref.value.ident.value.equals(Id.This.value) && (hasThis(sc_ref.value) == null))
                {
                    if (mt_ref.value.ident.value.equals(Id._super.value))
                    {
                        error(mt_ref.value.loc, new BytePtr("Using `super` as a type is obsolete. Use `typeof(super)` instead"));
                    }
                    if (mt_ref.value.ident.value.equals(Id.This.value))
                    {
                        error(mt_ref.value.loc, new BytePtr("Using `this` as a type is obsolete. Use `typeof(this)` instead"));
                    }
                    {
                        Ref<AggregateDeclaration> ad = ref((sc_ref.value.get()).getStructClassScope());
                        if ((ad.value) != null)
                        {
                            {
                                Ref<ClassDeclaration> cd = ref(ad.value.isClassDeclaration());
                                if ((cd.value) != null)
                                {
                                    if (mt_ref.value.ident.value.equals(Id.This.value))
                                    {
                                        mt_ref.value.ident.value = cd.value.ident.value;
                                    }
                                    else if ((cd.value.baseClass.value != null) && mt_ref.value.ident.value.equals(Id._super.value))
                                    {
                                        mt_ref.value.ident.value = cd.value.baseClass.value.ident.value;
                                    }
                                }
                                else
                                {
                                    Ref<StructDeclaration> sd = ref(ad.value.isStructDeclaration());
                                    if ((sd.value != null) && mt_ref.value.ident.value.equals(Id.This.value))
                                    {
                                        mt_ref.value.ident.value = sd.value.ident.value;
                                    }
                                }
                            }
                        }
                    }
                }
                if ((pequals(mt_ref.value.ident.value, Id.ctfe.value)))
                {
                    error(loc_ref.value, new BytePtr("variable `__ctfe` cannot be read at compile time"));
                    returnError.invoke();
                    return null;
                }
                Ref<Dsymbol> scopesym = ref(null);
                Ref<Dsymbol> s = ref((sc_ref.value.get()).search(loc_ref.value, mt_ref.value.ident.value, ptr(scopesym), 0));
                if ((s.value == null) && ((sc_ref.value.get()).enclosing.value != null))
                {
                    Ref<ScopeDsymbol> sds = ref(((sc_ref.value.get()).enclosing.value.get()).scopesym.value);
                    if ((sds.value != null) && (sds.value.members.value != null))
                    {
                        Function1<Dsymbol,Void> semanticOnMixin = new Function1<Dsymbol,Void>(){
                            public Void invoke(Dsymbol member) {
                                {
                                    Ref<CompileDeclaration> compileDecl = ref(member.isCompileDeclaration());
                                    if ((compileDecl.value) != null)
                                    {
                                        dsymbolSemantic(compileDecl.value, sc_ref.value);
                                    }
                                    else {
                                        Ref<TemplateMixin> mixinTempl = ref(member.isTemplateMixin());
                                        if ((mixinTempl.value) != null)
                                        {
                                            dsymbolSemantic(mixinTempl.value, sc_ref.value);
                                        }
                                    }
                                }
                                return null;
                            }
                        };
                        foreachDsymbol(sds.value.members.value, __lambda3);
                        s.value = (sc_ref.value.get()).search(loc_ref.value, mt_ref.value.ident.value, ptr(scopesym), 0);
                    }
                }
                if (s.value != null)
                {
                    {
                        Ref<FuncDeclaration> f = ref(s.value.isFuncDeclaration());
                        if ((f.value) != null)
                        {
                            {
                                Ref<TemplateDeclaration> td = ref(getFuncTemplateDecl(f.value));
                                if ((td.value) != null)
                                {
                                    if (td.value.overroot.value != null)
                                    {
                                        td.value = td.value.overroot.value;
                                    }
                                    s.value = td.value;
                                }
                            }
                        }
                    }
                }
                resolveHelper(mt_ref.value, loc_ref.value, sc_ref.value, s.value, scopesym.value, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                if (pt_ref.value.get() != null)
                {
                    pt_ref.value.set(0, (pt_ref.value.get()).addMod(mt_ref.value.mod.value));
                }
            }
        };
        Function1<TypeInstance,Void> visitInstance = new Function1<TypeInstance,Void>(){
            public Void invoke(TypeInstance mt) {
                Ref<TypeInstance> mt_ref = ref(mt);
                dsymbolSemantic(mt_ref.value.tempinst.value, sc_ref.value);
                if ((global.gag.value == 0) && mt_ref.value.tempinst.value.errors.value)
                {
                    returnError.invoke();
                    return null;
                }
                resolveHelper(mt_ref.value, loc_ref.value, sc_ref.value, mt_ref.value.tempinst.value, null, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                if (pt_ref.value.get() != null)
                {
                    pt_ref.value.set(0, (pt_ref.value.get()).addMod(mt_ref.value.mod.value));
                }
            }
        };
        Function1<TypeTypeof,Void> visitTypeof = new Function1<TypeTypeof,Void>(){
            public Void invoke(TypeTypeof mt) {
                Ref<TypeTypeof> mt_ref = ref(mt);
                if ((sc_ref.value == null))
                {
                    error(loc_ref.value, new BytePtr("Invalid scope."));
                    returnError.invoke();
                    return null;
                }
                if (mt_ref.value.inuse.value != 0)
                {
                    mt_ref.value.inuse.value = 2;
                    error(loc_ref.value, new BytePtr("circular `typeof` definition"));
                /*Lerr:*/
                    mt_ref.value.inuse.value--;
                    returnError.invoke();
                    return null;
                }
                mt_ref.value.inuse.value++;
                Ref<Ptr<Scope>> sc2 = ref((sc_ref.value.get()).push());
                (sc2.value.get()).intypeof.value = 1;
                Ref<Expression> exp2 = ref(expressionSemantic(mt_ref.value.exp.value, sc2.value));
                exp2.value = resolvePropertiesOnly(sc2.value, exp2.value);
                (sc2.value.get()).pop();
                if (((exp2.value.op.value & 0xFF) == 127))
                {
                    if (global.gag.value == 0)
                    {
                        mt_ref.value.exp.value = exp2.value;
                    }
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                mt_ref.value.exp.value = exp2.value;
                if (((mt_ref.value.exp.value.op.value & 0xFF) == 20) || ((mt_ref.value.exp.value.op.value & 0xFF) == 203))
                {
                    if (mt_ref.value.exp.value.checkType())
                    {
                        /*goto Lerr*/throw Dispatch0.INSTANCE;
                    }
                }
                {
                    Ref<FuncDeclaration> f = ref(((mt_ref.value.exp.value.op.value & 0xFF) == 26) ? ((VarExp)mt_ref.value.exp.value).var.value.isFuncDeclaration() : ((mt_ref.value.exp.value.op.value & 0xFF) == 27) ? ((DotVarExp)mt_ref.value.exp.value).var.value.isFuncDeclaration() : null);
                    if ((f.value) != null)
                    {
                        if (f.value.checkForwardRef(loc_ref.value))
                        {
                            /*goto Lerr*/throw Dispatch0.INSTANCE;
                        }
                    }
                }
                {
                    Ref<FuncDeclaration> f = ref(isFuncAddress(mt_ref.value.exp.value, null));
                    if ((f.value) != null)
                    {
                        if (f.value.checkForwardRef(loc_ref.value))
                        {
                            /*goto Lerr*/throw Dispatch0.INSTANCE;
                        }
                    }
                }
                Ref<Type> t = ref(mt_ref.value.exp.value.type.value);
                if (t.value == null)
                {
                    error(loc_ref.value, new BytePtr("expression `%s` has no type"), mt_ref.value.exp.value.toChars());
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                if (((t.value.ty.value & 0xFF) == ENUMTY.Ttypeof))
                {
                    error(loc_ref.value, new BytePtr("forward reference to `%s`"), mt_ref.value.toChars());
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                if ((mt_ref.value.idents.length.value == 0))
                {
                    returnType.invoke(t.value.addMod(mt_ref.value.mod.value));
                }
                else
                {
                    {
                        Ref<Dsymbol> s = ref(t.value.toDsymbol(sc_ref.value));
                        if ((s.value) != null)
                        {
                            resolveHelper(mt_ref.value, loc_ref.value, sc_ref.value, s.value, null, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                        }
                        else
                        {
                            Ref<Expression> e = ref(typeToExpressionHelper(mt_ref.value, new TypeExp(loc_ref.value, t.value), 0));
                            e.value = expressionSemantic(e.value, sc_ref.value);
                            resolveExp(e.value, pt_ref.value, pe_ref.value, ps_ref.value);
                        }
                    }
                    if (pt_ref.value.get() != null)
                    {
                        pt_ref.value.set(0, (pt_ref.value.get()).addMod(mt_ref.value.mod.value));
                    }
                }
                mt_ref.value.inuse.value--;
            }
        };
        Function1<TypeReturn,Void> visitReturn = new Function1<TypeReturn,Void>(){
            public Void invoke(TypeReturn mt) {
                Ref<TypeReturn> mt_ref = ref(mt);
                Ref<Type> t = ref(null);
                {
                    Ref<FuncDeclaration> func = ref((sc_ref.value.get()).func.value);
                    if (func.value == null)
                    {
                        error(loc_ref.value, new BytePtr("`typeof(return)` must be inside function"));
                        returnError.invoke();
                        return null;
                    }
                    if (func.value.fes.value != null)
                    {
                        func.value = func.value.fes.value.func.value;
                    }
                    t.value = func.value.type.value.nextOf();
                    if (t.value == null)
                    {
                        error(loc_ref.value, new BytePtr("cannot use `typeof(return)` inside function `%s` with inferred return type"), (sc_ref.value.get()).func.value.toChars());
                        returnError.invoke();
                        return null;
                    }
                }
                if ((mt_ref.value.idents.length.value == 0))
                {
                    returnType.invoke(t.value.addMod(mt_ref.value.mod.value));
                    return null;
                }
                else
                {
                    {
                        Ref<Dsymbol> s = ref(t.value.toDsymbol(sc_ref.value));
                        if ((s.value) != null)
                        {
                            resolveHelper(mt_ref.value, loc_ref.value, sc_ref.value, s.value, null, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                        }
                        else
                        {
                            Ref<Expression> e = ref(typeToExpressionHelper(mt_ref.value, new TypeExp(loc_ref.value, t.value), 0));
                            e.value = expressionSemantic(e.value, sc_ref.value);
                            resolveExp(e.value, pt_ref.value, pe_ref.value, ps_ref.value);
                        }
                    }
                    if (pt_ref.value.get() != null)
                    {
                        pt_ref.value.set(0, (pt_ref.value.get()).addMod(mt_ref.value.mod.value));
                    }
                }
            }
        };
        Function1<TypeSlice,Void> visitSlice = new Function1<TypeSlice,Void>(){
            public Void invoke(TypeSlice mt) {
                Ref<TypeSlice> mt_ref = ref(mt);
                resolve(mt_ref.value.next.value, loc_ref.value, sc_ref.value, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                if (pe_ref.value.get() != null)
                {
                    {
                        Ref<Dsymbol> s = ref(getDsymbol(pe_ref.value.get()));
                        if ((s.value) != null)
                        {
                            pe_ref.value.set(0, (new DsymbolExp(loc_ref.value, s.value, true)));
                        }
                    }
                    returnExp.invoke(new ArrayExp(loc_ref.value, pe_ref.value.get(), new IntervalExp(loc_ref.value, mt_ref.value.lwr.value, mt_ref.value.upr.value)));
                    return null;
                }
                else if (ps_ref.value.get() != null)
                {
                    Dsymbol s = ps_ref.value.get();
                    Ref<TupleDeclaration> td = ref(s.isTupleDeclaration());
                    if (td.value != null)
                    {
                        Ref<ScopeDsymbol> sym = ref(new ArrayScopeSymbol(sc_ref.value, td.value));
                        sym.value.parent.value = (sc_ref.value.get()).scopesym.value;
                        sc_ref.value = (sc_ref.value.get()).push(sym.value);
                        sc_ref.value = (sc_ref.value.get()).startCTFE();
                        mt_ref.value.lwr.value = expressionSemantic(mt_ref.value.lwr.value, sc_ref.value);
                        mt_ref.value.upr.value = expressionSemantic(mt_ref.value.upr.value, sc_ref.value);
                        sc_ref.value = (sc_ref.value.get()).endCTFE();
                        sc_ref.value = (sc_ref.value.get()).pop();
                        mt_ref.value.lwr.value = mt_ref.value.lwr.value.ctfeInterpret();
                        mt_ref.value.upr.value = mt_ref.value.upr.value.ctfeInterpret();
                        Ref<Long> i1 = ref(mt_ref.value.lwr.value.toUInteger());
                        Ref<Long> i2 = ref(mt_ref.value.upr.value.toUInteger());
                        if (!((i1.value <= i2.value) && (i2.value <= (long)(td.value.objects.value.get()).length.value)))
                        {
                            error(loc_ref.value, new BytePtr("slice `[%llu..%llu]` is out of range of [0..%u]"), i1.value, i2.value, (td.value.objects.value.get()).length.value);
                            returnError.invoke();
                            return null;
                        }
                        if ((i1.value == 0L) && (i2.value == (long)(td.value.objects.value.get()).length.value))
                        {
                            returnSymbol.invoke(td.value);
                            return null;
                        }
                        Ref<Ptr<DArray<RootObject>>> objects = ref(refPtr(new DArray<RootObject>((int)(i2.value - i1.value))));
                        {
                            IntRef i = ref(0);
                            for (; (i.value < (objects.value.get()).length.value);i.value++){
                                objects.value.get().set(i.value, (td.value.objects.value.get()).get((int)i1.value + i.value));
                            }
                        }
                        returnSymbol.invoke(new TupleDeclaration(loc_ref.value, td.value.ident.value, objects.value));
                        return null;
                    }
                    else
                    {
                        visitType.invoke(mt_ref.value);
                    }
                }
                else
                {
                    if ((((pt_ref.value.get()).ty.value & 0xFF) != ENUMTY.Terror))
                    {
                        mt_ref.value.next.value = pt_ref.value.get();
                    }
                    visitType.invoke(mt_ref.value);
                }
            }
        };
        switch ((mt.ty.value & 0xFF))
        {
            default:
            visitType.invoke(mt);
            break;
            case 1:
                visitSArray.invoke((TypeSArray)mt);
                break;
            case 0:
                visitDArray.invoke((TypeDArray)mt);
                break;
            case 2:
                visitAArray.invoke((TypeAArray)mt);
                break;
            case 6:
                visitIdentifier.invoke((TypeIdentifier)mt);
                break;
            case 35:
                visitInstance.invoke((TypeInstance)mt);
                break;
            case 36:
                visitTypeof.invoke((TypeTypeof)mt);
                break;
            case 39:
                visitReturn.invoke((TypeReturn)mt);
                break;
            case 38:
                visitSlice.invoke((TypeSlice)mt);
                break;
        }
    }

    // defaulted all parameters starting with #7
    public static void resolve(Type mt, Loc loc, Ptr<Scope> sc, Ptr<Expression> pe, Ptr<Type> pt, Ptr<Dsymbol> ps) {
        return resolve(mt, loc, sc, pe, pt, ps, false);
    }

    public static Expression dotExp(Type mt, Ptr<Scope> sc, Expression e, Identifier ident, int flag) {
        Ref<Ptr<Scope>> sc_ref = ref(sc);
        Ref<Expression> e_ref = ref(e);
        Ref<Identifier> ident_ref = ref(ident);
        IntRef flag_ref = ref(flag);
        Function1<Type,Expression> visitType = new Function1<Type,Expression>(){
            public Expression invoke(Type mt) {
                Ref<Type> mt_ref = ref(mt);
                Ref<VarDeclaration> v = ref(null);
                Ref<Expression> ex = ref(e_ref.value);
                for (; ((ex.value.op.value & 0xFF) == 99);) {
                    ex.value = ((CommaExp)ex.value).e2.value;
                }
                if (((ex.value.op.value & 0xFF) == 27))
                {
                    DotVarExp dv = (DotVarExp)ex.value;
                    v.value = dv.var.value.isVarDeclaration();
                }
                else if (((ex.value.op.value & 0xFF) == 26))
                {
                    VarExp ve = (VarExp)ex.value;
                    v.value = ve.var.value.isVarDeclaration();
                }
                try {
                    if (v.value != null)
                    {
                        if ((pequals(ident_ref.value, Id.offsetof.value)))
                        {
                            if (v.value.isField())
                            {
                                Ref<AggregateDeclaration> ad = ref(v.value.toParent().isAggregateDeclaration());
                                objc().checkOffsetof(e_ref.value, ad.value);
                                ad.value.size(e_ref.value.loc.value);
                                if ((ad.value.sizeok.value != Sizeok.done))
                                {
                                    return new ErrorExp();
                                }
                                return new IntegerExp(e_ref.value.loc.value, (long)v.value.offset.value, Type.tsize_t.value);
                            }
                        }
                        else if ((pequals(ident_ref.value, Id._init.value)))
                        {
                            Type tb = mt_ref.value.toBasetype();
                            e_ref.value = mt_ref.value.defaultInitLiteral(e_ref.value.loc.value);
                            if (((tb.ty.value & 0xFF) == ENUMTY.Tstruct) && tb.needsNested())
                            {
                                e_ref.value.isStructLiteralExp().useStaticInit.value = true;
                            }
                            /*goto Lreturn*/throw Dispatch0.INSTANCE;
                        }
                    }
                    if ((pequals(ident_ref.value, Id.stringof.value)))
                    {
                        Ref<BytePtr> s = ref(pcopy(e_ref.value.toChars()));
                        e_ref.value = new StringExp(e_ref.value.loc.value, s.value);
                    }
                    else
                    {
                        e_ref.value = getProperty(mt_ref.value, e_ref.value.loc.value, ident_ref.value, flag_ref.value & DotExpFlag.gag);
                    }
                }
                catch(Dispatch0 __d){}
            /*Lreturn:*/
                if (e_ref.value != null)
                {
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                }
                return e_ref.value;
            }
        };
        Function1<TypeError,Expression> visitError = new Function1<TypeError,Expression>(){
            public Expression invoke(TypeError _param_0) {
                return new ErrorExp();
            }
        };
        Function1<TypeBasic,Expression> visitBasic = new Function1<TypeBasic,Expression>(){
            public Expression invoke(TypeBasic mt) {
                Ref<TypeBasic> mt_ref = ref(mt);
                Ref<Type> t = ref(null);
                if ((pequals(ident_ref.value, Id.re.value)))
                {
                    {
                        int __dispatch26 = 0;
                        dispatched_26:
                        do {
                            switch (__dispatch26 != 0 ? __dispatch26 : (mt_ref.value.ty.value & 0xFF))
                            {
                                case 27:
                                    t.value = Type.tfloat32.value;
                                    /*goto L1*/{ __dispatch26 = -1; continue dispatched_26; }
                                case 28:
                                    t.value = Type.tfloat64.value;
                                    /*goto L1*/{ __dispatch26 = -1; continue dispatched_26; }
                                case 29:
                                    t.value = Type.tfloat80.value;
                                    /*goto L1*/{ __dispatch26 = -1; continue dispatched_26; }
                                /*L1:*/
                                case -1:
                                __dispatch26 = 0;
                                    e_ref.value = e_ref.value.castTo(sc_ref.value, t.value);
                                    break;
                                case 21:
                                case 22:
                                case 23:
                                    break;
                                case 24:
                                    t.value = Type.tfloat32.value;
                                    /*goto L2*/{ __dispatch26 = -2; continue dispatched_26; }
                                case 25:
                                    t.value = Type.tfloat64.value;
                                    /*goto L2*/{ __dispatch26 = -2; continue dispatched_26; }
                                case 26:
                                    t.value = Type.tfloat80.value;
                                    /*goto L2*/{ __dispatch26 = -2; continue dispatched_26; }
                                /*L2:*/
                                case -2:
                                __dispatch26 = 0;
                                    e_ref.value = new RealExp(e_ref.value.loc.value, CTFloat.zero.value, t.value);
                                    break;
                                default:
                                e_ref.value = getProperty(mt_ref.value.Type, e_ref.value.loc.value, ident_ref.value, flag_ref.value);
                                break;
                            }
                        } while(__dispatch26 != 0);
                    }
                }
                else if ((pequals(ident_ref.value, Id.im.value)))
                {
                    Ref<Type> t2 = ref(null);
                    {
                        int __dispatch27 = 0;
                        dispatched_27:
                        do {
                            switch (__dispatch27 != 0 ? __dispatch27 : (mt_ref.value.ty.value & 0xFF))
                            {
                                case 27:
                                    t.value = Type.timaginary32.value;
                                    t2.value = Type.tfloat32.value;
                                    /*goto L3*/{ __dispatch27 = -1; continue dispatched_27; }
                                case 28:
                                    t.value = Type.timaginary64.value;
                                    t2.value = Type.tfloat64.value;
                                    /*goto L3*/{ __dispatch27 = -1; continue dispatched_27; }
                                case 29:
                                    t.value = Type.timaginary80.value;
                                    t2.value = Type.tfloat80.value;
                                    /*goto L3*/{ __dispatch27 = -1; continue dispatched_27; }
                                /*L3:*/
                                case -1:
                                __dispatch27 = 0;
                                    e_ref.value = e_ref.value.castTo(sc_ref.value, t.value);
                                    e_ref.value.type.value = t2.value;
                                    break;
                                case 24:
                                    t.value = Type.tfloat32.value;
                                    /*goto L4*/{ __dispatch27 = -2; continue dispatched_27; }
                                case 25:
                                    t.value = Type.tfloat64.value;
                                    /*goto L4*/{ __dispatch27 = -2; continue dispatched_27; }
                                case 26:
                                    t.value = Type.tfloat80.value;
                                    /*goto L4*/{ __dispatch27 = -2; continue dispatched_27; }
                                /*L4:*/
                                case -2:
                                __dispatch27 = 0;
                                    e_ref.value = e_ref.value.copy();
                                    e_ref.value.type.value = t.value;
                                    break;
                                case 21:
                                case 22:
                                case 23:
                                    e_ref.value = new RealExp(e_ref.value.loc.value, CTFloat.zero.value, mt_ref.value);
                                    break;
                                default:
                                e_ref.value = getProperty(mt_ref.value.Type, e_ref.value.loc.value, ident_ref.value, flag_ref.value);
                                break;
                            }
                        } while(__dispatch27 != 0);
                    }
                }
                else
                {
                    return visitType.invoke(mt_ref.value);
                }
                if (((flag_ref.value & 1) == 0) || (e_ref.value != null))
                {
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                }
                return e_ref.value;
            }
        };
        Function1<TypeVector,Expression> visitVector = new Function1<TypeVector,Expression>(){
            public Expression invoke(TypeVector mt) {
                Ref<TypeVector> mt_ref = ref(mt);
                if ((pequals(ident_ref.value, Id.ptr.value)) && ((e_ref.value.op.value & 0xFF) == 18))
                {
                    e_ref.value = new AddrExp(e_ref.value.loc.value, e_ref.value);
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                    return e_ref.value.castTo(sc_ref.value, mt_ref.value.basetype.value.nextOf().pointerTo());
                }
                if ((pequals(ident_ref.value, Id.array.value)))
                {
                    e_ref.value = new VectorArrayExp(e_ref.value.loc.value, e_ref.value);
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                    return e_ref.value;
                }
                if ((pequals(ident_ref.value, Id._init.value)) || (pequals(ident_ref.value, Id.offsetof.value)) || (pequals(ident_ref.value, Id.stringof.value)) || (pequals(ident_ref.value, Id.__xalignof.value)))
                {
                    return visitType.invoke(mt_ref.value);
                }
                return dotExp(mt_ref.value.basetype.value, sc_ref.value, e_ref.value.castTo(sc_ref.value, mt_ref.value.basetype.value), ident_ref.value, flag_ref.value);
            }
        };
        Function1<TypeArray,Expression> visitArray = new Function1<TypeArray,Expression>(){
            public Expression invoke(TypeArray mt) {
                Ref<TypeArray> mt_ref = ref(mt);
                e_ref.value = visitType.invoke(mt_ref.value);
                if (((flag_ref.value & 1) == 0) || (e_ref.value != null))
                {
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                }
                return e_ref.value;
            }
        };
        Function1<TypeSArray,Expression> visitSArray = new Function1<TypeSArray,Expression>(){
            public Expression invoke(TypeSArray mt) {
                Ref<TypeSArray> mt_ref = ref(mt);
                if ((pequals(ident_ref.value, Id.length.value)))
                {
                    Ref<Loc> oldLoc = ref(e_ref.value.loc.value.copy());
                    e_ref.value = mt_ref.value.dim.value.copy();
                    e_ref.value.loc.value = oldLoc.value.copy();
                }
                else if ((pequals(ident_ref.value, Id.ptr.value)))
                {
                    if (((e_ref.value.op.value & 0xFF) == 20))
                    {
                        e_ref.value.error(new BytePtr("`%s` is not an expression"), e_ref.value.toChars());
                        return new ErrorExp();
                    }
                    else if (((flag_ref.value & DotExpFlag.noDeref) == 0) && ((sc_ref.value.get()).func.value != null) && ((sc_ref.value.get()).intypeof.value == 0) && (sc_ref.value.get()).func.value.setUnsafe() && (((sc_ref.value.get()).flags.value & 8) == 0))
                    {
                        e_ref.value.error(new BytePtr("`%s.ptr` cannot be used in `@safe` code, use `&%s[0]` instead"), e_ref.value.toChars(), e_ref.value.toChars());
                        return new ErrorExp();
                    }
                    e_ref.value = e_ref.value.castTo(sc_ref.value, e_ref.value.type.value.nextOf().pointerTo());
                }
                else
                {
                    e_ref.value = visitArray.invoke(mt_ref.value);
                }
                if (((flag_ref.value & 1) == 0) || (e_ref.value != null))
                {
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                }
                return e_ref.value;
            }
        };
        Function1<TypeDArray,Expression> visitDArray = new Function1<TypeDArray,Expression>(){
            public Expression invoke(TypeDArray mt) {
                Ref<TypeDArray> mt_ref = ref(mt);
                if (((e_ref.value.op.value & 0xFF) == 20) && (pequals(ident_ref.value, Id.length.value)) || (pequals(ident_ref.value, Id.ptr.value)))
                {
                    e_ref.value.error(new BytePtr("`%s` is not an expression"), e_ref.value.toChars());
                    return new ErrorExp();
                }
                if ((pequals(ident_ref.value, Id.length.value)))
                {
                    if (((e_ref.value.op.value & 0xFF) == 121))
                    {
                        StringExp se = (StringExp)e_ref.value;
                        return new IntegerExp(se.loc.value, (long)se.len.value, Type.tsize_t.value);
                    }
                    if (((e_ref.value.op.value & 0xFF) == 13))
                    {
                        return new IntegerExp(e_ref.value.loc.value, 0L, Type.tsize_t.value);
                    }
                    if (checkNonAssignmentArrayOp(e_ref.value, false))
                    {
                        return new ErrorExp();
                    }
                    e_ref.value = new ArrayLengthExp(e_ref.value.loc.value, e_ref.value);
                    e_ref.value.type.value = Type.tsize_t.value;
                    return e_ref.value;
                }
                else if ((pequals(ident_ref.value, Id.ptr.value)))
                {
                    if (((flag_ref.value & DotExpFlag.noDeref) == 0) && ((sc_ref.value.get()).func.value != null) && ((sc_ref.value.get()).intypeof.value == 0) && (sc_ref.value.get()).func.value.setUnsafe() && (((sc_ref.value.get()).flags.value & 8) == 0))
                    {
                        e_ref.value.error(new BytePtr("`%s.ptr` cannot be used in `@safe` code, use `&%s[0]` instead"), e_ref.value.toChars(), e_ref.value.toChars());
                        return new ErrorExp();
                    }
                    return e_ref.value.castTo(sc_ref.value, mt_ref.value.next.value.pointerTo());
                }
                else
                {
                    return visitArray.invoke(mt_ref.value);
                }
            }
        };
        Function1<TypeAArray,Expression> visitAArray = new Function1<TypeAArray,Expression>(){
            public Expression invoke(TypeAArray mt) {
                Ref<TypeAArray> mt_ref = ref(mt);
                if ((pequals(ident_ref.value, Id.length.value)))
                {
                    if ((typesem.visitAArrayfd_aaLen.value == null))
                    {
                        Ref<Ptr<DArray<Parameter>>> fparams = ref(refPtr(new DArray<Parameter>()));
                        (fparams.value.get()).push(new Parameter(2048L, mt_ref.value, null, null, null));
                        typesem.visitAArrayfd_aaLen.value = FuncDeclaration.genCfunc(fparams.value, Type.tsize_t.value, Id.aaLen.value, 0L);
                        TypeFunction tf = typesem.visitAArrayfd_aaLen.value.type.value.toTypeFunction();
                        tf.purity.value = PURE.const_;
                        tf.isnothrow.value = true;
                        tf.isnogc.value = false;
                    }
                    Ref<Expression> ev = ref(new VarExp(e_ref.value.loc.value, typesem.visitAArrayfd_aaLen.value, false));
                    e_ref.value = new CallExp(e_ref.value.loc.value, ev.value, e_ref.value);
                    e_ref.value.type.value = typesem.visitAArrayfd_aaLen.value.type.value.toTypeFunction().next.value;
                    return e_ref.value;
                }
                else
                {
                    return visitType.invoke(mt_ref.value);
                }
            }
        };
        Function1<TypeReference,Expression> visitReference = new Function1<TypeReference,Expression>(){
            public Expression invoke(TypeReference mt) {
                return dotExp(mt.next.value, sc_ref.value, e_ref.value, ident_ref.value, flag_ref.value);
            }
        };
        Function1<TypeDelegate,Expression> visitDelegate = new Function1<TypeDelegate,Expression>(){
            public Expression invoke(TypeDelegate mt) {
                Ref<TypeDelegate> mt_ref = ref(mt);
                if ((pequals(ident_ref.value, Id.ptr.value)))
                {
                    e_ref.value = new DelegatePtrExp(e_ref.value.loc.value, e_ref.value);
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                }
                else if ((pequals(ident_ref.value, Id.funcptr.value)))
                {
                    if (((flag_ref.value & DotExpFlag.noDeref) == 0) && ((sc_ref.value.get()).func.value != null) && ((sc_ref.value.get()).intypeof.value == 0) && (sc_ref.value.get()).func.value.setUnsafe() && (((sc_ref.value.get()).flags.value & 8) == 0))
                    {
                        e_ref.value.error(new BytePtr("`%s.funcptr` cannot be used in `@safe` code"), e_ref.value.toChars());
                        return new ErrorExp();
                    }
                    e_ref.value = new DelegateFuncptrExp(e_ref.value.loc.value, e_ref.value);
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                }
                else
                {
                    return visitType.invoke(mt_ref.value);
                }
                return e_ref.value;
            }
        };
        Function5<Type,Ptr<Scope>,Expression,Identifier,Integer,Expression> noMember = new Function5<Type,Ptr<Scope>,Expression,Identifier,Integer,Expression>(){
            public Expression invoke(Type mt, Ptr<Scope> sc, Expression e, Identifier ident, Integer flag) {
                Ref<Type> mt_ref = ref(mt);
                Ref<Ptr<Scope>> sc_ref = ref(sc);
                Ref<Expression> e_ref = ref(e);
                Ref<Identifier> ident_ref = ref(ident);
                IntRef flag_ref = ref(flag);
                Ref<Boolean> gagError = ref(((flag_ref.value & 1) != 0));
                Function1<Expression,Expression> returnExp = new Function1<Expression,Expression>(){
                    public Expression invoke(Expression e) {
                        Ref<Expression> e_ref = ref(e);
                        typesem.noMembernest.value -= 1;
                        return e_ref.value;
                    }
                };
                if (((typesem.noMembernest.value += 1) > 500))
                {
                    error(e_ref.value.loc.value, new BytePtr("cannot resolve identifier `%s`"), ident_ref.value.toChars());
                    return returnExp.invoke(gagError.value ? null : new ErrorExp());
                }
                assert(((mt_ref.value.ty.value & 0xFF) == ENUMTY.Tstruct) || ((mt_ref.value.ty.value & 0xFF) == ENUMTY.Tclass));
                Ref<AggregateDeclaration> sym = ref(mt_ref.value.toDsymbol(sc_ref.value).isAggregateDeclaration());
                assert(sym.value != null);
                if ((!pequals(ident_ref.value, Id.__sizeof.value)) && (!pequals(ident_ref.value, Id.__xalignof.value)) && (!pequals(ident_ref.value, Id._init.value)) && (!pequals(ident_ref.value, Id._mangleof.value)) && (!pequals(ident_ref.value, Id.stringof.value)) && (!pequals(ident_ref.value, Id.offsetof.value)) && (!pequals(ident_ref.value, Id.ctor.value)) && (!pequals(ident_ref.value, Id.dtor.value)) && (!pequals(ident_ref.value, Id.__xdtor.value)) && (!pequals(ident_ref.value, Id.postblit.value)) && (!pequals(ident_ref.value, Id.__xpostblit.value)))
                {
                    {
                        Ref<Dsymbol> fd = ref(search_function(sym.value, Id.opDot.value));
                        if ((fd.value) != null)
                        {
                            e_ref.value = build_overload(e_ref.value.loc.value, sc_ref.value, e_ref.value, null, fd.value);
                            e_ref.value.deprecation(new BytePtr("`opDot` is deprecated. Use `alias this`"));
                            e_ref.value = new DotIdExp(e_ref.value.loc.value, e_ref.value, ident_ref.value);
                            return returnExp.invoke(expressionSemantic(e_ref.value, sc_ref.value));
                        }
                    }
                    {
                        Ref<Dsymbol> fd = ref(search_function(sym.value, Id.opDispatch.value));
                        if ((fd.value) != null)
                        {
                            Ref<TemplateDeclaration> td = ref(fd.value.isTemplateDeclaration());
                            if (td.value == null)
                            {
                                fd.value.error(new BytePtr("must be a template `opDispatch(string s)`, not a %s"), fd.value.kind());
                                return returnExp.invoke(new ErrorExp());
                            }
                            Ref<StringExp> se = ref(new StringExp(e_ref.value.loc.value, ident_ref.value.toChars()));
                            Ref<Ptr<DArray<RootObject>>> tiargs = ref(refPtr(new DArray<RootObject>()));
                            (tiargs.value.get()).push(se.value);
                            Ref<DotTemplateInstanceExp> dti = ref(new DotTemplateInstanceExp(e_ref.value.loc.value, e_ref.value, Id.opDispatch.value, tiargs.value));
                            dti.value.ti.tempdecl.value = td.value;
                            IntRef errors = ref(gagError.value ? global.startGagging() : 0);
                            e_ref.value = semanticY(dti.value, sc_ref.value, 0);
                            if (gagError.value && global.endGagging(errors.value))
                            {
                                e_ref.value = null;
                            }
                            return returnExp.invoke(e_ref.value);
                        }
                    }
                    Ref<Expression> alias_e = ref(resolveAliasThis(sc_ref.value, e_ref.value, gagError.value));
                    if ((alias_e.value != null) && (!pequals(alias_e.value, e_ref.value)))
                    {
                        Ref<DotIdExp> die = ref(new DotIdExp(e_ref.value.loc.value, alias_e.value, ident_ref.value));
                        IntRef errors = ref(gagError.value ? 0 : global.startGagging());
                        Ref<Expression> exp = ref(semanticY(die.value, sc_ref.value, (gagError.value ? 1 : 0)));
                        if (!gagError.value)
                        {
                            global.endGagging(errors.value);
                            if ((exp.value != null) && ((exp.value.op.value & 0xFF) == 127))
                            {
                                exp.value = null;
                            }
                        }
                        if ((exp.value != null) && gagError.value)
                        {
                            resolveAliasThis(sc_ref.value, e_ref.value, false);
                        }
                        return returnExp.invoke(exp.value);
                    }
                }
                return returnExp.invoke(visitType.invoke(mt_ref.value));
            }
        };
        Function1<TypeStruct,Expression> visitStruct = new Function1<TypeStruct,Expression>(){
            public Expression invoke(TypeStruct mt) {
                Ref<TypeStruct> mt_ref = ref(mt);
                Ref<Dsymbol> s = ref(null);
                assert(((e_ref.value.op.value & 0xFF) != 97));
                if ((pequals(ident_ref.value, Id._mangleof.value)))
                {
                    return getProperty(mt_ref.value, e_ref.value.loc.value, ident_ref.value, flag_ref.value & 1);
                }
                if ((pequals(ident_ref.value, Id._tupleof.value)))
                {
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                    if (!mt_ref.value.sym.value.determineFields())
                    {
                        error(e_ref.value.loc.value, new BytePtr("unable to determine fields of `%s` because of forward references"), mt_ref.value.toChars());
                    }
                    Ref<Expression> e0 = ref(null);
                    Ref<Expression> ev = ref(((e_ref.value.op.value & 0xFF) == 20) ? null : e_ref.value);
                    if (ev.value != null)
                    {
                        ev.value = extractSideEffect(sc_ref.value, new BytePtr("__tup"), e0, ev.value, false);
                    }
                    Ref<Ptr<DArray<Expression>>> exps = ref(refPtr(new DArray<Expression>()));
                    (exps.value.get()).reserve(mt_ref.value.sym.value.fields.length.value);
                    {
                        IntRef i = ref(0);
                        for (; (i.value < mt_ref.value.sym.value.fields.length.value);i.value++){
                            Ref<VarDeclaration> v = ref(mt_ref.value.sym.value.fields.get(i.value));
                            Ref<Expression> ex = ref(null);
                            if (ev.value != null)
                            {
                                ex.value = new DotVarExp(e_ref.value.loc.value, ev.value, v.value, true);
                            }
                            else
                            {
                                ex.value = new VarExp(e_ref.value.loc.value, v.value, true);
                                ex.value.type.value = ex.value.type.value.addMod(e_ref.value.type.value.mod.value);
                            }
                            (exps.value.get()).push(ex.value);
                        }
                    }
                    e_ref.value = new TupleExp(e_ref.value.loc.value, e0.value, exps.value);
                    Ref<Ptr<Scope>> sc2 = ref((sc_ref.value.get()).push());
                    (sc2.value.get()).flags.value |= global.params.vsafe.value ? 1024 : 2;
                    e_ref.value = expressionSemantic(e_ref.value, sc2.value);
                    (sc2.value.get()).pop();
                    return e_ref.value;
                }
                IntRef flags = ref(((sc_ref.value.get()).flags.value & 512) != 0 ? 128 : 0);
                s.value = mt_ref.value.sym.value.search(e_ref.value.loc.value, ident_ref.value, flags.value | 1);
                while(true) try {
                /*L1:*/
                    if (s.value == null)
                    {
                        return noMember.invoke(mt_ref.value, sc_ref.value, e_ref.value, ident_ref.value, flag_ref.value);
                    }
                    if ((((sc_ref.value.get()).flags.value & 512) == 0) && !symbolIsVisible(sc_ref.value, s.value))
                    {
                        return noMember.invoke(mt_ref.value, sc_ref.value, e_ref.value, ident_ref.value, flag_ref.value);
                    }
                    if (s.value.isFuncDeclaration() == null)
                    {
                        s.value.checkDeprecated(e_ref.value.loc.value, sc_ref.value);
                        {
                            Ref<Declaration> d = ref(s.value.isDeclaration());
                            if ((d.value) != null)
                            {
                                d.value.checkDisabled(e_ref.value.loc.value, sc_ref.value, false);
                            }
                        }
                    }
                    s.value = s.value.toAlias();
                    {
                        Ref<EnumMember> em = ref(s.value.isEnumMember());
                        if ((em.value) != null)
                        {
                            return em.value.getVarExp(e_ref.value.loc.value, sc_ref.value);
                        }
                    }
                    {
                        Ref<VarDeclaration> v = ref(s.value.isVarDeclaration());
                        if ((v.value) != null)
                        {
                            if ((v.value.type.value == null) || (v.value.type.value.deco.value == null) && (v.value.inuse.value != 0))
                            {
                                if (v.value.inuse.value != 0)
                                {
                                    e_ref.value.error(new BytePtr("circular reference to %s `%s`"), v.value.kind(), v.value.toPrettyChars(false));
                                }
                                else
                                {
                                    e_ref.value.error(new BytePtr("forward reference to %s `%s`"), v.value.kind(), v.value.toPrettyChars(false));
                                }
                                return new ErrorExp();
                            }
                            if (((v.value.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                            {
                                return new ErrorExp();
                            }
                            if (((v.value.storage_class.value & 8388608L) != 0) && (v.value._init.value != null))
                            {
                                if (v.value.inuse.value != 0)
                                {
                                    e_ref.value.error(new BytePtr("circular initialization of %s `%s`"), v.value.kind(), v.value.toPrettyChars(false));
                                    return new ErrorExp();
                                }
                                checkAccess(e_ref.value.loc.value, sc_ref.value, null, (Declaration)v);
                                Ref<Expression> ve = ref(new VarExp(e_ref.value.loc.value, v.value, true));
                                if (!isTrivialExp(e_ref.value))
                                {
                                    ve.value = new CommaExp(e_ref.value.loc.value, e_ref.value, ve.value, true);
                                }
                                return expressionSemantic(ve.value, sc_ref.value);
                            }
                        }
                    }
                    {
                        Ref<Type> t = ref(s.value.getType());
                        if ((t.value) != null)
                        {
                            return expressionSemantic(new TypeExp(e_ref.value.loc.value, t.value), sc_ref.value);
                        }
                    }
                    Ref<TemplateMixin> tm = ref(s.value.isTemplateMixin());
                    if (tm.value != null)
                    {
                        Ref<Expression> de = ref(new DotExp(e_ref.value.loc.value, e_ref.value, new ScopeExp(e_ref.value.loc.value, tm.value)));
                        de.value.type.value = e_ref.value.type.value;
                        return de.value;
                    }
                    Ref<TemplateDeclaration> td = ref(s.value.isTemplateDeclaration());
                    if (td.value != null)
                    {
                        if (((e_ref.value.op.value & 0xFF) == 20))
                        {
                            e_ref.value = new TemplateExp(e_ref.value.loc.value, td.value, null);
                        }
                        else
                        {
                            e_ref.value = new DotTemplateExp(e_ref.value.loc.value, e_ref.value, td.value);
                        }
                        return expressionSemantic(e_ref.value, sc_ref.value);
                    }
                    Ref<TemplateInstance> ti = ref(s.value.isTemplateInstance());
                    if (ti.value != null)
                    {
                        if (ti.value.semanticRun.value == 0)
                        {
                            dsymbolSemantic(ti.value, sc_ref.value);
                            if ((ti.value.inst.value == null) || ti.value.errors.value)
                            {
                                return new ErrorExp();
                            }
                        }
                        s.value = ti.value.inst.value.toAlias();
                        if (s.value.isTemplateInstance() == null)
                        {
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        }
                        if (((e_ref.value.op.value & 0xFF) == 20))
                        {
                            e_ref.value = new ScopeExp(e_ref.value.loc.value, ti.value);
                        }
                        else
                        {
                            e_ref.value = new DotExp(e_ref.value.loc.value, e_ref.value, new ScopeExp(e_ref.value.loc.value, ti.value));
                        }
                        return expressionSemantic(e_ref.value, sc_ref.value);
                    }
                    if ((s.value.isImport() != null) || (s.value.isModule() != null) || (s.value.isPackage() != null))
                    {
                        return symbolToExp(s.value, e_ref.value.loc.value, sc_ref.value, false);
                    }
                    Ref<OverloadSet> o = ref(s.value.isOverloadSet());
                    if (o.value != null)
                    {
                        Ref<OverExp> oe = ref(new OverExp(e_ref.value.loc.value, o.value));
                        if (((e_ref.value.op.value & 0xFF) == 20))
                        {
                            return oe.value;
                        }
                        return new DotExp(e_ref.value.loc.value, e_ref.value, oe.value);
                    }
                    Ref<Declaration> d = ref(s.value.isDeclaration());
                    if (d.value == null)
                    {
                        e_ref.value.error(new BytePtr("`%s.%s` is not a declaration"), e_ref.value.toChars(), ident_ref.value.toChars());
                        return new ErrorExp();
                    }
                    if (((e_ref.value.op.value & 0xFF) == 20))
                    {
                        {
                            Ref<TupleDeclaration> tup = ref(d.value.isTupleDeclaration());
                            if ((tup.value) != null)
                            {
                                e_ref.value = new TupleExp(e_ref.value.loc.value, tup.value);
                                return expressionSemantic(e_ref.value, sc_ref.value);
                            }
                        }
                        if (d.value.needThis() && ((sc_ref.value.get()).intypeof.value != 1))
                        {
                            if (hasThis(sc_ref.value) != null)
                            {
                                e_ref.value = new DotVarExp(e_ref.value.loc.value, new ThisExp(e_ref.value.loc.value), d.value, true);
                                return expressionSemantic(e_ref.value, sc_ref.value);
                            }
                        }
                        if ((d.value.semanticRun.value == PASS.init))
                        {
                            dsymbolSemantic(d.value, null);
                        }
                        checkAccess(e_ref.value.loc.value, sc_ref.value, e_ref.value, d.value);
                        Ref<VarExp> ve = ref(new VarExp(e_ref.value.loc.value, d.value, true));
                        if ((d.value.isVarDeclaration() != null) && d.value.needThis())
                        {
                            ve.value.type.value = d.value.type.value.addMod(e_ref.value.type.value.mod.value);
                        }
                        return ve.value;
                    }
                    Ref<Boolean> unreal = ref(((e_ref.value.op.value & 0xFF) == 26) && ((VarExp)e_ref.value).var.value.isField());
                    if (d.value.isDataseg() || unreal.value && d.value.isField())
                    {
                        checkAccess(e_ref.value.loc.value, sc_ref.value, e_ref.value, d.value);
                        Ref<Expression> ve = ref(new VarExp(e_ref.value.loc.value, d.value, true));
                        e_ref.value = unreal.value ? ve.value : new CommaExp(e_ref.value.loc.value, e_ref.value, ve.value, true);
                        return expressionSemantic(e_ref.value, sc_ref.value);
                    }
                    e_ref.value = new DotVarExp(e_ref.value.loc.value, e_ref.value, d.value, true);
                    return expressionSemantic(e_ref.value, sc_ref.value);
                    break;
                } catch(Dispatch0 __d){}
            }
        };
        Function1<TypeEnum,Expression> visitEnum = new Function1<TypeEnum,Expression>(){
            public Expression invoke(TypeEnum mt) {
                Ref<TypeEnum> mt_ref = ref(mt);
                if ((pequals(ident_ref.value, Id._mangleof.value)))
                {
                    return getProperty(mt_ref.value, e_ref.value.loc.value, ident_ref.value, flag_ref.value & 1);
                }
                if ((mt_ref.value.sym.value.semanticRun.value < PASS.semanticdone))
                {
                    dsymbolSemantic(mt_ref.value.sym.value, null);
                }
                if (mt_ref.value.sym.value.members.value == null)
                {
                    if (mt_ref.value.sym.value.isSpecial())
                    {
                        e_ref.value = dotExp(mt_ref.value.sym.value.memtype.value, sc_ref.value, e_ref.value, ident_ref.value, flag_ref.value);
                    }
                    else if ((flag_ref.value & 1) == 0)
                    {
                        mt_ref.value.sym.value.error(new BytePtr("is forward referenced when looking for `%s`"), ident_ref.value.toChars());
                        e_ref.value = new ErrorExp();
                    }
                    else
                    {
                        e_ref.value = null;
                    }
                    return e_ref.value;
                }
                Ref<Dsymbol> s = ref(mt_ref.value.sym.value.search(e_ref.value.loc.value, ident_ref.value, 8));
                if (s.value == null)
                {
                    if ((pequals(ident_ref.value, Id.max.value)) || (pequals(ident_ref.value, Id.min.value)) || (pequals(ident_ref.value, Id._init.value)))
                    {
                        return getProperty(mt_ref.value, e_ref.value.loc.value, ident_ref.value, flag_ref.value & 1);
                    }
                    Ref<Expression> res = ref(dotExp(mt_ref.value.sym.value.getMemtype(Loc.initial.value), sc_ref.value, e_ref.value, ident_ref.value, 1));
                    if (((flag_ref.value & 1) == 0) && (res.value == null))
                    {
                        {
                            Ref<Dsymbol> ns = ref(mt_ref.value.sym.value.search_correct(ident_ref.value));
                            if ((ns.value) != null)
                            {
                                e_ref.value.error(new BytePtr("no property `%s` for type `%s`. Did you mean `%s.%s` ?"), ident_ref.value.toChars(), mt_ref.value.toChars(), mt_ref.value.toChars(), ns.value.toChars());
                            }
                            else
                            {
                                e_ref.value.error(new BytePtr("no property `%s` for type `%s`"), ident_ref.value.toChars(), mt_ref.value.toChars());
                            }
                        }
                        return new ErrorExp();
                    }
                    return res.value;
                }
                EnumMember m = s.value.isEnumMember();
                return m.getVarExp(e_ref.value.loc.value, sc_ref.value);
            }
        };
        Function1<TypeClass,Expression> visitClass = new Function1<TypeClass,Expression>(){
            public Expression invoke(TypeClass mt) {
                Ref<TypeClass> mt_ref = ref(mt);
                Ref<Dsymbol> s = ref(null);
                assert(((e_ref.value.op.value & 0xFF) != 97));
                if ((pequals(ident_ref.value, Id.__sizeof.value)) || (pequals(ident_ref.value, Id.__xalignof.value)) || (pequals(ident_ref.value, Id._mangleof.value)))
                {
                    return getProperty(mt_ref.value.Type, e_ref.value.loc.value, ident_ref.value, 0);
                }
                if ((pequals(ident_ref.value, Id._tupleof.value)))
                {
                    objc().checkTupleof(e_ref.value, mt_ref.value);
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                    mt_ref.value.sym.value.size(e_ref.value.loc.value);
                    Ref<Expression> e0 = ref(null);
                    Ref<Expression> ev = ref(((e_ref.value.op.value & 0xFF) == 20) ? null : e_ref.value);
                    if (ev.value != null)
                    {
                        ev.value = extractSideEffect(sc_ref.value, new BytePtr("__tup"), e0, ev.value, false);
                    }
                    Ref<Ptr<DArray<Expression>>> exps = ref(refPtr(new DArray<Expression>()));
                    (exps.value.get()).reserve(mt_ref.value.sym.value.fields.length.value);
                    {
                        IntRef i = ref(0);
                        for (; (i.value < mt_ref.value.sym.value.fields.length.value);i.value++){
                            Ref<VarDeclaration> v = ref(mt_ref.value.sym.value.fields.get(i.value));
                            if (v.value.isThisDeclaration() != null)
                            {
                                continue;
                            }
                            Ref<Expression> ex = ref(null);
                            if (ev.value != null)
                            {
                                ex.value = new DotVarExp(e_ref.value.loc.value, ev.value, v.value, true);
                            }
                            else
                            {
                                ex.value = new VarExp(e_ref.value.loc.value, v.value, true);
                                ex.value.type.value = ex.value.type.value.addMod(e_ref.value.type.value.mod.value);
                            }
                            (exps.value.get()).push(ex.value);
                        }
                    }
                    e_ref.value = new TupleExp(e_ref.value.loc.value, e0.value, exps.value);
                    Ref<Ptr<Scope>> sc2 = ref((sc_ref.value.get()).push());
                    (sc2.value.get()).flags.value |= global.params.vsafe.value ? 1024 : 2;
                    e_ref.value = expressionSemantic(e_ref.value, sc2.value);
                    (sc2.value.get()).pop();
                    return e_ref.value;
                }
                IntRef flags = ref(((sc_ref.value.get()).flags.value & 512) != 0 ? 128 : 0);
                s.value = mt_ref.value.sym.value.search(e_ref.value.loc.value, ident_ref.value, flags.value | 1);
                while(true) try {
                /*L1:*/
                    if (s.value == null)
                    {
                        if ((pequals(mt_ref.value.sym.value.ident.value, ident_ref.value)))
                        {
                            if (((e_ref.value.op.value & 0xFF) == 20))
                            {
                                return getProperty(mt_ref.value.Type, e_ref.value.loc.value, ident_ref.value, 0);
                            }
                            e_ref.value = new DotTypeExp(e_ref.value.loc.value, e_ref.value, mt_ref.value.sym.value);
                            e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                            return e_ref.value;
                        }
                        {
                            Ref<ClassDeclaration> cbase = ref(mt_ref.value.sym.value.searchBase(ident_ref.value));
                            if ((cbase.value) != null)
                            {
                                if (((e_ref.value.op.value & 0xFF) == 20))
                                {
                                    return getProperty(mt_ref.value.Type, e_ref.value.loc.value, ident_ref.value, 0);
                                }
                                {
                                    Ref<InterfaceDeclaration> ifbase = ref(cbase.value.isInterfaceDeclaration());
                                    if ((ifbase.value) != null)
                                    {
                                        e_ref.value = new CastExp(e_ref.value.loc.value, e_ref.value, ifbase.value.type.value);
                                    }
                                    else
                                    {
                                        e_ref.value = new DotTypeExp(e_ref.value.loc.value, e_ref.value, cbase.value);
                                    }
                                }
                                e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                                return e_ref.value;
                            }
                        }
                        if ((pequals(ident_ref.value, Id.classinfo.value)))
                        {
                            if (Type.typeinfoclass.value == null)
                            {
                                error(e_ref.value.loc.value, new BytePtr("`object.TypeInfo_Class` could not be found, but is implicitly used"));
                                return new ErrorExp();
                            }
                            Ref<Type> t = ref(Type.typeinfoclass.value.type.value);
                            if (((e_ref.value.op.value & 0xFF) == 20) || ((e_ref.value.op.value & 0xFF) == 30))
                            {
                                if (mt_ref.value.sym.value.vclassinfo.value == null)
                                {
                                    mt_ref.value.sym.value.vclassinfo.value = new TypeInfoClassDeclaration(mt_ref.value.sym.value.type.value);
                                }
                                e_ref.value = new VarExp(e_ref.value.loc.value, mt_ref.value.sym.value.vclassinfo.value, true);
                                e_ref.value = e_ref.value.addressOf();
                                e_ref.value.type.value = t.value;
                            }
                            else
                            {
                                e_ref.value = new PtrExp(e_ref.value.loc.value, e_ref.value);
                                e_ref.value.type.value = t.value.pointerTo();
                                if (mt_ref.value.sym.value.isInterfaceDeclaration() != null)
                                {
                                    if (mt_ref.value.sym.value.isCPPinterface())
                                    {
                                        error(e_ref.value.loc.value, new BytePtr("no `.classinfo` for C++ interface objects"));
                                    }
                                    e_ref.value.type.value = e_ref.value.type.value.pointerTo();
                                    e_ref.value = new PtrExp(e_ref.value.loc.value, e_ref.value);
                                    e_ref.value.type.value = t.value.pointerTo();
                                }
                                e_ref.value = new PtrExp(e_ref.value.loc.value, e_ref.value, t.value);
                            }
                            return e_ref.value;
                        }
                        if ((pequals(ident_ref.value, Id.__vptr.value)))
                        {
                            e_ref.value = e_ref.value.castTo(sc_ref.value, Type.tvoidptr.value.immutableOf().pointerTo().pointerTo());
                            e_ref.value = new PtrExp(e_ref.value.loc.value, e_ref.value);
                            e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                            return e_ref.value;
                        }
                        if ((pequals(ident_ref.value, Id.__monitor.value)) && mt_ref.value.sym.value.hasMonitor())
                        {
                            e_ref.value = e_ref.value.castTo(sc_ref.value, Type.tvoidptr.value.pointerTo());
                            e_ref.value = new AddExp(e_ref.value.loc.value, e_ref.value, literal_356A192B7913B04C());
                            e_ref.value = new PtrExp(e_ref.value.loc.value, e_ref.value);
                            e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                            return e_ref.value;
                        }
                        if ((pequals(ident_ref.value, Id.outer.value)) && (mt_ref.value.sym.value.vthis.value != null))
                        {
                            if ((mt_ref.value.sym.value.vthis.value.semanticRun.value == PASS.init))
                            {
                                dsymbolSemantic(mt_ref.value.sym.value.vthis.value, null);
                            }
                            {
                                Ref<ClassDeclaration> cdp = ref(mt_ref.value.sym.value.toParentLocal().isClassDeclaration());
                                if ((cdp.value) != null)
                                {
                                    Ref<DotVarExp> dve = ref(new DotVarExp(e_ref.value.loc.value, e_ref.value, mt_ref.value.sym.value.vthis.value, true));
                                    dve.value.type.value = cdp.value.type.value.addMod(e_ref.value.type.value.mod.value);
                                    return dve.value;
                                }
                            }
                            {
                                Ref<Dsymbol> p = ref(mt_ref.value.sym.value.toParentLocal());
                                for (; p.value != null;p.value = p.value.toParentLocal()){
                                    Ref<FuncDeclaration> fd = ref(p.value.isFuncDeclaration());
                                    if (fd.value == null)
                                    {
                                        break;
                                    }
                                    Ref<AggregateDeclaration> ad = ref(fd.value.isThis());
                                    if ((ad.value == null) && fd.value.isNested())
                                    {
                                        continue;
                                    }
                                    if (ad.value == null)
                                    {
                                        break;
                                    }
                                    {
                                        Ref<ClassDeclaration> cdp = ref(ad.value.isClassDeclaration());
                                        if ((cdp.value) != null)
                                        {
                                            Ref<ThisExp> ve = ref(new ThisExp(e_ref.value.loc.value));
                                            ve.value.var.value = fd.value.vthis.value;
                                            boolean nestedError = fd.value.vthis.value.checkNestedReference(sc_ref.value, e_ref.value.loc.value);
                                            assert(!nestedError);
                                            ve.value.type.value = cdp.value.type.value.addMod(fd.value.vthis.value.type.value.mod.value).addMod(e_ref.value.type.value.mod.value);
                                            return ve.value;
                                        }
                                    }
                                    break;
                                }
                            }
                            Ref<DotVarExp> dve = ref(new DotVarExp(e_ref.value.loc.value, e_ref.value, mt_ref.value.sym.value.vthis.value, true));
                            dve.value.type.value = mt_ref.value.sym.value.vthis.value.type.value.addMod(e_ref.value.type.value.mod.value);
                            return dve.value;
                        }
                        return noMember.invoke(mt_ref.value, sc_ref.value, e_ref.value, ident_ref.value, flag_ref.value & 1);
                    }
                    if ((((sc_ref.value.get()).flags.value & 512) == 0) && !symbolIsVisible(sc_ref.value, s.value))
                    {
                        return noMember.invoke(mt_ref.value, sc_ref.value, e_ref.value, ident_ref.value, flag_ref.value);
                    }
                    if (s.value.isFuncDeclaration() == null)
                    {
                        s.value.checkDeprecated(e_ref.value.loc.value, sc_ref.value);
                        {
                            Ref<Declaration> d = ref(s.value.isDeclaration());
                            if ((d.value) != null)
                            {
                                d.value.checkDisabled(e_ref.value.loc.value, sc_ref.value, false);
                            }
                        }
                    }
                    s.value = s.value.toAlias();
                    {
                        Ref<EnumMember> em = ref(s.value.isEnumMember());
                        if ((em.value) != null)
                        {
                            return em.value.getVarExp(e_ref.value.loc.value, sc_ref.value);
                        }
                    }
                    {
                        Ref<VarDeclaration> v = ref(s.value.isVarDeclaration());
                        if ((v.value) != null)
                        {
                            if ((v.value.type.value == null) || (v.value.type.value.deco.value == null) && (v.value.inuse.value != 0))
                            {
                                if (v.value.inuse.value != 0)
                                {
                                    e_ref.value.error(new BytePtr("circular reference to %s `%s`"), v.value.kind(), v.value.toPrettyChars(false));
                                }
                                else
                                {
                                    e_ref.value.error(new BytePtr("forward reference to %s `%s`"), v.value.kind(), v.value.toPrettyChars(false));
                                }
                                return new ErrorExp();
                            }
                            if (((v.value.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                            {
                                return new ErrorExp();
                            }
                            if (((v.value.storage_class.value & 8388608L) != 0) && (v.value._init.value != null))
                            {
                                if (v.value.inuse.value != 0)
                                {
                                    e_ref.value.error(new BytePtr("circular initialization of %s `%s`"), v.value.kind(), v.value.toPrettyChars(false));
                                    return new ErrorExp();
                                }
                                checkAccess(e_ref.value.loc.value, sc_ref.value, null, (Declaration)v);
                                Ref<Expression> ve = ref(new VarExp(e_ref.value.loc.value, v.value, true));
                                ve.value = expressionSemantic(ve.value, sc_ref.value);
                                return ve.value;
                            }
                        }
                    }
                    {
                        Ref<Type> t = ref(s.value.getType());
                        if ((t.value) != null)
                        {
                            return expressionSemantic(new TypeExp(e_ref.value.loc.value, t.value), sc_ref.value);
                        }
                    }
                    Ref<TemplateMixin> tm = ref(s.value.isTemplateMixin());
                    if (tm.value != null)
                    {
                        Ref<Expression> de = ref(new DotExp(e_ref.value.loc.value, e_ref.value, new ScopeExp(e_ref.value.loc.value, tm.value)));
                        de.value.type.value = e_ref.value.type.value;
                        return de.value;
                    }
                    Ref<TemplateDeclaration> td = ref(s.value.isTemplateDeclaration());
                    if (td.value != null)
                    {
                        if (((e_ref.value.op.value & 0xFF) == 20))
                        {
                            e_ref.value = new TemplateExp(e_ref.value.loc.value, td.value, null);
                        }
                        else
                        {
                            e_ref.value = new DotTemplateExp(e_ref.value.loc.value, e_ref.value, td.value);
                        }
                        e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                        return e_ref.value;
                    }
                    Ref<TemplateInstance> ti = ref(s.value.isTemplateInstance());
                    if (ti.value != null)
                    {
                        if (ti.value.semanticRun.value == 0)
                        {
                            dsymbolSemantic(ti.value, sc_ref.value);
                            if ((ti.value.inst.value == null) || ti.value.errors.value)
                            {
                                return new ErrorExp();
                            }
                        }
                        s.value = ti.value.inst.value.toAlias();
                        if (s.value.isTemplateInstance() == null)
                        {
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        }
                        if (((e_ref.value.op.value & 0xFF) == 20))
                        {
                            e_ref.value = new ScopeExp(e_ref.value.loc.value, ti.value);
                        }
                        else
                        {
                            e_ref.value = new DotExp(e_ref.value.loc.value, e_ref.value, new ScopeExp(e_ref.value.loc.value, ti.value));
                        }
                        return expressionSemantic(e_ref.value, sc_ref.value);
                    }
                    if ((s.value.isImport() != null) || (s.value.isModule() != null) || (s.value.isPackage() != null))
                    {
                        e_ref.value = symbolToExp(s.value, e_ref.value.loc.value, sc_ref.value, false);
                        return e_ref.value;
                    }
                    Ref<OverloadSet> o = ref(s.value.isOverloadSet());
                    if (o.value != null)
                    {
                        Ref<OverExp> oe = ref(new OverExp(e_ref.value.loc.value, o.value));
                        if (((e_ref.value.op.value & 0xFF) == 20))
                        {
                            return oe.value;
                        }
                        return new DotExp(e_ref.value.loc.value, e_ref.value, oe.value);
                    }
                    Ref<Declaration> d = ref(s.value.isDeclaration());
                    if (d.value == null)
                    {
                        e_ref.value.error(new BytePtr("`%s.%s` is not a declaration"), e_ref.value.toChars(), ident_ref.value.toChars());
                        return new ErrorExp();
                    }
                    if (((e_ref.value.op.value & 0xFF) == 20))
                    {
                        {
                            Ref<TupleDeclaration> tup = ref(d.value.isTupleDeclaration());
                            if ((tup.value) != null)
                            {
                                e_ref.value = new TupleExp(e_ref.value.loc.value, tup.value);
                                e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                                return e_ref.value;
                            }
                        }
                        if ((mt_ref.value.sym.value.classKind.value == ClassKind.objc) && (d.value.isFuncDeclaration() != null) && d.value.isFuncDeclaration().isStatic() && (d.value.isFuncDeclaration().selector.value != null))
                        {
                            Ref<ObjcClassReferenceExp> classRef = ref(new ObjcClassReferenceExp(e_ref.value.loc.value, mt_ref.value.sym.value));
                            return expressionSemantic(new DotVarExp(e_ref.value.loc.value, classRef.value, d.value, true), sc_ref.value);
                        }
                        else if (d.value.needThis() && ((sc_ref.value.get()).intypeof.value != 1))
                        {
                            Ref<AggregateDeclaration> ad = ref(d.value.isMemberLocal());
                            {
                                Ref<FuncDeclaration> f = ref(hasThis(sc_ref.value));
                                if ((f.value) != null)
                                {
                                    Ref<Expression> e1 = ref(null);
                                    Ref<Type> t = ref(null);
                                    try {
                                        if (f.value.isThis2.value)
                                        {
                                            if (followInstantiationContextAggregateDeclaration(f.value, ad.value))
                                            {
                                                e1.value = new VarExp(e_ref.value.loc.value, f.value.vthis.value, true);
                                                e1.value = new PtrExp(e1.value.loc.value, e1.value);
                                                e1.value = new IndexExp(e1.value.loc.value, e1.value, literal_356A192B7913B04C());
                                                Ref<Declaration> pd = ref(f.value.toParent2().isDeclaration());
                                                assert(pd.value != null);
                                                t.value = pd.value.type.value.toBasetype();
                                                e1.value = getThisSkipNestedFuncs(e1.value.loc.value, sc_ref.value, f.value.toParent2(), ad.value, e1.value, t.value, d.value, true);
                                                if (e1.value == null)
                                                {
                                                    e_ref.value = new VarExp(e_ref.value.loc.value, d.value, true);
                                                    return e_ref.value;
                                                }
                                                /*goto L2*/throw Dispatch0.INSTANCE;
                                            }
                                        }
                                        e1.value = new ThisExp(e_ref.value.loc.value);
                                        e1.value = expressionSemantic(e1.value, sc_ref.value);
                                    }
                                    catch(Dispatch0 __d){}
                                /*L2:*/
                                    t.value = e1.value.type.value.toBasetype();
                                    Ref<ClassDeclaration> cd = ref(e_ref.value.type.value.isClassHandle());
                                    Ref<ClassDeclaration> tcd = ref(t.value.isClassHandle());
                                    if ((cd.value != null) && (tcd.value != null) && (pequals(tcd.value, cd.value)) || cd.value.isBaseOf(tcd.value, null))
                                    {
                                        e_ref.value = new DotTypeExp(e1.value.loc.value, e1.value, cd.value);
                                        e_ref.value = new DotVarExp(e_ref.value.loc.value, e_ref.value, d.value, true);
                                        e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                                        return e_ref.value;
                                    }
                                    if ((tcd.value != null) && tcd.value.isNested())
                                    {
                                        Ref<VarDeclaration> vthis = ref(followInstantiationContextAggregateDeclaration(tcd.value, ad.value) ? tcd.value.vthis2.value : tcd.value.vthis.value);
                                        e1.value = new DotVarExp(e_ref.value.loc.value, e1.value, vthis.value, true);
                                        e1.value.type.value = vthis.value.type.value;
                                        e1.value.type.value = e1.value.type.value.addMod(t.value.mod.value);
                                        e1.value = getThisSkipNestedFuncs(e1.value.loc.value, sc_ref.value, toParentPAggregateDeclaration(tcd.value, ad.value), ad.value, e1.value, t.value, d.value, true);
                                        if (e1.value == null)
                                        {
                                            e_ref.value = new VarExp(e_ref.value.loc.value, d.value, true);
                                            return e_ref.value;
                                        }
                                        /*goto L2*/throw Dispatch0.INSTANCE;
                                    }
                                }
                            }
                        }
                        if ((d.value.semanticRun.value == PASS.init))
                        {
                            dsymbolSemantic(d.value, null);
                        }
                        {
                            Ref<FuncDeclaration> fd = ref(d.value.isFuncDeclaration());
                            if ((fd.value) != null)
                            {
                                d.value = (Declaration)mostVisibleOverload(fd.value, (sc_ref.value.get())._module.value);
                            }
                        }
                        checkAccess(e_ref.value.loc.value, sc_ref.value, e_ref.value, d.value);
                        Ref<VarExp> ve = ref(new VarExp(e_ref.value.loc.value, d.value, true));
                        if ((d.value.isVarDeclaration() != null) && d.value.needThis())
                        {
                            ve.value.type.value = d.value.type.value.addMod(e_ref.value.type.value.mod.value);
                        }
                        return ve.value;
                    }
                    Ref<Boolean> unreal = ref(((e_ref.value.op.value & 0xFF) == 26) && ((VarExp)e_ref.value).var.value.isField());
                    if (d.value.isDataseg() || unreal.value && d.value.isField())
                    {
                        checkAccess(e_ref.value.loc.value, sc_ref.value, e_ref.value, d.value);
                        Ref<Expression> ve = ref(new VarExp(e_ref.value.loc.value, d.value, true));
                        e_ref.value = unreal.value ? ve.value : new CommaExp(e_ref.value.loc.value, e_ref.value, ve.value, true);
                        e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                        return e_ref.value;
                    }
                    e_ref.value = new DotVarExp(e_ref.value.loc.value, e_ref.value, d.value, true);
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                    return e_ref.value;
                    break;
                } catch(Dispatch0 __d){}
            }
        };
        switch ((mt.ty.value & 0xFF))
        {
            case 41:
                return visitVector.invoke((TypeVector)mt);
            case 1:
                return visitSArray.invoke((TypeSArray)mt);
            case 8:
                return visitStruct.invoke((TypeStruct)mt);
            case 9:
                return visitEnum.invoke((TypeEnum)mt);
            case 34:
                return visitError.invoke((TypeError)mt);
            case 0:
                return visitDArray.invoke((TypeDArray)mt);
            case 2:
                return visitAArray.invoke((TypeAArray)mt);
            case 4:
                return visitReference.invoke((TypeReference)mt);
            case 10:
                return visitDelegate.invoke((TypeDelegate)mt);
            case 7:
                return visitClass.invoke((TypeClass)mt);
            default:
            return mt.isTypeBasic() != null ? visitBasic.invoke((TypeBasic)mt) : visitType.invoke(mt);
        }
    }

    public static Expression defaultInit(Type mt, Loc loc) {
        Ref<Loc> loc_ref = ref(loc);
        Function1<TypeBasic,Expression> visitBasic = new Function1<TypeBasic,Expression>(){
            public Expression invoke(TypeBasic mt) {
                Ref<TypeBasic> mt_ref = ref(mt);
                Ref<Long> value = ref(0L);
                switch ((mt_ref.value.ty.value & 0xFF))
                {
                    case 31:
                        value.value = 255L;
                        break;
                    case 32:
                    case 33:
                        value.value = 65535L;
                        break;
                    case 24:
                    case 25:
                    case 26:
                    case 21:
                    case 22:
                    case 23:
                        return new RealExp(loc_ref.value, target.RealProperties.nan.value, mt_ref.value);
                    case 27:
                    case 28:
                    case 29:
                        Ref<complex_t> cvalue = ref(cvalue.value = new complex_t(target.RealProperties.nan.value, target.RealProperties.nan.value));
                        return new ComplexExp(loc_ref.value, cvalue.value, mt_ref.value);
                    case 12:
                        error(loc_ref.value, new BytePtr("`void` does not have a default initializer"));
                        return new ErrorExp();
                    default:
                    break;
                }
                return new IntegerExp(loc_ref.value, value.value, mt_ref.value);
            }
        };
        Function1<TypeVector,Expression> visitVector = new Function1<TypeVector,Expression>(){
            public Expression invoke(TypeVector mt) {
                Ref<TypeVector> mt_ref = ref(mt);
                assert(((mt_ref.value.basetype.value.ty.value & 0xFF) == ENUMTY.Tsarray));
                Ref<Expression> e = ref(defaultInit(mt_ref.value.basetype.value, loc_ref.value));
                Ref<VectorExp> ve = ref(new VectorExp(loc_ref.value, e.value, mt_ref.value));
                ve.value.type.value = mt_ref.value;
                ve.value.dim.value = (int)(mt_ref.value.basetype.value.size(loc_ref.value) / mt_ref.value.elementType().size(loc_ref.value));
                return ve.value;
            }
        };
        Function1<TypeSArray,Expression> visitSArray = new Function1<TypeSArray,Expression>(){
            public Expression invoke(TypeSArray mt) {
                if (((mt.next.value.ty.value & 0xFF) == ENUMTY.Tvoid))
                {
                    return defaultInit(Type.tuns8.value, loc_ref.value);
                }
                else
                {
                    return defaultInit(mt.next.value, loc_ref.value);
                }
            }
        };
        Function1<TypeFunction,Expression> visitFunction = new Function1<TypeFunction,Expression>(){
            public Expression invoke(TypeFunction mt) {
                error(loc_ref.value, new BytePtr("`function` does not have a default initializer"));
                return new ErrorExp();
            }
        };
        Function1<TypeStruct,Expression> visitStruct = new Function1<TypeStruct,Expression>(){
            public Expression invoke(TypeStruct mt) {
                Ref<TypeStruct> mt_ref = ref(mt);
                Ref<Declaration> d = ref(new SymbolDeclaration(mt_ref.value.sym.value.loc.value, mt_ref.value.sym.value));
                assert(d.value != null);
                d.value.type.value = mt_ref.value;
                d.value.storage_class.value |= 2199023255552L;
                return new VarExp(mt_ref.value.sym.value.loc.value, d.value, true);
            }
        };
        Function1<TypeEnum,Expression> visitEnum = new Function1<TypeEnum,Expression>(){
            public Expression invoke(TypeEnum mt) {
                Ref<TypeEnum> mt_ref = ref(mt);
                Ref<Expression> e = ref(mt_ref.value.sym.value.getDefaultValue(loc_ref.value));
                e.value = e.value.copy();
                e.value.loc.value = loc_ref.value.copy();
                e.value.type.value = mt_ref.value;
                return e.value;
            }
        };
        Function1<TypeTuple,Expression> visitTuple = new Function1<TypeTuple,Expression>(){
            public Expression invoke(TypeTuple mt) {
                Ref<Ptr<DArray<Expression>>> exps = ref(refPtr(new DArray<Expression>((mt.arguments.value.get()).length.value)));
                {
                    IntRef i = ref(0);
                    for (; (i.value < (mt.arguments.value.get()).length.value);i.value++){
                        Parameter p = (mt.arguments.value.get()).get(i.value);
                        assert(p.type.value != null);
                        Ref<Expression> e = ref(p.type.value.defaultInitLiteral(loc_ref.value));
                        if (((e.value.op.value & 0xFF) == 127))
                        {
                            return e.value;
                        }
                        exps.value.get().set(i.value, e.value);
                    }
                }
                return new TupleExp(loc_ref.value, exps.value);
            }
        };
        switch ((mt.ty.value & 0xFF))
        {
            case 41:
                return visitVector.invoke((TypeVector)mt);
            case 1:
                return visitSArray.invoke((TypeSArray)mt);
            case 5:
                return visitFunction.invoke((TypeFunction)mt);
            case 8:
                return visitStruct.invoke((TypeStruct)mt);
            case 9:
                return visitEnum.invoke((TypeEnum)mt);
            case 37:
                return visitTuple.invoke((TypeTuple)mt);
            case 40:
                return new NullExp(Loc.initial.value, Type.tnull);
            case 34:
                return new ErrorExp();
            case 0:
            case 2:
            case 3:
            case 4:
            case 10:
            case 7:
                return new NullExp(loc_ref.value, mt);
            default:
            return mt.isTypeBasic() != null ? visitBasic.invoke((TypeBasic)mt) : null;
        }
    }

}
