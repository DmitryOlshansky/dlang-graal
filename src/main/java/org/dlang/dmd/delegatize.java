package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.visitor.*;

public class delegatize {
    private static class LambdaSetParent extends StoppableVisitor
    {
        private FuncDeclaration fd;
        public  LambdaSetParent(FuncDeclaration fd) {
            super();
            this.fd = fd;
        }

        public  void visit(Expression _param_0) {
        }

        public  void visit(DeclarationExp e) {
            e.declaration.parent = this.fd;
            e.declaration.accept(this);
        }

        public  void visit(IndexExp e) {
            if (e.lengthVar != null)
            {
                e.lengthVar.parent = this.fd;
                e.lengthVar.accept(this);
            }
        }

        public  void visit(SliceExp e) {
            if (e.lengthVar != null)
            {
                e.lengthVar.parent = this.fd;
                e.lengthVar.accept(this);
            }
        }

        public  void visit(Dsymbol _param_0) {
        }

        public  void visit(VarDeclaration v) {
            if (v._init != null)
                v._init.accept(this);
        }

        public  void visit(Initializer _param_0) {
        }

        public  void visit(ExpInitializer ei) {
            walkPostorder(ei.exp, this);
        }

        public  void visit(StructInitializer si) {
            {
                Slice<Identifier> __r900 = si.field.opSlice().copy();
                int __key899 = 0;
                for (; __key899 < __r900.getLength();__key899 += 1) {
                    Identifier id = __r900.get(__key899);
                    int i = __key899;
                    {
                        Initializer iz = si.value.get(i);
                        if (iz != null)
                            iz.accept(this);
                    }
                }
            }
        }

        public  void visit(ArrayInitializer ai) {
            {
                Slice<Expression> __r902 = ai.index.opSlice().copy();
                int __key901 = 0;
                for (; __key901 < __r902.getLength();__key901 += 1) {
                    Expression ex = __r902.get(__key901);
                    int i = __key901;
                    if (ex != null)
                        walkPostorder(ex, this);
                    {
                        Initializer iz = ai.value.get(i);
                        if (iz != null)
                            iz.accept(this);
                    }
                }
            }
        }

        private Object this;

        public LambdaSetParent() {}

        public LambdaSetParent copy() {
            LambdaSetParent that = new LambdaSetParent();
            that.fd = this.fd;
            that.this = this.this;
            that.stop = this.stop;
            return that;
        }
    }
    private static class LambdaCheckForNestedRef extends StoppableVisitor
    {
        private Scope sc;
        private boolean result;
        public  LambdaCheckForNestedRef(Scope sc) {
            super();
            this.sc = sc;
        }

        public  void visit(Expression _param_0) {
        }

        public  void visit(SymOffExp e) {
            VarDeclaration v = e.var.isVarDeclaration();
            if (v != null)
                this.result = v.checkNestedReference(this.sc, Loc.initial);
        }

        public  void visit(VarExp e) {
            VarDeclaration v = e.var.isVarDeclaration();
            if (v != null)
                this.result = v.checkNestedReference(this.sc, Loc.initial);
        }

        public  void visit(ThisExp e) {
            if (e.var != null)
                this.result = e.var.checkNestedReference(this.sc, Loc.initial);
        }

        public  void visit(DeclarationExp e) {
            VarDeclaration v = e.declaration.isVarDeclaration();
            if (v != null)
            {
                this.result = v.checkNestedReference(this.sc, Loc.initial);
                if (this.result)
                    return ;
                if ((v._init != null && v._init.isExpInitializer() != null))
                {
                    Expression ie = initializerToExpression(v._init, null);
                    this.result = lambdaCheckForNestedRef(ie, this.sc);
                }
            }
        }

        private Object this;

        public LambdaCheckForNestedRef() {}

        public LambdaCheckForNestedRef copy() {
            LambdaCheckForNestedRef that = new LambdaCheckForNestedRef();
            that.sc = this.sc;
            that.result = this.result;
            that.this = this.this;
            that.stop = this.stop;
            return that;
        }
    }

    public static Expression toDelegate(Expression e, Type t, Scope sc) {
        Loc loc = e.loc.copy();
        TypeFunction tf = new TypeFunction(new ParameterList(null, VarArg.none), t, LINK.d, 0L);
        if ((t.hasWild()) != 0)
            tf.mod = (byte)8;
        FuncLiteralDeclaration fld = new FuncLiteralDeclaration(loc, loc, tf, TOK.delegate_, null, null);
        lambdaSetParent(e, fld);
        sc = (sc).push();
        (sc).parent = fld;
        boolean r = lambdaCheckForNestedRef(e, sc);
        sc = (sc).pop();
        if (r)
            return new ErrorExp();
        Statement s = null;
        if ((t.ty & 0xFF) == ENUMTY.Tvoid)
            s = new ExpStatement(loc, e);
        else
            s = new ReturnStatement(loc, e);
        fld.fbody = s;
        e = new FuncExp(loc, fld);
        e = expressionSemantic(e, sc);
        return e;
    }

    public static void lambdaSetParent(Expression e, FuncDeclaration fd) {
        LambdaSetParent lsp = new LambdaSetParent(fd);
        walkPostorder(e, lsp);
    }

    public static boolean lambdaCheckForNestedRef(Expression e, Scope sc) {
        LambdaCheckForNestedRef v = new LambdaCheckForNestedRef(sc);
        walkPostorder(e, v);
        return v.result;
    }

    public static boolean ensureStaticLinkTo(Dsymbol s, Dsymbol p) {
        for (; s != null;){
            if (pequals(s, p))
                return true;
            {
                FuncDeclaration fd = s.isFuncDeclaration();
                if (fd != null)
                {
                    if ((!(fd.isThis() != null) && !(fd.isNested())))
                        break;
                    {
                        FuncLiteralDeclaration fld = fd.isFuncLiteralDeclaration();
                        if (fld != null)
                            fld.tok = TOK.delegate_;
                    }
                }
            }
            {
                AggregateDeclaration ad = s.isAggregateDeclaration();
                if (ad != null)
                {
                    if ((ad.storage_class & 1L) != 0)
                        break;
                }
            }
            s = toParentP(s, p);
        }
        return false;
    }

}
