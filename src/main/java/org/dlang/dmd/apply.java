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
        public StoppableVisitor v = null;
        public  PostorderExpressionVisitor(StoppableVisitor v) {
            super();
            this.v = v;
        }

        public  boolean doCond(Expression e) {
            if (!this.stop.value && (e != null))
            {
                e.accept(this);
            }
            return this.stop.value;
        }

        public  boolean doCond(Ptr<DArray<Expression>> e) {
            if (e == null)
            {
                return false;
            }
            {
                int i = 0;
                for (; (i < (e.get()).length.value) && !this.stop.value;i++) {
                    this.doCond((e.get()).get(i));
                }
            }
            return this.stop.value;
        }

        public  boolean applyTo(Expression e) {
            e.accept(this.v);
            this.stop.value = this.v.stop.value;
            return true;
        }

        public  void visit(Expression e) {
            this.applyTo(e);
        }

        public  void visit(NewExp e) {
            expr(this.doCond(e.thisexp.value) || this.doCond(e.newargs.value) || this.doCond(e.arguments.value) || this.applyTo(e));
        }

        public  void visit(NewAnonClassExp e) {
            expr(this.doCond(e.thisexp) || this.doCond(e.newargs) || this.doCond(e.arguments) || this.applyTo(e));
        }

        public  void visit(TypeidExp e) {
            expr(this.doCond(isExpression(e.obj.value)) || this.applyTo(e));
        }

        public  void visit(UnaExp e) {
            expr(this.doCond(e.e1.value) || this.applyTo(e));
        }

        public  void visit(BinExp e) {
            expr(this.doCond(e.e1.value) || this.doCond(e.e2.value) || this.applyTo(e));
        }

        public  void visit(AssertExp e) {
            expr(this.doCond(e.e1.value) || this.doCond(e.msg) || this.applyTo(e));
        }

        public  void visit(CallExp e) {
            expr(this.doCond(e.e1.value) || this.doCond(e.arguments.value) || this.applyTo(e));
        }

        public  void visit(ArrayExp e) {
            expr(this.doCond(e.e1.value) || this.doCond(e.arguments.value) || this.applyTo(e));
        }

        public  void visit(SliceExp e) {
            expr(this.doCond(e.e1.value) || this.doCond(e.lwr.value) || this.doCond(e.upr.value) || this.applyTo(e));
        }

        public  void visit(ArrayLiteralExp e) {
            expr(this.doCond(e.basis.value) || this.doCond(e.elements.value) || this.applyTo(e));
        }

        public  void visit(AssocArrayLiteralExp e) {
            expr(this.doCond(e.keys.value) || this.doCond(e.values.value) || this.applyTo(e));
        }

        public  void visit(StructLiteralExp e) {
            if ((e.stageflags.value & 8) != 0)
            {
                return ;
            }
            int old = e.stageflags.value;
            e.stageflags.value |= 8;
            expr(this.doCond(e.elements.value) || this.applyTo(e));
            e.stageflags.value = old;
        }

        public  void visit(TupleExp e) {
            expr(this.doCond(e.e0.value) || this.doCond(e.exps.value) || this.applyTo(e));
        }

        public  void visit(CondExp e) {
            expr(this.doCond(e.econd.value) || this.doCond(e.e1.value) || this.doCond(e.e2.value) || this.applyTo(e));
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
        return v.stop.value;
    }

}
