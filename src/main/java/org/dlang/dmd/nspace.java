package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.visitor.*;

public class nspace {

    static boolean LOG = false;
    public static class Nspace extends ScopeDsymbol
    {
        public Expression identExp;
        public  Nspace(Loc loc, Identifier ident, Expression identExp, DArray<Dsymbol> members) {
            super(loc, ident);
            this.members = members;
            this.identExp = identExp;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            Nspace ns = new Nspace(this.loc, this.ident, this.identExp, null);
            return this.syntaxCopy(ns);
        }

        public  void addMember(Scope sc, ScopeDsymbol sds) {
            this.addMember(sc, sds);
            if (this.members != null)
            {
                if (this.symtab == null)
                    this.symtab = new DsymbolTable();
                {
                    Scope sce = sc;
                    for (; 1 != 0;sce = (sce).enclosing){
                        ScopeDsymbol sds2 = (sce).scopesym;
                        if (sds2 != null)
                        {
                            sds2.importScope(this, new Prot(Prot.Kind.public_));
                            break;
                        }
                    }
                }
                assert(sc != null);
                sc = (sc).push(this);
                (sc).linkage = LINK.cpp;
                (sc).parent = this;
                Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s) {
                        s.addMember(sc, this);
                        return null;
                    }
                };
                foreachDsymbol(this.members, __lambda3);
                (sc).pop();
            }
        }

        public  void setScope(Scope sc) {
            this.setScope(sc);
            if (this.members != null)
            {
                assert(sc != null);
                sc = (sc).push(this);
                (sc).linkage = LINK.cpp;
                (sc).parent = this;
                Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s) {
                        s.setScope(sc);
                        return null;
                    }
                };
                foreachDsymbol(this.members, __lambda2);
                (sc).pop();
            }
        }

        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            return this.oneMember(ps, ident);
        }

        public  Dsymbol search(Loc loc, Identifier ident, int flags) {
            if ((this._scope != null) && (this.symtab == null))
                dsymbolSemantic(this, this._scope);
            if ((this.members == null) || (this.symtab == null))
            {
                this.error(new BytePtr("is forward referenced when looking for `%s`"), ident.toChars());
                return null;
            }
            return this.search(loc, ident, flags);
        }

        // defaulted all parameters starting with #3
        public  Dsymbol search(Loc loc, Identifier ident) {
            search(loc, ident, 8);
        }

        public  int apply(Function2<Dsymbol,Object,Integer> fp, Object param) {
            Function1<Dsymbol,Integer> __lambda3 = new Function1<Dsymbol,Integer>(){
                public Integer invoke(Dsymbol s) {
                    return (((s != null) && (s.apply(fp, param) != 0)) ? 1 : 0);
                }
            };
            return foreachDsymbol(this.members, __lambda3);
        }

        public  boolean hasPointers() {
            Function1<Dsymbol,Integer> __lambda1 = new Function1<Dsymbol,Integer>(){
                public Integer invoke(Dsymbol s) {
                    return (s.hasPointers() ? 1 : 0);
                }
            };
            return foreachDsymbol(this.members, __lambda1) != 0;
        }

        public  void setFieldOffset(AggregateDeclaration ad, IntPtr poffset, boolean isunion) {
            if (this._scope != null)
                dsymbolSemantic(this, null);
            Function1<Dsymbol,Void> __lambda4 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s) {
                    s.setFieldOffset(ad, poffset, isunion);
                    return null;
                }
            };
            foreachDsymbol(this.members, __lambda4);
        }

        public  BytePtr kind() {
            return new BytePtr("namespace");
        }

        public  Nspace isNspace() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public Nspace() {}

        public Nspace copy() {
            Nspace that = new Nspace();
            that.identExp = this.identExp;
            that.members = this.members;
            that.symtab = this.symtab;
            that.endlinnum = this.endlinnum;
            that.importedScopes = this.importedScopes;
            that.prots = this.prots;
            that.accessiblePackages = this.accessiblePackages;
            that.privateAccessiblePackages = this.privateAccessiblePackages;
            that.ident = this.ident;
            that.parent = this.parent;
            that.namespace = this.namespace;
            that.csym = this.csym;
            that.isym = this.isym;
            that.comment = this.comment;
            that.loc = this.loc;
            that._scope = this._scope;
            that.prettystring = this.prettystring;
            that.errors = this.errors;
            that.semanticRun = this.semanticRun;
            that.depdecl = this.depdecl;
            that.userAttribDecl = this.userAttribDecl;
            that.ddocUnittest = this.ddocUnittest;
            return that;
        }
    }
}
