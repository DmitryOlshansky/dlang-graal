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

public class sapply {

    public static class PostorderStatementVisitor extends StoppableVisitor
    {
        public StoppableVisitor v = null;
        // Erasure: __ctor<StoppableVisitor>
        public  PostorderStatementVisitor(StoppableVisitor v) {
            super();
            this.v = v;
        }

        // Erasure: doCond<Statement>
        public  boolean doCond(Statement s) {
            if (!this.stop && (s != null))
            {
                s.accept(this);
            }
            return this.stop;
        }

        // Erasure: applyTo<Statement>
        public  boolean applyTo(Statement s) {
            s.accept(this.v);
            this.stop = this.v.stop;
            return true;
        }

        // Erasure: visit<Statement>
        public  void visit(Statement s) {
            this.applyTo(s);
        }

        // Erasure: visit<PeelStatement>
        public  void visit(PeelStatement s) {
            expr(this.doCond(s.s.value) || this.applyTo(s));
        }

        // Erasure: visit<CompoundStatement>
        public  void visit(CompoundStatement s) {
            {
                int i = 0;
                for (; (i < (s.statements).length);i++) {
                    if (this.doCond((s.statements).get(i)))
                    {
                        return ;
                    }
                }
            }
            this.applyTo(s);
        }

        // Erasure: visit<UnrolledLoopStatement>
        public  void visit(UnrolledLoopStatement s) {
            {
                int i = 0;
                for (; (i < (s.statements).length);i++) {
                    if (this.doCond((s.statements).get(i)))
                    {
                        return ;
                    }
                }
            }
            this.applyTo(s);
        }

        // Erasure: visit<ScopeStatement>
        public  void visit(ScopeStatement s) {
            expr(this.doCond(s.statement.value) || this.applyTo(s));
        }

        // Erasure: visit<WhileStatement>
        public  void visit(WhileStatement s) {
            expr(this.doCond(s._body.value) || this.applyTo(s));
        }

        // Erasure: visit<DoStatement>
        public  void visit(DoStatement s) {
            expr(this.doCond(s._body.value) || this.applyTo(s));
        }

        // Erasure: visit<ForStatement>
        public  void visit(ForStatement s) {
            expr(this.doCond(s._init.value) || this.doCond(s._body.value) || this.applyTo(s));
        }

        // Erasure: visit<ForeachStatement>
        public  void visit(ForeachStatement s) {
            expr(this.doCond(s._body.value) || this.applyTo(s));
        }

        // Erasure: visit<ForeachRangeStatement>
        public  void visit(ForeachRangeStatement s) {
            expr(this.doCond(s._body.value) || this.applyTo(s));
        }

        // Erasure: visit<IfStatement>
        public  void visit(IfStatement s) {
            expr(this.doCond(s.ifbody.value) || this.doCond(s.elsebody.value) || this.applyTo(s));
        }

        // Erasure: visit<PragmaStatement>
        public  void visit(PragmaStatement s) {
            expr(this.doCond(s._body) || this.applyTo(s));
        }

        // Erasure: visit<SwitchStatement>
        public  void visit(SwitchStatement s) {
            expr(this.doCond(s._body.value) || this.applyTo(s));
        }

        // Erasure: visit<CaseStatement>
        public  void visit(CaseStatement s) {
            expr(this.doCond(s.statement.value) || this.applyTo(s));
        }

        // Erasure: visit<DefaultStatement>
        public  void visit(DefaultStatement s) {
            expr(this.doCond(s.statement.value) || this.applyTo(s));
        }

        // Erasure: visit<SynchronizedStatement>
        public  void visit(SynchronizedStatement s) {
            expr(this.doCond(s._body.value) || this.applyTo(s));
        }

        // Erasure: visit<WithStatement>
        public  void visit(WithStatement s) {
            expr(this.doCond(s._body.value) || this.applyTo(s));
        }

        // Erasure: visit<TryCatchStatement>
        public  void visit(TryCatchStatement s) {
            if (this.doCond(s._body.value))
            {
                return ;
            }
            {
                int i = 0;
                for (; (i < (s.catches).length);i++) {
                    if (this.doCond((s.catches).get(i).handler.value))
                    {
                        return ;
                    }
                }
            }
            this.applyTo(s);
        }

        // Erasure: visit<TryFinallyStatement>
        public  void visit(TryFinallyStatement s) {
            expr(this.doCond(s._body.value) || this.doCond(s.finalbody.value) || this.applyTo(s));
        }

        // Erasure: visit<ScopeGuardStatement>
        public  void visit(ScopeGuardStatement s) {
            expr(this.doCond(s.statement) || this.applyTo(s));
        }

        // Erasure: visit<DebugStatement>
        public  void visit(DebugStatement s) {
            expr(this.doCond(s.statement.value) || this.applyTo(s));
        }

        // Erasure: visit<LabelStatement>
        public  void visit(LabelStatement s) {
            expr(this.doCond(s.statement.value) || this.applyTo(s));
        }


        public PostorderStatementVisitor() {}

        public PostorderStatementVisitor copy() {
            PostorderStatementVisitor that = new PostorderStatementVisitor();
            that.v = this.v;
            that.stop = this.stop;
            return that;
        }
    }
    // Erasure: walkPostorder<Statement, StoppableVisitor>
    public static boolean walkPostorder(Statement s, StoppableVisitor v) {
        PostorderStatementVisitor pv = new PostorderStatementVisitor(v);
        s.accept(pv);
        return v.stop;
    }

}
