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
import static org.dlang.dmd.cond.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dmangle.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.gluelayer.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.tokens.*;

public class objc {

    public static class ObjcSelector
    {
        public static StringTable stringtable = new StringTable();
        public static StringTable vTableDispatchSelectors = new StringTable();
        public static int incnum = 0;
        public BytePtr stringvalue = null;
        public int stringlen = 0;
        public int paramCount = 0;
        // Erasure: _init<>
        public static void _init() {
            stringtable._init(0);
        }

        // Erasure: __ctor<Ptr, int, int>
        public  ObjcSelector(BytePtr sv, int len, int pcount) {
            this.stringvalue = pcopy(sv);
            this.stringlen = len;
            this.paramCount = pcount;
        }

        // Erasure: lookup<Ptr>
        public static Ptr<ObjcSelector> lookup(BytePtr s) {
            int len = 0;
            int pcount = 0;
            BytePtr i = pcopy(s);
            for (; ((i.get() & 0xFF) != 0);){
                len += 1;
                if (((i.get() & 0xFF) == 58))
                {
                    pcount += 1;
                }
                i.plusAssign(1);
            }
            return lookup(s, len, pcount);
        }

        // Erasure: lookup<Ptr, int, int>
        public static Ptr<ObjcSelector> lookup(BytePtr s, int len, int pcount) {
            Ptr<StringValue> sv = stringtable.update(s, len);
            Ptr<ObjcSelector> sel = ((Ptr<ObjcSelector>)(sv.get()).ptrvalue);
            if (sel == null)
            {
                sel = pcopy((refPtr(new ObjcSelector((sv.get()).toDchars(), len, pcount))));
                (sv.get()).ptrvalue = pcopy((toBytePtr(sel)));
            }
            return sel;
        }

        // Erasure: create<FuncDeclaration>
        public static Ptr<ObjcSelector> create(FuncDeclaration fdecl) {
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                int pcount = 0;
                TypeFunction ftype = (TypeFunction)fdecl.type;
                ByteSlice id = fdecl.ident.asString().copy();
                try {
                    if (ftype.isproperty && (ftype.parameterList.parameters != null) && ((ftype.parameterList.parameters.get()).length == 1))
                    {
                        byte firstChar = id.get(0);
                        if (((firstChar & 0xFF) >= 97) && ((firstChar & 0xFF) <= 122))
                        {
                            firstChar = (byte)((firstChar & 0xFF) - 97 + 65);
                        }
                        buf.value.writestring(new ByteSlice("set"));
                        buf.value.writeByte((firstChar & 0xFF));
                        buf.value.write((id.getPtr(0).plus(1)), id.getLength() - 1);
                        buf.value.writeByte(58);
                        /*goto Lcomplete*/throw Dispatch0.INSTANCE;
                    }
                    buf.value.write(id.getPtr(0), id.getLength());
                    if ((ftype.parameterList.parameters != null) && ((ftype.parameterList.parameters.get()).length != 0))
                    {
                        buf.value.writeByte(95);
                        Ptr<DArray<Parameter>> arguments = ftype.parameterList.parameters;
                        int dim = Parameter.dim(arguments);
                        {
                            int i = 0;
                            for (; (i < dim);i++){
                                Parameter arg = Parameter.getNth(arguments, i, null);
                                mangleToBuffer(arg.type, ptr(buf));
                                buf.value.writeByte(58);
                            }
                        }
                        pcount = dim;
                    }
                }
                catch(Dispatch0 __d){}
            /*Lcomplete:*/
                buf.value.writeByte(0);
                return lookup(toBytePtr(buf.value.data), buf.value.size, pcount);
            }
            finally {
            }
        }

        // Erasure: asString<>
        public  ByteSlice asString() {
            return this.stringvalue.slice(0,this.stringlen);
        }

