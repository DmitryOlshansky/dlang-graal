module visitors.passed_as_ref;

import dmd.expression;
import dmd.declaration;
import dmd.visitor : SemanticTimeTransitiveVisitor;

// TODO: for a given var decl find if it's ever passed by ref
extern(C++) class PassedAsRef : SemanticTimeTransitiveVisitor {
    bool passed = false;
    private int inCall = 0;
    private VarDeclaration decl;

    this(VarDeclaration decl) {
        this.decl = decl;
    }

    alias visit = typeof(super).visit;

    override void visit(CallExp call) {
        inCall++;
        super.visit(call);
        inCall--;
    }

    override void visit(VarExp e) {
        if (inCall > 0 && e.var.ident == decl.ident) {
            passed = true;
        }
    }
}