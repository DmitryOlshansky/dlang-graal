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

    public static int foreachDsymbol(DArray<Dsymbol> symbols, Function1<Dsymbol,Integer> dg) {
        assert(dg != null);
        if (symbols != null)
        {
            {
                int i = 0;
                for (; (i < (symbols).length);i += 1){
                    Dsymbol s = (symbols).get(i);
                    int result = dg.invoke(s);
                    if (result != 0)
                        return result;
                }
            }
        }
        return 0;
    }

    public static void foreachDsymbol(DArray<Dsymbol> symbols, Function1<Dsymbol,Void> dg) {
        assert(dg != null);
        if (symbols != null)
        {
            {
                int i = 0;
                for (; (i < (symbols).length);i += 1){
                    Dsymbol s = (symbols).get(i);
                    dg.invoke(s);
                }
            }
        }
    }

    public static class Ungag
    {
        public int oldgag;
        public  Ungag(int old) {
            this.oldgag = old;
        }

        public Ungag(){
        }
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

        public int kind;
        public dmodule.Package pkg;
        public  Prot(int kind) {
            this.kind = kind;
        }

        public  boolean isMoreRestrictiveThan(Prot other) {
            return this.kind < other.kind;
        }

        public  boolean opEquals(Prot other) {
            if ((this.kind == other.kind))
            {
                if ((this.kind == Kind.package_))
                    return pequals(this.pkg, other.pkg);
                return true;
            }
            return false;
        }

        public  boolean isSubsetOf(Prot parent) {
            if ((this.kind != parent.kind))
                return false;
            if ((this.kind == Kind.package_))
            {
                if (this.pkg == null)
                    return true;
                if (parent.pkg == null)
                    return false;
                if (parent.pkg.isAncestorPackageOf(this.pkg))
                    return true;
            }
            return true;
        }

        public Prot(){
        }
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


    public static class 
    {
        public static final int IgnoreNone = 0;
        public static final int IgnorePrivateImports = 1;
        public static final int IgnoreErrors = 2;
        public static final int IgnoreAmbiguous = 4;
        public static final int SearchLocalsOnly = 8;
        public static final int SearchImportsOnly = 16;
        public static final int SearchUnqualifiedModule = 32;
        public static final int IgnoreSymbolVisibility = 128;
    }

    public static class Dsymbol extends ASTNode
    {
        public Identifier ident;
        public Dsymbol parent;
        public CPPNamespaceDeclaration namespace;
        public Symbol csym;
        public Symbol isym;
        public BytePtr comment;
        public Loc loc = new Loc();
        public Scope _scope;
        public BytePtr prettystring;
        public boolean errors;
        public int semanticRun = PASS.init;
        public DeprecatedDeclaration depdecl;
        public UserAttributeDeclaration userAttribDecl;
        public UnitTestDeclaration ddocUnittest;
        public  Dsymbol() {
            super();
            this.loc = new Loc(null, 0, 0);
        }

        public  Dsymbol(Identifier ident) {
            super();
            this.loc = new Loc(null, 0, 0);
            this.ident = ident;
        }

        public  Dsymbol(Loc loc, Identifier ident) {
            super();
            this.loc = loc.copy();
            this.ident = ident;
        }

        public static Dsymbol create(Identifier ident) {
            return new Dsymbol(ident);
        }

        public  BytePtr toChars() {
            return this.ident != null ? this.ident.toChars() : new BytePtr("__anonymous");
        }

        public  BytePtr toPrettyCharsHelper() {
            return this.toChars();
        }

        public  Loc getLoc() {
            if (!this.loc.isValid())
                {
                    dmodule.Module m = this.getModule();
                    if ((m) != null)
                        return new Loc(m.srcfile.toChars(), 0, 0);
                }
            return this.loc;
        }

        public  BytePtr locToChars() {
            return this.getLoc().toChars(global.params.showColumns);
        }

        public  boolean equals(RootObject o) {
            if ((pequals(this, o)))
                return true;
            if ((o.dyncast() != DYNCAST.dsymbol))
                return false;
            Dsymbol s = (Dsymbol)o;
            if ((s != null) && (this.ident != null) && (s.ident != null) && this.ident.equals(s.ident))
                return true;
            return false;
        }

        public  boolean isAnonymous() {
            return this.ident == null;
        }

        public  void error(Loc loc, BytePtr format, Object... ap) {
            BytePtr cstr = pcopy(this.toPrettyChars(false));
            ByteSlice pretty = (byte)96.concat(cstr.slice(0,strlen(cstr))).concat(new ByteSlice("`\u0000")).copy();
            verror(loc, format, new Slice<>(ap), this.kind(), toBytePtr(pretty), new BytePtr("Error: "));
        }

        public  void error(BytePtr format, Object... ap) {
            BytePtr cstr = pcopy(this.toPrettyChars(false));
            ByteSlice pretty = (byte)96.concat(cstr.slice(0,strlen(cstr))).concat(new ByteSlice("`\u0000")).copy();
            Loc loc = this.getLoc().copy();
            verror(loc, format, new Slice<>(ap), this.kind(), toBytePtr(pretty), new BytePtr("Error: "));
        }

        public  void deprecation(Loc loc, BytePtr format, Object... ap) {
            BytePtr cstr = pcopy(this.toPrettyChars(false));
            ByteSlice pretty = (byte)96.concat(cstr.slice(0,strlen(cstr))).concat(new ByteSlice("`\u0000")).copy();
            vdeprecation(loc, format, new Slice<>(ap), this.kind(), toBytePtr(pretty));
        }

        public  void deprecation(BytePtr format, Object... ap) {
            BytePtr cstr = pcopy(this.toPrettyChars(false));
            ByteSlice pretty = (byte)96.concat(cstr.slice(0,strlen(cstr))).concat(new ByteSlice("`\u0000")).copy();
            Loc loc = this.getLoc().copy();
            vdeprecation(loc, format, new Slice<>(ap), this.kind(), toBytePtr(pretty));
        }

        public  boolean checkDeprecated(Loc loc, Scope sc) {
            if (((global.params.useDeprecated & 0xFF) != 2) && this.isDeprecated())
            {
                if ((sc).isDeprecated())
                    return false;
                BytePtr message = null;
                {
                    Dsymbol p = this;
                    for (; p != null;p = p.parent){
                        message = pcopy((p.depdecl != null ? getMessage(p.depdecl) : null));
                        if (message != null)
                            break;
                    }
                }
                if (message != null)
                    this.deprecation(loc, new BytePtr("is deprecated - %s"), message);
                else
                    this.deprecation(loc, new BytePtr("is deprecated"));
                return true;
            }
            return false;
        }

        public  dmodule.Module getModule() {
            {
                TemplateInstance ti = this.isInstantiated();
                if ((ti) != null)
                    return ti.tempdecl.getModule();
            }
            Dsymbol s = this;
            for (; s != null;){
                dmodule.Module m = s.isModule();
                if (m != null)
                    return m;
                s = s.parent;
            }
            return null;
        }

        public  dmodule.Module getAccessModule() {
            {
                TemplateInstance ti = this.isInstantiated();
                if ((ti) != null)
                    return ti.tempdecl.getAccessModule();
            }
            Dsymbol s = this;
            for (; s != null;){
                dmodule.Module m = s.isModule();
                if (m != null)
                    return m;
                TemplateInstance ti = s.isTemplateInstance();
                if ((ti != null) && (ti.enclosing != null))
                {
                    s = ti.tempdecl;
                }
                else
                    s = s.parent;
            }
            return null;
        }

        public  Dsymbol pastMixin() {
            if ((this.isTemplateMixin() == null) && (this.isForwardingAttribDeclaration() == null))
                return this;
            if (this.parent == null)
                return null;
            return this.parent.pastMixin();
        }

        public  Dsymbol toParent() {
            return this.parent != null ? this.parent.pastMixin() : null;
        }

        public  Dsymbol toParent2() {
            if ((this.parent == null) || (this.parent.isTemplateInstance() == null) && (this.parent.isForwardingAttribDeclaration() == null))
                return this.parent;
            return this.parent.toParent2();
        }

        public  Dsymbol toParentDecl() {
            return this.toParentDeclImpl(false);
        }

        public  Dsymbol toParentLocal() {
            return this.toParentDeclImpl(true);
        }

        public  Dsymbol toParentDeclImpl(boolean localOnly) {
            Dsymbol p = this.toParent();
            if ((p == null) || (p.isTemplateInstance() == null))
                return p;
            TemplateInstance ti = p.isTemplateInstance();
            if ((ti.tempdecl != null) && !localOnly || !((TemplateDeclaration)ti.tempdecl).isstatic)
                return ti.tempdecl.toParentDeclImpl(localOnly);
            return this.parent.toParentDeclImpl(localOnly);
        }

        public  TemplateInstance isInstantiated() {
            if (this.parent == null)
                return null;
            TemplateInstance ti = this.parent.isTemplateInstance();
            if ((ti != null) && (ti.isTemplateMixin() == null))
                return ti;
            return this.parent.isInstantiated();
        }

        public  TemplateInstance isSpeculative() {
            if (this.parent == null)
                return null;
            TemplateInstance ti = this.parent.isTemplateInstance();
            if ((ti != null) && ti.gagged)
                return ti;
            if (this.parent.toParent() == null)
                return null;
            return this.parent.isSpeculative();
        }

        public  Ungag ungagSpeculative() {
            int oldgag = global.gag;
            if ((global.gag != 0) && (this.isSpeculative() == null) && (this.toParent2().isFuncDeclaration() == null))
                global.gag = 0;
            return new Ungag(oldgag);
        }

        public  int dyncast() {
            return DYNCAST.dsymbol;
        }

        public static DArray<Dsymbol> arraySyntaxCopy(DArray<Dsymbol> a) {
            DArray<Dsymbol> b = null;
            if (a != null)
            {
                b = (a).copy();
                {
                    int i = 0;
                    for (; (i < (b).length);i++){
                        b.set(i, (b).get(i).syntaxCopy(null));
                    }
                }
            }
            return b;
        }

        public  Identifier getIdent() {
            return this.ident;
        }

        public  BytePtr toPrettyChars(boolean QualifyTypes) {
            if ((this.prettystring != null) && !QualifyTypes)
                return this.prettystring;
            if (this.parent == null)
            {
                BytePtr s = pcopy(this.toChars());
                if (!QualifyTypes)
                    this.prettystring = pcopy(s);
                return s;
            }
            int complength = 0;
            {
                Dsymbol p = this;
                for (; p != null;p = p.parent) {
                    complength += 1;
                }
            }
            Ptr<ByteSlice> compptr = pcopy(((Ptr<ByteSlice>)malloc(complength * 8)));
            if (compptr == null)
                Mem.error();
            Slice<ByteSlice> comp = compptr.slice(0,complength).copy();
            int length = 0;
            int i = 0;
            {
                Dsymbol p = this;
                for (; p != null;p = p.parent){
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
                int __key1124 = 0;
                int __limit1125 = complength;
                for (; (__key1124 < __limit1125);__key1124 += 1) {
                    int j = __key1124;
                    BytePtr t = pcopy(toBytePtr(comp.get(j)));
                    int len = comp.get(j).getLength();
                    q.minusAssign(len);
                    memcpy((BytePtr)(q), (t), len);
                    if ((q == s))
                        break;
                    (q.minusAssign(1)).set(0, (byte)46);
                }
            }
            free(toPtr<ByteSlice>(comp));
            if (!QualifyTypes)
                this.prettystring = pcopy(s);
            return s;
        }

        public  BytePtr kind() {
            return new BytePtr("symbol");
        }

        public  Dsymbol toAlias() {
            return this;
        }

        public  Dsymbol toAlias2() {
            return this.toAlias();
        }

        public  int apply(Function2<Dsymbol,Object,Integer> fp, Object param) {
            return (fp).invoke(this, param);
        }

        public  void addMember(Scope sc, ScopeDsymbol sds) {
            this.parent = sds;
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

        public  void setScope(Scope sc) {
            if (!(sc).nofree)
                (sc).setNoFree();
            this._scope = sc;
            if ((sc).depdecl != null)
                this.depdecl = (sc).depdecl;
            if (this.userAttribDecl == null)
                this.userAttribDecl = (sc).userAttribDecl;
        }

        public  void importAll(Scope sc) {
        }

        public  Dsymbol search(Loc loc, Identifier ident, int flags) {
            return null;
        }

        public  Dsymbol search_correct(Identifier ident) {
            Function2<ByteSlice,Integer,Dsymbol> symbol_search_fp = new Function2<ByteSlice,Integer,Dsymbol>(){
                public Dsymbol invoke(ByteSlice seed, IntRef cost){
                    if (seed.getLength() == 0)
                        return null;
                    Identifier id = Identifier.lookup(seed);
                    if (id == null)
                        return null;
                    cost.value = 0;
                    Dsymbol s = this;
                    dmodule.Module.clearCache();
                    return s.search(Loc.initial, id, 2);
                }
            };
            if (global.gag != 0)
                return null;
            {
                Dsymbol s = this.search(Loc.initial, ident, 2);
                if ((s) != null)
                    return s;
            }
            return speller.invoke(ident.asString());
        }

        public  Dsymbol searchX(Loc loc, Scope sc, RootObject id, int flags) {
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
                            error(loc, new BytePtr("template identifier `%s` is not a member of %s `%s`, did you mean %s `%s`?"), ti.name.toChars(), s.kind(), s.toPrettyChars(false), sm.kind(), sm.toChars());
                        else
                            error(loc, new BytePtr("template identifier `%s` is not a member of %s `%s`"), ti.name.toChars(), s.kind(), s.toPrettyChars(false));
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
                        dsymbolSemantic(ti, sc);
                    sm = ti.toAlias();
                    break;
                case DYNCAST.type:
                case DYNCAST.expression:
                default:
                throw new AssertionError("Unreachable code!");
            }
            return sm;
        }

        public  boolean overloadInsert(Dsymbol s) {
            return false;
        }

        public  long size(Loc loc) {
            this.error(new BytePtr("Dsymbol `%s` has no size"), this.toChars());
            return -1L;
        }

        public  boolean isforwardRef() {
            return false;
        }

        public  AggregateDeclaration isThis() {
            return null;
        }

        public  boolean isExport() {
            return false;
        }

        public  boolean isImportedSymbol() {
            return false;
        }

        public  boolean isDeprecated() {
            return false;
        }

        public  boolean isOverloadable() {
            return false;
        }

        public  LabelDsymbol isLabel() {
            return null;
        }

        public  AggregateDeclaration isMember() {
            Dsymbol p = this.toParent();
            return p != null ? p.isAggregateDeclaration() : null;
        }

        public  AggregateDeclaration isMember2() {
            Dsymbol p = this.toParent2();
            return p != null ? p.isAggregateDeclaration() : null;
        }

        public  AggregateDeclaration isMemberDecl() {
            Dsymbol p = this.toParentDecl();
            return p != null ? p.isAggregateDeclaration() : null;
        }

        public  AggregateDeclaration isMemberLocal() {
            Dsymbol p = this.toParentLocal();
            return p != null ? p.isAggregateDeclaration() : null;
        }

        public  ClassDeclaration isClassMember() {
            AggregateDeclaration ad = this.isMember();
            return ad != null ? ad.isClassDeclaration() : null;
        }

        public  Type getType() {
            return null;
        }

        public  boolean needThis() {
            return false;
        }

        public  Prot prot() {
            return new Prot(Prot.Kind.public_);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            printf(new BytePtr("%s %s\n"), this.kind(), this.toChars());
            throw new AssertionError("Unreachable code!");
        }

        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            ps.set(0, this);
            return true;
        }

        public static boolean oneMembers(DArray<Dsymbol> members, Ptr<Dsymbol> ps, Identifier ident) {
            Dsymbol s = null;
            if (members != null)
            {
                {
                    int i = 0;
                    for (; (i < (members).length);i++){
                        Dsymbol sx = (members).get(i);
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
                                continue;
                            if (s == null)
                                s = ps.get();
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

        public  void setFieldOffset(AggregateDeclaration ad, IntPtr poffset, boolean isunion) {
        }

        public  boolean hasPointers() {
            return false;
        }

        public  boolean hasStaticCtorOrDtor() {
            return false;
        }

        public  void addLocalClass(DArray<ClassDeclaration> _param_0) {
        }

        public  void addObjcSymbols(DArray<ClassDeclaration> classes, DArray<ClassDeclaration> categories) {
        }

        public  void checkCtorConstInit() {
        }

        public  void addComment(BytePtr comment) {
            if (this.comment == null)
                this.comment = pcopy(comment);
            else if ((comment != null) && (strcmp(comment, this.comment) != 0))
            {
                this.comment = pcopy(Lexer.combineComments(this.comment, comment, true));
            }
        }

        public  boolean inNonRoot() {
            Dsymbol s = this.parent;
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
                            return true;
                        break;
                    }
                }
            }
            return false;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }

        public  dmodule.Package isPackage() {
            return null;
        }

        public  dmodule.Module isModule() {
            return null;
        }

        public  EnumMember isEnumMember() {
            return null;
        }

        public  TemplateDeclaration isTemplateDeclaration() {
            return null;
        }

        public  TemplateInstance isTemplateInstance() {
            return null;
        }

        public  TemplateMixin isTemplateMixin() {
            return null;
        }

        public  ForwardingAttribDeclaration isForwardingAttribDeclaration() {
            return null;
        }

        public  Nspace isNspace() {
            return null;
        }

        public  Declaration isDeclaration() {
            return null;
        }

        public  StorageClassDeclaration isStorageClassDeclaration() {
            return null;
        }

        public  ExpressionDsymbol isExpressionDsymbol() {
            return null;
        }

        public  ThisDeclaration isThisDeclaration() {
            return null;
        }

        public  TypeInfoDeclaration isTypeInfoDeclaration() {
            return null;
        }

        public  TupleDeclaration isTupleDeclaration() {
            return null;
        }

        public  AliasDeclaration isAliasDeclaration() {
            return null;
        }

        public  AggregateDeclaration isAggregateDeclaration() {
            return null;
        }

        public  FuncDeclaration isFuncDeclaration() {
            return null;
        }

        public  FuncAliasDeclaration isFuncAliasDeclaration() {
            return null;
        }

        public  OverDeclaration isOverDeclaration() {
            return null;
        }

        public  FuncLiteralDeclaration isFuncLiteralDeclaration() {
            return null;
        }

        public  CtorDeclaration isCtorDeclaration() {
            return null;
        }

        public  PostBlitDeclaration isPostBlitDeclaration() {
            return null;
        }

        public  DtorDeclaration isDtorDeclaration() {
            return null;
        }

        public  StaticCtorDeclaration isStaticCtorDeclaration() {
            return null;
        }

        public  StaticDtorDeclaration isStaticDtorDeclaration() {
            return null;
        }

        public  SharedStaticCtorDeclaration isSharedStaticCtorDeclaration() {
            return null;
        }

        public  SharedStaticDtorDeclaration isSharedStaticDtorDeclaration() {
            return null;
        }

        public  InvariantDeclaration isInvariantDeclaration() {
            return null;
        }

        public  UnitTestDeclaration isUnitTestDeclaration() {
            return null;
        }

        public  NewDeclaration isNewDeclaration() {
            return null;
        }

        public  VarDeclaration isVarDeclaration() {
            return null;
        }

        public  ClassDeclaration isClassDeclaration() {
            return null;
        }

        public  StructDeclaration isStructDeclaration() {
            return null;
        }

        public  UnionDeclaration isUnionDeclaration() {
            return null;
        }

        public  InterfaceDeclaration isInterfaceDeclaration() {
            return null;
        }

        public  ScopeDsymbol isScopeDsymbol() {
            return null;
        }

        public  ForwardingScopeDsymbol isForwardingScopeDsymbol() {
            return null;
        }

        public  WithScopeSymbol isWithScopeSymbol() {
            return null;
        }

        public  ArrayScopeSymbol isArrayScopeSymbol() {
            return null;
        }

        public  Import isImport() {
            return null;
        }

        public  EnumDeclaration isEnumDeclaration() {
            return null;
        }

        public  DeleteDeclaration isDeleteDeclaration() {
            return null;
        }

        public  SymbolDeclaration isSymbolDeclaration() {
            return null;
        }

        public  AttribDeclaration isAttribDeclaration() {
            return null;
        }

        public  AnonDeclaration isAnonDeclaration() {
            return null;
        }

        public  CPPNamespaceDeclaration isCPPNamespaceDeclaration() {
            return null;
        }

        public  ProtDeclaration isProtDeclaration() {
            return null;
        }

        public  OverloadSet isOverloadSet() {
            return null;
        }

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
        public DArray<Dsymbol> members;
        public DsymbolTable symtab;
        public int endlinnum;
        public DArray<Dsymbol> importedScopes;
        public IntPtr prots;
        public BitArray accessiblePackages = new BitArray();
        public BitArray privateAccessiblePackages = new BitArray();
        public  ScopeDsymbol() {
            super();
        }

        public  ScopeDsymbol(Identifier ident) {
            super(ident);
        }

        public  ScopeDsymbol(Loc loc, Identifier ident) {
            super(loc, ident);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            ScopeDsymbol sds = s != null ? (ScopeDsymbol)s : new ScopeDsymbol(this.ident);
            sds.members = Dsymbol.arraySyntaxCopy(this.members);
            sds.endlinnum = this.endlinnum;
            return sds;
        }

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
                return null;
            Dsymbol s = null;
            OverloadSet a = null;
            {
                int i = 0;
                for (; (i < (this.importedScopes).length);i++){
                    if (((flags & 1) != 0) && (this.prots.get(i) == Prot.Kind.private_))
                        continue;
                    int sflags = flags & 6;
                    Dsymbol ss = (this.importedScopes).get(i);
                    if (ss.isModule() != null)
                    {
                        if ((flags & 8) != 0)
                            continue;
                    }
                    else
                    {
                        if ((flags & 16) != 0)
                            continue;
                        sflags |= 8;
                    }
                    Dsymbol s2 = ss.search(loc, ident, sflags | (ss.isModule() != null ? 1 : 0));
                    if ((s2 == null) || ((flags & 128) == 0) && !symbolIsVisible(this, s2))
                        continue;
                    if (s == null)
                    {
                        s = s2;
                        if ((s != null) && (s.isOverloadSet() != null))
                            a = this.mergeOverloadSet(ident, a, s);
                    }
                    else if ((s2 != null) && (!pequals(s, s2)))
                    {
                        if ((pequals(s.toAlias(), s2.toAlias())) || (pequals(s.getType(), s2.getType())) && (s.getType() != null))
                        {
                            if (s.isDeprecated() || s.prot().isMoreRestrictiveThan(s2.prot()) && (s2.prot().kind != Prot.Kind.none))
                                s = s2;
                        }
                        else
                        {
                            Import i1 = s.isImport();
                            Import i2 = s2.isImport();
                            if (!((i1 != null) && (i2 != null) && (pequals(i1.mod, i2.mod)) || (i1.parent.isImport() == null) && (i2.parent.isImport() == null) && i1.ident.equals(i2.ident)))
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
                                        s = s2;
                                    continue;
                                }
                                if ((flags & 4) != 0)
                                    return null;
                                if ((flags & 2) == 0)
                                    multiplyDefined(loc, s, s2);
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
                        a = this.mergeOverloadSet(ident, a, s);
                    s = a;
                }
                return s;
            }
            return null;
        }

        public  OverloadSet mergeOverloadSet(Identifier ident, OverloadSet os, Dsymbol s) {
            if (os == null)
            {
                os = new OverloadSet(ident, null);
                os.parent = this;
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

        public  void importScope(Dsymbol s, Prot protection) {
            if ((!pequals(s, this)))
            {
                if (this.importedScopes == null)
                    this.importedScopes = new DArray<Dsymbol>();
                else
                {
                    {
                        int i = 0;
                        for (; (i < (this.importedScopes).length);i++){
                            Dsymbol ss = (this.importedScopes).get(i);
                            if ((pequals(ss, s)))
                            {
                                if ((protection.kind > this.prots.get(i)))
                                    this.prots.set(i, protection.kind);
                                return ;
                            }
                        }
                    }
                }
                (this.importedScopes).push(s);
                this.prots = pcopy((((IntPtr)Mem.xrealloc(this.prots, (this.importedScopes).length * 4))));
                this.prots.set(((this.importedScopes).length - 1), protection.kind);
            }
        }

        public  void addAccessiblePackage(dmodule.Package p, Prot protection) {
            if ((p == null))
                return ;
            BitArray pary = (protection.kind == Prot.Kind.private_) ? this.privateAccessiblePackages : this.accessiblePackages;
            if (((pary).length() <= p.tag))
                (pary).length(p.tag + 1);
            (pary).opIndexAssign(true, p.tag);
        }

        public  boolean isPackageAccessible(dmodule.Package p, Prot protection, int flags) {
            if ((p.tag < this.accessiblePackages.length()) && this.accessiblePackages.get(p.tag) || (protection.kind == Prot.Kind.private_) && (p.tag < this.privateAccessiblePackages.length()) && this.privateAccessiblePackages.get(p.tag))
                return true;
            {
                Slice<Dsymbol> __r1135 = (this.importedScopes != null ? (this.importedScopes).opSlice() : new Slice<Dsymbol>()).copy();
                int __key1134 = 0;
                for (; (__key1134 < __r1135.getLength());__key1134 += 1) {
                    Dsymbol ss = __r1135.get(__key1134);
                    int i = __key1134;
                    if ((protection.kind <= this.prots.get(i)) && ss.isScopeDsymbol().isPackageAccessible(p, protection, 1))
                        return true;
                }
            }
            return false;
        }

        public  boolean isforwardRef() {
            return this.members == null;
        }

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

        public  BytePtr kind() {
            return new BytePtr("ScopeDsymbol");
        }

        public  FuncDeclaration findGetMembers() {
            Dsymbol s = search_function(this, Id.getmembers);
            FuncDeclaration fdx = s != null ? s.isFuncDeclaration() : null;
            if ((fdx != null) && fdx.isVirtual())
                fdx = null;
            return fdx;
        }

        public  Dsymbol symtabInsert(Dsymbol s) {
            return this.symtab.insert(s);
        }

        public  Dsymbol symtabLookup(Dsymbol s, Identifier id) {
            return this.symtab.lookup(id);
        }

        public  boolean hasStaticCtorOrDtor() {
            if (this.members != null)
            {
                {
                    int i = 0;
                    for (; (i < (this.members).length);i++){
                        Dsymbol member = (this.members).get(i);
                        if (member.hasStaticCtorOrDtor())
                            return true;
                    }
                }
            }
            return false;
        }

        public static int _foreach(Scope sc, DArray<Dsymbol> members, Function2<Integer,Dsymbol,Integer> dg, IntPtr pn) {
            assert(dg != null);
            if (members == null)
                return 0;
            IntRef n = ref(pn != null ? pn.get() : 0);
            int result = 0;
            {
                int __key1136 = 0;
                int __limit1137 = (members).length;
                for (; (__key1136 < __limit1137);__key1136 += 1) {
                    int i = __key1136;
                    Dsymbol s = (members).get(i);
                    {
                        AttribDeclaration a = s.isAttribDeclaration();
                        if ((a) != null)
                            result = _foreach(sc, a.include(sc), dg, ptr(n));
                        else {
                            TemplateMixin tm = s.isTemplateMixin();
                            if ((tm) != null)
                                result = _foreach(sc, tm.members, dg, ptr(n));
                            else if (s.isTemplateInstance() != null)
                            {
                            }
                            else if (s.isUnitTestDeclaration() != null)
                            {
                            }
                            else
                                result = dg.invoke(n.value++, s);
                        }
                    }
                    if (result != 0)
                        break;
                }
            }
            if (pn != null)
                pn.set(0, n.value);
            return result;
        }

        public  ScopeDsymbol isScopeDsymbol() {
            return this;
        }

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
        public WithStatement withstate;
        public  WithScopeSymbol(WithStatement withstate) {
            super();
            this.withstate = withstate;
        }

        public  Dsymbol search(Loc loc, Identifier ident, int flags) {
            if ((flags & 16) != 0)
                return null;
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
                        s = e.type.toDsymbol(null);
                    }
                    else
                    {
                        Type t = e.type.toBasetype();
                        s = t.toDsymbol(null);
                    }
                    if (s != null)
                    {
                        s = s.search(loc, ident, flags);
                        if (s != null)
                            return s;
                    }
                    eold = e;
                }
            }
            return null;
        }

        public  WithScopeSymbol isWithScopeSymbol() {
            return this;
        }

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
        public Expression exp;
        public TypeTuple type;
        public TupleDeclaration td;
        public Scope sc;
        public  ArrayScopeSymbol(Scope sc, Expression exp) {
            super(exp.loc, null);
            assert(((exp.op & 0xFF) == 62) || ((exp.op & 0xFF) == 31) || ((exp.op & 0xFF) == 17));
            this.exp = exp;
            this.sc = sc;
        }

        public  ArrayScopeSymbol(Scope sc, TypeTuple type) {
            super();
            this.type = type;
            this.sc = sc;
        }

        public  ArrayScopeSymbol(Scope sc, TupleDeclaration td) {
            super();
            this.td = td;
            this.sc = sc;
        }

        public  Dsymbol search(Loc loc, Identifier ident, int flags) {
            if ((!pequals(ident, Id.dollar)))
                return null;
            Ptr<VarDeclaration> pvar = null;
            Expression ce = null;
            while(true) try {
            /*L1:*/
                if (this.td != null)
                {
                    VarDeclaration v = new VarDeclaration(loc, Type.tsize_t, Id.dollar, null, 0L);
                    Expression e = new IntegerExp(Loc.initial, (long)(this.td.objects).length, Type.tsize_t);
                    v._init = new ExpInitializer(Loc.initial, e);
                    v.storage_class |= 1099511627781L;
                    dsymbolSemantic(v, this.sc);
                    return v;
                }
                if (this.type != null)
                {
                    VarDeclaration v = new VarDeclaration(loc, Type.tsize_t, Id.dollar, null, 0L);
                    Expression e = new IntegerExp(Loc.initial, (long)(this.type.arguments).length, Type.tsize_t);
                    v._init = new ExpInitializer(Loc.initial, e);
                    v.storage_class |= 1099511627781L;
                    dsymbolSemantic(v, this.sc);
                    return v;
                }
                if (((this.exp.op & 0xFF) == 62))
                {
                    IndexExp ie = (IndexExp)this.exp;
                    pvar = pcopy((ie.lengthVar));
                    ce = ie.e1;
                }
                else if (((this.exp.op & 0xFF) == 31))
                {
                    SliceExp se = (SliceExp)this.exp;
                    pvar = pcopy((se.lengthVar));
                    ce = se.e1;
                }
                else if (((this.exp.op & 0xFF) == 17))
                {
                    ArrayExp ae = (ArrayExp)this.exp;
                    pvar = pcopy((ae.lengthVar));
                    ce = ae.e1;
                }
                else
                {
                    return null;
                }
                for (; ((ce.op & 0xFF) == 99);) {
                    ce = ((CommaExp)ce).e2;
                }
                if (((ce.op & 0xFF) == 20))
                {
                    Type t = ((TypeExp)ce).type;
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
                        Expression e = new IntegerExp(Loc.initial, (long)(((TupleExp)ce).exps).length, Type.tsize_t);
                        v = new VarDeclaration(loc, Type.tsize_t, Id.dollar, new ExpInitializer(Loc.initial, e), 0L);
                        v.storage_class |= 1099511627781L;
                    }
                    else if ((ce.type != null) && ((t = ce.type.toBasetype()) != null) && ((t.ty & 0xFF) == ENUMTY.Tstruct) || ((t.ty & 0xFF) == ENUMTY.Tclass))
                    {
                        assert(((this.exp.op & 0xFF) == 17) || ((this.exp.op & 0xFF) == 31));
                        AggregateDeclaration ad = isAggregate(t);
                        assert(ad != null);
                        Dsymbol s = ad.search(loc, Id.opDollar, 8);
                        if (s == null)
                            return null;
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
                                DArray<RootObject> tiargs = new DArray<RootObject>();
                                Expression edim = new IntegerExp(Loc.initial, dim, Type.tsize_t);
                                edim = expressionSemantic(edim, this.sc);
                                (tiargs).push(edim);
                                e = new DotTemplateInstanceExp(loc, ce, td.ident, tiargs);
                            }
                            else
                            {
                                if (((this.exp.op & 0xFF) == 17) && ((((ArrayExp)this.exp).arguments).length != 1))
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
                        if (e.type == null)
                            this.exp.error(new BytePtr("`%s` has no value"), e.toChars());
                        t = e.type.toBasetype();
                        if ((t != null) && ((t.ty & 0xFF) == ENUMTY.Tfunction))
                            e = new CallExp(e.loc, e);
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

        public  ArrayScopeSymbol isArrayScopeSymbol() {
            return this;
        }

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
        public  OverloadSet(Identifier ident, OverloadSet os) {
            super(ident);
            if (os != null)
            {
                this.a.pushSlice(os.a.opSlice());
            }
        }

        public  void push(Dsymbol s) {
            this.a.push(s);
        }

        public  OverloadSet isOverloadSet() {
            return this;
        }

        public  BytePtr kind() {
            return new BytePtr("overloadset");
        }

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
        public ScopeDsymbol forward;
        public  ForwardingScopeDsymbol(ScopeDsymbol forward) {
            super(null);
            this.forward = forward;
        }

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

        public  void importScope(Dsymbol s, Prot protection) {
            this.forward.importScope(s, protection);
        }

        public  BytePtr kind() {
            return new BytePtr("local scope");
        }

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
        public Expression exp;
        public  ExpressionDsymbol(Expression exp) {
            super();
            this.exp = exp;
        }

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
        public AssocArrayIdentifierDsymbol tab = new AssocArrayIdentifierDsymbol();
        public  Dsymbol lookup(Identifier ident) {
            return this.tab.get(ident);
        }

        public  Dsymbol insert(Dsymbol s) {
            return this.insert(s.ident, s);
        }

        public  Dsymbol update(Dsymbol s) {
            Identifier ident = s.ident;
            Ptr<Dsymbol> ps = pcopy(this.tab.getLvalue(ident));
            ps.set(0, s);
            return s;
        }

        public  Dsymbol insert(Identifier ident, Dsymbol s) {
            Ptr<Dsymbol> ps = pcopy(this.tab.getLvalue(ident));
            if (ps.get() != null)
                return null;
            ps.set(0, s);
            return s;
        }

        public  int len() {
            return this.tab.length();
        }

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
