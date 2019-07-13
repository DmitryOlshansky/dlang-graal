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
        private Ref<Ptr<CompiledCtfeFunction>> ccf = ref(null);
        public  VarWalker(Ptr<CompiledCtfeFunction> ccf) {
            Ref<Ptr<CompiledCtfeFunction>> ccf_ref = ref(ccf);
            super();
            this.ccf.value = ccf_ref.value;
        }

        public  void visit(Expression e) {
        }

        public  void visit(ErrorExp e) {
            if ((global.gag.value != 0) && ((this.ccf.value.get()).func.value != null))
            {
                this.stop.value = true;
                return ;
            }
            error(e.loc.value, new BytePtr("CTFE internal error: ErrorExp in `%s`\n"), (this.ccf.value.get()).func.value != null ? (this.ccf.value.get()).func.value.loc.value.toChars(global.params.showColumns.value) : (this.ccf.value.get()).callingloc.toChars(global.params.showColumns.value));
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(DeclarationExp e) {
            Ref<VarDeclaration> v = ref(e.declaration.value.isVarDeclaration());
            if (v.value == null)
            {
                return ;
            }
            Ref<TupleDeclaration> td = ref(v.value.toAlias().isTupleDeclaration());
            if (td.value != null)
            {
                if (td.value.objects.value == null)
                {
                    return ;
                }
                {
                    Ref<Slice<RootObject>> __r933 = ref((td.value.objects.value.get()).opSlice().copy());
                    IntRef __key934 = ref(0);
                    for (; (__key934.value < __r933.value.getLength());__key934.value += 1) {
                        Ref<RootObject> o = ref(__r933.value.get(__key934.value));
                        Ref<Expression> ex = ref(isExpression(o.value));
                        Ref<DsymbolExp> s = ref(ex.value != null ? ex.value.isDsymbolExp() : null);
                        assert(s.value != null);
                        Ref<VarDeclaration> v2 = ref(s.value.s.value.isVarDeclaration());
                        assert(v2.value != null);
                        if (!v2.value.isDataseg() || v2.value.isCTFE())
                        {
                            (this.ccf.value.get()).onDeclaration(v2.value);
                        }
                    }
                }
            }
            else if (!(v.value.isDataseg() || ((v.value.storage_class.value & 8388608L) != 0)) || v.value.isCTFE())
            {
                (this.ccf.value.get()).onDeclaration(v.value);
            }
            Ref<Dsymbol> s = ref(v.value.toAlias());
            if ((pequals(s.value, v.value)) && !v.value.isStatic() && (v.value._init.value != null))
            {
                Ref<ExpInitializer> ie = ref(v.value._init.value.isExpInitializer());
                if (ie.value != null)
                {
                    (this.ccf.value.get()).onExpression(ie.value.exp.value);
                }
            }
        }

        public  void visit(IndexExp e) {
            if (e.lengthVar.value != null)
            {
                (this.ccf.value.get()).onDeclaration(e.lengthVar.value);
            }
        }

        public  void visit(SliceExp e) {
            if (e.lengthVar.value != null)
            {
                (this.ccf.value.get()).onDeclaration(e.lengthVar.value);
            }
        }


        public VarWalker() {}
    }
    private static class RecursiveBlock
    {
        private Ref<Ptr<InterState>> istate = ref(null);
        private Ref<Expression> newval = ref(null);
        private Ref<Boolean> refCopy = ref(false);
        private Ref<Boolean> needsPostblit = ref(false);
        private Ref<Boolean> needsDtor = ref(false);
        public  Expression assignTo(ArrayLiteralExp ae) {
            Ref<ArrayLiteralExp> ae_ref = ref(ae);
            return this.assignTo(ae_ref.value, 0, (ae_ref.value.elements.value.get()).length.value);
        }

        public  Expression assignTo(ArrayLiteralExp ae, int lwr, int upr) {
            IntRef lwr_ref = ref(lwr);
            IntRef upr_ref = ref(upr);
            Ptr<DArray<Expression>> w = ae.elements.value;
            assert(((ae.type.value.ty.value & 0xFF) == ENUMTY.Tsarray) || ((ae.type.value.ty.value & 0xFF) == ENUMTY.Tarray));
            Ref<Boolean> directblk = ref(((TypeArray)ae.type.value).next.value.equivalent(this.newval.value.type.value));
            {
                IntRef k = ref(lwr_ref.value);
                for (; (k.value < upr_ref.value);k.value++){
                    if (!directblk.value && (((w.get()).get(k.value).op.value & 0xFF) == 47))
                    {
                        {
                            Ref<Expression> ex = ref(this.assignTo((ArrayLiteralExp)(w.get()).get(k.value)));
                            if ((ex.value) != null)
                            {
                                return ex.value;
                            }
                        }
                    }
                    else if (this.refCopy.value)
                    {
                        w.get().set(k.value, this.newval.value);
                    }
                    else if (!this.needsPostblit.value && !this.needsDtor.value)
                    {
                        assignInPlace((w.get()).get(k.value), this.newval.value);
                    }
                    else
                    {
                        Ref<Expression> oldelem = ref((w.get()).get(k.value));
                        Ref<Expression> tmpelem = ref(this.needsDtor.value ? copyLiteral(oldelem.value).copy() : null);
                        assignInPlace(oldelem.value, this.newval.value);
                        if (this.needsPostblit.value)
                        {
                            {
                                Ref<Expression> ex = ref(evaluatePostblit(this.istate.value, oldelem.value));
                                if ((ex.value) != null)
                                {
                                    return ex.value;
                                }
                            }
                        }
                        if (this.needsDtor.value)
                        {
                            {
                                Ref<Expression> ex = ref(evaluateDtor(this.istate.value, tmpelem.value));
                                if ((ex.value) != null)
                                {
                                    return ex.value;
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }

        private Object this = null;
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
        public RecursiveBlock(Ptr<InterState> istate, Expression newval, boolean refCopy, boolean needsPostblit, boolean needsDtor, Object this) {
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
                switch (__dispatch0 != 0 ? __dispatch0 : (e.op.value & 0xFF))
                {
                    case 135:
                    case 140:
                    case 147:
                    case 13:
                    case 121:
                        if (((e.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                        {
                            return new ErrorExp();
                        }
                        /*goto case*/{ __dispatch0 = 127; continue dispatched_0; }
                    case 127:
                        __dispatch0 = 0;
                        return e;
                    default:
                    break;
                }
            } while(__dispatch0 != 0);
        }
        assert(e.type.value != null);
        if (((e.type.value.ty.value & 0xFF) == ENUMTY.Terror))
        {
            return new ErrorExp();
        }
        CompiledCtfeFunction ctfeCodeGlobal = ctfeCodeGlobal = new CompiledCtfeFunction(null);
        ctfeCodeGlobal.callingloc = e.loc.value.copy();
        ctfeCodeGlobal.onExpression(e);
        Expression result = interpret(e, null, CtfeGoal.ctfeNeedRvalue);
        if (!CTFEExp.isCantExp(result))
        {
            result = scrubReturnValue(e.loc.value, result);
        }
        if (CTFEExp.isCantExp(result))
        {
            result = new ErrorExp();
        }
        return result;
    }

    public static Expression ctfeInterpretForPragmaMsg(Expression e) {
        if (((e.op.value & 0xFF) == 127) || ((e.op.value & 0xFF) == 20))
        {
            return e;
        }
        {
            VarExp ve = e.isVarExp();
            if ((ve) != null)
            {
                if (ve.var.value.isFuncDeclaration() != null)
                {
                    return e;
                }
            }
        }
        TupleExp tup = e.isTupleExp();
        if (tup == null)
        {
            return e.ctfeInterpret();
        }
        Ptr<DArray<Expression>> expsx = null;
        {
            Slice<Expression> __r932 = (tup.exps.value.get()).opSlice().copy();
            int __key931 = 0;
            for (; (__key931 < __r932.getLength());__key931 += 1) {
                Expression g = __r932.get(__key931);
                int i = __key931;
                Expression h = ctfeInterpretForPragmaMsg(g);
                if ((!pequals(h, g)))
                {
                    if (expsx == null)
                    {
                        expsx = (tup.exps.value.get()).copy();
                    }
                    expsx.get().set(i, h);
                }
            }
        }
        if (expsx != null)
        {
            TupleExp te = new TupleExp(e.loc.value, expsx);
            expandTuples(te.exps.value);
            te.type.value = new TypeTuple(te.exps.value);
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
        public Ptr<CompiledCtfeFunction> pimpl = null;
        public CompiledCtfeFunctionPimpl(){
        }
        public CompiledCtfeFunctionPimpl copy(){
            CompiledCtfeFunctionPimpl r = new CompiledCtfeFunctionPimpl();
            r.pimpl = pimpl;
            return r;
        }
        public CompiledCtfeFunctionPimpl(Ptr<CompiledCtfeFunction> pimpl) {
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
        public Expression localThis = null;
        public  int stackPointer() {
            return this.values.length.value;
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
            this.localThis = this.savedThis.get(this.savedThis.length.value - 1);
            this.popAll(this.framepointer);
            this.framepointer = oldframe;
            this.frames.setDim(this.frames.length - 1);
            this.savedThis.setDim(this.savedThis.length.value - 1);
        }

        public  boolean isInCurrentFrame(VarDeclaration v) {
            if (v.isDataseg() && !v.isCTFE())
            {
                return false;
            }
            return v.ctfeAdrOnStack >= this.framepointer;
        }

        public  Expression getValue(VarDeclaration v) {
            if (v.isDataseg() || ((v.storage_class.value & 8388608L) != 0) && !v.isCTFE())
            {
                assert((v.ctfeAdrOnStack >= 0) && (v.ctfeAdrOnStack < this.globalValues.length.value));
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
            v.ctfeAdrOnStack = this.values.length.value;
            this.vars.push(v);
            this.values.push(null);
        }

        public  void pop(VarDeclaration v) {
            assert(!v.isDataseg() || v.isCTFE());
            assert((v.storage_class.value & 2101248L) == 0);
            int oldid = v.ctfeAdrOnStack;
            v.ctfeAdrOnStack = ((int)this.savedId.get(oldid));
            if ((v.ctfeAdrOnStack == this.values.length.value - 1))
            {
                this.values.pop();
                this.vars.pop();
                this.savedId.pop();
            }
        }

        public  void popAll(int stackpointer) {
            if ((this.stackPointer() > this.maxStackPointer))
            {
                this.maxStackPointer = this.stackPointer();
            }
            assert((this.values.length.value >= stackpointer));
            {
                int i = stackpointer;
                for (; (i < this.values.length.value);i += 1){
                    VarDeclaration v = this.vars.get(i);
                    v.ctfeAdrOnStack = ((int)this.savedId.get(i));
                }
            }
            this.values.setDim(stackpointer);
            this.vars.setDim(stackpointer);
            this.savedId.setDim(stackpointer);
        }

        public  void saveGlobalConstant(VarDeclaration v, Expression e) {
            assert((v._init.value != null) && v.isConst() || v.isImmutable() || ((v.storage_class.value & 8388608L) != 0) && !v.isCTFE());
            v.ctfeAdrOnStack = this.globalValues.length.value;
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
        public Ptr<InterState> caller = null;
        public FuncDeclaration fd = null;
        public Statement start = null;
        public Statement gotoTarget = null;
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
        public InterState(Ptr<InterState> caller, FuncDeclaration fd, Statement start, Statement gotoTarget) {
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
        public Ref<FuncDeclaration> func = ref(null);
        public int numVars = 0;
        public Loc callingloc = new Loc();
        public  CompiledCtfeFunction(FuncDeclaration f) {
            this.func.value = f;
        }

        public  void onDeclaration(VarDeclaration v) {
            this.numVars += 1;
        }

        public  void onExpression(Expression e) {
            // skipping duplicate class VarWalker
            VarWalker v = new VarWalker(ptr(this));
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
        public Ptr<CompiledCtfeFunction> ccf = null;
        public  CtfeCompiler(Ptr<CompiledCtfeFunction> ccf) {
            this.ccf = ccf;
        }

        public  void visit(Statement s) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ExpStatement s) {
            if (s.exp.value != null)
            {
                (this.ccf.get()).onExpression(s.exp.value);
            }
        }

        public  void visit(IfStatement s) {
            (this.ccf.get()).onExpression(s.condition.value);
            if (s.ifbody.value != null)
            {
                this.ctfeCompile(s.ifbody.value);
            }
            if (s.elsebody.value != null)
            {
                this.ctfeCompile(s.elsebody.value);
            }
        }

        public  void visit(ScopeGuardStatement s) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(DoStatement s) {
            (this.ccf.get()).onExpression(s.condition.value);
            if (s._body.value != null)
            {
                this.ctfeCompile(s._body.value);
            }
        }

        public  void visit(WhileStatement s) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ForStatement s) {
            if (s._init.value != null)
            {
                this.ctfeCompile(s._init.value);
            }
            if (s.condition.value != null)
            {
                (this.ccf.get()).onExpression(s.condition.value);
            }
            if (s.increment.value != null)
            {
                (this.ccf.get()).onExpression(s.increment.value);
            }
            if (s._body.value != null)
            {
                this.ctfeCompile(s._body.value);
            }
        }

        public  void visit(ForeachStatement s) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(SwitchStatement s) {
            (this.ccf.get()).onExpression(s.condition.value);
            {
                Slice<CaseStatement> __r935 = (s.cases.get()).opSlice().copy();
                int __key936 = 0;
                for (; (__key936 < __r935.getLength());__key936 += 1) {
                    CaseStatement cs = __r935.get(__key936);
                    (this.ccf.get()).onExpression(cs.exp);
                }
            }
            if (s._body.value != null)
            {
                this.ctfeCompile(s._body.value);
            }
        }

        public  void visit(CaseStatement s) {
            if (s.statement.value != null)
            {
                this.ctfeCompile(s.statement.value);
            }
        }

        public  void visit(GotoDefaultStatement s) {
        }

        public  void visit(GotoCaseStatement s) {
        }

        public  void visit(SwitchErrorStatement s) {
        }

        public  void visit(ReturnStatement s) {
            if (s.exp.value != null)
            {
                (this.ccf.get()).onExpression(s.exp.value);
            }
        }

        public  void visit(BreakStatement s) {
        }

        public  void visit(ContinueStatement s) {
        }

        public  void visit(WithStatement s) {
            if (((s.exp.value.op.value & 0xFF) == 203) || ((s.exp.value.op.value & 0xFF) == 20))
            {
            }
            else
            {
                (this.ccf.get()).onDeclaration(s.wthis);
                (this.ccf.get()).onExpression(s.exp.value);
            }
            if (s._body.value != null)
            {
                this.ctfeCompile(s._body.value);
            }
        }

        public  void visit(TryCatchStatement s) {
            if (s._body.value != null)
            {
                this.ctfeCompile(s._body.value);
            }
            {
                Slice<Catch> __r937 = (s.catches.get()).opSlice().copy();
                int __key938 = 0;
                for (; (__key938 < __r937.getLength());__key938 += 1) {
                    Catch ca = __r937.get(__key938);
                    if (ca.var != null)
                    {
                        (this.ccf.get()).onDeclaration(ca.var);
                    }
                    if (ca.handler.value != null)
                    {
                        this.ctfeCompile(ca.handler.value);
                    }
                }
            }
        }

        public  void visit(ThrowStatement s) {
            (this.ccf.get()).onExpression(s.exp);
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
        assert(!fd.semantic3Errors.value);
        assert((fd.semanticRun.value == PASS.semantic3done));
        fd.ctfeCode.pimpl = refPtr(new CompiledCtfeFunction(fd));
        if (fd.parameters.value != null)
        {
            Type tb = fd.type.value.toBasetype().isTypeFunction();
            assert(tb != null);
            {
                Slice<VarDeclaration> __r939 = (fd.parameters.value.get()).opSlice().copy();
                int __key940 = 0;
                for (; (__key940 < __r939.getLength());__key940 += 1) {
                    VarDeclaration v = __r939.get(__key940);
                    (fd.ctfeCode.pimpl.get()).onDeclaration(v);
                }
            }
        }
        if (fd.vresult != null)
        {
            (fd.ctfeCode.pimpl.get()).onDeclaration(fd.vresult);
        }
        CtfeCompiler v = new CtfeCompiler(fd.ctfeCode.pimpl);
        v.ctfeCompile(fd.fbody.value);
    }

    public static Expression interpretFunction(Ptr<UnionExp> pue, FuncDeclaration fd, Ptr<InterState> istate, Ptr<DArray<Expression>> arguments, Expression thisarg) {
        assert(pue != null);
        if ((fd.semanticRun.value == PASS.semantic3))
        {
            fd.error(new BytePtr("circular dependency. Functions cannot be interpreted while being compiled"));
            return CTFEExp.cantexp.value;
        }
        if (!fd.functionSemantic3())
        {
            return CTFEExp.cantexp.value;
        }
        if ((fd.semanticRun.value < PASS.semantic3done))
        {
            return CTFEExp.cantexp.value;
        }
        if (fd.ctfeCode.pimpl == null)
        {
            ctfeCompile(fd);
        }
        Type tb = fd.type.value.toBasetype();
        assert(((tb.ty.value & 0xFF) == ENUMTY.Tfunction));
        TypeFunction tf = (TypeFunction)tb;
        if ((tf.parameterList.varargs.value != VarArg.none) && (arguments != null) && (fd.parameters.value != null) && ((arguments.get()).length.value != (fd.parameters.value.get()).length.value) || (fd.parameters.value == null) && ((arguments.get()).length.value != 0))
        {
            fd.error(new BytePtr("C-style variadic functions are not yet implemented in CTFE"));
            return CTFEExp.cantexp.value;
        }
        if (fd.isNested() && (fd.toParentLocal().isFuncDeclaration() != null) && (thisarg == null) && (istate != null))
        {
            thisarg = ctfeStack.getThis();
        }
        if (fd.needThis() && (thisarg == null))
        {
            fd.error(new BytePtr("need `this` to access member `%s`"), fd.toChars());
            return CTFEExp.cantexp.value;
        }
        int dim = arguments != null ? (arguments.get()).length.value : 0;
        assert(((fd.parameters.value != null ? (fd.parameters.value.get()).length.value : 0) == dim));
        DArray<Expression> eargs = eargs = new DArray<Expression>(dim);
        try {
            {
                int i = 0;
                for (; (i < dim);i++){
                    Expression earg = (arguments.get()).get(i);
                    Parameter fparam = tf.parameterList.get(i);
                    if ((fparam.storageClass.value & 2101248L) != 0)
                    {
                        if ((istate == null) && ((fparam.storageClass.value & 4096L) != 0))
                        {
                            earg.error(new BytePtr("global `%s` cannot be passed as an `out` parameter at compile time"), earg.toChars());
                            return CTFEExp.cantexp.value;
                        }
                        earg = interpret(earg, istate, CtfeGoal.ctfeNeedLvalue);
                        if (CTFEExp.isCantExp(earg))
                        {
                            return earg;
                        }
                    }
                    else if ((fparam.storageClass.value & 8192L) != 0)
                    {
                    }
                    else
                    {
                        Type ta = fparam.type.value.toBasetype();
                        if (((ta.ty.value & 0xFF) == ENUMTY.Tsarray))
                        {
                            {
                                AddrExp eaddr = earg.isAddrExp();
                                if ((eaddr) != null)
                                {
                                    earg = eaddr.e1.value;
                                }
                            }
                        }
                        earg = interpret(earg, istate, CtfeGoal.ctfeNeedRvalue);
                        if (CTFEExp.isCantExp(earg))
                        {
                            return earg;
                        }
                        if (((earg.op.value & 0xFF) == 49) && ((fparam.storageClass.value & 1048580L) == 0))
                        {
                            earg = copyLiteral(earg).copy();
                        }
                    }
                    if (((earg.op.value & 0xFF) == 51))
                    {
                        if (istate != null)
                        {
                            return earg;
                        }
                        ((ThrownExceptionExp)earg).generateUncaughtError();
                        return CTFEExp.cantexp.value;
                    }
                    eargs.set(i, earg);
                }
            }
            Ref<InterState> istatex = ref(new InterState());
            istatex.value.caller = istate;
            istatex.value.fd = fd;
            if (fd.isThis2.value)
            {
                Expression arg0 = thisarg;
                if ((arg0 != null) && ((arg0.type.value.ty.value & 0xFF) == ENUMTY.Tstruct))
                {
                    Type t = arg0.type.value.pointerTo();
                    arg0 = new AddrExp(arg0.loc.value, arg0);
                    arg0.type.value = t;
                }
                Ptr<DArray<Expression>> elements = refPtr(new DArray<Expression>(2));
                elements.get().set(0, arg0);
                elements.get().set(1, ctfeStack.getThis());
                Type t2 = Type.tvoidptr.value.sarrayOf(2L);
                Loc loc = thisarg != null ? thisarg.loc.value : fd.loc.value.copy();
                thisarg = new ArrayLiteralExp(loc, t2, elements);
                thisarg = new AddrExp(loc, thisarg);
                thisarg.type.value = t2.pointerTo();
            }
            ctfeStack.startFrame(thisarg);
            if ((fd.vthis.value != null) && (thisarg != null))
            {
                ctfeStack.push(fd.vthis.value);
                setValue(fd.vthis.value, thisarg);
            }
            {
                int i = 0;
                for (; (i < dim);i++){
                    Expression earg = eargs.get(i);
                    Parameter fparam = tf.parameterList.get(i);
                    VarDeclaration v = (fd.parameters.value.get()).get(i);
                    ctfeStack.push(v);
                    if (((fparam.storageClass.value & 2101248L) != 0) && ((earg.op.value & 0xFF) == 26) && (pequals(((VarExp)earg).var.value.toParent2(), fd)))
                    {
                        VarDeclaration vx = ((VarExp)earg).var.value.isVarDeclaration();
                        if (vx == null)
                        {
                            fd.error(new BytePtr("cannot interpret `%s` as a `ref` parameter"), earg.toChars());
                            return CTFEExp.cantexp.value;
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
            {
                ctfeStack.push(fd.vresult);
            }
            CtfeStatus.callDepth += 1;
            if ((CtfeStatus.callDepth > CtfeStatus.maxCallDepth))
            {
                CtfeStatus.maxCallDepth = CtfeStatus.callDepth;
            }
            Expression e = null;
            for (; 1 != 0;){
                if ((CtfeStatus.callDepth > 1000))
                {
                    global.gag.value = 0;
                    fd.error(new BytePtr("CTFE recursion limit exceeded"));
                    e = CTFEExp.cantexp.value;
                    break;
                }
                e = interpret(pue, fd.fbody.value, ptr(istatex));
                if (CTFEExp.isCantExp(e))
                if (istatex.value.start != null)
                {
                    fd.error(new BytePtr("CTFE internal error: failed to resume at statement `%s`"), istatex.value.start.toChars());
                    return CTFEExp.cantexp.value;
                }
                if (CTFEExp.isGotoExp(e))
                {
                    istatex.value.start = istatex.value.gotoTarget;
                    istatex.value.gotoTarget = null;
                }
                else
                {
                    assert((e == null) || ((e.op.value & 0xFF) != 192) && ((e.op.value & 0xFF) != 191));
                    break;
                }
            }
            if ((e == null) && ((tf.next.value.ty.value & 0xFF) == ENUMTY.Tvoid))
            {
                e = CTFEExp.voidexp;
            }
            if (tf.isref.value && ((e.op.value & 0xFF) == 26) && (pequals(((VarExp)e).var.value, fd.vthis.value)))
            {
                e = thisarg;
            }
            if (tf.isref.value && fd.isThis2.value && ((e.op.value & 0xFF) == 62))
            {
                IndexExp ie = (IndexExp)e;
                PtrExp pe = ie.e1.value.isPtrExp();
                VarExp ve = pe == null ? null : pe.e1.value.isVarExp();
                if ((ve != null) && (pequals(ve.var.value, fd.vthis.value)))
                {
                    IntegerExp ne = ie.e2.value.isIntegerExp();
                    assert(ne != null);
                    assert(((thisarg.op.value & 0xFF) == 19));
                    e = ((AddrExp)thisarg).e1.value;
                    e = (((ArrayLiteralExp)e).elements.value.get()).get((int)ne.getInteger());
                    if (((e.op.value & 0xFF) == 19))
                    {
                        e = ((AddrExp)e).e1.value;
                    }
                }
            }
            assert((e != null));
            CtfeStatus.callDepth -= 1;
            ctfeStack.endFrame();
            if ((istate == null) && ((e.op.value & 0xFF) == 51))
            {
                if ((pequals(e, (pue.get()).exp())))
                {
                    e = (pue.get()).copy();
                }
                ((ThrownExceptionExp)e).generateUncaughtError();
                e = CTFEExp.cantexp.value;
            }
            return e;
        }
        finally {
        }
    }

    public static class Interpreter extends Visitor
    {
        public Ref<Ptr<InterState>> istate = ref(null);
        public int goal = 0;
        public Ref<Expression> result = ref(null);
        public Ptr<UnionExp> pue = null;
        public  Interpreter(Ptr<UnionExp> pue, Ptr<InterState> istate, int goal) {
            this.pue = pue;
            this.istate.value = istate;
            this.goal = goal;
        }

        public  boolean exceptionOrCant(Expression e) {
            if (exceptionOrCantInterpret(e))
            {
                this.result.value = ((e.op.value & 0xFF) == 233) ? CTFEExp.cantexp.value : e;
                return true;
            }
            return false;
        }

        public static Ptr<DArray<Expression>> copyArrayOnWrite(Ptr<DArray<Expression>> exps, Ptr<DArray<Expression>> original) {
            if ((exps == original))
            {
                if (original == null)
                {
                    exps = refPtr(new DArray<Expression>());
                }
                else
                {
                    exps = (original.get()).copy();
                }
                CtfeStatus.numArrayAllocs += 1;
            }
            return exps;
        }

        public  void visit(Statement s) {
            if ((this.istate.value.get()).start != null)
            {
                if ((!pequals((this.istate.value.get()).start, s)))
                {
                    return ;
                }
                (this.istate.value.get()).start = null;
            }
            s.error(new BytePtr("statement `%s` cannot be interpreted at compile time"), s.toChars());
            this.result.value = CTFEExp.cantexp.value;
        }

        public  void visit(ExpStatement s) {
            if ((this.istate.value.get()).start != null)
            {
                if ((!pequals((this.istate.value.get()).start, s)))
                {
                    return ;
                }
                (this.istate.value.get()).start = null;
            }
            Expression e = interpret(this.pue, s.exp.value, this.istate.value, CtfeGoal.ctfeNeedNothing);
            if (this.exceptionOrCant(e))
            {
                return ;
            }
        }

        public  void visit(CompoundStatement s) {
            if ((pequals((this.istate.value.get()).start, s)))
            {
                (this.istate.value.get()).start = null;
            }
            int dim = s.statements != null ? (s.statements.get()).length.value : 0;
            {
                int __key942 = 0;
                int __limit943 = dim;
                for (; (__key942 < __limit943);__key942 += 1) {
                    int i = __key942;
                    Statement sx = (s.statements.get()).get(i);
                    this.result.value = interpret(this.pue, sx, this.istate.value);
                    if (this.result.value != null)
                    {
                        break;
                    }
                }
            }
        }

        public  void visit(UnrolledLoopStatement s) {
            if ((pequals((this.istate.value.get()).start, s)))
            {
                (this.istate.value.get()).start = null;
            }
            int dim = s.statements != null ? (s.statements.get()).length.value : 0;
            {
                int __key944 = 0;
                int __limit945 = dim;
                for (; (__key944 < __limit945);__key944 += 1) {
                    int i = __key944;
                    Statement sx = (s.statements.get()).get(i);
                    Expression e = interpret(this.pue, sx, this.istate.value);
                    if (e == null)
                    {
                        continue;
                    }
                    if (this.exceptionOrCant(e))
                    {
                        return ;
                    }
                    if (((e.op.value & 0xFF) == 191))
                    {
                        if (((this.istate.value.get()).gotoTarget != null) && (!pequals((this.istate.value.get()).gotoTarget, s)))
                        {
                            this.result.value = e;
                            return ;
                        }
                        (this.istate.value.get()).gotoTarget = null;
                        this.result.value = null;
                        return ;
                    }
                    if (((e.op.value & 0xFF) == 192))
                    {
                        if (((this.istate.value.get()).gotoTarget != null) && (!pequals((this.istate.value.get()).gotoTarget, s)))
                        {
                            this.result.value = e;
                            return ;
                        }
                        (this.istate.value.get()).gotoTarget = null;
                        continue;
                    }
                    this.result.value = e;
                    break;
                }
            }
        }

        public  void visit(IfStatement s) {
            if ((pequals((this.istate.value.get()).start, s)))
            {
                (this.istate.value.get()).start = null;
            }
            if ((this.istate.value.get()).start != null)
            {
                Expression e = null;
                e = interpret(s.ifbody.value, this.istate.value);
                if ((e == null) && ((this.istate.value.get()).start != null))
                {
                    e = interpret(s.elsebody.value, this.istate.value);
                }
                this.result.value = e;
                return ;
            }
            Ref<UnionExp> ue = ref(null);
            Expression e = interpret(ptr(ue), s.condition.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            assert(e != null);
            if (this.exceptionOrCant(e))
            {
                return ;
            }
            if (isTrueBool(e))
            {
                this.result.value = interpret(this.pue, s.ifbody.value, this.istate.value);
            }
            else if (e.isBool(false))
            {
                this.result.value = interpret(this.pue, s.elsebody.value, this.istate.value);
            }
            else
            {
                this.result.value = CTFEExp.cantexp.value;
            }
        }

        public  void visit(ScopeStatement s) {
            if ((pequals((this.istate.value.get()).start, s)))
            {
                (this.istate.value.get()).start = null;
            }
            this.result.value = interpret(this.pue, s.statement.value, this.istate.value);
        }

        public static boolean stopPointersEscaping(Loc loc, Expression e) {
            if (!e.type.value.hasPointers())
            {
                return true;
            }
            if (isPointer(e.type.value))
            {
                Expression x = e;
                {
                    AddrExp eaddr = e.isAddrExp();
                    if ((eaddr) != null)
                    {
                        x = eaddr.e1.value;
                    }
                }
                VarDeclaration v = null;
                for (; ((x.op.value & 0xFF) == 26) && ((v = ((VarExp)x).var.value.isVarDeclaration()) != null);){
                    if ((v.storage_class.value & 2097152L) != 0)
                    {
                        x = getValue(v);
                        {
                            AddrExp eaddr = e.isAddrExp();
                            if ((eaddr) != null)
                            {
                                eaddr.e1.value = x;
                            }
                        }
                        continue;
                    }
                    if (ctfeStack.isInCurrentFrame(v))
                    {
                        error(loc, new BytePtr("returning a pointer to a local stack variable"));
                        return false;
                    }
                    else
                    {
                        break;
                    }
                }
            }
            {
                StructLiteralExp se = e.isStructLiteralExp();
                if ((se) != null)
                {
                    return stopPointersEscapingFromArray(loc, se.elements.value);
                }
            }
            {
                ArrayLiteralExp ale = e.isArrayLiteralExp();
                if ((ale) != null)
                {
                    return stopPointersEscapingFromArray(loc, ale.elements.value);
                }
            }
            {
                AssocArrayLiteralExp aae = e.isAssocArrayLiteralExp();
                if ((aae) != null)
                {
                    if (!stopPointersEscapingFromArray(loc, aae.keys.value))
                    {
                        return false;
                    }
                    return stopPointersEscapingFromArray(loc, aae.values.value);
                }
            }
            return true;
        }

        public static boolean stopPointersEscapingFromArray(Loc loc, Ptr<DArray<Expression>> elems) {
            {
                Slice<Expression> __r946 = (elems.get()).opSlice().copy();
                int __key947 = 0;
                for (; (__key947 < __r946.getLength());__key947 += 1) {
                    Expression e = __r946.get(__key947);
                    if ((e != null) && !stopPointersEscaping(loc, e))
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        public  void visit(ReturnStatement s) {
            if ((this.istate.value.get()).start != null)
            {
                if ((!pequals((this.istate.value.get()).start, s)))
                {
                    return ;
                }
                (this.istate.value.get()).start = null;
            }
            if (s.exp.value == null)
            {
                this.result.value = CTFEExp.voidexp;
                return ;
            }
            assert((this.istate.value != null) && ((this.istate.value.get()).fd != null) && ((this.istate.value.get()).fd.type.value != null) && (((this.istate.value.get()).fd.type.value.ty.value & 0xFF) == ENUMTY.Tfunction));
            TypeFunction tf = (TypeFunction)(this.istate.value.get()).fd.type.value;
            if (tf.isref.value)
            {
                this.result.value = interpret(this.pue, s.exp.value, this.istate.value, CtfeGoal.ctfeNeedLvalue);
                return ;
            }
            if ((tf.next.value != null) && ((tf.next.value.ty.value & 0xFF) == ENUMTY.Tdelegate) && ((this.istate.value.get()).fd.closureVars.length.value > 0))
            {
                s.error(new BytePtr("closures are not yet supported in CTFE"));
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            Expression e = interpret(this.pue, s.exp.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e))
            {
                return ;
            }
            if (!stopPointersEscaping(s.loc, e))
            {
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            if (needToCopyLiteral(e))
            {
                e = copyLiteral(e).copy();
            }
            this.result.value = e;
        }

        public static Statement findGotoTarget(Ptr<InterState> istate, Identifier ident) {
            Statement target = null;
            if (ident != null)
            {
                LabelDsymbol label = (istate.get()).fd.searchLabel(ident);
                assert((label != null) && (label.statement != null));
                LabelStatement ls = label.statement;
                target = ls.gotoTarget != null ? ls.gotoTarget : ls.statement.value;
            }
            return target;
        }

        public  void visit(BreakStatement s) {
            if ((this.istate.value.get()).start != null)
            {
                if ((!pequals((this.istate.value.get()).start, s)))
                {
                    return ;
                }
                (this.istate.value.get()).start = null;
            }
            (this.istate.value.get()).gotoTarget = findGotoTarget(this.istate.value, s.ident.value);
            this.result.value = CTFEExp.breakexp;
        }

        public  void visit(ContinueStatement s) {
            if ((this.istate.value.get()).start != null)
            {
                if ((!pequals((this.istate.value.get()).start, s)))
                {
                    return ;
                }
                (this.istate.value.get()).start = null;
            }
            (this.istate.value.get()).gotoTarget = findGotoTarget(this.istate.value, s.ident.value);
            this.result.value = CTFEExp.continueexp;
        }

        public  void visit(WhileStatement s) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(DoStatement s) {
            if ((pequals((this.istate.value.get()).start, s)))
            {
                (this.istate.value.get()).start = null;
            }
            for (; 1 != 0;){
                Expression e = interpret(s._body.value, this.istate.value);
                if ((e == null) && ((this.istate.value.get()).start != null))
                {
                    return ;
                }
                assert((this.istate.value.get()).start == null);
                if (this.exceptionOrCant(e))
                {
                    return ;
                }
                if ((e != null) && ((e.op.value & 0xFF) == 191))
                {
                    if (((this.istate.value.get()).gotoTarget != null) && (!pequals((this.istate.value.get()).gotoTarget, s)))
                    {
                        this.result.value = e;
                        return ;
                    }
                    (this.istate.value.get()).gotoTarget = null;
                    break;
                }
                if ((e != null) && ((e.op.value & 0xFF) == 192))
                {
                    if (((this.istate.value.get()).gotoTarget != null) && (!pequals((this.istate.value.get()).gotoTarget, s)))
                    {
                        this.result.value = e;
                        return ;
                    }
                    (this.istate.value.get()).gotoTarget = null;
                    e = null;
                }
                if (e != null)
                {
                    this.result.value = e;
                    return ;
                }
                Ref<UnionExp> ue = ref(null);
                e = interpret(ptr(ue), s.condition.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e))
                {
                    return ;
                }
                if (e.isConst() == 0)
                {
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                if (e.isBool(false))
                {
                    break;
                }
                assert(isTrueBool(e));
            }
            assert((this.result.value == null));
        }

        public  void visit(ForStatement s) {
            if ((pequals((this.istate.value.get()).start, s)))
            {
                (this.istate.value.get()).start = null;
            }
            Ref<UnionExp> ueinit = ref(null);
            Expression ei = interpret(ptr(ueinit), s._init.value, this.istate.value);
            if (this.exceptionOrCant(ei))
            {
                return ;
            }
            assert(ei == null);
            for (; 1 != 0;){
                if ((s.condition.value != null) && ((this.istate.value.get()).start == null))
                {
                    Ref<UnionExp> ue = ref(null);
                    Expression e = interpret(ptr(ue), s.condition.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                    if (this.exceptionOrCant(e))
                    {
                        return ;
                    }
                    if (e.isBool(false))
                    {
                        break;
                    }
                    assert(isTrueBool(e));
                }
                Expression e = interpret(this.pue, s._body.value, this.istate.value);
                if ((e == null) && ((this.istate.value.get()).start != null))
                {
                    return ;
                }
                assert((this.istate.value.get()).start == null);
                if (this.exceptionOrCant(e))
                {
                    return ;
                }
                if ((e != null) && ((e.op.value & 0xFF) == 191))
                {
                    if (((this.istate.value.get()).gotoTarget != null) && (!pequals((this.istate.value.get()).gotoTarget, s)))
                    {
                        this.result.value = e;
                        return ;
                    }
                    (this.istate.value.get()).gotoTarget = null;
                    break;
                }
                if ((e != null) && ((e.op.value & 0xFF) == 192))
                {
                    if (((this.istate.value.get()).gotoTarget != null) && (!pequals((this.istate.value.get()).gotoTarget, s)))
                    {
                        this.result.value = e;
                        return ;
                    }
                    (this.istate.value.get()).gotoTarget = null;
                    e = null;
                }
                if (e != null)
                {
                    this.result.value = e;
                    return ;
                }
                Ref<UnionExp> uei = ref(null);
                e = interpret(ptr(uei), s.increment.value, this.istate.value, CtfeGoal.ctfeNeedNothing);
                if (this.exceptionOrCant(e))
                {
                    return ;
                }
            }
            assert((this.result.value == null));
        }

        public  void visit(ForeachStatement s) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ForeachRangeStatement s) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(SwitchStatement s) {
            if ((pequals((this.istate.value.get()).start, s)))
            {
                (this.istate.value.get()).start = null;
            }
            if ((this.istate.value.get()).start != null)
            {
                Expression e = interpret(s._body.value, this.istate.value);
                if ((this.istate.value.get()).start != null)
                {
                    return ;
                }
                if (this.exceptionOrCant(e))
                {
                    return ;
                }
                if ((e != null) && ((e.op.value & 0xFF) == 191))
                {
                    if (((this.istate.value.get()).gotoTarget != null) && (!pequals((this.istate.value.get()).gotoTarget, s)))
                    {
                        this.result.value = e;
                        return ;
                    }
                    (this.istate.value.get()).gotoTarget = null;
                    e = null;
                }
                this.result.value = e;
                return ;
            }
            Ref<UnionExp> uecond = ref(null);
            Expression econdition = interpret(ptr(uecond), s.condition.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(econdition))
            {
                return ;
            }
            Statement scase = null;
            if (s.cases != null)
            {
                Slice<CaseStatement> __r948 = (s.cases.get()).opSlice().copy();
                int __key949 = 0;
                for (; (__key949 < __r948.getLength());__key949 += 1) {
                    CaseStatement cs = __r948.get(__key949);
                    Ref<UnionExp> uecase = ref(null);
                    Expression ecase = interpret(ptr(uecase), cs.exp, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                    if (this.exceptionOrCant(ecase))
                    {
                        return ;
                    }
                    if (ctfeEqual(cs.exp.loc.value, TOK.equal, econdition, ecase) != 0)
                    {
                        scase = cs;
                        break;
                    }
                }
            }
            if (scase == null)
            {
                if (s.hasNoDefault != 0)
                {
                    s.error(new BytePtr("no `default` or `case` for `%s` in `switch` statement"), econdition.toChars());
                }
                scase = s.sdefault;
            }
            assert(scase != null);
            (this.istate.value.get()).start = scase;
            Expression e = interpret(this.pue, s._body.value, this.istate.value);
            assert((this.istate.value.get()).start == null);
            if ((e != null) && ((e.op.value & 0xFF) == 191))
            {
                if (((this.istate.value.get()).gotoTarget != null) && (!pequals((this.istate.value.get()).gotoTarget, s)))
                {
                    this.result.value = e;
                    return ;
                }
                (this.istate.value.get()).gotoTarget = null;
                e = null;
            }
            this.result.value = e;
        }

        public  void visit(CaseStatement s) {
            if ((pequals((this.istate.value.get()).start, s)))
            {
                (this.istate.value.get()).start = null;
            }
            this.result.value = interpret(this.pue, s.statement.value, this.istate.value);
        }

        public  void visit(DefaultStatement s) {
            if ((pequals((this.istate.value.get()).start, s)))
            {
                (this.istate.value.get()).start = null;
            }
            this.result.value = interpret(this.pue, s.statement.value, this.istate.value);
        }

        public  void visit(GotoStatement s) {
            if ((this.istate.value.get()).start != null)
            {
                if ((!pequals((this.istate.value.get()).start, s)))
                {
                    return ;
                }
                (this.istate.value.get()).start = null;
            }
            assert((s.label != null) && (s.label.statement != null));
            (this.istate.value.get()).gotoTarget = s.label.statement;
            this.result.value = CTFEExp.gotoexp;
        }

        public  void visit(GotoCaseStatement s) {
            if ((this.istate.value.get()).start != null)
            {
                if ((!pequals((this.istate.value.get()).start, s)))
                {
                    return ;
                }
                (this.istate.value.get()).start = null;
            }
            assert(s.cs != null);
            (this.istate.value.get()).gotoTarget = s.cs;
            this.result.value = CTFEExp.gotoexp;
        }

        public  void visit(GotoDefaultStatement s) {
            if ((this.istate.value.get()).start != null)
            {
                if ((!pequals((this.istate.value.get()).start, s)))
                {
                    return ;
                }
                (this.istate.value.get()).start = null;
            }
            assert((s.sw != null) && (s.sw.sdefault != null));
            (this.istate.value.get()).gotoTarget = s.sw.sdefault;
            this.result.value = CTFEExp.gotoexp;
        }

        public  void visit(LabelStatement s) {
            if ((pequals((this.istate.value.get()).start, s)))
            {
                (this.istate.value.get()).start = null;
            }
            this.result.value = interpret(this.pue, s.statement.value, this.istate.value);
        }

        public  void visit(TryCatchStatement s) {
            if ((pequals((this.istate.value.get()).start, s)))
            {
                (this.istate.value.get()).start = null;
            }
            if ((this.istate.value.get()).start != null)
            {
                Expression e = null;
                e = interpret(this.pue, s._body.value, this.istate.value);
                {
                    Slice<Catch> __r950 = (s.catches.get()).opSlice().copy();
                    int __key951 = 0;
                    for (; (__key951 < __r950.getLength());__key951 += 1) {
                        Catch ca = __r950.get(__key951);
                        if ((e != null) || ((this.istate.value.get()).start == null))
                        {
                            break;
                        }
                        e = interpret(this.pue, ca.handler.value, this.istate.value);
                    }
                }
                this.result.value = e;
                return ;
            }
            Expression e = interpret(s._body.value, this.istate.value);
            if ((e != null) && ((e.op.value & 0xFF) == 51))
            {
                ThrownExceptionExp ex = (ThrownExceptionExp)e;
                Type extype = ex.thrown.originalClass().type.value;
                {
                    Slice<Catch> __r952 = (s.catches.get()).opSlice().copy();
                    int __key953 = 0;
                    for (; (__key953 < __r952.getLength());__key953 += 1) {
                        Catch ca = __r952.get(__key953);
                        Type catype = ca.type.value;
                        if (!catype.equals(extype) && !catype.isBaseOf(extype, null))
                        {
                            continue;
                        }
                        if (ca.var != null)
                        {
                            ctfeStack.push(ca.var);
                            setValue(ca.var, ex.thrown);
                        }
                        e = interpret(ca.handler.value, this.istate.value);
                        if (CTFEExp.isGotoExp(e))
                        {
                            Ref<InterState> istatex = ref(this.istate.value.get().copy());
                            istatex.value.start = (this.istate.value.get()).gotoTarget;
                            istatex.value.gotoTarget = null;
                            Expression eh = interpret(ca.handler.value, ptr(istatex));
                            if (istatex.value.start == null)
                            {
                                (this.istate.value.get()).gotoTarget = null;
                                e = eh;
                            }
                        }
                        break;
                    }
                }
            }
            this.result.value = e;
        }

        public static boolean isAnErrorException(ClassDeclaration cd) {
            return (pequals(cd, ClassDeclaration.errorException.value)) || ClassDeclaration.errorException.value.isBaseOf(cd, null);
        }

        public static ThrownExceptionExp chainExceptions(ThrownExceptionExp oldest, ThrownExceptionExp newest) {
            ClassReferenceExp boss = oldest.thrown;
            int next = 4;
            assert((((boss.value.elements.value.get()).get(4).type.value.ty.value & 0xFF) == ENUMTY.Tclass));
            ClassReferenceExp collateral = newest.thrown;
            if (isAnErrorException(collateral.originalClass()) && !isAnErrorException(boss.originalClass()))
            {
                int bypass = 5;
                if ((((collateral.value.elements.value.get()).get(bypass).type.value.ty.value & 0xFF) == ENUMTY.Tuns32))
                {
                    bypass += 1;
                }
                assert((((collateral.value.elements.value.get()).get(bypass).type.value.ty.value & 0xFF) == ENUMTY.Tclass));
                collateral.value.elements.value.get().set(bypass, boss);
                return newest;
            }
            for (; (((boss.value.elements.value.get()).get(4).op.value & 0xFF) == 50);){
                boss = (ClassReferenceExp)(boss.value.elements.value.get()).get(4);
            }
            boss.value.elements.value.get().set(4, collateral);
            return oldest;
        }

        public  void visit(TryFinallyStatement s) {
            if ((pequals((this.istate.value.get()).start, s)))
            {
                (this.istate.value.get()).start = null;
            }
            if ((this.istate.value.get()).start != null)
            {
                Expression e = null;
                e = interpret(this.pue, s._body.value, this.istate.value);
                this.result.value = e;
                return ;
            }
            Expression ex = interpret(s._body.value, this.istate.value);
            if (CTFEExp.isCantExp(ex))
            {
                this.result.value = ex;
                return ;
            }
            for (; CTFEExp.isGotoExp(ex);){
                Ref<InterState> istatex = ref(this.istate.value.get().copy());
                istatex.value.start = (this.istate.value.get()).gotoTarget;
                istatex.value.gotoTarget = null;
                Expression bex = interpret(s._body.value, ptr(istatex));
                if (istatex.value.start != null)
                {
                    break;
                }
                if (CTFEExp.isCantExp(bex))
                {
                    this.result.value = bex;
                    return ;
                }
                this.istate.value.set(0, istatex.value);
                ex = bex;
            }
            Expression ey = interpret(s.finalbody.value, this.istate.value);
            if (CTFEExp.isCantExp(ey))
            {
                this.result.value = ey;
                return ;
            }
            if ((ey != null) && ((ey.op.value & 0xFF) == 51))
            {
                if ((ex != null) && ((ex.op.value & 0xFF) == 51))
                {
                    ex = chainExceptions((ThrownExceptionExp)ex, (ThrownExceptionExp)ey);
                }
                else
                {
                    ex = ey;
                }
            }
            this.result.value = ex;
        }

        public  void visit(ThrowStatement s) {
            if ((this.istate.value.get()).start != null)
            {
                if ((!pequals((this.istate.value.get()).start, s)))
                {
                    return ;
                }
                (this.istate.value.get()).start = null;
            }
            Expression e = interpret(s.exp, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e))
            {
                return ;
            }
            assert(((e.op.value & 0xFF) == 50));
            this.result.value = new ThrownExceptionExp(s.loc, (ClassReferenceExp)e);
        }

        public  void visit(ScopeGuardStatement s) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(WithStatement s) {
            if ((pequals((this.istate.value.get()).start, s)))
            {
                (this.istate.value.get()).start = null;
            }
            if ((this.istate.value.get()).start != null)
            {
                this.result.value = s._body.value != null ? interpret(s._body.value, this.istate.value) : null;
                return ;
            }
            if (((s.exp.value.op.value & 0xFF) == 203) || ((s.exp.value.op.value & 0xFF) == 20))
            {
                this.result.value = interpret(this.pue, s._body.value, this.istate.value);
                return ;
            }
            Expression e = interpret(s.exp.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e))
            {
                return ;
            }
            if (((s.wthis.type.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((s.exp.value.type.value.ty.value & 0xFF) != ENUMTY.Tpointer))
            {
                e = new AddrExp(s.loc, e, s.wthis.type.value);
            }
            ctfeStack.push(s.wthis);
            setValue(s.wthis, e);
            e = interpret(s._body.value, this.istate.value);
            if (CTFEExp.isGotoExp(e))
            {
                Ref<InterState> istatex = ref(this.istate.value.get().copy());
                istatex.value.start = (this.istate.value.get()).gotoTarget;
                istatex.value.gotoTarget = null;
                Expression ex = interpret(s._body.value, ptr(istatex));
                if (istatex.value.start == null)
                {
                    (this.istate.value.get()).gotoTarget = null;
                    e = ex;
                }
            }
            ctfeStack.pop(s.wthis);
            this.result.value = e;
        }

        public  void visit(AsmStatement s) {
            if ((this.istate.value.get()).start != null)
            {
                if ((!pequals((this.istate.value.get()).start, s)))
                {
                    return ;
                }
                (this.istate.value.get()).start = null;
            }
            s.error(new BytePtr("`asm` statements cannot be interpreted at compile time"));
            this.result.value = CTFEExp.cantexp.value;
        }

        public  void visit(ImportStatement s) {
            if ((this.istate.value.get()).start != null)
            {
                if ((!pequals((this.istate.value.get()).start, s)))
                {
                    return ;
                }
                (this.istate.value.get()).start = null;
            }
        }

        public  void visit(Expression e) {
            e.error(new BytePtr("cannot interpret `%s` at compile time"), e.toChars());
            this.result.value = CTFEExp.cantexp.value;
        }

        public  void visit(ThisExp e) {
            if ((this.goal == CtfeGoal.ctfeNeedLvalue))
            {
                if ((this.istate.value != null) && ((this.istate.value.get()).fd.vthis.value != null))
                {
                    this.result.value = new VarExp(e.loc.value, (this.istate.value.get()).fd.vthis.value, true);
                    if ((this.istate.value.get()).fd.isThis2.value)
                    {
                        this.result.value = new PtrExp(e.loc.value, this.result.value);
                        this.result.value.type.value = Type.tvoidptr.value.sarrayOf(2L);
                        this.result.value = new IndexExp(e.loc.value, this.result.value, literal_B6589FC6AB0DC82C());
                    }
                    this.result.value.type.value = e.type.value;
                }
                else
                {
                    this.result.value = e;
                }
                return ;
            }
            this.result.value = ctfeStack.getThis();
            if (this.result.value != null)
            {
                if ((this.istate.value != null) && (this.istate.value.get()).fd.isThis2.value)
                {
                    assert(((this.result.value.op.value & 0xFF) == 19));
                    this.result.value = ((AddrExp)this.result.value).e1.value;
                    assert(((this.result.value.op.value & 0xFF) == 47));
                    this.result.value = (((ArrayLiteralExp)this.result.value).elements.value.get()).get(0);
                    if (((e.type.value.ty.value & 0xFF) == ENUMTY.Tstruct))
                    {
                        this.result.value = ((AddrExp)this.result.value).e1.value;
                    }
                    return ;
                }
                assert(((this.result.value.op.value & 0xFF) == 49) || ((this.result.value.op.value & 0xFF) == 50));
                return ;
            }
            e.error(new BytePtr("value of `this` is not known at compile time"));
            this.result.value = CTFEExp.cantexp.value;
        }

        public  void visit(NullExp e) {
            this.result.value = e;
        }

        public  void visit(IntegerExp e) {
            this.result.value = e;
        }

        public  void visit(RealExp e) {
            this.result.value = e;
        }

        public  void visit(ComplexExp e) {
            this.result.value = e;
        }

        public  void visit(StringExp e) {
            this.result.value = e;
        }

        public  void visit(FuncExp e) {
            this.result.value = e;
        }

        public  void visit(SymOffExp e) {
            if ((e.var.value.isFuncDeclaration() != null) && (e.offset.value == 0L))
            {
                this.result.value = e;
                return ;
            }
            if (isTypeInfo_Class(e.type.value) && (e.offset.value == 0L))
            {
                this.result.value = e;
                return ;
            }
            if (((e.type.value.ty.value & 0xFF) != ENUMTY.Tpointer))
            {
                e.error(new BytePtr("cannot interpret `%s` at compile time"), e.toChars());
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            Type pointee = ((TypePointer)e.type.value).next.value;
            if (e.var.value.isThreadlocal())
            {
                e.error(new BytePtr("cannot take address of thread-local variable %s at compile time"), e.var.value.toChars());
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            Type fromType = null;
            if (((e.var.value.type.value.ty.value & 0xFF) == ENUMTY.Tarray) || ((e.var.value.type.value.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                fromType = ((TypeArray)e.var.value.type.value).next.value;
            }
            if (e.var.value.isDataseg() && (e.offset.value == 0L) && isSafePointerCast(e.var.value.type.value, pointee) || (fromType != null) && isSafePointerCast(fromType, pointee))
            {
                this.result.value = e;
                return ;
            }
            Expression val = getVarExp(e.loc.value, this.istate.value, e.var.value, this.goal);
            if (this.exceptionOrCant(val))
            {
                return ;
            }
            if (((val.type.value.ty.value & 0xFF) == ENUMTY.Tarray) || ((val.type.value.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                Type elemtype = ((TypeArray)val.type.value).next.value;
                long elemsize = elemtype.size();
                if (((val.type.value.ty.value & 0xFF) == ENUMTY.Tsarray) && ((pointee.ty.value & 0xFF) == ENUMTY.Tarray) && (elemsize == pointee.nextOf().size()))
                {
                    (this.pue) = new UnionExp(new AddrExp(e.loc.value, val, e.type.value));
                    this.result.value = (this.pue.get()).exp();
                    return ;
                }
                if (((val.type.value.ty.value & 0xFF) == ENUMTY.Tsarray) && ((pointee.ty.value & 0xFF) == ENUMTY.Tsarray) && (elemsize == pointee.nextOf().size()))
                {
                    int d = (int)((TypeSArray)pointee).dim.value.toInteger();
                    Expression elwr = new IntegerExp(e.loc.value, e.offset.value / elemsize, Type.tsize_t.value);
                    Expression eupr = new IntegerExp(e.loc.value, e.offset.value / elemsize + (long)d, Type.tsize_t.value);
                    SliceExp se = new SliceExp(e.loc.value, val, elwr, eupr);
                    se.type.value = pointee;
                    (this.pue) = new UnionExp(new AddrExp(e.loc.value, se, e.type.value));
                    this.result.value = (this.pue.get()).exp();
                    return ;
                }
                if (!isSafePointerCast(elemtype, pointee))
                {
                    if ((e.offset.value == 0L) && isSafePointerCast(e.var.value.type.value, pointee))
                    {
                        VarExp ve = new VarExp(e.loc.value, e.var.value, true);
                        ve.type.value = elemtype;
                        (this.pue) = new UnionExp(new AddrExp(e.loc.value, ve, e.type.value));
                        this.result.value = (this.pue.get()).exp();
                        return ;
                    }
                    e.error(new BytePtr("reinterpreting cast from `%s` to `%s` is not supported in CTFE"), val.type.value.toChars(), e.type.value.toChars());
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                long sz = pointee.size();
                long indx = e.offset.value / sz;
                assert((sz * indx == e.offset.value));
                Expression aggregate = null;
                if (((val.op.value & 0xFF) == 47) || ((val.op.value & 0xFF) == 121))
                {
                    aggregate = val;
                }
                else {
                    SliceExp se = val.isSliceExp();
                    if ((se) != null)
                    {
                        aggregate = se.e1.value;
                        Ref<UnionExp> uelwr = ref(null);
                        Expression lwr = interpret(ptr(uelwr), se.lwr.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                        indx += lwr.toInteger();
                    }
                }
                if (aggregate != null)
                {
                    IntegerExp ofs = new IntegerExp(e.loc.value, indx, Type.tsize_t.value);
                    IndexExp ei = new IndexExp(e.loc.value, aggregate, ofs);
                    ei.type.value = elemtype;
                    (this.pue) = new UnionExp(new AddrExp(e.loc.value, ei, e.type.value));
                    this.result.value = (this.pue.get()).exp();
                    return ;
                }
            }
            else if ((e.offset.value == 0L) && isSafePointerCast(e.var.value.type.value, pointee))
            {
                VarExp ve = new VarExp(e.loc.value, e.var.value, true);
                ve.type.value = e.var.value.type.value;
                (this.pue) = new UnionExp(new AddrExp(e.loc.value, ve, e.type.value));
                this.result.value = (this.pue.get()).exp();
                return ;
            }
            e.error(new BytePtr("cannot convert `&%s` to `%s` at compile time"), e.var.value.type.value.toChars(), e.type.value.toChars());
            this.result.value = CTFEExp.cantexp.value;
        }

        public  void visit(AddrExp e) {
            {
                VarExp ve = e.e1.value.isVarExp();
                if ((ve) != null)
                {
                    Declaration decl = ve.var.value;
                    if (decl.isImportedSymbol())
                    {
                        e.error(new BytePtr("cannot take address of imported symbol `%s` at compile time"), decl.toChars());
                        this.result.value = CTFEExp.cantexp.value;
                        return ;
                    }
                    if (decl.isDataseg())
                    {
                        (this.pue) = new UnionExp(new SymOffExp(e.loc.value, ((VarExp)e.e1.value).var.value, 0));
                        this.result.value = (this.pue.get()).exp();
                        this.result.value.type.value = e.type.value;
                        return ;
                    }
                }
            }
            Expression er = interpret(e.e1.value, this.istate.value, CtfeGoal.ctfeNeedLvalue);
            {
                VarExp ve = er.isVarExp();
                if ((ve) != null)
                {
                    if ((pequals(ve.var.value, (this.istate.value.get()).fd.vthis.value)))
                    {
                        er = interpret(er, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                    }
                }
            }
            if (this.exceptionOrCant(er))
            {
                return ;
            }
            (this.pue) = new UnionExp(new AddrExp(e.loc.value, er, e.type.value));
            this.result.value = (this.pue.get()).exp();
        }

        public  void visit(DelegateExp e) {
            {
                VarExp ve1 = e.e1.value.isVarExp();
                if ((ve1) != null)
                {
                    if ((pequals(ve1.var.value, e.func.value)))
                    {
                        this.result.value = e;
                        return ;
                    }
                }
            }
            Expression er = interpret(this.pue, e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(er))
            {
                return ;
            }
            if ((pequals(er, e.e1.value)))
            {
                this.result.value = e;
            }
            else
            {
                er = (pequals(er, (this.pue.get()).exp())) ? (this.pue.get()).copy() : er;
                (this.pue) = new UnionExp(new DelegateExp(e.loc.value, er, e.func.value, false));
                this.result.value = (this.pue.get()).exp();
                this.result.value.type.value = e.type.value;
            }
        }

        public static Expression getVarExp(Loc loc, Ptr<InterState> istate, Declaration d, int goal) {
            Expression e = CTFEExp.cantexp.value;
            {
                VarDeclaration v = d.isVarDeclaration();
                if ((v) != null)
                {
                    if ((pequals(v.ident.value, Id.ctfe.value)))
                    {
                        return new IntegerExp(loc, 1L, Type.tbool.value);
                    }
                    if ((v.originalType.value == null) && (v.semanticRun.value < PASS.semanticdone))
                    {
                        dsymbolSemantic(v, null);
                        if (((v.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                        {
                            return CTFEExp.cantexp.value;
                        }
                    }
                    if (v.isConst() || v.isImmutable() || ((v.storage_class.value & 8388608L) != 0) && !hasValue(v) && (v._init.value != null) && !v.isCTFE())
                    {
                        if (v.inuse.value != 0)
                        {
                            error(loc, new BytePtr("circular initialization of %s `%s`"), v.kind(), v.toPrettyChars(false));
                            return CTFEExp.cantexp.value;
                        }
                        if (v._scope.value != null)
                        {
                            v.inuse.value++;
                            v._init.value = initializerSemantic(v._init.value, v._scope.value, v.type.value, NeedInterpret.INITinterpret);
                            v.inuse.value--;
                        }
                        e = initializerToExpression(v._init.value, v.type.value);
                        if (e == null)
                        {
                            return CTFEExp.cantexp.value;
                        }
                        assert(e.type.value != null);
                        if (((e.op.value & 0xFF) == 95) || ((e.op.value & 0xFF) == 96))
                        {
                            AssignExp ae = (AssignExp)e;
                            e = ae.e2.value;
                        }
                        if (((e.op.value & 0xFF) == 127))
                        {
                        }
                        else if (v.isDataseg() || ((v.storage_class.value & 8388608L) != 0))
                        {
                            e = scrubCacheValue(e);
                            ctfeStack.saveGlobalConstant(v, e);
                        }
                        else
                        {
                            v.inuse.value++;
                            e = interpret(e, istate, CtfeGoal.ctfeNeedRvalue);
                            v.inuse.value--;
                            if (CTFEExp.isCantExp(e) && (global.gag.value == 0) && (CtfeStatus.stackTraceCallsToSuppress == 0))
                            {
                                errorSupplemental(loc, new BytePtr("while evaluating %s.init"), v.toChars());
                            }
                            if (exceptionOrCantInterpret(e))
                            {
                                return e;
                            }
                        }
                    }
                    else if (v.isCTFE() && !hasValue(v))
                    {
                        if ((v._init.value != null) && (v.type.value.size() != 0L))
                        {
                            if (v._init.value.isVoidInitializer() != null)
                            {
                                error(loc, new BytePtr("CTFE internal error: trying to access uninitialized var"));
                                throw new AssertionError("Unreachable code!");
                            }
                            e = initializerToExpression(v._init.value, null);
                        }
                        else
                        {
                            e = v.type.value.defaultInitLiteral(e.loc.value);
                        }
                        e = interpret(e, istate, CtfeGoal.ctfeNeedRvalue);
                    }
                    else if (!(v.isDataseg() || ((v.storage_class.value & 8388608L) != 0)) && !v.isCTFE() && (istate == null))
                    {
                        error(loc, new BytePtr("variable `%s` cannot be read at compile time"), v.toChars());
                        return CTFEExp.cantexp.value;
                    }
                    else
                    {
                        e = hasValue(v) ? getValue(v) : null;
                        if ((e == null) && !v.isCTFE() && v.isDataseg())
                        {
                            error(loc, new BytePtr("static variable `%s` cannot be read at compile time"), v.toChars());
                            return CTFEExp.cantexp.value;
                        }
                        if (e == null)
                        {
                            assert(!((v._init.value != null) && (v._init.value.isVoidInitializer() != null)));
                            error(loc, new BytePtr("variable `%s` cannot be read at compile time"), v.toChars());
                            return CTFEExp.cantexp.value;
                        }
                        {
                            VoidInitExp vie = e.isVoidInitExp();
                            if ((vie) != null)
                            {
                                error(loc, new BytePtr("cannot read uninitialized variable `%s` in ctfe"), v.toPrettyChars(false));
                                errorSupplemental(vie.var.loc.value, new BytePtr("`%s` was uninitialized and used before set"), vie.var.toChars());
                                return CTFEExp.cantexp.value;
                            }
                        }
                        if ((goal != CtfeGoal.ctfeNeedLvalue) && v.isRef() || v.isOut())
                        {
                            e = interpret(e, istate, goal);
                        }
                    }
                    if (e == null)
                    {
                        e = CTFEExp.cantexp.value;
                    }
                }
                else {
                    SymbolDeclaration s = d.isSymbolDeclaration();
                    if ((s) != null)
                    {
                        e = s.dsym.type.value.defaultInitLiteral(loc);
                        if (((e.op.value & 0xFF) == 127))
                        {
                            error(loc, new BytePtr("CTFE failed because of previous errors in `%s.init`"), s.toChars());
                        }
                        e = expressionSemantic(e, null);
                        if (((e.op.value & 0xFF) == 127))
                        {
                            e = CTFEExp.cantexp.value;
                        }
                        else
                        {
                            e = interpret(e, istate, goal);
                        }
                    }
                    else
                    {
                        error(loc, new BytePtr("cannot interpret declaration `%s` at compile time"), d.toChars());
                    }
                }
            }
            return e;
        }

        public  void visit(VarExp e) {
            if (e.var.value.isFuncDeclaration() != null)
            {
                this.result.value = e;
                return ;
            }
            if ((this.goal == CtfeGoal.ctfeNeedLvalue))
            {
                VarDeclaration v = e.var.value.isVarDeclaration();
                if ((v != null) && !v.isDataseg() && !v.isCTFE() && (this.istate.value == null))
                {
                    e.error(new BytePtr("variable `%s` cannot be read at compile time"), v.toChars());
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                if ((v != null) && !hasValue(v))
                {
                    if (!v.isCTFE() && v.isDataseg())
                    {
                        e.error(new BytePtr("static variable `%s` cannot be read at compile time"), v.toChars());
                    }
                    else
                    {
                        e.error(new BytePtr("variable `%s` cannot be read at compile time"), v.toChars());
                    }
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                if ((v != null) && ((v.storage_class.value & 2101248L) != 0) && hasValue(v))
                {
                    Expression ev = getValue(v);
                    if (((ev.op.value & 0xFF) == 26) || ((ev.op.value & 0xFF) == 62) || ((ev.op.value & 0xFF) == 31) || ((ev.op.value & 0xFF) == 27))
                    {
                        this.result.value = interpret(this.pue, ev, this.istate.value, this.goal);
                        return ;
                    }
                }
                this.result.value = e;
                return ;
            }
            this.result.value = getVarExp(e.loc.value, this.istate.value, e.var.value, this.goal);
            if (this.exceptionOrCant(this.result.value))
            {
                return ;
            }
            if (((e.var.value.storage_class.value & 2101248L) == 0L) && ((e.type.value.baseElemOf().ty.value & 0xFF) != ENUMTY.Tstruct))
            {
                this.result.value = paintTypeOntoLiteral(this.pue, e.type.value, this.result.value);
            }
        }

        public  void visit(DeclarationExp e) {
            Dsymbol s = e.declaration.value;
            {
                VarDeclaration v = s.isVarDeclaration();
                if ((v) != null)
                {
                    {
                        TupleDeclaration td = v.toAlias().isTupleDeclaration();
                        if ((td) != null)
                        {
                            this.result.value = null;
                            if (td.objects.value == null)
                            {
                                return ;
                            }
                            {
                                Slice<RootObject> __r954 = (td.objects.value.get()).opSlice().copy();
                                int __key955 = 0;
                                for (; (__key955 < __r954.getLength());__key955 += 1) {
                                    RootObject o = __r954.get(__key955);
                                    Expression ex = isExpression(o);
                                    DsymbolExp ds = ex != null ? ex.isDsymbolExp() : null;
                                    VarDeclaration v2 = ds != null ? ds.s.value.isVarDeclaration() : null;
                                    assert(v2 != null);
                                    if (v2.isDataseg() && !v2.isCTFE())
                                    {
                                        continue;
                                    }
                                    ctfeStack.push(v2);
                                    if (v2._init.value != null)
                                    {
                                        Expression einit = null;
                                        {
                                            ExpInitializer ie = v2._init.value.isExpInitializer();
                                            if ((ie) != null)
                                            {
                                                einit = interpret(ie.exp.value, this.istate.value, this.goal);
                                                if (this.exceptionOrCant(einit))
                                                {
                                                    return ;
                                                }
                                            }
                                            else if (v2._init.value.isVoidInitializer() != null)
                                            {
                                                einit = voidInitLiteral(v2.type.value, v2).copy();
                                            }
                                            else
                                            {
                                                e.error(new BytePtr("declaration `%s` is not yet implemented in CTFE"), e.toChars());
                                                this.result.value = CTFEExp.cantexp.value;
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
                        this.result.value = null;
                        return ;
                    }
                    if (!(v.isDataseg() || ((v.storage_class.value & 8388608L) != 0)) || v.isCTFE())
                    {
                        ctfeStack.push(v);
                    }
                    if (v._init.value != null)
                    {
                        {
                            ExpInitializer ie = v._init.value.isExpInitializer();
                            if ((ie) != null)
                            {
                                this.result.value = interpret(ie.exp.value, this.istate.value, this.goal);
                            }
                            else if (v._init.value.isVoidInitializer() != null)
                            {
                                this.result.value = voidInitLiteral(v.type.value, v).copy();
                                setValue(v, this.result.value);
                            }
                            else
                            {
                                e.error(new BytePtr("declaration `%s` is not yet implemented in CTFE"), e.toChars());
                                this.result.value = CTFEExp.cantexp.value;
                            }
                        }
                    }
                    else if ((v.type.value.size() == 0L))
                    {
                        this.result.value = v.type.value.defaultInitLiteral(e.loc.value);
                    }
                    else
                    {
                        e.error(new BytePtr("variable `%s` cannot be modified at compile time"), v.toChars());
                        this.result.value = CTFEExp.cantexp.value;
                    }
                    return ;
                }
            }
            if ((s.isAttribDeclaration() != null) || (s.isTemplateMixin() != null) || (s.isTupleDeclaration() != null))
            {
                AttribDeclaration ad = e.declaration.value.isAttribDeclaration();
                if ((ad != null) && (ad.decl.value != null) && ((ad.decl.value.get()).length.value == 1))
                {
                    Dsymbol sparent = (ad.decl.value.get()).get(0);
                    if ((sparent.isAggregateDeclaration() != null) || (sparent.isTemplateDeclaration() != null) || (sparent.isAliasDeclaration() != null))
                    {
                        this.result.value = null;
                        return ;
                    }
                }
                e.error(new BytePtr("declaration `%s` is not yet implemented in CTFE"), e.toChars());
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            this.result.value = null;
        }

        public  void visit(TypeidExp e) {
            {
                Type t = isType(e.obj.value);
                if ((t) != null)
                {
                    this.result.value = e;
                    return ;
                }
            }
            {
                Expression ex = isExpression(e.obj.value);
                if ((ex) != null)
                {
                    this.result.value = interpret(this.pue, ex, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                    if (this.exceptionOrCant(ex))
                    {
                        return ;
                    }
                    if (((this.result.value.op.value & 0xFF) == 13))
                    {
                        e.error(new BytePtr("null pointer dereference evaluating typeid. `%s` is `null`"), ex.toChars());
                        this.result.value = CTFEExp.cantexp.value;
                        return ;
                    }
                    if (((this.result.value.op.value & 0xFF) != 50))
                    {
                        e.error(new BytePtr("CTFE internal error: determining classinfo"));
                        this.result.value = CTFEExp.cantexp.value;
                        return ;
                    }
                    ClassDeclaration cd = ((ClassReferenceExp)this.result.value).originalClass();
                    assert(cd != null);
                    (this.pue) = new UnionExp(new TypeidExp(e.loc.value, cd.type.value));
                    this.result.value = (this.pue.get()).exp();
                    this.result.value.type.value = e.type.value;
                    return ;
                }
            }
            this.visit((Expression)e);
        }

        public  void visit(TupleExp e) {
            if (this.exceptionOrCant(interpret(e.e0.value, this.istate.value, CtfeGoal.ctfeNeedNothing)))
            {
                return ;
            }
            Ptr<DArray<Expression>> expsx = e.exps.value;
            {
                Slice<Expression> __r957 = (expsx.get()).opSlice().copy();
                int __key956 = 0;
                for (; (__key956 < __r957.getLength());__key956 += 1) {
                    Expression exp = __r957.get(__key956);
                    int i = __key956;
                    Expression ex = interpret(exp, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                    if (this.exceptionOrCant(ex))
                    {
                        return ;
                    }
                    if ((this.goal == CtfeGoal.ctfeNeedNothing))
                    {
                        continue;
                    }
                    if (((ex.op.value & 0xFF) == 232))
                    {
                        e.error(new BytePtr("CTFE internal error: void element `%s` in tuple"), exp.toChars());
                        throw new AssertionError("Unreachable code!");
                    }
                    if ((ex != exp))
                    {
                        expsx = copyArrayOnWrite(expsx, e.exps.value);
                        expsx.get().set(i, ex);
                    }
                }
            }
            if ((expsx != e.exps.value))
            {
                expandTuples(expsx);
                (this.pue) = new UnionExp(new TupleExp(e.loc.value, expsx));
                this.result.value = (this.pue.get()).exp();
                this.result.value.type.value = new TypeTuple(expsx);
            }
            else
            {
                this.result.value = e;
            }
        }

        public  void visit(ArrayLiteralExp e) {
            if (((e.ownedByCtfe & 0xFF) >= 1))
            {
                this.result.value = e;
                return ;
            }
            Type tn = e.type.value.toBasetype().nextOf().toBasetype();
            boolean wantCopy = ((tn.ty.value & 0xFF) == ENUMTY.Tsarray) || ((tn.ty.value & 0xFF) == ENUMTY.Tstruct);
            Expression basis = interpret(e.basis.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(basis))
            {
                return ;
            }
            Ptr<DArray<Expression>> expsx = e.elements.value;
            int dim = expsx != null ? (expsx.get()).length.value : 0;
            {
                int i = 0;
                for (; (i < dim);i++){
                    Expression exp = (expsx.get()).get(i);
                    Expression ex = null;
                    if (exp == null)
                    {
                        ex = copyLiteral(basis).copy();
                    }
                    else
                    {
                        assert(((exp.op.value & 0xFF) != 62) || (!pequals(((IndexExp)exp).e1.value, e)));
                        ex = interpret(exp, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                        if (this.exceptionOrCant(ex))
                        {
                            return ;
                        }
                        if (wantCopy)
                        {
                            ex = copyLiteral(ex).copy();
                        }
                    }
                    if ((ex != exp))
                    {
                        expsx = copyArrayOnWrite(expsx, e.elements.value);
                        expsx.get().set(i, ex);
                    }
                }
            }
            if ((expsx != e.elements.value))
            {
                expandTuples(expsx);
                if (((expsx.get()).length.value != dim))
                {
                    e.error(new BytePtr("CTFE internal error: invalid array literal"));
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                (this.pue) = new UnionExp(new ArrayLiteralExp(e.loc.value, e.type.value, basis, expsx));
                ArrayLiteralExp ale = (ArrayLiteralExp)(this.pue.get()).exp();
                ale.ownedByCtfe = OwnedBy.ctfe;
                this.result.value = ale;
            }
            else if (((((TypeNext)e.type.value).next.value.mod.value & 0xFF) & 5) != 0)
            {
                this.result.value = e;
            }
            else
            {
                this.pue.set(0, copyLiteral(e));
                this.result.value = (this.pue.get()).exp();
            }
        }

        public  void visit(AssocArrayLiteralExp e) {
            if (((e.ownedByCtfe & 0xFF) >= 1))
            {
                this.result.value = e;
                return ;
            }
            Ptr<DArray<Expression>> keysx = e.keys.value;
            Ptr<DArray<Expression>> valuesx = e.values.value;
            {
                Slice<Expression> __r959 = (keysx.get()).opSlice().copy();
                int __key958 = 0;
                for (; (__key958 < __r959.getLength());__key958 += 1) {
                    Expression ekey = __r959.get(__key958);
                    int i = __key958;
                    Expression evalue = (valuesx.get()).get(i);
                    Expression ek = interpret(ekey, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                    if (this.exceptionOrCant(ek))
                    {
                        return ;
                    }
                    Expression ev = interpret(evalue, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                    if (this.exceptionOrCant(ev))
                    {
                        return ;
                    }
                    if ((ek != ekey) || (ev != evalue))
                    {
                        keysx = copyArrayOnWrite(keysx, e.keys.value);
                        valuesx = copyArrayOnWrite(valuesx, e.values.value);
                        keysx.get().set(i, ek);
                        valuesx.get().set(i, ev);
                    }
                }
            }
            if ((keysx != e.keys.value))
            {
                expandTuples(keysx);
            }
            if ((valuesx != e.values.value))
            {
                expandTuples(valuesx);
            }
            if (((keysx.get()).length.value != (valuesx.get()).length.value))
            {
                e.error(new BytePtr("CTFE internal error: invalid AA"));
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            {
                int i = 1;
                for (; (i < (keysx.get()).length.value);i++){
                    Expression ekey = (keysx.get()).get(i - 1);
                    {
                        int j = i;
                        for (; (j < (keysx.get()).length.value);j++){
                            Expression ekey2 = (keysx.get()).get(j);
                            if (ctfeEqual(e.loc.value, TOK.equal, ekey, ekey2) == 0)
                            {
                                continue;
                            }
                            keysx = copyArrayOnWrite(keysx, e.keys.value);
                            valuesx = copyArrayOnWrite(valuesx, e.values.value);
                            (keysx.get()).remove(i - 1);
                            (valuesx.get()).remove(i - 1);
                            i -= 1;
                            break;
                        }
                    }
                }
            }
            if ((keysx != e.keys.value) || (valuesx != e.values.value))
            {
                assert((keysx != e.keys.value) && (valuesx != e.values.value));
                AssocArrayLiteralExp aae = new AssocArrayLiteralExp(e.loc.value, keysx, valuesx);
                aae.type.value = e.type.value;
                aae.ownedByCtfe = OwnedBy.ctfe;
                this.result.value = aae;
            }
            else
            {
                this.pue.set(0, copyLiteral(e));
                this.result.value = (this.pue.get()).exp();
            }
        }

        public  void visit(StructLiteralExp e) {
            if (((e.ownedByCtfe.value & 0xFF) >= 1))
            {
                this.result.value = e;
                return ;
            }
            int dim = e.elements.value != null ? (e.elements.value.get()).length.value : 0;
            Ptr<DArray<Expression>> expsx = e.elements.value;
            if ((dim != e.sd.fields.length.value))
            {
                int nvthis = e.sd.fields.length.value - e.sd.nonHiddenFields();
                assert((e.sd.fields.length.value - dim == nvthis));
                {
                    int __key960 = 0;
                    int __limit961 = nvthis;
                    for (; (__key960 < __limit961);__key960 += 1) {
                        int i = __key960;
                        NullExp ne = new NullExp(e.loc.value, null);
                        VarDeclaration vthis = (i == 0) ? e.sd.vthis.value : e.sd.vthis2.value;
                        ne.type.value = vthis.type.value;
                        expsx = copyArrayOnWrite(expsx, e.elements.value);
                        (expsx.get()).push(ne);
                        dim += 1;
                    }
                }
            }
            assert((dim == e.sd.fields.length.value));
            {
                int __key962 = 0;
                int __limit963 = dim;
                for (; (__key962 < __limit963);__key962 += 1) {
                    int i = __key962;
                    VarDeclaration v = e.sd.fields.get(i);
                    Expression exp = (expsx.get()).get(i);
                    Expression ex = null;
                    if (exp == null)
                    {
                        ex = voidInitLiteral(v.type.value, v).copy();
                    }
                    else
                    {
                        ex = interpret(exp, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                        if (this.exceptionOrCant(ex))
                        {
                            return ;
                        }
                        if (((v.type.value.ty.value & 0xFF) != (ex.type.value.ty.value & 0xFF)) && ((v.type.value.ty.value & 0xFF) == ENUMTY.Tsarray))
                        {
                            TypeSArray tsa = (TypeSArray)v.type.value;
                            int len = (int)tsa.dim.value.toInteger();
                            Ref<UnionExp> ue = ref(null);
                            ex = createBlockDuplicatedArrayLiteral(ptr(ue), ex.loc.value, v.type.value, ex, len);
                            if ((pequals(ex, ue.value.exp())))
                            {
                                ex = ue.value.copy();
                            }
                        }
                    }
                    if ((ex != exp))
                    {
                        expsx = copyArrayOnWrite(expsx, e.elements.value);
                        expsx.get().set(i, ex);
                    }
                }
            }
            if ((expsx != e.elements.value))
            {
                expandTuples(expsx);
                if (((expsx.get()).length.value != e.sd.fields.length.value))
                {
                    e.error(new BytePtr("CTFE internal error: invalid struct literal"));
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                (this.pue) = new UnionExp(new StructLiteralExp(e.loc.value, e.sd, expsx));
                StructLiteralExp sle = (StructLiteralExp)(this.pue.get()).exp();
                sle.type.value = e.type.value;
                sle.ownedByCtfe.value = OwnedBy.ctfe;
                sle.origin = e.origin;
                this.result.value = sle;
            }
            else
            {
                this.pue.set(0, copyLiteral(e));
                this.result.value = (this.pue.get()).exp();
            }
        }

        public static Expression recursivelyCreateArrayLiteral(Ptr<UnionExp> pue, Loc loc, Type newtype, Ptr<InterState> istate, Ptr<DArray<Expression>> arguments, int argnum) {
            Expression lenExpr = interpret(pue, (arguments.get()).get(argnum), istate, CtfeGoal.ctfeNeedRvalue);
            if (exceptionOrCantInterpret(lenExpr))
            {
                return lenExpr;
            }
            int len = (int)lenExpr.toInteger();
            Type elemType = ((TypeArray)newtype).next.value;
            if (((elemType.ty.value & 0xFF) == ENUMTY.Tarray) && (argnum < (arguments.get()).length.value - 1))
            {
                Expression elem = recursivelyCreateArrayLiteral(pue, loc, elemType, istate, arguments, argnum + 1);
                if (exceptionOrCantInterpret(elem))
                {
                    return elem;
                }
                Ptr<DArray<Expression>> elements = refPtr(new DArray<Expression>(len));
                {
                    Slice<Expression> __r964 = (elements.get()).opSlice().copy();
                    int __key965 = 0;
                    for (; (__key965 < __r964.getLength());__key965 += 1) {
                        Expression element = __r964.get(__key965);
                        element = copyLiteral(elem).copy();
                    }
                }
                (pue) = new UnionExp(new ArrayLiteralExp(loc, newtype, elements));
                ArrayLiteralExp ae = (ArrayLiteralExp)(pue.get()).exp();
                ae.ownedByCtfe = OwnedBy.ctfe;
                return ae;
            }
            assert((argnum == (arguments.get()).length.value - 1));
            if (((elemType.ty.value & 0xFF) == ENUMTY.Tchar) || ((elemType.ty.value & 0xFF) == ENUMTY.Twchar) || ((elemType.ty.value & 0xFF) == ENUMTY.Tdchar))
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
            if (e.allocator.value != null)
            {
                e.error(new BytePtr("member allocators not supported by CTFE"));
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            Expression epre = interpret(this.pue, e.argprefix.value, this.istate.value, CtfeGoal.ctfeNeedNothing);
            if (this.exceptionOrCant(epre))
            {
                return ;
            }
            if (((e.newtype.value.ty.value & 0xFF) == ENUMTY.Tarray) && (e.arguments.value != null))
            {
                this.result.value = recursivelyCreateArrayLiteral(this.pue, e.loc.value, e.newtype.value, this.istate.value, e.arguments.value, 0);
                return ;
            }
            {
                TypeStruct ts = e.newtype.value.toBasetype().isTypeStruct();
                if ((ts) != null)
                {
                    if (e.member.value != null)
                    {
                        Expression se = e.newtype.value.defaultInitLiteral(e.loc.value);
                        se = interpret(se, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                        if (this.exceptionOrCant(se))
                        {
                            return ;
                        }
                        this.result.value = interpretFunction(this.pue, e.member.value, this.istate.value, e.arguments.value, se);
                        this.result.value.loc.value = e.loc.value.copy();
                    }
                    else
                    {
                        StructDeclaration sd = ts.sym.value;
                        Ptr<DArray<Expression>> exps = refPtr(new DArray<Expression>());
                        (exps.get()).reserve(sd.fields.length.value);
                        if (e.arguments.value != null)
                        {
                            (exps.get()).setDim((e.arguments.value.get()).length.value);
                            {
                                Slice<Expression> __r967 = (e.arguments.value.get()).opSlice().copy();
                                int __key966 = 0;
                                for (; (__key966 < __r967.getLength());__key966 += 1) {
                                    Expression ex = __r967.get(__key966);
                                    int i = __key966;
                                    ex = interpret(ex, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                                    if (this.exceptionOrCant(ex))
                                    {
                                        return ;
                                    }
                                    exps.get().set(i, ex);
                                }
                            }
                        }
                        sd.fill(e.loc.value, exps, false);
                        StructLiteralExp se = new StructLiteralExp(e.loc.value, sd, exps, e.newtype.value);
                        se.type.value = e.newtype.value;
                        se.ownedByCtfe.value = OwnedBy.ctfe;
                        this.result.value = interpret(this.pue, (Expression)se, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                    }
                    if (this.exceptionOrCant(this.result.value))
                    {
                        return ;
                    }
                    Expression ev = (pequals(this.result.value, (this.pue.get()).exp())) ? (this.pue.get()).copy() : this.result.value;
                    (this.pue) = new UnionExp(new AddrExp(e.loc.value, ev, e.type.value));
                    this.result.value = (this.pue.get()).exp();
                    return ;
                }
            }
            {
                TypeClass tc = e.newtype.value.toBasetype().isTypeClass();
                if ((tc) != null)
                {
                    ClassDeclaration cd = tc.sym.value;
                    int totalFieldCount = 0;
                    {
                        ClassDeclaration c = cd;
                        for (; c != null;c = c.baseClass.value) {
                            totalFieldCount += c.fields.length.value;
                        }
                    }
                    Ptr<DArray<Expression>> elems = refPtr(new DArray<Expression>(totalFieldCount));
                    int fieldsSoFar = totalFieldCount;
                    {
                        ClassDeclaration c = cd;
                        for (; c != null;c = c.baseClass.value){
                            fieldsSoFar -= c.fields.length.value;
                            {
                                Slice<VarDeclaration> __r969 = c.fields.opSlice().copy();
                                int __key968 = 0;
                                for (; (__key968 < __r969.getLength());__key968 += 1) {
                                    VarDeclaration v = __r969.get(__key968);
                                    int i = __key968;
                                    if (v.inuse.value != 0)
                                    {
                                        e.error(new BytePtr("circular reference to `%s`"), v.toPrettyChars(false));
                                        this.result.value = CTFEExp.cantexp.value;
                                        return ;
                                    }
                                    Expression m = null;
                                    if (v._init.value != null)
                                    {
                                        if (v._init.value.isVoidInitializer() != null)
                                        {
                                            m = voidInitLiteral(v.type.value, v).copy();
                                        }
                                        else
                                        {
                                            m = v.getConstInitializer(true);
                                        }
                                    }
                                    else
                                    {
                                        m = v.type.value.defaultInitLiteral(e.loc.value);
                                    }
                                    if (this.exceptionOrCant(m))
                                    {
                                        return ;
                                    }
                                    elems.get().set(fieldsSoFar + i, copyLiteral(m).copy());
                                }
                            }
                        }
                    }
                    StructLiteralExp se = new StructLiteralExp(e.loc.value, (StructDeclaration)cd, elems, e.newtype.value);
                    se.ownedByCtfe.value = OwnedBy.ctfe;
                    (this.pue) = new UnionExp(new ClassReferenceExp(e.loc.value, se, e.type.value));
                    Expression eref = (this.pue.get()).exp();
                    if (e.member.value != null)
                    {
                        if (e.member.value.fbody.value == null)
                        {
                            Expression ctorfail = evaluateIfBuiltin(this.pue, this.istate.value, e.loc.value, e.member.value, e.arguments.value, eref);
                            if (ctorfail != null)
                            {
                                if (this.exceptionOrCant(ctorfail))
                                {
                                    return ;
                                }
                                this.result.value = eref;
                                return ;
                            }
                            e.member.value.error(new BytePtr("`%s` cannot be constructed at compile time, because the constructor has no available source code"), e.newtype.value.toChars());
                            this.result.value = CTFEExp.cantexp.value;
                            return ;
                        }
                        Ref<UnionExp> ue = ref(null);
                        Expression ctorfail = interpretFunction(ptr(ue), e.member.value, this.istate.value, e.arguments.value, eref);
                        if (this.exceptionOrCant(ctorfail))
                        {
                            return ;
                        }
                        eref.loc.value = e.loc.value.copy();
                    }
                    this.result.value = eref;
                    return ;
                }
            }
            if (e.newtype.value.toBasetype().isscalar())
            {
                Expression newval = null;
                if ((e.arguments.value != null) && ((e.arguments.value.get()).length.value != 0))
                {
                    newval = (e.arguments.value.get()).get(0);
                }
                else
                {
                    newval = e.newtype.value.defaultInitLiteral(e.loc.value);
                }
                newval = interpret(newval, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(newval))
                {
                    return ;
                }
                Ptr<DArray<Expression>> elements = refPtr(new DArray<Expression>(1));
                elements.get().set(0, newval);
                ArrayLiteralExp ae = new ArrayLiteralExp(e.loc.value, e.newtype.value.arrayOf(), elements);
                ae.ownedByCtfe = OwnedBy.ctfe;
                IndexExp ei = new IndexExp(e.loc.value, ae, new IntegerExp(Loc.initial.value, 0L, Type.tsize_t.value));
                ei.type.value = e.newtype.value;
                (this.pue) = new UnionExp(new AddrExp(e.loc.value, ei, e.type.value));
                this.result.value = (this.pue.get()).exp();
                return ;
            }
            e.error(new BytePtr("cannot interpret `%s` at compile time"), e.toChars());
            this.result.value = CTFEExp.cantexp.value;
        }

        public  void visit(UnaExp e) {
            Ref<UnionExp> ue = ref(null);
            Expression e1 = interpret(ptr(ue), e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e1))
            {
                return ;
            }
            switch ((e.op.value & 0xFF))
            {
                case 8:
                    this.pue.set(0, Neg(e.type.value, e1));
                    break;
                case 92:
                    this.pue.set(0, Com(e.type.value, e1));
                    break;
                case 91:
                    this.pue.set(0, Not(e.type.value, e1));
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            this.result.value = (this.pue.get()).exp();
        }

        public  void visit(DotTypeExp e) {
            Ref<UnionExp> ue = ref(null);
            Expression e1 = interpret(ptr(ue), e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e1))
            {
                return ;
            }
            if ((pequals(e1, e.e1.value)))
            {
                this.result.value = e;
            }
            else
            {
                DotTypeExp edt = (DotTypeExp)e.copy();
                edt.e1.value = (pequals(e1, ue.value.exp())) ? e1.copy() : e1;
                this.result.value = edt;
            }
        }

        public  void interpretCommon(BinExp e, Function4<Loc,Type,Expression,Expression,UnionExp> fp) {
            if (((e.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((e.e2.value.type.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((e.op.value & 0xFF) == 75))
            {
                Ref<UnionExp> ue1 = ref(null);
                Expression e1 = interpret(ptr(ue1), e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e1))
                {
                    return ;
                }
                Ref<UnionExp> ue2 = ref(null);
                Expression e2 = interpret(ptr(ue2), e.e2.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e2))
                {
                    return ;
                }
                this.pue.set(0, pointerDifference(e.loc.value, e.type.value, e1, e2));
                this.result.value = (this.pue.get()).exp();
                return ;
            }
            if (((e.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tpointer) && e.e2.value.type.value.isintegral())
            {
                Ref<UnionExp> ue1 = ref(null);
                Expression e1 = interpret(ptr(ue1), e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e1))
                {
                    return ;
                }
                Ref<UnionExp> ue2 = ref(null);
                Expression e2 = interpret(ptr(ue2), e.e2.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e2))
                {
                    return ;
                }
                this.pue.set(0, pointerArithmetic(e.loc.value, e.op.value, e.type.value, e1, e2));
                this.result.value = (this.pue.get()).exp();
                return ;
            }
            if (((e.e2.value.type.value.ty.value & 0xFF) == ENUMTY.Tpointer) && e.e1.value.type.value.isintegral() && ((e.op.value & 0xFF) == 74))
            {
                Ref<UnionExp> ue1 = ref(null);
                Expression e1 = interpret(ptr(ue1), e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e1))
                {
                    return ;
                }
                Ref<UnionExp> ue2 = ref(null);
                Expression e2 = interpret(ptr(ue2), e.e2.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e2))
                {
                    return ;
                }
                this.pue.set(0, pointerArithmetic(e.loc.value, e.op.value, e.type.value, e2, e1));
                this.result.value = (this.pue.get()).exp();
                return ;
            }
            if (((e.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tpointer) || ((e.e2.value.type.value.ty.value & 0xFF) == ENUMTY.Tpointer))
            {
                e.error(new BytePtr("pointer expression `%s` cannot be interpreted at compile time"), e.toChars());
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            Function3<Ptr<UnionExp>,Expression,Expression,Boolean> evalOperand = new Function3<Ptr<UnionExp>,Expression,Expression,Boolean>(){
                public Boolean invoke(Ptr<UnionExp> pue, Expression ex, Ref<Expression> er) {
                    Ref<Ptr<UnionExp>> pue_ref = ref(pue);
                    Ref<Expression> ex_ref = ref(ex);
                    er.value = null;
                    er.value = interpret(pue_ref.value, ex_ref.value, istate.value, CtfeGoal.ctfeNeedRvalue);
                    if (exceptionOrCant(er.value))
                    {
                        return false;
                    }
                    if ((er.value.isConst() != 1))
                    {
                        if (((er.value.op.value & 0xFF) == 47))
                        {
                            e.error(new BytePtr("cannot interpret array literal expression `%s` at compile time"), e.toChars());
                        }
                        else
                        {
                            e.error(new BytePtr("CTFE internal error: non-constant value `%s`"), ex_ref.value.toChars());
                        }
                        result.value = CTFEExp.cantexp.value;
                        return false;
                    }
                    return true;
                }
            };
            Ref<UnionExp> ue1 = ref(null);
            Ref<Expression> e1 = ref(null);
            if (!evalOperand.invoke(ptr(ue1), e.e1.value, e1))
            {
                return ;
            }
            Ref<UnionExp> ue2 = ref(null);
            Ref<Expression> e2 = ref(null);
            if (!evalOperand.invoke(ptr(ue2), e.e2.value, e2))
            {
                return ;
            }
            if (((e.op.value & 0xFF) == 65) || ((e.op.value & 0xFF) == 64) || ((e.op.value & 0xFF) == 68))
            {
                long i2 = (long)e2.value.toInteger();
                long sz = e1.value.type.value.size() * 8L;
                if ((i2 < 0L) || ((long)i2 >= sz))
                {
                    e.error(new BytePtr("shift by %lld is outside the range 0..%llu"), i2, sz - 1L);
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
            }
            this.pue.set(0, (fp).invoke(e.loc.value, e.type.value, e1.value, e2.value));
            this.result.value = (this.pue.get()).exp();
            if (CTFEExp.isCantExp(this.result.value))
            {
                e.error(new BytePtr("`%s` cannot be interpreted at compile time"), e.toChars());
            }
        }

        public  void interpretCompareCommon(BinExp e, Function4<Loc,Byte,Expression,Expression,Integer> fp) {
            Ref<UnionExp> ue1 = ref(null);
            Ref<UnionExp> ue2 = ref(null);
            if (((e.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((e.e2.value.type.value.ty.value & 0xFF) == ENUMTY.Tpointer))
            {
                Expression e1 = interpret(ptr(ue1), e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e1))
                {
                    return ;
                }
                Expression e2 = interpret(ptr(ue2), e.e2.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e2))
                {
                    return ;
                }
                Ref<Long> ofs1 = ref(0L);
                Ref<Long> ofs2 = ref(0L);
                Expression agg1 = getAggregateFromPointer(e1, ptr(ofs1));
                Expression agg2 = getAggregateFromPointer(e2, ptr(ofs2));
                int cmp = comparePointers(e.op.value, agg1, ofs1.value, agg2, ofs2.value);
                if ((cmp == -1))
                {
                    byte dir = ((e.op.value & 0xFF) == 55) || ((e.op.value & 0xFF) == 57) ? (byte)60 : (byte)62;
                    e.error(new BytePtr("the ordering of pointers to unrelated memory blocks is indeterminate in CTFE. To check if they point to the same memory block, use both `>` and `<` inside `&&` or `||`, eg `%s && %s %c= %s + 1`"), e.toChars(), e.e1.value.toChars(), (dir & 0xFF), e.e2.value.toChars());
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                (this.pue) = new UnionExp(new IntegerExp(e.loc.value, cmp, e.type.value));
                this.result.value = (this.pue.get()).exp();
                return ;
            }
            Expression e1 = interpret(ptr(ue1), e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e1))
            {
                return ;
            }
            if (!isCtfeComparable(e1))
            {
                e.error(new BytePtr("cannot compare `%s` at compile time"), e1.toChars());
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            Expression e2 = interpret(ptr(ue2), e.e2.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e2))
            {
                return ;
            }
            if (!isCtfeComparable(e2))
            {
                e.error(new BytePtr("cannot compare `%s` at compile time"), e2.toChars());
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            int cmp = (fp).invoke(e.loc.value, e.op.value, e1, e2);
            (this.pue) = new UnionExp(new IntegerExp(e.loc.value, cmp, e.type.value));
            this.result.value = (this.pue.get()).exp();
        }

        public  void visit(BinExp e) {
            switch ((e.op.value & 0xFF))
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
                printf(new BytePtr("be = '%s' %s at [%s]\n"), Token.toChars(e.op.value), e.toChars(), e.loc.value.toChars(global.params.showColumns.value));
                throw new AssertionError("Unreachable code!");
            }
        }

        public static VarDeclaration findParentVar(Expression e) {
            for (; ;){
                {
                    VarExp ve = e.isVarExp();
                    if ((ve) != null)
                    {
                        VarDeclaration v = ve.var.value.isVarDeclaration();
                        assert(v != null);
                        return v;
                    }
                }
                {
                    IndexExp ie = e.isIndexExp();
                    if ((ie) != null)
                    {
                        e = ie.e1.value;
                    }
                    else {
                        DotVarExp dve = e.isDotVarExp();
                        if ((dve) != null)
                        {
                            e = dve.e1.value;
                        }
                        else {
                            DotTemplateInstanceExp dtie = e.isDotTemplateInstanceExp();
                            if ((dtie) != null)
                            {
                                e = dtie.e1.value;
                            }
                            else {
                                SliceExp se = e.isSliceExp();
                                if ((se) != null)
                                {
                                    e = se.e1.value;
                                }
                                else
                                {
                                    return null;
                                }
                            }
                        }
                    }
                }
            }
        }

        public  void interpretAssignCommon(BinExp e, Function4<Loc,Type,Expression,Expression,UnionExp> fp, int post) {
            this.result.value = CTFEExp.cantexp.value;
            Expression e1 = e.e1.value;
            if (this.istate.value == null)
            {
                e.error(new BytePtr("value of `%s` is not known at compile time"), e1.toChars());
                return ;
            }
            CtfeStatus.numAssignments += 1;
            boolean isBlockAssignment = false;
            if (((e1.op.value & 0xFF) == 31))
            {
                Type tdst = e1.type.value.toBasetype();
                Type tsrc = e.e2.value.type.value.toBasetype();
                for (; ((tdst.ty.value & 0xFF) == ENUMTY.Tsarray) || ((tdst.ty.value & 0xFF) == ENUMTY.Tarray);){
                    tdst = ((TypeArray)tdst).next.value.toBasetype();
                    if (tsrc.equivalent(tdst))
                    {
                        isBlockAssignment = true;
                        break;
                    }
                }
            }
            if (((e.op.value & 0xFF) == 95) || ((e.op.value & 0xFF) == 96) && ((((AssignExp)e).memset & MemorySet.referenceInit) != 0))
            {
                assert(fp == null);
                Expression newval = interpret(e.e2.value, this.istate.value, CtfeGoal.ctfeNeedLvalue);
                if (this.exceptionOrCant(newval))
                {
                    return ;
                }
                VarDeclaration v = ((VarExp)e1).var.value.isVarDeclaration();
                setValue(v, newval);
                if ((this.goal == CtfeGoal.ctfeNeedRvalue))
                {
                    this.result.value = interpret(newval, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                }
                else
                {
                    this.result.value = e1;
                }
                return ;
            }
            if (fp != null)
            {
                for (; ((e1.op.value & 0xFF) == 12);){
                    CastExp ce = (CastExp)e1;
                    e1 = ce.e1.value;
                }
            }
            AssocArrayLiteralExp existingAA = null;
            Expression lastIndex = null;
            Expression oldval = null;
            if (((e1.op.value & 0xFF) == 62) && ((((IndexExp)e1).e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Taarray))
            {
                IndexExp ie = (IndexExp)e1;
                int depth = 0;
                for (; ((ie.e1.value.op.value & 0xFF) == 62) && ((((IndexExp)ie.e1.value).e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Taarray);){
                    assert(ie.modifiable);
                    ie = (IndexExp)ie.e1.value;
                    depth += 1;
                }
                Expression aggregate = interpret(ie.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(aggregate))
                {
                    return ;
                }
                if (((existingAA = aggregate.isAssocArrayLiteralExp()) != null))
                {
                    lastIndex = interpret(((IndexExp)e1).e2.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                    lastIndex = resolveSlice(lastIndex, null);
                    if (this.exceptionOrCant(lastIndex))
                    {
                        return ;
                    }
                    for (; (depth > 0);){
                        IndexExp xe = (IndexExp)e1;
                        {
                            int __key970 = 0;
                            int __limit971 = depth;
                            for (; (__key970 < __limit971);__key970 += 1) {
                                int d = __key970;
                                xe = (IndexExp)xe.e1.value;
                            }
                        }
                        Expression ekey = interpret(xe.e2.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                        if (this.exceptionOrCant(ekey))
                        {
                            return ;
                        }
                        Ref<UnionExp> ekeyTmp = ref(null);
                        ekey = resolveSlice(ekey, ptr(ekeyTmp));
                        AssocArrayLiteralExp newAA = (AssocArrayLiteralExp)findKeyInAA(e.loc.value, existingAA, ekey);
                        if (this.exceptionOrCant(newAA))
                        {
                            return ;
                        }
                        if (newAA == null)
                        {
                            Ptr<DArray<Expression>> keysx = refPtr(new DArray<Expression>());
                            Ptr<DArray<Expression>> valuesx = refPtr(new DArray<Expression>());
                            newAA = new AssocArrayLiteralExp(e.loc.value, keysx, valuesx);
                            newAA.type.value = xe.type.value;
                            newAA.ownedByCtfe = OwnedBy.ctfe;
                            (existingAA.keys.value.get()).push(ekey);
                            (existingAA.values.value.get()).push(newAA);
                        }
                        existingAA = newAA;
                        depth -= 1;
                    }
                    if (fp != null)
                    {
                        oldval = findKeyInAA(e.loc.value, existingAA, lastIndex);
                        if (oldval == null)
                        {
                            oldval = copyLiteral(e.e1.value.type.value.defaultInitLiteral(e.loc.value)).copy();
                        }
                    }
                }
                else
                {
                    oldval = copyLiteral(e.e1.value.type.value.defaultInitLiteral(e.loc.value)).copy();
                    Expression newaae = oldval;
                    for (; ((e1.op.value & 0xFF) == 62) && ((((IndexExp)e1).e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Taarray);){
                        Expression ekey = interpret(((IndexExp)e1).e2.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                        if (this.exceptionOrCant(ekey))
                        {
                            return ;
                        }
                        ekey = resolveSlice(ekey, null);
                        Ptr<DArray<Expression>> keysx = refPtr(new DArray<Expression>());
                        Ptr<DArray<Expression>> valuesx = refPtr(new DArray<Expression>());
                        (keysx.get()).push(ekey);
                        (valuesx.get()).push(newaae);
                        AssocArrayLiteralExp aae = new AssocArrayLiteralExp(e.loc.value, keysx, valuesx);
                        aae.type.value = ((IndexExp)e1).e1.value.type.value;
                        aae.ownedByCtfe = OwnedBy.ctfe;
                        if (existingAA == null)
                        {
                            existingAA = aae;
                            lastIndex = ekey;
                        }
                        newaae = aae;
                        e1 = ((IndexExp)e1).e1.value;
                    }
                    e1 = interpret(e1, this.istate.value, CtfeGoal.ctfeNeedLvalue);
                    if (this.exceptionOrCant(e1))
                    {
                        return ;
                    }
                    e1 = this.assignToLvalue(e, e1, newaae);
                    if (this.exceptionOrCant(e1))
                    {
                        return ;
                    }
                }
                assert((existingAA != null) && (lastIndex != null));
                e1 = null;
            }
            else if (((e1.op.value & 0xFF) == 32))
            {
                oldval = interpret(e1, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(oldval))
                {
                    return ;
                }
            }
            else if (((e.op.value & 0xFF) == 95) || ((e.op.value & 0xFF) == 96))
            {
                VarDeclaration ultimateVar = findParentVar(e1);
                {
                    VarExp ve = e1.isVarExp();
                    if ((ve) != null)
                    {
                        VarDeclaration v = ve.var.value.isVarDeclaration();
                        assert(v != null);
                        if ((v.storage_class.value & 4096L) != 0)
                        {
                            /*goto L1*//*unrolled goto*/
                        }
                    }
                    else if ((ultimateVar != null) && (getValue(ultimateVar) == null))
                    {
                        Expression ex = interpret(ultimateVar.type.value.defaultInitLiteral(e.loc.value), this.istate.value, CtfeGoal.ctfeNeedRvalue);
                        if (this.exceptionOrCant(ex))
                        {
                            return ;
                        }
                        setValue(ultimateVar, ex);
                    }
                    else
                    {
                        /*goto L1*//*unrolled goto*/
                    }
                }
            }
            else
            {
            /*L1:*/
                e1 = interpret(e1, this.istate.value, CtfeGoal.ctfeNeedLvalue);
                if (this.exceptionOrCant(e1))
                {
                    return ;
                }
                if (((e1.op.value & 0xFF) == 62) && ((((IndexExp)e1).e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Taarray))
                {
                    IndexExp ie = (IndexExp)e1;
                    assert(((ie.e1.value.op.value & 0xFF) == 48));
                    existingAA = (AssocArrayLiteralExp)ie.e1.value;
                    lastIndex = ie.e2.value;
                }
            }
            Expression newval = interpret(e.e2.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(newval))
            {
                return ;
            }
            if (((e.op.value & 0xFF) == 96) && ((newval.op.value & 0xFF) == 135))
            {
                Type tbn = e.type.value.baseElemOf();
                if (((tbn.ty.value & 0xFF) == ENUMTY.Tstruct))
                {
                    newval = e.type.value.defaultInitLiteral(e.loc.value);
                    if (((newval.op.value & 0xFF) == 127))
                    {
                        this.result.value = CTFEExp.cantexp.value;
                        return ;
                    }
                    newval = interpret(newval, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                    if (this.exceptionOrCant(newval))
                    {
                        return ;
                    }
                }
            }
            if (fp != null)
            {
                if (oldval == null)
                {
                    oldval = interpret(e1, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                    if (this.exceptionOrCant(oldval))
                    {
                        return ;
                    }
                }
                if (((e.e1.value.type.value.ty.value & 0xFF) != ENUMTY.Tpointer))
                {
                    if (((e.op.value & 0xFF) == 71) || ((e.op.value & 0xFF) == 72) || ((e.op.value & 0xFF) == 73))
                    {
                        if (((newval.type.value.ty.value & 0xFF) != ENUMTY.Tarray))
                        {
                            newval = copyLiteral(newval).copy();
                            newval.type.value = e.e2.value.type.value;
                        }
                        else
                        {
                            newval = paintTypeOntoLiteral(e.e2.value.type.value, newval);
                            newval = resolveSlice(newval, null);
                        }
                    }
                    oldval = resolveSlice(oldval, null);
                    newval = (fp).invoke(e.loc.value, e.type.value, oldval, newval).copy();
                }
                else if (e.e2.value.type.value.isintegral() && ((e.op.value & 0xFF) == 76) || ((e.op.value & 0xFF) == 77) || ((e.op.value & 0xFF) == 93) || ((e.op.value & 0xFF) == 94))
                {
                    newval = pointerArithmetic(e.loc.value, e.op.value, e.type.value, oldval, newval).copy();
                }
                else
                {
                    e.error(new BytePtr("pointer expression `%s` cannot be interpreted at compile time"), e.toChars());
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                if (this.exceptionOrCant(newval))
                {
                    if (CTFEExp.isCantExp(newval))
                    {
                        e.error(new BytePtr("cannot interpret `%s` at compile time"), e.toChars());
                    }
                    return ;
                }
            }
            if (existingAA != null)
            {
                if (((existingAA.ownedByCtfe & 0xFF) != 1))
                {
                    e.error(new BytePtr("cannot modify read-only constant `%s`"), existingAA.toChars());
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                assignAssocArrayElement(e.loc.value, existingAA, lastIndex, newval);
                this.result.value = ctfeCast(this.pue, e.loc.value, e.type.value, e.type.value, (fp != null) && (post != 0) ? oldval : newval);
                return ;
            }
            if (((e1.op.value & 0xFF) == 32))
            {
                this.result.value = ctfeCast(this.pue, e.loc.value, e.type.value, e.type.value, (fp != null) && (post != 0) ? oldval : newval);
                if (this.exceptionOrCant(this.result.value))
                {
                    return ;
                }
                if ((pequals(this.result.value, (this.pue.get()).exp())))
                {
                    this.result.value = (this.pue.get()).copy();
                }
                int oldlen = (int)oldval.toInteger();
                int newlen = (int)newval.toInteger();
                if ((oldlen == newlen))
                {
                    return ;
                }
                e1 = ((ArrayLengthExp)e1).e1.value;
                Type t = e1.type.value.toBasetype();
                if (((t.ty.value & 0xFF) != ENUMTY.Tarray))
                {
                    e.error(new BytePtr("`%s` is not yet supported at compile time"), e.toChars());
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                e1 = interpret(e1, this.istate.value, CtfeGoal.ctfeNeedLvalue);
                if (this.exceptionOrCant(e1))
                {
                    return ;
                }
                if ((oldlen != 0))
                {
                    oldval = interpret(e1, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                }
                newval = changeArrayLiteralLength(e.loc.value, (TypeArray)t, oldval, oldlen, newlen).copy();
                e1 = this.assignToLvalue(e, e1, newval);
                if (this.exceptionOrCant(e1))
                {
                    return ;
                }
                return ;
            }
            if (!isBlockAssignment)
            {
                newval = ctfeCast(this.pue, e.loc.value, e.type.value, e.type.value, newval);
                if (this.exceptionOrCant(newval))
                {
                    return ;
                }
                if ((pequals(newval, (this.pue.get()).exp())))
                {
                    newval = (this.pue.get()).copy();
                }
                if ((this.goal == CtfeGoal.ctfeNeedLvalue))
                {
                    this.result.value = e1;
                }
                else
                {
                    this.result.value = ctfeCast(this.pue, e.loc.value, e.type.value, e.type.value, (fp != null) && (post != 0) ? oldval : newval);
                    if ((pequals(this.result.value, (this.pue.get()).exp())))
                    {
                        this.result.value = (this.pue.get()).copy();
                    }
                }
                if (this.exceptionOrCant(this.result.value))
                {
                    return ;
                }
            }
            if (this.exceptionOrCant(newval))
            {
                return ;
            }
            if (((e1.op.value & 0xFF) == 31) || ((e1.op.value & 0xFF) == 229) || ((e1.op.value & 0xFF) == 47) || ((e1.op.value & 0xFF) == 121) || ((e1.op.value & 0xFF) == 13) && ((e1.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tarray))
            {
                this.result.value = this.interpretAssignToSlice(this.pue, e, e1, newval, isBlockAssignment);
                if (this.exceptionOrCant(this.result.value))
                {
                    return ;
                }
                {
                    SliceExp se = e.e1.value.isSliceExp();
                    if ((se) != null)
                    {
                        Expression e1x = interpret(se.e1.value, this.istate.value, CtfeGoal.ctfeNeedLvalue);
                        {
                            DotVarExp dve = e1x.isDotVarExp();
                            if ((dve) != null)
                            {
                                Expression ex = dve.e1.value;
                                StructLiteralExp sle = ((ex.op.value & 0xFF) == 49) ? (StructLiteralExp)ex : ((ex.op.value & 0xFF) == 50) ? ((ClassReferenceExp)ex).value : null;
                                VarDeclaration v = dve.var.value.isVarDeclaration();
                                if ((sle == null) || (v == null))
                                {
                                    e.error(new BytePtr("CTFE internal error: dotvar slice assignment"));
                                    this.result.value = CTFEExp.cantexp.value;
                                    return ;
                                }
                                this.stompOverlappedFields(sle, v);
                            }
                        }
                    }
                }
                return ;
            }
            assert(this.result.value != null);
            {
                Expression ex = this.assignToLvalue(e, e1, newval);
                if ((ex) != null)
                {
                    this.result.value = ex;
                }
            }
            return ;
        }

        // defaulted all parameters starting with #3
        public  void interpretAssignCommon(BinExp e, Function4<Loc,Type,Expression,Expression,UnionExp> fp) {
            return interpretAssignCommon(e, fp, 0);
        }

        public  void stompOverlappedFields(StructLiteralExp sle, VarDeclaration v) {
            if (!v.overlapped.value)
            {
                return ;
            }
            {
                Slice<VarDeclaration> __r973 = sle.sd.fields.opSlice().copy();
                int __key972 = 0;
                for (; (__key972 < __r973.getLength());__key972 += 1) {
                    VarDeclaration v2 = __r973.get(__key972);
                    int i = __key972;
                    if ((v == v2) || !v.isOverlappedWith(v2))
                    {
                        continue;
                    }
                    Expression e = (sle.elements.value.get()).get(i);
                    if (((e.op.value & 0xFF) != 128))
                    {
                        sle.elements.value.get().set(i, voidInitLiteral(e.type.value, v).copy());
                    }
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
                    vd = ve.var.value.isVarDeclaration();
                    oldval = getValue(vd);
                }
                else {
                    DotVarExp dve = e1.isDotVarExp();
                    if ((dve) != null)
                    {
                        Expression ex = dve.e1.value;
                        StructLiteralExp sle = ((ex.op.value & 0xFF) == 49) ? (StructLiteralExp)ex : ((ex.op.value & 0xFF) == 50) ? ((ClassReferenceExp)ex).value : null;
                        VarDeclaration v = ((DotVarExp)e1).var.value.isVarDeclaration();
                        if ((sle == null) || (v == null))
                        {
                            e.error(new BytePtr("CTFE internal error: dotvar assignment"));
                            return CTFEExp.cantexp.value;
                        }
                        if (((sle.ownedByCtfe.value & 0xFF) != 1))
                        {
                            e.error(new BytePtr("cannot modify read-only constant `%s`"), sle.toChars());
                            return CTFEExp.cantexp.value;
                        }
                        int fieldi = ((ex.op.value & 0xFF) == 49) ? findFieldIndexByName(sle.sd, v) : ((ClassReferenceExp)ex).findFieldIndexByName(v);
                        if ((fieldi == -1))
                        {
                            e.error(new BytePtr("CTFE internal error: cannot find field `%s` in `%s`"), v.toChars(), ex.toChars());
                            return CTFEExp.cantexp.value;
                        }
                        assert((0 <= fieldi) && (fieldi < (sle.elements.value.get()).length.value));
                        this.stompOverlappedFields(sle, v);
                        payload = pcopy((ptr((sle.elements.value.get()).get(fieldi))));
                        oldval = payload.get();
                    }
                    else {
                        IndexExp ie = e1.isIndexExp();
                        if ((ie) != null)
                        {
                            assert(((ie.e1.value.type.value.toBasetype().ty.value & 0xFF) != ENUMTY.Taarray));
                            Ref<Expression> aggregate = ref(null);
                            Ref<Long> indexToModify = ref(0L);
                            if (!resolveIndexing(ie, this.istate.value, ptr(aggregate), ptr(indexToModify), true))
                            {
                                return CTFEExp.cantexp.value;
                            }
                            int index = (int)indexToModify.value;
                            {
                                StringExp existingSE = aggregate.value.isStringExp();
                                if ((existingSE) != null)
                                {
                                    if (((existingSE.ownedByCtfe & 0xFF) != 1))
                                    {
                                        e.error(new BytePtr("cannot modify read-only string literal `%s`"), ie.e1.value.toChars());
                                        return CTFEExp.cantexp.value;
                                    }
                                    existingSE.setCodeUnit(index, (int)newval.toInteger());
                                    return null;
                                }
                            }
                            if (((aggregate.value.op.value & 0xFF) != 47))
                            {
                                e.error(new BytePtr("index assignment `%s` is not yet supported in CTFE "), e.toChars());
                                return CTFEExp.cantexp.value;
                            }
                            ArrayLiteralExp existingAE = (ArrayLiteralExp)aggregate.value;
                            if (((existingAE.ownedByCtfe & 0xFF) != 1))
                            {
                                e.error(new BytePtr("cannot modify read-only constant `%s`"), existingAE.toChars());
                                return CTFEExp.cantexp.value;
                            }
                            payload = pcopy((ptr((existingAE.elements.value.get()).get(index))));
                            oldval = payload.get();
                        }
                        else
                        {
                            e.error(new BytePtr("`%s` cannot be evaluated at compile time"), e.toChars());
                            return CTFEExp.cantexp.value;
                        }
                    }
                }
            }
            Type t1b = e1.type.value.toBasetype();
            boolean wantCopy = (t1b.baseElemOf().ty.value & 0xFF) == ENUMTY.Tstruct;
            if (((newval.op.value & 0xFF) == 49) && (oldval != null))
            {
                newval = copyLiteral(newval).copy();
                assignInPlace(oldval, newval);
            }
            else if (wantCopy && ((e.op.value & 0xFF) == 90))
            {
                assert(oldval != null);
                newval = resolveSlice(newval, null);
                if (CTFEExp.isCantExp(newval))
                {
                    e.error(new BytePtr("CTFE internal error: assignment `%s`"), e.toChars());
                    return CTFEExp.cantexp.value;
                }
                assert(((oldval.op.value & 0xFF) == 47));
                assert(((newval.op.value & 0xFF) == 47));
                Ptr<DArray<Expression>> oldelems = ((ArrayLiteralExp)oldval).elements.value;
                Ptr<DArray<Expression>> newelems = ((ArrayLiteralExp)newval).elements.value;
                assert(((oldelems.get()).length.value == (newelems.get()).length.value));
                Type elemtype = oldval.type.value.nextOf();
                {
                    Slice<Expression> __r975 = (oldelems.get()).opSlice().copy();
                    int __key974 = 0;
                    for (; (__key974 < __r975.getLength());__key974 += 1) {
                        Expression oldelem = __r975.get(__key974);
                        int i = __key974;
                        Expression newelem = paintTypeOntoLiteral(elemtype, (newelems.get()).get(i));
                        if (e.e2.value.isLvalue())
                        {
                            {
                                Expression ex = evaluatePostblit(this.istate.value, newelem);
                                if ((ex) != null)
                                {
                                    return ex;
                                }
                            }
                        }
                        {
                            Expression ex = evaluateDtor(this.istate.value, oldelem);
                            if ((ex) != null)
                            {
                                return ex;
                            }
                        }
                        oldelem = newelem;
                    }
                }
            }
            else
            {
                if (wantCopy)
                {
                    newval = copyLiteral(newval).copy();
                }
                if (((t1b.ty.value & 0xFF) == ENUMTY.Tsarray) && ((e.op.value & 0xFF) == 95) && e.e2.value.isLvalue())
                {
                    {
                        Expression ex = evaluatePostblit(this.istate.value, newval);
                        if ((ex) != null)
                        {
                            return ex;
                        }
                    }
                }
                oldval = newval;
            }
            if (vd != null)
            {
                setValue(vd, oldval);
            }
            else
            {
                payload.set(0, oldval);
            }
            if (((e.op.value & 0xFF) == 96))
            {
                return oldval;
            }
            return null;
        }

        public  Expression interpretAssignToSlice(Ptr<UnionExp> pue, BinExp e, Expression e1, Expression newval, boolean isBlockAssignment) {
            long lowerbound = 0L;
            long upperbound = 0L;
            long firstIndex = 0L;
            Expression aggregate = null;
            {
                SliceExp se = e1.isSliceExp();
                if ((se) != null)
                {
                    Expression oldval = interpret(se.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                    long dollar = resolveArrayLength(oldval);
                    if (se.lengthVar.value != null)
                    {
                        Expression dollarExp = new IntegerExp(e1.loc.value, dollar, Type.tsize_t.value);
                        ctfeStack.push(se.lengthVar.value);
                        setValue(se.lengthVar.value, dollarExp);
                    }
                    Expression lwr = interpret(se.lwr.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                    if (exceptionOrCantInterpret(lwr))
                    {
                        if (se.lengthVar.value != null)
                        {
                            ctfeStack.pop(se.lengthVar.value);
                        }
                        return lwr;
                    }
                    Expression upr = interpret(se.upr.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                    if (exceptionOrCantInterpret(upr))
                    {
                        if (se.lengthVar.value != null)
                        {
                            ctfeStack.pop(se.lengthVar.value);
                        }
                        return upr;
                    }
                    if (se.lengthVar.value != null)
                    {
                        ctfeStack.pop(se.lengthVar.value);
                    }
                    long dim = dollar;
                    lowerbound = lwr != null ? lwr.toInteger() : 0L;
                    upperbound = upr != null ? upr.toInteger() : dim;
                    if ((lowerbound < 0L) || (dim < upperbound))
                    {
                        e.error(new BytePtr("array bounds `[0..%llu]` exceeded in slice `[%llu..%llu]`"), dim, lowerbound, upperbound);
                        return CTFEExp.cantexp.value;
                    }
                    aggregate = oldval;
                    firstIndex = lowerbound;
                    {
                        SliceExp oldse = aggregate.isSliceExp();
                        if ((oldse) != null)
                        {
                            if ((oldse.upr.value.toInteger() < upperbound + oldse.lwr.value.toInteger()))
                            {
                                e.error(new BytePtr("slice `[%llu..%llu]` exceeds array bounds `[0..%llu]`"), lowerbound, upperbound, oldse.upr.value.toInteger() - oldse.lwr.value.toInteger());
                                return CTFEExp.cantexp.value;
                            }
                            aggregate = oldse.e1.value;
                            firstIndex = lowerbound + oldse.lwr.value.toInteger();
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
                            upperbound = (long)(ale.elements.value.get()).length.value;
                        }
                        else {
                            StringExp se = e1.isStringExp();
                            if ((se) != null)
                            {
                                lowerbound = 0L;
                                upperbound = (long)se.len.value;
                            }
                            else if (((e1.op.value & 0xFF) == 13))
                            {
                                lowerbound = 0L;
                                upperbound = 0L;
                            }
                            else
                            {
                                throw new AssertionError("Unreachable code!");
                            }
                        }
                    }
                    aggregate = e1;
                    firstIndex = lowerbound;
                }
            }
            if ((upperbound == lowerbound))
            {
                return newval;
            }
            if (!isBlockAssignment)
            {
                long srclen = resolveArrayLength(newval);
                if ((srclen != upperbound - lowerbound))
                {
                    e.error(new BytePtr("array length mismatch assigning `[0..%llu]` to `[%llu..%llu]`"), srclen, lowerbound, upperbound);
                    return CTFEExp.cantexp.value;
                }
            }
            {
                StringExp existingSE = aggregate.isStringExp();
                if ((existingSE) != null)
                {
                    if (((existingSE.ownedByCtfe & 0xFF) != 1))
                    {
                        e.error(new BytePtr("cannot modify read-only string literal `%s`"), existingSE.toChars());
                        return CTFEExp.cantexp.value;
                    }
                    {
                        SliceExp se = newval.isSliceExp();
                        if ((se) != null)
                        {
                            Expression aggr2 = se.e1.value;
                            long srclower = se.lwr.value.toInteger();
                            long srcupper = se.upr.value.toInteger();
                            if ((pequals(aggregate, aggr2)) && (lowerbound < srcupper) && (srclower < upperbound))
                            {
                                e.error(new BytePtr("overlapping slice assignment `[%llu..%llu] = [%llu..%llu]`"), lowerbound, upperbound, srclower, srcupper);
                                return CTFEExp.cantexp.value;
                            }
                            Expression orignewval = newval;
                            newval = resolveSlice(newval, null);
                            if (CTFEExp.isCantExp(newval))
                            {
                                e.error(new BytePtr("CTFE internal error: slice `%s`"), orignewval.toChars());
                                return CTFEExp.cantexp.value;
                            }
                            assert(((newval.op.value & 0xFF) != 31));
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
                    {
                        return null;
                    }
                    SliceExp retslice = new SliceExp(e.loc.value, existingSE, new IntegerExp(e.loc.value, firstIndex, Type.tsize_t.value), new IntegerExp(e.loc.value, firstIndex + upperbound - lowerbound, Type.tsize_t.value));
                    retslice.type.value = e.type.value;
                    return interpret(pue, (Expression)retslice, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                }
            }
            {
                ArrayLiteralExp existingAE = aggregate.isArrayLiteralExp();
                if ((existingAE) != null)
                {
                    if (((existingAE.ownedByCtfe & 0xFF) != 1))
                    {
                        e.error(new BytePtr("cannot modify read-only constant `%s`"), existingAE.toChars());
                        return CTFEExp.cantexp.value;
                    }
                    if (((newval.op.value & 0xFF) == 31) && !isBlockAssignment)
                    {
                        SliceExp se = (SliceExp)newval;
                        Expression aggr2 = se.e1.value;
                        long srclower = se.lwr.value.toInteger();
                        long srcupper = se.upr.value.toInteger();
                        boolean wantCopy = (newval.type.value.toBasetype().nextOf().baseElemOf().ty.value & 0xFF) == ENUMTY.Tstruct;
                        if (wantCopy)
                        {
                            assert(((aggr2.op.value & 0xFF) == 47));
                            Ptr<DArray<Expression>> oldelems = existingAE.elements.value;
                            Ptr<DArray<Expression>> newelems = ((ArrayLiteralExp)aggr2).elements.value;
                            Type elemtype = aggregate.type.value.nextOf();
                            boolean needsPostblit = e.e2.value.isLvalue();
                            if ((pequals(aggregate, aggr2)) && (srclower < lowerbound) && (lowerbound < srcupper))
                            {
                                {
                                    long i = upperbound - lowerbound;
                                    for (; (0L < i--);){
                                        Expression oldelem = (oldelems.get()).get((int)(i + firstIndex));
                                        Expression newelem = (newelems.get()).get((int)(i + srclower));
                                        newelem = copyLiteral(newelem).copy();
                                        newelem.type.value = elemtype;
                                        if (needsPostblit)
                                        {
                                            {
                                                Expression x = evaluatePostblit(this.istate.value, newelem);
                                                if ((x) != null)
                                                {
                                                    return x;
                                                }
                                            }
                                        }
                                        {
                                            Expression x = evaluateDtor(this.istate.value, oldelem);
                                            if ((x) != null)
                                            {
                                                return x;
                                            }
                                        }
                                        oldelems.get().set((int)(lowerbound + i), newelem);
                                    }
                                }
                            }
                            else
                            {
                                {
                                    int i = 0;
                                    for (; ((long)i < upperbound - lowerbound);i++){
                                        Expression oldelem = (oldelems.get()).get((int)((long)i + firstIndex));
                                        Expression newelem = (newelems.get()).get((int)((long)i + srclower));
                                        newelem = copyLiteral(newelem).copy();
                                        newelem.type.value = elemtype;
                                        if (needsPostblit)
                                        {
                                            {
                                                Expression x = evaluatePostblit(this.istate.value, newelem);
                                                if ((x) != null)
                                                {
                                                    return x;
                                                }
                                            }
                                        }
                                        {
                                            Expression x = evaluateDtor(this.istate.value, oldelem);
                                            if ((x) != null)
                                            {
                                                return x;
                                            }
                                        }
                                        oldelems.get().set((int)(lowerbound + (long)i), newelem);
                                    }
                                }
                            }
                            return newval;
                        }
                        if ((pequals(aggregate, aggr2)) && (lowerbound < srcupper) && (srclower < upperbound))
                        {
                            e.error(new BytePtr("overlapping slice assignment `[%llu..%llu] = [%llu..%llu]`"), lowerbound, upperbound, srclower, srcupper);
                            return CTFEExp.cantexp.value;
                        }
                        Expression orignewval = newval;
                        newval = resolveSlice(newval, null);
                        if (CTFEExp.isCantExp(newval))
                        {
                            e.error(new BytePtr("CTFE internal error: slice `%s`"), orignewval.toChars());
                            return CTFEExp.cantexp.value;
                        }
                        assert(((newval.op.value & 0xFF) != 31));
                    }
                    if (((newval.op.value & 0xFF) == 121) && !isBlockAssignment)
                    {
                        sliceAssignArrayLiteralFromString(existingAE, (StringExp)newval, (int)firstIndex);
                        return newval;
                    }
                    if (((newval.op.value & 0xFF) == 47) && !isBlockAssignment)
                    {
                        Ptr<DArray<Expression>> oldelems = existingAE.elements.value;
                        Ptr<DArray<Expression>> newelems = ((ArrayLiteralExp)newval).elements.value;
                        Type elemtype = existingAE.type.value.nextOf();
                        boolean needsPostblit = ((e.op.value & 0xFF) != 96) && e.e2.value.isLvalue();
                        {
                            Slice<Expression> __r979 = (newelems.get()).opSlice().copy();
                            int __key978 = 0;
                            for (; (__key978 < __r979.getLength());__key978 += 1) {
                                Expression newelem = __r979.get(__key978);
                                int j = __key978;
                                newelem = paintTypeOntoLiteral(elemtype, newelem);
                                if (needsPostblit)
                                {
                                    Expression x = evaluatePostblit(this.istate.value, newelem);
                                    if (exceptionOrCantInterpret(x))
                                    {
                                        return x;
                                    }
                                }
                                oldelems.get().set((int)((long)j + firstIndex), newelem);
                            }
                        }
                        return newval;
                    }
                    Type tn = newval.type.value.toBasetype();
                    boolean wantRef = ((tn.ty.value & 0xFF) == ENUMTY.Tarray) || isAssocArray(tn) || ((tn.ty.value & 0xFF) == ENUMTY.Tclass);
                    boolean cow = ((newval.op.value & 0xFF) != 49) && ((newval.op.value & 0xFF) != 47) && ((newval.op.value & 0xFF) != 121);
                    Type tb = tn.baseElemOf();
                    StructDeclaration sd = ((tb.ty.value & 0xFF) == ENUMTY.Tstruct) ? ((TypeStruct)tb).sym.value : null;
                    RecursiveBlock rb = new RecursiveBlock(null, null, false, false, false).copy();
                    rb.istate.value = this.istate.value;
                    rb.newval.value = newval;
                    rb.refCopy.value = wantRef || cow;
                    rb.needsPostblit.value = (sd != null) && (sd.postblit.value != null) && ((e.op.value & 0xFF) != 96) && e.e2.value.isLvalue();
                    rb.needsDtor.value = (sd != null) && (sd.dtor.value != null) && ((e.op.value & 0xFF) == 90);
                    {
                        Expression ex = rb.assignTo(existingAE, (int)lowerbound, (int)upperbound);
                        if ((ex) != null)
                        {
                            return ex;
                        }
                    }
                    if ((this.goal == CtfeGoal.ctfeNeedNothing))
                    {
                        return null;
                    }
                    SliceExp retslice = new SliceExp(e.loc.value, existingAE, new IntegerExp(e.loc.value, firstIndex, Type.tsize_t.value), new IntegerExp(e.loc.value, firstIndex + upperbound - lowerbound, Type.tsize_t.value));
                    retslice.type.value = e.type.value;
                    return interpret(pue, (Expression)retslice, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                }
            }
            e.error(new BytePtr("slice operation `%s = %s` cannot be evaluated at compile time"), e1.toChars(), newval.toChars());
            return CTFEExp.cantexp.value;
        }

        public  void visit(AssignExp e) {
            this.interpretAssignCommon(e, null, 0);
        }

        public  void visit(BinAssignExp e) {
            switch ((e.op.value & 0xFF))
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
            if (((e.op.value & 0xFF) == 93))
            {
                this.interpretAssignCommon(e, dinterpret::Add, 1);
            }
            else
            {
                this.interpretAssignCommon(e, dinterpret::Min, 1);
            }
        }

        public static int isPointerCmpExp(Expression e, Ptr<Expression> p1, Ptr<Expression> p2) {
            int ret = 1;
            for (; ((e.op.value & 0xFF) == 91);){
                ret *= -1;
                e = ((NotExp)e).e1.value;
            }
            switch ((e.op.value & 0xFF))
            {
                case 54:
                case 56:
                    ret *= -1;
                case 55:
                case 57:
                    p1.set(0, ((BinExp)e).e1.value);
                    p2.set(0, ((BinExp)e).e2.value);
                    if (!(isPointer((p1.get()).type.value) && isPointer((p2.get()).type.value)))
                    {
                        ret = 0;
                    }
                    break;
                default:
                ret = 0;
                break;
            }
            return ret;
        }

        public  void interpretFourPointerRelation(Ptr<UnionExp> pue, BinExp e) {
            assert(((e.op.value & 0xFF) == 101) || ((e.op.value & 0xFF) == 102));
            Ref<Expression> p1 = ref(null);
            Ref<Expression> p2 = ref(null);
            Ref<Expression> p3 = ref(null);
            Ref<Expression> p4 = ref(null);
            int dir1 = isPointerCmpExp(e.e1.value, ptr(p1), ptr(p2));
            int dir2 = isPointerCmpExp(e.e2.value, ptr(p3), ptr(p4));
            if ((dir1 == 0) || (dir2 == 0))
            {
                this.result.value = null;
                return ;
            }
            Ref<UnionExp> ue1 = ref(null);
            Ref<UnionExp> ue2 = ref(null);
            Ref<UnionExp> ue3 = ref(null);
            Ref<UnionExp> ue4 = ref(null);
            p1.value = interpret(ptr(ue1), p1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(p1.value))
            {
                return ;
            }
            p2.value = interpret(ptr(ue2), p2.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(p2.value))
            {
                return ;
            }
            Ref<Long> ofs1 = ref(0L);
            Ref<Long> ofs2 = ref(0L);
            Expression agg1 = getAggregateFromPointer(p1.value, ptr(ofs1));
            Expression agg2 = getAggregateFromPointer(p2.value, ptr(ofs2));
            if (!pointToSameMemoryBlock(agg1, agg2) && ((agg1.op.value & 0xFF) != 13) && ((agg2.op.value & 0xFF) != 13))
            {
                p3.value = interpret(ptr(ue3), p3.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (CTFEExp.isCantExp(p3.value))
                {
                    return ;
                }
                Expression except = null;
                if (exceptionOrCantInterpret(p3.value))
                {
                    except = p3.value;
                }
                else
                {
                    p4.value = interpret(ptr(ue4), p4.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                    if (CTFEExp.isCantExp(p4.value))
                    {
                        this.result.value = p4.value;
                        return ;
                    }
                    if (exceptionOrCantInterpret(p4.value))
                    {
                        except = p4.value;
                    }
                }
                if (except != null)
                {
                    e.error(new BytePtr("comparison `%s` of pointers to unrelated memory blocks remains indeterminate at compile time because exception `%s` was thrown while evaluating `%s`"), e.e1.value.toChars(), except.toChars(), e.e2.value.toChars());
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                Ref<Long> ofs3 = ref(0L);
                Ref<Long> ofs4 = ref(0L);
                Expression agg3 = getAggregateFromPointer(p3.value, ptr(ofs3));
                Expression agg4 = getAggregateFromPointer(p4.value, ptr(ofs4));
                if ((dir1 == dir2) && pointToSameMemoryBlock(agg1, agg4) && pointToSameMemoryBlock(agg2, agg3) || (dir1 != dir2) && pointToSameMemoryBlock(agg1, agg3) && pointToSameMemoryBlock(agg2, agg4))
                {
                    (pue) = new UnionExp(new IntegerExp(e.loc.value, ((e.op.value & 0xFF) == 101) ? 0 : 1, e.type.value));
                    this.result.value = (pue.get()).exp();
                    return ;
                }
                e.error(new BytePtr("comparison `%s` of pointers to unrelated memory blocks is indeterminate at compile time, even when combined with `%s`."), e.e1.value.toChars(), e.e2.value.toChars());
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            boolean nott = false;
            Expression ex = e.e1.value;
            for (; 1 != 0;){
                {
                    NotExp ne = ex.isNotExp();
                    if ((ne) != null)
                    {
                        nott = !nott;
                        ex = ne.e1.value;
                    }
                    else
                    {
                        break;
                    }
                }
            }
            Function1<Byte,Byte> negateRelation = new Function1<Byte,Byte>(){
                public Byte invoke(Byte op) {
                    Ref<Byte> op_ref = ref(op);
                    switch ((op_ref.value & 0xFF))
                    {
                        case 57:
                            op_ref.value = TOK.lessThan;
                            break;
                        case 55:
                            op_ref.value = TOK.lessOrEqual;
                            break;
                        case 56:
                            op_ref.value = TOK.greaterThan;
                            break;
                        case 54:
                            op_ref.value = TOK.greaterOrEqual;
                            break;
                        default:
                        throw new AssertionError("Unreachable code!");
                    }
                    return op_ref.value;
                }
            };
            byte cmpop = nott ? (byte)(negateRelation.invoke(ex.op.value) & 0xFF) : (byte)(ex.op.value & 0xFF);
            int cmp = comparePointers(cmpop, agg1, ofs1.value, agg2, ofs2.value);
            assert((cmp >= 0));
            if (((e.op.value & 0xFF) == 101) && (cmp == 1) || ((e.op.value & 0xFF) == 102) && (cmp == 0))
            {
                this.result.value = interpret(pue, e.e2.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                return ;
            }
            (pue) = new UnionExp(new IntegerExp(e.loc.value, ((e.op.value & 0xFF) == 101) ? 0 : 1, e.type.value));
            this.result.value = (pue.get()).exp();
        }

        public  void visit(LogicalExp e) {
            this.interpretFourPointerRelation(this.pue, e);
            if (this.result.value != null)
            {
                return ;
            }
            this.result.value = interpret(e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(this.result.value))
            {
                return ;
            }
            int res = 0;
            boolean andand = (e.op.value & 0xFF) == 101;
            if (andand ? this.result.value.isBool(false) : isTrueBool(this.result.value))
            {
                res = (!andand ? 1 : 0);
            }
            else if (andand ? isTrueBool(this.result.value) : this.result.value.isBool(false))
            {
                Ref<UnionExp> ue2 = ref(new UnionExp().copy());
                this.result.value = interpret(ptr(ue2), e.e2.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(this.result.value))
                {
                    return ;
                }
                if (((this.result.value.op.value & 0xFF) == 232))
                {
                    assert(((e.type.value.ty.value & 0xFF) == ENUMTY.Tvoid));
                    this.result.value = null;
                    return ;
                }
                if (this.result.value.isBool(false))
                {
                    res = 0;
                }
                else if (isTrueBool(this.result.value))
                {
                    res = 1;
                }
                else
                {
                    this.result.value.error(new BytePtr("`%s` does not evaluate to a `bool`"), this.result.value.toChars());
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
            }
            else
            {
                this.result.value.error(new BytePtr("`%s` cannot be interpreted as a `bool`"), this.result.value.toChars());
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            if ((this.goal != CtfeGoal.ctfeNeedNothing))
            {
                (this.pue) = new UnionExp(new IntegerExp(e.loc.value, res, e.type.value));
                this.result.value = (this.pue.get()).exp();
            }
        }

        public  void showCtfeBackTrace(CallExp callingExp, FuncDeclaration fd) {
            if ((CtfeStatus.stackTraceCallsToSuppress > 0))
            {
                CtfeStatus.stackTraceCallsToSuppress -= 1;
                return ;
            }
            errorSupplemental(callingExp.loc.value, new BytePtr("called from here: `%s`"), callingExp.toChars());
            if ((CtfeStatus.callDepth < 6) || global.params.verbose)
            {
                return ;
            }
            int numToSuppress = 0;
            int recurseCount = 0;
            int depthSoFar = 0;
            Ptr<InterState> lastRecurse = this.istate.value;
            {
                Ptr<InterState> cur = this.istate.value;
                for (; cur != null;cur = (cur.get()).caller){
                    if ((pequals((cur.get()).fd, fd)))
                    {
                        recurseCount += 1;
                        numToSuppress = depthSoFar;
                        lastRecurse = cur;
                    }
                    depthSoFar += 1;
                }
            }
            if ((recurseCount < 2))
            {
                return ;
            }
            errorSupplemental(fd.loc.value, new BytePtr("%d recursive calls to function `%s`"), recurseCount, fd.toChars());
            {
                Ptr<InterState> cur = this.istate.value;
                for (; (!pequals((cur.get()).fd, fd));cur = (cur.get()).caller){
                    errorSupplemental((cur.get()).fd.loc.value, new BytePtr("recursively called from function `%s`"), (cur.get()).fd.toChars());
                }
            }
            Ptr<InterState> cur = this.istate.value;
            for (; ((lastRecurse.get()).caller != null) && (pequals((cur.get()).fd, ((lastRecurse.get()).caller.get()).fd));){
                cur = (cur.get()).caller;
                lastRecurse = (lastRecurse.get()).caller;
                numToSuppress += 1;
            }
            CtfeStatus.stackTraceCallsToSuppress = numToSuppress;
        }

        public  void visit(CallExp e) {
            Expression pthis = null;
            FuncDeclaration fd = null;
            Expression ecall = interpret(e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(ecall))
            {
                return ;
            }
            {
                DotVarExp dve = ecall.isDotVarExp();
                if ((dve) != null)
                {
                    pthis = dve.e1.value;
                    fd = dve.var.value.isFuncDeclaration();
                    assert(fd != null);
                    {
                        DotTypeExp dte = pthis.isDotTypeExp();
                        if ((dte) != null)
                        {
                            pthis = dte.e1.value;
                        }
                    }
                }
                else {
                    VarExp ve = ecall.isVarExp();
                    if ((ve) != null)
                    {
                        fd = ve.var.value.isFuncDeclaration();
                        assert(fd != null);
                        if ((pequals(fd.ident.value, Id.__ArrayPostblit)) || (pequals(fd.ident.value, Id.__ArrayDtor)))
                        {
                            assert(((e.arguments.value.get()).length.value == 1));
                            Expression ea = (e.arguments.value.get()).get(0);
                            {
                                SliceExp se = ea.isSliceExp();
                                if ((se) != null)
                                {
                                    ea = se.e1.value;
                                }
                            }
                            {
                                CastExp ce = ea.isCastExp();
                                if ((ce) != null)
                                {
                                    ea = ce.e1.value;
                                }
                            }
                            if (((ea.op.value & 0xFF) == 26) || ((ea.op.value & 0xFF) == 25))
                            {
                                this.result.value = getVarExp(e.loc.value, this.istate.value, ((SymbolExp)ea).var.value, CtfeGoal.ctfeNeedRvalue);
                            }
                            else {
                                AddrExp ae = ea.isAddrExp();
                                if ((ae) != null)
                                {
                                    this.result.value = interpret(ae.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                                }
                                else {
                                    ArrayLiteralExp ale = ea.isArrayLiteralExp();
                                    if ((ale) != null)
                                    {
                                        this.result.value = interpret((Expression)ale, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                                    }
                                    else
                                    {
                                        throw new AssertionError("Unreachable code!");
                                    }
                                }
                            }
                            if (CTFEExp.isCantExp(this.result.value))
                            {
                                return ;
                            }
                            if ((pequals(fd.ident.value, Id.__ArrayPostblit)))
                            {
                                this.result.value = evaluatePostblit(this.istate.value, this.result.value);
                            }
                            else
                            {
                                this.result.value = evaluateDtor(this.istate.value, this.result.value);
                            }
                            if (this.result.value == null)
                            {
                                this.result.value = CTFEExp.voidexp;
                            }
                            return ;
                        }
                    }
                    else {
                        SymOffExp soe = ecall.isSymOffExp();
                        if ((soe) != null)
                        {
                            fd = soe.var.value.isFuncDeclaration();
                            assert((fd != null) && (soe.offset.value == 0L));
                        }
                        else {
                            DelegateExp de = ecall.isDelegateExp();
                            if ((de) != null)
                            {
                                fd = de.func.value;
                                pthis = de.e1.value;
                                {
                                    VarExp ve = pthis.isVarExp();
                                    if ((ve) != null)
                                    {
                                        if ((pequals(ve.var.value, fd)))
                                        {
                                            pthis = null;
                                        }
                                    }
                                }
                            }
                            else {
                                FuncExp fe = ecall.isFuncExp();
                                if ((fe) != null)
                                {
                                    fd = fe.fd.value;
                                }
                                else
                                {
                                    e.error(new BytePtr("cannot call `%s` at compile time"), e.toChars());
                                    this.result.value = CTFEExp.cantexp.value;
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
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            if (pthis != null)
            {
                assert(!fd.isNested() || fd.needThis());
                if (((pthis.op.value & 0xFF) == 42))
                {
                    pthis.error(new BytePtr("static variable `%s` cannot be read at compile time"), pthis.toChars());
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                assert(pthis != null);
                if (((pthis.op.value & 0xFF) == 13))
                {
                    assert(((pthis.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tclass));
                    e.error(new BytePtr("function call through null class reference `%s`"), pthis.toChars());
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                assert(((pthis.op.value & 0xFF) == 49) || ((pthis.op.value & 0xFF) == 50));
                if (fd.isVirtual() && !e.directcall)
                {
                    assert(((pthis.op.value & 0xFF) == 50));
                    ClassDeclaration cd = ((ClassReferenceExp)pthis).originalClass();
                    fd = cd.findFunc(fd.ident.value, (TypeFunction)fd.type.value);
                    assert(fd != null);
                }
            }
            if ((fd != null) && (fd.semanticRun.value >= PASS.semantic3done) && fd.semantic3Errors.value)
            {
                e.error(new BytePtr("CTFE failed because of previous errors in `%s`"), fd.toChars());
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            this.result.value = evaluateIfBuiltin(this.pue, this.istate.value, e.loc.value, fd, e.arguments.value, pthis);
            if (this.result.value != null)
            {
                return ;
            }
            if (fd.fbody.value == null)
            {
                e.error(new BytePtr("`%s` cannot be interpreted at compile time, because it has no available source code"), fd.toChars());
                this.result.value = CTFEExp.showcontext;
                return ;
            }
            this.result.value = interpretFunction(this.pue, fd, this.istate.value, e.arguments.value, pthis);
            if (((this.result.value.op.value & 0xFF) == 232))
            {
                return ;
            }
            if (!exceptionOrCantInterpret(this.result.value))
            {
                if ((this.goal != CtfeGoal.ctfeNeedLvalue))
                {
                    if ((pequals(this.result.value, (this.pue.get()).exp())))
                    {
                        this.result.value = (this.pue.get()).copy();
                    }
                    this.result.value = interpret(this.pue, this.result.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                }
            }
            if (!exceptionOrCantInterpret(this.result.value))
            {
                this.result.value = paintTypeOntoLiteral(e.type.value, this.result.value);
                this.result.value.loc.value = e.loc.value.copy();
            }
            else if (CTFEExp.isCantExp(this.result.value) && (global.gag.value == 0))
            {
                this.showCtfeBackTrace(e, fd);
            }
        }

        public  void visit(CommaExp e) {
            Ref<InterState> istateComma = ref(new InterState());
            if ((this.istate.value == null) && ((firstComma(e.e1.value).op.value & 0xFF) == 38))
            {
                ctfeStack.startFrame(null);
                this.istate.value = ptr(istateComma);
            }
            Function0<Void> endTempStackFrame = new Function0<Void>(){
                public Void invoke() {
                    if ((istate.value == ptr(istateComma)))
                    {
                        ctfeStack.endFrame();
                    }
                    return null;
                }
            };
            this.result.value = CTFEExp.cantexp.value;
            if (((e.e1.value.op.value & 0xFF) == 38) && ((e.e2.value.op.value & 0xFF) == 26) && (pequals(((DeclarationExp)e.e1.value).declaration.value, ((VarExp)e.e2.value).var.value)) && ((((VarExp)e.e2.value).var.value.storage_class.value & 68719476736L) != 0))
            {
                VarExp ve = (VarExp)e.e2.value;
                VarDeclaration v = ve.var.value.isVarDeclaration();
                ctfeStack.push(v);
                if ((v._init.value == null) && (getValue(v) == null))
                {
                    setValue(v, copyLiteral(v.type.value.defaultInitLiteral(e.loc.value)).copy());
                }
                if (getValue(v) == null)
                {
                    Expression newval = initializerToExpression(v._init.value, null);
                    newval = interpret(newval, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                    if (this.exceptionOrCant(newval))
                    {
                        endTempStackFrame.invoke();
                        return ;
                    }
                    if (((newval.op.value & 0xFF) != 232))
                    {
                        setValueWithoutChecking(v, copyLiteral(newval).copy());
                    }
                }
            }
            else
            {
                Ref<UnionExp> ue = ref(null);
                Expression e1 = interpret(ptr(ue), e.e1.value, this.istate.value, CtfeGoal.ctfeNeedNothing);
                if (this.exceptionOrCant(e1))
                {
                    endTempStackFrame.invoke();
                    return ;
                }
            }
            this.result.value = interpret(this.pue, e.e2.value, this.istate.value, this.goal);
            endTempStackFrame.invoke();
            return ;
        }

        public  void visit(CondExp e) {
            Ref<UnionExp> uecond = ref(null);
            Expression econd = null;
            econd = interpret(ptr(uecond), e.econd.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(econd))
            {
                return ;
            }
            if (isPointer(e.econd.value.type.value))
            {
                if (((econd.op.value & 0xFF) != 13))
                {
                    ptr(uecond) = new UnionExp(new IntegerExp(e.loc.value, 1, Type.tbool.value));
                    econd = uecond.value.exp();
                }
            }
            if (isTrueBool(econd))
            {
                this.result.value = interpret(this.pue, e.e1.value, this.istate.value, this.goal);
            }
            else if (econd.isBool(false))
            {
                this.result.value = interpret(this.pue, e.e2.value, this.istate.value, this.goal);
            }
            else
            {
                e.error(new BytePtr("`%s` does not evaluate to boolean result at compile time"), e.econd.value.toChars());
                this.result.value = CTFEExp.cantexp.value;
            }
        }

        public  void visit(ArrayLengthExp e) {
            Ref<UnionExp> ue1 = ref(new UnionExp().copy());
            Expression e1 = interpret(ptr(ue1), e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            assert(e1 != null);
            if (this.exceptionOrCant(e1))
            {
                return ;
            }
            if (((e1.op.value & 0xFF) != 121) && ((e1.op.value & 0xFF) != 47) && ((e1.op.value & 0xFF) != 31) && ((e1.op.value & 0xFF) != 13))
            {
                e.error(new BytePtr("`%s` cannot be evaluated at compile time"), e.toChars());
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            (this.pue) = new UnionExp(new IntegerExp(e.loc.value, resolveArrayLength(e1), e.type.value));
            this.result.value = (this.pue.get()).exp();
        }

        public static Expression interpretVectorToArray(Ptr<UnionExp> pue, VectorExp e) {
            {
                ArrayLiteralExp ale = e.e1.value.isArrayLiteralExp();
                if ((ale) != null)
                {
                    return ale;
                }
            }
            if (((e.e1.value.op.value & 0xFF) == 135) || ((e.e1.value.op.value & 0xFF) == 140))
            {
                Ptr<DArray<Expression>> elements = refPtr(new DArray<Expression>(e.dim.value));
                {
                    Slice<Expression> __r980 = (elements.get()).opSlice().copy();
                    int __key981 = 0;
                    for (; (__key981 < __r980.getLength());__key981 += 1) {
                        Expression element = __r980.get(__key981);
                        element = copyLiteral(e.e1.value).copy();
                    }
                }
                Type type = ((e.type.value.ty.value & 0xFF) == ENUMTY.Tvector) ? e.type.value.isTypeVector().basetype.value : e.type.value.isTypeSArray();
                assert(type != null);
                (pue) = new UnionExp(new ArrayLiteralExp(e.loc.value, type, elements));
                ArrayLiteralExp ale = (ArrayLiteralExp)(pue.get()).exp();
                ale.ownedByCtfe = OwnedBy.ctfe;
                return ale;
            }
            return e;
        }

        public  void visit(VectorExp e) {
            if (((e.ownedByCtfe & 0xFF) >= 1))
            {
                this.result.value = e;
                return ;
            }
            Expression e1 = interpret(this.pue, e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            assert(e1 != null);
            if (this.exceptionOrCant(e1))
            {
                return ;
            }
            if (((e1.op.value & 0xFF) != 47) && ((e1.op.value & 0xFF) != 135) && ((e1.op.value & 0xFF) != 140))
            {
                e.error(new BytePtr("`%s` cannot be evaluated at compile time"), e.toChars());
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            if ((pequals(e1, (this.pue.get()).exp())))
            {
                e1 = (this.pue.get()).copy();
            }
            (this.pue) = new UnionExp(new VectorExp(e.loc.value, e1, e.to));
            VectorExp ve = (VectorExp)(this.pue.get()).exp();
            ve.type.value = e.type.value;
            ve.dim.value = e.dim.value;
            ve.ownedByCtfe = OwnedBy.ctfe;
            this.result.value = ve;
        }

        public  void visit(VectorArrayExp e) {
            Expression e1 = interpret(this.pue, e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            assert(e1 != null);
            if (this.exceptionOrCant(e1))
            {
                return ;
            }
            {
                VectorExp ve = e1.isVectorExp();
                if ((ve) != null)
                {
                    this.result.value = interpretVectorToArray(this.pue, ve);
                    if (((this.result.value.op.value & 0xFF) != 229))
                    {
                        return ;
                    }
                }
            }
            e.error(new BytePtr("`%s` cannot be evaluated at compile time"), e.toChars());
            this.result.value = CTFEExp.cantexp.value;
        }

        public  void visit(DelegatePtrExp e) {
            Expression e1 = interpret(this.pue, e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            assert(e1 != null);
            if (this.exceptionOrCant(e1))
            {
                return ;
            }
            e.error(new BytePtr("`%s` cannot be evaluated at compile time"), e.toChars());
            this.result.value = CTFEExp.cantexp.value;
        }

        public  void visit(DelegateFuncptrExp e) {
            Expression e1 = interpret(this.pue, e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            assert(e1 != null);
            if (this.exceptionOrCant(e1))
            {
                return ;
            }
            e.error(new BytePtr("`%s` cannot be evaluated at compile time"), e.toChars());
            this.result.value = CTFEExp.cantexp.value;
        }

        public static boolean resolveIndexing(IndexExp e, Ptr<InterState> istate, Ptr<Expression> pagg, Ptr<Long> pidx, boolean modify) {
            assert(((e.e1.value.type.value.toBasetype().ty.value & 0xFF) != ENUMTY.Taarray));
            if (((e.e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tpointer))
            {
                Expression e1 = interpret(e.e1.value, istate, CtfeGoal.ctfeNeedRvalue);
                if (exceptionOrCantInterpret(e1))
                {
                    return false;
                }
                Expression e2 = interpret(e.e2.value, istate, CtfeGoal.ctfeNeedRvalue);
                if (exceptionOrCantInterpret(e2))
                {
                    return false;
                }
                long indx = (long)e2.toInteger();
                Ref<Long> ofs = ref(0L);
                Expression agg = getAggregateFromPointer(e1, ptr(ofs));
                if (((agg.op.value & 0xFF) == 13))
                {
                    e.error(new BytePtr("cannot index through null pointer `%s`"), e.e1.value.toChars());
                    return false;
                }
                if (((agg.op.value & 0xFF) == 135))
                {
                    e.error(new BytePtr("cannot index through invalid pointer `%s` of value `%s`"), e.e1.value.toChars(), e1.toChars());
                    return false;
                }
                if (((agg.op.value & 0xFF) == 25))
                {
                    e.error(new BytePtr("mutable variable `%s` cannot be %s at compile time, even through a pointer"), modify ? new BytePtr("modified") : new BytePtr("read"), ((SymOffExp)agg).var.value.toChars());
                    return false;
                }
                if (((agg.op.value & 0xFF) == 47) || ((agg.op.value & 0xFF) == 121))
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
            Expression e1 = interpret(e.e1.value, istate, CtfeGoal.ctfeNeedRvalue);
            if (exceptionOrCantInterpret(e1))
            {
                return false;
            }
            if (((e1.op.value & 0xFF) == 13))
            {
                e.error(new BytePtr("cannot index null array `%s`"), e.e1.value.toChars());
                return false;
            }
            {
                VectorExp ve = e1.isVectorExp();
                if ((ve) != null)
                {
                    Ref<UnionExp> ue = ref(null);
                    e1 = interpretVectorToArray(ptr(ue), ve);
                    e1 = (pequals(e1, ue.value.exp())) ? ue.value.copy() : e1;
                }
            }
            long len = 0L;
            if (((e1.op.value & 0xFF) == 26) && ((e1.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                len = e1.type.value.toBasetype().isTypeSArray().dim.value.toInteger();
            }
            else
            {
                if (((e1.op.value & 0xFF) != 47) && ((e1.op.value & 0xFF) != 121) && ((e1.op.value & 0xFF) != 31) && ((e1.op.value & 0xFF) != 229))
                {
                    e.error(new BytePtr("cannot determine length of `%s` at compile time"), e.e1.value.toChars());
                    return false;
                }
                len = resolveArrayLength(e1);
            }
            if (e.lengthVar.value != null)
            {
                Expression dollarExp = new IntegerExp(e.loc.value, len, Type.tsize_t.value);
                ctfeStack.push(e.lengthVar.value);
                setValue(e.lengthVar.value, dollarExp);
            }
            Expression e2 = interpret(e.e2.value, istate, CtfeGoal.ctfeNeedRvalue);
            if (e.lengthVar.value != null)
            {
                ctfeStack.pop(e.lengthVar.value);
            }
            if (exceptionOrCantInterpret(e2))
            {
                return false;
            }
            if (((e2.op.value & 0xFF) != 135))
            {
                e.error(new BytePtr("CTFE internal error: non-integral index `[%s]`"), e.e2.value.toChars());
                return false;
            }
            {
                SliceExp se = e1.isSliceExp();
                if ((se) != null)
                {
                    long index = e2.toInteger();
                    long ilwr = se.lwr.value.toInteger();
                    long iupr = se.upr.value.toInteger();
                    if ((index > iupr - ilwr))
                    {
                        e.error(new BytePtr("index %llu exceeds array length %llu"), index, iupr - ilwr);
                        return false;
                    }
                    pagg.set(0, ((SliceExp)e1).e1.value);
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
            if (((e.e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tpointer))
            {
                Ref<Expression> agg = ref(null);
                Ref<Long> indexToAccess = ref(0L);
                if (!resolveIndexing(e, this.istate.value, ptr(agg), ptr(indexToAccess), false))
                {
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                if (((agg.value.op.value & 0xFF) == 47) || ((agg.value.op.value & 0xFF) == 121))
                {
                    if ((this.goal == CtfeGoal.ctfeNeedLvalue))
                    {
                        (this.pue) = new UnionExp(new IndexExp(e.loc.value, agg.value, new IntegerExp(e.e2.value.loc.value, indexToAccess.value, e.e2.value.type.value)));
                        this.result.value = (this.pue.get()).exp();
                        this.result.value.type.value = e.type.value;
                        return ;
                    }
                    this.result.value = ctfeIndex(e.loc.value, e.type.value, agg.value, indexToAccess.value);
                    return ;
                }
                else
                {
                    assert((indexToAccess.value == 0L));
                    this.result.value = interpret(agg.value, this.istate.value, this.goal);
                    if (this.exceptionOrCant(this.result.value))
                    {
                        return ;
                    }
                    this.result.value = paintTypeOntoLiteral(e.type.value, this.result.value);
                    return ;
                }
            }
            if (((e.e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Taarray))
            {
                Expression e1 = interpret(e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e1))
                {
                    return ;
                }
                if (((e1.op.value & 0xFF) == 13))
                {
                    if ((this.goal == CtfeGoal.ctfeNeedLvalue) && ((e1.type.value.ty.value & 0xFF) == ENUMTY.Taarray) && e.modifiable)
                    {
                        throw new AssertionError("Unreachable code!");
                    }
                    e.error(new BytePtr("cannot index null array `%s`"), e.e1.value.toChars());
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                Expression e2 = interpret(e.e2.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e2))
                {
                    return ;
                }
                if ((this.goal == CtfeGoal.ctfeNeedLvalue))
                {
                    if ((pequals(e1, e.e1.value)) && (pequals(e2, e.e2.value)))
                    {
                        this.result.value = e;
                    }
                    else
                    {
                        (this.pue) = new UnionExp(new IndexExp(e.loc.value, e1, e2));
                        this.result.value = (this.pue.get()).exp();
                        this.result.value.type.value = e.type.value;
                    }
                    return ;
                }
                assert(((e1.op.value & 0xFF) == 48));
                Ref<UnionExp> e2tmp = ref(null);
                e2 = resolveSlice(e2, ptr(e2tmp));
                this.result.value = findKeyInAA(e.loc.value, (AssocArrayLiteralExp)e1, e2);
                if (this.result.value == null)
                {
                    e.error(new BytePtr("key `%s` not found in associative array `%s`"), e2.toChars(), e.e1.value.toChars());
                    this.result.value = CTFEExp.cantexp.value;
                }
                return ;
            }
            Ref<Expression> agg = ref(null);
            Ref<Long> indexToAccess = ref(0L);
            if (!resolveIndexing(e, this.istate.value, ptr(agg), ptr(indexToAccess), false))
            {
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            if ((this.goal == CtfeGoal.ctfeNeedLvalue))
            {
                Expression e2 = new IntegerExp(e.e2.value.loc.value, indexToAccess.value, Type.tsize_t.value);
                (this.pue) = new UnionExp(new IndexExp(e.loc.value, agg.value, e2));
                this.result.value = (this.pue.get()).exp();
                this.result.value.type.value = e.type.value;
                return ;
            }
            this.result.value = ctfeIndex(e.loc.value, e.type.value, agg.value, indexToAccess.value);
            if (this.exceptionOrCant(this.result.value))
            {
                return ;
            }
            if (((this.result.value.op.value & 0xFF) == 128))
            {
                e.error(new BytePtr("`%s` is used before initialized"), e.toChars());
                errorSupplemental(this.result.value.loc.value, new BytePtr("originally uninitialized here"));
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            this.result.value = paintTypeOntoLiteral(e.type.value, this.result.value);
        }

        public  void visit(SliceExp e) {
            if (((e.e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tpointer))
            {
                Expression e1 = interpret(e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e1))
                {
                    return ;
                }
                if (((e1.op.value & 0xFF) == 135))
                {
                    e.error(new BytePtr("cannot slice invalid pointer `%s` of value `%s`"), e.e1.value.toChars(), e1.toChars());
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                Expression lwr = interpret(e.lwr.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(lwr))
                {
                    return ;
                }
                Expression upr = interpret(e.upr.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(upr))
                {
                    return ;
                }
                long ilwr = lwr.toInteger();
                long iupr = upr.toInteger();
                Ref<Long> ofs = ref(0L);
                Expression agg = getAggregateFromPointer(e1, ptr(ofs));
                ilwr += ofs.value;
                iupr += ofs.value;
                if (((agg.op.value & 0xFF) == 13))
                {
                    if ((iupr == ilwr))
                    {
                        this.result.value = new NullExp(e.loc.value, null);
                        this.result.value.type.value = e.type.value;
                        return ;
                    }
                    e.error(new BytePtr("cannot slice null pointer `%s`"), e.e1.value.toChars());
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                if (((agg.op.value & 0xFF) == 25))
                {
                    e.error(new BytePtr("slicing pointers to static variables is not supported in CTFE"));
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                if (((agg.op.value & 0xFF) != 47) && ((agg.op.value & 0xFF) != 121))
                {
                    e.error(new BytePtr("pointer `%s` cannot be sliced at compile time (it does not point to an array)"), e.e1.value.toChars());
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                assert(((agg.op.value & 0xFF) == 47) || ((agg.op.value & 0xFF) == 121));
                long len = ArrayLength(Type.tsize_t.value, agg).exp().toInteger();
                if ((iupr > len + 1L) || (iupr < ilwr))
                {
                    e.error(new BytePtr("pointer slice `[%lld..%lld]` exceeds allocated memory block `[0..%lld]`"), ilwr, iupr, len);
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                if ((ofs.value != 0L))
                {
                    lwr = new IntegerExp(e.loc.value, ilwr, lwr.type.value);
                    upr = new IntegerExp(e.loc.value, iupr, upr.type.value);
                }
                (this.pue) = new UnionExp(new SliceExp(e.loc.value, agg, lwr, upr));
                this.result.value = (this.pue.get()).exp();
                this.result.value.type.value = e.type.value;
                return ;
            }
            int goal1 = CtfeGoal.ctfeNeedRvalue;
            if ((this.goal == CtfeGoal.ctfeNeedLvalue))
            {
                if (((e.e1.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray))
                {
                    {
                        VarExp ve = e.e1.value.isVarExp();
                        if ((ve) != null)
                        {
                            {
                                VarDeclaration vd = ve.var.value.isVarDeclaration();
                                if ((vd) != null)
                                {
                                    if ((vd.storage_class.value & 2097152L) != 0)
                                    {
                                        goal1 = CtfeGoal.ctfeNeedLvalue;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Expression e1 = interpret(e.e1.value, this.istate.value, goal1);
            if (this.exceptionOrCant(e1))
            {
                return ;
            }
            if (e.lwr.value == null)
            {
                this.result.value = paintTypeOntoLiteral(e.type.value, e1);
                return ;
            }
            {
                VectorExp ve = e1.isVectorExp();
                if ((ve) != null)
                {
                    e1 = interpretVectorToArray(this.pue, ve);
                    e1 = (pequals(e1, (this.pue.get()).exp())) ? (this.pue.get()).copy() : e1;
                }
            }
            long dollar = 0L;
            if (((e1.op.value & 0xFF) == 26) || ((e1.op.value & 0xFF) == 27) && ((e1.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                dollar = e1.type.value.toBasetype().isTypeSArray().dim.value.toInteger();
            }
            else
            {
                if (((e1.op.value & 0xFF) != 47) && ((e1.op.value & 0xFF) != 121) && ((e1.op.value & 0xFF) != 13) && ((e1.op.value & 0xFF) != 31) && ((e1.op.value & 0xFF) != 229))
                {
                    e.error(new BytePtr("cannot determine length of `%s` at compile time"), e1.toChars());
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                dollar = resolveArrayLength(e1);
            }
            if (e.lengthVar.value != null)
            {
                IntegerExp dollarExp = new IntegerExp(e.loc.value, dollar, Type.tsize_t.value);
                ctfeStack.push(e.lengthVar.value);
                setValue(e.lengthVar.value, dollarExp);
            }
            Expression lwr = interpret(e.lwr.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(lwr))
            {
                if (e.lengthVar.value != null)
                {
                    ctfeStack.pop(e.lengthVar.value);
                }
                return ;
            }
            Expression upr = interpret(e.upr.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(upr))
            {
                if (e.lengthVar.value != null)
                {
                    ctfeStack.pop(e.lengthVar.value);
                }
                return ;
            }
            if (e.lengthVar.value != null)
            {
                ctfeStack.pop(e.lengthVar.value);
            }
            long ilwr = lwr.toInteger();
            long iupr = upr.toInteger();
            if (((e1.op.value & 0xFF) == 13))
            {
                if ((ilwr == 0L) && (iupr == 0L))
                {
                    this.result.value = e1;
                    return ;
                }
                e1.error(new BytePtr("slice `[%llu..%llu]` is out of bounds"), ilwr, iupr);
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            {
                SliceExp se = e1.isSliceExp();
                if ((se) != null)
                {
                    long lo1 = se.lwr.value.toInteger();
                    long up1 = se.upr.value.toInteger();
                    if ((ilwr > iupr) || (iupr > up1 - lo1))
                    {
                        e.error(new BytePtr("slice `[%llu..%llu]` exceeds array bounds `[%llu..%llu]`"), ilwr, iupr, lo1, up1);
                        this.result.value = CTFEExp.cantexp.value;
                        return ;
                    }
                    ilwr += lo1;
                    iupr += lo1;
                    (this.pue) = new UnionExp(new SliceExp(e.loc.value, se.e1.value, new IntegerExp(e.loc.value, ilwr, lwr.type.value), new IntegerExp(e.loc.value, iupr, upr.type.value)));
                    this.result.value = (this.pue.get()).exp();
                    this.result.value.type.value = e.type.value;
                    return ;
                }
            }
            if (((e1.op.value & 0xFF) == 47) || ((e1.op.value & 0xFF) == 121))
            {
                if ((iupr < ilwr) || (dollar < iupr))
                {
                    e.error(new BytePtr("slice `[%lld..%lld]` exceeds array bounds `[0..%lld]`"), ilwr, iupr, dollar);
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
            }
            (this.pue) = new UnionExp(new SliceExp(e.loc.value, e1, lwr, upr));
            this.result.value = (this.pue.get()).exp();
            this.result.value.type.value = e.type.value;
        }

        public  void visit(InExp e) {
            Expression e1 = interpret(e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e1))
            {
                return ;
            }
            Expression e2 = interpret(e.e2.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e2))
            {
                return ;
            }
            if (((e2.op.value & 0xFF) == 13))
            {
                (this.pue) = new UnionExp(new NullExp(e.loc.value, e.type.value));
                this.result.value = (this.pue.get()).exp();
                return ;
            }
            if (((e2.op.value & 0xFF) != 48))
            {
                e.error(new BytePtr("`%s` cannot be interpreted at compile time"), e.toChars());
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            e1 = resolveSlice(e1, null);
            this.result.value = findKeyInAA(e.loc.value, (AssocArrayLiteralExp)e2, e1);
            if (this.exceptionOrCant(this.result.value))
            {
                return ;
            }
            if (this.result.value == null)
            {
                (this.pue) = new UnionExp(new NullExp(e.loc.value, e.type.value));
                this.result.value = (this.pue.get()).exp();
            }
            else
            {
                this.result.value = new IndexExp(e.loc.value, e2, e1);
                this.result.value.type.value = e.type.value.nextOf();
                (this.pue) = new UnionExp(new AddrExp(e.loc.value, this.result.value, e.type.value));
                this.result.value = (this.pue.get()).exp();
            }
        }

        public  void visit(CatExp e) {
            Ref<UnionExp> ue1 = ref(null);
            Expression e1 = interpret(ptr(ue1), e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e1))
            {
                return ;
            }
            Ref<UnionExp> ue2 = ref(null);
            Expression e2 = interpret(ptr(ue2), e.e2.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e2))
            {
                return ;
            }
            Ref<UnionExp> e1tmp = ref(null);
            e1 = resolveSlice(e1, ptr(e1tmp));
            Ref<UnionExp> e2tmp = ref(null);
            e2 = resolveSlice(e2, ptr(e2tmp));
            if (!(((e1.op.value & 0xFF) == 121) && ((e2.op.value & 0xFF) == 121)))
            {
                if ((pequals(e1, ue1.value.exp())))
                {
                    e1 = ue1.value.copy();
                }
                if ((pequals(e2, ue2.value.exp())))
                {
                    e2 = ue2.value.copy();
                }
            }
            this.pue.set(0, ctfeCat(e.loc.value, e.type.value, e1, e2));
            this.result.value = (this.pue.get()).exp();
            if (CTFEExp.isCantExp(this.result.value))
            {
                e.error(new BytePtr("`%s` cannot be interpreted at compile time"), e.toChars());
                return ;
            }
            {
                ArrayLiteralExp ale = this.result.value.isArrayLiteralExp();
                if ((ale) != null)
                {
                    ale.ownedByCtfe = OwnedBy.ctfe;
                    {
                        Slice<Expression> __r982 = (ale.elements.value.get()).opSlice().copy();
                        int __key983 = 0;
                        for (; (__key983 < __r982.getLength());__key983 += 1) {
                            Expression elem = __r982.get(__key983);
                            Expression ex = evaluatePostblit(this.istate.value, elem);
                            if (this.exceptionOrCant(ex))
                            {
                                return ;
                            }
                        }
                    }
                }
                else {
                    StringExp se = this.result.value.isStringExp();
                    if ((se) != null)
                    {
                        se.ownedByCtfe = OwnedBy.ctfe;
                    }
                }
            }
        }

        public  void visit(DeleteExp e) {
            this.result.value = interpret(e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(this.result.value))
            {
                return ;
            }
            if (((this.result.value.op.value & 0xFF) == 13))
            {
                this.result.value = CTFEExp.voidexp;
                return ;
            }
            Type tb = e.e1.value.type.value.toBasetype();
            switch ((tb.ty.value & 0xFF))
            {
                case 7:
                    if (((this.result.value.op.value & 0xFF) != 50))
                    {
                        e.error(new BytePtr("`delete` on invalid class reference `%s`"), this.result.value.toChars());
                        this.result.value = CTFEExp.cantexp.value;
                        return ;
                    }
                    ClassReferenceExp cre = (ClassReferenceExp)this.result.value;
                    ClassDeclaration cd = cre.originalClass();
                    if (cd.aggDelete.value != null)
                    {
                        e.error(new BytePtr("member deallocators not supported by CTFE"));
                        this.result.value = CTFEExp.cantexp.value;
                        return ;
                    }
                    if (cd.dtor.value != null)
                    {
                        this.result.value = interpretFunction(this.pue, cd.dtor.value, this.istate.value, null, cre);
                        if (this.exceptionOrCant(this.result.value))
                        {
                            return ;
                        }
                    }
                    break;
                case 3:
                    tb = ((TypePointer)tb).next.value.toBasetype();
                    if (((tb.ty.value & 0xFF) == ENUMTY.Tstruct))
                    {
                        if (((this.result.value.op.value & 0xFF) != 19) || ((((AddrExp)this.result.value).e1.value.op.value & 0xFF) != 49))
                        {
                            e.error(new BytePtr("`delete` on invalid struct pointer `%s`"), this.result.value.toChars());
                            this.result.value = CTFEExp.cantexp.value;
                            return ;
                        }
                        StructDeclaration sd = ((TypeStruct)tb).sym.value;
                        StructLiteralExp sle = (StructLiteralExp)((AddrExp)this.result.value).e1.value;
                        if (sd.aggDelete.value != null)
                        {
                            e.error(new BytePtr("member deallocators not supported by CTFE"));
                            this.result.value = CTFEExp.cantexp.value;
                            return ;
                        }
                        if (sd.dtor.value != null)
                        {
                            this.result.value = interpretFunction(this.pue, sd.dtor.value, this.istate.value, null, sle);
                            if (this.exceptionOrCant(this.result.value))
                            {
                                return ;
                            }
                        }
                    }
                    break;
                case 0:
                    Type tv = tb.nextOf().baseElemOf();
                    if (((tv.ty.value & 0xFF) == ENUMTY.Tstruct))
                    {
                        if (((this.result.value.op.value & 0xFF) != 47))
                        {
                            e.error(new BytePtr("`delete` on invalid struct array `%s`"), this.result.value.toChars());
                            this.result.value = CTFEExp.cantexp.value;
                            return ;
                        }
                        StructDeclaration sd_1 = ((TypeStruct)tv).sym.value;
                        if (sd_1.aggDelete.value != null)
                        {
                            e.error(new BytePtr("member deallocators not supported by CTFE"));
                            this.result.value = CTFEExp.cantexp.value;
                            return ;
                        }
                        if (sd_1.dtor.value != null)
                        {
                            ArrayLiteralExp ale = (ArrayLiteralExp)this.result.value;
                            {
                                Slice<Expression> __r984 = (ale.elements.value.get()).opSlice().copy();
                                int __key985 = 0;
                                for (; (__key985 < __r984.getLength());__key985 += 1) {
                                    Expression el = __r984.get(__key985);
                                    this.result.value = interpretFunction(this.pue, sd_1.dtor.value, this.istate.value, null, el);
                                    if (this.exceptionOrCant(this.result.value))
                                    {
                                        return ;
                                    }
                                }
                            }
                        }
                    }
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            this.result.value = CTFEExp.voidexp;
        }

        public  void visit(CastExp e) {
            Expression e1 = interpret(e.e1.value, this.istate.value, this.goal);
            if (this.exceptionOrCant(e1))
            {
                return ;
            }
            if (((e.to.value.ty.value & 0xFF) == ENUMTY.Tvoid))
            {
                this.result.value = CTFEExp.voidexp;
                return ;
            }
            if (((e.to.value.ty.value & 0xFF) == ENUMTY.Tpointer) && ((e1.op.value & 0xFF) != 13))
            {
                Type pointee = ((TypePointer)e.type.value).next.value;
                if (((e1.op.value & 0xFF) == 135))
                {
                    this.result.value = paintTypeOntoLiteral(this.pue, e.to.value, e1);
                    return ;
                }
                boolean castToSarrayPointer = false;
                boolean castBackFromVoid = false;
                if (((e1.type.value.ty.value & 0xFF) == ENUMTY.Tarray) || ((e1.type.value.ty.value & 0xFF) == ENUMTY.Tsarray) || ((e1.type.value.ty.value & 0xFF) == ENUMTY.Tpointer))
                {
                    Type elemtype = e1.type.value.nextOf();
                    {
                        SliceExp se = e1.isSliceExp();
                        if ((se) != null)
                        {
                            elemtype = se.e1.value.type.value.nextOf();
                        }
                    }
                    Type ultimatePointee = pointee;
                    Type ultimateSrc = elemtype;
                    for (; ((ultimatePointee.ty.value & 0xFF) == ENUMTY.Tpointer) && ((ultimateSrc.ty.value & 0xFF) == ENUMTY.Tpointer);){
                        ultimatePointee = ultimatePointee.nextOf();
                        ultimateSrc = ultimateSrc.nextOf();
                    }
                    if (((ultimatePointee.ty.value & 0xFF) == ENUMTY.Tsarray) && ultimatePointee.nextOf().equivalent(ultimateSrc))
                    {
                        castToSarrayPointer = true;
                    }
                    else if (((ultimatePointee.ty.value & 0xFF) != ENUMTY.Tvoid) && ((ultimateSrc.ty.value & 0xFF) != ENUMTY.Tvoid) && !isSafePointerCast(elemtype, pointee))
                    {
                        e.error(new BytePtr("reinterpreting cast from `%s*` to `%s*` is not supported in CTFE"), elemtype.toChars(), pointee.toChars());
                        this.result.value = CTFEExp.cantexp.value;
                        return ;
                    }
                    if (((ultimateSrc.ty.value & 0xFF) == ENUMTY.Tvoid))
                    {
                        castBackFromVoid = true;
                    }
                }
                {
                    SliceExp se = e1.isSliceExp();
                    if ((se) != null)
                    {
                        if (((se.e1.value.op.value & 0xFF) == 13))
                        {
                            this.result.value = paintTypeOntoLiteral(this.pue, e.type.value, se.e1.value);
                            return ;
                        }
                        IndexExp ei = new IndexExp(e.loc.value, se.e1.value, se.lwr.value);
                        ei.type.value = e.type.value.nextOf();
                        (this.pue) = new UnionExp(new AddrExp(e.loc.value, ei, e.type.value));
                        this.result.value = (this.pue.get()).exp();
                        return ;
                    }
                }
                if (((e1.op.value & 0xFF) == 47) || ((e1.op.value & 0xFF) == 121))
                {
                    IndexExp ei = new IndexExp(e.loc.value, e1, new IntegerExp(e.loc.value, 0L, Type.tsize_t.value));
                    ei.type.value = e.type.value.nextOf();
                    (this.pue) = new UnionExp(new AddrExp(e.loc.value, ei, e.type.value));
                    this.result.value = (this.pue.get()).exp();
                    return ;
                }
                if (((e1.op.value & 0xFF) == 62) && !((IndexExp)e1).e1.value.type.value.equals(e1.type.value))
                {
                    IndexExp ie = (IndexExp)e1;
                    if (castBackFromVoid)
                    {
                        Type origType = ie.e1.value.type.value.nextOf();
                        if (((ie.e1.value.op.value & 0xFF) == 47) && ((ie.e2.value.op.value & 0xFF) == 135))
                        {
                            ArrayLiteralExp ale = (ArrayLiteralExp)ie.e1.value;
                            int indx = (int)ie.e2.value.toInteger();
                            if ((indx < (ale.elements.value.get()).length.value))
                            {
                                {
                                    Expression xx = (ale.elements.value.get()).get(indx);
                                    if ((xx) != null)
                                    {
                                        {
                                            IndexExp iex = xx.isIndexExp();
                                            if ((iex) != null)
                                            {
                                                origType = iex.e1.value.type.value.nextOf();
                                            }
                                            else {
                                                AddrExp ae = xx.isAddrExp();
                                                if ((ae) != null)
                                                {
                                                    origType = ae.e1.value.type.value;
                                                }
                                                else {
                                                    VarExp ve = xx.isVarExp();
                                                    if ((ve) != null)
                                                    {
                                                        origType = ve.var.value.type.value;
                                                    }
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
                            this.result.value = CTFEExp.cantexp.value;
                            return ;
                        }
                    }
                    (this.pue) = new UnionExp(new IndexExp(e1.loc.value, ie.e1.value, ie.e2.value));
                    this.result.value = (this.pue.get()).exp();
                    this.result.value.type.value = e.type.value;
                    return ;
                }
                {
                    AddrExp ae = e1.isAddrExp();
                    if ((ae) != null)
                    {
                        Type origType = ae.e1.value.type.value;
                        if (isSafePointerCast(origType, pointee))
                        {
                            (this.pue) = new UnionExp(new AddrExp(e.loc.value, ae.e1.value, e.type.value));
                            this.result.value = (this.pue.get()).exp();
                            return ;
                        }
                        if (castToSarrayPointer && ((pointee.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray) && ((ae.e1.value.op.value & 0xFF) == 62))
                        {
                            long dim = ((TypeSArray)pointee.toBasetype()).dim.value.toInteger();
                            IndexExp ie = (IndexExp)ae.e1.value;
                            Expression lwr = ie.e2.value;
                            Expression upr = new IntegerExp(ie.e2.value.loc.value, ie.e2.value.toInteger() + dim, Type.tsize_t.value);
                            SliceExp er = new SliceExp(e.loc.value, ie.e1.value, lwr, upr);
                            er.type.value = pointee;
                            (this.pue) = new UnionExp(new AddrExp(e.loc.value, er, e.type.value));
                            this.result.value = (this.pue.get()).exp();
                            return ;
                        }
                    }
                }
                if (((e1.op.value & 0xFF) == 26) || ((e1.op.value & 0xFF) == 25))
                {
                    Type origType = ((SymbolExp)e1).var.value.type.value;
                    if (castBackFromVoid && !isSafePointerCast(origType, pointee))
                    {
                        e.error(new BytePtr("using `void*` to reinterpret cast from `%s*` to `%s*` is not supported in CTFE"), origType.toChars(), pointee.toChars());
                        this.result.value = CTFEExp.cantexp.value;
                        return ;
                    }
                    {
                        VarExp ve = e1.isVarExp();
                        if ((ve) != null)
                        {
                            (this.pue) = new UnionExp(new VarExp(e.loc.value, ve.var.value));
                        }
                        else
                        {
                            (this.pue) = new UnionExp(new SymOffExp(e.loc.value, ((SymOffExp)e1).var.value, ((SymOffExp)e1).offset.value));
                        }
                    }
                    this.result.value = (this.pue.get()).exp();
                    this.result.value.type.value = e.to.value;
                    return ;
                }
                e1 = interpret(e1, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (((e1.op.value & 0xFF) != 13))
                {
                    e.error(new BytePtr("pointer cast from `%s` to `%s` is not supported at compile time"), e1.type.value.toChars(), e.to.value.toChars());
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
            }
            if (((e.to.value.ty.value & 0xFF) == ENUMTY.Tsarray) && ((e.e1.value.type.value.ty.value & 0xFF) == ENUMTY.Tvector))
            {
                e1 = interpret(e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                if (this.exceptionOrCant(e1))
                {
                    return ;
                }
                assert(((e1.op.value & 0xFF) == 229));
                e1 = interpretVectorToArray(this.pue, e1.isVectorExp());
            }
            if (((e.to.value.ty.value & 0xFF) == ENUMTY.Tarray) && ((e1.op.value & 0xFF) == 31))
            {
                SliceExp se = (SliceExp)e1;
                if (!isSafePointerCast(se.e1.value.type.value.nextOf(), e.to.value.nextOf()))
                {
                    e.error(new BytePtr("array cast from `%s` to `%s` is not supported at compile time"), se.e1.value.type.value.toChars(), e.to.value.toChars());
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
                (this.pue) = new UnionExp(new SliceExp(e1.loc.value, se.e1.value, se.lwr.value, se.upr.value));
                this.result.value = (this.pue.get()).exp();
                this.result.value.type.value = e.to.value;
                return ;
            }
            if (((e.to.value.ty.value & 0xFF) == ENUMTY.Tsarray) || ((e.to.value.ty.value & 0xFF) == ENUMTY.Tarray) && ((e1.type.value.ty.value & 0xFF) == ENUMTY.Tsarray) || ((e1.type.value.ty.value & 0xFF) == ENUMTY.Tarray) && !isSafePointerCast(e1.type.value.nextOf(), e.to.value.nextOf()))
            {
                e.error(new BytePtr("array cast from `%s` to `%s` is not supported at compile time"), e1.type.value.toChars(), e.to.value.toChars());
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            if (((e.to.value.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                e1 = resolveSlice(e1, null);
            }
            if (((e.to.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tbool) && ((e1.type.value.ty.value & 0xFF) == ENUMTY.Tpointer))
            {
                (this.pue) = new UnionExp(new IntegerExp(e.loc.value, (e1.op.value & 0xFF) != 13, e.to.value));
                this.result.value = (this.pue.get()).exp();
                return ;
            }
            this.result.value = ctfeCast(this.pue, e.loc.value, e.type.value, e.to.value, e1);
        }

        public  void visit(AssertExp e) {
            Expression e1 = interpret(this.pue, e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(e1))
            {
                return ;
            }
            if (isTrueBool(e1))
            {
            }
            else if (e1.isBool(false))
            {
                if (e.msg != null)
                {
                    Ref<UnionExp> ue = ref(null);
                    this.result.value = interpret(ptr(ue), e.msg, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                    if (this.exceptionOrCant(this.result.value))
                    {
                        return ;
                    }
                    e.error(new BytePtr("`%s`"), this.result.value.toChars());
                }
                else
                {
                    e.error(new BytePtr("`%s` failed"), e.toChars());
                }
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            else
            {
                e.error(new BytePtr("`%s` is not a compile time boolean expression"), e1.toChars());
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            this.result.value = e1;
            return ;
        }

        public  void visit(PtrExp e) {
            {
                SymOffExp soe1 = e.e1.value.isSymOffExp();
                if ((soe1) != null)
                {
                    if ((soe1.offset.value == 0L) && (soe1.var.value.isVarDeclaration() != null) && isFloatIntPaint(e.type.value, soe1.var.value.type.value))
                    {
                        this.result.value = paintFloatInt(this.pue, getVarExp(e.loc.value, this.istate.value, soe1.var.value, CtfeGoal.ctfeNeedRvalue), e.type.value);
                        return ;
                    }
                }
            }
            {
                CastExp ce1 = e.e1.value.isCastExp();
                if ((ce1) != null)
                {
                    {
                        AddrExp ae11 = ce1.e1.value.isAddrExp();
                        if ((ae11) != null)
                        {
                            Expression x = ae11.e1.value;
                            if (isFloatIntPaint(e.type.value, x.type.value))
                            {
                                this.result.value = paintFloatInt(this.pue, interpret(x, this.istate.value, CtfeGoal.ctfeNeedRvalue), e.type.value);
                                return ;
                            }
                        }
                    }
                }
            }
            {
                AddExp ae = e.e1.value.isAddExp();
                if ((ae) != null)
                {
                    if (((ae.e1.value.op.value & 0xFF) == 19) && ((ae.e2.value.op.value & 0xFF) == 135))
                    {
                        AddrExp ade = (AddrExp)ae.e1.value;
                        Expression ex = interpret(ade.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
                        if (this.exceptionOrCant(ex))
                        {
                            return ;
                        }
                        {
                            StructLiteralExp se = ex.isStructLiteralExp();
                            if ((se) != null)
                            {
                                long offset = ae.e2.value.toInteger();
                                this.result.value = se.getField(e.type.value, (int)offset);
                                if (this.result.value != null)
                                {
                                    return ;
                                }
                            }
                        }
                    }
                }
            }
            this.result.value = interpret(e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(this.result.value))
            {
                return ;
            }
            if (((this.result.value.op.value & 0xFF) == 161))
            {
                return ;
            }
            {
                SymOffExp soe = this.result.value.isSymOffExp();
                if ((soe) != null)
                {
                    if ((soe.offset.value == 0L) && (soe.var.value.isFuncDeclaration() != null))
                    {
                        return ;
                    }
                    e.error(new BytePtr("cannot dereference pointer to static variable `%s` at compile time"), soe.var.value.toChars());
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
            }
            if (((this.result.value.op.value & 0xFF) != 19))
            {
                if (((this.result.value.op.value & 0xFF) == 13))
                {
                    e.error(new BytePtr("dereference of null pointer `%s`"), e.e1.value.toChars());
                }
                else
                {
                    e.error(new BytePtr("dereference of invalid pointer `%s`"), this.result.value.toChars());
                }
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            this.result.value = ((AddrExp)this.result.value).e1.value;
            if (((this.result.value.op.value & 0xFF) == 31) && ((e.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                return ;
            }
            this.result.value = interpret(this.pue, this.result.value, this.istate.value, this.goal);
            if (this.exceptionOrCant(this.result.value))
            {
                return ;
            }
        }

        public  void visit(DotVarExp e) {
            Expression ex = interpret(e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(ex))
            {
                return ;
            }
            {
                FuncDeclaration f = e.var.value.isFuncDeclaration();
                if ((f) != null)
                {
                    if ((pequals(ex, e.e1.value)))
                    {
                        this.result.value = e;
                    }
                    else
                    {
                        (this.pue) = new UnionExp(new DotVarExp(e.loc.value, ex, f, false));
                        this.result.value = (this.pue.get()).exp();
                        this.result.value.type.value = e.type.value;
                    }
                    return ;
                }
            }
            VarDeclaration v = e.var.value.isVarDeclaration();
            if (v == null)
            {
                e.error(new BytePtr("CTFE internal error: `%s`"), e.toChars());
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            if (((ex.op.value & 0xFF) == 13))
            {
                if (((ex.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tclass))
                {
                    e.error(new BytePtr("class `%s` is `null` and cannot be dereferenced"), e.e1.value.toChars());
                }
                else
                {
                    e.error(new BytePtr("CTFE internal error: null this `%s`"), e.e1.value.toChars());
                }
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            if (((ex.op.value & 0xFF) != 49) && ((ex.op.value & 0xFF) != 50))
            {
                e.error(new BytePtr("`%s.%s` is not yet implemented at compile time"), e.e1.value.toChars(), e.var.value.toChars());
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            StructLiteralExp se = null;
            int i = 0;
            if (((ex.op.value & 0xFF) == 50))
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
                e.error(new BytePtr("couldn't find field `%s` of type `%s` in `%s`"), v.toChars(), e.type.value.toChars(), se.toChars());
                this.result.value = CTFEExp.cantexp.value;
                return ;
            }
            if ((this.goal == CtfeGoal.ctfeNeedLvalue))
            {
                Expression ev = (se.elements.value.get()).get(i);
                if ((ev == null) || ((ev.op.value & 0xFF) == 128))
                {
                    se.elements.value.get().set(i, voidInitLiteral(e.type.value, v).copy());
                }
                if ((pequals(e.e1.value, ex)))
                {
                    this.result.value = e;
                }
                else
                {
                    (this.pue) = new UnionExp(new DotVarExp(e.loc.value, ex, v));
                    this.result.value = (this.pue.get()).exp();
                    this.result.value.type.value = e.type.value;
                }
                return ;
            }
            this.result.value = (se.elements.value.get()).get(i);
            if (this.result.value == null)
            {
                if ((v.type.value.size() == 0L))
                {
                    this.result.value = voidInitLiteral(e.type.value, v).copy();
                }
                else
                {
                    e.error(new BytePtr("Internal Compiler Error: null field `%s`"), v.toChars());
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
            }
            {
                VoidInitExp vie = this.result.value.isVoidInitExp();
                if ((vie) != null)
                {
                    BytePtr s = pcopy(vie.var.toChars());
                    if (v.overlapped.value)
                    {
                        e.error(new BytePtr("reinterpretation through overlapped field `%s` is not allowed in CTFE"), s);
                        this.result.value = CTFEExp.cantexp.value;
                        return ;
                    }
                    e.error(new BytePtr("cannot read uninitialized variable `%s` in CTFE"), s);
                    this.result.value = CTFEExp.cantexp.value;
                    return ;
                }
            }
            if (((v.type.value.ty.value & 0xFF) != (this.result.value.type.value.ty.value & 0xFF)) && ((v.type.value.ty.value & 0xFF) == ENUMTY.Tsarray))
            {
                TypeSArray tsa = (TypeSArray)v.type.value;
                int len = (int)tsa.dim.value.toInteger();
                Ref<UnionExp> ue = ref(null);
                this.result.value = createBlockDuplicatedArrayLiteral(ptr(ue), ex.loc.value, v.type.value, ex, len);
                if ((pequals(this.result.value, ue.value.exp())))
                {
                    this.result.value = ue.value.copy();
                }
                se.elements.value.get().set(i, this.result.value);
            }
        }

        public  void visit(RemoveExp e) {
            Expression agg = interpret(e.e1.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(agg))
            {
                return ;
            }
            Expression index = interpret(e.e2.value, this.istate.value, CtfeGoal.ctfeNeedRvalue);
            if (this.exceptionOrCant(index))
            {
                return ;
            }
            if (((agg.op.value & 0xFF) == 13))
            {
                this.result.value = CTFEExp.voidexp;
                return ;
            }
            AssocArrayLiteralExp aae = agg.isAssocArrayLiteralExp();
            Ptr<DArray<Expression>> keysx = aae.keys.value;
            Ptr<DArray<Expression>> valuesx = aae.values.value;
            int removed = 0;
            {
                Slice<Expression> __r987 = (valuesx.get()).opSlice().copy();
                int __key986 = 0;
                for (; (__key986 < __r987.getLength());__key986 += 1) {
                    Expression evalue = __r987.get(__key986);
                    int j = __key986;
                    Expression ekey = (keysx.get()).get(j);
                    int eq = ctfeEqual(e.loc.value, TOK.equal, ekey, index);
                    if (eq != 0)
                    {
                        removed += 1;
                    }
                    else if ((removed != 0))
                    {
                        keysx.get().set(j - removed, ekey);
                        valuesx.get().set(j - removed, evalue);
                    }
                }
            }
            (valuesx.get()).length.value = (valuesx.get()).length.value - removed;
            (keysx.get()).length.value = (keysx.get()).length.value - removed;
            (this.pue) = new UnionExp(new IntegerExp(e.loc.value, removed != 0 ? 1 : 0, Type.tbool.value));
            this.result.value = (this.pue.get()).exp();
        }

        public  void visit(ClassReferenceExp e) {
            this.result.value = e;
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
    public static Expression interpret(Ptr<UnionExp> pue, Expression e, Ptr<InterState> istate, int goal) {
        if (e == null)
        {
            return null;
        }
        Interpreter v = new Interpreter(pue, istate, goal);
        e.accept(v);
        Expression ex = v.result.value;
        assert((goal == CtfeGoal.ctfeNeedNothing) || (ex != null));
        return ex;
    }

    // defaulted all parameters starting with #4
    public static Expression interpret(Ptr<UnionExp> pue, Expression e, Ptr<InterState> istate) {
        return interpret(pue, e, istate, CtfeGoal.ctfeNeedRvalue);
    }

    public static Expression interpret(Expression e, Ptr<InterState> istate, int goal) {
        Ref<UnionExp> ue = ref(null);
        Expression result = interpret(ptr(ue), e, istate, goal);
        if ((pequals(result, ue.value.exp())))
        {
            result = ue.value.copy();
        }
        return result;
    }

    // defaulted all parameters starting with #3
    public static Expression interpret(Expression e, Ptr<InterState> istate) {
        return interpret(e, istate, CtfeGoal.ctfeNeedRvalue);
    }

    public static Expression interpret(Ptr<UnionExp> pue, Statement s, Ptr<InterState> istate) {
        if (s == null)
        {
            return null;
        }
        Interpreter v = new Interpreter(pue, istate, CtfeGoal.ctfeNeedNothing);
        s.accept(v);
        return v.result.value;
    }

    public static Expression interpret(Statement s, Ptr<InterState> istate) {
        Ref<UnionExp> ue = ref(null);
        Expression result = interpret(ptr(ue), s, istate);
        if ((pequals(result, ue.value.exp())))
        {
            result = ue.value.copy();
        }
        return result;
    }

    public static Expression scrubReturnValue(Loc loc, Expression e) {
        Function2<Expression,Boolean,Boolean> isVoid = new Function2<Expression,Boolean,Boolean>(){
            public Boolean invoke(Expression e, Boolean checkArrayType) {
                Ref<Boolean> checkArrayType_ref = ref(checkArrayType);
                if (((e.op.value & 0xFF) == 128))
                {
                    return true;
                }
                Function1<Ptr<DArray<Expression>>,Boolean> isEntirelyVoid = new Function1<Ptr<DArray<Expression>>,Boolean>(){
                    public Boolean invoke(Ptr<DArray<Expression>> elems) {
                        {
                            Ref<Slice<Expression>> __r988 = ref((elems.get()).opSlice().copy());
                            IntRef __key989 = ref(0);
                            for (; (__key989.value < __r988.value.getLength());__key989.value += 1) {
                                Expression e = __r988.value.get(__key989.value);
                                if ((e != null) && !isVoid.invoke(e, false))
                                {
                                    return false;
                                }
                            }
                        }
                        return true;
                    }
                };
                {
                    StructLiteralExp sle = e.isStructLiteralExp();
                    if ((sle) != null)
                    {
                        return isEntirelyVoid.invoke(sle.elements.value);
                    }
                }
                if (checkArrayType_ref.value && ((e.type.value.ty.value & 0xFF) != ENUMTY.Tsarray))
                {
                    return false;
                }
                {
                    ArrayLiteralExp ale = e.isArrayLiteralExp();
                    if ((ale) != null)
                    {
                        return isEntirelyVoid.invoke(ale.elements.value);
                    }
                }
                return false;
            }
        };
        Function2<Ptr<DArray<Expression>>,Boolean,Expression> scrubArray = new Function2<Ptr<DArray<Expression>>,Boolean,Expression>(){
            public Expression invoke(Ptr<DArray<Expression>> elems, Boolean structlit) {
                Ref<Boolean> structlit_ref = ref(structlit);
                {
                    Ref<Slice<Expression>> __r990 = ref((elems.get()).opSlice().copy());
                    IntRef __key991 = ref(0);
                    for (; (__key991.value < __r990.value.getLength());__key991.value += 1) {
                        Ref<Expression> e = ref(__r990.value.get(__key991.value));
                        if (e.value == null)
                        {
                            continue;
                        }
                        if (structlit_ref.value && isVoid.invoke(e.value, true))
                        {
                            e.value = null;
                        }
                        else
                        {
                            e.value = scrubReturnValue(loc, e.value);
                            if (CTFEExp.isCantExp(e.value) || ((e.value.op.value & 0xFF) == 127))
                            {
                                return e.value;
                            }
                        }
                    }
                }
                return null;
            }
        };
        Function1<StructLiteralExp,Expression> scrubSE = new Function1<StructLiteralExp,Expression>(){
            public Expression invoke(StructLiteralExp sle) {
                sle.ownedByCtfe.value = OwnedBy.code;
                if ((sle.stageflags.value & 1) == 0)
                {
                    IntRef old = ref(sle.stageflags.value);
                    sle.stageflags.value |= 1;
                    {
                        Ref<Expression> ex = ref(scrubArray.invoke(sle.elements.value, true));
                        if ((ex.value) != null)
                        {
                            return ex.value;
                        }
                    }
                    sle.stageflags.value = old.value;
                }
                return null;
            }
        };
        if (((e.op.value & 0xFF) == 50))
        {
            StructLiteralExp sle = ((ClassReferenceExp)e).value;
            {
                Expression ex = scrubSE.invoke(sle);
                if ((ex) != null)
                {
                    return ex;
                }
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
                    {
                        return ex;
                    }
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
                            Expression ex = scrubArray.invoke(ale.elements.value, false);
                            if ((ex) != null)
                            {
                                return ex;
                            }
                        }
                    }
                    else {
                        AssocArrayLiteralExp aae = e.isAssocArrayLiteralExp();
                        if ((aae) != null)
                        {
                            aae.ownedByCtfe = OwnedBy.code;
                            {
                                Expression ex = scrubArray.invoke(aae.keys.value, false);
                                if ((ex) != null)
                                {
                                    return ex;
                                }
                            }
                            {
                                Expression ex = scrubArray.invoke(aae.values.value, false);
                                if ((ex) != null)
                                {
                                    return ex;
                                }
                            }
                            aae.type.value = toBuiltinAAType(aae.type.value);
                        }
                        else {
                            VectorExp ve = e.isVectorExp();
                            if ((ve) != null)
                            {
                                ve.ownedByCtfe = OwnedBy.code;
                                {
                                    ArrayLiteralExp ale = ve.e1.value.isArrayLiteralExp();
                                    if ((ale) != null)
                                    {
                                        ale.ownedByCtfe = OwnedBy.code;
                                        {
                                            Expression ex = scrubArray.invoke(ale.elements.value, false);
                                            if ((ex) != null)
                                            {
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
        }
        return e;
    }

    public static Expression scrubCacheValue(Expression e) {
        if (e == null)
        {
            return e;
        }
        Function1<Ptr<DArray<Expression>>,Expression> scrubArrayCache = new Function1<Ptr<DArray<Expression>>,Expression>(){
            public Expression invoke(Ptr<DArray<Expression>> elems) {
                {
                    Ref<Slice<Expression>> __r992 = ref((elems.get()).opSlice().copy());
                    IntRef __key993 = ref(0);
                    for (; (__key993.value < __r992.value.getLength());__key993.value += 1) {
                        Ref<Expression> e = ref(__r992.value.get(__key993.value));
                        e.value = scrubCacheValue(e.value);
                    }
                }
                return null;
            }
        };
        Function1<StructLiteralExp,Expression> scrubSE = new Function1<StructLiteralExp,Expression>(){
            public Expression invoke(StructLiteralExp sle) {
                sle.ownedByCtfe.value = OwnedBy.cache;
                if ((sle.stageflags.value & 1) == 0)
                {
                    IntRef old = ref(sle.stageflags.value);
                    sle.stageflags.value |= 1;
                    {
                        Ref<Expression> ex = ref(scrubArrayCache.invoke(sle.elements.value));
                        if ((ex.value) != null)
                        {
                            return ex.value;
                        }
                    }
                    sle.stageflags.value = old.value;
                }
                return null;
            }
        };
        if (((e.op.value & 0xFF) == 50))
        {
            {
                Expression ex = scrubSE.invoke(((ClassReferenceExp)e).value);
                if ((ex) != null)
                {
                    return ex;
                }
            }
        }
        else {
            StructLiteralExp sle = e.isStructLiteralExp();
            if ((sle) != null)
            {
                {
                    Expression ex = scrubSE.invoke(sle);
                    if ((ex) != null)
                    {
                        return ex;
                    }
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
                            Expression ex = scrubArrayCache.invoke(ale.elements.value);
                            if ((ex) != null)
                            {
                                return ex;
                            }
                        }
                    }
                    else {
                        AssocArrayLiteralExp aae = e.isAssocArrayLiteralExp();
                        if ((aae) != null)
                        {
                            aae.ownedByCtfe = OwnedBy.cache;
                            {
                                Expression ex = scrubArrayCache.invoke(aae.keys.value);
                                if ((ex) != null)
                                {
                                    return ex;
                                }
                            }
                            {
                                Expression ex = scrubArrayCache.invoke(aae.values.value);
                                if ((ex) != null)
                                {
                                    return ex;
                                }
                            }
                        }
                        else {
                            VectorExp ve = e.isVectorExp();
                            if ((ve) != null)
                            {
                                ve.ownedByCtfe = OwnedBy.cache;
                                {
                                    ArrayLiteralExp ale = ve.e1.value.isArrayLiteralExp();
                                    if ((ale) != null)
                                    {
                                        ale.ownedByCtfe = OwnedBy.cache;
                                        {
                                            Expression ex = scrubArrayCache.invoke(ale.elements.value);
                                            if ((ex) != null)
                                            {
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
        }
        return e;
    }

    public static Expression interpret_length(Ptr<UnionExp> pue, Ptr<InterState> istate, Expression earg) {
        earg = interpret(pue, earg, istate, CtfeGoal.ctfeNeedRvalue);
        if (exceptionOrCantInterpret(earg))
        {
            return earg;
        }
        long len = 0L;
        {
            AssocArrayLiteralExp aae = earg.isAssocArrayLiteralExp();
            if ((aae) != null)
            {
                len = (long)(aae.keys.value.get()).length.value;
            }
            else
            {
                assert(((earg.op.value & 0xFF) == 13));
            }
        }
        (pue) = new UnionExp(new IntegerExp(earg.loc.value, len, Type.tsize_t.value));
        return (pue.get()).exp();
    }

    public static Expression interpret_keys(Ptr<UnionExp> pue, Ptr<InterState> istate, Expression earg, Type returnType) {
        earg = interpret(pue, earg, istate, CtfeGoal.ctfeNeedRvalue);
        if (exceptionOrCantInterpret(earg))
        {
            return earg;
        }
        if (((earg.op.value & 0xFF) == 13))
        {
            (pue) = new UnionExp(new NullExp(earg.loc.value, earg.type.value));
            return (pue.get()).exp();
        }
        if (((earg.op.value & 0xFF) != 48) && ((earg.type.value.toBasetype().ty.value & 0xFF) != ENUMTY.Taarray))
        {
            return null;
        }
        AssocArrayLiteralExp aae = earg.isAssocArrayLiteralExp();
        ArrayLiteralExp ae = new ArrayLiteralExp(aae.loc.value, returnType, aae.keys.value);
        ae.ownedByCtfe = aae.ownedByCtfe;
        pue.set(0, copyLiteral(ae));
        return (pue.get()).exp();
    }

    public static Expression interpret_values(Ptr<UnionExp> pue, Ptr<InterState> istate, Expression earg, Type returnType) {
        earg = interpret(pue, earg, istate, CtfeGoal.ctfeNeedRvalue);
        if (exceptionOrCantInterpret(earg))
        {
            return earg;
        }
        if (((earg.op.value & 0xFF) == 13))
        {
            (pue) = new UnionExp(new NullExp(earg.loc.value, earg.type.value));
            return (pue.get()).exp();
        }
        if (((earg.op.value & 0xFF) != 48) && ((earg.type.value.toBasetype().ty.value & 0xFF) != ENUMTY.Taarray))
        {
            return null;
        }
        AssocArrayLiteralExp aae = earg.isAssocArrayLiteralExp();
        ArrayLiteralExp ae = new ArrayLiteralExp(aae.loc.value, returnType, aae.values.value);
        ae.ownedByCtfe = aae.ownedByCtfe;
        pue.set(0, copyLiteral(ae));
        return (pue.get()).exp();
    }

    public static Expression interpret_dup(Ptr<UnionExp> pue, Ptr<InterState> istate, Expression earg) {
        earg = interpret(pue, earg, istate, CtfeGoal.ctfeNeedRvalue);
        if (exceptionOrCantInterpret(earg))
        {
            return earg;
        }
        if (((earg.op.value & 0xFF) == 13))
        {
            (pue) = new UnionExp(new NullExp(earg.loc.value, earg.type.value));
            return (pue.get()).exp();
        }
        if (((earg.op.value & 0xFF) != 48) && ((earg.type.value.toBasetype().ty.value & 0xFF) != ENUMTY.Taarray))
        {
            return null;
        }
        AssocArrayLiteralExp aae = copyLiteral(earg).copy().isAssocArrayLiteralExp();
        {
            int i = 0;
            for (; (i < (aae.keys.value.get()).length.value);i++){
                {
                    Expression e = evaluatePostblit(istate, (aae.keys.value.get()).get(i));
                    if ((e) != null)
                    {
                        return e;
                    }
                }
                {
                    Expression e = evaluatePostblit(istate, (aae.values.value.get()).get(i));
                    if ((e) != null)
                    {
                        return e;
                    }
                }
            }
        }
        aae.type.value = earg.type.value.mutableOf();
        return aae;
    }

    public static Expression interpret_aaApply(Ptr<UnionExp> pue, Ptr<InterState> istate, Expression aa, Expression deleg) {
        aa = interpret(aa, istate, CtfeGoal.ctfeNeedRvalue);
        if (exceptionOrCantInterpret(aa))
        {
            return aa;
        }
        if (((aa.op.value & 0xFF) != 48))
        {
            (pue) = new UnionExp(new IntegerExp(deleg.loc.value, 0, Type.tsize_t.value));
            return (pue.get()).exp();
        }
        FuncDeclaration fd = null;
        Expression pthis = null;
        {
            DelegateExp de = deleg.isDelegateExp();
            if ((de) != null)
            {
                fd = de.func.value;
                pthis = de.e1.value;
            }
            else {
                FuncExp fe = deleg.isFuncExp();
                if ((fe) != null)
                {
                    fd = fe.fd.value;
                }
            }
        }
        assert((fd != null) && (fd.fbody.value != null));
        assert(fd.parameters.value != null);
        int numParams = (fd.parameters.value.get()).length.value;
        assert((numParams == 1) || (numParams == 2));
        Parameter fparam = __dop994.get(numParams - 1);
        boolean wantRefValue = 0L != (fparam.storageClass.value & 2101248L);
        Ref<DArray<Expression>> args = ref(args.value = new DArray<Expression>(numParams));
        try {
            AssocArrayLiteralExp ae = (AssocArrayLiteralExp)aa;
            if ((ae.keys.value == null) || ((ae.keys.value.get()).length.value == 0))
            {
                return new IntegerExp(deleg.loc.value, 0L, Type.tsize_t.value);
            }
            Expression eresult = null;
            {
                int i = 0;
                for (; (i < (ae.keys.value.get()).length.value);i += 1){
                    Expression ekey = (ae.keys.value.get()).get(i);
                    Expression evalue = (ae.values.value.get()).get(i);
                    if (wantRefValue)
                    {
                        Type t = evalue.type.value;
                        evalue = new IndexExp(deleg.loc.value, ae, ekey);
                        evalue.type.value = t;
                    }
                    args.value.set(numParams - 1, evalue);
                    if ((numParams == 2))
                    {
                        args.value.set(0, ekey);
                    }
                    Ref<UnionExp> ue = ref(null);
                    eresult = interpretFunction(ptr(ue), fd, istate, ptr(args), pthis);
                    if ((pequals(eresult, ue.value.exp())))
                    {
                        eresult = ue.value.copy();
                    }
                    if (exceptionOrCantInterpret(eresult))
                    {
                        return eresult;
                    }
                    if ((eresult.isIntegerExp().getInteger() != 0L))
                    {
                        return eresult;
                    }
                }
            }
            return eresult;
        }
        finally {
        }
    }

    public static Expression foreachApplyUtf(Ptr<UnionExp> pue, Ptr<InterState> istate, Expression str, Expression deleg, boolean rvs) {
        FuncDeclaration fd = null;
        Expression pthis = null;
        {
            DelegateExp de = deleg.isDelegateExp();
            if ((de) != null)
            {
                fd = de.func.value;
                pthis = de.e1.value;
            }
            else {
                FuncExp fe = deleg.isFuncExp();
                if ((fe) != null)
                {
                    fd = fe.fd.value;
                }
            }
        }
        assert((fd != null) && (fd.fbody.value != null));
        assert(fd.parameters.value != null);
        int numParams = (fd.parameters.value.get()).length.value;
        assert((numParams == 1) || (numParams == 2));
        Type charType = (fd.parameters.value.get()).get(numParams - 1).type.value;
        Type indexType = (numParams == 2) ? (fd.parameters.value.get()).get(0).type.value : Type.tsize_t.value;
        int len = (int)resolveArrayLength(str);
        if ((len == 0))
        {
            (pue) = new UnionExp(new IntegerExp(deleg.loc.value, 0, indexType));
            return (pue.get()).exp();
        }
        str = resolveSlice(str, null);
        StringExp se = str.isStringExp();
        ArrayLiteralExp ale = str.isArrayLiteralExp();
        if ((se == null) && (ale == null))
        {
            str.error(new BytePtr("CTFE internal error: cannot foreach `%s`"), str.toChars());
            return CTFEExp.cantexp.value;
        }
        Ref<DArray<Expression>> args = ref(args.value = new DArray<Expression>(numParams));
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
                    int rawvalue = 0x0ffff;
                    int currentIndex = indx.value;
                    if (ale != null)
                    {
                        int buflen = 1;
                        IntRef n = ref(1);
                        int sz = (int)ale.type.value.nextOf().size();
                        switch (sz)
                        {
                            case 1:
                                if (rvs)
                                {
                                    indx.value -= 1;
                                    buflen = 1;
                                    for (; (indx.value > 0) && (buflen < 4);){
                                        Expression r = (ale.elements.value.get()).get(indx.value);
                                        byte x = (byte)r.isIntegerExp().getInteger();
                                        if ((((x & 0xFF) & 192) != 128))
                                        {
                                            break;
                                        }
                                        buflen += 1;
                                    }
                                }
                                else
                                {
                                    buflen = (indx.value + 4 > len) ? len - indx.value : 4;
                                }
                                {
                                    int i = 0;
                                    for (; (i < buflen);i += 1){
                                        Expression r_1 = (ale.elements.value.get()).get(indx.value + i);
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
                                    Expression r_3 = (ale.elements.value.get()).get(indx.value);
                                    int x_1 = (int)r_3.isIntegerExp().getInteger();
                                    if ((indx.value > 0) && ((int)x_1 >= 56320) && ((int)x_1 <= 57343))
                                    {
                                        indx.value -= 1;
                                        buflen += 1;
                                    }
                                }
                                else
                                {
                                    buflen = (indx.value + 2 > len) ? len - indx.value : 2;
                                }
                                {
                                    int i_1 = 0;
                                    for (; (i_1 < buflen);i_1 += 1){
                                        Expression r_2 = (ale.elements.value.get()).get(indx.value + i_1);
                                        utf16buf.set(i_1, (char)(int)r_2.isIntegerExp().getInteger());
                                    }
                                }
                                n.value = 0;
                                errmsg = pcopy(utf_decodeWchar(ptr(utf16buf), buflen, n, rawvalue));
                                break;
                            case 4:
                                {
                                    if (rvs)
                                    {
                                        indx.value -= 1;
                                    }
                                    Expression r_4 = (ale.elements.value.get()).get(indx.value);
                                    rawvalue = (int)r_4.isIntegerExp().getInteger();
                                    n.value = 1;
                                }
                                break;
                            default:
                            throw new AssertionError("Unreachable code!");
                        }
                        if (!rvs)
                        {
                            indx.value += n.value;
                        }
                    }
                    else
                    {
                        int saveindx = 0;
                        switch ((se.sz.value & 0xFF))
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
                                errmsg = pcopy(utf_decodeChar(se.string.value, se.len.value, indx, rawvalue));
                                if (rvs)
                                {
                                    indx.value = saveindx;
                                }
                                break;
                            case 2:
                                if (rvs)
                                {
                                    indx.value -= 1;
                                    int wc = se.getCodeUnit(indx.value);
                                    if ((wc >= 56320) && (wc <= 57343))
                                    {
                                        indx.value -= 1;
                                    }
                                    saveindx = indx.value;
                                }
                                errmsg = pcopy(utf_decodeWchar(se.wstring.value, se.len.value, indx, rawvalue));
                                if (rvs)
                                {
                                    indx.value = saveindx;
                                }
                                break;
                            case 4:
                                if (rvs)
                                {
                                    indx.value -= 1;
                                }
                                rawvalue = se.getCodeUnit(indx.value);
                                if (!rvs)
                                {
                                    indx.value += 1;
                                }
                                break;
                            default:
                            throw new AssertionError("Unreachable code!");
                        }
                    }
                    if (errmsg != null)
                    {
                        deleg.error(new BytePtr("`%s`"), errmsg);
                        return CTFEExp.cantexp.value;
                    }
                    int charlen = 1;
                    switch (charType.size())
                    {
                        case 1L:
                            charlen = utf_codeLengthChar(rawvalue);
                            utf_encodeChar(ptr(utf8buf), rawvalue);
                            break;
                        case 2L:
                            charlen = utf_codeLengthWchar(rawvalue);
                            utf_encodeWchar(ptr(utf16buf), rawvalue);
                            break;
                        case 4L:
                            break;
                        default:
                        throw new AssertionError("Unreachable code!");
                    }
                    if (rvs)
                    {
                        currentIndex = indx.value;
                    }
                    if ((numParams == 2))
                    {
                        args.value.set(0, new IntegerExp(deleg.loc.value, (long)currentIndex, indexType));
                    }
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
                                    codepoint = rawvalue;
                                    break;
                                default:
                                throw new AssertionError("Unreachable code!");
                            }
                            val = new IntegerExp(str.loc.value, (long)codepoint, charType);
                            args.value.set(numParams - 1, val);
                            Ref<UnionExp> ue = ref(null);
                            eresult = interpretFunction(ptr(ue), fd, istate, ptr(args), pthis);
                            if ((pequals(eresult, ue.value.exp())))
                            {
                                eresult = ue.value.copy();
                            }
                            if (exceptionOrCantInterpret(eresult))
                            {
                                return eresult;
                            }
                            if ((eresult.isIntegerExp().getInteger() != 0L))
                            {
                                return eresult;
                            }
                        }
                    }
                }
            }
            return eresult;
        }
        finally {
        }
    }

    public static Expression evaluateIfBuiltin(Ptr<UnionExp> pue, Ptr<InterState> istate, Loc loc, FuncDeclaration fd, Ptr<DArray<Expression>> arguments, Expression pthis) {
        Expression e = null;
        int nargs = arguments != null ? (arguments.get()).length.value : 0;
        if (pthis == null)
        {
            if ((isBuiltin(fd) == BUILTIN.yes))
            {
                Ref<DArray<Expression>> args = ref(args.value = new DArray<Expression>(nargs));
                try {
                    {
                        Slice<Expression> __r1001 = args.value.opSlice().copy();
                        int __key1000 = 0;
                        for (; (__key1000 < __r1001.getLength());__key1000 += 1) {
                            Expression arg = __r1001.get(__key1000);
                            int i = __key1000;
                            Expression earg = (arguments.get()).get(i);
                            earg = interpret(earg, istate, CtfeGoal.ctfeNeedRvalue);
                            if (exceptionOrCantInterpret(earg))
                            {
                                return earg;
                            }
                            arg = earg;
                        }
                    }
                    e = eval_builtin(loc, fd, ptr(args));
                    if (e == null)
                    {
                        error(loc, new BytePtr("cannot evaluate unimplemented builtin `%s` at compile time"), fd.toChars());
                        e = CTFEExp.cantexp.value;
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
                Expression firstarg = (arguments.get()).get(0);
                {
                    TypeAArray firstAAtype = firstarg.type.value.toBasetype().isTypeAArray();
                    if ((firstAAtype) != null)
                    {
                        Identifier id = fd.ident.value;
                        if ((nargs == 1))
                        {
                            if ((pequals(id, Id.aaLen.value)))
                            {
                                return interpret_length(pue, istate, firstarg);
                            }
                            if ((pequals(fd.toParent2().ident.value, Id.object.value)))
                            {
                                if ((pequals(id, Id.keys)))
                                {
                                    return interpret_keys(pue, istate, firstarg, firstAAtype.index.value.arrayOf());
                                }
                                if ((pequals(id, Id.values)))
                                {
                                    return interpret_values(pue, istate, firstarg, firstAAtype.nextOf().arrayOf());
                                }
                                if ((pequals(id, Id.rehash)))
                                {
                                    return interpret(pue, firstarg, istate, CtfeGoal.ctfeNeedRvalue);
                                }
                                if ((pequals(id, Id.dup.value)))
                                {
                                    return interpret_dup(pue, istate, firstarg);
                                }
                            }
                        }
                        else
                        {
                            if ((pequals(id, Id._aaApply)))
                            {
                                return interpret_aaApply(pue, istate, firstarg, (arguments.get()).data.get(2));
                            }
                            if ((pequals(id, Id._aaApply2)))
                            {
                                return interpret_aaApply(pue, istate, firstarg, (arguments.get()).data.get(2));
                            }
                        }
                    }
                }
            }
        }
        if ((pthis != null) && (fd.fbody.value == null) && (fd.isCtorDeclaration() != null) && (fd.parent.value != null) && (fd.parent.value.parent.value != null) && (pequals(fd.parent.value.parent.value.ident.value, Id.object.value)))
        {
            if (((pthis.op.value & 0xFF) == 50) && (pequals(fd.parent.value.ident.value, Id.Throwable.value)))
            {
                StructLiteralExp se = ((ClassReferenceExp)pthis).value;
                assert(((arguments.get()).length.value <= (se.elements.value.get()).length.value));
                {
                    Slice<Expression> __r1003 = (arguments.get()).opSlice().copy();
                    int __key1002 = 0;
                    for (; (__key1002 < __r1003.getLength());__key1002 += 1) {
                        Expression arg = __r1003.get(__key1002);
                        int i = __key1002;
                        Expression elem = interpret(arg, istate, CtfeGoal.ctfeNeedRvalue);
                        if (exceptionOrCantInterpret(elem))
                        {
                            return elem;
                        }
                        se.elements.value.get().set(i, elem);
                    }
                }
                return CTFEExp.voidexp;
            }
        }
        if ((nargs == 1) && (pthis == null) && (pequals(fd.ident.value, Id.criticalenter)) || (pequals(fd.ident.value, Id.criticalexit)))
        {
            return CTFEExp.voidexp;
        }
        if (pthis == null)
        {
            int idlen = fd.ident.value.asString().getLength();
            BytePtr id = pcopy(fd.ident.value.toChars());
            if ((nargs == 2) && (idlen == 10) || (idlen == 11) && (strncmp(id, new BytePtr("_aApply"), 7) == 0))
            {
                boolean rvs = idlen == 11;
                byte c = id.get(idlen - 3);
                byte s = id.get(idlen - 2);
                byte n = id.get(idlen - 1);
                if (((n & 0xFF) == 49) || ((n & 0xFF) == 50) && ((c & 0xFF) == 99) || ((c & 0xFF) == 119) || ((c & 0xFF) == 100) && ((s & 0xFF) == 99) || ((s & 0xFF) == 119) || ((s & 0xFF) == 100) && ((c & 0xFF) != (s & 0xFF)))
                {
                    Expression str = (arguments.get()).get(0);
                    str = interpret(str, istate, CtfeGoal.ctfeNeedRvalue);
                    if (exceptionOrCantInterpret(str))
                    {
                        return str;
                    }
                    return foreachApplyUtf(pue, istate, str, (arguments.get()).get(1), rvs);
                }
            }
        }
        return e;
    }

    public static Expression evaluatePostblit(Ptr<InterState> istate, Expression e) {
        TypeStruct ts = e.type.value.baseElemOf().isTypeStruct();
        if (ts == null)
        {
            return null;
        }
        StructDeclaration sd = ts.sym.value;
        if (sd.postblit.value == null)
        {
            return null;
        }
        {
            ArrayLiteralExp ale = e.isArrayLiteralExp();
            if ((ale) != null)
            {
                {
                    Slice<Expression> __r1004 = (ale.elements.value.get()).opSlice().copy();
                    int __key1005 = 0;
                    for (; (__key1005 < __r1004.getLength());__key1005 += 1) {
                        Expression elem = __r1004.get(__key1005);
                        {
                            Expression ex = evaluatePostblit(istate, elem);
                            if ((ex) != null)
                            {
                                return ex;
                            }
                        }
                    }
                }
                return null;
            }
        }
        if (((e.op.value & 0xFF) == 49))
        {
            Ref<UnionExp> ue = ref(null);
            e = interpretFunction(ptr(ue), sd.postblit.value, istate, null, e);
            if ((pequals(e, ue.value.exp())))
            {
                e = ue.value.copy();
            }
            if (exceptionOrCantInterpret(e))
            {
                return e;
            }
            return null;
        }
        throw new AssertionError("Unreachable code!");
    }

    public static Expression evaluateDtor(Ptr<InterState> istate, Expression e) {
        TypeStruct ts = e.type.value.baseElemOf().isTypeStruct();
        if (ts == null)
        {
            return null;
        }
        StructDeclaration sd = ts.sym.value;
        if (sd.dtor.value == null)
        {
            return null;
        }
        Ref<UnionExp> ue = ref(null);
        {
            ArrayLiteralExp ale = e.isArrayLiteralExp();
            if ((ale) != null)
            {
                {
                    Slice<Expression> __r1006 = (ale.elements.value.get()).opSlice().copy();
                    int __key1007 = __r1006.getLength();
                    for (; __key1007-- != 0;) {
                        Expression elem = __r1006.get(__key1007);
                        e = evaluateDtor(istate, elem);
                    }
                }
            }
            else if (((e.op.value & 0xFF) == 49))
            {
                e = interpretFunction(ptr(ue), sd.dtor.value, istate, null, e);
            }
            else
            {
                throw new AssertionError("Unreachable code!");
            }
        }
        if (exceptionOrCantInterpret(e))
        {
            if ((pequals(e, ue.value.exp())))
            {
                e = ue.value.copy();
            }
            return e;
        }
        return null;
    }

    public static boolean hasValue(VarDeclaration vd) {
        if ((vd.ctfeAdrOnStack == -1))
        {
            return false;
        }
        return null != getValue(vd);
    }

    public static void setValueWithoutChecking(VarDeclaration vd, Expression newval) {
        ctfeStack.setValue(vd, newval);
    }

    public static void setValue(VarDeclaration vd, Expression newval) {
        assert((vd.storage_class.value & 2101248L) != 0 ? isCtfeReferenceValid(newval) : isCtfeValueValid(newval));
        ctfeStack.setValue(vd, newval);
    }

}
