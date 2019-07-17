package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.canthrow.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.denum.*;
import static org.dlang.dmd.dimport.*;
import static org.dlang.dmd.dmodule.*;
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
import static org.dlang.dmd.lambdacomp.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.nogc.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.visitor.*;

public class traits {
    private static final ByteSlice[] initializer_0 = {new ByteSlice("isAbstractClass"), new ByteSlice("isArithmetic"), new ByteSlice("isAssociativeArray"), new ByteSlice("isDisabled"), new ByteSlice("isDeprecated"), new ByteSlice("isFuture"), new ByteSlice("isFinalClass"), new ByteSlice("isPOD"), new ByteSlice("isNested"), new ByteSlice("isFloating"), new ByteSlice("isIntegral"), new ByteSlice("isScalar"), new ByteSlice("isStaticArray"), new ByteSlice("isUnsigned"), new ByteSlice("isVirtualFunction"), new ByteSlice("isVirtualMethod"), new ByteSlice("isAbstractFunction"), new ByteSlice("isFinalFunction"), new ByteSlice("isOverrideFunction"), new ByteSlice("isStaticFunction"), new ByteSlice("isModule"), new ByteSlice("isPackage"), new ByteSlice("isRef"), new ByteSlice("isOut"), new ByteSlice("isLazy"), new ByteSlice("isReturnOnStack"), new ByteSlice("hasMember"), new ByteSlice("identifier"), new ByteSlice("getProtection"), new ByteSlice("parent"), new ByteSlice("getLinkage"), new ByteSlice("getMember"), new ByteSlice("getOverloads"), new ByteSlice("getVirtualFunctions"), new ByteSlice("getVirtualMethods"), new ByteSlice("classInstanceSize"), new ByteSlice("allMembers"), new ByteSlice("derivedMembers"), new ByteSlice("isSame"), new ByteSlice("compiles"), new ByteSlice("parameters"), new ByteSlice("getAliasThis"), new ByteSlice("getAttributes"), new ByteSlice("getFunctionAttributes"), new ByteSlice("getFunctionVariadicStyle"), new ByteSlice("getParameterStorageClasses"), new ByteSlice("getUnitTests"), new ByteSlice("getVirtualIndex"), new ByteSlice("getPointerBitmap"), new ByteSlice("isZeroInit"), new ByteSlice("getTargetInfo"), new ByteSlice("getLocation")};
    static Slice<ByteSlice> _sharedStaticCtor_L88_C1names = slice(initializer_0);
    private static class PointerBitmapVisitor extends Visitor
    {
        // Erasure: __ctor<Ptr, long>
        public  PointerBitmapVisitor(DArray<Long> _data, long _sz_size_t) {
            this.data = pcopy(_data);
            this.sz_size_t = _sz_size_t;
        }

        // Erasure: setpointer<long>
        public  void setpointer(long off) {
            long ptroff = off / this.sz_size_t;
            (this.data).get((int)(ptroff / (8L * this.sz_size_t))) |= (long)(1L << (int)(ptroff % (8L * this.sz_size_t)));
        }

        // Erasure: visit<Type>
        public  void visit(Type t) {
            Type tb = t.toBasetype();
            if ((!pequals(tb, t)))
            {
                tb.accept(this);
            }
        }

        // Erasure: visit<TypeError>
        public  void visit(TypeError t) {
            this.visit((Type)t);
        }

        // Erasure: visit<TypeNext>
        public  void visit(TypeNext t) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<TypeBasic>
        public  void visit(TypeBasic t) {
            if (((t.ty & 0xFF) == ENUMTY.Tvoid))
            {
                this.setpointer(this.offset);
            }
        }

        // Erasure: visit<TypeVector>
        public  void visit(TypeVector t) {
        }

        // Erasure: visit<TypeArray>
        public  void visit(TypeArray t) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<TypeSArray>
        public  void visit(TypeSArray t) {
            long arrayoff = this.offset;
            long nextsize = t.next.value.size();
            if ((nextsize == -1L))
            {
                this.error = true;
            }
            long dim = t.dim.toInteger();
            {
                long i = 0L;
                for (; (i < dim);i++){
                    this.offset = arrayoff + i * nextsize;
                    t.next.value.accept(this);
                }
            }
            this.offset = arrayoff;
        }

        // Erasure: visit<TypeDArray>
        public  void visit(TypeDArray t) {
            this.setpointer(this.offset + this.sz_size_t);
        }

        // Erasure: visit<TypeAArray>
        public  void visit(TypeAArray t) {
            this.setpointer(this.offset);
        }

        // Erasure: visit<TypePointer>
        public  void visit(TypePointer t) {
            if (((t.nextOf().ty & 0xFF) != ENUMTY.Tfunction))
            {
                this.setpointer(this.offset);
            }
        }

        // Erasure: visit<TypeReference>
        public  void visit(TypeReference t) {
            this.setpointer(this.offset);
        }

        // Erasure: visit<TypeClass>
        public  void visit(TypeClass t) {
            this.setpointer(this.offset);
        }

        // Erasure: visit<TypeFunction>
        public  void visit(TypeFunction t) {
        }

        // Erasure: visit<TypeDelegate>
        public  void visit(TypeDelegate t) {
            this.setpointer(this.offset);
        }

        // Erasure: visit<TypeQualified>
        public  void visit(TypeQualified t) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<TypeIdentifier>
        public  void visit(TypeIdentifier t) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<TypeInstance>
        public  void visit(TypeInstance t) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<TypeTypeof>
        public  void visit(TypeTypeof t) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<TypeReturn>
        public  void visit(TypeReturn t) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<TypeEnum>
        public  void visit(TypeEnum t) {
            this.visit((Type)t);
        }

        // Erasure: visit<TypeTuple>
        public  void visit(TypeTuple t) {
            this.visit((Type)t);
        }

        // Erasure: visit<TypeSlice>
        public  void visit(TypeSlice t) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<TypeNull>
        public  void visit(TypeNull t) {
        }

        // Erasure: visit<TypeStruct>
        public  void visit(TypeStruct t) {
            long structoff = this.offset;
            {
                Slice<VarDeclaration> __r1675 = t.sym.fields.opSlice().copy();
                int __key1676 = 0;
                for (; (__key1676 < __r1675.getLength());__key1676 += 1) {
                    VarDeclaration v = __r1675.get(__key1676);
                    this.offset = structoff + (long)v.offset;
                    if (((v.type.ty & 0xFF) == ENUMTY.Tclass))
                    {
                        this.setpointer(this.offset);
                    }
                    else
                    {
                        v.type.accept(this);
                    }
                }
            }
            this.offset = structoff;
        }

        // Erasure: visitClass<TypeClass>
        public  void visitClass(TypeClass t) {
            long classoff = this.offset;
            if (t.sym.baseClass != null)
            {
                this.visitClass(((TypeClass)t.sym.baseClass.type));
            }
            {
                Slice<VarDeclaration> __r1677 = t.sym.fields.opSlice().copy();
                int __key1678 = 0;
                for (; (__key1678 < __r1677.getLength());__key1678 += 1) {
                    VarDeclaration v = __r1677.get(__key1678);
                    this.offset = classoff + (long)v.offset;
                    v.type.accept(this);
                }
            }
            this.offset = classoff;
        }

        private DArray<Long> data = null;
        private long offset = 0L;
        private long sz_size_t = 0L;
        private boolean error = false;

        public PointerBitmapVisitor() {}
    }

    static boolean LOGSEMANTIC = false;
    // Erasure: getDsymbolWithoutExpCtx<RootObject>
    public static Dsymbol getDsymbolWithoutExpCtx(RootObject oarg) {
        {
            Expression e = isExpression(oarg);
            if ((e) != null)
            {
                if (((e.op & 0xFF) == 27))
                {
                    return (((DotVarExp)e)).var;
                }
                if (((e.op & 0xFF) == 37))
                {
                    return (((DotTemplateExp)e)).td;
                }
            }
        }
        return getDsymbol(oarg);
    }

