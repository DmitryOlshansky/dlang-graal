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
    static FuncDeclaration visitAArrayfeq = null;
    static FuncDeclaration visitAArrayfcmp = null;
    static FuncDeclaration visitAArrayfhash = null;
    static Slice<BytePtr> visitTraitsctxt = slice(initializer_0);
    static FuncDeclaration visitAArrayfd_aaLen = null;
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
            pt.set(0, Type.terror);
            return ;
        }
        eindex.value = semanticLength(sc, tup, eindex.value);
        eindex.value = eindex.value.ctfeInterpret();
        if (((eindex.value.op & 0xFF) == 127))
        {
            pt.set(0, Type.terror);
            return ;
        }
        long d = eindex.value.toUInteger();
        if ((d >= (long)(tup.objects.get()).length))
        {
            error(loc, new BytePtr("tuple index `%llu` exceeds length %u"), d, (tup.objects.get()).length);
            pt.set(0, Type.terror);
            return ;
        }
        RootObject o = (tup.objects.get()).get((int)d);
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
        pe.set(0, null);
        pt.set(0, null);
        ps.set(0, null);
        if (s != null)
        {
            Declaration d = s.isDeclaration();
            if ((d != null) && ((d.storage_class & 262144L) != 0))
            {
                s = s.toAlias();
            }
            else
            {
                s.checkDeprecated(loc, sc);
                if (d != null)
                {
                    d.checkDisabled(loc, sc, true);
                }
            }
            s = s.toAlias();
            {
                int i = 0;
                for (; (i < mt.idents.length);i++){
                    RootObject id = mt.idents.get(i);
                    if ((id.dyncast() == DYNCAST.expression) || (id.dyncast() == DYNCAST.type))
                    {
                        Ref<Type> tx = ref(null);
                        Ref<Expression> ex = ref(null);
                        Ref<Dsymbol> sx = ref(null);
                        resolveTupleIndex(loc, sc, s, ptr(ex), ptr(tx), ptr(sx), id);
                        if (sx.value != null)
                        {
                            s = sx.value.toAlias();
                            continue;
                        }
                        if (tx.value != null)
                        {
                            ex.value = new TypeExp(loc, tx.value);
                        }
                        assert(ex.value != null);
                        ex.value = typeToExpressionHelper(mt, ex.value, i + 1);
                        ex.value = expressionSemantic(ex.value, sc);
                        resolveExp(ex.value, pt, pe, ps);
                        return ;
                    }
                    Type t = s.getType();
                    int errorsave = global.value.errors;
                    int flags = (t == null) ? 8 : 1;
                    Dsymbol sm = s.searchX(loc, sc, id, flags);
                    if ((sm != null) && (((sc.get()).flags & 512) == 0) && !symbolIsVisible(sc, sm))
                    {
                        error(loc, new BytePtr("`%s` is not visible from module `%s`"), sm.toPrettyChars(false), (sc.get())._module.toChars());
                        sm = null;
                    }
                    if ((global.value.errors != errorsave))
                    {
                        pt.set(0, Type.terror);
                        return ;
                    }
                    Function0<Void> helper3 = new Function0<Void>(){
                        public Void invoke() {
                            Expression e = null;
                            VarDeclaration v = s.isVarDeclaration();
                            FuncDeclaration f = s.isFuncDeclaration();
                            if (intypeid || (v == null) && (f == null))
                            {
                                e = symbolToExp(s, loc, sc, true);
                            }
                            else
                            {
                                e = new VarExp(loc, s.isDeclaration(), true);
                            }
                            e = typeToExpressionHelper(mt, e, i);
                            e = expressionSemantic(e, sc);
                            resolveExp(e, pt, pe, ps);
                            return null;
                        }
                    };
                    if (intypeid && (t == null) && (sm != null) && sm.needThis())
                    {
                        helper3.invoke();
                        return ;
                    }
                    {
                        VarDeclaration v = s.isVarDeclaration();
                        if ((v) != null)
                        {
                            if ((v.type == null))
                            {
                                dsymbolSemantic(v, sc);
                            }
                            if (((v.storage_class & 9437188L) != 0) || v.type.isConst() || v.type.isImmutable())
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
                            if (s.isDeclaration() != null)
                            {
                                t = s.isDeclaration().type;
                                if ((t == null) && (s.isTupleDeclaration() != null))
                                {
                                    helper3.invoke();
                                    return ;
                                }
                            }
                            else if ((s.isTemplateInstance() != null) || (s.isImport() != null) || (s.isPackage() != null) || (s.isModule() != null))
                            {
                                helper3.invoke();
                                return ;
                            }
                        }
                        if (t != null)
                        {
                            sm = t.toDsymbol(sc);
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
                                assert(global.value.errors != 0);
                            }
                            else
                            {
                                assert((id.dyncast() == DYNCAST.identifier));
                                sm = s.search_correct((Identifier)id);
                                if (sm != null)
                                {
                                    error(loc, new BytePtr("identifier `%s` of `%s` is not defined, did you mean %s `%s`?"), id.toChars(), mt.toChars(), sm.kind(), sm.toChars());
                                }
                                else
                                {
                                    error(loc, new BytePtr("identifier `%s` of `%s` is not defined"), id.toChars(), mt.toChars());
                                }
                            }
                            pe.set(0, (new ErrorExp()));
                            return ;
                        }
                    }
                    s = sm.toAlias();
                }
            }
            {
                EnumMember em = s.isEnumMember();
                if ((em) != null)
                {
                    pe.set(0, em.getVarExp(loc, sc));
                    return ;
                }
            }
            {
                VarDeclaration v = s.isVarDeclaration();
                if ((v) != null)
                {
                    if ((v.type == null) || (v.type.deco == null) && (v.inuse != 0))
                    {
                        if (v.inuse != 0)
                        {
                            error(loc, new BytePtr("circular reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                        }
                        else
                        {
                            error(loc, new BytePtr("forward reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                        }
                        pt.set(0, Type.terror);
                        return ;
                    }
                    if (((v.type.ty & 0xFF) == ENUMTY.Terror))
                    {
                        pt.set(0, Type.terror);
                    }
                    else
                    {
                        pe.set(0, (new VarExp(loc, v, true)));
                    }
                    return ;
                }
            }
            {
                FuncLiteralDeclaration fld = s.isFuncLiteralDeclaration();
                if ((fld) != null)
                {
                    pe.set(0, (new FuncExp(loc, fld)));
                    pe.set(0, expressionSemantic(pe.get(), sc));
                    return ;
                }
            }
            Type t = null;
            for (; 1 != 0;){
                t = s.getType();
                if (t != null)
                {
                    break;
                }
                {
                    Import si = s.isImport();
                    if ((si) != null)
                    {
                        s = si.search(loc, s.ident, 8);
                        if ((s != null) && (!pequals(s, si)))
                        {
                            continue;
                        }
                        s = si;
                    }
                }
                ps.set(0, s);
                return ;
            }
            {
                TypeInstance ti = t.isTypeInstance();
                if ((ti) != null)
                {
                    if ((!pequals(ti, mt)) && (ti.deco == null))
                    {
                        if (!ti.tempinst.errors)
                        {
                            error(loc, new BytePtr("forward reference to `%s`"), ti.toChars());
                        }
                        pt.set(0, Type.terror);
                        return ;
                    }
                }
            }
            if (((t.ty & 0xFF) == ENUMTY.Ttuple))
            {
                pt.set(0, t);
            }
            else
            {
                pt.set(0, merge(t));
            }
        }
        if (s == null)
        {
            BytePtr p = pcopy(mt.mutableOf().unSharedOf().toChars());
            Identifier id = Identifier.idPool(p, strlen(p));
            {
                ByteSlice n = importHint(id.asString()).copy();
                if ((n).getLength() != 0)
                {
                    error(loc, new BytePtr("`%s` is not defined, perhaps `import %.*s;` ?"), p, n.getLength(), toBytePtr(n));
                }
                else {
                    Dsymbol s2 = (sc.get()).search_correct(id);
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
            pt.set(0, Type.terror);
        }
    }

    // defaulted all parameters starting with #9
    public static void resolveHelper(TypeQualified mt, Loc loc, Ptr<Scope> sc, Dsymbol s, Dsymbol scopesym, Ptr<Expression> pe, Ptr<Type> pt, Ptr<Dsymbol> ps) {
        return resolveHelper(mt, loc, sc, s, scopesym, pe, pt, ps, false);
    }

    public static Type stripDefaultArgs(Type t) {
        Function1<Ptr<DArray<Parameter>>,Ptr<DArray<Parameter>>> stripParams = new Function1<Ptr<DArray<Parameter>>,Ptr<DArray<Parameter>>>(){
            public Ptr<DArray<Parameter>> invoke(Ptr<DArray<Parameter>> parameters) {
                Function1<Parameter,Parameter> stripParameter = new Function1<Parameter,Parameter>(){
                    public Parameter invoke(Parameter p) {
                        Ref<Parameter> p_ref = ref(p);
                        Ref<Type> t = ref(stripDefaultArgs(p_ref.value.type));
                        return (!pequals(t.value, p_ref.value.type)) || (p_ref.value.defaultArg != null) || (p_ref.value.ident != null) || (p_ref.value.userAttribDecl != null) ? new Parameter(p_ref.value.storageClass, t.value, null, null, null) : null;
                    }
                };
                if (parameters != null)
                {
                    {
                        Slice<Parameter> __r1644 = (parameters.get()).opSlice().copy();
                        int __key1643 = 0;
                        for (; (__key1643 < __r1644.getLength());__key1643 += 1) {
                            Parameter p = __r1644.get(__key1643);
                            int i = __key1643;
                            Parameter ps = stripParameter.invoke(p);
                            if (ps != null)
                            {
                                Ptr<DArray<Parameter>> nparams = refPtr(new DArray<Parameter>((parameters.get()).length));
                                {
                                    Slice<Parameter> __r1646 = (nparams.get()).opSlice().copy();
                                    int __key1645 = 0;
                                    for (; (__key1645 < __r1646.getLength());__key1645 += 1) {
                                        Parameter np = __r1646.get(__key1645);
                                        int j = __key1645;
                                        Parameter pj = (parameters.get()).get(j);
                                        if ((j < i))
                                        {
                                            np = pj;
                                        }
                                        else if ((j == i))
                                        {
                                            np = ps;
                                        }
                                        else
                                        {
                                            Parameter nps = stripParameter.invoke(pj);
                                            np = nps != null ? nps : pj;
                                        }
                                    }
                                }
                                return nparams;
                            }
                        }
                    }
                }
                return parameters;
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
                Ptr<DArray<Parameter>> params = stripParams.invoke(tf.parameterList.parameters);
                if ((pequals(tret, tf.next.value)) && (params == tf.parameterList.parameters))
                {
                    return t;
                }
                TypeFunction tr = (TypeFunction)tf.copy();
                tr.parameterList.parameters = params;
                tr.next.value = tret;
                return tr;
            }
            else {
                TypeTuple tt = t.isTypeTuple();
                if ((tt) != null)
                {
                    Ptr<DArray<Parameter>> args = stripParams.invoke(tt.arguments);
                    if ((args == tt.arguments))
                    {
                        return t;
                    }
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
                    Expression e = typeToExpression(t.next.value);
                    if ((e) != null)
                    {
                        return new ArrayExp(t.dim.loc, e, t.dim);
                    }
                }
                return null;
            }
        };
        Function1<TypeAArray,Expression> visitAArray = new Function1<TypeAArray,Expression>(){
            public Expression invoke(TypeAArray t) {
                {
                    Expression e = typeToExpression(t.next.value);
                    if ((e) != null)
                    {
                        {
                            Expression ei = typeToExpression(t.index);
                            if ((ei) != null)
                            {
                                return new ArrayExp(t.loc, e, ei);
                            }
                        }
                    }
                }
                return null;
            }
        };
        Function1<TypeIdentifier,Expression> visitIdentifier = new Function1<TypeIdentifier,Expression>(){
            public Expression invoke(TypeIdentifier t) {
                return typeToExpressionHelper(t, new IdentifierExp(t.loc, t.ident), 0);
            }
        };
        Function1<TypeInstance,Expression> visitInstance = new Function1<TypeInstance,Expression>(){
            public Expression invoke(TypeInstance t) {
                return typeToExpressionHelper(t, new ScopeExp(t.loc, t.tempinst), 0);
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
            Slice<RootObject> __r1647 = t.idents.opSlice(i, t.idents.length).copy();
            int __key1648 = 0;
            for (; (__key1648 < __r1647.getLength());__key1648 += 1) {
                RootObject id = __r1647.get(__key1648);
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
        Function0<Type> error = new Function0<Type>(){
            public Type invoke() {
                return Type.terror;
            }
        };
        Function1<Type,Type> visitType = new Function1<Type,Type>(){
            public Type invoke(Type t) {
                if (((t.ty & 0xFF) == ENUMTY.Tint128) || ((t.ty & 0xFF) == ENUMTY.Tuns128))
                {
                    error(loc, new BytePtr("`cent` and `ucent` types not implemented"));
                    return error.invoke();
                }
                return merge(t);
            }
        };
        Function1<TypeVector,Type> visitVector = new Function1<TypeVector,Type>(){
            public Type invoke(TypeVector mtype) {
                int errors = global.value.errors;
                mtype.basetype = typeSemantic(mtype.basetype, loc, sc);
                if ((errors != global.value.errors))
                {
                    return error.invoke();
                }
                mtype.basetype = mtype.basetype.toBasetype().mutableOf();
                if (((mtype.basetype.ty & 0xFF) != ENUMTY.Tsarray))
                {
                    error(loc, new BytePtr("T in __vector(T) must be a static array, not `%s`"), mtype.basetype.toChars());
                    return error.invoke();
                }
                TypeSArray t = (TypeSArray)mtype.basetype;
                int sz = (int)t.size(loc);
                switch (target.value.isVectorTypeSupported(sz, t.nextOf()))
                {
                    case 0:
                        break;
                    case 1:
                        error(loc, new BytePtr("SIMD vector types not supported on this platform"));
                        return error.invoke();
                    case 2:
                        error(loc, new BytePtr("vector type `%s` is not supported on this platform"), mtype.toChars());
                        return error.invoke();
                    case 3:
                        error(loc, new BytePtr("%d byte vector type `%s` is not supported on this platform"), sz, mtype.toChars());
                        return error.invoke();
                    default:
                    throw SwitchError.INSTANCE;
                }
                return merge(mtype);
            }
        };
        Function1<TypeSArray,Type> visitSArray = new Function1<TypeSArray,Type>(){
            public Type invoke(TypeSArray mtype) {
                Ref<TypeSArray> mtype_ref = ref(mtype);
                Ref<Type> t = ref(null);
                Ref<Expression> e = ref(null);
                Ref<Dsymbol> s = ref(null);
                resolve(mtype_ref.value.next.value, loc, sc, ptr(e), ptr(t), ptr(s), false);
                {
                    TupleDeclaration tup = s.value != null ? s.value.isTupleDeclaration() : null;
                    if ((tup) != null)
                    {
                        mtype_ref.value.dim = semanticLength(sc, tup, mtype_ref.value.dim);
                        mtype_ref.value.dim = mtype_ref.value.dim.ctfeInterpret();
                        if (((mtype_ref.value.dim.op & 0xFF) == 127))
                        {
                            return error.invoke();
                        }
                        long d = mtype_ref.value.dim.toUInteger();
                        if ((d >= (long)(tup.objects.get()).length))
                        {
                            error(loc, new BytePtr("tuple index %llu exceeds %llu"), d, (long)(tup.objects.get()).length);
                            return error.invoke();
                        }
                        RootObject o = (tup.objects.get()).get((int)d);
                        if ((o.dyncast() != DYNCAST.type))
                        {
                            error(loc, new BytePtr("`%s` is not a type"), mtype_ref.value.toChars());
                            return error.invoke();
                        }
                        return ((Type)o).addMod(mtype_ref.value.mod);
                    }
                }
                Type tn = typeSemantic(mtype_ref.value.next.value, loc, sc);
                if (((tn.ty & 0xFF) == ENUMTY.Terror))
                {
                    return error.invoke();
                }
                Ref<Type> tbn = ref(tn.toBasetype());
                if (mtype_ref.value.dim != null)
                {
                    if (mtype_ref.value.dim.isDotVarExp() != null)
                    {
                        {
                            Declaration vd = mtype_ref.value.dim.isDotVarExp().var;
                            if ((vd) != null)
                            {
                                FuncDeclaration fd = vd.toAlias().isFuncDeclaration();
                                if (fd != null)
                                {
                                    mtype_ref.value.dim = new CallExp(loc, fd, null);
                                }
                            }
                        }
                    }
                    int errors = global.value.errors;
                    mtype_ref.value.dim = semanticLength(sc, tbn.value, mtype_ref.value.dim);
                    if ((errors != global.value.errors))
                    {
                        return error.invoke();
                    }
                    mtype_ref.value.dim = mtype_ref.value.dim.optimize(0, false);
                    mtype_ref.value.dim = mtype_ref.value.dim.ctfeInterpret();
                    if (((mtype_ref.value.dim.op & 0xFF) == 127))
                    {
                        return error.invoke();
                    }
                    errors = global.value.errors;
                    Ref<Long> d1 = ref(mtype_ref.value.dim.toInteger());
                    if ((errors != global.value.errors))
                    {
                        return error.invoke();
                    }
                    mtype_ref.value.dim = mtype_ref.value.dim.implicitCastTo(sc, Type.tsize_t);
                    mtype_ref.value.dim = mtype_ref.value.dim.optimize(0, false);
                    if (((mtype_ref.value.dim.op & 0xFF) == 127))
                    {
                        return error.invoke();
                    }
                    errors = global.value.errors;
                    long d2 = mtype_ref.value.dim.toInteger();
                    if ((errors != global.value.errors))
                    {
                        return error.invoke();
                    }
                    if (((mtype_ref.value.dim.op & 0xFF) == 127))
                    {
                        return error.invoke();
                    }
                    Function0<Type> overflowError = new Function0<Type>(){
                        public Type invoke() {
                            error(loc, new BytePtr("`%s` size %llu * %llu exceeds 0x%llx size limit for static array"), mtype_ref.value.toChars(), tbn.value.size(loc), d1.value, target.value.maxStaticDataSize);
                            return error.invoke();
                        }
                    };
                    if ((d1.value != d2))
                    {
                        return overflowError.invoke();
                    }
                    Type tbx = tbn.value.baseElemOf();
                    if (((tbx.ty & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)tbx).sym.members == null) || ((tbx.ty & 0xFF) == ENUMTY.Tenum) && (((TypeEnum)tbx).sym.members == null))
                    {
                    }
                    else if ((tbn.value.isTypeBasic() != null) || ((tbn.value.ty & 0xFF) == ENUMTY.Tpointer) || ((tbn.value.ty & 0xFF) == ENUMTY.Tarray) || ((tbn.value.ty & 0xFF) == ENUMTY.Tsarray) || ((tbn.value.ty & 0xFF) == ENUMTY.Taarray) || ((tbn.value.ty & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)tbn.value).sym.sizeok == Sizeok.done) || ((tbn.value.ty & 0xFF) == ENUMTY.Tclass))
                    {
                        Ref<Boolean> overflow = ref(false);
                        if ((mulu(tbn.value.size(loc), d2, overflow) >= target.value.maxStaticDataSize) || overflow.value)
                        {
                            return overflowError.invoke();
                        }
                    }
                }
                switch ((tbn.value.ty & 0xFF))
                {
                    case 37:
                        assert(mtype_ref.value.dim != null);
                        TypeTuple tt = (TypeTuple)tbn.value;
                        long d = mtype_ref.value.dim.toUInteger();
                        if ((d >= (long)(tt.arguments.get()).length))
                        {
                            error(loc, new BytePtr("tuple index %llu exceeds %llu"), d, (long)(tt.arguments.get()).length);
                            return error.invoke();
                        }
                        Type telem = (tt.arguments.get()).get((int)d).type;
                        return telem.addMod(mtype_ref.value.mod);
                    case 5:
                    case 11:
                        error(loc, new BytePtr("cannot have array of `%s`"), tbn.value.toChars());
                        return error.invoke();
                    default:
                    break;
                }
                if (tbn.value.isscope())
                {
                    error(loc, new BytePtr("cannot have array of scope `%s`"), tbn.value.toChars());
                    return error.invoke();
                }
                mtype_ref.value.next.value = tn;
                mtype_ref.value.transitive();
                return merge(mtype_ref.value.addMod(tn.mod));
            }
        };
        Function1<TypeDArray,Type> visitDArray = new Function1<TypeDArray,Type>(){
            public Type invoke(TypeDArray mtype) {
                Type tn = typeSemantic(mtype.next.value, loc, sc);
                Type tbn = tn.toBasetype();
                switch ((tbn.ty & 0xFF))
                {
                    case 37:
                        return tbn;
                    case 5:
                    case 11:
                        error(loc, new BytePtr("cannot have array of `%s`"), tbn.toChars());
                        return error.invoke();
                    case 34:
                        return error.invoke();
                    default:
                    break;
                }
                if (tn.isscope())
                {
                    error(loc, new BytePtr("cannot have array of scope `%s`"), tn.toChars());
                    return error.invoke();
                }
                mtype.next.value = tn;
                mtype.transitive();
                return merge(mtype);
            }
        };
        Function1<TypeAArray,Type> visitAArray = new Function1<TypeAArray,Type>(){
            public Type invoke(TypeAArray mtype) {
                if (mtype.deco != null)
                {
                    return mtype;
                }
                mtype.loc = loc.copy();
                mtype.sc = sc;
                if (sc != null)
                {
                    (sc.get()).setNoFree();
                }
                if (((mtype.index.ty & 0xFF) == ENUMTY.Tident) || ((mtype.index.ty & 0xFF) == ENUMTY.Tinstance) || ((mtype.index.ty & 0xFF) == ENUMTY.Tsarray) || ((mtype.index.ty & 0xFF) == ENUMTY.Ttypeof) || ((mtype.index.ty & 0xFF) == ENUMTY.Treturn))
                {
                    Ref<Expression> e = ref(null);
                    Ref<Type> t = ref(null);
                    Ref<Dsymbol> s = ref(null);
                    resolve(mtype.index, loc, sc, ptr(e), ptr(t), ptr(s), false);
                    if (s.value != null)
                    {
                        {
                            FuncDeclaration fd = s.value.toAlias().isFuncDeclaration();
                            if ((fd) != null)
                            {
                                e.value = new CallExp(loc, fd, null);
                            }
                        }
                    }
                    if (e.value != null)
                    {
                        TypeSArray tsa = new TypeSArray(mtype.next.value, e.value);
                        return typeSemantic(tsa, loc, sc);
                    }
                    else if (t.value != null)
                    {
                        mtype.index = typeSemantic(t.value, loc, sc);
                    }
                    else
                    {
                        error(loc, new BytePtr("index is not a type or an expression"));
                        return error.invoke();
                    }
                }
                else
                {
                    mtype.index = typeSemantic(mtype.index, loc, sc);
                }
                mtype.index = mtype.index.merge2();
                if ((mtype.index.nextOf() != null) && !mtype.index.nextOf().isImmutable())
                {
                    mtype.index = mtype.index.constOf().mutableOf();
                }
                {
                    int __dispatch5 = 0;
                    dispatched_5:
                    do {
                        switch (__dispatch5 != 0 ? __dispatch5 : (mtype.index.toBasetype().ty & 0xFF))
                        {
                            case 5:
                            case 12:
                            case 11:
                            case 37:
                                error(loc, new BytePtr("cannot have associative array key of `%s`"), mtype.index.toBasetype().toChars());
                                /*goto case*/{ __dispatch5 = 34; continue dispatched_5; }
                            case 34:
                                __dispatch5 = 0;
                                return error.invoke();
                            default:
                            break;
                        }
                    } while(__dispatch5 != 0);
                }
                Type tbase = mtype.index.baseElemOf();
                for (; ((tbase.ty & 0xFF) == ENUMTY.Tarray);) {
                    tbase = tbase.nextOf().baseElemOf();
                }
                {
                    TypeStruct ts = tbase.isTypeStruct();
                    if ((ts) != null)
                    {
                        StructDeclaration sd = ts.sym;
                        if ((sd.semanticRun < PASS.semanticdone))
                        {
                            dsymbolSemantic(sd, null);
                        }
                        if ((sd.xeq != null) && (sd.xeq._scope != null) && (sd.xeq.semanticRun < PASS.semantic3done))
                        {
                            int errors = global.value.startGagging();
                            semantic3(sd.xeq, sd.xeq._scope);
                            if (global.value.endGagging(errors))
                            {
                                sd.xeq = StructDeclaration.xerreq;
                            }
                        }
                        BytePtr s = pcopy(((mtype.index.toBasetype().ty & 0xFF) != ENUMTY.Tstruct) ? new BytePtr("bottom of ") : new BytePtr(""));
                        if (sd.xeq == null)
                        {
                        }
                        else if ((pequals(sd.xeq, StructDeclaration.xerreq)))
                        {
                            if (search_function(sd, Id.eq) != null)
                            {
                                error(loc, new BytePtr("%sAA key type `%s` does not have `bool opEquals(ref const %s) const`"), s, sd.toChars(), sd.toChars());
                            }
                            else
                            {
                                error(loc, new BytePtr("%sAA key type `%s` does not support const equality"), s, sd.toChars());
                            }
                            return error.invoke();
                        }
                        else if (sd.xhash == null)
                        {
                            if (search_function(sd, Id.eq) != null)
                            {
                                error(loc, new BytePtr("%sAA key type `%s` should have `size_t toHash() const nothrow @safe` if `opEquals` defined"), s, sd.toChars());
                            }
                            else
                            {
                                error(loc, new BytePtr("%sAA key type `%s` supports const equality but doesn't support const hashing"), s, sd.toChars());
                            }
                            return error.invoke();
                        }
                        else
                        {
                            assert((sd.xeq != null) && (sd.xhash != null));
                        }
                    }
                    else if (((tbase.ty & 0xFF) == ENUMTY.Tclass) && (((TypeClass)tbase).sym.isInterfaceDeclaration() == null))
                    {
                        ClassDeclaration cd = ((TypeClass)tbase).sym;
                        if ((cd.semanticRun < PASS.semanticdone))
                        {
                            dsymbolSemantic(cd, null);
                        }
                        if (ClassDeclaration.object == null)
                        {
                            error(Loc.initial, new BytePtr("missing or corrupt object.d"));
                            fatal();
                        }
                        if (typesem.visitAArrayfeq == null)
                        {
                            typesem.visitAArrayfeq = search_function(ClassDeclaration.object, Id.eq).isFuncDeclaration();
                        }
                        if (typesem.visitAArrayfcmp == null)
                        {
                            typesem.visitAArrayfcmp = search_function(ClassDeclaration.object, Id.cmp).isFuncDeclaration();
                        }
                        if (typesem.visitAArrayfhash == null)
                        {
                            typesem.visitAArrayfhash = search_function(ClassDeclaration.object, Id.tohash).isFuncDeclaration();
                        }
                        assert((typesem.visitAArrayfcmp != null) && (typesem.visitAArrayfeq != null) && (typesem.visitAArrayfhash != null));
                        if ((typesem.visitAArrayfeq.vtblIndex < cd.vtbl.value.length) && (pequals(cd.vtbl.value.get(typesem.visitAArrayfeq.vtblIndex), typesem.visitAArrayfeq)))
                        {
                            if ((typesem.visitAArrayfcmp.vtblIndex < cd.vtbl.value.length) && (!pequals(cd.vtbl.value.get(typesem.visitAArrayfcmp.vtblIndex), typesem.visitAArrayfcmp)))
                            {
                                BytePtr s = pcopy(((mtype.index.toBasetype().ty & 0xFF) != ENUMTY.Tclass) ? new BytePtr("bottom of ") : new BytePtr(""));
                                error(loc, new BytePtr("%sAA key type `%s` now requires equality rather than comparison"), s, cd.toChars());
                                errorSupplemental(loc, new BytePtr("Please override `Object.opEquals` and `Object.toHash`."));
                            }
                        }
                    }
                }
                mtype.next.value = typeSemantic(mtype.next.value, loc, sc).merge2();
                mtype.transitive();
                {
                    int __dispatch6 = 0;
                    dispatched_6:
                    do {
                        switch (__dispatch6 != 0 ? __dispatch6 : (mtype.next.value.toBasetype().ty & 0xFF))
                        {
                            case 5:
                            case 12:
                            case 11:
                            case 37:
                                error(loc, new BytePtr("cannot have associative array of `%s`"), mtype.next.value.toChars());
                                /*goto case*/{ __dispatch6 = 34; continue dispatched_6; }
                            case 34:
                                __dispatch6 = 0;
                                return error.invoke();
                            default:
                            break;
                        }
                    } while(__dispatch6 != 0);
                }
                if (mtype.next.value.isscope())
                {
                    error(loc, new BytePtr("cannot have array of scope `%s`"), mtype.next.value.toChars());
                    return error.invoke();
                }
                return merge(mtype);
            }
        };
        Function1<TypePointer,Type> visitPointer = new Function1<TypePointer,Type>(){
            public Type invoke(TypePointer mtype) {
                if (mtype.deco != null)
                {
                    return mtype;
                }
                Type n = typeSemantic(mtype.next.value, loc, sc);
                {
                    int __dispatch7 = 0;
                    dispatched_7:
                    do {
                        switch (__dispatch7 != 0 ? __dispatch7 : (n.toBasetype().ty & 0xFF))
                        {
                            case 37:
                                error(loc, new BytePtr("cannot have pointer to `%s`"), n.toChars());
                                /*goto case*/{ __dispatch7 = 34; continue dispatched_7; }
                            case 34:
                                __dispatch7 = 0;
                                return error.invoke();
                            default:
                            break;
                        }
                    } while(__dispatch7 != 0);
                }
                if ((!pequals(n, mtype.next.value)))
                {
                    mtype.deco = null;
                }
                mtype.next.value = n;
                if (((mtype.next.value.ty & 0xFF) != ENUMTY.Tfunction))
                {
                    mtype.transitive();
                    return merge(mtype);
                }
                mtype.deco = pcopy(merge(mtype).deco);
                return mtype;
            }
        };
        Function1<TypeReference,Type> visitReference = new Function1<TypeReference,Type>(){
            public Type invoke(TypeReference mtype) {
                Type n = typeSemantic(mtype.next.value, loc, sc);
                if ((!pequals(n, mtype.next.value)))
                {
                    mtype.deco = null;
                }
                mtype.next.value = n;
                mtype.transitive();
                return merge(mtype);
            }
        };
        Function1<TypeFunction,Type> visitFunction = new Function1<TypeFunction,Type>(){
            public Type invoke(TypeFunction mtype) {
                if (mtype.deco != null)
                {
                    return mtype;
                }
                boolean errors = false;
                if ((mtype.inuse > 500))
                {
                    mtype.inuse = 0;
                    error(loc, new BytePtr("recursive type"));
                    return error.invoke();
                }
                TypeFunction tf = mtype.copy().toTypeFunction();
                if (mtype.parameterList.parameters != null)
                {
                    tf.parameterList.parameters = (mtype.parameterList.parameters.get()).copy();
                    {
                        int i = 0;
                        for (; (i < (mtype.parameterList.parameters.get()).length);i++){
                            Parameter p = null;
                            (p) = ((mtype.parameterList.parameters.get()).get(i)).copy();
                            tf.parameterList.parameters.get().set(i, p);
                        }
                    }
                }
                if (((sc.get()).stc & 67108864L) != 0)
                {
                    tf.purity = PURE.fwdref;
                }
                if (((sc.get()).stc & 33554432L) != 0)
                {
                    tf.isnothrow = true;
                }
                if (((sc.get()).stc & 4398046511104L) != 0)
                {
                    tf.isnogc = true;
                }
                if (((sc.get()).stc & 2097152L) != 0)
                {
                    tf.isref = true;
                }
                if (((sc.get()).stc & 17592186044416L) != 0)
                {
                    tf.isreturn = true;
                }
                if (((sc.get()).stc & 4503599627370496L) != 0)
                {
                    tf.isreturninferred = true;
                }
                if (((sc.get()).stc & 524288L) != 0)
                {
                    tf.isscope = true;
                }
                if (((sc.get()).stc & 562949953421312L) != 0)
                {
                    tf.isscopeinferred = true;
                }
                if ((tf.trust == TRUST.default_))
                {
                    if (((sc.get()).stc & 8589934592L) != 0)
                    {
                        tf.trust = TRUST.safe;
                    }
                    else if (((sc.get()).stc & 34359738368L) != 0)
                    {
                        tf.trust = TRUST.system;
                    }
                    else if (((sc.get()).stc & 17179869184L) != 0)
                    {
                        tf.trust = TRUST.trusted;
                    }
                }
                if (((sc.get()).stc & 4294967296L) != 0)
                {
                    tf.isproperty = true;
                }
                tf.linkage = (sc.get()).linkage;
                boolean wildreturn = false;
                if (tf.next.value != null)
                {
                    sc = (sc.get()).push();
                    (sc.get()).stc &= -4465259184133L;
                    tf.next.value = typeSemantic(tf.next.value, loc, sc);
                    sc = (sc.get()).pop();
                    (errors ? 1 : 0) |= (tf.checkRetType(loc) ? 1 : 0);
                    if (tf.next.value.isscope() && (((sc.get()).flags & 1) == 0))
                    {
                        error(loc, new BytePtr("functions cannot return `scope %s`"), tf.next.value.toChars());
                        errors = true;
                    }
                    if (tf.next.value.hasWild() != 0)
                    {
                        wildreturn = true;
                    }
                    if (tf.isreturn && !tf.isref && !tf.next.value.hasPointers())
                    {
                        tf.isreturn = false;
                    }
                }
                byte wildparams = (byte)0;
                if (tf.parameterList.parameters != null)
                {
                    Ptr<Scope> argsc = (sc.get()).push();
                    (argsc.get()).stc = 0L;
                    (argsc.get()).protection = new Prot(Prot.Kind.public_).copy();
                    (argsc.get()).func = null;
                    int dim = tf.parameterList.length();
                    {
                        int i = 0;
                        for (; (i < dim);i++){
                            Parameter fparam = tf.parameterList.get(i);
                            mtype.inuse++;
                            fparam.type = typeSemantic(fparam.type, loc, argsc);
                            mtype.inuse--;
                            if (((fparam.type.ty & 0xFF) == ENUMTY.Terror))
                            {
                                errors = true;
                                continue;
                            }
                            fparam.type = fparam.type.addStorageClass(fparam.storageClass);
                            if ((fparam.storageClass & 268435713L) != 0)
                            {
                                if (fparam.type == null)
                                {
                                    continue;
                                }
                            }
                            Type t = fparam.type.toBasetype();
                            if (((t.ty & 0xFF) == ENUMTY.Tfunction))
                            {
                                error(loc, new BytePtr("cannot have parameter of function type `%s`"), fparam.type.toChars());
                                errors = true;
                            }
                            else if (((fparam.storageClass & 2101248L) == 0) && ((t.ty & 0xFF) == ENUMTY.Tstruct) || ((t.ty & 0xFF) == ENUMTY.Tsarray) || ((t.ty & 0xFF) == ENUMTY.Tenum))
                            {
                                Type tb2 = t.baseElemOf();
                                if (((tb2.ty & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)tb2).sym.members == null) || ((tb2.ty & 0xFF) == ENUMTY.Tenum) && (((TypeEnum)tb2).sym.memtype == null))
                                {
                                    error(loc, new BytePtr("cannot have parameter of opaque type `%s` by value"), fparam.type.toChars());
                                    errors = true;
                                }
                            }
                            else if (((fparam.storageClass & 8192L) == 0) && ((t.ty & 0xFF) == ENUMTY.Tvoid))
                            {
                                error(loc, new BytePtr("cannot have parameter of type `%s`"), fparam.type.toChars());
                                errors = true;
                            }
                            if (((fparam.storageClass & 2149580800L) == 2149580800L))
                            {
                                fparam.storageClass |= 17592186044416L;
                            }
                            if ((fparam.storageClass & 17592186044416L) != 0)
                            {
                                if ((fparam.storageClass & 2101248L) != 0)
                                {
                                    if (false)
                                    {
                                        Ref<Long> stc = ref(fparam.storageClass & 2101248L);
                                        error(loc, new BytePtr("parameter `%s` is `return %s` but function does not return by `ref`"), fparam.ident != null ? fparam.ident.toChars() : new BytePtr(""), stcToChars(stc));
                                        errors = true;
                                    }
                                }
                                else
                                {
                                    if ((fparam.storageClass & 524288L) == 0)
                                    {
                                        fparam.storageClass |= 562949953945600L;
                                    }
                                    if (tf.isref)
                                    {
                                    }
                                    else if ((tf.next.value != null) && !tf.next.value.hasPointers() && ((tf.next.value.toBasetype().ty & 0xFF) != ENUMTY.Tvoid))
                                    {
                                        fparam.storageClass &= -17592186044417L;
                                    }
                                }
                            }
                            if ((fparam.storageClass & 2105344L) != 0)
                            {
                            }
                            else if ((fparam.storageClass & 4096L) != 0)
                            {
                                {
                                    byte m = (byte)((fparam.type.mod & 0xFF) & 13);
                                    if ((m) != 0)
                                    {
                                        error(loc, new BytePtr("cannot have `%s out` parameter of type `%s`"), MODtoChars(m), t.toChars());
                                        errors = true;
                                    }
                                    else
                                    {
                                        Type tv = t.baseElemOf();
                                        if (((tv.ty & 0xFF) == ENUMTY.Tstruct) && ((TypeStruct)tv).sym.noDefaultCtor)
                                        {
                                            error(loc, new BytePtr("cannot have `out` parameter of type `%s` because the default construction is disabled"), fparam.type.toChars());
                                            errors = true;
                                        }
                                    }
                                }
                            }
                            if (((fparam.storageClass & 524288L) != 0) && !fparam.type.hasPointers() && ((fparam.type.ty & 0xFF) != ENUMTY.Ttuple))
                            {
                                fparam.storageClass &= -524289L;
                                if (!tf.isref || (((sc.get()).flags & 1) != 0))
                                {
                                    fparam.storageClass &= -17592186044417L;
                                }
                            }
                            if (t.hasWild() != 0)
                            {
                                wildparams |= 1;
                            }
                            if (fparam.defaultArg != null)
                            {
                                Expression e = fparam.defaultArg;
                                long isRefOrOut = fparam.storageClass & 2101248L;
                                long isAuto = fparam.storageClass & 35184372089088L;
                                if ((isRefOrOut != 0) && (isAuto == 0))
                                {
                                    e = expressionSemantic(e, argsc);
                                    e = resolveProperties(argsc, e);
                                }
                                else
                                {
                                    e = inferType(e, fparam.type, 0);
                                    Initializer iz = new ExpInitializer(e.loc, e);
                                    iz = initializerSemantic(iz, argsc, fparam.type, NeedInterpret.INITnointerpret);
                                    e = initializerToExpression(iz, null);
                                }
                                if (((e.op & 0xFF) == 161))
                                {
                                    FuncExp fe = (FuncExp)e;
                                    e = new VarExp(e.loc, fe.fd, false);
                                    e = new AddrExp(e.loc, e);
                                    e = expressionSemantic(e, argsc);
                                }
                                if ((isRefOrOut != 0) && (isAuto == 0) || e.isLvalue() && !MODimplicitConv(e.type.value.mod, fparam.type.mod))
                                {
                                    BytePtr errTxt = pcopy((fparam.storageClass & 2097152L) != 0 ? new BytePtr("ref") : new BytePtr("out"));
                                    error(e.loc, new BytePtr("expression `%s` of type `%s` is not implicitly convertible to type `%s %s` of parameter `%s`"), e.toChars(), e.type.value.toChars(), errTxt, fparam.type.toChars(), fparam.toChars());
                                }
                                e = e.implicitCastTo(argsc, fparam.type);
                                if ((isRefOrOut != 0) && (isAuto == 0))
                                {
                                    e = e.toLvalue(argsc, e);
                                }
                                fparam.defaultArg = e;
                                if (((e.op & 0xFF) == 127))
                                {
                                    errors = true;
                                }
                            }
                            {
                                TypeTuple tt = t.isTypeTuple();
                                if ((tt) != null)
                                {
                                    if ((tt.arguments != null) && ((tt.arguments.get()).length != 0))
                                    {
                                        int tdim = (tt.arguments.get()).length;
                                        Ptr<DArray<Parameter>> newparams = refPtr(new DArray<Parameter>(tdim));
                                        {
                                            int j = 0;
                                            for (; (j < tdim);j++){
                                                Parameter narg = (tt.arguments.get()).get(j);
                                                long stc = fparam.storageClass | narg.storageClass;
                                                long stc1 = fparam.storageClass & 2109440L;
                                                long stc2 = narg.storageClass & 2109440L;
                                                if ((stc1 != 0) && (stc2 != 0) && (stc1 != stc2))
                                                {
                                                    Ref<OutBuffer> buf1 = ref(new OutBuffer());
                                                    try {
                                                        stcToBuffer(ptr(buf1), stc1 | ((stc1 & 2097152L) != 0 ? fparam.storageClass & 256L : 0L));
                                                        Ref<OutBuffer> buf2 = ref(new OutBuffer());
                                                        try {
                                                            stcToBuffer(ptr(buf2), stc2);
                                                            error(loc, new BytePtr("incompatible parameter storage classes `%s` and `%s`"), buf1.value.peekChars(), buf2.value.peekChars());
                                                            errors = true;
                                                            stc = stc1 | stc & -2109441L;
                                                        }
                                                        finally {
                                                        }
                                                    }
                                                    finally {
                                                    }
                                                }
                                                Expression paramDefaultArg = narg.defaultArg;
                                                TupleExp te = fparam.defaultArg != null ? fparam.defaultArg.isTupleExp() : null;
                                                if ((te != null) && (te.exps != null) && ((te.exps.get()).length != 0))
                                                {
                                                    paramDefaultArg = (te.exps.get()).get(j);
                                                }
                                                newparams.get().set(j, new Parameter(stc, narg.type, narg.ident, paramDefaultArg, narg.userAttribDecl));
                                            }
                                        }
                                        fparam.type = new TypeTuple(newparams);
                                    }
                                    fparam.storageClass = 0L;
                                    dim = tf.parameterList.length();
                                    i--;
                                    continue;
                                }
                            }
                            if ((fparam.storageClass & 256L) != 0)
                            {
                                Expression farg = (mtype.fargs != null) && (i < (mtype.fargs.get()).length) ? (mtype.fargs.get()).get(i) : fparam.defaultArg;
                                if ((farg != null) && ((fparam.storageClass & 2097152L) != 0))
                                {
                                    if (farg.isLvalue())
                                    {
                                    }
                                    else
                                    {
                                        fparam.storageClass &= -2097153L;
                                    }
                                    fparam.storageClass &= -257L;
                                    fparam.storageClass |= 35184372088832L;
                                }
                                else if (mtype.incomplete && ((fparam.storageClass & 2097152L) != 0))
                                {
                                    fparam.storageClass &= -257L;
                                    fparam.storageClass |= 35184372088832L;
                                }
                                else
                                {
                                    error(loc, new BytePtr("`auto` can only be used as part of `auto ref` for template function parameters"));
                                    errors = true;
                                }
                            }
                            fparam.storageClass &= -2685405189L;
                        }
                    }
                    (argsc.get()).pop();
                }
                if (tf.isWild())
                {
                    wildparams |= 2;
                }
                if (wildreturn && (wildparams == 0))
                {
                    error(loc, new BytePtr("`inout` on `return` means `inout` must be on a parameter as well for `%s`"), mtype.toChars());
                    errors = true;
                }
                tf.iswild = wildparams;
                if (tf.isproperty && (tf.parameterList.varargs != VarArg.none) || (tf.parameterList.length() > 2))
                {
                    error(loc, new BytePtr("properties can only have zero, one, or two parameter"));
                    errors = true;
                }
                if ((tf.parameterList.varargs == VarArg.variadic) && (tf.linkage != LINK.d) && (tf.parameterList.length() == 0))
                {
                    error(loc, new BytePtr("variadic functions with non-D linkage must have at least one parameter"));
                    errors = true;
                }
                if (errors)
                {
                    return error.invoke();
                }
                if (tf.next.value != null)
                {
                    tf.deco = pcopy(merge(tf).deco);
                }
                return tf;
            }
        };
        Function1<TypeDelegate,Type> visitDelegate = new Function1<TypeDelegate,Type>(){
            public Type invoke(TypeDelegate mtype) {
                if (mtype.deco != null)
                {
                    return mtype;
                }
                mtype.next.value = typeSemantic(mtype.next.value, loc, sc);
                if (((mtype.next.value.ty & 0xFF) != ENUMTY.Tfunction))
                {
                    return error.invoke();
                }
                mtype.deco = pcopy(merge(mtype).deco);
                return mtype;
            }
        };
        Function1<TypeIdentifier,Type> visitIdentifier = new Function1<TypeIdentifier,Type>(){
            public Type invoke(TypeIdentifier mtype) {
                Ref<Type> t = ref(null);
                Ref<Expression> e = ref(null);
                Ref<Dsymbol> s = ref(null);
                resolve(mtype, loc, sc, ptr(e), ptr(t), ptr(s), false);
                if (t.value != null)
                {
                    return t.value.addMod(mtype.mod);
                }
                else
                {
                    if (s.value != null)
                    {
                        TemplateDeclaration td = s.value.isTemplateDeclaration();
                        if ((td != null) && (td.onemember != null) && (td.onemember.isAggregateDeclaration() != null))
                        {
                            error(loc, new BytePtr("template %s `%s` is used as a type without instantiation; to instantiate it use `%s!(arguments)`"), s.value.kind(), s.value.toPrettyChars(false), s.value.ident.toChars());
                        }
                        else
                        {
                            error(loc, new BytePtr("%s `%s` is used as a type"), s.value.kind(), s.value.toPrettyChars(false));
                        }
                    }
                    else
                    {
                        error(loc, new BytePtr("`%s` is used as a type"), mtype.toChars());
                    }
                    return error.invoke();
                }
            }
        };
        Function1<TypeInstance,Type> visitInstance = new Function1<TypeInstance,Type>(){
            public Type invoke(TypeInstance mtype) {
                Ref<Type> t = ref(null);
                Ref<Expression> e = ref(null);
                Ref<Dsymbol> s = ref(null);
                {
                    int errors = global.value.errors;
                    resolve(mtype, loc, sc, ptr(e), ptr(t), ptr(s), false);
                    if ((t.value == null) && (errors != global.value.errors))
                    {
                        return error.invoke();
                    }
                }
                if (t.value == null)
                {
                    if ((e.value == null) && (s.value != null) && s.value.errors)
                    {
                        error(loc, new BytePtr("`%s` had previous errors"), mtype.toChars());
                    }
                    else
                    {
                        error(loc, new BytePtr("`%s` is used as a type"), mtype.toChars());
                    }
                    return error.invoke();
                }
                return t.value;
            }
        };
        Function1<TypeTypeof,Type> visitTypeof = new Function1<TypeTypeof,Type>(){
            public Type invoke(TypeTypeof mtype) {
                Ref<Expression> e = ref(null);
                Ref<Type> t = ref(null);
                Ref<Dsymbol> s = ref(null);
                resolve(mtype, loc, sc, ptr(e), ptr(t), ptr(s), false);
                if ((s.value != null) && ((t.value = s.value.getType()) != null))
                {
                    t.value = t.value.addMod(mtype.mod);
                }
                if (t.value == null)
                {
                    error(loc, new BytePtr("`%s` is used as a type"), mtype.toChars());
                    return error.invoke();
                }
                return t.value;
            }
        };
        Function1<TypeTraits,Type> visitTraits = new Function1<TypeTraits,Type>(){
            public Type invoke(TypeTraits mtype) {
                if (((mtype.ty & 0xFF) == ENUMTY.Terror))
                {
                    return mtype;
                }
                if ((!pequals(mtype.exp.ident, Id.allMembers)) && (!pequals(mtype.exp.ident, Id.derivedMembers)) && (!pequals(mtype.exp.ident, Id.getMember)) && (!pequals(mtype.exp.ident, Id.parent)) && (!pequals(mtype.exp.ident, Id.getOverloads)) && (!pequals(mtype.exp.ident, Id.getVirtualFunctions)) && (!pequals(mtype.exp.ident, Id.getVirtualMethods)) && (!pequals(mtype.exp.ident, Id.getAttributes)) && (!pequals(mtype.exp.ident, Id.getUnitTests)) && (!pequals(mtype.exp.ident, Id.getAliasThis)))
                {
                    error(mtype.loc, new BytePtr("trait `%s` is either invalid or not supported %s"), mtype.exp.ident.toChars(), typesem.visitTraitsctxt.get((mtype.inAliasDeclaration ? 1 : 0)));
                    mtype.ty = (byte)34;
                    return mtype;
                }
                Type result = null;
                {
                    Expression e = semanticTraits(mtype.exp, sc);
                    if ((e) != null)
                    {
                        switch ((e.op & 0xFF))
                        {
                            case 27:
                                mtype.sym = ((DotVarExp)e).var;
                                break;
                            case 26:
                                mtype.sym = ((VarExp)e).var;
                                break;
                            case 161:
                                FuncExp fe = (FuncExp)e;
                                mtype.sym = fe.td != null ? fe.td : fe.fd;
                                break;
                            case 37:
                                mtype.sym = ((DotTemplateExp)e).td;
                                break;
                            case 41:
                                mtype.sym = ((DsymbolExp)e).s;
                                break;
                            case 36:
                                mtype.sym = ((TemplateExp)e).td;
                                break;
                            case 203:
                                mtype.sym = ((ScopeExp)e).sds;
                                break;
                            case 126:
                                mtype.sym = new TupleDeclaration(e.loc, Identifier.generateId(new BytePtr("__aliastup")), ((Ptr<DArray<RootObject>>)e.toTupleExp().exps));
                                break;
                            case 30:
                                result = isType(((DotTypeExp)e).sym);
                                break;
                            case 20:
                                result = ((TypeExp)e).type.value;
                                break;
                            case 214:
                                result = ((OverExp)e).type.value;
                                break;
                            default:
                            break;
                        }
                    }
                }
                if (result != null)
                {
                    result = result.addMod(mtype.mod);
                }
                if (!mtype.inAliasDeclaration && (result == null))
                {
                    if (global.value.errors == 0)
                    {
                        error(mtype.loc, new BytePtr("`%s` does not give a valid type"), mtype.toChars());
                    }
                    return error.invoke();
                }
                return result;
            }
        };
        Function1<TypeReturn,Type> visitReturn = new Function1<TypeReturn,Type>(){
            public Type invoke(TypeReturn mtype) {
                Ref<Expression> e = ref(null);
                Ref<Type> t = ref(null);
                Ref<Dsymbol> s = ref(null);
                resolve(mtype, loc, sc, ptr(e), ptr(t), ptr(s), false);
                if ((s.value != null) && ((t.value = s.value.getType()) != null))
                {
                    t.value = t.value.addMod(mtype.mod);
                }
                if (t.value == null)
                {
                    error(loc, new BytePtr("`%s` is used as a type"), mtype.toChars());
                    return error.invoke();
                }
                return t.value;
            }
        };
        Function1<TypeStruct,Type> visitStruct = new Function1<TypeStruct,Type>(){
            public Type invoke(TypeStruct mtype) {
                if (mtype.deco != null)
                {
                    if ((sc != null) && ((sc.get()).cppmangle != CPPMANGLE.def))
                    {
                        if ((mtype.cppmangle == CPPMANGLE.def))
                        {
                            mtype.cppmangle = (sc.get()).cppmangle;
                        }
                    }
                    return mtype;
                }
                assert(mtype.sym.parent.value != null);
                if (((mtype.sym.type.ty & 0xFF) == ENUMTY.Terror))
                {
                    return error.invoke();
                }
                if ((sc != null) && ((sc.get()).cppmangle != CPPMANGLE.def))
                {
                    mtype.cppmangle = (sc.get()).cppmangle;
                }
                else
                {
                    mtype.cppmangle = CPPMANGLE.asStruct;
                }
                return merge(mtype);
            }
        };
        Function1<TypeEnum,Type> visitEnum = new Function1<TypeEnum,Type>(){
            public Type invoke(TypeEnum mtype) {
                return mtype.deco != null ? mtype : merge(mtype);
            }
        };
        Function1<TypeClass,Type> visitClass = new Function1<TypeClass,Type>(){
            public Type invoke(TypeClass mtype) {
                if (mtype.deco != null)
                {
                    if ((sc != null) && ((sc.get()).cppmangle != CPPMANGLE.def))
                    {
                        if ((mtype.cppmangle == CPPMANGLE.def))
                        {
                            mtype.cppmangle = (sc.get()).cppmangle;
                        }
                    }
                    return mtype;
                }
                assert(mtype.sym.parent.value != null);
                if (((mtype.sym.type.ty & 0xFF) == ENUMTY.Terror))
                {
                    return error.invoke();
                }
                if ((sc != null) && ((sc.get()).cppmangle != CPPMANGLE.def))
                {
                    mtype.cppmangle = (sc.get()).cppmangle;
                }
                else
                {
                    mtype.cppmangle = CPPMANGLE.asClass;
                }
                return merge(mtype);
            }
        };
        Function1<TypeTuple,Type> visitTuple = new Function1<TypeTuple,Type>(){
            public Type invoke(TypeTuple mtype) {
                if (mtype.deco == null)
                {
                    mtype.deco = pcopy(merge(mtype).deco);
                }
                return mtype;
            }
        };
        Function1<TypeSlice,Type> visitSlice = new Function1<TypeSlice,Type>(){
            public Type invoke(TypeSlice mtype) {
                Type tn = typeSemantic(mtype.next.value, loc, sc);
                Type tbn = tn.toBasetype();
                if (((tbn.ty & 0xFF) != ENUMTY.Ttuple))
                {
                    error(loc, new BytePtr("can only slice tuple types, not `%s`"), tbn.toChars());
                    return error.invoke();
                }
                TypeTuple tt = (TypeTuple)tbn;
                mtype.lwr = semanticLength(sc, tbn, mtype.lwr);
                mtype.upr = semanticLength(sc, tbn, mtype.upr);
                mtype.lwr = mtype.lwr.ctfeInterpret();
                mtype.upr = mtype.upr.ctfeInterpret();
                if (((mtype.lwr.op & 0xFF) == 127) || ((mtype.upr.op & 0xFF) == 127))
                {
                    return error.invoke();
                }
                long i1 = mtype.lwr.toUInteger();
                long i2 = mtype.upr.toUInteger();
                if (!((i1 <= i2) && (i2 <= (long)(tt.arguments.get()).length)))
                {
                    error(loc, new BytePtr("slice `[%llu..%llu]` is out of range of `[0..%llu]`"), i1, i2, (long)(tt.arguments.get()).length);
                    return error.invoke();
                }
                mtype.next.value = tn;
                mtype.transitive();
                Ptr<DArray<Parameter>> args = refPtr(new DArray<Parameter>());
                (args.get()).reserve((int)(i2 - i1));
                {
                    Slice<Parameter> __r1649 = (tt.arguments.get()).opSlice((int)i1, (int)i2).copy();
                    int __key1650 = 0;
                    for (; (__key1650 < __r1649.getLength());__key1650 += 1) {
                        Parameter arg = __r1649.get(__key1650);
                        (args.get()).push(arg);
                    }
                }
                Type t = new TypeTuple(args);
                return typeSemantic(t, loc, sc);
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
                        {
                            return type;
                        }
                        /*goto default*/ { __dispatch10 = -1; continue dispatched_10; }
                    default:
                    __dispatch10 = 0;
                    if ((type.nextOf() != null) && (type.nextOf().deco == null))
                    {
                        return type;
                    }
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
        Function1<Type,Expression> visitType = new Function1<Type,Expression>(){
            public Expression invoke(Type mt) {
                Expression e = null;
                if ((pequals(ident, Id.__sizeof)))
                {
                    long sz = mt.size(loc);
                    if ((sz == -1L))
                    {
                        return new ErrorExp();
                    }
                    e = new IntegerExp(loc, sz, Type.tsize_t);
                }
                else if ((pequals(ident, Id.__xalignof)))
                {
                    int explicitAlignment = mt.alignment();
                    int naturalAlignment = mt.alignsize();
                    int actualAlignment = (explicitAlignment == -1) ? naturalAlignment : explicitAlignment;
                    e = new IntegerExp(loc, (long)actualAlignment, Type.tsize_t);
                }
                else if ((pequals(ident, Id._init)))
                {
                    Type tb = mt.toBasetype();
                    e = mt.defaultInitLiteral(loc);
                    if (((tb.ty & 0xFF) == ENUMTY.Tstruct) && tb.needsNested())
                    {
                        e.isStructLiteralExp().useStaticInit = true;
                    }
                }
                else if ((pequals(ident, Id._mangleof)))
                {
                    if (mt.deco == null)
                    {
                        error(loc, new BytePtr("forward reference of type `%s.mangleof`"), mt.toChars());
                        e = new ErrorExp();
                    }
                    else
                    {
                        e = new StringExp(loc, mt.deco);
                        Ref<Scope> sc = ref(new Scope().copy());
                        e = expressionSemantic(e, ptr(sc));
                    }
                }
                else if ((pequals(ident, Id.stringof)))
                {
                    BytePtr s = pcopy(mt.toChars());
                    e = new StringExp(loc, s);
                    Ref<Scope> sc = ref(new Scope().copy());
                    e = expressionSemantic(e, ptr(sc));
                }
                else if ((flag != 0) && (!pequals(mt, Type.terror)))
                {
                    return null;
                }
                else
                {
                    Dsymbol s = null;
                    if (((mt.ty & 0xFF) == ENUMTY.Tstruct) || ((mt.ty & 0xFF) == ENUMTY.Tclass) || ((mt.ty & 0xFF) == ENUMTY.Tenum))
                    {
                        s = mt.toDsymbol(null);
                    }
                    if (s != null)
                    {
                        s = s.search_correct(ident);
                    }
                    if ((!pequals(mt, Type.terror)))
                    {
                        if (s != null)
                        {
                            error(loc, new BytePtr("no property `%s` for type `%s`, did you mean `%s`?"), ident.toChars(), mt.toChars(), s.toPrettyChars(false));
                        }
                        else
                        {
                            if ((pequals(ident, Id.call)) && ((mt.ty & 0xFF) == ENUMTY.Tclass))
                            {
                                error(loc, new BytePtr("no property `%s` for type `%s`, did you mean `new %s`?"), ident.toChars(), mt.toChars(), mt.toPrettyChars(false));
                            }
                            else
                            {
                                error(loc, new BytePtr("no property `%s` for type `%s`"), ident.toChars(), mt.toChars());
                            }
                        }
                    }
                    e = new ErrorExp();
                }
                return e;
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
                if ((pequals(ident, Id.max)))
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
                else if ((pequals(ident, Id.min)))
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
                else if ((pequals(ident, Id.min_normal)))
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
                else if ((pequals(ident, Id.nan)))
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
                else if ((pequals(ident, Id.infinity)))
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
                else if ((pequals(ident, Id.dig)))
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
                else if ((pequals(ident, Id.epsilon)))
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
                else if ((pequals(ident, Id.mant_dig)))
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
                else if ((pequals(ident, Id.max_10_exp)))
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
                else if ((pequals(ident, Id.max_exp)))
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
                else if ((pequals(ident, Id.min_10_exp)))
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
                else if ((pequals(ident, Id.min_exp)))
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
                return visitType.invoke(mt);
            }
        };
        Function1<TypeEnum,Expression> visitEnum = new Function1<TypeEnum,Expression>(){
            public Expression invoke(TypeEnum mt) {
                Expression e = null;
                if ((pequals(ident, Id.max)) || (pequals(ident, Id.min)))
                {
                    return mt.sym.getMaxMinValue(loc, ident);
                }
                else if ((pequals(ident, Id._init)))
                {
                    e = mt.defaultInitLiteral(loc);
                }
                else if ((pequals(ident, Id.stringof)))
                {
                    BytePtr s = pcopy(mt.toChars());
                    e = new StringExp(loc, s);
                    Ref<Scope> sc = ref(new Scope().copy());
                    e = expressionSemantic(e, ptr(sc));
                }
                else if ((pequals(ident, Id._mangleof)))
                {
                    e = visitType.invoke(mt);
                }
                else
                {
                    e = getProperty(mt.toBasetype(), loc, ident, flag);
                }
                return e;
            }
        };
        Function1<TypeTuple,Expression> visitTuple = new Function1<TypeTuple,Expression>(){
            public Expression invoke(TypeTuple mt) {
                Expression e = null;
                if ((pequals(ident, Id.length)))
                {
                    e = new IntegerExp(loc, (long)(mt.arguments.get()).length, Type.tsize_t);
                }
                else if ((pequals(ident, Id._init)))
                {
                    e = mt.defaultInitLiteral(loc);
                }
                else if (flag != 0)
                {
                    e = null;
                }
                else
                {
                    error(loc, new BytePtr("no property `%s` for tuple `%s`"), ident.toChars(), mt.toChars());
                    e = new ErrorExp();
                }
                return e;
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
                        pt.set(0, Type.terror);
                        return ;
                    case 20:
                        pt.set(0, e.type.value);
                        return ;
                    case 26:
                        s = ((VarExp)e).var;
                        if (s.isVarDeclaration() != null)
                        {
                            /*goto default*/ { __dispatch24 = -1; continue dispatched_24; }
                        }
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
        Ref<Ptr<Scope>> sc_ref = ref(sc);
        Function1<Expression,Void> returnExp = new Function1<Expression,Void>(){
            public Void invoke(Expression e) {
                pt.set(0, null);
                pe.set(0, e);
                ps.set(0, null);
                return null;
            }
        };
        Function1<Type,Void> returnType = new Function1<Type,Void>(){
            public Void invoke(Type t) {
                pt.set(0, t);
                pe.set(0, null);
                ps.set(0, null);
                return null;
            }
        };
        Function1<Dsymbol,Void> returnSymbol = new Function1<Dsymbol,Void>(){
            public Void invoke(Dsymbol s) {
                pt.set(0, null);
                pe.set(0, null);
                ps.set(0, s);
                return null;
            }
        };
        Function0<Void> returnError = new Function0<Void>(){
            public Void invoke() {
                returnType.invoke(Type.terror);
                return null;
            }
        };
        Function1<Type,Void> visitType = new Function1<Type,Void>(){
            public Void invoke(Type mt) {
                Type t = typeSemantic(mt, loc, sc_ref.value);
                assert(t != null);
                returnType.invoke(t);
                return null;
            }
        };
        Function1<TypeSArray,Void> visitSArray = new Function1<TypeSArray,Void>(){
            public Void invoke(TypeSArray mt) {
                resolve(mt.next.value, loc, sc_ref.value, pe, pt, ps, intypeid);
                if (pe.get() != null)
                {
                    {
                        Dsymbol s = getDsymbol(pe.get());
                        if ((s) != null)
                        {
                            pe.set(0, (new DsymbolExp(loc, s, true)));
                        }
                    }
                    returnExp.invoke(new ArrayExp(loc, pe.get(), mt.dim));
                }
                else if (ps.get() != null)
                {
                    Dsymbol s = ps.get();
                    {
                        TupleDeclaration tup = s.isTupleDeclaration();
                        if ((tup) != null)
                        {
                            mt.dim = semanticLength(sc_ref.value, tup, mt.dim);
                            mt.dim = mt.dim.ctfeInterpret();
                            if (((mt.dim.op & 0xFF) == 127))
                            {
                                returnError.invoke();
                                return null;
                            }
                            long d = mt.dim.toUInteger();
                            if ((d >= (long)(tup.objects.get()).length))
                            {
                                error(loc, new BytePtr("tuple index `%llu` exceeds length %u"), d, (tup.objects.get()).length);
                                returnError.invoke();
                                return null;
                            }
                            RootObject o = (tup.objects.get()).get((int)d);
                            if ((o.dyncast() == DYNCAST.dsymbol))
                            {
                                returnSymbol.invoke((Dsymbol)o);
                                return null;
                            }
                            if ((o.dyncast() == DYNCAST.expression))
                            {
                                Expression e = (Expression)o;
                                if (((e.op & 0xFF) == 41))
                                {
                                    returnSymbol.invoke(((DsymbolExp)e).s);
                                    return null;
                                }
                                else
                                {
                                    returnExp.invoke(e);
                                    return null;
                                }
                            }
                            if ((o.dyncast() == DYNCAST.type))
                            {
                                returnType.invoke(((Type)o).addMod(mt.mod));
                                return null;
                            }
                            Ptr<DArray<RootObject>> objects = refPtr(new DArray<RootObject>(1));
                            objects.get().set(0, o);
                            returnSymbol.invoke(new TupleDeclaration(loc, tup.ident, objects));
                            return null;
                        }
                        else
                        {
                            visitType.invoke(mt);
                            return null;
                        }
                    }
                }
                else
                {
                    if ((((pt.get()).ty & 0xFF) != ENUMTY.Terror))
                    {
                        mt.next.value = pt.get();
                    }
                    visitType.invoke(mt);
                }
            }
        };
        Function1<TypeDArray,Void> visitDArray = new Function1<TypeDArray,Void>(){
            public Void invoke(TypeDArray mt) {
                resolve(mt.next.value, loc, sc_ref.value, pe, pt, ps, intypeid);
                if (pe.get() != null)
                {
                    {
                        Dsymbol s = getDsymbol(pe.get());
                        if ((s) != null)
                        {
                            pe.set(0, (new DsymbolExp(loc, s, true)));
                        }
                    }
                    returnExp.invoke(new ArrayExp(loc, pe.get(), null));
                }
                else if (ps.get() != null)
                {
                    {
                        TupleDeclaration tup = (ps.get()).isTupleDeclaration();
                        if ((tup) != null)
                        {
                        }
                        else
                        {
                            visitType.invoke(mt);
                        }
                    }
                }
                else
                {
                    if ((((pt.get()).ty & 0xFF) != ENUMTY.Terror))
                    {
                        mt.next.value = pt.get();
                    }
                    visitType.invoke(mt);
                }
                return null;
            }
        };
        Function1<TypeAArray,Void> visitAArray = new Function1<TypeAArray,Void>(){
            public Void invoke(TypeAArray mt) {
                if (((mt.index.ty & 0xFF) == ENUMTY.Tident) || ((mt.index.ty & 0xFF) == ENUMTY.Tinstance) || ((mt.index.ty & 0xFF) == ENUMTY.Tsarray))
                {
                    Ref<Expression> e = ref(null);
                    Ref<Type> t = ref(null);
                    Ref<Dsymbol> s = ref(null);
                    resolve(mt.index, loc, sc_ref.value, ptr(e), ptr(t), ptr(s), intypeid);
                    if (e.value != null)
                    {
                        TypeSArray tsa = new TypeSArray(mt.next.value, e.value);
                        tsa.mod = mt.mod;
                        resolve(tsa, loc, sc_ref.value, pe, pt, ps, intypeid);
                        return null;
                    }
                    else if (t.value != null)
                    {
                        mt.index = t.value;
                    }
                    else
                    {
                        error(loc, new BytePtr("index is not a type or an expression"));
                    }
                }
                visitType.invoke(mt);
            }
        };
        Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>(){
            public Void invoke(Dsymbol s) {
                semanticOnMixin.invoke(s);
                return null;
            }
        };
        Function1<TypeIdentifier,Void> visitIdentifier = new Function1<TypeIdentifier,Void>(){
            public Void invoke(TypeIdentifier mt) {
                if (mt.ident.equals(Id._super) || mt.ident.equals(Id.This) && (hasThis(sc_ref.value) == null))
                {
                    if (mt.ident.equals(Id._super))
                    {
                        error(mt.loc, new BytePtr("Using `super` as a type is obsolete. Use `typeof(super)` instead"));
                    }
                    if (mt.ident.equals(Id.This))
                    {
                        error(mt.loc, new BytePtr("Using `this` as a type is obsolete. Use `typeof(this)` instead"));
                    }
                    {
                        AggregateDeclaration ad = (sc_ref.value.get()).getStructClassScope();
                        if ((ad) != null)
                        {
                            {
                                ClassDeclaration cd = ad.isClassDeclaration();
                                if ((cd) != null)
                                {
                                    if (mt.ident.equals(Id.This))
                                    {
                                        mt.ident = cd.ident;
                                    }
                                    else if ((cd.baseClass != null) && mt.ident.equals(Id._super))
                                    {
                                        mt.ident = cd.baseClass.ident;
                                    }
                                }
                                else
                                {
                                    StructDeclaration sd = ad.isStructDeclaration();
                                    if ((sd != null) && mt.ident.equals(Id.This))
                                    {
                                        mt.ident = sd.ident;
                                    }
                                }
                            }
                        }
                    }
                }
                if ((pequals(mt.ident, Id.ctfe)))
                {
                    error(loc, new BytePtr("variable `__ctfe` cannot be read at compile time"));
                    returnError.invoke();
                    return null;
                }
                Ref<Dsymbol> scopesym = ref(null);
                Dsymbol s = (sc_ref.value.get()).search(loc, mt.ident, ptr(scopesym), 0);
                if ((s == null) && ((sc_ref.value.get()).enclosing != null))
                {
                    ScopeDsymbol sds = ((sc_ref.value.get()).enclosing.get()).scopesym;
                    if ((sds != null) && (sds.members != null))
                    {
                        Function1<Dsymbol,Void> semanticOnMixin = new Function1<Dsymbol,Void>(){
                            public Void invoke(Dsymbol member) {
                                Ref<Dsymbol> member_ref = ref(member);
                                {
                                    Ref<CompileDeclaration> compileDecl = ref(member_ref.value.isCompileDeclaration());
                                    if ((compileDecl.value) != null)
                                    {
                                        dsymbolSemantic(compileDecl.value, sc_ref.value);
                                    }
                                    else {
                                        Ref<TemplateMixin> mixinTempl = ref(member_ref.value.isTemplateMixin());
                                        if ((mixinTempl.value) != null)
                                        {
                                            dsymbolSemantic(mixinTempl.value, sc_ref.value);
                                        }
                                    }
                                }
                                return null;
                            }
                        };
                        foreachDsymbol(sds.members, __lambda3);
                        s = (sc_ref.value.get()).search(loc, mt.ident, ptr(scopesym), 0);
                    }
                }
                if (s != null)
                {
                    {
                        FuncDeclaration f = s.isFuncDeclaration();
                        if ((f) != null)
                        {
                            {
                                TemplateDeclaration td = getFuncTemplateDecl(f);
                                if ((td) != null)
                                {
                                    if (td.overroot != null)
                                    {
                                        td = td.overroot;
                                    }
                                    s = td;
                                }
                            }
                        }
                    }
                }
                resolveHelper(mt, loc, sc_ref.value, s, scopesym.value, pe, pt, ps, intypeid);
                if (pt.get() != null)
                {
                    pt.set(0, (pt.get()).addMod(mt.mod));
                }
            }
        };
        Function1<TypeInstance,Void> visitInstance = new Function1<TypeInstance,Void>(){
            public Void invoke(TypeInstance mt) {
                dsymbolSemantic(mt.tempinst, sc_ref.value);
                if ((global.value.gag == 0) && mt.tempinst.errors)
                {
                    returnError.invoke();
                    return null;
                }
                resolveHelper(mt, loc, sc_ref.value, mt.tempinst, null, pe, pt, ps, intypeid);
                if (pt.get() != null)
                {
                    pt.set(0, (pt.get()).addMod(mt.mod));
                }
            }
        };
        Function1<TypeTypeof,Void> visitTypeof = new Function1<TypeTypeof,Void>(){
            public Void invoke(TypeTypeof mt) {
                if ((sc_ref.value == null))
                {
                    error(loc, new BytePtr("Invalid scope."));
                    returnError.invoke();
                    return null;
                }
                if (mt.inuse != 0)
                {
                    mt.inuse = 2;
                    error(loc, new BytePtr("circular `typeof` definition"));
                /*Lerr:*/
                    mt.inuse--;
                    returnError.invoke();
                    return null;
                }
                mt.inuse++;
                Ptr<Scope> sc2 = (sc_ref.value.get()).push();
                (sc2.get()).intypeof = 1;
                Expression exp2 = expressionSemantic(mt.exp, sc2);
                exp2 = resolvePropertiesOnly(sc2, exp2);
                (sc2.get()).pop();
                if (((exp2.op & 0xFF) == 127))
                {
                    if (global.value.gag == 0)
                    {
                        mt.exp = exp2;
                    }
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                mt.exp = exp2;
                if (((mt.exp.op & 0xFF) == 20) || ((mt.exp.op & 0xFF) == 203))
                {
                    if (mt.exp.checkType())
                    {
                        /*goto Lerr*/throw Dispatch0.INSTANCE;
                    }
                }
                {
                    FuncDeclaration f = ((mt.exp.op & 0xFF) == 26) ? ((VarExp)mt.exp).var.isFuncDeclaration() : ((mt.exp.op & 0xFF) == 27) ? ((DotVarExp)mt.exp).var.isFuncDeclaration() : null;
                    if ((f) != null)
                    {
                        if (f.checkForwardRef(loc))
                        {
                            /*goto Lerr*/throw Dispatch0.INSTANCE;
                        }
                    }
                }
                {
                    FuncDeclaration f = isFuncAddress(mt.exp, null);
                    if ((f) != null)
                    {
                        if (f.checkForwardRef(loc))
                        {
                            /*goto Lerr*/throw Dispatch0.INSTANCE;
                        }
                    }
                }
                Type t = mt.exp.type.value;
                if (t == null)
                {
                    error(loc, new BytePtr("expression `%s` has no type"), mt.exp.toChars());
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                if (((t.ty & 0xFF) == ENUMTY.Ttypeof))
                {
                    error(loc, new BytePtr("forward reference to `%s`"), mt.toChars());
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                if ((mt.idents.length == 0))
                {
                    returnType.invoke(t.addMod(mt.mod));
                }
                else
                {
                    {
                        Dsymbol s = t.toDsymbol(sc_ref.value);
                        if ((s) != null)
                        {
                            resolveHelper(mt, loc, sc_ref.value, s, null, pe, pt, ps, intypeid);
                        }
                        else
                        {
                            Expression e = typeToExpressionHelper(mt, new TypeExp(loc, t), 0);
                            e = expressionSemantic(e, sc_ref.value);
                            resolveExp(e, pt, pe, ps);
                        }
                    }
                    if (pt.get() != null)
                    {
                        pt.set(0, (pt.get()).addMod(mt.mod));
                    }
                }
                mt.inuse--;
            }
        };
        Function1<TypeReturn,Void> visitReturn = new Function1<TypeReturn,Void>(){
            public Void invoke(TypeReturn mt) {
                Type t = null;
                {
                    FuncDeclaration func = (sc_ref.value.get()).func;
                    if (func == null)
                    {
                        error(loc, new BytePtr("`typeof(return)` must be inside function"));
                        returnError.invoke();
                        return null;
                    }
                    if (func.fes != null)
                    {
                        func = func.fes.func;
                    }
                    t = func.type.nextOf();
                    if (t == null)
                    {
                        error(loc, new BytePtr("cannot use `typeof(return)` inside function `%s` with inferred return type"), (sc_ref.value.get()).func.toChars());
                        returnError.invoke();
                        return null;
                    }
                }
                if ((mt.idents.length == 0))
                {
                    returnType.invoke(t.addMod(mt.mod));
                    return null;
                }
                else
                {
                    {
                        Dsymbol s = t.toDsymbol(sc_ref.value);
                        if ((s) != null)
                        {
                            resolveHelper(mt, loc, sc_ref.value, s, null, pe, pt, ps, intypeid);
                        }
                        else
                        {
                            Expression e = typeToExpressionHelper(mt, new TypeExp(loc, t), 0);
                            e = expressionSemantic(e, sc_ref.value);
                            resolveExp(e, pt, pe, ps);
                        }
                    }
                    if (pt.get() != null)
                    {
                        pt.set(0, (pt.get()).addMod(mt.mod));
                    }
                }
            }
        };
        Function1<TypeSlice,Void> visitSlice = new Function1<TypeSlice,Void>(){
            public Void invoke(TypeSlice mt) {
                resolve(mt.next.value, loc, sc_ref.value, pe, pt, ps, intypeid);
                if (pe.get() != null)
                {
                    {
                        Dsymbol s = getDsymbol(pe.get());
                        if ((s) != null)
                        {
                            pe.set(0, (new DsymbolExp(loc, s, true)));
                        }
                    }
                    returnExp.invoke(new ArrayExp(loc, pe.get(), new IntervalExp(loc, mt.lwr, mt.upr)));
                    return null;
                }
                else if (ps.get() != null)
                {
                    Dsymbol s = ps.get();
                    TupleDeclaration td = s.isTupleDeclaration();
                    if (td != null)
                    {
                        ScopeDsymbol sym = new ArrayScopeSymbol(sc_ref.value, td);
                        sym.parent.value = (sc_ref.value.get()).scopesym;
                        sc_ref.value = (sc_ref.value.get()).push(sym);
                        sc_ref.value = (sc_ref.value.get()).startCTFE();
                        mt.lwr = expressionSemantic(mt.lwr, sc_ref.value);
                        mt.upr = expressionSemantic(mt.upr, sc_ref.value);
                        sc_ref.value = (sc_ref.value.get()).endCTFE();
                        sc_ref.value = (sc_ref.value.get()).pop();
                        mt.lwr = mt.lwr.ctfeInterpret();
                        mt.upr = mt.upr.ctfeInterpret();
                        long i1 = mt.lwr.toUInteger();
                        long i2 = mt.upr.toUInteger();
                        if (!((i1 <= i2) && (i2 <= (long)(td.objects.get()).length)))
                        {
                            error(loc, new BytePtr("slice `[%llu..%llu]` is out of range of [0..%u]"), i1, i2, (td.objects.get()).length);
                            returnError.invoke();
                            return null;
                        }
                        if ((i1 == 0L) && (i2 == (long)(td.objects.get()).length))
                        {
                            returnSymbol.invoke(td);
                            return null;
                        }
                        Ptr<DArray<RootObject>> objects = refPtr(new DArray<RootObject>((int)(i2 - i1)));
                        {
                            int i = 0;
                            for (; (i < (objects.get()).length);i++){
                                objects.get().set(i, (td.objects.get()).get((int)i1 + i));
                            }
                        }
                        returnSymbol.invoke(new TupleDeclaration(loc, td.ident, objects));
                        return null;
                    }
                    else
                    {
                        visitType.invoke(mt);
                    }
                }
                else
                {
                    if ((((pt.get()).ty & 0xFF) != ENUMTY.Terror))
                    {
                        mt.next.value = pt.get();
                    }
                    visitType.invoke(mt);
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
        Function1<Type,Expression> visitType = new Function1<Type,Expression>(){
            public Expression invoke(Type mt) {
                VarDeclaration v = null;
                Expression ex = e;
                for (; ((ex.op & 0xFF) == 99);) {
                    ex = ((CommaExp)ex).e2.value;
                }
                if (((ex.op & 0xFF) == 27))
                {
                    DotVarExp dv = (DotVarExp)ex;
                    v = dv.var.isVarDeclaration();
                }
                else if (((ex.op & 0xFF) == 26))
                {
                    VarExp ve = (VarExp)ex;
                    v = ve.var.isVarDeclaration();
                }
                try {
                    if (v != null)
                    {
                        if ((pequals(ident, Id.offsetof)))
                        {
                            if (v.isField())
                            {
                                AggregateDeclaration ad = v.toParent().isAggregateDeclaration();
                                objc().checkOffsetof(e, ad);
                                ad.size(e.loc);
                                if ((ad.sizeok != Sizeok.done))
                                {
                                    return new ErrorExp();
                                }
                                return new IntegerExp(e.loc, (long)v.offset, Type.tsize_t);
                            }
                        }
                        else if ((pequals(ident, Id._init)))
                        {
                            Type tb = mt.toBasetype();
                            e = mt.defaultInitLiteral(e.loc);
                            if (((tb.ty & 0xFF) == ENUMTY.Tstruct) && tb.needsNested())
                            {
                                e.isStructLiteralExp().useStaticInit = true;
                            }
                            /*goto Lreturn*/throw Dispatch0.INSTANCE;
                        }
                    }
                    if ((pequals(ident, Id.stringof)))
                    {
                        BytePtr s = pcopy(e.toChars());
                        e = new StringExp(e.loc, s);
                    }
                    else
                    {
                        e = getProperty(mt, e.loc, ident, flag & DotExpFlag.gag);
                    }
                }
                catch(Dispatch0 __d){}
            /*Lreturn:*/
                if (e != null)
                {
                    e = expressionSemantic(e, sc);
                }
                return e;
            }
        };
        Function1<TypeError,Expression> visitError = new Function1<TypeError,Expression>(){
            public Expression invoke(TypeError _param_0) {
                return new ErrorExp();
            }
        };
        Function1<TypeBasic,Expression> visitBasic = new Function1<TypeBasic,Expression>(){
            public Expression invoke(TypeBasic mt) {
                Type t = null;
                if ((pequals(ident, Id.re)))
                {
                    {
                        int __dispatch26 = 0;
                        dispatched_26:
                        do {
                            switch (__dispatch26 != 0 ? __dispatch26 : (mt.ty & 0xFF))
                            {
                                case 27:
                                    t = Type.tfloat32;
                                    /*goto L1*/{ __dispatch26 = -1; continue dispatched_26; }
                                case 28:
                                    t = Type.tfloat64;
                                    /*goto L1*/{ __dispatch26 = -1; continue dispatched_26; }
                                case 29:
                                    t = Type.tfloat80;
                                    /*goto L1*/{ __dispatch26 = -1; continue dispatched_26; }
                                /*L1:*/
                                case -1:
                                __dispatch26 = 0;
                                    e = e.castTo(sc, t);
                                    break;
                                case 21:
                                case 22:
                                case 23:
                                    break;
                                case 24:
                                    t = Type.tfloat32;
                                    /*goto L2*/{ __dispatch26 = -2; continue dispatched_26; }
                                case 25:
                                    t = Type.tfloat64;
                                    /*goto L2*/{ __dispatch26 = -2; continue dispatched_26; }
                                case 26:
                                    t = Type.tfloat80;
                                    /*goto L2*/{ __dispatch26 = -2; continue dispatched_26; }
                                /*L2:*/
                                case -2:
                                __dispatch26 = 0;
                                    e = new RealExp(e.loc, CTFloat.zero, t);
                                    break;
                                default:
                                e = getProperty(mt.Type, e.loc, ident, flag);
                                break;
                            }
                        } while(__dispatch26 != 0);
                    }
                }
                else if ((pequals(ident, Id.im)))
                {
                    Type t2 = null;
                    {
                        int __dispatch27 = 0;
                        dispatched_27:
                        do {
                            switch (__dispatch27 != 0 ? __dispatch27 : (mt.ty & 0xFF))
                            {
                                case 27:
                                    t = Type.timaginary32;
                                    t2 = Type.tfloat32;
                                    /*goto L3*/{ __dispatch27 = -1; continue dispatched_27; }
                                case 28:
                                    t = Type.timaginary64;
                                    t2 = Type.tfloat64;
                                    /*goto L3*/{ __dispatch27 = -1; continue dispatched_27; }
                                case 29:
                                    t = Type.timaginary80;
                                    t2 = Type.tfloat80;
                                    /*goto L3*/{ __dispatch27 = -1; continue dispatched_27; }
                                /*L3:*/
                                case -1:
                                __dispatch27 = 0;
                                    e = e.castTo(sc, t);
                                    e.type.value = t2;
                                    break;
                                case 24:
                                    t = Type.tfloat32;
                                    /*goto L4*/{ __dispatch27 = -2; continue dispatched_27; }
                                case 25:
                                    t = Type.tfloat64;
                                    /*goto L4*/{ __dispatch27 = -2; continue dispatched_27; }
                                case 26:
                                    t = Type.tfloat80;
                                    /*goto L4*/{ __dispatch27 = -2; continue dispatched_27; }
                                /*L4:*/
                                case -2:
                                __dispatch27 = 0;
                                    e = e.copy();
                                    e.type.value = t;
                                    break;
                                case 21:
                                case 22:
                                case 23:
                                    e = new RealExp(e.loc, CTFloat.zero, mt);
                                    break;
                                default:
                                e = getProperty(mt.Type, e.loc, ident, flag);
                                break;
                            }
                        } while(__dispatch27 != 0);
                    }
                }
                else
                {
                    return visitType.invoke(mt);
                }
                if (((flag & 1) == 0) || (e != null))
                {
                    e = expressionSemantic(e, sc);
                }
                return e;
            }
        };
        Function1<TypeVector,Expression> visitVector = new Function1<TypeVector,Expression>(){
            public Expression invoke(TypeVector mt) {
                if ((pequals(ident, Id.ptr)) && ((e.op & 0xFF) == 18))
                {
                    e = new AddrExp(e.loc, e);
                    e = expressionSemantic(e, sc);
                    return e.castTo(sc, mt.basetype.nextOf().pointerTo());
                }
                if ((pequals(ident, Id.array)))
                {
                    e = new VectorArrayExp(e.loc, e);
                    e = expressionSemantic(e, sc);
                    return e;
                }
                if ((pequals(ident, Id._init)) || (pequals(ident, Id.offsetof)) || (pequals(ident, Id.stringof)) || (pequals(ident, Id.__xalignof)))
                {
                    return visitType.invoke(mt);
                }
                return dotExp(mt.basetype, sc, e.castTo(sc, mt.basetype), ident, flag);
            }
        };
        Function1<TypeArray,Expression> visitArray = new Function1<TypeArray,Expression>(){
            public Expression invoke(TypeArray mt) {
                e = visitType.invoke(mt);
                if (((flag & 1) == 0) || (e != null))
                {
                    e = expressionSemantic(e, sc);
                }
                return e;
            }
        };
        Function1<TypeSArray,Expression> visitSArray = new Function1<TypeSArray,Expression>(){
            public Expression invoke(TypeSArray mt) {
                if ((pequals(ident, Id.length)))
                {
                    Loc oldLoc = e.loc.copy();
                    e = mt.dim.copy();
                    e.loc = oldLoc.copy();
                }
                else if ((pequals(ident, Id.ptr)))
                {
                    if (((e.op & 0xFF) == 20))
                    {
                        e.error(new BytePtr("`%s` is not an expression"), e.toChars());
                        return new ErrorExp();
                    }
                    else if (((flag & DotExpFlag.noDeref) == 0) && ((sc.get()).func != null) && ((sc.get()).intypeof == 0) && (sc.get()).func.setUnsafe() && (((sc.get()).flags & 8) == 0))
                    {
                        e.error(new BytePtr("`%s.ptr` cannot be used in `@safe` code, use `&%s[0]` instead"), e.toChars(), e.toChars());
                        return new ErrorExp();
                    }
                    e = e.castTo(sc, e.type.value.nextOf().pointerTo());
                }
                else
                {
                    e = visitArray.invoke(mt);
                }
                if (((flag & 1) == 0) || (e != null))
                {
                    e = expressionSemantic(e, sc);
                }
                return e;
            }
        };
        Function1<TypeDArray,Expression> visitDArray = new Function1<TypeDArray,Expression>(){
            public Expression invoke(TypeDArray mt) {
                if (((e.op & 0xFF) == 20) && (pequals(ident, Id.length)) || (pequals(ident, Id.ptr)))
                {
                    e.error(new BytePtr("`%s` is not an expression"), e.toChars());
                    return new ErrorExp();
                }
                if ((pequals(ident, Id.length)))
                {
                    if (((e.op & 0xFF) == 121))
                    {
                        StringExp se = (StringExp)e;
                        return new IntegerExp(se.loc, (long)se.len, Type.tsize_t);
                    }
                    if (((e.op & 0xFF) == 13))
                    {
                        return new IntegerExp(e.loc, 0L, Type.tsize_t);
                    }
                    if (checkNonAssignmentArrayOp(e, false))
                    {
                        return new ErrorExp();
                    }
                    e = new ArrayLengthExp(e.loc, e);
                    e.type.value = Type.tsize_t;
                    return e;
                }
                else if ((pequals(ident, Id.ptr)))
                {
                    if (((flag & DotExpFlag.noDeref) == 0) && ((sc.get()).func != null) && ((sc.get()).intypeof == 0) && (sc.get()).func.setUnsafe() && (((sc.get()).flags & 8) == 0))
                    {
                        e.error(new BytePtr("`%s.ptr` cannot be used in `@safe` code, use `&%s[0]` instead"), e.toChars(), e.toChars());
                        return new ErrorExp();
                    }
                    return e.castTo(sc, mt.next.value.pointerTo());
                }
                else
                {
                    return visitArray.invoke(mt);
                }
            }
        };
        Function1<TypeAArray,Expression> visitAArray = new Function1<TypeAArray,Expression>(){
            public Expression invoke(TypeAArray mt) {
                if ((pequals(ident, Id.length)))
                {
                    if ((typesem.visitAArrayfd_aaLen == null))
                    {
                        Ptr<DArray<Parameter>> fparams = refPtr(new DArray<Parameter>());
                        (fparams.get()).push(new Parameter(2048L, mt, null, null, null));
                        typesem.visitAArrayfd_aaLen = FuncDeclaration.genCfunc(fparams, Type.tsize_t, Id.aaLen, 0L);
                        TypeFunction tf = typesem.visitAArrayfd_aaLen.type.toTypeFunction();
                        tf.purity = PURE.const_;
                        tf.isnothrow = true;
                        tf.isnogc = false;
                    }
                    Expression ev = new VarExp(e.loc, typesem.visitAArrayfd_aaLen, false);
                    e = new CallExp(e.loc, ev, e);
                    e.type.value = typesem.visitAArrayfd_aaLen.type.toTypeFunction().next.value;
                    return e;
                }
                else
                {
                    return visitType.invoke(mt);
                }
            }
        };
        Function1<TypeReference,Expression> visitReference = new Function1<TypeReference,Expression>(){
            public Expression invoke(TypeReference mt) {
                return dotExp(mt.next.value, sc, e, ident, flag);
            }
        };
        Function1<TypeDelegate,Expression> visitDelegate = new Function1<TypeDelegate,Expression>(){
            public Expression invoke(TypeDelegate mt) {
                if ((pequals(ident, Id.ptr)))
                {
                    e = new DelegatePtrExp(e.loc, e);
                    e = expressionSemantic(e, sc);
                }
                else if ((pequals(ident, Id.funcptr)))
                {
                    if (((flag & DotExpFlag.noDeref) == 0) && ((sc.get()).func != null) && ((sc.get()).intypeof == 0) && (sc.get()).func.setUnsafe() && (((sc.get()).flags & 8) == 0))
                    {
                        e.error(new BytePtr("`%s.funcptr` cannot be used in `@safe` code"), e.toChars());
                        return new ErrorExp();
                    }
                    e = new DelegateFuncptrExp(e.loc, e);
                    e = expressionSemantic(e, sc);
                }
                else
                {
                    return visitType.invoke(mt);
                }
                return e;
            }
        };
        Function5<Type,Ptr<Scope>,Expression,Identifier,Integer,Expression> noMember = new Function5<Type,Ptr<Scope>,Expression,Identifier,Integer,Expression>(){
            public Expression invoke(Type mt, Ptr<Scope> sc, Expression e, Identifier ident, Integer flag) {
                boolean gagError = ((flag & 1) != 0);
                Function1<Expression,Expression> returnExp = new Function1<Expression,Expression>(){
                    public Expression invoke(Expression e) {
                        Ref<Expression> e_ref = ref(e);
                        typesem.noMembernest.value -= 1;
                        return e_ref.value;
                    }
                };
                if (((typesem.noMembernest.value += 1) > 500))
                {
                    error(e.loc, new BytePtr("cannot resolve identifier `%s`"), ident.toChars());
                    return returnExp.invoke(gagError ? null : new ErrorExp());
                }
                assert(((mt.ty & 0xFF) == ENUMTY.Tstruct) || ((mt.ty & 0xFF) == ENUMTY.Tclass));
                AggregateDeclaration sym = mt.toDsymbol(sc).isAggregateDeclaration();
                assert(sym != null);
                if ((!pequals(ident, Id.__sizeof)) && (!pequals(ident, Id.__xalignof)) && (!pequals(ident, Id._init)) && (!pequals(ident, Id._mangleof)) && (!pequals(ident, Id.stringof)) && (!pequals(ident, Id.offsetof)) && (!pequals(ident, Id.ctor)) && (!pequals(ident, Id.dtor)) && (!pequals(ident, Id.__xdtor)) && (!pequals(ident, Id.postblit)) && (!pequals(ident, Id.__xpostblit)))
                {
                    {
                        Dsymbol fd = search_function(sym, Id.opDot);
                        if ((fd) != null)
                        {
                            e = build_overload(e.loc, sc, e, null, fd);
                            e.deprecation(new BytePtr("`opDot` is deprecated. Use `alias this`"));
                            e = new DotIdExp(e.loc, e, ident);
                            return returnExp.invoke(expressionSemantic(e, sc));
                        }
                    }
                    {
                        Dsymbol fd = search_function(sym, Id.opDispatch);
                        if ((fd) != null)
                        {
                            TemplateDeclaration td = fd.isTemplateDeclaration();
                            if (td == null)
                            {
                                fd.error(new BytePtr("must be a template `opDispatch(string s)`, not a %s"), fd.kind());
                                return returnExp.invoke(new ErrorExp());
                            }
                            StringExp se = new StringExp(e.loc, ident.toChars());
                            Ptr<DArray<RootObject>> tiargs = refPtr(new DArray<RootObject>());
                            (tiargs.get()).push(se);
                            DotTemplateInstanceExp dti = new DotTemplateInstanceExp(e.loc, e, Id.opDispatch, tiargs);
                            dti.ti.tempdecl = td;
                            int errors = gagError ? global.value.startGagging() : 0;
                            e = semanticY(dti, sc, 0);
                            if (gagError && global.value.endGagging(errors))
                            {
                                e = null;
                            }
                            return returnExp.invoke(e);
                        }
                    }
                    Expression alias_e = resolveAliasThis(sc, e, gagError);
                    if ((alias_e != null) && (!pequals(alias_e, e)))
                    {
                        DotIdExp die = new DotIdExp(e.loc, alias_e, ident);
                        int errors = gagError ? 0 : global.value.startGagging();
                        Expression exp = semanticY(die, sc, (gagError ? 1 : 0));
                        if (!gagError)
                        {
                            global.value.endGagging(errors);
                            if ((exp != null) && ((exp.op & 0xFF) == 127))
                            {
                                exp = null;
                            }
                        }
                        if ((exp != null) && gagError)
                        {
                            resolveAliasThis(sc, e, false);
                        }
                        return returnExp.invoke(exp);
                    }
                }
                return returnExp.invoke(visitType.invoke(mt));
            }
        };
        Function1<TypeStruct,Expression> visitStruct = new Function1<TypeStruct,Expression>(){
            public Expression invoke(TypeStruct mt) {
                Dsymbol s = null;
                assert(((e.op & 0xFF) != 97));
                if ((pequals(ident, Id._mangleof)))
                {
                    return getProperty(mt, e.loc, ident, flag & 1);
                }
                if ((pequals(ident, Id._tupleof)))
                {
                    e = expressionSemantic(e, sc);
                    if (!mt.sym.determineFields())
                    {
                        error(e.loc, new BytePtr("unable to determine fields of `%s` because of forward references"), mt.toChars());
                    }
                    Ref<Expression> e0 = ref(null);
                    Expression ev = ((e.op & 0xFF) == 20) ? null : e;
                    if (ev != null)
                    {
                        ev = extractSideEffect(sc, new BytePtr("__tup"), e0, ev, false);
                    }
                    Ptr<DArray<Expression>> exps = refPtr(new DArray<Expression>());
                    (exps.get()).reserve(mt.sym.fields.length);
                    {
                        int i = 0;
                        for (; (i < mt.sym.fields.length);i++){
                            VarDeclaration v = mt.sym.fields.get(i);
                            Expression ex = null;
                            if (ev != null)
                            {
                                ex = new DotVarExp(e.loc, ev, v, true);
                            }
                            else
                            {
                                ex = new VarExp(e.loc, v, true);
                                ex.type.value = ex.type.value.addMod(e.type.value.mod);
                            }
                            (exps.get()).push(ex);
                        }
                    }
                    e = new TupleExp(e.loc, e0.value, exps);
                    Ptr<Scope> sc2 = (sc.get()).push();
                    (sc2.get()).flags |= global.value.params.vsafe ? 1024 : 2;
                    e = expressionSemantic(e, sc2);
                    (sc2.get()).pop();
                    return e;
                }
                int flags = ((sc.get()).flags & 512) != 0 ? 128 : 0;
                s = mt.sym.search(e.loc, ident, flags | 1);
                while(true) try {
                /*L1:*/
                    if (s == null)
                    {
                        return noMember.invoke(mt, sc, e, ident, flag);
                    }
                    if ((((sc.get()).flags & 512) == 0) && !symbolIsVisible(sc, s))
                    {
                        return noMember.invoke(mt, sc, e, ident, flag);
                    }
                    if (s.isFuncDeclaration() == null)
                    {
                        s.checkDeprecated(e.loc, sc);
                        {
                            Declaration d = s.isDeclaration();
                            if ((d) != null)
                            {
                                d.checkDisabled(e.loc, sc, false);
                            }
                        }
                    }
                    s = s.toAlias();
                    {
                        EnumMember em = s.isEnumMember();
                        if ((em) != null)
                        {
                            return em.getVarExp(e.loc, sc);
                        }
                    }
                    {
                        VarDeclaration v = s.isVarDeclaration();
                        if ((v) != null)
                        {
                            if ((v.type == null) || (v.type.deco == null) && (v.inuse != 0))
                            {
                                if (v.inuse != 0)
                                {
                                    e.error(new BytePtr("circular reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                                }
                                else
                                {
                                    e.error(new BytePtr("forward reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                                }
                                return new ErrorExp();
                            }
                            if (((v.type.ty & 0xFF) == ENUMTY.Terror))
                            {
                                return new ErrorExp();
                            }
                            if (((v.storage_class & 8388608L) != 0) && (v._init != null))
                            {
                                if (v.inuse != 0)
                                {
                                    e.error(new BytePtr("circular initialization of %s `%s`"), v.kind(), v.toPrettyChars(false));
                                    return new ErrorExp();
                                }
                                checkAccess(e.loc, sc, null, (Declaration)v);
                                Expression ve = new VarExp(e.loc, v, true);
                                if (!isTrivialExp(e))
                                {
                                    ve = new CommaExp(e.loc, e, ve, true);
                                }
                                return expressionSemantic(ve, sc);
                            }
                        }
                    }
                    {
                        Type t = s.getType();
                        if ((t) != null)
                        {
                            return expressionSemantic(new TypeExp(e.loc, t), sc);
                        }
                    }
                    TemplateMixin tm = s.isTemplateMixin();
                    if (tm != null)
                    {
                        Expression de = new DotExp(e.loc, e, new ScopeExp(e.loc, tm));
                        de.type.value = e.type.value;
                        return de;
                    }
                    TemplateDeclaration td = s.isTemplateDeclaration();
                    if (td != null)
                    {
                        if (((e.op & 0xFF) == 20))
                        {
                            e = new TemplateExp(e.loc, td, null);
                        }
                        else
                        {
                            e = new DotTemplateExp(e.loc, e, td);
                        }
                        return expressionSemantic(e, sc);
                    }
                    TemplateInstance ti = s.isTemplateInstance();
                    if (ti != null)
                    {
                        if (ti.semanticRun == 0)
                        {
                            dsymbolSemantic(ti, sc);
                            if ((ti.inst == null) || ti.errors)
                            {
                                return new ErrorExp();
                            }
                        }
                        s = ti.inst.toAlias();
                        if (s.isTemplateInstance() == null)
                        {
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        }
                        if (((e.op & 0xFF) == 20))
                        {
                            e = new ScopeExp(e.loc, ti);
                        }
                        else
                        {
                            e = new DotExp(e.loc, e, new ScopeExp(e.loc, ti));
                        }
                        return expressionSemantic(e, sc);
                    }
                    if ((s.isImport() != null) || (s.isModule() != null) || (s.isPackage() != null))
                    {
                        return symbolToExp(s, e.loc, sc, false);
                    }
                    OverloadSet o = s.isOverloadSet();
                    if (o != null)
                    {
                        OverExp oe = new OverExp(e.loc, o);
                        if (((e.op & 0xFF) == 20))
                        {
                            return oe;
                        }
                        return new DotExp(e.loc, e, oe);
                    }
                    Declaration d = s.isDeclaration();
                    if (d == null)
                    {
                        e.error(new BytePtr("`%s.%s` is not a declaration"), e.toChars(), ident.toChars());
                        return new ErrorExp();
                    }
                    if (((e.op & 0xFF) == 20))
                    {
                        {
                            TupleDeclaration tup = d.isTupleDeclaration();
                            if ((tup) != null)
                            {
                                e = new TupleExp(e.loc, tup);
                                return expressionSemantic(e, sc);
                            }
                        }
                        if (d.needThis() && ((sc.get()).intypeof != 1))
                        {
                            if (hasThis(sc) != null)
                            {
                                e = new DotVarExp(e.loc, new ThisExp(e.loc), d, true);
                                return expressionSemantic(e, sc);
                            }
                        }
                        if ((d.semanticRun == PASS.init))
                        {
                            dsymbolSemantic(d, null);
                        }
                        checkAccess(e.loc, sc, e, d);
                        VarExp ve = new VarExp(e.loc, d, true);
                        if ((d.isVarDeclaration() != null) && d.needThis())
                        {
                            ve.type.value = d.type.addMod(e.type.value.mod);
                        }
                        return ve;
                    }
                    boolean unreal = ((e.op & 0xFF) == 26) && ((VarExp)e).var.isField();
                    if (d.isDataseg() || unreal && d.isField())
                    {
                        checkAccess(e.loc, sc, e, d);
                        Expression ve = new VarExp(e.loc, d, true);
                        e = unreal ? ve : new CommaExp(e.loc, e, ve, true);
                        return expressionSemantic(e, sc);
                    }
                    e = new DotVarExp(e.loc, e, d, true);
                    return expressionSemantic(e, sc);
                    break;
                } catch(Dispatch0 __d){}
            }
        };
        Function1<TypeEnum,Expression> visitEnum = new Function1<TypeEnum,Expression>(){
            public Expression invoke(TypeEnum mt) {
                if ((pequals(ident, Id._mangleof)))
                {
                    return getProperty(mt, e.loc, ident, flag & 1);
                }
                if ((mt.sym.semanticRun < PASS.semanticdone))
                {
                    dsymbolSemantic(mt.sym, null);
                }
                if (mt.sym.members == null)
                {
                    if (mt.sym.isSpecial())
                    {
                        e = dotExp(mt.sym.memtype, sc, e, ident, flag);
                    }
                    else if ((flag & 1) == 0)
                    {
                        mt.sym.error(new BytePtr("is forward referenced when looking for `%s`"), ident.toChars());
                        e = new ErrorExp();
                    }
                    else
                    {
                        e = null;
                    }
                    return e;
                }
                Dsymbol s = mt.sym.search(e.loc, ident, 8);
                if (s == null)
                {
                    if ((pequals(ident, Id.max)) || (pequals(ident, Id.min)) || (pequals(ident, Id._init)))
                    {
                        return getProperty(mt, e.loc, ident, flag & 1);
                    }
                    Expression res = dotExp(mt.sym.getMemtype(Loc.initial), sc, e, ident, 1);
                    if (((flag & 1) == 0) && (res == null))
                    {
                        {
                            Dsymbol ns = mt.sym.search_correct(ident);
                            if ((ns) != null)
                            {
                                e.error(new BytePtr("no property `%s` for type `%s`. Did you mean `%s.%s` ?"), ident.toChars(), mt.toChars(), mt.toChars(), ns.toChars());
                            }
                            else
                            {
                                e.error(new BytePtr("no property `%s` for type `%s`"), ident.toChars(), mt.toChars());
                            }
                        }
                        return new ErrorExp();
                    }
                    return res;
                }
                EnumMember m = s.isEnumMember();
                return m.getVarExp(e.loc, sc);
            }
        };
        Function1<TypeClass,Expression> visitClass = new Function1<TypeClass,Expression>(){
            public Expression invoke(TypeClass mt) {
                Dsymbol s = null;
                assert(((e.op & 0xFF) != 97));
                if ((pequals(ident, Id.__sizeof)) || (pequals(ident, Id.__xalignof)) || (pequals(ident, Id._mangleof)))
                {
                    return getProperty(mt.Type, e.loc, ident, 0);
                }
                if ((pequals(ident, Id._tupleof)))
                {
                    objc().checkTupleof(e, mt);
                    e = expressionSemantic(e, sc);
                    mt.sym.size(e.loc);
                    Ref<Expression> e0 = ref(null);
                    Expression ev = ((e.op & 0xFF) == 20) ? null : e;
                    if (ev != null)
                    {
                        ev = extractSideEffect(sc, new BytePtr("__tup"), e0, ev, false);
                    }
                    Ptr<DArray<Expression>> exps = refPtr(new DArray<Expression>());
                    (exps.get()).reserve(mt.sym.fields.length);
                    {
                        int i = 0;
                        for (; (i < mt.sym.fields.length);i++){
                            VarDeclaration v = mt.sym.fields.get(i);
                            if (v.isThisDeclaration() != null)
                            {
                                continue;
                            }
                            Expression ex = null;
                            if (ev != null)
                            {
                                ex = new DotVarExp(e.loc, ev, v, true);
                            }
                            else
                            {
                                ex = new VarExp(e.loc, v, true);
                                ex.type.value = ex.type.value.addMod(e.type.value.mod);
                            }
                            (exps.get()).push(ex);
                        }
                    }
                    e = new TupleExp(e.loc, e0.value, exps);
                    Ptr<Scope> sc2 = (sc.get()).push();
                    (sc2.get()).flags |= global.value.params.vsafe ? 1024 : 2;
                    e = expressionSemantic(e, sc2);
                    (sc2.get()).pop();
                    return e;
                }
                int flags = ((sc.get()).flags & 512) != 0 ? 128 : 0;
                s = mt.sym.search(e.loc, ident, flags | 1);
                while(true) try {
                /*L1:*/
                    if (s == null)
                    {
                        if ((pequals(mt.sym.ident, ident)))
                        {
                            if (((e.op & 0xFF) == 20))
                            {
                                return getProperty(mt.Type, e.loc, ident, 0);
                            }
                            e = new DotTypeExp(e.loc, e, mt.sym);
                            e = expressionSemantic(e, sc);
                            return e;
                        }
                        {
                            ClassDeclaration cbase = mt.sym.searchBase(ident);
                            if ((cbase) != null)
                            {
                                if (((e.op & 0xFF) == 20))
                                {
                                    return getProperty(mt.Type, e.loc, ident, 0);
                                }
                                {
                                    InterfaceDeclaration ifbase = cbase.isInterfaceDeclaration();
                                    if ((ifbase) != null)
                                    {
                                        e = new CastExp(e.loc, e, ifbase.type);
                                    }
                                    else
                                    {
                                        e = new DotTypeExp(e.loc, e, cbase);
                                    }
                                }
                                e = expressionSemantic(e, sc);
                                return e;
                            }
                        }
                        if ((pequals(ident, Id.classinfo)))
                        {
                            if (Type.typeinfoclass == null)
                            {
                                error(e.loc, new BytePtr("`object.TypeInfo_Class` could not be found, but is implicitly used"));
                                return new ErrorExp();
                            }
                            Type t = Type.typeinfoclass.type;
                            if (((e.op & 0xFF) == 20) || ((e.op & 0xFF) == 30))
                            {
                                if (mt.sym.vclassinfo == null)
                                {
                                    mt.sym.vclassinfo = new TypeInfoClassDeclaration(mt.sym.type);
                                }
                                e = new VarExp(e.loc, mt.sym.vclassinfo, true);
                                e = e.addressOf();
                                e.type.value = t;
                            }
                            else
                            {
                                e = new PtrExp(e.loc, e);
                                e.type.value = t.pointerTo();
                                if (mt.sym.isInterfaceDeclaration() != null)
                                {
                                    if (mt.sym.isCPPinterface())
                                    {
                                        error(e.loc, new BytePtr("no `.classinfo` for C++ interface objects"));
                                    }
                                    e.type.value = e.type.value.pointerTo();
                                    e = new PtrExp(e.loc, e);
                                    e.type.value = t.pointerTo();
                                }
                                e = new PtrExp(e.loc, e, t);
                            }
                            return e;
                        }
                        if ((pequals(ident, Id.__vptr)))
                        {
                            e = e.castTo(sc, Type.tvoidptr.immutableOf().pointerTo().pointerTo());
                            e = new PtrExp(e.loc, e);
                            e = expressionSemantic(e, sc);
                            return e;
                        }
                        if ((pequals(ident, Id.__monitor)) && mt.sym.hasMonitor())
                        {
                            e = e.castTo(sc, Type.tvoidptr.pointerTo());
                            e = new AddExp(e.loc, e, literal_356A192B7913B04C());
                            e = new PtrExp(e.loc, e);
                            e = expressionSemantic(e, sc);
                            return e;
                        }
                        if ((pequals(ident, Id.outer)) && (mt.sym.vthis != null))
                        {
                            if ((mt.sym.vthis.semanticRun == PASS.init))
                            {
                                dsymbolSemantic(mt.sym.vthis, null);
                            }
                            {
                                ClassDeclaration cdp = mt.sym.toParentLocal().isClassDeclaration();
                                if ((cdp) != null)
                                {
                                    DotVarExp dve = new DotVarExp(e.loc, e, mt.sym.vthis, true);
                                    dve.type.value = cdp.type.addMod(e.type.value.mod);
                                    return dve;
                                }
                            }
                            {
                                Dsymbol p = mt.sym.toParentLocal();
                                for (; p != null;p = p.toParentLocal()){
                                    FuncDeclaration fd = p.isFuncDeclaration();
                                    if (fd == null)
                                    {
                                        break;
                                    }
                                    AggregateDeclaration ad = fd.isThis();
                                    if ((ad == null) && fd.isNested())
                                    {
                                        continue;
                                    }
                                    if (ad == null)
                                    {
                                        break;
                                    }
                                    {
                                        ClassDeclaration cdp = ad.isClassDeclaration();
                                        if ((cdp) != null)
                                        {
                                            ThisExp ve = new ThisExp(e.loc);
                                            ve.var = fd.vthis;
                                            boolean nestedError = fd.vthis.checkNestedReference(sc, e.loc);
                                            assert(!nestedError);
                                            ve.type.value = cdp.type.addMod(fd.vthis.type.mod).addMod(e.type.value.mod);
                                            return ve;
                                        }
                                    }
                                    break;
                                }
                            }
                            DotVarExp dve = new DotVarExp(e.loc, e, mt.sym.vthis, true);
                            dve.type.value = mt.sym.vthis.type.addMod(e.type.value.mod);
                            return dve;
                        }
                        return noMember.invoke(mt, sc, e, ident, flag & 1);
                    }
                    if ((((sc.get()).flags & 512) == 0) && !symbolIsVisible(sc, s))
                    {
                        return noMember.invoke(mt, sc, e, ident, flag);
                    }
                    if (s.isFuncDeclaration() == null)
                    {
                        s.checkDeprecated(e.loc, sc);
                        {
                            Declaration d = s.isDeclaration();
                            if ((d) != null)
                            {
                                d.checkDisabled(e.loc, sc, false);
                            }
                        }
                    }
                    s = s.toAlias();
                    {
                        EnumMember em = s.isEnumMember();
                        if ((em) != null)
                        {
                            return em.getVarExp(e.loc, sc);
                        }
                    }
                    {
                        VarDeclaration v = s.isVarDeclaration();
                        if ((v) != null)
                        {
                            if ((v.type == null) || (v.type.deco == null) && (v.inuse != 0))
                            {
                                if (v.inuse != 0)
                                {
                                    e.error(new BytePtr("circular reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                                }
                                else
                                {
                                    e.error(new BytePtr("forward reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                                }
                                return new ErrorExp();
                            }
                            if (((v.type.ty & 0xFF) == ENUMTY.Terror))
                            {
                                return new ErrorExp();
                            }
                            if (((v.storage_class & 8388608L) != 0) && (v._init != null))
                            {
                                if (v.inuse != 0)
                                {
                                    e.error(new BytePtr("circular initialization of %s `%s`"), v.kind(), v.toPrettyChars(false));
                                    return new ErrorExp();
                                }
                                checkAccess(e.loc, sc, null, (Declaration)v);
                                Expression ve = new VarExp(e.loc, v, true);
                                ve = expressionSemantic(ve, sc);
                                return ve;
                            }
                        }
                    }
                    {
                        Type t = s.getType();
                        if ((t) != null)
                        {
                            return expressionSemantic(new TypeExp(e.loc, t), sc);
                        }
                    }
                    TemplateMixin tm = s.isTemplateMixin();
                    if (tm != null)
                    {
                        Expression de = new DotExp(e.loc, e, new ScopeExp(e.loc, tm));
                        de.type.value = e.type.value;
                        return de;
                    }
                    TemplateDeclaration td = s.isTemplateDeclaration();
                    if (td != null)
                    {
                        if (((e.op & 0xFF) == 20))
                        {
                            e = new TemplateExp(e.loc, td, null);
                        }
                        else
                        {
                            e = new DotTemplateExp(e.loc, e, td);
                        }
                        e = expressionSemantic(e, sc);
                        return e;
                    }
                    TemplateInstance ti = s.isTemplateInstance();
                    if (ti != null)
                    {
                        if (ti.semanticRun == 0)
                        {
                            dsymbolSemantic(ti, sc);
                            if ((ti.inst == null) || ti.errors)
                            {
                                return new ErrorExp();
                            }
                        }
                        s = ti.inst.toAlias();
                        if (s.isTemplateInstance() == null)
                        {
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        }
                        if (((e.op & 0xFF) == 20))
                        {
                            e = new ScopeExp(e.loc, ti);
                        }
                        else
                        {
                            e = new DotExp(e.loc, e, new ScopeExp(e.loc, ti));
                        }
                        return expressionSemantic(e, sc);
                    }
                    if ((s.isImport() != null) || (s.isModule() != null) || (s.isPackage() != null))
                    {
                        e = symbolToExp(s, e.loc, sc, false);
                        return e;
                    }
                    OverloadSet o = s.isOverloadSet();
                    if (o != null)
                    {
                        OverExp oe = new OverExp(e.loc, o);
                        if (((e.op & 0xFF) == 20))
                        {
                            return oe;
                        }
                        return new DotExp(e.loc, e, oe);
                    }
                    Declaration d = s.isDeclaration();
                    if (d == null)
                    {
                        e.error(new BytePtr("`%s.%s` is not a declaration"), e.toChars(), ident.toChars());
                        return new ErrorExp();
                    }
                    if (((e.op & 0xFF) == 20))
                    {
                        {
                            TupleDeclaration tup = d.isTupleDeclaration();
                            if ((tup) != null)
                            {
                                e = new TupleExp(e.loc, tup);
                                e = expressionSemantic(e, sc);
                                return e;
                            }
                        }
                        if ((mt.sym.classKind == ClassKind.objc) && (d.isFuncDeclaration() != null) && d.isFuncDeclaration().isStatic() && (d.isFuncDeclaration().selector != null))
                        {
                            ObjcClassReferenceExp classRef = new ObjcClassReferenceExp(e.loc, mt.sym);
                            return expressionSemantic(new DotVarExp(e.loc, classRef, d, true), sc);
                        }
                        else if (d.needThis() && ((sc.get()).intypeof != 1))
                        {
                            AggregateDeclaration ad = d.isMemberLocal();
                            {
                                FuncDeclaration f = hasThis(sc);
                                if ((f) != null)
                                {
                                    Expression e1 = null;
                                    Type t = null;
                                    try {
                                        if (f.isThis2)
                                        {
                                            if (followInstantiationContextAggregateDeclaration(f, ad))
                                            {
                                                e1 = new VarExp(e.loc, f.vthis, true);
                                                e1 = new PtrExp(e1.loc, e1);
                                                e1 = new IndexExp(e1.loc, e1, literal_356A192B7913B04C());
                                                Declaration pd = f.toParent2().isDeclaration();
                                                assert(pd != null);
                                                t = pd.type.toBasetype();
                                                e1 = getThisSkipNestedFuncs(e1.loc, sc, f.toParent2(), ad, e1, t, d, true);
                                                if (e1 == null)
                                                {
                                                    e = new VarExp(e.loc, d, true);
                                                    return e;
                                                }
                                                /*goto L2*/throw Dispatch0.INSTANCE;
                                            }
                                        }
                                        e1 = new ThisExp(e.loc);
                                        e1 = expressionSemantic(e1, sc);
                                    }
                                    catch(Dispatch0 __d){}
                                /*L2:*/
                                    t = e1.type.value.toBasetype();
                                    ClassDeclaration cd = e.type.value.isClassHandle();
                                    ClassDeclaration tcd = t.isClassHandle();
                                    if ((cd != null) && (tcd != null) && (pequals(tcd, cd)) || cd.isBaseOf(tcd, null))
                                    {
                                        e = new DotTypeExp(e1.loc, e1, cd);
                                        e = new DotVarExp(e.loc, e, d, true);
                                        e = expressionSemantic(e, sc);
                                        return e;
                                    }
                                    if ((tcd != null) && tcd.isNested())
                                    {
                                        VarDeclaration vthis = followInstantiationContextAggregateDeclaration(tcd, ad) ? tcd.vthis2 : tcd.vthis;
                                        e1 = new DotVarExp(e.loc, e1, vthis, true);
                                        e1.type.value = vthis.type;
                                        e1.type.value = e1.type.value.addMod(t.mod);
                                        e1 = getThisSkipNestedFuncs(e1.loc, sc, toParentPAggregateDeclaration(tcd, ad), ad, e1, t, d, true);
                                        if (e1 == null)
                                        {
                                            e = new VarExp(e.loc, d, true);
                                            return e;
                                        }
                                        /*goto L2*/throw Dispatch0.INSTANCE;
                                    }
                                }
                            }
                        }
                        if ((d.semanticRun == PASS.init))
                        {
                            dsymbolSemantic(d, null);
                        }
                        {
                            FuncDeclaration fd = d.isFuncDeclaration();
                            if ((fd) != null)
                            {
                                d = (Declaration)mostVisibleOverload(fd, (sc.get())._module);
                            }
                        }
                        checkAccess(e.loc, sc, e, d);
                        VarExp ve = new VarExp(e.loc, d, true);
                        if ((d.isVarDeclaration() != null) && d.needThis())
                        {
                            ve.type.value = d.type.addMod(e.type.value.mod);
                        }
                        return ve;
                    }
                    boolean unreal = ((e.op & 0xFF) == 26) && ((VarExp)e).var.isField();
                    if (d.isDataseg() || unreal && d.isField())
                    {
                        checkAccess(e.loc, sc, e, d);
                        Expression ve = new VarExp(e.loc, d, true);
                        e = unreal ? ve : new CommaExp(e.loc, e, ve, true);
                        e = expressionSemantic(e, sc);
                        return e;
                    }
                    e = new DotVarExp(e.loc, e, d, true);
                    e = expressionSemantic(e, sc);
                    return e;
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
        Function1<TypeBasic,Expression> visitBasic = new Function1<TypeBasic,Expression>(){
            public Expression invoke(TypeBasic mt) {
                long value = 0L;
                switch ((mt.ty & 0xFF))
                {
                    case 31:
                        value = 255L;
                        break;
                    case 32:
                    case 33:
                        value = 65535L;
                        break;
                    case 24:
                    case 25:
                    case 26:
                    case 21:
                    case 22:
                    case 23:
                        return new RealExp(loc, target.value.RealProperties.nan, mt);
                    case 27:
                    case 28:
                    case 29:
                        complex_t cvalue = cvalue = new complex_t(target.value.RealProperties.nan, target.value.RealProperties.nan);
                        return new ComplexExp(loc, cvalue, mt);
                    case 12:
                        error(loc, new BytePtr("`void` does not have a default initializer"));
                        return new ErrorExp();
                    default:
                    break;
                }
                return new IntegerExp(loc, value, mt);
            }
        };
        Function1<TypeVector,Expression> visitVector = new Function1<TypeVector,Expression>(){
            public Expression invoke(TypeVector mt) {
                assert(((mt.basetype.ty & 0xFF) == ENUMTY.Tsarray));
                Expression e = defaultInit(mt.basetype, loc);
                VectorExp ve = new VectorExp(loc, e, mt);
                ve.type.value = mt;
                ve.dim = (int)(mt.basetype.size(loc) / mt.elementType().size(loc));
                return ve;
            }
        };
        Function1<TypeSArray,Expression> visitSArray = new Function1<TypeSArray,Expression>(){
            public Expression invoke(TypeSArray mt) {
                if (((mt.next.value.ty & 0xFF) == ENUMTY.Tvoid))
                {
                    return defaultInit(Type.tuns8, loc);
                }
                else
                {
                    return defaultInit(mt.next.value, loc);
                }
            }
        };
        Function1<TypeFunction,Expression> visitFunction = new Function1<TypeFunction,Expression>(){
            public Expression invoke(TypeFunction mt) {
                error(loc, new BytePtr("`function` does not have a default initializer"));
                return new ErrorExp();
            }
        };
        Function1<TypeStruct,Expression> visitStruct = new Function1<TypeStruct,Expression>(){
            public Expression invoke(TypeStruct mt) {
                Declaration d = new SymbolDeclaration(mt.sym.loc, mt.sym);
                assert(d != null);
                d.type = mt;
                d.storage_class |= 2199023255552L;
                return new VarExp(mt.sym.loc, d, true);
            }
        };
        Function1<TypeEnum,Expression> visitEnum = new Function1<TypeEnum,Expression>(){
            public Expression invoke(TypeEnum mt) {
                Expression e = mt.sym.getDefaultValue(loc);
                e = e.copy();
                e.loc = loc.copy();
                e.type.value = mt;
                return e;
            }
        };
        Function1<TypeTuple,Expression> visitTuple = new Function1<TypeTuple,Expression>(){
            public Expression invoke(TypeTuple mt) {
                Ptr<DArray<Expression>> exps = refPtr(new DArray<Expression>((mt.arguments.get()).length));
                {
                    int i = 0;
                    for (; (i < (mt.arguments.get()).length);i++){
                        Parameter p = (mt.arguments.get()).get(i);
                        assert(p.type != null);
                        Expression e = p.type.defaultInitLiteral(loc);
                        if (((e.op & 0xFF) == 127))
                        {
                            return e;
                        }
                        exps.get().set(i, e);
                    }
                }
                return new TupleExp(loc, exps);
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
                return new NullExp(Loc.initial, Type.tnull);
            case 34:
                return new ErrorExp();
            case 0:
            case 2:
            case 3:
            case 4:
            case 10:
            case 7:
                return new NullExp(loc, mt);
            default:
            return mt.isTypeBasic() != null ? visitBasic.invoke((TypeBasic)mt) : null;
        }
    }

}
