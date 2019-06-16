package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;

import static org.dlang.dmd.utils.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.console.*;
import static org.dlang.dmd.filecache.*;
import static org.dlang.dmd.lexer.*;
import static org.dlang.dmd.tokens.*;

public class errors {
    static BytePtr vdeprecationheader = new BytePtr("Deprecation: ");
    static int colorHighlightCodenested;

    public static abstract class DiagnosticReporter extends Object
    {
        public abstract int errorCount();
        public abstract int warningCount();
        public abstract int deprecationCount();
        public  void error(Loc loc, BytePtr format, Object... args) {
            this.error(loc, format, new Slice<>(args));
        }

        public abstract void error(Loc loc, BytePtr format, Slice<Object> args);
        public  void errorSupplemental(Loc loc, BytePtr format, Object... args) {
            this.errorSupplemental(loc, format, new Slice<>(args));
        }

        public abstract void errorSupplemental(Loc loc, BytePtr format, Slice<Object> arg2);
        public  void warning(Loc loc, BytePtr format, Object... args) {
            this.warning(loc, format, new Slice<>(args));
        }

        public abstract void warning(Loc loc, BytePtr format, Slice<Object> args);
        public  void warningSupplemental(Loc loc, BytePtr format, Object... args) {
            this.warningSupplemental(loc, format, new Slice<>(args));
        }

        public abstract void warningSupplemental(Loc loc, BytePtr format, Slice<Object> arg2);
        public  void deprecation(Loc loc, BytePtr format, Object... args) {
            this.deprecation(loc, format, new Slice<>(args));
        }

        public abstract void deprecation(Loc loc, BytePtr format, Slice<Object> args);
        public  void deprecationSupplemental(Loc loc, BytePtr format, Object... args) {
            this.deprecationSupplemental(loc, format, new Slice<>(args));
        }

        public abstract void deprecationSupplemental(Loc loc, BytePtr format, Slice<Object> arg2);
    }
    public static class StderrDiagnosticReporter extends DiagnosticReporter
    {
        public byte useDeprecated;
        public int errorCount_;
        public int warningCount_;
        public int deprecationCount_;
        public  StderrDiagnosticReporter(byte useDeprecated) {
            this.useDeprecated = useDeprecated;
        }

        public  int errorCount() {
            return this.errorCount_;
        }

        public  int warningCount() {
            return this.warningCount_;
        }

        public  int deprecationCount() {
            return this.deprecationCount_;
        }

        public  void error(Loc loc, BytePtr format, Slice<Object> args) {
            verror(loc, format, args, null, null, new BytePtr("Error: "));
            this.errorCount_++;
        }

        public  void errorSupplemental(Loc loc, BytePtr format, Slice<Object> args) {
            verrorSupplemental(loc, format, args);
        }

        public  void warning(Loc loc, BytePtr format, Slice<Object> args) {
            vwarning(loc, format, args);
            this.warningCount_++;
        }

        public  void warningSupplemental(Loc loc, BytePtr format, Slice<Object> args) {
            vwarningSupplemental(loc, format, args);
        }

        public  void deprecation(Loc loc, BytePtr format, Slice<Object> args) {
            vdeprecation(loc, format, args, null, null);
            if (this.useDeprecated == (byte)0)
                this.errorCount_++;
            else
                this.deprecationCount_++;
        }

        public  void deprecationSupplemental(Loc loc, BytePtr format, Slice<Object> args) {
            vdeprecationSupplemental(loc, format, args);
        }

    }

    public static class Classification 
    {
        public static final int error = Color.brightRed;
        public static final int gagged = Color.brightBlue;
        public static final int warning = Color.brightYellow;
        public static final int deprecation = Color.brightCyan;
    }

    public static void error(Loc loc, BytePtr format, Object... ap) {
        verror(loc, format, new Slice<>(ap), null, null, new BytePtr("Error: "));
    }

    public static void error(BytePtr filename, int linnum, int charnum, BytePtr format, Object... ap) {
        Loc loc = comma(null, new Loc(filename, linnum, charnum));
        verror(loc, format, new Slice<>(ap), null, null, new BytePtr("Error: "));
    }

    public static void errorSupplemental(Loc loc, BytePtr format, Object... ap) {
        verrorSupplemental(loc, format, new Slice<>(ap));
    }

    public static void warning(Loc loc, BytePtr format, Object... ap) {
        vwarning(loc, format, new Slice<>(ap));
    }

    public static void warningSupplemental(Loc loc, BytePtr format, Object... ap) {
        vwarningSupplemental(loc, format, new Slice<>(ap));
    }

    public static void deprecation(Loc loc, BytePtr format, Object... ap) {
        vdeprecation(loc, format, new Slice<>(ap), null, null);
    }

    public static void deprecationSupplemental(Loc loc, BytePtr format, Object... ap) {
        vdeprecationSupplemental(loc, format, new Slice<>(ap));
    }

    public static void message(Loc loc, BytePtr format, Object... ap) {
        vmessage(loc, format, new Slice<>(ap));
    }

