package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.argtypes.*;
import static org.dlang.dmd.cppmangle.*;
import static org.dlang.dmd.cppmanglewin.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.utils.*;

public class target {

    public static class Target
    {
        public int ptrsize;
        public int realsize;
        public int realpad;
        public int realalignsize;
        public int classinfosize;
        public long maxStaticDataSize;
        public int c_longsize;
        public int c_long_doublesize;
        public int criticalSectionSize;
        public boolean reverseCppOverloads;
        public boolean cppExceptions;
        public boolean twoDtorInVtable;
        // from template FPTypeProperties!(Double)
        public static class FPTypePropertiesDouble
        {
            public double max;
            public double min_normal;
            public double nan;
            public double infinity;
            public double epsilon;
            public long dig = 18L;
            public long mant_dig = 64L;
            public long max_exp = 16384L;
            public long min_exp = -16381L;
            public long max_10_exp = 4932L;
            public long min_10_exp = -4931L;
            public  void _init() {
                this.max = double;
                this.min_normal = double;
                this.nan = double;
                this.infinity = double;
                this.epsilon = double;
            }

            public FPTypePropertiesDouble(){
            }
            public FPTypePropertiesDouble copy(){
                FPTypePropertiesDouble r = new FPTypePropertiesDouble();
                r.max = max;
                r.min_normal = min_normal;
                r.nan = nan;
                r.infinity = infinity;
                r.epsilon = epsilon;
                r.dig = dig;
                r.mant_dig = mant_dig;
                r.max_exp = max_exp;
                r.min_exp = min_exp;
                r.max_10_exp = max_10_exp;
                r.min_10_exp = min_10_exp;
                return r;
            }
            public FPTypePropertiesDouble(double max, double min_normal, double nan, double infinity, double epsilon, long dig, long mant_dig, long max_exp, long min_exp, long max_10_exp, long min_10_exp) {
                this.max = max;
                this.min_normal = min_normal;
                this.nan = nan;
                this.infinity = infinity;
                this.epsilon = epsilon;
                this.dig = dig;
                this.mant_dig = mant_dig;
                this.max_exp = max_exp;
                this.min_exp = min_exp;
                this.max_10_exp = max_10_exp;
                this.min_10_exp = min_10_exp;
            }

            public FPTypePropertiesDouble opAssign(FPTypePropertiesDouble that) {
                this.max = that.max;
                this.min_normal = that.min_normal;
                this.nan = that.nan;
                this.infinity = that.infinity;
                this.epsilon = that.epsilon;
                this.dig = that.dig;
                this.mant_dig = that.mant_dig;
                this.max_exp = that.max_exp;
                this.min_exp = that.min_exp;
                this.max_10_exp = that.max_10_exp;
                this.min_10_exp = that.min_10_exp;
                return this;
            }
        }

        // from template FPTypeProperties!(Float)
        public static class FPTypePropertiesFloat
        {
            public double max;
            public double min_normal;
            public double nan;
            public double infinity;
            public double epsilon;
            public long dig = 6L;
            public long mant_dig = 24L;
            public long max_exp = 128L;
            public long min_exp = -125L;
            public long max_10_exp = 38L;
            public long min_10_exp = -37L;
            public  void _init() {
                this.max = double;
                this.min_normal = double;
                this.nan = double;
                this.infinity = double;
                this.epsilon = double;
            }

            public FPTypePropertiesFloat(){
            }
            public FPTypePropertiesFloat copy(){
                FPTypePropertiesFloat r = new FPTypePropertiesFloat();
                r.max = max;
                r.min_normal = min_normal;
                r.nan = nan;
                r.infinity = infinity;
                r.epsilon = epsilon;
                r.dig = dig;
                r.mant_dig = mant_dig;
                r.max_exp = max_exp;
                r.min_exp = min_exp;
                r.max_10_exp = max_10_exp;
                r.min_10_exp = min_10_exp;
                return r;
            }
            public FPTypePropertiesFloat(double max, double min_normal, double nan, double infinity, double epsilon, long dig, long mant_dig, long max_exp, long min_exp, long max_10_exp, long min_10_exp) {
                this.max = max;
                this.min_normal = min_normal;
                this.nan = nan;
                this.infinity = infinity;
                this.epsilon = epsilon;
                this.dig = dig;
                this.mant_dig = mant_dig;
                this.max_exp = max_exp;
                this.min_exp = min_exp;
                this.max_10_exp = max_10_exp;
                this.min_10_exp = min_10_exp;
            }

