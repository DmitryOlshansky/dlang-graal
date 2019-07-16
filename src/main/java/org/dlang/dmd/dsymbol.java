package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.access.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.aliasthis.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.ast_node.*;
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.denum.*;
import static org.dlang.dmd.dimport.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.gluelayer.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.lexer.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.nspace.*;
import static org.dlang.dmd.opover.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.visitor.*;

public class dsymbol {

    // Erasure: foreachDsymbol<Ptr, Function1>
    public static int foreachDsymbol(Ptr<DArray<Dsymbol>> symbols, Function1<Dsymbol,Integer> dg) {
        assert(dg != null);
        if (symbols != null)
        {
            {
                int i = 0;
                for (; (i < (symbols.get()).length);i += 1){
                    Dsymbol s = (symbols.get()).get(i);
                    int result = dg.invoke(s);
                    if (result != 0)
                    {
                        return result;
                    }
                }
            }
        }
        return 0;
    }

    // Erasure: foreachDsymbol<Ptr, Function1>
    public static void foreachDsymbol(Ptr<DArray<Dsymbol>> symbols, Function1<Dsymbol,Void> dg, ETag1 __tag) {
        assert(dg != null);
        if (symbols != null)
        {
            {
                int i = 0;
                for (; (i < (symbols.get()).length);i += 1){
                    Dsymbol s = (symbols.get()).get(i);
                    dg.invoke(s);
                }
            }
        }
    }

    public static class Ungag
    {
        public int oldgag = 0;
        // Erasure: __ctor<int>
        public  Ungag(int old) {
            this.oldgag = old;
        }

        public Ungag(){ }
        public Ungag copy(){
            Ungag r = new Ungag();
            r.oldgag = oldgag;
            return r;
        }
        public Ungag opAssign(Ungag that) {
            this.oldgag = that.oldgag;
            return this;
        }
    }
    public static class Prot
    {

        public static class Kind 
        {
            public static final int undefined = 0;
            public static final int none = 1;
            public static final int private_ = 2;
            public static final int package_ = 3;
            public static final int protected_ = 4;
            public static final int public_ = 5;
            public static final int export_ = 6;
        }

        public int kind = 0;
        public dmodule.Package pkg = null;
        // Erasure: __ctor<int>
        public  Prot(int kind) {
            this.kind = kind;
        }

        // Erasure: isMoreRestrictiveThan<Prot>
        public  boolean isMoreRestrictiveThan(Prot other) {
            return this.kind < other.kind;
        }

        // Erasure: opEquals<Prot>
        public  boolean opEquals(Prot other) {
            if ((this.kind == other.kind))
            {
                if ((this.kind == Kind.package_))
                {
                    return pequals(this.pkg, other.pkg);
                }
                return true;
            }
            return false;
        }

        // Erasure: isSubsetOf<Prot>
        public  boolean isSubsetOf(Prot parent) {
            if ((this.kind != parent.kind))
            {
                return false;
            }
            if ((this.kind == Kind.package_))
            {
                if (this.pkg == null)
                {
                    return true;
                }
                if (parent.pkg == null)
                {
                    return false;
                }
                if (parent.pkg.isAncestorPackageOf(this.pkg))
                {
                    return true;
                }
            }
            return true;
        }

        public Prot(){ }
        public Prot copy(){
            Prot r = new Prot();
            r.kind = kind;
            r.pkg = pkg;
            return r;
        }
        public Prot opAssign(Prot that) {
            this.kind = that.kind;
            this.pkg = that.pkg;
            return this;
        }
    }

    public static class PASS 
    {
        public static final int init = 0;
        public static final int semantic = 1;
        public static final int semanticdone = 2;
        public static final int semantic2 = 3;
        public static final int semantic2done = 4;
        public static final int semantic3 = 5;
        public static final int semantic3done = 6;
        public static final int inline = 7;
        public static final int inlinedone = 8;
        public static final int obj = 9;
    }


    public static final int IgnoreNone = 0;
    public static final int IgnorePrivateImports = 1;
    public static final int IgnoreErrors = 2;
    public static final int IgnoreAmbiguous = 4;
    public static final int SearchLocalsOnly = 8;
    public static final int SearchImportsOnly = 16;
    public static final int SearchUnqualifiedModule = 32;
    public static final int IgnoreSymbolVisibility = 128;
    public static class Dsymbol extends ASTNode
    {
        public Identifier ident = null;
        public Ref<Dsymbol> parent = ref(null);
        public CPPNamespaceDeclaration namespace = null;
        public Ptr<Symbol> csym = null;
        public Ptr<Symbol> isym = null;
        public BytePtr comment = null;
        public Loc loc = new Loc();
        public Ptr<Scope> _scope = null;
        public BytePtr prettystring = null;
        public boolean errors = false;
        public int semanticRun = PASS.init;
        public DeprecatedDeclaration depdecl = null;
        public UserAttributeDeclaration userAttribDecl = null;
        public UnitTestDeclaration ddocUnittest = null;
        // Erasure: __ctor<>
        public  Dsymbol() {
            super();
            this.loc = new Loc(null, 0, 0);
        }

        // Erasure: __ctor<Identifier>
        public  Dsymbol(Identifier ident) {
            super();
            this.loc = new Loc(null, 0, 0);
            this.ident = ident;
        }

        // Erasure: __ctor<Loc, Identifier>
        public  Dsymbol(Loc loc, Identifier ident) {
            super();
            this.loc.opAssign(loc.copy());
            this.ident = ident;
        }

        // Erasure: create<Identifier>
        public static Dsymbol create(Identifier ident) {
            return new Dsymbol(ident);
        }

        // Erasure: toChars<>
        public  BytePtr toChars() {
            return this.ident != null ? this.ident.toChars() : new BytePtr("__anonymous");
        }

        // Erasure: toPrettyCharsHelper<>
        public  BytePtr toPrettyCharsHelper() {
            return this.toChars();
        }

        // Erasure: getLoc<>
        public  Loc getLoc() {
            if (!this.loc.isValid())
            {
                {
                    dmodule.Module m = this.getModule();
                    if ((m) != null)
                    {
                        return new Loc(m.srcfile.toChars(), 0, 0);
                    }
                }
            }
            return this.loc;
        }

        // Erasure: locToChars<>
        public  BytePtr locToChars() {
            return this.getLoc().toChars(global.params.showColumns);
        }

        // Erasure: equals<RootObject>
        public  boolean equals(RootObject o) {
            if ((pequals(this, o)))
            {
                return true;
            }
            if ((o.dyncast() != DYNCAST.dsymbol))
            {
                return false;
            }
            Dsymbol s = (Dsymbol)o;
            if ((s != null) && (this.ident != null) && (s.ident != null) && this.ident.equals(s.ident))
            {
                return true;
            }
            return false;
        }

        // Erasure: isAnonymous<>
        public  boolean isAnonymous() {
            return this.ident == null;
        }

        // Erasure: error<Loc, Ptr>
        public  void error(Loc loc, BytePtr format, Object... ap) {
            Ref<BytePtr> format_ref = ref(format);
            BytePtr cstr = pcopy(this.toPrettyChars(false));
            ByteSlice pretty = concat(concat((byte)96, cstr.slice(0,strlen(cstr))), new ByteSlice("`\u0000")).copy();
            verror(loc, format_ref.value, new RawSlice<>(ap), this.kind(), pretty.getPtr(0), new BytePtr("Error: "));
        }

