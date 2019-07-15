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
        // Erasure: __ctor<FuncDeclaration, boolean>
        public  BlockExit(FuncDeclaration func, boolean mustNotThrow) {
            this.func = func;
            this.mustNotThrow = mustNotThrow;
            this.result = 0;
        }

        // Erasure: visit<Statement>
        public  void visit(Statement s) {
            printf(new BytePtr("Statement::blockExit(%p)\n"), s);
            printf(new BytePtr("%s\n"), s.toChars());
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<ErrorStatement>
        public  void visit(ErrorStatement s) {
            this.result = 0;
        }

        // Erasure: visit<ExpStatement>
        public  void visit(ExpStatement s) {
            this.result = 1;
            if (s.exp != null)
            {
                if (((s.exp.op & 0xFF) == 125))
                {
                    this.result = 16;
                    return ;
                }
                if (((s.exp.op & 0xFF) == 14))
                {
                    AssertExp a = (AssertExp)s.exp;
                    if (a.e1.value.isBool(false))
                    {
                        this.result = 16;
                        return ;
                    }
                }
                if (canThrow(s.exp, this.func, this.mustNotThrow))
                {
                    this.result |= BE.throw_;
                }
            }
        }

        // Erasure: visit<CompileStatement>
        public  void visit(CompileStatement s) {
            assert(global.errors != 0);
            this.result = 1;
        }

        // Erasure: visit<CompoundStatement>
        public  void visit(CompoundStatement cs) {
            this.result = 1;
            Statement slast = null;
            {
                Slice<Statement> __r797 = (cs.statements.get()).opSlice().copy();
                int __key798 = 0;
                for (; (__key798 < __r797.getLength());__key798 += 1) {
                    Statement s = __r797.get(__key798);
                    if (s != null)
                    {
                        if (((this.result & BE.fallthru) != 0) && (slast != null))
                        {
                            slast = slast.last();
                            if ((slast != null) && (slast.isCaseStatement() != null) || (slast.isDefaultStatement() != null) && (s.isCaseStatement() != null) || (s.isDefaultStatement() != null))
                            {
                                CaseStatement sc = slast.isCaseStatement();
                                DefaultStatement sd = slast.isDefaultStatement();
                                if ((sc != null) && !sc.statement.value.hasCode() || (sc.statement.value.isCaseStatement() != null) || (sc.statement.value.isErrorStatement() != null))
                                {
                                }
                                else if ((sd != null) && !sd.statement.value.hasCode() || (sd.statement.value.isCaseStatement() != null) || (sd.statement.value.isErrorStatement() != null))
                                {
                                }
                                else
                                {
                                    BytePtr gototype = pcopy(s.isCaseStatement() != null ? new BytePtr("case") : new BytePtr("default"));
                                    s.deprecation(new BytePtr("switch case fallthrough - use 'goto %s;' if intended"), gototype);
                                }
                            }
                        }
                        if (((this.result & BE.fallthru) == 0) && !s.comeFrom())
                        {
                            if ((blockExit(s, this.func, this.mustNotThrow) != BE.halt) && s.hasCode())
                            {
                                s.warning(new BytePtr("statement is not reachable"));
                            }
                        }
                        else
                        {
                            this.result &= -2;
                            this.result |= blockExit(s, this.func, this.mustNotThrow);
                        }
                        slast = s;
                    }
                }
            }
        }

        // Erasure: visit<UnrolledLoopStatement>
        public  void visit(UnrolledLoopStatement uls) {
            this.result = 1;
            {
                Slice<Statement> __r799 = (uls.statements.get()).opSlice().copy();
                int __key800 = 0;
                for (; (__key800 < __r799.getLength());__key800 += 1) {
                    Statement s = __r799.get(__key800);
                    if (s != null)
                    {
                        int r = blockExit(s, this.func, this.mustNotThrow);
                        this.result |= r & -98;
                        if (((r & 97) == 0))
                        {
                            this.result &= -2;
                        }
                    }
                }
            }
        }

        // Erasure: visit<ScopeStatement>
        public  void visit(ScopeStatement s) {
            this.result = blockExit(s.statement.value, this.func, this.mustNotThrow);
        }

        // Erasure: visit<WhileStatement>
        public  void visit(WhileStatement s) {
            assert(global.errors != 0);
            this.result = 1;
        }

        // Erasure: visit<DoStatement>
        public  void visit(DoStatement s) {
            if (s._body.value != null)
            {
                this.result = blockExit(s._body.value, this.func, this.mustNotThrow);
                if ((this.result == BE.break_))
                {
                    this.result = 1;
                    return ;
                }
                if ((this.result & BE.continue_) != 0)
                {
                    this.result |= BE.fallthru;
                }
            }
            else
            {
                this.result = 1;
            }
            if ((this.result & BE.fallthru) != 0)
            {
                if (canThrow(s.condition, this.func, this.mustNotThrow))
                {
                    this.result |= BE.throw_;
                }
                if (((this.result & BE.break_) == 0) && s.condition.isBool(true))
                {
                    this.result &= -2;
                }
            }
            this.result &= -97;
        }

        // Erasure: visit<ForStatement>
        public  void visit(ForStatement s) {
            this.result = 1;
            if (s._init.value != null)
            {
                this.result = blockExit(s._init.value, this.func, this.mustNotThrow);
                if ((this.result & BE.fallthru) == 0)
                {
                    return ;
                }
            }
            if (s.condition != null)
            {
                if (canThrow(s.condition, this.func, this.mustNotThrow))
                {
                    this.result |= BE.throw_;
                }
                if (s.condition.isBool(true))
                {
                    this.result &= -2;
                }
                else if (s.condition.isBool(false))
                {
                    return ;
                }
            }
            else
            {
                this.result &= -2;
            }
            if (s._body.value != null)
            {
                int r = blockExit(s._body.value, this.func, this.mustNotThrow);
                if ((r & 40) != 0)
                {
                    this.result |= BE.fallthru;
                }
                this.result |= r & -98;
            }
            if ((s.increment != null) && canThrow(s.increment, this.func, this.mustNotThrow))
            {
                this.result |= BE.throw_;
            }
        }

        // Erasure: visit<ForeachStatement>
        public  void visit(ForeachStatement s) {
            this.result = 1;
            if (canThrow(s.aggr.value, this.func, this.mustNotThrow))
            {
                this.result |= BE.throw_;
            }
            if (s._body.value != null)
            {
                this.result |= blockExit(s._body.value, this.func, this.mustNotThrow) & -97;
            }
        }

        // Erasure: visit<ForeachRangeStatement>
        public  void visit(ForeachRangeStatement s) {
            assert(global.errors != 0);
            this.result = 1;
        }

        // Erasure: visit<IfStatement>
        public  void visit(IfStatement s) {
            this.result = 0;
            if (canThrow(s.condition, this.func, this.mustNotThrow))
            {
                this.result |= BE.throw_;
            }
            if (s.condition.isBool(true))
            {
                this.result |= blockExit(s.ifbody.value, this.func, this.mustNotThrow);
            }
            else if (s.condition.isBool(false))
            {
                this.result |= blockExit(s.elsebody.value, this.func, this.mustNotThrow);
            }
            else
            {
                this.result |= blockExit(s.ifbody.value, this.func, this.mustNotThrow);
                this.result |= blockExit(s.elsebody.value, this.func, this.mustNotThrow);
            }
        }

        // Erasure: visit<ConditionalStatement>
        public  void visit(ConditionalStatement s) {
            this.result = blockExit(s.ifbody, this.func, this.mustNotThrow);
            if (s.elsebody != null)
            {
                this.result |= blockExit(s.elsebody, this.func, this.mustNotThrow);
            }
        }

        // Erasure: visit<PragmaStatement>
        public  void visit(PragmaStatement s) {
            this.result = 1;
        }

        // Erasure: visit<StaticAssertStatement>
        public  void visit(StaticAssertStatement s) {
            this.result = 1;
        }

        // Erasure: visit<SwitchStatement>
        public  void visit(SwitchStatement s) {
            this.result = 0;
            if (canThrow(s.condition, this.func, this.mustNotThrow))
            {
                this.result |= BE.throw_;
            }
            if (s._body.value != null)
            {
                this.result |= blockExit(s._body.value, this.func, this.mustNotThrow);
                if ((this.result & BE.break_) != 0)
                {
                    this.result |= BE.fallthru;
                    this.result &= -33;
                }
            }
            else
            {
                this.result |= BE.fallthru;
            }
        }

        // Erasure: visit<CaseStatement>
        public  void visit(CaseStatement s) {
            this.result = blockExit(s.statement.value, this.func, this.mustNotThrow);
        }

        // Erasure: visit<DefaultStatement>
        public  void visit(DefaultStatement s) {
            this.result = blockExit(s.statement.value, this.func, this.mustNotThrow);
        }

        // Erasure: visit<GotoDefaultStatement>
        public  void visit(GotoDefaultStatement s) {
            this.result = 8;
        }

        // Erasure: visit<GotoCaseStatement>
        public  void visit(GotoCaseStatement s) {
            this.result = 8;
        }

        // Erasure: visit<SwitchErrorStatement>
        public  void visit(SwitchErrorStatement s) {
            this.result = 16;
        }

        // Erasure: visit<ReturnStatement>
        public  void visit(ReturnStatement s) {
            this.result = 4;
            if ((s.exp != null) && canThrow(s.exp, this.func, this.mustNotThrow))
            {
                this.result |= BE.throw_;
            }
        }

        // Erasure: visit<BreakStatement>
        public  void visit(BreakStatement s) {
            this.result = s.ident != null ? 8 : 32;
        }

        // Erasure: visit<ContinueStatement>
        public  void visit(ContinueStatement s) {
            this.result = s.ident != null ? 72 : 64;
        }

        // Erasure: visit<SynchronizedStatement>
        public  void visit(SynchronizedStatement s) {
            this.result = blockExit(s._body.value, this.func, this.mustNotThrow);
        }

        // Erasure: visit<WithStatement>
        public  void visit(WithStatement s) {
            this.result = 0;
            if (canThrow(s.exp, this.func, this.mustNotThrow))
            {
                this.result = 2;
            }
            this.result |= blockExit(s._body.value, this.func, this.mustNotThrow);
        }

        // Erasure: visit<TryCatchStatement>
        public  void visit(TryCatchStatement s) {
            assert(s._body.value != null);
            this.result = blockExit(s._body.value, this.func, false);
            int catchresult = 0;
            {
                Slice<Catch> __r801 = (s.catches.get()).opSlice().copy();
                int __key802 = 0;
                for (; (__key802 < __r801.getLength());__key802 += 1) {
                    Catch c = __r801.get(__key802);
                    if ((pequals(c.type, Type.terror)))
                    {
                        continue;
                    }
                    int cresult = blockExit(c.handler.value, this.func, this.mustNotThrow);
                    Identifier id = c.type.toBasetype().isClassHandle().ident;
                    if (c.internalCatch && ((cresult & BE.fallthru) != 0))
                    {
                        cresult &= -2;
                    }
                    else if ((pequals(id, Id.Object)) || (pequals(id, Id.Throwable)))
                    {
                        this.result &= -131;
                    }
                    else if ((pequals(id, Id.Exception)))
                    {
                        this.result &= -3;
                    }
                    catchresult |= cresult;
                }
            }
            if (this.mustNotThrow && ((this.result & BE.throw_) != 0))
            {
                blockExit(s._body.value, this.func, this.mustNotThrow);
            }
            this.result |= catchresult;
        }

        // Erasure: visit<TryFinallyStatement>
        public  void visit(TryFinallyStatement s) {
            this.result = 1;
            if (s._body.value != null)
            {
                this.result = blockExit(s._body.value, this.func, false);
            }
            int finalresult = 1;
            if (s.finalbody.value != null)
            {
                finalresult = blockExit(s.finalbody.value, this.func, false);
            }
            if ((this.result == BE.halt))
            {
                finalresult = 0;
            }
            if ((finalresult == BE.halt))
            {
                this.result = 0;
            }
            if (this.mustNotThrow)
            {
                if ((s._body.value != null) && ((this.result & BE.throw_) != 0))
                {
                    blockExit(s._body.value, this.func, this.mustNotThrow);
                }
                if ((s.finalbody.value != null) && ((finalresult & BE.throw_) != 0))
                {
                    blockExit(s.finalbody.value, this.func, this.mustNotThrow);
                }
            }
            if ((finalresult & BE.fallthru) == 0)
            {
                this.result &= -2;
            }
            this.result |= finalresult & -2;
        }

        // Erasure: visit<ScopeGuardStatement>
        public  void visit(ScopeGuardStatement s) {
            this.result = 1;
        }

        // Erasure: visit<ThrowStatement>
        public  void visit(ThrowStatement s) {
            if (s.internalThrow)
            {
                this.result = 1;
                return ;
            }
            Type t = s.exp.type.value.toBasetype();
            ClassDeclaration cd = t.isClassHandle();
            assert(cd != null);
            if ((pequals(cd, ClassDeclaration.errorException)) || ClassDeclaration.errorException.isBaseOf(cd, null))
            {
                this.result = 128;
                return ;
            }
            if (this.mustNotThrow)
            {
                s.error(new BytePtr("`%s` is thrown but not caught"), s.exp.type.value.toChars());
            }
            this.result = 2;
        }

        // Erasure: visit<GotoStatement>
        public  void visit(GotoStatement s) {
            this.result = 8;
        }

        // Erasure: visit<LabelStatement>
        public  void visit(LabelStatement s) {
            this.result = blockExit(s.statement.value, this.func, this.mustNotThrow);
            if (s.breaks)
            {
                this.result |= BE.fallthru;
            }
        }

        // Erasure: visit<CompoundAsmStatement>
        public  void visit(CompoundAsmStatement s) {
            this.result = 29;
            if ((s.stc & 33554432L) == 0)
            {
                if (this.mustNotThrow && ((s.stc & 33554432L) == 0))
                {
                    s.deprecation(new BytePtr("`asm` statement is assumed to throw - mark it with `nothrow` if it does not"));
                }
                else
                {
                    this.result |= BE.throw_;
                }
            }
        }

        // Erasure: visit<ImportStatement>
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

    // Erasure: blockExit<Statement, FuncDeclaration, boolean>
    public static int blockExit(Statement s, FuncDeclaration func, boolean mustNotThrow) {
        // skipping duplicate class BlockExit
        if (s == null)
        {
            return 1;
        }
        BlockExit be = new BlockExit(func, mustNotThrow);
        s.accept(be);
        return be.result;
    }

}
