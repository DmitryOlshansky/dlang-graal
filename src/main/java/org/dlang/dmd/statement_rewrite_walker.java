package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.visitor.*;

public class statement_rewrite_walker {

    public static class StatementRewriteWalker extends SemanticTimePermissiveVisitor
    {
        public Ptr<Statement> ps = null;
        // Erasure: visitStmt<Statement>
        public  void visitStmt(Ref<Statement> s) {
            this.ps = pcopy(ptr(s));
            s.value.accept(this);
        }

        // Erasure: replaceCurrent<Statement>
        public  void replaceCurrent(Statement s) {
            this.ps.set(0, s);
        }

        // Erasure: visit<PeelStatement>
        public  void visit(PeelStatement s) {
            if (s.s.value != null)
            {
                this.visitStmt(s);
            }
        }

        // Erasure: visit<CompoundStatement>
        public  void visit(CompoundStatement s) {
            if ((s.statements != null) && ((s.statements).length != 0))
            {
                {
                    int i = 0;
                    for (; (i < (s.statements).length);i++){
                        if ((s.statements).get(i) != null)
                        {
                            this.visitStmt((s.statements).get(i));
                        }
                    }
                }
            }
        }

        // Erasure: visit<CompoundDeclarationStatement>
        public  void visit(CompoundDeclarationStatement s) {
            this.visit((CompoundStatement)s);
        }

        // Erasure: visit<UnrolledLoopStatement>
        public  void visit(UnrolledLoopStatement s) {
            if ((s.statements != null) && ((s.statements).length != 0))
            {
                {
                    int i = 0;
                    for (; (i < (s.statements).length);i++){
                        if ((s.statements).get(i) != null)
                        {
                            this.visitStmt((s.statements).get(i));
                        }
                    }
                }
            }
        }

        // Erasure: visit<ScopeStatement>
        public  void visit(ScopeStatement s) {
            if (s.statement.value != null)
            {
                this.visitStmt(statement);
            }
        }

        // Erasure: visit<WhileStatement>
        public  void visit(WhileStatement s) {
            if (s._body.value != null)
            {
                this.visitStmt(_body);
            }
        }

        // Erasure: visit<DoStatement>
        public  void visit(DoStatement s) {
            if (s._body.value != null)
            {
                this.visitStmt(_body);
            }
        }

        // Erasure: visit<ForStatement>
        public  void visit(ForStatement s) {
            if (s._init.value != null)
            {
                this.visitStmt(_init);
            }
            if (s._body.value != null)
            {
                this.visitStmt(_body);
            }
        }

        // Erasure: visit<ForeachStatement>
        public  void visit(ForeachStatement s) {
            if (s._body.value != null)
            {
                this.visitStmt(_body);
            }
        }

        // Erasure: visit<ForeachRangeStatement>
        public  void visit(ForeachRangeStatement s) {
            if (s._body.value != null)
            {
                this.visitStmt(_body);
            }
        }

        // Erasure: visit<IfStatement>
        public  void visit(IfStatement s) {
            if (s.ifbody.value != null)
            {
                this.visitStmt(ifbody);
            }
            if (s.elsebody.value != null)
            {
                this.visitStmt(elsebody);
            }
        }

        // Erasure: visit<SwitchStatement>
        public  void visit(SwitchStatement s) {
            if (s._body.value != null)
            {
                this.visitStmt(_body);
            }
        }

        // Erasure: visit<CaseStatement>
        public  void visit(CaseStatement s) {
            if (s.statement.value != null)
            {
                this.visitStmt(statement);
            }
        }

        // Erasure: visit<CaseRangeStatement>
        public  void visit(CaseRangeStatement s) {
            if (s.statement.value != null)
            {
                this.visitStmt(statement);
            }
        }

        // Erasure: visit<DefaultStatement>
        public  void visit(DefaultStatement s) {
            if (s.statement.value != null)
            {
                this.visitStmt(statement);
            }
        }

        // Erasure: visit<SynchronizedStatement>
        public  void visit(SynchronizedStatement s) {
            if (s._body.value != null)
            {
                this.visitStmt(_body);
            }
        }

        // Erasure: visit<WithStatement>
        public  void visit(WithStatement s) {
            if (s._body.value != null)
            {
                this.visitStmt(_body);
            }
        }

        // Erasure: visit<TryCatchStatement>
        public  void visit(TryCatchStatement s) {
            if (s._body.value != null)
            {
                this.visitStmt(_body);
            }
            if ((s.catches != null) && ((s.catches).length != 0))
            {
                {
                    int i = 0;
                    for (; (i < (s.catches).length);i++){
                        Catch c = (s.catches).get(i);
                        if ((c != null) && (c.handler.value != null))
                        {
                            this.visitStmt(handler);
                        }
                    }
                }
            }
        }

        // Erasure: visit<TryFinallyStatement>
        public  void visit(TryFinallyStatement s) {
            if (s._body.value != null)
            {
                this.visitStmt(_body);
            }
            if (s.finalbody.value != null)
            {
                this.visitStmt(finalbody);
            }
        }

        // Erasure: visit<DebugStatement>
        public  void visit(DebugStatement s) {
            if (s.statement.value != null)
            {
                this.visitStmt(statement);
            }
        }

        // Erasure: visit<LabelStatement>
        public  void visit(LabelStatement s) {
            if (s.statement.value != null)
            {
                this.visitStmt(statement);
            }
        }


        public StatementRewriteWalker() {}

        public StatementRewriteWalker copy() {
            StatementRewriteWalker that = new StatementRewriteWalker();
            that.ps = this.ps;
            return that;
        }
    }
}