    static StringTable traitsStringTable = new StringTable();
    static {
        traitsStringTable._init(52);
        {
            Slice<ByteSlice> __r1673 = traits._sharedStaticCtor_L88_C1names.copy();
            int __key1674 = 0;
            for (; (__key1674 < __r1673.getLength());__key1674 += 1) {
                ByteSlice s = __r1673.get(__key1674).copy();
                Ptr<StringValue> sv = traitsStringTable.insert(toByteSlice(s), s.getPtr(0));
                assert(sv != null);
            }
        }
    }
    // Erasure: getTypePointerBitmap<Loc, Type, Ptr>
    public static long getTypePointerBitmap(Loc loc, Type t, DArray<Long> data) {
        long sz = 0L;
        if (((t.ty & 0xFF) == ENUMTY.Tclass) && ((((TypeClass)t)).sym.isInterfaceDeclaration() == null))
        {
            sz = (((TypeClass)t)).sym.size(loc);
        }
        else
        {
            sz = t.size(loc);
        }
        if ((sz == -1L))
        {
            return -1L;
        }
        long sz_size_t = Type.tsize_t.size(loc);
        if ((sz > -1L - sz_size_t))
        {
            error(loc, new BytePtr("size overflow for type `%s`"), t.toChars());
            return -1L;
        }
        long bitsPerWord = sz_size_t * 8L;
        long cntptr = (sz + sz_size_t - 1L) / sz_size_t;
        long cntdata = (cntptr + bitsPerWord - 1L) / bitsPerWord;
        (data).setDim((int)cntdata);
        (data).zero();
        // skipping duplicate class PointerBitmapVisitor
        PointerBitmapVisitor pbv = new PointerBitmapVisitor(data, sz_size_t);
        if (((t.ty & 0xFF) == ENUMTY.Tclass))
        {
            pbv.visitClass(((TypeClass)t));
        }
        else
        {
            t.accept(pbv);
        }
        return pbv.error ? -1L : sz;
    }

    // Erasure: pointerBitmap<TraitsExp>
    public static Expression pointerBitmap(TraitsExp e) {
        if ((e.args == null) || ((e.args).length != 1))
        {
            error(e.loc, new BytePtr("a single type expected for trait pointerBitmap"));
            return new ErrorExp();
        }
        Type t = getType((e.args).get(0));
        if (t == null)
        {
            error(e.loc, new BytePtr("`%s` is not a type"), (e.args).get(0).toChars());
            return new ErrorExp();
        }
        Ref<DArray<Long>> data = ref(new DArray<Long>());
        try {
            long sz = getTypePointerBitmap(e.loc, t, data.value);
            if ((sz == -1L))
            {
                return new ErrorExp();
            }
            DArray<Expression> exps = new DArray<Expression>(data.value.length + 1);
            exps.set(0, new IntegerExp(e.loc, sz, Type.tsize_t));
            {
                int __key1679 = 1;
                int __limit1680 = (exps).length;
                for (; (__key1679 < __limit1680);__key1679 += 1) {
                    int i = __key1679;
                    exps.set(i, new IntegerExp(e.loc, data.value.get(i - 1), Type.tsize_t));
                }
            }
            ArrayLiteralExp ale = new ArrayLiteralExp(e.loc, Type.tsize_t.sarrayOf((long)(data.value.length + 1)), exps);
            return ale;
        }
        finally {
        }
    }