            public FPTypePropertiesFloat opAssign(FPTypePropertiesFloat that) {
                this.max = that.max;
                this.min_normal = that.min_normal;
                this.nan = that.nan;
                this.infinity = that.infinity;
                this.epsilon = that.epsilon;
                this.dig = that.dig;
                this.mant_dig = that.mant_dig;
                this.max_exp = that.max_exp;
                this.min_exp = that.min_exp;
                this.max_10_exp = that.max_10_exp;
                this.min_10_exp = that.min_10_exp;
                return this;
            }
        }

        // from template FPTypeProperties!(Double)
        public static class FPTypePropertiesDouble
        {
            public double max;
            public double min_normal;
            public double nan;
            public double infinity;
            public double epsilon;
            public long dig = 15L;
            public long mant_dig = 53L;
            public long max_exp = 1024L;
            public long min_exp = -1021L;
            public long max_10_exp = 308L;
            public long min_10_exp = -307L;
            public  void _init() {
                this.max = double;
                this.min_normal = double;
                this.nan = double;
                this.infinity = double;
                this.epsilon = double;
            }

            public FPTypePropertiesDouble(){
            }
            public FPTypePropertiesDouble copy(){
                FPTypePropertiesDouble r = new FPTypePropertiesDouble();
                r.max = max;
                r.min_normal = min_normal;
                r.nan = nan;
                r.infinity = infinity;
                r.epsilon = epsilon;
                r.dig = dig;
                r.mant_dig = mant_dig;
                r.max_exp = max_exp;
                r.min_exp = min_exp;
                r.max_10_exp = max_10_exp;
                r.min_10_exp = min_10_exp;
                return r;
            }
            public FPTypePropertiesDouble(double max, double min_normal, double nan, double infinity, double epsilon, long dig, long mant_dig, long max_exp, long min_exp, long max_10_exp, long min_10_exp) {
                this.max = max;
                this.min_normal = min_normal;
                this.nan = nan;
                this.infinity = infinity;
                this.epsilon = epsilon;
                this.dig = dig;
                this.mant_dig = mant_dig;
                this.max_exp = max_exp;
                this.min_exp = min_exp;
                this.max_10_exp = max_10_exp;
                this.min_10_exp = min_10_exp;
            }

            public FPTypePropertiesDouble opAssign(FPTypePropertiesDouble that) {
                this.max = that.max;
                this.min_normal = that.min_normal;
                this.nan = that.nan;
                this.infinity = that.infinity;
                this.epsilon = that.epsilon;
                this.dig = that.dig;
                this.mant_dig = that.mant_dig;
                this.max_exp = that.max_exp;
                this.min_exp = that.min_exp;
                this.max_10_exp = that.max_10_exp;
                this.min_10_exp = that.min_10_exp;
                return this;
            }
        }

        public FPTypePropertiesFloat FloatProperties = new FPTypePropertiesFloat();
        public FPTypePropertiesDouble DoubleProperties = new FPTypePropertiesDouble();
        public FPTypePropertiesDouble RealProperties = new FPTypePropertiesDouble();
        public  void _init(Param params) {
            this.FloatProperties._init();
            this.DoubleProperties._init();
            this.RealProperties._init();
            this.ptrsize = 4;
            this.classinfosize = 76;
            this.maxStaticDataSize = 2147483647L;
            if (params.isLP64)
            {
                this.ptrsize = 8;
                this.classinfosize = 152;
            }
            if (((((params.isLinux || params.isFreeBSD) || params.isOpenBSD) || params.isDragonFlyBSD) || params.isSolaris))
            {
                this.realsize = 12;
                this.realpad = 2;
                this.realalignsize = 4;
                this.c_longsize = 4;
                this.twoDtorInVtable = true;
            }
            else if (params.isOSX)
            {
                this.realsize = 16;
                this.realpad = 6;
                this.realalignsize = 16;
                this.c_longsize = 4;
                this.twoDtorInVtable = true;
            }
            else if (params.isWindows)
            {
                this.realsize = 10;
                this.realpad = 0;
                this.realalignsize = 2;
                this.reverseCppOverloads = true;
                this.twoDtorInVtable = false;
                this.c_longsize = 4;
                if (this.ptrsize == 4)
                {
                    this.maxStaticDataSize = 16777216L;
                }
            }
            else
                throw new AssertionError("Unreachable code!");
            if (params.is64bit)
            {
                if ((((params.isLinux || params.isFreeBSD) || params.isDragonFlyBSD) || params.isSolaris))
                {
                    this.realsize = 16;
                    this.realpad = 6;
                    this.realalignsize = 16;
                    this.c_longsize = 8;
                }
                else if (params.isOSX)
                {
                    this.c_longsize = 8;
                }
            }
            this.c_long_doublesize = this.realsize;
            if ((params.is64bit && params.isWindows))
                this.c_long_doublesize = 8;
            this.criticalSectionSize = getCriticalSectionSize(params);
            this.cppExceptions = (((params.isLinux || params.isFreeBSD) || params.isDragonFlyBSD) || params.isOSX);
        }

