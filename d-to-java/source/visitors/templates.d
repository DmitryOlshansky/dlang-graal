module visitors.templates;

import ds.buffer, ds.identity_map, ds.stack;

import dmd.visitor : SemanticTimeTransitiveVisitor;

import dmd.aggregate;
import dmd.attrib;
import dmd.cond;
import dmd.declaration;
import dmd.dclass;
import dmd.dstruct;
import dmd.dsymbol;
import dmd.dtemplate;
import dmd.statement;
import dmd.func;
import dmd.mtype;

import visitors.expression;

import std.algorithm, std.stdio;

struct Template {
    string str;
    Type[] types;
    bool empty(){ return str.length == 0; }
}


Template tiArgs(TemplateInstance ti, ExprOpts opts) {
    if (ti is null || ti.tiargs is null) return Template.init;
    else {
        Type[] types;
        auto temp = new TextBuffer();
        foreach (arg; *ti.tiargs) {
            auto t = arg.isType();
            auto e = arg.isExpression();
            if (e && e.type.toJava(opts) == "ByteSlice") temp.fmt("_%s", e.toString[1..$-1]);
            else if(e && e.type.toJava(opts) == "boolean") temp.fmt("%d", e.toInteger());
            else if (t) temp.put(t.toJava(opts, Boxing.yes));
            else if (e){
                if(auto em = e.type.isTypeEnum())
                    temp.fmt("%s", e.toInteger);
                else 
                    temp.fmt("%s", e.toString);
            }
            else
                temp.fmt("_%s", arg.toString);
            if (t) types ~= t;
        }
        return Template(temp.data.dup, types);
    }
}


// first pass to register template declarations for forward-referenced templates
extern(C++) class RegisterTemplates : SemanticTimeTransitiveVisitor {
    alias visit = typeof(super).visit;
    IdentityMap!Template templates;
    Stack!TemplateInstance inst;
    ExprOpts opts;
    int inFunc;

    Template args() {
        return inst.length ? tiArgs(inst.top, opts) : Template.init;
    }

    override void visit(ConditionalDeclaration ver) {
        if (ver.condition.inc == Include.yes) {
            if (ver.decl) {
                foreach(d; *ver.decl){
                    d.accept(this);
                }
            }
        }
        else if(ver.elsedecl) {
            foreach(d; *ver.elsedecl){
                d.accept(this);
            }
        }
    }

    override void visit(TemplateDeclaration td) {
        if (td.ident.symbol.startsWith("RTInfo")) return;
        foreach(ti; td.instances.values) {
            auto _ = pushed(inst, ti);
            ti.accept(this);
        }
    }

    override void visit(TemplateInstance ti) {
        if (inst.empty || !inst.top) return;
        if (ti.tiargs) {
            auto decl = ti.tempdecl.isTemplateDeclaration();
            foreach(m; *ti.members) {
                m.accept(this);
            }
        }
    }

    override void visit(StructDeclaration d) {
        if (!args.empty) templates[d] = args;
        auto _ = pushed(inst, null);
        super.visit(d);
    }

    override void visit(ClassDeclaration d) {
        if (!args.empty) templates[d] = args;
        auto _ = pushed(inst, null);
        super.visit(d);
    }

    override void visit(FuncDeclaration func) {
        inFunc++;
        if (!args.empty) templates[func] = args;
        super.visit(func);
        inFunc--;
    }

    override void visit(VarDeclaration var) {
        if (!args.empty) templates[var] = args;
        super.visit(var);
    }
}

IdentityMap!Template registerTemplates(Dsymbol sym) {
    scope v = new RegisterTemplates();
    sym.accept(v);
    return v.templates;
}
