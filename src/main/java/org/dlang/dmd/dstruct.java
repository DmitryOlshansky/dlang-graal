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
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.opover.*;
import static org.dlang.dmd.semantic3.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.typinf.*;
import static org.dlang.dmd.visitor.*;

public class dstruct {
    static TypeFunction search_toStringtftostring = null;

    public static FuncDeclaration search_toString(StructDeclaration sd) {
        Dsymbol s = search_function(sd, Id.tostring);
        FuncDeclaration fd = s != null ? s.isFuncDeclaration() : null;
        if (fd != null)
        {
            if (dstruct.search_toStringtftostring == null)
            {
                dstruct.search_toStringtftostring = new TypeFunction(new ParameterList(null, VarArg.none), Type.tstring.value, LINK.d, 0L);
                dstruct.search_toStringtftostring = merge(dstruct.search_toStringtftostring).toTypeFunction();
            }
            fd = fd.overloadExactMatch(dstruct.search_toStringtftostring);
        }
        return fd;
    }

    public static void semanticTypeInfo(Ptr<Scope> sc, Type t) {
        Ref<Ptr<Scope>> sc_ref = ref(sc);
        if (sc_ref.value != null)
        {
            if ((sc_ref.value.get()).func.value == null)
            {
                return ;
            }
            if ((sc_ref.value.get()).intypeof.value != 0)
            {
                return ;
            }
            if (((sc_ref.value.get()).flags.value & 384) != 0)
            {
                return ;
            }
        }
        if (t == null)
        {
            return ;
        }
        Function1<TypeVector,Void> visitVector = new Function1<TypeVector,Void>(){
            public Void invoke(TypeVector t) {
                semanticTypeInfo(sc_ref.value, t.basetype.value);
                return null;
            }
        };
        Function1<TypeAArray,Void> visitAArray = new Function1<TypeAArray,Void>(){
            public Void invoke(TypeAArray t) {
                semanticTypeInfo(sc_ref.value, t.index.value);
                semanticTypeInfo(sc_ref.value, t.next.value);
                return null;
            }
        };
        Function1<TypeStruct,Void> visitStruct = new Function1<TypeStruct,Void>(){
            public Void invoke(TypeStruct t) {
                Ref<TypeStruct> t_ref = ref(t);
                Ref<StructDeclaration> sd = ref(t_ref.value.sym.value);
                if (sc_ref.value == null)
                {
                    Ref<Scope> scx = ref(new Scope().copy());
                    scx.value._module.value = sd.value.getModule();
                    getTypeInfoType(sd.value.loc.value, t_ref.value, ptr(scx));
                    sd.value.requestTypeInfo.value = true;
                }
                else if ((sc_ref.value.get()).minst.value == null)
                {
                }
                else
                {
                    getTypeInfoType(sd.value.loc.value, t_ref.value, sc_ref.value);
                    sd.value.requestTypeInfo.value = true;
                }
                if (sd.value.members.value == null)
                {
                    return null;
                }
                if ((sd.value.xeq.value == null) && (sd.value.xcmp.value == null) && (sd.value.postblit.value == null) && (sd.value.dtor.value == null) && (sd.value.xhash.value == null) && (search_toString(sd.value) == null))
                {
                    return null;
                }
                if ((sd.value.semanticRun.value >= PASS.semantic3))
                {
                }
                else {
                    Ref<TemplateInstance> ti = ref(sd.value.isInstantiated());
                    if ((ti.value) != null)
                    {
                        if ((ti.value.minst.value != null) && !ti.value.minst.value.isRoot())
                        {
                            dmodule.Module.addDeferredSemantic3(sd.value);
                        }
                    }
                    else
                    {
                        if (sd.value.inNonRoot())
                        {
                            dmodule.Module.addDeferredSemantic3(sd.value);
                        }
                    }
                }
                return null;
            }
        };
        Function1<TypeTuple,Void> visitTuple = new Function1<TypeTuple,Void>(){
            public Void invoke(TypeTuple t) {
                if (t.arguments.value != null)
                {
                    {
                        Ref<Slice<Parameter>> __r1113 = ref((t.arguments.value.get()).opSlice().copy());
                        IntRef __key1114 = ref(0);
                        for (; (__key1114.value < __r1113.value.getLength());__key1114.value += 1) {
                            Parameter arg = __r1113.value.get(__key1114.value);
                            semanticTypeInfo(sc_ref.value, arg.type.value);
                        }
                    }
                }
                return null;
            }
        };
        Type tb = t.toBasetype();
        switch ((tb.ty.value & 0xFF))
        {
            case 41:
                visitVector.invoke(tb.isTypeVector());
                break;
            case 2:
                visitAArray.invoke(tb.isTypeAArray());
                break;
            case 8:
                visitStruct.invoke(tb.isTypeStruct());
                break;
            case 37:
                visitTuple.invoke(tb.isTypeTuple());
                break;
            case 7:
            case 9:
                break;
            default:
            semanticTypeInfo(sc_ref.value, tb.nextOf());
            break;
        }
    }


