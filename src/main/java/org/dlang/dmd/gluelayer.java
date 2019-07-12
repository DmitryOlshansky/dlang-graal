package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.lib.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.statement.*;

public class gluelayer {


    public static class Symbol;
    public static class code;
    public static class block;
    public static class Blockx;
    public static class elem;
    public static class TYPE;
    public static void obj_write_deferred(Library library) {
    }

    public static void obj_start(BytePtr srcfile) {
    }

    public static void obj_end(Library library, BytePtr objfilename) {
    }

    public static void genObjFile(dmodule.Module m, boolean multiobj) {
    }

    public static void backend_init() {
    }

    public static void backend_term() {
    }

    public static Statement asmSemantic(AsmStatement s, Ptr<Scope> sc) {
        (sc.get()).func.hasReturnExp = 8;
        return null;
    }

    public static void toObjFile(Dsymbol ds, boolean multiobj) {
    }

    public static abstract class ObjcGlue
    {
        public static void initialize() {
        }


        public ObjcGlue() {}

        public abstract ObjcGlue copy();
    }
}
