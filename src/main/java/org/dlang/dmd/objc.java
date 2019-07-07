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
        public BytePtr stringvalue;
        public int stringlen;
        public int paramCount;
        public static void _init() {
            stringtable._init(0);
        }

        public  ObjcSelector(BytePtr sv, int len, int pcount) {
            this.stringvalue = pcopy(sv);
            this.stringlen = len;
            this.paramCount = pcount;
        }

        public static ObjcSelector lookup(BytePtr s) {
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

        public static ObjcSelector lookup(BytePtr s, int len, int pcount) {
            StringValue sv = stringtable.update(s, len);
            ObjcSelector sel = ((ObjcSelector)(sv).ptrvalue);
            if (sel == null)
            {
                sel = new ObjcSelector((sv).toDchars(), len, pcount);
                (sv).ptrvalue = pcopy((toBytePtr(sel)));
            }
            return sel;
        }

        public static ObjcSelector create(FuncDeclaration fdecl) {
            OutBuffer buf = new OutBuffer();
            try {
                int pcount = 0;
                TypeFunction ftype = (TypeFunction)fdecl.type;
                ByteSlice id = fdecl.ident.asString().copy();
                try {
                    if (ftype.isproperty && (ftype.parameterList.parameters != null) && ((ftype.parameterList.parameters).length == 1))
                    {
                        byte firstChar = id.get(0);
                        if (((firstChar & 0xFF) >= 97) && ((firstChar & 0xFF) <= 122))
                            firstChar = (byte)((firstChar & 0xFF) - 97 + 65);
                        buf.writestring(new ByteSlice("set"));
                        buf.writeByte((firstChar & 0xFF));
                        buf.write((toBytePtr(id).plus(1)), id.getLength() - 1);
                        buf.writeByte(58);
                        /*goto Lcomplete*/throw Dispatch0.INSTANCE;
                    }
                    buf.write(toBytePtr(id), id.getLength());
                    if ((ftype.parameterList.parameters != null) && ((ftype.parameterList.parameters).length != 0))
                    {
                        buf.writeByte(95);
                        DArray<Parameter> arguments = ftype.parameterList.parameters;
                        int dim = Parameter.dim(arguments);
                        {
                            int i = 0;
                            for (; (i < dim);i++){
                                Parameter arg = Parameter.getNth(arguments, i, null);
                                mangleToBuffer(arg.type, buf);
                                buf.writeByte(58);
                            }
                        }
                        pcount = dim;
                    }
                }
                catch(Dispatch0 __d){}
            /*Lcomplete:*/
                buf.writeByte(0);
                return lookup(toBytePtr(buf.data), buf.size, pcount);
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
    static Objc _objc;
    public static Objc objc() {
        return _objc;
    }

    public static class ObjcClassDeclaration
    {
        public boolean isMeta = false;
        public boolean isExtern = false;
        public Identifier identifier;
        public ClassDeclaration classDeclaration;
        public ClassDeclaration metaclass;
        public DArray<Dsymbol> methodList;
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
            if (global.params.isOSX && global.params.is64bit)
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
        public abstract void setSelector(FuncDeclaration arg0, Scope sc);
        public abstract void validateSelector(FuncDeclaration fd);
        public abstract void checkLinkage(FuncDeclaration fd);
        public abstract boolean isVirtual(FuncDeclaration fd);
        public abstract ClassDeclaration getParent(FuncDeclaration fd, ClassDeclaration cd);
        public abstract void addToClassMethodList(FuncDeclaration fd, ClassDeclaration cd);
        public abstract AggregateDeclaration isThis(FuncDeclaration funcDeclaration);
        public abstract VarDeclaration createSelectorParameter(FuncDeclaration fd, Scope sc);
        public abstract void setMetaclass(InterfaceDeclaration interfaceDeclaration, Scope sc);
        public abstract void setMetaclass(ClassDeclaration classDeclaration, Scope sc);
        public abstract ClassDeclaration getRuntimeMetaclass(ClassDeclaration classDeclaration);
        public abstract void addSymbols(AttribDeclaration attribDeclaration, DArray<ClassDeclaration> classes, DArray<ClassDeclaration> categories);
        public abstract void addSymbols(ClassDeclaration classDeclaration, DArray<ClassDeclaration> classes, DArray<ClassDeclaration> categories);
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

        public  void setSelector(FuncDeclaration _param_0, Scope _param_1) {
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

        public  VarDeclaration createSelectorParameter(FuncDeclaration _param_0, Scope _param_1) {
            return null;
        }

        public  void setMetaclass(InterfaceDeclaration _param_0, Scope _param_1) {
        }

        public  void setMetaclass(ClassDeclaration _param_0, Scope _param_1) {
        }

        public  ClassDeclaration getRuntimeMetaclass(ClassDeclaration classDeclaration) {
            throw new AssertionError("Unreachable code!");
        }

        public  void addSymbols(AttribDeclaration attribDeclaration, DArray<ClassDeclaration> classes, DArray<ClassDeclaration> categories) {
        }

        public  void addSymbols(ClassDeclaration classDeclaration, DArray<ClassDeclaration> classes, DArray<ClassDeclaration> categories) {
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

        public  void setSelector(FuncDeclaration fd, Scope sc) {
            if (fd.userAttribDecl == null)
                return ;
            DArray<Expression> udas = fd.userAttribDecl.getAttributes();
            arrayExpressionSemantic(udas, sc, true);
            {
                int i = 0;
                for (; (i < (udas).length);i++){
                    Expression uda = (udas).get(i);
                    assert(uda != null);
                    if (((uda.op & 0xFF) != 126))
                        continue;
                    DArray<Expression> exps = ((TupleExp)uda).exps;
                    {
                        int j = 0;
                        for (; (j < (exps).length);j++){
                            Expression e = (exps).get(j);
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
                            assert(((literal.elements).length == 1));
                            StringExp se = (literal.elements).get(0).toStringExp();
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
            if (((fd.selector).paramCount != (tf.parameterList.parameters).length))
                fd.error(new BytePtr("number of colons in Objective-C selector must match number of parameters"));
            if ((fd.parent != null) && (fd.parent.isTemplateInstance() != null))
                fd.error(new BytePtr("template cannot have an Objective-C selector attached"));
        }

        public  void checkLinkage(FuncDeclaration fd) {
            if ((fd.linkage != LINK.objc) && (fd.selector != null))
                fd.error(new BytePtr("must have Objective-C linkage to attach a selector"));
        }

        public  boolean isVirtual(FuncDeclaration fd) {
            return !(((__withSym).kind == Prot.Kind.private_) || ((__withSym).kind == Prot.Kind.package_));
        }

        public  ClassDeclaration getParent(FuncDeclaration fd, ClassDeclaration cd) {
            Ref<FuncDeclaration> fd_ref = ref(fd);
            Ref<ClassDeclaration> cd_ref = ref(cd);
            try {
                if ((cd_ref.value.classKind == ClassKind.objc) && fd_ref.value.isStatic() && !cd_ref.value.objc.isMeta)
                    __result = cd_ref.value.objc.metaclass;
                    /*goto __returnLabel*/throw Dispatch0.INSTANCE;
                else
                    __result = cd_ref.value;
                    /*goto __returnLabel*/throw Dispatch0.INSTANCE;
            }
            catch(Dispatch0 __d){}
        /*__returnLabel:*/
            Function3<ClassDeclaration,FuncDeclaration,ClassDeclaration,Void> __ensure = new Function3<ClassDeclaration,FuncDeclaration,ClassDeclaration,Void>(){
                public Void invoke(Ref<ClassDeclaration> __result, Ref<FuncDeclaration> fd, Ref<ClassDeclaration> cd){
                    {
                        ClassDeclaration metaclass = __result.value;
                        {
                            assert(metaclass != null);
                        }
                    }
                }
            };
            __ensure.invoke(__result, fd_ref, cd_ref);
            return __result;
        }

        public  void addToClassMethodList(FuncDeclaration fd, ClassDeclaration cd) {
            if ((cd.classKind != ClassKind.objc))
                return ;
            if (fd.selector == null)
                return ;
            assert(fd.isStatic() ? cd.objc.isMeta : !cd.objc.isMeta);
            (cd.objc.methodList).push(fd);
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

        public  VarDeclaration createSelectorParameter(FuncDeclaration fd, Scope sc) {
            if (fd.selector == null)
                return null;
            VarDeclaration var = new VarDeclaration(fd.loc, Type.tvoidptr, Identifier.anonymous(), null, 0L);
            var.storage_class |= 32L;
            dsymbolSemantic(var, sc);
            if ((sc).insert(var) == null)
                throw new AssertionError("Unreachable code!");
            var.parent = fd;
            return var;
        }

        public  void setMetaclass(InterfaceDeclaration interfaceDeclaration, Scope sc) {
            Function2<Loc,DArray<BaseClass>,InterfaceDeclaration> newMetaclass = new Function2<Loc,DArray<BaseClass>,InterfaceDeclaration>(){
                public InterfaceDeclaration invoke(Loc loc, DArray<BaseClass> metaBases){
                    return new InterfaceDeclaration(loc, null, metaBases);
                }
            };
            setMetaclass_newMetaclassInterfaceDeclaration(interfaceDeclaration, sc);
        }

        public  void setMetaclass(ClassDeclaration classDeclaration, Scope sc) {
            Function2<Loc,DArray<BaseClass>,ClassDeclaration> newMetaclass = new Function2<Loc,DArray<BaseClass>,ClassDeclaration>(){
                public ClassDeclaration invoke(Loc loc, DArray<BaseClass> metaBases){
                    return new ClassDeclaration(loc, null, metaBases, new DArray<Dsymbol>(), false);
                }
            };
            setMetaclass_newMetaclassClassDeclaration.invoke(classDeclaration, sc);
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

        public  void addSymbols(AttribDeclaration attribDeclaration, DArray<ClassDeclaration> classes, DArray<ClassDeclaration> categories) {
            DArray<Dsymbol> symbols = attribDeclaration.include(null);
            if (symbols == null)
                return ;
            {
                Slice<Dsymbol> __r1602 = (symbols).opSlice().copy();
                int __key1603 = 0;
                for (; (__key1603 < __r1602.getLength());__key1603 += 1) {
                    Dsymbol symbol = __r1602.get(__key1603);
                    symbol.addObjcSymbols(classes, categories);
                }
            }
        }

        public  void addSymbols(ClassDeclaration classDeclaration, DArray<ClassDeclaration> classes, DArray<ClassDeclaration> categories) {
            if ((__withSym.classKind == ClassKind.objc) && !__withSym.objc.isExtern && !__withSym.objc.isMeta)
                (classes).push(classDeclaration);
        }

        public  void checkOffsetof(Expression expression, AggregateDeclaration aggregateDeclaration) {
            if ((aggregateDeclaration.classKind != ClassKind.objc))
                return ;
            ByteSlice errorMessage = new ByteSlice("no property `offsetof` for member `%s` of type `%s`");
            ByteSlice supplementalMessage = new ByteSlice("`offsetof` is not available for members of Objective-C classes. Please use the Objective-C runtime instead");
            expression.error(new BytePtr("no property `offsetof` for member `%s` of type `%s`"), expression.toChars(), expression.type.toChars());
            expression.errorSupplemental(new BytePtr("`offsetof` is not available for members of Objective-C classes. Please use the Objective-C runtime instead"));
        }

        public  void checkTupleof(Expression expression, TypeClass type) {
            if ((type.sym.classKind != ClassKind.objc))
                return ;
            expression.error(new BytePtr("no property `tupleof` for type `%s`"), type.toChars());
            expression.errorSupplemental(new BytePtr("`tupleof` is not available for members of Objective-C classes. Please use the Objective-C runtime instead"));
        }

        public  boolean isUdaSelector(StructDeclaration sd) {
            if ((!pequals(sd.ident, Id.udaSelector)) || (sd.parent == null))
                return false;
            dmodule.Module _module = sd.parent.isModule();
            return (_module != null) && _module.isCoreModule(Id.attribute);
        }


        public Supported copy() {
            Supported that = new Supported();
            return that;
        }
    }
    // from template setMetaclass!(_newMetaclassClassDeclaration)
    public static void setMetaclass_newMetaclassClassDeclaration(ClassDeclaration classDeclaration, Scope sc) {
        ByteSlice errorType = new ByteSlice("class");
        {
            if ((__withSym.classKind != ClassKind.objc) || __withSym.objc.isMeta || (__withSym.objc.metaclass != null))
                return ;
            if (__withSym.objc.identifier == null)
                __withSym.objc.identifier = classDeclaration.ident;
            DArray<BaseClass> metaBases = new DArray<BaseClass>();
            {
                Slice<BaseClass> __r1600 = (__withSym.baseclasses).opSlice().copy();
                int __key1601 = 0;
                for (; (__key1601 < __r1600.getLength());__key1601 += 1) {
                    BaseClass base = __r1600.get(__key1601);
                    ClassDeclaration baseCd = (base).sym;
                    assert(baseCd != null);
                    if ((baseCd.classKind == ClassKind.objc))
                    {
                        assert(baseCd.objc.metaclass != null);
                        assert(baseCd.objc.metaclass.objc.isMeta);
                        assert(((baseCd.objc.metaclass.type.ty & 0xFF) == ENUMTY.Tclass));
                        BaseClass metaBase = new BaseClass(baseCd.objc.metaclass.type);
                        (metaBase).sym = baseCd.objc.metaclass;
                        (metaBases).push(metaBase);
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
            (__withSym.members).push(__withSym.objc.metaclass);
            __withSym.objc.metaclass.addMember(sc, classDeclaration);
            dsymbolSemantic(__withSym.objc.metaclass, sc);
        }
    }


    // from template setMetaclass!(_newMetaclassInterfaceDeclaration)
    public static void setMetaclass_newMetaclassInterfaceDeclaration(InterfaceDeclaration classDeclaration, Scope sc) {
        ByteSlice errorType = new ByteSlice("interface");
        {
            if ((__withSym.classKind != ClassKind.objc) || __withSym.objc.isMeta || (__withSym.objc.metaclass != null))
                return ;
            if (__withSym.objc.identifier == null)
                __withSym.objc.identifier = classDeclaration.ident;
            DArray<BaseClass> metaBases = new DArray<BaseClass>();
            {
                Slice<BaseClass> __r1598 = (__withSym.baseclasses).opSlice().copy();
                int __key1599 = 0;
                for (; (__key1599 < __r1598.getLength());__key1599 += 1) {
                    BaseClass base = __r1598.get(__key1599);
                    ClassDeclaration baseCd = (base).sym;
                    assert(baseCd != null);
                    if ((baseCd.classKind == ClassKind.objc))
                    {
                        assert(baseCd.objc.metaclass != null);
                        assert(baseCd.objc.metaclass.objc.isMeta);
                        assert(((baseCd.objc.metaclass.type.ty & 0xFF) == ENUMTY.Tclass));
                        BaseClass metaBase = new BaseClass(baseCd.objc.metaclass.type);
                        (metaBase).sym = baseCd.objc.metaclass;
                        (metaBases).push(metaBase);
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
            (__withSym.members).push(__withSym.objc.metaclass);
            __withSym.objc.metaclass.addMember(sc, classDeclaration);
            dsymbolSemantic(__withSym.objc.metaclass, sc);
        }
    }


}
