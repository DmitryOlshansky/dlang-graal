package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.ast_node.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.hdrgen.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.visitor.*;

public class init {


    public static class NeedInterpret 
    {
        public static final int INITnointerpret = 0;
        public static final int INITinterpret = 1;
    }


    public static class InitKind 
    {
        public static final byte void_ = (byte)0;
        public static final byte error = (byte)1;
        public static final byte struct_ = (byte)2;
        public static final byte array = (byte)3;
        public static final byte exp = (byte)4;
    }

    public static class Initializer extends ASTNode
    {
        public Ref<Loc> loc = ref(new Loc());
        public byte kind = 0;
        public  Initializer(Loc loc, byte kind) {
            super();
            this.loc.value = loc.copy();
            this.kind = kind;
        }

        public  BytePtr toChars() {
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                Ref<HdrGenState> hgs = ref(new HdrGenState());
                toCBuffer(this, ptr(buf), ptr(hgs));
                return buf.value.extractChars();
            }
            finally {
            }
        }

        public  ErrorInitializer isErrorInitializer() {
            return ((this.kind & 0xFF) == 1) ? ((ErrorInitializer)this) : null;
        }

        public  VoidInitializer isVoidInitializer() {
            return ((this.kind & 0xFF) == 0) ? ((VoidInitializer)this) : null;
        }

        public  StructInitializer isStructInitializer() {
            return ((this.kind & 0xFF) == 2) ? ((StructInitializer)this) : null;
        }

        public  ArrayInitializer isArrayInitializer() {
            return ((this.kind & 0xFF) == 3) ? ((ArrayInitializer)this) : null;
        }

        public  ExpInitializer isExpInitializer() {
            return ((this.kind & 0xFF) == 4) ? ((ExpInitializer)this) : null;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public Initializer() {}

        public Initializer copy() {
            Initializer that = new Initializer();
            that.loc = this.loc;
            that.kind = this.kind;
            return that;
        }
    }
    public static class VoidInitializer extends Initializer
    {
        public Ref<Type> type = ref(null);
        public  VoidInitializer(Loc loc) {
            super(loc, InitKind.void_);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public VoidInitializer() {}

        public VoidInitializer copy() {
            VoidInitializer that = new VoidInitializer();
            that.type = this.type;
            that.loc = this.loc;
            that.kind = this.kind;
            return that;
        }
    }
    public static class ErrorInitializer extends Initializer
    {
        public  ErrorInitializer() {
            super(Loc.initial.value, InitKind.error);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ErrorInitializer copy() {
            ErrorInitializer that = new ErrorInitializer();
            that.loc = this.loc;
            that.kind = this.kind;
            return that;
        }
    }
    public static class StructInitializer extends Initializer
    {
        public DArray<Identifier> field = new DArray<Identifier>();
        public DArray<Initializer> value = new DArray<Initializer>();
        public  StructInitializer(Loc loc) {
            super(loc, InitKind.struct_);
        }

        public  void addInit(Identifier field, Initializer value) {
            this.field.push(field);
            this.value.push(value);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public StructInitializer() {}

        public StructInitializer copy() {
            StructInitializer that = new StructInitializer();
            that.field = this.field;
            that.value = this.value;
            that.loc = this.loc;
            that.kind = this.kind;
            return that;
        }
    }
    public static class ArrayInitializer extends Initializer
    {
        public DArray<Expression> index = new DArray<Expression>();
        public DArray<Initializer> value = new DArray<Initializer>();
        public IntRef dim = ref(0);
        public Ref<Type> type = ref(null);
        public Ref<Boolean> sem = ref(false);
        public  ArrayInitializer(Loc loc) {
            super(loc, InitKind.array);
        }

        public  void addInit(Expression index, Initializer value) {
            this.index.push(index);
            this.value.push(value);
            this.dim.value = 0;
            this.type.value = null;
        }

        public  boolean isAssociativeArray() {
            {
                Slice<Expression> __r1493 = this.index.opSlice().copy();
                int __key1494 = 0;
                for (; (__key1494 < __r1493.getLength());__key1494 += 1) {
                    Expression idx = __r1493.get(__key1494);
                    if (idx != null)
                        return true;
                }
            }
            return false;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ArrayInitializer() {}

        public ArrayInitializer copy() {
            ArrayInitializer that = new ArrayInitializer();
            that.index = this.index;
            that.value = this.value;
            that.dim = this.dim;
            that.type = this.type;
            that.sem = this.sem;
            that.loc = this.loc;
            that.kind = this.kind;
            return that;
        }
    }
    public static class ExpInitializer extends Initializer
    {
        public Ref<Boolean> expandTuples = ref(false);
        public Ref<Expression> exp = ref(null);
        public  ExpInitializer(Loc loc, Expression exp) {
            super(loc, InitKind.exp);
            this.exp.value = exp;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ExpInitializer() {}

        public ExpInitializer copy() {
            ExpInitializer that = new ExpInitializer();
            that.expandTuples = this.expandTuples;
            that.exp = this.exp;
            that.loc = this.loc;
            that.kind = this.kind;
            return that;
        }
    }

