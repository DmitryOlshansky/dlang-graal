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

    // Erasure: checkFrameAccess<Loc, Ptr, AggregateDeclaration, int>
    public static boolean checkFrameAccess(Loc loc, Ptr<Scope> sc, AggregateDeclaration ad, int iStart) {
        Dsymbol sparent = ad.toParentLocal();
        Dsymbol sparent2 = ad.toParent2();
        Dsymbol s = (sc.get()).func;
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
            for (; (i < ad.fields.length);i++){
                VarDeclaration vd = ad.fields.get(i);
                Type tb = vd.type.baseElemOf();
                if (((tb.ty & 0xFF) == ENUMTY.Tstruct))
                {
                    (result ? 1 : 0) |= (checkFrameAccess(loc, sc, (((TypeStruct)tb)).sym, 0) ? 1 : 0);
                }
            }
        }
        return result;
    }

    // defaulted all parameters starting with #4
    public static boolean checkFrameAccess(Loc loc, Ptr<Scope> sc, AggregateDeclaration ad) {
        return checkFrameAccess(loc, sc, ad, 0);
    }

    // Erasure: modifyFieldVar<Loc, Ptr, VarDeclaration, Expression>
    public static boolean modifyFieldVar(Loc loc, Ptr<Scope> sc, VarDeclaration var, Expression e1) {
        Dsymbol s = (sc.get()).func;
        for (; 1 != 0;){
            FuncDeclaration fd = null;
            if (s != null)
            {
                fd = s.isFuncDeclaration();
            }
            if ((fd != null) && (fd.isCtorDeclaration() != null) && var.isField() || (fd.isStaticCtorDeclaration() != null) && !var.isField() && (pequals(fd.toParentDecl(), var.toParent2())) && (e1 == null) || ((e1.op & 0xFF) == 123))
            {
                boolean result = true;
                var.ctorinit = true;
                if (var.isField() && ((sc.get()).ctorflow.fieldinit.getLength() != 0) && ((sc.get()).intypeof == 0))
                {
                    assert(e1 != null);
                    boolean mustInit = ((var.storage_class & 549755813888L) != 0L) || var.type.needsNested();
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
                    Ptr<FieldInit> fieldInit = (sc.get()).ctorflow.fieldinit.getPtr(i);
                    int fi = (fieldInit.get()).csx.value;
                    if ((fi & 1) != 0)
                    {
                        if (var.type.isMutable() && e1.type.value.isMutable())
                        {
                            result = false;
                        }
                        else
                        {
                            BytePtr modStr = pcopy(!var.type.isMutable() ? MODtoChars(var.type.mod) : MODtoChars(e1.type.value.mod));
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
                        if (!mustInit && var.type.isMutable() && e1.type.value.isMutable())
                        {
                            result = false;
                        }
                        else
                        {
                            BytePtr modStr = pcopy(!var.type.isMutable() ? MODtoChars(var.type.mod) : MODtoChars(e1.type.value.mod));
                            error(loc, new BytePtr("%s field `%s` initialization is not allowed in loops or after labels"), modStr, var.toChars());
                        }
                    }
                    (fieldInit.get()).csx.value |= 1;
                    (fieldInit.get()).loc.opAssign(e1.loc.copy());
                    if (var.overlapped)
                    {
                        {
                            Slice<VarDeclaration> __r934 = ad.fields.opSlice().copy();
                            int __key933 = 0;
                            for (; (__key933 < __r934.getLength());__key933 += 1) {
                                VarDeclaration v = __r934.get(__key933);
                                int j = __key933;
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
                else if ((!pequals(fd, (sc.get()).func)))
                {
                    if (var.type.isMutable())
                    {
                        result = false;
                    }
                    else if ((sc.get()).func.fes != null)
                    {
                        BytePtr p = pcopy(var.isField() ? new BytePtr("field") : var.kind());
                        error(loc, new BytePtr("%s %s `%s` initialization is not allowed in foreach loop"), MODtoChars(var.type.mod), p, var.toChars());
                    }
                    else
                    {
                        BytePtr p = pcopy(var.isField() ? new BytePtr("field") : var.kind());
                        error(loc, new BytePtr("%s %s `%s` initialization is not allowed in nested function `%s`"), MODtoChars(var.type.mod), p, var.toChars(), (sc.get()).func.toChars());
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

    // Erasure: ObjectNotFound<Identifier>
    public static void ObjectNotFound(Identifier id) {
        error(Loc.initial, new BytePtr("`%s` not found. object.d may be incorrectly installed or corrupt."), id.toChars());
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
        public int count = 0;
        public int last = MATCH.nomatch;
        public FuncDeclaration lastf = null;
        public FuncDeclaration nextf = null;
        public MatchAccumulator(){ }
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
        public Type type = null;
        public Type originalType = null;
        public long storage_class = 0L;
        public Prot protection = new Prot();
        public int linkage = LINK.default_;
        public int inuse = 0;
        public ByteSlice mangleOverride = new ByteSlice();
        // Erasure: __ctor<Identifier>
        public  Declaration(Identifier ident) {
            super(ident);
            this.protection = new Prot(Prot.Kind.undefined);
        }

        // Erasure: __ctor<Loc, Identifier>
        public  Declaration(Loc loc, Identifier ident) {
            super(loc, ident);
            this.protection = new Prot(Prot.Kind.undefined);
        }

        // Erasure: kind<>
        public  BytePtr kind() {
            return new BytePtr("declaration");
        }

        // Erasure: size<Loc>
        public  long size(Loc loc) {
            assert(this.type != null);
            return this.type.size();
        }

        // Erasure: checkDisabled<Loc, Ptr, boolean>
        public  boolean checkDisabled(Loc loc, Ptr<Scope> sc, boolean isAliasedDeclaration) {
            if ((this.storage_class & 137438953472L) != 0)
            {
                if (!(((sc.get()).func != null) && (((sc.get()).func.storage_class & 137438953472L) != 0)))
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
                                    for (; ovl != null;ovl = ((FuncDeclaration)ovl.overnext)) {
                                        if ((ovl.storage_class & 137438953472L) == 0)
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

        // Erasure: checkModify<Loc, Ptr, Expression, int>
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
                    for (; scx != null;scx = pcopy((scx.get()).enclosing)){
                        if ((pequals((scx.get()).func, this.parent.value)) && (((scx.get()).flags & 96) != 0))
                        {
                            BytePtr s = pcopy(this.isParameter() && (!pequals(this.parent.value.ident, Id.ensure)) ? new BytePtr("parameter") : new BytePtr("result"));
                            if (flag == 0)
                            {
                                this.error(loc, new BytePtr("cannot modify %s `%s` in contract"), s, this.toChars());
                            }
                            return Modifiable.initialization;
                        }
                    }
                }
            }
            if ((e1 != null) && ((e1.op & 0xFF) == 123) && this.isField())
            {
                VarDeclaration vthis = (((ThisExp)e1)).var;
                {
                    Ptr<Scope> scx = sc;
                    for (; scx != null;scx = pcopy((scx.get()).enclosing)){
                        if ((pequals((scx.get()).func, vthis.parent.value)) && (((scx.get()).flags & 96) != 0))
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
                if (((this.storage_class & 2113536L) == 2113536L))
                {
                    return Modifiable.initialization;
                }
                return modifyFieldVar(loc, sc, v, e1) ? Modifiable.initialization : Modifiable.yes;
            }
            return Modifiable.yes;
        }

        // Erasure: search<Loc, Identifier, int>
        public  Dsymbol search(Loc loc, Identifier ident, int flags) {
            Dsymbol s = this.search(loc, ident, flags);
            if ((s == null) && (this.type != null))
            {
                s = this.type.toDsymbol(this._scope);
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

        // Erasure: isStatic<>
        public  boolean isStatic() {
            return (this.storage_class & 1L) != 0L;
        }

        // Erasure: isDelete<>
        public  boolean isDelete() {
            return false;
        }

        // Erasure: isDataseg<>
        public  boolean isDataseg() {
            return false;
        }

        // Erasure: isThreadlocal<>
        public  boolean isThreadlocal() {
            return false;
        }

        // Erasure: isCodeseg<>
        public  boolean isCodeseg() {
            return false;
        }

        // Erasure: isCtorinit<>
        public  boolean isCtorinit() {
            return (this.storage_class & 131072L) != 0L;
        }

        // Erasure: isFinal<>
        public  boolean isFinal() {
            return (this.storage_class & 8L) != 0L;
        }

        // Erasure: isAbstract<>
        public  boolean isAbstract() {
            return (this.storage_class & 16L) != 0L;
        }

        // Erasure: isConst<>
        public  boolean isConst() {
            return (this.storage_class & 4L) != 0L;
        }

        // Erasure: isImmutable<>
        public  boolean isImmutable() {
            return (this.storage_class & 1048576L) != 0L;
        }

        // Erasure: isWild<>
        public  boolean isWild() {
            return (this.storage_class & 2147483648L) != 0L;
        }

        // Erasure: isAuto<>
        public  boolean isAuto() {
            return (this.storage_class & 256L) != 0L;
        }

        // Erasure: isScope<>
        public  boolean isScope() {
            return (this.storage_class & 524288L) != 0L;
        }

        // Erasure: isSynchronized<>
        public  boolean isSynchronized() {
            return (this.storage_class & 512L) != 0L;
        }

        // Erasure: isParameter<>
        public  boolean isParameter() {
            return (this.storage_class & 32L) != 0L;
        }

        // Erasure: isDeprecated<>
        public  boolean isDeprecated() {
            return (this.storage_class & 1024L) != 0L;
        }

        // Erasure: isDisabled<>
        public  boolean isDisabled() {
            return (this.storage_class & 137438953472L) != 0L;
        }

        // Erasure: isOverride<>
        public  boolean isOverride() {
            return (this.storage_class & 128L) != 0L;
        }

        // Erasure: isResult<>
        public  boolean isResult() {
            return (this.storage_class & 274877906944L) != 0L;
        }

        // Erasure: isField<>
        public  boolean isField() {
            return (this.storage_class & 64L) != 0L;
        }

        // Erasure: isIn<>
        public  boolean isIn() {
            return (this.storage_class & 2048L) != 0L;
        }

        // Erasure: isOut<>
        public  boolean isOut() {
            return (this.storage_class & 4096L) != 0L;
        }

        // Erasure: isRef<>
        public  boolean isRef() {
            return (this.storage_class & 2097152L) != 0L;
        }

        // Erasure: isFuture<>
        public  boolean isFuture() {
            return (this.storage_class & 1125899906842624L) != 0L;
        }

        // Erasure: prot<>
        public  Prot prot() {
            return this.protection;
        }

        // Erasure: isDeclaration<>
        public  Declaration isDeclaration() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public Declaration() {}

        public abstract Declaration copy();
    }
    public static class TupleDeclaration extends Declaration
    {
        public DArray<RootObject> objects = null;
        public boolean isexp = false;
        public TypeTuple tupletype = null;
        // Erasure: __ctor<Loc, Identifier, Ptr>
        public  TupleDeclaration(Loc loc, Identifier ident, DArray<RootObject> objects) {
            super(loc, ident);
            this.objects = pcopy(objects);
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: kind<>
        public  BytePtr kind() {
            return new BytePtr("tuple");
        }

        // Erasure: getType<>
        public  Type getType() {
            if (this.isexp)
            {
                return null;
            }
            if (this.tupletype == null)
            {
                {
                    int i = 0;
                    for (; (i < (this.objects).length);i++){
                        RootObject o = (this.objects).get(i);
                        if ((o.dyncast() != DYNCAST.type))
                        {
                            return null;
                        }
                    }
                }
                DArray<Type> types = ((DArray<Type>)this.objects);
                DArray<Parameter> args = new DArray<Parameter>((this.objects).length);
                OutBuffer buf = new OutBuffer();
                try {
                    int hasdeco = 1;
                    {
                        int i = 0;
                        for (; (i < (types).length);i++){
                            Type t = (types).get(i);
                            Parameter arg = new Parameter(0L, t, null, null, null);
                            args.set(i, arg);
                            if (t.deco == null)
                            {
                                hasdeco = 0;
                            }
                        }
                    }
                    this.tupletype = new TypeTuple(args);
                    if (hasdeco != 0)
                    {
                        return typeSemantic(this.tupletype, Loc.initial, null);
                    }
                }
                finally {
                }
            }
            return this.tupletype;
        }

        // Erasure: toAlias2<>
        public  Dsymbol toAlias2() {
            {
                int i = 0;
                for (; (i < (this.objects).length);i++){
                    RootObject o = (this.objects).get(i);
                    {
                        Dsymbol s = isDsymbol(o);
                        if ((s) != null)
                        {
                            s = s.toAlias2();
                            this.objects.set(i, s);
                        }
                    }
                }
            }
            return this;
        }

        // Erasure: needThis<>
        public  boolean needThis() {
            {
                int i = 0;
                for (; (i < (this.objects).length);i++){
                    RootObject o = (this.objects).get(i);
                    if ((o.dyncast() == DYNCAST.expression))
                    {
                        Expression e = ((Expression)o);
                        if (((e.op & 0xFF) == 41))
                        {
                            DsymbolExp ve = ((DsymbolExp)e);
                            Declaration d = ve.s.isDeclaration();
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

        // Erasure: isTupleDeclaration<>
        public  TupleDeclaration isTupleDeclaration() {
            return this;
        }

        // Erasure: accept<Visitor>
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
        public Dsymbol aliassym = null;
        public Dsymbol overnext = null;
        public Dsymbol _import = null;
        // Erasure: __ctor<Loc, Identifier, Type>
        public  AliasDeclaration(Loc loc, Identifier ident, Type type) {
            super(loc, ident);
            this.type = type;
            assert(type != null);
        }

        // Erasure: __ctor<Loc, Identifier, Dsymbol>
        public  AliasDeclaration(Loc loc, Identifier ident, Dsymbol s) {
            super(loc, ident);
            assert((!pequals(s, this)));
            this.aliassym = s;
            assert(s != null);
        }

        // Erasure: create<Loc, Identifier, Type>
        public static AliasDeclaration create(Loc loc, Identifier id, Type type) {
            return new AliasDeclaration(loc, id, type);
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            AliasDeclaration sa = this.type != null ? new AliasDeclaration(this.loc, this.ident, this.type.syntaxCopy()) : new AliasDeclaration(this.loc, this.ident, this.aliassym.syntaxCopy(null));
            sa.storage_class = this.storage_class;
            return sa;
        }

        // Erasure: overloadInsert<Dsymbol>
        public  boolean overloadInsert(Dsymbol s) {
            if ((this.semanticRun >= PASS.semanticdone))
            {
                if (this.type != null)
                {
                    return false;
                }
                Dsymbol sa = this.aliassym.toAlias();
                {
                    FuncDeclaration fd = sa.isFuncDeclaration();
                    if ((fd) != null)
                    {
                        FuncAliasDeclaration fa = new FuncAliasDeclaration(this.ident, fd, true);
                        fa.protection.opAssign(this.protection.copy());
                        fa.parent.value = this.parent.value;
                        this.aliassym = fa;
                        return this.aliassym.overloadInsert(s);
                    }
                }
                {
                    TemplateDeclaration td = sa.isTemplateDeclaration();
                    if ((td) != null)
                    {
                        OverDeclaration od = new OverDeclaration(this.ident, td, true);
                        od.protection.opAssign(this.protection.copy());
                        od.parent.value = this.parent.value;
                        this.aliassym = od;
                        return this.aliassym.overloadInsert(s);
                    }
                }
                {
                    OverDeclaration od = sa.isOverDeclaration();
                    if ((od) != null)
                    {
                        if ((!pequals(sa.ident, this.ident)) || (!pequals(sa.parent.value, this.parent.value)))
                        {
                            od = new OverDeclaration(this.ident, od, true);
                            od.protection.opAssign(this.protection.copy());
                            od.parent.value = this.parent.value;
                            this.aliassym = od;
                        }
                        return od.overloadInsert(s);
                    }
                }
                {
                    OverloadSet os = sa.isOverloadSet();
                    if ((os) != null)
                    {
                        if ((!pequals(sa.ident, this.ident)) || (!pequals(sa.parent.value, this.parent.value)))
                        {
                            os = new OverloadSet(this.ident, os);
                            os.parent.value = this.parent.value;
                            this.aliassym = os;
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

        // Erasure: kind<>
        public  BytePtr kind() {
            return new BytePtr("alias");
        }

        // Erasure: getType<>
        public  Type getType() {
            if (this.type != null)
            {
                return this.type;
            }
            return this.toAlias().getType();
        }

        // Erasure: toAlias<>
        public  Dsymbol toAlias() {
            assert((!pequals(this, this.aliassym)));
            if ((this.inuse == 1) && (this.type != null) && (this._scope != null))
            {
                this.inuse = 2;
                int olderrors = global.errors;
                Dsymbol s = this.type.toDsymbol(this._scope);
                if ((global.errors != olderrors))
                {
                    /*goto Lerr*//*unrolled goto*/
                /*Lerr:*/
                    if (global.gag != 0)
                    {
                        return this;
                    }
                    this.aliassym = new AliasDeclaration(this.loc, this.ident, Type.terror);
                    this.type = Type.terror;
                    return this.aliassym;
                }
                if (s != null)
                {
                    s = s.toAlias();
                    if ((global.errors != olderrors))
                    {
                        /*goto Lerr*//*unrolled goto*/
                    /*Lerr:*/
                        if (global.gag != 0)
                        {
                            return this;
                        }
                        this.aliassym = new AliasDeclaration(this.loc, this.ident, Type.terror);
                        this.type = Type.terror;
                        return this.aliassym;
                    }
                    this.aliassym = s;
                    this.inuse = 0;
                }
                else
                {
                    Type t = typeSemantic(this.type, this.loc, this._scope);
                    if (((t.ty & 0xFF) == ENUMTY.Terror))
                    {
                        /*goto Lerr*//*unrolled goto*/
                    /*Lerr:*/
                        if (global.gag != 0)
                        {
                            return this;
                        }
                        this.aliassym = new AliasDeclaration(this.loc, this.ident, Type.terror);
                        this.type = Type.terror;
                        return this.aliassym;
                    }
                    if ((global.errors != olderrors))
                    {
                        /*goto Lerr*//*unrolled goto*/
                    /*Lerr:*/
                        if (global.gag != 0)
                        {
                            return this;
                        }
                        this.aliassym = new AliasDeclaration(this.loc, this.ident, Type.terror);
                        this.type = Type.terror;
                        return this.aliassym;
                    }
                    this.inuse = 0;
                }
            }
            if (this.inuse != 0)
            {
                this.error(new BytePtr("recursive alias declaration"));
            /*Lerr:*/
                if (global.gag != 0)
                {
                    return this;
                }
                this.aliassym = new AliasDeclaration(this.loc, this.ident, Type.terror);
                this.type = Type.terror;
                return this.aliassym;
            }
            if ((this.semanticRun >= PASS.semanticdone))
            {
            }
            else
            {
                if ((this._import != null) && (this._import._scope != null))
                {
                    dsymbolSemantic(this._import, null);
                }
                if (this._scope != null)
                {
                    aliasSemantic(this, this._scope);
                }
            }
            this.inuse = 1;
            Dsymbol s = this.aliassym != null ? this.aliassym.toAlias() : this;
            this.inuse = 0;
            return s;
        }

        // Erasure: toAlias2<>
        public  Dsymbol toAlias2() {
            if (this.inuse != 0)
            {
                this.error(new BytePtr("recursive alias declaration"));
                return this;
            }
            this.inuse = 1;
            Dsymbol s = this.aliassym != null ? this.aliassym.toAlias2() : this;
            this.inuse = 0;
            return s;
        }

        // Erasure: isOverloadable<>
        public  boolean isOverloadable() {
            return (this.semanticRun < PASS.semanticdone) || (this.aliassym != null) && this.aliassym.isOverloadable();
        }

        // Erasure: isAliasDeclaration<>
        public  AliasDeclaration isAliasDeclaration() {
            return this;
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Identifier, Dsymbol, boolean>
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

        // Erasure: kind<>
        public  BytePtr kind() {
            return new BytePtr("overload alias");
        }

        // Erasure: equals<RootObject>
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

        // Erasure: overloadInsert<Dsymbol>
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

        // Erasure: isOverloadable<>
        public  boolean isOverloadable() {
            return true;
        }

        // Erasure: isUnique<>
        public  Dsymbol isUnique() {
            OverDeclaration __self = this;
            if (!this.hasOverloads)
            {
                if ((this.aliassym.isFuncDeclaration() != null) || (this.aliassym.isTemplateDeclaration() != null))
                {
                    return this.aliassym;
                }
            }
            Dsymbol result = null;
            Function1<Dsymbol,Integer> __lambda1 = new Function1<Dsymbol,Integer>() {
                public Integer invoke(Dsymbol s) {
                 {
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
                }}

            };
            overloadApply(this.aliassym, __lambda1, null);
            return result;
        }

        // Erasure: isOverDeclaration<>
        public  OverDeclaration isOverDeclaration() {
            return this;
        }

        // Erasure: accept<Visitor>
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
        public Initializer _init = null;
        public int offset = 0;
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
        public boolean overlapped = false;
        public boolean overlapUnsafe = false;
        public boolean doNotInferScope = false;
        public byte isdataseg = 0;
        public Dsymbol aliassym = null;
        public VarDeclaration lastVar = null;
        public int endlinnum = 0;
        public int ctfeAdrOnStack = 0;
        public Expression edtor = null;
        public Ptr<IntRange> range = null;
        public DArray<VarDeclaration> maybes = null;
        public boolean _isAnonymous = false;
        // Erasure: __ctor<Loc, Type, Identifier, Initializer, long>
        public  VarDeclaration(Loc loc, Type type, Identifier ident, Initializer _init, long storage_class) {
            if ((ident == Identifier.anonymous()))
            {
                ident = Identifier.generateId(new BytePtr("__anonvar"));
                this._isAnonymous = true;
            }
            assert(ident != null);
            super(loc, ident);
            assert((type != null) || (_init != null));
            this.type = type;
            this._init = _init;
            this.ctfeAdrOnStack = -1;
            this.storage_class = storage_class;
            this.sequenceNumber = (nextSequenceNumber += 1);
        }

        // defaulted all parameters starting with #5
        public  VarDeclaration(Loc loc, Type type, Identifier ident, Initializer _init) {
            this(loc, type, ident, _init, 0L);
        }

        // Erasure: create<Loc, Type, Identifier, Initializer, long>
        public static VarDeclaration create(Loc loc, Type type, Identifier ident, Initializer _init, long storage_class) {
            return new VarDeclaration(loc, type, ident, _init, storage_class);
        }

        // defaulted all parameters starting with #5
        public static VarDeclaration create(Loc loc, Type type, Identifier ident, Initializer _init) {
            return create(loc, type, ident, _init, 0L);
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            VarDeclaration v = new VarDeclaration(this.loc, this.type != null ? this.type.syntaxCopy() : null, this.ident, this._init != null ? syntaxCopy(this._init) : null, this.storage_class);
            return v;
        }

        // Erasure: setFieldOffset<AggregateDeclaration, Ptr, boolean>
        public  void setFieldOffset(AggregateDeclaration ad, Ptr<Integer> poffset, boolean isunion) {
            if (this.aliassym != null)
            {
                TupleDeclaration v2 = this.aliassym.isTupleDeclaration();
                assert(v2 != null);
                {
                    int i = 0;
                    for (; (i < (v2.objects).length);i++){
                        RootObject o = (v2.objects).get(i);
                        assert((o.dyncast() == DYNCAST.expression));
                        Expression e = ((Expression)o);
                        assert(((e.op & 0xFF) == 41));
                        DsymbolExp se = ((DsymbolExp)e);
                        se.s.setFieldOffset(ad, poffset, isunion);
                    }
                }
                return ;
            }
            if (!this.isField())
            {
                return ;
            }
            assert((this.storage_class & 134217763L) == 0);
            if (this.offset != 0)
            {
                poffset.set(0, ad.structsize.value);
                return ;
            }
            {
                int i = 0;
                for (; (i < ad.fields.length);i++){
                    if ((pequals(ad.fields.get(i), this)))
                    {
                        poffset.set(0, ad.structsize.value);
                        return ;
                    }
                }
            }
            Type t = this.type.toBasetype();
            if ((this.storage_class & 2097152L) != 0)
            {
                t = Type.tvoidptr;
            }
            Type tv = t.baseElemOf();
            if (((tv.ty & 0xFF) == ENUMTY.Tstruct))
            {
                TypeStruct ts = ((TypeStruct)tv);
                assert((!pequals(ts.sym, ad)));
                if (!ts.sym.determineSize(this.loc))
                {
                    this.type = Type.terror;
                    this.errors = true;
                    return ;
                }
            }
            ad.fields.push(this);
            if (((t.ty & 0xFF) == ENUMTY.Terror))
            {
                return ;
            }
            long sz = t.size(this.loc);
            assert((sz != -1L) && (sz < 4294967295L));
            int memsize = (int)sz;
            int memalignsize = target.fieldalign(t);
            this.offset = AggregateDeclaration.placeField(poffset, memsize, memalignsize, this.alignment, ptr(ad.structsize), ptr(ad.alignsize), isunion);
        }

        // Erasure: kind<>
        public  BytePtr kind() {
            return new BytePtr("variable");
        }

        // Erasure: isThis<>
        public  AggregateDeclaration isThis() {
            if ((this.storage_class & 69936087043L) == 0)
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

        // Erasure: needThis<>
        public  boolean needThis() {
            return this.isField();
        }

        // Erasure: isAnonymous<>
        public  boolean isAnonymous() {
            return this._isAnonymous;
        }

        // Erasure: isExport<>
        public  boolean isExport() {
            return this.protection.kind == Prot.Kind.export_;
        }

        // Erasure: isImportedSymbol<>
        public  boolean isImportedSymbol() {
            if ((this.protection.kind == Prot.Kind.export_) && (this._init == null) && ((this.storage_class & 1L) != 0) || (this.parent.value.isModule() != null))
            {
                return true;
            }
            return false;
        }

        // Erasure: isDataseg<>
        public  boolean isDataseg() {
            if (((this.isdataseg & 0xFF) == 0))
            {
                this.isdataseg = (byte)2;
                if (!this.canTakeAddressOf())
                {
                    return false;
                }
                Dsymbol parent = this.toParent();
                if ((parent == null) && ((this.storage_class & 1L) == 0))
                {
                    this.error(new BytePtr("forward referenced"));
                    this.type = Type.terror;
                }
                else if (((this.storage_class & 1207959555L) != 0) || (parent.isModule() != null) || (parent.isTemplateInstance() != null) || (parent.isNspace() != null))
                {
                    assert(!this.isParameter() && !this.isResult());
                    this.isdataseg = (byte)1;
                }
            }
            return (this.isdataseg & 0xFF) == 1;
        }

        // Erasure: isThreadlocal<>
        public  boolean isThreadlocal() {
            boolean i = this.isDataseg() && ((this.storage_class & 1611661316L) == 0);
            return i;
        }

        // Erasure: isCTFE<>
        public  boolean isCTFE() {
            return (this.storage_class & 68719476736L) != 0L;
        }

        // Erasure: isOverlappedWith<VarDeclaration>
        public  boolean isOverlappedWith(VarDeclaration v) {
            long vsz = v.type.size();
            long tsz = this.type.size();
            assert((vsz != -1L) && (tsz != -1L));
            return ((long)this.offset < (long)v.offset + vsz) && ((long)v.offset < (long)this.offset + tsz);
        }

        // Erasure: hasPointers<>
        public  boolean hasPointers() {
            return !this.isDataseg() && this.type.hasPointers();
        }

        // Erasure: canTakeAddressOf<>
        public  boolean canTakeAddressOf() {
            return (this.storage_class & 8388608L) == 0;
        }

        // Erasure: needsScopeDtor<>
        public  boolean needsScopeDtor() {
            return (this.edtor != null) && ((this.storage_class & 16777216L) == 0);
        }

        // Erasure: callScopeDtor<Ptr>
        public  Expression callScopeDtor(Ptr<Scope> sc) {
            if ((this.storage_class & 18878528L) != 0)
            {
                return null;
            }
            if (this.iscatchvar)
            {
                return null;
            }
            Expression e = null;
            Type tv = this.type.baseElemOf();
            if (((tv.ty & 0xFF) == ENUMTY.Tstruct))
            {
                StructDeclaration sd = (((TypeStruct)tv)).sym;
                if ((sd.dtor == null) || sd.errors)
                {
                    return null;
                }
                long sz = this.type.size();
                assert((sz != -1L));
                if (sz == 0)
                {
                    return null;
                }
                if (((this.type.toBasetype().ty & 0xFF) == ENUMTY.Tstruct))
                {
                    e = new VarExp(this.loc, this, true);
                    e.type.value = e.type.value.mutableOf();
                    e.type.value = e.type.value.unSharedOf();
                    e = new DotVarExp(this.loc, e, sd.dtor, false);
                    e = new CallExp(this.loc, e);
                }
                else
                {
                    e = new VarExp(this.loc, this, true);
                    long sdsz = sd.type.size();
                    assert((sdsz != -1L) && (sdsz != 0L));
                    long n = sz / sdsz;
                    e = new SliceExp(this.loc, e, new IntegerExp(this.loc, 0L, Type.tsize_t), new IntegerExp(this.loc, n, Type.tsize_t));
                    (((SliceExp)e)).upperIsInBounds = true;
                    (((SliceExp)e)).lowerIsLessThanUpper = true;
                    e.type.value = sd.type.arrayOf();
                    e = new CallExp(this.loc, new IdentifierExp(this.loc, Id.__ArrayDtor), e);
                }
                return e;
            }
            if (((this.storage_class & 524544L) != 0) && ((this.storage_class & 32L) == 0))
            {
                {
                    ClassDeclaration cd = this.type.isClassHandle();
                    for (; cd != null;cd = cd.baseClass){
                        if ((cd.classKind == ClassKind.cpp))
                        {
                            break;
                        }
                        if (this.mynew || this.onstack)
                        {
                            Expression ec = null;
                            ec = new VarExp(this.loc, this, true);
                            e = new DeleteExp(this.loc, ec, true);
                            e.type.value = Type.tvoid;
                            break;
                        }
                    }
                }
            }
            return e;
        }

        // Erasure: getConstInitializer<boolean>
        public  Expression getConstInitializer(boolean needFullType) {
            assert((this.type != null) && (this._init != null));
            int oldgag = global.gag;
            if (global.gag != 0)
            {
                Dsymbol sym = this.toParent().isAggregateDeclaration();
                if ((sym != null) && (sym.isSpeculative() == null))
                {
                    global.gag = 0;
                }
            }
            if (this._scope != null)
            {
                this.inuse++;
                this._init = initializerSemantic(this._init, this._scope, this.type, NeedInterpret.INITinterpret);
                this._scope = null;
                this.inuse--;
            }
            Expression e = initializerToExpression(this._init, needFullType ? this.type : null);
            global.gag = oldgag;
            return e;
        }

        // defaulted all parameters starting with #1
        public  Expression getConstInitializer() {
            return getConstInitializer(true);
        }

        // Erasure: expandInitializer<Loc>
        public  Expression expandInitializer(Loc loc) {
            assert(((this.storage_class & 8388608L) != 0) && (this._init != null));
            Expression e = this.getConstInitializer(true);
            if (e == null)
            {
                error(loc, new BytePtr("cannot make expression out of initializer for `%s`"), this.toChars());
                return new ErrorExp();
            }
            e = e.copy();
            e.loc.opAssign(loc.copy());
            return e;
        }

        // Erasure: checkCtorConstInit<>
        public  void checkCtorConstInit() {
        }

        // Erasure: checkNestedReference<Ptr, Loc>
        public  boolean checkNestedReference(Ptr<Scope> sc, Loc loc) {
            if (((sc.get()).intypeof == 1) || (((sc.get()).flags & 128) != 0))
            {
                return false;
            }
            if ((this.parent.value == null) || (pequals(this.parent.value, (sc.get()).parent.value)))
            {
                return false;
            }
            if (this.isDataseg() || ((this.storage_class & 8388608L) != 0))
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
                    if ((i == this.nestedrefs.length))
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
            if ((pequals(fdthis.ident, Id.require)) || (pequals(fdthis.ident, Id.ensure)))
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
            if (((sc.get()).intypeof == 0) && (((sc.get()).flags & 256) == 0) && ((fdv.flags & FUNCFLAG.compileTimeOnly) != 0) || ((fdthis.flags & FUNCFLAG.compileTimeOnly) == 0))
            {
                {
                    int i = 0;
                    for (; 1 != 0;i++){
                        if ((i == fdv.closureVars.length))
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
            if ((pequals(this.ident, Id.dollar)))
            {
                error(loc, new BytePtr("cannnot use `$` inside a function literal"));
                return true;
            }
            if ((pequals(this.ident, Id.withSym)))
            {
                ExpInitializer ez = this._init.isExpInitializer();
                assert(ez != null);
                Expression e = ez.exp;
                if (((e.op & 0xFF) == 95) || ((e.op & 0xFF) == 96))
                {
                    e = (((AssignExp)e)).e2.value;
                }
                return lambdaCheckForNestedRef(e, sc);
            }
            return false;
        }

        // Erasure: toAlias<>
        public  Dsymbol toAlias() {
            if ((this.type == null) || (this.type.deco == null) && (this._scope != null))
            {
                dsymbolSemantic(this, this._scope);
            }
            assert((!pequals(this, this.aliassym)));
            Dsymbol s = this.aliassym != null ? this.aliassym.toAlias() : this;
            return s;
        }

        // Erasure: isVarDeclaration<>
        public  VarDeclaration isVarDeclaration() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }

        // Erasure: enclosesLifetimeOf<VarDeclaration>
        public  boolean enclosesLifetimeOf(VarDeclaration v) {
            return this.sequenceNumber < v.sequenceNumber;
        }

        // Erasure: addMaybe<VarDeclaration>
        public  void addMaybe(VarDeclaration v) {
            if (this.maybes == null)
            {
                this.maybes = pcopy(new DArray<VarDeclaration>());
            }
            (this.maybes).push(v);
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
        // Erasure: __ctor<Loc, StructDeclaration>
        public  SymbolDeclaration(Loc loc, StructDeclaration dsym) {
            super(loc, dsym.ident);
            this.dsym = dsym;
            this.storage_class |= 4L;
        }

        // Erasure: isSymbolDeclaration<>
        public  SymbolDeclaration isSymbolDeclaration() {
            return this;
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Type>
        public  TypeInfoDeclaration(Type tinfo) {
            super(Loc.initial, Type.dtypeinfo.type, tinfo.getTypeInfoIdent(), null, 0L);
            this.tinfo = tinfo;
            this.storage_class = 1073741825L;
            this.protection.opAssign(new Prot(Prot.Kind.public_).copy());
            this.linkage = LINK.c;
            this.alignment = target.ptrsize;
        }

        // Erasure: create<Type>
        public static TypeInfoDeclaration create(Type tinfo) {
            return new TypeInfoDeclaration(tinfo);
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: toChars<>
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

        // Erasure: isTypeInfoDeclaration<>
        public  TypeInfoDeclaration isTypeInfoDeclaration() {
            return this;
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Type>
        public  TypeInfoStructDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfostruct == null)
            {
                ObjectNotFound(Id.TypeInfo_Struct);
            }
            this.type = Type.typeinfostruct.type;
        }

        // Erasure: create<Type>
        public static TypeInfoStructDeclaration create(Type tinfo) {
            return new TypeInfoStructDeclaration(tinfo);
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Type>
        public  TypeInfoClassDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfoclass == null)
            {
                ObjectNotFound(Id.TypeInfo_Class);
            }
            this.type = Type.typeinfoclass.type;
        }

        // Erasure: create<Type>
        public static TypeInfoClassDeclaration create(Type tinfo) {
            return new TypeInfoClassDeclaration(tinfo);
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Type>
        public  TypeInfoInterfaceDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfointerface == null)
            {
                ObjectNotFound(Id.TypeInfo_Interface);
            }
            this.type = Type.typeinfointerface.type;
        }

        // Erasure: create<Type>
        public static TypeInfoInterfaceDeclaration create(Type tinfo) {
            return new TypeInfoInterfaceDeclaration(tinfo);
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Type>
        public  TypeInfoPointerDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfopointer == null)
            {
                ObjectNotFound(Id.TypeInfo_Pointer);
            }
            this.type = Type.typeinfopointer.type;
        }

        // Erasure: create<Type>
        public static TypeInfoPointerDeclaration create(Type tinfo) {
            return new TypeInfoPointerDeclaration(tinfo);
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Type>
        public  TypeInfoArrayDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfoarray == null)
            {
                ObjectNotFound(Id.TypeInfo_Array);
            }
            this.type = Type.typeinfoarray.type;
        }

        // Erasure: create<Type>
        public static TypeInfoArrayDeclaration create(Type tinfo) {
            return new TypeInfoArrayDeclaration(tinfo);
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Type>
        public  TypeInfoStaticArrayDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfostaticarray == null)
            {
                ObjectNotFound(Id.TypeInfo_StaticArray);
            }
            this.type = Type.typeinfostaticarray.type;
        }

        // Erasure: create<Type>
        public static TypeInfoStaticArrayDeclaration create(Type tinfo) {
            return new TypeInfoStaticArrayDeclaration(tinfo);
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Type>
        public  TypeInfoAssociativeArrayDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfoassociativearray == null)
            {
                ObjectNotFound(Id.TypeInfo_AssociativeArray);
            }
            this.type = Type.typeinfoassociativearray.type;
        }

        // Erasure: create<Type>
        public static TypeInfoAssociativeArrayDeclaration create(Type tinfo) {
            return new TypeInfoAssociativeArrayDeclaration(tinfo);
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Type>
        public  TypeInfoEnumDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfoenum == null)
            {
                ObjectNotFound(Id.TypeInfo_Enum);
            }
            this.type = Type.typeinfoenum.type;
        }

        // Erasure: create<Type>
        public static TypeInfoEnumDeclaration create(Type tinfo) {
            return new TypeInfoEnumDeclaration(tinfo);
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Type>
        public  TypeInfoFunctionDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfofunction == null)
            {
                ObjectNotFound(Id.TypeInfo_Function);
            }
            this.type = Type.typeinfofunction.type;
        }

        // Erasure: create<Type>
        public static TypeInfoFunctionDeclaration create(Type tinfo) {
            return new TypeInfoFunctionDeclaration(tinfo);
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Type>
        public  TypeInfoDelegateDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfodelegate == null)
            {
                ObjectNotFound(Id.TypeInfo_Delegate);
            }
            this.type = Type.typeinfodelegate.type;
        }

        // Erasure: create<Type>
        public static TypeInfoDelegateDeclaration create(Type tinfo) {
            return new TypeInfoDelegateDeclaration(tinfo);
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Type>
        public  TypeInfoTupleDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfotypelist == null)
            {
                ObjectNotFound(Id.TypeInfo_Tuple);
            }
            this.type = Type.typeinfotypelist.type;
        }

        // Erasure: create<Type>
        public static TypeInfoTupleDeclaration create(Type tinfo) {
            return new TypeInfoTupleDeclaration(tinfo);
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Type>
        public  TypeInfoConstDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfoconst == null)
            {
                ObjectNotFound(Id.TypeInfo_Const);
            }
            this.type = Type.typeinfoconst.type;
        }

        // Erasure: create<Type>
        public static TypeInfoConstDeclaration create(Type tinfo) {
            return new TypeInfoConstDeclaration(tinfo);
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Type>
        public  TypeInfoInvariantDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfoinvariant == null)
            {
                ObjectNotFound(Id.TypeInfo_Invariant);
            }
            this.type = Type.typeinfoinvariant.type;
        }

        // Erasure: create<Type>
        public static TypeInfoInvariantDeclaration create(Type tinfo) {
            return new TypeInfoInvariantDeclaration(tinfo);
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Type>
        public  TypeInfoSharedDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfoshared == null)
            {
                ObjectNotFound(Id.TypeInfo_Shared);
            }
            this.type = Type.typeinfoshared.type;
        }

        // Erasure: create<Type>
        public static TypeInfoSharedDeclaration create(Type tinfo) {
            return new TypeInfoSharedDeclaration(tinfo);
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Type>
        public  TypeInfoWildDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfowild == null)
            {
                ObjectNotFound(Id.TypeInfo_Wild);
            }
            this.type = Type.typeinfowild.type;
        }

        // Erasure: create<Type>
        public static TypeInfoWildDeclaration create(Type tinfo) {
            return new TypeInfoWildDeclaration(tinfo);
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Type>
        public  TypeInfoVectorDeclaration(Type tinfo) {
            super(tinfo);
            if (Type.typeinfovector == null)
            {
                ObjectNotFound(Id.TypeInfo_Vector);
            }
            this.type = Type.typeinfovector.type;
        }

        // Erasure: create<Type>
        public static TypeInfoVectorDeclaration create(Type tinfo) {
            return new TypeInfoVectorDeclaration(tinfo);
        }

        // Erasure: accept<Visitor>
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
        // Erasure: __ctor<Loc, Type>
        public  ThisDeclaration(Loc loc, Type t) {
            super(loc, t, Id.This, null, 0L);
            this.storage_class |= 16777216L;
        }

        // Erasure: syntaxCopy<Dsymbol>
        public  Dsymbol syntaxCopy(Dsymbol s) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: isThisDeclaration<>
        public  ThisDeclaration isThisDeclaration() {
            return this;
        }

        // Erasure: accept<Visitor>
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
