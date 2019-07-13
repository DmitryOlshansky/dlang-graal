package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.gluelayer.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.visitor.*;

public class aggregate {
    private static class SearchCtor
    {
        public static int fp(Dsymbol s, Object ctxt) {
            Ref<CtorDeclaration> f = ref(s.isCtorDeclaration());
            if ((f.value != null) && (f.value.semanticRun.value == PASS.init))
            {
                dsymbolSemantic(f.value, null);
            }
            return 0;
        }

        public SearchCtor(){
        }
        public SearchCtor copy(){
            SearchCtor r = new SearchCtor();
            return r;
        }
        public SearchCtor opAssign(SearchCtor that) {
            return this;
        }
    }


    public static class Sizeok 
    {
        public static final int none = 0;
        public static final int fwd = 1;
        public static final int inProcess = 2;
        public static final int done = 3;
    }


    public static class Baseok 
    {
        public static final int none = 0;
        public static final int start = 1;
        public static final int done = 2;
        public static final int semanticdone = 3;
    }


    public static class ClassKind 
    {
        public static final int d = 0;
        public static final int cpp = 1;
        public static final int objc = 2;
    }

    public static abstract class AggregateDeclaration extends ScopeDsymbol
    {
        public Ref<Type> type = ref(null);
        public long storage_class = 0L;
        public Prot protection = new Prot();
        public IntRef structsize = ref(0);
        public IntRef alignsize = ref(0);
        public DArray<VarDeclaration> fields = new DArray<VarDeclaration>();
        public IntRef sizeok = ref(Sizeok.none);
        public Dsymbol deferred = null;
        public boolean isdeprecated = false;
        public IntRef classKind = ref(0);
        public Dsymbol enclosing = null;
        public Ref<VarDeclaration> vthis = ref(null);
        public Ref<VarDeclaration> vthis2 = ref(null);
        public DArray<FuncDeclaration> invs = new DArray<FuncDeclaration>();
        public FuncDeclaration inv = null;
        public NewDeclaration aggNew = null;
        public Ref<DeleteDeclaration> aggDelete = ref(null);
        public Ref<Dsymbol> ctor = ref(null);
        public CtorDeclaration defaultCtor = null;
        public Ref<Dsymbol> aliasthis = ref(null);
        public Ref<Boolean> noDefaultCtor = ref(false);
        public DArray<DtorDeclaration> dtors = new DArray<DtorDeclaration>();
        public Ref<DtorDeclaration> dtor = ref(null);
        public DtorDeclaration primaryDtor = null;
        public DtorDeclaration tidtor = null;
        public FuncDeclaration fieldDtor = null;
        public Expression getRTInfo = null;
        public  AggregateDeclaration(Loc loc, Identifier id) {
            super(loc, id);
            this.protection = new Prot(Prot.Kind.public_);
        }

        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            Ptr<Scope> sc2 = (sc.get()).push(this);
            (sc2.get()).stc.value &= 60129542144L;
            (sc2.get()).parent.value = this;
            (sc2.get()).inunion = this.isUnionDeclaration();
            (sc2.get()).protection.value = new Prot(Prot.Kind.public_).copy();
            (sc2.get()).explicitProtection = 0;
            (sc2.get()).aligndecl = null;
            (sc2.get()).userAttribDecl = null;
            (sc2.get()).namespace = null;
            return sc2;
        }

        public  void setScope(Ptr<Scope> sc) {
            if ((this.semanticRun.value < PASS.semanticdone))
            {
                this.setScope(sc);
            }
        }

