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
        private FuncDeclaration func = null;
        private boolean mustNotThrow = false;
        private int result = 0;
        public  BlockExit(FuncDeclaration func, boolean mustNotThrow) {
            Ref<FuncDeclaration> func_ref = ref(func);
            Ref<Boolean> mustNotThrow_ref = ref(mustNotThrow);
            this.func = func_ref.value;
            this.mustNotThrow = mustNotThrow_ref.value;
            this.result = 0;
        }

        public  void visit(Statement s) {
            Ref<Statement> s_ref = ref(s);
            printf(new BytePtr("Statement::blockExit(%p)\n"), s_ref.value);
            printf(new BytePtr("%s\n"), s_ref.value.toChars());
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ErrorStatement s) {
            this.result = 0;
        }

        public  void visit(ExpStatement s) {
            Ref<ExpStatement> s_ref = ref(s);
            this.result = 1;
            if (s_ref.value.exp != null)
            {
                if (((s_ref.value.exp.op & 0xFF) == 125))
                {
                    this.result = 16;
                    return ;
                }
                if (((s_ref.value.exp.op & 0xFF) == 14))
                {
                    Ref<AssertExp> a = ref((AssertExp)s_ref.value.exp);
                    if (a.value.e1.isBool(false))
                    {
                        this.result = 16;
                        return ;
                    }
                }
                if (canThrow(s_ref.value.exp, this.func, this.mustNotThrow))
                    this.result |= BE.throw_;
            }
        }

        public  void visit(CompileStatement s) {
            assert(global.value.errors != 0);
            this.result = 1;
        }

        public  void visit(CompoundStatement cs) {
            Ref<CompoundStatement> cs_ref = ref(cs);
            this.result = 1;
            Ref<Statement> slast = ref(null);
            {
                Ref<Slice<Statement>> __r799 = ref((cs_ref.value.statements.get()).opSlice().copy());
                IntRef __key800 = ref(0);
                for (; (__key800.value < __r799.value.getLength());__key800.value += 1) {
                    Ref<Statement> s = ref(__r799.value.get(__key800.value));
                    if (s.value != null)
                    {
                        if (((this.result & BE.fallthru) != 0) && (slast.value != null))
                        {
                            slast.value = slast.value.last();
                            if ((slast.value != null) && (slast.value.isCaseStatement() != null) || (slast.value.isDefaultStatement() != null) && (s.value.isCaseStatement() != null) || (s.value.isDefaultStatement() != null))
                            {
                                Ref<CaseStatement> sc = ref(slast.value.isCaseStatement());
                                Ref<DefaultStatement> sd = ref(slast.value.isDefaultStatement());
                                if ((sc.value != null) && !sc.value.statement.hasCode() || (sc.value.statement.isCaseStatement() != null) || (sc.value.statement.isErrorStatement() != null))
                                {
                                }
                                else if ((sd.value != null) && !sd.value.statement.hasCode() || (sd.value.statement.isCaseStatement() != null) || (sd.value.statement.isErrorStatement() != null))
                                {
                                }
                                else
                                {
                                    Ref<BytePtr> gototype = ref(pcopy(s.value.isCaseStatement() != null ? new BytePtr("case") : new BytePtr("default")));
                                    s.value.deprecation(new BytePtr("switch case fallthrough - use 'goto %s;' if intended"), gototype.value);
                                }
                            }
                        }
                        if (((this.result & BE.fallthru) == 0) && !s.value.comeFrom())
                        {
                            if ((blockExit(s.value, this.func, this.mustNotThrow) != BE.halt) && s.value.hasCode())
                                s.value.warning(new BytePtr("statement is not reachable"));
                        }
                        else
                        {
                            this.result &= -2;
                            this.result |= blockExit(s.value, this.func, this.mustNotThrow);
                        }
                        slast.value = s.value;
                    }
                }
            }
        }

        public  void visit(UnrolledLoopStatement uls) {
            Ref<UnrolledLoopStatement> uls_ref = ref(uls);
            this.result = 1;
            {
                Ref<Slice<Statement>> __r801 = ref((uls_ref.value.statements.get()).opSlice().copy());
                IntRef __key802 = ref(0);
                for (; (__key802.value < __r801.value.getLength());__key802.value += 1) {
                    Ref<Statement> s = ref(__r801.value.get(__key802.value));
                    if (s.value != null)
                    {
                        IntRef r = ref(blockExit(s.value, this.func, this.mustNotThrow));
                        this.result |= r.value & -98;
                        if (((r.value & 97) == 0))
                            this.result &= -2;
                    }
                }
            }
        }

        public  void visit(ScopeStatement s) {
            Ref<ScopeStatement> s_ref = ref(s);
            this.result = blockExit(s_ref.value.statement, this.func, this.mustNotThrow);
        }

        public  void visit(WhileStatement s) {
            assert(global.value.errors != 0);
            this.result = 1;
        }

        public  void visit(DoStatement s) {
            Ref<DoStatement> s_ref = ref(s);
            if (s_ref.value._body != null)
            {
                this.result = blockExit(s_ref.value._body, this.func, this.mustNotThrow);
                if ((this.result == BE.break_))
                {
                    this.result = 1;
                    return ;
                }
                if ((this.result & BE.continue_) != 0)
                    this.result |= BE.fallthru;
            }
            else
                this.result = 1;
            if ((this.result & BE.fallthru) != 0)
            {
                if (canThrow(s_ref.value.condition, this.func, this.mustNotThrow))
                    this.result |= BE.throw_;
                if (((this.result & BE.break_) == 0) && s_ref.value.condition.isBool(true))
                    this.result &= -2;
            }
            this.result &= -97;
        }

        public  void visit(ForStatement s) {
            Ref<ForStatement> s_ref = ref(s);
            this.result = 1;
            if (s_ref.value._init != null)
            {
                this.result = blockExit(s_ref.value._init, this.func, this.mustNotThrow);
                if ((this.result & BE.fallthru) == 0)
                    return ;
            }
            if (s_ref.value.condition != null)
            {
                if (canThrow(s_ref.value.condition, this.func, this.mustNotThrow))
                    this.result |= BE.throw_;
                if (s_ref.value.condition.isBool(true))
                    this.result &= -2;
                else if (s_ref.value.condition.isBool(false))
                    return ;
            }
            else
                this.result &= -2;
            if (s_ref.value._body != null)
            {
                IntRef r = ref(blockExit(s_ref.value._body, this.func, this.mustNotThrow));
                if ((r.value & 40) != 0)
                    this.result |= BE.fallthru;
                this.result |= r.value & -98;
            }
            if ((s_ref.value.increment != null) && canThrow(s_ref.value.increment, this.func, this.mustNotThrow))
                this.result |= BE.throw_;
        }

        public  void visit(ForeachStatement s) {
            Ref<ForeachStatement> s_ref = ref(s);
            this.result = 1;
            if (canThrow(s_ref.value.aggr, this.func, this.mustNotThrow))
                this.result |= BE.throw_;
            if (s_ref.value._body.value != null)
                this.result |= blockExit(s_ref.value._body.value, this.func, this.mustNotThrow) & -97;
        }

        public  void visit(ForeachRangeStatement s) {
            assert(global.value.errors != 0);
            this.result = 1;
        }

        public  void visit(IfStatement s) {
            Ref<IfStatement> s_ref = ref(s);
            this.result = 0;
            if (canThrow(s_ref.value.condition, this.func, this.mustNotThrow))
                this.result |= BE.throw_;
            if (s_ref.value.condition.isBool(true))
            {
                this.result |= blockExit(s_ref.value.ifbody, this.func, this.mustNotThrow);
            }
            else if (s_ref.value.condition.isBool(false))
            {
                this.result |= blockExit(s_ref.value.elsebody, this.func, this.mustNotThrow);
            }
            else
            {
                this.result |= blockExit(s_ref.value.ifbody, this.func, this.mustNotThrow);
                this.result |= blockExit(s_ref.value.elsebody, this.func, this.mustNotThrow);
            }
        }

        public  void visit(ConditionalStatement s) {
            Ref<ConditionalStatement> s_ref = ref(s);
            this.result = blockExit(s_ref.value.ifbody, this.func, this.mustNotThrow);
            if (s_ref.value.elsebody != null)
                this.result |= blockExit(s_ref.value.elsebody, this.func, this.mustNotThrow);
        }

        public  void visit(PragmaStatement s) {
            this.result = 1;
        }

        public  void visit(StaticAssertStatement s) {
            this.result = 1;
        }

        public  void visit(SwitchStatement s) {
            Ref<SwitchStatement> s_ref = ref(s);
            this.result = 0;
            if (canThrow(s_ref.value.condition, this.func, this.mustNotThrow))
                this.result |= BE.throw_;
            if (s_ref.value._body != null)
            {
                this.result |= blockExit(s_ref.value._body, this.func, this.mustNotThrow);
                if ((this.result & BE.break_) != 0)
                {
                    this.result |= BE.fallthru;
                    this.result &= -33;
                }
            }
            else
                this.result |= BE.fallthru;
        }

        public  void visit(CaseStatement s) {
            Ref<CaseStatement> s_ref = ref(s);
            this.result = blockExit(s_ref.value.statement, this.func, this.mustNotThrow);
        }

        public  void visit(DefaultStatement s) {
            Ref<DefaultStatement> s_ref = ref(s);
            this.result = blockExit(s_ref.value.statement, this.func, this.mustNotThrow);
        }

        public  void visit(GotoDefaultStatement s) {
            this.result = 8;
        }

        public  void visit(GotoCaseStatement s) {
            this.result = 8;
        }

        public  void visit(SwitchErrorStatement s) {
            this.result = 16;
        }

        public  void visit(ReturnStatement s) {
            Ref<ReturnStatement> s_ref = ref(s);
            this.result = 4;
            if ((s_ref.value.exp != null) && canThrow(s_ref.value.exp, this.func, this.mustNotThrow))
                this.result |= BE.throw_;
        }

        public  void visit(BreakStatement s) {
            Ref<BreakStatement> s_ref = ref(s);
            this.result = s_ref.value.ident != null ? 8 : 32;
        }

        public  void visit(ContinueStatement s) {
            Ref<ContinueStatement> s_ref = ref(s);
            this.result = s_ref.value.ident != null ? 72 : 64;
        }

        public  void visit(SynchronizedStatement s) {
            Ref<SynchronizedStatement> s_ref = ref(s);
            this.result = blockExit(s_ref.value._body, this.func, this.mustNotThrow);
        }

        public  void visit(WithStatement s) {
            Ref<WithStatement> s_ref = ref(s);
            this.result = 0;
            if (canThrow(s_ref.value.exp, this.func, this.mustNotThrow))
                this.result = 2;
            this.result |= blockExit(s_ref.value._body, this.func, this.mustNotThrow);
        }

        public  void visit(TryCatchStatement s) {
            Ref<TryCatchStatement> s_ref = ref(s);
            assert(s_ref.value._body != null);
            this.result = blockExit(s_ref.value._body, this.func, false);
            IntRef catchresult = ref(0);
            {
                Ref<Slice<Catch>> __r803 = ref((s_ref.value.catches.get()).opSlice().copy());
                IntRef __key804 = ref(0);
                for (; (__key804.value < __r803.value.getLength());__key804.value += 1) {
                    Ref<Catch> c = ref(__r803.value.get(__key804.value));
                    if ((pequals(c.value.type, Type.terror.value)))
                        continue;
                    IntRef cresult = ref(blockExit(c.value.handler, this.func, this.mustNotThrow));
                    Ref<Identifier> id = ref(c.value.type.toBasetype().isClassHandle().ident);
                    if (c.value.internalCatch && ((cresult.value & BE.fallthru) != 0))
                    {
                        cresult.value &= -2;
                    }
                    else if ((pequals(id.value, Id.Object.value)) || (pequals(id.value, Id.Throwable.value)))
                    {
                        this.result &= -131;
                    }
                    else if ((pequals(id.value, Id.Exception.value)))
                    {
                        this.result &= -3;
                    }
                    catchresult.value |= cresult.value;
                }
            }
            if (this.mustNotThrow && ((this.result & BE.throw_) != 0))
            {
                blockExit(s_ref.value._body, this.func, this.mustNotThrow);
            }
            this.result |= catchresult.value;
        }

        public  void visit(TryFinallyStatement s) {
            Ref<TryFinallyStatement> s_ref = ref(s);
            this.result = 1;
            if (s_ref.value._body != null)
                this.result = blockExit(s_ref.value._body, this.func, false);
            IntRef finalresult = ref(1);
            if (s_ref.value.finalbody != null)
                finalresult.value = blockExit(s_ref.value.finalbody, this.func, false);
            if ((this.result == BE.halt))
                finalresult.value = 0;
            if ((finalresult.value == BE.halt))
                this.result = 0;
            if (this.mustNotThrow)
            {
                if ((s_ref.value._body != null) && ((this.result & BE.throw_) != 0))
                    blockExit(s_ref.value._body, this.func, this.mustNotThrow);
                if ((s_ref.value.finalbody != null) && ((finalresult.value & BE.throw_) != 0))
                    blockExit(s_ref.value.finalbody, this.func, this.mustNotThrow);
            }
            if ((finalresult.value & BE.fallthru) == 0)
                this.result &= -2;
            this.result |= finalresult.value & -2;
        }

        public  void visit(ScopeGuardStatement s) {
            this.result = 1;
        }

        public  void visit(ThrowStatement s) {
            Ref<ThrowStatement> s_ref = ref(s);
            if (s_ref.value.internalThrow)
            {
                this.result = 1;
                return ;
            }
            Ref<Type> t = ref(s_ref.value.exp.type.value.toBasetype());
            Ref<ClassDeclaration> cd = ref(t.value.isClassHandle());
            assert(cd.value != null);
            if ((pequals(cd.value, ClassDeclaration.errorException.value)) || ClassDeclaration.errorException.value.isBaseOf(cd.value, null))
            {
                this.result = 128;
                return ;
            }
            if (this.mustNotThrow)
                s_ref.value.error(new BytePtr("`%s` is thrown but not caught"), s_ref.value.exp.type.value.toChars());
            this.result = 2;
        }

        public  void visit(GotoStatement s) {
            this.result = 8;
        }

        public  void visit(LabelStatement s) {
            Ref<LabelStatement> s_ref = ref(s);
            this.result = blockExit(s_ref.value.statement, this.func, this.mustNotThrow);
            if (s_ref.value.breaks)
                this.result |= BE.fallthru;
        }

        public  void visit(CompoundAsmStatement s) {
            Ref<CompoundAsmStatement> s_ref = ref(s);
            this.result = 29;
            if ((s_ref.value.stc & 33554432L) == 0)
            {
                if (this.mustNotThrow && ((s_ref.value.stc & 33554432L) == 0))
                    s_ref.value.deprecation(new BytePtr("`asm` statement is assumed to throw - mark it with `nothrow` if it does not"));
                else
                    this.result |= BE.throw_;
            }
        }

        public  void visit(ImportStatement s) {
            this.result = 1;
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
        return be.result;
    }

}
