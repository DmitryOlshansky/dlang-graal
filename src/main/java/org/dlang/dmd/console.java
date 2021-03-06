package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;

public class console {


    public static class Color 
    {
        public static final int black = 0;
        public static final int red = 1;
        public static final int green = 2;
        public static final int blue = 4;
        public static final int yellow = 3;
        public static final int magenta = 5;
        public static final int cyan = 6;
        public static final int lightGray = 7;
        public static final int bright = 8;
        public static final int darkGray = 8;
        public static final int brightRed = 9;
        public static final int brightGreen = 10;
        public static final int brightBlue = 12;
        public static final int brightYellow = 11;
        public static final int brightMagenta = 13;
        public static final int brightCyan = 14;
        public static final int white = 15;
    }

    public static class Console
    {

        public Ptr<_IO_FILE> _fp = null;
        // Erasure: fp<>
        public  Ptr<_IO_FILE> fp() {
            return this._fp;
        }

        // Erasure: detectTerminal<>
        public static boolean detectTerminal() {
            BytePtr term = pcopy(getenv(new BytePtr("TERM")));
            return (isatty(2) != 0) && (term != null) && (term.get(0) != 0) && (strcmp(term, new BytePtr("dumb")) != 0);
        }

        // Erasure: create<Ptr>
        public static Ptr<Console> create(Ptr<_IO_FILE> fp) {
            Ptr<Console> c = refPtr(new Console(null));
            (c.get())._fp = pcopy(fp);
            return c;
        }

        // Erasure: setColorBright<boolean>
        public  void setColorBright(boolean bright) {
            fprintf(this._fp, new BytePtr("\u001b[%dm"), (bright ? 1 : 0));
        }

        // Erasure: setColor<int>
        public  void setColor(int color) {
            fprintf(this._fp, new BytePtr("\u001b[%d;%dm"), (color & Color.bright) != 0 ? 1 : 0, 30 + (color & -9));
        }

        // Erasure: resetColor<>
        public  void resetColor() {
            fputs(new BytePtr("\u001b[m"), this._fp);
        }

        public Console(){ }
        public Console copy(){
            Console r = new Console();
            r._fp = _fp;
            return r;
        }
        public Console(Ptr<_IO_FILE> _fp) {
            this._fp = _fp;
        }

        public Console opAssign(Console that) {
            this._fp = that._fp;
            return this;
        }
    }
}
