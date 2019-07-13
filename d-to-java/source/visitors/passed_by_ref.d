module visitors.passed_by_ref;

import ds.identity_map;

import dmd.expression;
import dmd.declaration;
import dmd.dmodule;
import dmd.dtemplate;
import dmd.func;
import dmd.mtype;
import dmd.visitor : SemanticTimeTransitiveVisitor;
import dmd.statement;
import dmd.tokens;

import std.stdio;


extern(C) void foobar();

///
IdentityMap!bool passedByRef(Module[] mods) {
    scope v = new PassedByRef();
    foreach(m; mods) m.accept(v);
    return v.passed;
}

// For a given var decl find if it's ever passed by ref or used in nested functions
private extern(C++) class PassedByRef : SemanticTimeTransitiveVisitor {
    int depth;
    IdentityMap!bool passed;


    alias visit = typeof(super).visit;

    private bool allowed(Type type) {
        return type.ty != Taarray && type.ty != Tsarray && !type.isTypeFunction && !type.isConst;
    }

    override void visit(FuncDeclaration func) {
        depth++;
        if (func.fbody) func.fbody.accept(this);
        depth--;
    }

    override void visit(TemplateDeclaration td)
    {
        if (td.instances) {
            foreach(ti; td.instances.values) {
                ti.accept(this);
            }
        }
    }

    override void visit(TemplateInstance ti) 
    {
        if (ti.members)
            foreach(m ; *ti.members) m.accept(this);
    }

    override void visit(UnaExp una) {
        if(una.op == TOK.address && una.e1.isVarExp()) {
            auto var = una.e1.isVarExp().var;
            if (allowed(var.type)) passed[var] = true;
        }
        else if (una.op == TOK.address && una.e1.isDotVarExp()) {
            auto var = una.e1.isDotVarExp().var;
            if (allowed(var.type))passed[var] = true;
        }
        else
            una.e1.accept(this);
    }

    override void visit(SymOffExp symoff) {
        super.visit(symoff);
        if (symoff.var.isVarDeclaration) {
            if (allowed(symoff.type)) {
                passed[symoff.var.isVarDeclaration] = true;
            }
        }
    }

    override void visit(VarExp var) {
        if (allowed(var.type) && depth > 2) {
            //stderr.writefln("Deep reference %s\n", var.var.ident.toString);
            passed[var.var] = true;
        }
    }

    override void visit(CallExp call) {
        super.visit(call);
        if (call.f && call.f.parameters) {
            foreach (i, param; (*call.f.parameters)[]) {
                bool refParam = param.isRef() || param.isOut();
                auto var = (*call.arguments)[i].isVarExp();
                auto dotVar = (*call.arguments)[i].isDotVarExp();
                if(var && allowed(var.type) && var.var && refParam){
                    //stderr.writefln( "IsRef = %s param #%d (%s) in %s func call for %s\n", refParam, i, var.type.toString, call.f.ident.toString, decl.ident.toString);
                    passed[var.var] = true;
                    return;
                }
                else if(dotVar && allowed(dotVar.type) && dotVar.var && refParam) {
                    passed[dotVar.var] = true;
                    return;
                }
            }
        }
    }

}