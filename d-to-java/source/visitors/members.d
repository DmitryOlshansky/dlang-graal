module visitors.members;

import ds.identity_map;

import dmd.visitor : SemanticTimeTransitiveVisitor;

import dmd.aggregate;
import dmd.attrib;
import dmd.cond;
import dmd.declaration;
import dmd.dclass;
import dmd.dsymbol;
import dmd.dmodule;
import dmd.dstruct;
import dmd.expression;
import dmd.statement;
import dmd.func;
import dmd.init;
import dmd.identifier;
import dmd.staticassert;

import std.algorithm, std.range, std.conv;

extern(C) void foobar();

struct Members {
    bool hasUnion;
    VarDeclaration[] all;
}

Members collectMembers(Dsymbol agg, bool recurseBase = false) {
    extern(C++) static class Collector : SemanticTimeTransitiveVisitor {
        alias visit = typeof(super).visit;
        VarDeclaration[] decls = [];
        bool hasUnion = false;
        bool recursive = false;
        int aggCount = 0;

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

        override void visit(AnonDeclaration un) {
            hasUnion = true;
            super.visit(un);
        }

        override void visit(UnionDeclaration un) {
            hasUnion = true;
            super.visit(un);
        }

        override void visit(StructDeclaration d) {
            if (aggCount++ == 0) super.visit(d);
        }
        
        override void visit(ClassDeclaration d) {
            if (aggCount++ == 0) super.visit(d);
            if (recursive && d.baseClass) {
                decls ~= collectMembers(d.baseClass).all;
            }
        }
        override void visit(FuncDeclaration ){}
        override void visit(StaticCtorDeclaration){}
        override void visit(SharedStaticCtorDeclaration){}
        override void visit(StaticAssert ) {}
        override void visit(VarDeclaration v) {
            if (!v.isStatic && !(v.storage_class & STC.gshared) && !v.ident.toString.startsWith("__")){
                decls ~= v;
            }
        }
        override void visit(ExpStatement ) {}
    }
    scope v = new Collector();
    v.recursive = recurseBase;
    agg.accept(v);
    return Members(v.hasUnion, v.decls);
}

void disambiguateVars(Statement st, ref IdentityMap!string renames) {
    extern(C++) static class Collector : SemanticTimeTransitiveVisitor {
        alias visit = typeof(super).visit;
        VarDeclaration[] decls = [];
        
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

        override void visit(AnonDeclaration un) { }

        override void visit(UnionDeclaration un) { }

        override void visit(StructDeclaration d) { }
        override void visit(ClassDeclaration d) {  }
        override void visit(FuncDeclaration ){}
        override void visit(StaticCtorDeclaration){}
        override void visit(SharedStaticCtorDeclaration){}
        override void visit(StaticAssert ) {}
        override void visit(VarDeclaration v) {
            if (!v.isStatic && !(v.storage_class & STC.gshared) && !v.ident.toString.startsWith("__")){
                decls ~= v;
            }
        }
    }
    scope v = new Collector();
    st.accept(v);
    auto groups = sort!((a,b) => a.ident.toString < b.ident.toString)(v.decls).groupBy;
    foreach (g; groups) {
        foreach (i, var; g.enumerate) {
            if (i) {
                if (var !in renames)
                    renames[var] = (var.ident.toString ~ "_" ~ to!string(i)).dup;
            }
        }
    }
}