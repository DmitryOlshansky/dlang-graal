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
            {
                s.exp.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CompileStatement s) {
            this.visitArgs(s.exps, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CompoundStatement s) {
            {
                Slice<Statement> __r1655 = (s.statements.get()).opSlice().copy();
                int __key1656 = 0;
                for (; (__key1656 < __r1655.getLength());__key1656 += 1) {
                    Statement sx = __r1655.get(__key1656);
                    if (sx != null)
                    {
                        sx.accept(this);
                    }
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitVarDecl(VarDeclaration v) {
            if (v.type != null)
            {
                this.visitType(v.type);
            }
            if (v._init != null)
            {
                ExpInitializer ie = v._init.isExpInitializer();
                if ((ie != null) && ((ie.exp.op & 0xFF) == 95) || ((ie.exp.op & 0xFF) == 96))
                {
                    ((AssignExp)ie.exp).e2.value.accept(this);
                }
                else
                {
                    v._init.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(CompoundDeclarationStatement s) {
            {
                Slice<Statement> __r1661 = (s.statements.get()).opSlice().copy();
                int __key1662 = 0;
                for (; (__key1662 < __r1661.getLength());__key1662 += 1) {
                    Statement sx = __r1661.get(__key1662);
                    ExpStatement ds = sx != null ? sx.isExpStatement() : null;
                    if ((ds != null) && ((ds.exp.op & 0xFF) == 38))
                    {
                        Dsymbol d = ((DeclarationExp)ds.exp).declaration;
                        assert(d.isDeclaration() != null);
                        {
                            VarDeclaration v = d.isVarDeclaration();
                            if ((v) != null)
                            {
                                this.visitVarDecl(v);
                            }
                            else
                            {
                                d.accept(this);
                            }
                        }
                    }
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ScopeStatement s) {
            if (s.statement != null)
            {
                s.statement.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(WhileStatement s) {
            s.condition.accept(this);
            if (s._body != null)
            {
                s._body.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(DoStatement s) {
            if (s._body != null)
            {
                s._body.accept(this);
            }
            s.condition.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ForStatement s) {
            if (s._init != null)
            {
                s._init.accept(this);
            }
            if (s.condition != null)
            {
                s.condition.accept(this);
            }
            if (s.increment != null)
            {
                s.increment.accept(this);
            }
            if (s._body != null)
            {
                s._body.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ForeachStatement s) {
            {
                Slice<Parameter> __r1663 = (s.parameters.get()).opSlice().copy();
                int __key1664 = 0;
                for (; (__key1664 < __r1663.getLength());__key1664 += 1) {
                    Parameter p = __r1663.get(__key1664);
                    if (p.type != null)
                    {
                        this.visitType(p.type);
                    }
                }
            }
            s.aggr.value.accept(this);
            if (s._body.value != null)
            {
                s._body.value.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ForeachRangeStatement s) {
            if (s.prm.type != null)
            {
                this.visitType(s.prm.type);
            }
            s.lwr.accept(this);
            s.upr.accept(this);
            if (s._body.value != null)
            {
                s._body.value.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(IfStatement s) {
            if ((s.prm != null) && (s.prm.type != null))
            {
                this.visitType(s.prm.type);
            }
            s.condition.accept(this);
            s.ifbody.accept(this);
            if (s.elsebody != null)
            {
                s.elsebody.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ConditionalStatement s) {
            s.condition.accept(this);
            if (s.ifbody != null)
            {
                s.ifbody.accept(this);
            }
            if (s.elsebody != null)
            {
                s.elsebody.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitArgs(Ptr<DArray<Expression>> expressions, Expression basis) {
            if ((expressions == null) || ((expressions.get()).length == 0))
            {
                return ;
            }
            {
                Slice<Expression> __r1653 = (expressions.get()).opSlice().copy();
                int __key1654 = 0;
                for (; (__key1654 < __r1653.getLength());__key1654 += 1) {
                    Expression el = __r1653.get(__key1654);
                    if (el == null)
                    {
                        el = basis;
                    }
                    if (el != null)
                    {
                        el.accept(this);
                    }
                }
            }
        }

        // defaulted all parameters starting with #2
        public  void visitArgs(Ptr<DArray<Expression>> expressions) {
            return visitArgs(expressions, null);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(PragmaStatement s) {
            if ((s.args != null) && ((s.args.get()).length != 0))
            {
                this.visitArgs(s.args, null);
            }
            if (s._body != null)
            {
                s._body.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(StaticAssertStatement s) {
            s.sa.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(SwitchStatement s) {
            s.condition.accept(this);
            if (s._body != null)
            {
                s._body.accept(this);
            }
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
            {
                s.exp.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ReturnStatement s) {
            if (s.exp != null)
            {
                s.exp.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(SynchronizedStatement s) {
            if (s.exp != null)
            {
                s.exp.accept(this);
            }
            if (s._body != null)
            {
                s._body.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(WithStatement s) {
            s.exp.accept(this);
            if (s._body != null)
            {
                s._body.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TryCatchStatement s) {
            if (s._body != null)
            {
                s._body.accept(this);
            }
            {
                Slice<Catch> __r1665 = (s.catches.get()).opSlice().copy();
                int __key1666 = 0;
                for (; (__key1666 < __r1665.getLength());__key1666 += 1) {
                    Catch c = __r1665.get(__key1666);
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
            {
                s.statement.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ImportStatement s) {
            {
                Slice<Dsymbol> __r1667 = (s.imports.get()).opSlice().copy();
                int __key1668 = 0;
                for (; (__key1668 < __r1667.getLength());__key1668 += 1) {
                    Dsymbol imp = __r1667.get(__key1668);
                    imp.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(Catch c) {
            if (c.type != null)
            {
                this.visitType(c.type);
            }
            if (c.handler != null)
            {
                c.handler.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitType(Type t) {
            if (t == null)
            {
                return ;
            }
            if (((t.ty & 0xFF) == ENUMTY.Tfunction))
            {
                this.visitFunctionType((TypeFunction)t, null);
                return ;
            }
            else
            {
                t.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitFunctionType(TypeFunction t, TemplateDeclaration td) {
            if (t.next.value != null)
            {
                this.visitType(t.next.value);
            }
            if (td != null)
            {
                {
                    Slice<TemplateParameter> __r1657 = (td.origParameters.get()).opSlice().copy();
                    int __key1658 = 0;
                    for (; (__key1658 < __r1657.getLength());__key1658 += 1) {
                        TemplateParameter p = __r1657.get(__key1658);
                        p.accept(this);
                    }
                }
            }
            this.visitParameters(t.parameterList.parameters);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitParameters(Ptr<DArray<Parameter>> parameters) {
            if (parameters != null)
            {
                int dim = Parameter.dim(parameters);
                {
                    int __key1659 = 0;
                    int __limit1660 = dim;
                    for (; (__key1659 < __limit1660);__key1659 += 1) {
                        int i = __key1659;
                        Parameter fparam = Parameter.getNth(parameters, i, null);
                        fparam.accept(this);
                    }
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeVector t) {
            if (t.basetype == null)
            {
                return ;
            }
            t.basetype.accept(this);
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
            t.index.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypePointer t) {
            if (((t.next.value.ty & 0xFF) == ENUMTY.Tfunction))
            {
                this.visitFunctionType((TypeFunction)t.next.value, null);
            }
            else
            {
                t.next.value.accept(this);
            }
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
                Slice<RootObject> __r1669 = t.idents.opSlice().copy();
                int __key1670 = 0;
                for (; (__key1670 < __r1669.getLength());__key1670 += 1) {
                    RootObject id = __r1669.get(__key1670);
                    if ((id.dyncast() == DYNCAST.dsymbol))
                    {
                        ((TemplateInstance)id).accept(this);
                    }
                    else if ((id.dyncast() == DYNCAST.expression))
                    {
                        ((Expression)id).accept(this);
                    }
                    else if ((id.dyncast() == DYNCAST.type))
                    {
                        ((Type)id).accept(this);
                    }
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
            t.next.value.accept(this);
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
            {
                s.msg.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(EnumMember em) {
            if (em.type != null)
            {
                this.visitType(em.type);
            }
            if (em.value() != null)
            {
                em.value().accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitAttribDeclaration(AttribDeclaration d) {
            if (d.decl != null)
            {
                Slice<Dsymbol> __r1671 = (d.decl.get()).opSlice().copy();
                int __key1672 = 0;
                for (; (__key1672 < __r1671.getLength());__key1672 += 1) {
                    Dsymbol de = __r1671.get(__key1672);
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
            if ((d.args != null) && ((d.args.get()).length != 0))
            {
                this.visitArgs(d.args, null);
            }
            this.visitAttribDeclaration(d);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ConditionalDeclaration d) {
            d.condition.accept(this);
            if (d.decl != null)
            {
                Slice<Dsymbol> __r1673 = (d.decl.get()).opSlice().copy();
                int __key1674 = 0;
                for (; (__key1674 < __r1673.getLength());__key1674 += 1) {
                    Dsymbol de = __r1673.get(__key1674);
                    de.accept(this);
                }
            }
            if (d.elsedecl != null)
            {
                Slice<Dsymbol> __r1675 = (d.elsedecl.get()).opSlice().copy();
                int __key1676 = 0;
                for (; (__key1676 < __r1675.getLength());__key1676 += 1) {
                    Dsymbol de = __r1675.get(__key1676);
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
                    Slice<Statement> __r1677 = (f.frequires.get()).opSlice().copy();
                    int __key1678 = 0;
                    for (; (__key1678 < __r1677.getLength());__key1678 += 1) {
                        Statement frequire = __r1677.get(__key1678);
                        frequire.accept(this);
                    }
                }
            }
            if (f.fensures != null)
            {
                {
                    Slice<Ensure> __r1679 = (f.fensures.get()).opSlice().copy();
                    int __key1680 = 0;
                    for (; (__key1680 < __r1679.getLength());__key1680 += 1) {
                        Ensure fensure = __r1679.get(__key1680).copy();
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
            if ((d == null) || ((d.baseclasses.get()).length == 0))
            {
                return ;
            }
            {
                Slice<Ptr<BaseClass>> __r1681 = (d.baseclasses.get()).opSlice().copy();
                int __key1682 = 0;
                for (; (__key1682 < __r1681.getLength());__key1682 += 1) {
                    Ptr<BaseClass> b = __r1681.get(__key1682);
                    this.visitType((b.get()).type);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  boolean visitEponymousMember(TemplateDeclaration d) {
            if ((d.members == null) || ((d.members.get()).length != 1))
            {
                return false;
            }
            Dsymbol onemember = (d.members.get()).get(0);
            if ((!pequals(onemember.ident, d.ident)))
            {
                return false;
            }
            {
                FuncDeclaration fd = onemember.isFuncDeclaration();
                if ((fd) != null)
                {
                    assert(fd.type != null);
                    this.visitFunctionType((TypeFunction)fd.type, d);
                    if (d.constraint != null)
                    {
                        d.constraint.accept(this);
                    }
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
                    {
                        d.constraint.accept(this);
                    }
                    this.visitBaseClasses(ad.isClassDeclaration());
                    if (ad.members != null)
                    {
                        Slice<Dsymbol> __r1685 = (ad.members.get()).opSlice().copy();
                        int __key1686 = 0;
                        for (; (__key1686 < __r1685.getLength());__key1686 += 1) {
                            Dsymbol s = __r1685.get(__key1686);
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
                    {
                        return false;
                    }
                    if (vd.type != null)
                    {
                        this.visitType(vd.type);
                    }
                    this.visitTemplateParameters(d.parameters);
                    if (vd._init != null)
                    {
                        ExpInitializer ie = vd._init.isExpInitializer();
                        if ((ie != null) && ((ie.exp.op & 0xFF) == 95) || ((ie.exp.op & 0xFF) == 96))
                        {
                            ((AssignExp)ie.exp).e2.value.accept(this);
                        }
                        else
                        {
                            vd._init.accept(this);
                        }
                        return true;
                    }
                }
            }
            return false;
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visitTemplateParameters(Ptr<DArray<TemplateParameter>> parameters) {
            if ((parameters == null) || ((parameters.get()).length == 0))
            {
                return ;
            }
            {
                Slice<TemplateParameter> __r1683 = (parameters.get()).opSlice().copy();
                int __key1684 = 0;
                for (; (__key1684 < __r1683.getLength());__key1684 += 1) {
                    TemplateParameter p = __r1683.get(__key1684);
                    p.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TemplateDeclaration d) {
            if (this.visitEponymousMember(d))
            {
                return ;
            }
            this.visitTemplateParameters(d.parameters);
            if (d.constraint != null)
            {
                d.constraint.accept(this);
            }
            {
                Slice<Dsymbol> __r1687 = (d.members.get()).opSlice().copy();
                int __key1688 = 0;
                for (; (__key1688 < __r1687.getLength());__key1688 += 1) {
                    Dsymbol s = __r1687.get(__key1688);
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
                                Slice<RootObject> __r1689 = (args.get()).opSlice().copy();
                                int __key1690 = 0;
                                for (; (__key1690 < __r1689.getLength());__key1690 += 1) {
                                    RootObject arg = __r1689.get(__key1690);
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
            {
                return ;
            }
            {
                Slice<RootObject> __r1691 = (ti.tiargs.get()).opSlice().copy();
                int __key1692 = 0;
                for (; (__key1692 < __r1691.getLength());__key1692 += 1) {
                    RootObject arg = __r1691.get(__key1692);
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
            {
                this.visitType(d.memtype);
            }
            if (d.members == null)
            {
                return ;
            }
            {
                Slice<Dsymbol> __r1693 = (d.members.get()).opSlice().copy();
                int __key1694 = 0;
                for (; (__key1694 < __r1693.getLength());__key1694 += 1) {
                    Dsymbol em = __r1693.get(__key1694);
                    if (em == null)
                    {
                        continue;
                    }
                    em.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(Nspace d) {
            {
                Slice<Dsymbol> __r1695 = (d.members.get()).opSlice().copy();
                int __key1696 = 0;
                for (; (__key1696 < __r1695.getLength());__key1696 += 1) {
                    Dsymbol s = __r1695.get(__key1696);
                    s.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(StructDeclaration d) {
            if (d.members == null)
            {
                return ;
            }
            {
                Slice<Dsymbol> __r1697 = (d.members.get()).opSlice().copy();
                int __key1698 = 0;
                for (; (__key1698 < __r1697.getLength());__key1698 += 1) {
                    Dsymbol s = __r1697.get(__key1698);
                    s.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ClassDeclaration d) {
            this.visitBaseClasses(d);
            if (d.members != null)
            {
                Slice<Dsymbol> __r1699 = (d.members.get()).opSlice().copy();
                int __key1700 = 0;
                for (; (__key1700 < __r1699.getLength());__key1700 += 1) {
                    Dsymbol s = __r1699.get(__key1700);
                    s.accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(AliasDeclaration d) {
            if (d.aliassym != null)
            {
                d.aliassym.accept(this);
            }
            else
            {
                this.visitType(d.type);
            }
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
            {
                return ;
            }
            TypeFunction tf = (TypeFunction)f.type;
            if (!f.inferRetType && (tf.next.value != null))
            {
                this.visitType(tf.next.value);
            }
            this.visitParameters(tf.parameterList.parameters);
            CompoundStatement cs = f.fbody.isCompoundStatement();
            Statement s = cs == null ? f.fbody : null;
            ReturnStatement rs = s != null ? s.isReturnStatement() : null;
            if ((rs != null) && (rs.exp != null))
            {
                rs.exp.accept(this);
            }
            else
            {
                this.visitFuncBody(f);
            }
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
                Slice<Identifier> __r1702 = si.field.opSlice().copy();
                int __key1701 = 0;
                for (; (__key1701 < __r1702.getLength());__key1701 += 1) {
                    Identifier id = __r1702.get(__key1701);
                    int i = __key1701;
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


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ArrayInitializer ai) {
            {
                Slice<Expression> __r1704 = ai.index.opSlice().copy();
                int __key1703 = 0;
                for (; (__key1703 < __r1704.getLength());__key1703 += 1) {
                    Expression ex = __r1704.get(__key1703);
                    int i = __key1703;
                    if (ex != null)
                    {
                        ex.accept(this);
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


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ExpInitializer ei) {
            ei.exp.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ArrayLiteralExp e) {
            this.visitArgs(e.elements, e.basis.value);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(AssocArrayLiteralExp e) {
            {
                Slice<Expression> __r1706 = (e.keys.get()).opSlice().copy();
                int __key1705 = 0;
                for (; (__key1705 < __r1706.getLength());__key1705 += 1) {
                    Expression key = __r1706.get(__key1705);
                    int i = __key1705;
                    key.accept(this);
                    (e.values.get()).get(i).accept(this);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TypeExp e) {
            this.visitType(e.type.value);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(ScopeExp e) {
            if (e.sds.isTemplateInstance() != null)
            {
                e.sds.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(NewExp e) {
            if (e.thisexp.value != null)
            {
                e.thisexp.value.accept(this);
            }
            if ((e.newargs != null) && ((e.newargs.get()).length != 0))
            {
                this.visitArgs(e.newargs, null);
            }
            this.visitType(e.newtype);
            if ((e.arguments != null) && ((e.arguments.get()).length != 0))
            {
                this.visitArgs(e.arguments, null);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(NewAnonClassExp e) {
            if (e.thisexp != null)
            {
                e.thisexp.accept(this);
            }
            if ((e.newargs != null) && ((e.newargs.get()).length != 0))
            {
                this.visitArgs(e.newargs, null);
            }
            if ((e.arguments != null) && ((e.arguments.get()).length != 0))
            {
                this.visitArgs(e.arguments, null);
            }
            if (e.cd != null)
            {
                e.cd.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TupleExp e) {
            if (e.e0.value != null)
            {
                e.e0.value.accept(this);
            }
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
                {
                    this.visitVarDecl(v);
                }
                else
                {
                    e.declaration.accept(this);
                }
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
                Slice<RootObject> __r1707 = (e.args.get()).opSlice().copy();
                int __key1708 = 0;
                for (; (__key1708 < __r1707.getLength());__key1708 += 1) {
                    RootObject arg = __r1707.get(__key1708);
                    this.visitObject(arg);
                }
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(IsExp e) {
            this.visitType(e.targ);
            if (e.tspec != null)
            {
                this.visitType(e.tspec);
            }
            if ((e.parameters != null) && ((e.parameters.get()).length != 0))
            {
                this.visitTemplateParameters(e.parameters);
            }
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
            {
                e.msg.accept(this);
            }
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
            this.visitArgs(e.arguments, null);
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
            if (e.to != null)
            {
                this.visitType(e.to);
            }
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
            this.visitArgs(e.arguments, null);
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
            {
                this.visitType(tp.specType);
            }
            if (tp.defaultType != null)
            {
                this.visitType(tp.defaultType);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TemplateThisParameter tp) {
            this.visit((TemplateTypeParameter)tp);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TemplateAliasParameter tp) {
            if (tp.specType != null)
            {
                this.visitType(tp.specType);
            }
            if (tp.specAlias != null)
            {
                this.visitObject(tp.specAlias);
            }
            if (tp.defaultAlias != null)
            {
                this.visitObject(tp.defaultAlias);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(TemplateValueParameter tp) {
            this.visitType(tp.valType);
            if (tp.specValue != null)
            {
                tp.specValue.accept(this);
            }
            if (tp.defaultValue != null)
            {
                tp.defaultValue.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(StaticIfCondition c) {
            c.exp.accept(this);
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(Parameter p) {
            this.visitType(p.type);
            if (p.defaultArg != null)
            {
                p.defaultArg.accept(this);
            }
        }


        // from template ParseVisitMethods!(ASTCodegen)
        public  void visit(dmodule.Module m) {
            {
                Slice<Dsymbol> __r1709 = (m.members.get()).opSlice().copy();
                int __key1710 = 0;
                for (; (__key1710 < __r1709.getLength());__key1710 += 1) {
                    Dsymbol s = __r1709.get(__key1710);
                    s.accept(this);
                }
            }
        }


        public  void visit(PeelStatement s) {
            if (s.s != null)
            {
                s.s.accept(this);
            }
        }

        public  void visit(UnrolledLoopStatement s) {
            {
                Slice<Statement> __r1711 = (s.statements.get()).opSlice().copy();
                int __key1712 = 0;
                for (; (__key1712 < __r1711.getLength());__key1712 += 1) {
                    Statement sx = __r1711.get(__key1712);
                    if (sx != null)
                    {
                        sx.accept(this);
                    }
                }
            }
        }

        public  void visit(DebugStatement s) {
            if (s.statement != null)
            {
                s.statement.accept(this);
            }
        }

        public  void visit(ForwardingStatement s) {
            if (s.statement != null)
            {
                s.statement.accept(this);
            }
        }

        public  void visit(StructLiteralExp e) {
            if ((e.stageflags & 32) == 0)
            {
                int old = e.stageflags;
                e.stageflags |= 32;
                {
                    Slice<Expression> __r1713 = (e.elements.get()).opSlice().copy();
                    int __key1714 = 0;
                    for (; (__key1714 < __r1713.getLength());__key1714 += 1) {
                        Expression el = __r1713.get(__key1714);
                        if (el != null)
                        {
                            el.accept(this);
                        }
                    }
                }
                e.stageflags = old;
            }
        }

        public  void visit(DotTemplateExp e) {
            e.e1.value.accept(this);
        }

        public  void visit(DotVarExp e) {
            e.e1.value.accept(this);
        }

        public  void visit(DelegateExp e) {
            if (!e.func.isNested() || e.func.needThis())
            {
                e.e1.value.accept(this);
            }
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
            {
                e.upr.value.accept(this);
            }
            if (e.lwr.value != null)
            {
                e.lwr.value.accept(this);
            }
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
