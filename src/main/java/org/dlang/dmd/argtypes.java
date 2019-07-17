package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.visitor.*;

public class argtypes {
    private static class ToArgTypes extends Visitor
    {
        private TypeTuple result = null;
        // Erasure: memory<>
        public  void memory() {
            this.result = new TypeTuple();
        }

        // Erasure: oneType<Type>
        public  void oneType(Type t) {
            this.result = new TypeTuple(t);
        }

        // Erasure: twoTypes<Type, Type>
        public  void twoTypes(Type t1, Type t2) {
            this.result = new TypeTuple(t1, t2);
        }

        // Erasure: visit<Type>
        public  void visit(Type _param_0) {
        }

        // Erasure: visit<TypeError>
        public  void visit(TypeError _param_0) {
            this.result = new TypeTuple(Type.terror);
        }

        // Erasure: visit<TypeBasic>
        public  void visit(TypeBasic t) {
            Type t1 = null;
            Type t2 = null;
            switch ((t.ty & 0xFF))
            {
                case 12:
                    return ;
                case 30:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 21:
                case 19:
                case 20:
                case 42:
                case 43:
                case 22:
                case 23:
                    t1 = t;
                    break;
                case 24:
                    t1 = Type.tfloat32;
                    break;
                case 25:
                    t1 = Type.tfloat64;
                    break;
                case 26:
                    t1 = Type.tfloat80;
                    break;
                case 27:
                    if (isDMDx64Target())
                    {
                        t1 = Type.tfloat64;
                    }
                    else
                    {
                        t1 = Type.tfloat64;
                        t2 = Type.tfloat64;
                    }
                    break;
                case 28:
                    t1 = Type.tfloat64;
                    t2 = Type.tfloat64;
                    break;
                case 29:
                    t1 = Type.tfloat80;
                    t2 = Type.tfloat80;
                    break;
                case 31:
                    t1 = Type.tuns8;
                    break;
                case 32:
                    t1 = Type.tuns16;
                    break;
                case 33:
                    t1 = Type.tuns32;
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            if (t1 != null)
            {
                if (t2 != null)
                {
                    this.twoTypes(t1, t2);
                    return ;
                }
                else
                {
                    this.oneType(t1);
                    return ;
                }
            }
            else
            {
                this.memory();
                return ;
            }
        }

        // Erasure: visit<TypeVector>
        public  void visit(TypeVector t) {
            this.oneType(t);
            return ;
        }

        // Erasure: visit<TypeAArray>
        public  void visit(TypeAArray _param_0) {
            this.oneType(Type.tvoidptr);
            return ;
        }

        // Erasure: visit<TypePointer>
        public  void visit(TypePointer _param_0) {
            this.oneType(Type.tvoidptr);
            return ;
        }

        // Erasure: mergeFloatToInt<Type>
        public static Type mergeFloatToInt(Type t) {
            switch ((t.ty & 0xFF))
            {
                case 21:
                case 24:
                    t = Type.tint32;
                    break;
                case 22:
                case 25:
                case 27:
                    t = Type.tint64;
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            return t;
        }

        // Erasure: argtypemerge<Type, Type, int>
        public static Type argtypemerge(Type t1, Type t2, int offset2) {
            if (t1 == null)
            {
                assert((t2 == null) || (offset2 == 0));
                return t2;
            }
            if (t2 == null)
            {
                return t1;
            }
            long sz1 = t1.size(Loc.initial);
            long sz2 = t2.size(Loc.initial);
            assert((sz1 != -1L) && (sz2 != -1L));
            if (((t1.ty & 0xFF) != (t2.ty & 0xFF)) && ((t1.ty & 0xFF) == ENUMTY.Tfloat80) || ((t2.ty & 0xFF) == ENUMTY.Tfloat80))
            {
                return null;
            }
            if (((t1.ty & 0xFF) == ENUMTY.Tfloat32) && ((t2.ty & 0xFF) == ENUMTY.Tfloat32) && (offset2 == 4))
            {
                return Type.tfloat64;
            }
            if (t1.isfloating())
            {
                if (!t2.isfloating())
                {
                    t1 = mergeFloatToInt(t1);
                }
            }
            else if (t2.isfloating())
            {
                t2 = mergeFloatToInt(t2);
            }
            Type t = null;
            if ((sz1 < sz2))
            {
                t = t2;
            }
            else
            {
                t = t1;
            }
            Ref<Boolean> overflow = ref(false);
            long offset3 = addu((long)offset2, sz2, overflow);
            assert(!overflow.value);
            if ((offset2 != 0) && (sz1 < offset3))
            {
                switch ((int)offset3)
                {
                    case (int)2L:
                        t = Type.tint16;
                        break;
                    case (int)3L:
                    case (int)4L:
                        t = Type.tint32;
                        break;
                    default:
                    t = Type.tint64;
                    break;
                }
            }
            return t;
        }

        // Erasure: visit<TypeDArray>
        public  void visit(TypeDArray _param_0) {
            if (isDMDx64Target() && !global.params.isLP64)
            {
                int offset = (int)Type.tsize_t.size(Loc.initial);
                Type t = argtypemerge(Type.tsize_t, Type.tvoidptr, offset);
                if (t != null)
                {
                    this.oneType(t);
                    return ;
                }
            }
            this.twoTypes(Type.tsize_t, Type.tvoidptr);
            return ;
        }

        // Erasure: visit<TypeDelegate>
        public  void visit(TypeDelegate _param_0) {
            if (isDMDx64Target() && !global.params.isLP64)
            {
                int offset = (int)Type.tsize_t.size(Loc.initial);
                Type t = argtypemerge(Type.tvoidptr, Type.tvoidptr, offset);
                if (t != null)
                {
                    this.oneType(t);
                    return ;
                }
            }
            this.twoTypes(Type.tvoidptr, Type.tvoidptr);
            return ;
        }

        // Erasure: visit<TypeSArray>
        public  void visit(TypeSArray t) {
            ToArgTypes __self = this;
            long sz = t.size(Loc.initial);
            if ((sz > 16L))
            {
                this.memory();
                return ;
            }
            long dim = t.dim.toInteger();
            Type tn = t.next.value;
            long tnsize = tn.size();
            int tnalignsize = tn.alignsize();
            Function3<Integer,Ref<Integer>,Ref<Integer>,Type> getNthElement = new Function3<Integer,Ref<Integer>,Ref<Integer>,Type>() {
                public Type invoke(Integer n, Ref<Integer> offset, Ref<Integer> alignsize) {
                 {
                    offset.value = 0;
                    alignsize.value = 0;
                    offset.value = (int)((long)n * tnsize);
                    alignsize.value = tnalignsize;
                    return tn;
                }}

            };
            this.aggregate(sz, (int)dim, getNthElement);
        }

        // Erasure: visit<TypeStruct>
        public  void visit(TypeStruct t) {
            ToArgTypes __self = this;
            if (!t.sym.isPOD())
            {
                this.memory();
                return ;
            }
            Function3<Integer,Ref<Integer>,Ref<Integer>,Type> getNthField = new Function3<Integer,Ref<Integer>,Ref<Integer>,Type>() {
                public Type invoke(Integer n, Ref<Integer> offset, Ref<Integer> alignsize) {
                 {
                    offset.value = 0;
                    alignsize.value = 0;
                    VarDeclaration field = t.sym.fields.get(n);
                    offset.value = field.offset;
                    alignsize.value = field.type.alignsize();
                    return field.type;
                }}

            };
            this.aggregate(t.size(Loc.initial), t.sym.fields.length, getNthField);
        }

        // Erasure: aggregate<long, int, Function3>
        public  void aggregate(long sz, int nfields, Function3<Integer,Ref<Integer>,Ref<Integer>,Type> getFieldInfo) {
            if ((nfields == 0))
            {
                this.memory();
                return ;
            }
            if (isDMDx64Target())
            {
                if ((sz == 0L) || (sz > 16L))
                {
                    this.memory();
                    return ;
                }
                Type t1 = null;
                Type t2 = null;
                {
                    int __key709 = 0;
                    int __limit710 = nfields;
                    for (; (__key709 < __limit710);__key709 += 1) {
                        int n = __key709;
                        Ref<Integer> foffset = ref(0);
                        Ref<Integer> falignsize = ref(0);
                        Type ftype = getFieldInfo.invoke(n, foffset, falignsize);
                        TypeTuple tup = toArgTypes(ftype);
                        if (tup == null)
                        {
                            this.memory();
                            return ;
                        }
                        int dim = (tup.arguments).length;
                        Type ft1 = null;
                        Type ft2 = null;
                        switch (dim)
                        {
                            case 2:
                                ft1 = (tup.arguments).get(0).type;
                                ft2 = (tup.arguments).get(1).type;
                                break;
                            case 1:
                                if ((foffset.value < 8))
                                {
                                    ft1 = (tup.arguments).get(0).type;
                                }
                                else
                                {
                                    ft2 = (tup.arguments).get(0).type;
                                }
                                break;
                            default:
                            this.memory();
                            return ;
                        }
                        if ((foffset.value & 7) != 0)
                        {
                            if ((foffset.value & falignsize.value - 1) != 0)
                            {
                                this.memory();
                                return ;
                            }
                            long fieldsz = ftype.size(Loc.initial);
                            Ref<Boolean> overflow = ref(false);
                            long nextOffset = addu((long)foffset.value, fieldsz, overflow);
                            assert(!overflow.value);
                            if ((foffset.value < 8) && (nextOffset > 8L))
                            {
                                this.memory();
                                return ;
                            }
                        }
                        assert((t1 != null) || (foffset.value == 0));
                        if (ft1 != null)
                        {
                            t1 = argtypemerge(t1, ft1, foffset.value);
                            if (t1 == null)
                            {
                                this.memory();
                                return ;
                            }
                        }
                        if (ft2 != null)
                        {
                            int off2 = ft1 != null ? 8 : foffset.value;
                            if ((t2 == null) && (off2 != 8))
                            {
                                this.memory();
                                return ;
                            }
                            assert((t2 != null) || (off2 == 8));
                            t2 = argtypemerge(t2, ft2, off2 - 8);
                            if (t2 == null)
                            {
                                this.memory();
                                return ;
                            }
                        }
                    }
                }
                if (t2 != null)
                {
                    if (t1.isfloating() && t2.isfloating())
                    {
                        if (((t1.ty & 0xFF) == ENUMTY.Tfloat32) || ((t1.ty & 0xFF) == ENUMTY.Tfloat64) && ((t2.ty & 0xFF) == ENUMTY.Tfloat32) || ((t2.ty & 0xFF) == ENUMTY.Tfloat64))
                        {
                        }
                        else
                        {
                            this.memory();
                            return ;
                        }
                    }
                    else if (t1.isfloating() || t2.isfloating())
                    {
                        this.memory();
                        return ;
                    }
                    this.twoTypes(t1, t2);
                    return ;
                }
                if (t1 != null)
                {
                    this.oneType(t1);
                    return ;
                }
                else
                {
                    this.memory();
                    return ;
                }
            }
            else
            {
                Type t1 = null;
                switch ((int)sz)
                {
                    case 1:
                        t1 = Type.tint8;
                        break;
                    case 2:
                        t1 = Type.tint16;
                        break;
                    case 4:
                        t1 = Type.tint32;
                        break;
                    case 8:
                        t1 = Type.tint64;
                        break;
                    case 16:
                        t1 = null;
                        break;
                    default:
                    this.memory();
                    return ;
                }
                if (global.params.isFreeBSD && (nfields == 1) && (sz == 4L) || (sz == 8L))
                {
                    Ref<Integer> foffset = ref(0);
                    Ref<Integer> falignsize = ref(0);
                    Type ftype = getFieldInfo.invoke(0, foffset, falignsize);
                    TypeTuple tup = toArgTypes(ftype);
                    if ((tup != null) && ((tup.arguments).length == 1))
                    {
                        Type ft1 = (tup.arguments).get(0).type;
                        if (((ft1.ty & 0xFF) == ENUMTY.Tfloat32) || ((ft1.ty & 0xFF) == ENUMTY.Tfloat64))
                        {
                            this.oneType(ft1);
                            return ;
                        }
                    }
                }
                if (t1 != null)
                {
                    this.oneType(t1);
                    return ;
                }
                else
                {
                    this.memory();
                    return ;
                }
            }
        }

        // Erasure: visit<TypeEnum>
        public  void visit(TypeEnum t) {
            t.toBasetype().accept(this);
        }

        // Erasure: visit<TypeClass>
        public  void visit(TypeClass _param_0) {
            this.result = new TypeTuple(Type.tvoidptr);
        }


        public ToArgTypes() {}
    }

    // Erasure: isDMDx64Target<>
    public static boolean isDMDx64Target() {
        return global.params.is64bit;
    }

    // Erasure: toArgTypes<Type>
    public static TypeTuple toArgTypes(Type t) {
        // skipping duplicate class ToArgTypes
        ToArgTypes v = new ToArgTypes();
        t.accept(v);
        return v.result;
    }

}
