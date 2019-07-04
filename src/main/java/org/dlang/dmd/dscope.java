package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.ctorflow.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.statement.*;

public class dscope {


    public static class SCOPE 
    {
        public static final int ctor = 1;
        public static final int noaccesscheck = 2;
        public static final int condition = 4;
        public static final int debug_ = 8;
        public static final int constraint = 16;
        public static final int invariant_ = 32;
        public static final int require = 64;
        public static final int ensure = 96;
        public static final int contract = 96;
        public static final int ctfe = 128;
        public static final int compile = 256;
        public static final int ignoresymbolvisibility = 512;
        public static final int onlysafeaccess = 1024;
        public static final int free = 32768;
        public static final int fullinst = 65536;
    }

    static int SCOPEpush = 2042;
    public static class Scope
    {
        public Scope enclosing;
        public Module _module;
        public ScopeDsymbol scopesym;
        public FuncDeclaration func;
        public Dsymbol parent;
        public LabelStatement slabel;
        public SwitchStatement sw;
        public TryFinallyStatement tf;
        public ScopeGuardStatement os;
        public Statement sbreak;
        public Statement scontinue;
        public ForeachStatement fes;
        public Scope callsc;
        public Dsymbol inunion;
        public boolean nofree;
        public boolean inLoop;
        public int intypeof;
        public VarDeclaration lastVar;
        public Module minst;
        public TemplateInstance tinst;
        public CtorFlow ctorflow = new CtorFlow();
        public AlignDeclaration aligndecl;
        public CPPNamespaceDeclaration namespace;
        public int linkage = LINK.d;
        public int cppmangle = CPPMANGLE.def;
        public int inlining = PINLINE.default_;
        public Prot protection = new Prot(Prot.Kind.public_, null);
        public int explicitProtection;
        public long stc;
        public DeprecatedDeclaration depdecl;
        public int flags;
        public UserAttributeDeclaration userAttribDecl;
        public DocComment lastdc;
        public AA<Object,Integer> anchorCounts = new AA<Object,Integer>();
        public Identifier prevAnchor;
        public static Scope freelist;
        public static Scope alloc() {
            if (freelist != null)
            {
                Scope s = freelist;
                freelist = (s).enclosing;
                assert(((s).flags & 32768) != 0);
                (s).flags &= -32769;
                return s;
            }
            return new Scope(null, null, null, null, null, null, null, null, null, null, null, null, null, null, false, false, 0, null, null, null, new CtorFlow(CSX.none, new Slice<FieldInit>()), null, null, LINK.d, CPPMANGLE.def, PINLINE.default_, new Prot(Prot.Kind.public_, null), 0, 0L, null, 0, null, null, null, null);
        }

        public static Scope createGlobal(Module _module) {
            Scope sc = alloc();
            sc.opAssign(new Scope(null, null, null, null, null, null, null, null, null, null, null, null, null, null, false, false, 0, null, null, null, new CtorFlow(CSX.none, new Slice<FieldInit>()), null, null, LINK.d, CPPMANGLE.def, PINLINE.default_, new Prot(Prot.Kind.public_, null), 0, 0L, null, 0, null, null, null, null));
            (sc)._module = _module;
            (sc).minst = _module;
            (sc).scopesym = new ScopeDsymbol();
            (sc).scopesym.symtab = new DsymbolTable();
            Dsymbol m = _module;
            for (; m.parent != null;) {
                m = m.parent;
            }
            m.addMember(null, (sc).scopesym);
            m.parent = null;
            sc = (sc).push(_module);
            (sc).parent = _module;
            return sc;
        }

        public  Scope push() {
            Scope s = this.copy();
            assert(!((this.flags & 32768) != 0));
            (s).scopesym = null;
            (s).enclosing = this;
            (s).slabel = null;
            (s).nofree = false;
            (s).ctorflow.fieldinit = arraydup(this.ctorflow.fieldinit).copy();
            (s).flags = this.flags & 2042;
            (s).lastdc = null;
            assert(this != s);
            return s;
        }

        public  Scope push(ScopeDsymbol ss) {
            Scope s = this.push();
            (s).scopesym = ss;
            return s;
        }

