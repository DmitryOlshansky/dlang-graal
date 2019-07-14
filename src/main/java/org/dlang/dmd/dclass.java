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
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.gluelayer.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.objc.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.visitor.*;

public class dclass {
    static BytePtr __ctormsg = new BytePtr("only object.d can define this reserved class name");


    public static class Abstract 
    {
        public static final int fwdref = 0;
        public static final int yes = 1;
        public static final int no = 2;
    }

    public static class BaseClass
    {
        public Type type = null;
        public ClassDeclaration sym = null;
        public int offset = 0;
        public DArray<FuncDeclaration> vtbl = new DArray<FuncDeclaration>();
        public Slice<BaseClass> baseInterfaces = new Slice<BaseClass>();
        public  BaseClass(Type type) {
            this.type = type;
        }

        public  boolean fillVtbl(ClassDeclaration cd, Ptr<DArray<FuncDeclaration>> vtbl, int newinstance) {
            boolean result = false;
            if (vtbl != null)
            {
                (vtbl.get()).setDim(this.sym.vtbl.value.length);
            }
            {
                int j = this.sym.vtblOffset();
                for (; (j < this.sym.vtbl.value.length);j++){
                    FuncDeclaration ifd = this.sym.vtbl.value.get(j).isFuncDeclaration();
                    FuncDeclaration fd = null;
                    TypeFunction tf = null;
                    assert(ifd != null);
                    tf = ifd.type.toTypeFunction();
                    fd = cd.findFunc(ifd.ident, tf);
                    if ((fd != null) && !fd.isAbstract())
                    {
                        if ((fd.linkage != ifd.linkage))
                        {
                            fd.error(new BytePtr("linkage doesn't match interface function"));
                        }
                        if ((newinstance != 0) && (!pequals(fd.toParent(), cd)) && (pequals(ifd.toParent(), this.sym)))
                        {
                            cd.error(new BytePtr("interface function `%s` is not implemented"), ifd.toFullSignature());
                        }
                        if ((pequals(fd.toParent(), cd)))
                        {
                            result = true;
                        }
                    }
                    else
                    {
                        if (!cd.isAbstract())
                        {
                            cd.error(new BytePtr("interface function `%s` is not implemented"), ifd.toFullSignature());
                        }
                        fd = null;
                    }
                    if (vtbl != null)
                    {
                        vtbl.get().set(j, fd);
                    }
                }
            }
            return result;
        }

        public  void copyBaseInterfaces(Ptr<DArray<Ptr<BaseClass>>> vtblInterfaces) {
            Ptr<BaseClass> bc = ptr(new BaseClass[36]);
            this.baseInterfaces = bc.slice(0,this.sym.interfaces.getLength()).copy();
            {
                int i = 0;
                for (; (i < this.baseInterfaces.getLength());i++){
                    Ptr<BaseClass> b = ptr(this.baseInterfaces.get(i));
                    Ptr<BaseClass> b2 = this.sym.interfaces.get(i);
                    assert(((b2.get()).vtbl.length == 0));
                    (b).set(0, (b2));
                    if (i != 0)
                    {
                        (vtblInterfaces.get()).push(b);
                    }
                    (b.get()).copyBaseInterfaces(vtblInterfaces);
                }
            }
        }

        public BaseClass(){
            vtbl = new DArray<FuncDeclaration>();
        }
        public BaseClass copy(){
            BaseClass r = new BaseClass();
            r.type = type;
            r.sym = sym;
            r.offset = offset;
            r.vtbl = vtbl.copy();
            r.baseInterfaces = baseInterfaces.copy();
            return r;
        }
        public BaseClass opAssign(BaseClass that) {
            this.type = that.type;
            this.sym = that.sym;
            this.offset = that.offset;
            this.vtbl = that.vtbl;
            this.baseInterfaces = that.baseInterfaces;
            return this;
        }
    }

    public static class ClassFlags 
    {
        public static final int none = 0;
        public static final int isCOMclass = 1;
        public static final int noPointers = 2;
        public static final int hasOffTi = 4;
        public static final int hasCtor = 8;
        public static final int hasGetMembers = 16;
        public static final int hasTypeInfo = 32;
        public static final int isAbstract = 64;
        public static final int isCPPclass = 128;
        public static final int hasDtor = 256;
    }

