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

import std.algorithm, std.conv, std.string, std.digest.sha;

struct Template {
    string str;
    Type[] types;
    bool empty(){ return str.length == 0; }
}


Template tiArgs(TemplateInstance ti, ExprOpts opts) {
    string subst(const(char)[] tiargs) {
        return tiargs.map!((c){
            switch(c) {
                case '!': return "not";
                case '~': return "invert";
                case '>': return "r";
                case '<': return "l";
                case '+': return "plus";
                case '-': return "minus";
                case '*': return "mul";
                case '/': return "div";
                case '%': return "mod";
                case 'a': .. case 'z':
                case 'A': .. case 'Z':
                case '0': .. case '9':
                case '_':
                    return c.to!string;
                default:
                    return "_";
            }
        }).join("").idup;
    }
    if (ti is null || ti.tiargs is null) return Template.init;
    else {
        Type[] types;
        auto temp = new TextBuffer();
        foreach (arg; *ti.tiargs) {
            auto t = arg.isType();
            auto e = arg.isExpression();
            if (e && e.type.toJava(opts) == "ByteSlice") temp.fmt("_%s", subst(e.toString[1..$-1]));
            else if(e && e.type.toJava(opts) == "boolean") temp.fmt("%d", e.toInteger());
            else if (t) temp.put(t.toJava(opts, Boxing.yes));
            else if (e) {
                if(auto em = e.type.isTypeEnum())
                    temp.fmt("%s", e.toInteger);
                else 
                    temp.fmt("_%s", sha1Of(e.toString)[0..8].toHexString);
            }
            else
                temp.fmt("_%s", sha1Of(arg.toString)[0..8].toHexString);
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
        auto v = args;
        if (!v.empty) templates[d] = v;
        auto _ = pushed(inst, null);
        super.visit(d);
    }

    override void visit(ClassDeclaration d) {
        auto v = args;
        if (!v.empty) templates[d] = v;
        auto _ = pushed(inst, null);
        super.visit(d);
    }

    override void visit(FuncDeclaration func) {
        inFunc++;
        auto v = args;
        if (!v.empty) templates[func] = v;
        super.visit(func);
        inFunc--;
    }

    override void visit(VarDeclaration var) {
        auto v = args;
        if (!v.empty) templates[var] = v;
        super.visit(var);
    }

}

IdentityMap!Template registerTemplates(Dsymbol sym) {
    scope v = new RegisterTemplates();
    sym.accept(v);
    return v.templates;
}