    public static class StructFlags 
    {
        public static final int none = 0;
        public static final int hasPointers = 1;
    }


    public static class StructPOD 
    {
        public static final int no = 0;
        public static final int yes = 1;
        public static final int fwd = 2;
    }

    public static class StructDeclaration extends AggregateDeclaration
    {
        public boolean zeroInit = false;
        public Ref<Boolean> hasIdentityAssign = ref(false);
        public Ref<Boolean> hasIdentityEquals = ref(false);
        public boolean hasNoFields = false;
        public DArray<FuncDeclaration> postblits = new DArray<FuncDeclaration>();
        public Ref<FuncDeclaration> postblit = ref(null);
        public boolean hasCopyCtor = false;
        public Ref<FuncDeclaration> xeq = ref(null);
        public Ref<FuncDeclaration> xcmp = ref(null);
        public Ref<FuncDeclaration> xhash = ref(null);
        public static Ref<FuncDeclaration> xerreq = ref(null);
        public static FuncDeclaration xerrcmp = null;
        public int alignment = 0;
        public int ispod = 0;
        public Type arg1type = null;
        public Type arg2type = null;
        public Ref<Boolean> requestTypeInfo = ref(false);
        public  StructDeclaration(Loc loc, Identifier id, boolean inObject) {
            super(loc, id);
            this.zeroInit = false;
            this.ispod = StructPOD.fwd;
            this.type.value = new TypeStruct(this);
            if (inObject)
            {
                if ((pequals(id, Id.ModuleInfo)) && (dmodule.Module.moduleinfo == null))
                {
                    dmodule.Module.moduleinfo = this;
                }
            }
        }

        public static StructDeclaration create(Loc loc, Identifier id, boolean inObject) {
            return new StructDeclaration(loc, id, inObject);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            StructDeclaration sd = s != null ? (StructDeclaration)s : new StructDeclaration(this.loc.value, this.ident.value, false);
            return this.syntaxCopy(sd);
        }