        public  Scope pop() {
            if (this.enclosing != null)
                (this.enclosing).ctorflow.OR(this.ctorflow);
            this.ctorflow.freeFieldinit();
            Scope enc = this.enclosing;
            if (!(this.nofree))
            {
                this.enclosing = freelist;
                freelist = this;
                this.flags |= 32768;
            }
            return enc;
        }

        public  void detach() {
            this.ctorflow.freeFieldinit();
            this.enclosing = null;
            this.pop();
        }

        public  Scope startCTFE() {
            Scope sc = this.push();
            (sc).flags = this.flags | 128;
            return sc;
        }

        public  Scope endCTFE() {
            assert((this.flags & 128) != 0);
            return this.pop();
        }

        public  void merge(Loc loc, CtorFlow ctorflow) {
            if (!(mergeCallSuper(this.ctorflow.callSuper, ctorflow.callSuper)))
                error(loc, new BytePtr("one path skips constructor"));
            Slice<FieldInit> fies = ctorflow.fieldinit.copy();
            if (((this.ctorflow.fieldinit.getLength()) != 0 && (fies.getLength()) != 0))
            {
                FuncDeclaration f = this.func;
                if (this.fes != null)
                    f = this.fes.func;
                AggregateDeclaration ad = f.isMemberDecl();
                assert(ad != null);
                {
                    Slice<VarDeclaration> __r1080 = ad.fields.opSlice().copy();
                    int __key1079 = 0;
                    for (; __key1079 < __r1080.getLength();__key1079 += 1) {
                        VarDeclaration v = __r1080.get(__key1079);
                        int i = __key1079;
                        boolean mustInit = ((v.storage_class & 549755813888L) != 0 || v.type.needsNested());
                        FieldInit fieldInit = this.ctorflow.fieldinit.get(i);
                        FieldInit fiesCurrent = fies.get(i).copy();
                        if ((fieldInit).loc == new Loc(null, 0, 0))
                            (fieldInit).loc = fiesCurrent.loc.copy();
                        if ((!(mergeFieldInit(this.ctorflow.fieldinit.get(i).csx, fiesCurrent.csx)) && mustInit))
                        {
                            error(loc, new BytePtr("one path skips field `%s`"), v.toChars());
                        }
                    }
                }
            }
        }

        public  Module instantiatingModule() {
            return this.minst != null ? this.minst : this._module;
        }

