package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.ast_node.*;
import static org.dlang.dmd.astcodegen.*;
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.canthrow.*;
import static org.dlang.dmd.cond.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.denum.*;
import static org.dlang.dmd.dimport.*;
import static org.dlang.dmd.dinterpret.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.gluelayer.*;
import static org.dlang.dmd.hdrgen.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.parse.*;
import static org.dlang.dmd.sapply.*;
import static org.dlang.dmd.sideeffect.*;
import static org.dlang.dmd.statementsem.*;
import static org.dlang.dmd.staticassert.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.visitor.*;

public class statement {
    private static class UsesEH extends StoppableVisitor
    {
        public  void visit(Statement s) {
        }

        public  void visit(TryCatchStatement s) {
            this.stop = true;
        }

        public  void visit(TryFinallyStatement s) {
            this.stop = true;
        }

        public  void visit(ScopeGuardStatement s) {
            this.stop = true;
        }

        public  void visit(SynchronizedStatement s) {
            this.stop = true;
        }

        public  UsesEH() {
            super();
        }

    }
    private static class ComeFrom extends StoppableVisitor
    {
        public  void visit(Statement s) {
        }

        public  void visit(CaseStatement s) {
            this.stop = true;
        }

        public  void visit(DefaultStatement s) {
            this.stop = true;
        }

        public  void visit(LabelStatement s) {
            this.stop = true;
        }

        public  void visit(AsmStatement s) {
            this.stop = true;
        }

        public  ComeFrom() {
            super();
        }

    }
    private static class HasCode extends StoppableVisitor
    {
        public  void visit(Statement s) {
            this.stop = true;
        }

        public  void visit(ExpStatement s) {
            Ref<ExpStatement> s_ref = ref(s);
            if ((s_ref.value.exp != null))
            {
                this.stop = s_ref.value.exp.hasCode();
            }
        }

        public  void visit(CompoundStatement s) {
        }

        public  void visit(ScopeStatement s) {
        }

        public  void visit(ImportStatement s) {
        }

        public  HasCode() {
            super();
        }

    }
    private static class ToStmt extends Visitor
    {
        private Statement result = null;
        public  Statement visitMembers(Loc loc, Ptr<DArray<Dsymbol>> a) {
            Ref<Ptr<DArray<Dsymbol>>> a_ref = ref(a);
            if (a_ref.value == null)
                return null;
            Ref<Ptr<DArray<Statement>>> statements = ref(new DArray<Statement>());
            {
                Ref<Slice<Dsymbol>> __r1561 = ref((a_ref.value.get()).opSlice().copy());
                IntRef __key1562 = ref(0);
                for (; (__key1562.value < __r1561.value.getLength());__key1562.value += 1) {
                    Ref<Dsymbol> s = ref(__r1561.value.get(__key1562.value));
                    (statements.value.get()).push(toStatement(s.value));
                }
            }
            return new CompoundStatement(loc, statements.value);
        }

        public  void visit(Dsymbol s) {
            Ref<Dsymbol> s_ref = ref(s);
            error(Loc.initial.value, new BytePtr("Internal Compiler Error: cannot mixin %s `%s`\n"), s_ref.value.kind(), s_ref.value.toChars());
            this.result = new ErrorStatement();
        }

        public  void visit(TemplateMixin tm) {
            Ref<TemplateMixin> tm_ref = ref(tm);
            Ref<Ptr<DArray<Statement>>> a = ref(new DArray<Statement>());
            {
                Ref<Slice<Dsymbol>> __r1563 = ref((tm_ref.value.members.get()).opSlice().copy());
                IntRef __key1564 = ref(0);
                for (; (__key1564.value < __r1563.value.getLength());__key1564.value += 1) {
                    Ref<Dsymbol> m = ref(__r1563.value.get(__key1564.value));
                    Ref<Statement> s = ref(toStatement(m.value));
                    if (s.value != null)
                        (a.value.get()).push(s.value);
                }
            }
            this.result = new CompoundStatement(tm_ref.value.loc, a.value);
        }

        public  Statement declStmt(Dsymbol s) {
            Ref<Dsymbol> s_ref = ref(s);
            Ref<DeclarationExp> de = ref(new DeclarationExp(s_ref.value.loc, s_ref.value));
            de.value.type.value = Type.tvoid.value;
            return new ExpStatement(s_ref.value.loc, de.value);
        }

        public  void visit(VarDeclaration d) {
            Ref<VarDeclaration> d_ref = ref(d);
            this.result = this.declStmt(d_ref.value);
        }

        public  void visit(AggregateDeclaration d) {
            Ref<AggregateDeclaration> d_ref = ref(d);
            this.result = this.declStmt(d_ref.value);
        }

        public  void visit(FuncDeclaration d) {
            Ref<FuncDeclaration> d_ref = ref(d);
            this.result = this.declStmt(d_ref.value);
        }

        public  void visit(EnumDeclaration d) {
            Ref<EnumDeclaration> d_ref = ref(d);
            this.result = this.declStmt(d_ref.value);
        }

        public  void visit(AliasDeclaration d) {
            Ref<AliasDeclaration> d_ref = ref(d);
            this.result = this.declStmt(d_ref.value);
        }

        public  void visit(TemplateDeclaration d) {
            Ref<TemplateDeclaration> d_ref = ref(d);
            this.result = this.declStmt(d_ref.value);
        }

        public  void visit(StorageClassDeclaration d) {
            Ref<StorageClassDeclaration> d_ref = ref(d);
            this.result = this.visitMembers(d_ref.value.loc, d_ref.value.decl);
        }

        public  void visit(DeprecatedDeclaration d) {
            Ref<DeprecatedDeclaration> d_ref = ref(d);
            this.result = this.visitMembers(d_ref.value.loc, d_ref.value.decl);
        }

        public  void visit(LinkDeclaration d) {
            Ref<LinkDeclaration> d_ref = ref(d);
            this.result = this.visitMembers(d_ref.value.loc, d_ref.value.decl);
        }

        public  void visit(ProtDeclaration d) {
            Ref<ProtDeclaration> d_ref = ref(d);
            this.result = this.visitMembers(d_ref.value.loc, d_ref.value.decl);
        }

        public  void visit(AlignDeclaration d) {
            Ref<AlignDeclaration> d_ref = ref(d);
            this.result = this.visitMembers(d_ref.value.loc, d_ref.value.decl);
        }

        public  void visit(UserAttributeDeclaration d) {
            Ref<UserAttributeDeclaration> d_ref = ref(d);
            this.result = this.visitMembers(d_ref.value.loc, d_ref.value.decl);
        }

        public  void visit(StaticAssert s) {
        }

        public  void visit(Import s) {
        }

        public  void visit(PragmaDeclaration d) {
        }

        public  void visit(ConditionalDeclaration d) {
            Ref<ConditionalDeclaration> d_ref = ref(d);
            this.result = this.visitMembers(d_ref.value.loc, d_ref.value.include(null));
        }

        public  void visit(StaticForeachDeclaration d) {
            Ref<StaticForeachDeclaration> d_ref = ref(d);
            assert((d_ref.value.sfe != null) && d_ref.value.sfe.aggrfe != null ^ d_ref.value.sfe.rangefe != null);
            (d_ref.value.sfe.aggrfe != null ? ptr(d_ref.value.sfe.aggrfe._body.value) : ptr(d_ref.value.sfe.rangefe._body.value)).set(0, this.visitMembers(d_ref.value.loc, d_ref.value.decl));
            this.result = new StaticForeachStatement(d_ref.value.loc, d_ref.value.sfe);
        }

        public  void visit(CompileDeclaration d) {
            Ref<CompileDeclaration> d_ref = ref(d);
            this.result = this.visitMembers(d_ref.value.loc, d_ref.value.include(null));
        }


        public ToStmt() {}
    }

    public static TypeIdentifier getThrowable() {
        TypeIdentifier tid = new TypeIdentifier(Loc.initial.value, Id.empty.value);
        tid.addIdent(Id.object.value);
        tid.addIdent(Id.Throwable.value);
        return tid;
    }

