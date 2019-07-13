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
import static org.dlang.dmd.ctorflow.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.delegatize.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.initsem.*;
import static org.dlang.dmd.intrange.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.visitor.*;

public class declaration {

    public static boolean checkFrameAccess(Loc loc, Ptr<Scope> sc, AggregateDeclaration ad, int iStart) {
        Dsymbol sparent = ad.toParentLocal();
        Dsymbol sparent2 = ad.toParent2();
        Dsymbol s = (sc.get()).func.value;
        if (ad.isNested() && (s != null))
        {
            if (!ensureStaticLinkTo(s, sparent) || (!pequals(sparent, sparent2)) && !ensureStaticLinkTo(s, sparent2))
            {
                error(loc, new BytePtr("cannot access frame pointer of `%s`"), ad.toPrettyChars(false));
                return true;
            }
        }
        boolean result = false;
        {
            int i = iStart;
            for (; (i < ad.fields.length.value);i++){
                VarDeclaration vd = ad.fields.get(i);
                Type tb = vd.type.value.baseElemOf();
                if (((tb.ty.value & 0xFF) == ENUMTY.Tstruct))
                {
                    (result ? 1 : 0) |= (checkFrameAccess(loc, sc, ((TypeStruct)tb).sym.value, 0) ? 1 : 0);
                }
            }
        }
        return result;
    }

    // defaulted all parameters starting with #4
    public static boolean checkFrameAccess(Loc loc, Ptr<Scope> sc, AggregateDeclaration ad) {
        return checkFrameAccess(loc, sc, ad, 0);
    }

    public static boolean modifyFieldVar(Loc loc, Ptr<Scope> sc, VarDeclaration var, Expression e1) {
        Dsymbol s = (sc.get()).func.value;
        for (; 1 != 0;){
            FuncDeclaration fd = null;
            if (s != null)
            {
                fd = s.isFuncDeclaration();
            }
            if ((fd != null) && (fd.isCtorDeclaration() != null) && var.isField() || (fd.isStaticCtorDeclaration() != null) && !var.isField() && (pequals(fd.toParentDecl(), var.toParent2())) && (e1 == null) || ((e1.op.value & 0xFF) == 123))
            {
                boolean result = true;
                var.ctorinit = true;
                if (var.isField() && ((sc.get()).ctorflow.fieldinit.getLength() != 0) && ((sc.get()).intypeof.value == 0))
                {
                    assert(e1 != null);
                    boolean mustInit = ((var.storage_class.value & 549755813888L) != 0L) || var.type.value.needsNested();
                    int dim = (sc.get()).ctorflow.fieldinit.getLength();
                    AggregateDeclaration ad = fd.isMemberDecl();
                    assert(ad != null);
                    int i = 0;
                    {
                        i = 0;
                        for (; (i < dim);i++){
                            if ((pequals(ad.fields.get(i), var)))
                            {
                                break;
                            }
                        }
                    }
                    assert((i < dim));
                    Ptr<FieldInit> fieldInit = ptr((sc.get()).ctorflow.fieldinit.get(i));
                    int fi = (fieldInit.get()).csx.value;
                    if ((fi & 1) != 0)
                    {
                        if (var.type.value.isMutable() && e1.type.value.isMutable())
                        {
                            result = false;
                        }
                        else
                        {
                            BytePtr modStr = pcopy(!var.type.value.isMutable() ? MODtoChars(var.type.value.mod.value) : MODtoChars(e1.type.value.mod.value));
                            if ((fi & 64) != 0)
                            {
                                deprecation(loc, new BytePtr("%s field `%s` was initialized in a previous constructor call"), modStr, var.toChars());
                            }
                            else
                            {
                                error(loc, new BytePtr("%s field `%s` initialized multiple times"), modStr, var.toChars());
                                errorSupplemental((fieldInit.get()).loc, new BytePtr("Previous initialization is here."));
                            }
                        }
                    }
                    else if ((sc.get()).inLoop || ((fi & 4) != 0))
                    {
                        if (!mustInit && var.type.value.isMutable() && e1.type.value.isMutable())
                        {
                            result = false;
                        }
                        else
                        {
                            BytePtr modStr = pcopy(!var.type.value.isMutable() ? MODtoChars(var.type.value.mod.value) : MODtoChars(e1.type.value.mod.value));
                            error(loc, new BytePtr("%s field `%s` initialization is not allowed in loops or after labels"), modStr, var.toChars());
                        }
                    }
                    (fieldInit.get()).csx.value |= 1;
                    (fieldInit.get()).loc = e1.loc.value.copy();
                    if (var.overlapped.value)
                    {
                        {
                            Slice<VarDeclaration> __r920 = ad.fields.opSlice().copy();
                            int __key919 = 0;
                            for (; (__key919 < __r920.getLength());__key919 += 1) {
                                VarDeclaration v = __r920.get(__key919);
                                int j = __key919;
                                if ((v == var) || !var.isOverlappedWith(v))
                                {
                                    continue;
                                }
                                v.ctorinit = true;
                                (sc.get()).ctorflow.fieldinit.get(j).csx.value = CSX.this_ctor;
                            }
                        }
                    }
                }
                else if ((!pequals(fd, (sc.get()).func.value)))
                {
                    if (var.type.value.isMutable())
                    {
                        result = false;
                    }
                    else if ((sc.get()).func.value.fes.value != null)
                    {
                        BytePtr p = pcopy(var.isField() ? new BytePtr("field") : var.kind());
                        error(loc, new BytePtr("%s %s `%s` initialization is not allowed in foreach loop"), MODtoChars(var.type.value.mod.value), p, var.toChars());
                    }
                    else
                    {
                        BytePtr p = pcopy(var.isField() ? new BytePtr("field") : var.kind());
                        error(loc, new BytePtr("%s %s `%s` initialization is not allowed in nested function `%s`"), MODtoChars(var.type.value.mod.value), p, var.toChars(), (sc.get()).func.value.toChars());
                    }
                }
                return result;
            }
            else
            {
                if (s != null)
                {
                    s = toParentPDsymbol(s, var.toParent2());
                    continue;
                }
            }
            break;
        }
        return false;
    }

    public static void ObjectNotFound(Identifier id) {
        error(Loc.initial.value, new BytePtr("`%s` not found. object.d may be incorrectly installed or corrupt."), id.toChars());
        fatal();
    }


