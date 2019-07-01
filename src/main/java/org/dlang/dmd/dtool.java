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
            this.open(attr.toChars());
            super.visit(attr);
            this.close();
        }

        public  void visit(ASTBase.StaticAssert as) {
            this.open(new BytePtr("static assert"));
            super.visit(as);
            this.close();
        }

        public  void visit(ASTBase.DebugSymbol sym) {
            this.open(new BytePtr("debug"));
            super.visit(sym);
            this.close();
        }

        public  void visit(ASTBase.VersionSymbol ver) {
            this.open(new BytePtr("version"));
            super.visit(ver);
            this.close();
        }

        public  void visit(ASTBase.VarDeclaration d) {
            (this.buf).printf( new ByteSlice("( var %s "), d.ident.toChars());
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
            super.visit(d);
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
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.UserAttributeDeclaration d) {
            this.open(new BytePtr("udas"));
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.LinkDeclaration link) {
            this.open(new BytePtr("%s"), link.ident.toChars());
            super.visit(link);
            this.close();
        }

        public  void visit(ASTBase.AnonDeclaration anon) {
            this.open(new BytePtr("anon union %s"), anon.ident.toChars());
            super.visit(anon);
            this.close();
        }

        public  void visit(ASTBase.AlignDeclaration d) {
            this.open(new BytePtr("align "));
            super.visit(d.ealign);
            (this.buf).printf( new ByteSlice(" "));
            if (d.decl != null)
            {
                {
                    Slice<ASTBase.Dsymbol> __r200 = (d.decl).opSlice().copy();
                    int __key201 = 0;
                    for (; __key201 < __r200.getLength();__key201 += 1) {
                        ASTBase.Dsymbol di = __r200.get(__key201);
                        super.visit(di);
                    }
                }
            }
            this.close();
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

        public  void visit(ASTBase.StorageClassDeclaration d) {
            (this.buf).printf( new ByteSlice("( "));
            ASTBase.stcToBuffer(this.buf, d.stc);
            (this.buf).level++;
            (this.buf).writenl();
            if (d.decl != null)
            {
                {
                    Slice<ASTBase.Dsymbol> __r202 = (d.decl).opSlice().copy();
                    int __key203 = 0;
                    for (; __key203 < __r202.getLength();__key203 += 1) {
                        ASTBase.Dsymbol di = __r202.get(__key203);
                        di.accept(this);
                    }
                }
            }
            this.close();
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

        public  void visit(ASTBase.StructDeclaration d) {
            this.open(new BytePtr("struct %s"), d.ident.toChars());
            if (d.members != null)
            {
                {
                    Slice<ASTBase.Dsymbol> __r204 = (d.members).opSlice().copy();
                    int __key205 = 0;
                    for (; __key205 < __r204.getLength();__key205 += 1) {
                        ASTBase.Dsymbol m = __r204.get(__key205);
                        m.accept(this);
                    }
                }
            }
            this.close();
        }

        public  void visit(ASTBase.UnionDeclaration d) {
            this.open(new BytePtr("union %s"), d.ident.toChars());
            super.visit(d);
            this.close();
        }

        public  void visit(ASTBase.ClassDeclaration d) {
            this.open(new BytePtr("class %s"), d.ident != null ? d.ident.toChars() : new BytePtr(""));
            if (d.baseclasses != null)
            {
                {
                    Slice<ASTBase.BaseClass> __r207 = (d.baseclasses).opSlice().copy();
                    int __key206 = 0;
                    for (; __key206 < __r207.getLength();__key206 += 1) {
                        ASTBase.BaseClass c = __r207.get(__key206);
                        int i = __key206;
                        if ((i) != 0)
                            printf( new ByteSlice(" "));
                        (c).type.accept(this);
                    }
                }
                (this.buf).writenl();
            }
            if (d.members != null)
            {
                {
                    Slice<ASTBase.Dsymbol> __r208 = (d.members).opSlice().copy();
                    int __key209 = 0;
                    for (; __key209 < __r208.getLength();__key209 += 1) {
                        ASTBase.Dsymbol m = __r208.get(__key209);
                        m.accept(this);
                    }
                }
            }
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
                Slice<ASTBase.Statement> __r211 = (s.statements).opSlice().copy();
                int __key210 = 0;
                for (; __key210 < __r211.getLength();__key210 += 1) {
                    ASTBase.Statement st = __r211.get(__key210);
                    int i = __key210;
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
                    Slice<ASTBase.Parameter> __r213 = (t.arguments).opSlice().copy();
                    int __key212 = 0;
                    for (; __key212 < __r213.getLength();__key212 += 1) {
                        ASTBase.Parameter a = __r213.get(__key212);
                        int i = __key212;
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

        public  void visit(ASTBase.TypeDelegate _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypePointer _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ASTBase.TypeFunction tf) {
            tf.next.accept(this);
            (this.buf).printf( new ByteSlice(" "));
            {
                Slice<ASTBase.Parameter> __r215 = (tf.parameterList.parameters).opSlice().copy();
                int __key214 = 0;
                for (; __key214 < __r215.getLength();__key214 += 1) {
                    ASTBase.Parameter p = __r215.get(__key214);
                    int i = __key214;
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

        public  void visit(ASTBase.IdentifierExp e) {
            (this.buf).printf( new ByteSlice("%s"), e.ident.toChars());
        }

        public  void visit(ASTBase.UnaExp e) {
            (this.buf).printf( new ByteSlice("%s %s"), Token.toChars(e.op));
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

        public  void visit(ASTBase.AssignExp a) {
            (this.buf).printf( new ByteSlice("( = "));
            a.e1.accept(this);
            (this.buf).printf( new ByteSlice(" "));
            a.e2.accept(this);
            (this.buf).printf( new ByteSlice(")"));
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

        public  void visit(ASTBase.ExpInitializer ei) {
            ei.exp.accept(this);
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
        {
            Slice<ByteSlice> __r411 = args_ref.value.slice(1,args_ref.value.getLength()).copy();
            int __key412 = 0;
            for (; __key412 < __r411.getLength();__key412 += 1) {
                ByteSlice arg = __r411.get(__key412).copy();
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
                    Slice<ASTBase.Dsymbol> __r415 = (decls).opSlice().copy();
                    int __key416 = 0;
                    for (; __key416 < __r415.getLength();__key416 += 1) {
                        ASTBase.Dsymbol d = __r415.get(__key416);
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


}
