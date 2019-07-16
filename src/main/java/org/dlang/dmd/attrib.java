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
        public Ptr<DArray<Dsymbol>> decl = null;
        // Erasure: __ctor<Ptr>
        public  AttribDeclaration(Ptr<DArray<Dsymbol>> decl) {
            super();
            this.decl = pcopy(decl);
        }

        // Erasure: __ctor<Loc, Identifier, Ptr>
        public  AttribDeclaration(Loc loc, Identifier ident, Ptr<DArray<Dsymbol>> decl) {
            super(loc, ident);
            this.decl = pcopy(decl);
        }

        // Erasure: include<Ptr>
        public  Ptr<DArray<Dsymbol>> include(Ptr<Scope> sc) {
            if (this.errors)
            {
                return null;
            }
            return this.decl;
        }

        // Erasure: apply<Ptr, Ptr>
        public  int apply(Function2<Dsymbol,Object,Integer> fp, Object param) {
            AttribDeclaration __self = this;
            Function1<Dsymbol,Integer> __lambda3 = new Function1<Dsymbol,Integer>() {
                public Integer invoke(Dsymbol s) {
                 {
                    return (((s != null) && (s.apply(fp, param) != 0)) ? 1 : 0);
                }}

            };
            return foreachDsymbol(this.include(this._scope), __lambda3);
        }

        // Erasure: createNewScope<Ptr, long, int, int, Prot, int, AlignDeclaration, int>
        public static Ptr<Scope> createNewScope(Ptr<Scope> sc, long stc, int linkage, int cppmangle, Prot protection, int explicitProtection, AlignDeclaration aligndecl, int inlining) {
            Ptr<Scope> sc2 = sc;
            if ((stc != (sc.get()).stc) || (linkage != (sc.get()).linkage) || (cppmangle != (sc.get()).cppmangle) || !protection.isSubsetOf((sc.get()).protection) || (explicitProtection != (sc.get()).explicitProtection) || (aligndecl != (sc.get()).aligndecl) || (inlining != (sc.get()).inlining))
            {
                sc2 = pcopy((sc.get()).copy());
                (sc2.get()).stc = stc;
                (sc2.get()).linkage = linkage;
                (sc2.get()).cppmangle = cppmangle;
                (sc2.get()).protection.opAssign(protection.copy());
                (sc2.get()).explicitProtection = explicitProtection;
                (sc2.get()).aligndecl = aligndecl;
                (sc2.get()).inlining = inlining;
            }
            return sc2;
        }

        // Erasure: newScope<Ptr>
        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            return sc;
        }

        // Erasure: addMember<Ptr, ScopeDsymbol>
        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sds) {
            AttribDeclaration __self = this;
            Ptr<DArray<Dsymbol>> d = this.include(sc);
            if (d != null)
            {
                Ptr<Scope> sc2 = this.newScope(sc);
                Runnable1<Dsymbol> __lambda3 = new Runnable1<Dsymbol>() {
                    public Void invoke(Dsymbol s) {
                     {
                        s.addMember(sc2, sds);
                        return null;
                    }}

                };
                foreachDsymbol(d, __lambda3);
                if ((sc2 != sc))
                {
                    (sc2.get()).pop();
                }
            }
        }

        // Erasure: setScope<Ptr>
        public  void setScope(Ptr<Scope> sc) {
            AttribDeclaration __self = this;
            Ptr<DArray<Dsymbol>> d = this.include(sc);
            if (d != null)
            {
                Ptr<Scope> sc2 = this.newScope(sc);
                Runnable1<Dsymbol> __lambda2 = new Runnable1<Dsymbol>() {
                    public Void invoke(Dsymbol s) {
                     {
                        s.setScope(sc2);
                        return null;
                    }}

                };
                foreachDsymbol(d, __lambda2);
                if ((sc2 != sc))
                {
                    (sc2.get()).pop();
                }
            }
        }

        // Erasure: importAll<Ptr>
        public  void importAll(Ptr<Scope> sc) {
            AttribDeclaration __self = this;
            Ptr<DArray<Dsymbol>> d = this.include(sc);
            if (d != null)
            {
                Ptr<Scope> sc2 = this.newScope(sc);
                Runnable1<Dsymbol> __lambda2 = new Runnable1<Dsymbol>() {
                    public Void invoke(Dsymbol s) {
                     {
                        s.importAll(sc2);
                        return null;
                    }}

                };
                foreachDsymbol(d, __lambda2);
                if ((sc2 != sc))
                {
                    (sc2.get()).pop();
                }
            }
        }

        // Erasure: addComment<Ptr>
        public  void addComment(BytePtr comment) {
            AttribDeclaration __self = this;
            if (comment != null)
            {
                Runnable1<Dsymbol> __lambda2 = new Runnable1<Dsymbol>() {
                    public Void invoke(Dsymbol s) {
                     {
                        s.addComment(comment);
                        return null;
                    }}

                };
                foreachDsymbol(this.include(null), __lambda2);
            }
        }

        // Erasure: kind<>
        public  BytePtr kind() {
            return new BytePtr("attribute");
        }

        // Erasure: oneMember<Ptr, Identifier>
        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            Ptr<DArray<Dsymbol>> d = this.include(null);
            return Dsymbol.oneMembers(d, ps, ident);
        }

        // Erasure: setFieldOffset<AggregateDeclaration, Ptr, boolean>
        public  void setFieldOffset(AggregateDeclaration ad, Ptr<Integer> poffset, boolean isunion) {
            AttribDeclaration __self = this;
            Runnable1<Dsymbol> __lambda4 = new Runnable1<Dsymbol>() {
                public Void invoke(Dsymbol s) {
                 {
                    s.setFieldOffset(ad, poffset, isunion);
                    return null;
                }}

            };
            foreachDsymbol(this.include(null), __lambda4);
        }

        // Erasure: hasPointers<>
        public  boolean hasPointers() {
            AttribDeclaration __self = this;
            Function1<Dsymbol,Integer> __lambda1 = new Function1<Dsymbol,Integer>() {
                public Integer invoke(Dsymbol s) {
                 {
                    return (s.hasPointers() ? 1 : 0);
                }}

            };
            return foreachDsymbol(this.include(null), __lambda1) != 0;
        }

        // Erasure: hasStaticCtorOrDtor<>
        public  boolean hasStaticCtorOrDtor() {
            AttribDeclaration __self = this;
            Function1<Dsymbol,Integer> __lambda1 = new Function1<Dsymbol,Integer>() {
                public Integer invoke(Dsymbol s) {
                 {
                    return (s.hasStaticCtorOrDtor() ? 1 : 0);
                }}

            };
            return foreachDsymbol(this.include(null), __lambda1) != 0;
        }

        // Erasure: checkCtorConstInit<>
        public  void checkCtorConstInit() {
            AttribDeclaration __self = this;
            Runnable1<Dsymbol> __lambda1 = new Runnable1<Dsymbol>() {
                public Void invoke(Dsymbol s) {
                 {
                    s.checkCtorConstInit();
                    return null;
                }}

            };
            foreachDsymbol(this.include(null), __lambda1);
        }

        // Erasure: addLocalClass<Ptr>
        public  void addLocalClass(Ptr<DArray<ClassDeclaration>> aclasses) {
            AttribDeclaration __self = this;
            Runnable1<Dsymbol> __lambda2 = new Runnable1<Dsymbol>() {
                public Void invoke(Dsymbol s) {
                 {
                    s.addLocalClass(aclasses);
                    return null;
                }}

            };
            foreachDsymbol(this.include(null), __lambda2);
        }

        // Erasure: addObjcSymbols<Ptr, Ptr>
        public  void addObjcSymbols(Ptr<DArray<ClassDeclaration>> classes, Ptr<DArray<ClassDeclaration>> categories) {
            objc().addSymbols(this, classes, categories);
        }

        // Erasure: isAttribDeclaration<>
        public  AttribDeclaration isAttribDeclaration() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AttribDeclaration() {}

        public abstract AttribDeclaration copy();
    }
    public static class StorageClassDeclaration extends AttribDeclaration
    {
        public long stc = 0L;
        // Erasure: __ctor<long, Ptr>
        public  StorageClassDeclaration(long stc, Ptr<DArray<Dsymbol>> decl) {
            super(decl);
            this.stc = stc;
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new StorageClassDeclaration(this.stc, Dsymbol.arraySyntaxCopy(this.decl));
        }

        // Erasure: newScope<Ptr>
        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            long scstc = (sc.get()).stc;
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
            return AttribDeclaration.createNewScope(sc, scstc, (sc.get()).linkage, (sc.get()).cppmangle, (sc.get()).protection, (sc.get()).explicitProtection, (sc.get()).aligndecl, (sc.get()).inlining);
        }

        // Erasure: oneMember<Ptr, Identifier>
        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            boolean t = Dsymbol.oneMembers(this.decl, ps, ident);
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

        // Erasure: addMember<Ptr, ScopeDsymbol>
        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sds) {
            StorageClassDeclaration __self = this;
            Ptr<DArray<Dsymbol>> d = this.include(sc);
            if (d != null)
            {
                Ptr<Scope> sc2 = this.newScope(sc);
                Runnable1<Dsymbol> __lambda3 = new Runnable1<Dsymbol>() {
                    public Void invoke(Dsymbol s) {
                     {
                        {
                            Declaration decl = s.isDeclaration();
                            if ((decl) != null)
                            {
                                decl.storage_class |= stc & 2251799813685248L;
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
                    }}

                };
                foreachDsymbol(d, __lambda3);
                if ((sc2 != sc))
                {
                    (sc2.get()).pop();
                }
            }
        }

        // Erasure: isStorageClassDeclaration<>
        public  StorageClassDeclaration isStorageClassDeclaration() {
            return this;
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Expression, Ptr>
        public  DeprecatedDeclaration(Expression msg, Ptr<DArray<Dsymbol>> decl) {
            super(1024L, decl);
            this.msg = msg;
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new DeprecatedDeclaration(this.msg.syntaxCopy(), Dsymbol.arraySyntaxCopy(this.decl));
        }

        // Erasure: newScope<Ptr>
        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            Ptr<Scope> scx = super.newScope(sc);
            if ((scx == sc))
            {
                scx = pcopy((sc.get()).push());
            }
            (scx.get()).depdecl = this;
            return scx;
        }

        // Erasure: setScope<Ptr>
        public  void setScope(Ptr<Scope> sc) {
            if (this.decl != null)
            {
                this.setScope(sc);
            }
            this.setScope(sc);
            return ;
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<int, Ptr>
        public  LinkDeclaration(int linkage, Ptr<DArray<Dsymbol>> decl) {
            super(decl);
            this.linkage = (linkage == LINK.system) ? target.systemLinkage() : linkage;
        }

        // Erasure: create<int, Ptr>
        public static LinkDeclaration create(int p, Ptr<DArray<Dsymbol>> decl) {
            return new LinkDeclaration(p, decl);
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new LinkDeclaration(this.linkage, Dsymbol.arraySyntaxCopy(this.decl));
        }

        // Erasure: newScope<Ptr>
        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            return AttribDeclaration.createNewScope(sc, (sc.get()).stc, this.linkage, (sc.get()).cppmangle, (sc.get()).protection, (sc.get()).explicitProtection, (sc.get()).aligndecl, (sc.get()).inlining);
        }

        // Erasure: toChars<>
        public  BytePtr toChars() {
            return this.asString().getPtr(0);
        }

        // Erasure: asString<>
        public  ByteSlice asString() {
            return new ByteSlice("extern ()");
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<int, Ptr>
        public  CPPMangleDeclaration(int cppmangle, Ptr<DArray<Dsymbol>> decl) {
            super(decl);
            this.cppmangle = cppmangle;
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new CPPMangleDeclaration(this.cppmangle, Dsymbol.arraySyntaxCopy(this.decl));
        }

        // Erasure: newScope<Ptr>
        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            return AttribDeclaration.createNewScope(sc, (sc.get()).stc, LINK.cpp, this.cppmangle, (sc.get()).protection, (sc.get()).explicitProtection, (sc.get()).aligndecl, (sc.get()).inlining);
        }

        // Erasure: toChars<>
        public  BytePtr toChars() {
            return this.asString().getPtr(0);
        }

        // Erasure: asString<>
        public  ByteSlice asString() {
            return new ByteSlice("extern ()");
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Identifier, Ptr>
        public  CPPNamespaceDeclaration(Identifier ident, Ptr<DArray<Dsymbol>> decl) {
            super(decl);
            this.ident = ident;
        }

        // Erasure: __ctor<Expression, Ptr>
        public  CPPNamespaceDeclaration(Expression exp, Ptr<DArray<Dsymbol>> decl) {
            super(decl);
            this.exp = exp;
        }

        // Erasure: __ctor<Identifier, Expression, Ptr, CPPNamespaceDeclaration>
        public  CPPNamespaceDeclaration(Identifier ident, Expression exp, Ptr<DArray<Dsymbol>> decl, CPPNamespaceDeclaration parent) {
            super(decl);
            this.ident = ident;
            this.exp = exp;
            this.namespace = parent;
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new CPPNamespaceDeclaration(this.ident, this.exp, Dsymbol.arraySyntaxCopy(this.decl), this.namespace);
        }

        // Erasure: newScope<Ptr>
        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            Ptr<Scope> scx = (sc.get()).copy();
            (scx.get()).linkage = LINK.cpp;
            (scx.get()).namespace = this;
            return scx;
        }

        // Erasure: toChars<>
        public  BytePtr toChars() {
            return this.asString().getPtr(0);
        }

        // Erasure: asString<>
        public  ByteSlice asString() {
            return new ByteSlice("extern (C++, `namespace`)");
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }

        // Erasure: isCPPNamespaceDeclaration<>
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
        public Ptr<DArray<Identifier>> pkg_identifiers = null;
        // Erasure: __ctor<Loc, Prot, Ptr>
        public  ProtDeclaration(Loc loc, Prot protection, Ptr<DArray<Dsymbol>> decl) {
            super(loc, null, decl);
            this.protection.opAssign(protection.copy());
        }

        // Erasure: __ctor<Loc, Ptr, Ptr>
        public  ProtDeclaration(Loc loc, Ptr<DArray<Identifier>> pkg_identifiers, Ptr<DArray<Dsymbol>> decl) {
            super(loc, null, decl);
            this.protection.kind = Prot.Kind.package_;
            this.pkg_identifiers = pcopy(pkg_identifiers);
            if ((pkg_identifiers != null) && ((pkg_identifiers.get()).length > 0))
            {
                Ref<Dsymbol> tmp = ref(null);
                dmodule.Package.resolve(pkg_identifiers, ptr(tmp), null);
                this.protection.pkg = tmp.value != null ? tmp.value.isPackage() : null;
            }
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            if ((this.protection.kind == Prot.Kind.package_))
            {
                return new ProtDeclaration(this.loc, this.pkg_identifiers, Dsymbol.arraySyntaxCopy(this.decl));
            }
            else
            {
                return new ProtDeclaration(this.loc, this.protection, Dsymbol.arraySyntaxCopy(this.decl));
            }
        }

        // Erasure: newScope<Ptr>
        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            if (this.pkg_identifiers != null)
            {
                dsymbolSemantic(this, sc);
            }
            return AttribDeclaration.createNewScope(sc, (sc.get()).stc, (sc.get()).linkage, (sc.get()).cppmangle, this.protection, 1, (sc.get()).aligndecl, (sc.get()).inlining);
        }

        // Erasure: addMember<Ptr, ScopeDsymbol>
        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sds) {
            if (this.pkg_identifiers != null)
            {
                Ref<Dsymbol> tmp = ref(null);
                dmodule.Package.resolve(this.pkg_identifiers, ptr(tmp), null);
                this.protection.pkg = tmp.value != null ? tmp.value.isPackage() : null;
                this.pkg_identifiers = null;
            }
            if ((this.protection.kind == Prot.Kind.package_) && (this.protection.pkg != null) && ((sc.get())._module != null))
            {
                dmodule.Module m = (sc.get())._module;
                dmodule.Package pkg = m.parent.value != null ? m.parent.value.isPackage() : null;
                if ((pkg == null) || !this.protection.pkg.isAncestorPackageOf(pkg))
                {
                    this.error(new BytePtr("does not bind to one of ancestor packages of module `%s`"), m.toPrettyChars(true));
                }
            }
            this.addMember(sc, sds);
            return ;
        }

        // Erasure: kind<>
        public  BytePtr kind() {
            return new BytePtr("protection attribute");
        }

        // Erasure: toPrettyChars<boolean>
        public  BytePtr toPrettyChars(boolean _param_0) {
            assert((this.protection.kind > Prot.Kind.undefined));
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                protectionToBuffer(ptr(buf), this.protection);
                return buf.value.extractChars();
            }
            finally {
            }
        }

        // Erasure: isProtDeclaration<>
        public  ProtDeclaration isProtDeclaration() {
            return this;
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Loc, Expression, Ptr>
        public  AlignDeclaration(Loc loc, Expression ealign, Ptr<DArray<Dsymbol>> decl) {
            super(loc, null, decl);
            this.ealign = ealign;
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new AlignDeclaration(this.loc, this.ealign != null ? this.ealign.syntaxCopy() : null, Dsymbol.arraySyntaxCopy(this.decl));
        }

        // Erasure: newScope<Ptr>
        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            return AttribDeclaration.createNewScope(sc, (sc.get()).stc, (sc.get()).linkage, (sc.get()).cppmangle, (sc.get()).protection, (sc.get()).explicitProtection, this, (sc.get()).inlining);
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Loc, boolean, Ptr>
        public  AnonDeclaration(Loc loc, boolean isunion, Ptr<DArray<Dsymbol>> decl) {
            super(loc, null, decl);
            this.isunion = isunion;
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new AnonDeclaration(this.loc, this.isunion, Dsymbol.arraySyntaxCopy(this.decl));
        }

        // Erasure: setScope<Ptr>
        public  void setScope(Ptr<Scope> sc) {
            if (this.decl != null)
            {
                this.setScope(sc);
            }
            this.setScope(sc);
            return ;
        }

        // Erasure: setFieldOffset<AggregateDeclaration, Ptr, boolean>
        public  void setFieldOffset(AggregateDeclaration ad, Ptr<Integer> poffset, boolean isunion) {
            AnonDeclaration __self = this;
            if (this.decl != null)
            {
                int fieldstart = ad.fields.length;
                int savestructsize = ad.structsize.value;
                int savealignsize = ad.alignsize.value;
                ad.structsize.value = 0;
                ad.alignsize.value = 0;
                Ref<Integer> offset = ref(0);
                Runnable1<Dsymbol> __lambda4 = new Runnable1<Dsymbol>() {
                    public Void invoke(Dsymbol s) {
                     {
                        s.setFieldOffset(ad, ptr(offset), isunion);
                        if (isunion)
                        {
                            offset.value = 0;
                        }
                        return null;
                    }}

                };
                foreachDsymbol(this.decl, __lambda4);
                if ((fieldstart == ad.fields.length))
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
                assert(this._scope != null);
                int alignment = (this._scope.get()).alignment();
                this.anonoffset = AggregateDeclaration.placeField(poffset, this.anonstructsize, this.anonalignsize, alignment, ptr(ad.structsize), ptr(ad.alignsize), isunion);
                {
                    int __key791 = fieldstart;
                    int __limit792 = ad.fields.length;
                    for (; (__key791 < __limit792);__key791 += 1) {
                        int i = __key791;
                        VarDeclaration v = ad.fields.get(i);
                        v.offset += this.anonoffset;
                    }
                }
            }
        }

        // Erasure: kind<>
        public  BytePtr kind() {
            return this.isunion ? new BytePtr("anonymous union") : new BytePtr("anonymous struct");
        }

        // Erasure: isAnonDeclaration<>
        public  AnonDeclaration isAnonDeclaration() {
            return this;
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Loc, Identifier, Ptr, Ptr>
        public  PragmaDeclaration(Loc loc, Identifier ident, Ptr<DArray<Expression>> args, Ptr<DArray<Dsymbol>> decl) {
            super(loc, ident, decl);
            this.args = pcopy(args);
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new PragmaDeclaration(this.loc, this.ident, Expression.arraySyntaxCopy(this.args), Dsymbol.arraySyntaxCopy(this.decl));
        }

        // Erasure: newScope<Ptr>
        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            if ((pequals(this.ident, Id.Pinline)))
            {
                int inlining = PINLINE.default_;
                if ((this.args == null) || ((this.args.get()).length == 0))
                {
                    inlining = PINLINE.default_;
                }
                else if (((this.args.get()).length != 1))
                {
                    this.error(new BytePtr("one boolean expression expected for `pragma(inline)`, not %d"), (this.args.get()).length);
                    (this.args.get()).setDim(1);
                    this.args.get().set(0, new ErrorExp());
                }
                else
                {
                    Expression e = (this.args.get()).get(0);
                    if (((e.op & 0xFF) != 135) || !e.type.value.equals(Type.tbool))
                    {
                        if (((e.op & 0xFF) != 127))
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
                return AttribDeclaration.createNewScope(sc, (sc.get()).stc, (sc.get()).linkage, (sc.get()).cppmangle, (sc.get()).protection, (sc.get()).explicitProtection, (sc.get()).aligndecl, inlining);
            }
            return sc;
        }

        // Erasure: kind<>
        public  BytePtr kind() {
            return new BytePtr("pragma");
        }

        // Erasure: accept<Visitor>
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
        public Ptr<DArray<Dsymbol>> elsedecl = null;
        // Erasure: __ctor<Condition, Ptr, Ptr>
        public  ConditionalDeclaration(Condition condition, Ptr<DArray<Dsymbol>> decl, Ptr<DArray<Dsymbol>> elsedecl) {
            super(decl);
            this.condition = condition;
            this.elsedecl = pcopy(elsedecl);
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new ConditionalDeclaration(this.condition.syntaxCopy(), Dsymbol.arraySyntaxCopy(this.decl), Dsymbol.arraySyntaxCopy(this.elsedecl));
        }

        // Erasure: oneMember<Ptr, Identifier>
        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            if ((this.condition.inc != Include.notComputed))
            {
                Ptr<DArray<Dsymbol>> d = this.condition.include(null) != 0 ? this.decl : this.elsedecl;
                return Dsymbol.oneMembers(d, ps, ident);
            }
            else
            {
                boolean res = Dsymbol.oneMembers(this.decl, ps, ident) && (ps.get() == null) && Dsymbol.oneMembers(this.elsedecl, ps, ident) && (ps.get() == null);
                ps.set(0, null);
                return res;
            }
        }

        // Erasure: include<Ptr>
        public  Ptr<DArray<Dsymbol>> include(Ptr<Scope> sc) {
            if (this.errors)
            {
                return null;
            }
            assert(this.condition != null);
            return this.condition.include(this._scope != null ? this._scope : sc) != 0 ? this.decl : this.elsedecl;
        }

        // Erasure: addComment<Ptr>
        public  void addComment(BytePtr comment) {
            ConditionalDeclaration __self = this;
            if (comment != null)
            {
                Runnable1<Dsymbol> __lambda2 = new Runnable1<Dsymbol>() {
                    public Void invoke(Dsymbol s) {
                     {
                        s.addComment(comment);
                        return null;
                    }}

                };
                foreachDsymbol(this.decl, __lambda2);
                Runnable1<Dsymbol> __lambda3 = new Runnable1<Dsymbol>() {
                    public Void invoke(Dsymbol s) {
                     {
                        s.addComment(comment);
                        return null;
                    }}

                };
                foreachDsymbol(this.elsedecl, __lambda3);
            }
        }

        // Erasure: setScope<Ptr>
        public  void setScope(Ptr<Scope> sc) {
            ConditionalDeclaration __self = this;
            Runnable1<Dsymbol> __lambda2 = new Runnable1<Dsymbol>() {
                public Void invoke(Dsymbol s) {
                 {
                    s.setScope(sc);
                    return null;
                }}

            };
            foreachDsymbol(this.include(sc), __lambda2);
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Condition, Ptr, Ptr>
        public  StaticIfDeclaration(Condition condition, Ptr<DArray<Dsymbol>> decl, Ptr<DArray<Dsymbol>> elsedecl) {
            super(condition, decl, elsedecl);
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new StaticIfDeclaration(this.condition.syntaxCopy(), Dsymbol.arraySyntaxCopy(this.decl), Dsymbol.arraySyntaxCopy(this.elsedecl));
        }

        // Erasure: include<Ptr>
        public  Ptr<DArray<Dsymbol>> include(Ptr<Scope> sc) {
            StaticIfDeclaration __self = this;
            if (this.errors || this.onStack)
            {
                return null;
            }
            this.onStack = true;
            try {
                if ((sc != null) && (this.condition.inc == Include.notComputed))
                {
                    assert(this.scopesym != null);
                    assert(this._scope != null);
                    Ptr<DArray<Dsymbol>> d = this.include(this._scope);
                    if ((d != null) && !this.addisdone)
                    {
                        Runnable1<Dsymbol> __lambda2 = new Runnable1<Dsymbol>() {
                            public Void invoke(Dsymbol s) {
                             {
                                s.addMember(_scope, scopesym);
                                return null;
                            }}

                        };
                        foreachDsymbol(d, __lambda2);
                        Runnable1<Dsymbol> __lambda3 = new Runnable1<Dsymbol>() {
                            public Void invoke(Dsymbol s) {
                             {
                                s.setScope(_scope);
                                return null;
                            }}

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

        // Erasure: addMember<Ptr, ScopeDsymbol>
        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sds) {
            this.scopesym = sds;
        }

        // Erasure: setScope<Ptr>
        public  void setScope(Ptr<Scope> sc) {
            this.setScope(sc);
        }

        // Erasure: importAll<Ptr>
        public  void importAll(Ptr<Scope> sc) {
        }

        // Erasure: kind<>
        public  BytePtr kind() {
            return new BytePtr("static if");
        }

        // Erasure: accept<Visitor>
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
        public StaticForeach sfe = null;
        public ScopeDsymbol scopesym = null;
        public boolean onStack = false;
        public boolean cached = false;
        public Ptr<DArray<Dsymbol>> cache = null;
        // Erasure: __ctor<StaticForeach, Ptr>
        public  StaticForeachDeclaration(StaticForeach sfe, Ptr<DArray<Dsymbol>> decl) {
            super(decl);
            this.sfe = sfe;
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new StaticForeachDeclaration(this.sfe.syntaxCopy(), Dsymbol.arraySyntaxCopy(this.decl));
        }

        // Erasure: oneMember<Ptr, Identifier>
        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            if (this.cached)
            {
                return super.oneMember(ps, ident);
            }
            ps.set(0, null);
            return false;
        }

        // Erasure: include<Ptr>
        public  Ptr<DArray<Dsymbol>> include(Ptr<Scope> sc) {
            StaticForeachDeclaration __self = this;
            if (this.errors || this.onStack)
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
                if (this._scope != null)
                {
                    this.sfe.prepare(this._scope);
                }
                if (!this.sfe.ready())
                {
                    return null;
                }
                Ptr<DArray<Dsymbol>> d = makeTupleForeach11(this._scope, this.sfe.aggrfe, this.decl, this.sfe.needExpansion);
                if (d != null)
                {
                    Runnable1<Dsymbol> __lambda2 = new Runnable1<Dsymbol>() {
                        public Void invoke(Dsymbol s) {
                         {
                            s.addMember(_scope, scopesym);
                            return null;
                        }}

                    };
                    foreachDsymbol(d, __lambda2);
                    Runnable1<Dsymbol> __lambda3 = new Runnable1<Dsymbol>() {
                        public Void invoke(Dsymbol s) {
                         {
                            s.setScope(_scope);
                            return null;
                        }}

                    };
                    foreachDsymbol(d, __lambda3);
                }
                this.cached = true;
                this.cache = pcopy(d);
                return d;
            }
            finally {
                this.onStack = false;
            }
        }

        // Erasure: addMember<Ptr, ScopeDsymbol>
        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sds) {
            this.scopesym = sds;
        }

        // Erasure: addComment<Ptr>
        public  void addComment(BytePtr comment) {
        }

        // Erasure: setScope<Ptr>
        public  void setScope(Ptr<Scope> sc) {
            this.setScope(sc);
        }

        // Erasure: importAll<Ptr>
        public  void importAll(Ptr<Scope> sc) {
        }

        // Erasure: kind<>
        public  BytePtr kind() {
            return new BytePtr("static foreach");
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Ptr>
        public  ForwardingAttribDeclaration(Ptr<DArray<Dsymbol>> decl) {
            super(decl);
            this.sym = new ForwardingScopeDsymbol(null);
            this.sym.symtab = new DsymbolTable();
        }

        // Erasure: newScope<Ptr>
        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            return (sc.get()).push(this.sym);
        }

        // Erasure: addMember<Ptr, ScopeDsymbol>
        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sds) {
            this.parent.value = (this.sym.parent.value = (this.sym.forward = sds));
            super.addMember(sc, this.sym);
            return ;
        }

        // Erasure: isForwardingAttribDeclaration<>
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
        // Erasure: __ctor<Loc, Ptr>
        public  CompileDeclaration(Loc loc, Ptr<DArray<Expression>> exps) {
            super(loc, null, null);
            this.exps = pcopy(exps);
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            return new CompileDeclaration(this.loc, Expression.arraySyntaxCopy(this.exps));
        }

        // Erasure: addMember<Ptr, ScopeDsymbol>
        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sds) {
            this.scopesym = sds;
        }

        // Erasure: setScope<Ptr>
        public  void setScope(Ptr<Scope> sc) {
            this.setScope(sc);
        }

        // Erasure: kind<>
        public  BytePtr kind() {
            return new BytePtr("mixin");
        }

        // Erasure: isCompileDeclaration<>
        public  CompileDeclaration isCompileDeclaration() {
            return this;
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Ptr, Ptr>
        public  UserAttributeDeclaration(Ptr<DArray<Expression>> atts, Ptr<DArray<Dsymbol>> decl) {
            super(decl);
            this.atts = pcopy(atts);
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new UserAttributeDeclaration(Expression.arraySyntaxCopy(this.atts), Dsymbol.arraySyntaxCopy(this.decl));
        }

        // Erasure: newScope<Ptr>
        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            Ptr<Scope> sc2 = sc;
            if ((this.atts != null) && ((this.atts.get()).length != 0))
            {
                sc2 = pcopy((sc.get()).copy());
                (sc2.get()).userAttribDecl = this;
            }
            return sc2;
        }

        // Erasure: setScope<Ptr>
        public  void setScope(Ptr<Scope> sc) {
            if (this.decl != null)
            {
                this.setScope(sc);
            }
            this.setScope(sc);
            return ;
        }

        // Erasure: concat<Ptr, Ptr>
        public static Ptr<DArray<Expression>> concat(Ptr<DArray<Expression>> udas1, Ptr<DArray<Expression>> udas2) {
            Ptr<DArray<Expression>> udas = null;
            if ((udas1 == null) || ((udas1.get()).length == 0))
            {
                udas = pcopy(udas2);
            }
            else if ((udas2 == null) || ((udas2.get()).length == 0))
            {
                udas = pcopy(udas1);
            }
            else
            {
                udas = pcopy((refPtr(new DArray<Expression>(2))));
                udas.get().set(0, new TupleExp(Loc.initial, udas1));
                udas.get().set(1, new TupleExp(Loc.initial, udas2));
            }
            return udas;
        }

        // Erasure: getAttributes<>
        public  Ptr<DArray<Expression>> getAttributes() {
            {
                Ptr<Scope> sc = this._scope;
                if ((sc) != null)
                {
                    this._scope = null;
                    arrayExpressionSemantic(this.atts, sc, false);
                }
            }
            Ptr<DArray<Expression>> exps = refPtr(new DArray<Expression>());
            if (this.userAttribDecl != null)
            {
                (exps.get()).push(new TupleExp(Loc.initial, this.userAttribDecl.getAttributes()));
            }
            if ((this.atts != null) && ((this.atts.get()).length != 0))
            {
                (exps.get()).push(new TupleExp(Loc.initial, this.atts));
            }
            return exps;
        }

        // Erasure: kind<>
        public  BytePtr kind() {
            return new BytePtr("UserAttribute");
        }

        // Erasure: accept<Visitor>
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
