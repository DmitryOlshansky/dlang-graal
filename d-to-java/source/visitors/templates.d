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

import visitors.expression;

import std.algorithm, std.stdio;

struct Template {
    string tiArgs;
    bool local; // = true for nested function
}


string tiArgs(TemplateInstance ti, ExprOpts opts) {
    if (ti is null || ti.tiargs is null) return "";
    else {
        auto temp = new TextBuffer();
        foreach (arg; *ti.tiargs) {
            auto t = arg.isType();
            auto e = arg.isExpression();
            if (e && e.type.toJava(opts) == "ByteSlice") temp.fmt("_%s", e.toString[1..$-1]);
            else if(e && e.type.toJava(opts) == "boolean") temp.fmt("%d", e.toInteger());
            else if (t) temp.put(t.toJava(opts, Boxing.yes));
            else temp.fmt("_%s", arg.toString);
        }
        return temp.data.dup;
    }
}


// first pass to register template declarations for forward-referenced templates
extern(C++) class RegisterTemplates : SemanticTimeTransitiveVisitor {
    alias visit = typeof(super).visit;
    IdentityMap!Template templates;
    Stack!TemplateInstance inst;
    ExprOpts opts;
    int inFunc;

    string args() {
        return inst.length ? tiArgs(inst.top, opts) : "";
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
        if (args.length) templates[d] = Template(args, false);
        auto _ = pushed(inst, null);
        super.visit(d);
    }

    override void visit(ClassDeclaration d) {
        if (args.length) templates[d] = Template(args, false);
        auto _ = pushed(inst, null);
        super.visit(d);
    }

    override void visit(FuncDeclaration func) {
        inFunc++;
        if (args.length) templates[func] = Template(args, false);
        super.visit(func);
        inFunc--;
    }

    override void visit(VarDeclaration var) {
        if (args.length && inFunc == 0) templates[var] = Template(args, false);
        super.visit(var);
    }
}

IdentityMap!Template registerTemplates(Dsymbol sym, ExprOpts opts) {
    scope v = new RegisterTemplates();
    v.opts = opts;
    sym.accept(v);
    return v.templates;
}