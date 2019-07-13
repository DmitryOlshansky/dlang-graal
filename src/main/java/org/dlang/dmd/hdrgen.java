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
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.complex.*;
import static org.dlang.dmd.cond.*;
import static org.dlang.dmd.ctfeexpr.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.denum.*;
import static org.dlang.dmd.dimport.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.doc.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.dversion.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.nspace.*;
import static org.dlang.dmd.parse.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.staticassert.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.utils.*;
import static org.dlang.dmd.visitor.*;

public class hdrgen {
    private static final SCstring[] initializer_0 = {new SCstring(256L, TOK.auto_, new ByteSlice()), new SCstring(524288L, TOK.scope_, new ByteSlice()), new SCstring(1L, TOK.static_, new ByteSlice()), new SCstring(2L, TOK.extern_, new ByteSlice()), new SCstring(4L, TOK.const_, new ByteSlice()), new SCstring(8L, TOK.final_, new ByteSlice()), new SCstring(16L, TOK.abstract_, new ByteSlice()), new SCstring(512L, TOK.synchronized_, new ByteSlice()), new SCstring(1024L, TOK.deprecated_, new ByteSlice()), new SCstring(128L, TOK.override_, new ByteSlice()), new SCstring(8192L, TOK.lazy_, new ByteSlice()), new SCstring(268435456L, TOK.alias_, new ByteSlice()), new SCstring(4096L, TOK.out_, new ByteSlice()), new SCstring(2048L, TOK.in_, new ByteSlice()), new SCstring(8388608L, TOK.enum_, new ByteSlice()), new SCstring(1048576L, TOK.immutable_, new ByteSlice()), new SCstring(536870912L, TOK.shared_, new ByteSlice()), new SCstring(33554432L, TOK.nothrow_, new ByteSlice()), new SCstring(2147483648L, TOK.inout_, new ByteSlice()), new SCstring(67108864L, TOK.pure_, new ByteSlice()), new SCstring(2097152L, TOK.ref_, new ByteSlice()), new SCstring(17592186044416L, TOK.return_, new ByteSlice()), new SCstring(134217728L, TOK.reserved, new ByteSlice()), new SCstring(1073741824L, TOK.gshared, new ByteSlice()), new SCstring(4398046511104L, TOK.at, new ByteSlice("@nogc")), new SCstring(4294967296L, TOK.at, new ByteSlice("@property")), new SCstring(8589934592L, TOK.at, new ByteSlice("@safe")), new SCstring(17179869184L, TOK.at, new ByteSlice("@trusted")), new SCstring(34359738368L, TOK.at, new ByteSlice("@system")), new SCstring(137438953472L, TOK.at, new ByteSlice("@disable")), new SCstring(1125899906842624L, TOK.at, new ByteSlice("@__future")), new SCstring(2251799813685248L, TOK.at, new ByteSlice("__local")), new SCstring(0L, TOK.reserved, new ByteSlice())};
    private static class SCstring
    {
        private long stc = 0;
        private byte tok = 0;
        private ByteSlice id = new ByteSlice();
        public SCstring(){
        }
        public SCstring copy(){
            SCstring r = new SCstring();
            r.stc = stc;
            r.tok = tok;
            r.id = id.copy();
            return r;
        }
        public SCstring(long stc, byte tok, ByteSlice id) {
            this.stc = stc;
            this.tok = tok;
            this.id = id;
        }

        public SCstring opAssign(SCstring that) {
            this.stc = that.stc;
            this.tok = that.tok;
            this.id = that.id;
            return this;
        }
    }
    static Slice<SCstring> stcToStringtable = slice(initializer_0);

    public static class HdrGenState
    {
        public boolean hdrgen = false;
        public Ref<Boolean> ddoc = ref(false);
        public boolean fullDump = false;
        public Ref<Boolean> fullQual = ref(false);
        public int tpltMember = 0;
        public int autoMember = 0;
        public int forStmtInit = 0;
        public Ref<Boolean> declstring = ref(false);
        public EnumDeclaration inEnumDecl = null;
        public HdrGenState(){
        }
        public HdrGenState copy(){
            HdrGenState r = new HdrGenState();
            r.hdrgen = hdrgen;
            r.ddoc = ddoc;
            r.fullDump = fullDump;
            r.fullQual = fullQual;
            r.tpltMember = tpltMember;
            r.autoMember = autoMember;
            r.forStmtInit = forStmtInit;
            r.declstring = declstring;
            r.inEnumDecl = inEnumDecl;
            return r;
        }
        public HdrGenState(boolean hdrgen, boolean ddoc, boolean fullDump, boolean fullQual, int tpltMember, int autoMember, int forStmtInit, boolean declstring, EnumDeclaration inEnumDecl) {
            this.hdrgen = hdrgen;
            this.ddoc = ddoc;
            this.fullDump = fullDump;
            this.fullQual = fullQual;
            this.tpltMember = tpltMember;
            this.autoMember = autoMember;
            this.forStmtInit = forStmtInit;
            this.declstring = declstring;
            this.inEnumDecl = inEnumDecl;
        }

        public HdrGenState opAssign(HdrGenState that) {
            this.hdrgen = that.hdrgen;
            this.ddoc = that.ddoc;
            this.fullDump = that.fullDump;
            this.fullQual = that.fullQual;
            this.tpltMember = that.tpltMember;
            this.autoMember = that.autoMember;
            this.forStmtInit = that.forStmtInit;
            this.declstring = that.declstring;
            this.inEnumDecl = that.inEnumDecl;
            return this;
        }
    }
    static int TEST_EMIT_ALL = 0;
    public static void genhdrfile(dmodule.Module m) {
        Ref<OutBuffer> buf = ref(new OutBuffer());
        try {
            buf.value.doindent = true;
            buf.value.printf(new BytePtr("// D import file generated from '%s'"), m.srcfile.toChars());
            buf.value.writenl();
            Ref<HdrGenState> hgs = ref(new HdrGenState());
            hgs.value.hdrgen = true;
            toCBuffer((Dsymbol)m, ptr(buf), ptr(hgs));
            writeFile(m.loc.value, m.hdrfile.asString(), toByteSlice(buf.value.peekSlice()));
        }
        finally {
        }
    }

    public static void moduleToBuffer(Ptr<OutBuffer> buf, dmodule.Module m) {
        Ref<HdrGenState> hgs = ref(new HdrGenState());
        hgs.value.fullDump = true;
        toCBuffer((Dsymbol)m, buf, ptr(hgs));
    }

    public static void moduleToBuffer2(dmodule.Module m, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        if (m.md != null)
        {
            if (m.userAttribDecl != null)
            {
                (buf.get()).writestring(new ByteSlice("@("));
                argsToBuffer(m.userAttribDecl.atts, buf, hgs, null);
                (buf.get()).writeByte(41);
                (buf.get()).writenl();
            }
            if ((m.md.get()).isdeprecated)
            {
                if ((m.md.get()).msg != null)
                {
                    (buf.get()).writestring(new ByteSlice("deprecated("));
                    expressionToBuffer((m.md.get()).msg, buf, hgs);
                    (buf.get()).writestring(new ByteSlice(") "));
                }
                else
                    (buf.get()).writestring(new ByteSlice("deprecated "));
            }
            (buf.get()).writestring(new ByteSlice("module "));
            (buf.get()).writestring((m.md.get()).toChars());
            (buf.get()).writeByte(59);
            (buf.get()).writenl();
        }
        {
            Slice<Dsymbol> __r1417 = (m.members.value.get()).opSlice().copy();
            int __key1418 = 0;
            for (; (__key1418 < __r1417.getLength());__key1418 += 1) {
                Dsymbol s = __r1417.get(__key1418);
                dsymbolToBuffer(s, buf, hgs);
            }
        }
    }

    public static void statementToBuffer(Statement s, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        StatementPrettyPrintVisitor v = new StatementPrettyPrintVisitor(buf, hgs);
        s.accept(v);
    }

    public static class StatementPrettyPrintVisitor extends Visitor
    {
        public Ptr<OutBuffer> buf = null;
        public Ptr<HdrGenState> hgs = null;
        public  StatementPrettyPrintVisitor(Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
            this.buf = buf;
            this.hgs = hgs;
        }

        public  void visit(Statement s) {
            (this.buf.get()).writestring(new ByteSlice("Statement::toCBuffer()"));
            (this.buf.get()).writenl();
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ErrorStatement s) {
            (this.buf.get()).writestring(new ByteSlice("__error__"));
            (this.buf.get()).writenl();
        }

        public  void visit(ExpStatement s) {
            if ((s.exp.value != null) && ((s.exp.value.op.value & 0xFF) == 38) && (((DeclarationExp)s.exp.value).declaration.value != null))
            {
                dsymbolToBuffer(((DeclarationExp)s.exp.value).declaration.value, this.buf, this.hgs);
                return ;
            }
            if (s.exp.value != null)
                expressionToBuffer(s.exp.value, this.buf, this.hgs);
            (this.buf.get()).writeByte(59);
            if ((this.hgs.get()).forStmtInit == 0)
                (this.buf.get()).writenl();
        }

        public  void visit(CompileStatement s) {
            (this.buf.get()).writestring(new ByteSlice("mixin("));
            argsToBuffer(s.exps, this.buf, this.hgs, null);
            (this.buf.get()).writestring(new ByteSlice(");"));
            if ((this.hgs.get()).forStmtInit == 0)
                (this.buf.get()).writenl();
        }

        public  void visit(CompoundStatement s) {
            {
                Slice<Statement> __r1419 = (s.statements.get()).opSlice().copy();
                int __key1420 = 0;
                for (; (__key1420 < __r1419.getLength());__key1420 += 1) {
                    Statement sx = __r1419.get(__key1420);
                    if (sx != null)
                        sx.accept(this);
                }
            }
        }

        public  void visit(CompoundDeclarationStatement s) {
            boolean anywritten = false;
            {
                Slice<Statement> __r1421 = (s.statements.get()).opSlice().copy();
                int __key1422 = 0;
                for (; (__key1422 < __r1421.getLength());__key1422 += 1) {
                    Statement sx = __r1421.get(__key1422);
                    ExpStatement ds = sx != null ? sx.isExpStatement() : null;
                    if ((ds != null) && ((ds.exp.value.op.value & 0xFF) == 38))
                    {
                        Dsymbol d = ((DeclarationExp)ds.exp.value).declaration.value;
                        assert(d.isDeclaration() != null);
                        {
                            VarDeclaration v = d.isVarDeclaration();
                            if ((v) != null)
                            {
                                DsymbolPrettyPrintVisitor ppv = new DsymbolPrettyPrintVisitor(this.buf, this.hgs);
                                ppv.visitVarDecl(v, anywritten);
                            }
                            else
                                dsymbolToBuffer(d, this.buf, this.hgs);
                        }
                        anywritten = true;
                    }
                }
            }
            (this.buf.get()).writeByte(59);
            if ((this.hgs.get()).forStmtInit == 0)
                (this.buf.get()).writenl();
        }

        public  void visit(UnrolledLoopStatement s) {
            (this.buf.get()).writestring(new ByteSlice("/*unrolled*/ {"));
            (this.buf.get()).writenl();
            (this.buf.get()).level++;
            {
                Slice<Statement> __r1423 = (s.statements.get()).opSlice().copy();
                int __key1424 = 0;
                for (; (__key1424 < __r1423.getLength());__key1424 += 1) {
                    Statement sx = __r1423.get(__key1424);
                    if (sx != null)
                        sx.accept(this);
                }
            }
            (this.buf.get()).level--;
            (this.buf.get()).writeByte(125);
            (this.buf.get()).writenl();
        }

        public  void visit(ScopeStatement s) {
            (this.buf.get()).writeByte(123);
            (this.buf.get()).writenl();
            (this.buf.get()).level++;
            if (s.statement.value != null)
                s.statement.value.accept(this);
            (this.buf.get()).level--;
            (this.buf.get()).writeByte(125);
            (this.buf.get()).writenl();
        }

        public  void visit(WhileStatement s) {
            (this.buf.get()).writestring(new ByteSlice("while ("));
            expressionToBuffer(s.condition, this.buf, this.hgs);
            (this.buf.get()).writeByte(41);
            (this.buf.get()).writenl();
            if (s._body != null)
                s._body.accept(this);
        }

        public  void visit(DoStatement s) {
            (this.buf.get()).writestring(new ByteSlice("do"));
            (this.buf.get()).writenl();
            if (s._body.value != null)
                s._body.value.accept(this);
            (this.buf.get()).writestring(new ByteSlice("while ("));
            expressionToBuffer(s.condition.value, this.buf, this.hgs);
            (this.buf.get()).writestring(new ByteSlice(");"));
            (this.buf.get()).writenl();
        }

        public  void visit(ForStatement s) {
            (this.buf.get()).writestring(new ByteSlice("for ("));
            if (s._init.value != null)
            {
                (this.hgs.get()).forStmtInit++;
                s._init.value.accept(this);
                (this.hgs.get()).forStmtInit--;
            }
            else
                (this.buf.get()).writeByte(59);
            if (s.condition.value != null)
            {
                (this.buf.get()).writeByte(32);
                expressionToBuffer(s.condition.value, this.buf, this.hgs);
            }
            (this.buf.get()).writeByte(59);
            if (s.increment.value != null)
            {
                (this.buf.get()).writeByte(32);
                expressionToBuffer(s.increment.value, this.buf, this.hgs);
            }
            (this.buf.get()).writeByte(41);
            (this.buf.get()).writenl();
            (this.buf.get()).writeByte(123);
            (this.buf.get()).writenl();
            (this.buf.get()).level++;
            if (s._body.value != null)
                s._body.value.accept(this);
            (this.buf.get()).level--;
            (this.buf.get()).writeByte(125);
            (this.buf.get()).writenl();
        }

        public  void foreachWithoutBody(ForeachStatement s) {
            (this.buf.get()).writestring(Token.asString(s.op.value));
            (this.buf.get()).writestring(new ByteSlice(" ("));
            {
                Slice<Parameter> __r1426 = (s.parameters.get()).opSlice().copy();
                int __key1425 = 0;
                for (; (__key1425 < __r1426.getLength());__key1425 += 1) {
                    Parameter p = __r1426.get(__key1425);
                    int i = __key1425;
                    if (i != 0)
                        (this.buf.get()).writestring(new ByteSlice(", "));
                    if (stcToBuffer(this.buf, p.storageClass.value))
                        (this.buf.get()).writeByte(32);
                    if (p.type.value != null)
                        typeToBuffer(p.type.value, p.ident.value, this.buf, this.hgs);
                    else
                        (this.buf.get()).writestring(p.ident.value.asString());
                }
            }
            (this.buf.get()).writestring(new ByteSlice("; "));
            expressionToBuffer(s.aggr.value, this.buf, this.hgs);
            (this.buf.get()).writeByte(41);
            (this.buf.get()).writenl();
        }

        public  void visit(ForeachStatement s) {
            this.foreachWithoutBody(s);
            (this.buf.get()).writeByte(123);
            (this.buf.get()).writenl();
            (this.buf.get()).level++;
            if (s._body.value != null)
                s._body.value.accept(this);
            (this.buf.get()).level--;
            (this.buf.get()).writeByte(125);
            (this.buf.get()).writenl();
        }

        public  void foreachRangeWithoutBody(ForeachRangeStatement s) {
            (this.buf.get()).writestring(Token.asString(s.op.value));
            (this.buf.get()).writestring(new ByteSlice(" ("));
            if (s.prm.type.value != null)
                typeToBuffer(s.prm.type.value, s.prm.ident.value, this.buf, this.hgs);
            else
                (this.buf.get()).writestring(s.prm.ident.value.asString());
            (this.buf.get()).writestring(new ByteSlice("; "));
            expressionToBuffer(s.lwr.value, this.buf, this.hgs);
            (this.buf.get()).writestring(new ByteSlice(" .. "));
            expressionToBuffer(s.upr.value, this.buf, this.hgs);
            (this.buf.get()).writeByte(41);
            (this.buf.get()).writenl();
        }

        public  void visit(ForeachRangeStatement s) {
            this.foreachRangeWithoutBody(s);
            (this.buf.get()).writeByte(123);
            (this.buf.get()).writenl();
            (this.buf.get()).level++;
            if (s._body.value != null)
                s._body.value.accept(this);
            (this.buf.get()).level--;
            (this.buf.get()).writeByte(125);
            (this.buf.get()).writenl();
        }

        public  void visit(StaticForeachStatement s) {
            (this.buf.get()).writestring(new ByteSlice("static "));
            if (s.sfe.aggrfe.value != null)
            {
                this.visit(s.sfe.aggrfe.value);
            }
            else
            {
                assert(s.sfe.rangefe.value != null);
                this.visit(s.sfe.rangefe.value);
            }
        }

        public  void visit(IfStatement s) {
            (this.buf.get()).writestring(new ByteSlice("if ("));
            {
                Parameter p = s.prm;
                if ((p) != null)
                {
                    long stc = p.storageClass.value;
                    if ((p.type.value == null) && (stc == 0))
                        stc = 256L;
                    if (stcToBuffer(this.buf, stc))
                        (this.buf.get()).writeByte(32);
                    if (p.type.value != null)
                        typeToBuffer(p.type.value, p.ident.value, this.buf, this.hgs);
                    else
                        (this.buf.get()).writestring(p.ident.value.asString());
                    (this.buf.get()).writestring(new ByteSlice(" = "));
                }
            }
            expressionToBuffer(s.condition.value, this.buf, this.hgs);
            (this.buf.get()).writeByte(41);
            (this.buf.get()).writenl();
            if (s.ifbody.value.isScopeStatement() != null)
            {
                s.ifbody.value.accept(this);
            }
            else
            {
                (this.buf.get()).level++;
                s.ifbody.value.accept(this);
                (this.buf.get()).level--;
            }
            if (s.elsebody.value != null)
            {
                (this.buf.get()).writestring(new ByteSlice("else"));
                if (s.elsebody.value.isIfStatement() == null)
                {
                    (this.buf.get()).writenl();
                }
                else
                {
                    (this.buf.get()).writeByte(32);
                }
                if ((s.elsebody.value.isScopeStatement() != null) || (s.elsebody.value.isIfStatement() != null))
                {
                    s.elsebody.value.accept(this);
                }
                else
                {
                    (this.buf.get()).level++;
                    s.elsebody.value.accept(this);
                    (this.buf.get()).level--;
                }
            }
        }