        // Erasure: error<Ptr>
        public  void error(BytePtr format, Object... ap) {
            Ref<BytePtr> format_ref = ref(format);
            BytePtr cstr = pcopy(this.toPrettyChars(false));
            ByteSlice pretty = concat(concat((byte)96, cstr.slice(0,strlen(cstr))), new ByteSlice("`\u0000")).copy();
            Loc loc = this.getLoc().copy();
            verror(loc, format_ref.value, new RawSlice<>(ap), this.kind(), pretty.getPtr(0), new BytePtr("Error: "));
        }

        // Erasure: deprecation<Loc, Ptr>
        public  void deprecation(Loc loc, BytePtr format, Object... ap) {
            Ref<BytePtr> format_ref = ref(format);
            BytePtr cstr = pcopy(this.toPrettyChars(false));
            ByteSlice pretty = concat(concat((byte)96, cstr.slice(0,strlen(cstr))), new ByteSlice("`\u0000")).copy();
            vdeprecation(loc, format_ref.value, new RawSlice<>(ap), this.kind(), pretty.getPtr(0));
        }

        // Erasure: deprecation<Ptr>
        public  void deprecation(BytePtr format, Object... ap) {
            Ref<BytePtr> format_ref = ref(format);
            BytePtr cstr = pcopy(this.toPrettyChars(false));
            ByteSlice pretty = concat(concat((byte)96, cstr.slice(0,strlen(cstr))), new ByteSlice("`\u0000")).copy();
            Loc loc = this.getLoc().copy();
            vdeprecation(loc, format_ref.value, new RawSlice<>(ap), this.kind(), pretty.getPtr(0));
        }

        // Erasure: checkDeprecated<Loc, Ptr>
        public  boolean checkDeprecated(Loc loc, Ptr<Scope> sc) {
            if (((global.params.useDeprecated & 0xFF) != 2) && this.isDeprecated())
            {
                if ((sc.get()).isDeprecated())
                {
                    return false;
                }
                BytePtr message = null;
                {
                    Dsymbol p = this;
                    for (; p != null;p = p.parent.value){
                        message = pcopy((p.depdecl != null ? getMessage(p.depdecl) : null));
                        if (message != null)
                        {
                            break;
                        }
                    }
                }
                if (message != null)
                {
                    this.deprecation(loc, new BytePtr("is deprecated - %s"), message);
                }
                else
                {
                    this.deprecation(loc, new BytePtr("is deprecated"));
                }
                return true;
            }
            return false;
        }

        // Erasure: getModule<>
        public  dmodule.Module getModule() {
            {
                TemplateInstance ti = this.isInstantiated();
                if ((ti) != null)
                {
                    return ti.tempdecl.getModule();
                }
            }
            Dsymbol s = this;
            for (; s != null;){
                dmodule.Module m = s.isModule();
                if (m != null)
                {
                    return m;
                }
                s = s.parent.value;
            }
            return null;
        }

        // Erasure: getAccessModule<>
        public  dmodule.Module getAccessModule() {
            {
                TemplateInstance ti = this.isInstantiated();
                if ((ti) != null)
                {
                    return ti.tempdecl.getAccessModule();
                }
            }
            Dsymbol s = this;
            for (; s != null;){
                dmodule.Module m = s.isModule();
                if (m != null)
                {
                    return m;
                }
                TemplateInstance ti = s.isTemplateInstance();
                if ((ti != null) && (ti.enclosing != null))
                {
                    s = ti.tempdecl;
                }
                else
                {
                    s = s.parent.value;
                }
            }
            return null;
        }

        // Erasure: pastMixin<>
        public  Dsymbol pastMixin() {
            if ((this.isTemplateMixin() == null) && (this.isForwardingAttribDeclaration() == null))
            {
                return this;
            }
            if (this.parent.value == null)
            {
                return null;
            }
            return this.parent.value.pastMixin();
        }

        // Erasure: toParent<>
        public  Dsymbol toParent() {
            return this.parent.value != null ? this.parent.value.pastMixin() : null;
        }

        // Erasure: toParent2<>
        public  Dsymbol toParent2() {
            if ((this.parent.value == null) || (this.parent.value.isTemplateInstance() == null) && (this.parent.value.isForwardingAttribDeclaration() == null))
            {
                return this.parent.value;
            }
            return this.parent.value.toParent2();
        }

        // Erasure: toParentDecl<>
        public  Dsymbol toParentDecl() {
            return this.toParentDeclImpl(false);
        }

        // Erasure: toParentLocal<>
        public  Dsymbol toParentLocal() {
            return this.toParentDeclImpl(true);
        }

        // Erasure: toParentDeclImpl<boolean>
        public  Dsymbol toParentDeclImpl(boolean localOnly) {
            Dsymbol p = this.toParent();
            if ((p == null) || (p.isTemplateInstance() == null))
            {
                return p;
            }
            TemplateInstance ti = p.isTemplateInstance();
            if ((ti.tempdecl != null) && !localOnly || !((TemplateDeclaration)ti.tempdecl).isstatic)
            {
                return ti.tempdecl.toParentDeclImpl(localOnly);
            }
            return this.parent.value.toParentDeclImpl(localOnly);
        }

        // Erasure: isInstantiated<>
        public  TemplateInstance isInstantiated() {
            if (this.parent.value == null)
            {
                return null;
            }
            TemplateInstance ti = this.parent.value.isTemplateInstance();
            if ((ti != null) && (ti.isTemplateMixin() == null))
            {
                return ti;
            }
            return this.parent.value.isInstantiated();
        }

        // Erasure: isSpeculative<>
        public  TemplateInstance isSpeculative() {
            if (this.parent.value == null)
            {
                return null;
            }
            TemplateInstance ti = this.parent.value.isTemplateInstance();
            if ((ti != null) && ti.gagged)
            {
                return ti;
            }
            if (this.parent.value.toParent() == null)
            {
                return null;
            }
            return this.parent.value.isSpeculative();
        }

        // Erasure: ungagSpeculative<>
        public  Ungag ungagSpeculative() {
            int oldgag = global.gag;
            if ((global.gag != 0) && (this.isSpeculative() == null) && (this.toParent2().isFuncDeclaration() == null))
            {
                global.gag = 0;
            }
            return new Ungag(oldgag);
        }

        // Erasure: dyncast<>
        public  int dyncast() {
            return DYNCAST.dsymbol;
        }

        // Erasure: arraySyntaxCopy<Ptr>
        public static Ptr<DArray<Dsymbol>> arraySyntaxCopy(Ptr<DArray<Dsymbol>> a) {
            Ptr<DArray<Dsymbol>> b = null;
            if (a != null)
            {
                b = pcopy((a.get()).copy());
                {
                    int i = 0;
                    for (; (i < (b.get()).length);i++){
                        b.get().set(i, (b.get()).get(i).syntaxCopy(null));
                    }
                }
            }
            return b;
        }

        // Erasure: getIdent<>
        public  Identifier getIdent() {
            return this.ident;
        }

