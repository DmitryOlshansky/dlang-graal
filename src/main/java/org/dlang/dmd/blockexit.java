package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.canthrow.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.visitor.*;

public class blockexit {
    private static class BlockExit extends Visitor
    {
        private Ref<FuncDeclaration> func = ref(null);
        private Ref<Boolean> mustNotThrow = ref(false);
        private IntRef result = ref(0);
        public  BlockExit(FuncDeclaration func, boolean mustNotThrow) {
            Ref<FuncDeclaration> func_ref = ref(func);
            Ref<Boolean> mustNotThrow_ref = ref(mustNotThrow);
            this.func.value = func_ref.value;
            this.mustNotThrow.value = mustNotThrow_ref.value;
            this.result.value = 0;
        }

        public  void visit(Statement s) {
            Ref<Statement> s_ref = ref(s);
            printf(new BytePtr("Statement::blockExit(%p)\n"), s_ref.value);
            printf(new BytePtr("%s\n"), s_ref.value.toChars());
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ErrorStatement s) {
            this.result.value = 0;
        }

        public  void visit(ExpStatement s) {
            this.result.value = 1;
            if (s.exp.value != null)
            {
                if (((s.exp.value.op.value & 0xFF) == 125))
                {
                    this.result.value = 16;
                    return ;
                }
                if (((s.exp.value.op.value & 0xFF) == 14))
                {
                    AssertExp a = (AssertExp)s.exp.value;
                    if (a.e1.value.isBool(false))
                    {
                        this.result.value = 16;
                        return ;
                    }
                }
                if (canThrow(s.exp.value, this.func.value, this.mustNotThrow.value))
                    this.result.value |= BE.throw_;
            }
        }

        public  void visit(CompileStatement s) {
            assert(global.errors.value != 0);
            this.result.value = 1;
        }

        public  void visit(CompoundStatement cs) {
            this.result.value = 1;
            Ref<Statement> slast = ref(null);
            {
                Ref<Slice<Statement>> __r799 = ref((cs.statements.get()).opSlice().copy());
                IntRef __key800 = ref(0);
                for (; (__key800.value < __r799.value.getLength());__key800.value += 1) {
                    Ref<Statement> s = ref(__r799.value.get(__key800.value));
                    if (s.value != null)
                    {
                        if (((this.result.value & BE.fallthru) != 0) && (slast.value != null))
                        {
                            slast.value = slast.value.last();
                            if ((slast.value != null) && (slast.value.isCaseStatement() != null) || (slast.value.isDefaultStatement() != null) && (s.value.isCaseStatement() != null) || (s.value.isDefaultStatement() != null))
                            {
                                Ref<CaseStatement> sc = ref(slast.value.isCaseStatement());
                                Ref<DefaultStatement> sd = ref(slast.value.isDefaultStatement());
                                if ((sc.value != null) && !sc.value.statement.value.hasCode() || (sc.value.statement.value.isCaseStatement() != null) || (sc.value.statement.value.isErrorStatement() != null))
                                {
                                }
                                else if ((sd.value != null) && !sd.value.statement.value.hasCode() || (sd.value.statement.value.isCaseStatement() != null) || (sd.value.statement.value.isErrorStatement() != null))
                                {
                                }
                                else
                                {
                                    Ref<BytePtr> gototype = ref(pcopy(s.value.isCaseStatement() != null ? new BytePtr("case") : new BytePtr("default")));
                                    s.value.deprecation(new BytePtr("switch case fallthrough - use 'goto %s;' if intended"), gototype.value);
                                }
                            }
                        }
                        if (((this.result.value & BE.fallthru) == 0) && !s.value.comeFrom())
                        {
                            if ((blockExit(s.value, this.func.value, this.mustNotThrow.value) != BE.halt) && s.value.hasCode())
                                s.value.warning(new BytePtr("statement is not reachable"));
                        }
                        else
                        {
                            this.result.value &= -2;
                            this.result.value |= blockExit(s.value, this.func.value, this.mustNotThrow.value);
                        }
                        slast.value = s.value;
                    }
                }
            }
        }

        public  void visit(UnrolledLoopStatement uls) {
            this.result.value = 1;
            {
                Ref<Slice<Statement>> __r801 = ref((uls.statements.get()).opSlice().copy());
                IntRef __key802 = ref(0);
                for (; (__key802.value < __r801.value.getLength());__key802.value += 1) {
                    Ref<Statement> s = ref(__r801.value.get(__key802.value));
                    if (s.value != null)
                    {
                        IntRef r = ref(blockExit(s.value, this.func.value, this.mustNotThrow.value));
                        this.result.value |= r.value & -98;
                        if (((r.value & 97) == 0))
                            this.result.value &= -2;
                    }
                }
            }
        }

        public  void visit(ScopeStatement s) {
            this.result.value = blockExit(s.statement.value, this.func.value, this.mustNotThrow.value);
        }

        public  void visit(WhileStatement s) {
            assert(global.errors.value != 0);
            this.result.value = 1;
        }

