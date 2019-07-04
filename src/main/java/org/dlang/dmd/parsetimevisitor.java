package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.aliasthis.*;
import static org.dlang.dmd.astbase.*;
import static org.dlang.dmd.astcodegen.*;
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.cond.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.denum.*;
import static org.dlang.dmd.dimport.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.dversion.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.nspace.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.staticassert.*;

public class parsetimevisitor {

    // from template ParseTimeVisitor!(ASTBase)
    public static class ParseTimeVisitorASTBase
    {
        public  void visit(ASTBase.Dsymbol _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.Parameter _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.Statement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.Type _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.Expression _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TemplateParameter _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.Condition _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.Initializer _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.AliasThis s) {
            this.visit((ASTBase.Dsymbol)s);
        }

        public  void visit(ASTBase.Declaration s) {
            this.visit((ASTBase.Dsymbol)s);
        }

        public  void visit(ASTBase.ScopeDsymbol s) {
            this.visit((ASTBase.Dsymbol)s);
        }

        public  void visit(ASTBase.Import s) {
            this.visit((ASTBase.Dsymbol)s);
        }

        public  void visit(ASTBase.AttribDeclaration s) {
            this.visit((ASTBase.Dsymbol)s);
        }

        public  void visit(ASTBase.StaticAssert s) {
            this.visit((ASTBase.Dsymbol)s);
        }

        public  void visit(ASTBase.DebugSymbol s) {
            this.visit((ASTBase.Dsymbol)s);
        }

        public  void visit(ASTBase.VersionSymbol s) {
            this.visit((ASTBase.Dsymbol)s);
        }

        public  void visit(ASTBase.Package s) {
            this.visit((ASTBase.ScopeDsymbol)s);
        }

        public  void visit(ASTBase.EnumDeclaration s) {
            this.visit((ASTBase.ScopeDsymbol)s);
        }

        public  void visit(ASTBase.AggregateDeclaration s) {
            this.visit((ASTBase.ScopeDsymbol)s);
        }

        public  void visit(ASTBase.TemplateDeclaration s) {
            this.visit((ASTBase.ScopeDsymbol)s);
        }

        public  void visit(ASTBase.TemplateInstance s) {
            this.visit((ASTBase.ScopeDsymbol)s);
        }

        public  void visit(ASTBase.Nspace s) {
            this.visit((ASTBase.ScopeDsymbol)s);
        }

        public  void visit(ASTBase.VarDeclaration s) {
            this.visit((ASTBase.Declaration)s);
        }

        public  void visit(ASTBase.FuncDeclaration s) {
            this.visit((ASTBase.Declaration)s);
        }

        public  void visit(ASTBase.AliasDeclaration s) {
            this.visit((ASTBase.Declaration)s);
        }

        public  void visit(ASTBase.TupleDeclaration s) {
            this.visit((ASTBase.Declaration)s);
        }

        public  void visit(ASTBase.FuncLiteralDeclaration s) {
            this.visit((ASTBase.FuncDeclaration)s);
        }

        public  void visit(ASTBase.PostBlitDeclaration s) {
            this.visit((ASTBase.FuncDeclaration)s);
        }

        public  void visit(ASTBase.CtorDeclaration s) {
            this.visit((ASTBase.FuncDeclaration)s);
        }

        public  void visit(ASTBase.DtorDeclaration s) {
            this.visit((ASTBase.FuncDeclaration)s);
        }

        public  void visit(ASTBase.InvariantDeclaration s) {
            this.visit((ASTBase.FuncDeclaration)s);
        }

        public  void visit(ASTBase.UnitTestDeclaration s) {
            this.visit((ASTBase.FuncDeclaration)s);
        }

        public  void visit(ASTBase.NewDeclaration s) {
            this.visit((ASTBase.FuncDeclaration)s);
        }

        public  void visit(ASTBase.DeleteDeclaration s) {
            this.visit((ASTBase.FuncDeclaration)s);
        }

