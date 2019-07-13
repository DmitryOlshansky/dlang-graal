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
import static org.dlang.dmd.cond.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.hdrgen.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.objc.*;
import static org.dlang.dmd.statementsem.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.visitor.*;

public class attrib {

    public static abstract class AttribDeclaration extends Dsymbol
    {
        public Ref<Ptr<DArray<Dsymbol>>> decl = ref(null);
        public  AttribDeclaration(Ptr<DArray<Dsymbol>> decl) {
            super();
            this.decl.value = decl;
        }

        public  AttribDeclaration(Loc loc, Identifier ident, Ptr<DArray<Dsymbol>> decl) {
            super(loc, ident);
            this.decl.value = decl;
        }

        public  Ptr<DArray<Dsymbol>> include(Ptr<Scope> sc) {
            if (this.errors.value)
            {
                return null;
            }
            return this.decl.value;
        }

        public  int apply(Function2<Dsymbol,Object,Integer> fp, Object param) {
            Function1<Dsymbol,Integer> __lambda3 = new Function1<Dsymbol,Integer>(){
                public Integer invoke(Dsymbol s) {
                    return (((s != null) && (s.apply(fp, param) != 0)) ? 1 : 0);
                }
            };
            return foreachDsymbol(this.include(this._scope.value), __lambda3);
        }

