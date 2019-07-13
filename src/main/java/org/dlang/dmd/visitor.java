package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.astcodegen.*;
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.cond.*;
import static org.dlang.dmd.ctfeexpr.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.denum.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.nspace.*;
import static org.dlang.dmd.parsetimevisitor.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.staticassert.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.transitivevisitor.*;

public class visitor {

    public static class Visitor extends ParseTimeVisitorASTCodegen
    {
        public  void visit(ErrorStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(PeelStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(UnrolledLoopStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(SwitchErrorStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(DebugStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(DtorExpStatement s) {
            this.visit((ExpStatement)s);
        }

        public  void visit(ForwardingStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(OverloadSet s) {
            this.visit((Dsymbol)s);
        }

        public  void visit(LabelDsymbol s) {
            this.visit((Dsymbol)s);
        }

        public  void visit(WithScopeSymbol s) {
            this.visit((ScopeDsymbol)s);
        }

        public  void visit(ArrayScopeSymbol s) {
            this.visit((ScopeDsymbol)s);
        }

        public  void visit(OverDeclaration s) {
            this.visit((Declaration)s);
        }

        public  void visit(SymbolDeclaration s) {
            this.visit((Declaration)s);
        }

        public  void visit(ThisDeclaration s) {
            this.visit((VarDeclaration)s);
        }

        public  void visit(TypeInfoDeclaration s) {
            this.visit((VarDeclaration)s);
        }

        public  void visit(TypeInfoStructDeclaration s) {
            this.visit((TypeInfoDeclaration)s);
        }

        public  void visit(TypeInfoClassDeclaration s) {
            this.visit((TypeInfoDeclaration)s);
        }

        public  void visit(TypeInfoInterfaceDeclaration s) {
            this.visit((TypeInfoDeclaration)s);
        }

        public  void visit(TypeInfoPointerDeclaration s) {
            this.visit((TypeInfoDeclaration)s);
        }

        public  void visit(TypeInfoArrayDeclaration s) {
            this.visit((TypeInfoDeclaration)s);
        }

        public  void visit(TypeInfoStaticArrayDeclaration s) {
            this.visit((TypeInfoDeclaration)s);
        }

        public  void visit(TypeInfoAssociativeArrayDeclaration s) {
            this.visit((TypeInfoDeclaration)s);
        }

        public  void visit(TypeInfoEnumDeclaration s) {
            this.visit((TypeInfoDeclaration)s);
        }

        public  void visit(TypeInfoFunctionDeclaration s) {
            this.visit((TypeInfoDeclaration)s);
        }

        public  void visit(TypeInfoDelegateDeclaration s) {
            this.visit((TypeInfoDeclaration)s);
        }

        public  void visit(TypeInfoTupleDeclaration s) {
            this.visit((TypeInfoDeclaration)s);
        }

        public  void visit(TypeInfoConstDeclaration s) {
            this.visit((TypeInfoDeclaration)s);
        }

        public  void visit(TypeInfoInvariantDeclaration s) {
            this.visit((TypeInfoDeclaration)s);
        }

        public  void visit(TypeInfoSharedDeclaration s) {
            this.visit((TypeInfoDeclaration)s);
        }

        public  void visit(TypeInfoWildDeclaration s) {
            this.visit((TypeInfoDeclaration)s);
        }

        public  void visit(TypeInfoVectorDeclaration s) {
            this.visit((TypeInfoDeclaration)s);
        }

        public  void visit(FuncAliasDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        public  void visit(ErrorInitializer i) {
            this.visit((Initializer)i);
        }

        public  void visit(ErrorExp e) {
            this.visit((Expression)e);
        }

        public  void visit(ComplexExp e) {
            this.visit((Expression)e);
        }

        public  void visit(StructLiteralExp e) {
            this.visit((Expression)e);
        }

        public  void visit(ObjcClassReferenceExp e) {
            this.visit((Expression)e);
        }

        public  void visit(SymOffExp e) {
            this.visit((SymbolExp)e);
        }

        public  void visit(OverExp e) {
            this.visit((Expression)e);
        }

        public  void visit(HaltExp e) {
            this.visit((Expression)e);
        }

        public  void visit(DotTemplateExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(DotVarExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(DelegateExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(DotTypeExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(VectorExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(VectorArrayExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(SliceExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(ArrayLengthExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(DelegatePtrExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(DelegateFuncptrExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(DotExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(IndexExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(ConstructExp e) {
            this.visit((AssignExp)e);
        }

        public  void visit(BlitExp e) {
            this.visit((AssignExp)e);
        }

        public  void visit(RemoveExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(ClassReferenceExp e) {
            this.visit((Expression)e);
        }

        public  void visit(VoidInitExp e) {
            this.visit((Expression)e);
        }

        public  void visit(ThrownExceptionExp e) {
            this.visit((Expression)e);
        }


        public Visitor() {}

        public Visitor copy() {
            Visitor that = new Visitor();
            return that;
        }
    }
    public static class SemanticTimePermissiveVisitor extends Visitor
    {
        public  void visit(Dsymbol _param_0) {
        }

        public  void visit(Parameter _param_0) {
        }

        public  void visit(Statement _param_0) {
        }

        public  void visit(Type _param_0) {
        }

        public  void visit(Expression _param_0) {
        }

        public  void visit(TemplateParameter _param_0) {
        }

        public  void visit(Condition _param_0) {
        }

        public  void visit(Initializer _param_0) {
        }


        public SemanticTimePermissiveVisitor() {}

        public SemanticTimePermissiveVisitor copy() {
            SemanticTimePermissiveVisitor that = new SemanticTimePermissiveVisitor();
            return that;
        }
    }
    public static class SemanticTimeTransitiveVisitor extends SemanticTimePermissiveVisitor
    {
        // from template mixin ParseVisitMethods!(ASTCodegen)
        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ExpStatement s) {
            if ((s.exp.value != null) && ((s.exp.value.op.value & 0xFF) == 38))
            {
                ((DeclarationExp)s.exp.value).declaration.value.accept(this);
                return ;
            }
            if (s.exp.value != null)
                s.exp.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CompileStatement s) {
            this.visitArgs(s.exps, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CompoundStatement s) {
            {
                Slice<Statement> __r2867 = (s.statements.get()).opSlice().copy();
                int __key2868 = 0;
                for (; (__key2868 < __r2867.getLength());__key2868 += 1) {
                    Statement sx = __r2867.get(__key2868);
                    if (sx != null)
                        sx.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitVarDecl(VarDeclaration v) {
            if (v.type.value != null)
                this.visitType(v.type.value);
            if (v._init.value != null)
            {
                ExpInitializer ie = v._init.value.isExpInitializer();
                if ((ie != null) && ((ie.exp.value.op.value & 0xFF) == 95) || ((ie.exp.value.op.value & 0xFF) == 96))
                    ((AssignExp)ie.exp.value).e2.value.accept(this);
                else
                    v._init.value.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CompoundDeclarationStatement s) {
            {
                Slice<Statement> __r2873 = (s.statements.get()).opSlice().copy();
                int __key2874 = 0;
                for (; (__key2874 < __r2873.getLength());__key2874 += 1) {
                    Statement sx = __r2873.get(__key2874);
                    ExpStatement ds = sx != null ? sx.isExpStatement() : null;
                    if ((ds != null) && ((ds.exp.value.op.value & 0xFF) == 38))
                    {
                        Dsymbol d = ((DeclarationExp)ds.exp.value).declaration.value;
                        assert(d.isDeclaration() != null);
                        {
                            VarDeclaration v = d.isVarDeclaration();
                            if ((v) != null)
                                this.visitVarDecl(v);
                            else
                                d.accept(this);
                        }
                    }
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ScopeStatement s) {
            if (s.statement.value != null)
                s.statement.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(WhileStatement s) {
            s.condition.accept(this);
            if (s._body != null)
                s._body.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(DoStatement s) {
            if (s._body.value != null)
                s._body.value.accept(this);
            s.condition.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ForStatement s) {
            if (s._init.value != null)
                s._init.value.accept(this);
            if (s.condition.value != null)
                s.condition.value.accept(this);
            if (s.increment.value != null)
                s.increment.value.accept(this);
            if (s._body.value != null)
                s._body.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ForeachStatement s) {
            {
                Slice<Parameter> __r2875 = (s.parameters.get()).opSlice().copy();
                int __key2876 = 0;
                for (; (__key2876 < __r2875.getLength());__key2876 += 1) {
                    Parameter p = __r2875.get(__key2876);
                    if (p.type.value != null)
                        this.visitType(p.type.value);
                }
            }
            s.aggr.value.accept(this);
            if (s._body.value != null)
                s._body.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ForeachRangeStatement s) {
            if (s.prm.type.value != null)
                this.visitType(s.prm.type.value);
            s.lwr.value.accept(this);
            s.upr.value.accept(this);
            if (s._body.value != null)
                s._body.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(IfStatement s) {
            if ((s.prm != null) && (s.prm.type.value != null))
                this.visitType(s.prm.type.value);
            s.condition.value.accept(this);
            s.ifbody.value.accept(this);
            if (s.elsebody.value != null)
                s.elsebody.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ConditionalStatement s) {
            s.condition.accept(this);
            if (s.ifbody.value != null)
                s.ifbody.value.accept(this);
            if (s.elsebody.value != null)
                s.elsebody.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitArgs(Ptr<DArray<Expression>> expressions, Expression basis) {
            if ((expressions == null) || ((expressions.get()).length.value == 0))
                return ;
            {
                Slice<Expression> __r2865 = (expressions.get()).opSlice().copy();
                int __key2866 = 0;
                for (; (__key2866 < __r2865.getLength());__key2866 += 1) {
                    Expression el = __r2865.get(__key2866);
                    if (el == null)
                        el = basis;
                    if (el != null)
                        el.accept(this);
                }
            }
        }

        // defaulted all parameters starting with #2
        public  void visitArgs(Ptr<DArray<Expression>> expressions) {
            return visitArgs(expressions, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(PragmaStatement s) {
            if ((s.args != null) && ((s.args.get()).length.value != 0))
                this.visitArgs(s.args, null);
            if (s._body != null)
                s._body.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(StaticAssertStatement s) {
            s.sa.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(SwitchStatement s) {
            s.condition.value.accept(this);
            if (s._body.value != null)
                s._body.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CaseStatement s) {
            s.exp.accept(this);
            s.statement.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CaseRangeStatement s) {
            s.first.accept(this);
            s.last.accept(this);
            s.statement.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(DefaultStatement s) {
            s.statement.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(GotoCaseStatement s) {
            if (s.exp != null)
                s.exp.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ReturnStatement s) {
            if (s.exp.value != null)
                s.exp.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(SynchronizedStatement s) {
            if (s.exp != null)
                s.exp.accept(this);
            if (s._body.value != null)
                s._body.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(WithStatement s) {
            s.exp.value.accept(this);
            if (s._body.value != null)
                s._body.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TryCatchStatement s) {
            if (s._body.value != null)
                s._body.value.accept(this);
            {
                Slice<Catch> __r2877 = (s.catches.get()).opSlice().copy();
                int __key2878 = 0;
                for (; (__key2878 < __r2877.getLength());__key2878 += 1) {
                    Catch c = __r2877.get(__key2878);
                    this.visit(c);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TryFinallyStatement s) {
            s._body.value.accept(this);
            s.finalbody.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ScopeGuardStatement s) {
            s.statement.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ThrowStatement s) {
            s.exp.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(LabelStatement s) {
            if (s.statement.value != null)
                s.statement.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ImportStatement s) {
            {
                Slice<Dsymbol> __r2879 = (s.imports.get()).opSlice().copy();
                int __key2880 = 0;
                for (; (__key2880 < __r2879.getLength());__key2880 += 1) {
                    Dsymbol imp = __r2879.get(__key2880);
                    imp.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(Catch c) {
            if (c.type.value != null)
                this.visitType(c.type.value);
            if (c.handler.value != null)
                c.handler.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitType(Type t) {
            if (t == null)
                return ;
            if (((t.ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                this.visitFunctionType((TypeFunction)t, null);
                return ;
            }
            else
                t.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitFunctionType(TypeFunction t, TemplateDeclaration td) {
            if (t.next.value != null)
                this.visitType(t.next.value);
            if (td != null)
            {
                {
                    Slice<TemplateParameter> __r2869 = (td.origParameters.value.get()).opSlice().copy();
                    int __key2870 = 0;
                    for (; (__key2870 < __r2869.getLength());__key2870 += 1) {
                        TemplateParameter p = __r2869.get(__key2870);
                        p.accept(this);
                    }
                }
            }
            this.visitParameters(t.parameterList.parameters.value);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitParameters(Ptr<DArray<Parameter>> parameters) {
            if (parameters != null)
            {
                int dim = Parameter.dim(parameters);
                {
                    int __key2871 = 0;
                    int __limit2872 = dim;
                    for (; (__key2871 < __limit2872);__key2871 += 1) {
                        int i = __key2871;
                        Parameter fparam = Parameter.getNth(parameters, i, null);
                        fparam.accept(this);
                    }
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeVector t) {
            if (t.basetype.value == null)
                return ;
            t.basetype.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeSArray t) {
            t.next.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeDArray t) {
            t.next.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeAArray t) {
            t.next.value.accept(this);
            t.index.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypePointer t) {
            if (((t.next.value.ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                this.visitFunctionType((TypeFunction)t.next.value, null);
            }
            else
                t.next.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeReference t) {
            t.next.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeFunction t) {
            this.visitFunctionType(t, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeDelegate t) {
            this.visitFunctionType((TypeFunction)t.next.value, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitTypeQualified(TypeQualified t) {
            {
                Slice<RootObject> __r2881 = t.idents.opSlice().copy();
                int __key2882 = 0;
                for (; (__key2882 < __r2881.getLength());__key2882 += 1) {
                    RootObject id = __r2881.get(__key2882);
                    if ((id.dyncast() == DYNCAST.dsymbol))
                        ((TemplateInstance)id).accept(this);
                    else if ((id.dyncast() == DYNCAST.expression))
                        ((Expression)id).accept(this);
                    else if ((id.dyncast() == DYNCAST.type))
                        ((Type)id).accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeIdentifier t) {
            this.visitTypeQualified(t);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeInstance t) {
            t.tempinst.value.accept(this);
            this.visitTypeQualified(t);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeTypeof t) {
            t.exp.value.accept(this);
            this.visitTypeQualified(t);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeReturn t) {
            this.visitTypeQualified(t);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeTuple t) {
            this.visitParameters(t.arguments.value);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeSlice t) {
            t.next.value.accept(this);
            t.lwr.value.accept(this);
            t.upr.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeTraits t) {
            t.exp.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(StaticAssert s) {
            s.exp.accept(this);
            if (s.msg != null)
                s.msg.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(EnumMember em) {
            if (em.type.value != null)
                this.visitType(em.type.value);
            if (em.value() != null)
                em.value().accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitAttribDeclaration(AttribDeclaration d) {
            if (d.decl.value != null)
            {
                Slice<Dsymbol> __r2883 = (d.decl.value.get()).opSlice().copy();
                int __key2884 = 0;
                for (; (__key2884 < __r2883.getLength());__key2884 += 1) {
                    Dsymbol de = __r2883.get(__key2884);
                    de.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(AttribDeclaration d) {
            this.visitAttribDeclaration(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(StorageClassDeclaration d) {
            this.visitAttribDeclaration(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(DeprecatedDeclaration d) {
            d.msg.accept(this);
            this.visitAttribDeclaration(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(LinkDeclaration d) {
            this.visitAttribDeclaration(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CPPMangleDeclaration d) {
            this.visitAttribDeclaration(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ProtDeclaration d) {
            this.visitAttribDeclaration(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(AlignDeclaration d) {
            this.visitAttribDeclaration(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(AnonDeclaration d) {
            this.visitAttribDeclaration(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(PragmaDeclaration d) {
            if ((d.args != null) && ((d.args.get()).length.value != 0))
                this.visitArgs(d.args, null);
            this.visitAttribDeclaration(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ConditionalDeclaration d) {
            d.condition.accept(this);
            if (d.decl.value != null)
            {
                Slice<Dsymbol> __r2885 = (d.decl.value.get()).opSlice().copy();
                int __key2886 = 0;
                for (; (__key2886 < __r2885.getLength());__key2886 += 1) {
                    Dsymbol de = __r2885.get(__key2886);
                    de.accept(this);
                }
            }
            if (d.elsedecl.value != null)
            {
                Slice<Dsymbol> __r2887 = (d.elsedecl.value.get()).opSlice().copy();
                int __key2888 = 0;
                for (; (__key2888 < __r2887.getLength());__key2888 += 1) {
                    Dsymbol de = __r2887.get(__key2888);
                    de.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CompileDeclaration d) {
            this.visitArgs(d.exps, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(UserAttributeDeclaration d) {
            this.visitArgs(d.atts, null);
            this.visitAttribDeclaration(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitFuncBody(FuncDeclaration f) {
            if (f.frequires != null)
            {
                {
                    Slice<Statement> __r2889 = (f.frequires.get()).opSlice().copy();
                    int __key2890 = 0;
                    for (; (__key2890 < __r2889.getLength());__key2890 += 1) {
                        Statement frequire = __r2889.get(__key2890);
                        frequire.accept(this);
                    }
                }
            }
            if (f.fensures != null)
            {
                {
                    Slice<Ensure> __r2891 = (f.fensures.get()).opSlice().copy();
                    int __key2892 = 0;
                    for (; (__key2892 < __r2891.getLength());__key2892 += 1) {
                        Ensure fensure = __r2891.get(__key2892).copy();
                        fensure.ensure.accept(this);
                    }
                }
            }
            if (f.fbody.value != null)
            {
                f.fbody.value.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitBaseClasses(ClassDeclaration d) {
            if ((d == null) || ((d.baseclasses.get()).length.value == 0))
                return ;
            {
                Slice<Ptr<BaseClass>> __r2893 = (d.baseclasses.get()).opSlice().copy();
                int __key2894 = 0;
                for (; (__key2894 < __r2893.getLength());__key2894 += 1) {
                    Ptr<BaseClass> b = __r2893.get(__key2894);
                    this.visitType((b.get()).type.value);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  boolean visitEponymousMember(TemplateDeclaration d) {
            if ((d.members.value == null) || ((d.members.value.get()).length.value != 1))
                return false;
            Dsymbol onemember = (d.members.value.get()).get(0);
            if ((!pequals(onemember.ident.value, d.ident.value)))
                return false;
            {
                FuncDeclaration fd = onemember.isFuncDeclaration();
                if ((fd) != null)
                {
                    assert(fd.type.value != null);
                    this.visitFunctionType((TypeFunction)fd.type.value, d);
                    if (d.constraint.value != null)
                        d.constraint.value.accept(this);
                    this.visitFuncBody(fd);
                    return true;
                }
            }
            {
                AggregateDeclaration ad = onemember.isAggregateDeclaration();
                if ((ad) != null)
                {
                    this.visitTemplateParameters(d.parameters);
                    if (d.constraint.value != null)
                        d.constraint.value.accept(this);
                    this.visitBaseClasses(ad.isClassDeclaration());
                    if (ad.members.value != null)
                    {
                        Slice<Dsymbol> __r2897 = (ad.members.value.get()).opSlice().copy();
                        int __key2898 = 0;
                        for (; (__key2898 < __r2897.getLength());__key2898 += 1) {
                            Dsymbol s = __r2897.get(__key2898);
                            s.accept(this);
                        }
                    }
                    return true;
                }
            }
            {
                VarDeclaration vd = onemember.isVarDeclaration();
                if ((vd) != null)
                {
                    if (d.constraint.value != null)
                        return false;
                    if (vd.type.value != null)
                        this.visitType(vd.type.value);
                    this.visitTemplateParameters(d.parameters);
                    if (vd._init.value != null)
                    {
                        ExpInitializer ie = vd._init.value.isExpInitializer();
                        if ((ie != null) && ((ie.exp.value.op.value & 0xFF) == 95) || ((ie.exp.value.op.value & 0xFF) == 96))
                            ((AssignExp)ie.exp.value).e2.value.accept(this);
                        else
                            vd._init.value.accept(this);
                        return true;
                    }
                }
            }
            return false;
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitTemplateParameters(Ptr<DArray<TemplateParameter>> parameters) {
            if ((parameters == null) || ((parameters.get()).length.value == 0))
                return ;
            {
                Slice<TemplateParameter> __r2895 = (parameters.get()).opSlice().copy();
                int __key2896 = 0;
                for (; (__key2896 < __r2895.getLength());__key2896 += 1) {
                    TemplateParameter p = __r2895.get(__key2896);
                    p.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TemplateDeclaration d) {
            if (this.visitEponymousMember(d))
                return ;
            this.visitTemplateParameters(d.parameters);
            if (d.constraint.value != null)
                d.constraint.value.accept(this);
            {
                Slice<Dsymbol> __r2899 = (d.members.value.get()).opSlice().copy();
                int __key2900 = 0;
                for (; (__key2900 < __r2899.getLength());__key2900 += 1) {
                    Dsymbol s = __r2899.get(__key2900);
                    s.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitObject(RootObject oarg) {
            {
                Type t = isType(oarg);
                if ((t) != null)
                {
                    this.visitType(t);
                }
                else {
                    Expression e = isExpression(oarg);
                    if ((e) != null)
                    {
                        e.accept(this);
                    }
                    else {
                        Tuple v = isTuple(oarg);
                        if ((v) != null)
                        {
                            Ptr<DArray<RootObject>> args = ptr(v.objects);
                            {
                                Slice<RootObject> __r2901 = (args.get()).opSlice().copy();
                                int __key2902 = 0;
                                for (; (__key2902 < __r2901.getLength());__key2902 += 1) {
                                    RootObject arg = __r2901.get(__key2902);
                                    this.visitObject(arg);
                                }
                            }
                        }
                    }
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitTiargs(TemplateInstance ti) {
            if (ti.tiargs.value == null)
                return ;
            {
                Slice<RootObject> __r2903 = (ti.tiargs.value.get()).opSlice().copy();
                int __key2904 = 0;
                for (; (__key2904 < __r2903.getLength());__key2904 += 1) {
                    RootObject arg = __r2903.get(__key2904);
                    this.visitObject(arg);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TemplateInstance ti) {
            this.visitTiargs(ti);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TemplateMixin tm) {
            this.visitType(tm.tqual);
            this.visitTiargs(tm);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(EnumDeclaration d) {
            if (d.memtype.value != null)
                this.visitType(d.memtype.value);
            if (d.members.value == null)
                return ;
            {
                Slice<Dsymbol> __r2905 = (d.members.value.get()).opSlice().copy();
                int __key2906 = 0;
                for (; (__key2906 < __r2905.getLength());__key2906 += 1) {
                    Dsymbol em = __r2905.get(__key2906);
                    if (em == null)
                        continue;
                    em.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(Nspace d) {
            {
                Slice<Dsymbol> __r2907 = (d.members.value.get()).opSlice().copy();
                int __key2908 = 0;
                for (; (__key2908 < __r2907.getLength());__key2908 += 1) {
                    Dsymbol s = __r2907.get(__key2908);
                    s.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(StructDeclaration d) {
            if (d.members.value == null)
                return ;
            {
                Slice<Dsymbol> __r2909 = (d.members.value.get()).opSlice().copy();
                int __key2910 = 0;
                for (; (__key2910 < __r2909.getLength());__key2910 += 1) {
                    Dsymbol s = __r2909.get(__key2910);
                    s.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ClassDeclaration d) {
            this.visitBaseClasses(d);
            if (d.members.value != null)
            {
                Slice<Dsymbol> __r2911 = (d.members.value.get()).opSlice().copy();
                int __key2912 = 0;
                for (; (__key2912 < __r2911.getLength());__key2912 += 1) {
                    Dsymbol s = __r2911.get(__key2912);
                    s.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(AliasDeclaration d) {
            if (d.aliassym.value != null)
                d.aliassym.value.accept(this);
            else
                this.visitType(d.type.value);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(VarDeclaration d) {
            this.visitVarDecl(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(FuncDeclaration f) {
            TypeFunction tf = (TypeFunction)f.type.value;
            this.visitType(tf);
            this.visitFuncBody(f);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(FuncLiteralDeclaration f) {
            if (((f.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                return ;
            TypeFunction tf = (TypeFunction)f.type.value;
            if (!f.inferRetType && (tf.next.value != null))
                this.visitType(tf.next.value);
            this.visitParameters(tf.parameterList.parameters.value);
            CompoundStatement cs = f.fbody.value.isCompoundStatement();
            Statement s = cs == null ? f.fbody.value : null;
            ReturnStatement rs = s != null ? s.isReturnStatement() : null;
            if ((rs != null) && (rs.exp.value != null))
                rs.exp.value.accept(this);
            else
                this.visitFuncBody(f);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(PostBlitDeclaration d) {
            this.visitFuncBody(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(DtorDeclaration d) {
            this.visitFuncBody(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(StaticCtorDeclaration d) {
            this.visitFuncBody(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(StaticDtorDeclaration d) {
            this.visitFuncBody(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(InvariantDeclaration d) {
            this.visitFuncBody(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(UnitTestDeclaration d) {
            this.visitFuncBody(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(NewDeclaration d) {
            this.visitParameters(d.parameters);
            this.visitFuncBody(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(DeleteDeclaration d) {
            this.visitParameters(d.parameters);
            this.visitFuncBody(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(StructInitializer si) {
            {
                Slice<Identifier> __r2914 = si.field.opSlice().copy();
                int __key2913 = 0;
                for (; (__key2913 < __r2914.getLength());__key2913 += 1) {
                    Identifier id = __r2914.get(__key2913);
                    int i = __key2913;
                    {
                        Initializer iz = si.value.get(i);
                        if ((iz) != null)
                            iz.accept(this);
                    }
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ArrayInitializer ai) {
            {
                Slice<Expression> __r2916 = ai.index.opSlice().copy();
                int __key2915 = 0;
                for (; (__key2915 < __r2916.getLength());__key2915 += 1) {
                    Expression ex = __r2916.get(__key2915);
                    int i = __key2915;
                    if (ex != null)
                        ex.accept(this);
                    {
                        Initializer iz = ai.value.get(i);
                        if ((iz) != null)
                            iz.accept(this);
                    }
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ExpInitializer ei) {
            ei.exp.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ArrayLiteralExp e) {
            this.visitArgs(e.elements.value, e.basis.value);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(AssocArrayLiteralExp e) {
            {
                Slice<Expression> __r2918 = (e.keys.value.get()).opSlice().copy();
                int __key2917 = 0;
                for (; (__key2917 < __r2918.getLength());__key2917 += 1) {
                    Expression key = __r2918.get(__key2917);
                    int i = __key2917;
                    key.accept(this);
                    (e.values.value.get()).get(i).accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeExp e) {
            this.visitType(e.type.value);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ScopeExp e) {
            if (e.sds.value.isTemplateInstance() != null)
                e.sds.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(NewExp e) {
            if (e.thisexp.value != null)
                e.thisexp.value.accept(this);
            if ((e.newargs.value != null) && ((e.newargs.value.get()).length.value != 0))
                this.visitArgs(e.newargs.value, null);
            this.visitType(e.newtype.value);
            if ((e.arguments.value != null) && ((e.arguments.value.get()).length.value != 0))
                this.visitArgs(e.arguments.value, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(NewAnonClassExp e) {
            if (e.thisexp != null)
                e.thisexp.accept(this);
            if ((e.newargs != null) && ((e.newargs.get()).length.value != 0))
                this.visitArgs(e.newargs, null);
            if ((e.arguments != null) && ((e.arguments.get()).length.value != 0))
                this.visitArgs(e.arguments, null);
            if (e.cd != null)
                e.cd.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TupleExp e) {
            if (e.e0.value != null)
                e.e0.value.accept(this);
            this.visitArgs(e.exps.value, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(FuncExp e) {
            e.fd.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(DeclarationExp e) {
            {
                VarDeclaration v = e.declaration.value.isVarDeclaration();
                if ((v) != null)
                    this.visitVarDecl(v);
                else
                    e.declaration.value.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeidExp e) {
            this.visitObject(e.obj.value);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TraitsExp e) {
            if (e.args.value != null)
            {
                Slice<RootObject> __r2919 = (e.args.value.get()).opSlice().copy();
                int __key2920 = 0;
                for (; (__key2920 < __r2919.getLength());__key2920 += 1) {
                    RootObject arg = __r2919.get(__key2920);
                    this.visitObject(arg);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(IsExp e) {
            this.visitType(e.targ.value);
            if (e.tspec != null)
                this.visitType(e.tspec);
            if ((e.parameters != null) && ((e.parameters.get()).length.value != 0))
                this.visitTemplateParameters(e.parameters);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(UnaExp e) {
            e.e1.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(BinExp e) {
            e.e1.value.accept(this);
            e.e2.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CompileExp e) {
            this.visitArgs(e.exps, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ImportExp e) {
            e.e1.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(AssertExp e) {
            e.e1.value.accept(this);
            if (e.msg != null)
                e.msg.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(DotIdExp e) {
            e.e1.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(DotTemplateInstanceExp e) {
            e.e1.value.accept(this);
            e.ti.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CallExp e) {
            e.e1.value.accept(this);
            this.visitArgs(e.arguments.value, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(PtrExp e) {
            e.e1.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(DeleteExp e) {
            e.e1.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CastExp e) {
            if (e.to.value != null)
                this.visitType(e.to.value);
            e.e1.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(IntervalExp e) {
            e.lwr.value.accept(this);
            e.upr.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ArrayExp e) {
            e.e1.value.accept(this);
            this.visitArgs(e.arguments.value, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(PostExp e) {
            e.e1.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CondExp e) {
            e.econd.value.accept(this);
            e.e1.value.accept(this);
            e.e2.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TemplateTypeParameter tp) {
            if (tp.specType != null)
                this.visitType(tp.specType);
            if (tp.defaultType.value != null)
                this.visitType(tp.defaultType.value);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TemplateThisParameter tp) {
            this.visit((TemplateTypeParameter)tp);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TemplateAliasParameter tp) {
            if (tp.specType != null)
                this.visitType(tp.specType);
            if (tp.specAlias != null)
                this.visitObject(tp.specAlias);
            if (tp.defaultAlias != null)
                this.visitObject(tp.defaultAlias);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TemplateValueParameter tp) {
            this.visitType(tp.valType);
            if (tp.specValue != null)
                tp.specValue.accept(this);
            if (tp.defaultValue != null)
                tp.defaultValue.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(StaticIfCondition c) {
            c.exp.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(Parameter p) {
            this.visitType(p.type.value);
            if (p.defaultArg.value != null)
                p.defaultArg.value.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(dmodule.Module m) {
            {
                Slice<Dsymbol> __r2921 = (m.members.value.get()).opSlice().copy();
                int __key2922 = 0;
                for (; (__key2922 < __r2921.getLength());__key2922 += 1) {
                    Dsymbol s = __r2921.get(__key2922);
                    s.accept(this);
                }
            }
        }


        public  void visit(PeelStatement s) {
            if (s.s != null)
                s.s.accept(this);
        }

        public  void visit(UnrolledLoopStatement s) {
            {
                Slice<Statement> __r2923 = (s.statements.get()).opSlice().copy();
                int __key2924 = 0;
                for (; (__key2924 < __r2923.getLength());__key2924 += 1) {
                    Statement sx = __r2923.get(__key2924);
                    if (sx != null)
                        sx.accept(this);
                }
            }
        }

        public  void visit(DebugStatement s) {
            if (s.statement != null)
                s.statement.accept(this);
        }

        public  void visit(ForwardingStatement s) {
            if (s.statement != null)
                s.statement.accept(this);
        }

        public  void visit(StructLiteralExp e) {
            if ((e.stageflags.value & 32) == 0)
            {
                int old = e.stageflags.value;
                e.stageflags.value |= 32;
                {
                    Slice<Expression> __r2925 = (e.elements.value.get()).opSlice().copy();
                    int __key2926 = 0;
                    for (; (__key2926 < __r2925.getLength());__key2926 += 1) {
                        Expression el = __r2925.get(__key2926);
                        if (el != null)
                            el.accept(this);
                    }
                }
                e.stageflags.value = old;
            }
        }

        public  void visit(DotTemplateExp e) {
            e.e1.value.accept(this);
        }

        public  void visit(DotVarExp e) {
            e.e1.value.accept(this);
        }

        public  void visit(DelegateExp e) {
            if (!e.func.value.isNested() || e.func.value.needThis())
                e.e1.value.accept(this);
        }

        public  void visit(DotTypeExp e) {
            e.e1.value.accept(this);
        }

        public  void visit(VectorExp e) {
            this.visitType(e.to);
            e.e1.value.accept(this);
        }

        public  void visit(VectorArrayExp e) {
            e.e1.value.accept(this);
        }

        public  void visit(SliceExp e) {
            e.e1.value.accept(this);
            if (e.upr.value != null)
                e.upr.value.accept(this);
            if (e.lwr.value != null)
                e.lwr.value.accept(this);
        }

        public  void visit(ArrayLengthExp e) {
            e.e1.value.accept(this);
        }

        public  void visit(DelegatePtrExp e) {
            e.e1.value.accept(this);
        }

        public  void visit(DelegateFuncptrExp e) {
            e.e1.value.accept(this);
        }

        public  void visit(DotExp e) {
            e.e1.value.accept(this);
            e.e2.value.accept(this);
        }

        public  void visit(IndexExp e) {
            e.e1.value.accept(this);
            e.e2.value.accept(this);
        }

        public  void visit(RemoveExp e) {
            e.e1.value.accept(this);
            e.e2.value.accept(this);
        }


        public SemanticTimeTransitiveVisitor() {}

        public SemanticTimeTransitiveVisitor copy() {
            SemanticTimeTransitiveVisitor that = new SemanticTimeTransitiveVisitor();
            return that;
        }
    }
    public static class StoppableVisitor extends Visitor
    {
        public Ref<Boolean> stop = ref(false);
        public  StoppableVisitor() {
        }


        public StoppableVisitor copy() {
            StoppableVisitor that = new StoppableVisitor();
            that.stop = this.stop;
            return that;
        }
    }
}
