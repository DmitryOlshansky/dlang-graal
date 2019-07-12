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
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.lexer.*;
import static org.dlang.dmd.parse.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.transitivevisitor.*;

public class dtool {

    public static BytePtr modToChars(int mod) {
        switch (mod)
        {
            case 1:
                return new BytePtr("const");
            case 4:
                return new BytePtr("immutable");
            case 2:
                return new BytePtr("shared");
            case 8:
                return new BytePtr("inout");
            case 9:
                return new BytePtr("const(inout)");
            case 16:
                return new BytePtr("");
            default:
            return new BytePtr("");
        }
    }

    public static BytePtr linkToChars(int link) {
        switch (link)
        {
            case 0:
                return new BytePtr("");
            case 1:
                return new BytePtr("extern(D)");
            case 2:
                return new BytePtr("extern(C)");
            case 3:
                return new BytePtr("extern(C++)");
            case 4:
                return new BytePtr("extern(Windows)");
            case 5:
                return new BytePtr("extern(Pascal)");
            case 6:
                return new BytePtr("extern(Obj-C)");
            case 7:
                return new BytePtr("extern(System)");
            default:
            throw new AssertionError("Unreachable code!");
        }
    }

    public static class LispyPrint extends ParseTimeTransitiveVisitorASTBase
    {
        public Ptr<OutBuffer> buf = null;
        public  void open(BytePtr format, Object... ap) {
            (this.buf.get()).writestring(new ByteSlice("( "));
            (this.buf.get()).vprintf(format, new Slice<>(ap));
            (this.buf.get()).level++;
            (this.buf.get()).writenl();
        }

        public  void close() {
            (this.buf.get()).level--;
            (this.buf.get()).writenl();
            (this.buf.get()).printf(new BytePtr(")"));
            (this.buf.get()).writenl();
        }

        public  void visitDecls(Ptr<DArray<ASTBase.Dsymbol>> decls) {
            if (decls != null)
            {
                {
                    Slice<ASTBase.Dsymbol> __r262 = (decls.get()).opSlice().copy();
                    int __key263 = 0;
                    for (; (__key263 < __r262.getLength());__key263 += 1) {
                        ASTBase.Dsymbol m = __r262.get(__key263);
                        m.accept(this);
                    }
                }
            }
        }

        public  void visitExps(Ptr<DArray<ASTBase.Expression>> exps) {
            if (exps != null)
            {
                {
                    Slice<ASTBase.Expression> __r265 = (exps.get()).opSlice().copy();
                    int __key264 = 0;
                    for (; (__key264 < __r265.getLength());__key264 += 1) {
                        ASTBase.Expression e = __r265.get(__key264);
                        int i = __key264;
                        if (i != 0)
                            (this.buf.get()).printf(new BytePtr(" "));
                        e.accept(this);
                    }
                }
            }
        }

        public  void visitStatements(Ptr<DArray<ASTBase.Statement>> statements) {
            if (statements != null)
            {
                Slice<ASTBase.Statement> __r267 = (statements.get()).opSlice().copy();
                int __key266 = 0;
                for (; (__key266 < __r267.getLength());__key266 += 1) {
                    ASTBase.Statement st = __r267.get(__key266);
                    int i = __key266;
                    if (i != 0)
                        (this.buf.get()).writenl();
                    st.accept(this);
                }
            }
        }

        public  void visitTiargs(Ptr<DArray<RootObject>> tiargs) {
            if (tiargs != null)
            {
                Slice<RootObject> __r269 = (tiargs.get()).opSlice().copy();
                int __key268 = 0;
                for (; (__key268 < __r269.getLength());__key268 += 1) {
                    RootObject m = __r269.get(__key268);
                    int i = __key268;
                    if (i != 0)
                        (this.buf.get()).printf(new BytePtr(" "));
                    switch (m.dyncast())
                    {
                        case DYNCAST.expression:
                            ((ASTBase.Expression)m).accept(this);
                            break;
                        case DYNCAST.type:
                            ((ASTBase.Type)m).accept(this);
                            break;
                        default:
                        (this.buf.get()).printf(new BytePtr("%s"), m.toChars());
                    }
                }
            }
        }

        public  void visit(ASTBase.Dsymbol s) {
            (this.buf.get()).printf(new BytePtr("%s"), s.toChars());
        }

        public  void visit(ASTBase.AliasThis a) {
            super.visit(a);
        }

        public  void visit(ASTBase.Declaration d) {
            (this.buf.get()).printf(new BytePtr("%s"), d.toChars());
        }

        public  void visit(ASTBase.ScopeDsymbol scd) {
            (this.buf.get()).printf(new BytePtr("%s"), scd.toChars());
        }

        public  void visit(ASTBase.Import imp) {
            (this.buf.get()).printf(new BytePtr("import %s"), imp.toChars());
        }

        public  void visit(ASTBase.AttribDeclaration attr) {
            this.open(new BytePtr("%s"), attr.toChars());
            this.visitDecls(attr.decl);
            this.close();
        }

        public  void visit(ASTBase.StaticAssert as) {
            this.open(new BytePtr("static assert"));
            as.exp.accept(this);
            this.close();
        }

        public  void visit(ASTBase.DebugSymbol sym) {
            (this.buf.get()).printf(new BytePtr("debug"));
        }

        public  void visit(ASTBase.VersionSymbol ver) {
            (this.buf.get()).printf(new BytePtr("version"));
        }

        public  void visit(ASTBase.VarDeclaration d) {
            this.open(new BytePtr("var %s "), d.ident.toChars());
            if (d.type != null)
                d.type.accept(this);
            if (d._init != null)
            {
                (this.buf.get()).printf(new BytePtr(" "));
                d._init.accept(this);
            }
            this.close();
        }

        public  void visit(ASTBase.FuncDeclaration d) {
            this.open(new BytePtr("func %s"), d.ident.toChars());
            if (d.type != null)
                d.type.accept(this);
            if (d.fbody != null)
                d.fbody.accept(this);
            this.close();
        }

        public  void visit(ASTBase.AliasDeclaration d) {
            this.open(new BytePtr("alias %s "), d.ident.toChars());
            if (d.aliassym != null)
            {
                d.aliassym.accept(this);
                (this.buf.get()).printf(new BytePtr(" "));
            }
            if (d.type != null)
                d.type.accept(this);
            this.close();
        }

