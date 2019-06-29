module visitors.members;

import ds.identity_map;

import dmd.visitor : SemanticTimeTransitiveVisitor;

import dmd.aggregate;
import dmd.attrib;
import dmd.cond;
import dmd.declaration;
import dmd.dclass;
import dmd.dmodule;
import dmd.dstruct;
import dmd.statement;
import dmd.func;
import dmd.init;
import dmd.identifier;
import dmd.staticassert;

import std.algorithm, std.range;

struct Members {
    bool hasUnion;
    VarDeclaration[] all;
}

Members collectMembers(AggregateDeclaration agg, bool recurseBase = false) {
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
    }
    scope v = new Collector();
    v.recursive = recurseBase;
    agg.accept(v);
    return Members(v.hasUnion, v.decls);
}

VarDeclaration[] collectVars(Statement st) {
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
    IdentityMap!VarDeclaration decls;
    IdentityMap!VarDeclaration dupes;
    foreach (d; v.decls) {
        if (auto p = d.ident in decls){
            if (p.type.toString != d.type.toString)
                dupes[d.ident] = d;
        }
        else
            decls[d.ident] = d;
    }
    foreach (k; dupes.keys) {
        decls.remove(k);
    }
    return decls.values;
}