    public static class ClassDeclaration extends AggregateDeclaration
    {
        public static ClassDeclaration object = null;
        public static ClassDeclaration throwable = null;
        public static ClassDeclaration exception = null;
        public static ClassDeclaration errorException = null;
        public static ClassDeclaration cpp_type_info_ptr = null;
        public ClassDeclaration baseClass = null;
        public FuncDeclaration staticCtor = null;
        public FuncDeclaration staticDtor = null;
        public Ref<DArray<Dsymbol>> vtbl = ref(new DArray<Dsymbol>());
        public Ref<DArray<Dsymbol>> vtblFinal = ref(new DArray<Dsymbol>());
        public Ptr<DArray<Ptr<BaseClass>>> baseclasses = null;
        public Slice<Ptr<BaseClass>> interfaces = new Slice<Ptr<BaseClass>>();
        public Ptr<DArray<Ptr<BaseClass>>> vtblInterfaces = null;
        public TypeInfoClassDeclaration vclassinfo = null;
        public boolean com = false;
        public boolean stack = false;
        public int cppDtorVtblIndex = -1;
        public boolean inuse = false;
        public boolean isActuallyAnonymous = false;
        public int isabstract = 0;
        public int baseok = 0;
        public ObjcClassDeclaration objc = new ObjcClassDeclaration();
        public Ptr<Symbol> cpp_type_info_ptr_sym = null;
        public  ClassDeclaration(Loc loc, Identifier id, Ptr<DArray<Ptr<BaseClass>>> baseclasses, Ptr<DArray<Dsymbol>> members, boolean inObject) {
            this.objc = new ObjcClassDeclaration(this);
            if (id == null)
            {
                this.isActuallyAnonymous = true;
            }
            super(loc, id != null ? id : Identifier.generateId(new BytePtr("__anonclass")));
            if (baseclasses != null)
            {
                this.baseclasses = baseclasses;
            }
            else
            {
                this.baseclasses = refPtr(new DArray<Ptr<BaseClass>>());
            }
            this.members = members;
            this.type = new TypeClass(this);
            if (id != null)
            {
                if ((pequals(id, Id.__sizeof)) || (pequals(id, Id.__xalignof)) || (pequals(id, Id._mangleof)))
                {
                    this.error(new BytePtr("illegal class name"));
                }
                if (((id.toChars().get(0) & 0xFF) == 84))
                {
                    if ((pequals(id, Id.TypeInfo)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), dclass.__ctormsg);
                        }
                        Type.dtypeinfo = this;
                    }
                    if ((pequals(id, Id.TypeInfo_Class)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), dclass.__ctormsg);
                        }
                        Type.typeinfoclass = this;
                    }
                    if ((pequals(id, Id.TypeInfo_Interface)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), dclass.__ctormsg);
                        }
                        Type.typeinfointerface = this;
                    }
                    if ((pequals(id, Id.TypeInfo_Struct)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), dclass.__ctormsg);
                        }
                        Type.typeinfostruct = this;
                    }
                    if ((pequals(id, Id.TypeInfo_Pointer)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), dclass.__ctormsg);
                        }
                        Type.typeinfopointer = this;
                    }
                    if ((pequals(id, Id.TypeInfo_Array)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), dclass.__ctormsg);
                        }
                        Type.typeinfoarray = this;
                    }
                    if ((pequals(id, Id.TypeInfo_StaticArray)))
                    {
                        Type.typeinfostaticarray = this;
                    }
                    if ((pequals(id, Id.TypeInfo_AssociativeArray)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), dclass.__ctormsg);
                        }
                        Type.typeinfoassociativearray = this;
                    }
                    if ((pequals(id, Id.TypeInfo_Enum)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), dclass.__ctormsg);
                        }
                        Type.typeinfoenum = this;
                    }
                    if ((pequals(id, Id.TypeInfo_Function)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), dclass.__ctormsg);
                        }
                        Type.typeinfofunction = this;
                    }
                    if ((pequals(id, Id.TypeInfo_Delegate)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), dclass.__ctormsg);
                        }
                        Type.typeinfodelegate = this;
                    }
                    if ((pequals(id, Id.TypeInfo_Tuple)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), dclass.__ctormsg);
                        }
                        Type.typeinfotypelist = this;
                    }
                    if ((pequals(id, Id.TypeInfo_Const)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), dclass.__ctormsg);
                        }
                        Type.typeinfoconst = this;
                    }
                    if ((pequals(id, Id.TypeInfo_Invariant)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), dclass.__ctormsg);
                        }
                        Type.typeinfoinvariant = this;
                    }
                    if ((pequals(id, Id.TypeInfo_Shared)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), dclass.__ctormsg);
                        }
                        Type.typeinfoshared = this;
                    }
                    if ((pequals(id, Id.TypeInfo_Wild)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), dclass.__ctormsg);
                        }
                        Type.typeinfowild = this;
                    }
                    if ((pequals(id, Id.TypeInfo_Vector)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), dclass.__ctormsg);
                        }
                        Type.typeinfovector = this;
                    }
                }
                if ((pequals(id, Id.Object)))
                {
                    if (!inObject)
                    {
                        this.error(new BytePtr("%s"), dclass.__ctormsg);
                    }
                    object = this;
                }
                if ((pequals(id, Id.Throwable)))
                {
                    if (!inObject)
                    {
                        this.error(new BytePtr("%s"), dclass.__ctormsg);
                    }
                    throwable = this;
                }
                if ((pequals(id, Id.Exception)))
                {
                    if (!inObject)
                    {
                        this.error(new BytePtr("%s"), dclass.__ctormsg);
                    }
                    exception = this;
                }
                if ((pequals(id, Id.Error)))
                {
                    if (!inObject)
                    {
                        this.error(new BytePtr("%s"), dclass.__ctormsg);
                    }
                    errorException = this;
                }
                if ((pequals(id, Id.cpp_type_info_ptr)))
                {
                    if (!inObject)
                    {
                        this.error(new BytePtr("%s"), dclass.__ctormsg);
                    }
                    cpp_type_info_ptr = this;
                }
            }
            this.baseok = Baseok.none;
        }

        public static ClassDeclaration create(Loc loc, Identifier id, Ptr<DArray<Ptr<BaseClass>>> baseclasses, Ptr<DArray<Dsymbol>> members, boolean inObject) {
            return new ClassDeclaration(loc, id, baseclasses, members, inObject);
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            ClassDeclaration cd = s != null ? (ClassDeclaration)s : new ClassDeclaration(this.loc, this.ident, null, null, false);
            cd.storage_class |= this.storage_class;
            (cd.baseclasses.get()).setDim((this.baseclasses.get()).length);
            {
                int i = 0;
                for (; (i < (cd.baseclasses.get()).length);i++){
                    Ptr<BaseClass> b = (this.baseclasses.get()).get(i);
                    Ptr<BaseClass> b2 = refPtr(new BaseClass((b.get()).type.syntaxCopy()));
                    cd.baseclasses.get().set(i, b2);
                }
            }
            return this.syntaxCopy(cd);
        }

        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            Ptr<Scope> sc2 = super.newScope(sc);
            if (this.isCOMclass())
            {
                (sc2.get()).linkage = target.systemLinkage();
            }
            return sc2;
        }

        public  boolean isBaseOf2(ClassDeclaration cd) {
            if (cd == null)
            {
                return false;
            }
            {
                int i = 0;
                for (; (i < (cd.baseclasses.get()).length);i++){
                    Ptr<BaseClass> b = (cd.baseclasses.get()).get(i);
                    if ((pequals((b.get()).sym, this)) || this.isBaseOf2((b.get()).sym))
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public int OFFSET_RUNTIME = 1985229328;
        public int OFFSET_FWDREF = 1985229329;
        public  boolean isBaseOf(ClassDeclaration cd, Ptr<Integer> poffset) {
            if (poffset != null)
            {
                poffset.set(0, 0);
            }
            for (; cd != null;){
                if ((cd.baseClass == null) && (cd.semanticRun < PASS.semanticdone) && (cd.isInterfaceDeclaration() == null))
                {
                    dsymbolSemantic(cd, null);
                    if ((cd.baseClass == null) && (cd.semanticRun < PASS.semanticdone))
                    {
                        cd.error(new BytePtr("base class is forward referenced by `%s`"), this.toChars());
                    }
                }
                if ((pequals(this, cd.baseClass)))
                {
                    return true;
                }
                cd = cd.baseClass;
            }
            return false;
        }

        public  boolean isBaseInfoComplete() {
            return this.baseok >= Baseok.done;
        }

        public  Dsymbol search(Loc loc, Identifier ident, int flags) {
            if ((this._scope != null) && (this.baseok < Baseok.done))
            {
                if (!this.inuse)
                {
                    this.inuse = true;
                    dsymbolSemantic(this, null);
                    this.inuse = false;
                }
            }
            if ((this.members == null) || (this.symtab == null))
            {
                this.error(new BytePtr("is forward referenced when looking for `%s`"), ident.toChars());
                return null;
            }
            Dsymbol s = this.search(loc, ident, flags);
            if ((flags & 16) != 0)
            {
                return s;
            }
            if (s == null)
            {
                {
                    int i = 0;
                    for (; (i < (this.baseclasses.get()).length);i++){
                        Ptr<BaseClass> b = (this.baseclasses.get()).get(i);
                        if ((b.get()).sym != null)
                        {
                            if ((b.get()).sym.symtab == null)
                            {
                                this.error(new BytePtr("base `%s` is forward referenced"), (b.get()).sym.ident.toChars());
                            }
                            else
                            {
                                s = (b.get()).sym.search(loc, ident, flags);
                                if (s == null)
                                {
                                    continue;
                                }
                                else if ((pequals(s, this)))
                                {
                                    s = null;
                                }
                                else if (((flags & 128) == 0) && !(s.prot().kind == Prot.Kind.protected_) && !symbolIsVisible(this, s))
                                {
                                    s = null;
                                }
                                else
                                {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            return s;
        }

        // defaulted all parameters starting with #3
        public  Dsymbol search(Loc loc, Identifier ident) {
            return search(loc, ident, 8);
        }

        public  ClassDeclaration searchBase(Identifier ident) {
            {
                Slice<Ptr<BaseClass>> __r901 = (this.baseclasses.get()).opSlice().copy();
                int __key902 = 0;
                for (; (__key902 < __r901.getLength());__key902 += 1) {
                    Ptr<BaseClass> b = __r901.get(__key902);
                    ClassDeclaration cdb = (b.get()).type.isClassHandle();
                    if (cdb == null)
                    {
                        return null;
                    }
                    if (cdb.ident.equals(ident))
                    {
                        return cdb;
                    }
                    ClassDeclaration result = cdb.searchBase(ident);
                    if (result != null)
                    {
                        return result;
                    }
                }
            }
            return null;
        }

        public  void finalizeSize() {
            assert((this.sizeok != Sizeok.done));
            if (this.baseClass != null)
            {
                assert((this.baseClass.sizeok == Sizeok.done));
                this.alignsize.value = this.baseClass.alignsize.value;
                this.structsize.value = this.baseClass.structsize.value;
                if ((this.classKind == ClassKind.cpp) && global.params.isWindows)
                {
                    this.structsize.value = this.structsize.value + this.alignsize.value - 1 & ~(this.alignsize.value - 1);
                }
            }
            else if (this.isInterfaceDeclaration() != null)
            {
                if ((this.interfaces.getLength() == 0))
                {
                    this.alignsize.value = target.ptrsize;
                    this.structsize.value = target.ptrsize;
                }
            }
            else
            {
                this.alignsize.value = target.ptrsize;
                this.structsize.value = target.ptrsize;
                if (this.hasMonitor())
                {
                    this.structsize.value += target.ptrsize;
                }
            }
            Ref<Integer> bi = ref(0);
            Function2<ClassDeclaration,Integer,Integer> membersPlace = (cd, baseOffset) -> {
             {
                Ref<Integer> offset = ref(baseOffset);
                {
                    Slice<Ptr<BaseClass>> __r903 = cd.interfaces.copy();
                    Ref<Integer> __key904 = ref(0);
                    for (; (__key904.value < __r903.getLength());__key904.value += 1) {
                        Ptr<BaseClass> b = __r903.get(__key904.value);
                        if (((b.get()).sym.sizeok != Sizeok.done))
                        {
                            (b.get()).sym.finalizeSize();
                        }
                        assert(((b.get()).sym.sizeok == Sizeok.done));
                        if ((b.get()).sym.alignsize.value == 0)
                        {
                            (b.get()).sym.alignsize.value = target.ptrsize;
                        }
                        AggregateDeclaration.alignmember((b.get()).sym.alignsize.value, (b.get()).sym.alignsize.value, ptr(offset));
                        assert((bi.value < (vtblInterfaces.get()).length));
                        Ptr<BaseClass> bv = (vtblInterfaces.get()).get(bi.value);
                        if (((b.get()).sym.interfaces.getLength() == 0))
                        {
                            (bv.get()).offset = offset.value;
                            bi.value += 1;
                            {
                                Ref<Ptr<BaseClass>> b2 = ref(bv);
                                for (; (b2.value.get()).baseInterfaces.getLength() != 0;){
                                    b2.value = ptr((b2.value.get()).baseInterfaces.get(0));
                                    (b2.value.get()).offset = offset.value;
                                }
                            }
                        }
                        membersPlace.invoke((b.get()).sym, offset.value);
                        offset.value += (b.get()).sym.structsize.value;
                        if ((alignsize.value < (b.get()).sym.alignsize.value))
                        {
                            alignsize.value = (b.get()).sym.alignsize.value;
                        }
                    }
                }
                return offset.value - baseOffset;
            }
            };
            this.structsize.value += membersPlace.invoke(this, this.structsize.value);
            if (this.isInterfaceDeclaration() != null)
            {
                this.sizeok = Sizeok.done;
                return ;
            }
            this.fields.setDim(0);
            Ref<Integer> offset = ref(this.structsize.value);
            {
                Slice<Dsymbol> __r905 = (this.members.get()).opSlice().copy();
                int __key906 = 0;
                for (; (__key906 < __r905.getLength());__key906 += 1) {
                    Dsymbol s = __r905.get(__key906);
                    s.setFieldOffset(this, ptr(offset), false);
                }
            }
            this.sizeok = Sizeok.done;
            this.checkOverlappedFields();
        }

        public  boolean hasMonitor() {
            return this.classKind == ClassKind.d;
        }

        public  boolean isAnonymous() {
            return this.isActuallyAnonymous;
        }

        public  boolean isFuncHidden(FuncDeclaration fd) {
            Dsymbol s = this.search(Loc.initial, fd.ident, 6);
            if (s == null)
            {
                return false;
            }
            s = s.toAlias();
            {
                OverloadSet os = s.isOverloadSet();
                if ((os) != null)
                {
                    {
                        Slice<Dsymbol> __r907 = os.a.opSlice().copy();
                        int __key908 = 0;
                        for (; (__key908 < __r907.getLength());__key908 += 1) {
                            Dsymbol sm = __r907.get(__key908);
                            FuncDeclaration fm = sm.isFuncDeclaration();
                            if (overloadApply(fm, __lambda2, null) != 0)
                            {
                                return false;
                            }
                        }
                    }
                    return true;
                }
                else
                {
                    FuncDeclaration f = s.isFuncDeclaration();
                    if (overloadApply(f, __lambda3, null) != 0)
                    {
                        return false;
                    }
                    return fd.parent.value.isTemplateMixin() == null;
                }
            }
        }

        public  FuncDeclaration findFunc(Identifier ident, TypeFunction tf) {
            Ref<FuncDeclaration> fdmatch = ref(null);
            Ref<FuncDeclaration> fdambig = ref(null);
            Function1<FuncDeclaration,Void> updateBestMatch = (fd) -> {
             {
                fdmatch.value = fd;
                fdambig.value = null;
                return null;
            }
            };
            Function1<Ref<DArray<Dsymbol>>,Void> searchVtbl = (vtbl) -> {
             {
                {
                    Slice<Dsymbol> __r909 = vtbl.opSlice().copy();
                    Ref<Integer> __key910 = ref(0);
                    for (; (__key910.value < __r909.getLength());__key910.value += 1) {
                        Dsymbol s = __r909.get(__key910.value);
                        FuncDeclaration fd = s.isFuncDeclaration();
                        if (fd == null)
                        {
                            continue;
                        }
                        if ((pequals(ident, fd.ident)) && (fd.type.covariant(tf, null, true) == 1))
                        {
                            if (fdmatch.value == null)
                            {
                                updateBestMatch.invoke(fd);
                                continue;
                            }
                            if ((pequals(fd, fdmatch.value)))
                            {
                                continue;
                            }
                            {
                                int m1 = tf.equals(fd.type) ? MATCH.exact : MATCH.nomatch;
                                int m2 = tf.equals(fdmatch.value.type) ? MATCH.exact : MATCH.nomatch;
                                if ((m1 > m2))
                                {
                                    updateBestMatch.invoke(fd);
                                    continue;
                                }
                                else if ((m1 < m2))
                                {
                                    continue;
                                }
                            }
                            {
                                int m1 = ((tf.mod & 0xFF) == (fd.type.mod & 0xFF)) ? MATCH.exact : MATCH.nomatch;
                                int m2 = ((tf.mod & 0xFF) == (fdmatch.value.type.mod & 0xFF)) ? MATCH.exact : MATCH.nomatch;
                                if ((m1 > m2))
                                {
                                    updateBestMatch.invoke(fd);
                                    continue;
                                }
                                else if ((m1 < m2))
                                {
                                    continue;
                                }
                            }
                            {
                                int m1 = fd.parent.value.isClassDeclaration() != null ? MATCH.exact : MATCH.nomatch;
                                int m2 = fdmatch.value.parent.value.isClassDeclaration() != null ? MATCH.exact : MATCH.nomatch;
                                if ((m1 > m2))
                                {
                                    updateBestMatch.invoke(fd);
                                    continue;
                                }
                                else if ((m1 < m2))
                                {
                                    continue;
                                }
                            }
                            fdambig.value = fd;
                        }
                    }
                }
                return null;
            }
            };
            searchVtbl.invoke(vtbl);
            {
                ClassDeclaration cd = this;
                for (; cd != null;cd = cd.baseClass){
                    searchVtbl.invoke(vtblFinal);
                }
            }
            if (fdambig.value != null)
            {
                this.error(new BytePtr("ambiguous virtual function `%s`"), fdambig.value.toChars());
            }
            return fdmatch.value;
        }

        public  boolean isCOMclass() {
            return this.com;
        }

        public  boolean isCOMinterface() {
            return false;
        }

        public  boolean isCPPclass() {
            return this.classKind == ClassKind.cpp;
        }

        public  boolean isCPPinterface() {
            return false;
        }

        public  boolean isAbstract() {
            boolean log = false;
            if ((this.isabstract != Abstract.fwdref))
            {
                return this.isabstract == Abstract.yes;
            }
            if (false)
            {
                printf(new BytePtr("isAbstract(%s)\n"), this.toChars());
            }
            Function0<Boolean> no = () -> {
             {
                if (false)
                {
                    printf(new BytePtr("no\n"));
                }
                isabstract = Abstract.no;
                return false;
            }
            };
            Function0<Boolean> yes = () -> {
             {
                if (false)
                {
                    printf(new BytePtr("yes\n"));
                }
                isabstract = Abstract.yes;
                return true;
            }
            };
            if (((this.storage_class & 16L) != 0) || (this._scope != null) && (((this._scope.get()).stc & 16L) != 0))
            {
                return yes.invoke();
            }
            if (this.errors)
            {
                return no.invoke();
            }
            Function2<Dsymbol,Object,Integer> func = (s, param) -> {
             {
                FuncDeclaration fd = s.isFuncDeclaration();
                if (fd == null)
                {
                    return 0;
                }
                if ((fd.storage_class & 1L) != 0)
                {
                    return 0;
                }
                if (fd.isAbstract())
                {
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
                        return yes.invoke();
                    }
                }
            }
            if ((this.isInterfaceDeclaration() == null) && (this.baseClass == null) || !this.baseClass.isAbstract())
            {
                return no.invoke();
            }
            dsymbolSemantic(this, null);
            {
                Function2<Dsymbol,Object,Integer> virtualSemantic = (s, param) -> {
                 {
                    FuncDeclaration fd = s.isFuncDeclaration();
                    if ((fd != null) && ((fd.storage_class & 1L) == 0) && (fd.isUnitTestDeclaration() == null))
                    {
                        dsymbolSemantic(fd, null);
                    }
                    return 0;
                }
                };
                {
                    int i = 0;
                    for (; (i < (this.members.get()).length);i++){
                        Dsymbol s = (this.members.get()).get(i);
                        s.apply(virtualSemantic, this);
                    }
                }
            }
            {
                int __key911 = 1;
                int __limit912 = this.vtbl.value.length;
                for (; (__key911 < __limit912);__key911 += 1) {
                    int i = __key911;
                    FuncDeclaration fd = this.vtbl.value.get(i).isFuncDeclaration();
                    if ((fd == null) || fd.isAbstract())
                    {
                        return yes.invoke();
                    }
                }
            }
            return no.invoke();
        }

        public  int vtblOffset() {
            return (this.classKind == ClassKind.cpp) ? 0 : 1;
        }

        public  BytePtr kind() {
            return new BytePtr("class");
        }

        public  void addLocalClass(Ptr<DArray<ClassDeclaration>> aclasses) {
            if ((this.classKind != ClassKind.objc))
            {
                (aclasses.get()).push(this);
            }
        }

        public  void addObjcSymbols(Ptr<DArray<ClassDeclaration>> classes, Ptr<DArray<ClassDeclaration>> categories) {
            objc().addSymbols(this, classes, categories);
        }

        public Dsymbol vtblsym = null;
        public  Dsymbol vtblSymbol() {
            if (this.vtblsym == null)
            {
                Type vtype = Type.tvoidptr.immutableOf().sarrayOf((long)this.vtbl.value.length);
                VarDeclaration var = new VarDeclaration(this.loc, vtype, Identifier.idPool(new ByteSlice("__vtbl")), null, 1048577L);
                var.addMember(null, this);
                var.isdataseg = (byte)1;
                var.linkage = LINK.d;
                var.semanticRun = PASS.semanticdone;
                this.vtblsym = var;
            }
            return this.vtblsym;
        }

        public  ClassDeclaration isClassDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ClassDeclaration() {}

        public ClassDeclaration copy() {
            ClassDeclaration that = new ClassDeclaration();
            that.baseClass = this.baseClass;
            that.staticCtor = this.staticCtor;
            that.staticDtor = this.staticDtor;
            that.vtbl = this.vtbl;
            that.vtblFinal = this.vtblFinal;
            that.baseclasses = this.baseclasses;
            that.interfaces = this.interfaces;
            that.vtblInterfaces = this.vtblInterfaces;
            that.vclassinfo = this.vclassinfo;
            that.com = this.com;
            that.stack = this.stack;
            that.cppDtorVtblIndex = this.cppDtorVtblIndex;
            that.inuse = this.inuse;
            that.isActuallyAnonymous = this.isActuallyAnonymous;
            that.isabstract = this.isabstract;
            that.baseok = this.baseok;
            that.objc = this.objc;
            that.cpp_type_info_ptr_sym = this.cpp_type_info_ptr_sym;
            that.OFFSET_RUNTIME = this.OFFSET_RUNTIME;
            that.OFFSET_FWDREF = this.OFFSET_FWDREF;
            that.vtblsym = this.vtblsym;
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
    public static class InterfaceDeclaration extends ClassDeclaration
    {
        public  InterfaceDeclaration(Loc loc, Identifier id, Ptr<DArray<Ptr<BaseClass>>> baseclasses) {
            super(loc, id, baseclasses, null, false);
            if ((pequals(id, Id.IUnknown)))
            {
                this.com = true;
                this.classKind = ClassKind.cpp;
            }
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            InterfaceDeclaration id = s != null ? (InterfaceDeclaration)s : new InterfaceDeclaration(this.loc, this.ident, null);
            return this.syntaxCopy(id);
        }

        public  Ptr<Scope> newScope(Ptr<Scope> sc) {
            Ptr<Scope> sc2 = super.newScope(sc);
            if (this.com)
            {
                (sc2.get()).linkage = LINK.windows;
            }
            else if ((this.classKind == ClassKind.cpp))
            {
                (sc2.get()).linkage = LINK.cpp;
            }
            else if ((this.classKind == ClassKind.objc))
            {
                (sc2.get()).linkage = LINK.objc;
            }
            return sc2;
        }

        public  boolean isBaseOf(ClassDeclaration cd, Ptr<Integer> poffset) {
            assert(this.baseClass == null);
            {
                Slice<Ptr<BaseClass>> __r913 = cd.interfaces.copy();
                int __key914 = 0;
                for (; (__key914 < __r913.getLength());__key914 += 1) {
                    Ptr<BaseClass> b = __r913.get(__key914);
                    if ((pequals(this, (b.get()).sym)))
                    {
                        if (poffset != null)
                        {
                            poffset.set(0, ((cd.sizeok == Sizeok.done) ? (b.get()).offset : 1985229329));
                        }
                        return true;
                    }
                    if (this.isBaseOf(b, poffset))
                    {
                        return true;
                    }
                }
            }
            if ((cd.baseClass != null) && this.isBaseOf(cd.baseClass, poffset))
            {
                return true;
            }
            if (poffset != null)
            {
                poffset.set(0, 0);
            }
            return false;
        }

        public  boolean isBaseOf(Ptr<BaseClass> bc, Ptr<Integer> poffset) {
            {
                int j = 0;
                for (; (j < (bc.get()).baseInterfaces.getLength());j++){
                    Ptr<BaseClass> b = ptr((bc.get()).baseInterfaces.get(j));
                    if ((pequals(this, (b.get()).sym)))
                    {
                        if (poffset != null)
                        {
                            poffset.set(0, ((b.get()).offset));
                        }
                        return true;
                    }
                    if (this.isBaseOf(b, poffset))
                    {
                        return true;
                    }
                }
            }
            if (poffset != null)
            {
                poffset.set(0, 0);
            }
            return false;
        }

        public  BytePtr kind() {
            return new BytePtr("interface");
        }

        public  int vtblOffset() {
            if (this.isCOMinterface() || this.isCPPinterface())
            {
                return 0;
            }
            return 1;
        }

        public  boolean isCPPinterface() {
            return this.classKind == ClassKind.cpp;
        }

        public  boolean isCOMinterface() {
            return this.com;
        }

        public  InterfaceDeclaration isInterfaceDeclaration() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public InterfaceDeclaration() {}

        public InterfaceDeclaration copy() {
            InterfaceDeclaration that = new InterfaceDeclaration();
            that.baseClass = this.baseClass;
            that.staticCtor = this.staticCtor;
            that.staticDtor = this.staticDtor;
            that.vtbl = this.vtbl;
            that.vtblFinal = this.vtblFinal;
            that.baseclasses = this.baseclasses;
            that.interfaces = this.interfaces;
            that.vtblInterfaces = this.vtblInterfaces;
            that.vclassinfo = this.vclassinfo;
            that.com = this.com;
            that.stack = this.stack;
            that.cppDtorVtblIndex = this.cppDtorVtblIndex;
            that.inuse = this.inuse;
            that.isActuallyAnonymous = this.isActuallyAnonymous;
            that.isabstract = this.isabstract;
            that.baseok = this.baseok;
            that.objc = this.objc;
            that.cpp_type_info_ptr_sym = this.cpp_type_info_ptr_sym;
            that.OFFSET_RUNTIME = this.OFFSET_RUNTIME;
            that.OFFSET_FWDREF = this.OFFSET_FWDREF;
            that.vtblsym = this.vtblsym;
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
