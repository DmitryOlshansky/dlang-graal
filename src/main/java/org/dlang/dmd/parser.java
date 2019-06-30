package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.astbase.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.parse.*;
import static org.dlang.dmd.transitivevisitor.*;

public class parser {

    public static class LispyPrint extends ParseTimeTransitiveVisitorASTBase
    {
        public OutBuffer buf;
        public  void visit(ASTBase.Dsymbol s) {
            (this.buf).printf( new ByteSlice("%s"), s.toChars());
        }

        public  void visit(ASTBase.AliasThis a) {
            super.visit(a);
        }

        public  void visit(ASTBase.Declaration d) {
            (this.buf).printf( new ByteSlice("%s"), d.toChars());
        }

        public  void visit(ASTBase.ScopeDsymbol scd) {
            (this.buf).printf( new ByteSlice("%s"), scd.toChars());
        }

        public  void visit(ASTBase.Import imp) {
            (this.buf).printf( new ByteSlice("import %s"), imp.toChars());
        }

        public  void visit(ASTBase.AttribDeclaration attr) {
            (this.buf).printf( new ByteSlice("( %s "), attr.toChars());
            super.visit(attr);
            (this.buf).printf( new ByteSlice(")"));
        }

        public  void visit(ASTBase.StaticAssert as) {
            (this.buf).printf( new ByteSlice("( static assert "));
            super.visit(as);
            (this.buf).printf( new ByteSlice(")"));
        }

        public  void visit(ASTBase.DebugSymbol sym) {
            (this.buf).printf( new ByteSlice("( debug "));
            super.visit(sym);
            (this.buf).printf( new ByteSlice(")"));
        }

        public  void visit(ASTBase.VersionSymbol ver) {
            (this.buf).printf( new ByteSlice("( version "));
            super.visit(ver);
            (this.buf).printf( new ByteSlice(")"));
        }

        public  void visit(ASTBase.VarDeclaration d) {
            (this.buf).printf( new ByteSlice("( var "));
            (this.buf).level++;
            super.visit(d);
            (this.buf).level--;
            (this.buf).printf( new ByteSlice(")"));
        }

        public  void visit(ASTBase.FuncDeclaration d) {
            (this.buf).printf( new ByteSlice("( func \n"));
            (this.buf).level++;
            super.visit(d);
            (this.buf).level--;
            (this.buf).printf( new ByteSlice(")"));
        }

        public  void visit(ASTBase.AliasDeclaration d) {
            (this.buf).printf( new ByteSlice("(alias "));
            (this.buf).level++;
            super.visit(d);
            (this.buf).level--;
            (this.buf).printf( new ByteSlice(")"));
        }

        public  void visit(ASTBase.TupleDeclaration d) {
            (this.buf).printf( new ByteSlice("( tuple "));
            (this.buf).level++;
            super.visit(d);
            (this.buf).level--;
            (this.buf).printf( new ByteSlice(")"));
        }

        public  void visit(ASTBase.FuncLiteralDeclaration d) {
            (this.buf).printf( new ByteSlice("( func literal"));
            (this.buf).level++;
            super.visit(d);
            (this.buf).level--;
            (this.buf).printf( new ByteSlice(")"));
        }

        public  void visit(ASTBase.PostBlitDeclaration d) {
            (this.buf).printf( new ByteSlice("( this(this) "));
            (this.buf).level++;
            super.visit(d);
            (this.buf).level--;
            (this.buf).printf( new ByteSlice(")"));
        }

        public  void visit(ASTBase.CtorDeclaration d) {
            (this.buf).printf( new ByteSlice("( ctor "));
            (this.buf).level++;
            super.visit(d);
            (this.buf).level--;
            (this.buf).printf( new ByteSlice(")"));
        }

        public  void visit(ASTBase.DtorDeclaration d) {
            (this.buf).printf( new ByteSlice("( dtor "));
            (this.buf).level++;
            super.visit(d);
            (this.buf).level--;
            (this.buf).printf( new ByteSlice(")"));
        }

        public  void visit(ASTBase.InvariantDeclaration d) {
            (this.buf).printf( new ByteSlice("( invariant "));
            (this.buf).level++;
            super.visit(d);
            (this.buf).level--;
            (this.buf).printf( new ByteSlice(")"));
        }