        // Erasure: toPrettyChars<boolean>
        public  BytePtr toPrettyChars(boolean QualifyTypes) {
            if ((this.prettystring != null) && !QualifyTypes)
            {
                return this.prettystring;
            }
            if (this.parent.value == null)
            {
                BytePtr s = pcopy(this.toChars());
                if (!QualifyTypes)
                {
                    this.prettystring = pcopy(s);
                }
                return s;
            }
            int complength = 0;
            {
                Dsymbol p = this;
                for (; p != null;p = p.parent.value) {
                    complength += 1;
                }
            }
            Ptr<ByteSlice> compptr = pcopy(((Ptr<ByteSlice>)malloc(complength * 8)));
            if (compptr == null)
            {
                Mem.error();
            }
            Slice<ByteSlice> comp = compptr.slice(0,complength).copy();
            int length = 0;
            int i = 0;
            {
                Dsymbol p = this;
                for (; p != null;p = p.parent.value){
                    BytePtr s = pcopy(QualifyTypes ? p.toPrettyCharsHelper() : p.toChars());
                    int len = strlen(s);
                    comp.set((i), s.slice(0,len));
                    i += 1;
                    length += len + 1;
                }
            }
            BytePtr s = pcopy(((BytePtr)Mem.xmalloc(length)));
            BytePtr q = pcopy(s.plus(length).minus(1));
            q.set(0, (byte)0);
            {
                int __key1138 = 0;
                int __limit1139 = complength;
                for (; (__key1138 < __limit1139);__key1138 += 1) {
                    int j = __key1138;
                    BytePtr t = pcopy(comp.get(j).getPtr(0));
                    int len = comp.get(j).getLength();
                    q.minusAssign(len);
                    memcpy((BytePtr)(q), (t), len);
                    if ((q == s))
                    {
                        break;
                    }
                    (q.minusAssign(1)).set(0, (byte)46);
                }
            }
            free(comp.getPtr(0));
            if (!QualifyTypes)
            {
                this.prettystring = pcopy(s);
            }
            return s;
        }

        // defaulted all parameters starting with #1
        public  BytePtr toPrettyChars() {
            return toPrettyChars(false);
        }

        // Erasure: kind<>
        public  BytePtr kind() {
            return new BytePtr("symbol");
        }

        // Erasure: toAlias<>
        public  Dsymbol toAlias() {
            return this;
        }

        // Erasure: toAlias2<>
        public  Dsymbol toAlias2() {
            return this.toAlias();
        }

        // Erasure: apply<Ptr, Ptr>
        public  int apply(Function2<Dsymbol,Object,Integer> fp, Object param) {
            return (fp).invoke(this, param);
        }

        // Erasure: addMember<Ptr, ScopeDsymbol>
        public  void addMember(Ptr<Scope> sc, ScopeDsymbol sds) {
            this.parent.value = sds;
            if (!this.isAnonymous())
            {
                if (sds.symtabInsert(this) == null)
                {
                    Dsymbol s2 = sds.symtabLookup(this, this.ident);
                    if (!s2.overloadInsert(this))
                    {
                        ScopeDsymbol.multiplyDefined(Loc.initial, this, s2);
                        this.errors = true;
                    }
                }
                if ((sds.isAggregateDeclaration() != null) || (sds.isEnumDeclaration() != null))
                {
                    if ((pequals(this.ident, Id.__sizeof)) || (pequals(this.ident, Id.__xalignof)) || (pequals(this.ident, Id._mangleof)))
                    {
                        this.error(new BytePtr("`.%s` property cannot be redefined"), this.ident.toChars());
                        this.errors = true;
                    }
                }
            }
        }

        // Erasure: setScope<Ptr>
        public  void setScope(Ptr<Scope> sc) {
            if (!(sc.get()).nofree)
            {
                (sc.get()).setNoFree();
            }
            this._scope = pcopy(sc);
            if ((sc.get()).depdecl != null)
            {
                this.depdecl = (sc.get()).depdecl;
            }
            if (this.userAttribDecl == null)
            {
                this.userAttribDecl = (sc.get()).userAttribDecl;
            }
        }

        // Erasure: importAll<Ptr>
        public  void importAll(Ptr<Scope> sc) {
        }

        // Erasure: search<Loc, Identifier, int>
        public  Dsymbol search(Loc loc, Identifier ident, int flags) {
            return null;
        }

        // defaulted all parameters starting with #3
        public  Dsymbol search(Loc loc, Identifier ident) {
            return search(loc, ident, 0);
        }

