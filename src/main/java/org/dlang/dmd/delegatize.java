package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.apply.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.initsem.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.visitor.*;

public class delegatize {
    private static class LambdaSetParent extends StoppableVisitor
    {
        private FuncDeclaration fd = null;
        public  LambdaSetParent(FuncDeclaration fd) {
            super();
            this.fd = fd;
        }

        public  void visit(Expression _param_0) {
        }

        public  void visit(DeclarationExp e) {
            e.declaration.parent.value = this.fd;
            e.declaration.accept(this);
        }

        public  void visit(IndexExp e) {
            if (e.lengthVar.value != null)
            {
                e.lengthVar.value.parent.value = this.fd;
                e.lengthVar.value.accept(this);
            }
        }

        public  void visit(SliceExp e) {
            if (e.lengthVar.value != null)
            {
                e.lengthVar.value.parent.value = this.fd;
                e.lengthVar.value.accept(this);
            }
        }

        public  void visit(Dsymbol _param_0) {
        }

        public  void visit(VarDeclaration v) {
            if (v._init != null)
            {
                v._init.accept(this);
            }
        }

        public  void visit(Initializer _param_0) {
        }

        public  void visit(ExpInitializer ei) {
            walkPostorder(ei.exp, this);
        }

        public  void visit(StructInitializer si) {
            {
                Slice<Identifier> __r938 = si.field.opSlice().copy();
                int __key937 = 0;
                for (; (__key937 < __r938.getLength());__key937 += 1) {
                    Identifier id = __r938.get(__key937);
                    int i = __key937;
                    {
                        Initializer iz = si.value.get(i);
                        if ((iz) != null)
                        {
                            iz.accept(this);
                        }
                    }
                }
            }
        }

        public  void visit(ArrayInitializer ai) {
            {
                Slice<Expression> __r940 = ai.index.opSlice().copy();
                int __key939 = 0;
                for (; (__key939 < __r940.getLength());__key939 += 1) {
                    Expression ex = __r940.get(__key939);
                    int i = __key939;
                    if (ex != null)
                    {
                        walkPostorder(ex, this);
                    }
                    {
                        Initializer iz = ai.value.get(i);
                        if ((iz) != null)
                        {
                            iz.accept(this);
                        }
                    }
                }
            }
        }


        public LambdaSetParent() {}
    }
    private static class LambdaCheckForNestedRef extends StoppableVisitor
    {
        private Ptr<Scope> sc = null;
        private boolean result = false;
        public  LambdaCheckForNestedRef(Ptr<Scope> sc) {
            super();
            this.sc = pcopy(sc);
        }

        public  void visit(Expression _param_0) {
        }

        public  void visit(SymOffExp e) {
            VarDeclaration v = e.var.isVarDeclaration();
            if (v != null)
            {
                this.result = v.checkNestedReference(this.sc, Loc.initial);
            }
        }

        public  void visit(VarExp e) {
            VarDeclaration v = e.var.isVarDeclaration();
            if (v != null)
            {
                this.result = v.checkNestedReference(this.sc, Loc.initial);
            }
        }

        public  void visit(ThisExp e) {
            if (e.var != null)
            {
                this.result = e.var.checkNestedReference(this.sc, Loc.initial);
            }
        }

        public  void visit(DeclarationExp e) {
            VarDeclaration v = e.declaration.isVarDeclaration();
            if (v != null)
            {
                this.result = v.checkNestedReference(this.sc, Loc.initial);
                if (this.result)
                {
                    return ;
                }
                if ((v._init != null) && (v._init.isExpInitializer() != null))
                {
                    Expression ie = initializerToExpression(v._init, null);
                    this.result = lambdaCheckForNestedRef(ie, this.sc);
                }
            }
        }


        public LambdaCheckForNestedRef() {}
    }

    public static Expression toDelegate(Expression e, Type t, Ptr<Scope> sc) {
        Loc loc = e.loc.copy();
        TypeFunction tf = new TypeFunction(new ParameterList(null, VarArg.none), t, LINK.d, 0L);
        if (t.hasWild() != 0)
        {
            tf.mod = (byte)8;
        }
        FuncLiteralDeclaration fld = new FuncLiteralDeclaration(loc, loc, tf, TOK.delegate_, null, null);
        lambdaSetParent(e, fld);
        sc = pcopy((sc.get()).push());
        (sc.get()).parent.value = fld;
        boolean r = lambdaCheckForNestedRef(e, sc);
        sc = pcopy((sc.get()).pop());
        if (r)
        {
            return new ErrorExp();
        }
        Statement s = null;
        if (((t.ty & 0xFF) == ENUMTY.Tvoid))
        {
            s = new ExpStatement(loc, e);
        }
        else
        {
            s = new ReturnStatement(loc, e);
        }
        fld.fbody.value = s;
        e = new FuncExp(loc, fld);
        e = expressionSemantic(e, sc);
        return e;
    }

    public static void lambdaSetParent(Expression e, FuncDeclaration fd) {
        // skipping duplicate class LambdaSetParent
        LambdaSetParent lsp = new LambdaSetParent(fd);
        walkPostorder(e, lsp);
    }

    public static boolean lambdaCheckForNestedRef(Expression e, Ptr<Scope> sc) {
        // skipping duplicate class LambdaCheckForNestedRef
        LambdaCheckForNestedRef v = new LambdaCheckForNestedRef(sc);
        walkPostorder(e, v);
        return v.result;
    }

    public static boolean ensureStaticLinkTo(Dsymbol s, Dsymbol p) {
        for (; s != null;){
            if ((pequals(s, p)))
            {
                return true;
            }
            {
                FuncDeclaration fd = s.isFuncDeclaration();
                if ((fd) != null)
                {
                    if ((fd.isThis() == null) && !fd.isNested())
                    {
                        break;
                    }
                    {
                        FuncLiteralDeclaration fld = fd.isFuncLiteralDeclaration();
                        if ((fld) != null)
                        {
                            fld.tok = TOK.delegate_;
                        }
                    }
                }
            }
            {
                AggregateDeclaration ad = s.isAggregateDeclaration();
                if ((ad) != null)
                {
                    if ((ad.storage_class & 1L) != 0)
                    {
                        break;
                    }
                }
            }
            s = toParentPDsymbol(s, p);
        }
        return false;
    }

}
