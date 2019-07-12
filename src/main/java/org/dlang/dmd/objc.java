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
        public static void _init() {
            stringtable._init(0);
        }

        public  ObjcSelector(BytePtr sv, int len, int pcount) {
            this.stringvalue = pcopy(sv);
            this.stringlen = len;
            this.paramCount = pcount;
        }

        public static Ptr<ObjcSelector> lookup(BytePtr s) {
            int len = 0;
            int pcount = 0;
            BytePtr i = pcopy(s);
            for (; ((i.get() & 0xFF) != 0);){
                len += 1;
                if (((i.get() & 0xFF) == 58))
                    pcount += 1;
                i.plusAssign(1);
            }
            return lookup(s, len, pcount);
        }

        public static Ptr<ObjcSelector> lookup(BytePtr s, int len, int pcount) {
            Ptr<StringValue> sv = stringtable.update(s, len);
            Ptr<ObjcSelector> sel = ((Ptr<ObjcSelector>)(sv.get()).ptrvalue);
            if (sel == null)
            {
                sel = new ObjcSelector((sv.get()).toDchars(), len, pcount);
                (sv.get()).ptrvalue = pcopy((toBytePtr(sel)));
            }
            return sel;
        }

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
                            firstChar = (byte)((firstChar & 0xFF) - 97 + 65);
                        buf.value.writestring(new ByteSlice("set"));
                        buf.value.writeByte((firstChar & 0xFF));
                        buf.value.write((toBytePtr(id).plus(1)), id.getLength() - 1);
                        buf.value.writeByte(58);
                        /*goto Lcomplete*/throw Dispatch0.INSTANCE;
                    }
                    buf.value.write(toBytePtr(id), id.getLength());
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

        public  ByteSlice asString() {
            return this.stringvalue.slice(0,this.stringlen);
        }

        public ObjcSelector(){
        }
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
        public  ObjcClassDeclaration(ClassDeclaration classDeclaration) {
            this.classDeclaration = classDeclaration;
            this.methodList = new DArray<Dsymbol>();
        }

        public  boolean isRootClass() {
            return (this.classDeclaration.classKind == ClassKind.objc) && (this.metaclass == null) && (this.classDeclaration.baseClass == null);
        }

        public ObjcClassDeclaration(){
        }
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
        public static void _init() {
            if (global.value.params.isOSX && global.value.params.is64bit)
                _objc = new Supported();
            else
                _objc = new Unsupported();
        }

        public static void deinitialize() {
            _objc = null;
        }

        public abstract void setObjc(ClassDeclaration cd);


        public abstract void setObjc(InterfaceDeclaration arg0);


        public abstract void deprecate(InterfaceDeclaration interfaceDeclaration);


        public abstract void setSelector(FuncDeclaration arg0, Ptr<Scope> sc);


        public abstract void validateSelector(FuncDeclaration fd);


        public abstract void checkLinkage(FuncDeclaration fd);


        public abstract boolean isVirtual(FuncDeclaration fd);


        public abstract ClassDeclaration getParent(FuncDeclaration fd, ClassDeclaration cd);


        public abstract void addToClassMethodList(FuncDeclaration fd, ClassDeclaration cd);


        public abstract AggregateDeclaration isThis(FuncDeclaration funcDeclaration);


        public abstract VarDeclaration createSelectorParameter(FuncDeclaration fd, Ptr<Scope> sc);


        public abstract void setMetaclass(InterfaceDeclaration interfaceDeclaration, Ptr<Scope> sc);


        public abstract void setMetaclass(ClassDeclaration classDeclaration, Ptr<Scope> sc);


        public abstract ClassDeclaration getRuntimeMetaclass(ClassDeclaration classDeclaration);


        public abstract void addSymbols(AttribDeclaration attribDeclaration, Ptr<DArray<ClassDeclaration>> classes, Ptr<DArray<ClassDeclaration>> categories);


        public abstract void addSymbols(ClassDeclaration classDeclaration, Ptr<DArray<ClassDeclaration>> classes, Ptr<DArray<ClassDeclaration>> categories);


        public abstract void checkOffsetof(Expression expression, AggregateDeclaration aggregateDeclaration);


        public abstract void checkTupleof(Expression expression, TypeClass type);



        public Objc() {}

        public abstract Objc copy();
    }
    public static class Unsupported extends Objc
    {
        public  Unsupported() {
            ObjcGlue.initialize();
        }

        public  void setObjc(ClassDeclaration cd) {
            cd.error(new BytePtr("Objective-C classes not supported"));
        }

        public  void setObjc(InterfaceDeclaration id) {
            id.error(new BytePtr("Objective-C interfaces not supported"));
        }

        public  void deprecate(InterfaceDeclaration _param_0) {
        }

        public  void setSelector(FuncDeclaration _param_0, Ptr<Scope> _param_1) {
        }

        public  void validateSelector(FuncDeclaration _param_0) {
        }

        public  void checkLinkage(FuncDeclaration _param_0) {
        }

        public  boolean isVirtual(FuncDeclaration _param_0) {
            throw new AssertionError("Unreachable code!");
        }

        public  ClassDeclaration getParent(FuncDeclaration _param_0, ClassDeclaration cd) {
            return cd;
        }

        public  void addToClassMethodList(FuncDeclaration _param_0, ClassDeclaration _param_1) {
        }

        public  AggregateDeclaration isThis(FuncDeclaration funcDeclaration) {
            return null;
        }

        public  VarDeclaration createSelectorParameter(FuncDeclaration _param_0, Ptr<Scope> _param_1) {
            return null;
        }

        public  void setMetaclass(InterfaceDeclaration _param_0, Ptr<Scope> _param_1) {
        }

        public  void setMetaclass(ClassDeclaration _param_0, Ptr<Scope> _param_1) {
        }

        public  ClassDeclaration getRuntimeMetaclass(ClassDeclaration classDeclaration) {
            throw new AssertionError("Unreachable code!");
        }

        public  void addSymbols(AttribDeclaration attribDeclaration, Ptr<DArray<ClassDeclaration>> classes, Ptr<DArray<ClassDeclaration>> categories) {
        }

        public  void addSymbols(ClassDeclaration classDeclaration, Ptr<DArray<ClassDeclaration>> classes, Ptr<DArray<ClassDeclaration>> categories) {
        }

        public  void checkOffsetof(Expression expression, AggregateDeclaration aggregateDeclaration) {
        }

        public  void checkTupleof(Expression expression, TypeClass type) {
        }


        public Unsupported copy() {
            Unsupported that = new Unsupported();
            return that;
        }
    }
    public static class Supported extends Objc
    {
        public  Supported() {
            VersionCondition.addPredefinedGlobalIdent(new ByteSlice("D_ObjectiveC"));
            ObjcGlue.initialize();
            ObjcSelector._init();
        }

        public  void setObjc(ClassDeclaration cd) {
            cd.classKind = ClassKind.objc;
            cd.objc.isExtern = (cd.storage_class & 2L) > 0L;
        }

        public  void setObjc(InterfaceDeclaration id) {
            id.classKind = ClassKind.objc;
            id.objc.isExtern = true;
        }

        public  void deprecate(InterfaceDeclaration id) {
            if (id.objc.isMeta)
                return ;
            id.deprecation(new BytePtr("Objective-C interfaces have been deprecated"));
            deprecationSupplemental(id.loc, new BytePtr("Representing an Objective-C class as a D interface has been deprecated. Please use `extern (Objective-C) extern class` instead"));
        }

        public  void setSelector(FuncDeclaration fd, Ptr<Scope> sc) {
            if (fd.userAttribDecl == null)
                return ;
            Ptr<DArray<Expression>> udas = fd.userAttribDecl.getAttributes();
            arrayExpressionSemantic(udas, sc, true);
            {
                int i = 0;
                for (; (i < (udas.get()).length);i++){
                    Expression uda = (udas.get()).get(i);
                    assert(uda != null);
                    if (((uda.op & 0xFF) != 126))
                        continue;
                    Ptr<DArray<Expression>> exps = ((TupleExp)uda).exps;
                    {
                        int j = 0;
                        for (; (j < (exps.get()).length);j++){
                            Expression e = (exps.get()).get(j);
                            assert(e != null);
                            if (((e.op & 0xFF) != 49))
                                continue;
                            StructLiteralExp literal = (StructLiteralExp)e;
                            assert(literal.sd != null);
                            if (!this.isUdaSelector(literal.sd))
                                continue;
                            if (fd.selector != null)
                            {
                                fd.error(new BytePtr("can only have one Objective-C selector per method"));
                                return ;
                            }
                            assert(((literal.elements.get()).length == 1));
                            StringExp se = (literal.elements.get()).get(0).toStringExp();
                            assert(se != null);
                            fd.selector = ObjcSelector.lookup(se.toUTF8(sc).string);
                        }
                    }
                }
            }
        }

        public  void validateSelector(FuncDeclaration fd) {
            if (fd.selector == null)
                return ;
            TypeFunction tf = (TypeFunction)fd.type;
            if (((fd.selector.get()).paramCount != (tf.parameterList.parameters.get()).length))
                fd.error(new BytePtr("number of colons in Objective-C selector must match number of parameters"));
            if ((fd.parent.value != null) && (fd.parent.value.isTemplateInstance() != null))
                fd.error(new BytePtr("template cannot have an Objective-C selector attached"));
        }

        public  void checkLinkage(FuncDeclaration fd) {
            if ((fd.linkage != LINK.objc) && (fd.selector != null))
                fd.error(new BytePtr("must have Objective-C linkage to attach a selector"));
        }

        public  boolean isVirtual(FuncDeclaration fd) {
            return !(((__withSym.get()).kind == Prot.Kind.private_) || ((__withSym.get()).kind == Prot.Kind.package_));
        }

        public  ClassDeclaration getParent(FuncDeclaration fd, ClassDeclaration cd) {
            ClassDeclaration __result = null;
            Ref<FuncDeclaration> fd_ref = ref(fd);
            try {
                if ((cd.classKind == ClassKind.objc) && fd_ref.value.isStatic() && !cd.objc.isMeta)
                    __result = cd.objc.metaclass;
                    /*goto __returnLabel*/throw Dispatch0.INSTANCE;
                else
                    __result = cd;
                    /*goto __returnLabel*/throw Dispatch0.INSTANCE;
            }
            catch(Dispatch0 __d){}
        /*__returnLabel:*/
            Function3<ClassDeclaration,FuncDeclaration,ClassDeclaration,Void> __ensure = new Function3<ClassDeclaration,FuncDeclaration,ClassDeclaration,Void>(){
                public Void invoke(ClassDeclaration __result, Ref<FuncDeclaration> fd, Ref<ClassDeclaration> cd) {
                    {
                        ClassDeclaration metaclass = __result;
                        {
                            assert(metaclass != null);
                        }
                    }
                }
            };
            __ensure.invoke(__result, fd_ref, cd);
            return __result;
        }

        public  void addToClassMethodList(FuncDeclaration fd, ClassDeclaration cd) {
            if ((cd.classKind != ClassKind.objc))
                return ;
            if (fd.selector == null)
                return ;
            assert(fd.isStatic() ? cd.objc.isMeta : !cd.objc.isMeta);
            (cd.objc.methodList.get()).push(fd);
        }

        public  AggregateDeclaration isThis(FuncDeclaration funcDeclaration) {
            {
                if (__withSym.selector == null)
                    return null;
                ClassDeclaration cd = __withSym.isMember2().isClassDeclaration();
                if ((cd.classKind == ClassKind.objc))
                {
                    if (!cd.objc.isMeta)
                        return cd.objc.metaclass;
                }
                return null;
            }
        }

        public  VarDeclaration createSelectorParameter(FuncDeclaration fd, Ptr<Scope> sc) {
            if (fd.selector == null)
                return null;
            VarDeclaration var = new VarDeclaration(fd.loc, Type.tvoidptr.value, Identifier.anonymous(), null, 0L);
            var.storage_class |= 32L;
            dsymbolSemantic(var, sc);
            if ((sc.get()).insert(var) == null)
                throw new AssertionError("Unreachable code!");
            var.parent.value = fd;
            return var;
        }

        public  void setMetaclass(InterfaceDeclaration interfaceDeclaration, Ptr<Scope> sc) {
            Function2<Loc,Ptr<DArray<Ptr<BaseClass>>>,InterfaceDeclaration> newMetaclass = new Function2<Loc,Ptr<DArray<Ptr<BaseClass>>>,InterfaceDeclaration>(){
                public InterfaceDeclaration invoke(Loc loc, Ptr<DArray<Ptr<BaseClass>>> metaBases) {
                    Ref<Ptr<DArray<Ptr<BaseClass>>>> metaBases_ref = ref(metaBases);
                    return new InterfaceDeclaration(loc, null, metaBases_ref.value);
                }
            };
            setMetaclass_98AC5D09E954A8ECInterfaceDeclaration(interfaceDeclaration, sc);
        }

        public  void setMetaclass(ClassDeclaration classDeclaration, Ptr<Scope> sc) {
            Function2<Loc,Ptr<DArray<Ptr<BaseClass>>>,ClassDeclaration> newMetaclass = new Function2<Loc,Ptr<DArray<Ptr<BaseClass>>>,ClassDeclaration>(){
                public ClassDeclaration invoke(Loc loc, Ptr<DArray<Ptr<BaseClass>>> metaBases) {
                    Ref<Ptr<DArray<Ptr<BaseClass>>>> metaBases_ref = ref(metaBases);
                    return new ClassDeclaration(loc, null, metaBases_ref.value, new DArray<Dsymbol>(), false);
                }
            };
            setMetaclass_98AC5D09E954A8ECClassDeclaration.invoke(classDeclaration, sc);
        }

        public  ClassDeclaration getRuntimeMetaclass(ClassDeclaration classDeclaration) {
            if ((classDeclaration.objc.metaclass == null) && classDeclaration.objc.isMeta)
            {
                if (classDeclaration.baseClass != null)
                    return this.getRuntimeMetaclass(classDeclaration.baseClass);
                else
                    return classDeclaration;
            }
            else
                return classDeclaration.objc.metaclass;
        }

        public  void addSymbols(AttribDeclaration attribDeclaration, Ptr<DArray<ClassDeclaration>> classes, Ptr<DArray<ClassDeclaration>> categories) {
            Ptr<DArray<Dsymbol>> symbols = attribDeclaration.include(null);
            if (symbols == null)
                return ;
            {
                Slice<Dsymbol> __r1529 = (symbols.get()).opSlice().copy();
                int __key1530 = 0;
                for (; (__key1530 < __r1529.getLength());__key1530 += 1) {
                    Dsymbol symbol = __r1529.get(__key1530);
                    symbol.addObjcSymbols(classes, categories);
                }
            }
        }

        public  void addSymbols(ClassDeclaration classDeclaration, Ptr<DArray<ClassDeclaration>> classes, Ptr<DArray<ClassDeclaration>> categories) {
            if ((__withSym.classKind == ClassKind.objc) && !__withSym.objc.isExtern && !__withSym.objc.isMeta)
                (classes.get()).push(classDeclaration);
        }

        public  void checkOffsetof(Expression expression, AggregateDeclaration aggregateDeclaration) {
            if ((aggregateDeclaration.classKind != ClassKind.objc))
                return ;
            ByteSlice errorMessage = new ByteSlice("no property `offsetof` for member `%s` of type `%s`");
            ByteSlice supplementalMessage = new ByteSlice("`offsetof` is not available for members of Objective-C classes. Please use the Objective-C runtime instead");
            expression.error(new BytePtr("no property `offsetof` for member `%s` of type `%s`"), expression.toChars(), expression.type.value.toChars());
            expression.errorSupplemental(new BytePtr("`offsetof` is not available for members of Objective-C classes. Please use the Objective-C runtime instead"));
        }

        public  void checkTupleof(Expression expression, TypeClass type) {
            if ((type.sym.classKind != ClassKind.objc))
                return ;
            expression.error(new BytePtr("no property `tupleof` for type `%s`"), type.toChars());
            expression.errorSupplemental(new BytePtr("`tupleof` is not available for members of Objective-C classes. Please use the Objective-C runtime instead"));
        }

        public  boolean isUdaSelector(StructDeclaration sd) {
            if ((!pequals(sd.ident, Id.udaSelector)) || (sd.parent.value == null))
                return false;
            dmodule.Module _module = sd.parent.value.isModule();
            return (_module != null) && _module.isCoreModule(Id.attribute);
        }


        public Supported copy() {
            Supported that = new Supported();
            return that;
        }
    }
    // from template setMetaclass!(_98AC5D09E954A8ECClassDeclaration)
    public static void setMetaclass_98AC5D09E954A8ECClassDeclaration(ClassDeclaration classDeclaration, Ptr<Scope> sc) {
        ByteSlice errorType = new ByteSlice("class");
        {
            if ((__withSym.classKind != ClassKind.objc) || __withSym.objc.isMeta || (__withSym.objc.metaclass != null))
                return ;
            if (__withSym.objc.identifier == null)
                __withSym.objc.identifier = classDeclaration.ident;
            Ptr<DArray<Ptr<BaseClass>>> metaBases = new DArray<Ptr<BaseClass>>();
            {
                Slice<Ptr<BaseClass>> __r1527 = (__withSym.baseclasses.get()).opSlice().copy();
                int __key1528 = 0;
                for (; (__key1528 < __r1527.getLength());__key1528 += 1) {
                    Ptr<BaseClass> base = __r1527.get(__key1528);
                    ClassDeclaration baseCd = (base.get()).sym;
                    assert(baseCd != null);
                    if ((baseCd.classKind == ClassKind.objc))
                    {
                        assert(baseCd.objc.metaclass != null);
                        assert(baseCd.objc.metaclass.objc.isMeta);
                        assert(((baseCd.objc.metaclass.type.ty & 0xFF) == ENUMTY.Tclass));
                        Ptr<BaseClass> metaBase = new BaseClass(baseCd.objc.metaclass.type);
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
                __withSym.objc.metaclass.baseClass = __withSym.baseClass.objc.metaclass;
            (__withSym.members.get()).push(__withSym.objc.metaclass);
            __withSym.objc.metaclass.addMember(sc, classDeclaration);
            dsymbolSemantic(__withSym.objc.metaclass, sc);
        }
    }


    // from template setMetaclass!(_98AC5D09E954A8ECInterfaceDeclaration)
    public static void setMetaclass_98AC5D09E954A8ECInterfaceDeclaration(InterfaceDeclaration classDeclaration, Ptr<Scope> sc) {
        ByteSlice errorType = new ByteSlice("interface");
        {
            if ((__withSym.classKind != ClassKind.objc) || __withSym.objc.isMeta || (__withSym.objc.metaclass != null))
                return ;
            if (__withSym.objc.identifier == null)
                __withSym.objc.identifier = classDeclaration.ident;
            Ptr<DArray<Ptr<BaseClass>>> metaBases = new DArray<Ptr<BaseClass>>();
            {
                Slice<Ptr<BaseClass>> __r1525 = (__withSym.baseclasses.get()).opSlice().copy();
                int __key1526 = 0;
                for (; (__key1526 < __r1525.getLength());__key1526 += 1) {
                    Ptr<BaseClass> base = __r1525.get(__key1526);
                    ClassDeclaration baseCd = (base.get()).sym;
                    assert(baseCd != null);
                    if ((baseCd.classKind == ClassKind.objc))
                    {
                        assert(baseCd.objc.metaclass != null);
                        assert(baseCd.objc.metaclass.objc.isMeta);
                        assert(((baseCd.objc.metaclass.type.ty & 0xFF) == ENUMTY.Tclass));
                        Ptr<BaseClass> metaBase = new BaseClass(baseCd.objc.metaclass.type);
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
                __withSym.objc.metaclass.baseClass = __withSym.baseClass.objc.metaclass;
            (__withSym.members.get()).push(__withSym.objc.metaclass);
            __withSym.objc.metaclass.addMember(sc, classDeclaration);
            dsymbolSemantic(__withSym.objc.metaclass, sc);
        }
    }


}
