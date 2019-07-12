package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.aliasthis.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.clone.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.visitor.*;

public class opover {
    private static class OpIdVisitor extends Visitor
    {
        private Identifier id = null;
        public  void visit(Expression e) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(UAddExp e) {
            this.id = Id.uadd.value;
        }

        public  void visit(NegExp e) {
            this.id = Id.neg.value;
        }

        public  void visit(ComExp e) {
            this.id = Id.com.value;
        }

        public  void visit(CastExp e) {
            this.id = Id._cast.value;
        }

        public  void visit(InExp e) {
            this.id = Id.opIn.value;
        }

        public  void visit(PostExp e) {
            Ref<PostExp> e_ref = ref(e);
            this.id = ((e_ref.value.op & 0xFF) == 93) ? Id.postinc.value : Id.postdec.value;
        }

        public  void visit(AddExp e) {
            this.id = Id.add.value;
        }

        public  void visit(MinExp e) {
            this.id = Id.sub.value;
        }

        public  void visit(MulExp e) {
            this.id = Id.mul.value;
        }

        public  void visit(DivExp e) {
            this.id = Id.div.value;
        }

        public  void visit(ModExp e) {
            this.id = Id.mod.value;
        }

        public  void visit(PowExp e) {
            this.id = Id.pow.value;
        }

        public  void visit(ShlExp e) {
            this.id = Id.shl.value;
        }

        public  void visit(ShrExp e) {
            this.id = Id.shr.value;
        }

        public  void visit(UshrExp e) {
            this.id = Id.ushr.value;
        }

        public  void visit(AndExp e) {
            this.id = Id.iand.value;
        }

        public  void visit(OrExp e) {
            this.id = Id.ior.value;
        }

        public  void visit(XorExp e) {
            this.id = Id.ixor.value;
        }

        public  void visit(CatExp e) {
            this.id = Id.cat.value;
        }

        public  void visit(AssignExp e) {
            this.id = Id.assign.value;
        }

        public  void visit(AddAssignExp e) {
            this.id = Id.addass.value;
        }

        public  void visit(MinAssignExp e) {
            this.id = Id.subass.value;
        }

        public  void visit(MulAssignExp e) {
            this.id = Id.mulass.value;
        }

        public  void visit(DivAssignExp e) {
            this.id = Id.divass.value;
        }

        public  void visit(ModAssignExp e) {
            this.id = Id.modass.value;
        }

        public  void visit(AndAssignExp e) {
            this.id = Id.andass.value;
        }

        public  void visit(OrAssignExp e) {
            this.id = Id.orass.value;
        }

        public  void visit(XorAssignExp e) {
            this.id = Id.xorass.value;
        }

        public  void visit(ShlAssignExp e) {
            this.id = Id.shlass.value;
        }

        public  void visit(ShrAssignExp e) {
            this.id = Id.shrass.value;
        }

        public  void visit(UshrAssignExp e) {
            this.id = Id.ushrass.value;
        }

        public  void visit(CatAssignExp e) {
            this.id = Id.catass.value;
        }

        public  void visit(PowAssignExp e) {
            this.id = Id.powass.value;
        }

        public  void visit(EqualExp e) {
            this.id = Id.eq.value;
        }

        public  void visit(CmpExp e) {
            this.id = Id.cmp.value;
        }

        public  void visit(ArrayExp e) {
            this.id = Id.index.value;
        }

        public  void visit(PtrExp e) {
            this.id = Id.opStar.value;
        }


        public OpIdVisitor() {}
    }
    private static class OpIdRVisitor extends Visitor
    {
        private Identifier id = null;
        public  void visit(Expression e) {
            this.id = null;
        }

        public  void visit(InExp e) {
            this.id = Id.opIn_r.value;
        }

        public  void visit(AddExp e) {
            this.id = Id.add_r.value;
        }

        public  void visit(MinExp e) {
            this.id = Id.sub_r.value;
        }

        public  void visit(MulExp e) {
            this.id = Id.mul_r.value;
        }

        public  void visit(DivExp e) {
            this.id = Id.div_r.value;
        }

        public  void visit(ModExp e) {
            this.id = Id.mod_r.value;
        }

        public  void visit(PowExp e) {
            this.id = Id.pow_r.value;
        }

        public  void visit(ShlExp e) {
            this.id = Id.shl_r.value;
        }

        public  void visit(ShrExp e) {
            this.id = Id.shr_r.value;
        }

        public  void visit(UshrExp e) {
            this.id = Id.ushr_r.value;
        }

        public  void visit(AndExp e) {
            this.id = Id.iand_r.value;
        }

        public  void visit(OrExp e) {
            this.id = Id.ior_r.value;
        }

        public  void visit(XorExp e) {
            this.id = Id.ixor_r.value;
        }

        public  void visit(CatExp e) {
            this.id = Id.cat_r.value;
        }


        public OpIdRVisitor() {}
    }
    private static class OpOverload extends Visitor
    {
        private Ptr<Scope> sc = null;
        private BytePtr pop = null;
        private Expression result = null;
        public  OpOverload(Ptr<Scope> sc, BytePtr pop) {
            Ref<Ptr<Scope>> sc_ref = ref(sc);
            Ref<BytePtr> pop_ref = ref(pop);
            this.sc = sc_ref.value;
            this.pop = pcopy(pop_ref.value);
        }

