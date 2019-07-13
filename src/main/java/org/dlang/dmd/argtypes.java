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
        public  void memory() {
            this.result = new TypeTuple();
        }

        public  void oneType(Type t) {
            this.result = new TypeTuple(t);
        }

        public  void twoTypes(Type t1, Type t2) {
            this.result = new TypeTuple(t1, t2);
        }

        public  void visit(Type _param_0) {
        }

        public  void visit(TypeError _param_0) {
            this.result = new TypeTuple(Type.terror);
        }

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

        public  void visit(TypeVector t) {
            this.oneType(t);
            return ;
        }

        public  void visit(TypeAArray _param_0) {
            this.oneType(Type.tvoidptr);
            return ;
        }

        public  void visit(TypePointer _param_0) {
            this.oneType(Type.tvoidptr);
            return ;
        }

        public static Type mergeFloatToInt(Type t) {
            switch ((t.ty & 0xFF))
            {
                case 21:
                case 24:
                    t = Type.tint32.value;
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
                switch (offset3)
                {
                    case 2L:
                        t = Type.tint16;
                        break;
                    case 3L:
                    case 4L:
                        t = Type.tint32.value;
                        break;
                    default:
                    t = Type.tint64;
                    break;
                }
            }
            return t;
        }

        public  void visit(TypeDArray _param_0) {
            if (isDMDx64Target() && !global.value.params.isLP64)
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

        public  void visit(TypeDelegate _param_0) {
            if (isDMDx64Target() && !global.value.params.isLP64)
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

        public  void visit(TypeSArray t) {
            long sz = t.size(Loc.initial);
            if ((sz > 16L))
            {
                this.memory();
                return ;
            }
            long dim = t.dim.toInteger();
            Ref<Type> tn = ref(t.next.value);
            Ref<Long> tnsize = ref(tn.value.size());
            IntRef tnalignsize = ref(tn.value.alignsize());
            Function3<Integer,Integer,Integer,Type> getNthElement = new Function3<Integer,Integer,Integer,Type>(){
                public Type invoke(Integer n, IntRef offset, IntRef alignsize) {
                    IntRef n_ref = ref(n);
                    offset.value = 0;
                    alignsize.value = 0;
                    offset.value = (int)((long)n_ref.value * tnsize.value);
                    alignsize.value = tnalignsize.value;
                    return tn.value;
                }
            };
            this.aggregate(sz, (int)dim, getNthElement);
        }

        public  void visit(TypeStruct t) {
            Ref<TypeStruct> t_ref = ref(t);
            if (!t_ref.value.sym.isPOD())
            {
                this.memory();
                return ;
            }
            Function3<Integer,Integer,Integer,Type> getNthField = new Function3<Integer,Integer,Integer,Type>(){
                public Type invoke(Integer n, IntRef offset, IntRef alignsize) {
                    IntRef n_ref = ref(n);
                    offset.value = 0;
                    alignsize.value = 0;
                    Ref<VarDeclaration> field = ref(t_ref.value.sym.fields.get(n_ref.value));
                    offset.value = field.value.offset;
                    alignsize.value = field.value.type.alignsize();
                    return field.value.type;
                }
            };
            this.aggregate(t_ref.value.size(Loc.initial), t_ref.value.sym.fields.length, getNthField);
        }

        public  void aggregate(long sz, int nfields, Function3<Integer,Integer,Integer,Type> getFieldInfo) {
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
                        int foffset = 0;
                        int falignsize = 0;
                        Type ftype = getFieldInfo.invoke(n, foffset, falignsize);
                        TypeTuple tup = toArgTypes(ftype);
                        if (tup == null)
                        {
                            this.memory();
                            return ;
                        }
                        int dim = (tup.arguments.get()).length;
                        Type ft1 = null;
                        Type ft2 = null;
                        switch (dim)
                        {
                            case 2:
                                ft1 = (tup.arguments.get()).get(0).type;
                                ft2 = (tup.arguments.get()).get(1).type;
                                break;
                            case 1:
                                if ((foffset < 8))
                                {
                                    ft1 = (tup.arguments.get()).get(0).type;
                                }
                                else
                                {
                                    ft2 = (tup.arguments.get()).get(0).type;
                                }
                                break;
                            default:
                            this.memory();
                            return ;
                        }
                        if ((foffset & 7) != 0)
                        {
                            if ((foffset & falignsize - 1) != 0)
                            {
                                this.memory();
                                return ;
                            }
                            long fieldsz = ftype.size(Loc.initial);
                            Ref<Boolean> overflow = ref(false);
                            long nextOffset = addu((long)foffset, fieldsz, overflow);
                            assert(!overflow.value);
                            if ((foffset < 8) && (nextOffset > 8L))
                            {
                                this.memory();
                                return ;
                            }
                        }
                        assert((t1 != null) || (foffset == 0));
                        if (ft1 != null)
                        {
                            t1 = argtypemerge(t1, ft1, foffset);
                            if (t1 == null)
                            {
                                this.memory();
                                return ;
                            }
                        }
                        if (ft2 != null)
                        {
                            int off2 = ft1 != null ? 8 : foffset;
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
                        t1 = Type.tint32.value;
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
                if (global.value.params.isFreeBSD && (nfields == 1) && (sz == 4L) || (sz == 8L))
                {
                    int foffset = 0;
                    int falignsize = 0;
                    Type ftype = getFieldInfo.invoke(0, foffset, falignsize);
                    TypeTuple tup = toArgTypes(ftype);
                    if ((tup != null) && ((tup.arguments.get()).length == 1))
                    {
                        Type ft1 = (tup.arguments.get()).get(0).type;
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

        public  void visit(TypeEnum t) {
            t.toBasetype().accept(this);
        }

        public  void visit(TypeClass _param_0) {
            this.result = new TypeTuple(Type.tvoidptr);
        }


        public ToArgTypes() {}
    }

    public static boolean isDMDx64Target() {
        return global.value.params.is64bit;
    }

    public static TypeTuple toArgTypes(Type t) {
        // skipping duplicate class ToArgTypes
        ToArgTypes v = new ToArgTypes();
        t.accept(v);
        return v.result;
    }

}
