module visitors.members;

import ds.identity_map;

import dmd.visitor : SemanticTimeTransitiveVisitor;

import dmd.aggregate;
import dmd.attrib;
import dmd.cond;
import dmd.declaration;
import dmd.dclass;
import dmd.dsymbol;
import dmd.dimport;
import dmd.dmodule;
import dmd.dstruct;
import dmd.dtemplate;
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

        override void visit(TemplateDeclaration td){   }

        override void visit(TemplateInstance ti){   }

        override void visit(AnonDeclaration un) {
            hasUnion = true;
            super.visit(un);
        }

        override void visit(UnionDeclaration un) {
            hasUnion = true;
            super.visit(un);
        }

        override void visit(StructDeclaration d) {
            aggCount++;
            super.visit(d);
            aggCount--;
        }
        
        override void visit(ClassDeclaration d) {
            aggCount++;
            super.visit(d);
            aggCount--;
            if (recursive && d.baseClass) {
                decls ~= collectMembers(d.baseClass, true).all;
            }
        }
        override void visit(FuncDeclaration ){}
        override void visit(StaticCtorDeclaration){}
        override void visit(SharedStaticCtorDeclaration){}
        override void visit(StaticAssert ) {}
        override void visit(VarDeclaration v) {
            if (aggCount <= 1 && !v.isStatic && !(v.storage_class & STC.gshared) && !v.ident.toString.startsWith("__")){
                if (!v.isThisDeclaration) decls ~= v;
            }
        }
        override void visit(ExpStatement ) {}
    }
    scope v = new Collector();
    v.recursive = recurseBase;
    agg.accept(v);
    return Members(v.hasUnion, v.decls);
}

Import[] collectImports(Dsymbol agg) {
    extern(C++) static class Collector : SemanticTimeTransitiveVisitor {
        alias visit = typeof(super).visit;
        Import[] imps = [];
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
            super.visit(un);
        }

        override void visit(UnionDeclaration un) {
            super.visit(un);
        }

        override void visit(StructDeclaration d) {
            super.visit(d);
        }
        
        override void visit(ClassDeclaration d) {
            super.visit(d);
        }
        override void visit(FuncDeclaration ){}
        override void visit(StaticCtorDeclaration){}
        override void visit(SharedStaticCtorDeclaration){}
        override void visit(StaticAssert ) {}
        override void visit(Import imp) {
            imps ~= imp;
        }
        override void visit(ExpStatement ) {}
    }
    scope v = new Collector();
    agg.accept(v);
    return v.imps;
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


bool containsStatement(Statement haystack, Statement needle) {
    extern(C++) static class Collector : SemanticTimeTransitiveVisitor {
        alias visit = typeof(super).visit;
        Statement target;
        bool found;
        
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

        override void visit(Statement st) {
            if (st == target) found = true;
            super.visit(st);
        }

        override void visit(IfStatement st) {
            if (st == target) found = true;
            super.visit(st);
        }

        override void visit(ExpStatement st) {
            if (st == target) found = true;
            super.visit(st);
        }
    }
    scope v = new Collector();
    v.target = needle;
    haystack.accept(v);
    return v.found;   
}

bool hasLocalFunctions(FuncDeclaration func) {
    extern(C++) static class Collector : SemanticTimeTransitiveVisitor {
        alias visit = typeof(super).visit;
        bool found;
        int depth;
        
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

        override void visit(FuncExp ) {
            found = true;
        }

        override void visit(FuncDeclaration func) {
            depth++;
            if (depth > 1) found = true;
            super.visit(func);
            depth--;
        }
    }
    scope v = new Collector();
    func.accept(v);
    return v.found;   
}

bool hasReturn(FuncDeclaration func) {
    extern(C++) static class Collector : SemanticTimeTransitiveVisitor {
        alias visit = typeof(super).visit;
        bool found;
        
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

        override void visit(ReturnStatement st) {
            found = true;
        }
    }
    scope v = new Collector();
    func.accept(v);
    return v.found;   
}