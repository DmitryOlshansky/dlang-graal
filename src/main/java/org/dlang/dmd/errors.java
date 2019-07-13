package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.console.*;
import static org.dlang.dmd.filecache.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.lexer.*;
import static org.dlang.dmd.tokens.*;

public class errors {
    static BytePtr vdeprecationheader = new BytePtr("Deprecation: ");
    static int colorHighlightCodenested = 0;

    public static abstract class DiagnosticReporter extends Object
    {
        public abstract int errorCount();


        public abstract int warningCount();


        public abstract int deprecationCount();


        public  void error(Loc loc, BytePtr format, Object... args) {
            this.error(loc, format, new RawSlice<>(args));
        }

        public abstract void error(Loc loc, BytePtr format, Ptr<Slice<Object>> args);


        public  void errorSupplemental(Loc loc, BytePtr format, Object... args) {
            this.errorSupplemental(loc, format, new RawSlice<>(args));
        }

        public abstract void errorSupplemental(Loc loc, BytePtr format, Ptr<Slice<Object>> arg2);


        public  void warning(Loc loc, BytePtr format, Object... args) {
            this.warning(loc, format, new RawSlice<>(args));
        }

        public abstract void warning(Loc loc, BytePtr format, Ptr<Slice<Object>> args);


        public  void warningSupplemental(Loc loc, BytePtr format, Object... args) {
            this.warningSupplemental(loc, format, new RawSlice<>(args));
        }

        public abstract void warningSupplemental(Loc loc, BytePtr format, Ptr<Slice<Object>> arg2);


        public  void deprecation(Loc loc, BytePtr format, Object... args) {
            this.deprecation(loc, format, new RawSlice<>(args));
        }

        public abstract void deprecation(Loc loc, BytePtr format, Ptr<Slice<Object>> args);


        public  void deprecationSupplemental(Loc loc, BytePtr format, Object... args) {
            this.deprecationSupplemental(loc, format, new RawSlice<>(args));
        }

        public abstract void deprecationSupplemental(Loc loc, BytePtr format, Ptr<Slice<Object>> arg2);



        public DiagnosticReporter() {}

        public abstract DiagnosticReporter copy();
    }
    public static class StderrDiagnosticReporter extends DiagnosticReporter
    {
        public byte useDeprecated = 0;
        public int errorCount_ = 0;
        public int warningCount_ = 0;
        public int deprecationCount_ = 0;
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

        public  void error(Loc loc, BytePtr format, Ptr<Slice<Object>> args) {
            verror(loc, format, args, null, null, new BytePtr("Error: "));
            this.errorCount_++;
        }

        public  void errorSupplemental(Loc loc, BytePtr format, Ptr<Slice<Object>> args) {
            verrorSupplemental(loc, format, args);
        }

        public  void warning(Loc loc, BytePtr format, Ptr<Slice<Object>> args) {
            vwarning(loc, format, args);
            this.warningCount_++;
        }

        public  void warningSupplemental(Loc loc, BytePtr format, Ptr<Slice<Object>> args) {
            vwarningSupplemental(loc, format, args);
        }

        public  void deprecation(Loc loc, BytePtr format, Ptr<Slice<Object>> args) {
            vdeprecation(loc, format, args, null, null);
            if (((this.useDeprecated & 0xFF) == 0))
            {
                this.errorCount_++;
            }
            else
            {
                this.deprecationCount_++;
            }
        }

        public  void deprecationSupplemental(Loc loc, BytePtr format, Ptr<Slice<Object>> args) {
            vdeprecationSupplemental(loc, format, args);
        }


        public StderrDiagnosticReporter() {}

        public StderrDiagnosticReporter copy() {
            StderrDiagnosticReporter that = new StderrDiagnosticReporter();
            that.useDeprecated = this.useDeprecated;
            that.errorCount_ = this.errorCount_;
            that.warningCount_ = this.warningCount_;
            that.deprecationCount_ = this.deprecationCount_;
            return that;
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
        verror(loc, format, new RawSlice<>(ap), null, null, new BytePtr("Error: "));
    }

    // removed duplicate function, [["void errorLoc, BytePtr", "int isattyint"]] signature: void errorLoc, BytePtr
    public static void error(BytePtr filename, int linnum, int charnum, BytePtr format, Object... ap) {
        Loc loc = loc = new Loc(filename, linnum, charnum);
        verror(loc, format, new RawSlice<>(ap), null, null, new BytePtr("Error: "));
    }

