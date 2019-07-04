package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
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
        // from template mixin ParseVisitMethods!(ASTCodegen)// from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ExpStatement s) {
            if ((s.exp != null && (s.exp.op & 0xFF) == 38))
            {
                ((DeclarationExp)s.exp).declaration.accept(this);
                return ;
            }
            if (s.exp != null)
                s.exp.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CompileStatement s) {
            this.visitArgs(s.exps, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CompoundStatement s) {
            {
                Slice<Statement> __r2852 = (s.statements).opSlice().copy();
                int __key2853 = 0;
                for (; __key2853 < __r2852.getLength();__key2853 += 1) {
                    Statement sx = __r2852.get(__key2853);
                    if (sx != null)
                        sx.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitVarDecl(VarDeclaration v) {
            if (v.type != null)
                this.visitType(v.type);
            if (v._init != null)
            {
                ExpInitializer ie = v._init.isExpInitializer();
                if ((ie != null && ((ie.exp.op & 0xFF) == 95 || (ie.exp.op & 0xFF) == 96)))
                    ((AssignExp)ie.exp).e2.accept(this);
                else
                    v._init.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CompoundDeclarationStatement s) {
            {
                Slice<Statement> __r2858 = (s.statements).opSlice().copy();
                int __key2859 = 0;
                for (; __key2859 < __r2858.getLength();__key2859 += 1) {
                    Statement sx = __r2858.get(__key2859);
                    ExpStatement ds = sx != null ? sx.isExpStatement() : null;
                    if ((ds != null && (ds.exp.op & 0xFF) == 38))
                    {
                        Dsymbol d = ((DeclarationExp)ds.exp).declaration;
                        assert(d.isDeclaration() != null);
                        {
                            VarDeclaration v = d.isVarDeclaration();
                            if (v != null)
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
            if (s.statement != null)
                s.statement.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(WhileStatement s) {
            s.condition.accept(this);
            if (s._body != null)
                s._body.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(DoStatement s) {
            if (s._body != null)
                s._body.accept(this);
            s.condition.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ForStatement s) {
            if (s._init != null)
                s._init.accept(this);
            if (s.condition != null)
                s.condition.accept(this);
            if (s.increment != null)
                s.increment.accept(this);
            if (s._body != null)
                s._body.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ForeachStatement s) {
            {
                Slice<Parameter> __r2860 = (s.parameters).opSlice().copy();
                int __key2861 = 0;
                for (; __key2861 < __r2860.getLength();__key2861 += 1) {
                    Parameter p = __r2860.get(__key2861);
                    if (p.type != null)
                        this.visitType(p.type);
                }
            }
            s.aggr.accept(this);
            if (s._body != null)
                s._body.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ForeachRangeStatement s) {
            if (s.prm.type != null)
                this.visitType(s.prm.type);
            s.lwr.accept(this);
            s.upr.accept(this);
            if (s._body != null)
                s._body.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(IfStatement s) {
            if ((s.prm != null && s.prm.type != null))
                this.visitType(s.prm.type);
            s.condition.accept(this);
            s.ifbody.accept(this);
            if (s.elsebody != null)
                s.elsebody.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ConditionalStatement s) {
            s.condition.accept(this);
            if (s.ifbody != null)
                s.ifbody.accept(this);
            if (s.elsebody != null)
                s.elsebody.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitArgs(DArray<Expression> expressions, Expression basis) {
            if ((expressions == null || !(((expressions).length) != 0)))
                return ;
            {
                Slice<Expression> __r2850 = (expressions).opSlice().copy();
                int __key2851 = 0;
                for (; __key2851 < __r2850.getLength();__key2851 += 1) {
                    Expression el = __r2850.get(__key2851);
                    if (!(el != null))
                        el = basis;
                    if (el != null)
                        el.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(PragmaStatement s) {
            if ((s.args != null && ((s.args).length) != 0))
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
            s.condition.accept(this);
            if (s._body != null)
                s._body.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CaseStatement s) {
            s.exp.accept(this);
            s.statement.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CaseRangeStatement s) {
            s.first.accept(this);
            s.last.accept(this);
            s.statement.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(DefaultStatement s) {
            s.statement.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(GotoCaseStatement s) {
            if (s.exp != null)
                s.exp.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ReturnStatement s) {
            if (s.exp != null)
                s.exp.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(SynchronizedStatement s) {
            if (s.exp != null)
                s.exp.accept(this);
            if (s._body != null)
                s._body.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(WithStatement s) {
            s.exp.accept(this);
            if (s._body != null)
                s._body.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TryCatchStatement s) {
            if (s._body != null)
                s._body.accept(this);
            {
                Slice<Catch> __r2862 = (s.catches).opSlice().copy();
                int __key2863 = 0;
                for (; __key2863 < __r2862.getLength();__key2863 += 1) {
                    Catch c = __r2862.get(__key2863);
                    this.visit(c);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TryFinallyStatement s) {
            s._body.accept(this);
            s.finalbody.accept(this);
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
            if (s.statement != null)
                s.statement.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ImportStatement s) {
            {
                Slice<Dsymbol> __r2864 = (s.imports).opSlice().copy();
                int __key2865 = 0;
                for (; __key2865 < __r2864.getLength();__key2865 += 1) {
                    Dsymbol imp = __r2864.get(__key2865);
                    imp.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(Catch c) {
            if (c.type != null)
                this.visitType(c.type);
            if (c.handler != null)
                c.handler.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitType(Type t) {
            if (!(t != null))
                return ;
            if ((t.ty & 0xFF) == ENUMTY.Tfunction)
            {
                this.visitFunctionType((TypeFunction)t, null);
                return ;
            }
            else
                t.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitFunctionType(TypeFunction t, TemplateDeclaration td) {
            if (t.next != null)
                this.visitType(t.next);
            if (td != null)
            {
                {
                    Slice<TemplateParameter> __r2854 = (td.origParameters).opSlice().copy();
                    int __key2855 = 0;
                    for (; __key2855 < __r2854.getLength();__key2855 += 1) {
                        TemplateParameter p = __r2854.get(__key2855);
                        p.accept(this);
                    }
                }
            }
            this.visitParameters(t.parameterList.parameters);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitParameters(DArray<Parameter> parameters) {
            if (parameters != null)
            {
                int dim = Parameter.dim(parameters);
                {
                    int __key2856 = 0;
                    int __limit2857 = dim;
                    for (; __key2856 < __limit2857;__key2856 += 1) {
                        int i = __key2856;
                        Parameter fparam = Parameter.getNth(parameters, i, null);
                        fparam.accept(this);
                    }
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeVector t) {
            if (!(t.basetype != null))
                return ;
            t.basetype.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeSArray t) {
            t.next.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeDArray t) {
            t.next.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeAArray t) {
            t.next.accept(this);
            t.index.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypePointer t) {
            if ((t.next.ty & 0xFF) == ENUMTY.Tfunction)
            {
                this.visitFunctionType((TypeFunction)t.next, null);
            }
            else
                t.next.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeReference t) {
            t.next.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeFunction t) {
            this.visitFunctionType(t, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeDelegate t) {
            this.visitFunctionType((TypeFunction)t.next, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitTypeQualified(TypeQualified t) {
            {
                Slice<RootObject> __r2866 = t.idents.opSlice().copy();
                int __key2867 = 0;
                for (; __key2867 < __r2866.getLength();__key2867 += 1) {
                    RootObject id = __r2866.get(__key2867);
                    if (id.dyncast() == DYNCAST.dsymbol)
                        ((TemplateInstance)id).accept(this);
                    else if (id.dyncast() == DYNCAST.expression)
                        ((Expression)id).accept(this);
                    else if (id.dyncast() == DYNCAST.type)
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
            t.tempinst.accept(this);
            this.visitTypeQualified(t);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeTypeof t) {
            t.exp.accept(this);
            this.visitTypeQualified(t);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeReturn t) {
            this.visitTypeQualified(t);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeTuple t) {
            this.visitParameters(t.arguments);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeSlice t) {
            t.next.accept(this);
            t.lwr.accept(this);
            t.upr.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeTraits t) {
            t.exp.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(StaticAssert s) {
            s.exp.accept(this);
            if (s.msg != null)
                s.msg.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(EnumMember em) {
            if (em.type != null)
                this.visitType(em.type);
            if (em.value() != null)
                em.value().accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitAttribDeclaration(AttribDeclaration d) {
            if (d.decl != null)
            {
                Slice<Dsymbol> __r2868 = (d.decl).opSlice().copy();
                int __key2869 = 0;
                for (; __key2869 < __r2868.getLength();__key2869 += 1) {
                    Dsymbol de = __r2868.get(__key2869);
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
            if ((d.args != null && ((d.args).length) != 0))
                this.visitArgs(d.args, null);
            this.visitAttribDeclaration(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ConditionalDeclaration d) {
            d.condition.accept(this);
            if (d.decl != null)
            {
                Slice<Dsymbol> __r2870 = (d.decl).opSlice().copy();
                int __key2871 = 0;
                for (; __key2871 < __r2870.getLength();__key2871 += 1) {
                    Dsymbol de = __r2870.get(__key2871);
                    de.accept(this);
                }
            }
            if (d.elsedecl != null)
            {
                Slice<Dsymbol> __r2872 = (d.elsedecl).opSlice().copy();
                int __key2873 = 0;
                for (; __key2873 < __r2872.getLength();__key2873 += 1) {
                    Dsymbol de = __r2872.get(__key2873);
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
                    Slice<Statement> __r2874 = (f.frequires).opSlice().copy();
                    int __key2875 = 0;
                    for (; __key2875 < __r2874.getLength();__key2875 += 1) {
                        Statement frequire = __r2874.get(__key2875);
                        frequire.accept(this);
                    }
                }
            }
            if (f.fensures != null)
            {
                {
                    Slice<Ensure> __r2876 = (f.fensures).opSlice().copy();
                    int __key2877 = 0;
                    for (; __key2877 < __r2876.getLength();__key2877 += 1) {
                        Ensure fensure = __r2876.get(__key2877).copy();
                        fensure.ensure.accept(this);
                    }
                }
            }
            if (f.fbody != null)
            {
                f.fbody.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitBaseClasses(ClassDeclaration d) {
            if ((!(d != null) || !(((d.baseclasses).length) != 0)))
                return ;
            {
                Slice<BaseClass> __r2878 = (d.baseclasses).opSlice().copy();
                int __key2879 = 0;
                for (; __key2879 < __r2878.getLength();__key2879 += 1) {
                    BaseClass b = __r2878.get(__key2879);
                    this.visitType((b).type);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  boolean visitEponymousMember(TemplateDeclaration d) {
            if ((d.members == null || (d.members).length != 1))
                return false;
            Dsymbol onemember = (d.members).get(0);
            if (!pequals(onemember.ident, d.ident))
                return false;
            {
                FuncDeclaration fd = onemember.isFuncDeclaration();
                if (fd != null)
                {
                    assert(fd.type != null);
                    this.visitFunctionType((TypeFunction)fd.type, d);
                    if (d.constraint != null)
                        d.constraint.accept(this);
                    this.visitFuncBody(fd);
                    return true;
                }
            }
            {
                AggregateDeclaration ad = onemember.isAggregateDeclaration();
                if (ad != null)
                {
                    this.visitTemplateParameters(d.parameters);
                    if (d.constraint != null)
                        d.constraint.accept(this);
                    this.visitBaseClasses(ad.isClassDeclaration());
                    if (ad.members != null)
                    {
                        Slice<Dsymbol> __r2882 = (ad.members).opSlice().copy();
                        int __key2883 = 0;
                        for (; __key2883 < __r2882.getLength();__key2883 += 1) {
                            Dsymbol s = __r2882.get(__key2883);
                            s.accept(this);
                        }
                    }
                    return true;
                }
            }
            {
                VarDeclaration vd = onemember.isVarDeclaration();
                if (vd != null)
                {
                    if (d.constraint != null)
                        return false;
                    if (vd.type != null)
                        this.visitType(vd.type);
                    this.visitTemplateParameters(d.parameters);
                    if (vd._init != null)
                    {
                        ExpInitializer ie = vd._init.isExpInitializer();
                        if ((ie != null && ((ie.exp.op & 0xFF) == 95 || (ie.exp.op & 0xFF) == 96)))
                            ((AssignExp)ie.exp).e2.accept(this);
                        else
                            vd._init.accept(this);
                        return true;
                    }
                }
            }
            return false;
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitTemplateParameters(DArray<TemplateParameter> parameters) {
            if ((parameters == null || !(((parameters).length) != 0)))
                return ;
            {
                Slice<TemplateParameter> __r2880 = (parameters).opSlice().copy();
                int __key2881 = 0;
                for (; __key2881 < __r2880.getLength();__key2881 += 1) {
                    TemplateParameter p = __r2880.get(__key2881);
                    p.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TemplateDeclaration d) {
            if (this.visitEponymousMember(d))
                return ;
            this.visitTemplateParameters(d.parameters);
            if (d.constraint != null)
                d.constraint.accept(this);
            {
                Slice<Dsymbol> __r2884 = (d.members).opSlice().copy();
                int __key2885 = 0;
                for (; __key2885 < __r2884.getLength();__key2885 += 1) {
                    Dsymbol s = __r2884.get(__key2885);
                    s.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitObject(RootObject oarg) {
            {
                Type t = isType(oarg);
                if (t != null)
                {
                    this.visitType(t);
                }
                else {
                    Expression e = isExpression(oarg);
                    if (e != null)
                    {
                        e.accept(this);
                    }
                    else {
                        Tuple v = isTuple(oarg);
                        if (v != null)
                        {
                            DArray<RootObject> args = v.objects;
                            {
                                Slice<RootObject> __r2886 = (args).opSlice().copy();
                                int __key2887 = 0;
                                for (; __key2887 < __r2886.getLength();__key2887 += 1) {
                                    RootObject arg = __r2886.get(__key2887);
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
            if (ti.tiargs == null)
                return ;
            {
                Slice<RootObject> __r2888 = (ti.tiargs).opSlice().copy();
                int __key2889 = 0;
                for (; __key2889 < __r2888.getLength();__key2889 += 1) {
                    RootObject arg = __r2888.get(__key2889);
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
            if (d.memtype != null)
                this.visitType(d.memtype);
            if (d.members == null)
                return ;
            {
                Slice<Dsymbol> __r2890 = (d.members).opSlice().copy();
                int __key2891 = 0;
                for (; __key2891 < __r2890.getLength();__key2891 += 1) {
                    Dsymbol em = __r2890.get(__key2891);
                    if (!(em != null))
                        continue;
                    em.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(Nspace d) {
            {
                Slice<Dsymbol> __r2892 = (d.members).opSlice().copy();
                int __key2893 = 0;
                for (; __key2893 < __r2892.getLength();__key2893 += 1) {
                    Dsymbol s = __r2892.get(__key2893);
                    s.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(StructDeclaration d) {
            if (d.members == null)
                return ;
            {
                Slice<Dsymbol> __r2894 = (d.members).opSlice().copy();
                int __key2895 = 0;
                for (; __key2895 < __r2894.getLength();__key2895 += 1) {
                    Dsymbol s = __r2894.get(__key2895);
                    s.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ClassDeclaration d) {
            this.visitBaseClasses(d);
            if (d.members != null)
            {
                Slice<Dsymbol> __r2896 = (d.members).opSlice().copy();
                int __key2897 = 0;
                for (; __key2897 < __r2896.getLength();__key2897 += 1) {
                    Dsymbol s = __r2896.get(__key2897);
                    s.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(AliasDeclaration d) {
            if (d.aliassym != null)
                d.aliassym.accept(this);
            else
                this.visitType(d.type);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(VarDeclaration d) {
            this.visitVarDecl(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(FuncDeclaration f) {
            TypeFunction tf = (TypeFunction)f.type;
            this.visitType(tf);
            this.visitFuncBody(f);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(FuncLiteralDeclaration f) {
            if ((f.type.ty & 0xFF) == ENUMTY.Terror)
                return ;
            TypeFunction tf = (TypeFunction)f.type;
            if ((!(f.inferRetType) && tf.next != null))
                this.visitType(tf.next);
            this.visitParameters(tf.parameterList.parameters);
            CompoundStatement cs = f.fbody.isCompoundStatement();
            Statement s = !(cs != null) ? f.fbody : null;
            ReturnStatement rs = s != null ? s.isReturnStatement() : null;
            if ((rs != null && rs.exp != null))
                rs.exp.accept(this);
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
                Slice<Identifier> __r2899 = si.field.opSlice().copy();
                int __key2898 = 0;
                for (; __key2898 < __r2899.getLength();__key2898 += 1) {
                    Identifier id = __r2899.get(__key2898);
                    int i = __key2898;
                    {
                        Initializer iz = si.value.get(i);
                        if (iz != null)
                            iz.accept(this);
                    }
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ArrayInitializer ai) {
            {
                Slice<Expression> __r2901 = ai.index.opSlice().copy();
                int __key2900 = 0;
                for (; __key2900 < __r2901.getLength();__key2900 += 1) {
                    Expression ex = __r2901.get(__key2900);
                    int i = __key2900;
                    if (ex != null)
                        ex.accept(this);
                    {
                        Initializer iz = ai.value.get(i);
                        if (iz != null)
                            iz.accept(this);
                    }
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ExpInitializer ei) {
            ei.exp.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ArrayLiteralExp e) {
            this.visitArgs(e.elements, e.basis);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(AssocArrayLiteralExp e) {
            {
                Slice<Expression> __r2903 = (e.keys).opSlice().copy();
                int __key2902 = 0;
                for (; __key2902 < __r2903.getLength();__key2902 += 1) {
                    Expression key = __r2903.get(__key2902);
                    int i = __key2902;
                    key.accept(this);
                    (e.values).get(i).accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeExp e) {
            this.visitType(e.type);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ScopeExp e) {
            if (e.sds.isTemplateInstance() != null)
                e.sds.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(NewExp e) {
            if (e.thisexp != null)
                e.thisexp.accept(this);
            if ((e.newargs != null && ((e.newargs).length) != 0))
                this.visitArgs(e.newargs, null);
            this.visitType(e.newtype);
            if ((e.arguments != null && ((e.arguments).length) != 0))
                this.visitArgs(e.arguments, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(NewAnonClassExp e) {
            if (e.thisexp != null)
                e.thisexp.accept(this);
            if ((e.newargs != null && ((e.newargs).length) != 0))
                this.visitArgs(e.newargs, null);
            if ((e.arguments != null && ((e.arguments).length) != 0))
                this.visitArgs(e.arguments, null);
            if (e.cd != null)
                e.cd.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TupleExp e) {
            if (e.e0 != null)
                e.e0.accept(this);
            this.visitArgs(e.exps, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(FuncExp e) {
            e.fd.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(DeclarationExp e) {
            {
                VarDeclaration v = e.declaration.isVarDeclaration();
                if (v != null)
                    this.visitVarDecl(v);
                else
                    e.declaration.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeidExp e) {
            this.visitObject(e.obj);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TraitsExp e) {
            if (e.args != null)
            {
                Slice<RootObject> __r2904 = (e.args).opSlice().copy();
                int __key2905 = 0;
                for (; __key2905 < __r2904.getLength();__key2905 += 1) {
                    RootObject arg = __r2904.get(__key2905);
                    this.visitObject(arg);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(IsExp e) {
            this.visitType(e.targ);
            if (e.tspec != null)
                this.visitType(e.tspec);
            if ((e.parameters != null && ((e.parameters).length) != 0))
                this.visitTemplateParameters(e.parameters);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(UnaExp e) {
            e.e1.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(BinExp e) {
            e.e1.accept(this);
            e.e2.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CompileExp e) {
            this.visitArgs(e.exps, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ImportExp e) {
            e.e1.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(AssertExp e) {
            e.e1.accept(this);
            if (e.msg != null)
                e.msg.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(DotIdExp e) {
            e.e1.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(DotTemplateInstanceExp e) {
            e.e1.accept(this);
            e.ti.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CallExp e) {
            e.e1.accept(this);
            this.visitArgs(e.arguments, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(PtrExp e) {
            e.e1.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(DeleteExp e) {
            e.e1.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CastExp e) {
            if (e.to != null)
                this.visitType(e.to);
            e.e1.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(IntervalExp e) {
            e.lwr.accept(this);
            e.upr.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ArrayExp e) {
            e.e1.accept(this);
            this.visitArgs(e.arguments, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(PostExp e) {
            e.e1.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CondExp e) {
            e.econd.accept(this);
            e.e1.accept(this);
            e.e2.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TemplateTypeParameter tp) {
            if (tp.specType != null)
                this.visitType(tp.specType);
            if (tp.defaultType != null)
                this.visitType(tp.defaultType);
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
            this.visitType(p.type);
            if (p.defaultArg != null)
                p.defaultArg.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(Module m) {
            {
                Slice<Dsymbol> __r2906 = (m.members).opSlice().copy();
                int __key2907 = 0;
                for (; __key2907 < __r2906.getLength();__key2907 += 1) {
                    Dsymbol s = __r2906.get(__key2907);
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
                Slice<Statement> __r2908 = (s.statements).opSlice().copy();
                int __key2909 = 0;
                for (; __key2909 < __r2908.getLength();__key2909 += 1) {
                    Statement sx = __r2908.get(__key2909);
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
            if (!((e.stageflags & 32) != 0))
            {
                int old = e.stageflags;
                e.stageflags |= 32;
                {
                    Slice<Expression> __r2910 = (e.elements).opSlice().copy();
                    int __key2911 = 0;
                    for (; __key2911 < __r2910.getLength();__key2911 += 1) {
                        Expression el = __r2910.get(__key2911);
                        if (el != null)
                            el.accept(this);
                    }
                }
                e.stageflags = old;
            }
        }

        public  void visit(DotTemplateExp e) {
            e.e1.accept(this);
        }

        public  void visit(DotVarExp e) {
            e.e1.accept(this);
        }

        public  void visit(DelegateExp e) {
            if ((!(e.func.isNested()) || e.func.needThis()))
                e.e1.accept(this);
        }

        public  void visit(DotTypeExp e) {
            e.e1.accept(this);
        }

        public  void visit(VectorExp e) {
            this.visitType(e.to);
            e.e1.accept(this);
        }

        public  void visit(VectorArrayExp e) {
            e.e1.accept(this);
        }

        public  void visit(SliceExp e) {
            e.e1.accept(this);
            if (e.upr != null)
                e.upr.accept(this);
            if (e.lwr != null)
                e.lwr.accept(this);
        }

        public  void visit(ArrayLengthExp e) {
            e.e1.accept(this);
        }

        public  void visit(DelegatePtrExp e) {
            e.e1.accept(this);
        }

        public  void visit(DelegateFuncptrExp e) {
            e.e1.accept(this);
        }

        public  void visit(DotExp e) {
            e.e1.accept(this);
            e.e2.accept(this);
        }

        public  void visit(IndexExp e) {
            e.e1.accept(this);
            e.e2.accept(this);
        }

        public  void visit(RemoveExp e) {
            e.e1.accept(this);
            e.e2.accept(this);
        }


        public SemanticTimeTransitiveVisitor() {}

        public SemanticTimeTransitiveVisitor copy() {
            SemanticTimeTransitiveVisitor that = new SemanticTimeTransitiveVisitor();
            return that;
        }
    }
    public static class StoppableVisitor extends Visitor
    {
        public boolean stop;
        public  StoppableVisitor() {
        }


        public StoppableVisitor copy() {
            StoppableVisitor that = new StoppableVisitor();
            that.stop = this.stop;
            return that;
        }
    }
}
