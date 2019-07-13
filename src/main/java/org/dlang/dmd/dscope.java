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
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.ctorflow.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.doc.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.tokens.*;

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
        public Ref<Ptr<Scope>> enclosing = ref(null);
        public Ref<dmodule.Module> _module = ref(null);
        public Ref<ScopeDsymbol> scopesym = ref(null);
        public Ref<FuncDeclaration> func = ref(null);
        public Ref<Dsymbol> parent = ref(null);
        public LabelStatement slabel = null;
        public SwitchStatement sw = null;
        public TryFinallyStatement tf = null;
        public ScopeGuardStatement os = null;
        public Statement sbreak = null;
        public Statement scontinue = null;
        public ForeachStatement fes = null;
        public Ptr<Scope> callsc = null;
        public Dsymbol inunion = null;
        public boolean nofree = false;
        public boolean inLoop = false;
        public IntRef intypeof = ref(0);
        public VarDeclaration lastVar = null;
        public Ref<dmodule.Module> minst = ref(null);
        public TemplateInstance tinst = null;
        public CtorFlow ctorflow = new CtorFlow();
        public AlignDeclaration aligndecl = null;
        public CPPNamespaceDeclaration namespace = null;
        public IntRef linkage = ref(LINK.d);
        public IntRef cppmangle = ref(CPPMANGLE.def);
        public int inlining = PINLINE.default_;
        public Ref<Prot> protection = ref(new Prot(Prot.Kind.public_, null));
        public int explicitProtection = 0;
        public Ref<Long> stc = ref(0L);
        public DeprecatedDeclaration depdecl = null;
        public IntRef flags = ref(0);
        public UserAttributeDeclaration userAttribDecl = null;
        public Ref<Ptr<DocComment>> lastdc = ref(null);
        public AA<Object,Integer> anchorCounts = new AA<Object,Integer>();
        public Identifier prevAnchor = null;
        public static Ptr<Scope> freelist = null;
        public static Ptr<Scope> alloc() {
            if (freelist != null)
            {
                Ptr<Scope> s = freelist;
                freelist = (s.get()).enclosing.value;
                assert(((s.get()).flags.value & 32768) != 0);
                (s.get()).flags.value &= -32769;
                return s;
            }
            return refPtr(new Scope(null, null, null, null, null, null, null, null, null, null, null, null, null, null, false, false, 0, null, null, null, new CtorFlow(CSX.none, new Slice<FieldInit>()), null, null, LINK.d, CPPMANGLE.def, PINLINE.default_, new Prot(Prot.Kind.public_, null), 0, 0L, null, 0, null, null, null, null));
        }

        public static Ptr<Scope> createGlobal(dmodule.Module _module) {
            Ptr<Scope> sc = alloc();
            sc.set(0, new Scope(null, null, null, null, null, null, null, null, null, null, null, null, null, null, false, false, 0, null, null, null, new CtorFlow(CSX.none, new Slice<FieldInit>()), null, null, LINK.d, CPPMANGLE.def, PINLINE.default_, new Prot(Prot.Kind.public_, null), 0, 0L, null, 0, null, null, null, null));
            (sc.get())._module.value = _module;
            (sc.get()).minst.value = _module;
            (sc.get()).scopesym.value = new ScopeDsymbol();
            (sc.get()).scopesym.value.symtab = new DsymbolTable();
            Dsymbol m = _module;
            for (; m.parent.value != null;) {
                m = m.parent.value;
            }
            m.addMember(null, (sc.get()).scopesym.value);
            m.parent.value = null;
            sc = (sc.get()).push(_module);
            (sc.get()).parent.value = _module;
            return sc;
        }

        public  Ptr<Scope> push() {
            Ptr<Scope> s = this.copy();
            assert((this.flags.value & 32768) == 0);
            (s.get()).scopesym.value = null;
            (s.get()).enclosing.value = ptr(this);
            (s.get()).slabel = null;
            (s.get()).nofree = false;
            (s.get()).ctorflow.fieldinit = arraydup(this.ctorflow.fieldinit).copy();
            (s.get()).flags.value = this.flags.value & 2042;
            (s.get()).lastdc.value = null;
            assert((ptr(this) != s));
            return s;
        }

        public  Ptr<Scope> push(ScopeDsymbol ss) {
            Ptr<Scope> s = this.push();
            (s.get()).scopesym.value = ss;
            return s;
        }

        public  Ptr<Scope> pop() {
            if (this.enclosing.value != null)
            {
                (this.enclosing.value.get()).ctorflow.OR(this.ctorflow);
            }
            this.ctorflow.freeFieldinit();
            Ptr<Scope> enc = this.enclosing.value;
            if (!this.nofree)
            {
                this.enclosing.value = freelist;
                freelist = ptr(this);
                this.flags.value |= 32768;
            }
            return enc;
        }

        public  void detach() {
            this.ctorflow.freeFieldinit();
            this.enclosing.value = null;
            this.pop();
        }

        public  Ptr<Scope> startCTFE() {
            Ptr<Scope> sc = this.push();
            (sc.get()).flags.value = this.flags.value | 128;
            return sc;
        }

        public  Ptr<Scope> endCTFE() {
            assert((this.flags.value & 128) != 0);
            return this.pop();
        }

        public  void merge(Loc loc, CtorFlow ctorflow) {
            if (!mergeCallSuper(callSuper, ctorflow.callSuper.value))
            {
                error(loc, new BytePtr("one path skips constructor"));
            }
            Slice<FieldInit> fies = ctorflow.fieldinit.copy();
            if ((this.ctorflow.fieldinit.getLength() != 0) && (fies.getLength() != 0))
            {
                FuncDeclaration f = this.func.value;
                if (this.fes != null)
                {
                    f = this.fes.func.value;
                }
                AggregateDeclaration ad = f.isMemberDecl();
                assert(ad != null);
                {
                    Slice<VarDeclaration> __r1104 = ad.fields.opSlice().copy();
                    int __key1103 = 0;
                    for (; (__key1103 < __r1104.getLength());__key1103 += 1) {
                        VarDeclaration v = __r1104.get(__key1103);
                        int i = __key1103;
                        boolean mustInit = ((v.storage_class.value & 549755813888L) != 0) || v.type.value.needsNested();
                        Ptr<FieldInit> fieldInit = ptr(this.ctorflow.fieldinit.get(i));
                        FieldInit fiesCurrent = fies.get(i).copy();
                        if (((fieldInit.get()).loc == new Loc(null, 0, 0)))
                        {
                            (fieldInit.get()).loc = fiesCurrent.loc.copy();
                        }
                        if (!mergeFieldInit(csx, fiesCurrent.csx.value) && mustInit)
                        {
                            error(loc, new BytePtr("one path skips field `%s`"), v.toChars());
                        }
                    }
                }
            }
        }

        public  dmodule.Module instantiatingModule() {
            return this.minst.value != null ? this.minst.value : this._module.value;
        }

        public  Dsymbol search(Loc loc, Identifier ident, Ptr<Dsymbol> pscopesym, int flags) {
            Ref<Identifier> ident_ref = ref(ident);
            Ref<Ptr<Dsymbol>> pscopesym_ref = ref(pscopesym);
            assert((flags & 24) == 0);
            if ((pequals(ident_ref.value, Id.empty.value)))
            {
                {
                    Ptr<Scope> sc = ptr(this);
                    for (; sc != null;sc = (sc.get()).enclosing.value){
                        assert((sc != (sc.get()).enclosing.value));
                        if ((sc.get()).scopesym.value == null)
                        {
                            continue;
                        }
                        {
                            Dsymbol s = (sc.get()).scopesym.value.isModule();
                            if ((s) != null)
                            {
                                if (pscopesym_ref.value != null)
                                {
                                    pscopesym_ref.value.set(0, (sc.get()).scopesym.value);
                                }
                                return s;
                            }
                        }
                    }
                }
                return null;
            }
            Function4<AggregateDeclaration,Identifier,Integer,Ptr<Expression>,Dsymbol> checkAliasThis = new Function4<AggregateDeclaration,Identifier,Integer,Ptr<Expression>,Dsymbol>(){
                public Dsymbol invoke(AggregateDeclaration ad, Identifier ident, Integer flags, Ptr<Expression> exp) {
                    Ref<AggregateDeclaration> ad_ref = ref(ad);
                    Ref<Identifier> ident_ref = ref(ident);
                    IntRef flags_ref = ref(flags);
                    Ref<Ptr<Expression>> exp_ref = ref(exp);
                    if ((ad_ref.value == null) || (ad_ref.value.aliasthis.value == null))
                    {
                        return null;
                    }
                    Ref<Declaration> decl = ref(ad_ref.value.aliasthis.value.isDeclaration());
                    if (decl.value == null)
                    {
                        return null;
                    }
                    Ref<Type> t = ref(decl.value.type.value);
                    Ref<ScopeDsymbol> sds = ref(null);
                    Ref<TypeClass> tc = ref(null);
                    Ref<TypeStruct> ts = ref(null);
                    switch ((t.value.ty.value & 0xFF))
                    {
                        case 8:
                            ts.value = (TypeStruct)t.value;
                            sds.value = ts.value.sym.value;
                            break;
                        case 7:
                            tc.value = (TypeClass)t.value;
                            sds.value = tc.value.sym.value;
                            break;
                        case 35:
                            sds.value = ((TypeInstance)t.value).tempinst.value;
                            break;
                        case 9:
                            sds.value = ((TypeEnum)t.value).sym.value;
                            break;
                        default:
                        break;
                    }
                    if (sds.value == null)
                    {
                        return null;
                    }
                    Ref<Dsymbol> ret = ref(sds.value.search(loc, ident_ref.value, flags_ref.value));
                    if (ret.value != null)
                    {
                        exp_ref.value.set(0, (new DotIdExp(loc, exp_ref.value.get(), ad_ref.value.aliasthis.value.ident.value)));
                        exp_ref.value.set(0, (new DotIdExp(loc, exp_ref.value.get(), ident_ref.value)));
                        return ret.value;
                    }
                    if ((ts.value == null) && (tc.value == null))
                    {
                        return null;
                    }
                    Ref<Dsymbol> s = ref(null);
                    exp_ref.value.set(0, (new DotIdExp(loc, exp_ref.value.get(), ad_ref.value.aliasthis.value.ident.value)));
                    if ((ts.value != null) && ((ts.value.att.value & AliasThisRec.tracing) == 0))
                    {
                        ts.value.att.value = ts.value.att.value | AliasThisRec.tracing;
                        s.value = checkAliasThis.invoke(sds.value.isAggregateDeclaration(), ident_ref.value, flags_ref.value, exp_ref.value);
                        ts.value.att.value = ts.value.att.value & -5;
                    }
                    else if ((tc.value != null) && ((tc.value.att.value & AliasThisRec.tracing) == 0))
                    {
                        tc.value.att.value = tc.value.att.value | AliasThisRec.tracing;
                        s.value = checkAliasThis.invoke(sds.value.isAggregateDeclaration(), ident_ref.value, flags_ref.value, exp_ref.value);
                        tc.value.att.value = tc.value.att.value & -5;
                    }
                    return s.value;
                }
            };
            Function1<Integer,Dsymbol> searchScopes = new Function1<Integer,Dsymbol>(){
                public Dsymbol invoke(Integer flags) {
                    IntRef flags_ref = ref(flags);
                    {
                        Ref<Ptr<Scope>> sc = ref(ptr(this));
                        for (; sc.value != null;sc.value = (sc.value.get()).enclosing.value){
                            assert((sc.value != (sc.value.get()).enclosing.value));
                            if ((sc.value.get()).scopesym.value == null)
                            {
                                continue;
                            }
                            if ((sc.value.get()).scopesym.value.isModule() != null)
                            {
                                flags_ref.value |= 32;
                            }
                            {
                                Ref<Dsymbol> s = ref((sc.value.get()).scopesym.value.search(loc, ident_ref.value, flags_ref.value));
                                if ((s.value) != null)
                                {
                                    if (((flags_ref.value & 18) == 0) && (pequals(ident_ref.value, Id.length.value)) && ((sc.value.get()).scopesym.value.isArrayScopeSymbol() != null) && ((sc.value.get()).enclosing.value != null) && (((sc.value.get()).enclosing.value.get()).search(loc, ident_ref.value, null, flags_ref.value) != null))
                                    {
                                        warning(s.value.loc.value, new BytePtr("array `length` hides other `length` name in outer scope"));
                                    }
                                    if (pscopesym_ref.value != null)
                                    {
                                        pscopesym_ref.value.set(0, (sc.value.get()).scopesym.value);
                                    }
                                    return s.value;
                                }
                            }
                            if (global.params.fixAliasThis.value)
                            {
                                Ref<Expression> exp = ref(new ThisExp(loc));
                                Ref<Dsymbol> aliasSym = ref(checkAliasThis.invoke((sc.value.get()).scopesym.value.isAggregateDeclaration(), ident_ref.value, flags_ref.value, ptr(exp)));
                                if (aliasSym.value != null)
                                {
                                    if (pscopesym_ref.value != null)
                                    {
                                        pscopesym_ref.value.set(0, (new ExpressionDsymbol(exp.value)));
                                    }
                                    return aliasSym.value;
                                }
                            }
                            if (((sc.value.get()).scopesym.value.isModule() != null) && !(((sc.value.get()).enclosing.value != null) && (((sc.value.get()).enclosing.value.get()).enclosing.value == null)))
                            {
                                break;
                            }
                        }
                    }
                    return null;
                }
            };
            if ((this.flags.value & 512) != 0)
            {
                flags |= 128;
            }
            Dsymbol s = searchScopes.invoke(flags | 8);
            if (s == null)
            {
                s = searchScopes.invoke(flags | 16);
            }
            return s;
        }

        // defaulted all parameters starting with #4
        public  Dsymbol search(Loc loc, Identifier ident, Ptr<Dsymbol> pscopesym) {
            return search(loc, ident, pscopesym, 0);
        }

        public  Dsymbol search_correct(Identifier ident) {
            if (global.gag.value != 0)
            {
                return null;
            }
            Function2<ByteSlice,Integer,Dsymbol> scope_search_fp = new Function2<ByteSlice,Integer,Dsymbol>(){
                public Dsymbol invoke(ByteSlice seed, IntRef cost) {
                    Ref<ByteSlice> seed_ref = ref(seed);
                    if (seed_ref.value.getLength() == 0)
                    {
                        return null;
                    }
                    Ref<Identifier> id = ref(Identifier.lookup(seed_ref.value));
                    if (id.value == null)
                    {
                        return null;
                    }
                    Ref<Ptr<Scope>> sc = ref(ptr(this));
                    dmodule.Module.clearCache();
                    Ref<Dsymbol> scopesym = ref(null);
                    Ref<Dsymbol> s = ref((sc.value.get()).search(Loc.initial.value, id.value, ptr(scopesym), 2));
                    if (s.value != null)
                    {
                        {
                            cost.value = 0;
                            for (; sc.value != null;comma(sc.value = (sc.value.get()).enclosing.value, cost.value += 1)) {
                                if ((pequals((sc.value.get()).scopesym.value, scopesym.value)))
                                {
                                    break;
                                }
                            }
                        }
                        if ((!pequals(scopesym.value, s.value.parent.value)))
                        {
                            cost.value += 1;
                            if ((s.value.prot().kind.value == Prot.Kind.private_))
                            {
                                return null;
                            }
                        }
                    }
                    return s.value;
                }
            };
            Ref<Dsymbol> scopesym = ref(null);
            {
                Dsymbol s = this.search(Loc.initial.value, ident, ptr(scopesym), 2);
                if ((s) != null)
                {
                    return s;
                }
            }
            return speller.invoke(ident.asString());
        }

        public static BytePtr search_correct_C(Identifier ident) {
            byte tok = TOK.reserved;
            if ((pequals(ident, Id.NULL)))
            {
                tok = TOK.null_;
            }
            else if ((pequals(ident, Id.TRUE)))
            {
                tok = TOK.true_;
            }
            else if ((pequals(ident, Id.FALSE)))
            {
                tok = TOK.false_;
            }
            else if ((pequals(ident, Id.unsigned)))
            {
                tok = TOK.uns32;
            }
            else if ((pequals(ident, Id.wchar_t)))
            {
                tok = global.params.isWindows ? TOK.wchar_ : TOK.dchar_;
            }
            else
            {
                return null;
            }
            return Token.toChars(tok);
        }

        public  Dsymbol insert(Dsymbol s) {
            {
                VarDeclaration vd = s.isVarDeclaration();
                if ((vd) != null)
                {
                    if (this.lastVar != null)
                    {
                        vd.lastVar.value = this.lastVar;
                    }
                    this.lastVar = vd;
                }
                else {
                    WithScopeSymbol ss = s.isWithScopeSymbol();
                    if ((ss) != null)
                    {
                        {
                            VarDeclaration vd = ss.withstate.wthis;
                            if ((vd) != null)
                            {
                                if (this.lastVar != null)
                                {
                                    vd.lastVar.value = this.lastVar;
                                }
                                this.lastVar = vd;
                            }
                        }
                        return null;
                    }
                }
            }
            {
                Ptr<Scope> sc = ptr(this);
                for (; sc != null;sc = (sc.get()).enclosing.value){
                    if ((sc.get()).scopesym.value != null)
                    {
                        if ((sc.get()).scopesym.value.symtab == null)
                        {
                            (sc.get()).scopesym.value.symtab = new DsymbolTable();
                        }
                        return (sc.get()).scopesym.value.symtabInsert(s);
                    }
                }
            }
            throw new AssertionError("Unreachable code!");
        }

        public  ClassDeclaration getClassScope() {
            {
                Ptr<Scope> sc = ptr(this);
                for (; sc != null;sc = (sc.get()).enclosing.value){
                    if ((sc.get()).scopesym.value == null)
                    {
                        continue;
                    }
                    ClassDeclaration cd = (sc.get()).scopesym.value.isClassDeclaration();
                    if (cd != null)
                    {
                        return cd;
                    }
                }
            }
            return null;
        }

        public  AggregateDeclaration getStructClassScope() {
            {
                Ptr<Scope> sc = ptr(this);
                for (; sc != null;sc = (sc.get()).enclosing.value){
                    if ((sc.get()).scopesym.value == null)
                    {
                        continue;
                    }
                    AggregateDeclaration ad = (sc.get()).scopesym.value.isClassDeclaration();
                    if (ad != null)
                    {
                        return ad;
                    }
                    ad = (sc.get()).scopesym.value.isStructDeclaration();
                    if (ad != null)
                    {
                        return ad;
                    }
                }
            }
            return null;
        }

        public  void setNoFree() {
            {
                Ptr<Scope> sc = ptr(this);
                for (; sc != null;sc = (sc.get()).enclosing.value){
                    (sc.get()).nofree = true;
                    assert((this.flags.value & 32768) == 0);
                }
            }
        }

        public  Scope(Scope sc) {
            this._module.value = sc._module.value;
            this.scopesym.value = sc.scopesym.value;
            this.enclosing.value = sc.enclosing.value;
            this.parent.value = sc.parent.value;
            this.sw = sc.sw;
            this.tf = sc.tf;
            this.os = sc.os;
            this.tinst = sc.tinst;
            this.minst.value = sc.minst.value;
            this.sbreak = sc.sbreak;
            this.scontinue = sc.scontinue;
            this.fes = sc.fes;
            this.callsc = sc.callsc;
            this.aligndecl = sc.aligndecl;
            this.func.value = sc.func.value;
            this.slabel = sc.slabel;
            this.linkage.value = sc.linkage.value;
            this.cppmangle.value = sc.cppmangle.value;
            this.inlining = sc.inlining;
            this.protection.value = sc.protection.value.copy();
            this.explicitProtection = sc.explicitProtection;
            this.stc.value = sc.stc.value;
            this.depdecl = sc.depdecl;
            this.inunion = sc.inunion;
            this.nofree = sc.nofree;
            this.inLoop = sc.inLoop;
            this.intypeof.value = sc.intypeof.value;
            this.lastVar = sc.lastVar;
            this.ctorflow = sc.ctorflow.copy();
            this.flags.value = sc.flags.value;
            this.lastdc.value = sc.lastdc.value;
            this.anchorCounts = sc.anchorCounts;
            this.prevAnchor = sc.prevAnchor;
            this.userAttribDecl = sc.userAttribDecl;
        }

        public  int alignment() {
            if (this.aligndecl != null)
            {
                return getAlignment(this.aligndecl, ptr(this));
            }
            else
            {
                return -1;
            }
        }

        public  boolean isDeprecated() {
            {
                Ptr<Dsymbol> sp = pcopy(ptr(this.parent));
                for (; sp.get() != null;sp = pcopy((ptr(sp.get().parent)))){
                    if ((sp.get()).isDeprecated())
                    {
                        return true;
                    }
                }
            }
            {
                Ptr<Scope> sc2 = ptr(this);
                for (; sc2 != null;sc2 = (sc2.get()).enclosing.value){
                    if (((sc2.get()).scopesym.value != null) && (sc2.get()).scopesym.value.isDeprecated())
                    {
                        return true;
                    }
                    if (((sc2.get()).stc.value & 1024L) != 0)
                    {
                        return true;
                    }
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
