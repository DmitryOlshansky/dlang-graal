package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.tokens.*;

public class safe {

    public static boolean checkUnsafeAccess(Scope sc, Expression e, boolean readonly, boolean printmsg) {
        if (((e.op & 0xFF) != 27))
            return false;
        DotVarExp dve = (DotVarExp)e;
        {
            VarDeclaration v = dve.var.isVarDeclaration();
            if ((v) != null)
            {
                if (((sc).intypeof != 0) || ((sc).func == null) || !(sc).func.isSafeBypassingInference())
                    return false;
                AggregateDeclaration ad = v.toParent2().isAggregateDeclaration();
                if (ad == null)
                    return false;
                boolean hasPointers = v.type.hasPointers();
                if (hasPointers)
                {
                    if ((ad.sizeok != Sizeok.done))
                        expr(ad.determineSize(ad.loc));
                    if (v.overlapped && (sc).func.setUnsafe())
                    {
                        if (printmsg)
                            e.error(new BytePtr("field `%s.%s` cannot access pointers in `@safe` code that overlap other fields"), ad.toChars(), v.toChars());
                        return true;
                    }
                }
                if (readonly || !e.type.isMutable())
                    return false;
                if (hasPointers && ((v.type.toBasetype().ty & 0xFF) != ENUMTY.Tstruct))
                {
                    if ((ad.type.alignment() < target.ptrsize) || ((v.offset & target.ptrsize - 1) != 0) && (sc).func.setUnsafe())
                    {
                        if (printmsg)
                            e.error(new BytePtr("field `%s.%s` cannot modify misaligned pointers in `@safe` code"), ad.toChars(), v.toChars());
                        return true;
                    }
                }
                if (v.overlapUnsafe && (sc).func.setUnsafe())
                {
                    if (printmsg)
                        e.error(new BytePtr("field `%s.%s` cannot modify fields in `@safe` code that overlap fields with other storage classes"), ad.toChars(), v.toChars());
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isSafeCast(Expression e, Type tfrom, Type tto) {
        if (tfrom.implicitConvTo(tto) != 0)
            return true;
        if (!tto.hasPointers())
            return true;
        Type tfromb = tfrom.toBasetype();
        Type ttob = tto.toBasetype();
        if (((ttob.ty & 0xFF) == ENUMTY.Tclass) && ((tfromb.ty & 0xFF) == ENUMTY.Tclass))
        {
            ClassDeclaration cdfrom = tfromb.isClassHandle();
            ClassDeclaration cdto = ttob.isClassHandle();
            IntRef offset = ref(0);
            if (!cdfrom.isBaseOf(cdto, ptr(offset)) && !((cdfrom.isInterfaceDeclaration() != null) || (cdto.isInterfaceDeclaration() != null) && (cdfrom.classKind == ClassKind.d) && (cdto.classKind == ClassKind.d)))
                return false;
            if (cdfrom.isCPPinterface() || cdto.isCPPinterface())
                return false;
            if (!MODimplicitConv(tfromb.mod, ttob.mod))
                return false;
            return true;
        }
        if (((ttob.ty & 0xFF) == ENUMTY.Tarray) && ((tfromb.ty & 0xFF) == ENUMTY.Tsarray))
            tfromb = tfromb.nextOf().arrayOf();
        if (((ttob.ty & 0xFF) == ENUMTY.Tarray) && ((tfromb.ty & 0xFF) == ENUMTY.Tarray) || ((ttob.ty & 0xFF) == ENUMTY.Tpointer) && ((tfromb.ty & 0xFF) == ENUMTY.Tpointer))
        {
            Type ttobn = ttob.nextOf().toBasetype();
            Type tfromn = tfromb.nextOf().toBasetype();
            if (((tfromn.ty & 0xFF) == ENUMTY.Tvoid) && ttobn.isMutable())
            {
                if (((ttob.ty & 0xFF) == ENUMTY.Tarray) && ((e.op & 0xFF) == 47))
                    return true;
                return false;
            }
            if (((ttobn.ty & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)ttobn).sym.members == null) || ((tfromn.ty & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)tfromn).sym.members == null))
                return false;
            boolean frompointers = tfromn.hasPointers();
            boolean topointers = ttobn.hasPointers();
            if (frompointers && !topointers && ttobn.isMutable())
                return false;
            if (!frompointers && topointers)
                return false;
            if (!topointers && ((ttobn.ty & 0xFF) != ENUMTY.Tfunction) && ((tfromn.ty & 0xFF) != ENUMTY.Tfunction) && ((ttob.ty & 0xFF) == ENUMTY.Tarray) || (ttobn.size() <= tfromn.size()) && MODimplicitConv(tfromn.mod, ttobn.mod))
            {
                return true;
            }
        }
        return false;
    }

}
