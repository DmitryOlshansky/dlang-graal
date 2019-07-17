package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.constfold.*;
import static org.dlang.dmd.ctfeexpr.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.sideeffect.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.visitor.*;

public class optimize {
    private static class OptimizeVisitor extends Visitor
    {
        private Expression ret = null;
        private int result = 0;
        private boolean keepLvalue = false;
        // Erasure: __ctor<Expression, int, boolean>
        public  OptimizeVisitor(Expression e, int result, boolean keepLvalue) {
            this.ret = e;
            this.result = result;
            this.keepLvalue = keepLvalue;
        }

        // Erasure: error<>
        public  void error() {
            this.ret = new ErrorExp();
        }

        // Erasure: expOptimize<Expression, int, boolean>
        public  boolean expOptimize(Ref<Expression> e, int flags, boolean keepLvalue) {
            if (e.value == null)
            {
                return false;
            }
            Expression ex = Expression_optimize(e.value, flags, keepLvalue);
            if (((ex.op & 0xFF) == 127))
            {
                this.ret = ex;
                return true;
            }
            else
            {
                e.value = ex;
                return false;
            }
        }

        // defaulted all parameters starting with #3
        public  boolean expOptimize(Ref<Expression> e, int flags) {
            return expOptimize(e, flags, false);
        }

        // Erasure: unaOptimize<UnaExp, int>
        public  boolean unaOptimize(UnaExp e, int flags) {
            return this.expOptimize(e1, flags, false);
        }

        // Erasure: binOptimize<BinExp, int>
        public  boolean binOptimize(BinExp e, int flags) {
            this.expOptimize(e1, flags, false);
            this.expOptimize(e2, flags, false);
            return (this.ret.op & 0xFF) == 127;
        }

        // Erasure: visit<Expression>
        public  void visit(Expression e) {
        }

        // Erasure: visit<VarExp>
        public  void visit(VarExp e) {
            if (this.keepLvalue)
            {
                VarDeclaration v = e.var.isVarDeclaration();
                if ((v != null) && ((v.storage_class & 8388608L) == 0))
                {
                    return ;
                }
            }
            this.ret = fromConstInitializer(this.result, e);
        }

        // Erasure: visit<TupleExp>
        public  void visit(TupleExp e) {
            this.expOptimize(e0, 0, false);
            {
                int i = 0;
                for (; (i < (e.exps).length);i++){
                    this.expOptimize((e.exps).get(i), 0, false);
                }
            }
        }

        // Erasure: visit<ArrayLiteralExp>
        public  void visit(ArrayLiteralExp e) {
            if (e.elements != null)
            {
                this.expOptimize(basis, this.result & 1, false);
                {
                    int i = 0;
                    for (; (i < (e.elements).length);i++){
                        this.expOptimize((e.elements).get(i), this.result & 1, false);
                    }
                }
            }
        }

        // Erasure: visit<AssocArrayLiteralExp>
        public  void visit(AssocArrayLiteralExp e) {
            assert(((e.keys).length == (e.values).length));
            {
                int i = 0;
                for (; (i < (e.keys).length);i++){
                    this.expOptimize((e.keys).get(i), this.result & 1, false);
                    this.expOptimize((e.values).get(i), this.result & 1, false);
                }
            }
        }

        // Erasure: visit<StructLiteralExp>
        public  void visit(StructLiteralExp e) {
            if ((e.stageflags & 4) != 0)
            {
                return ;
            }
            int old = e.stageflags;
            e.stageflags |= 4;
            if (e.elements != null)
            {
                {
                    int i = 0;
                    for (; (i < (e.elements).length);i++){
                        this.expOptimize((e.elements).get(i), this.result & 1, false);
                    }
                }
            }
            e.stageflags = old;
        }

        // Erasure: visit<UnaExp>
        public  void visit(UnaExp e) {
            if (this.unaOptimize(e, this.result))
            {
                return ;
            }
        }

        // Erasure: visit<NegExp>
        public  void visit(NegExp e) {
            if (this.unaOptimize(e, this.result))
            {
                return ;
            }
            if ((e.e1.value.isConst() == 1))
            {
                this.ret = Neg(e.type.value, e.e1.value).copy();
            }
        }

        // Erasure: visit<ComExp>
        public  void visit(ComExp e) {
            if (this.unaOptimize(e, this.result))
            {
                return ;
            }
            if ((e.e1.value.isConst() == 1))
            {
                this.ret = Com(e.type.value, e.e1.value).copy();
            }
        }

        // Erasure: visit<NotExp>
        public  void visit(NotExp e) {
            if (this.unaOptimize(e, this.result))
            {
                return ;
            }
            if ((e.e1.value.isConst() == 1))
            {
                this.ret = Not(e.type.value, e.e1.value).copy();
            }
        }

        // Erasure: visit<SymOffExp>
        public  void visit(SymOffExp e) {
            assert(e.var != null);
        }