        public  void deinitialize() {
            this = new Target(0, 0, 0, 0, 0, 0L, 0, 0, 0, false, false, false, new FPTypePropertiesFloat(double, double, double, double, double, 6L, 24L, 128L, -125L, 38L, -37L), new FPTypePropertiesDouble(double, double, double, double, double, 15L, 53L, 1024L, -1021L, 308L, -307L), new FPTypePropertiesDouble(double, double, double, double, double, 18L, 64L, 16384L, -16381L, 4932L, -4931L)).copy();
        }

        public  int alignsize(Type type) {
            assert(type.isTypeBasic() != null);
            switch ((type.ty & 0xFF))
            {
                case 23:
                case 26:
                case 29:
                    return target.realalignsize;
                case 27:
                    if ((((((global.params.isLinux || global.params.isOSX) || global.params.isFreeBSD) || global.params.isOpenBSD) || global.params.isDragonFlyBSD) || global.params.isSolaris))
                        return 4;
                    break;
                case 19:
                case 20:
                case 22:
                case 25:
                case 28:
                    if ((((((global.params.isLinux || global.params.isOSX) || global.params.isFreeBSD) || global.params.isOpenBSD) || global.params.isDragonFlyBSD) || global.params.isSolaris))
                        return global.params.is64bit ? 8 : 4;
                    break;
                default:
                break;
            }
            return (int)type.size(Loc.initial);
        }

        public  int fieldalign(Type type) {
            int size = type.alignsize();
            if (((global.params.is64bit || global.params.isOSX) && (size == 16 || size == 32)))
                return size;
            return 8 < size ? 8 : size;
        }

        public  int critsecsize() {
            return this.criticalSectionSize;
        }

        public static int getCriticalSectionSize(Param params) {
            if (params.isWindows)
            {
                return params.isLP64 ? 40 : 24;
            }
            else if (params.isLinux)
            {
                if (params.is64bit)
                    return params.isLP64 ? 40 : 32;
                else
                    return params.isLP64 ? 40 : 24;
            }
            else if (params.isFreeBSD)
            {
                return params.isLP64 ? 8 : 4;
            }
            else if (params.isOpenBSD)
            {
                return params.isLP64 ? 8 : 4;
            }
            else if (params.isDragonFlyBSD)
            {
                return params.isLP64 ? 8 : 4;
            }
            else if (params.isOSX)
            {
                return params.isLP64 ? 64 : 44;
            }
            else if (params.isSolaris)
            {
                return 24;
            }
            throw new AssertionError("Unreachable code!");
        }

        public  Type va_listType() {
            if (global.params.isWindows)
            {
                return Type.tchar.pointerTo();
            }
            else if ((((((global.params.isLinux || global.params.isFreeBSD) || global.params.isOpenBSD) || global.params.isDragonFlyBSD) || global.params.isSolaris) || global.params.isOSX))
            {
                if (global.params.is64bit)
                {
                    return (new TypeIdentifier(Loc.initial, Identifier.idPool(new ByteSlice("__va_list_tag")))).pointerTo();
                }
                else
                {
                    return Type.tchar.pointerTo();
                }
            }
            else
            {
                throw new AssertionError("Unreachable code!");
            }
        }

        public  boolean isXmmSupported() {
            return (global.params.is64bit || global.params.isOSX);
        }