        // Erasure: search_correct<Identifier>
        public  Dsymbol search_correct(Identifier ident) {
            Function2<ByteSlice,Ref<Integer>,Dsymbol> symbol_search_fp = new Function2<ByteSlice,Ref<Integer>,Dsymbol>() {
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
                    cost.value = 0;
                    Dsymbol s = this;
                    dmodule.Module.clearCache();
                    return s.search(Loc.initial, id, 2);
                }}

            };
            if (global.gag != 0)
            {
                return null;
            }
            {
                Dsymbol s = this.search(Loc.initial, ident, 2);
                if ((s) != null)
                {
                    return s;
                }
            }
            return speller.invoke(ident.asString());
        }

        // Erasure: searchX<Loc, Ptr, RootObject, int>
        public  Dsymbol searchX(Loc loc, Ptr<Scope> sc, RootObject id, int flags) {
            Dsymbol s = this.toAlias();
            Dsymbol sm = null;
            {
                Declaration d = s.isDeclaration();
                if ((d) != null)
                {
                    if (d.inuse != 0)
                    {
                        error(loc, new BytePtr("circular reference to `%s`"), d.toPrettyChars(false));
                        return null;
                    }
                }
            }
            switch (id.dyncast())
            {
                case DYNCAST.identifier:
                    sm = s.search(loc, (Identifier)id, flags);
                    break;
                case DYNCAST.dsymbol:
                    Dsymbol st = (Dsymbol)id;
                    TemplateInstance ti = st.isTemplateInstance();
                    sm = s.search(loc, ti.name, 0);
                    if (sm == null)
                    {
                        sm = s.search_correct(ti.name);
                        if (sm != null)
                        {
                            error(loc, new BytePtr("template identifier `%s` is not a member of %s `%s`, did you mean %s `%s`?"), ti.name.toChars(), s.kind(), s.toPrettyChars(false), sm.kind(), sm.toChars());
                        }
                        else
                        {
                            error(loc, new BytePtr("template identifier `%s` is not a member of %s `%s`"), ti.name.toChars(), s.kind(), s.toPrettyChars(false));
                        }
                        return null;
                    }
                    sm = sm.toAlias();
                    TemplateDeclaration td = sm.isTemplateDeclaration();
                    if (td == null)
                    {
                        error(loc, new BytePtr("`%s.%s` is not a template, it is a %s"), s.toPrettyChars(false), ti.name.toChars(), sm.kind());
                        return null;
                    }
                    ti.tempdecl = td;
                    if (ti.semanticRun == 0)
                    {
                        dsymbolSemantic(ti, sc);
                    }
                    sm = ti.toAlias();
                    break;
                case DYNCAST.type:
                case DYNCAST.expression:
                default:
                throw new AssertionError("Unreachable code!");
            }
            return sm;
        }

        // Erasure: overloadInsert<Dsymbol>
        public  boolean overloadInsert(Dsymbol s) {
            return false;
        }

        // Erasure: size<Loc>
        public  long size(Loc loc) {
            this.error(new BytePtr("Dsymbol `%s` has no size"), this.toChars());
            return -1L;
        }

        // Erasure: isforwardRef<>
        public  boolean isforwardRef() {
            return false;
        }

        // Erasure: isThis<>
        public  AggregateDeclaration isThis() {
            return null;
        }

        // Erasure: isExport<>
        public  boolean isExport() {
            return false;
        }

        // Erasure: isImportedSymbol<>
        public  boolean isImportedSymbol() {
            return false;
        }

        // Erasure: isDeprecated<>
        public  boolean isDeprecated() {
            return false;
        }

        // Erasure: isOverloadable<>
        public  boolean isOverloadable() {
            return false;
        }

        // Erasure: isLabel<>
        public  LabelDsymbol isLabel() {
            return null;
        }

        // Erasure: isMember<>
        public  AggregateDeclaration isMember() {
            Dsymbol p = this.toParent();
            return p != null ? p.isAggregateDeclaration() : null;
        }

        // Erasure: isMember2<>
        public  AggregateDeclaration isMember2() {
            Dsymbol p = this.toParent2();
            return p != null ? p.isAggregateDeclaration() : null;
        }

        // Erasure: isMemberDecl<>
        public  AggregateDeclaration isMemberDecl() {
            Dsymbol p = this.toParentDecl();
            return p != null ? p.isAggregateDeclaration() : null;
        }

        // Erasure: isMemberLocal<>
        public  AggregateDeclaration isMemberLocal() {
            Dsymbol p = this.toParentLocal();
            return p != null ? p.isAggregateDeclaration() : null;
        }

        // Erasure: isClassMember<>
        public  ClassDeclaration isClassMember() {
            AggregateDeclaration ad = this.isMember();
            return ad != null ? ad.isClassDeclaration() : null;
        }

        // Erasure: getType<>
        public  Type getType() {
            return null;
        }

        // Erasure: needThis<>
        public  boolean needThis() {
            return false;
        }

        // Erasure: prot<>
        public  Prot prot() {
            return new Prot(Prot.Kind.public_);
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            printf(new BytePtr("%s %s\n"), this.kind(), this.toChars());
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: oneMember<Ptr, Identifier>
        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            ps.set(0, this);
            return true;
        }

        // Erasure: oneMembers<Ptr, Ptr, Identifier>
        public static boolean oneMembers(Ptr<DArray<Dsymbol>> members, Ptr<Dsymbol> ps, Identifier ident) {
            Dsymbol s = null;
            if (members != null)
            {
                {
                    int i = 0;
                    for (; (i < (members.get()).length);i++){
                        Dsymbol sx = (members.get()).get(i);
                        boolean x = sx.oneMember(ps, ident);
                        if (!x)
                        {
                            assert((ps.get() == null));
                            return false;
                        }
                        if (ps.get() != null)
                        {
                            assert(ident != null);
                            if (((ps.get()).ident == null) || !(ps.get()).ident.equals(ident))
                            {
                                continue;
                            }
                            if (s == null)
                            {
                                s = ps.get();
                            }
                            else if (s.isOverloadable() && (ps.get()).isOverloadable())
                            {
                                FuncDeclaration f1 = s.isFuncDeclaration();
                                FuncDeclaration f2 = (ps.get()).isFuncDeclaration();
                                if ((f1 != null) && (f2 != null))
                                {
                                    assert(f1.isFuncAliasDeclaration() == null);
                                    assert(f2.isFuncAliasDeclaration() == null);
                                    for (; (!pequals(f1, f2));f1 = f1.overnext0){
                                        if ((f1.overnext0 == null))
                                        {
                                            f1.overnext0 = f2;
                                            break;
                                        }
                                    }
                                }
                            }
                            else
                            {
                                ps.set(0, null);
                                return false;
                            }
                        }
                    }
                }
            }
            ps.set(0, s);
            return true;
        }

        // Erasure: setFieldOffset<AggregateDeclaration, Ptr, boolean>
        public  void setFieldOffset(AggregateDeclaration ad, Ptr<Integer> poffset, boolean isunion) {
        }

        // Erasure: hasPointers<>
        public  boolean hasPointers() {
            return false;
        }

        // Erasure: hasStaticCtorOrDtor<>
        public  boolean hasStaticCtorOrDtor() {
            return false;
        }

        // Erasure: addLocalClass<Ptr>
        public  void addLocalClass(Ptr<DArray<ClassDeclaration>> _param_0) {
        }

        // Erasure: addObjcSymbols<Ptr, Ptr>
        public  void addObjcSymbols(Ptr<DArray<ClassDeclaration>> classes, Ptr<DArray<ClassDeclaration>> categories) {
        }

        // Erasure: checkCtorConstInit<>
        public  void checkCtorConstInit() {
        }

        // Erasure: addComment<Ptr>
        public  void addComment(BytePtr comment) {
            if (this.comment == null)
            {
                this.comment = pcopy(comment);
            }
            else if ((comment != null) && (strcmp(comment, this.comment) != 0))
            {
                this.comment = pcopy(Lexer.combineComments(this.comment, comment, true));
            }
        }

        // Erasure: inNonRoot<>
        public  boolean inNonRoot() {
            Dsymbol s = this.parent.value;
            for (; s != null;s = s.toParent()){
                {
                    TemplateInstance ti = s.isTemplateInstance();
                    if ((ti) != null)
                    {
                        return false;
                    }
                }
                {
                    dmodule.Module m = s.isModule();
                    if ((m) != null)
                    {
                        if (!m.isRoot())
                        {
                            return true;
                        }
                        break;
                    }
                }
            }
            return false;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }

        // Erasure: isPackage<>
        public  dmodule.Package isPackage() {
            return null;
        }

        // Erasure: isModule<>
        public  dmodule.Module isModule() {
            return null;
        }

        // Erasure: isEnumMember<>
        public  EnumMember isEnumMember() {
            return null;
        }

        // Erasure: isTemplateDeclaration<>
        public  TemplateDeclaration isTemplateDeclaration() {
            return null;
        }

        // Erasure: isTemplateInstance<>
        public  TemplateInstance isTemplateInstance() {
            return null;
        }

        // Erasure: isTemplateMixin<>
        public  TemplateMixin isTemplateMixin() {
            return null;
        }

        // Erasure: isForwardingAttribDeclaration<>
        public  ForwardingAttribDeclaration isForwardingAttribDeclaration() {
            return null;
        }

        // Erasure: isNspace<>
        public  Nspace isNspace() {
            return null;
        }

        // Erasure: isDeclaration<>
        public  Declaration isDeclaration() {
            return null;
        }

        // Erasure: isStorageClassDeclaration<>
        public  StorageClassDeclaration isStorageClassDeclaration() {
            return null;
        }

        // Erasure: isExpressionDsymbol<>
        public  ExpressionDsymbol isExpressionDsymbol() {
            return null;
        }

        // Erasure: isThisDeclaration<>
        public  ThisDeclaration isThisDeclaration() {
            return null;
        }

        // Erasure: isTypeInfoDeclaration<>
        public  TypeInfoDeclaration isTypeInfoDeclaration() {
            return null;
        }

        // Erasure: isTupleDeclaration<>
        public  TupleDeclaration isTupleDeclaration() {
            return null;
        }

        // Erasure: isAliasDeclaration<>
        public  AliasDeclaration isAliasDeclaration() {
            return null;
        }

        // Erasure: isAggregateDeclaration<>
        public  AggregateDeclaration isAggregateDeclaration() {
            return null;
        }

        // Erasure: isFuncDeclaration<>
        public  FuncDeclaration isFuncDeclaration() {
            return null;
        }

        // Erasure: isFuncAliasDeclaration<>
        public  FuncAliasDeclaration isFuncAliasDeclaration() {
            return null;
        }

        // Erasure: isOverDeclaration<>
        public  OverDeclaration isOverDeclaration() {
            return null;
        }

        // Erasure: isFuncLiteralDeclaration<>
        public  FuncLiteralDeclaration isFuncLiteralDeclaration() {
            return null;
        }

        // Erasure: isCtorDeclaration<>
        public  CtorDeclaration isCtorDeclaration() {
            return null;
        }

        // Erasure: isPostBlitDeclaration<>
        public  PostBlitDeclaration isPostBlitDeclaration() {
            return null;
        }

        // Erasure: isDtorDeclaration<>
        public  DtorDeclaration isDtorDeclaration() {
            return null;
        }

        // Erasure: isStaticCtorDeclaration<>
        public  StaticCtorDeclaration isStaticCtorDeclaration() {
            return null;
        }

        // Erasure: isStaticDtorDeclaration<>
        public  StaticDtorDeclaration isStaticDtorDeclaration() {
            return null;
        }

        // Erasure: isSharedStaticCtorDeclaration<>
        public  SharedStaticCtorDeclaration isSharedStaticCtorDeclaration() {
            return null;
        }

        // Erasure: isSharedStaticDtorDeclaration<>
        public  SharedStaticDtorDeclaration isSharedStaticDtorDeclaration() {
            return null;
        }

        // Erasure: isInvariantDeclaration<>
        public  InvariantDeclaration isInvariantDeclaration() {
            return null;
        }

        // Erasure: isUnitTestDeclaration<>
        public  UnitTestDeclaration isUnitTestDeclaration() {
            return null;
        }

        // Erasure: isNewDeclaration<>
        public  NewDeclaration isNewDeclaration() {
            return null;
        }

        // Erasure: isVarDeclaration<>
        public  VarDeclaration isVarDeclaration() {
            return null;
        }

        // Erasure: isClassDeclaration<>
        public  ClassDeclaration isClassDeclaration() {
            return null;
        }

        // Erasure: isStructDeclaration<>
        public  StructDeclaration isStructDeclaration() {
            return null;
        }

        // Erasure: isUnionDeclaration<>
        public  UnionDeclaration isUnionDeclaration() {
            return null;
        }

        // Erasure: isInterfaceDeclaration<>
        public  InterfaceDeclaration isInterfaceDeclaration() {
            return null;
        }

        // Erasure: isScopeDsymbol<>
        public  ScopeDsymbol isScopeDsymbol() {
            return null;
        }

        // Erasure: isForwardingScopeDsymbol<>
        public  ForwardingScopeDsymbol isForwardingScopeDsymbol() {
            return null;
        }

        // Erasure: isWithScopeSymbol<>
        public  WithScopeSymbol isWithScopeSymbol() {
            return null;
        }

        // Erasure: isArrayScopeSymbol<>
        public  ArrayScopeSymbol isArrayScopeSymbol() {
            return null;
        }

        // Erasure: isImport<>
        public  Import isImport() {
            return null;
        }

        // Erasure: isEnumDeclaration<>
        public  EnumDeclaration isEnumDeclaration() {
            return null;
        }

        // Erasure: isDeleteDeclaration<>
        public  DeleteDeclaration isDeleteDeclaration() {
            return null;
        }

        // Erasure: isSymbolDeclaration<>
        public  SymbolDeclaration isSymbolDeclaration() {
            return null;
        }

        // Erasure: isAttribDeclaration<>
        public  AttribDeclaration isAttribDeclaration() {
            return null;
        }

        // Erasure: isAnonDeclaration<>
        public  AnonDeclaration isAnonDeclaration() {
            return null;
        }

        // Erasure: isCPPNamespaceDeclaration<>
        public  CPPNamespaceDeclaration isCPPNamespaceDeclaration() {
            return null;
        }

        // Erasure: isProtDeclaration<>
        public  ProtDeclaration isProtDeclaration() {
            return null;
        }

        // Erasure: isOverloadSet<>
        public  OverloadSet isOverloadSet() {
            return null;
        }

        // Erasure: isCompileDeclaration<>
        public  CompileDeclaration isCompileDeclaration() {
            return null;
        }


        public Dsymbol copy() {
            Dsymbol that = new Dsymbol();
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
    public static class ScopeDsymbol extends Dsymbol
    {
        public Ptr<DArray<Dsymbol>> members = null;
        public DsymbolTable symtab = null;
        public int endlinnum = 0;
        public Ptr<DArray<Dsymbol>> importedScopes = null;
        public Ptr<Integer> prots = null;
        public Ref<BitArray> accessiblePackages = ref(new BitArray());
        public Ref<BitArray> privateAccessiblePackages = ref(new BitArray());
        // Erasure: __ctor<>
        public  ScopeDsymbol() {
            super();
        }

        // Erasure: __ctor<Identifier>
        public  ScopeDsymbol(Identifier ident) {
            super(ident);
        }

        // Erasure: __ctor<Loc, Identifier>
        public  ScopeDsymbol(Loc loc, Identifier ident) {
            super(loc, ident);
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            ScopeDsymbol sds = s != null ? (ScopeDsymbol)s : new ScopeDsymbol(this.ident);
            sds.members = pcopy(Dsymbol.arraySyntaxCopy(this.members));
            sds.endlinnum = this.endlinnum;
            return sds;
        }

        // Erasure: search<Loc, Identifier, int>
        public  Dsymbol search(Loc loc, Identifier ident, int flags) {
            if ((this.symtab != null) && ((flags & 16) == 0))
            {
                Dsymbol s1 = this.symtab.lookup(ident);
                if (s1 != null)
                {
                    return s1;
                }
            }
            if (this.importedScopes == null)
            {
                return null;
            }
            Dsymbol s = null;
            OverloadSet a = null;
            {
                int i = 0;
                for (; (i < (this.importedScopes.get()).length);i++){
                    if (((flags & 1) != 0) && (this.prots.get(i) == Prot.Kind.private_))
                    {
                        continue;
                    }
                    int sflags = flags & 6;
                    Dsymbol ss = (this.importedScopes.get()).get(i);
                    if (ss.isModule() != null)
                    {
                        if ((flags & 8) != 0)
                        {
                            continue;
                        }
                    }
                    else
                    {
                        if ((flags & 16) != 0)
                        {
                            continue;
                        }
                        sflags |= 8;
                    }
                    Dsymbol s2 = ss.search(loc, ident, sflags | (ss.isModule() != null ? 1 : 0));
                    if ((s2 == null) || ((flags & 128) == 0) && !symbolIsVisible(this, s2))
                    {
                        continue;
                    }
                    if (s == null)
                    {
                        s = s2;
                        if ((s != null) && (s.isOverloadSet() != null))
                        {
                            a = this.mergeOverloadSet(ident, a, s);
                        }
                    }
                    else if ((s2 != null) && (!pequals(s, s2)))
                    {
                        if ((pequals(s.toAlias(), s2.toAlias())) || (pequals(s.getType(), s2.getType())) && (s.getType() != null))
                        {
                            if (s.isDeprecated() || s.prot().isMoreRestrictiveThan(s2.prot()) && (s2.prot().kind != Prot.Kind.none))
                            {
                                s = s2;
                            }
                        }
                        else
                        {
                            Import i1 = s.isImport();
                            Import i2 = s2.isImport();
                            if (!((i1 != null) && (i2 != null) && (pequals(i1.mod, i2.mod)) || (i1.parent.value.isImport() == null) && (i2.parent.value.isImport() == null) && i1.ident.equals(i2.ident)))
                            {
                                s = s.toAlias();
                                s2 = s2.toAlias();
                                if ((s2.isOverloadSet() != null) || s2.isOverloadable() && (a != null) || s.isOverloadable())
                                {
                                    if (symbolIsVisible(this, s2))
                                    {
                                        a = this.mergeOverloadSet(ident, a, s2);
                                    }
                                    if (!symbolIsVisible(this, s))
                                    {
                                        s = s2;
                                    }
                                    continue;
                                }
                                if ((flags & 4) != 0)
                                {
                                    return null;
                                }
                                if ((flags & 2) == 0)
                                {
                                    multiplyDefined(loc, s, s2);
                                }
                                break;
                            }
                        }
                    }
                }
            }
            if (s != null)
            {
                if (a != null)
                {
                    if (s.isOverloadSet() == null)
                    {
                        a = this.mergeOverloadSet(ident, a, s);
                    }
                    s = a;
                }
                return s;
            }
            return null;
        }

        // defaulted all parameters starting with #3
        public  Dsymbol search(Loc loc, Identifier ident) {
            return search(loc, ident, 8);
        }

        // Erasure: mergeOverloadSet<Identifier, OverloadSet, Dsymbol>
        public  OverloadSet mergeOverloadSet(Identifier ident, OverloadSet os, Dsymbol s) {
            if (os == null)
            {
                os = new OverloadSet(ident, null);
                os.parent.value = this;
            }
            {
                OverloadSet os2 = s.isOverloadSet();
                if ((os2) != null)
                {
                    if ((os.a.length == 0))
                    {
                        os.a.setDim(os2.a.length);
                        memcpy((BytePtr)(os.a.tdata()), (os2.a.tdata()), (4 * os2.a.length));
                    }
                    else
                    {
                        {
                            int i = 0;
                            for (; (i < os2.a.length);i++){
                                os = this.mergeOverloadSet(ident, os, os2.a.get(i));
                            }
                        }
                    }
                }
                else
                {
                    assert(s.isOverloadable());
                    try {
                        {
                            int j = 0;
                        L_outer1:
                            for (; (j < os.a.length);j++){
                                Dsymbol s2 = os.a.get(j);
                                if ((pequals(s.toAlias(), s2.toAlias())))
                                {
                                    if (s2.isDeprecated() || s2.prot().isMoreRestrictiveThan(s.prot()) && (s.prot().kind != Prot.Kind.none))
                                    {
                                        os.a.set(j, s);
                                    }
                                    /*goto Lcontinue*/throw Dispatch0.INSTANCE;
                                }
                            }
                        }
                        os.push(s);
                    }
                    catch(Dispatch0 __d){}
                /*Lcontinue:*/
                }
            }
            return os;
        }

        // Erasure: importScope<Dsymbol, Prot>
        public  void importScope(Dsymbol s, Prot protection) {
            if ((!pequals(s, this)))
            {
                if (this.importedScopes == null)
                {
                    this.importedScopes = pcopy((refPtr(new DArray<Dsymbol>())));
                }
                else
                {
                    {
                        int i = 0;
                        for (; (i < (this.importedScopes.get()).length);i++){
                            Dsymbol ss = (this.importedScopes.get()).get(i);
                            if ((pequals(ss, s)))
                            {
                                if ((protection.kind > this.prots.get(i)))
                                {
                                    this.prots.set(i, protection.kind);
                                }
                                return ;
                            }
                        }
                    }
                }
                (this.importedScopes.get()).push(s);
                this.prots = pcopy((((Ptr<Integer>)Mem.xrealloc(this.prots, (this.importedScopes.get()).length * 4))));
                this.prots.set(((this.importedScopes.get()).length - 1), protection.kind);
            }
        }

        // Erasure: addAccessiblePackage<Package, Prot>
        public  void addAccessiblePackage(dmodule.Package p, Prot protection) {
            if ((p == null))
            {
                return ;
            }
            Ptr<BitArray> pary = (protection.kind == Prot.Kind.private_) ? ptr(this.privateAccessiblePackages) : ptr(this.accessiblePackages);
            if (((pary.get()).length() <= p.tag))
            {
                (pary.get()).length(p.tag + 1);
            }
            (pary.get()).opIndexAssign(true, p.tag);
        }

        // Erasure: isPackageAccessible<Package, Prot, int>
        public  boolean isPackageAccessible(dmodule.Package p, Prot protection, int flags) {
            if ((p.tag < this.accessiblePackages.value.length()) && this.accessiblePackages.value.get(p.tag) || (protection.kind == Prot.Kind.private_) && (p.tag < this.privateAccessiblePackages.value.length()) && this.privateAccessiblePackages.value.get(p.tag))
            {
                return true;
            }
            {
                Slice<Dsymbol> __r1149 = (this.importedScopes != null ? (this.importedScopes.get()).opSlice() : new RawSlice<Dsymbol>()).copy();
                int __key1148 = 0;
                for (; (__key1148 < __r1149.getLength());__key1148 += 1) {
                    Dsymbol ss = __r1149.get(__key1148);
                    int i = __key1148;
                    if ((protection.kind <= this.prots.get(i)) && ss.isScopeDsymbol().isPackageAccessible(p, protection, 1))
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        // defaulted all parameters starting with #3
        public  boolean isPackageAccessible(dmodule.Package p, Prot protection) {
            return isPackageAccessible(p, protection, 0);
        }

        // Erasure: isforwardRef<>
        public  boolean isforwardRef() {
            return this.members == null;
        }

        // Erasure: multiplyDefined<Loc, Dsymbol, Dsymbol>
        public static void multiplyDefined(Loc loc, Dsymbol s1, Dsymbol s2) {
            if (loc.isValid())
            {
                error(loc, new BytePtr("`%s` at %s conflicts with `%s` at %s"), s1.toPrettyChars(false), s1.locToChars(), s2.toPrettyChars(false), s2.locToChars());
            }
            else
            {
                s1.error(s1.loc, new BytePtr("conflicts with %s `%s` at %s"), s2.kind(), s2.toPrettyChars(false), s2.locToChars());
            }
        }

        // Erasure: kind<>
        public  BytePtr kind() {
            return new BytePtr("ScopeDsymbol");
        }

        // Erasure: findGetMembers<>
        public  FuncDeclaration findGetMembers() {
            Dsymbol s = search_function(this, Id.getmembers);
            FuncDeclaration fdx = s != null ? s.isFuncDeclaration() : null;
            if ((fdx != null) && fdx.isVirtual())
            {
                fdx = null;
            }
            return fdx;
        }

        // Erasure: symtabInsert<Dsymbol>
        public  Dsymbol symtabInsert(Dsymbol s) {
            return this.symtab.insert(s);
        }

        // Erasure: symtabLookup<Dsymbol, Identifier>
        public  Dsymbol symtabLookup(Dsymbol s, Identifier id) {
            return this.symtab.lookup(id);
        }

        // Erasure: hasStaticCtorOrDtor<>
        public  boolean hasStaticCtorOrDtor() {
            if (this.members != null)
            {
                {
                    int i = 0;
                    for (; (i < (this.members.get()).length);i++){
                        Dsymbol member = (this.members.get()).get(i);
                        if (member.hasStaticCtorOrDtor())
                        {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        // Erasure: _foreach<Ptr, Ptr, Function2, Ptr>
        public static int _foreach(Ptr<Scope> sc, Ptr<DArray<Dsymbol>> members, Function2<Integer,Dsymbol,Integer> dg, Ptr<Integer> pn) {
            assert(dg != null);
            if (members == null)
            {
                return 0;
            }
            Ref<Integer> n = ref(pn != null ? pn.get() : 0);
            int result = 0;
            {
                int __key1150 = 0;
                int __limit1151 = (members.get()).length;
                for (; (__key1150 < __limit1151);__key1150 += 1) {
                    int i = __key1150;
                    Dsymbol s = (members.get()).get(i);
                    {
                        AttribDeclaration a = s.isAttribDeclaration();
                        if ((a) != null)
                        {
                            result = _foreach(sc, a.include(sc), dg, ptr(n));
                        }
                        else {
                            TemplateMixin tm = s.isTemplateMixin();
                            if ((tm) != null)
                            {
                                result = _foreach(sc, tm.members, dg, ptr(n));
                            }
                            else if (s.isTemplateInstance() != null)
                            {
                            }
                            else if (s.isUnitTestDeclaration() != null)
                            {
                            }
                            else
                            {
                                result = dg.invoke(n.value++, s);
                            }
                        }
                    }
                    if (result != 0)
                    {
                        break;
                    }
                }
            }
            if (pn != null)
            {
                pn.set(0, n.value);
            }
            return result;
        }

        // defaulted all parameters starting with #4
        public static int _foreach(Ptr<Scope> sc, Ptr<DArray<Dsymbol>> members, Function2<Integer,Dsymbol,Integer> dg) {
            return _foreach(sc, members, dg, (Ptr<Integer>)null);
        }

        // Erasure: isScopeDsymbol<>
        public  ScopeDsymbol isScopeDsymbol() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ScopeDsymbol copy() {
            ScopeDsymbol that = new ScopeDsymbol();
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
    public static class WithScopeSymbol extends ScopeDsymbol
    {
        public WithStatement withstate = null;
        // Erasure: __ctor<WithStatement>
        public  WithScopeSymbol(WithStatement withstate) {
            super();
            this.withstate = withstate;
        }

        // Erasure: search<Loc, Identifier, int>
        public  Dsymbol search(Loc loc, Identifier ident, int flags) {
            if ((flags & 16) != 0)
            {
                return null;
            }
            Dsymbol s = null;
            Expression eold = null;
            {
                Expression e = this.withstate.exp;
                for (; (!pequals(e, eold));e = resolveAliasThis(this._scope, e, false)){
                    if (((e.op & 0xFF) == 203))
                    {
                        s = ((ScopeExp)e).sds;
                    }
                    else if (((e.op & 0xFF) == 20))
                    {
                        s = e.type.value.toDsymbol(null);
                    }
                    else
                    {
                        Type t = e.type.value.toBasetype();
                        s = t.toDsymbol(null);
                    }
                    if (s != null)
                    {
                        s = s.search(loc, ident, flags);
                        if (s != null)
                        {
                            return s;
                        }
                    }
                    eold = e;
                }
            }
            return null;
        }

        // defaulted all parameters starting with #3
        public  Dsymbol search(Loc loc, Identifier ident) {
            return search(loc, ident, 8);
        }

        // Erasure: isWithScopeSymbol<>
        public  WithScopeSymbol isWithScopeSymbol() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public WithScopeSymbol() {}

        public WithScopeSymbol copy() {
            WithScopeSymbol that = new WithScopeSymbol();
            that.withstate = this.withstate;
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
    public static class ArrayScopeSymbol extends ScopeDsymbol
    {
        public Expression exp = null;
        public TypeTuple type = null;
        public TupleDeclaration td = null;
        public Ptr<Scope> sc = null;
        // Erasure: __ctor<Ptr, Expression>
        public  ArrayScopeSymbol(Ptr<Scope> sc, Expression exp) {
            super(exp.loc, null);
            assert(((exp.op & 0xFF) == 62) || ((exp.op & 0xFF) == 31) || ((exp.op & 0xFF) == 17));
            this.exp = exp;
            this.sc = pcopy(sc);
        }

        // Erasure: __ctor<Ptr, TypeTuple>
        public  ArrayScopeSymbol(Ptr<Scope> sc, TypeTuple type) {
            super();
            this.type = type;
            this.sc = pcopy(sc);
        }

        // Erasure: __ctor<Ptr, TupleDeclaration>
        public  ArrayScopeSymbol(Ptr<Scope> sc, TupleDeclaration td) {
            super();
            this.td = td;
            this.sc = pcopy(sc);
        }

        // Erasure: search<Loc, Identifier, int>
        public  Dsymbol search(Loc loc, Identifier ident, int flags) {
            if ((!pequals(ident, Id.dollar)))
            {
                return null;
            }
            Ptr<VarDeclaration> pvar = null;
            Expression ce = null;
            while(true) try {
            /*L1:*/
                if (this.td != null)
                {
                    VarDeclaration v = new VarDeclaration(loc, Type.tsize_t, Id.dollar, null, 0L);
                    Expression e = new IntegerExp(Loc.initial, (long)(this.td.objects.get()).length, Type.tsize_t);
                    v._init = new ExpInitializer(Loc.initial, e);
                    v.storage_class |= 1099511627781L;
                    dsymbolSemantic(v, this.sc);
                    return v;
                }
                if (this.type != null)
                {
                    VarDeclaration v = new VarDeclaration(loc, Type.tsize_t, Id.dollar, null, 0L);
                    Expression e = new IntegerExp(Loc.initial, (long)(this.type.arguments.get()).length, Type.tsize_t);
                    v._init = new ExpInitializer(Loc.initial, e);
                    v.storage_class |= 1099511627781L;
                    dsymbolSemantic(v, this.sc);
                    return v;
                }
                if (((this.exp.op & 0xFF) == 62))
                {
                    IndexExp ie = (IndexExp)this.exp;
                    pvar = pcopy((ptr(ie.lengthVar)));
                    ce = ie.e1.value;
                }
                else if (((this.exp.op & 0xFF) == 31))
                {
                    SliceExp se = (SliceExp)this.exp;
                    pvar = pcopy((ptr(se.lengthVar)));
                    ce = se.e1.value;
                }
                else if (((this.exp.op & 0xFF) == 17))
                {
                    ArrayExp ae = (ArrayExp)this.exp;
                    pvar = pcopy((ptr(ae.lengthVar)));
                    ce = ae.e1.value;
                }
                else
                {
                    return null;
                }
                for (; ((ce.op & 0xFF) == 99);) {
                    ce = ((CommaExp)ce).e2.value;
                }
                if (((ce.op & 0xFF) == 20))
                {
                    Type t = ((TypeExp)ce).type.value;
                    if (((t.ty & 0xFF) == ENUMTY.Ttuple))
                    {
                        this.type = (TypeTuple)t;
                        /*goto L1*/throw Dispatch0.INSTANCE;
                    }
                }
                if (pvar.get() == null)
                {
                    VarDeclaration v = null;
                    Type t = null;
                    if (((ce.op & 0xFF) == 126))
                    {
                        Expression e = new IntegerExp(Loc.initial, (long)(((TupleExp)ce).exps.get()).length, Type.tsize_t);
                        v = new VarDeclaration(loc, Type.tsize_t, Id.dollar, new ExpInitializer(Loc.initial, e), 0L);
                        v.storage_class |= 1099511627781L;
                    }
                    else if ((ce.type.value != null) && ((t = ce.type.value.toBasetype()) != null) && ((t.ty & 0xFF) == ENUMTY.Tstruct) || ((t.ty & 0xFF) == ENUMTY.Tclass))
                    {
                        assert(((this.exp.op & 0xFF) == 17) || ((this.exp.op & 0xFF) == 31));
                        AggregateDeclaration ad = isAggregate(t);
                        assert(ad != null);
                        Dsymbol s = ad.search(loc, Id.opDollar, 8);
                        if (s == null)
                        {
                            return null;
                        }
                        s = s.toAlias();
                        Expression e = null;
                        {
                            TemplateDeclaration td = s.isTemplateDeclaration();
                            if ((td) != null)
                            {
                                long dim = 0L;
                                if (((this.exp.op & 0xFF) == 17))
                                {
                                    dim = (long)((ArrayExp)this.exp).currentDimension;
                                }
                                else if (((this.exp.op & 0xFF) == 31))
                                {
                                    dim = 0L;
                                }
                                else
                                {
                                    throw new AssertionError("Unreachable code!");
                                }
                                Ptr<DArray<RootObject>> tiargs = refPtr(new DArray<RootObject>());
                                Expression edim = new IntegerExp(Loc.initial, dim, Type.tsize_t);
                                edim = expressionSemantic(edim, this.sc);
                                (tiargs.get()).push(edim);
                                e = new DotTemplateInstanceExp(loc, ce, td.ident, tiargs);
                            }
                            else
                            {
                                if (((this.exp.op & 0xFF) == 17) && ((((ArrayExp)this.exp).arguments.get()).length != 1))
                                {
                                    this.exp.error(new BytePtr("`%s` only defines opDollar for one dimension"), ad.toChars());
                                    return null;
                                }
                                Declaration d = s.isDeclaration();
                                assert(d != null);
                                e = new DotVarExp(loc, ce, d, true);
                            }
                        }
                        e = expressionSemantic(e, this.sc);
                        if (e.type.value == null)
                        {
                            this.exp.error(new BytePtr("`%s` has no value"), e.toChars());
                        }
                        t = e.type.value.toBasetype();
                        if ((t != null) && ((t.ty & 0xFF) == ENUMTY.Tfunction))
                        {
                            e = new CallExp(e.loc, e);
                        }
                        v = new VarDeclaration(loc, null, Id.dollar, new ExpInitializer(Loc.initial, e), 0L);
                        v.storage_class |= 3367254360064L;
                    }
                    else
                    {
                        VoidInitializer e = new VoidInitializer(Loc.initial);
                        e.type = Type.tsize_t;
                        v = new VarDeclaration(loc, Type.tsize_t, Id.dollar, e, 0L);
                        v.storage_class |= 1168231104512L;
                    }
                    pvar.set(0, v);
                }
                dsymbolSemantic(pvar.get(), this.sc);
                return pvar.get();
                break;
            } catch(Dispatch0 __d){}
        }

        // defaulted all parameters starting with #3
        public  Dsymbol search(Loc loc, Identifier ident) {
            return search(loc, ident, 0);
        }

        // Erasure: isArrayScopeSymbol<>
        public  ArrayScopeSymbol isArrayScopeSymbol() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ArrayScopeSymbol() {}

        public ArrayScopeSymbol copy() {
            ArrayScopeSymbol that = new ArrayScopeSymbol();
            that.exp = this.exp;
            that.type = this.type;
            that.td = this.td;
            that.sc = this.sc;
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
    public static class OverloadSet extends Dsymbol
    {
        public DArray<Dsymbol> a = new DArray<Dsymbol>();
        // Erasure: __ctor<Identifier, OverloadSet>
        public  OverloadSet(Identifier ident, OverloadSet os) {
            super(ident);
            if (os != null)
            {
                this.a.pushSlice(os.a.opSlice());
            }
        }

        // defaulted all parameters starting with #2
        public  OverloadSet(Identifier ident) {
            this(ident, (OverloadSet)null);
        }

        // Erasure: push<Dsymbol>
        public  void push(Dsymbol s) {
            this.a.push(s);
        }

        // Erasure: isOverloadSet<>
        public  OverloadSet isOverloadSet() {
            return this;
        }

        // Erasure: kind<>
        public  BytePtr kind() {
            return new BytePtr("overloadset");
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public OverloadSet() {}

        public OverloadSet copy() {
            OverloadSet that = new OverloadSet();
            that.a = this.a;
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
    public static class ForwardingScopeDsymbol extends ScopeDsymbol
    {
        public ScopeDsymbol forward = null;
        // Erasure: __ctor<ScopeDsymbol>
        public  ForwardingScopeDsymbol(ScopeDsymbol forward) {
            super((Identifier)null);
            this.forward = forward;
        }

        // Erasure: symtabInsert<Dsymbol>
        public  Dsymbol symtabInsert(Dsymbol s) {
            assert(this.forward != null);
            {
                Declaration d = s.isDeclaration();
                if ((d) != null)
                {
                    if ((d.storage_class & 2251799813685248L) != 0)
                    {
                        if (this.symtab == null)
                        {
                            this.symtab = new DsymbolTable();
                        }
                        return super.symtabInsert(s);
                    }
                }
            }
            if (this.forward.symtab == null)
            {
                this.forward.symtab = new DsymbolTable();
            }
            return this.forward.symtabInsert(s);
        }

        // Erasure: symtabLookup<Dsymbol, Identifier>
        public  Dsymbol symtabLookup(Dsymbol s, Identifier id) {
            assert(this.forward != null);
            {
                Declaration d = s.isDeclaration();
                if ((d) != null)
                {
                    if ((d.storage_class & 2251799813685248L) != 0)
                    {
                        if (this.symtab == null)
                        {
                            this.symtab = new DsymbolTable();
                        }
                        return super.symtabLookup(s, id);
                    }
                }
            }
            if (this.forward.symtab == null)
            {
                this.forward.symtab = new DsymbolTable();
            }
            return this.forward.symtabLookup(s, id);
        }

        // Erasure: importScope<Dsymbol, Prot>
        public  void importScope(Dsymbol s, Prot protection) {
            this.forward.importScope(s, protection);
        }

        // Erasure: kind<>
        public  BytePtr kind() {
            return new BytePtr("local scope");
        }

        // Erasure: isForwardingScopeDsymbol<>
        public  ForwardingScopeDsymbol isForwardingScopeDsymbol() {
            return this;
        }


        public ForwardingScopeDsymbol() {}

        public ForwardingScopeDsymbol copy() {
            ForwardingScopeDsymbol that = new ForwardingScopeDsymbol();
            that.forward = this.forward;
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
    public static class ExpressionDsymbol extends Dsymbol
    {
        public Expression exp = null;
        // Erasure: __ctor<Expression>
        public  ExpressionDsymbol(Expression exp) {
            super();
            this.exp = exp;
        }

        // Erasure: isExpressionDsymbol<>
        public  ExpressionDsymbol isExpressionDsymbol() {
            return this;
        }


        public ExpressionDsymbol() {}

        public ExpressionDsymbol copy() {
            ExpressionDsymbol that = new ExpressionDsymbol();
            that.exp = this.exp;
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
    public static class DsymbolTable extends RootObject
    {
        public AA<Identifier,Dsymbol> tab = new AA<Identifier,Dsymbol>();
        // Erasure: lookup<Identifier>
        public  Dsymbol lookup(Identifier ident) {
            return this.tab.get(ident);
        }

        // Erasure: insert<Dsymbol>
        public  Dsymbol insert(Dsymbol s) {
            return this.insert(s.ident, s);
        }

        // Erasure: update<Dsymbol>
        public  Dsymbol update(Dsymbol s) {
            Identifier ident = s.ident;
            Ptr<Dsymbol> ps = pcopy(this.tab.getLvalue(ident));
            ps.set(0, s);
            return s;
        }

        // Erasure: insert<Identifier, Dsymbol>
        public  Dsymbol insert(Identifier ident, Dsymbol s) {
            Ptr<Dsymbol> ps = pcopy(this.tab.getLvalue(ident));
            if (ps.get() != null)
            {
                return null;
            }
            ps.set(0, s);
            return s;
        }

        // Erasure: len<>
        public  int len() {
            return this.tab.length();
        }

        // Erasure: __ctor<>
        public  DsymbolTable() {
            super();
        }


        public DsymbolTable copy() {
            DsymbolTable that = new DsymbolTable();
            that.tab = this.tab;
            return that;
        }
    }
}