        public  void visit(ASTBase.TupleDeclaration d) {
            this.open(new BytePtr("tuple"));
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.FuncLiteralDeclaration d) {
            this.open(new BytePtr("func literal %s"), d.ident.toChars());
            if (d.fbody != null)
                d.fbody.accept(this);
            this.close();
        }

        public  void visit(ASTBase.PostBlitDeclaration d) {
            this.open(new BytePtr("this(this)"));
            if (d.fbody != null)
                d.fbody.accept(this);
            this.close();
        }

        public  void visit(ASTBase.CtorDeclaration d) {
            this.open(new BytePtr("ctor"));
            if (d.fbody != null)
                d.fbody.accept(this);
            this.close();
        }

        public  void visit(ASTBase.DtorDeclaration d) {
            this.open(new BytePtr("dtor"));
            if (d.fbody != null)
                d.fbody.accept(this);
            this.close();
        }

        public  void visit(ASTBase.InvariantDeclaration d) {
            this.open(new BytePtr("invariant"));
            if (d.fbody != null)
                d.fbody.accept(this);
            this.close();
        }

        public  void visit(ASTBase.UnitTestDeclaration d) {
            this.open(new BytePtr("unittest"));
            if (d.fbody != null)
                d.fbody.accept(this);
            this.close();
        }

        public  void visit(ASTBase.NewDeclaration d) {
            this.open(new BytePtr("newdecl"));
            if (d.fbody != null)
                d.fbody.accept(this);
            this.close();
        }

        public  void visit(ASTBase.DeleteDeclaration d) {
            this.open(new BytePtr("deletedecl"));
            if (d.fbody != null)
                d.fbody.accept(this);
            this.close();
        }

        public  void visit(ASTBase.StaticCtorDeclaration d) {
            this.open(new BytePtr("static this"));
            if (d.fbody != null)
                d.fbody.accept(this);
            this.close();
        }

        public  void visit(ASTBase.StaticDtorDeclaration d) {
            this.open(new BytePtr("static ~this"));
            if (d.fbody != null)
                d.fbody.accept(this);
            this.close();
        }

        public  void visit(ASTBase.SharedStaticCtorDeclaration d) {
            this.open(new BytePtr("shared static this"));
            if (d.fbody != null)
                d.fbody.accept(this);
            this.close();
        }

        public  void visit(ASTBase.SharedStaticDtorDeclaration d) {
            this.open(new BytePtr("shared static ~this"));
            if (d.fbody != null)
                d.fbody.accept(this);
            this.close();
        }

        public  void visit(ASTBase.Package d) {
            this.open(new BytePtr("package"));
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.EnumDeclaration d) {
            this.open(new BytePtr("enum "));
            if (d.ident != null)
                (this.buf.get()).printf(new BytePtr("%s"), d.ident.toChars());
            this.visitDecls(d.members);
            this.close();
        }

        public  void visit(ASTBase.AggregateDeclaration d) {
            this.open(new BytePtr("aggregate %s"), d.ident.toChars());
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.TemplateDeclaration d) {
            this.open(new BytePtr("template %s"), d.ident.toChars());
            this.visitDecls(d.members);
            this.close();
        }

        public  void visit(ASTBase.TemplateInstance ti) {
            this.open(new BytePtr("template instance %s"), ti.ident != null ? ti.ident.toChars() : ti.name.toChars());
            this.visitTiargs(ti.tiargs);
            this.visitDecls(ti.members);
            this.close();
        }

        public  void visit(ASTBase.Nspace n) {
            this.open(new BytePtr("nspace %s"), n.ident.toChars());
            super.visit(n);
            this.close();
        }

        public  void visit(ASTBase.CompileDeclaration d) {
            this.open(new BytePtr("compiletime"));
            this.visitDecls(d.decl);
            this.close();
        }

        public  void visit(ASTBase.UserAttributeDeclaration d) {
            this.open(new BytePtr("udas"));
            this.visitDecls(d.decl);
            this.close();
        }

        public  void visit(ASTBase.LinkDeclaration link) {
            this.open(new BytePtr("%s"), linkToChars(link.linkage));
            this.visitDecls(link.decl);
            this.close();
        }

        public  void visit(ASTBase.AnonDeclaration anon) {
            this.open(new BytePtr("anon union"));
            this.visitDecls(anon.decl);
            this.close();
        }

        public  void visit(ASTBase.AlignDeclaration d) {
            this.open(new BytePtr("align "));
            super.visit(d.ealign);
            (this.buf.get()).printf(new BytePtr(" "));
            this.visitDecls(d.decl);
            this.close();
        }

        public  void visit(ASTBase.CPPMangleDeclaration mangle) {
            this.open(new BytePtr("cppmangle %d"), mangle.cppmangle);
            this.visitDecls(mangle.decl);
            this.close();
        }

        public  void visit(ASTBase.ProtDeclaration d) {
            this.open(new BytePtr("%s"), ASTBase.protectionToChars(d.protection.kind));
            this.visitDecls(d.decl);
            this.close();
        }

        public  void visit(ASTBase.PragmaDeclaration d) {
            this.open(new BytePtr("pragma %s"), d.ident.toChars());
            this.visitExps(d.args);
            (this.buf.get()).writenl();
            this.visitDecls(d.decl);
            this.close();
        }

        public  void visit(ASTBase.StorageClassDeclaration d) {
            (this.buf.get()).printf(new BytePtr("( "));
            ASTBase.stcToBuffer(this.buf, d.stc);
            (this.buf.get()).level++;
            (this.buf.get()).writenl();
            this.visitDecls(d.decl);
            this.close();
        }

        public  void visit(ASTBase.ConditionalDeclaration ver) {
            this.open(new BytePtr("version %s "), ver.ident != null ? ver.ident.toChars() : new BytePtr(""));
            ver.condition.accept(this);
            (this.buf.get()).printf(new BytePtr(" "));
            (this.buf.get()).writenl();
            this.visitDecls(ver.decl);
            (this.buf.get()).printf(new BytePtr(" else "));
            (this.buf.get()).writenl();
            this.visitDecls(ver.elsedecl);
            this.close();
        }

        public  void visit(ASTBase.DeprecatedDeclaration d) {
            this.open(new BytePtr("deprecated"));
            this.visitDecls(d.decl);
            this.close();
        }

