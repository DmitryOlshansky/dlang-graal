package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;

public class astcodegen {

    public static class ASTCodegen
    {
        public ASTCodegen(){
        }
        public ASTCodegen copy(){
            ASTCodegen r = new ASTCodegen();
            return r;
        }
        public ASTCodegen opAssign(ASTCodegen that) {
            return this;
        }
    }
}