    public static TypeIdentifier getException() {
        TypeIdentifier tid = new TypeIdentifier(Loc.initial.value, Id.empty.value);
        tid.addIdent(Id.object.value);
        tid.addIdent(Id.Exception.value);
        return tid;
    }

    public static abstract class Statement extends ASTNode
    {
        public Loc loc = new Loc();
        public  int dyncast() {
            return DYNCAST.statement;
        }

        public  Statement(Loc loc) {
            super();
            this.loc = loc.copy();
        }

        public  Statement syntaxCopy() {
            throw new AssertionError("Unreachable code!");
        }

        public static Ptr<DArray<Statement>> arraySyntaxCopy(Ptr<DArray<Statement>> a) {
            Ptr<DArray<Statement>> b = null;
            if (a != null)
            {
                b = (a.get()).copy();
                {
                    Slice<Statement> __r1560 = (a.get()).opSlice().copy();
                    int __key1559 = 0;
                    for (; (__key1559 < __r1560.getLength());__key1559 += 1) {
                        Statement s = __r1560.get(__key1559);
                        int i = __key1559;
                        b.get().set(i, s != null ? s.syntaxCopy() : null);
                    }
                }
            }
            return b;
        }

        public  BytePtr toChars() {
            Ref<HdrGenState> hgs = ref(new HdrGenState());
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                toCBuffer(this, ptr(buf), ptr(hgs));
                return buf.value.extractChars();
            }
            finally {
            }
        }

        public  void error(BytePtr format, Object... ap) {
            verror(this.loc, format, new Slice<>(ap), null, null, new BytePtr("Error: "));
        }

        public  void warning(BytePtr format, Object... ap) {
            vwarning(this.loc, format, new Slice<>(ap));
        }

        public  void deprecation(BytePtr format, Object... ap) {
            vdeprecation(this.loc, format, new Slice<>(ap), null, null);
        }

        public  Statement getRelatedLabeled() {
            return this;
        }

        public  boolean hasBreak() {
            return false;
        }

        public  boolean hasContinue() {
            return false;
        }

        public  boolean usesEH() {
            UsesEH ueh = new UsesEH();
            return walkPostorder(this, ueh);
        }

        public  boolean comeFrom() {
            ComeFrom cf = new ComeFrom();
            return walkPostorder(this, cf);
        }

        public  boolean hasCode() {
            HasCode hc = new HasCode();
            return walkPostorder(this, hc);
        }

        public  Statement scopeCode(Ptr<Scope> sc, Ptr<Statement> sentry, Ptr<Statement> sexception, Ptr<Statement> sfinally) {
            sentry.set(0, null);
            sexception.set(0, null);
            sfinally.set(0, null);
            return this;
        }

        public  Ptr<DArray<Statement>> flatten(Ptr<Scope> sc) {
            return null;
        }