        public  void visit(Expression e) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(UnaExp e) {
            Ref<UnaExp> e_ref = ref(e);
            if (((e_ref.value.e1.op & 0xFF) == 17))
            {
                Ref<ArrayExp> ae = ref((ArrayExp)e_ref.value.e1);
                ae.value.e1 = expressionSemantic(ae.value.e1, this.sc);
                ae.value.e1 = resolveProperties(this.sc, ae.value.e1);
                Ref<Expression> ae1old = ref(ae.value.e1);
                boolean maybeSlice = ((ae.value.arguments.get()).length == 0) || ((ae.value.arguments.get()).length == 1) && (((ae.value.arguments.get()).get(0).op & 0xFF) == 231);
                Ref<IntervalExp> ie = ref(null);
                if (maybeSlice && ((ae.value.arguments.get()).length != 0))
                {
                    assert((((ae.value.arguments.get()).get(0).op & 0xFF) == 231));
                    ie.value = (IntervalExp)(ae.value.arguments.get()).get(0);
                }
            L_outer1:
                for (; true;){
                    if (((ae.value.e1.op & 0xFF) == 127))
                    {
                        this.result = ae.value.e1;
                        return ;
                    }
                    Ref<Expression> e0 = ref(null);
                    Ref<Expression> ae1save = ref(ae.value.e1);
                    ae.value.lengthVar.value = null;
                    Ref<Type> t1b = ref(ae.value.e1.type.value.toBasetype());
                    Ref<AggregateDeclaration> ad = ref(isAggregate(t1b.value));
                    if (ad.value == null)
                        break;
                    try {
                        if (search_function(ad.value, Id.opIndexUnary.value) != null)
                        {
                            this.result = resolveOpDollar(this.sc, ae.value, ptr(e0));
                            if (this.result == null)
                                /*goto Lfallback*/throw Dispatch0.INSTANCE;
                            if (((this.result.op & 0xFF) == 127))
                                return ;
                            Ref<Ptr<DArray<Expression>>> a = ref((ae.value.arguments.get()).copy());
                            Ref<Ptr<DArray<RootObject>>> tiargs = ref(opToArg(this.sc, e_ref.value.op));
                            this.result = new DotTemplateInstanceExp(e_ref.value.loc, ae.value.e1, Id.opIndexUnary.value, tiargs.value);
                            this.result = new CallExp(e_ref.value.loc, this.result, a.value);
                            if (maybeSlice)
                                this.result = trySemantic(this.result, this.sc);
                            else
                                this.result = expressionSemantic(this.result, this.sc);
                            if (this.result != null)
                            {
                                this.result = Expression.combine(e0.value, this.result);
                                return ;
                            }
                        }
                    }
                    catch(Dispatch0 __d){}
                /*Lfallback:*/
                    if (maybeSlice && (search_function(ad.value, Id.opSliceUnary.value) != null))
                    {
                        this.result = resolveOpDollar(this.sc, ae.value, ie.value, ptr(e0));
                        if (((this.result.op & 0xFF) == 127))
                            return ;
                        Ref<Ptr<DArray<Expression>>> a = ref(new DArray<Expression>());
                        if (ie.value != null)
                        {
                            (a.value.get()).push(ie.value.lwr.value);
                            (a.value.get()).push(ie.value.upr.value);
                        }
                        Ref<Ptr<DArray<RootObject>>> tiargs = ref(opToArg(this.sc, e_ref.value.op));
                        this.result = new DotTemplateInstanceExp(e_ref.value.loc, ae.value.e1, Id.opSliceUnary.value, tiargs.value);
                        this.result = new CallExp(e_ref.value.loc, this.result, a.value);
                        this.result = expressionSemantic(this.result, this.sc);
                        this.result = Expression.combine(e0.value, this.result);
                        return ;
                    }
                    if ((ad.value.aliasthis != null) && (!pequals(t1b.value, ae.value.att1)))
                    {
                        if ((ae.value.att1 == null) && t1b.value.checkAliasThisRec())
                            ae.value.att1 = t1b.value;
                        ae.value.e1 = resolveAliasThis(this.sc, ae1save.value, true);
                        if (ae.value.e1 != null)
                            continue L_outer1;
                    }
                    break;
                }
                ae.value.e1 = ae1old.value;
                ae.value.lengthVar.value = null;
            }
            e_ref.value.e1 = expressionSemantic(e_ref.value.e1, this.sc);
            e_ref.value.e1 = resolveProperties(this.sc, e_ref.value.e1);
            if (((e_ref.value.e1.op & 0xFF) == 127))
            {
                this.result = e_ref.value.e1;
                return ;
            }
            Ref<AggregateDeclaration> ad = ref(isAggregate(e_ref.value.e1.type.value));
            if (ad.value != null)
            {
                Ref<Dsymbol> fd = ref(null);
                if (((e_ref.value.op & 0xFF) != 103) && ((e_ref.value.op & 0xFF) != 104))
                {
                    fd.value = search_function(ad.value, opId(e_ref.value));
                    if (fd.value != null)
                    {
                        this.result = build_overload(e_ref.value.loc, this.sc, e_ref.value.e1, null, fd.value);
                        return ;
                    }
                }
                fd.value = search_function(ad.value, Id.opUnary.value);
                if (fd.value != null)
                {
                    Ref<Ptr<DArray<RootObject>>> tiargs = ref(opToArg(this.sc, e_ref.value.op));
                    this.result = new DotTemplateInstanceExp(e_ref.value.loc, e_ref.value.e1, fd.value.ident, tiargs.value);
                    this.result = new CallExp(e_ref.value.loc, this.result);
                    this.result = expressionSemantic(this.result, this.sc);
                    return ;
                }
                if ((ad.value.aliasthis != null) && (!pequals(e_ref.value.e1.type.value, e_ref.value.att1)))
                {
                    Ref<Expression> e1 = ref(new DotIdExp(e_ref.value.loc, e_ref.value.e1, ad.value.aliasthis.ident));
                    Ref<UnaExp> ue = ref((UnaExp)e_ref.value.copy());
                    if ((ue.value.att1 == null) && e_ref.value.e1.type.value.checkAliasThisRec())
                        ue.value.att1 = e_ref.value.e1.type.value;
                    ue.value.e1 = e1.value;
                    this.result = trySemantic(ue.value, this.sc);
                    return ;
                }
            }
        }

        public  void visit(ArrayExp ae) {
            Ref<ArrayExp> ae_ref = ref(ae);
            ae_ref.value.e1 = expressionSemantic(ae_ref.value.e1, this.sc);
            ae_ref.value.e1 = resolveProperties(this.sc, ae_ref.value.e1);
            Ref<Expression> ae1old = ref(ae_ref.value.e1);
            boolean maybeSlice = ((ae_ref.value.arguments.get()).length == 0) || ((ae_ref.value.arguments.get()).length == 1) && (((ae_ref.value.arguments.get()).get(0).op & 0xFF) == 231);
            Ref<IntervalExp> ie = ref(null);
            if (maybeSlice && ((ae_ref.value.arguments.get()).length != 0))
            {
                assert((((ae_ref.value.arguments.get()).get(0).op & 0xFF) == 231));
                ie.value = (IntervalExp)(ae_ref.value.arguments.get()).get(0);
            }
        L_outer2:
            for (; true;){
                if (((ae_ref.value.e1.op & 0xFF) == 127))
                {
                    this.result = ae_ref.value.e1;
                    return ;
                }
                Ref<Expression> e0 = ref(null);
                Ref<Expression> ae1save = ref(ae_ref.value.e1);
                ae_ref.value.lengthVar.value = null;
                Ref<Type> t1b = ref(ae_ref.value.e1.type.value.toBasetype());
                Ref<AggregateDeclaration> ad = ref(isAggregate(t1b.value));
                if (ad.value == null)
                {
                    if (isIndexableNonAggregate(t1b.value) || ((ae_ref.value.e1.op & 0xFF) == 20))
                    {
                        if (maybeSlice)
                        {
                            this.result = new SliceExp(ae_ref.value.loc, ae_ref.value.e1, ie.value);
                            this.result = expressionSemantic(this.result, this.sc);
                            return ;
                        }
                        if (((ae_ref.value.arguments.get()).length == 1))
                        {
                            this.result = new IndexExp(ae_ref.value.loc, ae_ref.value.e1, (ae_ref.value.arguments.get()).get(0));
                            this.result = expressionSemantic(this.result, this.sc);
                            return ;
                        }
                    }
                    break;
                }
                try {
                    if (search_function(ad.value, Id.index.value) != null)
                    {
                        this.result = resolveOpDollar(this.sc, ae_ref.value, ptr(e0));
                        if (this.result == null)
                            /*goto Lfallback*/throw Dispatch0.INSTANCE;
                        if (((this.result.op & 0xFF) == 127))
                            return ;
                        Ref<Ptr<DArray<Expression>>> a = ref((ae_ref.value.arguments.get()).copy());
                        this.result = new DotIdExp(ae_ref.value.loc, ae_ref.value.e1, Id.index.value);
                        this.result = new CallExp(ae_ref.value.loc, this.result, a.value);
                        if (maybeSlice)
                            this.result = trySemantic(this.result, this.sc);
                        else
                            this.result = expressionSemantic(this.result, this.sc);
                        if (this.result != null)
                        {
                            this.result = Expression.combine(e0.value, this.result);
                            return ;
                        }
                    }
                }
                catch(Dispatch0 __d){}
            /*Lfallback:*/
                if (maybeSlice && ((ae_ref.value.e1.op & 0xFF) == 20))
                {
                    this.result = new SliceExp(ae_ref.value.loc, ae_ref.value.e1, ie.value);
                    this.result = expressionSemantic(this.result, this.sc);
                    this.result = Expression.combine(e0.value, this.result);
                    return ;
                }
                if (maybeSlice && (search_function(ad.value, Id.slice.value) != null))
                {
                    this.result = resolveOpDollar(this.sc, ae_ref.value, ie.value, ptr(e0));
                    if (((this.result.op & 0xFF) == 127))
                        return ;
                    Ref<Ptr<DArray<Expression>>> a = ref(new DArray<Expression>());
                    if (ie.value != null)
                    {
                        (a.value.get()).push(ie.value.lwr.value);
                        (a.value.get()).push(ie.value.upr.value);
                    }
                    this.result = new DotIdExp(ae_ref.value.loc, ae_ref.value.e1, Id.slice.value);
                    this.result = new CallExp(ae_ref.value.loc, this.result, a.value);
                    this.result = expressionSemantic(this.result, this.sc);
                    this.result = Expression.combine(e0.value, this.result);
                    return ;
                }
                if ((ad.value.aliasthis != null) && (!pequals(t1b.value, ae_ref.value.att1)))
                {
                    if ((ae_ref.value.att1 == null) && t1b.value.checkAliasThisRec())
                        ae_ref.value.att1 = t1b.value;
                    ae_ref.value.e1 = resolveAliasThis(this.sc, ae1save.value, true);
                    if (ae_ref.value.e1 != null)
                        continue L_outer2;
                }
                break;
            }
            ae_ref.value.e1 = ae1old.value;
            ae_ref.value.lengthVar.value = null;
        }