        public  void visit(ConditionalStatement s) {
            conditionToBuffer(s.condition, this.buf, this.hgs);
            (this.buf.get()).writenl();
            (this.buf.get()).writeByte(123);
            (this.buf.get()).writenl();
            (this.buf.get()).level++;
            if (s.ifbody.value != null)
                s.ifbody.value.accept(this);
            (this.buf.get()).level--;
            (this.buf.get()).writeByte(125);
            (this.buf.get()).writenl();
            if (s.elsebody.value != null)
            {
                (this.buf.get()).writestring(new ByteSlice("else"));
                (this.buf.get()).writenl();
                (this.buf.get()).writeByte(123);
                (this.buf.get()).level++;
                (this.buf.get()).writenl();
                s.elsebody.value.accept(this);
                (this.buf.get()).level--;
                (this.buf.get()).writeByte(125);
            }
            (this.buf.get()).writenl();
        }

        public  void visit(PragmaStatement s) {
            (this.buf.get()).writestring(new ByteSlice("pragma ("));
            (this.buf.get()).writestring(s.ident.asString());
            if ((s.args != null) && ((s.args.get()).length.value != 0))
            {
                (this.buf.get()).writestring(new ByteSlice(", "));
                argsToBuffer(s.args, this.buf, this.hgs, null);
            }
            (this.buf.get()).writeByte(41);
            if (s._body != null)
            {
                (this.buf.get()).writenl();
                (this.buf.get()).writeByte(123);
                (this.buf.get()).writenl();
                (this.buf.get()).level++;
                s._body.accept(this);
                (this.buf.get()).level--;
                (this.buf.get()).writeByte(125);
                (this.buf.get()).writenl();
            }
            else
            {
                (this.buf.get()).writeByte(59);
                (this.buf.get()).writenl();
            }
        }

        public  void visit(StaticAssertStatement s) {
            dsymbolToBuffer(s.sa, this.buf, this.hgs);
        }

        public  void visit(SwitchStatement s) {
            (this.buf.get()).writestring(s.isFinal ? new ByteSlice("final switch (") : new ByteSlice("switch ("));
            expressionToBuffer(s.condition.value, this.buf, this.hgs);
            (this.buf.get()).writeByte(41);
            (this.buf.get()).writenl();
            if (s._body.value != null)
            {
                if (s._body.value.isScopeStatement() == null)
                {
                    (this.buf.get()).writeByte(123);
                    (this.buf.get()).writenl();
                    (this.buf.get()).level++;
                    s._body.value.accept(this);
                    (this.buf.get()).level--;
                    (this.buf.get()).writeByte(125);
                    (this.buf.get()).writenl();
                }
                else
                {
                    s._body.value.accept(this);
                }
            }
        }

        public  void visit(CaseStatement s) {
            (this.buf.get()).writestring(new ByteSlice("case "));
            expressionToBuffer(s.exp, this.buf, this.hgs);
            (this.buf.get()).writeByte(58);
            (this.buf.get()).writenl();
            s.statement.value.accept(this);
        }

        public  void visit(CaseRangeStatement s) {
            (this.buf.get()).writestring(new ByteSlice("case "));
            expressionToBuffer(s.first, this.buf, this.hgs);
            (this.buf.get()).writestring(new ByteSlice(": .. case "));
            expressionToBuffer(s.last, this.buf, this.hgs);
            (this.buf.get()).writeByte(58);
            (this.buf.get()).writenl();
            s.statement.accept(this);
        }

        public  void visit(DefaultStatement s) {
            (this.buf.get()).writestring(new ByteSlice("default:"));
            (this.buf.get()).writenl();
            s.statement.value.accept(this);
        }

        public  void visit(GotoDefaultStatement s) {
            (this.buf.get()).writestring(new ByteSlice("goto default;"));
            (this.buf.get()).writenl();
        }

        public  void visit(GotoCaseStatement s) {
            (this.buf.get()).writestring(new ByteSlice("goto case"));
            if (s.exp != null)
            {
                (this.buf.get()).writeByte(32);
                expressionToBuffer(s.exp, this.buf, this.hgs);
            }
            (this.buf.get()).writeByte(59);
            (this.buf.get()).writenl();
        }

        public  void visit(SwitchErrorStatement s) {
            (this.buf.get()).writestring(new ByteSlice("SwitchErrorStatement::toCBuffer()"));
            (this.buf.get()).writenl();
        }

        public  void visit(ReturnStatement s) {
            (this.buf.get()).writestring(new ByteSlice("return "));
            if (s.exp.value != null)
                expressionToBuffer(s.exp.value, this.buf, this.hgs);
            (this.buf.get()).writeByte(59);
            (this.buf.get()).writenl();
        }

        public  void visit(BreakStatement s) {
            (this.buf.get()).writestring(new ByteSlice("break"));
            if (s.ident.value != null)
            {
                (this.buf.get()).writeByte(32);
                (this.buf.get()).writestring(s.ident.value.asString());
            }
            (this.buf.get()).writeByte(59);
            (this.buf.get()).writenl();
        }

        public  void visit(ContinueStatement s) {
            (this.buf.get()).writestring(new ByteSlice("continue"));
            if (s.ident.value != null)
            {
                (this.buf.get()).writeByte(32);
                (this.buf.get()).writestring(s.ident.value.asString());
            }
            (this.buf.get()).writeByte(59);
            (this.buf.get()).writenl();
        }

        public  void visit(SynchronizedStatement s) {
            (this.buf.get()).writestring(new ByteSlice("synchronized"));
            if (s.exp != null)
            {
                (this.buf.get()).writeByte(40);
                expressionToBuffer(s.exp, this.buf, this.hgs);
                (this.buf.get()).writeByte(41);
            }
            if (s._body.value != null)
            {
                (this.buf.get()).writeByte(32);
                s._body.value.accept(this);
            }
        }

        public  void visit(WithStatement s) {
            (this.buf.get()).writestring(new ByteSlice("with ("));
            expressionToBuffer(s.exp.value, this.buf, this.hgs);
            (this.buf.get()).writestring(new ByteSlice(")"));
            (this.buf.get()).writenl();
            if (s._body.value != null)
                s._body.value.accept(this);
        }

        public  void visit(TryCatchStatement s) {
            (this.buf.get()).writestring(new ByteSlice("try"));
            (this.buf.get()).writenl();
            if (s._body.value != null)
            {
                if (s._body.value.isScopeStatement() != null)
                {
                    s._body.value.accept(this);
                }
                else
                {
                    (this.buf.get()).level++;
                    s._body.value.accept(this);
                    (this.buf.get()).level--;
                }
            }
            {
                Slice<Catch> __r1427 = (s.catches.get()).opSlice().copy();
                int __key1428 = 0;
                for (; (__key1428 < __r1427.getLength());__key1428 += 1) {
                    Catch c = __r1427.get(__key1428);
                    this.visit(c);
                }
            }
        }

        public  void visit(TryFinallyStatement s) {
            (this.buf.get()).writestring(new ByteSlice("try"));
            (this.buf.get()).writenl();
            (this.buf.get()).writeByte(123);
            (this.buf.get()).writenl();
            (this.buf.get()).level++;
            s._body.value.accept(this);
            (this.buf.get()).level--;
            (this.buf.get()).writeByte(125);
            (this.buf.get()).writenl();
            (this.buf.get()).writestring(new ByteSlice("finally"));
            (this.buf.get()).writenl();
            if (s.finalbody.value.isScopeStatement() != null)
            {
                s.finalbody.value.accept(this);
            }
            else
            {
                (this.buf.get()).level++;
                s.finalbody.value.accept(this);
                (this.buf.get()).level--;
            }
        }

        public  void visit(ScopeGuardStatement s) {
            (this.buf.get()).writestring(Token.asString(s.tok));
            (this.buf.get()).writeByte(32);
            if (s.statement != null)
                s.statement.accept(this);
        }

        public  void visit(ThrowStatement s) {
            (this.buf.get()).writestring(new ByteSlice("throw "));
            expressionToBuffer(s.exp, this.buf, this.hgs);
            (this.buf.get()).writeByte(59);
            (this.buf.get()).writenl();
        }

        public  void visit(DebugStatement s) {
            if (s.statement != null)
            {
                s.statement.accept(this);
            }
        }

        public  void visit(GotoStatement s) {
            (this.buf.get()).writestring(new ByteSlice("goto "));
            (this.buf.get()).writestring(s.ident.asString());
            (this.buf.get()).writeByte(59);
            (this.buf.get()).writenl();
        }

        public  void visit(LabelStatement s) {
            (this.buf.get()).writestring(s.ident.asString());
            (this.buf.get()).writeByte(58);
            (this.buf.get()).writenl();
            if (s.statement.value != null)
                s.statement.value.accept(this);
        }

        public  void visit(AsmStatement s) {
            (this.buf.get()).writestring(new ByteSlice("asm { "));
            Ptr<Token> t = s.tokens;
            (this.buf.get()).level++;
            for (; t != null;){
                (this.buf.get()).writestring((t.get()).toChars());
                if (((t.get()).next != null) && (((t.get()).value & 0xFF) != 75) && (((t.get()).value & 0xFF) != 99) && ((((t.get()).next.get()).value & 0xFF) != 99) && (((t.get()).value & 0xFF) != 3) && ((((t.get()).next.get()).value & 0xFF) != 3) && ((((t.get()).next.get()).value & 0xFF) != 4) && (((t.get()).value & 0xFF) != 1) && ((((t.get()).next.get()).value & 0xFF) != 1) && ((((t.get()).next.get()).value & 0xFF) != 2) && (((t.get()).value & 0xFF) != 97) && ((((t.get()).next.get()).value & 0xFF) != 97))
                {
                    (this.buf.get()).writeByte(32);
                }
                t = (t.get()).next;
            }
            (this.buf.get()).level--;
            (this.buf.get()).writestring(new ByteSlice("; }"));
            (this.buf.get()).writenl();
        }

        public  void visit(ImportStatement s) {
            {
                Slice<Dsymbol> __r1429 = (s.imports.get()).opSlice().copy();
                int __key1430 = 0;
                for (; (__key1430 < __r1429.getLength());__key1430 += 1) {
                    Dsymbol imp = __r1429.get(__key1430);
                    dsymbolToBuffer(imp, this.buf, this.hgs);
                }
            }
        }

        public  void visit(Catch c) {
            (this.buf.get()).writestring(new ByteSlice("catch"));
            if (c.type.value != null)
            {
                (this.buf.get()).writeByte(40);
                typeToBuffer(c.type.value, c.ident, this.buf, this.hgs);
                (this.buf.get()).writeByte(41);
            }
            (this.buf.get()).writenl();
            (this.buf.get()).writeByte(123);
            (this.buf.get()).writenl();
            (this.buf.get()).level++;
            if (c.handler.value != null)
                c.handler.value.accept(this);
            (this.buf.get()).level--;
            (this.buf.get()).writeByte(125);
            (this.buf.get()).writenl();
        }


        public StatementPrettyPrintVisitor() {}

        public StatementPrettyPrintVisitor copy() {
            StatementPrettyPrintVisitor that = new StatementPrettyPrintVisitor();
            that.buf = this.buf;
            that.hgs = this.hgs;
            return that;
        }
    }
    public static void dsymbolToBuffer(Dsymbol s, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        DsymbolPrettyPrintVisitor v = new DsymbolPrettyPrintVisitor(buf, hgs);
        s.accept(v);
    }

    public static class DsymbolPrettyPrintVisitor extends Visitor
    {
        public Ref<Ptr<OutBuffer>> buf = ref(null);
        public Ref<Ptr<HdrGenState>> hgs = ref(null);
        public  DsymbolPrettyPrintVisitor(Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
            this.buf.value = buf;
            this.hgs.value = hgs;
        }

        public  void visit(Dsymbol s) {
            (this.buf.value.get()).writestring(s.toChars());
        }

        public  void visit(StaticAssert s) {
            (this.buf.value.get()).writestring(s.kind());
            (this.buf.value.get()).writeByte(40);
            expressionToBuffer(s.exp, this.buf.value, this.hgs.value);
            if (s.msg != null)
            {
                (this.buf.value.get()).writestring(new ByteSlice(", "));
                expressionToBuffer(s.msg, this.buf.value, this.hgs.value);
            }
            (this.buf.value.get()).writestring(new ByteSlice(");"));
            (this.buf.value.get()).writenl();
        }

        public  void visit(DebugSymbol s) {
            (this.buf.value.get()).writestring(new ByteSlice("debug = "));
            if (s.ident.value != null)
                (this.buf.value.get()).writestring(s.ident.value.asString());
            else
                (this.buf.value.get()).print((long)s.level);
            (this.buf.value.get()).writeByte(59);
            (this.buf.value.get()).writenl();
        }

        public  void visit(VersionSymbol s) {
            (this.buf.value.get()).writestring(new ByteSlice("version = "));
            if (s.ident.value != null)
                (this.buf.value.get()).writestring(s.ident.value.asString());
            else
                (this.buf.value.get()).print((long)s.level);
            (this.buf.value.get()).writeByte(59);
            (this.buf.value.get()).writenl();
        }

        public  void visit(EnumMember em) {
            if (em.type.value != null)
                typeToBuffer(em.type.value, em.ident.value, this.buf.value, this.hgs.value);
            else
                (this.buf.value.get()).writestring(em.ident.value.asString());
            if (em.value() != null)
            {
                (this.buf.value.get()).writestring(new ByteSlice(" = "));
                expressionToBuffer(em.value(), this.buf.value, this.hgs.value);
            }
        }

        public  void visit(Import imp) {
            if ((this.hgs.value.get()).hdrgen && (pequals(imp.id, Id.object.value)))
                return ;
            if (imp.isstatic != 0)
                (this.buf.value.get()).writestring(new ByteSlice("static "));
            (this.buf.value.get()).writestring(new ByteSlice("import "));
            if (imp.aliasId != null)
            {
                (this.buf.value.get()).printf(new BytePtr("%s = "), imp.aliasId.toChars());
            }
            if ((imp.packages.value != null) && ((imp.packages.value.get()).length.value != 0))
            {
                {
                    Slice<Identifier> __r1431 = (imp.packages.value.get()).opSlice().copy();
                    int __key1432 = 0;
                    for (; (__key1432 < __r1431.getLength());__key1432 += 1) {
                        Identifier pid = __r1431.get(__key1432);
                        (this.buf.value.get()).printf(new BytePtr("%s."), pid.toChars());
                    }
                }
            }
            (this.buf.value.get()).writestring(imp.id.asString());
            if (imp.names.length.value != 0)
            {
                (this.buf.value.get()).writestring(new ByteSlice(" : "));
                {
                    Slice<Identifier> __r1434 = imp.names.opSlice().copy();
                    int __key1433 = 0;
                    for (; (__key1433 < __r1434.getLength());__key1433 += 1) {
                        Identifier name = __r1434.get(__key1433);
                        int i = __key1433;
                        if (i != 0)
                            (this.buf.value.get()).writestring(new ByteSlice(", "));
                        Identifier _alias = imp.aliases.get(i);
                        if (_alias != null)
                            (this.buf.value.get()).printf(new BytePtr("%s = %s"), _alias.toChars(), name.toChars());
                        else
                            (this.buf.value.get()).writestring(name.toChars());
                    }
                }
            }
            (this.buf.value.get()).writeByte(59);
            (this.buf.value.get()).writenl();
        }

        public  void visit(AliasThis d) {
            (this.buf.value.get()).writestring(new ByteSlice("alias "));
            (this.buf.value.get()).writestring(d.ident.asString());
            (this.buf.value.get()).writestring(new ByteSlice(" this;\n"));
        }

        public  void visit(AttribDeclaration d) {
            if (d.decl.value == null)
            {
                (this.buf.value.get()).writeByte(59);
                (this.buf.value.get()).writenl();
                return ;
            }
            if (((d.decl.value.get()).length.value == 0))
                (this.buf.value.get()).writestring(new ByteSlice("{}"));
            else if ((this.hgs.value.get()).hdrgen && ((d.decl.value.get()).length.value == 1) && ((d.decl.value.get()).get(0).isUnitTestDeclaration() != null))
            {
                (this.buf.value.get()).writestring(new ByteSlice("{}"));
            }
            else if (((d.decl.value.get()).length.value == 1))
            {
                (d.decl.value.get()).get(0).accept(this);
                return ;
            }
            else
            {
                (this.buf.value.get()).writenl();
                (this.buf.value.get()).writeByte(123);
                (this.buf.value.get()).writenl();
                (this.buf.value.get()).level++;
                {
                    Slice<Dsymbol> __r1435 = (d.decl.value.get()).opSlice().copy();
                    int __key1436 = 0;
                    for (; (__key1436 < __r1435.getLength());__key1436 += 1) {
                        Dsymbol de = __r1435.get(__key1436);
                        de.accept(this);
                    }
                }
                (this.buf.value.get()).level--;
                (this.buf.value.get()).writeByte(125);
            }
            (this.buf.value.get()).writenl();
        }

        public  void visit(StorageClassDeclaration d) {
            if (stcToBuffer(this.buf.value, d.stc))
                (this.buf.value.get()).writeByte(32);
            this.visit((AttribDeclaration)d);
        }

        public  void visit(DeprecatedDeclaration d) {
            (this.buf.value.get()).writestring(new ByteSlice("deprecated("));
            expressionToBuffer(d.msg, this.buf.value, this.hgs.value);
            (this.buf.value.get()).writestring(new ByteSlice(") "));
            this.visit((AttribDeclaration)d);
        }

