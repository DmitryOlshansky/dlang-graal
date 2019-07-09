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
            if ((s.exp != null) && ((s.exp.op & 0xFF) == 38))
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
                Slice<Statement> __r2936 = (s.statements).opSlice().copy();
                int __key2937 = 0;
                for (; (__key2937 < __r2936.getLength());__key2937 += 1) {
                    Statement sx = __r2936.get(__key2937);
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
                if ((ie != null) && ((ie.exp.op & 0xFF) == 95) || ((ie.exp.op & 0xFF) == 96))
                    ((AssignExp)ie.exp).e2.accept(this);
                else
                    v._init.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CompoundDeclarationStatement s) {
            {
                Slice<Statement> __r2942 = (s.statements).opSlice().copy();
                int __key2943 = 0;
                for (; (__key2943 < __r2942.getLength());__key2943 += 1) {
                    Statement sx = __r2942.get(__key2943);
                    ExpStatement ds = sx != null ? sx.isExpStatement() : null;
                    if ((ds != null) && ((ds.exp.op & 0xFF) == 38))
                    {
                        Dsymbol d = ((DeclarationExp)ds.exp).declaration;
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
                Slice<Parameter> __r2944 = (s.parameters).opSlice().copy();
                int __key2945 = 0;
                for (; (__key2945 < __r2944.getLength());__key2945 += 1) {
                    Parameter p = __r2944.get(__key2945);
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
            if ((s.prm != null) && (s.prm.type != null))
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
            if ((expressions == null) || ((expressions).length == 0))
                return ;
            {
                Slice<Expression> __r2934 = (expressions).opSlice().copy();
                int __key2935 = 0;
                for (; (__key2935 < __r2934.getLength());__key2935 += 1) {
                    Expression el = __r2934.get(__key2935);
                    if (el == null)
                        el = basis;
                    if (el != null)
                        el.accept(this);
                }
            }
        }

        // defaulted all parameters starting with #2
        public  void visitArgs(DArray<Expression> expressions) {
            visitArgs(expressions, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(PragmaStatement s) {
            if ((s.args != null) && ((s.args).length != 0))
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
                Slice<Catch> __r2946 = (s.catches).opSlice().copy();
                int __key2947 = 0;
                for (; (__key2947 < __r2946.getLength());__key2947 += 1) {
                    Catch c = __r2946.get(__key2947);
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
                Slice<Dsymbol> __r2948 = (s.imports).opSlice().copy();
                int __key2949 = 0;
                for (; (__key2949 < __r2948.getLength());__key2949 += 1) {
                    Dsymbol imp = __r2948.get(__key2949);
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
            if (t == null)
                return ;
            if (((t.ty & 0xFF) == ENUMTY.Tfunction))
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
                    Slice<TemplateParameter> __r2938 = (td.origParameters).opSlice().copy();
                    int __key2939 = 0;
                    for (; (__key2939 < __r2938.getLength());__key2939 += 1) {
                        TemplateParameter p = __r2938.get(__key2939);
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
                    int __key2940 = 0;
                    int __limit2941 = dim;
                    for (; (__key2940 < __limit2941);__key2940 += 1) {
                        int i = __key2940;
                        Parameter fparam = Parameter.getNth(parameters, i, null);
                        fparam.accept(this);
                    }
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeVector t) {
            if (t.basetype == null)
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
            if (((t.next.ty & 0xFF) == ENUMTY.Tfunction))
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
                Slice<RootObject> __r2950 = t.idents.opSlice().copy();
                int __key2951 = 0;
                for (; (__key2951 < __r2950.getLength());__key2951 += 1) {
                    RootObject id = __r2950.get(__key2951);
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
                Slice<Dsymbol> __r2952 = (d.decl).opSlice().copy();
                int __key2953 = 0;
                for (; (__key2953 < __r2952.getLength());__key2953 += 1) {
                    Dsymbol de = __r2952.get(__key2953);
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
            if ((d.args != null) && ((d.args).length != 0))
                this.visitArgs(d.args, null);
            this.visitAttribDeclaration(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ConditionalDeclaration d) {
            d.condition.accept(this);
            if (d.decl != null)
            {
                Slice<Dsymbol> __r2954 = (d.decl).opSlice().copy();
                int __key2955 = 0;
                for (; (__key2955 < __r2954.getLength());__key2955 += 1) {
                    Dsymbol de = __r2954.get(__key2955);
                    de.accept(this);
                }
            }
            if (d.elsedecl != null)
            {
                Slice<Dsymbol> __r2956 = (d.elsedecl).opSlice().copy();
                int __key2957 = 0;
                for (; (__key2957 < __r2956.getLength());__key2957 += 1) {
                    Dsymbol de = __r2956.get(__key2957);
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
                    Slice<Statement> __r2958 = (f.frequires).opSlice().copy();
                    int __key2959 = 0;
                    for (; (__key2959 < __r2958.getLength());__key2959 += 1) {
                        Statement frequire = __r2958.get(__key2959);
                        frequire.accept(this);
                    }
                }
            }
            if (f.fensures != null)
            {
                {
                    Slice<Ensure> __r2960 = (f.fensures).opSlice().copy();
                    int __key2961 = 0;
                    for (; (__key2961 < __r2960.getLength());__key2961 += 1) {
                        Ensure fensure = __r2960.get(__key2961).copy();
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
            if ((d == null) || ((d.baseclasses).length == 0))
                return ;
            {
                Slice<BaseClass> __r2962 = (d.baseclasses).opSlice().copy();
                int __key2963 = 0;
                for (; (__key2963 < __r2962.getLength());__key2963 += 1) {
                    BaseClass b = __r2962.get(__key2963);
                    this.visitType((b).type);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  boolean visitEponymousMember(TemplateDeclaration d) {
            if ((d.members == null) || ((d.members).length != 1))
                return false;
            Dsymbol onemember = (d.members).get(0);
            if ((!pequals(onemember.ident, d.ident)))
                return false;
            {
                FuncDeclaration fd = onemember.isFuncDeclaration();
                if ((fd) != null)
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
                if ((ad) != null)
                {
                    this.visitTemplateParameters(d.parameters);
                    if (d.constraint != null)
                        d.constraint.accept(this);
                    this.visitBaseClasses(ad.isClassDeclaration());
                    if (ad.members != null)
                    {
                        Slice<Dsymbol> __r2966 = (ad.members).opSlice().copy();
                        int __key2967 = 0;
                        for (; (__key2967 < __r2966.getLength());__key2967 += 1) {
                            Dsymbol s = __r2966.get(__key2967);
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
                    if (d.constraint != null)
                        return false;
                    if (vd.type != null)
                        this.visitType(vd.type);
                    this.visitTemplateParameters(d.parameters);
                    if (vd._init != null)
                    {
                        ExpInitializer ie = vd._init.isExpInitializer();
                        if ((ie != null) && ((ie.exp.op & 0xFF) == 95) || ((ie.exp.op & 0xFF) == 96))
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
            if ((parameters == null) || ((parameters).length == 0))
                return ;
            {
                Slice<TemplateParameter> __r2964 = (parameters).opSlice().copy();
                int __key2965 = 0;
                for (; (__key2965 < __r2964.getLength());__key2965 += 1) {
                    TemplateParameter p = __r2964.get(__key2965);
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
                Slice<Dsymbol> __r2968 = (d.members).opSlice().copy();
                int __key2969 = 0;
                for (; (__key2969 < __r2968.getLength());__key2969 += 1) {
                    Dsymbol s = __r2968.get(__key2969);
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
                            DArray<RootObject> args = v.objects;
                            {
                                Slice<RootObject> __r2970 = (args).opSlice().copy();
                                int __key2971 = 0;
                                for (; (__key2971 < __r2970.getLength());__key2971 += 1) {
                                    RootObject arg = __r2970.get(__key2971);
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
                Slice<RootObject> __r2972 = (ti.tiargs).opSlice().copy();
                int __key2973 = 0;
                for (; (__key2973 < __r2972.getLength());__key2973 += 1) {
                    RootObject arg = __r2972.get(__key2973);
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
                Slice<Dsymbol> __r2974 = (d.members).opSlice().copy();
                int __key2975 = 0;
                for (; (__key2975 < __r2974.getLength());__key2975 += 1) {
                    Dsymbol em = __r2974.get(__key2975);
                    if (em == null)
                        continue;
                    em.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(Nspace d) {
            {
                Slice<Dsymbol> __r2976 = (d.members).opSlice().copy();
                int __key2977 = 0;
                for (; (__key2977 < __r2976.getLength());__key2977 += 1) {
                    Dsymbol s = __r2976.get(__key2977);
                    s.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(StructDeclaration d) {
            if (d.members == null)
                return ;
            {
                Slice<Dsymbol> __r2978 = (d.members).opSlice().copy();
                int __key2979 = 0;
                for (; (__key2979 < __r2978.getLength());__key2979 += 1) {
                    Dsymbol s = __r2978.get(__key2979);
                    s.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ClassDeclaration d) {
            this.visitBaseClasses(d);
            if (d.members != null)
            {
                Slice<Dsymbol> __r2980 = (d.members).opSlice().copy();
                int __key2981 = 0;
                for (; (__key2981 < __r2980.getLength());__key2981 += 1) {
                    Dsymbol s = __r2980.get(__key2981);
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
            if (((f.type.ty & 0xFF) == ENUMTY.Terror))
                return ;
            TypeFunction tf = (TypeFunction)f.type;
            if (!f.inferRetType && (tf.next != null))
                this.visitType(tf.next);
            this.visitParameters(tf.parameterList.parameters);
            CompoundStatement cs = f.fbody.isCompoundStatement();
            Statement s = cs == null ? f.fbody : null;
            ReturnStatement rs = s != null ? s.isReturnStatement() : null;
            if ((rs != null) && (rs.exp != null))
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
                Slice<Identifier> __r2983 = si.field.opSlice().copy();
                int __key2982 = 0;
                for (; (__key2982 < __r2983.getLength());__key2982 += 1) {
                    Identifier id = __r2983.get(__key2982);
                    int i = __key2982;
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
                Slice<Expression> __r2985 = ai.index.opSlice().copy();
                int __key2984 = 0;
                for (; (__key2984 < __r2985.getLength());__key2984 += 1) {
                    Expression ex = __r2985.get(__key2984);
                    int i = __key2984;
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
            ei.exp.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ArrayLiteralExp e) {
            this.visitArgs(e.elements, e.basis);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(AssocArrayLiteralExp e) {
            {
                Slice<Expression> __r2987 = (e.keys).opSlice().copy();
                int __key2986 = 0;
                for (; (__key2986 < __r2987.getLength());__key2986 += 1) {
                    Expression key = __r2987.get(__key2986);
                    int i = __key2986;
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
            if ((e.newargs != null) && ((e.newargs).length != 0))
                this.visitArgs(e.newargs, null);
            this.visitType(e.newtype);
            if ((e.arguments != null) && ((e.arguments).length != 0))
                this.visitArgs(e.arguments, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(NewAnonClassExp e) {
            if (e.thisexp != null)
                e.thisexp.accept(this);
            if ((e.newargs != null) && ((e.newargs).length != 0))
                this.visitArgs(e.newargs, null);
            if ((e.arguments != null) && ((e.arguments).length != 0))
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
                if ((v) != null)
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
                Slice<RootObject> __r2988 = (e.args).opSlice().copy();
                int __key2989 = 0;
                for (; (__key2989 < __r2988.getLength());__key2989 += 1) {
                    RootObject arg = __r2988.get(__key2989);
                    this.visitObject(arg);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(IsExp e) {
            this.visitType(e.targ);
            if (e.tspec != null)
                this.visitType(e.tspec);
            if ((e.parameters != null) && ((e.parameters).length != 0))
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
        public  void visit(dmodule.Module m) {
            {
                Slice<Dsymbol> __r2990 = (m.members).opSlice().copy();
                int __key2991 = 0;
                for (; (__key2991 < __r2990.getLength());__key2991 += 1) {
                    Dsymbol s = __r2990.get(__key2991);
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
                Slice<Statement> __r2992 = (s.statements).opSlice().copy();
                int __key2993 = 0;
                for (; (__key2993 < __r2992.getLength());__key2993 += 1) {
                    Statement sx = __r2992.get(__key2993);
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
            if ((e.stageflags & 32) == 0)
            {
                int old = e.stageflags;
                e.stageflags |= 32;
                {
                    Slice<Expression> __r2994 = (e.elements).opSlice().copy();
                    int __key2995 = 0;
                    for (; (__key2995 < __r2994.getLength());__key2995 += 1) {
                        Expression el = __r2994.get(__key2995);
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
            if (!e.func.isNested() || e.func.needThis())
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
        public boolean stop = false;
        public  StoppableVisitor() {
        }


        public StoppableVisitor copy() {
            StoppableVisitor that = new StoppableVisitor();
            that.stop = this.stop;
            return that;
        }
    }
}
