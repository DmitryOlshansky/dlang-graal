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

    // Erasure: modToChars<int>
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

    // Erasure: linkToChars<int>
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
        // Erasure: open<Ptr>
        public  void open(BytePtr format, Object... ap) {
            Ref<BytePtr> format_ref = ref(format);
            (this.buf.get()).writestring(new ByteSlice("( "));
            (this.buf.get()).vprintf(format_ref.value, new RawSlice<>(ap));
            (this.buf.get()).level++;
            (this.buf.get()).writenl();
        }

        // Erasure: close<>
        public  void close() {
            (this.buf.get()).level--;
            (this.buf.get()).writenl();
            (this.buf.get()).printf(new BytePtr(")"));
            (this.buf.get()).writenl();
        }

        // Erasure: visitDecls<Ptr>
        public  void visitDecls(Ptr<DArray<ASTBase.Dsymbol>> decls) {
            if (decls != null)
            {
                {
                    Slice<ASTBase.Dsymbol> __r284 = (decls.get()).opSlice().copy();
                    int __key285 = 0;
                    for (; (__key285 < __r284.getLength());__key285 += 1) {
                        ASTBase.Dsymbol m = __r284.get(__key285);
                        m.accept(this);
                    }
                }
            }
        }

        // Erasure: visitExps<Ptr>
        public  void visitExps(Ptr<DArray<ASTBase.Expression>> exps) {
            if (exps != null)
            {
                {
                    Slice<ASTBase.Expression> __r287 = (exps.get()).opSlice().copy();
                    int __key286 = 0;
                    for (; (__key286 < __r287.getLength());__key286 += 1) {
                        ASTBase.Expression e = __r287.get(__key286);
                        int i = __key286;
                        if (i != 0)
                        {
                            (this.buf.get()).printf(new BytePtr(" "));
                        }
                        e.accept(this);
                    }
                }
            }
        }

        // Erasure: visitStatements<Ptr>
        public  void visitStatements(Ptr<DArray<ASTBase.Statement>> statements) {
            if (statements != null)
            {
                Slice<ASTBase.Statement> __r289 = (statements.get()).opSlice().copy();
                int __key288 = 0;
                for (; (__key288 < __r289.getLength());__key288 += 1) {
                    ASTBase.Statement st = __r289.get(__key288);
                    int i = __key288;
                    if (i != 0)
                    {
                        (this.buf.get()).writenl();
                    }
                    st.accept(this);
                }
            }
        }

        // Erasure: visitTiargs<Ptr>
        public  void visitTiargs(Ptr<DArray<RootObject>> tiargs) {
            if (tiargs != null)
            {
                Slice<RootObject> __r291 = (tiargs.get()).opSlice().copy();
                int __key290 = 0;
                for (; (__key290 < __r291.getLength());__key290 += 1) {
                    RootObject m = __r291.get(__key290);
                    int i = __key290;
                    if (i != 0)
                    {
                        (this.buf.get()).printf(new BytePtr(" "));
                    }
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

        // Erasure: visit<Dsymbol>
        public  void visit(ASTBase.Dsymbol s) {
            (this.buf.get()).printf(new BytePtr("%s"), s.toChars());
        }

        // Erasure: visit<AliasThis>
        public  void visit(ASTBase.AliasThis a) {
            super.visit(a);
        }

        // Erasure: visit<Declaration>
        public  void visit(ASTBase.Declaration d) {
            (this.buf.get()).printf(new BytePtr("%s"), d.toChars());
        }

        // Erasure: visit<ScopeDsymbol>
        public  void visit(ASTBase.ScopeDsymbol scd) {
            (this.buf.get()).printf(new BytePtr("%s"), scd.toChars());
        }

        // Erasure: visit<Import>
        public  void visit(ASTBase.Import imp) {
            (this.buf.get()).printf(new BytePtr("import %s"), imp.toChars());
        }

        // Erasure: visit<AttribDeclaration>
        public  void visit(ASTBase.AttribDeclaration attr) {
            this.open(new BytePtr("%s"), attr.toChars());
            this.visitDecls(attr.decl);
            this.close();
        }

        // Erasure: visit<StaticAssert>
        public  void visit(ASTBase.StaticAssert as) {
            this.open(new BytePtr("static assert"));
            as.exp.accept(this);
            this.close();
        }

        // Erasure: visit<DebugSymbol>
        public  void visit(ASTBase.DebugSymbol sym) {
            (this.buf.get()).printf(new BytePtr("debug"));
        }

        // Erasure: visit<VersionSymbol>
        public  void visit(ASTBase.VersionSymbol ver) {
            (this.buf.get()).printf(new BytePtr("version"));
        }

        // Erasure: visit<VarDeclaration>
        public  void visit(ASTBase.VarDeclaration d) {
            this.open(new BytePtr("var %s "), d.ident.toChars());
            if (d.type != null)
            {
                d.type.accept(this);
            }
            if (d._init != null)
            {
                (this.buf.get()).printf(new BytePtr(" "));
                d._init.accept(this);
            }
            this.close();
        }

        // Erasure: visit<FuncDeclaration>
        public  void visit(ASTBase.FuncDeclaration d) {
            this.open(new BytePtr("func %s"), d.ident.toChars());
            if (d.type != null)
            {
                d.type.accept(this);
            }
            if (d.fbody != null)
            {
                d.fbody.accept(this);
            }
            this.close();
        }

        // Erasure: visit<AliasDeclaration>
        public  void visit(ASTBase.AliasDeclaration d) {
            this.open(new BytePtr("alias %s "), d.ident.toChars());
            if (d.aliassym != null)
            {
                d.aliassym.accept(this);
                (this.buf.get()).printf(new BytePtr(" "));
            }
            if (d.type != null)
            {
                d.type.accept(this);
            }
            this.close();
        }

        // Erasure: visit<TupleDeclaration>
        public  void visit(ASTBase.TupleDeclaration d) {
            this.open(new BytePtr("tuple"));
            super.visit(d);
            this.close();
        }

        // Erasure: visit<FuncLiteralDeclaration>
        public  void visit(ASTBase.FuncLiteralDeclaration d) {
            this.open(new BytePtr("func literal %s"), d.ident.toChars());
            if (d.fbody != null)
            {
                d.fbody.accept(this);
            }
            this.close();
        }

        // Erasure: visit<PostBlitDeclaration>
        public  void visit(ASTBase.PostBlitDeclaration d) {
            this.open(new BytePtr("this(this)"));
            if (d.fbody != null)
            {
                d.fbody.accept(this);
            }
            this.close();
        }

        // Erasure: visit<CtorDeclaration>
        public  void visit(ASTBase.CtorDeclaration d) {
            this.open(new BytePtr("ctor"));
            if (d.fbody != null)
            {
                d.fbody.accept(this);
            }
            this.close();
        }

        // Erasure: visit<DtorDeclaration>
        public  void visit(ASTBase.DtorDeclaration d) {
            this.open(new BytePtr("dtor"));
            if (d.fbody != null)
            {
                d.fbody.accept(this);
            }
            this.close();
        }

        // Erasure: visit<InvariantDeclaration>
        public  void visit(ASTBase.InvariantDeclaration d) {
            this.open(new BytePtr("invariant"));
            if (d.fbody != null)
            {
                d.fbody.accept(this);
            }
            this.close();
        }

        // Erasure: visit<UnitTestDeclaration>
        public  void visit(ASTBase.UnitTestDeclaration d) {
            this.open(new BytePtr("unittest"));
            if (d.fbody != null)
            {
                d.fbody.accept(this);
            }
            this.close();
        }

        // Erasure: visit<NewDeclaration>
        public  void visit(ASTBase.NewDeclaration d) {
            this.open(new BytePtr("newdecl"));
            if (d.fbody != null)
            {
                d.fbody.accept(this);
            }
            this.close();
        }

        // Erasure: visit<DeleteDeclaration>
        public  void visit(ASTBase.DeleteDeclaration d) {
            this.open(new BytePtr("deletedecl"));
            if (d.fbody != null)
            {
                d.fbody.accept(this);
            }
            this.close();
        }

        // Erasure: visit<StaticCtorDeclaration>
        public  void visit(ASTBase.StaticCtorDeclaration d) {
            this.open(new BytePtr("static this"));
            if (d.fbody != null)
            {
                d.fbody.accept(this);
            }
            this.close();
        }

        // Erasure: visit<StaticDtorDeclaration>
        public  void visit(ASTBase.StaticDtorDeclaration d) {
            this.open(new BytePtr("static ~this"));
            if (d.fbody != null)
            {
                d.fbody.accept(this);
            }
            this.close();
        }

        // Erasure: visit<SharedStaticCtorDeclaration>
        public  void visit(ASTBase.SharedStaticCtorDeclaration d) {
            this.open(new BytePtr("shared static this"));
            if (d.fbody != null)
            {
                d.fbody.accept(this);
            }
            this.close();
        }

        // Erasure: visit<SharedStaticDtorDeclaration>
        public  void visit(ASTBase.SharedStaticDtorDeclaration d) {
            this.open(new BytePtr("shared static ~this"));
            if (d.fbody != null)
            {
                d.fbody.accept(this);
            }
            this.close();
        }

        // Erasure: visit<Package>
        public  void visit(ASTBase.Package d) {
            this.open(new BytePtr("package"));
            super.visit(d);
            this.close();
        }

        // Erasure: visit<EnumDeclaration>
        public  void visit(ASTBase.EnumDeclaration d) {
            this.open(new BytePtr("enum "));
            if (d.ident != null)
            {
                (this.buf.get()).printf(new BytePtr("%s"), d.ident.toChars());
            }
            this.visitDecls(d.members);
            this.close();
        }

        // Erasure: visit<AggregateDeclaration>
        public  void visit(ASTBase.AggregateDeclaration d) {
            this.open(new BytePtr("aggregate %s"), d.ident.toChars());
            super.visit(d);
            this.close();
        }

        // Erasure: visit<TemplateDeclaration>
        public  void visit(ASTBase.TemplateDeclaration d) {
            this.open(new BytePtr("template %s"), d.ident.toChars());
            this.visitDecls(d.members);
            this.close();
        }

        // Erasure: visit<TemplateInstance>
        public  void visit(ASTBase.TemplateInstance ti) {
            this.open(new BytePtr("template instance %s"), ti.ident != null ? ti.ident.toChars() : ti.name.toChars());
            this.visitTiargs(ti.tiargs);
            this.visitDecls(ti.members);
            this.close();
        }

        // Erasure: visit<Nspace>
        public  void visit(ASTBase.Nspace n) {
            this.open(new BytePtr("nspace %s"), n.ident.toChars());
            super.visit(n);
            this.close();
        }

        // Erasure: visit<CompileDeclaration>
        public  void visit(ASTBase.CompileDeclaration d) {
            this.open(new BytePtr("compiletime"));
            this.visitDecls(d.decl);
            this.close();
        }

        // Erasure: visit<UserAttributeDeclaration>
        public  void visit(ASTBase.UserAttributeDeclaration d) {
            this.open(new BytePtr("udas"));
            this.visitDecls(d.decl);
            this.close();
        }

        // Erasure: visit<LinkDeclaration>
        public  void visit(ASTBase.LinkDeclaration link) {
            this.open(new BytePtr("%s"), linkToChars(link.linkage));
            this.visitDecls(link.decl);
            this.close();
        }

        // Erasure: visit<AnonDeclaration>
        public  void visit(ASTBase.AnonDeclaration anon) {
            this.open(new BytePtr("anon union"));
            this.visitDecls(anon.decl);
            this.close();
        }

        // Erasure: visit<AlignDeclaration>
        public  void visit(ASTBase.AlignDeclaration d) {
            this.open(new BytePtr("align "));
            super.visit(d.ealign);
            (this.buf.get()).printf(new BytePtr(" "));
            this.visitDecls(d.decl);
            this.close();
        }

        // Erasure: visit<CPPMangleDeclaration>
        public  void visit(ASTBase.CPPMangleDeclaration mangle) {
            this.open(new BytePtr("cppmangle %d"), mangle.cppmangle);
            this.visitDecls(mangle.decl);
            this.close();
        }

        // Erasure: visit<ProtDeclaration>
        public  void visit(ASTBase.ProtDeclaration d) {
            this.open(new BytePtr("%s"), ASTBase.protectionToChars(d.protection.kind));
            this.visitDecls(d.decl);
            this.close();
        }

        // Erasure: visit<PragmaDeclaration>
        public  void visit(ASTBase.PragmaDeclaration d) {
            this.open(new BytePtr("pragma %s"), d.ident.toChars());
            this.visitExps(d.args);
            (this.buf.get()).writenl();
            this.visitDecls(d.decl);
            this.close();
        }

        // Erasure: visit<StorageClassDeclaration>
        public  void visit(ASTBase.StorageClassDeclaration d) {
            (this.buf.get()).printf(new BytePtr("( "));
            ASTBase.stcToBuffer(this.buf, d.stc);
            (this.buf.get()).level++;
            (this.buf.get()).writenl();
            this.visitDecls(d.decl);
            this.close();
        }

        // Erasure: visit<ConditionalDeclaration>
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

        // Erasure: visit<DeprecatedDeclaration>
        public  void visit(ASTBase.DeprecatedDeclaration d) {
            this.open(new BytePtr("deprecated"));
            this.visitDecls(d.decl);
            this.close();
        }

        // Erasure: visit<StaticIfDeclaration>
        public  void visit(ASTBase.StaticIfDeclaration sif) {
            this.open(new BytePtr("static if"));
            this.visitDecls(sif.decl);
            (this.buf.get()).printf(new BytePtr("else"));
            (this.buf.get()).writenl();
            this.visitDecls(sif.elsedecl);
            this.close();
        }

        // Erasure: visit<EnumMember>
        public  void visit(ASTBase.EnumMember em) {
            (this.buf.get()).printf(new BytePtr("( %s "), em.ident.toChars());
            if (em._init != null)
            {
                (this.buf.get()).printf(new BytePtr(" "));
                em._init.accept(this);
            }
            (this.buf.get()).printf(new BytePtr(" )"));
        }

        // Erasure: visit<Module>
        public  void visit(ASTBase.Module _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<StructDeclaration>
        public  void visit(ASTBase.StructDeclaration d) {
            this.open(new BytePtr("struct %s"), d.ident.toChars());
            this.visitDecls(d.members);
            this.close();
        }

        // Erasure: visit<UnionDeclaration>
        public  void visit(ASTBase.UnionDeclaration d) {
            this.open(new BytePtr("union %s"), d.ident.toChars());
            this.visitDecls(d.members);
            this.close();
        }

        // Erasure: visit<ClassDeclaration>
        public  void visit(ASTBase.ClassDeclaration d) {
            this.open(new BytePtr("class %s"), d.ident != null ? d.ident.toChars() : new BytePtr(""));
            if (d.baseclasses != null)
            {
                {
                    Slice<Ptr<ASTBase.BaseClass>> __r293 = (d.baseclasses.get()).opSlice().copy();
                    int __key292 = 0;
                    for (; (__key292 < __r293.getLength());__key292 += 1) {
                        Ptr<ASTBase.BaseClass> c = __r293.get(__key292);
                        int i = __key292;
                        if (i != 0)
                        {
                            (this.buf.get()).printf(new BytePtr(" "));
                        }
                        (c.get()).type.accept(this);
                    }
                }
                (this.buf.get()).writenl();
            }
            this.visitDecls(d.members);
            this.close();
        }

        // Erasure: visit<InterfaceDeclaration>
        public  void visit(ASTBase.InterfaceDeclaration d) {
            this.open(new BytePtr("interface %s"), d.ident.toChars());
            this.visitDecls(d.members);
            this.close();
        }

        // Erasure: visit<TemplateMixin>
        public  void visit(ASTBase.TemplateMixin m) {
            this.open(new BytePtr("template mixin"));
            this.visitDecls(m.members);
            this.close();
        }

        // Erasure: visit<Parameter>
        public  void visit(ASTBase.Parameter p) {
            (this.buf.get()).printf(new BytePtr("%s "), p.ident != null ? p.ident.toChars() : new BytePtr("anonymous"));
            if (p.type != null)
            {
                p.type.accept(this);
            }
        }

        // Erasure: visit<Statement>
        public  void visit(ASTBase.Statement _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<ImportStatement>
        public  void visit(ASTBase.ImportStatement imp) {
            this.open(new BytePtr("import"));
            this.visitDecls(imp.imports);
            this.close();
        }

        // Erasure: visit<ScopeStatement>
        public  void visit(ASTBase.ScopeStatement ss) {
            this.open(new BytePtr("{}"));
            if (ss.statement != null)
            {
                ss.statement.accept(this);
            }
            this.close();
        }

        // Erasure: visit<ReturnStatement>
        public  void visit(ASTBase.ReturnStatement r) {
            (this.buf.get()).printf(new BytePtr("(return "));
            if (r.exp != null)
            {
                r.exp.accept(this);
            }
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<LabelStatement>
        public  void visit(ASTBase.LabelStatement label) {
            this.open(new BytePtr("label %s"), label.ident.toChars());
            if (label.statement != null)
            {
                label.statement.accept(this);
            }
            this.close();
        }

        // Erasure: visit<StaticAssertStatement>
        public  void visit(ASTBase.StaticAssertStatement st) {
            st.sa.accept(this);
        }

        // Erasure: visit<CompileStatement>
        public  void visit(ASTBase.CompileStatement ct) {
            this.open(new BytePtr("compiletime"));
            this.visitExps(ct.exps);
            this.close();
        }

        // Erasure: visit<WhileStatement>
        public  void visit(ASTBase.WhileStatement st) {
            this.open(new BytePtr("while"));
            if (st.condition != null)
            {
                st.condition.accept(this);
            }
            (this.buf.get()).writenl();
            if (st._body != null)
            {
                st._body.accept(this);
            }
            this.close();
        }

        // Erasure: visit<ForStatement>
        public  void visit(ASTBase.ForStatement st) {
            this.open(new BytePtr("for"));
            if (st._init != null)
            {
                st._init.accept(this);
            }
            (this.buf.get()).writenl();
            if (st.condition != null)
            {
                st.condition.accept(this);
            }
            (this.buf.get()).writenl();
            if (st.increment != null)
            {
                st.increment.accept(this);
            }
            (this.buf.get()).writenl();
            if (st._body != null)
            {
                st._body.accept(this);
            }
            this.close();
        }

        // Erasure: visit<DoStatement>
        public  void visit(ASTBase.DoStatement st) {
            this.open(new BytePtr("do"));
            if (st._body != null)
            {
                st._body.accept(this);
            }
            (this.buf.get()).writenl();
            if (st.condition != null)
            {
                st.condition.accept(this);
            }
            this.close();
        }

        // Erasure: visit<ForeachRangeStatement>
        public  void visit(ASTBase.ForeachRangeStatement st) {
            this.open(new BytePtr("%s"), Token.toChars(st.op));
            if (st.prm != null)
            {
                st.prm.accept(this);
            }
            (this.buf.get()).writenl();
            if (st.lwr != null)
            {
                st.lwr.accept(this);
            }
            if (st.upr != null)
            {
                (this.buf.get()).printf(new BytePtr(".."));
                st.upr.accept(this);
            }
            if (st._body != null)
            {
                st._body.accept(this);
            }
            (this.buf.get()).writenl();
            this.close();
        }

        // Erasure: visit<ForeachStatement>
        public  void visit(ASTBase.ForeachStatement st) {
            this.open(new BytePtr("%s"), Token.toChars(st.op));
            if (st.parameters != null)
            {
                {
                    Slice<ASTBase.Parameter> __r294 = (st.parameters.get()).opSlice().copy();
                    int __key295 = 0;
                    for (; (__key295 < __r294.getLength());__key295 += 1) {
                        ASTBase.Parameter prm = __r294.get(__key295);
                        prm.accept(this);
                    }
                }
            }
            (this.buf.get()).writenl();
            if (st.aggr != null)
            {
                st.aggr.accept(this);
            }
            (this.buf.get()).writenl();
            if (st._body != null)
            {
                st._body.accept(this);
            }
            (this.buf.get()).writenl();
            this.close();
        }

        // Erasure: visit<IfStatement>
        public  void visit(ASTBase.IfStatement st) {
            this.open(new BytePtr("if"));
            if (st.prm != null)
            {
                st.prm.accept(this);
            }
            (this.buf.get()).writenl();
            if (st.condition != null)
            {
                st.condition.accept(this);
            }
            (this.buf.get()).writenl();
            if (st.ifbody != null)
            {
                st.ifbody.accept(this);
            }
            (this.buf.get()).writenl();
            if (st.elsebody != null)
            {
                this.open(new BytePtr("else"));
                st.elsebody.accept(this);
                this.close();
            }
            this.close();
        }

        // Erasure: visit<ScopeGuardStatement>
        public  void visit(ASTBase.ScopeGuardStatement sgt) {
            this.open(new BytePtr("scope %s "), Token.toChars(sgt.tok));
            if (sgt.statement != null)
            {
                sgt.statement.accept(this);
            }
            this.close();
        }

        // Erasure: visit<ConditionalStatement>
        public  void visit(ASTBase.ConditionalStatement st) {
            this.open(new BytePtr("static if"));
            if (st.condition != null)
            {
                st.condition.accept(this);
            }
            (this.buf.get()).writenl();
            if (st.ifbody != null)
            {
                st.ifbody.accept(this);
            }
            (this.buf.get()).writenl();
            if (st.elsebody != null)
            {
                this.open(new BytePtr("else"));
                st.elsebody.accept(this);
                this.close();
            }
            this.close();
        }

        // Erasure: visit<PragmaStatement>
        public  void visit(ASTBase.PragmaStatement st) {
            (this.buf.get()).printf(new BytePtr("( pragma %s"), st.ident.toChars());
            this.visitExps(st.args);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<SwitchStatement>
        public  void visit(ASTBase.SwitchStatement sw) {
            this.open(new BytePtr("%s switch"), sw.isFinal ? new BytePtr("final") : new BytePtr(""));
            if (sw._body != null)
            {
                sw._body.accept(this);
            }
            this.close();
        }

        // Erasure: visit<CaseRangeStatement>
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

        // Erasure: visit<CaseStatement>
        public  void visit(ASTBase.CaseStatement ct) {
            this.open(new BytePtr("case "));
            ct.exp.accept(this);
            (this.buf.get()).writenl();
            if (ct.statement != null)
            {
                ct.statement.accept(this);
            }
            this.close();
        }

        // Erasure: visit<DefaultStatement>
        public  void visit(ASTBase.DefaultStatement def) {
            this.open(new BytePtr("default"));
            if (def.statement != null)
            {
                def.statement.accept(this);
            }
            this.close();
        }

        // Erasure: visit<BreakStatement>
        public  void visit(ASTBase.BreakStatement brk) {
            (this.buf.get()).printf(new BytePtr("( break %s )"), brk.ident != null ? brk.ident.toChars() : new BytePtr(""));
            (this.buf.get()).writenl();
        }

        // Erasure: visit<ContinueStatement>
        public  void visit(ASTBase.ContinueStatement cont) {
            (this.buf.get()).printf(new BytePtr("( continue %s )"), cont.ident != null ? cont.ident.toChars() : new BytePtr(""));
            (this.buf.get()).writenl();
        }

        // Erasure: visit<GotoDefaultStatement>
        public  void visit(ASTBase.GotoDefaultStatement gds) {
            (this.buf.get()).printf(new BytePtr("( goto default )"));
            (this.buf.get()).writenl();
        }

        // Erasure: visit<GotoCaseStatement>
        public  void visit(ASTBase.GotoCaseStatement gcs) {
            (this.buf.get()).printf(new BytePtr("( goto case "));
            if (gcs.exp != null)
            {
                gcs.exp.accept(this);
            }
            (this.buf.get()).printf(new BytePtr(")"));
            (this.buf.get()).writenl();
        }

        // Erasure: visit<GotoStatement>
        public  void visit(ASTBase.GotoStatement gs) {
            (this.buf.get()).printf(new BytePtr("( goto %s)"), gs.ident.toChars());
            (this.buf.get()).writenl();
        }

        // Erasure: visit<SynchronizedStatement>
        public  void visit(ASTBase.SynchronizedStatement sync) {
            this.open(new BytePtr("synchronized"));
            if (sync._body != null)
            {
                sync._body.accept(this);
            }
            this.close();
        }

        // Erasure: visit<WithStatement>
        public  void visit(ASTBase.WithStatement w) {
            this.open(new BytePtr("with"));
            if (w.exp != null)
            {
                w.exp.accept(this);
            }
            (this.buf.get()).writenl();
            if (w._body != null)
            {
                w._body.accept(this);
            }
            this.close();
        }

        // Erasure: visit<TryCatchStatement>
        public  void visit(ASTBase.TryCatchStatement tc) {
            this.open(new BytePtr("try"));
            if (tc._body != null)
            {
                tc._body.accept(this);
            }
            if (tc.catches != null)
            {
                Slice<ASTBase.Catch> __r296 = (tc.catches.get()).opSlice().copy();
                int __key297 = 0;
                for (; (__key297 < __r296.getLength());__key297 += 1) {
                    ASTBase.Catch c = __r296.get(__key297);
                    this.open(new BytePtr("catch"));
                    if (c.type != null)
                    {
                        c.type.accept(this);
                        (this.buf.get()).printf(new BytePtr(" "));
                    }
                    if (c.ident != null)
                    {
                        (this.buf.get()).printf(new BytePtr("%s"), c.ident.toChars());
                    }
                    (this.buf.get()).writenl();
                    if (c.handler != null)
                    {
                        c.handler.accept(this);
                    }
                    this.close();
                }
            }
            this.close();
        }

        // Erasure: visit<TryFinallyStatement>
        public  void visit(ASTBase.TryFinallyStatement tf) {
            this.open(new BytePtr("try"));
            if (tf._body != null)
            {
                tf._body.accept(this);
            }
            if (tf.finalbody != null)
            {
                this.open(new BytePtr("finally"));
                tf.finalbody.accept(this);
                this.close();
            }
            this.close();
        }

        // Erasure: visit<ThrowStatement>
        public  void visit(ASTBase.ThrowStatement thr) {
            this.open(new BytePtr("throw"));
            thr.exp.accept(this);
            this.close();
        }

        // Erasure: visit<AsmStatement>
        public  void visit(ASTBase.AsmStatement ast) {
            Ptr<Token> t = ast.tokens;
            for (; t != null;){
                (this.buf.get()).printf(new BytePtr("%s"), (t.get()).toChars());
                t = pcopy((t.get()).next.value);
            }
        }

        // Erasure: visit<ExpStatement>
        public  void visit(ASTBase.ExpStatement s) {
            (this.buf.get()).printf(new BytePtr("( expr "));
            (this.buf.get()).writenl();
            (this.buf.get()).level++;
            s.exp.accept(this);
            (this.buf.get()).level--;
            (this.buf.get()).writenl();
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<CompoundStatement>
        public  void visit(ASTBase.CompoundStatement s) {
            this.open(new BytePtr(""));
            this.visitStatements(s.statements);
            this.close();
        }

        // Erasure: visit<CompoundDeclarationStatement>
        public  void visit(ASTBase.CompoundDeclarationStatement s) {
            this.visitStatements(s.statements);
        }

        // Erasure: visit<CompoundAsmStatement>
        public  void visit(ASTBase.CompoundAsmStatement st) {
            Ref<Long> stc = ref(st.stc);
            this.open(new BytePtr("asm %s"), ASTBase.stcToChars(stc));
            this.visitStatements(st.statements);
            this.close();
        }

        // Erasure: visit<InlineAsmStatement>
        public  void visit(ASTBase.InlineAsmStatement iasm) {
            this.visit((ASTBase.AsmStatement)iasm);
        }

        // Erasure: visit<Type>
        public  void visit(ASTBase.Type t) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<TypeBasic>
        public  void visit(ASTBase.TypeBasic t) {
            (this.buf.get()).printf(new BytePtr("%s %s"), modToChars((t.mod & 0xFF)), t.dstring);
        }

        // Erasure: visit<TypeError>
        public  void visit(ASTBase.TypeError _param_0) {
            (this.buf.get()).printf(new BytePtr("terror"));
        }

        // Erasure: visit<TypeNull>
        public  void visit(ASTBase.TypeNull _param_0) {
            (this.buf.get()).printf(new BytePtr("typeof(null)"));
        }

        // Erasure: visit<TypeVector>
        public  void visit(ASTBase.TypeVector t) {
            (this.buf.get()).printf(new BytePtr("( __vector "));
            if (t.basetype != null)
            {
                t.basetype.accept(this);
            }
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<TypeEnum>
        public  void visit(ASTBase.TypeEnum t) {
            (this.buf.get()).printf(new BytePtr("enum %s "), t.sym.toChars());
            t.sym.memtype.accept(this);
        }

        // Erasure: visit<TypeTuple>
        public  void visit(ASTBase.TypeTuple t) {
            (this.buf.get()).printf(new BytePtr("( typetuple "));
            if (t.arguments != null)
            {
                {
                    Slice<ASTBase.Parameter> __r299 = (t.arguments.get()).opSlice().copy();
                    int __key298 = 0;
                    for (; (__key298 < __r299.getLength());__key298 += 1) {
                        ASTBase.Parameter a = __r299.get(__key298);
                        int i = __key298;
                        if (i != 0)
                        {
                            (this.buf.get()).printf(new BytePtr(" "));
                        }
                        super.visit(a);
                    }
                }
            }
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<TypeClass>
        public  void visit(ASTBase.TypeClass tc) {
            (this.buf.get()).printf(new BytePtr("%s"), tc.sym.ident.toChars());
        }

        // Erasure: visit<TypeStruct>
        public  void visit(ASTBase.TypeStruct ts) {
            (this.buf.get()).printf(new BytePtr("%s"), ts.sym.ident.toChars());
        }

        // Erasure: visit<TypeNext>
        public  void visit(ASTBase.TypeNext _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<TypeReference>
        public  void visit(ASTBase.TypeReference t) {
            (this.buf.get()).printf(new BytePtr("ref "));
            super.visit(t.next.value);
        }

        // Erasure: visit<TypeSlice>
        public  void visit(ASTBase.TypeSlice ts) {
            (this.buf.get()).printf(new BytePtr("( slice "));
            ts.lwr.accept(this);
            (this.buf.get()).printf(new BytePtr(" "));
            ts.upr.accept(this);
            (this.buf.get()).printf(new BytePtr(" "));
            ts.next.value.accept(this);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<TypeDelegate>
        public  void visit(ASTBase.TypeDelegate td) {
            (this.buf.get()).printf(new BytePtr("delegate "));
            if (td.next.value != null)
            {
                td.next.value.accept(this);
            }
        }

        // Erasure: visit<TypePointer>
        public  void visit(ASTBase.TypePointer tp) {
            tp.next.value.accept(this);
            (this.buf.get()).printf(new BytePtr("*"));
        }

        // Erasure: visit<TypeFunction>
        public  void visit(ASTBase.TypeFunction tf) {
            if (tf.next.value != null)
            {
                tf.next.value.accept(this);
                (this.buf.get()).printf(new BytePtr(" "));
            }
            {
                Slice<ASTBase.Parameter> __r301 = (tf.parameterList.parameters.get()).opSlice().copy();
                int __key300 = 0;
                for (; (__key300 < __r301.getLength());__key300 += 1) {
                    ASTBase.Parameter p = __r301.get(__key300);
                    int i = __key300;
                    if (i != 0)
                    {
                        (this.buf.get()).printf(new BytePtr(" "));
                    }
                    p.accept(this);
                }
            }
        }

        // Erasure: visit<TypeArray>
        public  void visit(ASTBase.TypeArray _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<TypeDArray>
        public  void visit(ASTBase.TypeDArray d) {
            d.next.value.accept(this);
            (this.buf.get()).printf(new BytePtr("[]"));
        }

        // Erasure: visit<TypeAArray>
        public  void visit(ASTBase.TypeAArray ta) {
            ta.next.value.accept(this);
            (this.buf.get()).printf(new BytePtr("["));
            ta.index.accept(this);
            (this.buf.get()).printf(new BytePtr("]"));
        }

        // Erasure: visit<TypeSArray>
        public  void visit(ASTBase.TypeSArray tsa) {
            tsa.next.value.accept(this);
            (this.buf.get()).printf(new BytePtr("["));
            tsa.dim.accept(this);
            (this.buf.get()).printf(new BytePtr("]"));
        }

        // Erasure: visit<TypeQualified>
        public  void visit(ASTBase.TypeQualified _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<TypeTraits>
        public  void visit(ASTBase.TypeTraits tt) {
            (this.buf.get()).printf(new BytePtr("type __traits("));
            tt.exp.accept(this);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<TypeIdentifier>
        public  void visit(ASTBase.TypeIdentifier d) {
            (this.buf.get()).printf(new BytePtr("%s"), d.ident.toChars());
        }

        // Erasure: visit<TypeReturn>
        public  void visit(ASTBase.TypeReturn _param_0) {
            (this.buf.get()).printf(new BytePtr("typeof(return)"));
        }

        // Erasure: visit<TypeTypeof>
        public  void visit(ASTBase.TypeTypeof tt) {
            (this.buf.get()).printf(new BytePtr("typeof("));
            if (tt.exp != null)
            {
                tt.exp.accept(this);
            }
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<TypeInstance>
        public  void visit(ASTBase.TypeInstance ti) {
            (this.buf.get()).printf(new BytePtr("%s!("), ti.tempinst.tempdecl != null ? ti.tempinst.tempdecl.toChars() : ti.tempinst.name.toChars());
            this.visitTiargs(ti.tempinst.tiargs);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<Expression>
        public  void visit(ASTBase.Expression _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<DeclarationExp>
        public  void visit(ASTBase.DeclarationExp e) {
            if (e.declaration != null)
            {
                e.declaration.accept(this);
            }
        }

        // Erasure: visit<IntegerExp>
        public  void visit(ASTBase.IntegerExp e) {
            (this.buf.get()).printf(new BytePtr("%lld"), (long)e.value);
        }

        // Erasure: visit<NewAnonClassExp>
        public  void visit(ASTBase.NewAnonClassExp nc) {
            (this.buf.get()).printf(new BytePtr("( new anonclass %s "), nc.cd.ident.toChars());
            this.visitExps(nc.arguments);
            nc.cd.accept(this);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<IsExp>
        public  void visit(ASTBase.IsExp ie) {
            (this.buf.get()).printf(new BytePtr("( is %s"), Token.toChars(ie.tok));
            if (ie.id != null)
            {
                (this.buf.get()).printf(new BytePtr("%s "), ie.id.toChars());
            }
            if (ie.type != null)
            {
                ie.type.accept(this);
            }
        }

        // Erasure: visit<RealExp>
        public  void visit(ASTBase.RealExp r) {
            (this.buf.get()).printf(new BytePtr("%llf"), r.value);
        }

        // Erasure: visit<NullExp>
        public  void visit(ASTBase.NullExp _param_0) {
            (this.buf.get()).printf(new BytePtr("null"));
        }

        // Erasure: visit<TypeidExp>
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

        // Erasure: visit<TraitsExp>
        public  void visit(ASTBase.TraitsExp te) {
            (this.buf.get()).printf(new BytePtr("( __traits %s "), te.ident.toChars());
            if (te.args != null)
            {
                {
                    Slice<RootObject> __r302 = (te.args.get()).opSlice().copy();
                    int __key303 = 0;
                    for (; (__key303 < __r302.getLength());__key303 += 1) {
                        RootObject arg = __r302.get(__key303);
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

        // Erasure: visit<StringExp>
        public  void visit(ASTBase.StringExp exp) {
            if (exp.type != null)
            {
                exp.type.accept(this);
            }
            if (((exp.sz & 0xFF) == 1))
            {
                (this.buf.get()).printf(new BytePtr("\"\"\"%.*s\"\"\""), exp.len, exp.string);
            }
            else if (((exp.sz & 0xFF) == 2))
            {
                (this.buf.get()).printf(new BytePtr("\"\"\"%.*s\"\"\""), exp.len, exp.wstring);
            }
            else if (((exp.sz & 0xFF) == 4))
            {
                (this.buf.get()).printf(new BytePtr("\"\"\"%.*s\"\"\""), exp.len, exp.dstring);
            }
        }

        // Erasure: visit<NewExp>
        public  void visit(ASTBase.NewExp ne) {
            (this.buf.get()).printf(new BytePtr("( new "));
            this.visitExps(ne.arguments);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<AssocArrayLiteralExp>
        public  void visit(ASTBase.AssocArrayLiteralExp aa) {
            (this.buf.get()).printf(new BytePtr("( key[value] "));
            if (aa.keys != null)
            {
                Slice<ASTBase.Expression> __r305 = (aa.keys.get()).opSlice().copy();
                int __key304 = 0;
                for (; (__key304 < __r305.getLength());__key304 += 1) {
                    ASTBase.Expression key = __r305.get(__key304);
                    int i = __key304;
                    if (i != 0)
                    {
                        (this.buf.get()).printf(new BytePtr(" "));
                    }
                    ASTBase.Expression v = (aa.values.get()).get(i);
                    key.accept(this);
                    (this.buf.get()).printf(new BytePtr(" "));
                    v.accept(this);
                }
            }
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<ArrayLiteralExp>
        public  void visit(ASTBase.ArrayLiteralExp ae) {
            (this.buf.get()).printf(new BytePtr("( [] "));
            if (ae.elements != null)
            {
                Slice<ASTBase.Expression> __r307 = (ae.elements.get()).opSlice().copy();
                int __key306 = 0;
                for (; (__key306 < __r307.getLength());__key306 += 1) {
                    ASTBase.Expression el = __r307.get(__key306);
                    int i = __key306;
                    if (i != 0)
                    {
                        (this.buf.get()).printf(new BytePtr(" "));
                    }
                    ASTBase.Expression e = el != null ? el : ae.basis;
                    if (e != null)
                    {
                        e.accept(this);
                    }
                }
            }
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<FuncExp>
        public  void visit(ASTBase.FuncExp fe) {
            this.open(new BytePtr("func-expr"));
            if (fe.fd != null)
            {
                fe.fd.accept(this);
            }
            else if (fe.td != null)
            {
                fe.td.accept(this);
            }
            this.close();
        }

        // Erasure: visit<IntervalExp>
        public  void visit(ASTBase.IntervalExp ival) {
            ival.lwr.accept(this);
            (this.buf.get()).printf(new BytePtr(" .. "));
            if (ival.upr != null)
            {
                ival.upr.accept(this);
            }
            else
            {
                (this.buf.get()).printf(new BytePtr("$"));
            }
        }

        // Erasure: visit<TypeExp>
        public  void visit(ASTBase.TypeExp te) {
            if (te.type != null)
            {
                te.type.accept(this);
            }
        }

        // Erasure: visit<ScopeExp>
        public  void visit(ASTBase.ScopeExp s) {
            this.open(new BytePtr("{}"));
            if (s.sds != null)
            {
                s.sds.accept(this);
            }
            this.close();
        }

        // Erasure: visit<IdentifierExp>
        public  void visit(ASTBase.IdentifierExp e) {
            (this.buf.get()).printf(new BytePtr("%s"), e.ident.toChars());
        }

        // Erasure: visit<UnaExp>
        public  void visit(ASTBase.UnaExp e) {
            (this.buf.get()).printf(new BytePtr("%s"), Token.toChars(e.op));
            e.e1.accept(this);
        }

        // Erasure: visit<DefaultInitExp>
        public  void visit(ASTBase.DefaultInitExp ie) {
            if (ie.type != null)
            {
                ie.type.accept(this);
            }
            (this.buf.get()).printf(new BytePtr(" init"));
        }

        // Erasure: visit<BinExp>
        public  void visit(ASTBase.BinExp e) {
            (this.buf.get()).printf(new BytePtr("( %s "), Token.toChars(e.op));
            e.e1.accept(this);
            (this.buf.get()).printf(new BytePtr(" "));
            e.e2.accept(this);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<DsymbolExp>
        public  void visit(ASTBase.DsymbolExp e) {
            (this.buf.get()).printf(new BytePtr("%s"), e.s.ident.toChars());
        }

        // Erasure: visit<TemplateExp>
        public  void visit(ASTBase.TemplateExp e) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<SymbolExp>
        public  void visit(ASTBase.SymbolExp e) {
            (this.buf.get()).printf(new BytePtr("( symbol "));
            e.type.accept(this);
            (this.buf.get()).printf(new BytePtr(" %s"), e.var.ident.toChars());
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<VarExp>
        public  void visit(ASTBase.VarExp e) {
            (this.buf.get()).printf(new BytePtr("( var "));
            e.type.accept(this);
            (this.buf.get()).printf(new BytePtr(" %s"), e.var.ident.toChars());
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<TupleExp>
        public  void visit(ASTBase.TupleExp e) {
            this.open(new BytePtr("tuple-exp"));
            this.visitExps(e.exps);
            this.close();
        }

        // Erasure: visit<DollarExp>
        public  void visit(ASTBase.DollarExp e) {
            (this.buf.get()).printf(new BytePtr("$"));
        }

        // Erasure: visit<ThisExp>
        public  void visit(ASTBase.ThisExp e) {
            (this.buf.get()).printf(new BytePtr("this"));
        }

        // Erasure: visit<SuperExp>
        public  void visit(ASTBase.SuperExp e) {
            (this.buf.get()).printf(new BytePtr("super"));
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
            (this.buf.get()).printf(new BytePtr("( cast "));
            if (e.to != null)
            {
                e.to.accept(this);
                (this.buf.get()).printf(new BytePtr(" "));
            }
            e.e1.accept(this);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<CallExp>
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

        // Erasure: visit<DotIdExp>
        public  void visit(ASTBase.DotIdExp e) {
            (this.buf.get()).printf(new BytePtr("( . "));
            e.e1.accept(this);
            (this.buf.get()).printf(new BytePtr(" %s"), e.ident.toChars());
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<AssertExp>
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

        // Erasure: visit<CompileExp>
        public  void visit(ASTBase.CompileExp c) {
            (this.buf.get()).printf(new BytePtr("( mixin "));
            this.visitExps(c.exps);
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<ImportExp>
        public  void visit(ASTBase.ImportExp ie) {
            this.visit((ASTBase.UnaExp)ie);
        }

        // Erasure: visit<DotTemplateInstanceExp>
        public  void visit(ASTBase.DotTemplateInstanceExp e) {
            (this.buf.get()).printf(new BytePtr("( . "));
            e.e1.accept(this);
            (this.buf.get()).printf(new BytePtr(" %s"), e.ti.ident != null ? e.ti.ident.toChars() : e.ti.name.toChars());
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<ArrayExp>
        public  void visit(ASTBase.ArrayExp arr) {
            if (arr.e1 != null)
            {
                arr.e1.accept(this);
            }
            if (arr.arguments != null)
            {
                Slice<ASTBase.Expression> __r308 = (arr.arguments.get()).opSlice().copy();
                int __key309 = 0;
                for (; (__key309 < __r308.getLength());__key309 += 1) {
                    ASTBase.Expression arg = __r308.get(__key309);
                    arg.accept(this);
                }
            }
        }

        // Erasure: visit<FuncInitExp>
        public  void visit(ASTBase.FuncInitExp e) {
            (this.buf.get()).printf(new BytePtr("func-init "));
            this.visit((ASTBase.DefaultInitExp)e);
        }

        // Erasure: visit<PrettyFuncInitExp>
        public  void visit(ASTBase.PrettyFuncInitExp e) {
            (this.buf.get()).printf(new BytePtr("pretty-init "));
            this.visit((ASTBase.DefaultInitExp)e);
        }

        // Erasure: visit<FileInitExp>
        public  void visit(ASTBase.FileInitExp e) {
            (this.buf.get()).printf(new BytePtr("file-init "));
            this.visit((ASTBase.DefaultInitExp)e);
        }

        // Erasure: visit<LineInitExp>
        public  void visit(ASTBase.LineInitExp e) {
            (this.buf.get()).printf(new BytePtr("line-init "));
            this.visit((ASTBase.DefaultInitExp)e);
        }

        // Erasure: visit<ModuleInitExp>
        public  void visit(ASTBase.ModuleInitExp e) {
            (this.buf.get()).printf(new BytePtr("module-init "));
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
            (this.buf.get()).printf(new BytePtr("( ? "));
            if (e.econd != null)
            {
                e.econd.accept(this);
            }
            if (e.e1 != null)
            {
                e.e1.accept(this);
            }
            if (e.e2 != null)
            {
                e.e2.accept(this);
            }
            (this.buf.get()).printf(new BytePtr(")"));
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
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<MinAssignExp>
        public  void visit(ASTBase.MinAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<MulAssignExp>
        public  void visit(ASTBase.MulAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<DivAssignExp>
        public  void visit(ASTBase.DivAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<ModAssignExp>
        public  void visit(ASTBase.ModAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<PowAssignExp>
        public  void visit(ASTBase.PowAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<AndAssignExp>
        public  void visit(ASTBase.AndAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<OrAssignExp>
        public  void visit(ASTBase.OrAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<XorAssignExp>
        public  void visit(ASTBase.XorAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<ShlAssignExp>
        public  void visit(ASTBase.ShlAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<ShrAssignExp>
        public  void visit(ASTBase.ShrAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<UshrAssignExp>
        public  void visit(ASTBase.UshrAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<CatAssignExp>
        public  void visit(ASTBase.CatAssignExp e) {
            this.visit((ASTBase.BinExp)e);
        }

        // Erasure: visit<TemplateParameter>
        public  void visit(ASTBase.TemplateParameter tp) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<TemplateAliasParameter>
        public  void visit(ASTBase.TemplateAliasParameter tp) {
            (this.buf.get()).printf(new BytePtr("alias %s"), tp.ident.toChars());
        }

        // Erasure: visit<TemplateTypeParameter>
        public  void visit(ASTBase.TemplateTypeParameter tp) {
        }

        // Erasure: visit<TemplateTupleParameter>
        public  void visit(ASTBase.TemplateTupleParameter tp) {
            (this.buf.get()).printf(new BytePtr("alias %s"), tp.ident.toChars());
        }

        // Erasure: visit<TemplateValueParameter>
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

        // Erasure: visit<TemplateThisParameter>
        public  void visit(ASTBase.TemplateThisParameter tp) {
            (this.buf.get()).printf(new BytePtr("template-this"));
        }

        // Erasure: visit<Condition>
        public  void visit(ASTBase.Condition _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<StaticForeachStatement>
        public  void visit(ASTBase.StaticForeachStatement sfst) {
            if (sfst.sfe.aggrfe != null)
            {
                sfst.sfe.aggrfe.accept(this);
            }
            else
            {
                sfst.sfe.rangefe.accept(this);
            }
        }

        // Erasure: visit<StaticIfCondition>
        public  void visit(ASTBase.StaticIfCondition cond) {
            cond.exp.accept(this);
        }

        // Erasure: visit<DVCondition>
        public  void visit(ASTBase.DVCondition _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<DebugCondition>
        public  void visit(ASTBase.DebugCondition d) {
            (this.buf.get()).printf(new BytePtr("debug %s"), d.ident != null ? d.ident.toChars() : new BytePtr(""));
        }

        // Erasure: visit<VersionCondition>
        public  void visit(ASTBase.VersionCondition ver) {
            (this.buf.get()).printf(new BytePtr("%s"), ver.ident.toChars());
        }

        // Erasure: visit<Initializer>
        public  void visit(ASTBase.Initializer _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: visit<ExpInitializer>
        public  void visit(ASTBase.ExpInitializer ei) {
            if (ei.exp != null)
            {
                ei.exp.accept(this);
            }
        }

        // Erasure: visit<StructInitializer>
        public  void visit(ASTBase.StructInitializer si) {
            (this.buf.get()).printf(new BytePtr("( struct-init "));
            {
                Slice<Identifier> __r311 = si.field.opSlice().copy();
                int __key310 = 0;
                for (; (__key310 < __r311.getLength());__key310 += 1) {
                    Identifier id = __r311.get(__key310);
                    int i = __key310;
                    if (i != 0)
                    {
                        (this.buf.get()).printf(new BytePtr(" "));
                    }
                    if (id != null)
                    {
                        (this.buf.get()).writestring(id.asString());
                        (this.buf.get()).writeByte(58);
                    }
                    {
                        ASTBase.Initializer iz = si.value.get(i);
                        if ((iz) != null)
                        {
                            iz.accept(this);
                        }
                    }
                }
            }
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<ArrayInitializer>
        public  void visit(ASTBase.ArrayInitializer ai) {
            (this.buf.get()).printf(new BytePtr("( array-init "));
            {
                Slice<ASTBase.Initializer> __r313 = ai.value.opSlice().copy();
                int __key312 = 0;
                for (; (__key312 < __r313.getLength());__key312 += 1) {
                    ASTBase.Initializer v = __r313.get(__key312);
                    int i = __key312;
                    if (i != 0)
                    {
                        (this.buf.get()).printf(new BytePtr(" "));
                    }
                    if (v != null)
                    {
                        v.accept(this);
                    }
                    else
                    {
                        (this.buf.get()).printf(new BytePtr("null"));
                    }
                }
            }
            (this.buf.get()).printf(new BytePtr(")"));
        }

        // Erasure: visit<VoidInitializer>
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
    // Erasure: main<Array>
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
        global.params.isLinux = true;
        global.params.useUnitTests = true;
        global._init();
        ASTBase.Type._init();
        Id.initialize();
        {
            Slice<ByteSlice> __r509 = args_ref.value.slice(1,args_ref.value.getLength()).copy();
            int __key510 = 0;
            for (; (__key510 < __r509.getLength());__key510 += 1) {
                ByteSlice arg = __r509.get(__key510).copy();
                if (__equals(tool.value, new ByteSlice("lex")))
                {
                    processFile_D1F3732A9A6A6D5A(arg, outdir.value, new BytePtr("tk"));
                }
                else if (__equals(tool.value, new ByteSlice("lispy")))
                {
                    processFile_24239CC9FAA32FB7(arg, outdir.value, new BytePtr("ast"));
                }
                else
                {
                    fprintf(stderr, new BytePtr("Unsupported tool name: %.*s"), tool.value.getLength(), tool.value.getPtr(0));
                    exit(2);
                }
            }
        }
        exit(0);
    }

    // Erasure: lex<Ptr, Array>
    public static ByteSlice lex(BytePtr argz, ByteSlice buf) {
        Lexer lexer = new Lexer(argz, buf.getPtr(0), 0, buf.getLength(), true, true, new StderrDiagnosticReporter(DiagnosticReporting.error));
        Ptr<OutBuffer> output = refPtr(new OutBuffer(null, 0, 0, 0, false, false));
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
        {
            (output.get()).printf(new BytePtr(" | Line %5d |\n"), lexer.token.value.loc.linnum);
        }
        return (output.get()).extractSlice();
    }

    // Erasure: lispy<Ptr, Array>
    public static ByteSlice lispy(BytePtr argz, ByteSlice buf) {
        StderrDiagnosticReporter diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
        try {
            ASTBase.Module mod = new ASTBase.Module(argz, Identifier.idPool(argz.slice(0,strlen(argz) - 2)), 1, 0);
            ParserASTBase p = new ParserASTBase(mod, buf, true, diagnosticReporter);
            try {
                p.nextToken();
                Ptr<DArray<ASTBase.Dsymbol>> decls = p.parseModule();
                LispyPrint lispPrint = new LispyPrint();
                lispPrint.buf = pcopy((refPtr(new OutBuffer(null, 0, 0, 0, false, false))));
                (lispPrint.buf.get()).doindent = true;
                {
                    Slice<ASTBase.Dsymbol> __r513 = (decls.get()).opSlice().copy();
                    int __key514 = 0;
                    for (; (__key514 < __r513.getLength());__key514 += 1) {
                        ASTBase.Dsymbol d = __r513.get(__key514);
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
    // Erasure: processFile_24239CC9FAA32FB7<Array, Array, Ptr>
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
            ByteSlice filePath = concat(toByteSlice((concat(outdir, new ByteSlice("/")))), dest.slice(0,strlen(dest))).copy();
            ByteSlice output = lispy(toBytePtr(argz), toByteSlice(buf)).copy();
            if (!File.write(toStringz(filePath), toByteSlice(output)))
            {
                fprintf(stderr, new BytePtr("Failed to write file: %s\n"), toStringz(filePath));
            }
        }
        finally {
        }
    }


    // from template processFile!(_D1F3732A9A6A6D5A)
    // Erasure: processFile_D1F3732A9A6A6D5A<Array, Array, Ptr>
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
            ByteSlice filePath = concat(toByteSlice((concat(outdir, new ByteSlice("/")))), dest.slice(0,strlen(dest))).copy();
            ByteSlice output = lex(toBytePtr(argz), toByteSlice(buf)).copy();
            if (!File.write(toStringz(filePath), toByteSlice(output)))
            {
                fprintf(stderr, new BytePtr("Failed to write file: %s\n"), toStringz(filePath));
            }
        }
        finally {
        }
    }


}
