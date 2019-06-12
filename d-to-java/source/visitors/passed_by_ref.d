module visitors.passed_by_ref;

import dmd.expression;
import dmd.declaration;
import dmd.func;
import dmd.mtype;
import dmd.visitor : SemanticTimeTransitiveVisitor;
import dmd.statement;

import std.stdio;

///
bool passedByRef(VarDeclaration var, FuncDeclaration func) {
    scope v = new PassedByRef(var);
    if (func.fbody) func.fbody.accept(v);
    return v.passed;
}

// For a given var decl find if it's ever passed by ref
private extern(C++) class PassedByRef : SemanticTimeTransitiveVisitor {
    bool passed;
    private VarDeclaration decl;

    this(VarDeclaration decl) {
        this.decl = decl;
        passed = false;
    }

    alias visit = typeof(super).visit;

    override void visit(CallExp call) {
        if (call.f && call.f.parameters) {
            foreach (i, param; (*call.f.parameters)[]) {
                bool refParam = param.isRef() || param.isOut();
                auto  var = (*call.arguments)[i].isVarExp();
                if(var && var.type.ty != Tstruct && var.var.ident == decl.ident && refParam){
                    passed = true;
                    return;
                }
            }
        }
    }

}