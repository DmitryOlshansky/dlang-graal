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
        private long stc;
        private byte tok;
        private ByteSlice id;
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
        public boolean hdrgen;
        public boolean ddoc;
        public boolean fullDump;
        public boolean fullQual;
        public int tpltMember;
        public int autoMember;
        public int forStmtInit;
        public boolean declstring;
        public EnumDeclaration inEnumDecl;
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
        OutBuffer buf = new OutBuffer();
        try {
            expr(buf.doindent = true);
            buf.printf(new BytePtr("// D import file generated from '%s'"), m.srcfile.toChars());
            buf.writenl();
            HdrGenState hgs = new HdrGenState();
            expr(hgs.hdrgen = true);
            toCBuffer((Dsymbol)m, buf, hgs);
            writeFile(m.loc, m.hdrfile.asString(), toByteSlice(buf.peekSlice()));
        }
        finally {
        }
    }

    public static void moduleToBuffer(OutBuffer buf, dmodule.Module m) {
        HdrGenState hgs = new HdrGenState();
        expr(hgs.fullDump = true);
        toCBuffer((Dsymbol)m, buf, hgs);
    }

    public static void moduleToBuffer2(dmodule.Module m, OutBuffer buf, HdrGenState hgs) {
        if (m.md != null)
        {
            if (m.userAttribDecl != null)
            {
                (buf).writestring(new ByteSlice("@("));
                argsToBuffer(m.userAttribDecl.atts, buf, hgs, null);
                (buf).writeByte(41);
                (buf).writenl();
            }
            if ((m.md).isdeprecated)
            {
                if ((m.md).msg != null)
                {
                    (buf).writestring(new ByteSlice("deprecated("));
                    expressionToBuffer((m.md).msg, buf, hgs);
                    (buf).writestring(new ByteSlice(") "));
                }
                else
                    (buf).writestring(new ByteSlice("deprecated "));
            }
            (buf).writestring(new ByteSlice("module "));
            (buf).writestring((m.md).toChars());
            (buf).writeByte(59);
            (buf).writenl();
        }
        {
            Slice<Dsymbol> __r1419 = (m.members).opSlice().copy();
            int __key1420 = 0;
            for (; (__key1420 < __r1419.getLength());__key1420 += 1) {
                Dsymbol s = __r1419.get(__key1420);
                dsymbolToBuffer(s, buf, hgs);
            }
        }
    }

    public static void statementToBuffer(Statement s, OutBuffer buf, HdrGenState hgs) {
        StatementPrettyPrintVisitor v = new StatementPrettyPrintVisitor(buf, hgs);
        s.accept(v);
    }

    public static class StatementPrettyPrintVisitor extends Visitor
    {
        public OutBuffer buf;
        public HdrGenState hgs;
        public  StatementPrettyPrintVisitor(OutBuffer buf, HdrGenState hgs) {
            this.buf = buf;
            this.hgs = hgs;
        }

        public  void visit(Statement s) {
            (this.buf).writestring(new ByteSlice("Statement::toCBuffer()"));
            (this.buf).writenl();
            throw new AssertionError("Unreachable code!");
        }

        public  void visit(ErrorStatement s) {
            (this.buf).writestring(new ByteSlice("__error__"));
            (this.buf).writenl();
        }

        public  void visit(ExpStatement s) {
            if ((s.exp != null) && ((s.exp.op & 0xFF) == 38) && (((DeclarationExp)s.exp).declaration != null))
            {
                dsymbolToBuffer(((DeclarationExp)s.exp).declaration, this.buf, this.hgs);
                return ;
            }
            if (s.exp != null)
                expressionToBuffer(s.exp, this.buf, this.hgs);
            (this.buf).writeByte(59);
            if ((this.hgs).forStmtInit == 0)
                (this.buf).writenl();
        }

        public  void visit(CompileStatement s) {
            (this.buf).writestring(new ByteSlice("mixin("));
            argsToBuffer(s.exps, this.buf, this.hgs, null);
            (this.buf).writestring(new ByteSlice(");"));
            if ((this.hgs).forStmtInit == 0)
                (this.buf).writenl();
        }

        public  void visit(CompoundStatement s) {
            {
                Slice<Statement> __r1421 = (s.statements).opSlice().copy();
                int __key1422 = 0;
                for (; (__key1422 < __r1421.getLength());__key1422 += 1) {
                    Statement sx = __r1421.get(__key1422);
                    if (sx != null)
                        sx.accept(this);
                }
            }
        }

        public  void visit(CompoundDeclarationStatement s) {
            boolean anywritten = false;
            {
                Slice<Statement> __r1423 = (s.statements).opSlice().copy();
                int __key1424 = 0;
                for (; (__key1424 < __r1423.getLength());__key1424 += 1) {
                    Statement sx = __r1423.get(__key1424);
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
                                dsymbolToBuffer(d, this.buf, this.hgs);
                        }
                        expr(anywritten = true);
                    }
                }
            }
            (this.buf).writeByte(59);
            if ((this.hgs).forStmtInit == 0)
                (this.buf).writenl();
        }

        public  void visit(UnrolledLoopStatement s) {
            (this.buf).writestring(new ByteSlice("/*unrolled*/ {"));
            (this.buf).writenl();
            (this.buf).level++;
            {
                Slice<Statement> __r1425 = (s.statements).opSlice().copy();
                int __key1426 = 0;
                for (; (__key1426 < __r1425.getLength());__key1426 += 1) {
                    Statement sx = __r1425.get(__key1426);
                    if (sx != null)
                        sx.accept(this);
                }
            }
            (this.buf).level--;
            (this.buf).writeByte(125);
            (this.buf).writenl();
        }

        public  void visit(ScopeStatement s) {
            (this.buf).writeByte(123);
            (this.buf).writenl();
            (this.buf).level++;
            if (s.statement != null)
                s.statement.accept(this);
            (this.buf).level--;
            (this.buf).writeByte(125);
            (this.buf).writenl();
        }

        public  void visit(WhileStatement s) {
            (this.buf).writestring(new ByteSlice("while ("));
            expressionToBuffer(s.condition, this.buf, this.hgs);
            (this.buf).writeByte(41);
            (this.buf).writenl();
            if (s._body != null)
                s._body.accept(this);
        }

        public  void visit(DoStatement s) {
            (this.buf).writestring(new ByteSlice("do"));
            (this.buf).writenl();
            if (s._body != null)
                s._body.accept(this);
            (this.buf).writestring(new ByteSlice("while ("));
            expressionToBuffer(s.condition, this.buf, this.hgs);
            (this.buf).writestring(new ByteSlice(");"));
            (this.buf).writenl();
        }

        public  void visit(ForStatement s) {
            (this.buf).writestring(new ByteSlice("for ("));
            if (s._init != null)
            {
                (this.hgs).forStmtInit++;
                s._init.accept(this);
                (this.hgs).forStmtInit--;
            }
            else
                (this.buf).writeByte(59);
            if (s.condition != null)
            {
                (this.buf).writeByte(32);
                expressionToBuffer(s.condition, this.buf, this.hgs);
            }
            (this.buf).writeByte(59);
            if (s.increment != null)
            {
                (this.buf).writeByte(32);
                expressionToBuffer(s.increment, this.buf, this.hgs);
            }
            (this.buf).writeByte(41);
            (this.buf).writenl();
            (this.buf).writeByte(123);
            (this.buf).writenl();
            (this.buf).level++;
            if (s._body != null)
                s._body.accept(this);
            (this.buf).level--;
            (this.buf).writeByte(125);
            (this.buf).writenl();
        }

        public  void foreachWithoutBody(ForeachStatement s) {
            (this.buf).writestring(Token.asString(s.op));
            (this.buf).writestring(new ByteSlice(" ("));
            {
                Slice<Parameter> __r1428 = (s.parameters).opSlice().copy();
                int __key1427 = 0;
                for (; (__key1427 < __r1428.getLength());__key1427 += 1) {
                    Parameter p = __r1428.get(__key1427);
                    int i = __key1427;
                    if (i != 0)
                        (this.buf).writestring(new ByteSlice(", "));
                    if (stcToBuffer(this.buf, p.storageClass))
                        (this.buf).writeByte(32);
                    if (p.type != null)
                        typeToBuffer(p.type, p.ident, this.buf, this.hgs);
                    else
                        (this.buf).writestring(p.ident.asString());
                }
            }
            (this.buf).writestring(new ByteSlice("; "));
            expressionToBuffer(s.aggr, this.buf, this.hgs);
            (this.buf).writeByte(41);
            (this.buf).writenl();
        }

        public  void visit(ForeachStatement s) {
            this.foreachWithoutBody(s);
            (this.buf).writeByte(123);
            (this.buf).writenl();
            (this.buf).level++;
            if (s._body != null)
                s._body.accept(this);
            (this.buf).level--;
            (this.buf).writeByte(125);
            (this.buf).writenl();
        }

        public  void foreachRangeWithoutBody(ForeachRangeStatement s) {
            (this.buf).writestring(Token.asString(s.op));
            (this.buf).writestring(new ByteSlice(" ("));
            if (s.prm.type != null)
                typeToBuffer(s.prm.type, s.prm.ident, this.buf, this.hgs);
            else
                (this.buf).writestring(s.prm.ident.asString());
            (this.buf).writestring(new ByteSlice("; "));
            expressionToBuffer(s.lwr, this.buf, this.hgs);
            (this.buf).writestring(new ByteSlice(" .. "));
            expressionToBuffer(s.upr, this.buf, this.hgs);
            (this.buf).writeByte(41);
            (this.buf).writenl();
        }

        public  void visit(ForeachRangeStatement s) {
            this.foreachRangeWithoutBody(s);
            (this.buf).writeByte(123);
            (this.buf).writenl();
            (this.buf).level++;
            if (s._body != null)
                s._body.accept(this);
            (this.buf).level--;
            (this.buf).writeByte(125);
            (this.buf).writenl();
        }

        public  void visit(StaticForeachStatement s) {
            (this.buf).writestring(new ByteSlice("static "));
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
            (this.buf).writestring(new ByteSlice("if ("));
            {
                Parameter p = s.prm;
                if ((p) != null)
                {
                    long stc = p.storageClass;
                    if ((p.type == null) && (stc == 0))
                        stc = 256L;
                    if (stcToBuffer(this.buf, stc))
                        (this.buf).writeByte(32);
                    if (p.type != null)
                        typeToBuffer(p.type, p.ident, this.buf, this.hgs);
                    else
                        (this.buf).writestring(p.ident.asString());
                    (this.buf).writestring(new ByteSlice(" = "));
                }
            }
            expressionToBuffer(s.condition, this.buf, this.hgs);
            (this.buf).writeByte(41);
            (this.buf).writenl();
            if (s.ifbody.isScopeStatement() != null)
            {
                s.ifbody.accept(this);
            }
            else
            {
                (this.buf).level++;
                s.ifbody.accept(this);
                (this.buf).level--;
            }
            if (s.elsebody != null)
            {
                (this.buf).writestring(new ByteSlice("else"));
                if (s.elsebody.isIfStatement() == null)
                {
                    (this.buf).writenl();
                }
                else
                {
                    (this.buf).writeByte(32);
                }
                if ((s.elsebody.isScopeStatement() != null) || (s.elsebody.isIfStatement() != null))
                {
                    s.elsebody.accept(this);
                }
                else
                {
                    (this.buf).level++;
                    s.elsebody.accept(this);
                    (this.buf).level--;
                }
            }
        }

        public  void visit(ConditionalStatement s) {
            conditionToBuffer(s.condition, this.buf, this.hgs);
            (this.buf).writenl();
            (this.buf).writeByte(123);
            (this.buf).writenl();
            (this.buf).level++;
            if (s.ifbody != null)
                s.ifbody.accept(this);
            (this.buf).level--;
            (this.buf).writeByte(125);
            (this.buf).writenl();
            if (s.elsebody != null)
            {
                (this.buf).writestring(new ByteSlice("else"));
                (this.buf).writenl();
                (this.buf).writeByte(123);
                (this.buf).level++;
                (this.buf).writenl();
                s.elsebody.accept(this);
                (this.buf).level--;
                (this.buf).writeByte(125);
            }
            (this.buf).writenl();
        }

        public  void visit(PragmaStatement s) {
            (this.buf).writestring(new ByteSlice("pragma ("));
            (this.buf).writestring(s.ident.asString());
            if ((s.args != null) && ((s.args).length != 0))
            {
                (this.buf).writestring(new ByteSlice(", "));
                argsToBuffer(s.args, this.buf, this.hgs, null);
            }
            (this.buf).writeByte(41);
            if (s._body != null)
            {
                (this.buf).writenl();
                (this.buf).writeByte(123);
                (this.buf).writenl();
                (this.buf).level++;
                s._body.accept(this);
                (this.buf).level--;
                (this.buf).writeByte(125);
                (this.buf).writenl();
            }
            else
            {
                (this.buf).writeByte(59);
                (this.buf).writenl();
            }
        }

        public  void visit(StaticAssertStatement s) {
            dsymbolToBuffer(s.sa, this.buf, this.hgs);
        }

        public  void visit(SwitchStatement s) {
            (this.buf).writestring(s.isFinal ? new ByteSlice("final switch (") : new ByteSlice("switch ("));
            expressionToBuffer(s.condition, this.buf, this.hgs);
            (this.buf).writeByte(41);
            (this.buf).writenl();
            if (s._body != null)
            {
                if (s._body.isScopeStatement() == null)
                {
                    (this.buf).writeByte(123);
                    (this.buf).writenl();
                    (this.buf).level++;
                    s._body.accept(this);
                    (this.buf).level--;
                    (this.buf).writeByte(125);
                    (this.buf).writenl();
                }
                else
                {
                    s._body.accept(this);
                }
            }
        }

        public  void visit(CaseStatement s) {
            (this.buf).writestring(new ByteSlice("case "));
            expressionToBuffer(s.exp, this.buf, this.hgs);
            (this.buf).writeByte(58);
            (this.buf).writenl();
            s.statement.accept(this);
        }

        public  void visit(CaseRangeStatement s) {
            (this.buf).writestring(new ByteSlice("case "));
            expressionToBuffer(s.first, this.buf, this.hgs);
            (this.buf).writestring(new ByteSlice(": .. case "));
            expressionToBuffer(s.last, this.buf, this.hgs);
            (this.buf).writeByte(58);
            (this.buf).writenl();
            s.statement.accept(this);
        }

        public  void visit(DefaultStatement s) {
            (this.buf).writestring(new ByteSlice("default:"));
            (this.buf).writenl();
            s.statement.accept(this);
        }

        public  void visit(GotoDefaultStatement s) {
            (this.buf).writestring(new ByteSlice("goto default;"));
            (this.buf).writenl();
        }

        public  void visit(GotoCaseStatement s) {
            (this.buf).writestring(new ByteSlice("goto case"));
            if (s.exp != null)
            {
                (this.buf).writeByte(32);
                expressionToBuffer(s.exp, this.buf, this.hgs);
            }
            (this.buf).writeByte(59);
            (this.buf).writenl();
        }

        public  void visit(SwitchErrorStatement s) {
            (this.buf).writestring(new ByteSlice("SwitchErrorStatement::toCBuffer()"));
            (this.buf).writenl();
        }

        public  void visit(ReturnStatement s) {
            (this.buf).writestring(new ByteSlice("return "));
            if (s.exp != null)
                expressionToBuffer(s.exp, this.buf, this.hgs);
            (this.buf).writeByte(59);
            (this.buf).writenl();
        }

        public  void visit(BreakStatement s) {
            (this.buf).writestring(new ByteSlice("break"));
            if (s.ident != null)
            {
                (this.buf).writeByte(32);
                (this.buf).writestring(s.ident.asString());
            }
            (this.buf).writeByte(59);
            (this.buf).writenl();
        }

        public  void visit(ContinueStatement s) {
            (this.buf).writestring(new ByteSlice("continue"));
            if (s.ident != null)
            {
                (this.buf).writeByte(32);
                (this.buf).writestring(s.ident.asString());
            }
            (this.buf).writeByte(59);
            (this.buf).writenl();
        }

        public  void visit(SynchronizedStatement s) {
            (this.buf).writestring(new ByteSlice("synchronized"));
            if (s.exp != null)
            {
                (this.buf).writeByte(40);
                expressionToBuffer(s.exp, this.buf, this.hgs);
                (this.buf).writeByte(41);
            }
            if (s._body != null)
            {
                (this.buf).writeByte(32);
                s._body.accept(this);
            }
        }

        public  void visit(WithStatement s) {
            (this.buf).writestring(new ByteSlice("with ("));
            expressionToBuffer(s.exp, this.buf, this.hgs);
            (this.buf).writestring(new ByteSlice(")"));
            (this.buf).writenl();
            if (s._body != null)
                s._body.accept(this);
        }

        public  void visit(TryCatchStatement s) {
            (this.buf).writestring(new ByteSlice("try"));
            (this.buf).writenl();
            if (s._body != null)
            {
                if (s._body.isScopeStatement() != null)
                {
                    s._body.accept(this);
                }
                else
                {
                    (this.buf).level++;
                    s._body.accept(this);
                    (this.buf).level--;
                }
            }
            {
                Slice<Catch> __r1429 = (s.catches).opSlice().copy();
                int __key1430 = 0;
                for (; (__key1430 < __r1429.getLength());__key1430 += 1) {
                    Catch c = __r1429.get(__key1430);
                    this.visit(c);
                }
            }
        }

        public  void visit(TryFinallyStatement s) {
            (this.buf).writestring(new ByteSlice("try"));
            (this.buf).writenl();
            (this.buf).writeByte(123);
            (this.buf).writenl();
            (this.buf).level++;
            s._body.accept(this);
            (this.buf).level--;
            (this.buf).writeByte(125);
            (this.buf).writenl();
            (this.buf).writestring(new ByteSlice("finally"));
            (this.buf).writenl();
            if (s.finalbody.isScopeStatement() != null)
            {
                s.finalbody.accept(this);
            }
            else
            {
                (this.buf).level++;
                s.finalbody.accept(this);
                (this.buf).level--;
            }
        }

        public  void visit(ScopeGuardStatement s) {
            (this.buf).writestring(Token.asString(s.tok));
            (this.buf).writeByte(32);
            if (s.statement != null)
                s.statement.accept(this);
        }

        public  void visit(ThrowStatement s) {
            (this.buf).writestring(new ByteSlice("throw "));
            expressionToBuffer(s.exp, this.buf, this.hgs);
            (this.buf).writeByte(59);
            (this.buf).writenl();
        }

        public  void visit(DebugStatement s) {
            if (s.statement != null)
            {
                s.statement.accept(this);
            }
        }

        public  void visit(GotoStatement s) {
            (this.buf).writestring(new ByteSlice("goto "));
            (this.buf).writestring(s.ident.asString());
            (this.buf).writeByte(59);
            (this.buf).writenl();
        }

        public  void visit(LabelStatement s) {
            (this.buf).writestring(s.ident.asString());
            (this.buf).writeByte(58);
            (this.buf).writenl();
            if (s.statement != null)
                s.statement.accept(this);
        }

        public  void visit(AsmStatement s) {
            (this.buf).writestring(new ByteSlice("asm { "));
            Token t = s.tokens;
            (this.buf).level++;
            for (; t != null;){
                (this.buf).writestring((t).toChars());
                if (((t).next != null) && (((t).value & 0xFF) != 75) && (((t).value & 0xFF) != 99) && ((((t).next).value & 0xFF) != 99) && (((t).value & 0xFF) != 3) && ((((t).next).value & 0xFF) != 3) && ((((t).next).value & 0xFF) != 4) && (((t).value & 0xFF) != 1) && ((((t).next).value & 0xFF) != 1) && ((((t).next).value & 0xFF) != 2) && (((t).value & 0xFF) != 97) && ((((t).next).value & 0xFF) != 97))
                {
                    (this.buf).writeByte(32);
                }
                t = (t).next;
            }
            (this.buf).level--;
            (this.buf).writestring(new ByteSlice("; }"));
            (this.buf).writenl();
        }

        public  void visit(ImportStatement s) {
            {
                Slice<Dsymbol> __r1431 = (s.imports).opSlice().copy();
                int __key1432 = 0;
                for (; (__key1432 < __r1431.getLength());__key1432 += 1) {
                    Dsymbol imp = __r1431.get(__key1432);
                    dsymbolToBuffer(imp, this.buf, this.hgs);
                }
            }
        }

        public  void visit(Catch c) {
            (this.buf).writestring(new ByteSlice("catch"));
            if (c.type != null)
            {
                (this.buf).writeByte(40);
                typeToBuffer(c.type, c.ident, this.buf, this.hgs);
                (this.buf).writeByte(41);
            }
            (this.buf).writenl();
            (this.buf).writeByte(123);
            (this.buf).writenl();
            (this.buf).level++;
            if (c.handler != null)
                c.handler.accept(this);
            (this.buf).level--;
            (this.buf).writeByte(125);
            (this.buf).writenl();
        }


        public StatementPrettyPrintVisitor() {}

        public StatementPrettyPrintVisitor copy() {
            StatementPrettyPrintVisitor that = new StatementPrettyPrintVisitor();
            that.buf = this.buf;
            that.hgs = this.hgs;
            return that;
        }
    }
    public static void dsymbolToBuffer(Dsymbol s, OutBuffer buf, HdrGenState hgs) {
        DsymbolPrettyPrintVisitor v = new DsymbolPrettyPrintVisitor(buf, hgs);
        s.accept(v);
    }

    public static class DsymbolPrettyPrintVisitor extends Visitor
    {
        public OutBuffer buf;
        public HdrGenState hgs;
        public  DsymbolPrettyPrintVisitor(OutBuffer buf, HdrGenState hgs) {
            this.buf = buf;
            this.hgs = hgs;
        }

        public  void visit(Dsymbol s) {
            (this.buf).writestring(s.toChars());
        }

        public  void visit(StaticAssert s) {
            (this.buf).writestring(s.kind());
            (this.buf).writeByte(40);
            expressionToBuffer(s.exp, this.buf, this.hgs);
            if (s.msg != null)
            {
                (this.buf).writestring(new ByteSlice(", "));
                expressionToBuffer(s.msg, this.buf, this.hgs);
            }
            (this.buf).writestring(new ByteSlice(");"));
            (this.buf).writenl();
        }

        public  void visit(DebugSymbol s) {
            (this.buf).writestring(new ByteSlice("debug = "));
            if (s.ident != null)
                (this.buf).writestring(s.ident.asString());
            else
                (this.buf).print((long)s.level);
            (this.buf).writeByte(59);
            (this.buf).writenl();
        }

        public  void visit(VersionSymbol s) {
            (this.buf).writestring(new ByteSlice("version = "));
            if (s.ident != null)
                (this.buf).writestring(s.ident.asString());
            else
                (this.buf).print((long)s.level);
            (this.buf).writeByte(59);
            (this.buf).writenl();
        }

        public  void visit(EnumMember em) {
            if (em.type != null)
                typeToBuffer(em.type, em.ident, this.buf, this.hgs);
            else
                (this.buf).writestring(em.ident.asString());
            if (em.value() != null)
            {
                (this.buf).writestring(new ByteSlice(" = "));
                expressionToBuffer(em.value(), this.buf, this.hgs);
            }
        }

        public  void visit(Import imp) {
            if ((this.hgs).hdrgen && (pequals(imp.id, Id.object)))
                return ;
            if (imp.isstatic != 0)
                (this.buf).writestring(new ByteSlice("static "));
            (this.buf).writestring(new ByteSlice("import "));
            if (imp.aliasId != null)
            {
                (this.buf).printf(new BytePtr("%s = "), imp.aliasId.toChars());
            }
            if ((imp.packages != null) && ((imp.packages).length != 0))
            {
                {
                    Slice<Identifier> __r1433 = (imp.packages).opSlice().copy();
                    int __key1434 = 0;
                    for (; (__key1434 < __r1433.getLength());__key1434 += 1) {
                        Identifier pid = __r1433.get(__key1434);
                        (this.buf).printf(new BytePtr("%s."), pid.toChars());
                    }
                }
            }
            (this.buf).writestring(imp.id.asString());
            if (imp.names.length != 0)
            {
                (this.buf).writestring(new ByteSlice(" : "));
                {
                    Slice<Identifier> __r1436 = imp.names.opSlice().copy();
                    int __key1435 = 0;
                    for (; (__key1435 < __r1436.getLength());__key1435 += 1) {
                        Identifier name = __r1436.get(__key1435);
                        int i = __key1435;
                        if (i != 0)
                            (this.buf).writestring(new ByteSlice(", "));
                        Identifier _alias = imp.aliases.get(i);
                        if (_alias != null)
                            (this.buf).printf(new BytePtr("%s = %s"), _alias.toChars(), name.toChars());
                        else
                            (this.buf).writestring(name.toChars());
                    }
                }
            }
            (this.buf).writeByte(59);
            (this.buf).writenl();
        }

        public  void visit(AliasThis d) {
            (this.buf).writestring(new ByteSlice("alias "));
            (this.buf).writestring(d.ident.asString());
            (this.buf).writestring(new ByteSlice(" this;\n"));
        }

        public  void visit(AttribDeclaration d) {
            if (d.decl == null)
            {
                (this.buf).writeByte(59);
                (this.buf).writenl();
                return ;
            }
            if (((d.decl).length == 0))
                (this.buf).writestring(new ByteSlice("{}"));
            else if ((this.hgs).hdrgen && ((d.decl).length == 1) && ((d.decl).get(0).isUnitTestDeclaration() != null))
            {
                (this.buf).writestring(new ByteSlice("{}"));
            }
            else if (((d.decl).length == 1))
            {
                (d.decl).get(0).accept(this);
                return ;
            }
            else
            {
                (this.buf).writenl();
                (this.buf).writeByte(123);
                (this.buf).writenl();
                (this.buf).level++;
                {
                    Slice<Dsymbol> __r1437 = (d.decl).opSlice().copy();
                    int __key1438 = 0;
                    for (; (__key1438 < __r1437.getLength());__key1438 += 1) {
                        Dsymbol de = __r1437.get(__key1438);
                        de.accept(this);
                    }
                }
                (this.buf).level--;
                (this.buf).writeByte(125);
            }
            (this.buf).writenl();
        }

        public  void visit(StorageClassDeclaration d) {
            if (stcToBuffer(this.buf, d.stc))
                (this.buf).writeByte(32);
            this.visit((AttribDeclaration)d);
        }

        public  void visit(DeprecatedDeclaration d) {
            (this.buf).writestring(new ByteSlice("deprecated("));
            expressionToBuffer(d.msg, this.buf, this.hgs);
            (this.buf).writestring(new ByteSlice(") "));
            this.visit((AttribDeclaration)d);
        }

        public  void visit(LinkDeclaration d) {
            (this.buf).writestring(new ByteSlice("extern ("));
            (this.buf).writestring(linkageToString(d.linkage));
            (this.buf).writestring(new ByteSlice(") "));
            this.visit((AttribDeclaration)d);
        }

        public  void visit(CPPMangleDeclaration d) {
            ByteSlice s = new ByteSlice();
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
            (this.buf).writestring(new ByteSlice("extern (C++, "));
            (this.buf).writestring(s);
            (this.buf).writestring(new ByteSlice(") "));
            this.visit((AttribDeclaration)d);
        }

        public  void visit(ProtDeclaration d) {
            protectionToBuffer(this.buf, d.protection);
            (this.buf).writeByte(32);
            AttribDeclaration ad = d;
            if (((ad.decl).length == 1) && ((ad.decl).get(0).isProtDeclaration() != null))
                this.visit((AttribDeclaration)(ad.decl).get(0));
            else
                this.visit((AttribDeclaration)d);
        }

        public  void visit(AlignDeclaration d) {
            (this.buf).writestring(new ByteSlice("align "));
            if (d.ealign != null)
                (this.buf).printf(new BytePtr("(%s) "), d.ealign.toChars());
            this.visit((AttribDeclaration)d);
        }

        public  void visit(AnonDeclaration d) {
            (this.buf).writestring(d.isunion ? new ByteSlice("union") : new ByteSlice("struct"));
            (this.buf).writenl();
            (this.buf).writestring(new ByteSlice("{"));
            (this.buf).writenl();
            (this.buf).level++;
            if (d.decl != null)
            {
                {
                    Slice<Dsymbol> __r1439 = (d.decl).opSlice().copy();
                    int __key1440 = 0;
                    for (; (__key1440 < __r1439.getLength());__key1440 += 1) {
                        Dsymbol de = __r1439.get(__key1440);
                        de.accept(this);
                    }
                }
            }
            (this.buf).level--;
            (this.buf).writestring(new ByteSlice("}"));
            (this.buf).writenl();
        }

        public  void visit(PragmaDeclaration d) {
            (this.buf).writestring(new ByteSlice("pragma ("));
            (this.buf).writestring(d.ident.asString());
            if ((d.args != null) && ((d.args).length != 0))
            {
                (this.buf).writestring(new ByteSlice(", "));
                argsToBuffer(d.args, this.buf, this.hgs, null);
            }
            (this.buf).writeByte(41);
            this.visit((AttribDeclaration)d);
        }

        public  void visit(ConditionalDeclaration d) {
            conditionToBuffer(d.condition, this.buf, this.hgs);
            if ((d.decl != null) || (d.elsedecl != null))
            {
                (this.buf).writenl();
                (this.buf).writeByte(123);
                (this.buf).writenl();
                (this.buf).level++;
                if (d.decl != null)
                {
                    {
                        Slice<Dsymbol> __r1441 = (d.decl).opSlice().copy();
                        int __key1442 = 0;
                        for (; (__key1442 < __r1441.getLength());__key1442 += 1) {
                            Dsymbol de = __r1441.get(__key1442);
                            de.accept(this);
                        }
                    }
                }
                (this.buf).level--;
                (this.buf).writeByte(125);
                if (d.elsedecl != null)
                {
                    (this.buf).writenl();
                    (this.buf).writestring(new ByteSlice("else"));
                    (this.buf).writenl();
                    (this.buf).writeByte(123);
                    (this.buf).writenl();
                    (this.buf).level++;
                    {
                        Slice<Dsymbol> __r1443 = (d.elsedecl).opSlice().copy();
                        int __key1444 = 0;
                        for (; (__key1444 < __r1443.getLength());__key1444 += 1) {
                            Dsymbol de = __r1443.get(__key1444);
                            de.accept(this);
                        }
                    }
                    (this.buf).level--;
                    (this.buf).writeByte(125);
                }
            }
            else
                (this.buf).writeByte(58);
            (this.buf).writenl();
        }

        public  void visit(StaticForeachDeclaration s) {
            Function1<ForeachStatement,Void> foreachWithoutBody = new Function1<ForeachStatement,Void>(){
                public Void invoke(ForeachStatement s){
                    (buf).writestring(Token.asString(s.op));
                    (buf).writestring(new ByteSlice(" ("));
                    {
                        Slice<Parameter> __r1446 = (s.parameters).opSlice().copy();
                        int __key1445 = 0;
                        for (; (__key1445 < __r1446.getLength());__key1445 += 1) {
                            Parameter p = __r1446.get(__key1445);
                            int i = __key1445;
                            if (i != 0)
                                (buf).writestring(new ByteSlice(", "));
                            if (stcToBuffer(buf, p.storageClass))
                                (buf).writeByte(32);
                            if (p.type != null)
                                typeToBuffer(p.type, p.ident, buf, hgs);
                            else
                                (buf).writestring(p.ident.asString());
                        }
                    }
                    (buf).writestring(new ByteSlice("; "));
                    expressionToBuffer(s.aggr, buf, hgs);
                    (buf).writeByte(41);
                    (buf).writenl();
                }
            };
            Function1<ForeachRangeStatement,Void> foreachRangeWithoutBody = new Function1<ForeachRangeStatement,Void>(){
                public Void invoke(ForeachRangeStatement s){
                    (buf).writestring(Token.asString(s.op));
                    (buf).writestring(new ByteSlice(" ("));
                    if (s.prm.type != null)
                        typeToBuffer(s.prm.type, s.prm.ident, buf, hgs);
                    else
                        (buf).writestring(s.prm.ident.asString());
                    (buf).writestring(new ByteSlice("; "));
                    expressionToBuffer(s.lwr, buf, hgs);
                    (buf).writestring(new ByteSlice(" .. "));
                    expressionToBuffer(s.upr, buf, hgs);
                    (buf).writeByte(41);
                    (buf).writenl();
                }
            };
            (this.buf).writestring(new ByteSlice("static "));
            if (s.sfe.aggrfe != null)
            {
                foreachWithoutBody.invoke(s.sfe.aggrfe);
            }
            else
            {
                assert(s.sfe.rangefe != null);
                foreachRangeWithoutBody.invoke(s.sfe.rangefe);
            }
            (this.buf).writeByte(123);
            (this.buf).writenl();
            (this.buf).level++;
            this.visit((AttribDeclaration)s);
            (this.buf).level--;
            (this.buf).writeByte(125);
            (this.buf).writenl();
        }

        public  void visit(CompileDeclaration d) {
            (this.buf).writestring(new ByteSlice("mixin("));
            argsToBuffer(d.exps, this.buf, this.hgs, null);
            (this.buf).writestring(new ByteSlice(");"));
            (this.buf).writenl();
        }

        public  void visit(UserAttributeDeclaration d) {
            (this.buf).writestring(new ByteSlice("@("));
            argsToBuffer(d.atts, this.buf, this.hgs, null);
            (this.buf).writeByte(41);
            this.visit((AttribDeclaration)d);
        }

        public  void visit(TemplateDeclaration d) {
            if ((this.hgs).hdrgen || (this.hgs).fullDump && this.visitEponymousMember(d))
                return ;
            if ((this.hgs).ddoc)
                (this.buf).writestring(d.kind());
            else
                (this.buf).writestring(new ByteSlice("template"));
            (this.buf).writeByte(32);
            (this.buf).writestring(d.ident.asString());
            (this.buf).writeByte(40);
            this.visitTemplateParameters((this.hgs).ddoc ? d.origParameters : d.parameters);
            (this.buf).writeByte(41);
            this.visitTemplateConstraint(d.constraint);
            if ((this.hgs).hdrgen || (this.hgs).fullDump)
            {
                (this.hgs).tpltMember++;
                (this.buf).writenl();
                (this.buf).writeByte(123);
                (this.buf).writenl();
                (this.buf).level++;
                {
                    Slice<Dsymbol> __r1447 = (d.members).opSlice().copy();
                    int __key1448 = 0;
                    for (; (__key1448 < __r1447.getLength());__key1448 += 1) {
                        Dsymbol s = __r1447.get(__key1448);
                        s.accept(this);
                    }
                }
                (this.buf).level--;
                (this.buf).writeByte(125);
                (this.buf).writenl();
                (this.hgs).tpltMember--;
            }
        }

        public  boolean visitEponymousMember(TemplateDeclaration d) {
            if ((d.members == null) || ((d.members).length != 1))
                return false;
            Dsymbol onemember = (d.members).get(0);
            if ((!pequals(onemember.ident, d.ident)))
                return false;
            {
                FuncDeclaration fd = onemember.isFuncDeclaration();
                if ((fd) != null)
                {
                    assert(fd.type != null);
                    if (stcToBuffer(this.buf, fd.storage_class))
                        (this.buf).writeByte(32);
                    functionToBufferFull((TypeFunction)fd.type, this.buf, d.ident, this.hgs, d);
                    this.visitTemplateConstraint(d.constraint);
                    (this.hgs).tpltMember++;
                    this.bodyToBuffer(fd);
                    (this.hgs).tpltMember--;
                    return true;
                }
            }
            {
                AggregateDeclaration ad = onemember.isAggregateDeclaration();
                if ((ad) != null)
                {
                    (this.buf).writestring(ad.kind());
                    (this.buf).writeByte(32);
                    (this.buf).writestring(ad.ident.asString());
                    (this.buf).writeByte(40);
                    this.visitTemplateParameters((this.hgs).ddoc ? d.origParameters : d.parameters);
                    (this.buf).writeByte(41);
                    this.visitTemplateConstraint(d.constraint);
                    this.visitBaseClasses(ad.isClassDeclaration());
                    (this.hgs).tpltMember++;
                    if (ad.members != null)
                    {
                        (this.buf).writenl();
                        (this.buf).writeByte(123);
                        (this.buf).writenl();
                        (this.buf).level++;
                        {
                            Slice<Dsymbol> __r1449 = (ad.members).opSlice().copy();
                            int __key1450 = 0;
                            for (; (__key1450 < __r1449.getLength());__key1450 += 1) {
                                Dsymbol s = __r1449.get(__key1450);
                                s.accept(this);
                            }
                        }
                        (this.buf).level--;
                        (this.buf).writeByte(125);
                    }
                    else
                        (this.buf).writeByte(59);
                    (this.buf).writenl();
                    (this.hgs).tpltMember--;
                    return true;
                }
            }
            {
                VarDeclaration vd = onemember.isVarDeclaration();
                if ((vd) != null)
                {
                    if (d.constraint != null)
                        return false;
                    if (stcToBuffer(this.buf, vd.storage_class))
                        (this.buf).writeByte(32);
                    if (vd.type != null)
                        typeToBuffer(vd.type, vd.ident, this.buf, this.hgs);
                    else
                        (this.buf).writestring(vd.ident.asString());
                    (this.buf).writeByte(40);
                    this.visitTemplateParameters((this.hgs).ddoc ? d.origParameters : d.parameters);
                    (this.buf).writeByte(41);
                    if (vd._init != null)
                    {
                        (this.buf).writestring(new ByteSlice(" = "));
                        ExpInitializer ie = vd._init.isExpInitializer();
                        if ((ie != null) && ((ie.exp.op & 0xFF) == 95) || ((ie.exp.op & 0xFF) == 96))
                            expressionToBuffer(((AssignExp)ie.exp).e2, this.buf, this.hgs);
                        else
                            initializerToBuffer(vd._init, this.buf, this.hgs);
                    }
                    (this.buf).writeByte(59);
                    (this.buf).writenl();
                    return true;
                }
            }
            return false;
        }

        public  void visitTemplateParameters(DArray<TemplateParameter> parameters) {
            if ((parameters == null) || ((parameters).length == 0))
                return ;
            {
                Slice<TemplateParameter> __r1452 = (parameters).opSlice().copy();
                int __key1451 = 0;
                for (; (__key1451 < __r1452.getLength());__key1451 += 1) {
                    TemplateParameter p = __r1452.get(__key1451);
                    int i = __key1451;
                    if (i != 0)
                        (this.buf).writestring(new ByteSlice(", "));
                    templateParameterToBuffer(p, this.buf, this.hgs);
                }
            }
        }

        public  void visitTemplateConstraint(Expression constraint) {
            if (constraint == null)
                return ;
            (this.buf).writestring(new ByteSlice(" if ("));
            expressionToBuffer(constraint, this.buf, this.hgs);
            (this.buf).writeByte(41);
        }

        public  void visit(TemplateInstance ti) {
            (this.buf).writestring(ti.name.toChars());
            tiargsToBuffer(ti, this.buf, this.hgs);
            if ((this.hgs).fullDump)
            {
                (this.buf).writenl();
                dumpTemplateInstance(ti, this.buf, this.hgs);
            }
        }

        public  void visit(TemplateMixin tm) {
            (this.buf).writestring(new ByteSlice("mixin "));
            typeToBuffer(tm.tqual, null, this.buf, this.hgs);
            tiargsToBuffer(tm, this.buf, this.hgs);
            if ((tm.ident != null) && (memcmp(tm.ident.toChars(), new BytePtr("__mixin"), 7) != 0))
            {
                (this.buf).writeByte(32);
                (this.buf).writestring(tm.ident.asString());
            }
            (this.buf).writeByte(59);
            (this.buf).writenl();
            if ((this.hgs).fullDump)
                dumpTemplateInstance(tm, this.buf, this.hgs);
        }

        public  void visit(EnumDeclaration d) {
            EnumDeclaration oldInEnumDecl = (this.hgs).inEnumDecl;
            try {
                (this.hgs).inEnumDecl = d;
                (this.buf).writestring(new ByteSlice("enum "));
                if (d.ident != null)
                {
                    (this.buf).writestring(d.ident.asString());
                    (this.buf).writeByte(32);
                }
                if (d.memtype != null)
                {
                    (this.buf).writestring(new ByteSlice(": "));
                    typeToBuffer(d.memtype, null, this.buf, this.hgs);
                }
                if (d.members == null)
                {
                    (this.buf).writeByte(59);
                    (this.buf).writenl();
                    return ;
                }
                (this.buf).writenl();
                (this.buf).writeByte(123);
                (this.buf).writenl();
                (this.buf).level++;
                {
                    Slice<Dsymbol> __r1453 = (d.members).opSlice().copy();
                    int __key1454 = 0;
                    for (; (__key1454 < __r1453.getLength());__key1454 += 1) {
                        Dsymbol em = __r1453.get(__key1454);
                        if (em == null)
                            continue;
                        em.accept(this);
                        (this.buf).writeByte(44);
                        (this.buf).writenl();
                    }
                }
                (this.buf).level--;
                (this.buf).writeByte(125);
                (this.buf).writenl();
            }
            finally {
                (this.hgs).inEnumDecl = oldInEnumDecl;
            }
        }

        public  void visit(Nspace d) {
            (this.buf).writestring(new ByteSlice("extern (C++, "));
            (this.buf).writestring(d.ident.asString());
            (this.buf).writeByte(41);
            (this.buf).writenl();
            (this.buf).writeByte(123);
            (this.buf).writenl();
            (this.buf).level++;
            {
                Slice<Dsymbol> __r1455 = (d.members).opSlice().copy();
                int __key1456 = 0;
                for (; (__key1456 < __r1455.getLength());__key1456 += 1) {
                    Dsymbol s = __r1455.get(__key1456);
                    s.accept(this);
                }
            }
            (this.buf).level--;
            (this.buf).writeByte(125);
            (this.buf).writenl();
        }

        public  void visit(StructDeclaration d) {
            (this.buf).writestring(d.kind());
            (this.buf).writeByte(32);
            if (!d.isAnonymous())
                (this.buf).writestring(d.toChars());
            if (d.members == null)
            {
                (this.buf).writeByte(59);
                (this.buf).writenl();
                return ;
            }
            (this.buf).writenl();
            (this.buf).writeByte(123);
            (this.buf).writenl();
            (this.buf).level++;
            {
                Slice<Dsymbol> __r1457 = (d.members).opSlice().copy();
                int __key1458 = 0;
                for (; (__key1458 < __r1457.getLength());__key1458 += 1) {
                    Dsymbol s = __r1457.get(__key1458);
                    s.accept(this);
                }
            }
            (this.buf).level--;
            (this.buf).writeByte(125);
            (this.buf).writenl();
        }

        public  void visit(ClassDeclaration d) {
            if (!d.isAnonymous())
            {
                (this.buf).writestring(d.kind());
                (this.buf).writeByte(32);
                (this.buf).writestring(d.ident.asString());
            }
            this.visitBaseClasses(d);
            if (d.members != null)
            {
                (this.buf).writenl();
                (this.buf).writeByte(123);
                (this.buf).writenl();
                (this.buf).level++;
                {
                    Slice<Dsymbol> __r1459 = (d.members).opSlice().copy();
                    int __key1460 = 0;
                    for (; (__key1460 < __r1459.getLength());__key1460 += 1) {
                        Dsymbol s = __r1459.get(__key1460);
                        s.accept(this);
                    }
                }
                (this.buf).level--;
                (this.buf).writeByte(125);
            }
            else
                (this.buf).writeByte(59);
            (this.buf).writenl();
        }

        public  void visitBaseClasses(ClassDeclaration d) {
            if ((d == null) || ((d.baseclasses).length == 0))
                return ;
            if (!d.isAnonymous())
                (this.buf).writestring(new ByteSlice(" : "));
            {
                Slice<BaseClass> __r1462 = (d.baseclasses).opSlice().copy();
                int __key1461 = 0;
                for (; (__key1461 < __r1462.getLength());__key1461 += 1) {
                    BaseClass b = __r1462.get(__key1461);
                    int i = __key1461;
                    if (i != 0)
                        (this.buf).writestring(new ByteSlice(", "));
                    typeToBuffer((b).type, null, this.buf, this.hgs);
                }
            }
        }

        public  void visit(AliasDeclaration d) {
            if ((d.storage_class & 2251799813685248L) != 0)
                return ;
            (this.buf).writestring(new ByteSlice("alias "));
            if (d.aliassym != null)
            {
                (this.buf).writestring(d.ident.asString());
                (this.buf).writestring(new ByteSlice(" = "));
                if (stcToBuffer(this.buf, d.storage_class))
                    (this.buf).writeByte(32);
                d.aliassym.accept(this);
            }
            else if (((d.type.ty & 0xFF) == ENUMTY.Tfunction))
            {
                if (stcToBuffer(this.buf, d.storage_class))
                    (this.buf).writeByte(32);
                typeToBuffer(d.type, d.ident, this.buf, this.hgs);
            }
            else if (d.ident != null)
            {
                expr((this.hgs).declstring = (pequals(d.ident, Id.string)) || (pequals(d.ident, Id.wstring)) || (pequals(d.ident, Id.dstring)));
                (this.buf).writestring(d.ident.asString());
                (this.buf).writestring(new ByteSlice(" = "));
                if (stcToBuffer(this.buf, d.storage_class))
                    (this.buf).writeByte(32);
                typeToBuffer(d.type, null, this.buf, this.hgs);
                expr((this.hgs).declstring = false);
            }
            (this.buf).writeByte(59);
            (this.buf).writenl();
        }

        public  void visit(VarDeclaration d) {
            if ((d.storage_class & 2251799813685248L) != 0)
                return ;
            this.visitVarDecl(d, false);
            (this.buf).writeByte(59);
            (this.buf).writenl();
        }

        public  void visitVarDecl(VarDeclaration v, boolean anywritten) {
            if (anywritten)
            {
                (this.buf).writestring(new ByteSlice(", "));
                (this.buf).writestring(v.ident.asString());
            }
            else
            {
                if (stcToBuffer(this.buf, v.storage_class))
                    (this.buf).writeByte(32);
                if (v.type != null)
                    typeToBuffer(v.type, v.ident, this.buf, this.hgs);
                else
                    (this.buf).writestring(v.ident.asString());
            }
            if (v._init != null)
            {
                (this.buf).writestring(new ByteSlice(" = "));
                ExpInitializer ie = v._init.isExpInitializer();
                if ((ie != null) && ((ie.exp.op & 0xFF) == 95) || ((ie.exp.op & 0xFF) == 96))
                    expressionToBuffer(((AssignExp)ie.exp).e2, this.buf, this.hgs);
                else
                    initializerToBuffer(v._init, this.buf, this.hgs);
            }
        }

        public  void visit(FuncDeclaration f) {
            if (stcToBuffer(this.buf, f.storage_class))
                (this.buf).writeByte(32);
            TypeFunction tf = (TypeFunction)f.type;
            typeToBuffer(tf, f.ident, this.buf, this.hgs);
            if ((this.hgs).hdrgen)
            {
                if ((tf.next == null) || ((f.storage_class & 256L) != 0))
                {
                    (this.hgs).autoMember++;
                    this.bodyToBuffer(f);
                    (this.hgs).autoMember--;
                }
                else if (((this.hgs).tpltMember == 0) && global.params.hdrStripPlainFunctions)
                {
                    (this.buf).writeByte(59);
                    (this.buf).writenl();
                }
                else
                    this.bodyToBuffer(f);
            }
            else
                this.bodyToBuffer(f);
        }

        public  void bodyToBuffer(FuncDeclaration f) {
            if ((f.fbody == null) || (this.hgs).hdrgen && global.params.hdrStripPlainFunctions && ((this.hgs).autoMember == 0) && ((this.hgs).tpltMember == 0))
            {
                (this.buf).writeByte(59);
                (this.buf).writenl();
                return ;
            }
            int savetlpt = (this.hgs).tpltMember;
            int saveauto = (this.hgs).autoMember;
            (this.hgs).tpltMember = 0;
            (this.hgs).autoMember = 0;
            (this.buf).writenl();
            boolean requireDo = false;
            if (f.frequires != null)
            {
                {
                    Slice<Statement> __r1463 = (f.frequires).opSlice().copy();
                    int __key1464 = 0;
                    for (; (__key1464 < __r1463.getLength());__key1464 += 1) {
                        Statement frequire = __r1463.get(__key1464);
                        (this.buf).writestring(new ByteSlice("in"));
                        {
                            ExpStatement es = frequire.isExpStatement();
                            if ((es) != null)
                            {
                                assert((es.exp != null) && ((es.exp.op & 0xFF) == 14));
                                (this.buf).writestring(new ByteSlice(" ("));
                                expressionToBuffer(((AssertExp)es.exp).e1, this.buf, this.hgs);
                                (this.buf).writeByte(41);
                                (this.buf).writenl();
                                expr(requireDo = false);
                            }
                            else
                            {
                                (this.buf).writenl();
                                statementToBuffer(frequire, this.buf, this.hgs);
                                expr(requireDo = true);
                            }
                        }
                    }
                }
            }
            if (f.fensures != null)
            {
                {
                    Slice<Ensure> __r1465 = (f.fensures).opSlice().copy();
                    int __key1466 = 0;
                    for (; (__key1466 < __r1465.getLength());__key1466 += 1) {
                        Ensure fensure = __r1465.get(__key1466).copy();
                        (this.buf).writestring(new ByteSlice("out"));
                        {
                            ExpStatement es = fensure.ensure.isExpStatement();
                            if ((es) != null)
                            {
                                assert((es.exp != null) && ((es.exp.op & 0xFF) == 14));
                                (this.buf).writestring(new ByteSlice(" ("));
                                if (fensure.id != null)
                                {
                                    (this.buf).writestring(fensure.id.asString());
                                }
                                (this.buf).writestring(new ByteSlice("; "));
                                expressionToBuffer(((AssertExp)es.exp).e1, this.buf, this.hgs);
                                (this.buf).writeByte(41);
                                (this.buf).writenl();
                                expr(requireDo = false);
                            }
                            else
                            {
                                if (fensure.id != null)
                                {
                                    (this.buf).writeByte(40);
                                    (this.buf).writestring(fensure.id.asString());
                                    (this.buf).writeByte(41);
                                }
                                (this.buf).writenl();
                                statementToBuffer(fensure.ensure, this.buf, this.hgs);
                                expr(requireDo = true);
                            }
                        }
                    }
                }
            }
            if (requireDo)
            {
                (this.buf).writestring(new ByteSlice("do"));
                (this.buf).writenl();
            }
            (this.buf).writeByte(123);
            (this.buf).writenl();
            (this.buf).level++;
            statementToBuffer(f.fbody, this.buf, this.hgs);
            (this.buf).level--;
            (this.buf).writeByte(125);
            (this.buf).writenl();
            (this.hgs).tpltMember = savetlpt;
            (this.hgs).autoMember = saveauto;
        }

        public  void visit(FuncLiteralDeclaration f) {
            if (((f.type.ty & 0xFF) == ENUMTY.Terror))
            {
                (this.buf).writestring(new ByteSlice("__error"));
                return ;
            }
            if (((f.tok & 0xFF) != 0))
            {
                (this.buf).writestring(f.kind());
                (this.buf).writeByte(32);
            }
            TypeFunction tf = (TypeFunction)f.type;
            if (!f.inferRetType && (tf.next != null))
                typeToBuffer(tf.next, null, this.buf, this.hgs);
            parametersToBuffer(tf.parameterList, this.buf, this.hgs);
            CompoundStatement cs = f.fbody.isCompoundStatement();
            Statement s1 = null;
            if ((f.semanticRun >= PASS.semantic3done) && (cs != null))
            {
                s1 = (cs.statements).get((cs.statements).length - 1);
            }
            else
                s1 = cs == null ? f.fbody : null;
            ReturnStatement rs = s1 != null ? s1.isReturnStatement() : null;
            if ((rs != null) && (rs.exp != null))
            {
                (this.buf).writestring(new ByteSlice(" => "));
                expressionToBuffer(rs.exp, this.buf, this.hgs);
            }
            else
            {
                (this.hgs).tpltMember++;
                this.bodyToBuffer(f);
                (this.hgs).tpltMember--;
            }
        }

        public  void visit(PostBlitDeclaration d) {
            if (stcToBuffer(this.buf, d.storage_class))
                (this.buf).writeByte(32);
            (this.buf).writestring(new ByteSlice("this(this)"));
            this.bodyToBuffer(d);
        }

        public  void visit(DtorDeclaration d) {
            if ((d.storage_class & 17179869184L) != 0)
                (this.buf).writestring(new ByteSlice("@trusted "));
            if ((d.storage_class & 8589934592L) != 0)
                (this.buf).writestring(new ByteSlice("@safe "));
            if ((d.storage_class & 4398046511104L) != 0)
                (this.buf).writestring(new ByteSlice("@nogc "));
            if ((d.storage_class & 137438953472L) != 0)
                (this.buf).writestring(new ByteSlice("@disable "));
            (this.buf).writestring(new ByteSlice("~this()"));
            this.bodyToBuffer(d);
        }

        public  void visit(StaticCtorDeclaration d) {
            if (stcToBuffer(this.buf, d.storage_class & -2L))
                (this.buf).writeByte(32);
            if (d.isSharedStaticCtorDeclaration() != null)
                (this.buf).writestring(new ByteSlice("shared "));
            (this.buf).writestring(new ByteSlice("static this()"));
            if ((this.hgs).hdrgen && ((this.hgs).tpltMember == 0))
            {
                (this.buf).writeByte(59);
                (this.buf).writenl();
            }
            else
                this.bodyToBuffer(d);
        }

        public  void visit(StaticDtorDeclaration d) {
            if (stcToBuffer(this.buf, d.storage_class & -2L))
                (this.buf).writeByte(32);
            if (d.isSharedStaticDtorDeclaration() != null)
                (this.buf).writestring(new ByteSlice("shared "));
            (this.buf).writestring(new ByteSlice("static ~this()"));
            if ((this.hgs).hdrgen && ((this.hgs).tpltMember == 0))
            {
                (this.buf).writeByte(59);
                (this.buf).writenl();
            }
            else
                this.bodyToBuffer(d);
        }

        public  void visit(InvariantDeclaration d) {
            if ((this.hgs).hdrgen)
                return ;
            if (stcToBuffer(this.buf, d.storage_class))
                (this.buf).writeByte(32);
            (this.buf).writestring(new ByteSlice("invariant"));
            {
                ExpStatement es = d.fbody.isExpStatement();
                if ((es) != null)
                {
                    assert((es.exp != null) && ((es.exp.op & 0xFF) == 14));
                    (this.buf).writestring(new ByteSlice(" ("));
                    expressionToBuffer(((AssertExp)es.exp).e1, this.buf, this.hgs);
                    (this.buf).writestring(new ByteSlice(");"));
                    (this.buf).writenl();
                }
                else
                {
                    this.bodyToBuffer(d);
                }
            }
        }

        public  void visit(UnitTestDeclaration d) {
            if ((this.hgs).hdrgen)
                return ;
            if (stcToBuffer(this.buf, d.storage_class))
                (this.buf).writeByte(32);
            (this.buf).writestring(new ByteSlice("unittest"));
            this.bodyToBuffer(d);
        }

        public  void visit(NewDeclaration d) {
            if (stcToBuffer(this.buf, d.storage_class & -2L))
                (this.buf).writeByte(32);
            (this.buf).writestring(new ByteSlice("new"));
            parametersToBuffer(new ParameterList(d.parameters, d.varargs), this.buf, this.hgs);
            this.bodyToBuffer(d);
        }

        public  void visit(DeleteDeclaration d) {
            if (stcToBuffer(this.buf, d.storage_class & -2L))
                (this.buf).writeByte(32);
            (this.buf).writestring(new ByteSlice("delete"));
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
        public OutBuffer buf;
        public HdrGenState hgs;
        public  ExpressionPrettyPrintVisitor(OutBuffer buf, HdrGenState hgs) {
            this.buf = buf;
            this.hgs = hgs;
        }

        public  void visit(Expression e) {
            (this.buf).writestring(Token.asString(e.op));
        }

        public  void visit(IntegerExp e) {
            long v = e.toInteger();
            if (e.type != null)
            {
                Type t = e.type;
            /*L1:*/
                {
                    int __dispatch1 = 0;
                    dispatched_1:
                    do {
                        switch (__dispatch1 != 0 ? __dispatch1 : (t.ty & 0xFF))
                        {
                            case 9:
                                TypeEnum te = (TypeEnum)t;
                                if ((this.hgs).fullDump)
                                {
                                    EnumDeclaration sym = te.sym;
                                    if ((!pequals((this.hgs).inEnumDecl, sym)))
                                    {
                                        int __key1467 = 0;
                                        int __limit1468 = (sym.members).length;
                                        for (; (__key1467 < __limit1468);__key1467 += 1) {
                                            int i = __key1467;
                                            EnumMember em = (EnumMember)(sym.members).get(i);
                                            if ((em.value().toInteger() == v))
                                            {
                                                (this.buf).printf(new BytePtr("%s.%s"), sym.toChars(), em.ident.toChars());
                                                return ;
                                            }
                                        }
                                    }
                                }
                                (this.buf).printf(new BytePtr("cast(%s)"), te.sym.toChars());
                                t = te.sym.memtype;
                                /*goto L1*/throw Dispatch0.INSTANCE;
                            case 32:
                            case 33:
                                if ((v > 255L))
                                {
                                    (this.buf).printf(new BytePtr("'\\U%08x'"), v);
                                    break;
                                }
                            case 31:
                                int o = (this.buf).offset;
                                if ((v == 39L))
                                    (this.buf).writestring(new ByteSlice("'\\''"));
                                else if ((isprint((int)v) != 0) && (v != 92L))
                                    (this.buf).printf(new BytePtr("'%c'"), (int)v);
                                else
                                    (this.buf).printf(new BytePtr("'\\x%02x'"), (int)v);
                                if ((this.hgs).ddoc)
                                    escapeDdocString(this.buf, o);
                                break;
                            case 13:
                                (this.buf).writestring(new ByteSlice("cast(byte)"));
                                /*goto L2*/{ __dispatch1 = -1; continue dispatched_1; }
                            case 15:
                                (this.buf).writestring(new ByteSlice("cast(short)"));
                                /*goto L2*/{ __dispatch1 = -1; continue dispatched_1; }
                            case 17:
                            /*L2:*/
                            case -1:
                            __dispatch1 = 0;
                                (this.buf).printf(new BytePtr("%d"), (int)v);
                                break;
                            case 14:
                                (this.buf).writestring(new ByteSlice("cast(ubyte)"));
                                /*goto case*/{ __dispatch1 = 18; continue dispatched_1; }
                            case 16:
                                (this.buf).writestring(new ByteSlice("cast(ushort)"));
                                /*goto case*/{ __dispatch1 = 18; continue dispatched_1; }
                            case 18:
                                __dispatch1 = 0;
                                (this.buf).printf(new BytePtr("%uu"), (int)v);
                                break;
                            case 19:
                                (this.buf).printf(new BytePtr("%lldL"), v);
                                break;
                            case 20:
                                __dispatch1 = 0;
                                (this.buf).printf(new BytePtr("%lluLU"), v);
                                break;
                            case 30:
                                (this.buf).writestring(v != 0 ? new ByteSlice("true") : new ByteSlice("false"));
                                break;
                            case 3:
                                (this.buf).writestring(new ByteSlice("cast("));
                                (this.buf).writestring(t.toChars());
                                (this.buf).writeByte(41);
                                if ((target.ptrsize == 8))
                                    /*goto case*/{ __dispatch1 = 20; continue dispatched_1; }
                                else
                                    /*goto case*/{ __dispatch1 = 18; continue dispatched_1; }
                            default:
                            if (global.errors == 0)
                            {
                                throw new AssertionError("Unreachable code!");
                            }
                            break;
                        }
                    } while(__dispatch1 != 0);
                }
            }
            else if ((v & -9223372036854775808L) != 0)
                (this.buf).printf(new BytePtr("0x%llx"), v);
            else
                (this.buf).print(v);
        }

        public  void visit(ErrorExp e) {
            (this.buf).writestring(new ByteSlice("__error"));
        }

        public  void visit(VoidInitExp e) {
            (this.buf).writestring(new ByteSlice("__void"));
        }

        public  void floatToBuffer(Type type, double value) {
            int BUFFER_LEN = 58;
            ByteSlice buffer = (byte)255;
            CTFloat.sprint(ptr(buffer), (byte)103, value);
            assert((strlen(ptr(buffer)) < 58));
            if ((this.hgs).hdrgen)
            {
                double r = CTFloat.parse(ptr(buffer), null);
                if ((r != value))
                    CTFloat.sprint(ptr(buffer), (byte)97, value);
            }
            (this.buf).writestring(ptr(buffer));
            if (type != null)
            {
                Type t = type.toBasetype();
                switch ((t.ty & 0xFF))
                {
                    case 21:
                    case 24:
                    case 27:
                        (this.buf).writeByte(70);
                        break;
                    case 23:
                    case 26:
                    case 29:
                        (this.buf).writeByte(76);
                        break;
                    default:
                    break;
                }
                if (t.isimaginary())
                    (this.buf).writeByte(105);
            }
        }

        public  void visit(RealExp e) {
            this.floatToBuffer(e.type, e.value);
        }

        public  void visit(ComplexExp e) {
            (this.buf).writeByte(40);
            this.floatToBuffer(e.type, creall(e.value));
            (this.buf).writeByte(43);
            this.floatToBuffer(e.type, cimagl(e.value));
            (this.buf).writestring(new ByteSlice("i)"));
        }

        public  void visit(IdentifierExp e) {
            if ((this.hgs).hdrgen || (this.hgs).ddoc)
                (this.buf).writestring(e.ident.toHChars2());
            else
                (this.buf).writestring(e.ident.asString());
        }

        public  void visit(DsymbolExp e) {
            (this.buf).writestring(e.s.toChars());
        }

        public  void visit(ThisExp e) {
            (this.buf).writestring(new ByteSlice("this"));
        }

        public  void visit(SuperExp e) {
            (this.buf).writestring(new ByteSlice("super"));
        }

        public  void visit(NullExp e) {
            (this.buf).writestring(new ByteSlice("null"));
        }

        public  void visit(StringExp e) {
            (this.buf).writeByte(34);
            int o = (this.buf).offset;
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
                                    (this.buf).writeByte(92);
                                    /*goto default*/ { __dispatch3 = -1; continue dispatched_3; }
                                default:
                                __dispatch3 = 0;
                                if ((c <= 255))
                                {
                                    if ((c <= 127) && (isprint(c) != 0))
                                        (this.buf).writeByte(c);
                                    else
                                        (this.buf).printf(new BytePtr("\\x%02x"), c);
                                }
                                else if ((c <= 65535))
                                    (this.buf).printf(new BytePtr("\\x%02x\\x%02x"), c & 255, c >> 8);
                                else
                                    (this.buf).printf(new BytePtr("\\x%02x\\x%02x\\x%02x\\x%02x"), c & 255, c >> 8 & 255, c >> 16 & 255, c >> 24);
                                break;
                            }
                        } while(__dispatch3 != 0);
                    }
                }
            }
            if ((this.hgs).ddoc)
                escapeDdocString(this.buf, o);
            (this.buf).writeByte(34);
            if (e.postfix != 0)
                (this.buf).writeByte((e.postfix & 0xFF));
        }

        public  void visit(ArrayLiteralExp e) {
            (this.buf).writeByte(91);
            argsToBuffer(e.elements, this.buf, this.hgs, e.basis);
            (this.buf).writeByte(93);
        }

        public  void visit(AssocArrayLiteralExp e) {
            (this.buf).writeByte(91);
            {
                Slice<Expression> __r1470 = (e.keys).opSlice().copy();
                int __key1469 = 0;
                for (; (__key1469 < __r1470.getLength());__key1469 += 1) {
                    Expression key = __r1470.get(__key1469);
                    int i = __key1469;
                    if (i != 0)
                        (this.buf).writestring(new ByteSlice(", "));
                    expToBuffer(key, PREC.assign, this.buf, this.hgs);
                    (this.buf).writeByte(58);
                    Expression value = (e.values).get(i);
                    expToBuffer(value, PREC.assign, this.buf, this.hgs);
                }
            }
            (this.buf).writeByte(93);
        }

        public  void visit(StructLiteralExp e) {
            (this.buf).writestring(e.sd.toChars());
            (this.buf).writeByte(40);
            if ((e.stageflags & 32) != 0)
                (this.buf).writestring(new ByteSlice("<recursion>"));
            else
            {
                int old = e.stageflags;
                e.stageflags |= 32;
                argsToBuffer(e.elements, this.buf, this.hgs, null);
                e.stageflags = old;
            }
            (this.buf).writeByte(41);
        }

        public  void visit(TypeExp e) {
            typeToBuffer(e.type, null, this.buf, this.hgs);
        }

        public  void visit(ScopeExp e) {
            if (e.sds.isTemplateInstance() != null)
            {
                dsymbolToBuffer(e.sds, this.buf, this.hgs);
            }
            else if ((this.hgs != null) && (this.hgs).ddoc)
            {
                {
                    dmodule.Module m = e.sds.isModule();
                    if ((m) != null)
                        (this.buf).writestring((m.md).toChars());
                    else
                        (this.buf).writestring(e.sds.toChars());
                }
            }
            else
            {
                (this.buf).writestring(e.sds.kind());
                (this.buf).writeByte(32);
                (this.buf).writestring(e.sds.toChars());
            }
        }

        public  void visit(TemplateExp e) {
            (this.buf).writestring(e.td.toChars());
        }

        public  void visit(NewExp e) {
            if (e.thisexp != null)
            {
                expToBuffer(e.thisexp, PREC.primary, this.buf, this.hgs);
                (this.buf).writeByte(46);
            }
            (this.buf).writestring(new ByteSlice("new "));
            if ((e.newargs != null) && ((e.newargs).length != 0))
            {
                (this.buf).writeByte(40);
                argsToBuffer(e.newargs, this.buf, this.hgs, null);
                (this.buf).writeByte(41);
            }
            typeToBuffer(e.newtype, null, this.buf, this.hgs);
            if ((e.arguments != null) && ((e.arguments).length != 0))
            {
                (this.buf).writeByte(40);
                argsToBuffer(e.arguments, this.buf, this.hgs, null);
                (this.buf).writeByte(41);
            }
        }

        public  void visit(NewAnonClassExp e) {
            if (e.thisexp != null)
            {
                expToBuffer(e.thisexp, PREC.primary, this.buf, this.hgs);
                (this.buf).writeByte(46);
            }
            (this.buf).writestring(new ByteSlice("new"));
            if ((e.newargs != null) && ((e.newargs).length != 0))
            {
                (this.buf).writeByte(40);
                argsToBuffer(e.newargs, this.buf, this.hgs, null);
                (this.buf).writeByte(41);
            }
            (this.buf).writestring(new ByteSlice(" class "));
            if ((e.arguments != null) && ((e.arguments).length != 0))
            {
                (this.buf).writeByte(40);
                argsToBuffer(e.arguments, this.buf, this.hgs, null);
                (this.buf).writeByte(41);
            }
            if (e.cd != null)
                dsymbolToBuffer(e.cd, this.buf, this.hgs);
        }

        public  void visit(SymOffExp e) {
            if (e.offset != 0)
                (this.buf).printf(new BytePtr("(& %s+%u)"), e.var.toChars(), e.offset);
            else if (e.var.isTypeInfoDeclaration() != null)
                (this.buf).writestring(e.var.toChars());
            else
                (this.buf).printf(new BytePtr("& %s"), e.var.toChars());
        }

        public  void visit(VarExp e) {
            (this.buf).writestring(e.var.toChars());
        }

        public  void visit(OverExp e) {
            (this.buf).writestring(e.vars.ident.asString());
        }

        public  void visit(TupleExp e) {
            if (e.e0 != null)
            {
                (this.buf).writeByte(40);
                e.e0.accept(this);
                (this.buf).writestring(new ByteSlice(", tuple("));
                argsToBuffer(e.exps, this.buf, this.hgs, null);
                (this.buf).writestring(new ByteSlice("))"));
            }
            else
            {
                (this.buf).writestring(new ByteSlice("tuple("));
                argsToBuffer(e.exps, this.buf, this.hgs, null);
                (this.buf).writeByte(41);
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
                        (this.buf).writeByte(40);
                        DsymbolPrettyPrintVisitor v = new DsymbolPrettyPrintVisitor(this.buf, this.hgs);
                        v.visitVarDecl(var, false);
                        (this.buf).writeByte(59);
                        (this.buf).writeByte(41);
                    }
                    else
                        dsymbolToBuffer(e.declaration, this.buf, this.hgs);
                }
            }
        }

        public  void visit(TypeidExp e) {
            (this.buf).writestring(new ByteSlice("typeid("));
            objectToBuffer(e.obj, this.buf, this.hgs);
            (this.buf).writeByte(41);
        }

        public  void visit(TraitsExp e) {
            (this.buf).writestring(new ByteSlice("__traits("));
            if (e.ident != null)
                (this.buf).writestring(e.ident.asString());
            if (e.args != null)
            {
                {
                    Slice<RootObject> __r1471 = (e.args).opSlice().copy();
                    int __key1472 = 0;
                    for (; (__key1472 < __r1471.getLength());__key1472 += 1) {
                        RootObject arg = __r1471.get(__key1472);
                        (this.buf).writestring(new ByteSlice(", "));
                        objectToBuffer(arg, this.buf, this.hgs);
                    }
                }
            }
            (this.buf).writeByte(41);
        }

        public  void visit(HaltExp e) {
            (this.buf).writestring(new ByteSlice("halt"));
        }

        public  void visit(IsExp e) {
            (this.buf).writestring(new ByteSlice("is("));
            typeToBuffer(e.targ, e.id, this.buf, this.hgs);
            if (((e.tok2 & 0xFF) != 0))
            {
                (this.buf).printf(new BytePtr(" %s %s"), Token.toChars(e.tok), Token.toChars(e.tok2));
            }
            else if (e.tspec != null)
            {
                if (((e.tok & 0xFF) == 7))
                    (this.buf).writestring(new ByteSlice(" : "));
                else
                    (this.buf).writestring(new ByteSlice(" == "));
                typeToBuffer(e.tspec, null, this.buf, this.hgs);
            }
            if ((e.parameters != null) && ((e.parameters).length != 0))
            {
                (this.buf).writestring(new ByteSlice(", "));
                DsymbolPrettyPrintVisitor v = new DsymbolPrettyPrintVisitor(this.buf, this.hgs);
                v.visitTemplateParameters(e.parameters);
            }
            (this.buf).writeByte(41);
        }

        public  void visit(UnaExp e) {
            (this.buf).writestring(Token.asString(e.op));
            expToBuffer(e.e1, precedence.get((e.op & 0xFF)), this.buf, this.hgs);
        }

        public  void visit(BinExp e) {
            expToBuffer(e.e1, precedence.get((e.op & 0xFF)), this.buf, this.hgs);
            (this.buf).writeByte(32);
            (this.buf).writestring(Token.asString(e.op));
            (this.buf).writeByte(32);
            expToBuffer(e.e2, precedence.get((e.op & 0xFF)) + 1, this.buf, this.hgs);
        }

        public  void visit(CompileExp e) {
            (this.buf).writestring(new ByteSlice("mixin("));
            argsToBuffer(e.exps, this.buf, this.hgs, null);
            (this.buf).writeByte(41);
        }

        public  void visit(ImportExp e) {
            (this.buf).writestring(new ByteSlice("import("));
            expToBuffer(e.e1, PREC.assign, this.buf, this.hgs);
            (this.buf).writeByte(41);
        }

        public  void visit(AssertExp e) {
            (this.buf).writestring(new ByteSlice("assert("));
            expToBuffer(e.e1, PREC.assign, this.buf, this.hgs);
            if (e.msg != null)
            {
                (this.buf).writestring(new ByteSlice(", "));
                expToBuffer(e.msg, PREC.assign, this.buf, this.hgs);
            }
            (this.buf).writeByte(41);
        }

        public  void visit(DotIdExp e) {
            expToBuffer(e.e1, PREC.primary, this.buf, this.hgs);
            (this.buf).writeByte(46);
            (this.buf).writestring(e.ident.asString());
        }

        public  void visit(DotTemplateExp e) {
            expToBuffer(e.e1, PREC.primary, this.buf, this.hgs);
            (this.buf).writeByte(46);
            (this.buf).writestring(e.td.toChars());
        }

        public  void visit(DotVarExp e) {
            expToBuffer(e.e1, PREC.primary, this.buf, this.hgs);
            (this.buf).writeByte(46);
            (this.buf).writestring(e.var.toChars());
        }

        public  void visit(DotTemplateInstanceExp e) {
            expToBuffer(e.e1, PREC.primary, this.buf, this.hgs);
            (this.buf).writeByte(46);
            dsymbolToBuffer(e.ti, this.buf, this.hgs);
        }

        public  void visit(DelegateExp e) {
            (this.buf).writeByte(38);
            if (!e.func.isNested() || e.func.needThis())
            {
                expToBuffer(e.e1, PREC.primary, this.buf, this.hgs);
                (this.buf).writeByte(46);
            }
            (this.buf).writestring(e.func.toChars());
        }

        public  void visit(DotTypeExp e) {
            expToBuffer(e.e1, PREC.primary, this.buf, this.hgs);
            (this.buf).writeByte(46);
            (this.buf).writestring(e.sym.toChars());
        }

        public  void visit(CallExp e) {
            if (((e.e1.op & 0xFF) == 20))
            {
                e.e1.accept(this);
            }
            else
                expToBuffer(e.e1, precedence.get((e.op & 0xFF)), this.buf, this.hgs);
            (this.buf).writeByte(40);
            argsToBuffer(e.arguments, this.buf, this.hgs, null);
            (this.buf).writeByte(41);
        }

        public  void visit(PtrExp e) {
            (this.buf).writeByte(42);
            expToBuffer(e.e1, precedence.get((e.op & 0xFF)), this.buf, this.hgs);
        }

        public  void visit(DeleteExp e) {
            (this.buf).writestring(new ByteSlice("delete "));
            expToBuffer(e.e1, precedence.get((e.op & 0xFF)), this.buf, this.hgs);
        }

        public  void visit(CastExp e) {
            (this.buf).writestring(new ByteSlice("cast("));
            if (e.to != null)
                typeToBuffer(e.to, null, this.buf, this.hgs);
            else
            {
                MODtoBuffer(this.buf, e.mod);
            }
            (this.buf).writeByte(41);
            expToBuffer(e.e1, precedence.get((e.op & 0xFF)), this.buf, this.hgs);
        }

        public  void visit(VectorExp e) {
            (this.buf).writestring(new ByteSlice("cast("));
            typeToBuffer(e.to, null, this.buf, this.hgs);
            (this.buf).writeByte(41);
            expToBuffer(e.e1, precedence.get((e.op & 0xFF)), this.buf, this.hgs);
        }

        public  void visit(VectorArrayExp e) {
            expToBuffer(e.e1, PREC.primary, this.buf, this.hgs);
            (this.buf).writestring(new ByteSlice(".array"));
        }

        public  void visit(SliceExp e) {
            expToBuffer(e.e1, precedence.get((e.op & 0xFF)), this.buf, this.hgs);
            (this.buf).writeByte(91);
            if ((e.upr != null) || (e.lwr != null))
            {
                if (e.lwr != null)
                    sizeToBuffer(e.lwr, this.buf, this.hgs);
                else
                    (this.buf).writeByte(48);
                (this.buf).writestring(new ByteSlice(".."));
                if (e.upr != null)
                    sizeToBuffer(e.upr, this.buf, this.hgs);
                else
                    (this.buf).writeByte(36);
            }
            (this.buf).writeByte(93);
        }

        public  void visit(ArrayLengthExp e) {
            expToBuffer(e.e1, PREC.primary, this.buf, this.hgs);
            (this.buf).writestring(new ByteSlice(".length"));
        }

        public  void visit(IntervalExp e) {
            expToBuffer(e.lwr, PREC.assign, this.buf, this.hgs);
            (this.buf).writestring(new ByteSlice(".."));
            expToBuffer(e.upr, PREC.assign, this.buf, this.hgs);
        }

        public  void visit(DelegatePtrExp e) {
            expToBuffer(e.e1, PREC.primary, this.buf, this.hgs);
            (this.buf).writestring(new ByteSlice(".ptr"));
        }

        public  void visit(DelegateFuncptrExp e) {
            expToBuffer(e.e1, PREC.primary, this.buf, this.hgs);
            (this.buf).writestring(new ByteSlice(".funcptr"));
        }

        public  void visit(ArrayExp e) {
            expToBuffer(e.e1, PREC.primary, this.buf, this.hgs);
            (this.buf).writeByte(91);
            argsToBuffer(e.arguments, this.buf, this.hgs, null);
            (this.buf).writeByte(93);
        }

        public  void visit(DotExp e) {
            expToBuffer(e.e1, PREC.primary, this.buf, this.hgs);
            (this.buf).writeByte(46);
            expToBuffer(e.e2, PREC.primary, this.buf, this.hgs);
        }

        public  void visit(IndexExp e) {
            expToBuffer(e.e1, PREC.primary, this.buf, this.hgs);
            (this.buf).writeByte(91);
            sizeToBuffer(e.e2, this.buf, this.hgs);
            (this.buf).writeByte(93);
        }

        public  void visit(PostExp e) {
            expToBuffer(e.e1, precedence.get((e.op & 0xFF)), this.buf, this.hgs);
            (this.buf).writestring(Token.asString(e.op));
        }

        public  void visit(PreExp e) {
            (this.buf).writestring(Token.asString(e.op));
            expToBuffer(e.e1, precedence.get((e.op & 0xFF)), this.buf, this.hgs);
        }

        public  void visit(RemoveExp e) {
            expToBuffer(e.e1, PREC.primary, this.buf, this.hgs);
            (this.buf).writestring(new ByteSlice(".remove("));
            expToBuffer(e.e2, PREC.assign, this.buf, this.hgs);
            (this.buf).writeByte(41);
        }

        public  void visit(CondExp e) {
            expToBuffer(e.econd, PREC.oror, this.buf, this.hgs);
            (this.buf).writestring(new ByteSlice(" ? "));
            expToBuffer(e.e1, PREC.expr, this.buf, this.hgs);
            (this.buf).writestring(new ByteSlice(" : "));
            expToBuffer(e.e2, PREC.cond, this.buf, this.hgs);
        }

        public  void visit(DefaultInitExp e) {
            (this.buf).writestring(Token.asString(e.subop));
        }

        public  void visit(ClassReferenceExp e) {
            (this.buf).writestring(e.value.toChars());
        }


        public ExpressionPrettyPrintVisitor() {}

        public ExpressionPrettyPrintVisitor copy() {
            ExpressionPrettyPrintVisitor that = new ExpressionPrettyPrintVisitor();
            that.buf = this.buf;
            that.hgs = this.hgs;
            return that;
        }
    }
    public static void templateParameterToBuffer(TemplateParameter tp, OutBuffer buf, HdrGenState hgs) {
        TemplateParameterPrettyPrintVisitor v = new TemplateParameterPrettyPrintVisitor(buf, hgs);
        tp.accept(v);
    }

    public static class TemplateParameterPrettyPrintVisitor extends Visitor
    {
        public OutBuffer buf;
        public HdrGenState hgs;
        public  TemplateParameterPrettyPrintVisitor(OutBuffer buf, HdrGenState hgs) {
            this.buf = buf;
            this.hgs = hgs;
        }

        public  void visit(TemplateTypeParameter tp) {
            (this.buf).writestring(tp.ident.asString());
            if (tp.specType != null)
            {
                (this.buf).writestring(new ByteSlice(" : "));
                typeToBuffer(tp.specType, null, this.buf, this.hgs);
            }
            if (tp.defaultType != null)
            {
                (this.buf).writestring(new ByteSlice(" = "));
                typeToBuffer(tp.defaultType, null, this.buf, this.hgs);
            }
        }

        public  void visit(TemplateThisParameter tp) {
            (this.buf).writestring(new ByteSlice("this "));
            this.visit((TemplateTypeParameter)tp);
        }

        public  void visit(TemplateAliasParameter tp) {
            (this.buf).writestring(new ByteSlice("alias "));
            if (tp.specType != null)
                typeToBuffer(tp.specType, tp.ident, this.buf, this.hgs);
            else
                (this.buf).writestring(tp.ident.asString());
            if (tp.specAlias != null)
            {
                (this.buf).writestring(new ByteSlice(" : "));
                objectToBuffer(tp.specAlias, this.buf, this.hgs);
            }
            if (tp.defaultAlias != null)
            {
                (this.buf).writestring(new ByteSlice(" = "));
                objectToBuffer(tp.defaultAlias, this.buf, this.hgs);
            }
        }

        public  void visit(TemplateValueParameter tp) {
            typeToBuffer(tp.valType, tp.ident, this.buf, this.hgs);
            if (tp.specValue != null)
            {
                (this.buf).writestring(new ByteSlice(" : "));
                expressionToBuffer(tp.specValue, this.buf, this.hgs);
            }
            if (tp.defaultValue != null)
            {
                (this.buf).writestring(new ByteSlice(" = "));
                expressionToBuffer(tp.defaultValue, this.buf, this.hgs);
            }
        }

        public  void visit(TemplateTupleParameter tp) {
            (this.buf).writestring(tp.ident.asString());
            (this.buf).writestring(new ByteSlice("..."));
        }


        public TemplateParameterPrettyPrintVisitor() {}

        public TemplateParameterPrettyPrintVisitor copy() {
            TemplateParameterPrettyPrintVisitor that = new TemplateParameterPrettyPrintVisitor();
            that.buf = this.buf;
            that.hgs = this.hgs;
            return that;
        }
    }
    public static void conditionToBuffer(Condition c, OutBuffer buf, HdrGenState hgs) {
        ConditionPrettyPrintVisitor v = new ConditionPrettyPrintVisitor(buf, hgs);
        c.accept(v);
    }

    public static class ConditionPrettyPrintVisitor extends Visitor
    {
        public OutBuffer buf;
        public HdrGenState hgs;
        public  ConditionPrettyPrintVisitor(OutBuffer buf, HdrGenState hgs) {
            this.buf = buf;
            this.hgs = hgs;
        }

        public  void visit(DebugCondition c) {
            (this.buf).writestring(new ByteSlice("debug ("));
            if (c.ident != null)
                (this.buf).writestring(c.ident.asString());
            else
                (this.buf).print((long)c.level);
            (this.buf).writeByte(41);
        }

        public  void visit(VersionCondition c) {
            (this.buf).writestring(new ByteSlice("version ("));
            if (c.ident != null)
                (this.buf).writestring(c.ident.asString());
            else
                (this.buf).print((long)c.level);
            (this.buf).writeByte(41);
        }

        public  void visit(StaticIfCondition c) {
            (this.buf).writestring(new ByteSlice("static if ("));
            expressionToBuffer(c.exp, this.buf, this.hgs);
            (this.buf).writeByte(41);
        }


        public ConditionPrettyPrintVisitor() {}

        public ConditionPrettyPrintVisitor copy() {
            ConditionPrettyPrintVisitor that = new ConditionPrettyPrintVisitor();
            that.buf = this.buf;
            that.hgs = this.hgs;
            return that;
        }
    }
    public static void toCBuffer(Statement s, OutBuffer buf, HdrGenState hgs) {
        StatementPrettyPrintVisitor v = new StatementPrettyPrintVisitor(buf, hgs);
        s.accept(v);
    }

    public static void toCBuffer(Type t, OutBuffer buf, Identifier ident, HdrGenState hgs) {
        typeToBuffer(t, ident, buf, hgs);
    }

    public static void toCBuffer(Dsymbol s, OutBuffer buf, HdrGenState hgs) {
        DsymbolPrettyPrintVisitor v = new DsymbolPrettyPrintVisitor(buf, hgs);
        s.accept(v);
    }

    public static void toCBufferInstance(TemplateInstance ti, OutBuffer buf, boolean qualifyTypes) {
        HdrGenState hgs = new HdrGenState();
        expr(hgs.fullQual = qualifyTypes);
        DsymbolPrettyPrintVisitor v = new DsymbolPrettyPrintVisitor(buf, hgs);
        v.visit(ti);
    }

    public static void toCBuffer(Initializer iz, OutBuffer buf, HdrGenState hgs) {
        initializerToBuffer(iz, buf, hgs);
    }

    public static boolean stcToBuffer(OutBuffer buf, long stc) {
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
                (buf).writeByte(32);
            expr(result = true);
            (buf).writestring(s);
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
        return s.get(0);
    }

    public static void trustToBuffer(OutBuffer buf, int trust) {
        (buf).writestring(trustToString(trust));
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

    public static void linkageToBuffer(OutBuffer buf, int linkage) {
        ByteSlice s = linkageToString(linkage).copy();
        if (s.getLength() != 0)
        {
            (buf).writestring(new ByteSlice("extern ("));
            (buf).writestring(s);
            (buf).writeByte(41);
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

    public static void protectionToBuffer(OutBuffer buf, Prot prot) {
        (buf).writestring(protectionToString(prot.kind));
        if ((prot.kind == Prot.Kind.package_) && (prot.pkg != null))
        {
            (buf).writeByte(40);
            (buf).writestring(prot.pkg.toPrettyChars(true));
            (buf).writeByte(41);
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

    public static void functionToBufferFull(TypeFunction tf, OutBuffer buf, Identifier ident, HdrGenState hgs, TemplateDeclaration td) {
        visitFuncIdentWithPrefix(tf, ident, td, buf, hgs);
    }

    public static void functionToBufferWithIdent(TypeFunction tf, OutBuffer buf, BytePtr ident) {
        HdrGenState hgs = new HdrGenState();
        visitFuncIdentWithPostfix(tf, toDString(ident), buf, hgs);
    }

    public static void toCBuffer(Expression e, OutBuffer buf, HdrGenState hgs) {
        ExpressionPrettyPrintVisitor v = new ExpressionPrettyPrintVisitor(buf, hgs);
        e.accept(v);
    }

    public static void argExpTypesToCBuffer(OutBuffer buf, DArray<Expression> arguments) {
        if ((arguments == null) || ((arguments).length == 0))
            return ;
        HdrGenState hgs = new HdrGenState();
        {
            Slice<Expression> __r1474 = (arguments).opSlice().copy();
            int __key1473 = 0;
            for (; (__key1473 < __r1474.getLength());__key1473 += 1) {
                Expression arg = __r1474.get(__key1473);
                int i = __key1473;
                if (i != 0)
                    (buf).writestring(new ByteSlice(", "));
                typeToBuffer(arg.type, null, buf, hgs);
            }
        }
    }

    public static void toCBuffer(TemplateParameter tp, OutBuffer buf, HdrGenState hgs) {
        TemplateParameterPrettyPrintVisitor v = new TemplateParameterPrettyPrintVisitor(buf, hgs);
        tp.accept(v);
    }

    public static void arrayObjectsToBuffer(OutBuffer buf, DArray<RootObject> objects) {
        if ((objects == null) || ((objects).length == 0))
            return ;
        HdrGenState hgs = new HdrGenState();
        {
            Slice<RootObject> __r1476 = (objects).opSlice().copy();
            int __key1475 = 0;
            for (; (__key1475 < __r1476.getLength());__key1475 += 1) {
                RootObject o = __r1476.get(__key1475);
                int i = __key1475;
                if (i != 0)
                    (buf).writestring(new ByteSlice(", "));
                objectToBuffer(o, buf, hgs);
            }
        }
    }

    public static BytePtr parametersTypeToChars(ParameterList pl) {
        OutBuffer buf = new OutBuffer();
        try {
            HdrGenState hgs = new HdrGenState();
            parametersToBuffer(pl, buf, hgs);
            return buf.extractChars();
        }
        finally {
        }
    }

    public static BytePtr parameterToChars(Parameter parameter, TypeFunction tf, boolean fullQual) {
        OutBuffer buf = new OutBuffer();
        try {
            HdrGenState hgs = new HdrGenState();
            expr(hgs.fullQual = fullQual);
            parameterToBuffer(parameter, buf, hgs);
            if ((tf.parameterList.varargs == VarArg.typesafe) && (pequals(parameter, tf.parameterList.get((tf.parameterList.parameters).length - 1))))
            {
                buf.writestring(new ByteSlice("..."));
            }
            return buf.extractChars();
        }
        finally {
        }
    }

    public static void parametersToBuffer(ParameterList pl, OutBuffer buf, HdrGenState hgs) {
        (buf).writeByte(40);
        {
            int __key1477 = 0;
            int __limit1478 = pl.length();
            for (; (__key1477 < __limit1478);__key1477 += 1) {
                int i = __key1477;
                if (i != 0)
                    (buf).writestring(new ByteSlice(", "));
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
                            /*goto case*/{ __dispatch7 = VarArg.typesafe; continue dispatched_7; }
                        (buf).writestring(new ByteSlice(", ..."));
                        break;
                    case VarArg.typesafe:
                        __dispatch7 = 0;
                        (buf).writestring(new ByteSlice("..."));
                        break;
                    default:
                    throw SwitchError.INSTANCE;
                }
            } while(__dispatch7 != 0);
        }
        (buf).writeByte(41);
    }

    public static void parameterToBuffer(Parameter p, OutBuffer buf, HdrGenState hgs) {
        if (p.userAttribDecl != null)
        {
            (buf).writeByte(64);
            boolean isAnonymous = ((p.userAttribDecl.atts).length > 0) && (((p.userAttribDecl.atts).get(0).op & 0xFF) != 18);
            if (isAnonymous)
                (buf).writeByte(40);
            argsToBuffer(p.userAttribDecl.atts, buf, hgs, null);
            if (isAnonymous)
                (buf).writeByte(41);
            (buf).writeByte(32);
        }
        if ((p.storageClass & 256L) != 0)
            (buf).writestring(new ByteSlice("auto "));
        if ((p.storageClass & 17592186044416L) != 0)
            (buf).writestring(new ByteSlice("return "));
        if ((p.storageClass & 4096L) != 0)
            (buf).writestring(new ByteSlice("out "));
        else if ((p.storageClass & 2097152L) != 0)
            (buf).writestring(new ByteSlice("ref "));
        else if ((p.storageClass & 2048L) != 0)
            (buf).writestring(new ByteSlice("in "));
        else if ((p.storageClass & 8192L) != 0)
            (buf).writestring(new ByteSlice("lazy "));
        else if ((p.storageClass & 268435456L) != 0)
            (buf).writestring(new ByteSlice("alias "));
        long stc = p.storageClass;
        if ((p.type != null) && (((p.type.mod & 0xFF) & MODFlags.shared_) != 0))
            stc &= -536870913L;
        if (stcToBuffer(buf, stc & 562952639348740L))
            (buf).writeByte(32);
        if ((p.storageClass & 268435456L) != 0)
        {
            if (p.ident != null)
                (buf).writestring(p.ident.asString());
        }
        else if (((p.type.ty & 0xFF) == ENUMTY.Tident) && (((TypeIdentifier)p.type).ident.asString().getLength() > 3) && (strncmp(((TypeIdentifier)p.type).ident.toChars(), new BytePtr("__T"), 3) == 0))
        {
            (buf).writestring(p.ident.asString());
        }
        else
        {
            typeToBuffer(p.type, p.ident, buf, hgs);
        }
        if (p.defaultArg != null)
        {
            (buf).writestring(new ByteSlice(" = "));
            expToBuffer(p.defaultArg, PREC.assign, buf, hgs);
        }
    }

    public static void argsToBuffer(DArray<Expression> expressions, OutBuffer buf, HdrGenState hgs, Expression basis) {
        if ((expressions == null) || ((expressions).length == 0))
            return ;
        {
            Slice<Expression> __r1480 = (expressions).opSlice().copy();
            int __key1479 = 0;
            for (; (__key1479 < __r1480.getLength());__key1479 += 1) {
                Expression el = __r1480.get(__key1479);
                int i = __key1479;
                if (i != 0)
                    (buf).writestring(new ByteSlice(", "));
                if (el == null)
                    el = basis;
                if (el != null)
                    expToBuffer(el, PREC.assign, buf, hgs);
            }
        }
    }

    public static void sizeToBuffer(Expression e, OutBuffer buf, HdrGenState hgs) {
        if ((pequals(e.type, Type.tsize_t)))
        {
            Expression ex = ((e.op & 0xFF) == 12) ? ((CastExp)e).e1 : e;
            ex = ex.optimize(0, false);
            long uval = ((ex.op & 0xFF) == 135) ? ex.toInteger() : -1L;
            if (((long)uval >= 0L))
            {
                long sizemax = null;
                if ((target.ptrsize == 8))
                    sizemax = -1L;
                else if ((target.ptrsize == 4))
                    sizemax = 4294967295L;
                else if ((target.ptrsize == 2))
                    sizemax = 65535L;
                else
                    throw new AssertionError("Unreachable code!");
                if ((uval <= sizemax) && (uval <= 9223372036854775807L))
                {
                    (buf).print(uval);
                    return ;
                }
            }
        }
        expToBuffer(e, PREC.assign, buf, hgs);
    }

    public static void expressionToBuffer(Expression e, OutBuffer buf, HdrGenState hgs) {
        ExpressionPrettyPrintVisitor v = new ExpressionPrettyPrintVisitor(buf, hgs);
        e.accept(v);
    }

    public static void expToBuffer(Expression e, int pr, OutBuffer buf, HdrGenState hgs) {
        assert((precedence.get((e.op & 0xFF)) != PREC.zero));
        assert((pr != PREC.zero));
        if ((precedence.get((e.op & 0xFF)) < pr) || (pr == PREC.rel) && (precedence.get((e.op & 0xFF)) == pr) || (pr >= PREC.or) && (pr <= PREC.and) && (precedence.get((e.op & 0xFF)) == PREC.rel))
        {
            (buf).writeByte(40);
            expressionToBuffer(e, buf, hgs);
            (buf).writeByte(41);
        }
        else
        {
            expressionToBuffer(e, buf, hgs);
        }
    }

    public static void typeToBuffer(Type t, Identifier ident, OutBuffer buf, HdrGenState hgs) {
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
            (buf).writeByte(32);
            (buf).writestring(ident.asString());
        }
    }

    public static void visitWithMask(Type t, byte modMask, OutBuffer buf, HdrGenState hgs) {
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
                (buf).writeByte(40);
            }
            if (((m & 0xFF) & MODFlags.wild) != 0)
            {
                MODtoBuffer(buf, (byte)8);
                (buf).writeByte(40);
            }
            if (((m & 0xFF) & 5) != 0)
            {
                MODtoBuffer(buf, (byte)((m & 0xFF) & 5));
                (buf).writeByte(40);
            }
            typeToBufferx(t, buf, hgs);
            if (((m & 0xFF) & 5) != 0)
                (buf).writeByte(41);
            if (((m & 0xFF) & MODFlags.wild) != 0)
                (buf).writeByte(41);
            if (((m & 0xFF) & MODFlags.shared_) != 0)
                (buf).writeByte(41);
        }
    }

    public static void dumpTemplateInstance(TemplateInstance ti, OutBuffer buf, HdrGenState hgs) {
        (buf).writeByte(123);
        (buf).writenl();
        (buf).level++;
        if (ti.aliasdecl != null)
        {
            dsymbolToBuffer(ti.aliasdecl, buf, hgs);
            (buf).writenl();
        }
        else if (ti.members != null)
        {
            {
                Slice<Dsymbol> __r1481 = (ti.members).opSlice().copy();
                int __key1482 = 0;
                for (; (__key1482 < __r1481.getLength());__key1482 += 1) {
                    Dsymbol m = __r1481.get(__key1482);
                    dsymbolToBuffer(m, buf, hgs);
                }
            }
        }
        (buf).level--;
        (buf).writeByte(125);
        (buf).writenl();
    }

    public static void tiargsToBuffer(TemplateInstance ti, OutBuffer buf, HdrGenState hgs) {
        (buf).writeByte(33);
        if (ti.nest != 0)
        {
            (buf).writestring(new ByteSlice("(...)"));
            return ;
        }
        if (ti.tiargs == null)
        {
            (buf).writestring(new ByteSlice("()"));
            return ;
        }
        if (((ti.tiargs).length == 1))
        {
            RootObject oarg = (ti.tiargs).get(0);
            {
                Type t = isType(oarg);
                if ((t) != null)
                {
                    if (t.equals(Type.tstring) || t.equals(Type.twstring) || t.equals(Type.tdstring) || ((t.mod & 0xFF) == 0) && (t.isTypeBasic() != null) || ((t.ty & 0xFF) == ENUMTY.Tident) && (((TypeIdentifier)t).idents.length == 0))
                    {
                        (buf).writestring(t.toChars());
                        return ;
                    }
                }
                else {
                    Expression e = isExpression(oarg);
                    if ((e) != null)
                    {
                        if (((e.op & 0xFF) == 135) || ((e.op & 0xFF) == 140) || ((e.op & 0xFF) == 13) || ((e.op & 0xFF) == 121) || ((e.op & 0xFF) == 123))
                        {
                            (buf).writestring(e.toChars());
                            return ;
                        }
                    }
                }
            }
        }
        (buf).writeByte(40);
        ti.nest++;
        {
            Slice<RootObject> __r1484 = (ti.tiargs).opSlice().copy();
            int __key1483 = 0;
            for (; (__key1483 < __r1484.getLength());__key1483 += 1) {
                RootObject arg = __r1484.get(__key1483);
                int i = __key1483;
                if (i != 0)
                    (buf).writestring(new ByteSlice(", "));
                objectToBuffer(arg, buf, hgs);
            }
        }
        ti.nest--;
        (buf).writeByte(41);
    }

    public static void objectToBuffer(RootObject oarg, OutBuffer buf, HdrGenState hgs) {
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
                        e = e.optimize(0, false);
                    expToBuffer(e, PREC.assign, buf, hgs);
                }
                else {
                    Dsymbol s = isDsymbol(oarg);
                    if ((s) != null)
                    {
                        BytePtr p = pcopy(s.ident != null ? s.ident.toChars() : s.toChars());
                        (buf).writestring(p);
                    }
                    else {
                        Tuple v = isTuple(oarg);
                        if ((v) != null)
                        {
                            DArray<RootObject> args = v.objects;
                            {
                                Slice<RootObject> __r1486 = (args).opSlice().copy();
                                int __key1485 = 0;
                                for (; (__key1485 < __r1486.getLength());__key1485 += 1) {
                                    RootObject arg = __r1486.get(__key1485);
                                    int i = __key1485;
                                    if (i != 0)
                                        (buf).writestring(new ByteSlice(", "));
                                    objectToBuffer(arg, buf, hgs);
                                }
                            }
                        }
                        else if (oarg == null)
                        {
                            (buf).writestring(new ByteSlice("NULL"));
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

    public static void visitFuncIdentWithPostfix(TypeFunction t, ByteSlice ident, OutBuffer buf, HdrGenState hgs) {
        Ref<OutBuffer> buf_ref = ref(buf);
        if (t.inuse != 0)
        {
            t.inuse = 2;
            return ;
        }
        t.inuse++;
        if ((t.linkage > LINK.d) && (((hgs).ddoc ? 1 : 0) != 1) && !(hgs).hdrgen)
        {
            linkageToBuffer(buf_ref.value, t.linkage);
            (buf_ref.value).writeByte(32);
        }
        if (t.next != null)
        {
            typeToBuffer(t.next, null, buf_ref.value, hgs);
            if (ident.getLength() != 0)
                (buf_ref.value).writeByte(32);
        }
        else if ((hgs).ddoc)
            (buf_ref.value).writestring(new ByteSlice("auto "));
        if (ident.getLength() != 0)
            (buf_ref.value).writestring(ident);
        parametersToBuffer(t.parameterList, buf_ref.value, hgs);
        if (t.mod != 0)
        {
            (buf_ref.value).writeByte(32);
            MODtoBuffer(buf_ref.value, t.mod);
        }
        Function1<ByteSlice,Void> dg = new Function1<ByteSlice,Void>(){
            public Void invoke(ByteSlice str){
                (buf_ref.value).writeByte(32);
                (buf_ref.value).writestring(str);
            }
        };
        attributesApply(t, dg, TRUSTformat.TRUSTformatDefault);
        t.inuse--;
    }

    public static void visitFuncIdentWithPrefix(TypeFunction t, Identifier ident, TemplateDeclaration td, OutBuffer buf, HdrGenState hgs) {
        Ref<Identifier> ident_ref = ref(ident);
        Ref<OutBuffer> buf_ref = ref(buf);
        if (t.inuse != 0)
        {
            t.inuse = 2;
            return ;
        }
        t.inuse++;
        if (t.mod != 0)
        {
            MODtoBuffer(buf_ref.value, t.mod);
            (buf_ref.value).writeByte(32);
        }
        Function1<ByteSlice,Void> ignoreReturn = new Function1<ByteSlice,Void>(){
            public Void invoke(ByteSlice str){
                if (!__equals(str, new ByteSlice("return")))
                {
                    if ((pequals(ident_ref.value, Id.ctor)) && __equals(str, new ByteSlice("ref")))
                        return null;
                    (buf_ref.value).writestring(str);
                    (buf_ref.value).writeByte(32);
                }
            }
        };
        attributesApply(t, ignoreReturn, TRUSTformat.TRUSTformatDefault);
        if ((t.linkage > LINK.d) && (((hgs).ddoc ? 1 : 0) != 1) && !(hgs).hdrgen)
        {
            linkageToBuffer(buf_ref.value, t.linkage);
            (buf_ref.value).writeByte(32);
        }
        if ((ident_ref.value != null) && (ident_ref.value.toHChars2() != ident_ref.value.toChars()))
        {
        }
        else if (t.next != null)
        {
            typeToBuffer(t.next, null, buf_ref.value, hgs);
            if (ident_ref.value != null)
                (buf_ref.value).writeByte(32);
        }
        else if ((hgs).ddoc)
            (buf_ref.value).writestring(new ByteSlice("auto "));
        if (ident_ref.value != null)
            (buf_ref.value).writestring(ident_ref.value.toHChars2());
        if (td != null)
        {
            (buf_ref.value).writeByte(40);
            {
                Slice<TemplateParameter> __r1488 = (td.origParameters).opSlice().copy();
                int __key1487 = 0;
                for (; (__key1487 < __r1488.getLength());__key1487 += 1) {
                    TemplateParameter p = __r1488.get(__key1487);
                    int i = __key1487;
                    if (i != 0)
                        (buf_ref.value).writestring(new ByteSlice(", "));
                    templateParameterToBuffer(p, buf_ref.value, hgs);
                }
            }
            (buf_ref.value).writeByte(41);
        }
        parametersToBuffer(t.parameterList, buf_ref.value, hgs);
        if (t.isreturn)
        {
            (buf_ref.value).writestring(new ByteSlice(" return"));
        }
        t.inuse--;
    }

    public static void initializerToBuffer(Initializer inx, OutBuffer buf, HdrGenState hgs) {
        Ref<OutBuffer> buf_ref = ref(buf);
        Ref<HdrGenState> hgs_ref = ref(hgs);
        Function1<ErrorInitializer,Void> visitError = new Function1<ErrorInitializer,Void>(){
            public Void invoke(ErrorInitializer iz){
                (buf_ref.value).writestring(new ByteSlice("__error__"));
            }
        };
        Function1<VoidInitializer,Void> visitVoid = new Function1<VoidInitializer,Void>(){
            public Void invoke(VoidInitializer iz){
                (buf_ref.value).writestring(new ByteSlice("void"));
            }
        };
        Function1<StructInitializer,Void> visitStruct = new Function1<StructInitializer,Void>(){
            public Void invoke(StructInitializer si){
                (buf_ref.value).writeByte(123);
                {
                    Slice<Identifier> __r1490 = si.field.opSlice().copy();
                    int __key1489 = 0;
                    for (; (__key1489 < __r1490.getLength());__key1489 += 1) {
                        Identifier id = __r1490.get(__key1489);
                        int i = __key1489;
                        if (i != 0)
                            (buf_ref.value).writestring(new ByteSlice(", "));
                        if (id != null)
                        {
                            (buf_ref.value).writestring(id.asString());
                            (buf_ref.value).writeByte(58);
                        }
                        {
                            Initializer iz = si.value.get(i);
                            if ((iz) != null)
                                initializerToBuffer(iz, buf_ref.value, hgs_ref.value);
                        }
                    }
                }
                (buf_ref.value).writeByte(125);
            }
        };
        Function1<ArrayInitializer,Void> visitArray = new Function1<ArrayInitializer,Void>(){
            public Void invoke(ArrayInitializer ai){
                (buf_ref.value).writeByte(91);
                {
                    Slice<Expression> __r1492 = ai.index.opSlice().copy();
                    int __key1491 = 0;
                    for (; (__key1491 < __r1492.getLength());__key1491 += 1) {
                        Expression ex = __r1492.get(__key1491);
                        int i = __key1491;
                        if (i != 0)
                            (buf_ref.value).writestring(new ByteSlice(", "));
                        if (ex != null)
                        {
                            expressionToBuffer(ex, buf_ref.value, hgs_ref.value);
                            (buf_ref.value).writeByte(58);
                        }
                        {
                            Initializer iz = ai.value.get(i);
                            if ((iz) != null)
                                initializerToBuffer(iz, buf_ref.value, hgs_ref.value);
                        }
                    }
                }
                (buf_ref.value).writeByte(93);
            }
        };
        Function1<ExpInitializer,Void> visitExp = new Function1<ExpInitializer,Void>(){
            public Void invoke(ExpInitializer ei){
                expressionToBuffer(ei.exp, buf_ref.value, hgs_ref.value);
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

    public static void typeToBufferx(Type t, OutBuffer buf, HdrGenState hgs) {
        Ref<OutBuffer> buf_ref = ref(buf);
        Ref<HdrGenState> hgs_ref = ref(hgs);
        Function1<Type,Void> visitType = new Function1<Type,Void>(){
            public Void invoke(Type t){
                printf(new BytePtr("t = %p, ty = %d\n"), t, (t.ty & 0xFF));
                throw new AssertionError("Unreachable code!");
            }
        };
        Function1<TypeError,Void> visitError = new Function1<TypeError,Void>(){
            public Void invoke(TypeError t){
                (buf_ref.value).writestring(new ByteSlice("_error_"));
            }
        };
        Function1<TypeBasic,Void> visitBasic = new Function1<TypeBasic,Void>(){
            public Void invoke(TypeBasic t){
                (buf_ref.value).writestring(t.dstring);
            }
        };
        Function1<TypeTraits,Void> visitTraits = new Function1<TypeTraits,Void>(){
            public Void invoke(TypeTraits t){
                expressionToBuffer(t.exp, buf_ref.value, hgs_ref.value);
            }
        };
        Function1<TypeVector,Void> visitVector = new Function1<TypeVector,Void>(){
            public Void invoke(TypeVector t){
                (buf_ref.value).writestring(new ByteSlice("__vector("));
                visitWithMask(t.basetype, t.mod, buf_ref.value, hgs_ref.value);
                (buf_ref.value).writestring(new ByteSlice(")"));
            }
        };
        Function1<TypeSArray,Void> visitSArray = new Function1<TypeSArray,Void>(){
            public Void invoke(TypeSArray t){
                visitWithMask(t.next, t.mod, buf_ref.value, hgs_ref.value);
                (buf_ref.value).writeByte(91);
                sizeToBuffer(t.dim, buf_ref.value, hgs_ref.value);
                (buf_ref.value).writeByte(93);
            }
        };
        Function1<TypeDArray,Void> visitDArray = new Function1<TypeDArray,Void>(){
            public Void invoke(TypeDArray t){
                Type ut = t.castMod((byte)0);
                if ((hgs_ref.value).declstring)
                    /*goto L1*//*unrolled goto*/
                    (buf_ref.value).writestring(new ByteSlice("dstring"));
                if (ut.equals(Type.tstring))
                    (buf_ref.value).writestring(new ByteSlice("string"));
                else if (ut.equals(Type.twstring))
                    (buf_ref.value).writestring(new ByteSlice("wstring"));
                else if (ut.equals(Type.tdstring))
                    (buf_ref.value).writestring(new ByteSlice("dstring"));
                else
                {
                /*L1:*/
                    visitWithMask(t.next, t.mod, buf_ref.value, hgs_ref.value);
                    (buf_ref.value).writestring(new ByteSlice("[]"));
                }
            }
        };
        Function1<TypeAArray,Void> visitAArray = new Function1<TypeAArray,Void>(){
            public Void invoke(TypeAArray t){
                visitWithMask(t.next, t.mod, buf_ref.value, hgs_ref.value);
                (buf_ref.value).writeByte(91);
                visitWithMask(t.index, (byte)0, buf_ref.value, hgs_ref.value);
                (buf_ref.value).writeByte(93);
            }
        };
        Function1<TypePointer,Void> visitPointer = new Function1<TypePointer,Void>(){
            public Void invoke(TypePointer t){
                if (((t.next.ty & 0xFF) == ENUMTY.Tfunction))
                    visitFuncIdentWithPostfix((TypeFunction)t.next, new ByteSlice("function"), buf_ref.value, hgs_ref.value);
                else
                {
                    visitWithMask(t.next, t.mod, buf_ref.value, hgs_ref.value);
                    (buf_ref.value).writeByte(42);
                }
            }
        };
        Function1<TypeReference,Void> visitReference = new Function1<TypeReference,Void>(){
            public Void invoke(TypeReference t){
                visitWithMask(t.next, t.mod, buf_ref.value, hgs_ref.value);
                (buf_ref.value).writeByte(38);
            }
        };
        Function1<TypeFunction,Void> visitFunction = new Function1<TypeFunction,Void>(){
            public Void invoke(TypeFunction t){
                visitFuncIdentWithPostfix(t, new ByteSlice(), buf_ref.value, hgs_ref.value);
            }
        };
        Function1<TypeDelegate,Void> visitDelegate = new Function1<TypeDelegate,Void>(){
            public Void invoke(TypeDelegate t){
                visitFuncIdentWithPostfix((TypeFunction)t.next, new ByteSlice("delegate"), buf_ref.value, hgs_ref.value);
            }
        };
        Function1<TypeQualified,Void> visitTypeQualifiedHelper = new Function1<TypeQualified,Void>(){
            public Void invoke(TypeQualified t){
                {
                    Slice<RootObject> __r1493 = t.idents.opSlice().copy();
                    int __key1494 = 0;
                    for (; (__key1494 < __r1493.getLength());__key1494 += 1) {
                        RootObject id = __r1493.get(__key1494);
                        if ((id.dyncast() == DYNCAST.dsymbol))
                        {
                            (buf_ref.value).writeByte(46);
                            TemplateInstance ti = (TemplateInstance)id;
                            dsymbolToBuffer(ti, buf_ref.value, hgs_ref.value);
                        }
                        else if ((id.dyncast() == DYNCAST.expression))
                        {
                            (buf_ref.value).writeByte(91);
                            expressionToBuffer((Expression)id, buf_ref.value, hgs_ref.value);
                            (buf_ref.value).writeByte(93);
                        }
                        else if ((id.dyncast() == DYNCAST.type))
                        {
                            (buf_ref.value).writeByte(91);
                            typeToBufferx((Type)id, buf_ref.value, hgs_ref.value);
                            (buf_ref.value).writeByte(93);
                        }
                        else
                        {
                            (buf_ref.value).writeByte(46);
                            (buf_ref.value).writestring(id.asString());
                        }
                    }
                }
            }
        };
        Function1<TypeIdentifier,Void> visitIdentifier = new Function1<TypeIdentifier,Void>(){
            public Void invoke(TypeIdentifier t){
                (buf_ref.value).writestring(t.ident.asString());
                visitTypeQualifiedHelper.invoke(t);
            }
        };
        Function1<TypeInstance,Void> visitInstance = new Function1<TypeInstance,Void>(){
            public Void invoke(TypeInstance t){
                dsymbolToBuffer(t.tempinst, buf_ref.value, hgs_ref.value);
                visitTypeQualifiedHelper.invoke(t);
            }
        };
        Function1<TypeTypeof,Void> visitTypeof = new Function1<TypeTypeof,Void>(){
            public Void invoke(TypeTypeof t){
                (buf_ref.value).writestring(new ByteSlice("typeof("));
                expressionToBuffer(t.exp, buf_ref.value, hgs_ref.value);
                (buf_ref.value).writeByte(41);
                visitTypeQualifiedHelper.invoke(t);
            }
        };
        Function1<TypeReturn,Void> visitReturn = new Function1<TypeReturn,Void>(){
            public Void invoke(TypeReturn t){
                (buf_ref.value).writestring(new ByteSlice("typeof(return)"));
                visitTypeQualifiedHelper.invoke(t);
            }
        };
        Function1<TypeEnum,Void> visitEnum = new Function1<TypeEnum,Void>(){
            public Void invoke(TypeEnum t){
                (buf_ref.value).writestring((hgs_ref.value).fullQual ? t.sym.toPrettyChars(false) : t.sym.toChars());
            }
        };
        Function1<TypeStruct,Void> visitStruct = new Function1<TypeStruct,Void>(){
            public Void invoke(TypeStruct t){
                TemplateInstance ti = t.sym.parent != null ? t.sym.parent.isTemplateInstance() : null;
                if ((ti != null) && (pequals(ti.aliasdecl, t.sym)))
                    (buf_ref.value).writestring((hgs_ref.value).fullQual ? ti.toPrettyChars(false) : ti.toChars());
                else
                    (buf_ref.value).writestring((hgs_ref.value).fullQual ? t.sym.toPrettyChars(false) : t.sym.toChars());
            }
        };
        Function1<TypeClass,Void> visitClass = new Function1<TypeClass,Void>(){
            public Void invoke(TypeClass t){
                TemplateInstance ti = t.sym.parent.isTemplateInstance();
                if ((ti != null) && (pequals(ti.aliasdecl, t.sym)))
                    (buf_ref.value).writestring((hgs_ref.value).fullQual ? ti.toPrettyChars(false) : ti.toChars());
                else
                    (buf_ref.value).writestring((hgs_ref.value).fullQual ? t.sym.toPrettyChars(false) : t.sym.toChars());
            }
        };
        Function1<TypeTuple,Void> visitTuple = new Function1<TypeTuple,Void>(){
            public Void invoke(TypeTuple t){
                parametersToBuffer(new ParameterList(t.arguments, VarArg.none), buf_ref.value, hgs_ref.value);
            }
        };
        Function1<TypeSlice,Void> visitSlice = new Function1<TypeSlice,Void>(){
            public Void invoke(TypeSlice t){
                visitWithMask(t.next, t.mod, buf_ref.value, hgs_ref.value);
                (buf_ref.value).writeByte(91);
                sizeToBuffer(t.lwr, buf_ref.value, hgs_ref.value);
                (buf_ref.value).writestring(new ByteSlice(" .. "));
                sizeToBuffer(t.upr, buf_ref.value, hgs_ref.value);
                (buf_ref.value).writeByte(93);
            }
        };
        Function1<TypeNull,Void> visitNull = new Function1<TypeNull,Void>(){
            public Void invoke(TypeNull t){
                (buf_ref.value).writestring(new ByteSlice("typeof(null)"));
            }
        };
        switch ((t.ty & 0xFF))
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
