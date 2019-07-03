/+dub.sdl:
dependency "dmd:frontend" path="vendor/dmd"
+/
module dtool;

import dmd.astbase;
import dmd.errors;
import dmd.globals;
import dmd.parse;
import dmd.transitivevisitor;
import dmd.tokens;
import dmd.id;
import dmd.identifier;
import dmd.lexer;

import dmd.root.array;
import dmd.root.file;
import dmd.root.filename;
import dmd.root.outbuffer;
import dmd.root.rootobject;

import core.stdc.stdio, core.stdc.stdarg, core.stdc.stdlib,  core.stdc.string;

import std.string;
import std.getopt;


alias AST = ASTBase;

const(char)* modToChars(uint mod) {
    switch(mod) with(ASTBase.MODFlags) {
        case const_: return "const";
        case immutable_: return "immutable";
        case shared_: return "shared";
        case wild: return "inout";
        case wildconst: return "const(inout)";
        case mutable: return "";
        default: return "";
    }
}

const(char)* linkToChars(uint link) {
    switch(link) with(LINK) {
        case default_: return "";
        case d: return "extern(D)";
        case c: return "extern(C)";
        case cpp: return "extern(C++)";
        case windows: return "extern(Windows)";
        case pascal: return "extern(Pascal)";
        case objc: return "extern(Obj-C)";
        case system: return "extern(System)";
        default: assert(0);
    }
}

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

    void visitDecls(Array!(AST.Dsymbol)* decls) {
        if (decls) {
            foreach (m; *decls) {
                m.accept(this);
            }
        }
    }

    void visitExps(Array!(AST.Expression)* exps) {
        if (exps) {
            foreach (i, e; *exps) {
                if (i) buf.printf(" ");
                e.accept(this);
            }
        }
    }

    void visitStatements(Array!(AST.Statement)* statements) {
        if (statements)
            foreach (i, st; *statements) {
                if (i) buf.writenl;
                st.accept(this);
            }
    }

    void visitTiargs(Array!(RootObject)* tiargs) {
        if (tiargs)
            foreach (i, m; *tiargs) {
                if (i) buf.printf(" ");
                switch (m.dyncast) {
                    case DYNCAST.expression:
                        (cast(ASTBase.Expression)m).accept(this);
                        break;
                    case DYNCAST.type:
                        (cast(ASTBase.Type)m).accept(this);
                        break;
                    default:
                        buf.printf("%s", m.toChars);
                }
            }
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
        open("%s", attr.toChars);
        visitDecls(attr.decl);
        close();
    }

    override void visit(AST.StaticAssert as) { 
        open("static assert");
        as.exp.accept(this);
        close();
    }
    
    override void visit(AST.DebugSymbol sym) {
        buf.printf("debug");
    }

    override void visit(AST.VersionSymbol ver) { 
        buf.printf("version");
    }

    override void visit(AST.VarDeclaration d) {
        open("var %s ", d.ident.toChars);
        if (d.type) d.type.accept(this);
        if (d._init) {
            buf.printf(" ");
            d._init.accept(this);
        }
        close();
    }

    override void visit(AST.FuncDeclaration d) {
        open("func %s", d.ident.toChars);
        if (d.type) d.type.accept(this);
        if (d.fbody) d.fbody.accept(this);
        close();
    }

    override void visit(AST.AliasDeclaration d) {
        open("alias %s ", d.ident.toChars);
        if (d.aliassym) {
             d.aliassym.accept(this);
             buf.printf(" ");
        }
        if (d.type) d.type.accept(this);
        close();
    }

    override void visit(AST.TupleDeclaration d) {
        open("tuple");
        super.visit(d);
        close();
    }

    override void visit(AST.FuncLiteralDeclaration d) {
        open("func literal %s", d.ident.toChars);
        if (d.fbody) d.fbody.accept(this);
        close();
    }

    override void visit(AST.PostBlitDeclaration d) {
        open("this(this)");
        if (d.fbody) d.fbody.accept(this);
        close();
    }

    override void visit(AST.CtorDeclaration d) {
        open("ctor");
        if (d.fbody) d.fbody.accept(this);
        close();
    }

    override void visit(AST.DtorDeclaration d) {
        open("dtor");
        if (d.fbody) d.fbody.accept(this);
        close();
    }

    override void visit(AST.InvariantDeclaration d) {
        open("invariant");
        if (d.fbody) d.fbody.accept(this);
        close();
    }

    override void visit(AST.UnitTestDeclaration d) {
        open("unittest");
        if (d.fbody) d.fbody.accept(this);
        close();
    }

    override void visit(AST.NewDeclaration d) { 
        open("newdecl");
        if (d.fbody) d.fbody.accept(this);
        close();
    }
    
    override void visit(AST.DeleteDeclaration d) {
        open("deletedecl");
        if (d.fbody) d.fbody.accept(this);
        close();
    }

    override void visit(AST.StaticCtorDeclaration d) { 
        open("static this");
        if (d.fbody) d.fbody.accept(this);
        close();
    }

    override void visit(AST.StaticDtorDeclaration d) {
        open("static ~this");
        if (d.fbody) d.fbody.accept(this);
        close();
    }

    override void visit(AST.SharedStaticCtorDeclaration d) {
        open("shared static this");
        if (d.fbody) d.fbody.accept(this);
        close();    
    }

    override void visit(AST.SharedStaticDtorDeclaration d) {
        open("shared static ~this");
        if (d.fbody) d.fbody.accept(this);
        close();
    }

    override void visit(AST.Package d) {
        open("package");
        super.visit(d);
        close();
    }

    override void visit(AST.EnumDeclaration d) {
        open("enum ");
        if (d.ident) buf.printf("%s", d.ident.toChars);
        visitDecls(d.members);
        close();
    }

    override void visit(AST.AggregateDeclaration d) {
        open("aggregate %s", d.ident.toChars); // should  be handled by struct/class/union...
        super.visit(d);
        close();
    }

    override void visit(AST.TemplateDeclaration d) {
        open("template %s", d.ident.toChars);
        visitDecls(d.members);
        close();
    }

    override void visit(AST.TemplateInstance ti) {
        open("template instance %s", ti.ident ? ti.ident.toChars : ti.name.toChars);
        visitTiargs(ti.tiargs);
        visitDecls(ti.members);
        close();
    }

    override void visit(AST.Nspace n) {
        open("nspace %s", n.ident.toChars);
        super.visit(n);
        close();
    }

    override void visit(AST.CompileDeclaration d) {
        open("compiletime");
        visitDecls(d.decl);
        close();
    }

    override void visit(AST.UserAttributeDeclaration d) { 
        open("udas");
        visitDecls(d.decl);
        close();
    }
    
    override void visit(AST.LinkDeclaration link) {
        open("%s", linkToChars(link.linkage));
        visitDecls(link.decl);
        close();
    }

    override void visit(AST.AnonDeclaration anon) {
        open("anon union");
        visitDecls(anon.decl);
        close();
    }

    override void visit(AST.AlignDeclaration d) {
        open("align ");
        super.visit(d.ealign);
        buf.printf(" ");
        visitDecls(d.decl);
        close();
    }

    override void visit(AST.CPPMangleDeclaration mangle) {
        open("cppmangle %d", mangle.cppmangle);
        visitDecls(mangle.decl);
        close();
    }

    override void visit(AST.ProtDeclaration d) {
        open("%s", AST.protectionToChars(d.protection.kind));
        visitDecls(d.decl);
        close();
    }

    override void visit(AST.PragmaDeclaration d) {
        open("pragma %s", d.ident.toChars);
        visitExps(d.args);
        buf.writenl;
        visitDecls(d.decl);
        close();
    }
    
    override void visit(AST.StorageClassDeclaration d) {
        buf.printf("( ");
        AST.stcToBuffer(buf, d.stc);
        buf.level++;
        buf.writenl;
        visitDecls(d.decl);
        close();
    }

    override void visit(AST.ConditionalDeclaration ver) {
        open("version %s ", ver.ident ? ver.ident.toChars : "");
        ver.condition.accept(this);
        buf.printf(" ");
        buf.writenl();
        visitDecls(ver.decl);
        buf.printf(" else ");
        buf.writenl;
        visitDecls(ver.elsedecl);
        close();
    }

    override void visit(AST.DeprecatedDeclaration d) {
        open("deprecated");
        visitDecls(d.decl);
        close();
    }

    override void visit(AST.StaticIfDeclaration sif) {
        open("static if");
        visitDecls(sif.decl);
        buf.printf("else");
        buf.writenl;
        visitDecls(sif.elsedecl);
        close();
    }

    override void visit(AST.EnumMember em) {
        buf.printf("( %s ", em.ident.toChars);
        if (em._init) {
            buf.printf(" ");
            em._init.accept(this);
        }
        buf.printf(" )");
    }

    override void visit(AST.Module) { assert(0); }

    override void visit(AST.StructDeclaration d) {
        open("struct %s", d.ident.toChars);
        visitDecls(d.members);
        close();
    }
    
    override void visit(AST.UnionDeclaration d) {
        open("union %s", d.ident.toChars);
        visitDecls(d.members);
        close();
    }

    override void visit(AST.ClassDeclaration d) {
        open("class %s", d.ident ? d.ident.toChars : "");
        if (d.baseclasses) {
            foreach (i, c; *d.baseclasses) {
                if (i) buf.printf(" ");
                c.type.accept(this);
            }
            buf.writenl;
        }
        visitDecls(d.members);
        close();
    }

    override void visit(AST.InterfaceDeclaration d) {
        open("interface %s", d.ident.toChars);
        visitDecls(d.members);
        close();
    }

    override void visit(AST.TemplateMixin m) {
        open("template mixin");
        visitDecls(m.members);
        close();
    }

    override void visit(AST.Parameter p) { 
        buf.printf("%s ", p.ident ? p.ident.toChars : "anonymous");
        if (p.type) p.type.accept(this);
    }

    override void visit(AST.Statement) { assert(0); }
    override void visit(AST.ImportStatement imp) {
        open("import");
        visitDecls(imp.imports);
        close();
    }

    override void visit(AST.ScopeStatement ss) {
        open("{}");
        if (ss.statement) ss.statement.accept(this);
        close();
    }

    override void visit(AST.ReturnStatement r) {
        buf.printf("(return ");
        if (r.exp) r.exp.accept(this);
        buf.printf(")");
    }

    override void visit(AST.LabelStatement label) {
        open("label %s", label.ident.toChars);
        if (label.statement) label.statement.accept(this);
        close();
    }

    override void visit(AST.StaticAssertStatement st) {
        st.sa.accept(this);
    }

    override void visit(AST.CompileStatement ct) {
        open("compiletime");
        visitExps(ct.exps);
        close();
    }

    override void visit(AST.WhileStatement st) {
        open("while");
        if(st.condition) st.condition.accept(this);
        buf.writenl;
        if(st._body) st._body.accept(this);
        close();
    }

    override void visit(AST.ForStatement st) {
        open("for");
        if (st._init) st._init.accept(this);
        buf.writenl;
        if (st.condition) st.condition.accept(this);
        buf.writenl;
        if (st.increment) st.increment.accept(this);
        buf.writenl;
        if (st._body) st._body.accept(this);
        close();    
    }

    override void visit(AST.DoStatement st) {
        open("do");
        if(st._body) st._body.accept(this);
        buf.writenl;
        if(st.condition) st.condition.accept(this);
        close();
    }
    
    override void visit(AST.ForeachRangeStatement st) {
        open("%s", Token.toChars(st.op));
        if (st.prm) st.prm.accept(this);
        buf.writenl;
        if (st.lwr) st.lwr.accept(this);
        if (st.upr) {
            buf.printf("..");
            st.upr.accept(this);
        }
        if(st._body) st._body.accept(this);
        buf.writenl;
        close();
    }

    override void visit(AST.ForeachStatement st) {
        open("%s", Token.toChars(st.op));
        if (st.parameters)  {
            foreach (prm; *st.parameters)
                prm.accept(this);
        }
        buf.writenl;
        if (st.aggr) st.aggr.accept(this);
        buf.writenl;
        if(st._body) st._body.accept(this);
        buf.writenl;
        close();
    }

    override void visit(AST.IfStatement st) {
        open("if");
        if (st.prm) st.prm.accept(this);
        buf.writenl;
        if (st.condition) st.condition.accept(this);
        buf.writenl;
        if (st.ifbody) st.ifbody.accept(this);
        buf.writenl;
        if (st.elsebody) {
            open("else");
            st.elsebody.accept(this);
            close();
        }
        close();
    }

    override void visit(AST.ScopeGuardStatement sgt) {
        open("scope %s ", Token.toChars(sgt.tok));
        if (sgt.statement) sgt.statement.accept(this);
        close();
    }

    override void visit(AST.ConditionalStatement st) {
        open("static if");
        if (st.condition) st.condition.accept(this);
        buf.writenl;
        if (st.ifbody) st.ifbody.accept(this);
        buf.writenl;
        if (st.elsebody) {
            open("else");
            st.elsebody.accept(this);
            close();
        }
        close();
    }

    override void visit(AST.PragmaStatement st) {
        buf.printf("( pragma %s", st.ident.toChars);
        visitExps(st.args);
        buf.printf(")");
    }

    override void visit(AST.SwitchStatement sw) {
        open("%s switch", sw.isFinal ? cast(const(char)*)"final" : "");
        if (sw._body) sw._body.accept(this);
        close();
    }

    override void visit(AST.CaseRangeStatement st) {
        open("case ");
        st.first.accept(this);
        buf.printf(" .. ");
        buf.printf(" ");
        st.last.accept(this);
        buf.writenl;
        st.statement.accept(this);
        close();
    }

    override void visit(AST.CaseStatement ct) {
        open("case ");
        ct.exp.accept(this);
        buf.writenl;
        if (ct.statement) ct.statement.accept(this);
        close();
    }

    override void visit(AST.DefaultStatement def) {
        open("default");
        if(def.statement) def.statement.accept(this);
        close();
    }

    override void visit(AST.BreakStatement brk) {
        buf.printf("( break %s )", brk.ident ? brk.ident.toChars : "");
        buf.writenl;
    }

    override void visit(AST.ContinueStatement cont) {
        buf.printf("( continue %s )", cont.ident ? cont.ident.toChars : "");
        buf.writenl;
    }

    override void visit(AST.GotoDefaultStatement gds) {
        buf.printf("( goto default )");
        buf.writenl;
    }

    override void visit(AST.GotoCaseStatement gcs) {
        buf.printf("( goto case ");
        if (gcs.exp) gcs.exp.accept(this);
        buf.printf(")");
        buf.writenl;
    }

    override void visit(AST.GotoStatement gs) {
        buf.printf("( goto %s)", gs.ident.toChars);
        buf.writenl;
    }

    override void visit(AST.SynchronizedStatement sync) {
        open("synchronized");
        if (sync._body) sync._body.accept(this);
        close();
    }

    override void visit(AST.WithStatement w) { 
        open("with");
        if (w.exp) w.exp.accept(this);
        buf.writenl;
        if (w._body) w._body.accept(this);
        close();
    }

    override void visit(AST.TryCatchStatement tc) {
        open("try");
        if (tc._body) tc._body.accept(this);
        if (tc.catches)
            foreach (c; *tc.catches) {
                open("catch");
                if (c.type) {
                    c.type.accept(this);
                    buf.printf(" ");
                }
                if (c.ident) buf.printf("%s", c.ident.toChars);
                buf.writenl;
                if (c.handler) c.handler.accept(this);
                close();
            }
        close();
    }

    override void visit(AST.TryFinallyStatement tf) {
        open("try");
        if (tf._body) tf._body.accept(this);
        if (tf.finalbody) {
            open("finally");
            tf.finalbody.accept(this);
            close();
        }
        close();
    }

    override void visit(AST.ThrowStatement thr) { 
        open("throw");
        thr.exp.accept(this);
        close();
    }

    override void visit(AST.AsmStatement ast) {
        auto t = ast.tokens;
        while(t) {
            buf.printf("%s", t.toChars);
            t = t.next;
        }
    }

    override void visit(AST.ExpStatement s) {
        buf.printf("( expr ");
        buf.writenl;
        buf.level++;
        s.exp.accept(this);
        buf.level--;
        buf.writenl;
        buf.printf(")");
    }

    override void visit(AST.CompoundStatement s) {
        open("");
        visitStatements(s.statements);
        close();
    }

    override void visit(AST.CompoundDeclarationStatement s) {
        visitStatements(s.statements);
    }

    override void visit(AST.CompoundAsmStatement st) {
        auto stc = st.stc;
        open("asm %s", AST.stcToChars(stc));
        visitStatements(st.statements);
        close();
    }

    override void visit(AST.InlineAsmStatement iasm) {
        visit(cast(AST.AsmStatement)iasm);
    }

    override void visit(AST.Type t) {
        assert(0, "unknown type?");
    }

    override void visit(AST.TypeBasic t) { 
        buf.printf("%s %s", modToChars(t.mod), t.dstring);
    }

    override void visit(AST.TypeError) {
        buf.printf("terror");
    }

    override void visit(AST.TypeNull) {
        buf.printf("typeof(null)");
    }

    override void visit(AST.TypeVector t) {
        buf.printf("( __vector ");
        if (t.basetype) t.basetype.accept(this);
        buf.printf(")");
    }

    override void visit(AST.TypeEnum t) {
        buf.printf("enum %s ", t.sym.toChars);
        t.sym.memtype.accept(this);
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
        ts.lwr.accept(this);
        buf.printf(" ");
        ts.upr.accept(this);
        buf.printf(" ");
        ts.next.accept(this);
        buf.printf(")");
    }

    override void visit(AST.TypeDelegate td) {
        buf.printf("delegate ");
        if (td.next) td.next.accept(this);
    }

    override void visit(AST.TypePointer tp) {
        tp.next.accept(this);
        buf.printf("*");
    }

    override void visit(AST.TypeFunction tf) {
        if (tf.next) {
            tf.next.accept(this);
            buf.printf(" ");
        }
        foreach (i, p; *tf.parameterList.parameters) {
            if (i) buf.printf(" ");
            p.accept(this);
        }
    }
    
    override void visit(AST.TypeArray) {
        assert(0, "Unexpected generic array");
    }

    override void visit(AST.TypeDArray d) {
        d.next.accept(this);
        buf.printf("[]");
    }
    
    override void visit(AST.TypeAArray ta) {
        ta.next.accept(this);
        buf.printf("[");
        ta.index.accept(this);
        buf.printf("]");
    }

    override void visit(AST.TypeSArray tsa) {
        tsa.next.accept(this);
        buf.printf("[");
        tsa.dim.accept(this);
        buf.printf("]");
    }

    override void visit(AST.TypeQualified) { assert(0); }

    override void visit(AST.TypeTraits tt) {
        buf.printf("type __traits(");
        tt.exp.accept(this);
        buf.printf(")");
    }

    override void visit(AST.TypeIdentifier d) {
        buf.printf("%s", d.ident.toChars);
    }

    override void visit(AST.TypeReturn) {
        buf.printf("typeof(return)");
    }

    override void visit(AST.TypeTypeof tt) {
        buf.printf("typeof(");
        if (tt.exp) tt.exp.accept(this);
        buf.printf(")");
    }
    
    override void visit(AST.TypeInstance ti) {    
        buf.printf("%s!(", ti.tempinst.tempdecl ? ti.tempinst.tempdecl.toChars : ti.tempinst.name.toChars);
        visitTiargs(ti.tempinst.tiargs);
        buf.printf(")");
    }

    override void visit(AST.Expression) { 
        assert(0); 
    }

    override void visit(AST.DeclarationExp e) {
        if (e.declaration) e.declaration.accept(this);
    }

    override void visit(AST.IntegerExp e) {
        buf.printf("%lld", cast(long)e.value);
    }

    override void visit(AST.NewAnonClassExp nc) { 
        buf.printf("( new anonclass %s ", nc.cd.ident.toChars);
        visitExps(nc.arguments);
        nc.cd.accept(this);
        buf.printf(")");
    }
    
    override void visit(AST.IsExp ie) { 
        buf.printf("( is %s", Token.toChars(ie.tok));
        if (ie.id) buf.printf("%s ", ie.id.toChars);
        if (ie.type) ie.type.accept(this);
    }

    override void visit(AST.RealExp r) {
        buf.printf("%llf", r.value);
    }
    
    override void visit(AST.NullExp) {
        buf.printf("null");
    }

    override void visit(AST.TypeidExp tie) {
        switch(tie.obj.dyncast()){
            case DYNCAST.expression:
                return (cast(AST.Expression)tie.obj).accept(this);
            case DYNCAST.dsymbol:
                return (cast(AST.Dsymbol)tie.obj).accept(this);
            case DYNCAST.type:
                return (cast(AST.Type)tie.obj).accept(this);
            default:
                buf.printf("<typeid>");
        }
    }

    override void visit(AST.TraitsExp te) {
        buf.printf("( __traits %s ", te.ident.toChars);
        if (te.args) {
            foreach(arg; *te.args) {
                switch(arg.dyncast) {
                    case DYNCAST.expression:
                        return (cast(AST.Expression)arg).accept(this);
                    case DYNCAST.dsymbol:
                        return (cast(AST.Dsymbol)arg).accept(this);
                    case DYNCAST.type:
                        return (cast(AST.Type)arg).accept(this);
                    default:
                        buf.printf("%s", arg.toChars);
                }
            }
        }
    }
    
    override void visit(AST.StringExp exp) {
        if (exp.type) exp.type.accept(this);
        if (exp.sz == 1)
            buf.printf(`"""%.*s"""`, exp.len, exp.string);
        else if (exp.sz == 2) {
            buf.printf(`"""%.*s"""`, exp.len, exp.wstring);
        }
        else if (exp.sz == 4) {
            buf.printf(`"""%.*s"""`, exp.len, exp.dstring);
        }
    }
    
    override void visit(AST.NewExp ne) {  
        buf.printf("( new ");
        visitExps(ne.arguments);
        buf.printf(")");
    }
    
    override void visit(AST.AssocArrayLiteralExp aa) {
        buf.printf("( key[value] ");
        if (aa.keys)
            foreach (i, key; *aa.keys) {
                if (i) buf.printf(" ");
                auto v = (*aa.values)[i];
                key.accept(this);
                buf.printf(" ");
                v.accept(this);
            }
        buf.printf(")");
    }
    
    override void visit(AST.ArrayLiteralExp ae) {     
        buf.printf("( [] ");
        if (ae.elements)
            foreach (i, el; *ae.elements) {
                if (i) buf.printf(" ");
                auto e = el ? el : ae.basis;
                if (e) {
                    e.accept(this);
                }
            }
        buf.printf(")");
    }

    override void visit(AST.FuncExp fe) {
        open("func-expr");
        if (fe.fd) fe.fd.accept(this);
        else if (fe.td) fe.td.accept(this);
        close();
    }

    override void visit(AST.IntervalExp ival) {
        ival.lwr.accept(this);
        buf.printf(" .. ");
        if (ival.upr) ival.upr.accept(this);
        else buf.printf("$");
    }

    override void visit(AST.TypeExp te) {
        if (te.type) te.type.accept(this);
    }

    override void visit(AST.ScopeExp s) {
        open("{}");
        if (s.sds) s.sds.accept(this);
        close();
    }
    
    override void visit(AST.IdentifierExp e) {
        buf.printf("%s", e.ident.toChars);
    }
    
    override void visit(AST.UnaExp e) { 
        buf.printf("%s", Token.toChars(e.op));
        e.e1.accept(this);
    }

    override void visit(AST.DefaultInitExp ie) { 
        if (ie.type) ie.type.accept(this);
        buf.printf(" init");
    }

    override void visit(AST.BinExp e) {
        buf.printf("( %s ", Token.toChars(e.op));
        e.e1.accept(this);
        buf.printf(" ");
        e.e2.accept(this);
        buf.printf(")");
    }
    
    override void visit(AST.DsymbolExp e) { 
        buf.printf("%s", e.s.ident.toChars);
    }
    
    override void visit(AST.TemplateExp e) {
        assert(0);
    }
    
    override void visit(AST.SymbolExp e) {
        buf.printf("( symbol ");
        e.type.accept(this);
        buf.printf(" %s", e.var.ident.toChars);
        buf.printf(")");
    }
    
    override void visit(AST.VarExp e) {
        buf.printf("( var ");
        e.type.accept(this);
        buf.printf(" %s", e.var.ident.toChars);
        buf.printf(")");
    }
    
    override void visit(AST.TupleExp e) { 
        open("tuple-exp");
        visitExps(e.exps);
        close();  
    }

    override void visit(AST.DollarExp e) { buf.printf("$");  }
    override void visit(AST.ThisExp e) { buf.printf("this");  }
    override void visit(AST.SuperExp e) { buf.printf("super");  }
    override void visit(AST.AddrExp e) { visit(cast(AST.UnaExp) e);  }
    override void visit(AST.PreExp e) { visit(cast(AST.UnaExp) e);  }
    override void visit(AST.PtrExp e) { visit(cast(AST.UnaExp) e);  }
    override void visit(AST.NegExp e) { visit(cast(AST.UnaExp) e);  }
    override void visit(AST.UAddExp e) { visit(cast(AST.UnaExp) e);  }
    override void visit(AST.NotExp e) { visit(cast(AST.UnaExp) e);  }

    override void visit(AST.ComExp e) { visit(cast(AST.UnaExp)e); }
    override void visit(AST.DeleteExp e) { visit(cast(AST.UnaExp)e); }
    
    override void visit(AST.CastExp e) {
        buf.printf("( cast ");
        if (e.to) {
            e.to.accept(this);
            buf.printf(" ");
        }
        e.e1.accept(this);
        buf.printf(")");
    }

    override void visit(AST.CallExp call) {
        buf.printf("( call ");
        if (call.e1) {
            call.e1.accept(this);
            buf.printf(" ");
        }
        visitExps(call.arguments);
        buf.printf(")");
    }

    override void visit(AST.DotIdExp e) {
        buf.printf("( . ");
        e.e1.accept(this);
        buf.printf(" %s", e.ident.toChars);
        buf.printf(")");
    }
    
    override void visit(AST.AssertExp e) {
        buf.printf("( assert ");
        e.e1.accept(this);
        if (e.msg) {
            buf.printf(" ");
            e.msg.accept(this);
        }
        buf.printf(")");
    }

    override void visit(AST.CompileExp c) {
        buf.printf("( mixin ");
        visitExps(c.exps);
        buf.printf(")");
    }

    override void visit(AST.ImportExp ie) {
        visit(cast(AST.UnaExp)ie);
    }
    
    override void visit(AST.DotTemplateInstanceExp e) {
        buf.printf("( . ");
        e.e1.accept(this);
        buf.printf(" %s", e.ti.ident ? e.ti.ident.toChars : e.ti.name.toChars);
        buf.printf(")");
    }

    override void visit(AST.ArrayExp arr) {
        if (arr.e1) arr.e1.accept(this);
        if (arr.arguments)
            foreach(arg; *arr.arguments) {
                arg.accept(this);
            }        
    }

    override void visit(AST.FuncInitExp e) {
        buf.printf("func-init ");
        visit(cast(AST.DefaultInitExp)e);
    }

    override void visit(AST.PrettyFuncInitExp e) {
        buf.printf("pretty-init ");
        visit(cast(AST.DefaultInitExp)e);
    }

    override void visit(AST.FileInitExp e) {
        buf.printf("file-init ");
        visit(cast(AST.DefaultInitExp)e);
    }

    override void visit(AST.LineInitExp e) {
        buf.printf("line-init ");
        visit(cast(AST.DefaultInitExp)e);
    }

    override void visit(AST.ModuleInitExp e) {
        buf.printf("module-init ");
        visit(cast(AST.DefaultInitExp)e);
    }

    override void visit(AST.CommaExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.PostExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.PowExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.MulExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.DivExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.ModExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.AddExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.MinExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.CatExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.ShlExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.ShrExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.UshrExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.EqualExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.InExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.IdentityExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.CmpExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.AndExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.XorExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.OrExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.LogicalExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.CondExp e) { 
        buf.printf("( ? ");
        if (e.econd) e.econd.accept(this);
        if (e.e1) e.e1.accept(this);
        if (e.e2) e.e2.accept(this);
        buf.printf(")");
    }
    override void visit(AST.AssignExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.BinAssignExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.AddAssignExp e) { visit(cast(AST.BinExp )e); } 
    override void visit(AST.MinAssignExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.MulAssignExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.DivAssignExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.ModAssignExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.PowAssignExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.AndAssignExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.OrAssignExp e) { visit(cast(AST.BinExp )e); } 
    override void visit(AST.XorAssignExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.ShlAssignExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.ShrAssignExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.UshrAssignExp e) { visit(cast(AST.BinExp )e); }
    override void visit(AST.CatAssignExp e) { visit(cast(AST.BinExp )e); }

    override void visit(AST.TemplateParameter tp) { assert(0); }

    override void visit(AST.TemplateAliasParameter tp) {
        buf.printf("alias %s", tp.ident.toChars);
    }

    override void visit(AST.TemplateTypeParameter tp) {

    }
    
    override void visit(AST.TemplateTupleParameter tp) {
        buf.printf("alias %s", tp.ident.toChars);
    }

    override void visit(AST.TemplateValueParameter tv) {
        buf.printf("template-value ");
        tv.valType.accept(this);
        if (tv.specValue) {
            buf.printf(" ");
            tv.specValue.accept(this);
        }
        if (tv.defaultValue) {
            buf.printf(" default ");
            tv.specValue.accept(this);
        }
    }

    override void visit(AST.TemplateThisParameter tp) {
        buf.printf("template-this");
    }

    override void visit(AST.Condition) { assert(0); }

    override void visit(AST.StaticForeachStatement sfst) {
        if (sfst.sfe.aggrfe) sfst.sfe.aggrfe.accept(this);
        else sfst.sfe.rangefe.accept(this);
    }
    
    override void visit(AST.StaticIfCondition cond) {
        cond.exp.accept(this);
    }

    override void visit(AST.DVCondition) { assert(0); }
    
    override void visit(AST.DebugCondition d) {
        buf.printf("debug %s", d.ident ? d.ident.toChars : "");
    }

    override void visit(AST.VersionCondition ver) {
        buf.printf("%s", ver.ident.toChars);
    }
    
    override void visit(AST.Initializer) { assert(0); }
    
    override void visit(AST.ExpInitializer ei) {
        if (ei.exp)
            ei.exp.accept(this);
    }

    override void visit(AST.StructInitializer si) {
        buf.printf("( struct-init ");
        foreach (i, const id; si.field)
        {
            if (i)
                buf.printf(" ");
            if (id)
            {
                buf.writestring(id.toString());
                buf.writeByte(':');
            }
            if (auto iz = si.value[i])
                iz.accept(this);
        }
        buf.printf(")");
    }
    
    override void visit(AST.ArrayInitializer ai) {
        buf.printf("( array-init ");
        foreach (i, v; ai.value) {
            if (i) buf.printf(" ");
            if (v) v.accept(this);
            else buf.printf("null");
        }
        buf.printf(")");
    }

    override void visit(AST.VoidInitializer) { 
        buf.printf("void");
    }
}