        public  void visit(DoStatement s) {
            if (s._body.value != null)
            {
                this.result.value = blockExit(s._body.value, this.func.value, this.mustNotThrow.value);
                if ((this.result.value == BE.break_))
                {
                    this.result.value = 1;
                    return ;
                }
                if ((this.result.value & BE.continue_) != 0)
                    this.result.value |= BE.fallthru;
            }
            else
                this.result.value = 1;
            if ((this.result.value & BE.fallthru) != 0)
            {
                if (canThrow(s.condition.value, this.func.value, this.mustNotThrow.value))
                    this.result.value |= BE.throw_;
                if (((this.result.value & BE.break_) == 0) && s.condition.value.isBool(true))
                    this.result.value &= -2;
            }
            this.result.value &= -97;
        }

        public  void visit(ForStatement s) {
            this.result.value = 1;
            if (s._init.value != null)
            {
                this.result.value = blockExit(s._init.value, this.func.value, this.mustNotThrow.value);
                if ((this.result.value & BE.fallthru) == 0)
                    return ;
            }
            if (s.condition.value != null)
            {
                if (canThrow(s.condition.value, this.func.value, this.mustNotThrow.value))
                    this.result.value |= BE.throw_;
                if (s.condition.value.isBool(true))
                    this.result.value &= -2;
                else if (s.condition.value.isBool(false))
                    return ;
            }
            else
                this.result.value &= -2;
            if (s._body.value != null)
            {
                IntRef r = ref(blockExit(s._body.value, this.func.value, this.mustNotThrow.value));
                if ((r.value & 40) != 0)
                    this.result.value |= BE.fallthru;
                this.result.value |= r.value & -98;
            }
            if ((s.increment.value != null) && canThrow(s.increment.value, this.func.value, this.mustNotThrow.value))
                this.result.value |= BE.throw_;
        }

        public  void visit(ForeachStatement s) {
            this.result.value = 1;
            if (canThrow(s.aggr.value, this.func.value, this.mustNotThrow.value))
                this.result.value |= BE.throw_;
            if (s._body.value != null)
                this.result.value |= blockExit(s._body.value, this.func.value, this.mustNotThrow.value) & -97;
        }

        public  void visit(ForeachRangeStatement s) {
            assert(global.errors.value != 0);
            this.result.value = 1;
        }

        public  void visit(IfStatement s) {
            this.result.value = 0;
            if (canThrow(s.condition.value, this.func.value, this.mustNotThrow.value))
                this.result.value |= BE.throw_;
            if (s.condition.value.isBool(true))
            {
                this.result.value |= blockExit(s.ifbody.value, this.func.value, this.mustNotThrow.value);
            }
            else if (s.condition.value.isBool(false))
            {
                this.result.value |= blockExit(s.elsebody.value, this.func.value, this.mustNotThrow.value);
            }
            else
            {
                this.result.value |= blockExit(s.ifbody.value, this.func.value, this.mustNotThrow.value);
                this.result.value |= blockExit(s.elsebody.value, this.func.value, this.mustNotThrow.value);
            }
        }

        public  void visit(ConditionalStatement s) {
            this.result.value = blockExit(s.ifbody.value, this.func.value, this.mustNotThrow.value);
            if (s.elsebody.value != null)
                this.result.value |= blockExit(s.elsebody.value, this.func.value, this.mustNotThrow.value);
        }

        public  void visit(PragmaStatement s) {
            this.result.value = 1;
        }

        public  void visit(StaticAssertStatement s) {
            this.result.value = 1;
        }

        public  void visit(SwitchStatement s) {
            this.result.value = 0;
            if (canThrow(s.condition.value, this.func.value, this.mustNotThrow.value))
                this.result.value |= BE.throw_;
            if (s._body.value != null)
            {
                this.result.value |= blockExit(s._body.value, this.func.value, this.mustNotThrow.value);
                if ((this.result.value & BE.break_) != 0)
                {
                    this.result.value |= BE.fallthru;
                    this.result.value &= -33;
                }
            }
            else
                this.result.value |= BE.fallthru;
        }

        public  void visit(CaseStatement s) {
            this.result.value = blockExit(s.statement.value, this.func.value, this.mustNotThrow.value);
        }

        public  void visit(DefaultStatement s) {
            this.result.value = blockExit(s.statement.value, this.func.value, this.mustNotThrow.value);
        }

        public  void visit(GotoDefaultStatement s) {
            this.result.value = 8;
        }

        public  void visit(GotoCaseStatement s) {
            this.result.value = 8;
        }

        public  void visit(SwitchErrorStatement s) {
            this.result.value = 16;
        }

        public  void visit(ReturnStatement s) {
            this.result.value = 4;
            if ((s.exp.value != null) && canThrow(s.exp.value, this.func.value, this.mustNotThrow.value))
                this.result.value |= BE.throw_;
        }

        public  void visit(BreakStatement s) {
            this.result.value = s.ident.value != null ? 8 : 32;
        }

        public  void visit(ContinueStatement s) {
            this.result.value = s.ident.value != null ? 72 : 64;
        }

