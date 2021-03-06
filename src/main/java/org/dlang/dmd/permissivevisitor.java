package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.astbase.*;
import static org.dlang.dmd.parsetimevisitor.*;

public class permissivevisitor {

    // from template PermissiveVisitor!(ASTBase)
    public static class PermissiveVisitorASTBase extends ParseTimeVisitorASTBase
    {
        // Erasure: visit<Dsymbol>
        public  void visit(ASTBase.Dsymbol _param_0) {
        }

        // Erasure: visit<Parameter>
        public  void visit(ASTBase.Parameter _param_0) {
        }

        // Erasure: visit<Statement>
        public  void visit(ASTBase.Statement _param_0) {
        }

        // Erasure: visit<Type>
        public  void visit(ASTBase.Type _param_0) {
        }

        // Erasure: visit<Expression>
        public  void visit(ASTBase.Expression _param_0) {
        }

        // Erasure: visit<TemplateParameter>
        public  void visit(ASTBase.TemplateParameter _param_0) {
        }

        // Erasure: visit<Condition>
        public  void visit(ASTBase.Condition _param_0) {
        }

        // Erasure: visit<Initializer>
        public  void visit(ASTBase.Initializer _param_0) {
        }


        public PermissiveVisitorASTBase() {}

        public PermissiveVisitorASTBase copy() {
            PermissiveVisitorASTBase that = new PermissiveVisitorASTBase();
            return that;
        }
    }

}
