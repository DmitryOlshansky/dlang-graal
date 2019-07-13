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
        private long stc = 0L;
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
        public boolean ddoc = false;
        public boolean fullDump = false;
        public boolean fullQual = false;
        public int tpltMember = 0;
        public int autoMember = 0;
        public int forStmtInit = 0;
        public boolean declstring = false;
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
            writeFile(m.loc, m.hdrfile.asString(), toByteSlice(buf.value.peekSlice()));
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
                {
                    (buf.get()).writestring(new ByteSlice("deprecated "));
                }
            }
            (buf.get()).writestring(new ByteSlice("module "));
            (buf.get()).writestring((m.md.get()).toChars());
            (buf.get()).writeByte(59);
            (buf.get()).writenl();
        }
        {
            Slice<Dsymbol> __r1415 = (m.members.get()).opSlice().copy();
            int __key1416 = 0;
            for (; (__key1416 < __r1415.getLength());__key1416 += 1) {
                Dsymbol s = __r1415.get(__key1416);
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
            if ((s.exp != null) && ((s.exp.op & 0xFF) == 38) && (((DeclarationExp)s.exp).declaration != null))
            {
                dsymbolToBuffer(((DeclarationExp)s.exp).declaration, this.buf, this.hgs);
                return ;
            }
            if (s.exp != null)
            {
                expressionToBuffer(s.exp, this.buf, this.hgs);
            }
            (this.buf.get()).writeByte(59);
            if ((this.hgs.get()).forStmtInit == 0)
            {
                (this.buf.get()).writenl();
            }
        }

        public  void visit(CompileStatement s) {
            (this.buf.get()).writestring(new ByteSlice("mixin("));
            argsToBuffer(s.exps, this.buf, this.hgs, null);
            (this.buf.get()).writestring(new ByteSlice(");"));
            if ((this.hgs.get()).forStmtInit == 0)
            {
                (this.buf.get()).writenl();
            }
        }

        public  void visit(CompoundStatement s) {
            {
                Slice<Statement> __r1417 = (s.statements.get()).opSlice().copy();
                int __key1418 = 0;
                for (; (__key1418 < __r1417.getLength());__key1418 += 1) {
                    Statement sx = __r1417.get(__key1418);
                    if (sx != null)
                    {
                        sx.accept(this);
                    }
                }
            }
        }

        public  void visit(CompoundDeclarationStatement s) {
            boolean anywritten = false;
            {
                Slice<Statement> __r1419 = (s.statements.get()).opSlice().copy();
                int __key1420 = 0;
                for (; (__key1420 < __r1419.getLength());__key1420 += 1) {
                    Statement sx = __r1419.get(__key1420);
                    ExpStatement ds = sx != null ? sx.isExpStatement() : null;
                    if ((ds != null) && ((ds.exp.op & 0xFF) == 38))
                    {
                        Dsymbol d = ((DeclarationExp)ds.exp).declaration;
                        assert(d.isDeclaration() != null);
                        {
                            VarDeclaration v = d.isVarDeclaration();
                            if ((v) != null)
                            {
                                DsymbolPrettyPrintVisitor ppv = new DsymbolPrettyPrintVisitor(this.buf, this.hgs);
                                ppv.visitVarDecl(v, anywritten);
                            }
                            else
                            {
                                dsymbolToBuffer(d, this.buf, this.hgs);
                            }
                        }
                        anywritten = true;
                    }
                }
            }
            (this.buf.get()).writeByte(59);
            if ((this.hgs.get()).forStmtInit == 0)
            {
                (this.buf.get()).writenl();
            }
        }

        public  void visit(UnrolledLoopStatement s) {
            (this.buf.get()).writestring(new ByteSlice("/*unrolled*/ {"));
            (this.buf.get()).writenl();
            (this.buf.get()).level++;
            {
                Slice<Statement> __r1421 = (s.statements.get()).opSlice().copy();
                int __key1422 = 0;
                for (; (__key1422 < __r1421.getLength());__key1422 += 1) {
                    Statement sx = __r1421.get(__key1422);
                    if (sx != null)
                    {
                        sx.accept(this);
                    }
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
            if (s.statement != null)
            {
                s.statement.accept(this);
            }
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
            {
                s._body.accept(this);
            }
        }

        public  void visit(DoStatement s) {
            (this.buf.get()).writestring(new ByteSlice("do"));
            (this.buf.get()).writenl();
            if (s._body != null)
            {
                s._body.accept(this);
            }
            (this.buf.get()).writestring(new ByteSlice("while ("));
            expressionToBuffer(s.condition, this.buf, this.hgs);
            (this.buf.get()).writestring(new ByteSlice(");"));
            (this.buf.get()).writenl();
        }

        public  void visit(ForStatement s) {
            (this.buf.get()).writestring(new ByteSlice("for ("));
            if (s._init != null)
            {
                (this.hgs.get()).forStmtInit++;
                s._init.accept(this);
                (this.hgs.get()).forStmtInit--;
            }
            else
            {
                (this.buf.get()).writeByte(59);
            }
            if (s.condition != null)
            {
                (this.buf.get()).writeByte(32);
                expressionToBuffer(s.condition, this.buf, this.hgs);
            }
            (this.buf.get()).writeByte(59);
            if (s.increment != null)
            {
                (this.buf.get()).writeByte(32);
                expressionToBuffer(s.increment, this.buf, this.hgs);
            }
            (this.buf.get()).writeByte(41);
            (this.buf.get()).writenl();
            (this.buf.get()).writeByte(123);
            (this.buf.get()).writenl();
            (this.buf.get()).level++;
            if (s._body != null)
            {
                s._body.accept(this);
            }
            (this.buf.get()).level--;
            (this.buf.get()).writeByte(125);
            (this.buf.get()).writenl();
        }

        public  void foreachWithoutBody(ForeachStatement s) {
            (this.buf.get()).writestring(Token.asString(s.op));
            (this.buf.get()).writestring(new ByteSlice(" ("));
            {
                Slice<Parameter> __r1424 = (s.parameters.get()).opSlice().copy();
                int __key1423 = 0;
                for (; (__key1423 < __r1424.getLength());__key1423 += 1) {
                    Parameter p = __r1424.get(__key1423);
                    int i = __key1423;
                    if (i != 0)
                    {
                        (this.buf.get()).writestring(new ByteSlice(", "));
                    }
                    if (stcToBuffer(this.buf, p.storageClass))
                    {
                        (this.buf.get()).writeByte(32);
                    }
                    if (p.type != null)
                    {
                        typeToBuffer(p.type, p.ident, this.buf, this.hgs);
                    }
                    else
                    {
                        (this.buf.get()).writestring(p.ident.asString());
                    }
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
            {
                s._body.value.accept(this);
            }
            (this.buf.get()).level--;
            (this.buf.get()).writeByte(125);
            (this.buf.get()).writenl();
        }

        public  void foreachRangeWithoutBody(ForeachRangeStatement s) {
            (this.buf.get()).writestring(Token.asString(s.op));
            (this.buf.get()).writestring(new ByteSlice(" ("));
            if (s.prm.type != null)
            {
                typeToBuffer(s.prm.type, s.prm.ident, this.buf, this.hgs);
            }
            else
            {
                (this.buf.get()).writestring(s.prm.ident.asString());
            }
            (this.buf.get()).writestring(new ByteSlice("; "));
            expressionToBuffer(s.lwr, this.buf, this.hgs);
            (this.buf.get()).writestring(new ByteSlice(" .. "));
            expressionToBuffer(s.upr, this.buf, this.hgs);
            (this.buf.get()).writeByte(41);
            (this.buf.get()).writenl();
        }

        public  void visit(ForeachRangeStatement s) {
            this.foreachRangeWithoutBody(s);
            (this.buf.get()).writeByte(123);
            (this.buf.get()).writenl();
            (this.buf.get()).level++;
            if (s._body.value != null)
            {
                s._body.value.accept(this);
            }
            (this.buf.get()).level--;
            (this.buf.get()).writeByte(125);
            (this.buf.get()).writenl();
        }

        public  void visit(StaticForeachStatement s) {
            (this.buf.get()).writestring(new ByteSlice("static "));
            if (s.sfe.aggrfe != null)
            {
                this.visit(s.sfe.aggrfe);
            }
            else
            {
                assert(s.sfe.rangefe != null);
                this.visit(s.sfe.rangefe);
            }
        }

        public  void visit(IfStatement s) {
            (this.buf.get()).writestring(new ByteSlice("if ("));
            {
                Parameter p = s.prm;
                if ((p) != null)
                {
                    long stc = p.storageClass;
                    if ((p.type == null) && (stc == 0))
                    {
                        stc = 256L;
                    }
                    if (stcToBuffer(this.buf, stc))
                    {
                        (this.buf.get()).writeByte(32);
                    }
                    if (p.type != null)
                    {
                        typeToBuffer(p.type, p.ident, this.buf, this.hgs);
                    }
                    else
                    {
                        (this.buf.get()).writestring(p.ident.asString());
                    }
                    (this.buf.get()).writestring(new ByteSlice(" = "));
                }
            }
            expressionToBuffer(s.condition, this.buf, this.hgs);
            (this.buf.get()).writeByte(41);
            (this.buf.get()).writenl();
            if (s.ifbody.isScopeStatement() != null)
            {
                s.ifbody.accept(this);
            }
            else
            {
                (this.buf.get()).level++;
                s.ifbody.accept(this);
                (this.buf.get()).level--;
            }
            if (s.elsebody != null)
            {
                (this.buf.get()).writestring(new ByteSlice("else"));
                if (s.elsebody.isIfStatement() == null)
                {
                    (this.buf.get()).writenl();
                }
                else
                {
                    (this.buf.get()).writeByte(32);
                }
                if ((s.elsebody.isScopeStatement() != null) || (s.elsebody.isIfStatement() != null))
                {
                    s.elsebody.accept(this);
                }
                else
                {
                    (this.buf.get()).level++;
                    s.elsebody.accept(this);
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
            if (s.ifbody != null)
            {
                s.ifbody.accept(this);
            }
            (this.buf.get()).level--;
            (this.buf.get()).writeByte(125);
            (this.buf.get()).writenl();
            if (s.elsebody != null)
            {
                (this.buf.get()).writestring(new ByteSlice("else"));
                (this.buf.get()).writenl();
                (this.buf.get()).writeByte(123);
                (this.buf.get()).level++;
                (this.buf.get()).writenl();
                s.elsebody.accept(this);
                (this.buf.get()).level--;
                (this.buf.get()).writeByte(125);
            }
            (this.buf.get()).writenl();
        }

        public  void visit(PragmaStatement s) {
            (this.buf.get()).writestring(new ByteSlice("pragma ("));
            (this.buf.get()).writestring(s.ident.asString());
            if ((s.args != null) && ((s.args.get()).length != 0))
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
            expressionToBuffer(s.condition, this.buf, this.hgs);
            (this.buf.get()).writeByte(41);
            (this.buf.get()).writenl();
            if (s._body != null)
            {
                if (s._body.isScopeStatement() == null)
                {
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
                    s._body.accept(this);
                }
            }
        }

        public  void visit(CaseStatement s) {
            (this.buf.get()).writestring(new ByteSlice("case "));
            expressionToBuffer(s.exp, this.buf, this.hgs);
            (this.buf.get()).writeByte(58);
            (this.buf.get()).writenl();
            s.statement.accept(this);
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
            s.statement.accept(this);
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
            if (s.exp != null)
            {
                expressionToBuffer(s.exp, this.buf, this.hgs);
            }
            (this.buf.get()).writeByte(59);
            (this.buf.get()).writenl();
        }

        public  void visit(BreakStatement s) {
            (this.buf.get()).writestring(new ByteSlice("break"));
            if (s.ident != null)
            {
                (this.buf.get()).writeByte(32);
                (this.buf.get()).writestring(s.ident.asString());
            }
            (this.buf.get()).writeByte(59);
            (this.buf.get()).writenl();
        }

        public  void visit(ContinueStatement s) {
            (this.buf.get()).writestring(new ByteSlice("continue"));
            if (s.ident != null)
            {
                (this.buf.get()).writeByte(32);
                (this.buf.get()).writestring(s.ident.asString());
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
            if (s._body != null)
            {
                (this.buf.get()).writeByte(32);
                s._body.accept(this);
            }
        }

        public  void visit(WithStatement s) {
            (this.buf.get()).writestring(new ByteSlice("with ("));
            expressionToBuffer(s.exp, this.buf, this.hgs);
            (this.buf.get()).writestring(new ByteSlice(")"));
            (this.buf.get()).writenl();
            if (s._body != null)
            {
                s._body.accept(this);
            }
        }

        public  void visit(TryCatchStatement s) {
            (this.buf.get()).writestring(new ByteSlice("try"));
            (this.buf.get()).writenl();
            if (s._body != null)
            {
                if (s._body.isScopeStatement() != null)
                {
                    s._body.accept(this);
                }
                else
                {
                    (this.buf.get()).level++;
                    s._body.accept(this);
                    (this.buf.get()).level--;
                }
            }
            {
                Slice<Catch> __r1425 = (s.catches.get()).opSlice().copy();
                int __key1426 = 0;
                for (; (__key1426 < __r1425.getLength());__key1426 += 1) {
                    Catch c = __r1425.get(__key1426);
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
            s._body.accept(this);
            (this.buf.get()).level--;
            (this.buf.get()).writeByte(125);
            (this.buf.get()).writenl();
            (this.buf.get()).writestring(new ByteSlice("finally"));
            (this.buf.get()).writenl();
            if (s.finalbody.isScopeStatement() != null)
            {
                s.finalbody.accept(this);
            }
            else
            {
                (this.buf.get()).level++;
                s.finalbody.accept(this);
                (this.buf.get()).level--;
            }
        }

        public  void visit(ScopeGuardStatement s) {
            (this.buf.get()).writestring(Token.asString(s.tok));
            (this.buf.get()).writeByte(32);
            if (s.statement != null)
            {
                s.statement.accept(this);
            }
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
            if (s.statement != null)
            {
                s.statement.accept(this);
            }
        }

        public  void visit(AsmStatement s) {
            (this.buf.get()).writestring(new ByteSlice("asm { "));
            Ptr<Token> t = s.tokens;
            (this.buf.get()).level++;
            for (; t != null;){
                (this.buf.get()).writestring((t.get()).toChars());
                if (((t.get()).next.value != null) && (((t.get()).value & 0xFF) != 75) && (((t.get()).value & 0xFF) != 99) && ((((t.get()).next.value.get()).value & 0xFF) != 99) && (((t.get()).value & 0xFF) != 3) && ((((t.get()).next.value.get()).value & 0xFF) != 3) && ((((t.get()).next.value.get()).value & 0xFF) != 4) && (((t.get()).value & 0xFF) != 1) && ((((t.get()).next.value.get()).value & 0xFF) != 1) && ((((t.get()).next.value.get()).value & 0xFF) != 2) && (((t.get()).value & 0xFF) != 97) && ((((t.get()).next.value.get()).value & 0xFF) != 97))
                {
                    (this.buf.get()).writeByte(32);
                }
                t = (t.get()).next.value;
            }
            (this.buf.get()).level--;
            (this.buf.get()).writestring(new ByteSlice("; }"));
            (this.buf.get()).writenl();
        }

        public  void visit(ImportStatement s) {
            {
                Slice<Dsymbol> __r1427 = (s.imports.get()).opSlice().copy();
                int __key1428 = 0;
                for (; (__key1428 < __r1427.getLength());__key1428 += 1) {
                    Dsymbol imp = __r1427.get(__key1428);
                    dsymbolToBuffer(imp, this.buf, this.hgs);
                }
            }
        }

        public  void visit(Catch c) {
            (this.buf.get()).writestring(new ByteSlice("catch"));
            if (c.type != null)
            {
                (this.buf.get()).writeByte(40);
                typeToBuffer(c.type, c.ident, this.buf, this.hgs);
                (this.buf.get()).writeByte(41);
            }
            (this.buf.get()).writenl();
            (this.buf.get()).writeByte(123);
            (this.buf.get()).writenl();
            (this.buf.get()).level++;
            if (c.handler != null)
            {
                c.handler.accept(this);
            }
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
        public Ptr<OutBuffer> buf = null;
        public Ptr<HdrGenState> hgs = null;
        public  DsymbolPrettyPrintVisitor(Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
            this.buf = buf;
            this.hgs = hgs;
        }

        public  void visit(Dsymbol s) {
            (this.buf.get()).writestring(s.toChars());
        }

        public  void visit(StaticAssert s) {
            (this.buf.get()).writestring(s.kind());
            (this.buf.get()).writeByte(40);
            expressionToBuffer(s.exp, this.buf, this.hgs);
            if (s.msg != null)
            {
                (this.buf.get()).writestring(new ByteSlice(", "));
                expressionToBuffer(s.msg, this.buf, this.hgs);
            }
            (this.buf.get()).writestring(new ByteSlice(");"));
            (this.buf.get()).writenl();
        }

        public  void visit(DebugSymbol s) {
            (this.buf.get()).writestring(new ByteSlice("debug = "));
            if (s.ident != null)
            {
                (this.buf.get()).writestring(s.ident.asString());
            }
            else
            {
                (this.buf.get()).print((long)s.level);
            }
            (this.buf.get()).writeByte(59);
            (this.buf.get()).writenl();
        }

        public  void visit(VersionSymbol s) {
            (this.buf.get()).writestring(new ByteSlice("version = "));
            if (s.ident != null)
            {
                (this.buf.get()).writestring(s.ident.asString());
            }
            else
            {
                (this.buf.get()).print((long)s.level);
            }
            (this.buf.get()).writeByte(59);
            (this.buf.get()).writenl();
        }

        public  void visit(EnumMember em) {
            if (em.type != null)
            {
                typeToBuffer(em.type, em.ident, this.buf, this.hgs);
            }
            else
            {
                (this.buf.get()).writestring(em.ident.asString());
            }
            if (em.value() != null)
            {
                (this.buf.get()).writestring(new ByteSlice(" = "));
                expressionToBuffer(em.value(), this.buf, this.hgs);
            }
        }

        public  void visit(Import imp) {
            if ((this.hgs.get()).hdrgen && (pequals(imp.id, Id.object)))
            {
                return ;
            }
            if (imp.isstatic != 0)
            {
                (this.buf.get()).writestring(new ByteSlice("static "));
            }
            (this.buf.get()).writestring(new ByteSlice("import "));
            if (imp.aliasId != null)
            {
                (this.buf.get()).printf(new BytePtr("%s = "), imp.aliasId.toChars());
            }
            if ((imp.packages != null) && ((imp.packages.get()).length != 0))
            {
                {
                    Slice<Identifier> __r1429 = (imp.packages.get()).opSlice().copy();
                    int __key1430 = 0;
                    for (; (__key1430 < __r1429.getLength());__key1430 += 1) {
                        Identifier pid = __r1429.get(__key1430);
                        (this.buf.get()).printf(new BytePtr("%s."), pid.toChars());
                    }
                }
            }
            (this.buf.get()).writestring(imp.id.asString());
            if (imp.names.length != 0)
            {
                (this.buf.get()).writestring(new ByteSlice(" : "));
                {
                    Slice<Identifier> __r1432 = imp.names.opSlice().copy();
                    int __key1431 = 0;
                    for (; (__key1431 < __r1432.getLength());__key1431 += 1) {
                        Identifier name = __r1432.get(__key1431);
                        int i = __key1431;
                        if (i != 0)
                        {
                            (this.buf.get()).writestring(new ByteSlice(", "));
                        }
                        Identifier _alias = imp.aliases.get(i);
                        if (_alias != null)
                        {
                            (this.buf.get()).printf(new BytePtr("%s = %s"), _alias.toChars(), name.toChars());
                        }
                        else
                        {
                            (this.buf.get()).writestring(name.toChars());
                        }
                    }
                }
            }
            (this.buf.get()).writeByte(59);
            (this.buf.get()).writenl();
        }

        public  void visit(AliasThis d) {
            (this.buf.get()).writestring(new ByteSlice("alias "));
            (this.buf.get()).writestring(d.ident.asString());
            (this.buf.get()).writestring(new ByteSlice(" this;\n"));
        }

        public  void visit(AttribDeclaration d) {
            if (d.decl == null)
            {
                (this.buf.get()).writeByte(59);
                (this.buf.get()).writenl();
                return ;
            }
            if (((d.decl.get()).length == 0))
            {
                (this.buf.get()).writestring(new ByteSlice("{}"));
            }
            else if ((this.hgs.get()).hdrgen && ((d.decl.get()).length == 1) && ((d.decl.get()).get(0).isUnitTestDeclaration() != null))
            {
                (this.buf.get()).writestring(new ByteSlice("{}"));
            }
            else if (((d.decl.get()).length == 1))
            {
                (d.decl.get()).get(0).accept(this);
                return ;
            }
            else
            {
                (this.buf.get()).writenl();
                (this.buf.get()).writeByte(123);
                (this.buf.get()).writenl();
                (this.buf.get()).level++;
                {
                    Slice<Dsymbol> __r1433 = (d.decl.get()).opSlice().copy();
                    int __key1434 = 0;
                    for (; (__key1434 < __r1433.getLength());__key1434 += 1) {
                        Dsymbol de = __r1433.get(__key1434);
                        de.accept(this);
                    }
                }
                (this.buf.get()).level--;
                (this.buf.get()).writeByte(125);
            }
            (this.buf.get()).writenl();
        }

        public  void visit(StorageClassDeclaration d) {
            if (stcToBuffer(this.buf, d.stc))
            {
                (this.buf.get()).writeByte(32);
            }
            this.visit((AttribDeclaration)d);
        }

        public  void visit(DeprecatedDeclaration d) {
            (this.buf.get()).writestring(new ByteSlice("deprecated("));
            expressionToBuffer(d.msg, this.buf, this.hgs);
            (this.buf.get()).writestring(new ByteSlice(") "));
            this.visit((AttribDeclaration)d);
        }

        public  void visit(LinkDeclaration d) {
            (this.buf.get()).writestring(new ByteSlice("extern ("));
            (this.buf.get()).writestring(linkageToString(d.linkage));
            (this.buf.get()).writestring(new ByteSlice(") "));
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
            (this.buf.get()).writestring(new ByteSlice("extern (C++, "));
            (this.buf.get()).writestring(s);
            (this.buf.get()).writestring(new ByteSlice(") "));
            this.visit((AttribDeclaration)d);
        }

        public  void visit(ProtDeclaration d) {
            protectionToBuffer(this.buf, d.protection);
            (this.buf.get()).writeByte(32);
            AttribDeclaration ad = d;
            if (((ad.decl.get()).length == 1) && ((ad.decl.get()).get(0).isProtDeclaration() != null))
            {
                this.visit((AttribDeclaration)(ad.decl.get()).get(0));
            }
            else
            {
                this.visit((AttribDeclaration)d);
            }
        }

        public  void visit(AlignDeclaration d) {
            (this.buf.get()).writestring(new ByteSlice("align "));
            if (d.ealign != null)
            {
                (this.buf.get()).printf(new BytePtr("(%s) "), d.ealign.toChars());
            }
            this.visit((AttribDeclaration)d);
        }

        public  void visit(AnonDeclaration d) {
            (this.buf.get()).writestring(d.isunion ? new ByteSlice("union") : new ByteSlice("struct"));
            (this.buf.get()).writenl();
            (this.buf.get()).writestring(new ByteSlice("{"));
            (this.buf.get()).writenl();
            (this.buf.get()).level++;
            if (d.decl != null)
            {
                {
                    Slice<Dsymbol> __r1435 = (d.decl.get()).opSlice().copy();
                    int __key1436 = 0;
                    for (; (__key1436 < __r1435.getLength());__key1436 += 1) {
                        Dsymbol de = __r1435.get(__key1436);
                        de.accept(this);
                    }
                }
            }
            (this.buf.get()).level--;
            (this.buf.get()).writestring(new ByteSlice("}"));
            (this.buf.get()).writenl();
        }

        public  void visit(PragmaDeclaration d) {
            (this.buf.get()).writestring(new ByteSlice("pragma ("));
            (this.buf.get()).writestring(d.ident.asString());
            if ((d.args != null) && ((d.args.get()).length != 0))
            {
                (this.buf.get()).writestring(new ByteSlice(", "));
                argsToBuffer(d.args, this.buf, this.hgs, null);
            }
            (this.buf.get()).writeByte(41);
            this.visit((AttribDeclaration)d);
        }

        public  void visit(ConditionalDeclaration d) {
            conditionToBuffer(d.condition, this.buf, this.hgs);
            if ((d.decl != null) || (d.elsedecl != null))
            {
                (this.buf.get()).writenl();
                (this.buf.get()).writeByte(123);
                (this.buf.get()).writenl();
                (this.buf.get()).level++;
                if (d.decl != null)
                {
                    {
                        Slice<Dsymbol> __r1437 = (d.decl.get()).opSlice().copy();
                        int __key1438 = 0;
                        for (; (__key1438 < __r1437.getLength());__key1438 += 1) {
                            Dsymbol de = __r1437.get(__key1438);
                            de.accept(this);
                        }
                    }
                }
                (this.buf.get()).level--;
                (this.buf.get()).writeByte(125);
                if (d.elsedecl != null)
                {
                    (this.buf.get()).writenl();
                    (this.buf.get()).writestring(new ByteSlice("else"));
                    (this.buf.get()).writenl();
                    (this.buf.get()).writeByte(123);
                    (this.buf.get()).writenl();
                    (this.buf.get()).level++;
                    {
                        Slice<Dsymbol> __r1439 = (d.elsedecl.get()).opSlice().copy();
                        int __key1440 = 0;
                        for (; (__key1440 < __r1439.getLength());__key1440 += 1) {
                            Dsymbol de = __r1439.get(__key1440);
                            de.accept(this);
                        }
                    }
                    (this.buf.get()).level--;
                    (this.buf.get()).writeByte(125);
                }
            }
            else
            {
                (this.buf.get()).writeByte(58);
            }
            (this.buf.get()).writenl();
        }

        public  void visit(StaticForeachDeclaration s) {
            Function1<ForeachStatement,Void> foreachWithoutBody = new Function1<ForeachStatement,Void>(){
                public Void invoke(ForeachStatement s) {
                    (buf.get()).writestring(Token.asString(s.op));
                    (buf.get()).writestring(new ByteSlice(" ("));
                    {
                        Slice<Parameter> __r1442 = (s.parameters.get()).opSlice().copy();
                        int __key1441 = 0;
                        for (; (__key1441 < __r1442.getLength());__key1441 += 1) {
                            Parameter p = __r1442.get(__key1441);
                            int i = __key1441;
                            if (i != 0)
                            {
                                (buf.get()).writestring(new ByteSlice(", "));
                            }
                            if (stcToBuffer(buf, p.storageClass))
                            {
                                (buf.get()).writeByte(32);
                            }
                            if (p.type != null)
                            {
                                typeToBuffer(p.type, p.ident, buf, hgs);
                            }
                            else
                            {
                                (buf.get()).writestring(p.ident.asString());
                            }
                        }
                    }
                    (buf.get()).writestring(new ByteSlice("; "));
                    expressionToBuffer(s.aggr.value, buf, hgs);
                    (buf.get()).writeByte(41);
                    (buf.get()).writenl();
                    return null;
                }
            };
            Function1<ForeachRangeStatement,Void> foreachRangeWithoutBody = new Function1<ForeachRangeStatement,Void>(){
                public Void invoke(ForeachRangeStatement s) {
                    (buf.get()).writestring(Token.asString(s.op));
                    (buf.get()).writestring(new ByteSlice(" ("));
                    if (s.prm.type != null)
                    {
                        typeToBuffer(s.prm.type, s.prm.ident, buf, hgs);
                    }
                    else
                    {
                        (buf.get()).writestring(s.prm.ident.asString());
                    }
                    (buf.get()).writestring(new ByteSlice("; "));
                    expressionToBuffer(s.lwr, buf, hgs);
                    (buf.get()).writestring(new ByteSlice(" .. "));
                    expressionToBuffer(s.upr, buf, hgs);
                    (buf.get()).writeByte(41);
                    (buf.get()).writenl();
                    return null;
                }
            };
            (this.buf.get()).writestring(new ByteSlice("static "));
            if (s.sfe.aggrfe != null)
            {
                foreachWithoutBody.invoke(s.sfe.aggrfe);
            }
            else
            {
                assert(s.sfe.rangefe != null);
                foreachRangeWithoutBody.invoke(s.sfe.rangefe);
            }
            (this.buf.get()).writeByte(123);
            (this.buf.get()).writenl();
            (this.buf.get()).level++;
            this.visit((AttribDeclaration)s);
            (this.buf.get()).level--;
            (this.buf.get()).writeByte(125);
            (this.buf.get()).writenl();
        }

        public  void visit(CompileDeclaration d) {
            (this.buf.get()).writestring(new ByteSlice("mixin("));
            argsToBuffer(d.exps, this.buf, this.hgs, null);
            (this.buf.get()).writestring(new ByteSlice(");"));
            (this.buf.get()).writenl();
        }

        public  void visit(UserAttributeDeclaration d) {
            (this.buf.get()).writestring(new ByteSlice("@("));
            argsToBuffer(d.atts, this.buf, this.hgs, null);
            (this.buf.get()).writeByte(41);
            this.visit((AttribDeclaration)d);
        }

        public  void visit(TemplateDeclaration d) {
            if ((this.hgs.get()).hdrgen || (this.hgs.get()).fullDump && this.visitEponymousMember(d))
            {
                return ;
            }
            if ((this.hgs.get()).ddoc)
            {
                (this.buf.get()).writestring(d.kind());
            }
            else
            {
                (this.buf.get()).writestring(new ByteSlice("template"));
            }
            (this.buf.get()).writeByte(32);
            (this.buf.get()).writestring(d.ident.asString());
            (this.buf.get()).writeByte(40);
            this.visitTemplateParameters((this.hgs.get()).ddoc ? d.origParameters : d.parameters);
            (this.buf.get()).writeByte(41);
            this.visitTemplateConstraint(d.constraint);
            if ((this.hgs.get()).hdrgen || (this.hgs.get()).fullDump)
            {
                (this.hgs.get()).tpltMember++;
                (this.buf.get()).writenl();
                (this.buf.get()).writeByte(123);
                (this.buf.get()).writenl();
                (this.buf.get()).level++;
                {
                    Slice<Dsymbol> __r1443 = (d.members.get()).opSlice().copy();
                    int __key1444 = 0;
                    for (; (__key1444 < __r1443.getLength());__key1444 += 1) {
                        Dsymbol s = __r1443.get(__key1444);
                        s.accept(this);
                    }
                }
                (this.buf.get()).level--;
                (this.buf.get()).writeByte(125);
                (this.buf.get()).writenl();
                (this.hgs.get()).tpltMember--;
            }
        }

        public  boolean visitEponymousMember(TemplateDeclaration d) {
            if ((d.members == null) || ((d.members.get()).length != 1))
            {
                return false;
            }
            Dsymbol onemember = (d.members.get()).get(0);
            if ((!pequals(onemember.ident, d.ident)))
            {
                return false;
            }
            {
                FuncDeclaration fd = onemember.isFuncDeclaration();
                if ((fd) != null)
                {
                    assert(fd.type != null);
                    if (stcToBuffer(this.buf, fd.storage_class))
                    {
                        (this.buf.get()).writeByte(32);
                    }
                    functionToBufferFull((TypeFunction)fd.type, this.buf, d.ident, this.hgs, d);
                    this.visitTemplateConstraint(d.constraint);
                    (this.hgs.get()).tpltMember++;
                    this.bodyToBuffer(fd);
                    (this.hgs.get()).tpltMember--;
                    return true;
                }
            }
            {
                AggregateDeclaration ad = onemember.isAggregateDeclaration();
                if ((ad) != null)
                {
                    (this.buf.get()).writestring(ad.kind());
                    (this.buf.get()).writeByte(32);
                    (this.buf.get()).writestring(ad.ident.asString());
                    (this.buf.get()).writeByte(40);
                    this.visitTemplateParameters((this.hgs.get()).ddoc ? d.origParameters : d.parameters);
                    (this.buf.get()).writeByte(41);
                    this.visitTemplateConstraint(d.constraint);
                    this.visitBaseClasses(ad.isClassDeclaration());
                    (this.hgs.get()).tpltMember++;
                    if (ad.members != null)
                    {
                        (this.buf.get()).writenl();
                        (this.buf.get()).writeByte(123);
                        (this.buf.get()).writenl();
                        (this.buf.get()).level++;
                        {
                            Slice<Dsymbol> __r1445 = (ad.members.get()).opSlice().copy();
                            int __key1446 = 0;
                            for (; (__key1446 < __r1445.getLength());__key1446 += 1) {
                                Dsymbol s = __r1445.get(__key1446);
                                s.accept(this);
                            }
                        }
                        (this.buf.get()).level--;
                        (this.buf.get()).writeByte(125);
                    }
                    else
                    {
                        (this.buf.get()).writeByte(59);
                    }
                    (this.buf.get()).writenl();
                    (this.hgs.get()).tpltMember--;
                    return true;
                }
            }
            {
                VarDeclaration vd = onemember.isVarDeclaration();
                if ((vd) != null)
                {
                    if (d.constraint != null)
                    {
                        return false;
                    }
                    if (stcToBuffer(this.buf, vd.storage_class))
                    {
                        (this.buf.get()).writeByte(32);
                    }
                    if (vd.type != null)
                    {
                        typeToBuffer(vd.type, vd.ident, this.buf, this.hgs);
                    }
                    else
                    {
                        (this.buf.get()).writestring(vd.ident.asString());
                    }
                    (this.buf.get()).writeByte(40);
                    this.visitTemplateParameters((this.hgs.get()).ddoc ? d.origParameters : d.parameters);
                    (this.buf.get()).writeByte(41);
                    if (vd._init != null)
                    {
                        (this.buf.get()).writestring(new ByteSlice(" = "));
                        ExpInitializer ie = vd._init.isExpInitializer();
                        if ((ie != null) && ((ie.exp.op & 0xFF) == 95) || ((ie.exp.op & 0xFF) == 96))
                        {
                            expressionToBuffer(((AssignExp)ie.exp).e2.value, this.buf, this.hgs);
                        }
                        else
                        {
                            initializerToBuffer(vd._init, this.buf, this.hgs);
                        }
                    }
                    (this.buf.get()).writeByte(59);
                    (this.buf.get()).writenl();
                    return true;
                }
            }
            return false;
        }

        public  void visitTemplateParameters(Ptr<DArray<TemplateParameter>> parameters) {
            if ((parameters == null) || ((parameters.get()).length == 0))
            {
                return ;
            }
            {
                Slice<TemplateParameter> __r1448 = (parameters.get()).opSlice().copy();
                int __key1447 = 0;
                for (; (__key1447 < __r1448.getLength());__key1447 += 1) {
                    TemplateParameter p = __r1448.get(__key1447);
                    int i = __key1447;
                    if (i != 0)
                    {
                        (this.buf.get()).writestring(new ByteSlice(", "));
                    }
                    templateParameterToBuffer(p, this.buf, this.hgs);
                }
            }
        }

        public  void visitTemplateConstraint(Expression constraint) {
            if (constraint == null)
            {
                return ;
            }
            (this.buf.get()).writestring(new ByteSlice(" if ("));
            expressionToBuffer(constraint, this.buf, this.hgs);
            (this.buf.get()).writeByte(41);
        }

        public  void visit(TemplateInstance ti) {
            (this.buf.get()).writestring(ti.name.toChars());
            tiargsToBuffer(ti, this.buf, this.hgs);
            if ((this.hgs.get()).fullDump)
            {
                (this.buf.get()).writenl();
                dumpTemplateInstance(ti, this.buf, this.hgs);
            }
        }

        public  void visit(TemplateMixin tm) {
            (this.buf.get()).writestring(new ByteSlice("mixin "));
            typeToBuffer(tm.tqual, null, this.buf, this.hgs);
            tiargsToBuffer(tm, this.buf, this.hgs);
            if ((tm.ident != null) && (memcmp(tm.ident.toChars(), new BytePtr("__mixin"), 7) != 0))
            {
                (this.buf.get()).writeByte(32);
                (this.buf.get()).writestring(tm.ident.asString());
            }
            (this.buf.get()).writeByte(59);
            (this.buf.get()).writenl();
            if ((this.hgs.get()).fullDump)
            {
                dumpTemplateInstance(tm, this.buf, this.hgs);
            }
        }

        public  void visit(EnumDeclaration d) {
            EnumDeclaration oldInEnumDecl = (this.hgs.get()).inEnumDecl;
            try {
                (this.hgs.get()).inEnumDecl = d;
                (this.buf.get()).writestring(new ByteSlice("enum "));
                if (d.ident != null)
                {
                    (this.buf.get()).writestring(d.ident.asString());
                    (this.buf.get()).writeByte(32);
                }
                if (d.memtype != null)
                {
                    (this.buf.get()).writestring(new ByteSlice(": "));
                    typeToBuffer(d.memtype, null, this.buf, this.hgs);
                }
                if (d.members == null)
                {
                    (this.buf.get()).writeByte(59);
                    (this.buf.get()).writenl();
                    return ;
                }
                (this.buf.get()).writenl();
                (this.buf.get()).writeByte(123);
                (this.buf.get()).writenl();
                (this.buf.get()).level++;
                {
                    Slice<Dsymbol> __r1449 = (d.members.get()).opSlice().copy();
                    int __key1450 = 0;
                    for (; (__key1450 < __r1449.getLength());__key1450 += 1) {
                        Dsymbol em = __r1449.get(__key1450);
                        if (em == null)
                        {
                            continue;
                        }
                        em.accept(this);
                        (this.buf.get()).writeByte(44);
                        (this.buf.get()).writenl();
                    }
                }
                (this.buf.get()).level--;
                (this.buf.get()).writeByte(125);
                (this.buf.get()).writenl();
            }
            finally {
                (this.hgs.get()).inEnumDecl = oldInEnumDecl;
            }
        }

        public  void visit(Nspace d) {
            (this.buf.get()).writestring(new ByteSlice("extern (C++, "));
            (this.buf.get()).writestring(d.ident.asString());
            (this.buf.get()).writeByte(41);
            (this.buf.get()).writenl();
            (this.buf.get()).writeByte(123);
            (this.buf.get()).writenl();
            (this.buf.get()).level++;
            {
                Slice<Dsymbol> __r1451 = (d.members.get()).opSlice().copy();
                int __key1452 = 0;
                for (; (__key1452 < __r1451.getLength());__key1452 += 1) {
                    Dsymbol s = __r1451.get(__key1452);
                    s.accept(this);
                }
            }
            (this.buf.get()).level--;
            (this.buf.get()).writeByte(125);
            (this.buf.get()).writenl();
        }

        public  void visit(StructDeclaration d) {
            (this.buf.get()).writestring(d.kind());
            (this.buf.get()).writeByte(32);
            if (!d.isAnonymous())
            {
                (this.buf.get()).writestring(d.toChars());
            }
            if (d.members == null)
            {
                (this.buf.get()).writeByte(59);
                (this.buf.get()).writenl();
                return ;
            }
            (this.buf.get()).writenl();
            (this.buf.get()).writeByte(123);
            (this.buf.get()).writenl();
            (this.buf.get()).level++;
            {
                Slice<Dsymbol> __r1453 = (d.members.get()).opSlice().copy();
                int __key1454 = 0;
                for (; (__key1454 < __r1453.getLength());__key1454 += 1) {
                    Dsymbol s = __r1453.get(__key1454);
                    s.accept(this);
                }
            }
            (this.buf.get()).level--;
            (this.buf.get()).writeByte(125);
            (this.buf.get()).writenl();
        }

        public  void visit(ClassDeclaration d) {
            if (!d.isAnonymous())
            {
                (this.buf.get()).writestring(d.kind());
                (this.buf.get()).writeByte(32);
                (this.buf.get()).writestring(d.ident.asString());
            }
            this.visitBaseClasses(d);
            if (d.members != null)
            {
                (this.buf.get()).writenl();
                (this.buf.get()).writeByte(123);
                (this.buf.get()).writenl();
                (this.buf.get()).level++;
                {
                    Slice<Dsymbol> __r1455 = (d.members.get()).opSlice().copy();
                    int __key1456 = 0;
                    for (; (__key1456 < __r1455.getLength());__key1456 += 1) {
                        Dsymbol s = __r1455.get(__key1456);
                        s.accept(this);
                    }
                }
                (this.buf.get()).level--;
                (this.buf.get()).writeByte(125);
            }
            else
            {
                (this.buf.get()).writeByte(59);
            }
            (this.buf.get()).writenl();
        }

        public  void visitBaseClasses(ClassDeclaration d) {
            if ((d == null) || ((d.baseclasses.get()).length == 0))
            {
                return ;
            }
            if (!d.isAnonymous())
            {
                (this.buf.get()).writestring(new ByteSlice(" : "));
            }
            {
                Slice<Ptr<BaseClass>> __r1458 = (d.baseclasses.get()).opSlice().copy();
                int __key1457 = 0;
                for (; (__key1457 < __r1458.getLength());__key1457 += 1) {
                    Ptr<BaseClass> b = __r1458.get(__key1457);
                    int i = __key1457;
                    if (i != 0)
                    {
                        (this.buf.get()).writestring(new ByteSlice(", "));
                    }
                    typeToBuffer((b.get()).type, null, this.buf, this.hgs);
                }
            }
        }

        public  void visit(AliasDeclaration d) {
            if ((d.storage_class & 2251799813685248L) != 0)
            {
                return ;
            }
            (this.buf.get()).writestring(new ByteSlice("alias "));
            if (d.aliassym != null)
            {
                (this.buf.get()).writestring(d.ident.asString());
                (this.buf.get()).writestring(new ByteSlice(" = "));
                if (stcToBuffer(this.buf, d.storage_class))
                {
                    (this.buf.get()).writeByte(32);
                }
                d.aliassym.accept(this);
            }
            else if (((d.type.ty & 0xFF) == ENUMTY.Tfunction))
            {
                if (stcToBuffer(this.buf, d.storage_class))
                {
                    (this.buf.get()).writeByte(32);
                }
                typeToBuffer(d.type, d.ident, this.buf, this.hgs);
            }
            else if (d.ident != null)
            {
                (this.hgs.get()).declstring = (pequals(d.ident, Id.string)) || (pequals(d.ident, Id.wstring)) || (pequals(d.ident, Id.dstring));
                (this.buf.get()).writestring(d.ident.asString());
                (this.buf.get()).writestring(new ByteSlice(" = "));
                if (stcToBuffer(this.buf, d.storage_class))
                {
                    (this.buf.get()).writeByte(32);
                }
                typeToBuffer(d.type, null, this.buf, this.hgs);
                (this.hgs.get()).declstring = false;
            }
            (this.buf.get()).writeByte(59);
            (this.buf.get()).writenl();
        }

        public  void visit(VarDeclaration d) {
            if ((d.storage_class & 2251799813685248L) != 0)
            {
                return ;
            }
            this.visitVarDecl(d, false);
            (this.buf.get()).writeByte(59);
            (this.buf.get()).writenl();
        }

        public  void visitVarDecl(VarDeclaration v, boolean anywritten) {
            if (anywritten)
            {
                (this.buf.get()).writestring(new ByteSlice(", "));
                (this.buf.get()).writestring(v.ident.asString());
            }
            else
            {
                if (stcToBuffer(this.buf, v.storage_class))
                {
                    (this.buf.get()).writeByte(32);
                }
                if (v.type != null)
                {
                    typeToBuffer(v.type, v.ident, this.buf, this.hgs);
                }
                else
                {
                    (this.buf.get()).writestring(v.ident.asString());
                }
            }
            if (v._init != null)
            {
                (this.buf.get()).writestring(new ByteSlice(" = "));
                ExpInitializer ie = v._init.isExpInitializer();
                if ((ie != null) && ((ie.exp.op & 0xFF) == 95) || ((ie.exp.op & 0xFF) == 96))
                {
                    expressionToBuffer(((AssignExp)ie.exp).e2.value, this.buf, this.hgs);
                }
                else
                {
                    initializerToBuffer(v._init, this.buf, this.hgs);
                }
            }
        }

        public  void visit(FuncDeclaration f) {
            if (stcToBuffer(this.buf, f.storage_class))
            {
                (this.buf.get()).writeByte(32);
            }
            TypeFunction tf = (TypeFunction)f.type;
            typeToBuffer(tf, f.ident, this.buf, this.hgs);
            if ((this.hgs.get()).hdrgen)
            {
                if ((tf.next.value == null) || ((f.storage_class & 256L) != 0))
                {
                    (this.hgs.get()).autoMember++;
                    this.bodyToBuffer(f);
                    (this.hgs.get()).autoMember--;
                }
                else if (((this.hgs.get()).tpltMember == 0) && global.value.params.hdrStripPlainFunctions)
                {
                    (this.buf.get()).writeByte(59);
                    (this.buf.get()).writenl();
                }
                else
                {
                    this.bodyToBuffer(f);
                }
            }
            else
            {
                this.bodyToBuffer(f);
            }
        }

        public  void bodyToBuffer(FuncDeclaration f) {
            if ((f.fbody == null) || (this.hgs.get()).hdrgen && global.value.params.hdrStripPlainFunctions && ((this.hgs.get()).autoMember == 0) && ((this.hgs.get()).tpltMember == 0))
            {
                (this.buf.get()).writeByte(59);
                (this.buf.get()).writenl();
                return ;
            }
            int savetlpt = (this.hgs.get()).tpltMember;
            int saveauto = (this.hgs.get()).autoMember;
            (this.hgs.get()).tpltMember = 0;
            (this.hgs.get()).autoMember = 0;
            (this.buf.get()).writenl();
            boolean requireDo = false;
            if (f.frequires != null)
            {
                {
                    Slice<Statement> __r1459 = (f.frequires.get()).opSlice().copy();
                    int __key1460 = 0;
                    for (; (__key1460 < __r1459.getLength());__key1460 += 1) {
                        Statement frequire = __r1459.get(__key1460);
                        (this.buf.get()).writestring(new ByteSlice("in"));
                        {
                            ExpStatement es = frequire.isExpStatement();
                            if ((es) != null)
                            {
                                assert((es.exp != null) && ((es.exp.op & 0xFF) == 14));
                                (this.buf.get()).writestring(new ByteSlice(" ("));
                                expressionToBuffer(((AssertExp)es.exp).e1.value, this.buf, this.hgs);
                                (this.buf.get()).writeByte(41);
                                (this.buf.get()).writenl();
                                requireDo = false;
                            }
                            else
                            {
                                (this.buf.get()).writenl();
                                statementToBuffer(frequire, this.buf, this.hgs);
                                requireDo = true;
                            }
                        }
                    }
                }
            }
            if (f.fensures != null)
            {
                {
                    Slice<Ensure> __r1461 = (f.fensures.get()).opSlice().copy();
                    int __key1462 = 0;
                    for (; (__key1462 < __r1461.getLength());__key1462 += 1) {
                        Ensure fensure = __r1461.get(__key1462).copy();
                        (this.buf.get()).writestring(new ByteSlice("out"));
                        {
                            ExpStatement es = fensure.ensure.isExpStatement();
                            if ((es) != null)
                            {
                                assert((es.exp != null) && ((es.exp.op & 0xFF) == 14));
                                (this.buf.get()).writestring(new ByteSlice(" ("));
                                if (fensure.id != null)
                                {
                                    (this.buf.get()).writestring(fensure.id.asString());
                                }
                                (this.buf.get()).writestring(new ByteSlice("; "));
                                expressionToBuffer(((AssertExp)es.exp).e1.value, this.buf, this.hgs);
                                (this.buf.get()).writeByte(41);
                                (this.buf.get()).writenl();
                                requireDo = false;
                            }
                            else
                            {
                                if (fensure.id != null)
                                {
                                    (this.buf.get()).writeByte(40);
                                    (this.buf.get()).writestring(fensure.id.asString());
                                    (this.buf.get()).writeByte(41);
                                }
                                (this.buf.get()).writenl();
                                statementToBuffer(fensure.ensure, this.buf, this.hgs);
                                requireDo = true;
                            }
                        }
                    }
                }
            }
            if (requireDo)
            {
                (this.buf.get()).writestring(new ByteSlice("do"));
                (this.buf.get()).writenl();
            }
            (this.buf.get()).writeByte(123);
            (this.buf.get()).writenl();
            (this.buf.get()).level++;
            statementToBuffer(f.fbody, this.buf, this.hgs);
            (this.buf.get()).level--;
            (this.buf.get()).writeByte(125);
            (this.buf.get()).writenl();
            (this.hgs.get()).tpltMember = savetlpt;
            (this.hgs.get()).autoMember = saveauto;
        }

        public  void visit(FuncLiteralDeclaration f) {
            if (((f.type.ty & 0xFF) == ENUMTY.Terror))
            {
                (this.buf.get()).writestring(new ByteSlice("__error"));
                return ;
            }
            if (((f.tok & 0xFF) != 0))
            {
                (this.buf.get()).writestring(f.kind());
                (this.buf.get()).writeByte(32);
            }
            TypeFunction tf = (TypeFunction)f.type;
            if (!f.inferRetType && (tf.next.value != null))
            {
                typeToBuffer(tf.next.value, null, this.buf, this.hgs);
            }
            parametersToBuffer(tf.parameterList, this.buf, this.hgs);
            CompoundStatement cs = f.fbody.isCompoundStatement();
            Statement s1 = null;
            if ((f.semanticRun >= PASS.semantic3done) && (cs != null))
            {
                s1 = (cs.statements.get()).get((cs.statements.get()).length - 1);
            }
            else
            {
                s1 = cs == null ? f.fbody : null;
            }
            ReturnStatement rs = s1 != null ? s1.isReturnStatement() : null;
            if ((rs != null) && (rs.exp != null))
            {
                (this.buf.get()).writestring(new ByteSlice(" => "));
                expressionToBuffer(rs.exp, this.buf, this.hgs);
            }
            else
            {
                (this.hgs.get()).tpltMember++;
                this.bodyToBuffer(f);
                (this.hgs.get()).tpltMember--;
            }
        }

        public  void visit(PostBlitDeclaration d) {
            if (stcToBuffer(this.buf, d.storage_class))
            {
                (this.buf.get()).writeByte(32);
            }
            (this.buf.get()).writestring(new ByteSlice("this(this)"));
            this.bodyToBuffer(d);
        }

        public  void visit(DtorDeclaration d) {
            if ((d.storage_class & 17179869184L) != 0)
            {
                (this.buf.get()).writestring(new ByteSlice("@trusted "));
            }
            if ((d.storage_class & 8589934592L) != 0)
            {
                (this.buf.get()).writestring(new ByteSlice("@safe "));
            }
            if ((d.storage_class & 4398046511104L) != 0)
            {
                (this.buf.get()).writestring(new ByteSlice("@nogc "));
            }
            if ((d.storage_class & 137438953472L) != 0)
            {
                (this.buf.get()).writestring(new ByteSlice("@disable "));
            }
            (this.buf.get()).writestring(new ByteSlice("~this()"));
            this.bodyToBuffer(d);
        }

        public  void visit(StaticCtorDeclaration d) {
            if (stcToBuffer(this.buf, d.storage_class & -2L))
            {
                (this.buf.get()).writeByte(32);
            }
            if (d.isSharedStaticCtorDeclaration() != null)
            {
                (this.buf.get()).writestring(new ByteSlice("shared "));
            }
            (this.buf.get()).writestring(new ByteSlice("static this()"));
            if ((this.hgs.get()).hdrgen && ((this.hgs.get()).tpltMember == 0))
            {
                (this.buf.get()).writeByte(59);
                (this.buf.get()).writenl();
            }
            else
            {
                this.bodyToBuffer(d);
            }
        }

        public  void visit(StaticDtorDeclaration d) {
            if (stcToBuffer(this.buf, d.storage_class & -2L))
            {
                (this.buf.get()).writeByte(32);
            }
            if (d.isSharedStaticDtorDeclaration() != null)
            {
                (this.buf.get()).writestring(new ByteSlice("shared "));
            }
            (this.buf.get()).writestring(new ByteSlice("static ~this()"));
            if ((this.hgs.get()).hdrgen && ((this.hgs.get()).tpltMember == 0))
            {
                (this.buf.get()).writeByte(59);
                (this.buf.get()).writenl();
            }
            else
            {
                this.bodyToBuffer(d);
            }
        }

        public  void visit(InvariantDeclaration d) {
            if ((this.hgs.get()).hdrgen)
            {
                return ;
            }
            if (stcToBuffer(this.buf, d.storage_class))
            {
                (this.buf.get()).writeByte(32);
            }
            (this.buf.get()).writestring(new ByteSlice("invariant"));
            {
                ExpStatement es = d.fbody.isExpStatement();
                if ((es) != null)
                {
                    assert((es.exp != null) && ((es.exp.op & 0xFF) == 14));
                    (this.buf.get()).writestring(new ByteSlice(" ("));
                    expressionToBuffer(((AssertExp)es.exp).e1.value, this.buf, this.hgs);
                    (this.buf.get()).writestring(new ByteSlice(");"));
                    (this.buf.get()).writenl();
                }
                else
                {
                    this.bodyToBuffer(d);
                }
            }
        }

        public  void visit(UnitTestDeclaration d) {
            if ((this.hgs.get()).hdrgen)
            {
                return ;
            }
            if (stcToBuffer(this.buf, d.storage_class))
            {
                (this.buf.get()).writeByte(32);
            }
            (this.buf.get()).writestring(new ByteSlice("unittest"));
            this.bodyToBuffer(d);
        }

        public  void visit(NewDeclaration d) {
            if (stcToBuffer(this.buf, d.storage_class & -2L))
            {
                (this.buf.get()).writeByte(32);
            }
            (this.buf.get()).writestring(new ByteSlice("new"));
            parametersToBuffer(new ParameterList(d.parameters, d.varargs), this.buf, this.hgs);
            this.bodyToBuffer(d);
        }

        public  void visit(DeleteDeclaration d) {
            if (stcToBuffer(this.buf, d.storage_class & -2L))
            {
                (this.buf.get()).writeByte(32);
            }
            (this.buf.get()).writestring(new ByteSlice("delete"));
            parametersToBuffer(new ParameterList(d.parameters, VarArg.none), this.buf, this.hgs);
            this.bodyToBuffer(d);
        }

        public  void visit(dmodule.Module m) {
            moduleToBuffer2(m, this.buf, this.hgs);
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
            (this.buf.get()).writestring(Token.asString(e.op));
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
                        switch (__dispatch1 != 0 ? __dispatch1 : (t.ty & 0xFF))
                        {
                            case 9:
                                TypeEnum te = (TypeEnum)t;
                                if ((this.hgs.get()).fullDump)
                                {
                                    EnumDeclaration sym = te.sym;
                                    if ((!pequals((this.hgs.get()).inEnumDecl, sym)))
                                    {
                                        int __key1463 = 0;
                                        int __limit1464 = (sym.members.get()).length;
                                        for (; (__key1463 < __limit1464);__key1463 += 1) {
                                            int i = __key1463;
                                            EnumMember em = (EnumMember)(sym.members.get()).get(i);
                                            if ((em.value().toInteger() == v))
                                            {
                                                (this.buf.get()).printf(new BytePtr("%s.%s"), sym.toChars(), em.ident.toChars());
                                                return ;
                                            }
                                        }
                                    }
                                }
                                (this.buf.get()).printf(new BytePtr("cast(%s)"), te.sym.toChars());
                                t = te.sym.memtype;
                                /*goto L1*/throw Dispatch0.INSTANCE;
                            case 32:
                            case 33:
                                if ((v > 255L))
                                {
                                    (this.buf.get()).printf(new BytePtr("'\\U%08x'"), v);
                                    break;
                                }
                            case 31:
                                int o = (this.buf.get()).offset;
                                if ((v == 39L))
                                {
                                    (this.buf.get()).writestring(new ByteSlice("'\\''"));
                                }
                                else if ((isprint((int)v) != 0) && (v != 92L))
                                {
                                    (this.buf.get()).printf(new BytePtr("'%c'"), (int)v);
                                }
                                else
                                {
                                    (this.buf.get()).printf(new BytePtr("'\\x%02x'"), (int)v);
                                }
                                if ((this.hgs.get()).ddoc)
                                {
                                    escapeDdocString(this.buf, o);
                                }
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
                                if ((target.value.ptrsize == 8))
                                {
                                    /*goto case*/{ __dispatch1 = 20; continue dispatched_1; }
                                }
                                else
                                {
                                    /*goto case*/{ __dispatch1 = 18; continue dispatched_1; }
                                }
                            default:
                            if (global.value.errors == 0)
                            {
                                throw new AssertionError("Unreachable code!");
                            }
                            break;
                        }
                    } while(__dispatch1 != 0);
                }
            }
            else if ((v & -9223372036854775808L) != 0)
            {
                (this.buf.get()).printf(new BytePtr("0x%llx"), v);
            }
            else
            {
                (this.buf.get()).print(v);
            }
        }

        public  void visit(ErrorExp e) {
            (this.buf.get()).writestring(new ByteSlice("__error"));
        }

        public  void visit(VoidInitExp e) {
            (this.buf.get()).writestring(new ByteSlice("__void"));
        }

        public  void floatToBuffer(Type type, double value) {
            int BUFFER_LEN = 58;
            Ref<ByteSlice> buffer = ref((byte)255);
            CTFloat.sprint(ptr(buffer), (byte)103, value);
            assert((strlen(ptr(buffer)) < 58));
            if ((this.hgs.get()).hdrgen)
            {
                double r = CTFloat.parse(ptr(buffer), null);
                if ((r != value))
                {
                    CTFloat.sprint(ptr(buffer), (byte)97, value);
                }
            }
            (this.buf.get()).writestring(ptr(buffer));
            if (type != null)
            {
                Type t = type.toBasetype();
                switch ((t.ty & 0xFF))
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
                {
                    (this.buf.get()).writeByte(105);
                }
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
            if ((this.hgs.get()).hdrgen || (this.hgs.get()).ddoc)
            {
                (this.buf.get()).writestring(e.ident.toHChars2());
            }
            else
            {
                (this.buf.get()).writestring(e.ident.asString());
            }
        }

        public  void visit(DsymbolExp e) {
            (this.buf.get()).writestring(e.s.toChars());
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
            int o = (this.buf.get()).offset;
            {
                int i = 0;
            L_outer1:
                for (; (i < e.len);i++){
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
                                    {
                                        (this.buf.get()).writeByte(c);
                                    }
                                    else
                                    {
                                        (this.buf.get()).printf(new BytePtr("\\x%02x"), c);
                                    }
                                }
                                else if ((c <= 65535))
                                {
                                    (this.buf.get()).printf(new BytePtr("\\x%02x\\x%02x"), c & 255, c >> 8);
                                }
                                else
                                {
                                    (this.buf.get()).printf(new BytePtr("\\x%02x\\x%02x\\x%02x\\x%02x"), c & 255, c >> 8 & 255, c >> 16 & 255, c >> 24);
                                }
                                break;
                            }
                        } while(__dispatch3 != 0);
                    }
                }
            }
            if ((this.hgs.get()).ddoc)
            {
                escapeDdocString(this.buf, o);
            }
            (this.buf.get()).writeByte(34);
            if (e.postfix != 0)
            {
                (this.buf.get()).writeByte((e.postfix & 0xFF));
            }
        }

        public  void visit(ArrayLiteralExp e) {
            (this.buf.get()).writeByte(91);
            argsToBuffer(e.elements, this.buf, this.hgs, e.basis.value);
            (this.buf.get()).writeByte(93);
        }

        public  void visit(AssocArrayLiteralExp e) {
            (this.buf.get()).writeByte(91);
            {
                Slice<Expression> __r1466 = (e.keys.get()).opSlice().copy();
                int __key1465 = 0;
                for (; (__key1465 < __r1466.getLength());__key1465 += 1) {
                    Expression key = __r1466.get(__key1465);
                    int i = __key1465;
                    if (i != 0)
                    {
                        (this.buf.get()).writestring(new ByteSlice(", "));
                    }
                    expToBuffer(key, PREC.assign, this.buf, this.hgs);
                    (this.buf.get()).writeByte(58);
                    Expression value = (e.values.get()).get(i);
                    expToBuffer(value, PREC.assign, this.buf, this.hgs);
                }
            }
            (this.buf.get()).writeByte(93);
        }

        public  void visit(StructLiteralExp e) {
            (this.buf.get()).writestring(e.sd.toChars());
            (this.buf.get()).writeByte(40);
            if ((e.stageflags & 32) != 0)
            {
                (this.buf.get()).writestring(new ByteSlice("<recursion>"));
            }
            else
            {
                int old = e.stageflags;
                e.stageflags |= 32;
                argsToBuffer(e.elements, this.buf, this.hgs, null);
                e.stageflags = old;
            }
            (this.buf.get()).writeByte(41);
        }

        public  void visit(TypeExp e) {
            typeToBuffer(e.type.value, null, this.buf, this.hgs);
        }

        public  void visit(ScopeExp e) {
            if (e.sds.isTemplateInstance() != null)
            {
                dsymbolToBuffer(e.sds, this.buf, this.hgs);
            }
            else if ((this.hgs != null) && (this.hgs.get()).ddoc)
            {
                {
                    dmodule.Module m = e.sds.isModule();
                    if ((m) != null)
                    {
                        (this.buf.get()).writestring((m.md.get()).toChars());
                    }
                    else
                    {
                        (this.buf.get()).writestring(e.sds.toChars());
                    }
                }
            }
            else
            {
                (this.buf.get()).writestring(e.sds.kind());
                (this.buf.get()).writeByte(32);
                (this.buf.get()).writestring(e.sds.toChars());
            }
        }

        public  void visit(TemplateExp e) {
            (this.buf.get()).writestring(e.td.toChars());
        }

        public  void visit(NewExp e) {
            if (e.thisexp.value != null)
            {
                expToBuffer(e.thisexp.value, PREC.primary, this.buf, this.hgs);
                (this.buf.get()).writeByte(46);
            }
            (this.buf.get()).writestring(new ByteSlice("new "));
            if ((e.newargs != null) && ((e.newargs.get()).length != 0))
            {
                (this.buf.get()).writeByte(40);
                argsToBuffer(e.newargs, this.buf, this.hgs, null);
                (this.buf.get()).writeByte(41);
            }
            typeToBuffer(e.newtype, null, this.buf, this.hgs);
            if ((e.arguments != null) && ((e.arguments.get()).length != 0))
            {
                (this.buf.get()).writeByte(40);
                argsToBuffer(e.arguments, this.buf, this.hgs, null);
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
            if ((e.newargs != null) && ((e.newargs.get()).length != 0))
            {
                (this.buf.get()).writeByte(40);
                argsToBuffer(e.newargs, this.buf, this.hgs, null);
                (this.buf.get()).writeByte(41);
            }
            (this.buf.get()).writestring(new ByteSlice(" class "));
            if ((e.arguments != null) && ((e.arguments.get()).length != 0))
            {
                (this.buf.get()).writeByte(40);
                argsToBuffer(e.arguments, this.buf, this.hgs, null);
                (this.buf.get()).writeByte(41);
            }
            if (e.cd != null)
            {
                dsymbolToBuffer(e.cd, this.buf, this.hgs);
            }
        }

        public  void visit(SymOffExp e) {
            if (e.offset != 0)
            {
                (this.buf.get()).printf(new BytePtr("(& %s+%u)"), e.var.toChars(), e.offset);
            }
            else if (e.var.isTypeInfoDeclaration() != null)
            {
                (this.buf.get()).writestring(e.var.toChars());
            }
            else
            {
                (this.buf.get()).printf(new BytePtr("& %s"), e.var.toChars());
            }
        }

        public  void visit(VarExp e) {
            (this.buf.get()).writestring(e.var.toChars());
        }

        public  void visit(OverExp e) {
            (this.buf.get()).writestring(e.vars.ident.asString());
        }

        public  void visit(TupleExp e) {
            if (e.e0.value != null)
            {
                (this.buf.get()).writeByte(40);
                e.e0.value.accept(this);
                (this.buf.get()).writestring(new ByteSlice(", tuple("));
                argsToBuffer(e.exps, this.buf, this.hgs, null);
                (this.buf.get()).writestring(new ByteSlice("))"));
            }
            else
            {
                (this.buf.get()).writestring(new ByteSlice("tuple("));
                argsToBuffer(e.exps, this.buf, this.hgs, null);
                (this.buf.get()).writeByte(41);
            }
        }

        public  void visit(FuncExp e) {
            dsymbolToBuffer(e.fd, this.buf, this.hgs);
        }

        public  void visit(DeclarationExp e) {
            if (e.declaration != null)
            {
                {
                    VarDeclaration var = e.declaration.isVarDeclaration();
                    if ((var) != null)
                    {
                        (this.buf.get()).writeByte(40);
                        DsymbolPrettyPrintVisitor v = new DsymbolPrettyPrintVisitor(this.buf, this.hgs);
                        v.visitVarDecl(var, false);
                        (this.buf.get()).writeByte(59);
                        (this.buf.get()).writeByte(41);
                    }
                    else
                    {
                        dsymbolToBuffer(e.declaration, this.buf, this.hgs);
                    }
                }
            }
        }

        public  void visit(TypeidExp e) {
            (this.buf.get()).writestring(new ByteSlice("typeid("));
            objectToBuffer(e.obj, this.buf, this.hgs);
            (this.buf.get()).writeByte(41);
        }

        public  void visit(TraitsExp e) {
            (this.buf.get()).writestring(new ByteSlice("__traits("));
            if (e.ident != null)
            {
                (this.buf.get()).writestring(e.ident.asString());
            }
            if (e.args != null)
            {
                {
                    Slice<RootObject> __r1467 = (e.args.get()).opSlice().copy();
                    int __key1468 = 0;
                    for (; (__key1468 < __r1467.getLength());__key1468 += 1) {
                        RootObject arg = __r1467.get(__key1468);
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
            typeToBuffer(e.targ, e.id, this.buf, this.hgs);
            if (((e.tok2 & 0xFF) != 0))
            {
                (this.buf.get()).printf(new BytePtr(" %s %s"), Token.toChars(e.tok), Token.toChars(e.tok2));
            }
            else if (e.tspec != null)
            {
                if (((e.tok & 0xFF) == 7))
                {
                    (this.buf.get()).writestring(new ByteSlice(" : "));
                }
                else
                {
                    (this.buf.get()).writestring(new ByteSlice(" == "));
                }
                typeToBuffer(e.tspec, null, this.buf, this.hgs);
            }
            if ((e.parameters != null) && ((e.parameters.get()).length != 0))
            {
                (this.buf.get()).writestring(new ByteSlice(", "));
                DsymbolPrettyPrintVisitor v = new DsymbolPrettyPrintVisitor(this.buf, this.hgs);
                v.visitTemplateParameters(e.parameters);
            }
            (this.buf.get()).writeByte(41);
        }

        public  void visit(UnaExp e) {
            (this.buf.get()).writestring(Token.asString(e.op));
            expToBuffer(e.e1.value, precedence.get((e.op & 0xFF)), this.buf, this.hgs);
        }

        public  void visit(BinExp e) {
            expToBuffer(e.e1.value, precedence.get((e.op & 0xFF)), this.buf, this.hgs);
            (this.buf.get()).writeByte(32);
            (this.buf.get()).writestring(Token.asString(e.op));
            (this.buf.get()).writeByte(32);
            expToBuffer(e.e2.value, precedence.get((e.op & 0xFF)) + 1, this.buf, this.hgs);
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
            (this.buf.get()).writestring(e.td.toChars());
        }

        public  void visit(DotVarExp e) {
            expToBuffer(e.e1.value, PREC.primary, this.buf, this.hgs);
            (this.buf.get()).writeByte(46);
            (this.buf.get()).writestring(e.var.toChars());
        }

        public  void visit(DotTemplateInstanceExp e) {
            expToBuffer(e.e1.value, PREC.primary, this.buf, this.hgs);
            (this.buf.get()).writeByte(46);
            dsymbolToBuffer(e.ti, this.buf, this.hgs);
        }

        public  void visit(DelegateExp e) {
            (this.buf.get()).writeByte(38);
            if (!e.func.isNested() || e.func.needThis())
            {
                expToBuffer(e.e1.value, PREC.primary, this.buf, this.hgs);
                (this.buf.get()).writeByte(46);
            }
            (this.buf.get()).writestring(e.func.toChars());
        }

        public  void visit(DotTypeExp e) {
            expToBuffer(e.e1.value, PREC.primary, this.buf, this.hgs);
            (this.buf.get()).writeByte(46);
            (this.buf.get()).writestring(e.sym.toChars());
        }

        public  void visit(CallExp e) {
            if (((e.e1.value.op & 0xFF) == 20))
            {
                e.e1.value.accept(this);
            }
            else
            {
                expToBuffer(e.e1.value, precedence.get((e.op & 0xFF)), this.buf, this.hgs);
            }
            (this.buf.get()).writeByte(40);
            argsToBuffer(e.arguments, this.buf, this.hgs, null);
            (this.buf.get()).writeByte(41);
        }

        public  void visit(PtrExp e) {
            (this.buf.get()).writeByte(42);
            expToBuffer(e.e1.value, precedence.get((e.op & 0xFF)), this.buf, this.hgs);
        }

        public  void visit(DeleteExp e) {
            (this.buf.get()).writestring(new ByteSlice("delete "));
            expToBuffer(e.e1.value, precedence.get((e.op & 0xFF)), this.buf, this.hgs);
        }

        public  void visit(CastExp e) {
            (this.buf.get()).writestring(new ByteSlice("cast("));
            if (e.to != null)
            {
                typeToBuffer(e.to, null, this.buf, this.hgs);
            }
            else
            {
                MODtoBuffer(this.buf, e.mod);
            }
            (this.buf.get()).writeByte(41);
            expToBuffer(e.e1.value, precedence.get((e.op & 0xFF)), this.buf, this.hgs);
        }

        public  void visit(VectorExp e) {
            (this.buf.get()).writestring(new ByteSlice("cast("));
            typeToBuffer(e.to, null, this.buf, this.hgs);
            (this.buf.get()).writeByte(41);
            expToBuffer(e.e1.value, precedence.get((e.op & 0xFF)), this.buf, this.hgs);
        }

        public  void visit(VectorArrayExp e) {
            expToBuffer(e.e1.value, PREC.primary, this.buf, this.hgs);
            (this.buf.get()).writestring(new ByteSlice(".array"));
        }

        public  void visit(SliceExp e) {
            expToBuffer(e.e1.value, precedence.get((e.op & 0xFF)), this.buf, this.hgs);
            (this.buf.get()).writeByte(91);
            if ((e.upr.value != null) || (e.lwr.value != null))
            {
                if (e.lwr.value != null)
                {
                    sizeToBuffer(e.lwr.value, this.buf, this.hgs);
                }
                else
                {
                    (this.buf.get()).writeByte(48);
                }
                (this.buf.get()).writestring(new ByteSlice(".."));
                if (e.upr.value != null)
                {
                    sizeToBuffer(e.upr.value, this.buf, this.hgs);
                }
                else
                {
                    (this.buf.get()).writeByte(36);
                }
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
            argsToBuffer(e.arguments, this.buf, this.hgs, null);
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
            expToBuffer(e.e1.value, precedence.get((e.op & 0xFF)), this.buf, this.hgs);
            (this.buf.get()).writestring(Token.asString(e.op));
        }

        public  void visit(PreExp e) {
            (this.buf.get()).writestring(Token.asString(e.op));
            expToBuffer(e.e1.value, precedence.get((e.op & 0xFF)), this.buf, this.hgs);
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
            (this.buf.get()).writestring(tp.ident.asString());
            if (tp.specType != null)
            {
                (this.buf.get()).writestring(new ByteSlice(" : "));
                typeToBuffer(tp.specType, null, this.buf, this.hgs);
            }
            if (tp.defaultType != null)
            {
                (this.buf.get()).writestring(new ByteSlice(" = "));
                typeToBuffer(tp.defaultType, null, this.buf, this.hgs);
            }
        }

        public  void visit(TemplateThisParameter tp) {
            (this.buf.get()).writestring(new ByteSlice("this "));
            this.visit((TemplateTypeParameter)tp);
        }

        public  void visit(TemplateAliasParameter tp) {
            (this.buf.get()).writestring(new ByteSlice("alias "));
            if (tp.specType != null)
            {
                typeToBuffer(tp.specType, tp.ident, this.buf, this.hgs);
            }
            else
            {
                (this.buf.get()).writestring(tp.ident.asString());
            }
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
            typeToBuffer(tp.valType, tp.ident, this.buf, this.hgs);
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
            (this.buf.get()).writestring(tp.ident.asString());
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
            {
                (this.buf.get()).writestring(c.ident.asString());
            }
            else
            {
                (this.buf.get()).print((long)c.level);
            }
            (this.buf.get()).writeByte(41);
        }

        public  void visit(VersionCondition c) {
            (this.buf.get()).writestring(new ByteSlice("version ("));
            if (c.ident != null)
            {
                (this.buf.get()).writestring(c.ident.asString());
            }
            else
            {
                (this.buf.get()).print((long)c.level);
            }
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
        hgs.value.fullQual = qualifyTypes;
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
        {
            stc_ref.value &= -524289L;
        }
        if ((stc_ref.value & 562949953421312L) != 0)
        {
            stc_ref.value &= -562949953945601L;
        }
        for (; stc_ref.value != 0;){
            ByteSlice s = stcToString(stc_ref).copy();
            if (s.getLength() == 0)
            {
                break;
            }
            if (result)
            {
                (buf.get()).writeByte(32);
            }
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
                    {
                        return new ByteSlice("__thread");
                    }
                    byte tok = hdrgen.stcToStringtable.get(i).tok;
                    if (((tok & 0xFF) != 225) && (hdrgen.stcToStringtable.get(i).id.getLength() == 0))
                    {
                        hdrgen.stcToStringtable.get(i).id = Token.asString(tok).copy();
                    }
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
        (buf.get()).writestring(protectionToString(prot.kind));
        if ((prot.kind == Prot.Kind.package_) && (prot.pkg != null))
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
        if ((arguments == null) || ((arguments.get()).length == 0))
        {
            return ;
        }
        Ref<HdrGenState> hgs = ref(new HdrGenState());
        {
            Slice<Expression> __r1470 = (arguments.get()).opSlice().copy();
            int __key1469 = 0;
            for (; (__key1469 < __r1470.getLength());__key1469 += 1) {
                Expression arg = __r1470.get(__key1469);
                int i = __key1469;
                if (i != 0)
                {
                    (buf.get()).writestring(new ByteSlice(", "));
                }
                typeToBuffer(arg.type.value, null, buf, ptr(hgs));
            }
        }
    }

    public static void toCBuffer(TemplateParameter tp, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        TemplateParameterPrettyPrintVisitor v = new TemplateParameterPrettyPrintVisitor(buf, hgs);
        tp.accept(v);
    }

    public static void arrayObjectsToBuffer(Ptr<OutBuffer> buf, Ptr<DArray<RootObject>> objects) {
        if ((objects == null) || ((objects.get()).length == 0))
        {
            return ;
        }
        Ref<HdrGenState> hgs = ref(new HdrGenState());
        {
            Slice<RootObject> __r1472 = (objects.get()).opSlice().copy();
            int __key1471 = 0;
            for (; (__key1471 < __r1472.getLength());__key1471 += 1) {
                RootObject o = __r1472.get(__key1471);
                int i = __key1471;
                if (i != 0)
                {
                    (buf.get()).writestring(new ByteSlice(", "));
                }
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
            hgs.value.fullQual = fullQual;
            parameterToBuffer(parameter, ptr(buf), ptr(hgs));
            if ((tf.parameterList.varargs == VarArg.typesafe) && (pequals(parameter, tf.parameterList.get((tf.parameterList.parameters.get()).length - 1))))
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
            int __key1473 = 0;
            int __limit1474 = pl.length();
            for (; (__key1473 < __limit1474);__key1473 += 1) {
                int i = __key1473;
                if (i != 0)
                {
                    (buf.get()).writestring(new ByteSlice(", "));
                }
                parameterToBuffer(pl.get(i), buf, hgs);
            }
        }
        {
            int __dispatch7 = 0;
            dispatched_7:
            do {
                switch (__dispatch7 != 0 ? __dispatch7 : pl.varargs)
                {
                    case VarArg.none:
                        break;
                    case VarArg.variadic:
                        if ((pl.length() == 0))
                        {
                            /*goto case*/{ __dispatch7 = VarArg.typesafe; continue dispatched_7; }
                        }
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
        if (p.userAttribDecl != null)
        {
            (buf.get()).writeByte(64);
            boolean isAnonymous = ((p.userAttribDecl.atts.get()).length > 0) && (((p.userAttribDecl.atts.get()).get(0).op & 0xFF) != 18);
            if (isAnonymous)
            {
                (buf.get()).writeByte(40);
            }
            argsToBuffer(p.userAttribDecl.atts, buf, hgs, null);
            if (isAnonymous)
            {
                (buf.get()).writeByte(41);
            }
            (buf.get()).writeByte(32);
        }
        if ((p.storageClass & 256L) != 0)
        {
            (buf.get()).writestring(new ByteSlice("auto "));
        }
        if ((p.storageClass & 17592186044416L) != 0)
        {
            (buf.get()).writestring(new ByteSlice("return "));
        }
        if ((p.storageClass & 4096L) != 0)
        {
            (buf.get()).writestring(new ByteSlice("out "));
        }
        else if ((p.storageClass & 2097152L) != 0)
        {
            (buf.get()).writestring(new ByteSlice("ref "));
        }
        else if ((p.storageClass & 2048L) != 0)
        {
            (buf.get()).writestring(new ByteSlice("in "));
        }
        else if ((p.storageClass & 8192L) != 0)
        {
            (buf.get()).writestring(new ByteSlice("lazy "));
        }
        else if ((p.storageClass & 268435456L) != 0)
        {
            (buf.get()).writestring(new ByteSlice("alias "));
        }
        long stc = p.storageClass;
        if ((p.type != null) && (((p.type.mod & 0xFF) & MODFlags.shared_) != 0))
        {
            stc &= -536870913L;
        }
        if (stcToBuffer(buf, stc & 562952639348740L))
        {
            (buf.get()).writeByte(32);
        }
        if ((p.storageClass & 268435456L) != 0)
        {
            if (p.ident != null)
            {
                (buf.get()).writestring(p.ident.asString());
            }
        }
        else if (((p.type.ty & 0xFF) == ENUMTY.Tident) && (((TypeIdentifier)p.type).ident.asString().getLength() > 3) && (strncmp(((TypeIdentifier)p.type).ident.toChars(), new BytePtr("__T"), 3) == 0))
        {
            (buf.get()).writestring(p.ident.asString());
        }
        else
        {
            typeToBuffer(p.type, p.ident, buf, hgs);
        }
        if (p.defaultArg != null)
        {
            (buf.get()).writestring(new ByteSlice(" = "));
            expToBuffer(p.defaultArg, PREC.assign, buf, hgs);
        }
    }

    public static void argsToBuffer(Ptr<DArray<Expression>> expressions, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs, Expression basis) {
        if ((expressions == null) || ((expressions.get()).length == 0))
        {
            return ;
        }
        {
            Slice<Expression> __r1476 = (expressions.get()).opSlice().copy();
            int __key1475 = 0;
            for (; (__key1475 < __r1476.getLength());__key1475 += 1) {
                Expression el = __r1476.get(__key1475);
                int i = __key1475;
                if (i != 0)
                {
                    (buf.get()).writestring(new ByteSlice(", "));
                }
                if (el == null)
                {
                    el = basis;
                }
                if (el != null)
                {
                    expToBuffer(el, PREC.assign, buf, hgs);
                }
            }
        }
    }

    // defaulted all parameters starting with #4
    public static void argsToBuffer(Ptr<DArray<Expression>> expressions, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        return argsToBuffer(expressions, buf, hgs, null);
    }

    public static void sizeToBuffer(Expression e, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        if ((pequals(e.type.value, Type.tsize_t)))
        {
            Expression ex = ((e.op & 0xFF) == 12) ? ((CastExp)e).e1.value : e;
            ex = ex.optimize(0, false);
            long uval = ((ex.op & 0xFF) == 135) ? ex.toInteger() : -1L;
            if (((long)uval >= 0L))
            {
                long sizemax = null;
                if ((target.value.ptrsize == 8))
                {
                    sizemax = -1L;
                }
                else if ((target.value.ptrsize == 4))
                {
                    sizemax = 4294967295L;
                }
                else if ((target.value.ptrsize == 2))
                {
                    sizemax = 65535L;
                }
                else
                {
                    throw new AssertionError("Unreachable code!");
                }
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
        assert((precedence.get((e.op & 0xFF)) != PREC.zero));
        assert((pr != PREC.zero));
        if ((precedence.get((e.op & 0xFF)) < pr) || (pr == PREC.rel) && (precedence.get((e.op & 0xFF)) == pr) || (pr >= PREC.or) && (pr <= PREC.and) && (precedence.get((e.op & 0xFF)) == PREC.rel))
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
        if (((modMask & 0xFF) == (t.mod & 0xFF)) || ((t.ty & 0xFF) == ENUMTY.Tfunction) || ((t.ty & 0xFF) == ENUMTY.Ttuple))
        {
            typeToBufferx(t, buf, hgs);
        }
        else
        {
            byte m = (byte)((t.mod & 0xFF) & ~((t.mod & 0xFF) & (modMask & 0xFF)));
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
            {
                (buf.get()).writeByte(41);
            }
            if (((m & 0xFF) & MODFlags.wild) != 0)
            {
                (buf.get()).writeByte(41);
            }
            if (((m & 0xFF) & MODFlags.shared_) != 0)
            {
                (buf.get()).writeByte(41);
            }
        }
    }

    public static void dumpTemplateInstance(TemplateInstance ti, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        (buf.get()).writeByte(123);
        (buf.get()).writenl();
        (buf.get()).level++;
        if (ti.aliasdecl != null)
        {
            dsymbolToBuffer(ti.aliasdecl, buf, hgs);
            (buf.get()).writenl();
        }
        else if (ti.members != null)
        {
            {
                Slice<Dsymbol> __r1477 = (ti.members.get()).opSlice().copy();
                int __key1478 = 0;
                for (; (__key1478 < __r1477.getLength());__key1478 += 1) {
                    Dsymbol m = __r1477.get(__key1478);
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
        if (ti.tiargs == null)
        {
            (buf.get()).writestring(new ByteSlice("()"));
            return ;
        }
        if (((ti.tiargs.get()).length == 1))
        {
            RootObject oarg = (ti.tiargs.get()).get(0);
            {
                Type t = isType(oarg);
                if ((t) != null)
                {
                    if (t.equals(Type.tstring) || t.equals(Type.twstring) || t.equals(Type.tdstring) || ((t.mod & 0xFF) == 0) && (t.isTypeBasic() != null) || ((t.ty & 0xFF) == ENUMTY.Tident) && (((TypeIdentifier)t).idents.length == 0))
                    {
                        (buf.get()).writestring(t.toChars());
                        return ;
                    }
                }
                else {
                    Expression e = isExpression(oarg);
                    if ((e) != null)
                    {
                        if (((e.op & 0xFF) == 135) || ((e.op & 0xFF) == 140) || ((e.op & 0xFF) == 13) || ((e.op & 0xFF) == 121) || ((e.op & 0xFF) == 123))
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
            Slice<RootObject> __r1480 = (ti.tiargs.get()).opSlice().copy();
            int __key1479 = 0;
            for (; (__key1479 < __r1480.getLength());__key1479 += 1) {
                RootObject arg = __r1480.get(__key1479);
                int i = __key1479;
                if (i != 0)
                {
                    (buf.get()).writestring(new ByteSlice(", "));
                }
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
                    if (((e.op & 0xFF) == 26))
                    {
                        e = e.optimize(0, false);
                    }
                    expToBuffer(e, PREC.assign, buf, hgs);
                }
                else {
                    Dsymbol s = isDsymbol(oarg);
                    if ((s) != null)
                    {
                        BytePtr p = pcopy(s.ident != null ? s.ident.toChars() : s.toChars());
                        (buf.get()).writestring(p);
                    }
                    else {
                        Tuple v = isTuple(oarg);
                        if ((v) != null)
                        {
                            Ptr<DArray<RootObject>> args = ptr(v.objects);
                            {
                                Slice<RootObject> __r1482 = (args.get()).opSlice().copy();
                                int __key1481 = 0;
                                for (; (__key1481 < __r1482.getLength());__key1481 += 1) {
                                    RootObject arg = __r1482.get(__key1481);
                                    int i = __key1481;
                                    if (i != 0)
                                    {
                                        (buf.get()).writestring(new ByteSlice(", "));
                                    }
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
        if (t.inuse != 0)
        {
            t.inuse = 2;
            return ;
        }
        t.inuse++;
        if ((t.linkage > LINK.d) && (((hgs.get()).ddoc ? 1 : 0) != 1) && !(hgs.get()).hdrgen)
        {
            linkageToBuffer(buf, t.linkage);
            (buf.get()).writeByte(32);
        }
        if (t.next.value != null)
        {
            typeToBuffer(t.next.value, null, buf, hgs);
            if (ident.getLength() != 0)
            {
                (buf.get()).writeByte(32);
            }
        }
        else if ((hgs.get()).ddoc)
        {
            (buf.get()).writestring(new ByteSlice("auto "));
        }
        if (ident.getLength() != 0)
        {
            (buf.get()).writestring(ident);
        }
        parametersToBuffer(t.parameterList, buf, hgs);
        if (t.mod != 0)
        {
            (buf.get()).writeByte(32);
            MODtoBuffer(buf, t.mod);
        }
        Function1<ByteSlice,Void> dg = new Function1<ByteSlice,Void>(){
            public Void invoke(ByteSlice str) {
                (buf.get()).writeByte(32);
                (buf.get()).writestring(str);
                return null;
            }
        };
        attributesApply(t, dg, TRUSTformat.TRUSTformatDefault);
        t.inuse--;
    }

    public static void visitFuncIdentWithPrefix(TypeFunction t, Identifier ident, TemplateDeclaration td, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        if (t.inuse != 0)
        {
            t.inuse = 2;
            return ;
        }
        t.inuse++;
        if (t.mod != 0)
        {
            MODtoBuffer(buf, t.mod);
            (buf.get()).writeByte(32);
        }
        Function1<ByteSlice,Void> ignoreReturn = new Function1<ByteSlice,Void>(){
            public Void invoke(ByteSlice str) {
                if (!__equals(str, new ByteSlice("return")))
                {
                    if ((pequals(ident, Id.ctor)) && __equals(str, new ByteSlice("ref")))
                    {
                        return null;
                    }
                    (buf.get()).writestring(str);
                    (buf.get()).writeByte(32);
                }
                return null;
            }
        };
        attributesApply(t, ignoreReturn, TRUSTformat.TRUSTformatDefault);
        if ((t.linkage > LINK.d) && (((hgs.get()).ddoc ? 1 : 0) != 1) && !(hgs.get()).hdrgen)
        {
            linkageToBuffer(buf, t.linkage);
            (buf.get()).writeByte(32);
        }
        if ((ident != null) && (ident.toHChars2() != ident.toChars()))
        {
        }
        else if (t.next.value != null)
        {
            typeToBuffer(t.next.value, null, buf, hgs);
            if (ident != null)
            {
                (buf.get()).writeByte(32);
            }
        }
        else if ((hgs.get()).ddoc)
        {
            (buf.get()).writestring(new ByteSlice("auto "));
        }
        if (ident != null)
        {
            (buf.get()).writestring(ident.toHChars2());
        }
        if (td != null)
        {
            (buf.get()).writeByte(40);
            {
                Slice<TemplateParameter> __r1484 = (td.origParameters.get()).opSlice().copy();
                int __key1483 = 0;
                for (; (__key1483 < __r1484.getLength());__key1483 += 1) {
                    TemplateParameter p = __r1484.get(__key1483);
                    int i = __key1483;
                    if (i != 0)
                    {
                        (buf.get()).writestring(new ByteSlice(", "));
                    }
                    templateParameterToBuffer(p, buf, hgs);
                }
            }
            (buf.get()).writeByte(41);
        }
        parametersToBuffer(t.parameterList, buf, hgs);
        if (t.isreturn)
        {
            (buf.get()).writestring(new ByteSlice(" return"));
        }
        t.inuse--;
    }

    public static void initializerToBuffer(Initializer inx, Ptr<OutBuffer> buf, Ptr<HdrGenState> hgs) {
        Function1<ErrorInitializer,Void> visitError = new Function1<ErrorInitializer,Void>(){
            public Void invoke(ErrorInitializer iz) {
                (buf.get()).writestring(new ByteSlice("__error__"));
                return null;
            }
        };
        Function1<VoidInitializer,Void> visitVoid = new Function1<VoidInitializer,Void>(){
            public Void invoke(VoidInitializer iz) {
                (buf.get()).writestring(new ByteSlice("void"));
                return null;
            }
        };
        Function1<StructInitializer,Void> visitStruct = new Function1<StructInitializer,Void>(){
            public Void invoke(StructInitializer si) {
                (buf.get()).writeByte(123);
                {
                    Slice<Identifier> __r1486 = si.field.opSlice().copy();
                    int __key1485 = 0;
                    for (; (__key1485 < __r1486.getLength());__key1485 += 1) {
                        Identifier id = __r1486.get(__key1485);
                        int i = __key1485;
                        if (i != 0)
                        {
                            (buf.get()).writestring(new ByteSlice(", "));
                        }
                        if (id != null)
                        {
                            (buf.get()).writestring(id.asString());
                            (buf.get()).writeByte(58);
                        }
                        {
                            Initializer iz = si.value.get(i);
                            if ((iz) != null)
                            {
                                initializerToBuffer(iz, buf, hgs);
                            }
                        }
                    }
                }
                (buf.get()).writeByte(125);
                return null;
            }
        };
        Function1<ArrayInitializer,Void> visitArray = new Function1<ArrayInitializer,Void>(){
            public Void invoke(ArrayInitializer ai) {
                (buf.get()).writeByte(91);
                {
                    Slice<Expression> __r1488 = ai.index.opSlice().copy();
                    int __key1487 = 0;
                    for (; (__key1487 < __r1488.getLength());__key1487 += 1) {
                        Expression ex = __r1488.get(__key1487);
                        int i = __key1487;
                        if (i != 0)
                        {
                            (buf.get()).writestring(new ByteSlice(", "));
                        }
                        if (ex != null)
                        {
                            expressionToBuffer(ex, buf, hgs);
                            (buf.get()).writeByte(58);
                        }
                        {
                            Initializer iz = ai.value.get(i);
                            if ((iz) != null)
                            {
                                initializerToBuffer(iz, buf, hgs);
                            }
                        }
                    }
                }
                (buf.get()).writeByte(93);
                return null;
            }
        };
        Function1<ExpInitializer,Void> visitExp = new Function1<ExpInitializer,Void>(){
            public Void invoke(ExpInitializer ei) {
                expressionToBuffer(ei.exp, buf, hgs);
                return null;
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
        Function1<Type,Void> visitType = new Function1<Type,Void>(){
            public Void invoke(Type t) {
                printf(new BytePtr("t = %p, ty = %d\n"), t, (t.ty & 0xFF));
                throw new AssertionError("Unreachable code!");
            }
        };
        Function1<TypeError,Void> visitError = new Function1<TypeError,Void>(){
            public Void invoke(TypeError t) {
                (buf.get()).writestring(new ByteSlice("_error_"));
                return null;
            }
        };
        Function1<TypeBasic,Void> visitBasic = new Function1<TypeBasic,Void>(){
            public Void invoke(TypeBasic t) {
                (buf.get()).writestring(t.dstring);
                return null;
            }
        };
        Function1<TypeTraits,Void> visitTraits = new Function1<TypeTraits,Void>(){
            public Void invoke(TypeTraits t) {
                expressionToBuffer(t.exp, buf, hgs);
                return null;
            }
        };
        Function1<TypeVector,Void> visitVector = new Function1<TypeVector,Void>(){
            public Void invoke(TypeVector t) {
                (buf.get()).writestring(new ByteSlice("__vector("));
                visitWithMask(t.basetype, t.mod, buf, hgs);
                (buf.get()).writestring(new ByteSlice(")"));
                return null;
            }
        };
        Function1<TypeSArray,Void> visitSArray = new Function1<TypeSArray,Void>(){
            public Void invoke(TypeSArray t) {
                visitWithMask(t.next.value, t.mod, buf, hgs);
                (buf.get()).writeByte(91);
                sizeToBuffer(t.dim, buf, hgs);
                (buf.get()).writeByte(93);
                return null;
            }
        };
        Function1<TypeDArray,Void> visitDArray = new Function1<TypeDArray,Void>(){
            public Void invoke(TypeDArray t) {
                Type ut = t.castMod((byte)0);
                if ((hgs.get()).declstring)
                {
                    /*goto L1*//*unrolled goto*/
                    (buf.get()).writestring(new ByteSlice("dstring"));
                }
                if (ut.equals(Type.tstring))
                {
                    (buf.get()).writestring(new ByteSlice("string"));
                }
                else if (ut.equals(Type.twstring))
                {
                    (buf.get()).writestring(new ByteSlice("wstring"));
                }
                else if (ut.equals(Type.tdstring))
                {
                    (buf.get()).writestring(new ByteSlice("dstring"));
                }
                else
                {
                /*L1:*/
                    visitWithMask(t.next.value, t.mod, buf, hgs);
                    (buf.get()).writestring(new ByteSlice("[]"));
                }
                return null;
            }
        };
        Function1<TypeAArray,Void> visitAArray = new Function1<TypeAArray,Void>(){
            public Void invoke(TypeAArray t) {
                visitWithMask(t.next.value, t.mod, buf, hgs);
                (buf.get()).writeByte(91);
                visitWithMask(t.index, (byte)0, buf, hgs);
                (buf.get()).writeByte(93);
                return null;
            }
        };
        Function1<TypePointer,Void> visitPointer = new Function1<TypePointer,Void>(){
            public Void invoke(TypePointer t) {
                if (((t.next.value.ty & 0xFF) == ENUMTY.Tfunction))
                {
                    visitFuncIdentWithPostfix((TypeFunction)t.next.value, new ByteSlice("function"), buf, hgs);
                }
                else
                {
                    visitWithMask(t.next.value, t.mod, buf, hgs);
                    (buf.get()).writeByte(42);
                }
                return null;
            }
        };
        Function1<TypeReference,Void> visitReference = new Function1<TypeReference,Void>(){
            public Void invoke(TypeReference t) {
                visitWithMask(t.next.value, t.mod, buf, hgs);
                (buf.get()).writeByte(38);
                return null;
            }
        };
        Function1<TypeFunction,Void> visitFunction = new Function1<TypeFunction,Void>(){
            public Void invoke(TypeFunction t) {
                visitFuncIdentWithPostfix(t, new ByteSlice(), buf, hgs);
                return null;
            }
        };
        Function1<TypeDelegate,Void> visitDelegate = new Function1<TypeDelegate,Void>(){
            public Void invoke(TypeDelegate t) {
                visitFuncIdentWithPostfix((TypeFunction)t.next.value, new ByteSlice("delegate"), buf, hgs);
                return null;
            }
        };
        Function1<TypeQualified,Void> visitTypeQualifiedHelper = new Function1<TypeQualified,Void>(){
            public Void invoke(TypeQualified t) {
                {
                    Slice<RootObject> __r1489 = t.idents.opSlice().copy();
                    int __key1490 = 0;
                    for (; (__key1490 < __r1489.getLength());__key1490 += 1) {
                        RootObject id = __r1489.get(__key1490);
                        if ((id.dyncast() == DYNCAST.dsymbol))
                        {
                            (buf.get()).writeByte(46);
                            TemplateInstance ti = (TemplateInstance)id;
                            dsymbolToBuffer(ti, buf, hgs);
                        }
                        else if ((id.dyncast() == DYNCAST.expression))
                        {
                            (buf.get()).writeByte(91);
                            expressionToBuffer((Expression)id, buf, hgs);
                            (buf.get()).writeByte(93);
                        }
                        else if ((id.dyncast() == DYNCAST.type))
                        {
                            (buf.get()).writeByte(91);
                            typeToBufferx((Type)id, buf, hgs);
                            (buf.get()).writeByte(93);
                        }
                        else
                        {
                            (buf.get()).writeByte(46);
                            (buf.get()).writestring(id.asString());
                        }
                    }
                }
                return null;
            }
        };
        Function1<TypeIdentifier,Void> visitIdentifier = new Function1<TypeIdentifier,Void>(){
            public Void invoke(TypeIdentifier t) {
                (buf.get()).writestring(t.ident.asString());
                visitTypeQualifiedHelper.invoke(t);
                return null;
            }
        };
        Function1<TypeInstance,Void> visitInstance = new Function1<TypeInstance,Void>(){
            public Void invoke(TypeInstance t) {
                dsymbolToBuffer(t.tempinst, buf, hgs);
                visitTypeQualifiedHelper.invoke(t);
                return null;
            }
        };
        Function1<TypeTypeof,Void> visitTypeof = new Function1<TypeTypeof,Void>(){
            public Void invoke(TypeTypeof t) {
                (buf.get()).writestring(new ByteSlice("typeof("));
                expressionToBuffer(t.exp, buf, hgs);
                (buf.get()).writeByte(41);
                visitTypeQualifiedHelper.invoke(t);
                return null;
            }
        };
        Function1<TypeReturn,Void> visitReturn = new Function1<TypeReturn,Void>(){
            public Void invoke(TypeReturn t) {
                (buf.get()).writestring(new ByteSlice("typeof(return)"));
                visitTypeQualifiedHelper.invoke(t);
                return null;
            }
        };
        Function1<TypeEnum,Void> visitEnum = new Function1<TypeEnum,Void>(){
            public Void invoke(TypeEnum t) {
                (buf.get()).writestring((hgs.get()).fullQual ? t.sym.toPrettyChars(false) : t.sym.toChars());
                return null;
            }
        };
        Function1<TypeStruct,Void> visitStruct = new Function1<TypeStruct,Void>(){
            public Void invoke(TypeStruct t) {
                TemplateInstance ti = t.sym.parent.value != null ? t.sym.parent.value.isTemplateInstance() : null;
                if ((ti != null) && (pequals(ti.aliasdecl, t.sym)))
                {
                    (buf.get()).writestring((hgs.get()).fullQual ? ti.toPrettyChars(false) : ti.toChars());
                }
                else
                {
                    (buf.get()).writestring((hgs.get()).fullQual ? t.sym.toPrettyChars(false) : t.sym.toChars());
                }
                return null;
            }
        };
        Function1<TypeClass,Void> visitClass = new Function1<TypeClass,Void>(){
            public Void invoke(TypeClass t) {
                TemplateInstance ti = t.sym.parent.value.isTemplateInstance();
                if ((ti != null) && (pequals(ti.aliasdecl, t.sym)))
                {
                    (buf.get()).writestring((hgs.get()).fullQual ? ti.toPrettyChars(false) : ti.toChars());
                }
                else
                {
                    (buf.get()).writestring((hgs.get()).fullQual ? t.sym.toPrettyChars(false) : t.sym.toChars());
                }
                return null;
            }
        };
        Function1<TypeTuple,Void> visitTuple = new Function1<TypeTuple,Void>(){
            public Void invoke(TypeTuple t) {
                parametersToBuffer(new ParameterList(t.arguments, VarArg.none), buf, hgs);
                return null;
            }
        };
        Function1<TypeSlice,Void> visitSlice = new Function1<TypeSlice,Void>(){
            public Void invoke(TypeSlice t) {
                visitWithMask(t.next.value, t.mod, buf, hgs);
                (buf.get()).writeByte(91);
                sizeToBuffer(t.lwr, buf, hgs);
                (buf.get()).writestring(new ByteSlice(" .. "));
                sizeToBuffer(t.upr, buf, hgs);
                (buf.get()).writeByte(93);
                return null;
            }
        };
        Function1<TypeNull,Void> visitNull = new Function1<TypeNull,Void>(){
            public Void invoke(TypeNull t) {
                (buf.get()).writestring(new ByteSlice("typeof(null)"));
                return null;
            }
        };
        switch ((t.ty & 0xFF))
        {
            default:
            expr(t.isTypeBasic() != null ? visitBasic.invoke((TypeBasic)t) : visitType.invoke(t));
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