        public  void visit(LinkDeclaration d) {
            (this.buf.value.get()).writestring(new ByteSlice("extern ("));
            (this.buf.value.get()).writestring(linkageToString(d.linkage));
            (this.buf.value.get()).writestring(new ByteSlice(") "));
            this.visit((AttribDeclaration)d);
        }

        public  void visit(CPPMangleDeclaration d) {
            ByteSlice s = new ByteSlice().copy();
            switch (d.cppmangle)
            {
                case CPPMANGLE.asClass:
                    s = new ByteSlice("class").copy();
                    break;
                case CPPMANGLE.asStruct:
                    s = new ByteSlice("struct").copy();
                    break;
                case CPPMANGLE.def:
                    break;
                default:
                throw SwitchError.INSTANCE;
            }
            (this.buf.value.get()).writestring(new ByteSlice("extern (C++, "));
            (this.buf.value.get()).writestring(s);
            (this.buf.value.get()).writestring(new ByteSlice(") "));
            this.visit((AttribDeclaration)d);
        }

        public  void visit(ProtDeclaration d) {
            protectionToBuffer(this.buf.value, d.protection.value);
            (this.buf.value.get()).writeByte(32);
            AttribDeclaration ad = d;
            if (((ad.decl.value.get()).length.value == 1) && ((ad.decl.value.get()).get(0).isProtDeclaration() != null))
                this.visit((AttribDeclaration)(ad.decl.value.get()).get(0));
            else
                this.visit((AttribDeclaration)d);
        }

        public  void visit(AlignDeclaration d) {
            (this.buf.value.get()).writestring(new ByteSlice("align "));
            if (d.ealign != null)
                (this.buf.value.get()).printf(new BytePtr("(%s) "), d.ealign.toChars());
            this.visit((AttribDeclaration)d);
        }

        public  void visit(AnonDeclaration d) {
            (this.buf.value.get()).writestring(d.isunion ? new ByteSlice("union") : new ByteSlice("struct"));
            (this.buf.value.get()).writenl();
            (this.buf.value.get()).writestring(new ByteSlice("{"));
            (this.buf.value.get()).writenl();
            (this.buf.value.get()).level++;
            if (d.decl.value != null)
            {
                {
                    Slice<Dsymbol> __r1437 = (d.decl.value.get()).opSlice().copy();
                    int __key1438 = 0;
                    for (; (__key1438 < __r1437.getLength());__key1438 += 1) {
                        Dsymbol de = __r1437.get(__key1438);
                        de.accept(this);
                    }
                }
            }
            (this.buf.value.get()).level--;
            (this.buf.value.get()).writestring(new ByteSlice("}"));
            (this.buf.value.get()).writenl();
        }

        public  void visit(PragmaDeclaration d) {
            (this.buf.value.get()).writestring(new ByteSlice("pragma ("));
            (this.buf.value.get()).writestring(d.ident.value.asString());
            if ((d.args != null) && ((d.args.get()).length.value != 0))
            {
                (this.buf.value.get()).writestring(new ByteSlice(", "));
                argsToBuffer(d.args, this.buf.value, this.hgs.value, null);
            }
            (this.buf.value.get()).writeByte(41);
            this.visit((AttribDeclaration)d);
        }

        public  void visit(ConditionalDeclaration d) {
            conditionToBuffer(d.condition, this.buf.value, this.hgs.value);
            if ((d.decl.value != null) || (d.elsedecl.value != null))
            {
                (this.buf.value.get()).writenl();
                (this.buf.value.get()).writeByte(123);
                (this.buf.value.get()).writenl();
                (this.buf.value.get()).level++;
                if (d.decl.value != null)
                {
                    {
                        Slice<Dsymbol> __r1439 = (d.decl.value.get()).opSlice().copy();
                        int __key1440 = 0;
                        for (; (__key1440 < __r1439.getLength());__key1440 += 1) {
                            Dsymbol de = __r1439.get(__key1440);
                            de.accept(this);
                        }
                    }
                }
                (this.buf.value.get()).level--;
                (this.buf.value.get()).writeByte(125);
                if (d.elsedecl.value != null)
                {
                    (this.buf.value.get()).writenl();
                    (this.buf.value.get()).writestring(new ByteSlice("else"));
                    (this.buf.value.get()).writenl();
                    (this.buf.value.get()).writeByte(123);
                    (this.buf.value.get()).writenl();
                    (this.buf.value.get()).level++;
                    {
                        Slice<Dsymbol> __r1441 = (d.elsedecl.value.get()).opSlice().copy();
                        int __key1442 = 0;
                        for (; (__key1442 < __r1441.getLength());__key1442 += 1) {
                            Dsymbol de = __r1441.get(__key1442);
                            de.accept(this);
                        }
                    }
                    (this.buf.value.get()).level--;
                    (this.buf.value.get()).writeByte(125);
                }
            }
            else
                (this.buf.value.get()).writeByte(58);
            (this.buf.value.get()).writenl();
        }

        public  void visit(StaticForeachDeclaration s) {
            Function1<ForeachStatement,Void> foreachWithoutBody = new Function1<ForeachStatement,Void>(){
                public Void invoke(ForeachStatement s) {
                    (buf.value.get()).writestring(Token.asString(s.op.value));
                    (buf.value.get()).writestring(new ByteSlice(" ("));
                    {
                        Ref<Slice<Parameter>> __r1444 = ref((s.parameters.get()).opSlice().copy());
                        IntRef __key1443 = ref(0);
                        for (; (__key1443.value < __r1444.value.getLength());__key1443.value += 1) {
                            Parameter p = __r1444.value.get(__key1443.value);
                            IntRef i = ref(__key1443.value);
                            if (i.value != 0)
                                (buf.value.get()).writestring(new ByteSlice(", "));
                            if (stcToBuffer(buf.value, p.storageClass.value))
                                (buf.value.get()).writeByte(32);
                            if (p.type.value != null)
                                typeToBuffer(p.type.value, p.ident.value, buf.value, hgs.value);
                            else
                                (buf.value.get()).writestring(p.ident.value.asString());
                        }
                    }
                    (buf.value.get()).writestring(new ByteSlice("; "));
                    expressionToBuffer(s.aggr.value, buf.value, hgs.value);
                    (buf.value.get()).writeByte(41);
                    (buf.value.get()).writenl();
                }
            };
            Function1<ForeachRangeStatement,Void> foreachRangeWithoutBody = new Function1<ForeachRangeStatement,Void>(){
                public Void invoke(ForeachRangeStatement s) {
                    (buf.value.get()).writestring(Token.asString(s.op.value));
                    (buf.value.get()).writestring(new ByteSlice(" ("));
                    if (s.prm.type.value != null)
                        typeToBuffer(s.prm.type.value, s.prm.ident.value, buf.value, hgs.value);
                    else
                        (buf.value.get()).writestring(s.prm.ident.value.asString());
                    (buf.value.get()).writestring(new ByteSlice("; "));
                    expressionToBuffer(s.lwr.value, buf.value, hgs.value);
                    (buf.value.get()).writestring(new ByteSlice(" .. "));
                    expressionToBuffer(s.upr.value, buf.value, hgs.value);
                    (buf.value.get()).writeByte(41);
                    (buf.value.get()).writenl();
                }
            };
            (this.buf.value.get()).writestring(new ByteSlice("static "));
            if (s.sfe.value.aggrfe.value != null)
            {
                foreachWithoutBody.invoke(s.sfe.value.aggrfe.value);
            }
            else
            {
                assert(s.sfe.value.rangefe.value != null);
                foreachRangeWithoutBody.invoke(s.sfe.value.rangefe.value);
            }
            (this.buf.value.get()).writeByte(123);
            (this.buf.value.get()).writenl();
            (this.buf.value.get()).level++;
            this.visit((AttribDeclaration)s);
            (this.buf.value.get()).level--;
            (this.buf.value.get()).writeByte(125);
            (this.buf.value.get()).writenl();
        }

        public  void visit(CompileDeclaration d) {
            (this.buf.value.get()).writestring(new ByteSlice("mixin("));
            argsToBuffer(d.exps, this.buf.value, this.hgs.value, null);
            (this.buf.value.get()).writestring(new ByteSlice(");"));
            (this.buf.value.get()).writenl();
        }

        public  void visit(UserAttributeDeclaration d) {
            (this.buf.value.get()).writestring(new ByteSlice("@("));
            argsToBuffer(d.atts, this.buf.value, this.hgs.value, null);
            (this.buf.value.get()).writeByte(41);
            this.visit((AttribDeclaration)d);
        }

        public  void visit(TemplateDeclaration d) {
            if ((this.hgs.value.get()).hdrgen || (this.hgs.value.get()).fullDump && this.visitEponymousMember(d))
                return ;
            if ((this.hgs.value.get()).ddoc.value)
                (this.buf.value.get()).writestring(d.kind());
            else
                (this.buf.value.get()).writestring(new ByteSlice("template"));
            (this.buf.value.get()).writeByte(32);
            (this.buf.value.get()).writestring(d.ident.value.asString());
            (this.buf.value.get()).writeByte(40);
            this.visitTemplateParameters((this.hgs.value.get()).ddoc.value ? d.origParameters.value : d.parameters);
            (this.buf.value.get()).writeByte(41);
            this.visitTemplateConstraint(d.constraint.value);
            if ((this.hgs.value.get()).hdrgen || (this.hgs.value.get()).fullDump)
            {
                (this.hgs.value.get()).tpltMember++;
                (this.buf.value.get()).writenl();
                (this.buf.value.get()).writeByte(123);
                (this.buf.value.get()).writenl();
                (this.buf.value.get()).level++;
                {
                    Slice<Dsymbol> __r1445 = (d.members.value.get()).opSlice().copy();
                    int __key1446 = 0;
                    for (; (__key1446 < __r1445.getLength());__key1446 += 1) {
                        Dsymbol s = __r1445.get(__key1446);
                        s.accept(this);
                    }
                }
                (this.buf.value.get()).level--;
                (this.buf.value.get()).writeByte(125);
                (this.buf.value.get()).writenl();
                (this.hgs.value.get()).tpltMember--;
            }
        }

        public  boolean visitEponymousMember(TemplateDeclaration d) {
            if ((d.members.value == null) || ((d.members.value.get()).length.value != 1))
                return false;
            Dsymbol onemember = (d.members.value.get()).get(0);
            if ((!pequals(onemember.ident.value, d.ident.value)))
                return false;
            {
                FuncDeclaration fd = onemember.isFuncDeclaration();
                if ((fd) != null)
                {
                    assert(fd.type.value != null);
                    if (stcToBuffer(this.buf.value, fd.storage_class.value))
                        (this.buf.value.get()).writeByte(32);
                    functionToBufferFull((TypeFunction)fd.type.value, this.buf.value, d.ident.value, this.hgs.value, d);
                    this.visitTemplateConstraint(d.constraint.value);
                    (this.hgs.value.get()).tpltMember++;
                    this.bodyToBuffer(fd);
                    (this.hgs.value.get()).tpltMember--;
                    return true;
                }
            }
            {
                AggregateDeclaration ad = onemember.isAggregateDeclaration();
                if ((ad) != null)
                {
                    (this.buf.value.get()).writestring(ad.kind());
                    (this.buf.value.get()).writeByte(32);
                    (this.buf.value.get()).writestring(ad.ident.value.asString());
                    (this.buf.value.get()).writeByte(40);
                    this.visitTemplateParameters((this.hgs.value.get()).ddoc.value ? d.origParameters.value : d.parameters);
                    (this.buf.value.get()).writeByte(41);
                    this.visitTemplateConstraint(d.constraint.value);
                    this.visitBaseClasses(ad.isClassDeclaration());
                    (this.hgs.value.get()).tpltMember++;
                    if (ad.members.value != null)
                    {
                        (this.buf.value.get()).writenl();
                        (this.buf.value.get()).writeByte(123);
                        (this.buf.value.get()).writenl();
                        (this.buf.value.get()).level++;
                        {
                            Slice<Dsymbol> __r1447 = (ad.members.value.get()).opSlice().copy();
                            int __key1448 = 0;
                            for (; (__key1448 < __r1447.getLength());__key1448 += 1) {
                                Dsymbol s = __r1447.get(__key1448);
                                s.accept(this);
                            }
                        }
                        (this.buf.value.get()).level--;
                        (this.buf.value.get()).writeByte(125);
                    }
                    else
                        (this.buf.value.get()).writeByte(59);
                    (this.buf.value.get()).writenl();
                    (this.hgs.value.get()).tpltMember--;
                    return true;
                }
            }
            {
                VarDeclaration vd = onemember.isVarDeclaration();
                if ((vd) != null)
                {
                    if (d.constraint.value != null)
                        return false;
                    if (stcToBuffer(this.buf.value, vd.storage_class.value))
                        (this.buf.value.get()).writeByte(32);
                    if (vd.type.value != null)
                        typeToBuffer(vd.type.value, vd.ident.value, this.buf.value, this.hgs.value);
                    else
                        (this.buf.value.get()).writestring(vd.ident.value.asString());
                    (this.buf.value.get()).writeByte(40);
                    this.visitTemplateParameters((this.hgs.value.get()).ddoc.value ? d.origParameters.value : d.parameters);
                    (this.buf.value.get()).writeByte(41);
                    if (vd._init.value != null)
                    {
                        (this.buf.value.get()).writestring(new ByteSlice(" = "));
                        ExpInitializer ie = vd._init.value.isExpInitializer();
                        if ((ie != null) && ((ie.exp.value.op.value & 0xFF) == 95) || ((ie.exp.value.op.value & 0xFF) == 96))
                            expressionToBuffer(((AssignExp)ie.exp.value).e2.value, this.buf.value, this.hgs.value);
                        else
                            initializerToBuffer(vd._init.value, this.buf.value, this.hgs.value);
                    }
                    (this.buf.value.get()).writeByte(59);
                    (this.buf.value.get()).writenl();
                    return true;
                }
            }
            return false;
        }

        public  void visitTemplateParameters(Ptr<DArray<TemplateParameter>> parameters) {
            if ((parameters == null) || ((parameters.get()).length.value == 0))
                return ;
            {
                Slice<TemplateParameter> __r1450 = (parameters.get()).opSlice().copy();
                int __key1449 = 0;
                for (; (__key1449 < __r1450.getLength());__key1449 += 1) {
                    TemplateParameter p = __r1450.get(__key1449);
                    int i = __key1449;
                    if (i != 0)
                        (this.buf.value.get()).writestring(new ByteSlice(", "));
                    templateParameterToBuffer(p, this.buf.value, this.hgs.value);
                }
            }
        }

        public  void visitTemplateConstraint(Expression constraint) {
            if (constraint == null)
                return ;
            (this.buf.value.get()).writestring(new ByteSlice(" if ("));
            expressionToBuffer(constraint, this.buf.value, this.hgs.value);
            (this.buf.value.get()).writeByte(41);
        }

        public  void visit(TemplateInstance ti) {
            (this.buf.value.get()).writestring(ti.name.value.toChars());
            tiargsToBuffer(ti, this.buf.value, this.hgs.value);
            if ((this.hgs.value.get()).fullDump)
            {
                (this.buf.value.get()).writenl();
                dumpTemplateInstance(ti, this.buf.value, this.hgs.value);
            }
        }

        public  void visit(TemplateMixin tm) {
            (this.buf.value.get()).writestring(new ByteSlice("mixin "));
            typeToBuffer(tm.tqual, null, this.buf.value, this.hgs.value);
            tiargsToBuffer(tm, this.buf.value, this.hgs.value);
            if ((tm.ident.value != null) && (memcmp(tm.ident.value.toChars(), new BytePtr("__mixin"), 7) != 0))
            {
                (this.buf.value.get()).writeByte(32);
                (this.buf.value.get()).writestring(tm.ident.value.asString());
            }
            (this.buf.value.get()).writeByte(59);
            (this.buf.value.get()).writenl();
            if ((this.hgs.value.get()).fullDump)
                dumpTemplateInstance(tm, this.buf.value, this.hgs.value);
        }

        public  void visit(EnumDeclaration d) {
            EnumDeclaration oldInEnumDecl = (this.hgs.value.get()).inEnumDecl;
            try {
                (this.hgs.value.get()).inEnumDecl = d;
                (this.buf.value.get()).writestring(new ByteSlice("enum "));
                if (d.ident.value != null)
                {
                    (this.buf.value.get()).writestring(d.ident.value.asString());
                    (this.buf.value.get()).writeByte(32);
                }
                if (d.memtype.value != null)
                {
                    (this.buf.value.get()).writestring(new ByteSlice(": "));
                    typeToBuffer(d.memtype.value, null, this.buf.value, this.hgs.value);
                }
                if (d.members.value == null)
                {
                    (this.buf.value.get()).writeByte(59);
                    (this.buf.value.get()).writenl();
                    return ;
                }
                (this.buf.value.get()).writenl();
                (this.buf.value.get()).writeByte(123);
                (this.buf.value.get()).writenl();
                (this.buf.value.get()).level++;
                {
                    Slice<Dsymbol> __r1451 = (d.members.value.get()).opSlice().copy();
                    int __key1452 = 0;
                    for (; (__key1452 < __r1451.getLength());__key1452 += 1) {
                        Dsymbol em = __r1451.get(__key1452);
                        if (em == null)
                            continue;
                        em.accept(this);
                        (this.buf.value.get()).writeByte(44);
                        (this.buf.value.get()).writenl();
                    }
                }
                (this.buf.value.get()).level--;
                (this.buf.value.get()).writeByte(125);
                (this.buf.value.get()).writenl();
            }
            finally {
                (this.hgs.value.get()).inEnumDecl = oldInEnumDecl;
            }
        }

