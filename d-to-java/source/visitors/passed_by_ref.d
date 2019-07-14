module visitors.passed_by_ref;

import ds.identity_map;

import dmd.arraytypes;
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
    scope v = new PassedByRefFields();
    foreach(m; mods) m.accept(v);
    return v.passed;
}

/// local variables
IdentityMap!bool passedByRef(FuncDeclaration func) {
    scope v = new PassedByRef();
    func.accept(v);
    return v.passed;
}

private bool allowed(Type type) {
    return type.ty != Taarray && type.ty != Tsarray && !type.isTypeFunction && !type.isConst;
}

// For a given var decl find if it's ever passed by ref or used in nested functions
private extern(C++) class PassedByRefFields : SemanticTimeTransitiveVisitor {
    int depth;
    IdentityMap!bool passed;

    alias visit = typeof(super).visit;

    override void visit(FuncDeclaration func) {
        if (func.fbody) func.fbody.accept(this);
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
        if (una.op == TOK.address && una.e1.isDotVarExp()) {
            auto var = una.e1.isDotVarExp().var;
            if (allowed(var.type))passed[var] = true;
        }
        else
            una.e1.accept(this);
    }

    override void visit(CallExp call) {
        super.visit(call);
        if (call.f && call.f.parameters) {
            foreach (i, param; (*call.f.parameters)[]) {
                bool refParam = param.isRef() || param.isOut();
                auto dotVar = (*call.arguments)[i].isDotVarExp();
                if(dotVar && allowed(dotVar.type) && dotVar.var && refParam) {
                    passed[dotVar.var] = true;
                    return;
                }
            }
        }
    }
}

private extern(C++) class PassedByRef : SemanticTimeTransitiveVisitor {
    int depth;
    IdentityMap!bool passed;

    alias visit = typeof(super).visit;

    override void visit(FuncDeclaration func) {
        depth++;
        super.visit(func);
        depth--;
    }

    override void visit(UnaExp una) {
        if(una.op == TOK.address && una.e1.isVarExp()) {
            auto var = una.e1.isVarExp().var;
            if (allowed(var.type)) passed[var] = true;
        }
        else
            una.e1.accept(this);
    }

    override void visit(BinExp bin) {
        switch(bin.op) {
            case TOK.assign, TOK.addAssign, TOK.minAssign, 
            TOK.mulAssign, TOK.divAssign, TOK.orAssign,
            TOK.andAssign, TOK.xorAssign, TOK.modAssign:
                auto var = bin.e1.isVarExp;
                if (var && allowed(var.type) && depth > 1) {
                    //stderr.writefln("Deep reference %s\n", var.var.ident.toString);
                    passed[var.var] = true;
                }
            default:
        }
        super.visit(bin);
    }

    override void visit(SymOffExp symoff) {
        super.visit(symoff);
        if (symoff.var.isVarDeclaration && !symoff.var.type.isTypeSArray) {
            if (allowed(symoff.type)) {
                passed[symoff.var.isVarDeclaration] = true;
            }
        }
    }


    override void visit(CallExp call) {
        super.visit(call);
        Parameters* params;
        if (call.f && call.f.parameters) {
            foreach (i, param; (*call.f.parameters)) {
                bool refParam = param.isRef || param.isOut;
                auto var = (*call.arguments)[i].isVarExp();
                if(var && allowed(var.type) && var.var && refParam){
                    //stderr.writefln( "IsRef = %s param #%d (%s) in %s func call for %s\n", refParam, i, var.type.toString, call.f.ident.toString, decl.ident.toString);
                    passed[var.var] = true;
                }
            }
            return;
        }
        if(call.e1.type && call.e1.type.isTypeFunction) {
            params = call.e1.type.isTypeFunction.parameterList;
        }
        else if(call.e1.type && call.e1.type.isTypeDelegate) {
            params = call.e1.type.isTypeDelegate.next.isTypeFunction.parameterList;
        }
        if (params) {
            foreach (i, param; (*params)) {
                bool refParam = (param.storageClass & (STC.ref_ | STC.out_)) != 0;
                auto var = (*call.arguments)[i].isVarExp();
                if(var && allowed(var.type) && var.var && refParam){
                    //stderr.writefln( "IsRef = %s param #%d (%s) in %s func call for %s\n", refParam, i, var.type.toString, call.f.ident.toString, decl.ident.toString);
                    passed[var.var] = true;
                }
            }
        }
    }
}