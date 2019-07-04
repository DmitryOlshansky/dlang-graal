package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.cond.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.visitor.*;

public class attrib {

    public static abstract class AttribDeclaration extends Dsymbol
    {
        public DArray<Dsymbol> decl;
        public  AttribDeclaration(DArray<Dsymbol> decl) {
            super();
            this.decl = decl;
        }

        public  AttribDeclaration(Loc loc, Identifier ident, DArray<Dsymbol> decl) {
            super(loc, ident);
            this.decl = decl;
        }

        public  DArray<Dsymbol> include(Scope sc) {
            if (this.errors)
                return null;
            return this.decl;
        }

        public  int apply(Function2<Dsymbol,Object,Integer> fp, Object param) {
            Function1<Dsymbol,Integer> __lambda3 = new Function1<Dsymbol,Integer>(){
                public Integer invoke(Dsymbol s){
                    return (((s != null && (s.apply(fp, param)) != 0)) ? 1 : 0);
                }
            };
            return foreachDsymbol(this.include(this._scope), __lambda3);
        }

        public static Scope createNewScope(Scope sc, long stc, int linkage, int cppmangle, Prot protection, int explicitProtection, AlignDeclaration aligndecl, int inlining) {
            Scope sc2 = sc;
            if (((((((stc != (sc).stc || linkage != (sc).linkage) || cppmangle != (sc).cppmangle) || !(protection.isSubsetOf((sc).protection))) || explicitProtection != (sc).explicitProtection) || aligndecl != (sc).aligndecl) || inlining != (sc).inlining))
            {
                sc2 = (sc).copy();
                (sc2).stc = stc;
                (sc2).linkage = linkage;
                (sc2).cppmangle = cppmangle;
                (sc2).protection = protection.copy();
                (sc2).explicitProtection = explicitProtection;
                (sc2).aligndecl = aligndecl;
                (sc2).inlining = inlining;
            }
            return sc2;
        }

        public  Scope newScope(Scope sc) {
            return sc;
        }

        public  void addMember(Scope sc, ScopeDsymbol sds) {
            DArray<Dsymbol> d = this.include(sc);
            if (d != null)
            {
                Scope sc2 = this.newScope(sc);
                foreachDsymbol(d, __lambda3);
                if (sc2 != sc)
                    (sc2).pop();
            }
        }