    public static class STC 
    {
        public static final long undefined_ = 0L;
        public static final long static_ = 1L;
        public static final long extern_ = 2L;
        public static final long const_ = 4L;
        public static final long final_ = 8L;
        public static final long abstract_ = 16L;
        public static final long parameter = 32L;
        public static final long field = 64L;
        public static final long override_ = 128L;
        public static final long auto_ = 256L;
        public static final long synchronized_ = 512L;
        public static final long deprecated_ = 1024L;
        public static final long in_ = 2048L;
        public static final long out_ = 4096L;
        public static final long lazy_ = 8192L;
        public static final long foreach_ = 16384L;
        public static final long variadic = 65536L;
        public static final long ctorinit = 131072L;
        public static final long templateparameter = 262144L;
        public static final long scope_ = 524288L;
        public static final long immutable_ = 1048576L;
        public static final long ref_ = 2097152L;
        public static final long init = 4194304L;
        public static final long manifest = 8388608L;
        public static final long nodtor = 16777216L;
        public static final long nothrow_ = 33554432L;
        public static final long pure_ = 67108864L;
        public static final long tls = 134217728L;
        public static final long alias_ = 268435456L;
        public static final long shared_ = 536870912L;
        public static final long gshared = 1073741824L;
        public static final long wild = 2147483648L;
        public static final long property = 4294967296L;
        public static final long safe = 8589934592L;
        public static final long trusted = 17179869184L;
        public static final long system = 34359738368L;
        public static final long ctfe = 68719476736L;
        public static final long disable = 137438953472L;
        public static final long result = 274877906944L;
        public static final long nodefaultctor = 549755813888L;
        public static final long temp = 1099511627776L;
        public static final long rvalue = 2199023255552L;
        public static final long nogc = 4398046511104L;
        public static final long volatile_ = 8796093022208L;
        public static final long return_ = 17592186044416L;
        public static final long autoref = 35184372088832L;
        public static final long inference = 70368744177664L;
        public static final long exptemp = 140737488355328L;
        public static final long maybescope = 281474976710656L;
        public static final long scopeinferred = 562949953421312L;
        public static final long future = 1125899906842624L;
        public static final long local = 2251799813685248L;
        public static final long returninferred = 4503599627370496L;
        public static final long TYPECTOR = 2685403140L;
        public static final long FUNCATTR = 4462573780992L;
    }

    static long STCStorageClass = 3399896090034079L;
    public static class MatchAccumulator
    {
        public IntRef count = ref(0);
        public IntRef last = ref(MATCH.nomatch);
        public Ref<FuncDeclaration> lastf = ref(null);
        public Ref<FuncDeclaration> nextf = ref(null);
        public MatchAccumulator(){
        }
        public MatchAccumulator copy(){
            MatchAccumulator r = new MatchAccumulator();
            r.count = count;
            r.last = last;
            r.lastf = lastf;
            r.nextf = nextf;
            return r;
        }
        public MatchAccumulator(int count, int last, FuncDeclaration lastf, FuncDeclaration nextf) {
            this.count = count;
            this.last = last;
            this.lastf = lastf;
            this.nextf = nextf;
        }

        public MatchAccumulator opAssign(MatchAccumulator that) {
            this.count = that.count;
            this.last = that.last;
            this.lastf = that.lastf;
            this.nextf = that.nextf;
            return this;
        }
    }
    public static abstract class Declaration extends Dsymbol
    {
        public Ref<Type> type = ref(null);
        public Ref<Type> originalType = ref(null);
        public Ref<Long> storage_class = ref(0L);
        public Prot protection = new Prot();
        public IntRef linkage = ref(LINK.default_);
        public IntRef inuse = ref(0);
        public ByteSlice mangleOverride = new ByteSlice();
        public  Declaration(Identifier ident) {
            super(ident);
            this.protection = new Prot(Prot.Kind.undefined);
        }

        public  Declaration(Loc loc, Identifier ident) {
            super(loc, ident);
            this.protection = new Prot(Prot.Kind.undefined);
        }

        public  BytePtr kind() {
            return new BytePtr("declaration");
        }

        public  long size(Loc loc) {
            assert(this.type.value != null);
            return this.type.value.size();
        }

        public  boolean checkDisabled(Loc loc, Ptr<Scope> sc, boolean isAliasedDeclaration) {
            if ((this.storage_class.value & 137438953472L) != 0)
            {
                if (!(((sc.get()).func.value != null) && (((sc.get()).func.value.storage_class.value & 137438953472L) != 0)))
                {
                    Dsymbol p = this.toParent();
                    if ((p != null) && (this.isPostBlitDeclaration() != null))
                    {
                        p.error(loc, new BytePtr("is not copyable because it is annotated with `@disable`"));
                    }
                    else
                    {
                        if (isAliasedDeclaration)
                        {
                            FuncDeclaration fd = this.isFuncDeclaration();
                            if (fd != null)
                            {
                                {
                                    FuncDeclaration ovl = fd;
                                    for (; ovl != null;ovl = (FuncDeclaration)ovl.overnext) {
                                        if ((ovl.storage_class.value & 137438953472L) == 0)
                                        {
                                            return false;
                                        }
                                    }
                                }
                            }
                        }
                        this.error(loc, new BytePtr("cannot be used because it is annotated with `@disable`"));
                    }
                }
                return true;
            }
            return false;
        }

        // defaulted all parameters starting with #3
        public  boolean checkDisabled(Loc loc, Ptr<Scope> sc) {
            return checkDisabled(loc, sc, false);
        }

        public  int checkModify(Loc loc, Ptr<Scope> sc, Expression e1, int flag) {
            VarDeclaration v = this.isVarDeclaration();
            if ((v != null) && (v.canassign != 0))
            {
                return Modifiable.initialization;
            }
            if (this.isParameter() || this.isResult())
            {
                {
                    Ptr<Scope> scx = sc;
                    for (; scx != null;scx = (scx.get()).enclosing.value){
                        if ((pequals((scx.get()).func.value, this.parent.value)) && (((scx.get()).flags.value & 96) != 0))
                        {
                            BytePtr s = pcopy(this.isParameter() && (!pequals(this.parent.value.ident.value, Id.ensure)) ? new BytePtr("parameter") : new BytePtr("result"));
                            if (flag == 0)
                            {
                                this.error(loc, new BytePtr("cannot modify %s `%s` in contract"), s, this.toChars());
                            }
                            return Modifiable.initialization;
                        }
                    }
                }
            }
            if ((e1 != null) && ((e1.op.value & 0xFF) == 123) && this.isField())
            {
                VarDeclaration vthis = ((ThisExp)e1).var.value;
                {
                    Ptr<Scope> scx = sc;
                    for (; scx != null;scx = (scx.get()).enclosing.value){
                        if ((pequals((scx.get()).func.value, vthis.parent.value)) && (((scx.get()).flags.value & 96) != 0))
                        {
                            if (flag == 0)
                            {
                                this.error(loc, new BytePtr("cannot modify parameter 'this' in contract"));
                            }
                            return Modifiable.initialization;
                        }
                    }
                }
            }
            if ((v != null) && this.isCtorinit() || this.isField())
            {
                if (((this.storage_class.value & 2113536L) == 2113536L))
                {
                    return Modifiable.initialization;
                }
                return modifyFieldVar(loc, sc, v, e1) ? Modifiable.initialization : Modifiable.yes;
            }
            return Modifiable.yes;
        }

        public  Dsymbol search(Loc loc, Identifier ident, int flags) {
            Dsymbol s = this.search(loc, ident, flags);
            if ((s == null) && (this.type.value != null))
            {
                s = this.type.value.toDsymbol(this._scope.value);
                if (s != null)
                {
                    s = s.search(loc, ident, flags);
                }
            }
            return s;
        }

        // defaulted all parameters starting with #3
        public  Dsymbol search(Loc loc, Identifier ident) {
            return search(loc, ident, 8);
        }

        public  boolean isStatic() {
            return (this.storage_class.value & 1L) != 0L;
        }

        public  boolean isDelete() {
            return false;
        }