        // Erasure: visit<AddrExp>
        public  void visit(AddrExp e) {
            if (((e.e1.value.op & 0xFF) == 99))
            {
                CommaExp ce = ((CommaExp)e.e1.value);
                AddrExp ae = new AddrExp(e.loc, ce.e2.value, e.type.value);
                this.ret = new CommaExp(ce.loc, ce.e1.value, ae, true);
                this.ret.type.value = e.type.value;
                return ;
            }
            if (this.expOptimize(e1, this.result, true))
            {
                return ;
            }
            if (((e.e1.value.op & 0xFF) == 24))
            {
                Expression ex = (((PtrExp)e.e1.value)).e1.value;
                if (e.type.value.equals(ex.type.value))
                {
                    this.ret = ex;
                }
                else if (e.type.value.toBasetype().equivalent(ex.type.value.toBasetype()))
                {
                    this.ret = ex.copy();
                    this.ret.type.value = e.type.value;
                }
                return ;
            }
            if (((e.e1.value.op & 0xFF) == 26))
            {
                VarExp ve = ((VarExp)e.e1.value);
                if (!ve.var.isOut() && !ve.var.isRef() && !ve.var.isImportedSymbol())
                {
                    this.ret = new SymOffExp(e.loc, ve.var, 0L, ve.hasOverloads);
                    this.ret.type.value = e.type.value;
                    return ;
                }
            }
            if (((e.e1.value.op & 0xFF) == 62))
            {
                IndexExp ae = ((IndexExp)e.e1.value);
                if (((ae.e2.value.op & 0xFF) == 135) && ((ae.e1.value.op & 0xFF) == 26))
                {
                    long index = (long)ae.e2.value.toInteger();
                    VarExp ve = ((VarExp)ae.e1.value);
                    if (((ve.type.value.ty & 0xFF) == ENUMTY.Tsarray) && !ve.var.isImportedSymbol())
                    {
                        TypeSArray ts = ((TypeSArray)ve.type.value);
                        long dim = (long)ts.dim.toInteger();
                        if ((index < 0L) || (index >= dim))
                        {
                            e.error(new BytePtr("array index %lld is out of bounds `[0..%lld]`"), index, dim);
                            this.error();
                            return ;
                        }
                        Ref<Boolean> overflow = ref(false);
                        long offset = mulu((long)index, ts.nextOf().size(e.loc), overflow);
                        if (overflow.value)
                        {
                            e.error(new BytePtr("array offset overflow"));
                            this.error();
                            return ;
                        }
                        this.ret = new SymOffExp(e.loc, ve.var, offset, true);
                        this.ret.type.value = e.type.value;
                        return ;
                    }
                }
            }
        }

        // Erasure: visit<PtrExp>
        public  void visit(PtrExp e) {
            if (this.expOptimize(e1, this.result, false))
            {
                return ;
            }
            if (((e.e1.value.op & 0xFF) == 19))
            {
                Expression ex = (((AddrExp)e.e1.value)).e1.value;
                if (e.type.value.equals(ex.type.value))
                {
                    this.ret = ex;
                }
                else if (e.type.value.toBasetype().equivalent(ex.type.value.toBasetype()))
                {
                    this.ret = ex.copy();
                    this.ret.type.value = e.type.value;
                }
            }
            if (this.keepLvalue)
            {
                return ;
            }
            if (((e.e1.value.op & 0xFF) == 74))
            {
                Expression ex = Ptr(e.type.value, e.e1.value).copy();
                if (!CTFEExp.isCantExp(ex))
                {
                    this.ret = ex;
                    return ;
                }
            }
            if (((e.e1.value.op & 0xFF) == 25))
            {
                SymOffExp se = ((SymOffExp)e.e1.value);
                VarDeclaration v = se.var.isVarDeclaration();
                Expression ex = expandVar(this.result, v);
                if ((ex != null) && ((ex.op & 0xFF) == 49))
                {
                    StructLiteralExp sle = ((StructLiteralExp)ex);
                    ex = sle.getField(e.type.value, (int)se.offset);
                    if ((ex != null) && !CTFEExp.isCantExp(ex))
                    {
                        this.ret = ex;
                        return ;
                    }
                }
            }
        }

        // Erasure: visit<DotVarExp>
        public  void visit(DotVarExp e) {
            if (this.expOptimize(e1, this.result, false))
            {
                return ;
            }
            if (this.keepLvalue)
            {
                return ;
            }
            Expression ex = e.e1.value;
            if (((ex.op & 0xFF) == 26))
            {
                VarExp ve = ((VarExp)ex);
                VarDeclaration v = ve.var.isVarDeclaration();
                ex = expandVar(this.result, v);
            }
            if ((ex != null) && ((ex.op & 0xFF) == 49))
            {
                StructLiteralExp sle = ((StructLiteralExp)ex);
                VarDeclaration vf = e.var.isVarDeclaration();
                if ((vf != null) && !vf.overlapped)
                {
                    ex = sle.getField(e.type.value, vf.offset);
                    if ((ex != null) && !CTFEExp.isCantExp(ex))
                    {
                        this.ret = ex;
                        return ;
                    }
                }
            }
        }

        // Erasure: visit<NewExp>
        public  void visit(NewExp e) {
            this.expOptimize(thisexp, 0, false);
            if (e.newargs != null)
            {
                {
                    int i = 0;
                    for (; (i < (e.newargs).length);i++){
                        this.expOptimize((e.newargs).get(i), 0, false);
                    }
                }
            }
            if (e.arguments != null)
            {
                {
                    int i = 0;
                    for (; (i < (e.arguments).length);i++){
                        this.expOptimize((e.arguments).get(i), 0, false);
                    }
                }
            }
        }

