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
        public  OptimizeVisitor(Expression e, int result, boolean keepLvalue) {
            Ref<Expression> e_ref = ref(e);
            this.ret = e_ref.value;
            this.result = result;
            this.keepLvalue = keepLvalue;
        }

        public  void error() {
            this.ret = new ErrorExp();
        }

        public  boolean expOptimize(Ref<Expression> e, int flags, boolean keepLvalue) {
            IntRef flags_ref = ref(flags);
            Ref<Boolean> keepLvalue_ref = ref(keepLvalue);
            if (e.value == null)
                return false;
            Ref<Expression> ex = ref(Expression_optimize(e.value, flags_ref.value, keepLvalue_ref.value));
            if (((ex.value.op & 0xFF) == 127))
            {
                this.ret = ex.value;
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
            Ref<UnaExp> e_ref = ref(e);
            IntRef flags_ref = ref(flags);
            return this.expOptimize(e_ref.value.e1, flags_ref.value, false);
        }

        public  boolean binOptimize(BinExp e, int flags) {
            Ref<BinExp> e_ref = ref(e);
            IntRef flags_ref = ref(flags);
            this.expOptimize(e_ref.value.e1.value, flags_ref.value, false);
            this.expOptimize(e_ref.value.e2.value, flags_ref.value, false);
            return (this.ret.op & 0xFF) == 127;
        }

        public  void visit(Expression e) {
        }

        public  void visit(VarExp e) {
            Ref<VarExp> e_ref = ref(e);
            if (this.keepLvalue)
            {
                Ref<VarDeclaration> v = ref(e_ref.value.var.isVarDeclaration());
                if ((v.value != null) && ((v.value.storage_class & 8388608L) == 0))
                    return ;
            }
            this.ret = fromConstInitializer(this.result, e_ref.value);
        }

        public  void visit(TupleExp e) {
            Ref<TupleExp> e_ref = ref(e);
            this.expOptimize(e_ref.value.e0, 0, false);
            {
                IntRef i = ref(0);
                for (; (i.value < (e_ref.value.exps.get()).length);i.value++){
                    this.expOptimize((e_ref.value.exps.get()).get(i.value), 0, false);
                }
            }
        }

        public  void visit(ArrayLiteralExp e) {
            Ref<ArrayLiteralExp> e_ref = ref(e);
            if (e_ref.value.elements != null)
            {
                this.expOptimize(e_ref.value.basis, this.result & 1, false);
                {
                    IntRef i = ref(0);
                    for (; (i.value < (e_ref.value.elements.get()).length);i.value++){
                        this.expOptimize((e_ref.value.elements.get()).get(i.value), this.result & 1, false);
                    }
                }
            }
        }

        public  void visit(AssocArrayLiteralExp e) {
            Ref<AssocArrayLiteralExp> e_ref = ref(e);
            assert(((e_ref.value.keys.get()).length == (e_ref.value.values.get()).length));
            {
                IntRef i = ref(0);
                for (; (i.value < (e_ref.value.keys.get()).length);i.value++){
                    this.expOptimize((e_ref.value.keys.get()).get(i.value), this.result & 1, false);
                    this.expOptimize((e_ref.value.values.get()).get(i.value), this.result & 1, false);
                }
            }
        }

        public  void visit(StructLiteralExp e) {
            Ref<StructLiteralExp> e_ref = ref(e);
            if ((e_ref.value.stageflags & 4) != 0)
                return ;
            IntRef old = ref(e_ref.value.stageflags);
            e_ref.value.stageflags |= 4;
            if (e_ref.value.elements != null)
            {
                {
                    IntRef i = ref(0);
                    for (; (i.value < (e_ref.value.elements.get()).length);i.value++){
                        this.expOptimize((e_ref.value.elements.get()).get(i.value), this.result & 1, false);
                    }
                }
            }
            e_ref.value.stageflags = old.value;
        }

        public  void visit(UnaExp e) {
            Ref<UnaExp> e_ref = ref(e);
            if (this.unaOptimize(e_ref.value, this.result))
                return ;
        }

        public  void visit(NegExp e) {
            Ref<NegExp> e_ref = ref(e);
            if (this.unaOptimize(e_ref.value, this.result))
                return ;
            if ((e_ref.value.e1.isConst() == 1))
            {
                this.ret = Neg(e_ref.value.type.value, e_ref.value.e1).copy();
            }
        }

        public  void visit(ComExp e) {
            Ref<ComExp> e_ref = ref(e);
            if (this.unaOptimize(e_ref.value, this.result))
                return ;
            if ((e_ref.value.e1.isConst() == 1))
            {
                this.ret = Com(e_ref.value.type.value, e_ref.value.e1).copy();
            }
        }

        public  void visit(NotExp e) {
            Ref<NotExp> e_ref = ref(e);
            if (this.unaOptimize(e_ref.value, this.result))
                return ;
            if ((e_ref.value.e1.isConst() == 1))
            {
                this.ret = Not(e_ref.value.type.value, e_ref.value.e1).copy();
            }
        }

        public  void visit(SymOffExp e) {
            Ref<SymOffExp> e_ref = ref(e);
            assert(e_ref.value.var != null);
        }

        public  void visit(AddrExp e) {
            Ref<AddrExp> e_ref = ref(e);
            if (((e_ref.value.e1.op & 0xFF) == 99))
            {
                Ref<CommaExp> ce = ref((CommaExp)e_ref.value.e1);
                Ref<AddrExp> ae = ref(new AddrExp(e_ref.value.loc, ce.value.e2.value, e_ref.value.type.value));
                this.ret = new CommaExp(ce.value.loc, ce.value.e1.value, ae.value, true);
                this.ret.type.value = e_ref.value.type.value;
                return ;
            }
            if (this.expOptimize(e_ref.value.e1, this.result, true))
                return ;
            if (((e_ref.value.e1.op & 0xFF) == 24))
            {
                Ref<Expression> ex = ref(((PtrExp)e_ref.value.e1).e1);
                if (e_ref.value.type.value.equals(ex.value.type.value))
                    this.ret = ex.value;
                else if (e_ref.value.type.value.toBasetype().equivalent(ex.value.type.value.toBasetype()))
                {
                    this.ret = ex.value.copy();
                    this.ret.type.value = e_ref.value.type.value;
                }
                return ;
            }
            if (((e_ref.value.e1.op & 0xFF) == 26))
            {
                Ref<VarExp> ve = ref((VarExp)e_ref.value.e1);
                if (!ve.value.var.isOut() && !ve.value.var.isRef() && !ve.value.var.isImportedSymbol())
                {
                    this.ret = new SymOffExp(e_ref.value.loc, ve.value.var, 0L, ve.value.hasOverloads);
                    this.ret.type.value = e_ref.value.type.value;
                    return ;
                }
            }
            if (((e_ref.value.e1.op & 0xFF) == 62))
            {
                Ref<IndexExp> ae = ref((IndexExp)e_ref.value.e1);
                if (((ae.value.e2.value.op & 0xFF) == 135) && ((ae.value.e1.value.op & 0xFF) == 26))
                {
                    Ref<Long> index = ref((long)ae.value.e2.value.toInteger());
                    Ref<VarExp> ve = ref((VarExp)ae.value.e1.value);
                    if (((ve.value.type.value.ty & 0xFF) == ENUMTY.Tsarray) && !ve.value.var.isImportedSymbol())
                    {
                        Ref<TypeSArray> ts = ref((TypeSArray)ve.value.type.value);
                        Ref<Long> dim = ref((long)ts.value.dim.toInteger());
                        if ((index.value < 0L) || (index.value >= dim.value))
                        {
                            e_ref.value.error(new BytePtr("array index %lld is out of bounds `[0..%lld]`"), index.value, dim.value);
                            this.error();
                            return ;
                        }
                        Ref<Boolean> overflow = ref(false);
                        Ref<Long> offset = ref(mulu((long)index.value, ts.value.nextOf().size(e_ref.value.loc), overflow));
                        if (overflow.value)
                        {
                            e_ref.value.error(new BytePtr("array offset overflow"));
                            this.error();
                            return ;
                        }
                        this.ret = new SymOffExp(e_ref.value.loc, ve.value.var, offset.value, true);
                        this.ret.type.value = e_ref.value.type.value;
                        return ;
                    }
                }
            }
        }

        public  void visit(PtrExp e) {
            Ref<PtrExp> e_ref = ref(e);
            if (this.expOptimize(e_ref.value.e1, this.result, false))
                return ;
            if (((e_ref.value.e1.op & 0xFF) == 19))
            {
                Ref<Expression> ex = ref(((AddrExp)e_ref.value.e1).e1);
                if (e_ref.value.type.value.equals(ex.value.type.value))
                    this.ret = ex.value;
                else if (e_ref.value.type.value.toBasetype().equivalent(ex.value.type.value.toBasetype()))
                {
                    this.ret = ex.value.copy();
                    this.ret.type.value = e_ref.value.type.value;
                }
            }
            if (this.keepLvalue)
                return ;
            if (((e_ref.value.e1.op & 0xFF) == 74))
            {
                Ref<Expression> ex = ref(Ptr(e_ref.value.type.value, e_ref.value.e1).copy());
                if (!CTFEExp.isCantExp(ex.value))
                {
                    this.ret = ex.value;
                    return ;
                }
            }
            if (((e_ref.value.e1.op & 0xFF) == 25))
            {
                Ref<SymOffExp> se = ref((SymOffExp)e_ref.value.e1);
                Ref<VarDeclaration> v = ref(se.value.var.isVarDeclaration());
                Ref<Expression> ex = ref(expandVar(this.result, v.value));
                if ((ex.value != null) && ((ex.value.op & 0xFF) == 49))
                {
                    Ref<StructLiteralExp> sle = ref((StructLiteralExp)ex.value);
                    ex.value = sle.value.getField(e_ref.value.type.value, (int)se.value.offset);
                    if ((ex.value != null) && !CTFEExp.isCantExp(ex.value))
                    {
                        this.ret = ex.value;
                        return ;
                    }
                }
            }
        }

        public  void visit(DotVarExp e) {
            Ref<DotVarExp> e_ref = ref(e);
            if (this.expOptimize(e_ref.value.e1, this.result, false))
                return ;
            if (this.keepLvalue)
                return ;
            Ref<Expression> ex = ref(e_ref.value.e1);
            if (((ex.value.op & 0xFF) == 26))
            {
                Ref<VarExp> ve = ref((VarExp)ex.value);
                Ref<VarDeclaration> v = ref(ve.value.var.isVarDeclaration());
                ex.value = expandVar(this.result, v.value);
            }
            if ((ex.value != null) && ((ex.value.op & 0xFF) == 49))
            {
                Ref<StructLiteralExp> sle = ref((StructLiteralExp)ex.value);
                Ref<VarDeclaration> vf = ref(e_ref.value.var.isVarDeclaration());
                if ((vf.value != null) && !vf.value.overlapped)
                {
                    ex.value = sle.value.getField(e_ref.value.type.value, vf.value.offset);
                    if ((ex.value != null) && !CTFEExp.isCantExp(ex.value))
                    {
                        this.ret = ex.value;
                        return ;
                    }
                }
            }
        }

        public  void visit(NewExp e) {
            Ref<NewExp> e_ref = ref(e);
            this.expOptimize(e_ref.value.thisexp, 0, false);
            if (e_ref.value.newargs != null)
            {
                {
                    IntRef i = ref(0);
                    for (; (i.value < (e_ref.value.newargs.get()).length);i.value++){
                        this.expOptimize((e_ref.value.newargs.get()).get(i.value), 0, false);
                    }
                }
            }
            if (e_ref.value.arguments != null)
            {
                {
                    IntRef i = ref(0);
                    for (; (i.value < (e_ref.value.arguments.get()).length);i.value++){
                        this.expOptimize((e_ref.value.arguments.get()).get(i.value), 0, false);
                    }
                }
            }
        }

        public  void visit(CallExp e) {
            Ref<CallExp> e_ref = ref(e);
            if (this.expOptimize(e_ref.value.e1, this.result, false))
                return ;
            if (e_ref.value.arguments != null)
            {
                Ref<Type> t1 = ref(e_ref.value.e1.type.value.toBasetype());
                if (((t1.value.ty & 0xFF) == ENUMTY.Tdelegate))
                    t1.value = t1.value.nextOf();
                assert(((t1.value.ty & 0xFF) == ENUMTY.Tfunction));
                Ref<TypeFunction> tf = ref((TypeFunction)t1.value);
                {
                    IntRef i = ref(0);
                    for (; (i.value < (e_ref.value.arguments.get()).length);i.value++){
                        Ref<Parameter> p = ref(tf.value.parameterList.get(i.value));
                        Ref<Boolean> keep = ref((p.value != null) && ((p.value.storageClass & 2101248L) != 0L));
                        this.expOptimize((e_ref.value.arguments.get()).get(i.value), 0, keep.value);
                    }
                }
            }
        }

        public  void visit(CastExp e) {
            Ref<CastExp> e_ref = ref(e);
            assert(e_ref.value.type.value != null);
            Ref<Byte> op1 = ref(e_ref.value.e1.op);
            Ref<Expression> e1old = ref(e_ref.value.e1);
            if (this.expOptimize(e_ref.value.e1, this.result, false))
                return ;
            e_ref.value.e1 = fromConstInitializer(this.result, e_ref.value.e1);
            if ((pequals(e_ref.value.e1, e1old.value)) && ((e_ref.value.e1.op & 0xFF) == 47) && ((e_ref.value.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tpointer) && ((e_ref.value.e1.type.value.toBasetype().ty & 0xFF) != ENUMTY.Tsarray))
            {
                return ;
            }
            if (((e_ref.value.e1.op & 0xFF) == 121) || ((e_ref.value.e1.op & 0xFF) == 47) && ((e_ref.value.type.value.ty & 0xFF) == ENUMTY.Tpointer) || ((e_ref.value.type.value.ty & 0xFF) == ENUMTY.Tarray))
            {
                Ref<Long> esz = ref(e_ref.value.type.value.nextOf().size(e_ref.value.loc));
                Ref<Long> e1sz = ref(e_ref.value.e1.type.value.toBasetype().nextOf().size(e_ref.value.e1.loc));
                if ((esz.value == -1L) || (e1sz.value == -1L))
                    this.error();
                    return ;
                if ((e1sz.value == esz.value))
                {
                    if (((e_ref.value.type.value.nextOf().ty & 0xFF) == ENUMTY.Tvoid))
                        return ;
                    this.ret = e_ref.value.e1.castTo(null, e_ref.value.type.value);
                    return ;
                }
            }
            if (((e_ref.value.e1.op & 0xFF) == 49) && (e_ref.value.e1.type.value.implicitConvTo(e_ref.value.type.value) >= MATCH.constant))
            {
            /*L1:*/
                this.ret = (pequals(e1old.value, e_ref.value.e1)) ? e_ref.value.e1.copy() : e_ref.value.e1;
                this.ret.type.value = e_ref.value.type.value;
                return ;
            }
            if (((op1.value & 0xFF) != 47) && ((e_ref.value.e1.op & 0xFF) == 47))
            {
                this.ret = e_ref.value.e1.castTo(null, e_ref.value.to);
                return ;
            }
            if (((e_ref.value.e1.op & 0xFF) == 13) && ((e_ref.value.type.value.ty & 0xFF) == ENUMTY.Tpointer) || ((e_ref.value.type.value.ty & 0xFF) == ENUMTY.Tclass) || ((e_ref.value.type.value.ty & 0xFF) == ENUMTY.Tarray))
            {
                /*goto L1*/throw Dispatch0.INSTANCE;
            }
            if (((e_ref.value.type.value.ty & 0xFF) == ENUMTY.Tclass) && ((e_ref.value.e1.type.value.ty & 0xFF) == ENUMTY.Tclass))
            {
                Ref<ClassDeclaration> cdfrom = ref(e_ref.value.e1.type.value.isClassHandle());
                Ref<ClassDeclaration> cdto = ref(e_ref.value.type.value.isClassHandle());
                if ((pequals(cdto.value, ClassDeclaration.object.value)) && (cdfrom.value.isInterfaceDeclaration() == null))
                    /*goto L1*/throw Dispatch0.INSTANCE;
                cdfrom.value.size(e_ref.value.loc);
                assert((cdfrom.value.sizeok == Sizeok.done));
                assert((cdto.value.sizeok == Sizeok.done) || !cdto.value.isBaseOf(cdfrom.value, null));
                IntRef offset = ref(0);
                if (cdto.value.isBaseOf(cdfrom.value, ptr(offset)) && (offset.value == 0))
                {
                    /*goto L1*/throw Dispatch0.INSTANCE;
                }
            }
            if (e_ref.value.to.mutableOf().constOf().equals(e_ref.value.e1.type.value.mutableOf().constOf()))
            {
                /*goto L1*/throw Dispatch0.INSTANCE;
            }
            if (e_ref.value.e1.isConst() != 0)
            {
                if (((e_ref.value.e1.op & 0xFF) == 25))
                {
                    if (((e_ref.value.type.value.toBasetype().ty & 0xFF) != ENUMTY.Tsarray))
                    {
                        Ref<Long> esz = ref(e_ref.value.type.value.size(e_ref.value.loc));
                        Ref<Long> e1sz = ref(e_ref.value.e1.type.value.size(e_ref.value.e1.loc));
                        if ((esz.value == -1L) || (e1sz.value == -1L))
                            this.error();
                            return ;
                        if ((esz.value == e1sz.value))
                            /*goto L1*/throw Dispatch0.INSTANCE;
                    }
                    return ;
                }
                if (((e_ref.value.to.toBasetype().ty & 0xFF) != ENUMTY.Tvoid))
                {
                    if (e_ref.value.e1.type.value.equals(e_ref.value.type.value) && e_ref.value.type.value.equals(e_ref.value.to))
                        this.ret = e_ref.value.e1;
                    else
                        this.ret = Cast(e_ref.value.loc, e_ref.value.type.value, e_ref.value.to, e_ref.value.e1).copy();
                }
            }
        }

        public  void visit(BinExp e) {
            Ref<BinExp> e_ref = ref(e);
            Ref<Boolean> e2only = ref(((e_ref.value.op & 0xFF) == 95) || ((e_ref.value.op & 0xFF) == 96));
            if (e2only.value ? this.expOptimize(e_ref.value.e2.value, this.result, false) : this.binOptimize(e_ref.value, this.result))
                return ;
            if (((e_ref.value.op & 0xFF) == 66) || ((e_ref.value.op & 0xFF) == 67) || ((e_ref.value.op & 0xFF) == 69))
            {
                if ((e_ref.value.e2.value.isConst() == 1))
                {
                    Ref<Long> i2 = ref((long)e_ref.value.e2.value.toInteger());
                    Ref<Long> sz = ref(e_ref.value.e1.value.type.value.size(e_ref.value.e1.value.loc));
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
            if (this.binOptimize(e_ref.value, this.result))
                return ;
            if ((e_ref.value.e1.value.isConst() != 0) && (e_ref.value.e2.value.isConst() != 0))
            {
                if (((e_ref.value.e1.value.op & 0xFF) == 25) && ((e_ref.value.e2.value.op & 0xFF) == 25))
                    return ;
                this.ret = Add(e_ref.value.loc, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
            }
        }

        public  void visit(MinExp e) {
            Ref<MinExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, this.result))
                return ;
            if ((e_ref.value.e1.value.isConst() != 0) && (e_ref.value.e2.value.isConst() != 0))
            {
                if (((e_ref.value.e2.value.op & 0xFF) == 25))
                    return ;
                this.ret = Min(e_ref.value.loc, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
            }
        }

        public  void visit(MulExp e) {
            Ref<MulExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, this.result))
                return ;
            if ((e_ref.value.e1.value.isConst() == 1) && (e_ref.value.e2.value.isConst() == 1))
            {
                this.ret = Mul(e_ref.value.loc, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
            }
        }

        public  void visit(DivExp e) {
            Ref<DivExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, this.result))
                return ;
            if ((e_ref.value.e1.value.isConst() == 1) && (e_ref.value.e2.value.isConst() == 1))
            {
                this.ret = Div(e_ref.value.loc, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
            }
        }

        public  void visit(ModExp e) {
            Ref<ModExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, this.result))
                return ;
            if ((e_ref.value.e1.value.isConst() == 1) && (e_ref.value.e2.value.isConst() == 1))
            {
                this.ret = Mod(e_ref.value.loc, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
            }
        }

        public  void shift_optimize(BinExp e, Function4<Loc,Type,Expression,Expression,UnionExp> shift) {
            Ref<BinExp> e_ref = ref(e);
            Ref<Function4<Loc,Type,Expression,Expression,UnionExp>> shift_ref = ref(shift);
            if (this.binOptimize(e_ref.value, this.result))
                return ;
            if ((e_ref.value.e2.value.isConst() == 1))
            {
                Ref<Long> i2 = ref((long)e_ref.value.e2.value.toInteger());
                Ref<Long> sz = ref(e_ref.value.e1.value.type.value.size(e_ref.value.e1.value.loc));
                assert((sz.value != -1L));
                sz.value *= 8L;
                if ((i2.value < 0L) || ((long)i2.value >= sz.value))
                {
                    e_ref.value.error(new BytePtr("shift by %lld is outside the range `0..%llu`"), i2.value, sz.value - 1L);
                    this.error();
                    return ;
                }
                if ((e_ref.value.e1.value.isConst() == 1))
                    this.ret = (shift_ref.value).invoke(e_ref.value.loc, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
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
            if (this.binOptimize(e_ref.value, this.result))
                return ;
            if ((e_ref.value.e1.value.isConst() == 1) && (e_ref.value.e2.value.isConst() == 1))
                this.ret = And(e_ref.value.loc, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
        }

        public  void visit(OrExp e) {
            Ref<OrExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, this.result))
                return ;
            if ((e_ref.value.e1.value.isConst() == 1) && (e_ref.value.e2.value.isConst() == 1))
                this.ret = Or(e_ref.value.loc, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
        }

        public  void visit(XorExp e) {
            Ref<XorExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, this.result))
                return ;
            if ((e_ref.value.e1.value.isConst() == 1) && (e_ref.value.e2.value.isConst() == 1))
                this.ret = Xor(e_ref.value.loc, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
        }

        public  void visit(PowExp e) {
            Ref<PowExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, this.result))
                return ;
            if (((e_ref.value.e1.value.op & 0xFF) == 135) && (e_ref.value.e1.value.toInteger() == 1L) || ((e_ref.value.e1.value.op & 0xFF) == 140) && (e_ref.value.e1.value.toReal() == CTFloat.one.value))
            {
                this.ret = new CommaExp(e_ref.value.loc, e_ref.value.e2.value, e_ref.value.e1.value, true);
                this.ret.type.value = e_ref.value.type.value;
                return ;
            }
            if (e_ref.value.e2.value.type.value.isintegral() && ((e_ref.value.e1.value.op & 0xFF) == 135) && ((long)e_ref.value.e1.value.toInteger() == -1L))
            {
                this.ret = new AndExp(e_ref.value.loc, e_ref.value.e2.value, new IntegerExp(e_ref.value.loc, 1L, e_ref.value.e2.value.type.value));
                this.ret.type.value = e_ref.value.e2.value.type.value;
                this.ret = new CondExp(e_ref.value.loc, this.ret, new IntegerExp(e_ref.value.loc, -1L, e_ref.value.type.value), new IntegerExp(e_ref.value.loc, 1L, e_ref.value.type.value));
                this.ret.type.value = e_ref.value.type.value;
                return ;
            }
            if (((e_ref.value.e2.value.op & 0xFF) == 135) && (e_ref.value.e2.value.toInteger() == 0L) || ((e_ref.value.e2.value.op & 0xFF) == 140) && (e_ref.value.e2.value.toReal() == CTFloat.zero.value))
            {
                if (e_ref.value.e1.value.type.value.isintegral())
                    this.ret = new IntegerExp(e_ref.value.loc, 1L, e_ref.value.e1.value.type.value);
                else
                    this.ret = new RealExp(e_ref.value.loc, CTFloat.one.value, e_ref.value.e1.value.type.value);
                this.ret = new CommaExp(e_ref.value.loc, e_ref.value.e1.value, this.ret, true);
                this.ret.type.value = e_ref.value.type.value;
                return ;
            }
            if (((e_ref.value.e2.value.op & 0xFF) == 135) && (e_ref.value.e2.value.toInteger() == 1L) || ((e_ref.value.e2.value.op & 0xFF) == 140) && (e_ref.value.e2.value.toReal() == CTFloat.one.value))
            {
                this.ret = e_ref.value.e1.value;
                return ;
            }
            if (((e_ref.value.e2.value.op & 0xFF) == 140) && (e_ref.value.e2.value.toReal() == CTFloat.minusone.value))
            {
                this.ret = new DivExp(e_ref.value.loc, new RealExp(e_ref.value.loc, CTFloat.one.value, e_ref.value.e2.value.type.value), e_ref.value.e1.value);
                this.ret.type.value = e_ref.value.type.value;
                return ;
            }
            if (e_ref.value.e1.value.type.value.isintegral() && ((e_ref.value.e2.value.op & 0xFF) == 135) && ((long)e_ref.value.e2.value.toInteger() < 0L))
            {
                e_ref.value.error(new BytePtr("cannot raise `%s` to a negative integer power. Did you mean `(cast(real)%s)^^%s` ?"), e_ref.value.e1.value.type.value.toBasetype().toChars(), e_ref.value.e1.value.toChars(), e_ref.value.e2.value.toChars());
                this.error();
                return ;
            }
            if (((e_ref.value.e2.value.op & 0xFF) == 140))
            {
                if ((e_ref.value.e2.value.toReal() == (double)(long)e_ref.value.e2.value.toReal()))
                    e_ref.value.e2.value = new IntegerExp(e_ref.value.loc, e_ref.value.e2.value.toInteger(), Type.tint64.value);
            }
            if ((e_ref.value.e1.value.isConst() == 1) && (e_ref.value.e2.value.isConst() == 1))
            {
                Ref<Expression> ex = ref(Pow(e_ref.value.loc, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy());
                if (!CTFEExp.isCantExp(ex.value))
                {
                    this.ret = ex.value;
                    return ;
                }
            }
            if (((e_ref.value.e1.value.op & 0xFF) == 135) && (e_ref.value.e1.value.toInteger() > 0L) && ((e_ref.value.e1.value.toInteger() - 1L & e_ref.value.e1.value.toInteger()) == 0) && e_ref.value.e2.value.type.value.isintegral() && e_ref.value.e2.value.type.value.isunsigned())
            {
                Ref<Long> i = ref(e_ref.value.e1.value.toInteger());
                Ref<Long> mul = ref(1L);
                for (; ((i.value >>= 1) > 1L);) {
                    mul.value++;
                }
                Ref<Expression> shift = ref(new MulExp(e_ref.value.loc, e_ref.value.e2.value, new IntegerExp(e_ref.value.loc, mul.value, e_ref.value.e2.value.type.value)));
                shift.value.type.value = e_ref.value.e2.value.type.value;
                shift.value = shift.value.castTo(null, Type.tshiftcnt.value);
                this.ret = new ShlExp(e_ref.value.loc, new IntegerExp(e_ref.value.loc, 1L, e_ref.value.e1.value.type.value), shift.value);
                this.ret.type.value = e_ref.value.type.value;
                return ;
            }
        }

        public  void visit(CommaExp e) {
            Ref<CommaExp> e_ref = ref(e);
            this.expOptimize(e_ref.value.e1.value, 0, false);
            this.expOptimize(e_ref.value.e2.value, this.result, this.keepLvalue);
            if (((this.ret.op & 0xFF) == 127))
                return ;
            if ((e_ref.value.e1.value == null) || ((e_ref.value.e1.value.op & 0xFF) == 135) || ((e_ref.value.e1.value.op & 0xFF) == 140) || !hasSideEffect(e_ref.value.e1.value))
            {
                this.ret = e_ref.value.e2.value;
                if (this.ret != null)
                    this.ret.type.value = e_ref.value.type.value;
            }
        }

        public  void visit(ArrayLengthExp e) {
            Ref<ArrayLengthExp> e_ref = ref(e);
            if (this.unaOptimize(e_ref.value, 1))
                return ;
            if (((e_ref.value.e1.op & 0xFF) == 26))
            {
                Ref<VarDeclaration> v = ref(((VarExp)e_ref.value.e1).var.isVarDeclaration());
                if ((v.value != null) && ((v.value.storage_class & 1L) != 0) && ((v.value.storage_class & 1048576L) != 0) && (v.value._init != null))
                {
                    {
                        Ref<Expression> ci = ref(v.value.getConstInitializer(true));
                        if ((ci.value) != null)
                            e_ref.value.e1 = ci.value;
                    }
                }
            }
            if (((e_ref.value.e1.op & 0xFF) == 121) || ((e_ref.value.e1.op & 0xFF) == 47) || ((e_ref.value.e1.op & 0xFF) == 48) || ((e_ref.value.e1.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
            {
                this.ret = ArrayLength(e_ref.value.type.value, e_ref.value.e1).copy();
            }
        }

        public  void visit(EqualExp e) {
            Ref<EqualExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, 0))
                return ;
            Ref<Expression> e1 = ref(fromConstInitializer(this.result, e_ref.value.e1.value));
            Ref<Expression> e2 = ref(fromConstInitializer(this.result, e_ref.value.e2.value));
            if (((e1.value.op & 0xFF) == 127))
            {
                this.ret = e1.value;
                return ;
            }
            if (((e2.value.op & 0xFF) == 127))
            {
                this.ret = e2.value;
                return ;
            }
            this.ret = Equal(e_ref.value.op, e_ref.value.loc, e_ref.value.type.value, e1.value, e2.value).copy();
            if (CTFEExp.isCantExp(this.ret))
                this.ret = e_ref.value;
        }

        public  void visit(IdentityExp e) {
            Ref<IdentityExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, 0))
                return ;
            if ((e_ref.value.e1.value.isConst() != 0) && (e_ref.value.e2.value.isConst() != 0) || ((e_ref.value.e1.value.op & 0xFF) == 13) && ((e_ref.value.e2.value.op & 0xFF) == 13))
            {
                this.ret = Identity(e_ref.value.op, e_ref.value.loc, e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
                if (CTFEExp.isCantExp(this.ret))
                    this.ret = e_ref.value;
            }
        }

        public  void visit(IndexExp e) {
            Ref<IndexExp> e_ref = ref(e);
            if (this.expOptimize(e_ref.value.e1.value, this.result & 1, false))
                return ;
            Ref<Expression> ex = ref(fromConstInitializer(this.result, e_ref.value.e1.value));
            setLengthVarIfKnown(e_ref.value.lengthVar.value, ex.value);
            if (this.expOptimize(e_ref.value.e2.value, 0, false))
                return ;
            if (this.keepLvalue)
                return ;
            this.ret = Index(e_ref.value.type.value, ex.value, e_ref.value.e2.value).copy();
            if (CTFEExp.isCantExp(this.ret))
                this.ret = e_ref.value;
        }

        public  void visit(SliceExp e) {
            Ref<SliceExp> e_ref = ref(e);
            if (this.expOptimize(e_ref.value.e1, this.result & 1, false))
                return ;
            if (e_ref.value.lwr == null)
            {
                if (((e_ref.value.e1.op & 0xFF) == 121))
                {
                    Ref<Type> t = ref(e_ref.value.e1.type.value.toBasetype());
                    {
                        Ref<Type> tn = ref(t.value.nextOf());
                        if ((tn.value) != null)
                            this.ret = e_ref.value.e1.castTo(null, tn.value.arrayOf());
                    }
                }
            }
            else
            {
                e_ref.value.e1 = fromConstInitializer(this.result, e_ref.value.e1);
                setLengthVarIfKnown(e_ref.value.lengthVar.value, e_ref.value.e1);
                this.expOptimize(e_ref.value.lwr, 0, false);
                this.expOptimize(e_ref.value.upr, 0, false);
                if (((this.ret.op & 0xFF) == 127))
                    return ;
                this.ret = Slice(e_ref.value.type.value, e_ref.value.e1, e_ref.value.lwr, e_ref.value.upr).copy();
                if (CTFEExp.isCantExp(this.ret))
                    this.ret = e_ref.value;
            }
            if (((this.ret.op & 0xFF) == 121))
            {
                e_ref.value.e1 = this.ret;
                e_ref.value.lwr = null;
                e_ref.value.upr = null;
                this.ret = e_ref.value;
            }
        }

        public  void visit(LogicalExp e) {
            Ref<LogicalExp> e_ref = ref(e);
            if (this.expOptimize(e_ref.value.e1.value, 0, false))
                return ;
            Ref<Boolean> oror = ref((e_ref.value.op & 0xFF) == 102);
            if (e_ref.value.e1.value.isBool(oror.value))
            {
                this.ret = new IntegerExp(e_ref.value.loc, (oror.value ? 1 : 0), Type.tbool.value);
                this.ret = Expression.combine(e_ref.value.e1.value, this.ret);
                if (((e_ref.value.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tvoid))
                {
                    this.ret = new CastExp(e_ref.value.loc, this.ret, Type.tvoid.value);
                    this.ret.type.value = e_ref.value.type.value;
                }
                this.ret = Expression_optimize(this.ret, this.result, false);
                return ;
            }
            if (this.expOptimize(e_ref.value.e2.value, 0, false))
                return ;
            if (e_ref.value.e1.value.isConst() != 0)
            {
                if (e_ref.value.e2.value.isConst() != 0)
                {
                    Ref<Boolean> n1 = ref(e_ref.value.e1.value.isBool(true));
                    Ref<Boolean> n2 = ref(e_ref.value.e2.value.isBool(true));
                    this.ret = new IntegerExp(e_ref.value.loc, oror.value ? ((n1.value || n2.value) ? 1 : 0) : ((n1.value && n2.value) ? 1 : 0), e_ref.value.type.value);
                }
                else if (e_ref.value.e1.value.isBool(!oror.value))
                {
                    if (((e_ref.value.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tvoid))
                        this.ret = e_ref.value.e2.value;
                    else
                    {
                        this.ret = new CastExp(e_ref.value.loc, e_ref.value.e2.value, e_ref.value.type.value);
                        this.ret.type.value = e_ref.value.type.value;
                    }
                }
            }
        }

        public  void visit(CmpExp e) {
            Ref<CmpExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, 0))
                return ;
            Ref<Expression> e1 = ref(fromConstInitializer(this.result, e_ref.value.e1.value));
            Ref<Expression> e2 = ref(fromConstInitializer(this.result, e_ref.value.e2.value));
            this.ret = Cmp(e_ref.value.op, e_ref.value.loc, e_ref.value.type.value, e1.value, e2.value).copy();
            if (CTFEExp.isCantExp(this.ret))
                this.ret = e_ref.value;
        }

        public  void visit(CatExp e) {
            Ref<CatExp> e_ref = ref(e);
            if (this.binOptimize(e_ref.value, this.result))
                return ;
            if (((e_ref.value.e1.value.op & 0xFF) == 70))
            {
                Ref<CatExp> ce1 = ref((CatExp)e_ref.value.e1.value);
                Ref<CatExp> cex = ref(new CatExp(e_ref.value.loc, ce1.value.e2.value, e_ref.value.e2.value));
                cex.value.type.value = e_ref.value.type.value;
                Ref<Expression> ex = ref(Expression_optimize(cex.value, this.result, false));
                if ((!pequals(ex.value, cex.value)))
                {
                    e_ref.value.e1.value = ce1.value.e1.value;
                    e_ref.value.e2.value = ex.value;
                }
            }
            if (((e_ref.value.e1.value.op & 0xFF) == 31))
            {
                Ref<SliceExp> se1 = ref((SliceExp)e_ref.value.e1.value);
                if (((se1.value.e1.op & 0xFF) == 121) && (se1.value.lwr == null))
                    e_ref.value.e1.value = se1.value.e1;
            }
            if (((e_ref.value.e2.value.op & 0xFF) == 31))
            {
                Ref<SliceExp> se2 = ref((SliceExp)e_ref.value.e2.value);
                if (((se2.value.e1.op & 0xFF) == 121) && (se2.value.lwr == null))
                    e_ref.value.e2.value = se2.value.e1;
            }
            this.ret = Cat(e_ref.value.type.value, e_ref.value.e1.value, e_ref.value.e2.value).copy();
            if (CTFEExp.isCantExp(this.ret))
                this.ret = e_ref.value;
        }

        public  void visit(CondExp e) {
            Ref<CondExp> e_ref = ref(e);
            if (this.expOptimize(e_ref.value.econd, 0, false))
                return ;
            if (e_ref.value.econd.isBool(true))
                this.ret = Expression_optimize(e_ref.value.e1.value, this.result, this.keepLvalue);
            else if (e_ref.value.econd.isBool(false))
                this.ret = Expression_optimize(e_ref.value.e2.value, this.result, this.keepLvalue);
            else
            {
                this.expOptimize(e_ref.value.e1.value, this.result, this.keepLvalue);
                this.expOptimize(e_ref.value.e2.value, this.result, this.keepLvalue);
            }
        }


        public OptimizeVisitor() {}
    }

    public static Expression expandVar(int result, VarDeclaration v) {
        IntRef result_ref = ref(result);
        Ref<VarDeclaration> v_ref = ref(v);
        Function1<Expression,Expression> initializerReturn = new Function1<Expression,Expression>(){
            public Expression invoke(Expression e) {
                Ref<Expression> e_ref = ref(e);
                if ((!pequals(e_ref.value.type.value, v_ref.value.type)))
                {
                    e_ref.value = e_ref.value.castTo(null, v_ref.value.type);
                }
                v_ref.value.inuse++;
                e_ref.value = e_ref.value.optimize(result_ref.value, false);
                v_ref.value.inuse--;
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
                        ei = ae.e2.value;
                        if ((ei.isConst() == 1))
                        {
                        }
                        else if (((ei.op & 0xFF) == 121))
                        {
                            if (((result_ref.value & 1) == 0) && ((ei.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tpointer))
                                return nullReturn.invoke();
                        }
                        else
                            return nullReturn.invoke();
                        if ((pequals(ei.type.value, v_ref.value.type)))
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
        if (((e1.op & 0xFF) == 26))
        {
            VarExp ve = (VarExp)e1;
            VarDeclaration v = ve.var.isVarDeclaration();
            e = expandVar(result, v);
            if (e != null)
            {
                if (((e.op & 0xFF) == 99) && ((((CommaExp)e).e1.value.op & 0xFF) == 38))
                    e = e1;
                else if ((!pequals(e.type.value, e1.type.value)) && (e1.type.value != null) && ((e1.type.value.ty & 0xFF) != ENUMTY.Tident))
                {
                    e = e.copy();
                    e.type.value = e1.type.value;
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
            len = (((ArrayLiteralExp)arr).elements.get()).length;
        else
        {
            Type t = arr.type.value.toBasetype();
            if (((t.ty & 0xFF) == ENUMTY.Tsarray))
                len = (int)((TypeSArray)t).dim.toInteger();
            else
                return ;
        }
        Expression dollar = new IntegerExp(Loc.initial.value, (long)len, Type.tsize_t.value);
        lengthVar._init = new ExpInitializer(Loc.initial.value, dollar);
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
        Expression dollar = new IntegerExp(Loc.initial.value, (long)len, Type.tsize_t.value);
        lengthVar._init = new ExpInitializer(Loc.initial.value, dollar);
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
