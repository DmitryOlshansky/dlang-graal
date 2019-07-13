package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.visitor.*;

public class dimport {

    public static class Import extends Dsymbol
    {
        public Ref<Ptr<DArray<Identifier>>> packages = ref(null);
        public Identifier id = null;
        public Identifier aliasId = null;
        public int isstatic = 0;
        public Prot protection = new Prot();
        public DArray<Identifier> names = new DArray<Identifier>();
        public DArray<Identifier> aliases = new DArray<Identifier>();
        public dmodule.Module mod = null;
        public Ref<dmodule.Package> pkg = ref(null);
        public DArray<AliasDeclaration> aliasdecls = new DArray<AliasDeclaration>();
        public  Import(Loc loc, Ptr<DArray<Identifier>> packages, Identifier id, Identifier aliasId, int isstatic) {
            Ref<Ptr<DArray<Identifier>>> packages_ref = ref(packages);
            Ref<Identifier> id_ref = ref(id);
            Ref<Identifier> aliasId_ref = ref(aliasId);
            Function0<Identifier> selectIdent = new Function0<Identifier>(){
                public Identifier invoke() {
                    if (aliasId_ref.value != null)
                    {
                        return aliasId_ref.value;
                    }
                    else if ((packages_ref.value != null) && ((packages_ref.value.get()).length.value != 0))
                    {
                        return (packages_ref.value.get()).get(0);
                    }
                    else
                    {
                        return id_ref.value;
                    }
                }
            };
            super(loc, selectIdent.invoke());
            assert(id_ref.value != null);
            this.packages.value = packages_ref.value;
            this.id = id_ref.value;
            this.aliasId = aliasId_ref.value;
            this.isstatic = isstatic;
            this.protection = new Prot(Prot.Kind.private_);
        }

        public  void addAlias(Identifier name, Identifier _alias) {
            if (this.isstatic != 0)
                this.error(new BytePtr("cannot have an import bind list"));
            if (this.aliasId == null)
                this.ident.value = null;
            this.names.push(name);
            this.aliases.push(_alias);
        }

        public  BytePtr kind() {
            return this.isstatic != 0 ? new BytePtr("static import") : new BytePtr("import");
        }

        public  Prot prot() {
            return this.protection;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            Import si = new Import(this.loc.value, this.packages.value, this.id, this.aliasId, this.isstatic);
            {
                int i = 0;
                for (; (i < this.names.length.value);i++){
                    si.addAlias(this.names.get(i), this.aliases.get(i));
                }
            }
            return si;
        }

        public  boolean load(Ptr<Scope> sc) {
            int errors = global.errors.value;
            DsymbolTable dst = dmodule.Package.resolve(this.packages.value, null, ptr(this.pkg));
            Dsymbol s = dst.lookup(this.id);
            if (s != null)
            {
                if (s.isModule() != null)
                    this.mod = (dmodule.Module)s;
                else
                {
                    if (s.isAliasDeclaration() != null)
                    {
                        error(this.loc.value, new BytePtr("%s `%s` conflicts with `%s`"), s.kind(), s.toPrettyChars(false), this.id.toChars());
                    }
                    else {
                        dmodule.Package p = s.isPackage();
                        if ((p) != null)
                        {
                            if ((p.isPkgMod == PKG.unknown))
                            {
                                this.mod = dmodule.Module.load(this.loc.value, this.packages.value, this.id);
                                if (this.mod == null)
                                    p.isPkgMod = PKG.package_;
                                else
                                {
                                    assert(((this.mod.isPackageFile ? 1 : 0) == ((p.isPkgMod == PKG.module_) ? 1 : 0)));
                                    if (this.mod.isPackageFile)
                                        this.mod.tag = p.tag;
                                }
                            }
                            else
                            {
                                this.mod = p.isPackageMod();
                            }
                            if (this.mod == null)
                            {
                                error(this.loc.value, new BytePtr("can only import from a module, not from package `%s.%s`"), p.toPrettyChars(false), this.id.toChars());
                            }
                        }
                        else if (this.pkg.value != null)
                        {
                            error(this.loc.value, new BytePtr("can only import from a module, not from package `%s.%s`"), this.pkg.value.toPrettyChars(false), this.id.toChars());
                        }
                        else
                        {
                            error(this.loc.value, new BytePtr("can only import from a module, not from package `%s`"), this.id.toChars());
                        }
                    }
                }
            }
            if (this.mod == null)
            {
                this.mod = dmodule.Module.load(this.loc.value, this.packages.value, this.id);
                if (this.mod != null)
                {
                    dst.insert(this.id, this.mod);
                }
            }
            if ((this.mod != null) && (this.mod.importedFrom == null))
                this.mod.importedFrom = sc != null ? (sc.get())._module.value.importedFrom : dmodule.Module.rootModule;
            if (this.pkg.value == null)
                this.pkg.value = this.mod;
            return global.errors.value != errors;
        }

