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
        public Type memtype = null;
        public Prot protection = new Prot();
        public Ref<Expression> maxval = ref(null);
        public Ref<Expression> minval = ref(null);
        public Expression defaultval = null;
        public boolean isdeprecated = false;
        public boolean added = false;
        public int inuse = 0;
        // Erasure: __ctor<Loc, Identifier, Type>
        public  EnumDeclaration(Loc loc, Identifier ident, Type memtype) {
            super(loc, ident);
            this.type = new TypeEnum(this);
            this.memtype = memtype;
            this.protection = new Prot(Prot.Kind.undefined);
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            EnumDeclaration ed = new EnumDeclaration(this.loc, this.ident, this.memtype != null ? this.memtype.syntaxCopy() : null);
            return this.syntaxCopy(ed);
        }

        // Erasure: addMember<Ptr, ScopeDsymbol>
        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sds) {
            ScopeDsymbol scopesym = this.isAnonymous() ? sds : this;
            if (!this.isAnonymous())
            {
                this.addMember(sc, sds);
                if (this.symtab == null)
                {
                    this.symtab = new DsymbolTable();
                }
            }
            if (this.members != null)
            {
                {
                    int i = 0;
                    for (; (i < (this.members.get()).length);i++){
                        EnumMember em = (this.members.get()).get(i).isEnumMember();
                        em.ed = this;
                        em.addMember(sc, this.isAnonymous() ? scopesym : this);
                    }
                }
            }
            this.added = true;
        }

        // Erasure: setScope<Ptr>
        public  void setScope(Ptr<Scope> sc) {
            if ((this.semanticRun > PASS.init))
            {
                return ;
            }
            this.setScope(sc);
        }

        // Erasure: oneMember<Ptr, Identifier>
        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            if (this.isAnonymous())
            {
                return Dsymbol.oneMembers(this.members, ps, ident);
            }
            return this.oneMember(ps, ident);
        }

        // Erasure: getType<>
        public  Type getType() {
            return this.type;
        }

        // Erasure: kind<>
        public  BytePtr kind() {
            return new BytePtr("enum");
        }

        // Erasure: search<Loc, Identifier, int>
        public  Dsymbol search(Loc loc, Identifier ident, int flags) {
            if (this._scope != null)
            {
                dsymbolSemantic(this, this._scope);
            }
            if ((this.members == null) || (this.symtab == null) || (this._scope != null))
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

        // Erasure: isDeprecated<>
        public  boolean isDeprecated() {
            return this.isdeprecated;
        }

        // Erasure: prot<>
        public  Prot prot() {
            return this.protection;
        }

        // Erasure: getMaxMinValue<Loc, Identifier>
        public  Expression getMaxMinValue(Loc loc, Identifier id) {
            Function2<Expression,Ref<Loc>,Expression> pvalToResult = new Function2<Expression,Ref<Loc>,Expression>() {
                public Expression invoke(Expression e, Loc loc) {
                 {
                    Ref<Expression> e_ref = ref(e);
                    if (((e_ref.value.op & 0xFF) != 127))
                    {
                        e_ref.value = e_ref.value.copy();
                        e_ref.value.loc.opAssign(loc.copy());
                    }
                    return e_ref.value;
                }}

            };
            Ptr<Expression> pval = pcopy((pequals(id, Id.max)) ? ptr(this.maxval) : ptr(this.minval));
            Function0<Expression> errorReturn = new Function0<Expression>() {
                public Expression invoke() {
                 {
                    pval.set(0, (new ErrorExp()));
                    return pval.get();
                }}

            };
            if (this.inuse != 0)
            {
                this.error(loc, new BytePtr("recursive definition of `.%s` property"), id.toChars());
                return errorReturn.invoke();
            }
            if (pval.get() != null)
            {
                return pvalToResult.invoke(pval.get(), loc);
            }
            if (this._scope != null)
            {
                dsymbolSemantic(this, this._scope);
            }
            if (this.errors)
            {
                return errorReturn.invoke();
            }
            if ((this.semanticRun == PASS.init) || (this.members == null))
            {
                if (this.isSpecial())
                {
                    return getProperty(this.memtype, loc, id, 0);
                }
                this.error(new BytePtr("is forward referenced looking for `.%s`"), id.toChars());
                return errorReturn.invoke();
            }
            if (!((this.memtype != null) && this.memtype.isintegral()))
            {
                this.error(loc, new BytePtr("has no `.%s` property because base type `%s` is not an integral type"), id.toChars(), this.memtype != null ? this.memtype.toChars() : new BytePtr(""));
                return errorReturn.invoke();
            }
            boolean first = true;
            {
                int i = 0;
                for (; (i < (this.members.get()).length);i++){
                    EnumMember em = (this.members.get()).get(i).isEnumMember();
                    if (em == null)
                    {
                        continue;
                    }
                    if (em.errors)
                    {
                        this.errors = true;
                        continue;
                    }
                    if (first)
                    {
                        pval.set(0, em.value());
                        first = false;
                    }
                    else
                    {
                        Expression e = em.value();
                        Expression ec = new CmpExp((pequals(id, Id.max)) ? TOK.greaterThan : TOK.lessThan, em.loc, e, pval.get());
                        this.inuse++;
                        ec = expressionSemantic(ec, em._scope);
                        this.inuse--;
                        ec = ec.ctfeInterpret();
                        if (((ec.op & 0xFF) == 127))
                        {
                            this.errors = true;
                            continue;
                        }
                        if (ec.toInteger() != 0)
                        {
                            pval.set(0, e);
                        }
                    }
                }
            }
            return this.errors ? errorReturn.invoke() : pvalToResult.invoke(pval.get(), loc);
        }

        // Erasure: isSpecial<>
        public  boolean isSpecial() {
            return isSpecialEnumIdent(this.ident) && (this.memtype != null);
        }

        // Erasure: getDefaultValue<Loc>
        public  Expression getDefaultValue(Loc loc) {
            Function0<Expression> handleErrors = new Function0<Expression>() {
                public Expression invoke() {
                 {
                    defaultval = new ErrorExp();
                    return defaultval;
                }}

            };
            if (this.defaultval != null)
            {
                return this.defaultval;
            }
            if (this._scope != null)
            {
                dsymbolSemantic(this, this._scope);
            }
            if (this.errors)
            {
                return handleErrors.invoke();
            }
            if ((this.semanticRun == PASS.init) || (this.members == null))
            {
                if (this.isSpecial())
                {
                    return defaultInit(this.memtype, loc);
                }
                this.error(loc, new BytePtr("forward reference of `%s.init`"), this.toChars());
                return handleErrors.invoke();
            }
            {
                int __key941 = 0;
                int __limit942 = (this.members.get()).length;
                for (; (__key941 < __limit942);__key941 += 1) {
                    int i = __key941;
                    EnumMember em = (this.members.get()).get(i).isEnumMember();
                    if (em != null)
                    {
                        this.defaultval = em.value();
                        return this.defaultval;
                    }
                }
            }
            return handleErrors.invoke();
        }

        // Erasure: getMemtype<Loc>
        public  Type getMemtype(Loc loc) {
            if (this._scope != null)
            {
                if (this.memtype != null)
                {
                    Loc locx = loc.isValid() ? loc : this.loc.copy();
                    this.memtype = typeSemantic(this.memtype, locx, this._scope);
                }
                else
                {
                    if (!this.isAnonymous() && (this.members != null))
                    {
                        this.memtype = Type.tint32;
                    }
                }
            }
            if (this.memtype == null)
            {
                if (!this.isAnonymous() && (this.members != null))
                {
                    this.memtype = Type.tint32;
                }
                else
                {
                    Loc locx = loc.isValid() ? loc : this.loc.copy();
                    this.error(locx, new BytePtr("is forward referenced looking for base type"));
                    return Type.terror;
                }
            }
            return this.memtype;
        }

        // Erasure: isEnumDeclaration<>
        public  EnumDeclaration isEnumDeclaration() {
            return this;
        }

        public Ptr<Symbol> sinit = null;
        // Erasure: accept<Visitor>
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
        // Erasure: value<>
        public  Expression value() {
            return ((ExpInitializer)this._init).exp;
        }

        public Expression origValue = null;
        public Type origType = null;
        public EnumDeclaration ed = null;
        // Erasure: __ctor<Loc, Identifier, Expression, Type>
        public  EnumMember(Loc loc, Identifier id, Expression value, Type origType) {
            super(loc, null, id != null ? id : Id.empty, new ExpInitializer(loc, value), 0L);
            this.origValue = value;
            this.origType = origType;
        }

        // Erasure: __ctor<Loc, Identifier, Expression, Type, long, UserAttributeDeclaration, DeprecatedDeclaration>
        public  EnumMember(Loc loc, Identifier id, Expression value, Type memtype, long stc, UserAttributeDeclaration uad, DeprecatedDeclaration dd) {
            this(loc, id, value, memtype);
            this.storage_class = stc;
            this.userAttribDecl = uad;
            this.depdecl = dd;
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            return new EnumMember(this.loc, this.ident, this.value() != null ? this.value().syntaxCopy() : null, this.origType != null ? this.origType.syntaxCopy() : null, this.storage_class, this.userAttribDecl != null ? (UserAttributeDeclaration)this.userAttribDecl.syntaxCopy(s) : null, this.depdecl != null ? (DeprecatedDeclaration)this.depdecl.syntaxCopy(s) : null);
        }

        // Erasure: kind<>
        public  BytePtr kind() {
            return new BytePtr("enum member");
        }

        // Erasure: getVarExp<Loc, Ptr>
        public  Expression getVarExp(Loc loc, Ptr<Scope> sc) {
            dsymbolSemantic(this, sc);
            if (this.errors)
            {
                return new ErrorExp();
            }
            this.checkDisabled(loc, sc, false);
            if ((this.depdecl != null) && (this.depdecl._scope == null))
            {
                this.depdecl._scope = pcopy(sc);
            }
            this.checkDeprecated(loc, sc);
            if (this.errors)
            {
                return new ErrorExp();
            }
            Expression e = new VarExp(loc, this, true);
            return expressionSemantic(e, sc);
        }

        // Erasure: isEnumMember<>
        public  EnumMember isEnumMember() {
            return this;
        }

        // Erasure: accept<Visitor>
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
    // Erasure: isSpecialEnumIdent<Identifier>
    public static boolean isSpecialEnumIdent(Identifier ident) {
        return (pequals(ident, Id.__c_long)) || (pequals(ident, Id.__c_ulong)) || (pequals(ident, Id.__c_longlong)) || (pequals(ident, Id.__c_ulonglong)) || (pequals(ident, Id.__c_long_double)) || (pequals(ident, Id.__c_wchar_t));
    }

}