        public  void visit(ASTBase.StaticCtorDeclaration s) {
            this.visit((ASTBase.FuncDeclaration)s);
        }

        public  void visit(ASTBase.StaticDtorDeclaration s) {
            this.visit((ASTBase.FuncDeclaration)s);
        }

        public  void visit(ASTBase.SharedStaticCtorDeclaration s) {
            this.visit((ASTBase.StaticCtorDeclaration)s);
        }

        public  void visit(ASTBase.SharedStaticDtorDeclaration s) {
            this.visit((ASTBase.StaticDtorDeclaration)s);
        }

        public  void visit(ASTBase.CompileDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        public  void visit(ASTBase.UserAttributeDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        public  void visit(ASTBase.LinkDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        public  void visit(ASTBase.AnonDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        public  void visit(ASTBase.AlignDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        public  void visit(ASTBase.CPPMangleDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        public  void visit(ASTBase.CPPNamespaceDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        public  void visit(ASTBase.ProtDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        public  void visit(ASTBase.PragmaDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        public  void visit(ASTBase.StorageClassDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        public  void visit(ASTBase.ConditionalDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        public  void visit(ASTBase.StaticForeachDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        public  void visit(ASTBase.DeprecatedDeclaration s) {
            this.visit((ASTBase.StorageClassDeclaration)s);
        }

        public  void visit(ASTBase.StaticIfDeclaration s) {
            this.visit((ASTBase.ConditionalDeclaration)s);
        }

        public  void visit(ASTBase.EnumMember s) {
            this.visit((ASTBase.VarDeclaration)s);
        }

        public  void visit(ASTBase.Module s) {
            this.visit((ASTBase.Package)s);
        }

        public  void visit(ASTBase.StructDeclaration s) {
            this.visit((ASTBase.AggregateDeclaration)s);
        }

        public  void visit(ASTBase.UnionDeclaration s) {
            this.visit((ASTBase.StructDeclaration)s);
        }

        public  void visit(ASTBase.ClassDeclaration s) {
            this.visit((ASTBase.AggregateDeclaration)s);
        }

        public  void visit(ASTBase.InterfaceDeclaration s) {
            this.visit((ASTBase.ClassDeclaration)s);
        }

        public  void visit(ASTBase.TemplateMixin s) {
            this.visit((ASTBase.TemplateInstance)s);
        }

        public  void visit(ASTBase.ImportStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.ScopeStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.ReturnStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.LabelStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.StaticAssertStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.CompileStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.WhileStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.ForStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.DoStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.ForeachRangeStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.ForeachStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.IfStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.ScopeGuardStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.ConditionalStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.StaticForeachStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.PragmaStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.SwitchStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.CaseRangeStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.CaseStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.DefaultStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.BreakStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.ContinueStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.GotoDefaultStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.GotoCaseStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.GotoStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.SynchronizedStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.WithStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.TryCatchStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.TryFinallyStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.ThrowStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.AsmStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.ExpStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.CompoundStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        public  void visit(ASTBase.CompoundDeclarationStatement s) {
            this.visit((ASTBase.CompoundStatement)s);
        }

        public  void visit(ASTBase.CompoundAsmStatement s) {
            this.visit((ASTBase.CompoundStatement)s);
        }

        public  void visit(ASTBase.InlineAsmStatement s) {
            this.visit((ASTBase.AsmStatement)s);
        }

        public  void visit(ASTBase.GccAsmStatement s) {
            this.visit((ASTBase.AsmStatement)s);
        }

        public  void visit(ASTBase.TypeBasic t) {
            this.visit((ASTBase.Type)t);
        }

        public  void visit(ASTBase.TypeError t) {
            this.visit((ASTBase.Type)t);
        }

        public  void visit(ASTBase.TypeNull t) {
            this.visit((ASTBase.Type)t);
        }

        public  void visit(ASTBase.TypeVector t) {
            this.visit((ASTBase.Type)t);
        }

        public  void visit(ASTBase.TypeEnum t) {
            this.visit((ASTBase.Type)t);
        }

        public  void visit(ASTBase.TypeTuple t) {
            this.visit((ASTBase.Type)t);
        }

        public  void visit(ASTBase.TypeClass t) {
            this.visit((ASTBase.Type)t);
        }

        public  void visit(ASTBase.TypeStruct t) {
            this.visit((ASTBase.Type)t);
        }

        public  void visit(ASTBase.TypeNext t) {
            this.visit((ASTBase.Type)t);
        }

        public  void visit(ASTBase.TypeQualified t) {
            this.visit((ASTBase.Type)t);
        }

        public  void visit(ASTBase.TypeTraits t) {
            this.visit((ASTBase.Type)t);
        }

        public  void visit(ASTBase.TypeReference t) {
            this.visit((ASTBase.TypeNext)t);
        }

        public  void visit(ASTBase.TypeSlice t) {
            this.visit((ASTBase.TypeNext)t);
        }

        public  void visit(ASTBase.TypeDelegate t) {
            this.visit((ASTBase.TypeNext)t);
        }

        public  void visit(ASTBase.TypePointer t) {
            this.visit((ASTBase.TypeNext)t);
        }

        public  void visit(ASTBase.TypeFunction t) {
            this.visit((ASTBase.TypeNext)t);
        }

        public  void visit(ASTBase.TypeArray t) {
            this.visit((ASTBase.TypeNext)t);
        }

        public  void visit(ASTBase.TypeDArray t) {
            this.visit((ASTBase.TypeArray)t);
        }

        public  void visit(ASTBase.TypeAArray t) {
            this.visit((ASTBase.TypeArray)t);
        }

        public  void visit(ASTBase.TypeSArray t) {
            this.visit((ASTBase.TypeArray)t);
        }

        public  void visit(ASTBase.TypeIdentifier t) {
            this.visit((ASTBase.TypeQualified)t);
        }

        public  void visit(ASTBase.TypeReturn t) {
            this.visit((ASTBase.TypeQualified)t);
        }

        public  void visit(ASTBase.TypeTypeof t) {
            this.visit((ASTBase.TypeQualified)t);
        }

        public  void visit(ASTBase.TypeInstance t) {
            this.visit((ASTBase.TypeQualified)t);
        }

        public  void visit(ASTBase.DeclarationExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.IntegerExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.NewAnonClassExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.IsExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.RealExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.NullExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.TypeidExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.TraitsExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.StringExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.NewExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.AssocArrayLiteralExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.ArrayLiteralExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.CompileExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.FuncExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.IntervalExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.TypeExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.ScopeExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.IdentifierExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.UnaExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.DefaultInitExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.BinExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.DsymbolExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.TemplateExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.SymbolExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.TupleExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.ThisExp e) {
            this.visit((ASTBase.Expression)e);
        }

        public  void visit(ASTBase.VarExp e) {
            this.visit((ASTBase.SymbolExp)e);
        }

        public  void visit(ASTBase.DollarExp e) {
            this.visit((ASTBase.IdentifierExp)e);
        }

        public  void visit(ASTBase.SuperExp e) {
            this.visit((ASTBase.ThisExp)e);
        }

        public  void visit(ASTBase.AddrExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        public  void visit(ASTBase.PreExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        public  void visit(ASTBase.PtrExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        public  void visit(ASTBase.NegExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        public  void visit(ASTBase.UAddExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        public  void visit(ASTBase.NotExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        public  void visit(ASTBase.ComExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        public  void visit(ASTBase.DeleteExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        public  void visit(ASTBase.CastExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        public  void visit(ASTBase.CallExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        public  void visit(ASTBase.DotIdExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        public  void visit(ASTBase.AssertExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        public  void visit(ASTBase.ImportExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        public  void visit(ASTBase.DotTemplateInstanceExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        public  void visit(ASTBase.ArrayExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        public  void visit(ASTBase.FuncInitExp e) {
            this.visit((ASTBase.DefaultInitExp)e);
        }

        public  void visit(ASTBase.PrettyFuncInitExp e) {
            this.visit((ASTBase.DefaultInitExp)e);
        }

        public  void visit(ASTBase.FileInitExp e) {
            this.visit((ASTBase.DefaultInitExp)e);
        }

        public  void visit(ASTBase.LineInitExp e) {
            this.visit((ASTBase.DefaultInitExp)e);
        }

        public  void visit(ASTBase.ModuleInitExp e) {
            this.visit((ASTBase.DefaultInitExp)e);
        }

        public  void visit(ASTBase.CommaExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.PostExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.PowExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.MulExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.DivExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.ModExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.AddExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.MinExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.CatExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.ShlExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.ShrExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.UshrExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.EqualExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.InExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.IdentityExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.CmpExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.AndExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.XorExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.OrExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.LogicalExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.CondExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.AssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.BinAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.AddAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        public  void visit(ASTBase.MinAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        public  void visit(ASTBase.MulAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        public  void visit(ASTBase.DivAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        public  void visit(ASTBase.ModAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        public  void visit(ASTBase.PowAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        public  void visit(ASTBase.AndAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        public  void visit(ASTBase.OrAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        public  void visit(ASTBase.XorAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        public  void visit(ASTBase.ShlAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        public  void visit(ASTBase.ShrAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        public  void visit(ASTBase.UshrAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        public  void visit(ASTBase.CatAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        public  void visit(ASTBase.TemplateAliasParameter tp) {
            this.visit((ASTBase.TemplateParameter)tp);
        }

        public  void visit(ASTBase.TemplateTypeParameter tp) {
            this.visit((ASTBase.TemplateParameter)tp);
        }

        public  void visit(ASTBase.TemplateTupleParameter tp) {
            this.visit((ASTBase.TemplateParameter)tp);
        }

        public  void visit(ASTBase.TemplateValueParameter tp) {
            this.visit((ASTBase.TemplateParameter)tp);
        }

        public  void visit(ASTBase.TemplateThisParameter tp) {
            this.visit((ASTBase.TemplateTypeParameter)tp);
        }

        public  void visit(ASTBase.StaticIfCondition c) {
            this.visit((ASTBase.Condition)c);
        }

        public  void visit(ASTBase.DVCondition c) {
            this.visit((ASTBase.Condition)c);
        }

        public  void visit(ASTBase.DebugCondition c) {
            this.visit((ASTBase.DVCondition)c);
        }

        public  void visit(ASTBase.VersionCondition c) {
            this.visit((ASTBase.DVCondition)c);
        }

        public  void visit(ASTBase.ExpInitializer i) {
            this.visit((ASTBase.Initializer)i);
        }

        public  void visit(ASTBase.StructInitializer i) {
            this.visit((ASTBase.Initializer)i);
        }

        public  void visit(ASTBase.ArrayInitializer i) {
            this.visit((ASTBase.Initializer)i);
        }

        public  void visit(ASTBase.VoidInitializer i) {
            this.visit((ASTBase.Initializer)i);
        }


        public ParseTimeVisitorASTBase() {}

        public ParseTimeVisitorASTBase copy() {
            ParseTimeVisitorASTBase that = new ParseTimeVisitorASTBase();
            return that;
        }
    }

    // from template ParseTimeVisitor!(ASTCodegen)
    public static class ParseTimeVisitorASTCodegen
    {
        public  void visit(Dsymbol _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(Parameter _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(Statement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(Type _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(Expression _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(TemplateParameter _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(Condition _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(Initializer _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(AliasThis s) {
            this.visit((Dsymbol)s);
        }

        public  void visit(Declaration s) {
            this.visit((Dsymbol)s);
        }

        public  void visit(ScopeDsymbol s) {
            this.visit((Dsymbol)s);
        }

        public  void visit(Import s) {
            this.visit((Dsymbol)s);
        }

        public  void visit(AttribDeclaration s) {
            this.visit((Dsymbol)s);
        }

        public  void visit(StaticAssert s) {
            this.visit((Dsymbol)s);
        }

        public  void visit(DebugSymbol s) {
            this.visit((Dsymbol)s);
        }

        public  void visit(VersionSymbol s) {
            this.visit((Dsymbol)s);
        }

        public  void visit(Package s) {
            this.visit((ScopeDsymbol)s);
        }

        public  void visit(EnumDeclaration s) {
            this.visit((ScopeDsymbol)s);
        }

        public  void visit(AggregateDeclaration s) {
            this.visit((ScopeDsymbol)s);
        }

        public  void visit(TemplateDeclaration s) {
            this.visit((ScopeDsymbol)s);
        }

        public  void visit(TemplateInstance s) {
            this.visit((ScopeDsymbol)s);
        }

        public  void visit(Nspace s) {
            this.visit((ScopeDsymbol)s);
        }

        public  void visit(VarDeclaration s) {
            this.visit((Declaration)s);
        }

        public  void visit(FuncDeclaration s) {
            this.visit((Declaration)s);
        }

        public  void visit(AliasDeclaration s) {
            this.visit((Declaration)s);
        }

        public  void visit(TupleDeclaration s) {
            this.visit((Declaration)s);
        }

        public  void visit(FuncLiteralDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        public  void visit(PostBlitDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        public  void visit(CtorDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        public  void visit(DtorDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        public  void visit(InvariantDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        public  void visit(UnitTestDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        public  void visit(NewDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        public  void visit(DeleteDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        public  void visit(StaticCtorDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        public  void visit(StaticDtorDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        public  void visit(SharedStaticCtorDeclaration s) {
            this.visit((StaticCtorDeclaration)s);
        }

        public  void visit(SharedStaticDtorDeclaration s) {
            this.visit((StaticDtorDeclaration)s);
        }

        public  void visit(CompileDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        public  void visit(UserAttributeDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        public  void visit(LinkDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        public  void visit(AnonDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        public  void visit(AlignDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        public  void visit(CPPMangleDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        public  void visit(CPPNamespaceDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        public  void visit(ProtDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        public  void visit(PragmaDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        public  void visit(StorageClassDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        public  void visit(ConditionalDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        public  void visit(StaticForeachDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        public  void visit(DeprecatedDeclaration s) {
            this.visit((StorageClassDeclaration)s);
        }

        public  void visit(StaticIfDeclaration s) {
            this.visit((ConditionalDeclaration)s);
        }

        public  void visit(EnumMember s) {
            this.visit((VarDeclaration)s);
        }

        public  void visit(Module s) {
            this.visit((Package)s);
        }

        public  void visit(StructDeclaration s) {
            this.visit((AggregateDeclaration)s);
        }

        public  void visit(UnionDeclaration s) {
            this.visit((StructDeclaration)s);
        }

        public  void visit(ClassDeclaration s) {
            this.visit((AggregateDeclaration)s);
        }

        public  void visit(InterfaceDeclaration s) {
            this.visit((ClassDeclaration)s);
        }

        public  void visit(TemplateMixin s) {
            this.visit((TemplateInstance)s);
        }

        public  void visit(ImportStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(ScopeStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(ReturnStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(LabelStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(StaticAssertStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(CompileStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(WhileStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(ForStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(DoStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(ForeachRangeStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(ForeachStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(IfStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(ScopeGuardStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(ConditionalStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(StaticForeachStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(PragmaStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(SwitchStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(CaseRangeStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(CaseStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(DefaultStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(BreakStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(ContinueStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(GotoDefaultStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(GotoCaseStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(GotoStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(SynchronizedStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(WithStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(TryCatchStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(TryFinallyStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(ThrowStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(AsmStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(ExpStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(CompoundStatement s) {
            this.visit((Statement)s);
        }

        public  void visit(CompoundDeclarationStatement s) {
            this.visit((CompoundStatement)s);
        }

        public  void visit(CompoundAsmStatement s) {
            this.visit((CompoundStatement)s);
        }

        public  void visit(InlineAsmStatement s) {
            this.visit((AsmStatement)s);
        }

        public  void visit(GccAsmStatement s) {
            this.visit((AsmStatement)s);
        }

        public  void visit(TypeBasic t) {
            this.visit((Type)t);
        }

        public  void visit(TypeError t) {
            this.visit((Type)t);
        }

        public  void visit(TypeNull t) {
            this.visit((Type)t);
        }

        public  void visit(TypeVector t) {
            this.visit((Type)t);
        }

        public  void visit(TypeEnum t) {
            this.visit((Type)t);
        }

        public  void visit(TypeTuple t) {
            this.visit((Type)t);
        }

        public  void visit(TypeClass t) {
            this.visit((Type)t);
        }

        public  void visit(TypeStruct t) {
            this.visit((Type)t);
        }

        public  void visit(TypeNext t) {
            this.visit((Type)t);
        }

        public  void visit(TypeQualified t) {
            this.visit((Type)t);
        }

        public  void visit(TypeTraits t) {
            this.visit((Type)t);
        }

        public  void visit(TypeReference t) {
            this.visit((TypeNext)t);
        }

        public  void visit(TypeSlice t) {
            this.visit((TypeNext)t);
        }

        public  void visit(TypeDelegate t) {
            this.visit((TypeNext)t);
        }

        public  void visit(TypePointer t) {
            this.visit((TypeNext)t);
        }

        public  void visit(TypeFunction t) {
            this.visit((TypeNext)t);
        }

        public  void visit(TypeArray t) {
            this.visit((TypeNext)t);
        }

        public  void visit(TypeDArray t) {
            this.visit((TypeArray)t);
        }

        public  void visit(TypeAArray t) {
            this.visit((TypeArray)t);
        }

        public  void visit(TypeSArray t) {
            this.visit((TypeArray)t);
        }

        public  void visit(TypeIdentifier t) {
            this.visit((TypeQualified)t);
        }

        public  void visit(TypeReturn t) {
            this.visit((TypeQualified)t);
        }

        public  void visit(TypeTypeof t) {
            this.visit((TypeQualified)t);
        }

        public  void visit(TypeInstance t) {
            this.visit((TypeQualified)t);
        }

        public  void visit(DeclarationExp e) {
            this.visit((Expression)e);
        }

        public  void visit(IntegerExp e) {
            this.visit((Expression)e);
        }

        public  void visit(NewAnonClassExp e) {
            this.visit((Expression)e);
        }

        public  void visit(IsExp e) {
            this.visit((Expression)e);
        }

        public  void visit(RealExp e) {
            this.visit((Expression)e);
        }

        public  void visit(NullExp e) {
            this.visit((Expression)e);
        }

        public  void visit(TypeidExp e) {
            this.visit((Expression)e);
        }

        public  void visit(TraitsExp e) {
            this.visit((Expression)e);
        }

        public  void visit(StringExp e) {
            this.visit((Expression)e);
        }

        public  void visit(NewExp e) {
            this.visit((Expression)e);
        }

        public  void visit(AssocArrayLiteralExp e) {
            this.visit((Expression)e);
        }

        public  void visit(ArrayLiteralExp e) {
            this.visit((Expression)e);
        }

        public  void visit(CompileExp e) {
            this.visit((Expression)e);
        }

        public  void visit(FuncExp e) {
            this.visit((Expression)e);
        }

        public  void visit(IntervalExp e) {
            this.visit((Expression)e);
        }

        public  void visit(TypeExp e) {
            this.visit((Expression)e);
        }

        public  void visit(ScopeExp e) {
            this.visit((Expression)e);
        }

        public  void visit(IdentifierExp e) {
            this.visit((Expression)e);
        }

        public  void visit(UnaExp e) {
            this.visit((Expression)e);
        }

        public  void visit(DefaultInitExp e) {
            this.visit((Expression)e);
        }

        public  void visit(BinExp e) {
            this.visit((Expression)e);
        }

        public  void visit(DsymbolExp e) {
            this.visit((Expression)e);
        }

        public  void visit(TemplateExp e) {
            this.visit((Expression)e);
        }

        public  void visit(SymbolExp e) {
            this.visit((Expression)e);
        }

        public  void visit(TupleExp e) {
            this.visit((Expression)e);
        }

        public  void visit(ThisExp e) {
            this.visit((Expression)e);
        }

        public  void visit(VarExp e) {
            this.visit((SymbolExp)e);
        }

        public  void visit(DollarExp e) {
            this.visit((IdentifierExp)e);
        }

        public  void visit(SuperExp e) {
            this.visit((ThisExp)e);
        }

        public  void visit(AddrExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(PreExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(PtrExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(NegExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(UAddExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(NotExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(ComExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(DeleteExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(CastExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(CallExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(DotIdExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(AssertExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(ImportExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(DotTemplateInstanceExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(ArrayExp e) {
            this.visit((UnaExp)e);
        }

        public  void visit(FuncInitExp e) {
            this.visit((DefaultInitExp)e);
        }

        public  void visit(PrettyFuncInitExp e) {
            this.visit((DefaultInitExp)e);
        }

        public  void visit(FileInitExp e) {
            this.visit((DefaultInitExp)e);
        }

        public  void visit(LineInitExp e) {
            this.visit((DefaultInitExp)e);
        }

        public  void visit(ModuleInitExp e) {
            this.visit((DefaultInitExp)e);
        }

        public  void visit(CommaExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(PostExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(PowExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(MulExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(DivExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(ModExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(AddExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(MinExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(CatExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(ShlExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(ShrExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(UshrExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(EqualExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(InExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(IdentityExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(CmpExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(AndExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(XorExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(OrExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(LogicalExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(CondExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(AssignExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(BinAssignExp e) {
            this.visit((BinExp)e);
        }

        public  void visit(AddAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        public  void visit(MinAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        public  void visit(MulAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        public  void visit(DivAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        public  void visit(ModAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        public  void visit(PowAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        public  void visit(AndAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        public  void visit(OrAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        public  void visit(XorAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        public  void visit(ShlAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        public  void visit(ShrAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        public  void visit(UshrAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        public  void visit(CatAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        public  void visit(TemplateAliasParameter tp) {
            this.visit((TemplateParameter)tp);
        }

        public  void visit(TemplateTypeParameter tp) {
            this.visit((TemplateParameter)tp);
        }

        public  void visit(TemplateTupleParameter tp) {
            this.visit((TemplateParameter)tp);
        }

        public  void visit(TemplateValueParameter tp) {
            this.visit((TemplateParameter)tp);
        }

        public  void visit(TemplateThisParameter tp) {
            this.visit((TemplateTypeParameter)tp);
        }

        public  void visit(StaticIfCondition c) {
            this.visit((Condition)c);
        }

        public  void visit(DVCondition c) {
            this.visit((Condition)c);
        }

        public  void visit(DebugCondition c) {
            this.visit((DVCondition)c);
        }

        public  void visit(VersionCondition c) {
            this.visit((DVCondition)c);
        }

        public  void visit(ExpInitializer i) {
            this.visit((Initializer)i);
        }

        public  void visit(StructInitializer i) {
            this.visit((Initializer)i);
        }

        public  void visit(ArrayInitializer i) {
            this.visit((Initializer)i);
        }

        public  void visit(VoidInitializer i) {
            this.visit((Initializer)i);
        }


        public ParseTimeVisitorASTCodegen() {}

        public ParseTimeVisitorASTCodegen copy() {
            ParseTimeVisitorASTCodegen that = new ParseTimeVisitorASTCodegen();
            return that;
        }
    }

}
