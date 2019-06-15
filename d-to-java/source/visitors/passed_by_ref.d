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

// For a given var decl find if it's ever passed by ref or used in nested functions
private extern(C++) class PassedByRef : SemanticTimeTransitiveVisitor {
    bool passed;
    int depth;
    private VarDeclaration decl;

    this(VarDeclaration decl) {
        this.decl = decl;
        passed = false;
    }

    alias visit = typeof(super).visit;

    private bool allowed(Type type) {
        return type.ty != Tpointer && type.ty != Taarray && type.ty != Tstruct && type.ty != Tclass;
    }

    override void visit(FuncDeclaration func) {
        depth++;
        super.visit(func);
        depth--;
    }

    override void visit(VarExp var) {
        if (var.var is decl && allowed(var.type) && depth == 1) {
            stderr.writefln("Deep reference %s\n", var.var.ident.toString);
            passed = true;
        }
    }

    override void visit(CallExp call) {
        if (call.f && call.f.parameters) {
            foreach (i, param; (*call.f.parameters)[]) {
                bool refParam = param.isRef() || param.isOut();
                auto  var = (*call.arguments)[i].isVarExp();
                if(var && allowed(var.type) && var.var is decl && refParam){
                    stderr.writefln( "IsRef = %s param #%d (%s) in %s func call for %s\n", refParam, i, var.type.toString, call.f.ident.toString, decl.ident.toString);
                    passed = true;
                    return;
                }
            }
        }
    }

}