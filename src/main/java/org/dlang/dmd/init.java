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
        public Loc loc = new Loc();
        public byte kind = 0;
        // Erasure: __ctor<Loc, byte>
        public  Initializer(Loc loc, byte kind) {
            super();
            this.loc.opAssign(loc.copy());
            this.kind = kind;
        }

        // Erasure: toChars<>
        public  BytePtr toChars() {
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                Ref<HdrGenState> hgs = ref(new HdrGenState());
                toCBuffer(this, buf.value, ptr(hgs));
                return buf.value.extractChars();
            }
            finally {
            }
        }

        // Erasure: isErrorInitializer<>
        public  ErrorInitializer isErrorInitializer() {
            return ((this.kind & 0xFF) == 1) ? ((ErrorInitializer)this) : null;
        }

        // Erasure: isVoidInitializer<>
        public  VoidInitializer isVoidInitializer() {
            return ((this.kind & 0xFF) == 0) ? ((VoidInitializer)this) : null;
        }

        // Erasure: isStructInitializer<>
        public  StructInitializer isStructInitializer() {
            return ((this.kind & 0xFF) == 2) ? ((StructInitializer)this) : null;
        }

        // Erasure: isArrayInitializer<>
        public  ArrayInitializer isArrayInitializer() {
            return ((this.kind & 0xFF) == 3) ? ((ArrayInitializer)this) : null;
        }

        // Erasure: isExpInitializer<>
        public  ExpInitializer isExpInitializer() {
            return ((this.kind & 0xFF) == 4) ? ((ExpInitializer)this) : null;
        }

        // Erasure: accept<Visitor>
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
        public Type type = null;
        // Erasure: __ctor<Loc>
        public  VoidInitializer(Loc loc) {
            super(loc, InitKind.void_);
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<>
        public  ErrorInitializer() {
            super(Loc.initial, InitKind.error);
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Loc>
        public  StructInitializer(Loc loc) {
            super(loc, InitKind.struct_);
        }

        // Erasure: addInit<Identifier, Initializer>
        public  void addInit(Identifier field, Initializer value) {
            this.field.push(field);
            this.value.push(value);
        }

        // Erasure: accept<Visitor>
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
        public int dim = 0;
        public Type type = null;
        public boolean sem = false;
        // Erasure: __ctor<Loc>
        public  ArrayInitializer(Loc loc) {
            super(loc, InitKind.array);
        }

        // Erasure: addInit<Expression, Initializer>
        public  void addInit(Expression index, Initializer value) {
            this.index.push(index);
            this.value.push(value);
            this.dim = 0;
            this.type = null;
        }

        // Erasure: isAssociativeArray<>
        public  boolean isAssociativeArray() {
            {
                Slice<Expression> __r1509 = this.index.opSlice().copy();
                int __key1510 = 0;
                for (; (__key1510 < __r1509.getLength());__key1510 += 1) {
                    Expression idx = __r1509.get(__key1510);
                    if (idx != null)
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        // Erasure: accept<Visitor>
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
        public boolean expandTuples = false;
        public Expression exp = null;
        // Erasure: __ctor<Loc, Expression>
        public  ExpInitializer(Loc loc, Expression exp) {
            super(loc, InitKind.exp);
            this.exp = exp;
        }

        // Erasure: accept<Visitor>
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

    // Erasure: hasNonConstPointers<Expression>
    public static boolean hasNonConstPointers(Expression e) {
        Function1<DArray<Expression>,Boolean> checkArray = new Function1<DArray<Expression>,Boolean>() {
            public Boolean invoke(DArray<Expression> elems) {
             {
                {
                    Slice<Expression> __r1511 = (elems).opSlice().copy();
                    Ref<Integer> __key1512 = ref(0);
                    for (; (__key1512.value < __r1511.getLength());__key1512.value += 1) {
                        Expression e = __r1511.get(__key1512.value);
                        if ((e != null) && hasNonConstPointers(e))
                        {
                            return true;
                        }
                    }
                }
                return false;
            }}

        };
        if (((e.type.value.ty & 0xFF) == ENUMTY.Terror))
        {
            return false;
        }
        if (((e.op & 0xFF) == 13))
        {
            return false;
        }
        {
            StructLiteralExp se = e.isStructLiteralExp();
            if ((se) != null)
            {
                return checkArray.invoke(se.elements);
            }
        }
        {
            ArrayLiteralExp ae = e.isArrayLiteralExp();
            if ((ae) != null)
            {
                if (!ae.type.value.nextOf().hasPointers())
                {
                    return false;
                }
                return checkArray.invoke(ae.elements);
            }
        }
        {
            AssocArrayLiteralExp ae = e.isAssocArrayLiteralExp();
            if ((ae) != null)
            {
                if (ae.type.value.nextOf().hasPointers() && checkArray.invoke(ae.values))
                {
                    return true;
                }
                if ((((TypeAArray)ae.type.value)).index.hasPointers())
                {
                    return checkArray.invoke(ae.keys);
                }
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
                        if ((se.stageflags & 2) == 0)
                        {
                            int old = se.stageflags;
                            se.stageflags |= 2;
                            boolean ret = checkArray.invoke(se.elements);
                            se.stageflags = old;
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
        if (((e.type.value.ty & 0xFF) == ENUMTY.Tpointer) && ((e.type.value.nextOf().ty & 0xFF) != ENUMTY.Tfunction))
        {
            if (((e.op & 0xFF) == 25))
            {
                return false;
            }
            if (((e.op & 0xFF) == 135))
            {
                return false;
            }
            if (((e.op & 0xFF) == 121))
            {
                return false;
            }
            return true;
        }
        return false;
    }

    // Erasure: syntaxCopy<Initializer>
    public static Initializer syntaxCopy(Initializer inx) {
        Function1<StructInitializer,Initializer> copyStruct = new Function1<StructInitializer,Initializer>() {
            public Initializer invoke(StructInitializer vi) {
             {
                StructInitializer si = new StructInitializer(vi.loc);
                assert((vi.field.length == vi.value.length));
                si.field.setDim(vi.field.length);
                si.value.setDim(vi.value.length);
                {
                    Ref<Integer> __key1513 = ref(0);
                    int __limit1514 = vi.field.length;
                    for (; (__key1513.value < __limit1514);__key1513.value += 1) {
                        int i = __key1513.value;
                        si.field.set(i, vi.field.get(i));
                        si.value.set(i, syntaxCopy(vi.value.get(i)));
                    }
                }
                return si;
            }}

        };
        Function1<ArrayInitializer,Initializer> copyArray = new Function1<ArrayInitializer,Initializer>() {
            public Initializer invoke(ArrayInitializer vi) {
             {
                ArrayInitializer ai = new ArrayInitializer(vi.loc);
                assert((vi.index.length == vi.value.length));
                ai.index.setDim(vi.index.length);
                ai.value.setDim(vi.value.length);
                {
                    Ref<Integer> __key1515 = ref(0);
                    int __limit1516 = vi.value.length;
                    for (; (__key1515.value < __limit1516);__key1515.value += 1) {
                        int i = __key1515.value;
                        ai.index.set(i, vi.index.get(i) != null ? vi.index.get(i).syntaxCopy() : null);
                        ai.value.set(i, syntaxCopy(vi.value.get(i)));
                    }
                }
                return ai;
            }}

        };
        switch ((inx.kind & 0xFF))
        {
            case 0:
                return new VoidInitializer(inx.loc);
            case 1:
                return inx;
            case 2:
                return copyStruct.invoke(((StructInitializer)inx));
            case 3:
                return copyArray.invoke(((ArrayInitializer)inx));
            case 4:
                return new ExpInitializer(inx.loc, (((ExpInitializer)inx)).exp.syntaxCopy());
            default:
            throw SwitchError.INSTANCE;
        }
    }

}