        public  void visit(ASTBase.StaticIfDeclaration sif) {
            this.open(new BytePtr("static if"));
            this.visitDecls(sif.decl);
            (this.buf.get()).printf(new BytePtr("else"));
            (this.buf.get()).writenl();
            this.visitDecls(sif.elsedecl);
            this.close();
        }

        public  void visit(ASTBase.EnumMember em) {
            (this.buf.get()).printf(new BytePtr("( %s "), em.ident.toChars());
            if (em._init != null)
            {
                (this.buf.get()).printf(new BytePtr(" "));
                em._init.accept(this);
            }
            (this.buf.get()).printf(new BytePtr(" )"));
        }

        public  void visit(ASTBase.Module _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.StructDeclaration d) {
            this.open(new BytePtr("struct %s"), d.ident.toChars());
            this.visitDecls(d.members);
            this.close();
        }

        public  void visit(ASTBase.UnionDeclaration d) {
            this.open(new BytePtr("union %s"), d.ident.toChars());
            this.visitDecls(d.members);
            this.close();
        }

        public  void visit(ASTBase.ClassDeclaration d) {
            this.open(new BytePtr("class %s"), d.ident != null ? d.ident.toChars() : new BytePtr(""));
            if (d.baseclasses != null)
            {
                {
                    Slice<Ptr<ASTBase.BaseClass>> __r271 = (d.baseclasses.get()).opSlice().copy();
                    int __key270 = 0;
                    for (; (__key270 < __r271.getLength());__key270 += 1) {
                        Ptr<ASTBase.BaseClass> c = __r271.get(__key270);
                        int i = __key270;
                        if (i != 0)
                            (this.buf.get()).printf(new BytePtr(" "));
                        (c.get()).type.accept(this);
                    }
                }
                (this.buf.get()).writenl();
            }
            this.visitDecls(d.members);
            this.close();
        }

        public  void visit(ASTBase.InterfaceDeclaration d) {
            this.open(new BytePtr("interface %s"), d.ident.toChars());
            this.visitDecls(d.members);
            this.close();
        }

        public  void visit(ASTBase.TemplateMixin m) {
            this.open(new BytePtr("template mixin"));
            this.visitDecls(m.members);
            this.close();
        }

        public  void visit(ASTBase.Parameter p) {
            (this.buf.get()).printf(new BytePtr("%s "), p.ident != null ? p.ident.toChars() : new BytePtr("anonymous"));
            if (p.type != null)
                p.type.accept(this);
        }

        public  void visit(ASTBase.Statement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ImportStatement imp) {
            this.open(new BytePtr("import"));
            this.visitDecls(imp.imports);
            this.close();
        }

        public  void visit(ASTBase.ScopeStatement ss) {
            this.open(new BytePtr("{}"));
            if (ss.statement != null)
                ss.statement.accept(this);
            this.close();
        }

        public  void visit(ASTBase.ReturnStatement r) {
            (this.buf.get()).printf(new BytePtr("(return "));
            if (r.exp != null)
                r.exp.accept(this);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.LabelStatement label) {
            this.open(new BytePtr("label %s"), label.ident.toChars());
            if (label.statement != null)
                label.statement.accept(this);
            this.close();
        }

        public  void visit(ASTBase.StaticAssertStatement st) {
            st.sa.accept(this);
        }

        public  void visit(ASTBase.CompileStatement ct) {
            this.open(new BytePtr("compiletime"));
            this.visitExps(ct.exps);
            this.close();
        }

        public  void visit(ASTBase.WhileStatement st) {
            this.open(new BytePtr("while"));
            if (st.condition != null)
                st.condition.accept(this);
            (this.buf.get()).writenl();
            if (st._body != null)
                st._body.accept(this);
            this.close();
        }

        public  void visit(ASTBase.ForStatement st) {
            this.open(new BytePtr("for"));
            if (st._init != null)
                st._init.accept(this);
            (this.buf.get()).writenl();
            if (st.condition != null)
                st.condition.accept(this);
            (this.buf.get()).writenl();
            if (st.increment != null)
                st.increment.accept(this);
            (this.buf.get()).writenl();
            if (st._body != null)
                st._body.accept(this);
            this.close();
        }

        public  void visit(ASTBase.DoStatement st) {
            this.open(new BytePtr("do"));
            if (st._body != null)
                st._body.accept(this);
            (this.buf.get()).writenl();
            if (st.condition != null)
                st.condition.accept(this);
            this.close();
        }

        public  void visit(ASTBase.ForeachRangeStatement st) {
            this.open(new BytePtr("%s"), Token.toChars(st.op));
            if (st.prm != null)
                st.prm.accept(this);
            (this.buf.get()).writenl();
            if (st.lwr != null)
                st.lwr.accept(this);
            if (st.upr != null)
            {
                (this.buf.get()).printf(new BytePtr(".."));
                st.upr.accept(this);
            }
            if (st._body != null)
                st._body.accept(this);
            (this.buf.get()).writenl();
            this.close();
        }

        public  void visit(ASTBase.ForeachStatement st) {
            this.open(new BytePtr("%s"), Token.toChars(st.op));
            if (st.parameters != null)
            {
                {
                    Slice<ASTBase.Parameter> __r272 = (st.parameters.get()).opSlice().copy();
                    int __key273 = 0;
                    for (; (__key273 < __r272.getLength());__key273 += 1) {
                        ASTBase.Parameter prm = __r272.get(__key273);
                        prm.accept(this);
                    }
                }
            }
            (this.buf.get()).writenl();
            if (st.aggr != null)
                st.aggr.accept(this);
            (this.buf.get()).writenl();
            if (st._body != null)
                st._body.accept(this);
            (this.buf.get()).writenl();
            this.close();
        }

        public  void visit(ASTBase.IfStatement st) {
            this.open(new BytePtr("if"));
            if (st.prm != null)
                st.prm.accept(this);
            (this.buf.get()).writenl();
            if (st.condition != null)
                st.condition.accept(this);
            (this.buf.get()).writenl();
            if (st.ifbody != null)
                st.ifbody.accept(this);
            (this.buf.get()).writenl();
            if (st.elsebody != null)
            {
                this.open(new BytePtr("else"));
                st.elsebody.accept(this);
                this.close();
            }
            this.close();
        }