int main(string[] args) {
	string outdir = ".";
    string tool = "lex";
	auto res = getopt(args,
		"outdir", "output directory", &outdir,
        "tool", "select tool - lex or lispy", &tool
	);
	if (res.helpWanted) {
		defaultGetoptPrinter("Trivial D lexer based on DMD.", res.options);
		return 1;
	}
    global.params.isLinux = true;
    global.params.useUnitTests = true;
    global._init();
    ASTBase.Type._init();
    Id.initialize();
	foreach(arg; args[1..$]) {
        if (tool == "lex")
		    processFile!lex(arg, outdir, "tk");
        else if(tool == "lispy")
            processFile!lispy(arg, outdir, "ast");        
        else {
            fprintf(stderr, "Unsupported tool name: %.*s", tool.length, tool.ptr);
            return 2;
        }
	}
	return 0;
}

char[] lex(const(char)* argz, const(char)[] buf) {
    auto lexer = new Lexer(argz, cast(char*)buf.ptr, 0, buf.length, true, true, new StderrDiagnosticReporter(DiagnosticReporting.error));
    auto output = new OutBuffer();
    int i = 0;
    while (lexer.nextToken() != TOK.endOfFile) {
        output.printf("%4d", lexer.token.value);
        if (++i == 20) {
            output.printf(" | Line %5d |\n", lexer.token.loc.linnum);
            i  = 0;
        }
    }
    if (i != 0) output.printf(" | Line %5d |\n", lexer.token.loc.linnum);
    return output.extractSlice;
}

char[] lispy(const(char)* argz, const(char)[] buf) {
    scope diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
    auto mod = new AST.Module(argz, Identifier.idPool(argz[0..strlen(argz)-2]), 1, 0);
    scope p = new Parser!ASTBase(mod, buf, true, diagnosticReporter);
    p.nextToken();
    auto decls = p.parseModule();
    auto lispPrint = new LispyPrint();
    lispPrint.buf = new OutBuffer();
    lispPrint.buf.doindent = true;
    foreach (d; *decls)
        d.accept(lispPrint);
    return lispPrint.buf.extractSlice();
}

void processFile(alias fn)(string arg, string outdir, const(char)* suffix) {
    auto argz = arg.toStringz;
    auto buffer = File.read(argz);
    if (!buffer.success) {
        fprintf(stderr, "Failed to read from file: %s", argz);
        exit(2);
    }
    auto buf = buffer.extractData();
    auto dest = FileName.forceExt(FileName.name(argz), suffix);
    auto filePath = outdir ~ "/" ~ dest[0..strlen(dest)];
    auto output = fn(argz, cast(char[])buf);
    if (!File.write(filePath.toStringz, output))
        fprintf(stderr, "Failed to write file: %s\n", filePath.toStringz);
}