/+dub.sdl:
dependency "dmd:frontend" path="vendor/dmd"
+/
module dtool;

import core.stdc.stdio, core.stdc.string;

import std.string;
import std.getopt;

import dmd.astbase;
import dmd.errors;
import dmd.globals;
import dmd.parse;
import dmd.transitivevisitor;
import dmd.tokens;
import dmd.lexer;

import dmd.root.file;
import dmd.root.filename;
import dmd.root.outbuffer;
import dmd.root.outbuffer;

import core.stdc.stdio, core.stdc.stdarg;

alias AST = ASTBase;

extern(C++) class LispyPrint : ParseTimeTransitiveVisitor!AST {
    alias visit = typeof(super).visit;
    OutBuffer* buf;

    void open(const(char)* format, ...) {
        va_list ap;
        va_start(ap, format);
        buf.writestring("( ");
        buf.vprintf(format, ap);
        va_end(ap);
        buf.level++;
        buf.writenl;
    }

    void close() {
        buf.level--;
        buf.writenl;
        buf.printf(")");
        buf.writenl;
    }

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
        open(attr.toChars);
        super.visit(attr);
        close();
    }

    override void visit(AST.StaticAssert as) { 
        open("static assert");
        super.visit(as);
        close();
    }
    
    override void visit(AST.DebugSymbol sym) {
        open("debug");
        super.visit(sym);
        close();
    }

    override void visit(AST.VersionSymbol ver) { 
        open("version");
        super.visit(ver);
        close();
    }

    override void visit(AST.VarDeclaration d) {
        buf.printf("( var %s ", d.ident.toChars);
        d.type.accept(this);
        if (d._init) {
            buf.printf(" ");
            d._init.accept(this);
        }
        buf.printf(")");
    }

    override void visit(AST.FuncDeclaration d) {
        open("func %s", d.ident.toChars);
        d.type.accept(this);
        if (d.fbody) d.fbody.accept(this);
        close();
    }

    override void visit(AST.AliasDeclaration d) {
        open("alias %s", d.ident.toChars);
        super.visit(d);
        close();
    }

    override void visit(AST.TupleDeclaration d) {
        open("tuple");
        super.visit(d);
        close();
    }

    override void visit(AST.FuncLiteralDeclaration d) {
        open("func literal");
        super.visit(d);
        close();
    }

    override void visit(AST.PostBlitDeclaration d) {
        open("this(this)");
        super.visit(d);
        close();
    }

    override void visit(AST.CtorDeclaration d) {
        open("ctor");
        super.visit(d);
        close();
    }

    override void visit(AST.DtorDeclaration d) {
        open("dtor");
        super.visit(d);
        close();
    }

    override void visit(AST.InvariantDeclaration d) {
        open("invariant");
        super.visit(d);
        close();
    }

    override void visit(AST.UnitTestDeclaration d) {
        open("unittest");
        super.visit(d);
        close();
    }

    override void visit(AST.NewDeclaration d) { 
        open("newdecl");
        super.visit(d);
        close();
    }
    
    override void visit(AST.DeleteDeclaration d) {
        open("deletedecl");
        super.visit(d);
        close();
    }

    override void visit(AST.StaticCtorDeclaration d) { 
        open("static this");
        super.visit(d);
        close();
    }

    override void visit(AST.StaticDtorDeclaration d) {
        open("static ~this");
        super.visit(d);
        close();
    }

    override void visit(AST.SharedStaticCtorDeclaration d) {
        open("shared static this");
        super.visit(d);
        close();    
    }

    override void visit(AST.SharedStaticDtorDeclaration d) {
        open("shared static ~this");
        super.visit(d);
        close();
    }

    override void visit(AST.Package d) {
        open("package");
        super.visit(d);
        close();
    }

    override void visit(AST.EnumDeclaration d) {
        open("enum %s", d.ident.toChars);
        super.visit(d);
        close();
    }

    override void visit(AST.AggregateDeclaration d) {
        open("aggregate %s", d.ident.toChars); // should  be handled by struct/class/union...
        super.visit(d);
        close();
    }

    override void visit(AST.TemplateDeclaration d) {
        open("template %s", d.ident.toChars);
        super.visit(d);
        close();
    }

    override void visit(AST.TemplateInstance ti) {
        open("template instance %s", ti.ident.toChars);
        super.visit(ti);
        close();
    }

    override void visit(AST.Nspace n) {
        open("nspace %s", n.ident.toChars);
        super.visit(n);
        close();
    }

    override void visit(AST.CompileDeclaration d) {
        open("compiletime");
        super.visit(d);
        close();
    }

    override void visit(AST.UserAttributeDeclaration d) { 
        open("udas");
        super.visit(d);
        close();
    }
    
    override void visit(AST.LinkDeclaration link) {
        open("%s", link.ident.toChars);
        super.visit(link);
        close();
    }

    override void visit(AST.AnonDeclaration anon) {
        open("anon union %s", anon.ident.toChars);
        super.visit(anon);
        close();
    }

    override void visit(AST.AlignDeclaration d) {
        open("align ");
        super.visit(d.ealign);
        buf.printf(" ");
        if (d.decl) {
            foreach (di; *d.decl)
                super.visit(di);
        }
        close();
    }
    override void visit(AST.CPPMangleDeclaration) { assert(0); }
    override void visit(AST.ProtDeclaration) { assert(0); }
    override void visit(AST.PragmaDeclaration) { assert(0); }
    
    override void visit(AST.StorageClassDeclaration d) {
        buf.printf("( ");
        AST.stcToBuffer(buf, d.stc);
        buf.level++;
        buf.writenl;
        super.visit(d);
        close();
    }

    override void visit(AST.ConditionalDeclaration) { assert(0); }
    override void visit(AST.DeprecatedDeclaration) { assert(0); }
    override void visit(AST.StaticIfDeclaration) { assert(0); }
    override void visit(AST.EnumMember) { assert(0); }
    override void visit(AST.Module) { assert(0); }

    override void visit(AST.StructDeclaration d) {
        open("struct %s", d.ident.toChars);
        if (d.members){
            foreach (m; *d.members)
                m.accept(this);
        }
        close();
    }
    
    override void visit(AST.UnionDeclaration d) {
        open("union %s", d.ident.toChars);
        super.visit(d);
        close();
    }

    override void visit(AST.ClassDeclaration d) {
        open("class %s", d.ident ? d.ident.toChars : "");
        if (d.baseclasses) {
            foreach (i, c; *d.baseclasses) {
                if (i) printf(" ");
                c.type.accept(this);
            }
            buf.writenl;
        }
        if (d.members) {
            foreach (m; *d.members) {
                m.accept(this);
            }
        }
        close();
    }

    override void visit(AST.InterfaceDeclaration d) {
        open("interface %s", d.ident.toChars);
        super.visit(d);
        close();
    }

    override void visit(AST.TemplateMixin m) {
        open("template mixin");
        super.visit(m);
        close();
    }

    override void visit(AST.Parameter p) { 
        buf.printf("%s ", p.ident.toChars);
        p.type.accept(this);
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

    override void visit(AST.Type t) {
        assert(0, "unknown type?");
    }

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

    override void visit(AST.TypeEnum t) {
        buf.printf("enum %s : ", t.sym.toChars);
        super.visit(t.sym.memtype);
    }

    override void visit(AST.TypeTuple t) { 
        buf.printf("( typetuple ");
        if (t.arguments) {
            foreach(i, a; *t.arguments) {
                if (i) buf.printf(" ");
                super.visit(a);
            }
        }
        buf.printf(")");
    }

    override void visit(AST.TypeClass tc) {
        buf.printf("%s", tc.sym.ident.toChars);
    }

    override void visit(AST.TypeStruct ts) {
        buf.printf("%s", ts.sym.ident.toChars);
    }

    override void visit(AST.TypeNext) {
        assert(0, "Unexpected TypeNext"); 
    }
    override void visit(AST.TypeReference t) {
        buf.printf("ref ");
        super.visit(t.next);
    }
    override void visit(AST.TypeSlice ts) { 
        buf.printf("( slice ");
        this.visit(ts.lwr);
        buf.printf(" ");
        this.visit(ts.upr);
        buf.printf(" ");
        this.visit(ts.next);
        buf.printf(")");
    }
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
    override void visit(AST.TypeArray) {
        assert(0, "Unexpected generic array");
    }
    override void visit(AST.TypeDArray d) {
        super.visit(d);
        buf.printf("[]");
    }
    override void visit(AST.TypeAArray ta) {
        super.visit(ta.next);
        buf.printf("[");
        super.visit(ta.index);
        buf.printf("]");
    }

    override void visit(AST.TypeSArray tsa) {
        super.visit(tsa.next);
        buf.printf("[");
        super.visit(tsa.dim);
        buf.printf("]");
    }
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
    
    override void visit(AST.UnaExp e) { 
        buf.printf("%s %s", Token.toChars(e.op));
    }

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
    override void visit(AST.ExpInitializer ei) {
        (ei.exp).accept(this);
    }
    override void visit(AST.StructInitializer) { assert(0); }
    override void visit(AST.ArrayInitializer) { assert(0); }
    override void visit(AST.VoidInitializer) { assert(0); }
}

