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
                this.visit(s);
            }

            public  void visit(ASTBase.Declaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.ScopeDsymbol s) {
                this.visit(s);
            }

            public  void visit(ASTBase.Import s) {
                this.visit(s);
            }

            public  void visit(ASTBase.AttribDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.StaticAssert s) {
                this.visit(s);
            }

            public  void visit(ASTBase.DebugSymbol s) {
                this.visit(s);
            }

            public  void visit(ASTBase.VersionSymbol s) {
                this.visit(s);
            }

            public  void visit(ASTBase.Package s) {
                this.visit(s);
            }

            public  void visit(ASTBase.EnumDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.AggregateDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.TemplateDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.TemplateInstance s) {
                this.visit(s);
            }

            public  void visit(ASTBase.Nspace s) {
                this.visit(s);
            }

            public  void visit(ASTBase.VarDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.FuncDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.AliasDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.TupleDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.FuncLiteralDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.PostBlitDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.CtorDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.DtorDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.InvariantDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.UnitTestDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.NewDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.DeleteDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.StaticCtorDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.StaticDtorDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.SharedStaticCtorDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.SharedStaticDtorDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.CompileDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.UserAttributeDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.LinkDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.AnonDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.AlignDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.CPPMangleDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.ProtDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.PragmaDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.StorageClassDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.ConditionalDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.StaticForeachDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.DeprecatedDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.StaticIfDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.EnumMember s) {
                this.visit(s);
            }

            public  void visit(ASTBase.Module s) {
                this.visit(s);
            }

            public  void visit(ASTBase.StructDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.UnionDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.ClassDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.InterfaceDeclaration s) {
                this.visit(s);
            }

            public  void visit(ASTBase.TemplateMixin s) {
                this.visit(s);
            }

            public  void visit(ASTBase.ImportStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.ScopeStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.ReturnStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.LabelStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.StaticAssertStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.CompileStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.WhileStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.ForStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.DoStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.ForeachRangeStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.ForeachStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.IfStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.ScopeGuardStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.ConditionalStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.StaticForeachStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.PragmaStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.SwitchStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.CaseRangeStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.CaseStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.DefaultStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.BreakStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.ContinueStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.GotoDefaultStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.GotoCaseStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.GotoStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.SynchronizedStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.WithStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.TryCatchStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.TryFinallyStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.ThrowStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.AsmStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.ExpStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.CompoundStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.CompoundDeclarationStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.CompoundAsmStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.InlineAsmStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.GccAsmStatement s) {
                this.visit(s);
            }

            public  void visit(ASTBase.TypeBasic t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeError t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeNull t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeVector t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeEnum t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeTuple t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeClass t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeStruct t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeNext t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeQualified t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeTraits t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeReference t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeSlice t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeDelegate t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypePointer t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeFunction t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeArray t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeDArray t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeAArray t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeSArray t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeIdentifier t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeReturn t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeTypeof t) {
                this.visit(t);
            }

            public  void visit(ASTBase.TypeInstance t) {
                this.visit(t);
            }

            public  void visit(ASTBase.DeclarationExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.IntegerExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.NewAnonClassExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.IsExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.RealExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.NullExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.TypeidExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.TraitsExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.StringExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.NewExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.AssocArrayLiteralExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.ArrayLiteralExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.CompileExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.FuncExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.IntervalExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.TypeExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.ScopeExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.IdentifierExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.UnaExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.DefaultInitExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.BinExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.DsymbolExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.TemplateExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.SymbolExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.TupleExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.ThisExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.VarExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.DollarExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.SuperExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.AddrExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.PreExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.PtrExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.NegExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.UAddExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.NotExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.ComExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.DeleteExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.CastExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.CallExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.DotIdExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.AssertExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.ImportExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.DotTemplateInstanceExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.ArrayExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.FuncInitExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.PrettyFuncInitExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.FileInitExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.LineInitExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.ModuleInitExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.CommaExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.PostExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.PowExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.MulExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.DivExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.ModExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.AddExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.MinExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.CatExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.ShlExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.ShrExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.UshrExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.EqualExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.InExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.IdentityExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.CmpExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.AndExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.XorExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.OrExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.LogicalExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.CondExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.AssignExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.BinAssignExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.AddAssignExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.MinAssignExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.MulAssignExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.DivAssignExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.ModAssignExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.PowAssignExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.AndAssignExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.OrAssignExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.XorAssignExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.ShlAssignExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.ShrAssignExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.UshrAssignExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.CatAssignExp e) {
                this.visit(e);
            }

            public  void visit(ASTBase.TemplateAliasParameter tp) {
                this.visit(tp);
            }

            public  void visit(ASTBase.TemplateTypeParameter tp) {
                this.visit(tp);
            }

            public  void visit(ASTBase.TemplateTupleParameter tp) {
                this.visit(tp);
            }

            public  void visit(ASTBase.TemplateValueParameter tp) {
                this.visit(tp);
            }

            public  void visit(ASTBase.TemplateThisParameter tp) {
                this.visit(tp);
            }

            public  void visit(ASTBase.StaticIfCondition c) {
                this.visit(c);
            }

            public  void visit(ASTBase.DVCondition c) {
                this.visit(c);
            }

            public  void visit(ASTBase.DebugCondition c) {
                this.visit(c);
            }

            public  void visit(ASTBase.VersionCondition c) {
                this.visit(c);
            }

            public  void visit(ASTBase.ExpInitializer i) {
                this.visit(i);
            }

            public  void visit(ASTBase.StructInitializer i) {
                this.visit(i);
            }

            public  void visit(ASTBase.ArrayInitializer i) {
                this.visit(i);
            }

            public  void visit(ASTBase.VoidInitializer i) {
                this.visit(i);
            }


            protected ParseTimeVisitor() {}

            public ParseTimeVisitor copy() {
                ParseTimeVisitor that = new ParseTimeVisitor();
                return that;
            }
        }

}