        public  Dsymbol search(Loc loc, Identifier ident, Ptr<Dsymbol> pscopesym, int flags) {
            Ref<Identifier> ident_ref = ref(ident);
            Ref<Ptr<Dsymbol>> pscopesym_ref = ref(pscopesym);
            assert(!((flags & 24) != 0));
            if (pequals(ident_ref.value, Id.empty))
            {
                {
                    Scope sc = this;
                    for (; sc != null;sc = (sc).enclosing){
                        assert(sc != (sc).enclosing);
                        if (!((sc).scopesym != null))
                            continue;
                        {
                            Dsymbol s = (sc).scopesym.isModule();
                            if (s != null)
                            {
                                if (pscopesym_ref.value != null)
                                    pscopesym_ref.value.set(0, (sc).scopesym);
                                return s;
                            }
                        }
                    }
                }
                return null;
            }
            Function4<AggregateDeclaration,Identifier,Integer,Ptr<Expression>,Dsymbol> checkAliasThis = new Function4<AggregateDeclaration,Identifier,Integer,Ptr<Expression>,Dsymbol>(){
                public Dsymbol invoke(AggregateDeclaration ad, Identifier ident, Integer flags, Ptr<Expression> exp){
                    if ((!(ad != null) || !(ad.aliasthis != null)))
                        return null;
                    Declaration decl = ad.aliasthis.isDeclaration();
                    if (!(decl != null))
                        return null;
                    Type t = decl.type;
                    ScopeDsymbol sds = null;
                    TypeClass tc = null;
                    TypeStruct ts = null;
                    switch ((t.ty & 0xFF))
                    {
                        case 8:
                            ts = (TypeStruct)t;
                            sds = ts.sym;
                            break;
                        case 7:
                            tc = (TypeClass)t;
                            sds = tc.sym;
                            break;
                        case 35:
                            sds = ((TypeInstance)t).tempinst;
                            break;
                        case 9:
                            sds = ((TypeEnum)t).sym;
                            break;
                        default:
                        break;
                    }
                    if (!(sds != null))
                        return null;
                    Dsymbol ret = sds.search(loc, ident, flags);
                    if (ret != null)
                    {
                        exp.set(0, (new DotIdExp(loc, exp.get(), ad.aliasthis.ident)));
                        exp.set(0, (new DotIdExp(loc, exp.get(), ident)));
                        return ret;
                    }
                    if ((!(ts != null) && !(tc != null)))
                        return null;
                    Dsymbol s = null;
                    exp.set(0, (new DotIdExp(loc, exp.get(), ad.aliasthis.ident)));
                    if ((ts != null && !((ts.att & AliasThisRec.tracing) != 0)))
                    {
                        ts.att = ts.att | AliasThisRec.tracing;
                        s = checkAliasThis.invoke(sds.isAggregateDeclaration(), ident, flags, exp);
                        ts.att = ts.att & -5;
                    }
                    else if ((tc != null && !((tc.att & AliasThisRec.tracing) != 0)))
                    {
                        tc.att = tc.att | AliasThisRec.tracing;
                        s = checkAliasThis.invoke(sds.isAggregateDeclaration(), ident, flags, exp);
                        tc.att = tc.att & -5;
                    }
                    return s;
                }
            };
            Function1<Integer,Dsymbol> searchScopes = new Function1<Integer,Dsymbol>(){
                public Dsymbol invoke(Integer flags){
                    {
                        Scope sc = this;
                        for (; sc != null;sc = (sc).enclosing){
                            assert(sc != (sc).enclosing);
                            if (!((sc).scopesym != null))
                                continue;
                            if ((sc).scopesym.isModule() != null)
                                flags |= 32;
                            {
                                Dsymbol s = (sc).scopesym.search(loc, ident_ref.value, flags);
                                if (s != null)
                                {
                                    if (((((!((flags & 18) != 0) && pequals(ident_ref.value, Id.length)) && (sc).scopesym.isArrayScopeSymbol() != null) && (sc).enclosing != null) && ((sc).enclosing).search(loc, ident_ref.value, null, flags) != null))
                                    {
                                        warning(s.loc, new BytePtr("array `length` hides other `length` name in outer scope"));
                                    }
                                    if (pscopesym_ref.value != null)
                                        pscopesym_ref.value.set(0, (sc).scopesym);
                                    return s;
                                }
                            }
                            if (global.params.fixAliasThis)
                            {
                                Ref<Expression> exp = ref(new ThisExp(loc));
                                Dsymbol aliasSym = checkAliasThis.invoke((sc).scopesym.isAggregateDeclaration(), ident_ref.value, flags, ptr(exp));
                                if (aliasSym != null)
                                {
                                    if (pscopesym_ref.value != null)
                                        pscopesym_ref.value.set(0, (new ExpressionDsymbol(exp.value)));
                                    return aliasSym;
                                }
                            }
                            if (((sc).scopesym.isModule() != null && !(((sc).enclosing != null && ((sc).enclosing).enclosing == null))))
                                break;
                        }
                    }
                    return null;
                }
            };
            if ((this.flags & 512) != 0)
                flags |= 128;
            Dsymbol s = searchScopes.invoke(flags | 8);
            if (!(s != null))
            {
                s = searchScopes.invoke(flags | 16);
            }
            return s;
        }