        public  boolean determineFields() {
            if (this._scope.value != null)
            {
                dsymbolSemantic(this, null);
            }
            if ((this.sizeok.value != Sizeok.none))
            {
                return true;
            }
            this.fields.setDim(0);
            Function2<Dsymbol,Object,Integer> func = new Function2<Dsymbol,Object,Integer>(){
                public Integer invoke(Dsymbol s, Object param) {
                    Ref<Object> param_ref = ref(param);
                    Ref<VarDeclaration> v = ref(s.isVarDeclaration());
                    if (v.value == null)
                    {
                        return 0;
                    }
                    if ((v.value.storage_class.value & 8388608L) != 0)
                    {
                        return 0;
                    }
                    Ref<AggregateDeclaration> ad = ref(((AggregateDeclaration)param_ref.value));
                    if ((v.value.semanticRun.value < PASS.semanticdone))
                    {
                        dsymbolSemantic(v.value, null);
                    }
                    if ((ad.value.sizeok.value != Sizeok.none))
                    {
                        return 1;
                    }
                    if (v.value.aliassym.value != null)
                    {
                        return 0;
                    }
                    if ((v.value.storage_class.value & 69936087043L) != 0)
                    {
                        return 0;
                    }
                    if (!v.value.isField() || (v.value.semanticRun.value < PASS.semanticdone))
                    {
                        return 1;
                    }
                    ad.value.fields.push(v.value);
                    if ((v.value.storage_class.value & 2097152L) != 0)
                    {
                        return 0;
                    }
                    Type tv = v.value.type.value.baseElemOf();
                    if (((tv.ty.value & 0xFF) != ENUMTY.Tstruct))
                    {
                        return 0;
                    }
                    if ((pequals(ad.value, ((TypeStruct)tv).sym.value)))
                    {
                        Ref<BytePtr> psz = ref(pcopy(((v.value.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray) ? new BytePtr("static array of ") : new BytePtr("")));
                        ad.value.error(new BytePtr("cannot have field `%s` with %ssame struct type"), v.value.toChars(), psz.value);
                        ad.value.type.value = Type.terror.value;
                        ad.value.errors.value = true;
                        return 1;
                    }
                    return 0;
                }
            };
            {
                int i = 0;
                for (; (i < (this.members.value.get()).length.value);i++){
                    Dsymbol s = (this.members.value.get()).get(i);
                    if (s.apply(func, this) != 0)
                    {
                        if ((this.sizeok.value != Sizeok.none))
                        {
                            return true;
                        }
                        return false;
                    }
                }
            }
            if ((this.sizeok.value != Sizeok.done))
            {
                this.sizeok.value = Sizeok.fwd;
            }
            return true;
        }

        public  int nonHiddenFields() {
            return this.fields.length.value - (this.isNested() ? 1 : 0) - ((this.vthis2.value != null) ? 1 : 0);
        }

        public  boolean determineSize(Loc loc) {
            if (((this.type.value.ty.value & 0xFF) == ENUMTY.Terror))
            {
                return false;
            }
            if ((this.sizeok.value == Sizeok.done))
            {
                return true;
            }
            if (this.members.value == null)
            {
                this.error(loc, new BytePtr("unknown size"));
                return false;
            }
            if (this._scope.value != null)
            {
                dsymbolSemantic(this, null);
            }
            try {
                {
                    ClassDeclaration cd = this.isClassDeclaration();
                    if ((cd) != null)
                    {
                        cd = cd.baseClass.value;
                        if ((cd != null) && !cd.determineSize(loc))
                        {
                            /*goto Lfail*/throw Dispatch0.INSTANCE;
                        }
                    }
                }
                if (!this.determineFields())
                {
                    /*goto Lfail*/throw Dispatch0.INSTANCE;
                }
                if ((this.sizeok.value != Sizeok.done))
                {
                    this.finalizeSize();
                }
                if (((this.type.value.ty.value & 0xFF) == ENUMTY.Terror))
                {
                    return false;
                }
                if ((this.sizeok.value == Sizeok.done))
                {
                    return true;
                }
            }
            catch(Dispatch0 __d){}
        /*Lfail:*/
            if ((!pequals(this.type.value, Type.terror.value)))
            {
                this.error(loc, new BytePtr("no size because of forward reference"));
            }
            if (global.gag.value == 0)
            {
                this.type.value = Type.terror.value;
                this.errors.value = true;
            }
            return false;
        }

        public abstract void finalizeSize();


        public  long size(Loc loc) {
            boolean ok = this.determineSize(loc);
            return ok ? (long)this.structsize.value : -1L;
        }

        public  boolean checkOverlappedFields() {
            assert((this.sizeok.value == Sizeok.done));
            int nfields = this.fields.length.value;
            if (this.isNested())
            {
                ClassDeclaration cd = this.isClassDeclaration();
                if ((cd == null) || (cd.baseClass.value == null) || !cd.baseClass.value.isNested())
                {
                    nfields--;
                }
                if ((this.vthis2.value != null) && !((cd != null) && (cd.baseClass.value != null) && (cd.baseClass.value.vthis2.value != null)))
                {
                    nfields--;
                }
            }
            boolean errors = false;
            {
                int __key701 = 0;
                int __limit702 = nfields;
                for (; (__key701 < __limit702);__key701 += 1) {
                    int i = __key701;
                    VarDeclaration vd = this.fields.get(i);
                    if (vd.errors.value)
                    {
                        errors = true;
                        continue;
                    }
                    VarDeclaration vx = vd;
                    if ((vd._init.value != null) && (vd._init.value.isVoidInitializer() != null))
                    {
                        vx = null;
                    }
                    {
                        int __key703 = 0;
                        int __limit704 = nfields;
                        for (; (__key703 < __limit704);__key703 += 1) {
                            int j = __key703;
                            if ((i == j))
                            {
                                continue;
                            }
                            VarDeclaration v2 = this.fields.get(j);
                            if (v2.errors.value)
                            {
                                errors = true;
                                continue;
                            }
                            if (!vd.isOverlappedWith(v2))
                            {
                                continue;
                            }
                            vd.overlapped.value = true;
                            v2.overlapped.value = true;
                            if (!MODimplicitConv(vd.type.value.mod.value, v2.type.value.mod.value))
                            {
                                v2.overlapUnsafe = true;
                            }
                            if (!MODimplicitConv(v2.type.value.mod.value, vd.type.value.mod.value))
                            {
                                vd.overlapUnsafe = true;
                            }
                            if (vx == null)
                            {
                                continue;
                            }
                            if ((v2._init.value != null) && (v2._init.value.isVoidInitializer() != null))
                            {
                                continue;
                            }
                            if ((vx._init.value != null) && (v2._init.value != null))
                            {
                                error(this.loc.value, new BytePtr("overlapping default initialization for field `%s` and `%s`"), v2.toChars(), vd.toChars());
                                errors = true;
                            }
                        }
                    }
                }
            }
            return errors;
        }

        public  boolean fill(Loc loc, Ptr<DArray<Expression>> elements, boolean ctorinit) {
            assert((this.sizeok.value == Sizeok.done));
            assert(elements != null);
            int nfields = this.nonHiddenFields();
            boolean errors = false;
            int dim = (elements.get()).length.value;
            (elements.get()).setDim(nfields);
            {
                int __key705 = dim;
                int __limit706 = nfields;
                for (; (__key705 < __limit706);__key705 += 1) {
                    int i = __key705;
                    elements.get().set(i, null);
                }
            }
            {
                int __key707 = 0;
                int __limit708 = nfields;
                for (; (__key707 < __limit708);__key707 += 1) {
                    int i = __key707;
                    if ((elements.get()).get(i) != null)
                    {
                        continue;
                    }
                    VarDeclaration vd = this.fields.get(i);
                    VarDeclaration vx = vd;
                    if ((vd._init.value != null) && (vd._init.value.isVoidInitializer() != null))
                    {
                        vx = null;
                    }
                    int fieldi = i;
                    {
                        int __key709 = 0;
                        int __limit710 = nfields;
                        for (; (__key709 < __limit710);__key709 += 1) {
                            int j = __key709;
                            if ((i == j))
                            {
                                continue;
                            }
                            VarDeclaration v2 = this.fields.get(j);
                            if (!vd.isOverlappedWith(v2))
                            {
                                continue;
                            }
                            if ((elements.get()).get(j) != null)
                            {
                                vx = null;
                                break;
                            }
                            if ((v2._init.value != null) && (v2._init.value.isVoidInitializer() != null))
                            {
                                continue;
                            }
                            if (vx == null)
                            {
                                vx = v2;
                                fieldi = j;
                            }
                            else if (v2._init.value != null)
                            {
                                error(loc, new BytePtr("overlapping initialization for field `%s` and `%s`"), v2.toChars(), vd.toChars());
                                errors = true;
                            }
                        }
                    }
                    if (vx != null)
                    {
                        Expression e = null;
                        if ((vx.type.value.size() == 0L))
                        {
                            e = null;
                        }
                        else if (vx._init.value != null)
                        {
                            assert(vx._init.value.isVoidInitializer() == null);
                            if (vx.inuse.value != 0)
                            {
                                vx.error(loc, new BytePtr("recursive initialization of field"));
                                errors = true;
                            }
                            else
                            {
                                e = vx.getConstInitializer(false);
                            }
                        }
                        else
                        {
                            if (((vx.storage_class.value & 549755813888L) != 0) && !ctorinit)
                            {
                                error(loc, new BytePtr("field `%s.%s` must be initialized because it has no default constructor"), this.type.value.toChars(), vx.toChars());
                                errors = true;
                            }
                            Type telem = vx.type.value;
                            if (((telem.ty.value & 0xFF) == ENUMTY.Tsarray))
                            {
                                for (; ((telem.toBasetype().ty.value & 0xFF) == ENUMTY.Tsarray);) {
                                    telem = ((TypeSArray)telem.toBasetype()).next.value;
                                }
                                if (((telem.ty.value & 0xFF) == ENUMTY.Tvoid))
                                {
                                    telem = Type.tuns8.value.addMod(telem.mod.value);
                                }
                            }
                            if (telem.needsNested() && ctorinit)
                            {
                                e = defaultInit(telem, loc);
                            }
                            else
                            {
                                e = telem.defaultInitLiteral(loc);
                            }
                        }
                        elements.get().set(fieldi, e);
                    }
                }
            }
            {
                Slice<Expression> __r711 = (elements.get()).opSlice().copy();
                int __key712 = 0;
                for (; (__key712 < __r711.getLength());__key712 += 1) {
                    Expression e = __r711.get(__key712);
                    if ((e != null) && ((e.op.value & 0xFF) == 127))
                    {
                        return false;
                    }
                }
            }
            return !errors;
        }

        public static void alignmember(int alignment, int size, IntPtr poffset) {
            switch (alignment)
            {
                case 1:
                    break;
                case -1:
                    assert((size > 0) && ((size & size - 1) == 0));
                    poffset.set(0, (poffset.get() + size - 1 & ~(size - 1)));
                    break;
                default:
                assert((alignment > 0) && ((alignment & alignment - 1) == 0));
                poffset.set(0, (poffset.get() + alignment - 1 & ~(alignment - 1)));
                break;
            }
        }

        public static int placeField(IntPtr nextoffset, int memsize, int memalignsize, int alignment, IntPtr paggsize, IntPtr paggalignsize, boolean isunion) {
            IntRef ofs = ref(nextoffset.get());
            int actualAlignment = (alignment == -1) ? memalignsize : alignment;
            Ref<Boolean> overflow = ref(false);
            int sz = addu(memsize, actualAlignment, overflow);
            int sum = addu(ofs.value, sz, overflow);
            if (overflow.value)
            {
                throw new AssertionError("Unreachable code!");
            }
            alignmember(alignment, memalignsize, ptr(ofs));
            int memoffset = ofs.value;
            ofs.value += memsize;
            if ((ofs.value > paggsize.get()))
            {
                paggsize.set(0, ofs.value);
            }
            if (!isunion)
            {
                nextoffset.set(0, ofs.value);
            }
            if ((paggalignsize.get() < actualAlignment))
            {
                paggalignsize.set(0, actualAlignment);
            }
            return memoffset;
        }

        public  Type getType() {
            return this.type.value;
        }

        public  boolean isDeprecated() {
            return this.isdeprecated;
        }

        public  boolean isNested() {
            return this.enclosing != null;
        }

        public  void makeNested() {
            if (this.enclosing != null)
            {
                return ;
            }
            if ((this.sizeok.value == Sizeok.done))
            {
                return ;
            }
            if ((this.isUnionDeclaration() != null) || (this.isInterfaceDeclaration() != null))
            {
                return ;
            }
            if ((this.storage_class & 1L) != 0)
            {
                return ;
            }
            Dsymbol s = this.toParentLocal();
            if (s == null)
            {
                s = this.toParent2();
            }
            if (s == null)
            {
                return ;
            }
            Type t = null;
            {
                FuncDeclaration fd = s.isFuncDeclaration();
                if ((fd) != null)
                {
                    this.enclosing = fd;
                    t = Type.tvoidptr.value;
                }
                else {
                    AggregateDeclaration ad = s.isAggregateDeclaration();
                    if ((ad) != null)
                    {
                        if ((this.isClassDeclaration() != null) && (ad.isClassDeclaration() != null))
                        {
                            this.enclosing = ad;
                        }
                        else if (this.isStructDeclaration() != null)
                        {
                            {
                                TemplateInstance ti = ad.parent.value.isTemplateInstance();
                                if ((ti) != null)
                                {
                                    this.enclosing = ti.enclosing.value;
                                }
                            }
                        }
                        t = ad.handleType();
                    }
                }
            }
            if (this.enclosing != null)
            {
                assert(t != null);
                if (((t.ty.value & 0xFF) == ENUMTY.Tstruct))
                {
                    t = Type.tvoidptr.value;
                }
                assert(this.vthis.value == null);
                this.vthis.value = new ThisDeclaration(this.loc.value, t);
                (this.members.value.get()).push(this.vthis.value);
                this.vthis.value.storage_class.value |= 64L;
                this.vthis.value.parent.value = this;
                this.vthis.value.protection = new Prot(Prot.Kind.public_).copy();
                this.vthis.value.alignment = t.alignment();
                this.vthis.value.semanticRun.value = PASS.semanticdone;
                if ((this.sizeok.value == Sizeok.fwd))
                {
                    this.fields.push(this.vthis.value);
                }
                this.makeNested2();
            }
        }

        public  void makeNested2() {
            if (this.vthis2.value != null)
            {
                return ;
            }
            if (this.vthis.value == null)
            {
                this.makeNested();
            }
            if (this.vthis.value == null)
            {
                return ;
            }
            if ((this.sizeok.value == Sizeok.done))
            {
                return ;
            }
            if ((this.isUnionDeclaration() != null) || (this.isInterfaceDeclaration() != null))
            {
                return ;
            }
            if ((this.storage_class & 1L) != 0)
            {
                return ;
            }
            Dsymbol s0 = this.toParentLocal();
            Dsymbol s = this.toParent2();
            if ((s == null) || (s0 == null) || (pequals(s, s0)))
            {
                return ;
            }
            ClassDeclaration cd = s.isClassDeclaration();
            Type t = cd != null ? cd.type.value : Type.tvoidptr.value;
            this.vthis2.value = new ThisDeclaration(this.loc.value, t);
            (this.members.value.get()).push(this.vthis2.value);
            this.vthis2.value.storage_class.value |= 64L;
            this.vthis2.value.parent.value = this;
            this.vthis2.value.protection = new Prot(Prot.Kind.public_).copy();
            this.vthis2.value.alignment = t.alignment();
            this.vthis2.value.semanticRun.value = PASS.semanticdone;
            if ((this.sizeok.value == Sizeok.fwd))
            {
                this.fields.push(this.vthis2.value);
            }
        }

        public  boolean isExport() {
            return this.protection.kind.value == Prot.Kind.export_;
        }

        public  Dsymbol searchCtor() {
            Dsymbol s = this.search(Loc.initial.value, Id.ctor.value, 8);
            if (s != null)
            {
                if (!((s.isCtorDeclaration() != null) || (s.isTemplateDeclaration() != null) || (s.isOverloadSet() != null)))
                {
                    s.error(new BytePtr("is not a constructor; identifiers starting with `__` are reserved for the implementation"));
                    this.errors.value = true;
                    s = null;
                }
            }
            if ((s != null) && (!pequals(s.toParent(), this)))
            {
                s = null;
            }
            if (s != null)
            {
                {
                    int i = 0;
                    for (; (i < (this.members.value.get()).length.value);i++){
                        Dsymbol sm = (this.members.value.get()).get(i);
                        sm.apply(SearchCtor::fp, null);
                    }
                }
            }
            return s;
        }

        public  Prot prot() {
            return this.protection;
        }

        public  Type handleType() {
            return this.type.value;
        }

        public Ptr<Symbol> stag = null;
        public Ptr<Symbol> sinit = null;
        public  AggregateDeclaration isAggregateDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AggregateDeclaration() {}

        public abstract AggregateDeclaration copy();
    }
}
