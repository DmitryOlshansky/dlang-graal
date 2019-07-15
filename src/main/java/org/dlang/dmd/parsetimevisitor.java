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
        // Erasure: visit<Dsymbol>
        public  void visit(ASTBase.Dsymbol _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<Parameter>
        public  void visit(ASTBase.Parameter _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<Statement>
        public  void visit(ASTBase.Statement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<Type>
        public  void visit(ASTBase.Type _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<Expression>
        public  void visit(ASTBase.Expression _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<TemplateParameter>
        public  void visit(ASTBase.TemplateParameter _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<Condition>
        public  void visit(ASTBase.Condition _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<Initializer>
        public  void visit(ASTBase.Initializer _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<AliasThis>
        public  void visit(ASTBase.AliasThis s) {
            this.visit((ASTBase.Dsymbol)s);
        }

        // Erasure: visit<Declaration>
        public  void visit(ASTBase.Declaration s) {
            this.visit((ASTBase.Dsymbol)s);
        }

        // Erasure: visit<ScopeDsymbol>
        public  void visit(ASTBase.ScopeDsymbol s) {
            this.visit((ASTBase.Dsymbol)s);
        }

        // Erasure: visit<Import>
        public  void visit(ASTBase.Import s) {
            this.visit((ASTBase.Dsymbol)s);
        }

        // Erasure: visit<AttribDeclaration>
        public  void visit(ASTBase.AttribDeclaration s) {
            this.visit((ASTBase.Dsymbol)s);
        }

        // Erasure: visit<StaticAssert>
        public  void visit(ASTBase.StaticAssert s) {
            this.visit((ASTBase.Dsymbol)s);
        }

        // Erasure: visit<DebugSymbol>
        public  void visit(ASTBase.DebugSymbol s) {
            this.visit((ASTBase.Dsymbol)s);
        }

        // Erasure: visit<VersionSymbol>
        public  void visit(ASTBase.VersionSymbol s) {
            this.visit((ASTBase.Dsymbol)s);
        }

        // Erasure: visit<Package>
        public  void visit(ASTBase.Package s) {
            this.visit((ASTBase.ScopeDsymbol)s);
        }

        // Erasure: visit<EnumDeclaration>
        public  void visit(ASTBase.EnumDeclaration s) {
            this.visit((ASTBase.ScopeDsymbol)s);
        }

        // Erasure: visit<AggregateDeclaration>
        public  void visit(ASTBase.AggregateDeclaration s) {
            this.visit((ASTBase.ScopeDsymbol)s);
        }

        // Erasure: visit<TemplateDeclaration>
        public  void visit(ASTBase.TemplateDeclaration s) {
            this.visit((ASTBase.ScopeDsymbol)s);
        }

        // Erasure: visit<TemplateInstance>
        public  void visit(ASTBase.TemplateInstance s) {
            this.visit((ASTBase.ScopeDsymbol)s);
        }

        // Erasure: visit<Nspace>
        public  void visit(ASTBase.Nspace s) {
            this.visit((ASTBase.ScopeDsymbol)s);
        }

        // Erasure: visit<VarDeclaration>
        public  void visit(ASTBase.VarDeclaration s) {
            this.visit((ASTBase.Declaration)s);
        }

        // Erasure: visit<FuncDeclaration>
        public  void visit(ASTBase.FuncDeclaration s) {
            this.visit((ASTBase.Declaration)s);
        }

        // Erasure: visit<AliasDeclaration>
        public  void visit(ASTBase.AliasDeclaration s) {
            this.visit((ASTBase.Declaration)s);
        }

        // Erasure: visit<TupleDeclaration>
        public  void visit(ASTBase.TupleDeclaration s) {
            this.visit((ASTBase.Declaration)s);
        }

        // Erasure: visit<FuncLiteralDeclaration>
        public  void visit(ASTBase.FuncLiteralDeclaration s) {
            this.visit((ASTBase.FuncDeclaration)s);
        }

        // Erasure: visit<PostBlitDeclaration>
        public  void visit(ASTBase.PostBlitDeclaration s) {
            this.visit((ASTBase.FuncDeclaration)s);
        }

        // Erasure: visit<CtorDeclaration>
        public  void visit(ASTBase.CtorDeclaration s) {
            this.visit((ASTBase.FuncDeclaration)s);
        }

        // Erasure: visit<DtorDeclaration>
        public  void visit(ASTBase.DtorDeclaration s) {
            this.visit((ASTBase.FuncDeclaration)s);
        }

        // Erasure: visit<InvariantDeclaration>
        public  void visit(ASTBase.InvariantDeclaration s) {
            this.visit((ASTBase.FuncDeclaration)s);
        }

        // Erasure: visit<UnitTestDeclaration>
        public  void visit(ASTBase.UnitTestDeclaration s) {
            this.visit((ASTBase.FuncDeclaration)s);
        }

        // Erasure: visit<NewDeclaration>
        public  void visit(ASTBase.NewDeclaration s) {
            this.visit((ASTBase.FuncDeclaration)s);
        }

        // Erasure: visit<DeleteDeclaration>
        public  void visit(ASTBase.DeleteDeclaration s) {
            this.visit((ASTBase.FuncDeclaration)s);
        }

        // Erasure: visit<StaticCtorDeclaration>
        public  void visit(ASTBase.StaticCtorDeclaration s) {
            this.visit((ASTBase.FuncDeclaration)s);
        }

        // Erasure: visit<StaticDtorDeclaration>
        public  void visit(ASTBase.StaticDtorDeclaration s) {
            this.visit((ASTBase.FuncDeclaration)s);
        }

        // Erasure: visit<SharedStaticCtorDeclaration>
        public  void visit(ASTBase.SharedStaticCtorDeclaration s) {
            this.visit((ASTBase.StaticCtorDeclaration)s);
        }

        // Erasure: visit<SharedStaticDtorDeclaration>
        public  void visit(ASTBase.SharedStaticDtorDeclaration s) {
            this.visit((ASTBase.StaticDtorDeclaration)s);
        }

        // Erasure: visit<CompileDeclaration>
        public  void visit(ASTBase.CompileDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        // Erasure: visit<UserAttributeDeclaration>
        public  void visit(ASTBase.UserAttributeDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        // Erasure: visit<LinkDeclaration>
        public  void visit(ASTBase.LinkDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        // Erasure: visit<AnonDeclaration>
        public  void visit(ASTBase.AnonDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        // Erasure: visit<AlignDeclaration>
        public  void visit(ASTBase.AlignDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        // Erasure: visit<CPPMangleDeclaration>
        public  void visit(ASTBase.CPPMangleDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        // Erasure: visit<CPPNamespaceDeclaration>
        public  void visit(ASTBase.CPPNamespaceDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        // Erasure: visit<ProtDeclaration>
        public  void visit(ASTBase.ProtDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        // Erasure: visit<PragmaDeclaration>
        public  void visit(ASTBase.PragmaDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        // Erasure: visit<StorageClassDeclaration>
        public  void visit(ASTBase.StorageClassDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        // Erasure: visit<ConditionalDeclaration>
        public  void visit(ASTBase.ConditionalDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        // Erasure: visit<StaticForeachDeclaration>
        public  void visit(ASTBase.StaticForeachDeclaration s) {
            this.visit((ASTBase.AttribDeclaration)s);
        }

        // Erasure: visit<DeprecatedDeclaration>
        public  void visit(ASTBase.DeprecatedDeclaration s) {
            this.visit((ASTBase.StorageClassDeclaration)s);
        }

        // Erasure: visit<StaticIfDeclaration>
        public  void visit(ASTBase.StaticIfDeclaration s) {
            this.visit((ASTBase.ConditionalDeclaration)s);
        }

        // Erasure: visit<EnumMember>
        public  void visit(ASTBase.EnumMember s) {
            this.visit((ASTBase.VarDeclaration)s);
        }

        // Erasure: visit<Module>
        public  void visit(ASTBase.Module s) {
            this.visit((ASTBase.Package)s);
        }

        // Erasure: visit<StructDeclaration>
        public  void visit(ASTBase.StructDeclaration s) {
            this.visit((ASTBase.AggregateDeclaration)s);
        }

        // Erasure: visit<UnionDeclaration>
        public  void visit(ASTBase.UnionDeclaration s) {
            this.visit((ASTBase.StructDeclaration)s);
        }

        // Erasure: visit<ClassDeclaration>
        public  void visit(ASTBase.ClassDeclaration s) {
            this.visit((ASTBase.AggregateDeclaration)s);
        }

        // Erasure: visit<InterfaceDeclaration>
        public  void visit(ASTBase.InterfaceDeclaration s) {
            this.visit((ASTBase.ClassDeclaration)s);
        }

        // Erasure: visit<TemplateMixin>
        public  void visit(ASTBase.TemplateMixin s) {
            this.visit((ASTBase.TemplateInstance)s);
        }

        // Erasure: visit<ImportStatement>
        public  void visit(ASTBase.ImportStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<ScopeStatement>
        public  void visit(ASTBase.ScopeStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<ReturnStatement>
        public  void visit(ASTBase.ReturnStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<LabelStatement>
        public  void visit(ASTBase.LabelStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<StaticAssertStatement>
        public  void visit(ASTBase.StaticAssertStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<CompileStatement>
        public  void visit(ASTBase.CompileStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<WhileStatement>
        public  void visit(ASTBase.WhileStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<ForStatement>
        public  void visit(ASTBase.ForStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<DoStatement>
        public  void visit(ASTBase.DoStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<ForeachRangeStatement>
        public  void visit(ASTBase.ForeachRangeStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<ForeachStatement>
        public  void visit(ASTBase.ForeachStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<IfStatement>
        public  void visit(ASTBase.IfStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<ScopeGuardStatement>
        public  void visit(ASTBase.ScopeGuardStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<ConditionalStatement>
        public  void visit(ASTBase.ConditionalStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<StaticForeachStatement>
        public  void visit(ASTBase.StaticForeachStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<PragmaStatement>
        public  void visit(ASTBase.PragmaStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<SwitchStatement>
        public  void visit(ASTBase.SwitchStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<CaseRangeStatement>
        public  void visit(ASTBase.CaseRangeStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<CaseStatement>
        public  void visit(ASTBase.CaseStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<DefaultStatement>
        public  void visit(ASTBase.DefaultStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<BreakStatement>
        public  void visit(ASTBase.BreakStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<ContinueStatement>
        public  void visit(ASTBase.ContinueStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<GotoDefaultStatement>
        public  void visit(ASTBase.GotoDefaultStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<GotoCaseStatement>
        public  void visit(ASTBase.GotoCaseStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<GotoStatement>
        public  void visit(ASTBase.GotoStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<SynchronizedStatement>
        public  void visit(ASTBase.SynchronizedStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<WithStatement>
        public  void visit(ASTBase.WithStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<TryCatchStatement>
        public  void visit(ASTBase.TryCatchStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<TryFinallyStatement>
        public  void visit(ASTBase.TryFinallyStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<ThrowStatement>
        public  void visit(ASTBase.ThrowStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<AsmStatement>
        public  void visit(ASTBase.AsmStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<ExpStatement>
        public  void visit(ASTBase.ExpStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<CompoundStatement>
        public  void visit(ASTBase.CompoundStatement s) {
            this.visit((ASTBase.Statement)s);
        }

        // Erasure: visit<CompoundDeclarationStatement>
        public  void visit(ASTBase.CompoundDeclarationStatement s) {
            this.visit((ASTBase.CompoundStatement)s);
        }

        // Erasure: visit<CompoundAsmStatement>
        public  void visit(ASTBase.CompoundAsmStatement s) {
            this.visit((ASTBase.CompoundStatement)s);
        }

        // Erasure: visit<InlineAsmStatement>
        public  void visit(ASTBase.InlineAsmStatement s) {
            this.visit((ASTBase.AsmStatement)s);
        }

        // Erasure: visit<GccAsmStatement>
        public  void visit(ASTBase.GccAsmStatement s) {
            this.visit((ASTBase.AsmStatement)s);
        }

        // Erasure: visit<TypeBasic>
        public  void visit(ASTBase.TypeBasic t) {
            this.visit((ASTBase.Type)t);
        }

        // Erasure: visit<TypeError>
        public  void visit(ASTBase.TypeError t) {
            this.visit((ASTBase.Type)t);
        }

        // Erasure: visit<TypeNull>
        public  void visit(ASTBase.TypeNull t) {
            this.visit((ASTBase.Type)t);
        }

        // Erasure: visit<TypeVector>
        public  void visit(ASTBase.TypeVector t) {
            this.visit((ASTBase.Type)t);
        }

        // Erasure: visit<TypeEnum>
        public  void visit(ASTBase.TypeEnum t) {
            this.visit((ASTBase.Type)t);
        }

        // Erasure: visit<TypeTuple>
        public  void visit(ASTBase.TypeTuple t) {
            this.visit((ASTBase.Type)t);
        }

        // Erasure: visit<TypeClass>
        public  void visit(ASTBase.TypeClass t) {
            this.visit((ASTBase.Type)t);
        }

        // Erasure: visit<TypeStruct>
        public  void visit(ASTBase.TypeStruct t) {
            this.visit((ASTBase.Type)t);
        }

        // Erasure: visit<TypeNext>
        public  void visit(ASTBase.TypeNext t) {
            this.visit((ASTBase.Type)t);
        }

        // Erasure: visit<TypeQualified>
        public  void visit(ASTBase.TypeQualified t) {
            this.visit((ASTBase.Type)t);
        }

        // Erasure: visit<TypeTraits>
        public  void visit(ASTBase.TypeTraits t) {
            this.visit((ASTBase.Type)t);
        }

        // Erasure: visit<TypeReference>
        public  void visit(ASTBase.TypeReference t) {
            this.visit((ASTBase.TypeNext)t);
        }

        // Erasure: visit<TypeSlice>
        public  void visit(ASTBase.TypeSlice t) {
            this.visit((ASTBase.TypeNext)t);
        }

        // Erasure: visit<TypeDelegate>
        public  void visit(ASTBase.TypeDelegate t) {
            this.visit((ASTBase.TypeNext)t);
        }

        // Erasure: visit<TypePointer>
        public  void visit(ASTBase.TypePointer t) {
            this.visit((ASTBase.TypeNext)t);
        }

        // Erasure: visit<TypeFunction>
        public  void visit(ASTBase.TypeFunction t) {
            this.visit((ASTBase.TypeNext)t);
        }

        // Erasure: visit<TypeArray>
        public  void visit(ASTBase.TypeArray t) {
            this.visit((ASTBase.TypeNext)t);
        }

        // Erasure: visit<TypeDArray>
        public  void visit(ASTBase.TypeDArray t) {
            this.visit((ASTBase.TypeArray)t);
        }

        // Erasure: visit<TypeAArray>
        public  void visit(ASTBase.TypeAArray t) {
            this.visit((ASTBase.TypeArray)t);
        }

        // Erasure: visit<TypeSArray>
        public  void visit(ASTBase.TypeSArray t) {
            this.visit((ASTBase.TypeArray)t);
        }

        // Erasure: visit<TypeIdentifier>
        public  void visit(ASTBase.TypeIdentifier t) {
            this.visit((ASTBase.TypeQualified)t);
        }

        // Erasure: visit<TypeReturn>
        public  void visit(ASTBase.TypeReturn t) {
            this.visit((ASTBase.TypeQualified)t);
        }

        // Erasure: visit<TypeTypeof>
        public  void visit(ASTBase.TypeTypeof t) {
            this.visit((ASTBase.TypeQualified)t);
        }

        // Erasure: visit<TypeInstance>
        public  void visit(ASTBase.TypeInstance t) {
            this.visit((ASTBase.TypeQualified)t);
        }

        // Erasure: visit<DeclarationExp>
        public  void visit(ASTBase.DeclarationExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<IntegerExp>
        public  void visit(ASTBase.IntegerExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<NewAnonClassExp>
        public  void visit(ASTBase.NewAnonClassExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<IsExp>
        public  void visit(ASTBase.IsExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<RealExp>
        public  void visit(ASTBase.RealExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<NullExp>
        public  void visit(ASTBase.NullExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<TypeidExp>
        public  void visit(ASTBase.TypeidExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<TraitsExp>
        public  void visit(ASTBase.TraitsExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<StringExp>
        public  void visit(ASTBase.StringExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<NewExp>
        public  void visit(ASTBase.NewExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<AssocArrayLiteralExp>
        public  void visit(ASTBase.AssocArrayLiteralExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<ArrayLiteralExp>
        public  void visit(ASTBase.ArrayLiteralExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<CompileExp>
        public  void visit(ASTBase.CompileExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<FuncExp>
        public  void visit(ASTBase.FuncExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<IntervalExp>
        public  void visit(ASTBase.IntervalExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<TypeExp>
        public  void visit(ASTBase.TypeExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<ScopeExp>
        public  void visit(ASTBase.ScopeExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<IdentifierExp>
        public  void visit(ASTBase.IdentifierExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<UnaExp>
        public  void visit(ASTBase.UnaExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<DefaultInitExp>
        public  void visit(ASTBase.DefaultInitExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<BinExp>
        public  void visit(ASTBase.BinExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<DsymbolExp>
        public  void visit(ASTBase.DsymbolExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<TemplateExp>
        public  void visit(ASTBase.TemplateExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<SymbolExp>
        public  void visit(ASTBase.SymbolExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<TupleExp>
        public  void visit(ASTBase.TupleExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<ThisExp>
        public  void visit(ASTBase.ThisExp e) {
            this.visit((ASTBase.Expression)e);
        }

        // Erasure: visit<VarExp>
        public  void visit(ASTBase.VarExp e) {
            this.visit((ASTBase.SymbolExp)e);
        }

        // Erasure: visit<DollarExp>
        public  void visit(ASTBase.DollarExp e) {
            this.visit((ASTBase.IdentifierExp)e);
        }

        // Erasure: visit<SuperExp>
        public  void visit(ASTBase.SuperExp e) {
            this.visit((ASTBase.ThisExp)e);
        }

        // Erasure: visit<AddrExp>
        public  void visit(ASTBase.AddrExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        // Erasure: visit<PreExp>
        public  void visit(ASTBase.PreExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        // Erasure: visit<PtrExp>
        public  void visit(ASTBase.PtrExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        // Erasure: visit<NegExp>
        public  void visit(ASTBase.NegExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        // Erasure: visit<UAddExp>
        public  void visit(ASTBase.UAddExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        // Erasure: visit<NotExp>
        public  void visit(ASTBase.NotExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        // Erasure: visit<ComExp>
        public  void visit(ASTBase.ComExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        // Erasure: visit<DeleteExp>
        public  void visit(ASTBase.DeleteExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        // Erasure: visit<CastExp>
        public  void visit(ASTBase.CastExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        // Erasure: visit<CallExp>
        public  void visit(ASTBase.CallExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        // Erasure: visit<DotIdExp>
        public  void visit(ASTBase.DotIdExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        // Erasure: visit<AssertExp>
        public  void visit(ASTBase.AssertExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        // Erasure: visit<ImportExp>
        public  void visit(ASTBase.ImportExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        // Erasure: visit<DotTemplateInstanceExp>
        public  void visit(ASTBase.DotTemplateInstanceExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        // Erasure: visit<ArrayExp>
        public  void visit(ASTBase.ArrayExp e) {
            this.visit((ASTBase.UnaExp)e);
        }

        // Erasure: visit<FuncInitExp>
        public  void visit(ASTBase.FuncInitExp e) {
            this.visit((ASTBase.DefaultInitExp)e);
        }

        // Erasure: visit<PrettyFuncInitExp>
        public  void visit(ASTBase.PrettyFuncInitExp e) {
            this.visit((ASTBase.DefaultInitExp)e);
        }

        // Erasure: visit<FileInitExp>
        public  void visit(ASTBase.FileInitExp e) {
            this.visit((ASTBase.DefaultInitExp)e);
        }

        // Erasure: visit<LineInitExp>
        public  void visit(ASTBase.LineInitExp e) {
            this.visit((ASTBase.DefaultInitExp)e);
        }

        // Erasure: visit<ModuleInitExp>
        public  void visit(ASTBase.ModuleInitExp e) {
            this.visit((ASTBase.DefaultInitExp)e);
        }

        // Erasure: visit<CommaExp>
        public  void visit(ASTBase.CommaExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<PostExp>
        public  void visit(ASTBase.PostExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<PowExp>
        public  void visit(ASTBase.PowExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<MulExp>
        public  void visit(ASTBase.MulExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<DivExp>
        public  void visit(ASTBase.DivExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<ModExp>
        public  void visit(ASTBase.ModExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<AddExp>
        public  void visit(ASTBase.AddExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<MinExp>
        public  void visit(ASTBase.MinExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<CatExp>
        public  void visit(ASTBase.CatExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<ShlExp>
        public  void visit(ASTBase.ShlExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<ShrExp>
        public  void visit(ASTBase.ShrExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<UshrExp>
        public  void visit(ASTBase.UshrExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<EqualExp>
        public  void visit(ASTBase.EqualExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<InExp>
        public  void visit(ASTBase.InExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<IdentityExp>
        public  void visit(ASTBase.IdentityExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<CmpExp>
        public  void visit(ASTBase.CmpExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<AndExp>
        public  void visit(ASTBase.AndExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<XorExp>
        public  void visit(ASTBase.XorExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<OrExp>
        public  void visit(ASTBase.OrExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<LogicalExp>
        public  void visit(ASTBase.LogicalExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<CondExp>
        public  void visit(ASTBase.CondExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<AssignExp>
        public  void visit(ASTBase.AssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<BinAssignExp>
        public  void visit(ASTBase.BinAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<AddAssignExp>
        public  void visit(ASTBase.AddAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        // Erasure: visit<MinAssignExp>
        public  void visit(ASTBase.MinAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        // Erasure: visit<MulAssignExp>
        public  void visit(ASTBase.MulAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        // Erasure: visit<DivAssignExp>
        public  void visit(ASTBase.DivAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        // Erasure: visit<ModAssignExp>
        public  void visit(ASTBase.ModAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        // Erasure: visit<PowAssignExp>
        public  void visit(ASTBase.PowAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        // Erasure: visit<AndAssignExp>
        public  void visit(ASTBase.AndAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        // Erasure: visit<OrAssignExp>
        public  void visit(ASTBase.OrAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        // Erasure: visit<XorAssignExp>
        public  void visit(ASTBase.XorAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        // Erasure: visit<ShlAssignExp>
        public  void visit(ASTBase.ShlAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        // Erasure: visit<ShrAssignExp>
        public  void visit(ASTBase.ShrAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        // Erasure: visit<UshrAssignExp>
        public  void visit(ASTBase.UshrAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        // Erasure: visit<CatAssignExp>
        public  void visit(ASTBase.CatAssignExp e) {
            this.visit((ASTBase.BinAssignExp)e);
        }

        // Erasure: visit<TemplateAliasParameter>
        public  void visit(ASTBase.TemplateAliasParameter tp) {
            this.visit((ASTBase.TemplateParameter)tp);
        }

        // Erasure: visit<TemplateTypeParameter>
        public  void visit(ASTBase.TemplateTypeParameter tp) {
            this.visit((ASTBase.TemplateParameter)tp);
        }

        // Erasure: visit<TemplateTupleParameter>
        public  void visit(ASTBase.TemplateTupleParameter tp) {
            this.visit((ASTBase.TemplateParameter)tp);
        }

        // Erasure: visit<TemplateValueParameter>
        public  void visit(ASTBase.TemplateValueParameter tp) {
            this.visit((ASTBase.TemplateParameter)tp);
        }

        // Erasure: visit<TemplateThisParameter>
        public  void visit(ASTBase.TemplateThisParameter tp) {
            this.visit((ASTBase.TemplateTypeParameter)tp);
        }

        // Erasure: visit<StaticIfCondition>
        public  void visit(ASTBase.StaticIfCondition c) {
            this.visit((ASTBase.Condition)c);
        }

        // Erasure: visit<DVCondition>
        public  void visit(ASTBase.DVCondition c) {
            this.visit((ASTBase.Condition)c);
        }

        // Erasure: visit<DebugCondition>
        public  void visit(ASTBase.DebugCondition c) {
            this.visit((ASTBase.DVCondition)c);
        }

        // Erasure: visit<VersionCondition>
        public  void visit(ASTBase.VersionCondition c) {
            this.visit((ASTBase.DVCondition)c);
        }

        // Erasure: visit<ExpInitializer>
        public  void visit(ASTBase.ExpInitializer i) {
            this.visit((ASTBase.Initializer)i);
        }

        // Erasure: visit<StructInitializer>
        public  void visit(ASTBase.StructInitializer i) {
            this.visit((ASTBase.Initializer)i);
        }

        // Erasure: visit<ArrayInitializer>
        public  void visit(ASTBase.ArrayInitializer i) {
            this.visit((ASTBase.Initializer)i);
        }

        // Erasure: visit<VoidInitializer>
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
        // Erasure: visit<Dsymbol>
        public  void visit(Dsymbol _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<Parameter>
        public  void visit(Parameter _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<Statement>
        public  void visit(Statement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<Type>
        public  void visit(Type _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<Expression>
        public  void visit(Expression _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<TemplateParameter>
        public  void visit(TemplateParameter _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<Condition>
        public  void visit(Condition _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<Initializer>
        public  void visit(Initializer _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<AliasThis>
        public  void visit(AliasThis s) {
            this.visit((Dsymbol)s);
        }

        // Erasure: visit<Declaration>
        public  void visit(Declaration s) {
            this.visit((Dsymbol)s);
        }

        // Erasure: visit<ScopeDsymbol>
        public  void visit(ScopeDsymbol s) {
            this.visit((Dsymbol)s);
        }

        // Erasure: visit<Import>
        public  void visit(Import s) {
            this.visit((Dsymbol)s);
        }

        // Erasure: visit<AttribDeclaration>
        public  void visit(AttribDeclaration s) {
            this.visit((Dsymbol)s);
        }

        // Erasure: visit<StaticAssert>
        public  void visit(StaticAssert s) {
            this.visit((Dsymbol)s);
        }

        // Erasure: visit<DebugSymbol>
        public  void visit(DebugSymbol s) {
            this.visit((Dsymbol)s);
        }

        // Erasure: visit<VersionSymbol>
        public  void visit(VersionSymbol s) {
            this.visit((Dsymbol)s);
        }

        // Erasure: visit<Package>
        public  void visit(dmodule.Package s) {
            this.visit((ScopeDsymbol)s);
        }

        // Erasure: visit<EnumDeclaration>
        public  void visit(EnumDeclaration s) {
            this.visit((ScopeDsymbol)s);
        }

        // Erasure: visit<AggregateDeclaration>
        public  void visit(AggregateDeclaration s) {
            this.visit((ScopeDsymbol)s);
        }

        // Erasure: visit<TemplateDeclaration>
        public  void visit(TemplateDeclaration s) {
            this.visit((ScopeDsymbol)s);
        }

        // Erasure: visit<TemplateInstance>
        public  void visit(TemplateInstance s) {
            this.visit((ScopeDsymbol)s);
        }

        // Erasure: visit<Nspace>
        public  void visit(Nspace s) {
            this.visit((ScopeDsymbol)s);
        }

        // Erasure: visit<VarDeclaration>
        public  void visit(VarDeclaration s) {
            this.visit((Declaration)s);
        }

        // Erasure: visit<FuncDeclaration>
        public  void visit(FuncDeclaration s) {
            this.visit((Declaration)s);
        }

        // Erasure: visit<AliasDeclaration>
        public  void visit(AliasDeclaration s) {
            this.visit((Declaration)s);
        }

        // Erasure: visit<TupleDeclaration>
        public  void visit(TupleDeclaration s) {
            this.visit((Declaration)s);
        }

        // Erasure: visit<FuncLiteralDeclaration>
        public  void visit(FuncLiteralDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        // Erasure: visit<PostBlitDeclaration>
        public  void visit(PostBlitDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        // Erasure: visit<CtorDeclaration>
        public  void visit(CtorDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        // Erasure: visit<DtorDeclaration>
        public  void visit(DtorDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        // Erasure: visit<InvariantDeclaration>
        public  void visit(InvariantDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        // Erasure: visit<UnitTestDeclaration>
        public  void visit(UnitTestDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        // Erasure: visit<NewDeclaration>
        public  void visit(NewDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        // Erasure: visit<DeleteDeclaration>
        public  void visit(DeleteDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        // Erasure: visit<StaticCtorDeclaration>
        public  void visit(StaticCtorDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        // Erasure: visit<StaticDtorDeclaration>
        public  void visit(StaticDtorDeclaration s) {
            this.visit((FuncDeclaration)s);
        }

        // Erasure: visit<SharedStaticCtorDeclaration>
        public  void visit(SharedStaticCtorDeclaration s) {
            this.visit((StaticCtorDeclaration)s);
        }

        // Erasure: visit<SharedStaticDtorDeclaration>
        public  void visit(SharedStaticDtorDeclaration s) {
            this.visit((StaticDtorDeclaration)s);
        }

        // Erasure: visit<CompileDeclaration>
        public  void visit(CompileDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        // Erasure: visit<UserAttributeDeclaration>
        public  void visit(UserAttributeDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        // Erasure: visit<LinkDeclaration>
        public  void visit(LinkDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        // Erasure: visit<AnonDeclaration>
        public  void visit(AnonDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        // Erasure: visit<AlignDeclaration>
        public  void visit(AlignDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        // Erasure: visit<CPPMangleDeclaration>
        public  void visit(CPPMangleDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        // Erasure: visit<CPPNamespaceDeclaration>
        public  void visit(CPPNamespaceDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        // Erasure: visit<ProtDeclaration>
        public  void visit(ProtDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        // Erasure: visit<PragmaDeclaration>
        public  void visit(PragmaDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        // Erasure: visit<StorageClassDeclaration>
        public  void visit(StorageClassDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        // Erasure: visit<ConditionalDeclaration>
        public  void visit(ConditionalDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        // Erasure: visit<StaticForeachDeclaration>
        public  void visit(StaticForeachDeclaration s) {
            this.visit((AttribDeclaration)s);
        }

        // Erasure: visit<DeprecatedDeclaration>
        public  void visit(DeprecatedDeclaration s) {
            this.visit((StorageClassDeclaration)s);
        }

        // Erasure: visit<StaticIfDeclaration>
        public  void visit(StaticIfDeclaration s) {
            this.visit((ConditionalDeclaration)s);
        }

        // Erasure: visit<EnumMember>
        public  void visit(EnumMember s) {
            this.visit((VarDeclaration)s);
        }

        // Erasure: visit<Module>
        public  void visit(dmodule.Module s) {
            this.visit((dmodule.Package)s);
        }

        // Erasure: visit<StructDeclaration>
        public  void visit(StructDeclaration s) {
            this.visit((AggregateDeclaration)s);
        }

        // Erasure: visit<UnionDeclaration>
        public  void visit(UnionDeclaration s) {
            this.visit((StructDeclaration)s);
        }

        // Erasure: visit<ClassDeclaration>
        public  void visit(ClassDeclaration s) {
            this.visit((AggregateDeclaration)s);
        }

        // Erasure: visit<InterfaceDeclaration>
        public  void visit(InterfaceDeclaration s) {
            this.visit((ClassDeclaration)s);
        }

        // Erasure: visit<TemplateMixin>
        public  void visit(TemplateMixin s) {
            this.visit((TemplateInstance)s);
        }

        // Erasure: visit<ImportStatement>
        public  void visit(ImportStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<ScopeStatement>
        public  void visit(ScopeStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<ReturnStatement>
        public  void visit(ReturnStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<LabelStatement>
        public  void visit(LabelStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<StaticAssertStatement>
        public  void visit(StaticAssertStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<CompileStatement>
        public  void visit(CompileStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<WhileStatement>
        public  void visit(WhileStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<ForStatement>
        public  void visit(ForStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<DoStatement>
        public  void visit(DoStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<ForeachRangeStatement>
        public  void visit(ForeachRangeStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<ForeachStatement>
        public  void visit(ForeachStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<IfStatement>
        public  void visit(IfStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<ScopeGuardStatement>
        public  void visit(ScopeGuardStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<ConditionalStatement>
        public  void visit(ConditionalStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<StaticForeachStatement>
        public  void visit(StaticForeachStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<PragmaStatement>
        public  void visit(PragmaStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<SwitchStatement>
        public  void visit(SwitchStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<CaseRangeStatement>
        public  void visit(CaseRangeStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<CaseStatement>
        public  void visit(CaseStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<DefaultStatement>
        public  void visit(DefaultStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<BreakStatement>
        public  void visit(BreakStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<ContinueStatement>
        public  void visit(ContinueStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<GotoDefaultStatement>
        public  void visit(GotoDefaultStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<GotoCaseStatement>
        public  void visit(GotoCaseStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<GotoStatement>
        public  void visit(GotoStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<SynchronizedStatement>
        public  void visit(SynchronizedStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<WithStatement>
        public  void visit(WithStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<TryCatchStatement>
        public  void visit(TryCatchStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<TryFinallyStatement>
        public  void visit(TryFinallyStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<ThrowStatement>
        public  void visit(ThrowStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<AsmStatement>
        public  void visit(AsmStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<ExpStatement>
        public  void visit(ExpStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<CompoundStatement>
        public  void visit(CompoundStatement s) {
            this.visit((Statement)s);
        }

        // Erasure: visit<CompoundDeclarationStatement>
        public  void visit(CompoundDeclarationStatement s) {
            this.visit((CompoundStatement)s);
        }

        // Erasure: visit<CompoundAsmStatement>
        public  void visit(CompoundAsmStatement s) {
            this.visit((CompoundStatement)s);
        }

        // Erasure: visit<InlineAsmStatement>
        public  void visit(InlineAsmStatement s) {
            this.visit((AsmStatement)s);
        }

        // Erasure: visit<GccAsmStatement>
        public  void visit(GccAsmStatement s) {
            this.visit((AsmStatement)s);
        }

        // Erasure: visit<TypeBasic>
        public  void visit(TypeBasic t) {
            this.visit((Type)t);
        }

        // Erasure: visit<TypeError>
        public  void visit(TypeError t) {
            this.visit((Type)t);
        }

        // Erasure: visit<TypeNull>
        public  void visit(TypeNull t) {
            this.visit((Type)t);
        }

        // Erasure: visit<TypeVector>
        public  void visit(TypeVector t) {
            this.visit((Type)t);
        }

        // Erasure: visit<TypeEnum>
        public  void visit(TypeEnum t) {
            this.visit((Type)t);
        }

        // Erasure: visit<TypeTuple>
        public  void visit(TypeTuple t) {
            this.visit((Type)t);
        }

        // Erasure: visit<TypeClass>
        public  void visit(TypeClass t) {
            this.visit((Type)t);
        }

        // Erasure: visit<TypeStruct>
        public  void visit(TypeStruct t) {
            this.visit((Type)t);
        }

        // Erasure: visit<TypeNext>
        public  void visit(TypeNext t) {
            this.visit((Type)t);
        }

        // Erasure: visit<TypeQualified>
        public  void visit(TypeQualified t) {
            this.visit((Type)t);
        }

        // Erasure: visit<TypeTraits>
        public  void visit(TypeTraits t) {
            this.visit((Type)t);
        }

        // Erasure: visit<TypeReference>
        public  void visit(TypeReference t) {
            this.visit((TypeNext)t);
        }

        // Erasure: visit<TypeSlice>
        public  void visit(TypeSlice t) {
            this.visit((TypeNext)t);
        }

        // Erasure: visit<TypeDelegate>
        public  void visit(TypeDelegate t) {
            this.visit((TypeNext)t);
        }

        // Erasure: visit<TypePointer>
        public  void visit(TypePointer t) {
            this.visit((TypeNext)t);
        }

        // Erasure: visit<TypeFunction>
        public  void visit(TypeFunction t) {
            this.visit((TypeNext)t);
        }

        // Erasure: visit<TypeArray>
        public  void visit(TypeArray t) {
            this.visit((TypeNext)t);
        }

        // Erasure: visit<TypeDArray>
        public  void visit(TypeDArray t) {
            this.visit((TypeArray)t);
        }

        // Erasure: visit<TypeAArray>
        public  void visit(TypeAArray t) {
            this.visit((TypeArray)t);
        }

        // Erasure: visit<TypeSArray>
        public  void visit(TypeSArray t) {
            this.visit((TypeArray)t);
        }

        // Erasure: visit<TypeIdentifier>
        public  void visit(TypeIdentifier t) {
            this.visit((TypeQualified)t);
        }

        // Erasure: visit<TypeReturn>
        public  void visit(TypeReturn t) {
            this.visit((TypeQualified)t);
        }

        // Erasure: visit<TypeTypeof>
        public  void visit(TypeTypeof t) {
            this.visit((TypeQualified)t);
        }

        // Erasure: visit<TypeInstance>
        public  void visit(TypeInstance t) {
            this.visit((TypeQualified)t);
        }

        // Erasure: visit<DeclarationExp>
        public  void visit(DeclarationExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<IntegerExp>
        public  void visit(IntegerExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<NewAnonClassExp>
        public  void visit(NewAnonClassExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<IsExp>
        public  void visit(IsExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<RealExp>
        public  void visit(RealExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<NullExp>
        public  void visit(NullExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<TypeidExp>
        public  void visit(TypeidExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<TraitsExp>
        public  void visit(TraitsExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<StringExp>
        public  void visit(StringExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<NewExp>
        public  void visit(NewExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<AssocArrayLiteralExp>
        public  void visit(AssocArrayLiteralExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<ArrayLiteralExp>
        public  void visit(ArrayLiteralExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<CompileExp>
        public  void visit(CompileExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<FuncExp>
        public  void visit(FuncExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<IntervalExp>
        public  void visit(IntervalExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<TypeExp>
        public  void visit(TypeExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<ScopeExp>
        public  void visit(ScopeExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<IdentifierExp>
        public  void visit(IdentifierExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<UnaExp>
        public  void visit(UnaExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<DefaultInitExp>
        public  void visit(DefaultInitExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<BinExp>
        public  void visit(BinExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<DsymbolExp>
        public  void visit(DsymbolExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<TemplateExp>
        public  void visit(TemplateExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<SymbolExp>
        public  void visit(SymbolExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<TupleExp>
        public  void visit(TupleExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<ThisExp>
        public  void visit(ThisExp e) {
            this.visit((Expression)e);
        }

        // Erasure: visit<VarExp>
        public  void visit(VarExp e) {
            this.visit((SymbolExp)e);
        }

        // Erasure: visit<DollarExp>
        public  void visit(DollarExp e) {
            this.visit((IdentifierExp)e);
        }

        // Erasure: visit<SuperExp>
        public  void visit(SuperExp e) {
            this.visit((ThisExp)e);
        }

        // Erasure: visit<AddrExp>
        public  void visit(AddrExp e) {
            this.visit((UnaExp)e);
        }

        // Erasure: visit<PreExp>
        public  void visit(PreExp e) {
            this.visit((UnaExp)e);
        }

        // Erasure: visit<PtrExp>
        public  void visit(PtrExp e) {
            this.visit((UnaExp)e);
        }

        // Erasure: visit<NegExp>
        public  void visit(NegExp e) {
            this.visit((UnaExp)e);
        }

        // Erasure: visit<UAddExp>
        public  void visit(UAddExp e) {
            this.visit((UnaExp)e);
        }

        // Erasure: visit<NotExp>
        public  void visit(NotExp e) {
            this.visit((UnaExp)e);
        }

        // Erasure: visit<ComExp>
        public  void visit(ComExp e) {
            this.visit((UnaExp)e);
        }

        // Erasure: visit<DeleteExp>
        public  void visit(DeleteExp e) {
            this.visit((UnaExp)e);
        }

        // Erasure: visit<CastExp>
        public  void visit(CastExp e) {
            this.visit((UnaExp)e);
        }

        // Erasure: visit<CallExp>
        public  void visit(CallExp e) {
            this.visit((UnaExp)e);
        }

        // Erasure: visit<DotIdExp>
        public  void visit(DotIdExp e) {
            this.visit((UnaExp)e);
        }

        // Erasure: visit<AssertExp>
        public  void visit(AssertExp e) {
            this.visit((UnaExp)e);
        }

        // Erasure: visit<ImportExp>
        public  void visit(ImportExp e) {
            this.visit((UnaExp)e);
        }

        // Erasure: visit<DotTemplateInstanceExp>
        public  void visit(DotTemplateInstanceExp e) {
            this.visit((UnaExp)e);
        }

        // Erasure: visit<ArrayExp>
        public  void visit(ArrayExp e) {
            this.visit((UnaExp)e);
        }

        // Erasure: visit<FuncInitExp>
        public  void visit(FuncInitExp e) {
            this.visit((DefaultInitExp)e);
        }

        // Erasure: visit<PrettyFuncInitExp>
        public  void visit(PrettyFuncInitExp e) {
            this.visit((DefaultInitExp)e);
        }

        // Erasure: visit<FileInitExp>
        public  void visit(FileInitExp e) {
            this.visit((DefaultInitExp)e);
        }

        // Erasure: visit<LineInitExp>
        public  void visit(LineInitExp e) {
            this.visit((DefaultInitExp)e);
        }

        // Erasure: visit<ModuleInitExp>
        public  void visit(ModuleInitExp e) {
            this.visit((DefaultInitExp)e);
        }

        // Erasure: visit<CommaExp>
        public  void visit(CommaExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<PostExp>
        public  void visit(PostExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<PowExp>
        public  void visit(PowExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<MulExp>
        public  void visit(MulExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<DivExp>
        public  void visit(DivExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<ModExp>
        public  void visit(ModExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<AddExp>
        public  void visit(AddExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<MinExp>
        public  void visit(MinExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<CatExp>
        public  void visit(CatExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<ShlExp>
        public  void visit(ShlExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<ShrExp>
        public  void visit(ShrExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<UshrExp>
        public  void visit(UshrExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<EqualExp>
        public  void visit(EqualExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<InExp>
        public  void visit(InExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<IdentityExp>
        public  void visit(IdentityExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<CmpExp>
        public  void visit(CmpExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<AndExp>
        public  void visit(AndExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<XorExp>
        public  void visit(XorExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<OrExp>
        public  void visit(OrExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<LogicalExp>
        public  void visit(LogicalExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<CondExp>
        public  void visit(CondExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<AssignExp>
        public  void visit(AssignExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<BinAssignExp>
        public  void visit(BinAssignExp e) {
            this.visit((BinExp)e);
        }

        // Erasure: visit<AddAssignExp>
        public  void visit(AddAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        // Erasure: visit<MinAssignExp>
        public  void visit(MinAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        // Erasure: visit<MulAssignExp>
        public  void visit(MulAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        // Erasure: visit<DivAssignExp>
        public  void visit(DivAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        // Erasure: visit<ModAssignExp>
        public  void visit(ModAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        // Erasure: visit<PowAssignExp>
        public  void visit(PowAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        // Erasure: visit<AndAssignExp>
        public  void visit(AndAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        // Erasure: visit<OrAssignExp>
        public  void visit(OrAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        // Erasure: visit<XorAssignExp>
        public  void visit(XorAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        // Erasure: visit<ShlAssignExp>
        public  void visit(ShlAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        // Erasure: visit<ShrAssignExp>
        public  void visit(ShrAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        // Erasure: visit<UshrAssignExp>
        public  void visit(UshrAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        // Erasure: visit<CatAssignExp>
        public  void visit(CatAssignExp e) {
            this.visit((BinAssignExp)e);
        }

        // Erasure: visit<TemplateAliasParameter>
        public  void visit(TemplateAliasParameter tp) {
            this.visit((TemplateParameter)tp);
        }

        // Erasure: visit<TemplateTypeParameter>
        public  void visit(TemplateTypeParameter tp) {
            this.visit((TemplateParameter)tp);
        }

        // Erasure: visit<TemplateTupleParameter>
        public  void visit(TemplateTupleParameter tp) {
            this.visit((TemplateParameter)tp);
        }

        // Erasure: visit<TemplateValueParameter>
        public  void visit(TemplateValueParameter tp) {
            this.visit((TemplateParameter)tp);
        }

        // Erasure: visit<TemplateThisParameter>
        public  void visit(TemplateThisParameter tp) {
            this.visit((TemplateTypeParameter)tp);
        }

        // Erasure: visit<StaticIfCondition>
        public  void visit(StaticIfCondition c) {
            this.visit((Condition)c);
        }

        // Erasure: visit<DVCondition>
        public  void visit(DVCondition c) {
            this.visit((Condition)c);
        }

        // Erasure: visit<DebugCondition>
        public  void visit(DebugCondition c) {
            this.visit((DVCondition)c);
        }

        // Erasure: visit<VersionCondition>
        public  void visit(VersionCondition c) {
            this.visit((DVCondition)c);
        }

        // Erasure: visit<ExpInitializer>
        public  void visit(ExpInitializer i) {
            this.visit((Initializer)i);
        }

        // Erasure: visit<StructInitializer>
        public  void visit(StructInitializer i) {
            this.visit((Initializer)i);
        }

        // Erasure: visit<ArrayInitializer>
        public  void visit(ArrayInitializer i) {
            this.visit((Initializer)i);
        }

        // Erasure: visit<VoidInitializer>
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