        public ObjcSelector(){ }
        public ObjcSelector copy(){
            ObjcSelector r = new ObjcSelector();
            r.stringvalue = stringvalue;
            r.stringlen = stringlen;
            r.paramCount = paramCount;
            return r;
        }
        public ObjcSelector opAssign(ObjcSelector that) {
            this.stringvalue = that.stringvalue;
            this.stringlen = that.stringlen;
            this.paramCount = that.paramCount;
            return this;
        }
    }
    static Objc _objc = null;
    // Erasure: objc<>
    public static Objc objc() {
        return _objc;
    }

    public static class ObjcClassDeclaration
    {
        public boolean isMeta = false;
        public boolean isExtern = false;
        public Identifier identifier = null;
        public ClassDeclaration classDeclaration = null;
        public ClassDeclaration metaclass = null;
        public Ptr<DArray<Dsymbol>> methodList = null;
        // Erasure: __ctor<ClassDeclaration>
        public  ObjcClassDeclaration(ClassDeclaration classDeclaration) {
            this.classDeclaration = classDeclaration;
            this.methodList = pcopy((refPtr(new DArray<Dsymbol>())));
        }

        // Erasure: isRootClass<>
        public  boolean isRootClass() {
            return (this.classDeclaration.classKind == ClassKind.objc) && (this.metaclass == null) && (this.classDeclaration.baseClass == null);
        }

        public ObjcClassDeclaration(){ }
        public ObjcClassDeclaration copy(){
            ObjcClassDeclaration r = new ObjcClassDeclaration();
            r.isMeta = isMeta;
            r.isExtern = isExtern;
            r.identifier = identifier;
            r.classDeclaration = classDeclaration;
            r.metaclass = metaclass;
            r.methodList = methodList;
            return r;
        }
        public ObjcClassDeclaration opAssign(ObjcClassDeclaration that) {
            this.isMeta = that.isMeta;
            this.isExtern = that.isExtern;
            this.identifier = that.identifier;
            this.classDeclaration = that.classDeclaration;
            this.metaclass = that.metaclass;
            this.methodList = that.methodList;
            return this;
        }
    }
    public static abstract class Objc
    {
        // Erasure: _init<>
        public static void _init() {
            if (global.params.isOSX && global.params.is64bit)
            {
                _objc = new Supported();
            }
            else
            {
                _objc = new Unsupported();
            }
        }

        // Erasure: deinitialize<>
        public static void deinitialize() {
            _objc = null;
        }

        // Erasure: setObjc<>
        public abstract void setObjc(ClassDeclaration cd);


        // Erasure: setObjc<>
        public abstract void setObjc(InterfaceDeclaration arg0, ETag1 __tag);


        // Erasure: deprecate<>
        public abstract void deprecate(InterfaceDeclaration interfaceDeclaration);


        // Erasure: setSelector<>
        public abstract void setSelector(FuncDeclaration arg0, Ptr<Scope> sc);


        // Erasure: validateSelector<>
        public abstract void validateSelector(FuncDeclaration fd);


        // Erasure: checkLinkage<>
        public abstract void checkLinkage(FuncDeclaration fd);


        // Erasure: isVirtual<>
        public abstract boolean isVirtual(FuncDeclaration fd);


        // Erasure: getParent<>
        public abstract ClassDeclaration getParent(FuncDeclaration fd, ClassDeclaration cd);


        // Erasure: addToClassMethodList<>
        public abstract void addToClassMethodList(FuncDeclaration fd, ClassDeclaration cd);


        // Erasure: isThis<>
        public abstract AggregateDeclaration isThis(FuncDeclaration funcDeclaration);


        // Erasure: createSelectorParameter<>
        public abstract VarDeclaration createSelectorParameter(FuncDeclaration fd, Ptr<Scope> sc);


        // Erasure: setMetaclass<>
        public abstract void setMetaclass(InterfaceDeclaration interfaceDeclaration, Ptr<Scope> sc);


        // Erasure: setMetaclass<>
        public abstract void setMetaclass(ClassDeclaration classDeclaration, Ptr<Scope> sc, ETag1 __tag);


        // Erasure: getRuntimeMetaclass<>
        public abstract ClassDeclaration getRuntimeMetaclass(ClassDeclaration classDeclaration);


        // Erasure: addSymbols<>
        public abstract void addSymbols(AttribDeclaration attribDeclaration, Ptr<DArray<ClassDeclaration>> classes, Ptr<DArray<ClassDeclaration>> categories);


        // Erasure: addSymbols<>
        public abstract void addSymbols(ClassDeclaration classDeclaration, Ptr<DArray<ClassDeclaration>> classes, Ptr<DArray<ClassDeclaration>> categories, ETag1 __tag);


        // Erasure: checkOffsetof<>
        public abstract void checkOffsetof(Expression expression, AggregateDeclaration aggregateDeclaration);


        // Erasure: checkTupleof<>
        public abstract void checkTupleof(Expression expression, TypeClass type);



        public Objc() {}

        public abstract Objc copy();
    }
    public static class Unsupported extends Objc
    {
        // Erasure: __ctor<>
        public  Unsupported() {
            ObjcGlue.initialize();
        }

        // Erasure: setObjc<ClassDeclaration>
        public  void setObjc(ClassDeclaration cd) {
            cd.error(new BytePtr("Objective-C classes not supported"));
        }

        // Erasure: setObjc<InterfaceDeclaration>
        public  void setObjc(InterfaceDeclaration id) {
            id.error(new BytePtr("Objective-C interfaces not supported"));
        }

        // Erasure: deprecate<InterfaceDeclaration>
        public  void deprecate(InterfaceDeclaration _param_0) {
        }

        // Erasure: setSelector<FuncDeclaration, Ptr>
        public  void setSelector(FuncDeclaration _param_0, Ptr<Scope> _param_1) {
        }

        // Erasure: validateSelector<FuncDeclaration>
        public  void validateSelector(FuncDeclaration _param_0) {
        }

        // Erasure: checkLinkage<FuncDeclaration>
        public  void checkLinkage(FuncDeclaration _param_0) {
        }

        // Erasure: isVirtual<FuncDeclaration>
        public  boolean isVirtual(FuncDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: getParent<FuncDeclaration, ClassDeclaration>
        public  ClassDeclaration getParent(FuncDeclaration _param_0, ClassDeclaration cd) {
            return cd;
        }

        // Erasure: addToClassMethodList<FuncDeclaration, ClassDeclaration>
        public  void addToClassMethodList(FuncDeclaration _param_0, ClassDeclaration _param_1) {
        }

        // Erasure: isThis<FuncDeclaration>
        public  AggregateDeclaration isThis(FuncDeclaration funcDeclaration) {
            return null;
        }

        // Erasure: createSelectorParameter<FuncDeclaration, Ptr>
        public  VarDeclaration createSelectorParameter(FuncDeclaration _param_0, Ptr<Scope> _param_1) {
            return null;
        }

        // Erasure: setMetaclass<InterfaceDeclaration, Ptr>
        public  void setMetaclass(InterfaceDeclaration _param_0, Ptr<Scope> _param_1) {
        }

        // Erasure: setMetaclass<ClassDeclaration, Ptr>
        public  void setMetaclass(ClassDeclaration _param_0, Ptr<Scope> _param_1) {
        }

        // Erasure: getRuntimeMetaclass<ClassDeclaration>
        public  ClassDeclaration getRuntimeMetaclass(ClassDeclaration classDeclaration) {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: addSymbols<AttribDeclaration, Ptr, Ptr>
        public  void addSymbols(AttribDeclaration attribDeclaration, Ptr<DArray<ClassDeclaration>> classes, Ptr<DArray<ClassDeclaration>> categories) {
        }

        // Erasure: addSymbols<ClassDeclaration, Ptr, Ptr>
        public  void addSymbols(ClassDeclaration classDeclaration, Ptr<DArray<ClassDeclaration>> classes, Ptr<DArray<ClassDeclaration>> categories) {
        }

        // Erasure: checkOffsetof<Expression, AggregateDeclaration>
        public  void checkOffsetof(Expression expression, AggregateDeclaration aggregateDeclaration) {
        }

        // Erasure: checkTupleof<Expression, TypeClass>
        public  void checkTupleof(Expression expression, TypeClass type) {
        }


        public Unsupported copy() {
            Unsupported that = new Unsupported();
            return that;
        }
    }
    public static class Supported extends Objc
    {
        // Erasure: __ctor<>
        public  Supported() {
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("D_ObjectiveC"));
            ObjcGlue.initialize();
            ObjcSelector._init();
        }

        // Erasure: setObjc<ClassDeclaration>
        public  void setObjc(ClassDeclaration cd) {
            cd.classKind = ClassKind.objc;
            cd.objc.isExtern = (cd.storage_class & 2L) > 0L;
        }

        // Erasure: setObjc<InterfaceDeclaration>
        public  void setObjc(InterfaceDeclaration id) {
            id.classKind = ClassKind.objc;
            id.objc.isExtern = true;
        }

        // Erasure: deprecate<InterfaceDeclaration>
        public  void deprecate(InterfaceDeclaration id) {
            if (id.objc.isMeta)
            {
                return ;
            }
            id.deprecation(new BytePtr("Objective-C interfaces have been deprecated"));
            deprecationSupplemental(id.loc, new BytePtr("Representing an Objective-C class as a D interface has been deprecated. Please use `extern (Objective-C) extern class` instead"));
        }

        // Erasure: setSelector<FuncDeclaration, Ptr>
        public  void setSelector(FuncDeclaration fd, Ptr<Scope> sc) {
            if (fd.userAttribDecl == null)
            {
                return ;
            }
            Ptr<DArray<Expression>> udas = fd.userAttribDecl.getAttributes();
            arrayExpressionSemantic(udas, sc, true);
            {
                int i = 0;
                for (; (i < (udas.get()).length);i++){
                    Expression uda = (udas.get()).get(i);
                    assert(uda != null);
                    if (((uda.op & 0xFF) != 126))
                    {
                        continue;
                    }
                    Ptr<DArray<Expression>> exps = ((TupleExp)uda).exps;
                    {
                        int j = 0;
                        for (; (j < (exps.get()).length);j++){
                            Expression e = (exps.get()).get(j);
                            assert(e != null);
                            if (((e.op & 0xFF) != 49))
                            {
                                continue;
                            }
                            StructLiteralExp literal = (StructLiteralExp)e;
                            assert(literal.sd != null);
                            if (!this.isUdaSelector(literal.sd))
                            {
                                continue;
                            }
                            if (fd.selector != null)
                            {
                                fd.error(new BytePtr("can only have one Objective-C selector per method"));
                                return ;
                            }
                            assert(((literal.elements.get()).length == 1));
                            StringExp se = (literal.elements.get()).get(0).toStringExp();
                            assert(se != null);
                            fd.selector = pcopy(ObjcSelector.lookup(se.toUTF8(sc).string));
                        }
                    }
                }
            }
        }

        // Erasure: validateSelector<FuncDeclaration>
        public  void validateSelector(FuncDeclaration fd) {
            if (fd.selector == null)
            {
                return ;
            }
            TypeFunction tf = (TypeFunction)fd.type;
            if (((fd.selector.get()).paramCount != (tf.parameterList.parameters.get()).length))
            {
                fd.error(new BytePtr("number of colons in Objective-C selector must match number of parameters"));
            }
            if ((fd.parent.value != null) && (fd.parent.value.isTemplateInstance() != null))
            {
                fd.error(new BytePtr("template cannot have an Objective-C selector attached"));
            }
        }

        // Erasure: checkLinkage<FuncDeclaration>
        public  void checkLinkage(FuncDeclaration fd) {
            if ((fd.linkage != LINK.objc) && (fd.selector != null))
            {
                fd.error(new BytePtr("must have Objective-C linkage to attach a selector"));
            }
        }

        // Erasure: isVirtual<FuncDeclaration>
        public  boolean isVirtual(FuncDeclaration fd) {
            return !(((__withSym.get()).kind == Prot.Kind.private_) || ((__withSym.get()).kind == Prot.Kind.package_));
        }

        // Erasure: getParent<FuncDeclaration, ClassDeclaration>
        public  ClassDeclaration getParent(FuncDeclaration fd, ClassDeclaration cd) {
            ClassDeclaration __result = null;
            Ref<FuncDeclaration> fd_ref = ref(fd);
            Ref<ClassDeclaration> cd_ref = ref(cd);
            try {
                if ((cd_ref.value.classKind == ClassKind.objc) && fd_ref.value.isStatic() && !cd_ref.value.objc.isMeta)
                {
                    __result = cd_ref.value.objc.metaclass;
                    /*goto __returnLabel*/throw Dispatch0.INSTANCE;
                }
                else
                {
                    __result = cd_ref.value;
                    /*goto __returnLabel*/throw Dispatch0.INSTANCE;
                }
            }
            catch(Dispatch0 __d){}
        /*__returnLabel:*/
            Function3<Ref<ClassDeclaration>,Ref<FuncDeclaration>,Ref<ClassDeclaration>,Void> __ensure = new Function3<Ref<ClassDeclaration>,Ref<FuncDeclaration>,Ref<ClassDeclaration>,Void>() {
                public Void invoke(ClassDeclaration __result, Ref<FuncDeclaration> fd, Ref<ClassDeclaration> cd) {
                 {
                    {
                        ClassDeclaration metaclass = __result;
                        {
                            assert(metaclass != null);
                        }
                    }
                    return null;
                }}

            };
            __ensure.invoke(__result, fd_ref, cd_ref);
            return __result;
        }

        // Erasure: addToClassMethodList<FuncDeclaration, ClassDeclaration>
        public  void addToClassMethodList(FuncDeclaration fd, ClassDeclaration cd) {
            if ((cd.classKind != ClassKind.objc))
            {
                return ;
            }
            if (fd.selector == null)
            {
                return ;
            }
            assert(fd.isStatic() ? cd.objc.isMeta : !cd.objc.isMeta);
            (cd.objc.methodList.get()).push(fd);
        }

        // Erasure: isThis<FuncDeclaration>
        public  AggregateDeclaration isThis(FuncDeclaration funcDeclaration) {
            {
                if (__withSym.selector == null)
                {
                    return null;
                }
                ClassDeclaration cd = __withSym.isMember2().isClassDeclaration();
                if ((cd.classKind == ClassKind.objc))
                {
                    if (!cd.objc.isMeta)
                    {
                        return cd.objc.metaclass;
                    }
                }
                return null;
            }
        }

        // Erasure: createSelectorParameter<FuncDeclaration, Ptr>
        public  VarDeclaration createSelectorParameter(FuncDeclaration fd, Ptr<Scope> sc) {
            if (fd.selector == null)
            {
                return null;
            }
            VarDeclaration var = new VarDeclaration(fd.loc, Type.tvoidptr, Identifier.anonymous(), null, 0L);
            var.storage_class |= 32L;
            dsymbolSemantic(var, sc);
            if ((sc.get()).insert(var) == null)
            {
                throw new AssertionError("Unreachable code!");
            }
            var.parent.value = fd;
            return var;
        }

        // Erasure: setMetaclass<InterfaceDeclaration, Ptr>
        public  void setMetaclass(InterfaceDeclaration interfaceDeclaration, Ptr<Scope> sc) {
            Function2<Loc,Ptr<DArray<Ptr<BaseClass>>>,InterfaceDeclaration> newMetaclass = new Function2<Loc,Ptr<DArray<Ptr<BaseClass>>>,InterfaceDeclaration>() {
                public InterfaceDeclaration invoke(Loc loc, Ptr<DArray<Ptr<BaseClass>>> metaBases) {
                 {
                    return new InterfaceDeclaration(loc, null, metaBases);
                }}

            };
            setMetaclass_98AC5D09E954A8ECInterfaceDeclaration(interfaceDeclaration, sc);
        }

        // Erasure: setMetaclass<ClassDeclaration, Ptr>
        public  void setMetaclass(ClassDeclaration classDeclaration, Ptr<Scope> sc) {
            Function2<Loc,Ptr<DArray<Ptr<BaseClass>>>,ClassDeclaration> newMetaclass = new Function2<Loc,Ptr<DArray<Ptr<BaseClass>>>,ClassDeclaration>() {
                public ClassDeclaration invoke(Loc loc, Ptr<DArray<Ptr<BaseClass>>> metaBases) {
                 {
                    return new ClassDeclaration(loc, null, metaBases, refPtr(new DArray<Dsymbol>()), false);
                }}

            };
            setMetaclass_98AC5D09E954A8ECClassDeclaration.invoke(classDeclaration, sc);
        }

        // Erasure: getRuntimeMetaclass<ClassDeclaration>
        public  ClassDeclaration getRuntimeMetaclass(ClassDeclaration classDeclaration) {
            if ((classDeclaration.objc.metaclass == null) && classDeclaration.objc.isMeta)
            {
                if (classDeclaration.baseClass != null)
                {
                    return this.getRuntimeMetaclass(classDeclaration.baseClass);
                }
                else
                {
                    return classDeclaration;
                }
            }
            else
            {
                return classDeclaration.objc.metaclass;
            }
        }

        // Erasure: addSymbols<AttribDeclaration, Ptr, Ptr>
        public  void addSymbols(AttribDeclaration attribDeclaration, Ptr<DArray<ClassDeclaration>> classes, Ptr<DArray<ClassDeclaration>> categories) {
            Ptr<DArray<Dsymbol>> symbols = attribDeclaration.include(null);
            if (symbols == null)
            {
                return ;
            }
            {
                Slice<Dsymbol> __r1555 = (symbols.get()).opSlice().copy();
                int __key1556 = 0;
                for (; (__key1556 < __r1555.getLength());__key1556 += 1) {
                    Dsymbol symbol = __r1555.get(__key1556);
                    symbol.addObjcSymbols(classes, categories);
                }
            }
        }

        // Erasure: addSymbols<ClassDeclaration, Ptr, Ptr>
        public  void addSymbols(ClassDeclaration classDeclaration, Ptr<DArray<ClassDeclaration>> classes, Ptr<DArray<ClassDeclaration>> categories) {
            if ((__withSym.classKind == ClassKind.objc) && !__withSym.objc.isExtern && !__withSym.objc.isMeta)
            {
                (classes.get()).push(classDeclaration);
            }
        }

        // Erasure: checkOffsetof<Expression, AggregateDeclaration>
        public  void checkOffsetof(Expression expression, AggregateDeclaration aggregateDeclaration) {
            if ((aggregateDeclaration.classKind != ClassKind.objc))
            {
                return ;
            }
            ByteSlice errorMessage = new ByteSlice("no property `offsetof` for member `%s` of type `%s`");
            ByteSlice supplementalMessage = new ByteSlice("`offsetof` is not available for members of Objective-C classes. Please use the Objective-C runtime instead");
            expression.error(new BytePtr("no property `offsetof` for member `%s` of type `%s`"), expression.toChars(), expression.type.value.toChars());
            expression.errorSupplemental(new BytePtr("`offsetof` is not available for members of Objective-C classes. Please use the Objective-C runtime instead"));
        }

        // Erasure: checkTupleof<Expression, TypeClass>
        public  void checkTupleof(Expression expression, TypeClass type) {
            if ((type.sym.classKind != ClassKind.objc))
            {
                return ;
            }
            expression.error(new BytePtr("no property `tupleof` for type `%s`"), type.toChars());
            expression.errorSupplemental(new BytePtr("`tupleof` is not available for members of Objective-C classes. Please use the Objective-C runtime instead"));
        }

        // Erasure: isUdaSelector<StructDeclaration>
        public  boolean isUdaSelector(StructDeclaration sd) {
            if ((!pequals(sd.ident, Id.udaSelector)) || (sd.parent.value == null))
            {
                return false;
            }
            dmodule.Module _module = sd.parent.value.isModule();
            return (_module != null) && _module.isCoreModule(Id.attribute);
        }


        public Supported copy() {
            Supported that = new Supported();
            return that;
        }
    }
    // from template setMetaclass!(_98AC5D09E954A8ECClassDeclaration)
    // Erasure: setMetaclass_98AC5D09E954A8ECClassDeclaration<ClassDeclaration, Ptr>
    public static void setMetaclass_98AC5D09E954A8ECClassDeclaration(ClassDeclaration classDeclaration, Ptr<Scope> sc) {
        ByteSlice errorType = new ByteSlice("class");
        {
            if ((__withSym.classKind != ClassKind.objc) || __withSym.objc.isMeta || (__withSym.objc.metaclass != null))
            {
                return ;
            }
            if (__withSym.objc.identifier == null)
            {
                __withSym.objc.identifier = classDeclaration.ident;
            }
            Ptr<DArray<Ptr<BaseClass>>> metaBases = refPtr(new DArray<Ptr<BaseClass>>());
            {
                Slice<Ptr<BaseClass>> __r1553 = (__withSym.baseclasses.get()).opSlice().copy();
                int __key1554 = 0;
                for (; (__key1554 < __r1553.getLength());__key1554 += 1) {
                    Ptr<BaseClass> base = __r1553.get(__key1554);
                    ClassDeclaration baseCd = (base.get()).sym;
                    assert(baseCd != null);
                    if ((baseCd.classKind == ClassKind.objc))
                    {
                        assert(baseCd.objc.metaclass != null);
                        assert(baseCd.objc.metaclass.objc.isMeta);
                        assert(((baseCd.objc.metaclass.type.ty & 0xFF) == ENUMTY.Tclass));
                        Ptr<BaseClass> metaBase = refPtr(new BaseClass(baseCd.objc.metaclass.type));
                        (metaBase.get()).sym = baseCd.objc.metaclass;
                        (metaBases.get()).push(metaBase);
                    }
                    else
                    {
                        __withSym.error(new BytePtr("base class for an Objective-C class must be `extern (Objective-C)`"));
                    }
                }
            }
            __withSym.objc.metaclass = newMetaclass.invoke(__withSym.loc, metaBases);
            __withSym.objc.metaclass.storage_class |= 1L;
            __withSym.objc.metaclass.classKind = ClassKind.objc;
            __withSym.objc.metaclass.objc.isMeta = true;
            __withSym.objc.metaclass.objc.isExtern = __withSym.objc.isExtern;
            __withSym.objc.metaclass.objc.identifier = __withSym.objc.identifier;
            if (__withSym.baseClass != null)
            {
                __withSym.objc.metaclass.baseClass = __withSym.baseClass.objc.metaclass;
            }
            (__withSym.members.get()).push(__withSym.objc.metaclass);
            __withSym.objc.metaclass.addMember(sc, classDeclaration);
            dsymbolSemantic(__withSym.objc.metaclass, sc);
        }
    }


    // from template setMetaclass!(_98AC5D09E954A8ECInterfaceDeclaration)
    // Erasure: setMetaclass_98AC5D09E954A8ECInterfaceDeclaration<InterfaceDeclaration, Ptr>
    public static void setMetaclass_98AC5D09E954A8ECInterfaceDeclaration(InterfaceDeclaration classDeclaration, Ptr<Scope> sc) {
        ByteSlice errorType = new ByteSlice("interface");
        {
            if ((__withSym.classKind != ClassKind.objc) || __withSym.objc.isMeta || (__withSym.objc.metaclass != null))
            {
                return ;
            }
            if (__withSym.objc.identifier == null)
            {
                __withSym.objc.identifier = classDeclaration.ident;
            }
            Ptr<DArray<Ptr<BaseClass>>> metaBases = refPtr(new DArray<Ptr<BaseClass>>());
            {
                Slice<Ptr<BaseClass>> __r1551 = (__withSym.baseclasses.get()).opSlice().copy();
                int __key1552 = 0;
                for (; (__key1552 < __r1551.getLength());__key1552 += 1) {
                    Ptr<BaseClass> base = __r1551.get(__key1552);
                    ClassDeclaration baseCd = (base.get()).sym;
                    assert(baseCd != null);
                    if ((baseCd.classKind == ClassKind.objc))
                    {
                        assert(baseCd.objc.metaclass != null);
                        assert(baseCd.objc.metaclass.objc.isMeta);
                        assert(((baseCd.objc.metaclass.type.ty & 0xFF) == ENUMTY.Tclass));
                        Ptr<BaseClass> metaBase = refPtr(new BaseClass(baseCd.objc.metaclass.type));
                        (metaBase.get()).sym = baseCd.objc.metaclass;
                        (metaBases.get()).push(metaBase);
                    }
                    else
                    {
                        __withSym.error(new BytePtr("base interface for an Objective-C interface must be `extern (Objective-C)`"));
                    }
                }
            }
            __withSym.objc.metaclass = newMetaclass.invoke(__withSym.loc, metaBases);
            __withSym.objc.metaclass.storage_class |= 1L;
            __withSym.objc.metaclass.classKind = ClassKind.objc;
            __withSym.objc.metaclass.objc.isMeta = true;
            __withSym.objc.metaclass.objc.isExtern = __withSym.objc.isExtern;
            __withSym.objc.metaclass.objc.identifier = __withSym.objc.identifier;
            if (__withSym.baseClass != null)
            {
                __withSym.objc.metaclass.baseClass = __withSym.baseClass.objc.metaclass;
            }
            (__withSym.members.get()).push(__withSym.objc.metaclass);
            __withSym.objc.metaclass.addMember(sc, classDeclaration);
            dsymbolSemantic(__withSym.objc.metaclass, sc);
        }
    }


}