        // Erasure: visit<CallExp>
        public  void visit(CallExp e) {
            if (this.expOptimize(e1, this.result, false))
            {
                return ;
            }
            if (e.arguments != null)
            {
                Type t1 = e.e1.value.type.value.toBasetype();
                if (((t1.ty & 0xFF) == ENUMTY.Tdelegate))
                {
                    t1 = t1.nextOf();
                }
                assert(((t1.ty & 0xFF) == ENUMTY.Tfunction));
                TypeFunction tf = ((TypeFunction)t1);
                {
                    int i = 0;
                    for (; (i < (e.arguments).length);i++){
                        Parameter p = tf.parameterList.get(i);
                        boolean keep = (p != null) && ((p.storageClass & 2101248L) != 0L);
                        this.expOptimize((e.arguments).get(i), 0, keep);
                    }
                }
            }
        }

        // Erasure: visit<CastExp>
        public  void visit(CastExp e) {
            assert(e.type.value != null);
            byte op1 = e.e1.value.op;
            Expression e1old = e.e1.value;
            if (this.expOptimize(e1, this.result, false))
            {
                return ;
            }
            e.e1.value = fromConstInitializer(this.result, e.e1.value);
            if ((pequals(e.e1.value, e1old)) && ((e.e1.value.op & 0xFF) == 47) && ((e.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tpointer) && ((e.e1.value.type.value.toBasetype().ty & 0xFF) != ENUMTY.Tsarray))
            {
                return ;
            }
            if (((e.e1.value.op & 0xFF) == 121) || ((e.e1.value.op & 0xFF) == 47) && ((e.type.value.ty & 0xFF) == ENUMTY.Tpointer) || ((e.type.value.ty & 0xFF) == ENUMTY.Tarray))
            {
                long esz = e.type.value.nextOf().size(e.loc);
                long e1sz = e.e1.value.type.value.toBasetype().nextOf().size(e.e1.value.loc);
                if ((esz == -1L) || (e1sz == -1L))
                {
                    this.error();
                    return ;
                }
                if ((e1sz == esz))
                {
                    if (((e.type.value.nextOf().ty & 0xFF) == ENUMTY.Tvoid))
                    {
                        return ;
                    }
                    this.ret = e.e1.value.castTo(null, e.type.value);
                    return ;
                }
            }
            if (((e.e1.value.op & 0xFF) == 49) && (e.e1.value.type.value.implicitConvTo(e.type.value) >= MATCH.constant))
            {
            /*L1:*/
                this.ret = (pequals(e1old, e.e1.value)) ? e.e1.value.copy() : e.e1.value;
                this.ret.type.value = e.type.value;
                return ;
            }
            if (((op1 & 0xFF) != 47) && ((e.e1.value.op & 0xFF) == 47))
            {
                this.ret = e.e1.value.castTo(null, e.to);
                return ;
            }
            if (((e.e1.value.op & 0xFF) == 13) && ((e.type.value.ty & 0xFF) == ENUMTY.Tpointer) || ((e.type.value.ty & 0xFF) == ENUMTY.Tclass) || ((e.type.value.ty & 0xFF) == ENUMTY.Tarray))
            {
                /*goto L1*/throw Dispatch0.INSTANCE;
            }
            if (((e.type.value.ty & 0xFF) == ENUMTY.Tclass) && ((e.e1.value.type.value.ty & 0xFF) == ENUMTY.Tclass))
            {
                ClassDeclaration cdfrom = e.e1.value.type.value.isClassHandle();
                ClassDeclaration cdto = e.type.value.isClassHandle();
                if ((pequals(cdto, ClassDeclaration.object)) && (cdfrom.isInterfaceDeclaration() == null))
                {
                    /*goto L1*/throw Dispatch0.INSTANCE;
                }
                cdfrom.size(e.loc);
                assert((cdfrom.sizeok == Sizeok.done));
                assert((cdto.sizeok == Sizeok.done) || !cdto.isBaseOf(cdfrom, null));
                Ref<Integer> offset = ref(0);
                if (cdto.isBaseOf(cdfrom, ptr(offset)) && (offset.value == 0))
                {
                    /*goto L1*/throw Dispatch0.INSTANCE;
                }
            }
            if (e.to.mutableOf().constOf().equals(e.e1.value.type.value.mutableOf().constOf()))
            {
                /*goto L1*/throw Dispatch0.INSTANCE;
            }
            if (e.e1.value.isConst() != 0)
            {
                if (((e.e1.value.op & 0xFF) == 25))
                {
                    if (((e.type.value.toBasetype().ty & 0xFF) != ENUMTY.Tsarray))
                    {
                        long esz = e.type.value.size(e.loc);
                        long e1sz = e.e1.value.type.value.size(e.e1.value.loc);
                        if ((esz == -1L) || (e1sz == -1L))
                        {
                            this.error();
                            return ;
                        }
                        if ((esz == e1sz))
                        {
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        }
                    }
                    return ;
                }
                if (((e.to.toBasetype().ty & 0xFF) != ENUMTY.Tvoid))
                {
                    if (e.e1.value.type.value.equals(e.type.value) && e.type.value.equals(e.to))
                    {
                        this.ret = e.e1.value;
                    }
                    else
                    {
                        this.ret = Cast(e.loc, e.type.value, e.to, e.e1.value).copy();
                    }
                }
            }
        }

        // Erasure: visit<BinExp>
        public  void visit(BinExp e) {
            boolean e2only = ((e.op & 0xFF) == 95) || ((e.op & 0xFF) == 96);
            if (e2only ? this.expOptimize(e2, this.result, false) : this.binOptimize(e, this.result))
            {
                return ;
            }
            if (((e.op & 0xFF) == 66) || ((e.op & 0xFF) == 67) || ((e.op & 0xFF) == 69))
            {
                if ((e.e2.value.isConst() == 1))
                {
                    long i2 = (long)e.e2.value.toInteger();
                    long sz = e.e1.value.type.value.size(e.e1.value.loc);
                    assert((sz != -1L));
                    sz *= 8L;
                    if ((i2 < 0L) || ((long)i2 >= sz))
                    {
                        e.error(new BytePtr("shift assign by %lld is outside the range `0..%llu`"), i2, sz - 1L);
                        this.error();
                        return ;
                    }
                }
            }
        }

        // Erasure: visit<AddExp>
        public  void visit(AddExp e) {
            if (this.binOptimize(e, this.result))
            {
                return ;
            }
            if ((e.e1.value.isConst() != 0) && (e.e2.value.isConst() != 0))
            {
                if (((e.e1.value.op & 0xFF) == 25) && ((e.e2.value.op & 0xFF) == 25))
                {
                    return ;
                }
                this.ret = Add(e.loc, e.type.value, e.e1.value, e.e2.value).copy();
            }
        }

        // Erasure: visit<MinExp>
        public  void visit(MinExp e) {
            if (this.binOptimize(e, this.result))
            {
                return ;
            }
            if ((e.e1.value.isConst() != 0) && (e.e2.value.isConst() != 0))
            {
                if (((e.e2.value.op & 0xFF) == 25))
                {
                    return ;
                }
                this.ret = Min(e.loc, e.type.value, e.e1.value, e.e2.value).copy();
            }
        }

        // Erasure: visit<MulExp>
        public  void visit(MulExp e) {
            if (this.binOptimize(e, this.result))
            {
                return ;
            }
            if ((e.e1.value.isConst() == 1) && (e.e2.value.isConst() == 1))
            {
                this.ret = Mul(e.loc, e.type.value, e.e1.value, e.e2.value).copy();
            }
        }

        // Erasure: visit<DivExp>
        public  void visit(DivExp e) {
            if (this.binOptimize(e, this.result))
            {
                return ;
            }
            if ((e.e1.value.isConst() == 1) && (e.e2.value.isConst() == 1))
            {
                this.ret = Div(e.loc, e.type.value, e.e1.value, e.e2.value).copy();
            }
        }

        // Erasure: visit<ModExp>
        public  void visit(ModExp e) {
            if (this.binOptimize(e, this.result))
            {
                return ;
            }
            if ((e.e1.value.isConst() == 1) && (e.e2.value.isConst() == 1))
            {
                this.ret = Mod(e.loc, e.type.value, e.e1.value, e.e2.value).copy();
            }
        }

        // Erasure: shift_optimize<BinExp, Ptr>
        public  void shift_optimize(BinExp e, Function4<Ref<Loc>,Type,Expression,Expression,UnionExp> shift) {
            if (this.binOptimize(e, this.result))
            {
                return ;
            }
            if ((e.e2.value.isConst() == 1))
            {
                long i2 = (long)e.e2.value.toInteger();
                long sz = e.e1.value.type.value.size(e.e1.value.loc);
                assert((sz != -1L));
                sz *= 8L;
                if ((i2 < 0L) || ((long)i2 >= sz))
                {
                    e.error(new BytePtr("shift by %lld is outside the range `0..%llu`"), i2, sz - 1L);
                    this.error();
                    return ;
                }
                if ((e.e1.value.isConst() == 1))
                {
                    this.ret = (shift).invoke(e.loc, e.type.value, e.e1.value, e.e2.value).copy();
                }
            }
        }

        // Erasure: visit<ShlExp>
        public  void visit(ShlExp e) {
            this.shift_optimize(e, optimize::Shl);
        }

        // Erasure: visit<ShrExp>
        public  void visit(ShrExp e) {
            this.shift_optimize(e, optimize::Shr);
        }

        // Erasure: visit<UshrExp>
        public  void visit(UshrExp e) {
            this.shift_optimize(e, optimize::Ushr);
        }

        // Erasure: visit<AndExp>
        public  void visit(AndExp e) {
            if (this.binOptimize(e, this.result))
            {
                return ;
            }
            if ((e.e1.value.isConst() == 1) && (e.e2.value.isConst() == 1))
            {
                this.ret = And(e.loc, e.type.value, e.e1.value, e.e2.value).copy();
            }
        }

        // Erasure: visit<OrExp>
        public  void visit(OrExp e) {
            if (this.binOptimize(e, this.result))
            {
                return ;
            }
            if ((e.e1.value.isConst() == 1) && (e.e2.value.isConst() == 1))
            {
                this.ret = Or(e.loc, e.type.value, e.e1.value, e.e2.value).copy();
            }
        }

        // Erasure: visit<XorExp>
        public  void visit(XorExp e) {
            if (this.binOptimize(e, this.result))
            {
                return ;
            }
            if ((e.e1.value.isConst() == 1) && (e.e2.value.isConst() == 1))
            {
                this.ret = Xor(e.loc, e.type.value, e.e1.value, e.e2.value).copy();
            }
        }

        // Erasure: visit<PowExp>
        public  void visit(PowExp e) {
            if (this.binOptimize(e, this.result))
            {
                return ;
            }
            if (((e.e1.value.op & 0xFF) == 135) && (e.e1.value.toInteger() == 1L) || ((e.e1.value.op & 0xFF) == 140) && (e.e1.value.toReal() == CTFloat.one))
            {
                this.ret = new CommaExp(e.loc, e.e2.value, e.e1.value, true);
                this.ret.type.value = e.type.value;
                return ;
            }
            if (e.e2.value.type.value.isintegral() && ((e.e1.value.op & 0xFF) == 135) && ((long)e.e1.value.toInteger() == -1L))
            {
                this.ret = new AndExp(e.loc, e.e2.value, new IntegerExp(e.loc, 1L, e.e2.value.type.value));
                this.ret.type.value = e.e2.value.type.value;
                this.ret = new CondExp(e.loc, this.ret, new IntegerExp(e.loc, -1L, e.type.value), new IntegerExp(e.loc, 1L, e.type.value));
                this.ret.type.value = e.type.value;
                return ;
            }
            if (((e.e2.value.op & 0xFF) == 135) && (e.e2.value.toInteger() == 0L) || ((e.e2.value.op & 0xFF) == 140) && (e.e2.value.toReal() == CTFloat.zero))
            {
                if (e.e1.value.type.value.isintegral())
                {
                    this.ret = new IntegerExp(e.loc, 1L, e.e1.value.type.value);
                }
                else
                {
                    this.ret = new RealExp(e.loc, CTFloat.one, e.e1.value.type.value);
                }
                this.ret = new CommaExp(e.loc, e.e1.value, this.ret, true);
                this.ret.type.value = e.type.value;
                return ;
            }
            if (((e.e2.value.op & 0xFF) == 135) && (e.e2.value.toInteger() == 1L) || ((e.e2.value.op & 0xFF) == 140) && (e.e2.value.toReal() == CTFloat.one))
            {
                this.ret = e.e1.value;
                return ;
            }
            if (((e.e2.value.op & 0xFF) == 140) && (e.e2.value.toReal() == CTFloat.minusone))
            {
                this.ret = new DivExp(e.loc, new RealExp(e.loc, CTFloat.one, e.e2.value.type.value), e.e1.value);
                this.ret.type.value = e.type.value;
                return ;
            }
            if (e.e1.value.type.value.isintegral() && ((e.e2.value.op & 0xFF) == 135) && ((long)e.e2.value.toInteger() < 0L))
            {
                e.error(new BytePtr("cannot raise `%s` to a negative integer power. Did you mean `(cast(real)%s)^^%s` ?"), e.e1.value.type.value.toBasetype().toChars(), e.e1.value.toChars(), e.e2.value.toChars());
                this.error();
                return ;
            }
            if (((e.e2.value.op & 0xFF) == 140))
            {
                if ((e.e2.value.toReal() == (double)(long)e.e2.value.toReal()))
                {
                    e.e2.value = new IntegerExp(e.loc, e.e2.value.toInteger(), Type.tint64);
                }
            }
            if ((e.e1.value.isConst() == 1) && (e.e2.value.isConst() == 1))
            {
                Expression ex = Pow(e.loc, e.type.value, e.e1.value, e.e2.value).copy();
                if (!CTFEExp.isCantExp(ex))
                {
                    this.ret = ex;
                    return ;
                }
            }
            if (((e.e1.value.op & 0xFF) == 135) && (e.e1.value.toInteger() > 0L) && ((e.e1.value.toInteger() - 1L & e.e1.value.toInteger()) == 0) && e.e2.value.type.value.isintegral() && e.e2.value.type.value.isunsigned())
            {
                long i = e.e1.value.toInteger();
                long mul = 1L;
                for (; ((i >>= 1) > 1L);) {
                    mul++;
                }
                Expression shift = new MulExp(e.loc, e.e2.value, new IntegerExp(e.loc, mul, e.e2.value.type.value));
                shift.type.value = e.e2.value.type.value;
                shift = shift.castTo(null, Type.tshiftcnt);
                this.ret = new ShlExp(e.loc, new IntegerExp(e.loc, 1L, e.e1.value.type.value), shift);
                this.ret.type.value = e.type.value;
                return ;
            }
        }

        // Erasure: visit<CommaExp>
        public  void visit(CommaExp e) {
            this.expOptimize(e1, 0, false);
            this.expOptimize(e2, this.result, this.keepLvalue);
            if (((this.ret.op & 0xFF) == 127))
            {
                return ;
            }
            if ((e.e1.value == null) || ((e.e1.value.op & 0xFF) == 135) || ((e.e1.value.op & 0xFF) == 140) || !hasSideEffect(e.e1.value))
            {
                this.ret = e.e2.value;
                if (this.ret != null)
                {
                    this.ret.type.value = e.type.value;
                }
            }
        }

        // Erasure: visit<ArrayLengthExp>
        public  void visit(ArrayLengthExp e) {
            if (this.unaOptimize(e, 1))
            {
                return ;
            }
            if (((e.e1.value.op & 0xFF) == 26))
            {
                VarDeclaration v = (((VarExp)e.e1.value)).var.isVarDeclaration();
                if ((v != null) && ((v.storage_class & 1L) != 0) && ((v.storage_class & 1048576L) != 0) && (v._init != null))
                {
                    {
                        Expression ci = v.getConstInitializer(true);
                        if ((ci) != null)
                        {
                            e.e1.value = ci;
                        }
                    }
                }
            }
            if (((e.e1.value.op & 0xFF) == 121) || ((e.e1.value.op & 0xFF) == 47) || ((e.e1.value.op & 0xFF) == 48) || ((e.e1.value.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
            {
                this.ret = ArrayLength(e.type.value, e.e1.value).copy();
            }
        }

        // Erasure: visit<EqualExp>
        public  void visit(EqualExp e) {
            if (this.binOptimize(e, 0))
            {
                return ;
            }
            Expression e1 = fromConstInitializer(this.result, e.e1.value);
            Expression e2 = fromConstInitializer(this.result, e.e2.value);
            if (((e1.op & 0xFF) == 127))
            {
                this.ret = e1;
                return ;
            }
            if (((e2.op & 0xFF) == 127))
            {
                this.ret = e2;
                return ;
            }
            this.ret = Equal(e.op, e.loc, e.type.value, e1, e2).copy();
            if (CTFEExp.isCantExp(this.ret))
            {
                this.ret = e;
            }
        }

        // Erasure: visit<IdentityExp>
        public  void visit(IdentityExp e) {
            if (this.binOptimize(e, 0))
            {
                return ;
            }
            if ((e.e1.value.isConst() != 0) && (e.e2.value.isConst() != 0) || ((e.e1.value.op & 0xFF) == 13) && ((e.e2.value.op & 0xFF) == 13))
            {
                this.ret = Identity(e.op, e.loc, e.type.value, e.e1.value, e.e2.value).copy();
                if (CTFEExp.isCantExp(this.ret))
                {
                    this.ret = e;
                }
            }
        }

        // Erasure: visit<IndexExp>
        public  void visit(IndexExp e) {
            if (this.expOptimize(e1, this.result & 1, false))
            {
                return ;
            }
            Expression ex = fromConstInitializer(this.result, e.e1.value);
            setLengthVarIfKnown(e.lengthVar.value, ex);
            if (this.expOptimize(e2, 0, false))
            {
                return ;
            }
            if (this.keepLvalue)
            {
                return ;
            }
            this.ret = Index(e.type.value, ex, e.e2.value).copy();
            if (CTFEExp.isCantExp(this.ret))
            {
                this.ret = e;
            }
        }

        // Erasure: visit<SliceExp>
        public  void visit(SliceExp e) {
            if (this.expOptimize(e1, this.result & 1, false))
            {
                return ;
            }
            if (e.lwr.value == null)
            {
                if (((e.e1.value.op & 0xFF) == 121))
                {
                    Type t = e.e1.value.type.value.toBasetype();
                    {
                        Type tn = t.nextOf();
                        if ((tn) != null)
                        {
                            this.ret = e.e1.value.castTo(null, tn.arrayOf());
                        }
                    }
                }
            }
            else
            {
                e.e1.value = fromConstInitializer(this.result, e.e1.value);
                setLengthVarIfKnown(e.lengthVar.value, e.e1.value);
                this.expOptimize(lwr, 0, false);
                this.expOptimize(upr, 0, false);
                if (((this.ret.op & 0xFF) == 127))
                {
                    return ;
                }
                this.ret = Slice(e.type.value, e.e1.value, e.lwr.value, e.upr.value).copy();
                if (CTFEExp.isCantExp(this.ret))
                {
                    this.ret = e;
                }
            }
            if (((this.ret.op & 0xFF) == 121))
            {
                e.e1.value = this.ret;
                e.lwr.value = null;
                e.upr.value = null;
                this.ret = e;
            }
        }

        // Erasure: visit<LogicalExp>
        public  void visit(LogicalExp e) {
            if (this.expOptimize(e1, 0, false))
            {
                return ;
            }
            boolean oror = (e.op & 0xFF) == 102;
            if (e.e1.value.isBool(oror))
            {
                this.ret = new IntegerExp(e.loc, (oror ? 1 : 0), Type.tbool);
                this.ret = Expression.combine(e.e1.value, this.ret);
                if (((e.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tvoid))
                {
                    this.ret = new CastExp(e.loc, this.ret, Type.tvoid);
                    this.ret.type.value = e.type.value;
                }
                this.ret = Expression_optimize(this.ret, this.result, false);
                return ;
            }
            if (this.expOptimize(e2, 0, false))
            {
                return ;
            }
            if (e.e1.value.isConst() != 0)
            {
                if (e.e2.value.isConst() != 0)
                {
                    boolean n1 = e.e1.value.isBool(true);
                    boolean n2 = e.e2.value.isBool(true);
                    this.ret = new IntegerExp(e.loc, oror ? ((n1 || n2) ? 1 : 0) : ((n1 && n2) ? 1 : 0), e.type.value);
                }
                else if (e.e1.value.isBool(!oror))
                {
                    if (((e.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tvoid))
                    {
                        this.ret = e.e2.value;
                    }
                    else
                    {
                        this.ret = new CastExp(e.loc, e.e2.value, e.type.value);
                        this.ret.type.value = e.type.value;
                    }
                }
            }
        }

        // Erasure: visit<CmpExp>
        public  void visit(CmpExp e) {
            if (this.binOptimize(e, 0))
            {
                return ;
            }
            Expression e1 = fromConstInitializer(this.result, e.e1.value);
            Expression e2 = fromConstInitializer(this.result, e.e2.value);
            this.ret = Cmp(e.op, e.loc, e.type.value, e1, e2).copy();
            if (CTFEExp.isCantExp(this.ret))
            {
                this.ret = e;
            }
        }

        // Erasure: visit<CatExp>
        public  void visit(CatExp e) {
            if (this.binOptimize(e, this.result))
            {
                return ;
            }
            if (((e.e1.value.op & 0xFF) == 70))
            {
                CatExp ce1 = ((CatExp)e.e1.value);
                CatExp cex = new CatExp(e.loc, ce1.e2.value, e.e2.value);
                cex.type.value = e.type.value;
                Expression ex = Expression_optimize(cex, this.result, false);
                if ((!pequals(ex, cex)))
                {
                    e.e1.value = ce1.e1.value;
                    e.e2.value = ex;
                }
            }
            if (((e.e1.value.op & 0xFF) == 31))
            {
                SliceExp se1 = ((SliceExp)e.e1.value);
                if (((se1.e1.value.op & 0xFF) == 121) && (se1.lwr.value == null))
                {
                    e.e1.value = se1.e1.value;
                }
            }
            if (((e.e2.value.op & 0xFF) == 31))
            {
                SliceExp se2 = ((SliceExp)e.e2.value);
                if (((se2.e1.value.op & 0xFF) == 121) && (se2.lwr.value == null))
                {
                    e.e2.value = se2.e1.value;
                }
            }
            this.ret = Cat(e.type.value, e.e1.value, e.e2.value).copy();
            if (CTFEExp.isCantExp(this.ret))
            {
                this.ret = e;
            }
        }

        // Erasure: visit<CondExp>
        public  void visit(CondExp e) {
            if (this.expOptimize(econd, 0, false))
            {
                return ;
            }
            if (e.econd.value.isBool(true))
            {
                this.ret = Expression_optimize(e.e1.value, this.result, this.keepLvalue);
            }
            else if (e.econd.value.isBool(false))
            {
                this.ret = Expression_optimize(e.e2.value, this.result, this.keepLvalue);
            }
            else
            {
                this.expOptimize(e1, this.result, this.keepLvalue);
                this.expOptimize(e2, this.result, this.keepLvalue);
            }
        }


        public OptimizeVisitor() {}
    }

    // Erasure: expandVar<int, VarDeclaration>
    public static Expression expandVar(int result, VarDeclaration v) {
        Function1<Expression,Expression> initializerReturn = new Function1<Expression,Expression>() {
            public Expression invoke(Expression e) {
             {
                Ref<Expression> e_ref = ref(e);
                if ((!pequals(e_ref.value.type.value, v.type)))
                {
                    e_ref.value = e_ref.value.castTo(null, v.type);
                }
                v.inuse++;
                e_ref.value = e_ref.value.optimize(result, false);
                v.inuse--;
                return e_ref.value;
            }}

        };
        Function0<Expression> nullReturn = new Function0<Expression>() {
            public Expression invoke() {
             {
                return null;
            }}

        };
        Function0<Expression> errorReturn = new Function0<Expression>() {
            public Expression invoke() {
             {
                return new ErrorExp();
            }}

        };
        if (v == null)
        {
            return nullReturn.invoke();
        }
        if ((v.originalType == null) && (v.semanticRun < PASS.semanticdone))
        {
            dsymbolSemantic(v, null);
        }
        if ((v.type != null) && v.isConst() || v.isImmutable() || ((v.storage_class & 8388608L) != 0))
        {
            Type tb = v.type.toBasetype();
            if (((v.storage_class & 8388608L) != 0) || tb.isscalar() || ((result & 1) != 0) && ((tb.ty & 0xFF) != ENUMTY.Tsarray) && ((tb.ty & 0xFF) != ENUMTY.Tstruct))
            {
                if (v._init != null)
                {
                    if (v.inuse != 0)
                    {
                        if ((v.storage_class & 8388608L) != 0)
                        {
                            v.error(new BytePtr("recursive initialization of constant"));
                            return errorReturn.invoke();
                        }
                        return nullReturn.invoke();
                    }
                    Expression ei = v.getConstInitializer(true);
                    if (ei == null)
                    {
                        if ((v.storage_class & 8388608L) != 0)
                        {
                            v.error(new BytePtr("enum cannot be initialized with `%s`"), v._init.toChars());
                            return errorReturn.invoke();
                        }
                        return nullReturn.invoke();
                    }
                    if (((ei.op & 0xFF) == 95) || ((ei.op & 0xFF) == 96))
                    {
                        AssignExp ae = ((AssignExp)ei);
                        ei = ae.e2.value;
                        if ((ei.isConst() == 1))
                        {
                        }
                        else if (((ei.op & 0xFF) == 121))
                        {
                            if (((result & 1) == 0) && ((ei.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tpointer))
                            {
                                return nullReturn.invoke();
                            }
                        }
                        else
                        {
                            return nullReturn.invoke();
                        }
                        if ((pequals(ei.type.value, v.type)))
                        {
                        }
                        else if ((ei.implicitConvTo(v.type) >= MATCH.constant))
                        {
                            ei = ei.implicitCastTo(null, v.type);
                            ei = expressionSemantic(ei, null);
                        }
                        else
                        {
                            return nullReturn.invoke();
                        }
                    }
                    else if (((v.storage_class & 8388608L) == 0) && (ei.isConst() != 1) && ((ei.op & 0xFF) != 121) && ((ei.op & 0xFF) != 19))
                    {
                        return nullReturn.invoke();
                    }
                    if (ei.type.value == null)
                    {
                        return nullReturn.invoke();
                    }
                    else
                    {
                        return initializerReturn.invoke(ei.copy());
                    }
                }
                else
                {
                    return nullReturn.invoke();
                }
                throw new AssertionError("Unreachable code!");
            }
        }
        return nullReturn.invoke();
    }

    // Erasure: fromConstInitializer<int, Expression>
    public static Expression fromConstInitializer(int result, Expression e1) {
        Expression e = e1;
        if (((e1.op & 0xFF) == 26))
        {
            VarExp ve = ((VarExp)e1);
            VarDeclaration v = ve.var.isVarDeclaration();
            e = expandVar(result, v);
            if (e != null)
            {
                if (((e.op & 0xFF) == 99) && (((((CommaExp)e)).e1.value.op & 0xFF) == 38))
                {
                    e = e1;
                }
                else if ((!pequals(e.type.value, e1.type.value)) && (e1.type.value != null) && ((e1.type.value.ty & 0xFF) != ENUMTY.Tident))
                {
                    e = e.copy();
                    e.type.value = e1.type.value;
                }
                e.loc.opAssign(e1.loc.copy());
            }
            else
            {
                e = e1;
            }
        }
        return e;
    }

    // Erasure: setLengthVarIfKnown<VarDeclaration, Expression>
    public static void setLengthVarIfKnown(VarDeclaration lengthVar, Expression arr) {
        if (lengthVar == null)
        {
            return ;
        }
        if ((lengthVar._init != null) && (lengthVar._init.isVoidInitializer() == null))
        {
            return ;
        }
        int len = 0;
        if (((arr.op & 0xFF) == 121))
        {
            len = (((StringExp)arr)).len;
        }
        else if (((arr.op & 0xFF) == 47))
        {
            len = ((((ArrayLiteralExp)arr)).elements).length;
        }
        else
        {
            Type t = arr.type.value.toBasetype();
            if (((t.ty & 0xFF) == ENUMTY.Tsarray))
            {
                len = (int)(((TypeSArray)t)).dim.toInteger();
            }
            else
            {
                return ;
            }
        }
        Expression dollar = new IntegerExp(Loc.initial, (long)len, Type.tsize_t);
        lengthVar._init = new ExpInitializer(Loc.initial, dollar);
        lengthVar.storage_class |= 5L;
    }

    // Erasure: setLengthVarIfKnown<VarDeclaration, Type>
    public static void setLengthVarIfKnown(VarDeclaration lengthVar, Type type) {
        if (lengthVar == null)
        {
            return ;
        }
        if ((lengthVar._init != null) && (lengthVar._init.isVoidInitializer() == null))
        {
            return ;
        }
        int len = 0;
        Type t = type.toBasetype();
        if (((t.ty & 0xFF) == ENUMTY.Tsarray))
        {
            len = (int)(((TypeSArray)t)).dim.toInteger();
        }
        else
        {
            return ;
        }
        Expression dollar = new IntegerExp(Loc.initial, (long)len, Type.tsize_t);
        lengthVar._init = new ExpInitializer(Loc.initial, dollar);
        lengthVar.storage_class |= 5L;
    }

    // Erasure: Expression_optimize<Expression, int, boolean>
    public static Expression Expression_optimize(Expression e, int result, boolean keepLvalue) {
        // skipping duplicate class OptimizeVisitor
        OptimizeVisitor v = new OptimizeVisitor(e, result, keepLvalue);
        for (; 1 != 0;){
            Expression ex = v.ret;
            ex.accept(v);
            if ((pequals(ex, v.ret)))
            {
                break;
            }
        }
        return v.ret;
    }

}