        public  void visit(ASTBase.UnitTestDeclaration d) {
            (this.buf).printf( new ByteSlice("( unittest "));
            (this.buf).level++;
            super.visit(d);
            (this.buf).level--;
            (this.buf).printf( new ByteSlice(")"));
        }

        public  void visit(ASTBase.NewDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.DeleteDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.StaticCtorDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.StaticDtorDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.SharedStaticCtorDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.SharedStaticDtorDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.Package _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.EnumDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.AggregateDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TemplateDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TemplateInstance _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.Nspace _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.CompileDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.UserAttributeDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.LinkDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.AnonDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.AlignDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.CPPMangleDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ProtDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.PragmaDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.StorageClassDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ConditionalDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.DeprecatedDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.StaticIfDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.EnumMember _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.Module _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.StructDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.UnionDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ClassDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.InterfaceDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TemplateMixin _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.Parameter _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.Statement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ImportStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ScopeStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ReturnStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.LabelStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.StaticAssertStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.CompileStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.WhileStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ForStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.DoStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ForeachRangeStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ForeachStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.IfStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ScopeGuardStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ConditionalStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.PragmaStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.SwitchStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.CaseRangeStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.CaseStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.DefaultStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.BreakStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ContinueStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.GotoDefaultStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.GotoCaseStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.GotoStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.SynchronizedStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.WithStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TryCatchStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TryFinallyStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ThrowStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.AsmStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ExpStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.CompoundStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.CompoundDeclarationStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.CompoundAsmStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.InlineAsmStatement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.Type _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeBasic _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeError _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeNull _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeVector _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeEnum _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeTuple _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeClass _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeStruct _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeNext _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeReference _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeSlice _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeDelegate _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypePointer _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeFunction _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeArray _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeDArray _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeAArray _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeSArray _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeQualified _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeTraits _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeIdentifier _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeReturn _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeTypeof _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeInstance _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.Expression _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.DeclarationExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.IntegerExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.NewAnonClassExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.IsExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.RealExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.NullExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeidExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TraitsExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.StringExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.NewExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.AssocArrayLiteralExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ArrayLiteralExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.FuncExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.IntervalExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ScopeExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.IdentifierExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.UnaExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.DefaultInitExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.BinExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.DsymbolExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TemplateExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.SymbolExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.VarExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TupleExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.DollarExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ThisExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.SuperExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.AddrExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.PreExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.PtrExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.NegExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.UAddExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.NotExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ComExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.DeleteExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.CastExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.CallExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.DotIdExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.AssertExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.CompileExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ImportExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.DotTemplateInstanceExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ArrayExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.FuncInitExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.PrettyFuncInitExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.FileInitExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.LineInitExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ModuleInitExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.CommaExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.PostExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.PowExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.MulExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.DivExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ModExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.AddExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.MinExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.CatExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ShlExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ShrExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.UshrExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.EqualExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.InExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.IdentityExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.CmpExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.AndExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.XorExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.OrExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.LogicalExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.CondExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.AssignExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.BinAssignExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.AddAssignExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.MinAssignExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.MulAssignExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.DivAssignExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ModAssignExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.PowAssignExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.AndAssignExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.OrAssignExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.XorAssignExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ShlAssignExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ShrAssignExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.UshrAssignExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.CatAssignExp _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TemplateParameter _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TemplateAliasParameter _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TemplateTypeParameter _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TemplateTupleParameter _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TemplateValueParameter _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TemplateThisParameter _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.Condition _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.StaticIfCondition _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.DVCondition _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.DebugCondition _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.VersionCondition _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.Initializer _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ExpInitializer _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.StructInitializer _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ArrayInitializer _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.VoidInitializer _param_0) {
            throw new AssertionError("Unreachable code!");
        }


        public LispyPrint() {}

        public LispyPrint copy() {
            LispyPrint that = new LispyPrint();
            that.buf = this.buf;
            return that;
        }
    }
    public static void main(Slice<ByteSlice> args) {
        StderrDiagnosticReporter diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
        ParserASTBase parser = new ParserASTBase(null, new ByteSlice(), false, diagnosticReporter);
        assert(parser != null);
        exit(0);
    }

}