    public static void errorSupplemental(Loc loc, BytePtr format, Object... ap) {
        verrorSupplemental(loc, format, new RawSlice<>(ap));
    }

    public static void warning(Loc loc, BytePtr format, Object... ap) {
        vwarning(loc, format, new RawSlice<>(ap));
    }

    public static void warningSupplemental(Loc loc, BytePtr format, Object... ap) {
        vwarningSupplemental(loc, format, new RawSlice<>(ap));
    }

    public static void deprecation(Loc loc, BytePtr format, Object... ap) {
        vdeprecation(loc, format, new RawSlice<>(ap), null, null);
    }

    public static void deprecationSupplemental(Loc loc, BytePtr format, Object... ap) {
        vdeprecationSupplemental(loc, format, new RawSlice<>(ap));
    }

    public static void message(Loc loc, BytePtr format, Object... ap) {
        vmessage(loc, format, new RawSlice<>(ap));
    }

    public static void message(BytePtr format, Object... ap) {
        vmessage(Loc.initial.value, format, new RawSlice<>(ap));
    }

    public static void verrorPrint(Loc loc, int headerColor, BytePtr header, BytePtr format, Ptr<Slice<Object>> ap, BytePtr p1, BytePtr p2) {
        Ptr<Console> con = ((Ptr<Console>)global.console);
        BytePtr p = pcopy(loc.toChars(global.params.showColumns.value));
        if (con != null)
        {
            (con.get()).setColorBright(true);
        }
        if (p.get() != 0)
        {
            fprintf(stderr, new BytePtr("%s: "), p);
            Mem.xfree(p);
        }
        if (con != null)
        {
            (con.get()).setColor(headerColor);
        }
        fputs(header, stderr);
        if (con != null)
        {
            (con.get()).resetColor();
        }
        if (p1 != null)
        {
            fprintf(stderr, new BytePtr("%s "), p1);
        }
        if (p2 != null)
        {
            fprintf(stderr, new BytePtr("%s "), p2);
        }
        Ref<OutBuffer> tmp = ref(new OutBuffer());
        tmp.value.vprintf(format, ap);
        if ((con != null) && (strchr(tmp.value.peekChars(), 96) != null))
        {
            colorSyntaxHighlight(ptr(tmp));
            writeHighlights(con, ptr(tmp));
        }
        else
        {
            fputs(tmp.value.peekChars(), stderr);
        }
        fputc(10, stderr);
        if (global.params.printErrorContext && !loc.opEquals(Loc.initial.value) && (strstr(loc.filename, new BytePtr(".d-mixin-")) == null) && (global.params.mixinOut == null))
        {
            FileAndLines fllines = FileCache.fileCache.addOrGetFile(loc.filename.slice(0,strlen(loc.filename)));
            if ((loc.linnum - 1 < fllines.lines.value.getLength()))
            {
                ByteSlice line = fllines.lines.value.get(loc.linnum - 1).copy();
                if ((loc.charnum < line.getLength()))
                {
                    fprintf(stderr, new BytePtr("%.*s\n"), line.getLength(), toBytePtr(line));
                    {
                        int __key104 = 1;
                        int __limit105 = loc.charnum;
                        for (; (__key104 < __limit105);__key104 += 1) {
                            int __ = __key104;
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

    // defaulted all parameters starting with #7
    public static void verrorPrint(Loc loc, int headerColor, BytePtr header, BytePtr format, Ptr<Slice<Object>> ap, BytePtr p1) {
        return verrorPrint(loc, headerColor, header, format, ap, p1, null);
    }

    // defaulted all parameters starting with #6
    public static void verrorPrint(Loc loc, int headerColor, BytePtr header, BytePtr format, Ptr<Slice<Object>> ap) {
        return verrorPrint(loc, headerColor, header, format, ap, null, null);
    }

    public static void verror(Loc loc, BytePtr format, Ptr<Slice<Object>> ap, BytePtr p1, BytePtr p2, BytePtr header) {
        global.errors.value++;
        if (global.gag.value == 0)
        {
            verrorPrint(loc, Color.brightRed, header, format, ap, p1, p2);
            if ((global.params.errorLimit != 0) && (global.errors.value >= global.params.errorLimit))
            {
                fatal();
            }
        }
        else
        {
            if (global.params.showGaggedErrors)
            {
                fprintf(stderr, new BytePtr("(spec:%d) "), global.gag.value);
                verrorPrint(loc, Color.brightBlue, header, format, ap, p1, p2);
            }
            global.gaggedErrors++;
        }
    }

    // defaulted all parameters starting with #6
    public static void verror(Loc loc, BytePtr format, Ptr<Slice<Object>> ap, BytePtr p1, BytePtr p2) {
        return verror(loc, format, ap, p1, p2, new BytePtr("Error: "));
    }

    // defaulted all parameters starting with #5
    public static void verror(Loc loc, BytePtr format, Ptr<Slice<Object>> ap, BytePtr p1) {
        return verror(loc, format, ap, p1, null, new BytePtr("Error: "));
    }

    // defaulted all parameters starting with #4
    public static void verror(Loc loc, BytePtr format, Ptr<Slice<Object>> ap) {
        return verror(loc, format, ap, null, null, new BytePtr("Error: "));
    }

    public static void verrorSupplemental(Loc loc, BytePtr format, Ptr<Slice<Object>> ap) {
        int color = Color.black;
        if (global.gag.value != 0)
        {
            if (!global.params.showGaggedErrors)
            {
                return ;
            }
            color = Color.brightBlue;
        }
        else
        {
            color = Color.brightRed;
        }
        verrorPrint(loc, color, new BytePtr("       "), format, ap, null, null);
    }

    public static void vwarning(Loc loc, BytePtr format, Ptr<Slice<Object>> ap) {
        if (((global.params.warnings & 0xFF) != 2))
        {
            if (global.gag.value == 0)
            {
                verrorPrint(loc, Color.brightYellow, new BytePtr("Warning: "), format, ap, null, null);
                if (((global.params.warnings & 0xFF) == 0))
                {
                    global.warnings++;
                }
            }
            else
            {
                global.gaggedWarnings++;
            }
        }
    }

    public static void vwarningSupplemental(Loc loc, BytePtr format, Ptr<Slice<Object>> ap) {
        if (((global.params.warnings & 0xFF) != 2) && (global.gag.value == 0))
        {
            verrorPrint(loc, Color.brightYellow, new BytePtr("       "), format, ap, null, null);
        }
    }

    public static void vdeprecation(Loc loc, BytePtr format, Ptr<Slice<Object>> ap, BytePtr p1, BytePtr p2) {
        if (((global.params.useDeprecated & 0xFF) == 0))
        {
            verror(loc, format, ap, p1, p2, errors.vdeprecationheader);
        }
        else if (((global.params.useDeprecated & 0xFF) == 1))
        {
            if (global.gag.value == 0)
            {
                verrorPrint(loc, Color.brightCyan, errors.vdeprecationheader, format, ap, p1, p2);
            }
            else
            {
                global.gaggedWarnings++;
            }
        }
    }

    // defaulted all parameters starting with #5
    public static void vdeprecation(Loc loc, BytePtr format, Ptr<Slice<Object>> ap, BytePtr p1) {
        return vdeprecation(loc, format, ap, p1, null);
    }

    // defaulted all parameters starting with #4
    public static void vdeprecation(Loc loc, BytePtr format, Ptr<Slice<Object>> ap) {
        return vdeprecation(loc, format, ap, null, null);
    }

    public static void vmessage(Loc loc, BytePtr format, Ptr<Slice<Object>> ap) {
        BytePtr p = pcopy(loc.toChars(global.params.showColumns.value));
        if (p.get() != 0)
        {
            fprintf(stdout, new BytePtr("%s: "), p);
            Mem.xfree(p);
        }
        OutBuffer tmp = new OutBuffer();
        tmp.vprintf(format, ap);
        fputs(tmp.peekChars(), stdout);
        fputc(10, stdout);
        fflush(stdout);
    }

    public static void vdeprecationSupplemental(Loc loc, BytePtr format, Ptr<Slice<Object>> ap) {
        if (((global.params.useDeprecated & 0xFF) == 0))
        {
            verrorSupplemental(loc, format, ap);
        }
        else if (((global.params.useDeprecated & 0xFF) == 1) && (global.gag.value == 0))
        {
            verrorPrint(loc, Color.brightCyan, new BytePtr("       "), format, ap, null, null);
        }
    }

    public static void fatal() {
        exit(1);
    }

    public static void halt() {
        throw new AssertionError("Unreachable code!");
    }

    public static void colorSyntaxHighlight(Ptr<OutBuffer> buf) {
        boolean inBacktick = false;
        int iCodeStart = 0;
        int offset = 0;
        {
            int i = offset;
            for (; (i < (buf.get()).offset.value);i += 1){
                byte c = (byte)(buf.get()).data.value.get(i);
                switch ((c & 0xFF))
                {
                    case 96:
                        if (inBacktick)
                        {
                            inBacktick = false;
                            Ref<OutBuffer> codebuf = ref(new OutBuffer());
                            try {
                                codebuf.value.write((toBytePtr((buf.get()).peekSlice()).plus(iCodeStart).plus(1)), i - (iCodeStart + 1));
                                codebuf.value.writeByte(0);
                                colorHighlightCode(ptr(codebuf));
                                (buf.get()).remove(iCodeStart, i - iCodeStart + 1);
                                ByteSlice pre = new ByteSlice("").copy();
                                i = (buf.get()).insert(iCodeStart, toByteSlice(pre));
                                i = (buf.get()).insert(i, codebuf.value.peekSlice());
                                i--;
                                break;
                            }
                            finally {
                            }
                        }
                        inBacktick = true;
                        iCodeStart = i;
                        break;
                    default:
                    break;
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

    public static void colorHighlightCode(Ptr<OutBuffer> buf) {
        if (errors.colorHighlightCodenested != 0)
        {
            errors.colorHighlightCodenested -= 1;
            return ;
        }
        errors.colorHighlightCodenested += 1;
        int gaggedErrorsSave = global.startGagging();
        StderrDiagnosticReporter diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
        Lexer lex = new Lexer(null, toBytePtr(buf.get().data), 0, (buf.get()).offset.value - 1, false, true, diagnosticReporter);
        Ref<OutBuffer> res = ref(new OutBuffer());
        BytePtr lastp = pcopy(toBytePtr(buf.get().data));
        res.value.reserve((buf.get()).offset.value);
        res.value.writeByte(255);
        res.value.writeByte(6);
        for (; 1 != 0;){
            Ref<Token> tok = ref(new Token().copy());
            lex.scan(ptr(tok));
            res.value.writestring(lastp.slice(0,((tok.value.ptr.minus(lastp)))));
            byte highlight = HIGHLIGHT.Default;
            switch ((tok.value.value & 0xFF))
            {
                case 120:
                    highlight = HIGHLIGHT.Identifier;
                    break;
                case 46:
                    highlight = HIGHLIGHT.Comment;
                    break;
                case 105:
                case 106:
                case 107:
                case 108:
                case 109:
                case 110:
                case 111:
                case 112:
                case 113:
                case 114:
                case 115:
                case 116:
                case 117:
                case 118:
                case 119:
                case 121:
                    highlight = HIGHLIGHT.Identifier;
                    break;
                default:
                if (tok.value.isKeyword() != 0)
                {
                    highlight = HIGHLIGHT.Identifier;
                }
                break;
            }
            if (((highlight & 0xFF) != 0))
            {
                res.value.writeByte(255);
                res.value.writeByte((highlight & 0xFF));
                res.value.writestring(tok.value.ptr.slice(0,((lex.p.value.minus(tok.value.ptr)))));
                res.value.writeByte(255);
                res.value.writeByte(6);
            }
            else
            {
                res.value.writestring(tok.value.ptr.slice(0,((lex.p.value.minus(tok.value.ptr)))));
            }
            if (((tok.value.value & 0xFF) == 11))
            {
                break;
            }
            lastp = pcopy(lex.p.value);
        }
        res.value.writeByte(255);
        res.value.writeByte(0);
        (buf.get()).setsize(0);
        (buf.get()).write(ptr(res));
        global.endGagging(gaggedErrorsSave);
        errors.colorHighlightCodenested -= 1;
    }

    public static void writeHighlights(Ptr<Console> con, Ptr<OutBuffer> buf) {
        boolean colors = false;
        {
            int i = 0;
            for (; (i < (buf.get()).offset.value);i += 1){
                byte c = (buf.get()).data.value.get(i);
                if (((c & 0xFF) == 255))
                {
                    byte color = (buf.get()).data.value.get(i += 1);
                    if (((color & 0xFF) == 0))
                    {
                        (con.get()).resetColor();
                        colors = false;
                    }
                    else if (((color & 0xFF) == Color.white))
                    {
                        (con.get()).resetColor();
                        (con.get()).setColorBright(true);
                        colors = true;
                    }
                    else
                    {
                        (con.get()).setColor((int)color);
                        colors = true;
                    }
                }
                else
                {
                    fputc((c & 0xFF), (con.get()).fp());
                }
            }
        }
        {
            if (colors)
            {
                (con.get()).resetColor();
            }
        }
    }

}