        public  void visit(Nspace d) {
            (this.buf.value.get()).writestring(new ByteSlice("extern (C++, "));
            (this.buf.value.get()).writestring(d.ident.value.asString());
            (this.buf.value.get()).writeByte(41);
            (this.buf.value.get()).writenl();
            (this.buf.value.get()).writeByte(123);
            (this.buf.value.get()).writenl();
            (this.buf.value.get()).level++;
            {
                Slice<Dsymbol> __r1453 = (d.members.value.get()).opSlice().copy();
                int __key1454 = 0;
                for (; (__key1454 < __r1453.getLength());__key1454 += 1) {
                    Dsymbol s = __r1453.get(__key1454);
                    s.accept(this);
                }
            }
            (this.buf.value.get()).level--;
            (this.buf.value.get()).writeByte(125);
            (this.buf.value.get()).writenl();
        }

        public  void visit(StructDeclaration d) {
            (this.buf.value.get()).writestring(d.kind());
            (this.buf.value.get()).writeByte(32);
            if (!d.isAnonymous())
                (this.buf.value.get()).writestring(d.toChars());
            if (d.members.value == null)
            {
                (this.buf.value.get()).writeByte(59);
                (this.buf.value.get()).writenl();
                return ;
            }
            (this.buf.value.get()).writenl();
            (this.buf.value.get()).writeByte(123);
            (this.buf.value.get()).writenl();
            (this.buf.value.get()).level++;
            {
                Slice<Dsymbol> __r1455 = (d.members.value.get()).opSlice().copy();
                int __key1456 = 0;
                for (; (__key1456 < __r1455.getLength());__key1456 += 1) {
                    Dsymbol s = __r1455.get(__key1456);
                    s.accept(this);
                }
            }
            (this.buf.value.get()).level--;
            (this.buf.value.get()).writeByte(125);
            (this.buf.value.get()).writenl();
        }

        public  void visit(ClassDeclaration d) {
            if (!d.isAnonymous())
            {
                (this.buf.value.get()).writestring(d.kind());
                (this.buf.value.get()).writeByte(32);
                (this.buf.value.get()).writestring(d.ident.value.asString());
            }
            this.visitBaseClasses(d);
            if (d.members.value != null)
            {
                (this.buf.value.get()).writenl();
                (this.buf.value.get()).writeByte(123);
                (this.buf.value.get()).writenl();
                (this.buf.value.get()).level++;
                {
                    Slice<Dsymbol> __r1457 = (d.members.value.get()).opSlice().copy();
                    int __key1458 = 0;
                    for (; (__key1458 < __r1457.getLength());__key1458 += 1) {
                        Dsymbol s = __r1457.get(__key1458);
                        s.accept(this);
                    }
                }
                (this.buf.value.get()).level--;
                (this.buf.value.get()).writeByte(125);
            }
            else
                (this.buf.value.get()).writeByte(59);
            (this.buf.value.get()).writenl();
        }

        public  void visitBaseClasses(ClassDeclaration d) {
            if ((d == null) || ((d.baseclasses.get()).length.value == 0))
                return ;
            if (!d.isAnonymous())
                (this.buf.value.get()).writestring(new ByteSlice(" : "));
            {
                Slice<Ptr<BaseClass>> __r1460 = (d.baseclasses.get()).opSlice().copy();
                int __key1459 = 0;
                for (; (__key1459 < __r1460.getLength());__key1459 += 1) {
                    Ptr<BaseClass> b = __r1460.get(__key1459);
                    int i = __key1459;
                    if (i != 0)
                        (this.buf.value.get()).writestring(new ByteSlice(", "));
                    typeToBuffer((b.get()).type.value, null, this.buf.value, this.hgs.value);
                }
            }
        }

        public  void visit(AliasDeclaration d) {
            if ((d.storage_class.value & 2251799813685248L) != 0)
                return ;
            (this.buf.value.get()).writestring(new ByteSlice("alias "));
            if (d.aliassym.value != null)
            {
                (this.buf.value.get()).writestring(d.ident.value.asString());
                (this.buf.value.get()).writestring(new ByteSlice(" = "));
                if (stcToBuffer(this.buf.value, d.storage_class.value))
                    (this.buf.value.get()).writeByte(32);
                d.aliassym.value.accept(this);
            }
            else if (((d.type.value.ty.value & 0xFF) == ENUMTY.Tfunction))
            {
                if (stcToBuffer(this.buf.value, d.storage_class.value))
                    (this.buf.value.get()).writeByte(32);
                typeToBuffer(d.type.value, d.ident.value, this.buf.value, this.hgs.value);
            }
            else if (d.ident.value != null)
            {
                (this.hgs.value.get()).declstring.value = (pequals(d.ident.value, Id.string)) || (pequals(d.ident.value, Id.wstring)) || (pequals(d.ident.value, Id.dstring));
                (this.buf.value.get()).writestring(d.ident.value.asString());
                (this.buf.value.get()).writestring(new ByteSlice(" = "));
                if (stcToBuffer(this.buf.value, d.storage_class.value))
                    (this.buf.value.get()).writeByte(32);
                typeToBuffer(d.type.value, null, this.buf.value, this.hgs.value);
                (this.hgs.value.get()).declstring.value = false;
            }
            (this.buf.value.get()).writeByte(59);
            (this.buf.value.get()).writenl();
        }

        public  void visit(VarDeclaration d) {
            if ((d.storage_class.value & 2251799813685248L) != 0)
                return ;
            this.visitVarDecl(d, false);
            (this.buf.value.get()).writeByte(59);
            (this.buf.value.get()).writenl();
        }

        public  void visitVarDecl(VarDeclaration v, boolean anywritten) {
            if (anywritten)
            {
                (this.buf.value.get()).writestring(new ByteSlice(", "));
                (this.buf.value.get()).writestring(v.ident.value.asString());
            }
            else
            {
                if (stcToBuffer(this.buf.value, v.storage_class.value))
                    (this.buf.value.get()).writeByte(32);
                if (v.type.value != null)
                    typeToBuffer(v.type.value, v.ident.value, this.buf.value, this.hgs.value);
                else
                    (this.buf.value.get()).writestring(v.ident.value.asString());
            }
            if (v._init.value != null)
            {
                (this.buf.value.get()).writestring(new ByteSlice(" = "));
                ExpInitializer ie = v._init.value.isExpInitializer();
                if ((ie != null) && ((ie.exp.value.op.value & 0xFF) == 95) || ((ie.exp.value.op.value & 0xFF) == 96))
                    expressionToBuffer(((AssignExp)ie.exp.value).e2.value, this.buf.value, this.hgs.value);
                else
                    initializerToBuffer(v._init.value, this.buf.value, this.hgs.value);
            }
        }

        public  void visit(FuncDeclaration f) {
            if (stcToBuffer(this.buf.value, f.storage_class.value))
                (this.buf.value.get()).writeByte(32);
            TypeFunction tf = (TypeFunction)f.type.value;
            typeToBuffer(tf, f.ident.value, this.buf.value, this.hgs.value);
            if ((this.hgs.value.get()).hdrgen)
            {
                if ((tf.next.value == null) || ((f.storage_class.value & 256L) != 0))
                {
                    (this.hgs.value.get()).autoMember++;
                    this.bodyToBuffer(f);
                    (this.hgs.value.get()).autoMember--;
                }
                else if (((this.hgs.value.get()).tpltMember == 0) && global.params.hdrStripPlainFunctions)
                {
                    (this.buf.value.get()).writeByte(59);
                    (this.buf.value.get()).writenl();
                }
                else
                    this.bodyToBuffer(f);
            }
            else
                this.bodyToBuffer(f);
        }

        public  void bodyToBuffer(FuncDeclaration f) {
            if ((f.fbody.value == null) || (this.hgs.value.get()).hdrgen && global.params.hdrStripPlainFunctions && ((this.hgs.value.get()).autoMember == 0) && ((this.hgs.value.get()).tpltMember == 0))
            {
                (this.buf.value.get()).writeByte(59);
                (this.buf.value.get()).writenl();
                return ;
            }
            int savetlpt = (this.hgs.value.get()).tpltMember;
            int saveauto = (this.hgs.value.get()).autoMember;
            (this.hgs.value.get()).tpltMember = 0;
            (this.hgs.value.get()).autoMember = 0;
            (this.buf.value.get()).writenl();
            boolean requireDo = false;
            if (f.frequires != null)
            {
                {
                    Slice<Statement> __r1461 = (f.frequires.get()).opSlice().copy();
                    int __key1462 = 0;
                    for (; (__key1462 < __r1461.getLength());__key1462 += 1) {
                        Statement frequire = __r1461.get(__key1462);
                        (this.buf.value.get()).writestring(new ByteSlice("in"));
                        {
                            ExpStatement es = frequire.isExpStatement();
                            if ((es) != null)
                            {
                                assert((es.exp.value != null) && ((es.exp.value.op.value & 0xFF) == 14));
                                (this.buf.value.get()).writestring(new ByteSlice(" ("));
                                expressionToBuffer(((AssertExp)es.exp.value).e1.value, this.buf.value, this.hgs.value);
                                (this.buf.value.get()).writeByte(41);
                                (this.buf.value.get()).writenl();
                                requireDo = false;
                            }
                            else
                            {
                                (this.buf.value.get()).writenl();
                                statementToBuffer(frequire, this.buf.value, this.hgs.value);
                                requireDo = true;
                            }
                        }
                    }
                }
            }
            if (f.fensures != null)
            {
                {
                    Slice<Ensure> __r1463 = (f.fensures.get()).opSlice().copy();
                    int __key1464 = 0;
                    for (; (__key1464 < __r1463.getLength());__key1464 += 1) {
                        Ensure fensure = __r1463.get(__key1464).copy();
                        (this.buf.value.get()).writestring(new ByteSlice("out"));
                        {
                            ExpStatement es = fensure.ensure.isExpStatement();
                            if ((es) != null)
                            {
                                assert((es.exp.value != null) && ((es.exp.value.op.value & 0xFF) == 14));
                                (this.buf.value.get()).writestring(new ByteSlice(" ("));
                                if (fensure.id != null)
                                {
                                    (this.buf.value.get()).writestring(fensure.id.asString());
                                }
                                (this.buf.value.get()).writestring(new ByteSlice("; "));
                                expressionToBuffer(((AssertExp)es.exp.value).e1.value, this.buf.value, this.hgs.value);
                                (this.buf.value.get()).writeByte(41);
                                (this.buf.value.get()).writenl();
                                requireDo = false;
                            }
                            else
                            {
                                if (fensure.id != null)
                                {
                                    (this.buf.value.get()).writeByte(40);
                                    (this.buf.value.get()).writestring(fensure.id.asString());
                                    (this.buf.value.get()).writeByte(41);
                                }
                                (this.buf.value.get()).writenl();
                                statementToBuffer(fensure.ensure, this.buf.value, this.hgs.value);
                                requireDo = true;
                            }
                        }
                    }
                }
            }
            if (requireDo)
            {
                (this.buf.value.get()).writestring(new ByteSlice("do"));
                (this.buf.value.get()).writenl();
            }
            (this.buf.value.get()).writeByte(123);
            (this.buf.value.get()).writenl();
            (this.buf.value.get()).level++;
            statementToBuffer(f.fbody.value, this.buf.value, this.hgs.value);
            (this.buf.value.get()).level--;
            (this.buf.value.get()).writeByte(125);
            (this.buf.value.get()).writenl();
            (this.hgs.value.get()).tpltMember = savetlpt;
            (this.hgs.value.get()).autoMember = saveauto;
        }

        public  void visit(FuncLiteralDeclaration f) {
            if (((f.type.value.ty.value & 0xFF) == ENUMTY.Terror))
            {
                (this.buf.value.get()).writestring(new ByteSlice("__error"));
                return ;
            }
            if (((f.tok.value & 0xFF) != 0))
            {
                (this.buf.value.get()).writestring(f.kind());
                (this.buf.value.get()).writeByte(32);
            }
            TypeFunction tf = (TypeFunction)f.type.value;
            if (!f.inferRetType && (tf.next.value != null))
                typeToBuffer(tf.next.value, null, this.buf.value, this.hgs.value);
            parametersToBuffer(tf.parameterList, this.buf.value, this.hgs.value);
            CompoundStatement cs = f.fbody.value.isCompoundStatement();
            Statement s1 = null;
            if ((f.semanticRun.value >= PASS.semantic3done) && (cs != null))
            {
                s1 = (cs.statements.get()).get((cs.statements.get()).length.value - 1);
            }
            else
                s1 = cs == null ? f.fbody.value : null;
            ReturnStatement rs = s1 != null ? s1.isReturnStatement() : null;
            if ((rs != null) && (rs.exp.value != null))
            {
                (this.buf.value.get()).writestring(new ByteSlice(" => "));
                expressionToBuffer(rs.exp.value, this.buf.value, this.hgs.value);
            }
            else
            {
                (this.hgs.value.get()).tpltMember++;
                this.bodyToBuffer(f);
                (this.hgs.value.get()).tpltMember--;
            }
        }

        public  void visit(PostBlitDeclaration d) {
            if (stcToBuffer(this.buf.value, d.storage_class.value))
                (this.buf.value.get()).writeByte(32);
            (this.buf.value.get()).writestring(new ByteSlice("this(this)"));
            this.bodyToBuffer(d);
        }

        public  void visit(DtorDeclaration d) {
            if ((d.storage_class.value & 17179869184L) != 0)
                (this.buf.value.get()).writestring(new ByteSlice("@trusted "));
            if ((d.storage_class.value & 8589934592L) != 0)
                (this.buf.value.get()).writestring(new ByteSlice("@safe "));
            if ((d.storage_class.value & 4398046511104L) != 0)
                (this.buf.value.get()).writestring(new ByteSlice("@nogc "));
            if ((d.storage_class.value & 137438953472L) != 0)
                (this.buf.value.get()).writestring(new ByteSlice("@disable "));
            (this.buf.value.get()).writestring(new ByteSlice("~this()"));
            this.bodyToBuffer(d);
        }

        public  void visit(StaticCtorDeclaration d) {
            if (stcToBuffer(this.buf.value, d.storage_class.value & -2L))
                (this.buf.value.get()).writeByte(32);
            if (d.isSharedStaticCtorDeclaration() != null)
                (this.buf.value.get()).writestring(new ByteSlice("shared "));
            (this.buf.value.get()).writestring(new ByteSlice("static this()"));
            if ((this.hgs.value.get()).hdrgen && ((this.hgs.value.get()).tpltMember == 0))
            {
                (this.buf.value.get()).writeByte(59);
                (this.buf.value.get()).writenl();
            }
            else
                this.bodyToBuffer(d);
        }

        public  void visit(StaticDtorDeclaration d) {
            if (stcToBuffer(this.buf.value, d.storage_class.value & -2L))
                (this.buf.value.get()).writeByte(32);
            if (d.isSharedStaticDtorDeclaration() != null)
                (this.buf.value.get()).writestring(new ByteSlice("shared "));
            (this.buf.value.get()).writestring(new ByteSlice("static ~this()"));
            if ((this.hgs.value.get()).hdrgen && ((this.hgs.value.get()).tpltMember == 0))
            {
                (this.buf.value.get()).writeByte(59);
                (this.buf.value.get()).writenl();
            }
            else
                this.bodyToBuffer(d);
        }

        public  void visit(InvariantDeclaration d) {
            if ((this.hgs.value.get()).hdrgen)
                return ;
            if (stcToBuffer(this.buf.value, d.storage_class.value))
                (this.buf.value.get()).writeByte(32);
            (this.buf.value.get()).writestring(new ByteSlice("invariant"));
            {
                ExpStatement es = d.fbody.value.isExpStatement();
                if ((es) != null)
                {
                    assert((es.exp.value != null) && ((es.exp.value.op.value & 0xFF) == 14));
                    (this.buf.value.get()).writestring(new ByteSlice(" ("));
                    expressionToBuffer(((AssertExp)es.exp.value).e1.value, this.buf.value, this.hgs.value);
                    (this.buf.value.get()).writestring(new ByteSlice(");"));
                    (this.buf.value.get()).writenl();
                }
                else
                {
                    this.bodyToBuffer(d);
                }
            }
        }

        public  void visit(UnitTestDeclaration d) {
            if ((this.hgs.value.get()).hdrgen)
                return ;
            if (stcToBuffer(this.buf.value, d.storage_class.value))
                (this.buf.value.get()).writeByte(32);
            (this.buf.value.get()).writestring(new ByteSlice("unittest"));
            this.bodyToBuffer(d);
        }

        public  void visit(NewDeclaration d) {
            if (stcToBuffer(this.buf.value, d.storage_class.value & -2L))
                (this.buf.value.get()).writeByte(32);
            (this.buf.value.get()).writestring(new ByteSlice("new"));
            parametersToBuffer(new ParameterList(d.parameters, d.varargs), this.buf.value, this.hgs.value);
            this.bodyToBuffer(d);
        }

        public  void visit(DeleteDeclaration d) {
            if (stcToBuffer(this.buf.value, d.storage_class.value & -2L))
                (this.buf.value.get()).writeByte(32);
            (this.buf.value.get()).writestring(new ByteSlice("delete"));
            parametersToBuffer(new ParameterList(d.parameters, VarArg.none), this.buf.value, this.hgs.value);
            this.bodyToBuffer(d);
        }

        public  void visit(dmodule.Module m) {
            moduleToBuffer2(m, this.buf.value, this.hgs.value);
        }


        public DsymbolPrettyPrintVisitor() {}

        public DsymbolPrettyPrintVisitor copy() {
            DsymbolPrettyPrintVisitor that = new DsymbolPrettyPrintVisitor();
            that.buf = this.buf;
            that.hgs = this.hgs;
            return that;
        }
    }
    public static class ExpressionPrettyPrintVisitor extends Visitor
    {
        public Ptr<OutBuffer> buf = null;
        public Ptr<HdrGenState> hgs = null;
        public  ExpressionPrettyPrintVisitor(Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
            this.buf = buf;
            this.hgs = hgs;
        }

        public  void visit(Expression e) {
            (this.buf.get()).writestring(Token.asString(e.op.value));
        }

