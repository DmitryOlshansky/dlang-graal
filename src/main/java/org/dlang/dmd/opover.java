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
        // Erasure: visit<Expression>
        public  void visit(Expression e) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<UAddExp>
        public  void visit(UAddExp e) {
            this.id = Id.uadd;
        }

        // Erasure: visit<NegExp>
        public  void visit(NegExp e) {
            this.id = Id.neg;
        }

        // Erasure: visit<ComExp>
        public  void visit(ComExp e) {
            this.id = Id.com;
        }

        // Erasure: visit<CastExp>
        public  void visit(CastExp e) {
            this.id = Id._cast;
        }

        // Erasure: visit<InExp>
        public  void visit(InExp e) {
            this.id = Id.opIn;
        }

        // Erasure: visit<PostExp>
        public  void visit(PostExp e) {
            this.id = ((e.op & 0xFF) == 93) ? Id.postinc : Id.postdec;
        }

        // Erasure: visit<AddExp>
        public  void visit(AddExp e) {
            this.id = Id.add;
        }

        // Erasure: visit<MinExp>
        public  void visit(MinExp e) {
            this.id = Id.sub;
        }

        // Erasure: visit<MulExp>
        public  void visit(MulExp e) {
            this.id = Id.mul;
        }

        // Erasure: visit<DivExp>
        public  void visit(DivExp e) {
            this.id = Id.div;
        }

        // Erasure: visit<ModExp>
        public  void visit(ModExp e) {
            this.id = Id.mod;
        }

        // Erasure: visit<PowExp>
        public  void visit(PowExp e) {
            this.id = Id.pow;
        }

        // Erasure: visit<ShlExp>
        public  void visit(ShlExp e) {
            this.id = Id.shl;
        }

        // Erasure: visit<ShrExp>
        public  void visit(ShrExp e) {
            this.id = Id.shr;
        }

        // Erasure: visit<UshrExp>
        public  void visit(UshrExp e) {
            this.id = Id.ushr;
        }

        // Erasure: visit<AndExp>
        public  void visit(AndExp e) {
            this.id = Id.iand;
        }

        // Erasure: visit<OrExp>
        public  void visit(OrExp e) {
            this.id = Id.ior;
        }

        // Erasure: visit<XorExp>
        public  void visit(XorExp e) {
            this.id = Id.ixor;
        }

        // Erasure: visit<CatExp>
        public  void visit(CatExp e) {
            this.id = Id.cat;
        }

        // Erasure: visit<AssignExp>
        public  void visit(AssignExp e) {
            this.id = Id.assign;
        }

        // Erasure: visit<AddAssignExp>
        public  void visit(AddAssignExp e) {
            this.id = Id.addass;
        }

        // Erasure: visit<MinAssignExp>
        public  void visit(MinAssignExp e) {
            this.id = Id.subass;
        }

        // Erasure: visit<MulAssignExp>
        public  void visit(MulAssignExp e) {
            this.id = Id.mulass;
        }

        // Erasure: visit<DivAssignExp>
        public  void visit(DivAssignExp e) {
            this.id = Id.divass;
        }

        // Erasure: visit<ModAssignExp>
        public  void visit(ModAssignExp e) {
            this.id = Id.modass;
        }

        // Erasure: visit<AndAssignExp>
        public  void visit(AndAssignExp e) {
            this.id = Id.andass;
        }

        // Erasure: visit<OrAssignExp>
        public  void visit(OrAssignExp e) {
            this.id = Id.orass;
        }

        // Erasure: visit<XorAssignExp>
        public  void visit(XorAssignExp e) {
            this.id = Id.xorass;
        }

        // Erasure: visit<ShlAssignExp>
        public  void visit(ShlAssignExp e) {
            this.id = Id.shlass;
        }

        // Erasure: visit<ShrAssignExp>
        public  void visit(ShrAssignExp e) {
            this.id = Id.shrass;
        }

        // Erasure: visit<UshrAssignExp>
        public  void visit(UshrAssignExp e) {
            this.id = Id.ushrass;
        }

        // Erasure: visit<CatAssignExp>
        public  void visit(CatAssignExp e) {
            this.id = Id.catass;
        }

        // Erasure: visit<PowAssignExp>
        public  void visit(PowAssignExp e) {
            this.id = Id.powass;
        }

        // Erasure: visit<EqualExp>
        public  void visit(EqualExp e) {
            this.id = Id.eq;
        }

        // Erasure: visit<CmpExp>
        public  void visit(CmpExp e) {
            this.id = Id.cmp;
        }

        // Erasure: visit<ArrayExp>
        public  void visit(ArrayExp e) {
            this.id = Id.index;
        }

        // Erasure: visit<PtrExp>
        public  void visit(PtrExp e) {
            this.id = Id.opStar;
        }


        public OpIdVisitor() {}
    }
    private static class OpIdRVisitor extends Visitor
    {
        private Identifier id = null;
        // Erasure: visit<Expression>
        public  void visit(Expression e) {
            this.id = null;
        }

        // Erasure: visit<InExp>
        public  void visit(InExp e) {
            this.id = Id.opIn_r;
        }

        // Erasure: visit<AddExp>
        public  void visit(AddExp e) {
            this.id = Id.add_r;
        }

        // Erasure: visit<MinExp>
        public  void visit(MinExp e) {
            this.id = Id.sub_r;
        }

        // Erasure: visit<MulExp>
        public  void visit(MulExp e) {
            this.id = Id.mul_r;
        }

        // Erasure: visit<DivExp>
        public  void visit(DivExp e) {
            this.id = Id.div_r;
        }

        // Erasure: visit<ModExp>
        public  void visit(ModExp e) {
            this.id = Id.mod_r;
        }

        // Erasure: visit<PowExp>
        public  void visit(PowExp e) {
            this.id = Id.pow_r;
        }

        // Erasure: visit<ShlExp>
        public  void visit(ShlExp e) {
            this.id = Id.shl_r;
        }

        // Erasure: visit<ShrExp>
        public  void visit(ShrExp e) {
            this.id = Id.shr_r;
        }

        // Erasure: visit<UshrExp>
        public  void visit(UshrExp e) {
            this.id = Id.ushr_r;
        }

        // Erasure: visit<AndExp>
        public  void visit(AndExp e) {
            this.id = Id.iand_r;
        }

        // Erasure: visit<OrExp>
        public  void visit(OrExp e) {
            this.id = Id.ior_r;
        }

        // Erasure: visit<XorExp>
        public  void visit(XorExp e) {
            this.id = Id.ixor_r;
        }

        // Erasure: visit<CatExp>
        public  void visit(CatExp e) {
            this.id = Id.cat_r;
        }


        public OpIdRVisitor() {}
    }
    private static class OpOverload extends Visitor
    {
        private Ptr<Scope> sc = null;
        private BytePtr pop = null;
        private Expression result = null;
        // Erasure: __ctor<Ptr, Ptr>
        public  OpOverload(Ptr<Scope> sc, BytePtr pop) {
            this.sc = pcopy(sc);
            this.pop = pcopy(pop);
        }

        // Erasure: visit<Expression>
        public  void visit(Expression e) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<UnaExp>
        public  void visit(UnaExp e) {
            if (((e.e1.value.op & 0xFF) == 17))
            {
                ArrayExp ae = (ArrayExp)e.e1.value;
                ae.e1.value = expressionSemantic(ae.e1.value, this.sc);
                ae.e1.value = resolveProperties(this.sc, ae.e1.value);
                Expression ae1old = ae.e1.value;
                boolean maybeSlice = ((ae.arguments.get()).length == 0) || ((ae.arguments.get()).length == 1) && (((ae.arguments.get()).get(0).op & 0xFF) == 231);
                IntervalExp ie = null;
                if (maybeSlice && ((ae.arguments.get()).length != 0))
                {
                    assert((((ae.arguments.get()).get(0).op & 0xFF) == 231));
                    ie = (IntervalExp)(ae.arguments.get()).get(0);
                }
            L_outer1:
                for (; true;){
                    if (((ae.e1.value.op & 0xFF) == 127))
                    {
                        this.result = ae.e1.value;
                        return ;
                    }
                    Ref<Expression> e0 = ref(null);
                    Expression ae1save = ae.e1.value;
                    ae.lengthVar.value = null;
                    Type t1b = ae.e1.value.type.value.toBasetype();
                    AggregateDeclaration ad = isAggregate(t1b);
                    if (ad == null)
                    {
                        break;
                    }
                    try {
                        if (search_function(ad, Id.opIndexUnary) != null)
                        {
                            this.result = resolveOpDollar(this.sc, ae, ptr(e0));
                            if (this.result == null)
                            {
                                /*goto Lfallback*/throw Dispatch0.INSTANCE;
                            }
                            if (((this.result.op & 0xFF) == 127))
                            {
                                return ;
                            }
                            Ptr<DArray<Expression>> a = (ae.arguments.get()).copy();
                            Ptr<DArray<RootObject>> tiargs = opToArg(this.sc, e.op);
                            this.result = new DotTemplateInstanceExp(e.loc, ae.e1.value, Id.opIndexUnary, tiargs);
                            this.result = new CallExp(e.loc, this.result, a);
                            if (maybeSlice)
                            {
                                this.result = trySemantic(this.result, this.sc);
                            }
                            else
                            {
                                this.result = expressionSemantic(this.result, this.sc);
                            }
                            if (this.result != null)
                            {
                                this.result = Expression.combine(e0.value, this.result);
                                return ;
                            }
                        }
                    }
                    catch(Dispatch0 __d){}
                /*Lfallback:*/
                    if (maybeSlice && (search_function(ad, Id.opSliceUnary) != null))
                    {
                        this.result = resolveOpDollar(this.sc, ae, ie, ptr(e0));
                        if (((this.result.op & 0xFF) == 127))
                        {
                            return ;
                        }
                        Ptr<DArray<Expression>> a = refPtr(new DArray<Expression>());
                        if (ie != null)
                        {
                            (a.get()).push(ie.lwr.value);
                            (a.get()).push(ie.upr.value);
                        }
                        Ptr<DArray<RootObject>> tiargs = opToArg(this.sc, e.op);
                        this.result = new DotTemplateInstanceExp(e.loc, ae.e1.value, Id.opSliceUnary, tiargs);
                        this.result = new CallExp(e.loc, this.result, a);
                        this.result = expressionSemantic(this.result, this.sc);
                        this.result = Expression.combine(e0.value, this.result);
                        return ;
                    }
                    if ((ad.aliasthis != null) && (!pequals(t1b, ae.att1)))
                    {
                        if ((ae.att1 == null) && t1b.checkAliasThisRec())
                        {
                            ae.att1 = t1b;
                        }
                        ae.e1.value = resolveAliasThis(this.sc, ae1save, true);
                        if (ae.e1.value != null)
                        {
                            continue L_outer1;
                        }
                    }
                    break;
                }
                ae.e1.value = ae1old;
                ae.lengthVar.value = null;
            }
            e.e1.value = expressionSemantic(e.e1.value, this.sc);
            e.e1.value = resolveProperties(this.sc, e.e1.value);
            if (((e.e1.value.op & 0xFF) == 127))
            {
                this.result = e.e1.value;
                return ;
            }
            AggregateDeclaration ad = isAggregate(e.e1.value.type.value);
            if (ad != null)
            {
                Dsymbol fd = null;
                if (((e.op & 0xFF) != 103) && ((e.op & 0xFF) != 104))
                {
                    fd = search_function(ad, opId(e));
                    if (fd != null)
                    {
                        this.result = build_overload(e.loc, this.sc, e.e1.value, null, fd);
                        return ;
                    }
                }
                fd = search_function(ad, Id.opUnary);
                if (fd != null)
                {
                    Ptr<DArray<RootObject>> tiargs = opToArg(this.sc, e.op);
                    this.result = new DotTemplateInstanceExp(e.loc, e.e1.value, fd.ident, tiargs);
                    this.result = new CallExp(e.loc, this.result);
                    this.result = expressionSemantic(this.result, this.sc);
                    return ;
                }
                if ((ad.aliasthis != null) && (!pequals(e.e1.value.type.value, e.att1)))
                {
                    Expression e1 = new DotIdExp(e.loc, e.e1.value, ad.aliasthis.ident);
                    UnaExp ue = (UnaExp)e.copy();
                    if ((ue.att1 == null) && e.e1.value.type.value.checkAliasThisRec())
                    {
                        ue.att1 = e.e1.value.type.value;
                    }
                    ue.e1.value = e1;
                    this.result = trySemantic(ue, this.sc);
                    return ;
                }
            }
        }

        // Erasure: visit<ArrayExp>
        public  void visit(ArrayExp ae) {
            ae.e1.value = expressionSemantic(ae.e1.value, this.sc);
            ae.e1.value = resolveProperties(this.sc, ae.e1.value);
            Expression ae1old = ae.e1.value;
            boolean maybeSlice = ((ae.arguments.get()).length == 0) || ((ae.arguments.get()).length == 1) && (((ae.arguments.get()).get(0).op & 0xFF) == 231);
            IntervalExp ie = null;
            if (maybeSlice && ((ae.arguments.get()).length != 0))
            {
                assert((((ae.arguments.get()).get(0).op & 0xFF) == 231));
                ie = (IntervalExp)(ae.arguments.get()).get(0);
            }
        L_outer2:
            for (; true;){
                if (((ae.e1.value.op & 0xFF) == 127))
                {
                    this.result = ae.e1.value;
                    return ;
                }
                Ref<Expression> e0 = ref(null);
                Expression ae1save = ae.e1.value;
                ae.lengthVar.value = null;
                Type t1b = ae.e1.value.type.value.toBasetype();
                AggregateDeclaration ad = isAggregate(t1b);
                if (ad == null)
                {
                    if (isIndexableNonAggregate(t1b) || ((ae.e1.value.op & 0xFF) == 20))
                    {
                        if (maybeSlice)
                        {
                            this.result = new SliceExp(ae.loc, ae.e1.value, ie);
                            this.result = expressionSemantic(this.result, this.sc);
                            return ;
                        }
                        if (((ae.arguments.get()).length == 1))
                        {
                            this.result = new IndexExp(ae.loc, ae.e1.value, (ae.arguments.get()).get(0));
                            this.result = expressionSemantic(this.result, this.sc);
                            return ;
                        }
                    }
                    break;
                }
                try {
                    if (search_function(ad, Id.index) != null)
                    {
                        this.result = resolveOpDollar(this.sc, ae, ptr(e0));
                        if (this.result == null)
                        {
                            /*goto Lfallback*/throw Dispatch0.INSTANCE;
                        }
                        if (((this.result.op & 0xFF) == 127))
                        {
                            return ;
                        }
                        Ptr<DArray<Expression>> a = (ae.arguments.get()).copy();
                        this.result = new DotIdExp(ae.loc, ae.e1.value, Id.index);
                        this.result = new CallExp(ae.loc, this.result, a);
                        if (maybeSlice)
                        {
                            this.result = trySemantic(this.result, this.sc);
                        }
                        else
                        {
                            this.result = expressionSemantic(this.result, this.sc);
                        }
                        if (this.result != null)
                        {
                            this.result = Expression.combine(e0.value, this.result);
                            return ;
                        }
                    }
                }
                catch(Dispatch0 __d){}
            /*Lfallback:*/
                if (maybeSlice && ((ae.e1.value.op & 0xFF) == 20))
                {
                    this.result = new SliceExp(ae.loc, ae.e1.value, ie);
                    this.result = expressionSemantic(this.result, this.sc);
                    this.result = Expression.combine(e0.value, this.result);
                    return ;
                }
                if (maybeSlice && (search_function(ad, Id.slice) != null))
                {
                    this.result = resolveOpDollar(this.sc, ae, ie, ptr(e0));
                    if (((this.result.op & 0xFF) == 127))
                    {
                        return ;
                    }
                    Ptr<DArray<Expression>> a = refPtr(new DArray<Expression>());
                    if (ie != null)
                    {
                        (a.get()).push(ie.lwr.value);
                        (a.get()).push(ie.upr.value);
                    }
                    this.result = new DotIdExp(ae.loc, ae.e1.value, Id.slice);
                    this.result = new CallExp(ae.loc, this.result, a);
                    this.result = expressionSemantic(this.result, this.sc);
                    this.result = Expression.combine(e0.value, this.result);
                    return ;
                }
                if ((ad.aliasthis != null) && (!pequals(t1b, ae.att1)))
                {
                    if ((ae.att1 == null) && t1b.checkAliasThisRec())
                    {
                        ae.att1 = t1b;
                    }
                    ae.e1.value = resolveAliasThis(this.sc, ae1save, true);
                    if (ae.e1.value != null)
                    {
                        continue L_outer2;
                    }
                }
                break;
            }
            ae.e1.value = ae1old;
            ae.lengthVar.value = null;
        }

        // Erasure: visit<CastExp>
        public  void visit(CastExp e) {
            AggregateDeclaration ad = isAggregate(e.e1.value.type.value);
            if (ad != null)
            {
                Dsymbol fd = null;
                fd = search_function(ad, Id._cast);
                if (fd != null)
                {
                    if (fd.isFuncDeclaration() != null)
                    {
                        this.result = build_overload(e.loc, this.sc, e.e1.value, null, fd);
                        return ;
                    }
                    Ptr<DArray<RootObject>> tiargs = refPtr(new DArray<RootObject>());
                    (tiargs.get()).push(e.to);
                    this.result = new DotTemplateInstanceExp(e.loc, e.e1.value, fd.ident, tiargs);
                    this.result = new CallExp(e.loc, this.result);
                    this.result = expressionSemantic(this.result, this.sc);
                    return ;
                }
                if (ad.aliasthis != null)
                {
                    Expression e1 = resolveAliasThis(this.sc, e.e1.value, false);
                    this.result = e.copy();
                    ((UnaExp)this.result).e1.value = e1;
                    this.result = op_overload(this.result, this.sc, null);
                    return ;
                }
            }
        }

        // Erasure: visit<BinExp>
        public  void visit(BinExp e) {
            Identifier id = opId(e);
            Identifier id_r = opId_r(e);
            Ref<DArray<Expression>> args1 = ref(new DArray<Expression>());
            try {
                Ref<DArray<Expression>> args2 = ref(new DArray<Expression>());
                try {
                    int argsset = 0;
                    AggregateDeclaration ad1 = isAggregate(e.e1.value.type.value);
                    AggregateDeclaration ad2 = isAggregate(e.e2.value.type.value);
                    if (((e.op & 0xFF) == 90) && (pequals(ad1, ad2)))
                    {
                        StructDeclaration sd = ad1.isStructDeclaration();
                        if ((sd != null) && !sd.hasIdentityAssign)
                        {
                            return ;
                        }
                    }
                    Dsymbol s = null;
                    Dsymbol s_r = null;
                    if ((ad1 != null) && (id != null))
                    {
                        s = search_function(ad1, id);
                    }
                    if ((ad2 != null) && (id_r != null))
                    {
                        s_r = search_function(ad2, id_r);
                        if ((s_r != null) && (pequals(s_r, s)))
                        {
                            s_r = null;
                        }
                    }
                    Ptr<DArray<RootObject>> tiargs = null;
                    if (((e.op & 0xFF) == 93) || ((e.op & 0xFF) == 94))
                    {
                        if ((ad1 != null) && (search_function(ad1, Id.opUnary) != null))
                        {
                            return ;
                        }
                    }
                    if ((s == null) && (s_r == null) && ((e.op & 0xFF) != 58) && ((e.op & 0xFF) != 59) && ((e.op & 0xFF) != 90) && ((e.op & 0xFF) != 93) && ((e.op & 0xFF) != 94))
                    {
                        if (ad1 != null)
                        {
                            s = search_function(ad1, Id.opBinary);
                            if ((s != null) && (s.isTemplateDeclaration() == null))
                            {
                                e.e1.value.error(new BytePtr("`%s.opBinary` isn't a template"), e.e1.value.toChars());
                                this.result = new ErrorExp();
                                return ;
                            }
                        }
                        if (ad2 != null)
                        {
                            s_r = search_function(ad2, Id.opBinaryRight);
                            if ((s_r != null) && (s_r.isTemplateDeclaration() == null))
                            {
                                e.e2.value.error(new BytePtr("`%s.opBinaryRight` isn't a template"), e.e2.value.toChars());
                                this.result = new ErrorExp();
                                return ;
                            }
                            if ((s_r != null) && (pequals(s_r, s)))
                            {
                                s_r = null;
                            }
                        }
                        if ((s != null) || (s_r != null))
                        {
                            id = Id.opBinary;
                            id_r = Id.opBinaryRight;
                            tiargs = pcopy(opToArg(this.sc, e.op));
                        }
                    }
                    try {
                        if ((s != null) || (s_r != null))
                        {
                            args1.value.setDim(1);
                            args1.value.set(0, e.e1.value);
                            expandTuples(ptr(args1));
                            args2.value.setDim(1);
                            args2.value.set(0, e.e2.value);
                            expandTuples(ptr(args2));
                            argsset = 1;
                            Ref<MatchAccumulator> m = ref(new MatchAccumulator());
                            if (s != null)
                            {
                                functionResolve(m, s, e.loc, this.sc, tiargs, e.e1.value.type.value, ptr(args2), null);
                                if ((m.value.lastf != null) && m.value.lastf.errors || m.value.lastf.semantic3Errors)
                                {
                                    this.result = new ErrorExp();
                                    return ;
                                }
                            }
                            FuncDeclaration lastf = m.value.lastf;
                            if (s_r != null)
                            {
                                functionResolve(m, s_r, e.loc, this.sc, tiargs, e.e2.value.type.value, ptr(args1), null);
                                if ((m.value.lastf != null) && m.value.lastf.errors || m.value.lastf.semantic3Errors)
                                {
                                    this.result = new ErrorExp();
                                    return ;
                                }
                            }
                            if ((m.value.count > 1))
                            {
                                e.error(new BytePtr("overloads `%s` and `%s` both match argument list for `%s`"), m.value.lastf.type.toChars(), m.value.nextf.type.toChars(), m.value.lastf.toChars());
                            }
                            else if ((m.value.last <= MATCH.nomatch))
                            {
                                if (tiargs != null)
                                {
                                    /*goto L1*/throw Dispatch0.INSTANCE;
                                }
                                m.value.lastf = null;
                            }
                            if (((e.op & 0xFF) == 93) || ((e.op & 0xFF) == 94))
                            {
                                this.result = build_overload(e.loc, this.sc, e.e1.value, null, m.value.lastf != null ? m.value.lastf : s);
                            }
                            else if ((lastf != null) && (pequals(m.value.lastf, lastf)) || (s_r == null) && (m.value.last <= MATCH.nomatch))
                            {
                                this.result = build_overload(e.loc, this.sc, e.e1.value, e.e2.value, m.value.lastf != null ? m.value.lastf : s);
                            }
                            else
                            {
                                this.result = build_overload(e.loc, this.sc, e.e2.value, e.e1.value, m.value.lastf != null ? m.value.lastf : s_r);
                            }
                            return ;
                        }
                    }
                    catch(Dispatch0 __d){}
                /*L1:*/
                    if (isCommutative(e.op) && (tiargs == null))
                    {
                        s = null;
                        s_r = null;
                        if ((ad1 != null) && (id_r != null))
                        {
                            s_r = search_function(ad1, id_r);
                        }
                        if ((ad2 != null) && (id != null))
                        {
                            s = search_function(ad2, id);
                            if ((s != null) && (pequals(s, s_r)))
                            {
                                s = null;
                            }
                        }
                        if ((s != null) || (s_r != null))
                        {
                            if (argsset == 0)
                            {
                                args1.value.setDim(1);
                                args1.value.set(0, e.e1.value);
                                expandTuples(ptr(args1));
                                args2.value.setDim(1);
                                args2.value.set(0, e.e2.value);
                                expandTuples(ptr(args2));
                            }
                            Ref<MatchAccumulator> m = ref(new MatchAccumulator());
                            if (s_r != null)
                            {
                                functionResolve(m, s_r, e.loc, this.sc, tiargs, e.e1.value.type.value, ptr(args2), null);
                                if ((m.value.lastf != null) && m.value.lastf.errors || m.value.lastf.semantic3Errors)
                                {
                                    this.result = new ErrorExp();
                                    return ;
                                }
                            }
                            FuncDeclaration lastf = m.value.lastf;
                            if (s != null)
                            {
                                functionResolve(m, s, e.loc, this.sc, tiargs, e.e2.value.type.value, ptr(args1), null);
                                if ((m.value.lastf != null) && m.value.lastf.errors || m.value.lastf.semantic3Errors)
                                {
                                    this.result = new ErrorExp();
                                    return ;
                                }
                            }
                            if ((m.value.count > 1))
                            {
                                e.error(new BytePtr("overloads `%s` and `%s` both match argument list for `%s`"), m.value.lastf.type.toChars(), m.value.nextf.type.toChars(), m.value.lastf.toChars());
                            }
                            else if ((m.value.last <= MATCH.nomatch))
                            {
                                m.value.lastf = null;
                            }
                            if ((lastf != null) && (pequals(m.value.lastf, lastf)) || (s == null) && (m.value.last <= MATCH.nomatch))
                            {
                                this.result = build_overload(e.loc, this.sc, e.e1.value, e.e2.value, m.value.lastf != null ? m.value.lastf : s_r);
                            }
                            else
                            {
                                this.result = build_overload(e.loc, this.sc, e.e2.value, e.e1.value, m.value.lastf != null ? m.value.lastf : s);
                            }
                            if (this.pop != null)
                            {
                                this.pop.set(0, reverseRelation(e.op));
                            }
                            return ;
                        }
                    }
                    Expression tempResult = null;
                    if (!(((e.op & 0xFF) == 90) && (ad2 != null) && (pequals(ad1, ad2))))
                    {
                        this.result = checkAliasThisForLhs(ad1, this.sc, e);
                        if (this.result != null)
                        {
                            if (((e.op & 0xFF) != 90) || ((e.e1.value.op & 0xFF) == 20))
                            {
                                return ;
                            }
                            if ((ad1.fields.length == 1) || (ad1.fields.length == 2) && (ad1.vthis != null))
                            {
                                VarDeclaration var = ad1.aliasthis.isVarDeclaration();
                                if ((var != null) && (pequals(var.type, ad1.fields.get(0).type)))
                                {
                                    return ;
                                }
                                FuncDeclaration func = ad1.aliasthis.isFuncDeclaration();
                                TypeFunction tf = (TypeFunction)func.type;
                                if (tf.isref && (pequals(ad1.fields.get(0).type, tf.next.value)))
                                {
                                    return ;
                                }
                            }
                            tempResult = this.result;
                        }
                    }
                    if (!(((e.op & 0xFF) == 90) && (ad1 != null) && (pequals(ad1, ad2))))
                    {
                        this.result = checkAliasThisForRhs(ad2, this.sc, e);
                        if (this.result != null)
                        {
                            return ;
                        }
                    }
                    if (tempResult != null)
                    {
                        e.deprecation(new BytePtr("Cannot use `alias this` to partially initialize variable `%s` of type `%s`. Use `%s`"), e.e1.value.toChars(), ad1.toChars(), ((BinExp)tempResult).e1.value.toChars());
                        this.result = tempResult;
                    }
                }
                finally {
                }
            }
            finally {
            }
        }

        // Erasure: visit<EqualExp>
        public  void visit(EqualExp e) {
            Type t1 = e.e1.value.type.value.toBasetype();
            Type t2 = e.e2.value.type.value.toBasetype();
            if (((t1.ty & 0xFF) == ENUMTY.Tarray) || ((t1.ty & 0xFF) == ENUMTY.Tsarray) && ((t2.ty & 0xFF) == ENUMTY.Tarray) || ((t2.ty & 0xFF) == ENUMTY.Tsarray))
            {
                Function0<Boolean> needsDirectEq = new Function0<Boolean>() {
                    public Boolean invoke() {
                     {
                        Type t1n = t1.nextOf().toBasetype();
                        Type t2n = t2.nextOf().toBasetype();
                        if (((t1n.ty & 0xFF) == ENUMTY.Tchar) || ((t1n.ty & 0xFF) == ENUMTY.Twchar) || ((t1n.ty & 0xFF) == ENUMTY.Tdchar) && ((t2n.ty & 0xFF) == ENUMTY.Tchar) || ((t2n.ty & 0xFF) == ENUMTY.Twchar) || ((t2n.ty & 0xFF) == ENUMTY.Tdchar) || ((t1n.ty & 0xFF) == ENUMTY.Tvoid) || ((t2n.ty & 0xFF) == ENUMTY.Tvoid))
                        {
                            return false;
                        }
                        if ((!pequals(t1n.constOf(), t2n.constOf())))
                        {
                            return true;
                        }
                        Ref<Type> t = ref(t1n);
                        for (; t.value.toBasetype().nextOf() != null;) {
                            t.value = t.value.nextOf().toBasetype();
                        }
                        if (((t.value.ty & 0xFF) != ENUMTY.Tstruct))
                        {
                            return false;
                        }
                        if (global.params.useTypeInfo && (Type.dtypeinfo != null))
                        {
                            semanticTypeInfo(sc, t.value);
                        }
                        return ((TypeStruct)t.value).sym.hasIdentityEquals;
                    }}

                };
                if (needsDirectEq.invoke() && !(((t1.ty & 0xFF) == ENUMTY.Tarray) && ((t2.ty & 0xFF) == ENUMTY.Tarray)))
                {
                    Expression eeq = new IdentifierExp(e.loc, Id.__ArrayEq);
                    this.result = new CallExp(e.loc, eeq, e.e1.value, e.e2.value);
                    if (((e.op & 0xFF) == 59))
                    {
                        this.result = new NotExp(e.loc, this.result);
                    }
                    this.result = trySemantic(this.result, this.sc);
                    if (this.result == null)
                    {
                        e.error(new BytePtr("cannot compare `%s` and `%s`"), t1.toChars(), t2.toChars());
                        this.result = new ErrorExp();
                    }
                    return ;
                }
            }
            if (((t1.ty & 0xFF) == ENUMTY.Tclass) && ((e.e2.value.op & 0xFF) == 13) || ((t2.ty & 0xFF) == ENUMTY.Tclass) && ((e.e1.value.op & 0xFF) == 13))
            {
                e.error(new BytePtr("use `%s` instead of `%s` when comparing with `null`"), Token.toChars(((e.op & 0xFF) == 58) ? TOK.identity : TOK.notIdentity), Token.toChars(e.op));
                this.result = new ErrorExp();
                return ;
            }
            if (((t1.ty & 0xFF) == ENUMTY.Tclass) && ((t2.ty & 0xFF) == ENUMTY.Tnull) || ((t1.ty & 0xFF) == ENUMTY.Tnull) && ((t2.ty & 0xFF) == ENUMTY.Tclass))
            {
                return ;
            }
            if (((t1.ty & 0xFF) == ENUMTY.Tclass) && ((t2.ty & 0xFF) == ENUMTY.Tclass))
            {
                ClassDeclaration cd1 = t1.isClassHandle();
                ClassDeclaration cd2 = t2.isClassHandle();
                if (!((cd1.classKind == ClassKind.cpp) || (cd2.classKind == ClassKind.cpp)))
                {
                    Expression e1x = e.e1.value;
                    Expression e2x = e.e2.value;
                    Type to = ClassDeclaration.object.getType();
                    if (cd1.isInterfaceDeclaration() != null)
                    {
                        e1x = new CastExp(e.loc, e.e1.value, t1.isMutable() ? to : to.constOf());
                    }
                    if (cd2.isInterfaceDeclaration() != null)
                    {
                        e2x = new CastExp(e.loc, e.e2.value, t2.isMutable() ? to : to.constOf());
                    }
                    this.result = new IdentifierExp(e.loc, Id.empty);
                    this.result = new DotIdExp(e.loc, this.result, Id.object);
                    this.result = new DotIdExp(e.loc, this.result, Id.eq);
                    this.result = new CallExp(e.loc, this.result, e1x, e2x);
                    if (((e.op & 0xFF) == 59))
                    {
                        this.result = new NotExp(e.loc, this.result);
                    }
                    this.result = expressionSemantic(this.result, this.sc);
                    return ;
                }
            }
            this.result = compare_overload(e, this.sc, Id.eq, null);
            if (this.result != null)
            {
                if (((this.result.op & 0xFF) == 18) && ((e.op & 0xFF) == 59))
                {
                    this.result = new NotExp(this.result.loc, this.result);
                    this.result = expressionSemantic(this.result, this.sc);
                }
                return ;
            }
            if (((t1.ty & 0xFF) == ENUMTY.Tarray) && ((t2.ty & 0xFF) == ENUMTY.Tarray))
            {
                return ;
            }
            if (((t1.ty & 0xFF) == ENUMTY.Tpointer) || ((t2.ty & 0xFF) == ENUMTY.Tpointer))
            {
                byte op2 = ((e.op & 0xFF) == 58) ? TOK.identity : TOK.notIdentity;
                this.result = new IdentityExp(op2, e.loc, e.e1.value, e.e2.value);
                this.result = expressionSemantic(this.result, this.sc);
                return ;
            }
            if (((t1.ty & 0xFF) == ENUMTY.Tstruct) && ((t2.ty & 0xFF) == ENUMTY.Tstruct))
            {
                StructDeclaration sd = ((TypeStruct)t1).sym;
                if ((!pequals(sd, ((TypeStruct)t2).sym)))
                {
                    return ;
                }
                if (!global.params.fieldwise && !needOpEquals(sd))
                {
                    byte op2 = ((e.op & 0xFF) == 58) ? TOK.identity : TOK.notIdentity;
                    this.result = new IdentityExp(op2, e.loc, e.e1.value, e.e2.value);
                    this.result = expressionSemantic(this.result, this.sc);
                    return ;
                }
                if ((e.att1 != null) && (pequals(t1, e.att1)))
                {
                    return ;
                }
                if ((e.att2 != null) && (pequals(t2, e.att2)))
                {
                    return ;
                }
                e = (EqualExp)e.copy();
                if (e.att1 == null)
                {
                    e.att1 = t1;
                }
                if (e.att2 == null)
                {
                    e.att2 = t2;
                }
                e.e1.value = new DotIdExp(e.loc, e.e1.value, Id._tupleof);
                e.e2.value = new DotIdExp(e.loc, e.e2.value, Id._tupleof);
                Ptr<Scope> sc2 = (this.sc.get()).push();
                (sc2.get()).flags = (sc2.get()).flags & -1025 | 2;
                this.result = expressionSemantic(e, sc2);
                (sc2.get()).pop();
                if (((this.result.op & 0xFF) == (e.op & 0xFF)) && (pequals(((EqualExp)this.result).e1.value.type.value.toBasetype(), t1)))
                {
                    e.error(new BytePtr("cannot compare `%s` because its auto generated member-wise equality has recursive definition"), t1.toChars());
                    this.result = new ErrorExp();
                }
                return ;
            }
            if (((e.e1.value.op & 0xFF) == 126) && ((e.e2.value.op & 0xFF) == 126))
            {
                TupleExp tup1 = (TupleExp)e.e1.value;
                TupleExp tup2 = (TupleExp)e.e2.value;
                int dim = (tup1.exps.get()).length;
                if ((dim != (tup2.exps.get()).length))
                {
                    e.error(new BytePtr("mismatched tuple lengths, `%d` and `%d`"), dim, (tup2.exps.get()).length);
                    this.result = new ErrorExp();
                    return ;
                }
                if ((dim == 0))
                {
                    this.result = new IntegerExp(e.loc, (((e.op & 0xFF) == 58) ? 1 : 0), Type.tbool);
                }
                else
                {
                    {
                        int i = 0;
                        for (; (i < dim);i++){
                            Expression ex1 = (tup1.exps.get()).get(i);
                            Expression ex2 = (tup2.exps.get()).get(i);
                            EqualExp eeq = new EqualExp(e.op, e.loc, ex1, ex2);
                            eeq.att1 = e.att1;
                            eeq.att2 = e.att2;
                            if (this.result == null)
                            {
                                this.result = eeq;
                            }
                            else if (((e.op & 0xFF) == 58))
                            {
                                this.result = new LogicalExp(e.loc, TOK.andAnd, this.result, eeq);
                            }
                            else
                            {
                                this.result = new LogicalExp(e.loc, TOK.orOr, this.result, eeq);
                            }
                        }
                    }
                    assert(this.result != null);
                }
                this.result = Expression.combine(tup1.e0.value, tup2.e0.value, this.result);
                this.result = expressionSemantic(this.result, this.sc);
                return ;
            }
        }

        // Erasure: visit<CmpExp>
        public  void visit(CmpExp e) {
            this.result = compare_overload(e, this.sc, Id.cmp, this.pop);
        }

        // Erasure: visit<BinAssignExp>
        public  void visit(BinAssignExp e) {
            if (((e.e1.value.op & 0xFF) == 17))
            {
                ArrayExp ae = (ArrayExp)e.e1.value;
                ae.e1.value = expressionSemantic(ae.e1.value, this.sc);
                ae.e1.value = resolveProperties(this.sc, ae.e1.value);
                Expression ae1old = ae.e1.value;
                boolean maybeSlice = ((ae.arguments.get()).length == 0) || ((ae.arguments.get()).length == 1) && (((ae.arguments.get()).get(0).op & 0xFF) == 231);
                IntervalExp ie = null;
                if (maybeSlice && ((ae.arguments.get()).length != 0))
                {
                    assert((((ae.arguments.get()).get(0).op & 0xFF) == 231));
                    ie = (IntervalExp)(ae.arguments.get()).get(0);
                }
            L_outer3:
                for (; true;){
                    if (((ae.e1.value.op & 0xFF) == 127))
                    {
                        this.result = ae.e1.value;
                        return ;
                    }
                    Ref<Expression> e0 = ref(null);
                    Expression ae1save = ae.e1.value;
                    ae.lengthVar.value = null;
                    Type t1b = ae.e1.value.type.value.toBasetype();
                    AggregateDeclaration ad = isAggregate(t1b);
                    if (ad == null)
                    {
                        break;
                    }
                    try {
                        if (search_function(ad, Id.opIndexOpAssign) != null)
                        {
                            this.result = resolveOpDollar(this.sc, ae, ptr(e0));
                            if (this.result == null)
                            {
                                /*goto Lfallback*/throw Dispatch0.INSTANCE;
                            }
                            if (((this.result.op & 0xFF) == 127))
                            {
                                return ;
                            }
                            this.result = expressionSemantic(e.e2.value, this.sc);
                            if (((this.result.op & 0xFF) == 127))
                            {
                                return ;
                            }
                            e.e2.value = this.result;
                            Ptr<DArray<Expression>> a = (ae.arguments.get()).copy();
                            (a.get()).insert(0, e.e2.value);
                            Ptr<DArray<RootObject>> tiargs = opToArg(this.sc, e.op);
                            this.result = new DotTemplateInstanceExp(e.loc, ae.e1.value, Id.opIndexOpAssign, tiargs);
                            this.result = new CallExp(e.loc, this.result, a);
                            if (maybeSlice)
                            {
                                this.result = trySemantic(this.result, this.sc);
                            }
                            else
                            {
                                this.result = expressionSemantic(this.result, this.sc);
                            }
                            if (this.result != null)
                            {
                                this.result = Expression.combine(e0.value, this.result);
                                return ;
                            }
                        }
                    }
                    catch(Dispatch0 __d){}
                /*Lfallback:*/
                    if (maybeSlice && (search_function(ad, Id.opSliceOpAssign) != null))
                    {
                        this.result = resolveOpDollar(this.sc, ae, ie, ptr(e0));
                        if (((this.result.op & 0xFF) == 127))
                        {
                            return ;
                        }
                        this.result = expressionSemantic(e.e2.value, this.sc);
                        if (((this.result.op & 0xFF) == 127))
                        {
                            return ;
                        }
                        e.e2.value = this.result;
                        Ptr<DArray<Expression>> a = refPtr(new DArray<Expression>());
                        (a.get()).push(e.e2.value);
                        if (ie != null)
                        {
                            (a.get()).push(ie.lwr.value);
                            (a.get()).push(ie.upr.value);
                        }
                        Ptr<DArray<RootObject>> tiargs = opToArg(this.sc, e.op);
                        this.result = new DotTemplateInstanceExp(e.loc, ae.e1.value, Id.opSliceOpAssign, tiargs);
                        this.result = new CallExp(e.loc, this.result, a);
                        this.result = expressionSemantic(this.result, this.sc);
                        this.result = Expression.combine(e0.value, this.result);
                        return ;
                    }
                    if ((ad.aliasthis != null) && (!pequals(t1b, ae.att1)))
                    {
                        if ((ae.att1 == null) && t1b.checkAliasThisRec())
                        {
                            ae.att1 = t1b;
                        }
                        ae.e1.value = resolveAliasThis(this.sc, ae1save, true);
                        if (ae.e1.value != null)
                        {
                            continue L_outer3;
                        }
                    }
                    break;
                }
                ae.e1.value = ae1old;
                ae.lengthVar.value = null;
            }
            this.result = binSemanticProp(e, this.sc);
            if (this.result != null)
            {
                return ;
            }
            if (((e.e1.value.type.value.ty & 0xFF) == ENUMTY.Terror) || ((e.e2.value.type.value.ty & 0xFF) == ENUMTY.Terror))
            {
                this.result = new ErrorExp();
                return ;
            }
            Identifier id = opId(e);
            Ref<DArray<Expression>> args2 = ref(new DArray<Expression>());
            try {
                AggregateDeclaration ad1 = isAggregate(e.e1.value.type.value);
                Dsymbol s = null;
                if ((ad1 != null) && (id != null))
                {
                    s = search_function(ad1, id);
                }
                Ptr<DArray<RootObject>> tiargs = null;
                if (s == null)
                {
                    if (ad1 != null)
                    {
                        s = search_function(ad1, Id.opOpAssign);
                        if ((s != null) && (s.isTemplateDeclaration() == null))
                        {
                            e.error(new BytePtr("`%s.opOpAssign` isn't a template"), e.e1.value.toChars());
                            this.result = new ErrorExp();
                            return ;
                        }
                    }
                    if (s != null)
                    {
                        id = Id.opOpAssign;
                        tiargs = pcopy(opToArg(this.sc, e.op));
                    }
                }
                try {
                    if (s != null)
                    {
                        args2.value.setDim(1);
                        args2.value.set(0, e.e2.value);
                        expandTuples(ptr(args2));
                        Ref<MatchAccumulator> m = ref(new MatchAccumulator());
                        if (s != null)
                        {
                            functionResolve(m, s, e.loc, this.sc, tiargs, e.e1.value.type.value, ptr(args2), null);
                            if ((m.value.lastf != null) && m.value.lastf.errors || m.value.lastf.semantic3Errors)
                            {
                                this.result = new ErrorExp();
                                return ;
                            }
                        }
                        if ((m.value.count > 1))
                        {
                            e.error(new BytePtr("overloads `%s` and `%s` both match argument list for `%s`"), m.value.lastf.type.toChars(), m.value.nextf.type.toChars(), m.value.lastf.toChars());
                        }
                        else if ((m.value.last <= MATCH.nomatch))
                        {
                            if (tiargs != null)
                            {
                                /*goto L1*/throw Dispatch0.INSTANCE;
                            }
                            m.value.lastf = null;
                        }
                        this.result = build_overload(e.loc, this.sc, e.e1.value, e.e2.value, m.value.lastf != null ? m.value.lastf : s);
                        return ;
                    }
                }
                catch(Dispatch0 __d){}
            /*L1:*/
                this.result = checkAliasThisForLhs(ad1, this.sc, e);
                if ((this.result != null) || (s == null))
                {
                    return ;
                }
                this.result = checkAliasThisForRhs(isAggregate(e.e2.value.type.value), this.sc, e);
            }
            finally {
            }
        }


        public OpOverload() {}
    }

    // Erasure: isCommutative<byte>
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

    // Erasure: opId<Expression>
    public static Identifier opId(Expression e) {
        // skipping duplicate class OpIdVisitor
        OpIdVisitor v = new OpIdVisitor();
        e.accept(v);
        return v.id;
    }

    // Erasure: opId_r<Expression>
    public static Identifier opId_r(Expression e) {
        // skipping duplicate class OpIdRVisitor
        OpIdRVisitor v = new OpIdRVisitor();
        e.accept(v);
        return v.id;
    }

    // Erasure: opToArg<Ptr, byte>
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
        Expression e = new StringExp(Loc.initial, Token.toChars(op));
        e = expressionSemantic(e, sc);
        Ptr<DArray<RootObject>> tiargs = refPtr(new DArray<RootObject>());
        (tiargs.get()).push(e);
        return tiargs;
    }

    // Erasure: checkAliasThisForLhs<AggregateDeclaration, Ptr, BinExp>
    public static Expression checkAliasThisForLhs(AggregateDeclaration ad, Ptr<Scope> sc, BinExp e) {
        if ((ad == null) || (ad.aliasthis == null))
        {
            return null;
        }
        if ((e.att1 != null) && (pequals(e.e1.value.type.value, e.att1)))
        {
            return null;
        }
        Expression e1 = new DotIdExp(e.loc, e.e1.value, ad.aliasthis.ident);
        BinExp be = (BinExp)e.copy();
        if ((be.att1 == null) && e.e1.value.type.value.checkAliasThisRec())
        {
            be.att1 = e.e1.value.type.value;
        }
        be.e1.value = e1;
        Expression result = null;
        if (((be.op & 0xFF) == 71))
        {
            result = op_overload(be, sc, null);
        }
        else
        {
            result = trySemantic(be, sc);
        }
        return result;
    }

    // Erasure: checkAliasThisForRhs<AggregateDeclaration, Ptr, BinExp>
    public static Expression checkAliasThisForRhs(AggregateDeclaration ad, Ptr<Scope> sc, BinExp e) {
        if ((ad == null) || (ad.aliasthis == null))
        {
            return null;
        }
        if ((e.att2 != null) && (pequals(e.e2.value.type.value, e.att2)))
        {
            return null;
        }
        Expression e2 = new DotIdExp(e.loc, e.e2.value, ad.aliasthis.ident);
        BinExp be = (BinExp)e.copy();
        if ((be.att2 == null) && e.e2.value.type.value.checkAliasThisRec())
        {
            be.att2 = e.e2.value.type.value;
        }
        be.e2.value = e2;
        Expression result = null;
        if (((be.op & 0xFF) == 71))
        {
            result = op_overload(be, sc, null);
        }
        else
        {
            result = trySemantic(be, sc);
        }
        return result;
    }

    // Erasure: op_overload<Expression, Ptr, Ptr>
    public static Expression op_overload(Expression e, Ptr<Scope> sc, BytePtr pop) {
        // skipping duplicate class OpOverload
        if (pop != null)
        {
            pop.set(0, e.op);
        }
        OpOverload v = new OpOverload(sc, pop);
        e.accept(v);
        return v.result;
    }

    // defaulted all parameters starting with #3
    public static Expression op_overload(Expression e, Ptr<Scope> sc) {
        return op_overload(e, sc, (BytePtr)null);
    }

    // Erasure: compare_overload<BinExp, Ptr, Identifier, Ptr>
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
            {
                s_r = null;
            }
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
                        {
                            return new ErrorExp();
                        }
                    }
                    FuncDeclaration lastf = m.value.lastf;
                    int count = m.value.count;
                    if (s_r != null)
                    {
                        functionResolve(m, s_r, e.loc, sc, tiargs, e.e2.value.type.value, ptr(args1), null);
                        if ((m.value.lastf != null) && m.value.lastf.errors || m.value.lastf.semantic3Errors)
                        {
                            return new ErrorExp();
                        }
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
                        {
                            pop.set(0, reverseRelation(e.op));
                        }
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
        {
            return null;
        }
        Expression result = checkAliasThisForLhs(ad1, sc, e);
        return result != null ? result : checkAliasThisForRhs(isAggregate(e.e2.value.type.value), sc, e);
    }

    // Erasure: build_overload<Loc, Ptr, Expression, Expression, Dsymbol>
    public static Expression build_overload(Loc loc, Ptr<Scope> sc, Expression ethis, Expression earg, Dsymbol d) {
        assert(d != null);
        Expression e = null;
        Declaration decl = d.isDeclaration();
        if (decl != null)
        {
            e = new DotVarExp(loc, ethis, decl, false);
        }
        else
        {
            e = new DotIdExp(loc, ethis, d.ident);
        }
        e = new CallExp(loc, e, earg);
        e = expressionSemantic(e, sc);
        return e;
    }

    // Erasure: search_function<ScopeDsymbol, Identifier>
    public static Dsymbol search_function(ScopeDsymbol ad, Identifier funcid) {
        Dsymbol s = ad.search(Loc.initial, funcid, 8);
        if (s != null)
        {
            Dsymbol s2 = s.toAlias();
            FuncDeclaration fd = s2.isFuncDeclaration();
            if ((fd != null) && ((fd.type.ty & 0xFF) == ENUMTY.Tfunction))
            {
                return fd;
            }
            TemplateDeclaration td = s2.isTemplateDeclaration();
            if (td != null)
            {
                return td;
            }
        }
        return null;
    }

    // Erasure: inferForeachAggregate<Ptr, boolean, Expression, Dsymbol>
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
            {
                return false;
            }
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
                    if (ad.search(Loc.initial, isForeach ? Id.Ffront : Id.Fback, 8) != null)
                    {
                        break;
                    }
                    if (ad.aliasthis != null)
                    {
                        if ((pequals(att, tab)))
                        {
                            return false;
                        }
                        if ((att == null) && tab.checkAliasThisRec())
                        {
                            att = tab;
                        }
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

    // Erasure: inferApplyArgTypes<ForeachStatement, Ptr, Dsymbol>
    public static boolean inferApplyArgTypes(ForeachStatement fes, Ptr<Scope> sc, Ref<Dsymbol> sapply) {
        if ((fes.parameters == null) || ((fes.parameters.get()).length == 0))
        {
            return false;
        }
        if (sapply.value != null)
        {
            {
                Slice<Parameter> __r1559 = (fes.parameters.get()).opSlice().copy();
                int __key1560 = 0;
                for (; (__key1560 < __r1559.getLength());__key1560 += 1) {
                    Parameter p = __r1559.get(__key1560);
                    if (p.type != null)
                    {
                        p.type = typeSemantic(p.type, fes.loc, sc);
                        p.type = p.type.addStorageClass(p.storageClass);
                    }
                }
            }
            Expression ethis = null;
            Type tab = fes.aggr.value.type.value.toBasetype();
            if (((tab.ty & 0xFF) == ENUMTY.Tclass) || ((tab.ty & 0xFF) == ENUMTY.Tstruct))
            {
                ethis = fes.aggr.value;
            }
            else
            {
                assert(((tab.ty & 0xFF) == ENUMTY.Tdelegate) && ((fes.aggr.value.op & 0xFF) == 160));
                ethis = ((DelegateExp)fes.aggr.value).e1.value;
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
        Type taggr = fes.aggr.value.type.value;
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
                        p.type = Type.tsize_t;
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
                        {
                            p.type = p.type.addMod((byte)1);
                        }
                    }
                    p = (fes.parameters.get()).get(1);
                }
                if (p.type == null)
                {
                    p.type = taa.next.value;
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
                        Dsymbol s = ad.search(Loc.initial, id, 8);
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
                        {
                            p.type = ((Declaration)s).type;
                        }
                        else
                        {
                            break;
                        }
                    }
                    break;
                }
                break;
            case 10:
                if (!matchParamsToOpApply((TypeFunction)tab.nextOf(), fes.parameters, true))
                {
                    return false;
                }
                break;
            default:
            break;
        }
        return true;
    }

    // Erasure: findBestOpApplyMatch<Expression, FuncDeclaration, Ptr>
    public static FuncDeclaration findBestOpApplyMatch(Expression ethis, FuncDeclaration fstart, Ptr<DArray<Parameter>> parameters) {
        byte mod = ethis.type.value.mod;
        int match = MATCH.nomatch;
        FuncDeclaration fd_best = null;
        FuncDeclaration fd_ambig = null;
        Function1<Dsymbol,Integer> __lambda4 = new Function1<Dsymbol,Integer>() {
            public Integer invoke(Dsymbol s) {
             {
                FuncDeclaration f = s.isFuncDeclaration();
                if (f == null)
                {
                    return 0;
                }
                TypeFunction tf = (TypeFunction)f.type;
                int m = MATCH.exact;
                if (f.isThis() != null)
                {
                    if (!MODimplicitConv(mod, tf.mod))
                    {
                        m = MATCH.nomatch;
                    }
                    else if (((mod & 0xFF) != (tf.mod & 0xFF)))
                    {
                        m = MATCH.constant;
                    }
                }
                if (!matchParamsToOpApply(tf, parameters, false))
                {
                    m = MATCH.nomatch;
                }
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
                    {
                        fd_ambig = f;
                    }
                }
                return 0;
            }}

        };
        overloadApply(fstart, __lambda4, null);
        if (fd_ambig != null)
        {
            error(ethis.loc, new BytePtr("`%s.%s` matches more than one declaration:\n`%s`:     `%s`\nand:\n`%s`:     `%s`"), ethis.toChars(), fstart.ident.toChars(), fd_best.loc.toChars(global.params.showColumns), fd_best.type.toChars(), fd_ambig.loc.toChars(global.params.showColumns), fd_ambig.type.toChars());
            return null;
        }
        return fd_best;
    }

    // Erasure: matchParamsToOpApply<TypeFunction, Ptr, boolean>
    public static boolean matchParamsToOpApply(TypeFunction tf, Ptr<DArray<Parameter>> parameters, boolean infer) {
        boolean nomatch = false;
        if ((tf.parameterList.length() != 1))
        {
            return false;
        }
        Parameter p0 = tf.parameterList.get(0);
        if (((p0.type.ty & 0xFF) != ENUMTY.Tdelegate))
        {
            return false;
        }
        TypeFunction tdg = (TypeFunction)p0.type.nextOf();
        assert(((tdg.ty & 0xFF) == ENUMTY.Tfunction));
        int nparams = tdg.parameterList.length();
        if ((nparams == 0) || (nparams != (parameters.get()).length) || (tdg.parameterList.varargs != VarArg.none))
        {
            return false;
        }
        {
            Slice<Parameter> __r1562 = (parameters.get()).opSlice().copy();
            int __key1561 = 0;
            for (; (__key1561 < __r1562.getLength());__key1561 += 1) {
                Parameter p = __r1562.get(__key1561);
                int u = __key1561;
                Parameter param = tdg.parameterList.get(u);
                if (p.type != null)
                {
                    if (!p.type.equals(param.type))
                    {
                        return false;
                    }
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

    // Erasure: reverseRelation<byte>
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
