package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.ast_node.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.hdrgen.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
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
        public byte kind;
        public  Initializer(Loc loc, byte kind) {
            super();
            this.loc = loc.copy();
            this.kind = kind;
        }

        public  BytePtr toChars() {
            OutBuffer buf = new OutBuffer();
            try {
                HdrGenState hgs = new HdrGenState();
                toCBuffer(this, buf, hgs);
                return buf.extractChars();
            }
            finally {
            }
        }

        public  ErrorInitializer isErrorInitializer() {
            return (this.kind & 0xFF) == 1 ? (ErrorInitializer)this : null;
        }

        public  VoidInitializer isVoidInitializer() {
            return (this.kind & 0xFF) == 0 ? (VoidInitializer)this : null;
        }

        public  StructInitializer isStructInitializer() {
            return (this.kind & 0xFF) == 2 ? (StructInitializer)this : null;
        }

        public  ArrayInitializer isArrayInitializer() {
            return (this.kind & 0xFF) == 3 ? (ArrayInitializer)this : null;
        }

        public  ExpInitializer isExpInitializer() {
            return (this.kind & 0xFF) == 4 ? (ExpInitializer)this : null;
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
        public Type type;
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
            super(Loc.initial, InitKind.error);
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
        public int dim;
        public Type type;
        public boolean sem;
        public  ArrayInitializer(Loc loc) {
            super(loc, InitKind.array);
        }

        public  void addInit(Expression index, Initializer value) {
            this.index.push(index);
            this.value.push(value);
            this.dim = 0;
            this.type = null;
        }

        public  boolean isAssociativeArray() {
            {
                Slice<Expression> __r1411 = this.index.opSlice().copy();
                int __key1412 = 0;
                for (; __key1412 < __r1411.getLength();__key1412 += 1) {
                    Expression idx = __r1411.get(__key1412);
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
        public boolean expandTuples;
        public Expression exp;
        public  ExpInitializer(Loc loc, Expression exp) {
            super(loc, InitKind.exp);
            this.exp = exp;
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
        Function1<DArray<Expression>,Boolean> checkArray = new Function1<DArray<Expression>,Boolean>(){
            public Boolean invoke(DArray<Expression> elems){
                {
                    Slice<Expression> __r1413 = (elems).opSlice().copy();
                    int __key1414 = 0;
                    for (; __key1414 < __r1413.getLength();__key1414 += 1) {
                        Expression e = __r1413.get(__key1414);
                        if ((e != null && hasNonConstPointers(e)))
                            return true;
                    }
                }
                return false;
            }
        };
        if ((e.type.ty & 0xFF) == ENUMTY.Terror)
            return false;
        if ((e.op & 0xFF) == 13)
            return false;
        {
            StructLiteralExp se = e.isStructLiteralExp();
            if (se != null)
            {
                return checkArray.invoke(se.elements);
            }
        }
        {
            ArrayLiteralExp ae = e.isArrayLiteralExp();
            if (ae != null)
            {
                if (!(ae.type.nextOf().hasPointers()))
                    return false;
                return checkArray.invoke(ae.elements);
            }
        }
        {
            AssocArrayLiteralExp ae = e.isAssocArrayLiteralExp();
            if (ae != null)
            {
                if ((ae.type.nextOf().hasPointers() && checkArray.invoke(ae.values)))
                    return true;
                if (((TypeAArray)ae.type).index.hasPointers())
                    return checkArray.invoke(ae.keys);
                return false;
            }
        }
        {
            AddrExp ae = e.isAddrExp();
            if (ae != null)
            {
                {
                    StructLiteralExp se = ae.e1.isStructLiteralExp();
                    if (se != null)
                    {
                        if (!((se.stageflags & 2) != 0))
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
        if (((e.type.ty & 0xFF) == ENUMTY.Tpointer && (e.type.nextOf().ty & 0xFF) != ENUMTY.Tfunction))
        {
            if ((e.op & 0xFF) == 25)
                return false;
            if ((e.op & 0xFF) == 135)
                return false;
            if ((e.op & 0xFF) == 121)
                return false;
            return true;
        }
        return false;
    }

    public static Initializer syntaxCopy(Initializer inx) {
        Function1<StructInitializer,Initializer> copyStruct = new Function1<StructInitializer,Initializer>(){
            public Initializer invoke(StructInitializer vi){
                StructInitializer si = new StructInitializer(vi.loc);
                assert(vi.field.length == vi.value.length);
                si.field.setDim(vi.field.length);
                si.value.setDim(vi.value.length);
                {
                    int __key1415 = 0;
                    int __limit1416 = vi.field.length;
                    for (; __key1415 < __limit1416;__key1415 += 1) {
                        int i = __key1415;
                        si.field.set(i, vi.field.get(i));
                        si.value.set(i, syntaxCopy(vi.value.get(i)));
                    }
                }
                return si;
            }
        };
        Function1<ArrayInitializer,Initializer> copyArray = new Function1<ArrayInitializer,Initializer>(){
            public Initializer invoke(ArrayInitializer vi){
                ArrayInitializer ai = new ArrayInitializer(vi.loc);
                assert(vi.index.length == vi.value.length);
                ai.index.setDim(vi.index.length);
                ai.value.setDim(vi.value.length);
                {
                    int __key1417 = 0;
                    int __limit1418 = vi.value.length;
                    for (; __key1417 < __limit1418;__key1417 += 1) {
                        int i = __key1417;
                        ai.index.set(i, vi.index.get(i) != null ? vi.index.get(i).syntaxCopy() : null);
                        ai.value.set(i, syntaxCopy(vi.value.get(i)));
                    }
                }
                return ai;
            }
        };
        switch ((inx.kind & 0xFF))
        {
            case 0:
                return new VoidInitializer(inx.loc);
            case 1:
                return inx;
            case 2:
                return copyStruct.invoke((StructInitializer)inx);
            case 3:
                return copyArray.invoke((ArrayInitializer)inx);
            case 4:
                return new ExpInitializer(inx.loc, ((ExpInitializer)inx).exp.syntaxCopy());
            default:
            throw SwitchError.INSTANCE;
        }
    }

}
