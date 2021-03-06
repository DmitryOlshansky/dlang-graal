package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.visitor.*;

public class ast_node {

    public static abstract class ASTNode extends RootObject
    {
        // Erasure: accept<>
        public abstract void accept(Visitor v);


        // Erasure: __ctor<>
        public  ASTNode() {
            super();
        }


        public abstract ASTNode copy();
    }
}