        public  void visit(IntegerExp e) {
            long v = e.toInteger();
            if (e.type.value != null)
            {
                Type t = e.type.value;
            /*L1:*/
                {
                    int __dispatch1 = 0;
                    dispatched_1:
                    do {
                        switch (__dispatch1 != 0 ? __dispatch1 : (t.ty.value & 0xFF))
                        {
                            case 9:
                                TypeEnum te = (TypeEnum)t;
                                if ((this.hgs.get()).fullDump)
                                {
                                    EnumDeclaration sym = te.sym.value;
                                    if ((!pequals((this.hgs.get()).inEnumDecl, sym)))
                                    {
                                        int __key1465 = 0;
                                        int __limit1466 = (sym.members.value.get()).length.value;
                                        for (; (__key1465 < __limit1466);__key1465 += 1) {
                                            int i = __key1465;
                                            EnumMember em = (EnumMember)(sym.members.value.get()).get(i);
                                            if ((em.value().toInteger() == v))
                                            {
                                                (this.buf.get()).printf(new BytePtr("%s.%s"), sym.toChars(), em.ident.value.toChars());
                                                return ;
                                            }
                                        }
                                    }
                                }
                                (this.buf.get()).printf(new BytePtr("cast(%s)"), te.sym.value.toChars());
                                t = te.sym.value.memtype.value;
                                /*goto L1*/throw Dispatch0.INSTANCE;
                            case 32:
                            case 33:
                                if ((v > 255L))
                                {
                                    (this.buf.get()).printf(new BytePtr("'\\U%08x'"), v);
                                    break;
                                }
                            case 31:
                                int o = (this.buf.get()).offset.value;
                                if ((v == 39L))
                                    (this.buf.get()).writestring(new ByteSlice("'\\''"));
                                else if ((isprint((int)v) != 0) && (v != 92L))
                                    (this.buf.get()).printf(new BytePtr("'%c'"), (int)v);
                                else
                                    (this.buf.get()).printf(new BytePtr("'\\x%02x'"), (int)v);
                                if ((this.hgs.get()).ddoc.value)
                                    escapeDdocString(this.buf, o);
                                break;
                            case 13:
                                (this.buf.get()).writestring(new ByteSlice("cast(byte)"));
                                /*goto L2*/{ __dispatch1 = -1; continue dispatched_1; }
                            case 15:
                                (this.buf.get()).writestring(new ByteSlice("cast(short)"));
                                /*goto L2*/{ __dispatch1 = -1; continue dispatched_1; }
                            case 17:
                            /*L2:*/
                            case -1:
                            __dispatch1 = 0;
                                (this.buf.get()).printf(new BytePtr("%d"), (int)v);
                                break;
                            case 14:
                                (this.buf.get()).writestring(new ByteSlice("cast(ubyte)"));
                                /*goto case*/{ __dispatch1 = 18; continue dispatched_1; }
                            case 16:
                                (this.buf.get()).writestring(new ByteSlice("cast(ushort)"));
                                /*goto case*/{ __dispatch1 = 18; continue dispatched_1; }
                            case 18:
                                __dispatch1 = 0;
                                (this.buf.get()).printf(new BytePtr("%uu"), (int)v);
                                break;
                            case 19:
                                (this.buf.get()).printf(new BytePtr("%lldL"), v);
                                break;
                            case 20:
                                __dispatch1 = 0;
                                (this.buf.get()).printf(new BytePtr("%lluLU"), v);
                                break;
                            case 30:
                                (this.buf.get()).writestring(v != 0 ? new ByteSlice("true") : new ByteSlice("false"));
                                break;
                            case 3:
                                (this.buf.get()).writestring(new ByteSlice("cast("));
                                (this.buf.get()).writestring(t.toChars());
                                (this.buf.get()).writeByte(41);
                                if ((target.ptrsize.value == 8))
                                    /*goto case*/{ __dispatch1 = 20; continue dispatched_1; }
                                else
                                    /*goto case*/{ __dispatch1 = 18; continue dispatched_1; }
                            default:
                            if (global.errors.value == 0)
                            {
                                throw new AssertionError("Unreachable code!");
                            }
                            break;
                        }
                    } while(__dispatch1 != 0);
                }
            }
            else if ((v & -9223372036854775808L) != 0)
                (this.buf.get()).printf(new BytePtr("0x%llx"), v);
            else
                (this.buf.get()).print(v);
        }

        public  void visit(ErrorExp e) {
            (this.buf.get()).writestring(new ByteSlice("__error"));
        }

        public  void visit(VoidInitExp e) {
            (this.buf.get()).writestring(new ByteSlice("__void"));
        }

        public  void floatToBuffer(Type type, double value) {
            int BUFFER_LEN = 58;
            ByteSlice buffer = (byte)255;
            CTFloat.sprint(ptr(buffer), (byte)103, value);
            assert((strlen(ptr(buffer)) < 58));
            if ((this.hgs.get()).hdrgen)
            {
                double r = CTFloat.parse(ptr(buffer), null);
                if ((r != value))
                    CTFloat.sprint(ptr(buffer), (byte)97, value);
            }
            (this.buf.get()).writestring(ptr(buffer));
            if (type != null)
            {
                Type t = type.toBasetype();
                switch ((t.ty.value & 0xFF))
                {
                    case 21:
                    case 24:
                    case 27:
                        (this.buf.get()).writeByte(70);
                        break;
                    case 23:
                    case 26:
                    case 29:
                        (this.buf.get()).writeByte(76);
                        break;
                    default:
                    break;
                }
                if (t.isimaginary())
                    (this.buf.get()).writeByte(105);
            }
        }

        public  void visit(RealExp e) {
            this.floatToBuffer(e.type.value, e.value);
        }

        public  void visit(ComplexExp e) {
            (this.buf.get()).writeByte(40);
            this.floatToBuffer(e.type.value, creall(e.value));
            (this.buf.get()).writeByte(43);
            this.floatToBuffer(e.type.value, cimagl(e.value));
            (this.buf.get()).writestring(new ByteSlice("i)"));
        }

        public  void visit(IdentifierExp e) {
            if ((this.hgs.get()).hdrgen || (this.hgs.get()).ddoc.value)
                (this.buf.get()).writestring(e.ident.value.toHChars2());
            else
                (this.buf.get()).writestring(e.ident.value.asString());
        }

        public  void visit(DsymbolExp e) {
            (this.buf.get()).writestring(e.s.value.toChars());
        }

        public  void visit(ThisExp e) {
            (this.buf.get()).writestring(new ByteSlice("this"));
        }

        public  void visit(SuperExp e) {
            (this.buf.get()).writestring(new ByteSlice("super"));
        }

        public  void visit(NullExp e) {
            (this.buf.get()).writestring(new ByteSlice("null"));
        }

        public  void visit(StringExp e) {
            (this.buf.get()).writeByte(34);
            int o = (this.buf.get()).offset.value;
            {
                int i = 0;
            L_outer1:
                for (; (i < e.len.value);i++){
                    int c = e.charAt((long)i);
                    {
                        int __dispatch3 = 0;
                        dispatched_3:
                        do {
                            switch (__dispatch3 != 0 ? __dispatch3 : c)
                            {
                                case 34:
                                case 92:
                                    (this.buf.get()).writeByte(92);
                                    /*goto default*/ { __dispatch3 = -1; continue dispatched_3; }
                                default:
                                __dispatch3 = 0;
                                if ((c <= 255))
                                {
                                    if ((c <= 127) && (isprint(c) != 0))
                                        (this.buf.get()).writeByte(c);
                                    else
                                        (this.buf.get()).printf(new BytePtr("\\x%02x"), c);
                                }
                                else if ((c <= 65535))
                                    (this.buf.get()).printf(new BytePtr("\\x%02x\\x%02x"), c & 255, c >> 8);
                                else
                                    (this.buf.get()).printf(new BytePtr("\\x%02x\\x%02x\\x%02x\\x%02x"), c & 255, c >> 8 & 255, c >> 16 & 255, c >> 24);
                                break;
                            }
                        } while(__dispatch3 != 0);
                    }
                }
            }
            if ((this.hgs.get()).ddoc.value)
                escapeDdocString(this.buf, o);
            (this.buf.get()).writeByte(34);
            if (e.postfix.value != 0)
                (this.buf.get()).writeByte((e.postfix.value & 0xFF));
        }

        public  void visit(ArrayLiteralExp e) {
            (this.buf.get()).writeByte(91);
            argsToBuffer(e.elements.value, this.buf, this.hgs, e.basis.value);
            (this.buf.get()).writeByte(93);
        }

        public  void visit(AssocArrayLiteralExp e) {
            (this.buf.get()).writeByte(91);
            {
                Slice<Expression> __r1468 = (e.keys.value.get()).opSlice().copy();
                int __key1467 = 0;
                for (; (__key1467 < __r1468.getLength());__key1467 += 1) {
                    Expression key = __r1468.get(__key1467);
                    int i = __key1467;
                    if (i != 0)
                        (this.buf.get()).writestring(new ByteSlice(", "));
                    expToBuffer(key, PREC.assign, this.buf, this.hgs);
                    (this.buf.get()).writeByte(58);
                    Expression value = (e.values.value.get()).get(i);
                    expToBuffer(value, PREC.assign, this.buf, this.hgs);
                }
            }
            (this.buf.get()).writeByte(93);
        }

        public  void visit(StructLiteralExp e) {
            (this.buf.get()).writestring(e.sd.toChars());
            (this.buf.get()).writeByte(40);
            if ((e.stageflags.value & 32) != 0)
                (this.buf.get()).writestring(new ByteSlice("<recursion>"));
            else
            {
                int old = e.stageflags.value;
                e.stageflags.value |= 32;
                argsToBuffer(e.elements.value, this.buf, this.hgs, null);
                e.stageflags.value = old;
            }
            (this.buf.get()).writeByte(41);
        }

        public  void visit(TypeExp e) {
            typeToBuffer(e.type.value, null, this.buf, this.hgs);
        }

        public  void visit(ScopeExp e) {
            if (e.sds.value.isTemplateInstance() != null)
            {
                dsymbolToBuffer(e.sds.value, this.buf, this.hgs);
            }
            else if ((this.hgs != null) && (this.hgs.get()).ddoc.value)
            {
                {
                    dmodule.Module m = e.sds.value.isModule();
                    if ((m) != null)
                        (this.buf.get()).writestring((m.md.get()).toChars());
                    else
                        (this.buf.get()).writestring(e.sds.value.toChars());
                }
            }
            else
            {
                (this.buf.get()).writestring(e.sds.value.kind());
                (this.buf.get()).writeByte(32);
                (this.buf.get()).writestring(e.sds.value.toChars());
            }
        }

        public  void visit(TemplateExp e) {
            (this.buf.get()).writestring(e.td.value.toChars());
        }

        public  void visit(NewExp e) {
            if (e.thisexp.value != null)
            {
                expToBuffer(e.thisexp.value, PREC.primary, this.buf, this.hgs);
                (this.buf.get()).writeByte(46);
            }
            (this.buf.get()).writestring(new ByteSlice("new "));
            if ((e.newargs.value != null) && ((e.newargs.value.get()).length.value != 0))
            {
                (this.buf.get()).writeByte(40);
                argsToBuffer(e.newargs.value, this.buf, this.hgs, null);
                (this.buf.get()).writeByte(41);
            }
            typeToBuffer(e.newtype.value, null, this.buf, this.hgs);
            if ((e.arguments.value != null) && ((e.arguments.value.get()).length.value != 0))
            {
                (this.buf.get()).writeByte(40);
                argsToBuffer(e.arguments.value, this.buf, this.hgs, null);
                (this.buf.get()).writeByte(41);
            }
        }

        public  void visit(NewAnonClassExp e) {
            if (e.thisexp != null)
            {
                expToBuffer(e.thisexp, PREC.primary, this.buf, this.hgs);
                (this.buf.get()).writeByte(46);
            }
            (this.buf.get()).writestring(new ByteSlice("new"));
            if ((e.newargs != null) && ((e.newargs.get()).length.value != 0))
            {
                (this.buf.get()).writeByte(40);
                argsToBuffer(e.newargs, this.buf, this.hgs, null);
                (this.buf.get()).writeByte(41);
            }
            (this.buf.get()).writestring(new ByteSlice(" class "));
            if ((e.arguments != null) && ((e.arguments.get()).length.value != 0))
            {
                (this.buf.get()).writeByte(40);
                argsToBuffer(e.arguments, this.buf, this.hgs, null);
                (this.buf.get()).writeByte(41);
            }
            if (e.cd != null)
                dsymbolToBuffer(e.cd, this.buf, this.hgs);
        }

        public  void visit(SymOffExp e) {
            if (e.offset.value != 0)
                (this.buf.get()).printf(new BytePtr("(& %s+%u)"), e.var.value.toChars(), e.offset.value);
            else if (e.var.value.isTypeInfoDeclaration() != null)
                (this.buf.get()).writestring(e.var.value.toChars());
            else
                (this.buf.get()).printf(new BytePtr("& %s"), e.var.value.toChars());
        }

        public  void visit(VarExp e) {
            (this.buf.get()).writestring(e.var.value.toChars());
        }

        public  void visit(OverExp e) {
            (this.buf.get()).writestring(e.vars.ident.value.asString());
        }

        public  void visit(TupleExp e) {
            if (e.e0.value != null)
            {
                (this.buf.get()).writeByte(40);
                e.e0.value.accept(this);
                (this.buf.get()).writestring(new ByteSlice(", tuple("));
                argsToBuffer(e.exps.value, this.buf, this.hgs, null);
                (this.buf.get()).writestring(new ByteSlice("))"));
            }
            else
            {
                (this.buf.get()).writestring(new ByteSlice("tuple("));
                argsToBuffer(e.exps.value, this.buf, this.hgs, null);
                (this.buf.get()).writeByte(41);
            }
        }

        public  void visit(FuncExp e) {
            dsymbolToBuffer(e.fd.value, this.buf, this.hgs);
        }

        public  void visit(DeclarationExp e) {
            if (e.declaration.value != null)
            {
                {
                    VarDeclaration var = e.declaration.value.isVarDeclaration();
                    if ((var) != null)
                    {
                        (this.buf.get()).writeByte(40);
                        DsymbolPrettyPrintVisitor v = new DsymbolPrettyPrintVisitor(this.buf, this.hgs);
                        v.visitVarDecl(var, false);
                        (this.buf.get()).writeByte(59);
                        (this.buf.get()).writeByte(41);
                    }
                    else
                        dsymbolToBuffer(e.declaration.value, this.buf, this.hgs);
                }
            }
        }

        public  void visit(TypeidExp e) {
            (this.buf.get()).writestring(new ByteSlice("typeid("));
            objectToBuffer(e.obj.value, this.buf, this.hgs);
            (this.buf.get()).writeByte(41);
        }

        public  void visit(TraitsExp e) {
            (this.buf.get()).writestring(new ByteSlice("__traits("));
            if (e.ident.value != null)
                (this.buf.get()).writestring(e.ident.value.asString());
            if (e.args.value != null)
            {
                {
                    Slice<RootObject> __r1469 = (e.args.value.get()).opSlice().copy();
                    int __key1470 = 0;
                    for (; (__key1470 < __r1469.getLength());__key1470 += 1) {
                        RootObject arg = __r1469.get(__key1470);
                        (this.buf.get()).writestring(new ByteSlice(", "));
                        objectToBuffer(arg, this.buf, this.hgs);
                    }
                }
            }
            (this.buf.get()).writeByte(41);
        }

        public  void visit(HaltExp e) {
            (this.buf.get()).writestring(new ByteSlice("halt"));
        }

        public  void visit(IsExp e) {
            (this.buf.get()).writestring(new ByteSlice("is("));
            typeToBuffer(e.targ.value, e.id, this.buf, this.hgs);
            if (((e.tok2 & 0xFF) != 0))
            {
                (this.buf.get()).printf(new BytePtr(" %s %s"), Token.toChars(e.tok), Token.toChars(e.tok2));
            }
            else if (e.tspec != null)
            {
                if (((e.tok & 0xFF) == 7))
                    (this.buf.get()).writestring(new ByteSlice(" : "));
                else
                    (this.buf.get()).writestring(new ByteSlice(" == "));
                typeToBuffer(e.tspec, null, this.buf, this.hgs);
            }
            if ((e.parameters != null) && ((e.parameters.get()).length.value != 0))
            {
                (this.buf.get()).writestring(new ByteSlice(", "));
                DsymbolPrettyPrintVisitor v = new DsymbolPrettyPrintVisitor(this.buf, this.hgs);
                v.visitTemplateParameters(e.parameters);
            }
            (this.buf.get()).writeByte(41);
        }

        public  void visit(UnaExp e) {
            (this.buf.get()).writestring(Token.asString(e.op.value));
            expToBuffer(e.e1.value, precedence.get((e.op.value & 0xFF)), this.buf, this.hgs);
        }

        public  void visit(BinExp e) {
            expToBuffer(e.e1.value, precedence.get((e.op.value & 0xFF)), this.buf, this.hgs);
            (this.buf.get()).writeByte(32);
            (this.buf.get()).writestring(Token.asString(e.op.value));
            (this.buf.get()).writeByte(32);
            expToBuffer(e.e2.value, precedence.get((e.op.value & 0xFF)) + 1, this.buf, this.hgs);
        }

        public  void visit(CompileExp e) {
            (this.buf.get()).writestring(new ByteSlice("mixin("));
            argsToBuffer(e.exps, this.buf, this.hgs, null);
            (this.buf.get()).writeByte(41);
        }

        public  void visit(ImportExp e) {
            (this.buf.get()).writestring(new ByteSlice("import("));
            expToBuffer(e.e1.value, PREC.assign, this.buf, this.hgs);
            (this.buf.get()).writeByte(41);
        }

        public  void visit(AssertExp e) {
            (this.buf.get()).writestring(new ByteSlice("assert("));
            expToBuffer(e.e1.value, PREC.assign, this.buf, this.hgs);
            if (e.msg != null)
            {
                (this.buf.get()).writestring(new ByteSlice(", "));
                expToBuffer(e.msg, PREC.assign, this.buf, this.hgs);
            }
            (this.buf.get()).writeByte(41);
        }

