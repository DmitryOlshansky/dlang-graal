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
        public StoppableVisitor v;
        public  PostorderStatementVisitor(StoppableVisitor v) {
            super();
            this.v = v;
        }

        public  boolean doCond(Statement s) {
            if (!this.stop && (s != null))
                s.accept(this);
            return this.stop;
        }

        public  boolean applyTo(Statement s) {
            s.accept(this.v);
            this.stop = this.v.stop;
            return true;
        }

        public  void visit(Statement s) {
            this.applyTo(s);
        }

        public  void visit(PeelStatement s) {
            this.doCond(s.s) || this.applyTo(s);
        }

        public  void visit(CompoundStatement s) {
            {
                int i = 0;
                for (; (i < (s.statements).length);i++) {
                    if (this.doCond((s.statements).get(i)))
                        return ;
                }
            }
            this.applyTo(s);
        }

        public  void visit(UnrolledLoopStatement s) {
            {
                int i = 0;
                for (; (i < (s.statements).length);i++) {
                    if (this.doCond((s.statements).get(i)))
                        return ;
                }
            }
            this.applyTo(s);
        }

        public  void visit(ScopeStatement s) {
            this.doCond(s.statement) || this.applyTo(s);
        }

        public  void visit(WhileStatement s) {
            this.doCond(s._body) || this.applyTo(s);
        }

        public  void visit(DoStatement s) {
            this.doCond(s._body) || this.applyTo(s);
        }

        public  void visit(ForStatement s) {
            this.doCond(s._init) || this.doCond(s._body) || this.applyTo(s);
        }

        public  void visit(ForeachStatement s) {
            this.doCond(s._body) || this.applyTo(s);
        }

        public  void visit(ForeachRangeStatement s) {
            this.doCond(s._body) || this.applyTo(s);
        }

        public  void visit(IfStatement s) {
            this.doCond(s.ifbody) || this.doCond(s.elsebody) || this.applyTo(s);
        }

        public  void visit(PragmaStatement s) {
            this.doCond(s._body) || this.applyTo(s);
        }

        public  void visit(SwitchStatement s) {
            this.doCond(s._body) || this.applyTo(s);
        }

        public  void visit(CaseStatement s) {
            this.doCond(s.statement) || this.applyTo(s);
        }

        public  void visit(DefaultStatement s) {
            this.doCond(s.statement) || this.applyTo(s);
        }

        public  void visit(SynchronizedStatement s) {
            this.doCond(s._body) || this.applyTo(s);
        }

        public  void visit(WithStatement s) {
            this.doCond(s._body) || this.applyTo(s);
        }

        public  void visit(TryCatchStatement s) {
            if (this.doCond(s._body))
                return ;
            {
                int i = 0;
                for (; (i < (s.catches).length);i++) {
                    if (this.doCond((s.catches).get(i).handler))
                        return ;
                }
            }
            this.applyTo(s);
        }

        public  void visit(TryFinallyStatement s) {
            this.doCond(s._body) || this.doCond(s.finalbody) || this.applyTo(s);
        }

        public  void visit(ScopeGuardStatement s) {
            this.doCond(s.statement) || this.applyTo(s);
        }

        public  void visit(DebugStatement s) {
            this.doCond(s.statement) || this.applyTo(s);
        }

        public  void visit(LabelStatement s) {
            this.doCond(s.statement) || this.applyTo(s);
        }


        public PostorderStatementVisitor() {}

        public PostorderStatementVisitor copy() {
            PostorderStatementVisitor that = new PostorderStatementVisitor();
            that.v = this.v;
            that.stop = this.stop;
            return that;
        }
    }
    public static boolean walkPostorder(Statement s, StoppableVisitor v) {
        PostorderStatementVisitor pv = new PostorderStatementVisitor(v);
        s.accept(pv);
        return v.stop;
    }

}