        public  void visit(ASTBase.ScopeGuardStatement sgt) {
            this.open(new BytePtr("scope %s "), Token.toChars(sgt.tok));
            if (sgt.statement != null)
                sgt.statement.accept(this);
            this.close();
        }

        public  void visit(ASTBase.ConditionalStatement st) {
            this.open(new BytePtr("static if"));
            if (st.condition != null)
                st.condition.accept(this);
            (this.buf.get()).writenl();
            if (st.ifbody != null)
                st.ifbody.accept(this);
            (this.buf.get()).writenl();
            if (st.elsebody != null)
            {
                this.open(new BytePtr("else"));
                st.elsebody.accept(this);
                this.close();
            }
            this.close();
        }

        public  void visit(ASTBase.PragmaStatement st) {
            (this.buf.get()).printf(new BytePtr("( pragma %s"), st.ident.toChars());
            this.visitExps(st.args);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.SwitchStatement sw) {
            this.open(new BytePtr("%s switch"), sw.isFinal ? new BytePtr("final") : new BytePtr(""));
            if (sw._body != null)
                sw._body.accept(this);
            this.close();
        }

        public  void visit(ASTBase.CaseRangeStatement st) {
            this.open(new BytePtr("case "));
            st.first.accept(this);
            (this.buf.get()).printf(new BytePtr(" .. "));
            (this.buf.get()).printf(new BytePtr(" "));
            st.last.accept(this);
            (this.buf.get()).writenl();
            st.statement.accept(this);
            this.close();
        }

        public  void visit(ASTBase.CaseStatement ct) {
            this.open(new BytePtr("case "));
            ct.exp.accept(this);
            (this.buf.get()).writenl();
            if (ct.statement != null)
                ct.statement.accept(this);
            this.close();
        }

        public  void visit(ASTBase.DefaultStatement def) {
            this.open(new BytePtr("default"));
            if (def.statement != null)
                def.statement.accept(this);
            this.close();
        }

        public  void visit(ASTBase.BreakStatement brk) {
            (this.buf.get()).printf(new BytePtr("( break %s )"), brk.ident != null ? brk.ident.toChars() : new BytePtr(""));
            (this.buf.get()).writenl();
        }

        public  void visit(ASTBase.ContinueStatement cont) {
            (this.buf.get()).printf(new BytePtr("( continue %s )"), cont.ident != null ? cont.ident.toChars() : new BytePtr(""));
            (this.buf.get()).writenl();
        }

        public  void visit(ASTBase.GotoDefaultStatement gds) {
            (this.buf.get()).printf(new BytePtr("( goto default )"));
            (this.buf.get()).writenl();
        }

        public  void visit(ASTBase.GotoCaseStatement gcs) {
            (this.buf.get()).printf(new BytePtr("( goto case "));
            if (gcs.exp != null)
                gcs.exp.accept(this);
            (this.buf.get()).printf(new BytePtr(")"));
            (this.buf.get()).writenl();
        }

        public  void visit(ASTBase.GotoStatement gs) {
            (this.buf.get()).printf(new BytePtr("( goto %s)"), gs.ident.toChars());
            (this.buf.get()).writenl();
        }

        public  void visit(ASTBase.SynchronizedStatement sync) {
            this.open(new BytePtr("synchronized"));
            if (sync._body != null)
                sync._body.accept(this);
            this.close();
        }

        public  void visit(ASTBase.WithStatement w) {
            this.open(new BytePtr("with"));
            if (w.exp != null)
                w.exp.accept(this);
            (this.buf.get()).writenl();
            if (w._body != null)
                w._body.accept(this);
            this.close();
        }

        public  void visit(ASTBase.TryCatchStatement tc) {
            this.open(new BytePtr("try"));
            if (tc._body != null)
                tc._body.accept(this);
            if (tc.catches != null)
            {
                Slice<ASTBase.Catch> __r274 = (tc.catches.get()).opSlice().copy();
                int __key275 = 0;
                for (; (__key275 < __r274.getLength());__key275 += 1) {
                    ASTBase.Catch c = __r274.get(__key275);
                    this.open(new BytePtr("catch"));
                    if (c.type != null)
                    {
                        c.type.accept(this);
                        (this.buf.get()).printf(new BytePtr(" "));
                    }
                    if (c.ident != null)
                        (this.buf.get()).printf(new BytePtr("%s"), c.ident.toChars());
                    (this.buf.get()).writenl();
                    if (c.handler != null)
                        c.handler.accept(this);
                    this.close();
                }
            }
            this.close();
        }

        public  void visit(ASTBase.TryFinallyStatement tf) {
            this.open(new BytePtr("try"));
            if (tf._body != null)
                tf._body.accept(this);
            if (tf.finalbody != null)
            {
                this.open(new BytePtr("finally"));
                tf.finalbody.accept(this);
                this.close();
            }
            this.close();
        }

        public  void visit(ASTBase.ThrowStatement thr) {
            this.open(new BytePtr("throw"));
            thr.exp.accept(this);
            this.close();
        }

        public  void visit(ASTBase.AsmStatement ast) {
            Ptr<Token> t = ast.tokens;
            for (; t != null;){
                (this.buf.get()).printf(new BytePtr("%s"), (t.get()).toChars());
                t = (t.get()).next;
            }
        }

        public  void visit(ASTBase.ExpStatement s) {
            (this.buf.get()).printf(new BytePtr("( expr "));
            (this.buf.get()).writenl();
            (this.buf.get()).level++;
            s.exp.accept(this);
            (this.buf.get()).level--;
            (this.buf.get()).writenl();
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.CompoundStatement s) {
            this.open(new BytePtr(""));
            this.visitStatements(s.statements);
            this.close();
        }

        public  void visit(ASTBase.CompoundDeclarationStatement s) {
            this.visitStatements(s.statements);
        }

        public  void visit(ASTBase.CompoundAsmStatement st) {
            Ref<Long> stc = ref(st.stc);
            this.open(new BytePtr("asm %s"), ASTBase.stcToChars(stc));
            this.visitStatements(st.statements);
            this.close();
        }

        public  void visit(ASTBase.InlineAsmStatement iasm) {
            this.visit((ASTBase.AsmStatement)iasm);
        }

