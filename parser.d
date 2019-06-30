import dmd.astbase;
import dmd.errors;
import dmd.globals;
import dmd.parse;
import dmd.transitivevisitor;
import dmd.root.outbuffer;

import core.stdc.stdio;

alias AST = ASTBase;

extern(C++) class LispyPrint : ParseTimeTransitiveVisitor!AST {
    OutBuffer* buf;
    override void visit(AST.Dsymbol s) { 
        buf.printf("%s", s.toChars());
    }

    override void visit(AST.AliasThis a) { 
        // TODO:
        super.visit(a);
    }

    override void visit(AST.Declaration d) { 
        buf.printf("%s", d.toChars());
    }

    override void visit(AST.ScopeDsymbol scd) { 
        buf.printf("%s", scd.toChars());
    }

    override void visit(AST.Import imp) { 
        buf.printf("import %s", imp.toChars());
    }

    override void visit(AST.AttribDeclaration attr) { 
        buf.printf("( %s ", attr.toChars);
        buf.writenl;
        buf.level++;
        super.visit(attr);
        buf.level--;
        buf.writenl;
        buf.printf(")");
    }

    override void visit(AST.StaticAssert as) { 
        buf.printf("( static assert ");
        super.visit(as);
        buf.printf(")");
    }
    
    override void visit(AST.DebugSymbol sym) {
        buf.printf("( debug ");
        buf.writenl;
        buf.level++;
        super.visit(sym);
        buf.level--;
        buf.writenl;
        buf.printf(")");
        buf.writenl;
    }

    override void visit(AST.VersionSymbol ver) { 
        buf.printf("( version ");
        buf.writenl;
        buf.level++;
        super.visit(ver);
        buf.level--;
        buf.writenl;
        buf.printf(")");
        buf.writenl;
    }

    override void visit(AST.VarDeclaration d) {
        buf.printf("( var ");
        buf.level++;
        buf.printf("%s ", d.ident.toChars);
        d.type.accept(this);
        buf.level--;
        buf.printf(")");
    }

    override void visit(AST.FuncDeclaration d) {
        buf.printf("( func ");
        buf.writenl;
        buf.level++;
        d.type.accept(this);
        d.fbody.accept(this);
        buf.level--;
        buf.writenl;
        buf.printf(")");
        buf.writenl;
    }

    override void visit(AST.AliasDeclaration d) {
        buf.printf("(alias ");
        buf.writenl;
        buf.level++;
        super.visit(d);
        buf.level--;
        buf.writenl;
        buf.printf(")");
    }

    override void visit(AST.TupleDeclaration d) {
        buf.printf("( tuple ");
        buf.writenl;
        buf.level++;
        super.visit(d);
        buf.level--;
        buf.writenl;
        buf.printf(")");
    }

    override void visit(AST.FuncLiteralDeclaration d) {
        buf.printf("( func literal ");
        buf.writenl;
        buf.level++;
        super.visit(d);
        buf.level--;
        buf.writenl;
        buf.printf(")");
    }

    override void visit(AST.PostBlitDeclaration d) {
        buf.printf("( this(this) ");
        buf.writenl;
        buf.level++;
        super.visit(d);
        buf.level--;
        buf.writenl;
        buf.printf(")");
    }

    override void visit(AST.CtorDeclaration d) {
        buf.printf("( ctor \n");
        buf.level++;
        super.visit(d);
        buf.level--;

        buf.printf(")");
    }

    override void visit(AST.DtorDeclaration d) {
        buf.printf("( dtor ");
        buf.level++;
        super.visit(d);
        buf.level--;

        buf.printf(")");
    }

    override void visit(AST.InvariantDeclaration d) {
        buf.printf("( invariant ");
        buf.level++;
        super.visit(d);
        buf.level--;

        buf.printf(")");
    }

    override void visit(AST.UnitTestDeclaration d) {
        buf.printf("( unittest ");
        buf.level++;
        super.visit(d);
        buf.level--;

        buf.printf(")");
    }

    override void visit(AST.NewDeclaration) { assert(0); }
    
    override void visit(AST.DeleteDeclaration) { assert(0); }

    override void visit(AST.StaticCtorDeclaration) { assert(0); }

    override void visit(AST.StaticDtorDeclaration) { assert(0); }

    override void visit(AST.SharedStaticCtorDeclaration) { assert(0); }
    override void visit(AST.SharedStaticDtorDeclaration) { assert(0); }
    override void visit(AST.Package) { assert(0); }
    override void visit(AST.EnumDeclaration) { assert(0); }
    override void visit(AST.AggregateDeclaration) { assert(0); }
    override void visit(AST.TemplateDeclaration) { assert(0); }
    override void visit(AST.TemplateInstance) { assert(0); }
    override void visit(AST.Nspace) { assert(0); }
    override void visit(AST.CompileDeclaration) { assert(0); }
    override void visit(AST.UserAttributeDeclaration) { assert(0); }
    override void visit(AST.LinkDeclaration) { assert(0); }
    override void visit(AST.AnonDeclaration) { assert(0); }
    override void visit(AST.AlignDeclaration) { assert(0); }
    override void visit(AST.CPPMangleDeclaration) { assert(0); }
    override void visit(AST.ProtDeclaration) { assert(0); }
    override void visit(AST.PragmaDeclaration) { assert(0); }
    override void visit(AST.StorageClassDeclaration) { assert(0); }
    override void visit(AST.ConditionalDeclaration) { assert(0); }
    override void visit(AST.DeprecatedDeclaration) { assert(0); }
    override void visit(AST.StaticIfDeclaration) { assert(0); }
    override void visit(AST.EnumMember) { assert(0); }
    override void visit(AST.Module) { assert(0); }
    override void visit(AST.StructDeclaration d) {
        buf.printf("( struct ");
        buf.writenl;
        buf.level++;
        super.visit(d);
        buf.level--;
        buf.writenl;
        buf.printf(")");
        buf.writenl;
    }
    override void visit(AST.UnionDeclaration) { assert(0); }
    override void visit(AST.ClassDeclaration) { assert(0); }
    override void visit(AST.InterfaceDeclaration) { assert(0); }
    override void visit(AST.TemplateMixin) { assert(0); }
    override void visit(AST.Parameter p) { 
        buf.printf("%s ", p.ident.toChars);
        super.visit(p);
    }
    override void visit(AST.Statement) { assert(0); }
    override void visit(AST.ImportStatement) { assert(0); }
    override void visit(AST.ScopeStatement) { assert(0); }
    override void visit(AST.ReturnStatement) { assert(0); }
    override void visit(AST.LabelStatement) { assert(0); }
    override void visit(AST.StaticAssertStatement) { assert(0); }
    override void visit(AST.CompileStatement) { assert(0); }
    override void visit(AST.WhileStatement) { assert(0); }
    override void visit(AST.ForStatement) { assert(0); }
    override void visit(AST.DoStatement) { assert(0); }
    override void visit(AST.ForeachRangeStatement) { assert(0); }
    override void visit(AST.ForeachStatement) { assert(0); }
    override void visit(AST.IfStatement) { assert(0); }
    override void visit(AST.ScopeGuardStatement) { assert(0); }
    override void visit(AST.ConditionalStatement) { assert(0); }
    override void visit(AST.PragmaStatement) { assert(0); }
    override void visit(AST.SwitchStatement) { assert(0); }
    override void visit(AST.CaseRangeStatement) { assert(0); }
    override void visit(AST.CaseStatement) { assert(0); }
    override void visit(AST.DefaultStatement) { assert(0); }
    override void visit(AST.BreakStatement) { assert(0); }
    override void visit(AST.ContinueStatement) { assert(0); }
    override void visit(AST.GotoDefaultStatement) { assert(0); }
    override void visit(AST.GotoCaseStatement) { assert(0); }
    override void visit(AST.GotoStatement) { assert(0); }
    override void visit(AST.SynchronizedStatement) { assert(0); }
    override void visit(AST.WithStatement) { assert(0); }
    override void visit(AST.TryCatchStatement) { assert(0); }
    override void visit(AST.TryFinallyStatement) { assert(0); }
    override void visit(AST.ThrowStatement) { assert(0); }
    override void visit(AST.AsmStatement) { assert(0); }
    override void visit(AST.ExpStatement s) {
        buf.printf("( expr ");
        buf.writenl;
        buf.level++;
        super.visit(s);
        buf.level--;
        buf.writenl;
        buf.printf(")");
    }
    override void visit(AST.CompoundStatement s) {
        buf.printf("(");
        buf.writenl;
        buf.level++;
        if (s.statements)
            foreach (i, st; *s.statements) {
                if (i) buf.writenl;
                st.accept(this);
            }
        buf.level--;
        buf.writenl;
        buf.printf(")");
        
    }
    override void visit(AST.CompoundDeclarationStatement) { assert(0); }
    override void visit(AST.CompoundAsmStatement) { assert(0); }
    override void visit(AST.InlineAsmStatement) { assert(0); }
    override void visit(AST.Type) { assert(0); }
    override void visit(AST.TypeBasic t) { 
        buf.printf("%s", t.dstring);
    }
    override void visit(AST.TypeError) {
        buf.printf("terror");
    }
    override void visit(AST.TypeNull) {
        buf.printf("typeof(null)");
    }
    override void visit(AST.TypeVector t) {
        buf.printf("( __vector");
        super.visit(t);
        buf.printf(")");
    }
    override void visit(AST.TypeEnum) { assert(0); }
    override void visit(AST.TypeTuple) { assert(0); }
    override void visit(AST.TypeClass) { assert(0); }
    override void visit(AST.TypeStruct) { assert(0); }
    override void visit(AST.TypeNext) { assert(0); }
    override void visit(AST.TypeReference) { assert(0); }
    override void visit(AST.TypeSlice) { assert(0); }
    override void visit(AST.TypeDelegate) { assert(0); }
    override void visit(AST.TypePointer) { assert(0); }
    override void visit(AST.TypeFunction tf) {
        tf.next.accept(this);
        buf.printf(" ");
        foreach (i, p; *tf.parameterList.parameters) {
            if (i) buf.printf(" ");
            p.accept(this);
        }
    }
    override void visit(AST.TypeArray) { assert(0); }
    override void visit(AST.TypeDArray d) {
        super.visit(d);
        buf.printf("[]");
    }
    override void visit(AST.TypeAArray) { assert(0); }
    override void visit(AST.TypeSArray) { assert(0); }
    override void visit(AST.TypeQualified) { assert(0); }
    override void visit(AST.TypeTraits) { assert(0); }
    override void visit(AST.TypeIdentifier d) {
        super.visit(d);
        buf.printf("%s", d.ident.toChars);
    }
    override void visit(AST.TypeReturn) { assert(0); }
    override void visit(AST.TypeTypeof) { assert(0); }
    override void visit(AST.TypeInstance) { assert(0); }
    override void visit(AST.Expression) { assert(0); }
    override void visit(AST.DeclarationExp) { assert(0); }
    override void visit(AST.IntegerExp e) {
        buf.printf("%lld", e.value);
    }
    override void visit(AST.NewAnonClassExp) { assert(0); }
    override void visit(AST.IsExp) { assert(0); }
    override void visit(AST.RealExp) { assert(0); }
    override void visit(AST.NullExp) { assert(0); }
    override void visit(AST.TypeidExp) { assert(0); }
    override void visit(AST.TraitsExp) { assert(0); }
    override void visit(AST.StringExp) { assert(0); }
    override void visit(AST.NewExp) { assert(0); }
    override void visit(AST.AssocArrayLiteralExp) { assert(0); }
    override void visit(AST.ArrayLiteralExp) { assert(0); }
    override void visit(AST.FuncExp) { assert(0); }
    override void visit(AST.IntervalExp) { assert(0); }
    override void visit(AST.TypeExp) { assert(0); }
    override void visit(AST.ScopeExp) { assert(0); }
    override void visit(AST.IdentifierExp e) {
        buf.printf("%s", e.ident.toChars);
    }
    override void visit(AST.UnaExp) { assert(0); }
    override void visit(AST.DefaultInitExp) { assert(0); }
    override void visit(AST.BinExp) { assert(0); }
    override void visit(AST.DsymbolExp) { assert(0); }
    override void visit(AST.TemplateExp) { assert(0); }
    override void visit(AST.SymbolExp) { assert(0); }
    override void visit(AST.VarExp) { assert(0); }
    override void visit(AST.TupleExp) { assert(0); }
    override void visit(AST.DollarExp) { assert(0); }
    override void visit(AST.ThisExp) { assert(0); }
    override void visit(AST.SuperExp) { assert(0); }
    override void visit(AST.AddrExp) { assert(0); }
    override void visit(AST.PreExp) { assert(0); }
    override void visit(AST.PtrExp) { assert(0); }
    override void visit(AST.NegExp) { assert(0); }
    override void visit(AST.UAddExp) { assert(0); }
    override void visit(AST.NotExp) { assert(0); }
    override void visit(AST.ComExp) { assert(0); }
    override void visit(AST.DeleteExp) { assert(0); }
    override void visit(AST.CastExp) { assert(0); }
    override void visit(AST.CallExp) { assert(0); }
    override void visit(AST.DotIdExp) { assert(0); }
    override void visit(AST.AssertExp) { assert(0); }
    override void visit(AST.CompileExp) { assert(0); }
    override void visit(AST.ImportExp) { assert(0); }
    override void visit(AST.DotTemplateInstanceExp) { assert(0); }
    override void visit(AST.ArrayExp) { assert(0); }
    override void visit(AST.FuncInitExp) { assert(0); }
    override void visit(AST.PrettyFuncInitExp) { assert(0); }
    override void visit(AST.FileInitExp) { assert(0); }
    override void visit(AST.LineInitExp) { assert(0); }
    override void visit(AST.ModuleInitExp) { assert(0); }
    override void visit(AST.CommaExp) { assert(0); }
    override void visit(AST.PostExp) { assert(0); }
    override void visit(AST.PowExp) { assert(0); }
    override void visit(AST.MulExp) { assert(0); }
    override void visit(AST.DivExp) { assert(0); }
    override void visit(AST.ModExp) { assert(0); }
    override void visit(AST.AddExp) { assert(0); }
    override void visit(AST.MinExp) { assert(0); }
    override void visit(AST.CatExp) { assert(0); }
    override void visit(AST.ShlExp) { assert(0); }
    override void visit(AST.ShrExp) { assert(0); }
    override void visit(AST.UshrExp) { assert(0); }
    override void visit(AST.EqualExp) { assert(0); }
    override void visit(AST.InExp) { assert(0); }
    override void visit(AST.IdentityExp) { assert(0); }
    override void visit(AST.CmpExp) { assert(0); }
    override void visit(AST.AndExp) { assert(0); }
    override void visit(AST.XorExp) { assert(0); }
    override void visit(AST.OrExp) { assert(0); }
    override void visit(AST.LogicalExp) { assert(0); }
    override void visit(AST.CondExp) { assert(0); }
    override void visit(AST.AssignExp a) {
        buf.printf("( = ");
        a.e1.accept(this);
        buf.printf(" ");
        a.e2.accept(this);
        buf.printf(")");
    }
    override void visit(AST.BinAssignExp) { assert(0); }
    override void visit(AST.AddAssignExp) { assert(0); }
    override void visit(AST.MinAssignExp) { assert(0); }
    override void visit(AST.MulAssignExp) { assert(0); }
    override void visit(AST.DivAssignExp) { assert(0); }
    override void visit(AST.ModAssignExp) { assert(0); }
    override void visit(AST.PowAssignExp) { assert(0); }
    override void visit(AST.AndAssignExp) { assert(0); }
    override void visit(AST.OrAssignExp) { assert(0); }
    override void visit(AST.XorAssignExp) { assert(0); }
    override void visit(AST.ShlAssignExp) { assert(0); }
    override void visit(AST.ShrAssignExp) { assert(0); }
    override void visit(AST.UshrAssignExp) { assert(0); }
    override void visit(AST.CatAssignExp) { assert(0); }
    override void visit(AST.TemplateParameter) { assert(0); }
    override void visit(AST.TemplateAliasParameter) { assert(0); }
    override void visit(AST.TemplateTypeParameter) { assert(0); }
    override void visit(AST.TemplateTupleParameter) { assert(0); }
    override void visit(AST.TemplateValueParameter) { assert(0); }
    override void visit(AST.TemplateThisParameter) { assert(0); }
    override void visit(AST.Condition) { assert(0); }
    override void visit(AST.StaticIfCondition) { assert(0); }
    override void visit(AST.DVCondition) { assert(0); }
    override void visit(AST.DebugCondition) { assert(0); }
    override void visit(AST.VersionCondition) { assert(0); }
    override void visit(AST.Initializer) { assert(0); }
    override void visit(AST.ExpInitializer) { assert(0); }
    override void visit(AST.StructInitializer) { assert(0); }
    override void visit(AST.ArrayInitializer) { assert(0); }
    override void visit(AST.VoidInitializer) { assert(0); }
}

void main(string[] args)
{
    scope diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
    scope parser = new Parser!ASTBase(null, null, false, diagnosticReporter);
    assert(parser !is null);
}
