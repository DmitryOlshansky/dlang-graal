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
            Ref<FuncDeclaration> fd_ref = ref(fd);
            super();
            this.fd = fd_ref.value;
        }

        public  void visit(Expression _param_0) {
        }

        public  void visit(DeclarationExp e) {
            Ref<DeclarationExp> e_ref = ref(e);
            e_ref.value.declaration.parent.value = this.fd;
            e_ref.value.declaration.accept(this);
        }

        public  void visit(IndexExp e) {
            Ref<IndexExp> e_ref = ref(e);
            if (e_ref.value.lengthVar.value != null)
            {
                e_ref.value.lengthVar.value.parent.value = this.fd;
                e_ref.value.lengthVar.value.accept(this);
            }
        }

        public  void visit(SliceExp e) {
            Ref<SliceExp> e_ref = ref(e);
            if (e_ref.value.lengthVar.value != null)
            {
                e_ref.value.lengthVar.value.parent.value = this.fd;
                e_ref.value.lengthVar.value.accept(this);
            }
        }

        public  void visit(Dsymbol _param_0) {
        }

        public  void visit(VarDeclaration v) {
            Ref<VarDeclaration> v_ref = ref(v);
            if (v_ref.value._init != null)
                v_ref.value._init.accept(this);
        }

        public  void visit(Initializer _param_0) {
        }

        public  void visit(ExpInitializer ei) {
            Ref<ExpInitializer> ei_ref = ref(ei);
            walkPostorder(ei_ref.value.exp, this);
        }

        public  void visit(StructInitializer si) {
            Ref<StructInitializer> si_ref = ref(si);
            {
                Ref<Slice<Identifier>> __r922 = ref(si_ref.value.field.opSlice().copy());
                IntRef __key921 = ref(0);
                for (; (__key921.value < __r922.value.getLength());__key921.value += 1) {
                    Identifier id = __r922.value.get(__key921.value);
                    IntRef i = ref(__key921.value);
                    {
                        Ref<Initializer> iz = ref(si_ref.value.value.get(i.value));
                        if ((iz.value) != null)
                            iz.value.accept(this);
                    }
                }
            }
        }

        public  void visit(ArrayInitializer ai) {
            Ref<ArrayInitializer> ai_ref = ref(ai);
            {
                Ref<Slice<Expression>> __r924 = ref(ai_ref.value.index.opSlice().copy());
                IntRef __key923 = ref(0);
                for (; (__key923.value < __r924.value.getLength());__key923.value += 1) {
                    Ref<Expression> ex = ref(__r924.value.get(__key923.value));
                    IntRef i = ref(__key923.value);
                    if (ex.value != null)
                        walkPostorder(ex.value, this);
                    {
                        Ref<Initializer> iz = ref(ai_ref.value.value.get(i.value));
                        if ((iz.value) != null)
                            iz.value.accept(this);
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
            Ref<Ptr<Scope>> sc_ref = ref(sc);
            super();
            this.sc = sc_ref.value;
        }

        public  void visit(Expression _param_0) {
        }

        public  void visit(SymOffExp e) {
            Ref<SymOffExp> e_ref = ref(e);
            Ref<VarDeclaration> v = ref(e_ref.value.var.isVarDeclaration());
            if (v.value != null)
                this.result = v.value.checkNestedReference(this.sc, Loc.initial.value);
        }

        public  void visit(VarExp e) {
            Ref<VarExp> e_ref = ref(e);
            Ref<VarDeclaration> v = ref(e_ref.value.var.isVarDeclaration());
            if (v.value != null)
                this.result = v.value.checkNestedReference(this.sc, Loc.initial.value);
        }

        public  void visit(ThisExp e) {
            Ref<ThisExp> e_ref = ref(e);
            if (e_ref.value.var != null)
                this.result = e_ref.value.var.checkNestedReference(this.sc, Loc.initial.value);
        }

        public  void visit(DeclarationExp e) {
            Ref<DeclarationExp> e_ref = ref(e);
            Ref<VarDeclaration> v = ref(e_ref.value.declaration.isVarDeclaration());
            if (v.value != null)
            {
                this.result = v.value.checkNestedReference(this.sc, Loc.initial.value);
                if (this.result)
                    return ;
                if ((v.value._init != null) && (v.value._init.isExpInitializer() != null))
                {
                    Ref<Expression> ie = ref(initializerToExpression(v.value._init, null));
                    this.result = lambdaCheckForNestedRef(ie.value, this.sc);
                }
            }
        }


        public LambdaCheckForNestedRef() {}
    }

    public static Expression toDelegate(Expression e, Type t, Ptr<Scope> sc) {
        Loc loc = e.loc.copy();
        TypeFunction tf = new TypeFunction(new ParameterList(null, VarArg.none), t, LINK.d, 0L);
        if (t.hasWild() != 0)
            tf.mod = (byte)8;
        FuncLiteralDeclaration fld = new FuncLiteralDeclaration(loc, loc, tf, TOK.delegate_, null, null);
        lambdaSetParent(e, fld);
        sc = (sc.get()).push();
        (sc.get()).parent.value = fld;
        boolean r = lambdaCheckForNestedRef(e, sc);
        sc = (sc.get()).pop();
        if (r)
            return new ErrorExp();
        Statement s = null;
        if (((t.ty & 0xFF) == ENUMTY.Tvoid))
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

    public static boolean lambdaCheckForNestedRef(Expression e, Ptr<Scope> sc) {
        LambdaCheckForNestedRef v = new LambdaCheckForNestedRef(sc);
        walkPostorder(e, v);
        return v.result;
    }

    public static boolean ensureStaticLinkTo(Dsymbol s, Dsymbol p) {
        for (; s != null;){
            if ((pequals(s, p)))
                return true;
            {
                FuncDeclaration fd = s.isFuncDeclaration();
                if ((fd) != null)
                {
                    if ((fd.isThis() == null) && !fd.isNested())
                        break;
                    {
                        FuncLiteralDeclaration fld = fd.isFuncLiteralDeclaration();
                        if ((fld) != null)
                            fld.tok = TOK.delegate_;
                    }
                }
            }
            {
                AggregateDeclaration ad = s.isAggregateDeclaration();
                if ((ad) != null)
                {
                    if ((ad.storage_class & 1L) != 0)
                        break;
                }
            }
            s = toParentPDsymbol(s, p);
        }
        return false;
    }

}
