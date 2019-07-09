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
        private Expression ret;
        private int result = 0;
        private boolean keepLvalue = false;
        public  OptimizeVisitor(Expression e, int result, boolean keepLvalue) {
            this.ret = e;
            this.result = result;
            this.keepLvalue = keepLvalue;
        }

        public  void error() {
            this.ret = new ErrorExp();
        }

        public  boolean expOptimize(Ref<Expression> e, int flags, boolean keepLvalue) {
            if (e.value == null)
                return false;
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
            expOptimize(e, flags, false);
        }

        public  boolean unaOptimize(UnaExp e, int flags) {
            return this.expOptimize(e.e1, flags, false);
        }

        public  boolean binOptimize(BinExp e, int flags) {
            this.expOptimize(e.e1, flags, false);
            this.expOptimize(e.e2, flags, false);
            return (this.ret.op & 0xFF) == 127;
        }

        public  void visit(Expression e) {
        }

        public  void visit(VarExp e) {
            if (this.keepLvalue)
            {
                VarDeclaration v = e.var.isVarDeclaration();
                if ((v != null) && ((v.storage_class & 8388608L) == 0))
                    return ;
            }
            this.ret = fromConstInitializer(this.result, e);
        }

        public  void visit(TupleExp e) {
            this.expOptimize(e.e0, 0, false);
            {
                int i = 0;
                for (; (i < (e.exps).length);i++){
                    this.expOptimize((e.exps).get(i), 0, false);
                }
            }
        }

        public  void visit(ArrayLiteralExp e) {
            if (e.elements != null)
            {
                this.expOptimize(e.basis, this.result & 1, false);
                {
                    int i = 0;
                    for (; (i < (e.elements).length);i++){
                        this.expOptimize((e.elements).get(i), this.result & 1, false);
                    }
                }
            }
        }

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

        public  void visit(StructLiteralExp e) {
            if ((e.stageflags & 4) != 0)
                return ;
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

        public  void visit(UnaExp e) {
            if (this.unaOptimize(e, this.result))
                return ;
        }

        public  void visit(NegExp e) {
            if (this.unaOptimize(e, this.result))
                return ;
            if ((e.e1.isConst() == 1))
            {
                this.ret = Neg(e.type, e.e1).copy();
            }
        }

        public  void visit(ComExp e) {
            if (this.unaOptimize(e, this.result))
                return ;
            if ((e.e1.isConst() == 1))
            {
                this.ret = Com(e.type, e.e1).copy();
            }
        }

        public  void visit(NotExp e) {
            if (this.unaOptimize(e, this.result))
                return ;
            if ((e.e1.isConst() == 1))
            {
                this.ret = Not(e.type, e.e1).copy();
            }
        }

        public  void visit(SymOffExp e) {
            assert(e.var != null);
        }

        public  void visit(AddrExp e) {
            if (((e.e1.op & 0xFF) == 99))
            {
                CommaExp ce = (CommaExp)e.e1;
                AddrExp ae = new AddrExp(e.loc, ce.e2, e.type);
                this.ret = new CommaExp(ce.loc, ce.e1, ae, true);
                this.ret.type = e.type;
                return ;
            }
            if (this.expOptimize(e.e1, this.result, true))
                return ;
            if (((e.e1.op & 0xFF) == 24))
            {
                Expression ex = ((PtrExp)e.e1).e1;
                if (e.type.equals(ex.type))
                    this.ret = ex;
                else if (e.type.toBasetype().equivalent(ex.type.toBasetype()))
                {
                    this.ret = ex.copy();
                    this.ret.type = e.type;
                }
                return ;
            }
            if (((e.e1.op & 0xFF) == 26))
            {
                VarExp ve = (VarExp)e.e1;
                if (!ve.var.isOut() && !ve.var.isRef() && !ve.var.isImportedSymbol())
                {
                    this.ret = new SymOffExp(e.loc, ve.var, 0L, ve.hasOverloads);
                    this.ret.type = e.type;
                    return ;
                }
            }
            if (((e.e1.op & 0xFF) == 62))
            {
                IndexExp ae = (IndexExp)e.e1;
                if (((ae.e2.op & 0xFF) == 135) && ((ae.e1.op & 0xFF) == 26))
                {
                    long index = (long)ae.e2.toInteger();
                    VarExp ve = (VarExp)ae.e1;
                    if (((ve.type.ty & 0xFF) == ENUMTY.Tsarray) && !ve.var.isImportedSymbol())
                    {
                        TypeSArray ts = (TypeSArray)ve.type;
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
                        this.ret.type = e.type;
                        return ;
                    }
                }
            }
        }

        public  void visit(PtrExp e) {
            if (this.expOptimize(e.e1, this.result, false))
                return ;
            if (((e.e1.op & 0xFF) == 19))
            {
                Expression ex = ((AddrExp)e.e1).e1;
                if (e.type.equals(ex.type))
                    this.ret = ex;
                else if (e.type.toBasetype().equivalent(ex.type.toBasetype()))
                {
                    this.ret = ex.copy();
                    this.ret.type = e.type;
                }
            }
            if (this.keepLvalue)
                return ;
            if (((e.e1.op & 0xFF) == 74))
            {
                Expression ex = Ptr(e.type, e.e1).copy();
                if (!CTFEExp.isCantExp(ex))
                {
                    this.ret = ex;
                    return ;
                }
            }
            if (((e.e1.op & 0xFF) == 25))
            {
                SymOffExp se = (SymOffExp)e.e1;
                VarDeclaration v = se.var.isVarDeclaration();
                Expression ex = expandVar(this.result, v);
                if ((ex != null) && ((ex.op & 0xFF) == 49))
                {
                    StructLiteralExp sle = (StructLiteralExp)ex;
                    ex = sle.getField(e.type, (int)se.offset);
                    if ((ex != null) && !CTFEExp.isCantExp(ex))
                    {
                        this.ret = ex;
                        return ;
                    }
                }
            }
        }

        public  void visit(DotVarExp e) {
            if (this.expOptimize(e.e1, this.result, false))
                return ;
            if (this.keepLvalue)
                return ;
            Expression ex = e.e1;
            if (((ex.op & 0xFF) == 26))
            {
                VarExp ve = (VarExp)ex;
                VarDeclaration v = ve.var.isVarDeclaration();
                ex = expandVar(this.result, v);
            }
            if ((ex != null) && ((ex.op & 0xFF) == 49))
            {
                StructLiteralExp sle = (StructLiteralExp)ex;
                VarDeclaration vf = e.var.isVarDeclaration();
                if ((vf != null) && !vf.overlapped)
                {
                    ex = sle.getField(e.type, vf.offset);
                    if ((ex != null) && !CTFEExp.isCantExp(ex))
                    {
                        this.ret = ex;
                        return ;
                    }
                }
            }
        }

        public  void visit(NewExp e) {
            this.expOptimize(e.thisexp, 0, false);
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

        public  void visit(CallExp e) {
            if (this.expOptimize(e.e1, this.result, false))
                return ;
            if (e.arguments != null)
            {
                Type t1 = e.e1.type.toBasetype();
                if (((t1.ty & 0xFF) == ENUMTY.Tdelegate))
                    t1 = t1.nextOf();
                assert(((t1.ty & 0xFF) == ENUMTY.Tfunction));
                TypeFunction tf = (TypeFunction)t1;
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

        public  void visit(CastExp e) {
            assert(e.type != null);
            byte op1 = e.e1.op;
            Expression e1old = e.e1;
            if (this.expOptimize(e.e1, this.result, false))
                return ;
            e.e1 = fromConstInitializer(this.result, e.e1);
            if ((pequals(e.e1, e1old)) && ((e.e1.op & 0xFF) == 47) && ((e.type.toBasetype().ty & 0xFF) == ENUMTY.Tpointer) && ((e.e1.type.toBasetype().ty & 0xFF) != ENUMTY.Tsarray))
            {
                return ;
            }
            if (((e.e1.op & 0xFF) == 121) || ((e.e1.op & 0xFF) == 47) && ((e.type.ty & 0xFF) == ENUMTY.Tpointer) || ((e.type.ty & 0xFF) == ENUMTY.Tarray))
            {
                long esz = e.type.nextOf().size(e.loc);
                long e1sz = e.e1.type.toBasetype().nextOf().size(e.e1.loc);
                if ((esz == -1L) || (e1sz == -1L))
                    this.error();
                    return ;
                if ((e1sz == esz))
                {
                    if (((e.type.nextOf().ty & 0xFF) == ENUMTY.Tvoid))
                        return ;
                    this.ret = e.e1.castTo(null, e.type);
                    return ;
                }
            }
            if (((e.e1.op & 0xFF) == 49) && (e.e1.type.implicitConvTo(e.type) >= MATCH.constant))
            {
            /*L1:*/
                this.ret = (pequals(e1old, e.e1)) ? e.e1.copy() : e.e1;
                this.ret.type = e.type;
                return ;
            }
            if (((op1 & 0xFF) != 47) && ((e.e1.op & 0xFF) == 47))
            {
                this.ret = e.e1.castTo(null, e.to);
                return ;
            }
            if (((e.e1.op & 0xFF) == 13) && ((e.type.ty & 0xFF) == ENUMTY.Tpointer) || ((e.type.ty & 0xFF) == ENUMTY.Tclass) || ((e.type.ty & 0xFF) == ENUMTY.Tarray))
            {
                /*goto L1*/throw Dispatch0.INSTANCE;
            }
            if (((e.type.ty & 0xFF) == ENUMTY.Tclass) && ((e.e1.type.ty & 0xFF) == ENUMTY.Tclass))
            {
                ClassDeclaration cdfrom = e.e1.type.isClassHandle();
                ClassDeclaration cdto = e.type.isClassHandle();
                if ((pequals(cdto, ClassDeclaration.object)) && (cdfrom.isInterfaceDeclaration() == null))
                    /*goto L1*/throw Dispatch0.INSTANCE;
                cdfrom.size(e.loc);
                assert((cdfrom.sizeok == Sizeok.done));
                assert((cdto.sizeok == Sizeok.done) || !cdto.isBaseOf(cdfrom, null));
                IntRef offset = ref(0);
                if (cdto.isBaseOf(cdfrom, ptr(offset)) && (offset.value == 0))
                {
                    /*goto L1*/throw Dispatch0.INSTANCE;
                }
            }
            if (e.to.mutableOf().constOf().equals(e.e1.type.mutableOf().constOf()))
            {
                /*goto L1*/throw Dispatch0.INSTANCE;
            }
            if (e.e1.isConst() != 0)
            {
                if (((e.e1.op & 0xFF) == 25))
                {
                    if (((e.type.toBasetype().ty & 0xFF) != ENUMTY.Tsarray))
                    {
                        long esz = e.type.size(e.loc);
                        long e1sz = e.e1.type.size(e.e1.loc);
                        if ((esz == -1L) || (e1sz == -1L))
                            this.error();
                            return ;
                        if ((esz == e1sz))
                            /*goto L1*/throw Dispatch0.INSTANCE;
                    }
                    return ;
                }
                if (((e.to.toBasetype().ty & 0xFF) != ENUMTY.Tvoid))
                {
                    if (e.e1.type.equals(e.type) && e.type.equals(e.to))
                        this.ret = e.e1;
                    else
                        this.ret = Cast(e.loc, e.type, e.to, e.e1).copy();
                }
            }
        }

        public  void visit(BinExp e) {
            boolean e2only = ((e.op & 0xFF) == 95) || ((e.op & 0xFF) == 96);
            if (e2only ? this.expOptimize(e.e2, this.result, false) : this.binOptimize(e, this.result))
                return ;
            if (((e.op & 0xFF) == 66) || ((e.op & 0xFF) == 67) || ((e.op & 0xFF) == 69))
            {
                if ((e.e2.isConst() == 1))
                {
                    long i2 = (long)e.e2.toInteger();
                    long sz = e.e1.type.size(e.e1.loc);
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

        public  void visit(AddExp e) {
            if (this.binOptimize(e, this.result))
                return ;
            if ((e.e1.isConst() != 0) && (e.e2.isConst() != 0))
            {
                if (((e.e1.op & 0xFF) == 25) && ((e.e2.op & 0xFF) == 25))
                    return ;
                this.ret = Add(e.loc, e.type, e.e1, e.e2).copy();
            }
        }

        public  void visit(MinExp e) {
            if (this.binOptimize(e, this.result))
                return ;
            if ((e.e1.isConst() != 0) && (e.e2.isConst() != 0))
            {
                if (((e.e2.op & 0xFF) == 25))
                    return ;
                this.ret = Min(e.loc, e.type, e.e1, e.e2).copy();
            }
        }

        public  void visit(MulExp e) {
            if (this.binOptimize(e, this.result))
                return ;
            if ((e.e1.isConst() == 1) && (e.e2.isConst() == 1))
            {
                this.ret = Mul(e.loc, e.type, e.e1, e.e2).copy();
            }
        }

        public  void visit(DivExp e) {
            if (this.binOptimize(e, this.result))
                return ;
            if ((e.e1.isConst() == 1) && (e.e2.isConst() == 1))
            {
                this.ret = Div(e.loc, e.type, e.e1, e.e2).copy();
            }
        }

        public  void visit(ModExp e) {
            if (this.binOptimize(e, this.result))
                return ;
            if ((e.e1.isConst() == 1) && (e.e2.isConst() == 1))
            {
                this.ret = Mod(e.loc, e.type, e.e1, e.e2).copy();
            }
        }

        public  void shift_optimize(BinExp e, Function4<Loc,Type,Expression,Expression,UnionExp> shift) {
            if (this.binOptimize(e, this.result))
                return ;
            if ((e.e2.isConst() == 1))
            {
                long i2 = (long)e.e2.toInteger();
                long sz = e.e1.type.size(e.e1.loc);
                assert((sz != -1L));
                sz *= 8L;
                if ((i2 < 0L) || ((long)i2 >= sz))
                {
                    e.error(new BytePtr("shift by %lld is outside the range `0..%llu`"), i2, sz - 1L);
                    this.error();
                    return ;
                }
                if ((e.e1.isConst() == 1))
                    this.ret = (shift).invoke(e.loc, e.type, e.e1, e.e2).copy();
            }
        }

        public  void visit(ShlExp e) {
            this.shift_optimize(e, optimize::Shl);
        }

        public  void visit(ShrExp e) {
            this.shift_optimize(e, optimize::Shr);
        }

        public  void visit(UshrExp e) {
            this.shift_optimize(e, optimize::Ushr);
        }

        public  void visit(AndExp e) {
            if (this.binOptimize(e, this.result))
                return ;
            if ((e.e1.isConst() == 1) && (e.e2.isConst() == 1))
                this.ret = And(e.loc, e.type, e.e1, e.e2).copy();
        }

        public  void visit(OrExp e) {
            if (this.binOptimize(e, this.result))
                return ;
            if ((e.e1.isConst() == 1) && (e.e2.isConst() == 1))
                this.ret = Or(e.loc, e.type, e.e1, e.e2).copy();
        }

        public  void visit(XorExp e) {
            if (this.binOptimize(e, this.result))
                return ;
            if ((e.e1.isConst() == 1) && (e.e2.isConst() == 1))
                this.ret = Xor(e.loc, e.type, e.e1, e.e2).copy();
        }

        public  void visit(PowExp e) {
            if (this.binOptimize(e, this.result))
                return ;
            if (((e.e1.op & 0xFF) == 135) && (e.e1.toInteger() == 1L) || ((e.e1.op & 0xFF) == 140) && (e.e1.toReal() == CTFloat.one))
            {
                this.ret = new CommaExp(e.loc, e.e2, e.e1, true);
                this.ret.type = e.type;
                return ;
            }
            if (e.e2.type.isintegral() && ((e.e1.op & 0xFF) == 135) && ((long)e.e1.toInteger() == -1L))
            {
                this.ret = new AndExp(e.loc, e.e2, new IntegerExp(e.loc, 1L, e.e2.type));
                this.ret.type = e.e2.type;
                this.ret = new CondExp(e.loc, this.ret, new IntegerExp(e.loc, -1L, e.type), new IntegerExp(e.loc, 1L, e.type));
                this.ret.type = e.type;
                return ;
            }
            if (((e.e2.op & 0xFF) == 135) && (e.e2.toInteger() == 0L) || ((e.e2.op & 0xFF) == 140) && (e.e2.toReal() == CTFloat.zero))
            {
                if (e.e1.type.isintegral())
                    this.ret = new IntegerExp(e.loc, 1L, e.e1.type);
                else
                    this.ret = new RealExp(e.loc, CTFloat.one, e.e1.type);
                this.ret = new CommaExp(e.loc, e.e1, this.ret, true);
                this.ret.type = e.type;
                return ;
            }
            if (((e.e2.op & 0xFF) == 135) && (e.e2.toInteger() == 1L) || ((e.e2.op & 0xFF) == 140) && (e.e2.toReal() == CTFloat.one))
            {
                this.ret = e.e1;
                return ;
            }
            if (((e.e2.op & 0xFF) == 140) && (e.e2.toReal() == CTFloat.minusone))
            {
                this.ret = new DivExp(e.loc, new RealExp(e.loc, CTFloat.one, e.e2.type), e.e1);
                this.ret.type = e.type;
                return ;
            }
            if (e.e1.type.isintegral() && ((e.e2.op & 0xFF) == 135) && ((long)e.e2.toInteger() < 0L))
            {
                e.error(new BytePtr("cannot raise `%s` to a negative integer power. Did you mean `(cast(real)%s)^^%s` ?"), e.e1.type.toBasetype().toChars(), e.e1.toChars(), e.e2.toChars());
                this.error();
                return ;
            }
            if (((e.e2.op & 0xFF) == 140))
            {
                if ((e.e2.toReal() == (double)(long)e.e2.toReal()))
                    e.e2 = new IntegerExp(e.loc, e.e2.toInteger(), Type.tint64);
            }
            if ((e.e1.isConst() == 1) && (e.e2.isConst() == 1))
            {
                Expression ex = Pow(e.loc, e.type, e.e1, e.e2).copy();
                if (!CTFEExp.isCantExp(ex))
                {
                    this.ret = ex;
                    return ;
                }
            }
            if (((e.e1.op & 0xFF) == 135) && (e.e1.toInteger() > 0L) && ((e.e1.toInteger() - 1L & e.e1.toInteger()) == 0) && e.e2.type.isintegral() && e.e2.type.isunsigned())
            {
                long i = e.e1.toInteger();
                long mul = 1L;
                for (; ((i >>= 1) > 1L);) {
                    mul++;
                }
                Expression shift = new MulExp(e.loc, e.e2, new IntegerExp(e.loc, mul, e.e2.type));
                shift.type = e.e2.type;
                shift = shift.castTo(null, Type.tshiftcnt);
                this.ret = new ShlExp(e.loc, new IntegerExp(e.loc, 1L, e.e1.type), shift);
                this.ret.type = e.type;
                return ;
            }
        }

        public  void visit(CommaExp e) {
            this.expOptimize(e.e1, 0, false);
            this.expOptimize(e.e2, this.result, this.keepLvalue);
            if (((this.ret.op & 0xFF) == 127))
                return ;
            if ((e.e1 == null) || ((e.e1.op & 0xFF) == 135) || ((e.e1.op & 0xFF) == 140) || !hasSideEffect(e.e1))
            {
                this.ret = e.e2;
                if (this.ret != null)
                    this.ret.type = e.type;
            }
        }

        public  void visit(ArrayLengthExp e) {
            if (this.unaOptimize(e, 1))
                return ;
            if (((e.e1.op & 0xFF) == 26))
            {
                VarDeclaration v = ((VarExp)e.e1).var.isVarDeclaration();
                if ((v != null) && ((v.storage_class & 1L) != 0) && ((v.storage_class & 1048576L) != 0) && (v._init != null))
                {
                    {
                        Expression ci = v.getConstInitializer(true);
                        if ((ci) != null)
                            e.e1 = ci;
                    }
                }
            }
            if (((e.e1.op & 0xFF) == 121) || ((e.e1.op & 0xFF) == 47) || ((e.e1.op & 0xFF) == 48) || ((e.e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
            {
                this.ret = ArrayLength(e.type, e.e1).copy();
            }
        }

        public  void visit(EqualExp e) {
            if (this.binOptimize(e, 0))
                return ;
            Expression e1 = fromConstInitializer(this.result, e.e1);
            Expression e2 = fromConstInitializer(this.result, e.e2);
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
            this.ret = Equal(e.op, e.loc, e.type, e1, e2).copy();
            if (CTFEExp.isCantExp(this.ret))
                this.ret = e;
        }

        public  void visit(IdentityExp e) {
            if (this.binOptimize(e, 0))
                return ;
            if ((e.e1.isConst() != 0) && (e.e2.isConst() != 0) || ((e.e1.op & 0xFF) == 13) && ((e.e2.op & 0xFF) == 13))
            {
                this.ret = Identity(e.op, e.loc, e.type, e.e1, e.e2).copy();
                if (CTFEExp.isCantExp(this.ret))
                    this.ret = e;
            }
        }

        public  void visit(IndexExp e) {
            if (this.expOptimize(e.e1, this.result & 1, false))
                return ;
            Expression ex = fromConstInitializer(this.result, e.e1);
            setLengthVarIfKnown(e.lengthVar, ex);
            if (this.expOptimize(e.e2, 0, false))
                return ;
            if (this.keepLvalue)
                return ;
            this.ret = Index(e.type, ex, e.e2).copy();
            if (CTFEExp.isCantExp(this.ret))
                this.ret = e;
        }

        public  void visit(SliceExp e) {
            if (this.expOptimize(e.e1, this.result & 1, false))
                return ;
            if (e.lwr == null)
            {
                if (((e.e1.op & 0xFF) == 121))
                {
                    Type t = e.e1.type.toBasetype();
                    {
                        Type tn = t.nextOf();
                        if ((tn) != null)
                            this.ret = e.e1.castTo(null, tn.arrayOf());
                    }
                }
            }
            else
            {
                e.e1 = fromConstInitializer(this.result, e.e1);
                setLengthVarIfKnown(e.lengthVar, e.e1);
                this.expOptimize(e.lwr, 0, false);
                this.expOptimize(e.upr, 0, false);
                if (((this.ret.op & 0xFF) == 127))
                    return ;
                this.ret = Slice(e.type, e.e1, e.lwr, e.upr).copy();
                if (CTFEExp.isCantExp(this.ret))
                    this.ret = e;
            }
            if (((this.ret.op & 0xFF) == 121))
            {
                e.e1 = this.ret;
                e.lwr = null;
                e.upr = null;
                this.ret = e;
            }
        }

        public  void visit(LogicalExp e) {
            if (this.expOptimize(e.e1, 0, false))
                return ;
            boolean oror = (e.op & 0xFF) == 102;
            if (e.e1.isBool(oror))
            {
                this.ret = new IntegerExp(e.loc, (oror ? 1 : 0), Type.tbool);
                this.ret = Expression.combine(e.e1, this.ret);
                if (((e.type.toBasetype().ty & 0xFF) == ENUMTY.Tvoid))
                {
                    this.ret = new CastExp(e.loc, this.ret, Type.tvoid);
                    this.ret.type = e.type;
                }
                this.ret = Expression_optimize(this.ret, this.result, false);
                return ;
            }
            if (this.expOptimize(e.e2, 0, false))
                return ;
            if (e.e1.isConst() != 0)
            {
                if (e.e2.isConst() != 0)
                {
                    boolean n1 = e.e1.isBool(true);
                    boolean n2 = e.e2.isBool(true);
                    this.ret = new IntegerExp(e.loc, oror ? ((n1 || n2) ? 1 : 0) : ((n1 && n2) ? 1 : 0), e.type);
                }
                else if (e.e1.isBool(!oror))
                {
                    if (((e.type.toBasetype().ty & 0xFF) == ENUMTY.Tvoid))
                        this.ret = e.e2;
                    else
                    {
                        this.ret = new CastExp(e.loc, e.e2, e.type);
                        this.ret.type = e.type;
                    }
                }
            }
        }

        public  void visit(CmpExp e) {
            if (this.binOptimize(e, 0))
                return ;
            Expression e1 = fromConstInitializer(this.result, e.e1);
            Expression e2 = fromConstInitializer(this.result, e.e2);
            this.ret = Cmp(e.op, e.loc, e.type, e1, e2).copy();
            if (CTFEExp.isCantExp(this.ret))
                this.ret = e;
        }

        public  void visit(CatExp e) {
            if (this.binOptimize(e, this.result))
                return ;
            if (((e.e1.op & 0xFF) == 70))
            {
                CatExp ce1 = (CatExp)e.e1;
                CatExp cex = new CatExp(e.loc, ce1.e2, e.e2);
                cex.type = e.type;
                Expression ex = Expression_optimize(cex, this.result, false);
                if ((!pequals(ex, cex)))
                {
                    e.e1 = ce1.e1;
                    e.e2 = ex;
                }
            }
            if (((e.e1.op & 0xFF) == 31))
            {
                SliceExp se1 = (SliceExp)e.e1;
                if (((se1.e1.op & 0xFF) == 121) && (se1.lwr == null))
                    e.e1 = se1.e1;
            }
            if (((e.e2.op & 0xFF) == 31))
            {
                SliceExp se2 = (SliceExp)e.e2;
                if (((se2.e1.op & 0xFF) == 121) && (se2.lwr == null))
                    e.e2 = se2.e1;
            }
            this.ret = Cat(e.type, e.e1, e.e2).copy();
            if (CTFEExp.isCantExp(this.ret))
                this.ret = e;
        }

        public  void visit(CondExp e) {
            if (this.expOptimize(e.econd, 0, false))
                return ;
            if (e.econd.isBool(true))
                this.ret = Expression_optimize(e.e1, this.result, this.keepLvalue);
            else if (e.econd.isBool(false))
                this.ret = Expression_optimize(e.e2, this.result, this.keepLvalue);
            else
            {
                this.expOptimize(e.e1, this.result, this.keepLvalue);
                this.expOptimize(e.e2, this.result, this.keepLvalue);
            }
        }


        public OptimizeVisitor() {}
    }

    public static Expression expandVar(int result, VarDeclaration v) {
        IntRef result_ref = ref(result);
        Ref<VarDeclaration> v_ref = ref(v);
        Function1<Expression,Expression> initializerReturn = new Function1<Expression,Expression>(){
            public Expression invoke(Expression e) {
                if ((!pequals(e.type, v_ref.value.type)))
                {
                    e = e.castTo(null, v_ref.value.type);
                }
                v_ref.value.inuse++;
                e = e.optimize(result_ref.value, false);
                v_ref.value.inuse--;
                return e;
            }
        };
        Function0<Expression> nullReturn = new Function0<Expression>(){
            public Expression invoke() {
                return null;
            }
        };
        Function0<Expression> errorReturn = new Function0<Expression>(){
            public Expression invoke() {
                return new ErrorExp();
            }
        };
        if (v_ref.value == null)
            return nullReturn.invoke();
        if ((v_ref.value.originalType == null) && (v_ref.value.semanticRun < PASS.semanticdone))
            dsymbolSemantic(v_ref.value, null);
        if ((v_ref.value.type != null) && v_ref.value.isConst() || v_ref.value.isImmutable() || ((v_ref.value.storage_class & 8388608L) != 0))
        {
            Type tb = v_ref.value.type.toBasetype();
            if (((v_ref.value.storage_class & 8388608L) != 0) || tb.isscalar() || ((result_ref.value & 1) != 0) && ((tb.ty & 0xFF) != ENUMTY.Tsarray) && ((tb.ty & 0xFF) != ENUMTY.Tstruct))
            {
                if (v_ref.value._init != null)
                {
                    if (v_ref.value.inuse != 0)
                    {
                        if ((v_ref.value.storage_class & 8388608L) != 0)
                        {
                            v_ref.value.error(new BytePtr("recursive initialization of constant"));
                            return errorReturn.invoke();
                        }
                        return nullReturn.invoke();
                    }
                    Expression ei = v_ref.value.getConstInitializer(true);
                    if (ei == null)
                    {
                        if ((v_ref.value.storage_class & 8388608L) != 0)
                        {
                            v_ref.value.error(new BytePtr("enum cannot be initialized with `%s`"), v_ref.value._init.toChars());
                            return errorReturn.invoke();
                        }
                        return nullReturn.invoke();
                    }
                    if (((ei.op & 0xFF) == 95) || ((ei.op & 0xFF) == 96))
                    {
                        AssignExp ae = (AssignExp)ei;
                        ei = ae.e2;
                        if ((ei.isConst() == 1))
                        {
                        }
                        else if (((ei.op & 0xFF) == 121))
                        {
                            if (((result_ref.value & 1) == 0) && ((ei.type.toBasetype().ty & 0xFF) == ENUMTY.Tpointer))
                                return nullReturn.invoke();
                        }
                        else
                            return nullReturn.invoke();
                        if ((pequals(ei.type, v_ref.value.type)))
                        {
                        }
                        else if ((ei.implicitConvTo(v_ref.value.type) >= MATCH.constant))
                        {
                            ei = ei.implicitCastTo(null, v_ref.value.type);
                            ei = expressionSemantic(ei, null);
                        }
                        else
                            return nullReturn.invoke();
                    }
                    else if (((v_ref.value.storage_class & 8388608L) == 0) && (ei.isConst() != 1) && ((ei.op & 0xFF) != 121) && ((ei.op & 0xFF) != 19))
                    {
                        return nullReturn.invoke();
                    }
                    if (ei.type == null)
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

    public static Expression fromConstInitializer(int result, Expression e1) {
        Expression e = e1;
        if (((e1.op & 0xFF) == 26))
        {
            VarExp ve = (VarExp)e1;
            VarDeclaration v = ve.var.isVarDeclaration();
            e = expandVar(result, v);
            if (e != null)
            {
                if (((e.op & 0xFF) == 99) && ((((CommaExp)e).e1.op & 0xFF) == 38))
                    e = e1;
                else if ((!pequals(e.type, e1.type)) && (e1.type != null) && ((e1.type.ty & 0xFF) != ENUMTY.Tident))
                {
                    e = e.copy();
                    e.type = e1.type;
                }
                e.loc = e1.loc.copy();
            }
            else
            {
                e = e1;
            }
        }
        return e;
    }

    public static void setLengthVarIfKnown(VarDeclaration lengthVar, Expression arr) {
        if (lengthVar == null)
            return ;
        if ((lengthVar._init != null) && (lengthVar._init.isVoidInitializer() == null))
            return ;
        int len = 0;
        if (((arr.op & 0xFF) == 121))
            len = ((StringExp)arr).len;
        else if (((arr.op & 0xFF) == 47))
            len = (((ArrayLiteralExp)arr).elements).length;
        else
        {
            Type t = arr.type.toBasetype();
            if (((t.ty & 0xFF) == ENUMTY.Tsarray))
                len = (int)((TypeSArray)t).dim.toInteger();
            else
                return ;
        }
        Expression dollar = new IntegerExp(Loc.initial, (long)len, Type.tsize_t);
        lengthVar._init = new ExpInitializer(Loc.initial, dollar);
        lengthVar.storage_class |= 5L;
    }

    public static void setLengthVarIfKnown(VarDeclaration lengthVar, Type type) {
        if (lengthVar == null)
            return ;
        if ((lengthVar._init != null) && (lengthVar._init.isVoidInitializer() == null))
            return ;
        int len = 0;
        Type t = type.toBasetype();
        if (((t.ty & 0xFF) == ENUMTY.Tsarray))
            len = (int)((TypeSArray)t).dim.toInteger();
        else
            return ;
        Expression dollar = new IntegerExp(Loc.initial, (long)len, Type.tsize_t);
        lengthVar._init = new ExpInitializer(Loc.initial, dollar);
        lengthVar.storage_class |= 5L;
    }

    public static Expression Expression_optimize(Expression e, int result, boolean keepLvalue) {
        OptimizeVisitor v = new OptimizeVisitor(e, result, keepLvalue);
        for (; 1 != 0;){
            Expression ex = v.ret;
            ex.accept(v);
            if ((pequals(ex, v.ret)))
                break;
        }
        return v.ret;
    }

}