        public  void visit(CastExp e) {
            Ref<CastExp> e_ref = ref(e);
            Ref<AggregateDeclaration> ad = ref(isAggregate(e_ref.value.e1.type.value));
            if (ad.value != null)
            {
                Ref<Dsymbol> fd = ref(null);
                fd.value = search_function(ad.value, Id._cast.value);
                if (fd.value != null)
                {
                    if (fd.value.isFuncDeclaration() != null)
                    {
                        this.result = build_overload(e_ref.value.loc, this.sc, e_ref.value.e1, null, fd.value);
                        return ;
                    }
                    Ref<Ptr<DArray<RootObject>>> tiargs = ref(new DArray<RootObject>());
                    (tiargs.value.get()).push(e_ref.value.to);
                    this.result = new DotTemplateInstanceExp(e_ref.value.loc, e_ref.value.e1, fd.value.ident, tiargs.value);
                    this.result = new CallExp(e_ref.value.loc, this.result);
                    this.result = expressionSemantic(this.result, this.sc);
                    return ;
                }
                if (ad.value.aliasthis != null)
                {
                    Ref<Expression> e1 = ref(resolveAliasThis(this.sc, e_ref.value.e1, false));
                    this.result = e_ref.value.copy();
                    ((UnaExp)this.result).e1 = e1.value;
                    this.result = op_overload(this.result, this.sc, null);
                    return ;
                }
            }
        }

        public  void visit(BinExp e) {
            Ref<BinExp> e_ref = ref(e);
            Ref<Identifier> id = ref(opId(e_ref.value));
            Ref<Identifier> id_r = ref(opId_r(e_ref.value));
            Ref<DArray<Expression>> args1 = ref(new DArray<Expression>());
            try {
                Ref<DArray<Expression>> args2 = ref(new DArray<Expression>());
                try {
                    IntRef argsset = ref(0);
                    Ref<AggregateDeclaration> ad1 = ref(isAggregate(e_ref.value.e1.value.type.value));
                    Ref<AggregateDeclaration> ad2 = ref(isAggregate(e_ref.value.e2.value.type.value));
                    if (((e_ref.value.op & 0xFF) == 90) && (pequals(ad1.value, ad2.value)))
                    {
                        Ref<StructDeclaration> sd = ref(ad1.value.isStructDeclaration());
                        if ((sd.value != null) && !sd.value.hasIdentityAssign)
                        {
                            return ;
                        }
                    }
                    Ref<Dsymbol> s = ref(null);
                    Ref<Dsymbol> s_r = ref(null);
                    if ((ad1.value != null) && (id.value != null))
                    {
                        s.value = search_function(ad1.value, id.value);
                    }
                    if ((ad2.value != null) && (id_r.value != null))
                    {
                        s_r.value = search_function(ad2.value, id_r.value);
                        if ((s_r.value != null) && (pequals(s_r.value, s.value)))
                            s_r.value = null;
                    }
                    Ref<Ptr<DArray<RootObject>>> tiargs = ref(null);
                    if (((e_ref.value.op & 0xFF) == 93) || ((e_ref.value.op & 0xFF) == 94))
                    {
                        if ((ad1.value != null) && (search_function(ad1.value, Id.opUnary.value) != null))
                            return ;
                    }
                    if ((s.value == null) && (s_r.value == null) && ((e_ref.value.op & 0xFF) != 58) && ((e_ref.value.op & 0xFF) != 59) && ((e_ref.value.op & 0xFF) != 90) && ((e_ref.value.op & 0xFF) != 93) && ((e_ref.value.op & 0xFF) != 94))
                    {
                        if (ad1.value != null)
                        {
                            s.value = search_function(ad1.value, Id.opBinary.value);
                            if ((s.value != null) && (s.value.isTemplateDeclaration() == null))
                            {
                                e_ref.value.e1.value.error(new BytePtr("`%s.opBinary` isn't a template"), e_ref.value.e1.value.toChars());
                                this.result = new ErrorExp();
                                return ;
                            }
                        }
                        if (ad2.value != null)
                        {
                            s_r.value = search_function(ad2.value, Id.opBinaryRight.value);
                            if ((s_r.value != null) && (s_r.value.isTemplateDeclaration() == null))
                            {
                                e_ref.value.e2.value.error(new BytePtr("`%s.opBinaryRight` isn't a template"), e_ref.value.e2.value.toChars());
                                this.result = new ErrorExp();
                                return ;
                            }
                            if ((s_r.value != null) && (pequals(s_r.value, s.value)))
                                s_r.value = null;
                        }
                        if ((s.value != null) || (s_r.value != null))
                        {
                            id.value = Id.opBinary.value;
                            id_r.value = Id.opBinaryRight.value;
                            tiargs.value = opToArg(this.sc, e_ref.value.op);
                        }
                    }
                    try {
                        if ((s.value != null) || (s_r.value != null))
                        {
                            args1.value.setDim(1);
                            args1.value.set(0, e_ref.value.e1.value);
                            expandTuples(ptr(args1));
                            args2.value.setDim(1);
                            args2.value.set(0, e_ref.value.e2.value);
                            expandTuples(ptr(args2));
                            argsset.value = 1;
                            Ref<MatchAccumulator> m = ref(new MatchAccumulator());
                            if (s.value != null)
                            {
                                functionResolve(m, s.value, e_ref.value.loc, this.sc, tiargs.value, e_ref.value.e1.value.type.value, ptr(args2), null);
                                if ((m.value.lastf != null) && m.value.lastf.errors || m.value.lastf.semantic3Errors)
                                {
                                    this.result = new ErrorExp();
                                    return ;
                                }
                            }
                            Ref<FuncDeclaration> lastf = ref(m.value.lastf);
                            if (s_r.value != null)
                            {
                                functionResolve(m, s_r.value, e_ref.value.loc, this.sc, tiargs.value, e_ref.value.e2.value.type.value, ptr(args1), null);
                                if ((m.value.lastf != null) && m.value.lastf.errors || m.value.lastf.semantic3Errors)
                                {
                                    this.result = new ErrorExp();
                                    return ;
                                }
                            }
                            if ((m.value.count > 1))
                            {
                                e_ref.value.error(new BytePtr("overloads `%s` and `%s` both match argument list for `%s`"), m.value.lastf.type.toChars(), m.value.nextf.type.toChars(), m.value.lastf.toChars());
                            }
                            else if ((m.value.last <= MATCH.nomatch))
                            {
                                if (tiargs.value != null)
                                    /*goto L1*/throw Dispatch0.INSTANCE;
                                m.value.lastf = null;
                            }
                            if (((e_ref.value.op & 0xFF) == 93) || ((e_ref.value.op & 0xFF) == 94))
                            {
                                this.result = build_overload(e_ref.value.loc, this.sc, e_ref.value.e1.value, null, m.value.lastf != null ? m.value.lastf : s.value);
                            }
                            else if ((lastf.value != null) && (pequals(m.value.lastf, lastf.value)) || (s_r.value == null) && (m.value.last <= MATCH.nomatch))
                            {
                                this.result = build_overload(e_ref.value.loc, this.sc, e_ref.value.e1.value, e_ref.value.e2.value, m.value.lastf != null ? m.value.lastf : s.value);
                            }
                            else
                            {
                                this.result = build_overload(e_ref.value.loc, this.sc, e_ref.value.e2.value, e_ref.value.e1.value, m.value.lastf != null ? m.value.lastf : s_r.value);
                            }
                            return ;
                        }
                    }
                    catch(Dispatch0 __d){}
                /*L1:*/
                    if (isCommutative(e_ref.value.op) && (tiargs.value == null))
                    {
                        s.value = null;
                        s_r.value = null;
                        if ((ad1.value != null) && (id_r.value != null))
                        {
                            s_r.value = search_function(ad1.value, id_r.value);
                        }
                        if ((ad2.value != null) && (id.value != null))
                        {
                            s.value = search_function(ad2.value, id.value);
                            if ((s.value != null) && (pequals(s.value, s_r.value)))
                                s.value = null;
                        }
                        if ((s.value != null) || (s_r.value != null))
                        {
                            if (argsset.value == 0)
                            {
                                args1.value.setDim(1);
                                args1.value.set(0, e_ref.value.e1.value);
                                expandTuples(ptr(args1));
                                args2.value.setDim(1);
                                args2.value.set(0, e_ref.value.e2.value);
                                expandTuples(ptr(args2));
                            }
                            Ref<MatchAccumulator> m = ref(new MatchAccumulator());
                            if (s_r.value != null)
                            {
                                functionResolve(m, s_r.value, e_ref.value.loc, this.sc, tiargs.value, e_ref.value.e1.value.type.value, ptr(args2), null);
                                if ((m.value.lastf != null) && m.value.lastf.errors || m.value.lastf.semantic3Errors)
                                {
                                    this.result = new ErrorExp();
                                    return ;
                                }
                            }
                            Ref<FuncDeclaration> lastf = ref(m.value.lastf);
                            if (s.value != null)
                            {
                                functionResolve(m, s.value, e_ref.value.loc, this.sc, tiargs.value, e_ref.value.e2.value.type.value, ptr(args1), null);
                                if ((m.value.lastf != null) && m.value.lastf.errors || m.value.lastf.semantic3Errors)
                                {
                                    this.result = new ErrorExp();
                                    return ;
                                }
                            }
                            if ((m.value.count > 1))
                            {
                                e_ref.value.error(new BytePtr("overloads `%s` and `%s` both match argument list for `%s`"), m.value.lastf.type.toChars(), m.value.nextf.type.toChars(), m.value.lastf.toChars());
                            }
                            else if ((m.value.last <= MATCH.nomatch))
                            {
                                m.value.lastf = null;
                            }
                            if ((lastf.value != null) && (pequals(m.value.lastf, lastf.value)) || (s.value == null) && (m.value.last <= MATCH.nomatch))
                            {
                                this.result = build_overload(e_ref.value.loc, this.sc, e_ref.value.e1.value, e_ref.value.e2.value, m.value.lastf != null ? m.value.lastf : s_r.value);
                            }
                            else
                            {
                                this.result = build_overload(e_ref.value.loc, this.sc, e_ref.value.e2.value, e_ref.value.e1.value, m.value.lastf != null ? m.value.lastf : s.value);
                            }
                            if (this.pop != null)
                                this.pop.set(0, reverseRelation(e_ref.value.op));
                            return ;
                        }
                    }
                    Ref<Expression> tempResult = ref(null);
                    if (!(((e_ref.value.op & 0xFF) == 90) && (ad2.value != null) && (pequals(ad1.value, ad2.value))))
                    {
                        this.result = checkAliasThisForLhs(ad1.value, this.sc, e_ref.value);
                        if (this.result != null)
                        {
                            if (((e_ref.value.op & 0xFF) != 90) || ((e_ref.value.e1.value.op & 0xFF) == 20))
                                return ;
                            if ((ad1.value.fields.length == 1) || (ad1.value.fields.length == 2) && (ad1.value.vthis != null))
                            {
                                Ref<VarDeclaration> var = ref(ad1.value.aliasthis.isVarDeclaration());
                                if ((var.value != null) && (pequals(var.value.type, ad1.value.fields.get(0).type)))
                                    return ;
                                Ref<FuncDeclaration> func = ref(ad1.value.aliasthis.isFuncDeclaration());
                                Ref<TypeFunction> tf = ref((TypeFunction)func.value.type);
                                if (tf.value.isref && (pequals(ad1.value.fields.get(0).type, tf.value.next)))
                                    return ;
                            }
                            tempResult.value = this.result;
                        }
                    }
                    if (!(((e_ref.value.op & 0xFF) == 90) && (ad1.value != null) && (pequals(ad1.value, ad2.value))))
                    {
                        this.result = checkAliasThisForRhs(ad2.value, this.sc, e_ref.value);
                        if (this.result != null)
                            return ;
                    }
                    if (tempResult.value != null)
                    {
                        e_ref.value.deprecation(new BytePtr("Cannot use `alias this` to partially initialize variable `%s` of type `%s`. Use `%s`"), e_ref.value.e1.value.toChars(), ad1.value.toChars(), ((BinExp)tempResult.value).e1.value.toChars());
                        this.result = tempResult.value;
                    }
                }
                finally {
                }
            }
            finally {
            }
        }

