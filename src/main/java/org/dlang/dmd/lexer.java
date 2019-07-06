package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.entity.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.utf.*;

public class lexer {
    static boolean scaninitdone = false;
    static ByteSlice scandate = new ByteSlice(new byte[12]);
    static ByteSlice scantime = new ByteSlice(new byte[9]);
    static ByteSlice scantimestamp = new ByteSlice(new byte[25]);

    private static class FLAGS 
    {
        public static final int none = 0;
        public static final int decimal = 1;
        public static final int unsigned = 2;
        public static final int long_ = 4;
    }


    static int LS = 8232;
    static int PS = 8233;
    static ByteSlice cmtable = slice(new byte[]{(byte)0, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)0, (byte)32, (byte)32, (byte)0, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)0, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)0, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)56, (byte)32, (byte)63, (byte)63, (byte)63, (byte)63, (byte)63, (byte)63, (byte)63, (byte)63, (byte)62, (byte)62, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)38, (byte)46, (byte)38, (byte)38, (byte)62, (byte)62, (byte)36, (byte)36, (byte)36, (byte)36, (byte)36, (byte)60, (byte)36, (byte)36, (byte)36, (byte)60, (byte)36, (byte)36, (byte)36, (byte)36, (byte)60, (byte)36, (byte)36, (byte)44, (byte)36, (byte)36, (byte)32, (byte)0, (byte)32, (byte)32, (byte)60, (byte)32, (byte)38, (byte)46, (byte)38, (byte)38, (byte)62, (byte)62, (byte)36, (byte)36, (byte)60, (byte)36, (byte)36, (byte)60, (byte)36, (byte)36, (byte)36, (byte)60, (byte)36, (byte)36, (byte)36, (byte)36, (byte)60, (byte)36, (byte)36, (byte)44, (byte)36, (byte)36, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0});
    static int CMoctal = 1;
    static int CMhex = 2;
    static int CMidchar = 4;
    static int CMzerosecond = 8;
    static int CMdigitsecond = 16;
    static int CMsinglechar = 32;
    public static boolean isoctal(byte c) {
        return ((cmtable.get((c & 0xFF)) & 0xFF) & 1) != 0;
    }

    public static boolean ishex(byte c) {
        return ((cmtable.get((c & 0xFF)) & 0xFF) & 2) != 0;
    }

    public static boolean isidchar(byte c) {
        return ((cmtable.get((c & 0xFF)) & 0xFF) & 4) != 0;
    }

    public static boolean isZeroSecond(byte c) {
        return ((cmtable.get((c & 0xFF)) & 0xFF) & 8) != 0;
    }

    public static boolean isDigitSecond(byte c) {
        return ((cmtable.get((c & 0xFF)) & 0xFF) & 16) != 0;
    }

    public static boolean issinglechar(byte c) {
        return ((cmtable.get((c & 0xFF)) & 0xFF) & 32) != 0;
    }

    public static boolean c_isxdigit(int c) {
        return (((c >= 48 && c <= 57) || (c >= 97 && c <= 102)) || (c >= 65 && c <= 70));
    }

    public static boolean c_isalnum(int c) {
        return (((c >= 48 && c <= 57) || (c >= 97 && c <= 122)) || (c >= 65 && c <= 90));
    }

    public static class Lexer extends Object
    {
        public static OutBuffer stringbuffer = new OutBuffer();
        public Loc scanloc = new Loc();
        public Loc prevloc = new Loc();
        public BytePtr p;
        public Token token = new Token();
        public BytePtr base;
        public BytePtr end;
        public BytePtr line;
        public boolean doDocComment;
        public boolean anyToken;
        public boolean commentToken;
        public int inTokenStringConstant;
        public int lastDocLine;
        public DiagnosticReporter diagnosticReporter;
        public Token tokenFreelist;
        public  Lexer(BytePtr filename, BytePtr base, int begoffset, int endoffset, boolean doDocComment, boolean commentToken, DiagnosticReporter diagnosticReporter) {
            {
                {
                    assert(diagnosticReporter != null);
                }
            }
            this.diagnosticReporter = diagnosticReporter;
            this.scanloc = new Loc(filename, 1, 1);
            this.token = new Token().copy();
            this.base = pcopy(base);
            this.end = pcopy((base.plus(endoffset)));
            this.p = pcopy((base.plus(begoffset)));
            this.line = pcopy(this.p);
            this.doDocComment = doDocComment;
            this.commentToken = commentToken;
            this.inTokenStringConstant = 0;
            this.lastDocLine = 0;
            if (((this.p != null && (this.p.get(0) & 0xFF) == 35) && (this.p.get(1) & 0xFF) == 33))
            {
                this.p.plusAssign(2);
                for (; (1) != 0;){
                    byte c = this.p.postInc().get();
                    switch ((c & 0xFF))
                    {
                        case 0:
                        case 26:
                            this.p.postDec();
                        case 10:
                            break;
                        default:
                        continue;
                    }
                    break;
                }
                this.endOfLine();
            }
        }

        public  boolean errors() {
            return this.diagnosticReporter.errorCount() > 0;
        }

        public  Token allocateToken() {
            if (this.tokenFreelist != null)
            {
                Token t = this.tokenFreelist;
                this.tokenFreelist = (t).next;
                (t).next = null;
                return t;
            }
            return new Token();
        }

        public  void releaseToken(Token token) {
            (token).next = this.tokenFreelist;
            this.tokenFreelist = token;
        }

        public  byte nextToken() {
            this.prevloc = this.token.loc.copy();
            if (this.token.next != null)
            {
                Token t = this.token.next;
                (this.token).opAssign((t));
                this.releaseToken(t);
            }
            else
            {
                this.scan(this.token);
            }
            return this.token.value;
        }

        public  byte peekNext() {
            return (this.peek(this.token)).value;
        }

        public  byte peekNext2() {
            Token t = this.peek(this.token);
            return (this.peek(t)).value;
        }

        public  void scan(Token t) {
            int lastLine = this.scanloc.linnum;
            Loc startLoc = new Loc();
            (t).blockComment = null;
            (t).lineComment = null;
        L_outer1:
            for (; (1) != 0;){
                (t).ptr = pcopy(this.p);
                (t).loc = this.loc().copy();
                {
                    int __dispatch1 = 0;
                    dispatched_1:
                    do {
                        switch (__dispatch1 != 0 ? __dispatch1 : (this.p.get() & 0xFF))
                        {
                            case 0:
                            case 26:
                                (t).value = TOK.endOfFile;
                                return ;
                            case 32:
                            case 9:
                            case 11:
                            case 12:
                                this.p.postInc();
                                continue L_outer1;
                            case 13:
                                this.p.postInc();
                                if ((this.p.get() & 0xFF) != 10)
                                    this.endOfLine();
                                continue L_outer1;
                            case 10:
                                this.p.postInc();
                                this.endOfLine();
                                continue L_outer1;
                            case 48:
                                if (!(isZeroSecond(this.p.get(1))))
                                {
                                    this.p.plusAssign(1);
                                    (t).intvalue = 0L;
                                    (t).value = TOK.int32Literal;
                                    return ;
                                }
                                /*goto Lnumber*/{ __dispatch1 = -1; continue dispatched_1; }
                            case 49:
                            case 50:
                            case 51:
                            case 52:
                            case 53:
                            case 54:
                            case 55:
                            case 56:
                            case 57:
                                if (!(isDigitSecond(this.p.get(1))))
                                {
                                    (t).intvalue = (long)((this.p.get() & 0xFF) - 48);
                                    this.p.plusAssign(1);
                                    (t).value = TOK.int32Literal;
                                    return ;
                                }
                            /*Lnumber:*/
                            case -1:
                            __dispatch1 = 0;
                                (t).value = this.number(t);
                                return ;
                            case 39:
                                if ((issinglechar(this.p.get(1)) && (this.p.get(2) & 0xFF) == 39))
                                {
                                    (t).intvalue = (long)this.p.get(1);
                                    (t).value = TOK.charLiteral;
                                    this.p.plusAssign(3);
                                }
                                else
                                    (t).value = this.charConstant(t);
                                return ;
                            case 114:
                                if ((this.p.get(1) & 0xFF) != 34)
                                    /*goto case_ident*/{ __dispatch1 = -2; continue dispatched_1; }
                                this.p.postInc();
                                /*goto case*/{ __dispatch1 = 96; continue dispatched_1; }
                            case 96:
                                __dispatch1 = 0;
                                this.wysiwygStringConstant(t);
                                return ;
                            case 120:
                                if ((this.p.get(1) & 0xFF) != 34)
                                    /*goto case_ident*/{ __dispatch1 = -2; continue dispatched_1; }
                                this.p.postInc();
                                BytePtr start = pcopy(this.p);
                                OutBuffer hexString = new OutBuffer(null, 0, 0, 0, false, false);
                                (t).value = this.hexStringConstant(t);
                                (hexString).write(start, ((this.p.minus(start))));
                                this.error(new BytePtr("Built-in hex string literals are obsolete, use `std.conv.hexString!%s` instead."), (hexString).extractChars());
                                return ;
                            case 113:
                                if ((this.p.get(1) & 0xFF) == 34)
                                {
                                    this.p.postInc();
                                    this.delimitedStringConstant(t);
                                    return ;
                                }
                                else if ((this.p.get(1) & 0xFF) == 123)
                                {
                                    this.p.postInc();
                                    this.tokenStringConstant(t);
                                    return ;
                                }
                                else
                                    /*goto case_ident*/{ __dispatch1 = -2; continue dispatched_1; }
                            case 34:
                                this.escapeStringConstant(t);
                                return ;
                            case 97:
                            case 98:
                            case 99:
                            case 100:
                            case 101:
                            case 102:
                            case 103:
                            case 104:
                            case 105:
                            case 106:
                            case 107:
                            case 108:
                            case 109:
                            case 110:
                            case 111:
                            case 112:
                            case 115:
                            case 116:
                            case 117:
                            case 118:
                            case 119:
                            case 121:
                            case 122:
                            case 65:
                            case 66:
                            case 67:
                            case 68:
                            case 69:
                            case 70:
                            case 71:
                            case 72:
                            case 73:
                            case 74:
                            case 75:
                            case 76:
                            case 77:
                            case 78:
                            case 79:
                            case 80:
                            case 81:
                            case 82:
                            case 83:
                            case 84:
                            case 85:
                            case 86:
                            case 87:
                            case 88:
                            case 89:
                            case 90:
                            case 95:
                            /*case_ident:*/
                            case -2:
                            __dispatch1 = 0;
                                {
                                    for (; (1) != 0;){
                                        byte c = (this.p.plusAssign(1)).get();
                                        if (isidchar(c))
                                            continue;
                                        else if (((c & 0xFF) & 128) != 0)
                                        {
                                            BytePtr s = pcopy(this.p);
                                            int u = this.decodeUTF();
                                            if (isUniAlpha(u))
                                                continue;
                                            this.error(new BytePtr("char 0x%04x not allowed in identifier"), u);
                                            this.p = pcopy(s);
                                        }
                                        break;
                                    }
                                    Identifier id = Identifier.idPool((t).ptr, ((this.p.minus((t).ptr))));
                                    (t).ident = id;
                                    (t).value = (byte)id.getValue();
                                    this.anyToken = true;
                                    if (((t).ptr.get() & 0xFF) == 95)
                                    {
                                        if (!(lexer.scaninitdone))
                                        {
                                            lexer.scaninitdone = true;
                                            IntRef ct = ref(0);
                                            time(ptr(ct));
                                            BytePtr p = pcopy(ctime(ptr(ct)));
                                            assert(p != null);
                                            sprintf(ptr(lexer.scandate), new BytePtr("%.6s %.4s"), p.plus(4), p.plus(20));
                                            sprintf(ptr(lexer.scantime), new BytePtr("%.8s"), p.plus(11));
                                            sprintf(ptr(lexer.scantimestamp), new BytePtr("%.24s"), p);
                                        }
                                        if (pequals(id, Id.DATE))
                                        {
                                            (t).ustring = pcopy(ptr(lexer.scandate));
                                            /*goto Lstr*/
                                            (t).value = TOK.string_;
                                            (t).postfix = (byte)0;
                                            (t).len = strlen((t).ustring);
                                        }
                                        else if (pequals(id, Id.TIME))
                                        {
                                            (t).ustring = pcopy(ptr(lexer.scantime));
                                            /*goto Lstr*/
                                            (t).value = TOK.string_;
                                            (t).postfix = (byte)0;
                                            (t).len = strlen((t).ustring);
                                        }
                                        else if (pequals(id, Id.VENDOR))
                                        {
                                            (t).ustring = pcopy((toBytePtr(xarraydup(global.vendor))));
                                            /*goto Lstr*/
                                            (t).value = TOK.string_;
                                            (t).postfix = (byte)0;
                                            (t).len = strlen((t).ustring);
                                        }
                                        else if (pequals(id, Id.TIMESTAMP))
                                        {
                                            (t).ustring = pcopy(ptr(lexer.scantimestamp));
                                        /*Lstr:*/
                                            (t).value = TOK.string_;
                                            (t).postfix = (byte)0;
                                            (t).len = strlen((t).ustring);
                                        }
                                        else if (pequals(id, Id.VERSIONX))
                                        {
                                            (t).value = TOK.int64Literal;
                                            (t).intvalue = (long)global.versionNumber();
                                        }
                                        else if (pequals(id, Id.EOFX))
                                        {
                                            (t).value = TOK.endOfFile;
                                            for (; !(((this.p.get() & 0xFF) == 0 || (this.p.get() & 0xFF) == 26));) {
                                                this.p.postInc();
                                            }
                                        }
                                    }
                                    return ;
                                }
                            case 47:
                                this.p.postInc();
                                switch ((this.p.get() & 0xFF))
                                {
                                    case 61:
                                        this.p.postInc();
                                        (t).value = TOK.divAssign;
                                        return ;
                                    case 42:
                                        this.p.postInc();
                                        startLoc = this.loc().copy();
                                        for (; (1) != 0;){
                                            for (; (1) != 0;){
                                                byte c_1 = this.p.get();
                                                switch ((c_1 & 0xFF))
                                                {
                                                    case 47:
                                                        break;
                                                    case 10:
                                                        this.endOfLine();
                                                        this.p.postInc();
                                                        continue;
                                                    case 13:
                                                        this.p.postInc();
                                                        if ((this.p.get() & 0xFF) != 10)
                                                            this.endOfLine();
                                                        continue;
                                                    case 0:
                                                    case 26:
                                                        this.error(new BytePtr("unterminated /* */ comment"));
                                                        this.p = pcopy(this.end);
                                                        (t).loc = this.loc().copy();
                                                        (t).value = TOK.endOfFile;
                                                        return ;
                                                    default:
                                                    if (((c_1 & 0xFF) & 128) != 0)
                                                    {
                                                        int u_1 = this.decodeUTF();
                                                        if ((u_1 == 8233 || u_1 == 8232))
                                                            this.endOfLine();
                                                    }
                                                    this.p.postInc();
                                                    continue;
                                                }
                                                break;
                                            }
                                            this.p.postInc();
                                            if (((this.p.get(-2) & 0xFF) == 42 && this.p.minus(3) != (t).ptr))
                                                break;
                                        }
                                        if (this.commentToken)
                                        {
                                            (t).loc = startLoc.copy();
                                            (t).value = TOK.comment;
                                            return ;
                                        }
                                        else if (((this.doDocComment && ((t).ptr.get(2) & 0xFF) == 42) && this.p.minus(4) != (t).ptr))
                                        {
                                            this.getDocComment(t, ((lastLine == startLoc.linnum) ? 1 : 0), startLoc.linnum - this.lastDocLine > 1);
                                            this.lastDocLine = this.scanloc.linnum;
                                        }
                                        continue L_outer1;
                                    case 47:
                                        startLoc = this.loc().copy();
                                        for (; (1) != 0;){
                                            byte c_2 = (this.p.plusAssign(1)).get();
                                            switch ((c_2 & 0xFF))
                                            {
                                                case 10:
                                                    break;
                                                case 13:
                                                    if ((this.p.get(1) & 0xFF) == 10)
                                                        this.p.postInc();
                                                    break;
                                                case 0:
                                                case 26:
                                                    if (this.commentToken)
                                                    {
                                                        this.p = pcopy(this.end);
                                                        (t).loc = startLoc.copy();
                                                        (t).value = TOK.comment;
                                                        return ;
                                                    }
                                                    if ((this.doDocComment && ((t).ptr.get(2) & 0xFF) == 47))
                                                    {
                                                        this.getDocComment(t, ((lastLine == startLoc.linnum) ? 1 : 0), startLoc.linnum - this.lastDocLine > 1);
                                                        this.lastDocLine = this.scanloc.linnum;
                                                    }
                                                    this.p = pcopy(this.end);
                                                    (t).loc = this.loc().copy();
                                                    (t).value = TOK.endOfFile;
                                                    return ;
                                                default:
                                                if (((c_2 & 0xFF) & 128) != 0)
                                                {
                                                    int u_2 = this.decodeUTF();
                                                    if ((u_2 == 8233 || u_2 == 8232))
                                                        break;
                                                }
                                                continue;
                                            }
                                            break;
                                        }
                                        if (this.commentToken)
                                        {
                                            this.p.postInc();
                                            this.endOfLine();
                                            (t).loc = startLoc.copy();
                                            (t).value = TOK.comment;
                                            return ;
                                        }
                                        if ((this.doDocComment && ((t).ptr.get(2) & 0xFF) == 47))
                                        {
                                            this.getDocComment(t, ((lastLine == startLoc.linnum) ? 1 : 0), startLoc.linnum - this.lastDocLine > 1);
                                            this.lastDocLine = this.scanloc.linnum;
                                        }
                                        this.p.postInc();
                                        this.endOfLine();
                                        continue L_outer1;
                                    case 43:
                                        int nest = 0;
                                        startLoc = this.loc().copy();
                                        this.p.postInc();
                                        nest = 1;
                                        for (; (1) != 0;){
                                            byte c_4 = this.p.get();
                                            switch ((c_4 & 0xFF))
                                            {
                                                case 47:
                                                    this.p.postInc();
                                                    if ((this.p.get() & 0xFF) == 43)
                                                    {
                                                        this.p.postInc();
                                                        nest++;
                                                    }
                                                    continue;
                                                case 43:
                                                    this.p.postInc();
                                                    if ((this.p.get() & 0xFF) == 47)
                                                    {
                                                        this.p.postInc();
                                                        if ((nest -= 1) == 0)
                                                            break;
                                                    }
                                                    continue;
                                                case 13:
                                                    this.p.postInc();
                                                    if ((this.p.get() & 0xFF) != 10)
                                                        this.endOfLine();
                                                    continue;
                                                case 10:
                                                    this.endOfLine();
                                                    this.p.postInc();
                                                    continue;
                                                case 0:
                                                case 26:
                                                    this.error(new BytePtr("unterminated /+ +/ comment"));
                                                    this.p = pcopy(this.end);
                                                    (t).loc = this.loc().copy();
                                                    (t).value = TOK.endOfFile;
                                                    return ;
                                                default:
                                                if (((c_4 & 0xFF) & 128) != 0)
                                                {
                                                    int u_3 = this.decodeUTF();
                                                    if ((u_3 == 8233 || u_3 == 8232))
                                                        this.endOfLine();
                                                }
                                                this.p.postInc();
                                                continue;
                                            }
                                            break;
                                        }
                                        if (this.commentToken)
                                        {
                                            (t).loc = startLoc.copy();
                                            (t).value = TOK.comment;
                                            return ;
                                        }
                                        if (((this.doDocComment && ((t).ptr.get(2) & 0xFF) == 43) && this.p.minus(4) != (t).ptr))
                                        {
                                            this.getDocComment(t, ((lastLine == startLoc.linnum) ? 1 : 0), startLoc.linnum - this.lastDocLine > 1);
                                            this.lastDocLine = this.scanloc.linnum;
                                        }
                                        continue L_outer1;
                                    default:
                                    break;
                                }
                                (t).value = TOK.div;
                                return ;
                            case 46:
                                this.p.postInc();
                                if ((isdigit((this.p.get() & 0xFF))) != 0)
                                {
                                    this.p.postDec();
                                    (t).value = this.inreal(t);
                                }
                                else if ((this.p.get(0) & 0xFF) == 46)
                                {
                                    if ((this.p.get(1) & 0xFF) == 46)
                                    {
                                        this.p.plusAssign(2);
                                        (t).value = TOK.dotDotDot;
                                    }
                                    else
                                    {
                                        this.p.postInc();
                                        (t).value = TOK.slice;
                                    }
                                }
                                else
                                    (t).value = TOK.dot;
                                return ;
                            case 38:
                                this.p.postInc();
                                if ((this.p.get() & 0xFF) == 61)
                                {
                                    this.p.postInc();
                                    (t).value = TOK.andAssign;
                                }
                                else if ((this.p.get() & 0xFF) == 38)
                                {
                                    this.p.postInc();
                                    (t).value = TOK.andAnd;
                                }
                                else
                                    (t).value = TOK.and;
                                return ;
                            case 124:
                                this.p.postInc();
                                if ((this.p.get() & 0xFF) == 61)
                                {
                                    this.p.postInc();
                                    (t).value = TOK.orAssign;
                                }
                                else if ((this.p.get() & 0xFF) == 124)
                                {
                                    this.p.postInc();
                                    (t).value = TOK.orOr;
                                }
                                else
                                    (t).value = TOK.or;
                                return ;
                            case 45:
                                this.p.postInc();
                                if ((this.p.get() & 0xFF) == 61)
                                {
                                    this.p.postInc();
                                    (t).value = TOK.minAssign;
                                }
                                else if ((this.p.get() & 0xFF) == 45)
                                {
                                    this.p.postInc();
                                    (t).value = TOK.minusMinus;
                                }
                                else
                                    (t).value = TOK.min;
                                return ;
                            case 43:
                                this.p.postInc();
                                if ((this.p.get() & 0xFF) == 61)
                                {
                                    this.p.postInc();
                                    (t).value = TOK.addAssign;
                                }
                                else if ((this.p.get() & 0xFF) == 43)
                                {
                                    this.p.postInc();
                                    (t).value = TOK.plusPlus;
                                }
                                else
                                    (t).value = TOK.add;
                                return ;
                            case 60:
                                this.p.postInc();
                                if ((this.p.get() & 0xFF) == 61)
                                {
                                    this.p.postInc();
                                    (t).value = TOK.lessOrEqual;
                                }
                                else if ((this.p.get() & 0xFF) == 60)
                                {
                                    this.p.postInc();
                                    if ((this.p.get() & 0xFF) == 61)
                                    {
                                        this.p.postInc();
                                        (t).value = TOK.leftShiftAssign;
                                    }
                                    else
                                        (t).value = TOK.leftShift;
                                }
                                else
                                    (t).value = TOK.lessThan;
                                return ;
                            case 62:
                                this.p.postInc();
                                if ((this.p.get() & 0xFF) == 61)
                                {
                                    this.p.postInc();
                                    (t).value = TOK.greaterOrEqual;
                                }
                                else if ((this.p.get() & 0xFF) == 62)
                                {
                                    this.p.postInc();
                                    if ((this.p.get() & 0xFF) == 61)
                                    {
                                        this.p.postInc();
                                        (t).value = TOK.rightShiftAssign;
                                    }
                                    else if ((this.p.get() & 0xFF) == 62)
                                    {
                                        this.p.postInc();
                                        if ((this.p.get() & 0xFF) == 61)
                                        {
                                            this.p.postInc();
                                            (t).value = TOK.unsignedRightShiftAssign;
                                        }
                                        else
                                            (t).value = TOK.unsignedRightShift;
                                    }
                                    else
                                        (t).value = TOK.rightShift;
                                }
                                else
                                    (t).value = TOK.greaterThan;
                                return ;
                            case 33:
                                this.p.postInc();
                                if ((this.p.get() & 0xFF) == 61)
                                {
                                    this.p.postInc();
                                    (t).value = TOK.notEqual;
                                }
                                else
                                    (t).value = TOK.not;
                                return ;
                            case 61:
                                this.p.postInc();
                                if ((this.p.get() & 0xFF) == 61)
                                {
                                    this.p.postInc();
                                    (t).value = TOK.equal;
                                }
                                else if ((this.p.get() & 0xFF) == 62)
                                {
                                    this.p.postInc();
                                    (t).value = TOK.goesTo;
                                }
                                else
                                    (t).value = TOK.assign;
                                return ;
                            case 126:
                                this.p.postInc();
                                if ((this.p.get() & 0xFF) == 61)
                                {
                                    this.p.postInc();
                                    (t).value = TOK.concatenateAssign;
                                }
                                else
                                    (t).value = TOK.tilde;
                                return ;
                            case 94:
                                this.p.postInc();
                                if ((this.p.get() & 0xFF) == 94)
                                {
                                    this.p.postInc();
                                    if ((this.p.get() & 0xFF) == 61)
                                    {
                                        this.p.postInc();
                                        (t).value = TOK.powAssign;
                                    }
                                    else
                                        (t).value = TOK.pow;
                                }
                                else if ((this.p.get() & 0xFF) == 61)
                                {
                                    this.p.postInc();
                                    (t).value = TOK.xorAssign;
                                }
                                else
                                    (t).value = TOK.xor;
                                return ;
                            case 40:
                                this.p.postInc();
                                (t).value = TOK.leftParentheses;
                                return ;
                            case 41:
                                this.p.postInc();
                                (t).value = TOK.rightParentheses;
                                return ;
                            case 91:
                                this.p.postInc();
                                (t).value = TOK.leftBracket;
                                return ;
                            case 93:
                                this.p.postInc();
                                (t).value = TOK.rightBracket;
                                return ;
                            case 123:
                                this.p.postInc();
                                (t).value = TOK.leftCurly;
                                return ;
                            case 125:
                                this.p.postInc();
                                (t).value = TOK.rightCurly;
                                return ;
                            case 63:
                                this.p.postInc();
                                (t).value = TOK.question;
                                return ;
                            case 44:
                                this.p.postInc();
                                (t).value = TOK.comma;
                                return ;
                            case 59:
                                this.p.postInc();
                                (t).value = TOK.semicolon;
                                return ;
                            case 58:
                                this.p.postInc();
                                (t).value = TOK.colon;
                                return ;
                            case 36:
                                this.p.postInc();
                                (t).value = TOK.dollar;
                                return ;
                            case 64:
                                this.p.postInc();
                                (t).value = TOK.at;
                                return ;
                            case 42:
                                this.p.postInc();
                                if ((this.p.get() & 0xFF) == 61)
                                {
                                    this.p.postInc();
                                    (t).value = TOK.mulAssign;
                                }
                                else
                                    (t).value = TOK.mul;
                                return ;
                            case 37:
                                this.p.postInc();
                                if ((this.p.get() & 0xFF) == 61)
                                {
                                    this.p.postInc();
                                    (t).value = TOK.modAssign;
                                }
                                else
                                    (t).value = TOK.mod;
                                return ;
                            case 35:
                                this.p.postInc();
                                Token n = new Token().copy();
                                this.scan(n);
                                if ((n.value & 0xFF) == 120)
                                {
                                    if (pequals(n.ident, Id.line))
                                    {
                                        this.poundLine();
                                        continue L_outer1;
                                    }
                                    else
                                    {
                                        Loc locx = this.loc().copy();
                                        this.warning(locx, new BytePtr("C preprocessor directive `#%s` is not supported"), n.ident.toChars());
                                    }
                                }
                                else if ((n.value & 0xFF) == 183)
                                {
                                    this.error(new BytePtr("C preprocessor directive `#if` is not supported, use `version` or `static if`"));
                                }
                                (t).value = TOK.pound;
                                return ;
                            default:
                            int c_3 = (this.p.get() & 0xFF);
                            if ((c_3 & 128) != 0)
                            {
                                c_3 = this.decodeUTF();
                                if (isUniAlpha(c_3))
                                    /*goto case_ident*/{ __dispatch1 = -2; continue dispatched_1; }
                                if ((c_3 == 8233 || c_3 == 8232))
                                {
                                    this.endOfLine();
                                    this.p.postInc();
                                    continue L_outer1;
                                }
                            }
                            if ((c_3 < 128 && (isprint(c_3)) != 0))
                                this.error(new BytePtr("character '%c' is not a valid token"), c_3);
                            else
                                this.error(new BytePtr("character 0x%02x is not a valid token"), c_3);
                            this.p.postInc();
                            continue L_outer1;
                        }
                    } while(__dispatch1 != 0);
                }
            }
        }

        public  Token peek(Token ct) {
            Token t = null;
            if ((ct).next != null)
                t = (ct).next;
            else
            {
                t = this.allocateToken();
                this.scan(t);
                (ct).next = t;
            }
            return t;
        }

        public  Token peekPastParen(Token tk) {
            int parens = 1;
            int curlynest = 0;
            for (; (1) != 0;){
                tk = this.peek(tk);
                switch (((tk).value & 0xFF))
                {
                    case 1:
                        parens++;
                        continue;
                    case 2:
                        parens -= 1;
                        if ((parens) != 0)
                            continue;
                        tk = this.peek(tk);
                        break;
                    case 5:
                        curlynest++;
                        continue;
                    case 6:
                        if ((curlynest -= 1) >= 0)
                            continue;
                        break;
                    case 9:
                        if ((curlynest) != 0)
                            continue;
                        break;
                    case 11:
                        break;
                    default:
                    continue;
                }
                return tk;
            }
        }

        public  int escapeSequence() {
            return escapeSequence(this.token.loc, this.diagnosticReporter, this.p);
        }

        public int escapeSequence(Loc loc, DiagnosticReporter handler, BytePtr sequence) {
            {
                {
                    assert(handler != null);
                }
            }
            p = pcopy(sequence);
            try {
                int c = (p.get() & 0xFF);
                int ndigits = 0;
                {
                    int __dispatch7 = 0;
                    dispatched_7:
                    do {
                        switch (__dispatch7 != 0 ? __dispatch7 : c)
                        {
                            case 39:
                            case 34:
                            case 63:
                            case 92:
                            /*Lconsume:*/
                            case -1:
                            __dispatch7 = 0;
                                p.postInc();
                                break;
                            case 97:
                                c = 7;
                                /*goto Lconsume*/{ __dispatch7 = -1; continue dispatched_7; }
                            case 98:
                                c = 8;
                                /*goto Lconsume*/{ __dispatch7 = -1; continue dispatched_7; }
                            case 102:
                                c = 12;
                                /*goto Lconsume*/{ __dispatch7 = -1; continue dispatched_7; }
                            case 110:
                                c = 10;
                                /*goto Lconsume*/{ __dispatch7 = -1; continue dispatched_7; }
                            case 114:
                                c = 13;
                                /*goto Lconsume*/{ __dispatch7 = -1; continue dispatched_7; }
                            case 116:
                                c = 9;
                                /*goto Lconsume*/{ __dispatch7 = -1; continue dispatched_7; }
                            case 118:
                                c = 11;
                                /*goto Lconsume*/{ __dispatch7 = -1; continue dispatched_7; }
                            case 117:
                                ndigits = 4;
                                /*goto Lhex*/{ __dispatch7 = -2; continue dispatched_7; }
                            case 85:
                                ndigits = 8;
                                /*goto Lhex*/{ __dispatch7 = -2; continue dispatched_7; }
                            case 120:
                                ndigits = 2;
                            /*Lhex:*/
                            case -2:
                            __dispatch7 = 0;
                                p.postInc();
                                c = (p.get() & 0xFF);
                                if (ishex((byte)c))
                                {
                                    int v = 0;
                                    int n = 0;
                                    for (; (1) != 0;){
                                        if ((isdigit(((byte)c & 0xFF))) != 0)
                                            c -= 48;
                                        else if ((islower(c)) != 0)
                                            c -= 87;
                                        else
                                            c -= 55;
                                        v = v * 16 + c;
                                        c = ((p.plusAssign(1)).get() & 0xFF);
                                        if ((n += 1) == ndigits)
                                            break;
                                        if (!(ishex((byte)c)))
                                        {
                                            handler.error(loc, new BytePtr("escape hex sequence has %d hex digits instead of %d"), n, ndigits);
                                            break;
                                        }
                                    }
                                    if ((ndigits != 2 && !(utf_isValidDchar(v))))
                                    {
                                        handler.error(loc, new BytePtr("invalid UTF character \\U%08x"), v);
                                        v = 63;
                                    }
                                    c = v;
                                }
                                else
                                {
                                    handler.error(loc, new BytePtr("undefined escape hex sequence \\%c%c"), sequence.get(0), c);
                                    p.postInc();
                                }
                                break;
                            case 38:
                                {
                                    BytePtr idstart = pcopy(p.plusAssign(1));
                                    for (; (1) != 0;p.postInc()){
                                        switch ((p.get() & 0xFF))
                                        {
                                            case 59:
                                                c = HtmlNamedEntity(idstart, ((p.minus(idstart))));
                                                if (c == -1)
                                                {
                                                    handler.error(loc, new BytePtr("unnamed character entity &%.*s;"), (p.minus(idstart)), idstart);
                                                    c = 63;
                                                }
                                                p.postInc();
                                                break;
                                            default:
                                            if (((isalpha((p.get() & 0xFF))) != 0 || (p != idstart && (isdigit((p.get() & 0xFF))) != 0)))
                                                continue;
                                            handler.error(loc, new BytePtr("unterminated named entity &%.*s;"), (p.minus(idstart)) + 1, idstart);
                                            c = 63;
                                            break;
                                        }
                                        break;
                                    }
                                }
                                break;
                            case 0:
                            case 26:
                                c = 92;
                                break;
                            default:
                            if (isoctal((byte)c))
                            {
                                int v_1 = 0;
                                int n_1 = 0;
                                do {
                                    {
                                        v_1 = v_1 * 8 + (c - 48);
                                        c = ((p.plusAssign(1)).get() & 0xFF);
                                    }
                                } while (((n_1 += 1) < 3 && isoctal((byte)c)));
                                c = v_1;
                                if (c > 255)
                                    handler.error(loc, new BytePtr("escape octal sequence \\%03o is larger than \\377"), c);
                            }
                            else
                            {
                                handler.error(loc, new BytePtr("undefined escape sequence \\%c"), c);
                                p.postInc();
                            }
                            break;
                        }
                    } while(__dispatch7 != 0);
                }
                return c;
            }
            finally {
            }
        }

        public  void wysiwygStringConstant(Token result) {
            (result).value = TOK.string_;
            Loc start = this.loc().copy();
            byte terminator = this.p.get(0);
            this.p.postInc();
            stringbuffer.reset();
            for (; (1) != 0;){
                int c = (this.p.get(0) & 0xFF);
                this.p.postInc();
                switch (c)
                {
                    case 10:
                        this.endOfLine();
                        break;
                    case 13:
                        if ((this.p.get(0) & 0xFF) == 10)
                            continue;
                        c = 0x0000a;
                        this.endOfLine();
                        break;
                    case 0:
                    case 26:
                        this.error(new BytePtr("unterminated string constant starting at %s"), start.toChars(global.params.showColumns));
                        (result).setString();
                        this.p.postDec();
                        return ;
                    default:
                    if (c == (terminator & 0xFF))
                    {
                        (result).setString(stringbuffer);
                        this.stringPostfix(result);
                        return ;
                    }
                    else if ((c & 128) != 0)
                    {
                        this.p.postDec();
                        int u = this.decodeUTF();
                        this.p.postInc();
                        if ((u == 8233 || u == 8232))
                            this.endOfLine();
                        stringbuffer.writeUTF8(u);
                        continue;
                    }
                    break;
                }
                stringbuffer.writeByte(c);
            }
        }

        public  byte hexStringConstant(Token t) {
            Loc start = this.loc().copy();
            int n = 0;
            int v = -1;
            this.p.postInc();
            stringbuffer.reset();
        L_outer2:
            for (; (1) != 0;){
                int c = (this.p.postInc().get() & 0xFF);
                {
                    int __dispatch10 = 0;
                    dispatched_10:
                    do {
                        switch (__dispatch10 != 0 ? __dispatch10 : c)
                        {
                            case 32:
                            case 9:
                            case 11:
                            case 12:
                                continue L_outer2;
                            case 13:
                                if ((this.p.get() & 0xFF) == 10)
                                    continue L_outer2;
                                /*goto case*/{ __dispatch10 = 10; continue dispatched_10; }
                            case 10:
                                __dispatch10 = 0;
                                this.endOfLine();
                                continue L_outer2;
                            case 0:
                            case 26:
                                this.error(new BytePtr("unterminated string constant starting at %s"), start.toChars(global.params.showColumns));
                                (t).setString();
                                this.p.postDec();
                                return TOK.hexadecimalString;
                            case 34:
                                if ((n & 1) != 0)
                                {
                                    this.error(new BytePtr("odd number (%d) of hex characters in hex string"), n);
                                    stringbuffer.writeByte(v);
                                }
                                (t).setString(stringbuffer);
                                this.stringPostfix(t);
                                return TOK.hexadecimalString;
                            default:
                            if ((c >= 48 && c <= 57))
                                c -= 48;
                            else if ((c >= 97 && c <= 102))
                                c -= 87;
                            else if ((c >= 65 && c <= 70))
                                c -= 55;
                            else if ((c & 128) != 0)
                            {
                                this.p.postDec();
                                int u = this.decodeUTF();
                                this.p.postInc();
                                if ((u == 8233 || u == 8232))
                                    this.endOfLine();
                                else
                                    this.error(new BytePtr("non-hex character \\u%04x in hex string"), u);
                            }
                            else
                                this.error(new BytePtr("non-hex character '%c' in hex string"), c);
                            if ((n & 1) != 0)
                            {
                                v = v << 4 | c;
                                stringbuffer.writeByte(v);
                            }
                            else
                                v = c;
                            n++;
                            break;
                        }
                    } while(__dispatch10 != 0);
                }
            }
        }

        public  void delimitedStringConstant(Token result) {
            (result).value = TOK.string_;
            Loc start = this.loc().copy();
            int delimleft = 0x00000;
            int delimright = 0x00000;
            int nest = 1;
            int nestcount = -1;
            Identifier hereid = null;
            int blankrol = 0;
            int startline = 0;
            this.p.postInc();
            stringbuffer.reset();
            try {
            L_outer3:
                for (; (1) != 0;){
                    int c = (this.p.postInc().get() & 0xFF);
                    {
                        int __dispatch11 = 0;
                        dispatched_11:
                        do {
                            switch (__dispatch11 != 0 ? __dispatch11 : c)
                            {
                                case 10:
                                /*Lnextline:*/
                                case -1:
                                __dispatch11 = 0;
                                    this.endOfLine();
                                    startline = 1;
                                    if ((blankrol) != 0)
                                    {
                                        blankrol = 0;
                                        continue L_outer3;
                                    }
                                    if (hereid != null)
                                    {
                                        stringbuffer.writeUTF8(c);
                                        continue L_outer3;
                                    }
                                    break;
                                case 13:
                                    if ((this.p.get() & 0xFF) == 10)
                                        continue L_outer3;
                                    c = 0x0000a;
                                    /*goto Lnextline*/{ __dispatch11 = -1; continue dispatched_11; }
                                case 0:
                                case 26:
                                    this.error(new BytePtr("unterminated delimited string constant starting at %s"), start.toChars(global.params.showColumns));
                                    (result).setString();
                                    this.p.postDec();
                                    return ;
                                default:
                                if ((c & 128) != 0)
                                {
                                    this.p.postDec();
                                    c = this.decodeUTF();
                                    this.p.postInc();
                                    if ((c == 8233 || c == 8232))
                                        /*goto Lnextline*/{ __dispatch11 = -1; continue dispatched_11; }
                                }
                                break;
                            }
                        } while(__dispatch11 != 0);
                    }
                    if (delimleft == 0)
                    {
                        delimleft = c;
                        nest = 1;
                        nestcount = 1;
                        if (c == 40)
                            delimright = 0x00029;
                        else if (c == 123)
                            delimright = 0x0007d;
                        else if (c == 91)
                            delimright = 0x0005d;
                        else if (c == 60)
                            delimright = 0x0003e;
                        else if ((((isalpha(c)) != 0 || c == 95) || (c >= 128 && isUniAlpha(c))))
                        {
                            Token tok = new Token().copy();
                            this.p.postDec();
                            this.scan(tok);
                            if ((tok.value & 0xFF) != 120)
                            {
                                this.error(new BytePtr("identifier expected for heredoc, not %s"), tok.toChars());
                                delimright = c;
                            }
                            else
                            {
                                hereid = tok.ident;
                                blankrol = 1;
                            }
                            nest = 0;
                        }
                        else
                        {
                            delimright = c;
                            nest = 0;
                            if ((isspace(c)) != 0)
                                this.error(new BytePtr("delimiter cannot be whitespace"));
                        }
                    }
                    else
                    {
                        if ((blankrol) != 0)
                        {
                            this.error(new BytePtr("heredoc rest of line should be blank"));
                            blankrol = 0;
                            continue L_outer3;
                        }
                        if (nest == 1)
                        {
                            if (c == delimleft)
                                nestcount++;
                            else if (c == delimright)
                            {
                                nestcount--;
                                if (nestcount == 0)
                                    /*goto Ldone*/throw Dispatch0.INSTANCE;
                            }
                        }
                        else if (c == delimright)
                            /*goto Ldone*/throw Dispatch0.INSTANCE;
                        if ((((startline) != 0 && (((isalpha(c)) != 0 || c == 95) || (c >= 128 && isUniAlpha(c)))) && hereid != null))
                        {
                            Token tok = new Token().copy();
                            BytePtr psave = pcopy(this.p);
                            this.p.postDec();
                            this.scan(tok);
                            if (((tok.value & 0xFF) == 120 && tok.ident == hereid))
                            {
                                /*goto Ldone*/throw Dispatch0.INSTANCE;
                            }
                            this.p = pcopy(psave);
                        }
                        stringbuffer.writeUTF8(c);
                        startline = 0;
                    }
                }
            }
            catch(Dispatch0 __d){}
        /*Ldone:*/
            if ((this.p.get() & 0xFF) == 34)
                this.p.postInc();
            else if (hereid != null)
                this.error(new BytePtr("delimited string must end in %s\""), hereid.toChars());
            else
                this.error(new BytePtr("delimited string must end in %c\""), delimright);
            (result).setString(stringbuffer);
            this.stringPostfix(result);
        }

        public  void tokenStringConstant(Token result) {
            (result).value = TOK.string_;
            int nest = 1;
            Loc start = this.loc().copy();
            BytePtr pstart = pcopy(this.p.plusAssign(1));
            this.inTokenStringConstant++;
            try {
                for (; (1) != 0;){
                    Token tok = new Token().copy();
                    this.scan(tok);
                    switch ((tok.value & 0xFF))
                    {
                        case 5:
                            nest++;
                            continue;
                        case 6:
                            if ((nest -= 1) == 0)
                            {
                                (result).setString(pstart, ((this.p.minus(1).minus(pstart))));
                                this.stringPostfix(result);
                                return ;
                            }
                            continue;
                        case 11:
                            this.error(new BytePtr("unterminated token string constant starting at %s"), start.toChars(global.params.showColumns));
                            (result).setString();
                            return ;
                        default:
                        continue;
                    }
                }
            }
            finally {
                this.inTokenStringConstant--;
            }
        }

        public  void escapeStringConstant(Token t) {
            (t).value = TOK.string_;
            Loc start = this.loc().copy();
            this.p.postInc();
            stringbuffer.reset();
            for (; (1) != 0;){
                int c = (this.p.postInc().get() & 0xFF);
                switch (c)
                {
                    case 92:
                        switch ((this.p.get() & 0xFF))
                        {
                            case 117:
                            case 85:
                            case 38:
                                c = this.escapeSequence();
                                stringbuffer.writeUTF8(c);
                                continue;
                            default:
                            c = this.escapeSequence();
                            break;
                        }
                        break;
                    case 10:
                        this.endOfLine();
                        break;
                    case 13:
                        if ((this.p.get() & 0xFF) == 10)
                            continue;
                        c = 0x0000a;
                        this.endOfLine();
                        break;
                    case 34:
                        (t).setString(stringbuffer);
                        this.stringPostfix(t);
                        return ;
                    case 0:
                    case 26:
                        this.p.postDec();
                        this.error(new BytePtr("unterminated string constant starting at %s"), start.toChars(global.params.showColumns));
                        (t).setString();
                        return ;
                    default:
                    if ((c & 128) != 0)
                    {
                        this.p.postDec();
                        c = this.decodeUTF();
                        if ((c == 8232 || c == 8233))
                        {
                            c = 0x0000a;
                            this.endOfLine();
                        }
                        this.p.postInc();
                        stringbuffer.writeUTF8(c);
                        continue;
                    }
                    break;
                }
                stringbuffer.writeByte(c);
            }
        }

        public  byte charConstant(Token t) {
            byte tk = TOK.charLiteral;
            this.p.postInc();
            int c = (this.p.postInc().get() & 0xFF);
            {
                int __dispatch15 = 0;
                dispatched_15:
                do {
                    switch (__dispatch15 != 0 ? __dispatch15 : c)
                    {
                        case 92:
                            switch ((this.p.get() & 0xFF))
                            {
                                case 117:
                                    (t).intvalue = (long)this.escapeSequence();
                                    tk = TOK.wcharLiteral;
                                    break;
                                case 85:
                                case 38:
                                    (t).intvalue = (long)this.escapeSequence();
                                    tk = TOK.dcharLiteral;
                                    break;
                                default:
                                (t).intvalue = (long)this.escapeSequence();
                                break;
                            }
                            break;
                        case 10:
                        /*L1:*/
                        case -1:
                        __dispatch15 = 0;
                            this.endOfLine();
                        case 13:
                            /*goto case*/{ __dispatch15 = 39; continue dispatched_15; }
                        case 0:
                        case 26:
                            this.p.postDec();
                        case 39:
                            __dispatch15 = 0;
                            this.error(new BytePtr("unterminated character constant"));
                            (t).intvalue = 63L;
                            return tk;
                        default:
                        if ((c & 128) != 0)
                        {
                            this.p.postDec();
                            c = this.decodeUTF();
                            this.p.postInc();
                            if ((c == 8232 || c == 8233))
                                /*goto L1*/{ __dispatch15 = -1; continue dispatched_15; }
                            if ((c < 55296 || (c >= 57344 && c < 65534)))
                                tk = TOK.wcharLiteral;
                            else
                                tk = TOK.dcharLiteral;
                        }
                        (t).intvalue = (long)c;
                        break;
                    }
                } while(__dispatch15 != 0);
            }
            if ((this.p.get() & 0xFF) != 39)
            {
                this.error(new BytePtr("unterminated character constant"));
                (t).intvalue = 63L;
                return tk;
            }
            this.p.postInc();
            return tk;
        }

        public  void stringPostfix(Token t) {
            switch ((this.p.get() & 0xFF))
            {
                case 99:
                case 119:
                case 100:
                    (t).postfix = (byte)this.p.get();
                    this.p.postInc();
                    break;
                default:
                (t).postfix = (byte)0;
                break;
            }
        }

        public  byte number(Token t) {
            int base = 10;
            BytePtr start = pcopy(this.p);
            long n = 0L;
            int d = 0;
            boolean err = false;
            Ref<Boolean> overflow = ref(false);
            boolean anyBinaryDigitsNoSingleUS = false;
            boolean anyHexDigitsNoSingleUS = false;
            int c = (this.p.get() & 0xFF);
            try {
                if (c == 48)
                {
                    this.p.plusAssign(1);
                    c = (this.p.get() & 0xFF);
                    {
                        int __dispatch18 = 0;
                        dispatched_18:
                        do {
                            switch (__dispatch18 != 0 ? __dispatch18 : c)
                            {
                                case 48:
                                case 49:
                                case 50:
                                case 51:
                                case 52:
                                case 53:
                                case 54:
                                case 55:
                                case 56:
                                case 57:
                                    base = 8;
                                    break;
                                case 120:
                                case 88:
                                    this.p.plusAssign(1);
                                    base = 16;
                                    break;
                                case 98:
                                case 66:
                                    this.p.plusAssign(1);
                                    base = 2;
                                    break;
                                case 46:
                                    if ((this.p.get(1) & 0xFF) == 46)
                                        /*goto Ldone*/throw Dispatch0.INSTANCE;
                                    if ((((isalpha((this.p.get(1) & 0xFF))) != 0 || (this.p.get(1) & 0xFF) == 95) || ((this.p.get(1) & 0xFF) & 128) != 0))
                                        /*goto Ldone*/throw Dispatch0.INSTANCE;
                                    /*goto Lreal*/
                                    this.p = pcopy(start);
                                    return this.inreal(t);
                                case 105:
                                case 102:
                                case 70:
                                    /*goto Lreal*/
                                    this.p = pcopy(start);
                                    return this.inreal(t);
                                case 95:
                                    this.p.plusAssign(1);
                                    base = 8;
                                    break;
                                case 76:
                                    if ((this.p.get(1) & 0xFF) == 105) {
                                        /*goto Lreal*/
                                        this.p = pcopy(start);
                                        return this.inreal(t);
                                    }
                                    break;
                                default:
                                break;
                            }
                        } while(__dispatch18 != 0);
                    }
                }
            L_outer4:
                for (; (1) != 0;){
                    c = (this.p.get() & 0xFF);
                    {
                        int __dispatch19 = 0;
                        dispatched_19:
                        do {
                            switch (__dispatch19 != 0 ? __dispatch19 : c)
                            {
                                case 48:
                                case 49:
                                case 50:
                                case 51:
                                case 52:
                                case 53:
                                case 54:
                                case 55:
                                case 56:
                                case 57:
                                    this.p.plusAssign(1);
                                    d = (c - 48);
                                    break;
                                case 97:
                                case 98:
                                case 99:
                                case 100:
                                case 101:
                                case 102:
                                case 65:
                                case 66:
                                case 67:
                                case 68:
                                case 69:
                                case 70:
                                    this.p.plusAssign(1);
                                    if (base != 16)
                                    {
                                        if ((((c == 101 || c == 69) || c == 102) || c == 70))
                                            /*goto Lreal*/{ __dispatch19 = -1; continue dispatched_19; }
                                    }
                                    if (c >= 97)
                                        d = (c + 10 - 97);
                                    else
                                        d = (c + 10 - 65);
                                    break;
                                case 76:
                                    if ((this.p.get(1) & 0xFF) == 105)
                                        /*goto Lreal*/{ __dispatch19 = -1; continue dispatched_19; }
                                    /*goto Ldone*/throw Dispatch0.INSTANCE;
                                case 46:
                                    if ((this.p.get(1) & 0xFF) == 46)
                                        /*goto Ldone*/throw Dispatch0.INSTANCE;
                                    if ((base == 10 && (((isalpha((this.p.get(1) & 0xFF))) != 0 || (this.p.get(1) & 0xFF) == 95) || ((this.p.get(1) & 0xFF) & 128) != 0)))
                                        /*goto Ldone*/throw Dispatch0.INSTANCE;
                                    if ((base == 16 && ((!(ishex(this.p.get(1))) || (this.p.get(1) & 0xFF) == 95) || ((this.p.get(1) & 0xFF) & 128) != 0)))
                                        /*goto Ldone*/throw Dispatch0.INSTANCE;
                                    if (base == 2)
                                        /*goto Ldone*/throw Dispatch0.INSTANCE;
                                    /*goto Lreal*/{ __dispatch19 = -1; continue dispatched_19; }
                                case 112:
                                case 80:
                                case 105:
                                /*Lreal:*/
                                case -1:
                                __dispatch19 = 0;
                                    this.p = pcopy(start);
                                    return this.inreal(t);
                                case 95:
                                    this.p.plusAssign(1);
                                    continue L_outer4;
                                default:
                                /*goto Ldone*/throw Dispatch0.INSTANCE;
                            }
                        } while(__dispatch19 != 0);
                    }
                    anyHexDigitsNoSingleUS = true;
                    anyBinaryDigitsNoSingleUS = true;
                    if ((!(err) && d >= base))
                    {
                        this.error(new BytePtr("%s digit expected, not `%c`"), base == 2 ? new BytePtr("binary") : base == 8 ? new BytePtr("octal") : new BytePtr("decimal"), c);
                        err = true;
                    }
                    if (n <= 1152921504606846975L)
                        n = n * (long)base + (long)d;
                    else
                    {
                        n = mulu(n, base, overflow);
                        n = addu(n, (long)d, overflow);
                    }
                }
            }
            catch(Dispatch0 __d){}
        /*Ldone:*/
            if ((overflow.value && !(err)))
            {
                this.error(new BytePtr("integer overflow"));
                err = true;
            }
            if (((base == 2 && !(anyBinaryDigitsNoSingleUS)) || (base == 16 && !(anyHexDigitsNoSingleUS))))
                this.error(new BytePtr("`%.*s` isn't a valid integer literal, use `%.*s0` instead"), (this.p.minus(start)), start, 2, start);
            int flags = base == 10 ? FLAGS.decimal : FLAGS.none;
            BytePtr psuffix = pcopy(this.p);
        L_outer5:
            for (; (1) != 0;){
                int f = FLAGS.none;
                {
                    int __dispatch20 = 0;
                    dispatched_20:
                    do {
                        switch (__dispatch20 != 0 ? __dispatch20 : (this.p.get() & 0xFF))
                        {
                            case 85:
                            case 117:
                                f = FLAGS.unsigned;
                                /*goto L1*/{ __dispatch20 = -1; continue dispatched_20; }
                            case 108:
                                f = FLAGS.long_;
                                this.error(new BytePtr("lower case integer suffix 'l' is not allowed. Please use 'L' instead"));
                                /*goto L1*/{ __dispatch20 = -1; continue dispatched_20; }
                            case 76:
                                f = FLAGS.long_;
                            /*L1:*/
                            case -1:
                            __dispatch20 = 0;
                                this.p.postInc();
                                if (((flags & f) != 0 && !(err)))
                                {
                                    this.error(new BytePtr("unrecognized token"));
                                    err = true;
                                }
                                flags = flags | f;
                                continue L_outer5;
                            default:
                            break;
                        }
                    } while(__dispatch20 != 0);
                }
                break;
            }
            if ((base == 8 && n >= 8L))
            {
                if (err)
                    this.error(new BytePtr("octal literals larger than 7 are no longer supported"));
                else
                    this.error(new BytePtr("octal literals `0%llo%.*s` are no longer supported, use `std.conv.octal!%llo%.*s` instead"), n, (this.p.minus(psuffix)), psuffix, n, (this.p.minus(psuffix)), psuffix);
            }
            byte result = TOK.reserved;
            switch (flags)
            {
                case FLAGS.none:
                    if ((n & -9223372036854775808L) != 0)
                        result = TOK.uns64Literal;
                    else if ((n & -4294967296L) != 0)
                        result = TOK.int64Literal;
                    else if ((n & 2147483648L) != 0)
                        result = TOK.uns32Literal;
                    else
                        result = TOK.int32Literal;
                    break;
                case FLAGS.decimal:
                    if ((n & -9223372036854775808L) != 0)
                    {
                        if (!(err))
                        {
                            this.error(new BytePtr("signed integer overflow"));
                            err = true;
                        }
                        result = TOK.uns64Literal;
                    }
                    else if ((n & -2147483648L) != 0)
                        result = TOK.int64Literal;
                    else
                        result = TOK.int32Literal;
                    break;
                case FLAGS.unsigned:
                case 3:
                    if ((n & -4294967296L) != 0)
                        result = TOK.uns64Literal;
                    else
                        result = TOK.uns32Literal;
                    break;
                case 5:
                    if ((n & -9223372036854775808L) != 0)
                    {
                        if (!(err))
                        {
                            this.error(new BytePtr("signed integer overflow"));
                            err = true;
                        }
                        result = TOK.uns64Literal;
                    }
                    else
                        result = TOK.int64Literal;
                    break;
                case FLAGS.long_:
                    if ((n & -9223372036854775808L) != 0)
                        result = TOK.uns64Literal;
                    else
                        result = TOK.int64Literal;
                    break;
                case 6:
                case 7:
                    result = TOK.uns64Literal;
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            (t).intvalue = n;
            return result;
        }

        public  byte inreal(Token t) {
            boolean isWellformedString = true;
            stringbuffer.reset();
            BytePtr pstart = pcopy(this.p);
            boolean hex = false;
            int c = (this.p.postInc().get() & 0xFF);
            if (c == 48)
            {
                c = (this.p.postInc().get() & 0xFF);
                if ((c == 120 || c == 88))
                {
                    hex = true;
                    c = (this.p.postInc().get() & 0xFF);
                }
            }
            for (; (1) != 0;){
                if (c == 46)
                {
                    c = (this.p.postInc().get() & 0xFF);
                    break;
                }
                if ((((isdigit(c)) != 0 || (hex && (isxdigit(c)) != 0)) || c == 95))
                {
                    c = (this.p.postInc().get() & 0xFF);
                    continue;
                }
                break;
            }
            for (; (1) != 0;){
                if ((((isdigit(c)) != 0 || (hex && (isxdigit(c)) != 0)) || c == 95))
                {
                    c = (this.p.postInc().get() & 0xFF);
                    continue;
                }
                break;
            }
            if (((c == 101 || c == 69) || (hex && (c == 112 || c == 80))))
            {
                c = (this.p.postInc().get() & 0xFF);
                if ((c == 45 || c == 43))
                {
                    c = (this.p.postInc().get() & 0xFF);
                }
                boolean anyexp = false;
                for (; (1) != 0;){
                    if ((isdigit(c)) != 0)
                    {
                        anyexp = true;
                        c = (this.p.postInc().get() & 0xFF);
                        continue;
                    }
                    if (c == 95)
                    {
                        c = (this.p.postInc().get() & 0xFF);
                        continue;
                    }
                    if (!(anyexp))
                    {
                        this.error(new BytePtr("missing exponent"));
                        isWellformedString = false;
                    }
                    break;
                }
            }
            else if (hex)
            {
                this.error(new BytePtr("exponent required for hex float"));
                isWellformedString = false;
            }
            this.p.minusAssign(1);
            for (; pstart.lessThan(this.p);){
                if ((pstart.get() & 0xFF) != 95)
                    stringbuffer.writeByte((pstart.get() & 0xFF));
                pstart.plusAssign(1);
            }
            stringbuffer.writeByte(0);
            BytePtr sbufptr = pcopy(toBytePtr(stringbuffer.data));
            byte result = TOK.reserved;
            Ref<Boolean> isOutOfRange = ref(false);
            (t).floatvalue = isWellformedString ? CTFloat.parse(sbufptr, ptr(isOutOfRange)) : CTFloat.zero;
            {
                int __dispatch22 = 0;
                dispatched_22:
                do {
                    switch (__dispatch22 != 0 ? __dispatch22 : (this.p.get() & 0xFF))
                    {
                        case 70:
                        case 102:
                            if ((isWellformedString && !(isOutOfRange.value)))
                                isOutOfRange.value = Port.isFloat32LiteralOutOfRange(sbufptr);
                            result = TOK.float32Literal;
                            this.p.postInc();
                            break;
                        default:
                        if ((isWellformedString && !(isOutOfRange.value)))
                            isOutOfRange.value = Port.isFloat64LiteralOutOfRange(sbufptr);
                        result = TOK.float64Literal;
                        break;
                        case 108:
                            this.error(new BytePtr("use 'L' suffix instead of 'l'"));
                            /*goto case*/{ __dispatch22 = 76; continue dispatched_22; }
                        case 76:
                            __dispatch22 = 0;
                            result = TOK.float80Literal;
                            this.p.postInc();
                            break;
                    }
                } while(__dispatch22 != 0);
            }
            if (((this.p.get() & 0xFF) == 105 || (this.p.get() & 0xFF) == 73))
            {
                if ((this.p.get() & 0xFF) == 73)
                    this.error(new BytePtr("use 'i' suffix instead of 'I'"));
                this.p.postInc();
                switch ((result & 0xFF))
                {
                    case 111:
                        result = TOK.imaginary32Literal;
                        break;
                    case 112:
                        result = TOK.imaginary64Literal;
                        break;
                    case 113:
                        result = TOK.imaginary80Literal;
                        break;
                    default:
                    break;
                }
            }
            boolean isLong = ((result & 0xFF) == 113 || (result & 0xFF) == 116);
            if ((isOutOfRange.value && !(isLong)))
            {
                BytePtr suffix = pcopy(((result & 0xFF) == 111 || (result & 0xFF) == 114) ? new BytePtr("f") : new BytePtr(""));
                this.error(this.scanloc, new BytePtr("number `%s%s` is not representable"), sbufptr, suffix);
            }
            return result;
        }

        public  Loc loc() {
            this.scanloc.charnum = ((this.p.plus(1).minus(this.line)));
            return this.scanloc;
        }

        public  void error(BytePtr format, Object... args) {
            this.diagnosticReporter.error(this.token.loc, format, new Slice<>(args));
        }

        public  void error(Loc loc, BytePtr format, Object... args) {
            this.diagnosticReporter.error(loc, format, new Slice<>(args));
        }

        public  void errorSupplemental(Loc loc, BytePtr format, Object... args) {
            this.diagnosticReporter.errorSupplemental(loc, format, new Slice<>(args));
        }

        public  void warning(Loc loc, BytePtr format, Object... args) {
            this.diagnosticReporter.warning(loc, format, new Slice<>(args));
        }

        public  void warningSupplemental(Loc loc, BytePtr format, Object... args) {
            this.diagnosticReporter.warningSupplemental(loc, format, new Slice<>(args));
        }

        public  void deprecation(BytePtr format, Object... args) {
            this.diagnosticReporter.deprecation(this.token.loc, format, new Slice<>(args));
        }

        public  void deprecation(Loc loc, BytePtr format, Object... args) {
            this.diagnosticReporter.deprecation(loc, format, new Slice<>(args));
        }

        public  void deprecationSupplemental(Loc loc, BytePtr format, Object... args) {
            this.diagnosticReporter.deprecationSupplemental(loc, format, new Slice<>(args));
        }

        public  void poundLine() {
            int linnum = this.scanloc.linnum;
            BytePtr filespec = null;
            Loc loc = this.loc().copy();
            Token tok = new Token().copy();
            this.scan(tok);
            try {
                if (((tok.value & 0xFF) == 105 || (tok.value & 0xFF) == 107))
                {
                    int lin = (int)(tok.intvalue - 1L);
                    if ((long)lin != tok.intvalue - 1L)
                        this.error(new BytePtr("line number `%lld` out of range"), tok.intvalue);
                    else
                        linnum = lin;
                }
                else if ((tok.value & 0xFF) == 218)
                {
                }
                else
                    /*goto Lerr*/throw Dispatch0.INSTANCE;
            L_outer6:
                for (; (1) != 0;){
                    {
                        int __dispatch24 = 0;
                        dispatched_24:
                        do {
                            switch (__dispatch24 != 0 ? __dispatch24 : (this.p.get() & 0xFF))
                            {
                                case 0:
                                case 26:
                                case 10:
                                /*Lnewline:*/
                                case -1:
                                __dispatch24 = 0;
                                    if (!((this.inTokenStringConstant) != 0))
                                    {
                                        this.scanloc.linnum = linnum;
                                        if (filespec != null)
                                            this.scanloc.filename = pcopy(filespec);
                                    }
                                    return ;
                                case 13:
                                    this.p.postInc();
                                    if ((this.p.get() & 0xFF) != 10)
                                    {
                                        this.p.postDec();
                                        /*goto Lnewline*/{ __dispatch24 = -1; continue dispatched_24; }
                                    }
                                    continue L_outer6;
                                case 32:
                                case 9:
                                case 11:
                                case 12:
                                    this.p.postInc();
                                    continue L_outer6;
                                case 95:
                                    if (memcmp(this.p, new BytePtr("__FILE__"), 8) == 0)
                                    {
                                        this.p.plusAssign(8);
                                        filespec = pcopy(Mem.xstrdup(this.scanloc.filename));
                                        continue L_outer6;
                                    }
                                    /*goto Lerr*/throw Dispatch0.INSTANCE;
                                case 34:
                                    if (filespec != null)
                                        /*goto Lerr*/throw Dispatch0.INSTANCE;
                                    stringbuffer.reset();
                                    this.p.postInc();
                                L_outer7:
                                    for (; (1) != 0;){
                                        int c = 0;
                                        c = (this.p.get() & 0xFF);
                                        {
                                            int __dispatch25 = 0;
                                            dispatched_25:
                                            do {
                                                switch (__dispatch25 != 0 ? __dispatch25 : c)
                                                {
                                                    case 10:
                                                    case 13:
                                                    case 0:
                                                    case 26:
                                                        /*goto Lerr*/throw Dispatch0.INSTANCE;
                                                    case 34:
                                                        stringbuffer.writeByte(0);
                                                        filespec = pcopy(Mem.xstrdup(toBytePtr(stringbuffer.data)));
                                                        this.p.postInc();
                                                        break;
                                                    default:
                                                    if ((c & 128) != 0)
                                                    {
                                                        int u = this.decodeUTF();
                                                        if ((u == 8233 || u == 8232))
                                                            /*goto Lerr*/throw Dispatch0.INSTANCE;
                                                    }
                                                    stringbuffer.writeByte(c);
                                                    this.p.postInc();
                                                    continue L_outer7;
                                                }
                                            } while(__dispatch25 != 0);
                                        }
                                        break;
                                    }
                                    continue L_outer6;
                                default:
                                if (((this.p.get() & 0xFF) & 128) != 0)
                                {
                                    int u_1 = this.decodeUTF();
                                    if ((u_1 == 8233 || u_1 == 8232))
                                        /*goto Lnewline*/{ __dispatch24 = -1; continue dispatched_24; }
                                }
                                /*goto Lerr*/throw Dispatch0.INSTANCE;
                            }
                        } while(__dispatch24 != 0);
                    }
                }
            }
            catch(Dispatch0 __d){}
        /*Lerr:*/
            this.error(loc, new BytePtr("#line integer [\"filespec\"]\\n expected"));
        }

        public  int decodeUTF() {
            BytePtr s = pcopy(this.p);
            assert(((s.get() & 0xFF) & 128) != 0);
            int len = 0;
            {
                len = 1;
                for (; (len < 4 && (s.get(len)) != 0);len++){
                }
            }
            IntRef idx = ref(0);
            IntRef u = ref(0x0ffff);
            BytePtr msg = pcopy(utf_decodeChar(s, len, idx, u));
            this.p.plusAssign((idx.value - 1));
            if (msg != null)
            {
                this.error(new BytePtr("%s"), msg);
            }
            return u.value;
        }

        public  void getDocComment(Token t, int lineComment, boolean newParagraph) {
            byte ct = (t).ptr.get(2);
            BytePtr q = pcopy((t).ptr.plus(3));
            BytePtr qend = pcopy(this.p);
            if (((ct & 0xFF) == 42 || (ct & 0xFF) == 43))
                qend.minusAssign(2);
            for (; q.lessThan(qend);q.postInc()){
                if ((q.get() & 0xFF) != (ct & 0xFF))
                    break;
            }
            int linestart = 0;
            if ((ct & 0xFF) == 47)
            {
                for (; (q.lessThan(qend) && ((q.get() & 0xFF) == 32 || (q.get() & 0xFF) == 9));) {
                    q.plusAssign(1);
                }
            }
            else if (q.lessThan(qend))
            {
                if ((q.get() & 0xFF) == 13)
                {
                    q.plusAssign(1);
                    if ((q.lessThan(qend) && (q.get() & 0xFF) == 10))
                        q.plusAssign(1);
                    linestart = 1;
                }
                else if ((q.get() & 0xFF) == 10)
                {
                    q.plusAssign(1);
                    linestart = 1;
                }
            }
            if ((ct & 0xFF) != 47)
            {
                for (; q.lessThan(qend);qend.postDec()){
                    if ((qend.get(-1) & 0xFF) != (ct & 0xFF))
                        break;
                }
            }
            OutBuffer buf = new OutBuffer();
            try {
                Function0<Void> trimTrailingWhitespace = new Function0<Void>(){
                    public Void invoke(){
                        ByteSlice s = buf.peekSlice().copy();
                        int len = s.getLength();
                        for (; ((len) != 0 && ((s.get(len - 1) & 0xFF) == 32 || (s.get(len - 1) & 0xFF) == 9));) {
                            len -= 1;
                        }
                        buf.setsize(len);
                    }
                };
            L_outer8:
                for (; q.lessThan(qend);q.postInc()){
                    byte c = q.get();
                    {
                        int __dispatch26 = 0;
                        dispatched_26:
                        do {
                            switch (__dispatch26 != 0 ? __dispatch26 : (c & 0xFF))
                            {
                                case 42:
                                case 43:
                                    if (((linestart) != 0 && (c & 0xFF) == (ct & 0xFF)))
                                    {
                                        linestart = 0;
                                        trimTrailingWhitespace.invoke();
                                        continue L_outer8;
                                    }
                                    break;
                                case 32:
                                case 9:
                                    break;
                                case 13:
                                    if ((q.get(1) & 0xFF) == 10)
                                        continue L_outer8;
                                    /*goto Lnewline*/{ __dispatch26 = -1; continue dispatched_26; }
                                default:
                                if ((c & 0xFF) == 226)
                                {
                                    if (((q.get(1) & 0xFF) == 128 && ((q.get(2) & 0xFF) == 168 || (q.get(2) & 0xFF) == 169)))
                                    {
                                        q.plusAssign(2);
                                        /*goto Lnewline*/{ __dispatch26 = -1; continue dispatched_26; }
                                    }
                                }
                                linestart = 0;
                                break;
                            /*Lnewline:*/
                            case -1:
                            __dispatch26 = 0;
                                c = (byte)10;
                                case 10:
                                    linestart = 1;
                                    trimTrailingWhitespace.invoke();
                                    break;
                            }
                        } while(__dispatch26 != 0);
                    }
                    buf.writeByte((c & 0xFF));
                }
                trimTrailingWhitespace.invoke();
                ByteSlice s = buf.peekSlice().copy();
                if ((s.getLength() == 0 || (s.get(s.getLength() - 1) & 0xFF) != 10))
                    buf.writeByte(10);
                if (((lineComment) != 0 && this.anyToken)) {
                    if ((t).lineComment != null) t.lineComment = combineComments(t.lineComment, buf.peekChars(), newParagraph);
                    else t.lineComment =  buf.extractChars();
                }
                else {
                    if ((t).blockComment != null) t.blockComment = combineComments(t.blockComment, buf.peekChars(), newParagraph);
                    else t.blockComment =  buf.extractChars();
                }
            }
            finally {
            }
        }

        public static BytePtr combineComments(BytePtr c1, BytePtr c2, boolean newParagraph) {
            BytePtr c = pcopy(c2);
            int newParagraphSize = newParagraph ? 1 : 0;
            if (c1 != null)
            {
                c = pcopy(c1);
                if (c2 != null)
                {
                    int len1 = strlen(c1);
                    int len2 = strlen(c2);
                    int insertNewLine = 0;
                    if (((len1) != 0 && (c1.get(len1 - 1) & 0xFF) != 10))
                    {
                        len1 += 1;
                        insertNewLine = 1;
                    }
                    BytePtr p = pcopy((BytePtr)Mem.xmalloc(len1 + newParagraphSize + len2 + 1));
                    memcpy((BytePtr)(p), (c1), (len1 - insertNewLine));
                    if ((insertNewLine) != 0)
                        p.set((len1 - 1), (byte)10);
                    if (newParagraph)
                        p.set(len1, (byte)10);
                    memcpy((BytePtr)((p.plus(len1).plus(newParagraphSize))), (c2), len2);
                    p.set((len1 + newParagraphSize + len2), (byte)0);
                    c = pcopy(p);
                }
            }
            return c;
        }

        public  void endOfLine() {
            this.scanloc.linnum++;
            this.line = pcopy(this.p);
        }


        public Lexer() {}

        public Lexer copy() {
            Lexer that = new Lexer();
            that.scanloc = this.scanloc;
            that.prevloc = this.prevloc;
            that.p = this.p;
            that.token = this.token;
            that.base = this.base;
            that.end = this.end;
            that.line = this.line;
            that.doDocComment = this.doDocComment;
            that.anyToken = this.anyToken;
            that.commentToken = this.commentToken;
            that.inTokenStringConstant = this.inTokenStringConstant;
            that.lastDocLine = this.lastDocLine;
            that.diagnosticReporter = this.diagnosticReporter;
            that.tokenFreelist = this.tokenFreelist;
            return that;
        }
    }
}
