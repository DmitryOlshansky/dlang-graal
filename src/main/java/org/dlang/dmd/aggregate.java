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
            Ref<Dsymbol> s_ref = ref(s);
            Ref<CtorDeclaration> f = ref(s_ref.value.isCtorDeclaration());
            if ((f.value != null) && (f.value.semanticRun == PASS.init))
                dsymbolSemantic(f.value, null);
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
        public Type type = null;
        public long storage_class = 0;
        public Prot protection = new Prot();
        public IntRef structsize = ref(0);
        public IntRef alignsize = ref(0);
        public DArray<VarDeclaration> fields = new DArray<VarDeclaration>();
        public int sizeok = Sizeok.none;
        public Dsymbol deferred = null;
        public boolean isdeprecated = false;
        public int classKind = 0;
        public Dsymbol enclosing = null;
        public VarDeclaration vthis = null;
        public VarDeclaration vthis2 = null;
        public DArray<FuncDeclaration> invs = new DArray<FuncDeclaration>();
        public FuncDeclaration inv = null;
        public NewDeclaration aggNew = null;
        public DeleteDeclaration aggDelete = null;
        public Dsymbol ctor = null;
        public CtorDeclaration defaultCtor = null;
        public Dsymbol aliasthis = null;
        public boolean noDefaultCtor = false;
        public DArray<DtorDeclaration> dtors = new DArray<DtorDeclaration>();
        public DtorDeclaration dtor = null;
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
            (sc2.get()).stc &= 60129542144L;
            (sc2.get()).parent.value = this;
            (sc2.get()).inunion = this.isUnionDeclaration();
            (sc2.get()).protection = new Prot(Prot.Kind.public_).copy();
            (sc2.get()).explicitProtection = 0;
            (sc2.get()).aligndecl = null;
            (sc2.get()).userAttribDecl = null;
            (sc2.get()).namespace = null;
            return sc2;
        }

        public  void setScope(Ptr<Scope> sc) {
            if ((this.semanticRun < PASS.semanticdone))
                this.setScope(sc);
        }

        public  boolean determineFields() {
            if (this._scope != null)
                dsymbolSemantic(this, null);
            if ((this.sizeok != Sizeok.none))
                return true;
            this.fields.setDim(0);
            Function2<Dsymbol,Object,Integer> func = new Function2<Dsymbol,Object,Integer>(){
                public Integer invoke(Dsymbol s, Object param) {
                    Ref<Dsymbol> s_ref = ref(s);
                    Ref<Object> param_ref = ref(param);
                    Ref<VarDeclaration> v = ref(s_ref.value.isVarDeclaration());
                    if (v.value == null)
                        return 0;
                    if ((v.value.storage_class & 8388608L) != 0)
                        return 0;
                    Ref<AggregateDeclaration> ad = ref(((AggregateDeclaration)param_ref.value));
                    if ((v.value.semanticRun < PASS.semanticdone))
                        dsymbolSemantic(v.value, null);
                    if ((ad.value.sizeok != Sizeok.none))
                        return 1;
                    if (v.value.aliassym != null)
                        return 0;
                    if ((v.value.storage_class & 69936087043L) != 0)
                        return 0;
                    if (!v.value.isField() || (v.value.semanticRun < PASS.semanticdone))
                        return 1;
                    ad.value.fields.push(v.value);
                    if ((v.value.storage_class & 2097152L) != 0)
                        return 0;
                    Ref<Type> tv = ref(v.value.type.baseElemOf());
                    if (((tv.value.ty & 0xFF) != ENUMTY.Tstruct))
                        return 0;
                    if ((pequals(ad.value, ((TypeStruct)tv.value).sym)))
                    {
                        Ref<BytePtr> psz = ref(pcopy(((v.value.type.toBasetype().ty & 0xFF) == ENUMTY.Tsarray) ? new BytePtr("static array of ") : new BytePtr("")));
                        ad.value.error(new BytePtr("cannot have field `%s` with %ssame struct type"), v.value.toChars(), psz.value);
                        ad.value.type = Type.terror.value;
                        ad.value.errors = true;
                        return 1;
                    }
                    return 0;
                }
            };
            {
                int i = 0;
                for (; (i < (this.members.get()).length);i++){
                    Dsymbol s = (this.members.get()).get(i);
                    if (s.apply(func, this) != 0)
                    {
                        if ((this.sizeok != Sizeok.none))
                        {
                            return true;
                        }
                        return false;
                    }
                }
            }
            if ((this.sizeok != Sizeok.done))
                this.sizeok = Sizeok.fwd;
            return true;
        }

        public  int nonHiddenFields() {
            return this.fields.length - (this.isNested() ? 1 : 0) - ((this.vthis2 != null) ? 1 : 0);
        }

        public  boolean determineSize(Loc loc) {
            if (((this.type.ty & 0xFF) == ENUMTY.Terror))
                return false;
            if ((this.sizeok == Sizeok.done))
                return true;
            if (this.members == null)
            {
                this.error(loc, new BytePtr("unknown size"));
                return false;
            }
            if (this._scope != null)
                dsymbolSemantic(this, null);
            try {
                {
                    ClassDeclaration cd = this.isClassDeclaration();
                    if ((cd) != null)
                    {
                        cd = cd.baseClass;
                        if ((cd != null) && !cd.determineSize(loc))
                            /*goto Lfail*/throw Dispatch0.INSTANCE;
                    }
                }
                if (!this.determineFields())
                    /*goto Lfail*/throw Dispatch0.INSTANCE;
                if ((this.sizeok != Sizeok.done))
                    this.finalizeSize();
                if (((this.type.ty & 0xFF) == ENUMTY.Terror))
                    return false;
                if ((this.sizeok == Sizeok.done))
                    return true;
            }
            catch(Dispatch0 __d){}
        /*Lfail:*/
            if ((!pequals(this.type, Type.terror.value)))
                this.error(loc, new BytePtr("no size because of forward reference"));
            if (global.value.gag == 0)
            {
                this.type = Type.terror.value;
                this.errors = true;
            }
            return false;
        }

        public abstract void finalizeSize();


        public  long size(Loc loc) {
            boolean ok = this.determineSize(loc);
            return ok ? (long)this.structsize.value : -1L;
        }

        public  boolean checkOverlappedFields() {
            assert((this.sizeok == Sizeok.done));
            int nfields = this.fields.length;
            if (this.isNested())
            {
                ClassDeclaration cd = this.isClassDeclaration();
                if ((cd == null) || (cd.baseClass == null) || !cd.baseClass.isNested())
                    nfields--;
                if ((this.vthis2 != null) && !((cd != null) && (cd.baseClass != null) && (cd.baseClass.vthis2 != null)))
                    nfields--;
            }
            boolean errors = false;
            {
                int __key701 = 0;
                int __limit702 = nfields;
                for (; (__key701 < __limit702);__key701 += 1) {
                    int i = __key701;
                    VarDeclaration vd = this.fields.get(i);
                    if (vd.errors)
                    {
                        errors = true;
                        continue;
                    }
                    VarDeclaration vx = vd;
                    if ((vd._init != null) && (vd._init.isVoidInitializer() != null))
                        vx = null;
                    {
                        int __key703 = 0;
                        int __limit704 = nfields;
                        for (; (__key703 < __limit704);__key703 += 1) {
                            int j = __key703;
                            if ((i == j))
                                continue;
                            VarDeclaration v2 = this.fields.get(j);
                            if (v2.errors)
                            {
                                errors = true;
                                continue;
                            }
                            if (!vd.isOverlappedWith(v2))
                                continue;
                            vd.overlapped = true;
                            v2.overlapped = true;
                            if (!MODimplicitConv(vd.type.mod, v2.type.mod))
                                v2.overlapUnsafe = true;
                            if (!MODimplicitConv(v2.type.mod, vd.type.mod))
                                vd.overlapUnsafe = true;
                            if (vx == null)
                                continue;
                            if ((v2._init != null) && (v2._init.isVoidInitializer() != null))
                                continue;
                            if ((vx._init != null) && (v2._init != null))
                            {
                                error(this.loc, new BytePtr("overlapping default initialization for field `%s` and `%s`"), v2.toChars(), vd.toChars());
                                errors = true;
                            }
                        }
                    }
                }
            }
            return errors;
        }

        public  boolean fill(Loc loc, Ptr<DArray<Expression>> elements, boolean ctorinit) {
            assert((this.sizeok == Sizeok.done));
            assert(elements != null);
            int nfields = this.nonHiddenFields();
            boolean errors = false;
            int dim = (elements.get()).length;
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
                        continue;
                    VarDeclaration vd = this.fields.get(i);
                    VarDeclaration vx = vd;
                    if ((vd._init != null) && (vd._init.isVoidInitializer() != null))
                        vx = null;
                    int fieldi = i;
                    {
                        int __key709 = 0;
                        int __limit710 = nfields;
                        for (; (__key709 < __limit710);__key709 += 1) {
                            int j = __key709;
                            if ((i == j))
                                continue;
                            VarDeclaration v2 = this.fields.get(j);
                            if (!vd.isOverlappedWith(v2))
                                continue;
                            if ((elements.get()).get(j) != null)
                            {
                                vx = null;
                                break;
                            }
                            if ((v2._init != null) && (v2._init.isVoidInitializer() != null))
                                continue;
                            if (vx == null)
                            {
                                vx = v2;
                                fieldi = j;
                            }
                            else if (v2._init != null)
                            {
                                error(loc, new BytePtr("overlapping initialization for field `%s` and `%s`"), v2.toChars(), vd.toChars());
                                errors = true;
                            }
                        }
                    }
                    if (vx != null)
                    {
                        Expression e = null;
                        if ((vx.type.size() == 0L))
                        {
                            e = null;
                        }
                        else if (vx._init != null)
                        {
                            assert(vx._init.isVoidInitializer() == null);
                            if (vx.inuse != 0)
                            {
                                vx.error(loc, new BytePtr("recursive initialization of field"));
                                errors = true;
                            }
                            else
                                e = vx.getConstInitializer(false);
                        }
                        else
                        {
                            if (((vx.storage_class & 549755813888L) != 0) && !ctorinit)
                            {
                                error(loc, new BytePtr("field `%s.%s` must be initialized because it has no default constructor"), this.type.toChars(), vx.toChars());
                                errors = true;
                            }
                            Type telem = vx.type;
                            if (((telem.ty & 0xFF) == ENUMTY.Tsarray))
                            {
                                for (; ((telem.toBasetype().ty & 0xFF) == ENUMTY.Tsarray);) {
                                    telem = ((TypeSArray)telem.toBasetype()).next;
                                }
                                if (((telem.ty & 0xFF) == ENUMTY.Tvoid))
                                    telem = Type.tuns8.value.addMod(telem.mod);
                            }
                            if (telem.needsNested() && ctorinit)
                                e = defaultInit(telem, loc);
                            else
                                e = telem.defaultInitLiteral(loc);
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
                    if ((e != null) && ((e.op & 0xFF) == 127))
                        return false;
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
                throw new AssertionError("Unreachable code!");
            alignmember(alignment, memalignsize, ptr(ofs));
            int memoffset = ofs.value;
            ofs.value += memsize;
            if ((ofs.value > paggsize.get()))
                paggsize.set(0, ofs.value);
            if (!isunion)
                nextoffset.set(0, ofs.value);
            if ((paggalignsize.get() < actualAlignment))
                paggalignsize.set(0, actualAlignment);
            return memoffset;
        }

        public  Type getType() {
            return this.type;
        }

        public  boolean isDeprecated() {
            return this.isdeprecated;
        }

        public  boolean isNested() {
            return this.enclosing != null;
        }

        public  void makeNested() {
            if (this.enclosing != null)
                return ;
            if ((this.sizeok == Sizeok.done))
                return ;
            if ((this.isUnionDeclaration() != null) || (this.isInterfaceDeclaration() != null))
                return ;
            if ((this.storage_class & 1L) != 0)
                return ;
            Dsymbol s = this.toParentLocal();
            if (s == null)
                s = this.toParent2();
            if (s == null)
                return ;
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
                                    this.enclosing = ti.enclosing;
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
                if (((t.ty & 0xFF) == ENUMTY.Tstruct))
                    t = Type.tvoidptr.value;
                assert(this.vthis == null);
                this.vthis = new ThisDeclaration(this.loc, t);
                (this.members.get()).push(this.vthis);
                this.vthis.storage_class |= 64L;
                this.vthis.parent.value = this;
                this.vthis.protection = new Prot(Prot.Kind.public_).copy();
                this.vthis.alignment = t.alignment();
                this.vthis.semanticRun = PASS.semanticdone;
                if ((this.sizeok == Sizeok.fwd))
                    this.fields.push(this.vthis);
                this.makeNested2();
            }
        }

        public  void makeNested2() {
            if (this.vthis2 != null)
                return ;
            if (this.vthis == null)
                this.makeNested();
            if (this.vthis == null)
                return ;
            if ((this.sizeok == Sizeok.done))
                return ;
            if ((this.isUnionDeclaration() != null) || (this.isInterfaceDeclaration() != null))
                return ;
            if ((this.storage_class & 1L) != 0)
                return ;
            Dsymbol s0 = this.toParentLocal();
            Dsymbol s = this.toParent2();
            if ((s == null) || (s0 == null) || (pequals(s, s0)))
                return ;
            ClassDeclaration cd = s.isClassDeclaration();
            Type t = cd != null ? cd.type : Type.tvoidptr.value;
            this.vthis2 = new ThisDeclaration(this.loc, t);
            (this.members.get()).push(this.vthis2);
            this.vthis2.storage_class |= 64L;
            this.vthis2.parent.value = this;
            this.vthis2.protection = new Prot(Prot.Kind.public_).copy();
            this.vthis2.alignment = t.alignment();
            this.vthis2.semanticRun = PASS.semanticdone;
            if ((this.sizeok == Sizeok.fwd))
                this.fields.push(this.vthis2);
        }

        public  boolean isExport() {
            return this.protection.kind == Prot.Kind.export_;
        }

        public  Dsymbol searchCtor() {
            Dsymbol s = this.search(Loc.initial.value, Id.ctor.value, 8);
            if (s != null)
            {
                if (!((s.isCtorDeclaration() != null) || (s.isTemplateDeclaration() != null) || (s.isOverloadSet() != null)))
                {
                    s.error(new BytePtr("is not a constructor; identifiers starting with `__` are reserved for the implementation"));
                    this.errors = true;
                    s = null;
                }
            }
            if ((s != null) && (!pequals(s.toParent(), this)))
                s = null;
            if (s != null)
            {
                {
                    int i = 0;
                    for (; (i < (this.members.get()).length);i++){
                        Dsymbol sm = (this.members.get()).get(i);
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
            return this.type;
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