        public  void visit(ASTBase.Type t) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeBasic t) {
            (this.buf.get()).printf(new BytePtr("%s %s"), modToChars((t.mod & 0xFF)), t.dstring);
        }

        public  void visit(ASTBase.TypeError _param_0) {
            (this.buf.get()).printf(new BytePtr("terror"));
        }

        public  void visit(ASTBase.TypeNull _param_0) {
            (this.buf.get()).printf(new BytePtr("typeof(null)"));
        }

        public  void visit(ASTBase.TypeVector t) {
            (this.buf.get()).printf(new BytePtr("( __vector "));
            if (t.basetype != null)
                t.basetype.accept(this);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.TypeEnum t) {
            (this.buf.get()).printf(new BytePtr("enum %s "), t.sym.toChars());
            t.sym.memtype.accept(this);
        }

        public  void visit(ASTBase.TypeTuple t) {
            (this.buf.get()).printf(new BytePtr("( typetuple "));
            if (t.arguments != null)
            {
                {
                    Slice<ASTBase.Parameter> __r277 = (t.arguments.get()).opSlice().copy();
                    int __key276 = 0;
                    for (; (__key276 < __r277.getLength());__key276 += 1) {
                        ASTBase.Parameter a = __r277.get(__key276);
                        int i = __key276;
                        if (i != 0)
                            (this.buf.get()).printf(new BytePtr(" "));
                        super.visit(a);
                    }
                }
            }
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.TypeClass tc) {
            (this.buf.get()).printf(new BytePtr("%s"), tc.sym.ident.toChars());
        }

        public  void visit(ASTBase.TypeStruct ts) {
            (this.buf.get()).printf(new BytePtr("%s"), ts.sym.ident.toChars());
        }

        public  void visit(ASTBase.TypeNext _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeReference t) {
            (this.buf.get()).printf(new BytePtr("ref "));
            super.visit(t.next);
        }

        public  void visit(ASTBase.TypeSlice ts) {
            (this.buf.get()).printf(new BytePtr("( slice "));
            ts.lwr.accept(this);
            (this.buf.get()).printf(new BytePtr(" "));
            ts.upr.accept(this);
            (this.buf.get()).printf(new BytePtr(" "));
            ts.next.accept(this);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.TypeDelegate td) {
            (this.buf.get()).printf(new BytePtr("delegate "));
            if (td.next != null)
                td.next.accept(this);
        }

        public  void visit(ASTBase.TypePointer tp) {
            tp.next.accept(this);
            (this.buf.get()).printf(new BytePtr("*"));
        }

        public  void visit(ASTBase.TypeFunction tf) {
            if (tf.next != null)
            {
                tf.next.accept(this);
                (this.buf.get()).printf(new BytePtr(" "));
            }
            {
                Slice<ASTBase.Parameter> __r279 = (tf.parameterList.parameters.get()).opSlice().copy();
                int __key278 = 0;
                for (; (__key278 < __r279.getLength());__key278 += 1) {
                    ASTBase.Parameter p = __r279.get(__key278);
                    int i = __key278;
                    if (i != 0)
                        (this.buf.get()).printf(new BytePtr(" "));
                    p.accept(this);
                }
            }
        }

        public  void visit(ASTBase.TypeArray _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeDArray d) {
            d.next.accept(this);
            (this.buf.get()).printf(new BytePtr("[]"));
        }

        public  void visit(ASTBase.TypeAArray ta) {
            ta.next.accept(this);
            (this.buf.get()).printf(new BytePtr("["));
            ta.index.accept(this);
            (this.buf.get()).printf(new BytePtr("]"));
        }

        public  void visit(ASTBase.TypeSArray tsa) {
            tsa.next.accept(this);
            (this.buf.get()).printf(new BytePtr("["));
            tsa.dim.accept(this);
            (this.buf.get()).printf(new BytePtr("]"));
        }

        public  void visit(ASTBase.TypeQualified _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeTraits tt) {
            (this.buf.get()).printf(new BytePtr("type __traits("));
            tt.exp.accept(this);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.TypeIdentifier d) {
            (this.buf.get()).printf(new BytePtr("%s"), d.ident.toChars());
        }

        public  void visit(ASTBase.TypeReturn _param_0) {
            (this.buf.get()).printf(new BytePtr("typeof(return)"));
        }

        public  void visit(ASTBase.TypeTypeof tt) {
            (this.buf.get()).printf(new BytePtr("typeof("));
            if (tt.exp != null)
                tt.exp.accept(this);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.TypeInstance ti) {
            (this.buf.get()).printf(new BytePtr("%s!("), ti.tempinst.tempdecl != null ? ti.tempinst.tempdecl.toChars() : ti.tempinst.name.toChars());
            this.visitTiargs(ti.tempinst.tiargs);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.Expression _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.DeclarationExp e) {
            if (e.declaration != null)
                e.declaration.accept(this);
        }

        public  void visit(ASTBase.IntegerExp e) {
            (this.buf.get()).printf(new BytePtr("%lld"), (long)e.value);
        }

        public  void visit(ASTBase.NewAnonClassExp nc) {
            (this.buf.get()).printf(new BytePtr("( new anonclass %s "), nc.cd.ident.toChars());
            this.visitExps(nc.arguments);
            nc.cd.accept(this);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.IsExp ie) {
            (this.buf.get()).printf(new BytePtr("( is %s"), Token.toChars(ie.tok));
            if (ie.id != null)
                (this.buf.get()).printf(new BytePtr("%s "), ie.id.toChars());
            if (ie.type != null)
                ie.type.accept(this);
        }

        public  void visit(ASTBase.RealExp r) {
            (this.buf.get()).printf(new BytePtr("%llf"), r.value);
        }

        public  void visit(ASTBase.NullExp _param_0) {
            (this.buf.get()).printf(new BytePtr("null"));
        }

        public  void visit(ASTBase.TypeidExp tie) {
            switch (tie.obj.dyncast())
            {
                case DYNCAST.expression:
                    ((ASTBase.Expression)tie.obj).accept(this);
                    return ;
                case DYNCAST.dsymbol:
                    ((ASTBase.Dsymbol)tie.obj).accept(this);
                    return ;
                case DYNCAST.type:
                    ((ASTBase.Type)tie.obj).accept(this);
                    return ;
                default:
                (this.buf.get()).printf(new BytePtr("<typeid>"));
            }
        }

