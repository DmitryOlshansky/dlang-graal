package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.astbase.*;

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

}
