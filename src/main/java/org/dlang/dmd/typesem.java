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
    static int noMembernest;

    public static Expression semanticLength(Scope sc, Type t, Expression exp) {
        {
            TypeTuple tt = t.isTypeTuple();
            if (tt != null)
            {
                ScopeDsymbol sym = new ArrayScopeSymbol(sc, tt);
                sym.parent = (sc).scopesym;
                sc = (sc).push(sym);
                sc = (sc).startCTFE();
                exp = expressionSemantic(exp, sc);
                sc = (sc).endCTFE();
                (sc).pop();
            }
            else
            {
                sc = (sc).startCTFE();
                exp = expressionSemantic(exp, sc);
                sc = (sc).endCTFE();
            }
        }
        return exp;
    }

    public static Expression semanticLength(Scope sc, TupleDeclaration tup, Expression exp) {
        ScopeDsymbol sym = new ArrayScopeSymbol(sc, tup);
        sym.parent = (sc).scopesym;
        sc = (sc).push(sym);
        sc = (sc).startCTFE();
        exp = expressionSemantic(exp, sc);
        sc = (sc).endCTFE();
        (sc).pop();
        return exp;
    }

    public static void resolveTupleIndex(Loc loc, Scope sc, Dsymbol s, Ptr<Expression> pe, Ptr<Type> pt, Ptr<Dsymbol> ps, RootObject oindex) {
        pt.set(0, null);
        ps.set(0, null);
        pe.set(0, null);
        TupleDeclaration tup = s.isTupleDeclaration();
        Ref<Expression> eindex = ref(isExpression(oindex));
        Ref<Type> tindex = ref(isType(oindex));
        Ref<Dsymbol> sindex = ref(isDsymbol(oindex));
        if (!(tup != null))
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
        if (!(eindex.value != null))
        {
            error(loc, new BytePtr("index `%s` is not an expression"), oindex.toChars());
            pt.set(0, Type.terror);
            return ;
        }
        eindex.value = semanticLength(sc, tup, eindex.value);
        eindex.value = eindex.value.ctfeInterpret();
        if ((eindex.value.op & 0xFF) == 127)
        {
            pt.set(0, Type.terror);
            return ;
        }
        long d = eindex.value.toUInteger();
        if (d >= (long)(tup.objects).length)
        {
            error(loc, new BytePtr("tuple index `%llu` exceeds length %u"), d, (tup.objects).length);
            pt.set(0, Type.terror);
            return ;
        }
        RootObject o = (tup.objects).get((int)d);
        pt.set(0, isType(o));
        ps.set(0, isDsymbol(o));
        pe.set(0, isExpression(o));
        if (pt.get() != null)
            pt.set(0, typeSemantic(pt.get(), loc, sc));
        if (pe.get() != null)
            resolveExp(pe.get(), pt, pe, ps);
    }

    public static void resolveHelper(TypeQualified mt, Loc loc, Scope sc, Dsymbol s, Dsymbol scopesym, Ptr<Expression> pe, Ptr<Type> pt, Ptr<Dsymbol> ps, boolean intypeid) {
        Ref<TypeQualified> mt_ref = ref(mt);
        Ref<Scope> sc_ref = ref(sc);
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
            if ((d != null && (d.storage_class & 262144L) != 0))
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
                for (; i.value < mt_ref.value.idents.length;i.value++){
                    RootObject id = mt_ref.value.idents.get(i.value);
                    if ((id.dyncast() == DYNCAST.expression || id.dyncast() == DYNCAST.type))
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
                    int errorsave = global.errors;
                    int flags = t == null ? 8 : 1;
                    Dsymbol sm = s_ref.value.searchX(loc, sc_ref.value, id, flags);
                    if (((sm != null && !(((sc_ref.value).flags & 512) != 0)) && !(symbolIsVisible(sc_ref.value, sm))))
                    {
                        error(loc, new BytePtr("`%s` is not visible from module `%s`"), sm.toPrettyChars(false), (sc_ref.value)._module.toChars());
                        sm = null;
                    }
                    if (global.errors != errorsave)
                    {
                        pt_ref.value.set(0, Type.terror);
                        return ;
                    }
                    Function0<Void> helper3 = new Function0<Void>(){
                        public Void invoke(){
                            Expression e = null;
                            VarDeclaration v = s_ref.value.isVarDeclaration();
                            FuncDeclaration f = s_ref.value.isFuncDeclaration();
                            if ((intypeid_ref.value || (!(v != null) && !(f != null))))
                                e = symbolToExp(s_ref.value, loc, sc_ref.value, true);
                            else
                                e = new VarExp(loc, s_ref.value.isDeclaration(), true);
                            e = typeToExpressionHelper(mt_ref.value, e, i.value);
                            e = expressionSemantic(e, sc_ref.value);
                            resolveExp(e, pt_ref.value, pe_ref.value, ps_ref.value);
                            return null;
                        }
                    };
                    if ((((intypeid_ref.value && !(t != null)) && sm != null) && sm.needThis()))
                        helper3.invoke();
                        return ;
                    {
                        VarDeclaration v = s_ref.value.isVarDeclaration();
                        if (v != null)
                        {
                            if (v.type == null)
                                dsymbolSemantic(v, sc_ref.value);
                            if ((((v.storage_class & 9437188L) != 0 || v.type.isConst()) || v.type.isImmutable()))
                            {
                                if (!(v.isThisDeclaration() != null))
                                    helper3.invoke();
                                    return ;
                            }
                        }
                    }
                    if (!(sm != null))
                    {
                        if (!(t != null))
                        {
                            if (s_ref.value.isDeclaration() != null)
                            {
                                t = s_ref.value.isDeclaration().type;
                                if ((!(t != null) && s_ref.value.isTupleDeclaration() != null))
                                    helper3.invoke();
                                    return ;
                            }
                            else if ((((s_ref.value.isTemplateInstance() != null || s_ref.value.isImport() != null) || s_ref.value.isPackage() != null) || s_ref.value.isModule() != null))
                            {
                                helper3.invoke();
                                return ;
                            }
                        }
                        if (t != null)
                        {
                            sm = t.toDsymbol(sc_ref.value);
                            if ((sm != null && id.dyncast() == DYNCAST.identifier))
                            {
                                sm = sm.search(loc, (Identifier)id, 1);
                                if (!(sm != null))
                                    helper3.invoke();
                                    return ;
                            }
                            else
                                helper3.invoke();
                                return ;
                        }
                        else
                        {
                            if (id.dyncast() == DYNCAST.dsymbol)
                            {
                                assert((global.errors) != 0);
                            }
                            else
                            {
                                assert(id.dyncast() == DYNCAST.identifier);
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
                if (em != null)
                {
                    pe_ref.value.set(0, em.getVarExp(loc, sc_ref.value));
                    return ;
                }
            }
            {
                VarDeclaration v = s_ref.value.isVarDeclaration();
                if (v != null)
                {
                    if ((!(v.type != null) || (v.type.deco == null && (v.inuse) != 0)))
                    {
                        if ((v.inuse) != 0)
                            error(loc, new BytePtr("circular reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                        else
                            error(loc, new BytePtr("forward reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                        pt_ref.value.set(0, Type.terror);
                        return ;
                    }
                    if ((v.type.ty & 0xFF) == ENUMTY.Terror)
                        pt_ref.value.set(0, Type.terror);
                    else
                        pe_ref.value.set(0, (new VarExp(loc, v, true)));
                    return ;
                }
            }
            {
                FuncLiteralDeclaration fld = s_ref.value.isFuncLiteralDeclaration();
                if (fld != null)
                {
                    pe_ref.value.set(0, (new FuncExp(loc, fld)));
                    pe_ref.value.set(0, expressionSemantic(pe_ref.value.get(), sc_ref.value));
                    return ;
                }
            }
            Type t = null;
            for (; (1) != 0;){
                t = s_ref.value.getType();
                if (t != null)
                    break;
                {
                    Import si = s_ref.value.isImport();
                    if (si != null)
                    {
                        s_ref.value = si.search(loc, s_ref.value.ident, 8);
                        if ((s_ref.value != null && !pequals(s_ref.value, si)))
                            continue;
                        s_ref.value = si;
                    }
                }
                ps_ref.value.set(0, s_ref.value);
                return ;
            }
            {
                TypeInstance ti = t.isTypeInstance();
                if (ti != null)
                    if ((!pequals(ti, mt_ref.value) && ti.deco == null))
                    {
                        if (!(ti.tempinst.errors))
                            error(loc, new BytePtr("forward reference to `%s`"), ti.toChars());
                        pt_ref.value.set(0, Type.terror);
                        return ;
                    }
            }
            if ((t.ty & 0xFF) == ENUMTY.Ttuple)
                pt_ref.value.set(0, t);
            else
                pt_ref.value.set(0, merge(t));
        }
        if (!(s_ref.value != null))
        {
            BytePtr p = pcopy(mt_ref.value.mutableOf().unSharedOf().toChars());
            Identifier id = Identifier.idPool(p, strlen(p));
            {
                ByteSlice n = importHint(id.asString()).copy();
                if (n.getLength() != 0)
                    error(loc, new BytePtr("`%s` is not defined, perhaps `import %.*s;` ?"), p, n.getLength(), toBytePtr(n));
                else {
                    Dsymbol s2 = (sc_ref.value).search_correct(id);
                    if (s2 != null)
                        error(loc, new BytePtr("undefined identifier `%s`, did you mean %s `%s`?"), p, s2.kind(), s2.toChars());
                    else {
                        BytePtr q = pcopy(Scope.search_correct_C(id));
                        if (q != null)
                            error(loc, new BytePtr("undefined identifier `%s`, did you mean `%s`?"), p, q);
                        else
                            error(loc, new BytePtr("undefined identifier `%s`"), p);
                    }
                }
            }
            pt_ref.value.set(0, Type.terror);
        }
    }

    public static Type stripDefaultArgs(Type t) {
        Function1<DArray<Parameter>,DArray<Parameter>> stripParams = new Function1<DArray<Parameter>,DArray<Parameter>>(){
            public DArray<Parameter> invoke(DArray<Parameter> parameters){
                Function1<Parameter,Parameter> stripParameter = new Function1<Parameter,Parameter>(){
                    public Parameter invoke(Parameter p){
                        Type t = stripDefaultArgs(p.type);
                        return (((!pequals(t, p.type) || p.defaultArg != null) || p.ident != null) || p.userAttribDecl != null) ? new Parameter(p.storageClass, t, null, null, null) : null;
                    }
                };
                if (parameters != null)
                {
                    {
                        Slice<Parameter> __r1719 = (parameters).opSlice().copy();
                        int __key1718 = 0;
                        for (; __key1718 < __r1719.getLength();__key1718 += 1) {
                            Parameter p = __r1719.get(__key1718);
                            int i = __key1718;
                            Parameter ps = stripParameter.invoke(p);
                            if (ps != null)
                            {
                                DArray<Parameter> nparams = new DArray<Parameter>((parameters).length);
                                {
                                    Slice<Parameter> __r1721 = (nparams).opSlice().copy();
                                    int __key1720 = 0;
                                    for (; __key1720 < __r1721.getLength();__key1720 += 1) {
                                        Parameter np = __r1721.get(__key1720);
                                        int j = __key1720;
                                        Parameter pj = (parameters).get(j);
                                        if (j < i)
                                            np = pj;
                                        else if (j == i)
                                            np = ps;
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
        if (t == null)
            return t;
        {
            TypeFunction tf = t.isTypeFunction();
            if (tf != null)
            {
                Type tret = stripDefaultArgs(tf.next);
                DArray<Parameter> params = stripParams.invoke(tf.parameterList.parameters);
                if ((pequals(tret, tf.next) && params == tf.parameterList.parameters))
                    return t;
                TypeFunction tr = (TypeFunction)tf.copy();
                tr.parameterList.parameters = params;
                tr.next = tret;
                return tr;
            }
            else {
                TypeTuple tt = t.isTypeTuple();
                if (tt != null)
                {
                    DArray<Parameter> args = stripParams.invoke(tt.arguments);
                    if (args == tt.arguments)
                        return t;
                    TypeTuple tr = (TypeTuple)t.copy();
                    tr.arguments = args;
                    return tr;
                }
                else if ((t.ty & 0xFF) == ENUMTY.Tenum)
                {
                    return t;
                }
                else
                {
                    Type tn = t.nextOf();
                    Type n = stripDefaultArgs(tn);
                    if (pequals(n, tn))
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
            public Expression invoke(TypeSArray t){
                {
                    Expression e = typeToExpression(t.next);
                    if (e != null)
                        return new ArrayExp(t.dim.loc, e, t.dim);
                }
                return null;
            }
        };
        Function1<TypeAArray,Expression> visitAArray = new Function1<TypeAArray,Expression>(){
            public Expression invoke(TypeAArray t){
                {
                    Expression e = typeToExpression(t.next);
                    if (e != null)
                    {
                        {
                            Expression ei = typeToExpression(t.index);
                            if (ei != null)
                                return new ArrayExp(t.loc, e, ei);
                        }
                    }
                }
                return null;
            }
        };
        Function1<TypeIdentifier,Expression> visitIdentifier = new Function1<TypeIdentifier,Expression>(){
            public Expression invoke(TypeIdentifier t){
                return typeToExpressionHelper(t, new IdentifierExp(t.loc, t.ident), 0);
            }
        };
        Function1<TypeInstance,Expression> visitInstance = new Function1<TypeInstance,Expression>(){
            public Expression invoke(TypeInstance t){
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
            Slice<RootObject> __r1722 = t.idents.opSlice(i, t.idents.length).copy();
            int __key1723 = 0;
            for (; __key1723 < __r1722.getLength();__key1723 += 1) {
                RootObject id = __r1722.get(__key1723);
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

    public static Type typeSemantic(Type t, Loc loc, Scope sc) {
        Ref<Scope> sc_ref = ref(sc);
        Function0<Type> error = new Function0<Type>(){
            public Type invoke(){
                return Type.terror;
            }
        };
        Function1<Type,Type> visitType = new Function1<Type,Type>(){
            public Type invoke(Type t){
                if (((t.ty & 0xFF) == ENUMTY.Tint128 || (t.ty & 0xFF) == ENUMTY.Tuns128))
                {
                    error(loc, new BytePtr("`cent` and `ucent` types not implemented"));
                    return error.invoke();
                }
                return merge(t);
            }
        };
        Function1<TypeVector,Type> visitVector = new Function1<TypeVector,Type>(){
            public Type invoke(TypeVector mtype){
                int errors = global.errors;
                mtype.basetype = typeSemantic(mtype.basetype, loc, sc_ref.value);
                if (errors != global.errors)
                    return error.invoke();
                mtype.basetype = mtype.basetype.toBasetype().mutableOf();
                if ((mtype.basetype.ty & 0xFF) != ENUMTY.Tsarray)
                {
                    error(loc, new BytePtr("T in __vector(T) must be a static array, not `%s`"), mtype.basetype.toChars());
                    return error.invoke();
                }
                TypeSArray t = (TypeSArray)mtype.basetype;
                int sz = (int)t.size(loc);
                switch (target.isVectorTypeSupported(sz, t.nextOf()))
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
            public Type invoke(TypeSArray mtype){
                Ref<TypeSArray> mtype_ref = ref(mtype);
                Ref<Type> t = ref(null);
                Ref<Expression> e = ref(null);
                Ref<Dsymbol> s = ref(null);
                resolve(mtype_ref.value.next, loc, sc_ref.value, ptr(e), ptr(t), ptr(s), false);
                {
                    TupleDeclaration tup = s.value != null ? s.value.isTupleDeclaration() : null;
                    if (tup != null)
                    {
                        mtype_ref.value.dim = semanticLength(sc_ref.value, tup, mtype_ref.value.dim);
                        mtype_ref.value.dim = mtype_ref.value.dim.ctfeInterpret();
                        if ((mtype_ref.value.dim.op & 0xFF) == 127)
                            return error.invoke();
                        long d = mtype_ref.value.dim.toUInteger();
                        if (d >= (long)(tup.objects).length)
                        {
                            error(loc, new BytePtr("tuple index %llu exceeds %llu"), d, (long)(tup.objects).length);
                            return error.invoke();
                        }
                        RootObject o = (tup.objects).get((int)d);
                        if (o.dyncast() != DYNCAST.type)
                        {
                            error(loc, new BytePtr("`%s` is not a type"), mtype_ref.value.toChars());
                            return error.invoke();
                        }
                        return ((Type)o).addMod(mtype_ref.value.mod);
                    }
                }
                Type tn = typeSemantic(mtype_ref.value.next, loc, sc_ref.value);
                if ((tn.ty & 0xFF) == ENUMTY.Terror)
                    return error.invoke();
                Ref<Type> tbn = ref(tn.toBasetype());
                if (mtype_ref.value.dim != null)
                {
                    if (mtype_ref.value.dim.isDotVarExp() != null)
                    {
                        {
                            Declaration vd = mtype_ref.value.dim.isDotVarExp().var;
                            if (vd != null)
                            {
                                FuncDeclaration fd = vd.toAlias().isFuncDeclaration();
                                if (fd != null)
                                    mtype_ref.value.dim = new CallExp(loc, fd, null);
                            }
                        }
                    }
                    int errors = global.errors;
                    mtype_ref.value.dim = semanticLength(sc_ref.value, tbn.value, mtype_ref.value.dim);
                    if (errors != global.errors)
                        return error.invoke();
                    mtype_ref.value.dim = mtype_ref.value.dim.optimize(0, false);
                    mtype_ref.value.dim = mtype_ref.value.dim.ctfeInterpret();
                    if ((mtype_ref.value.dim.op & 0xFF) == 127)
                        return error.invoke();
                    errors = global.errors;
                    Ref<Long> d1 = ref(mtype_ref.value.dim.toInteger());
                    if (errors != global.errors)
                        return error.invoke();
                    mtype_ref.value.dim = mtype_ref.value.dim.implicitCastTo(sc_ref.value, Type.tsize_t);
                    mtype_ref.value.dim = mtype_ref.value.dim.optimize(0, false);
                    if ((mtype_ref.value.dim.op & 0xFF) == 127)
                        return error.invoke();
                    errors = global.errors;
                    long d2 = mtype_ref.value.dim.toInteger();
                    if (errors != global.errors)
                        return error.invoke();
                    if ((mtype_ref.value.dim.op & 0xFF) == 127)
                        return error.invoke();
                    Function0<Type> overflowError = new Function0<Type>(){
                        public Type invoke(){
                            error(loc, new BytePtr("`%s` size %llu * %llu exceeds 0x%llx size limit for static array"), mtype_ref.value.toChars(), tbn.value.size(loc), d1.value, target.maxStaticDataSize);
                            return error.invoke();
                        }
                    };
                    if (d1.value != d2)
                        return overflowError.invoke();
                    Type tbx = tbn.value.baseElemOf();
                    if ((((tbx.ty & 0xFF) == ENUMTY.Tstruct && ((TypeStruct)tbx).sym.members == null) || ((tbx.ty & 0xFF) == ENUMTY.Tenum && ((TypeEnum)tbx).sym.members == null)))
                    {
                    }
                    else if (((((((tbn.value.isTypeBasic() != null || (tbn.value.ty & 0xFF) == ENUMTY.Tpointer) || (tbn.value.ty & 0xFF) == ENUMTY.Tarray) || (tbn.value.ty & 0xFF) == ENUMTY.Tsarray) || (tbn.value.ty & 0xFF) == ENUMTY.Taarray) || ((tbn.value.ty & 0xFF) == ENUMTY.Tstruct && ((TypeStruct)tbn.value).sym.sizeok == Sizeok.done)) || (tbn.value.ty & 0xFF) == ENUMTY.Tclass))
                    {
                        Ref<Boolean> overflow = ref(false);
                        if ((mulu(tbn.value.size(loc), d2, overflow) >= target.maxStaticDataSize || overflow.value))
                            return overflowError.invoke();
                    }
                }
                switch ((tbn.value.ty & 0xFF))
                {
                    case 37:
                        assert(mtype_ref.value.dim != null);
                        TypeTuple tt = (TypeTuple)tbn.value;
                        long d = mtype_ref.value.dim.toUInteger();
                        if (d >= (long)(tt.arguments).length)
                        {
                            error(loc, new BytePtr("tuple index %llu exceeds %llu"), d, (long)(tt.arguments).length);
                            return error.invoke();
                        }
                        Type telem = (tt.arguments).get((int)d).type;
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
                mtype_ref.value.next = tn;
                mtype_ref.value.transitive();
                return merge(mtype_ref.value.addMod(tn.mod));
            }
        };
        Function1<TypeDArray,Type> visitDArray = new Function1<TypeDArray,Type>(){
            public Type invoke(TypeDArray mtype){
                Type tn = typeSemantic(mtype.next, loc, sc_ref.value);
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
                mtype.next = tn;
                mtype.transitive();
                return merge(mtype);
            }
        };
        Function1<TypeAArray,Type> visitAArray = new Function1<TypeAArray,Type>(){
            public Type invoke(TypeAArray mtype){
                if (mtype.deco != null)
                {
                    return mtype;
                }
                mtype.loc = loc.copy();
                mtype.sc = sc_ref.value;
                if (sc_ref.value != null)
                    (sc_ref.value).setNoFree();
                if ((((((mtype.index.ty & 0xFF) == ENUMTY.Tident || (mtype.index.ty & 0xFF) == ENUMTY.Tinstance) || (mtype.index.ty & 0xFF) == ENUMTY.Tsarray) || (mtype.index.ty & 0xFF) == ENUMTY.Ttypeof) || (mtype.index.ty & 0xFF) == ENUMTY.Treturn))
                {
                    Ref<Expression> e = ref(null);
                    Ref<Type> t = ref(null);
                    Ref<Dsymbol> s = ref(null);
                    resolve(mtype.index, loc, sc_ref.value, ptr(e), ptr(t), ptr(s), false);
                    if (s.value != null)
                    {
                        {
                            FuncDeclaration fd = s.value.toAlias().isFuncDeclaration();
                            if (fd != null)
                                e.value = new CallExp(loc, fd, null);
                        }
                    }
                    if (e.value != null)
                    {
                        TypeSArray tsa = new TypeSArray(mtype.next, e.value);
                        return typeSemantic(tsa, loc, sc_ref.value);
                    }
                    else if (t.value != null)
                        mtype.index = typeSemantic(t.value, loc, sc_ref.value);
                    else
                    {
                        error(loc, new BytePtr("index is not a type or an expression"));
                        return error.invoke();
                    }
                }
                else
                    mtype.index = typeSemantic(mtype.index, loc, sc_ref.value);
                mtype.index = mtype.index.merge2();
                if ((mtype.index.nextOf() != null && !(mtype.index.nextOf().isImmutable())))
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
                for (; (tbase.ty & 0xFF) == ENUMTY.Tarray;) {
                    tbase = tbase.nextOf().baseElemOf();
                }
                {
                    TypeStruct ts = tbase.isTypeStruct();
                    if (ts != null)
                    {
                        StructDeclaration sd = ts.sym;
                        if (sd.semanticRun < PASS.semanticdone)
                            dsymbolSemantic(sd, null);
                        if (((sd.xeq != null && sd.xeq._scope != null) && sd.xeq.semanticRun < PASS.semantic3done))
                        {
                            int errors = global.startGagging();
                            semantic3(sd.xeq, sd.xeq._scope);
                            if (global.endGagging(errors))
                                sd.xeq = StructDeclaration.xerreq;
                        }
                        BytePtr s = pcopy((mtype.index.toBasetype().ty & 0xFF) != ENUMTY.Tstruct ? new BytePtr("bottom of ") : new BytePtr(""));
                        if (!(sd.xeq != null))
                        {
                        }
                        else if (pequals(sd.xeq, StructDeclaration.xerreq))
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
                        else if (!(sd.xhash != null))
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
                            assert((sd.xeq != null && sd.xhash != null));
                        }
                    }
                    else if (((tbase.ty & 0xFF) == ENUMTY.Tclass && !(((TypeClass)tbase).sym.isInterfaceDeclaration() != null)))
                    {
                        ClassDeclaration cd = ((TypeClass)tbase).sym;
                        if (cd.semanticRun < PASS.semanticdone)
                            dsymbolSemantic(cd, null);
                        if (!(ClassDeclaration.object != null))
                        {
                            error(Loc.initial, new BytePtr("missing or corrupt object.d"));
                            fatal();
                        }
                        if (!(typesem.visitAArrayfeq != null))
                            typesem.visitAArrayfeq = search_function(ClassDeclaration.object, Id.eq).isFuncDeclaration();
                        if (!(typesem.visitAArrayfcmp != null))
                            typesem.visitAArrayfcmp = search_function(ClassDeclaration.object, Id.cmp).isFuncDeclaration();
                        if (!(typesem.visitAArrayfhash != null))
                            typesem.visitAArrayfhash = search_function(ClassDeclaration.object, Id.tohash).isFuncDeclaration();
                        assert(((typesem.visitAArrayfcmp != null && typesem.visitAArrayfeq != null) && typesem.visitAArrayfhash != null));
                        if ((typesem.visitAArrayfeq.vtblIndex < cd.vtbl.length && pequals(cd.vtbl.get(typesem.visitAArrayfeq.vtblIndex), typesem.visitAArrayfeq)))
                        {
                            if ((typesem.visitAArrayfcmp.vtblIndex < cd.vtbl.length && !pequals(cd.vtbl.get(typesem.visitAArrayfcmp.vtblIndex), typesem.visitAArrayfcmp)))
                            {
                                BytePtr s = pcopy((mtype.index.toBasetype().ty & 0xFF) != ENUMTY.Tclass ? new BytePtr("bottom of ") : new BytePtr(""));
                                error(loc, new BytePtr("%sAA key type `%s` now requires equality rather than comparison"), s, cd.toChars());
                                errorSupplemental(loc, new BytePtr("Please override `Object.opEquals` and `Object.toHash`."));
                            }
                        }
                    }
                }
                mtype.next = typeSemantic(mtype.next, loc, sc_ref.value).merge2();
                mtype.transitive();
                {
                    int __dispatch6 = 0;
                    dispatched_6:
                    do {
                        switch (__dispatch6 != 0 ? __dispatch6 : (mtype.next.toBasetype().ty & 0xFF))
                        {
                            case 5:
                            case 12:
                            case 11:
                            case 37:
                                error(loc, new BytePtr("cannot have associative array of `%s`"), mtype.next.toChars());
                                /*goto case*/{ __dispatch6 = 34; continue dispatched_6; }
                            case 34:
                                __dispatch6 = 0;
                                return error.invoke();
                            default:
                            break;
                        }
                    } while(__dispatch6 != 0);
                }
                if (mtype.next.isscope())
                {
                    error(loc, new BytePtr("cannot have array of scope `%s`"), mtype.next.toChars());
                    return error.invoke();
                }
                return merge(mtype);
            }
        };
        Function1<TypePointer,Type> visitPointer = new Function1<TypePointer,Type>(){
            public Type invoke(TypePointer mtype){
                if (mtype.deco != null)
                {
                    return mtype;
                }
                Type n = typeSemantic(mtype.next, loc, sc_ref.value);
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
                if (!pequals(n, mtype.next))
                {
                    mtype.deco = null;
                }
                mtype.next = n;
                if ((mtype.next.ty & 0xFF) != ENUMTY.Tfunction)
                {
                    mtype.transitive();
                    return merge(mtype);
                }
                mtype.deco = pcopy(merge(mtype).deco);
                return mtype;
            }
        };
        Function1<TypeReference,Type> visitReference = new Function1<TypeReference,Type>(){
            public Type invoke(TypeReference mtype){
                Type n = typeSemantic(mtype.next, loc, sc_ref.value);
                if (!pequals(n, mtype.next))
                    mtype.deco = null;
                mtype.next = n;
                mtype.transitive();
                return merge(mtype);
            }
        };
        Function1<TypeFunction,Type> visitFunction = new Function1<TypeFunction,Type>(){
            public Type invoke(TypeFunction mtype){
                if (mtype.deco != null)
                {
                    return mtype;
                }
                boolean errors = false;
                if (mtype.inuse > 500)
                {
                    mtype.inuse = 0;
                    error(loc, new BytePtr("recursive type"));
                    return error.invoke();
                }
                TypeFunction tf = mtype.copy().toTypeFunction();
                if (mtype.parameterList.parameters != null)
                {
                    tf.parameterList.parameters = (mtype.parameterList.parameters).copy();
                    {
                        int i = 0;
                        for (; i < (mtype.parameterList.parameters).length;i++){
                            Parameter p = null;
                            (p) = ((mtype.parameterList.parameters).get(i)).copy();
                            tf.parameterList.parameters.set(i, p);
                        }
                    }
                }
                if (((sc_ref.value).stc & 67108864L) != 0)
                    tf.purity = PURE.fwdref;
                if (((sc_ref.value).stc & 33554432L) != 0)
                    tf.isnothrow = true;
                if (((sc_ref.value).stc & 4398046511104L) != 0)
                    tf.isnogc = true;
                if (((sc_ref.value).stc & 2097152L) != 0)
                    tf.isref = true;
                if (((sc_ref.value).stc & 17592186044416L) != 0)
                    tf.isreturn = true;
                if (((sc_ref.value).stc & 4503599627370496L) != 0)
                    tf.isreturninferred = true;
                if (((sc_ref.value).stc & 524288L) != 0)
                    tf.isscope = true;
                if (((sc_ref.value).stc & 562949953421312L) != 0)
                    tf.isscopeinferred = true;
                if (tf.trust == TRUST.default_)
                {
                    if (((sc_ref.value).stc & 8589934592L) != 0)
                        tf.trust = TRUST.safe;
                    else if (((sc_ref.value).stc & 34359738368L) != 0)
                        tf.trust = TRUST.system;
                    else if (((sc_ref.value).stc & 17179869184L) != 0)
                        tf.trust = TRUST.trusted;
                }
                if (((sc_ref.value).stc & 4294967296L) != 0)
                    tf.isproperty = true;
                tf.linkage = (sc_ref.value).linkage;
                boolean wildreturn = false;
                if (tf.next != null)
                {
                    sc_ref.value = (sc_ref.value).push();
                    (sc_ref.value).stc &= -4465259184133L;
                    tf.next = typeSemantic(tf.next, loc, sc_ref.value);
                    sc_ref.value = (sc_ref.value).pop();
                    (errors ? 1 : 0) |= (tf.checkRetType(loc) ? 1 : 0);
                    if ((tf.next.isscope() && !(((sc_ref.value).flags & 1) != 0)))
                    {
                        error(loc, new BytePtr("functions cannot return `scope %s`"), tf.next.toChars());
                        errors = true;
                    }
                    if ((tf.next.hasWild()) != 0)
                        wildreturn = true;
                    if (((tf.isreturn && !(tf.isref)) && !(tf.next.hasPointers())))
                    {
                        tf.isreturn = false;
                    }
                }
                byte wildparams = (byte)0;
                if (tf.parameterList.parameters != null)
                {
                    Scope argsc = (sc_ref.value).push();
                    (argsc).stc = 0L;
                    (argsc).protection = new Prot(Prot.Kind.public_).copy();
                    (argsc).func = null;
                    int dim = tf.parameterList.length();
                    {
                        int i = 0;
                        for (; i < dim;i++){
                            Parameter fparam = tf.parameterList.get(i);
                            mtype.inuse++;
                            fparam.type = typeSemantic(fparam.type, loc, argsc);
                            mtype.inuse--;
                            if ((fparam.type.ty & 0xFF) == ENUMTY.Terror)
                            {
                                errors = true;
                                continue;
                            }
                            fparam.type = fparam.type.addStorageClass(fparam.storageClass);
                            if ((fparam.storageClass & 268435713L) != 0)
                            {
                                if (!(fparam.type != null))
                                    continue;
                            }
                            Type t = fparam.type.toBasetype();
                            if ((t.ty & 0xFF) == ENUMTY.Tfunction)
                            {
                                error(loc, new BytePtr("cannot have parameter of function type `%s`"), fparam.type.toChars());
                                errors = true;
                            }
                            else if ((!((fparam.storageClass & 2101248L) != 0) && (((t.ty & 0xFF) == ENUMTY.Tstruct || (t.ty & 0xFF) == ENUMTY.Tsarray) || (t.ty & 0xFF) == ENUMTY.Tenum)))
                            {
                                Type tb2 = t.baseElemOf();
                                if ((((tb2.ty & 0xFF) == ENUMTY.Tstruct && ((TypeStruct)tb2).sym.members == null) || ((tb2.ty & 0xFF) == ENUMTY.Tenum && !(((TypeEnum)tb2).sym.memtype != null))))
                                {
                                    error(loc, new BytePtr("cannot have parameter of opaque type `%s` by value"), fparam.type.toChars());
                                    errors = true;
                                }
                            }
                            else if ((!((fparam.storageClass & 8192L) != 0) && (t.ty & 0xFF) == ENUMTY.Tvoid))
                            {
                                error(loc, new BytePtr("cannot have parameter of type `%s`"), fparam.type.toChars());
                                errors = true;
                            }
                            if ((fparam.storageClass & 2149580800L) == 2149580800L)
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
                                    if (!((fparam.storageClass & 524288L) != 0))
                                        fparam.storageClass |= 562949953945600L;
                                    if (tf.isref)
                                    {
                                    }
                                    else if (((tf.next != null && !(tf.next.hasPointers())) && (tf.next.toBasetype().ty & 0xFF) != ENUMTY.Tvoid))
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
                                        if (((tv.ty & 0xFF) == ENUMTY.Tstruct && ((TypeStruct)tv).sym.noDefaultCtor))
                                        {
                                            error(loc, new BytePtr("cannot have `out` parameter of type `%s` because the default construction is disabled"), fparam.type.toChars());
                                            errors = true;
                                        }
                                    }
                                }
                            }
                            if ((((fparam.storageClass & 524288L) != 0 && !(fparam.type.hasPointers())) && (fparam.type.ty & 0xFF) != ENUMTY.Ttuple))
                            {
                                fparam.storageClass &= -524289L;
                                if ((!(tf.isref) || ((sc_ref.value).flags & 1) != 0))
                                    fparam.storageClass &= -17592186044417L;
                            }
                            if ((t.hasWild()) != 0)
                            {
                                wildparams |= 1;
                            }
                            if (fparam.defaultArg != null)
                            {
                                Expression e = fparam.defaultArg;
                                long isRefOrOut = fparam.storageClass & 2101248L;
                                long isAuto = fparam.storageClass & 35184372089088L;
                                if (((isRefOrOut) != 0 && !((isAuto) != 0)))
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
                                if ((e.op & 0xFF) == 161)
                                {
                                    FuncExp fe = (FuncExp)e;
                                    e = new VarExp(e.loc, fe.fd, false);
                                    e = new AddrExp(e.loc, e);
                                    e = expressionSemantic(e, argsc);
                                }
                                if ((((isRefOrOut) != 0 && (!((isAuto) != 0) || e.isLvalue())) && !(MODimplicitConv(e.type.mod, fparam.type.mod))))
                                {
                                    BytePtr errTxt = pcopy((fparam.storageClass & 2097152L) != 0 ? new BytePtr("ref") : new BytePtr("out"));
                                    error(e.loc, new BytePtr("expression `%s` of type `%s` is not implicitly convertible to type `%s %s` of parameter `%s`"), e.toChars(), e.type.toChars(), errTxt, fparam.type.toChars(), fparam.toChars());
                                }
                                e = e.implicitCastTo(argsc, fparam.type);
                                if (((isRefOrOut) != 0 && !((isAuto) != 0)))
                                    e = e.toLvalue(argsc, e);
                                fparam.defaultArg = e;
                                if ((e.op & 0xFF) == 127)
                                    errors = true;
                            }
                            {
                                TypeTuple tt = t.isTypeTuple();
                                if (tt != null)
                                {
                                    if ((tt.arguments != null && ((tt.arguments).length) != 0))
                                    {
                                        int tdim = (tt.arguments).length;
                                        DArray<Parameter> newparams = new DArray<Parameter>(tdim);
                                        {
                                            int j = 0;
                                            for (; j < tdim;j++){
                                                Parameter narg = (tt.arguments).get(j);
                                                long stc = fparam.storageClass | narg.storageClass;
                                                long stc1 = fparam.storageClass & 2109440L;
                                                long stc2 = narg.storageClass & 2109440L;
                                                if ((((stc1) != 0 && (stc2) != 0) && stc1 != stc2))
                                                {
                                                    OutBuffer buf1 = new OutBuffer();
                                                    try {
                                                        stcToBuffer(buf1, stc1 | ((stc1 & 2097152L) != 0 ? fparam.storageClass & 256L : 0L));
                                                        OutBuffer buf2 = new OutBuffer();
                                                        try {
                                                            stcToBuffer(buf2, stc2);
                                                            error(loc, new BytePtr("incompatible parameter storage classes `%s` and `%s`"), buf1.peekChars(), buf2.peekChars());
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
                                                if (((te != null && te.exps != null) && ((te.exps).length) != 0))
                                                    paramDefaultArg = (te.exps).get(j);
                                                newparams.set(j, new Parameter(stc, narg.type, narg.ident, paramDefaultArg, narg.userAttribDecl));
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
                                Expression farg = (mtype.fargs != null && i < (mtype.fargs).length) ? (mtype.fargs).get(i) : fparam.defaultArg;
                                if ((farg != null && (fparam.storageClass & 2097152L) != 0))
                                {
                                    if (farg.isLvalue())
                                    {
                                    }
                                    else
                                        fparam.storageClass &= -2097153L;
                                    fparam.storageClass &= -257L;
                                    fparam.storageClass |= 35184372088832L;
                                }
                                else if ((mtype.incomplete && (fparam.storageClass & 2097152L) != 0))
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
                    (argsc).pop();
                }
                if (tf.isWild())
                    wildparams |= 2;
                if ((wildreturn && !((wildparams) != 0)))
                {
                    error(loc, new BytePtr("`inout` on `return` means `inout` must be on a parameter as well for `%s`"), mtype.toChars());
                    errors = true;
                }
                tf.iswild = wildparams;
                if ((tf.isproperty && (tf.parameterList.varargs != VarArg.none || tf.parameterList.length() > 2)))
                {
                    error(loc, new BytePtr("properties can only have zero, one, or two parameter"));
                    errors = true;
                }
                if (((tf.parameterList.varargs == VarArg.variadic && tf.linkage != LINK.d) && tf.parameterList.length() == 0))
                {
                    error(loc, new BytePtr("variadic functions with non-D linkage must have at least one parameter"));
                    errors = true;
                }
                if (errors)
                    return error.invoke();
                if (tf.next != null)
                    tf.deco = pcopy(merge(tf).deco);
                return tf;
            }
        };
        Function1<TypeDelegate,Type> visitDelegate = new Function1<TypeDelegate,Type>(){
            public Type invoke(TypeDelegate mtype){
                if (mtype.deco != null)
                {
                    return mtype;
                }
                mtype.next = typeSemantic(mtype.next, loc, sc_ref.value);
                if ((mtype.next.ty & 0xFF) != ENUMTY.Tfunction)
                    return error.invoke();
                mtype.deco = pcopy(merge(mtype).deco);
                return mtype;
            }
        };
        Function1<TypeIdentifier,Type> visitIdentifier = new Function1<TypeIdentifier,Type>(){
            public Type invoke(TypeIdentifier mtype){
                Ref<Type> t = ref(null);
                Ref<Expression> e = ref(null);
                Ref<Dsymbol> s = ref(null);
                resolve(mtype, loc, sc_ref.value, ptr(e), ptr(t), ptr(s), false);
                if (t.value != null)
                {
                    return t.value.addMod(mtype.mod);
                }
                else
                {
                    if (s.value != null)
                    {
                        TemplateDeclaration td = s.value.isTemplateDeclaration();
                        if (((td != null && td.onemember != null) && td.onemember.isAggregateDeclaration() != null))
                            error(loc, new BytePtr("template %s `%s` is used as a type without instantiation; to instantiate it use `%s!(arguments)`"), s.value.kind(), s.value.toPrettyChars(false), s.value.ident.toChars());
                        else
                            error(loc, new BytePtr("%s `%s` is used as a type"), s.value.kind(), s.value.toPrettyChars(false));
                    }
                    else
                        error(loc, new BytePtr("`%s` is used as a type"), mtype.toChars());
                    return error.invoke();
                }
            }
        };
        Function1<TypeInstance,Type> visitInstance = new Function1<TypeInstance,Type>(){
            public Type invoke(TypeInstance mtype){
                Ref<Type> t = ref(null);
                Ref<Expression> e = ref(null);
                Ref<Dsymbol> s = ref(null);
                {
                    int errors = global.errors;
                    resolve(mtype, loc, sc_ref.value, ptr(e), ptr(t), ptr(s), false);
                    if ((!(t.value != null) && errors != global.errors))
                        return error.invoke();
                }
                if (!(t.value != null))
                {
                    if (((!(e.value != null) && s.value != null) && s.value.errors))
                    {
                        error(loc, new BytePtr("`%s` had previous errors"), mtype.toChars());
                    }
                    else
                        error(loc, new BytePtr("`%s` is used as a type"), mtype.toChars());
                    return error.invoke();
                }
                return t.value;
            }
        };
        Function1<TypeTypeof,Type> visitTypeof = new Function1<TypeTypeof,Type>(){
            public Type invoke(TypeTypeof mtype){
                Ref<Expression> e = ref(null);
                Ref<Type> t = ref(null);
                Ref<Dsymbol> s = ref(null);
                resolve(mtype, loc, sc_ref.value, ptr(e), ptr(t), ptr(s), false);
                if ((s.value != null && (t.value = s.value.getType()) != null))
                    t.value = t.value.addMod(mtype.mod);
                if (!(t.value != null))
                {
                    error(loc, new BytePtr("`%s` is used as a type"), mtype.toChars());
                    return error.invoke();
                }
                return t.value;
            }
        };
        Function1<TypeTraits,Type> visitTraits = new Function1<TypeTraits,Type>(){
            public Type invoke(TypeTraits mtype){
                if ((mtype.ty & 0xFF) == ENUMTY.Terror)
                    return mtype;
                if ((((((((((!pequals(mtype.exp.ident, Id.allMembers) && !pequals(mtype.exp.ident, Id.derivedMembers)) && !pequals(mtype.exp.ident, Id.getMember)) && !pequals(mtype.exp.ident, Id.parent)) && !pequals(mtype.exp.ident, Id.getOverloads)) && !pequals(mtype.exp.ident, Id.getVirtualFunctions)) && !pequals(mtype.exp.ident, Id.getVirtualMethods)) && !pequals(mtype.exp.ident, Id.getAttributes)) && !pequals(mtype.exp.ident, Id.getUnitTests)) && !pequals(mtype.exp.ident, Id.getAliasThis)))
                {
                    error(mtype.loc, new BytePtr("trait `%s` is either invalid or not supported %s"), mtype.exp.ident.toChars(), typesem.visitTraitsctxt.get((mtype.inAliasDeclaration ? 1 : 0)));
                    mtype.ty = (byte)34;
                    return mtype;
                }
                Type result = null;
                {
                    Expression e = semanticTraits(mtype.exp, sc_ref.value);
                    if (e != null)
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
                                mtype.sym = new TupleDeclaration(e.loc, Identifier.generateId(new BytePtr("__aliastup")), (DArray<RootObject>)e.toTupleExp().exps);
                                break;
                            case 30:
                                result = isType(((DotTypeExp)e).sym);
                                break;
                            case 20:
                                result = ((TypeExp)e).type;
                                break;
                            case 214:
                                result = ((OverExp)e).type;
                                break;
                            default:
                            break;
                        }
                    }
                }
                if (result != null)
                    result = result.addMod(mtype.mod);
                if ((!(mtype.inAliasDeclaration) && !(result != null)))
                {
                    if (!((global.errors) != 0))
                        error(mtype.loc, new BytePtr("`%s` does not give a valid type"), mtype.toChars());
                    return error.invoke();
                }
                return result;
            }
        };
        Function1<TypeReturn,Type> visitReturn = new Function1<TypeReturn,Type>(){
            public Type invoke(TypeReturn mtype){
                Ref<Expression> e = ref(null);
                Ref<Type> t = ref(null);
                Ref<Dsymbol> s = ref(null);
                resolve(mtype, loc, sc_ref.value, ptr(e), ptr(t), ptr(s), false);
                if ((s.value != null && (t.value = s.value.getType()) != null))
                    t.value = t.value.addMod(mtype.mod);
                if (!(t.value != null))
                {
                    error(loc, new BytePtr("`%s` is used as a type"), mtype.toChars());
                    return error.invoke();
                }
                return t.value;
            }
        };
        Function1<TypeStruct,Type> visitStruct = new Function1<TypeStruct,Type>(){
            public Type invoke(TypeStruct mtype){
                if (mtype.deco != null)
                {
                    if ((sc_ref.value != null && (sc_ref.value).cppmangle != CPPMANGLE.def))
                    {
                        if (mtype.cppmangle == CPPMANGLE.def)
                            mtype.cppmangle = (sc_ref.value).cppmangle;
                    }
                    return mtype;
                }
                assert(mtype.sym.parent != null);
                if ((mtype.sym.type.ty & 0xFF) == ENUMTY.Terror)
                    return error.invoke();
                if ((sc_ref.value != null && (sc_ref.value).cppmangle != CPPMANGLE.def))
                    mtype.cppmangle = (sc_ref.value).cppmangle;
                else
                    mtype.cppmangle = CPPMANGLE.asStruct;
                return merge(mtype);
            }
        };
        Function1<TypeEnum,Type> visitEnum = new Function1<TypeEnum,Type>(){
            public Type invoke(TypeEnum mtype){
                return mtype.deco != null ? mtype : merge(mtype);
            }
        };
        Function1<TypeClass,Type> visitClass = new Function1<TypeClass,Type>(){
            public Type invoke(TypeClass mtype){
                if (mtype.deco != null)
                {
                    if ((sc_ref.value != null && (sc_ref.value).cppmangle != CPPMANGLE.def))
                    {
                        if (mtype.cppmangle == CPPMANGLE.def)
                            mtype.cppmangle = (sc_ref.value).cppmangle;
                    }
                    return mtype;
                }
                assert(mtype.sym.parent != null);
                if ((mtype.sym.type.ty & 0xFF) == ENUMTY.Terror)
                    return error.invoke();
                if ((sc_ref.value != null && (sc_ref.value).cppmangle != CPPMANGLE.def))
                    mtype.cppmangle = (sc_ref.value).cppmangle;
                else
                    mtype.cppmangle = CPPMANGLE.asClass;
                return merge(mtype);
            }
        };
        Function1<TypeTuple,Type> visitTuple = new Function1<TypeTuple,Type>(){
            public Type invoke(TypeTuple mtype){
                if (mtype.deco == null)
                    mtype.deco = pcopy(merge(mtype).deco);
                return mtype;
            }
        };
        Function1<TypeSlice,Type> visitSlice = new Function1<TypeSlice,Type>(){
            public Type invoke(TypeSlice mtype){
                Type tn = typeSemantic(mtype.next, loc, sc_ref.value);
                Type tbn = tn.toBasetype();
                if ((tbn.ty & 0xFF) != ENUMTY.Ttuple)
                {
                    error(loc, new BytePtr("can only slice tuple types, not `%s`"), tbn.toChars());
                    return error.invoke();
                }
                TypeTuple tt = (TypeTuple)tbn;
                mtype.lwr = semanticLength(sc_ref.value, tbn, mtype.lwr);
                mtype.upr = semanticLength(sc_ref.value, tbn, mtype.upr);
                mtype.lwr = mtype.lwr.ctfeInterpret();
                mtype.upr = mtype.upr.ctfeInterpret();
                if (((mtype.lwr.op & 0xFF) == 127 || (mtype.upr.op & 0xFF) == 127))
                    return error.invoke();
                long i1 = mtype.lwr.toUInteger();
                long i2 = mtype.upr.toUInteger();
                if (!((i1 <= i2 && i2 <= (long)(tt.arguments).length)))
                {
                    error(loc, new BytePtr("slice `[%llu..%llu]` is out of range of `[0..%llu]`"), i1, i2, (long)(tt.arguments).length);
                    return error.invoke();
                }
                mtype.next = tn;
                mtype.transitive();
                DArray<Parameter> args = new DArray<Parameter>();
                (args).reserve((int)(i2 - i1));
                {
                    Slice<Parameter> __r1724 = (tt.arguments).opSlice((int)i1, (int)i2).copy();
                    int __key1725 = 0;
                    for (; __key1725 < __r1724.getLength();__key1725 += 1) {
                        Parameter arg = __r1724.get(__key1725);
                        (args).push(arg);
                    }
                }
                Type t = new TypeTuple(args);
                return typeSemantic(t, loc, sc_ref.value);
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
                    if ((type.nextOf() != null && type.nextOf().deco == null))
                        return type;
                    break;
                }
            } while(__dispatch10 != 0);
        }
        if (type.deco == null)
        {
            OutBuffer buf = new OutBuffer();
            try {
                buf.reserve(32);
                mangleToBuffer(type, buf);
                StringValue sv = Type.stringtable.update(buf.extractSlice());
                if ((sv).ptrvalue != null)
                {
                    Type t = (Type)(sv).ptrvalue;
                    assert(t.deco != null);
                    return t;
                }
                else
                {
                    Type t = stripDefaultArgs(type);
                    (sv).ptrvalue = pcopy(((Object)t));
                    type.deco = pcopy((t.deco = pcopy((sv).toDchars())));
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
            public Expression invoke(Type mt){
                Expression e = null;
                if (pequals(ident_ref.value, Id.__sizeof))
                {
                    long sz = mt.size(loc);
                    if (sz == -1L)
                        return new ErrorExp();
                    e = new IntegerExp(loc, sz, Type.tsize_t);
                }
                else if (pequals(ident_ref.value, Id.__xalignof))
                {
                    int explicitAlignment = mt.alignment();
                    int naturalAlignment = mt.alignsize();
                    int actualAlignment = explicitAlignment == -1 ? naturalAlignment : explicitAlignment;
                    e = new IntegerExp(loc, (long)actualAlignment, Type.tsize_t);
                }
                else if (pequals(ident_ref.value, Id._init))
                {
                    Type tb = mt.toBasetype();
                    e = mt.defaultInitLiteral(loc);
                    if (((tb.ty & 0xFF) == ENUMTY.Tstruct && tb.needsNested()))
                    {
                        e.isStructLiteralExp().useStaticInit = true;
                    }
                }
                else if (pequals(ident_ref.value, Id._mangleof))
                {
                    if (mt.deco == null)
                    {
                        error(loc, new BytePtr("forward reference of type `%s.mangleof`"), mt.toChars());
                        e = new ErrorExp();
                    }
                    else
                    {
                        e = new StringExp(loc, mt.deco);
                        Scope sc = new Scope().copy();
                        e = expressionSemantic(e, sc);
                    }
                }
                else if (pequals(ident_ref.value, Id.stringof))
                {
                    BytePtr s = pcopy(mt.toChars());
                    e = new StringExp(loc, s);
                    Scope sc = new Scope().copy();
                    e = expressionSemantic(e, sc);
                }
                else if (((flag_ref.value) != 0 && !pequals(mt, Type.terror)))
                {
                    return null;
                }
                else
                {
                    Dsymbol s = null;
                    if ((((mt.ty & 0xFF) == ENUMTY.Tstruct || (mt.ty & 0xFF) == ENUMTY.Tclass) || (mt.ty & 0xFF) == ENUMTY.Tenum))
                        s = mt.toDsymbol(null);
                    if (s != null)
                        s = s.search_correct(ident_ref.value);
                    if (!pequals(mt, Type.terror))
                    {
                        if (s != null)
                            error(loc, new BytePtr("no property `%s` for type `%s`, did you mean `%s`?"), ident_ref.value.toChars(), mt.toChars(), s.toPrettyChars(false));
                        else
                        {
                            if ((pequals(ident_ref.value, Id.call) && (mt.ty & 0xFF) == ENUMTY.Tclass))
                                error(loc, new BytePtr("no property `%s` for type `%s`, did you mean `new %s`?"), ident_ref.value.toChars(), mt.toChars(), mt.toPrettyChars(false));
                            else
                                error(loc, new BytePtr("no property `%s` for type `%s`"), ident_ref.value.toChars(), mt.toChars());
                        }
                    }
                    e = new ErrorExp();
                }
                return e;
            }
        };
        Function1<TypeError,Expression> visitError = new Function1<TypeError,Expression>(){
            public Expression invoke(TypeError _param_0){
                return new ErrorExp();
            }
        };
        Function1<TypeBasic,Expression> visitBasic = new Function1<TypeBasic,Expression>(){
            public Expression invoke(TypeBasic mt){
                Ref<TypeBasic> mt_ref = ref(mt);
                Function1<Long,Expression> integerValue = new Function1<Long,Expression>(){
                    public Expression invoke(Long i){
                        return new IntegerExp(loc, i, mt_ref.value);
                    }
                };
                Function1<Long,Expression> intValue = new Function1<Long,Expression>(){
                    public Expression invoke(Long i){
                        return new IntegerExp(loc, i, Type.tint32);
                    }
                };
                Function1<Double,Expression> floatValue = new Function1<Double,Expression>(){
                    public Expression invoke(Double r){
                        if ((mt_ref.value.isreal() || mt_ref.value.isimaginary()))
                            return new RealExp(loc, r, mt_ref.value);
                        else
                        {
                            return new ComplexExp(loc, new complex_t(r, r), mt_ref.value);
                        }
                    }
                };
                if (pequals(ident_ref.value, Id.max))
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
                            return floatValue.invoke(target.FloatProperties.max);
                        case 28:
                        case 25:
                        case 22:
                            return floatValue.invoke(target.DoubleProperties.max);
                        case 29:
                        case 26:
                        case 23:
                            return floatValue.invoke(target.RealProperties.max);
                        default:
                        break;
                    }
                }
                else if (pequals(ident_ref.value, Id.min))
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
                else if (pequals(ident_ref.value, Id.min_normal))
                {
                    switch ((mt_ref.value.ty & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return floatValue.invoke(target.FloatProperties.min_normal);
                        case 28:
                        case 25:
                        case 22:
                            return floatValue.invoke(target.DoubleProperties.min_normal);
                        case 29:
                        case 26:
                        case 23:
                            return floatValue.invoke(target.RealProperties.min_normal);
                        default:
                        break;
                    }
                }
                else if (pequals(ident_ref.value, Id.nan))
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
                            return floatValue.invoke(target.RealProperties.nan);
                        default:
                        break;
                    }
                }
                else if (pequals(ident_ref.value, Id.infinity))
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
                            return floatValue.invoke(target.RealProperties.infinity);
                        default:
                        break;
                    }
                }
                else if (pequals(ident_ref.value, Id.dig))
                {
                    switch ((mt_ref.value.ty & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return intValue.invoke((long)target.FloatProperties.dig);
                        case 28:
                        case 25:
                        case 22:
                            return intValue.invoke((long)target.DoubleProperties.dig);
                        case 29:
                        case 26:
                        case 23:
                            return intValue.invoke((long)target.RealProperties.dig);
                        default:
                        break;
                    }
                }
                else if (pequals(ident_ref.value, Id.epsilon))
                {
                    switch ((mt_ref.value.ty & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return floatValue.invoke(target.FloatProperties.epsilon);
                        case 28:
                        case 25:
                        case 22:
                            return floatValue.invoke(target.DoubleProperties.epsilon);
                        case 29:
                        case 26:
                        case 23:
                            return floatValue.invoke(target.RealProperties.epsilon);
                        default:
                        break;
                    }
                }
                else if (pequals(ident_ref.value, Id.mant_dig))
                {
                    switch ((mt_ref.value.ty & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return intValue.invoke((long)target.FloatProperties.mant_dig);
                        case 28:
                        case 25:
                        case 22:
                            return intValue.invoke((long)target.DoubleProperties.mant_dig);
                        case 29:
                        case 26:
                        case 23:
                            return intValue.invoke((long)target.RealProperties.mant_dig);
                        default:
                        break;
                    }
                }
                else if (pequals(ident_ref.value, Id.max_10_exp))
                {
                    switch ((mt_ref.value.ty & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return intValue.invoke((long)target.FloatProperties.max_10_exp);
                        case 28:
                        case 25:
                        case 22:
                            return intValue.invoke((long)target.DoubleProperties.max_10_exp);
                        case 29:
                        case 26:
                        case 23:
                            return intValue.invoke((long)target.RealProperties.max_10_exp);
                        default:
                        break;
                    }
                }
                else if (pequals(ident_ref.value, Id.max_exp))
                {
                    switch ((mt_ref.value.ty & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return intValue.invoke((long)target.FloatProperties.max_exp);
                        case 28:
                        case 25:
                        case 22:
                            return intValue.invoke((long)target.DoubleProperties.max_exp);
                        case 29:
                        case 26:
                        case 23:
                            return intValue.invoke((long)target.RealProperties.max_exp);
                        default:
                        break;
                    }
                }
                else if (pequals(ident_ref.value, Id.min_10_exp))
                {
                    switch ((mt_ref.value.ty & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return intValue.invoke((long)target.FloatProperties.min_10_exp);
                        case 28:
                        case 25:
                        case 22:
                            return intValue.invoke((long)target.DoubleProperties.min_10_exp);
                        case 29:
                        case 26:
                        case 23:
                            return intValue.invoke((long)target.RealProperties.min_10_exp);
                        default:
                        break;
                    }
                }
                else if (pequals(ident_ref.value, Id.min_exp))
                {
                    switch ((mt_ref.value.ty & 0xFF))
                    {
                        case 27:
                        case 24:
                        case 21:
                            return intValue.invoke((long)target.FloatProperties.min_exp);
                        case 28:
                        case 25:
                        case 22:
                            return intValue.invoke((long)target.DoubleProperties.min_exp);
                        case 29:
                        case 26:
                        case 23:
                            return intValue.invoke((long)target.RealProperties.min_exp);
                        default:
                        break;
                    }
                }
                return visitType.invoke(mt_ref.value);
            }
        };
        Function1<TypeVector,Expression> visitVector = new Function1<TypeVector,Expression>(){
            public Expression invoke(TypeVector mt){
                return visitType.invoke(mt);
            }
        };
        Function1<TypeEnum,Expression> visitEnum = new Function1<TypeEnum,Expression>(){
            public Expression invoke(TypeEnum mt){
                Expression e = null;
                if ((pequals(ident_ref.value, Id.max) || pequals(ident_ref.value, Id.min)))
                {
                    return mt.sym.getMaxMinValue(loc, ident_ref.value);
                }
                else if (pequals(ident_ref.value, Id._init))
                {
                    e = mt.defaultInitLiteral(loc);
                }
                else if (pequals(ident_ref.value, Id.stringof))
                {
                    BytePtr s = pcopy(mt.toChars());
                    e = new StringExp(loc, s);
                    Scope sc = new Scope().copy();
                    e = expressionSemantic(e, sc);
                }
                else if (pequals(ident_ref.value, Id._mangleof))
                {
                    e = visitType.invoke(mt);
                }
                else
                {
                    e = getProperty(mt.toBasetype(), loc, ident_ref.value, flag_ref.value);
                }
                return e;
            }
        };
        Function1<TypeTuple,Expression> visitTuple = new Function1<TypeTuple,Expression>(){
            public Expression invoke(TypeTuple mt){
                Expression e = null;
                if (pequals(ident_ref.value, Id.length))
                {
                    e = new IntegerExp(loc, (long)(mt.arguments).length, Type.tsize_t);
                }
                else if (pequals(ident_ref.value, Id._init))
                {
                    e = mt.defaultInitLiteral(loc);
                }
                else if ((flag_ref.value) != 0)
                {
                    e = null;
                }
                else
                {
                    error(loc, new BytePtr("no property `%s` for tuple `%s`"), ident_ref.value.toChars(), mt.toChars());
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
                        pt.set(0, e.type);
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
                    pe.set(0, e);
                    return ;
                }
            } while(__dispatch24 != 0);
        }
        ps.set(0, s);
    }

    public static void resolve(Type mt, Loc loc, Scope sc, Ptr<Expression> pe, Ptr<Type> pt, Ptr<Dsymbol> ps, boolean intypeid) {
        Ref<Scope> sc_ref = ref(sc);
        Ref<Ptr<Expression>> pe_ref = ref(pe);
        Ref<Ptr<Type>> pt_ref = ref(pt);
        Ref<Ptr<Dsymbol>> ps_ref = ref(ps);
        Ref<Boolean> intypeid_ref = ref(intypeid);
        Function1<Expression,Void> returnExp = new Function1<Expression,Void>(){
            public Void invoke(Expression e){
                pt_ref.value.set(0, null);
                pe_ref.value.set(0, e);
                ps_ref.value.set(0, null);
                return null;
            }
        };
        Function1<Type,Void> returnType = new Function1<Type,Void>(){
            public Void invoke(Type t){
                pt_ref.value.set(0, t);
                pe_ref.value.set(0, null);
                ps_ref.value.set(0, null);
                return null;
            }
        };
        Function1<Dsymbol,Void> returnSymbol = new Function1<Dsymbol,Void>(){
            public Void invoke(Dsymbol s){
                pt_ref.value.set(0, null);
                pe_ref.value.set(0, null);
                ps_ref.value.set(0, s);
                return null;
            }
        };
        Function0<Void> returnError = new Function0<Void>(){
            public Void invoke(){
                returnType.invoke(Type.terror);
                return null;
            }
        };
        Function1<Type,Void> visitType = new Function1<Type,Void>(){
            public Void invoke(Type mt){
                Type t = typeSemantic(mt, loc, sc_ref.value);
                assert(t != null);
                returnType.invoke(t);
                return null;
            }
        };
        Function1<TypeSArray,Void> visitSArray = new Function1<TypeSArray,Void>(){
            public Void invoke(TypeSArray mt){
                resolve(mt.next, loc, sc_ref.value, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                if (pe_ref.value.get() != null)
                {
                    {
                        Dsymbol s = getDsymbol(pe_ref.value.get());
                        if (s != null)
                            pe_ref.value.set(0, (new DsymbolExp(loc, s, true)));
                    }
                    returnExp.invoke(new ArrayExp(loc, pe_ref.value.get(), mt.dim));
                }
                else if (ps_ref.value.get() != null)
                {
                    Dsymbol s = ps_ref.value.get();
                    {
                        TupleDeclaration tup = s.isTupleDeclaration();
                        if (tup != null)
                        {
                            mt.dim = semanticLength(sc_ref.value, tup, mt.dim);
                            mt.dim = mt.dim.ctfeInterpret();
                            if ((mt.dim.op & 0xFF) == 127)
                                returnError.invoke();
                                return null;
                            long d = mt.dim.toUInteger();
                            if (d >= (long)(tup.objects).length)
                            {
                                error(loc, new BytePtr("tuple index `%llu` exceeds length %u"), d, (tup.objects).length);
                                returnError.invoke();
                                return null;
                            }
                            RootObject o = (tup.objects).get((int)d);
                            if (o.dyncast() == DYNCAST.dsymbol)
                            {
                                returnSymbol.invoke((Dsymbol)o);
                                return null;
                            }
                            if (o.dyncast() == DYNCAST.expression)
                            {
                                Expression e = (Expression)o;
                                if ((e.op & 0xFF) == 41)
                                    returnSymbol.invoke(((DsymbolExp)e).s);
                                    return null;
                                else
                                    returnExp.invoke(e);
                                    return null;
                            }
                            if (o.dyncast() == DYNCAST.type)
                            {
                                returnType.invoke(((Type)o).addMod(mt.mod));
                                return null;
                            }
                            DArray<RootObject> objects = new DArray<RootObject>(1);
                            objects.set(0, o);
                            returnSymbol.invoke(new TupleDeclaration(loc, tup.ident, objects));
                            return null;
                        }
                        else
                            visitType.invoke(mt);
                            return null;
                    }
                }
                else
                {
                    if (((pt_ref.value.get()).ty & 0xFF) != ENUMTY.Terror)
                        mt.next = pt_ref.value.get();
                    visitType.invoke(mt);
                }
                return null;
            }
        };
        Function1<TypeDArray,Void> visitDArray = new Function1<TypeDArray,Void>(){
            public Void invoke(TypeDArray mt){
                resolve(mt.next, loc, sc_ref.value, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                if (pe_ref.value.get() != null)
                {
                    {
                        Dsymbol s = getDsymbol(pe_ref.value.get());
                        if (s != null)
                            pe_ref.value.set(0, (new DsymbolExp(loc, s, true)));
                    }
                    returnExp.invoke(new ArrayExp(loc, pe_ref.value.get(), null));
                }
                else if (ps_ref.value.get() != null)
                {
                    {
                        TupleDeclaration tup = (ps_ref.value.get()).isTupleDeclaration();
                        if (tup != null)
                        {
                        }
                        else
                            visitType.invoke(mt);
                    }
                }
                else
                {
                    if (((pt_ref.value.get()).ty & 0xFF) != ENUMTY.Terror)
                        mt.next = pt_ref.value.get();
                    visitType.invoke(mt);
                }
                return null;
            }
        };
        Function1<TypeAArray,Void> visitAArray = new Function1<TypeAArray,Void>(){
            public Void invoke(TypeAArray mt){
                if ((((mt.index.ty & 0xFF) == ENUMTY.Tident || (mt.index.ty & 0xFF) == ENUMTY.Tinstance) || (mt.index.ty & 0xFF) == ENUMTY.Tsarray))
                {
                    Ref<Expression> e = ref(null);
                    Ref<Type> t = ref(null);
                    Ref<Dsymbol> s = ref(null);
                    resolve(mt.index, loc, sc_ref.value, ptr(e), ptr(t), ptr(s), intypeid_ref.value);
                    if (e.value != null)
                    {
                        TypeSArray tsa = new TypeSArray(mt.next, e.value);
                        tsa.mod = mt.mod;
                        resolve(tsa, loc, sc_ref.value, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                        return null;
                    }
                    else if (t.value != null)
                        mt.index = t.value;
                    else
                        error(loc, new BytePtr("index is not a type or an expression"));
                }
                visitType.invoke(mt);
                return null;
            }
        };
        Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>(){
            public Void invoke(Dsymbol s){
                semanticOnMixin.invoke(s);
                return null;
                return null;
            }
        };
        Function1<TypeIdentifier,Void> visitIdentifier = new Function1<TypeIdentifier,Void>(){
            public Void invoke(TypeIdentifier mt){
                if (((mt.ident.equals(Id._super) || mt.ident.equals(Id.This)) && !(hasThis(sc_ref.value) != null)))
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
                        AggregateDeclaration ad = (sc_ref.value).getStructClassScope();
                        if (ad != null)
                        {
                            {
                                ClassDeclaration cd = ad.isClassDeclaration();
                                if (cd != null)
                                {
                                    if (mt.ident.equals(Id.This))
                                        mt.ident = cd.ident;
                                    else if ((cd.baseClass != null && mt.ident.equals(Id._super)))
                                        mt.ident = cd.baseClass.ident;
                                }
                                else
                                {
                                    StructDeclaration sd = ad.isStructDeclaration();
                                    if ((sd != null && mt.ident.equals(Id.This)))
                                        mt.ident = sd.ident;
                                }
                            }
                        }
                    }
                }
                if (pequals(mt.ident, Id.ctfe))
                {
                    error(loc, new BytePtr("variable `__ctfe` cannot be read at compile time"));
                    returnError.invoke();
                    return null;
                }
                Ref<Dsymbol> scopesym = ref(null);
                Dsymbol s = (sc_ref.value).search(loc, mt.ident, ptr(scopesym), 0);
                if ((!(s != null) && (sc_ref.value).enclosing != null))
                {
                    ScopeDsymbol sds = ((sc_ref.value).enclosing).scopesym;
                    if ((sds != null && sds.members != null))
                    {
                        Function1<Dsymbol,Void> semanticOnMixin = new Function1<Dsymbol,Void>(){
                            public Void invoke(Dsymbol member){
                                {
                                    CompileDeclaration compileDecl = member.isCompileDeclaration();
                                    if (compileDecl != null)
                                        dsymbolSemantic(compileDecl, sc_ref.value);
                                    else {
                                        TemplateMixin mixinTempl = member.isTemplateMixin();
                                        if (mixinTempl != null)
                                            dsymbolSemantic(mixinTempl, sc_ref.value);
                                    }
                                }
                                return null;
                            }
                        };
                        foreachDsymbol(sds.members, __lambda3);
                        s = (sc_ref.value).search(loc, mt.ident, ptr(scopesym), 0);
                    }
                }
                if (s != null)
                {
                    {
                        FuncDeclaration f = s.isFuncDeclaration();
                        if (f != null)
                        {
                            {
                                TemplateDeclaration td = getFuncTemplateDecl(f);
                                if (td != null)
                                {
                                    if (td.overroot != null)
                                        td = td.overroot;
                                    s = td;
                                }
                            }
                        }
                    }
                }
                resolveHelper(mt, loc, sc_ref.value, s, scopesym.value, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                if (pt_ref.value.get() != null)
                    pt_ref.value.set(0, (pt_ref.value.get()).addMod(mt.mod));
                return null;
            }
        };
        Function1<TypeInstance,Void> visitInstance = new Function1<TypeInstance,Void>(){
            public Void invoke(TypeInstance mt){
                dsymbolSemantic(mt.tempinst, sc_ref.value);
                if ((!((global.gag) != 0) && mt.tempinst.errors))
                    returnError.invoke();
                    return null;
                resolveHelper(mt, loc, sc_ref.value, mt.tempinst, null, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                if (pt_ref.value.get() != null)
                    pt_ref.value.set(0, (pt_ref.value.get()).addMod(mt.mod));
                return null;
            }
        };
        Function1<TypeTypeof,Void> visitTypeof = new Function1<TypeTypeof,Void>(){
            public Void invoke(TypeTypeof mt){
                if (sc_ref.value == null)
                {
                    error(loc, new BytePtr("Invalid scope."));
                    returnError.invoke();
                    return null;
                }
                if ((mt.inuse) != 0)
                {
                    mt.inuse = 2;
                    error(loc, new BytePtr("circular `typeof` definition"));
                /*Lerr:*/
                    mt.inuse--;
                    returnError.invoke();
                    return null;
                }
                mt.inuse++;
                Scope sc2 = (sc_ref.value).push();
                (sc2).intypeof = 1;
                Expression exp2 = expressionSemantic(mt.exp, sc2);
                exp2 = resolvePropertiesOnly(sc2, exp2);
                (sc2).pop();
                if ((exp2.op & 0xFF) == 127)
                {
                    if (!((global.gag) != 0))
                        mt.exp = exp2;
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                mt.exp = exp2;
                if (((mt.exp.op & 0xFF) == 20 || (mt.exp.op & 0xFF) == 203))
                {
                    if (mt.exp.checkType())
                        /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                {
                    FuncDeclaration f = (mt.exp.op & 0xFF) == 26 ? ((VarExp)mt.exp).var.isFuncDeclaration() : (mt.exp.op & 0xFF) == 27 ? ((DotVarExp)mt.exp).var.isFuncDeclaration() : null;
                    if (f != null)
                    {
                        if (f.checkForwardRef(loc))
                            /*goto Lerr*/throw Dispatch0.INSTANCE;
                    }
                }
                {
                    FuncDeclaration f = isFuncAddress(mt.exp, null);
                    if (f != null)
                    {
                        if (f.checkForwardRef(loc))
                            /*goto Lerr*/throw Dispatch0.INSTANCE;
                    }
                }
                Type t = mt.exp.type;
                if (!(t != null))
                {
                    error(loc, new BytePtr("expression `%s` has no type"), mt.exp.toChars());
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                if ((t.ty & 0xFF) == ENUMTY.Ttypeof)
                {
                    error(loc, new BytePtr("forward reference to `%s`"), mt.toChars());
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                }
                if (mt.idents.length == 0)
                {
                    returnType.invoke(t.addMod(mt.mod));
                }
                else
                {
                    {
                        Dsymbol s = t.toDsymbol(sc_ref.value);
                        if (s != null)
                            resolveHelper(mt, loc, sc_ref.value, s, null, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                        else
                        {
                            Expression e = typeToExpressionHelper(mt, new TypeExp(loc, t), 0);
                            e = expressionSemantic(e, sc_ref.value);
                            resolveExp(e, pt_ref.value, pe_ref.value, ps_ref.value);
                        }
                    }
                    if (pt_ref.value.get() != null)
                        pt_ref.value.set(0, (pt_ref.value.get()).addMod(mt.mod));
                }
                mt.inuse--;
                return null;
            }
        };
        Function1<TypeReturn,Void> visitReturn = new Function1<TypeReturn,Void>(){
            public Void invoke(TypeReturn mt){
                Type t = null;
                {
                    FuncDeclaration func = (sc_ref.value).func;
                    if (!(func != null))
                    {
                        error(loc, new BytePtr("`typeof(return)` must be inside function"));
                        returnError.invoke();
                        return null;
                    }
                    if (func.fes != null)
                        func = func.fes.func;
                    t = func.type.nextOf();
                    if (!(t != null))
                    {
                        error(loc, new BytePtr("cannot use `typeof(return)` inside function `%s` with inferred return type"), (sc_ref.value).func.toChars());
                        returnError.invoke();
                        return null;
                    }
                }
                if (mt.idents.length == 0)
                {
                    returnType.invoke(t.addMod(mt.mod));
                    return null;
                }
                else
                {
                    {
                        Dsymbol s = t.toDsymbol(sc_ref.value);
                        if (s != null)
                            resolveHelper(mt, loc, sc_ref.value, s, null, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                        else
                        {
                            Expression e = typeToExpressionHelper(mt, new TypeExp(loc, t), 0);
                            e = expressionSemantic(e, sc_ref.value);
                            resolveExp(e, pt_ref.value, pe_ref.value, ps_ref.value);
                        }
                    }
                    if (pt_ref.value.get() != null)
                        pt_ref.value.set(0, (pt_ref.value.get()).addMod(mt.mod));
                }
                return null;
            }
        };
        Function1<TypeSlice,Void> visitSlice = new Function1<TypeSlice,Void>(){
            public Void invoke(TypeSlice mt){
                resolve(mt.next, loc, sc_ref.value, pe_ref.value, pt_ref.value, ps_ref.value, intypeid_ref.value);
                if (pe_ref.value.get() != null)
                {
                    {
                        Dsymbol s = getDsymbol(pe_ref.value.get());
                        if (s != null)
                            pe_ref.value.set(0, (new DsymbolExp(loc, s, true)));
                    }
                    returnExp.invoke(new ArrayExp(loc, pe_ref.value.get(), new IntervalExp(loc, mt.lwr, mt.upr)));
                    return null;
                }
                else if (ps_ref.value.get() != null)
                {
                    Dsymbol s = ps_ref.value.get();
                    TupleDeclaration td = s.isTupleDeclaration();
                    if (td != null)
                    {
                        ScopeDsymbol sym = new ArrayScopeSymbol(sc_ref.value, td);
                        sym.parent = (sc_ref.value).scopesym;
                        sc_ref.value = (sc_ref.value).push(sym);
                        sc_ref.value = (sc_ref.value).startCTFE();
                        mt.lwr = expressionSemantic(mt.lwr, sc_ref.value);
                        mt.upr = expressionSemantic(mt.upr, sc_ref.value);
                        sc_ref.value = (sc_ref.value).endCTFE();
                        sc_ref.value = (sc_ref.value).pop();
                        mt.lwr = mt.lwr.ctfeInterpret();
                        mt.upr = mt.upr.ctfeInterpret();
                        long i1 = mt.lwr.toUInteger();
                        long i2 = mt.upr.toUInteger();
                        if (!((i1 <= i2 && i2 <= (long)(td.objects).length)))
                        {
                            error(loc, new BytePtr("slice `[%llu..%llu]` is out of range of [0..%u]"), i1, i2, (td.objects).length);
                            returnError.invoke();
                            return null;
                        }
                        if ((i1 == 0L && i2 == (long)(td.objects).length))
                        {
                            returnSymbol.invoke(td);
                            return null;
                        }
                        DArray<RootObject> objects = new DArray<RootObject>((int)(i2 - i1));
                        {
                            int i = 0;
                            for (; i < (objects).length;i++){
                                objects.set(i, (td.objects).get((int)i1 + i));
                            }
                        }
                        returnSymbol.invoke(new TupleDeclaration(loc, td.ident, objects));
                        return null;
                    }
                    else
                        visitType.invoke(mt);
                }
                else
                {
                    if (((pt_ref.value.get()).ty & 0xFF) != ENUMTY.Terror)
                        mt.next = pt_ref.value.get();
                    visitType.invoke(mt);
                }
                return null;
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

    public static Expression dotExp(Type mt, Scope sc, Expression e, Identifier ident, int flag) {
        Ref<Scope> sc_ref = ref(sc);
        Ref<Expression> e_ref = ref(e);
        Ref<Identifier> ident_ref = ref(ident);
        IntRef flag_ref = ref(flag);
        Function1<Type,Expression> visitType = new Function1<Type,Expression>(){
            public Expression invoke(Type mt){
                VarDeclaration v = null;
                Expression ex = e_ref.value;
                for (; (ex.op & 0xFF) == 99;) {
                    ex = ((CommaExp)ex).e2;
                }
                if ((ex.op & 0xFF) == 27)
                {
                    DotVarExp dv = (DotVarExp)ex;
                    v = dv.var.isVarDeclaration();
                }
                else if ((ex.op & 0xFF) == 26)
                {
                    VarExp ve = (VarExp)ex;
                    v = ve.var.isVarDeclaration();
                }
                try {
                    if (v != null)
                    {
                        if (pequals(ident_ref.value, Id.offsetof))
                        {
                            if (v.isField())
                            {
                                AggregateDeclaration ad = v.toParent().isAggregateDeclaration();
                                objc().checkOffsetof(e_ref.value, ad);
                                ad.size(e_ref.value.loc);
                                if (ad.sizeok != Sizeok.done)
                                    return new ErrorExp();
                                return new IntegerExp(e_ref.value.loc, (long)v.offset, Type.tsize_t);
                            }
                        }
                        else if (pequals(ident_ref.value, Id._init))
                        {
                            Type tb = mt.toBasetype();
                            e_ref.value = mt.defaultInitLiteral(e_ref.value.loc);
                            if (((tb.ty & 0xFF) == ENUMTY.Tstruct && tb.needsNested()))
                            {
                                e_ref.value.isStructLiteralExp().useStaticInit = true;
                            }
                            /*goto Lreturn*/throw Dispatch0.INSTANCE;
                        }
                    }
                    if (pequals(ident_ref.value, Id.stringof))
                    {
                        BytePtr s = pcopy(e_ref.value.toChars());
                        e_ref.value = new StringExp(e_ref.value.loc, s);
                    }
                    else
                        e_ref.value = getProperty(mt, e_ref.value.loc, ident_ref.value, flag_ref.value & DotExpFlag.gag);
                }
                catch(Dispatch0 __d){}
            /*Lreturn:*/
                if (e_ref.value != null)
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                return e_ref.value;
            }
        };
        Function1<TypeError,Expression> visitError = new Function1<TypeError,Expression>(){
            public Expression invoke(TypeError _param_0){
                return new ErrorExp();
            }
        };
        Function1<TypeBasic,Expression> visitBasic = new Function1<TypeBasic,Expression>(){
            public Expression invoke(TypeBasic mt){
                Type t = null;
                if (pequals(ident_ref.value, Id.re))
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
                                    e_ref.value = e_ref.value.castTo(sc_ref.value, t);
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
                                    e_ref.value = new RealExp(e_ref.value.loc, CTFloat.zero, t);
                                    break;
                                default:
                                e_ref.value = getProperty(mt.Type, e_ref.value.loc, ident_ref.value, flag_ref.value);
                                break;
                            }
                        } while(__dispatch26 != 0);
                    }
                }
                else if (pequals(ident_ref.value, Id.im))
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
                                    e_ref.value = e_ref.value.castTo(sc_ref.value, t);
                                    e_ref.value.type = t2;
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
                                    e_ref.value = e_ref.value.copy();
                                    e_ref.value.type = t;
                                    break;
                                case 21:
                                case 22:
                                case 23:
                                    e_ref.value = new RealExp(e_ref.value.loc, CTFloat.zero, mt);
                                    break;
                                default:
                                e_ref.value = getProperty(mt.Type, e_ref.value.loc, ident_ref.value, flag_ref.value);
                                break;
                            }
                        } while(__dispatch27 != 0);
                    }
                }
                else
                {
                    return visitType.invoke(mt);
                }
                if ((!((flag_ref.value & 1) != 0) || e_ref.value != null))
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                return e_ref.value;
            }
        };
        Function1<TypeVector,Expression> visitVector = new Function1<TypeVector,Expression>(){
            public Expression invoke(TypeVector mt){
                if ((pequals(ident_ref.value, Id.ptr) && (e_ref.value.op & 0xFF) == 18))
                {
                    e_ref.value = new AddrExp(e_ref.value.loc, e_ref.value);
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                    return e_ref.value.castTo(sc_ref.value, mt.basetype.nextOf().pointerTo());
                }
                if (pequals(ident_ref.value, Id.array))
                {
                    e_ref.value = new VectorArrayExp(e_ref.value.loc, e_ref.value);
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                    return e_ref.value;
                }
                if ((((pequals(ident_ref.value, Id._init) || pequals(ident_ref.value, Id.offsetof)) || pequals(ident_ref.value, Id.stringof)) || pequals(ident_ref.value, Id.__xalignof)))
                {
                    return visitType.invoke(mt);
                }
                return dotExp(mt.basetype, sc_ref.value, e_ref.value.castTo(sc_ref.value, mt.basetype), ident_ref.value, flag_ref.value);
            }
        };
        Function1<TypeArray,Expression> visitArray = new Function1<TypeArray,Expression>(){
            public Expression invoke(TypeArray mt){
                e_ref.value = visitType.invoke(mt);
                if ((!((flag_ref.value & 1) != 0) || e_ref.value != null))
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                return e_ref.value;
            }
        };
        Function1<TypeSArray,Expression> visitSArray = new Function1<TypeSArray,Expression>(){
            public Expression invoke(TypeSArray mt){
                if (pequals(ident_ref.value, Id.length))
                {
                    Loc oldLoc = e_ref.value.loc.copy();
                    e_ref.value = mt.dim.copy();
                    e_ref.value.loc = oldLoc.copy();
                }
                else if (pequals(ident_ref.value, Id.ptr))
                {
                    if ((e_ref.value.op & 0xFF) == 20)
                    {
                        e_ref.value.error(new BytePtr("`%s` is not an expression"), e_ref.value.toChars());
                        return new ErrorExp();
                    }
                    else if (((((!((flag_ref.value & DotExpFlag.noDeref) != 0) && (sc_ref.value).func != null) && !(((sc_ref.value).intypeof) != 0)) && (sc_ref.value).func.setUnsafe()) && !(((sc_ref.value).flags & 8) != 0)))
                    {
                        e_ref.value.error(new BytePtr("`%s.ptr` cannot be used in `@safe` code, use `&%s[0]` instead"), e_ref.value.toChars(), e_ref.value.toChars());
                        return new ErrorExp();
                    }
                    e_ref.value = e_ref.value.castTo(sc_ref.value, e_ref.value.type.nextOf().pointerTo());
                }
                else
                {
                    e_ref.value = visitArray.invoke(mt);
                }
                if ((!((flag_ref.value & 1) != 0) || e_ref.value != null))
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                return e_ref.value;
            }
        };
        Function1<TypeDArray,Expression> visitDArray = new Function1<TypeDArray,Expression>(){
            public Expression invoke(TypeDArray mt){
                if (((e_ref.value.op & 0xFF) == 20 && (pequals(ident_ref.value, Id.length) || pequals(ident_ref.value, Id.ptr))))
                {
                    e_ref.value.error(new BytePtr("`%s` is not an expression"), e_ref.value.toChars());
                    return new ErrorExp();
                }
                if (pequals(ident_ref.value, Id.length))
                {
                    if ((e_ref.value.op & 0xFF) == 121)
                    {
                        StringExp se = (StringExp)e_ref.value;
                        return new IntegerExp(se.loc, (long)se.len, Type.tsize_t);
                    }
                    if ((e_ref.value.op & 0xFF) == 13)
                    {
                        return new IntegerExp(e_ref.value.loc, 0L, Type.tsize_t);
                    }
                    if (checkNonAssignmentArrayOp(e_ref.value, false))
                    {
                        return new ErrorExp();
                    }
                    e_ref.value = new ArrayLengthExp(e_ref.value.loc, e_ref.value);
                    e_ref.value.type = Type.tsize_t;
                    return e_ref.value;
                }
                else if (pequals(ident_ref.value, Id.ptr))
                {
                    if (((((!((flag_ref.value & DotExpFlag.noDeref) != 0) && (sc_ref.value).func != null) && !(((sc_ref.value).intypeof) != 0)) && (sc_ref.value).func.setUnsafe()) && !(((sc_ref.value).flags & 8) != 0)))
                    {
                        e_ref.value.error(new BytePtr("`%s.ptr` cannot be used in `@safe` code, use `&%s[0]` instead"), e_ref.value.toChars(), e_ref.value.toChars());
                        return new ErrorExp();
                    }
                    return e_ref.value.castTo(sc_ref.value, mt.next.pointerTo());
                }
                else
                {
                    return visitArray.invoke(mt);
                }
            }
        };
        Function1<TypeAArray,Expression> visitAArray = new Function1<TypeAArray,Expression>(){
            public Expression invoke(TypeAArray mt){
                if (pequals(ident_ref.value, Id.length))
                {
                    if (typesem.visitAArrayfd_aaLen == null)
                    {
                        DArray<Parameter> fparams = new DArray<Parameter>();
                        (fparams).push(new Parameter(2048L, mt, null, null, null));
                        typesem.visitAArrayfd_aaLen = FuncDeclaration.genCfunc(fparams, Type.tsize_t, Id.aaLen, 0L);
                        TypeFunction tf = typesem.visitAArrayfd_aaLen.type.toTypeFunction();
                        tf.purity = PURE.const_;
                        tf.isnothrow = true;
                        tf.isnogc = false;
                    }
                    Expression ev = new VarExp(e_ref.value.loc, typesem.visitAArrayfd_aaLen, false);
                    e_ref.value = new CallExp(e_ref.value.loc, ev, e_ref.value);
                    e_ref.value.type = typesem.visitAArrayfd_aaLen.type.toTypeFunction().next;
                    return e_ref.value;
                }
                else
                {
                    return visitType.invoke(mt);
                }
            }
        };
        Function1<TypeReference,Expression> visitReference = new Function1<TypeReference,Expression>(){
            public Expression invoke(TypeReference mt){
                return dotExp(mt.next, sc_ref.value, e_ref.value, ident_ref.value, flag_ref.value);
            }
        };
        Function1<TypeDelegate,Expression> visitDelegate = new Function1<TypeDelegate,Expression>(){
            public Expression invoke(TypeDelegate mt){
                if (pequals(ident_ref.value, Id.ptr))
                {
                    e_ref.value = new DelegatePtrExp(e_ref.value.loc, e_ref.value);
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                }
                else if (pequals(ident_ref.value, Id.funcptr))
                {
                    if (((((!((flag_ref.value & DotExpFlag.noDeref) != 0) && (sc_ref.value).func != null) && !(((sc_ref.value).intypeof) != 0)) && (sc_ref.value).func.setUnsafe()) && !(((sc_ref.value).flags & 8) != 0)))
                    {
                        e_ref.value.error(new BytePtr("`%s.funcptr` cannot be used in `@safe` code"), e_ref.value.toChars());
                        return new ErrorExp();
                    }
                    e_ref.value = new DelegateFuncptrExp(e_ref.value.loc, e_ref.value);
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                }
                else
                {
                    return visitType.invoke(mt);
                }
                return e_ref.value;
            }
        };
        Function5<Type,Scope,Expression,Identifier,Integer,Expression> noMember = new Function5<Type,Scope,Expression,Identifier,Integer,Expression>(){
            public Expression invoke(Type mt, Scope sc, Expression e, Identifier ident, Integer flag){
                boolean gagError = (boolean)(flag & 1);
                Function1<Expression,Expression> returnExp = new Function1<Expression,Expression>(){
                    public Expression invoke(Expression e){
                        typesem.noMembernest -= 1;
                        return e;
                    }
                };
                if ((typesem.noMembernest += 1) > 500)
                {
                    error(e.loc, new BytePtr("cannot resolve identifier `%s`"), ident.toChars());
                    return returnExp.invoke(gagError ? null : new ErrorExp());
                }
                assert(((mt.ty & 0xFF) == ENUMTY.Tstruct || (mt.ty & 0xFF) == ENUMTY.Tclass));
                AggregateDeclaration sym = mt.toDsymbol(sc).isAggregateDeclaration();
                assert(sym != null);
                if (((((((((((!pequals(ident, Id.__sizeof) && !pequals(ident, Id.__xalignof)) && !pequals(ident, Id._init)) && !pequals(ident, Id._mangleof)) && !pequals(ident, Id.stringof)) && !pequals(ident, Id.offsetof)) && !pequals(ident, Id.ctor)) && !pequals(ident, Id.dtor)) && !pequals(ident, Id.__xdtor)) && !pequals(ident, Id.postblit)) && !pequals(ident, Id.__xpostblit)))
                {
                    {
                        Dsymbol fd = search_function(sym, Id.opDot);
                        if (fd != null)
                        {
                            e = build_overload(e.loc, sc, e, null, fd);
                            e.deprecation(new BytePtr("`opDot` is deprecated. Use `alias this`"));
                            e = new DotIdExp(e.loc, e, ident);
                            return returnExp.invoke(expressionSemantic(e, sc));
                        }
                    }
                    {
                        Dsymbol fd = search_function(sym, Id.opDispatch);
                        if (fd != null)
                        {
                            TemplateDeclaration td = fd.isTemplateDeclaration();
                            if (!(td != null))
                            {
                                fd.error(new BytePtr("must be a template `opDispatch(string s)`, not a %s"), fd.kind());
                                return returnExp.invoke(new ErrorExp());
                            }
                            StringExp se = new StringExp(e.loc, ident.toChars());
                            DArray<RootObject> tiargs = new DArray<RootObject>();
                            (tiargs).push(se);
                            DotTemplateInstanceExp dti = new DotTemplateInstanceExp(e.loc, e, Id.opDispatch, tiargs);
                            dti.ti.tempdecl = td;
                            int errors = gagError ? global.startGagging() : 0;
                            e = semanticY(dti, sc, 0);
                            if ((gagError && global.endGagging(errors)))
                                e = null;
                            return returnExp.invoke(e);
                        }
                    }
                    Expression alias_e = resolveAliasThis(sc, e, gagError);
                    if ((alias_e != null && !pequals(alias_e, e)))
                    {
                        DotIdExp die = new DotIdExp(e.loc, alias_e, ident);
                        int errors = gagError ? 0 : global.startGagging();
                        Expression exp = semanticY(die, sc, (gagError ? 1 : 0));
                        if (!(gagError))
                        {
                            global.endGagging(errors);
                            if ((exp != null && (exp.op & 0xFF) == 127))
                                exp = null;
                        }
                        if ((exp != null && gagError))
                            resolveAliasThis(sc, e, false);
                        return returnExp.invoke(exp);
                    }
                }
                return returnExp.invoke(visitType.invoke(mt));
            }
        };
        Function1<TypeStruct,Expression> visitStruct = new Function1<TypeStruct,Expression>(){
            public Expression invoke(TypeStruct mt){
                Dsymbol s = null;
                assert((e_ref.value.op & 0xFF) != 97);
                if (pequals(ident_ref.value, Id._mangleof))
                {
                    return getProperty(mt, e_ref.value.loc, ident_ref.value, flag_ref.value & 1);
                }
                if (pequals(ident_ref.value, Id._tupleof))
                {
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                    if (!(mt.sym.determineFields()))
                    {
                        error(e_ref.value.loc, new BytePtr("unable to determine fields of `%s` because of forward references"), mt.toChars());
                    }
                    Ref<Expression> e0 = ref(null);
                    Expression ev = (e_ref.value.op & 0xFF) == 20 ? null : e_ref.value;
                    if (ev != null)
                        ev = extractSideEffect(sc_ref.value, new BytePtr("__tup"), e0, ev, false);
                    DArray<Expression> exps = new DArray<Expression>();
                    (exps).reserve(mt.sym.fields.length);
                    {
                        int i = 0;
                        for (; i < mt.sym.fields.length;i++){
                            VarDeclaration v = mt.sym.fields.get(i);
                            Expression ex = null;
                            if (ev != null)
                                ex = new DotVarExp(e_ref.value.loc, ev, v, true);
                            else
                            {
                                ex = new VarExp(e_ref.value.loc, v, true);
                                ex.type = ex.type.addMod(e_ref.value.type.mod);
                            }
                            (exps).push(ex);
                        }
                    }
                    e_ref.value = new TupleExp(e_ref.value.loc, e0.value, exps);
                    Scope sc2 = (sc_ref.value).push();
                    (sc2).flags |= global.params.vsafe ? 1024 : 2;
                    e_ref.value = expressionSemantic(e_ref.value, sc2);
                    (sc2).pop();
                    return e_ref.value;
                }
                int flags = ((sc_ref.value).flags & 512) != 0 ? 128 : 0;
                s = mt.sym.search(e_ref.value.loc, ident_ref.value, flags | 1);
                while(true) try {
                /*L1:*/
                    if (!(s != null))
                    {
                        return noMember.invoke(mt, sc_ref.value, e_ref.value, ident_ref.value, flag_ref.value);
                    }
                    if ((!(((sc_ref.value).flags & 512) != 0) && !(symbolIsVisible(sc_ref.value, s))))
                    {
                        return noMember.invoke(mt, sc_ref.value, e_ref.value, ident_ref.value, flag_ref.value);
                    }
                    if (!(s.isFuncDeclaration() != null))
                    {
                        s.checkDeprecated(e_ref.value.loc, sc_ref.value);
                        {
                            Declaration d = s.isDeclaration();
                            if (d != null)
                                d.checkDisabled(e_ref.value.loc, sc_ref.value, false);
                        }
                    }
                    s = s.toAlias();
                    {
                        EnumMember em = s.isEnumMember();
                        if (em != null)
                        {
                            return em.getVarExp(e_ref.value.loc, sc_ref.value);
                        }
                    }
                    {
                        VarDeclaration v = s.isVarDeclaration();
                        if (v != null)
                        {
                            if ((!(v.type != null) || (v.type.deco == null && (v.inuse) != 0)))
                            {
                                if ((v.inuse) != 0)
                                    e_ref.value.error(new BytePtr("circular reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                                else
                                    e_ref.value.error(new BytePtr("forward reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                                return new ErrorExp();
                            }
                            if ((v.type.ty & 0xFF) == ENUMTY.Terror)
                            {
                                return new ErrorExp();
                            }
                            if (((v.storage_class & 8388608L) != 0 && v._init != null))
                            {
                                if ((v.inuse) != 0)
                                {
                                    e_ref.value.error(new BytePtr("circular initialization of %s `%s`"), v.kind(), v.toPrettyChars(false));
                                    return new ErrorExp();
                                }
                                checkAccess(e_ref.value.loc, sc_ref.value, null, (Declaration)v);
                                Expression ve = new VarExp(e_ref.value.loc, v, true);
                                if (!(isTrivialExp(e_ref.value)))
                                {
                                    ve = new CommaExp(e_ref.value.loc, e_ref.value, ve, true);
                                }
                                return expressionSemantic(ve, sc_ref.value);
                            }
                        }
                    }
                    {
                        Type t = s.getType();
                        if (t != null)
                        {
                            return expressionSemantic(new TypeExp(e_ref.value.loc, t), sc_ref.value);
                        }
                    }
                    TemplateMixin tm = s.isTemplateMixin();
                    if (tm != null)
                    {
                        Expression de = new DotExp(e_ref.value.loc, e_ref.value, new ScopeExp(e_ref.value.loc, tm));
                        de.type = e_ref.value.type;
                        return de;
                    }
                    TemplateDeclaration td = s.isTemplateDeclaration();
                    if (td != null)
                    {
                        if ((e_ref.value.op & 0xFF) == 20)
                            e_ref.value = new TemplateExp(e_ref.value.loc, td, null);
                        else
                            e_ref.value = new DotTemplateExp(e_ref.value.loc, e_ref.value, td);
                        return expressionSemantic(e_ref.value, sc_ref.value);
                    }
                    TemplateInstance ti = s.isTemplateInstance();
                    if (ti != null)
                    {
                        if (!((ti.semanticRun) != 0))
                        {
                            dsymbolSemantic(ti, sc_ref.value);
                            if ((!(ti.inst != null) || ti.errors))
                            {
                                return new ErrorExp();
                            }
                        }
                        s = ti.inst.toAlias();
                        if (!(s.isTemplateInstance() != null))
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        if ((e_ref.value.op & 0xFF) == 20)
                            e_ref.value = new ScopeExp(e_ref.value.loc, ti);
                        else
                            e_ref.value = new DotExp(e_ref.value.loc, e_ref.value, new ScopeExp(e_ref.value.loc, ti));
                        return expressionSemantic(e_ref.value, sc_ref.value);
                    }
                    if (((s.isImport() != null || s.isModule() != null) || s.isPackage() != null))
                    {
                        return symbolToExp(s, e_ref.value.loc, sc_ref.value, false);
                    }
                    OverloadSet o = s.isOverloadSet();
                    if (o != null)
                    {
                        OverExp oe = new OverExp(e_ref.value.loc, o);
                        if ((e_ref.value.op & 0xFF) == 20)
                        {
                            return oe;
                        }
                        return new DotExp(e_ref.value.loc, e_ref.value, oe);
                    }
                    Declaration d = s.isDeclaration();
                    if (!(d != null))
                    {
                        e_ref.value.error(new BytePtr("`%s.%s` is not a declaration"), e_ref.value.toChars(), ident_ref.value.toChars());
                        return new ErrorExp();
                    }
                    if ((e_ref.value.op & 0xFF) == 20)
                    {
                        {
                            TupleDeclaration tup = d.isTupleDeclaration();
                            if (tup != null)
                            {
                                e_ref.value = new TupleExp(e_ref.value.loc, tup);
                                return expressionSemantic(e_ref.value, sc_ref.value);
                            }
                        }
                        if ((d.needThis() && (sc_ref.value).intypeof != 1))
                        {
                            if (hasThis(sc_ref.value) != null)
                            {
                                e_ref.value = new DotVarExp(e_ref.value.loc, new ThisExp(e_ref.value.loc), d, true);
                                return expressionSemantic(e_ref.value, sc_ref.value);
                            }
                        }
                        if (d.semanticRun == PASS.init)
                            dsymbolSemantic(d, null);
                        checkAccess(e_ref.value.loc, sc_ref.value, e_ref.value, d);
                        VarExp ve = new VarExp(e_ref.value.loc, d, true);
                        if ((d.isVarDeclaration() != null && d.needThis()))
                            ve.type = d.type.addMod(e_ref.value.type.mod);
                        return ve;
                    }
                    boolean unreal = ((e_ref.value.op & 0xFF) == 26 && ((VarExp)e_ref.value).var.isField());
                    if ((d.isDataseg() || (unreal && d.isField())))
                    {
                        checkAccess(e_ref.value.loc, sc_ref.value, e_ref.value, d);
                        Expression ve = new VarExp(e_ref.value.loc, d, true);
                        e_ref.value = unreal ? ve : new CommaExp(e_ref.value.loc, e_ref.value, ve, true);
                        return expressionSemantic(e_ref.value, sc_ref.value);
                    }
                    e_ref.value = new DotVarExp(e_ref.value.loc, e_ref.value, d, true);
                    return expressionSemantic(e_ref.value, sc_ref.value);
                    break;
                } catch(Dispatch0 __d){}
            }
        };
        Function1<TypeEnum,Expression> visitEnum = new Function1<TypeEnum,Expression>(){
            public Expression invoke(TypeEnum mt){
                if (pequals(ident_ref.value, Id._mangleof))
                {
                    return getProperty(mt, e_ref.value.loc, ident_ref.value, flag_ref.value & 1);
                }
                if (mt.sym.semanticRun < PASS.semanticdone)
                    dsymbolSemantic(mt.sym, null);
                if (mt.sym.members == null)
                {
                    if (mt.sym.isSpecial())
                    {
                        e_ref.value = dotExp(mt.sym.memtype, sc_ref.value, e_ref.value, ident_ref.value, flag_ref.value);
                    }
                    else if (!((flag_ref.value & 1) != 0))
                    {
                        mt.sym.error(new BytePtr("is forward referenced when looking for `%s`"), ident_ref.value.toChars());
                        e_ref.value = new ErrorExp();
                    }
                    else
                        e_ref.value = null;
                    return e_ref.value;
                }
                Dsymbol s = mt.sym.search(e_ref.value.loc, ident_ref.value, 8);
                if (!(s != null))
                {
                    if (((pequals(ident_ref.value, Id.max) || pequals(ident_ref.value, Id.min)) || pequals(ident_ref.value, Id._init)))
                    {
                        return getProperty(mt, e_ref.value.loc, ident_ref.value, flag_ref.value & 1);
                    }
                    Expression res = dotExp(mt.sym.getMemtype(Loc.initial), sc_ref.value, e_ref.value, ident_ref.value, 1);
                    if ((!((flag_ref.value & 1) != 0) && !(res != null)))
                    {
                        {
                            Dsymbol ns = mt.sym.search_correct(ident_ref.value);
                            if (ns != null)
                                e_ref.value.error(new BytePtr("no property `%s` for type `%s`. Did you mean `%s.%s` ?"), ident_ref.value.toChars(), mt.toChars(), mt.toChars(), ns.toChars());
                            else
                                e_ref.value.error(new BytePtr("no property `%s` for type `%s`"), ident_ref.value.toChars(), mt.toChars());
                        }
                        return new ErrorExp();
                    }
                    return res;
                }
                EnumMember m = s.isEnumMember();
                return m.getVarExp(e_ref.value.loc, sc_ref.value);
            }
        };
        Function1<TypeClass,Expression> visitClass = new Function1<TypeClass,Expression>(){
            public Expression invoke(TypeClass mt){
                Dsymbol s = null;
                assert((e_ref.value.op & 0xFF) != 97);
                if (((pequals(ident_ref.value, Id.__sizeof) || pequals(ident_ref.value, Id.__xalignof)) || pequals(ident_ref.value, Id._mangleof)))
                {
                    return getProperty(mt.Type, e_ref.value.loc, ident_ref.value, 0);
                }
                if (pequals(ident_ref.value, Id._tupleof))
                {
                    objc().checkTupleof(e_ref.value, mt);
                    e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                    mt.sym.size(e_ref.value.loc);
                    Ref<Expression> e0 = ref(null);
                    Expression ev = (e_ref.value.op & 0xFF) == 20 ? null : e_ref.value;
                    if (ev != null)
                        ev = extractSideEffect(sc_ref.value, new BytePtr("__tup"), e0, ev, false);
                    DArray<Expression> exps = new DArray<Expression>();
                    (exps).reserve(mt.sym.fields.length);
                    {
                        int i = 0;
                        for (; i < mt.sym.fields.length;i++){
                            VarDeclaration v = mt.sym.fields.get(i);
                            if (v.isThisDeclaration() != null)
                                continue;
                            Expression ex = null;
                            if (ev != null)
                                ex = new DotVarExp(e_ref.value.loc, ev, v, true);
                            else
                            {
                                ex = new VarExp(e_ref.value.loc, v, true);
                                ex.type = ex.type.addMod(e_ref.value.type.mod);
                            }
                            (exps).push(ex);
                        }
                    }
                    e_ref.value = new TupleExp(e_ref.value.loc, e0.value, exps);
                    Scope sc2 = (sc_ref.value).push();
                    (sc2).flags |= global.params.vsafe ? 1024 : 2;
                    e_ref.value = expressionSemantic(e_ref.value, sc2);
                    (sc2).pop();
                    return e_ref.value;
                }
                int flags = ((sc_ref.value).flags & 512) != 0 ? 128 : 0;
                s = mt.sym.search(e_ref.value.loc, ident_ref.value, flags | 1);
                while(true) try {
                /*L1:*/
                    if (!(s != null))
                    {
                        if (pequals(mt.sym.ident, ident_ref.value))
                        {
                            if ((e_ref.value.op & 0xFF) == 20)
                            {
                                return getProperty(mt.Type, e_ref.value.loc, ident_ref.value, 0);
                            }
                            e_ref.value = new DotTypeExp(e_ref.value.loc, e_ref.value, mt.sym);
                            e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                            return e_ref.value;
                        }
                        {
                            ClassDeclaration cbase = mt.sym.searchBase(ident_ref.value);
                            if (cbase != null)
                            {
                                if ((e_ref.value.op & 0xFF) == 20)
                                {
                                    return getProperty(mt.Type, e_ref.value.loc, ident_ref.value, 0);
                                }
                                {
                                    InterfaceDeclaration ifbase = cbase.isInterfaceDeclaration();
                                    if (ifbase != null)
                                        e_ref.value = new CastExp(e_ref.value.loc, e_ref.value, ifbase.type);
                                    else
                                        e_ref.value = new DotTypeExp(e_ref.value.loc, e_ref.value, cbase);
                                }
                                e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                                return e_ref.value;
                            }
                        }
                        if (pequals(ident_ref.value, Id.classinfo))
                        {
                            if (!(Type.typeinfoclass != null))
                            {
                                error(e_ref.value.loc, new BytePtr("`object.TypeInfo_Class` could not be found, but is implicitly used"));
                                return new ErrorExp();
                            }
                            Type t = Type.typeinfoclass.type;
                            if (((e_ref.value.op & 0xFF) == 20 || (e_ref.value.op & 0xFF) == 30))
                            {
                                if (!(mt.sym.vclassinfo != null))
                                    mt.sym.vclassinfo = new TypeInfoClassDeclaration(mt.sym.type);
                                e_ref.value = new VarExp(e_ref.value.loc, mt.sym.vclassinfo, true);
                                e_ref.value = e_ref.value.addressOf();
                                e_ref.value.type = t;
                            }
                            else
                            {
                                e_ref.value = new PtrExp(e_ref.value.loc, e_ref.value);
                                e_ref.value.type = t.pointerTo();
                                if (mt.sym.isInterfaceDeclaration() != null)
                                {
                                    if (mt.sym.isCPPinterface())
                                    {
                                        error(e_ref.value.loc, new BytePtr("no `.classinfo` for C++ interface objects"));
                                    }
                                    e_ref.value.type = e_ref.value.type.pointerTo();
                                    e_ref.value = new PtrExp(e_ref.value.loc, e_ref.value);
                                    e_ref.value.type = t.pointerTo();
                                }
                                e_ref.value = new PtrExp(e_ref.value.loc, e_ref.value, t);
                            }
                            return e_ref.value;
                        }
                        if (pequals(ident_ref.value, Id.__vptr))
                        {
                            e_ref.value = e_ref.value.castTo(sc_ref.value, Type.tvoidptr.immutableOf().pointerTo().pointerTo());
                            e_ref.value = new PtrExp(e_ref.value.loc, e_ref.value);
                            e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                            return e_ref.value;
                        }
                        if ((pequals(ident_ref.value, Id.__monitor) && mt.sym.hasMonitor()))
                        {
                            e_ref.value = e_ref.value.castTo(sc_ref.value, Type.tvoidptr.pointerTo());
                            e_ref.value = new AddExp(e_ref.value.loc, e_ref.value, literal());
                            e_ref.value = new PtrExp(e_ref.value.loc, e_ref.value);
                            e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                            return e_ref.value;
                        }
                        if ((pequals(ident_ref.value, Id.outer) && mt.sym.vthis != null))
                        {
                            if (mt.sym.vthis.semanticRun == PASS.init)
                                dsymbolSemantic(mt.sym.vthis, null);
                            {
                                ClassDeclaration cdp = mt.sym.toParentLocal().isClassDeclaration();
                                if (cdp != null)
                                {
                                    DotVarExp dve = new DotVarExp(e_ref.value.loc, e_ref.value, mt.sym.vthis, true);
                                    dve.type = cdp.type.addMod(e_ref.value.type.mod);
                                    return dve;
                                }
                            }
                            {
                                Dsymbol p = mt.sym.toParentLocal();
                                for (; p != null;p = p.toParentLocal()){
                                    FuncDeclaration fd = p.isFuncDeclaration();
                                    if (!(fd != null))
                                        break;
                                    AggregateDeclaration ad = fd.isThis();
                                    if ((!(ad != null) && fd.isNested()))
                                        continue;
                                    if (!(ad != null))
                                        break;
                                    {
                                        ClassDeclaration cdp = ad.isClassDeclaration();
                                        if (cdp != null)
                                        {
                                            ThisExp ve = new ThisExp(e_ref.value.loc);
                                            ve.var = fd.vthis;
                                            boolean nestedError = fd.vthis.checkNestedReference(sc_ref.value, e_ref.value.loc);
                                            assert(!(nestedError));
                                            ve.type = cdp.type.addMod(fd.vthis.type.mod).addMod(e_ref.value.type.mod);
                                            return ve;
                                        }
                                    }
                                    break;
                                }
                            }
                            DotVarExp dve = new DotVarExp(e_ref.value.loc, e_ref.value, mt.sym.vthis, true);
                            dve.type = mt.sym.vthis.type.addMod(e_ref.value.type.mod);
                            return dve;
                        }
                        return noMember.invoke(mt, sc_ref.value, e_ref.value, ident_ref.value, flag_ref.value & 1);
                    }
                    if ((!(((sc_ref.value).flags & 512) != 0) && !(symbolIsVisible(sc_ref.value, s))))
                    {
                        return noMember.invoke(mt, sc_ref.value, e_ref.value, ident_ref.value, flag_ref.value);
                    }
                    if (!(s.isFuncDeclaration() != null))
                    {
                        s.checkDeprecated(e_ref.value.loc, sc_ref.value);
                        {
                            Declaration d = s.isDeclaration();
                            if (d != null)
                                d.checkDisabled(e_ref.value.loc, sc_ref.value, false);
                        }
                    }
                    s = s.toAlias();
                    {
                        EnumMember em = s.isEnumMember();
                        if (em != null)
                        {
                            return em.getVarExp(e_ref.value.loc, sc_ref.value);
                        }
                    }
                    {
                        VarDeclaration v = s.isVarDeclaration();
                        if (v != null)
                        {
                            if ((!(v.type != null) || (v.type.deco == null && (v.inuse) != 0)))
                            {
                                if ((v.inuse) != 0)
                                    e_ref.value.error(new BytePtr("circular reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                                else
                                    e_ref.value.error(new BytePtr("forward reference to %s `%s`"), v.kind(), v.toPrettyChars(false));
                                return new ErrorExp();
                            }
                            if ((v.type.ty & 0xFF) == ENUMTY.Terror)
                            {
                                return new ErrorExp();
                            }
                            if (((v.storage_class & 8388608L) != 0 && v._init != null))
                            {
                                if ((v.inuse) != 0)
                                {
                                    e_ref.value.error(new BytePtr("circular initialization of %s `%s`"), v.kind(), v.toPrettyChars(false));
                                    return new ErrorExp();
                                }
                                checkAccess(e_ref.value.loc, sc_ref.value, null, (Declaration)v);
                                Expression ve = new VarExp(e_ref.value.loc, v, true);
                                ve = expressionSemantic(ve, sc_ref.value);
                                return ve;
                            }
                        }
                    }
                    {
                        Type t = s.getType();
                        if (t != null)
                        {
                            return expressionSemantic(new TypeExp(e_ref.value.loc, t), sc_ref.value);
                        }
                    }
                    TemplateMixin tm = s.isTemplateMixin();
                    if (tm != null)
                    {
                        Expression de = new DotExp(e_ref.value.loc, e_ref.value, new ScopeExp(e_ref.value.loc, tm));
                        de.type = e_ref.value.type;
                        return de;
                    }
                    TemplateDeclaration td = s.isTemplateDeclaration();
                    if (td != null)
                    {
                        if ((e_ref.value.op & 0xFF) == 20)
                            e_ref.value = new TemplateExp(e_ref.value.loc, td, null);
                        else
                            e_ref.value = new DotTemplateExp(e_ref.value.loc, e_ref.value, td);
                        e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                        return e_ref.value;
                    }
                    TemplateInstance ti = s.isTemplateInstance();
                    if (ti != null)
                    {
                        if (!((ti.semanticRun) != 0))
                        {
                            dsymbolSemantic(ti, sc_ref.value);
                            if ((!(ti.inst != null) || ti.errors))
                            {
                                return new ErrorExp();
                            }
                        }
                        s = ti.inst.toAlias();
                        if (!(s.isTemplateInstance() != null))
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        if ((e_ref.value.op & 0xFF) == 20)
                            e_ref.value = new ScopeExp(e_ref.value.loc, ti);
                        else
                            e_ref.value = new DotExp(e_ref.value.loc, e_ref.value, new ScopeExp(e_ref.value.loc, ti));
                        return expressionSemantic(e_ref.value, sc_ref.value);
                    }
                    if (((s.isImport() != null || s.isModule() != null) || s.isPackage() != null))
                    {
                        e_ref.value = symbolToExp(s, e_ref.value.loc, sc_ref.value, false);
                        return e_ref.value;
                    }
                    OverloadSet o = s.isOverloadSet();
                    if (o != null)
                    {
                        OverExp oe = new OverExp(e_ref.value.loc, o);
                        if ((e_ref.value.op & 0xFF) == 20)
                        {
                            return oe;
                        }
                        return new DotExp(e_ref.value.loc, e_ref.value, oe);
                    }
                    Declaration d = s.isDeclaration();
                    if (!(d != null))
                    {
                        e_ref.value.error(new BytePtr("`%s.%s` is not a declaration"), e_ref.value.toChars(), ident_ref.value.toChars());
                        return new ErrorExp();
                    }
                    if ((e_ref.value.op & 0xFF) == 20)
                    {
                        {
                            TupleDeclaration tup = d.isTupleDeclaration();
                            if (tup != null)
                            {
                                e_ref.value = new TupleExp(e_ref.value.loc, tup);
                                e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                                return e_ref.value;
                            }
                        }
                        if ((((mt.sym.classKind == ClassKind.objc && d.isFuncDeclaration() != null) && d.isFuncDeclaration().isStatic()) && d.isFuncDeclaration().selector != null))
                        {
                            ObjcClassReferenceExp classRef = new ObjcClassReferenceExp(e_ref.value.loc, mt.sym);
                            return expressionSemantic(new DotVarExp(e_ref.value.loc, classRef, d, true), sc_ref.value);
                        }
                        else if ((d.needThis() && (sc_ref.value).intypeof != 1))
                        {
                            AggregateDeclaration ad = d.isMemberLocal();
                            {
                                FuncDeclaration f = hasThis(sc_ref.value);
                                if (f != null)
                                {
                                    Expression e1 = null;
                                    Type t = null;
                                    try {
                                        if (f.isThis2)
                                        {
                                            if (followInstantiationContext(f, ad))
                                            {
                                                e1 = new VarExp(e_ref.value.loc, f.vthis, true);
                                                e1 = new PtrExp(e1.loc, e1);
                                                e1 = new IndexExp(e1.loc, e1, literal());
                                                Declaration pd = f.toParent2().isDeclaration();
                                                assert(pd != null);
                                                t = pd.type.toBasetype();
                                                e1 = getThisSkipNestedFuncs(e1.loc, sc_ref.value, f.toParent2(), ad, e1, t, d, true);
                                                if (!(e1 != null))
                                                {
                                                    e_ref.value = new VarExp(e_ref.value.loc, d, true);
                                                    return e_ref.value;
                                                }
                                                /*goto L2*/throw Dispatch0.INSTANCE;
                                            }
                                        }
                                        e1 = new ThisExp(e_ref.value.loc);
                                        e1 = expressionSemantic(e1, sc_ref.value);
                                    }
                                    catch(Dispatch0 __d){}
                                /*L2:*/
                                    t = e1.type.toBasetype();
                                    ClassDeclaration cd = e_ref.value.type.isClassHandle();
                                    ClassDeclaration tcd = t.isClassHandle();
                                    if (((cd != null && tcd != null) && (pequals(tcd, cd) || cd.isBaseOf(tcd, null))))
                                    {
                                        e_ref.value = new DotTypeExp(e1.loc, e1, cd);
                                        e_ref.value = new DotVarExp(e_ref.value.loc, e_ref.value, d, true);
                                        e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                                        return e_ref.value;
                                    }
                                    if ((tcd != null && tcd.isNested()))
                                    {
                                        VarDeclaration vthis = followInstantiationContext(tcd, ad) ? tcd.vthis2 : tcd.vthis;
                                        e1 = new DotVarExp(e_ref.value.loc, e1, vthis, true);
                                        e1.type = vthis.type;
                                        e1.type = e1.type.addMod(t.mod);
                                        e1 = getThisSkipNestedFuncs(e1.loc, sc_ref.value, toParentP(tcd, ad), ad, e1, t, d, true);
                                        if (!(e1 != null))
                                        {
                                            e_ref.value = new VarExp(e_ref.value.loc, d, true);
                                            return e_ref.value;
                                        }
                                        /*goto L2*/throw Dispatch0.INSTANCE;
                                    }
                                }
                            }
                        }
                        if (d.semanticRun == PASS.init)
                            dsymbolSemantic(d, null);
                        {
                            FuncDeclaration fd = d.isFuncDeclaration();
                            if (fd != null)
                            {
                                d = (Declaration)mostVisibleOverload(fd, (sc_ref.value)._module);
                            }
                        }
                        checkAccess(e_ref.value.loc, sc_ref.value, e_ref.value, d);
                        VarExp ve = new VarExp(e_ref.value.loc, d, true);
                        if ((d.isVarDeclaration() != null && d.needThis()))
                            ve.type = d.type.addMod(e_ref.value.type.mod);
                        return ve;
                    }
                    boolean unreal = ((e_ref.value.op & 0xFF) == 26 && ((VarExp)e_ref.value).var.isField());
                    if ((d.isDataseg() || (unreal && d.isField())))
                    {
                        checkAccess(e_ref.value.loc, sc_ref.value, e_ref.value, d);
                        Expression ve = new VarExp(e_ref.value.loc, d, true);
                        e_ref.value = unreal ? ve : new CommaExp(e_ref.value.loc, e_ref.value, ve, true);
                        e_ref.value = expressionSemantic(e_ref.value, sc_ref.value);
                        return e_ref.value;
                    }
                    e_ref.value = new DotVarExp(e_ref.value.loc, e_ref.value, d, true);
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
        Function1<TypeBasic,Expression> visitBasic = new Function1<TypeBasic,Expression>(){
            public Expression invoke(TypeBasic mt){
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
                        return new RealExp(loc, target.RealProperties.nan, mt);
                    case 27:
                    case 28:
                    case 29:
                        complex_t cvalue = cvalue = new complex_t(target.RealProperties.nan, target.RealProperties.nan);
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
            public Expression invoke(TypeVector mt){
                assert((mt.basetype.ty & 0xFF) == ENUMTY.Tsarray);
                Expression e = defaultInit(mt.basetype, loc);
                VectorExp ve = new VectorExp(loc, e, mt);
                ve.type = mt;
                ve.dim = (int)(mt.basetype.size(loc) / mt.elementType().size(loc));
                return ve;
            }
        };
        Function1<TypeSArray,Expression> visitSArray = new Function1<TypeSArray,Expression>(){
            public Expression invoke(TypeSArray mt){
                if ((mt.next.ty & 0xFF) == ENUMTY.Tvoid)
                    return defaultInit(Type.tuns8, loc);
                else
                    return defaultInit(mt.next, loc);
            }
        };
        Function1<TypeFunction,Expression> visitFunction = new Function1<TypeFunction,Expression>(){
            public Expression invoke(TypeFunction mt){
                error(loc, new BytePtr("`function` does not have a default initializer"));
                return new ErrorExp();
            }
        };
        Function1<TypeStruct,Expression> visitStruct = new Function1<TypeStruct,Expression>(){
            public Expression invoke(TypeStruct mt){
                Declaration d = new SymbolDeclaration(mt.sym.loc, mt.sym);
                assert(d != null);
                d.type = mt;
                d.storage_class |= 2199023255552L;
                return new VarExp(mt.sym.loc, d, true);
            }
        };
        Function1<TypeEnum,Expression> visitEnum = new Function1<TypeEnum,Expression>(){
            public Expression invoke(TypeEnum mt){
                Expression e = mt.sym.getDefaultValue(loc);
                e = e.copy();
                e.loc = loc.copy();
                e.type = mt;
                return e;
            }
        };
        Function1<TypeTuple,Expression> visitTuple = new Function1<TypeTuple,Expression>(){
            public Expression invoke(TypeTuple mt){
                DArray<Expression> exps = new DArray<Expression>((mt.arguments).length);
                {
                    int i = 0;
                    for (; i < (mt.arguments).length;i++){
                        Parameter p = (mt.arguments).get(i);
                        assert(p.type != null);
                        Expression e = p.type.defaultInitLiteral(loc);
                        if ((e.op & 0xFF) == 127)
                        {
                            return e;
                        }
                        exps.set(i, e);
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
