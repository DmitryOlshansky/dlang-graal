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
    static TypeFunction search_toStringtftostring;

    public static FuncDeclaration search_toString(StructDeclaration sd) {
        Dsymbol s = search_function(sd, Id.tostring);
        FuncDeclaration fd = s != null ? s.isFuncDeclaration() : null;
        if (fd != null)
        {
            if (dstruct.search_toStringtftostring == null)
            {
                dstruct.search_toStringtftostring = new TypeFunction(new ParameterList(null, VarArg.none), Type.tstring, LINK.d, 0L);
                dstruct.search_toStringtftostring = merge(dstruct.search_toStringtftostring).toTypeFunction();
            }
            fd = fd.overloadExactMatch(dstruct.search_toStringtftostring);
        }
        return fd;
    }

    public static void semanticTypeInfo(Scope sc, Type t) {
        Ref<Scope> sc_ref = ref(sc);
        if (sc_ref.value != null)
        {
            if ((sc_ref.value).func == null)
                return ;
            if ((sc_ref.value).intypeof != 0)
                return ;
            if (((sc_ref.value).flags & 384) != 0)
                return ;
        }
        if (t == null)
            return ;
        Function1<TypeVector,Void> visitVector = new Function1<TypeVector,Void>(){
            public Void invoke(TypeVector t) {
                semanticTypeInfo(sc_ref.value, t.basetype);
            }
        };
        Function1<TypeAArray,Void> visitAArray = new Function1<TypeAArray,Void>(){
            public Void invoke(TypeAArray t) {
                semanticTypeInfo(sc_ref.value, t.index);
                semanticTypeInfo(sc_ref.value, t.next);
            }
        };
        Function1<TypeStruct,Void> visitStruct = new Function1<TypeStruct,Void>(){
            public Void invoke(TypeStruct t) {
                StructDeclaration sd = t.sym;
                if (sc_ref.value == null)
                {
                    Scope scx = new Scope().copy();
                    scx._module = sd.getModule();
                    getTypeInfoType(sd.loc, t, scx);
                    sd.requestTypeInfo = true;
                }
                else if ((sc_ref.value).minst == null)
                {
                }
                else
                {
                    getTypeInfoType(sd.loc, t, sc_ref.value);
                    sd.requestTypeInfo = true;
                }
                if (sd.members == null)
                    return null;
                if ((sd.xeq == null) && (sd.xcmp == null) && (sd.postblit == null) && (sd.dtor == null) && (sd.xhash == null) && (search_toString(sd) == null))
                    return null;
                if ((sd.semanticRun >= PASS.semantic3))
                {
                }
                else {
                    TemplateInstance ti = sd.isInstantiated();
                    if ((ti) != null)
                    {
                        if ((ti.minst != null) && !ti.minst.isRoot())
                            dmodule.Module.addDeferredSemantic3(sd);
                    }
                    else
                    {
                        if (sd.inNonRoot())
                        {
                            dmodule.Module.addDeferredSemantic3(sd);
                        }
                    }
                }
            }
        };
        Function1<TypeTuple,Void> visitTuple = new Function1<TypeTuple,Void>(){
            public Void invoke(TypeTuple t) {
                if (t.arguments != null)
                {
                    {
                        Slice<Parameter> __r1113 = (t.arguments).opSlice().copy();
                        int __key1114 = 0;
                        for (; (__key1114 < __r1113.getLength());__key1114 += 1) {
                            Parameter arg = __r1113.get(__key1114);
                            semanticTypeInfo(sc_ref.value, arg.type);
                        }
                    }
                }
            }
        };
        Type tb = t.toBasetype();
        switch ((tb.ty & 0xFF))
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
        public boolean hasIdentityAssign = false;
        public boolean hasIdentityEquals = false;
        public boolean hasNoFields = false;
        public DArray<FuncDeclaration> postblits = new DArray<FuncDeclaration>();
        public FuncDeclaration postblit;
        public boolean hasCopyCtor = false;
        public FuncDeclaration xeq;
        public FuncDeclaration xcmp;
        public FuncDeclaration xhash;
        public static FuncDeclaration xerreq;
        public static FuncDeclaration xerrcmp;
        public int alignment = 0;
        public int ispod = 0;
        public Type arg1type;
        public Type arg2type;
        public boolean requestTypeInfo = false;
        public  StructDeclaration(Loc loc, Identifier id, boolean inObject) {
            super(loc, id);
            this.zeroInit = false;
            this.ispod = StructPOD.fwd;
            this.type = new TypeStruct(this);
            if (inObject)
            {
                if ((pequals(id, Id.ModuleInfo)) && (dmodule.Module.moduleinfo == null))
                    dmodule.Module.moduleinfo = this;
            }
        }

        public static StructDeclaration create(Loc loc, Identifier id, boolean inObject) {
            return new StructDeclaration(loc, id, inObject);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            StructDeclaration sd = s != null ? (StructDeclaration)s : new StructDeclaration(this.loc, this.ident, false);
            return this.syntaxCopy(sd);
        }

        public  void semanticTypeInfoMembers() {
            if ((this.xeq != null) && (this.xeq._scope != null) && (this.xeq.semanticRun < PASS.semantic3done))
            {
                int errors = global.startGagging();
                semantic3(this.xeq, this.xeq._scope);
                if (global.endGagging(errors))
                    this.xeq = xerreq;
            }
            if ((this.xcmp != null) && (this.xcmp._scope != null) && (this.xcmp.semanticRun < PASS.semantic3done))
            {
                int errors = global.startGagging();
                semantic3(this.xcmp, this.xcmp._scope);
                if (global.endGagging(errors))
                    this.xcmp = xerrcmp;
            }
            FuncDeclaration ftostr = search_toString(this);
            if ((ftostr != null) && (ftostr._scope != null) && (ftostr.semanticRun < PASS.semantic3done))
            {
                semantic3(ftostr, ftostr._scope);
            }
            if ((this.xhash != null) && (this.xhash._scope != null) && (this.xhash.semanticRun < PASS.semantic3done))
            {
                semantic3(this.xhash, this.xhash._scope);
            }
            if ((this.postblit != null) && (this.postblit._scope != null) && (this.postblit.semanticRun < PASS.semantic3done))
            {
                semantic3(this.postblit, this.postblit._scope);
            }
            if ((this.dtor != null) && (this.dtor._scope != null) && (this.dtor.semanticRun < PASS.semantic3done))
            {
                semantic3(this.dtor, this.dtor._scope);
            }
        }

        public  Dsymbol search(Loc loc, Identifier ident, int flags) {
            if ((this._scope != null) && (this.symtab == null))
                dsymbolSemantic(this, this._scope);
            if ((this.members == null) || (this.symtab == null))
            {
                this.error(new BytePtr("is forward referenced when looking for `%s`"), ident.toChars());
                return null;
            }
            return this.search(loc, ident, flags);
        }

        // defaulted all parameters starting with #3
        public  Dsymbol search(Loc loc, Identifier ident) {
            search(loc, ident, 8);
        }

        public  BytePtr kind() {
            return new BytePtr("struct");
        }

        public  void finalizeSize() {
            assert((this.sizeok != Sizeok.done));
            if ((this.sizeok == Sizeok.inProcess))
            {
                return ;
            }
            this.sizeok = Sizeok.inProcess;
            this.fields.setDim(0);
            IntRef offset = ref(0);
            boolean isunion = this.isUnionDeclaration() != null;
            {
                int i = 0;
                for (; (i < (this.members).length);i++){
                    Dsymbol s = (this.members).get(i);
                    s.setFieldOffset(this, ptr(offset), isunion);
                }
            }
            if (((this.type.ty & 0xFF) == ENUMTY.Terror))
            {
                this.errors = true;
                return ;
            }
            if ((this.structsize == 0))
            {
                this.hasNoFields = true;
                this.structsize = 1;
                this.alignsize = 1;
            }
            if ((this.alignment == -1))
                this.structsize = this.structsize + this.alignsize - 1 & ~(this.alignsize - 1);
            else
                this.structsize = this.structsize + this.alignment - 1 & ~(this.alignment - 1);
            this.sizeok = Sizeok.done;
            if (this.errors)
                return ;
            if (this.checkOverlappedFields())
            {
                this.errors = true;
                return ;
            }
            this.zeroInit = true;
            {
                Slice<VarDeclaration> __r1115 = this.fields.opSlice().copy();
                int __key1116 = 0;
                for (; (__key1116 < __r1115.getLength());__key1116 += 1) {
                    VarDeclaration vd = __r1115.get(__key1116);
                    if (vd._init != null)
                    {
                        if (vd._init.isVoidInitializer() != null)
                            continue;
                        if ((vd.type.size(vd.loc) == 0L))
                            continue;
                        Expression exp = vd.getConstInitializer(true);
                        if ((exp == null) || !_isZeroInit(exp))
                        {
                            this.zeroInit = false;
                            break;
                        }
                    }
                    else if (!vd.type.isZeroInit(this.loc))
                    {
                        this.zeroInit = false;
                        break;
                    }
                }
            }
            TypeTuple tt = target.toArgTypes(this.type);
            int dim = tt != null ? (tt.arguments).length : 0;
            if ((dim >= 1))
            {
                assert((dim <= 2));
                this.arg1type = (tt.arguments).get(0).type;
                if ((dim == 2))
                    this.arg2type = (tt.arguments).get(1).type;
            }
        }

        public  boolean fit(Loc loc, Scope sc, DArray<Expression> elements, Type stype) {
            if (elements == null)
                return true;
            int nfields = this.nonHiddenFields();
            int offset = 0;
            {
                int i = 0;
            L_outer1:
                for (; (i < (elements).length);i++){
                    Expression e = (elements).get(i);
                    if (e == null)
                        continue L_outer1;
                    e = resolveProperties(sc, e);
                    if ((i >= nfields))
                    {
                        if ((i <= this.fields.length) && ((e.op & 0xFF) == 13))
                        {
                            continue L_outer1;
                        }
                        error(loc, new BytePtr("more initializers than fields (%d) of `%s`"), nfields, this.toChars());
                        return false;
                    }
                    VarDeclaration v = this.fields.get(i);
                    if ((v.offset < offset))
                    {
                        error(loc, new BytePtr("overlapping initialization for `%s`"), v.toChars());
                        if (this.isUnionDeclaration() == null)
                        {
                            ByteSlice errorMsg = new ByteSlice("`struct` initializers that contain anonymous unions must initialize only the first member of a `union`. All subsequent non-overlapping fields are default initialized");
                            errorSupplemental(loc, new BytePtr("`struct` initializers that contain anonymous unions must initialize only the first member of a `union`. All subsequent non-overlapping fields are default initialized"));
                        }
                        return false;
                    }
                    offset = (int)((long)v.offset + v.type.size());
                    Type t = v.type;
                    if (stype != null)
                        t = t.addMod(stype.mod);
                    Type origType = t;
                    Type tb = t.toBasetype();
                    boolean hasPointers = tb.hasPointers();
                    if (hasPointers)
                    {
                        if ((stype.alignment() < target.ptrsize) || ((v.offset & target.ptrsize - 1) != 0) && ((sc).func != null) && (sc).func.setUnsafe())
                        {
                            error(loc, new BytePtr("field `%s.%s` cannot assign to misaligned pointers in `@safe` code"), this.toChars(), v.toChars());
                            return false;
                        }
                    }
                    try {
                        if (((e.op & 0xFF) == 121) && ((tb.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            StringExp se = (StringExp)e;
                            Type typeb = se.type.toBasetype();
                            byte tynto = tb.nextOf().ty;
                            if ((se.committed == 0) && ((typeb.ty & 0xFF) == ENUMTY.Tarray) || ((typeb.ty & 0xFF) == ENUMTY.Tsarray) && ((tynto & 0xFF) == ENUMTY.Tchar) || ((tynto & 0xFF) == ENUMTY.Twchar) || ((tynto & 0xFF) == ENUMTY.Tdchar) && ((long)se.numberOfCodeUnits((tynto & 0xFF)) < ((TypeSArray)tb).dim.toInteger()))
                            {
                                e = se.castTo(sc, t);
                                /*goto L1*/throw Dispatch0.INSTANCE;
                            }
                        }
                        for (; (e.implicitConvTo(t) == 0) && ((tb.ty & 0xFF) == ENUMTY.Tsarray);){
                            t = tb.nextOf();
                            tb = t.toBasetype();
                        }
                        if (e.implicitConvTo(t) == 0)
                            t = origType;
                        e = e.implicitCastTo(sc, t);
                    }
                    catch(Dispatch0 __d){}
                /*L1:*/
                    if (((e.op & 0xFF) == 127))
                        return false;
                    elements.set(i, doCopyOrMove(sc, e, null));
                }
            }
            return true;
        }

        public  boolean isPOD() {
            if ((this.ispod != StructPOD.fwd))
                return this.ispod == StructPOD.yes;
            this.ispod = StructPOD.yes;
            if ((this.enclosing != null) || (this.postblit != null) || (this.dtor != null) || this.hasCopyCtor)
                this.ispod = StructPOD.no;
            {
                int i = 0;
                for (; (i < this.fields.length);i++){
                    VarDeclaration v = this.fields.get(i);
                    if ((v.storage_class & 2097152L) != 0)
                    {
                        this.ispod = StructPOD.no;
                        break;
                    }
                    Type tv = v.type.baseElemOf();
                    if (((tv.ty & 0xFF) == ENUMTY.Tstruct))
                    {
                        TypeStruct ts = (TypeStruct)tv;
                        StructDeclaration sd = ts.sym;
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
        switch ((exp.op & 0xFF))
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
                    int __limit1118 = sle.sd.fields.length;
                    for (; (__key1117 < __limit1118);__key1117 += 1) {
                        int i = __key1117;
                        VarDeclaration field = sle.sd.fields.get(i);
                        if (field.type.size(field.loc) != 0)
                        {
                            Expression e = (sle.elements).get(i);
                            if (e != null ? !_isZeroInit(e) : !field.type.isZeroInit(field.loc))
                                return false;
                        }
                    }
                }
                return true;
            case 47:
                ArrayLiteralExp ale = (ArrayLiteralExp)exp;
                int dim = ale.elements != null ? (ale.elements).length : 0;
                if (((ale.type.toBasetype().ty & 0xFF) == ENUMTY.Tarray))
                    return dim == 0;
                {
                    int __key1119 = 0;
                    int __limit1120 = dim;
                    for (; (__key1119 < __limit1120);__key1119 += 1) {
                        int i_1 = __key1119;
                        if (!_isZeroInit(ale.getElement(i_1)))
                            return false;
                    }
                }
                return true;
            case 121:
                StringExp se = (StringExp)exp;
                if (((se.type.toBasetype().ty & 0xFF) == ENUMTY.Tarray))
                    return se.len == 0;
                {
                    int __key1121 = 0;
                    int __limit1122 = se.len;
                    for (; (__key1121 < __limit1122);__key1121 += 1) {
                        int i_2 = __key1121;
                        if (se.getCodeUnit(i_2) != 0)
                            return false;
                    }
                }
                return true;
            case 229:
                VectorExp ve = (VectorExp)exp;
                return _isZeroInit(ve.e1);
            case 140:
            case 147:
                return (exp.toReal() == CTFloat.zero) && (exp.toImaginary() == CTFloat.zero);
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
            UnionDeclaration ud = new UnionDeclaration(this.loc, this.ident);
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