        public  void semanticTypeInfoMembers() {
            if ((this.xeq.value != null) && (this.xeq.value._scope.value != null) && (this.xeq.value.semanticRun.value < PASS.semantic3done))
            {
                int errors = global.startGagging();
                semantic3(this.xeq.value, this.xeq.value._scope.value);
                if (global.endGagging(errors))
                {
                    this.xeq.value = xerreq.value;
                }
            }
            if ((this.xcmp.value != null) && (this.xcmp.value._scope.value != null) && (this.xcmp.value.semanticRun.value < PASS.semantic3done))
            {
                int errors = global.startGagging();
                semantic3(this.xcmp.value, this.xcmp.value._scope.value);
                if (global.endGagging(errors))
                {
                    this.xcmp.value = xerrcmp;
                }
            }
            FuncDeclaration ftostr = search_toString(this);
            if ((ftostr != null) && (ftostr._scope.value != null) && (ftostr.semanticRun.value < PASS.semantic3done))
            {
                semantic3(ftostr, ftostr._scope.value);
            }
            if ((this.xhash.value != null) && (this.xhash.value._scope.value != null) && (this.xhash.value.semanticRun.value < PASS.semantic3done))
            {
                semantic3(this.xhash.value, this.xhash.value._scope.value);
            }
            if ((this.postblit.value != null) && (this.postblit.value._scope.value != null) && (this.postblit.value.semanticRun.value < PASS.semantic3done))
            {
                semantic3(this.postblit.value, this.postblit.value._scope.value);
            }
            if ((this.dtor.value != null) && (this.dtor.value._scope.value != null) && (this.dtor.value.semanticRun.value < PASS.semantic3done))
            {
                semantic3(this.dtor.value, this.dtor.value._scope.value);
            }
        }

        public  Dsymbol search(Loc loc, Identifier ident, int flags) {
            if ((this._scope.value != null) && (this.symtab == null))
            {
                dsymbolSemantic(this, this._scope.value);
            }
            if ((this.members.value == null) || (this.symtab == null))
            {
                this.error(new BytePtr("is forward referenced when looking for `%s`"), ident.toChars());
                return null;
            }
            return this.search(loc, ident, flags);
        }

        // defaulted all parameters starting with #3
        public  Dsymbol search(Loc loc, Identifier ident) {
            return search(loc, ident, 8);
        }

        public  BytePtr kind() {
            return new BytePtr("struct");
        }

        public  void finalizeSize() {
            assert((this.sizeok.value != Sizeok.done));
            if ((this.sizeok.value == Sizeok.inProcess))
            {
                return ;
            }
            this.sizeok.value = Sizeok.inProcess;
            this.fields.setDim(0);
            IntRef offset = ref(0);
            boolean isunion = this.isUnionDeclaration() != null;
            {
                int i = 0;
                for (; (i < (this.members.value.get()).length.value);i++){
                    Dsymbol s = (this.members.value.get()).get(i);
                    s.setFieldOffset(this, ptr(offset), isunion);
                }
            }
            if (((this.type.value.ty.value & 0xFF) == ENUMTY.Terror))
            {
                this.errors.value = true;
                return ;
            }
            if ((this.structsize.value == 0))
            {
                this.hasNoFields = true;
                this.structsize.value = 1;
                this.alignsize.value = 1;
            }
            if ((this.alignment == -1))
            {
                this.structsize.value = this.structsize.value + this.alignsize.value - 1 & ~(this.alignsize.value - 1);
            }
            else
            {
                this.structsize.value = this.structsize.value + this.alignment - 1 & ~(this.alignment - 1);
            }
            this.sizeok.value = Sizeok.done;
            if (this.errors.value)
            {
                return ;
            }
            if (this.checkOverlappedFields())
            {
                this.errors.value = true;
                return ;
            }
            this.zeroInit = true;
            {
                Slice<VarDeclaration> __r1115 = this.fields.opSlice().copy();
                int __key1116 = 0;
                for (; (__key1116 < __r1115.getLength());__key1116 += 1) {
                    VarDeclaration vd = __r1115.get(__key1116);
                    if (vd._init.value != null)
                    {
                        if (vd._init.value.isVoidInitializer() != null)
                        {
                            continue;
                        }
                        if ((vd.type.value.size(vd.loc.value) == 0L))
                        {
                            continue;
                        }
                        Expression exp = vd.getConstInitializer(true);
                        if ((exp == null) || !_isZeroInit(exp))
                        {
                            this.zeroInit = false;
                            break;
                        }
                    }
                    else if (!vd.type.value.isZeroInit(this.loc.value))
                    {
                        this.zeroInit = false;
                        break;
                    }
                }
            }
            TypeTuple tt = target.toArgTypes(this.type.value);
            int dim = tt != null ? (tt.arguments.value.get()).length.value : 0;
            if ((dim >= 1))
            {
                assert((dim <= 2));
                this.arg1type = (tt.arguments.value.get()).get(0).type.value;
                if ((dim == 2))
                {
                    this.arg2type = (tt.arguments.value.get()).get(1).type.value;
                }
            }
        }