        public  Dsymbol search_correct(Identifier ident) {
            if ((global.gag) != 0)
                return null;
            Function2<ByteSlice,Integer,Dsymbol> scope_search_fp = new Function2<ByteSlice,Integer,Dsymbol>(){
                public Dsymbol invoke(ByteSlice seed, IntRef cost){
                    if (!((seed.getLength()) != 0))
                        return null;
                    Identifier id = Identifier.lookup(seed);
                    if (!(id != null))
                        return null;
                    Scope sc = this;
                    Module.clearCache();
                    Ref<Dsymbol> scopesym = ref(null);
                    Dsymbol s = (sc).search(Loc.initial, id, ptr(scopesym), 2);
                    if (s != null)
                    {
                        {
                            cost.value = 0;
                            for (; sc != null;comma(sc = (sc).enclosing, cost.value += 1)) {
                                if (pequals((sc).scopesym, scopesym.value))
                                    break;
                            }
                        }
                        if (!pequals(scopesym.value, s.parent))
                        {
                            cost.value += 1;
                            if (s.prot().kind == Prot.Kind.private_)
                                return null;
                        }
                    }
                    return s;
                }
            };
            Ref<Dsymbol> scopesym = ref(null);
            {
                Dsymbol s = this.search(Loc.initial, ident, ptr(scopesym), 2);
                if (s != null)
                    return s;
            }
            return speller.invoke(ident.asString());
        }

        public static BytePtr search_correct_C(Identifier ident) {
            byte tok = TOK.reserved;
            if (pequals(ident, Id.NULL))
                tok = TOK.null_;
            else if (pequals(ident, Id.TRUE))
                tok = TOK.true_;
            else if (pequals(ident, Id.FALSE))
                tok = TOK.false_;
            else if (pequals(ident, Id.unsigned))
                tok = TOK.uns32;
            else if (pequals(ident, Id.wchar_t))
                tok = global.params.isWindows ? TOK.wchar_ : TOK.dchar_;
            else
                return null;
            return Token.toChars(tok);
        }

        public  Dsymbol insert(Dsymbol s) {
            {
                VarDeclaration vd = s.isVarDeclaration();
                if (vd != null)
                {
                    if (this.lastVar != null)
                        vd.lastVar = this.lastVar;
                    this.lastVar = vd;
                }
                else {
                    WithScopeSymbol ss = s.isWithScopeSymbol();
                    if (ss != null)
                    {
                        {
                            VarDeclaration vd = ss.withstate.wthis;
                            if (vd != null)
                            {
                                if (this.lastVar != null)
                                    vd.lastVar = this.lastVar;
                                this.lastVar = vd;
                            }
                        }
                        return null;
                    }
                }
            }
            {
                Scope sc = this;
                for (; sc != null;sc = (sc).enclosing){
                    if ((sc).scopesym != null)
                    {
                        if (!((sc).scopesym.symtab != null))
                            (sc).scopesym.symtab = new DsymbolTable();
                        return (sc).scopesym.symtabInsert(s);
                    }
                }
            }
            throw new AssertionError("Unreachable code!");
        }

        public  ClassDeclaration getClassScope() {
            {
                Scope sc = this;
                for (; sc != null;sc = (sc).enclosing){
                    if (!((sc).scopesym != null))
                        continue;
                    ClassDeclaration cd = (sc).scopesym.isClassDeclaration();
                    if (cd != null)
                        return cd;
                }
            }
            return null;
        }

        public  AggregateDeclaration getStructClassScope() {
            {
                Scope sc = this;
                for (; sc != null;sc = (sc).enclosing){
                    if (!((sc).scopesym != null))
                        continue;
                    AggregateDeclaration ad = (sc).scopesym.isClassDeclaration();
                    if (ad != null)
                        return ad;
                    ad = (sc).scopesym.isStructDeclaration();
                    if (ad != null)
                        return ad;
                }
            }
            return null;
        }

        public  void setNoFree() {
            {
                Scope sc = this;
                for (; sc != null;sc = (sc).enclosing){
                    (sc).nofree = true;
                    assert(!((this.flags & 32768) != 0));
                }
            }
        }

