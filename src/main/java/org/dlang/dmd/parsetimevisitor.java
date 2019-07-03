package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.astbase.*;
import static org.dlang.dmd.astcodegen.*;

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
        public  void visit(Dsymbol arg0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(Parameter arg0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(Statement arg0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(Type arg0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(Expression arg0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(TemplateParameter arg0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(Condition arg0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(Initializer arg0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(AliasThis s) {
            visit.invoke((AST.Dsymbol)s);
        }

        public  void visit(Declaration s) {
            visit.invoke((AST.Dsymbol)s);
        }

        public  void visit(ScopeDsymbol s) {
            visit.invoke((AST.Dsymbol)s);
        }

        public  void visit(Import s) {
            visit.invoke((AST.Dsymbol)s);
        }

        public  void visit(AttribDeclaration s) {
            visit.invoke((AST.Dsymbol)s);
        }

        public  void visit(StaticAssert s) {
            visit.invoke((AST.Dsymbol)s);
        }

        public  void visit(DebugSymbol s) {
            visit.invoke((AST.Dsymbol)s);
        }

        public  void visit(VersionSymbol s) {
            visit.invoke((AST.Dsymbol)s);
        }

        public  void visit(Package s) {
            visit.invoke((AST.ScopeDsymbol)s);
        }

        public  void visit(EnumDeclaration s) {
            visit.invoke((AST.ScopeDsymbol)s);
        }

        public  void visit(AggregateDeclaration s) {
            visit.invoke((AST.ScopeDsymbol)s);
        }

        public  void visit(TemplateDeclaration s) {
            visit.invoke((AST.ScopeDsymbol)s);
        }

        public  void visit(TemplateInstance s) {
            visit.invoke((AST.ScopeDsymbol)s);
        }

        public  void visit(Nspace s) {
            visit.invoke((AST.ScopeDsymbol)s);
        }

        public  void visit(VarDeclaration s) {
            visit.invoke((AST.Declaration)s);
        }

        public  void visit(FuncDeclaration s) {
            visit.invoke((AST.Declaration)s);
        }

        public  void visit(AliasDeclaration s) {
            visit.invoke((AST.Declaration)s);
        }

        public  void visit(TupleDeclaration s) {
            visit.invoke((AST.Declaration)s);
        }

        public  void visit(FuncLiteralDeclaration s) {
            visit.invoke((AST.FuncDeclaration)s);
        }

        public  void visit(PostBlitDeclaration s) {
            visit.invoke((AST.FuncDeclaration)s);
        }

        public  void visit(CtorDeclaration s) {
            visit.invoke((AST.FuncDeclaration)s);
        }

        public  void visit(DtorDeclaration s) {
            visit.invoke((AST.FuncDeclaration)s);
        }

        public  void visit(InvariantDeclaration s) {
            visit.invoke((AST.FuncDeclaration)s);
        }

        public  void visit(UnitTestDeclaration s) {
            visit.invoke((AST.FuncDeclaration)s);
        }

        public  void visit(NewDeclaration s) {
            visit.invoke((AST.FuncDeclaration)s);
        }

        public  void visit(DeleteDeclaration s) {
            visit.invoke((AST.FuncDeclaration)s);
        }

        public  void visit(StaticCtorDeclaration s) {
            visit.invoke((AST.FuncDeclaration)s);
        }

        public  void visit(StaticDtorDeclaration s) {
            visit.invoke((AST.FuncDeclaration)s);
        }

        public  void visit(SharedStaticCtorDeclaration s) {
            visit.invoke((AST.StaticCtorDeclaration)s);
        }

        public  void visit(SharedStaticDtorDeclaration s) {
            visit.invoke((AST.StaticDtorDeclaration)s);
        }

        public  void visit(CompileDeclaration s) {
            visit.invoke((AST.AttribDeclaration)s);
        }

        public  void visit(UserAttributeDeclaration s) {
            visit.invoke((AST.AttribDeclaration)s);
        }

        public  void visit(LinkDeclaration s) {
            visit.invoke((AST.AttribDeclaration)s);
        }

        public  void visit(AnonDeclaration s) {
            visit.invoke((AST.AttribDeclaration)s);
        }

        public  void visit(AlignDeclaration s) {
            visit.invoke((AST.AttribDeclaration)s);
        }

        public  void visit(CPPMangleDeclaration s) {
            visit.invoke((AST.AttribDeclaration)s);
        }

        public  void visit(CPPNamespaceDeclaration s) {
            visit.invoke((AST.AttribDeclaration)s);
        }

        public  void visit(ProtDeclaration s) {
            visit.invoke((AST.AttribDeclaration)s);
        }

        public  void visit(PragmaDeclaration s) {
            visit.invoke((AST.AttribDeclaration)s);
        }

        public  void visit(StorageClassDeclaration s) {
            visit.invoke((AST.AttribDeclaration)s);
        }

        public  void visit(ConditionalDeclaration s) {
            visit.invoke((AST.AttribDeclaration)s);
        }

        public  void visit(StaticForeachDeclaration s) {
            visit.invoke((AST.AttribDeclaration)s);
        }

        public  void visit(DeprecatedDeclaration s) {
            visit.invoke((AST.StorageClassDeclaration)s);
        }

        public  void visit(StaticIfDeclaration s) {
            visit.invoke((AST.ConditionalDeclaration)s);
        }

        public  void visit(EnumMember s) {
            visit.invoke((AST.VarDeclaration)s);
        }

        public  void visit(Module s) {
            visit.invoke((AST.Package)s);
        }

        public  void visit(StructDeclaration s) {
            visit.invoke((AST.AggregateDeclaration)s);
        }

        public  void visit(UnionDeclaration s) {
            visit.invoke((AST.StructDeclaration)s);
        }

        public  void visit(ClassDeclaration s) {
            visit.invoke((AST.AggregateDeclaration)s);
        }

        public  void visit(InterfaceDeclaration s) {
            visit.invoke((AST.ClassDeclaration)s);
        }

        public  void visit(TemplateMixin s) {
            visit.invoke((AST.TemplateInstance)s);
        }

        public  void visit(ImportStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(ScopeStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(ReturnStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(LabelStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(StaticAssertStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(CompileStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(WhileStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(ForStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(DoStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(ForeachRangeStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(ForeachStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(IfStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(ScopeGuardStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(ConditionalStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(StaticForeachStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(PragmaStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(SwitchStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(CaseRangeStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(CaseStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(DefaultStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(BreakStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(ContinueStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(GotoDefaultStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(GotoCaseStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(GotoStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(SynchronizedStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(WithStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(TryCatchStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(TryFinallyStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(ThrowStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(AsmStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(ExpStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(CompoundStatement s) {
            visit.invoke((AST.Statement)s);
        }

        public  void visit(CompoundDeclarationStatement s) {
            visit.invoke((AST.CompoundStatement)s);
        }

        public  void visit(CompoundAsmStatement s) {
            visit.invoke((AST.CompoundStatement)s);
        }

        public  void visit(InlineAsmStatement s) {
            visit.invoke((AST.AsmStatement)s);
        }

        public  void visit(GccAsmStatement s) {
            visit.invoke((AST.AsmStatement)s);
        }

        public  void visit(TypeBasic t) {
            visit.invoke((AST.Type)t);
        }

        public  void visit(TypeError t) {
            visit.invoke((AST.Type)t);
        }

        public  void visit(TypeNull t) {
            visit.invoke((AST.Type)t);
        }

        public  void visit(TypeVector t) {
            visit.invoke((AST.Type)t);
        }

        public  void visit(TypeEnum t) {
            visit.invoke((AST.Type)t);
        }

        public  void visit(TypeTuple t) {
            visit.invoke((AST.Type)t);
        }

        public  void visit(TypeClass t) {
            visit.invoke((AST.Type)t);
        }

        public  void visit(TypeStruct t) {
            visit.invoke((AST.Type)t);
        }

        public  void visit(TypeNext t) {
            visit.invoke((AST.Type)t);
        }

        public  void visit(TypeQualified t) {
            visit.invoke((AST.Type)t);
        }

        public  void visit(TypeTraits t) {
            visit.invoke((AST.Type)t);
        }

        public  void visit(TypeReference t) {
            visit.invoke((AST.TypeNext)t);
        }

        public  void visit(TypeSlice t) {
            visit.invoke((AST.TypeNext)t);
        }

        public  void visit(TypeDelegate t) {
            visit.invoke((AST.TypeNext)t);
        }

        public  void visit(TypePointer t) {
            visit.invoke((AST.TypeNext)t);
        }

        public  void visit(TypeFunction t) {
            visit.invoke((AST.TypeNext)t);
        }

        public  void visit(TypeArray t) {
            visit.invoke((AST.TypeNext)t);
        }

        public  void visit(TypeDArray t) {
            visit.invoke((AST.TypeArray)t);
        }

        public  void visit(TypeAArray t) {
            visit.invoke((AST.TypeArray)t);
        }

        public  void visit(TypeSArray t) {
            visit.invoke((AST.TypeArray)t);
        }

        public  void visit(TypeIdentifier t) {
            visit.invoke((AST.TypeQualified)t);
        }

        public  void visit(TypeReturn t) {
            visit.invoke((AST.TypeQualified)t);
        }

        public  void visit(TypeTypeof t) {
            visit.invoke((AST.TypeQualified)t);
        }

        public  void visit(TypeInstance t) {
            visit.invoke((AST.TypeQualified)t);
        }

        public  void visit(DeclarationExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(IntegerExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(NewAnonClassExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(IsExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(RealExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(NullExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(TypeidExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(TraitsExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(StringExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(NewExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(AssocArrayLiteralExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(ArrayLiteralExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(CompileExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(FuncExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(IntervalExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(TypeExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(ScopeExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(IdentifierExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(UnaExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(DefaultInitExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(BinExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(DsymbolExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(TemplateExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(SymbolExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(TupleExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(ThisExp e) {
            visit.invoke((AST.Expression)e);
        }

        public  void visit(VarExp e) {
            visit.invoke((AST.SymbolExp)e);
        }

        public  void visit(DollarExp e) {
            visit.invoke((AST.IdentifierExp)e);
        }

        public  void visit(SuperExp e) {
            visit.invoke((AST.ThisExp)e);
        }

        public  void visit(AddrExp e) {
            visit.invoke((AST.UnaExp)e);
        }

        public  void visit(PreExp e) {
            visit.invoke((AST.UnaExp)e);
        }

        public  void visit(PtrExp e) {
            visit.invoke((AST.UnaExp)e);
        }

        public  void visit(NegExp e) {
            visit.invoke((AST.UnaExp)e);
        }

        public  void visit(UAddExp e) {
            visit.invoke((AST.UnaExp)e);
        }

        public  void visit(NotExp e) {
            visit.invoke((AST.UnaExp)e);
        }

        public  void visit(ComExp e) {
            visit.invoke((AST.UnaExp)e);
        }

        public  void visit(DeleteExp e) {
            visit.invoke((AST.UnaExp)e);
        }

        public  void visit(CastExp e) {
            visit.invoke((AST.UnaExp)e);
        }

        public  void visit(CallExp e) {
            visit.invoke((AST.UnaExp)e);
        }

        public  void visit(DotIdExp e) {
            visit.invoke((AST.UnaExp)e);
        }

        public  void visit(AssertExp e) {
            visit.invoke((AST.UnaExp)e);
        }

        public  void visit(ImportExp e) {
            visit.invoke((AST.UnaExp)e);
        }

        public  void visit(DotTemplateInstanceExp e) {
            visit.invoke((AST.UnaExp)e);
        }

        public  void visit(ArrayExp e) {
            visit.invoke((AST.UnaExp)e);
        }

        public  void visit(FuncInitExp e) {
            visit.invoke((AST.DefaultInitExp)e);
        }

        public  void visit(PrettyFuncInitExp e) {
            visit.invoke((AST.DefaultInitExp)e);
        }

        public  void visit(FileInitExp e) {
            visit.invoke((AST.DefaultInitExp)e);
        }

        public  void visit(LineInitExp e) {
            visit.invoke((AST.DefaultInitExp)e);
        }

        public  void visit(ModuleInitExp e) {
            visit.invoke((AST.DefaultInitExp)e);
        }

        public  void visit(CommaExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(PostExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(PowExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(MulExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(DivExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(ModExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(AddExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(MinExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(CatExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(ShlExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(ShrExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(UshrExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(EqualExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(InExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(IdentityExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(CmpExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(AndExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(XorExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(OrExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(LogicalExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(CondExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(AssignExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(BinAssignExp e) {
            visit.invoke((AST.BinExp)e);
        }

        public  void visit(AddAssignExp e) {
            visit.invoke((AST.BinAssignExp)e);
        }

        public  void visit(MinAssignExp e) {
            visit.invoke((AST.BinAssignExp)e);
        }

        public  void visit(MulAssignExp e) {
            visit.invoke((AST.BinAssignExp)e);
        }

        public  void visit(DivAssignExp e) {
            visit.invoke((AST.BinAssignExp)e);
        }

        public  void visit(ModAssignExp e) {
            visit.invoke((AST.BinAssignExp)e);
        }

        public  void visit(PowAssignExp e) {
            visit.invoke((AST.BinAssignExp)e);
        }

        public  void visit(AndAssignExp e) {
            visit.invoke((AST.BinAssignExp)e);
        }

        public  void visit(OrAssignExp e) {
            visit.invoke((AST.BinAssignExp)e);
        }

        public  void visit(XorAssignExp e) {
            visit.invoke((AST.BinAssignExp)e);
        }

        public  void visit(ShlAssignExp e) {
            visit.invoke((AST.BinAssignExp)e);
        }

        public  void visit(ShrAssignExp e) {
            visit.invoke((AST.BinAssignExp)e);
        }

        public  void visit(UshrAssignExp e) {
            visit.invoke((AST.BinAssignExp)e);
        }

        public  void visit(CatAssignExp e) {
            visit.invoke((AST.BinAssignExp)e);
        }

        public  void visit(TemplateAliasParameter tp) {
            visit.invoke((AST.TemplateParameter)tp);
        }

        public  void visit(TemplateTypeParameter tp) {
            visit.invoke((AST.TemplateParameter)tp);
        }

        public  void visit(TemplateTupleParameter tp) {
            visit.invoke((AST.TemplateParameter)tp);
        }

        public  void visit(TemplateValueParameter tp) {
            visit.invoke((AST.TemplateParameter)tp);
        }

        public  void visit(TemplateThisParameter tp) {
            visit.invoke((AST.TemplateTypeParameter)tp);
        }

        public  void visit(StaticIfCondition c) {
            visit.invoke((AST.Condition)c);
        }

        public  void visit(DVCondition c) {
            visit.invoke((AST.Condition)c);
        }

        public  void visit(DebugCondition c) {
            visit.invoke((AST.DVCondition)c);
        }

        public  void visit(VersionCondition c) {
            visit.invoke((AST.DVCondition)c);
        }

        public  void visit(ExpInitializer i) {
            visit.invoke((AST.Initializer)i);
        }

        public  void visit(StructInitializer i) {
            visit.invoke((AST.Initializer)i);
        }

        public  void visit(ArrayInitializer i) {
            visit.invoke((AST.Initializer)i);
        }

        public  void visit(VoidInitializer i) {
            visit.invoke((AST.Initializer)i);
        }


        public ParseTimeVisitorASTCodegen() {}

        public ParseTimeVisitorASTCodegen copy() {
            ParseTimeVisitorASTCodegen that = new ParseTimeVisitorASTCodegen();
            return that;
        }
    }

}
