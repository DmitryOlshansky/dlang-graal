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
        private Ref<Expression> ret = ref(null);
        private IntRef result = ref(0);
        private Ref<Boolean> keepLvalue = ref(false);
        public  OptimizeVisitor(Expression e, int result, boolean keepLvalue) {
            Ref<Expression> e_ref = ref(e);
            this.ret.value = e_ref.value;
            this.result.value = result;
            this.keepLvalue.value = keepLvalue;
        }

        public  void error() {
            this.ret.value = new ErrorExp();
        }

        public  boolean expOptimize(Ref<Expression> e, int flags, boolean keepLvalue) {
            IntRef flags_ref = ref(flags);
            Ref<Boolean> keepLvalue_ref = ref(keepLvalue);
            if (e.value == null)
            {
                return false;
            }
            Ref<Expression> ex = ref(Expression_optimize(e.value, flags_ref.value, keepLvalue_ref.value));
            if (((ex.value.op.value & 0xFF) == 127))
            {
                this.ret.value = ex.value;
                return true;
            }
            else
            {
                e.value = ex.value;
                return false;
            }
        }

        // defaulted all parameters starting with #3
        public  boolean expOptimize(Ref<Expression> e, int flags) {
            return expOptimize(e, flags, false);
        }

        public  boolean unaOptimize(UnaExp e, int flags) {
            IntRef flags_ref = ref(flags);
            return this.expOptimize(e1, flags_ref.value, false);
        }

        public  boolean binOptimize(BinExp e, int flags) {
            IntRef flags_ref = ref(flags);
            this.expOptimize(e1, flags_ref.value, false);
            this.expOptimize(e2, flags_ref.value, false);
            return (this.ret.value.op.value & 0xFF) == 127;
        }

        public  void visit(Expression e) {
        }

        public  void visit(VarExp e) {
            Ref<VarExp> e_ref = ref(e);
            if (this.keepLvalue.value)
            {
                Ref<VarDeclaration> v = ref(e_ref.value.var.value.isVarDeclaration());
                if ((v.value != null) && ((v.value.storage_class.value & 8388608L) == 0))
                {
                    return ;
                }
            }
            this.ret.value = fromConstInitializer(this.result.value, e_ref.value);
        }

        public  void visit(TupleExp e) {
            this.expOptimize(e0, 0, false);
            {
                IntRef i = ref(0);
                for (; (i.value < (e.exps.value.get()).length.value);i.value++){
                    this.expOptimize((e.exps.value.get()).get(i.value), 0, false);
                }
            }
        }

        public  void visit(ArrayLiteralExp e) {
            if (e.elements.value != null)
            {
                this.expOptimize(basis, this.result.value & 1, false);
                {
                    IntRef i = ref(0);
                    for (; (i.value < (e.elements.value.get()).length.value);i.value++){
                        this.expOptimize((e.elements.value.get()).get(i.value), this.result.value & 1, false);
                    }
                }
            }
        }

        public  void visit(AssocArrayLiteralExp e) {
            assert(((e.keys.value.get()).length.value == (e.values.value.get()).length.value));
            {
                IntRef i = ref(0);
                for (; (i.value < (e.keys.value.get()).length.value);i.value++){
                    this.expOptimize((e.keys.value.get()).get(i.value), this.result.value & 1, false);
                    this.expOptimize((e.values.value.get()).get(i.value), this.result.value & 1, false);
                }
            }
        }

        public  void visit(StructLiteralExp e) {
            if ((e.stageflags.value & 4) != 0)
            {
                return ;
            }
            IntRef old = ref(e.stageflags.value);
            e.stageflags.value |= 4;
            if (e.elements.value != null)
            {
                {
                    IntRef i = ref(0);
                    for (; (i.value < (e.elements.value.get()).length.value);i.value++){
                        this.expOptimize((e.elements.value.get()).get(i.value), this.result.value & 1, false);
                    }
                }
            }
            e.stageflags.value = old.value;
        }

        public  void visit(UnaExp e) {
            Ref<UnaExp> e_ref = ref(e);
            if (this.unaOptimize(e_ref.value, this.result.value))
            {
                return ;
            }
        }

        public  void visit(NegExp e) {
            Ref<NegExp> e_ref = ref(e);
            if (this.unaOptimize(e_ref.value, this.result.value))
            {
                return ;
            }
            if ((e_ref.value.e1.value.isConst() == 1))
            {
                this.ret.value = Neg(e_ref.value.type.value, e_ref.value.e1.value).copy();
            }
        }

        public  void visit(ComExp e) {
            Ref<ComExp> e_ref = ref(e);
            if (this.unaOptimize(e_ref.value, this.result.value))
            {
                return ;
            }
            if ((e_ref.value.e1.value.isConst() == 1))
            {
                this.ret.value = Com(e_ref.value.type.value, e_ref.value.e1.value).copy();
            }
        }

        public  void visit(NotExp e) {
            Ref<NotExp> e_ref = ref(e);
            if (this.unaOptimize(e_ref.value, this.result.value))
            {
                return ;
            }
            if ((e_ref.value.e1.value.isConst() == 1))
            {
                this.ret.value = Not(e_ref.value.type.value, e_ref.value.e1.value).copy();
            }
        }

        public  void visit(SymOffExp e) {
            assert(e.var.value != null);
        }

        public  void visit(AddrExp e) {
            if (((e.e1.value.op.value & 0xFF) == 99))
            {
                CommaExp ce = (CommaExp)e.e1.value;
                Ref<AddrExp> ae = ref(new AddrExp(e.loc.value, ce.e2.value, e.type.value));
                this.ret.value = new CommaExp(ce.loc.value, ce.e1.value, ae.value, true);
                this.ret.value.type.value = e.type.value;
                return ;
            }
            if (this.expOptimize(e1, this.result.value, true))
            {
                return ;
            }
            if (((e.e1.value.op.value & 0xFF) == 24))
            {
                Ref<Expression> ex = ref(((PtrExp)e.e1.value).e1.value);
                if (e.type.value.equals(ex.value.type.value))
                {
                    this.ret.value = ex.value;
                }
                else if (e.type.value.toBasetype().equivalent(ex.value.type.value.toBasetype()))
                {
                    this.ret.value = ex.value.copy();
                    this.ret.value.type.value = e.type.value;
                }
                return ;
            }
            if (((e.e1.value.op.value & 0xFF) == 26))
            {
                VarExp ve = (VarExp)e.e1.value;
                if (!ve.var.value.isOut() && !ve.var.value.isRef() && !ve.var.value.isImportedSymbol())
                {
                    this.ret.value = new SymOffExp(e.loc.value, ve.var.value, 0L, ve.hasOverloads.value);
                    this.ret.value.type.value = e.type.value;
                    return ;
                }
            }
            if (((e.e1.value.op.value & 0xFF) == 62))
            {
                IndexExp ae = (IndexExp)e.e1.value;
                if (((ae.e2.value.op.value & 0xFF) == 135) && ((ae.e1.value.op.value & 0xFF) == 26))
                {
                    Ref<Long> index = ref((long)ae.e2.value.toInteger());
                    VarExp ve = (VarExp)ae.e1.value;
                    if (((ve.type.value.ty.value & 0xFF) == ENUMTY.Tsarray) && !ve.var.value.isImportedSymbol())
                    {
                        TypeSArray ts = (TypeSArray)ve.type.value;
                        Ref<Long> dim = ref((long)ts.dim.value.toInteger());
                        if ((index.value < 0L) || (index.value >= dim.value))
                        {
                            e.error(new BytePtr("array index %lld is out of bounds `[0..%lld]`"), index.value, dim.value);
                            this.error();
                            return ;
                        }
                        Ref<Boolean> overflow = ref(false);
                        Ref<Long> offset = ref(mulu((long)index.value, ts.nextOf().size(e.loc.value), overflow));
                        if (overflow.value)
                        {
                            e.error(new BytePtr("array offset overflow"));
                            this.error();
                            return ;
                        }
                        this.ret.value = new SymOffExp(e.loc.value, ve.var.value, offset.value, true);
                        this.ret.value.type.value = e.type.value;
                        return ;
                    }
                }
            }
        }

        public  void visit(PtrExp e) {
            if (this.expOptimize(e1, this.result.value, false))
            {
                return ;
            }
            if (((e.e1.value.op.value & 0xFF) == 19))
            {
                Ref<Expression> ex = ref(((AddrExp)e.e1.value).e1.value);
                if (e.type.value.equals(ex.value.type.value))
                {
                    this.ret.value = ex.value;
                }
                else if (e.type.value.toBasetype().equivalent(ex.value.type.value.toBasetype()))
                {
                    this.ret.value = ex.value.copy();
                    this.ret.value.type.value = e.type.value;
                }
            }
            if (this.keepLvalue.value)
            {
                return ;
            }
            if (((e.e1.value.op.value & 0xFF) == 74))
            {
                Ref<Expression> ex = ref(Ptr(e.type.value, e.e1.value).copy());
                if (!CTFEExp.isCantExp(ex.value))
                {
                    this.ret.value = ex.value;
                    return ;
                }
            }
            if (((e.e1.value.op.value & 0xFF) == 25))
            {
                SymOffExp se = (SymOffExp)e.e1.value;
                Ref<VarDeclaration> v = ref(se.var.value.isVarDeclaration());
                Ref<Expression> ex = ref(expandVar(this.result.value, v.value));
                if ((ex.value != null) && ((ex.value.op.value & 0xFF) == 49))
                {
                    StructLiteralExp sle = (StructLiteralExp)ex.value;
                    ex.value = sle.getField(e.type.value, (int)se.offset.value);
                    if ((ex.value != null) && !CTFEExp.isCantExp(ex.value))
                    {
                        this.ret.value = ex.value;
                        return ;
                    }
                }
            }
        }

        public  void visit(DotVarExp e) {
            if (this.expOptimize(e1, this.result.value, false))
            {
                return ;
            }
            if (this.keepLvalue.value)
            {
                return ;
            }
            Ref<Expression> ex = ref(e.e1.value);
            if (((ex.value.op.value & 0xFF) == 26))
            {
                VarExp ve = (VarExp)ex.value;
                Ref<VarDeclaration> v = ref(ve.var.value.isVarDeclaration());
                ex.value = expandVar(this.result.value, v.value);
            }
            if ((ex.value != null) && ((ex.value.op.value & 0xFF) == 49))
            {
                StructLiteralExp sle = (StructLiteralExp)ex.value;
                Ref<VarDeclaration> vf = ref(e.var.value.isVarDeclaration());
                if ((vf.value != null) && !vf.value.overlapped.value)
                {
                    ex.value = sle.getField(e.type.value, vf.value.offset.value);
                    if ((ex.value != null) && !CTFEExp.isCantExp(ex.value))
                    {
                        this.ret.value = ex.value;
                        return ;
                    }
                }
            }
        }

        public  void visit(NewExp e) {
            this.expOptimize(thisexp, 0, false);
            if (e.newargs.value != null)
            {
                {
                    IntRef i = ref(0);
                    for (; (i.value < (e.newargs.value.get()).length.value);i.value++){
                        this.expOptimize((e.newargs.value.get()).get(i.value), 0, false);
                    }
                }
            }
            if (e.arguments.value != null)
            {
                {
                    IntRef i = ref(0);
                    for (; (i.value < (e.arguments.value.get()).length.value);i.value++){
                        this.expOptimize((e.arguments.value.get()).get(i.value), 0, false);
                    }
                }
            }
        }

        public  void visit(CallExp e) {
            if (this.expOptimize(e1, this.result.value, false))
            {
                return ;
            }
            if (e.arguments.value != null)
            {
                Ref<Type> t1 = ref(e.e1.value.type.value.toBasetype());
                if (((t1.value.ty.value & 0xFF) == ENUMTY.Tdelegate))
                {
                    t1.value = t1.value.nextOf();
                }
                assert(((t1.value.ty.value & 0xFF) == ENUMTY.Tfunction));
                TypeFunction tf = (TypeFunction)t1.value;
                {
                    IntRef i = ref(0);
                    for (; (i.value < (e.arguments.value.get()).length.value);i.value++){
                        Ref<Parameter> p = ref(tf.parameterList.get(i.value));
                        Ref<Boolean> keep = ref((p.value != null) && ((p.value.storageClass.value & 2101248L) != 0L));
                        this.expOptimize((e.arguments.value.get()).get(i.value), 0, keep.value);
                    }
                }
            }
        }

        public  void visit(CastExp e) {
            assert(e.type.value != null);
            Ref<Byte> op1 = ref(e.e1.value.op.value);
            Ref<Expression> e1old = ref(e.e1.value);
            if (this.expOptimize(e1, this.result.value, false))
            {
                return ;
            }
            e.e1.value = fromConstInitializer(this.result.value, e.e1.value);
            if ((pequals(e.e1.value, e1old.value)) && ((e.e1.value.op.value & 0xFF) == 47) && ((e.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tpointer) && ((e.e1.value.type.value.toBasetype().ty.value & 0xFF) != ENUMTY.Tsarray))
            {
                return ;
            }
            if (((e.e1.value.op.value & 0xFF) == 121) || ((e.e1.value.op.value & 0xFF) == 47) && ((e.type.value.ty.value & 0xFF) == ENUMTY.Tpointer) || ((e.type.value.ty.value & 0xFF) == ENUMTY.Tarray))
            {
                Ref<Long> esz = ref(e.type.value.nextOf().size(e.loc.value));
                Ref<Long> e1sz = ref(e.e1.value.type.value.toBasetype().nextOf().size(e.e1.value.loc.value));
                if ((esz.value == -1L) || (e1sz.value == -1L))
                {
                    this.error();
                    return ;
                }
                if ((e1sz.value == esz.value))
                {
                    if (((e.type.value.nextOf().ty.value & 0xFF) == ENUMTY.Tvoid))
                    {
                        return ;
                    }
                    this.ret.value = e.e1.value.castTo(null, e.type.value);
                    return ;
                }
            }
            if (((e.e1.value.op.value & 0xFF) == 49) && (e.e1.value.type.value.implicitConvTo(e.type.value) >= MATCH.constant))
            {
            /*L1:*/
                this.ret.value = (pequals(e1old.value, e.e1.value)) ? e.e1.value.copy() : e.e1.value;
                this.ret.value.type.value = e.type.value;
                return ;
            }
            if (((op1.value & 0xFF) != 47) && ((e.e1.value.op.value & 0xFF) == 47))
            {
                this.ret.value = e.e1.value.castTo(null, e.to.value);
                return ;
            }
            if (((e.e1.value.op.value & 0xFF) == 13) && ((e.type.value.ty.value & 0xFF) == ENUMTY.Tpointer) || ((e.type.value.ty.value & 0xFF) == ENUMTY.Tclass) || ((e.type.value.ty.value & 0xFF) == ENUMTY.Tarray))
            {
                /*goto L1*/throw Dispatch0.INSTANCE;
            }
            if (((e.type.value.ty.value & 0xFF) == ENUMTY.Tclass) && ((e.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tclass))
            {
                Ref<ClassDeclaration> cdfrom = ref(e.e1.value.type.value.isClassHandle());
                Ref<ClassDeclaration> cdto = ref(e.type.value.isClassHandle());
                if ((pequals(cdto.value, ClassDeclaration.object.value)) && (cdfrom.value.isInterfaceDeclaration() == null))
                {
                    /*goto L1*/throw Dispatch0.INSTANCE;
                }
                cdfrom.value.size(e.loc.value);
                assert((cdfrom.value.sizeok.value == Sizeok.done));
                assert((cdto.value.sizeok.value == Sizeok.done) || !cdto.value.isBaseOf(cdfrom.value, null));
                IntRef offset = ref(0);
                if (cdto.value.isBaseOf(cdfrom.value, ptr(offset)) && (offset.value == 0))
                {
                    /*goto L1*/throw Dispatch0.INSTANCE;
                }
            }
            if (e.to.value.mutableOf().constOf().equals(e.e1.value.type.value.mutableOf().constOf()))
            {
                /*goto L1*/throw Dispatch0.INSTANCE;
            }
            if (e.e1.value.isConst() != 0)
            {
                if (((e.e1.value.op.value & 0xFF) == 25))
                {
                    if (((e.type.value.toBasetype().ty.value & 0xFF) != ENUMTY.Tsarray))
                    {
                        Ref<Long> esz = ref(e.type.value.size(e.loc.value));
                        Ref<Long> e1sz = ref(e.e1.value.type.value.size(e.e1.value.loc.value));
                        if ((esz.value == -1L) || (e1sz.value == -1L))
                        {
                            this.error();
                            return ;
                        }
                        if ((esz.value == e1sz.value))
                        {
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        }
                    }
                    return ;
                }
                if (((e.to.value.toBasetype().ty.value & 0xFF) != ENUMTY.Tvoid))
                {
                    if (e.e1.value.type.value.equals(e.type.value) && e.type.value.equals(e.to.value))
                    {
                        this.ret.value = e.e1.value;
                    }
                    else
                    {
                        this.ret.value = Cast(e.loc.value, e.type.value, e.to.value, e.e1.value).copy();
                    }
                }
            }
        }

        public  void visit(BinExp e) {
            Ref<BinExp> e_ref = ref(e);
            Ref<Boolean> e2only = ref(((e_ref.value.op.value & 0xFF) == 95) || ((e_ref.value.op.value & 0xFF) == 96));
            if (e2only.value ? this.expOptimize(e2, this.result.value, false) : this.binOptimize(e_ref.value, this.result.value))
            {
                return ;
            }
            if (((e_ref.value.op.value & 0xFF) == 66) || ((e_ref.value.op.value & 0xFF) == 67) || ((e_ref.value.op.value & 0xFF) == 69))
            {
                if ((e_ref.value.e2.value.isConst() == 1))
                {
                    Ref<Long> i2 = ref((long)e_ref.value.e2.value.toInteger());
                    Ref<Long> sz = ref(e_ref.value.e1.value.type.value.size(e_ref.value.e1.value.loc.value));
                    assert((sz.value != -1L));
                    sz.value *= 8L;
                    if ((i2.value < 0L) || ((long)i2.value >= sz.value))
                    {
                        e_ref.value.error(new BytePtr("shift assign by %lld is outside the range `0..%llu`"), i2.value, sz.value - 1L);
                        this.error();
                        return ;
                    }
                }
            }
        }

        public  void visit(AddExp e) {
            Ref<AddExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, this.result.value))
            {
                return ;
            }
            if ((e_ref.value.e1.value.isConst() != 0) && (e_ref.value.e2.value.isConst() != 0))
            {
                if (((e_ref.value.e1.value.op.value & 0xFF) == 25) && ((e_ref.value.e2.value.op.value & 0xFF) == 25))
                {
                    return ;
                }
                this.ret.value = Add(e_ref.value.loc.value, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
            }
        }

        public  void visit(MinExp e) {
            Ref<MinExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, this.result.value))
            {
                return ;
            }
            if ((e_ref.value.e1.value.isConst() != 0) && (e_ref.value.e2.value.isConst() != 0))
            {
                if (((e_ref.value.e2.value.op.value & 0xFF) == 25))
                {
                    return ;
                }
                this.ret.value = Min(e_ref.value.loc.value, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
            }
        }

        public  void visit(MulExp e) {
            Ref<MulExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, this.result.value))
            {
                return ;
            }
            if ((e_ref.value.e1.value.isConst() == 1) && (e_ref.value.e2.value.isConst() == 1))
            {
                this.ret.value = Mul(e_ref.value.loc.value, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
            }
        }

        public  void visit(DivExp e) {
            Ref<DivExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, this.result.value))
            {
                return ;
            }
            if ((e_ref.value.e1.value.isConst() == 1) && (e_ref.value.e2.value.isConst() == 1))
            {
                this.ret.value = Div(e_ref.value.loc.value, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
            }
        }

        public  void visit(ModExp e) {
            Ref<ModExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, this.result.value))
            {
                return ;
            }
            if ((e_ref.value.e1.value.isConst() == 1) && (e_ref.value.e2.value.isConst() == 1))
            {
                this.ret.value = Mod(e_ref.value.loc.value, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
            }
        }

        public  void shift_optimize(BinExp e, Function4<Loc,Type,Expression,Expression,UnionExp> shift) {
            Ref<BinExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, this.result.value))
            {
                return ;
            }
            if ((e_ref.value.e2.value.isConst() == 1))
            {
                Ref<Long> i2 = ref((long)e_ref.value.e2.value.toInteger());
                Ref<Long> sz = ref(e_ref.value.e1.value.type.value.size(e_ref.value.e1.value.loc.value));
                assert((sz.value != -1L));
                sz.value *= 8L;
                if ((i2.value < 0L) || ((long)i2.value >= sz.value))
                {
                    e_ref.value.error(new BytePtr("shift by %lld is outside the range `0..%llu`"), i2.value, sz.value - 1L);
                    this.error();
                    return ;
                }
                if ((e_ref.value.e1.value.isConst() == 1))
                {
                    this.ret.value = (shift).invoke(e_ref.value.loc.value, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
                }
            }
        }

        public  void visit(ShlExp e) {
            Ref<ShlExp> e_ref = ref(e);
            this.shift_optimize(e_ref.value, optimize::Shl);
        }

        public  void visit(ShrExp e) {
            Ref<ShrExp> e_ref = ref(e);
            this.shift_optimize(e_ref.value, optimize::Shr);
        }

        public  void visit(UshrExp e) {
            Ref<UshrExp> e_ref = ref(e);
            this.shift_optimize(e_ref.value, optimize::Ushr);
        }

        public  void visit(AndExp e) {
            Ref<AndExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, this.result.value))
            {
                return ;
            }
            if ((e_ref.value.e1.value.isConst() == 1) && (e_ref.value.e2.value.isConst() == 1))
            {
                this.ret.value = And(e_ref.value.loc.value, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
            }
        }

        public  void visit(OrExp e) {
            Ref<OrExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, this.result.value))
            {
                return ;
            }
            if ((e_ref.value.e1.value.isConst() == 1) && (e_ref.value.e2.value.isConst() == 1))
            {
                this.ret.value = Or(e_ref.value.loc.value, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
            }
        }

        public  void visit(XorExp e) {
            Ref<XorExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, this.result.value))
            {
                return ;
            }
            if ((e_ref.value.e1.value.isConst() == 1) && (e_ref.value.e2.value.isConst() == 1))
            {
                this.ret.value = Xor(e_ref.value.loc.value, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
            }
        }

        public  void visit(PowExp e) {
            Ref<PowExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, this.result.value))
            {
                return ;
            }
            if (((e_ref.value.e1.value.op.value & 0xFF) == 135) && (e_ref.value.e1.value.toInteger() == 1L) || ((e_ref.value.e1.value.op.value & 0xFF) == 140) && (e_ref.value.e1.value.toReal() == CTFloat.one.value))
            {
                this.ret.value = new CommaExp(e_ref.value.loc.value, e_ref.value.e2.value, e_ref.value.e1.value, true);
                this.ret.value.type.value = e_ref.value.type.value;
                return ;
            }
            if (e_ref.value.e2.value.type.value.isintegral() && ((e_ref.value.e1.value.op.value & 0xFF) == 135) && ((long)e_ref.value.e1.value.toInteger() == -1L))
            {
                this.ret.value = new AndExp(e_ref.value.loc.value, e_ref.value.e2.value, new IntegerExp(e_ref.value.loc.value, 1L, e_ref.value.e2.value.type.value));
                this.ret.value.type.value = e_ref.value.e2.value.type.value;
                this.ret.value = new CondExp(e_ref.value.loc.value, this.ret.value, new IntegerExp(e_ref.value.loc.value, -1L, e_ref.value.type.value), new IntegerExp(e_ref.value.loc.value, 1L, e_ref.value.type.value));
                this.ret.value.type.value = e_ref.value.type.value;
                return ;
            }
            if (((e_ref.value.e2.value.op.value & 0xFF) == 135) && (e_ref.value.e2.value.toInteger() == 0L) || ((e_ref.value.e2.value.op.value & 0xFF) == 140) && (e_ref.value.e2.value.toReal() == CTFloat.zero.value))
            {
                if (e_ref.value.e1.value.type.value.isintegral())
                {
                    this.ret.value = new IntegerExp(e_ref.value.loc.value, 1L, e_ref.value.e1.value.type.value);
                }
                else
                {
                    this.ret.value = new RealExp(e_ref.value.loc.value, CTFloat.one.value, e_ref.value.e1.value.type.value);
                }
                this.ret.value = new CommaExp(e_ref.value.loc.value, e_ref.value.e1.value, this.ret.value, true);
                this.ret.value.type.value = e_ref.value.type.value;
                return ;
            }
            if (((e_ref.value.e2.value.op.value & 0xFF) == 135) && (e_ref.value.e2.value.toInteger() == 1L) || ((e_ref.value.e2.value.op.value & 0xFF) == 140) && (e_ref.value.e2.value.toReal() == CTFloat.one.value))
            {
                this.ret.value = e_ref.value.e1.value;
                return ;
            }
            if (((e_ref.value.e2.value.op.value & 0xFF) == 140) && (e_ref.value.e2.value.toReal() == CTFloat.minusone.value))
            {
                this.ret.value = new DivExp(e_ref.value.loc.value, new RealExp(e_ref.value.loc.value, CTFloat.one.value, e_ref.value.e2.value.type.value), e_ref.value.e1.value);
                this.ret.value.type.value = e_ref.value.type.value;
                return ;
            }
            if (e_ref.value.e1.value.type.value.isintegral() && ((e_ref.value.e2.value.op.value & 0xFF) == 135) && ((long)e_ref.value.e2.value.toInteger() < 0L))
            {
                e_ref.value.error(new BytePtr("cannot raise `%s` to a negative integer power. Did you mean `(cast(real)%s)^^%s` ?"), e_ref.value.e1.value.type.value.toBasetype().toChars(), e_ref.value.e1.value.toChars(), e_ref.value.e2.value.toChars());
                this.error();
                return ;
            }
            if (((e_ref.value.e2.value.op.value & 0xFF) == 140))
            {
                if ((e_ref.value.e2.value.toReal() == (double)(long)e_ref.value.e2.value.toReal()))
                {
                    e_ref.value.e2.value = new IntegerExp(e_ref.value.loc.value, e_ref.value.e2.value.toInteger(), Type.tint64.value);
                }
            }
            if ((e_ref.value.e1.value.isConst() == 1) && (e_ref.value.e2.value.isConst() == 1))
            {
                Ref<Expression> ex = ref(Pow(e_ref.value.loc.value, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy());
                if (!CTFEExp.isCantExp(ex.value))
                {
                    this.ret.value = ex.value;
                    return ;
                }
            }
            if (((e_ref.value.e1.value.op.value & 0xFF) == 135) && (e_ref.value.e1.value.toInteger() > 0L) && ((e_ref.value.e1.value.toInteger() - 1L & e_ref.value.e1.value.toInteger()) == 0) && e_ref.value.e2.value.type.value.isintegral() && e_ref.value.e2.value.type.value.isunsigned())
            {
                Ref<Long> i = ref(e_ref.value.e1.value.toInteger());
                Ref<Long> mul = ref(1L);
                for (; ((i.value >>= 1) > 1L);) {
                    mul.value++;
                }
                Ref<Expression> shift = ref(new MulExp(e_ref.value.loc.value, e_ref.value.e2.value, new IntegerExp(e_ref.value.loc.value, mul.value, e_ref.value.e2.value.type.value)));
                shift.value.type.value = e_ref.value.e2.value.type.value;
                shift.value = shift.value.castTo(null, Type.tshiftcnt.value);
                this.ret.value = new ShlExp(e_ref.value.loc.value, new IntegerExp(e_ref.value.loc.value, 1L, e_ref.value.e1.value.type.value), shift.value);
                this.ret.value.type.value = e_ref.value.type.value;
                return ;
            }
        }

        public  void visit(CommaExp e) {
            this.expOptimize(e1, 0, false);
            this.expOptimize(e2, this.result.value, this.keepLvalue.value);
            if (((this.ret.value.op.value & 0xFF) == 127))
            {
                return ;
            }
            if ((e.e1.value == null) || ((e.e1.value.op.value & 0xFF) == 135) || ((e.e1.value.op.value & 0xFF) == 140) || !hasSideEffect(e.e1.value))
            {
                this.ret.value = e.e2.value;
                if (this.ret.value != null)
                {
                    this.ret.value.type.value = e.type.value;
                }
            }
        }

        public  void visit(ArrayLengthExp e) {
            Ref<ArrayLengthExp> e_ref = ref(e);
            if (this.unaOptimize(e_ref.value, 1))
            {
                return ;
            }
            if (((e_ref.value.e1.value.op.value & 0xFF) == 26))
            {
                Ref<VarDeclaration> v = ref(((VarExp)e_ref.value.e1.value).var.value.isVarDeclaration());
                if ((v.value != null) && ((v.value.storage_class.value & 1L) != 0) && ((v.value.storage_class.value & 1048576L) != 0) && (v.value._init.value != null))
                {
                    {
                        Ref<Expression> ci = ref(v.value.getConstInitializer(true));
                        if ((ci.value) != null)
                        {
                            e_ref.value.e1.value = ci.value;
                        }
                    }
                }
            }
            if (((e_ref.value.e1.value.op.value & 0xFF) == 121) || ((e_ref.value.e1.value.op.value & 0xFF) == 47) || ((e_ref.value.e1.value.op.value & 0xFF) == 48) || ((e_ref.value.e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                this.ret.value = ArrayLength(e_ref.value.type.value, e_ref.value.e1.value).copy();
            }
        }

        public  void visit(EqualExp e) {
            Ref<EqualExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, 0))
            {
                return ;
            }
            Ref<Expression> e1 = ref(fromConstInitializer(this.result.value, e_ref.value.e1.value));
            Ref<Expression> e2 = ref(fromConstInitializer(this.result.value, e_ref.value.e2.value));
            if (((e1.value.op.value & 0xFF) == 127))
            {
                this.ret.value = e1.value;
                return ;
            }
            if (((e2.value.op.value & 0xFF) == 127))
            {
                this.ret.value = e2.value;
                return ;
            }
            this.ret.value = Equal(e_ref.value.op.value, e_ref.value.loc.value, e_ref.value.type.value, e1.value, e2.value).copy();
            if (CTFEExp.isCantExp(this.ret.value))
            {
                this.ret.value = e_ref.value;
            }
        }

        public  void visit(IdentityExp e) {
            Ref<IdentityExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, 0))
            {
                return ;
            }
            if ((e_ref.value.e1.value.isConst() != 0) && (e_ref.value.e2.value.isConst() != 0) || ((e_ref.value.e1.value.op.value & 0xFF) == 13) && ((e_ref.value.e2.value.op.value & 0xFF) == 13))
            {
                this.ret.value = Identity(e_ref.value.op.value, e_ref.value.loc.value, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
                if (CTFEExp.isCantExp(this.ret.value))
                {
                    this.ret.value = e_ref.value;
                }
            }
        }

        public  void visit(IndexExp e) {
            Ref<IndexExp> e_ref = ref(e);
            if (this.expOptimize(e1, this.result.value & 1, false))
            {
                return ;
            }
            Ref<Expression> ex = ref(fromConstInitializer(this.result.value, e_ref.value.e1.value));
            setLengthVarIfKnown(e_ref.value.lengthVar.value, ex.value);
            if (this.expOptimize(e2, 0, false))
            {
                return ;
            }
            if (this.keepLvalue.value)
            {
                return ;
            }
            this.ret.value = Index(e_ref.value.type.value, ex.value, e_ref.value.e2.value).copy();
            if (CTFEExp.isCantExp(this.ret.value))
            {
                this.ret.value = e_ref.value;
            }
        }

        public  void visit(SliceExp e) {
            Ref<SliceExp> e_ref = ref(e);
            if (this.expOptimize(e1, this.result.value & 1, false))
            {
                return ;
            }
            if (e_ref.value.lwr.value == null)
            {
                if (((e_ref.value.e1.value.op.value & 0xFF) == 121))
                {
                    Type t = e_ref.value.e1.value.type.value.toBasetype();
                    {
                        Ref<Type> tn = ref(t.nextOf());
                        if ((tn.value) != null)
                        {
                            this.ret.value = e_ref.value.e1.value.castTo(null, tn.value.arrayOf());
                        }
                    }
                }
            }
            else
            {
                e_ref.value.e1.value = fromConstInitializer(this.result.value, e_ref.value.e1.value);
                setLengthVarIfKnown(e_ref.value.lengthVar.value, e_ref.value.e1.value);
                this.expOptimize(lwr, 0, false);
                this.expOptimize(upr, 0, false);
                if (((this.ret.value.op.value & 0xFF) == 127))
                {
                    return ;
                }
                this.ret.value = Slice(e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.lwr.value, e_ref.value.upr.value).copy();
                if (CTFEExp.isCantExp(this.ret.value))
                {
                    this.ret.value = e_ref.value;
                }
            }
            if (((this.ret.value.op.value & 0xFF) == 121))
            {
                e_ref.value.e1.value = this.ret.value;
                e_ref.value.lwr.value = null;
                e_ref.value.upr.value = null;
                this.ret.value = e_ref.value;
            }
        }

        public  void visit(LogicalExp e) {
            if (this.expOptimize(e1, 0, false))
            {
                return ;
            }
            Ref<Boolean> oror = ref((e.op.value & 0xFF) == 102);
            if (e.e1.value.isBool(oror.value))
            {
                this.ret.value = new IntegerExp(e.loc.value, (oror.value ? 1 : 0), Type.tbool.value);
                this.ret.value = Expression.combine(e.e1.value, this.ret.value);
                if (((e.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tvoid))
                {
                    this.ret.value = new CastExp(e.loc.value, this.ret.value, Type.tvoid.value);
                    this.ret.value.type.value = e.type.value;
                }
                this.ret.value = Expression_optimize(this.ret.value, this.result.value, false);
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
                    Ref<Boolean> n1 = ref(e.e1.value.isBool(true));
                    Ref<Boolean> n2 = ref(e.e2.value.isBool(true));
                    this.ret.value = new IntegerExp(e.loc.value, oror.value ? ((n1.value || n2.value) ? 1 : 0) : ((n1.value && n2.value) ? 1 : 0), e.type.value);
                }
                else if (e.e1.value.isBool(!oror.value))
                {
                    if (((e.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tvoid))
                    {
                        this.ret.value = e.e2.value;
                    }
                    else
                    {
                        this.ret.value = new CastExp(e.loc.value, e.e2.value, e.type.value);
                        this.ret.value.type.value = e.type.value;
                    }
                }
            }
        }

        public  void visit(CmpExp e) {
            Ref<CmpExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, 0))
            {
                return ;
            }
            Expression e1 = fromConstInitializer(this.result.value, e_ref.value.e1.value);
            Expression e2 = fromConstInitializer(this.result.value, e_ref.value.e2.value);
            this.ret.value = Cmp(e_ref.value.op.value, e_ref.value.loc.value, e_ref.value.type.value, e1, e2).copy();
            if (CTFEExp.isCantExp(this.ret.value))
            {
                this.ret.value = e_ref.value;
            }
        }

        public  void visit(CatExp e) {
            Ref<CatExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, this.result.value))
            {
                return ;
            }
            if (((e_ref.value.e1.value.op.value & 0xFF) == 70))
            {
                CatExp ce1 = (CatExp)e_ref.value.e1.value;
                Ref<CatExp> cex = ref(new CatExp(e_ref.value.loc.value, ce1.e2.value, e_ref.value.e2.value));
                cex.value.type.value = e_ref.value.type.value;
                Ref<Expression> ex = ref(Expression_optimize(cex.value, this.result.value, false));
                if ((!pequals(ex.value, cex.value)))
                {
                    e_ref.value.e1.value = ce1.e1.value;
                    e_ref.value.e2.value = ex.value;
                }
            }
            if (((e_ref.value.e1.value.op.value & 0xFF) == 31))
            {
                SliceExp se1 = (SliceExp)e_ref.value.e1.value;
                if (((se1.e1.value.op.value & 0xFF) == 121) && (se1.lwr.value == null))
                {
                    e_ref.value.e1.value = se1.e1.value;
                }
            }
            if (((e_ref.value.e2.value.op.value & 0xFF) == 31))
            {
                SliceExp se2 = (SliceExp)e_ref.value.e2.value;
                if (((se2.e1.value.op.value & 0xFF) == 121) && (se2.lwr.value == null))
                {
                    e_ref.value.e2.value = se2.e1.value;
                }
            }
            this.ret.value = Cat(e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
            if (CTFEExp.isCantExp(this.ret.value))
            {
                this.ret.value = e_ref.value;
            }
        }

        public  void visit(CondExp e) {
            if (this.expOptimize(econd, 0, false))
            {
                return ;
            }
            if (e.econd.value.isBool(true))
            {
                this.ret.value = Expression_optimize(e.e1.value, this.result.value, this.keepLvalue.value);
            }
            else if (e.econd.value.isBool(false))
            {
                this.ret.value = Expression_optimize(e.e2.value, this.result.value, this.keepLvalue.value);
            }
            else
            {
                this.expOptimize(e1, this.result.value, this.keepLvalue.value);
                this.expOptimize(e2, this.result.value, this.keepLvalue.value);
            }
        }


        public OptimizeVisitor() {}
    }

    public static Expression expandVar(int result, VarDeclaration v) {
        IntRef result_ref = ref(result);
        Function1<Expression,Expression> initializerReturn = new Function1<Expression,Expression>(){
            public Expression invoke(Expression e) {
                Ref<Expression> e_ref = ref(e);
                if ((!pequals(e_ref.value.type.value, v.type.value)))
                {
                    e_ref.value = e_ref.value.castTo(null, v.type.value);
                }
                v.inuse.value++;
                e_ref.value = e_ref.value.optimize(result_ref.value, false);
                v.inuse.value--;
                return e_ref.value;
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
        if (v == null)
        {
            return nullReturn.invoke();
        }
        if ((v.originalType.value == null) && (v.semanticRun.value < PASS.semanticdone))
        {
            dsymbolSemantic(v, null);
        }
        if ((v.type.value != null) && v.isConst() || v.isImmutable() || ((v.storage_class.value & 8388608L) != 0))
        {
            Type tb = v.type.value.toBasetype();
            if (((v.storage_class.value & 8388608L) != 0) || tb.isscalar() || ((result_ref.value & 1) != 0) && ((tb.ty.value & 0xFF) != ENUMTY.Tsarray) && ((tb.ty.value & 0xFF) != ENUMTY.Tstruct))
            {
                if (v._init.value != null)
                {
                    if (v.inuse.value != 0)
                    {
                        if ((v.storage_class.value & 8388608L) != 0)
                        {
                            v.error(new BytePtr("recursive initialization of constant"));
                            return errorReturn.invoke();
                        }
                        return nullReturn.invoke();
                    }
                    Expression ei = v.getConstInitializer(true);
                    if (ei == null)
                    {
                        if ((v.storage_class.value & 8388608L) != 0)
                        {
                            v.error(new BytePtr("enum cannot be initialized with `%s`"), v._init.value.toChars());
                            return errorReturn.invoke();
                        }
                        return nullReturn.invoke();
                    }
                    if (((ei.op.value & 0xFF) == 95) || ((ei.op.value & 0xFF) == 96))
                    {
                        AssignExp ae = (AssignExp)ei;
                        ei = ae.e2.value;
                        if ((ei.isConst() == 1))
                        {
                        }
                        else if (((ei.op.value & 0xFF) == 121))
                        {
                            if (((result_ref.value & 1) == 0) && ((ei.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tpointer))
                            {
                                return nullReturn.invoke();
                            }
                        }
                        else
                        {
                            return nullReturn.invoke();
                        }
                        if ((pequals(ei.type.value, v.type.value)))
                        {
                        }
                        else if ((ei.implicitConvTo(v.type.value) >= MATCH.constant))
                        {
                            ei = ei.implicitCastTo(null, v.type.value);
                            ei = expressionSemantic(ei, null);
                        }
                        else
                        {
                            return nullReturn.invoke();
                        }
                    }
                    else if (((v.storage_class.value & 8388608L) == 0) && (ei.isConst() != 1) && ((ei.op.value & 0xFF) != 121) && ((ei.op.value & 0xFF) != 19))
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

    public static Expression fromConstInitializer(int result, Expression e1) {
        Expression e = e1;
        if (((e1.op.value & 0xFF) == 26))
        {
            VarExp ve = (VarExp)e1;
            VarDeclaration v = ve.var.value.isVarDeclaration();
            e = expandVar(result, v);
            if (e != null)
            {
                if (((e.op.value & 0xFF) == 99) && ((((CommaExp)e).e1.value.op.value & 0xFF) == 38))
                {
                    e = e1;
                }
                else if ((!pequals(e.type.value, e1.type.value)) && (e1.type.value != null) && ((e1.type.value.ty.value & 0xFF) != ENUMTY.Tident))
                {
                    e = e.copy();
                    e.type.value = e1.type.value;
                }
                e.loc.value = e1.loc.value.copy();
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
        {
            return ;
        }
        if ((lengthVar._init.value != null) && (lengthVar._init.value.isVoidInitializer() == null))
        {
            return ;
        }
        int len = 0;
        if (((arr.op.value & 0xFF) == 121))
        {
            len = ((StringExp)arr).len.value;
        }
        else if (((arr.op.value & 0xFF) == 47))
        {
            len = (((ArrayLiteralExp)arr).elements.value.get()).length.value;
        }
        else
        {
            Type t = arr.type.value.toBasetype();
            if (((t.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                len = (int)((TypeSArray)t).dim.value.toInteger();
            }
            else
            {
                return ;
            }
        }
        Expression dollar = new IntegerExp(Loc.initial.value, (long)len, Type.tsize_t.value);
        lengthVar._init.value = new ExpInitializer(Loc.initial.value, dollar);
        lengthVar.storage_class.value |= 5L;
    }

    public static void setLengthVarIfKnown(VarDeclaration lengthVar, Type type) {
        if (lengthVar == null)
        {
            return ;
        }
        if ((lengthVar._init.value != null) && (lengthVar._init.value.isVoidInitializer() == null))
        {
            return ;
        }
        int len = 0;
        Type t = type.toBasetype();
        if (((t.ty.value & 0xFF) == ENUMTY.Tsarray))
        {
            len = (int)((TypeSArray)t).dim.value.toInteger();
        }
        else
        {
            return ;
        }
        Expression dollar = new IntegerExp(Loc.initial.value, (long)len, Type.tsize_t.value);
        lengthVar._init.value = new ExpInitializer(Loc.initial.value, dollar);
        lengthVar.storage_class.value |= 5L;
    }

    public static Expression Expression_optimize(Expression e, int result, boolean keepLvalue) {
        // skipping duplicate class OptimizeVisitor
        OptimizeVisitor v = new OptimizeVisitor(e, result, keepLvalue);
        for (; 1 != 0;){
            Expression ex = v.ret.value;
            ex.accept(v);
            if ((pequals(ex, v.ret.value)))
            {
                break;
            }
        }
        return v.ret.value;
    }

}
