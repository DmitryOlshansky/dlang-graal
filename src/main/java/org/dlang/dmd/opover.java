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
        private Identifier id;
        public  void visit(Expression e) {
            throw new AssertionError("Unreachable code!");
        }
        public  void visit(UAddExp e) {
            this.id = Id.uadd;
        }
        public  void visit(NegExp e) {
            this.id = Id.neg;
        }
        public  void visit(ComExp e) {
            this.id = Id.com;
        }
        public  void visit(CastExp e) {
            this.id = Id._cast;
        }
        public  void visit(InExp e) {
            this.id = Id.opIn;
        }
        public  void visit(PostExp e) {
            this.id = ((e.op & 0xFF) == 93) ? Id.postinc : Id.postdec;
        }
        public  void visit(AddExp e) {
            this.id = Id.add;
        }
        public  void visit(MinExp e) {
            this.id = Id.sub;
        }
        public  void visit(MulExp e) {
            this.id = Id.mul;
        }
        public  void visit(DivExp e) {
            this.id = Id.div;
        }
        public  void visit(ModExp e) {
            this.id = Id.mod;
        }
        public  void visit(PowExp e) {
            this.id = Id.pow;
        }
        public  void visit(ShlExp e) {
            this.id = Id.shl;
        }
        public  void visit(ShrExp e) {
            this.id = Id.shr;
        }
        public  void visit(UshrExp e) {
            this.id = Id.ushr;
        }
        public  void visit(AndExp e) {
            this.id = Id.iand;
        }
        public  void visit(OrExp e) {
            this.id = Id.ior;
        }
        public  void visit(XorExp e) {
            this.id = Id.ixor;
        }
        public  void visit(CatExp e) {
            this.id = Id.cat;
        }
        public  void visit(AssignExp e) {
            this.id = Id.assign;
        }
        public  void visit(AddAssignExp e) {
            this.id = Id.addass;
        }
        public  void visit(MinAssignExp e) {
            this.id = Id.subass;
        }
        public  void visit(MulAssignExp e) {
            this.id = Id.mulass;
        }
        public  void visit(DivAssignExp e) {
            this.id = Id.divass;
        }
        public  void visit(ModAssignExp e) {
            this.id = Id.modass;
        }
        public  void visit(AndAssignExp e) {
            this.id = Id.andass;
        }
        public  void visit(OrAssignExp e) {
            this.id = Id.orass;
        }
        public  void visit(XorAssignExp e) {
            this.id = Id.xorass;
        }
        public  void visit(ShlAssignExp e) {
            this.id = Id.shlass;
        }
        public  void visit(ShrAssignExp e) {
            this.id = Id.shrass;
        }
        public  void visit(UshrAssignExp e) {
            this.id = Id.ushrass;
        }
        public  void visit(CatAssignExp e) {
            this.id = Id.catass;
        }
        public  void visit(PowAssignExp e) {
            this.id = Id.powass;
        }
        public  void visit(EqualExp e) {
            this.id = Id.eq;
        }
        public  void visit(CmpExp e) {
            this.id = Id.cmp;
        }
        public  void visit(ArrayExp e) {
            this.id = Id.index;
        }
        public  void visit(PtrExp e) {
            this.id = Id.opStar;
        }

        public OpIdVisitor() {}
    }
    private static class OpIdRVisitor extends Visitor
    {
        private Identifier id;
        public  void visit(Expression e) {
            this.id = null;
        }
        public  void visit(InExp e) {
            this.id = Id.opIn_r;
        }
        public  void visit(AddExp e) {
            this.id = Id.add_r;
        }
        public  void visit(MinExp e) {
            this.id = Id.sub_r;
        }
        public  void visit(MulExp e) {
            this.id = Id.mul_r;
        }
        public  void visit(DivExp e) {
            this.id = Id.div_r;
        }
        public  void visit(ModExp e) {
            this.id = Id.mod_r;
        }
        public  void visit(PowExp e) {
            this.id = Id.pow_r;
        }
        public  void visit(ShlExp e) {
            this.id = Id.shl_r;
        }
        public  void visit(ShrExp e) {
            this.id = Id.shr_r;
        }
        public  void visit(UshrExp e) {
            this.id = Id.ushr_r;
        }
        public  void visit(AndExp e) {
            this.id = Id.iand_r;
        }
        public  void visit(OrExp e) {
            this.id = Id.ior_r;
        }
        public  void visit(XorExp e) {
            this.id = Id.ixor_r;
        }
        public  void visit(CatExp e) {
            this.id = Id.cat_r;
        }

        public OpIdRVisitor() {}
    }
    private static class OpOverload extends Visitor
    {
        private Scope sc;
        private BytePtr pop;
        private Expression result;
        public  OpOverload(Scope sc, BytePtr pop) {
            this.sc = sc;
            this.pop = pcopy(pop);
        }
        public  void visit(Expression e) {
            throw new AssertionError("Unreachable code!");
        }
        public  void visit(UnaExp e) {
            if (((e.e1.op & 0xFF) == 17))
            {
                ArrayExp ae = (ArrayExp)e.e1;
                ae.e1 = expressionSemantic(ae.e1, this.sc);
                ae.e1 = resolveProperties(this.sc, ae.e1);
                Expression ae1old = ae.e1;
                boolean maybeSlice = ((ae.arguments).length == 0) || ((ae.arguments).length == 1) && (((ae.arguments).get(0).op & 0xFF) == 231);
                IntervalExp ie = null;
                if (maybeSlice && ((ae.arguments).length != 0))
                {
                    assert((((ae.arguments).get(0).op & 0xFF) == 231));
                    ie = (IntervalExp)(ae.arguments).get(0);
                }
            L_outer1:
                for (; true;){
                    if (((ae.e1.op & 0xFF) == 127))
                    {
                        this.result = ae.e1;
                        return ;
                    }
                    Ref<Expression> e0 = ref(null);
                    Expression ae1save = ae.e1;
                    ae.lengthVar = null;
                    Type t1b = ae.e1.type.toBasetype();
                    AggregateDeclaration ad = isAggregate(t1b);
                    if (ad == null)
                        break;
                    try {
                        if (search_function(ad, Id.opIndexUnary) != null)
                        {
                            this.result = resolveOpDollar(this.sc, ae, ptr(e0));
                            if (this.result == null)
                                /*goto Lfallback*/throw Dispatch0.INSTANCE;
                            if (((this.result.op & 0xFF) == 127))
                                return ;
                            DArray<Expression> a = (ae.arguments).copy();
                            DArray<RootObject> tiargs = opToArg(this.sc, e.op);
                            this.result = new DotTemplateInstanceExp(e.loc, ae.e1, Id.opIndexUnary, tiargs);
                            this.result = new CallExp(e.loc, this.result, a);
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
                    if (maybeSlice && (search_function(ad, Id.opSliceUnary) != null))
                    {
                        this.result = resolveOpDollar(this.sc, ae, ie, ptr(e0));
                        if (((this.result.op & 0xFF) == 127))
                            return ;
                        DArray<Expression> a = new DArray<Expression>();
                        if (ie != null)
                        {
                            (a).push(ie.lwr);
                            (a).push(ie.upr);
                        }
                        DArray<RootObject> tiargs = opToArg(this.sc, e.op);
                        this.result = new DotTemplateInstanceExp(e.loc, ae.e1, Id.opSliceUnary, tiargs);
                        this.result = new CallExp(e.loc, this.result, a);
                        this.result = expressionSemantic(this.result, this.sc);
                        this.result = Expression.combine(e0.value, this.result);
                        return ;
                    }
                    if ((ad.aliasthis != null) && (!pequals(t1b, ae.att1)))
                    {
                        if ((ae.att1 == null) && t1b.checkAliasThisRec())
                            ae.att1 = t1b;
                        ae.e1 = resolveAliasThis(this.sc, ae1save, true);
                        if (ae.e1 != null)
                            continue L_outer1;
                    }
                    break;
                }
                ae.e1 = ae1old;
                ae.lengthVar = null;
            }
            e.e1 = expressionSemantic(e.e1, this.sc);
            e.e1 = resolveProperties(this.sc, e.e1);
            if (((e.e1.op & 0xFF) == 127))
            {
                this.result = e.e1;
                return ;
            }
            AggregateDeclaration ad = isAggregate(e.e1.type);
            if (ad != null)
            {
                Dsymbol fd = null;
                if (((e.op & 0xFF) != 103) && ((e.op & 0xFF) != 104))
                {
                    fd = search_function(ad, opId(e));
                    if (fd != null)
                    {
                        this.result = build_overload(e.loc, this.sc, e.e1, null, fd);
                        return ;
                    }
                }
                fd = search_function(ad, Id.opUnary);
                if (fd != null)
                {
                    DArray<RootObject> tiargs = opToArg(this.sc, e.op);
                    this.result = new DotTemplateInstanceExp(e.loc, e.e1, fd.ident, tiargs);
                    this.result = new CallExp(e.loc, this.result);
                    this.result = expressionSemantic(this.result, this.sc);
                    return ;
                }
                if ((ad.aliasthis != null) && (!pequals(e.e1.type, e.att1)))
                {
                    Expression e1 = new DotIdExp(e.loc, e.e1, ad.aliasthis.ident);
                    UnaExp ue = (UnaExp)e.copy();
                    if ((ue.att1 == null) && e.e1.type.checkAliasThisRec())
                        ue.att1 = e.e1.type;
                    ue.e1 = e1;
                    this.result = trySemantic(ue, this.sc);
                    return ;
                }
            }
        }
        public  void visit(ArrayExp ae) {
            ae.e1 = expressionSemantic(ae.e1, this.sc);
            ae.e1 = resolveProperties(this.sc, ae.e1);
            Expression ae1old = ae.e1;
            boolean maybeSlice = ((ae.arguments).length == 0) || ((ae.arguments).length == 1) && (((ae.arguments).get(0).op & 0xFF) == 231);
            IntervalExp ie = null;
            if (maybeSlice && ((ae.arguments).length != 0))
            {
                assert((((ae.arguments).get(0).op & 0xFF) == 231));
                ie = (IntervalExp)(ae.arguments).get(0);
            }
        L_outer2:
            for (; true;){
                if (((ae.e1.op & 0xFF) == 127))
                {
                    this.result = ae.e1;
                    return ;
                }
                Ref<Expression> e0 = ref(null);
                Expression ae1save = ae.e1;
                ae.lengthVar = null;
                Type t1b = ae.e1.type.toBasetype();
                AggregateDeclaration ad = isAggregate(t1b);
                if (ad == null)
                {
                    if (isIndexableNonAggregate(t1b) || ((ae.e1.op & 0xFF) == 20))
                    {
                        if (maybeSlice)
                        {
                            this.result = new SliceExp(ae.loc, ae.e1, ie);
                            this.result = expressionSemantic(this.result, this.sc);
                            return ;
                        }
                        if (((ae.arguments).length == 1))
                        {
                            this.result = new IndexExp(ae.loc, ae.e1, (ae.arguments).get(0));
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
                            /*goto Lfallback*/throw Dispatch0.INSTANCE;
                        if (((this.result.op & 0xFF) == 127))
                            return ;
                        DArray<Expression> a = (ae.arguments).copy();
                        this.result = new DotIdExp(ae.loc, ae.e1, Id.index);
                        this.result = new CallExp(ae.loc, this.result, a);
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
                if (maybeSlice && ((ae.e1.op & 0xFF) == 20))
                {
                    this.result = new SliceExp(ae.loc, ae.e1, ie);
                    this.result = expressionSemantic(this.result, this.sc);
                    this.result = Expression.combine(e0.value, this.result);
                    return ;
                }
                if (maybeSlice && (search_function(ad, Id.slice) != null))
                {
                    this.result = resolveOpDollar(this.sc, ae, ie, ptr(e0));
                    if (((this.result.op & 0xFF) == 127))
                        return ;
                    DArray<Expression> a = new DArray<Expression>();
                    if (ie != null)
                    {
                        (a).push(ie.lwr);
                        (a).push(ie.upr);
                    }
                    this.result = new DotIdExp(ae.loc, ae.e1, Id.slice);
                    this.result = new CallExp(ae.loc, this.result, a);
                    this.result = expressionSemantic(this.result, this.sc);
                    this.result = Expression.combine(e0.value, this.result);
                    return ;
                }
                if ((ad.aliasthis != null) && (!pequals(t1b, ae.att1)))
                {
                    if ((ae.att1 == null) && t1b.checkAliasThisRec())
                        ae.att1 = t1b;
                    ae.e1 = resolveAliasThis(this.sc, ae1save, true);
                    if (ae.e1 != null)
                        continue L_outer2;
                }
                break;
            }
            ae.e1 = ae1old;
            ae.lengthVar = null;
        }
        public  void visit(CastExp e) {
            AggregateDeclaration ad = isAggregate(e.e1.type);
            if (ad != null)
            {
                Dsymbol fd = null;
                fd = search_function(ad, Id._cast);
                if (fd != null)
                {
                    if (fd.isFuncDeclaration() != null)
                    {
                        this.result = build_overload(e.loc, this.sc, e.e1, null, fd);
                        return ;
                    }
                    DArray<RootObject> tiargs = new DArray<RootObject>();
                    (tiargs).push(e.to);
                    this.result = new DotTemplateInstanceExp(e.loc, e.e1, fd.ident, tiargs);
                    this.result = new CallExp(e.loc, this.result);
                    this.result = expressionSemantic(this.result, this.sc);
                    return ;
                }
                if (ad.aliasthis != null)
                {
                    Expression e1 = resolveAliasThis(this.sc, e.e1, false);
                    this.result = e.copy();
                    ((UnaExp)this.result).e1 = e1;
                    this.result = op_overload(this.result, this.sc, null);
                    return ;
                }
            }
        }
        public  void visit(BinExp e) {
            Identifier id = opId(e);
            Identifier id_r = opId_r(e);
            DArray<Expression> args1 = new DArray<Expression>();
            try {
                DArray<Expression> args2 = new DArray<Expression>();
                try {
                    int argsset = 0;
                    AggregateDeclaration ad1 = isAggregate(e.e1.type);
                    AggregateDeclaration ad2 = isAggregate(e.e2.type);
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
                            s_r = null;
                    }
                    DArray<RootObject> tiargs = null;
                    if (((e.op & 0xFF) == 93) || ((e.op & 0xFF) == 94))
                    {
                        if ((ad1 != null) && (search_function(ad1, Id.opUnary) != null))
                            return ;
                    }
                    if ((s == null) && (s_r == null) && ((e.op & 0xFF) != 58) && ((e.op & 0xFF) != 59) && ((e.op & 0xFF) != 90) && ((e.op & 0xFF) != 93) && ((e.op & 0xFF) != 94))
                    {
                        if (ad1 != null)
                        {
                            s = search_function(ad1, Id.opBinary);
                            if ((s != null) && (s.isTemplateDeclaration() == null))
                            {
                                e.e1.error(new BytePtr("`%s.opBinary` isn't a template"), e.e1.toChars());
                                this.result = new ErrorExp();
                                return ;
                            }
                        }
                        if (ad2 != null)
                        {
                            s_r = search_function(ad2, Id.opBinaryRight);
                            if ((s_r != null) && (s_r.isTemplateDeclaration() == null))
                            {
                                e.e2.error(new BytePtr("`%s.opBinaryRight` isn't a template"), e.e2.toChars());
                                this.result = new ErrorExp();
                                return ;
                            }
                            if ((s_r != null) && (pequals(s_r, s)))
                                s_r = null;
                        }
                        if ((s != null) || (s_r != null))
                        {
                            id = Id.opBinary;
                            id_r = Id.opBinaryRight;
                            tiargs = opToArg(this.sc, e.op);
                        }
                    }
                    try {
                        if ((s != null) || (s_r != null))
                        {
                            args1.setDim(1);
                            args1.set(0, e.e1);
                            expandTuples(args1);
                            args2.setDim(1);
                            args2.set(0, e.e2);
                            expandTuples(args2);
                            argsset = 1;
                            MatchAccumulator m = new MatchAccumulator();
                            if (s != null)
                            {
                                functionResolve(m, s, e.loc, this.sc, tiargs, e.e1.type, args2, null);
                                if ((m.lastf != null) && m.lastf.errors || m.lastf.semantic3Errors)
                                {
                                    this.result = new ErrorExp();
                                    return ;
                                }
                            }
                            FuncDeclaration lastf = m.lastf;
                            if (s_r != null)
                            {
                                functionResolve(m, s_r, e.loc, this.sc, tiargs, e.e2.type, args1, null);
                                if ((m.lastf != null) && m.lastf.errors || m.lastf.semantic3Errors)
                                {
                                    this.result = new ErrorExp();
                                    return ;
                                }
                            }
                            if ((m.count > 1))
                            {
                                e.error(new BytePtr("overloads `%s` and `%s` both match argument list for `%s`"), m.lastf.type.toChars(), m.nextf.type.toChars(), m.lastf.toChars());
                            }
                            else if ((m.last <= MATCH.nomatch))
                            {
                                if (tiargs != null)
                                    /*goto L1*/throw Dispatch0.INSTANCE;
                                m.lastf = null;
                            }
                            if (((e.op & 0xFF) == 93) || ((e.op & 0xFF) == 94))
                            {
                                this.result = build_overload(e.loc, this.sc, e.e1, null, m.lastf != null ? m.lastf : s);
                            }
                            else if ((lastf != null) && (pequals(m.lastf, lastf)) || (s_r == null) && (m.last <= MATCH.nomatch))
                            {
                                this.result = build_overload(e.loc, this.sc, e.e1, e.e2, m.lastf != null ? m.lastf : s);
                            }
                            else
                            {
                                this.result = build_overload(e.loc, this.sc, e.e2, e.e1, m.lastf != null ? m.lastf : s_r);
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
                                s = null;
                        }
                        if ((s != null) || (s_r != null))
                        {
                            if (argsset == 0)
                            {
                                args1.setDim(1);
                                args1.set(0, e.e1);
                                expandTuples(args1);
                                args2.setDim(1);
                                args2.set(0, e.e2);
                                expandTuples(args2);
                            }
                            MatchAccumulator m = new MatchAccumulator();
                            if (s_r != null)
                            {
                                functionResolve(m, s_r, e.loc, this.sc, tiargs, e.e1.type, args2, null);
                                if ((m.lastf != null) && m.lastf.errors || m.lastf.semantic3Errors)
                                {
                                    this.result = new ErrorExp();
                                    return ;
                                }
                            }
                            FuncDeclaration lastf = m.lastf;
                            if (s != null)
                            {
                                functionResolve(m, s, e.loc, this.sc, tiargs, e.e2.type, args1, null);
                                if ((m.lastf != null) && m.lastf.errors || m.lastf.semantic3Errors)
                                {
                                    this.result = new ErrorExp();
                                    return ;
                                }
                            }
                            if ((m.count > 1))
                            {
                                e.error(new BytePtr("overloads `%s` and `%s` both match argument list for `%s`"), m.lastf.type.toChars(), m.nextf.type.toChars(), m.lastf.toChars());
                            }
                            else if ((m.last <= MATCH.nomatch))
                            {
                                m.lastf = null;
                            }
                            if ((lastf != null) && (pequals(m.lastf, lastf)) || (s == null) && (m.last <= MATCH.nomatch))
                            {
                                this.result = build_overload(e.loc, this.sc, e.e1, e.e2, m.lastf != null ? m.lastf : s_r);
                            }
                            else
                            {
                                this.result = build_overload(e.loc, this.sc, e.e2, e.e1, m.lastf != null ? m.lastf : s);
                            }
                            if (this.pop != null)
                                this.pop.set(0, reverseRelation(e.op));
                            return ;
                        }
                    }
                    Expression tempResult = null;
                    if (!(((e.op & 0xFF) == 90) && (ad2 != null) && (pequals(ad1, ad2))))
                    {
                        this.result = checkAliasThisForLhs(ad1, this.sc, e);
                        if (this.result != null)
                        {
                            if (((e.op & 0xFF) != 90) || ((e.e1.op & 0xFF) == 20))
                                return ;
                            if ((ad1.fields.length == 1) || (ad1.fields.length == 2) && (ad1.vthis != null))
                            {
                                VarDeclaration var = ad1.aliasthis.isVarDeclaration();
                                if ((var != null) && (pequals(var.type, ad1.fields.get(0).type)))
                                    return ;
                                FuncDeclaration func = ad1.aliasthis.isFuncDeclaration();
                                TypeFunction tf = (TypeFunction)func.type;
                                if (tf.isref && (pequals(ad1.fields.get(0).type, tf.next)))
                                    return ;
                            }
                            tempResult = this.result;
                        }
                    }
                    if (!(((e.op & 0xFF) == 90) && (ad1 != null) && (pequals(ad1, ad2))))
                    {
                        this.result = checkAliasThisForRhs(ad2, this.sc, e);
                        if (this.result != null)
                            return ;
                    }
                    if (tempResult != null)
                    {
                        e.deprecation(new BytePtr("Cannot use `alias this` to partially initialize variable `%s` of type `%s`. Use `%s`"), e.e1.toChars(), ad1.toChars(), ((BinExp)tempResult).e1.toChars());
                        this.result = tempResult;
                    }
                }
                finally {
                }
            }
            finally {
            }
        }
        public  void visit(EqualExp e) {
            Ref<Type> t1 = ref(e.e1.type.toBasetype());
            Ref<Type> t2 = ref(e.e2.type.toBasetype());
            if (((t1.value.ty & 0xFF) == ENUMTY.Tarray) || ((t1.value.ty & 0xFF) == ENUMTY.Tsarray) && ((t2.value.ty & 0xFF) == ENUMTY.Tarray) || ((t2.value.ty & 0xFF) == ENUMTY.Tsarray))
            {
                Function0<Boolean> needsDirectEq = new Function0<Boolean>(){
                    public Boolean invoke() {
                        Type t1n = t1.value.nextOf().toBasetype();
                        Type t2n = t2.value.nextOf().toBasetype();
                        if (((t1n.ty & 0xFF) == ENUMTY.Tchar) || ((t1n.ty & 0xFF) == ENUMTY.Twchar) || ((t1n.ty & 0xFF) == ENUMTY.Tdchar) && ((t2n.ty & 0xFF) == ENUMTY.Tchar) || ((t2n.ty & 0xFF) == ENUMTY.Twchar) || ((t2n.ty & 0xFF) == ENUMTY.Tdchar) || ((t1n.ty & 0xFF) == ENUMTY.Tvoid) || ((t2n.ty & 0xFF) == ENUMTY.Tvoid))
                        {
                            return false;
                        }
                        if ((!pequals(t1n.constOf(), t2n.constOf())))
                            return true;
                        Type t = t1n;
                        for (; t.toBasetype().nextOf() != null;) {
                            t = t.nextOf().toBasetype();
                        }
                        if (((t.ty & 0xFF) != ENUMTY.Tstruct))
                            return false;
                        if (global.params.useTypeInfo && (Type.dtypeinfo != null))
                            semanticTypeInfo(sc, t);
                        return ((TypeStruct)t).sym.hasIdentityEquals;
                    }
                };
                if (needsDirectEq.invoke() && !(((t1.value.ty & 0xFF) == ENUMTY.Tarray) && ((t2.value.ty & 0xFF) == ENUMTY.Tarray)))
                {
                    Expression eeq = new IdentifierExp(e.loc, Id.__ArrayEq);
                    this.result = new CallExp(e.loc, eeq, e.e1, e.e2);
                    if (((e.op & 0xFF) == 59))
                        this.result = new NotExp(e.loc, this.result);
                    this.result = trySemantic(this.result, this.sc);
                    if (this.result == null)
                    {
                        e.error(new BytePtr("cannot compare `%s` and `%s`"), t1.value.toChars(), t2.value.toChars());
                        this.result = new ErrorExp();
                    }
                    return ;
                }
            }
            if (((t1.value.ty & 0xFF) == ENUMTY.Tclass) && ((e.e2.op & 0xFF) == 13) || ((t2.value.ty & 0xFF) == ENUMTY.Tclass) && ((e.e1.op & 0xFF) == 13))
            {
                e.error(new BytePtr("use `%s` instead of `%s` when comparing with `null`"), Token.toChars(((e.op & 0xFF) == 58) ? TOK.identity : TOK.notIdentity), Token.toChars(e.op));
                this.result = new ErrorExp();
                return ;
            }
            if (((t1.value.ty & 0xFF) == ENUMTY.Tclass) && ((t2.value.ty & 0xFF) == ENUMTY.Tnull) || ((t1.value.ty & 0xFF) == ENUMTY.Tnull) && ((t2.value.ty & 0xFF) == ENUMTY.Tclass))
            {
                return ;
            }
            if (((t1.value.ty & 0xFF) == ENUMTY.Tclass) && ((t2.value.ty & 0xFF) == ENUMTY.Tclass))
            {
                ClassDeclaration cd1 = t1.value.isClassHandle();
                ClassDeclaration cd2 = t2.value.isClassHandle();
                if (!((cd1.classKind == ClassKind.cpp) || (cd2.classKind == ClassKind.cpp)))
                {
                    Expression e1x = e.e1;
                    Expression e2x = e.e2;
                    Type to = ClassDeclaration.object.getType();
                    if (cd1.isInterfaceDeclaration() != null)
                        e1x = new CastExp(e.loc, e.e1, t1.value.isMutable() ? to : to.constOf());
                    if (cd2.isInterfaceDeclaration() != null)
                        e2x = new CastExp(e.loc, e.e2, t2.value.isMutable() ? to : to.constOf());
                    this.result = new IdentifierExp(e.loc, Id.empty);
                    this.result = new DotIdExp(e.loc, this.result, Id.object);
                    this.result = new DotIdExp(e.loc, this.result, Id.eq);
                    this.result = new CallExp(e.loc, this.result, e1x, e2x);
                    if (((e.op & 0xFF) == 59))
                        this.result = new NotExp(e.loc, this.result);
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
            if (((t1.value.ty & 0xFF) == ENUMTY.Tarray) && ((t2.value.ty & 0xFF) == ENUMTY.Tarray))
                return ;
            if (((t1.value.ty & 0xFF) == ENUMTY.Tpointer) || ((t2.value.ty & 0xFF) == ENUMTY.Tpointer))
            {
                byte op2 = ((e.op & 0xFF) == 58) ? TOK.identity : TOK.notIdentity;
                this.result = new IdentityExp(op2, e.loc, e.e1, e.e2);
                this.result = expressionSemantic(this.result, this.sc);
                return ;
            }
            if (((t1.value.ty & 0xFF) == ENUMTY.Tstruct) && ((t2.value.ty & 0xFF) == ENUMTY.Tstruct))
            {
                StructDeclaration sd = ((TypeStruct)t1.value).sym;
                if ((!pequals(sd, ((TypeStruct)t2.value).sym)))
                    return ;
                if (!global.params.fieldwise && !needOpEquals(sd))
                {
                    byte op2 = ((e.op & 0xFF) == 58) ? TOK.identity : TOK.notIdentity;
                    this.result = new IdentityExp(op2, e.loc, e.e1, e.e2);
                    this.result = expressionSemantic(this.result, this.sc);
                    return ;
                }
                if ((e.att1 != null) && (pequals(t1.value, e.att1)))
                    return ;
                if ((e.att2 != null) && (pequals(t2.value, e.att2)))
                    return ;
                e = (EqualExp)e.copy();
                if (e.att1 == null)
                    e.att1 = t1.value;
                if (e.att2 == null)
                    e.att2 = t2.value;
                e.e1 = new DotIdExp(e.loc, e.e1, Id._tupleof);
                e.e2 = new DotIdExp(e.loc, e.e2, Id._tupleof);
                Scope sc2 = (this.sc).push();
                (sc2).flags = (sc2).flags & -1025 | 2;
                this.result = expressionSemantic(e, sc2);
                (sc2).pop();
                if (((this.result.op & 0xFF) == (e.op & 0xFF)) && (pequals(((EqualExp)this.result).e1.type.toBasetype(), t1.value)))
                {
                    e.error(new BytePtr("cannot compare `%s` because its auto generated member-wise equality has recursive definition"), t1.value.toChars());
                    this.result = new ErrorExp();
                }
                return ;
            }
            if (((e.e1.op & 0xFF) == 126) && ((e.e2.op & 0xFF) == 126))
            {
                TupleExp tup1 = (TupleExp)e.e1;
                TupleExp tup2 = (TupleExp)e.e2;
                int dim = (tup1.exps).length;
                if ((dim != (tup2.exps).length))
                {
                    e.error(new BytePtr("mismatched tuple lengths, `%d` and `%d`"), dim, (tup2.exps).length);
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
                            Expression ex1 = (tup1.exps).get(i);
                            Expression ex2 = (tup2.exps).get(i);
                            EqualExp eeq = new EqualExp(e.op, e.loc, ex1, ex2);
                            eeq.att1 = e.att1;
                            eeq.att2 = e.att2;
                            if (this.result == null)
                                this.result = eeq;
                            else if (((e.op & 0xFF) == 58))
                                this.result = new LogicalExp(e.loc, TOK.andAnd, this.result, eeq);
                            else
                                this.result = new LogicalExp(e.loc, TOK.orOr, this.result, eeq);
                        }
                    }
                    assert(this.result != null);
                }
                this.result = Expression.combine(tup1.e0, tup2.e0, this.result);
                this.result = expressionSemantic(this.result, this.sc);
                return ;
            }
        }
        public  void visit(CmpExp e) {
            this.result = compare_overload(e, this.sc, Id.cmp, this.pop);
        }
        public  void visit(BinAssignExp e) {
            if (((e.e1.op & 0xFF) == 17))
            {
                ArrayExp ae = (ArrayExp)e.e1;
                ae.e1 = expressionSemantic(ae.e1, this.sc);
                ae.e1 = resolveProperties(this.sc, ae.e1);
                Expression ae1old = ae.e1;
                boolean maybeSlice = ((ae.arguments).length == 0) || ((ae.arguments).length == 1) && (((ae.arguments).get(0).op & 0xFF) == 231);
                IntervalExp ie = null;
                if (maybeSlice && ((ae.arguments).length != 0))
                {
                    assert((((ae.arguments).get(0).op & 0xFF) == 231));
                    ie = (IntervalExp)(ae.arguments).get(0);
                }
            L_outer3:
                for (; true;){
                    if (((ae.e1.op & 0xFF) == 127))
                    {
                        this.result = ae.e1;
                        return ;
                    }
                    Ref<Expression> e0 = ref(null);
                    Expression ae1save = ae.e1;
                    ae.lengthVar = null;
                    Type t1b = ae.e1.type.toBasetype();
                    AggregateDeclaration ad = isAggregate(t1b);
                    if (ad == null)
                        break;
                    try {
                        if (search_function(ad, Id.opIndexOpAssign) != null)
                        {
                            this.result = resolveOpDollar(this.sc, ae, ptr(e0));
                            if (this.result == null)
                                /*goto Lfallback*/throw Dispatch0.INSTANCE;
                            if (((this.result.op & 0xFF) == 127))
                                return ;
                            this.result = expressionSemantic(e.e2, this.sc);
                            if (((this.result.op & 0xFF) == 127))
                                return ;
                            e.e2 = this.result;
                            DArray<Expression> a = (ae.arguments).copy();
                            (a).insert(0, e.e2);
                            DArray<RootObject> tiargs = opToArg(this.sc, e.op);
                            this.result = new DotTemplateInstanceExp(e.loc, ae.e1, Id.opIndexOpAssign, tiargs);
                            this.result = new CallExp(e.loc, this.result, a);
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
                    if (maybeSlice && (search_function(ad, Id.opSliceOpAssign) != null))
                    {
                        this.result = resolveOpDollar(this.sc, ae, ie, ptr(e0));
                        if (((this.result.op & 0xFF) == 127))
                            return ;
                        this.result = expressionSemantic(e.e2, this.sc);
                        if (((this.result.op & 0xFF) == 127))
                            return ;
                        e.e2 = this.result;
                        DArray<Expression> a = new DArray<Expression>();
                        (a).push(e.e2);
                        if (ie != null)
                        {
                            (a).push(ie.lwr);
                            (a).push(ie.upr);
                        }
                        DArray<RootObject> tiargs = opToArg(this.sc, e.op);
                        this.result = new DotTemplateInstanceExp(e.loc, ae.e1, Id.opSliceOpAssign, tiargs);
                        this.result = new CallExp(e.loc, this.result, a);
                        this.result = expressionSemantic(this.result, this.sc);
                        this.result = Expression.combine(e0.value, this.result);
                        return ;
                    }
                    if ((ad.aliasthis != null) && (!pequals(t1b, ae.att1)))
                    {
                        if ((ae.att1 == null) && t1b.checkAliasThisRec())
                            ae.att1 = t1b;
                        ae.e1 = resolveAliasThis(this.sc, ae1save, true);
                        if (ae.e1 != null)
                            continue L_outer3;
                    }
                    break;
                }
                ae.e1 = ae1old;
                ae.lengthVar = null;
            }
            this.result = binSemanticProp(e, this.sc);
            if (this.result != null)
                return ;
            if (((e.e1.type.ty & 0xFF) == ENUMTY.Terror) || ((e.e2.type.ty & 0xFF) == ENUMTY.Terror))
            {
                this.result = new ErrorExp();
                return ;
            }
            Identifier id = opId(e);
            DArray<Expression> args2 = new DArray<Expression>();
            try {
                AggregateDeclaration ad1 = isAggregate(e.e1.type);
                Dsymbol s = null;
                if ((ad1 != null) && (id != null))
                {
                    s = search_function(ad1, id);
                }
                DArray<RootObject> tiargs = null;
                if (s == null)
                {
                    if (ad1 != null)
                    {
                        s = search_function(ad1, Id.opOpAssign);
                        if ((s != null) && (s.isTemplateDeclaration() == null))
                        {
                            e.error(new BytePtr("`%s.opOpAssign` isn't a template"), e.e1.toChars());
                            this.result = new ErrorExp();
                            return ;
                        }
                    }
                    if (s != null)
                    {
                        id = Id.opOpAssign;
                        tiargs = opToArg(this.sc, e.op);
                    }
                }
                try {
                    if (s != null)
                    {
                        args2.setDim(1);
                        args2.set(0, e.e2);
                        expandTuples(args2);
                        MatchAccumulator m = new MatchAccumulator();
                        if (s != null)
                        {
                            functionResolve(m, s, e.loc, this.sc, tiargs, e.e1.type, args2, null);
                            if ((m.lastf != null) && m.lastf.errors || m.lastf.semantic3Errors)
                            {
                                this.result = new ErrorExp();
                                return ;
                            }
                        }
                        if ((m.count > 1))
                        {
                            e.error(new BytePtr("overloads `%s` and `%s` both match argument list for `%s`"), m.lastf.type.toChars(), m.nextf.type.toChars(), m.lastf.toChars());
                        }
                        else if ((m.last <= MATCH.nomatch))
                        {
                            if (tiargs != null)
                                /*goto L1*/throw Dispatch0.INSTANCE;
                            m.lastf = null;
                        }
                        this.result = build_overload(e.loc, this.sc, e.e1, e.e2, m.lastf != null ? m.lastf : s);
                        return ;
                    }
                }
                catch(Dispatch0 __d){}
            /*L1:*/
                this.result = checkAliasThisForLhs(ad1, this.sc, e);
                if ((this.result != null) || (s == null))
                    return ;
                this.result = checkAliasThisForRhs(isAggregate(e.e2.type), this.sc, e);
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
    public static DArray<RootObject> opToArg(Scope sc, byte op) {
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
        DArray<RootObject> tiargs = new DArray<RootObject>();
        (tiargs).push(e);
        return tiargs;
    }
    public static Expression checkAliasThisForLhs(AggregateDeclaration ad, Scope sc, BinExp e) {
        if ((ad == null) || (ad.aliasthis == null))
            return null;
        if ((e.att1 != null) && (pequals(e.e1.type, e.att1)))
            return null;
        Expression e1 = new DotIdExp(e.loc, e.e1, ad.aliasthis.ident);
        BinExp be = (BinExp)e.copy();
        if ((be.att1 == null) && e.e1.type.checkAliasThisRec())
            be.att1 = e.e1.type;
        be.e1 = e1;
        Expression result = null;
        if (((be.op & 0xFF) == 71))
            result = op_overload(be, sc, null);
        else
            result = trySemantic(be, sc);
        return result;
    }
    public static Expression checkAliasThisForRhs(AggregateDeclaration ad, Scope sc, BinExp e) {
        if ((ad == null) || (ad.aliasthis == null))
            return null;
        if ((e.att2 != null) && (pequals(e.e2.type, e.att2)))
            return null;
        Expression e2 = new DotIdExp(e.loc, e.e2, ad.aliasthis.ident);
        BinExp be = (BinExp)e.copy();
        if ((be.att2 == null) && e.e2.type.checkAliasThisRec())
            be.att2 = e.e2.type;
        be.e2 = e2;
        Expression result = null;
        if (((be.op & 0xFF) == 71))
            result = op_overload(be, sc, null);
        else
            result = trySemantic(be, sc);
        return result;
    }
    public static Expression op_overload(Expression e, Scope sc, BytePtr pop) {
        if (pop != null)
            pop.set(0, e.op);
        OpOverload v = new OpOverload(sc, pop);
        e.accept(v);
        return v.result;
    }
    public static Expression compare_overload(BinExp e, Scope sc, Identifier id, BytePtr pop) {
        AggregateDeclaration ad1 = isAggregate(e.e1.type);
        AggregateDeclaration ad2 = isAggregate(e.e2.type);
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
        DArray<RootObject> tiargs = null;
        if ((s != null) || (s_r != null))
        {
            DArray<Expression> args1 = args1 = new DArray<Expression>(1);
            try {
                args1.set(0, e.e1);
                expandTuples(args1);
                DArray<Expression> args2 = args2 = new DArray<Expression>(1);
                try {
                    args2.set(0, e.e2);
                    expandTuples(args2);
                    MatchAccumulator m = new MatchAccumulator();
                    if (false)
                    {
                        printf(new BytePtr("s  : %s\n"), s.toPrettyChars(false));
                        printf(new BytePtr("s_r: %s\n"), s_r.toPrettyChars(false));
                    }
                    if (s != null)
                    {
                        functionResolve(m, s, e.loc, sc, tiargs, e.e1.type, args2, null);
                        if ((m.lastf != null) && m.lastf.errors || m.lastf.semantic3Errors)
                            return new ErrorExp();
                    }
                    FuncDeclaration lastf = m.lastf;
                    int count = m.count;
                    if (s_r != null)
                    {
                        functionResolve(m, s_r, e.loc, sc, tiargs, e.e2.type, args1, null);
                        if ((m.lastf != null) && m.lastf.errors || m.lastf.semantic3Errors)
                            return new ErrorExp();
                    }
                    if ((m.count > 1))
                    {
                        if (!((pequals(m.lastf, lastf)) && (m.count == 2) && (count == 1)))
                        {
                            e.error(new BytePtr("overloads `%s` and `%s` both match argument list for `%s`"), m.lastf.type.toChars(), m.nextf.type.toChars(), m.lastf.toChars());
                        }
                    }
                    else if ((m.last <= MATCH.nomatch))
                    {
                        m.lastf = null;
                    }
                    Expression result = null;
                    if ((lastf != null) && (pequals(m.lastf, lastf)) || (s_r == null) && (m.last <= MATCH.nomatch))
                    {
                        result = build_overload(e.loc, sc, e.e1, e.e2, m.lastf != null ? m.lastf : s);
                    }
                    else
                    {
                        result = build_overload(e.loc, sc, e.e2, e.e1, m.lastf != null ? m.lastf : s_r);
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
        return result != null ? result : checkAliasThisForRhs(isAggregate(e.e2.type), sc, e);
    }
    public static Expression build_overload(Loc loc, Scope sc, Expression ethis, Expression earg, Dsymbol d) {
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
        Dsymbol s = ad.search(Loc.initial, funcid, 8);
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
    public static boolean inferForeachAggregate(Scope sc, boolean isForeach, Ref<Expression> feaggr, Ref<Dsymbol> sapply) {
        sapply.value = null;
        boolean sliced = false;
        Type att = null;
        Expression aggr = feaggr.value;
        for (; 1 != 0;){
            aggr = expressionSemantic(aggr, sc);
            aggr = resolveProperties(sc, aggr);
            aggr = aggr.optimize(0, false);
            if ((aggr.type == null) || ((aggr.op & 0xFF) == 127))
                return false;
            Type tab = aggr.type.toBasetype();
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
    public static boolean inferApplyArgTypes(ForeachStatement fes, Scope sc, Ref<Dsymbol> sapply) {
        if ((fes.parameters == null) || ((fes.parameters).length == 0))
            return false;
        if (sapply.value != null)
        {
            {
                Slice<Parameter> __r1606 = (fes.parameters).opSlice().copy();
                int __key1607 = 0;
                for (; (__key1607 < __r1606.getLength());__key1607 += 1) {
                    Parameter p = __r1606.get(__key1607);
                    if (p.type != null)
                    {
                        p.type = typeSemantic(p.type, fes.loc, sc);
                        p.type = p.type.addStorageClass(p.storageClass);
                    }
                }
            }
            Expression ethis = null;
            Type tab = fes.aggr.type.toBasetype();
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
        Parameter p = (fes.parameters).get(0);
        Type taggr = fes.aggr.type;
        assert(taggr != null);
        Type tab = taggr.toBasetype();
        switch ((tab.ty & 0xFF))
        {
            case 0:
            case 1:
            case 37:
                if (((fes.parameters).length == 2))
                {
                    if (p.type == null)
                    {
                        p.type = Type.tsize_t;
                        p.type = p.type.addStorageClass(p.storageClass);
                    }
                    p = (fes.parameters).get(1);
                }
                if ((p.type == null) && ((tab.ty & 0xFF) != ENUMTY.Ttuple))
                {
                    p.type = tab.nextOf();
                    p.type = p.type.addStorageClass(p.storageClass);
                }
                break;
            case 2:
                TypeAArray taa = (TypeAArray)tab;
                if (((fes.parameters).length == 2))
                {
                    if (p.type == null)
                    {
                        p.type = taa.index;
                        p.type = p.type.addStorageClass(p.storageClass);
                        if ((p.storageClass & 2097152L) != 0)
                            p.type = p.type.addMod((byte)1);
                    }
                    p = (fes.parameters).get(1);
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
                if (((fes.parameters).length == 1))
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
    public static FuncDeclaration findBestOpApplyMatch(Expression ethis, FuncDeclaration fstart, DArray<Parameter> parameters) {
        byte mod = ethis.type.mod;
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
            error(ethis.loc, new BytePtr("`%s.%s` matches more than one declaration:\n`%s`:     `%s`\nand:\n`%s`:     `%s`"), ethis.toChars(), fstart.ident.toChars(), fd_best.loc.toChars(global.params.showColumns), fd_best.type.toChars(), fd_ambig.loc.toChars(global.params.showColumns), fd_ambig.type.toChars());
            return null;
        }
        return fd_best;
    }
    public static boolean matchParamsToOpApply(TypeFunction tf, DArray<Parameter> parameters, boolean infer) {
        boolean nomatch = false;
        if ((tf.parameterList.length() != 1))
            return false;
        Parameter p0 = tf.parameterList.get(0);
        if (((p0.type.ty & 0xFF) != ENUMTY.Tdelegate))
            return false;
        TypeFunction tdg = (TypeFunction)p0.type.nextOf();
        assert(((tdg.ty & 0xFF) == ENUMTY.Tfunction));
        int nparams = tdg.parameterList.length();
        if ((nparams == 0) || (nparams != (parameters).length) || (tdg.parameterList.varargs != VarArg.none))
            return false;
        {
            Slice<Parameter> __r1609 = (parameters).opSlice().copy();
            int __key1608 = 0;
            for (; (__key1608 < __r1609.getLength());__key1608 += 1) {
                Parameter p = __r1609.get(__key1608);
                int u = __key1608;
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
