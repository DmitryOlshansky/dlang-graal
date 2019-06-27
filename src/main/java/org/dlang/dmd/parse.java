package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.lexer.*;
import static org.dlang.dmd.tokens.*;

public class parse {
    private static final int[] initializer_0 = {0, 0, 0, 0, 0, 0, 0, 0, PREC.unary, 0, 0, 0, PREC.unary, PREC.primary, PREC.primary, 0, 0, PREC.primary, PREC.primary, PREC.unary, PREC.expr, 0, PREC.unary, PREC.unary, PREC.unary, PREC.primary, PREC.primary, PREC.primary, PREC.primary, PREC.primary, PREC.primary, PREC.primary, PREC.primary, 0, 0, 0, PREC.primary, PREC.primary, PREC.expr, PREC.primary, 0, PREC.primary, PREC.primary, PREC.unary, PREC.primary, PREC.unary, 0, PREC.primary, PREC.primary, PREC.primary, PREC.primary, 0, PREC.primary, PREC.primary, PREC.rel, PREC.rel, PREC.rel, PREC.rel, PREC.rel, PREC.rel, PREC.rel, PREC.rel, PREC.primary, PREC.primary, PREC.shift, PREC.shift, PREC.assign, PREC.assign, PREC.shift, PREC.assign, PREC.add, PREC.assign, PREC.assign, PREC.assign, PREC.add, PREC.add, PREC.assign, PREC.assign, PREC.mul, PREC.mul, PREC.mul, PREC.assign, PREC.assign, PREC.assign, PREC.and, PREC.or, PREC.xor, PREC.assign, PREC.assign, PREC.assign, PREC.assign, PREC.unary, PREC.unary, PREC.primary, PREC.primary, PREC.assign, PREC.assign, PREC.primary, 0, PREC.expr, PREC.cond, PREC.andand, PREC.oror, PREC.primary, PREC.primary, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, PREC.primary, PREC.primary, 0, PREC.primary, PREC.primary, PREC.primary, PREC.primary, PREC.expr, PREC.primary, 0, 0, 0, 0, 0, 0, PREC.primary, 0, 0, 0, 0, PREC.primary, 0, 0, 0, 0, 0, 0, PREC.primary, 0, 0, 0, 0, 0, 0, 0, 0, 0, PREC.primary, 0, 0, PREC.unary, PREC.primary, PREC.primary, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, PREC.rel, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, PREC.primary, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, PREC.primary, 0, 0, 0, 0, 0, 0, 0, 0, 0, PREC.primary, PREC.primary, 0, 0, 0, PREC.primary, PREC.primary, PREC.primary, PREC.primary, PREC.primary, PREC.primary, 0, 0, PREC.pow, PREC.assign, 0, PREC.unary, 0, PREC.assign, 0, 0, 0, PREC.expr, PREC.primary};

    static int CDECLSYNTAX = 0;
    static int CCASTSYNTAX = 1;
    static int CARRAYDECL = 1;
    static IntSlice precedence = slice(initializer_0);

    public static class ParseStatementFlags 
    {
        public static final int semi = 1;
        public static final int scope_ = 2;
        public static final int curly = 4;
        public static final int curlyScope = 8;
        public static final int semiOk = 16;
    }

    public static boolean writeMixin(ByteSlice s, Loc loc) {
        if (global.params.mixinOut == null)
            return false;
        OutBuffer ob = global.params.mixinOut;
        (ob).writestring( new ByteSlice("// expansion at "));
        (ob).writestring(loc.toChars(global.params.showColumns));
        (ob).writenl();
        global.params.mixinLines++;
        loc = new Loc(global.params.mixinFile, (global.params.mixinLines + 1), loc.charnum).copy();
        int lastpos = 0;
        {
            int i = 0;
            for (; i < s.getLength();i += 1){
                byte c = s.get(i);
                if ((c & 0xFF) == 10 || (c & 0xFF) == 13 && i + 1 < s.getLength() && (s.get(i + 1) & 0xFF) == 10)
                {
                    (ob).writestring(s.slice(lastpos,i));
                    (ob).writenl();
                    global.params.mixinLines++;
                    if ((c & 0xFF) == 13)
                        i += 1;
                    lastpos = i + 1;
                }
            }
        }
        if (lastpos < s.getLength())
            (ob).writestring(s.slice(lastpos,s.getLength()));
        if (s.getLength() == 0 || (s.get(s.getLength() - 1) & 0xFF) != 10)
        {
            (ob).writenl();
            global.params.mixinLines++;
        }
        (ob).writenl();
        global.params.mixinLines++;
        return true;
    }


    public static class PREC 
    {
        public static final int zero = 0;
        public static final int expr = 1;
        public static final int assign = 2;
        public static final int cond = 3;
        public static final int oror = 4;
        public static final int andand = 5;
        public static final int or = 6;
        public static final int xor = 7;
        public static final int and = 8;
        public static final int equal = 9;
        public static final int rel = 10;
        public static final int shift = 11;
        public static final int add = 12;
        public static final int mul = 13;
        public static final int pow = 14;
        public static final int unary = 15;
        public static final int primary = 16;
    }

}