        public  void visit(EqualExp e) {
            Ref<EqualExp> e_ref = ref(e);
            Ref<Type> t1 = ref(e_ref.value.e1.value.type.value.toBasetype());
            Ref<Type> t2 = ref(e_ref.value.e2.value.type.value.toBasetype());
            if (((t1.value.ty & 0xFF) == ENUMTY.Tarray) || ((t1.value.ty & 0xFF) == ENUMTY.Tsarray) && ((t2.value.ty & 0xFF) == ENUMTY.Tarray) || ((t2.value.ty & 0xFF) == ENUMTY.Tsarray))
            {
                Function0<Boolean> needsDirectEq = new Function0<Boolean>(){
                    public Boolean invoke() {
                        Ref<Type> t1n = ref(t1.value.nextOf().toBasetype());
                        Ref<Type> t2n = ref(t2.value.nextOf().toBasetype());
                        if (((t1n.value.ty & 0xFF) == ENUMTY.Tchar) || ((t1n.value.ty & 0xFF) == ENUMTY.Twchar) || ((t1n.value.ty & 0xFF) == ENUMTY.Tdchar) && ((t2n.value.ty & 0xFF) == ENUMTY.Tchar) || ((t2n.value.ty & 0xFF) == ENUMTY.Twchar) || ((t2n.value.ty & 0xFF) == ENUMTY.Tdchar) || ((t1n.value.ty & 0xFF) == ENUMTY.Tvoid) || ((t2n.value.ty & 0xFF) == ENUMTY.Tvoid))
                        {
                            return false;
                        }
                        if ((!pequals(t1n.value.constOf(), t2n.value.constOf())))
                            return true;
                        Ref<Type> t = ref(t1n.value);
                        for (; t.value.toBasetype().nextOf() != null;) {
                            t.value = t.value.nextOf().toBasetype();
                        }
                        if (((t.value.ty & 0xFF) != ENUMTY.Tstruct))
                            return false;
                        if (global.value.params.useTypeInfo && (Type.dtypeinfo.value != null))
                            semanticTypeInfo(sc, t.value);
                        return ((TypeStruct)t.value).sym.hasIdentityEquals;
                    }
                };
                if (needsDirectEq.invoke() && !(((t1.value.ty & 0xFF) == ENUMTY.Tarray) && ((t2.value.ty & 0xFF) == ENUMTY.Tarray)))
                {
                    Ref<Expression> eeq = ref(new IdentifierExp(e_ref.value.loc, Id.__ArrayEq.value));
                    this.result = new CallExp(e_ref.value.loc, eeq.value, e_ref.value.e1.value, e_ref.value.e2.value);
                    if (((e_ref.value.op & 0xFF) == 59))
                        this.result = new NotExp(e_ref.value.loc, this.result);
                    this.result = trySemantic(this.result, this.sc);
                    if (this.result == null)
                    {
                        e_ref.value.error(new BytePtr("cannot compare `%s` and `%s`"), t1.value.toChars(), t2.value.toChars());
                        this.result = new ErrorExp();
                    }
                    return ;
                }
            }
            if (((t1.value.ty & 0xFF) == ENUMTY.Tclass) && ((e_ref.value.e2.value.op & 0xFF) == 13) || ((t2.value.ty & 0xFF) == ENUMTY.Tclass) && ((e_ref.value.e1.value.op & 0xFF) == 13))
            {
                e_ref.value.error(new BytePtr("use `%s` instead of `%s` when comparing with `null`"), Token.toChars(((e_ref.value.op & 0xFF) == 58) ? TOK.identity : TOK.notIdentity), Token.toChars(e_ref.value.op));
                this.result = new ErrorExp();
                return ;
            }
            if (((t1.value.ty & 0xFF) == ENUMTY.Tclass) && ((t2.value.ty & 0xFF) == ENUMTY.Tnull) || ((t1.value.ty & 0xFF) == ENUMTY.Tnull) && ((t2.value.ty & 0xFF) == ENUMTY.Tclass))
            {
                return ;
            }
            if (((t1.value.ty & 0xFF) == ENUMTY.Tclass) && ((t2.value.ty & 0xFF) == ENUMTY.Tclass))
            {
                Ref<ClassDeclaration> cd1 = ref(t1.value.isClassHandle());
                Ref<ClassDeclaration> cd2 = ref(t2.value.isClassHandle());
                if (!((cd1.value.classKind == ClassKind.cpp) || (cd2.value.classKind == ClassKind.cpp)))
                {
                    Ref<Expression> e1x = ref(e_ref.value.e1.value);
                    Ref<Expression> e2x = ref(e_ref.value.e2.value);
                    Ref<Type> to = ref(ClassDeclaration.object.value.getType());
                    if (cd1.value.isInterfaceDeclaration() != null)
                        e1x.value = new CastExp(e_ref.value.loc, e_ref.value.e1.value, t1.value.isMutable() ? to.value : to.value.constOf());
                    if (cd2.value.isInterfaceDeclaration() != null)
                        e2x.value = new CastExp(e_ref.value.loc, e_ref.value.e2.value, t2.value.isMutable() ? to.value : to.value.constOf());
                    this.result = new IdentifierExp(e_ref.value.loc, Id.empty.value);
                    this.result = new DotIdExp(e_ref.value.loc, this.result, Id.object.value);
                    this.result = new DotIdExp(e_ref.value.loc, this.result, Id.eq.value);
                    this.result = new CallExp(e_ref.value.loc, this.result, e1x.value, e2x.value);
                    if (((e_ref.value.op & 0xFF) == 59))
                        this.result = new NotExp(e_ref.value.loc, this.result);
                    this.result = expressionSemantic(this.result, this.sc);
                    return ;
                }
            }
            this.result = compare_overload(e_ref.value, this.sc, Id.eq.value, null);
            if (this.result != null)
            {
                if (((this.result.op & 0xFF) == 18) && ((e_ref.value.op & 0xFF) == 59))
                {
                    this.result = new NotExp(this.result.loc, this.result);
                    this.result = expressionSemantic(this.result, this.sc);
                }
                return ;
            }
            if (((t1.value.ty & 0xFF) == ENUMTY.Tarray) && ((t2.value.ty & 0xFF) == ENUMTY.Tarray))
                return ;
            if (((t1.value.ty & 0xFF) == ENUMTY.Tpointer) || ((t2.value.ty & 0xFF) == ENUMTY.Tpointer))
            {
                Ref<Byte> op2 = ref(((e_ref.value.op & 0xFF) == 58) ? TOK.identity : TOK.notIdentity);
                this.result = new IdentityExp(op2.value, e_ref.value.loc, e_ref.value.e1.value, e_ref.value.e2.value);
                this.result = expressionSemantic(this.result, this.sc);
                return ;
            }
            if (((t1.value.ty & 0xFF) == ENUMTY.Tstruct) && ((t2.value.ty & 0xFF) == ENUMTY.Tstruct))
            {
                Ref<StructDeclaration> sd = ref(((TypeStruct)t1.value).sym);
                if ((!pequals(sd.value, ((TypeStruct)t2.value).sym)))
                    return ;
                if (!global.value.params.fieldwise && !needOpEquals(sd.value))
                {
                    Ref<Byte> op2 = ref(((e_ref.value.op & 0xFF) == 58) ? TOK.identity : TOK.notIdentity);
                    this.result = new IdentityExp(op2.value, e_ref.value.loc, e_ref.value.e1.value, e_ref.value.e2.value);
                    this.result = expressionSemantic(this.result, this.sc);
                    return ;
                }
                if ((e_ref.value.att1 != null) && (pequals(t1.value, e_ref.value.att1)))
                    return ;
                if ((e_ref.value.att2 != null) && (pequals(t2.value, e_ref.value.att2)))
                    return ;
                e_ref.value = (EqualExp)e_ref.value.copy();
                if (e_ref.value.att1 == null)
                    e_ref.value.att1 = t1.value;
                if (e_ref.value.att2 == null)
                    e_ref.value.att2 = t2.value;
                e_ref.value.e1.value = new DotIdExp(e_ref.value.loc, e_ref.value.e1.value, Id._tupleof.value);
                e_ref.value.e2.value = new DotIdExp(e_ref.value.loc, e_ref.value.e2.value, Id._tupleof.value);
                Ref<Ptr<Scope>> sc2 = ref((this.sc.get()).push());
                (sc2.value.get()).flags = (sc2.value.get()).flags & -1025 | 2;
                this.result = expressionSemantic(e_ref.value, sc2.value);
                (sc2.value.get()).pop();
                if (((this.result.op & 0xFF) == (e_ref.value.op & 0xFF)) && (pequals(((EqualExp)this.result).e1.value.type.value.toBasetype(), t1.value)))
                {
                    e_ref.value.error(new BytePtr("cannot compare `%s` because its auto generated member-wise equality has recursive definition"), t1.value.toChars());
                    this.result = new ErrorExp();
                }
                return ;
            }
            if (((e_ref.value.e1.value.op & 0xFF) == 126) && ((e_ref.value.e2.value.op & 0xFF) == 126))
            {
                Ref<TupleExp> tup1 = ref((TupleExp)e_ref.value.e1.value);
                Ref<TupleExp> tup2 = ref((TupleExp)e_ref.value.e2.value);
                IntRef dim = ref((tup1.value.exps.get()).length);
                if ((dim.value != (tup2.value.exps.get()).length))
                {
                    e_ref.value.error(new BytePtr("mismatched tuple lengths, `%d` and `%d`"), dim.value, (tup2.value.exps.get()).length);
                    this.result = new ErrorExp();
                    return ;
                }
                if ((dim.value == 0))
                {
                    this.result = new IntegerExp(e_ref.value.loc, (((e_ref.value.op & 0xFF) == 58) ? 1 : 0), Type.tbool.value);
                }
                else
                {
                    {
                        IntRef i = ref(0);
                        for (; (i.value < dim.value);i.value++){
                            Ref<Expression> ex1 = ref((tup1.value.exps.get()).get(i.value));
                            Ref<Expression> ex2 = ref((tup2.value.exps.get()).get(i.value));
                            Ref<EqualExp> eeq = ref(new EqualExp(e_ref.value.op, e_ref.value.loc, ex1.value, ex2.value));
                            eeq.value.att1 = e_ref.value.att1;
                            eeq.value.att2 = e_ref.value.att2;
                            if (this.result == null)
                                this.result = eeq.value;
                            else if (((e_ref.value.op & 0xFF) == 58))
                                this.result = new LogicalExp(e_ref.value.loc, TOK.andAnd, this.result, eeq.value);
                            else
                                this.result = new LogicalExp(e_ref.value.loc, TOK.orOr, this.result, eeq.value);
                        }
                    }
                    assert(this.result != null);
                }
                this.result = Expression.combine(tup1.value.e0, tup2.value.e0, this.result);
                this.result = expressionSemantic(this.result, this.sc);
                return ;
            }
        }

