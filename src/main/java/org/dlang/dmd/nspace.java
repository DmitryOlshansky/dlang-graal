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
        public Expression identExp = null;
        // Erasure: __ctor<Loc, Identifier, Expression, Ptr>
        public  Nspace(Loc loc, Identifier ident, Expression identExp, Ptr<DArray<Dsymbol>> members) {
            super(loc, ident);
            this.members = pcopy(members);
            this.identExp = identExp;
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            Nspace ns = new Nspace(this.loc, this.ident, this.identExp, null);
            return this.syntaxCopy(ns);
        }

        // Erasure: addMember<Ptr, ScopeDsymbol>
        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sds) {
            Nspace __self = this;
            this.addMember(sc, sds);
            if (this.members != null)
            {
                if (this.symtab == null)
                {
                    this.symtab = new DsymbolTable();
                }
                {
                    Ptr<Scope> sce = sc;
                    for (; 1 != 0;sce = pcopy((sce.get()).enclosing)){
                        ScopeDsymbol sds2 = (sce.get()).scopesym;
                        if (sds2 != null)
                        {
                            sds2.importScope(this, new Prot(Prot.Kind.public_));
                            break;
                        }
                    }
                }
                assert(sc != null);
                sc = pcopy((sc.get()).push(this));
                (sc.get()).linkage = LINK.cpp;
                (sc.get()).parent.value = this;
                Runnable1<Dsymbol> __lambda3 = new Runnable1<Dsymbol>() {
                    public Void invoke(Dsymbol s) {
                     {
                        s.addMember(sc, __self);
                        return null;
                    }}

                };
                foreachDsymbol(this.members, __lambda3);
                (sc.get()).pop();
            }
        }

        // Erasure: setScope<Ptr>
        public  void setScope(Ptr<Scope> sc) {
            Nspace __self = this;
            this.setScope(sc);
            if (this.members != null)
            {
                assert(sc != null);
                sc = pcopy((sc.get()).push(this));
                (sc.get()).linkage = LINK.cpp;
                (sc.get()).parent.value = this;
                Runnable1<Dsymbol> __lambda2 = new Runnable1<Dsymbol>() {
                    public Void invoke(Dsymbol s) {
                     {
                        s.setScope(sc);
                        return null;
                    }}

                };
                foreachDsymbol(this.members, __lambda2);
                (sc.get()).pop();
            }
        }

        // Erasure: oneMember<Ptr, Identifier>
        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            return this.oneMember(ps, ident);
        }

        // Erasure: search<Loc, Identifier, int>
        public  Dsymbol search(Loc loc, Identifier ident, int flags) {
            if ((this._scope != null) && (this.symtab == null))
            {
                dsymbolSemantic(this, this._scope);
            }
            if ((this.members == null) || (this.symtab == null))
            {
                this.error(new BytePtr("is forward referenced when looking for `%s`"), ident.toChars());
                return null;
            }
            return this.search(loc, ident, flags);
        }

        // defaulted all parameters starting with #3
        public  Dsymbol search(Loc loc, Identifier ident) {
            return search(loc, ident, 8);
        }

        // Erasure: apply<Ptr, Ptr>
        public  int apply(Function2<Dsymbol,Object,Integer> fp, Object param) {
            Nspace __self = this;
            Function1<Dsymbol,Integer> __lambda3 = new Function1<Dsymbol,Integer>() {
                public Integer invoke(Dsymbol s) {
                 {
                    return (((s != null) && (s.apply(fp, param) != 0)) ? 1 : 0);
                }}

            };
            return foreachDsymbol(this.members, __lambda3);
        }

        // Erasure: hasPointers<>
        public  boolean hasPointers() {
            Nspace __self = this;
            Function1<Dsymbol,Integer> __lambda1 = new Function1<Dsymbol,Integer>() {
                public Integer invoke(Dsymbol s) {
                 {
                    return (s.hasPointers() ? 1 : 0);
                }}

            };
            return foreachDsymbol(this.members, __lambda1) != 0;
        }

        // Erasure: setFieldOffset<AggregateDeclaration, Ptr, boolean>
        public  void setFieldOffset(AggregateDeclaration ad, Ptr<Integer> poffset, boolean isunion) {
            Nspace __self = this;
            if (this._scope != null)
            {
                dsymbolSemantic(this, null);
            }
            Runnable1<Dsymbol> __lambda4 = new Runnable1<Dsymbol>() {
                public Void invoke(Dsymbol s) {
                 {
                    s.setFieldOffset(ad, poffset, isunion);
                    return null;
                }}

            };
            foreachDsymbol(this.members, __lambda4);
        }

        // Erasure: kind<>
        public  BytePtr kind() {
            return new BytePtr("namespace");
        }

        // Erasure: isNspace<>
        public  Nspace isNspace() {
            return this;
        }

        // Erasure: accept<Visitor>
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