        public  boolean fit(Loc loc, Ptr<Scope> sc, Ptr<DArray<Expression>> elements, Type stype) {
            if (elements == null)
            {
                return true;
            }
            int nfields = this.nonHiddenFields();
            int offset = 0;
            {
                int i = 0;
            L_outer1:
                for (; (i < (elements.get()).length.value);i++){
                    Expression e = (elements.get()).get(i);
                    if (e == null)
                    {
                        continue L_outer1;
                    }
                    e = resolveProperties(sc, e);
                    if ((i >= nfields))
                    {
                        if ((i <= this.fields.length.value) && ((e.op.value & 0xFF) == 13))
                        {
                            continue L_outer1;
                        }
                        error(loc, new BytePtr("more initializers than fields (%d) of `%s`"), nfields, this.toChars());
                        return false;
                    }
                    VarDeclaration v = this.fields.get(i);
                    if ((v.offset.value < offset))
                    {
                        error(loc, new BytePtr("overlapping initialization for `%s`"), v.toChars());
                        if (this.isUnionDeclaration() == null)
                        {
                            ByteSlice errorMsg = new ByteSlice("`struct` initializers that contain anonymous unions must initialize only the first member of a `union`. All subsequent non-overlapping fields are default initialized");
                            errorSupplemental(loc, new BytePtr("`struct` initializers that contain anonymous unions must initialize only the first member of a `union`. All subsequent non-overlapping fields are default initialized"));
                        }
                        return false;
                    }
                    offset = (int)((long)v.offset.value + v.type.value.size());
                    Type t = v.type.value;
                    if (stype != null)
                    {
                        t = t.addMod(stype.mod.value);
                    }
                    Type origType = t;
                    Type tb = t.toBasetype();
                    boolean hasPointers = tb.hasPointers();
                    if (hasPointers)
                    {
                        if ((stype.alignment() < target.ptrsize.value) || ((v.offset.value & target.ptrsize.value - 1) != 0) && ((sc.get()).func.value != null) && (sc.get()).func.value.setUnsafe())
                        {
                            error(loc, new BytePtr("field `%s.%s` cannot assign to misaligned pointers in `@safe` code"), this.toChars(), v.toChars());
                            return false;
                        }
                    }
                    try {
                        if (((e.op.value & 0xFF) == 121) && ((tb.ty.value & 0xFF) == ENUMTY.Tsarray))
                        {
                            StringExp se = (StringExp)e;
                            Type typeb = se.type.value.toBasetype();
                            byte tynto = tb.nextOf().ty.value;
                            if ((se.committed.value == 0) && ((typeb.ty.value & 0xFF) == ENUMTY.Tarray) || ((typeb.ty.value & 0xFF) == ENUMTY.Tsarray) && ((tynto & 0xFF) == ENUMTY.Tchar) || ((tynto & 0xFF) == ENUMTY.Twchar) || ((tynto & 0xFF) == ENUMTY.Tdchar) && ((long)se.numberOfCodeUnits((tynto & 0xFF)) < ((TypeSArray)tb).dim.value.toInteger()))
                            {
                                e = se.castTo(sc, t);
                                /*goto L1*/throw Dispatch0.INSTANCE;
                            }
                        }
                        for (; (e.implicitConvTo(t) == 0) && ((tb.ty.value & 0xFF) == ENUMTY.Tsarray);){
                            t = tb.nextOf();
                            tb = t.toBasetype();
                        }
                        if (e.implicitConvTo(t) == 0)
                        {
                            t = origType;
                        }
                        e = e.implicitCastTo(sc, t);
                    }
                    catch(Dispatch0 __d){}
                /*L1:*/
                    if (((e.op.value & 0xFF) == 127))
                    {
                        return false;
                    }
                    elements.get().set(i, doCopyOrMove(sc, e, null));
                }
            }
            return true;
        }

