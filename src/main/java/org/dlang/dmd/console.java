package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;

public class console {


    public enum Color 
    {
        black(0),
        red(1),
        green(2),
        blue(4),
        yellow(3),
        magenta(5),
        cyan(6),
        lightGray(7),
        bright(8),
        darkGray(8),
        brightRed(9),
        brightGreen(10),
        brightBlue(12),
        brightYellow(11),
        brightMagenta(13),
        brightCyan(14),
        white(15),
        ;
        public int value;
        Color(int value){ this.value = value; }
    }

    public static class Console
    {

        public _IO_FILE _fp;
        public  _IO_FILE fp() {
            return this._fp;
        }

        public static boolean detectTerminal() {
            BytePtr term = getenv( new ByteSlice("TERM"));
            return (isatty(2)) != 0 && term != null && (term.get(0)) != 0 && strcmp(term,  new ByteSlice("dumb")) != 0;
        }

        public static Console create(_IO_FILE fp) {
            Console c = Console(null);
            (c)._fp = fp;
            return c;
        }

        public  void setColorBright(boolean bright) {
            fprintf(this._fp,  new ByteSlice("\u001b[%dm"), (bright ? 1 : 0));
        }

        public  void setColor(Color color) {
            fprintf(this._fp,  new ByteSlice("\u001b[%d;%dm"), (color & Color.bright) != 0 ? 1 : 0, 30 + (color & -9));
        }

        public  void resetColor() {
            fputs( new ByteSlice("\u001b[m"), this._fp);
        }

        public Console(){}
        public Console(_IO_FILE _fp) {
            this._fp = _fp;
        }

        public Console opAssign(Console that) {
            this._fp = that._fp;
            return this;
        }
    }
}