        public  Scope(Scope sc) {
            this._module = sc._module;
            this.scopesym = sc.scopesym;
            this.enclosing = sc.enclosing;
            this.parent = sc.parent;
            this.sw = sc.sw;
            this.tf = sc.tf;
            this.os = sc.os;
            this.tinst = sc.tinst;
            this.minst = sc.minst;
            this.sbreak = sc.sbreak;
            this.scontinue = sc.scontinue;
            this.fes = sc.fes;
            this.callsc = sc.callsc;
            this.aligndecl = sc.aligndecl;
            this.func = sc.func;
            this.slabel = sc.slabel;
            this.linkage = sc.linkage;
            this.cppmangle = sc.cppmangle;
            this.inlining = sc.inlining;
            this.protection = sc.protection.copy();
            this.explicitProtection = sc.explicitProtection;
            this.stc = sc.stc;
            this.depdecl = sc.depdecl;
            this.inunion = sc.inunion;
            this.nofree = sc.nofree;
            this.inLoop = sc.inLoop;
            this.intypeof = sc.intypeof;
            this.lastVar = sc.lastVar;
            this.ctorflow = sc.ctorflow.copy();
            this.flags = sc.flags;
            this.lastdc = sc.lastdc;
            this.anchorCounts = sc.anchorCounts;
            this.prevAnchor = sc.prevAnchor;
            this.userAttribDecl = sc.userAttribDecl;
        }

        public  int alignment() {
            if (this.aligndecl != null)
                return getAlignment(this.aligndecl, this);
            else
                return -1;
        }

        public  boolean isDeprecated() {
            {
                Ptr<Dsymbol> sp = pcopy(this.parent);
                for (; sp.get() != null;sp = pcopy(((sp.get()).parent))){
                    if ((sp.get()).isDeprecated())
                        return true;
                }
            }
            {
                Scope sc2 = this;
                for (; sc2 != null;sc2 = (sc2).enclosing){
                    if (((sc2).scopesym != null && (sc2).scopesym.isDeprecated()))
                        return true;
                    if (((sc2).stc & 1024L) != 0)
                        return true;
                }
            }
            return false;
        }

        public Scope(){
            ctorflow = new CtorFlow();
            protection = new Prot();
        }
        public Scope copy(){
            Scope r = new Scope();
            r.enclosing = enclosing;
            r._module = _module;
            r.scopesym = scopesym;
            r.func = func;
            r.parent = parent;
            r.slabel = slabel;
            r.sw = sw;
            r.tf = tf;
            r.os = os;
            r.sbreak = sbreak;
            r.scontinue = scontinue;
            r.fes = fes;
            r.callsc = callsc;
            r.inunion = inunion;
            r.nofree = nofree;
            r.inLoop = inLoop;
            r.intypeof = intypeof;
            r.lastVar = lastVar;
            r.minst = minst;
            r.tinst = tinst;
            r.ctorflow = ctorflow.copy();
            r.aligndecl = aligndecl;
            r.namespace = namespace;
            r.linkage = linkage;
            r.cppmangle = cppmangle;
            r.inlining = inlining;
            r.protection = protection.copy();
            r.explicitProtection = explicitProtection;
            r.stc = stc;
            r.depdecl = depdecl;
            r.flags = flags;
            r.userAttribDecl = userAttribDecl;
            r.lastdc = lastdc;
            r.anchorCounts = anchorCounts;
            r.prevAnchor = prevAnchor;
            return r;
        }
        public Scope opAssign(Scope that) {
            this.enclosing = that.enclosing;
            this._module = that._module;
            this.scopesym = that.scopesym;
            this.func = that.func;
            this.parent = that.parent;
            this.slabel = that.slabel;
            this.sw = that.sw;
            this.tf = that.tf;
            this.os = that.os;
            this.sbreak = that.sbreak;
            this.scontinue = that.scontinue;
            this.fes = that.fes;
            this.callsc = that.callsc;
            this.inunion = that.inunion;
            this.nofree = that.nofree;
            this.inLoop = that.inLoop;
            this.intypeof = that.intypeof;
            this.lastVar = that.lastVar;
            this.minst = that.minst;
            this.tinst = that.tinst;
            this.ctorflow = that.ctorflow;
            this.aligndecl = that.aligndecl;
            this.namespace = that.namespace;
            this.linkage = that.linkage;
            this.cppmangle = that.cppmangle;
            this.inlining = that.inlining;
            this.protection = that.protection;
            this.explicitProtection = that.explicitProtection;
            this.stc = that.stc;
            this.depdecl = that.depdecl;
            this.flags = that.flags;
            this.userAttribDecl = that.userAttribDecl;
            this.lastdc = that.lastdc;
            this.anchorCounts = that.anchorCounts;
            this.prevAnchor = that.prevAnchor;
            return this;
        }
    }
}