        public  void visit(CmpExp e) {
            Ref<CmpExp> e_ref = ref(e);
            this.result = compare_overload(e_ref.value, this.sc, Id.cmp.value, this.pop);
        }

        public  void visit(BinAssignExp e) {
            Ref<BinAssignExp> e_ref = ref(e);
            if (((e_ref.value.e1.value.op & 0xFF) == 17))
            {
                Ref<ArrayExp> ae = ref((ArrayExp)e_ref.value.e1.value);
                ae.value.e1 = expressionSemantic(ae.value.e1, this.sc);
                ae.value.e1 = resolveProperties(this.sc, ae.value.e1);
                Ref<Expression> ae1old = ref(ae.value.e1);
                boolean maybeSlice = ((ae.value.arguments.get()).length == 0) || ((ae.value.arguments.get()).length == 1) && (((ae.value.arguments.get()).get(0).op & 0xFF) == 231);
                Ref<IntervalExp> ie = ref(null);
                if (maybeSlice && ((ae.value.arguments.get()).length != 0))
                {
                    assert((((ae.value.arguments.get()).get(0).op & 0xFF) == 231));
                    ie.value = (IntervalExp)(ae.value.arguments.get()).get(0);
                }
            L_outer3:
                for (; true;){
                    if (((ae.value.e1.op & 0xFF) == 127))
                    {
                        this.result = ae.value.e1;
                        return ;
                    }
                    Ref<Expression> e0 = ref(null);
                    Ref<Expression> ae1save = ref(ae.value.e1);
                    ae.value.lengthVar.value = null;
                    Ref<Type> t1b = ref(ae.value.e1.type.value.toBasetype());
                    Ref<AggregateDeclaration> ad = ref(isAggregate(t1b.value));
                    if (ad.value == null)
                        break;
                    try {
                        if (search_function(ad.value, Id.opIndexOpAssign.value) != null)
                        {
                            this.result = resolveOpDollar(this.sc, ae.value, ptr(e0));
                            if (this.result == null)
                                /*goto Lfallback*/throw Dispatch0.INSTANCE;
                            if (((this.result.op & 0xFF) == 127))
                                return ;
                            this.result = expressionSemantic(e_ref.value.e2.value, this.sc);
                            if (((this.result.op & 0xFF) == 127))
                                return ;
                            e_ref.value.e2.value = this.result;
                            Ref<Ptr<DArray<Expression>>> a = ref((ae.value.arguments.get()).copy());
                            (a.value.get()).insert(0, e_ref.value.e2.value);
                            Ref<Ptr<DArray<RootObject>>> tiargs = ref(opToArg(this.sc, e_ref.value.op));
                            this.result = new DotTemplateInstanceExp(e_ref.value.loc, ae.value.e1, Id.opIndexOpAssign.value, tiargs.value);
                            this.result = new CallExp(e_ref.value.loc, this.result, a.value);
                            if (maybeSlice)
                                this.result = trySemantic(this.result, this.sc);
                            else
                                this.result = expressionSemantic(this.result, this.sc);
                            if (this.result != null)
                            {
                                this.result = Expression.combine(e0.value, this.result);
                                return ;
                            }
                        }
                    }
                    catch(Dispatch0 __d){}
                /*Lfallback:*/
                    if (maybeSlice && (search_function(ad.value, Id.opSliceOpAssign.value) != null))
                    {
                        this.result = resolveOpDollar(this.sc, ae.value, ie.value, ptr(e0));
                        if (((this.result.op & 0xFF) == 127))
                            return ;
                        this.result = expressionSemantic(e_ref.value.e2.value, this.sc);
                        if (((this.result.op & 0xFF) == 127))
                            return ;
                        e_ref.value.e2.value = this.result;
                        Ref<Ptr<DArray<Expression>>> a = ref(new DArray<Expression>());
                        (a.value.get()).push(e_ref.value.e2.value);
                        if (ie.value != null)
                        {
                            (a.value.get()).push(ie.value.lwr.value);
                            (a.value.get()).push(ie.value.upr.value);
                        }
                        Ref<Ptr<DArray<RootObject>>> tiargs = ref(opToArg(this.sc, e_ref.value.op));
                        this.result = new DotTemplateInstanceExp(e_ref.value.loc, ae.value.e1, Id.opSliceOpAssign.value, tiargs.value);
                        this.result = new CallExp(e_ref.value.loc, this.result, a.value);
                        this.result = expressionSemantic(this.result, this.sc);
                        this.result = Expression.combine(e0.value, this.result);
                        return ;
                    }
                    if ((ad.value.aliasthis != null) && (!pequals(t1b.value, ae.value.att1)))
                    {
                        if ((ae.value.att1 == null) && t1b.value.checkAliasThisRec())
                            ae.value.att1 = t1b.value;
                        ae.value.e1 = resolveAliasThis(this.sc, ae1save.value, true);
                        if (ae.value.e1 != null)
                            continue L_outer3;
                    }
                    break;
                }
                ae.value.e1 = ae1old.value;
                ae.value.lengthVar.value = null;
            }
            this.result = binSemanticProp(e_ref.value, this.sc);
            if (this.result != null)
                return ;
            if (((e_ref.value.e1.value.type.value.ty & 0xFF) == ENUMTY.Terror) || ((e_ref.value.e2.value.type.value.ty & 0xFF) == ENUMTY.Terror))
            {
                this.result = new ErrorExp();
                return ;
            }
            Ref<Identifier> id = ref(opId(e_ref.value));
            Ref<DArray<Expression>> args2 = ref(new DArray<Expression>());
            try {
                Ref<AggregateDeclaration> ad1 = ref(isAggregate(e_ref.value.e1.value.type.value));
                Ref<Dsymbol> s = ref(null);
                if ((ad1.value != null) && (id.value != null))
                {
                    s.value = search_function(ad1.value, id.value);
                }
                Ref<Ptr<DArray<RootObject>>> tiargs = ref(null);
                if (s.value == null)
                {
                    if (ad1.value != null)
                    {
                        s.value = search_function(ad1.value, Id.opOpAssign.value);
                        if ((s.value != null) && (s.value.isTemplateDeclaration() == null))
                        {
                            e_ref.value.error(new BytePtr("`%s.opOpAssign` isn't a template"), e_ref.value.e1.value.toChars());
                            this.result = new ErrorExp();
                            return ;
                        }
                    }
                    if (s.value != null)
                    {
                        id.value = Id.opOpAssign.value;
                        tiargs.value = opToArg(this.sc, e_ref.value.op);
                    }
                }
                try {
                    if (s.value != null)
                    {
                        args2.value.setDim(1);
                        args2.value.set(0, e_ref.value.e2.value);
                        expandTuples(ptr(args2));
                        Ref<MatchAccumulator> m = ref(new MatchAccumulator());
                        if (s.value != null)
                        {
                            functionResolve(m, s.value, e_ref.value.loc, this.sc, tiargs.value, e_ref.value.e1.value.type.value, ptr(args2), null);
                            if ((m.value.lastf != null) && m.value.lastf.errors || m.value.lastf.semantic3Errors)
                            {
                                this.result = new ErrorExp();
                                return ;
                            }
                        }
                        if ((m.value.count > 1))
                        {
                            e_ref.value.error(new BytePtr("overloads `%s` and `%s` both match argument list for `%s`"), m.value.lastf.type.toChars(), m.value.nextf.type.toChars(), m.value.lastf.toChars());
                        }
                        else if ((m.value.last <= MATCH.nomatch))
                        {
                            if (tiargs.value != null)
                                /*goto L1*/throw Dispatch0.INSTANCE;
                            m.value.lastf = null;
                        }
                        this.result = build_overload(e_ref.value.loc, this.sc, e_ref.value.e1.value, e_ref.value.e2.value, m.value.lastf != null ? m.value.lastf : s.value);
                        return ;
                    }
                }
                catch(Dispatch0 __d){}
            /*L1:*/
                this.result = checkAliasThisForLhs(ad1.value, this.sc, e_ref.value);
                if ((this.result != null) || (s.value == null))
                    return ;
                this.result = checkAliasThisForRhs(isAggregate(e_ref.value.e2.value.type.value), this.sc, e_ref.value);
            }
            finally {
            }
        }