        public  void visit(ASTBase.TraitsExp te) {
            (this.buf.get()).printf(new BytePtr("( __traits %s "), te.ident.toChars());
            if (te.args != null)
            {
                {
                    Slice<RootObject> __r280 = (te.args.get()).opSlice().copy();
                    int __key281 = 0;
                    for (; (__key281 < __r280.getLength());__key281 += 1) {
                        RootObject arg = __r280.get(__key281);
                        switch (arg.dyncast())
                        {
                            case DYNCAST.expression:
                                ((ASTBase.Expression)arg).accept(this);
                                return ;
                            case DYNCAST.dsymbol:
                                ((ASTBase.Dsymbol)arg).accept(this);
                                return ;
                            case DYNCAST.type:
                                ((ASTBase.Type)arg).accept(this);
                                return ;
                            default:
                            (this.buf.get()).printf(new BytePtr("%s"), arg.toChars());
                        }
                    }
                }
            }
        }

        public  void visit(ASTBase.StringExp exp) {
            if (exp.type != null)
                exp.type.accept(this);
            if (((exp.sz & 0xFF) == 1))
                (this.buf.get()).printf(new BytePtr("\"\"\"%.*s\"\"\""), exp.len, exp.string);
            else if (((exp.sz & 0xFF) == 2))
            {
                (this.buf.get()).printf(new BytePtr("\"\"\"%.*s\"\"\""), exp.len, exp.wstring);
            }
            else if (((exp.sz & 0xFF) == 4))
            {
                (this.buf.get()).printf(new BytePtr("\"\"\"%.*s\"\"\""), exp.len, exp.dstring);
            }
        }

        public  void visit(ASTBase.NewExp ne) {
            (this.buf.get()).printf(new BytePtr("( new "));
            this.visitExps(ne.arguments);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.AssocArrayLiteralExp aa) {
            (this.buf.get()).printf(new BytePtr("( key[value] "));
            if (aa.keys != null)
            {
                Slice<ASTBase.Expression> __r283 = (aa.keys.get()).opSlice().copy();
                int __key282 = 0;
                for (; (__key282 < __r283.getLength());__key282 += 1) {
                    ASTBase.Expression key = __r283.get(__key282);
                    int i = __key282;
                    if (i != 0)
                        (this.buf.get()).printf(new BytePtr(" "));
                    ASTBase.Expression v = (aa.values.get()).get(i);
                    key.accept(this);
                    (this.buf.get()).printf(new BytePtr(" "));
                    v.accept(this);
                }
            }
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.ArrayLiteralExp ae) {
            (this.buf.get()).printf(new BytePtr("( [] "));
            if (ae.elements != null)
            {
                Slice<ASTBase.Expression> __r285 = (ae.elements.get()).opSlice().copy();
                int __key284 = 0;
                for (; (__key284 < __r285.getLength());__key284 += 1) {
                    ASTBase.Expression el = __r285.get(__key284);
                    int i = __key284;
                    if (i != 0)
                        (this.buf.get()).printf(new BytePtr(" "));
                    ASTBase.Expression e = el != null ? el : ae.basis;
                    if (e != null)
                    {
                        e.accept(this);
                    }
                }
            }
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.FuncExp fe) {
            this.open(new BytePtr("func-expr"));
            if (fe.fd != null)
                fe.fd.accept(this);
            else if (fe.td != null)
                fe.td.accept(this);
            this.close();
        }

        public  void visit(ASTBase.IntervalExp ival) {
            ival.lwr.accept(this);
            (this.buf.get()).printf(new BytePtr(" .. "));
            if (ival.upr != null)
                ival.upr.accept(this);
            else
                (this.buf.get()).printf(new BytePtr("$"));
        }

        public  void visit(ASTBase.TypeExp te) {
            if (te.type != null)
                te.type.accept(this);
        }

        public  void visit(ASTBase.ScopeExp s) {
            this.open(new BytePtr("{}"));
            if (s.sds != null)
                s.sds.accept(this);
            this.close();
        }

        public  void visit(ASTBase.IdentifierExp e) {
            (this.buf.get()).printf(new BytePtr("%s"), e.ident.toChars());
        }

        public  void visit(ASTBase.UnaExp e) {
            (this.buf.get()).printf(new BytePtr("%s"), Token.toChars(e.op));
            e.e1.accept(this);
        }

        public  void visit(ASTBase.DefaultInitExp ie) {
            if (ie.type != null)
                ie.type.accept(this);
            (this.buf.get()).printf(new BytePtr(" init"));
        }

        public  void visit(ASTBase.BinExp e) {
            (this.buf.get()).printf(new BytePtr("( %s "), Token.toChars(e.op));
            e.e1.accept(this);
            (this.buf.get()).printf(new BytePtr(" "));
            e.e2.accept(this);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.DsymbolExp e) {
            (this.buf.get()).printf(new BytePtr("%s"), e.s.ident.toChars());
        }

