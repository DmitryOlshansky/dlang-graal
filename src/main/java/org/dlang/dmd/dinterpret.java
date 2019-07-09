package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.apply.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.builtin.*;
import static org.dlang.dmd.constfold.*;
import static org.dlang.dmd.ctfeexpr.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.initsem.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.utf.*;
import static org.dlang.dmd.visitor.*;

public class dinterpret {
    private static class VarWalker extends StoppableVisitor
    {
        private CompiledCtfeFunction ccf;
        public  VarWalker(CompiledCtfeFunction ccf) {
            super();
            this.ccf = ccf;
        }

        public  void visit(Expression e) {
        }

        public  void visit(ErrorExp e) {
            if ((global.gag != 0) && ((this.ccf).func != null))
            {
                this.stop = true;
                return ;
            }
            error(e.loc, new BytePtr("CTFE internal error: ErrorExp in `%s`\n"), (this.ccf).func != null ? (this.ccf).func.loc.toChars(global.params.showColumns) : (this.ccf).callingloc.toChars(global.params.showColumns));
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(DeclarationExp e) {
            VarDeclaration v = e.declaration.isVarDeclaration();
            if (v == null)
                return ;
            TupleDeclaration td = v.toAlias().isTupleDeclaration();
            if (td != null)
            {
                if (td.objects == null)
                    return ;
                {
                    Slice<RootObject> __r933 = (td.objects).opSlice().copy();
                    int __key934 = 0;
                    for (; (__key934 < __r933.getLength());__key934 += 1) {
                        RootObject o = __r933.get(__key934);
                        Expression ex = isExpression(o);
                        DsymbolExp s = ex != null ? ex.isDsymbolExp() : null;
                        assert(s != null);
                        VarDeclaration v2 = s.s.isVarDeclaration();
                        assert(v2 != null);
                        if (!v2.isDataseg() || v2.isCTFE())
                            (this.ccf).onDeclaration(v2);
                    }
                }
            }
            else if (!(v.isDataseg() || ((v.storage_class & 8388608L) != 0)) || v.isCTFE())
                (this.ccf).onDeclaration(v);
            Dsymbol s = v.toAlias();
            if ((pequals(s, v)) && !v.isStatic() && (v._init != null))
            {
                ExpInitializer ie = v._init.isExpInitializer();
                if (ie != null)
                    (this.ccf).onExpression(ie.exp);
            }
        }

        public  void visit(IndexExp e) {
            if (e.lengthVar != null)
                (this.ccf).onDeclaration(e.lengthVar);
        }

        public  void visit(SliceExp e) {
            if (e.lengthVar != null)
                (this.ccf).onDeclaration(e.lengthVar);
        }


        public VarWalker() {}
    }
    private static class RecursiveBlock
    {
        private InterState istate;
        private Expression newval;
        private boolean refCopy = false;
        private boolean needsPostblit = false;
        private boolean needsDtor = false;
        public  Expression assignTo(ArrayLiteralExp ae) {
            return this.assignTo(ae, 0, (ae.elements).length);
        }

        public  Expression assignTo(ArrayLiteralExp ae, int lwr, int upr) {
            DArray<Expression> w = ae.elements;
            assert(((ae.type.ty & 0xFF) == ENUMTY.Tsarray) || ((ae.type.ty & 0xFF) == ENUMTY.Tarray));
            boolean directblk = ((TypeArray)ae.type).next.equivalent(this.newval.type);
            {
                int k = lwr;
                for (; (k < upr);k++){
                    if (!directblk && (((w).get(k).op & 0xFF) == 47))
                    {
                        {
                            Expression ex = this.assignTo((ArrayLiteralExp)(w).get(k));
                            if ((ex) != null)
                                return ex;
                        }
                    }
                    else if (this.refCopy)
                    {
                        w.set(k, this.newval);
                    }
                    else if (!this.needsPostblit && !this.needsDtor)
                    {
                        assignInPlace((w).get(k), this.newval);
                    }
                    else
                    {
                        Expression oldelem = (w).get(k);
                        Expression tmpelem = this.needsDtor ? copyLiteral(oldelem).copy() : null;
                        assignInPlace(oldelem, this.newval);
                        if (this.needsPostblit)
                        {
                            {
                                Expression ex = evaluatePostblit(this.istate, oldelem);
                                if ((ex) != null)
                                    return ex;
                            }
                        }
                        if (this.needsDtor)
                        {
                            {
                                Expression ex = evaluateDtor(this.istate, tmpelem);
                                if ((ex) != null)
                                    return ex;
                            }
                        }
                    }
                }
            }
            return null;
        }

        private Object this;
        public RecursiveBlock(){
        }
        public RecursiveBlock copy(){
            RecursiveBlock r = new RecursiveBlock();
            r.istate = istate;
            r.newval = newval;
            r.refCopy = refCopy;
            r.needsPostblit = needsPostblit;
            r.needsDtor = needsDtor;
            r.this = this;
            return r;
        }
        public RecursiveBlock(InterState istate, Expression newval, boolean refCopy, boolean needsPostblit, boolean needsDtor, Object this) {
            this.istate = istate;
            this.newval = newval;
            this.refCopy = refCopy;
            this.needsPostblit = needsPostblit;
            this.needsDtor = needsDtor;
            this.this = this;
        }

        public RecursiveBlock opAssign(RecursiveBlock that) {
            this.istate = that.istate;
            this.newval = that.newval;
            this.refCopy = that.refCopy;
            this.needsPostblit = that.needsPostblit;
            this.needsDtor = that.needsDtor;
            this.this = that.this;
            return this;
        }
    }

    public static Expression ctfeInterpret(Expression e) {
        {
            int __dispatch0 = 0;
            dispatched_0:
            do {
                switch (__dispatch0 != 0 ? __dispatch0 : (e.op & 0xFF))
                {
                    case 135:
                    case 140:
                    case 147:
                    case 13:
                    case 121:
                        if (((e.type.ty & 0xFF) == ENUMTY.Terror))
                            return new ErrorExp();
                        /*goto case*/{ __dispatch0 = 127; continue dispatched_0; }
                    case 127:
                        __dispatch0 = 0;
                        return e;
                    default:
                    break;
                }
            } while(__dispatch0 != 0);
        }
        assert(e.type != null);
        if (((e.type.ty & 0xFF) == ENUMTY.Terror))
            return new ErrorExp();
        CompiledCtfeFunction ctfeCodeGlobal = ctfeCodeGlobal = new CompiledCtfeFunction(null);
        ctfeCodeGlobal.callingloc = e.loc.copy();
        ctfeCodeGlobal.onExpression(e);
        Expression result = interpret(e, null, CtfeGoal.ctfeNeedRvalue);
        if (!CTFEExp.isCantExp(result))
            result = scrubReturnValue(e.loc, result);
        if (CTFEExp.isCantExp(result))
            result = new ErrorExp();
        return result;
    }

    public static Expression ctfeInterpretForPragmaMsg(Expression e) {
        if (((e.op & 0xFF) == 127) || ((e.op & 0xFF) == 20))
            return e;
        {
            VarExp ve = e.isVarExp();
            if ((ve) != null)
                if (ve.var.isFuncDeclaration() != null)
                {
                    return e;
                }
        }
        TupleExp tup = e.isTupleExp();
        if (tup == null)
            return e.ctfeInterpret();
        DArray<Expression> expsx = null;
        {
            Slice<Expression> __r932 = (tup.exps).opSlice().copy();
            int __key931 = 0;
            for (; (__key931 < __r932.getLength());__key931 += 1) {
                Expression g = __r932.get(__key931);
                int i = __key931;
                Expression h = ctfeInterpretForPragmaMsg(g);
                if ((!pequals(h, g)))
                {
                    if (expsx == null)
                    {
                        expsx = (tup.exps).copy();
                    }
                    expsx.set(i, h);
                }
            }
        }
        if (expsx != null)
        {
            TupleExp te = new TupleExp(e.loc, expsx);
            expandTuples(te.exps);
            te.type = new TypeTuple(te.exps);
            return te;
        }
        return e;
    }

    public static Expression getValue(VarDeclaration vd) {
        return ctfeStack.getValue(vd);
    }

    public static void printCtfePerformanceStats() {
    }

    public static class CompiledCtfeFunctionPimpl
    {
        public CompiledCtfeFunction pimpl;
        public CompiledCtfeFunctionPimpl(){
        }
        public CompiledCtfeFunctionPimpl copy(){
            CompiledCtfeFunctionPimpl r = new CompiledCtfeFunctionPimpl();
            r.pimpl = pimpl;
            return r;
        }
        public CompiledCtfeFunctionPimpl(CompiledCtfeFunction pimpl) {
            this.pimpl = pimpl;
        }

        public CompiledCtfeFunctionPimpl opAssign(CompiledCtfeFunctionPimpl that) {
            this.pimpl = that.pimpl;
            return this;
        }
    }

    public static class CtfeGoal 
    {
        public static final int ctfeNeedRvalue = 0;
        public static final int ctfeNeedLvalue = 1;
        public static final int ctfeNeedNothing = 2;
    }

    static int CTFE_RECURSION_LIMIT = 1000;
    public static class CtfeStack
    {
        public DArray<Expression> values = new DArray<Expression>();
        public DArray<VarDeclaration> vars = new DArray<VarDeclaration>();
        public DArray<Object> savedId = new DArray<Object>();
        public DArray<Object> frames = new DArray<Object>();
        public DArray<Expression> savedThis = new DArray<Expression>();
        public DArray<Expression> globalValues = new DArray<Expression>();
        public int framepointer = 0;
        public int maxStackPointer = 0;
        public Expression localThis;
        public  int stackPointer() {
            return this.values.length;
        }

        public  Expression getThis() {
            return this.localThis;
        }

        public  int maxStackUsage() {
            return this.maxStackPointer;
        }

        public  void startFrame(Expression thisexp) {
            this.frames.push(this.framepointer);
            this.savedThis.push(this.localThis);
            this.framepointer = this.stackPointer();
            this.localThis = thisexp;
        }

        public  void endFrame() {
            int oldframe = ((int)this.frames.get(this.frames.length - 1));
            this.localThis = this.savedThis.get(this.savedThis.length - 1);
            this.popAll(this.framepointer);
            this.framepointer = oldframe;
            this.frames.setDim(this.frames.length - 1);
            this.savedThis.setDim(this.savedThis.length - 1);
        }

        public  boolean isInCurrentFrame(VarDeclaration v) {
            if (v.isDataseg() && !v.isCTFE())
                return false;
            return v.ctfeAdrOnStack >= this.framepointer;
        }

        public  Expression getValue(VarDeclaration v) {
            if (v.isDataseg() || ((v.storage_class & 8388608L) != 0) && !v.isCTFE())
            {
                assert((v.ctfeAdrOnStack >= 0) && (v.ctfeAdrOnStack < this.globalValues.length));
                return this.globalValues.get(v.ctfeAdrOnStack);
            }
            assert((v.ctfeAdrOnStack >= 0) && (v.ctfeAdrOnStack < this.stackPointer()));
            return this.values.get(v.ctfeAdrOnStack);
        }

        public  void setValue(VarDeclaration v, Expression e) {
            assert(!v.isDataseg() || v.isCTFE());
            assert((v.ctfeAdrOnStack >= 0) && (v.ctfeAdrOnStack < this.stackPointer()));
            this.values.set(v.ctfeAdrOnStack, e);
        }

        public  void push(VarDeclaration v) {
            assert(!v.isDataseg() || v.isCTFE());
            if ((v.ctfeAdrOnStack != -1) && (v.ctfeAdrOnStack >= this.framepointer))
            {
                this.values.set(v.ctfeAdrOnStack, null);
                return ;
            }
            this.savedId.push(v.ctfeAdrOnStack);
            v.ctfeAdrOnStack = this.values.length;
            this.vars.push(v);
            this.values.push(null);
        }

        public  void pop(VarDeclaration v) {
            assert(!v.isDataseg() || v.isCTFE());
            assert((v.storage_class & 2101248L) == 0);
            int oldid = v.ctfeAdrOnStack;
            v.ctfeAdrOnStack = ((int)this.savedId.get(oldid));
            if ((v.ctfeAdrOnStack == this.values.length - 1))
            {
                this.values.pop();
                this.vars.pop();
                this.savedId.pop();
            }
        }

        public  void popAll(int stackpointer) {
            if ((this.stackPointer() > this.maxStackPointer))
                this.maxStackPointer = this.stackPointer();
            assert((this.values.length >= stackpointer));
            {
                int i = stackpointer;
                for (; (i < this.values.length);i += 1){
                    VarDeclaration v = this.vars.get(i);
                    v.ctfeAdrOnStack = ((int)this.savedId.get(i));
                }
            }
            this.values.setDim(stackpointer);
            this.vars.setDim(stackpointer);
            this.savedId.setDim(stackpointer);
        }

        public  void saveGlobalConstant(VarDeclaration v, Expression e) {
            assert((v._init != null) && v.isConst() || v.isImmutable() || ((v.storage_class & 8388608L) != 0) && !v.isCTFE());
            v.ctfeAdrOnStack = this.globalValues.length;
            this.globalValues.push(e);
        }

        public CtfeStack(){
            values = new DArray<Expression>();
            vars = new DArray<VarDeclaration>();
            savedId = new DArray<Object>();
            frames = new DArray<Object>();
            savedThis = new DArray<Expression>();
            globalValues = new DArray<Expression>();
        }
        public CtfeStack copy(){
            CtfeStack r = new CtfeStack();
            r.values = values.copy();
            r.vars = vars.copy();
            r.savedId = savedId.copy();
            r.frames = frames.copy();
            r.savedThis = savedThis.copy();
            r.globalValues = globalValues.copy();
            r.framepointer = framepointer;
            r.maxStackPointer = maxStackPointer;
            r.localThis = localThis;
            return r;
        }
        public CtfeStack(DArray<Expression> values, DArray<VarDeclaration> vars, DArray<Object> savedId, DArray<Object> frames, DArray<Expression> savedThis, DArray<Expression> globalValues, int framepointer, int maxStackPointer, Expression localThis) {
            this.values = values;
            this.vars = vars;
            this.savedId = savedId;
            this.frames = frames;
            this.savedThis = savedThis;
            this.globalValues = globalValues;
            this.framepointer = framepointer;
            this.maxStackPointer = maxStackPointer;
            this.localThis = localThis;
        }

        public CtfeStack opAssign(CtfeStack that) {
            this.values = that.values;
            this.vars = that.vars;
            this.savedId = that.savedId;
            this.frames = that.frames;
            this.savedThis = that.savedThis;
            this.globalValues = that.globalValues;
            this.framepointer = that.framepointer;
            this.maxStackPointer = that.maxStackPointer;
            this.localThis = that.localThis;
            return this;
        }
    }
    public static class InterState
    {
        public InterState caller;
        public FuncDeclaration fd;
        public Statement start;
        public Statement gotoTarget;
        public InterState(){
        }
        public InterState copy(){
            InterState r = new InterState();
            r.caller = caller;
            r.fd = fd;
            r.start = start;
            r.gotoTarget = gotoTarget;
            return r;
        }
        public InterState(InterState caller, FuncDeclaration fd, Statement start, Statement gotoTarget) {
            this.caller = caller;
            this.fd = fd;
            this.start = start;
            this.gotoTarget = gotoTarget;
        }

        public InterState opAssign(InterState that) {
            this.caller = that.caller;
            this.fd = that.fd;
            this.start = that.start;
            this.gotoTarget = that.gotoTarget;
            return this;
        }
    }
    static CtfeStack ctfeStack = new CtfeStack();
    public static class CompiledCtfeFunction
    {
        public FuncDeclaration func;
        public int numVars = 0;
        public Loc callingloc = new Loc();
        public  CompiledCtfeFunction(FuncDeclaration f) {
            this.func = f;
        }

        public  void onDeclaration(VarDeclaration v) {
            this.numVars += 1;
        }

        public  void onExpression(Expression e) {
            VarWalker v = new VarWalker(this);
            walkPostorder(e, v);
        }

        public CompiledCtfeFunction(){
            callingloc = new Loc();
        }
        public CompiledCtfeFunction copy(){
            CompiledCtfeFunction r = new CompiledCtfeFunction();
            r.func = func;
            r.numVars = numVars;
            r.callingloc = callingloc.copy();
            return r;
        }
        public CompiledCtfeFunction opAssign(CompiledCtfeFunction that) {
            this.func = that.func;
            this.numVars = that.numVars;
            this.callingloc = that.callingloc;
            return this;
        }
    }
    public static class CtfeCompiler extends SemanticTimeTransitiveVisitor
    {
        public CompiledCtfeFunction ccf;
        public  CtfeCompiler(CompiledCtfeFunction ccf) {
            this.ccf = ccf;
        }

        public  void visit(Statement s) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ExpStatement s) {
            if (s.exp != null)
                (this.ccf).onExpression(s.exp);
        }

        public  void visit(IfStatement s) {
            (this.ccf).onExpression(s.condition);
            if (s.ifbody != null)
                this.ctfeCompile(s.ifbody);
            if (s.elsebody != null)
                this.ctfeCompile(s.elsebody);
        }

        public  void visit(ScopeGuardStatement s) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(DoStatement s) {
            (this.ccf).onExpression(s.condition);
            if (s._body != null)
                this.ctfeCompile(s._body);
        }

        public  void visit(WhileStatement s) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ForStatement s) {
            if (s._init != null)
                this.ctfeCompile(s._init);
            if (s.condition != null)
                (this.ccf).onExpression(s.condition);
            if (s.increment != null)
                (this.ccf).onExpression(s.increment);
            if (s._body != null)
                this.ctfeCompile(s._body);
        }

        public  void visit(ForeachStatement s) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(SwitchStatement s) {
            (this.ccf).onExpression(s.condition);
            {
                Slice<CaseStatement> __r935 = (s.cases).opSlice().copy();
                int __key936 = 0;
                for (; (__key936 < __r935.getLength());__key936 += 1) {
                    CaseStatement cs = __r935.get(__key936);
                    (this.ccf).onExpression(cs.exp);
                }
            }
            if (s._body != null)
                this.ctfeCompile(s._body);
        }

        public  void visit(CaseStatement s) {
            if (s.statement != null)
                this.ctfeCompile(s.statement);
        }

        public  void visit(GotoDefaultStatement s) {
        }

        public  void visit(GotoCaseStatement s) {
        }

        public  void visit(SwitchErrorStatement s) {
        }

        public  void visit(ReturnStatement s) {
            if (s.exp != null)
                (this.ccf).onExpression(s.exp);
        }

        public  void visit(BreakStatement s) {
        }

        public  void visit(ContinueStatement s) {
        }

        public  void visit(WithStatement s) {
            if (((s.exp.op & 0xFF) == 203) || ((s.exp.op & 0xFF) == 20))
            {
            }
            else
            {
                (this.ccf).onDeclaration(s.wthis);
                (this.ccf).onExpression(s.exp);
            }
            if (s._body != null)
                this.ctfeCompile(s._body);
        }

        public  void visit(TryCatchStatement s) {
            if (s._body != null)
                this.ctfeCompile(s._body);
            {
                Slice<Catch> __r937 = (s.catches).opSlice().copy();
                int __key938 = 0;
                for (; (__key938 < __r937.getLength());__key938 += 1) {
                    Catch ca = __r937.get(__key938);
                    if (ca.var != null)
                        (this.ccf).onDeclaration(ca.var);
                    if (ca.handler != null)
                        this.ctfeCompile(ca.handler);
                }
            }
        }

        public  void visit(ThrowStatement s) {
            (this.ccf).onExpression(s.exp);
        }

        public  void visit(GotoStatement s) {
        }

        public  void visit(ImportStatement s) {
        }

        public  void visit(ForeachRangeStatement s) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(AsmStatement s) {
        }

        public  void ctfeCompile(Statement s) {
            s.accept(this);
        }


        public CtfeCompiler() {}