        public OpOverload() {}
    }

    public static boolean isCommutative(byte op) {
        switch ((op & 0xFF))
        {
            case 74:
            case 78:
            case 84:
            case 85:
            case 86:
            case 58:
            case 59:
            case 54:
            case 56:
            case 55:
            case 57:
                return true;
            default:
            break;
        }
        return false;
    }

    public static Identifier opId(Expression e) {
        OpIdVisitor v = new OpIdVisitor();
        e.accept(v);
        return v.id;
    }

    public static Identifier opId_r(Expression e) {
        OpIdRVisitor v = new OpIdRVisitor();
        e.accept(v);
        return v.id;
    }

    public static Ptr<DArray<RootObject>> opToArg(Ptr<Scope> sc, byte op) {
        switch ((op & 0xFF))
        {
            case 76:
                op = TOK.add;
                break;
            case 77:
                op = TOK.min;
                break;
            case 81:
                op = TOK.mul;
                break;
            case 82:
                op = TOK.div;
                break;
            case 83:
                op = TOK.mod;
                break;
            case 87:
                op = TOK.and;
                break;
            case 88:
                op = TOK.or;
                break;
            case 89:
                op = TOK.xor;
                break;
            case 66:
                op = TOK.leftShift;
                break;
            case 67:
                op = TOK.rightShift;
                break;
            case 69:
                op = TOK.unsignedRightShift;
                break;
            case 71:
                op = TOK.concatenate;
                break;
            case 227:
                op = TOK.pow;
                break;
            default:
            break;
        }
        Expression e = new StringExp(Loc.initial.value, Token.toChars(op));
        e = expressionSemantic(e, sc);
        Ptr<DArray<RootObject>> tiargs = new DArray<RootObject>();
        (tiargs.get()).push(e);
        return tiargs;
    }