        public  Statement last() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }

        public  ErrorStatement isErrorStatement() {
            return null;
        }

        public  ScopeStatement isScopeStatement() {
            return null;
        }

        public  ExpStatement isExpStatement() {
            return null;
        }

        public  CompoundStatement isCompoundStatement() {
            return null;
        }

        public  ReturnStatement isReturnStatement() {
            return null;
        }

        public  IfStatement isIfStatement() {
            return null;
        }

        public  CaseStatement isCaseStatement() {
            return null;
        }

        public  DefaultStatement isDefaultStatement() {
            return null;
        }

        public  LabelStatement isLabelStatement() {
            return null;
        }

        public  GotoDefaultStatement isGotoDefaultStatement() {
            return null;
        }

        public  GotoCaseStatement isGotoCaseStatement() {
            return null;
        }

        public  BreakStatement isBreakStatement() {
            return null;
        }

        public  DtorExpStatement isDtorExpStatement() {
            return null;
        }

        public  ForwardingStatement isForwardingStatement() {
            return null;
        }


        public Statement() {}

        public abstract Statement copy();
    }
    public static class ErrorStatement extends Statement
    {
        public  ErrorStatement() {
            super(Loc.initial.value);
            assert((global.value.gaggedErrors != 0) || (global.value.errors != 0));
        }

        public  Statement syntaxCopy() {
            return this;
        }

        public  ErrorStatement isErrorStatement() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ErrorStatement copy() {
            ErrorStatement that = new ErrorStatement();
            that.loc = this.loc;
            return that;
        }
    }
    public static class PeelStatement extends Statement
    {
        public Statement s = null;
        public  PeelStatement(Statement s) {
            super(s.loc);
            this.s = s;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public PeelStatement() {}

        public PeelStatement copy() {
            PeelStatement that = new PeelStatement();
            that.s = this.s;
            that.loc = this.loc;
            return that;
        }
    }
    public static Statement toStatement(Dsymbol s) {
        if (s == null)
            return null;
        ToStmt v = new ToStmt();
        s.accept(v);
        return v.result;
    }

    public static class ExpStatement extends Statement
    {
        public Expression exp = null;
        public  ExpStatement(Loc loc, Expression exp) {
            super(loc);
            this.exp = exp;
        }

        public  ExpStatement(Loc loc, Dsymbol declaration) {
            super(loc);
            this.exp = new DeclarationExp(loc, declaration);
        }

        public static ExpStatement create(Loc loc, Expression exp) {
            return new ExpStatement(loc, exp);
        }

        public  Statement syntaxCopy() {
            return new ExpStatement(this.loc, this.exp != null ? this.exp.syntaxCopy() : null);
        }

        public  Statement scopeCode(Ptr<Scope> sc, Ptr<Statement> sentry, Ptr<Statement> sexception, Ptr<Statement> sfinally) {
            sentry.set(0, null);
            sexception.set(0, null);
            sfinally.set(0, null);
            if ((this.exp != null) && ((this.exp.op & 0xFF) == 38))
            {
                DeclarationExp de = (DeclarationExp)this.exp;
                VarDeclaration v = de.declaration.isVarDeclaration();
                if ((v != null) && !v.isDataseg())
                {
                    if (v.needsScopeDtor())
                    {
                        sfinally.set(0, (new DtorExpStatement(this.loc, v.edtor, v)));
                        v.storage_class |= 16777216L;
                    }
                }
            }
            return this;
        }

        public  Ptr<DArray<Statement>> flatten(Ptr<Scope> sc) {
            if ((this.exp != null) && ((this.exp.op & 0xFF) == 38))
            {
                Dsymbol d = ((DeclarationExp)this.exp).declaration;
                {
                    TemplateMixin tm = d.isTemplateMixin();
                    if ((tm) != null)
                    {
                        Expression e = expressionSemantic(this.exp, sc);
                        if (((e.op & 0xFF) == 127) || tm.errors)
                        {
                            Ptr<DArray<Statement>> a = new DArray<Statement>();
                            (a.get()).push(new ErrorStatement());
                            return a;
                        }
                        assert(tm.members != null);
                        Statement s = toStatement(tm);
                        Ptr<DArray<Statement>> a = new DArray<Statement>();
                        (a.get()).push(s);
                        return a;
                    }
                }
            }
            return null;
        }

        public  ExpStatement isExpStatement() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ExpStatement() {}

        public ExpStatement copy() {
            ExpStatement that = new ExpStatement();
            that.exp = this.exp;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DtorExpStatement extends ExpStatement
    {
        public VarDeclaration var = null;
        public  DtorExpStatement(Loc loc, Expression exp, VarDeclaration var) {
            super(loc, exp);
            this.var = var;
        }

        public  Statement syntaxCopy() {
            return new DtorExpStatement(this.loc, this.exp != null ? this.exp.syntaxCopy() : null, this.var);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }

        public  DtorExpStatement isDtorExpStatement() {
            return this;
        }


        public DtorExpStatement() {}

        public DtorExpStatement copy() {
            DtorExpStatement that = new DtorExpStatement();
            that.var = this.var;
            that.exp = this.exp;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CompileStatement extends Statement
    {
        public Ptr<DArray<Expression>> exps = null;
        public  CompileStatement(Loc loc, Expression exp) {
            Ptr<DArray<Expression>> exps = new DArray<Expression>();
            (exps.get()).push(exp);
            this(loc, exps);
        }

        public  CompileStatement(Loc loc, Ptr<DArray<Expression>> exps) {
            super(loc);
            this.exps = exps;
        }

        public  Statement syntaxCopy() {
            return new CompileStatement(this.loc, Expression.arraySyntaxCopy(this.exps));
        }

        public  Ptr<DArray<Statement>> compileIt(Ptr<Scope> sc) {
            Function0<Ptr<DArray<Statement>>> errorStatements = new Function0<Ptr<DArray<Statement>>>(){
                public Ptr<DArray<Statement>> invoke() {
                    Ref<Ptr<DArray<Statement>>> a = ref(new DArray<Statement>());
                    (a.value.get()).push(new ErrorStatement());
                    return a.value;
                }
            };
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                if (expressionsToString(buf, sc, this.exps))
                    return errorStatements.invoke();
                int errors = global.value.errors;
                int len = buf.value.offset;
                ByteSlice str = buf.value.extractChars().slice(0,len).copy();
                StderrDiagnosticReporter diagnosticReporter = new StderrDiagnosticReporter(global.value.params.useDeprecated);
                try {
                    ParserASTCodegen p = new ParserASTCodegen(this.loc, (sc.get())._module, str, false, diagnosticReporter);
                    try {
                        p.nextToken();
                        Ptr<DArray<Statement>> a = new DArray<Statement>();
                        for (; ((p.token.value.value & 0xFF) != 11);){
                            Statement s = p.parseStatement(9, null, null);
                            if ((s == null) || p.errors())
                            {
                                assert(!p.errors() || (global.value.errors != errors));
                                return errorStatements.invoke();
                            }
                            (a.get()).push(s);
                        }
                        return a;
                    }
                    finally {
                    }
                }
                finally {
                }
            }
            finally {
            }
        }

        public  Ptr<DArray<Statement>> flatten(Ptr<Scope> sc) {
            return this.compileIt(sc);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CompileStatement() {}

        public CompileStatement copy() {
            CompileStatement that = new CompileStatement();
            that.exps = this.exps;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CompoundStatement extends Statement
    {
        public Ptr<DArray<Statement>> statements = null;
        public  CompoundStatement(Loc loc, Ptr<DArray<Statement>> statements) {
            super(loc);
            this.statements = statements;
        }

        public  CompoundStatement(Loc loc, Slice<Statement> sts) {
            super(loc);
            this.statements = new DArray<Statement>();
            (this.statements.get()).reserve(sts.getLength());
            {
                Slice<Statement> __r1565 = sts.copy();
                int __key1566 = 0;
                for (; (__key1566 < __r1565.getLength());__key1566 += 1) {
                    Statement s = __r1565.get(__key1566);
                    (this.statements.get()).push(s);
                }
            }
        }

        public static CompoundStatement create(Loc loc, Statement s1, Statement s2) {
            return new CompoundStatement(loc, slice(new Statement[]{s1, s2}));
        }

        public  Statement syntaxCopy() {
            return new CompoundStatement(this.loc, Statement.arraySyntaxCopy(this.statements));
        }

        public  Ptr<DArray<Statement>> flatten(Ptr<Scope> sc) {
            return this.statements;
        }

        public  ReturnStatement isReturnStatement() {
            ReturnStatement rs = null;
            {
                Slice<Statement> __r1567 = (this.statements.get()).opSlice().copy();
                int __key1568 = 0;
                for (; (__key1568 < __r1567.getLength());__key1568 += 1) {
                    Statement s = __r1567.get(__key1568);
                    if (s != null)
                    {
                        rs = s.isReturnStatement();
                        if (rs != null)
                            break;
                    }
                }
            }
            return rs;
        }

        public  Statement last() {
            Statement s = null;
            {
                int i = (this.statements.get()).length;
                for (; i != 0;i -= 1){
                    s = (this.statements.get()).get(i - 1);
                    if (s != null)
                    {
                        s = s.last();
                        if (s != null)
                            break;
                    }
                }
            }
            return s;
        }

        public  CompoundStatement isCompoundStatement() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CompoundStatement() {}

        public CompoundStatement copy() {
            CompoundStatement that = new CompoundStatement();
            that.statements = this.statements;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CompoundDeclarationStatement extends CompoundStatement
    {
        public  CompoundDeclarationStatement(Loc loc, Ptr<DArray<Statement>> statements) {
            super(loc, statements);
        }

        public  Statement syntaxCopy() {
            Ptr<DArray<Statement>> a = new DArray<Statement>((this.statements.get()).length);
            {
                Slice<Statement> __r1570 = (this.statements.get()).opSlice().copy();
                int __key1569 = 0;
                for (; (__key1569 < __r1570.getLength());__key1569 += 1) {
                    Statement s = __r1570.get(__key1569);
                    int i = __key1569;
                    a.get().set(i, s != null ? s.syntaxCopy() : null);
                }
            }
            return new CompoundDeclarationStatement(this.loc, a);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CompoundDeclarationStatement() {}

        public CompoundDeclarationStatement copy() {
            CompoundDeclarationStatement that = new CompoundDeclarationStatement();
            that.statements = this.statements;
            that.loc = this.loc;
            return that;
        }
    }
    public static class UnrolledLoopStatement extends Statement
    {
        public Ptr<DArray<Statement>> statements = null;
        public  UnrolledLoopStatement(Loc loc, Ptr<DArray<Statement>> statements) {
            super(loc);
            this.statements = statements;
        }

        public  Statement syntaxCopy() {
            Ptr<DArray<Statement>> a = new DArray<Statement>((this.statements.get()).length);
            {
                Slice<Statement> __r1572 = (this.statements.get()).opSlice().copy();
                int __key1571 = 0;
                for (; (__key1571 < __r1572.getLength());__key1571 += 1) {
                    Statement s = __r1572.get(__key1571);
                    int i = __key1571;
                    a.get().set(i, s != null ? s.syntaxCopy() : null);
                }
            }
            return new UnrolledLoopStatement(this.loc, a);
        }

        public  boolean hasBreak() {
            return true;
        }

        public  boolean hasContinue() {
            return true;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public UnrolledLoopStatement() {}

        public UnrolledLoopStatement copy() {
            UnrolledLoopStatement that = new UnrolledLoopStatement();
            that.statements = this.statements;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ScopeStatement extends Statement
    {
        public Statement statement = null;
        public Loc endloc = new Loc();
        public  ScopeStatement(Loc loc, Statement statement, Loc endloc) {
            super(loc);
            this.statement = statement;
            this.endloc = endloc.copy();
        }

        public  Statement syntaxCopy() {
            return new ScopeStatement(this.loc, this.statement != null ? this.statement.syntaxCopy() : null, this.endloc);
        }

        public  ScopeStatement isScopeStatement() {
            return this;
        }

        public  ReturnStatement isReturnStatement() {
            if (this.statement != null)
                return this.statement.isReturnStatement();
            return null;
        }

        public  boolean hasBreak() {
            return this.statement != null ? this.statement.hasBreak() : false;
        }

        public  boolean hasContinue() {
            return this.statement != null ? this.statement.hasContinue() : false;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ScopeStatement() {}

        public ScopeStatement copy() {
            ScopeStatement that = new ScopeStatement();
            that.statement = this.statement;
            that.endloc = this.endloc;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ForwardingStatement extends Statement
    {
        public ForwardingScopeDsymbol sym = null;
        public Statement statement = null;
        public  ForwardingStatement(Loc loc, ForwardingScopeDsymbol sym, Statement statement) {
            super(loc);
            this.sym = sym;
            assert(statement != null);
            this.statement = statement;
        }

        public  ForwardingStatement(Loc loc, Statement statement) {
            ForwardingScopeDsymbol sym = new ForwardingScopeDsymbol(null);
            sym.symtab = new DsymbolTable();
            this(loc, sym, statement);
        }

        public  Statement syntaxCopy() {
            return new ForwardingStatement(this.loc, this.statement.syntaxCopy());
        }

        public  Ptr<DArray<Statement>> flatten(Ptr<Scope> sc) {
            if (this.statement == null)
            {
                return null;
            }
            sc = (sc.get()).push(this.sym);
            Ptr<DArray<Statement>> a = this.statement.flatten(sc);
            sc = (sc.get()).pop();
            if (a == null)
            {
                return a;
            }
            Ptr<DArray<Statement>> b = new DArray<Statement>((a.get()).length);
            {
                Slice<Statement> __r1574 = (a.get()).opSlice().copy();
                int __key1573 = 0;
                for (; (__key1573 < __r1574.getLength());__key1573 += 1) {
                    Statement s = __r1574.get(__key1573);
                    int i = __key1573;
                    b.get().set(i, s != null ? new ForwardingStatement(s.loc, this.sym, s) : null);
                }
            }
            return b;
        }

        public  ForwardingStatement isForwardingStatement() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ForwardingStatement() {}

        public ForwardingStatement copy() {
            ForwardingStatement that = new ForwardingStatement();
            that.sym = this.sym;
            that.statement = this.statement;
            that.loc = this.loc;
            return that;
        }
    }
    public static class WhileStatement extends Statement
    {
        public Expression condition = null;
        public Statement _body = null;
        public Loc endloc = new Loc();
        public  WhileStatement(Loc loc, Expression condition, Statement _body, Loc endloc) {
            super(loc);
            this.condition = condition;
            this._body = _body;
            this.endloc = endloc.copy();
        }

        public  Statement syntaxCopy() {
            return new WhileStatement(this.loc, this.condition.syntaxCopy(), this._body != null ? this._body.syntaxCopy() : null, this.endloc);
        }

        public  boolean hasBreak() {
            return true;
        }

        public  boolean hasContinue() {
            return true;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public WhileStatement() {}

        public WhileStatement copy() {
            WhileStatement that = new WhileStatement();
            that.condition = this.condition;
            that._body = this._body;
            that.endloc = this.endloc;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DoStatement extends Statement
    {
        public Statement _body = null;
        public Expression condition = null;
        public Loc endloc = new Loc();
        public  DoStatement(Loc loc, Statement _body, Expression condition, Loc endloc) {
            super(loc);
            this._body = _body;
            this.condition = condition;
            this.endloc = endloc.copy();
        }

        public  Statement syntaxCopy() {
            return new DoStatement(this.loc, this._body != null ? this._body.syntaxCopy() : null, this.condition.syntaxCopy(), this.endloc);
        }

        public  boolean hasBreak() {
            return true;
        }

        public  boolean hasContinue() {
            return true;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DoStatement() {}

        public DoStatement copy() {
            DoStatement that = new DoStatement();
            that._body = this._body;
            that.condition = this.condition;
            that.endloc = this.endloc;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ForStatement extends Statement
    {
        public Statement _init = null;
        public Expression condition = null;
        public Expression increment = null;
        public Statement _body = null;
        public Loc endloc = new Loc();
        public Statement relatedLabeled = null;
        public  ForStatement(Loc loc, Statement _init, Expression condition, Expression increment, Statement _body, Loc endloc) {
            super(loc);
            this._init = _init;
            this.condition = condition;
            this.increment = increment;
            this._body = _body;
            this.endloc = endloc.copy();
        }

        public  Statement syntaxCopy() {
            return new ForStatement(this.loc, this._init != null ? this._init.syntaxCopy() : null, this.condition != null ? this.condition.syntaxCopy() : null, this.increment != null ? this.increment.syntaxCopy() : null, this._body.syntaxCopy(), this.endloc);
        }

        public  Statement scopeCode(Ptr<Scope> sc, Ptr<Statement> sentry, Ptr<Statement> sexception, Ptr<Statement> sfinally) {
            this.scopeCode(sc, sentry, sexception, sfinally);
            return this;
        }

        public  Statement getRelatedLabeled() {
            return this.relatedLabeled != null ? this.relatedLabeled : this;
        }

        public  boolean hasBreak() {
            return true;
        }

        public  boolean hasContinue() {
            return true;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ForStatement() {}

        public ForStatement copy() {
            ForStatement that = new ForStatement();
            that._init = this._init;
            that.condition = this.condition;
            that.increment = this.increment;
            that._body = this._body;
            that.endloc = this.endloc;
            that.relatedLabeled = this.relatedLabeled;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ForeachStatement extends Statement
    {
        public byte op = 0;
        public Ptr<DArray<Parameter>> parameters = null;
        public Expression aggr = null;
        public Ref<Statement> _body = ref(null);
        public Loc endloc = new Loc();
        public VarDeclaration key = null;
        public VarDeclaration value = null;
        public FuncDeclaration func = null;
        public Ptr<DArray<Statement>> cases = null;
        public Ptr<DArray<ScopeStatement>> gotos = null;
        public  ForeachStatement(Loc loc, byte op, Ptr<DArray<Parameter>> parameters, Expression aggr, Statement _body, Loc endloc) {
            super(loc);
            this.op = op;
            this.parameters = parameters;
            this.aggr = aggr;
            this._body.value = _body;
            this.endloc = endloc.copy();
        }

        public  Statement syntaxCopy() {
            return new ForeachStatement(this.loc, this.op, Parameter.arraySyntaxCopy(this.parameters), this.aggr.syntaxCopy(), this._body.value != null ? this._body.value.syntaxCopy() : null, this.endloc);
        }

        public  boolean hasBreak() {
            return true;
        }

        public  boolean hasContinue() {
            return true;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ForeachStatement() {}

        public ForeachStatement copy() {
            ForeachStatement that = new ForeachStatement();
            that.op = this.op;
            that.parameters = this.parameters;
            that.aggr = this.aggr;
            that._body = this._body;
            that.endloc = this.endloc;
            that.key = this.key;
            that.value = this.value;
            that.func = this.func;
            that.cases = this.cases;
            that.gotos = this.gotos;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ForeachRangeStatement extends Statement
    {
        public byte op = 0;
        public Parameter prm = null;
        public Expression lwr = null;
        public Expression upr = null;
        public Ref<Statement> _body = ref(null);
        public Loc endloc = new Loc();
        public VarDeclaration key = null;
        public  ForeachRangeStatement(Loc loc, byte op, Parameter prm, Expression lwr, Expression upr, Statement _body, Loc endloc) {
            super(loc);
            this.op = op;
            this.prm = prm;
            this.lwr = lwr;
            this.upr = upr;
            this._body.value = _body;
            this.endloc = endloc.copy();
        }

        public  Statement syntaxCopy() {
            return new ForeachRangeStatement(this.loc, this.op, this.prm.syntaxCopy(), this.lwr.syntaxCopy(), this.upr.syntaxCopy(), this._body.value != null ? this._body.value.syntaxCopy() : null, this.endloc);
        }

        public  boolean hasBreak() {
            return true;
        }

        public  boolean hasContinue() {
            return true;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ForeachRangeStatement() {}

        public ForeachRangeStatement copy() {
            ForeachRangeStatement that = new ForeachRangeStatement();
            that.op = this.op;
            that.prm = this.prm;
            that.lwr = this.lwr;
            that.upr = this.upr;
            that._body = this._body;
            that.endloc = this.endloc;
            that.key = this.key;
            that.loc = this.loc;
            return that;
        }
    }
    public static class IfStatement extends Statement
    {
        public Parameter prm = null;
        public Expression condition = null;
        public Statement ifbody = null;
        public Statement elsebody = null;
        public VarDeclaration match = null;
        public Loc endloc = new Loc();
        public  IfStatement(Loc loc, Parameter prm, Expression condition, Statement ifbody, Statement elsebody, Loc endloc) {
            super(loc);
            this.prm = prm;
            this.condition = condition;
            this.ifbody = ifbody;
            this.elsebody = elsebody;
            this.endloc = endloc.copy();
        }

        public  Statement syntaxCopy() {
            return new IfStatement(this.loc, this.prm != null ? this.prm.syntaxCopy() : null, this.condition.syntaxCopy(), this.ifbody != null ? this.ifbody.syntaxCopy() : null, this.elsebody != null ? this.elsebody.syntaxCopy() : null, this.endloc);
        }

        public  IfStatement isIfStatement() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public IfStatement() {}

        public IfStatement copy() {
            IfStatement that = new IfStatement();
            that.prm = this.prm;
            that.condition = this.condition;
            that.ifbody = this.ifbody;
            that.elsebody = this.elsebody;
            that.match = this.match;
            that.endloc = this.endloc;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ConditionalStatement extends Statement
    {
        public Condition condition = null;
        public Statement ifbody = null;
        public Statement elsebody = null;
        public  ConditionalStatement(Loc loc, Condition condition, Statement ifbody, Statement elsebody) {
            super(loc);
            this.condition = condition;
            this.ifbody = ifbody;
            this.elsebody = elsebody;
        }

        public  Statement syntaxCopy() {
            return new ConditionalStatement(this.loc, this.condition.syntaxCopy(), this.ifbody.syntaxCopy(), this.elsebody != null ? this.elsebody.syntaxCopy() : null);
        }

        public  Ptr<DArray<Statement>> flatten(Ptr<Scope> sc) {
            Statement s = null;
            if (this.condition.include(sc) != 0)
            {
                DebugCondition dc = this.condition.isDebugCondition();
                if (dc != null)
                    s = new DebugStatement(this.loc, this.ifbody);
                else
                    s = this.ifbody;
            }
            else
                s = this.elsebody;
            Ptr<DArray<Statement>> a = new DArray<Statement>();
            (a.get()).push(s);
            return a;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ConditionalStatement() {}

        public ConditionalStatement copy() {
            ConditionalStatement that = new ConditionalStatement();
            that.condition = this.condition;
            that.ifbody = this.ifbody;
            that.elsebody = this.elsebody;
            that.loc = this.loc;
            return that;
        }
    }
    public static class StaticForeachStatement extends Statement
    {
        public StaticForeach sfe = null;
        public  StaticForeachStatement(Loc loc, StaticForeach sfe) {
            super(loc);
            this.sfe = sfe;
        }

        public  Statement syntaxCopy() {
            return new StaticForeachStatement(this.loc, this.sfe.syntaxCopy());
        }

        public  Ptr<DArray<Statement>> flatten(Ptr<Scope> sc) {
            this.sfe.prepare(sc);
            if (this.sfe.ready())
            {
                Statement s = makeTupleForeach10(sc, this.sfe.aggrfe, this.sfe.needExpansion);
                Ptr<DArray<Statement>> result = s.flatten(sc);
                if (result != null)
                {
                    return result;
                }
                result = new DArray<Statement>();
                (result.get()).push(s);
                return result;
            }
            else
            {
                Ptr<DArray<Statement>> result = new DArray<Statement>();
                (result.get()).push(new ErrorStatement());
                return result;
            }
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public StaticForeachStatement() {}

        public StaticForeachStatement copy() {
            StaticForeachStatement that = new StaticForeachStatement();
            that.sfe = this.sfe;
            that.loc = this.loc;
            return that;
        }
    }
    public static class PragmaStatement extends Statement
    {
        public Identifier ident = null;
        public Ptr<DArray<Expression>> args = null;
        public Statement _body = null;
        public  PragmaStatement(Loc loc, Identifier ident, Ptr<DArray<Expression>> args, Statement _body) {
            super(loc);
            this.ident = ident;
            this.args = args;
            this._body = _body;
        }

        public  Statement syntaxCopy() {
            return new PragmaStatement(this.loc, this.ident, Expression.arraySyntaxCopy(this.args), this._body != null ? this._body.syntaxCopy() : null);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public PragmaStatement() {}

        public PragmaStatement copy() {
            PragmaStatement that = new PragmaStatement();
            that.ident = this.ident;
            that.args = this.args;
            that._body = this._body;
            that.loc = this.loc;
            return that;
        }
    }
    public static class StaticAssertStatement extends Statement
    {
        public StaticAssert sa = null;
        public  StaticAssertStatement(StaticAssert sa) {
            super(sa.loc);
            this.sa = sa;
        }

        public  Statement syntaxCopy() {
            return new StaticAssertStatement((StaticAssert)this.sa.syntaxCopy(null));
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public StaticAssertStatement() {}

        public StaticAssertStatement copy() {
            StaticAssertStatement that = new StaticAssertStatement();
            that.sa = this.sa;
            that.loc = this.loc;
            return that;
        }
    }
    public static class SwitchStatement extends Statement
    {
        public Expression condition = null;
        public Statement _body = null;
        public boolean isFinal = false;
        public DefaultStatement sdefault = null;
        public TryFinallyStatement tf = null;
        public DArray<GotoCaseStatement> gotoCases = new DArray<GotoCaseStatement>();
        public Ptr<DArray<CaseStatement>> cases = null;
        public int hasNoDefault = 0;
        public int hasVars = 0;
        public VarDeclaration lastVar = null;
        public  SwitchStatement(Loc loc, Expression condition, Statement _body, boolean isFinal) {
            super(loc);
            this.condition = condition;
            this._body = _body;
            this.isFinal = isFinal;
        }

        public  Statement syntaxCopy() {
            return new SwitchStatement(this.loc, this.condition.syntaxCopy(), this._body.syntaxCopy(), this.isFinal);
        }

        public  boolean hasBreak() {
            return true;
        }

        public  boolean checkLabel() {
            Function1<VarDeclaration,Boolean> checkVar = new Function1<VarDeclaration,Boolean>(){
                public Boolean invoke(VarDeclaration vd) {
                    Ref<VarDeclaration> vd_ref = ref(vd);
                    {
                        Ref<VarDeclaration> v = ref(vd_ref.value);
                        for (; (v.value != null) && (!pequals(v.value, lastVar));v.value = v.value.lastVar){
                            if (v.value.isDataseg() || ((v.value.storage_class & 1099520016384L) != 0) || (v.value._init.isVoidInitializer() != null))
                                continue;
                            if ((pequals(vd_ref.value.ident, Id.withSym.value)))
                                error(new BytePtr("`switch` skips declaration of `with` temporary at %s"), v.value.loc.toChars(global.value.params.showColumns));
                            else
                                error(new BytePtr("`switch` skips declaration of variable `%s` at %s"), v.value.toPrettyChars(false), v.value.loc.toChars(global.value.params.showColumns));
                            return true;
                        }
                    }
                    return false;
                }
            };
            boolean error = true;
            if ((this.sdefault != null) && checkVar.invoke(this.sdefault.lastVar))
                return false;
            {
                Slice<CaseStatement> __r1579 = (this.cases.get()).opSlice().copy();
                int __key1580 = 0;
                for (; (__key1580 < __r1579.getLength());__key1580 += 1) {
                    CaseStatement scase = __r1579.get(__key1580);
                    if ((scase != null) && checkVar.invoke(scase.lastVar))
                        return false;
                }
            }
            return false;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public SwitchStatement() {}

        public SwitchStatement copy() {
            SwitchStatement that = new SwitchStatement();
            that.condition = this.condition;
            that._body = this._body;
            that.isFinal = this.isFinal;
            that.sdefault = this.sdefault;
            that.tf = this.tf;
            that.gotoCases = this.gotoCases;
            that.cases = this.cases;
            that.hasNoDefault = this.hasNoDefault;
            that.hasVars = this.hasVars;
            that.lastVar = this.lastVar;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CaseStatement extends Statement
    {
        public Expression exp = null;
        public Statement statement = null;
        public int index = 0;
        public VarDeclaration lastVar = null;
        public  CaseStatement(Loc loc, Expression exp, Statement statement) {
            super(loc);
            this.exp = exp;
            this.statement = statement;
        }

        public  Statement syntaxCopy() {
            return new CaseStatement(this.loc, this.exp.syntaxCopy(), this.statement.syntaxCopy());
        }

        public  CaseStatement isCaseStatement() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CaseStatement() {}

        public CaseStatement copy() {
            CaseStatement that = new CaseStatement();
            that.exp = this.exp;
            that.statement = this.statement;
            that.index = this.index;
            that.lastVar = this.lastVar;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CaseRangeStatement extends Statement
    {
        public Expression first = null;
        public Expression last = null;
        public Statement statement = null;
        public  CaseRangeStatement(Loc loc, Expression first, Expression last, Statement statement) {
            super(loc);
            this.first = first;
            this.last = last;
            this.statement = statement;
        }

        public  Statement syntaxCopy() {
            return new CaseRangeStatement(this.loc, this.first.syntaxCopy(), this.last.syntaxCopy(), this.statement.syntaxCopy());
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CaseRangeStatement() {}

        public CaseRangeStatement copy() {
            CaseRangeStatement that = new CaseRangeStatement();
            that.first = this.first;
            that.last = this.last;
            that.statement = this.statement;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DefaultStatement extends Statement
    {
        public Statement statement = null;
        public VarDeclaration lastVar = null;
        public  DefaultStatement(Loc loc, Statement statement) {
            super(loc);
            this.statement = statement;
        }

        public  Statement syntaxCopy() {
            return new DefaultStatement(this.loc, this.statement.syntaxCopy());
        }

        public  DefaultStatement isDefaultStatement() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DefaultStatement() {}

        public DefaultStatement copy() {
            DefaultStatement that = new DefaultStatement();
            that.statement = this.statement;
            that.lastVar = this.lastVar;
            that.loc = this.loc;
            return that;
        }
    }
    public static class GotoDefaultStatement extends Statement
    {
        public SwitchStatement sw = null;
        public  GotoDefaultStatement(Loc loc) {
            super(loc);
        }

        public  Statement syntaxCopy() {
            return new GotoDefaultStatement(this.loc);
        }

        public  GotoDefaultStatement isGotoDefaultStatement() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public GotoDefaultStatement() {}

        public GotoDefaultStatement copy() {
            GotoDefaultStatement that = new GotoDefaultStatement();
            that.sw = this.sw;
            that.loc = this.loc;
            return that;
        }
    }
    public static class GotoCaseStatement extends Statement
    {
        public Expression exp = null;
        public CaseStatement cs = null;
        public  GotoCaseStatement(Loc loc, Expression exp) {
            super(loc);
            this.exp = exp;
        }

        public  Statement syntaxCopy() {
            return new GotoCaseStatement(this.loc, this.exp != null ? this.exp.syntaxCopy() : null);
        }

        public  GotoCaseStatement isGotoCaseStatement() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public GotoCaseStatement() {}

        public GotoCaseStatement copy() {
            GotoCaseStatement that = new GotoCaseStatement();
            that.exp = this.exp;
            that.cs = this.cs;
            that.loc = this.loc;
            return that;
        }
    }
    public static class SwitchErrorStatement extends Statement
    {
        public Expression exp = null;
        public  SwitchErrorStatement(Loc loc) {
            super(loc);
        }

        public  SwitchErrorStatement(Loc loc, Expression exp) {
            super(loc);
            this.exp = exp;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public SwitchErrorStatement() {}

        public SwitchErrorStatement copy() {
            SwitchErrorStatement that = new SwitchErrorStatement();
            that.exp = this.exp;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ReturnStatement extends Statement
    {
        public Expression exp = null;
        public int caseDim = 0;
        public  ReturnStatement(Loc loc, Expression exp) {
            super(loc);
            this.exp = exp;
        }

        public  Statement syntaxCopy() {
            return new ReturnStatement(this.loc, this.exp != null ? this.exp.syntaxCopy() : null);
        }

        public  ReturnStatement isReturnStatement() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ReturnStatement() {}

        public ReturnStatement copy() {
            ReturnStatement that = new ReturnStatement();
            that.exp = this.exp;
            that.caseDim = this.caseDim;
            that.loc = this.loc;
            return that;
        }
    }
    public static class BreakStatement extends Statement
    {
        public Identifier ident = null;
        public  BreakStatement(Loc loc, Identifier ident) {
            super(loc);
            this.ident = ident;
        }

        public  Statement syntaxCopy() {
            return new BreakStatement(this.loc, this.ident);
        }

        public  BreakStatement isBreakStatement() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public BreakStatement() {}

        public BreakStatement copy() {
            BreakStatement that = new BreakStatement();
            that.ident = this.ident;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ContinueStatement extends Statement
    {
        public Identifier ident = null;
        public  ContinueStatement(Loc loc, Identifier ident) {
            super(loc);
            this.ident = ident;
        }

        public  Statement syntaxCopy() {
            return new ContinueStatement(this.loc, this.ident);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ContinueStatement() {}

        public ContinueStatement copy() {
            ContinueStatement that = new ContinueStatement();
            that.ident = this.ident;
            that.loc = this.loc;
            return that;
        }
    }
    public static class SynchronizedStatement extends Statement
    {
        public Expression exp = null;
        public Statement _body = null;
        public  SynchronizedStatement(Loc loc, Expression exp, Statement _body) {
            super(loc);
            this.exp = exp;
            this._body = _body;
        }

        public  Statement syntaxCopy() {
            return new SynchronizedStatement(this.loc, this.exp != null ? this.exp.syntaxCopy() : null, this._body != null ? this._body.syntaxCopy() : null);
        }

        public  boolean hasBreak() {
            return false;
        }

        public  boolean hasContinue() {
            return false;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public SynchronizedStatement() {}

        public SynchronizedStatement copy() {
            SynchronizedStatement that = new SynchronizedStatement();
            that.exp = this.exp;
            that._body = this._body;
            that.loc = this.loc;
            return that;
        }
    }
    public static class WithStatement extends Statement
    {
        public Expression exp = null;
        public Statement _body = null;
        public VarDeclaration wthis = null;
        public Loc endloc = new Loc();
        public  WithStatement(Loc loc, Expression exp, Statement _body, Loc endloc) {
            super(loc);
            this.exp = exp;
            this._body = _body;
            this.endloc = endloc.copy();
        }

        public  Statement syntaxCopy() {
            return new WithStatement(this.loc, this.exp.syntaxCopy(), this._body != null ? this._body.syntaxCopy() : null, this.endloc);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public WithStatement() {}

        public WithStatement copy() {
            WithStatement that = new WithStatement();
            that.exp = this.exp;
            that._body = this._body;
            that.wthis = this.wthis;
            that.endloc = this.endloc;
            that.loc = this.loc;
            return that;
        }
    }
    public static class TryCatchStatement extends Statement
    {
        public Statement _body = null;
        public Ptr<DArray<Catch>> catches = null;
        public  TryCatchStatement(Loc loc, Statement _body, Ptr<DArray<Catch>> catches) {
            super(loc);
            this._body = _body;
            this.catches = catches;
        }

        public  Statement syntaxCopy() {
            Ptr<DArray<Catch>> a = new DArray<Catch>((this.catches.get()).length);
            {
                Slice<Catch> __r1582 = (this.catches.get()).opSlice().copy();
                int __key1581 = 0;
                for (; (__key1581 < __r1582.getLength());__key1581 += 1) {
                    Catch c = __r1582.get(__key1581);
                    int i = __key1581;
                    a.get().set(i, c.syntaxCopy());
                }
            }
            return new TryCatchStatement(this.loc, this._body.syntaxCopy(), a);
        }

        public  boolean hasBreak() {
            return false;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TryCatchStatement() {}

        public TryCatchStatement copy() {
            TryCatchStatement that = new TryCatchStatement();
            that._body = this._body;
            that.catches = this.catches;
            that.loc = this.loc;
            return that;
        }
    }
    public static class Catch extends RootObject
    {
        public Loc loc = new Loc();
        public Type type = null;
        public Identifier ident = null;
        public Statement handler = null;
        public VarDeclaration var = null;
        public boolean errors = false;
        public boolean internalCatch = false;
        public  Catch(Loc loc, Type type, Identifier ident, Statement handler) {
            super();
            this.loc = loc.copy();
            this.type = type;
            this.ident = ident;
            this.handler = handler;
        }

        public  Catch syntaxCopy() {
            Catch c = new Catch(this.loc, this.type != null ? this.type.syntaxCopy() : getThrowable(), this.ident, this.handler != null ? this.handler.syntaxCopy() : null);
            c.internalCatch = this.internalCatch;
            return c;
        }


        public Catch() {}

        public Catch copy() {
            Catch that = new Catch();
            that.loc = this.loc;
            that.type = this.type;
            that.ident = this.ident;
            that.handler = this.handler;
            that.var = this.var;
            that.errors = this.errors;
            that.internalCatch = this.internalCatch;
            return that;
        }
    }
    public static class TryFinallyStatement extends Statement
    {
        public Statement _body = null;
        public Statement finalbody = null;
        public boolean bodyFallsThru = false;
        public  TryFinallyStatement(Loc loc, Statement _body, Statement finalbody) {
            super(loc);
            this._body = _body;
            this.finalbody = finalbody;
            this.bodyFallsThru = true;
        }

        public static TryFinallyStatement create(Loc loc, Statement _body, Statement finalbody) {
            return new TryFinallyStatement(loc, _body, finalbody);
        }

        public  Statement syntaxCopy() {
            return new TryFinallyStatement(this.loc, this._body.syntaxCopy(), this.finalbody.syntaxCopy());
        }

        public  boolean hasBreak() {
            return false;
        }

        public  boolean hasContinue() {
            return false;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TryFinallyStatement() {}

        public TryFinallyStatement copy() {
            TryFinallyStatement that = new TryFinallyStatement();
            that._body = this._body;
            that.finalbody = this.finalbody;
            that.bodyFallsThru = this.bodyFallsThru;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ScopeGuardStatement extends Statement
    {
        public byte tok = 0;
        public Statement statement = null;
        public  ScopeGuardStatement(Loc loc, byte tok, Statement statement) {
            super(loc);
            this.tok = tok;
            this.statement = statement;
        }

        public  Statement syntaxCopy() {
            return new ScopeGuardStatement(this.loc, this.tok, this.statement.syntaxCopy());
        }

        public  Statement scopeCode(Ptr<Scope> sc, Ptr<Statement> sentry, Ptr<Statement> sexception, Ptr<Statement> sfinally) {
            sentry.set(0, null);
            sexception.set(0, null);
            sfinally.set(0, null);
            Statement s = new PeelStatement(this.statement);
            switch ((this.tok & 0xFF))
            {
                case 204:
                    sfinally.set(0, s);
                    break;
                case 205:
                    sexception.set(0, s);
                    break;
                case 206:
                    VarDeclaration v = copyToTemp(0L, new BytePtr("__os"), new IntegerExp(Loc.initial.value, 0L, Type.tbool.value));
                    dsymbolSemantic(v, sc);
                    sentry.set(0, (new ExpStatement(this.loc, v)));
                    Expression e = new IntegerExp(Loc.initial.value, 1L, Type.tbool.value);
                    e = new AssignExp(Loc.initial.value, new VarExp(Loc.initial.value, v, true), e);
                    sexception.set(0, (new ExpStatement(Loc.initial.value, e)));
                    e = new VarExp(Loc.initial.value, v, true);
                    e = new NotExp(Loc.initial.value, e);
                    sfinally.set(0, (new IfStatement(Loc.initial.value, null, e, s, null, Loc.initial.value)));
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            return null;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ScopeGuardStatement() {}

        public ScopeGuardStatement copy() {
            ScopeGuardStatement that = new ScopeGuardStatement();
            that.tok = this.tok;
            that.statement = this.statement;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ThrowStatement extends Statement
    {
        public Expression exp = null;
        public boolean internalThrow = false;
        public  ThrowStatement(Loc loc, Expression exp) {
            super(loc);
            this.exp = exp;
        }

        public  Statement syntaxCopy() {
            ThrowStatement s = new ThrowStatement(this.loc, this.exp.syntaxCopy());
            s.internalThrow = this.internalThrow;
            return s;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ThrowStatement() {}

        public ThrowStatement copy() {
            ThrowStatement that = new ThrowStatement();
            that.exp = this.exp;
            that.internalThrow = this.internalThrow;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DebugStatement extends Statement
    {
        public Statement statement = null;
        public  DebugStatement(Loc loc, Statement statement) {
            super(loc);
            this.statement = statement;
        }

        public  Statement syntaxCopy() {
            return new DebugStatement(this.loc, this.statement != null ? this.statement.syntaxCopy() : null);
        }

        public  Ptr<DArray<Statement>> flatten(Ptr<Scope> sc) {
            Ptr<DArray<Statement>> a = this.statement != null ? this.statement.flatten(sc) : null;
            if (a != null)
            {
                {
                    Slice<Statement> __r1583 = (a.get()).opSlice().copy();
                    int __key1584 = 0;
                    for (; (__key1584 < __r1583.getLength());__key1584 += 1) {
                        Statement s = __r1583.get(__key1584);
                        s = new DebugStatement(this.loc, s);
                    }
                }
            }
            return a;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DebugStatement() {}

        public DebugStatement copy() {
            DebugStatement that = new DebugStatement();
            that.statement = this.statement;
            that.loc = this.loc;
            return that;
        }
    }
    public static class GotoStatement extends Statement
    {
        public Identifier ident = null;
        public LabelDsymbol label = null;
        public TryFinallyStatement tf = null;
        public ScopeGuardStatement os = null;
        public VarDeclaration lastVar = null;
        public  GotoStatement(Loc loc, Identifier ident) {
            super(loc);
            this.ident = ident;
        }

        public  Statement syntaxCopy() {
            return new GotoStatement(this.loc, this.ident);
        }

        public  boolean checkLabel() {
            if (this.label.statement == null)
            {
                this.error(new BytePtr("label `%s` is undefined"), this.label.toChars());
                return true;
            }
            if ((!pequals(this.label.statement.os, this.os)))
            {
                if ((this.os != null) && ((this.os.tok & 0xFF) == 205) && (this.label.statement.os == null))
                {
                }
                else
                {
                    if (this.label.statement.os != null)
                        this.error(new BytePtr("cannot `goto` in to `%s` block"), Token.toChars(this.label.statement.os.tok));
                    else
                        this.error(new BytePtr("cannot `goto` out of `%s` block"), Token.toChars(this.os.tok));
                    return true;
                }
            }
            if ((!pequals(this.label.statement.tf, this.tf)))
            {
                this.error(new BytePtr("cannot `goto` in or out of `finally` block"));
                return true;
            }
            VarDeclaration vd = this.label.statement.lastVar;
            if ((vd == null) || vd.isDataseg() || ((vd.storage_class & 8388608L) != 0))
                return false;
            VarDeclaration last = this.lastVar;
            for (; (last != null) && (!pequals(last, vd));) {
                last = last.lastVar;
            }
            if ((pequals(last, vd)))
            {
            }
            else if ((vd.storage_class & 140737488355328L) != 0)
            {
            }
            else if ((pequals(vd.ident, Id.withSym.value)))
            {
                this.error(new BytePtr("`goto` skips declaration of `with` temporary at %s"), vd.loc.toChars(global.value.params.showColumns));
                return true;
            }
            else
            {
                this.error(new BytePtr("`goto` skips declaration of variable `%s` at %s"), vd.toPrettyChars(false), vd.loc.toChars(global.value.params.showColumns));
                return true;
            }
            return false;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public GotoStatement() {}

        public GotoStatement copy() {
            GotoStatement that = new GotoStatement();
            that.ident = this.ident;
            that.label = this.label;
            that.tf = this.tf;
            that.os = this.os;
            that.lastVar = this.lastVar;
            that.loc = this.loc;
            return that;
        }
    }
    public static class LabelStatement extends Statement
    {
        public Identifier ident = null;
        public Statement statement = null;
        public TryFinallyStatement tf = null;
        public ScopeGuardStatement os = null;
        public VarDeclaration lastVar = null;
        public Statement gotoTarget = null;
        public boolean breaks = false;
        public  LabelStatement(Loc loc, Identifier ident, Statement statement) {
            super(loc);
            this.ident = ident;
            this.statement = statement;
        }

        public  Statement syntaxCopy() {
            return new LabelStatement(this.loc, this.ident, this.statement != null ? this.statement.syntaxCopy() : null);
        }

        public  Ptr<DArray<Statement>> flatten(Ptr<Scope> sc) {
            Ptr<DArray<Statement>> a = null;
            if (this.statement != null)
            {
                a = this.statement.flatten(sc);
                if (a != null)
                {
                    if ((a.get()).length == 0)
                    {
                        (a.get()).push(new ExpStatement(this.loc, null));
                    }
                    this.statement = (a.get()).get(0);
                    a.get().set(0, this);
                }
            }
            return a;
        }

        public  Statement scopeCode(Ptr<Scope> sc, Ptr<Statement> sentry, Ptr<Statement> sexit, Ptr<Statement> sfinally) {
            if (this.statement != null)
                this.statement = this.statement.scopeCode(sc, sentry, sexit, sfinally);
            else
            {
                sentry.set(0, null);
                sexit.set(0, null);
                sfinally.set(0, null);
            }
            return this;
        }

        public  LabelStatement isLabelStatement() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public LabelStatement() {}

        public LabelStatement copy() {
            LabelStatement that = new LabelStatement();
            that.ident = this.ident;
            that.statement = this.statement;
            that.tf = this.tf;
            that.os = this.os;
            that.lastVar = this.lastVar;
            that.gotoTarget = this.gotoTarget;
            that.breaks = this.breaks;
            that.loc = this.loc;
            return that;
        }
    }
    public static class LabelDsymbol extends Dsymbol
    {
        public LabelStatement statement = null;
        public  LabelDsymbol(Identifier ident) {
            super(ident);
        }

        public static LabelDsymbol create(Identifier ident) {
            return new LabelDsymbol(ident);
        }

        public  LabelDsymbol isLabel() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public LabelDsymbol() {}

        public LabelDsymbol copy() {
            LabelDsymbol that = new LabelDsymbol();
            that.statement = this.statement;
            that.ident = this.ident;
            that.parent = this.parent;
            that.namespace = this.namespace;
            that.csym = this.csym;
            that.isym = this.isym;
            that.comment = this.comment;
            that.loc = this.loc;
            that._scope = this._scope;
            that.prettystring = this.prettystring;
            that.errors = this.errors;
            that.semanticRun = this.semanticRun;
            that.depdecl = this.depdecl;
            that.userAttribDecl = this.userAttribDecl;
            that.ddocUnittest = this.ddocUnittest;
            return that;
        }
    }
    public static class AsmStatement extends Statement
    {
        public Ptr<Token> tokens = null;
        public  AsmStatement(Loc loc, Ptr<Token> tokens) {
            super(loc);
            this.tokens = tokens;
        }

        public  Statement syntaxCopy() {
            return new AsmStatement(this.loc, this.tokens);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AsmStatement() {}

        public AsmStatement copy() {
            AsmStatement that = new AsmStatement();
            that.tokens = this.tokens;
            that.loc = this.loc;
            return that;
        }
    }
    public static class InlineAsmStatement extends AsmStatement
    {
        public Ptr<code> asmcode = null;
        public int asmalign = 0;
        public int regs = 0;
        public boolean refparam = false;
        public boolean naked = false;
        public  InlineAsmStatement(Loc loc, Ptr<Token> tokens) {
            super(loc, tokens);
        }

        public  Statement syntaxCopy() {
            return new InlineAsmStatement(this.loc, this.tokens);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public InlineAsmStatement() {}

        public InlineAsmStatement copy() {
            InlineAsmStatement that = new InlineAsmStatement();
            that.asmcode = this.asmcode;
            that.asmalign = this.asmalign;
            that.regs = this.regs;
            that.refparam = this.refparam;
            that.naked = this.naked;
            that.tokens = this.tokens;
            that.loc = this.loc;
            return that;
        }
    }
    public static class GccAsmStatement extends AsmStatement
    {
        public long stc = 0;
        public Expression insn = null;
        public Ptr<DArray<Expression>> args = null;
        public int outputargs = 0;
        public Ptr<DArray<Identifier>> names = null;
        public Ptr<DArray<Expression>> constraints = null;
        public Ptr<DArray<Expression>> clobbers = null;
        public Ptr<DArray<Identifier>> labels = null;
        public Ptr<DArray<GotoStatement>> gotos = null;
        public  GccAsmStatement(Loc loc, Ptr<Token> tokens) {
            super(loc, tokens);
        }

        public  Statement syntaxCopy() {
            return new GccAsmStatement(this.loc, this.tokens);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public GccAsmStatement() {}

        public GccAsmStatement copy() {
            GccAsmStatement that = new GccAsmStatement();
            that.stc = this.stc;
            that.insn = this.insn;
            that.args = this.args;
            that.outputargs = this.outputargs;
            that.names = this.names;
            that.constraints = this.constraints;
            that.clobbers = this.clobbers;
            that.labels = this.labels;
            that.gotos = this.gotos;
            that.tokens = this.tokens;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CompoundAsmStatement extends CompoundStatement
    {
        public long stc = 0;
        public  CompoundAsmStatement(Loc loc, Ptr<DArray<Statement>> statements, long stc) {
            super(loc, statements);
            this.stc = stc;
        }

        public  CompoundAsmStatement syntaxCopy() {
            Ptr<DArray<Statement>> a = new DArray<Statement>((this.statements.get()).length);
            {
                Slice<Statement> __r1586 = (this.statements.get()).opSlice().copy();
                int __key1585 = 0;
                for (; (__key1585 < __r1586.getLength());__key1585 += 1) {
                    Statement s = __r1586.get(__key1585);
                    int i = __key1585;
                    a.get().set(i, s != null ? s.syntaxCopy() : null);
                }
            }
            return (Statement)new CompoundAsmStatement(this.loc, a, this.stc);
        }

        public  Ptr<DArray<Statement>> flatten(Ptr<Scope> sc) {
            return null;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CompoundAsmStatement() {}

        public CompoundAsmStatement copy() {
            CompoundAsmStatement that = new CompoundAsmStatement();
            that.stc = this.stc;
            that.statements = this.statements;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ImportStatement extends Statement
    {
        public Ptr<DArray<Dsymbol>> imports = null;
        public  ImportStatement(Loc loc, Ptr<DArray<Dsymbol>> imports) {
            super(loc);
            this.imports = imports;
        }

        public  Statement syntaxCopy() {
            Ptr<DArray<Dsymbol>> m = new DArray<Dsymbol>((this.imports.get()).length);
            {
                Slice<Dsymbol> __r1588 = (this.imports.get()).opSlice().copy();
                int __key1587 = 0;
                for (; (__key1587 < __r1588.getLength());__key1587 += 1) {
                    Dsymbol s = __r1588.get(__key1587);
                    int i = __key1587;
                    m.get().set(i, s.syntaxCopy(null));
                }
            }
            return new ImportStatement(this.loc, m);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ImportStatement() {}

        public ImportStatement copy() {
            ImportStatement that = new ImportStatement();
            that.imports = this.imports;
            that.loc = this.loc;
            return that;
        }
    }
}
