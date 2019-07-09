package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.gluelayer.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.visitor.*;

public class typinf {

    public static void genTypeInfo(Loc loc, Type torig, Scope sc) {
        if ((sc == null) || (((sc).flags & 128) == 0))
        {
            if (!global.params.useTypeInfo)
            {
                error(loc, new BytePtr("`TypeInfo` cannot be used with -betterC"));
                fatal();
            }
        }
        if (Type.dtypeinfo == null)
        {
            error(loc, new BytePtr("`object.TypeInfo` could not be found, but is implicitly used"));
            fatal();
        }
        Type t = torig.merge2();
        if (t.vtinfo == null)
        {
            if (t.isShared())
                t.vtinfo = TypeInfoSharedDeclaration.create(t);
            else if (t.isConst())
                t.vtinfo = TypeInfoConstDeclaration.create(t);
            else if (t.isImmutable())
                t.vtinfo = TypeInfoInvariantDeclaration.create(t);
            else if (t.isWild())
                t.vtinfo = TypeInfoWildDeclaration.create(t);
            else
                t.vtinfo = getTypeInfoDeclaration(t);
            assert(t.vtinfo != null);
            if (!builtinTypeInfo(t))
            {
                if (sc != null)
                {
                    dmodule.Module m = (sc)._module.importedFrom;
                    (m.members).push(t.vtinfo);
                }
                else
                {
                    toObjFile(t.vtinfo, global.params.multiobj);
                }
            }
        }
        if (torig.vtinfo == null)
            torig.vtinfo = t.vtinfo;
        assert(torig.vtinfo != null);
    }

    public static Type getTypeInfoType(Loc loc, Type t, Scope sc) {
        assert(((t.ty & 0xFF) != ENUMTY.Terror));
        genTypeInfo(loc, t, sc);
        return t.vtinfo.type;
    }

    public static TypeInfoDeclaration getTypeInfoDeclaration(Type t) {
        switch ((t.ty & 0xFF))
        {
            case 3:
                return TypeInfoPointerDeclaration.create(t);
            case 0:
                return TypeInfoArrayDeclaration.create(t);
            case 1:
                return TypeInfoStaticArrayDeclaration.create(t);
            case 2:
                return TypeInfoAssociativeArrayDeclaration.create(t);
            case 8:
                return TypeInfoStructDeclaration.create(t);
            case 41:
                return TypeInfoVectorDeclaration.create(t);
            case 9:
                return TypeInfoEnumDeclaration.create(t);
            case 5:
                return TypeInfoFunctionDeclaration.create(t);
            case 10:
                return TypeInfoDelegateDeclaration.create(t);
            case 37:
                return TypeInfoTupleDeclaration.create(t);
            case 7:
                if (((TypeClass)t).sym.isInterfaceDeclaration() != null)
                    return TypeInfoInterfaceDeclaration.create(t);
                else
                    return TypeInfoClassDeclaration.create(t);
            default:
            return TypeInfoDeclaration.create(t);
        }
    }

    public static boolean isSpeculativeType(Type t) {
        Function1<TypeVector,Boolean> visitVector = new Function1<TypeVector,Boolean>(){
            public Boolean invoke(TypeVector t) {
                return isSpeculativeType(t.basetype);
            }
        };
        Function1<TypeAArray,Boolean> visitAArray = new Function1<TypeAArray,Boolean>(){
            public Boolean invoke(TypeAArray t) {
                return isSpeculativeType(t.index) || isSpeculativeType(t.next);
            }
        };
        Function1<TypeStruct,Boolean> visitStruct = new Function1<TypeStruct,Boolean>(){
            public Boolean invoke(TypeStruct t) {
                StructDeclaration sd = t.sym;
                {
                    TemplateInstance ti = sd.isInstantiated();
                    if ((ti) != null)
                    {
                        if (!ti.needsCodegen())
                        {
                            if ((ti.minst != null) || sd.requestTypeInfo)
                                return false;
                            return true;
                        }
                    }
                    else
                    {
                    }
                }
                return false;
            }
        };
        Function1<TypeClass,Boolean> visitClass = new Function1<TypeClass,Boolean>(){
            public Boolean invoke(TypeClass t) {
                ClassDeclaration sd = t.sym;
                {
                    TemplateInstance ti = sd.isInstantiated();
                    if ((ti) != null)
                    {
                        if (!ti.needsCodegen() && (ti.minst == null))
                        {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
        Function1<TypeTuple,Boolean> visitTuple = new Function1<TypeTuple,Boolean>(){
            public Boolean invoke(TypeTuple t) {
                if (t.arguments != null)
                {
                    {
                        Slice<Parameter> __r1726 = (t.arguments).opSlice().copy();
                        int __key1727 = 0;
                        for (; (__key1727 < __r1726.getLength());__key1727 += 1) {
                            Parameter arg = __r1726.get(__key1727);
                            if (isSpeculativeType(arg.type))
                                return true;
                        }
                    }
                }
                return false;
            }
        };
        if (t == null)
            return false;
        Type tb = t.toBasetype();
        switch ((tb.ty & 0xFF))
        {
            case 41:
                return visitVector.invoke(tb.isTypeVector());
            case 2:
                return visitAArray.invoke(tb.isTypeAArray());
            case 8:
                return visitStruct.invoke(tb.isTypeStruct());
            case 7:
                return visitClass.invoke(tb.isTypeClass());
            case 37:
                return visitTuple.invoke(tb.isTypeTuple());
            case 9:
                return false;
            default:
            return isSpeculativeType(tb.nextOf());
        }
    }

    public static boolean builtinTypeInfo(Type t) {
        if ((t.isTypeBasic() != null) || ((t.ty & 0xFF) == ENUMTY.Tclass) || ((t.ty & 0xFF) == ENUMTY.Tnull))
            return t.mod == 0;
        if (((t.ty & 0xFF) == ENUMTY.Tarray))
        {
            Type next = t.nextOf();
            return (t.mod == 0) && (next.isTypeBasic() != null) && (next.mod == 0) || ((next.ty & 0xFF) == ENUMTY.Tchar) && ((next.mod & 0xFF) == MODFlags.immutable_) || ((next.ty & 0xFF) == ENUMTY.Tchar) && ((next.mod & 0xFF) == MODFlags.const_);
        }
        return false;
    }

}