        public  boolean isDataseg() {
            return false;
        }

        public  boolean isThreadlocal() {
            return false;
        }

        public  boolean isCodeseg() {
            return false;
        }

        public  boolean isCtorinit() {
            return (this.storage_class.value & 131072L) != 0L;
        }

        public  boolean isFinal() {
            return (this.storage_class.value & 8L) != 0L;
        }

        public  boolean isAbstract() {
            return (this.storage_class.value & 16L) != 0L;
        }

        public  boolean isConst() {
            return (this.storage_class.value & 4L) != 0L;
        }

        public  boolean isImmutable() {
            return (this.storage_class.value & 1048576L) != 0L;
        }

        public  boolean isWild() {
            return (this.storage_class.value & 2147483648L) != 0L;
        }

        public  boolean isAuto() {
            return (this.storage_class.value & 256L) != 0L;
        }

        public  boolean isScope() {
            return (this.storage_class.value & 524288L) != 0L;
        }

        public  boolean isSynchronized() {
            return (this.storage_class.value & 512L) != 0L;
        }

        public  boolean isParameter() {
            return (this.storage_class.value & 32L) != 0L;
        }

        public  boolean isDeprecated() {
            return (this.storage_class.value & 1024L) != 0L;
        }

        public  boolean isDisabled() {
            return (this.storage_class.value & 137438953472L) != 0L;
        }

        public  boolean isOverride() {
            return (this.storage_class.value & 128L) != 0L;
        }

        public  boolean isResult() {
            return (this.storage_class.value & 274877906944L) != 0L;
        }

        public  boolean isField() {
            return (this.storage_class.value & 64L) != 0L;
        }

        public  boolean isIn() {
            return (this.storage_class.value & 2048L) != 0L;
        }

        public  boolean isOut() {
            return (this.storage_class.value & 4096L) != 0L;
        }

        public  boolean isRef() {
            return (this.storage_class.value & 2097152L) != 0L;
        }

        public  boolean isFuture() {
            return (this.storage_class.value & 1125899906842624L) != 0L;
        }

        public  Prot prot() {
            return this.protection;
        }

        public  Declaration isDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public Declaration() {}

        public abstract Declaration copy();
    }
    public static class TupleDeclaration extends Declaration
    {
        public Ref<Ptr<DArray<RootObject>>> objects = ref(null);
        public boolean isexp = false;
        public TypeTuple tupletype = null;
        public  TupleDeclaration(Loc loc, Identifier ident, Ptr<DArray<RootObject>> objects) {
            super(loc, ident);
            this.objects.value = objects;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            throw new AssertionError("Unreachable code!");
        }

        public  BytePtr kind() {
            return new BytePtr("tuple");
        }

        public  Type getType() {
            if (this.isexp)
            {
                return null;
            }
            if (this.tupletype == null)
            {
                {
                    int i = 0;
                    for (; (i < (this.objects.value.get()).length.value);i++){
                        RootObject o = (this.objects.value.get()).get(i);
                        if ((o.dyncast() != DYNCAST.type))
                        {
                            return null;
                        }
                    }
                }
                Ptr<DArray<Type>> types = ((Ptr<DArray<Type>>)this.objects.value);
                Ptr<DArray<Parameter>> args = refPtr(new DArray<Parameter>((this.objects.value.get()).length.value));
                OutBuffer buf = new OutBuffer();
                try {
                    int hasdeco = 1;
                    {
                        int i = 0;
                        for (; (i < (types.get()).length);i++){
                            Type t = (types.get()).get(i);
                            Parameter arg = new Parameter(0L, t, null, null, null);
                            args.get().set(i, arg);
                            if (t.deco.value == null)
                            {
                                hasdeco = 0;
                            }
                        }
                    }
                    this.tupletype = new TypeTuple(args);
                    if (hasdeco != 0)
                    {
                        return typeSemantic(this.tupletype, Loc.initial.value, null);
                    }
                }
                finally {
                }
            }
            return this.tupletype;
        }

        public  Dsymbol toAlias2() {
            {
                int i = 0;
                for (; (i < (this.objects.value.get()).length.value);i++){
                    RootObject o = (this.objects.value.get()).get(i);
                    {
                        Dsymbol s = isDsymbol(o);
                        if ((s) != null)
                        {
                            s = s.toAlias2();
                            this.objects.value.get().set(i, s);
                        }
                    }
                }
            }
            return this;
        }