        public CtfeCompiler copy() {
            CtfeCompiler that = new CtfeCompiler();
            that.ccf = this.ccf;
            return that;
        }
    }
    public static void ctfeCompile(FuncDeclaration fd) {
        assert(fd.ctfeCode.pimpl == null);
        assert(!fd.semantic3Errors);
        assert((fd.semanticRun == PASS.semantic3done));
        fd.ctfeCode.pimpl = new CompiledCtfeFunction(fd);
        if (fd.parameters != null)
        {
            Type tb = fd.type.toBasetype().isTypeFunction();
            assert(tb != null);
            {
                Slice<VarDeclaration> __r939 = (fd.parameters).opSlice().copy();
                int __key940 = 0;
                for (; (__key940 < __r939.getLength());__key940 += 1) {
                    VarDeclaration v = __r939.get(__key940);
                    (fd.ctfeCode.pimpl).onDeclaration(v);
                }
            }
        }
        if (fd.vresult != null)
            (fd.ctfeCode.pimpl).onDeclaration(fd.vresult);
        CtfeCompiler v = new CtfeCompiler(fd.ctfeCode.pimpl);
        v.ctfeCompile(fd.fbody);
    }

    public static Expression interpretFunction(UnionExp pue, FuncDeclaration fd, InterState istate, DArray<Expression> arguments, Expression thisarg) {
        assert(pue != null);
        if ((fd.semanticRun == PASS.semantic3))
        {
            fd.error(new BytePtr("circular dependency. Functions cannot be interpreted while being compiled"));
            return CTFEExp.cantexp;
        }
        if (!fd.functionSemantic3())
            return CTFEExp.cantexp;
        if ((fd.semanticRun < PASS.semantic3done))
            return CTFEExp.cantexp;
        if (fd.ctfeCode.pimpl == null)
            ctfeCompile(fd);
        Type tb = fd.type.toBasetype();
        assert(((tb.ty & 0xFF) == ENUMTY.Tfunction));
        TypeFunction tf = (TypeFunction)tb;
        if ((tf.parameterList.varargs != VarArg.none) && (arguments != null) && (fd.parameters != null) && ((arguments).length != (fd.parameters).length) || (fd.parameters == null) && ((arguments).length != 0))
        {
            fd.error(new BytePtr("C-style variadic functions are not yet implemented in CTFE"));
            return CTFEExp.cantexp;
        }
        if (fd.isNested() && (fd.toParentLocal().isFuncDeclaration() != null) && (thisarg == null) && (istate != null))
            thisarg = ctfeStack.getThis();
        if (fd.needThis() && (thisarg == null))
        {
            fd.error(new BytePtr("need `this` to access member `%s`"), fd.toChars());
            return CTFEExp.cantexp;
        }
        int dim = arguments != null ? (arguments).length : 0;
        assert(((fd.parameters != null ? (fd.parameters).length : 0) == dim));
        DArray<Expression> eargs = eargs = new DArray<Expression>(dim);
        try {
            {
                int i = 0;
                for (; (i < dim);i++){
                    Expression earg = (arguments).get(i);
                    Parameter fparam = tf.parameterList.get(i);
                    if ((fparam.storageClass & 2101248L) != 0)
                    {
                        if ((istate == null) && ((fparam.storageClass & 4096L) != 0))
                        {
                            earg.error(new BytePtr("global `%s` cannot be passed as an `out` parameter at compile time"), earg.toChars());
                            return CTFEExp.cantexp;
                        }
                        earg = interpret(earg, istate, CtfeGoal.ctfeNeedLvalue);
                        if (CTFEExp.isCantExp(earg))
                            return earg;
                    }
                    else if ((fparam.storageClass & 8192L) != 0)
                    {
                    }
                    else
                    {
                        Type ta = fparam.type.toBasetype();
                        if (((ta.ty & 0xFF) == ENUMTY.Tsarray))
                            {
                                AddrExp eaddr = earg.isAddrExp();
                                if ((eaddr) != null)
                                {
                                    earg = eaddr.e1;
                                }
                            }
                        earg = interpret(earg, istate, CtfeGoal.ctfeNeedRvalue);
                        if (CTFEExp.isCantExp(earg))
                            return earg;
                        if (((earg.op & 0xFF) == 49) && ((fparam.storageClass & 1048580L) == 0))
                            earg = copyLiteral(earg).copy();
                    }
                    if (((earg.op & 0xFF) == 51))
                    {
                        if (istate != null)
                            return earg;
                        ((ThrownExceptionExp)earg).generateUncaughtError();
                        return CTFEExp.cantexp;
                    }
                    eargs.set(i, earg);
                }
            }
            InterState istatex = new InterState();
            istatex.caller = istate;
            istatex.fd = fd;
            if (fd.isThis2)
            {
                Expression arg0 = thisarg;
                if ((arg0 != null) && ((arg0.type.ty & 0xFF) == ENUMTY.Tstruct))
                {
                    Type t = arg0.type.pointerTo();
                    arg0 = new AddrExp(arg0.loc, arg0);
                    arg0.type = t;
                }
                DArray<Expression> elements = new DArray<Expression>(2);
                elements.set(0, arg0);
                elements.set(1, ctfeStack.getThis());
                Type t2 = Type.tvoidptr.sarrayOf(2L);
                Loc loc = thisarg != null ? thisarg.loc : fd.loc.copy();
                thisarg = new ArrayLiteralExp(loc, t2, elements);
                thisarg = new AddrExp(loc, thisarg);
                thisarg.type = t2.pointerTo();
            }
            ctfeStack.startFrame(thisarg);
            if ((fd.vthis != null) && (thisarg != null))
            {
                ctfeStack.push(fd.vthis);
                setValue(fd.vthis, thisarg);
            }
            {
                int i = 0;
                for (; (i < dim);i++){
                    Expression earg = eargs.get(i);
                    Parameter fparam = tf.parameterList.get(i);
                    VarDeclaration v = (fd.parameters).get(i);
                    ctfeStack.push(v);
                    if (((fparam.storageClass & 2101248L) != 0) && ((earg.op & 0xFF) == 26) && (pequals(((VarExp)earg).var.toParent2(), fd)))
                    {
                        VarDeclaration vx = ((VarExp)earg).var.isVarDeclaration();
                        if (vx == null)
                        {
                            fd.error(new BytePtr("cannot interpret `%s` as a `ref` parameter"), earg.toChars());
                            return CTFEExp.cantexp;
                        }
                        int oldadr = vx.ctfeAdrOnStack;
                        ctfeStack.push(vx);
                        assert(!hasValue(vx));
                        v.ctfeAdrOnStack = oldadr;
                        assert(hasValue(v));
                    }
                    else
                    {
                        setValueWithoutChecking(v, earg);
                    }
                }
            }
            if (fd.vresult != null)
                ctfeStack.push(fd.vresult);
            CtfeStatus.callDepth += 1;
            if ((CtfeStatus.callDepth > CtfeStatus.maxCallDepth))
                CtfeStatus.maxCallDepth = CtfeStatus.callDepth;
            Expression e = null;
            for (; 1 != 0;){
                if ((CtfeStatus.callDepth > 1000))
                {
                    global.gag = 0;
                    fd.error(new BytePtr("CTFE recursion limit exceeded"));
                    e = CTFEExp.cantexp;
                    break;
                }
                e = interpret(pue, fd.fbody, istatex);
                if (CTFEExp.isCantExp(e))
                if (istatex.start != null)
                {
                    fd.error(new BytePtr("CTFE internal error: failed to resume at statement `%s`"), istatex.start.toChars());
                    return CTFEExp.cantexp;
                }
                if (CTFEExp.isGotoExp(e))
                {
                    istatex.start = istatex.gotoTarget;
                    istatex.gotoTarget = null;
                }
                else
                {
                    assert((e == null) || ((e.op & 0xFF) != 192) && ((e.op & 0xFF) != 191));
                    break;
                }
            }
            if ((e == null) && ((tf.next.ty & 0xFF) == ENUMTY.Tvoid))
                e = CTFEExp.voidexp;
            if (tf.isref && ((e.op & 0xFF) == 26) && (pequals(((VarExp)e).var, fd.vthis)))
                e = thisarg;
            if (tf.isref && fd.isThis2 && ((e.op & 0xFF) == 62))
            {
                IndexExp ie = (IndexExp)e;
                PtrExp pe = ie.e1.isPtrExp();
                VarExp ve = pe == null ? null : pe.e1.isVarExp();
                if ((ve != null) && (pequals(ve.var, fd.vthis)))
                {
                    IntegerExp ne = ie.e2.isIntegerExp();
                    assert(ne != null);
                    assert(((thisarg.op & 0xFF) == 19));
                    e = ((AddrExp)thisarg).e1;
                    e = (((ArrayLiteralExp)e).elements).get((int)ne.getInteger());
                    if (((e.op & 0xFF) == 19))
                    {
                        e = ((AddrExp)e).e1;
                    }
                }
            }
            assert((e != null));
            CtfeStatus.callDepth -= 1;
            ctfeStack.endFrame();
            if ((istate == null) && ((e.op & 0xFF) == 51))
            {
                if ((pequals(e, (pue).exp())))
                    e = (pue).copy();
                ((ThrownExceptionExp)e).generateUncaughtError();
                e = CTFEExp.cantexp;
            }
            return e;
        }
        finally {
        }
    }

    public static class Interpreter extends Visitor
    {
        public InterState istate;
        public int goal = 0;
        public Expression result;
        public UnionExp pue;
        public  Interpreter(UnionExp pue, InterState istate, int goal) {
            this.pue = pue;
            this.istate = istate;
            this.goal = goal;
        }

        public  boolean exceptionOrCant(Expression e) {
            if (exceptionOrCantInterpret(e))
            {
                this.result = ((e.op & 0xFF) == 233) ? CTFEExp.cantexp : e;
                return true;
            }
            return false;
        }

        public static DArray<Expression> copyArrayOnWrite(DArray<Expression> exps, DArray<Expression> original) {
            if ((exps == original))
            {
                if (original == null)
                    exps = new DArray<Expression>();
                else
                    exps = (original).copy();
                CtfeStatus.numArrayAllocs += 1;
            }
            return exps;
        }

        public  void visit(Statement s) {
            if ((this.istate).start != null)
            {
                if ((!pequals((this.istate).start, s)))
                    return ;
                (this.istate).start = null;
            }
            s.error(new BytePtr("statement `%s` cannot be interpreted at compile time"), s.toChars());
            this.result = CTFEExp.cantexp;
        }

        public  void visit(ExpStatement s) {
            if ((this.istate).start != null)
            {
                if ((!pequals((this.istate).start, s)))
                    return ;
                (this.istate).start = null;
            }
            Expression e = interpret(this.pue, s.exp, this.istate, CtfeGoal.ctfeNeedNothing);
            if (this.exceptionOrCant(e))
                return ;
        }

        public  void visit(CompoundStatement s) {
            if ((pequals((this.istate).start, s)))
                (this.istate).start = null;
            int dim = s.statements != null ? (s.statements).length : 0;
            {
                int __key942 = 0;
                int __limit943 = dim;
                for (; (__key942 < __limit943);__key942 += 1) {
                    int i = __key942;
                    Statement sx = (s.statements).get(i);
                    this.result = interpret(this.pue, sx, this.istate);
                    if (this.result != null)
                        break;
                }
            }
        }

        public  void visit(UnrolledLoopStatement s) {
            if ((pequals((this.istate).start, s)))
                (this.istate).start = null;
            int dim = s.statements != null ? (s.statements).length : 0;
            {
                int __key944 = 0;
                int __limit945 = dim;
                for (; (__key944 < __limit945);__key944 += 1) {
                    int i = __key944;
                    Statement sx = (s.statements).get(i);
                    Expression e = interpret(this.pue, sx, this.istate);
                    if (e == null)
                        continue;
                    if (this.exceptionOrCant(e))
                        return ;
                    if (((e.op & 0xFF) == 191))
                    {
                        if (((this.istate).gotoTarget != null) && (!pequals((this.istate).gotoTarget, s)))
                        {
                            this.result = e;
                            return ;
                        }
                        (this.istate).gotoTarget = null;
                        this.result = null;
                        return ;
                    }
                    if (((e.op & 0xFF) == 192))
                    {
                        if (((this.istate).gotoTarget != null) && (!pequals((this.istate).gotoTarget, s)))
                        {
                            this.result = e;
                            return ;
                        }
                        (this.istate).gotoTarget = null;
                        continue;
                    }
                    this.result = e;
                    break;
                }
            }
        }

        public  void visit(IfStatement s) {
            if ((pequals((this.istate).start, s)))
                (this.istate).start = null;
            if ((this.istate).start != null)
            {
                Expression e = null;
                e = interpret(s.ifbody, this.istate);
                if ((e == null) && ((this.istate).start != null))
                    e = interpret(s.elsebody, this.istate);
                this.result = e;
                return ;
            }
            UnionExp ue = null;
            Expression e = interpret(ue, s.condition, this.istate, CtfeGoal.ctfeNeedRvalue);
            assert(e != null);
            if (this.exceptionOrCant(e))
                return ;
            if (isTrueBool(e))
                this.result = interpret(this.pue, s.ifbody, this.istate);
            else if (e.isBool(false))
                this.result = interpret(this.pue, s.elsebody, this.istate);
            else
            {
                this.result = CTFEExp.cantexp;
            }
        }

        public  void visit(ScopeStatement s) {
            if ((pequals((this.istate).start, s)))
                (this.istate).start = null;
            this.result = interpret(this.pue, s.statement, this.istate);
        }

        public static boolean stopPointersEscaping(Loc loc, Expression e) {
            if (!e.type.hasPointers())
                return true;
            if (isPointer(e.type))
            {
                Expression x = e;
                {
                    AddrExp eaddr = e.isAddrExp();
                    if ((eaddr) != null)
                        x = eaddr.e1;
                }
                VarDeclaration v = null;
                for (; ((x.op & 0xFF) == 26) && ((v = ((VarExp)x).var.isVarDeclaration()) != null);){
                    if ((v.storage_class & 2097152L) != 0)
                    {
                        x = getValue(v);
                        {
                            AddrExp eaddr = e.isAddrExp();
                            if ((eaddr) != null)
                                eaddr.e1 = x;
                        }
                        continue;
                    }
                    if (ctfeStack.isInCurrentFrame(v))
                    {
                        error(loc, new BytePtr("returning a pointer to a local stack variable"));
                        return false;
                    }
                    else
                        break;
                }
            }
            {
                StructLiteralExp se = e.isStructLiteralExp();
                if ((se) != null)
                {
                    return stopPointersEscapingFromArray(loc, se.elements);
                }
            }
            {
                ArrayLiteralExp ale = e.isArrayLiteralExp();
                if ((ale) != null)
                {
                    return stopPointersEscapingFromArray(loc, ale.elements);
                }
            }
            {
                AssocArrayLiteralExp aae = e.isAssocArrayLiteralExp();
                if ((aae) != null)
                {
                    if (!stopPointersEscapingFromArray(loc, aae.keys))
                        return false;
                    return stopPointersEscapingFromArray(loc, aae.values);
                }
            }
            return true;
        }

        public static boolean stopPointersEscapingFromArray(Loc loc, DArray<Expression> elems) {
            {
                Slice<Expression> __r946 = (elems).opSlice().copy();
                int __key947 = 0;
                for (; (__key947 < __r946.getLength());__key947 += 1) {
                    Expression e = __r946.get(__key947);
                    if ((e != null) && !stopPointersEscaping(loc, e))
                        return false;
                }
            }
            return true;
        }

        public  void visit(ReturnStatement s) {
            if ((this.istate).start != null)
            {
                if ((!pequals((this.istate).start, s)))
                    return ;
                (this.istate).start = null;
            }
            if (s.exp == null)
            {
                this.result = CTFEExp.voidexp;
                return ;
            }
            assert((this.istate != null) && ((this.istate).fd != null) && ((this.istate).fd.type != null) && (((this.istate).fd.type.ty & 0xFF) == ENUMTY.Tfunction));
            TypeFunction tf = (TypeFunction)(this.istate).fd.type;
            if (tf.isref)
            {
                this.result = interpret(this.pue, s.exp, this.istate, CtfeGoal.ctfeNeedLvalue);
                return ;
            }
            if ((tf.next != null) && ((tf.next.ty & 0xFF) == ENUMTY.Tdelegate) && ((this.istate).fd.closureVars.length > 0))
            {
                s.error(new BytePtr("closures are not yet supported in CTFE"));
                this.result = CTFEExp.cantexp;
                return ;
            }
            Expression e = interpret(this.pue, s.exp, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e))
                return ;
            if (!stopPointersEscaping(s.loc, e))
            {
                this.result = CTFEExp.cantexp;
                return ;
            }
            if (needToCopyLiteral(e))
                e = copyLiteral(e).copy();
            this.result = e;
        }

        public static Statement findGotoTarget(InterState istate, Identifier ident) {
            Statement target = null;
            if (ident != null)
            {
                LabelDsymbol label = (istate).fd.searchLabel(ident);
                assert((label != null) && (label.statement != null));
                LabelStatement ls = label.statement;
                target = ls.gotoTarget != null ? ls.gotoTarget : ls.statement;
            }
            return target;
        }

        public  void visit(BreakStatement s) {
            if ((this.istate).start != null)
            {
                if ((!pequals((this.istate).start, s)))
                    return ;
                (this.istate).start = null;
            }
            (this.istate).gotoTarget = findGotoTarget(this.istate, s.ident);
            this.result = CTFEExp.breakexp;
        }

        public  void visit(ContinueStatement s) {
            if ((this.istate).start != null)
            {
                if ((!pequals((this.istate).start, s)))
                    return ;
                (this.istate).start = null;
            }
            (this.istate).gotoTarget = findGotoTarget(this.istate, s.ident);
            this.result = CTFEExp.continueexp;
        }

        public  void visit(WhileStatement s) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(DoStatement s) {
            if ((pequals((this.istate).start, s)))
                (this.istate).start = null;
            for (; 1 != 0;){
                Expression e = interpret(s._body, this.istate);
                if ((e == null) && ((this.istate).start != null))
                    return ;
                assert((this.istate).start == null);
                if (this.exceptionOrCant(e))
                    return ;
                if ((e != null) && ((e.op & 0xFF) == 191))
                {
                    if (((this.istate).gotoTarget != null) && (!pequals((this.istate).gotoTarget, s)))
                    {
                        this.result = e;
                        return ;
                    }
                    (this.istate).gotoTarget = null;
                    break;
                }
                if ((e != null) && ((e.op & 0xFF) == 192))
                {
                    if (((this.istate).gotoTarget != null) && (!pequals((this.istate).gotoTarget, s)))
                    {
                        this.result = e;
                        return ;
                    }
                    (this.istate).gotoTarget = null;
                    e = null;
                }
                if (e != null)
                {
                    this.result = e;
                    return ;
                }
                UnionExp ue = null;
                e = interpret(ue, s.condition, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e))
                    return ;
                if (e.isConst() == 0)
                {
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                if (e.isBool(false))
                    break;
                assert(isTrueBool(e));
            }
            assert((this.result == null));
        }

        public  void visit(ForStatement s) {
            if ((pequals((this.istate).start, s)))
                (this.istate).start = null;
            UnionExp ueinit = null;
            Expression ei = interpret(ueinit, s._init, this.istate);
            if (this.exceptionOrCant(ei))
                return ;
            assert(ei == null);
            for (; 1 != 0;){
                if ((s.condition != null) && ((this.istate).start == null))
                {
                    UnionExp ue = null;
                    Expression e = interpret(ue, s.condition, this.istate, CtfeGoal.ctfeNeedRvalue);
                    if (this.exceptionOrCant(e))
                        return ;
                    if (e.isBool(false))
                        break;
                    assert(isTrueBool(e));
                }
                Expression e = interpret(this.pue, s._body, this.istate);
                if ((e == null) && ((this.istate).start != null))
                    return ;
                assert((this.istate).start == null);
                if (this.exceptionOrCant(e))
                    return ;
                if ((e != null) && ((e.op & 0xFF) == 191))
                {
                    if (((this.istate).gotoTarget != null) && (!pequals((this.istate).gotoTarget, s)))
                    {
                        this.result = e;
                        return ;
                    }
                    (this.istate).gotoTarget = null;
                    break;
                }
                if ((e != null) && ((e.op & 0xFF) == 192))
                {
                    if (((this.istate).gotoTarget != null) && (!pequals((this.istate).gotoTarget, s)))
                    {
                        this.result = e;
                        return ;
                    }
                    (this.istate).gotoTarget = null;
                    e = null;
                }
                if (e != null)
                {
                    this.result = e;
                    return ;
                }
                UnionExp uei = null;
                e = interpret(uei, s.increment, this.istate, CtfeGoal.ctfeNeedNothing);
                if (this.exceptionOrCant(e))
                    return ;
            }
            assert((this.result == null));
        }

        public  void visit(ForeachStatement s) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ForeachRangeStatement s) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(SwitchStatement s) {
            if ((pequals((this.istate).start, s)))
                (this.istate).start = null;
            if ((this.istate).start != null)
            {
                Expression e = interpret(s._body, this.istate);
                if ((this.istate).start != null)
                    return ;
                if (this.exceptionOrCant(e))
                    return ;
                if ((e != null) && ((e.op & 0xFF) == 191))
                {
                    if (((this.istate).gotoTarget != null) && (!pequals((this.istate).gotoTarget, s)))
                    {
                        this.result = e;
                        return ;
                    }
                    (this.istate).gotoTarget = null;
                    e = null;
                }
                this.result = e;
                return ;
            }
            UnionExp uecond = null;
            Expression econdition = interpret(uecond, s.condition, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(econdition))
                return ;
            Statement scase = null;
            if (s.cases != null)
            {
                Slice<CaseStatement> __r948 = (s.cases).opSlice().copy();
                int __key949 = 0;
                for (; (__key949 < __r948.getLength());__key949 += 1) {
                    CaseStatement cs = __r948.get(__key949);
                    UnionExp uecase = null;
                    Expression ecase = interpret(uecase, cs.exp, this.istate, CtfeGoal.ctfeNeedRvalue);
                    if (this.exceptionOrCant(ecase))
                        return ;
                    if (ctfeEqual(cs.exp.loc, TOK.equal, econdition, ecase) != 0)
                    {
                        scase = cs;
                        break;
                    }
                }
            }
            if (scase == null)
            {
                if (s.hasNoDefault != 0)
                    s.error(new BytePtr("no `default` or `case` for `%s` in `switch` statement"), econdition.toChars());
                scase = s.sdefault;
            }
            assert(scase != null);
            (this.istate).start = scase;
            Expression e = interpret(this.pue, s._body, this.istate);
            assert((this.istate).start == null);
            if ((e != null) && ((e.op & 0xFF) == 191))
            {
                if (((this.istate).gotoTarget != null) && (!pequals((this.istate).gotoTarget, s)))
                {
                    this.result = e;
                    return ;
                }
                (this.istate).gotoTarget = null;
                e = null;
            }
            this.result = e;
        }

        public  void visit(CaseStatement s) {
            if ((pequals((this.istate).start, s)))
                (this.istate).start = null;
            this.result = interpret(this.pue, s.statement, this.istate);
        }

        public  void visit(DefaultStatement s) {
            if ((pequals((this.istate).start, s)))
                (this.istate).start = null;
            this.result = interpret(this.pue, s.statement, this.istate);
        }

        public  void visit(GotoStatement s) {
            if ((this.istate).start != null)
            {
                if ((!pequals((this.istate).start, s)))
                    return ;
                (this.istate).start = null;
            }
            assert((s.label != null) && (s.label.statement != null));
            (this.istate).gotoTarget = s.label.statement;
            this.result = CTFEExp.gotoexp;
        }

        public  void visit(GotoCaseStatement s) {
            if ((this.istate).start != null)
            {
                if ((!pequals((this.istate).start, s)))
                    return ;
                (this.istate).start = null;
            }
            assert(s.cs != null);
            (this.istate).gotoTarget = s.cs;
            this.result = CTFEExp.gotoexp;
        }

        public  void visit(GotoDefaultStatement s) {
            if ((this.istate).start != null)
            {
                if ((!pequals((this.istate).start, s)))
                    return ;
                (this.istate).start = null;
            }
            assert((s.sw != null) && (s.sw.sdefault != null));
            (this.istate).gotoTarget = s.sw.sdefault;
            this.result = CTFEExp.gotoexp;
        }

        public  void visit(LabelStatement s) {
            if ((pequals((this.istate).start, s)))
                (this.istate).start = null;
            this.result = interpret(this.pue, s.statement, this.istate);
        }

        public  void visit(TryCatchStatement s) {
            if ((pequals((this.istate).start, s)))
                (this.istate).start = null;
            if ((this.istate).start != null)
            {
                Expression e = null;
                e = interpret(this.pue, s._body, this.istate);
                {
                    Slice<Catch> __r950 = (s.catches).opSlice().copy();
                    int __key951 = 0;
                    for (; (__key951 < __r950.getLength());__key951 += 1) {
                        Catch ca = __r950.get(__key951);
                        if ((e != null) || ((this.istate).start == null))
                            break;
                        e = interpret(this.pue, ca.handler, this.istate);
                    }
                }
                this.result = e;
                return ;
            }
            Expression e = interpret(s._body, this.istate);
            if ((e != null) && ((e.op & 0xFF) == 51))
            {
                ThrownExceptionExp ex = (ThrownExceptionExp)e;
                Type extype = ex.thrown.originalClass().type;
                {
                    Slice<Catch> __r952 = (s.catches).opSlice().copy();
                    int __key953 = 0;
                    for (; (__key953 < __r952.getLength());__key953 += 1) {
                        Catch ca = __r952.get(__key953);
                        Type catype = ca.type;
                        if (!catype.equals(extype) && !catype.isBaseOf(extype, null))
                            continue;
                        if (ca.var != null)
                        {
                            ctfeStack.push(ca.var);
                            setValue(ca.var, ex.thrown);
                        }
                        e = interpret(ca.handler, this.istate);
                        if (CTFEExp.isGotoExp(e))
                        {
                            InterState istatex = this.istate.copy();
                            istatex.start = (this.istate).gotoTarget;
                            istatex.gotoTarget = null;
                            Expression eh = interpret(ca.handler, istatex);
                            if (istatex.start == null)
                            {
                                (this.istate).gotoTarget = null;
                                e = eh;
                            }
                        }
                        break;
                    }
                }
            }
            this.result = e;
        }

        public static boolean isAnErrorException(ClassDeclaration cd) {
            return (pequals(cd, ClassDeclaration.errorException)) || ClassDeclaration.errorException.isBaseOf(cd, null);
        }

        public static ThrownExceptionExp chainExceptions(ThrownExceptionExp oldest, ThrownExceptionExp newest) {
            ClassReferenceExp boss = oldest.thrown;
            int next = 4;
            assert((((boss.value.elements).get(4).type.ty & 0xFF) == ENUMTY.Tclass));
            ClassReferenceExp collateral = newest.thrown;
            if (isAnErrorException(collateral.originalClass()) && !isAnErrorException(boss.originalClass()))
            {
                int bypass = 5;
                if ((((collateral.value.elements).get(bypass).type.ty & 0xFF) == ENUMTY.Tuns32))
                    bypass += 1;
                assert((((collateral.value.elements).get(bypass).type.ty & 0xFF) == ENUMTY.Tclass));
                collateral.value.elements.set(bypass, boss);
                return newest;
            }
            for (; (((boss.value.elements).get(4).op & 0xFF) == 50);){
                boss = (ClassReferenceExp)(boss.value.elements).get(4);
            }
            boss.value.elements.set(4, collateral);
            return oldest;
        }

        public  void visit(TryFinallyStatement s) {
            if ((pequals((this.istate).start, s)))
                (this.istate).start = null;
            if ((this.istate).start != null)
            {
                Expression e = null;
                e = interpret(this.pue, s._body, this.istate);
                this.result = e;
                return ;
            }
            Expression ex = interpret(s._body, this.istate);
            if (CTFEExp.isCantExp(ex))
            {
                this.result = ex;
                return ;
            }
            for (; CTFEExp.isGotoExp(ex);){
                InterState istatex = this.istate.copy();
                istatex.start = (this.istate).gotoTarget;
                istatex.gotoTarget = null;
                Expression bex = interpret(s._body, istatex);
                if (istatex.start != null)
                {
                    break;
                }
                if (CTFEExp.isCantExp(bex))
                {
                    this.result = bex;
                    return ;
                }
                this.istate.opAssign(istatex);
                ex = bex;
            }
            Expression ey = interpret(s.finalbody, this.istate);
            if (CTFEExp.isCantExp(ey))
            {
                this.result = ey;
                return ;
            }
            if ((ey != null) && ((ey.op & 0xFF) == 51))
            {
                if ((ex != null) && ((ex.op & 0xFF) == 51))
                    ex = chainExceptions((ThrownExceptionExp)ex, (ThrownExceptionExp)ey);
                else
                    ex = ey;
            }
            this.result = ex;
        }

        public  void visit(ThrowStatement s) {
            if ((this.istate).start != null)
            {
                if ((!pequals((this.istate).start, s)))
                    return ;
                (this.istate).start = null;
            }
            Expression e = interpret(s.exp, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e))
                return ;
            assert(((e.op & 0xFF) == 50));
            this.result = new ThrownExceptionExp(s.loc, (ClassReferenceExp)e);
        }

        public  void visit(ScopeGuardStatement s) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(WithStatement s) {
            if ((pequals((this.istate).start, s)))
                (this.istate).start = null;
            if ((this.istate).start != null)
            {
                this.result = s._body != null ? interpret(s._body, this.istate) : null;
                return ;
            }
            if (((s.exp.op & 0xFF) == 203) || ((s.exp.op & 0xFF) == 20))
            {
                this.result = interpret(this.pue, s._body, this.istate);
                return ;
            }
            Expression e = interpret(s.exp, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e))
                return ;
            if (((s.wthis.type.ty & 0xFF) == ENUMTY.Tpointer) && ((s.exp.type.ty & 0xFF) != ENUMTY.Tpointer))
            {
                e = new AddrExp(s.loc, e, s.wthis.type);
            }
            ctfeStack.push(s.wthis);
            setValue(s.wthis, e);
            e = interpret(s._body, this.istate);
            if (CTFEExp.isGotoExp(e))
            {
                InterState istatex = this.istate.copy();
                istatex.start = (this.istate).gotoTarget;
                istatex.gotoTarget = null;
                Expression ex = interpret(s._body, istatex);
                if (istatex.start == null)
                {
                    (this.istate).gotoTarget = null;
                    e = ex;
                }
            }
            ctfeStack.pop(s.wthis);
            this.result = e;
        }

        public  void visit(AsmStatement s) {
            if ((this.istate).start != null)
            {
                if ((!pequals((this.istate).start, s)))
                    return ;
                (this.istate).start = null;
            }
            s.error(new BytePtr("`asm` statements cannot be interpreted at compile time"));
            this.result = CTFEExp.cantexp;
        }

        public  void visit(ImportStatement s) {
            if ((this.istate).start != null)
            {
                if ((!pequals((this.istate).start, s)))
                    return ;
                (this.istate).start = null;
            }
        }

        public  void visit(Expression e) {
            e.error(new BytePtr("cannot interpret `%s` at compile time"), e.toChars());
            this.result = CTFEExp.cantexp;
        }

        public  void visit(ThisExp e) {
            if ((this.goal == CtfeGoal.ctfeNeedLvalue))
            {
                if ((this.istate != null) && ((this.istate).fd.vthis != null))
                {
                    this.result = new VarExp(e.loc, (this.istate).fd.vthis, true);
                    if ((this.istate).fd.isThis2)
                    {
                        this.result = new PtrExp(e.loc, this.result);
                        this.result.type = Type.tvoidptr.sarrayOf(2L);
                        this.result = new IndexExp(e.loc, this.result, literal0());
                    }
                    this.result.type = e.type;
                }
                else
                    this.result = e;
                return ;
            }
            this.result = ctfeStack.getThis();
            if (this.result != null)
            {
                if ((this.istate != null) && (this.istate).fd.isThis2)
                {
                    assert(((this.result.op & 0xFF) == 19));
                    this.result = ((AddrExp)this.result).e1;
                    assert(((this.result.op & 0xFF) == 47));
                    this.result = (((ArrayLiteralExp)this.result).elements).get(0);
                    if (((e.type.ty & 0xFF) == ENUMTY.Tstruct))
                    {
                        this.result = ((AddrExp)this.result).e1;
                    }
                    return ;
                }
                assert(((this.result.op & 0xFF) == 49) || ((this.result.op & 0xFF) == 50));
                return ;
            }
            e.error(new BytePtr("value of `this` is not known at compile time"));
            this.result = CTFEExp.cantexp;
        }

        public  void visit(NullExp e) {
            this.result = e;
        }

        public  void visit(IntegerExp e) {
            this.result = e;
        }

        public  void visit(RealExp e) {
            this.result = e;
        }

        public  void visit(ComplexExp e) {
            this.result = e;
        }

        public  void visit(StringExp e) {
            this.result = e;
        }

        public  void visit(FuncExp e) {
            this.result = e;
        }

        public  void visit(SymOffExp e) {
            if ((e.var.isFuncDeclaration() != null) && (e.offset == 0L))
            {
                this.result = e;
                return ;
            }
            if (isTypeInfo_Class(e.type) && (e.offset == 0L))
            {
                this.result = e;
                return ;
            }
            if (((e.type.ty & 0xFF) != ENUMTY.Tpointer))
            {
                e.error(new BytePtr("cannot interpret `%s` at compile time"), e.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            Type pointee = ((TypePointer)e.type).next;
            if (e.var.isThreadlocal())
            {
                e.error(new BytePtr("cannot take address of thread-local variable %s at compile time"), e.var.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            Type fromType = null;
            if (((e.var.type.ty & 0xFF) == ENUMTY.Tarray) || ((e.var.type.ty & 0xFF) == ENUMTY.Tsarray))
            {
                fromType = ((TypeArray)e.var.type).next;
            }
            if (e.var.isDataseg() && (e.offset == 0L) && isSafePointerCast(e.var.type, pointee) || (fromType != null) && isSafePointerCast(fromType, pointee))
            {
                this.result = e;
                return ;
            }
            Expression val = getVarExp(e.loc, this.istate, e.var, this.goal);
            if (this.exceptionOrCant(val))
                return ;
            if (((val.type.ty & 0xFF) == ENUMTY.Tarray) || ((val.type.ty & 0xFF) == ENUMTY.Tsarray))
            {
                Type elemtype = ((TypeArray)val.type).next;
                long elemsize = elemtype.size();
                if (((val.type.ty & 0xFF) == ENUMTY.Tsarray) && ((pointee.ty & 0xFF) == ENUMTY.Tarray) && (elemsize == pointee.nextOf().size()))
                {
                    (this.pue) = new UnionExp(new AddrExp(e.loc, val, e.type));
                    this.result = (this.pue).exp();
                    return ;
                }
                if (((val.type.ty & 0xFF) == ENUMTY.Tsarray) && ((pointee.ty & 0xFF) == ENUMTY.Tsarray) && (elemsize == pointee.nextOf().size()))
                {
                    int d = (int)((TypeSArray)pointee).dim.toInteger();
                    Expression elwr = new IntegerExp(e.loc, e.offset / elemsize, Type.tsize_t);
                    Expression eupr = new IntegerExp(e.loc, e.offset / elemsize + (long)d, Type.tsize_t);
                    SliceExp se = new SliceExp(e.loc, val, elwr, eupr);
                    se.type = pointee;
                    (this.pue) = new UnionExp(new AddrExp(e.loc, se, e.type));
                    this.result = (this.pue).exp();
                    return ;
                }
                if (!isSafePointerCast(elemtype, pointee))
                {
                    if ((e.offset == 0L) && isSafePointerCast(e.var.type, pointee))
                    {
                        VarExp ve = new VarExp(e.loc, e.var, true);
                        ve.type = elemtype;
                        (this.pue) = new UnionExp(new AddrExp(e.loc, ve, e.type));
                        this.result = (this.pue).exp();
                        return ;
                    }
                    e.error(new BytePtr("reinterpreting cast from `%s` to `%s` is not supported in CTFE"), val.type.toChars(), e.type.toChars());
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                long sz = pointee.size();
                long indx = e.offset / sz;
                assert((sz * indx == e.offset));
                Expression aggregate = null;
                if (((val.op & 0xFF) == 47) || ((val.op & 0xFF) == 121))
                {
                    aggregate = val;
                }
                else {
                    SliceExp se = val.isSliceExp();
                    if ((se) != null)
                    {
                        aggregate = se.e1;
                        UnionExp uelwr = null;
                        Expression lwr = interpret(uelwr, se.lwr, this.istate, CtfeGoal.ctfeNeedRvalue);
                        indx += lwr.toInteger();
                    }
                }
                if (aggregate != null)
                {
                    IntegerExp ofs = new IntegerExp(e.loc, indx, Type.tsize_t);
                    IndexExp ei = new IndexExp(e.loc, aggregate, ofs);
                    ei.type = elemtype;
                    (this.pue) = new UnionExp(new AddrExp(e.loc, ei, e.type));
                    this.result = (this.pue).exp();
                    return ;
                }
            }
            else if ((e.offset == 0L) && isSafePointerCast(e.var.type, pointee))
            {
                VarExp ve = new VarExp(e.loc, e.var, true);
                ve.type = e.var.type;
                (this.pue) = new UnionExp(new AddrExp(e.loc, ve, e.type));
                this.result = (this.pue).exp();
                return ;
            }
            e.error(new BytePtr("cannot convert `&%s` to `%s` at compile time"), e.var.type.toChars(), e.type.toChars());
            this.result = CTFEExp.cantexp;
        }

        public  void visit(AddrExp e) {
            {
                VarExp ve = e.e1.isVarExp();
                if ((ve) != null)
                {
                    Declaration decl = ve.var;
                    if (decl.isImportedSymbol())
                    {
                        e.error(new BytePtr("cannot take address of imported symbol `%s` at compile time"), decl.toChars());
                        this.result = CTFEExp.cantexp;
                        return ;
                    }
                    if (decl.isDataseg())
                    {
                        (this.pue) = new UnionExp(new SymOffExp(e.loc, ((VarExp)e.e1).var, 0));
                        this.result = (this.pue).exp();
                        this.result.type = e.type;
                        return ;
                    }
                }
            }
            Expression er = interpret(e.e1, this.istate, CtfeGoal.ctfeNeedLvalue);
            {
                VarExp ve = er.isVarExp();
                if ((ve) != null)
                    if ((pequals(ve.var, (this.istate).fd.vthis)))
                        er = interpret(er, this.istate, CtfeGoal.ctfeNeedRvalue);
            }
            if (this.exceptionOrCant(er))
                return ;
            (this.pue) = new UnionExp(new AddrExp(e.loc, er, e.type));
            this.result = (this.pue).exp();
        }

        public  void visit(DelegateExp e) {
            {
                VarExp ve1 = e.e1.isVarExp();
                if ((ve1) != null)
                    if ((pequals(ve1.var, e.func)))
                    {
                        this.result = e;
                        return ;
                    }
            }
            Expression er = interpret(this.pue, e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(er))
                return ;
            if ((pequals(er, e.e1)))
            {
                this.result = e;
            }
            else
            {
                er = (pequals(er, (this.pue).exp())) ? (this.pue).copy() : er;
                (this.pue) = new UnionExp(new DelegateExp(e.loc, er, e.func, false));
                this.result = (this.pue).exp();
                this.result.type = e.type;
            }
        }

        public static Expression getVarExp(Loc loc, InterState istate, Declaration d, int goal) {
            Expression e = CTFEExp.cantexp;
            {
                VarDeclaration v = d.isVarDeclaration();
                if ((v) != null)
                {
                    if ((pequals(v.ident, Id.ctfe)))
                        return new IntegerExp(loc, 1L, Type.tbool);
                    if ((v.originalType == null) && (v.semanticRun < PASS.semanticdone))
                    {
                        dsymbolSemantic(v, null);
                        if (((v.type.ty & 0xFF) == ENUMTY.Terror))
                            return CTFEExp.cantexp;
                    }
                    if (v.isConst() || v.isImmutable() || ((v.storage_class & 8388608L) != 0) && !hasValue(v) && (v._init != null) && !v.isCTFE())
                    {
                        if (v.inuse != 0)
                        {
                            error(loc, new BytePtr("circular initialization of %s `%s`"), v.kind(), v.toPrettyChars(false));
                            return CTFEExp.cantexp;
                        }
                        if (v._scope != null)
                        {
                            v.inuse++;
                            v._init = initializerSemantic(v._init, v._scope, v.type, NeedInterpret.INITinterpret);
                            v.inuse--;
                        }
                        e = initializerToExpression(v._init, v.type);
                        if (e == null)
                            return CTFEExp.cantexp;
                        assert(e.type != null);
                        if (((e.op & 0xFF) == 95) || ((e.op & 0xFF) == 96))
                        {
                            AssignExp ae = (AssignExp)e;
                            e = ae.e2;
                        }
                        if (((e.op & 0xFF) == 127))
                        {
                        }
                        else if (v.isDataseg() || ((v.storage_class & 8388608L) != 0))
                        {
                            e = scrubCacheValue(e);
                            ctfeStack.saveGlobalConstant(v, e);
                        }
                        else
                        {
                            v.inuse++;
                            e = interpret(e, istate, CtfeGoal.ctfeNeedRvalue);
                            v.inuse--;
                            if (CTFEExp.isCantExp(e) && (global.gag == 0) && (CtfeStatus.stackTraceCallsToSuppress == 0))
                                errorSupplemental(loc, new BytePtr("while evaluating %s.init"), v.toChars());
                            if (exceptionOrCantInterpret(e))
                                return e;
                        }
                    }
                    else if (v.isCTFE() && !hasValue(v))
                    {
                        if ((v._init != null) && (v.type.size() != 0L))
                        {
                            if (v._init.isVoidInitializer() != null)
                            {
                                error(loc, new BytePtr("CTFE internal error: trying to access uninitialized var"));
                                throw new AssertionError("Unreachable code!");
                            }
                            e = initializerToExpression(v._init, null);
                        }
                        else
                            e = v.type.defaultInitLiteral(e.loc);
                        e = interpret(e, istate, CtfeGoal.ctfeNeedRvalue);
                    }
                    else if (!(v.isDataseg() || ((v.storage_class & 8388608L) != 0)) && !v.isCTFE() && (istate == null))
                    {
                        error(loc, new BytePtr("variable `%s` cannot be read at compile time"), v.toChars());
                        return CTFEExp.cantexp;
                    }
                    else
                    {
                        e = hasValue(v) ? getValue(v) : null;
                        if ((e == null) && !v.isCTFE() && v.isDataseg())
                        {
                            error(loc, new BytePtr("static variable `%s` cannot be read at compile time"), v.toChars());
                            return CTFEExp.cantexp;
                        }
                        if (e == null)
                        {
                            assert(!((v._init != null) && (v._init.isVoidInitializer() != null)));
                            error(loc, new BytePtr("variable `%s` cannot be read at compile time"), v.toChars());
                            return CTFEExp.cantexp;
                        }
                        {
                            VoidInitExp vie = e.isVoidInitExp();
                            if ((vie) != null)
                            {
                                error(loc, new BytePtr("cannot read uninitialized variable `%s` in ctfe"), v.toPrettyChars(false));
                                errorSupplemental(vie.var.loc, new BytePtr("`%s` was uninitialized and used before set"), vie.var.toChars());
                                return CTFEExp.cantexp;
                            }
                        }
                        if ((goal != CtfeGoal.ctfeNeedLvalue) && v.isRef() || v.isOut())
                            e = interpret(e, istate, goal);
                    }
                    if (e == null)
                        e = CTFEExp.cantexp;
                }
                else {
                    SymbolDeclaration s = d.isSymbolDeclaration();
                    if ((s) != null)
                    {
                        e = s.dsym.type.defaultInitLiteral(loc);
                        if (((e.op & 0xFF) == 127))
                            error(loc, new BytePtr("CTFE failed because of previous errors in `%s.init`"), s.toChars());
                        e = expressionSemantic(e, null);
                        if (((e.op & 0xFF) == 127))
                            e = CTFEExp.cantexp;
                        else
                            e = interpret(e, istate, goal);
                    }
                    else
                        error(loc, new BytePtr("cannot interpret declaration `%s` at compile time"), d.toChars());
                }
            }
            return e;
        }

        public  void visit(VarExp e) {
            if (e.var.isFuncDeclaration() != null)
            {
                this.result = e;
                return ;
            }
            if ((this.goal == CtfeGoal.ctfeNeedLvalue))
            {
                VarDeclaration v = e.var.isVarDeclaration();
                if ((v != null) && !v.isDataseg() && !v.isCTFE() && (this.istate == null))
                {
                    e.error(new BytePtr("variable `%s` cannot be read at compile time"), v.toChars());
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                if ((v != null) && !hasValue(v))
                {
                    if (!v.isCTFE() && v.isDataseg())
                        e.error(new BytePtr("static variable `%s` cannot be read at compile time"), v.toChars());
                    else
                        e.error(new BytePtr("variable `%s` cannot be read at compile time"), v.toChars());
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                if ((v != null) && ((v.storage_class & 2101248L) != 0) && hasValue(v))
                {
                    Expression ev = getValue(v);
                    if (((ev.op & 0xFF) == 26) || ((ev.op & 0xFF) == 62) || ((ev.op & 0xFF) == 31) || ((ev.op & 0xFF) == 27))
                    {
                        this.result = interpret(this.pue, ev, this.istate, this.goal);
                        return ;
                    }
                }
                this.result = e;
                return ;
            }
            this.result = getVarExp(e.loc, this.istate, e.var, this.goal);
            if (this.exceptionOrCant(this.result))
                return ;
            if (((e.var.storage_class & 2101248L) == 0L) && ((e.type.baseElemOf().ty & 0xFF) != ENUMTY.Tstruct))
            {
                this.result = paintTypeOntoLiteral(this.pue, e.type, this.result);
            }
        }

        public  void visit(DeclarationExp e) {
            Dsymbol s = e.declaration;
            {
                VarDeclaration v = s.isVarDeclaration();
                if ((v) != null)
                {
                    {
                        TupleDeclaration td = v.toAlias().isTupleDeclaration();
                        if ((td) != null)
                        {
                            this.result = null;
                            if (td.objects == null)
                                return ;
                            {
                                Slice<RootObject> __r954 = (td.objects).opSlice().copy();
                                int __key955 = 0;
                                for (; (__key955 < __r954.getLength());__key955 += 1) {
                                    RootObject o = __r954.get(__key955);
                                    Expression ex = isExpression(o);
                                    DsymbolExp ds = ex != null ? ex.isDsymbolExp() : null;
                                    VarDeclaration v2 = ds != null ? ds.s.isVarDeclaration() : null;
                                    assert(v2 != null);
                                    if (v2.isDataseg() && !v2.isCTFE())
                                        continue;
                                    ctfeStack.push(v2);
                                    if (v2._init != null)
                                    {
                                        Expression einit = null;
                                        {
                                            ExpInitializer ie = v2._init.isExpInitializer();
                                            if ((ie) != null)
                                            {
                                                einit = interpret(ie.exp, this.istate, this.goal);
                                                if (this.exceptionOrCant(einit))
                                                    return ;
                                            }
                                            else if (v2._init.isVoidInitializer() != null)
                                            {
                                                einit = voidInitLiteral(v2.type, v2).copy();
                                            }
                                            else
                                            {
                                                e.error(new BytePtr("declaration `%s` is not yet implemented in CTFE"), e.toChars());
                                                this.result = CTFEExp.cantexp;
                                                return ;
                                            }
                                        }
                                        setValue(v2, einit);
                                    }
                                }
                            }
                            return ;
                        }
                    }
                    if (v.isStatic())
                    {
                        this.result = null;
                        return ;
                    }
                    if (!(v.isDataseg() || ((v.storage_class & 8388608L) != 0)) || v.isCTFE())
                        ctfeStack.push(v);
                    if (v._init != null)
                    {
                        {
                            ExpInitializer ie = v._init.isExpInitializer();
                            if ((ie) != null)
                            {
                                this.result = interpret(ie.exp, this.istate, this.goal);
                            }
                            else if (v._init.isVoidInitializer() != null)
                            {
                                this.result = voidInitLiteral(v.type, v).copy();
                                setValue(v, this.result);
                            }
                            else
                            {
                                e.error(new BytePtr("declaration `%s` is not yet implemented in CTFE"), e.toChars());
                                this.result = CTFEExp.cantexp;
                            }
                        }
                    }
                    else if ((v.type.size() == 0L))
                    {
                        this.result = v.type.defaultInitLiteral(e.loc);
                    }
                    else
                    {
                        e.error(new BytePtr("variable `%s` cannot be modified at compile time"), v.toChars());
                        this.result = CTFEExp.cantexp;
                    }
                    return ;
                }
            }
            if ((s.isAttribDeclaration() != null) || (s.isTemplateMixin() != null) || (s.isTupleDeclaration() != null))
            {
                AttribDeclaration ad = e.declaration.isAttribDeclaration();
                if ((ad != null) && (ad.decl != null) && ((ad.decl).length == 1))
                {
                    Dsymbol sparent = (ad.decl).get(0);
                    if ((sparent.isAggregateDeclaration() != null) || (sparent.isTemplateDeclaration() != null) || (sparent.isAliasDeclaration() != null))
                    {
                        this.result = null;
                        return ;
                    }
                }
                e.error(new BytePtr("declaration `%s` is not yet implemented in CTFE"), e.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            this.result = null;
        }

        public  void visit(TypeidExp e) {
            {
                Type t = isType(e.obj);
                if ((t) != null)
                {
                    this.result = e;
                    return ;
                }
            }
            {
                Expression ex = isExpression(e.obj);
                if ((ex) != null)
                {
                    this.result = interpret(this.pue, ex, this.istate, CtfeGoal.ctfeNeedRvalue);
                    if (this.exceptionOrCant(ex))
                        return ;
                    if (((this.result.op & 0xFF) == 13))
                    {
                        e.error(new BytePtr("null pointer dereference evaluating typeid. `%s` is `null`"), ex.toChars());
                        this.result = CTFEExp.cantexp;
                        return ;
                    }
                    if (((this.result.op & 0xFF) != 50))
                    {
                        e.error(new BytePtr("CTFE internal error: determining classinfo"));
                        this.result = CTFEExp.cantexp;
                        return ;
                    }
                    ClassDeclaration cd = ((ClassReferenceExp)this.result).originalClass();
                    assert(cd != null);
                    (this.pue) = new UnionExp(new TypeidExp(e.loc, cd.type));
                    this.result = (this.pue).exp();
                    this.result.type = e.type;
                    return ;
                }
            }
            this.visit((Expression)e);
        }

        public  void visit(TupleExp e) {
            if (this.exceptionOrCant(interpret(e.e0, this.istate, CtfeGoal.ctfeNeedNothing)))
                return ;
            DArray<Expression> expsx = e.exps;
            {
                Slice<Expression> __r957 = (expsx).opSlice().copy();
                int __key956 = 0;
                for (; (__key956 < __r957.getLength());__key956 += 1) {
                    Expression exp = __r957.get(__key956);
                    int i = __key956;
                    Expression ex = interpret(exp, this.istate, CtfeGoal.ctfeNeedRvalue);
                    if (this.exceptionOrCant(ex))
                        return ;
                    if ((this.goal == CtfeGoal.ctfeNeedNothing))
                        continue;
                    if (((ex.op & 0xFF) == 232))
                    {
                        e.error(new BytePtr("CTFE internal error: void element `%s` in tuple"), exp.toChars());
                        throw new AssertionError("Unreachable code!");
                    }
                    if ((ex != exp))
                    {
                        expsx = copyArrayOnWrite(expsx, e.exps);
                        expsx.set(i, ex);
                    }
                }
            }
            if ((expsx != e.exps))
            {
                expandTuples(expsx);
                (this.pue) = new UnionExp(new TupleExp(e.loc, expsx));
                this.result = (this.pue).exp();
                this.result.type = new TypeTuple(expsx);
            }
            else
                this.result = e;
        }

        public  void visit(ArrayLiteralExp e) {
            if (((e.ownedByCtfe & 0xFF) >= 1))
            {
                this.result = e;
                return ;
            }
            Type tn = e.type.toBasetype().nextOf().toBasetype();
            boolean wantCopy = ((tn.ty & 0xFF) == ENUMTY.Tsarray) || ((tn.ty & 0xFF) == ENUMTY.Tstruct);
            Expression basis = interpret(e.basis, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(basis))
                return ;
            DArray<Expression> expsx = e.elements;
            int dim = expsx != null ? (expsx).length : 0;
            {
                int i = 0;
                for (; (i < dim);i++){
                    Expression exp = (expsx).get(i);
                    Expression ex = null;
                    if (exp == null)
                    {
                        ex = copyLiteral(basis).copy();
                    }
                    else
                    {
                        assert(((exp.op & 0xFF) != 62) || (!pequals(((IndexExp)exp).e1, e)));
                        ex = interpret(exp, this.istate, CtfeGoal.ctfeNeedRvalue);
                        if (this.exceptionOrCant(ex))
                            return ;
                        if (wantCopy)
                            ex = copyLiteral(ex).copy();
                    }
                    if ((ex != exp))
                    {
                        expsx = copyArrayOnWrite(expsx, e.elements);
                        expsx.set(i, ex);
                    }
                }
            }
            if ((expsx != e.elements))
            {
                expandTuples(expsx);
                if (((expsx).length != dim))
                {
                    e.error(new BytePtr("CTFE internal error: invalid array literal"));
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                (this.pue) = new UnionExp(new ArrayLiteralExp(e.loc, e.type, basis, expsx));
                ArrayLiteralExp ale = (ArrayLiteralExp)(this.pue).exp();
                ale.ownedByCtfe = OwnedBy.ctfe;
                this.result = ale;
            }
            else if (((((TypeNext)e.type).next.mod & 0xFF) & 5) != 0)
            {
                this.result = e;
            }
            else
            {
                this.pue.opAssign(copyLiteral(e));
                this.result = (this.pue).exp();
            }
        }

        public  void visit(AssocArrayLiteralExp e) {
            if (((e.ownedByCtfe & 0xFF) >= 1))
            {
                this.result = e;
                return ;
            }
            DArray<Expression> keysx = e.keys;
            DArray<Expression> valuesx = e.values;
            {
                Slice<Expression> __r959 = (keysx).opSlice().copy();
                int __key958 = 0;
                for (; (__key958 < __r959.getLength());__key958 += 1) {
                    Expression ekey = __r959.get(__key958);
                    int i = __key958;
                    Expression evalue = (valuesx).get(i);
                    Expression ek = interpret(ekey, this.istate, CtfeGoal.ctfeNeedRvalue);
                    if (this.exceptionOrCant(ek))
                        return ;
                    Expression ev = interpret(evalue, this.istate, CtfeGoal.ctfeNeedRvalue);
                    if (this.exceptionOrCant(ev))
                        return ;
                    if ((ek != ekey) || (ev != evalue))
                    {
                        keysx = copyArrayOnWrite(keysx, e.keys);
                        valuesx = copyArrayOnWrite(valuesx, e.values);
                        keysx.set(i, ek);
                        valuesx.set(i, ev);
                    }
                }
            }
            if ((keysx != e.keys))
                expandTuples(keysx);
            if ((valuesx != e.values))
                expandTuples(valuesx);
            if (((keysx).length != (valuesx).length))
            {
                e.error(new BytePtr("CTFE internal error: invalid AA"));
                this.result = CTFEExp.cantexp;
                return ;
            }
            {
                int i = 1;
                for (; (i < (keysx).length);i++){
                    Expression ekey = (keysx).get(i - 1);
                    {
                        int j = i;
                        for (; (j < (keysx).length);j++){
                            Expression ekey2 = (keysx).get(j);
                            if (ctfeEqual(e.loc, TOK.equal, ekey, ekey2) == 0)
                                continue;
                            keysx = copyArrayOnWrite(keysx, e.keys);
                            valuesx = copyArrayOnWrite(valuesx, e.values);
                            (keysx).remove(i - 1);
                            (valuesx).remove(i - 1);
                            i -= 1;
                            break;
                        }
                    }
                }
            }
            if ((keysx != e.keys) || (valuesx != e.values))
            {
                assert((keysx != e.keys) && (valuesx != e.values));
                AssocArrayLiteralExp aae = new AssocArrayLiteralExp(e.loc, keysx, valuesx);
                aae.type = e.type;
                aae.ownedByCtfe = OwnedBy.ctfe;
                this.result = aae;
            }
            else
            {
                this.pue.opAssign(copyLiteral(e));
                this.result = (this.pue).exp();
            }
        }

        public  void visit(StructLiteralExp e) {
            if (((e.ownedByCtfe & 0xFF) >= 1))
            {
                this.result = e;
                return ;
            }
            int dim = e.elements != null ? (e.elements).length : 0;
            DArray<Expression> expsx = e.elements;
            if ((dim != e.sd.fields.length))
            {
                int nvthis = e.sd.fields.length - e.sd.nonHiddenFields();
                assert((e.sd.fields.length - dim == nvthis));
                {
                    int __key960 = 0;
                    int __limit961 = nvthis;
                    for (; (__key960 < __limit961);__key960 += 1) {
                        int i = __key960;
                        NullExp ne = new NullExp(e.loc, null);
                        VarDeclaration vthis = (i == 0) ? e.sd.vthis : e.sd.vthis2;
                        ne.type = vthis.type;
                        expsx = copyArrayOnWrite(expsx, e.elements);
                        (expsx).push(ne);
                        dim += 1;
                    }
                }
            }
            assert((dim == e.sd.fields.length));
            {
                int __key962 = 0;
                int __limit963 = dim;
                for (; (__key962 < __limit963);__key962 += 1) {
                    int i = __key962;
                    VarDeclaration v = e.sd.fields.get(i);
                    Expression exp = (expsx).get(i);
                    Expression ex = null;
                    if (exp == null)
                    {
                        ex = voidInitLiteral(v.type, v).copy();
                    }
                    else
                    {
                        ex = interpret(exp, this.istate, CtfeGoal.ctfeNeedRvalue);
                        if (this.exceptionOrCant(ex))
                            return ;
                        if (((v.type.ty & 0xFF) != (ex.type.ty & 0xFF)) && ((v.type.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            TypeSArray tsa = (TypeSArray)v.type;
                            int len = (int)tsa.dim.toInteger();
                            UnionExp ue = null;
                            ex = createBlockDuplicatedArrayLiteral(ue, ex.loc, v.type, ex, len);
                            if ((pequals(ex, ue.exp())))
                                ex = ue.copy();
                        }
                    }
                    if ((ex != exp))
                    {
                        expsx = copyArrayOnWrite(expsx, e.elements);
                        expsx.set(i, ex);
                    }
                }
            }
            if ((expsx != e.elements))
            {
                expandTuples(expsx);
                if (((expsx).length != e.sd.fields.length))
                {
                    e.error(new BytePtr("CTFE internal error: invalid struct literal"));
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                (this.pue) = new UnionExp(new StructLiteralExp(e.loc, e.sd, expsx));
                StructLiteralExp sle = (StructLiteralExp)(this.pue).exp();
                sle.type = e.type;
                sle.ownedByCtfe = OwnedBy.ctfe;
                sle.origin = e.origin;
                this.result = sle;
            }
            else
            {
                this.pue.opAssign(copyLiteral(e));
                this.result = (this.pue).exp();
            }
        }

        public static Expression recursivelyCreateArrayLiteral(UnionExp pue, Loc loc, Type newtype, InterState istate, DArray<Expression> arguments, int argnum) {
            Expression lenExpr = interpret(pue, (arguments).get(argnum), istate, CtfeGoal.ctfeNeedRvalue);
            if (exceptionOrCantInterpret(lenExpr))
                return lenExpr;
            int len = (int)lenExpr.toInteger();
            Type elemType = ((TypeArray)newtype).next;
            if (((elemType.ty & 0xFF) == ENUMTY.Tarray) && (argnum < (arguments).length - 1))
            {
                Expression elem = recursivelyCreateArrayLiteral(pue, loc, elemType, istate, arguments, argnum + 1);
                if (exceptionOrCantInterpret(elem))
                    return elem;
                DArray<Expression> elements = new DArray<Expression>(len);
                {
                    Slice<Expression> __r964 = (elements).opSlice().copy();
                    int __key965 = 0;
                    for (; (__key965 < __r964.getLength());__key965 += 1) {
                        Expression element = __r964.get(__key965);
                        element = copyLiteral(elem).copy();
                    }
                }
                (pue) = new UnionExp(new ArrayLiteralExp(loc, newtype, elements));
                ArrayLiteralExp ae = (ArrayLiteralExp)(pue).exp();
                ae.ownedByCtfe = OwnedBy.ctfe;
                return ae;
            }
            assert((argnum == (arguments).length - 1));
            if (((elemType.ty & 0xFF) == ENUMTY.Tchar) || ((elemType.ty & 0xFF) == ENUMTY.Twchar) || ((elemType.ty & 0xFF) == ENUMTY.Tdchar))
            {
                int ch = (int)elemType.defaultInitLiteral(loc).toInteger();
                byte sz = (byte)elemType.size();
                return createBlockDuplicatedStringLiteral(pue, loc, newtype, ch, len, sz);
            }
            else
            {
                Expression el = interpret(elemType.defaultInitLiteral(loc), istate, CtfeGoal.ctfeNeedRvalue);
                return createBlockDuplicatedArrayLiteral(pue, loc, newtype, el, len);
            }
        }

        public  void visit(NewExp e) {
            if (e.allocator != null)
            {
                e.error(new BytePtr("member allocators not supported by CTFE"));
                this.result = CTFEExp.cantexp;
                return ;
            }
            Expression epre = interpret(this.pue, e.argprefix, this.istate, CtfeGoal.ctfeNeedNothing);
            if (this.exceptionOrCant(epre))
                return ;
            if (((e.newtype.ty & 0xFF) == ENUMTY.Tarray) && (e.arguments != null))
            {
                this.result = recursivelyCreateArrayLiteral(this.pue, e.loc, e.newtype, this.istate, e.arguments, 0);
                return ;
            }
            {
                TypeStruct ts = e.newtype.toBasetype().isTypeStruct();
                if ((ts) != null)
                {
                    if (e.member != null)
                    {
                        Expression se = e.newtype.defaultInitLiteral(e.loc);
                        se = interpret(se, this.istate, CtfeGoal.ctfeNeedRvalue);
                        if (this.exceptionOrCant(se))
                            return ;
                        this.result = interpretFunction(this.pue, e.member, this.istate, e.arguments, se);
                        this.result.loc = e.loc.copy();
                    }
                    else
                    {
                        StructDeclaration sd = ts.sym;
                        DArray<Expression> exps = new DArray<Expression>();
                        (exps).reserve(sd.fields.length);
                        if (e.arguments != null)
                        {
                            (exps).setDim((e.arguments).length);
                            {
                                Slice<Expression> __r967 = (e.arguments).opSlice().copy();
                                int __key966 = 0;
                                for (; (__key966 < __r967.getLength());__key966 += 1) {
                                    Expression ex = __r967.get(__key966);
                                    int i = __key966;
                                    ex = interpret(ex, this.istate, CtfeGoal.ctfeNeedRvalue);
                                    if (this.exceptionOrCant(ex))
                                        return ;
                                    exps.set(i, ex);
                                }
                            }
                        }
                        sd.fill(e.loc, exps, false);
                        StructLiteralExp se = new StructLiteralExp(e.loc, sd, exps, e.newtype);
                        se.type = e.newtype;
                        se.ownedByCtfe = OwnedBy.ctfe;
                        this.result = interpret(this.pue, (Expression)se, this.istate, CtfeGoal.ctfeNeedRvalue);
                    }
                    if (this.exceptionOrCant(this.result))
                        return ;
                    Expression ev = (pequals(this.result, (this.pue).exp())) ? (this.pue).copy() : this.result;
                    (this.pue) = new UnionExp(new AddrExp(e.loc, ev, e.type));
                    this.result = (this.pue).exp();
                    return ;
                }
            }
            {
                TypeClass tc = e.newtype.toBasetype().isTypeClass();
                if ((tc) != null)
                {
                    ClassDeclaration cd = tc.sym;
                    int totalFieldCount = 0;
                    {
                        ClassDeclaration c = cd;
                        for (; c != null;c = c.baseClass) {
                            totalFieldCount += c.fields.length;
                        }
                    }
                    DArray<Expression> elems = new DArray<Expression>(totalFieldCount);
                    int fieldsSoFar = totalFieldCount;
                    {
                        ClassDeclaration c = cd;
                        for (; c != null;c = c.baseClass){
                            fieldsSoFar -= c.fields.length;
                            {
                                Slice<VarDeclaration> __r969 = c.fields.opSlice().copy();
                                int __key968 = 0;
                                for (; (__key968 < __r969.getLength());__key968 += 1) {
                                    VarDeclaration v = __r969.get(__key968);
                                    int i = __key968;
                                    if (v.inuse != 0)
                                    {
                                        e.error(new BytePtr("circular reference to `%s`"), v.toPrettyChars(false));
                                        this.result = CTFEExp.cantexp;
                                        return ;
                                    }
                                    Expression m = null;
                                    if (v._init != null)
                                    {
                                        if (v._init.isVoidInitializer() != null)
                                            m = voidInitLiteral(v.type, v).copy();
                                        else
                                            m = v.getConstInitializer(true);
                                    }
                                    else
                                        m = v.type.defaultInitLiteral(e.loc);
                                    if (this.exceptionOrCant(m))
                                        return ;
                                    elems.set(fieldsSoFar + i, copyLiteral(m).copy());
                                }
                            }
                        }
                    }
                    StructLiteralExp se = new StructLiteralExp(e.loc, (StructDeclaration)cd, elems, e.newtype);
                    se.ownedByCtfe = OwnedBy.ctfe;
                    (this.pue) = new UnionExp(new ClassReferenceExp(e.loc, se, e.type));
                    Expression eref = (this.pue).exp();
                    if (e.member != null)
                    {
                        if (e.member.fbody == null)
                        {
                            Expression ctorfail = evaluateIfBuiltin(this.pue, this.istate, e.loc, e.member, e.arguments, eref);
                            if (ctorfail != null)
                            {
                                if (this.exceptionOrCant(ctorfail))
                                    return ;
                                this.result = eref;
                                return ;
                            }
                            e.member.error(new BytePtr("`%s` cannot be constructed at compile time, because the constructor has no available source code"), e.newtype.toChars());
                            this.result = CTFEExp.cantexp;
                            return ;
                        }
                        UnionExp ue = null;
                        Expression ctorfail = interpretFunction(ue, e.member, this.istate, e.arguments, eref);
                        if (this.exceptionOrCant(ctorfail))
                            return ;
                        eref.loc = e.loc.copy();
                    }
                    this.result = eref;
                    return ;
                }
            }
            if (e.newtype.toBasetype().isscalar())
            {
                Expression newval = null;
                if ((e.arguments != null) && ((e.arguments).length != 0))
                    newval = (e.arguments).get(0);
                else
                    newval = e.newtype.defaultInitLiteral(e.loc);
                newval = interpret(newval, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(newval))
                    return ;
                DArray<Expression> elements = new DArray<Expression>(1);
                elements.set(0, newval);
                ArrayLiteralExp ae = new ArrayLiteralExp(e.loc, e.newtype.arrayOf(), elements);
                ae.ownedByCtfe = OwnedBy.ctfe;
                IndexExp ei = new IndexExp(e.loc, ae, new IntegerExp(Loc.initial, 0L, Type.tsize_t));
                ei.type = e.newtype;
                (this.pue) = new UnionExp(new AddrExp(e.loc, ei, e.type));
                this.result = (this.pue).exp();
                return ;
            }
            e.error(new BytePtr("cannot interpret `%s` at compile time"), e.toChars());
            this.result = CTFEExp.cantexp;
        }

        public  void visit(UnaExp e) {
            UnionExp ue = null;
            Expression e1 = interpret(ue, e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e1))
                return ;
            switch ((e.op & 0xFF))
            {
                case 8:
                    this.pue.opAssign(Neg(e.type, e1));
                    break;
                case 92:
                    this.pue.opAssign(Com(e.type, e1));
                    break;
                case 91:
                    this.pue.opAssign(Not(e.type, e1));
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            this.result = (this.pue).exp();
        }

        public  void visit(DotTypeExp e) {
            UnionExp ue = null;
            Expression e1 = interpret(ue, e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e1))
                return ;
            if ((pequals(e1, e.e1)))
                this.result = e;
            else
            {
                DotTypeExp edt = (DotTypeExp)e.copy();
                edt.e1 = (pequals(e1, ue.exp())) ? e1.copy() : e1;
                this.result = edt;
            }
        }

        public  void interpretCommon(BinExp e, Function4<Loc,Type,Expression,Expression,UnionExp> fp) {
            Ref<BinExp> e_ref = ref(e);
            if (((e_ref.value.e1.type.ty & 0xFF) == ENUMTY.Tpointer) && ((e_ref.value.e2.type.ty & 0xFF) == ENUMTY.Tpointer) && ((e_ref.value.op & 0xFF) == 75))
            {
                UnionExp ue1 = null;
                Expression e1 = interpret(ue1, e_ref.value.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e1))
                    return ;
                UnionExp ue2 = null;
                Expression e2 = interpret(ue2, e_ref.value.e2, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e2))
                    return ;
                this.pue.opAssign(pointerDifference(e_ref.value.loc, e_ref.value.type, e1, e2));
                this.result = (this.pue).exp();
                return ;
            }
            if (((e_ref.value.e1.type.ty & 0xFF) == ENUMTY.Tpointer) && e_ref.value.e2.type.isintegral())
            {
                UnionExp ue1 = null;
                Expression e1 = interpret(ue1, e_ref.value.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e1))
                    return ;
                UnionExp ue2 = null;
                Expression e2 = interpret(ue2, e_ref.value.e2, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e2))
                    return ;
                this.pue.opAssign(pointerArithmetic(e_ref.value.loc, e_ref.value.op, e_ref.value.type, e1, e2));
                this.result = (this.pue).exp();
                return ;
            }
            if (((e_ref.value.e2.type.ty & 0xFF) == ENUMTY.Tpointer) && e_ref.value.e1.type.isintegral() && ((e_ref.value.op & 0xFF) == 74))
            {
                UnionExp ue1 = null;
                Expression e1 = interpret(ue1, e_ref.value.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e1))
                    return ;
                UnionExp ue2 = null;
                Expression e2 = interpret(ue2, e_ref.value.e2, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e2))
                    return ;
                this.pue.opAssign(pointerArithmetic(e_ref.value.loc, e_ref.value.op, e_ref.value.type, e2, e1));
                this.result = (this.pue).exp();
                return ;
            }
            if (((e_ref.value.e1.type.ty & 0xFF) == ENUMTY.Tpointer) || ((e_ref.value.e2.type.ty & 0xFF) == ENUMTY.Tpointer))
            {
                e_ref.value.error(new BytePtr("pointer expression `%s` cannot be interpreted at compile time"), e_ref.value.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            Function3<UnionExp,Expression,Expression,Boolean> evalOperand = new Function3<UnionExp,Expression,Expression,Boolean>(){
                public Boolean invoke(UnionExp pue, Expression ex, Ref<Expression> er){
                    er.value = null;
                    er.value = interpret(pue, ex, istate, CtfeGoal.ctfeNeedRvalue);
                    if (exceptionOrCant(er.value))
                        return false;
                    if ((er.value.isConst() != 1))
                    {
                        if (((er.value.op & 0xFF) == 47))
                            e_ref.value.error(new BytePtr("cannot interpret array literal expression `%s` at compile time"), e_ref.value.toChars());
                        else
                            e_ref.value.error(new BytePtr("CTFE internal error: non-constant value `%s`"), ex.toChars());
                        result = CTFEExp.cantexp;
                        return false;
                    }
                    return true;
                }
            };
            UnionExp ue1 = null;
            Ref<Expression> e1 = ref(null);
            if (!evalOperand.invoke(ue1, e_ref.value.e1, e1))
                return ;
            UnionExp ue2 = null;
            Ref<Expression> e2 = ref(null);
            if (!evalOperand.invoke(ue2, e_ref.value.e2, e2))
                return ;
            if (((e_ref.value.op & 0xFF) == 65) || ((e_ref.value.op & 0xFF) == 64) || ((e_ref.value.op & 0xFF) == 68))
            {
                long i2 = (long)e2.value.toInteger();
                long sz = e1.value.type.size() * 8L;
                if ((i2 < 0L) || ((long)i2 >= sz))
                {
                    e_ref.value.error(new BytePtr("shift by %lld is outside the range 0..%llu"), i2, sz - 1L);
                    this.result = CTFEExp.cantexp;
                    return ;
                }
            }
            this.pue.opAssign((fp).invoke(e_ref.value.loc, e_ref.value.type, e1.value, e2.value));
            this.result = (this.pue).exp();
            if (CTFEExp.isCantExp(this.result))
                e_ref.value.error(new BytePtr("`%s` cannot be interpreted at compile time"), e_ref.value.toChars());
        }

        public  void interpretCompareCommon(BinExp e, Function4<Loc,Byte,Expression,Expression,Integer> fp) {
            UnionExp ue1 = null;
            UnionExp ue2 = null;
            if (((e.e1.type.ty & 0xFF) == ENUMTY.Tpointer) && ((e.e2.type.ty & 0xFF) == ENUMTY.Tpointer))
            {
                Expression e1 = interpret(ue1, e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e1))
                    return ;
                Expression e2 = interpret(ue2, e.e2, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e2))
                    return ;
                Ref<Long> ofs1 = ref(0L);
                Ref<Long> ofs2 = ref(0L);
                Expression agg1 = getAggregateFromPointer(e1, ptr(ofs1));
                Expression agg2 = getAggregateFromPointer(e2, ptr(ofs2));
                int cmp = comparePointers(e.op, agg1, ofs1.value, agg2, ofs2.value);
                if ((cmp == -1))
                {
                    byte dir = ((e.op & 0xFF) == 55) || ((e.op & 0xFF) == 57) ? (byte)60 : (byte)62;
                    e.error(new BytePtr("the ordering of pointers to unrelated memory blocks is indeterminate in CTFE. To check if they point to the same memory block, use both `>` and `<` inside `&&` or `||`, eg `%s && %s %c= %s + 1`"), e.toChars(), e.e1.toChars(), (dir & 0xFF), e.e2.toChars());
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                (this.pue) = new UnionExp(new IntegerExp(e.loc, cmp, e.type));
                this.result = (this.pue).exp();
                return ;
            }
            Expression e1 = interpret(ue1, e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e1))
                return ;
            if (!isCtfeComparable(e1))
            {
                e.error(new BytePtr("cannot compare `%s` at compile time"), e1.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            Expression e2 = interpret(ue2, e.e2, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e2))
                return ;
            if (!isCtfeComparable(e2))
            {
                e.error(new BytePtr("cannot compare `%s` at compile time"), e2.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            int cmp = (fp).invoke(e.loc, e.op, e1, e2);
            (this.pue) = new UnionExp(new IntegerExp(e.loc, cmp, e.type));
            this.result = (this.pue).exp();
        }

        public  void visit(BinExp e) {
            switch ((e.op & 0xFF))
            {
                case 74:
                    this.interpretCommon(e, dinterpret::Add);
                    return ;
                case 75:
                    this.interpretCommon(e, dinterpret::Min);
                    return ;
                case 78:
                    this.interpretCommon(e, dinterpret::Mul);
                    return ;
                case 79:
                    this.interpretCommon(e, dinterpret::Div);
                    return ;
                case 80:
                    this.interpretCommon(e, dinterpret::Mod);
                    return ;
                case 64:
                    this.interpretCommon(e, dinterpret::Shl);
                    return ;
                case 65:
                    this.interpretCommon(e, dinterpret::Shr);
                    return ;
                case 68:
                    this.interpretCommon(e, dinterpret::Ushr);
                    return ;
                case 84:
                    this.interpretCommon(e, dinterpret::And);
                    return ;
                case 85:
                    this.interpretCommon(e, dinterpret::Or);
                    return ;
                case 86:
                    this.interpretCommon(e, dinterpret::Xor);
                    return ;
                case 226:
                    this.interpretCommon(e, dinterpret::Pow);
                    return ;
                case 58:
                case 59:
                    this.interpretCompareCommon(e, dinterpret::ctfeEqual);
                    return ;
                case 60:
                case 61:
                    this.interpretCompareCommon(e, dinterpret::ctfeIdentity);
                    return ;
                case 54:
                case 56:
                case 55:
                case 57:
                    this.interpretCompareCommon(e, dinterpret::ctfeCmp);
                    return ;
                default:
                printf(new BytePtr("be = '%s' %s at [%s]\n"), Token.toChars(e.op), e.toChars(), e.loc.toChars(global.params.showColumns));
                throw new AssertionError("Unreachable code!");
            }
        }

        public static VarDeclaration findParentVar(Expression e) {
            for (; ;){
                {
                    VarExp ve = e.isVarExp();
                    if ((ve) != null)
                    {
                        VarDeclaration v = ve.var.isVarDeclaration();
                        assert(v != null);
                        return v;
                    }
                }
                {
                    IndexExp ie = e.isIndexExp();
                    if ((ie) != null)
                        e = ie.e1;
                    else {
                        DotVarExp dve = e.isDotVarExp();
                        if ((dve) != null)
                            e = dve.e1;
                        else {
                            DotTemplateInstanceExp dtie = e.isDotTemplateInstanceExp();
                            if ((dtie) != null)
                                e = dtie.e1;
                            else {
                                SliceExp se = e.isSliceExp();
                                if ((se) != null)
                                    e = se.e1;
                                else
                                    return null;
                            }
                        }
                    }
                }
            }
        }

        public  void interpretAssignCommon(BinExp e, Function4<Loc,Type,Expression,Expression,UnionExp> fp, int post) {
            this.result = CTFEExp.cantexp;
            Expression e1 = e.e1;
            if (this.istate == null)
            {
                e.error(new BytePtr("value of `%s` is not known at compile time"), e1.toChars());
                return ;
            }
            CtfeStatus.numAssignments += 1;
            boolean isBlockAssignment = false;
            if (((e1.op & 0xFF) == 31))
            {
                Type tdst = e1.type.toBasetype();
                Type tsrc = e.e2.type.toBasetype();
                for (; ((tdst.ty & 0xFF) == ENUMTY.Tsarray) || ((tdst.ty & 0xFF) == ENUMTY.Tarray);){
                    tdst = ((TypeArray)tdst).next.toBasetype();
                    if (tsrc.equivalent(tdst))
                    {
                        isBlockAssignment = true;
                        break;
                    }
                }
            }
            if (((e.op & 0xFF) == 95) || ((e.op & 0xFF) == 96) && ((((AssignExp)e).memset & MemorySet.referenceInit) != 0))
            {
                assert(fp == null);
                Expression newval = interpret(e.e2, this.istate, CtfeGoal.ctfeNeedLvalue);
                if (this.exceptionOrCant(newval))
                    return ;
                VarDeclaration v = ((VarExp)e1).var.isVarDeclaration();
                setValue(v, newval);
                if ((this.goal == CtfeGoal.ctfeNeedRvalue))
                    this.result = interpret(newval, this.istate, CtfeGoal.ctfeNeedRvalue);
                else
                    this.result = e1;
                return ;
            }
            if (fp != null)
            {
                for (; ((e1.op & 0xFF) == 12);){
                    CastExp ce = (CastExp)e1;
                    e1 = ce.e1;
                }
            }
            AssocArrayLiteralExp existingAA = null;
            Expression lastIndex = null;
            Expression oldval = null;
            if (((e1.op & 0xFF) == 62) && ((((IndexExp)e1).e1.type.toBasetype().ty & 0xFF) == ENUMTY.Taarray))
            {
                IndexExp ie = (IndexExp)e1;
                int depth = 0;
                for (; ((ie.e1.op & 0xFF) == 62) && ((((IndexExp)ie.e1).e1.type.toBasetype().ty & 0xFF) == ENUMTY.Taarray);){
                    assert(ie.modifiable);
                    ie = (IndexExp)ie.e1;
                    depth += 1;
                }
                Expression aggregate = interpret(ie.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(aggregate))
                    return ;
                if (((existingAA = aggregate.isAssocArrayLiteralExp()) != null))
                {
                    lastIndex = interpret(((IndexExp)e1).e2, this.istate, CtfeGoal.ctfeNeedRvalue);
                    lastIndex = resolveSlice(lastIndex, null);
                    if (this.exceptionOrCant(lastIndex))
                        return ;
                    for (; (depth > 0);){
                        IndexExp xe = (IndexExp)e1;
                        {
                            int __key970 = 0;
                            int __limit971 = depth;
                            for (; (__key970 < __limit971);__key970 += 1) {
                                int d = __key970;
                                xe = (IndexExp)xe.e1;
                            }
                        }
                        Expression ekey = interpret(xe.e2, this.istate, CtfeGoal.ctfeNeedRvalue);
                        if (this.exceptionOrCant(ekey))
                            return ;
                        UnionExp ekeyTmp = null;
                        ekey = resolveSlice(ekey, ekeyTmp);
                        AssocArrayLiteralExp newAA = (AssocArrayLiteralExp)findKeyInAA(e.loc, existingAA, ekey);
                        if (this.exceptionOrCant(newAA))
                            return ;
                        if (newAA == null)
                        {
                            DArray<Expression> keysx = new DArray<Expression>();
                            DArray<Expression> valuesx = new DArray<Expression>();
                            newAA = new AssocArrayLiteralExp(e.loc, keysx, valuesx);
                            newAA.type = xe.type;
                            newAA.ownedByCtfe = OwnedBy.ctfe;
                            (existingAA.keys).push(ekey);
                            (existingAA.values).push(newAA);
                        }
                        existingAA = newAA;
                        depth -= 1;
                    }
                    if (fp != null)
                    {
                        oldval = findKeyInAA(e.loc, existingAA, lastIndex);
                        if (oldval == null)
                            oldval = copyLiteral(e.e1.type.defaultInitLiteral(e.loc)).copy();
                    }
                }
                else
                {
                    oldval = copyLiteral(e.e1.type.defaultInitLiteral(e.loc)).copy();
                    Expression newaae = oldval;
                    for (; ((e1.op & 0xFF) == 62) && ((((IndexExp)e1).e1.type.toBasetype().ty & 0xFF) == ENUMTY.Taarray);){
                        Expression ekey = interpret(((IndexExp)e1).e2, this.istate, CtfeGoal.ctfeNeedRvalue);
                        if (this.exceptionOrCant(ekey))
                            return ;
                        ekey = resolveSlice(ekey, null);
                        DArray<Expression> keysx = new DArray<Expression>();
                        DArray<Expression> valuesx = new DArray<Expression>();
                        (keysx).push(ekey);
                        (valuesx).push(newaae);
                        AssocArrayLiteralExp aae = new AssocArrayLiteralExp(e.loc, keysx, valuesx);
                        aae.type = ((IndexExp)e1).e1.type;
                        aae.ownedByCtfe = OwnedBy.ctfe;
                        if (existingAA == null)
                        {
                            existingAA = aae;
                            lastIndex = ekey;
                        }
                        newaae = aae;
                        e1 = ((IndexExp)e1).e1;
                    }
                    e1 = interpret(e1, this.istate, CtfeGoal.ctfeNeedLvalue);
                    if (this.exceptionOrCant(e1))
                        return ;
                    e1 = this.assignToLvalue(e, e1, newaae);
                    if (this.exceptionOrCant(e1))
                        return ;
                }
                assert((existingAA != null) && (lastIndex != null));
                e1 = null;
            }
            else if (((e1.op & 0xFF) == 32))
            {
                oldval = interpret(e1, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(oldval))
                    return ;
            }
            else if (((e.op & 0xFF) == 95) || ((e.op & 0xFF) == 96))
            {
                VarDeclaration ultimateVar = findParentVar(e1);
                {
                    VarExp ve = e1.isVarExp();
                    if ((ve) != null)
                    {
                        VarDeclaration v = ve.var.isVarDeclaration();
                        assert(v != null);
                        if ((v.storage_class & 4096L) != 0)
                            /*goto L1*//*unrolled goto*/
                    }
                    else if ((ultimateVar != null) && (getValue(ultimateVar) == null))
                    {
                        Expression ex = interpret(ultimateVar.type.defaultInitLiteral(e.loc), this.istate, CtfeGoal.ctfeNeedRvalue);
                        if (this.exceptionOrCant(ex))
                            return ;
                        setValue(ultimateVar, ex);
                    }
                    else
                        /*goto L1*//*unrolled goto*/
                }
            }
            else
            {
            /*L1:*/
                e1 = interpret(e1, this.istate, CtfeGoal.ctfeNeedLvalue);
                if (this.exceptionOrCant(e1))
                    return ;
                if (((e1.op & 0xFF) == 62) && ((((IndexExp)e1).e1.type.toBasetype().ty & 0xFF) == ENUMTY.Taarray))
                {
                    IndexExp ie = (IndexExp)e1;
                    assert(((ie.e1.op & 0xFF) == 48));
                    existingAA = (AssocArrayLiteralExp)ie.e1;
                    lastIndex = ie.e2;
                }
            }
            Expression newval = interpret(e.e2, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(newval))
                return ;
            if (((e.op & 0xFF) == 96) && ((newval.op & 0xFF) == 135))
            {
                Type tbn = e.type.baseElemOf();
                if (((tbn.ty & 0xFF) == ENUMTY.Tstruct))
                {
                    newval = e.type.defaultInitLiteral(e.loc);
                    if (((newval.op & 0xFF) == 127))
                    {
                        this.result = CTFEExp.cantexp;
                        return ;
                    }
                    newval = interpret(newval, this.istate, CtfeGoal.ctfeNeedRvalue);
                    if (this.exceptionOrCant(newval))
                        return ;
                }
            }
            if (fp != null)
            {
                if (oldval == null)
                {
                    oldval = interpret(e1, this.istate, CtfeGoal.ctfeNeedRvalue);
                    if (this.exceptionOrCant(oldval))
                        return ;
                }
                if (((e.e1.type.ty & 0xFF) != ENUMTY.Tpointer))
                {
                    if (((e.op & 0xFF) == 71) || ((e.op & 0xFF) == 72) || ((e.op & 0xFF) == 73))
                    {
                        if (((newval.type.ty & 0xFF) != ENUMTY.Tarray))
                        {
                            newval = copyLiteral(newval).copy();
                            newval.type = e.e2.type;
                        }
                        else
                        {
                            newval = paintTypeOntoLiteral(e.e2.type, newval);
                            newval = resolveSlice(newval, null);
                        }
                    }
                    oldval = resolveSlice(oldval, null);
                    newval = (fp).invoke(e.loc, e.type, oldval, newval).copy();
                }
                else if (e.e2.type.isintegral() && ((e.op & 0xFF) == 76) || ((e.op & 0xFF) == 77) || ((e.op & 0xFF) == 93) || ((e.op & 0xFF) == 94))
                {
                    newval = pointerArithmetic(e.loc, e.op, e.type, oldval, newval).copy();
                }
                else
                {
                    e.error(new BytePtr("pointer expression `%s` cannot be interpreted at compile time"), e.toChars());
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                if (this.exceptionOrCant(newval))
                {
                    if (CTFEExp.isCantExp(newval))
                        e.error(new BytePtr("cannot interpret `%s` at compile time"), e.toChars());
                    return ;
                }
            }
            if (existingAA != null)
            {
                if (((existingAA.ownedByCtfe & 0xFF) != 1))
                {
                    e.error(new BytePtr("cannot modify read-only constant `%s`"), existingAA.toChars());
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                assignAssocArrayElement(e.loc, existingAA, lastIndex, newval);
                this.result = ctfeCast(this.pue, e.loc, e.type, e.type, (fp != null) && (post != 0) ? oldval : newval);
                return ;
            }
            if (((e1.op & 0xFF) == 32))
            {
                this.result = ctfeCast(this.pue, e.loc, e.type, e.type, (fp != null) && (post != 0) ? oldval : newval);
                if (this.exceptionOrCant(this.result))
                    return ;
                if ((pequals(this.result, (this.pue).exp())))
                    this.result = (this.pue).copy();
                int oldlen = (int)oldval.toInteger();
                int newlen = (int)newval.toInteger();
                if ((oldlen == newlen))
                    return ;
                e1 = ((ArrayLengthExp)e1).e1;
                Type t = e1.type.toBasetype();
                if (((t.ty & 0xFF) != ENUMTY.Tarray))
                {
                    e.error(new BytePtr("`%s` is not yet supported at compile time"), e.toChars());
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                e1 = interpret(e1, this.istate, CtfeGoal.ctfeNeedLvalue);
                if (this.exceptionOrCant(e1))
                    return ;
                if ((oldlen != 0))
                    oldval = interpret(e1, this.istate, CtfeGoal.ctfeNeedRvalue);
                newval = changeArrayLiteralLength(e.loc, (TypeArray)t, oldval, oldlen, newlen).copy();
                e1 = this.assignToLvalue(e, e1, newval);
                if (this.exceptionOrCant(e1))
                    return ;
                return ;
            }
            if (!isBlockAssignment)
            {
                newval = ctfeCast(this.pue, e.loc, e.type, e.type, newval);
                if (this.exceptionOrCant(newval))
                    return ;
                if ((pequals(newval, (this.pue).exp())))
                    newval = (this.pue).copy();
                if ((this.goal == CtfeGoal.ctfeNeedLvalue))
                    this.result = e1;
                else
                {
                    this.result = ctfeCast(this.pue, e.loc, e.type, e.type, (fp != null) && (post != 0) ? oldval : newval);
                    if ((pequals(this.result, (this.pue).exp())))
                        this.result = (this.pue).copy();
                }
                if (this.exceptionOrCant(this.result))
                    return ;
            }
            if (this.exceptionOrCant(newval))
                return ;
            if (((e1.op & 0xFF) == 31) || ((e1.op & 0xFF) == 229) || ((e1.op & 0xFF) == 47) || ((e1.op & 0xFF) == 121) || ((e1.op & 0xFF) == 13) && ((e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tarray))
            {
                this.result = this.interpretAssignToSlice(this.pue, e, e1, newval, isBlockAssignment);
                if (this.exceptionOrCant(this.result))
                    return ;
                {
                    SliceExp se = e.e1.isSliceExp();
                    if ((se) != null)
                    {
                        Expression e1x = interpret(se.e1, this.istate, CtfeGoal.ctfeNeedLvalue);
                        {
                            DotVarExp dve = e1x.isDotVarExp();
                            if ((dve) != null)
                            {
                                Expression ex = dve.e1;
                                StructLiteralExp sle = ((ex.op & 0xFF) == 49) ? (StructLiteralExp)ex : ((ex.op & 0xFF) == 50) ? ((ClassReferenceExp)ex).value : null;
                                VarDeclaration v = dve.var.isVarDeclaration();
                                if ((sle == null) || (v == null))
                                {
                                    e.error(new BytePtr("CTFE internal error: dotvar slice assignment"));
                                    this.result = CTFEExp.cantexp;
                                    return ;
                                }
                                this.stompOverlappedFields(sle, v);
                            }
                        }
                    }
                }
                return ;
            }
            assert(this.result != null);
            {
                Expression ex = this.assignToLvalue(e, e1, newval);
                if ((ex) != null)
                    this.result = ex;
            }
            return ;
        }

        public  void stompOverlappedFields(StructLiteralExp sle, VarDeclaration v) {
            if (!v.overlapped)
                return ;
            {
                Slice<VarDeclaration> __r973 = sle.sd.fields.opSlice().copy();
                int __key972 = 0;
                for (; (__key972 < __r973.getLength());__key972 += 1) {
                    VarDeclaration v2 = __r973.get(__key972);
                    int i = __key972;
                    if ((v == v2) || !v.isOverlappedWith(v2))
                        continue;
                    Expression e = (sle.elements).get(i);
                    if (((e.op & 0xFF) != 128))
                        sle.elements.set(i, voidInitLiteral(e.type, v).copy());
                }
            }
        }

        public  Expression assignToLvalue(BinExp e, Expression e1, Expression newval) {
            VarDeclaration vd = null;
            Ptr<Expression> payload = null;
            Expression oldval = null;
            {
                VarExp ve = e1.isVarExp();
                if ((ve) != null)
                {
                    vd = ve.var.isVarDeclaration();
                    oldval = getValue(vd);
                }
                else {
                    DotVarExp dve = e1.isDotVarExp();
                    if ((dve) != null)
                    {
                        Expression ex = dve.e1;
                        StructLiteralExp sle = ((ex.op & 0xFF) == 49) ? (StructLiteralExp)ex : ((ex.op & 0xFF) == 50) ? ((ClassReferenceExp)ex).value : null;
                        VarDeclaration v = ((DotVarExp)e1).var.isVarDeclaration();
                        if ((sle == null) || (v == null))
                        {
                            e.error(new BytePtr("CTFE internal error: dotvar assignment"));
                            return CTFEExp.cantexp;
                        }
                        if (((sle.ownedByCtfe & 0xFF) != 1))
                        {
                            e.error(new BytePtr("cannot modify read-only constant `%s`"), sle.toChars());
                            return CTFEExp.cantexp;
                        }
                        int fieldi = ((ex.op & 0xFF) == 49) ? findFieldIndexByName(sle.sd, v) : ((ClassReferenceExp)ex).findFieldIndexByName(v);
                        if ((fieldi == -1))
                        {
                            e.error(new BytePtr("CTFE internal error: cannot find field `%s` in `%s`"), v.toChars(), ex.toChars());
                            return CTFEExp.cantexp;
                        }
                        assert((0 <= fieldi) && (fieldi < (sle.elements).length));
                        this.stompOverlappedFields(sle, v);
                        payload = pcopy(((sle.elements).get(fieldi)));
                        oldval = payload.get();
                    }
                    else {
                        IndexExp ie = e1.isIndexExp();
                        if ((ie) != null)
                        {
                            assert(((ie.e1.type.toBasetype().ty & 0xFF) != ENUMTY.Taarray));
                            Ref<Expression> aggregate = ref(null);
                            Ref<Long> indexToModify = ref(0L);
                            if (!resolveIndexing(ie, this.istate, ptr(aggregate), ptr(indexToModify), true))
                            {
                                return CTFEExp.cantexp;
                            }
                            int index = (int)indexToModify.value;
                            {
                                StringExp existingSE = aggregate.value.isStringExp();
                                if ((existingSE) != null)
                                {
                                    if (((existingSE.ownedByCtfe & 0xFF) != 1))
                                    {
                                        e.error(new BytePtr("cannot modify read-only string literal `%s`"), ie.e1.toChars());
                                        return CTFEExp.cantexp;
                                    }
                                    existingSE.setCodeUnit(index, (int)newval.toInteger());
                                    return null;
                                }
                            }
                            if (((aggregate.value.op & 0xFF) != 47))
                            {
                                e.error(new BytePtr("index assignment `%s` is not yet supported in CTFE "), e.toChars());
                                return CTFEExp.cantexp;
                            }
                            ArrayLiteralExp existingAE = (ArrayLiteralExp)aggregate.value;
                            if (((existingAE.ownedByCtfe & 0xFF) != 1))
                            {
                                e.error(new BytePtr("cannot modify read-only constant `%s`"), existingAE.toChars());
                                return CTFEExp.cantexp;
                            }
                            payload = pcopy(((existingAE.elements).get(index)));
                            oldval = payload.get();
                        }
                        else
                        {
                            e.error(new BytePtr("`%s` cannot be evaluated at compile time"), e.toChars());
                            return CTFEExp.cantexp;
                        }
                    }
                }
            }
            Type t1b = e1.type.toBasetype();
            boolean wantCopy = (t1b.baseElemOf().ty & 0xFF) == ENUMTY.Tstruct;
            if (((newval.op & 0xFF) == 49) && (oldval != null))
            {
                newval = copyLiteral(newval).copy();
                assignInPlace(oldval, newval);
            }
            else if (wantCopy && ((e.op & 0xFF) == 90))
            {
                assert(oldval != null);
                newval = resolveSlice(newval, null);
                if (CTFEExp.isCantExp(newval))
                {
                    e.error(new BytePtr("CTFE internal error: assignment `%s`"), e.toChars());
                    return CTFEExp.cantexp;
                }
                assert(((oldval.op & 0xFF) == 47));
                assert(((newval.op & 0xFF) == 47));
                DArray<Expression> oldelems = ((ArrayLiteralExp)oldval).elements;
                DArray<Expression> newelems = ((ArrayLiteralExp)newval).elements;
                assert(((oldelems).length == (newelems).length));
                Type elemtype = oldval.type.nextOf();
                {
                    Slice<Expression> __r975 = (oldelems).opSlice().copy();
                    int __key974 = 0;
                    for (; (__key974 < __r975.getLength());__key974 += 1) {
                        Expression oldelem = __r975.get(__key974);
                        int i = __key974;
                        Expression newelem = paintTypeOntoLiteral(elemtype, (newelems).get(i));
                        if (e.e2.isLvalue())
                        {
                            {
                                Expression ex = evaluatePostblit(this.istate, newelem);
                                if ((ex) != null)
                                    return ex;
                            }
                        }
                        {
                            Expression ex = evaluateDtor(this.istate, oldelem);
                            if ((ex) != null)
                                return ex;
                        }
                        oldelem = newelem;
                    }
                }
            }
            else
            {
                if (wantCopy)
                    newval = copyLiteral(newval).copy();
                if (((t1b.ty & 0xFF) == ENUMTY.Tsarray) && ((e.op & 0xFF) == 95) && e.e2.isLvalue())
                {
                    {
                        Expression ex = evaluatePostblit(this.istate, newval);
                        if ((ex) != null)
                            return ex;
                    }
                }
                oldval = newval;
            }
            if (vd != null)
                setValue(vd, oldval);
            else
                payload.set(0, oldval);
            if (((e.op & 0xFF) == 96))
                return oldval;
            return null;
        }

        public  Expression interpretAssignToSlice(UnionExp pue, BinExp e, Expression e1, Expression newval, boolean isBlockAssignment) {
            long lowerbound = 0L;
            long upperbound = 0L;
            long firstIndex = 0L;
            Expression aggregate = null;
            {
                SliceExp se = e1.isSliceExp();
                if ((se) != null)
                {
                    Expression oldval = interpret(se.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
                    long dollar = resolveArrayLength(oldval);
                    if (se.lengthVar != null)
                    {
                        Expression dollarExp = new IntegerExp(e1.loc, dollar, Type.tsize_t);
                        ctfeStack.push(se.lengthVar);
                        setValue(se.lengthVar, dollarExp);
                    }
                    Expression lwr = interpret(se.lwr, this.istate, CtfeGoal.ctfeNeedRvalue);
                    if (exceptionOrCantInterpret(lwr))
                    {
                        if (se.lengthVar != null)
                            ctfeStack.pop(se.lengthVar);
                        return lwr;
                    }
                    Expression upr = interpret(se.upr, this.istate, CtfeGoal.ctfeNeedRvalue);
                    if (exceptionOrCantInterpret(upr))
                    {
                        if (se.lengthVar != null)
                            ctfeStack.pop(se.lengthVar);
                        return upr;
                    }
                    if (se.lengthVar != null)
                        ctfeStack.pop(se.lengthVar);
                    long dim = dollar;
                    lowerbound = lwr != null ? lwr.toInteger() : 0L;
                    upperbound = upr != null ? upr.toInteger() : dim;
                    if ((lowerbound < 0L) || (dim < upperbound))
                    {
                        e.error(new BytePtr("array bounds `[0..%llu]` exceeded in slice `[%llu..%llu]`"), dim, lowerbound, upperbound);
                        return CTFEExp.cantexp;
                    }
                    aggregate = oldval;
                    firstIndex = lowerbound;
                    {
                        SliceExp oldse = aggregate.isSliceExp();
                        if ((oldse) != null)
                        {
                            if ((oldse.upr.toInteger() < upperbound + oldse.lwr.toInteger()))
                            {
                                e.error(new BytePtr("slice `[%llu..%llu]` exceeds array bounds `[0..%llu]`"), lowerbound, upperbound, oldse.upr.toInteger() - oldse.lwr.toInteger());
                                return CTFEExp.cantexp;
                            }
                            aggregate = oldse.e1;
                            firstIndex = lowerbound + oldse.lwr.toInteger();
                        }
                    }
                }
                else
                {
                    {
                        ArrayLiteralExp ale = e1.isArrayLiteralExp();
                        if ((ale) != null)
                        {
                            lowerbound = 0L;
                            upperbound = (long)(ale.elements).length;
                        }
                        else {
                            StringExp se = e1.isStringExp();
                            if ((se) != null)
                            {
                                lowerbound = 0L;
                                upperbound = (long)se.len;
                            }
                            else if (((e1.op & 0xFF) == 13))
                            {
                                lowerbound = 0L;
                                upperbound = 0L;
                            }
                            else
                                throw new AssertionError("Unreachable code!");
                        }
                    }
                    aggregate = e1;
                    firstIndex = lowerbound;
                }
            }
            if ((upperbound == lowerbound))
                return newval;
            if (!isBlockAssignment)
            {
                long srclen = resolveArrayLength(newval);
                if ((srclen != upperbound - lowerbound))
                {
                    e.error(new BytePtr("array length mismatch assigning `[0..%llu]` to `[%llu..%llu]`"), srclen, lowerbound, upperbound);
                    return CTFEExp.cantexp;
                }
            }
            {
                StringExp existingSE = aggregate.isStringExp();
                if ((existingSE) != null)
                {
                    if (((existingSE.ownedByCtfe & 0xFF) != 1))
                    {
                        e.error(new BytePtr("cannot modify read-only string literal `%s`"), existingSE.toChars());
                        return CTFEExp.cantexp;
                    }
                    {
                        SliceExp se = newval.isSliceExp();
                        if ((se) != null)
                        {
                            Expression aggr2 = se.e1;
                            long srclower = se.lwr.toInteger();
                            long srcupper = se.upr.toInteger();
                            if ((pequals(aggregate, aggr2)) && (lowerbound < srcupper) && (srclower < upperbound))
                            {
                                e.error(new BytePtr("overlapping slice assignment `[%llu..%llu] = [%llu..%llu]`"), lowerbound, upperbound, srclower, srcupper);
                                return CTFEExp.cantexp;
                            }
                            Expression orignewval = newval;
                            newval = resolveSlice(newval, null);
                            if (CTFEExp.isCantExp(newval))
                            {
                                e.error(new BytePtr("CTFE internal error: slice `%s`"), orignewval.toChars());
                                return CTFEExp.cantexp;
                            }
                            assert(((newval.op & 0xFF) != 31));
                        }
                    }
                    {
                        StringExp se = newval.isStringExp();
                        if ((se) != null)
                        {
                            sliceAssignStringFromString(existingSE, se, (int)firstIndex);
                            return newval;
                        }
                    }
                    {
                        ArrayLiteralExp ale = newval.isArrayLiteralExp();
                        if ((ale) != null)
                        {
                            sliceAssignStringFromArrayLiteral(existingSE, ale, (int)firstIndex);
                            return newval;
                        }
                    }
                    int value = (int)newval.toInteger();
                    {
                        long __key976 = 0L;
                        long __limit977 = upperbound - lowerbound;
                        for (; (__key976 < __limit977);__key976 += 1L) {
                            long i = __key976;
                            existingSE.setCodeUnit((int)(i + firstIndex), value);
                        }
                    }
                    if ((this.goal == CtfeGoal.ctfeNeedNothing))
                        return null;
                    SliceExp retslice = new SliceExp(e.loc, existingSE, new IntegerExp(e.loc, firstIndex, Type.tsize_t), new IntegerExp(e.loc, firstIndex + upperbound - lowerbound, Type.tsize_t));
                    retslice.type = e.type;
                    return interpret(pue, (Expression)retslice, this.istate, CtfeGoal.ctfeNeedRvalue);
                }
            }
            {
                ArrayLiteralExp existingAE = aggregate.isArrayLiteralExp();
                if ((existingAE) != null)
                {
                    if (((existingAE.ownedByCtfe & 0xFF) != 1))
                    {
                        e.error(new BytePtr("cannot modify read-only constant `%s`"), existingAE.toChars());
                        return CTFEExp.cantexp;
                    }
                    if (((newval.op & 0xFF) == 31) && !isBlockAssignment)
                    {
                        SliceExp se = (SliceExp)newval;
                        Expression aggr2 = se.e1;
                        long srclower = se.lwr.toInteger();
                        long srcupper = se.upr.toInteger();
                        boolean wantCopy = (newval.type.toBasetype().nextOf().baseElemOf().ty & 0xFF) == ENUMTY.Tstruct;
                        if (wantCopy)
                        {
                            assert(((aggr2.op & 0xFF) == 47));
                            DArray<Expression> oldelems = existingAE.elements;
                            DArray<Expression> newelems = ((ArrayLiteralExp)aggr2).elements;
                            Type elemtype = aggregate.type.nextOf();
                            boolean needsPostblit = e.e2.isLvalue();
                            if ((pequals(aggregate, aggr2)) && (srclower < lowerbound) && (lowerbound < srcupper))
                            {
                                {
                                    long i = upperbound - lowerbound;
                                    for (; (0L < i--);){
                                        Expression oldelem = (oldelems).get((int)(i + firstIndex));
                                        Expression newelem = (newelems).get((int)(i + srclower));
                                        newelem = copyLiteral(newelem).copy();
                                        newelem.type = elemtype;
                                        if (needsPostblit)
                                        {
                                            {
                                                Expression x = evaluatePostblit(this.istate, newelem);
                                                if ((x) != null)
                                                    return x;
                                            }
                                        }
                                        {
                                            Expression x = evaluateDtor(this.istate, oldelem);
                                            if ((x) != null)
                                                return x;
                                        }
                                        oldelems.set((int)(lowerbound + i), newelem);
                                    }
                                }
                            }
                            else
                            {
                                {
                                    int i = 0;
                                    for (; ((long)i < upperbound - lowerbound);i++){
                                        Expression oldelem = (oldelems).get((int)((long)i + firstIndex));
                                        Expression newelem = (newelems).get((int)((long)i + srclower));
                                        newelem = copyLiteral(newelem).copy();
                                        newelem.type = elemtype;
                                        if (needsPostblit)
                                        {
                                            {
                                                Expression x = evaluatePostblit(this.istate, newelem);
                                                if ((x) != null)
                                                    return x;
                                            }
                                        }
                                        {
                                            Expression x = evaluateDtor(this.istate, oldelem);
                                            if ((x) != null)
                                                return x;
                                        }
                                        oldelems.set((int)(lowerbound + (long)i), newelem);
                                    }
                                }
                            }
                            return newval;
                        }
                        if ((pequals(aggregate, aggr2)) && (lowerbound < srcupper) && (srclower < upperbound))
                        {
                            e.error(new BytePtr("overlapping slice assignment `[%llu..%llu] = [%llu..%llu]`"), lowerbound, upperbound, srclower, srcupper);
                            return CTFEExp.cantexp;
                        }
                        Expression orignewval = newval;
                        newval = resolveSlice(newval, null);
                        if (CTFEExp.isCantExp(newval))
                        {
                            e.error(new BytePtr("CTFE internal error: slice `%s`"), orignewval.toChars());
                            return CTFEExp.cantexp;
                        }
                        assert(((newval.op & 0xFF) != 31));
                    }
                    if (((newval.op & 0xFF) == 121) && !isBlockAssignment)
                    {
                        sliceAssignArrayLiteralFromString(existingAE, (StringExp)newval, (int)firstIndex);
                        return newval;
                    }
                    if (((newval.op & 0xFF) == 47) && !isBlockAssignment)
                    {
                        DArray<Expression> oldelems = existingAE.elements;
                        DArray<Expression> newelems = ((ArrayLiteralExp)newval).elements;
                        Type elemtype = existingAE.type.nextOf();
                        boolean needsPostblit = ((e.op & 0xFF) != 96) && e.e2.isLvalue();
                        {
                            Slice<Expression> __r979 = (newelems).opSlice().copy();
                            int __key978 = 0;
                            for (; (__key978 < __r979.getLength());__key978 += 1) {
                                Expression newelem = __r979.get(__key978);
                                int j = __key978;
                                newelem = paintTypeOntoLiteral(elemtype, newelem);
                                if (needsPostblit)
                                {
                                    Expression x = evaluatePostblit(this.istate, newelem);
                                    if (exceptionOrCantInterpret(x))
                                        return x;
                                }
                                oldelems.set((int)((long)j + firstIndex), newelem);
                            }
                        }
                        return newval;
                    }
                    Type tn = newval.type.toBasetype();
                    boolean wantRef = ((tn.ty & 0xFF) == ENUMTY.Tarray) || isAssocArray(tn) || ((tn.ty & 0xFF) == ENUMTY.Tclass);
                    boolean cow = ((newval.op & 0xFF) != 49) && ((newval.op & 0xFF) != 47) && ((newval.op & 0xFF) != 121);
                    Type tb = tn.baseElemOf();
                    StructDeclaration sd = ((tb.ty & 0xFF) == ENUMTY.Tstruct) ? ((TypeStruct)tb).sym : null;
                    RecursiveBlock rb = new RecursiveBlock(null, null, false, false, false).copy();
                    rb.istate = this.istate;
                    rb.newval = newval;
                    rb.refCopy = wantRef || cow;
                    rb.needsPostblit = (sd != null) && (sd.postblit != null) && ((e.op & 0xFF) != 96) && e.e2.isLvalue();
                    rb.needsDtor = (sd != null) && (sd.dtor != null) && ((e.op & 0xFF) == 90);
                    {
                        Expression ex = rb.assignTo(existingAE, (int)lowerbound, (int)upperbound);
                        if ((ex) != null)
                            return ex;
                    }
                    if ((this.goal == CtfeGoal.ctfeNeedNothing))
                        return null;
                    SliceExp retslice = new SliceExp(e.loc, existingAE, new IntegerExp(e.loc, firstIndex, Type.tsize_t), new IntegerExp(e.loc, firstIndex + upperbound - lowerbound, Type.tsize_t));
                    retslice.type = e.type;
                    return interpret(pue, (Expression)retslice, this.istate, CtfeGoal.ctfeNeedRvalue);
                }
            }
            e.error(new BytePtr("slice operation `%s = %s` cannot be evaluated at compile time"), e1.toChars(), newval.toChars());
            return CTFEExp.cantexp;
        }

        public  void visit(AssignExp e) {
            this.interpretAssignCommon(e, null, 0);
        }

        public  void visit(BinAssignExp e) {
            switch ((e.op & 0xFF))
            {
                case 76:
                    this.interpretAssignCommon(e, dinterpret::Add, 0);
                    return ;
                case 77:
                    this.interpretAssignCommon(e, dinterpret::Min, 0);
                    return ;
                case 71:
                case 72:
                case 73:
                    this.interpretAssignCommon(e, dinterpret::ctfeCat, 0);
                    return ;
                case 81:
                    this.interpretAssignCommon(e, dinterpret::Mul, 0);
                    return ;
                case 82:
                    this.interpretAssignCommon(e, dinterpret::Div, 0);
                    return ;
                case 83:
                    this.interpretAssignCommon(e, dinterpret::Mod, 0);
                    return ;
                case 66:
                    this.interpretAssignCommon(e, dinterpret::Shl, 0);
                    return ;
                case 67:
                    this.interpretAssignCommon(e, dinterpret::Shr, 0);
                    return ;
                case 69:
                    this.interpretAssignCommon(e, dinterpret::Ushr, 0);
                    return ;
                case 87:
                    this.interpretAssignCommon(e, dinterpret::And, 0);
                    return ;
                case 88:
                    this.interpretAssignCommon(e, dinterpret::Or, 0);
                    return ;
                case 89:
                    this.interpretAssignCommon(e, dinterpret::Xor, 0);
                    return ;
                case 227:
                    this.interpretAssignCommon(e, dinterpret::Pow, 0);
                    return ;
                default:
                throw new AssertionError("Unreachable code!");
            }
        }

        public  void visit(PostExp e) {
            if (((e.op & 0xFF) == 93))
                this.interpretAssignCommon(e, dinterpret::Add, 1);
            else
                this.interpretAssignCommon(e, dinterpret::Min, 1);
        }

        public static int isPointerCmpExp(Expression e, Ptr<Expression> p1, Ptr<Expression> p2) {
            int ret = 1;
            for (; ((e.op & 0xFF) == 91);){
                ret *= -1;
                e = ((NotExp)e).e1;
            }
            switch ((e.op & 0xFF))
            {
                case 54:
                case 56:
                    ret *= -1;
                case 55:
                case 57:
                    p1.set(0, ((BinExp)e).e1);
                    p2.set(0, ((BinExp)e).e2);
                    if (!(isPointer((p1.get()).type) && isPointer((p2.get()).type)))
                        ret = 0;
                    break;
                default:
                ret = 0;
                break;
            }
            return ret;
        }

        public  void interpretFourPointerRelation(UnionExp pue, BinExp e) {
            assert(((e.op & 0xFF) == 101) || ((e.op & 0xFF) == 102));
            Ref<Expression> p1 = ref(null);
            Ref<Expression> p2 = ref(null);
            Ref<Expression> p3 = ref(null);
            Ref<Expression> p4 = ref(null);
            int dir1 = isPointerCmpExp(e.e1, ptr(p1), ptr(p2));
            int dir2 = isPointerCmpExp(e.e2, ptr(p3), ptr(p4));
            if ((dir1 == 0) || (dir2 == 0))
            {
                this.result = null;
                return ;
            }
            UnionExp ue1 = null;
            UnionExp ue2 = null;
            UnionExp ue3 = null;
            UnionExp ue4 = null;
            p1.value = interpret(ue1, p1.value, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(p1.value))
                return ;
            p2.value = interpret(ue2, p2.value, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(p2.value))
                return ;
            Ref<Long> ofs1 = ref(0L);
            Ref<Long> ofs2 = ref(0L);
            Expression agg1 = getAggregateFromPointer(p1.value, ptr(ofs1));
            Expression agg2 = getAggregateFromPointer(p2.value, ptr(ofs2));
            if (!pointToSameMemoryBlock(agg1, agg2) && ((agg1.op & 0xFF) != 13) && ((agg2.op & 0xFF) != 13))
            {
                p3.value = interpret(ue3, p3.value, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (CTFEExp.isCantExp(p3.value))
                    return ;
                Expression except = null;
                if (exceptionOrCantInterpret(p3.value))
                    except = p3.value;
                else
                {
                    p4.value = interpret(ue4, p4.value, this.istate, CtfeGoal.ctfeNeedRvalue);
                    if (CTFEExp.isCantExp(p4.value))
                    {
                        this.result = p4.value;
                        return ;
                    }
                    if (exceptionOrCantInterpret(p4.value))
                        except = p4.value;
                }
                if (except != null)
                {
                    e.error(new BytePtr("comparison `%s` of pointers to unrelated memory blocks remains indeterminate at compile time because exception `%s` was thrown while evaluating `%s`"), e.e1.toChars(), except.toChars(), e.e2.toChars());
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                Ref<Long> ofs3 = ref(0L);
                Ref<Long> ofs4 = ref(0L);
                Expression agg3 = getAggregateFromPointer(p3.value, ptr(ofs3));
                Expression agg4 = getAggregateFromPointer(p4.value, ptr(ofs4));
                if ((dir1 == dir2) && pointToSameMemoryBlock(agg1, agg4) && pointToSameMemoryBlock(agg2, agg3) || (dir1 != dir2) && pointToSameMemoryBlock(agg1, agg3) && pointToSameMemoryBlock(agg2, agg4))
                {
                    (pue) = new UnionExp(new IntegerExp(e.loc, ((e.op & 0xFF) == 101) ? 0 : 1, e.type));
                    this.result = (pue).exp();
                    return ;
                }
                e.error(new BytePtr("comparison `%s` of pointers to unrelated memory blocks is indeterminate at compile time, even when combined with `%s`."), e.e1.toChars(), e.e2.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            boolean nott = false;
            Expression ex = e.e1;
            for (; 1 != 0;){
                {
                    NotExp ne = ex.isNotExp();
                    if ((ne) != null)
                    {
                        nott = !nott;
                        ex = ne.e1;
                    }
                    else
                        break;
                }
            }
            Function1<Byte,Byte> negateRelation = new Function1<Byte,Byte>(){
                public Byte invoke(Byte op){
                    switch ((op & 0xFF))
                    {
                        case 57:
                            op = TOK.lessThan;
                            break;
                        case 55:
                            op = TOK.lessOrEqual;
                            break;
                        case 56:
                            op = TOK.greaterThan;
                            break;
                        case 54:
                            op = TOK.greaterOrEqual;
                            break;
                        default:
                        throw new AssertionError("Unreachable code!");
                    }
                    return op;
                }
            };
            byte cmpop = nott ? (byte)(negateRelation.invoke(ex.op) & 0xFF) : (byte)(ex.op & 0xFF);
            int cmp = comparePointers(cmpop, agg1, ofs1.value, agg2, ofs2.value);
            assert((cmp >= 0));
            if (((e.op & 0xFF) == 101) && (cmp == 1) || ((e.op & 0xFF) == 102) && (cmp == 0))
            {
                this.result = interpret(pue, e.e2, this.istate, CtfeGoal.ctfeNeedRvalue);
                return ;
            }
            (pue) = new UnionExp(new IntegerExp(e.loc, ((e.op & 0xFF) == 101) ? 0 : 1, e.type));
            this.result = (pue).exp();
        }

        public  void visit(LogicalExp e) {
            this.interpretFourPointerRelation(this.pue, e);
            if (this.result != null)
                return ;
            this.result = interpret(e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(this.result))
                return ;
            int res = 0;
            boolean andand = (e.op & 0xFF) == 101;
            if (andand ? this.result.isBool(false) : isTrueBool(this.result))
                res = (!andand ? 1 : 0);
            else if (andand ? isTrueBool(this.result) : this.result.isBool(false))
            {
                UnionExp ue2 = new UnionExp().copy();
                this.result = interpret(ue2, e.e2, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(this.result))
                    return ;
                if (((this.result.op & 0xFF) == 232))
                {
                    assert(((e.type.ty & 0xFF) == ENUMTY.Tvoid));
                    this.result = null;
                    return ;
                }
                if (this.result.isBool(false))
                    res = 0;
                else if (isTrueBool(this.result))
                    res = 1;
                else
                {
                    this.result.error(new BytePtr("`%s` does not evaluate to a `bool`"), this.result.toChars());
                    this.result = CTFEExp.cantexp;
                    return ;
                }
            }
            else
            {
                this.result.error(new BytePtr("`%s` cannot be interpreted as a `bool`"), this.result.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            if ((this.goal != CtfeGoal.ctfeNeedNothing))
            {
                (this.pue) = new UnionExp(new IntegerExp(e.loc, res, e.type));
                this.result = (this.pue).exp();
            }
        }

        public  void showCtfeBackTrace(CallExp callingExp, FuncDeclaration fd) {
            if ((CtfeStatus.stackTraceCallsToSuppress > 0))
            {
                CtfeStatus.stackTraceCallsToSuppress -= 1;
                return ;
            }
            errorSupplemental(callingExp.loc, new BytePtr("called from here: `%s`"), callingExp.toChars());
            if ((CtfeStatus.callDepth < 6) || global.params.verbose)
                return ;
            int numToSuppress = 0;
            int recurseCount = 0;
            int depthSoFar = 0;
            InterState lastRecurse = this.istate;
            {
                InterState cur = this.istate;
                for (; cur != null;cur = (cur).caller){
                    if ((pequals((cur).fd, fd)))
                    {
                        recurseCount += 1;
                        numToSuppress = depthSoFar;
                        lastRecurse = cur;
                    }
                    depthSoFar += 1;
                }
            }
            if ((recurseCount < 2))
                return ;
            errorSupplemental(fd.loc, new BytePtr("%d recursive calls to function `%s`"), recurseCount, fd.toChars());
            {
                InterState cur = this.istate;
                for (; (!pequals((cur).fd, fd));cur = (cur).caller){
                    errorSupplemental((cur).fd.loc, new BytePtr("recursively called from function `%s`"), (cur).fd.toChars());
                }
            }
            InterState cur = this.istate;
            for (; ((lastRecurse).caller != null) && (pequals((cur).fd, ((lastRecurse).caller).fd));){
                cur = (cur).caller;
                lastRecurse = (lastRecurse).caller;
                numToSuppress += 1;
            }
            CtfeStatus.stackTraceCallsToSuppress = numToSuppress;
        }

        public  void visit(CallExp e) {
            Expression pthis = null;
            FuncDeclaration fd = null;
            Expression ecall = interpret(e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(ecall))
                return ;
            {
                DotVarExp dve = ecall.isDotVarExp();
                if ((dve) != null)
                {
                    pthis = dve.e1;
                    fd = dve.var.isFuncDeclaration();
                    assert(fd != null);
                    {
                        DotTypeExp dte = pthis.isDotTypeExp();
                        if ((dte) != null)
                            pthis = dte.e1;
                    }
                }
                else {
                    VarExp ve = ecall.isVarExp();
                    if ((ve) != null)
                    {
                        fd = ve.var.isFuncDeclaration();
                        assert(fd != null);
                        if ((pequals(fd.ident, Id.__ArrayPostblit)) || (pequals(fd.ident, Id.__ArrayDtor)))
                        {
                            assert(((e.arguments).length == 1));
                            Expression ea = (e.arguments).get(0);
                            {
                                SliceExp se = ea.isSliceExp();
                                if ((se) != null)
                                    ea = se.e1;
                            }
                            {
                                CastExp ce = ea.isCastExp();
                                if ((ce) != null)
                                    ea = ce.e1;
                            }
                            if (((ea.op & 0xFF) == 26) || ((ea.op & 0xFF) == 25))
                                this.result = getVarExp(e.loc, this.istate, ((SymbolExp)ea).var, CtfeGoal.ctfeNeedRvalue);
                            else {
                                AddrExp ae = ea.isAddrExp();
                                if ((ae) != null)
                                    this.result = interpret(ae.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
                                else {
                                    ArrayLiteralExp ale = ea.isArrayLiteralExp();
                                    if ((ale) != null)
                                        this.result = interpret((Expression)ale, this.istate, CtfeGoal.ctfeNeedRvalue);
                                    else
                                        throw new AssertionError("Unreachable code!");
                                }
                            }
                            if (CTFEExp.isCantExp(this.result))
                                return ;
                            if ((pequals(fd.ident, Id.__ArrayPostblit)))
                                this.result = evaluatePostblit(this.istate, this.result);
                            else
                                this.result = evaluateDtor(this.istate, this.result);
                            if (this.result == null)
                                this.result = CTFEExp.voidexp;
                            return ;
                        }
                    }
                    else {
                        SymOffExp soe = ecall.isSymOffExp();
                        if ((soe) != null)
                        {
                            fd = soe.var.isFuncDeclaration();
                            assert((fd != null) && (soe.offset == 0L));
                        }
                        else {
                            DelegateExp de = ecall.isDelegateExp();
                            if ((de) != null)
                            {
                                fd = de.func;
                                pthis = de.e1;
                                {
                                    VarExp ve = pthis.isVarExp();
                                    if ((ve) != null)
                                        if ((pequals(ve.var, fd)))
                                            pthis = null;
                                }
                            }
                            else {
                                FuncExp fe = ecall.isFuncExp();
                                if ((fe) != null)
                                {
                                    fd = fe.fd;
                                }
                                else
                                {
                                    e.error(new BytePtr("cannot call `%s` at compile time"), e.toChars());
                                    this.result = CTFEExp.cantexp;
                                    return ;
                                }
                            }
                        }
                    }
                }
            }
            if (fd == null)
            {
                e.error(new BytePtr("CTFE internal error: cannot evaluate `%s` at compile time"), e.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            if (pthis != null)
            {
                assert(!fd.isNested() || fd.needThis());
                if (((pthis.op & 0xFF) == 42))
                {
                    pthis.error(new BytePtr("static variable `%s` cannot be read at compile time"), pthis.toChars());
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                assert(pthis != null);
                if (((pthis.op & 0xFF) == 13))
                {
                    assert(((pthis.type.toBasetype().ty & 0xFF) == ENUMTY.Tclass));
                    e.error(new BytePtr("function call through null class reference `%s`"), pthis.toChars());
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                assert(((pthis.op & 0xFF) == 49) || ((pthis.op & 0xFF) == 50));
                if (fd.isVirtual() && !e.directcall)
                {
                    assert(((pthis.op & 0xFF) == 50));
                    ClassDeclaration cd = ((ClassReferenceExp)pthis).originalClass();
                    fd = cd.findFunc(fd.ident, (TypeFunction)fd.type);
                    assert(fd != null);
                }
            }
            if ((fd != null) && (fd.semanticRun >= PASS.semantic3done) && fd.semantic3Errors)
            {
                e.error(new BytePtr("CTFE failed because of previous errors in `%s`"), fd.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            this.result = evaluateIfBuiltin(this.pue, this.istate, e.loc, fd, e.arguments, pthis);
            if (this.result != null)
                return ;
            if (fd.fbody == null)
            {
                e.error(new BytePtr("`%s` cannot be interpreted at compile time, because it has no available source code"), fd.toChars());
                this.result = CTFEExp.showcontext;
                return ;
            }
            this.result = interpretFunction(this.pue, fd, this.istate, e.arguments, pthis);
            if (((this.result.op & 0xFF) == 232))
                return ;
            if (!exceptionOrCantInterpret(this.result))
            {
                if ((this.goal != CtfeGoal.ctfeNeedLvalue))
                {
                    if ((pequals(this.result, (this.pue).exp())))
                        this.result = (this.pue).copy();
                    this.result = interpret(this.pue, this.result, this.istate, CtfeGoal.ctfeNeedRvalue);
                }
            }
            if (!exceptionOrCantInterpret(this.result))
            {
                this.result = paintTypeOntoLiteral(e.type, this.result);
                this.result.loc = e.loc.copy();
            }
            else if (CTFEExp.isCantExp(this.result) && (global.gag == 0))
                this.showCtfeBackTrace(e, fd);
        }

        public  void visit(CommaExp e) {
            InterState istateComma = new InterState();
            if ((this.istate == null) && ((firstComma(e.e1).op & 0xFF) == 38))
            {
                ctfeStack.startFrame(null);
                this.istate = istateComma;
            }
            Function0<Void> endTempStackFrame = new Function0<Void>(){
                public Void invoke(){
                    if ((istate == istateComma))
                        ctfeStack.endFrame();
                }
            };
            this.result = CTFEExp.cantexp;
            if (((e.e1.op & 0xFF) == 38) && ((e.e2.op & 0xFF) == 26) && (pequals(((DeclarationExp)e.e1).declaration, ((VarExp)e.e2).var)) && ((((VarExp)e.e2).var.storage_class & 68719476736L) != 0))
            {
                VarExp ve = (VarExp)e.e2;
                VarDeclaration v = ve.var.isVarDeclaration();
                ctfeStack.push(v);
                if ((v._init == null) && (getValue(v) == null))
                {
                    setValue(v, copyLiteral(v.type.defaultInitLiteral(e.loc)).copy());
                }
                if (getValue(v) == null)
                {
                    Expression newval = initializerToExpression(v._init, null);
                    newval = interpret(newval, this.istate, CtfeGoal.ctfeNeedRvalue);
                    if (this.exceptionOrCant(newval))
                        endTempStackFrame.invoke();
                        return ;
                    if (((newval.op & 0xFF) != 232))
                    {
                        setValueWithoutChecking(v, copyLiteral(newval).copy());
                    }
                }
            }
            else
            {
                UnionExp ue = null;
                Expression e1 = interpret(ue, e.e1, this.istate, CtfeGoal.ctfeNeedNothing);
                if (this.exceptionOrCant(e1))
                    endTempStackFrame.invoke();
                    return ;
            }
            this.result = interpret(this.pue, e.e2, this.istate, this.goal);
            endTempStackFrame.invoke();
            return ;
        }

        public  void visit(CondExp e) {
            UnionExp uecond = null;
            Expression econd = null;
            econd = interpret(uecond, e.econd, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(econd))
                return ;
            if (isPointer(e.econd.type))
            {
                if (((econd.op & 0xFF) != 13))
                {
                    uecond = new UnionExp(new IntegerExp(e.loc, 1, Type.tbool));
                    econd = uecond.exp();
                }
            }
            if (isTrueBool(econd))
                this.result = interpret(this.pue, e.e1, this.istate, this.goal);
            else if (econd.isBool(false))
                this.result = interpret(this.pue, e.e2, this.istate, this.goal);
            else
            {
                e.error(new BytePtr("`%s` does not evaluate to boolean result at compile time"), e.econd.toChars());
                this.result = CTFEExp.cantexp;
            }
        }

        public  void visit(ArrayLengthExp e) {
            UnionExp ue1 = new UnionExp().copy();
            Expression e1 = interpret(ue1, e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
            assert(e1 != null);
            if (this.exceptionOrCant(e1))
                return ;
            if (((e1.op & 0xFF) != 121) && ((e1.op & 0xFF) != 47) && ((e1.op & 0xFF) != 31) && ((e1.op & 0xFF) != 13))
            {
                e.error(new BytePtr("`%s` cannot be evaluated at compile time"), e.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            (this.pue) = new UnionExp(new IntegerExp(e.loc, resolveArrayLength(e1), e.type));
            this.result = (this.pue).exp();
        }

        public static Expression interpretVectorToArray(UnionExp pue, VectorExp e) {
            {
                ArrayLiteralExp ale = e.e1.isArrayLiteralExp();
                if ((ale) != null)
                    return ale;
            }
            if (((e.e1.op & 0xFF) == 135) || ((e.e1.op & 0xFF) == 140))
            {
                DArray<Expression> elements = new DArray<Expression>(e.dim);
                {
                    Slice<Expression> __r980 = (elements).opSlice().copy();
                    int __key981 = 0;
                    for (; (__key981 < __r980.getLength());__key981 += 1) {
                        Expression element = __r980.get(__key981);
                        element = copyLiteral(e.e1).copy();
                    }
                }
                Type type = ((e.type.ty & 0xFF) == ENUMTY.Tvector) ? e.type.isTypeVector().basetype : e.type.isTypeSArray();
                assert(type != null);
                (pue) = new UnionExp(new ArrayLiteralExp(e.loc, type, elements));
                ArrayLiteralExp ale = (ArrayLiteralExp)(pue).exp();
                ale.ownedByCtfe = OwnedBy.ctfe;
                return ale;
            }
            return e;
        }

        public  void visit(VectorExp e) {
            if (((e.ownedByCtfe & 0xFF) >= 1))
            {
                this.result = e;
                return ;
            }
            Expression e1 = interpret(this.pue, e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
            assert(e1 != null);
            if (this.exceptionOrCant(e1))
                return ;
            if (((e1.op & 0xFF) != 47) && ((e1.op & 0xFF) != 135) && ((e1.op & 0xFF) != 140))
            {
                e.error(new BytePtr("`%s` cannot be evaluated at compile time"), e.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            if ((pequals(e1, (this.pue).exp())))
                e1 = (this.pue).copy();
            (this.pue) = new UnionExp(new VectorExp(e.loc, e1, e.to));
            VectorExp ve = (VectorExp)(this.pue).exp();
            ve.type = e.type;
            ve.dim = e.dim;
            ve.ownedByCtfe = OwnedBy.ctfe;
            this.result = ve;
        }

        public  void visit(VectorArrayExp e) {
            Expression e1 = interpret(this.pue, e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
            assert(e1 != null);
            if (this.exceptionOrCant(e1))
                return ;
            {
                VectorExp ve = e1.isVectorExp();
                if ((ve) != null)
                {
                    this.result = interpretVectorToArray(this.pue, ve);
                    if (((this.result.op & 0xFF) != 229))
                        return ;
                }
            }
            e.error(new BytePtr("`%s` cannot be evaluated at compile time"), e.toChars());
            this.result = CTFEExp.cantexp;
        }

        public  void visit(DelegatePtrExp e) {
            Expression e1 = interpret(this.pue, e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
            assert(e1 != null);
            if (this.exceptionOrCant(e1))
                return ;
            e.error(new BytePtr("`%s` cannot be evaluated at compile time"), e.toChars());
            this.result = CTFEExp.cantexp;
        }

        public  void visit(DelegateFuncptrExp e) {
            Expression e1 = interpret(this.pue, e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
            assert(e1 != null);
            if (this.exceptionOrCant(e1))
                return ;
            e.error(new BytePtr("`%s` cannot be evaluated at compile time"), e.toChars());
            this.result = CTFEExp.cantexp;
        }

        public static boolean resolveIndexing(IndexExp e, InterState istate, Ptr<Expression> pagg, Ptr<Long> pidx, boolean modify) {
            assert(((e.e1.type.toBasetype().ty & 0xFF) != ENUMTY.Taarray));
            if (((e.e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tpointer))
            {
                Expression e1 = interpret(e.e1, istate, CtfeGoal.ctfeNeedRvalue);
                if (exceptionOrCantInterpret(e1))
                    return false;
                Expression e2 = interpret(e.e2, istate, CtfeGoal.ctfeNeedRvalue);
                if (exceptionOrCantInterpret(e2))
                    return false;
                long indx = (long)e2.toInteger();
                Ref<Long> ofs = ref(0L);
                Expression agg = getAggregateFromPointer(e1, ptr(ofs));
                if (((agg.op & 0xFF) == 13))
                {
                    e.error(new BytePtr("cannot index through null pointer `%s`"), e.e1.toChars());
                    return false;
                }
                if (((agg.op & 0xFF) == 135))
                {
                    e.error(new BytePtr("cannot index through invalid pointer `%s` of value `%s`"), e.e1.toChars(), e1.toChars());
                    return false;
                }
                if (((agg.op & 0xFF) == 25))
                {
                    e.error(new BytePtr("mutable variable `%s` cannot be %s at compile time, even through a pointer"), modify ? new BytePtr("modified") : new BytePtr("read"), ((SymOffExp)agg).var.toChars());
                    return false;
                }
                if (((agg.op & 0xFF) == 47) || ((agg.op & 0xFF) == 121))
                {
                    long len = resolveArrayLength(agg);
                    if ((ofs.value + (long)indx >= len))
                    {
                        e.error(new BytePtr("pointer index `[%lld]` exceeds allocated memory block `[0..%lld]`"), ofs.value + (long)indx, len);
                        return false;
                    }
                }
                else
                {
                    if ((ofs.value + (long)indx != 0L))
                    {
                        e.error(new BytePtr("pointer index `[%lld]` lies outside memory block `[0..1]`"), ofs.value + (long)indx);
                        return false;
                    }
                }
                pagg.set(0, agg);
                pidx.set(0, (ofs.value + (long)indx));
                return true;
            }
            Expression e1 = interpret(e.e1, istate, CtfeGoal.ctfeNeedRvalue);
            if (exceptionOrCantInterpret(e1))
                return false;
            if (((e1.op & 0xFF) == 13))
            {
                e.error(new BytePtr("cannot index null array `%s`"), e.e1.toChars());
                return false;
            }
            {
                VectorExp ve = e1.isVectorExp();
                if ((ve) != null)
                {
                    UnionExp ue = null;
                    e1 = interpretVectorToArray(ue, ve);
                    e1 = (pequals(e1, ue.exp())) ? ue.copy() : e1;
                }
            }
            long len = 0L;
            if (((e1.op & 0xFF) == 26) && ((e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
                len = e1.type.toBasetype().isTypeSArray().dim.toInteger();
            else
            {
                if (((e1.op & 0xFF) != 47) && ((e1.op & 0xFF) != 121) && ((e1.op & 0xFF) != 31) && ((e1.op & 0xFF) != 229))
                {
                    e.error(new BytePtr("cannot determine length of `%s` at compile time"), e.e1.toChars());
                    return false;
                }
                len = resolveArrayLength(e1);
            }
            if (e.lengthVar != null)
            {
                Expression dollarExp = new IntegerExp(e.loc, len, Type.tsize_t);
                ctfeStack.push(e.lengthVar);
                setValue(e.lengthVar, dollarExp);
            }
            Expression e2 = interpret(e.e2, istate, CtfeGoal.ctfeNeedRvalue);
            if (e.lengthVar != null)
                ctfeStack.pop(e.lengthVar);
            if (exceptionOrCantInterpret(e2))
                return false;
            if (((e2.op & 0xFF) != 135))
            {
                e.error(new BytePtr("CTFE internal error: non-integral index `[%s]`"), e.e2.toChars());
                return false;
            }
            {
                SliceExp se = e1.isSliceExp();
                if ((se) != null)
                {
                    long index = e2.toInteger();
                    long ilwr = se.lwr.toInteger();
                    long iupr = se.upr.toInteger();
                    if ((index > iupr - ilwr))
                    {
                        e.error(new BytePtr("index %llu exceeds array length %llu"), index, iupr - ilwr);
                        return false;
                    }
                    pagg.set(0, ((SliceExp)e1).e1);
                    pidx.set(0, (index + ilwr));
                }
                else
                {
                    pagg.set(0, e1);
                    pidx.set(0, e2.toInteger());
                    if ((len <= pidx.get()))
                    {
                        e.error(new BytePtr("array index %lld is out of bounds `[0..%lld]`"), pidx.get(), len);
                        return false;
                    }
                }
            }
            return true;
        }

        public  void visit(IndexExp e) {
            if (((e.e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tpointer))
            {
                Ref<Expression> agg = ref(null);
                Ref<Long> indexToAccess = ref(0L);
                if (!resolveIndexing(e, this.istate, ptr(agg), ptr(indexToAccess), false))
                {
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                if (((agg.value.op & 0xFF) == 47) || ((agg.value.op & 0xFF) == 121))
                {
                    if ((this.goal == CtfeGoal.ctfeNeedLvalue))
                    {
                        (this.pue) = new UnionExp(new IndexExp(e.loc, agg.value, new IntegerExp(e.e2.loc, indexToAccess.value, e.e2.type)));
                        this.result = (this.pue).exp();
                        this.result.type = e.type;
                        return ;
                    }
                    this.result = ctfeIndex(e.loc, e.type, agg.value, indexToAccess.value);
                    return ;
                }
                else
                {
                    assert((indexToAccess.value == 0L));
                    this.result = interpret(agg.value, this.istate, this.goal);
                    if (this.exceptionOrCant(this.result))
                        return ;
                    this.result = paintTypeOntoLiteral(e.type, this.result);
                    return ;
                }
            }
            if (((e.e1.type.toBasetype().ty & 0xFF) == ENUMTY.Taarray))
            {
                Expression e1 = interpret(e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e1))
                    return ;
                if (((e1.op & 0xFF) == 13))
                {
                    if ((this.goal == CtfeGoal.ctfeNeedLvalue) && ((e1.type.ty & 0xFF) == ENUMTY.Taarray) && e.modifiable)
                    {
                        throw new AssertionError("Unreachable code!");
                    }
                    e.error(new BytePtr("cannot index null array `%s`"), e.e1.toChars());
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                Expression e2 = interpret(e.e2, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e2))
                    return ;
                if ((this.goal == CtfeGoal.ctfeNeedLvalue))
                {
                    if ((pequals(e1, e.e1)) && (pequals(e2, e.e2)))
                        this.result = e;
                    else
                    {
                        (this.pue) = new UnionExp(new IndexExp(e.loc, e1, e2));
                        this.result = (this.pue).exp();
                        this.result.type = e.type;
                    }
                    return ;
                }
                assert(((e1.op & 0xFF) == 48));
                UnionExp e2tmp = null;
                e2 = resolveSlice(e2, e2tmp);
                this.result = findKeyInAA(e.loc, (AssocArrayLiteralExp)e1, e2);
                if (this.result == null)
                {
                    e.error(new BytePtr("key `%s` not found in associative array `%s`"), e2.toChars(), e.e1.toChars());
                    this.result = CTFEExp.cantexp;
                }
                return ;
            }
            Ref<Expression> agg = ref(null);
            Ref<Long> indexToAccess = ref(0L);
            if (!resolveIndexing(e, this.istate, ptr(agg), ptr(indexToAccess), false))
            {
                this.result = CTFEExp.cantexp;
                return ;
            }
            if ((this.goal == CtfeGoal.ctfeNeedLvalue))
            {
                Expression e2 = new IntegerExp(e.e2.loc, indexToAccess.value, Type.tsize_t);
                (this.pue) = new UnionExp(new IndexExp(e.loc, agg.value, e2));
                this.result = (this.pue).exp();
                this.result.type = e.type;
                return ;
            }
            this.result = ctfeIndex(e.loc, e.type, agg.value, indexToAccess.value);
            if (this.exceptionOrCant(this.result))
                return ;
            if (((this.result.op & 0xFF) == 128))
            {
                e.error(new BytePtr("`%s` is used before initialized"), e.toChars());
                errorSupplemental(this.result.loc, new BytePtr("originally uninitialized here"));
                this.result = CTFEExp.cantexp;
                return ;
            }
            this.result = paintTypeOntoLiteral(e.type, this.result);
        }

        public  void visit(SliceExp e) {
            if (((e.e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tpointer))
            {
                Expression e1 = interpret(e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e1))
                    return ;
                if (((e1.op & 0xFF) == 135))
                {
                    e.error(new BytePtr("cannot slice invalid pointer `%s` of value `%s`"), e.e1.toChars(), e1.toChars());
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                Expression lwr = interpret(e.lwr, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(lwr))
                    return ;
                Expression upr = interpret(e.upr, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(upr))
                    return ;
                long ilwr = lwr.toInteger();
                long iupr = upr.toInteger();
                Ref<Long> ofs = ref(0L);
                Expression agg = getAggregateFromPointer(e1, ptr(ofs));
                ilwr += ofs.value;
                iupr += ofs.value;
                if (((agg.op & 0xFF) == 13))
                {
                    if ((iupr == ilwr))
                    {
                        this.result = new NullExp(e.loc, null);
                        this.result.type = e.type;
                        return ;
                    }
                    e.error(new BytePtr("cannot slice null pointer `%s`"), e.e1.toChars());
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                if (((agg.op & 0xFF) == 25))
                {
                    e.error(new BytePtr("slicing pointers to static variables is not supported in CTFE"));
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                if (((agg.op & 0xFF) != 47) && ((agg.op & 0xFF) != 121))
                {
                    e.error(new BytePtr("pointer `%s` cannot be sliced at compile time (it does not point to an array)"), e.e1.toChars());
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                assert(((agg.op & 0xFF) == 47) || ((agg.op & 0xFF) == 121));
                long len = ArrayLength(Type.tsize_t, agg).exp().toInteger();
                if ((iupr > len + 1L) || (iupr < ilwr))
                {
                    e.error(new BytePtr("pointer slice `[%lld..%lld]` exceeds allocated memory block `[0..%lld]`"), ilwr, iupr, len);
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                if ((ofs.value != 0L))
                {
                    lwr = new IntegerExp(e.loc, ilwr, lwr.type);
                    upr = new IntegerExp(e.loc, iupr, upr.type);
                }
                (this.pue) = new UnionExp(new SliceExp(e.loc, agg, lwr, upr));
                this.result = (this.pue).exp();
                this.result.type = e.type;
                return ;
            }
            int goal1 = CtfeGoal.ctfeNeedRvalue;
            if ((this.goal == CtfeGoal.ctfeNeedLvalue))
            {
                if (((e.e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
                    {
                        VarExp ve = e.e1.isVarExp();
                        if ((ve) != null)
                            {
                                VarDeclaration vd = ve.var.isVarDeclaration();
                                if ((vd) != null)
                                    if ((vd.storage_class & 2097152L) != 0)
                                        goal1 = CtfeGoal.ctfeNeedLvalue;
                            }
                    }
            }
            Expression e1 = interpret(e.e1, this.istate, goal1);
            if (this.exceptionOrCant(e1))
                return ;
            if (e.lwr == null)
            {
                this.result = paintTypeOntoLiteral(e.type, e1);
                return ;
            }
            {
                VectorExp ve = e1.isVectorExp();
                if ((ve) != null)
                {
                    e1 = interpretVectorToArray(this.pue, ve);
                    e1 = (pequals(e1, (this.pue).exp())) ? (this.pue).copy() : e1;
                }
            }
            long dollar = 0L;
            if (((e1.op & 0xFF) == 26) || ((e1.op & 0xFF) == 27) && ((e1.type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
                dollar = e1.type.toBasetype().isTypeSArray().dim.toInteger();
            else
            {
                if (((e1.op & 0xFF) != 47) && ((e1.op & 0xFF) != 121) && ((e1.op & 0xFF) != 13) && ((e1.op & 0xFF) != 31) && ((e1.op & 0xFF) != 229))
                {
                    e.error(new BytePtr("cannot determine length of `%s` at compile time"), e1.toChars());
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                dollar = resolveArrayLength(e1);
            }
            if (e.lengthVar != null)
            {
                IntegerExp dollarExp = new IntegerExp(e.loc, dollar, Type.tsize_t);
                ctfeStack.push(e.lengthVar);
                setValue(e.lengthVar, dollarExp);
            }
            Expression lwr = interpret(e.lwr, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(lwr))
            {
                if (e.lengthVar != null)
                    ctfeStack.pop(e.lengthVar);
                return ;
            }
            Expression upr = interpret(e.upr, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(upr))
            {
                if (e.lengthVar != null)
                    ctfeStack.pop(e.lengthVar);
                return ;
            }
            if (e.lengthVar != null)
                ctfeStack.pop(e.lengthVar);
            long ilwr = lwr.toInteger();
            long iupr = upr.toInteger();
            if (((e1.op & 0xFF) == 13))
            {
                if ((ilwr == 0L) && (iupr == 0L))
                {
                    this.result = e1;
                    return ;
                }
                e1.error(new BytePtr("slice `[%llu..%llu]` is out of bounds"), ilwr, iupr);
                this.result = CTFEExp.cantexp;
                return ;
            }
            {
                SliceExp se = e1.isSliceExp();
                if ((se) != null)
                {
                    long lo1 = se.lwr.toInteger();
                    long up1 = se.upr.toInteger();
                    if ((ilwr > iupr) || (iupr > up1 - lo1))
                    {
                        e.error(new BytePtr("slice `[%llu..%llu]` exceeds array bounds `[%llu..%llu]`"), ilwr, iupr, lo1, up1);
                        this.result = CTFEExp.cantexp;
                        return ;
                    }
                    ilwr += lo1;
                    iupr += lo1;
                    (this.pue) = new UnionExp(new SliceExp(e.loc, se.e1, new IntegerExp(e.loc, ilwr, lwr.type), new IntegerExp(e.loc, iupr, upr.type)));
                    this.result = (this.pue).exp();
                    this.result.type = e.type;
                    return ;
                }
            }
            if (((e1.op & 0xFF) == 47) || ((e1.op & 0xFF) == 121))
            {
                if ((iupr < ilwr) || (dollar < iupr))
                {
                    e.error(new BytePtr("slice `[%lld..%lld]` exceeds array bounds `[0..%lld]`"), ilwr, iupr, dollar);
                    this.result = CTFEExp.cantexp;
                    return ;
                }
            }
            (this.pue) = new UnionExp(new SliceExp(e.loc, e1, lwr, upr));
            this.result = (this.pue).exp();
            this.result.type = e.type;
        }

        public  void visit(InExp e) {
            Expression e1 = interpret(e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e1))
                return ;
            Expression e2 = interpret(e.e2, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e2))
                return ;
            if (((e2.op & 0xFF) == 13))
            {
                (this.pue) = new UnionExp(new NullExp(e.loc, e.type));
                this.result = (this.pue).exp();
                return ;
            }
            if (((e2.op & 0xFF) != 48))
            {
                e.error(new BytePtr("`%s` cannot be interpreted at compile time"), e.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            e1 = resolveSlice(e1, null);
            this.result = findKeyInAA(e.loc, (AssocArrayLiteralExp)e2, e1);
            if (this.exceptionOrCant(this.result))
                return ;
            if (this.result == null)
            {
                (this.pue) = new UnionExp(new NullExp(e.loc, e.type));
                this.result = (this.pue).exp();
            }
            else
            {
                this.result = new IndexExp(e.loc, e2, e1);
                this.result.type = e.type.nextOf();
                (this.pue) = new UnionExp(new AddrExp(e.loc, this.result, e.type));
                this.result = (this.pue).exp();
            }
        }

        public  void visit(CatExp e) {
            UnionExp ue1 = null;
            Expression e1 = interpret(ue1, e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e1))
                return ;
            UnionExp ue2 = null;
            Expression e2 = interpret(ue2, e.e2, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e2))
                return ;
            UnionExp e1tmp = null;
            e1 = resolveSlice(e1, e1tmp);
            UnionExp e2tmp = null;
            e2 = resolveSlice(e2, e2tmp);
            if (!(((e1.op & 0xFF) == 121) && ((e2.op & 0xFF) == 121)))
            {
                if ((pequals(e1, ue1.exp())))
                    e1 = ue1.copy();
                if ((pequals(e2, ue2.exp())))
                    e2 = ue2.copy();
            }
            this.pue.opAssign(ctfeCat(e.loc, e.type, e1, e2));
            this.result = (this.pue).exp();
            if (CTFEExp.isCantExp(this.result))
            {
                e.error(new BytePtr("`%s` cannot be interpreted at compile time"), e.toChars());
                return ;
            }
            {
                ArrayLiteralExp ale = this.result.isArrayLiteralExp();
                if ((ale) != null)
                {
                    ale.ownedByCtfe = OwnedBy.ctfe;
                    {
                        Slice<Expression> __r982 = (ale.elements).opSlice().copy();
                        int __key983 = 0;
                        for (; (__key983 < __r982.getLength());__key983 += 1) {
                            Expression elem = __r982.get(__key983);
                            Expression ex = evaluatePostblit(this.istate, elem);
                            if (this.exceptionOrCant(ex))
                                return ;
                        }
                    }
                }
                else {
                    StringExp se = this.result.isStringExp();
                    if ((se) != null)
                        se.ownedByCtfe = OwnedBy.ctfe;
                }
            }
        }

        public  void visit(DeleteExp e) {
            this.result = interpret(e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(this.result))
                return ;
            if (((this.result.op & 0xFF) == 13))
            {
                this.result = CTFEExp.voidexp;
                return ;
            }
            Type tb = e.e1.type.toBasetype();
            switch ((tb.ty & 0xFF))
            {
                case 7:
                    if (((this.result.op & 0xFF) != 50))
                    {
                        e.error(new BytePtr("`delete` on invalid class reference `%s`"), this.result.toChars());
                        this.result = CTFEExp.cantexp;
                        return ;
                    }
                    ClassReferenceExp cre = (ClassReferenceExp)this.result;
                    ClassDeclaration cd = cre.originalClass();
                    if (cd.aggDelete != null)
                    {
                        e.error(new BytePtr("member deallocators not supported by CTFE"));
                        this.result = CTFEExp.cantexp;
                        return ;
                    }
                    if (cd.dtor != null)
                    {
                        this.result = interpretFunction(this.pue, cd.dtor, this.istate, null, cre);
                        if (this.exceptionOrCant(this.result))
                            return ;
                    }
                    break;
                case 3:
                    tb = ((TypePointer)tb).next.toBasetype();
                    if (((tb.ty & 0xFF) == ENUMTY.Tstruct))
                    {
                        if (((this.result.op & 0xFF) != 19) || ((((AddrExp)this.result).e1.op & 0xFF) != 49))
                        {
                            e.error(new BytePtr("`delete` on invalid struct pointer `%s`"), this.result.toChars());
                            this.result = CTFEExp.cantexp;
                            return ;
                        }
                        StructDeclaration sd = ((TypeStruct)tb).sym;
                        StructLiteralExp sle = (StructLiteralExp)((AddrExp)this.result).e1;
                        if (sd.aggDelete != null)
                        {
                            e.error(new BytePtr("member deallocators not supported by CTFE"));
                            this.result = CTFEExp.cantexp;
                            return ;
                        }
                        if (sd.dtor != null)
                        {
                            this.result = interpretFunction(this.pue, sd.dtor, this.istate, null, sle);
                            if (this.exceptionOrCant(this.result))
                                return ;
                        }
                    }
                    break;
                case 0:
                    Type tv = tb.nextOf().baseElemOf();
                    if (((tv.ty & 0xFF) == ENUMTY.Tstruct))
                    {
                        if (((this.result.op & 0xFF) != 47))
                        {
                            e.error(new BytePtr("`delete` on invalid struct array `%s`"), this.result.toChars());
                            this.result = CTFEExp.cantexp;
                            return ;
                        }
                        StructDeclaration sd_1 = ((TypeStruct)tv).sym;
                        if (sd_1.aggDelete != null)
                        {
                            e.error(new BytePtr("member deallocators not supported by CTFE"));
                            this.result = CTFEExp.cantexp;
                            return ;
                        }
                        if (sd_1.dtor != null)
                        {
                            ArrayLiteralExp ale = (ArrayLiteralExp)this.result;
                            {
                                Slice<Expression> __r984 = (ale.elements).opSlice().copy();
                                int __key985 = 0;
                                for (; (__key985 < __r984.getLength());__key985 += 1) {
                                    Expression el = __r984.get(__key985);
                                    this.result = interpretFunction(this.pue, sd_1.dtor, this.istate, null, el);
                                    if (this.exceptionOrCant(this.result))
                                        return ;
                                }
                            }
                        }
                    }
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            this.result = CTFEExp.voidexp;
        }

        public  void visit(CastExp e) {
            Expression e1 = interpret(e.e1, this.istate, this.goal);
            if (this.exceptionOrCant(e1))
                return ;
            if (((e.to.ty & 0xFF) == ENUMTY.Tvoid))
            {
                this.result = CTFEExp.voidexp;
                return ;
            }
            if (((e.to.ty & 0xFF) == ENUMTY.Tpointer) && ((e1.op & 0xFF) != 13))
            {
                Type pointee = ((TypePointer)e.type).next;
                if (((e1.op & 0xFF) == 135))
                {
                    this.result = paintTypeOntoLiteral(this.pue, e.to, e1);
                    return ;
                }
                boolean castToSarrayPointer = false;
                boolean castBackFromVoid = false;
                if (((e1.type.ty & 0xFF) == ENUMTY.Tarray) || ((e1.type.ty & 0xFF) == ENUMTY.Tsarray) || ((e1.type.ty & 0xFF) == ENUMTY.Tpointer))
                {
                    Type elemtype = e1.type.nextOf();
                    {
                        SliceExp se = e1.isSliceExp();
                        if ((se) != null)
                            elemtype = se.e1.type.nextOf();
                    }
                    Type ultimatePointee = pointee;
                    Type ultimateSrc = elemtype;
                    for (; ((ultimatePointee.ty & 0xFF) == ENUMTY.Tpointer) && ((ultimateSrc.ty & 0xFF) == ENUMTY.Tpointer);){
                        ultimatePointee = ultimatePointee.nextOf();
                        ultimateSrc = ultimateSrc.nextOf();
                    }
                    if (((ultimatePointee.ty & 0xFF) == ENUMTY.Tsarray) && ultimatePointee.nextOf().equivalent(ultimateSrc))
                    {
                        castToSarrayPointer = true;
                    }
                    else if (((ultimatePointee.ty & 0xFF) != ENUMTY.Tvoid) && ((ultimateSrc.ty & 0xFF) != ENUMTY.Tvoid) && !isSafePointerCast(elemtype, pointee))
                    {
                        e.error(new BytePtr("reinterpreting cast from `%s*` to `%s*` is not supported in CTFE"), elemtype.toChars(), pointee.toChars());
                        this.result = CTFEExp.cantexp;
                        return ;
                    }
                    if (((ultimateSrc.ty & 0xFF) == ENUMTY.Tvoid))
                        castBackFromVoid = true;
                }
                {
                    SliceExp se = e1.isSliceExp();
                    if ((se) != null)
                    {
                        if (((se.e1.op & 0xFF) == 13))
                        {
                            this.result = paintTypeOntoLiteral(this.pue, e.type, se.e1);
                            return ;
                        }
                        IndexExp ei = new IndexExp(e.loc, se.e1, se.lwr);
                        ei.type = e.type.nextOf();
                        (this.pue) = new UnionExp(new AddrExp(e.loc, ei, e.type));
                        this.result = (this.pue).exp();
                        return ;
                    }
                }
                if (((e1.op & 0xFF) == 47) || ((e1.op & 0xFF) == 121))
                {
                    IndexExp ei = new IndexExp(e.loc, e1, new IntegerExp(e.loc, 0L, Type.tsize_t));
                    ei.type = e.type.nextOf();
                    (this.pue) = new UnionExp(new AddrExp(e.loc, ei, e.type));
                    this.result = (this.pue).exp();
                    return ;
                }
                if (((e1.op & 0xFF) == 62) && !((IndexExp)e1).e1.type.equals(e1.type))
                {
                    IndexExp ie = (IndexExp)e1;
                    if (castBackFromVoid)
                    {
                        Type origType = ie.e1.type.nextOf();
                        if (((ie.e1.op & 0xFF) == 47) && ((ie.e2.op & 0xFF) == 135))
                        {
                            ArrayLiteralExp ale = (ArrayLiteralExp)ie.e1;
                            int indx = (int)ie.e2.toInteger();
                            if ((indx < (ale.elements).length))
                            {
                                {
                                    Expression xx = (ale.elements).get(indx);
                                    if ((xx) != null)
                                    {
                                        {
                                            IndexExp iex = xx.isIndexExp();
                                            if ((iex) != null)
                                                origType = iex.e1.type.nextOf();
                                            else {
                                                AddrExp ae = xx.isAddrExp();
                                                if ((ae) != null)
                                                    origType = ae.e1.type;
                                                else {
                                                    VarExp ve = xx.isVarExp();
                                                    if ((ve) != null)
                                                        origType = ve.var.type;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (!isSafePointerCast(origType, pointee))
                        {
                            e.error(new BytePtr("using `void*` to reinterpret cast from `%s*` to `%s*` is not supported in CTFE"), origType.toChars(), pointee.toChars());
                            this.result = CTFEExp.cantexp;
                            return ;
                        }
                    }
                    (this.pue) = new UnionExp(new IndexExp(e1.loc, ie.e1, ie.e2));
                    this.result = (this.pue).exp();
                    this.result.type = e.type;
                    return ;
                }
                {
                    AddrExp ae = e1.isAddrExp();
                    if ((ae) != null)
                    {
                        Type origType = ae.e1.type;
                        if (isSafePointerCast(origType, pointee))
                        {
                            (this.pue) = new UnionExp(new AddrExp(e.loc, ae.e1, e.type));
                            this.result = (this.pue).exp();
                            return ;
                        }
                        if (castToSarrayPointer && ((pointee.toBasetype().ty & 0xFF) == ENUMTY.Tsarray) && ((ae.e1.op & 0xFF) == 62))
                        {
                            long dim = ((TypeSArray)pointee.toBasetype()).dim.toInteger();
                            IndexExp ie = (IndexExp)ae.e1;
                            Expression lwr = ie.e2;
                            Expression upr = new IntegerExp(ie.e2.loc, ie.e2.toInteger() + dim, Type.tsize_t);
                            SliceExp er = new SliceExp(e.loc, ie.e1, lwr, upr);
                            er.type = pointee;
                            (this.pue) = new UnionExp(new AddrExp(e.loc, er, e.type));
                            this.result = (this.pue).exp();
                            return ;
                        }
                    }
                }
                if (((e1.op & 0xFF) == 26) || ((e1.op & 0xFF) == 25))
                {
                    Type origType = ((SymbolExp)e1).var.type;
                    if (castBackFromVoid && !isSafePointerCast(origType, pointee))
                    {
                        e.error(new BytePtr("using `void*` to reinterpret cast from `%s*` to `%s*` is not supported in CTFE"), origType.toChars(), pointee.toChars());
                        this.result = CTFEExp.cantexp;
                        return ;
                    }
                    {
                        VarExp ve = e1.isVarExp();
                        if ((ve) != null)
                            (this.pue) = new UnionExp(new VarExp(e.loc, ve.var));
                        else
                            (this.pue) = new UnionExp(new SymOffExp(e.loc, ((SymOffExp)e1).var, ((SymOffExp)e1).offset));
                    }
                    this.result = (this.pue).exp();
                    this.result.type = e.to;
                    return ;
                }
                e1 = interpret(e1, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (((e1.op & 0xFF) != 13))
                {
                    e.error(new BytePtr("pointer cast from `%s` to `%s` is not supported at compile time"), e1.type.toChars(), e.to.toChars());
                    this.result = CTFEExp.cantexp;
                    return ;
                }
            }
            if (((e.to.ty & 0xFF) == ENUMTY.Tsarray) && ((e.e1.type.ty & 0xFF) == ENUMTY.Tvector))
            {
                e1 = interpret(e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e1))
                    return ;
                assert(((e1.op & 0xFF) == 229));
                e1 = interpretVectorToArray(this.pue, e1.isVectorExp());
            }
            if (((e.to.ty & 0xFF) == ENUMTY.Tarray) && ((e1.op & 0xFF) == 31))
            {
                SliceExp se = (SliceExp)e1;
                if (!isSafePointerCast(se.e1.type.nextOf(), e.to.nextOf()))
                {
                    e.error(new BytePtr("array cast from `%s` to `%s` is not supported at compile time"), se.e1.type.toChars(), e.to.toChars());
                    this.result = CTFEExp.cantexp;
                    return ;
                }
                (this.pue) = new UnionExp(new SliceExp(e1.loc, se.e1, se.lwr, se.upr));
                this.result = (this.pue).exp();
                this.result.type = e.to;
                return ;
            }
            if (((e.to.ty & 0xFF) == ENUMTY.Tsarray) || ((e.to.ty & 0xFF) == ENUMTY.Tarray) && ((e1.type.ty & 0xFF) == ENUMTY.Tsarray) || ((e1.type.ty & 0xFF) == ENUMTY.Tarray) && !isSafePointerCast(e1.type.nextOf(), e.to.nextOf()))
            {
                e.error(new BytePtr("array cast from `%s` to `%s` is not supported at compile time"), e1.type.toChars(), e.to.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            if (((e.to.ty & 0xFF) == ENUMTY.Tsarray))
                e1 = resolveSlice(e1, null);
            if (((e.to.toBasetype().ty & 0xFF) == ENUMTY.Tbool) && ((e1.type.ty & 0xFF) == ENUMTY.Tpointer))
            {
                (this.pue) = new UnionExp(new IntegerExp(e.loc, (e1.op & 0xFF) != 13, e.to));
                this.result = (this.pue).exp();
                return ;
            }
            this.result = ctfeCast(this.pue, e.loc, e.type, e.to, e1);
        }

        public  void visit(AssertExp e) {
            Expression e1 = interpret(this.pue, e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e1))
                return ;
            if (isTrueBool(e1))
            {
            }
            else if (e1.isBool(false))
            {
                if (e.msg != null)
                {
                    UnionExp ue = null;
                    this.result = interpret(ue, e.msg, this.istate, CtfeGoal.ctfeNeedRvalue);
                    if (this.exceptionOrCant(this.result))
                        return ;
                    e.error(new BytePtr("`%s`"), this.result.toChars());
                }
                else
                    e.error(new BytePtr("`%s` failed"), e.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            else
            {
                e.error(new BytePtr("`%s` is not a compile time boolean expression"), e1.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            this.result = e1;
            return ;
        }

        public  void visit(PtrExp e) {
            {
                SymOffExp soe1 = e.e1.isSymOffExp();
                if ((soe1) != null)
                    if ((soe1.offset == 0L) && (soe1.var.isVarDeclaration() != null) && isFloatIntPaint(e.type, soe1.var.type))
                    {
                        this.result = paintFloatInt(this.pue, getVarExp(e.loc, this.istate, soe1.var, CtfeGoal.ctfeNeedRvalue), e.type);
                        return ;
                    }
            }
            {
                CastExp ce1 = e.e1.isCastExp();
                if ((ce1) != null)
                    {
                        AddrExp ae11 = ce1.e1.isAddrExp();
                        if ((ae11) != null)
                        {
                            Expression x = ae11.e1;
                            if (isFloatIntPaint(e.type, x.type))
                            {
                                this.result = paintFloatInt(this.pue, interpret(x, this.istate, CtfeGoal.ctfeNeedRvalue), e.type);
                                return ;
                            }
                        }
                    }
            }
            {
                AddExp ae = e.e1.isAddExp();
                if ((ae) != null)
                {
                    if (((ae.e1.op & 0xFF) == 19) && ((ae.e2.op & 0xFF) == 135))
                    {
                        AddrExp ade = (AddrExp)ae.e1;
                        Expression ex = interpret(ade.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
                        if (this.exceptionOrCant(ex))
                            return ;
                        {
                            StructLiteralExp se = ex.isStructLiteralExp();
                            if ((se) != null)
                            {
                                long offset = ae.e2.toInteger();
                                this.result = se.getField(e.type, (int)offset);
                                if (this.result != null)
                                    return ;
                            }
                        }
                    }
                }
            }
            this.result = interpret(e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(this.result))
                return ;
            if (((this.result.op & 0xFF) == 161))
                return ;
            {
                SymOffExp soe = this.result.isSymOffExp();
                if ((soe) != null)
                {
                    if ((soe.offset == 0L) && (soe.var.isFuncDeclaration() != null))
                        return ;
                    e.error(new BytePtr("cannot dereference pointer to static variable `%s` at compile time"), soe.var.toChars());
                    this.result = CTFEExp.cantexp;
                    return ;
                }
            }
            if (((this.result.op & 0xFF) != 19))
            {
                if (((this.result.op & 0xFF) == 13))
                    e.error(new BytePtr("dereference of null pointer `%s`"), e.e1.toChars());
                else
                    e.error(new BytePtr("dereference of invalid pointer `%s`"), this.result.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            this.result = ((AddrExp)this.result).e1;
            if (((this.result.op & 0xFF) == 31) && ((e.type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray))
            {
                return ;
            }
            this.result = interpret(this.pue, this.result, this.istate, this.goal);
            if (this.exceptionOrCant(this.result))
                return ;
        }

        public  void visit(DotVarExp e) {
            Expression ex = interpret(e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(ex))
                return ;
            {
                FuncDeclaration f = e.var.isFuncDeclaration();
                if ((f) != null)
                {
                    if ((pequals(ex, e.e1)))
                        this.result = e;
                    else
                    {
                        (this.pue) = new UnionExp(new DotVarExp(e.loc, ex, f, false));
                        this.result = (this.pue).exp();
                        this.result.type = e.type;
                    }
                    return ;
                }
            }
            VarDeclaration v = e.var.isVarDeclaration();
            if (v == null)
            {
                e.error(new BytePtr("CTFE internal error: `%s`"), e.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            if (((ex.op & 0xFF) == 13))
            {
                if (((ex.type.toBasetype().ty & 0xFF) == ENUMTY.Tclass))
                    e.error(new BytePtr("class `%s` is `null` and cannot be dereferenced"), e.e1.toChars());
                else
                    e.error(new BytePtr("CTFE internal error: null this `%s`"), e.e1.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            if (((ex.op & 0xFF) != 49) && ((ex.op & 0xFF) != 50))
            {
                e.error(new BytePtr("`%s.%s` is not yet implemented at compile time"), e.e1.toChars(), e.var.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            StructLiteralExp se = null;
            int i = 0;
            if (((ex.op & 0xFF) == 50))
            {
                se = ((ClassReferenceExp)ex).value;
                i = ((ClassReferenceExp)ex).findFieldIndexByName(v);
            }
            else
            {
                se = (StructLiteralExp)ex;
                i = findFieldIndexByName(se.sd, v);
            }
            if ((i == -1))
            {
                e.error(new BytePtr("couldn't find field `%s` of type `%s` in `%s`"), v.toChars(), e.type.toChars(), se.toChars());
                this.result = CTFEExp.cantexp;
                return ;
            }
            if ((this.goal == CtfeGoal.ctfeNeedLvalue))
            {
                Expression ev = (se.elements).get(i);
                if ((ev == null) || ((ev.op & 0xFF) == 128))
                    se.elements.set(i, voidInitLiteral(e.type, v).copy());
                if ((pequals(e.e1, ex)))
                    this.result = e;
                else
                {
                    (this.pue) = new UnionExp(new DotVarExp(e.loc, ex, v));
                    this.result = (this.pue).exp();
                    this.result.type = e.type;
                }
                return ;
            }
            this.result = (se.elements).get(i);
            if (this.result == null)
            {
                if ((v.type.size() == 0L))
                    this.result = voidInitLiteral(e.type, v).copy();
                else
                {
                    e.error(new BytePtr("Internal Compiler Error: null field `%s`"), v.toChars());
                    this.result = CTFEExp.cantexp;
                    return ;
                }
            }
            {
                VoidInitExp vie = this.result.isVoidInitExp();
                if ((vie) != null)
                {
                    BytePtr s = pcopy(vie.var.toChars());
                    if (v.overlapped)
                    {
                        e.error(new BytePtr("reinterpretation through overlapped field `%s` is not allowed in CTFE"), s);
                        this.result = CTFEExp.cantexp;
                        return ;
                    }
                    e.error(new BytePtr("cannot read uninitialized variable `%s` in CTFE"), s);
                    this.result = CTFEExp.cantexp;
                    return ;
                }
            }
            if (((v.type.ty & 0xFF) != (this.result.type.ty & 0xFF)) && ((v.type.ty & 0xFF) == ENUMTY.Tsarray))
            {
                TypeSArray tsa = (TypeSArray)v.type;
                int len = (int)tsa.dim.toInteger();
                UnionExp ue = null;
                this.result = createBlockDuplicatedArrayLiteral(ue, ex.loc, v.type, ex, len);
                if ((pequals(this.result, ue.exp())))
                    this.result = ue.copy();
                se.elements.set(i, this.result);
            }
        }

        public  void visit(RemoveExp e) {
            Expression agg = interpret(e.e1, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(agg))
                return ;
            Expression index = interpret(e.e2, this.istate, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(index))
                return ;
            if (((agg.op & 0xFF) == 13))
            {
                this.result = CTFEExp.voidexp;
                return ;
            }
            AssocArrayLiteralExp aae = agg.isAssocArrayLiteralExp();
            DArray<Expression> keysx = aae.keys;
            DArray<Expression> valuesx = aae.values;
            int removed = 0;
            {
                Slice<Expression> __r987 = (valuesx).opSlice().copy();
                int __key986 = 0;
                for (; (__key986 < __r987.getLength());__key986 += 1) {
                    Expression evalue = __r987.get(__key986);
                    int j = __key986;
                    Expression ekey = (keysx).get(j);
                    int eq = ctfeEqual(e.loc, TOK.equal, ekey, index);
                    if (eq != 0)
                        removed += 1;
                    else if ((removed != 0))
                    {
                        keysx.set(j - removed, ekey);
                        valuesx.set(j - removed, evalue);
                    }
                }
            }
            (valuesx).length = (valuesx).length - removed;
            (keysx).length = (keysx).length - removed;
            (this.pue) = new UnionExp(new IntegerExp(e.loc, removed != 0 ? 1 : 0, Type.tbool));
            this.result = (this.pue).exp();
        }

        public  void visit(ClassReferenceExp e) {
            this.result = e;
        }

        public  void visit(VoidInitExp e) {
            e.error(new BytePtr("CTFE internal error: trying to read uninitialized variable"));
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ThrownExceptionExp e) {
            throw new AssertionError("Unreachable code!");
        }


        public Interpreter() {}

        public Interpreter copy() {
            Interpreter that = new Interpreter();
            that.istate = this.istate;
            that.goal = this.goal;
            that.result = this.result;
            that.pue = this.pue;
            return that;
        }
    }
    public static Expression interpret(UnionExp pue, Expression e, InterState istate, int goal) {
        if (e == null)
            return null;
        Interpreter v = new Interpreter(pue, istate, goal);
        e.accept(v);
        Expression ex = v.result;
        assert((goal == CtfeGoal.ctfeNeedNothing) || (ex != null));
        return ex;
    }

    public static Expression interpret(Expression e, InterState istate, int goal) {
        UnionExp ue = null;
        Expression result = interpret(ue, e, istate, goal);
        if ((pequals(result, ue.exp())))
            result = ue.copy();
        return result;
    }

    public static Expression interpret(UnionExp pue, Statement s, InterState istate) {
        if (s == null)
            return null;
        Interpreter v = new Interpreter(pue, istate, CtfeGoal.ctfeNeedNothing);
        s.accept(v);
        return v.result;
    }

    public static Expression interpret(Statement s, InterState istate) {
        UnionExp ue = null;
        Expression result = interpret(ue, s, istate);
        if ((pequals(result, ue.exp())))
            result = ue.copy();
        return result;
    }

    public static Expression scrubReturnValue(Loc loc, Expression e) {
        Function2<Expression,Boolean,Boolean> isVoid = new Function2<Expression,Boolean,Boolean>(){
            public Boolean invoke(Expression e, Boolean checkArrayType){
                if (((e.op & 0xFF) == 128))
                    return true;
                Function1<DArray<Expression>,Boolean> isEntirelyVoid = new Function1<DArray<Expression>,Boolean>(){
                    public Boolean invoke(DArray<Expression> elems){
                        {
                            Slice<Expression> __r988 = (elems).opSlice().copy();
                            int __key989 = 0;
                            for (; (__key989 < __r988.getLength());__key989 += 1) {
                                Expression e = __r988.get(__key989);
                                if ((e != null) && !isVoid.invoke(e, false))
                                    return false;
                            }
                        }
                        return true;
                    }
                };
                {
                    StructLiteralExp sle = e.isStructLiteralExp();
                    if ((sle) != null)
                        return isEntirelyVoid.invoke(sle.elements);
                }
                if (checkArrayType && ((e.type.ty & 0xFF) != ENUMTY.Tsarray))
                    return false;
                {
                    ArrayLiteralExp ale = e.isArrayLiteralExp();
                    if ((ale) != null)
                        return isEntirelyVoid.invoke(ale.elements);
                }
                return false;
            }
        };
        Function2<DArray<Expression>,Boolean,Expression> scrubArray = new Function2<DArray<Expression>,Boolean,Expression>(){
            public Expression invoke(DArray<Expression> elems, Boolean structlit){
                {
                    Slice<Expression> __r990 = (elems).opSlice().copy();
                    int __key991 = 0;
                    for (; (__key991 < __r990.getLength());__key991 += 1) {
                        Expression e = __r990.get(__key991);
                        if (e == null)
                            continue;
                        if (structlit && isVoid.invoke(e, true))
                        {
                            e = null;
                        }
                        else
                        {
                            e = scrubReturnValue(loc, e);
                            if (CTFEExp.isCantExp(e) || ((e.op & 0xFF) == 127))
                                return e;
                        }
                    }
                }
                return null;
            }
        };
        Function1<StructLiteralExp,Expression> scrubSE = new Function1<StructLiteralExp,Expression>(){
            public Expression invoke(StructLiteralExp sle){
                sle.ownedByCtfe = OwnedBy.code;
                if ((sle.stageflags & 1) == 0)
                {
                    int old = sle.stageflags;
                    sle.stageflags |= 1;
                    {
                        Expression ex = scrubArray.invoke(sle.elements, true);
                        if ((ex) != null)
                            return ex;
                    }
                    sle.stageflags = old;
                }
                return null;
            }
        };
        if (((e.op & 0xFF) == 50))
        {
            StructLiteralExp sle = ((ClassReferenceExp)e).value;
            {
                Expression ex = scrubSE.invoke(sle);
                if ((ex) != null)
                    return ex;
            }
        }
        else {
            VoidInitExp vie = e.isVoidInitExp();
            if ((vie) != null)
            {
                error(loc, new BytePtr("uninitialized variable `%s` cannot be returned from CTFE"), vie.var.toChars());
                return new ErrorExp();
            }
        }
        e = resolveSlice(e, null);
        {
            StructLiteralExp sle = e.isStructLiteralExp();
            if ((sle) != null)
            {
                {
                    Expression ex = scrubSE.invoke(sle);
                    if ((ex) != null)
                        return ex;
                }
            }
            else {
                StringExp se = e.isStringExp();
                if ((se) != null)
                {
                    se.ownedByCtfe = OwnedBy.code;
                }
                else {
                    ArrayLiteralExp ale = e.isArrayLiteralExp();
                    if ((ale) != null)
                    {
                        ale.ownedByCtfe = OwnedBy.code;
                        {
                            Expression ex = scrubArray.invoke(ale.elements, false);
                            if ((ex) != null)
                                return ex;
                        }
                    }
                    else {
                        AssocArrayLiteralExp aae = e.isAssocArrayLiteralExp();
                        if ((aae) != null)
                        {
                            aae.ownedByCtfe = OwnedBy.code;
                            {
                                Expression ex = scrubArray.invoke(aae.keys, false);
                                if ((ex) != null)
                                    return ex;
                            }
                            {
                                Expression ex = scrubArray.invoke(aae.values, false);
                                if ((ex) != null)
                                    return ex;
                            }
                            aae.type = toBuiltinAAType(aae.type);
                        }
                        else {
                            VectorExp ve = e.isVectorExp();
                            if ((ve) != null)
                            {
                                ve.ownedByCtfe = OwnedBy.code;
                                {
                                    ArrayLiteralExp ale = ve.e1.isArrayLiteralExp();
                                    if ((ale) != null)
                                    {
                                        ale.ownedByCtfe = OwnedBy.code;
                                        {
                                            Expression ex = scrubArray.invoke(ale.elements, false);
                                            if ((ex) != null)
                                                return ex;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return e;
    }

    public static Expression scrubCacheValue(Expression e) {
        if (e == null)
            return e;
        Function1<DArray<Expression>,Expression> scrubArrayCache = new Function1<DArray<Expression>,Expression>(){
            public Expression invoke(DArray<Expression> elems){
                {
                    Slice<Expression> __r992 = (elems).opSlice().copy();
                    int __key993 = 0;
                    for (; (__key993 < __r992.getLength());__key993 += 1) {
                        Expression e = __r992.get(__key993);
                        e = scrubCacheValue(e);
                    }
                }
                return null;
            }
        };
        Function1<StructLiteralExp,Expression> scrubSE = new Function1<StructLiteralExp,Expression>(){
            public Expression invoke(StructLiteralExp sle){
                sle.ownedByCtfe = OwnedBy.cache;
                if ((sle.stageflags & 1) == 0)
                {
                    int old = sle.stageflags;
                    sle.stageflags |= 1;
                    {
                        Expression ex = scrubArrayCache.invoke(sle.elements);
                        if ((ex) != null)
                            return ex;
                    }
                    sle.stageflags = old;
                }
                return null;
            }
        };
        if (((e.op & 0xFF) == 50))
        {
            {
                Expression ex = scrubSE.invoke(((ClassReferenceExp)e).value);
                if ((ex) != null)
                    return ex;
            }
        }
        else {
            StructLiteralExp sle = e.isStructLiteralExp();
            if ((sle) != null)
            {
                {
                    Expression ex = scrubSE.invoke(sle);
                    if ((ex) != null)
                        return ex;
                }
            }
            else {
                StringExp se = e.isStringExp();
                if ((se) != null)
                {
                    se.ownedByCtfe = OwnedBy.cache;
                }
                else {
                    ArrayLiteralExp ale = e.isArrayLiteralExp();
                    if ((ale) != null)
                    {
                        ale.ownedByCtfe = OwnedBy.cache;
                        {
                            Expression ex = scrubArrayCache.invoke(ale.elements);
                            if ((ex) != null)
                                return ex;
                        }
                    }
                    else {
                        AssocArrayLiteralExp aae = e.isAssocArrayLiteralExp();
                        if ((aae) != null)
                        {
                            aae.ownedByCtfe = OwnedBy.cache;
                            {
                                Expression ex = scrubArrayCache.invoke(aae.keys);
                                if ((ex) != null)
                                    return ex;
                            }
                            {
                                Expression ex = scrubArrayCache.invoke(aae.values);
                                if ((ex) != null)
                                    return ex;
                            }
                        }
                        else {
                            VectorExp ve = e.isVectorExp();
                            if ((ve) != null)
                            {
                                ve.ownedByCtfe = OwnedBy.cache;
                                {
                                    ArrayLiteralExp ale = ve.e1.isArrayLiteralExp();
                                    if ((ale) != null)
                                    {
                                        ale.ownedByCtfe = OwnedBy.cache;
                                        {
                                            Expression ex = scrubArrayCache.invoke(ale.elements);
                                            if ((ex) != null)
                                                return ex;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return e;
    }

    public static Expression interpret_length(UnionExp pue, InterState istate, Expression earg) {
        earg = interpret(pue, earg, istate, CtfeGoal.ctfeNeedRvalue);
        if (exceptionOrCantInterpret(earg))
            return earg;
        long len = 0L;
        {
            AssocArrayLiteralExp aae = earg.isAssocArrayLiteralExp();
            if ((aae) != null)
                len = (long)(aae.keys).length;
            else
                assert(((earg.op & 0xFF) == 13));
        }
        (pue) = new UnionExp(new IntegerExp(earg.loc, len, Type.tsize_t));
        return (pue).exp();
    }

    public static Expression interpret_keys(UnionExp pue, InterState istate, Expression earg, Type returnType) {
        earg = interpret(pue, earg, istate, CtfeGoal.ctfeNeedRvalue);
        if (exceptionOrCantInterpret(earg))
            return earg;
        if (((earg.op & 0xFF) == 13))
        {
            (pue) = new UnionExp(new NullExp(earg.loc, earg.type));
            return (pue).exp();
        }
        if (((earg.op & 0xFF) != 48) && ((earg.type.toBasetype().ty & 0xFF) != ENUMTY.Taarray))
            return null;
        AssocArrayLiteralExp aae = earg.isAssocArrayLiteralExp();
        ArrayLiteralExp ae = new ArrayLiteralExp(aae.loc, returnType, aae.keys);
        ae.ownedByCtfe = aae.ownedByCtfe;
        pue.opAssign(copyLiteral(ae));
        return (pue).exp();
    }

    public static Expression interpret_values(UnionExp pue, InterState istate, Expression earg, Type returnType) {
        earg = interpret(pue, earg, istate, CtfeGoal.ctfeNeedRvalue);
        if (exceptionOrCantInterpret(earg))
            return earg;
        if (((earg.op & 0xFF) == 13))
        {
            (pue) = new UnionExp(new NullExp(earg.loc, earg.type));
            return (pue).exp();
        }
        if (((earg.op & 0xFF) != 48) && ((earg.type.toBasetype().ty & 0xFF) != ENUMTY.Taarray))
            return null;
        AssocArrayLiteralExp aae = earg.isAssocArrayLiteralExp();
        ArrayLiteralExp ae = new ArrayLiteralExp(aae.loc, returnType, aae.values);
        ae.ownedByCtfe = aae.ownedByCtfe;
        pue.opAssign(copyLiteral(ae));
        return (pue).exp();
    }

    public static Expression interpret_dup(UnionExp pue, InterState istate, Expression earg) {
        earg = interpret(pue, earg, istate, CtfeGoal.ctfeNeedRvalue);
        if (exceptionOrCantInterpret(earg))
            return earg;
        if (((earg.op & 0xFF) == 13))
        {
            (pue) = new UnionExp(new NullExp(earg.loc, earg.type));
            return (pue).exp();
        }
        if (((earg.op & 0xFF) != 48) && ((earg.type.toBasetype().ty & 0xFF) != ENUMTY.Taarray))
            return null;
        AssocArrayLiteralExp aae = copyLiteral(earg).copy().isAssocArrayLiteralExp();
        {
            int i = 0;
            for (; (i < (aae.keys).length);i++){
                {
                    Expression e = evaluatePostblit(istate, (aae.keys).get(i));
                    if ((e) != null)
                        return e;
                }
                {
                    Expression e = evaluatePostblit(istate, (aae.values).get(i));
                    if ((e) != null)
                        return e;
                }
            }
        }
        aae.type = earg.type.mutableOf();
        return aae;
    }

    public static Expression interpret_aaApply(UnionExp pue, InterState istate, Expression aa, Expression deleg) {
        aa = interpret(aa, istate, CtfeGoal.ctfeNeedRvalue);
        if (exceptionOrCantInterpret(aa))
            return aa;
        if (((aa.op & 0xFF) != 48))
        {
            (pue) = new UnionExp(new IntegerExp(deleg.loc, 0, Type.tsize_t));
            return (pue).exp();
        }
        FuncDeclaration fd = null;
        Expression pthis = null;
        {
            DelegateExp de = deleg.isDelegateExp();
            if ((de) != null)
            {
                fd = de.func;
                pthis = de.e1;
            }
            else {
                FuncExp fe = deleg.isFuncExp();
                if ((fe) != null)
                    fd = fe.fd;
            }
        }
        assert((fd != null) && (fd.fbody != null));
        assert(fd.parameters != null);
        int numParams = (fd.parameters).length;
        assert((numParams == 1) || (numParams == 2));
        Parameter fparam = __dop994.get(numParams - 1);
        boolean wantRefValue = 0L != (fparam.storageClass & 2101248L);
        DArray<Expression> args = args = new DArray<Expression>(numParams);
        try {
            AssocArrayLiteralExp ae = (AssocArrayLiteralExp)aa;
            if ((ae.keys == null) || ((ae.keys).length == 0))
                return new IntegerExp(deleg.loc, 0L, Type.tsize_t);
            Expression eresult = null;
            {
                int i = 0;
                for (; (i < (ae.keys).length);i += 1){
                    Expression ekey = (ae.keys).get(i);
                    Expression evalue = (ae.values).get(i);
                    if (wantRefValue)
                    {
                        Type t = evalue.type;
                        evalue = new IndexExp(deleg.loc, ae, ekey);
                        evalue.type = t;
                    }
                    args.set(numParams - 1, evalue);
                    if ((numParams == 2))
                        args.set(0, ekey);
                    UnionExp ue = null;
                    eresult = interpretFunction(ue, fd, istate, args, pthis);
                    if ((pequals(eresult, ue.exp())))
                        eresult = ue.copy();
                    if (exceptionOrCantInterpret(eresult))
                        return eresult;
                    if ((eresult.isIntegerExp().getInteger() != 0L))
                        return eresult;
                }
            }
            return eresult;
        }
        finally {
        }
    }

    public static Expression foreachApplyUtf(UnionExp pue, InterState istate, Expression str, Expression deleg, boolean rvs) {
        FuncDeclaration fd = null;
        Expression pthis = null;
        {
            DelegateExp de = deleg.isDelegateExp();
            if ((de) != null)
            {
                fd = de.func;
                pthis = de.e1;
            }
            else {
                FuncExp fe = deleg.isFuncExp();
                if ((fe) != null)
                    fd = fe.fd;
            }
        }
        assert((fd != null) && (fd.fbody != null));
        assert(fd.parameters != null);
        int numParams = (fd.parameters).length;
        assert((numParams == 1) || (numParams == 2));
        Type charType = (fd.parameters).get(numParams - 1).type;
        Type indexType = (numParams == 2) ? (fd.parameters).get(0).type : Type.tsize_t;
        int len = (int)resolveArrayLength(str);
        if ((len == 0))
        {
            (pue) = new UnionExp(new IntegerExp(deleg.loc, 0, indexType));
            return (pue).exp();
        }
        str = resolveSlice(str, null);
        StringExp se = str.isStringExp();
        ArrayLiteralExp ale = str.isArrayLiteralExp();
        if ((se == null) && (ale == null))
        {
            str.error(new BytePtr("CTFE internal error: cannot foreach `%s`"), str.toChars());
            return CTFEExp.cantexp;
        }
        DArray<Expression> args = args = new DArray<Expression>(numParams);
        try {
            Expression eresult = null;
            ByteSlice utf8buf = new ByteSlice(new byte[4]);
            CharSlice utf16buf = new CharSlice(new char[2]);
            int start = rvs ? len : 0;
            int end = rvs ? 0 : len;
            {
                IntRef indx = ref(start);
                for (; (indx.value != end);){
                    BytePtr errmsg = null;
                    IntRef rawvalue = ref(0x0ffff);
                    int currentIndex = indx.value;
                    if (ale != null)
                    {
                        int buflen = 1;
                        IntRef n = ref(1);
                        int sz = (int)ale.type.nextOf().size();
                        switch (sz)
                        {
                            case 1:
                                if (rvs)
                                {
                                    indx.value -= 1;
                                    buflen = 1;
                                    for (; (indx.value > 0) && (buflen < 4);){
                                        Expression r = (ale.elements).get(indx.value);
                                        byte x = (byte)r.isIntegerExp().getInteger();
                                        if ((((x & 0xFF) & 192) != 128))
                                            break;
                                        buflen += 1;
                                    }
                                }
                                else
                                    buflen = (indx.value + 4 > len) ? len - indx.value : 4;
                                {
                                    int i = 0;
                                    for (; (i < buflen);i += 1){
                                        Expression r_1 = (ale.elements).get(indx.value + i);
                                        utf8buf.set(i, (byte)r_1.isIntegerExp().getInteger());
                                    }
                                }
                                n.value = 0;
                                errmsg = pcopy(utf_decodeChar(ptr(utf8buf), buflen, n, rawvalue));
                                break;
                            case 2:
                                if (rvs)
                                {
                                    indx.value -= 1;
                                    buflen = 1;
                                    Expression r_3 = (ale.elements).get(indx.value);
                                    int x_1 = (int)r_3.isIntegerExp().getInteger();
                                    if ((indx.value > 0) && ((int)x_1 >= 56320) && ((int)x_1 <= 57343))
                                    {
                                        indx.value -= 1;
                                        buflen += 1;
                                    }
                                }
                                else
                                    buflen = (indx.value + 2 > len) ? len - indx.value : 2;
                                {
                                    int i_1 = 0;
                                    for (; (i_1 < buflen);i_1 += 1){
                                        Expression r_2 = (ale.elements).get(indx.value + i_1);
                                        utf16buf.set(i_1, (char)(int)r_2.isIntegerExp().getInteger());
                                    }
                                }
                                n.value = 0;
                                errmsg = pcopy(utf_decodeWchar(ptr(utf16buf), buflen, n, rawvalue));
                                break;
                            case 4:
                                {
                                    if (rvs)
                                        indx.value -= 1;
                                    Expression r_4 = (ale.elements).get(indx.value);
                                    rawvalue.value = (int)r_4.isIntegerExp().getInteger();
                                    n.value = 1;
                                }
                                break;
                            default:
                            throw new AssertionError("Unreachable code!");
                        }
                        if (!rvs)
                            indx.value += n.value;
                    }
                    else
                    {
                        int saveindx = 0;
                        switch ((se.sz & 0xFF))
                        {
                            case 1:
                                if (rvs)
                                {
                                    indx.value -= 1;
                                    for (; (indx.value > 0) && ((se.getCodeUnit(indx.value) & 192) == 128);) {
                                        indx.value -= 1;
                                    }
                                    saveindx = indx.value;
                                }
                                errmsg = pcopy(utf_decodeChar(se.string, se.len, indx, rawvalue));
                                if (rvs)
                                    indx.value = saveindx;
                                break;
                            case 2:
                                if (rvs)
                                {
                                    indx.value -= 1;
                                    int wc = se.getCodeUnit(indx.value);
                                    if ((wc >= 56320) && (wc <= 57343))
                                        indx.value -= 1;
                                    saveindx = indx.value;
                                }
                                errmsg = pcopy(utf_decodeWchar(se.wstring, se.len, indx, rawvalue));
                                if (rvs)
                                    indx.value = saveindx;
                                break;
                            case 4:
                                if (rvs)
                                    indx.value -= 1;
                                rawvalue.value = se.getCodeUnit(indx.value);
                                if (!rvs)
                                    indx.value += 1;
                                break;
                            default:
                            throw new AssertionError("Unreachable code!");
                        }
                    }
                    if (errmsg != null)
                    {
                        deleg.error(new BytePtr("`%s`"), errmsg);
                        return CTFEExp.cantexp;
                    }
                    int charlen = 1;
                    switch (charType.size())
                    {
                        case 1L:
                            charlen = utf_codeLengthChar(rawvalue.value);
                            utf_encodeChar(ptr(utf8buf), rawvalue.value);
                            break;
                        case 2L:
                            charlen = utf_codeLengthWchar(rawvalue.value);
                            utf_encodeWchar(ptr(utf16buf), rawvalue.value);
                            break;
                        case 4L:
                            break;
                        default:
                        throw new AssertionError("Unreachable code!");
                    }
                    if (rvs)
                        currentIndex = indx.value;
                    if ((numParams == 2))
                        args.set(0, new IntegerExp(deleg.loc, (long)currentIndex, indexType));
                    Expression val = null;
                    {
                        int __key997 = 0;
                        int __limit998 = charlen;
                        for (; (__key997 < __limit998);__key997 += 1) {
                            int k = __key997;
                            int codepoint = 0x0ffff;
                            switch (charType.size())
                            {
                                case 1L:
                                    codepoint = (utf8buf.get(k) & 0xFF);
                                    break;
                                case 2L:
                                    codepoint = (int)utf16buf.get(k);
                                    break;
                                case 4L:
                                    codepoint = rawvalue.value;
                                    break;
                                default:
                                throw new AssertionError("Unreachable code!");
                            }
                            val = new IntegerExp(str.loc, (long)codepoint, charType);
                            args.set(numParams - 1, val);
                            UnionExp ue = null;
                            eresult = interpretFunction(ue, fd, istate, args, pthis);
                            if ((pequals(eresult, ue.exp())))
                                eresult = ue.copy();
                            if (exceptionOrCantInterpret(eresult))
                                return eresult;
                            if ((eresult.isIntegerExp().getInteger() != 0L))
                                return eresult;
                        }
                    }
                }
            }
            return eresult;
        }
        finally {
        }
    }

    public static Expression evaluateIfBuiltin(UnionExp pue, InterState istate, Loc loc, FuncDeclaration fd, DArray<Expression> arguments, Expression pthis) {
        Expression e = null;
        int nargs = arguments != null ? (arguments).length : 0;
        if (pthis == null)
        {
            if ((isBuiltin(fd) == BUILTIN.yes))
            {
                DArray<Expression> args = args = new DArray<Expression>(nargs);
                try {
                    {
                        Slice<Expression> __r1001 = args.opSlice().copy();
                        int __key1000 = 0;
                        for (; (__key1000 < __r1001.getLength());__key1000 += 1) {
                            Expression arg = __r1001.get(__key1000);
                            int i = __key1000;
                            Expression earg = (arguments).get(i);
                            earg = interpret(earg, istate, CtfeGoal.ctfeNeedRvalue);
                            if (exceptionOrCantInterpret(earg))
                                return earg;
                            arg = earg;
                        }
                    }
                    e = eval_builtin(loc, fd, args);
                    if (e == null)
                    {
                        error(loc, new BytePtr("cannot evaluate unimplemented builtin `%s` at compile time"), fd.toChars());
                        e = CTFEExp.cantexp;
                    }
                }
                finally {
                }
            }
        }
        if (pthis == null)
        {
            if ((nargs == 1) || (nargs == 3))
            {
                Expression firstarg = (arguments).get(0);
                {
                    TypeAArray firstAAtype = firstarg.type.toBasetype().isTypeAArray();
                    if ((firstAAtype) != null)
                    {
                        Identifier id = fd.ident;
                        if ((nargs == 1))
                        {
                            if ((pequals(id, Id.aaLen)))
                                return interpret_length(pue, istate, firstarg);
                            if ((pequals(fd.toParent2().ident, Id.object)))
                            {
                                if ((pequals(id, Id.keys)))
                                    return interpret_keys(pue, istate, firstarg, firstAAtype.index.arrayOf());
                                if ((pequals(id, Id.values)))
                                    return interpret_values(pue, istate, firstarg, firstAAtype.nextOf().arrayOf());
                                if ((pequals(id, Id.rehash)))
                                    return interpret(pue, firstarg, istate, CtfeGoal.ctfeNeedRvalue);
                                if ((pequals(id, Id.dup)))
                                    return interpret_dup(pue, istate, firstarg);
                            }
                        }
                        else
                        {
                            if ((pequals(id, Id._aaApply)))
                                return interpret_aaApply(pue, istate, firstarg, (arguments).data.get(2));
                            if ((pequals(id, Id._aaApply2)))
                                return interpret_aaApply(pue, istate, firstarg, (arguments).data.get(2));
                        }
                    }
                }
            }
        }
        if ((pthis != null) && (fd.fbody == null) && (fd.isCtorDeclaration() != null) && (fd.parent != null) && (fd.parent.parent != null) && (pequals(fd.parent.parent.ident, Id.object)))
        {
            if (((pthis.op & 0xFF) == 50) && (pequals(fd.parent.ident, Id.Throwable)))
            {
                StructLiteralExp se = ((ClassReferenceExp)pthis).value;
                assert(((arguments).length <= (se.elements).length));
                {
                    Slice<Expression> __r1003 = (arguments).opSlice().copy();
                    int __key1002 = 0;
                    for (; (__key1002 < __r1003.getLength());__key1002 += 1) {
                        Expression arg = __r1003.get(__key1002);
                        int i = __key1002;
                        Expression elem = interpret(arg, istate, CtfeGoal.ctfeNeedRvalue);
                        if (exceptionOrCantInterpret(elem))
                            return elem;
                        se.elements.set(i, elem);
                    }
                }
                return CTFEExp.voidexp;
            }
        }
        if ((nargs == 1) && (pthis == null) && (pequals(fd.ident, Id.criticalenter)) || (pequals(fd.ident, Id.criticalexit)))
        {
            return CTFEExp.voidexp;
        }
        if (pthis == null)
        {
            int idlen = fd.ident.asString().getLength();
            BytePtr id = pcopy(fd.ident.toChars());
            if ((nargs == 2) && (idlen == 10) || (idlen == 11) && (strncmp(id, new BytePtr("_aApply"), 7) == 0))
            {
                boolean rvs = idlen == 11;
                byte c = id.get(idlen - 3);
                byte s = id.get(idlen - 2);
                byte n = id.get(idlen - 1);
                if (((n & 0xFF) == 49) || ((n & 0xFF) == 50) && ((c & 0xFF) == 99) || ((c & 0xFF) == 119) || ((c & 0xFF) == 100) && ((s & 0xFF) == 99) || ((s & 0xFF) == 119) || ((s & 0xFF) == 100) && ((c & 0xFF) != (s & 0xFF)))
                {
                    Expression str = (arguments).get(0);
                    str = interpret(str, istate, CtfeGoal.ctfeNeedRvalue);
                    if (exceptionOrCantInterpret(str))
                        return str;
                    return foreachApplyUtf(pue, istate, str, (arguments).get(1), rvs);
                }
            }
        }
        return e;
    }

    public static Expression evaluatePostblit(InterState istate, Expression e) {
        TypeStruct ts = e.type.baseElemOf().isTypeStruct();
        if (ts == null)
            return null;
        StructDeclaration sd = ts.sym;
        if (sd.postblit == null)
            return null;
        {
            ArrayLiteralExp ale = e.isArrayLiteralExp();
            if ((ale) != null)
            {
                {
                    Slice<Expression> __r1004 = (ale.elements).opSlice().copy();
                    int __key1005 = 0;
                    for (; (__key1005 < __r1004.getLength());__key1005 += 1) {
                        Expression elem = __r1004.get(__key1005);
                        {
                            Expression ex = evaluatePostblit(istate, elem);
                            if ((ex) != null)
                                return ex;
                        }
                    }
                }
                return null;
            }
        }
        if (((e.op & 0xFF) == 49))
        {
            UnionExp ue = null;
            e = interpretFunction(ue, sd.postblit, istate, null, e);
            if ((pequals(e, ue.exp())))
                e = ue.copy();
            if (exceptionOrCantInterpret(e))
                return e;
            return null;
        }
        throw new AssertionError("Unreachable code!");
    }

    public static Expression evaluateDtor(InterState istate, Expression e) {
        TypeStruct ts = e.type.baseElemOf().isTypeStruct();
        if (ts == null)
            return null;
        StructDeclaration sd = ts.sym;
        if (sd.dtor == null)
            return null;
        UnionExp ue = null;
        {
            ArrayLiteralExp ale = e.isArrayLiteralExp();
            if ((ale) != null)
            {
                {
                    Slice<Expression> __r1006 = (ale.elements).opSlice().copy();
                    int __key1007 = __r1006.getLength();
                    for (; __key1007-- != 0;) {
                        Expression elem = __r1006.get(__key1007);
                        e = evaluateDtor(istate, elem);
                    }
                }
            }
            else if (((e.op & 0xFF) == 49))
            {
                e = interpretFunction(ue, sd.dtor, istate, null, e);
            }
            else
                throw new AssertionError("Unreachable code!");
        }
        if (exceptionOrCantInterpret(e))
        {
            if ((pequals(e, ue.exp())))
                e = ue.copy();
            return e;
        }
        return null;
    }

    public static boolean hasValue(VarDeclaration vd) {
        if ((vd.ctfeAdrOnStack == -1))
            return false;
        return null != getValue(vd);
    }

    public static void setValueWithoutChecking(VarDeclaration vd, Expression newval) {
        ctfeStack.setValue(vd, newval);
    }

    public static void setValue(VarDeclaration vd, Expression newval) {
        assert((vd.storage_class & 2101248L) != 0 ? isCtfeReferenceValid(newval) : isCtfeValueValid(newval));
        ctfeStack.setValue(vd, newval);
    }

}