        public  void visit(DotIdExp e) {
            expToBuffer(e.e1.value, PREC.primary, this.buf, this.hgs);
            (this.buf.get()).writeByte(46);
            (this.buf.get()).writestring(e.ident.asString());
        }

        public  void visit(DotTemplateExp e) {
            expToBuffer(e.e1.value, PREC.primary, this.buf, this.hgs);
            (this.buf.get()).writeByte(46);
            (this.buf.get()).writestring(e.td.value.toChars());
        }

        public  void visit(DotVarExp e) {
            expToBuffer(e.e1.value, PREC.primary, this.buf, this.hgs);
            (this.buf.get()).writeByte(46);
            (this.buf.get()).writestring(e.var.value.toChars());
        }

        public  void visit(DotTemplateInstanceExp e) {
            expToBuffer(e.e1.value, PREC.primary, this.buf, this.hgs);
            (this.buf.get()).writeByte(46);
            dsymbolToBuffer(e.ti, this.buf, this.hgs);
        }

        public  void visit(DelegateExp e) {
            (this.buf.get()).writeByte(38);
            if (!e.func.value.isNested() || e.func.value.needThis())
            {
                expToBuffer(e.e1.value, PREC.primary, this.buf, this.hgs);
                (this.buf.get()).writeByte(46);
            }
            (this.buf.get()).writestring(e.func.value.toChars());
        }

        public  void visit(DotTypeExp e) {
            expToBuffer(e.e1.value, PREC.primary, this.buf, this.hgs);
            (this.buf.get()).writeByte(46);
            (this.buf.get()).writestring(e.sym.value.toChars());
        }

        public  void visit(CallExp e) {
            if (((e.e1.value.op.value & 0xFF) == 20))
            {
                e.e1.value.accept(this);
            }
            else
                expToBuffer(e.e1.value, precedence.get((e.op.value & 0xFF)), this.buf, this.hgs);
            (this.buf.get()).writeByte(40);
            argsToBuffer(e.arguments.value, this.buf, this.hgs, null);
            (this.buf.get()).writeByte(41);
        }

        public  void visit(PtrExp e) {
            (this.buf.get()).writeByte(42);
            expToBuffer(e.e1.value, precedence.get((e.op.value & 0xFF)), this.buf, this.hgs);
        }

        public  void visit(DeleteExp e) {
            (this.buf.get()).writestring(new ByteSlice("delete "));
            expToBuffer(e.e1.value, precedence.get((e.op.value & 0xFF)), this.buf, this.hgs);
        }

        public  void visit(CastExp e) {
            (this.buf.get()).writestring(new ByteSlice("cast("));
            if (e.to.value != null)
                typeToBuffer(e.to.value, null, this.buf, this.hgs);
            else
            {
                MODtoBuffer(this.buf, e.mod);
            }
            (this.buf.get()).writeByte(41);
            expToBuffer(e.e1.value, precedence.get((e.op.value & 0xFF)), this.buf, this.hgs);
        }

        public  void visit(VectorExp e) {
            (this.buf.get()).writestring(new ByteSlice("cast("));
            typeToBuffer(e.to, null, this.buf, this.hgs);
            (this.buf.get()).writeByte(41);
            expToBuffer(e.e1.value, precedence.get((e.op.value & 0xFF)), this.buf, this.hgs);
        }

        public  void visit(VectorArrayExp e) {
            expToBuffer(e.e1.value, PREC.primary, this.buf, this.hgs);
            (this.buf.get()).writestring(new ByteSlice(".array"));
        }

        public  void visit(SliceExp e) {
            expToBuffer(e.e1.value, precedence.get((e.op.value & 0xFF)), this.buf, this.hgs);
            (this.buf.get()).writeByte(91);
            if ((e.upr.value != null) || (e.lwr.value != null))
            {
                if (e.lwr.value != null)
                    sizeToBuffer(e.lwr.value, this.buf, this.hgs);
                else
                    (this.buf.get()).writeByte(48);
                (this.buf.get()).writestring(new ByteSlice(".."));
                if (e.upr.value != null)
                    sizeToBuffer(e.upr.value, this.buf, this.hgs);
                else
                    (this.buf.get()).writeByte(36);
            }
            (this.buf.get()).writeByte(93);
        }

        public  void visit(ArrayLengthExp e) {
            expToBuffer(e.e1.value, PREC.primary, this.buf, this.hgs);
            (this.buf.get()).writestring(new ByteSlice(".length"));
        }

        public  void visit(IntervalExp e) {
            expToBuffer(e.lwr.value, PREC.assign, this.buf, this.hgs);
            (this.buf.get()).writestring(new ByteSlice(".."));
            expToBuffer(e.upr.value, PREC.assign, this.buf, this.hgs);
        }

        public  void visit(DelegatePtrExp e) {
            expToBuffer(e.e1.value, PREC.primary, this.buf, this.hgs);
            (this.buf.get()).writestring(new ByteSlice(".ptr"));
        }

        public  void visit(DelegateFuncptrExp e) {
            expToBuffer(e.e1.value, PREC.primary, this.buf, this.hgs);
            (this.buf.get()).writestring(new ByteSlice(".funcptr"));
        }

        public  void visit(ArrayExp e) {
            expToBuffer(e.e1.value, PREC.primary, this.buf, this.hgs);
            (this.buf.get()).writeByte(91);
            argsToBuffer(e.arguments.value, this.buf, this.hgs, null);
            (this.buf.get()).writeByte(93);
        }

        public  void visit(DotExp e) {
            expToBuffer(e.e1.value, PREC.primary, this.buf, this.hgs);
            (this.buf.get()).writeByte(46);
            expToBuffer(e.e2.value, PREC.primary, this.buf, this.hgs);
        }

        public  void visit(IndexExp e) {
            expToBuffer(e.e1.value, PREC.primary, this.buf, this.hgs);
            (this.buf.get()).writeByte(91);
            sizeToBuffer(e.e2.value, this.buf, this.hgs);
            (this.buf.get()).writeByte(93);
        }

        public  void visit(PostExp e) {
            expToBuffer(e.e1.value, precedence.get((e.op.value & 0xFF)), this.buf, this.hgs);
            (this.buf.get()).writestring(Token.asString(e.op.value));
        }

        public  void visit(PreExp e) {
            (this.buf.get()).writestring(Token.asString(e.op.value));
            expToBuffer(e.e1.value, precedence.get((e.op.value & 0xFF)), this.buf, this.hgs);
        }

        public  void visit(RemoveExp e) {
            expToBuffer(e.e1.value, PREC.primary, this.buf, this.hgs);
            (this.buf.get()).writestring(new ByteSlice(".remove("));
            expToBuffer(e.e2.value, PREC.assign, this.buf, this.hgs);
            (this.buf.get()).writeByte(41);
        }

        public  void visit(CondExp e) {
            expToBuffer(e.econd.value, PREC.oror, this.buf, this.hgs);
            (this.buf.get()).writestring(new ByteSlice(" ? "));
            expToBuffer(e.e1.value, PREC.expr, this.buf, this.hgs);
            (this.buf.get()).writestring(new ByteSlice(" : "));
            expToBuffer(e.e2.value, PREC.cond, this.buf, this.hgs);
        }

        public  void visit(DefaultInitExp e) {
            (this.buf.get()).writestring(Token.asString(e.subop));
        }

        public  void visit(ClassReferenceExp e) {
            (this.buf.get()).writestring(e.value.toChars());
        }


        public ExpressionPrettyPrintVisitor() {}

        public ExpressionPrettyPrintVisitor copy() {
            ExpressionPrettyPrintVisitor that = new ExpressionPrettyPrintVisitor();
            that.buf = this.buf;
            that.hgs = this.hgs;
            return that;
        }
    }
    public static void templateParameterToBuffer(TemplateParameter tp, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        TemplateParameterPrettyPrintVisitor v = new TemplateParameterPrettyPrintVisitor(buf, hgs);
        tp.accept(v);
    }

    public static class TemplateParameterPrettyPrintVisitor extends Visitor
    {
        public Ptr<OutBuffer> buf = null;
        public Ptr<HdrGenState> hgs = null;
        public  TemplateParameterPrettyPrintVisitor(Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
            this.buf = buf;
            this.hgs = hgs;
        }

        public  void visit(TemplateTypeParameter tp) {
            (this.buf.get()).writestring(tp.ident.value.asString());
            if (tp.specType != null)
            {
                (this.buf.get()).writestring(new ByteSlice(" : "));
                typeToBuffer(tp.specType, null, this.buf, this.hgs);
            }
            if (tp.defaultType.value != null)
            {
                (this.buf.get()).writestring(new ByteSlice(" = "));
                typeToBuffer(tp.defaultType.value, null, this.buf, this.hgs);
            }
        }

        public  void visit(TemplateThisParameter tp) {
            (this.buf.get()).writestring(new ByteSlice("this "));
            this.visit((TemplateTypeParameter)tp);
        }

        public  void visit(TemplateAliasParameter tp) {
            (this.buf.get()).writestring(new ByteSlice("alias "));
            if (tp.specType != null)
                typeToBuffer(tp.specType, tp.ident.value, this.buf, this.hgs);
            else
                (this.buf.get()).writestring(tp.ident.value.asString());
            if (tp.specAlias != null)
            {
                (this.buf.get()).writestring(new ByteSlice(" : "));
                objectToBuffer(tp.specAlias, this.buf, this.hgs);
            }
            if (tp.defaultAlias != null)
            {
                (this.buf.get()).writestring(new ByteSlice(" = "));
                objectToBuffer(tp.defaultAlias, this.buf, this.hgs);
            }
        }

        public  void visit(TemplateValueParameter tp) {
            typeToBuffer(tp.valType, tp.ident.value, this.buf, this.hgs);
            if (tp.specValue != null)
            {
                (this.buf.get()).writestring(new ByteSlice(" : "));
                expressionToBuffer(tp.specValue, this.buf, this.hgs);
            }
            if (tp.defaultValue != null)
            {
                (this.buf.get()).writestring(new ByteSlice(" = "));
                expressionToBuffer(tp.defaultValue, this.buf, this.hgs);
            }
        }

        public  void visit(TemplateTupleParameter tp) {
            (this.buf.get()).writestring(tp.ident.value.asString());
            (this.buf.get()).writestring(new ByteSlice("..."));
        }


        public TemplateParameterPrettyPrintVisitor() {}

        public TemplateParameterPrettyPrintVisitor copy() {
            TemplateParameterPrettyPrintVisitor that = new TemplateParameterPrettyPrintVisitor();
            that.buf = this.buf;
            that.hgs = this.hgs;
            return that;
        }
    }
    public static void conditionToBuffer(Condition c, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        ConditionPrettyPrintVisitor v = new ConditionPrettyPrintVisitor(buf, hgs);
        c.accept(v);
    }

    public static class ConditionPrettyPrintVisitor extends Visitor
    {
        public Ptr<OutBuffer> buf = null;
        public Ptr<HdrGenState> hgs = null;
        public  ConditionPrettyPrintVisitor(Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
            this.buf = buf;
            this.hgs = hgs;
        }

        public  void visit(DebugCondition c) {
            (this.buf.get()).writestring(new ByteSlice("debug ("));
            if (c.ident != null)
                (this.buf.get()).writestring(c.ident.asString());
            else
                (this.buf.get()).print((long)c.level);
            (this.buf.get()).writeByte(41);
        }

        public  void visit(VersionCondition c) {
            (this.buf.get()).writestring(new ByteSlice("version ("));
            if (c.ident != null)
                (this.buf.get()).writestring(c.ident.asString());
            else
                (this.buf.get()).print((long)c.level);
            (this.buf.get()).writeByte(41);
        }

        public  void visit(StaticIfCondition c) {
            (this.buf.get()).writestring(new ByteSlice("static if ("));
            expressionToBuffer(c.exp, this.buf, this.hgs);
            (this.buf.get()).writeByte(41);
        }


        public ConditionPrettyPrintVisitor() {}

        public ConditionPrettyPrintVisitor copy() {
            ConditionPrettyPrintVisitor that = new ConditionPrettyPrintVisitor();
            that.buf = this.buf;
            that.hgs = this.hgs;
            return that;
        }
    }
    public static void toCBuffer(Statement s, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        StatementPrettyPrintVisitor v = new StatementPrettyPrintVisitor(buf, hgs);
        s.accept(v);
    }

    public static void toCBuffer(Type t, Ptr<OutBuffer> buf, Identifier ident, Ptr<HdrGenState> hgs) {
        typeToBuffer(t, ident, buf, hgs);
    }

    public static void toCBuffer(Dsymbol s, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        DsymbolPrettyPrintVisitor v = new DsymbolPrettyPrintVisitor(buf, hgs);
        s.accept(v);
    }

    public static void toCBufferInstance(TemplateInstance ti, Ptr<OutBuffer> buf, boolean qualifyTypes) {
        Ref<HdrGenState> hgs = ref(new HdrGenState());
        hgs.value.fullQual.value = qualifyTypes;
        DsymbolPrettyPrintVisitor v = new DsymbolPrettyPrintVisitor(buf, ptr(hgs));
        v.visit(ti);
    }

    // defaulted all parameters starting with #3
    public static void toCBufferInstance(TemplateInstance ti, Ptr<OutBuffer> buf) {
        return toCBufferInstance(ti, buf, false);
    }

    public static void toCBuffer(Initializer iz, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        initializerToBuffer(iz, buf, hgs);
    }

    public static boolean stcToBuffer(Ptr<OutBuffer> buf, long stc) {
        Ref<Long> stc_ref = ref(stc);
        boolean result = false;
        if (((stc_ref.value & 17592186568704L) == 17592186568704L))
            stc_ref.value &= -524289L;
        if ((stc_ref.value & 562949953421312L) != 0)
            stc_ref.value &= -562949953945601L;
        for (; stc_ref.value != 0;){
            ByteSlice s = stcToString(stc_ref).copy();
            if (s.getLength() == 0)
                break;
            if (result)
                (buf.get()).writeByte(32);
            result = true;
            (buf.get()).writestring(s);
        }
        return result;
    }

    public static ByteSlice stcToString(Ref<Long> stc) {
        {
            int i = 0;
            for (; hdrgen.stcToStringtable.get(i).stc != 0;i++){
                long tbl = hdrgen.stcToStringtable.get(i).stc;
                assert((tbl & 3399896090034079L) != 0);
                if ((stc.value & tbl) != 0)
                {
                    stc.value &= ~tbl;
                    if ((tbl == 134217728L))
                        return new ByteSlice("__thread");
                    byte tok = hdrgen.stcToStringtable.get(i).tok;
                    if (((tok & 0xFF) != 225) && (hdrgen.stcToStringtable.get(i).id.getLength() == 0))
                        hdrgen.stcToStringtable.get(i).id = Token.asString(tok).copy();
                    return hdrgen.stcToStringtable.get(i).id;
                }
            }
        }
        return new ByteSlice();
    }

    public static BytePtr stcToChars(Ref<Long> stc) {
        ByteSlice s = stcToString(stc).copy();
        return ptr(s.get(0));
    }

    public static void trustToBuffer(Ptr<OutBuffer> buf, int trust) {
        (buf.get()).writestring(trustToString(trust));
    }

    public static BytePtr trustToChars(int trust) {
        return toBytePtr(trustToString(trust));
    }

    public static ByteSlice trustToString(int trust) {
        switch (trust)
        {
            case TRUST.default_:
                return new ByteSlice();
            case TRUST.system:
                return new ByteSlice("@system");
            case TRUST.trusted:
                return new ByteSlice("@trusted");
            case TRUST.safe:
                return new ByteSlice("@safe");
            default:
            throw SwitchError.INSTANCE;
        }
    }

    public static void linkageToBuffer(Ptr<OutBuffer> buf, int linkage) {
        ByteSlice s = linkageToString(linkage).copy();
        if (s.getLength() != 0)
        {
            (buf.get()).writestring(new ByteSlice("extern ("));
            (buf.get()).writestring(s);
            (buf.get()).writeByte(41);
        }
    }

    public static BytePtr linkageToChars(int linkage) {
        return toBytePtr(linkageToString(linkage));
    }

    public static ByteSlice linkageToString(int linkage) {
        switch (linkage)
        {
            case LINK.default_:
                return new ByteSlice();
            case LINK.d:
                return new ByteSlice("D");
            case LINK.c:
                return new ByteSlice("C");
            case LINK.cpp:
                return new ByteSlice("C++");
            case LINK.windows:
                return new ByteSlice("Windows");
            case LINK.pascal:
                return new ByteSlice("Pascal");
            case LINK.objc:
                return new ByteSlice("Objective-C");
            case LINK.system:
                return new ByteSlice("System");
            default:
            throw SwitchError.INSTANCE;
        }
    }

    public static void protectionToBuffer(Ptr<OutBuffer> buf, Prot prot) {
        (buf.get()).writestring(protectionToString(prot.kind.value));
        if ((prot.kind.value == Prot.Kind.package_) && (prot.pkg != null))
        {
            (buf.get()).writeByte(40);
            (buf.get()).writestring(prot.pkg.toPrettyChars(true));
            (buf.get()).writeByte(41);
        }
    }

    public static BytePtr protectionToChars(int kind) {
        return toBytePtr(protectionToString(kind));
    }

    public static ByteSlice protectionToString(int kind) {
        switch (kind)
        {
            case Prot.Kind.undefined:
                return new ByteSlice();
            case Prot.Kind.none:
                return new ByteSlice("none");
            case Prot.Kind.private_:
                return new ByteSlice("private");
            case Prot.Kind.package_:
                return new ByteSlice("package");
            case Prot.Kind.protected_:
                return new ByteSlice("protected");
            case Prot.Kind.public_:
                return new ByteSlice("public");
            case Prot.Kind.export_:
                return new ByteSlice("export");
            default:
            throw SwitchError.INSTANCE;
        }
    }

    public static void functionToBufferFull(TypeFunction tf, Ptr<OutBuffer> buf, Identifier ident, Ptr<HdrGenState> hgs, TemplateDeclaration td) {
        visitFuncIdentWithPrefix(tf, ident, td, buf, hgs);
    }