        public  boolean needThis() {
            {
                int i = 0;
                for (; (i < (this.objects.value.get()).length.value);i++){
                    RootObject o = (this.objects.value.get()).get(i);
                    if ((o.dyncast() == DYNCAST.expression))
                    {
                        Expression e = (Expression)o;
                        if (((e.op.value & 0xFF) == 41))
                        {
                            DsymbolExp ve = (DsymbolExp)e;
                            Declaration d = ve.s.value.isDeclaration();
                            if ((d != null) && d.needThis())
                            {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }

        public  TupleDeclaration isTupleDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TupleDeclaration() {}

        public TupleDeclaration copy() {
            TupleDeclaration that = new TupleDeclaration();
            that.objects = this.objects;
            that.isexp = this.isexp;
            that.tupletype = this.tupletype;
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
    public static class AliasDeclaration extends Declaration
    {
        public Ref<Dsymbol> aliassym = ref(null);
        public Dsymbol overnext = null;
        public Ref<Dsymbol> _import = ref(null);
        public  AliasDeclaration(Loc loc, Identifier ident, Type type) {
            super(loc, ident);
            this.type.value = type;
            assert(type != null);
        }

        public  AliasDeclaration(Loc loc, Identifier ident, Dsymbol s) {
            super(loc, ident);
            assert((!pequals(s, this)));
            this.aliassym.value = s;
            assert(s != null);
        }

        public static AliasDeclaration create(Loc loc, Identifier id, Type type) {
            return new AliasDeclaration(loc, id, type);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            AliasDeclaration sa = this.type.value != null ? new AliasDeclaration(this.loc.value, this.ident.value, this.type.value.syntaxCopy()) : new AliasDeclaration(this.loc.value, this.ident.value, this.aliassym.value.syntaxCopy(null));
            sa.storage_class.value = this.storage_class.value;
            return sa;
        }

        public  boolean overloadInsert(Dsymbol s) {
            if ((this.semanticRun.value >= PASS.semanticdone))
            {
                if (this.type.value != null)
                {
                    return false;
                }
                Dsymbol sa = this.aliassym.value.toAlias();
                {
                    FuncDeclaration fd = sa.isFuncDeclaration();
                    if ((fd) != null)
                    {
                        FuncAliasDeclaration fa = new FuncAliasDeclaration(this.ident.value, fd, true);
                        fa.protection = this.protection.copy();
                        fa.parent.value = this.parent.value;
                        this.aliassym.value = fa;
                        return this.aliassym.value.overloadInsert(s);
                    }
                }
                {
                    TemplateDeclaration td = sa.isTemplateDeclaration();
                    if ((td) != null)
                    {
                        OverDeclaration od = new OverDeclaration(this.ident.value, td, true);
                        od.protection = this.protection.copy();
                        od.parent.value = this.parent.value;
                        this.aliassym.value = od;
                        return this.aliassym.value.overloadInsert(s);
                    }
                }
                {
                    OverDeclaration od = sa.isOverDeclaration();
                    if ((od) != null)
                    {
                        if ((!pequals(sa.ident.value, this.ident.value)) || (!pequals(sa.parent.value, this.parent.value)))
                        {
                            od = new OverDeclaration(this.ident.value, od, true);
                            od.protection = this.protection.copy();
                            od.parent.value = this.parent.value;
                            this.aliassym.value = od;
                        }
                        return od.overloadInsert(s);
                    }
                }
                {
                    OverloadSet os = sa.isOverloadSet();
                    if ((os) != null)
                    {
                        if ((!pequals(sa.ident.value, this.ident.value)) || (!pequals(sa.parent.value, this.parent.value)))
                        {
                            os = new OverloadSet(this.ident.value, os);
                            os.parent.value = this.parent.value;
                            this.aliassym.value = os;
                        }
                        os.push(s);
                        return true;
                    }
                }
                return false;
            }
            if (this.overnext != null)
            {
                return this.overnext.overloadInsert(s);
            }
            if ((s == this))
            {
                return true;
            }
            this.overnext = s;
            return true;
        }

        public  BytePtr kind() {
            return new BytePtr("alias");
        }

        public  Type getType() {
            if (this.type.value != null)
            {
                return this.type.value;
            }
            return this.toAlias().getType();
        }

        public  Dsymbol toAlias() {
            assert((!pequals(this, this.aliassym.value)));
            if ((this.inuse.value == 1) && (this.type.value != null) && (this._scope.value != null))
            {
                this.inuse.value = 2;
                int olderrors = global.errors.value;
                Dsymbol s = this.type.value.toDsymbol(this._scope.value);
                if ((global.errors.value != olderrors))
                {
                    /*goto Lerr*//*unrolled goto*/
                /*Lerr:*/
                    if (global.gag.value != 0)
                    {
                        return this;
                    }
                    this.aliassym.value = new AliasDeclaration(this.loc.value, this.ident.value, Type.terror.value);
                    this.type.value = Type.terror.value;
                    return this.aliassym.value;
                }
                if (s != null)
                {
                    s = s.toAlias();
                    if ((global.errors.value != olderrors))
                    {
                        /*goto Lerr*//*unrolled goto*/
                    /*Lerr:*/
                        if (global.gag.value != 0)
                        {
                            return this;
                        }
                        this.aliassym.value = new AliasDeclaration(this.loc.value, this.ident.value, Type.terror.value);
                        this.type.value = Type.terror.value;
                        return this.aliassym.value;
                    }
                    this.aliassym.value = s;
                    this.inuse.value = 0;
                }
                else
                {
                    Type t = typeSemantic(this.type.value, this.loc.value, this._scope.value);
                    if (((t.ty.value & 0xFF) == ENUMTY.Terror))
                    {
                        /*goto Lerr*//*unrolled goto*/
                    /*Lerr:*/
                        if (global.gag.value != 0)
                        {
                            return this;
                        }
                        this.aliassym.value = new AliasDeclaration(this.loc.value, this.ident.value, Type.terror.value);
                        this.type.value = Type.terror.value;
                        return this.aliassym.value;
                    }
                    if ((global.errors.value != olderrors))
                    {
                        /*goto Lerr*//*unrolled goto*/
                    /*Lerr:*/
                        if (global.gag.value != 0)
                        {
                            return this;
                        }
                        this.aliassym.value = new AliasDeclaration(this.loc.value, this.ident.value, Type.terror.value);
                        this.type.value = Type.terror.value;
                        return this.aliassym.value;
                    }
                    this.inuse.value = 0;
                }
            }
            if (this.inuse.value != 0)
            {
                this.error(new BytePtr("recursive alias declaration"));
            /*Lerr:*/
                if (global.gag.value != 0)
                {
                    return this;
                }
                this.aliassym.value = new AliasDeclaration(this.loc.value, this.ident.value, Type.terror.value);
                this.type.value = Type.terror.value;
                return this.aliassym.value;
            }
            if ((this.semanticRun.value >= PASS.semanticdone))
            {
            }
            else
            {
                if ((this._import.value != null) && (this._import.value._scope.value != null))
                {
                    dsymbolSemantic(this._import.value, null);
                }
                if (this._scope.value != null)
                {
                    aliasSemantic(this, this._scope.value);
                }
            }
            this.inuse.value = 1;
            Dsymbol s = this.aliassym.value != null ? this.aliassym.value.toAlias() : this;
            this.inuse.value = 0;
            return s;
        }

        public  Dsymbol toAlias2() {
            if (this.inuse.value != 0)
            {
                this.error(new BytePtr("recursive alias declaration"));
                return this;
            }
            this.inuse.value = 1;
            Dsymbol s = this.aliassym.value != null ? this.aliassym.value.toAlias2() : this;
            this.inuse.value = 0;
            return s;
        }

        public  boolean isOverloadable() {
            return (this.semanticRun.value < PASS.semanticdone) || (this.aliassym.value != null) && this.aliassym.value.isOverloadable();
        }

        public  AliasDeclaration isAliasDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AliasDeclaration() {}

        public AliasDeclaration copy() {
            AliasDeclaration that = new AliasDeclaration();
            that.aliassym = this.aliassym;
            that.overnext = this.overnext;
            that._import = this._import;
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
    public static class OverDeclaration extends Declaration
    {
        public Dsymbol overnext = null;
        public Dsymbol aliassym = null;
        public boolean hasOverloads = false;
        public  OverDeclaration(Identifier ident, Dsymbol s, boolean hasOverloads) {
            super(ident);
            this.aliassym = s;
            this.hasOverloads = hasOverloads;
            if (hasOverloads)
            {
                {
                    OverDeclaration od = this.aliassym.isOverDeclaration();
                    if ((od) != null)
                    {
                        this.hasOverloads = od.hasOverloads;
                    }
                }
            }
            else
            {
                assert(this.aliassym.isOverDeclaration() == null);
            }
        }

        // defaulted all parameters starting with #3
        public  OverDeclaration(Identifier ident, Dsymbol s) {
            this(ident, s, true);
        }

        public  BytePtr kind() {
            return new BytePtr("overload alias");
        }

        public  boolean equals(RootObject o) {
            if ((pequals(this, o)))
            {
                return true;
            }
            Dsymbol s = isDsymbol(o);
            if (s == null)
            {
                return false;
            }
            OverDeclaration od1 = this;
            {
                OverDeclaration od2 = s.isOverDeclaration();
                if ((od2) != null)
                {
                    return od1.aliassym.equals(od2.aliassym) && ((od1.hasOverloads ? 1 : 0) == (od2.hasOverloads ? 1 : 0));
                }
            }
            if ((pequals(this.aliassym, s)))
            {
                if (this.hasOverloads)
                {
                    return true;
                }
                {
                    FuncDeclaration fd = s.isFuncDeclaration();
                    if ((fd) != null)
                    {
                        return fd.isUnique();
                    }
                }
                {
                    TemplateDeclaration td = s.isTemplateDeclaration();
                    if ((td) != null)
                    {
                        return td.overnext.value == null;
                    }
                }
            }
            return false;
        }

        public  boolean overloadInsert(Dsymbol s) {
            if (this.overnext != null)
            {
                return this.overnext.overloadInsert(s);
            }
            if ((pequals(s, this)))
            {
                return true;
            }
            this.overnext = s;
            return true;
        }

        public  boolean isOverloadable() {
            return true;
        }

        public  Dsymbol isUnique() {
            if (!this.hasOverloads)
            {
                if ((this.aliassym.isFuncDeclaration() != null) || (this.aliassym.isTemplateDeclaration() != null))
                {
                    return this.aliassym;
                }
            }
            Dsymbol result = null;
            Function1<Dsymbol,Integer> __lambda1 = new Function1<Dsymbol,Integer>(){
                public Integer invoke(Dsymbol s) {
                    if (result != null)
                    {
                        result = null;
                        return 1;
                    }
                    else
                    {
                        result = s;
                        return 0;
                    }
                }
            };
            overloadApply(this.aliassym, __lambda1, null);
            return result;
        }

        public  OverDeclaration isOverDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public OverDeclaration() {}

        public OverDeclaration copy() {
            OverDeclaration that = new OverDeclaration();
            that.overnext = this.overnext;
            that.aliassym = this.aliassym;
            that.hasOverloads = this.hasOverloads;
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
    public static class VarDeclaration extends Declaration
    {
        public Ref<Initializer> _init = ref(null);
        public IntRef offset = ref(0);
        public int sequenceNumber = 0;
        public static int nextSequenceNumber = 0;
        public DArray<FuncDeclaration> nestedrefs = new DArray<FuncDeclaration>();
        public int alignment = 0;
        public boolean isargptr = false;
        public boolean ctorinit = false;
        public boolean iscatchvar = false;
        public boolean onstack = false;
        public boolean mynew = false;
        public int canassign = 0;
        public Ref<Boolean> overlapped = ref(false);
        public boolean overlapUnsafe = false;
        public boolean doNotInferScope = false;
        public byte isdataseg = 0;
        public Ref<Dsymbol> aliassym = ref(null);
        public Ref<VarDeclaration> lastVar = ref(null);
        public int endlinnum = 0;
        public int ctfeAdrOnStack = 0;
        public Ref<Expression> edtor = ref(null);
        public Ref<Ptr<IntRange>> range = ref(null);
        public Ptr<DArray<VarDeclaration>> maybes = null;
        public boolean _isAnonymous = false;
        public  VarDeclaration(Loc loc, Type type, Identifier ident, Initializer _init, long storage_class) {
            if ((ident == Identifier.anonymous()))
            {
                ident = Identifier.generateId(new BytePtr("__anonvar"));
                this._isAnonymous = true;
            }
            assert(ident != null);
            super(loc, ident);
            assert((type != null) || (_init != null));
            this.type.value = type;
            this._init.value = _init;
            this.ctfeAdrOnStack = -1;
            this.storage_class.value = storage_class;
            this.sequenceNumber = (nextSequenceNumber += 1);
        }

        // defaulted all parameters starting with #5
        public  VarDeclaration(Loc loc, Type type, Identifier ident, Initializer _init) {
            this(loc, type, ident, _init, 0L);
        }

        public static VarDeclaration create(Loc loc, Type type, Identifier ident, Initializer _init, long storage_class) {
            return new VarDeclaration(loc, type, ident, _init, storage_class);
        }

        // defaulted all parameters starting with #5
        public static VarDeclaration create(Loc loc, Type type, Identifier ident, Initializer _init) {
            return create(loc, type, ident, _init, 0L);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            VarDeclaration v = new VarDeclaration(this.loc.value, this.type.value != null ? this.type.value.syntaxCopy() : null, this.ident.value, this._init.value != null ? syntaxCopy(this._init.value) : null, this.storage_class.value);
            return v;
        }

        public  void setFieldOffset(AggregateDeclaration ad, IntPtr poffset, boolean isunion) {
            if (this.aliassym.value != null)
            {
                TupleDeclaration v2 = this.aliassym.value.isTupleDeclaration();
                assert(v2 != null);
                {
                    int i = 0;
                    for (; (i < (v2.objects.value.get()).length.value);i++){
                        RootObject o = (v2.objects.value.get()).get(i);
                        assert((o.dyncast() == DYNCAST.expression));
                        Expression e = (Expression)o;
                        assert(((e.op.value & 0xFF) == 41));
                        DsymbolExp se = (DsymbolExp)e;
                        se.s.value.setFieldOffset(ad, poffset, isunion);
                    }
                }
                return ;
            }
            if (!this.isField())
            {
                return ;
            }
            assert((this.storage_class.value & 134217763L) == 0);
            if (this.offset.value != 0)
            {
                poffset.set(0, ad.structsize.value);
                return ;
            }
            {
                int i = 0;
                for (; (i < ad.fields.length.value);i++){
                    if ((pequals(ad.fields.get(i), this)))
                    {
                        poffset.set(0, ad.structsize.value);
                        return ;
                    }
                }
            }
            Type t = this.type.value.toBasetype();
            if ((this.storage_class.value & 2097152L) != 0)
            {
                t = Type.tvoidptr.value;
            }
            Type tv = t.baseElemOf();
            if (((tv.ty.value & 0xFF) == ENUMTY.Tstruct))
            {
                TypeStruct ts = (TypeStruct)tv;
                assert((!pequals(ts.sym.value, ad)));
                if (!ts.sym.value.determineSize(this.loc.value))
                {
                    this.type.value = Type.terror.value;
                    this.errors.value = true;
                    return ;
                }
            }
            ad.fields.push(this);
            if (((t.ty.value & 0xFF) == ENUMTY.Terror))
            {
                return ;
            }
            long sz = t.size(this.loc.value);
            assert((sz != -1L) && (sz < 4294967295L));
            int memsize = (int)sz;
            int memalignsize = target.fieldalign(t);
            this.offset.value = AggregateDeclaration.placeField(poffset, memsize, memalignsize, this.alignment, ptr(ad.structsize), ptr(ad.alignsize), isunion);
        }

        public  BytePtr kind() {
            return new BytePtr("variable");
        }

        public  AggregateDeclaration isThis() {
            if ((this.storage_class.value & 69936087043L) == 0)
            {
                {
                    Dsymbol s = this;
                    for (; s != null;s = s.parent.value){
                        AggregateDeclaration ad = s.isMember();
                        if (ad != null)
                        {
                            return ad;
                        }
                        if ((s.parent.value == null) || (s.parent.value.isTemplateMixin() == null))
                        {
                            break;
                        }
                    }
                }
            }
            return null;
        }

        public  boolean needThis() {
            return this.isField();
        }

        public  boolean isAnonymous() {
            return this._isAnonymous;
        }

        public  boolean isExport() {
            return this.protection.kind.value == Prot.Kind.export_;
        }

        public  boolean isImportedSymbol() {
            if ((this.protection.kind.value == Prot.Kind.export_) && (this._init.value == null) && ((this.storage_class.value & 1L) != 0) || (this.parent.value.isModule() != null))
            {
                return true;
            }
            return false;
        }

        public  boolean isDataseg() {
            if (((this.isdataseg & 0xFF) == 0))
            {
                this.isdataseg = (byte)2;
                if (!this.canTakeAddressOf())
                {
                    return false;
                }
                Dsymbol parent = this.toParent();
                if ((parent == null) && ((this.storage_class.value & 1L) == 0))
                {
                    this.error(new BytePtr("forward referenced"));
                    this.type.value = Type.terror.value;
                }
                else if (((this.storage_class.value & 1207959555L) != 0) || (parent.isModule() != null) || (parent.isTemplateInstance() != null) || (parent.isNspace() != null))
                {
                    assert(!this.isParameter() && !this.isResult());
                    this.isdataseg = (byte)1;
                }
            }
            return (this.isdataseg & 0xFF) == 1;
        }

        public  boolean isThreadlocal() {
            boolean i = this.isDataseg() && ((this.storage_class.value & 1611661316L) == 0);
            return i;
        }

        public  boolean isCTFE() {
            return (this.storage_class.value & 68719476736L) != 0L;
        }

        public  boolean isOverlappedWith(VarDeclaration v) {
            long vsz = v.type.value.size();
            long tsz = this.type.value.size();
            assert((vsz != -1L) && (tsz != -1L));
            return ((long)this.offset.value < (long)v.offset.value + vsz) && ((long)v.offset.value < (long)this.offset.value + tsz);
        }

        public  boolean hasPointers() {
            return !this.isDataseg() && this.type.value.hasPointers();
        }

        public  boolean canTakeAddressOf() {
            return (this.storage_class.value & 8388608L) == 0;
        }

        public  boolean needsScopeDtor() {
            return (this.edtor.value != null) && ((this.storage_class.value & 16777216L) == 0);
        }

        public  Expression callScopeDtor(Ptr<Scope> sc) {
            if ((this.storage_class.value & 18878528L) != 0)
            {
                return null;
            }
            if (this.iscatchvar)
            {
                return null;
            }
            Expression e = null;
            Type tv = this.type.value.baseElemOf();
            if (((tv.ty.value & 0xFF) == ENUMTY.Tstruct))
            {
                StructDeclaration sd = ((TypeStruct)tv).sym.value;
                if ((sd.dtor.value == null) || sd.errors.value)
                {
                    return null;
                }
                long sz = this.type.value.size();
                assert((sz != -1L));
                if (sz == 0)
                {
                    return null;
                }
                if (((this.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tstruct))
                {
                    e = new VarExp(this.loc.value, this, true);
                    e.type.value = e.type.value.mutableOf();
                    e.type.value = e.type.value.unSharedOf();
                    e = new DotVarExp(this.loc.value, e, sd.dtor.value, false);
                    e = new CallExp(this.loc.value, e);
                }
                else
                {
                    e = new VarExp(this.loc.value, this, true);
                    long sdsz = sd.type.value.size();
                    assert((sdsz != -1L) && (sdsz != 0L));
                    long n = sz / sdsz;
                    e = new SliceExp(this.loc.value, e, new IntegerExp(this.loc.value, 0L, Type.tsize_t.value), new IntegerExp(this.loc.value, n, Type.tsize_t.value));
                    ((SliceExp)e).upperIsInBounds = true;
                    ((SliceExp)e).lowerIsLessThanUpper = true;
                    e.type.value = sd.type.value.arrayOf();
                    e = new CallExp(this.loc.value, new IdentifierExp(this.loc.value, Id.__ArrayDtor), e);
                }
                return e;
            }
            if (((this.storage_class.value & 524544L) != 0) && ((this.storage_class.value & 32L) == 0))
            {
                {
                    ClassDeclaration cd = this.type.value.isClassHandle();
                    for (; cd != null;cd = cd.baseClass.value){
                        if ((cd.classKind.value == ClassKind.cpp))
                        {
                            break;
                        }
                        if (this.mynew || this.onstack)
                        {
                            Expression ec = null;
                            ec = new VarExp(this.loc.value, this, true);
                            e = new DeleteExp(this.loc.value, ec, true);
                            e.type.value = Type.tvoid.value;
                            break;
                        }
                    }
                }
            }
            return e;
        }

        public  Expression getConstInitializer(boolean needFullType) {
            assert((this.type.value != null) && (this._init.value != null));
            int oldgag = global.gag.value;
            if (global.gag.value != 0)
            {
                Dsymbol sym = this.toParent().isAggregateDeclaration();
                if ((sym != null) && (sym.isSpeculative() == null))
                {
                    global.gag.value = 0;
                }
            }
            if (this._scope.value != null)
            {
                this.inuse.value++;
                this._init.value = initializerSemantic(this._init.value, this._scope.value, this.type.value, NeedInterpret.INITinterpret);
                this._scope.value = null;
                this.inuse.value--;
            }
            Expression e = initializerToExpression(this._init.value, needFullType ? this.type.value : null);
            global.gag.value = oldgag;
            return e;
        }

        // defaulted all parameters starting with #1
        public  Expression getConstInitializer() {
            return getConstInitializer(true);
        }

        public  Expression expandInitializer(Loc loc) {
            assert(((this.storage_class.value & 8388608L) != 0) && (this._init.value != null));
            Expression e = this.getConstInitializer(true);
            if (e == null)
            {
                error(loc, new BytePtr("cannot make expression out of initializer for `%s`"), this.toChars());
                return new ErrorExp();
            }
            e = e.copy();
            e.loc.value = loc.copy();
            return e;
        }

        public  void checkCtorConstInit() {
        }

        public  boolean checkNestedReference(Ptr<Scope> sc, Loc loc) {
            if (((sc.get()).intypeof.value == 1) || (((sc.get()).flags.value & 128) != 0))
            {
                return false;
            }
            if ((this.parent.value == null) || (pequals(this.parent.value, (sc.get()).parent.value)))
            {
                return false;
            }
            if (this.isDataseg() || ((this.storage_class.value & 8388608L) != 0))
            {
                return false;
            }
            FuncDeclaration fdthis = (sc.get()).parent.value.isFuncDeclaration();
            if (fdthis == null)
            {
                return false;
            }
            Dsymbol p = this.toParent2();
            ensureStaticLinkTo(fdthis, p);
            FuncDeclaration fdv = p.isFuncDeclaration();
            if ((fdv == null) || (pequals(fdv, fdthis)))
            {
                return false;
            }
            {
                int i = 0;
                for (; 1 != 0;i++){
                    if ((i == this.nestedrefs.length.value))
                    {
                        this.nestedrefs.push(fdthis);
                        break;
                    }
                    if ((pequals(this.nestedrefs.get(i), fdthis)))
                    {
                        break;
                    }
                }
            }
            if ((pequals(fdthis.ident.value, Id.require)) || (pequals(fdthis.ident.value, Id.ensure)))
            {
                return false;
            }
            if (loc.isValid())
            {
                if ((fdthis.getLevelAndCheck(loc, sc, fdv) == -2))
                {
                    return true;
                }
            }
            if (((sc.get()).intypeof.value == 0) && (((sc.get()).flags.value & 256) == 0) && ((fdv.flags & FUNCFLAG.compileTimeOnly) != 0) || ((fdthis.flags & FUNCFLAG.compileTimeOnly) == 0))
            {
                {
                    int i = 0;
                    for (; 1 != 0;i++){
                        if ((i == fdv.closureVars.length.value))
                        {
                            fdv.closureVars.push(this);
                            break;
                        }
                        if ((pequals(fdv.closureVars.get(i), this)))
                        {
                            break;
                        }
                    }
                }
            }
            if ((pequals(this.ident.value, Id.dollar)))
            {
                error(loc, new BytePtr("cannnot use `$` inside a function literal"));
                return true;
            }
            if ((pequals(this.ident.value, Id.withSym.value)))
            {
                ExpInitializer ez = this._init.value.isExpInitializer();
                assert(ez != null);
                Expression e = ez.exp.value;
                if (((e.op.value & 0xFF) == 95) || ((e.op.value & 0xFF) == 96))
                {
                    e = ((AssignExp)e).e2.value;
                }
                return lambdaCheckForNestedRef(e, sc);
            }
            return false;
        }

        public  Dsymbol toAlias() {
            if ((this.type.value == null) || (this.type.value.deco.value == null) && (this._scope.value != null))
            {
                dsymbolSemantic(this, this._scope.value);
            }
            assert((!pequals(this, this.aliassym.value)));
            Dsymbol s = this.aliassym.value != null ? this.aliassym.value.toAlias() : this;
            return s;
        }

        public  VarDeclaration isVarDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }

        public  boolean enclosesLifetimeOf(VarDeclaration v) {
            return this.sequenceNumber < v.sequenceNumber;
        }

        public  void addMaybe(VarDeclaration v) {
            if (this.maybes == null)
            {
                this.maybes = refPtr(new DArray<VarDeclaration>());
            }
            (this.maybes.get()).push(v);
        }


        public VarDeclaration() {}

        public VarDeclaration copy() {
            VarDeclaration that = new VarDeclaration();
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
    public static class SymbolDeclaration extends Declaration
    {
        public StructDeclaration dsym = null;
        public  SymbolDeclaration(Loc loc, StructDeclaration dsym) {
            super(loc, dsym.ident.value);
            this.dsym = dsym;
            this.storage_class.value |= 4L;
        }

        public  SymbolDeclaration isSymbolDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public SymbolDeclaration() {}

        public SymbolDeclaration copy() {
            SymbolDeclaration that = new SymbolDeclaration();
            that.dsym = this.dsym;
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
    public static class TypeInfoDeclaration extends VarDeclaration
    {
        public Type tinfo = null;
        public  TypeInfoDeclaration(Type tinfo) {
            super(Loc.initial.value, Type.dtypeinfo.value.type.value, tinfo.getTypeInfoIdent(), null, 0L);
            this.tinfo = tinfo;
            this.storage_class.value = 1073741825L;
            this.protection = new Prot(Prot.Kind.public_).copy();
            this.linkage.value = LINK.c;
            this.alignment = target.ptrsize.value;
        }

        public static TypeInfoDeclaration create(Type tinfo) {
            return new TypeInfoDeclaration(tinfo);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            throw new AssertionError("Unreachable code!");
        }

        public  BytePtr toChars() {
            OutBuffer buf = new OutBuffer();
            try {
                buf.writestring(new ByteSlice("typeid("));
                buf.writestring(this.tinfo.toChars());
                buf.writeByte(41);
                return buf.extractChars();
            }
            finally {
            }
        }

        public  TypeInfoDeclaration isTypeInfoDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeInfoDeclaration() {}

        public TypeInfoDeclaration copy() {
            TypeInfoDeclaration that = new TypeInfoDeclaration();
            that.tinfo = this.tinfo;
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
    public static class TypeInfoStructDeclaration extends TypeInfoDeclaration
    {
        public  TypeInfoStructDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfostruct == null)
            {
                ObjectNotFound(Id.TypeInfo_Struct);
            }
            this.type.value = Type.typeinfostruct.type.value;
        }

        public static TypeInfoStructDeclaration create(Type tinfo) {
            return new TypeInfoStructDeclaration(tinfo);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeInfoStructDeclaration() {}

        public TypeInfoStructDeclaration copy() {
            TypeInfoStructDeclaration that = new TypeInfoStructDeclaration();
            that.tinfo = this.tinfo;
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
    public static class TypeInfoClassDeclaration extends TypeInfoDeclaration
    {
        public  TypeInfoClassDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfoclass.value == null)
            {
                ObjectNotFound(Id.TypeInfo_Class);
            }
            this.type.value = Type.typeinfoclass.value.type.value;
        }

        public static TypeInfoClassDeclaration create(Type tinfo) {
            return new TypeInfoClassDeclaration(tinfo);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeInfoClassDeclaration() {}

        public TypeInfoClassDeclaration copy() {
            TypeInfoClassDeclaration that = new TypeInfoClassDeclaration();
            that.tinfo = this.tinfo;
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
    public static class TypeInfoInterfaceDeclaration extends TypeInfoDeclaration
    {
        public  TypeInfoInterfaceDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfointerface == null)
            {
                ObjectNotFound(Id.TypeInfo_Interface);
            }
            this.type.value = Type.typeinfointerface.type.value;
        }

        public static TypeInfoInterfaceDeclaration create(Type tinfo) {
            return new TypeInfoInterfaceDeclaration(tinfo);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeInfoInterfaceDeclaration() {}

        public TypeInfoInterfaceDeclaration copy() {
            TypeInfoInterfaceDeclaration that = new TypeInfoInterfaceDeclaration();
            that.tinfo = this.tinfo;
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
    public static class TypeInfoPointerDeclaration extends TypeInfoDeclaration
    {
        public  TypeInfoPointerDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfopointer == null)
            {
                ObjectNotFound(Id.TypeInfo_Pointer);
            }
            this.type.value = Type.typeinfopointer.type.value;
        }

        public static TypeInfoPointerDeclaration create(Type tinfo) {
            return new TypeInfoPointerDeclaration(tinfo);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeInfoPointerDeclaration() {}

        public TypeInfoPointerDeclaration copy() {
            TypeInfoPointerDeclaration that = new TypeInfoPointerDeclaration();
            that.tinfo = this.tinfo;
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
    public static class TypeInfoArrayDeclaration extends TypeInfoDeclaration
    {
        public  TypeInfoArrayDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfoarray == null)
            {
                ObjectNotFound(Id.TypeInfo_Array);
            }
            this.type.value = Type.typeinfoarray.type.value;
        }

        public static TypeInfoArrayDeclaration create(Type tinfo) {
            return new TypeInfoArrayDeclaration(tinfo);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeInfoArrayDeclaration() {}

        public TypeInfoArrayDeclaration copy() {
            TypeInfoArrayDeclaration that = new TypeInfoArrayDeclaration();
            that.tinfo = this.tinfo;
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
    public static class TypeInfoStaticArrayDeclaration extends TypeInfoDeclaration
    {
        public  TypeInfoStaticArrayDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfostaticarray == null)
            {
                ObjectNotFound(Id.TypeInfo_StaticArray);
            }
            this.type.value = Type.typeinfostaticarray.type.value;
        }

        public static TypeInfoStaticArrayDeclaration create(Type tinfo) {
            return new TypeInfoStaticArrayDeclaration(tinfo);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeInfoStaticArrayDeclaration() {}

        public TypeInfoStaticArrayDeclaration copy() {
            TypeInfoStaticArrayDeclaration that = new TypeInfoStaticArrayDeclaration();
            that.tinfo = this.tinfo;
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
    public static class TypeInfoAssociativeArrayDeclaration extends TypeInfoDeclaration
    {
        public  TypeInfoAssociativeArrayDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfoassociativearray == null)
            {
                ObjectNotFound(Id.TypeInfo_AssociativeArray);
            }
            this.type.value = Type.typeinfoassociativearray.type.value;
        }

        public static TypeInfoAssociativeArrayDeclaration create(Type tinfo) {
            return new TypeInfoAssociativeArrayDeclaration(tinfo);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeInfoAssociativeArrayDeclaration() {}

        public TypeInfoAssociativeArrayDeclaration copy() {
            TypeInfoAssociativeArrayDeclaration that = new TypeInfoAssociativeArrayDeclaration();
            that.tinfo = this.tinfo;
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
    public static class TypeInfoEnumDeclaration extends TypeInfoDeclaration
    {
        public  TypeInfoEnumDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfoenum == null)
            {
                ObjectNotFound(Id.TypeInfo_Enum);
            }
            this.type.value = Type.typeinfoenum.type.value;
        }

        public static TypeInfoEnumDeclaration create(Type tinfo) {
            return new TypeInfoEnumDeclaration(tinfo);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeInfoEnumDeclaration() {}

        public TypeInfoEnumDeclaration copy() {
            TypeInfoEnumDeclaration that = new TypeInfoEnumDeclaration();
            that.tinfo = this.tinfo;
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
    public static class TypeInfoFunctionDeclaration extends TypeInfoDeclaration
    {
        public  TypeInfoFunctionDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfofunction == null)
            {
                ObjectNotFound(Id.TypeInfo_Function);
            }
            this.type.value = Type.typeinfofunction.type.value;
        }

        public static TypeInfoFunctionDeclaration create(Type tinfo) {
            return new TypeInfoFunctionDeclaration(tinfo);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeInfoFunctionDeclaration() {}

        public TypeInfoFunctionDeclaration copy() {
            TypeInfoFunctionDeclaration that = new TypeInfoFunctionDeclaration();
            that.tinfo = this.tinfo;
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
    public static class TypeInfoDelegateDeclaration extends TypeInfoDeclaration
    {
        public  TypeInfoDelegateDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfodelegate == null)
            {
                ObjectNotFound(Id.TypeInfo_Delegate);
            }
            this.type.value = Type.typeinfodelegate.type.value;
        }

        public static TypeInfoDelegateDeclaration create(Type tinfo) {
            return new TypeInfoDelegateDeclaration(tinfo);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeInfoDelegateDeclaration() {}

        public TypeInfoDelegateDeclaration copy() {
            TypeInfoDelegateDeclaration that = new TypeInfoDelegateDeclaration();
            that.tinfo = this.tinfo;
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
    public static class TypeInfoTupleDeclaration extends TypeInfoDeclaration
    {
        public  TypeInfoTupleDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfotypelist == null)
            {
                ObjectNotFound(Id.TypeInfo_Tuple);
            }
            this.type.value = Type.typeinfotypelist.type.value;
        }

        public static TypeInfoTupleDeclaration create(Type tinfo) {
            return new TypeInfoTupleDeclaration(tinfo);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeInfoTupleDeclaration() {}

        public TypeInfoTupleDeclaration copy() {
            TypeInfoTupleDeclaration that = new TypeInfoTupleDeclaration();
            that.tinfo = this.tinfo;
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
    public static class TypeInfoConstDeclaration extends TypeInfoDeclaration
    {
        public  TypeInfoConstDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfoconst == null)
            {
                ObjectNotFound(Id.TypeInfo_Const);
            }
            this.type.value = Type.typeinfoconst.type.value;
        }

        public static TypeInfoConstDeclaration create(Type tinfo) {
            return new TypeInfoConstDeclaration(tinfo);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeInfoConstDeclaration() {}

        public TypeInfoConstDeclaration copy() {
            TypeInfoConstDeclaration that = new TypeInfoConstDeclaration();
            that.tinfo = this.tinfo;
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
    public static class TypeInfoInvariantDeclaration extends TypeInfoDeclaration
    {
        public  TypeInfoInvariantDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfoinvariant == null)
            {
                ObjectNotFound(Id.TypeInfo_Invariant);
            }
            this.type.value = Type.typeinfoinvariant.type.value;
        }

        public static TypeInfoInvariantDeclaration create(Type tinfo) {
            return new TypeInfoInvariantDeclaration(tinfo);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeInfoInvariantDeclaration() {}

        public TypeInfoInvariantDeclaration copy() {
            TypeInfoInvariantDeclaration that = new TypeInfoInvariantDeclaration();
            that.tinfo = this.tinfo;
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
    public static class TypeInfoSharedDeclaration extends TypeInfoDeclaration
    {
        public  TypeInfoSharedDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfoshared == null)
            {
                ObjectNotFound(Id.TypeInfo_Shared);
            }
            this.type.value = Type.typeinfoshared.type.value;
        }

        public static TypeInfoSharedDeclaration create(Type tinfo) {
            return new TypeInfoSharedDeclaration(tinfo);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeInfoSharedDeclaration() {}

        public TypeInfoSharedDeclaration copy() {
            TypeInfoSharedDeclaration that = new TypeInfoSharedDeclaration();
            that.tinfo = this.tinfo;
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
    public static class TypeInfoWildDeclaration extends TypeInfoDeclaration
    {
        public  TypeInfoWildDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfowild == null)
            {
                ObjectNotFound(Id.TypeInfo_Wild);
            }
            this.type.value = Type.typeinfowild.type.value;
        }

        public static TypeInfoWildDeclaration create(Type tinfo) {
            return new TypeInfoWildDeclaration(tinfo);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeInfoWildDeclaration() {}

        public TypeInfoWildDeclaration copy() {
            TypeInfoWildDeclaration that = new TypeInfoWildDeclaration();
            that.tinfo = this.tinfo;
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
    public static class TypeInfoVectorDeclaration extends TypeInfoDeclaration
    {
        public  TypeInfoVectorDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfovector == null)
            {
                ObjectNotFound(Id.TypeInfo_Vector);
            }
            this.type.value = Type.typeinfovector.type.value;
        }

        public static TypeInfoVectorDeclaration create(Type tinfo) {
            return new TypeInfoVectorDeclaration(tinfo);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeInfoVectorDeclaration() {}

        public TypeInfoVectorDeclaration copy() {
            TypeInfoVectorDeclaration that = new TypeInfoVectorDeclaration();
            that.tinfo = this.tinfo;
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
    public static class ThisDeclaration extends VarDeclaration
    {
        public  ThisDeclaration(Loc loc, Type t) {
            super(loc, t, Id.This.value, null, 0L);
            this.storage_class.value |= 16777216L;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            throw new AssertionError("Unreachable code!");
        }

        public  ThisDeclaration isThisDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ThisDeclaration() {}

        public ThisDeclaration copy() {
            ThisDeclaration that = new ThisDeclaration();
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
}