    public static Expression checkAliasThisForLhs(AggregateDeclaration ad, Ptr<Scope> sc, BinExp e) {
        if ((ad == null) || (ad.aliasthis == null))
            return null;
        if ((e.att1 != null) && (pequals(e.e1.value.type.value, e.att1)))
            return null;
        Expression e1 = new DotIdExp(e.loc, e.e1.value, ad.aliasthis.ident);
        BinExp be = (BinExp)e.copy();
        if ((be.att1 == null) && e.e1.value.type.value.checkAliasThisRec())
            be.att1 = e.e1.value.type.value;
        be.e1.value = e1;
        Expression result = null;
        if (((be.op & 0xFF) == 71))
            result = op_overload(be, sc, null);
        else
            result = trySemantic(be, sc);
        return result;
    }

    public static Expression checkAliasThisForRhs(AggregateDeclaration ad, Ptr<Scope> sc, BinExp e) {
        if ((ad == null) || (ad.aliasthis == null))
            return null;
        if ((e.att2 != null) && (pequals(e.e2.value.type.value, e.att2)))
            return null;
        Expression e2 = new DotIdExp(e.loc, e.e2.value, ad.aliasthis.ident);
        BinExp be = (BinExp)e.copy();
        if ((be.att2 == null) && e.e2.value.type.value.checkAliasThisRec())
            be.att2 = e.e2.value.type.value;
        be.e2.value = e2;
        Expression result = null;
        if (((be.op & 0xFF) == 71))
            result = op_overload(be, sc, null);
        else
            result = trySemantic(be, sc);
        return result;
    }

    public static Expression op_overload(Expression e, Ptr<Scope> sc, BytePtr pop) {
        if (pop != null)
            pop.set(0, e.op);
        OpOverload v = new OpOverload(sc, pop);
        e.accept(v);
        return v.result;
    }

    // defaulted all parameters starting with #3
    public static Expression op_overload(Expression e, Ptr<Scope> sc) {
        return op_overload(e, sc, null);
    }

    public static Expression compare_overload(BinExp e, Ptr<Scope> sc, Identifier id, BytePtr pop) {
        AggregateDeclaration ad1 = isAggregate(e.e1.value.type.value);
        AggregateDeclaration ad2 = isAggregate(e.e2.value.type.value);
        Dsymbol s = null;
        Dsymbol s_r = null;
        if (ad1 != null)
        {
            s = search_function(ad1, id);
        }
        if (ad2 != null)
        {
            s_r = search_function(ad2, id);
            if ((pequals(s, s_r)))
                s_r = null;
        }
        Ptr<DArray<RootObject>> tiargs = null;
        if ((s != null) || (s_r != null))
        {
            Ref<DArray<Expression>> args1 = ref(args1.value = new DArray<Expression>(1));
            try {
                args1.value.set(0, e.e1.value);
                expandTuples(ptr(args1));
                Ref<DArray<Expression>> args2 = ref(args2.value = new DArray<Expression>(1));
                try {
                    args2.value.set(0, e.e2.value);
                    expandTuples(ptr(args2));
                    Ref<MatchAccumulator> m = ref(new MatchAccumulator());
                    if (false)
                    {
                        printf(new BytePtr("s  : %s\n"), s.toPrettyChars(false));
                        printf(new BytePtr("s_r: %s\n"), s_r.toPrettyChars(false));
                    }
                    if (s != null)
                    {
                        functionResolve(m, s, e.loc, sc, tiargs, e.e1.value.type.value, ptr(args2), null);
                        if ((m.value.lastf != null) && m.value.lastf.errors || m.value.lastf.semantic3Errors)
                            return new ErrorExp();
                    }
                    FuncDeclaration lastf = m.value.lastf;
                    int count = m.value.count;
                    if (s_r != null)
                    {
                        functionResolve(m, s_r, e.loc, sc, tiargs, e.e2.value.type.value, ptr(args1), null);
                        if ((m.value.lastf != null) && m.value.lastf.errors || m.value.lastf.semantic3Errors)
                            return new ErrorExp();
                    }
                    if ((m.value.count > 1))
                    {
                        if (!((pequals(m.value.lastf, lastf)) && (m.value.count == 2) && (count == 1)))
                        {
                            e.error(new BytePtr("overloads `%s` and `%s` both match argument list for `%s`"), m.value.lastf.type.toChars(), m.value.nextf.type.toChars(), m.value.lastf.toChars());
                        }
                    }
                    else if ((m.value.last <= MATCH.nomatch))
                    {
                        m.value.lastf = null;
                    }
                    Expression result = null;
                    if ((lastf != null) && (pequals(m.value.lastf, lastf)) || (s_r == null) && (m.value.last <= MATCH.nomatch))
                    {
                        result = build_overload(e.loc, sc, e.e1.value, e.e2.value, m.value.lastf != null ? m.value.lastf : s);
                    }
                    else
                    {
                        result = build_overload(e.loc, sc, e.e2.value, e.e1.value, m.value.lastf != null ? m.value.lastf : s_r);
                        if (pop != null)
                            pop.set(0, reverseRelation(e.op));
                    }
                    return result;
                }
                finally {
                }
            }
            finally {
            }
        }
        if (((e.op & 0xFF) == 58) || ((e.op & 0xFF) == 59) && (pequals(ad1, ad2)))
            return null;
        Expression result = checkAliasThisForLhs(ad1, sc, e);
        return result != null ? result : checkAliasThisForRhs(isAggregate(e.e2.value.type.value), sc, e);
    }

    public static Expression build_overload(Loc loc, Ptr<Scope> sc, Expression ethis, Expression earg, Dsymbol d) {
        assert(d != null);
        Expression e = null;
        Declaration decl = d.isDeclaration();
        if (decl != null)
            e = new DotVarExp(loc, ethis, decl, false);
        else
            e = new DotIdExp(loc, ethis, d.ident);
        e = new CallExp(loc, e, earg);
        e = expressionSemantic(e, sc);
        return e;
    }

    public static Dsymbol search_function(ScopeDsymbol ad, Identifier funcid) {
        Dsymbol s = ad.search(Loc.initial.value, funcid, 8);
        if (s != null)
        {
            Dsymbol s2 = s.toAlias();
            FuncDeclaration fd = s2.isFuncDeclaration();
            if ((fd != null) && ((fd.type.ty & 0xFF) == ENUMTY.Tfunction))
                return fd;
            TemplateDeclaration td = s2.isTemplateDeclaration();
            if (td != null)
                return td;
        }
        return null;
    }

    public static boolean inferForeachAggregate(Ptr<Scope> sc, boolean isForeach, Ref<Expression> feaggr, Ref<Dsymbol> sapply) {
        sapply.value = null;
        boolean sliced = false;
        Type att = null;
        Expression aggr = feaggr.value;
        for (; 1 != 0;){
            aggr = expressionSemantic(aggr, sc);
            aggr = resolveProperties(sc, aggr);
            aggr = aggr.optimize(0, false);
            if ((aggr.type.value == null) || ((aggr.op & 0xFF) == 127))
                return false;
            Type tab = aggr.type.value.toBasetype();
            switch ((tab.ty & 0xFF))
            {
                case 0:
                case 1:
                case 37:
                case 2:
                    break;
                case 7:
                case 8:
                    AggregateDeclaration ad = ((tab.ty & 0xFF) == ENUMTY.Tclass) ? ((TypeClass)tab).sym : ((TypeStruct)tab).sym;
                    if (!sliced)
                    {
                        sapply.value = search_function(ad, isForeach ? Id.apply : Id.applyReverse);
                        if (sapply.value != null)
                        {
                            break;
                        }
                        if (((feaggr.value.op & 0xFF) != 20))
                        {
                            Expression rinit = new ArrayExp(aggr.loc, feaggr.value, null);
                            rinit = trySemantic(rinit, sc);
                            if (rinit != null)
                            {
                                aggr = rinit;
                                sliced = true;
                                continue;
                            }
                        }
                    }
                    if (ad.search(Loc.initial.value, isForeach ? Id.Ffront : Id.Fback, 8) != null)
                    {
                        break;
                    }
                    if (ad.aliasthis != null)
                    {
                        if ((pequals(att, tab)))
                            return false;
                        if ((att == null) && tab.checkAliasThisRec())
                            att = tab;
                        aggr = resolveAliasThis(sc, aggr, false);
                        continue;
                    }
                    return false;
                case 10:
                    if (((aggr.op & 0xFF) == 160))
                    {
                        sapply.value = ((DelegateExp)aggr).func;
                    }
                    break;
                case 34:
                    break;
                default:
                return false;
            }
            feaggr.value = aggr;
            return true;
        }
        throw new AssertionError("Unreachable code!");
    }