int main(string[] args)
{
	string outdir = ".";
	auto res = getopt(args,
		"outdir", "output directory", &outdir
	);
	if (res.helpWanted) {
		defaultGetoptPrinter("Trivial D lexer based on DMD.", res.options);
		return 1;
	}
    scope diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
    scope parser = new Parser!ASTBase(null, null, false, diagnosticReporter);
    assert(parser !is null);

	foreach(arg; args[1..$]) {
		auto argz = arg.toStringz;
		auto buffer = File.read(argz);
        if (!buffer.success) {
            fprintf(stderr, "Failed to read from file: %s", argz);
            return 2;
        }
        auto buf = buffer.extractData();
        scope lex = new Lexer(argz, cast(char*)buf.ptr, 0, buf.length, true, true, new StderrDiagnosticReporter(DiagnosticReporting.error));
        auto dest = FileName.forceExt(FileName.name(argz), "tk");
        auto filePath = outdir ~ "/" ~ dest[0..strlen(dest)];
        scope output = new OutBuffer();
        int i = 0;
        while (lex.nextToken() != TOK.endOfFile) {
            output.printf("%4d", lex.token.value);
            if (++i == 20) {
                output.printf(" | Line %5d |\n", lex.token.loc.linnum);
                i  = 0;
            }
        }
        if (i != 0) output.printf(" | Line %5d |\n", lex.token.loc.linnum);
        if (!File.write(filePath.toStringz, output.extractSlice()))
            fprintf(stderr, "Failed to write file: %s\n", dest);
	}
	return 0;
}