    public static void functionToBufferWithIdent(TypeFunction tf, Ptr<OutBuffer> buf, BytePtr ident) {
        Ref<HdrGenState> hgs = ref(new HdrGenState());
        visitFuncIdentWithPostfix(tf, toDString(ident), buf, ptr(hgs));
    }

    public static void toCBuffer(Expression e, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        ExpressionPrettyPrintVisitor v = new ExpressionPrettyPrintVisitor(buf, hgs);
        e.accept(v);
    }

    public static void argExpTypesToCBuffer(Ptr<OutBuffer> buf, Ptr<DArray<Expression>> arguments) {
        if ((arguments == null) || ((arguments.get()).length.value == 0))
            return ;
        Ref<HdrGenState> hgs = ref(new HdrGenState());
        {
            Slice<Expression> __r1472 = (arguments.get()).opSlice().copy();
            int __key1471 = 0;
            for (; (__key1471 < __r1472.getLength());__key1471 += 1) {
                Expression arg = __r1472.get(__key1471);
                int i = __key1471;
                if (i != 0)
                    (buf.get()).writestring(new ByteSlice(", "));
                typeToBuffer(arg.type.value, null, buf, ptr(hgs));
            }
        }
    }

    public static void toCBuffer(TemplateParameter tp, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        TemplateParameterPrettyPrintVisitor v = new TemplateParameterPrettyPrintVisitor(buf, hgs);
        tp.accept(v);
    }

    public static void arrayObjectsToBuffer(Ptr<OutBuffer> buf, Ptr<DArray<RootObject>> objects) {
        if ((objects == null) || ((objects.get()).length.value == 0))
            return ;
        Ref<HdrGenState> hgs = ref(new HdrGenState());
        {
            Slice<RootObject> __r1474 = (objects.get()).opSlice().copy();
            int __key1473 = 0;
            for (; (__key1473 < __r1474.getLength());__key1473 += 1) {
                RootObject o = __r1474.get(__key1473);
                int i = __key1473;
                if (i != 0)
                    (buf.get()).writestring(new ByteSlice(", "));
                objectToBuffer(o, buf, ptr(hgs));
            }
        }
    }

    public static BytePtr parametersTypeToChars(ParameterList pl) {
        Ref<OutBuffer> buf = ref(new OutBuffer());
        try {
            Ref<HdrGenState> hgs = ref(new HdrGenState());
            parametersToBuffer(pl, ptr(buf), ptr(hgs));
            return buf.value.extractChars();
        }
        finally {
        }
    }

    public static BytePtr parameterToChars(Parameter parameter, TypeFunction tf, boolean fullQual) {
        Ref<OutBuffer> buf = ref(new OutBuffer());
        try {
            Ref<HdrGenState> hgs = ref(new HdrGenState());
            hgs.value.fullQual.value = fullQual;
            parameterToBuffer(parameter, ptr(buf), ptr(hgs));
            if ((tf.parameterList.varargs.value == VarArg.typesafe) && (pequals(parameter, tf.parameterList.get((tf.parameterList.parameters.value.get()).length.value - 1))))
            {
                buf.value.writestring(new ByteSlice("..."));
            }
            return buf.value.extractChars();
        }
        finally {
        }
    }

    public static void parametersToBuffer(ParameterList pl, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        (buf.get()).writeByte(40);
        {
            int __key1475 = 0;
            int __limit1476 = pl.length();
            for (; (__key1475 < __limit1476);__key1475 += 1) {
                int i = __key1475;
                if (i != 0)
                    (buf.get()).writestring(new ByteSlice(", "));
                parameterToBuffer(pl.get(i), buf, hgs);
            }
        }
        {
            int __dispatch7 = 0;
            dispatched_7:
            do {
                switch (__dispatch7 != 0 ? __dispatch7 : pl.varargs.value)
                {
                    case VarArg.none:
                        break;
                    case VarArg.variadic:
                        if ((pl.length() == 0))
                            /*goto case*/{ __dispatch7 = VarArg.typesafe; continue dispatched_7; }
                        (buf.get()).writestring(new ByteSlice(", ..."));
                        break;
                    case VarArg.typesafe:
                        __dispatch7 = 0;
                        (buf.get()).writestring(new ByteSlice("..."));
                        break;
                    default:
                    throw SwitchError.INSTANCE;
                }
            } while(__dispatch7 != 0);
        }
        (buf.get()).writeByte(41);
    }

    public static void parameterToBuffer(Parameter p, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        if (p.userAttribDecl.value != null)
        {
            (buf.get()).writeByte(64);
            boolean isAnonymous = ((p.userAttribDecl.value.atts.get()).length.value > 0) && (((p.userAttribDecl.value.atts.get()).get(0).op.value & 0xFF) != 18);
            if (isAnonymous)
                (buf.get()).writeByte(40);
            argsToBuffer(p.userAttribDecl.value.atts, buf, hgs, null);
            if (isAnonymous)
                (buf.get()).writeByte(41);
            (buf.get()).writeByte(32);
        }
        if ((p.storageClass.value & 256L) != 0)
            (buf.get()).writestring(new ByteSlice("auto "));
        if ((p.storageClass.value & 17592186044416L) != 0)
            (buf.get()).writestring(new ByteSlice("return "));
        if ((p.storageClass.value & 4096L) != 0)
            (buf.get()).writestring(new ByteSlice("out "));
        else if ((p.storageClass.value & 2097152L) != 0)
            (buf.get()).writestring(new ByteSlice("ref "));
        else if ((p.storageClass.value & 2048L) != 0)
            (buf.get()).writestring(new ByteSlice("in "));
        else if ((p.storageClass.value & 8192L) != 0)
            (buf.get()).writestring(new ByteSlice("lazy "));
        else if ((p.storageClass.value & 268435456L) != 0)
            (buf.get()).writestring(new ByteSlice("alias "));
        long stc = p.storageClass.value;
        if ((p.type.value != null) && (((p.type.value.mod.value & 0xFF) & MODFlags.shared_) != 0))
            stc &= -536870913L;
        if (stcToBuffer(buf, stc & 562952639348740L))
            (buf.get()).writeByte(32);
        if ((p.storageClass.value & 268435456L) != 0)
        {
            if (p.ident.value != null)
                (buf.get()).writestring(p.ident.value.asString());
        }
        else if (((p.type.value.ty.value & 0xFF) == ENUMTY.Tident) && (((TypeIdentifier)p.type.value).ident.value.asString().getLength() > 3) && (strncmp(((TypeIdentifier)p.type.value).ident.value.toChars(), new BytePtr("__T"), 3) == 0))
        {
            (buf.get()).writestring(p.ident.value.asString());
        }
        else
        {
            typeToBuffer(p.type.value, p.ident.value, buf, hgs);
        }
        if (p.defaultArg.value != null)
        {
            (buf.get()).writestring(new ByteSlice(" = "));
            expToBuffer(p.defaultArg.value, PREC.assign, buf, hgs);
        }
    }

    public static void argsToBuffer(Ptr<DArray<Expression>> expressions, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs, Expression basis) {
        if ((expressions == null) || ((expressions.get()).length.value == 0))
            return ;
        {
            Slice<Expression> __r1478 = (expressions.get()).opSlice().copy();
            int __key1477 = 0;
            for (; (__key1477 < __r1478.getLength());__key1477 += 1) {
                Expression el = __r1478.get(__key1477);
                int i = __key1477;
                if (i != 0)
                    (buf.get()).writestring(new ByteSlice(", "));
                if (el == null)
                    el = basis;
                if (el != null)
                    expToBuffer(el, PREC.assign, buf, hgs);
            }
        }
    }

    // defaulted all parameters starting with #4
    public static void argsToBuffer(Ptr<DArray<Expression>> expressions, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        return argsToBuffer(expressions, buf, hgs, null);
    }

    public static void sizeToBuffer(Expression e, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        if ((pequals(e.type.value, Type.tsize_t.value)))
        {
            Expression ex = ((e.op.value & 0xFF) == 12) ? ((CastExp)e).e1.value : e;
            ex = ex.optimize(0, false);
            long uval = ((ex.op.value & 0xFF) == 135) ? ex.toInteger() : -1L;
            if (((long)uval >= 0L))
            {
                long sizemax = null;
                if ((target.ptrsize.value == 8))
                    sizemax = -1L;
                else if ((target.ptrsize.value == 4))
                    sizemax = 4294967295L;
                else if ((target.ptrsize.value == 2))
                    sizemax = 65535L;
                else
                    throw new AssertionError("Unreachable code!");
                if ((uval <= sizemax) && (uval <= 9223372036854775807L))
                {
                    (buf.get()).print(uval);
                    return ;
                }
            }
        }
        expToBuffer(e, PREC.assign, buf, hgs);
    }

    public static void expressionToBuffer(Expression e, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        ExpressionPrettyPrintVisitor v = new ExpressionPrettyPrintVisitor(buf, hgs);
        e.accept(v);
    }

    public static void expToBuffer(Expression e, int pr, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        assert((precedence.get((e.op.value & 0xFF)) != PREC.zero));
        assert((pr != PREC.zero));
        if ((precedence.get((e.op.value & 0xFF)) < pr) || (pr == PREC.rel) && (precedence.get((e.op.value & 0xFF)) == pr) || (pr >= PREC.or) && (pr <= PREC.and) && (precedence.get((e.op.value & 0xFF)) == PREC.rel))
        {
            (buf.get()).writeByte(40);
            expressionToBuffer(e, buf, hgs);
            (buf.get()).writeByte(41);
        }
        else
        {
            expressionToBuffer(e, buf, hgs);
        }
    }

    public static void typeToBuffer(Type t, Identifier ident, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        {
            TypeFunction tf = t.isTypeFunction();
            if ((tf) != null)
            {
                visitFuncIdentWithPrefix(tf, ident, null, buf, hgs);
                return ;
            }
        }
        visitWithMask(t, (byte)0, buf, hgs);
        if (ident != null)
        {
            (buf.get()).writeByte(32);
            (buf.get()).writestring(ident.asString());
        }
    }

    public static void visitWithMask(Type t, byte modMask, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        if (((modMask & 0xFF) == (t.mod.value & 0xFF)) || ((t.ty.value & 0xFF) == ENUMTY.Tfunction) || ((t.ty.value & 0xFF) == ENUMTY.Ttuple))
        {
            typeToBufferx(t, buf, hgs);
        }
        else
        {
            byte m = (byte)((t.mod.value & 0xFF) & ~((t.mod.value & 0xFF) & (modMask & 0xFF)));
            if (((m & 0xFF) & MODFlags.shared_) != 0)
            {
                MODtoBuffer(buf, (byte)2);
                (buf.get()).writeByte(40);
            }
            if (((m & 0xFF) & MODFlags.wild) != 0)
            {
                MODtoBuffer(buf, (byte)8);
                (buf.get()).writeByte(40);
            }
            if (((m & 0xFF) & 5) != 0)
            {
                MODtoBuffer(buf, (byte)((m & 0xFF) & 5));
                (buf.get()).writeByte(40);
            }
            typeToBufferx(t, buf, hgs);
            if (((m & 0xFF) & 5) != 0)
                (buf.get()).writeByte(41);
            if (((m & 0xFF) & MODFlags.wild) != 0)
                (buf.get()).writeByte(41);
            if (((m & 0xFF) & MODFlags.shared_) != 0)
                (buf.get()).writeByte(41);
        }
    }

    public static void dumpTemplateInstance(TemplateInstance ti, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        (buf.get()).writeByte(123);
        (buf.get()).writenl();
        (buf.get()).level++;
        if (ti.aliasdecl.value != null)
        {
            dsymbolToBuffer(ti.aliasdecl.value, buf, hgs);
            (buf.get()).writenl();
        }
        else if (ti.members.value != null)
        {
            {
                Slice<Dsymbol> __r1479 = (ti.members.value.get()).opSlice().copy();
                int __key1480 = 0;
                for (; (__key1480 < __r1479.getLength());__key1480 += 1) {
                    Dsymbol m = __r1479.get(__key1480);
                    dsymbolToBuffer(m, buf, hgs);
                }
            }
        }
        (buf.get()).level--;
        (buf.get()).writeByte(125);
        (buf.get()).writenl();
    }

    public static void tiargsToBuffer(TemplateInstance ti, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        (buf.get()).writeByte(33);
        if (ti.nest != 0)
        {
            (buf.get()).writestring(new ByteSlice("(...)"));
            return ;
        }
        if (ti.tiargs.value == null)
        {
            (buf.get()).writestring(new ByteSlice("()"));
            return ;
        }
        if (((ti.tiargs.value.get()).length.value == 1))
        {
            RootObject oarg = (ti.tiargs.value.get()).get(0);
            {
                Type t = isType(oarg);
                if ((t) != null)
                {
                    if (t.equals(Type.tstring.value) || t.equals(Type.twstring.value) || t.equals(Type.tdstring.value) || ((t.mod.value & 0xFF) == 0) && (t.isTypeBasic() != null) || ((t.ty.value & 0xFF) == ENUMTY.Tident) && (((TypeIdentifier)t).idents.length.value == 0))
                    {
                        (buf.get()).writestring(t.toChars());
                        return ;
                    }
                }
                else {
                    Expression e = isExpression(oarg);
                    if ((e) != null)
                    {
                        if (((e.op.value & 0xFF) == 135) || ((e.op.value & 0xFF) == 140) || ((e.op.value & 0xFF) == 13) || ((e.op.value & 0xFF) == 121) || ((e.op.value & 0xFF) == 123))
                        {
                            (buf.get()).writestring(e.toChars());
                            return ;
                        }
                    }
                }
            }
        }
        (buf.get()).writeByte(40);
        ti.nest++;
        {
            Slice<RootObject> __r1482 = (ti.tiargs.value.get()).opSlice().copy();
            int __key1481 = 0;
            for (; (__key1481 < __r1482.getLength());__key1481 += 1) {
                RootObject arg = __r1482.get(__key1481);
                int i = __key1481;
                if (i != 0)
                    (buf.get()).writestring(new ByteSlice(", "));
                objectToBuffer(arg, buf, hgs);
            }
        }
        ti.nest--;
        (buf.get()).writeByte(41);
    }

    public static void objectToBuffer(RootObject oarg, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        {
            Type t = isType(oarg);
            if ((t) != null)
            {
                typeToBuffer(t, null, buf, hgs);
            }
            else {
                Expression e = isExpression(oarg);
                if ((e) != null)
                {
                    if (((e.op.value & 0xFF) == 26))
                        e = e.optimize(0, false);
                    expToBuffer(e, PREC.assign, buf, hgs);
                }
                else {
                    Dsymbol s = isDsymbol(oarg);
                    if ((s) != null)
                    {
                        BytePtr p = pcopy(s.ident.value != null ? s.ident.value.toChars() : s.toChars());
                        (buf.get()).writestring(p);
                    }
                    else {
                        Tuple v = isTuple(oarg);
                        if ((v) != null)
                        {
                            Ptr<DArray<RootObject>> args = ptr(v.objects);
                            {
                                Slice<RootObject> __r1484 = (args.get()).opSlice().copy();
                                int __key1483 = 0;
                                for (; (__key1483 < __r1484.getLength());__key1483 += 1) {
                                    RootObject arg = __r1484.get(__key1483);
                                    int i = __key1483;
                                    if (i != 0)
                                        (buf.get()).writestring(new ByteSlice(", "));
                                    objectToBuffer(arg, buf, hgs);
                                }
                            }
                        }
                        else if (oarg == null)
                        {
                            (buf.get()).writestring(new ByteSlice("NULL"));
                        }
                        else
                        {
                            throw new AssertionError("Unreachable code!");
                        }
                    }
                }
            }
        }
    }

    public static void visitFuncIdentWithPostfix(TypeFunction t, ByteSlice ident, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        if (t.inuse.value != 0)
        {
            t.inuse.value = 2;
            return ;
        }
        t.inuse.value++;
        if ((t.linkage.value > LINK.d) && (((hgs.get()).ddoc.value ? 1 : 0) != 1) && !(hgs.get()).hdrgen)
        {
            linkageToBuffer(buf, t.linkage.value);
            (buf.get()).writeByte(32);
        }
        if (t.next.value != null)
        {
            typeToBuffer(t.next.value, null, buf, hgs);
            if (ident.getLength() != 0)
                (buf.get()).writeByte(32);
        }
        else if ((hgs.get()).ddoc.value)
            (buf.get()).writestring(new ByteSlice("auto "));
        if (ident.getLength() != 0)
            (buf.get()).writestring(ident);
        parametersToBuffer(t.parameterList, buf, hgs);
        if (t.mod.value != 0)
        {
            (buf.get()).writeByte(32);
            MODtoBuffer(buf, t.mod.value);
        }
        Function1<ByteSlice,Void> dg = new Function1<ByteSlice,Void>(){
            public Void invoke(ByteSlice str) {
                Ref<ByteSlice> str_ref = ref(str);
                (buf.get()).writeByte(32);
                (buf.get()).writestring(str_ref.value);
            }
        };
        attributesApply(t, dg, TRUSTformat.TRUSTformatDefault);
        t.inuse.value--;
    }

