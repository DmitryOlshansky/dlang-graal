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
                sym.parent.value = (sc.get()).scopesym;
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
        sym.parent.value = (sc.get()).scopesym;
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
                eindex.value = new TypeExp(loc, tindex.value);
            else if (sindex.value != null)
                eindex.value = symbolToExp(sindex.value, loc, sc, false);
            Expression e = new IndexExp(loc, symbolToExp(s, loc, sc, false), eindex.value);
            e = expressionSemantic(e, sc);
            resolveExp(e, pt, pe, ps);
            return ;
        }
        if (tindex.value != null)
            resolve(tindex.value, loc, sc, ptr(eindex), ptr(tindex), ptr(sindex), false);
        if (sindex.value != null)
            eindex.value = symbolToExp(sindex.value, loc, sc, false);
        if (eindex.value == null)
        {
            error(loc, new BytePtr("index `%s` is not an expression"), oindex.toChars());
            pt.set(0, Type.terror.value);
            return ;
        }
        eindex.value = semanticLength(sc, tup, eindex.value);
        eindex.value = eindex.value.ctfeInterpret();
        if (((eindex.value.op & 0xFF) == 127))
        {
            pt.set(0, Type.terror.value);
            return ;
        }
        long d = eindex.value.toUInteger();
        if ((d >= (long)(tup.objects.get()).length))
        {
            error(loc, new BytePtr("tuple index `%llu` exceeds length %u"), d, (tup.objects.get()).length);
            pt.set(0, Type.terror.value);
            return ;
        }
        RootObject o = (tup.objects.get()).get((int)d);
        pt.set(0, isType(o));
        ps.set(0, isDsymbol(o));
        pe.set(0, isExpression(o));
        if (pt.get() != null)
            pt.set(0, typeSemantic(pt.get(), loc, sc));
        if (pe.get() != null)
            resolveExp(pe.get(), pt, pe, ps);
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
            if ((d != null) && ((d.storage_class & 262144L) != 0))
                s_ref.value = s_ref.value.toAlias();
            else
            {
                s_ref.value.checkDeprecated(loc, sc_ref.value);
                if (d != null)
                    d.checkDisabled(loc, sc_ref.value, true);
            }
            s_ref.value = s_ref.value.toAlias();
            {
                IntRef i = ref(0);
                for (; (i.value < mt_ref.value.idents.length);i.value++){
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
                            ex.value = new TypeExp(loc, tx.value);
                        assert(ex.value != null);
                        ex.value = typeToExpressionHelper(mt_ref.value, ex.value, i.value + 1);
                        ex.value = expressionSemantic(ex.value, sc_ref.value);
                        resolveExp(ex.value, pt_ref.value, pe_ref.value, ps_ref.value);
                        return ;
                    }
                    Type t = s_ref.value.getType();
                    int errorsave = global.value.errors;
                    int flags = (t == null) ? 8 : 1;
                    Dsymbol sm = s_ref.value.searchX(loc, sc_ref.value, id, flags);
                    if ((sm != null) && (((sc_ref.value.get()).flags & 512) == 0) && !symbolIsVisible(sc_ref.value, sm))
                    {
                        error(loc, new BytePtr("`%s` is not visible from module `%s`"), sm.toPrettyChars(false), (sc_ref.value.get())._module.toChars());
                        sm = null;
                    }
                    if ((global.value.errors != errorsave))
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
                                e.value = symbolToExp(s_ref.value, loc, sc_ref.value, true);
                            else
                                e.value = new VarExp(loc, s_ref.value.isDeclaration(), true);
                            e.value = typeToExpressionHelper(mt_ref.value, e.value, i.value);
                            e.value = expressionSemantic(e.value, sc_ref.value);
                            resolveExp(e.value, pt_ref.value, pe_ref.value, ps_ref.value);
                        }
                    };
                    if (intypeid_ref.value && (t == null) && (sm != null) && sm.needThis())
                        helper3.invoke();
                        return ;
                    {
                        VarDeclaration v = s_ref.value.isVarDeclaration();
                        if ((v) != null)
                        {
                            if ((v.type == null))
                                dsymbolSemantic(v, sc_ref.value);
                            if (((v.storage_class & 9437188L) != 0) || v.type.isConst() || v.type.isImmutable())
                            {
                                if (v.isThisDeclaration() == null)
                                    helper3.invoke();
                                    return ;
                            }
                        }
                    }
                    if (sm == null)
                    {
                        if (t == null)
                        {
                            if (s_ref.value.isDeclaration() != null)
                            {
                                t = s_ref.value.isDeclaration().type;
                                if ((t == null) && (s_ref.value.isTupleDeclaration() != null))
                                    helper3.invoke();
                                    return ;
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
                                    helper3.invoke();
                                    return ;
                            }
                            else
                                helper3.invoke();
                                return ;
                        }
                        else
                        {
                            if ((id.dyncast() == DYNCAST.dsymbol))
                            {
                                assert(global.value.errors != 0);
                            }
                            else
                            {
                                assert((id.dyncast() == DYNCAST.identifier));
                                sm = s_ref.value.search_correct((Identifier)id);
                                if (sm != null)
                                    error(loc, new BytePtr("identifier `%s` of `%s` is not defined, did you mean %s `%s`?"), id.toChars(), mt_ref.value.toChars(), sm.kind(), sm.toChars());
                                else
                                    error(loc, new BytePtr("identifier `%s` of `%s` is not defined"), id.toChars(), mt_ref.value.toChars());
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
                    if ((v.type == null) || (v.type.deco == null) && (v.inuse != 0))
                    {
                        if (v.inuse != 0)
                            error(loc, new BytePtr("circular reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                        else
                            error(loc, new BytePtr("forward reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                        pt_ref.value.set(0, Type.terror.value);
                        return ;
                    }
                    if (((v.type.ty & 0xFF) == ENUMTY.Terror))
                        pt_ref.value.set(0, Type.terror.value);
                    else
                        pe_ref.value.set(0, (new VarExp(loc, v, true)));
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
                    break;
                {
                    Import si = s_ref.value.isImport();
                    if ((si) != null)
                    {
                        s_ref.value = si.search(loc, s_ref.value.ident, 8);
                        if ((s_ref.value != null) && (!pequals(s_ref.value, si)))
                            continue;
                        s_ref.value = si;
                    }
                }
                ps_ref.value.set(0, s_ref.value);
                return ;
            }
            {
                TypeInstance ti = t.isTypeInstance();
                if ((ti) != null)
                    if ((!pequals(ti, mt_ref.value)) && (ti.deco == null))
                    {
                        if (!ti.tempinst.errors)
                            error(loc, new BytePtr("forward reference to `%s`"), ti.toChars());
                        pt_ref.value.set(0, Type.terror.value);
                        return ;
                    }
            }
            if (((t.ty & 0xFF) == ENUMTY.Ttuple))
                pt_ref.value.set(0, t);
            else
                pt_ref.value.set(0, merge(t));
        }
        if (s_ref.value == null)
        {
            BytePtr p = pcopy(mt_ref.value.mutableOf().unSharedOf().toChars());
            Identifier id = Identifier.idPool(p, strlen(p));
            {
                ByteSlice n = importHint(id.asString()).copy();
                if ((n).getLength() != 0)
                    error(loc, new BytePtr("`%s` is not defined, perhaps `import %.*s;` ?"), p, n.getLength(), toBytePtr(n));
                else {
                    Dsymbol s2 = (sc_ref.value.get()).search_correct(id);
                    if ((s2) != null)
                        error(loc, new BytePtr("undefined identifier `%s`, did you mean %s `%s`?"), p, s2.kind(), s2.toChars());
                    else {
                        BytePtr q = pcopy(Scope.search_correct_C(id));
                        if ((q) != null)
                            error(loc, new BytePtr("undefined identifier `%s`, did you mean `%s`?"), p, q);
                        else
                            error(loc, new BytePtr("undefined identifier `%s`"), p);
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
                        Ref<Parameter> p_ref = ref(p);
                        Ref<Type> t = ref(stripDefaultArgs(p_ref.value.type));
                        return (!pequals(t.value, p_ref.value.type)) || (p_ref.value.defaultArg != null) || (p_ref.value.ident != null) || (p_ref.value.userAttribDecl != null) ? new Parameter(p_ref.value.storageClass, t.value, null, null, null) : null;
                    }
                };
                if (parameters_ref.value != null)
                {
                    {
                        Ref<Slice<Parameter>> __r1646 = ref((parameters_ref.value.get()).opSlice().copy());
                        IntRef __key1645 = ref(0);
                        for (; (__key1645.value < __r1646.value.getLength());__key1645.value += 1) {
                            Ref<Parameter> p = ref(__r1646.value.get(__key1645.value));
                            IntRef i = ref(__key1645.value);
                            Ref<Parameter> ps = ref(stripParameter.invoke(p.value));
                            if (ps.value != null)
                            {
                                Ref<Ptr<DArray<Parameter>>> nparams = ref(new DArray<Parameter>((parameters_ref.value.get()).length));
                                {
                                    Ref<Slice<Parameter>> __r1648 = ref((nparams.value.get()).opSlice().copy());
                                    IntRef __key1647 = ref(0);
                                    for (; (__key1647.value < __r1648.value.getLength());__key1647.value += 1) {
                                        Ref<Parameter> np = ref(__r1648.value.get(__key1647.value));
                                        IntRef j = ref(__key1647.value);
                                        Ref<Parameter> pj = ref((parameters_ref.value.get()).get(j.value));
                                        if ((j.value < i.value))
                                            np.value = pj.value;
                                        else if ((j.value == i.value))
                                            np.value = ps.value;
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
            return t;
        {
            TypeFunction tf = t.isTypeFunction();
            if ((tf) != null)
            {
                Type tret = stripDefaultArgs(tf.next);
                Ptr<DArray<Parameter>> params = stripParams.invoke(tf.parameterList.parameters);
                if ((pequals(tret, tf.next)) && (params == tf.parameterList.parameters))
                    return t;
                TypeFunction tr = (TypeFunction)tf.copy();
                tr.parameterList.parameters = params;
                tr.next = tret;
                return tr;
            }
            else {
                TypeTuple tt = t.isTypeTuple();
                if ((tt) != null)
                {
                    Ptr<DArray<Parameter>> args = stripParams.invoke(tt.arguments);
                    if ((args == tt.arguments))
                        return t;
                    TypeTuple tr = (TypeTuple)t.copy();
                    tr.arguments = args;
                    return tr;
                }
                else if (((t.ty & 0xFF) == ENUMTY.Tenum))
                {
                    return t;
                }
                else
                {
                    Type tn = t.nextOf();
                    Type n = stripDefaultArgs(tn);
                    if ((pequals(n, tn)))
                        return t;
                    TypeNext tr = (TypeNext)t.copy();
                    tr.next = n;
                    return tr;
                }
            }
        }
    }

    public static Expression typeToExpression(Type t) {
        Function1<TypeSArray,Expression> visitSArray = new Function1<TypeSArray,Expression>(){
            public Expression invoke(TypeSArray t) {
                Ref<TypeSArray> t_ref = ref(t);
                {
                    Ref<Expression> e = ref(typeToExpression(t_ref.value.next));
                    if ((e.value) != null)
                        return new ArrayExp(t_ref.value.dim.loc, e.value, t_ref.value.dim);
                }
                return null;
            }
        };
        Function1<TypeAArray,Expression> visitAArray = new Function1<TypeAArray,Expression>(){
            public Expression invoke(TypeAArray t) {
                Ref<TypeAArray> t_ref = ref(t);
                {
                    Ref<Expression> e = ref(typeToExpression(t_ref.value.next));
                    if ((e.value) != null)
                    {
                        {
                            Ref<Expression> ei = ref(typeToExpression(t_ref.value.index));
                            if ((ei.value) != null)
                                return new ArrayExp(t_ref.value.loc, e.value, ei.value);
                        }
                    }
                }
                return null;
            }
        };
        Function1<TypeIdentifier,Expression> visitIdentifier = new Function1<TypeIdentifier,Expression>(){
            public Expression invoke(TypeIdentifier t) {
                Ref<TypeIdentifier> t_ref = ref(t);
                return typeToExpressionHelper(t_ref.value, new IdentifierExp(t_ref.value.loc, t_ref.value.ident), 0);
            }
        };
        Function1<TypeInstance,Expression> visitInstance = new Function1<TypeInstance,Expression>(){
            public Expression invoke(TypeInstance t) {
                Ref<TypeInstance> t_ref = ref(t);
                return typeToExpressionHelper(t_ref.value, new ScopeExp(t_ref.value.loc, t_ref.value.tempinst), 0);
            }
        };
        switch ((t.ty & 0xFF))
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
            Slice<RootObject> __r1649 = t.idents.opSlice(i, t.idents.length).copy();
            int __key1650 = 0;
            for (; (__key1650 < __r1649.getLength());__key1650 += 1) {
                RootObject id = __r1649.get(__key1650);
                switch (id.dyncast())
                {
                    case DYNCAST.identifier:
                        e = new DotIdExp(e.loc, e, (Identifier)id);
                        break;
                    case DYNCAST.dsymbol:
                        TemplateInstance ti = ((Dsymbol)id).isTemplateInstance();
                        assert(ti != null);
                        e = new DotTemplateInstanceExp(e.loc, e, ti.name, ti.tiargs);
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
                if (((t_ref.value.ty & 0xFF) == ENUMTY.Tint128) || ((t_ref.value.ty & 0xFF) == ENUMTY.Tuns128))
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
                IntRef errors = ref(global.value.errors);
                mtype_ref.value.basetype = typeSemantic(mtype_ref.value.basetype, loc_ref.value, sc_ref.value);
                if ((errors.value != global.value.errors))
                    return error.invoke();
                mtype_ref.value.basetype = mtype_ref.value.basetype.toBasetype().mutableOf();
                if (((mtype_ref.value.basetype.ty & 0xFF) != ENUMTY.Tsarray))
                {
                    error(loc_ref.value, new BytePtr("T in __vector(T) must be a static array, not `%s`"), mtype_ref.value.basetype.toChars());
                    return error.invoke();
                }
                Ref<TypeSArray> t = ref((TypeSArray)mtype_ref.value.basetype);
                IntRef sz = ref((int)t.value.size(loc_ref.value));
                switch (target.value.isVectorTypeSupported(sz.value, t.value.nextOf()))
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
                Ref<TypeSArray> mtype_ref = ref(mtype);
                Ref<Type> t = ref(null);
                Ref<Expression> e = ref(null);
                Ref<Dsymbol> s = ref(null);
                resolve(mtype_ref.value.next, loc_ref.value, sc_ref.value, ptr(e), ptr(t), ptr(s), false);
                {
                    Ref<TupleDeclaration> tup = ref(s.value != null ? s.value.isTupleDeclaration() : null);
                    if ((tup.value) != null)
                    {
                        mtype_ref.value.dim = semanticLength(sc_ref.value, tup.value, mtype_ref.value.dim);
                        mtype_ref.value.dim = mtype_ref.value.dim.ctfeInterpret();
                        if (((mtype_ref.value.dim.op & 0xFF) == 127))
                            return error.invoke();
                        Ref<Long> d = ref(mtype_ref.value.dim.toUInteger());
                        if ((d.value >= (long)(tup.value.objects.get()).length))
                        {
                            error(loc_ref.value, new BytePtr("tuple index %llu exceeds %llu"), d.value, (long)(tup.value.objects.get()).length);
                            return error.invoke();
                        }
                        Ref<RootObject> o = ref((tup.value.objects.get()).get((int)d.value));
                        if ((o.value.dyncast() != DYNCAST.type))
                        {
                            error(loc_ref.value, new BytePtr("`%s` is not a type"), mtype_ref.value.toChars());
                            return error.invoke();
                        }
                        return ((Type)o.value).addMod(mtype_ref.value.mod);
                    }
                }
                Ref<Type> tn = ref(typeSemantic(mtype_ref.value.next, loc_ref.value, sc_ref.value));
                if (((tn.value.ty & 0xFF) == ENUMTY.Terror))
                    return error.invoke();
                Ref<Type> tbn = ref(tn.value.toBasetype());
                if (mtype_ref.value.dim != null)
                {
                    if (mtype_ref.value.dim.isDotVarExp() != null)
                    {
                        {
                            Ref<Declaration> vd = ref(mtype_ref.value.dim.isDotVarExp().var);
                            if ((vd.value) != null)
                            {
                                Ref<FuncDeclaration> fd = ref(vd.value.toAlias().isFuncDeclaration());
                                if (fd.value != null)
                                    mtype_ref.value.dim = new CallExp(loc_ref.value, fd.value, null);
                            }
                        }
                    }
                    IntRef errors = ref(global.value.errors);
                    mtype_ref.value.dim = semanticLength(sc_ref.value, tbn.value, mtype_ref.value.dim);
                    if ((errors.value != global.value.errors))
                        return error.invoke();
                    mtype_ref.value.dim = mtype_ref.value.dim.optimize(0, false);
                    mtype_ref.value.dim = mtype_ref.value.dim.ctfeInterpret();
                    if (((mtype_ref.value.dim.op & 0xFF) == 127))
                        return error.invoke();
                    errors.value = global.value.errors;
                    Ref<Long> d1 = ref(mtype_ref.value.dim.toInteger());
                    if ((errors.value != global.value.errors))
                        return error.invoke();
                    mtype_ref.value.dim = mtype_ref.value.dim.implicitCastTo(sc_ref.value, Type.tsize_t.value);
                    mtype_ref.value.dim = mtype_ref.value.dim.optimize(0, false);
                    if (((mtype_ref.value.dim.op & 0xFF) == 127))
                        return error.invoke();
                    errors.value = global.value.errors;
                    Ref<Long> d2 = ref(mtype_ref.value.dim.toInteger());
                    if ((errors.value != global.value.errors))
                        return error.invoke();
                    if (((mtype_ref.value.dim.op & 0xFF) == 127))
                        return error.invoke();
                    Function0<Type> overflowError = new Function0<Type>(){
                        public Type invoke() {
                            error(loc_ref.value, new BytePtr("`%s` size %llu * %llu exceeds 0x%llx size limit for static array"), mtype_ref.value.toChars(), tbn.value.size(loc_ref.value), d1.value, target.value.maxStaticDataSize);
                            return error.invoke();
                        }
                    };
                    if ((d1.value != d2.value))
                        return overflowError.invoke();
                    Ref<Type> tbx = ref(tbn.value.baseElemOf());
                    if (((tbx.value.ty & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)tbx.value).sym.members == null) || ((tbx.value.ty & 0xFF) == ENUMTY.Tenum) && (((TypeEnum)tbx.value).sym.members == null))
                    {
                    }
                    else if ((tbn.value.isTypeBasic() != null) || ((tbn.value.ty & 0xFF) == ENUMTY.Tpointer) || ((tbn.value.ty & 0xFF) == ENUMTY.Tarray) || ((tbn.value.ty & 0xFF) == ENUMTY.Tsarray) || ((tbn.value.ty & 0xFF) == ENUMTY.Taarray) || ((tbn.value.ty & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)tbn.value).sym.sizeok == Sizeok.done) || ((tbn.value.ty & 0xFF) == ENUMTY.Tclass))
                    {
                        Ref<Boolean> overflow = ref(false);
                        if ((mulu(tbn.value.size(loc_ref.value), d2.value, overflow) >= target.value.maxStaticDataSize) || overflow.value)
                            return overflowError.invoke();
                    }
                }
                switch ((tbn.value.ty & 0xFF))
                {
                    case 37:
                        assert(mtype_ref.value.dim != null);
                        Ref<TypeTuple> tt = ref((TypeTuple)tbn.value);
                        Ref<Long> d = ref(mtype_ref.value.dim.toUInteger());
                        if ((d.value >= (long)(tt.value.arguments.get()).length))
                        {
                            error(loc_ref.value, new BytePtr("tuple index %llu exceeds %llu"), d.value, (long)(tt.value.arguments.get()).length);
                            return error.invoke();
                        }
                        Ref<Type> telem = ref((tt.value.arguments.get()).get((int)d.value).type);
                        return telem.value.addMod(mtype_ref.value.mod);
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
                mtype_ref.value.next = tn.value;
                mtype_ref.value.transitive();
                return merge(mtype_ref.value.addMod(tn.value.mod));
            }
        };
        Function1<TypeDArray,Type> visitDArray = new Function1<TypeDArray,Type>(){
            public Type invoke(TypeDArray mtype) {
                Ref<TypeDArray> mtype_ref = ref(mtype);
                Ref<Type> tn = ref(typeSemantic(mtype_ref.value.next, loc_ref.value, sc_ref.value));
                Ref<Type> tbn = ref(tn.value.toBasetype());
                switch ((tbn.value.ty & 0xFF))
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
                mtype_ref.value.next = tn.value;
                mtype_ref.value.transitive();
                return merge(mtype_ref.value);
            }
        };
        Function1<TypeAArray,Type> visitAArray = new Function1<TypeAArray,Type>(){
            public Type invoke(TypeAArray mtype) {
                Ref<TypeAArray> mtype_ref = ref(mtype);
                if (mtype_ref.value.deco != null)
                {
                    return mtype_ref.value;
                }
                mtype_ref.value.loc = loc_ref.value.copy();
                mtype_ref.value.sc = sc_ref.value;
                if (sc_ref.value != null)
                    (sc_ref.value.get()).setNoFree();
                if (((mtype_ref.value.index.ty & 0xFF) == ENUMTY.Tident) || ((mtype_ref.value.index.ty & 0xFF) == ENUMTY.Tinstance) || ((mtype_ref.value.index.ty & 0xFF) == ENUMTY.Tsarray) || ((mtype_ref.value.index.ty & 0xFF) == ENUMTY.Ttypeof) || ((mtype_ref.value.index.ty & 0xFF) == ENUMTY.Treturn))
                {
                    Ref<Expression> e = ref(null);
                    Ref<Type> t = ref(null);
                    Ref<Dsymbol> s = ref(null);
                    resolve(mtype_ref.value.index, loc_ref.value, sc_ref.value, ptr(e), ptr(t), ptr(s), false);
                    if (s.value != null)
                    {
                        {
                            Ref<FuncDeclaration> fd = ref(s.value.toAlias().isFuncDeclaration());
                            if ((fd.value) != null)
                                e.value = new CallExp(loc_ref.value, fd.value, null);
                        }
                    }
                    if (e.value != null)
                    {
                        Ref<TypeSArray> tsa = ref(new TypeSArray(mtype_ref.value.next, e.value));
                        return typeSemantic(tsa.value, loc_ref.value, sc_ref.value);
                    }
                    else if (t.value != null)
                        mtype_ref.value.index = typeSemantic(t.value, loc_ref.value, sc_ref.value);
                    else
                    {
                        error(loc_ref.value, new BytePtr("index is not a type or an expression"));
                        return error.invoke();
                    }
                }
                else
                    mtype_ref.value.index = typeSemantic(mtype_ref.value.index, loc_ref.value, sc_ref.value);
                mtype_ref.value.index = mtype_ref.value.index.merge2();
                if ((mtype_ref.value.index.nextOf() != null) && !mtype_ref.value.index.nextOf().isImmutable())
                {
                    mtype_ref.value.index = mtype_ref.value.index.constOf().mutableOf();
                }
                {
                    int __dispatch5 = 0;
                    dispatched_5:
                    do {
                        switch (__dispatch5 != 0 ? __dispatch5 : (mtype_ref.value.index.toBasetype().ty & 0xFF))
                        {
                            case 5:
                            case 12:
                            case 11:
                            case 37:
                                error(loc_ref.value, new BytePtr("cannot have associative array key of `%s`"), mtype_ref.value.index.toBasetype().toChars());
                                /*goto case*/{ __dispatch5 = 34; continue dispatched_5; }
                            case 34:
                                __dispatch5 = 0;
                                return error.invoke();
                            default:
                            break;
                        }
                    } while(__dispatch5 != 0);
                }
                Ref<Type> tbase = ref(mtype_ref.value.index.baseElemOf());
                for (; ((tbase.value.ty & 0xFF) == ENUMTY.Tarray);) {
                    tbase.value = tbase.value.nextOf().baseElemOf();
                }
                {
                    Ref<TypeStruct> ts = ref(tbase.value.isTypeStruct());
                    if ((ts.value) != null)
                    {
                        Ref<StructDeclaration> sd = ref(ts.value.sym);
                        if ((sd.value.semanticRun < PASS.semanticdone))
                            dsymbolSemantic(sd.value, null);
                        if ((sd.value.xeq != null) && (sd.value.xeq._scope != null) && (sd.value.xeq.semanticRun < PASS.semantic3done))
                        {
                            IntRef errors = ref(global.value.startGagging());
                            semantic3(sd.value.xeq, sd.value.xeq._scope);
                            if (global.value.endGagging(errors.value))
                                sd.value.xeq = StructDeclaration.xerreq.value;
                        }
                        Ref<BytePtr> s = ref(pcopy(((mtype_ref.value.index.toBasetype().ty & 0xFF) != ENUMTY.Tstruct) ? new BytePtr("bottom of ") : new BytePtr("")));
                        if (sd.value.xeq == null)
                        {
                        }
                        else if ((pequals(sd.value.xeq, StructDeclaration.xerreq.value)))
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
                        else if (sd.value.xhash == null)
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
                            assert((sd.value.xeq != null) && (sd.value.xhash != null));
                        }
                    }
                    else if (((tbase.value.ty & 0xFF) == ENUMTY.Tclass) && (((TypeClass)tbase.value).sym.isInterfaceDeclaration() == null))
                    {
                        Ref<ClassDeclaration> cd = ref(((TypeClass)tbase.value).sym);
                        if ((cd.value.semanticRun < PASS.semanticdone))
                            dsymbolSemantic(cd.value, null);
                        if (ClassDeclaration.object.value == null)
                        {
                            error(Loc.initial.value, new BytePtr("missing or corrupt object.d"));
                            fatal();
                        }
                        if (typesem.visitAArrayfeq.value == null)
                            typesem.visitAArrayfeq.value = search_function(ClassDeclaration.object.value, Id.eq.value).isFuncDeclaration();
                        if (typesem.visitAArrayfcmp.value == null)
                            typesem.visitAArrayfcmp.value = search_function(ClassDeclaration.object.value, Id.cmp.value).isFuncDeclaration();
                        if (typesem.visitAArrayfhash.value == null)
                            typesem.visitAArrayfhash.value = search_function(ClassDeclaration.object.value, Id.tohash.value).isFuncDeclaration();
                        assert((typesem.visitAArrayfcmp.value != null) && (typesem.visitAArrayfeq.value != null) && (typesem.visitAArrayfhash.value != null));
                        if ((typesem.visitAArrayfeq.value.vtblIndex < cd.value.vtbl.value.length) && (pequals(cd.value.vtbl.value.get(typesem.visitAArrayfeq.value.vtblIndex), typesem.visitAArrayfeq.value)))
                        {
                            if ((typesem.visitAArrayfcmp.value.vtblIndex < cd.value.vtbl.value.length) && (!pequals(cd.value.vtbl.value.get(typesem.visitAArrayfcmp.value.vtblIndex), typesem.visitAArrayfcmp.value)))
                            {
                                Ref<BytePtr> s = ref(pcopy(((mtype_ref.value.index.toBasetype().ty & 0xFF) != ENUMTY.Tclass) ? new BytePtr("bottom of ") : new BytePtr("")));
                                error(loc_ref.value, new BytePtr("%sAA key type `%s` now requires equality rather than comparison"), s.value, cd.value.toChars());
                                errorSupplemental(loc_ref.value, new BytePtr("Please override `Object.opEquals` and `Object.toHash`."));
                            }
                        }
                    }
                }
                mtype_ref.value.next = typeSemantic(mtype_ref.value.next, loc_ref.value, sc_ref.value).merge2();
                mtype_ref.value.transitive();
                {
                    int __dispatch6 = 0;
                    dispatched_6:
                    do {
                        switch (__dispatch6 != 0 ? __dispatch6 : (mtype_ref.value.next.toBasetype().ty & 0xFF))
                        {
                            case 5:
                            case 12:
                            case 11:
                            case 37:
                                error(loc_ref.value, new BytePtr("cannot have associative array of `%s`"), mtype_ref.value.next.toChars());
                                /*goto case*/{ __dispatch6 = 34; continue dispatched_6; }
                            case 34:
                                __dispatch6 = 0;
                                return error.invoke();
                            default:
                            break;
                        }
                    } while(__dispatch6 != 0);
                }
                if (mtype_ref.value.next.isscope())
                {
                    error(loc_ref.value, new BytePtr("cannot have array of scope `%s`"), mtype_ref.value.next.toChars());
                    return error.invoke();
                }
                return merge(mtype_ref.value);
            }
        };
        Function1<TypePointer,Type> visitPointer = new Function1<TypePointer,Type>(){
            public Type invoke(TypePointer mtype) {
                Ref<TypePointer> mtype_ref = ref(mtype);
                if (mtype_ref.value.deco != null)
                {
                    return mtype_ref.value;
                }
                Ref<Type> n = ref(typeSemantic(mtype_ref.value.next, loc_ref.value, sc_ref.value));
                {
                    int __dispatch7 = 0;
                    dispatched_7:
                    do {
                        switch (__dispatch7 != 0 ? __dispatch7 : (n.value.toBasetype().ty & 0xFF))
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
                if ((!pequals(n.value, mtype_ref.value.next)))
                {
                    mtype_ref.value.deco = null;
                }
                mtype_ref.value.next = n.value;
                if (((mtype_ref.value.next.ty & 0xFF) != ENUMTY.Tfunction))
                {
                    mtype_ref.value.transitive();
                    return merge(mtype_ref.value);
                }
                mtype_ref.value.deco = pcopy(merge(mtype_ref.value).deco);
                return mtype_ref.value;
            }
        };
        Function1<TypeReference,Type> visitReference = new Function1<TypeReference,Type>(){
            public Type invoke(TypeReference mtype) {
                Ref<TypeReference> mtype_ref = ref(mtype);
                Ref<Type> n = ref(typeSemantic(mtype_ref.value.next, loc_ref.value, sc_ref.value));
                if ((!pequals(n.value, mtype_ref.value.next)))
                    mtype_ref.value.deco = null;
                mtype_ref.value.next = n.value;
                mtype_ref.value.transitive();
                return merge(mtype_ref.value);
            }
        };
        Function1<TypeFunction,Type> visitFunction = new Function1<TypeFunction,Type>(){
            public Type invoke(TypeFunction mtype) {
                Ref<TypeFunction> mtype_ref = ref(mtype);
                if (mtype_ref.value.deco != null)
                {
                    return mtype_ref.value;
                }
                Ref<Boolean> errors = ref(false);
                if ((mtype_ref.value.inuse > 500))
                {
                    mtype_ref.value.inuse = 0;
                    error(loc_ref.value, new BytePtr("recursive type"));
                    return error.invoke();
                }
                Ref<TypeFunction> tf = ref(mtype_ref.value.copy().toTypeFunction());
                if (mtype_ref.value.parameterList.parameters != null)
                {
                    tf.value.parameterList.parameters = (mtype_ref.value.parameterList.parameters.get()).copy();
                    {
                        IntRef i = ref(0);
                        for (; (i.value < (mtype_ref.value.parameterList.parameters.get()).length);i.value++){
                            Ref<Parameter> p = ref(null);
                            (p.value) = ((mtype_ref.value.parameterList.parameters.get()).get(i.value)).copy();
                            tf.value.parameterList.parameters.get().set(i.value, p.value);
                        }
                    }
                }
                if (((sc_ref.value.get()).stc & 67108864L) != 0)
                    tf.value.purity = PURE.fwdref;
                if (((sc_ref.value.get()).stc & 33554432L) != 0)
                    tf.value.isnothrow = true;
                if (((sc_ref.value.get()).stc & 4398046511104L) != 0)
                    tf.value.isnogc = true;
                if (((sc_ref.value.get()).stc & 2097152L) != 0)
                    tf.value.isref = true;
                if (((sc_ref.value.get()).stc & 17592186044416L) != 0)
                    tf.value.isreturn = true;
                if (((sc_ref.value.get()).stc & 4503599627370496L) != 0)
                    tf.value.isreturninferred = true;
                if (((sc_ref.value.get()).stc & 524288L) != 0)
                    tf.value.isscope = true;
                if (((sc_ref.value.get()).stc & 562949953421312L) != 0)
                    tf.value.isscopeinferred = true;
                if ((tf.value.trust == TRUST.default_))
                {
                    if (((sc_ref.value.get()).stc & 8589934592L) != 0)
                        tf.value.trust = TRUST.safe;
                    else if (((sc_ref.value.get()).stc & 34359738368L) != 0)
                        tf.value.trust = TRUST.system;
                    else if (((sc_ref.value.get()).stc & 17179869184L) != 0)
                        tf.value.trust = TRUST.trusted;
                }
                if (((sc_ref.value.get()).stc & 4294967296L) != 0)
                    tf.value.isproperty = true;
                tf.value.linkage = (sc_ref.value.get()).linkage;
                Ref<Boolean> wildreturn = ref(false);
                if (tf.value.next != null)
                {
                    sc_ref.value = (sc_ref.value.get()).push();
                    (sc_ref.value.get()).stc &= -4465259184133L;
                    tf.value.next = typeSemantic(tf.value.next, loc_ref.value, sc_ref.value);
                    sc_ref.value = (sc_ref.value.get()).pop();
                    (errors.value ? 1 : 0) |= (tf.value.checkRetType(loc_ref.value) ? 1 : 0);
                    if (tf.value.next.isscope() && (((sc_ref.value.get()).flags & 1) == 0))
                    {
                        error(loc_ref.value, new BytePtr("functions cannot return `scope %s`"), tf.value.next.toChars());
                        errors.value = true;
                    }
                    if (tf.value.next.hasWild() != 0)
                        wildreturn.value = true;
                    if (tf.value.isreturn && !tf.value.isref && !tf.value.next.hasPointers())
                    {
                        tf.value.isreturn = false;
                    }
                }
                Ref<Byte> wildparams = ref((byte)0);
                if (tf.value.parameterList.parameters != null)
                {
                    Ref<Ptr<Scope>> argsc = ref((sc_ref.value.get()).push());
                    (argsc.value.get()).stc = 0L;
                    (argsc.value.get()).protection = new Prot(Prot.Kind.public_).copy();
                    (argsc.value.get()).func = null;
                    IntRef dim = ref(tf.value.parameterList.length());
                    {
                        IntRef i = ref(0);
                        for (; (i.value < dim.value);i.value++){
                            Ref<Parameter> fparam = ref(tf.value.parameterList.get(i.value));
                            mtype_ref.value.inuse++;
                            fparam.value.type = typeSemantic(fparam.value.type, loc_ref.value, argsc.value);
                            mtype_ref.value.inuse--;
                            if (((fparam.value.type.ty & 0xFF) == ENUMTY.Terror))
                            {
                                errors.value = true;
                                continue;
                            }
                            fparam.value.type = fparam.value.type.addStorageClass(fparam.value.storageClass);
                            if ((fparam.value.storageClass & 268435713L) != 0)
                            {
                                if (fparam.value.type == null)
                                    continue;
                            }
                            Ref<Type> t = ref(fparam.value.type.toBasetype());
                            if (((t.value.ty & 0xFF) == ENUMTY.Tfunction))
                            {
                                error(loc_ref.value, new BytePtr("cannot have parameter of function type `%s`"), fparam.value.type.toChars());
                                errors.value = true;
                            }
                            else if (((fparam.value.storageClass & 2101248L) == 0) && ((t.value.ty & 0xFF) == ENUMTY.Tstruct) || ((t.value.ty & 0xFF) == ENUMTY.Tsarray) || ((t.value.ty & 0xFF) == ENUMTY.Tenum))
                            {
                                Ref<Type> tb2 = ref(t.value.baseElemOf());
                                if (((tb2.value.ty & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)tb2.value).sym.members == null) || ((tb2.value.ty & 0xFF) == ENUMTY.Tenum) && (((TypeEnum)tb2.value).sym.memtype == null))
                                {
                                    error(loc_ref.value, new BytePtr("cannot have parameter of opaque type `%s` by value"), fparam.value.type.toChars());
                                    errors.value = true;
                                }
                            }
                            else if (((fparam.value.storageClass & 8192L) == 0) && ((t.value.ty & 0xFF) == ENUMTY.Tvoid))
                            {
                                error(loc_ref.value, new BytePtr("cannot have parameter of type `%s`"), fparam.value.type.toChars());
                                errors.value = true;
                            }
                            if (((fparam.value.storageClass & 2149580800L) == 2149580800L))
                            {
                                fparam.value.storageClass |= 17592186044416L;
                            }
                            if ((fparam.value.storageClass & 17592186044416L) != 0)
                            {
                                if ((fparam.value.storageClass & 2101248L) != 0)
                                {
                                    if (false)
                                    {
                                        Ref<Long> stc = ref(fparam.value.storageClass & 2101248L);
                                        error(loc_ref.value, new BytePtr("parameter `%s` is `return %s` but function does not return by `ref`"), fparam.value.ident != null ? fparam.value.ident.toChars() : new BytePtr(""), stcToChars(stc));
                                        errors.value = true;
                                    }
                                }
                                else
                                {
                                    if ((fparam.value.storageClass & 524288L) == 0)
                                        fparam.value.storageClass |= 562949953945600L;
                                    if (tf.value.isref)
                                    {
                                    }
                                    else if ((tf.value.next != null) && !tf.value.next.hasPointers() && ((tf.value.next.toBasetype().ty & 0xFF) != ENUMTY.Tvoid))
                                    {
                                        fparam.value.storageClass &= -17592186044417L;
                                    }
                                }
                            }
                            if ((fparam.value.storageClass & 2105344L) != 0)
                            {
                            }
                            else if ((fparam.value.storageClass & 4096L) != 0)
                            {
                                {
                                    Ref<Byte> m = ref((byte)((fparam.value.type.mod & 0xFF) & 13));
                                    if ((m.value) != 0)
                                    {
                                        error(loc_ref.value, new BytePtr("cannot have `%s out` parameter of type `%s`"), MODtoChars(m.value), t.value.toChars());
                                        errors.value = true;
                                    }
                                    else
                                    {
                                        Ref<Type> tv = ref(t.value.baseElemOf());
                                        if (((tv.value.ty & 0xFF) == ENUMTY.Tstruct) && ((TypeStruct)tv.value).sym.noDefaultCtor)
                                        {
                                            error(loc_ref.value, new BytePtr("cannot have `out` parameter of type `%s` because the default construction is disabled"), fparam.value.type.toChars());
                                            errors.value = true;
                                        }
                                    }
                                }
                            }
                            if (((fparam.value.storageClass & 524288L) != 0) && !fparam.value.type.hasPointers() && ((fparam.value.type.ty & 0xFF) != ENUMTY.Ttuple))
                            {
                                fparam.value.storageClass &= -524289L;
                                if (!tf.value.isref || (((sc_ref.value.get()).flags & 1) != 0))
                                    fparam.value.storageClass &= -17592186044417L;
                            }
                            if (t.value.hasWild() != 0)
                            {
                                wildparams.value |= 1;
                            }
                            if (fparam.value.defaultArg != null)
                            {
                                Ref<Expression> e = ref(fparam.value.defaultArg);
                                long isRefOrOut = fparam.value.storageClass & 2101248L;
                                long isAuto = fparam.value.storageClass & 35184372089088L;
                                if ((isRefOrOut != 0) && (isAuto == 0))
                                {
                                    e.value = expressionSemantic(e.value, argsc.value);
                                    e.value = resolveProperties(argsc.value, e.value);
                                }
                                else
                                {
                                    e.value = inferType(e.value, fparam.value.type, 0);
                                    Ref<Initializer> iz = ref(new ExpInitializer(e.value.loc, e.value));
                                    iz.value = initializerSemantic(iz.value, argsc.value, fparam.value.type, NeedInterpret.INITnointerpret);
                                    e.value = initializerToExpression(iz.value, null);
                                }
                                if (((e.value.op & 0xFF) == 161))
                                {
                                    Ref<FuncExp> fe = ref((FuncExp)e.value);
                                    e.value = new VarExp(e.value.loc, fe.value.fd, false);
                                    e.value = new AddrExp(e.value.loc, e.value);
                                    e.value = expressionSemantic(e.value, argsc.value);
                                }
                                if ((isRefOrOut != 0) && (isAuto == 0) || e.value.isLvalue() && !MODimplicitConv(e.value.type.value.mod, fparam.value.type.mod))
                                {
                                    Ref<BytePtr> errTxt = ref(pcopy((fparam.value.storageClass & 2097152L) != 0 ? new BytePtr("ref") : new BytePtr("out")));
                                    error(e.value.loc, new BytePtr("expression `%s` of type `%s` is not implicitly convertible to type `%s %s` of parameter `%s`"), e.value.toChars(), e.value.type.value.toChars(), errTxt.value, fparam.value.type.toChars(), fparam.value.toChars());
                                }
                                e.value = e.value.implicitCastTo(argsc.value, fparam.value.type);
                                if ((isRefOrOut != 0) && (isAuto == 0))
                                    e.value = e.value.toLvalue(argsc.value, e.value);
                                fparam.value.defaultArg = e.value;
                                if (((e.value.op & 0xFF) == 127))
                                    errors.value = true;
                            }
                            {
                                Ref<TypeTuple> tt = ref(t.value.isTypeTuple());
                                if ((tt.value) != null)
                                {
                                    if ((tt.value.arguments != null) && ((tt.value.arguments.get()).length != 0))
                                    {
                                        IntRef tdim = ref((tt.value.arguments.get()).length);
                                        Ref<Ptr<DArray<Parameter>>> newparams = ref(new DArray<Parameter>(tdim.value));
                                        {
                                            IntRef j = ref(0);
                                            for (; (j.value < tdim.value);j.value++){
                                                Ref<Parameter> narg = ref((tt.value.arguments.get()).get(j.value));
                                                Ref<Long> stc = ref(fparam.value.storageClass | narg.value.storageClass);
                                                Ref<Long> stc1 = ref(fparam.value.storageClass & 2109440L);
                                                Ref<Long> stc2 = ref(narg.value.storageClass & 2109440L);
                                                if ((stc1.value != 0) && (stc2.value != 0) && (stc1.value != stc2.value))
                                                {
                                                    Ref<OutBuffer> buf1 = ref(new OutBuffer());
                                                    try {
                                                        stcToBuffer(ptr(buf1), stc1.value | ((stc1.value & 2097152L) != 0 ? fparam.value.storageClass & 256L : 0L));
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
                                                Ref<Expression> paramDefaultArg = ref(narg.value.defaultArg);
                                                Ref<TupleExp> te = ref(fparam.value.defaultArg != null ? fparam.value.defaultArg.isTupleExp() : null);
                                                if ((te.value != null) && (te.value.exps != null) && ((te.value.exps.get()).length != 0))
                                                    paramDefaultArg.value = (te.value.exps.get()).get(j.value);
                                                newparams.value.get().set(j.value, new Parameter(stc.value, narg.value.type, narg.value.ident, paramDefaultArg.value, narg.value.userAttribDecl));
                                            }
                                        }
                                        fparam.value.type = new TypeTuple(newparams.value);
                                    }
                                    fparam.value.storageClass = 0L;
                                    dim.value = tf.value.parameterList.length();
                                    i.value--;
                                    continue;
                                }
                            }
                            if ((fparam.value.storageClass & 256L) != 0)
                            {
                                Ref<Expression> farg = ref((mtype_ref.value.fargs != null) && (i.value < (mtype_ref.value.fargs.get()).length) ? (mtype_ref.value.fargs.get()).get(i.value) : fparam.value.defaultArg);
                                if ((farg.value != null) && ((fparam.value.storageClass & 2097152L) != 0))
                                {
                                    if (farg.value.isLvalue())
                                    {
                                    }
                                    else
                                        fparam.value.storageClass &= -2097153L;
                                    fparam.value.storageClass &= -257L;
                                    fparam.value.storageClass |= 35184372088832L;
                                }
                                else if (mtype_ref.value.incomplete && ((fparam.value.storageClass & 2097152L) != 0))
                                {
                                    fparam.value.storageClass &= -257L;
                                    fparam.value.storageClass |= 35184372088832L;
                                }
                                else
                                {
                                    error(loc_ref.value, new BytePtr("`auto` can only be used as part of `auto ref` for template function parameters"));
                                    errors.value = true;
                                }
                            }
                            fparam.value.storageClass &= -2685405189L;
                        }
                    }
                    (argsc.value.get()).pop();
                }
                if (tf.value.isWild())
                    wildparams.value |= 2;
                if (wildreturn.value && (wildparams.value == 0))
                {
                    error(loc_ref.value, new BytePtr("`inout` on `return` means `inout` must be on a parameter as well for `%s`"), mtype_ref.value.toChars());
                    errors.value = true;
                }
                tf.value.iswild = wildparams.value;
                if (tf.value.isproperty && (tf.value.parameterList.varargs != VarArg.none) || (tf.value.parameterList.length() > 2))
                {
                    error(loc_ref.value, new BytePtr("properties can only have zero, one, or two parameter"));
                    errors.value = true;
                }
                if ((tf.value.parameterList.varargs == VarArg.variadic) && (tf.value.linkage != LINK.d) && (tf.value.parameterList.length() == 0))
                {
                    error(loc_ref.value, new BytePtr("variadic functions with non-D linkage must have at least one parameter"));
                    errors.value = true;
                }
                if (errors.value)
                    return error.invoke();
                if (tf.value.next != null)
                    tf.value.deco = pcopy(merge(tf.value).deco);
                return tf.value;
            }
        };
        Function1<TypeDelegate,Type> visitDelegate = new Function1<TypeDelegate,Type>(){
            public Type invoke(TypeDelegate mtype) {
                Ref<TypeDelegate> mtype_ref = ref(mtype);
                if (mtype_ref.value.deco != null)
                {
                    return mtype_ref.value;
                }
                mtype_ref.value.next = typeSemantic(mtype_ref.value.next, loc_ref.value, sc_ref.value);
                if (((mtype_ref.value.next.ty & 0xFF) != ENUMTY.Tfunction))
                    return error.invoke();
                mtype_ref.value.deco = pcopy(merge(mtype_ref.value).deco);
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
                    return t.value.addMod(mtype_ref.value.mod);
                }
                else
                {
                    if (s.value != null)
                    {
                        Ref<TemplateDeclaration> td = ref(s.value.isTemplateDeclaration());
                        if ((td.value != null) && (td.value.onemember != null) && (td.value.onemember.isAggregateDeclaration() != null))
                            error(loc_ref.value, new BytePtr("template %s `%s` is used as a type without instantiation; to instantiate it use `%s!(arguments)`"), s.value.kind(), s.value.toPrettyChars(false), s.value.ident.toChars());
                        else
                            error(loc_ref.value, new BytePtr("%s `%s` is used as a type"), s.value.kind(), s.value.toPrettyChars(false));
                    }
                    else
                        error(loc_ref.value, new BytePtr("`%s` is used as a type"), mtype_ref.value.toChars());
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
                    IntRef errors = ref(global.value.errors);
                    resolve(mtype_ref.value, loc_ref.value, sc_ref.value, ptr(e), ptr(t), ptr(s), false);
                    if ((t.value == null) && (errors.value != global.value.errors))
                        return error.invoke();
                }
                if (t.value == null)
                {
                    if ((e.value == null) && (s.value != null) && s.value.errors)
                    {
                        error(loc_ref.value, new BytePtr("`%s` had previous errors"), mtype_ref.value.toChars());
                    }
                    else
                        error(loc_ref.value, new BytePtr("`%s` is used as a type"), mtype_ref.value.toChars());
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
                    t.value = t.value.addMod(mtype_ref.value.mod);
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
                if (((mtype_ref.value.ty & 0xFF) == ENUMTY.Terror))
                    return mtype_ref.value;
                if ((!pequals(mtype_ref.value.exp.ident, Id.allMembers.value)) && (!pequals(mtype_ref.value.exp.ident, Id.derivedMembers.value)) && (!pequals(mtype_ref.value.exp.ident, Id.getMember.value)) && (!pequals(mtype_ref.value.exp.ident, Id.parent.value)) && (!pequals(mtype_ref.value.exp.ident, Id.getOverloads.value)) && (!pequals(mtype_ref.value.exp.ident, Id.getVirtualFunctions.value)) && (!pequals(mtype_ref.value.exp.ident, Id.getVirtualMethods.value)) && (!pequals(mtype_ref.value.exp.ident, Id.getAttributes.value)) && (!pequals(mtype_ref.value.exp.ident, Id.getUnitTests.value)) && (!pequals(mtype_ref.value.exp.ident, Id.getAliasThis.value)))
                {
                    error(mtype_ref.value.loc, new BytePtr("trait `%s` is either invalid or not supported %s"), mtype_ref.value.exp.ident.toChars(), typesem.visitTraitsctxt.get((mtype_ref.value.inAliasDeclaration ? 1 : 0)));
                    mtype_ref.value.ty = (byte)34;
                    return mtype_ref.value;
                }
                Ref<Type> result = ref(null);
                {
                    Ref<Expression> e = ref(semanticTraits(mtype_ref.value.exp, sc_ref.value));
                    if ((e.value) != null)
                    {
                        switch ((e.value.op & 0xFF))
                        {
                            case 27:
                                mtype_ref.value.sym = ((DotVarExp)e.value).var;
                                break;
                            case 26:
                                mtype_ref.value.sym = ((VarExp)e.value).var;
                                break;
                            case 161:
                                Ref<FuncExp> fe = ref((FuncExp)e.value);
                                mtype_ref.value.sym = fe.value.td != null ? fe.value.td : fe.value.fd;
                                break;
                            case 37:
                                mtype_ref.value.sym = ((DotTemplateExp)e.value).td;
                                break;
                            case 41:
                                mtype_ref.value.sym = ((DsymbolExp)e.value).s;
                                break;
                            case 36:
                                mtype_ref.value.sym = ((TemplateExp)e.value).td;
                                break;
                            case 203:
                                mtype_ref.value.sym = ((ScopeExp)e.value).sds;
                                break;
                            case 126:
                                mtype_ref.value.sym = new TupleDeclaration(e.value.loc, Identifier.generateId(new BytePtr("__aliastup")), ((Ptr<DArray<RootObject>>)e.value.toTupleExp().exps));
                                break;
                            case 30:
                                result.value = isType(((DotTypeExp)e.value).sym);
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
                    result.value = result.value.addMod(mtype_ref.value.mod);
                if (!mtype_ref.value.inAliasDeclaration && (result.value == null))
                {
                    if (global.value.errors == 0)
                        error(mtype_ref.value.loc, new BytePtr("`%s` does not give a valid type"), mtype_ref.value.toChars());
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
                    t.value = t.value.addMod(mtype_ref.value.mod);
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
                if (mtype_ref.value.deco != null)
                {
                    if ((sc_ref.value != null) && ((sc_ref.value.get()).cppmangle != CPPMANGLE.def))
                    {
                        if ((mtype_ref.value.cppmangle == CPPMANGLE.def))
                            mtype_ref.value.cppmangle = (sc_ref.value.get()).cppmangle;
                    }
                    return mtype_ref.value;
                }
                assert(mtype_ref.value.sym.parent.value != null);
                if (((mtype_ref.value.sym.type.ty & 0xFF) == ENUMTY.Terror))
                    return error.invoke();
                if ((sc_ref.value != null) && ((sc_ref.value.get()).cppmangle != CPPMANGLE.def))
                    mtype_ref.value.cppmangle = (sc_ref.value.get()).cppmangle;
                else
                    mtype_ref.value.cppmangle = CPPMANGLE.asStruct;
                return merge(mtype_ref.value);
            }
        };
        Function1<TypeEnum,Type> visitEnum = new Function1<TypeEnum,Type>(){
            public Type invoke(TypeEnum mtype) {
                Ref<TypeEnum> mtype_ref = ref(mtype);
                return mtype_ref.value.deco != null ? mtype_ref.value : merge(mtype_ref.value);
            }
        };
        Function1<TypeClass,Type> visitClass = new Function1<TypeClass,Type>(){
            public Type invoke(TypeClass mtype) {
                Ref<TypeClass> mtype_ref = ref(mtype);
                if (mtype_ref.value.deco != null)
                {
                    if ((sc_ref.value != null) && ((sc_ref.value.get()).cppmangle != CPPMANGLE.def))
                    {
                        if ((mtype_ref.value.cppmangle == CPPMANGLE.def))
                            mtype_ref.value.cppmangle = (sc_ref.value.get()).cppmangle;
                    }
                    return mtype_ref.value;
                }
                assert(mtype_ref.value.sym.parent.value != null);
                if (((mtype_ref.value.sym.type.ty & 0xFF) == ENUMTY.Terror))
                    return error.invoke();
                if ((sc_ref.value != null) && ((sc_ref.value.get()).cppmangle != CPPMANGLE.def))
                    mtype_ref.value.cppmangle = (sc_ref.value.get()).cppmangle;
                else
                    mtype_ref.value.cppmangle = CPPMANGLE.asClass;
                return merge(mtype_ref.value);
            }
        };
        Function1<TypeTuple,Type> visitTuple = new Function1<TypeTuple,Type>(){
            public Type invoke(TypeTuple mtype) {
                Ref<TypeTuple> mtype_ref = ref(mtype);
                if (mtype_ref.value.deco == null)
                    mtype_ref.value.deco = pcopy(merge(mtype_ref.value).deco);
                return mtype_ref.value;
            }
        };
        Function1<TypeSlice,Type> visitSlice = new Function1<TypeSlice,Type>(){
            public Type invoke(TypeSlice mtype) {
                Ref<TypeSlice> mtype_ref = ref(mtype);
                Ref<Type> tn = ref(typeSemantic(mtype_ref.value.next, loc_ref.value, sc_ref.value));
                Ref<Type> tbn = ref(tn.value.toBasetype());
                if (((tbn.value.ty & 0xFF) != ENUMTY.Ttuple))
                {
                    error(loc_ref.value, new BytePtr("can only slice tuple types, not `%s`"), tbn.value.toChars());
                    return error.invoke();
                }
                Ref<TypeTuple> tt = ref((TypeTuple)tbn.value);
                mtype_ref.value.lwr = semanticLength(sc_ref.value, tbn.value, mtype_ref.value.lwr);
                mtype_ref.value.upr = semanticLength(sc_ref.value, tbn.value, mtype_ref.value.upr);
                mtype_ref.value.lwr = mtype_ref.value.lwr.ctfeInterpret();
                mtype_ref.value.upr = mtype_ref.value.upr.ctfeInterpret();
                if (((mtype_ref.value.lwr.op & 0xFF) == 127) || ((mtype_ref.value.upr.op & 0xFF) == 127))
                    return error.invoke();
                Ref<Long> i1 = ref(mtype_ref.value.lwr.toUInteger());
                Ref<Long> i2 = ref(mtype_ref.value.upr.toUInteger());
                if (!((i1.value <= i2.value) && (i2.value <= (long)(tt.value.arguments.get()).length)))
                {
                    error(loc_ref.value, new BytePtr("slice `[%llu..%llu]` is out of range of `[0..%llu]`"), i1.value, i2.value, (long)(tt.value.arguments.get()).length);
                    return error.invoke();
                }
                mtype_ref.value.next = tn.value;
                mtype_ref.value.transitive();
                Ref<Ptr<DArray<Parameter>>> args = ref(new DArray<Parameter>());
                (args.value.get()).reserve((int)(i2.value - i1.value));
                {
                    Ref<Slice<Parameter>> __r1651 = ref((tt.value.arguments.get()).opSlice((int)i1.value, (int)i2.value).copy());
                    IntRef __key1652 = ref(0);
                    for (; (__key1652.value < __r1651.value.getLength());__key1652.value += 1) {
                        Ref<Parameter> arg = ref(__r1651.value.get(__key1652.value));
                        (args.value.get()).push(arg.value);
                    }
                }
                Ref<Type> t = ref(new TypeTuple(args.value));
                return typeSemantic(t.value, loc_ref.value, sc_ref.value);
            }
        };
        switch ((t.ty & 0xFF))
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
                switch (__dispatch10 != 0 ? __dispatch10 : (type.ty & 0xFF))
                {
                    case 34:
                    case 36:
                    case 6:
                    case 35:
                        return type;
                    case 9:
                        break;
                    case 2:
                        if (merge(((TypeAArray)type).index).deco == null)
                            return type;
                        /*goto default*/ { __dispatch10 = -1; continue dispatched_10; }
                    default:
                    __dispatch10 = 0;
                    if ((type.nextOf() != null) && (type.nextOf().deco == null))
                        return type;
                    break;
                }
            } while(__dispatch10 != 0);
        }
        if (type.deco == null)
        {
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                buf.value.reserve(32);
                mangleToBuffer(type, ptr(buf));
                Ptr<StringValue> sv = Type.stringtable.update(buf.value.extractSlice());
                if ((sv.get()).ptrvalue != null)
                {
                    Type t = ((Type)(sv.get()).ptrvalue);
                    assert(t.deco != null);
                    return t;
                }
                else
                {
                    Type t = stripDefaultArgs(type);
                    (sv.get()).ptrvalue = pcopy(((Object)t));
                    type.deco = pcopy((t.deco = pcopy((sv.get()).toDchars())));
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
                        return new ErrorExp();
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
                    Ref<Type> tb = ref(mt_ref.value.toBasetype());
                    e.value = mt_ref.value.defaultInitLiteral(loc);
                    if (((tb.value.ty & 0xFF) == ENUMTY.Tstruct) && tb.value.needsNested())
                    {
                        e.value.isStructLiteralExp().useStaticInit = true;
                    }
                }
                else if ((pequals(ident_ref.value, Id._mangleof.value)))
                {
                    if (mt_ref.value.deco == null)
                    {
                        error(loc, new BytePtr("forward reference of type `%s.mangleof`"), mt_ref.value.toChars());
                        e.value = new ErrorExp();
                    }
                    else
                    {
                        e.value = new StringExp(loc, mt_ref.value.deco);
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
                    if (((mt_ref.value.ty & 0xFF) == ENUMTY.Tstruct) || ((mt_ref.value.ty & 0xFF) == ENUMTY.Tclass) || ((mt_ref.value.ty & 0xFF) == ENUMTY.Tenum))
                        s.value = mt_ref.value.toDsymbol(null);
                    if (s.value != null)
                        s.value = s.value.search_correct(ident_ref.value);
                    if ((!pequals(mt_ref.value, Type.terror.value)))
                    {
                        if (s.value != null)
                            error(loc, new BytePtr("no property `%s` for type `%s`, did you mean `%s`?"), ident_ref.value.toChars(), mt_ref.value.toChars(), s.value.toPrettyChars(false));
                        else
                        {
                            if ((pequals(ident_ref.value, Id.call.value)) && ((mt_ref.value.ty & 0xFF) == ENUMTY.Tclass))
                                error(loc, new BytePtr("no property `%s` for type `%s`, did you mean `new %s`?"), ident_ref.value.toChars(), mt_ref.value.toChars(), mt_ref.value.toPrettyChars(false));
                            else
                                error(loc, new BytePtr("no property `%s` for type `%s`"), ident_ref.value.toChars(), mt_ref.value.toChars());
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
                            return new RealExp(loc, r_ref.value, mt_ref.value);
                        else
                        {
                            return new ComplexExp(loc, new complex_t(r_ref.value, r_ref.value), mt_ref.value);
                        }
                    }
                };
                if ((pequals(ident_ref.value, Id.max.value)))
                {
                    switch ((mt_ref.value.ty & 0xFF))
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
                            return floatValue.invoke(target.value.FloatProperties.max);
                        case 28:
                        case 25:
                        case 22:
                            return floatValue.invoke(target.value.DoubleProperties.max);
                        case 29:
                        case 26:
                        case 23:
                            return floatValue.invoke(target.value.RealProperties.max);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.min.value)))
                {
                    switch ((mt_ref.value.ty & 0xFF))
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
                    switch ((mt_ref.value.ty & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return floatValue.invoke(target.value.FloatProperties.min_normal);
                        case 28:
                        case 25:
                        case 22:
                            return floatValue.invoke(target.value.DoubleProperties.min_normal);
                        case 29:
                        case 26:
                        case 23:
                            return floatValue.invoke(target.value.RealProperties.min_normal);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.nan.value)))
                {
                    switch ((mt_ref.value.ty & 0xFF))
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
                            return floatValue.invoke(target.value.RealProperties.nan);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.infinity.value)))
                {
                    switch ((mt_ref.value.ty & 0xFF))
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
                            return floatValue.invoke(target.value.RealProperties.infinity);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.dig.value)))
                {
                    switch ((mt_ref.value.ty & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return intValue.invoke((long)target.value.FloatProperties.dig);
                        case 28:
                        case 25:
                        case 22:
                            return intValue.invoke((long)target.value.DoubleProperties.dig);
                        case 29:
                        case 26:
                        case 23:
                            return intValue.invoke((long)target.value.RealProperties.dig);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.epsilon.value)))
                {
                    switch ((mt_ref.value.ty & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return floatValue.invoke(target.value.FloatProperties.epsilon);
                        case 28:
                        case 25:
                        case 22:
                            return floatValue.invoke(target.value.DoubleProperties.epsilon);
                        case 29:
                        case 26:
                        case 23:
                            return floatValue.invoke(target.value.RealProperties.epsilon);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.mant_dig.value)))
                {
                    switch ((mt_ref.value.ty & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return intValue.invoke((long)target.value.FloatProperties.mant_dig);
                        case 28:
                        case 25:
                        case 22:
                            return intValue.invoke((long)target.value.DoubleProperties.mant_dig);
                        case 29:
                        case 26:
                        case 23:
                            return intValue.invoke((long)target.value.RealProperties.mant_dig);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.max_10_exp.value)))
                {
                    switch ((mt_ref.value.ty & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return intValue.invoke((long)target.value.FloatProperties.max_10_exp);
                        case 28:
                        case 25:
                        case 22:
                            return intValue.invoke((long)target.value.DoubleProperties.max_10_exp);
                        case 29:
                        case 26:
                        case 23:
                            return intValue.invoke((long)target.value.RealProperties.max_10_exp);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.max_exp.value)))
                {
                    switch ((mt_ref.value.ty & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return intValue.invoke((long)target.value.FloatProperties.max_exp);
                        case 28:
                        case 25:
                        case 22:
                            return intValue.invoke((long)target.value.DoubleProperties.max_exp);
                        case 29:
                        case 26:
                        case 23:
                            return intValue.invoke((long)target.value.RealProperties.max_exp);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.min_10_exp.value)))
                {
                    switch ((mt_ref.value.ty & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return intValue.invoke((long)target.value.FloatProperties.min_10_exp);
                        case 28:
                        case 25:
                        case 22:
                            return intValue.invoke((long)target.value.DoubleProperties.min_10_exp);
                        case 29:
                        case 26:
                        case 23:
                            return intValue.invoke((long)target.value.RealProperties.min_10_exp);
                        default:
                        break;
                    }
                }
                else if ((pequals(ident_ref.value, Id.min_exp.value)))
                {
                    switch ((mt_ref.value.ty & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return intValue.invoke((long)target.value.FloatProperties.min_exp);
                        case 28:
                        case 25:
                        case 22:
                            return intValue.invoke((long)target.value.DoubleProperties.min_exp);
                        case 29:
                        case 26:
                        case 23:
                            return intValue.invoke((long)target.value.RealProperties.min_exp);
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
                    return mt_ref.value.sym.getMaxMinValue(loc, ident_ref.value);
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
                Ref<TypeTuple> mt_ref = ref(mt);
                Ref<Expression> e = ref(null);
                if ((pequals(ident_ref.value, Id.length.value)))
                {
                    e.value = new IntegerExp(loc, (long)(mt_ref.value.arguments.get()).length, Type.tsize_t.value);
                }
                else if ((pequals(ident_ref.value, Id._init.value)))
                {
                    e.value = mt_ref.value.defaultInitLiteral(loc);
                }
                else if (flag_ref.value != 0)
                {
                    e.value = null;
                }
                else
                {
                    error(loc, new BytePtr("no property `%s` for tuple `%s`"), ident_ref.value.toChars(), mt_ref.value.toChars());
                    e.value = new ErrorExp();
                }
                return e.value;
            }
        };
        switch ((t.ty & 0xFF))
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
                switch (__dispatch24 != 0 ? __dispatch24 : (e.op & 0xFF))
                {
                    case 127:
                        pt.set(0, Type.terror.value);
                        return ;
                    case 20:
                        pt.set(0, e.type.value);
                        return ;
                    case 26:
                        s = ((VarExp)e).var;
                        if (s.isVarDeclaration() != null)
                            /*goto default*/ { __dispatch24 = -1; continue dispatched_24; }
                        break;
                    case 36:
                        s = ((TemplateExp)e).td;
                        break;
                    case 203:
                        s = ((ScopeExp)e).sds;
                        break;
                    case 161:
                        s = getDsymbol(e);
                        break;
                    case 37:
                        s = ((DotTemplateExp)e).td;
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
            }
        };
        Function1<Type,Void> returnType = new Function1<Type,Void>(){
            public Void invoke(Type t) {
                Ref<Type> t_ref = ref(t);
                pt_ref.value.set(0, t_ref.value);
                pe_ref.value.set(0, null);
                ps_ref.value.set(0, null);
            }
        };
        Function1<Dsymbol,Void> returnSymbol = new Function1<Dsymbol,Void>(){
            public Void invoke(Dsymbol s) {
                Ref<Dsymbol> s_ref = ref(s);
                pt_ref.value.set(0, null);
                pe_ref.value.set(0, null);
                ps_ref.value.set(0, s_ref.value);
            }
        };
        Function0<Void> returnError = new Function0<Void>(){
            public Void invoke() {
                returnType.invoke(Type.terror.value);
            }
        };
        Function1<Type,Void> visitType = new Function1<Type,Void>(){
            public Void invoke(Type mt) {
                Ref<Type> mt_ref = ref(mt);
                Ref<Type> t = ref(typeSemantic(mt_ref.value, loc_ref.value, sc_ref.value));
                assert(t.value != null);
                returnType.invoke(t.value);
            }
        };
        Function1<TypeSArray,Void> visitSArray = new Function1<TypeSArray,Void>(){
            public Void invoke(TypeSArray mt) {
                Ref<TypeSArray> mt_ref = ref(mt);
                resolve(mt_ref.value.next, loc_ref.value, sc_ref.value, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                if (pe_ref.value.get() != null)
                {
                    {
                        Ref<Dsymbol> s = ref(getDsymbol(pe_ref.value.get()));
                        if ((s.value) != null)
                            pe_ref.value.set(0, (new DsymbolExp(loc_ref.value, s.value, true)));
                    }
                    returnExp.invoke(new ArrayExp(loc_ref.value, pe_ref.value.get(), mt_ref.value.dim));
                }
                else if (ps_ref.value.get() != null)
                {
                    Ref<Dsymbol> s = ref(ps_ref.value.get());
                    {
                        Ref<TupleDeclaration> tup = ref(s.value.isTupleDeclaration());
                        if ((tup.value) != null)
                        {
                            mt_ref.value.dim = semanticLength(sc_ref.value, tup.value, mt_ref.value.dim);
                            mt_ref.value.dim = mt_ref.value.dim.ctfeInterpret();
                            if (((mt_ref.value.dim.op & 0xFF) == 127))
                                returnError.invoke();
                                return null;
                            Ref<Long> d = ref(mt_ref.value.dim.toUInteger());
                            if ((d.value >= (long)(tup.value.objects.get()).length))
                            {
                                error(loc_ref.value, new BytePtr("tuple index `%llu` exceeds length %u"), d.value, (tup.value.objects.get()).length);
                                returnError.invoke();
                                return null;
                            }
                            Ref<RootObject> o = ref((tup.value.objects.get()).get((int)d.value));
                            if ((o.value.dyncast() == DYNCAST.dsymbol))
                            {
                                returnSymbol.invoke((Dsymbol)o.value);
                                return null;
                            }
                            if ((o.value.dyncast() == DYNCAST.expression))
                            {
                                Ref<Expression> e = ref((Expression)o.value);
                                if (((e.value.op & 0xFF) == 41))
                                    returnSymbol.invoke(((DsymbolExp)e.value).s);
                                    return null;
                                else
                                    returnExp.invoke(e.value);
                                    return null;
                            }
                            if ((o.value.dyncast() == DYNCAST.type))
                            {
                                returnType.invoke(((Type)o.value).addMod(mt_ref.value.mod));
                                return null;
                            }
                            Ref<Ptr<DArray<RootObject>>> objects = ref(new DArray<RootObject>(1));
                            objects.value.get().set(0, o.value);
                            returnSymbol.invoke(new TupleDeclaration(loc_ref.value, tup.value.ident, objects.value));
                            return null;
                        }
                        else
                            visitType.invoke(mt_ref.value);
                            return null;
                    }
                }
                else
                {
                    if ((((pt_ref.value.get()).ty & 0xFF) != ENUMTY.Terror))
                        mt_ref.value.next = pt_ref.value.get();
                    visitType.invoke(mt_ref.value);
                }
            }
        };
        Function1<TypeDArray,Void> visitDArray = new Function1<TypeDArray,Void>(){
            public Void invoke(TypeDArray mt) {
                Ref<TypeDArray> mt_ref = ref(mt);
                resolve(mt_ref.value.next, loc_ref.value, sc_ref.value, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                if (pe_ref.value.get() != null)
                {
                    {
                        Ref<Dsymbol> s = ref(getDsymbol(pe_ref.value.get()));
                        if ((s.value) != null)
                            pe_ref.value.set(0, (new DsymbolExp(loc_ref.value, s.value, true)));
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
                            visitType.invoke(mt_ref.value);
                    }
                }
                else
                {
                    if ((((pt_ref.value.get()).ty & 0xFF) != ENUMTY.Terror))
                        mt_ref.value.next = pt_ref.value.get();
                    visitType.invoke(mt_ref.value);
                }
            }
        };
        Function1<TypeAArray,Void> visitAArray = new Function1<TypeAArray,Void>(){
            public Void invoke(TypeAArray mt) {
                Ref<TypeAArray> mt_ref = ref(mt);
                if (((mt_ref.value.index.ty & 0xFF) == ENUMTY.Tident) || ((mt_ref.value.index.ty & 0xFF) == ENUMTY.Tinstance) || ((mt_ref.value.index.ty & 0xFF) == ENUMTY.Tsarray))
                {
                    Ref<Expression> e = ref(null);
                    Ref<Type> t = ref(null);
                    Ref<Dsymbol> s = ref(null);
                    resolve(mt_ref.value.index, loc_ref.value, sc_ref.value, ptr(e), ptr(t), ptr(s), intypeid_ref.value);
                    if (e.value != null)
                    {
                        Ref<TypeSArray> tsa = ref(new TypeSArray(mt_ref.value.next, e.value));
                        tsa.value.mod = mt_ref.value.mod;
                        resolve(tsa.value, loc_ref.value, sc_ref.value, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                        return null;
                    }
                    else if (t.value != null)
                        mt_ref.value.index = t.value;
                    else
                        error(loc_ref.value, new BytePtr("index is not a type or an expression"));
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
                if (mt_ref.value.ident.equals(Id._super.value) || mt_ref.value.ident.equals(Id.This.value) && (hasThis(sc_ref.value) == null))
                {
                    if (mt_ref.value.ident.equals(Id._super.value))
                    {
                        error(mt_ref.value.loc, new BytePtr("Using `super` as a type is obsolete. Use `typeof(super)` instead"));
                    }
                    if (mt_ref.value.ident.equals(Id.This.value))
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
                                    if (mt_ref.value.ident.equals(Id.This.value))
                                        mt_ref.value.ident = cd.value.ident;
                                    else if ((cd.value.baseClass != null) && mt_ref.value.ident.equals(Id._super.value))
                                        mt_ref.value.ident = cd.value.baseClass.ident;
                                }
                                else
                                {
                                    Ref<StructDeclaration> sd = ref(ad.value.isStructDeclaration());
                                    if ((sd.value != null) && mt_ref.value.ident.equals(Id.This.value))
                                        mt_ref.value.ident = sd.value.ident;
                                }
                            }
                        }
                    }
                }
                if ((pequals(mt_ref.value.ident, Id.ctfe.value)))
                {
                    error(loc_ref.value, new BytePtr("variable `__ctfe` cannot be read at compile time"));
                    returnError.invoke();
                    return null;
                }
                Ref<Dsymbol> scopesym = ref(null);
                Ref<Dsymbol> s = ref((sc_ref.value.get()).search(loc_ref.value, mt_ref.value.ident, ptr(scopesym), 0));
                if ((s.value == null) && ((sc_ref.value.get()).enclosing != null))
                {
                    Ref<ScopeDsymbol> sds = ref(((sc_ref.value.get()).enclosing.get()).scopesym);
                    if ((sds.value != null) && (sds.value.members != null))
                    {
                        Function1<Dsymbol,Void> semanticOnMixin = new Function1<Dsymbol,Void>(){
                            public Void invoke(Dsymbol member) {
                                Ref<Dsymbol> member_ref = ref(member);
                                {
                                    Ref<CompileDeclaration> compileDecl = ref(member_ref.value.isCompileDeclaration());
                                    if ((compileDecl.value) != null)
                                        dsymbolSemantic(compileDecl.value, sc_ref.value);
                                    else {
                                        Ref<TemplateMixin> mixinTempl = ref(member_ref.value.isTemplateMixin());
                                        if ((mixinTempl.value) != null)
                                            dsymbolSemantic(mixinTempl.value, sc_ref.value);
                                    }
                                }
                            }
                        };
                        foreachDsymbol(sds.value.members, __lambda3);
                        s.value = (sc_ref.value.get()).search(loc_ref.value, mt_ref.value.ident, ptr(scopesym), 0);
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
                                    if (td.value.overroot != null)
                                        td.value = td.value.overroot;
                                    s.value = td.value;
                                }
                            }
                        }
                    }
                }
                resolveHelper(mt_ref.value, loc_ref.value, sc_ref.value, s.value, scopesym.value, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                if (pt_ref.value.get() != null)
                    pt_ref.value.set(0, (pt_ref.value.get()).addMod(mt_ref.value.mod));
            }
        };
        Function1<TypeInstance,Void> visitInstance = new Function1<TypeInstance,Void>(){
            public Void invoke(TypeInstance mt) {
                Ref<TypeInstance> mt_ref = ref(mt);
                dsymbolSemantic(mt_ref.value.tempinst, sc_ref.value);
                if ((global.value.gag == 0) && mt_ref.value.tempinst.errors)
                    returnError.invoke();
                    return null;
                resolveHelper(mt_ref.value, loc_ref.value, sc_ref.value, mt_ref.value.tempinst, null, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                if (pt_ref.value.get() != null)
                    pt_ref.value.set(0, (pt_ref.value.get()).addMod(mt_ref.value.mod));
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
                if (mt_ref.value.inuse != 0)
                {
                    mt_ref.value.inuse = 2;
                    error(loc_ref.value, new BytePtr("circular `typeof` definition"));
                /*Lerr:*/
                    mt_ref.value.inuse--;
                    returnError.invoke();
                    return null;
                }
                mt_ref.value.inuse++;
                Ref<Ptr<Scope>> sc2 = ref((sc_ref.value.get()).push());
                (sc2.value.get()).intypeof = 1;
                Ref<Expression> exp2 = ref(expressionSemantic(mt_ref.value.exp, sc2.value));
                exp2.value = resolvePropertiesOnly(sc2.value, exp2.value);
                (sc2.value.get()).pop();
                if (((exp2.value.op & 0xFF) == 127))
                {
                    if (global.value.gag == 0)
                        mt_ref.value.exp = exp2.value;
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                mt_ref.value.exp = exp2.value;
                if (((mt_ref.value.exp.op & 0xFF) == 20) || ((mt_ref.value.exp.op & 0xFF) == 203))
                {
                    if (mt_ref.value.exp.checkType())
                        /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                {
                    Ref<FuncDeclaration> f = ref(((mt_ref.value.exp.op & 0xFF) == 26) ? ((VarExp)mt_ref.value.exp).var.isFuncDeclaration() : ((mt_ref.value.exp.op & 0xFF) == 27) ? ((DotVarExp)mt_ref.value.exp).var.isFuncDeclaration() : null);
                    if ((f.value) != null)
                    {
                        if (f.value.checkForwardRef(loc_ref.value))
                            /*goto Lerr*/throw Dispatch0.INSTANCE;
                    }
                }
                {
                    Ref<FuncDeclaration> f = ref(isFuncAddress(mt_ref.value.exp, null));
                    if ((f.value) != null)
                    {
                        if (f.value.checkForwardRef(loc_ref.value))
                            /*goto Lerr*/throw Dispatch0.INSTANCE;
                    }
                }
                Ref<Type> t = ref(mt_ref.value.exp.type.value);
                if (t.value == null)
                {
                    error(loc_ref.value, new BytePtr("expression `%s` has no type"), mt_ref.value.exp.toChars());
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                if (((t.value.ty & 0xFF) == ENUMTY.Ttypeof))
                {
                    error(loc_ref.value, new BytePtr("forward reference to `%s`"), mt_ref.value.toChars());
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                if ((mt_ref.value.idents.length == 0))
                {
                    returnType.invoke(t.value.addMod(mt_ref.value.mod));
                }
                else
                {
                    {
                        Ref<Dsymbol> s = ref(t.value.toDsymbol(sc_ref.value));
                        if ((s.value) != null)
                            resolveHelper(mt_ref.value, loc_ref.value, sc_ref.value, s.value, null, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                        else
                        {
                            Ref<Expression> e = ref(typeToExpressionHelper(mt_ref.value, new TypeExp(loc_ref.value, t.value), 0));
                            e.value = expressionSemantic(e.value, sc_ref.value);
                            resolveExp(e.value, pt_ref.value, pe_ref.value, ps_ref.value);
                        }
                    }
                    if (pt_ref.value.get() != null)
                        pt_ref.value.set(0, (pt_ref.value.get()).addMod(mt_ref.value.mod));
                }
                mt_ref.value.inuse--;
            }
        };
        Function1<TypeReturn,Void> visitReturn = new Function1<TypeReturn,Void>(){
            public Void invoke(TypeReturn mt) {
                Ref<TypeReturn> mt_ref = ref(mt);
                Ref<Type> t = ref(null);
                {
                    Ref<FuncDeclaration> func = ref((sc_ref.value.get()).func);
                    if (func.value == null)
                    {
                        error(loc_ref.value, new BytePtr("`typeof(return)` must be inside function"));
                        returnError.invoke();
                        return null;
                    }
                    if (func.value.fes != null)
                        func.value = func.value.fes.func;
                    t.value = func.value.type.nextOf();
                    if (t.value == null)
                    {
                        error(loc_ref.value, new BytePtr("cannot use `typeof(return)` inside function `%s` with inferred return type"), (sc_ref.value.get()).func.toChars());
                        returnError.invoke();
                        return null;
                    }
                }
                if ((mt_ref.value.idents.length == 0))
                {
                    returnType.invoke(t.value.addMod(mt_ref.value.mod));
                    return null;
                }
                else
                {
                    {
                        Ref<Dsymbol> s = ref(t.value.toDsymbol(sc_ref.value));
                        if ((s.value) != null)
                            resolveHelper(mt_ref.value, loc_ref.value, sc_ref.value, s.value, null, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                        else
                        {
                            Ref<Expression> e = ref(typeToExpressionHelper(mt_ref.value, new TypeExp(loc_ref.value, t.value), 0));
                            e.value = expressionSemantic(e.value, sc_ref.value);
                            resolveExp(e.value, pt_ref.value, pe_ref.value, ps_ref.value);
                        }
                    }
                    if (pt_ref.value.get() != null)
                        pt_ref.value.set(0, (pt_ref.value.get()).addMod(mt_ref.value.mod));
                }
            }
        };
        Function1<TypeSlice,Void> visitSlice = new Function1<TypeSlice,Void>(){
            public Void invoke(TypeSlice mt) {
                Ref<TypeSlice> mt_ref = ref(mt);
                resolve(mt_ref.value.next, loc_ref.value, sc_ref.value, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                if (pe_ref.value.get() != null)
                {
                    {
                        Ref<Dsymbol> s = ref(getDsymbol(pe_ref.value.get()));
                        if ((s.value) != null)
                            pe_ref.value.set(0, (new DsymbolExp(loc_ref.value, s.value, true)));
                    }
                    returnExp.invoke(new ArrayExp(loc_ref.value, pe_ref.value.get(), new IntervalExp(loc_ref.value, mt_ref.value.lwr, mt_ref.value.upr)));
                    return null;
                }
                else if (ps_ref.value.get() != null)
                {
                    Ref<Dsymbol> s = ref(ps_ref.value.get());
                    Ref<TupleDeclaration> td = ref(s.value.isTupleDeclaration());
                    if (td.value != null)
                    {
                        Ref<ScopeDsymbol> sym = ref(new ArrayScopeSymbol(sc_ref.value, td.value));
                        sym.value.parent.value = (sc_ref.value.get()).scopesym;
                        sc_ref.value = (sc_ref.value.get()).push(sym.value);
                        sc_ref.value = (sc_ref.value.get()).startCTFE();
                        mt_ref.value.lwr = expressionSemantic(mt_ref.value.lwr, sc_ref.value);
                        mt_ref.value.upr = expressionSemantic(mt_ref.value.upr, sc_ref.value);
                        sc_ref.value = (sc_ref.value.get()).endCTFE();
                        sc_ref.value = (sc_ref.value.get()).pop();
                        mt_ref.value.lwr = mt_ref.value.lwr.ctfeInterpret();
                        mt_ref.value.upr = mt_ref.value.upr.ctfeInterpret();
                        Ref<Long> i1 = ref(mt_ref.value.lwr.toUInteger());
                        Ref<Long> i2 = ref(mt_ref.value.upr.toUInteger());
                        if (!((i1.value <= i2.value) && (i2.value <= (long)(td.value.objects.get()).length)))
                        {
                            error(loc_ref.value, new BytePtr("slice `[%llu..%llu]` is out of range of [0..%u]"), i1.value, i2.value, (td.value.objects.get()).length);
                            returnError.invoke();
                            return null;
                        }
                        if ((i1.value == 0L) && (i2.value == (long)(td.value.objects.get()).length))
                        {
                            returnSymbol.invoke(td.value);
                            return null;
                        }
                        Ref<Ptr<DArray<RootObject>>> objects = ref(new DArray<RootObject>((int)(i2.value - i1.value)));
                        {
                            IntRef i = ref(0);
                            for (; (i.value < (objects.value.get()).length);i.value++){
                                objects.value.get().set(i.value, (td.value.objects.get()).get((int)i1.value + i.value));
                            }
                        }
                        returnSymbol.invoke(new TupleDeclaration(loc_ref.value, td.value.ident, objects.value));
                        return null;
                    }
                    else
                        visitType.invoke(mt_ref.value);
                }
                else
                {
                    if ((((pt_ref.value.get()).ty & 0xFF) != ENUMTY.Terror))
                        mt_ref.value.next = pt_ref.value.get();
                    visitType.invoke(mt_ref.value);
                }
            }
        };
        switch ((mt.ty & 0xFF))
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
                for (; ((ex.value.op & 0xFF) == 99);) {
                    ex.value = ((CommaExp)ex.value).e2.value;
                }
                if (((ex.value.op & 0xFF) == 27))
                {
                    Ref<DotVarExp> dv = ref((DotVarExp)ex.value);
                    v.value = dv.value.var.isVarDeclaration();
                }
                else if (((ex.value.op & 0xFF) == 26))
                {
                    Ref<VarExp> ve = ref((VarExp)ex.value);
                    v.value = ve.value.var.isVarDeclaration();
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
                                ad.value.size(e_ref.value.loc);
                                if ((ad.value.sizeok != Sizeok.done))
                                    return new ErrorExp();
                                return new IntegerExp(e_ref.value.loc, (long)v.value.offset, Type.tsize_t.value);
                            }
                        }
                        else if ((pequals(ident_ref.value, Id._init.value)))
                        {
                            Ref<Type> tb = ref(mt_ref.value.toBasetype());
                            e_ref.value = mt_ref.value.defaultInitLiteral(e_ref.value.loc);
                            if (((tb.value.ty & 0xFF) == ENUMTY.Tstruct) && tb.value.needsNested())
                            {
                                e_ref.value.isStructLiteralExp().useStaticInit = true;
                            }
                            /*goto Lreturn*/throw Dispatch0.INSTANCE;
                        }
                    }
                    if ((pequals(ident_ref.value, Id.stringof.value)))
                    {
                        Ref<BytePtr> s = ref(pcopy(e_ref.value.toChars()));
                        e_ref.value = new StringExp(e_ref.value.loc, s.value);
                    }
                    else
                        e_ref.value = getProperty(mt_ref.value, e_ref.value.loc, ident_ref.value, flag_ref.value & DotExpFlag.gag);
                }
                catch(Dispatch0 __d){}
            /*Lreturn:*/
                if (e_ref.value != null)
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
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
                            switch (__dispatch26 != 0 ? __dispatch26 : (mt_ref.value.ty & 0xFF))
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
                                    e_ref.value = new RealExp(e_ref.value.loc, CTFloat.zero.value, t.value);
                                    break;
                                default:
                                e_ref.value = getProperty(mt_ref.value.Type, e_ref.value.loc, ident_ref.value, flag_ref.value);
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
                            switch (__dispatch27 != 0 ? __dispatch27 : (mt_ref.value.ty & 0xFF))
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
                                    e_ref.value = new RealExp(e_ref.value.loc, CTFloat.zero.value, mt_ref.value);
                                    break;
                                default:
                                e_ref.value = getProperty(mt_ref.value.Type, e_ref.value.loc, ident_ref.value, flag_ref.value);
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
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                return e_ref.value;
            }
        };
        Function1<TypeVector,Expression> visitVector = new Function1<TypeVector,Expression>(){
            public Expression invoke(TypeVector mt) {
                Ref<TypeVector> mt_ref = ref(mt);
                if ((pequals(ident_ref.value, Id.ptr.value)) && ((e_ref.value.op & 0xFF) == 18))
                {
                    e_ref.value = new AddrExp(e_ref.value.loc, e_ref.value);
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                    return e_ref.value.castTo(sc_ref.value, mt_ref.value.basetype.nextOf().pointerTo());
                }
                if ((pequals(ident_ref.value, Id.array.value)))
                {
                    e_ref.value = new VectorArrayExp(e_ref.value.loc, e_ref.value);
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                    return e_ref.value;
                }
                if ((pequals(ident_ref.value, Id._init.value)) || (pequals(ident_ref.value, Id.offsetof.value)) || (pequals(ident_ref.value, Id.stringof.value)) || (pequals(ident_ref.value, Id.__xalignof.value)))
                {
                    return visitType.invoke(mt_ref.value);
                }
                return dotExp(mt_ref.value.basetype, sc_ref.value, e_ref.value.castTo(sc_ref.value, mt_ref.value.basetype), ident_ref.value, flag_ref.value);
            }
        };
        Function1<TypeArray,Expression> visitArray = new Function1<TypeArray,Expression>(){
            public Expression invoke(TypeArray mt) {
                Ref<TypeArray> mt_ref = ref(mt);
                e_ref.value = visitType.invoke(mt_ref.value);
                if (((flag_ref.value & 1) == 0) || (e_ref.value != null))
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                return e_ref.value;
            }
        };
        Function1<TypeSArray,Expression> visitSArray = new Function1<TypeSArray,Expression>(){
            public Expression invoke(TypeSArray mt) {
                Ref<TypeSArray> mt_ref = ref(mt);
                if ((pequals(ident_ref.value, Id.length.value)))
                {
                    Ref<Loc> oldLoc = ref(e_ref.value.loc.copy());
                    e_ref.value = mt_ref.value.dim.copy();
                    e_ref.value.loc = oldLoc.value.copy();
                }
                else if ((pequals(ident_ref.value, Id.ptr.value)))
                {
                    if (((e_ref.value.op & 0xFF) == 20))
                    {
                        e_ref.value.error(new BytePtr("`%s` is not an expression"), e_ref.value.toChars());
                        return new ErrorExp();
                    }
                    else if (((flag_ref.value & DotExpFlag.noDeref) == 0) && ((sc_ref.value.get()).func != null) && ((sc_ref.value.get()).intypeof == 0) && (sc_ref.value.get()).func.setUnsafe() && (((sc_ref.value.get()).flags & 8) == 0))
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
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                return e_ref.value;
            }
        };
        Function1<TypeDArray,Expression> visitDArray = new Function1<TypeDArray,Expression>(){
            public Expression invoke(TypeDArray mt) {
                Ref<TypeDArray> mt_ref = ref(mt);
                if (((e_ref.value.op & 0xFF) == 20) && (pequals(ident_ref.value, Id.length.value)) || (pequals(ident_ref.value, Id.ptr.value)))
                {
                    e_ref.value.error(new BytePtr("`%s` is not an expression"), e_ref.value.toChars());
                    return new ErrorExp();
                }
                if ((pequals(ident_ref.value, Id.length.value)))
                {
                    if (((e_ref.value.op & 0xFF) == 121))
                    {
                        Ref<StringExp> se = ref((StringExp)e_ref.value);
                        return new IntegerExp(se.value.loc, (long)se.value.len, Type.tsize_t.value);
                    }
                    if (((e_ref.value.op & 0xFF) == 13))
                    {
                        return new IntegerExp(e_ref.value.loc, 0L, Type.tsize_t.value);
                    }
                    if (checkNonAssignmentArrayOp(e_ref.value, false))
                    {
                        return new ErrorExp();
                    }
                    e_ref.value = new ArrayLengthExp(e_ref.value.loc, e_ref.value);
                    e_ref.value.type.value = Type.tsize_t.value;
                    return e_ref.value;
                }
                else if ((pequals(ident_ref.value, Id.ptr.value)))
                {
                    if (((flag_ref.value & DotExpFlag.noDeref) == 0) && ((sc_ref.value.get()).func != null) && ((sc_ref.value.get()).intypeof == 0) && (sc_ref.value.get()).func.setUnsafe() && (((sc_ref.value.get()).flags & 8) == 0))
                    {
                        e_ref.value.error(new BytePtr("`%s.ptr` cannot be used in `@safe` code, use `&%s[0]` instead"), e_ref.value.toChars(), e_ref.value.toChars());
                        return new ErrorExp();
                    }
                    return e_ref.value.castTo(sc_ref.value, mt_ref.value.next.pointerTo());
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
                        Ref<Ptr<DArray<Parameter>>> fparams = ref(new DArray<Parameter>());
                        (fparams.value.get()).push(new Parameter(2048L, mt_ref.value, null, null, null));
                        typesem.visitAArrayfd_aaLen.value = FuncDeclaration.genCfunc(fparams.value, Type.tsize_t.value, Id.aaLen.value, 0L);
                        Ref<TypeFunction> tf = ref(typesem.visitAArrayfd_aaLen.value.type.toTypeFunction());
                        tf.value.purity = PURE.const_;
                        tf.value.isnothrow = true;
                        tf.value.isnogc = false;
                    }
                    Ref<Expression> ev = ref(new VarExp(e_ref.value.loc, typesem.visitAArrayfd_aaLen.value, false));
                    e_ref.value = new CallExp(e_ref.value.loc, ev.value, e_ref.value);
                    e_ref.value.type.value = typesem.visitAArrayfd_aaLen.value.type.toTypeFunction().next;
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
                Ref<TypeReference> mt_ref = ref(mt);
                return dotExp(mt_ref.value.next, sc_ref.value, e_ref.value, ident_ref.value, flag_ref.value);
            }
        };
        Function1<TypeDelegate,Expression> visitDelegate = new Function1<TypeDelegate,Expression>(){
            public Expression invoke(TypeDelegate mt) {
                Ref<TypeDelegate> mt_ref = ref(mt);
                if ((pequals(ident_ref.value, Id.ptr.value)))
                {
                    e_ref.value = new DelegatePtrExp(e_ref.value.loc, e_ref.value);
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                }
                else if ((pequals(ident_ref.value, Id.funcptr.value)))
                {
                    if (((flag_ref.value & DotExpFlag.noDeref) == 0) && ((sc_ref.value.get()).func != null) && ((sc_ref.value.get()).intypeof == 0) && (sc_ref.value.get()).func.setUnsafe() && (((sc_ref.value.get()).flags & 8) == 0))
                    {
                        e_ref.value.error(new BytePtr("`%s.funcptr` cannot be used in `@safe` code"), e_ref.value.toChars());
                        return new ErrorExp();
                    }
                    e_ref.value = new DelegateFuncptrExp(e_ref.value.loc, e_ref.value);
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
                    error(e_ref.value.loc, new BytePtr("cannot resolve identifier `%s`"), ident_ref.value.toChars());
                    return returnExp.invoke(gagError.value ? null : new ErrorExp());
                }
                assert(((mt_ref.value.ty & 0xFF) == ENUMTY.Tstruct) || ((mt_ref.value.ty & 0xFF) == ENUMTY.Tclass));
                Ref<AggregateDeclaration> sym = ref(mt_ref.value.toDsymbol(sc_ref.value).isAggregateDeclaration());
                assert(sym.value != null);
                if ((!pequals(ident_ref.value, Id.__sizeof.value)) && (!pequals(ident_ref.value, Id.__xalignof.value)) && (!pequals(ident_ref.value, Id._init.value)) && (!pequals(ident_ref.value, Id._mangleof.value)) && (!pequals(ident_ref.value, Id.stringof.value)) && (!pequals(ident_ref.value, Id.offsetof.value)) && (!pequals(ident_ref.value, Id.ctor.value)) && (!pequals(ident_ref.value, Id.dtor.value)) && (!pequals(ident_ref.value, Id.__xdtor.value)) && (!pequals(ident_ref.value, Id.postblit.value)) && (!pequals(ident_ref.value, Id.__xpostblit.value)))
                {
                    {
                        Ref<Dsymbol> fd = ref(search_function(sym.value, Id.opDot.value));
                        if ((fd.value) != null)
                        {
                            e_ref.value = build_overload(e_ref.value.loc, sc_ref.value, e_ref.value, null, fd.value);
                            e_ref.value.deprecation(new BytePtr("`opDot` is deprecated. Use `alias this`"));
                            e_ref.value = new DotIdExp(e_ref.value.loc, e_ref.value, ident_ref.value);
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
                            Ref<StringExp> se = ref(new StringExp(e_ref.value.loc, ident_ref.value.toChars()));
                            Ref<Ptr<DArray<RootObject>>> tiargs = ref(new DArray<RootObject>());
                            (tiargs.value.get()).push(se.value);
                            Ref<DotTemplateInstanceExp> dti = ref(new DotTemplateInstanceExp(e_ref.value.loc, e_ref.value, Id.opDispatch.value, tiargs.value));
                            dti.value.ti.tempdecl = td.value;
                            IntRef errors = ref(gagError.value ? global.value.startGagging() : 0);
                            e_ref.value = semanticY(dti.value, sc_ref.value, 0);
                            if (gagError.value && global.value.endGagging(errors.value))
                                e_ref.value = null;
                            return returnExp.invoke(e_ref.value);
                        }
                    }
                    Ref<Expression> alias_e = ref(resolveAliasThis(sc_ref.value, e_ref.value, gagError.value));
                    if ((alias_e.value != null) && (!pequals(alias_e.value, e_ref.value)))
                    {
                        Ref<DotIdExp> die = ref(new DotIdExp(e_ref.value.loc, alias_e.value, ident_ref.value));
                        IntRef errors = ref(gagError.value ? 0 : global.value.startGagging());
                        Ref<Expression> exp = ref(semanticY(die.value, sc_ref.value, (gagError.value ? 1 : 0)));
                        if (!gagError.value)
                        {
                            global.value.endGagging(errors.value);
                            if ((exp.value != null) && ((exp.value.op & 0xFF) == 127))
                                exp.value = null;
                        }
                        if ((exp.value != null) && gagError.value)
                            resolveAliasThis(sc_ref.value, e_ref.value, false);
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
                assert(((e_ref.value.op & 0xFF) != 97));
                if ((pequals(ident_ref.value, Id._mangleof.value)))
                {
                    return getProperty(mt_ref.value, e_ref.value.loc, ident_ref.value, flag_ref.value & 1);
                }
                if ((pequals(ident_ref.value, Id._tupleof.value)))
                {
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                    if (!mt_ref.value.sym.determineFields())
                    {
                        error(e_ref.value.loc, new BytePtr("unable to determine fields of `%s` because of forward references"), mt_ref.value.toChars());
                    }
                    Ref<Expression> e0 = ref(null);
                    Ref<Expression> ev = ref(((e_ref.value.op & 0xFF) == 20) ? null : e_ref.value);
                    if (ev.value != null)
                        ev.value = extractSideEffect(sc_ref.value, new BytePtr("__tup"), e0, ev.value, false);
                    Ref<Ptr<DArray<Expression>>> exps = ref(new DArray<Expression>());
                    (exps.value.get()).reserve(mt_ref.value.sym.fields.length);
                    {
                        IntRef i = ref(0);
                        for (; (i.value < mt_ref.value.sym.fields.length);i.value++){
                            Ref<VarDeclaration> v = ref(mt_ref.value.sym.fields.get(i.value));
                            Ref<Expression> ex = ref(null);
                            if (ev.value != null)
                                ex.value = new DotVarExp(e_ref.value.loc, ev.value, v.value, true);
                            else
                            {
                                ex.value = new VarExp(e_ref.value.loc, v.value, true);
                                ex.value.type.value = ex.value.type.value.addMod(e_ref.value.type.value.mod);
                            }
                            (exps.value.get()).push(ex.value);
                        }
                    }
                    e_ref.value = new TupleExp(e_ref.value.loc, e0.value, exps.value);
                    Ref<Ptr<Scope>> sc2 = ref((sc_ref.value.get()).push());
                    (sc2.value.get()).flags |= global.value.params.vsafe ? 1024 : 2;
                    e_ref.value = expressionSemantic(e_ref.value, sc2.value);
                    (sc2.value.get()).pop();
                    return e_ref.value;
                }
                IntRef flags = ref(((sc_ref.value.get()).flags & 512) != 0 ? 128 : 0);
                s.value = mt_ref.value.sym.search(e_ref.value.loc, ident_ref.value, flags.value | 1);
                while(true) try {
                /*L1:*/
                    if (s.value == null)
                    {
                        return noMember.invoke(mt_ref.value, sc_ref.value, e_ref.value, ident_ref.value, flag_ref.value);
                    }
                    if ((((sc_ref.value.get()).flags & 512) == 0) && !symbolIsVisible(sc_ref.value, s.value))
                    {
                        return noMember.invoke(mt_ref.value, sc_ref.value, e_ref.value, ident_ref.value, flag_ref.value);
                    }
                    if (s.value.isFuncDeclaration() == null)
                    {
                        s.value.checkDeprecated(e_ref.value.loc, sc_ref.value);
                        {
                            Ref<Declaration> d = ref(s.value.isDeclaration());
                            if ((d.value) != null)
                                d.value.checkDisabled(e_ref.value.loc, sc_ref.value, false);
                        }
                    }
                    s.value = s.value.toAlias();
                    {
                        Ref<EnumMember> em = ref(s.value.isEnumMember());
                        if ((em.value) != null)
                        {
                            return em.value.getVarExp(e_ref.value.loc, sc_ref.value);
                        }
                    }
                    {
                        Ref<VarDeclaration> v = ref(s.value.isVarDeclaration());
                        if ((v.value) != null)
                        {
                            if ((v.value.type == null) || (v.value.type.deco == null) && (v.value.inuse != 0))
                            {
                                if (v.value.inuse != 0)
                                    e_ref.value.error(new BytePtr("circular reference to %s `%s`"), v.value.kind(), v.value.toPrettyChars(false));
                                else
                                    e_ref.value.error(new BytePtr("forward reference to %s `%s`"), v.value.kind(), v.value.toPrettyChars(false));
                                return new ErrorExp();
                            }
                            if (((v.value.type.ty & 0xFF) == ENUMTY.Terror))
                            {
                                return new ErrorExp();
                            }
                            if (((v.value.storage_class & 8388608L) != 0) && (v.value._init != null))
                            {
                                if (v.value.inuse != 0)
                                {
                                    e_ref.value.error(new BytePtr("circular initialization of %s `%s`"), v.value.kind(), v.value.toPrettyChars(false));
                                    return new ErrorExp();
                                }
                                checkAccess(e_ref.value.loc, sc_ref.value, null, (Declaration)v);
                                Ref<Expression> ve = ref(new VarExp(e_ref.value.loc, v.value, true));
                                if (!isTrivialExp(e_ref.value))
                                {
                                    ve.value = new CommaExp(e_ref.value.loc, e_ref.value, ve.value, true);
                                }
                                return expressionSemantic(ve.value, sc_ref.value);
                            }
                        }
                    }
                    {
                        Ref<Type> t = ref(s.value.getType());
                        if ((t.value) != null)
                        {
                            return expressionSemantic(new TypeExp(e_ref.value.loc, t.value), sc_ref.value);
                        }
                    }
                    Ref<TemplateMixin> tm = ref(s.value.isTemplateMixin());
                    if (tm.value != null)
                    {
                        Ref<Expression> de = ref(new DotExp(e_ref.value.loc, e_ref.value, new ScopeExp(e_ref.value.loc, tm.value)));
                        de.value.type.value = e_ref.value.type.value;
                        return de.value;
                    }
                    Ref<TemplateDeclaration> td = ref(s.value.isTemplateDeclaration());
                    if (td.value != null)
                    {
                        if (((e_ref.value.op & 0xFF) == 20))
                            e_ref.value = new TemplateExp(e_ref.value.loc, td.value, null);
                        else
                            e_ref.value = new DotTemplateExp(e_ref.value.loc, e_ref.value, td.value);
                        return expressionSemantic(e_ref.value, sc_ref.value);
                    }
                    Ref<TemplateInstance> ti = ref(s.value.isTemplateInstance());
                    if (ti.value != null)
                    {
                        if (ti.value.semanticRun == 0)
                        {
                            dsymbolSemantic(ti.value, sc_ref.value);
                            if ((ti.value.inst == null) || ti.value.errors)
                            {
                                return new ErrorExp();
                            }
                        }
                        s.value = ti.value.inst.toAlias();
                        if (s.value.isTemplateInstance() == null)
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        if (((e_ref.value.op & 0xFF) == 20))
                            e_ref.value = new ScopeExp(e_ref.value.loc, ti.value);
                        else
                            e_ref.value = new DotExp(e_ref.value.loc, e_ref.value, new ScopeExp(e_ref.value.loc, ti.value));
                        return expressionSemantic(e_ref.value, sc_ref.value);
                    }
                    if ((s.value.isImport() != null) || (s.value.isModule() != null) || (s.value.isPackage() != null))
                    {
                        return symbolToExp(s.value, e_ref.value.loc, sc_ref.value, false);
                    }
                    Ref<OverloadSet> o = ref(s.value.isOverloadSet());
                    if (o.value != null)
                    {
                        Ref<OverExp> oe = ref(new OverExp(e_ref.value.loc, o.value));
                        if (((e_ref.value.op & 0xFF) == 20))
                        {
                            return oe.value;
                        }
                        return new DotExp(e_ref.value.loc, e_ref.value, oe.value);
                    }
                    Ref<Declaration> d = ref(s.value.isDeclaration());
                    if (d.value == null)
                    {
                        e_ref.value.error(new BytePtr("`%s.%s` is not a declaration"), e_ref.value.toChars(), ident_ref.value.toChars());
                        return new ErrorExp();
                    }
                    if (((e_ref.value.op & 0xFF) == 20))
                    {
                        {
                            Ref<TupleDeclaration> tup = ref(d.value.isTupleDeclaration());
                            if ((tup.value) != null)
                            {
                                e_ref.value = new TupleExp(e_ref.value.loc, tup.value);
                                return expressionSemantic(e_ref.value, sc_ref.value);
                            }
                        }
                        if (d.value.needThis() && ((sc_ref.value.get()).intypeof != 1))
                        {
                            if (hasThis(sc_ref.value) != null)
                            {
                                e_ref.value = new DotVarExp(e_ref.value.loc, new ThisExp(e_ref.value.loc), d.value, true);
                                return expressionSemantic(e_ref.value, sc_ref.value);
                            }
                        }
                        if ((d.value.semanticRun == PASS.init))
                            dsymbolSemantic(d.value, null);
                        checkAccess(e_ref.value.loc, sc_ref.value, e_ref.value, d.value);
                        Ref<VarExp> ve = ref(new VarExp(e_ref.value.loc, d.value, true));
                        if ((d.value.isVarDeclaration() != null) && d.value.needThis())
                            ve.value.type.value = d.value.type.addMod(e_ref.value.type.value.mod);
                        return ve.value;
                    }
                    Ref<Boolean> unreal = ref(((e_ref.value.op & 0xFF) == 26) && ((VarExp)e_ref.value).var.isField());
                    if (d.value.isDataseg() || unreal.value && d.value.isField())
                    {
                        checkAccess(e_ref.value.loc, sc_ref.value, e_ref.value, d.value);
                        Ref<Expression> ve = ref(new VarExp(e_ref.value.loc, d.value, true));
                        e_ref.value = unreal.value ? ve.value : new CommaExp(e_ref.value.loc, e_ref.value, ve.value, true);
                        return expressionSemantic(e_ref.value, sc_ref.value);
                    }
                    e_ref.value = new DotVarExp(e_ref.value.loc, e_ref.value, d.value, true);
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
                    return getProperty(mt_ref.value, e_ref.value.loc, ident_ref.value, flag_ref.value & 1);
                }
                if ((mt_ref.value.sym.semanticRun < PASS.semanticdone))
                    dsymbolSemantic(mt_ref.value.sym, null);
                if (mt_ref.value.sym.members == null)
                {
                    if (mt_ref.value.sym.isSpecial())
                    {
                        e_ref.value = dotExp(mt_ref.value.sym.memtype, sc_ref.value, e_ref.value, ident_ref.value, flag_ref.value);
                    }
                    else if ((flag_ref.value & 1) == 0)
                    {
                        mt_ref.value.sym.error(new BytePtr("is forward referenced when looking for `%s`"), ident_ref.value.toChars());
                        e_ref.value = new ErrorExp();
                    }
                    else
                        e_ref.value = null;
                    return e_ref.value;
                }
                Ref<Dsymbol> s = ref(mt_ref.value.sym.search(e_ref.value.loc, ident_ref.value, 8));
                if (s.value == null)
                {
                    if ((pequals(ident_ref.value, Id.max.value)) || (pequals(ident_ref.value, Id.min.value)) || (pequals(ident_ref.value, Id._init.value)))
                    {
                        return getProperty(mt_ref.value, e_ref.value.loc, ident_ref.value, flag_ref.value & 1);
                    }
                    Ref<Expression> res = ref(dotExp(mt_ref.value.sym.getMemtype(Loc.initial.value), sc_ref.value, e_ref.value, ident_ref.value, 1));
                    if (((flag_ref.value & 1) == 0) && (res.value == null))
                    {
                        {
                            Ref<Dsymbol> ns = ref(mt_ref.value.sym.search_correct(ident_ref.value));
                            if ((ns.value) != null)
                                e_ref.value.error(new BytePtr("no property `%s` for type `%s`. Did you mean `%s.%s` ?"), ident_ref.value.toChars(), mt_ref.value.toChars(), mt_ref.value.toChars(), ns.value.toChars());
                            else
                                e_ref.value.error(new BytePtr("no property `%s` for type `%s`"), ident_ref.value.toChars(), mt_ref.value.toChars());
                        }
                        return new ErrorExp();
                    }
                    return res.value;
                }
                Ref<EnumMember> m = ref(s.value.isEnumMember());
                return m.value.getVarExp(e_ref.value.loc, sc_ref.value);
            }
        };
        Function1<TypeClass,Expression> visitClass = new Function1<TypeClass,Expression>(){
            public Expression invoke(TypeClass mt) {
                Ref<TypeClass> mt_ref = ref(mt);
                Ref<Dsymbol> s = ref(null);
                assert(((e_ref.value.op & 0xFF) != 97));
                if ((pequals(ident_ref.value, Id.__sizeof.value)) || (pequals(ident_ref.value, Id.__xalignof.value)) || (pequals(ident_ref.value, Id._mangleof.value)))
                {
                    return getProperty(mt_ref.value.Type, e_ref.value.loc, ident_ref.value, 0);
                }
                if ((pequals(ident_ref.value, Id._tupleof.value)))
                {
                    objc().checkTupleof(e_ref.value, mt_ref.value);
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                    mt_ref.value.sym.size(e_ref.value.loc);
                    Ref<Expression> e0 = ref(null);
                    Ref<Expression> ev = ref(((e_ref.value.op & 0xFF) == 20) ? null : e_ref.value);
                    if (ev.value != null)
                        ev.value = extractSideEffect(sc_ref.value, new BytePtr("__tup"), e0, ev.value, false);
                    Ref<Ptr<DArray<Expression>>> exps = ref(new DArray<Expression>());
                    (exps.value.get()).reserve(mt_ref.value.sym.fields.length);
                    {
                        IntRef i = ref(0);
                        for (; (i.value < mt_ref.value.sym.fields.length);i.value++){
                            Ref<VarDeclaration> v = ref(mt_ref.value.sym.fields.get(i.value));
                            if (v.value.isThisDeclaration() != null)
                                continue;
                            Ref<Expression> ex = ref(null);
                            if (ev.value != null)
                                ex.value = new DotVarExp(e_ref.value.loc, ev.value, v.value, true);
                            else
                            {
                                ex.value = new VarExp(e_ref.value.loc, v.value, true);
                                ex.value.type.value = ex.value.type.value.addMod(e_ref.value.type.value.mod);
                            }
                            (exps.value.get()).push(ex.value);
                        }
                    }
                    e_ref.value = new TupleExp(e_ref.value.loc, e0.value, exps.value);
                    Ref<Ptr<Scope>> sc2 = ref((sc_ref.value.get()).push());
                    (sc2.value.get()).flags |= global.value.params.vsafe ? 1024 : 2;
                    e_ref.value = expressionSemantic(e_ref.value, sc2.value);
                    (sc2.value.get()).pop();
                    return e_ref.value;
                }
                IntRef flags = ref(((sc_ref.value.get()).flags & 512) != 0 ? 128 : 0);
                s.value = mt_ref.value.sym.search(e_ref.value.loc, ident_ref.value, flags.value | 1);
                while(true) try {
                /*L1:*/
                    if (s.value == null)
                    {
                        if ((pequals(mt_ref.value.sym.ident, ident_ref.value)))
                        {
                            if (((e_ref.value.op & 0xFF) == 20))
                            {
                                return getProperty(mt_ref.value.Type, e_ref.value.loc, ident_ref.value, 0);
                            }
                            e_ref.value = new DotTypeExp(e_ref.value.loc, e_ref.value, mt_ref.value.sym);
                            e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                            return e_ref.value;
                        }
                        {
                            Ref<ClassDeclaration> cbase = ref(mt_ref.value.sym.searchBase(ident_ref.value));
                            if ((cbase.value) != null)
                            {
                                if (((e_ref.value.op & 0xFF) == 20))
                                {
                                    return getProperty(mt_ref.value.Type, e_ref.value.loc, ident_ref.value, 0);
                                }
                                {
                                    Ref<InterfaceDeclaration> ifbase = ref(cbase.value.isInterfaceDeclaration());
                                    if ((ifbase.value) != null)
                                        e_ref.value = new CastExp(e_ref.value.loc, e_ref.value, ifbase.value.type);
                                    else
                                        e_ref.value = new DotTypeExp(e_ref.value.loc, e_ref.value, cbase.value);
                                }
                                e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                                return e_ref.value;
                            }
                        }
                        if ((pequals(ident_ref.value, Id.classinfo.value)))
                        {
                            if (Type.typeinfoclass.value == null)
                            {
                                error(e_ref.value.loc, new BytePtr("`object.TypeInfo_Class` could not be found, but is implicitly used"));
                                return new ErrorExp();
                            }
                            Ref<Type> t = ref(Type.typeinfoclass.value.type);
                            if (((e_ref.value.op & 0xFF) == 20) || ((e_ref.value.op & 0xFF) == 30))
                            {
                                if (mt_ref.value.sym.vclassinfo == null)
                                    mt_ref.value.sym.vclassinfo = new TypeInfoClassDeclaration(mt_ref.value.sym.type);
                                e_ref.value = new VarExp(e_ref.value.loc, mt_ref.value.sym.vclassinfo, true);
                                e_ref.value = e_ref.value.addressOf();
                                e_ref.value.type.value = t.value;
                            }
                            else
                            {
                                e_ref.value = new PtrExp(e_ref.value.loc, e_ref.value);
                                e_ref.value.type.value = t.value.pointerTo();
                                if (mt_ref.value.sym.isInterfaceDeclaration() != null)
                                {
                                    if (mt_ref.value.sym.isCPPinterface())
                                    {
                                        error(e_ref.value.loc, new BytePtr("no `.classinfo` for C++ interface objects"));
                                    }
                                    e_ref.value.type.value = e_ref.value.type.value.pointerTo();
                                    e_ref.value = new PtrExp(e_ref.value.loc, e_ref.value);
                                    e_ref.value.type.value = t.value.pointerTo();
                                }
                                e_ref.value = new PtrExp(e_ref.value.loc, e_ref.value, t.value);
                            }
                            return e_ref.value;
                        }
                        if ((pequals(ident_ref.value, Id.__vptr.value)))
                        {
                            e_ref.value = e_ref.value.castTo(sc_ref.value, Type.tvoidptr.value.immutableOf().pointerTo().pointerTo());
                            e_ref.value = new PtrExp(e_ref.value.loc, e_ref.value);
                            e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                            return e_ref.value;
                        }
                        if ((pequals(ident_ref.value, Id.__monitor.value)) && mt_ref.value.sym.hasMonitor())
                        {
                            e_ref.value = e_ref.value.castTo(sc_ref.value, Type.tvoidptr.value.pointerTo());
                            e_ref.value = new AddExp(e_ref.value.loc, e_ref.value, literal_356A192B7913B04C());
                            e_ref.value = new PtrExp(e_ref.value.loc, e_ref.value);
                            e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                            return e_ref.value;
                        }
                        if ((pequals(ident_ref.value, Id.outer.value)) && (mt_ref.value.sym.vthis != null))
                        {
                            if ((mt_ref.value.sym.vthis.semanticRun == PASS.init))
                                dsymbolSemantic(mt_ref.value.sym.vthis, null);
                            {
                                Ref<ClassDeclaration> cdp = ref(mt_ref.value.sym.toParentLocal().isClassDeclaration());
                                if ((cdp.value) != null)
                                {
                                    Ref<DotVarExp> dve = ref(new DotVarExp(e_ref.value.loc, e_ref.value, mt_ref.value.sym.vthis, true));
                                    dve.value.type.value = cdp.value.type.addMod(e_ref.value.type.value.mod);
                                    return dve.value;
                                }
                            }
                            {
                                Ref<Dsymbol> p = ref(mt_ref.value.sym.toParentLocal());
                                for (; p.value != null;p.value = p.value.toParentLocal()){
                                    Ref<FuncDeclaration> fd = ref(p.value.isFuncDeclaration());
                                    if (fd.value == null)
                                        break;
                                    Ref<AggregateDeclaration> ad = ref(fd.value.isThis());
                                    if ((ad.value == null) && fd.value.isNested())
                                        continue;
                                    if (ad.value == null)
                                        break;
                                    {
                                        Ref<ClassDeclaration> cdp = ref(ad.value.isClassDeclaration());
                                        if ((cdp.value) != null)
                                        {
                                            Ref<ThisExp> ve = ref(new ThisExp(e_ref.value.loc));
                                            ve.value.var = fd.value.vthis;
                                            boolean nestedError = fd.value.vthis.checkNestedReference(sc_ref.value, e_ref.value.loc);
                                            assert(!nestedError);
                                            ve.value.type.value = cdp.value.type.addMod(fd.value.vthis.type.mod).addMod(e_ref.value.type.value.mod);
                                            return ve.value;
                                        }
                                    }
                                    break;
                                }
                            }
                            Ref<DotVarExp> dve = ref(new DotVarExp(e_ref.value.loc, e_ref.value, mt_ref.value.sym.vthis, true));
                            dve.value.type.value = mt_ref.value.sym.vthis.type.addMod(e_ref.value.type.value.mod);
                            return dve.value;
                        }
                        return noMember.invoke(mt_ref.value, sc_ref.value, e_ref.value, ident_ref.value, flag_ref.value & 1);
                    }
                    if ((((sc_ref.value.get()).flags & 512) == 0) && !symbolIsVisible(sc_ref.value, s.value))
                    {
                        return noMember.invoke(mt_ref.value, sc_ref.value, e_ref.value, ident_ref.value, flag_ref.value);
                    }
                    if (s.value.isFuncDeclaration() == null)
                    {
                        s.value.checkDeprecated(e_ref.value.loc, sc_ref.value);
                        {
                            Ref<Declaration> d = ref(s.value.isDeclaration());
                            if ((d.value) != null)
                                d.value.checkDisabled(e_ref.value.loc, sc_ref.value, false);
                        }
                    }
                    s.value = s.value.toAlias();
                    {
                        Ref<EnumMember> em = ref(s.value.isEnumMember());
                        if ((em.value) != null)
                        {
                            return em.value.getVarExp(e_ref.value.loc, sc_ref.value);
                        }
                    }
                    {
                        Ref<VarDeclaration> v = ref(s.value.isVarDeclaration());
                        if ((v.value) != null)
                        {
                            if ((v.value.type == null) || (v.value.type.deco == null) && (v.value.inuse != 0))
                            {
                                if (v.value.inuse != 0)
                                    e_ref.value.error(new BytePtr("circular reference to %s `%s`"), v.value.kind(), v.value.toPrettyChars(false));
                                else
                                    e_ref.value.error(new BytePtr("forward reference to %s `%s`"), v.value.kind(), v.value.toPrettyChars(false));
                                return new ErrorExp();
                            }
                            if (((v.value.type.ty & 0xFF) == ENUMTY.Terror))
                            {
                                return new ErrorExp();
                            }
                            if (((v.value.storage_class & 8388608L) != 0) && (v.value._init != null))
                            {
                                if (v.value.inuse != 0)
                                {
                                    e_ref.value.error(new BytePtr("circular initialization of %s `%s`"), v.value.kind(), v.value.toPrettyChars(false));
                                    return new ErrorExp();
                                }
                                checkAccess(e_ref.value.loc, sc_ref.value, null, (Declaration)v);
                                Ref<Expression> ve = ref(new VarExp(e_ref.value.loc, v.value, true));
                                ve.value = expressionSemantic(ve.value, sc_ref.value);
                                return ve.value;
                            }
                        }
                    }
                    {
                        Ref<Type> t = ref(s.value.getType());
                        if ((t.value) != null)
                        {
                            return expressionSemantic(new TypeExp(e_ref.value.loc, t.value), sc_ref.value);
                        }
                    }
                    Ref<TemplateMixin> tm = ref(s.value.isTemplateMixin());
                    if (tm.value != null)
                    {
                        Ref<Expression> de = ref(new DotExp(e_ref.value.loc, e_ref.value, new ScopeExp(e_ref.value.loc, tm.value)));
                        de.value.type.value = e_ref.value.type.value;
                        return de.value;
                    }
                    Ref<TemplateDeclaration> td = ref(s.value.isTemplateDeclaration());
                    if (td.value != null)
                    {
                        if (((e_ref.value.op & 0xFF) == 20))
                            e_ref.value = new TemplateExp(e_ref.value.loc, td.value, null);
                        else
                            e_ref.value = new DotTemplateExp(e_ref.value.loc, e_ref.value, td.value);
                        e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                        return e_ref.value;
                    }
                    Ref<TemplateInstance> ti = ref(s.value.isTemplateInstance());
                    if (ti.value != null)
                    {
                        if (ti.value.semanticRun == 0)
                        {
                            dsymbolSemantic(ti.value, sc_ref.value);
                            if ((ti.value.inst == null) || ti.value.errors)
                            {
                                return new ErrorExp();
                            }
                        }
                        s.value = ti.value.inst.toAlias();
                        if (s.value.isTemplateInstance() == null)
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        if (((e_ref.value.op & 0xFF) == 20))
                            e_ref.value = new ScopeExp(e_ref.value.loc, ti.value);
                        else
                            e_ref.value = new DotExp(e_ref.value.loc, e_ref.value, new ScopeExp(e_ref.value.loc, ti.value));
                        return expressionSemantic(e_ref.value, sc_ref.value);
                    }
                    if ((s.value.isImport() != null) || (s.value.isModule() != null) || (s.value.isPackage() != null))
                    {
                        e_ref.value = symbolToExp(s.value, e_ref.value.loc, sc_ref.value, false);
                        return e_ref.value;
                    }
                    Ref<OverloadSet> o = ref(s.value.isOverloadSet());
                    if (o.value != null)
                    {
                        Ref<OverExp> oe = ref(new OverExp(e_ref.value.loc, o.value));
                        if (((e_ref.value.op & 0xFF) == 20))
                        {
                            return oe.value;
                        }
                        return new DotExp(e_ref.value.loc, e_ref.value, oe.value);
                    }
                    Ref<Declaration> d = ref(s.value.isDeclaration());
                    if (d.value == null)
                    {
                        e_ref.value.error(new BytePtr("`%s.%s` is not a declaration"), e_ref.value.toChars(), ident_ref.value.toChars());
                        return new ErrorExp();
                    }
                    if (((e_ref.value.op & 0xFF) == 20))
                    {
                        {
                            Ref<TupleDeclaration> tup = ref(d.value.isTupleDeclaration());
                            if ((tup.value) != null)
                            {
                                e_ref.value = new TupleExp(e_ref.value.loc, tup.value);
                                e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                                return e_ref.value;
                            }
                        }
                        if ((mt_ref.value.sym.classKind == ClassKind.objc) && (d.value.isFuncDeclaration() != null) && d.value.isFuncDeclaration().isStatic() && (d.value.isFuncDeclaration().selector != null))
                        {
                            Ref<ObjcClassReferenceExp> classRef = ref(new ObjcClassReferenceExp(e_ref.value.loc, mt_ref.value.sym));
                            return expressionSemantic(new DotVarExp(e_ref.value.loc, classRef.value, d.value, true), sc_ref.value);
                        }
                        else if (d.value.needThis() && ((sc_ref.value.get()).intypeof != 1))
                        {
                            Ref<AggregateDeclaration> ad = ref(d.value.isMemberLocal());
                            {
                                Ref<FuncDeclaration> f = ref(hasThis(sc_ref.value));
                                if ((f.value) != null)
                                {
                                    Ref<Expression> e1 = ref(null);
                                    Ref<Type> t = ref(null);
                                    try {
                                        if (f.value.isThis2)
                                        {
                                            if (followInstantiationContextAggregateDeclaration(f.value, ad.value))
                                            {
                                                e1.value = new VarExp(e_ref.value.loc, f.value.vthis, true);
                                                e1.value = new PtrExp(e1.value.loc, e1.value);
                                                e1.value = new IndexExp(e1.value.loc, e1.value, literal_356A192B7913B04C());
                                                Ref<Declaration> pd = ref(f.value.toParent2().isDeclaration());
                                                assert(pd.value != null);
                                                t.value = pd.value.type.toBasetype();
                                                e1.value = getThisSkipNestedFuncs(e1.value.loc, sc_ref.value, f.value.toParent2(), ad.value, e1.value, t.value, d.value, true);
                                                if (e1.value == null)
                                                {
                                                    e_ref.value = new VarExp(e_ref.value.loc, d.value, true);
                                                    return e_ref.value;
                                                }
                                                /*goto L2*/throw Dispatch0.INSTANCE;
                                            }
                                        }
                                        e1.value = new ThisExp(e_ref.value.loc);
                                        e1.value = expressionSemantic(e1.value, sc_ref.value);
                                    }
                                    catch(Dispatch0 __d){}
                                /*L2:*/
                                    t.value = e1.value.type.value.toBasetype();
                                    Ref<ClassDeclaration> cd = ref(e_ref.value.type.value.isClassHandle());
                                    Ref<ClassDeclaration> tcd = ref(t.value.isClassHandle());
                                    if ((cd.value != null) && (tcd.value != null) && (pequals(tcd.value, cd.value)) || cd.value.isBaseOf(tcd.value, null))
                                    {
                                        e_ref.value = new DotTypeExp(e1.value.loc, e1.value, cd.value);
                                        e_ref.value = new DotVarExp(e_ref.value.loc, e_ref.value, d.value, true);
                                        e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                                        return e_ref.value;
                                    }
                                    if ((tcd.value != null) && tcd.value.isNested())
                                    {
                                        Ref<VarDeclaration> vthis = ref(followInstantiationContextAggregateDeclaration(tcd.value, ad.value) ? tcd.value.vthis2 : tcd.value.vthis);
                                        e1.value = new DotVarExp(e_ref.value.loc, e1.value, vthis.value, true);
                                        e1.value.type.value = vthis.value.type;
                                        e1.value.type.value = e1.value.type.value.addMod(t.value.mod);
                                        e1.value = getThisSkipNestedFuncs(e1.value.loc, sc_ref.value, toParentPAggregateDeclaration(tcd.value, ad.value), ad.value, e1.value, t.value, d.value, true);
                                        if (e1.value == null)
                                        {
                                            e_ref.value = new VarExp(e_ref.value.loc, d.value, true);
                                            return e_ref.value;
                                        }
                                        /*goto L2*/throw Dispatch0.INSTANCE;
                                    }
                                }
                            }
                        }
                        if ((d.value.semanticRun == PASS.init))
                            dsymbolSemantic(d.value, null);
                        {
                            Ref<FuncDeclaration> fd = ref(d.value.isFuncDeclaration());
                            if ((fd.value) != null)
                            {
                                d.value = (Declaration)mostVisibleOverload(fd.value, (sc_ref.value.get())._module);
                            }
                        }
                        checkAccess(e_ref.value.loc, sc_ref.value, e_ref.value, d.value);
                        Ref<VarExp> ve = ref(new VarExp(e_ref.value.loc, d.value, true));
                        if ((d.value.isVarDeclaration() != null) && d.value.needThis())
                            ve.value.type.value = d.value.type.addMod(e_ref.value.type.value.mod);
                        return ve.value;
                    }
                    Ref<Boolean> unreal = ref(((e_ref.value.op & 0xFF) == 26) && ((VarExp)e_ref.value).var.isField());
                    if (d.value.isDataseg() || unreal.value && d.value.isField())
                    {
                        checkAccess(e_ref.value.loc, sc_ref.value, e_ref.value, d.value);
                        Ref<Expression> ve = ref(new VarExp(e_ref.value.loc, d.value, true));
                        e_ref.value = unreal.value ? ve.value : new CommaExp(e_ref.value.loc, e_ref.value, ve.value, true);
                        e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                        return e_ref.value;
                    }
                    e_ref.value = new DotVarExp(e_ref.value.loc, e_ref.value, d.value, true);
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                    return e_ref.value;
                    break;
                } catch(Dispatch0 __d){}
            }
        };
        switch ((mt.ty & 0xFF))
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
                switch ((mt_ref.value.ty & 0xFF))
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
                        return new RealExp(loc_ref.value, target.value.RealProperties.nan, mt_ref.value);
                    case 27:
                    case 28:
                    case 29:
                        Ref<complex_t> cvalue = ref(cvalue.value = new complex_t(target.value.RealProperties.nan, target.value.RealProperties.nan));
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
                assert(((mt_ref.value.basetype.ty & 0xFF) == ENUMTY.Tsarray));
                Ref<Expression> e = ref(defaultInit(mt_ref.value.basetype, loc_ref.value));
                Ref<VectorExp> ve = ref(new VectorExp(loc_ref.value, e.value, mt_ref.value));
                ve.value.type.value = mt_ref.value;
                ve.value.dim = (int)(mt_ref.value.basetype.size(loc_ref.value) / mt_ref.value.elementType().size(loc_ref.value));
                return ve.value;
            }
        };
        Function1<TypeSArray,Expression> visitSArray = new Function1<TypeSArray,Expression>(){
            public Expression invoke(TypeSArray mt) {
                Ref<TypeSArray> mt_ref = ref(mt);
                if (((mt_ref.value.next.ty & 0xFF) == ENUMTY.Tvoid))
                    return defaultInit(Type.tuns8.value, loc_ref.value);
                else
                    return defaultInit(mt_ref.value.next, loc_ref.value);
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
                Ref<Declaration> d = ref(new SymbolDeclaration(mt_ref.value.sym.loc, mt_ref.value.sym));
                assert(d.value != null);
                d.value.type = mt_ref.value;
                d.value.storage_class |= 2199023255552L;
                return new VarExp(mt_ref.value.sym.loc, d.value, true);
            }
        };
        Function1<TypeEnum,Expression> visitEnum = new Function1<TypeEnum,Expression>(){
            public Expression invoke(TypeEnum mt) {
                Ref<TypeEnum> mt_ref = ref(mt);
                Ref<Expression> e = ref(mt_ref.value.sym.getDefaultValue(loc_ref.value));
                e.value = e.value.copy();
                e.value.loc = loc_ref.value.copy();
                e.value.type.value = mt_ref.value;
                return e.value;
            }
        };
        Function1<TypeTuple,Expression> visitTuple = new Function1<TypeTuple,Expression>(){
            public Expression invoke(TypeTuple mt) {
                Ref<TypeTuple> mt_ref = ref(mt);
                Ref<Ptr<DArray<Expression>>> exps = ref(new DArray<Expression>((mt_ref.value.arguments.get()).length));
                {
                    IntRef i = ref(0);
                    for (; (i.value < (mt_ref.value.arguments.get()).length);i.value++){
                        Ref<Parameter> p = ref((mt_ref.value.arguments.get()).get(i.value));
                        assert(p.value.type != null);
                        Ref<Expression> e = ref(p.value.type.defaultInitLiteral(loc_ref.value));
                        if (((e.value.op & 0xFF) == 127))
                        {
                            return e.value;
                        }
                        exps.value.get().set(i.value, e.value);
                    }
                }
                return new TupleExp(loc_ref.value, exps.value);
            }
        };
        switch ((mt.ty & 0xFF))
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