    public static void message(BytePtr format, Object... ap) {
        vmessage(Loc.initial, format, new Slice<>(ap));
    }

    public static void verrorPrint(Loc loc, int headerColor, BytePtr header, BytePtr format, Slice<Object> ap, BytePtr p1, BytePtr p2) {
        Console con = (Console)global.console;
        BytePtr p = loc.toChars(global.params.showColumns);
        if (con != null)
            (con).setColorBright(true);
        if ((p.get(0)) != 0)
        {
            fprintf(stderr,  new ByteSlice("%s: "), p);
            Mem.xfree(p);
        }
        if (con != null)
            (con).setColor(headerColor);
        fputs(header, stderr);
        if (con != null)
            (con).resetColor();
        if (p1 != null)
            fprintf(stderr,  new ByteSlice("%s "), p1);
        if (p2 != null)
            fprintf(stderr,  new ByteSlice("%s "), p2);
        OutBuffer tmp = new OutBuffer();
        tmp.vprintf(format, ap);
        if (con != null && strchr(tmp.peekChars(), 96) != null)
        {
            colorSyntaxHighlight(tmp);
            writeHighlights(con, tmp);
        }
        else
            fputs(tmp.peekChars(), stderr);
        fputc(10, stderr);
        if (global.params.printErrorContext && !(loc.opEquals(Loc.initial)) && strstr(loc.filename,  new ByteSlice(".d-mixin-")) == null && global.params.mixinOut == null)
        {
            FileAndLines fllines = FileCache.fileCache.addOrGetFile(loc.filename.slice(0,strlen(loc.filename)));
            if (loc.linnum - 1 < fllines.lines.getLength())
            {
                ByteSlice line = fllines.lines.get(loc.linnum - 1);
                if (loc.charnum < line.getLength())
                {
                    fprintf(stderr,  new ByteSlice("%.*s\n"), line.getLength(), line.toBytePtr());
                    {
                        int __key47 = 1;
                        int __limit48 = loc.charnum;
                        for (; __key47 < __limit48;__key47 += 1) {
                            int _ = __key47;
                            fputc(32, stderr);
                        }
                    }
                    fputc(94, stderr);
                    fputc(10, stderr);
                }
            }
        }
        fflush(stderr);
    }

    public static void verror(Loc loc, BytePtr format, Slice<Object> ap, BytePtr p1, BytePtr p2, BytePtr header) {
        global.errors++;
        if (!((global.gag) != 0))
        {
            verrorPrint(loc, Color.brightRed, header, format, ap, p1, p2);
            if ((global.params.errorLimit) != 0 && global.errors >= global.params.errorLimit)
                fatal();
        }
        else
        {
            if (global.params.showGaggedErrors)
            {
                fprintf(stderr,  new ByteSlice("(spec:%d) "), global.gag);
                verrorPrint(loc, Color.brightBlue, header, format, ap, p1, p2);
            }
            global.gaggedErrors++;
        }
    }

    public static void verrorSupplemental(Loc loc, BytePtr format, Slice<Object> ap) {
        int color = Color.black;
        if ((global.gag) != 0)
        {
            if (!(global.params.showGaggedErrors))
                return ;
            color = Color.brightBlue;
        }
        else
            color = Color.brightRed;
        verrorPrint(loc, color, new BytePtr("       "), format, ap, null, null);
    }

    public static void vwarning(Loc loc, BytePtr format, Slice<Object> ap) {
        if (global.params.warnings != (byte)2)
        {
            if (!((global.gag) != 0))
            {
                verrorPrint(loc, Color.brightYellow, new BytePtr("Warning: "), format, ap, null, null);
                if (global.params.warnings == (byte)0)
                    global.warnings++;
            }
            else
            {
                global.gaggedWarnings++;
            }
        }
    }

    public static void vwarningSupplemental(Loc loc, BytePtr format, Slice<Object> ap) {
        if (global.params.warnings != (byte)2 && !((global.gag) != 0))
            verrorPrint(loc, Color.brightYellow, new BytePtr("       "), format, ap, null, null);
    }

    public static void vdeprecation(Loc loc, BytePtr format, Slice<Object> ap, BytePtr p1, BytePtr p2) {
        BytePtr header = errors.vdeprecationheader;
        if (global.params.useDeprecated == (byte)0)
            verror(loc, format, ap, p1, p2, header);
        else if (global.params.useDeprecated == (byte)1)
        {
            if (!((global.gag) != 0))
            {
                verrorPrint(loc, Color.brightCyan, header, format, ap, p1, p2);
            }
            else
            {
                global.gaggedWarnings++;
            }
        }
    }

    public static void vmessage(Loc loc, BytePtr format, Slice<Object> ap) {
        BytePtr p = loc.toChars(global.params.showColumns);
        if ((p.get(0)) != 0)
        {
            fprintf(stdout,  new ByteSlice("%s: "), p);
            Mem.xfree(p);
        }
        OutBuffer tmp = new OutBuffer();
        tmp.vprintf(format, ap);
        fputs(tmp.peekChars(), stdout);
        fputc(10, stdout);
        fflush(stdout);
    }