        public  void visit(SynchronizedStatement s) {
            this.result.value = blockExit(s._body.value, this.func.value, this.mustNotThrow.value);
        }

        public  void visit(WithStatement s) {
            this.result.value = 0;
            if (canThrow(s.exp.value, this.func.value, this.mustNotThrow.value))
                this.result.value = 2;
            this.result.value |= blockExit(s._body.value, this.func.value, this.mustNotThrow.value);
        }

        public  void visit(TryCatchStatement s) {
            assert(s._body.value != null);
            this.result.value = blockExit(s._body.value, this.func.value, false);
            IntRef catchresult = ref(0);
            {
                Ref<Slice<Catch>> __r803 = ref((s.catches.get()).opSlice().copy());
                IntRef __key804 = ref(0);
                for (; (__key804.value < __r803.value.getLength());__key804.value += 1) {
                    Catch c = __r803.value.get(__key804.value);
                    if ((pequals(c.type.value, Type.terror.value)))
                        continue;
                    IntRef cresult = ref(blockExit(c.handler.value, this.func.value, this.mustNotThrow.value));
                    Ref<Identifier> id = ref(c.type.value.toBasetype().isClassHandle().ident.value);
                    if (c.internalCatch.value && ((cresult.value & BE.fallthru) != 0))
                    {
                        cresult.value &= -2;
                    }
                    else if ((pequals(id.value, Id.Object.value)) || (pequals(id.value, Id.Throwable.value)))
                    {
                        this.result.value &= -131;
                    }
                    else if ((pequals(id.value, Id.Exception.value)))
                    {
                        this.result.value &= -3;
                    }
                    catchresult.value |= cresult.value;
                }
            }
            if (this.mustNotThrow.value && ((this.result.value & BE.throw_) != 0))
            {
                blockExit(s._body.value, this.func.value, this.mustNotThrow.value);
            }
            this.result.value |= catchresult.value;
        }

        public  void visit(TryFinallyStatement s) {
            this.result.value = 1;
            if (s._body.value != null)
                this.result.value = blockExit(s._body.value, this.func.value, false);
            IntRef finalresult = ref(1);
            if (s.finalbody.value != null)
                finalresult.value = blockExit(s.finalbody.value, this.func.value, false);
            if ((this.result.value == BE.halt))
                finalresult.value = 0;
            if ((finalresult.value == BE.halt))
                this.result.value = 0;
            if (this.mustNotThrow.value)
            {
                if ((s._body.value != null) && ((this.result.value & BE.throw_) != 0))
                    blockExit(s._body.value, this.func.value, this.mustNotThrow.value);
                if ((s.finalbody.value != null) && ((finalresult.value & BE.throw_) != 0))
                    blockExit(s.finalbody.value, this.func.value, this.mustNotThrow.value);
            }
            if ((finalresult.value & BE.fallthru) == 0)
                this.result.value &= -2;
            this.result.value |= finalresult.value & -2;
        }

        public  void visit(ScopeGuardStatement s) {
            this.result.value = 1;
        }

        public  void visit(ThrowStatement s) {
            if (s.internalThrow.value)
            {
                this.result.value = 1;
                return ;
            }
            Type t = s.exp.type.value.toBasetype();
            Ref<ClassDeclaration> cd = ref(t.isClassHandle());
            assert(cd.value != null);
            if ((pequals(cd.value, ClassDeclaration.errorException.value)) || ClassDeclaration.errorException.value.isBaseOf(cd.value, null))
            {
                this.result.value = 128;
                return ;
            }
            if (this.mustNotThrow.value)
                s.error(new BytePtr("`%s` is thrown but not caught"), s.exp.type.value.toChars());
            this.result.value = 2;
        }

        public  void visit(GotoStatement s) {
            this.result.value = 8;
        }

        public  void visit(LabelStatement s) {
            this.result.value = blockExit(s.statement.value, this.func.value, this.mustNotThrow.value);
            if (s.breaks.value)
                this.result.value |= BE.fallthru;
        }

        public  void visit(CompoundAsmStatement s) {
            this.result.value = 29;
            if ((s.stc.value & 33554432L) == 0)
            {
                if (this.mustNotThrow.value && ((s.stc.value & 33554432L) == 0))
                    s.deprecation(new BytePtr("`asm` statement is assumed to throw - mark it with `nothrow` if it does not"));
                else
                    this.result.value |= BE.throw_;
            }
        }

        public  void visit(ImportStatement s) {
            this.result.value = 1;
        }


        public BlockExit() {}
    }


    public static class BE 
    {
        public static final int none = 0;
        public static final int fallthru = 1;
        public static final int throw_ = 2;
        public static final int return_ = 4;
        public static final int goto_ = 8;
        public static final int halt = 16;
        public static final int break_ = 32;
        public static final int continue_ = 64;
        public static final int errthrow = 128;
        public static final int any = 31;
    }

    public static int blockExit(Statement s, FuncDeclaration func, boolean mustNotThrow) {
        if (s == null)
            return 1;
        BlockExit be = new BlockExit(func, mustNotThrow);
        s.accept(be);
        return be.result.value;
    }

}