        public  int isVectorTypeSupported(int sz, Type type) {
            if (!(this.isXmmSupported()))
                return 1;
            switch ((type.ty & 0xFF))
            {
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 21:
                case 19:
                case 20:
                case 22:
                    break;
                default:
                return 2;
            }
            if ((sz != 16 && !((global.params.cpu >= CPU.avx && sz == 32))))
                return 3;
            return 0;
        }

        public  boolean isVectorOpSupported(Type type, byte op, Type t2) {
            if ((type.ty & 0xFF) != ENUMTY.Tvector)
                return true;
            TypeVector tvec = (TypeVector)type;
            boolean supported = false;
            switch ((op & 0xFF))
            {
                case 8:
                    case 43:
                        supported = tvec.isscalar();
                        break;
                case 54:
                    case 55:
                        case 56:
                            case 57:
                                case 58:
                                    case 59:
                                        case 60:
                                            case 61:
                                                supported = false;
                                                break;
                case 64:
                    case 66:
                        case 65:
                            case 67:
                                case 68:
                                    case 69:
                                        supported = false;
                                        break;
                case 74:
                    case 76:
                        case 75:
                            case 77:
                                supported = tvec.isscalar();
                                break;
                case 78:
                    case 81:
                        if (((tvec.isfloating() || tvec.elementType().size(Loc.initial) == 2L) || (global.params.cpu >= CPU.sse4_1 && tvec.elementType().size(Loc.initial) == 4L)))
                            supported = true;
                        else
                            supported = false;
                        break;
                case 79:
                    case 82:
                        supported = tvec.isfloating();
                        break;
                case 80:
                    case 83:
                        supported = false;
                        break;
                case 84:
                    case 87:
                        case 85:
                            case 88:
                                case 86:
                                    case 89:
                                        supported = tvec.isintegral();
                                        break;
                case 91:
                    supported = false;
                    break;
                case 92:
                    supported = tvec.isintegral();
                    break;
                case 226:
                    case 227:
                        supported = false;
                        break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            return supported;
        }

        public  BytePtr toCppMangle(Dsymbol s) {
            return toCppMangleItanium(s);
        }

        public  BytePtr cppTypeInfoMangle(ClassDeclaration cd) {
            return cppTypeInfoMangleItanium(cd);
        }

        public  BytePtr cppTypeMangle(Type t) {
            return null;
        }

        public  Type cppParameterType(Parameter p) {
            Type t = p.type.merge2();
            if ((p.storageClass & 2101248L) != 0)
                t = t.referenceTo();
            else if ((p.storageClass & 8192L) != 0)
            {
                Type td = new TypeFunction(new ParameterList(null, VarArg.none), t, LINK.d, 0L);
                td = new TypeDelegate(td);
                t = merge(t);
            }
            return t;
        }

        public  boolean cppFundamentalType(Type t, Ref<Boolean> isFundamental) {
            return false;
        }

        public  int systemLinkage() {
            return global.params.isWindows ? LINK.windows : LINK.c;
        }

        public  TypeTuple toArgTypes(Type t) {
            if ((global.params.is64bit && global.params.isWindows))
                return null;
            return toArgTypes(t);
        }

        public  boolean isReturnOnStack(TypeFunction tf, boolean needsThis) {
            if (tf.isref)
            {
                return false;
            }
            Type tn = tf.next.toBasetype();
            long sz = tn.size();
            Type tns = tn;
            if ((global.params.isWindows && global.params.is64bit))
            {
                if ((tns.ty & 0xFF) == ENUMTY.Tcomplex32)
                    return true;
                if (tns.isscalar())
                    return false;
                tns = tns.baseElemOf();
                if ((tns.ty & 0xFF) == ENUMTY.Tstruct)
                {
                    StructDeclaration sd = ((TypeStruct)tns).sym;
                    if ((tf.linkage == LINK.cpp && needsThis))
                        return true;
                    if ((!(sd.isPOD()) || sz > 8L))
                        return true;
                    if (sd.fields.length == 0)
                        return true;
                }
                if ((sz <= 16L && !((sz & sz - 1L) != 0)))
                    return false;
                return true;
            }
            else if ((global.params.isWindows && global.params.mscoff))
            {
                Type tb = tns.baseElemOf();
                if ((tb.ty & 0xFF) == ENUMTY.Tstruct)
                {
                    if ((tf.linkage == LINK.cpp && needsThis))
                        return true;
                }
            }
            while(true) try {
            /*Lagain:*/
                if ((tns.ty & 0xFF) == ENUMTY.Tsarray)
                {
                    tns = tns.baseElemOf();
                    if ((tns.ty & 0xFF) != ENUMTY.Tstruct)
                    {
                    /*L2:*/
                        if (((global.params.isLinux && tf.linkage != LINK.d) && !(global.params.is64bit)))
                        {
                        }
                        else
                        {
                            switch (sz)
                            {
                                case 1L:
                                case 2L:
                                case 4L:
                                case 8L:
                                    return false;
                                default:
                                break;
                            }
                        }
                        return true;
                    }
                }
                if ((tns.ty & 0xFF) == ENUMTY.Tstruct)
                {
                    StructDeclaration sd = ((TypeStruct)tns).sym;
                    if (((global.params.isLinux && tf.linkage != LINK.d) && !(global.params.is64bit)))
                    {
                        return true;
                    }
                    if (((((global.params.isWindows && tf.linkage == LINK.cpp) && !(global.params.is64bit)) && sd.isPOD()) && sd.ctor != null))
                    {
                        return true;
                    }
                    if ((sd.arg1type != null && !(sd.arg2type != null)))
                    {
                        tns = sd.arg1type;
                        if ((tns.ty & 0xFF) != ENUMTY.Tstruct)
                            /*goto L2*/throw Dispatch0.INSTANCE;
                        /*goto Lagain*/throw Dispatch0.INSTANCE;
                    }
                    else if (((global.params.is64bit && !(sd.arg1type != null)) && !(sd.arg2type != null)))
                        return true;
                    else if (sd.isPOD())
                    {
                        switch (sz)
                        {
                            case 1L:
                            case 2L:
                            case 4L:
                            case 8L:
                                return false;
                            case 16L:
                                if ((!(global.params.isWindows) && global.params.is64bit))
                                    return false;
                                break;
                            default:
                            break;
                        }
                    }
                    return true;
                }
                else if (((((((global.params.isLinux || global.params.isOSX) || global.params.isFreeBSD) || global.params.isSolaris) || global.params.isDragonFlyBSD) && tf.linkage == LINK.c) && tns.iscomplex()))
                {
                    if ((tns.ty & 0xFF) == ENUMTY.Tcomplex32)
                        return false;
                    else
                        return true;
                }
                else
                {
                    return false;
                }
                break;
            } catch(Dispatch0 __d){}
        }

        public  long parameterSize(Loc loc, Type t) {
            if ((!(global.params.is64bit) && (global.params.isFreeBSD || global.params.isOSX)))
            {
                if ((t.ty & 0xFF) == ENUMTY.Tstruct)
                {
                    TypeStruct ts = (TypeStruct)t;
                    if (ts.sym.hasNoFields)
                        return 0L;
                }
            }
            long sz = t.size(loc);
            return global.params.is64bit ? sz + 7L & -8L : sz + 3L & -4L;
        }


        public static class TargetInfoKeys 
        {
            public static final int cppRuntimeLibrary = 0;
            public static final int cppStd = 1;
            public static final int floatAbi = 2;
            public static final int objectFormat = 3;
        }

        public  Expression getTargetInfo(BytePtr name, Loc loc) {
            Function1<ByteSlice,StringExp> stringExp = new Function1<ByteSlice,StringExp>(){
                public StringExp invoke(ByteSlice sval){
                    return new StringExp(loc, toBytePtr(sval), sval.getLength());
                }
            };
            switch (__switch(toDString(name)))
            {
                case 2:
                    if (global.params.isWindows)
                        return stringExp.invoke(global.params.mscoff ? new ByteSlice("coff") : new ByteSlice("omf"));
                    else if (global.params.isOSX)
                        return stringExp.invoke(new ByteSlice("macho"));
                    else
                        return stringExp.invoke(new ByteSlice("elf"));
                case 1:
                    return stringExp.invoke(new ByteSlice("hard"));
                case 3:
                    if (global.params.isWindows)
                    {
                        if (global.params.mscoff)
                            return stringExp.invoke(global.params.mscrtlib);
                        return stringExp.invoke(new ByteSlice("snn"));
                    }
                    return stringExp.invoke(new ByteSlice(""));
                case 0:
                    return new IntegerExp((long)global.params.cplusplus);
                default:
                return null;
            }
        }

        public Target(){
            FloatProperties = new FPTypePropertiesFloat();
            DoubleProperties = new FPTypePropertiesDouble();
            RealProperties = new FPTypePropertiesDouble();
        }
        public Target copy(){
            Target r = new Target();
            r.ptrsize = ptrsize;
            r.realsize = realsize;
            r.realpad = realpad;
            r.realalignsize = realalignsize;
            r.classinfosize = classinfosize;
            r.maxStaticDataSize = maxStaticDataSize;
            r.c_longsize = c_longsize;
            r.c_long_doublesize = c_long_doublesize;
            r.criticalSectionSize = criticalSectionSize;
            r.reverseCppOverloads = reverseCppOverloads;
            r.cppExceptions = cppExceptions;
            r.twoDtorInVtable = twoDtorInVtable;
            r.max = max;
            r.min_normal = min_normal;
            r.nan = nan;
            r.infinity = infinity;
            r.epsilon = epsilon;
            r.dig = dig;
            r.mant_dig = mant_dig;
            r.max_exp = max_exp;
            r.min_exp = min_exp;
            r.max_10_exp = max_10_exp;
            r.min_10_exp = min_10_exp;
            r.FloatProperties = FloatProperties.copy();
            r.DoubleProperties = DoubleProperties.copy();
            r.RealProperties = RealProperties.copy();
            return r;
        }
        public Target(int ptrsize, int realsize, int realpad, int realalignsize, int classinfosize, long maxStaticDataSize, int c_longsize, int c_long_doublesize, int criticalSectionSize, boolean reverseCppOverloads, boolean cppExceptions, boolean twoDtorInVtable, real_t max, real_t min_normal, real_t nan, real_t infinity, real_t epsilon, d_int64 dig, d_int64 mant_dig, d_int64 max_exp, d_int64 min_exp, d_int64 max_10_exp, d_int64 min_10_exp, FPTypePropertiesFloat FloatProperties, FPTypePropertiesDouble DoubleProperties, FPTypePropertiesDouble RealProperties) {
            this.ptrsize = ptrsize;
            this.realsize = realsize;
            this.realpad = realpad;
            this.realalignsize = realalignsize;
            this.classinfosize = classinfosize;
            this.maxStaticDataSize = maxStaticDataSize;
            this.c_longsize = c_longsize;
            this.c_long_doublesize = c_long_doublesize;
            this.criticalSectionSize = criticalSectionSize;
            this.reverseCppOverloads = reverseCppOverloads;
            this.cppExceptions = cppExceptions;
            this.twoDtorInVtable = twoDtorInVtable;
            this.max = max;
            this.min_normal = min_normal;
            this.nan = nan;
            this.infinity = infinity;
            this.epsilon = epsilon;
            this.dig = dig;
            this.mant_dig = mant_dig;
            this.max_exp = max_exp;
            this.min_exp = min_exp;
            this.max_10_exp = max_10_exp;
            this.min_10_exp = min_10_exp;
            this.FloatProperties = FloatProperties;
            this.DoubleProperties = DoubleProperties;
            this.RealProperties = RealProperties;
        }

        public Target opAssign(Target that) {
            this.ptrsize = that.ptrsize;
            this.realsize = that.realsize;
            this.realpad = that.realpad;
            this.realalignsize = that.realalignsize;
            this.classinfosize = that.classinfosize;
            this.maxStaticDataSize = that.maxStaticDataSize;
            this.c_longsize = that.c_longsize;
            this.c_long_doublesize = that.c_long_doublesize;
            this.criticalSectionSize = that.criticalSectionSize;
            this.reverseCppOverloads = that.reverseCppOverloads;
            this.cppExceptions = that.cppExceptions;
            this.twoDtorInVtable = that.twoDtorInVtable;
            this.max = that.max;
            this.min_normal = that.min_normal;
            this.nan = that.nan;
            this.infinity = that.infinity;
            this.epsilon = that.epsilon;
            this.dig = that.dig;
            this.mant_dig = that.mant_dig;
            this.max_exp = that.max_exp;
            this.min_exp = that.min_exp;
            this.max_10_exp = that.max_10_exp;
            this.min_10_exp = that.min_10_exp;
            this.FloatProperties = that.FloatProperties;
            this.DoubleProperties = that.DoubleProperties;
            this.RealProperties = that.RealProperties;
            return this;
        }
    }
    static Target target = new Target();
}