    public static void visitFuncIdentWithPrefix(TypeFunction t, Identifier ident, TemplateDeclaration td, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        if (t.inuse.value != 0)
        {
            t.inuse.value = 2;
            return ;
        }
        t.inuse.value++;
        if (t.mod.value != 0)
        {
            MODtoBuffer(buf, t.mod.value);
            (buf.get()).writeByte(32);
        }
        Function1<ByteSlice,Void> ignoreReturn = new Function1<ByteSlice,Void>(){
            public Void invoke(ByteSlice str) {
                Ref<ByteSlice> str_ref = ref(str);
                if (!__equals(str_ref.value, new ByteSlice("return")))
                {
                    if ((pequals(ident, Id.ctor.value)) && __equals(str_ref.value, new ByteSlice("ref")))
                        return null;
                    (buf.get()).writestring(str_ref.value);
                    (buf.get()).writeByte(32);
                }
            }
        };
        attributesApply(t, ignoreReturn, TRUSTformat.TRUSTformatDefault);
        if ((t.linkage.value > LINK.d) && (((hgs.get()).ddoc.value ? 1 : 0) != 1) && !(hgs.get()).hdrgen)
        {
            linkageToBuffer(buf, t.linkage.value);
            (buf.get()).writeByte(32);
        }
        if ((ident != null) && (ident.toHChars2() != ident.toChars()))
        {
        }
        else if (t.next.value != null)
        {
            typeToBuffer(t.next.value, null, buf, hgs);
            if (ident != null)
                (buf.get()).writeByte(32);
        }
        else if ((hgs.get()).ddoc.value)
            (buf.get()).writestring(new ByteSlice("auto "));
        if (ident != null)
            (buf.get()).writestring(ident.toHChars2());
        if (td != null)
        {
            (buf.get()).writeByte(40);
            {
                Slice<TemplateParameter> __r1486 = (td.origParameters.value.get()).opSlice().copy();
                int __key1485 = 0;
                for (; (__key1485 < __r1486.getLength());__key1485 += 1) {
                    TemplateParameter p = __r1486.get(__key1485);
                    int i = __key1485;
                    if (i != 0)
                        (buf.get()).writestring(new ByteSlice(", "));
                    templateParameterToBuffer(p, buf, hgs);
                }
            }
            (buf.get()).writeByte(41);
        }
        parametersToBuffer(t.parameterList, buf, hgs);
        if (t.isreturn.value)
        {
            (buf.get()).writestring(new ByteSlice(" return"));
        }
        t.inuse.value--;
    }

    public static void initializerToBuffer(Initializer inx, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        Ref<Ptr<OutBuffer>> buf_ref = ref(buf);
        Ref<Ptr<HdrGenState>> hgs_ref = ref(hgs);
        Function1<ErrorInitializer,Void> visitError = new Function1<ErrorInitializer,Void>(){
            public Void invoke(ErrorInitializer iz) {
                (buf_ref.value.get()).writestring(new ByteSlice("__error__"));
            }
        };
        Function1<VoidInitializer,Void> visitVoid = new Function1<VoidInitializer,Void>(){
            public Void invoke(VoidInitializer iz) {
                (buf_ref.value.get()).writestring(new ByteSlice("void"));
            }
        };
        Function1<StructInitializer,Void> visitStruct = new Function1<StructInitializer,Void>(){
            public Void invoke(StructInitializer si) {
                (buf_ref.value.get()).writeByte(123);
                {
                    Ref<Slice<Identifier>> __r1488 = ref(si.field.opSlice().copy());
                    IntRef __key1487 = ref(0);
                    for (; (__key1487.value < __r1488.value.getLength());__key1487.value += 1) {
                        Identifier id = __r1488.value.get(__key1487.value);
                        IntRef i = ref(__key1487.value);
                        if (i.value != 0)
                            (buf_ref.value.get()).writestring(new ByteSlice(", "));
                        if (id != null)
                        {
                            (buf_ref.value.get()).writestring(id.asString());
                            (buf_ref.value.get()).writeByte(58);
                        }
                        {
                            Ref<Initializer> iz = ref(si.value.get(i.value));
                            if ((iz.value) != null)
                                initializerToBuffer(iz.value, buf_ref.value, hgs_ref.value);
                        }
                    }
                }
                (buf_ref.value.get()).writeByte(125);
            }
        };
        Function1<ArrayInitializer,Void> visitArray = new Function1<ArrayInitializer,Void>(){
            public Void invoke(ArrayInitializer ai) {
                (buf_ref.value.get()).writeByte(91);
                {
                    Ref<Slice<Expression>> __r1490 = ref(ai.index.opSlice().copy());
                    IntRef __key1489 = ref(0);
                    for (; (__key1489.value < __r1490.value.getLength());__key1489.value += 1) {
                        Ref<Expression> ex = ref(__r1490.value.get(__key1489.value));
                        IntRef i = ref(__key1489.value);
                        if (i.value != 0)
                            (buf_ref.value.get()).writestring(new ByteSlice(", "));
                        if (ex.value != null)
                        {
                            expressionToBuffer(ex.value, buf_ref.value, hgs_ref.value);
                            (buf_ref.value.get()).writeByte(58);
                        }
                        {
                            Ref<Initializer> iz = ref(ai.value.get(i.value));
                            if ((iz.value) != null)
                                initializerToBuffer(iz.value, buf_ref.value, hgs_ref.value);
                        }
                    }
                }
                (buf_ref.value.get()).writeByte(93);
            }
        };
        Function1<ExpInitializer,Void> visitExp = new Function1<ExpInitializer,Void>(){
            public Void invoke(ExpInitializer ei) {
                expressionToBuffer(ei.exp.value, buf_ref.value, hgs_ref.value);
            }
        };
        switch ((inx.kind & 0xFF))
        {
            case 1:
                visitError.invoke(inx.isErrorInitializer());
                return ;
            case 0:
                visitVoid.invoke(inx.isVoidInitializer());
                return ;
            case 2:
                visitStruct.invoke(inx.isStructInitializer());
                return ;
            case 3:
                visitArray.invoke(inx.isArrayInitializer());
                return ;
            case 4:
                visitExp.invoke(inx.isExpInitializer());
                return ;
            default:
            throw SwitchError.INSTANCE;
        }
    }

    public static void typeToBufferx(Type t, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        Ref<Ptr<OutBuffer>> buf_ref = ref(buf);
        Ref<Ptr<HdrGenState>> hgs_ref = ref(hgs);
        Function1<Type,Void> visitType = new Function1<Type,Void>(){
            public Void invoke(Type t) {
                Ref<Type> t_ref = ref(t);
                printf(new BytePtr("t = %p, ty = %d\n"), t_ref.value, (t_ref.value.ty.value & 0xFF));
                throw new AssertionError("Unreachable code!");
            }
        };
        Function1<TypeError,Void> visitError = new Function1<TypeError,Void>(){
            public Void invoke(TypeError t) {
                (buf_ref.value.get()).writestring(new ByteSlice("_error_"));
            }
        };
        Function1<TypeBasic,Void> visitBasic = new Function1<TypeBasic,Void>(){
            public Void invoke(TypeBasic t) {
                (buf_ref.value.get()).writestring(t.dstring.value);
            }
        };
        Function1<TypeTraits,Void> visitTraits = new Function1<TypeTraits,Void>(){
            public Void invoke(TypeTraits t) {
                expressionToBuffer(t.exp.value, buf_ref.value, hgs_ref.value);
            }
        };
        Function1<TypeVector,Void> visitVector = new Function1<TypeVector,Void>(){
            public Void invoke(TypeVector t) {
                (buf_ref.value.get()).writestring(new ByteSlice("__vector("));
                visitWithMask(t.basetype.value, t.mod.value, buf_ref.value, hgs_ref.value);
                (buf_ref.value.get()).writestring(new ByteSlice(")"));
            }
        };
        Function1<TypeSArray,Void> visitSArray = new Function1<TypeSArray,Void>(){
            public Void invoke(TypeSArray t) {
                visitWithMask(t.next.value, t.mod.value, buf_ref.value, hgs_ref.value);
                (buf_ref.value.get()).writeByte(91);
                sizeToBuffer(t.dim.value, buf_ref.value, hgs_ref.value);
                (buf_ref.value.get()).writeByte(93);
            }
        };
        Function1<TypeDArray,Void> visitDArray = new Function1<TypeDArray,Void>(){
            public Void invoke(TypeDArray t) {
                Type ut = t.castMod((byte)0);
                if ((hgs_ref.value.get()).declstring.value)
                    /*goto L1*//*unrolled goto*/
                    (buf_ref.value.get()).writestring(new ByteSlice("dstring"));
                if (ut.equals(Type.tstring.value))
                    (buf_ref.value.get()).writestring(new ByteSlice("string"));
                else if (ut.equals(Type.twstring.value))
                    (buf_ref.value.get()).writestring(new ByteSlice("wstring"));
                else if (ut.equals(Type.tdstring.value))
                    (buf_ref.value.get()).writestring(new ByteSlice("dstring"));
                else
                {
                /*L1:*/
                    visitWithMask(t.next.value, t.mod.value, buf_ref.value, hgs_ref.value);
                    (buf_ref.value.get()).writestring(new ByteSlice("[]"));
                }
            }
        };
        Function1<TypeAArray,Void> visitAArray = new Function1<TypeAArray,Void>(){
            public Void invoke(TypeAArray t) {
                visitWithMask(t.next.value, t.mod.value, buf_ref.value, hgs_ref.value);
                (buf_ref.value.get()).writeByte(91);
                visitWithMask(t.index.value, (byte)0, buf_ref.value, hgs_ref.value);
                (buf_ref.value.get()).writeByte(93);
            }
        };
        Function1<TypePointer,Void> visitPointer = new Function1<TypePointer,Void>(){
            public Void invoke(TypePointer t) {
                if (((t.next.value.ty.value & 0xFF) == ENUMTY.Tfunction))
                    visitFuncIdentWithPostfix((TypeFunction)t.next.value, new ByteSlice("function"), buf_ref.value, hgs_ref.value);
                else
                {
                    visitWithMask(t.next.value, t.mod.value, buf_ref.value, hgs_ref.value);
                    (buf_ref.value.get()).writeByte(42);
                }
            }
        };
        Function1<TypeReference,Void> visitReference = new Function1<TypeReference,Void>(){
            public Void invoke(TypeReference t) {
                visitWithMask(t.next.value, t.mod.value, buf_ref.value, hgs_ref.value);
                (buf_ref.value.get()).writeByte(38);
            }
        };
        Function1<TypeFunction,Void> visitFunction = new Function1<TypeFunction,Void>(){
            public Void invoke(TypeFunction t) {
                Ref<TypeFunction> t_ref = ref(t);
                visitFuncIdentWithPostfix(t_ref.value, new ByteSlice(), buf_ref.value, hgs_ref.value);
            }
        };
        Function1<TypeDelegate,Void> visitDelegate = new Function1<TypeDelegate,Void>(){
            public Void invoke(TypeDelegate t) {
                visitFuncIdentWithPostfix((TypeFunction)t.next.value, new ByteSlice("delegate"), buf_ref.value, hgs_ref.value);
            }
        };
        Function1<TypeQualified,Void> visitTypeQualifiedHelper = new Function1<TypeQualified,Void>(){
            public Void invoke(TypeQualified t) {
                {
                    Ref<Slice<RootObject>> __r1491 = ref(t.idents.opSlice().copy());
                    IntRef __key1492 = ref(0);
                    for (; (__key1492.value < __r1491.value.getLength());__key1492.value += 1) {
                        Ref<RootObject> id = ref(__r1491.value.get(__key1492.value));
                        if ((id.value.dyncast() == DYNCAST.dsymbol))
                        {
                            (buf_ref.value.get()).writeByte(46);
                            Ref<TemplateInstance> ti = ref((TemplateInstance)id.value);
                            dsymbolToBuffer(ti.value, buf_ref.value, hgs_ref.value);
                        }
                        else if ((id.value.dyncast() == DYNCAST.expression))
                        {
                            (buf_ref.value.get()).writeByte(91);
                            expressionToBuffer((Expression)id.value, buf_ref.value, hgs_ref.value);
                            (buf_ref.value.get()).writeByte(93);
                        }
                        else if ((id.value.dyncast() == DYNCAST.type))
                        {
                            (buf_ref.value.get()).writeByte(91);
                            typeToBufferx((Type)id.value, buf_ref.value, hgs_ref.value);
                            (buf_ref.value.get()).writeByte(93);
                        }
                        else
                        {
                            (buf_ref.value.get()).writeByte(46);
                            (buf_ref.value.get()).writestring(id.value.asString());
                        }
                    }
                }
            }
        };
        Function1<TypeIdentifier,Void> visitIdentifier = new Function1<TypeIdentifier,Void>(){
            public Void invoke(TypeIdentifier t) {
                Ref<TypeIdentifier> t_ref = ref(t);
                (buf_ref.value.get()).writestring(t_ref.value.ident.value.asString());
                visitTypeQualifiedHelper.invoke(t_ref.value);
            }
        };
        Function1<TypeInstance,Void> visitInstance = new Function1<TypeInstance,Void>(){
            public Void invoke(TypeInstance t) {
                Ref<TypeInstance> t_ref = ref(t);
                dsymbolToBuffer(t_ref.value.tempinst.value, buf_ref.value, hgs_ref.value);
                visitTypeQualifiedHelper.invoke(t_ref.value);
            }
        };
        Function1<TypeTypeof,Void> visitTypeof = new Function1<TypeTypeof,Void>(){
            public Void invoke(TypeTypeof t) {
                Ref<TypeTypeof> t_ref = ref(t);
                (buf_ref.value.get()).writestring(new ByteSlice("typeof("));
                expressionToBuffer(t_ref.value.exp.value, buf_ref.value, hgs_ref.value);
                (buf_ref.value.get()).writeByte(41);
                visitTypeQualifiedHelper.invoke(t_ref.value);
            }
        };
        Function1<TypeReturn,Void> visitReturn = new Function1<TypeReturn,Void>(){
            public Void invoke(TypeReturn t) {
                Ref<TypeReturn> t_ref = ref(t);
                (buf_ref.value.get()).writestring(new ByteSlice("typeof(return)"));
                visitTypeQualifiedHelper.invoke(t_ref.value);
            }
        };
        Function1<TypeEnum,Void> visitEnum = new Function1<TypeEnum,Void>(){
            public Void invoke(TypeEnum t) {
                (buf_ref.value.get()).writestring((hgs_ref.value.get()).fullQual.value ? t.sym.value.toPrettyChars(false) : t.sym.value.toChars());
            }
        };
        Function1<TypeStruct,Void> visitStruct = new Function1<TypeStruct,Void>(){
            public Void invoke(TypeStruct t) {
                Ref<TemplateInstance> ti = ref(t.sym.value.parent.value != null ? t.sym.value.parent.value.isTemplateInstance() : null);
                if ((ti.value != null) && (pequals(ti.value.aliasdecl.value, t.sym.value)))
                    (buf_ref.value.get()).writestring((hgs_ref.value.get()).fullQual.value ? ti.value.toPrettyChars(false) : ti.value.toChars());
                else
                    (buf_ref.value.get()).writestring((hgs_ref.value.get()).fullQual.value ? t.sym.value.toPrettyChars(false) : t.sym.value.toChars());
            }
        };
        Function1<TypeClass,Void> visitClass = new Function1<TypeClass,Void>(){
            public Void invoke(TypeClass t) {
                Ref<TemplateInstance> ti = ref(t.sym.value.parent.value.isTemplateInstance());
                if ((ti.value != null) && (pequals(ti.value.aliasdecl.value, t.sym.value)))
                    (buf_ref.value.get()).writestring((hgs_ref.value.get()).fullQual.value ? ti.value.toPrettyChars(false) : ti.value.toChars());
                else
                    (buf_ref.value.get()).writestring((hgs_ref.value.get()).fullQual.value ? t.sym.value.toPrettyChars(false) : t.sym.value.toChars());
            }
        };
        Function1<TypeTuple,Void> visitTuple = new Function1<TypeTuple,Void>(){
            public Void invoke(TypeTuple t) {
                parametersToBuffer(new ParameterList(t.arguments.value, VarArg.none), buf_ref.value, hgs_ref.value);
            }
        };
        Function1<TypeSlice,Void> visitSlice = new Function1<TypeSlice,Void>(){
            public Void invoke(TypeSlice t) {
                visitWithMask(t.next.value, t.mod.value, buf_ref.value, hgs_ref.value);
                (buf_ref.value.get()).writeByte(91);
                sizeToBuffer(t.lwr.value, buf_ref.value, hgs_ref.value);
                (buf_ref.value.get()).writestring(new ByteSlice(" .. "));
                sizeToBuffer(t.upr.value, buf_ref.value, hgs_ref.value);
                (buf_ref.value.get()).writeByte(93);
            }
        };
        Function1<TypeNull,Void> visitNull = new Function1<TypeNull,Void>(){
            public Void invoke(TypeNull t) {
                (buf_ref.value.get()).writestring(new ByteSlice("typeof(null)"));
            }
        };
        switch ((t.ty.value & 0xFF))
        {
            default:
            t.isTypeBasic() != null ? visitBasic.invoke((TypeBasic)t) : visitType.invoke(t);
            return ;
            case 34:
                visitError.invoke((TypeError)t);
                return ;
            case 44:
                visitTraits.invoke((TypeTraits)t);
                return ;
            case 41:
                visitVector.invoke((TypeVector)t);
                return ;
            case 1:
                visitSArray.invoke((TypeSArray)t);
                return ;
            case 0:
                visitDArray.invoke((TypeDArray)t);
                return ;
            case 2:
                visitAArray.invoke((TypeAArray)t);
                return ;
            case 3:
                visitPointer.invoke((TypePointer)t);
                return ;
            case 4:
                visitReference.invoke((TypeReference)t);
                return ;
            case 5:
                visitFunction.invoke((TypeFunction)t);
                return ;
            case 10:
                visitDelegate.invoke((TypeDelegate)t);
                return ;
            case 6:
                visitIdentifier.invoke((TypeIdentifier)t);
                return ;
            case 35:
                visitInstance.invoke((TypeInstance)t);
                return ;
            case 36:
                visitTypeof.invoke((TypeTypeof)t);
                return ;
            case 39:
                visitReturn.invoke((TypeReturn)t);
                return ;
            case 9:
                visitEnum.invoke((TypeEnum)t);
                return ;
            case 8:
                visitStruct.invoke((TypeStruct)t);
                return ;
            case 7:
                visitClass.invoke((TypeClass)t);
                return ;
            case 37:
                visitTuple.invoke((TypeTuple)t);
                return ;
            case 38:
                visitSlice.invoke((TypeSlice)t);
                return ;
            case 40:
                visitNull.invoke((TypeNull)t);
                return ;
        }
    }

}