        public  void setScope(Scope sc) {
            DArray<Dsymbol> d = this.include(sc);
            Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s){
                    s.setScope(sc2);
                    return null;
                    return null;
                }
            };
            if (d != null)
            {
                Scope sc2 = this.newScope(sc);
                foreachDsymbol(d, __lambda2);
                if (sc2 != sc)
                    (sc2).pop();
            }
        }

        public  void importAll(Scope sc) {
            DArray<Dsymbol> d = this.include(sc);
            if (d != null)
            {
                Scope sc2 = this.newScope(sc);
                foreachDsymbol(d, __lambda2);
                if (sc2 != sc)
                    (sc2).pop();
            }
        }

        public  void addComment(BytePtr comment) {
            if (comment != null)
            {
                foreachDsymbol(this.include(null), __lambda2);
            }
        }

        public  BytePtr kind() {
            return new BytePtr("attribute");
        }

        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            DArray<Dsymbol> d = this.include(null);
            return Dsymbol.oneMembers(d, ps, ident);
        }

        public  void setFieldOffset(AggregateDeclaration ad, IntPtr poffset, boolean isunion) {
            Function1<Dsymbol,Void> __lambda4 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s){
                    s.setFieldOffset(ad, poffset, isunion);
                    return null;
                    return null;
                }
            };
            foreachDsymbol(this.include(null), __lambda4);
        }

        public  boolean hasPointers() {
            Function1<Dsymbol,Integer> __lambda1 = new Function1<Dsymbol,Integer>(){
                public Integer invoke(Dsymbol s){
                    return (s.hasPointers() ? 1 : 0);
                }
            };
            return foreachDsymbol(this.include(null), __lambda1) != 0;
        }

        public  boolean hasStaticCtorOrDtor() {
            return foreachDsymbol(this.include(null), __lambda1) != 0;
        }

        public  void checkCtorConstInit() {
            foreachDsymbol(this.include(null), __lambda1);
        }

        public  void addLocalClass(DArray<ClassDeclaration> aclasses) {
            foreachDsymbol(this.include(null), __lambda2);
        }

        public  void addObjcSymbols(DArray<ClassDeclaration> classes, DArray<ClassDeclaration> categories) {
            objc().addSymbols(this, classes, categories);
        }

        public  AttribDeclaration isAttribDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AttribDeclaration() {}

        public abstract AttribDeclaration copy();
    }
    public static class StorageClassDeclaration extends AttribDeclaration
    {
        public long stc;
        public  StorageClassDeclaration(long stc, DArray<Dsymbol> decl) {
            super(decl);
            this.stc = stc;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(!(s != null));
            return new StorageClassDeclaration(this.stc, Dsymbol.arraySyntaxCopy(this.decl));
        }

        public  Scope newScope(Scope sc) {
            long scstc = (sc).stc;
            if ((this.stc & 8913155L) != 0)
                scstc &= -8913156L;
            if ((this.stc & 1216872705L) != 0)
                scstc &= -1216872706L;
            if ((this.stc & 9437188L) != 0)
                scstc &= -9437189L;
            if ((this.stc & 1744830464L) != 0)
                scstc &= -1744830465L;
            if ((this.stc & 60129542144L) != 0)
                scstc &= -60129542145L;
            scstc |= this.stc;
            return AttribDeclaration.createNewScope(sc, scstc, (sc).linkage, (sc).cppmangle, (sc).protection, (sc).explicitProtection, (sc).aligndecl, (sc).inlining);
        }

        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            boolean t = Dsymbol.oneMembers(this.decl, ps, ident);
            if ((t && ps.get() != null))
            {
                FuncDeclaration fd = (ps.get()).isFuncDeclaration();
                if (fd != null)
                {
                    fd.storage_class2 |= this.stc;
                }
            }
            return t;
        }

        public  void addMember(Scope sc, ScopeDsymbol sds) {
            DArray<Dsymbol> d = this.include(sc);
            if (d != null)
            {
                Scope sc2 = this.newScope(sc);
                foreachDsymbol(d, __lambda3);
                if (sc2 != sc)
                    (sc2).pop();
            }
        }

        public  StorageClassDeclaration isStorageClassDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public StorageClassDeclaration() {}

        public StorageClassDeclaration copy() {
            StorageClassDeclaration that = new StorageClassDeclaration();
            that.stc = this.stc;
            that.decl = this.decl;
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
    public static class DeprecatedDeclaration extends StorageClassDeclaration
    {
        public Expression msg;
        public BytePtr msgstr;
        public  DeprecatedDeclaration(Expression msg, DArray<Dsymbol> decl) {
            super(1024L, decl);
            this.msg = msg;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(!(s != null));
            return new DeprecatedDeclaration(this.msg.syntaxCopy(), Dsymbol.arraySyntaxCopy(this.decl));
        }

        public  Scope newScope(Scope sc) {
            Scope scx = super.newScope(sc);
            if (scx == sc)
                scx = (sc).push();
            (scx).depdecl = this;
            return scx;
        }

        public  void setScope(Scope sc) {
            if (this.decl != null)
                this.setScope(sc);
            this.setScope(sc);
            return ;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DeprecatedDeclaration() {}

        public DeprecatedDeclaration copy() {
            DeprecatedDeclaration that = new DeprecatedDeclaration();
            that.msg = this.msg;
            that.msgstr = this.msgstr;
            that.stc = this.stc;
            that.decl = this.decl;
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
    public static class LinkDeclaration extends AttribDeclaration
    {
        public int linkage;
        public  LinkDeclaration(int linkage, DArray<Dsymbol> decl) {
            super(decl);
            this.linkage = linkage == LINK.system ? target.systemLinkage() : linkage;
        }

        public static LinkDeclaration create(int p, DArray<Dsymbol> decl) {
            return new LinkDeclaration(p, decl);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(!(s != null));
            return new LinkDeclaration(this.linkage, Dsymbol.arraySyntaxCopy(this.decl));
        }

        public  Scope newScope(Scope sc) {
            return AttribDeclaration.createNewScope(sc, (sc).stc, this.linkage, (sc).cppmangle, (sc).protection, (sc).explicitProtection, (sc).aligndecl, (sc).inlining);
        }

        public  BytePtr toChars() {
            return toBytePtr(this.asString());
        }

        public  ByteSlice asString() {
            return  new ByteSlice("extern ()");
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public LinkDeclaration() {}

        public LinkDeclaration copy() {
            LinkDeclaration that = new LinkDeclaration();
            that.linkage = this.linkage;
            that.decl = this.decl;
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
    public static class CPPMangleDeclaration extends AttribDeclaration
    {
        public int cppmangle;
        public  CPPMangleDeclaration(int cppmangle, DArray<Dsymbol> decl) {
            super(decl);
            this.cppmangle = cppmangle;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(!(s != null));
            return new CPPMangleDeclaration(this.cppmangle, Dsymbol.arraySyntaxCopy(this.decl));
        }

        public  Scope newScope(Scope sc) {
            return AttribDeclaration.createNewScope(sc, (sc).stc, LINK.cpp, this.cppmangle, (sc).protection, (sc).explicitProtection, (sc).aligndecl, (sc).inlining);
        }

        public  BytePtr toChars() {
            return toBytePtr(this.asString());
        }

        public  ByteSlice asString() {
            return  new ByteSlice("extern ()");
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CPPMangleDeclaration() {}

        public CPPMangleDeclaration copy() {
            CPPMangleDeclaration that = new CPPMangleDeclaration();
            that.cppmangle = this.cppmangle;
            that.decl = this.decl;
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
    public static class CPPNamespaceDeclaration extends AttribDeclaration
    {
        public Expression exp;
        public  CPPNamespaceDeclaration(Identifier ident, DArray<Dsymbol> decl) {
            super(decl);
            this.ident = ident;
        }

        public  CPPNamespaceDeclaration(Expression exp, DArray<Dsymbol> decl) {
            super(decl);
            this.exp = exp;
        }

        public  CPPNamespaceDeclaration(Identifier ident, Expression exp, DArray<Dsymbol> decl, CPPNamespaceDeclaration parent) {
            super(decl);
            this.ident = ident;
            this.exp = exp;
            this.namespace = parent;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(!(s != null));
            return new CPPNamespaceDeclaration(this.ident, this.exp, Dsymbol.arraySyntaxCopy(this.decl), this.namespace);
        }

        public  Scope newScope(Scope sc) {
            Scope scx = (sc).copy();
            (scx).linkage = LINK.cpp;
            (scx).namespace = this;
            return scx;
        }

        public  BytePtr toChars() {
            return toBytePtr(this.asString());
        }

        public  ByteSlice asString() {
            return  new ByteSlice("extern (C++, `namespace`)");
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }

        public  CPPNamespaceDeclaration isCPPNamespaceDeclaration() {
            return this;
        }


        public CPPNamespaceDeclaration() {}

        public CPPNamespaceDeclaration copy() {
            CPPNamespaceDeclaration that = new CPPNamespaceDeclaration();
            that.exp = this.exp;
            that.decl = this.decl;
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
    public static class ProtDeclaration extends AttribDeclaration
    {
        public Prot protection = new Prot();
        public DArray<Identifier> pkg_identifiers;
        public  ProtDeclaration(Loc loc, Prot protection, DArray<Dsymbol> decl) {
            super(loc, null, decl);
            this.protection = protection.copy();
        }

        public  ProtDeclaration(Loc loc, DArray<Identifier> pkg_identifiers, DArray<Dsymbol> decl) {
            super(loc, null, decl);
            this.protection.kind = Prot.Kind.package_;
            this.pkg_identifiers = pkg_identifiers;
            if ((pkg_identifiers != null && (pkg_identifiers).length > 0))
            {
                Ref<Dsymbol> tmp = ref(null);
                Package.resolve(pkg_identifiers, ptr(tmp), null);
                this.protection.pkg = tmp.value != null ? tmp.value.isPackage() : null;
            }
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(!(s != null));
            if (this.protection.kind == Prot.Kind.package_)
                return new ProtDeclaration(this.loc, this.pkg_identifiers, Dsymbol.arraySyntaxCopy(this.decl));
            else
                return new ProtDeclaration(this.loc, this.protection, Dsymbol.arraySyntaxCopy(this.decl));
        }

        public  Scope newScope(Scope sc) {
            if (this.pkg_identifiers != null)
                dsymbolSemantic(this, sc);
            return AttribDeclaration.createNewScope(sc, (sc).stc, (sc).linkage, (sc).cppmangle, this.protection, 1, (sc).aligndecl, (sc).inlining);
        }

        public  void addMember(Scope sc, ScopeDsymbol sds) {
            if (this.pkg_identifiers != null)
            {
                Ref<Dsymbol> tmp = ref(null);
                Package.resolve(this.pkg_identifiers, ptr(tmp), null);
                this.protection.pkg = tmp.value != null ? tmp.value.isPackage() : null;
                this.pkg_identifiers = null;
            }
            if (((this.protection.kind == Prot.Kind.package_ && this.protection.pkg != null) && (sc)._module != null))
            {
                Module m = (sc)._module;
                Package pkg = m.parent != null ? m.parent.isPackage() : null;
                if ((!(pkg != null) || !(this.protection.pkg.isAncestorPackageOf(pkg))))
                    this.error(new BytePtr("does not bind to one of ancestor packages of module `%s`"), m.toPrettyChars(true));
            }
            this.addMember(sc, sds);
            return ;
        }

        public  BytePtr kind() {
            return new BytePtr("protection attribute");
        }

        public  BytePtr toPrettyChars(boolean _param_0) {
            assert(this.protection.kind > Prot.Kind.undefined);
            OutBuffer buf = new OutBuffer();
            try {
                protectionToBuffer(buf, this.protection);
                return buf.extractChars();
            }
            finally {
            }
        }

        public  ProtDeclaration isProtDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ProtDeclaration() {}

        public ProtDeclaration copy() {
            ProtDeclaration that = new ProtDeclaration();
            that.protection = this.protection;
            that.pkg_identifiers = this.pkg_identifiers;
            that.decl = this.decl;
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
    public static class AlignDeclaration extends AttribDeclaration
    {
        public Expression ealign;
        public int UNKNOWN = 0;
        public int salign = 0;
        public  AlignDeclaration(Loc loc, Expression ealign, DArray<Dsymbol> decl) {
            super(loc, null, decl);
            this.ealign = ealign;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(!(s != null));
            return new AlignDeclaration(this.loc, this.ealign != null ? this.ealign.syntaxCopy() : null, Dsymbol.arraySyntaxCopy(this.decl));
        }

        public  Scope newScope(Scope sc) {
            return AttribDeclaration.createNewScope(sc, (sc).stc, (sc).linkage, (sc).cppmangle, (sc).protection, (sc).explicitProtection, this, (sc).inlining);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AlignDeclaration() {}

        public AlignDeclaration copy() {
            AlignDeclaration that = new AlignDeclaration();
            that.ealign = this.ealign;
            that.UNKNOWN = this.UNKNOWN;
            that.salign = this.salign;
            that.decl = this.decl;
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
    public static class AnonDeclaration extends AttribDeclaration
    {
        public boolean isunion;
        public int sem;
        public int anonoffset;
        public int anonstructsize;
        public int anonalignsize;
        public  AnonDeclaration(Loc loc, boolean isunion, DArray<Dsymbol> decl) {
            super(loc, null, decl);
            this.isunion = isunion;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(!(s != null));
            return new AnonDeclaration(this.loc, this.isunion, Dsymbol.arraySyntaxCopy(this.decl));
        }

        public  void setScope(Scope sc) {
            if (this.decl != null)
                this.setScope(sc);
            this.setScope(sc);
            return ;
        }

        public  void setFieldOffset(AggregateDeclaration ad, IntPtr poffset, boolean isunion) {
            if (this.decl != null)
            {
                int fieldstart = ad.fields.length;
                int savestructsize = ad.structsize;
                int savealignsize = ad.alignsize;
                ad.structsize = 0;
                ad.alignsize = 0;
                IntRef offset = ref(0);
                foreachDsymbol(this.decl, __lambda4);
                if (fieldstart == ad.fields.length)
                {
                    ad.structsize = savestructsize;
                    ad.alignsize = savealignsize;
                    poffset.set(0, ad.structsize);
                    return ;
                }
                this.anonstructsize = ad.structsize;
                this.anonalignsize = ad.alignsize;
                ad.structsize = savestructsize;
                ad.alignsize = savealignsize;
                if (this.anonstructsize == 0)
                {
                    this.anonstructsize = 1;
                    this.anonalignsize = 1;
                }
                assert(this._scope != null);
                int alignment = (this._scope).alignment();
                this.anonoffset = AggregateDeclaration.placeField(poffset, this.anonstructsize, this.anonalignsize, alignment, ad.structsize, ad.alignsize, isunion);
                {
                    int __key715 = fieldstart;
                    int __limit716 = ad.fields.length;
                    for (; __key715 < __limit716;__key715 += 1) {
                        int i = __key715;
                        VarDeclaration v = ad.fields.get(i);
                        v.offset += this.anonoffset;
                    }
                }
            }
        }

        public  BytePtr kind() {
            return this.isunion ? new BytePtr("anonymous union") : new BytePtr("anonymous struct");
        }

        public  AnonDeclaration isAnonDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AnonDeclaration() {}

        public AnonDeclaration copy() {
            AnonDeclaration that = new AnonDeclaration();
            that.isunion = this.isunion;
            that.sem = this.sem;
            that.anonoffset = this.anonoffset;
            that.anonstructsize = this.anonstructsize;
            that.anonalignsize = this.anonalignsize;
            that.decl = this.decl;
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
    public static class PragmaDeclaration extends AttribDeclaration
    {
        public DArray<Expression> args;
        public  PragmaDeclaration(Loc loc, Identifier ident, DArray<Expression> args, DArray<Dsymbol> decl) {
            super(loc, ident, decl);
            this.args = args;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(!(s != null));
            return new PragmaDeclaration(this.loc, this.ident, Expression.arraySyntaxCopy(this.args), Dsymbol.arraySyntaxCopy(this.decl));
        }

        public  Scope newScope(Scope sc) {
            if (pequals(this.ident, Id.Pinline))
            {
                int inlining = PINLINE.default_;
                if ((this.args == null || (this.args).length == 0))
                    inlining = PINLINE.default_;
                else if ((this.args).length != 1)
                {
                    this.error(new BytePtr("one boolean expression expected for `pragma(inline)`, not %d"), (this.args).length);
                    (this.args).setDim(1);
                    this.args.set(0, new ErrorExp());
                }
                else
                {
                    Expression e = (this.args).get(0);
                    if (((e.op & 0xFF) != 135 || !(e.type.equals(Type.tbool))))
                    {
                        if ((e.op & 0xFF) != 127)
                        {
                            this.error(new BytePtr("pragma(`inline`, `true` or `false`) expected, not `%s`"), e.toChars());
                            this.args.set(0, new ErrorExp());
                        }
                    }
                    else if (e.isBool(true))
                        inlining = PINLINE.always;
                    else if (e.isBool(false))
                        inlining = PINLINE.never;
                }
                return AttribDeclaration.createNewScope(sc, (sc).stc, (sc).linkage, (sc).cppmangle, (sc).protection, (sc).explicitProtection, (sc).aligndecl, inlining);
            }
            return sc;
        }

        public  BytePtr kind() {
            return new BytePtr("pragma");
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public PragmaDeclaration() {}

        public PragmaDeclaration copy() {
            PragmaDeclaration that = new PragmaDeclaration();
            that.args = this.args;
            that.decl = this.decl;
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
    public static class ConditionalDeclaration extends AttribDeclaration
    {
        public Condition condition;
        public DArray<Dsymbol> elsedecl;
        public  ConditionalDeclaration(Condition condition, DArray<Dsymbol> decl, DArray<Dsymbol> elsedecl) {
            super(decl);
            this.condition = condition;
            this.elsedecl = elsedecl;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(!(s != null));
            return new ConditionalDeclaration(this.condition.syntaxCopy(), Dsymbol.arraySyntaxCopy(this.decl), Dsymbol.arraySyntaxCopy(this.elsedecl));
        }

        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            if (this.condition.inc != Include.notComputed)
            {
                DArray<Dsymbol> d = (this.condition.include(null)) != 0 ? this.decl : this.elsedecl;
                return Dsymbol.oneMembers(d, ps, ident);
            }
            else
            {
                boolean res = (((Dsymbol.oneMembers(this.decl, ps, ident) && ps.get() == null) && Dsymbol.oneMembers(this.elsedecl, ps, ident)) && ps.get() == null);
                ps.set(0, null);
                return res;
            }
        }

        public  DArray<Dsymbol> include(Scope sc) {
            if (this.errors)
                return null;
            assert(this.condition != null);
            return (this.condition.include(this._scope != null ? this._scope : sc)) != 0 ? this.decl : this.elsedecl;
        }

        public  void addComment(BytePtr comment) {
            if (comment != null)
            {
                foreachDsymbol(this.decl, __lambda2);
                foreachDsymbol(this.elsedecl, __lambda3);
            }
        }

        public  void setScope(Scope sc) {
            foreachDsymbol(this.include(sc), __lambda2);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ConditionalDeclaration() {}

        public ConditionalDeclaration copy() {
            ConditionalDeclaration that = new ConditionalDeclaration();
            that.condition = this.condition;
            that.elsedecl = this.elsedecl;
            that.decl = this.decl;
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
    public static class StaticIfDeclaration extends ConditionalDeclaration
    {
        public ScopeDsymbol scopesym;
        public boolean addisdone = false;
        public boolean onStack = false;
        public  StaticIfDeclaration(Condition condition, DArray<Dsymbol> decl, DArray<Dsymbol> elsedecl) {
            super(condition, decl, elsedecl);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(!(s != null));
            return new StaticIfDeclaration(this.condition.syntaxCopy(), Dsymbol.arraySyntaxCopy(this.decl), Dsymbol.arraySyntaxCopy(this.elsedecl));
        }

        public  DArray<Dsymbol> include(Scope sc) {
            if ((this.errors || this.onStack))
                return null;
            this.onStack = true;
            try {
                if ((sc != null && this.condition.inc == Include.notComputed))
                {
                    assert(this.scopesym != null);
                    assert(this._scope != null);
                    DArray<Dsymbol> d = this.include(this._scope);
                    if ((d != null && !(this.addisdone)))
                    {
                        foreachDsymbol(d, __lambda2);
                        foreachDsymbol(d, __lambda3);
                        this.addisdone = true;
                    }
                    return d;
                }
                else
                {
                    return this.include(sc);
                }
            }
            finally {
                this.onStack = false;
            }
        }

        public  void addMember(Scope sc, ScopeDsymbol sds) {
            this.scopesym = sds;
        }

        public  void setScope(Scope sc) {
            this.setScope(sc);
        }

        public  void importAll(Scope sc) {
        }

        public  BytePtr kind() {
            return new BytePtr("static if");
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public StaticIfDeclaration() {}

        public StaticIfDeclaration copy() {
            StaticIfDeclaration that = new StaticIfDeclaration();
            that.scopesym = this.scopesym;
            that.addisdone = this.addisdone;
            that.onStack = this.onStack;
            that.condition = this.condition;
            that.elsedecl = this.elsedecl;
            that.decl = this.decl;
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
    public static class StaticForeachDeclaration extends AttribDeclaration
    {
        public StaticForeach sfe;
        public ScopeDsymbol scopesym;
        public boolean onStack = false;
        public boolean cached = false;
        public DArray<Dsymbol> cache = null;
        public  StaticForeachDeclaration(StaticForeach sfe, DArray<Dsymbol> decl) {
            super(decl);
            this.sfe = sfe;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(!(s != null));
            return new StaticForeachDeclaration(this.sfe.syntaxCopy(), Dsymbol.arraySyntaxCopy(this.decl));
        }

        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            if (this.cached)
            {
                return super.oneMember(ps, ident);
            }
            ps.set(0, null);
            return false;
        }

        public  DArray<Dsymbol> include(Scope sc) {
            if ((this.errors || this.onStack))
                return null;
            if (this.cached)
            {
                assert(!(this.onStack));
                return this.cache;
            }
            this.onStack = true;
            try {
                if (this._scope != null)
                {
                    this.sfe.prepare(this._scope);
                }
                if (!(this.sfe.ready()))
                {
                    return null;
                }
                DArray<Dsymbol> d = makeTupleForeach(this._scope, this.sfe.aggrfe, this.decl, this.sfe.needExpansion);
                if (d != null)
                {
                    foreachDsymbol(d, __lambda2);
                    foreachDsymbol(d, __lambda3);
                }
                this.cached = true;
                this.cache = d;
                return d;
            }
            finally {
                this.onStack = false;
            }
        }

        public  void addMember(Scope sc, ScopeDsymbol sds) {
            this.scopesym = sds;
        }

        public  void addComment(BytePtr comment) {
        }

        public  void setScope(Scope sc) {
            this.setScope(sc);
        }

        public  void importAll(Scope sc) {
        }

        public  BytePtr kind() {
            return new BytePtr("static foreach");
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public StaticForeachDeclaration() {}

        public StaticForeachDeclaration copy() {
            StaticForeachDeclaration that = new StaticForeachDeclaration();
            that.sfe = this.sfe;
            that.scopesym = this.scopesym;
            that.onStack = this.onStack;
            that.cached = this.cached;
            that.cache = this.cache;
            that.decl = this.decl;
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
    public static class ForwardingAttribDeclaration extends AttribDeclaration
    {
        public ForwardingScopeDsymbol sym = null;
        public  ForwardingAttribDeclaration(DArray<Dsymbol> decl) {
            super(decl);
            this.sym = new ForwardingScopeDsymbol(null);
            this.sym.symtab = new DsymbolTable();
        }

        public  Scope newScope(Scope sc) {
            return (sc).push(this.sym);
        }

        public  void addMember(Scope sc, ScopeDsymbol sds) {
            this.parent = (this.sym.parent = (this.sym.forward = sds));
            super.addMember(sc, this.sym);
            return ;
        }

        public  ForwardingAttribDeclaration isForwardingAttribDeclaration() {
            return this;
        }


        public ForwardingAttribDeclaration() {}

        public ForwardingAttribDeclaration copy() {
            ForwardingAttribDeclaration that = new ForwardingAttribDeclaration();
            that.sym = this.sym;
            that.decl = this.decl;
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
    public static class CompileDeclaration extends AttribDeclaration
    {
        public DArray<Expression> exps;
        public ScopeDsymbol scopesym;
        public boolean compiled;
        public  CompileDeclaration(Loc loc, DArray<Expression> exps) {
            super(loc, null, null);
            this.exps = exps;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            return new CompileDeclaration(this.loc, Expression.arraySyntaxCopy(this.exps));
        }

        public  void addMember(Scope sc, ScopeDsymbol sds) {
            this.scopesym = sds;
        }

        public  void setScope(Scope sc) {
            this.setScope(sc);
        }

        public  BytePtr kind() {
            return new BytePtr("mixin");
        }

        public  CompileDeclaration isCompileDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CompileDeclaration() {}

        public CompileDeclaration copy() {
            CompileDeclaration that = new CompileDeclaration();
            that.exps = this.exps;
            that.scopesym = this.scopesym;
            that.compiled = this.compiled;
            that.decl = this.decl;
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
    public static class UserAttributeDeclaration extends AttribDeclaration
    {
        public DArray<Expression> atts;
        public  UserAttributeDeclaration(DArray<Expression> atts, DArray<Dsymbol> decl) {
            super(decl);
            this.atts = atts;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(!(s != null));
            return new UserAttributeDeclaration(Expression.arraySyntaxCopy(this.atts), Dsymbol.arraySyntaxCopy(this.decl));
        }

        public  Scope newScope(Scope sc) {
            Scope sc2 = sc;
            if ((this.atts != null && ((this.atts).length) != 0))
            {
                sc2 = (sc).copy();
                (sc2).userAttribDecl = this;
            }
            return sc2;
        }

        public  void setScope(Scope sc) {
            if (this.decl != null)
                this.setScope(sc);
            this.setScope(sc);
            return ;
        }

        public static DArray<Expression> concat(DArray<Expression> udas1, DArray<Expression> udas2) {
            DArray<Expression> udas = null;
            if ((udas1 == null || (udas1).length == 0))
                udas = udas2;
            else if ((udas2 == null || (udas2).length == 0))
                udas = udas1;
            else
            {
                udas = new DArray<Expression>(2);
                udas.set(0, new TupleExp(Loc.initial, udas1));
                udas.set(1, new TupleExp(Loc.initial, udas2));
            }
            return udas;
        }

        public  DArray<Expression> getAttributes() {
            {
                Scope sc = this._scope;
                if (sc != null)
                {
                    this._scope = null;
                    arrayExpressionSemantic(this.atts, sc, false);
                }
            }
            DArray<Expression> exps = new DArray<Expression>();
            if (this.userAttribDecl != null)
                (exps).push(new TupleExp(Loc.initial, this.userAttribDecl.getAttributes()));
            if ((this.atts != null && ((this.atts).length) != 0))
                (exps).push(new TupleExp(Loc.initial, this.atts));
            return exps;
        }

        public  BytePtr kind() {
            return new BytePtr("UserAttribute");
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public UserAttributeDeclaration() {}

        public UserAttributeDeclaration copy() {
            UserAttributeDeclaration that = new UserAttributeDeclaration();
            that.atts = this.atts;
            that.decl = this.decl;
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