        public static Ptr<Scope> createNewScope(Ptr<Scope> sc, long stc, int linkage, int cppmangle, Prot protection, int explicitProtection, AlignDeclaration aligndecl, int inlining) {
            Ptr<Scope> sc2 = sc;
            if ((stc != (sc.get()).stc.value) || (linkage != (sc.get()).linkage.value) || (cppmangle != (sc.get()).cppmangle.value) || !protection.isSubsetOf((sc.get()).protection.value) || (explicitProtection != (sc.get()).explicitProtection) || (aligndecl != (sc.get()).aligndecl) || (inlining != (sc.get()).inlining))
            {
                sc2 = (sc.get()).copy();
                (sc2.get()).stc.value = stc;
                (sc2.get()).linkage.value = linkage;
                (sc2.get()).cppmangle.value = cppmangle;
                (sc2.get()).protection.value = protection.copy();
                (sc2.get()).explicitProtection = explicitProtection;
                (sc2.get()).aligndecl = aligndecl;
                (sc2.get()).inlining = inlining;
            }
            return sc2;
        }

        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            return sc;
        }

        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sds) {
            Ptr<DArray<Dsymbol>> d = this.include(sc);
            if (d != null)
            {
                Ptr<Scope> sc2 = this.newScope(sc);
                Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s) {
                        s.addMember(sc2, sds);
                        return null;
                    }
                };
                foreachDsymbol(d, __lambda3);
                if ((sc2 != sc))
                {
                    (sc2.get()).pop();
                }
            }
        }

        public  void setScope(Ptr<Scope> sc) {
            Ptr<DArray<Dsymbol>> d = this.include(sc);
            if (d != null)
            {
                Ptr<Scope> sc2 = this.newScope(sc);
                Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s) {
                        s.setScope(sc2);
                        return null;
                    }
                };
                foreachDsymbol(d, __lambda2);
                if ((sc2 != sc))
                {
                    (sc2.get()).pop();
                }
            }
        }

        public  void importAll(Ptr<Scope> sc) {
            Ptr<DArray<Dsymbol>> d = this.include(sc);
            if (d != null)
            {
                Ptr<Scope> sc2 = this.newScope(sc);
                Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s) {
                        s.importAll(sc2);
                        return null;
                    }
                };
                foreachDsymbol(d, __lambda2);
                if ((sc2 != sc))
                {
                    (sc2.get()).pop();
                }
            }
        }

        public  void addComment(BytePtr comment) {
            if (comment != null)
            {
                Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s) {
                        s.addComment(comment);
                        return null;
                    }
                };
                foreachDsymbol(this.include(null), __lambda2);
            }
        }

        public  BytePtr kind() {
            return new BytePtr("attribute");
        }

        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            Ptr<DArray<Dsymbol>> d = this.include(null);
            return Dsymbol.oneMembers(d, ps, ident);
        }

        public  void setFieldOffset(AggregateDeclaration ad, IntPtr poffset, boolean isunion) {
            Function1<Dsymbol,Void> __lambda4 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s) {
                    s.setFieldOffset(ad, poffset, isunion);
                    return null;
                }
            };
            foreachDsymbol(this.include(null), __lambda4);
        }

        public  boolean hasPointers() {
            Function1<Dsymbol,Integer> __lambda1 = new Function1<Dsymbol,Integer>(){
                public Integer invoke(Dsymbol s) {
                    return (s.hasPointers() ? 1 : 0);
                }
            };
            return foreachDsymbol(this.include(null), __lambda1) != 0;
        }

        public  boolean hasStaticCtorOrDtor() {
            Function1<Dsymbol,Integer> __lambda1 = new Function1<Dsymbol,Integer>(){
                public Integer invoke(Dsymbol s) {
                    return (s.hasStaticCtorOrDtor() ? 1 : 0);
                }
            };
            return foreachDsymbol(this.include(null), __lambda1) != 0;
        }

        public  void checkCtorConstInit() {
            Function1<Dsymbol,Void> __lambda1 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s) {
                    s.checkCtorConstInit();
                    return null;
                }
            };
            foreachDsymbol(this.include(null), __lambda1);
        }

        public  void addLocalClass(Ptr<DArray<ClassDeclaration>> aclasses) {
            Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s) {
                    s.addLocalClass(aclasses);
                    return null;
                }
            };
            foreachDsymbol(this.include(null), __lambda2);
        }

        public  void addObjcSymbols(Ptr<DArray<ClassDeclaration>> classes, Ptr<DArray<ClassDeclaration>> categories) {
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
        public long stc = 0L;
        public  StorageClassDeclaration(long stc, Ptr<DArray<Dsymbol>> decl) {
            super(decl);
            this.stc = stc;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new StorageClassDeclaration(this.stc, Dsymbol.arraySyntaxCopy(this.decl.value));
        }

        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            long scstc = (sc.get()).stc.value;
            if ((this.stc & 8913155L) != 0)
            {
                scstc &= -8913156L;
            }
            if ((this.stc & 1216872705L) != 0)
            {
                scstc &= -1216872706L;
            }
            if ((this.stc & 9437188L) != 0)
            {
                scstc &= -9437189L;
            }
            if ((this.stc & 1744830464L) != 0)
            {
                scstc &= -1744830465L;
            }
            if ((this.stc & 60129542144L) != 0)
            {
                scstc &= -60129542145L;
            }
            scstc |= this.stc;
            return AttribDeclaration.createNewScope(sc, scstc, (sc.get()).linkage.value, (sc.get()).cppmangle.value, (sc.get()).protection.value, (sc.get()).explicitProtection, (sc.get()).aligndecl, (sc.get()).inlining);
        }

        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            boolean t = Dsymbol.oneMembers(this.decl.value, ps, ident);
            if (t && (ps.get() != null))
            {
                FuncDeclaration fd = (ps.get()).isFuncDeclaration();
                if (fd != null)
                {
                    fd.storage_class2 |= this.stc;
                }
            }
            return t;
        }

        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sds) {
            Ptr<DArray<Dsymbol>> d = this.include(sc);
            if (d != null)
            {
                Ptr<Scope> sc2 = this.newScope(sc);
                Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s) {
                        {
                            Declaration decl = s.isDeclaration();
                            if ((decl) != null)
                            {
                                decl.storage_class.value |= stc & 2251799813685248L;
                                {
                                    StorageClassDeclaration sdecl = s.isStorageClassDeclaration();
                                    if ((sdecl) != null)
                                    {
                                        sdecl.stc |= stc & 2251799813685248L;
                                    }
                                }
                            }
                        }
                        s.addMember(sc2, sds);
                        return null;
                    }
                };
                foreachDsymbol(d, __lambda3);
                if ((sc2 != sc))
                {
                    (sc2.get()).pop();
                }
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
        public Expression msg = null;
        public BytePtr msgstr = null;
        public  DeprecatedDeclaration(Expression msg, Ptr<DArray<Dsymbol>> decl) {
            super(1024L, decl);
            this.msg = msg;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new DeprecatedDeclaration(this.msg.syntaxCopy(), Dsymbol.arraySyntaxCopy(this.decl.value));
        }

        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            Ptr<Scope> scx = super.newScope(sc);
            if ((scx == sc))
            {
                scx = (sc.get()).push();
            }
            (scx.get()).depdecl = this;
            return scx;
        }

        public  void setScope(Ptr<Scope> sc) {
            if (this.decl.value != null)
            {
                this.setScope(sc);
            }
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
        public int linkage = 0;
        public  LinkDeclaration(int linkage, Ptr<DArray<Dsymbol>> decl) {
            super(decl);
            this.linkage = (linkage == LINK.system) ? target.systemLinkage() : linkage;
        }

        public static LinkDeclaration create(int p, Ptr<DArray<Dsymbol>> decl) {
            return new LinkDeclaration(p, decl);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new LinkDeclaration(this.linkage, Dsymbol.arraySyntaxCopy(this.decl.value));
        }

        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            return AttribDeclaration.createNewScope(sc, (sc.get()).stc.value, this.linkage, (sc.get()).cppmangle.value, (sc.get()).protection.value, (sc.get()).explicitProtection, (sc.get()).aligndecl, (sc.get()).inlining);
        }

        public  BytePtr toChars() {
            return toBytePtr(this.asString());
        }

        public  ByteSlice asString() {
            return new ByteSlice("extern ()");
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
        public int cppmangle = 0;
        public  CPPMangleDeclaration(int cppmangle, Ptr<DArray<Dsymbol>> decl) {
            super(decl);
            this.cppmangle = cppmangle;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new CPPMangleDeclaration(this.cppmangle, Dsymbol.arraySyntaxCopy(this.decl.value));
        }

        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            return AttribDeclaration.createNewScope(sc, (sc.get()).stc.value, LINK.cpp, this.cppmangle, (sc.get()).protection.value, (sc.get()).explicitProtection, (sc.get()).aligndecl, (sc.get()).inlining);
        }

        public  BytePtr toChars() {
            return toBytePtr(this.asString());
        }

        public  ByteSlice asString() {
            return new ByteSlice("extern ()");
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
        public Expression exp = null;
        public  CPPNamespaceDeclaration(Identifier ident, Ptr<DArray<Dsymbol>> decl) {
            super(decl);
            this.ident.value = ident;
        }

        public  CPPNamespaceDeclaration(Expression exp, Ptr<DArray<Dsymbol>> decl) {
            super(decl);
            this.exp = exp;
        }

        public  CPPNamespaceDeclaration(Identifier ident, Expression exp, Ptr<DArray<Dsymbol>> decl, CPPNamespaceDeclaration parent) {
            super(decl);
            this.ident.value = ident;
            this.exp = exp;
            this.namespace = parent;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new CPPNamespaceDeclaration(this.ident.value, this.exp, Dsymbol.arraySyntaxCopy(this.decl.value), this.namespace);
        }

        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            Ptr<Scope> scx = (sc.get()).copy();
            (scx.get()).linkage.value = LINK.cpp;
            (scx.get()).namespace = this;
            return scx;
        }

        public  BytePtr toChars() {
            return toBytePtr(this.asString());
        }

        public  ByteSlice asString() {
            return new ByteSlice("extern (C++, `namespace`)");
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
        public Ref<Prot> protection = ref(new Prot());
        public Ptr<DArray<Identifier>> pkg_identifiers = null;
        public  ProtDeclaration(Loc loc, Prot protection, Ptr<DArray<Dsymbol>> decl) {
            super(loc, null, decl);
            this.protection.value = protection.copy();
        }

        public  ProtDeclaration(Loc loc, Ptr<DArray<Identifier>> pkg_identifiers, Ptr<DArray<Dsymbol>> decl) {
            super(loc, null, decl);
            this.protection.value.kind.value = Prot.Kind.package_;
            this.pkg_identifiers = pkg_identifiers;
            if ((pkg_identifiers != null) && ((pkg_identifiers.get()).length.value > 0))
            {
                Ref<Dsymbol> tmp = ref(null);
                dmodule.Package.resolve(pkg_identifiers, ptr(tmp), null);
                this.protection.value.pkg = tmp.value != null ? tmp.value.isPackage() : null;
            }
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            if ((this.protection.value.kind.value == Prot.Kind.package_))
            {
                return new ProtDeclaration(this.loc.value, this.pkg_identifiers, Dsymbol.arraySyntaxCopy(this.decl.value));
            }
            else
            {
                return new ProtDeclaration(this.loc.value, this.protection.value, Dsymbol.arraySyntaxCopy(this.decl.value));
            }
        }

        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            if (this.pkg_identifiers != null)
            {
                dsymbolSemantic(this, sc);
            }
            return AttribDeclaration.createNewScope(sc, (sc.get()).stc.value, (sc.get()).linkage.value, (sc.get()).cppmangle.value, this.protection.value, 1, (sc.get()).aligndecl, (sc.get()).inlining);
        }

        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sds) {
            if (this.pkg_identifiers != null)
            {
                Ref<Dsymbol> tmp = ref(null);
                dmodule.Package.resolve(this.pkg_identifiers, ptr(tmp), null);
                this.protection.value.pkg = tmp.value != null ? tmp.value.isPackage() : null;
                this.pkg_identifiers = null;
            }
            if ((this.protection.value.kind.value == Prot.Kind.package_) && (this.protection.value.pkg != null) && ((sc.get())._module.value != null))
            {
                dmodule.Module m = (sc.get())._module.value;
                dmodule.Package pkg = m.parent.value != null ? m.parent.value.isPackage() : null;
                if ((pkg == null) || !this.protection.value.pkg.isAncestorPackageOf(pkg))
                {
                    this.error(new BytePtr("does not bind to one of ancestor packages of module `%s`"), m.toPrettyChars(true));
                }
            }
            this.addMember(sc, sds);
            return ;
        }

        public  BytePtr kind() {
            return new BytePtr("protection attribute");
        }

        public  BytePtr toPrettyChars(boolean _param_0) {
            assert((this.protection.value.kind.value > Prot.Kind.undefined));
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                protectionToBuffer(ptr(buf), this.protection.value);
                return buf.value.extractChars();
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
        public Expression ealign = null;
        public int UNKNOWN = 0;
        public int salign = 0;
        public  AlignDeclaration(Loc loc, Expression ealign, Ptr<DArray<Dsymbol>> decl) {
            super(loc, null, decl);
            this.ealign = ealign;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new AlignDeclaration(this.loc.value, this.ealign != null ? this.ealign.syntaxCopy() : null, Dsymbol.arraySyntaxCopy(this.decl.value));
        }

        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            return AttribDeclaration.createNewScope(sc, (sc.get()).stc.value, (sc.get()).linkage.value, (sc.get()).cppmangle.value, (sc.get()).protection.value, (sc.get()).explicitProtection, this, (sc.get()).inlining);
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
        public boolean isunion = false;
        public int sem = 0;
        public int anonoffset = 0;
        public int anonstructsize = 0;
        public int anonalignsize = 0;
        public  AnonDeclaration(Loc loc, boolean isunion, Ptr<DArray<Dsymbol>> decl) {
            super(loc, null, decl);
            this.isunion = isunion;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new AnonDeclaration(this.loc.value, this.isunion, Dsymbol.arraySyntaxCopy(this.decl.value));
        }

        public  void setScope(Ptr<Scope> sc) {
            if (this.decl.value != null)
            {
                this.setScope(sc);
            }
            this.setScope(sc);
            return ;
        }

        public  void setFieldOffset(AggregateDeclaration ad, IntPtr poffset, boolean isunion) {
            if (this.decl.value != null)
            {
                int fieldstart = ad.fields.length.value;
                int savestructsize = ad.structsize.value;
                int savealignsize = ad.alignsize.value;
                ad.structsize.value = 0;
                ad.alignsize.value = 0;
                IntRef offset = ref(0);
                Function1<Dsymbol,Void> __lambda4 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s) {
                        s.setFieldOffset(ad, ptr(offset), isunion);
                        if (isunion)
                        {
                            offset.value = 0;
                        }
                        return null;
                    }
                };
                foreachDsymbol(this.decl.value, __lambda4);
                if ((fieldstart == ad.fields.length.value))
                {
                    ad.structsize.value = savestructsize;
                    ad.alignsize.value = savealignsize;
                    poffset.set(0, ad.structsize.value);
                    return ;
                }
                this.anonstructsize = ad.structsize.value;
                this.anonalignsize = ad.alignsize.value;
                ad.structsize.value = savestructsize;
                ad.alignsize.value = savealignsize;
                if ((this.anonstructsize == 0))
                {
                    this.anonstructsize = 1;
                    this.anonalignsize = 1;
                }
                assert(this._scope.value != null);
                int alignment = (this._scope.value.get()).alignment();
                this.anonoffset = AggregateDeclaration.placeField(poffset, this.anonstructsize, this.anonalignsize, alignment, ptr(ad.structsize), ptr(ad.alignsize), isunion);
                {
                    int __key795 = fieldstart;
                    int __limit796 = ad.fields.length.value;
                    for (; (__key795 < __limit796);__key795 += 1) {
                        int i = __key795;
                        VarDeclaration v = ad.fields.get(i);
                        v.offset.value += this.anonoffset;
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
        public Ptr<DArray<Expression>> args = null;
        public  PragmaDeclaration(Loc loc, Identifier ident, Ptr<DArray<Expression>> args, Ptr<DArray<Dsymbol>> decl) {
            super(loc, ident, decl);
            this.args = args;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new PragmaDeclaration(this.loc.value, this.ident.value, Expression.arraySyntaxCopy(this.args), Dsymbol.arraySyntaxCopy(this.decl.value));
        }

        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            if ((pequals(this.ident.value, Id.Pinline)))
            {
                int inlining = PINLINE.default_;
                if ((this.args == null) || ((this.args.get()).length.value == 0))
                {
                    inlining = PINLINE.default_;
                }
                else if (((this.args.get()).length.value != 1))
                {
                    this.error(new BytePtr("one boolean expression expected for `pragma(inline)`, not %d"), (this.args.get()).length.value);
                    (this.args.get()).setDim(1);
                    this.args.get().set(0, new ErrorExp());
                }
                else
                {
                    Expression e = (this.args.get()).get(0);
                    if (((e.op.value & 0xFF) != 135) || !e.type.value.equals(Type.tbool.value))
                    {
                        if (((e.op.value & 0xFF) != 127))
                        {
                            this.error(new BytePtr("pragma(`inline`, `true` or `false`) expected, not `%s`"), e.toChars());
                            this.args.get().set(0, new ErrorExp());
                        }
                    }
                    else if (e.isBool(true))
                    {
                        inlining = PINLINE.always;
                    }
                    else if (e.isBool(false))
                    {
                        inlining = PINLINE.never;
                    }
                }
                return AttribDeclaration.createNewScope(sc, (sc.get()).stc.value, (sc.get()).linkage.value, (sc.get()).cppmangle.value, (sc.get()).protection.value, (sc.get()).explicitProtection, (sc.get()).aligndecl, inlining);
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
        public Condition condition = null;
        public Ref<Ptr<DArray<Dsymbol>>> elsedecl = ref(null);
        public  ConditionalDeclaration(Condition condition, Ptr<DArray<Dsymbol>> decl, Ptr<DArray<Dsymbol>> elsedecl) {
            super(decl);
            this.condition = condition;
            this.elsedecl.value = elsedecl;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new ConditionalDeclaration(this.condition.syntaxCopy(), Dsymbol.arraySyntaxCopy(this.decl.value), Dsymbol.arraySyntaxCopy(this.elsedecl.value));
        }

        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            if ((this.condition.inc.value != Include.notComputed))
            {
                Ptr<DArray<Dsymbol>> d = this.condition.include(null) != 0 ? this.decl.value : this.elsedecl.value;
                return Dsymbol.oneMembers(d, ps, ident);
            }
            else
            {
                boolean res = Dsymbol.oneMembers(this.decl.value, ps, ident) && (ps.get() == null) && Dsymbol.oneMembers(this.elsedecl.value, ps, ident) && (ps.get() == null);
                ps.set(0, null);
                return res;
            }
        }

        public  Ptr<DArray<Dsymbol>> include(Ptr<Scope> sc) {
            if (this.errors.value)
            {
                return null;
            }
            assert(this.condition != null);
            return this.condition.include(this._scope.value != null ? this._scope.value : sc) != 0 ? this.decl.value : this.elsedecl.value;
        }

        public  void addComment(BytePtr comment) {
            if (comment != null)
            {
                Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s) {
                        s.addComment(comment);
                        return null;
                    }
                };
                foreachDsymbol(this.decl.value, __lambda2);
                Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>(){
                    public Void invoke(Dsymbol s) {
                        s.addComment(comment);
                        return null;
                    }
                };
                foreachDsymbol(this.elsedecl.value, __lambda3);
            }
        }

        public  void setScope(Ptr<Scope> sc) {
            Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s) {
                    s.setScope(sc);
                    return null;
                }
            };
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
        public ScopeDsymbol scopesym = null;
        public boolean addisdone = false;
        public boolean onStack = false;
        public  StaticIfDeclaration(Condition condition, Ptr<DArray<Dsymbol>> decl, Ptr<DArray<Dsymbol>> elsedecl) {
            super(condition, decl, elsedecl);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new StaticIfDeclaration(this.condition.syntaxCopy(), Dsymbol.arraySyntaxCopy(this.decl.value), Dsymbol.arraySyntaxCopy(this.elsedecl.value));
        }

        public  Ptr<DArray<Dsymbol>> include(Ptr<Scope> sc) {
            if (this.errors.value || this.onStack)
            {
                return null;
            }
            this.onStack = true;
            try {
                if ((sc != null) && (this.condition.inc.value == Include.notComputed))
                {
                    assert(this.scopesym != null);
                    assert(this._scope.value != null);
                    Ptr<DArray<Dsymbol>> d = this.include(this._scope.value);
                    if ((d != null) && !this.addisdone)
                    {
                        Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>(){
                            public Void invoke(Dsymbol s) {
                                s.addMember(_scope.value, scopesym);
                                return null;
                            }
                        };
                        foreachDsymbol(d, __lambda2);
                        Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>(){
                            public Void invoke(Dsymbol s) {
                                s.setScope(_scope.value);
                                return null;
                            }
                        };
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

        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sds) {
            this.scopesym = sds;
        }

        public  void setScope(Ptr<Scope> sc) {
            this.setScope(sc);
        }

        public  void importAll(Ptr<Scope> sc) {
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
        public Ref<StaticForeach> sfe = ref(null);
        public ScopeDsymbol scopesym = null;
        public boolean onStack = false;
        public boolean cached = false;
        public Ptr<DArray<Dsymbol>> cache = null;
        public  StaticForeachDeclaration(StaticForeach sfe, Ptr<DArray<Dsymbol>> decl) {
            super(decl);
            this.sfe.value = sfe;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new StaticForeachDeclaration(this.sfe.value.syntaxCopy(), Dsymbol.arraySyntaxCopy(this.decl.value));
        }

        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            if (this.cached)
            {
                return super.oneMember(ps, ident);
            }
            ps.set(0, null);
            return false;
        }

        public  Ptr<DArray<Dsymbol>> include(Ptr<Scope> sc) {
            if (this.errors.value || this.onStack)
            {
                return null;
            }
            if (this.cached)
            {
                assert(!this.onStack);
                return this.cache;
            }
            this.onStack = true;
            try {
                if (this._scope.value != null)
                {
                    this.sfe.value.prepare(this._scope.value);
                }
                if (!this.sfe.value.ready())
                {
                    return null;
                }
                Ptr<DArray<Dsymbol>> d = makeTupleForeach11(this._scope.value, this.sfe.value.aggrfe.value, this.decl.value, this.sfe.value.needExpansion);
                if (d != null)
                {
                    Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>(){
                        public Void invoke(Dsymbol s) {
                            s.addMember(_scope.value, scopesym);
                            return null;
                        }
                    };
                    foreachDsymbol(d, __lambda2);
                    Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>(){
                        public Void invoke(Dsymbol s) {
                            s.setScope(_scope.value);
                            return null;
                        }
                    };
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

        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sds) {
            this.scopesym = sds;
        }

        public  void addComment(BytePtr comment) {
        }

        public  void setScope(Ptr<Scope> sc) {
            this.setScope(sc);
        }

        public  void importAll(Ptr<Scope> sc) {
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
        public  ForwardingAttribDeclaration(Ptr<DArray<Dsymbol>> decl) {
            super(decl);
            this.sym = new ForwardingScopeDsymbol(null);
            this.sym.symtab = new DsymbolTable();
        }

        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            return (sc.get()).push(this.sym);
        }

        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sds) {
            this.parent.value = (this.sym.parent.value = (this.sym.forward = sds));
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
        public Ptr<DArray<Expression>> exps = null;
        public ScopeDsymbol scopesym = null;
        public boolean compiled = false;
        public  CompileDeclaration(Loc loc, Ptr<DArray<Expression>> exps) {
            super(loc, null, null);
            this.exps = exps;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            return new CompileDeclaration(this.loc.value, Expression.arraySyntaxCopy(this.exps));
        }

        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sds) {
            this.scopesym = sds;
        }

        public  void setScope(Ptr<Scope> sc) {
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
        public Ptr<DArray<Expression>> atts = null;
        public  UserAttributeDeclaration(Ptr<DArray<Expression>> atts, Ptr<DArray<Dsymbol>> decl) {
            super(decl);
            this.atts = atts;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new UserAttributeDeclaration(Expression.arraySyntaxCopy(this.atts), Dsymbol.arraySyntaxCopy(this.decl.value));
        }

        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            Ptr<Scope> sc2 = sc;
            if ((this.atts != null) && ((this.atts.get()).length.value != 0))
            {
                sc2 = (sc.get()).copy();
                (sc2.get()).userAttribDecl = this;
            }
            return sc2;
        }

        public  void setScope(Ptr<Scope> sc) {
            if (this.decl.value != null)
            {
                this.setScope(sc);
            }
            this.setScope(sc);
            return ;
        }

        public static Ptr<DArray<Expression>> concat(Ptr<DArray<Expression>> udas1, Ptr<DArray<Expression>> udas2) {
            Ptr<DArray<Expression>> udas = null;
            if ((udas1 == null) || ((udas1.get()).length.value == 0))
            {
                udas = udas2;
            }
            else if ((udas2 == null) || ((udas2.get()).length.value == 0))
            {
                udas = udas1;
            }
            else
            {
                udas = refPtr(new DArray<Expression>(2));
                udas.get().set(0, new TupleExp(Loc.initial.value, udas1));
                udas.get().set(1, new TupleExp(Loc.initial.value, udas2));
            }
            return udas;
        }

        public  Ptr<DArray<Expression>> getAttributes() {
            {
                Ptr<Scope> sc = this._scope.value;
                if ((sc) != null)
                {
                    this._scope.value = null;
                    arrayExpressionSemantic(this.atts, sc, false);
                }
            }
            Ptr<DArray<Expression>> exps = refPtr(new DArray<Expression>());
            if (this.userAttribDecl != null)
            {
                (exps.get()).push(new TupleExp(Loc.initial.value, this.userAttribDecl.getAttributes()));
            }
            if ((this.atts != null) && ((this.atts.get()).length.value != 0))
            {
                (exps.get()).push(new TupleExp(Loc.initial.value, this.atts));
            }
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