    public static boolean hasNonConstPointers(Expression e) {
        Function1<Ptr<DArray<Expression>>,Boolean> checkArray = new Function1<Ptr<DArray<Expression>>,Boolean>(){
            public Boolean invoke(Ptr<DArray<Expression>> elems) {
                {
                    Ref<Slice<Expression>> __r1495 = ref((elems.get()).opSlice().copy());
                    IntRef __key1496 = ref(0);
                    for (; (__key1496.value < __r1495.value.getLength());__key1496.value += 1) {
                        Ref<Expression> e = ref(__r1495.value.get(__key1496.value));
                        if ((e.value != null) && hasNonConstPointers(e.value))
                            return true;
                    }
                }
                return false;
            }
        };
        if (((e.type.value.ty.value & 0xFF) == ENUMTY.Terror))
            return false;
        if (((e.op.value & 0xFF) == 13))
            return false;
        {
            StructLiteralExp se = e.isStructLiteralExp();
            if ((se) != null)
            {
                return checkArray.invoke(se.elements.value);
            }
        }
        {
            ArrayLiteralExp ae = e.isArrayLiteralExp();
            if ((ae) != null)
            {
                if (!ae.type.value.nextOf().hasPointers())
                    return false;
                return checkArray.invoke(ae.elements.value);
            }
        }
        {
            AssocArrayLiteralExp ae = e.isAssocArrayLiteralExp();
            if ((ae) != null)
            {
                if (ae.type.value.nextOf().hasPointers() && checkArray.invoke(ae.values.value))
                    return true;
                if (((TypeAArray)ae.type.value).index.value.hasPointers())
                    return checkArray.invoke(ae.keys.value);
                return false;
            }
        }
        {
            AddrExp ae = e.isAddrExp();
            if ((ae) != null)
            {
                {
                    StructLiteralExp se = ae.e1.value.isStructLiteralExp();
                    if ((se) != null)
                    {
                        if ((se.stageflags.value & 2) == 0)
                        {
                            int old = se.stageflags.value;
                            se.stageflags.value |= 2;
                            boolean ret = checkArray.invoke(se.elements.value);
                            se.stageflags.value = old;
                            return ret;
                        }
                        else
                        {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        if (((e.type.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((e.type.value.nextOf().ty.value & 0xFF) != ENUMTY.Tfunction))
        {
            if (((e.op.value & 0xFF) == 25))
                return false;
            if (((e.op.value & 0xFF) == 135))
                return false;
            if (((e.op.value & 0xFF) == 121))
                return false;
            return true;
        }
        return false;
    }

    public static Initializer syntaxCopy(Initializer inx) {
        Function1<StructInitializer,Initializer> copyStruct = new Function1<StructInitializer,Initializer>(){
            public Initializer invoke(StructInitializer vi) {
                Ref<StructInitializer> si = ref(new StructInitializer(vi.loc.value));
                assert((vi.field.length.value == vi.value.length.value));
                si.value.field.setDim(vi.field.length.value);
                si.value.value.setDim(vi.value.length.value);
                {
                    IntRef __key1497 = ref(0);
                    IntRef __limit1498 = ref(vi.field.length.value);
                    for (; (__key1497.value < __limit1498.value);__key1497.value += 1) {
                        IntRef i = ref(__key1497.value);
                        si.value.field.set(i.value, vi.field.get(i.value));
                        si.value.value.set(i.value, syntaxCopy(vi.value.get(i.value)));
                    }
                }
                return si.value;
            }
        };
        Function1<ArrayInitializer,Initializer> copyArray = new Function1<ArrayInitializer,Initializer>(){
            public Initializer invoke(ArrayInitializer vi) {
                Ref<ArrayInitializer> ai = ref(new ArrayInitializer(vi.loc.value));
                assert((vi.index.length.value == vi.value.length.value));
                ai.value.index.setDim(vi.index.length.value);
                ai.value.value.setDim(vi.value.length.value);
                {
                    IntRef __key1499 = ref(0);
                    IntRef __limit1500 = ref(vi.value.length.value);
                    for (; (__key1499.value < __limit1500.value);__key1499.value += 1) {
                        IntRef i = ref(__key1499.value);
                        ai.value.index.set(i.value, vi.index.get(i.value) != null ? vi.index.get(i.value).syntaxCopy() : null);
                        ai.value.value.set(i.value, syntaxCopy(vi.value.get(i.value)));
                    }
                }
                return ai.value;
            }
        };
        switch ((inx.kind & 0xFF))
        {
            case 0:
                return new VoidInitializer(inx.loc.value);
            case 1:
                return inx;
            case 2:
                return copyStruct.invoke((StructInitializer)inx);
            case 3:
                return copyArray.invoke((ArrayInitializer)inx);
            case 4:
                return new ExpInitializer(inx.loc.value, ((ExpInitializer)inx).exp.value.syntaxCopy());
            default:
            throw SwitchError.INSTANCE;
        }
    }

}
