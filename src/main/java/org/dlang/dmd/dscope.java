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
        public Ptr<Scope> enclosing = null;
        public dmodule.Module _module = null;
        public ScopeDsymbol scopesym = null;
        public FuncDeclaration func = null;
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
        public int intypeof = 0;
        public VarDeclaration lastVar = null;
        public dmodule.Module minst = null;
        public TemplateInstance tinst = null;
        public CtorFlow ctorflow = new CtorFlow();
        public AlignDeclaration aligndecl = null;
        public CPPNamespaceDeclaration namespace = null;
        public int linkage = LINK.d;
        public int cppmangle = CPPMANGLE.def;
        public int inlining = PINLINE.default_;
        public Prot protection = new Prot(Prot.Kind.public_, null);
        public int explicitProtection = 0;
        public long stc = 0L;
        public DeprecatedDeclaration depdecl = null;
        public int flags = 0;
        public UserAttributeDeclaration userAttribDecl = null;
        public Ptr<DocComment> lastdc = null;
        public AA<Object,Integer> anchorCounts = new AA<Object,Integer>();
        public Identifier prevAnchor = null;
        public static Ptr<Scope> freelist = null;
        // Erasure: alloc<>
        public static Ptr<Scope> alloc() {
            if (freelist != null)
            {
                Ptr<Scope> s = freelist;
                freelist = pcopy((s.get()).enclosing);
                assert(((s.get()).flags & 32768) != 0);
                (s.get()).flags &= -32769;
                return s;
            }
            return refPtr(new Scope(null, null, null, null, null, null, null, null, null, null, null, null, null, null, false, false, 0, null, null, null, new CtorFlow(CSX.none, new Slice<FieldInit>()), null, null, LINK.d, CPPMANGLE.def, PINLINE.default_, new Prot(Prot.Kind.public_, null), 0, 0L, null, 0, null, null, null, null));
        }

        // Erasure: createGlobal<Module>
        public static Ptr<Scope> createGlobal(dmodule.Module _module) {
            Ptr<Scope> sc = alloc();
            sc.set(0, new Scope(null, null, null, null, null, null, null, null, null, null, null, null, null, null, false, false, 0, null, null, null, new CtorFlow(CSX.none, new Slice<FieldInit>()), null, null, LINK.d, CPPMANGLE.def, PINLINE.default_, new Prot(Prot.Kind.public_, null), 0, 0L, null, 0, null, null, null, null));
            (sc.get())._module = _module;
            (sc.get()).minst = _module;
            (sc.get()).scopesym = new ScopeDsymbol();
            (sc.get()).scopesym.symtab = new DsymbolTable();
            Dsymbol m = _module;
            for (; m.parent.value != null;) {
                m = m.parent.value;
            }
            m.addMember(null, (sc.get()).scopesym);
            m.parent.value = null;
            sc = pcopy((sc.get()).push(_module));
            (sc.get()).parent.value = _module;
            return sc;
        }

        // Erasure: push<>
        public  Ptr<Scope> push() {
            Ptr<Scope> s = this.copy();
            assert((this.flags & 32768) == 0);
            (s.get()).scopesym = null;
            (s.get()).enclosing = pcopy((ptr(this)));
            (s.get()).slabel = null;
            (s.get()).nofree = false;
            (s.get()).ctorflow.fieldinit = arraydup(this.ctorflow.fieldinit).copy();
            (s.get()).flags = this.flags & 2042;
            (s.get()).lastdc = null;
            assert((ptr(this) != s));
            return s;
        }

        // Erasure: push<ScopeDsymbol>
        public  Ptr<Scope> push(ScopeDsymbol ss) {
            Ptr<Scope> s = this.push();
            (s.get()).scopesym = ss;
            return s;
        }

        // Erasure: pop<>
        public  Ptr<Scope> pop() {
            if (this.enclosing != null)
            {
                (this.enclosing.get()).ctorflow.OR(this.ctorflow);
            }
            this.ctorflow.freeFieldinit();
            Ptr<Scope> enc = this.enclosing;
            if (!this.nofree)
            {
                this.enclosing = pcopy(freelist);
                freelist = pcopy((ptr(this)));
                this.flags |= 32768;
            }
            return enc;
        }

        // Erasure: detach<>
        public  void detach() {
            this.ctorflow.freeFieldinit();
            this.enclosing = null;
            this.pop();
        }

        // Erasure: startCTFE<>
        public  Ptr<Scope> startCTFE() {
            Ptr<Scope> sc = this.push();
            (sc.get()).flags = this.flags | 128;
            return sc;
        }

        // Erasure: endCTFE<>
        public  Ptr<Scope> endCTFE() {
            assert((this.flags & 128) != 0);
            return this.pop();
        }

        // Erasure: merge<Loc, CtorFlow>
        public  void merge(Loc loc, CtorFlow ctorflow) {
            if (!mergeCallSuper(callSuper, ctorflow.callSuper.value))
            {
                error(loc, new BytePtr("one path skips constructor"));
            }
            Slice<FieldInit> fies = ctorflow.fieldinit.copy();
            if ((this.ctorflow.fieldinit.getLength() != 0) && (fies.getLength() != 0))
            {
                FuncDeclaration f = this.func;
                if (this.fes != null)
                {
                    f = this.fes.func;
                }
                AggregateDeclaration ad = f.isMemberDecl();
                assert(ad != null);
                {
                    Slice<VarDeclaration> __r1118 = ad.fields.opSlice().copy();
                    int __key1117 = 0;
                    for (; (__key1117 < __r1118.getLength());__key1117 += 1) {
                        VarDeclaration v = __r1118.get(__key1117);
                        int i = __key1117;
                        boolean mustInit = ((v.storage_class & 549755813888L) != 0) || v.type.needsNested();
                        Ptr<FieldInit> fieldInit = ptr(this.ctorflow.fieldinit.get(i));
                        FieldInit fiesCurrent = fies.get(i).copy();
                        if (((fieldInit.get()).loc == new Loc(null, 0, 0)))
                        {
                            (fieldInit.get()).loc.opAssign(fiesCurrent.loc.copy());
                        }
                        if (!mergeFieldInit(csx, fiesCurrent.csx.value) && mustInit)
                        {
                            error(loc, new BytePtr("one path skips field `%s`"), v.toChars());
                        }
                    }
                }
            }
        }

        // Erasure: instantiatingModule<>
        public  dmodule.Module instantiatingModule() {
            return this.minst != null ? this.minst : this._module;
        }

        // Erasure: search<Loc, Identifier, Ptr, int>
        public  Dsymbol search(Loc loc, Identifier ident, Ptr<Dsymbol> pscopesym, int flags) {
            Scope __self = this;
            assert((flags & 24) == 0);
            if ((pequals(ident, Id.empty)))
            {
                {
                    Ptr<Scope> sc = ptr(this);
                    for (; sc != null;sc = pcopy((sc.get()).enclosing)){
                        assert((sc != (sc.get()).enclosing));
                        if ((sc.get()).scopesym == null)
                        {
                            continue;
                        }
                        {
                            Dsymbol s = (sc.get()).scopesym.isModule();
                            if ((s) != null)
                            {
                                if (pscopesym != null)
                                {
                                    pscopesym.set(0, (sc.get()).scopesym);
                                }
                                return s;
                            }
                        }
                    }
                }
                return null;
            }
            Function4<AggregateDeclaration,Identifier,Integer,Ptr<Expression>,Dsymbol> checkAliasThis = new Function4<AggregateDeclaration,Identifier,Integer,Ptr<Expression>,Dsymbol>() {
                public Dsymbol invoke(AggregateDeclaration ad, Identifier ident, Integer flags, Ptr<Expression> exp) {
                 {
                    if ((ad == null) || (ad.aliasthis == null))
                    {
                        return null;
                    }
                    Declaration decl = ad.aliasthis.isDeclaration();
                    if (decl == null)
                    {
                        return null;
                    }
                    Type t = decl.type;
                    Ref<ScopeDsymbol> sds = ref(null);
                    Ref<TypeClass> tc = ref(null);
                    Ref<TypeStruct> ts = ref(null);
                    switch ((t.ty & 0xFF))
                    {
                        case 8:
                            ts.value = (TypeStruct)t;
                            sds.value = ts.value.sym;
                            break;
                        case 7:
                            tc.value = (TypeClass)t;
                            sds.value = tc.value.sym;
                            break;
                        case 35:
                            sds.value = ((TypeInstance)t).tempinst;
                            break;
                        case 9:
                            sds.value = ((TypeEnum)t).sym;
                            break;
                        default:
                        break;
                    }
                    if (sds.value == null)
                    {
                        return null;
                    }
                    Dsymbol ret = sds.value.search(loc, ident, flags);
                    if (ret != null)
                    {
                        exp.set(0, (new DotIdExp(loc, exp.get(), ad.aliasthis.ident)));
                        exp.set(0, (new DotIdExp(loc, exp.get(), ident)));
                        return ret;
                    }
                    if ((ts.value == null) && (tc.value == null))
                    {
                        return null;
                    }
                    Ref<Dsymbol> s = ref(null);
                    exp.set(0, (new DotIdExp(loc, exp.get(), ad.aliasthis.ident)));
                    if ((ts.value != null) && ((ts.value.att.value & AliasThisRec.tracing) == 0))
                    {
                        ts.value.att.value = ts.value.att.value | AliasThisRec.tracing;
                        s.value = invoke(sds.value.isAggregateDeclaration(), ident, flags, exp);
                        ts.value.att.value = ts.value.att.value & -5;
                    }
                    else if ((tc.value != null) && ((tc.value.att.value & AliasThisRec.tracing) == 0))
                    {
                        tc.value.att.value = tc.value.att.value | AliasThisRec.tracing;
                        s.value = invoke(sds.value.isAggregateDeclaration(), ident, flags, exp);
                        tc.value.att.value = tc.value.att.value & -5;
                    }
                    return s.value;
                }}

            };
            Function1<Integer,Dsymbol> searchScopes = new Function1<Integer,Dsymbol>() {
                public Dsymbol invoke(Integer flags) {
                 {
                    Ref<Integer> flags_ref = ref(flags);
                    {
                        Ref<Ptr<Scope>> sc = ref(ptr(__self));
                        for (; sc.value != null;sc.value = pcopy((sc.value.get()).enclosing)){
                            assert((sc.value != (sc.value.get()).enclosing));
                            if ((sc.value.get()).scopesym == null)
                            {
                                continue;
                            }
                            if ((sc.value.get()).scopesym.isModule() != null)
                            {
                                flags_ref.value |= 32;
                            }
                            {
                                Dsymbol s = (sc.value.get()).scopesym.search(loc, ident, flags_ref.value);
                                if ((s) != null)
                                {
                                    if (((flags_ref.value & 18) == 0) && (pequals(ident, Id.length)) && ((sc.value.get()).scopesym.isArrayScopeSymbol() != null) && ((sc.value.get()).enclosing != null) && (((sc.value.get()).enclosing.get()).search(loc, ident, null, flags_ref.value) != null))
                                    {
                                        warning(s.loc, new BytePtr("array `length` hides other `length` name in outer scope"));
                                    }
                                    if (pscopesym != null)
                                    {
                                        pscopesym.set(0, (sc.value.get()).scopesym);
                                    }
                                    return s;
                                }
                            }
                            if (global.params.fixAliasThis)
                            {
                                Ref<Expression> exp = ref(new ThisExp(loc));
                                Dsymbol aliasSym = checkAliasThis.invoke((sc.value.get()).scopesym.isAggregateDeclaration(), ident, flags_ref.value, ptr(exp));
                                if (aliasSym != null)
                                {
                                    if (pscopesym != null)
                                    {
                                        pscopesym.set(0, (new ExpressionDsymbol(exp.value)));
                                    }
                                    return aliasSym;
                                }
                            }
                            if (((sc.value.get()).scopesym.isModule() != null) && !(((sc.value.get()).enclosing != null) && (((sc.value.get()).enclosing.get()).enclosing == null)))
                            {
                                break;
                            }
                        }
                    }
                    return null;
                }}

            };
            if ((this.flags & 512) != 0)
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

        // Erasure: search_correct<Identifier>
        public  Dsymbol search_correct(Identifier ident) {
            Scope __self = this;
            if (global.gag != 0)
            {
                return null;
            }
            Function2<ByteSlice,Ref<Integer>,Dsymbol> scope_search_fp = new Function2<ByteSlice,Ref<Integer>,Dsymbol>() {
                public Dsymbol invoke(ByteSlice seed, Ref<Integer> cost) {
                 {
                    if (seed.getLength() == 0)
                    {
                        return null;
                    }
                    Identifier id = Identifier.lookup(seed);
                    if (id == null)
                    {
                        return null;
                    }
                    Ref<Ptr<Scope>> sc = ref(ptr(__self));
                    dmodule.Module.clearCache();
                    Ref<Dsymbol> scopesym = ref(null);
                    Dsymbol s = (sc.value.get()).search(Loc.initial, id, ptr(scopesym), 2);
                    if (s != null)
                    {
                        {
                            cost.value = 0;
                            for (; sc.value != null;comma(sc.value = pcopy((sc.value.get()).enclosing), cost.value += 1)) {
                                if ((pequals((sc.value.get()).scopesym, scopesym.value)))
                                {
                                    break;
                                }
                            }
                        }
                        if ((!pequals(scopesym.value, s.parent.value)))
                        {
                            cost.value += 1;
                            if ((s.prot().kind == Prot.Kind.private_))
                            {
                                return null;
                            }
                        }
                    }
                    return s;
                }}

            };
            Ref<Dsymbol> scopesym = ref(null);
            {
                Dsymbol s = this.search(Loc.initial, ident, ptr(scopesym), 2);
                if ((s) != null)
                {
                    return s;
                }
            }
            return speller.invoke(ident.asString());
        }

        // Erasure: search_correct_C<Identifier>
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

        // Erasure: insert<Dsymbol>
        public  Dsymbol insert(Dsymbol s) {
            {
                VarDeclaration vd = s.isVarDeclaration();
                if ((vd) != null)
                {
                    if (this.lastVar != null)
                    {
                        vd.lastVar = this.lastVar;
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
                                    vd.lastVar = this.lastVar;
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
                for (; sc != null;sc = pcopy((sc.get()).enclosing)){
                    if ((sc.get()).scopesym != null)
                    {
                        if ((sc.get()).scopesym.symtab == null)
                        {
                            (sc.get()).scopesym.symtab = new DsymbolTable();
                        }
                        return (sc.get()).scopesym.symtabInsert(s);
                    }
                }
            }
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: getClassScope<>
        public  ClassDeclaration getClassScope() {
            {
                Ptr<Scope> sc = ptr(this);
                for (; sc != null;sc = pcopy((sc.get()).enclosing)){
                    if ((sc.get()).scopesym == null)
                    {
                        continue;
                    }
                    ClassDeclaration cd = (sc.get()).scopesym.isClassDeclaration();
                    if (cd != null)
                    {
                        return cd;
                    }
                }
            }
            return null;
        }

        // Erasure: getStructClassScope<>
        public  AggregateDeclaration getStructClassScope() {
            {
                Ptr<Scope> sc = ptr(this);
                for (; sc != null;sc = pcopy((sc.get()).enclosing)){
                    if ((sc.get()).scopesym == null)
                    {
                        continue;
                    }
                    AggregateDeclaration ad = (sc.get()).scopesym.isClassDeclaration();
                    if (ad != null)
                    {
                        return ad;
                    }
                    ad = (sc.get()).scopesym.isStructDeclaration();
                    if (ad != null)
                    {
                        return ad;
                    }
                }
            }
            return null;
        }

        // Erasure: setNoFree<>
        public  void setNoFree() {
            {
                Ptr<Scope> sc = ptr(this);
                for (; sc != null;sc = pcopy((sc.get()).enclosing)){
                    (sc.get()).nofree = true;
                    assert((this.flags & 32768) == 0);
                }
            }
        }

        // Erasure: __ctor<Scope>
        public  Scope(Scope sc) {
            this._module = sc._module;
            this.scopesym = sc.scopesym;
            this.enclosing = pcopy(sc.enclosing);
            this.parent.value = sc.parent.value;
            this.sw = sc.sw;
            this.tf = sc.tf;
            this.os = sc.os;
            this.tinst = sc.tinst;
            this.minst = sc.minst;
            this.sbreak = sc.sbreak;
            this.scontinue = sc.scontinue;
            this.fes = sc.fes;
            this.callsc = pcopy(sc.callsc);
            this.aligndecl = sc.aligndecl;
            this.func = sc.func;
            this.slabel = sc.slabel;
            this.linkage = sc.linkage;
            this.cppmangle = sc.cppmangle;
            this.inlining = sc.inlining;
            this.protection.opAssign(sc.protection.copy());
            this.explicitProtection = sc.explicitProtection;
            this.stc = sc.stc;
            this.depdecl = sc.depdecl;
            this.inunion = sc.inunion;
            this.nofree = sc.nofree;
            this.inLoop = sc.inLoop;
            this.intypeof = sc.intypeof;
            this.lastVar = sc.lastVar;
            this.ctorflow.opAssign(sc.ctorflow.copy());
            this.flags = sc.flags;
            this.lastdc = pcopy(sc.lastdc);
            this.anchorCounts = sc.anchorCounts;
            this.prevAnchor = sc.prevAnchor;
            this.userAttribDecl = sc.userAttribDecl;
        }

        // Erasure: alignment<>
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

        // Erasure: isDeprecated<>
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
                for (; sc2 != null;sc2 = pcopy((sc2.get()).enclosing)){
                    if (((sc2.get()).scopesym != null) && (sc2.get()).scopesym.isDeprecated())
                    {
                        return true;
                    }
                    if (((sc2.get()).stc & 1024L) != 0)
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public Scope(){ }
        public Scope copy(){
            Scope r = new Scope();
            r.enclosing = enclosing;
            r._module = _module;
            r.scopesym = scopesym;
            r.func = func;
            r.parent = parent.copy();
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
