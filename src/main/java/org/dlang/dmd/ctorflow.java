package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.globals.*;

public class ctorflow {


    public static class CSX 
    {
        public static final int none = (int)0;
        public static final int this_ctor = (int)1;
        public static final int super_ctor = (int)2;
        public static final int label = (int)4;
        public static final int return_ = (int)8;
        public static final int any_ctor = (int)16;
        public static final int halt = (int)32;
        public static final int deprecate_18719 = (int)64;
    }

    public static class FieldInit
    {
        public int csx = 0;
        public Loc loc = new Loc();
        public FieldInit(){
            loc = new Loc();
        }
        public FieldInit copy(){
            FieldInit r = new FieldInit();
            r.csx = csx;
            r.loc = loc.copy();
            return r;
        }
        public FieldInit(int csx, Loc loc) {
            this.csx = csx;
            this.loc = loc;
        }

        public FieldInit opAssign(FieldInit that) {
            this.csx = that.csx;
            this.loc = that.loc;
            return this;
        }
    }
    public static class CtorFlow
    {
        public int callSuper = 0;
        public Slice<FieldInit> fieldinit;
        public  void allocFieldinit(int dim) {
            this.fieldinit = (ptr(new FieldInit[dim])).slice(0,dim).copy();
        }
        public  void freeFieldinit() {
            if ((FieldInit)this.fieldinit != null)
                Mem.xfree((FieldInit)this.fieldinit);
            this.fieldinit = new Slice<FieldInit>().copy();
        }
        public  CtorFlow clone() {
            return new CtorFlow(this.callSuper, arraydup(this.fieldinit));
        }
        public  void orCSX(int csx) {
            this.callSuper |= csx;
            {
                Slice<FieldInit> __r890 = this.fieldinit.copy();
                int __key891 = 0;
                for (; (__key891 < __r890.getLength());__key891 += 1) {
                    FieldInit u = __r890.get(__key891).copy();
                    u.csx |= csx;
                }
            }
        }
        public  void OR(CtorFlow ctorflow) {
            this.callSuper |= ctorflow.callSuper;
            if ((this.fieldinit.getLength() != 0) && (ctorflow.fieldinit.getLength() != 0))
            {
                assert((this.fieldinit.getLength() == ctorflow.fieldinit.getLength()));
                {
                    Slice<FieldInit> __r893 = ctorflow.fieldinit.copy();
                    int __key892 = 0;
                    for (; (__key892 < __r893.getLength());__key892 += 1) {
                        FieldInit u = __r893.get(__key892).copy();
                        int i = __key892;
                        FieldInit fi = this.fieldinit.get(i);
                        (fi).csx |= u.csx;
                        if (((fi).loc == new Loc(null, 0, 0)))
                            (fi).loc = u.loc.copy();
                    }
                }
            }
        }
        public CtorFlow(){
        }
        public CtorFlow copy(){
            CtorFlow r = new CtorFlow();
            r.callSuper = callSuper;
            r.fieldinit = fieldinit.copy();
            return r;
        }
        public CtorFlow(int callSuper, Slice<FieldInit> fieldinit) {
            this.callSuper = callSuper;
            this.fieldinit = fieldinit;
        }

        public CtorFlow opAssign(CtorFlow that) {
            this.callSuper = that.callSuper;
            this.fieldinit = that.fieldinit;
            return this;
        }
    }
    public static boolean mergeCallSuper(Ref<Integer> a, int b) {
        if ((b == a.value))
            return true;
        boolean aAll = (a.value & 3) != 0;
        boolean bAll = (b & 3) != 0;
        boolean aAny = (a.value & 16) != 0;
        boolean bAny = (b & 16) != 0;
        boolean aRet = (a.value & 8) != 0;
        boolean bRet = (b & 8) != 0;
        boolean aHalt = (a.value & 32) != 0;
        boolean bHalt = (b & 32) != 0;
        if (aHalt && bHalt)
        {
            a.value = CSX.halt;
        }
        else if (!bHalt && bRet && !bAny && aAny || !aHalt && aRet && !aAny && bAny)
        {
            return false;
        }
        else if (bHalt || bRet && bAll)
        {
            a.value |= b & 20;
        }
        else if (aHalt || aRet && aAll)
        {
            a.value = (int)(b | a.value & 20);
        }
        else if (((aAll ? 1 : 0) != (bAll ? 1 : 0)))
            return false;
        else
        {
            if (bRet && !bAny)
                a.value |= 8;
            a.value |= b & 20;
        }
        return true;
    }
    public static boolean mergeFieldInit(Ref<Integer> a, int b) {
        if ((b == a.value))
            return true;
        boolean aRet = (a.value & 8) != 0;
        boolean bRet = (b & 8) != 0;
        boolean aHalt = (a.value & 32) != 0;
        boolean bHalt = (b & 32) != 0;
        if (aHalt && bHalt)
        {
            a.value = CSX.halt;
            return true;
        }
        boolean ok = false;
        if (!bHalt && bRet)
        {
            ok = ((b & 1) != 0);
            a.value = a.value;
        }
        else if (!aHalt && aRet)
        {
            ok = ((a.value & 1) != 0);
            a.value = b;
        }
        else if (bHalt)
        {
            ok = ((a.value & 1) != 0);
            a.value = a.value;
        }
        else if (aHalt)
        {
            ok = ((b & 1) != 0);
            a.value = b;
        }
        else
        {
            ok = ((a.value ^ b) & 1) == 0;
            a.value |= b;
        }
        return ok;
    }
}
