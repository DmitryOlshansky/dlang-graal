package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.visitor.*;

public class apply {

    public static class PostorderExpressionVisitor extends StoppableVisitor
    {
        public StoppableVisitor v;
        public  PostorderExpressionVisitor(StoppableVisitor v) {
            super();
            this.v = v;
        }

        public  boolean doCond(Expression e) {
            if ((!(this.stop) && e != null))
                e.accept(this);
            return this.stop;
        }

        public  boolean doCond(DArray<Expression> e) {
            if (e == null)
                return false;
            {
                int i = 0;
                for (; (i < (e).length && !(this.stop));i++) {
                    this.doCond((e).get(i));
                }
            }
            return this.stop;
        }

        public  boolean applyTo(Expression e) {
            e.accept(this.v);
            this.stop = this.v.stop;
            return true;
        }

        public  void visit(Expression e) {
            this.applyTo(e);
        }

        public  void visit(NewExp e) {
            (((this.doCond(e.thisexp) || this.doCond(e.newargs)) || this.doCond(e.arguments)) || this.applyTo(e));
        }

        public  void visit(NewAnonClassExp e) {
            (((this.doCond(e.thisexp) || this.doCond(e.newargs)) || this.doCond(e.arguments)) || this.applyTo(e));
        }

        public  void visit(TypeidExp e) {
            (this.doCond(isExpression(e.obj)) || this.applyTo(e));
        }

        public  void visit(UnaExp e) {
            (this.doCond(e.e1) || this.applyTo(e));
        }

        public  void visit(BinExp e) {
            ((this.doCond(e.e1) || this.doCond(e.e2)) || this.applyTo(e));
        }

        public  void visit(AssertExp e) {
            ((this.doCond(e.e1) || this.doCond(e.msg)) || this.applyTo(e));
        }

        public  void visit(CallExp e) {
            ((this.doCond(e.e1) || this.doCond(e.arguments)) || this.applyTo(e));
        }

        public  void visit(ArrayExp e) {
            ((this.doCond(e.e1) || this.doCond(e.arguments)) || this.applyTo(e));
        }

        public  void visit(SliceExp e) {
            (((this.doCond(e.e1) || this.doCond(e.lwr)) || this.doCond(e.upr)) || this.applyTo(e));
        }

        public  void visit(ArrayLiteralExp e) {
            ((this.doCond(e.basis) || this.doCond(e.elements)) || this.applyTo(e));
        }

        public  void visit(AssocArrayLiteralExp e) {
            ((this.doCond(e.keys) || this.doCond(e.values)) || this.applyTo(e));
        }

        public  void visit(StructLiteralExp e) {
            if ((e.stageflags & 8) != 0)
                return ;
            int old = e.stageflags;
            e.stageflags |= 8;
            (this.doCond(e.elements) || this.applyTo(e));
            e.stageflags = old;
        }

        public  void visit(TupleExp e) {
            ((this.doCond(e.e0) || this.doCond(e.exps)) || this.applyTo(e));
        }

        public  void visit(CondExp e) {
            (((this.doCond(e.econd) || this.doCond(e.e1)) || this.doCond(e.e2)) || this.applyTo(e));
        }


        public PostorderExpressionVisitor() {}

        public PostorderExpressionVisitor copy() {
            PostorderExpressionVisitor that = new PostorderExpressionVisitor();
            that.v = this.v;
            that.stop = this.stop;
            return that;
        }
    }
    public static boolean walkPostorder(Expression e, StoppableVisitor v) {
        PostorderExpressionVisitor pv = new PostorderExpressionVisitor(v);
        e.accept(pv);
        return v.stop;
    }

}