        public  void visit(ASTBase.TemplateExp e) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.SymbolExp e) {
            (this.buf.get()).printf(new BytePtr("( symbol "));
            e.type.accept(this);
            (this.buf.get()).printf(new BytePtr(" %s"), e.var.ident.toChars());
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.VarExp e) {
            (this.buf.get()).printf(new BytePtr("( var "));
            e.type.accept(this);
            (this.buf.get()).printf(new BytePtr(" %s"), e.var.ident.toChars());
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.TupleExp e) {
            this.open(new BytePtr("tuple-exp"));
            this.visitExps(e.exps);
            this.close();
        }

        public  void visit(ASTBase.DollarExp e) {
            (this.buf.get()).printf(new BytePtr("$"));
        }

        public  void visit(ASTBase.ThisExp e) {
            (this.buf.get()).printf(new BytePtr("this"));
        }

        public  void visit(ASTBase.SuperExp e) {
            (this.buf.get()).printf(new BytePtr("super"));
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
            (this.buf.get()).printf(new BytePtr("( cast "));
            if (e.to != null)
            {
                e.to.accept(this);
                (this.buf.get()).printf(new BytePtr(" "));
            }
            e.e1.accept(this);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.CallExp call) {
            (this.buf.get()).printf(new BytePtr("( call "));
            if (call.e1 != null)
            {
                call.e1.accept(this);
                (this.buf.get()).printf(new BytePtr(" "));
            }
            this.visitExps(call.arguments);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.DotIdExp e) {
            (this.buf.get()).printf(new BytePtr("( . "));
            e.e1.accept(this);
            (this.buf.get()).printf(new BytePtr(" %s"), e.ident.toChars());
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.AssertExp e) {
            (this.buf.get()).printf(new BytePtr("( assert "));
            e.e1.accept(this);
            if (e.msg != null)
            {
                (this.buf.get()).printf(new BytePtr(" "));
                e.msg.accept(this);
            }
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.CompileExp c) {
            (this.buf.get()).printf(new BytePtr("( mixin "));
            this.visitExps(c.exps);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.ImportExp ie) {
            this.visit((ASTBase.UnaExp)ie);
        }

        public  void visit(ASTBase.DotTemplateInstanceExp e) {
            (this.buf.get()).printf(new BytePtr("( . "));
            e.e1.accept(this);
            (this.buf.get()).printf(new BytePtr(" %s"), e.ti.ident != null ? e.ti.ident.toChars() : e.ti.name.toChars());
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.ArrayExp arr) {
            if (arr.e1 != null)
                arr.e1.accept(this);
            if (arr.arguments != null)
            {
                Slice<ASTBase.Expression> __r286 = (arr.arguments.get()).opSlice().copy();
                int __key287 = 0;
                for (; (__key287 < __r286.getLength());__key287 += 1) {
                    ASTBase.Expression arg = __r286.get(__key287);
                    arg.accept(this);
                }
            }
        }

        public  void visit(ASTBase.FuncInitExp e) {
            (this.buf.get()).printf(new BytePtr("func-init "));
            this.visit((ASTBase.DefaultInitExp)e);
        }

        public  void visit(ASTBase.PrettyFuncInitExp e) {
            (this.buf.get()).printf(new BytePtr("pretty-init "));
            this.visit((ASTBase.DefaultInitExp)e);
        }

        public  void visit(ASTBase.FileInitExp e) {
            (this.buf.get()).printf(new BytePtr("file-init "));
            this.visit((ASTBase.DefaultInitExp)e);
        }

        public  void visit(ASTBase.LineInitExp e) {
            (this.buf.get()).printf(new BytePtr("line-init "));
            this.visit((ASTBase.DefaultInitExp)e);
        }

        public  void visit(ASTBase.ModuleInitExp e) {
            (this.buf.get()).printf(new BytePtr("module-init "));
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
            (this.buf.get()).printf(new BytePtr("( ? "));
            if (e.econd != null)
                e.econd.accept(this);
            if (e.e1 != null)
                e.e1.accept(this);
            if (e.e2 != null)
                e.e2.accept(this);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.AssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.BinAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.AddAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.MinAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.MulAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.DivAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.ModAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.PowAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.AndAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.OrAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.XorAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.ShlAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.ShrAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.UshrAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.CatAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        public  void visit(ASTBase.TemplateParameter tp) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TemplateAliasParameter tp) {
            (this.buf.get()).printf(new BytePtr("alias %s"), tp.ident.toChars());
        }

        public  void visit(ASTBase.TemplateTypeParameter tp) {
        }

        public  void visit(ASTBase.TemplateTupleParameter tp) {
            (this.buf.get()).printf(new BytePtr("alias %s"), tp.ident.toChars());
        }

        public  void visit(ASTBase.TemplateValueParameter tv) {
            (this.buf.get()).printf(new BytePtr("template-value "));
            tv.valType.accept(this);
            if (tv.specValue != null)
            {
                (this.buf.get()).printf(new BytePtr(" "));
                tv.specValue.accept(this);
            }
            if (tv.defaultValue != null)
            {
                (this.buf.get()).printf(new BytePtr(" default "));
                tv.specValue.accept(this);
            }
        }

        public  void visit(ASTBase.TemplateThisParameter tp) {
            (this.buf.get()).printf(new BytePtr("template-this"));
        }

        public  void visit(ASTBase.Condition _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.StaticForeachStatement sfst) {
            if (sfst.sfe.aggrfe != null)
                sfst.sfe.aggrfe.accept(this);
            else
                sfst.sfe.rangefe.accept(this);
        }

        public  void visit(ASTBase.StaticIfCondition cond) {
            cond.exp.accept(this);
        }

        public  void visit(ASTBase.DVCondition _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.DebugCondition d) {
            (this.buf.get()).printf(new BytePtr("debug %s"), d.ident != null ? d.ident.toChars() : new BytePtr(""));
        }

        public  void visit(ASTBase.VersionCondition ver) {
            (this.buf.get()).printf(new BytePtr("%s"), ver.ident.toChars());
        }

        public  void visit(ASTBase.Initializer _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.ExpInitializer ei) {
            if (ei.exp != null)
                ei.exp.accept(this);
        }

        public  void visit(ASTBase.StructInitializer si) {
            (this.buf.get()).printf(new BytePtr("( struct-init "));
            {
                Slice<Identifier> __r289 = si.field.opSlice().copy();
                int __key288 = 0;
                for (; (__key288 < __r289.getLength());__key288 += 1) {
                    Identifier id = __r289.get(__key288);
                    int i = __key288;
                    if (i != 0)
                        (this.buf.get()).printf(new BytePtr(" "));
                    if (id != null)
                    {
                        (this.buf.get()).writestring(id.asString());
                        (this.buf.get()).writeByte(58);
                    }
                    {
                        ASTBase.Initializer iz = si.value.get(i);
                        if ((iz) != null)
                            iz.accept(this);
                    }
                }
            }
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.ArrayInitializer ai) {
            (this.buf.get()).printf(new BytePtr("( array-init "));
            {
                Slice<ASTBase.Initializer> __r291 = ai.value.opSlice().copy();
                int __key290 = 0;
                for (; (__key290 < __r291.getLength());__key290 += 1) {
                    ASTBase.Initializer v = __r291.get(__key290);
                    int i = __key290;
                    if (i != 0)
                        (this.buf.get()).printf(new BytePtr(" "));
                    if (v != null)
                        v.accept(this);
                    else
                        (this.buf.get()).printf(new BytePtr("null"));
                }
            }
            (this.buf.get()).printf(new BytePtr(")"));
        }

        public  void visit(ASTBase.VoidInitializer _param_0) {
            (this.buf.get()).printf(new BytePtr("void"));
        }


        public LispyPrint() {}

        public LispyPrint copy() {
            LispyPrint that = new LispyPrint();
            that.buf = this.buf;
            return that;
        }
    }
    public static void main(Slice<ByteSlice> args) {
        Ref<Slice<ByteSlice>> args_ref = ref(args);
        Ref<ByteSlice> outdir = ref(new ByteSlice(".").copy());
        Ref<ByteSlice> tool = ref(new ByteSlice("lex").copy());
        GetoptResult res = getopt(args_ref, new ByteSlice("outdir"), new ByteSlice("output directory"), ptr(outdir), new ByteSlice("tool"), new ByteSlice("select tool - lex or lispy"), ptr(tool)).copy();
        if (res.helpWanted)
        {
            defaultGetoptPrinter(new ByteSlice("Trivial D lexer based on DMD."), res.options);
            exit(1);
        }
        global.value.params.isLinux = true;
        global.value.params.useUnitTests = true;
        global.value._init();
        ASTBase.Type._init();
        Id.initialize();
        {
            Slice<ByteSlice> __r487 = args_ref.value.slice(1,args_ref.value.getLength()).copy();
            int __key488 = 0;
            for (; (__key488 < __r487.getLength());__key488 += 1) {
                ByteSlice arg = __r487.get(__key488).copy();
                if (__equals(tool.value, new ByteSlice("lex")))
                    processFile_D1F3732A9A6A6D5A(arg, outdir.value, new BytePtr("tk"));
                else if (__equals(tool.value, new ByteSlice("lispy")))
                    processFile_24239CC9FAA32FB7(arg, outdir.value, new BytePtr("ast"));
                else
                {
                    fprintf(stderr, new BytePtr("Unsupported tool name: %.*s"), tool.value.getLength(), toBytePtr(tool.value));
                    exit(2);
                }
            }
        }
        exit(0);
    }

    public static ByteSlice lex(BytePtr argz, ByteSlice buf) {
        Lexer lexer = new Lexer(argz, toBytePtr(buf), 0, buf.getLength(), true, true, new StderrDiagnosticReporter(DiagnosticReporting.error));
        Ptr<OutBuffer> output = new OutBuffer(null, 0, 0, 0, false, false);
        int i = 0;
        for (; ((lexer.nextToken() & 0xFF) != 11);){
            (output.get()).printf(new BytePtr("%4d"), (lexer.token.value.value & 0xFF));
            if (((i += 1) == 20))
            {
                (output.get()).printf(new BytePtr(" | Line %5d |\n"), lexer.token.value.loc.linnum);
                i = 0;
            }
        }
        if ((i != 0))
            (output.get()).printf(new BytePtr(" | Line %5d |\n"), lexer.token.value.loc.linnum);
        return (output.get()).extractSlice();
    }

    public static ByteSlice lispy(BytePtr argz, ByteSlice buf) {
        StderrDiagnosticReporter diagnosticReporter = new StderrDiagnosticReporter(global.value.params.useDeprecated);
        try {
            ASTBase.Module mod = new ASTBase.Module(argz, Identifier.idPool(argz.slice(0,strlen(argz) - 2)), 1, 0);
            ParserASTBase p = new ParserASTBase(mod, buf, true, diagnosticReporter);
            try {
                p.nextToken();
                Ptr<DArray<ASTBase.Dsymbol>> decls = p.parseModule();
                LispyPrint lispPrint = new LispyPrint();
                lispPrint.buf = new OutBuffer(null, 0, 0, 0, false, false);
                (lispPrint.buf.get()).doindent = true;
                {
                    Slice<ASTBase.Dsymbol> __r491 = (decls.get()).opSlice().copy();
                    int __key492 = 0;
                    for (; (__key492 < __r491.getLength());__key492 += 1) {
                        ASTBase.Dsymbol d = __r491.get(__key492);
                        d.accept(lispPrint);
                    }
                }
                return (lispPrint.buf.get()).extractSlice();
            }
            finally {
            }
        }
        finally {
        }
    }

    // from template processFile!(_24239CC9FAA32FB7)
    public static void processFile_24239CC9FAA32FB7(ByteSlice arg, ByteSlice outdir, BytePtr suffix) {
        BytePtr argz = pcopy(toStringz(arg));
        File.ReadResult buffer = File.read(toBytePtr(argz)).copy();
        try {
            if (!buffer.success)
            {
                fprintf(stderr, new BytePtr("Failed to read from file: %s"), argz);
                exit(2);
            }
            ByteSlice buf = buffer.extractData().copy();
            BytePtr dest = pcopy(FileName.forceExt(FileName.name(toBytePtr(argz)), suffix));
            ByteSlice filePath = toByteSlice((outdir.concat(new ByteSlice("/")))).concat(dest.slice(0,strlen(dest))).copy();
            ByteSlice output = lispy(toBytePtr(argz), toByteSlice(buf)).copy();
            if (!File.write(toStringz(filePath), toByteSlice(output)))
                fprintf(stderr, new BytePtr("Failed to write file: %s\n"), toStringz(filePath));
        }
        finally {
        }
    }


    // from template processFile!(_D1F3732A9A6A6D5A)
    public static void processFile_D1F3732A9A6A6D5A(ByteSlice arg, ByteSlice outdir, BytePtr suffix) {
        BytePtr argz = pcopy(toStringz(arg));
        File.ReadResult buffer = File.read(toBytePtr(argz)).copy();
        try {
            if (!buffer.success)
            {
                fprintf(stderr, new BytePtr("Failed to read from file: %s"), argz);
                exit(2);
            }
            ByteSlice buf = buffer.extractData().copy();
            BytePtr dest = pcopy(FileName.forceExt(FileName.name(toBytePtr(argz)), suffix));
            ByteSlice filePath = toByteSlice((outdir.concat(new ByteSlice("/")))).concat(dest.slice(0,strlen(dest))).copy();
            ByteSlice output = lex(toBytePtr(argz), toByteSlice(buf)).copy();
            if (!File.write(toStringz(filePath), toByteSlice(output)))
                fprintf(stderr, new BytePtr("Failed to write file: %s\n"), toStringz(filePath));
        }
        finally {
        }
    }


}