        public  boolean isPOD() {
            if ((this.ispod != StructPOD.fwd))
            {
                return this.ispod == StructPOD.yes;
            }
            this.ispod = StructPOD.yes;
            if ((this.enclosing != null) || (this.postblit.value != null) || (this.dtor.value != null) || this.hasCopyCtor)
            {
                this.ispod = StructPOD.no;
            }
            {
                int i = 0;
                for (; (i < this.fields.length.value);i++){
                    VarDeclaration v = this.fields.get(i);
                    if ((v.storage_class.value & 2097152L) != 0)
                    {
                        this.ispod = StructPOD.no;
                        break;
                    }
                    Type tv = v.type.value.baseElemOf();
                    if (((tv.ty.value & 0xFF) == ENUMTY.Tstruct))
                    {
                        TypeStruct ts = (TypeStruct)tv;
                        StructDeclaration sd = ts.sym.value;
                        if (!sd.isPOD())
                        {
                            this.ispod = StructPOD.no;
                            break;
                        }
                    }
                }
            }
            return this.ispod == StructPOD.yes;
        }

        public  StructDeclaration isStructDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public StructDeclaration() {}

        public StructDeclaration copy() {
            StructDeclaration that = new StructDeclaration();
            that.zeroInit = this.zeroInit;
            that.hasIdentityAssign = this.hasIdentityAssign;
            that.hasIdentityEquals = this.hasIdentityEquals;
            that.hasNoFields = this.hasNoFields;
            that.postblits = this.postblits;
            that.postblit = this.postblit;
            that.hasCopyCtor = this.hasCopyCtor;
            that.xeq = this.xeq;
            that.xcmp = this.xcmp;
            that.xhash = this.xhash;
            that.alignment = this.alignment;
            that.ispod = this.ispod;
            that.arg1type = this.arg1type;
            that.arg2type = this.arg2type;
            that.requestTypeInfo = this.requestTypeInfo;
            that.type = this.type;
            that.storage_class = this.storage_class;
            that.protection = this.protection;
            that.structsize = this.structsize;
            that.alignsize = this.alignsize;
            that.fields = this.fields;
            that.sizeok = this.sizeok;
            that.deferred = this.deferred;
            that.isdeprecated = this.isdeprecated;
            that.classKind = this.classKind;
            that.enclosing = this.enclosing;
            that.vthis = this.vthis;
            that.vthis2 = this.vthis2;
            that.invs = this.invs;
            that.inv = this.inv;
            that.aggNew = this.aggNew;
            that.aggDelete = this.aggDelete;
            that.ctor = this.ctor;
            that.defaultCtor = this.defaultCtor;
            that.aliasthis = this.aliasthis;
            that.noDefaultCtor = this.noDefaultCtor;
            that.dtors = this.dtors;
            that.dtor = this.dtor;
            that.primaryDtor = this.primaryDtor;
            that.tidtor = this.tidtor;
            that.fieldDtor = this.fieldDtor;
            that.getRTInfo = this.getRTInfo;
            that.stag = this.stag;
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
    public static boolean _isZeroInit(Expression exp) {
        switch ((exp.op.value & 0xFF))
        {
            case 135:
                return exp.toInteger() == 0L;
            case 13:
            case 16:
                return true;
            case 49:
                StructLiteralExp sle = (StructLiteralExp)exp;
                {
                    int __key1117 = 0;
                    int __limit1118 = sle.sd.fields.length.value;
                    for (; (__key1117 < __limit1118);__key1117 += 1) {
                        int i = __key1117;
                        VarDeclaration field = sle.sd.fields.get(i);
                        if (field.type.value.size(field.loc.value) != 0)
                        {
                            Expression e = (sle.elements.value.get()).get(i);
                            if (e != null ? !_isZeroInit(e) : !field.type.value.isZeroInit(field.loc.value))
                            {
                                return false;
                            }
                        }
                    }
                }
                return true;
            case 47:
                ArrayLiteralExp ale = (ArrayLiteralExp)exp;
                int dim = ale.elements.value != null ? (ale.elements.value.get()).length.value : 0;
                if (((ale.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tarray))
                {
                    return dim == 0;
                }
                {
                    int __key1119 = 0;
                    int __limit1120 = dim;
                    for (; (__key1119 < __limit1120);__key1119 += 1) {
                        int i_1 = __key1119;
                        if (!_isZeroInit(ale.getElement(i_1)))
                        {
                            return false;
                        }
                    }
                }
                return true;
            case 121:
                StringExp se = (StringExp)exp;
                if (((se.type.value.toBasetype().ty.value & 0xFF) == ENUMTY.Tarray))
                {
                    return se.len.value == 0;
                }
                {
                    int __key1121 = 0;
                    int __limit1122 = se.len.value;
                    for (; (__key1121 < __limit1122);__key1121 += 1) {
                        int i_2 = __key1121;
                        if (se.getCodeUnit(i_2) != 0)
                        {
                            return false;
                        }
                    }
                }
                return true;
            case 229:
                VectorExp ve = (VectorExp)exp;
                return _isZeroInit(ve.e1.value);
            case 140:
            case 147:
                return (exp.toReal() == CTFloat.zero.value) && (exp.toImaginary() == CTFloat.zero.value);
            default:
            return false;
        }
    }

    public static class UnionDeclaration extends StructDeclaration
    {
        public  UnionDeclaration(Loc loc, Identifier id) {
            super(loc, id, false);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            assert(s == null);
            UnionDeclaration ud = new UnionDeclaration(this.loc.value, this.ident.value);
            return this.syntaxCopy(ud);
        }

        public  BytePtr kind() {
            return new BytePtr("union");
        }

        public  UnionDeclaration isUnionDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public UnionDeclaration() {}

        public UnionDeclaration copy() {
            UnionDeclaration that = new UnionDeclaration();
            that.zeroInit = this.zeroInit;
            that.hasIdentityAssign = this.hasIdentityAssign;
            that.hasIdentityEquals = this.hasIdentityEquals;
            that.hasNoFields = this.hasNoFields;
            that.postblits = this.postblits;
            that.postblit = this.postblit;
            that.hasCopyCtor = this.hasCopyCtor;
            that.xeq = this.xeq;
            that.xcmp = this.xcmp;
            that.xhash = this.xhash;
            that.alignment = this.alignment;
            that.ispod = this.ispod;
            that.arg1type = this.arg1type;
            that.arg2type = this.arg2type;
            that.requestTypeInfo = this.requestTypeInfo;
            that.type = this.type;
            that.storage_class = this.storage_class;
            that.protection = this.protection;
            that.structsize = this.structsize;
            that.alignsize = this.alignsize;
            that.fields = this.fields;
            that.sizeok = this.sizeok;
            that.deferred = this.deferred;
            that.isdeprecated = this.isdeprecated;
            that.classKind = this.classKind;
            that.enclosing = this.enclosing;
            that.vthis = this.vthis;
            that.vthis2 = this.vthis2;
            that.invs = this.invs;
            that.inv = this.inv;
            that.aggNew = this.aggNew;
            that.aggDelete = this.aggDelete;
            that.ctor = this.ctor;
            that.defaultCtor = this.defaultCtor;
            that.aliasthis = this.aliasthis;
            that.noDefaultCtor = this.noDefaultCtor;
            that.dtors = this.dtors;
            that.dtor = this.dtor;
            that.primaryDtor = this.primaryDtor;
            that.tidtor = this.tidtor;
            that.fieldDtor = this.fieldDtor;
            that.getRTInfo = this.getRTInfo;
            that.stag = this.stag;
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
}