        public  void importAll(Ptr<Scope> sc) {
            if (this.mod != null)
                return ;
            this.load(sc);
            if (this.mod == null)
                return ;
            this.mod.importAll(null);
            if ((this.mod.md != null) && (this.mod.md.get()).isdeprecated)
            {
                Expression msg = (this.mod.md.get()).msg;
                {
                    StringExp se = msg != null ? msg.toStringExp() : null;
                    if ((se) != null)
                        this.mod.deprecation(this.loc.value, new BytePtr("is deprecated - %s"), se.string.value);
                    else
                        this.mod.deprecation(this.loc.value, new BytePtr("is deprecated"));
                }
            }
            if ((sc.get()).explicitProtection != 0)
                this.protection = (sc.get()).protection.value.copy();
            if ((this.isstatic == 0) && (this.aliasId == null) && (this.names.length.value == 0))
                (sc.get()).scopesym.value.importScope(this.mod, this.protection);
        }

        public  Dsymbol toAlias() {
            if (this.aliasId != null)
                return this.mod;
            return this;
        }

        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sd) {
            if ((this.names.length.value == 0))
                this.addMember(sc, sd);
                return ;
            if (this.aliasId != null)
                this.addMember(sc, sd);
            {
                int i = 0;
                for (; (i < this.names.length.value);i++){
                    Identifier name = this.names.get(i);
                    Identifier _alias = this.aliases.get(i);
                    if (_alias == null)
                        _alias = name;
                    TypeIdentifier tname = new TypeIdentifier(this.loc.value, name);
                    AliasDeclaration ad = new AliasDeclaration(this.loc.value, _alias, tname);
                    ad._import.value = this;
                    ad.addMember(sc, sd);
                    this.aliasdecls.push(ad);
                }
            }
        }

        public  void setScope(Ptr<Scope> sc) {
            this.setScope(sc);
            if (this.aliasdecls.length != 0)
            {
                if (this.mod == null)
                    this.importAll(sc);
                sc = (sc.get()).push(this.mod);
                (sc.get()).protection.value = this.protection.copy();
                {
                    Slice<AliasDeclaration> __r927 = this.aliasdecls.opSlice().copy();
                    int __key928 = 0;
                    for (; (__key928 < __r927.getLength());__key928 += 1) {
                        AliasDeclaration ad = __r927.get(__key928);
                        ad.setScope(sc);
                    }
                }
                sc = (sc.get()).pop();
            }
        }

        public  Dsymbol search(Loc loc, Identifier ident, int flags) {
            if (this.pkg.value == null)
            {
                this.load(null);
                this.mod.importAll(null);
                dsymbolSemantic(this.mod, null);
            }
            return this.pkg.value.search(loc, ident, flags);
        }

        // defaulted all parameters starting with #3
        public  Dsymbol search(Loc loc, Identifier ident) {
            return search(loc, ident, 8);
        }

        public  boolean overloadInsert(Dsymbol s) {
            assert((this.ident.value != null) && (pequals(this.ident.value, s.ident.value)));
            Import imp = null;
            if ((this.aliasId == null) && ((imp = s.isImport()) != null) && (imp.aliasId == null))
                return true;
            else
                return false;
        }

        public  Import isImport() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public Import() {}

        public Import copy() {
            Import that = new Import();
            that.packages = this.packages;
            that.id = this.id;
            that.aliasId = this.aliasId;
            that.isstatic = this.isstatic;
            that.protection = this.protection;
            that.names = this.names;
            that.aliases = this.aliases;
            that.mod = this.mod;
            that.pkg = this.pkg;
            that.aliasdecls = this.aliasdecls;
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