    public static boolean inferApplyArgTypes(ForeachStatement fes, Ptr<Scope> sc, Ref<Dsymbol> sapply) {
        if ((fes.parameters == null) || ((fes.parameters.get()).length == 0))
            return false;
        if (sapply.value != null)
        {
            {
                Slice<Parameter> __r1533 = (fes.parameters.get()).opSlice().copy();
                int __key1534 = 0;
                for (; (__key1534 < __r1533.getLength());__key1534 += 1) {
                    Parameter p = __r1533.get(__key1534);
                    if (p.type != null)
                    {
                        p.type = typeSemantic(p.type, fes.loc, sc);
                        p.type = p.type.addStorageClass(p.storageClass);
                    }
                }
            }
            Expression ethis = null;
            Type tab = fes.aggr.type.value.toBasetype();
            if (((tab.ty & 0xFF) == ENUMTY.Tclass) || ((tab.ty & 0xFF) == ENUMTY.Tstruct))
                ethis = fes.aggr;
            else
            {
                assert(((tab.ty & 0xFF) == ENUMTY.Tdelegate) && ((fes.aggr.op & 0xFF) == 160));
                ethis = ((DelegateExp)fes.aggr).e1;
            }
            {
                FuncDeclaration fd = sapply.value.isFuncDeclaration();
                if ((fd) != null)
                {
                    FuncDeclaration fdapply = findBestOpApplyMatch(ethis, fd, fes.parameters);
                    if (fdapply != null)
                    {
                        matchParamsToOpApply((TypeFunction)fdapply.type, fes.parameters, true);
                        sapply.value = fdapply;
                        return true;
                    }
                    return false;
                }
            }
            return sapply.value != null;
        }
        Parameter p = (fes.parameters.get()).get(0);
        Type taggr = fes.aggr.type.value;
        assert(taggr != null);
        Type tab = taggr.toBasetype();
        switch ((tab.ty & 0xFF))
        {
            case 0:
            case 1:
            case 37:
                if (((fes.parameters.get()).length == 2))
                {
                    if (p.type == null)
                    {
                        p.type = Type.tsize_t.value;
                        p.type = p.type.addStorageClass(p.storageClass);
                    }
                    p = (fes.parameters.get()).get(1);
                }
                if ((p.type == null) && ((tab.ty & 0xFF) != ENUMTY.Ttuple))
                {
                    p.type = tab.nextOf();
                    p.type = p.type.addStorageClass(p.storageClass);
                }
                break;
            case 2:
                TypeAArray taa = (TypeAArray)tab;
                if (((fes.parameters.get()).length == 2))
                {
                    if (p.type == null)
                    {
                        p.type = taa.index;
                        p.type = p.type.addStorageClass(p.storageClass);
                        if ((p.storageClass & 2097152L) != 0)
                            p.type = p.type.addMod((byte)1);
                    }
                    p = (fes.parameters.get()).get(1);
                }
                if (p.type == null)
                {
                    p.type = taa.next;
                    p.type = p.type.addStorageClass(p.storageClass);
                }
                break;
            case 7:
            case 8:
                AggregateDeclaration ad = ((tab.ty & 0xFF) == ENUMTY.Tclass) ? ((TypeClass)tab).sym : ((TypeStruct)tab).sym;
                if (((fes.parameters.get()).length == 1))
                {
                    if (p.type == null)
                    {
                        Identifier id = ((fes.op & 0xFF) == 201) ? Id.Ffront : Id.Fback;
                        Dsymbol s = ad.search(Loc.initial.value, id, 8);
                        FuncDeclaration fd = s != null ? s.isFuncDeclaration() : null;
                        if (fd != null)
                        {
                            p.type = fd.type.nextOf();
                            if (p.type != null)
                            {
                                p.type = p.type.substWildTo((tab.mod & 0xFF));
                                p.type = p.type.addStorageClass(p.storageClass);
                            }
                        }
                        else if ((s != null) && (s.isTemplateDeclaration() != null))
                        {
                        }
                        else if ((s != null) && (s.isDeclaration() != null))
                            p.type = ((Declaration)s).type;
                        else
                            break;
                    }
                    break;
                }
                break;
            case 10:
                if (!matchParamsToOpApply((TypeFunction)tab.nextOf(), fes.parameters, true))
                    return false;
                break;
            default:
            break;
        }
        return true;
    }

    public static FuncDeclaration findBestOpApplyMatch(Expression ethis, FuncDeclaration fstart, Ptr<DArray<Parameter>> parameters) {
        byte mod = ethis.type.value.mod;
        int match = MATCH.nomatch;
        FuncDeclaration fd_best = null;
        FuncDeclaration fd_ambig = null;
        Function1<Dsymbol,Integer> __lambda4 = new Function1<Dsymbol,Integer>(){
            public Integer invoke(Dsymbol s) {
                FuncDeclaration f = s.isFuncDeclaration();
                if (f == null)
                    return 0;
                TypeFunction tf = (TypeFunction)f.type;
                int m = MATCH.exact;
                if (f.isThis() != null)
                {
                    if (!MODimplicitConv(mod, tf.mod))
                        m = MATCH.nomatch;
                    else if (((mod & 0xFF) != (tf.mod & 0xFF)))
                        m = MATCH.constant;
                }
                if (!matchParamsToOpApply(tf, parameters, false))
                    m = MATCH.nomatch;
                if ((m > match))
                {
                    fd_best = f;
                    fd_ambig = null;
                    match = m;
                }
                else if ((m == match) && (m > MATCH.nomatch))
                {
                    assert(fd_best != null);
                    if ((tf.covariant(fd_best.type, null, true) != 1) && (fd_best.type.covariant(tf, null, true) != 1))
                        fd_ambig = f;
                }
                return 0;
            }
        };
        overloadApply(fstart, __lambda4, null);
        if (fd_ambig != null)
        {
            error(ethis.loc, new BytePtr("`%s.%s` matches more than one declaration:\n`%s`:     `%s`\nand:\n`%s`:     `%s`"), ethis.toChars(), fstart.ident.toChars(), fd_best.loc.toChars(global.value.params.showColumns), fd_best.type.toChars(), fd_ambig.loc.toChars(global.value.params.showColumns), fd_ambig.type.toChars());
            return null;
        }
        return fd_best;
    }

    public static boolean matchParamsToOpApply(TypeFunction tf, Ptr<DArray<Parameter>> parameters, boolean infer) {
        boolean nomatch = false;
        if ((tf.parameterList.length() != 1))
            return false;
        Parameter p0 = tf.parameterList.get(0);
        if (((p0.type.ty & 0xFF) != ENUMTY.Tdelegate))
            return false;
        TypeFunction tdg = (TypeFunction)p0.type.nextOf();
        assert(((tdg.ty & 0xFF) == ENUMTY.Tfunction));
        int nparams = tdg.parameterList.length();
        if ((nparams == 0) || (nparams != (parameters.get()).length) || (tdg.parameterList.varargs != VarArg.none))
            return false;
        {
            Slice<Parameter> __r1536 = (parameters.get()).opSlice().copy();
            int __key1535 = 0;
            for (; (__key1535 < __r1536.getLength());__key1535 += 1) {
                Parameter p = __r1536.get(__key1535);
                int u = __key1535;
                Parameter param = tdg.parameterList.get(u);
                if (p.type != null)
                {
                    if (!p.type.equals(param.type))
                        return false;
                }
                else if (infer)
                {
                    p.type = param.type;
                    p.type = p.type.addStorageClass(p.storageClass);
                }
            }
        }
        return true;
    }

    public static byte reverseRelation(byte op) {
        switch ((op & 0xFF))
        {
            case 57:
                op = TOK.lessOrEqual;
                break;
            case 55:
                op = TOK.lessThan;
                break;
            case 56:
                op = TOK.greaterOrEqual;
                break;
            case 54:
                op = TOK.greaterThan;
                break;
            default:
            break;
        }
        return op;
    }

}
