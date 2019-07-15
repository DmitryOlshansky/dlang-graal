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
        // Erasure: __ctor<StoppableVisitor>
        public  PostorderExpressionVisitor(StoppableVisitor v) {
            super();
            this.v = v;
        }

        // Erasure: doCond<Expression>
        public  boolean doCond(Expression e) {
            if (!this.stop && (e != null))
            {
                e.accept(this);
            }
            return this.stop;
        }

        // Erasure: doCond<Ptr>
        public  boolean doCond(Ptr<DArray<Expression>> e) {
            if (e == null)
            {
                return false;
            }
            {
                int i = 0;
                for (; (i < (e.get()).length) && !this.stop;i++) {
                    this.doCond((e.get()).get(i));
                }
            }
            return this.stop;
        }

        // Erasure: applyTo<Expression>
        public  boolean applyTo(Expression e) {
            e.accept(this.v);
            this.stop = this.v.stop;
            return true;
        }

        // Erasure: visit<Expression>
        public  void visit(Expression e) {
            this.applyTo(e);
        }

        // Erasure: visit<NewExp>
        public  void visit(NewExp e) {
            expr(this.doCond(e.thisexp.value) || this.doCond(e.newargs) || this.doCond(e.arguments) || this.applyTo(e));
        }

        // Erasure: visit<NewAnonClassExp>
        public  void visit(NewAnonClassExp e) {
            expr(this.doCond(e.thisexp) || this.doCond(e.newargs) || this.doCond(e.arguments) || this.applyTo(e));
        }

        // Erasure: visit<TypeidExp>
        public  void visit(TypeidExp e) {
            expr(this.doCond(isExpression(e.obj)) || this.applyTo(e));
        }

        // Erasure: visit<UnaExp>
        public  void visit(UnaExp e) {
            expr(this.doCond(e.e1.value) || this.applyTo(e));
        }

        // Erasure: visit<BinExp>
        public  void visit(BinExp e) {
            expr(this.doCond(e.e1.value) || this.doCond(e.e2.value) || this.applyTo(e));
        }

        // Erasure: visit<AssertExp>
        public  void visit(AssertExp e) {
            expr(this.doCond(e.e1.value) || this.doCond(e.msg) || this.applyTo(e));
        }

        // Erasure: visit<CallExp>
        public  void visit(CallExp e) {
            expr(this.doCond(e.e1.value) || this.doCond(e.arguments) || this.applyTo(e));
        }

        // Erasure: visit<ArrayExp>
        public  void visit(ArrayExp e) {
            expr(this.doCond(e.e1.value) || this.doCond(e.arguments) || this.applyTo(e));
        }

        // Erasure: visit<SliceExp>
        public  void visit(SliceExp e) {
            expr(this.doCond(e.e1.value) || this.doCond(e.lwr.value) || this.doCond(e.upr.value) || this.applyTo(e));
        }

        // Erasure: visit<ArrayLiteralExp>
        public  void visit(ArrayLiteralExp e) {
            expr(this.doCond(e.basis.value) || this.doCond(e.elements) || this.applyTo(e));
        }

        // Erasure: visit<AssocArrayLiteralExp>
        public  void visit(AssocArrayLiteralExp e) {
            expr(this.doCond(e.keys) || this.doCond(e.values) || this.applyTo(e));
        }

        // Erasure: visit<StructLiteralExp>
        public  void visit(StructLiteralExp e) {
            if ((e.stageflags & 8) != 0)
            {
                return ;
            }
            int old = e.stageflags;
            e.stageflags |= 8;
            expr(this.doCond(e.elements) || this.applyTo(e));
            e.stageflags = old;
        }

        // Erasure: visit<TupleExp>
        public  void visit(TupleExp e) {
            expr(this.doCond(e.e0.value) || this.doCond(e.exps) || this.applyTo(e));
        }

        // Erasure: visit<CondExp>
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
    // Erasure: walkPostorder<Expression, StoppableVisitor>
    public static boolean walkPostorder(Expression e, StoppableVisitor v) {
        PostorderExpressionVisitor pv = new PostorderExpressionVisitor(v);
        e.accept(pv);
        return v.stop;
    }

}
