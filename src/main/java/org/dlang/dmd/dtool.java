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
            return new BytePtr("unknown");
        }
    }

    public static class LispyPrint extends ParseTimeTransitiveVisitorASTBase
    {
        public OutBuffer buf;
        public  void open(BytePtr format, Object... ap) {
            (this.buf).writestring( new ByteSlice("( "));
            (this.buf).vprintf(format, new Slice<>(ap));
            (this.buf).level++;
            (this.buf).writenl();
        }

        public  void close() {
            (this.buf).level--;
            (this.buf).writenl();
            (this.buf).printf( new ByteSlice(")"));
            (this.buf).writenl();
        }

        public  void visitDecls(DArray<ASTBase.Dsymbol> decls) {
            if (decls != null)
            {
                {
                    Slice<ASTBase.Dsymbol> __r200 = (decls).opSlice().copy();
                    int __key201 = 0;
                    for (; __key201 < __r200.getLength();__key201 += 1) {
                        ASTBase.Dsymbol m = __r200.get(__key201);
                        m.accept(this);
                    }
                }
            }
        }

        public  void visitExps(DArray<ASTBase.Expression> exps) {
            if (exps != null)
            {
                {
                    Slice<ASTBase.Expression> __r202 = (exps).opSlice().copy();
                    int __key203 = 0;
                    for (; __key203 < __r202.getLength();__key203 += 1) {
                        ASTBase.Expression e = __r202.get(__key203);
                        e.accept(this);
                        (this.buf).writenl();
                    }
                }
            }
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
            (this.buf).printf( new ByteSlice("debug"));
        }

        public  void visit(ASTBase.VersionSymbol ver) {
            (this.buf).printf( new ByteSlice("version"));
        }

        public  void visit(ASTBase.VarDeclaration d) {
            (this.buf).printf( new ByteSlice("( var %s "), d.ident.toChars());
            if (d.type != null)
                d.type.accept(this);
            if (d._init != null)
            {
                (this.buf).printf( new ByteSlice(" "));
                d._init.accept(this);
            }
            (this.buf).printf( new ByteSlice(")"));
        }

        public  void visit(ASTBase.FuncDeclaration d) {
            this.open(new BytePtr("func %s"), d.ident.toChars());
            d.type.accept(this);
            if (d.fbody != null)
                d.fbody.accept(this);
            this.close();
        }

        public  void visit(ASTBase.AliasDeclaration d) {
            this.open(new BytePtr("alias %s"), d.ident.toChars());
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.TupleDeclaration d) {
            this.open(new BytePtr("tuple"));
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.FuncLiteralDeclaration d) {
            this.open(new BytePtr("func literal"));
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.PostBlitDeclaration d) {
            this.open(new BytePtr("this(this)"));
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.CtorDeclaration d) {
            this.open(new BytePtr("ctor"));
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.DtorDeclaration d) {
            this.open(new BytePtr("dtor"));
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.InvariantDeclaration d) {
            this.open(new BytePtr("invariant"));
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.UnitTestDeclaration d) {
            this.open(new BytePtr("unittest"));
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.NewDeclaration d) {
            this.open(new BytePtr("newdecl"));
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.DeleteDeclaration d) {
            this.open(new BytePtr("deletedecl"));
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.StaticCtorDeclaration d) {
            this.open(new BytePtr("static this"));
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.StaticDtorDeclaration d) {
            this.open(new BytePtr("static ~this"));
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.SharedStaticCtorDeclaration d) {
            this.open(new BytePtr("shared static this"));
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.SharedStaticDtorDeclaration d) {
            this.open(new BytePtr("shared static ~this"));
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.Package d) {
            this.open(new BytePtr("package"));
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.EnumDeclaration d) {
            this.open(new BytePtr("enum %s"), d.ident.toChars());
            super.visit(d);
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
            this.open(new BytePtr("template instance %s"), ti.ident.toChars());
            super.visit(ti);
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
            this.open(new BytePtr("%s"), link.ident.toChars());
            this.visitDecls(link.decl);
            this.close();
        }

        public  void visit(ASTBase.AnonDeclaration anon) {
            this.open(new BytePtr("anon union %s"), anon.ident.toChars());
            this.visitDecls(anon.decl);
            this.close();
        }

        public  void visit(ASTBase.AlignDeclaration d) {
            this.open(new BytePtr("align "));
            super.visit(d.ealign);
            (this.buf).printf( new ByteSlice(" "));
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
            (this.buf).writenl();
            this.visitDecls(d.decl);
            this.close();
        }

        public  void visit(ASTBase.StorageClassDeclaration d) {
            (this.buf).printf( new ByteSlice("( "));
            ASTBase.stcToBuffer(this.buf, d.stc);
            (this.buf).level++;
            (this.buf).writenl();
            this.visitDecls(d.decl);
            this.close();
        }

        public  void visit(ASTBase.ConditionalDeclaration ver) {
            this.open(new BytePtr("version %s "), ver.ident != null ? ver.ident.toChars() : new BytePtr(""));
            ver.condition.accept(this);
            (this.buf).printf( new ByteSlice(" "));
            (this.buf).writenl();
            this.visitDecls(ver.decl);
            (this.buf).printf( new ByteSlice(" else "));
            (this.buf).writenl();
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
            (this.buf).printf( new ByteSlice("else"));
            (this.buf).writenl();
            this.visitDecls(sif.elsedecl);
            this.close();
        }

        public  void visit(ASTBase.EnumMember em) {
            (this.buf).printf( new ByteSlice("( %s"), em.ident.toChars());
            if (em._init != null)
            {
                printf( new ByteSlice(" "));
                em._init.accept(this);
            }
            (this.buf).printf( new ByteSlice(" )"));
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
                    Slice<ASTBase.BaseClass> __r205 = (d.baseclasses).opSlice().copy();
                    int __key204 = 0;
                    for (; __key204 < __r205.getLength();__key204 += 1) {
                        ASTBase.BaseClass c = __r205.get(__key204);
                        int i = __key204;
                        if ((i) != 0)
                            printf( new ByteSlice(" "));
                        (c).type.accept(this);
                    }
                }
                (this.buf).writenl();
            }
            this.visitDecls(d.members);
            this.close();
        }

        public  void visit(ASTBase.InterfaceDeclaration d) {
            this.open(new BytePtr("interface %s"), d.ident.toChars());
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.TemplateMixin m) {
            this.open(new BytePtr("template mixin"));
            super.visit(m);
            this.close();
        }

        public  void visit(ASTBase.Parameter p) {
            (this.buf).printf( new ByteSlice("%s "), p.ident.toChars());
            p.type.accept(this);
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

        public  void visit(ASTBase.ExpStatement s) {
            (this.buf).printf( new ByteSlice("( expr "));
            (this.buf).writenl();
            (this.buf).level++;
            s.exp.accept(this);
            (this.buf).level--;
            (this.buf).writenl();
            (this.buf).printf( new ByteSlice(")"));
        }

        public  void visit(ASTBase.CompoundStatement s) {
            (this.buf).printf( new ByteSlice("("));
            (this.buf).writenl();
            (this.buf).level++;
            if (s.statements != null)
            {
                Slice<ASTBase.Statement> __r207 = (s.statements).opSlice().copy();
                int __key206 = 0;
                for (; __key206 < __r207.getLength();__key206 += 1) {
                    ASTBase.Statement st = __r207.get(__key206);
                    int i = __key206;
                    if ((i) != 0)
                        (this.buf).writenl();
                    st.accept(this);
                }
            }
            (this.buf).level--;
            (this.buf).writenl();
            (this.buf).printf( new ByteSlice(")"));
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

        public  void visit(ASTBase.Type t) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeBasic t) {
            (this.buf).printf( new ByteSlice("%s %s"), modToChars((t.mod & 0xFF)), t.dstring);
        }

        public  void visit(ASTBase.TypeError _param_0) {
            (this.buf).printf( new ByteSlice("terror"));
        }

        public  void visit(ASTBase.TypeNull _param_0) {
            (this.buf).printf( new ByteSlice("typeof(null)"));
        }

        public  void visit(ASTBase.TypeVector t) {
            (this.buf).printf( new ByteSlice("( __vector"));
            super.visit(t);
            (this.buf).printf( new ByteSlice(")"));
        }

        public  void visit(ASTBase.TypeEnum t) {
            (this.buf).printf( new ByteSlice("enum %s : "), t.sym.toChars());
            super.visit(t.sym.memtype);
        }

        public  void visit(ASTBase.TypeTuple t) {
            (this.buf).printf( new ByteSlice("( typetuple "));
            if (t.arguments != null)
            {
                {
                    Slice<ASTBase.Parameter> __r209 = (t.arguments).opSlice().copy();
                    int __key208 = 0;
                    for (; __key208 < __r209.getLength();__key208 += 1) {
                        ASTBase.Parameter a = __r209.get(__key208);
                        int i = __key208;
                        if ((i) != 0)
                            (this.buf).printf( new ByteSlice(" "));
                        super.visit(a);
                    }
                }
            }
            (this.buf).printf( new ByteSlice(")"));
        }

        public  void visit(ASTBase.TypeClass tc) {
            (this.buf).printf( new ByteSlice("%s"), tc.sym.ident.toChars());
        }

        public  void visit(ASTBase.TypeStruct ts) {
            (this.buf).printf( new ByteSlice("%s"), ts.sym.ident.toChars());
        }

        public  void visit(ASTBase.TypeNext _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeReference t) {
            (this.buf).printf( new ByteSlice("ref "));
            super.visit(t.next);
        }

        public  void visit(ASTBase.TypeSlice ts) {
            (this.buf).printf( new ByteSlice("( slice "));
            this.visit(ts.lwr);
            (this.buf).printf( new ByteSlice(" "));
            this.visit(ts.upr);
            (this.buf).printf( new ByteSlice(" "));
            this.visit(ts.next);
            (this.buf).printf( new ByteSlice(")"));
        }

        public  void visit(ASTBase.TypeDelegate td) {
            (this.buf).printf( new ByteSlice("delegate "));
            if (td.next != null)
                td.next.accept(this);
        }

        public  void visit(ASTBase.TypePointer tp) {
            tp.next.accept(this);
            (this.buf).printf( new ByteSlice("*"));
        }

        public  void visit(ASTBase.TypeFunction tf) {
            tf.next.accept(this);
            (this.buf).printf( new ByteSlice(" "));
            {
                Slice<ASTBase.Parameter> __r211 = (tf.parameterList.parameters).opSlice().copy();
                int __key210 = 0;
                for (; __key210 < __r211.getLength();__key210 += 1) {
                    ASTBase.Parameter p = __r211.get(__key210);
                    int i = __key210;
                    if ((i) != 0)
                        (this.buf).printf( new ByteSlice(" "));
                    p.accept(this);
                }
            }
        }

        public  void visit(ASTBase.TypeArray _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeDArray d) {
            d.next.accept(this);
            (this.buf).printf( new ByteSlice("[]"));
        }

        public  void visit(ASTBase.TypeAArray ta) {
            ta.next.accept(this);
            (this.buf).printf( new ByteSlice("["));
            ta.index.accept(this);
            (this.buf).printf( new ByteSlice("]"));
        }

        public  void visit(ASTBase.TypeSArray tsa) {
            tsa.next.accept(this);
            (this.buf).printf( new ByteSlice("["));
            tsa.dim.accept(this);
            (this.buf).printf( new ByteSlice("]"));
        }

        public  void visit(ASTBase.TypeQualified _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeTraits _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeIdentifier d) {
            (this.buf).printf( new ByteSlice("%s"), d.ident.toChars());
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

        public  void visit(ASTBase.DeclarationExp e) {
            if (e.declaration != null)
                e.declaration.accept(this);
        }

        public  void visit(ASTBase.IntegerExp e) {
            (this.buf).printf( new ByteSlice("%lld"), e.value);
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

        public  void visit(ASTBase.StringExp exp) {
            if (exp.type != null)
                exp.type.accept(this);
            if ((exp.sz & 0xFF) == 1)
                (this.buf).printf( new ByteSlice("\"\"\"%.*s\"\"\""), exp.len, exp.string);
            else if ((exp.sz & 0xFF) == 2)
            {
                (this.buf).printf( new ByteSlice("\"\"\"%.*s\"\"\""), exp.len, exp.wstring);
            }
            else if ((exp.sz & 0xFF) == 4)
            {
                (this.buf).printf( new ByteSlice("\"\"\"%.*s\"\"\""), exp.len, exp.dstring);
            }
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

        public  void visit(ASTBase.IntervalExp ival) {
            ival.lwr.accept(this);
            (this.buf).printf( new ByteSlice(" .. "));
            if (ival.upr != null)
                ival.upr.accept(this);
            else
                (this.buf).printf( new ByteSlice("$"));
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
            (this.buf).printf( new ByteSlice("%s"), e.ident.toChars());
        }

        public  void visit(ASTBase.UnaExp e) {
            (this.buf).printf( new ByteSlice("%s"), Token.toChars(e.op));
            e.e1.accept(this);
        }

        public  void visit(ASTBase.DefaultInitExp ie) {
            if (ie.type != null)
                ie.type.accept(this);
            (this.buf).printf( new ByteSlice(" init"));
        }

        public  void visit(ASTBase.BinExp e) {
            (this.buf).printf( new ByteSlice("( %s "), Token.toChars(e.op));
            e.e1.accept(this);
            (this.buf).printf( new ByteSlice(" "));
            e.e2.accept(this);
            (this.buf).printf( new ByteSlice(")"));
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

        public  void visit(ASTBase.ArrayExp arr) {
            if (arr.e1 != null)
                arr.e1.accept(this);
            if (arr.arguments != null)
            {
                Slice<ASTBase.Expression> __r212 = (arr.arguments).opSlice().copy();
                int __key213 = 0;
                for (; __key213 < __r212.getLength();__key213 += 1) {
                    ASTBase.Expression arg = __r212.get(__key213);
                    arg.accept(this);
                }
            }
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
            (this.buf).printf( new ByteSlice("( ? "));
            if (e.econd != null)
                e.econd.accept(this);
            if (e.e1 != null)
                e.e1.accept(this);
            if (e.e2 != null)
                e.e2.accept(this);
            (this.buf).printf( new ByteSlice(")"));
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

        public  void visit(ASTBase.ExpInitializer ei) {
            if (ei.exp != null)
                ei.exp.accept(this);
            else
                (this.buf).printf( new ByteSlice("null"));
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
        Ref<Slice<ByteSlice>> args_ref = ref(args);
        Ref<ByteSlice> outdir = ref( new ByteSlice(".").copy());
        Ref<ByteSlice> tool = ref( new ByteSlice("lex").copy());
        GetoptResult res = getopt(args_ref,  new ByteSlice("outdir"),  new ByteSlice("output directory"), ptr(outdir),  new ByteSlice("tool"),  new ByteSlice("select tool - lex or lispy"), ptr(tool)).copy();
        if (res.helpWanted)
        {
            defaultGetoptPrinter( new ByteSlice("Trivial D lexer based on DMD."), res.options);
            exit(1);
        }
        global.params.isLinux = true;
        global._init();
        ASTBase.Type._init();
        Id.initialize();
        {
            Slice<ByteSlice> __r409 = args_ref.value.slice(1,args_ref.value.getLength()).copy();
            int __key410 = 0;
            for (; __key410 < __r409.getLength();__key410 += 1) {
                ByteSlice arg = __r409.get(__key410).copy();
                if (__equals(tool.value,  new ByteSlice("lex")))
                    processFile_lex(arg, outdir.value, new BytePtr("tk"));
                else if (__equals(tool.value,  new ByteSlice("lispy")))
                    processFile_lispy(arg, outdir.value, new BytePtr("ast"));
                else
                {
                    fprintf(stderr,  new ByteSlice("Unsupported tool name: %.*s"), tool.value.getLength(), toBytePtr(tool.value));
                    exit(2);
                }
            }
        }
        exit(0);
    }

    public static ByteSlice lex(BytePtr argz, ByteSlice buf) {
        Lexer lexer = new Lexer(argz, toBytePtr(buf), 0, buf.getLength(), true, true, new StderrDiagnosticReporter(DiagnosticReporting.error));
        OutBuffer output = new OutBuffer(null, 0, 0, 0, false, false);
        int i = 0;
        for (; (lexer.nextToken() & 0xFF) != 11;){
            (output).printf( new ByteSlice("%4d"), (lexer.token.value & 0xFF));
            if ((i += 1) == 20)
            {
                (output).printf( new ByteSlice(" | Line %5d |\n"), lexer.token.loc.linnum);
                i = 0;
            }
        }
        if (i != 0)
            (output).printf( new ByteSlice(" | Line %5d |\n"), lexer.token.loc.linnum);
        return (output).extractSlice();
    }

    public static ByteSlice lispy(BytePtr argz, ByteSlice buf) {
        StderrDiagnosticReporter diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
        try {
            ParserASTBase p = new ParserASTBase(null, buf, true, diagnosticReporter);
            try {
                p.nextToken();
                DArray<ASTBase.Dsymbol> decls = p.parseModule();
                LispyPrint lispPrint = new LispyPrint();
                lispPrint.buf = new OutBuffer(null, 0, 0, 0, false, false);
                (lispPrint.buf).doindent = true;
                {
                    Slice<ASTBase.Dsymbol> __r413 = (decls).opSlice().copy();
                    int __key414 = 0;
                    for (; __key414 < __r413.getLength();__key414 += 1) {
                        ASTBase.Dsymbol d = __r413.get(__key414);
                        d.accept(lispPrint);
                    }
                }
                return (lispPrint.buf).extractSlice();
            }
            finally {
            }
        }
        finally {
        }
    }

    // from template processFile!(_lispy)
    public static void processFile_lispy(ByteSlice arg, ByteSlice outdir, BytePtr suffix) {
        BytePtr argz = pcopy(toStringz(arg));
        File.ReadResult buffer = File.read(toBytePtr(argz)).copy();
        try {
            if (!(buffer.success))
            {
                fprintf(stderr,  new ByteSlice("Failed to read from file: %s"), argz);
                exit(2);
            }
            ByteSlice buf = buffer.extractData().copy();
            BytePtr dest = pcopy(FileName.forceExt(FileName.name(toBytePtr(argz)), suffix));
            ByteSlice filePath = toByteSlice((outdir.concat( new ByteSlice("/")))).concat(dest.slice(0,strlen(dest))).copy();
            ByteSlice output = lispy(toBytePtr(argz), toByteSlice(buf)).copy();
            if (!(File.write(toStringz(filePath), toByteSlice(output))))
                fprintf(stderr,  new ByteSlice("Failed to write file: %s\n"), toStringz(filePath));
        }
        finally {
        }
    }


    // from template processFile!(_lex)
    public static void processFile_lex(ByteSlice arg, ByteSlice outdir, BytePtr suffix) {
        BytePtr argz = pcopy(toStringz(arg));
        File.ReadResult buffer = File.read(toBytePtr(argz)).copy();
        try {
            if (!(buffer.success))
            {
                fprintf(stderr,  new ByteSlice("Failed to read from file: %s"), argz);
                exit(2);
            }
            ByteSlice buf = buffer.extractData().copy();
            BytePtr dest = pcopy(FileName.forceExt(FileName.name(toBytePtr(argz)), suffix));
            ByteSlice filePath = toByteSlice((outdir.concat( new ByteSlice("/")))).concat(dest.slice(0,strlen(dest))).copy();
            ByteSlice output = lex(toBytePtr(argz), toByteSlice(buf)).copy();
            if (!(File.write(toStringz(filePath), toByteSlice(output))))
                fprintf(stderr,  new ByteSlice("Failed to write file: %s\n"), toStringz(filePath));
        }
        finally {
        }
    }


}
