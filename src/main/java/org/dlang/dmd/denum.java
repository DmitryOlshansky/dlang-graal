package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.gluelayer.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.visitor.*;

public class denum {

    public static class EnumDeclaration extends ScopeDsymbol
    {
        public Type type = null;
        public Ref<Type> memtype = ref(null);
        public Prot protection = new Prot();
        public Ref<Expression> maxval = ref(null);
        public Ref<Expression> minval = ref(null);
        public Ref<Expression> defaultval = ref(null);
        public boolean isdeprecated = false;
        public boolean added = false;
        public int inuse = 0;
        public  EnumDeclaration(Loc loc, Identifier ident, Type memtype) {
            super(loc, ident);
            this.type = new TypeEnum(this);
            this.memtype.value = memtype;
            this.protection = new Prot(Prot.Kind.undefined);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            EnumDeclaration ed = new EnumDeclaration(this.loc.value, this.ident.value, this.memtype.value != null ? this.memtype.value.syntaxCopy() : null);
            return this.syntaxCopy(ed);
        }

        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sds) {
            ScopeDsymbol scopesym = this.isAnonymous() ? sds : this;
            if (!this.isAnonymous())
            {
                this.addMember(sc, sds);
                if (this.symtab == null)
                    this.symtab = new DsymbolTable();
            }
            if (this.members.value != null)
            {
                {
                    int i = 0;
                    for (; (i < (this.members.value.get()).length.value);i++){
                        EnumMember em = (this.members.value.get()).get(i).isEnumMember();
                        em.ed = this;
                        em.addMember(sc, this.isAnonymous() ? scopesym : this);
                    }
                }
            }
            this.added = true;
        }