    public static void vdeprecationSupplemental(Loc loc, BytePtr format, Slice<Object> ap) {
        if (global.params.useDeprecated == (byte)0)
            verrorSupplemental(loc, format, ap);
        else if (global.params.useDeprecated == (byte)1 && !((global.gag) != 0))
            verrorPrint(loc, Color.brightCyan, new BytePtr("       "), format, ap, null, null);
    }

    public static void fatal() {
        exit(1);
    }

    public static void halt() {
        throw new AssertionError("Unreachable code!");
    }

    public static void colorSyntaxHighlight(OutBuffer buf) {
        boolean inBacktick = false;
        int iCodeStart = 0;
        int offset = 0;
        {
            int i = offset;
            for (; i < (buf).offset;i += 1){
                byte c = (byte)(buf).data.get(i);
                switch ((int)c)
                {
                    case (byte)96:
                        if (inBacktick)
                        {
                            inBacktick = false;
                            OutBuffer codebuf = new OutBuffer();
                            codebuf.write(((buf).peekSlice().toBytePtr().plus(iCodeStart * 1).plus(1)), i - (iCodeStart + 1));
                            codebuf.writeByte(0);
                            colorHighlightCode(codebuf);
                            (buf).remove(iCodeStart, i - iCodeStart + 1);
                            ByteSlice pre =  new ByteSlice("");
                            i = (buf).insert(iCodeStart, pre.toByteSlice());
                            i = (buf).insert(i, codebuf.peekSlice());
                            i--;
                            break;
                        }
                        inBacktick = true;
                        iCodeStart = i;
                        break;
                    default:
                    {
                        break;
                    }
                }
            }
        }
    }


    public static class HIGHLIGHT 
    {
        public static final byte Default = (byte)0;
        public static final byte Escape = (byte)255;
        public static final byte Identifier = (byte)15;
        public static final byte Keyword = (byte)15;
        public static final byte Literal = (byte)15;
        public static final byte Comment = (byte)8;
        public static final byte Other = (byte)6;
    }

    public static void colorHighlightCode(OutBuffer buf) {
        int nested = errors.colorHighlightCodenested;
        if ((nested) != 0)
        {
            nested -= 1;
            return ;
        }
        nested += 1;
        int gaggedErrorsSave = global.startGagging();
        StderrDiagnosticReporter diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
        Lexer lex = new Lexer(null, (buf).data.toBytePtr(), 0, (buf).offset - 1, false, true, diagnosticReporter);
        OutBuffer res = new OutBuffer();
        BytePtr lastp = (buf).data.toBytePtr();
        res.reserve((buf).offset);
        res.writeByte(255);
        res.writeByte(6);
        for (; (1) != 0;){
            Token tok = new Token();
            lex.scan(tok);
            res.writestring(lastp.slice(0,((tok.ptr.minus(lastp)) / 1)));
            byte highlight = HIGHLIGHT.Default;
            switch (tok.value)
            {
                case (byte)120:
                    highlight = HIGHLIGHT.Identifier;
                    break;
                case (byte)46:
                    highlight = HIGHLIGHT.Comment;
                    break;
                case (byte)105:
                case (byte)106:
                case (byte)107:
                case (byte)108:
                case (byte)109:
                case (byte)110:
                case (byte)111:
                case (byte)112:
                case (byte)113:
                case (byte)114:
                case (byte)115:
                case (byte)116:
                case (byte)117:
                case (byte)118:
                case (byte)119:
                case (byte)121:
                    highlight = HIGHLIGHT.Identifier;
                    break;
                default:
                {
                    if ((tok.isKeyword()) != 0)
                        highlight = HIGHLIGHT.Identifier;
                    break;
                }
            }
            if (highlight != (byte)0)
            {
                res.writeByte(255);
                res.writeByte(highlight);
                res.writestring(tok.ptr.slice(0,((lex.p.minus(tok.ptr)) / 1)));
                res.writeByte(255);
                res.writeByte(6);
            }
            else
                res.writestring(tok.ptr.slice(0,((lex.p.minus(tok.ptr)) / 1)));
            if (tok.value == (byte)11)
                break;
            lastp = lex.p;
        }
        res.writeByte(255);
        res.writeByte(0);
        (buf).setsize(0);
        (buf).write(res);
        global.endGagging(gaggedErrorsSave);
        nested -= 1;
    }

    public static void writeHighlights(Console con, OutBuffer buf) {
        boolean colors = false;
        {
            int i = 0;
            for (; i < (buf).offset;i += 1){
                byte c = (buf).data.get(i);
                if (c == (byte)255)
                {
                    byte color = (buf).data.get(i += 1);
                    if (color == (byte)0)
                    {
                        (con).resetColor();
                        colors = false;
                    }
                    else if (color == Color.white)
                    {
                        (con).resetColor();
                        (con).setColorBright(true);
                        colors = true;
                    }
                    else
                    {
                        (con).setColor((int)color);
                        colors = true;
                    }
                }
                else
                    fputc((int)c, (con).fp());
            }
        }
        {
            if (colors)
                (con).resetColor();
        }
    }

}