    // Erasure: semanticTraits<TraitsExp, Ptr>
    public static Expression semanticTraits(TraitsExp e, Ptr<Scope> sc) {
        if ((!pequals(e.ident, Id.compiles)) && (!pequals(e.ident, Id.isSame)) && (!pequals(e.ident, Id.identifier)) && (!pequals(e.ident, Id.getProtection)) && (!pequals(e.ident, Id.getAttributes)))
        {
            if (!TemplateInstance.semanticTiargs(e.loc, sc, e.args, 1))
            {
                return new ErrorExp();
            }
        }
        int dim = e.args != null ? (e.args).length : 0;
        Function1<Integer,Expression> dimError = new Function1<Integer,Expression>() {
            public Expression invoke(Integer expected) {
             {
                e.error(new BytePtr("expected %d arguments for `%s` but had %d"), expected, e.ident.toChars(), dim);
                return new ErrorExp();
            }}

        };
        Function0<IntegerExp> True = new Function0<IntegerExp>() {
            public IntegerExp invoke() {
             {
                return new IntegerExp(e.loc, 1L, Type.tbool);
            }}

        };
        Function0<IntegerExp> False = new Function0<IntegerExp>() {
            public IntegerExp invoke() {
             {
                return new IntegerExp(e.loc, 0L, Type.tbool);
            }}

        };
        Function2<RootObject,Ref<FuncDeclaration>,TypeFunction> toTypeFunction = new Function2<RootObject,Ref<FuncDeclaration>,TypeFunction>() {
            public TypeFunction invoke(RootObject o, Ref<FuncDeclaration> fdp) {
             {
                fdp.value = null;
                Ref<Type> t = ref(null);
                {
                    Dsymbol s = getDsymbolWithoutExpCtx(o);
                    if ((s) != null)
                    {
                        {
                            FuncDeclaration fd = s.isFuncDeclaration();
                            if ((fd) != null)
                            {
                                t.value = fd.type;
                                fdp.value = fd;
                            }
                            else {
                                VarDeclaration vd = s.isVarDeclaration();
                                if ((vd) != null)
                                {
                                    t.value = vd.type;
                                }
                                else
                                {
                                    t.value = isType(o);
                                }
                            }
                        }
                    }
                    else
                    {
                        t.value = isType(o);
                    }
                }
                if (t.value != null)
                {
                    if (((t.value.ty & 0xFF) == ENUMTY.Tfunction))
                    {
                        return ((TypeFunction)t.value);
                    }
                    else if (((t.value.ty & 0xFF) == ENUMTY.Tdelegate))
                    {
                        return ((TypeFunction)t.value.nextOf());
                    }
                    else if (((t.value.ty & 0xFF) == ENUMTY.Tpointer) && ((t.value.nextOf().ty & 0xFF) == ENUMTY.Tfunction))
                    {
                        return ((TypeFunction)t.value.nextOf());
                    }
                }
                return null;
            }}

        };
        // from template isX!(Declaration)
        Function1<Function1<Declaration,Boolean>,IntegerExp> isXDeclaration = new Function1<Function1<Declaration,Boolean>,IntegerExp>() {
            public IntegerExp invoke(Function1<Declaration,Boolean> fp) {
             {
                if (dim == 0)
                {
                    return False.invoke();
                }
                {
                    Slice<RootObject> __r1685 = (e.args).opSlice().copy();
                    Ref<Integer> __key1686 = ref(0);
                    for (; (__key1686.value < __r1685.getLength());__key1686.value += 1) {
                        RootObject o = __r1685.get(__key1686.value);
                        Dsymbol s = getDsymbolWithoutExpCtx(o);
                        if (s == null)
                        {
                            return False.invoke();
                        }
                        Declaration y = s.isDeclaration();
                        if ((y == null) || !fp.invoke(y))
                        {
                            return False.invoke();
                        }
                    }
                }
                return True.invoke();
            }}

        };

        // from template isX!(Dsymbol)
        Function1<Function1<Dsymbol,Boolean>,IntegerExp> isXDsymbol = new Function1<Function1<Dsymbol,Boolean>,IntegerExp>() {
            public IntegerExp invoke(Function1<Dsymbol,Boolean> fp) {
             {
                if (dim == 0)
                {
                    return False.invoke();
                }
                {
                    Slice<RootObject> __r1683 = (e.args).opSlice().copy();
                    Ref<Integer> __key1684 = ref(0);
                    for (; (__key1684.value < __r1683.getLength());__key1684.value += 1) {
                        RootObject o = __r1683.get(__key1684.value);
                        Dsymbol s = getDsymbolWithoutExpCtx(o);
                        if (s == null)
                        {
                            return False.invoke();
                        }
                        if ((s == null) || !fp.invoke(s))
                        {
                            return False.invoke();
                        }
                    }
                }
                return True.invoke();
            }}

        };

        // from template isX!(EnumMember)
        Function1<Function1<EnumMember,Boolean>,IntegerExp> isXEnumMember = new Function1<Function1<EnumMember,Boolean>,IntegerExp>() {
            public IntegerExp invoke(Function1<EnumMember,Boolean> fp) {
             {
                if (dim == 0)
                {
                    return False.invoke();
                }
                {
                    Slice<RootObject> __r1689 = (e.args).opSlice().copy();
                    Ref<Integer> __key1690 = ref(0);
                    for (; (__key1690.value < __r1689.getLength());__key1690.value += 1) {
                        RootObject o = __r1689.get(__key1690.value);
                        Dsymbol s = getDsymbolWithoutExpCtx(o);
                        if (s == null)
                        {
                            return False.invoke();
                        }
                        EnumMember y = s.isEnumMember();
                        if ((y == null) || !fp.invoke(y))
                        {
                            return False.invoke();
                        }
                    }
                }
                return True.invoke();
            }}

        };

        // from template isX!(FuncDeclaration)
        Function1<Function1<FuncDeclaration,Boolean>,IntegerExp> isXFuncDeclaration = new Function1<Function1<FuncDeclaration,Boolean>,IntegerExp>() {
            public IntegerExp invoke(Function1<FuncDeclaration,Boolean> fp) {
             {
                if (dim == 0)
                {
                    return False.invoke();
                }
                {
                    Slice<RootObject> __r1687 = (e.args).opSlice().copy();
                    Ref<Integer> __key1688 = ref(0);
                    for (; (__key1688.value < __r1687.getLength());__key1688.value += 1) {
                        RootObject o = __r1687.get(__key1688.value);
                        Dsymbol s = getDsymbolWithoutExpCtx(o);
                        if (s == null)
                        {
                            return False.invoke();
                        }
                        FuncDeclaration y = s.isFuncDeclaration();
                        if ((y == null) || !fp.invoke(y))
                        {
                            return False.invoke();
                        }
                    }
                }
                return True.invoke();
            }}

        };

        // from template isX!(Type)
        Function1<Function1<Type,Boolean>,IntegerExp> isXType = new Function1<Function1<Type,Boolean>,IntegerExp>() {
            public IntegerExp invoke(Function1<Type,Boolean> fp) {
             {
                if (dim == 0)
                {
                    return False.invoke();
                }
                {
                    Slice<RootObject> __r1681 = (e.args).opSlice().copy();
                    Ref<Integer> __key1682 = ref(0);
                    for (; (__key1682.value < __r1681.getLength());__key1682.value += 1) {
                        RootObject o = __r1681.get(__key1682.value);
                        Type y = getType(o);
                        if ((y == null) || !fp.invoke(y))
                        {
                            return False.invoke();
                        }
                    }
                }
                return True.invoke();
            }}

        };

        Function1<Dsymbol,Boolean> __lambda2 = new Function1<Dsymbol,Boolean>() {
            public Boolean invoke(Dsymbol sym) {
             {
                dmodule.Package p = resolveIsPackage(sym);
                return (p != null) && (fp).invoke(p);
            }}

        };
        Function1<Function1<dmodule.Package,Boolean>,Expression> isPkgX = new Function1<Function1<dmodule.Package,Boolean>,Expression>() {
            public Expression invoke(Function1<dmodule.Package,Boolean> fp) {
             {
                return isXDsymbol.invoke(__lambda2);
            }}

        };
        if ((pequals(e.ident, Id.isArithmetic)))
        {
            Function1<Type,Boolean> __lambda8 = new Function1<Type,Boolean>() {
                public Boolean invoke(Type t) {
                 {
                    return t.isintegral() || t.isfloating();
                }}

            };
            return isXType.invoke(__lambda8);
        }
        if ((pequals(e.ident, Id.isFloating)))
        {
            Function1<Type,Boolean> __lambda9 = new Function1<Type,Boolean>() {
                public Boolean invoke(Type t) {
                 {
                    return t.isfloating();
                }}

            };
            return isXType.invoke(__lambda9);
        }
        if ((pequals(e.ident, Id.isIntegral)))
        {
            Function1<Type,Boolean> __lambda10 = new Function1<Type,Boolean>() {
                public Boolean invoke(Type t) {
                 {
                    return t.isintegral();
                }}

            };
            return isXType.invoke(__lambda10);
        }
        if ((pequals(e.ident, Id.isScalar)))
        {
            Function1<Type,Boolean> __lambda11 = new Function1<Type,Boolean>() {
                public Boolean invoke(Type t) {
                 {
                    return t.isscalar();
                }}

            };
            return isXType.invoke(__lambda11);
        }
        if ((pequals(e.ident, Id.isUnsigned)))
        {
            Function1<Type,Boolean> __lambda12 = new Function1<Type,Boolean>() {
                public Boolean invoke(Type t) {
                 {
                    return t.isunsigned();
                }}

            };
            return isXType.invoke(__lambda12);
        }
        if ((pequals(e.ident, Id.isAssociativeArray)))
        {
            Function1<Type,Boolean> __lambda13 = new Function1<Type,Boolean>() {
                public Boolean invoke(Type t) {
                 {
                    return (t.toBasetype().ty & 0xFF) == ENUMTY.Taarray;
                }}

            };
            return isXType.invoke(__lambda13);
        }
        if ((pequals(e.ident, Id.isDeprecated)))
        {
            if (global.params.vcomplex)
            {
                if (isXType.invoke(__lambda14).isBool(true))
                {
                    return True.invoke();
                }
            }
            Function1<Dsymbol,Boolean> __lambda15 = new Function1<Dsymbol,Boolean>() {
                public Boolean invoke(Dsymbol t) {
                 {
                    return t.isDeprecated();
                }}

            };
            return isXDsymbol.invoke(__lambda15);
        }
        if ((pequals(e.ident, Id.isFuture)))
        {
            Function1<Declaration,Boolean> __lambda16 = new Function1<Declaration,Boolean>() {
                public Boolean invoke(Declaration t) {
                 {
                    return t.isFuture();
                }}

            };
            return isXDeclaration.invoke(__lambda16);
        }
        if ((pequals(e.ident, Id.isStaticArray)))
        {
            Function1<Type,Boolean> __lambda17 = new Function1<Type,Boolean>() {
                public Boolean invoke(Type t) {
                 {
                    return (t.toBasetype().ty & 0xFF) == ENUMTY.Tsarray;
                }}

            };
            return isXType.invoke(__lambda17);
        }
        if ((pequals(e.ident, Id.isAbstractClass)))
        {
            Function1<Type,Boolean> __lambda18 = new Function1<Type,Boolean>() {
                public Boolean invoke(Type t) {
                 {
                    return ((t.toBasetype().ty & 0xFF) == ENUMTY.Tclass) && (((TypeClass)t.toBasetype())).sym.isAbstract();
                }}

            };
            return isXType.invoke(__lambda18);
        }
        if ((pequals(e.ident, Id.isFinalClass)))
        {
            Function1<Type,Boolean> __lambda19 = new Function1<Type,Boolean>() {
                public Boolean invoke(Type t) {
                 {
                    return ((t.toBasetype().ty & 0xFF) == ENUMTY.Tclass) && (((((TypeClass)t.toBasetype())).sym.storage_class & 8L) != 0L);
                }}

            };
            return isXType.invoke(__lambda19);
        }
        if ((pequals(e.ident, Id.isTemplate)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            Function1<Dsymbol,Boolean> __lambda20 = new Function1<Dsymbol,Boolean>() {
                public Boolean invoke(Dsymbol s) {
                 {
                    if (!s.toAlias().isOverloadable())
                    {
                        return false;
                    }
                    Function1<Dsymbol,Integer> __lambda2 = new Function1<Dsymbol,Integer>() {
                        public Integer invoke(Dsymbol sm) {
                         {
                            return ((sm.isTemplateDeclaration() != null) ? 1 : 0);
                        }}

                    };
                    return overloadApply(s, __lambda2, null) != 0;
                }}

            };
            return isXDsymbol.invoke(__lambda20);
        }
        if ((pequals(e.ident, Id.isPOD)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            RootObject o = (e.args).get(0);
            Type t = isType(o);
            if (t == null)
            {
                e.error(new BytePtr("type expected as second argument of __traits `%s` instead of `%s`"), e.ident.toChars(), o.toChars());
                return new ErrorExp();
            }
            Type tb = t.baseElemOf();
            {
                StructDeclaration sd = ((tb.ty & 0xFF) == ENUMTY.Tstruct) ? (((TypeStruct)tb)).sym : null;
                if ((sd) != null)
                {
                    return sd.isPOD() ? True.invoke() : False.invoke();
                }
            }
            return True.invoke();
        }
        if ((pequals(e.ident, Id.isNested)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            RootObject o = (e.args).get(0);
            Dsymbol s = getDsymbolWithoutExpCtx(o);
            if (s == null)
            {
            }
            else {
                AggregateDeclaration ad = s.isAggregateDeclaration();
                if ((ad) != null)
                {
                    return ad.isNested() ? True.invoke() : False.invoke();
                }
                else {
                    FuncDeclaration fd = s.isFuncDeclaration();
                    if ((fd) != null)
                    {
                        return fd.isNested() ? True.invoke() : False.invoke();
                    }
                }
            }
            e.error(new BytePtr("aggregate or function expected instead of `%s`"), o.toChars());
            return new ErrorExp();
        }
        if ((pequals(e.ident, Id.isDisabled)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            Function1<Declaration,Boolean> __lambda21 = new Function1<Declaration,Boolean>() {
                public Boolean invoke(Declaration f) {
                 {
                    return f.isDisabled();
                }}

            };
            return isXDeclaration.invoke(__lambda21);
        }
        if ((pequals(e.ident, Id.isAbstractFunction)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            Function1<FuncDeclaration,Boolean> __lambda22 = new Function1<FuncDeclaration,Boolean>() {
                public Boolean invoke(FuncDeclaration f) {
                 {
                    return f.isAbstract();
                }}

            };
            return isXFuncDeclaration.invoke(__lambda22);
        }
        if ((pequals(e.ident, Id.isVirtualFunction)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            Function1<FuncDeclaration,Boolean> __lambda23 = new Function1<FuncDeclaration,Boolean>() {
                public Boolean invoke(FuncDeclaration f) {
                 {
                    return f.isVirtual();
                }}

            };
            return isXFuncDeclaration.invoke(__lambda23);
        }
        if ((pequals(e.ident, Id.isVirtualMethod)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            Function1<FuncDeclaration,Boolean> __lambda24 = new Function1<FuncDeclaration,Boolean>() {
                public Boolean invoke(FuncDeclaration f) {
                 {
                    return f.isVirtualMethod();
                }}

            };
            return isXFuncDeclaration.invoke(__lambda24);
        }
        if ((pequals(e.ident, Id.isFinalFunction)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            Function1<FuncDeclaration,Boolean> __lambda25 = new Function1<FuncDeclaration,Boolean>() {
                public Boolean invoke(FuncDeclaration f) {
                 {
                    return f.isFinalFunc();
                }}

            };
            return isXFuncDeclaration.invoke(__lambda25);
        }
        if ((pequals(e.ident, Id.isOverrideFunction)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            Function1<FuncDeclaration,Boolean> __lambda26 = new Function1<FuncDeclaration,Boolean>() {
                public Boolean invoke(FuncDeclaration f) {
                 {
                    return f.isOverride();
                }}

            };
            return isXFuncDeclaration.invoke(__lambda26);
        }
        if ((pequals(e.ident, Id.isStaticFunction)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            Function1<FuncDeclaration,Boolean> __lambda27 = new Function1<FuncDeclaration,Boolean>() {
                public Boolean invoke(FuncDeclaration f) {
                 {
                    return !f.needThis() && !f.isNested();
                }}

            };
            return isXFuncDeclaration.invoke(__lambda27);
        }
        if ((pequals(e.ident, Id.isModule)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            Function1<dmodule.Package,Boolean> __lambda28 = new Function1<dmodule.Package,Boolean>() {
                public Boolean invoke(dmodule.Package p) {
                 {
                    return (p.isModule() != null) || (p.isPackageMod() != null);
                }}

            };
            return isPkgX.invoke(__lambda28);
        }
        if ((pequals(e.ident, Id.isPackage)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            Function1<dmodule.Package,Boolean> __lambda29 = new Function1<dmodule.Package,Boolean>() {
                public Boolean invoke(dmodule.Package p) {
                 {
                    return p.isModule() == null;
                }}

            };
            return isPkgX.invoke(__lambda29);
        }
        if ((pequals(e.ident, Id.isRef)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            Function1<Declaration,Boolean> __lambda30 = new Function1<Declaration,Boolean>() {
                public Boolean invoke(Declaration d) {
                 {
                    return d.isRef();
                }}

            };
            return isXDeclaration.invoke(__lambda30);
        }
        if ((pequals(e.ident, Id.isOut)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            Function1<Declaration,Boolean> __lambda31 = new Function1<Declaration,Boolean>() {
                public Boolean invoke(Declaration d) {
                 {
                    return d.isOut();
                }}

            };
            return isXDeclaration.invoke(__lambda31);
        }
        if ((pequals(e.ident, Id.isLazy)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            Function1<Declaration,Boolean> __lambda32 = new Function1<Declaration,Boolean>() {
                public Boolean invoke(Declaration d) {
                 {
                    return (d.storage_class & 8192L) != 0L;
                }}

            };
            return isXDeclaration.invoke(__lambda32);
        }
        if ((pequals(e.ident, Id.identifier)))
        {
            if (!TemplateInstance.semanticTiargs(e.loc, sc, e.args, 2))
            {
                return new ErrorExp();
            }
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            RootObject o = (e.args).get(0);
            Identifier id = null;
            {
                Parameter po = isParameter(o);
                if ((po) != null)
                {
                    if (po.ident == null)
                    {
                        e.error(new BytePtr("argument `%s` has no identifier"), po.type.toChars());
                        return new ErrorExp();
                    }
                    id = po.ident;
                }
                else
                {
                    Dsymbol s = getDsymbolWithoutExpCtx(o);
                    if ((s == null) || (s.ident == null))
                    {
                        e.error(new BytePtr("argument `%s` has no identifier"), o.toChars());
                        return new ErrorExp();
                    }
                    id = s.ident;
                }
            }
            StringExp se = new StringExp(e.loc, id.toChars());
            return expressionSemantic(se, sc);
        }
        if ((pequals(e.ident, Id.getProtection)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            Ptr<Scope> sc2 = (sc.get()).push();
            (sc2.get()).flags = (sc.get()).flags | 2 | 512;
            boolean ok = TemplateInstance.semanticTiargs(e.loc, sc2, e.args, 1);
            (sc2.get()).pop();
            if (!ok)
            {
                return new ErrorExp();
            }
            RootObject o = (e.args).get(0);
            Dsymbol s = getDsymbolWithoutExpCtx(o);
            if (s == null)
            {
                if (!isError(o))
                {
                    e.error(new BytePtr("argument `%s` has no protection"), o.toChars());
                }
                return new ErrorExp();
            }
            if ((s.semanticRun == PASS.init))
            {
                dsymbolSemantic(s, null);
            }
            BytePtr protName = pcopy(protectionToChars(s.prot().kind));
            assert(protName != null);
            StringExp se = new StringExp(e.loc, protName);
            return expressionSemantic(se, sc);
        }
        if ((pequals(e.ident, Id.parent)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            RootObject o = (e.args).get(0);
            Dsymbol s = getDsymbolWithoutExpCtx(o);
            if (s != null)
            {
                {
                    AggregateDeclaration ad = s.isAggregateDeclaration();
                    if ((ad) != null)
                    {
                        if (ad.isNested())
                        {
                            {
                                Dsymbol p = s.toParent();
                                if ((p) != null)
                                {
                                    if (p.isTemplateInstance() != null)
                                    {
                                        s = p;
                                        Dsymbol td = (((TemplateInstance)p)).tempdecl;
                                        if (td != null)
                                        {
                                            s = td;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                {
                    FuncDeclaration fd = s.isFuncDeclaration();
                    if ((fd) != null)
                    {
                        s = fd.toAliasFunc();
                    }
                }
                if (s.isImport() == null)
                {
                    s = s.toParent();
                }
            }
            if ((s == null) || (s.isImport() != null))
            {
                e.error(new BytePtr("argument `%s` has no parent"), o.toChars());
                return new ErrorExp();
            }
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
                            Expression ex = new TemplateExp(e.loc, td, f);
                            ex = expressionSemantic(ex, sc);
                            return ex;
                        }
                    }
                    {
                        FuncLiteralDeclaration fld = f.isFuncLiteralDeclaration();
                        if ((fld) != null)
                        {
                            Expression ex = new VarExp(e.loc, fld, true);
                            return expressionSemantic(ex, sc);
                        }
                    }
                }
            }
            return symbolToExp(s, e.loc, sc, false);
        }
        if ((pequals(e.ident, Id.hasMember)) || (pequals(e.ident, Id.getMember)) || (pequals(e.ident, Id.getOverloads)) || (pequals(e.ident, Id.getVirtualMethods)) || (pequals(e.ident, Id.getVirtualFunctions)))
        {
            if ((dim != 2) && !((dim == 3) && (pequals(e.ident, Id.getOverloads))))
            {
                return dimError.invoke(2);
            }
            RootObject o = (e.args).get(0);
            Expression ex = isExpression((e.args).get(1));
            if (ex == null)
            {
                e.error(new BytePtr("expression expected as second argument of __traits `%s`"), e.ident.toChars());
                return new ErrorExp();
            }
            ex = ex.ctfeInterpret();
            boolean includeTemplates = false;
            if ((dim == 3) && (pequals(e.ident, Id.getOverloads)))
            {
                Expression b = isExpression((e.args).get(2));
                b = b.ctfeInterpret();
                if (!b.type.value.equals(Type.tbool))
                {
                    e.error(new BytePtr("`bool` expected as third argument of `__traits(getOverloads)`, not `%s` of type `%s`"), b.toChars(), b.type.value.toChars());
                    return new ErrorExp();
                }
                includeTemplates = b.isBool(true);
            }
            StringExp se = ex.toStringExp();
            if ((se == null) || (se.len == 0))
            {
                e.error(new BytePtr("string expected as second argument of __traits `%s` instead of `%s`"), e.ident.toChars(), ex.toChars());
                return new ErrorExp();
            }
            se = se.toUTF8(sc);
            if (((se.sz & 0xFF) != 1))
            {
                e.error(new BytePtr("string must be chars"));
                return new ErrorExp();
            }
            Identifier id = Identifier.idPool(se.peekSlice());
            Dsymbol sym = getDsymbol(o);
            if (sym != null)
            {
                if ((pequals(e.ident, Id.hasMember)))
                {
                    {
                        Dsymbol sm = sym.search(e.loc, id, 0);
                        if ((sm) != null)
                        {
                            return True.invoke();
                        }
                    }
                }
                ex = new DsymbolExp(e.loc, sym, true);
                ex = new DotIdExp(e.loc, ex, id);
            }
            else {
                Type t = isType(o);
                if ((t) != null)
                {
                    ex = typeDotIdExp(e.loc, t, id);
                }
                else {
                    Expression ex2 = isExpression(o);
                    if ((ex2) != null)
                    {
                        ex = new DotIdExp(e.loc, ex2, id);
                    }
                    else
                    {
                        e.error(new BytePtr("invalid first argument"));
                        return new ErrorExp();
                    }
                }
            }
            Ptr<Scope> scx = (sc.get()).push();
            (scx.get()).flags |= 514;
            try {
                if ((pequals(e.ident, Id.hasMember)))
                {
                    ex = trySemantic(ex, scx);
                    return ex != null ? True.invoke() : False.invoke();
                }
                else if ((pequals(e.ident, Id.getMember)))
                {
                    if (((ex.op & 0xFF) == 28))
                    {
                        (((DotIdExp)ex)).wantsym = true;
                    }
                    ex = expressionSemantic(ex, scx);
                    return ex;
                }
                else if ((pequals(e.ident, Id.getVirtualFunctions)) || (pequals(e.ident, Id.getVirtualMethods)) || (pequals(e.ident, Id.getOverloads)))
                {
                    int errors = global.errors;
                    Expression eorig = ex;
                    ex = expressionSemantic(ex, scx);
                    if ((errors < global.errors))
                    {
                        e.error(new BytePtr("`%s` cannot be resolved"), eorig.toChars());
                    }
                    DArray<Expression> exps = new DArray<Expression>();
                    Dsymbol f = null;
                    if (((ex.op & 0xFF) == 26))
                    {
                        VarExp ve = ((VarExp)ex);
                        f = ve.var.isFuncDeclaration();
                        ex = null;
                    }
                    else if (((ex.op & 0xFF) == 27))
                    {
                        DotVarExp dve = ((DotVarExp)ex);
                        f = dve.var.isFuncDeclaration();
                        if (((dve.e1.value.op & 0xFF) == 30) || ((dve.e1.value.op & 0xFF) == 123))
                        {
                            ex = null;
                        }
                        else
                        {
                            ex = dve.e1.value;
                        }
                    }
                    else if (((ex.op & 0xFF) == 36))
                    {
                        VarExp ve = ((VarExp)ex);
                        TemplateDeclaration td = ve.var.isTemplateDeclaration();
                        f = td;
                        if ((td != null) && (td.funcroot != null))
                        {
                            f = td.funcroot;
                        }
                        ex = null;
                    }
                    AA<ByteSlice,Boolean> funcTypeHash = null;
                    Runnable2<FuncDeclaration,Expression> insertInterfaceInheritedFunction = new Runnable2<FuncDeclaration,Expression>() {
                        public Void invoke(FuncDeclaration fd, Expression e) {
                         {
                            BytePtr funcType = pcopy(fd.type.toChars());
                            int len = strlen(funcType);
                            ByteSlice signature = idup(funcType.slice(0,len)).copy();
                            if (funcTypeHash.getLvalue(signature) == null)
                            {
                                funcTypeHash.set(signature, __aaval1691);
                                (exps).push(e);
                            }
                            return null;
                        }}

                    };
                    Function1<Dsymbol,Integer> dg = new Function1<Dsymbol,Integer>() {
                        public Integer invoke(Dsymbol s) {
                         {
                            if (includeTemplates)
                            {
                                (exps).push(new DsymbolExp(Loc.initial, s, false));
                                return 0;
                            }
                            FuncDeclaration fd = s.isFuncDeclaration();
                            if (fd == null)
                            {
                                return 0;
                            }
                            if ((pequals(e.ident, Id.getVirtualFunctions)) && !fd.isVirtual())
                            {
                                return 0;
                            }
                            if ((pequals(e.ident, Id.getVirtualMethods)) && !fd.isVirtualMethod())
                            {
                                return 0;
                            }
                            FuncAliasDeclaration fa = new FuncAliasDeclaration(fd.ident, fd, false);
                            fa.protection.opAssign(fd.protection.copy());
                            Expression e = ex != null ? new DotVarExp(Loc.initial, ex, fa, false) : new DsymbolExp(Loc.initial, fa, false);
                            if ((sym != null) && (sym.isInterfaceDeclaration() != null))
                            {
                                insertInterfaceInheritedFunction.invoke(fd, e);
                            }
                            else
                            {
                                (exps).push(e);
                            }
                            return 0;
                        }}

                    };
                    InterfaceDeclaration ifd = null;
                    if (sym != null)
                    {
                        ifd = sym.isInterfaceDeclaration();
                    }
                    overloadApply(f, dg, null);
                    if ((ifd != null) && (ifd.interfaces.getLength() != 0) && (f != null))
                    {
                        {
                            Slice<Ptr<BaseClass>> __r1692 = ifd.interfaces.copy();
                            int __key1693 = 0;
                            for (; (__key1693 < __r1692.getLength());__key1693 += 1) {
                                Ptr<BaseClass> bc = __r1692.get(__key1693);
                                {
                                    Dsymbol fd = (bc.get()).sym.search(e.loc, f.ident, 8);
                                    if ((fd) != null)
                                    {
                                        overloadApply(fd, dg, null);
                                    }
                                }
                            }
                        }
                    }
                    TupleExp tup = new TupleExp(e.loc, exps);
                    return expressionSemantic(tup, scx);
                }
                else
                {
                    throw new AssertionError("Unreachable code!");
                }
            }
            finally {
                (scx.get()).pop();
            }
        }
        if ((pequals(e.ident, Id.classInstanceSize)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            RootObject o = (e.args).get(0);
            Dsymbol s = getDsymbol(o);
            ClassDeclaration cd = s != null ? s.isClassDeclaration() : null;
            if (cd == null)
            {
                e.error(new BytePtr("first argument is not a class"));
                return new ErrorExp();
            }
            if ((cd.sizeok != Sizeok.done))
            {
                cd.size(e.loc);
            }
            if ((cd.sizeok != Sizeok.done))
            {
                e.error(new BytePtr("%s `%s` is forward referenced"), cd.kind(), cd.toChars());
                return new ErrorExp();
            }
            return new IntegerExp(e.loc, (long)cd.structsize.value, Type.tsize_t);
        }
        if ((pequals(e.ident, Id.getAliasThis)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            RootObject o = (e.args).get(0);
            Dsymbol s = getDsymbol(o);
            AggregateDeclaration ad = s != null ? s.isAggregateDeclaration() : null;
            DArray<Expression> exps = new DArray<Expression>();
            if ((ad != null) && (ad.aliasthis != null))
            {
                (exps).push(new StringExp(e.loc, ad.aliasthis.ident.toChars()));
            }
            Expression ex = new TupleExp(e.loc, exps);
            ex = expressionSemantic(ex, sc);
            return ex;
        }
        if ((pequals(e.ident, Id.getAttributes)))
        {
            if (!TemplateInstance.semanticTiargs(e.loc, sc, e.args, 3))
            {
                return new ErrorExp();
            }
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            RootObject o = (e.args).get(0);
            Parameter po = isParameter(o);
            Dsymbol s = getDsymbolWithoutExpCtx(o);
            UserAttributeDeclaration udad = null;
            if (po != null)
            {
                udad = po.userAttribDecl;
            }
            else if (s != null)
            {
                if (s.isImport() != null)
                {
                    s = s.isImport().mod;
                }
                udad = s.userAttribDecl;
            }
            else
            {
                e.error(new BytePtr("first argument is not a symbol"));
                return new ErrorExp();
            }
            DArray<Expression> exps = udad != null ? udad.getAttributes() : new DArray<Expression>();
            TupleExp tup = new TupleExp(e.loc, exps);
            return expressionSemantic(tup, sc);
        }
        if ((pequals(e.ident, Id.getFunctionAttributes)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            Ref<FuncDeclaration> fd = ref(null);
            TypeFunction tf = toTypeFunction.invoke((e.args).get(0), fd);
            if (tf == null)
            {
                e.error(new BytePtr("first argument is not a function"));
                return new ErrorExp();
            }
            DArray<Expression> mods = new DArray<Expression>();
            Runnable1<ByteSlice> addToMods = new Runnable1<ByteSlice>() {
                public Void invoke(ByteSlice str) {
                 {
                    (mods).push(new StringExp(Loc.initial, toBytePtr(str.getPtr(0)), str.getLength()));
                    return null;
                }}

            };
            modifiersApply(tf, addToMods);
            attributesApply(tf, addToMods, TRUSTformat.TRUSTformatSystem);
            TupleExp tup = new TupleExp(e.loc, mods);
            return expressionSemantic(tup, sc);
        }
        if ((pequals(e.ident, Id.isReturnOnStack)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            RootObject o = (e.args).get(0);
            Ref<FuncDeclaration> fd = ref(null);
            TypeFunction tf = toTypeFunction.invoke(o, fd);
            if (tf == null)
            {
                e.error(new BytePtr("argument to `__traits(isReturnOnStack, %s)` is not a function"), o.toChars());
                return new ErrorExp();
            }
            boolean value = target.isReturnOnStack(tf, (fd.value != null) && fd.value.needThis());
            return new IntegerExp(e.loc, (value ? 1 : 0), Type.tbool);
        }
        if ((pequals(e.ident, Id.getFunctionVariadicStyle)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            int link = LINK.default_;
            int varargs = VarArg.none;
            RootObject o = (e.args).get(0);
            Ref<FuncDeclaration> fd = ref(null);
            TypeFunction tf = toTypeFunction.invoke(o, fd);
            if (tf != null)
            {
                link = tf.linkage;
                varargs = tf.parameterList.varargs;
            }
            else
            {
                if (fd.value == null)
                {
                    e.error(new BytePtr("argument to `__traits(getFunctionVariadicStyle, %s)` is not a function"), o.toChars());
                    return new ErrorExp();
                }
                link = fd.value.linkage;
                varargs = fd.value.getParameterList().varargs;
            }
            ByteSlice style = new ByteSlice().copy();
            switch (varargs)
            {
                case VarArg.none:
                    style = new ByteSlice("none").copy();
                    break;
                case VarArg.variadic:
                    style = (link == LINK.d) ? new ByteSlice("argptr") : new ByteSlice("stdarg").copy();
                    break;
                case VarArg.typesafe:
                    style = new ByteSlice("typesafe").copy();
                    break;
                default:
                throw SwitchError.INSTANCE;
            }
            StringExp se = new StringExp(e.loc, style.getPtr(0));
            return expressionSemantic(se, sc);
        }
        if ((pequals(e.ident, Id.getParameterStorageClasses)))
        {
            if ((dim != 2))
            {
                return dimError.invoke(2);
            }
            RootObject o = (e.args).get(0);
            RootObject o1 = (e.args).get(1);
            Ref<FuncDeclaration> fd = ref(null);
            TypeFunction tf = toTypeFunction.invoke(o, fd);
            ParameterList fparams = new ParameterList();
            if (tf != null)
            {
                fparams.opAssign(tf.parameterList.copy());
            }
            else if (fd.value != null)
            {
                fparams.opAssign(fd.value.getParameterList().copy());
            }
            else
            {
                e.error(new BytePtr("first argument to `__traits(getParameterStorageClasses, %s, %s)` is not a function"), o.toChars(), o1.toChars());
                return new ErrorExp();
            }
            long stc = 0L;
            Expression ex = isExpression((e.args).get(1));
            if (ex == null)
            {
                e.error(new BytePtr("expression expected as second argument of `__traits(getParameterStorageClasses, %s, %s)`"), o.toChars(), o1.toChars());
                return new ErrorExp();
            }
            ex = ex.ctfeInterpret();
            long ii = ex.toUInteger();
            if ((ii >= (long)fparams.length()))
            {
                e.error(new BytePtr("parameter index must be in range 0..%u not %s"), fparams.length(), ex.toChars());
                return new ErrorExp();
            }
            int n = (int)ii;
            Parameter p = fparams.get(n);
            stc = p.storageClass;
            if ((p.type != null) && (((p.type.mod & 0xFF) & MODFlags.shared_) != 0))
            {
                stc &= -536870913L;
            }
            DArray<Expression> exps = new DArray<Expression>();
            Runnable1<ByteSlice> push = new Runnable1<ByteSlice>() {
                public Void invoke(ByteSlice s) {
                 {
                    (exps).push(new StringExp(e.loc, toBytePtr(s.getPtr(0)), s.getLength()));
                    return null;
                }}

            };
            if ((stc & 256L) != 0)
            {
                push.invoke(new ByteSlice("auto"));
            }
            if ((stc & 17592186044416L) != 0)
            {
                push.invoke(new ByteSlice("return"));
            }
            if ((stc & 4096L) != 0)
            {
                push.invoke(new ByteSlice("out"));
            }
            else if ((stc & 2097152L) != 0)
            {
                push.invoke(new ByteSlice("ref"));
            }
            else if ((stc & 2048L) != 0)
            {
                push.invoke(new ByteSlice("in"));
            }
            else if ((stc & 8192L) != 0)
            {
                push.invoke(new ByteSlice("lazy"));
            }
            else if ((stc & 268435456L) != 0)
            {
                push.invoke(new ByteSlice("alias"));
            }
            if ((stc & 4L) != 0)
            {
                push.invoke(new ByteSlice("const"));
            }
            if ((stc & 1048576L) != 0)
            {
                push.invoke(new ByteSlice("immutable"));
            }
            if ((stc & 2147483648L) != 0)
            {
                push.invoke(new ByteSlice("inout"));
            }
            if ((stc & 536870912L) != 0)
            {
                push.invoke(new ByteSlice("shared"));
            }
            if (((stc & 524288L) != 0) && ((stc & 562949953421312L) == 0))
            {
                push.invoke(new ByteSlice("scope"));
            }
            TupleExp tup = new TupleExp(e.loc, exps);
            return expressionSemantic(tup, sc);
        }
        if ((pequals(e.ident, Id.getLinkage)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            int link = LINK.default_;
            RootObject o = (e.args).get(0);
            Ref<FuncDeclaration> fd = ref(null);
            TypeFunction tf = toTypeFunction.invoke(o, fd);
            if (tf != null)
            {
                link = tf.linkage;
            }
            else
            {
                Dsymbol s = getDsymbol(o);
                Declaration d = null;
                AggregateDeclaration agg = null;
                if ((s == null) || ((d = s.isDeclaration()) == null) && ((agg = s.isAggregateDeclaration()) == null))
                {
                    e.error(new BytePtr("argument to `__traits(getLinkage, %s)` is not a declaration"), o.toChars());
                    return new ErrorExp();
                }
                if ((d != null))
                {
                    link = d.linkage;
                }
                else
                {
                    switch (agg.classKind)
                    {
                        case ClassKind.d:
                            link = LINK.d;
                            break;
                        case ClassKind.cpp:
                            link = LINK.cpp;
                            break;
                        case ClassKind.objc:
                            link = LINK.objc;
                            break;
                        default:
                        throw SwitchError.INSTANCE;
                    }
                }
            }
            BytePtr linkage = pcopy(linkageToChars(link));
            StringExp se = new StringExp(e.loc, linkage);
            return expressionSemantic(se, sc);
        }
        if ((pequals(e.ident, Id.allMembers)) || (pequals(e.ident, Id.derivedMembers)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            RootObject o = (e.args).get(0);
            Dsymbol s = getDsymbol(o);
            if (s == null)
            {
                e.error(new BytePtr("argument has no members"));
                return new ErrorExp();
            }
            {
                Import imp = s.isImport();
                if ((imp) != null)
                {
                    s = imp.mod;
                }
            }
            ScopeDsymbol sds = s.isScopeDsymbol();
            if ((sds == null) || (sds.isTemplateDeclaration() != null))
            {
                e.error(new BytePtr("%s `%s` has no members"), s.kind(), s.toChars());
                return new ErrorExp();
            }
            DArray<Identifier> idents = new DArray<Identifier>();
            Function2<Integer,Dsymbol,Integer> pushIdentsDg = new Function2<Integer,Dsymbol,Integer>() {
                public Integer invoke(Integer n, Dsymbol sm) {
                 {
                    if (sm == null)
                    {
                        return 1;
                    }
                    {
                        Declaration decl = sm.isDeclaration();
                        if ((decl) != null)
                        {
                            if ((decl.storage_class & 2251799813685248L) != 0)
                            {
                                return 0;
                            }
                        }
                    }
                    if (sm.ident != null)
                    {
                        BytePtr idx = pcopy(sm.ident.toChars());
                        if (((idx.get(0) & 0xFF) == 95) && ((idx.get(1) & 0xFF) == 95) && (!pequals(sm.ident, Id.ctor)) && (!pequals(sm.ident, Id.dtor)) && (!pequals(sm.ident, Id.__xdtor)) && (!pequals(sm.ident, Id.postblit)) && (!pequals(sm.ident, Id.__xpostblit)))
                        {
                            return 0;
                        }
                        if ((pequals(sm.ident, Id.empty)))
                        {
                            return 0;
                        }
                        if (sm.isTypeInfoDeclaration() != null)
                        {
                            return 0;
                        }
                        if ((sds.isModule() == null) && (sm.isImport() != null))
                        {
                            return 0;
                        }
                        {
                            Slice<Identifier> __r1694 = (idents).opSlice().copy();
                            Ref<Integer> __key1695 = ref(0);
                            for (; (__key1695.value < __r1694.getLength());__key1695.value += 1) {
                                Identifier id = __r1694.get(__key1695.value);
                                if ((pequals(id, sm.ident)))
                                {
                                    return 0;
                                }
                            }
                        }
                        (idents).push(sm.ident);
                    }
                    else {
                        EnumDeclaration ed = sm.isEnumDeclaration();
                        if ((ed) != null)
                        {
                            ScopeDsymbol._foreach(null, ed.members, pushIdentsDg, null);
                        }
                    }
                    return 0;
                }}

            };
            ScopeDsymbol._foreach(sc, sds.members, pushIdentsDg, null);
            ClassDeclaration cd = sds.isClassDeclaration();
            if ((cd != null) && (pequals(e.ident, Id.allMembers)))
            {
                if ((cd.semanticRun < PASS.semanticdone))
                {
                    dsymbolSemantic(cd, null);
                }
                Runnable1<ClassDeclaration> pushBaseMembersDg = new Runnable1<ClassDeclaration>() {
                    public Void invoke(ClassDeclaration cd) {
                     {
                        {
                            int i = 0;
                            for (; (i < (cd.baseclasses).length);i++){
                                ClassDeclaration cb = ((cd.baseclasses).get(i).get()).sym;
                                assert(cb != null);
                                ScopeDsymbol._foreach(null, cb.members, pushIdentsDg, null);
                                if ((cb.baseclasses).length != 0)
                                {
                                    invoke(cb);
                                }
                            }
                        }
                        return null;
                    }}

                };
                pushBaseMembersDg.invoke(cd);
            }
            assert(true);
            DArray<Expression> exps = ((DArray<Expression>)idents);
            {
                Slice<Identifier> __r1697 = (idents).opSlice().copy();
                int __key1696 = 0;
                for (; (__key1696 < __r1697.getLength());__key1696 += 1) {
                    Identifier id = __r1697.get(__key1696);
                    int i = __key1696;
                    StringExp se = new StringExp(e.loc, id.toChars());
                    exps.set(i, se);
                }
            }
            Expression ex = new TupleExp(e.loc, exps);
            ex = expressionSemantic(ex, sc);
            return ex;
        }
        if ((pequals(e.ident, Id.compiles)))
        {
            if (dim == 0)
            {
                return False.invoke();
            }
            {
                Slice<RootObject> __r1698 = (e.args).opSlice().copy();
                int __key1699 = 0;
                for (; (__key1699 < __r1698.getLength());__key1699 += 1) {
                    RootObject o = __r1698.get(__key1699);
                    int errors = global.startGagging();
                    Ptr<Scope> sc2 = (sc.get()).push();
                    (sc2.get()).tinst = null;
                    (sc2.get()).minst = null;
                    (sc2.get()).flags = (sc.get()).flags & -133 | 256 | 65536;
                    boolean err = false;
                    Ref<Type> t = ref(isType(o));
                    Ref<Expression> ex = ref(t.value != null ? typeToExpression(t.value) : isExpression(o));
                    if ((ex.value == null) && (t.value != null))
                    {
                        Ref<Dsymbol> s = ref(null);
                        resolve(t.value, e.loc, sc2, ptr(ex), ptr(t), ptr(s), false);
                        if (t.value != null)
                        {
                            typeSemantic(t.value, e.loc, sc2);
                            if (((t.value.ty & 0xFF) == ENUMTY.Terror))
                            {
                                err = true;
                            }
                        }
                        else if ((s.value != null) && s.value.errors)
                        {
                            err = true;
                        }
                    }
                    if (ex.value != null)
                    {
                        ex.value = expressionSemantic(ex.value, sc2);
                        ex.value = resolvePropertiesOnly(sc2, ex.value);
                        ex.value = ex.value.optimize(0, false);
                        if (((sc2.get()).func != null) && (((sc2.get()).func.type.ty & 0xFF) == ENUMTY.Tfunction))
                        {
                            TypeFunction tf = ((TypeFunction)(sc2.get()).func.type);
                            (err ? 1 : 0) |= ((tf.isnothrow && canThrow(ex.value, (sc2.get()).func, false)) ? 1 : 0);
                        }
                        ex.value = checkGC(sc2, ex.value);
                        if (((ex.value.op & 0xFF) == 127))
                        {
                            err = true;
                        }
                    }
                    (sc2.get()).detach();
                    if (global.endGagging(errors) || err)
                    {
                        return False.invoke();
                    }
                }
            }
            return True.invoke();
        }
        if ((pequals(e.ident, Id.isSame)))
        {
            if ((dim != 2))
            {
                return dimError.invoke(2);
            }
            if (!TemplateInstance.semanticTiargs(e.loc, sc, e.args, 0))
            {
                return new ErrorExp();
            }
            RootObject o1 = (e.args).get(0);
            RootObject o2 = (e.args).get(1);
            Function1<RootObject,FuncLiteralDeclaration> isLambda = new Function1<RootObject,FuncLiteralDeclaration>() {
                public FuncLiteralDeclaration invoke(RootObject oarg) {
                 {
                    {
                        Dsymbol t = isDsymbol(oarg);
                        if ((t) != null)
                        {
                            {
                                TemplateDeclaration td = t.isTemplateDeclaration();
                                if ((td) != null)
                                {
                                    if ((td.members != null) && ((td.members).length == 1))
                                    {
                                        {
                                            FuncLiteralDeclaration fd = (td.members).get(0).isFuncLiteralDeclaration();
                                            if ((fd) != null)
                                            {
                                                return fd;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            Expression ea = isExpression(oarg);
                            if ((ea) != null)
                            {
                                if (((ea.op & 0xFF) == 161))
                                {
                                    {
                                        FuncExp fe = ((FuncExp)ea);
                                        if ((fe) != null)
                                        {
                                            return fe.fd;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return null;
                }}

            };
            FuncLiteralDeclaration l1 = isLambda.invoke(o1);
            FuncLiteralDeclaration l2 = isLambda.invoke(o2);
            if ((l1 != null) && (l2 != null))
            {
                if (isSameFuncLiteral(l1, l2, sc))
                {
                    return True.invoke();
                }
            }
            Type t1 = isType(o1);
            Type t2 = isType(o2);
            if ((t1 != null) && (t2 != null) && t1.equals(t2))
            {
                return True.invoke();
            }
            Dsymbol s1 = getDsymbol(o1);
            Dsymbol s2 = getDsymbol(o2);
            if ((s1 == null) && (s2 == null))
            {
                Expression ea1 = isExpression(o1);
                Expression ea2 = isExpression(o2);
                if ((ea1 != null) && (ea2 != null))
                {
                    if (ea1.equals(ea2))
                    {
                        return True.invoke();
                    }
                }
            }
            if ((s1 == null) || (s2 == null))
            {
                return False.invoke();
            }
            s1 = s1.toAlias();
            s2 = s2.toAlias();
            {
                FuncAliasDeclaration fa1 = s1.isFuncAliasDeclaration();
                if ((fa1) != null)
                {
                    s1 = fa1.toAliasFunc();
                }
            }
            {
                FuncAliasDeclaration fa2 = s2.isFuncAliasDeclaration();
                if ((fa2) != null)
                {
                    s2 = fa2.toAliasFunc();
                }
            }
            Function2<Dsymbol,Dsymbol,Boolean> cmp = new Function2<Dsymbol,Dsymbol,Boolean>() {
                public Boolean invoke(Dsymbol s1, Dsymbol s2) {
                 {
                    Import imp = s1.isImport();
                    return (imp != null) && (imp.pkg.value != null) && (pequals(imp.pkg.value, s2.isPackage()));
                }}

            };
            if (cmp.invoke(s1, s2) || cmp.invoke(s2, s1))
            {
                return True.invoke();
            }
            if ((pequals(s1, s2)))
            {
                return True.invoke();
            }
            OverloadSet overSet1 = s1.isOverloadSet();
            if (overSet1 == null)
            {
                return False.invoke();
            }
            OverloadSet overSet2 = s2.isOverloadSet();
            if (overSet2 == null)
            {
                return False.invoke();
            }
            if ((overSet1.a.length != overSet2.a.length))
            {
                return False.invoke();
            }
        /*Lnext:*/
            {
                Slice<Dsymbol> __r1700 = overSet1.a.opSlice().copy();
                int __key1701 = 0;
                for (; (__key1701 < __r1700.getLength());__key1701 += 1) {
                    Dsymbol overload1 = __r1700.get(__key1701);
                    {
                        Slice<Dsymbol> __r1702 = overSet2.a.opSlice().copy();
                        int __key1703 = 0;
                        for (; (__key1703 < __r1702.getLength());__key1703 += 1) {
                            Dsymbol overload2 = __r1702.get(__key1703);
                            if ((pequals(overload1, overload2)))
                            {
                                continue Lnext;
                            }
                        }
                    }
                    return False.invoke();
                }
            }
            return True.invoke();
        }
        if ((pequals(e.ident, Id.getUnitTests)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            RootObject o = (e.args).get(0);
            Dsymbol s = getDsymbolWithoutExpCtx(o);
            if (s == null)
            {
                e.error(new BytePtr("argument `%s` to __traits(getUnitTests) must be a module or aggregate"), o.toChars());
                return new ErrorExp();
            }
            {
                Import imp = s.isImport();
                if ((imp) != null)
                {
                    s = imp.mod;
                }
            }
            ScopeDsymbol sds = s.isScopeDsymbol();
            if (sds == null)
            {
                e.error(new BytePtr("argument `%s` to __traits(getUnitTests) must be a module or aggregate, not a %s"), s.toChars(), s.kind());
                return new ErrorExp();
            }
            DArray<Expression> exps = new DArray<Expression>();
            if (global.params.useUnitTests)
            {
                AA<Object,Boolean> uniqueUnitTests = null;
                Runnable1<Dsymbol> symbolDg = new Runnable1<Dsymbol>() {
                    public Void invoke(Dsymbol s) {
                     {
                        {
                            AttribDeclaration ad = s.isAttribDeclaration();
                            if ((ad) != null)
                            {
                                foreachDsymbol(ad.include(null), symbolDg);
                            }
                            else {
                                UnitTestDeclaration ud = s.isUnitTestDeclaration();
                                if ((ud) != null)
                                {
                                    if (uniqueUnitTests.getLvalue((ud)) != null)
                                    {
                                        return null;
                                    }
                                    uniqueUnitTests.set((ud), __aaval1704);
                                    FuncAliasDeclaration ad = new FuncAliasDeclaration(ud.ident, ud, false);
                                    ad.protection.opAssign(ud.protection.copy());
                                    DsymbolExp e = new DsymbolExp(Loc.initial, ad, false);
                                    (exps).push(e);
                                }
                            }
                        }
                        return null;
                    }}

                };
                foreachDsymbol(sds.members, symbolDg);
            }
            TupleExp te = new TupleExp(e.loc, exps);
            return expressionSemantic(te, sc);
        }
        if ((pequals(e.ident, Id.getVirtualIndex)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            RootObject o = (e.args).get(0);
            Dsymbol s = getDsymbolWithoutExpCtx(o);
            FuncDeclaration fd = s != null ? s.isFuncDeclaration() : null;
            if (fd == null)
            {
                e.error(new BytePtr("first argument to __traits(getVirtualIndex) must be a function"));
                return new ErrorExp();
            }
            fd = fd.toAliasFunc();
            return new IntegerExp(e.loc, (long)fd.vtblIndex, Type.tptrdiff_t);
        }
        if ((pequals(e.ident, Id.getPointerBitmap)))
        {
            return pointerBitmap(e);
        }
        if ((pequals(e.ident, Id.isZeroInit)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            RootObject o = (e.args).get(0);
            Type t = isType(o);
            if (t == null)
            {
                e.error(new BytePtr("type expected as second argument of __traits `%s` instead of `%s`"), e.ident.toChars(), o.toChars());
                return new ErrorExp();
            }
            Type tb = t.baseElemOf();
            return tb.isZeroInit(e.loc) ? True.invoke() : False.invoke();
        }
        if ((pequals(e.ident, Id.getTargetInfo)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            Expression ex = isExpression((e.args).get(0));
            StringExp se = ex != null ? ex.ctfeInterpret().toStringExp() : null;
            if ((ex == null) || (se == null) || (se.len == 0))
            {
                e.error(new BytePtr("string expected as argument of __traits `%s` instead of `%s`"), e.ident.toChars(), ex.toChars());
                return new ErrorExp();
            }
            se = se.toUTF8(sc);
            Expression r = target.getTargetInfo(se.toPtr(), e.loc);
            if (r == null)
            {
                e.error(new BytePtr("`getTargetInfo` key `\"%s\"` not supported by this implementation"), se.toPtr());
                return new ErrorExp();
            }
            return expressionSemantic(r, sc);
        }
        if ((pequals(e.ident, Id.getLocation)))
        {
            if ((dim != 1))
            {
                return dimError.invoke(1);
            }
            RootObject arg0 = (e.args).get(0);
            Dsymbol s = getDsymbolWithoutExpCtx(arg0);
            if (s == null)
            {
                e.error(new BytePtr("can only get the location of a symbol, not `%s`"), arg0.toChars());
                return new ErrorExp();
            }
            FuncDeclaration fd = s.isFuncDeclaration();
            if ((fd != null) && (fd.overnext != null))
            {
                e.error(new BytePtr("cannot get location of an overload set, use `__traits(getOverloads, ..., \"%s\"%s)[N]` to get the Nth overload"), arg0.toChars(), new BytePtr(""));
                return new ErrorExp();
            }
            DArray<Expression> exps = new DArray<Expression>(3);
            (exps).data.set(0, new StringExp(e.loc, s.loc.filename, strlen(s.loc.filename)));
            (exps).data.set(1, new IntegerExp(e.loc, (long)s.loc.linnum, Type.tint32));
            (exps).data.set(2, new IntegerExp(e.loc, (long)s.loc.charnum, Type.tint32));
            TupleExp tup = new TupleExp(e.loc, exps);
            return expressionSemantic(tup, sc);
        }
        Function2<ByteSlice,Ref<Integer>,BytePtr> trait_search_fp = new Function2<ByteSlice,Ref<Integer>,BytePtr>() {
            public BytePtr invoke(ByteSlice seed, Ref<Integer> cost) {
             {
                if (seed.getLength() == 0)
                {
                    return null;
                }
                cost.value = 0;
                Ptr<StringValue> sv = traitsStringTable.lookup(seed);
                return sv != null ? ((BytePtr)(sv.get()).ptrvalue) : null;
            }}

        };
        {
            BytePtr sub = pcopy(speller.invoke(e.ident.asString()));
            if ((sub) != null)
            {
                e.error(new BytePtr("unrecognized trait `%s`, did you mean `%s`?"), e.ident.toChars(), sub);
            }
            else
            {
                e.error(new BytePtr("unrecognized trait `%s`"), e.ident.toChars());
            }
        }
        return new ErrorExp();
    }

}