        public  void setScope(Ptr<Scope> sc) {
            if ((this.semanticRun.value > PASS.init))
                return ;
            this.setScope(sc);
        }

        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            if (this.isAnonymous())
                return Dsymbol.oneMembers(this.members.value, ps, ident);
            return this.oneMember(ps, ident);
        }

        public  Type getType() {
            return this.type;
        }

        public  BytePtr kind() {
            return new BytePtr("enum");
        }

        public  Dsymbol search(Loc loc, Identifier ident, int flags) {
            if (this._scope.value != null)
            {
                dsymbolSemantic(this, this._scope.value);
            }
            if ((this.members.value == null) || (this.symtab == null) || (this._scope.value != null))
            {
                this.error(new BytePtr("is forward referenced when looking for `%s`"), ident.toChars());
                return null;
            }
            Dsymbol s = this.search(loc, ident, flags);
            return s;
        }

        // defaulted all parameters starting with #3
        public  Dsymbol search(Loc loc, Identifier ident) {
            return search(loc, ident, 8);
        }

        public  boolean isDeprecated() {
            return this.isdeprecated;
        }

        public  Prot prot() {
            return this.protection;
        }

        public  Expression getMaxMinValue(Loc loc, Identifier id) {
            Function2<Expression,Loc,Expression> pvalToResult = new Function2<Expression,Loc,Expression>(){
                public Expression invoke(Expression e, Loc loc) {
                    Ref<Expression> e_ref = ref(e);
                    Ref<Loc> loc_ref = ref(loc);
                    if (((e_ref.value.op.value & 0xFF) != 127))
                    {
                        e_ref.value = e_ref.value.copy();
                        e_ref.value.loc.value = loc_ref.value.copy();
                    }
                    return e_ref.value;
                }
            };
            Ref<Ptr<Expression>> pval = ref(pcopy((pequals(id, Id.max.value)) ? ptr(this.maxval) : ptr(this.minval)));
            Function0<Expression> errorReturn = new Function0<Expression>(){
                public Expression invoke() {
                    pval.value.set(0, (new ErrorExp()));
                    return pval.value.get();
                }
            };
            if (this.inuse != 0)
            {
                this.error(loc, new BytePtr("recursive definition of `.%s` property"), id.toChars());
                return errorReturn.invoke();
            }
            if (pval.value.get() != null)
                return pvalToResult.invoke(pval.value.get(), loc);
            if (this._scope.value != null)
                dsymbolSemantic(this, this._scope.value);
            if (this.errors.value)
                return errorReturn.invoke();
            if ((this.semanticRun.value == PASS.init) || (this.members.value == null))
            {
                if (this.isSpecial())
                {
                    return getProperty(this.memtype.value, loc, id, 0);
                }
                this.error(new BytePtr("is forward referenced looking for `.%s`"), id.toChars());
                return errorReturn.invoke();
            }
            if (!((this.memtype.value != null) && this.memtype.value.isintegral()))
            {
                this.error(loc, new BytePtr("has no `.%s` property because base type `%s` is not an integral type"), id.toChars(), this.memtype.value != null ? this.memtype.value.toChars() : new BytePtr(""));
                return errorReturn.invoke();
            }
            boolean first = true;
            {
                int i = 0;
                for (; (i < (this.members.value.get()).length.value);i++){
                    EnumMember em = (this.members.value.get()).get(i).isEnumMember();
                    if (em == null)
                        continue;
                    if (em.errors.value)
                    {
                        this.errors.value = true;
                        continue;
                    }
                    if (first)
                    {
                        pval.value.set(0, em.value());
                        first = false;
                    }
                    else
                    {
                        Expression e = em.value();
                        Expression ec = new CmpExp((pequals(id, Id.max.value)) ? TOK.greaterThan : TOK.lessThan, em.loc.value, e, pval.value.get());
                        this.inuse++;
                        ec = expressionSemantic(ec, em._scope.value);
                        this.inuse--;
                        ec = ec.ctfeInterpret();
                        if (((ec.op.value & 0xFF) == 127))
                        {
                            this.errors.value = true;
                            continue;
                        }
                        if (ec.toInteger() != 0)
                            pval.value.set(0, e);
                    }
                }
            }
            return this.errors.value ? errorReturn.invoke() : pvalToResult.invoke(pval.value.get(), loc);
        }

        public  boolean isSpecial() {
            return isSpecialEnumIdent(this.ident.value) && (this.memtype.value != null);
        }

        public  Expression getDefaultValue(Loc loc) {
            Function0<Expression> handleErrors = new Function0<Expression>(){
                public Expression invoke() {
                    defaultval.value = new ErrorExp();
                    return defaultval.value;
                }
            };
            if (this.defaultval.value != null)
                return this.defaultval.value;
            if (this._scope.value != null)
                dsymbolSemantic(this, this._scope.value);
            if (this.errors.value)
                return handleErrors.invoke();
            if ((this.semanticRun.value == PASS.init) || (this.members.value == null))
            {
                if (this.isSpecial())
                {
                    return defaultInit(this.memtype.value, loc);
                }
                this.error(loc, new BytePtr("forward reference of `%s.init`"), this.toChars());
                return handleErrors.invoke();
            }
            {
                int __key925 = 0;
                int __limit926 = (this.members.value.get()).length.value;
                for (; (__key925 < __limit926);__key925 += 1) {
                    int i = __key925;
                    EnumMember em = (this.members.value.get()).get(i).isEnumMember();
                    if (em != null)
                    {
                        this.defaultval.value = em.value();
                        return this.defaultval.value;
                    }
                }
            }
            return handleErrors.invoke();
        }

        public  Type getMemtype(Loc loc) {
            if (this._scope.value != null)
            {
                if (this.memtype.value != null)
                {
                    Loc locx = loc.isValid() ? loc : this.loc.value.copy();
                    this.memtype.value = typeSemantic(this.memtype.value, locx, this._scope.value);
                }
                else
                {
                    if (!this.isAnonymous() && (this.members.value != null))
                        this.memtype.value = Type.tint32.value;
                }
            }
            if (this.memtype.value == null)
            {
                if (!this.isAnonymous() && (this.members.value != null))
                    this.memtype.value = Type.tint32.value;
                else
                {
                    Loc locx = loc.isValid() ? loc : this.loc.value.copy();
                    this.error(locx, new BytePtr("is forward referenced looking for base type"));
                    return Type.terror.value;
                }
            }
            return this.memtype.value;
        }

        public  EnumDeclaration isEnumDeclaration() {
            return this;
        }

        public Ptr<Symbol> sinit = null;
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public EnumDeclaration() {}

        public EnumDeclaration copy() {
            EnumDeclaration that = new EnumDeclaration();
            that.type = this.type;
            that.memtype = this.memtype;
            that.protection = this.protection;
            that.maxval = this.maxval;
            that.minval = this.minval;
            that.defaultval = this.defaultval;
            that.isdeprecated = this.isdeprecated;
            that.added = this.added;
            that.inuse = this.inuse;
            that.sinit = this.sinit;
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
    public static class EnumMember extends VarDeclaration
    {
        public  Expression value() {
            return ((ExpInitializer)this._init.value).exp.value;
        }

        public Expression origValue = null;
        public Type origType = null;
        public EnumDeclaration ed = null;
        public  EnumMember(Loc loc, Identifier id, Expression value, Type origType) {
            super(loc, null, id != null ? id : Id.empty.value, new ExpInitializer(loc, value), 0L);
            this.origValue = value;
            this.origType = origType;
        }

        public  EnumMember(Loc loc, Identifier id, Expression value, Type memtype, long stc, UserAttributeDeclaration uad, DeprecatedDeclaration dd) {
            this(loc, id, value, memtype);
            this.storage_class.value = stc;
            this.userAttribDecl = uad;
            this.depdecl = dd;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new EnumMember(this.loc.value, this.ident.value, this.value() != null ? this.value().syntaxCopy() : null, this.origType != null ? this.origType.syntaxCopy() : null, this.storage_class.value, this.userAttribDecl != null ? (UserAttributeDeclaration)this.userAttribDecl.syntaxCopy(s) : null, this.depdecl != null ? (DeprecatedDeclaration)this.depdecl.syntaxCopy(s) : null);
        }

        public  BytePtr kind() {
            return new BytePtr("enum member");
        }

        public  Expression getVarExp(Loc loc, Ptr<Scope> sc) {
            dsymbolSemantic(this, sc);
            if (this.errors.value)
                return new ErrorExp();
            this.checkDisabled(loc, sc, false);
            if ((this.depdecl != null) && (this.depdecl._scope.value == null))
                this.depdecl._scope.value = sc;
            this.checkDeprecated(loc, sc);
            if (this.errors.value)
                return new ErrorExp();
            Expression e = new VarExp(loc, this, true);
            return expressionSemantic(e, sc);
        }

        public  EnumMember isEnumMember() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public EnumMember() {}

        public EnumMember copy() {
            EnumMember that = new EnumMember();
            that.origValue = this.origValue;
            that.origType = this.origType;
            that.ed = this.ed;
            that._init = this._init;
            that.offset = this.offset;
            that.sequenceNumber = this.sequenceNumber;
            that.nestedrefs = this.nestedrefs;
            that.alignment = this.alignment;
            that.isargptr = this.isargptr;
            that.ctorinit = this.ctorinit;
            that.iscatchvar = this.iscatchvar;
            that.onstack = this.onstack;
            that.mynew = this.mynew;
            that.canassign = this.canassign;
            that.overlapped = this.overlapped;
            that.overlapUnsafe = this.overlapUnsafe;
            that.doNotInferScope = this.doNotInferScope;
            that.isdataseg = this.isdataseg;
            that.aliassym = this.aliassym;
            that.lastVar = this.lastVar;
            that.endlinnum = this.endlinnum;
            that.ctfeAdrOnStack = this.ctfeAdrOnStack;
            that.edtor = this.edtor;
            that.range = this.range;
            that.maybes = this.maybes;
            that._isAnonymous = this._isAnonymous;
            that.type = this.type;
            that.originalType = this.originalType;
            that.storage_class = this.storage_class;
            that.protection = this.protection;
            that.linkage = this.linkage;
            that.inuse = this.inuse;
            that.mangleOverride = this.mangleOverride;
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
    public static boolean isSpecialEnumIdent(Identifier ident) {
        return (pequals(ident, Id.__c_long)) || (pequals(ident, Id.__c_ulong)) || (pequals(ident, Id.__c_longlong)) || (pequals(ident, Id.__c_ulonglong)) || (pequals(ident, Id.__c_long_double)) || (pequals(ident, Id.__c_wchar_t));
    }

}
