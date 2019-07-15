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
import static org.dlang.dmd.ast_node.*;
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.denum.*;
import static org.dlang.dmd.dmangle.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.gluelayer.*;
import static org.dlang.dmd.hdrgen.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.opover.*;
import static org.dlang.dmd.target.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.visitor.*;

public class mtype {
    private static final byte[] initializer_0 = {(byte)12, (byte)13, (byte)14, (byte)15, (byte)16, (byte)17, (byte)18, (byte)19, (byte)20, (byte)42, (byte)43, (byte)21, (byte)22, (byte)23, (byte)24, (byte)25, (byte)26, (byte)27, (byte)28, (byte)29, (byte)30, (byte)31, (byte)32, (byte)33, (byte)34};
    static ByteSlice _initbasetab = slice(initializer_0);

    static int LOGDOTEXP = 0;
    static int LOGDEFAULTINIT = 0;
    static long SIZE_INVALID = -1L;
    public static boolean MODimplicitConv(byte modfrom, byte modto) {
        if (((modfrom & 0xFF) == (modto & 0xFF)))
        {
            return true;
        }
        // from template X!(IntegerInteger)
        Function2<Integer,Integer,Integer> XIntegerInteger = new Function2<Integer,Integer,Integer>() {
            public Integer invoke(Integer m, Integer n) {
             {
                return m << 4 | n;
            }}

        };

        // from template X!(IntegerInteger)
        // removed duplicate function, [["int Xint, intIntegerInteger"]] signature: int Xint, intIntegerInteger

        // from template X!(IntegerInteger)
        // removed duplicate function, [["int Xint, intIntegerInteger"]] signature: int Xint, intIntegerInteger

        switch (XIntegerInteger.invoke((modfrom & 0xFF) & -3, (modto & 0xFF) & -3))
        {
            case 1:
            case 129:
            case 137:
            case 145:
                return ((modfrom & 0xFF) & MODFlags.shared_) == ((modto & 0xFF) & MODFlags.shared_);
            case 65:
            case 73:
                return true;
            default:
            return false;
        }
    }

    public static int MODmethodConv(byte modfrom, byte modto) {
        if (((modfrom & 0xFF) == (modto & 0xFF)))
        {
            return MATCH.exact;
        }
        if (MODimplicitConv(modfrom, modto))
        {
            return MATCH.constant;
        }
        // from template X!(ByteByte)
        Function2<Byte,Byte,Integer> XByteByte = new Function2<Byte,Byte,Integer>() {
            public Integer invoke(Byte m, Byte n) {
             {
                return (m & 0xFF) << 4 | (n & 0xFF);
            }}

        };

        // from template X!(IntegerInteger)
        Function2<Integer,Integer,Integer> XIntegerInteger = new Function2<Integer,Integer,Integer>() {
            public Integer invoke(Integer m, Integer n) {
             {
                return m << 4 | n;
            }}

        };

        // from template X!(IntegerInteger)
        // removed duplicate function, [["int Xbyte, byteByteByte", "int Xint, intIntegerInteger"]] signature: int Xint, intIntegerInteger

        switch (XByteByte.invoke(modfrom, modto))
        {
            case 8:
            case 72:
            case 24:
            case 152:
            case 42:
            case 106:
            case 58:
            case 186:
                return MATCH.constant;
            default:
            return MATCH.nomatch;
        }
    }

    public static byte MODmerge(byte mod1, byte mod2) {
        if (((mod1 & 0xFF) == (mod2 & 0xFF)))
        {
            return mod1;
        }
        byte result = (byte)0;
        if ((((mod1 & 0xFF) | (mod2 & 0xFF)) & MODFlags.shared_) != 0)
        {
            result |= MODFlags.shared_;
            mod1 &= -3;
            mod2 &= -3;
        }
        if (((mod1 & 0xFF) == 0) || ((mod1 & 0xFF) == MODFlags.mutable) || ((mod1 & 0xFF) == MODFlags.const_) || ((mod2 & 0xFF) == 0) || ((mod2 & 0xFF) == MODFlags.mutable) || ((mod2 & 0xFF) == MODFlags.const_))
        {
            result |= MODFlags.const_;
        }
        else
        {
            assert((((mod1 & 0xFF) & MODFlags.wild) != 0) || (((mod2 & 0xFF) & MODFlags.wild) != 0));
            result |= MODFlags.wildconst;
        }
        return result;
    }

    public static void MODtoBuffer(Ptr<OutBuffer> buf, byte mod) {
        (buf.get()).writestring(MODtoString(mod));
    }

    public static BytePtr MODtoChars(byte mod) {
        return toBytePtr(MODtoString(mod));
    }

    public static ByteSlice MODtoString(byte mod) {
        switch ((mod & 0xFF))
        {
            case 0:
                return new ByteSlice("");
            case 4:
                return new ByteSlice("immutable");
            case 2:
                return new ByteSlice("shared");
            case 3:
                return new ByteSlice("shared const");
            case 1:
                return new ByteSlice("const");
            case 10:
                return new ByteSlice("shared inout");
            case 8:
                return new ByteSlice("inout");
            case 11:
                return new ByteSlice("shared inout const");
            case 9:
                return new ByteSlice("inout const");
            default:
            throw SwitchError.INSTANCE;
        }
    }

    public static long ModToStc(int mod) {
        long stc = 0L;
        if ((mod & 4) != 0)
        {
            stc |= 1048576L;
        }
        if ((mod & 1) != 0)
        {
            stc |= 4L;
        }
        if ((mod & 8) != 0)
        {
            stc |= 2147483648L;
        }
        if ((mod & 2) != 0)
        {
            stc |= 536870912L;
        }
        return stc;
    }


    public static class TFlags 
    {
        public static final int integral = 1;
        public static final int floating = 2;
        public static final int unsigned = 4;
        public static final int real_ = 8;
        public static final int imaginary = 16;
        public static final int complex = 32;
        public static final int char_ = 64;
    }


    public static class ENUMTY 
    {
        public static final int Tarray = 0;
        public static final int Tsarray = 1;
        public static final int Taarray = 2;
        public static final int Tpointer = 3;
        public static final int Treference = 4;
        public static final int Tfunction = 5;
        public static final int Tident = 6;
        public static final int Tclass = 7;
        public static final int Tstruct = 8;
        public static final int Tenum = 9;
        public static final int Tdelegate = 10;
        public static final int Tnone = 11;
        public static final int Tvoid = 12;
        public static final int Tint8 = 13;
        public static final int Tuns8 = 14;
        public static final int Tint16 = 15;
        public static final int Tuns16 = 16;
        public static final int Tint32 = 17;
        public static final int Tuns32 = 18;
        public static final int Tint64 = 19;
        public static final int Tuns64 = 20;
        public static final int Tfloat32 = 21;
        public static final int Tfloat64 = 22;
        public static final int Tfloat80 = 23;
        public static final int Timaginary32 = 24;
        public static final int Timaginary64 = 25;
        public static final int Timaginary80 = 26;
        public static final int Tcomplex32 = 27;
        public static final int Tcomplex64 = 28;
        public static final int Tcomplex80 = 29;
        public static final int Tbool = 30;
        public static final int Tchar = 31;
        public static final int Twchar = 32;
        public static final int Tdchar = 33;
        public static final int Terror = 34;
        public static final int Tinstance = 35;
        public static final int Ttypeof = 36;
        public static final int Ttuple = 37;
        public static final int Tslice = 38;
        public static final int Treturn = 39;
        public static final int Tnull = 40;
        public static final int Tvector = 41;
        public static final int Tint128 = 42;
        public static final int Tuns128 = 43;
        public static final int TTraits = 44;
        public static final int TMAX = 45;
    }


    public static class MODFlags 
    {
        public static final int const_ = 1;
        public static final int immutable_ = 4;
        public static final int shared_ = 2;
        public static final int wild = 8;
        public static final int wildconst = 9;
        public static final int mutable = 16;
    }


    public static class DotExpFlag 
    {
        public static final int gag = 1;
        public static final int noDeref = 2;
    }


    public static class VarArg 
    {
        public static final int none = 0;
        public static final int variadic = 1;
        public static final int typesafe = 2;
    }

    public static abstract class Type extends ASTNode
    {
        public byte ty = 0;
        public byte mod = 0;
        public BytePtr deco = null;
        public Type cto = null;
        public Type ito = null;
        public Type sto = null;
        public Type scto = null;
        public Type wto = null;
        public Type wcto = null;
        public Type swto = null;
        public Type swcto = null;
        public Type pto = null;
        public Type rto = null;
        public Type arrayof = null;
        public TypeInfoDeclaration vtinfo = null;
        public Ptr<TYPE> ctype = null;
        public static Type tvoid = null;
        public static Type tint8 = null;
        public static Type tuns8 = null;
        public static Type tint16 = null;
        public static Type tuns16 = null;
        public static Type tint32 = null;
        public static Type tuns32 = null;
        public static Type tint64 = null;
        public static Type tuns64 = null;
        public static Type tint128 = null;
        public static Type tuns128 = null;
        public static Type tfloat32 = null;
        public static Type tfloat64 = null;
        public static Type tfloat80 = null;
        public static Type timaginary32 = null;
        public static Type timaginary64 = null;
        public static Type timaginary80 = null;
        public static Type tcomplex32 = null;
        public static Type tcomplex64 = null;
        public static Type tcomplex80 = null;
        public static Type tbool = null;
        public static Type tchar = null;
        public static Type twchar = null;
        public static Type tdchar = null;
        public static Type tshiftcnt = null;
        public static Type tvoidptr = null;
        public static Type tstring = null;
        public static Type twstring = null;
        public static Type tdstring = null;
        public static Type tvalist = null;
        public static Type terror = null;
        public static Type tnull = null;
        public static Type tsize_t = null;
        public static Type tptrdiff_t = null;
        public static Type thash_t = null;
        public static ClassDeclaration dtypeinfo = null;
        public static ClassDeclaration typeinfoclass = null;
        public static ClassDeclaration typeinfointerface = null;
        public static ClassDeclaration typeinfostruct = null;
        public static ClassDeclaration typeinfopointer = null;
        public static ClassDeclaration typeinfoarray = null;
        public static ClassDeclaration typeinfostaticarray = null;
        public static ClassDeclaration typeinfoassociativearray = null;
        public static ClassDeclaration typeinfovector = null;
        public static ClassDeclaration typeinfoenum = null;
        public static ClassDeclaration typeinfofunction = null;
        public static ClassDeclaration typeinfodelegate = null;
        public static ClassDeclaration typeinfotypelist = null;
        public static ClassDeclaration typeinfoconst = null;
        public static ClassDeclaration typeinfoinvariant = null;
        public static ClassDeclaration typeinfoshared = null;
        public static ClassDeclaration typeinfowild = null;
        public static TemplateDeclaration rtinfo = null;
        public static Slice<Type> basic = new RawSlice<Type>(new Type[45]);
        public static StringTable stringtable = new StringTable();
        public static ByteSlice sizeTy = slice(new byte[]{(byte)68, (byte)72, (byte)88, (byte)68, (byte)68, (byte)109, (byte)100, (byte)76, (byte)76, (byte)68, (byte)68, (byte)72, (byte)72, (byte)72, (byte)72, (byte)72, (byte)72, (byte)72, (byte)72, (byte)72, (byte)72, (byte)72, (byte)72, (byte)72, (byte)72, (byte)72, (byte)72, (byte)72, (byte)72, (byte)72, (byte)72, (byte)72, (byte)72, (byte)72, (byte)64, (byte)96, (byte)100, (byte)68, (byte)76, (byte)92, (byte)64, (byte)68, (byte)72, (byte)72, (byte)85});
        public  Type(byte ty) {
            super();
            this.ty = ty;
        }

        public  BytePtr kind() {
            throw new AssertionError("Unreachable code!");
        }

        public  Type syntaxCopy() {
            fprintf(stderr, new BytePtr("this = %s, ty = %d\n"), this.toChars(), (this.ty & 0xFF));
            throw new AssertionError("Unreachable code!");
        }

        public  boolean equals(RootObject o) {
            Type t = (Type)o;
            if ((pequals(this, o)) || (t != null) && (this.deco == t.deco) && (this.deco != null))
            {
                return true;
            }
            return false;
        }

        public  boolean equivalent(Type t) {
            return this.immutableOf().equals(t.immutableOf());
        }

        public  int dyncast() {
            return DYNCAST.type;
        }

        public  int covariant(Type t, Ptr<Long> pstc, boolean fix17349) {
            if (pstc != null)
            {
                pstc.set(0, 0L);
            }
            long stc = 0L;
            boolean notcovariant = false;
            if (this.equals(t))
            {
                return 1;
            }
            TypeFunction t1 = this.isTypeFunction();
            TypeFunction t2 = t.isTypeFunction();
            try {
                try {
                    try {
                        if ((t1 == null) || (t2 == null))
                        {
                            /*goto Ldistinct*/throw Dispatch1.INSTANCE;
                        }
                        if ((t1.parameterList.varargs != t2.parameterList.varargs))
                        {
                            /*goto Ldistinct*/throw Dispatch1.INSTANCE;
                        }
                        if ((t1.parameterList.parameters != null) && (t2.parameterList.parameters != null))
                        {
                            int dim = t1.parameterList.length();
                            if ((dim != t2.parameterList.length()))
                            {
                                /*goto Ldistinct*/throw Dispatch1.INSTANCE;
                            }
                            {
                                int i = 0;
                            L_outer1:
                                for (; (i < dim);i++){
                                    Parameter fparam1 = t1.parameterList.get(i);
                                    Parameter fparam2 = t2.parameterList.get(i);
                                    try {
                                        if (!fparam1.type.equals(fparam2.type))
                                        {
                                            if (!fix17349)
                                            {
                                                /*goto Ldistinct*/throw Dispatch1.INSTANCE;
                                            }
                                            Type tp1 = fparam1.type;
                                            Type tp2 = fparam2.type;
                                            if (((tp1.ty & 0xFF) == (tp2.ty & 0xFF)))
                                            {
                                                {
                                                    TypeClass tc1 = tp1.isTypeClass();
                                                    if ((tc1) != null)
                                                    {
                                                        if ((pequals(tc1.sym, ((TypeClass)tp2).sym)) && MODimplicitConv(tp2.mod, tp1.mod))
                                                        {
                                                            /*goto Lcov*/throw Dispatch0.INSTANCE;
                                                        }
                                                    }
                                                    else {
                                                        TypeStruct ts1 = tp1.isTypeStruct();
                                                        if ((ts1) != null)
                                                        {
                                                            if ((pequals(ts1.sym, ((TypeStruct)tp2).sym)) && MODimplicitConv(tp2.mod, tp1.mod))
                                                            {
                                                                /*goto Lcov*/throw Dispatch0.INSTANCE;
                                                            }
                                                        }
                                                        else if (((tp1.ty & 0xFF) == ENUMTY.Tpointer))
                                                        {
                                                            if (tp2.implicitConvTo(tp1) != 0)
                                                            {
                                                                /*goto Lcov*/throw Dispatch0.INSTANCE;
                                                            }
                                                        }
                                                        else if (((tp1.ty & 0xFF) == ENUMTY.Tarray))
                                                        {
                                                            if (tp2.implicitConvTo(tp1) != 0)
                                                            {
                                                                /*goto Lcov*/throw Dispatch0.INSTANCE;
                                                            }
                                                        }
                                                        else if (((tp1.ty & 0xFF) == ENUMTY.Tdelegate))
                                                        {
                                                            if (tp1.implicitConvTo(tp2) != 0)
                                                            {
                                                                /*goto Lcov*/throw Dispatch0.INSTANCE;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            /*goto Ldistinct*/throw Dispatch1.INSTANCE;
                                        }
                                    }
                                    catch(Dispatch0 __d){}
                                /*Lcov:*/
                                    (notcovariant ? 1 : 0) |= (!fparam1.isCovariant(t1.isref, fparam2) ? 1 : 0);
                                }
                            }
                        }
                        else if ((t1.parameterList.parameters != t2.parameterList.parameters))
                        {
                            if ((t1.parameterList.length() != 0) || (t2.parameterList.length() != 0))
                            {
                                /*goto Ldistinct*/throw Dispatch1.INSTANCE;
                            }
                        }
                        if (notcovariant)
                        {
                            /*goto Lnotcovariant*/throw Dispatch2.INSTANCE;
                        }
                        if ((t1.linkage != t2.linkage))
                        {
                            /*goto Lnotcovariant*/throw Dispatch2.INSTANCE;
                        }
                        {
                            Type t1n = t1.next.value;
                            Type t2n = t2.next.value;
                            if ((t1n == null) || (t2n == null))
                            {
                                /*goto Lnotcovariant*/throw Dispatch2.INSTANCE;
                            }
                            if (t1n.equals(t2n))
                            {
                                /*goto Lcovariant*/throw Dispatch0.INSTANCE;
                            }
                            if (((t1n.ty & 0xFF) == ENUMTY.Tclass) && ((t2n.ty & 0xFF) == ENUMTY.Tclass))
                            {
                                if ((pequals(((TypeClass)t1n).sym, ((TypeClass)t2n).sym)) && MODimplicitConv(t1n.mod, t2n.mod))
                                {
                                    /*goto Lcovariant*/throw Dispatch0.INSTANCE;
                                }
                                ClassDeclaration cd = ((TypeClass)t1n).sym;
                                if ((cd.semanticRun < PASS.semanticdone) && !cd.isBaseInfoComplete())
                                {
                                    dsymbolSemantic(cd, null);
                                }
                                if (!cd.isBaseInfoComplete())
                                {
                                    return 3;
                                }
                            }
                            if (((t1n.ty & 0xFF) == ENUMTY.Tstruct) && ((t2n.ty & 0xFF) == ENUMTY.Tstruct))
                            {
                                if ((pequals(((TypeStruct)t1n).sym, ((TypeStruct)t2n).sym)) && MODimplicitConv(t1n.mod, t2n.mod))
                                {
                                    /*goto Lcovariant*/throw Dispatch0.INSTANCE;
                                }
                            }
                            else if (((t1n.ty & 0xFF) == (t2n.ty & 0xFF)) && (t1n.implicitConvTo(t2n) != 0))
                            {
                                /*goto Lcovariant*/throw Dispatch0.INSTANCE;
                            }
                            else if (((t1n.ty & 0xFF) == ENUMTY.Tnull))
                            {
                                Type t2bn = t2n.toBasetype();
                                if (((t2bn.ty & 0xFF) == ENUMTY.Tnull) || ((t2bn.ty & 0xFF) == ENUMTY.Tpointer) || ((t2bn.ty & 0xFF) == ENUMTY.Tclass))
                                {
                                    /*goto Lcovariant*/throw Dispatch0.INSTANCE;
                                }
                            }
                        }
                        /*goto Lnotcovariant*/throw Dispatch2.INSTANCE;
                    }
                    catch(Dispatch0 __d){}
                /*Lcovariant:*/
                    if (((t1.isref ? 1 : 0) != (t2.isref ? 1 : 0)))
                    {
                        /*goto Lnotcovariant*/throw Dispatch2.INSTANCE;
                    }
                    if (!t1.isref && t1.isscope || t2.isscope)
                    {
                        long stc1 = t1.isscope ? 524288L : 0L;
                        long stc2 = t2.isscope ? 524288L : 0L;
                        if (t1.isreturn)
                        {
                            stc1 |= 17592186044416L;
                            if (!t1.isscope)
                            {
                                stc1 |= 2097152L;
                            }
                        }
                        if (t2.isreturn)
                        {
                            stc2 |= 17592186044416L;
                            if (!t2.isscope)
                            {
                                stc2 |= 2097152L;
                            }
                        }
                        if (!Parameter.isCovariantScope(t1.isref, stc1, stc2))
                        {
                            /*goto Lnotcovariant*/throw Dispatch2.INSTANCE;
                        }
                    }
                    else if (t1.isreturn && !t2.isreturn)
                    {
                        /*goto Lnotcovariant*/throw Dispatch2.INSTANCE;
                    }
                    if (!MODimplicitConv(t2.mod, t1.mod))
                    {
                        /*goto Ldistinct*/throw Dispatch1.INSTANCE;
                    }
                    if ((t1.purity == 0) && (t2.purity != 0))
                    {
                        stc |= 67108864L;
                    }
                    if (!t1.isnothrow && t2.isnothrow)
                    {
                        stc |= 33554432L;
                    }
                    if (!t1.isnogc && t2.isnogc)
                    {
                        stc |= 4398046511104L;
                    }
                    if ((t1.trust <= TRUST.system) && (t2.trust >= TRUST.trusted))
                    {
                        stc |= 8589934592L;
                    }
                    if (stc != 0)
                    {
                        if (pstc != null)
                        {
                            pstc.set(0, stc);
                        }
                        /*goto Lnotcovariant*/throw Dispatch2.INSTANCE;
                    }
                    return 1;
                }
                catch(Dispatch1 __d){}
            /*Ldistinct:*/
                return 0;
            }
            catch(Dispatch2 __d){}
        /*Lnotcovariant:*/
            return 2;
        }

        // defaulted all parameters starting with #3
        public  int covariant(Type t, Ptr<Long> pstc) {
            return covariant(t, pstc, true);
        }

        // defaulted all parameters starting with #2
        public  int covariant(Type t) {
            return covariant(t, null, true);
        }

        public  BytePtr toChars() {
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                buf.value.reserve(16);
                Ref<HdrGenState> hgs = ref(new HdrGenState());
                hgs.value.fullQual = ((this.ty & 0xFF) == ENUMTY.Tclass) && (this.mod == 0);
                toCBuffer(this, ptr(buf), null, ptr(hgs));
                return buf.value.extractChars();
            }
            finally {
            }
        }

        public  BytePtr toPrettyChars(boolean QualifyTypes) {
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                buf.value.reserve(16);
                Ref<HdrGenState> hgs = ref(new HdrGenState());
                hgs.value.fullQual = QualifyTypes;
                toCBuffer(this, ptr(buf), null, ptr(hgs));
                return buf.value.extractChars();
            }
            finally {
            }
        }

        // defaulted all parameters starting with #1
        public  BytePtr toPrettyChars() {
            return toPrettyChars(false);
        }

        public static void _init() {
            stringtable._init(14000);
            {
                int i = 0;
                for (; ((mtype._initbasetab.get(i) & 0xFF) != ENUMTY.Terror);i++){
                    Type t = new TypeBasic(mtype._initbasetab.get(i));
                    t = merge(t);
                    basic.set(((mtype._initbasetab.get(i) & 0xFF)), t);
                }
            }
            basic.set(34, new TypeError());
            tvoid = basic.get(12);
            tint8 = basic.get(13);
            tuns8 = basic.get(14);
            tint16 = basic.get(15);
            tuns16 = basic.get(16);
            tint32 = basic.get(17);
            tuns32 = basic.get(18);
            tint64 = basic.get(19);
            tuns64 = basic.get(20);
            tint128 = basic.get(42);
            tuns128 = basic.get(43);
            tfloat32 = basic.get(21);
            tfloat64 = basic.get(22);
            tfloat80 = basic.get(23);
            timaginary32 = basic.get(24);
            timaginary64 = basic.get(25);
            timaginary80 = basic.get(26);
            tcomplex32 = basic.get(27);
            tcomplex64 = basic.get(28);
            tcomplex80 = basic.get(29);
            tbool = basic.get(30);
            tchar = basic.get(31);
            twchar = basic.get(32);
            tdchar = basic.get(33);
            tshiftcnt = tint32;
            terror = basic.get(34);
            tnull = basic.get(40);
            tnull = new TypeNull();
            tnull.deco = pcopy(merge(tnull).deco);
            tvoidptr = tvoid.pointerTo();
            tstring = tchar.immutableOf().arrayOf();
            twstring = twchar.immutableOf().arrayOf();
            tdstring = tdchar.immutableOf().arrayOf();
            tvalist = target.va_listType();
            boolean isLP64 = global.params.isLP64;
            tsize_t = basic.get(isLP64 ? 20 : 18);
            tptrdiff_t = basic.get(isLP64 ? 19 : 17);
            thash_t = tsize_t;
        }

        public static void deinitialize() {
            stringtable.opAssign(new StringTable(null, 0, null, 0, 0, 0, 0));
        }

        public  long size() {
            return this.size(Loc.initial);
        }

        public  long size(Loc loc) {
            error(loc, new BytePtr("no size for type `%s`"), this.toChars());
            return -1L;
        }

        public  int alignsize() {
            return (int)this.size(Loc.initial);
        }

        public  Type trySemantic(Loc loc, Ptr<Scope> sc) {
            Type tcopy = this.syntaxCopy();
            int errors = global.startGagging();
            Type t = typeSemantic(this, loc, sc);
            if (global.endGagging(errors) || ((t.ty & 0xFF) == ENUMTY.Terror))
            {
                t = null;
            }
            else
            {
                if ((global.gaggedWarnings > 0))
                {
                    typeSemantic(tcopy, loc, sc);
                }
            }
            return t;
        }

        public  Type merge2() {
            Type t = this;
            assert(t != null);
            if (t.deco == null)
            {
                return merge(t);
            }
            Ptr<StringValue> sv = stringtable.lookup(t.deco, strlen(t.deco));
            if ((sv != null) && ((sv.get()).ptrvalue != null))
            {
                t = ((Type)(sv.get()).ptrvalue);
                assert(t.deco != null);
            }
            else
            {
                throw new AssertionError("Unreachable code!");
            }
            return t;
        }

        public  void modToBuffer(Ptr<OutBuffer> buf) {
            if (this.mod != 0)
            {
                (buf.get()).writeByte(32);
                MODtoBuffer(buf, this.mod);
            }
        }

        public  BytePtr modToChars() {
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                buf.value.reserve(16);
                this.modToBuffer(ptr(buf));
                return buf.value.extractChars();
            }
            finally {
            }
        }

        public  boolean isintegral() {
            return false;
        }

        public  boolean isfloating() {
            return false;
        }

        public  boolean isreal() {
            return false;
        }

        public  boolean isimaginary() {
            return false;
        }

        public  boolean iscomplex() {
            return false;
        }

        public  boolean isscalar() {
            return false;
        }

        public  boolean isunsigned() {
            return false;
        }

        public  boolean ischar() {
            return false;
        }

        public  boolean isscope() {
            return false;
        }

        public  boolean isString() {
            return false;
        }

        public  boolean isAssignable() {
            return true;
        }

        public  boolean isBoolean() {
            return this.isscalar();
        }

        public  void checkDeprecated(Loc loc, Ptr<Scope> sc) {
            {
                Dsymbol s = this.toDsymbol(sc);
                if ((s) != null)
                {
                    s.checkDeprecated(loc, sc);
                }
            }
        }

        public  boolean isConst() {
            return ((this.mod & 0xFF) & MODFlags.const_) != 0;
        }

        public  boolean isImmutable() {
            return ((this.mod & 0xFF) & MODFlags.immutable_) != 0;
        }

        public  boolean isMutable() {
            return ((this.mod & 0xFF) & 13) == 0;
        }

        public  boolean isShared() {
            return ((this.mod & 0xFF) & MODFlags.shared_) != 0;
        }

        public  boolean isSharedConst() {
            return ((this.mod & 0xFF) & 3) == 3;
        }

        public  boolean isWild() {
            return ((this.mod & 0xFF) & MODFlags.wild) != 0;
        }

        public  boolean isWildConst() {
            return ((this.mod & 0xFF) & MODFlags.wildconst) == MODFlags.wildconst;
        }

        public  boolean isSharedWild() {
            return ((this.mod & 0xFF) & 10) == 10;
        }

        public  boolean isNaked() {
            return (this.mod & 0xFF) == 0;
        }

        public  Type nullAttributes() {
            int sz = (sizeTy.get((this.ty & 0xFF)) & 0xFF);
            Type t = null;
            (t) = (this).copy();
            t.deco = null;
            t.arrayof = null;
            t.pto = null;
            t.rto = null;
            t.cto = null;
            t.ito = null;
            t.sto = null;
            t.scto = null;
            t.wto = null;
            t.wcto = null;
            t.swto = null;
            t.swcto = null;
            t.vtinfo = null;
            t.ctype = null;
            if (((t.ty & 0xFF) == ENUMTY.Tstruct))
            {
                ((TypeStruct)t).att.value = AliasThisRec.fwdref;
            }
            if (((t.ty & 0xFF) == ENUMTY.Tclass))
            {
                ((TypeClass)t).att.value = AliasThisRec.fwdref;
            }
            return t;
        }

        public  Type constOf() {
            if (((this.mod & 0xFF) == MODFlags.const_))
            {
                return this;
            }
            if (this.cto != null)
            {
                assert(((this.cto.mod & 0xFF) == MODFlags.const_));
                return this.cto;
            }
            Type t = this.makeConst();
            t = merge(t);
            t.fixTo(this);
            return t;
        }

        public  Type immutableOf() {
            if (this.isImmutable())
            {
                return this;
            }
            if (this.ito != null)
            {
                assert(this.ito.isImmutable());
                return this.ito;
            }
            Type t = this.makeImmutable();
            t = merge(t);
            t.fixTo(this);
            return t;
        }

        public  Type mutableOf() {
            Type t = this;
            if (this.isImmutable())
            {
                t = this.ito;
                assert((t == null) || t.isMutable() && !t.isShared());
            }
            else if (this.isConst())
            {
                if (this.isShared())
                {
                    if (this.isWild())
                    {
                        t = this.swcto;
                    }
                    else
                    {
                        t = this.sto;
                    }
                }
                else
                {
                    if (this.isWild())
                    {
                        t = this.wcto;
                    }
                    else
                    {
                        t = this.cto;
                    }
                }
                assert((t == null) || t.isMutable());
            }
            else if (this.isWild())
            {
                if (this.isShared())
                {
                    t = this.sto;
                }
                else
                {
                    t = this.wto;
                }
                assert((t == null) || t.isMutable());
            }
            if (t == null)
            {
                t = this.makeMutable();
                t = merge(t);
                t.fixTo(this);
            }
            else
            {
                t = merge(t);
            }
            assert(t.isMutable());
            return t;
        }

        public  Type sharedOf() {
            if (((this.mod & 0xFF) == MODFlags.shared_))
            {
                return this;
            }
            if (this.sto != null)
            {
                assert(((this.sto.mod & 0xFF) == MODFlags.shared_));
                return this.sto;
            }
            Type t = this.makeShared();
            t = merge(t);
            t.fixTo(this);
            return t;
        }

        public  Type sharedConstOf() {
            if (((this.mod & 0xFF) == 3))
            {
                return this;
            }
            if (this.scto != null)
            {
                assert(((this.scto.mod & 0xFF) == 3));
                return this.scto;
            }
            Type t = this.makeSharedConst();
            t = merge(t);
            t.fixTo(this);
            return t;
        }

        public  Type unSharedOf() {
            Type t = this;
            if (this.isShared())
            {
                if (this.isWild())
                {
                    if (this.isConst())
                    {
                        t = this.wcto;
                    }
                    else
                    {
                        t = this.wto;
                    }
                }
                else
                {
                    if (this.isConst())
                    {
                        t = this.cto;
                    }
                    else
                    {
                        t = this.sto;
                    }
                }
                assert((t == null) || !t.isShared());
            }
            if (t == null)
            {
                t = this.nullAttributes();
                t.mod = (byte)((this.mod & 0xFF) & -3);
                t.ctype = pcopy(this.ctype);
                t = merge(t);
                t.fixTo(this);
            }
            else
            {
                t = merge(t);
            }
            assert(!t.isShared());
            return t;
        }

        public  Type wildOf() {
            if (((this.mod & 0xFF) == MODFlags.wild))
            {
                return this;
            }
            if (this.wto != null)
            {
                assert(((this.wto.mod & 0xFF) == MODFlags.wild));
                return this.wto;
            }
            Type t = this.makeWild();
            t = merge(t);
            t.fixTo(this);
            return t;
        }

        public  Type wildConstOf() {
            if (((this.mod & 0xFF) == MODFlags.wildconst))
            {
                return this;
            }
            if (this.wcto != null)
            {
                assert(((this.wcto.mod & 0xFF) == MODFlags.wildconst));
                return this.wcto;
            }
            Type t = this.makeWildConst();
            t = merge(t);
            t.fixTo(this);
            return t;
        }

        public  Type sharedWildOf() {
            if (((this.mod & 0xFF) == 10))
            {
                return this;
            }
            if (this.swto != null)
            {
                assert(((this.swto.mod & 0xFF) == 10));
                return this.swto;
            }
            Type t = this.makeSharedWild();
            t = merge(t);
            t.fixTo(this);
            return t;
        }

        public  Type sharedWildConstOf() {
            if (((this.mod & 0xFF) == 11))
            {
                return this;
            }
            if (this.swcto != null)
            {
                assert(((this.swcto.mod & 0xFF) == 11));
                return this.swcto;
            }
            Type t = this.makeSharedWildConst();
            t = merge(t);
            t.fixTo(this);
            return t;
        }

        public  void fixTo(Type t) {
            Type mto = null;
            Type tn = this.nextOf();
            if ((tn == null) || ((this.ty & 0xFF) != ENUMTY.Tsarray) && ((tn.mod & 0xFF) == (t.nextOf().mod & 0xFF)))
            {
                switch ((t.mod & 0xFF))
                {
                    case 0:
                        mto = t;
                        break;
                    case 1:
                        this.cto = t;
                        break;
                    case 8:
                        this.wto = t;
                        break;
                    case 9:
                        this.wcto = t;
                        break;
                    case 2:
                        this.sto = t;
                        break;
                    case 3:
                        this.scto = t;
                        break;
                    case 10:
                        this.swto = t;
                        break;
                    case 11:
                        this.swcto = t;
                        break;
                    case 4:
                        this.ito = t;
                        break;
                    default:
                    break;
                }
            }
            assert(((this.mod & 0xFF) != (t.mod & 0xFF)));
            switch ((this.mod & 0xFF))
            {
                case 0:
                    break;
                case 1:
                    this.cto = mto;
                    t.cto = this;
                    break;
                case 8:
                    this.wto = mto;
                    t.wto = this;
                    break;
                case 9:
                    this.wcto = mto;
                    t.wcto = this;
                    break;
                case 2:
                    this.sto = mto;
                    t.sto = this;
                    break;
                case 3:
                    this.scto = mto;
                    t.scto = this;
                    break;
                case 10:
                    this.swto = mto;
                    t.swto = this;
                    break;
                case 11:
                    this.swcto = mto;
                    t.swcto = this;
                    break;
                case 4:
                    t.ito = this;
                    if (t.cto != null)
                    {
                        t.cto.ito = this;
                    }
                    if (t.sto != null)
                    {
                        t.sto.ito = this;
                    }
                    if (t.scto != null)
                    {
                        t.scto.ito = this;
                    }
                    if (t.wto != null)
                    {
                        t.wto.ito = this;
                    }
                    if (t.wcto != null)
                    {
                        t.wcto.ito = this;
                    }
                    if (t.swto != null)
                    {
                        t.swto.ito = this;
                    }
                    if (t.swcto != null)
                    {
                        t.swcto.ito = this;
                    }
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            this.check();
            t.check();
        }

        public  void check() {
            switch ((this.mod & 0xFF))
            {
                case 0:
                    if (this.cto != null)
                    {
                        assert(((this.cto.mod & 0xFF) == MODFlags.const_));
                    }
                    if (this.ito != null)
                    {
                        assert(((this.ito.mod & 0xFF) == MODFlags.immutable_));
                    }
                    if (this.sto != null)
                    {
                        assert(((this.sto.mod & 0xFF) == MODFlags.shared_));
                    }
                    if (this.scto != null)
                    {
                        assert(((this.scto.mod & 0xFF) == 3));
                    }
                    if (this.wto != null)
                    {
                        assert(((this.wto.mod & 0xFF) == MODFlags.wild));
                    }
                    if (this.wcto != null)
                    {
                        assert(((this.wcto.mod & 0xFF) == MODFlags.wildconst));
                    }
                    if (this.swto != null)
                    {
                        assert(((this.swto.mod & 0xFF) == 10));
                    }
                    if (this.swcto != null)
                    {
                        assert(((this.swcto.mod & 0xFF) == 11));
                    }
                    break;
                case 1:
                    if (this.cto != null)
                    {
                        assert(((this.cto.mod & 0xFF) == 0));
                    }
                    if (this.ito != null)
                    {
                        assert(((this.ito.mod & 0xFF) == MODFlags.immutable_));
                    }
                    if (this.sto != null)
                    {
                        assert(((this.sto.mod & 0xFF) == MODFlags.shared_));
                    }
                    if (this.scto != null)
                    {
                        assert(((this.scto.mod & 0xFF) == 3));
                    }
                    if (this.wto != null)
                    {
                        assert(((this.wto.mod & 0xFF) == MODFlags.wild));
                    }
                    if (this.wcto != null)
                    {
                        assert(((this.wcto.mod & 0xFF) == MODFlags.wildconst));
                    }
                    if (this.swto != null)
                    {
                        assert(((this.swto.mod & 0xFF) == 10));
                    }
                    if (this.swcto != null)
                    {
                        assert(((this.swcto.mod & 0xFF) == 11));
                    }
                    break;
                case 8:
                    if (this.cto != null)
                    {
                        assert(((this.cto.mod & 0xFF) == MODFlags.const_));
                    }
                    if (this.ito != null)
                    {
                        assert(((this.ito.mod & 0xFF) == MODFlags.immutable_));
                    }
                    if (this.sto != null)
                    {
                        assert(((this.sto.mod & 0xFF) == MODFlags.shared_));
                    }
                    if (this.scto != null)
                    {
                        assert(((this.scto.mod & 0xFF) == 3));
                    }
                    if (this.wto != null)
                    {
                        assert(((this.wto.mod & 0xFF) == 0));
                    }
                    if (this.wcto != null)
                    {
                        assert(((this.wcto.mod & 0xFF) == MODFlags.wildconst));
                    }
                    if (this.swto != null)
                    {
                        assert(((this.swto.mod & 0xFF) == 10));
                    }
                    if (this.swcto != null)
                    {
                        assert(((this.swcto.mod & 0xFF) == 11));
                    }
                    break;
                case 9:
                    assert((this.cto == null) || ((this.cto.mod & 0xFF) == MODFlags.const_));
                    assert((this.ito == null) || ((this.ito.mod & 0xFF) == MODFlags.immutable_));
                    assert((this.sto == null) || ((this.sto.mod & 0xFF) == MODFlags.shared_));
                    assert((this.scto == null) || ((this.scto.mod & 0xFF) == 3));
                    assert((this.wto == null) || ((this.wto.mod & 0xFF) == MODFlags.wild));
                    assert((this.wcto == null) || ((this.wcto.mod & 0xFF) == 0));
                    assert((this.swto == null) || ((this.swto.mod & 0xFF) == 10));
                    assert((this.swcto == null) || ((this.swcto.mod & 0xFF) == 11));
                    break;
                case 2:
                    if (this.cto != null)
                    {
                        assert(((this.cto.mod & 0xFF) == MODFlags.const_));
                    }
                    if (this.ito != null)
                    {
                        assert(((this.ito.mod & 0xFF) == MODFlags.immutable_));
                    }
                    if (this.sto != null)
                    {
                        assert(((this.sto.mod & 0xFF) == 0));
                    }
                    if (this.scto != null)
                    {
                        assert(((this.scto.mod & 0xFF) == 3));
                    }
                    if (this.wto != null)
                    {
                        assert(((this.wto.mod & 0xFF) == MODFlags.wild));
                    }
                    if (this.wcto != null)
                    {
                        assert(((this.wcto.mod & 0xFF) == MODFlags.wildconst));
                    }
                    if (this.swto != null)
                    {
                        assert(((this.swto.mod & 0xFF) == 10));
                    }
                    if (this.swcto != null)
                    {
                        assert(((this.swcto.mod & 0xFF) == 11));
                    }
                    break;
                case 3:
                    if (this.cto != null)
                    {
                        assert(((this.cto.mod & 0xFF) == MODFlags.const_));
                    }
                    if (this.ito != null)
                    {
                        assert(((this.ito.mod & 0xFF) == MODFlags.immutable_));
                    }
                    if (this.sto != null)
                    {
                        assert(((this.sto.mod & 0xFF) == MODFlags.shared_));
                    }
                    if (this.scto != null)
                    {
                        assert(((this.scto.mod & 0xFF) == 0));
                    }
                    if (this.wto != null)
                    {
                        assert(((this.wto.mod & 0xFF) == MODFlags.wild));
                    }
                    if (this.wcto != null)
                    {
                        assert(((this.wcto.mod & 0xFF) == MODFlags.wildconst));
                    }
                    if (this.swto != null)
                    {
                        assert(((this.swto.mod & 0xFF) == 10));
                    }
                    if (this.swcto != null)
                    {
                        assert(((this.swcto.mod & 0xFF) == 11));
                    }
                    break;
                case 10:
                    if (this.cto != null)
                    {
                        assert(((this.cto.mod & 0xFF) == MODFlags.const_));
                    }
                    if (this.ito != null)
                    {
                        assert(((this.ito.mod & 0xFF) == MODFlags.immutable_));
                    }
                    if (this.sto != null)
                    {
                        assert(((this.sto.mod & 0xFF) == MODFlags.shared_));
                    }
                    if (this.scto != null)
                    {
                        assert(((this.scto.mod & 0xFF) == 3));
                    }
                    if (this.wto != null)
                    {
                        assert(((this.wto.mod & 0xFF) == MODFlags.wild));
                    }
                    if (this.wcto != null)
                    {
                        assert(((this.wcto.mod & 0xFF) == MODFlags.wildconst));
                    }
                    if (this.swto != null)
                    {
                        assert(((this.swto.mod & 0xFF) == 0));
                    }
                    if (this.swcto != null)
                    {
                        assert(((this.swcto.mod & 0xFF) == 11));
                    }
                    break;
                case 11:
                    assert((this.cto == null) || ((this.cto.mod & 0xFF) == MODFlags.const_));
                    assert((this.ito == null) || ((this.ito.mod & 0xFF) == MODFlags.immutable_));
                    assert((this.sto == null) || ((this.sto.mod & 0xFF) == MODFlags.shared_));
                    assert((this.scto == null) || ((this.scto.mod & 0xFF) == 3));
                    assert((this.wto == null) || ((this.wto.mod & 0xFF) == MODFlags.wild));
                    assert((this.wcto == null) || ((this.wcto.mod & 0xFF) == MODFlags.wildconst));
                    assert((this.swto == null) || ((this.swto.mod & 0xFF) == 10));
                    assert((this.swcto == null) || ((this.swcto.mod & 0xFF) == 0));
                    break;
                case 4:
                    if (this.cto != null)
                    {
                        assert(((this.cto.mod & 0xFF) == MODFlags.const_));
                    }
                    if (this.ito != null)
                    {
                        assert(((this.ito.mod & 0xFF) == 0));
                    }
                    if (this.sto != null)
                    {
                        assert(((this.sto.mod & 0xFF) == MODFlags.shared_));
                    }
                    if (this.scto != null)
                    {
                        assert(((this.scto.mod & 0xFF) == 3));
                    }
                    if (this.wto != null)
                    {
                        assert(((this.wto.mod & 0xFF) == MODFlags.wild));
                    }
                    if (this.wcto != null)
                    {
                        assert(((this.wcto.mod & 0xFF) == MODFlags.wildconst));
                    }
                    if (this.swto != null)
                    {
                        assert(((this.swto.mod & 0xFF) == 10));
                    }
                    if (this.swcto != null)
                    {
                        assert(((this.swcto.mod & 0xFF) == 11));
                    }
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            Type tn = this.nextOf();
            if ((tn != null) && ((this.ty & 0xFF) != ENUMTY.Tfunction) && ((tn.ty & 0xFF) != ENUMTY.Tfunction) && ((this.ty & 0xFF) != ENUMTY.Tenum))
            {
                switch ((this.mod & 0xFF))
                {
                    case 0:
                    case 1:
                    case 8:
                    case 9:
                    case 2:
                    case 3:
                    case 10:
                    case 11:
                    case 4:
                        assert(((tn.mod & 0xFF) == MODFlags.immutable_) || (((tn.mod & 0xFF) & (this.mod & 0xFF)) == (this.mod & 0xFF)));
                        break;
                    default:
                    throw new AssertionError("Unreachable code!");
                }
                tn.check();
            }
        }

        public  Type addSTC(long stc) {
            Type t = this;
            if (t.isImmutable())
            {
            }
            else if ((stc & 1048576L) != 0)
            {
                t = t.makeImmutable();
            }
            else
            {
                if (((stc & 536870912L) != 0) && !t.isShared())
                {
                    if (t.isWild())
                    {
                        if (t.isConst())
                        {
                            t = t.makeSharedWildConst();
                        }
                        else
                        {
                            t = t.makeSharedWild();
                        }
                    }
                    else
                    {
                        if (t.isConst())
                        {
                            t = t.makeSharedConst();
                        }
                        else
                        {
                            t = t.makeShared();
                        }
                    }
                }
                if (((stc & 4L) != 0) && !t.isConst())
                {
                    if (t.isShared())
                    {
                        if (t.isWild())
                        {
                            t = t.makeSharedWildConst();
                        }
                        else
                        {
                            t = t.makeSharedConst();
                        }
                    }
                    else
                    {
                        if (t.isWild())
                        {
                            t = t.makeWildConst();
                        }
                        else
                        {
                            t = t.makeConst();
                        }
                    }
                }
                if (((stc & 2147483648L) != 0) && !t.isWild())
                {
                    if (t.isShared())
                    {
                        if (t.isConst())
                        {
                            t = t.makeSharedWildConst();
                        }
                        else
                        {
                            t = t.makeSharedWild();
                        }
                    }
                    else
                    {
                        if (t.isConst())
                        {
                            t = t.makeWildConst();
                        }
                        else
                        {
                            t = t.makeWild();
                        }
                    }
                }
            }
            return t;
        }

        public  Type castMod(byte mod) {
            Type t = null;
            switch ((mod & 0xFF))
            {
                case 0:
                    t = this.unSharedOf().mutableOf();
                    break;
                case 1:
                    t = this.unSharedOf().constOf();
                    break;
                case 8:
                    t = this.unSharedOf().wildOf();
                    break;
                case 9:
                    t = this.unSharedOf().wildConstOf();
                    break;
                case 2:
                    t = this.mutableOf().sharedOf();
                    break;
                case 3:
                    t = this.sharedConstOf();
                    break;
                case 10:
                    t = this.sharedWildOf();
                    break;
                case 11:
                    t = this.sharedWildConstOf();
                    break;
                case 4:
                    t = this.immutableOf();
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            return t;
        }

        public  Type addMod(byte mod) {
            Type t = this;
            if (!t.isImmutable())
            {
                switch ((mod & 0xFF))
                {
                    case 0:
                        break;
                    case 1:
                        if (this.isShared())
                        {
                            if (this.isWild())
                            {
                                t = this.sharedWildConstOf();
                            }
                            else
                            {
                                t = this.sharedConstOf();
                            }
                        }
                        else
                        {
                            if (this.isWild())
                            {
                                t = this.wildConstOf();
                            }
                            else
                            {
                                t = this.constOf();
                            }
                        }
                        break;
                    case 8:
                        if (this.isShared())
                        {
                            if (this.isConst())
                            {
                                t = this.sharedWildConstOf();
                            }
                            else
                            {
                                t = this.sharedWildOf();
                            }
                        }
                        else
                        {
                            if (this.isConst())
                            {
                                t = this.wildConstOf();
                            }
                            else
                            {
                                t = this.wildOf();
                            }
                        }
                        break;
                    case 9:
                        if (this.isShared())
                        {
                            t = this.sharedWildConstOf();
                        }
                        else
                        {
                            t = this.wildConstOf();
                        }
                        break;
                    case 2:
                        if (this.isWild())
                        {
                            if (this.isConst())
                            {
                                t = this.sharedWildConstOf();
                            }
                            else
                            {
                                t = this.sharedWildOf();
                            }
                        }
                        else
                        {
                            if (this.isConst())
                            {
                                t = this.sharedConstOf();
                            }
                            else
                            {
                                t = this.sharedOf();
                            }
                        }
                        break;
                    case 3:
                        if (this.isWild())
                        {
                            t = this.sharedWildConstOf();
                        }
                        else
                        {
                            t = this.sharedConstOf();
                        }
                        break;
                    case 10:
                        if (this.isConst())
                        {
                            t = this.sharedWildConstOf();
                        }
                        else
                        {
                            t = this.sharedWildOf();
                        }
                        break;
                    case 11:
                        t = this.sharedWildConstOf();
                        break;
                    case 4:
                        t = this.immutableOf();
                        break;
                    default:
                    throw new AssertionError("Unreachable code!");
                }
            }
            return t;
        }

        public  Type addStorageClass(long stc) {
            byte mod = (byte)0;
            if ((stc & 1048576L) != 0)
            {
                mod = (byte)4;
            }
            else
            {
                if ((stc & 2052L) != 0)
                {
                    mod |= MODFlags.const_;
                }
                if ((stc & 2147483648L) != 0)
                {
                    mod |= MODFlags.wild;
                }
                if ((stc & 536870912L) != 0)
                {
                    mod |= MODFlags.shared_;
                }
            }
            return this.addMod(mod);
        }

        public  Type pointerTo() {
            if (((this.ty & 0xFF) == ENUMTY.Terror))
            {
                return this;
            }
            if (this.pto == null)
            {
                Type t = new TypePointer(this);
                if (((this.ty & 0xFF) == ENUMTY.Tfunction))
                {
                    t.deco = pcopy(merge(t).deco);
                    this.pto = t;
                }
                else
                {
                    this.pto = merge(t);
                }
            }
            return this.pto;
        }

        public  Type referenceTo() {
            if (((this.ty & 0xFF) == ENUMTY.Terror))
            {
                return this;
            }
            if (this.rto == null)
            {
                Type t = new TypeReference(this);
                this.rto = merge(t);
            }
            return this.rto;
        }

        public  Type arrayOf() {
            if (((this.ty & 0xFF) == ENUMTY.Terror))
            {
                return this;
            }
            if (this.arrayof == null)
            {
                Type t = new TypeDArray(this);
                this.arrayof = merge(t);
            }
            return this.arrayof;
        }

        public  Type sarrayOf(long dim) {
            assert(this.deco != null);
            Type t = new TypeSArray(this, new IntegerExp(Loc.initial, dim, tsize_t));
            t = t.addMod(this.mod);
            t = merge(t);
            return t;
        }

        public  Type aliasthisOf() {
            AggregateDeclaration ad = isAggregate(this);
            if ((ad == null) || (ad.aliasthis == null))
            {
                return null;
            }
            Dsymbol s = ad.aliasthis;
            if (s.isAliasDeclaration() != null)
            {
                s = s.toAlias();
            }
            if (s.isTupleDeclaration() != null)
            {
                return null;
            }
            {
                VarDeclaration vd = s.isVarDeclaration();
                if ((vd) != null)
                {
                    Type t = vd.type;
                    if (vd.needThis())
                    {
                        t = t.addMod(this.mod);
                    }
                    return t;
                }
            }
            {
                FuncDeclaration fd = s.isFuncDeclaration();
                if ((fd) != null)
                {
                    fd = resolveFuncCall(Loc.initial, null, fd, null, this, null, FuncResolveFlag.quiet);
                    if ((fd == null) || fd.errors || !fd.functionSemantic())
                    {
                        return terror;
                    }
                    Type t = fd.type.nextOf();
                    if (t == null)
                    {
                        return terror;
                    }
                    t = t.substWildTo(((this.mod & 0xFF) == 0) ? 16 : (this.mod & 0xFF));
                    return t;
                }
            }
            {
                Declaration d = s.isDeclaration();
                if ((d) != null)
                {
                    assert(d.type != null);
                    return d.type;
                }
            }
            {
                EnumDeclaration ed = s.isEnumDeclaration();
                if ((ed) != null)
                {
                    return ed.type;
                }
            }
            {
                TemplateDeclaration td = s.isTemplateDeclaration();
                if ((td) != null)
                {
                    assert(td._scope != null);
                    FuncDeclaration fd = resolveFuncCall(Loc.initial, null, td, null, this, null, FuncResolveFlag.quiet);
                    if ((fd == null) || fd.errors || !fd.functionSemantic())
                    {
                        return terror;
                    }
                    Type t = fd.type.nextOf();
                    if (t == null)
                    {
                        return terror;
                    }
                    t = t.substWildTo(((this.mod & 0xFF) == 0) ? 16 : (this.mod & 0xFF));
                    return t;
                }
            }
            return null;
        }

        public  boolean checkAliasThisRec() {
            Type tb = this.toBasetype();
            Ptr<Integer> pflag = null;
            if (((tb.ty & 0xFF) == ENUMTY.Tstruct))
            {
                pflag = pcopy((ptr((TypeStruct)tb.att)));
            }
            else if (((tb.ty & 0xFF) == ENUMTY.Tclass))
            {
                pflag = pcopy((ptr((TypeClass)tb.att)));
            }
            else
            {
                return false;
            }
            int flag = pflag.get() & AliasThisRec.typeMask;
            if ((flag == AliasThisRec.fwdref))
            {
                Type att = this.aliasthisOf();
                flag = (att != null) && (att.implicitConvTo(this) != 0) ? AliasThisRec.yes : AliasThisRec.no;
            }
            pflag.set(0, (flag | pflag.get() & -4));
            return flag == AliasThisRec.yes;
        }

        public  Type makeConst() {
            if (this.cto != null)
            {
                return this.cto;
            }
            Type t = this.nullAttributes();
            t.mod = (byte)1;
            return t;
        }

        public  Type makeImmutable() {
            if (this.ito != null)
            {
                return this.ito;
            }
            Type t = this.nullAttributes();
            t.mod = (byte)4;
            return t;
        }

        public  Type makeShared() {
            if (this.sto != null)
            {
                return this.sto;
            }
            Type t = this.nullAttributes();
            t.mod = (byte)2;
            return t;
        }

        public  Type makeSharedConst() {
            if (this.scto != null)
            {
                return this.scto;
            }
            Type t = this.nullAttributes();
            t.mod = (byte)3;
            return t;
        }

        public  Type makeWild() {
            if (this.wto != null)
            {
                return this.wto;
            }
            Type t = this.nullAttributes();
            t.mod = (byte)8;
            return t;
        }

        public  Type makeWildConst() {
            if (this.wcto != null)
            {
                return this.wcto;
            }
            Type t = this.nullAttributes();
            t.mod = (byte)9;
            return t;
        }

        public  Type makeSharedWild() {
            if (this.swto != null)
            {
                return this.swto;
            }
            Type t = this.nullAttributes();
            t.mod = (byte)10;
            return t;
        }

        public  Type makeSharedWildConst() {
            if (this.swcto != null)
            {
                return this.swcto;
            }
            Type t = this.nullAttributes();
            t.mod = (byte)11;
            return t;
        }

        public  Type makeMutable() {
            Type t = this.nullAttributes();
            t.mod = (byte)((this.mod & 0xFF) & MODFlags.shared_);
            return t;
        }

        public  Dsymbol toDsymbol(Ptr<Scope> sc) {
            return null;
        }

        public  Type toBasetype() {
            return this;
        }

        public  boolean isBaseOf(Type t, Ptr<Integer> poffset) {
            return false;
        }

        public  int implicitConvTo(Type to) {
            if (this.equals(to))
            {
                return MATCH.exact;
            }
            return MATCH.nomatch;
        }

        public  int constConv(Type to) {
            if (this.equals(to))
            {
                return MATCH.exact;
            }
            if (((this.ty & 0xFF) == (to.ty & 0xFF)) && MODimplicitConv(this.mod, to.mod))
            {
                return MATCH.constant;
            }
            return MATCH.nomatch;
        }

        public  byte deduceWild(Type t, boolean isRef) {
            if (t.isWild())
            {
                if (this.isImmutable())
                {
                    return (byte)4;
                }
                else if (this.isWildConst())
                {
                    if (t.isWildConst())
                    {
                        return (byte)8;
                    }
                    else
                    {
                        return (byte)9;
                    }
                }
                else if (this.isWild())
                {
                    return (byte)8;
                }
                else if (this.isConst())
                {
                    return (byte)1;
                }
                else if (this.isMutable())
                {
                    return (byte)16;
                }
                else
                {
                    throw new AssertionError("Unreachable code!");
                }
            }
            return (byte)0;
        }

        public  Type substWildTo(int mod) {
            Type t = null;
            try {
                {
                    Type tn = this.nextOf();
                    if ((tn) != null)
                    {
                        if (((this.ty & 0xFF) == ENUMTY.Tpointer) && ((tn.ty & 0xFF) == ENUMTY.Tfunction))
                        {
                            t = this;
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        }
                        t = tn.substWildTo(mod);
                        if ((pequals(t, tn)))
                        {
                            t = this;
                        }
                        else
                        {
                            if (((this.ty & 0xFF) == ENUMTY.Tpointer))
                            {
                                t = t.pointerTo();
                            }
                            else if (((this.ty & 0xFF) == ENUMTY.Tarray))
                            {
                                t = t.arrayOf();
                            }
                            else if (((this.ty & 0xFF) == ENUMTY.Tsarray))
                            {
                                t = new TypeSArray(t, ((TypeSArray)this).dim.syntaxCopy());
                            }
                            else if (((this.ty & 0xFF) == ENUMTY.Taarray))
                            {
                                t = new TypeAArray(t, ((TypeAArray)this).index.syntaxCopy());
                                ((TypeAArray)t).sc = pcopy(((TypeAArray)this).sc);
                            }
                            else if (((this.ty & 0xFF) == ENUMTY.Tdelegate))
                            {
                                t = new TypeDelegate(t);
                            }
                            else
                            {
                                throw new AssertionError("Unreachable code!");
                            }
                            t = merge(t);
                        }
                    }
                    else
                    {
                        t = this;
                    }
                }
            }
            catch(Dispatch0 __d){}
        /*L1:*/
            if (this.isWild())
            {
                if ((mod == 4))
                {
                    t = t.immutableOf();
                }
                else if ((mod == 9))
                {
                    t = t.wildConstOf();
                }
                else if ((mod == 8))
                {
                    if (this.isWildConst())
                    {
                        t = t.wildConstOf();
                    }
                    else
                    {
                        t = t.wildOf();
                    }
                }
                else if ((mod == 1))
                {
                    t = t.constOf();
                }
                else
                {
                    if (this.isWildConst())
                    {
                        t = t.constOf();
                    }
                    else
                    {
                        t = t.mutableOf();
                    }
                }
            }
            if (this.isConst())
            {
                t = t.addMod((byte)1);
            }
            if (this.isShared())
            {
                t = t.addMod((byte)2);
            }
            return t;
        }

        public  Type unqualify(int m) {
            Type t = this.mutableOf().unSharedOf();
            Type tn = ((this.ty & 0xFF) == ENUMTY.Tenum) ? null : this.nextOf();
            if ((tn != null) && ((tn.ty & 0xFF) != ENUMTY.Tfunction))
            {
                Type utn = tn.unqualify(m);
                if ((!pequals(utn, tn)))
                {
                    if (((this.ty & 0xFF) == ENUMTY.Tpointer))
                    {
                        t = utn.pointerTo();
                    }
                    else if (((this.ty & 0xFF) == ENUMTY.Tarray))
                    {
                        t = utn.arrayOf();
                    }
                    else if (((this.ty & 0xFF) == ENUMTY.Tsarray))
                    {
                        t = new TypeSArray(utn, ((TypeSArray)this).dim);
                    }
                    else if (((this.ty & 0xFF) == ENUMTY.Taarray))
                    {
                        t = new TypeAArray(utn, ((TypeAArray)this).index);
                        ((TypeAArray)t).sc = pcopy(((TypeAArray)this).sc);
                    }
                    else
                    {
                        throw new AssertionError("Unreachable code!");
                    }
                    t = merge(t);
                }
            }
            t = t.addMod((byte)((this.mod & 0xFF) & ~m));
            return t;
        }

        public  Type toHeadMutable() {
            if (this.mod == 0)
            {
                return this;
            }
            return this.mutableOf();
        }

        public  ClassDeclaration isClassHandle() {
            return null;
        }

        public  int alignment() {
            return -1;
        }

        public  Expression defaultInitLiteral(Loc loc) {
            return defaultInit(this, loc);
        }

        public  boolean isZeroInit(Loc loc) {
            return false;
        }

        public  Identifier getTypeInfoIdent() {
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                buf.value.reserve(32);
                mangleToBuffer(this, ptr(buf));
                ByteSlice slice = buf.value.peekSlice().copy();
                ByteSlice namebuf = (byte)255;
                int namelen = 31 + slice.getLength() + 1;
                BytePtr name = pcopy((namelen <= 128) ? namebuf.ptr() : ((BytePtr)malloc(namelen)));
                assert(name != null);
                int length = sprintf(name, new BytePtr("_D%lluTypeInfo_%.*s6__initZ"), (long)(9 + slice.getLength()), slice.getLength(), toBytePtr(slice));
                assert((0 < length) && (length < namelen));
                Identifier id = Identifier.idPool(name, length);
                if ((name != namebuf.ptr()))
                {
                    free(name);
                }
                return id;
            }
            finally {
            }
        }

        public  int hasWild() {
            return (this.mod & 0xFF) & MODFlags.wild;
        }

        public  boolean hasPointers() {
            return false;
        }

        public  boolean hasVoidInitPointers() {
            return false;
        }

        public  Type nextOf() {
            return null;
        }

        public  Type baseElemOf() {
            Type t = this.toBasetype();
            TypeSArray tsa = null;
            for (; ((tsa = t.isTypeSArray()) != null);) {
                t = tsa.next.value.toBasetype();
            }
            return t;
        }

        public  int numberOfElems(Loc loc) {
            long n = 1L;
            Type tb = this;
            for (; (((tb = tb.toBasetype()).ty & 0xFF) == ENUMTY.Tsarray);){
                Ref<Boolean> overflow = ref(false);
                n = mulu(n, ((TypeSArray)tb).dim.toUInteger(), overflow);
                if (overflow.value || (n >= 4294967295L))
                {
                    error(loc, new BytePtr("static array `%s` size overflowed to %llu"), this.toChars(), n);
                    return -1;
                }
                tb = ((TypeSArray)tb).next.value;
            }
            return (int)n;
        }

        public  long sizemask() {
            long m = 0L;
            switch ((this.toBasetype().ty & 0xFF))
            {
                case 30:
                    m = 1L;
                    break;
                case 31:
                case 13:
                case 14:
                    m = 255L;
                    break;
                case 32:
                case 15:
                case 16:
                    m = 65535L;
                    break;
                case 33:
                case 17:
                case 18:
                    m = 4294967295L;
                    break;
                case 19:
                case 20:
                    m = -1L;
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            return m;
        }

        public  boolean needsDestruction() {
            return false;
        }

        public  boolean needsNested() {
            return false;
        }

        public  boolean checkComplexTransition(Loc loc, Ptr<Scope> sc) {
            if ((sc.get()).isDeprecated())
            {
                return false;
            }
            Type t = this.baseElemOf();
            for (; ((t.ty & 0xFF) == ENUMTY.Tpointer) || ((t.ty & 0xFF) == ENUMTY.Tarray);) {
                t = t.nextOf().baseElemOf();
            }
            if (((t.ty & 0xFF) == ENUMTY.Tenum) && (((TypeEnum)t).sym.memtype == null))
            {
                return false;
            }
            if (t.isimaginary() || t.iscomplex())
            {
                Type rt = null;
                switch ((t.ty & 0xFF))
                {
                    case 27:
                    case 24:
                        rt = tfloat32;
                        break;
                    case 28:
                    case 25:
                        rt = tfloat64;
                        break;
                    case 29:
                    case 26:
                        rt = tfloat80;
                        break;
                    default:
                    throw new AssertionError("Unreachable code!");
                }
                if (t.iscomplex())
                {
                    deprecation(loc, new BytePtr("use of complex type `%s` is deprecated, use `std.complex.Complex!(%s)` instead"), this.toChars(), rt.toChars());
                    return true;
                }
                else
                {
                    deprecation(loc, new BytePtr("use of imaginary type `%s` is deprecated, use `%s` instead"), this.toChars(), rt.toChars());
                    return true;
                }
            }
            return false;
        }

        public  TypeBasic isTypeBasic() {
            return null;
        }

        public  TypeError isTypeError() {
            return ((this.ty & 0xFF) == ENUMTY.Terror) ? (TypeError)this : null;
        }

        public  TypeVector isTypeVector() {
            return ((this.ty & 0xFF) == ENUMTY.Tvector) ? (TypeVector)this : null;
        }

        public  TypeSArray isTypeSArray() {
            return ((this.ty & 0xFF) == ENUMTY.Tsarray) ? (TypeSArray)this : null;
        }

        public  TypeDArray isTypeDArray() {
            return ((this.ty & 0xFF) == ENUMTY.Tarray) ? (TypeDArray)this : null;
        }

        public  TypeAArray isTypeAArray() {
            return ((this.ty & 0xFF) == ENUMTY.Taarray) ? (TypeAArray)this : null;
        }

        public  TypePointer isTypePointer() {
            return ((this.ty & 0xFF) == ENUMTY.Tpointer) ? (TypePointer)this : null;
        }

        public  TypeReference isTypeReference() {
            return ((this.ty & 0xFF) == ENUMTY.Treference) ? (TypeReference)this : null;
        }

        public  TypeFunction isTypeFunction() {
            return ((this.ty & 0xFF) == ENUMTY.Tfunction) ? (TypeFunction)this : null;
        }

        public  TypeDelegate isTypeDelegate() {
            return ((this.ty & 0xFF) == ENUMTY.Tdelegate) ? (TypeDelegate)this : null;
        }

        public  TypeIdentifier isTypeIdentifier() {
            return ((this.ty & 0xFF) == ENUMTY.Tident) ? (TypeIdentifier)this : null;
        }

        public  TypeInstance isTypeInstance() {
            return ((this.ty & 0xFF) == ENUMTY.Tinstance) ? (TypeInstance)this : null;
        }

        public  TypeTypeof isTypeTypeof() {
            return ((this.ty & 0xFF) == ENUMTY.Ttypeof) ? (TypeTypeof)this : null;
        }

        public  TypeReturn isTypeReturn() {
            return ((this.ty & 0xFF) == ENUMTY.Treturn) ? (TypeReturn)this : null;
        }

        public  TypeStruct isTypeStruct() {
            return ((this.ty & 0xFF) == ENUMTY.Tstruct) ? (TypeStruct)this : null;
        }

        public  TypeEnum isTypeEnum() {
            return ((this.ty & 0xFF) == ENUMTY.Tenum) ? (TypeEnum)this : null;
        }

        public  TypeClass isTypeClass() {
            return ((this.ty & 0xFF) == ENUMTY.Tclass) ? (TypeClass)this : null;
        }

        public  TypeTuple isTypeTuple() {
            return ((this.ty & 0xFF) == ENUMTY.Ttuple) ? (TypeTuple)this : null;
        }

        public  TypeSlice isTypeSlice() {
            return ((this.ty & 0xFF) == ENUMTY.Tslice) ? (TypeSlice)this : null;
        }

        public  TypeNull isTypeNull() {
            return ((this.ty & 0xFF) == ENUMTY.Tnull) ? (TypeNull)this : null;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }

        public  TypeFunction toTypeFunction() {
            if (((this.ty & 0xFF) != ENUMTY.Tfunction))
            {
                throw new AssertionError("Unreachable code!");
            }
            return (TypeFunction)this;
        }


        public Type() {}

        public abstract Type copy();
    }
    public static class TypeError extends Type
    {
        public  TypeError() {
            super((byte)34);
        }

        public  Type syntaxCopy() {
            return this;
        }

        public  long size(Loc loc) {
            return -1L;
        }

        public  Expression defaultInitLiteral(Loc loc) {
            return new ErrorExp();
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeError copy() {
            TypeError that = new TypeError();
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static abstract class TypeNext extends Type
    {
        public Ref<Type> next = ref(null);
        public  TypeNext(byte ty, Type next) {
            super(ty);
            this.next.value = next;
        }

        public  void checkDeprecated(Loc loc, Ptr<Scope> sc) {
            this.checkDeprecated(loc, sc);
            if (this.next.value != null)
            {
                this.next.value.checkDeprecated(loc, sc);
            }
        }

        public  int hasWild() {
            if (((this.ty & 0xFF) == ENUMTY.Tfunction))
            {
                return 0;
            }
            if (((this.ty & 0xFF) == ENUMTY.Tdelegate))
            {
                return this.hasWild();
            }
            return (((((this.mod & 0xFF) & MODFlags.wild) != 0) || (this.next.value != null) && (this.next.value.hasWild() != 0)) ? 1 : 0);
        }

        public  Type nextOf() {
            return this.next.value;
        }

        public  Type makeConst() {
            if (this.cto != null)
            {
                assert(((this.cto.mod & 0xFF) == MODFlags.const_));
                return this.cto;
            }
            TypeNext t = (TypeNext)this.makeConst();
            if (((this.ty & 0xFF) != ENUMTY.Tfunction) && ((this.next.value.ty & 0xFF) != ENUMTY.Tfunction) && !this.next.value.isImmutable())
            {
                if (this.next.value.isShared())
                {
                    if (this.next.value.isWild())
                    {
                        t.next.value = this.next.value.sharedWildConstOf();
                    }
                    else
                    {
                        t.next.value = this.next.value.sharedConstOf();
                    }
                }
                else
                {
                    if (this.next.value.isWild())
                    {
                        t.next.value = this.next.value.wildConstOf();
                    }
                    else
                    {
                        t.next.value = this.next.value.constOf();
                    }
                }
            }
            return t;
        }

        public  Type makeImmutable() {
            if (this.ito != null)
            {
                assert(this.ito.isImmutable());
                return this.ito;
            }
            TypeNext t = (TypeNext)this.makeImmutable();
            if (((this.ty & 0xFF) != ENUMTY.Tfunction) && ((this.next.value.ty & 0xFF) != ENUMTY.Tfunction) && !this.next.value.isImmutable())
            {
                t.next.value = this.next.value.immutableOf();
            }
            return t;
        }

        public  Type makeShared() {
            if (this.sto != null)
            {
                assert(((this.sto.mod & 0xFF) == MODFlags.shared_));
                return this.sto;
            }
            TypeNext t = (TypeNext)this.makeShared();
            if (((this.ty & 0xFF) != ENUMTY.Tfunction) && ((this.next.value.ty & 0xFF) != ENUMTY.Tfunction) && !this.next.value.isImmutable())
            {
                if (this.next.value.isWild())
                {
                    if (this.next.value.isConst())
                    {
                        t.next.value = this.next.value.sharedWildConstOf();
                    }
                    else
                    {
                        t.next.value = this.next.value.sharedWildOf();
                    }
                }
                else
                {
                    if (this.next.value.isConst())
                    {
                        t.next.value = this.next.value.sharedConstOf();
                    }
                    else
                    {
                        t.next.value = this.next.value.sharedOf();
                    }
                }
            }
            return t;
        }

        public  Type makeSharedConst() {
            if (this.scto != null)
            {
                assert(((this.scto.mod & 0xFF) == 3));
                return this.scto;
            }
            TypeNext t = (TypeNext)this.makeSharedConst();
            if (((this.ty & 0xFF) != ENUMTY.Tfunction) && ((this.next.value.ty & 0xFF) != ENUMTY.Tfunction) && !this.next.value.isImmutable())
            {
                if (this.next.value.isWild())
                {
                    t.next.value = this.next.value.sharedWildConstOf();
                }
                else
                {
                    t.next.value = this.next.value.sharedConstOf();
                }
            }
            return t;
        }

        public  Type makeWild() {
            if (this.wto != null)
            {
                assert(((this.wto.mod & 0xFF) == MODFlags.wild));
                return this.wto;
            }
            TypeNext t = (TypeNext)this.makeWild();
            if (((this.ty & 0xFF) != ENUMTY.Tfunction) && ((this.next.value.ty & 0xFF) != ENUMTY.Tfunction) && !this.next.value.isImmutable())
            {
                if (this.next.value.isShared())
                {
                    if (this.next.value.isConst())
                    {
                        t.next.value = this.next.value.sharedWildConstOf();
                    }
                    else
                    {
                        t.next.value = this.next.value.sharedWildOf();
                    }
                }
                else
                {
                    if (this.next.value.isConst())
                    {
                        t.next.value = this.next.value.wildConstOf();
                    }
                    else
                    {
                        t.next.value = this.next.value.wildOf();
                    }
                }
            }
            return t;
        }

        public  Type makeWildConst() {
            if (this.wcto != null)
            {
                assert(((this.wcto.mod & 0xFF) == MODFlags.wildconst));
                return this.wcto;
            }
            TypeNext t = (TypeNext)this.makeWildConst();
            if (((this.ty & 0xFF) != ENUMTY.Tfunction) && ((this.next.value.ty & 0xFF) != ENUMTY.Tfunction) && !this.next.value.isImmutable())
            {
                if (this.next.value.isShared())
                {
                    t.next.value = this.next.value.sharedWildConstOf();
                }
                else
                {
                    t.next.value = this.next.value.wildConstOf();
                }
            }
            return t;
        }

        public  Type makeSharedWild() {
            if (this.swto != null)
            {
                assert(this.swto.isSharedWild());
                return this.swto;
            }
            TypeNext t = (TypeNext)this.makeSharedWild();
            if (((this.ty & 0xFF) != ENUMTY.Tfunction) && ((this.next.value.ty & 0xFF) != ENUMTY.Tfunction) && !this.next.value.isImmutable())
            {
                if (this.next.value.isConst())
                {
                    t.next.value = this.next.value.sharedWildConstOf();
                }
                else
                {
                    t.next.value = this.next.value.sharedWildOf();
                }
            }
            return t;
        }

        public  Type makeSharedWildConst() {
            if (this.swcto != null)
            {
                assert(((this.swcto.mod & 0xFF) == 11));
                return this.swcto;
            }
            TypeNext t = (TypeNext)this.makeSharedWildConst();
            if (((this.ty & 0xFF) != ENUMTY.Tfunction) && ((this.next.value.ty & 0xFF) != ENUMTY.Tfunction) && !this.next.value.isImmutable())
            {
                t.next.value = this.next.value.sharedWildConstOf();
            }
            return t;
        }

        public  Type makeMutable() {
            TypeNext t = (TypeNext)this.makeMutable();
            if (((this.ty & 0xFF) == ENUMTY.Tsarray))
            {
                t.next.value = this.next.value.mutableOf();
            }
            return t;
        }

        public  int constConv(Type to) {
            if (this.equals(to))
            {
                return MATCH.exact;
            }
            if (!(((this.ty & 0xFF) == (to.ty & 0xFF)) && MODimplicitConv(this.mod, to.mod)))
            {
                return MATCH.nomatch;
            }
            Type tn = to.nextOf();
            if (!((tn != null) && ((this.next.value.ty & 0xFF) == (tn.ty & 0xFF))))
            {
                return MATCH.nomatch;
            }
            int m = MATCH.nomatch;
            if (to.isConst())
            {
                m = this.next.value.constConv(tn);
                if ((m == MATCH.exact))
                {
                    m = MATCH.constant;
                }
            }
            else
            {
                m = this.next.value.equals(tn) ? MATCH.constant : MATCH.nomatch;
            }
            return m;
        }

        public  byte deduceWild(Type t, boolean isRef) {
            if (((this.ty & 0xFF) == ENUMTY.Tfunction))
            {
                return (byte)0;
            }
            byte wm = (byte)0;
            Type tn = t.nextOf();
            if (!isRef && ((this.ty & 0xFF) == ENUMTY.Tarray) || ((this.ty & 0xFF) == ENUMTY.Tpointer) && (tn != null))
            {
                wm = this.next.value.deduceWild(tn, true);
                if (wm == 0)
                {
                    wm = this.deduceWild(t, true);
                }
            }
            else
            {
                wm = this.deduceWild(t, isRef);
                if ((wm == 0) && (tn != null))
                {
                    wm = this.next.value.deduceWild(tn, true);
                }
            }
            return wm;
        }

        public  void transitive() {
            this.next.value = this.next.value.addMod(this.mod);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeNext() {}

        public abstract TypeNext copy();
    }
    public static class TypeBasic extends Type
    {
        public BytePtr dstring = null;
        public int flags = 0;
        public  TypeBasic(byte ty) {
            super(ty);
            BytePtr d = null;
            int flags = 0;
            switch ((ty & 0xFF))
            {
                case 12:
                    d = pcopy(Token.toChars(TOK.void_));
                    break;
                case 13:
                    d = pcopy(Token.toChars(TOK.int8));
                    flags |= 1;
                    break;
                case 14:
                    d = pcopy(Token.toChars(TOK.uns8));
                    flags |= 5;
                    break;
                case 15:
                    d = pcopy(Token.toChars(TOK.int16));
                    flags |= 1;
                    break;
                case 16:
                    d = pcopy(Token.toChars(TOK.uns16));
                    flags |= 5;
                    break;
                case 17:
                    d = pcopy(Token.toChars(TOK.int32));
                    flags |= 1;
                    break;
                case 18:
                    d = pcopy(Token.toChars(TOK.uns32));
                    flags |= 5;
                    break;
                case 21:
                    d = pcopy(Token.toChars(TOK.float32));
                    flags |= 10;
                    break;
                case 19:
                    d = pcopy(Token.toChars(TOK.int64));
                    flags |= 1;
                    break;
                case 20:
                    d = pcopy(Token.toChars(TOK.uns64));
                    flags |= 5;
                    break;
                case 42:
                    d = pcopy(Token.toChars(TOK.int128));
                    flags |= 1;
                    break;
                case 43:
                    d = pcopy(Token.toChars(TOK.uns128));
                    flags |= 5;
                    break;
                case 22:
                    d = pcopy(Token.toChars(TOK.float64));
                    flags |= 10;
                    break;
                case 23:
                    d = pcopy(Token.toChars(TOK.float80));
                    flags |= 10;
                    break;
                case 24:
                    d = pcopy(Token.toChars(TOK.imaginary32));
                    flags |= 18;
                    break;
                case 25:
                    d = pcopy(Token.toChars(TOK.imaginary64));
                    flags |= 18;
                    break;
                case 26:
                    d = pcopy(Token.toChars(TOK.imaginary80));
                    flags |= 18;
                    break;
                case 27:
                    d = pcopy(Token.toChars(TOK.complex32));
                    flags |= 34;
                    break;
                case 28:
                    d = pcopy(Token.toChars(TOK.complex64));
                    flags |= 34;
                    break;
                case 29:
                    d = pcopy(Token.toChars(TOK.complex80));
                    flags |= 34;
                    break;
                case 30:
                    d = pcopy(new BytePtr("bool"));
                    flags |= 5;
                    break;
                case 31:
                    d = pcopy(Token.toChars(TOK.char_));
                    flags |= 69;
                    break;
                case 32:
                    d = pcopy(Token.toChars(TOK.wchar_));
                    flags |= 69;
                    break;
                case 33:
                    d = pcopy(Token.toChars(TOK.dchar_));
                    flags |= 69;
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            this.dstring = pcopy(d);
            this.flags = flags;
            merge(this);
        }

        public  BytePtr kind() {
            return this.dstring;
        }

        public  Type syntaxCopy() {
            return this;
        }

        public  long size(Loc loc) {
            int size = 0;
            switch ((this.ty & 0xFF))
            {
                case 13:
                case 14:
                    size = 1;
                    break;
                case 15:
                case 16:
                    size = 2;
                    break;
                case 17:
                case 18:
                case 21:
                case 24:
                    size = 4;
                    break;
                case 19:
                case 20:
                case 22:
                case 25:
                    size = 8;
                    break;
                case 23:
                case 26:
                    size = target.realsize;
                    break;
                case 27:
                    size = 8;
                    break;
                case 28:
                case 42:
                case 43:
                    size = 16;
                    break;
                case 29:
                    size = target.realsize * 2;
                    break;
                case 12:
                    size = 1;
                    break;
                case 30:
                    size = 1;
                    break;
                case 31:
                    size = 1;
                    break;
                case 32:
                    size = 2;
                    break;
                case 33:
                    size = 4;
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            return (long)size;
        }

        public  int alignsize() {
            return target.alignsize(this);
        }

        public  boolean isintegral() {
            return (this.flags & 1) != 0;
        }

        public  boolean isfloating() {
            return (this.flags & 2) != 0;
        }

        public  boolean isreal() {
            return (this.flags & 8) != 0;
        }

        public  boolean isimaginary() {
            return (this.flags & 16) != 0;
        }

        public  boolean iscomplex() {
            return (this.flags & 32) != 0;
        }

        public  boolean isscalar() {
            return (this.flags & 3) != 0;
        }

        public  boolean isunsigned() {
            return (this.flags & 4) != 0;
        }

        public  boolean ischar() {
            return (this.flags & 64) != 0;
        }

        public  int implicitConvTo(Type to) {
            if ((pequals(this, to)))
            {
                return MATCH.exact;
            }
            if (((this.ty & 0xFF) == (to.ty & 0xFF)))
            {
                if (((this.mod & 0xFF) == (to.mod & 0xFF)))
                {
                    return MATCH.exact;
                }
                else if (MODimplicitConv(this.mod, to.mod))
                {
                    return MATCH.constant;
                }
                else if ((((this.mod & 0xFF) ^ (to.mod & 0xFF)) & MODFlags.shared_) == 0)
                {
                    return MATCH.constant;
                }
                else
                {
                    return MATCH.convert;
                }
            }
            if (((this.ty & 0xFF) == ENUMTY.Tvoid) || ((to.ty & 0xFF) == ENUMTY.Tvoid))
            {
                return MATCH.nomatch;
            }
            if (((to.ty & 0xFF) == ENUMTY.Tbool))
            {
                return MATCH.nomatch;
            }
            TypeBasic tob = null;
            if (((to.ty & 0xFF) == ENUMTY.Tvector) && (to.deco != null))
            {
                TypeVector tv = (TypeVector)to;
                tob = tv.elementType();
            }
            else {
                TypeEnum te = to.isTypeEnum();
                if ((te) != null)
                {
                    EnumDeclaration ed = te.sym;
                    if (ed.isSpecial())
                    {
                        tob = to.toBasetype().isTypeBasic();
                    }
                    else
                    {
                        return MATCH.nomatch;
                    }
                }
                else
                {
                    tob = to.isTypeBasic();
                }
            }
            if (tob == null)
            {
                return MATCH.nomatch;
            }
            if ((this.flags & 1) != 0)
            {
                if ((tob.flags & 48) != 0)
                {
                    return MATCH.nomatch;
                }
                if ((tob.flags & 1) != 0)
                {
                    long sz = this.size(Loc.initial);
                    long tosz = tob.size(Loc.initial);
                    if ((sz > tosz))
                    {
                        return MATCH.nomatch;
                    }
                }
            }
            else if ((this.flags & 2) != 0)
            {
                if ((tob.flags & 1) != 0)
                {
                    return MATCH.nomatch;
                }
                assert(((tob.flags & 2) != 0) || ((to.ty & 0xFF) == ENUMTY.Tvector));
                if (((this.flags & 32) != 0) && ((tob.flags & 32) == 0))
                {
                    return MATCH.nomatch;
                }
                if (((this.flags & 24) != 0) && ((tob.flags & 32) != 0))
                {
                    return MATCH.nomatch;
                }
                if (((this.flags & 24) != (tob.flags & 24)))
                {
                    return MATCH.nomatch;
                }
            }
            return MATCH.convert;
        }

        public  boolean isZeroInit(Loc loc) {
            switch ((this.ty & 0xFF))
            {
                case 31:
                case 32:
                case 33:
                case 24:
                case 25:
                case 26:
                case 21:
                case 22:
                case 23:
                case 27:
                case 28:
                case 29:
                    return false;
                default:
                return true;
            }
        }

        public  TypeBasic isTypeBasic() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeBasic() {}

        public TypeBasic copy() {
            TypeBasic that = new TypeBasic();
            that.dstring = this.dstring;
            that.flags = this.flags;
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static class TypeVector extends Type
    {
        public Type basetype = null;
        public  TypeVector(Type basetype) {
            super((byte)41);
            this.basetype = basetype;
        }

        public static TypeVector create(Type basetype) {
            return new TypeVector(basetype);
        }

        public  BytePtr kind() {
            return new BytePtr("vector");
        }

        public  Type syntaxCopy() {
            return new TypeVector(this.basetype.syntaxCopy());
        }

        public  long size(Loc loc) {
            return this.basetype.size();
        }

        public  int alignsize() {
            return (int)this.basetype.size();
        }

        public  boolean isintegral() {
            return this.basetype.nextOf().isintegral();
        }

        public  boolean isfloating() {
            return this.basetype.nextOf().isfloating();
        }

        public  boolean isscalar() {
            return this.basetype.nextOf().isscalar();
        }

        public  boolean isunsigned() {
            return this.basetype.nextOf().isunsigned();
        }

        public  boolean isBoolean() {
            return false;
        }

        public  int implicitConvTo(Type to) {
            if ((pequals(this, to)))
            {
                return MATCH.exact;
            }
            if (((this.ty & 0xFF) == (to.ty & 0xFF)))
            {
                return MATCH.convert;
            }
            return MATCH.nomatch;
        }

        public  Expression defaultInitLiteral(Loc loc) {
            assert(((this.basetype.ty & 0xFF) == ENUMTY.Tsarray));
            Expression e = this.basetype.defaultInitLiteral(loc);
            VectorExp ve = new VectorExp(loc, e, this);
            ve.type.value = this;
            ve.dim = (int)(this.basetype.size(loc) / this.elementType().size(loc));
            return ve;
        }

        public  TypeBasic elementType() {
            assert(((this.basetype.ty & 0xFF) == ENUMTY.Tsarray));
            TypeSArray t = (TypeSArray)this.basetype;
            TypeBasic tb = t.nextOf().isTypeBasic();
            assert(tb != null);
            return tb;
        }

        public  boolean isZeroInit(Loc loc) {
            return this.basetype.isZeroInit(loc);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeVector() {}

        public TypeVector copy() {
            TypeVector that = new TypeVector();
            that.basetype = this.basetype;
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static abstract class TypeArray extends TypeNext
    {
        public  TypeArray(byte ty, Type next) {
            super(ty, next);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeArray() {}

        public abstract TypeArray copy();
    }
    public static class TypeSArray extends TypeArray
    {
        public Expression dim = null;
        public  TypeSArray(Type t, Expression dim) {
            super((byte)1, t);
            this.dim = dim;
        }

        public  BytePtr kind() {
            return new BytePtr("sarray");
        }

        public  Type syntaxCopy() {
            Type t = this.next.value.syntaxCopy();
            Expression e = this.dim.syntaxCopy();
            t = new TypeSArray(t, e);
            t.mod = this.mod;
            return t;
        }

        public  long size(Loc loc) {
            int n = this.numberOfElems(loc);
            long elemsize = this.baseElemOf().size(loc);
            Ref<Boolean> overflow = ref(false);
            long sz = mulu((long)n, elemsize, overflow);
            if (overflow.value || (sz >= 4294967295L))
            {
                if ((elemsize != -1L) && (n != -1))
                {
                    error(loc, new BytePtr("static array `%s` size overflowed to %lld"), this.toChars(), (long)sz);
                }
                return -1L;
            }
            return sz;
        }

        public  int alignsize() {
            return this.next.value.alignsize();
        }

        public  boolean isString() {
            byte nty = this.next.value.toBasetype().ty;
            return ((nty & 0xFF) == ENUMTY.Tchar) || ((nty & 0xFF) == ENUMTY.Twchar) || ((nty & 0xFF) == ENUMTY.Tdchar);
        }

        public  boolean isZeroInit(Loc loc) {
            return this.next.value.isZeroInit(loc);
        }

        public  int alignment() {
            return this.next.value.alignment();
        }

        public  int constConv(Type to) {
            {
                TypeSArray tsa = to.isTypeSArray();
                if ((tsa) != null)
                {
                    if (!this.dim.equals(tsa.dim))
                    {
                        return MATCH.nomatch;
                    }
                }
            }
            return this.constConv(to);
        }

        public  int implicitConvTo(Type to) {
            {
                TypeDArray ta = to.isTypeDArray();
                if ((ta) != null)
                {
                    if (!MODimplicitConv(this.next.value.mod, ta.next.value.mod))
                    {
                        return MATCH.nomatch;
                    }
                    if (((ta.next.value.ty & 0xFF) == ENUMTY.Tvoid))
                    {
                        return MATCH.convert;
                    }
                    int m = this.next.value.constConv(ta.next.value);
                    if ((m > MATCH.nomatch))
                    {
                        return MATCH.convert;
                    }
                    return MATCH.nomatch;
                }
            }
            {
                TypeSArray tsa = to.isTypeSArray();
                if ((tsa) != null)
                {
                    if ((pequals(this, to)))
                    {
                        return MATCH.exact;
                    }
                    if (this.dim.equals(tsa.dim))
                    {
                        int m = this.next.value.implicitConvTo(tsa.next.value);
                        if ((m >= MATCH.constant))
                        {
                            if (((this.mod & 0xFF) != (to.mod & 0xFF)))
                            {
                                m = MATCH.constant;
                            }
                            return m;
                        }
                    }
                }
            }
            return MATCH.nomatch;
        }

        public  Expression defaultInitLiteral(Loc loc) {
            int d = (int)this.dim.toInteger();
            Expression elementinit = null;
            if (((this.next.value.ty & 0xFF) == ENUMTY.Tvoid))
            {
                elementinit = Type.tuns8.defaultInitLiteral(loc);
            }
            else
            {
                elementinit = this.next.value.defaultInitLiteral(loc);
            }
            Ptr<DArray<Expression>> elements = refPtr(new DArray<Expression>(d));
            {
                Slice<Expression> __r1531 = (elements.get()).opSlice().copy();
                int __key1532 = 0;
                for (; (__key1532 < __r1531.getLength());__key1532 += 1) {
                    Expression e = __r1531.get(__key1532);
                    e = null;
                }
            }
            ArrayLiteralExp ae = new ArrayLiteralExp(Loc.initial, this, elementinit, elements);
            return ae;
        }

        public  boolean hasPointers() {
            if (((this.next.value.ty & 0xFF) == ENUMTY.Tvoid))
            {
                return true;
            }
            else
            {
                return this.next.value.hasPointers();
            }
        }

        public  boolean needsDestruction() {
            return this.next.value.needsDestruction();
        }

        public  boolean needsNested() {
            return this.next.value.needsNested();
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeSArray() {}

        public TypeSArray copy() {
            TypeSArray that = new TypeSArray();
            that.dim = this.dim;
            that.next = this.next;
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static class TypeDArray extends TypeArray
    {
        public  TypeDArray(Type t) {
            super((byte)0, t);
        }

        public  BytePtr kind() {
            return new BytePtr("darray");
        }

        public  Type syntaxCopy() {
            Type t = this.next.value.syntaxCopy();
            if ((pequals(t, this.next.value)))
            {
                t = this;
            }
            else
            {
                t = new TypeDArray(t);
                t.mod = this.mod;
            }
            return t;
        }

        public  long size(Loc loc) {
            return (long)(target.ptrsize * 2);
        }

        public  int alignsize() {
            return target.ptrsize;
        }

        public  boolean isString() {
            byte nty = this.next.value.toBasetype().ty;
            return ((nty & 0xFF) == ENUMTY.Tchar) || ((nty & 0xFF) == ENUMTY.Twchar) || ((nty & 0xFF) == ENUMTY.Tdchar);
        }

        public  boolean isZeroInit(Loc loc) {
            return true;
        }

        public  boolean isBoolean() {
            return true;
        }

        public  int implicitConvTo(Type to) {
            if (this.equals(to))
            {
                return MATCH.exact;
            }
            {
                TypeDArray ta = to.isTypeDArray();
                if ((ta) != null)
                {
                    if (!MODimplicitConv(this.next.value.mod, ta.next.value.mod))
                    {
                        return MATCH.nomatch;
                    }
                    if (((this.next.value.ty & 0xFF) != ENUMTY.Tvoid) && ((ta.next.value.ty & 0xFF) == ENUMTY.Tvoid))
                    {
                        return MATCH.convert;
                    }
                    int m = this.next.value.constConv(ta.next.value);
                    if ((m > MATCH.nomatch))
                    {
                        if ((m == MATCH.exact) && ((this.mod & 0xFF) != (to.mod & 0xFF)))
                        {
                            m = MATCH.constant;
                        }
                        return m;
                    }
                }
            }
            return this.implicitConvTo(to);
        }

        public  boolean hasPointers() {
            return true;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeDArray() {}

        public TypeDArray copy() {
            TypeDArray that = new TypeDArray();
            that.next = this.next;
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static class TypeAArray extends TypeArray
    {
        public Type index = null;
        public Loc loc = new Loc();
        public Ptr<Scope> sc = null;
        public  TypeAArray(Type t, Type index) {
            super((byte)2, t);
            this.index = index;
        }

        public static TypeAArray create(Type t, Type index) {
            return new TypeAArray(t, index);
        }

        public  BytePtr kind() {
            return new BytePtr("aarray");
        }

        public  Type syntaxCopy() {
            Type t = this.next.value.syntaxCopy();
            Type ti = this.index.syntaxCopy();
            if ((pequals(t, this.next.value)) && (pequals(ti, this.index)))
            {
                t = this;
            }
            else
            {
                t = new TypeAArray(t, ti);
                t.mod = this.mod;
            }
            return t;
        }

        public  long size(Loc loc) {
            return (long)target.ptrsize;
        }

        public  boolean isZeroInit(Loc loc) {
            return true;
        }

        public  boolean isBoolean() {
            return true;
        }

        public  boolean hasPointers() {
            return true;
        }

        public  int implicitConvTo(Type to) {
            if (this.equals(to))
            {
                return MATCH.exact;
            }
            {
                TypeAArray ta = to.isTypeAArray();
                if ((ta) != null)
                {
                    if (!MODimplicitConv(this.next.value.mod, ta.next.value.mod))
                    {
                        return MATCH.nomatch;
                    }
                    if (!MODimplicitConv(this.index.mod, ta.index.mod))
                    {
                        return MATCH.nomatch;
                    }
                    int m = this.next.value.constConv(ta.next.value);
                    int mi = this.index.constConv(ta.index);
                    if ((m > MATCH.nomatch) && (mi > MATCH.nomatch))
                    {
                        return MODimplicitConv(this.mod, to.mod) ? MATCH.constant : MATCH.nomatch;
                    }
                }
            }
            return this.implicitConvTo(to);
        }

        public  int constConv(Type to) {
            {
                TypeAArray taa = to.isTypeAArray();
                if ((taa) != null)
                {
                    int mindex = this.index.constConv(taa.index);
                    int mkey = this.next.value.constConv(taa.next.value);
                    return (mkey < mindex) ? mkey : mindex;
                }
            }
            return this.constConv(to);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeAArray() {}

        public TypeAArray copy() {
            TypeAArray that = new TypeAArray();
            that.index = this.index;
            that.loc = this.loc;
            that.sc = this.sc;
            that.next = this.next;
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static class TypePointer extends TypeNext
    {
        public  TypePointer(Type t) {
            super((byte)3, t);
        }

        public static TypePointer create(Type t) {
            return new TypePointer(t);
        }

        public  BytePtr kind() {
            return new BytePtr("pointer");
        }

        public  Type syntaxCopy() {
            Type t = this.next.value.syntaxCopy();
            if ((pequals(t, this.next.value)))
            {
                t = this;
            }
            else
            {
                t = new TypePointer(t);
                t.mod = this.mod;
            }
            return t;
        }

        public  long size(Loc loc) {
            return (long)target.ptrsize;
        }

        public  int implicitConvTo(Type to) {
            if (this.equals(to))
            {
                return MATCH.exact;
            }
            if (((this.next.value.ty & 0xFF) == ENUMTY.Tfunction))
            {
                {
                    TypePointer tp = to.isTypePointer();
                    if ((tp) != null)
                    {
                        if (((tp.next.value.ty & 0xFF) == ENUMTY.Tfunction))
                        {
                            if (this.next.value.equals(tp.next.value))
                            {
                                return MATCH.constant;
                            }
                            if ((this.next.value.covariant(tp.next.value, null, true) == 1))
                            {
                                Type tret = this.next.value.nextOf();
                                Type toret = tp.next.value.nextOf();
                                if (((tret.ty & 0xFF) == ENUMTY.Tclass) && ((toret.ty & 0xFF) == ENUMTY.Tclass))
                                {
                                    Ref<Integer> offset = ref(0);
                                    if (toret.isBaseOf(tret, ptr(offset)) && (offset.value != 0))
                                    {
                                        return MATCH.nomatch;
                                    }
                                }
                                return MATCH.convert;
                            }
                        }
                        else if (((tp.next.value.ty & 0xFF) == ENUMTY.Tvoid))
                        {
                            return MATCH.convert;
                        }
                    }
                }
                return MATCH.nomatch;
            }
            else {
                TypePointer tp = to.isTypePointer();
                if ((tp) != null)
                {
                    assert(tp.next.value != null);
                    if (!MODimplicitConv(this.next.value.mod, tp.next.value.mod))
                    {
                        return MATCH.nomatch;
                    }
                    if (((this.next.value.ty & 0xFF) != ENUMTY.Tvoid) && ((tp.next.value.ty & 0xFF) == ENUMTY.Tvoid))
                    {
                        return MATCH.convert;
                    }
                    int m = this.next.value.constConv(tp.next.value);
                    if ((m > MATCH.nomatch))
                    {
                        if ((m == MATCH.exact) && ((this.mod & 0xFF) != (to.mod & 0xFF)))
                        {
                            m = MATCH.constant;
                        }
                        return m;
                    }
                }
            }
            return MATCH.nomatch;
        }

        public  int constConv(Type to) {
            if (((this.next.value.ty & 0xFF) == ENUMTY.Tfunction))
            {
                if ((to.nextOf() != null) && this.next.value.equals(((TypeNext)to).next.value))
                {
                    return this.constConv(to);
                }
                else
                {
                    return MATCH.nomatch;
                }
            }
            return this.constConv(to);
        }

        public  boolean isscalar() {
            return true;
        }

        public  boolean isZeroInit(Loc loc) {
            return true;
        }

        public  boolean hasPointers() {
            return true;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypePointer() {}

        public TypePointer copy() {
            TypePointer that = new TypePointer();
            that.next = this.next;
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static class TypeReference extends TypeNext
    {
        public  TypeReference(Type t) {
            super((byte)4, t);
        }

        public  BytePtr kind() {
            return new BytePtr("reference");
        }

        public  Type syntaxCopy() {
            Type t = this.next.value.syntaxCopy();
            if ((pequals(t, this.next.value)))
            {
                t = this;
            }
            else
            {
                t = new TypeReference(t);
                t.mod = this.mod;
            }
            return t;
        }

        public  long size(Loc loc) {
            return (long)target.ptrsize;
        }

        public  boolean isZeroInit(Loc loc) {
            return true;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeReference() {}

        public TypeReference copy() {
            TypeReference that = new TypeReference();
            that.next = this.next;
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }

    public static class RET 
    {
        public static final int regs = 1;
        public static final int stack = 2;
    }


    public static class TRUST 
    {
        public static final int default_ = 0;
        public static final int system = 1;
        public static final int trusted = 2;
        public static final int safe = 3;
    }


    public static class TRUSTformat 
    {
        public static final int TRUSTformatDefault = 0;
        public static final int TRUSTformatSystem = 1;
    }


    public static class PURE 
    {
        public static final int impure = 0;
        public static final int fwdref = 1;
        public static final int weak = 2;
        public static final int const_ = 3;
        public static final int strong = 4;
    }

    public static class TypeFunction extends TypeNext
    {
        public ParameterList parameterList = new ParameterList();
        public boolean isnothrow = false;
        public boolean isnogc = false;
        public boolean isproperty = false;
        public boolean isref = false;
        public boolean isreturn = false;
        public boolean isscope = false;
        public boolean isreturninferred = false;
        public boolean isscopeinferred = false;
        public int linkage = 0;
        public int trust = 0;
        public int purity = PURE.impure;
        public byte iswild = 0;
        public Ptr<DArray<Expression>> fargs = null;
        public int inuse = 0;
        public boolean incomplete = false;
        public  TypeFunction(ParameterList pl, Type treturn, int linkage, long stc) {
            super((byte)5, treturn);
            assert((VarArg.none <= pl.varargs) && (pl.varargs <= VarArg.typesafe));
            this.parameterList.opAssign(pl.copy());
            this.linkage = linkage;
            if ((stc & 67108864L) != 0)
            {
                this.purity = PURE.fwdref;
            }
            if ((stc & 33554432L) != 0)
            {
                this.isnothrow = true;
            }
            if ((stc & 4398046511104L) != 0)
            {
                this.isnogc = true;
            }
            if ((stc & 4294967296L) != 0)
            {
                this.isproperty = true;
            }
            if ((stc & 2097152L) != 0)
            {
                this.isref = true;
            }
            if ((stc & 17592186044416L) != 0)
            {
                this.isreturn = true;
            }
            if ((stc & 4503599627370496L) != 0)
            {
                this.isreturninferred = true;
            }
            if ((stc & 524288L) != 0)
            {
                this.isscope = true;
            }
            if ((stc & 562949953421312L) != 0)
            {
                this.isscopeinferred = true;
            }
            this.trust = TRUST.default_;
            if ((stc & 8589934592L) != 0)
            {
                this.trust = TRUST.safe;
            }
            if ((stc & 34359738368L) != 0)
            {
                this.trust = TRUST.system;
            }
            if ((stc & 17179869184L) != 0)
            {
                this.trust = TRUST.trusted;
            }
        }

        // defaulted all parameters starting with #4
        public  TypeFunction(ParameterList pl, Type treturn, int linkage) {
            this(pl, treturn, linkage, 0L);
        }

        public static TypeFunction create(Ptr<DArray<Parameter>> parameters, Type treturn, int varargs, int linkage, long stc) {
            return new TypeFunction(new ParameterList(parameters, varargs), treturn, linkage, stc);
        }

        // defaulted all parameters starting with #5
        public static TypeFunction create(Ptr<DArray<Parameter>> parameters, Type treturn, int varargs, int linkage) {
            return create(parameters, treturn, varargs, linkage, 0L);
        }

        public  BytePtr kind() {
            return new BytePtr("function");
        }

        public  Type syntaxCopy() {
            Type treturn = this.next.value != null ? this.next.value.syntaxCopy() : null;
            Ptr<DArray<Parameter>> params = Parameter.arraySyntaxCopy(this.parameterList.parameters);
            TypeFunction t = new TypeFunction(new ParameterList(params, this.parameterList.varargs), treturn, this.linkage, 0L);
            t.mod = this.mod;
            t.isnothrow = this.isnothrow;
            t.isnogc = this.isnogc;
            t.purity = this.purity;
            t.isproperty = this.isproperty;
            t.isref = this.isref;
            t.isreturn = this.isreturn;
            t.isscope = this.isscope;
            t.isreturninferred = this.isreturninferred;
            t.isscopeinferred = this.isscopeinferred;
            t.iswild = this.iswild;
            t.trust = this.trust;
            t.fargs = pcopy(this.fargs);
            return t;
        }

        public  void purityLevel() {
            TypeFunction tf = this;
            if ((tf.purity != PURE.fwdref))
            {
                return ;
            }
            Function2<Boolean,Type,Integer> purityOfType = new Function2<Boolean,Type,Integer>() {
                public Integer invoke(Boolean isref, Type t) {
                 {
                    Ref<Type> t_ref = ref(t);
                    if (isref)
                    {
                        if (((t_ref.value.mod & 0xFF) & MODFlags.immutable_) != 0)
                        {
                            return PURE.strong;
                        }
                        if (((t_ref.value.mod & 0xFF) & MODFlags.wildconst) != 0)
                        {
                            return PURE.const_;
                        }
                        return PURE.weak;
                    }
                    t_ref.value = t_ref.value.baseElemOf();
                    if (!t_ref.value.hasPointers() || (((t_ref.value.mod & 0xFF) & MODFlags.immutable_) != 0))
                    {
                        return PURE.strong;
                    }
                    if (((t_ref.value.ty & 0xFF) == ENUMTY.Tarray) || ((t_ref.value.ty & 0xFF) == ENUMTY.Tpointer))
                    {
                        Type tn = t_ref.value.nextOf().toBasetype();
                        if (((tn.mod & 0xFF) & MODFlags.immutable_) != 0)
                        {
                            return PURE.strong;
                        }
                        if (((tn.mod & 0xFF) & MODFlags.wildconst) != 0)
                        {
                            return PURE.const_;
                        }
                    }
                    if (((t_ref.value.mod & 0xFF) & MODFlags.wildconst) != 0)
                    {
                        return PURE.const_;
                    }
                    return PURE.weak;
                }}

            };
            this.purity = PURE.strong;
            int dim = tf.parameterList.length();
        /*Lloop:*/
            {
                int __key1533 = 0;
                int __limit1534 = dim;
                for (; (__key1533 < __limit1534);__key1533 += 1) {
                    int i = __key1533;
                    Parameter fparam = tf.parameterList.get(i);
                    Type t = fparam.type;
                    if (t == null)
                    {
                        continue;
                    }
                    if ((fparam.storageClass & 12288L) != 0)
                    {
                        this.purity = PURE.weak;
                        break;
                    }
                    switch (purityOfType.invoke((fparam.storageClass & 2097152L) != 0L, t))
                    {
                        case PURE.weak:
                            this.purity = PURE.weak;
                            break Lloop;
                        case PURE.const_:
                            this.purity = PURE.const_;
                            continue;
                        case PURE.strong:
                            continue;
                        default:
                        throw new AssertionError("Unreachable code!");
                    }
                }
            }
            if ((this.purity > PURE.weak) && (tf.nextOf() != null))
            {
                int purity2 = purityOfType.invoke(tf.isref, tf.nextOf());
                if ((purity2 < this.purity))
                {
                    this.purity = purity2;
                }
            }
            tf.purity = this.purity;
        }

        public  boolean hasLazyParameters() {
            int dim = this.parameterList.length();
            {
                int i = 0;
                for (; (i < dim);i++){
                    Parameter fparam = this.parameterList.get(i);
                    if ((fparam.storageClass & 8192L) != 0)
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public  boolean parameterEscapes(Type tthis, Parameter p) {
            if ((this.parameterStorageClass(tthis, p) & 532480L) != 0)
            {
                return false;
            }
            return true;
        }

        public  long parameterStorageClass(Type tthis, Parameter p) {
            long stc = p.storageClass;
            if (!global.params.vsafe)
            {
                return stc;
            }
            if (((stc & 17592186576896L) != 0) || (this.purity == PURE.impure))
            {
                return stc;
            }
            if (this.nextOf() == null)
            {
                return stc;
            }
            this.purityLevel();
            if ((this.purity == PURE.weak))
            {
                int dim = this.parameterList.length();
                {
                    int __key1535 = 0;
                    int __limit1536 = dim;
                    for (; (__key1535 < __limit1536);__key1535 += 1) {
                        int i = __key1535;
                        Parameter fparam = this.parameterList.get(i);
                        if ((pequals(fparam, p)))
                        {
                            continue;
                        }
                        Type t = fparam.type;
                        if (t == null)
                        {
                            continue;
                        }
                        t = t.baseElemOf();
                        if (t.isMutable() && t.hasPointers())
                        {
                            if ((fparam.storageClass & 2101248L) != 0)
                            {
                            }
                            else if (((t.ty & 0xFF) == ENUMTY.Tarray) || ((t.ty & 0xFF) == ENUMTY.Tpointer))
                            {
                                Type tn = t.nextOf().toBasetype();
                                if (!(tn.isMutable() && tn.hasPointers()))
                                {
                                    continue;
                                }
                            }
                            return stc;
                        }
                    }
                }
                if ((tthis != null) && tthis.isMutable())
                {
                    Type tb = tthis.toBasetype();
                    AggregateDeclaration ad = null;
                    {
                        TypeClass tc = tb.isTypeClass();
                        if ((tc) != null)
                        {
                            ad = tc.sym;
                        }
                        else {
                            TypeStruct ts = tb.isTypeStruct();
                            if ((ts) != null)
                            {
                                ad = ts.sym;
                            }
                            else
                            {
                                throw new AssertionError("Unreachable code!");
                            }
                        }
                    }
                    {
                        Slice<VarDeclaration> __r1537 = ad.fields.opSlice().copy();
                        int __key1538 = 0;
                        for (; (__key1538 < __r1537.getLength());__key1538 += 1) {
                            VarDeclaration v = __r1537.get(__key1538);
                            if (v.hasPointers())
                            {
                                return stc;
                            }
                        }
                    }
                }
            }
            stc |= 524288L;
            return stc;
        }

        public  Type addStorageClass(long stc) {
            TypeFunction t = this.addStorageClass(stc).toTypeFunction();
            if (((stc & 67108864L) != 0) && (t.purity == 0) || ((stc & 33554432L) != 0) && !t.isnothrow || ((stc & 4398046511104L) != 0) && !t.isnogc || ((stc & 524288L) != 0) && !t.isscope || ((stc & 8589934592L) != 0) && (t.trust < TRUST.trusted))
            {
                TypeFunction tf = new TypeFunction(t.parameterList, t.next.value, t.linkage, 0L);
                tf.mod = t.mod;
                tf.fargs = pcopy(this.fargs);
                tf.purity = t.purity;
                tf.isnothrow = t.isnothrow;
                tf.isnogc = t.isnogc;
                tf.isproperty = t.isproperty;
                tf.isref = t.isref;
                tf.isreturn = t.isreturn;
                tf.isscope = t.isscope;
                tf.isreturninferred = t.isreturninferred;
                tf.isscopeinferred = t.isscopeinferred;
                tf.trust = t.trust;
                tf.iswild = t.iswild;
                if ((stc & 67108864L) != 0)
                {
                    tf.purity = PURE.fwdref;
                }
                if ((stc & 33554432L) != 0)
                {
                    tf.isnothrow = true;
                }
                if ((stc & 4398046511104L) != 0)
                {
                    tf.isnogc = true;
                }
                if ((stc & 8589934592L) != 0)
                {
                    tf.trust = TRUST.safe;
                }
                if ((stc & 524288L) != 0)
                {
                    tf.isscope = true;
                    if ((stc & 562949953421312L) != 0)
                    {
                        tf.isscopeinferred = true;
                    }
                }
                tf.deco = pcopy(merge(tf).deco);
                t = tf;
            }
            return t;
        }

        public  Type substWildTo(int _param_0) {
            if ((this.iswild == 0) && (((this.mod & 0xFF) & MODFlags.wild) == 0))
            {
                return this;
            }
            int m = 1;
            assert(this.next.value != null);
            Type tret = this.next.value.substWildTo(m);
            Ptr<DArray<Parameter>> params = this.parameterList.parameters;
            if (((this.mod & 0xFF) & MODFlags.wild) != 0)
            {
                params = pcopy((this.parameterList.parameters.get()).copy());
            }
            {
                int i = 0;
                for (; (i < (params.get()).length);i++){
                    Parameter p = (params.get()).get(i);
                    Type t = p.type.substWildTo(m);
                    if ((pequals(t, p.type)))
                    {
                        continue;
                    }
                    if ((params == this.parameterList.parameters))
                    {
                        params = pcopy((this.parameterList.parameters.get()).copy());
                    }
                    params.get().set(i, new Parameter(p.storageClass, t, null, null, null));
                }
            }
            if ((pequals(this.next.value, tret)) && (params == this.parameterList.parameters))
            {
                return this;
            }
            TypeFunction t = new TypeFunction(new ParameterList(params, this.parameterList.varargs), tret, this.linkage, 0L);
            t.mod = ((this.mod & 0xFF) & MODFlags.wild) != 0 ? (byte)((this.mod & 0xFF) & -9 | MODFlags.const_) : (byte)(this.mod & 0xFF);
            t.isnothrow = this.isnothrow;
            t.isnogc = this.isnogc;
            t.purity = this.purity;
            t.isproperty = this.isproperty;
            t.isref = this.isref;
            t.isreturn = this.isreturn;
            t.isscope = this.isscope;
            t.isreturninferred = this.isreturninferred;
            t.isscopeinferred = this.isscopeinferred;
            t.iswild = (byte)0;
            t.trust = this.trust;
            t.fargs = pcopy(this.fargs);
            return merge(t);
        }

        public  BytePtr getParamError(Expression arg, Parameter par) {
            if ((global.gag != 0) && !global.params.showGaggedErrors)
            {
                return null;
            }
            BytePtr at = pcopy(arg.type.value.toChars());
            boolean qual = !arg.type.value.equals(par.type) && (strcmp(at, par.type.toChars()) == 0);
            if (qual)
            {
                at = pcopy(arg.type.value.toPrettyChars(true));
            }
            OutBuffer buf = new OutBuffer();
            try {
                boolean rv = !arg.isLvalue() && ((par.storageClass & 2101248L) != 0);
                buf.printf(new BytePtr("cannot pass %sargument `%s` of type `%s` to parameter `%s`"), rv ? new BytePtr("rvalue ") : new BytePtr(""), arg.toChars(), at, parameterToChars(par, this, qual));
                return buf.extractChars();
            }
            finally {
            }
        }

        // from template getMatchError!(IntegerBytePtr)
        public  BytePtr getMatchErrorIntegerBytePtr(BytePtr format, int _param_1, BytePtr _param_2) {
            if ((global.gag != 0) && !global.params.showGaggedErrors)
            {
                return null;
            }
            OutBuffer buf = new OutBuffer();
            try {
                buf.printf(format, _param_1, _param_2);
                return buf.extractChars();
            }
            finally {
            }
        }


        // from template getMatchError!(IntegerInteger)
        public  BytePtr getMatchErrorIntegerInteger(BytePtr format, int _param_1, int _param_2) {
            if ((global.gag != 0) && !global.params.showGaggedErrors)
            {
                return null;
            }
            OutBuffer buf = new OutBuffer();
            try {
                buf.printf(format, _param_1, _param_2);
                return buf.extractChars();
            }
            finally {
            }
        }


        public  int callMatch(Type tthis, Slice<Expression> args, int flag, Ptr<BytePtr> pMessage, Ptr<Scope> sc) {
            int match = MATCH.exact;
            byte wildmatch = (byte)0;
            if (tthis != null)
            {
                Type t = tthis;
                if (((t.toBasetype().ty & 0xFF) == ENUMTY.Tpointer))
                {
                    t = t.toBasetype().nextOf();
                }
                if (((t.mod & 0xFF) != (this.mod & 0xFF)))
                {
                    if (MODimplicitConv(t.mod, this.mod))
                    {
                        match = MATCH.constant;
                    }
                    else if ((((this.mod & 0xFF) & MODFlags.wild) != 0) && MODimplicitConv(t.mod, (byte)((this.mod & 0xFF) & -9 | MODFlags.const_)))
                    {
                        match = MATCH.constant;
                    }
                    else
                    {
                        return MATCH.nomatch;
                    }
                }
                if (this.isWild())
                {
                    if (t.isWild())
                    {
                        wildmatch |= MODFlags.wild;
                    }
                    else if (t.isConst())
                    {
                        wildmatch |= MODFlags.const_;
                    }
                    else if (t.isImmutable())
                    {
                        wildmatch |= MODFlags.immutable_;
                    }
                    else
                    {
                        wildmatch |= MODFlags.mutable;
                    }
                }
            }
            int nparams = this.parameterList.length();
            int nargs = args.getLength();
            try {
                try {
                    if ((nargs > nparams))
                    {
                        if ((this.parameterList.varargs == VarArg.none))
                        {
                            if (pMessage == null)
                            {
                                /*goto Nomatch*/throw Dispatch1.INSTANCE;
                            }
                        }
                        match = MATCH.convert;
                    }
                    {
                        int u = 0;
                        for (; (u < nargs);u++){
                            if ((u >= nparams))
                            {
                                break;
                            }
                            Parameter p = this.parameterList.get(u);
                            Expression arg = args.get(u);
                            assert(arg != null);
                            Type tprm = p.type;
                            Type targ = arg.type.value;
                            if (!(((p.storageClass & 8192L) != 0) && ((tprm.ty & 0xFF) == ENUMTY.Tvoid) && ((targ.ty & 0xFF) != ENUMTY.Tvoid)))
                            {
                                boolean isRef = (p.storageClass & 2101248L) != 0L;
                                wildmatch |= (targ.deduceWild(tprm, isRef) & 0xFF);
                            }
                        }
                    }
                    if (wildmatch != 0)
                    {
                        if ((((wildmatch & 0xFF) & MODFlags.const_) != 0) || (((wildmatch & 0xFF) & (wildmatch & 0xFF) - 1) != 0))
                        {
                            wildmatch = (byte)1;
                        }
                        else if (((wildmatch & 0xFF) & MODFlags.immutable_) != 0)
                        {
                            wildmatch = (byte)4;
                        }
                        else if (((wildmatch & 0xFF) & MODFlags.wild) != 0)
                        {
                            wildmatch = (byte)8;
                        }
                        else
                        {
                            assert(((wildmatch & 0xFF) & MODFlags.mutable) != 0);
                            wildmatch = (byte)16;
                        }
                    }
                    {
                        int u = 0;
                    L_outer2:
                        for (; (u < nparams);u++){
                            int m = MATCH.nomatch;
                            Parameter p = this.parameterList.get(u);
                            assert(p != null);
                            if ((u >= nargs))
                            {
                                if (p.defaultArg != null)
                                {
                                    continue L_outer2;
                                }
                                /*goto L1*//*unrolled goto*/
                            /*L1:*/
                                if ((this.parameterList.varargs == VarArg.typesafe) && (u + 1 == nparams))
                                {
                                    Type tb = p.type.toBasetype();
                                    TypeSArray tsa = null;
                                    long sz = 0L;
                                    {
                                        int __dispatch15 = 0;
                                        dispatched_15:
                                        do {
                                            switch (__dispatch15 != 0 ? __dispatch15 : (tb.ty & 0xFF))
                                            {
                                                case 1:
                                                    tsa = (TypeSArray)tb;
                                                    sz = tsa.dim.toInteger();
                                                    if ((sz != (long)(nargs - u)))
                                                    {
                                                        if (pMessage != null)
                                                        {
                                                            if ((global.gag == 0) || global.params.showGaggedErrors)
                                                            {
                                                                OutBuffer buf = new OutBuffer();
                                                                buf.printf(new BytePtr("expected %d variadic argument(s)"), sz);
                                                                buf.printf(new BytePtr(", not %d"), nargs - u);
                                                                pMessage.set(0, buf.extractChars());
                                                            }
                                                        }
                                                        /*goto Nomatch*/throw Dispatch1.INSTANCE;
                                                    }
                                                    /*goto case*/{ __dispatch15 = 0; continue dispatched_15; }
                                                case 0:
                                                    __dispatch15 = 0;
                                                    TypeArray ta = (TypeArray)tb;
                                                    {
                                                        Slice<Expression> __r1539 = args.slice(u,nargs).copy();
                                                        int __key1540 = 0;
                                                    L_outer3:
                                                        for (; (__key1540 < __r1539.getLength());__key1540 += 1) {
                                                            Expression arg = __r1539.get(__key1540);
                                                            assert(arg != null);
                                                            Type tret = p.isLazyArray();
                                                            if (tret != null)
                                                            {
                                                                if (ta.next.value.equals(arg.type.value))
                                                                {
                                                                    m = MATCH.exact;
                                                                }
                                                                else if (((tret.toBasetype().ty & 0xFF) == ENUMTY.Tvoid))
                                                                {
                                                                    m = MATCH.convert;
                                                                }
                                                                else
                                                                {
                                                                    m = arg.implicitConvTo(tret);
                                                                    if ((m == MATCH.nomatch))
                                                                    {
                                                                        m = arg.implicitConvTo(ta.next.value);
                                                                    }
                                                                }
                                                            }
                                                            else
                                                            {
                                                                m = arg.implicitConvTo(ta.next.value);
                                                            }
                                                            if ((m == MATCH.nomatch))
                                                            {
                                                                if (pMessage != null)
                                                                {
                                                                    pMessage.set(0, this.getParamError(arg, p));
                                                                }
                                                                /*goto Nomatch*/throw Dispatch1.INSTANCE;
                                                            }
                                                            if ((m < match))
                                                            {
                                                                match = m;
                                                            }
                                                        }
                                                    }
                                                    /*goto Ldone*/throw Dispatch0.INSTANCE;
                                                case 7:
                                                    /*goto Ldone*/throw Dispatch0.INSTANCE;
                                                default:
                                                break;
                                            }
                                        } while(__dispatch15 != 0);
                                    }
                                }
                                if ((pMessage != null) && (u < nargs))
                                {
                                    pMessage.set(0, this.getParamError(args.get(u), p));
                                }
                                else if (pMessage != null)
                                {
                                    pMessage.set(0, this.getMatchErrorIntegerBytePtr(new BytePtr("missing argument for parameter #%d: `%s`"), u + 1, parameterToChars(p, this, false)));
                                }
                                /*goto Nomatch*/throw Dispatch1.INSTANCE;
                            }
                            {
                                Expression arg = args.get(u);
                                assert(arg != null);
                                Type targ = arg.type.value;
                                Type tprm = wildmatch != 0 ? p.type.substWildTo((wildmatch & 0xFF)) : p.type;
                                if (((p.storageClass & 8192L) != 0) && ((tprm.ty & 0xFF) == ENUMTY.Tvoid) && ((targ.ty & 0xFF) != ENUMTY.Tvoid))
                                {
                                    m = MATCH.convert;
                                }
                                else
                                {
                                    if (flag != 0)
                                    {
                                        m = targ.implicitConvTo(tprm);
                                    }
                                    else
                                    {
                                        boolean isRef = (p.storageClass & 2101248L) != 0L;
                                        StructDeclaration argStruct = null;
                                        StructDeclaration prmStruct = null;
                                        if (arg.isLvalue() && !isRef && ((targ.ty & 0xFF) == ENUMTY.Tstruct) && ((tprm.ty & 0xFF) == ENUMTY.Tstruct))
                                        {
                                            argStruct = ((TypeStruct)targ).sym;
                                            prmStruct = ((TypeStruct)tprm).sym;
                                        }
                                        if ((argStruct != null) && (pequals(argStruct, prmStruct)) && argStruct.hasCopyCtor)
                                        {
                                            VarDeclaration tmp = new VarDeclaration(arg.loc, tprm, Identifier.generateId(new BytePtr("__copytmp")), null, 0L);
                                            dsymbolSemantic(tmp, sc);
                                            Expression ve = new VarExp(arg.loc, tmp, true);
                                            Expression e = new DotIdExp(arg.loc, ve, Id.ctor);
                                            e = new CallExp(arg.loc, e, arg);
                                            if (trySemantic(e, sc) != null)
                                            {
                                                m = MATCH.exact;
                                            }
                                            else
                                            {
                                                m = MATCH.nomatch;
                                                if (pMessage != null)
                                                {
                                                    OutBuffer buf = new OutBuffer();
                                                    try {
                                                        buf.printf(new BytePtr("`struct %s` does not define a copy constructor for `%s` to `%s` copies"), argStruct.toChars(), targ.toChars(), tprm.toChars());
                                                        pMessage.set(0, buf.extractChars());
                                                    }
                                                    finally {
                                                    }
                                                }
                                                /*goto Nomatch*/throw Dispatch1.INSTANCE;
                                            }
                                        }
                                        else
                                        {
                                            m = arg.implicitConvTo(tprm);
                                        }
                                    }
                                }
                                if ((p.storageClass & 2101248L) != 0)
                                {
                                    Type ta = targ;
                                    Type tp = tprm;
                                    if ((m != 0) && !arg.isLvalue())
                                    {
                                        if ((p.storageClass & 4096L) != 0)
                                        {
                                            if (pMessage != null)
                                            {
                                                pMessage.set(0, this.getParamError(arg, p));
                                            }
                                            /*goto Nomatch*/throw Dispatch1.INSTANCE;
                                        }
                                        if (((arg.op & 0xFF) == 121) && ((tp.ty & 0xFF) == ENUMTY.Tsarray))
                                        {
                                            if (((ta.ty & 0xFF) != ENUMTY.Tsarray))
                                            {
                                                Type tn = tp.nextOf().castMod(ta.nextOf().mod);
                                                long dim = (long)((StringExp)arg).len;
                                                ta = tn.sarrayOf(dim);
                                            }
                                        }
                                        else if (((arg.op & 0xFF) == 31) && ((tp.ty & 0xFF) == ENUMTY.Tsarray))
                                        {
                                            if (((ta.ty & 0xFF) != ENUMTY.Tsarray))
                                            {
                                                Type tn = ta.nextOf();
                                                long dim = ((TypeSArray)tp).dim.toUInteger();
                                                ta = tn.sarrayOf(dim);
                                            }
                                        }
                                        else if (!global.params.rvalueRefParam || ((p.storageClass & 4096L) != 0) || !isCopyable(arg.type.value))
                                        {
                                            if (pMessage != null)
                                            {
                                                pMessage.set(0, this.getParamError(arg, p));
                                            }
                                            /*goto Nomatch*/throw Dispatch1.INSTANCE;
                                        }
                                        else
                                        {
                                            m = MATCH.convert;
                                        }
                                    }
                                    for (; 1 != 0;){
                                        Type tab = ta.toBasetype();
                                        Type tat = tab.aliasthisOf();
                                        if ((tat == null) || (tat.implicitConvTo(tprm) == 0))
                                        {
                                            break;
                                        }
                                        if ((pequals(tat, tab)))
                                        {
                                            break;
                                        }
                                        ta = tat;
                                    }
                                    if (ta.constConv(tp) == 0)
                                    {
                                        if (pMessage != null)
                                        {
                                            pMessage.set(0, this.getParamError(arg, p));
                                        }
                                        /*goto Nomatch*/throw Dispatch1.INSTANCE;
                                    }
                                }
                            }
                            if ((this.parameterList.varargs == VarArg.typesafe) && (u + 1 == nparams) && (nargs > nparams))
                            {
                                /*goto L1*//*unrolled goto*/
                            /*L1:*/
                                if ((this.parameterList.varargs == VarArg.typesafe) && (u + 1 == nparams))
                                {
                                    Type tb = p.type.toBasetype();
                                    TypeSArray tsa = null;
                                    long sz = 0L;
                                    {
                                        int __dispatch16 = 0;
                                        dispatched_16:
                                        do {
                                            switch (__dispatch16 != 0 ? __dispatch16 : (tb.ty & 0xFF))
                                            {
                                                case 1:
                                                    tsa = (TypeSArray)tb;
                                                    sz = tsa.dim.toInteger();
                                                    if ((sz != (long)(nargs - u)))
                                                    {
                                                        if (pMessage != null)
                                                        {
                                                            if ((global.gag == 0) || global.params.showGaggedErrors)
                                                            {
                                                                OutBuffer buf = new OutBuffer();
                                                                buf.printf(new BytePtr("expected %d variadic argument(s)"), sz);
                                                                buf.printf(new BytePtr(", not %d"), nargs - u);
                                                                pMessage.set(0, buf.extractChars());
                                                            }
                                                        }
                                                        /*goto Nomatch*/throw Dispatch1.INSTANCE;
                                                    }
                                                    /*goto case*/{ __dispatch16 = 0; continue dispatched_16; }
                                                case 0:
                                                    __dispatch16 = 0;
                                                    TypeArray ta = (TypeArray)tb;
                                                    {
                                                        Slice<Expression> __r1539 = args.slice(u,nargs).copy();
                                                        int __key1540 = 0;
                                                    L_outer4:
                                                        for (; (__key1540 < __r1539.getLength());__key1540 += 1) {
                                                            Expression arg = __r1539.get(__key1540);
                                                            assert(arg != null);
                                                            Type tret = p.isLazyArray();
                                                            if (tret != null)
                                                            {
                                                                if (ta.next.value.equals(arg.type.value))
                                                                {
                                                                    m = MATCH.exact;
                                                                }
                                                                else if (((tret.toBasetype().ty & 0xFF) == ENUMTY.Tvoid))
                                                                {
                                                                    m = MATCH.convert;
                                                                }
                                                                else
                                                                {
                                                                    m = arg.implicitConvTo(tret);
                                                                    if ((m == MATCH.nomatch))
                                                                    {
                                                                        m = arg.implicitConvTo(ta.next.value);
                                                                    }
                                                                }
                                                            }
                                                            else
                                                            {
                                                                m = arg.implicitConvTo(ta.next.value);
                                                            }
                                                            if ((m == MATCH.nomatch))
                                                            {
                                                                if (pMessage != null)
                                                                {
                                                                    pMessage.set(0, this.getParamError(arg, p));
                                                                }
                                                                /*goto Nomatch*/throw Dispatch1.INSTANCE;
                                                            }
                                                            if ((m < match))
                                                            {
                                                                match = m;
                                                            }
                                                        }
                                                    }
                                                    /*goto Ldone*/throw Dispatch0.INSTANCE;
                                                case 7:
                                                    /*goto Ldone*/throw Dispatch0.INSTANCE;
                                                default:
                                                break;
                                            }
                                        } while(__dispatch16 != 0);
                                    }
                                }
                                if ((pMessage != null) && (u < nargs))
                                {
                                    pMessage.set(0, this.getParamError(args.get(u), p));
                                }
                                else if (pMessage != null)
                                {
                                    pMessage.set(0, this.getMatchErrorIntegerBytePtr(new BytePtr("missing argument for parameter #%d: `%s`"), u + 1, parameterToChars(p, this, false)));
                                }
                                /*goto Nomatch*/throw Dispatch1.INSTANCE;
                            }
                            if ((m == MATCH.nomatch))
                            {
                            /*L1:*/
                                if ((this.parameterList.varargs == VarArg.typesafe) && (u + 1 == nparams))
                                {
                                    Type tb = p.type.toBasetype();
                                    TypeSArray tsa = null;
                                    long sz = 0L;
                                    {
                                        int __dispatch17 = 0;
                                        dispatched_17:
                                        do {
                                            switch (__dispatch17 != 0 ? __dispatch17 : (tb.ty & 0xFF))
                                            {
                                                case 1:
                                                    tsa = (TypeSArray)tb;
                                                    sz = tsa.dim.toInteger();
                                                    if ((sz != (long)(nargs - u)))
                                                    {
                                                        if (pMessage != null)
                                                        {
                                                            if ((global.gag == 0) || global.params.showGaggedErrors)
                                                            {
                                                                OutBuffer buf = new OutBuffer();
                                                                buf.printf(new BytePtr("expected %d variadic argument(s)"), sz);
                                                                buf.printf(new BytePtr(", not %d"), nargs - u);
                                                                pMessage.set(0, buf.extractChars());
                                                            }
                                                        }
                                                        /*goto Nomatch*/throw Dispatch1.INSTANCE;
                                                    }
                                                    /*goto case*/{ __dispatch17 = 0; continue dispatched_17; }
                                                case 0:
                                                    __dispatch17 = 0;
                                                    TypeArray ta = (TypeArray)tb;
                                                    {
                                                        Slice<Expression> __r1539 = args.slice(u,nargs).copy();
                                                        int __key1540 = 0;
                                                    L_outer5:
                                                        for (; (__key1540 < __r1539.getLength());__key1540 += 1) {
                                                            Expression arg = __r1539.get(__key1540);
                                                            assert(arg != null);
                                                            Type tret = p.isLazyArray();
                                                            if (tret != null)
                                                            {
                                                                if (ta.next.value.equals(arg.type.value))
                                                                {
                                                                    m = MATCH.exact;
                                                                }
                                                                else if (((tret.toBasetype().ty & 0xFF) == ENUMTY.Tvoid))
                                                                {
                                                                    m = MATCH.convert;
                                                                }
                                                                else
                                                                {
                                                                    m = arg.implicitConvTo(tret);
                                                                    if ((m == MATCH.nomatch))
                                                                    {
                                                                        m = arg.implicitConvTo(ta.next.value);
                                                                    }
                                                                }
                                                            }
                                                            else
                                                            {
                                                                m = arg.implicitConvTo(ta.next.value);
                                                            }
                                                            if ((m == MATCH.nomatch))
                                                            {
                                                                if (pMessage != null)
                                                                {
                                                                    pMessage.set(0, this.getParamError(arg, p));
                                                                }
                                                                /*goto Nomatch*/throw Dispatch1.INSTANCE;
                                                            }
                                                            if ((m < match))
                                                            {
                                                                match = m;
                                                            }
                                                        }
                                                    }
                                                    /*goto Ldone*/throw Dispatch0.INSTANCE;
                                                case 7:
                                                    /*goto Ldone*/throw Dispatch0.INSTANCE;
                                                default:
                                                break;
                                            }
                                        } while(__dispatch17 != 0);
                                    }
                                }
                                if ((pMessage != null) && (u < nargs))
                                {
                                    pMessage.set(0, this.getParamError(args.get(u), p));
                                }
                                else if (pMessage != null)
                                {
                                    pMessage.set(0, this.getMatchErrorIntegerBytePtr(new BytePtr("missing argument for parameter #%d: `%s`"), u + 1, parameterToChars(p, this, false)));
                                }
                                /*goto Nomatch*/throw Dispatch1.INSTANCE;
                            }
                            if ((m < match))
                            {
                                match = m;
                            }
                        }
                    }
                }
                catch(Dispatch0 __d){}
            /*Ldone:*/
                if ((pMessage != null) && (this.parameterList.varargs == 0) && (nargs > nparams))
                {
                    pMessage.set(0, this.getMatchErrorIntegerInteger(new BytePtr("expected %d argument(s), not %d"), nparams, nargs));
                    /*goto Nomatch*/throw Dispatch1.INSTANCE;
                }
                return match;
            }
            catch(Dispatch1 __d){}
        /*Nomatch:*/
            return MATCH.nomatch;
        }

        // defaulted all parameters starting with #5
        public  int callMatch(Type tthis, Slice<Expression> args, int flag, Ptr<BytePtr> pMessage) {
            return callMatch(tthis, args, flag, pMessage, null);
        }

        // defaulted all parameters starting with #4
        public  int callMatch(Type tthis, Slice<Expression> args, int flag) {
            return callMatch(tthis, args, flag, null, null);
        }

        // defaulted all parameters starting with #3
        public  int callMatch(Type tthis, Slice<Expression> args) {
            return callMatch(tthis, args, 0, null, null);
        }

        public  boolean checkRetType(Loc loc) {
            Type tb = this.next.value.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tfunction))
            {
                error(loc, new BytePtr("functions cannot return a function"));
                this.next.value = Type.terror;
            }
            if (((tb.ty & 0xFF) == ENUMTY.Ttuple))
            {
                error(loc, new BytePtr("functions cannot return a tuple"));
                this.next.value = Type.terror;
            }
            if (!this.isref && ((tb.ty & 0xFF) == ENUMTY.Tstruct) || ((tb.ty & 0xFF) == ENUMTY.Tsarray))
            {
                {
                    TypeStruct ts = tb.baseElemOf().isTypeStruct();
                    if ((ts) != null)
                    {
                        if (ts.sym.members == null)
                        {
                            error(loc, new BytePtr("functions cannot return opaque type `%s` by value"), tb.toChars());
                            this.next.value = Type.terror;
                        }
                    }
                }
            }
            if (((tb.ty & 0xFF) == ENUMTY.Terror))
            {
                return true;
            }
            return false;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeFunction() {}

        public TypeFunction copy() {
            TypeFunction that = new TypeFunction();
            that.parameterList = this.parameterList;
            that.isnothrow = this.isnothrow;
            that.isnogc = this.isnogc;
            that.isproperty = this.isproperty;
            that.isref = this.isref;
            that.isreturn = this.isreturn;
            that.isscope = this.isscope;
            that.isreturninferred = this.isreturninferred;
            that.isscopeinferred = this.isscopeinferred;
            that.linkage = this.linkage;
            that.trust = this.trust;
            that.purity = this.purity;
            that.iswild = this.iswild;
            that.fargs = this.fargs;
            that.inuse = this.inuse;
            that.incomplete = this.incomplete;
            that.next = this.next;
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static class TypeDelegate extends TypeNext
    {
        public  TypeDelegate(Type t) {
            super((byte)5, t);
            this.ty = (byte)10;
        }

        public static TypeDelegate create(Type t) {
            return new TypeDelegate(t);
        }

        public  BytePtr kind() {
            return new BytePtr("delegate");
        }

        public  Type syntaxCopy() {
            Type t = this.next.value.syntaxCopy();
            if ((pequals(t, this.next.value)))
            {
                t = this;
            }
            else
            {
                t = new TypeDelegate(t);
                t.mod = this.mod;
            }
            return t;
        }

        public  Type addStorageClass(long stc) {
            TypeDelegate t = (TypeDelegate)this.addStorageClass(stc);
            if (!global.params.vsafe)
            {
                return t;
            }
            if ((stc & 524288L) != 0)
            {
                Type n = t.next.value.addStorageClass(562949953945600L);
                if ((!pequals(n, t.next.value)))
                {
                    t.next.value = n;
                    t.deco = pcopy(merge(t).deco);
                }
            }
            return t;
        }

        public  long size(Loc loc) {
            return (long)(target.ptrsize * 2);
        }

        public  int alignsize() {
            return target.ptrsize;
        }

        public  int implicitConvTo(Type to) {
            if ((pequals(this, to)))
            {
                return MATCH.exact;
            }
            if (((to.ty & 0xFF) == ENUMTY.Tdelegate) && (this.nextOf().covariant(to.nextOf(), null, true) == 1))
            {
                Type tret = this.next.value.nextOf();
                Type toret = ((TypeDelegate)to).next.value.nextOf();
                if (((tret.ty & 0xFF) == ENUMTY.Tclass) && ((toret.ty & 0xFF) == ENUMTY.Tclass))
                {
                    Ref<Integer> offset = ref(0);
                    if (toret.isBaseOf(tret, ptr(offset)) && (offset.value != 0))
                    {
                        return MATCH.nomatch;
                    }
                }
                return MATCH.convert;
            }
            return MATCH.nomatch;
        }

        public  boolean isZeroInit(Loc loc) {
            return true;
        }

        public  boolean isBoolean() {
            return true;
        }

        public  boolean hasPointers() {
            return true;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeDelegate() {}

        public TypeDelegate copy() {
            TypeDelegate that = new TypeDelegate();
            that.next = this.next;
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static class TypeTraits extends Type
    {
        public Loc loc = new Loc();
        public TraitsExp exp = null;
        public Dsymbol sym = null;
        public boolean inAliasDeclaration = false;
        public  TypeTraits(Loc loc, TraitsExp exp) {
            super((byte)44);
            this.loc.opAssign(loc.copy());
            this.exp = exp;
        }

        public  Type syntaxCopy() {
            TraitsExp te = (TraitsExp)this.exp.syntaxCopy();
            TypeTraits tt = new TypeTraits(this.loc, te);
            tt.mod = this.mod;
            return tt;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }

        public  long size(Loc loc) {
            return -1L;
        }


        public TypeTraits() {}

        public TypeTraits copy() {
            TypeTraits that = new TypeTraits();
            that.loc = this.loc;
            that.exp = this.exp;
            that.sym = this.sym;
            that.inAliasDeclaration = this.inAliasDeclaration;
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static abstract class TypeQualified extends Type
    {
        public Loc loc = new Loc();
        public DArray<RootObject> idents = new DArray<RootObject>();
        public  TypeQualified(byte ty, Loc loc) {
            super(ty);
            this.loc.opAssign(loc.copy());
        }

        public  void syntaxCopyHelper(TypeQualified t) {
            this.idents.setDim(t.idents.length);
            {
                int i = 0;
                for (; (i < this.idents.length);i++){
                    RootObject id = t.idents.get(i);
                    if ((id.dyncast() == DYNCAST.dsymbol))
                    {
                        TemplateInstance ti = (TemplateInstance)id;
                        ti = (TemplateInstance)ti.syntaxCopy(null);
                        id = ti;
                    }
                    else if ((id.dyncast() == DYNCAST.expression))
                    {
                        Expression e = (Expression)id;
                        e = e.syntaxCopy();
                        id = e;
                    }
                    else if ((id.dyncast() == DYNCAST.type))
                    {
                        Type tx = (Type)id;
                        tx = tx.syntaxCopy();
                        id = tx;
                    }
                    this.idents.set(i, id);
                }
            }
        }

        public  void addIdent(Identifier ident) {
            this.idents.push(ident);
        }

        public  void addInst(TemplateInstance inst) {
            this.idents.push(inst);
        }

        public  void addIndex(RootObject e) {
            this.idents.push(e);
        }

        public  long size(Loc loc) {
            error(this.loc, new BytePtr("size of type `%s` is not known"), this.toChars());
            return -1L;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeQualified() {}

        public abstract TypeQualified copy();
    }
    public static class TypeIdentifier extends TypeQualified
    {
        public Identifier ident = null;
        public Dsymbol originalSymbol = null;
        public  TypeIdentifier(Loc loc, Identifier ident) {
            super((byte)6, loc);
            this.ident = ident;
        }

        public  BytePtr kind() {
            return new BytePtr("identifier");
        }

        public  Type syntaxCopy() {
            TypeIdentifier t = new TypeIdentifier(this.loc, this.ident);
            t.syntaxCopyHelper(this);
            t.mod = this.mod;
            return t;
        }

        public  Dsymbol toDsymbol(Ptr<Scope> sc) {
            if (sc == null)
            {
                return null;
            }
            Ref<Type> t = ref(null);
            Ref<Expression> e = ref(null);
            Ref<Dsymbol> s = ref(null);
            resolve(this, this.loc, sc, ptr(e), ptr(t), ptr(s), false);
            if ((t.value != null) && ((t.value.ty & 0xFF) != ENUMTY.Tident))
            {
                s.value = t.value.toDsymbol(sc);
            }
            if (e.value != null)
            {
                s.value = getDsymbol(e.value);
            }
            return s.value;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeIdentifier() {}

        public TypeIdentifier copy() {
            TypeIdentifier that = new TypeIdentifier();
            that.ident = this.ident;
            that.originalSymbol = this.originalSymbol;
            that.loc = this.loc;
            that.idents = this.idents;
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static class TypeInstance extends TypeQualified
    {
        public TemplateInstance tempinst = null;
        public  TypeInstance(Loc loc, TemplateInstance tempinst) {
            super((byte)35, loc);
            this.tempinst = tempinst;
        }

        public  BytePtr kind() {
            return new BytePtr("instance");
        }

        public  Type syntaxCopy() {
            TypeInstance t = new TypeInstance(this.loc, (TemplateInstance)this.tempinst.syntaxCopy(null));
            t.syntaxCopyHelper(this);
            t.mod = this.mod;
            return t;
        }

        public  Dsymbol toDsymbol(Ptr<Scope> sc) {
            Ref<Type> t = ref(null);
            Ref<Expression> e = ref(null);
            Ref<Dsymbol> s = ref(null);
            resolve(this, this.loc, sc, ptr(e), ptr(t), ptr(s), false);
            if ((t.value != null) && ((t.value.ty & 0xFF) != ENUMTY.Tinstance))
            {
                s.value = t.value.toDsymbol(sc);
            }
            return s.value;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeInstance() {}

        public TypeInstance copy() {
            TypeInstance that = new TypeInstance();
            that.tempinst = this.tempinst;
            that.loc = this.loc;
            that.idents = this.idents;
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static class TypeTypeof extends TypeQualified
    {
        public Expression exp = null;
        public int inuse = 0;
        public  TypeTypeof(Loc loc, Expression exp) {
            super((byte)36, loc);
            this.exp = exp;
        }

        public  BytePtr kind() {
            return new BytePtr("typeof");
        }

        public  Type syntaxCopy() {
            TypeTypeof t = new TypeTypeof(this.loc, this.exp.syntaxCopy());
            t.syntaxCopyHelper(this);
            t.mod = this.mod;
            return t;
        }

        public  Dsymbol toDsymbol(Ptr<Scope> sc) {
            Ref<Expression> e = ref(null);
            Ref<Type> t = ref(null);
            Ref<Dsymbol> s = ref(null);
            resolve(this, this.loc, sc, ptr(e), ptr(t), ptr(s), false);
            return s.value;
        }

        public  long size(Loc loc) {
            if (this.exp.type.value != null)
            {
                return this.exp.type.value.size(loc);
            }
            else
            {
                return this.size(loc);
            }
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeTypeof() {}

        public TypeTypeof copy() {
            TypeTypeof that = new TypeTypeof();
            that.exp = this.exp;
            that.inuse = this.inuse;
            that.loc = this.loc;
            that.idents = this.idents;
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static class TypeReturn extends TypeQualified
    {
        public  TypeReturn(Loc loc) {
            super((byte)39, loc);
        }

        public  BytePtr kind() {
            return new BytePtr("return");
        }

        public  Type syntaxCopy() {
            TypeReturn t = new TypeReturn(this.loc);
            t.syntaxCopyHelper(this);
            t.mod = this.mod;
            return t;
        }

        public  Dsymbol toDsymbol(Ptr<Scope> sc) {
            Ref<Expression> e = ref(null);
            Ref<Type> t = ref(null);
            Ref<Dsymbol> s = ref(null);
            resolve(this, this.loc, sc, ptr(e), ptr(t), ptr(s), false);
            return s.value;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeReturn() {}

        public TypeReturn copy() {
            TypeReturn that = new TypeReturn();
            that.loc = this.loc;
            that.idents = this.idents;
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }

    public static class AliasThisRec 
    {
        public static final int no = 0;
        public static final int yes = 1;
        public static final int fwdref = 2;
        public static final int typeMask = 3;
        public static final int tracing = 4;
        public static final int tracingDT = 8;
    }

    public static class TypeStruct extends Type
    {
        public StructDeclaration sym = null;
        public Ref<Integer> att = ref(AliasThisRec.fwdref);
        public int cppmangle = CPPMANGLE.def;
        public  TypeStruct(StructDeclaration sym) {
            super((byte)8);
            this.sym = sym;
        }

        public static TypeStruct create(StructDeclaration sym) {
            return new TypeStruct(sym);
        }

        public  BytePtr kind() {
            return new BytePtr("struct");
        }

        public  long size(Loc loc) {
            return this.sym.size(loc);
        }

        public  int alignsize() {
            this.sym.size(Loc.initial);
            return this.sym.alignsize.value;
        }

        public  Type syntaxCopy() {
            return this;
        }

        public  Dsymbol toDsymbol(Ptr<Scope> sc) {
            return this.sym;
        }

        public  int alignment() {
            if ((this.sym.alignment == 0))
            {
                this.sym.size(this.sym.loc);
            }
            return this.sym.alignment;
        }

        public  Expression defaultInitLiteral(Loc loc) {
            this.sym.size(loc);
            if ((this.sym.sizeok != Sizeok.done))
            {
                return new ErrorExp();
            }
            Ptr<DArray<Expression>> structelems = refPtr(new DArray<Expression>(this.sym.nonHiddenFields()));
            int offset = 0;
            {
                int __key1541 = 0;
                int __limit1542 = (structelems.get()).length;
                for (; (__key1541 < __limit1542);__key1541 += 1) {
                    int j = __key1541;
                    VarDeclaration vd = this.sym.fields.get(j);
                    Expression e = null;
                    if (vd.inuse != 0)
                    {
                        error(loc, new BytePtr("circular reference to `%s`"), vd.toPrettyChars(false));
                        return new ErrorExp();
                    }
                    if ((vd.offset < offset) || (vd.type.size() == 0L))
                    {
                        e = null;
                    }
                    else if (vd._init != null)
                    {
                        if (vd._init.isVoidInitializer() != null)
                        {
                            e = null;
                        }
                        else
                        {
                            e = vd.getConstInitializer(false);
                        }
                    }
                    else
                    {
                        e = vd.type.defaultInitLiteral(loc);
                    }
                    if ((e != null) && ((e.op & 0xFF) == 127))
                    {
                        return e;
                    }
                    if (e != null)
                    {
                        offset = vd.offset + (int)vd.type.size();
                    }
                    structelems.get().set(j, e);
                }
            }
            StructLiteralExp structinit = new StructLiteralExp(loc, this.sym, structelems, null);
            if ((this.size(loc) > (long)(target.ptrsize * 4)) && !this.needsNested())
            {
                structinit.useStaticInit = true;
            }
            structinit.type.value = this;
            return structinit;
        }

        public  boolean isZeroInit(Loc loc) {
            return this.sym.zeroInit;
        }

        public  boolean isAssignable() {
            boolean assignable = true;
            int offset = -1;
            this.sym.determineSize(this.sym.loc);
            {
                int i = 0;
                for (; (i < this.sym.fields.length);i++){
                    VarDeclaration v = this.sym.fields.get(i);
                    if ((i == 0))
                    {
                    }
                    else if ((v.offset == offset))
                    {
                        if (assignable)
                        {
                            continue;
                        }
                    }
                    else
                    {
                        if (!assignable)
                        {
                            return false;
                        }
                    }
                    assignable = v.type.isMutable() && v.type.isAssignable();
                    offset = v.offset;
                }
            }
            return assignable;
        }

        public  boolean isBoolean() {
            return false;
        }

        public  boolean needsDestruction() {
            return this.sym.dtor != null;
        }

        public  boolean needsNested() {
            if (this.sym.isNested())
            {
                return true;
            }
            {
                int i = 0;
                for (; (i < this.sym.fields.length);i++){
                    VarDeclaration v = this.sym.fields.get(i);
                    if (!v.isDataseg() && v.type.needsNested())
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public  boolean hasPointers() {
            StructDeclaration s = this.sym;
            if ((this.sym.members != null) && !this.sym.determineFields() && (!pequals(this.sym.type, Type.terror)))
            {
                error(this.sym.loc, new BytePtr("no size because of forward references"));
            }
            {
                Slice<VarDeclaration> __r1543 = s.fields.opSlice().copy();
                int __key1544 = 0;
                for (; (__key1544 < __r1543.getLength());__key1544 += 1) {
                    VarDeclaration v = __r1543.get(__key1544);
                    if (((v.storage_class & 2097152L) != 0) || v.hasPointers())
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public  boolean hasVoidInitPointers() {
            StructDeclaration s = this.sym;
            this.sym.size(Loc.initial);
            {
                Slice<VarDeclaration> __r1545 = s.fields.opSlice().copy();
                int __key1546 = 0;
                for (; (__key1546 < __r1545.getLength());__key1546 += 1) {
                    VarDeclaration v = __r1545.get(__key1546);
                    if ((v._init != null) && (v._init.isVoidInitializer() != null) && v.type.hasPointers())
                    {
                        return true;
                    }
                    if ((v._init == null) && v.type.hasVoidInitPointers())
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public  int implicitConvToWithoutAliasThis(Type to) {
            int m = MATCH.nomatch;
            if (((this.ty & 0xFF) == (to.ty & 0xFF)) && (pequals(this.sym, ((TypeStruct)to).sym)))
            {
                m = MATCH.exact;
                if (((this.mod & 0xFF) != (to.mod & 0xFF)))
                {
                    m = MATCH.constant;
                    if (MODimplicitConv(this.mod, to.mod))
                    {
                    }
                    else
                    {
                        int offset = -1;
                        {
                            int i = 0;
                            for (; (i < this.sym.fields.length);i++){
                                VarDeclaration v = this.sym.fields.get(i);
                                if ((i == 0))
                                {
                                }
                                else if ((v.offset == offset))
                                {
                                    if ((m > MATCH.nomatch))
                                    {
                                        continue;
                                    }
                                }
                                else
                                {
                                    if ((m <= MATCH.nomatch))
                                    {
                                        return m;
                                    }
                                }
                                Type tvf = v.type.addMod(this.mod);
                                Type tv = v.type.addMod(to.mod);
                                int mf = tvf.implicitConvTo(tv);
                                if ((mf <= MATCH.nomatch))
                                {
                                    return mf;
                                }
                                if ((mf < m))
                                {
                                    m = mf;
                                }
                                offset = v.offset;
                            }
                        }
                    }
                }
            }
            return m;
        }

        public  int implicitConvToThroughAliasThis(Type to) {
            int m = MATCH.nomatch;
            if (!(((this.ty & 0xFF) == (to.ty & 0xFF)) && (pequals(this.sym, ((TypeStruct)to).sym))) && (this.sym.aliasthis != null) && ((this.att.value & AliasThisRec.tracing) == 0))
            {
                {
                    Type ato = this.aliasthisOf();
                    if ((ato) != null)
                    {
                        this.att.value = this.att.value | AliasThisRec.tracing;
                        m = ato.implicitConvTo(to);
                        this.att.value = this.att.value & -5;
                    }
                    else
                    {
                        m = MATCH.nomatch;
                    }
                }
            }
            return m;
        }

        public  int implicitConvTo(Type to) {
            int m = this.implicitConvToWithoutAliasThis(to);
            return m != 0 ? m : this.implicitConvToThroughAliasThis(to);
        }

        public  int constConv(Type to) {
            if (this.equals(to))
            {
                return MATCH.exact;
            }
            if (((this.ty & 0xFF) == (to.ty & 0xFF)) && (pequals(this.sym, ((TypeStruct)to).sym)) && MODimplicitConv(this.mod, to.mod))
            {
                return MATCH.constant;
            }
            return MATCH.nomatch;
        }

        public  byte deduceWild(Type t, boolean isRef) {
            if (((this.ty & 0xFF) == (t.ty & 0xFF)) && (pequals(this.sym, ((TypeStruct)t).sym)))
            {
                return this.deduceWild(t, isRef);
            }
            byte wm = (byte)0;
            if ((t.hasWild() != 0) && (this.sym.aliasthis != null) && ((this.att.value & AliasThisRec.tracing) == 0))
            {
                {
                    Type ato = this.aliasthisOf();
                    if ((ato) != null)
                    {
                        this.att.value = this.att.value | AliasThisRec.tracing;
                        wm = ato.deduceWild(t, isRef);
                        this.att.value = this.att.value & -5;
                    }
                }
            }
            return wm;
        }

        public  Type toHeadMutable() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeStruct() {}

        public TypeStruct copy() {
            TypeStruct that = new TypeStruct();
            that.sym = this.sym;
            that.att = this.att;
            that.cppmangle = this.cppmangle;
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static class TypeEnum extends Type
    {
        public EnumDeclaration sym = null;
        public  TypeEnum(EnumDeclaration sym) {
            super((byte)9);
            this.sym = sym;
        }

        public  BytePtr kind() {
            return new BytePtr("enum");
        }

        public  Type syntaxCopy() {
            return this;
        }

        public  long size(Loc loc) {
            return this.sym.getMemtype(loc).size(loc);
        }

        public  Type memType(Loc loc) {
            return this.sym.getMemtype(loc);
        }

        // defaulted all parameters starting with #1
        public  Type memType() {
            return memType(Loc.initial);
        }

        public  int alignsize() {
            Type t = this.memType(Loc.initial);
            if (((t.ty & 0xFF) == ENUMTY.Terror))
            {
                return 4;
            }
            return t.alignsize();
        }

        public  Dsymbol toDsymbol(Ptr<Scope> sc) {
            return this.sym;
        }

        public  boolean isintegral() {
            return this.memType(Loc.initial).isintegral();
        }

        public  boolean isfloating() {
            return this.memType(Loc.initial).isfloating();
        }

        public  boolean isreal() {
            return this.memType(Loc.initial).isreal();
        }

        public  boolean isimaginary() {
            return this.memType(Loc.initial).isimaginary();
        }

        public  boolean iscomplex() {
            return this.memType(Loc.initial).iscomplex();
        }

        public  boolean isscalar() {
            return this.memType(Loc.initial).isscalar();
        }

        public  boolean isunsigned() {
            return this.memType(Loc.initial).isunsigned();
        }

        public  boolean ischar() {
            return this.memType(Loc.initial).ischar();
        }

        public  boolean isBoolean() {
            return this.memType(Loc.initial).isBoolean();
        }

        public  boolean isString() {
            return this.memType(Loc.initial).isString();
        }

        public  boolean isAssignable() {
            return this.memType(Loc.initial).isAssignable();
        }

        public  boolean needsDestruction() {
            return this.memType(Loc.initial).needsDestruction();
        }

        public  boolean needsNested() {
            return this.memType(Loc.initial).needsNested();
        }

        public  int implicitConvTo(Type to) {
            int m = MATCH.nomatch;
            if (((this.ty & 0xFF) == (to.ty & 0xFF)) && (pequals(this.sym, ((TypeEnum)to).sym)))
            {
                m = ((this.mod & 0xFF) == (to.mod & 0xFF)) ? MATCH.exact : MATCH.constant;
            }
            else if (this.sym.getMemtype(Loc.initial).implicitConvTo(to) != 0)
            {
                m = MATCH.convert;
            }
            else
            {
                m = MATCH.nomatch;
            }
            return m;
        }

        public  int constConv(Type to) {
            if (this.equals(to))
            {
                return MATCH.exact;
            }
            if (((this.ty & 0xFF) == (to.ty & 0xFF)) && (pequals(this.sym, ((TypeEnum)to).sym)) && MODimplicitConv(this.mod, to.mod))
            {
                return MATCH.constant;
            }
            return MATCH.nomatch;
        }

        public  Type toBasetype() {
            if ((this.sym.members == null) && (this.sym.memtype == null))
            {
                return this;
            }
            Type tb = this.sym.getMemtype(Loc.initial).toBasetype();
            return tb.castMod(this.mod);
        }

        public  boolean isZeroInit(Loc loc) {
            return this.sym.getDefaultValue(loc).isBool(false);
        }

        public  boolean hasPointers() {
            return this.memType(Loc.initial).hasPointers();
        }

        public  boolean hasVoidInitPointers() {
            return this.memType(Loc.initial).hasVoidInitPointers();
        }

        public  Type nextOf() {
            return this.memType(Loc.initial).nextOf();
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeEnum() {}

        public TypeEnum copy() {
            TypeEnum that = new TypeEnum();
            that.sym = this.sym;
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static class TypeClass extends Type
    {
        public ClassDeclaration sym = null;
        public Ref<Integer> att = ref(AliasThisRec.fwdref);
        public int cppmangle = CPPMANGLE.def;
        public  TypeClass(ClassDeclaration sym) {
            super((byte)7);
            this.sym = sym;
        }

        public  BytePtr kind() {
            return new BytePtr("class");
        }

        public  long size(Loc loc) {
            return (long)target.ptrsize;
        }

        public  Type syntaxCopy() {
            return this;
        }

        public  Dsymbol toDsymbol(Ptr<Scope> sc) {
            return this.sym;
        }

        public  ClassDeclaration isClassHandle() {
            return this.sym;
        }

        public  boolean isBaseOf(Type t, Ptr<Integer> poffset) {
            if ((t != null) && ((t.ty & 0xFF) == ENUMTY.Tclass))
            {
                ClassDeclaration cd = ((TypeClass)t).sym;
                if (this.sym.isBaseOf(cd, poffset))
                {
                    return true;
                }
            }
            return false;
        }

        public  int implicitConvToWithoutAliasThis(Type to) {
            int m = this.constConv(to);
            if ((m > MATCH.nomatch))
            {
                return m;
            }
            ClassDeclaration cdto = to.isClassHandle();
            if (cdto != null)
            {
                if ((cdto.semanticRun < PASS.semanticdone) && !cdto.isBaseInfoComplete())
                {
                    dsymbolSemantic(cdto, null);
                }
                if ((this.sym.semanticRun < PASS.semanticdone) && !this.sym.isBaseInfoComplete())
                {
                    dsymbolSemantic(this.sym, null);
                }
                if (cdto.isBaseOf(this.sym, null) && MODimplicitConv(this.mod, to.mod))
                {
                    return MATCH.convert;
                }
            }
            return MATCH.nomatch;
        }

        public  int implicitConvToThroughAliasThis(Type to) {
            int m = MATCH.nomatch;
            if ((this.sym.aliasthis != null) && ((this.att.value & AliasThisRec.tracing) == 0))
            {
                {
                    Type ato = this.aliasthisOf();
                    if ((ato) != null)
                    {
                        this.att.value = this.att.value | AliasThisRec.tracing;
                        m = ato.implicitConvTo(to);
                        this.att.value = this.att.value & -5;
                    }
                }
            }
            return m;
        }

        public  int implicitConvTo(Type to) {
            int m = this.implicitConvToWithoutAliasThis(to);
            return m != 0 ? m : this.implicitConvToThroughAliasThis(to);
        }

        public  int constConv(Type to) {
            if (this.equals(to))
            {
                return MATCH.exact;
            }
            if (((this.ty & 0xFF) == (to.ty & 0xFF)) && (pequals(this.sym, ((TypeClass)to).sym)) && MODimplicitConv(this.mod, to.mod))
            {
                return MATCH.constant;
            }
            Ref<Integer> offset = ref(0);
            if (to.isBaseOf(this, ptr(offset)) && (offset.value == 0) && MODimplicitConv(this.mod, to.mod))
            {
                if (!to.isMutable() && !to.isWild())
                {
                    return MATCH.convert;
                }
            }
            return MATCH.nomatch;
        }

        public  byte deduceWild(Type t, boolean isRef) {
            ClassDeclaration cd = t.isClassHandle();
            if ((cd != null) && (pequals(this.sym, cd)) || cd.isBaseOf(this.sym, null))
            {
                return this.deduceWild(t, isRef);
            }
            byte wm = (byte)0;
            if ((t.hasWild() != 0) && (this.sym.aliasthis != null) && ((this.att.value & AliasThisRec.tracing) == 0))
            {
                {
                    Type ato = this.aliasthisOf();
                    if ((ato) != null)
                    {
                        this.att.value = this.att.value | AliasThisRec.tracing;
                        wm = ato.deduceWild(t, isRef);
                        this.att.value = this.att.value & -5;
                    }
                }
            }
            return wm;
        }

        public  Type toHeadMutable() {
            return this;
        }

        public  boolean isZeroInit(Loc loc) {
            return true;
        }

        public  boolean isscope() {
            return this.sym.stack;
        }

        public  boolean isBoolean() {
            return true;
        }

        public  boolean hasPointers() {
            return true;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeClass() {}

        public TypeClass copy() {
            TypeClass that = new TypeClass();
            that.sym = this.sym;
            that.att = this.att;
            that.cppmangle = this.cppmangle;
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static class TypeTuple extends Type
    {
        public Ptr<DArray<Parameter>> arguments = null;
        public  TypeTuple(Ptr<DArray<Parameter>> arguments) {
            super((byte)37);
            this.arguments = pcopy(arguments);
        }

        public  TypeTuple(Ptr<DArray<Expression>> exps) {
            super((byte)37);
            Ptr<DArray<Parameter>> arguments = refPtr(new DArray<Parameter>());
            if (exps != null)
            {
                (arguments.get()).setDim((exps.get()).length);
                {
                    int i = 0;
                    for (; (i < (exps.get()).length);i++){
                        Expression e = (exps.get()).get(i);
                        if (((e.type.value.ty & 0xFF) == ENUMTY.Ttuple))
                        {
                            e.error(new BytePtr("cannot form tuple of tuples"));
                        }
                        Parameter arg = new Parameter(0L, e.type.value, null, null, null);
                        arguments.get().set(i, arg);
                    }
                }
            }
            this.arguments = pcopy(arguments);
        }

        public static TypeTuple create(Ptr<DArray<Parameter>> arguments) {
            return new TypeTuple(arguments);
        }

        public  TypeTuple() {
            super((byte)37);
            this.arguments = pcopy((refPtr(new DArray<Parameter>())));
        }

        public  TypeTuple(Type t1) {
            super((byte)37);
            this.arguments = pcopy((refPtr(new DArray<Parameter>())));
            (this.arguments.get()).push(new Parameter(0L, t1, null, null, null));
        }

        public  TypeTuple(Type t1, Type t2) {
            super((byte)37);
            this.arguments = pcopy((refPtr(new DArray<Parameter>())));
            (this.arguments.get()).push(new Parameter(0L, t1, null, null, null));
            (this.arguments.get()).push(new Parameter(0L, t2, null, null, null));
        }

        public  BytePtr kind() {
            return new BytePtr("tuple");
        }

        public  Type syntaxCopy() {
            Ptr<DArray<Parameter>> args = Parameter.arraySyntaxCopy(this.arguments);
            Type t = new TypeTuple(args);
            t.mod = this.mod;
            return t;
        }

        public  boolean equals(RootObject o) {
            Type t = (Type)o;
            if ((pequals(this, t)))
            {
                return true;
            }
            {
                TypeTuple tt = t.isTypeTuple();
                if ((tt) != null)
                {
                    if (((this.arguments.get()).length == (tt.arguments.get()).length))
                    {
                        {
                            int i = 0;
                            for (; (i < (tt.arguments.get()).length);i++){
                                Parameter arg1 = (this.arguments.get()).get(i);
                                Parameter arg2 = (tt.arguments.get()).get(i);
                                if (!arg1.type.equals(arg2.type))
                                {
                                    return false;
                                }
                            }
                        }
                        return true;
                    }
                }
            }
            return false;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeTuple copy() {
            TypeTuple that = new TypeTuple();
            that.arguments = this.arguments;
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static class TypeSlice extends TypeNext
    {
        public Expression lwr = null;
        public Expression upr = null;
        public  TypeSlice(Type next, Expression lwr, Expression upr) {
            super((byte)38, next);
            this.lwr = lwr;
            this.upr = upr;
        }

        public  BytePtr kind() {
            return new BytePtr("slice");
        }

        public  Type syntaxCopy() {
            Type t = new TypeSlice(this.next.value.syntaxCopy(), this.lwr.syntaxCopy(), this.upr.syntaxCopy());
            t.mod = this.mod;
            return t;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeSlice() {}

        public TypeSlice copy() {
            TypeSlice that = new TypeSlice();
            that.lwr = this.lwr;
            that.upr = this.upr;
            that.next = this.next;
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static class TypeNull extends Type
    {
        public  TypeNull() {
            super((byte)40);
        }

        public  BytePtr kind() {
            return new BytePtr("null");
        }

        public  Type syntaxCopy() {
            return this;
        }

        public  int implicitConvTo(Type to) {
            int m = this.implicitConvTo(to);
            if ((m != MATCH.nomatch))
            {
                return m;
            }
            {
                Type tb = to.toBasetype();
                if (((tb.ty & 0xFF) == ENUMTY.Tnull) || ((tb.ty & 0xFF) == ENUMTY.Tpointer) || ((tb.ty & 0xFF) == ENUMTY.Tarray) || ((tb.ty & 0xFF) == ENUMTY.Taarray) || ((tb.ty & 0xFF) == ENUMTY.Tclass) || ((tb.ty & 0xFF) == ENUMTY.Tdelegate))
                {
                    return MATCH.constant;
                }
            }
            return MATCH.nomatch;
        }

        public  boolean hasPointers() {
            return true;
        }

        public  boolean isBoolean() {
            return true;
        }

        public  long size(Loc loc) {
            return Type.tvoidptr.size(loc);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TypeNull copy() {
            TypeNull that = new TypeNull();
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static class ParameterList
    {
        public Ptr<DArray<Parameter>> parameters = null;
        public int varargs = VarArg.none;
        public  int length() {
            return Parameter.dim(this.parameters);
        }

        public  Parameter get(int i) {
            return Parameter.getNth(this.parameters, i, null);
        }

        public ParameterList(){
        }
        public ParameterList copy(){
            ParameterList r = new ParameterList();
            r.parameters = parameters;
            r.varargs = varargs;
            return r;
        }
        public ParameterList(Ptr<DArray<Parameter>> parameters, int varargs) {
            this.parameters = parameters;
            this.varargs = varargs;
        }

        public ParameterList opAssign(ParameterList that) {
            this.parameters = that.parameters;
            this.varargs = that.varargs;
            return this;
        }
    }
    public static class Parameter extends ASTNode
    {
        public long storageClass = 0L;
        public Type type = null;
        public Identifier ident = null;
        public Expression defaultArg = null;
        public UserAttributeDeclaration userAttribDecl = null;
        public  Parameter(long storageClass, Type type, Identifier ident, Expression defaultArg, UserAttributeDeclaration userAttribDecl) {
            super();
            this.type = type;
            this.ident = ident;
            this.storageClass = storageClass;
            this.defaultArg = defaultArg;
            this.userAttribDecl = userAttribDecl;
        }

        public static Parameter create(long storageClass, Type type, Identifier ident, Expression defaultArg, UserAttributeDeclaration userAttribDecl) {
            return new Parameter(storageClass, type, ident, defaultArg, userAttribDecl);
        }

        public  Parameter syntaxCopy() {
            return new Parameter(this.storageClass, this.type != null ? this.type.syntaxCopy() : null, this.ident, this.defaultArg != null ? this.defaultArg.syntaxCopy() : null, this.userAttribDecl != null ? (UserAttributeDeclaration)this.userAttribDecl.syntaxCopy(null) : null);
        }

        public  Type isLazyArray() {
            Type tb = this.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tsarray) || ((tb.ty & 0xFF) == ENUMTY.Tarray))
            {
                Type tel = ((TypeArray)tb).next.value.toBasetype();
                {
                    TypeDelegate td = tel.isTypeDelegate();
                    if ((td) != null)
                    {
                        TypeFunction tf = td.next.value.toTypeFunction();
                        if ((tf.parameterList.varargs == VarArg.none) && (tf.parameterList.length() == 0))
                        {
                            return tf.next.value;
                        }
                    }
                }
            }
            return null;
        }

        public  int dyncast() {
            return DYNCAST.parameter;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }

        public static Ptr<DArray<Parameter>> arraySyntaxCopy(Ptr<DArray<Parameter>> parameters) {
            Ptr<DArray<Parameter>> params = null;
            if (parameters != null)
            {
                params = pcopy((refPtr(new DArray<Parameter>((parameters.get()).length))));
                {
                    int i = 0;
                    for (; (i < (params.get()).length);i++) {
                        params.get().set(i, (parameters.get()).get(i).syntaxCopy());
                    }
                }
            }
            return params;
        }

        public static int dim(Ptr<DArray<Parameter>> parameters) {
            Ref<Integer> nargs = ref(0);
            Function2<Integer,Parameter,Integer> dimDg = new Function2<Integer,Parameter,Integer>() {
                public Integer invoke(Integer n, Parameter p) {
                 {
                    nargs.value += 1;
                    return 0;
                }}

            };
            _foreach(parameters, dimDg, null);
            return nargs.value;
        }

        public static Parameter getNth(Ptr<DArray<Parameter>> parameters, int nth, Ptr<Integer> pn) {
            Ref<Parameter> param = ref(null);
            Function2<Integer,Parameter,Integer> getNthParamDg = new Function2<Integer,Parameter,Integer>() {
                public Integer invoke(Integer n, Parameter p) {
                 {
                    if ((n == nth))
                    {
                        param.value = p;
                        return 1;
                    }
                    return 0;
                }}

            };
            int res = _foreach(parameters, getNthParamDg, null);
            return res != 0 ? param.value : null;
        }

        // defaulted all parameters starting with #3
        public static Parameter getNth(Ptr<DArray<Parameter>> parameters, int nth) {
            return getNth(parameters, nth, null);
        }

        public static int _foreach(Ptr<DArray<Parameter>> parameters, Function2<Integer,Parameter,Integer> dg, Ptr<Integer> pn) {
            assert(dg != null);
            if (parameters == null)
            {
                return 0;
            }
            Ref<Integer> n = ref(pn != null ? pn.get() : 0);
            int result = 0;
            {
                int __key1547 = 0;
                int __limit1548 = (parameters.get()).length;
                for (; (__key1547 < __limit1548);__key1547 += 1) {
                    int i = __key1547;
                    Parameter p = (parameters.get()).get(i);
                    Type t = p.type.toBasetype();
                    {
                        TypeTuple tu = t.isTypeTuple();
                        if ((tu) != null)
                        {
                            result = _foreach(tu.arguments, dg, ptr(n));
                        }
                        else
                        {
                            result = dg.invoke(n.value++, p);
                        }
                    }
                    if (result != 0)
                    {
                        break;
                    }
                }
            }
            if (pn != null)
            {
                pn.set(0, n.value);
            }
            return result;
        }

        // defaulted all parameters starting with #3
        public static int _foreach(Ptr<DArray<Parameter>> parameters, Function2<Integer,Parameter,Integer> dg) {
            return _foreach(parameters, dg, null);
        }

        public  BytePtr toChars() {
            return this.ident != null ? this.ident.toChars() : new BytePtr("__anonymous_param");
        }

        public  boolean isCovariant(boolean returnByRef, Parameter p) {
            long stc = 2111488L;
            if (((this.storageClass & 2111488L) != (p.storageClass & 2111488L)))
            {
                return false;
            }
            return isCovariantScope(returnByRef, this.storageClass, p.storageClass);
        }

        public static boolean isCovariantScope(boolean returnByRef, long from, long to) {
            if ((from == to))
            {
                return true;
            }
            Function2<Boolean,Long,Integer> buildSR = new Function2<Boolean,Long,Integer>() {
                public Integer invoke(Boolean returnByRef, Long stc) {
                 {
                    Ref<Integer> result = ref(0);
                    switch ((int)stc & 17592188665856L)
                    {
                        case (int)0L:
                            result.value = 0;
                            break;
                        case (int)2097152L:
                            result.value = 3;
                            break;
                        case (int)524288L:
                            result.value = 1;
                            break;
                        case (int)17592188141568L:
                            result.value = 4;
                            break;
                        case (int)17592186568704L:
                            result.value = 2;
                            break;
                        case (int)2621440L:
                            result.value = 5;
                            break;
                        case (int)17592188665856L:
                            result.value = returnByRef ? 6 : 7;
                            break;
                        default:
                        throw SwitchError.INSTANCE;
                    }
                    return result.value;
                }}

            };
            if (((from ^ to) & 2097152L) != 0)
            {
                return false;
            }
            return covariant.get(buildSR.invoke(returnByRef, from)).get(buildSR.invoke(returnByRef, to));
        }


        public static class SR 
        {
            public static final int None = 0;
            public static final int Scope = 1;
            public static final int ReturnScope = 2;
            public static final int Ref = 3;
            public static final int ReturnRef = 4;
            public static final int RefScope = 5;
            public static final int ReturnRef_Scope = 6;
            public static final int Ref_ReturnScope = 7;
        }

        public static Slice<Slice<Boolean>> covariantInit() {
            Slice<Slice<Boolean>> covariant = new RawSlice<Slice<Boolean>>(new Slice<Boolean>[8]);
            {
                int __key695 = 0;
                int __limit696 = 8;
                for (; (__key695 < __limit696);__key695 += 1) {
                    int i = __key695;
                    covariant.get(i).set((i), true);
                    covariant.get(5).set((i), true);
                }
            }
            covariant.get(2).set(0, true);
            covariant.get(1).set(0, true);
            covariant.get(1).set(2, true);
            covariant.get(3).set(4, true);
            covariant.get(6).set(4, true);
            covariant.get(7).set(3, true);
            covariant.get(7).set(4, true);
            return covariant;
        }

        public static Slice<Slice<Boolean>> covariant = slice(new Slice<Boolean>[]{slice(new boolean[]{true, false, false, false, false, false, false, false}), slice(new boolean[]{true, true, true, false, false, false, false, false}), slice(new boolean[]{true, false, true, false, false, false, false, false}), slice(new boolean[]{false, false, false, true, true, false, false, false}), slice(new boolean[]{false, false, false, false, true, false, false, false}), slice(new boolean[]{true, true, true, true, true, true, true, true}), slice(new boolean[]{false, false, false, false, true, false, true, false}), slice(new boolean[]{false, false, false, true, true, false, false, true})});

        public Parameter() {}

        public Parameter copy() {
            Parameter that = new Parameter();
            that.storageClass = this.storageClass;
            that.type = this.type;
            that.ident = this.ident;
            that.defaultArg = this.defaultArg;
            that.userAttribDecl = this.userAttribDecl;
            return that;
        }
    }
    public static Slice<BytePtr> toAutoQualChars(Type t1, Type t2) {
        BytePtr s1 = pcopy(t1.toChars());
        BytePtr s2 = pcopy(t2.toChars());
        if (!t1.equals(t2) && (strcmp(s1, s2) == 0))
        {
            s1 = pcopy(t1.toPrettyChars(true));
            s2 = pcopy(t2.toPrettyChars(true));
        }
        return slice(new BytePtr[]{s1, s2});
    }

    public static void modifiersApply(TypeFunction tf, Function1<ByteSlice,Void> dg) {
        ByteSlice modsArr = slice(new byte[]{(byte)1, (byte)4, (byte)8, (byte)2});
        {
            ByteSlice __r1549 = modsArr.copy();
            int __key1550 = 0;
            for (; (__key1550 < __r1549.getLength());__key1550 += 1) {
                byte modsarr = __r1549.get(__key1550);
                if (((tf.mod & 0xFF) & (modsarr & 0xFF)) != 0)
                {
                    dg.invoke(MODtoString(modsarr));
                }
            }
        }
    }

    public static void attributesApply(TypeFunction tf, Function1<ByteSlice,Void> dg, int trustFormat) {
        if (tf.purity != 0)
        {
            dg.invoke(new ByteSlice("pure"));
        }
        if (tf.isnothrow)
        {
            dg.invoke(new ByteSlice("nothrow"));
        }
        if (tf.isnogc)
        {
            dg.invoke(new ByteSlice("@nogc"));
        }
        if (tf.isproperty)
        {
            dg.invoke(new ByteSlice("@property"));
        }
        if (tf.isref)
        {
            dg.invoke(new ByteSlice("ref"));
        }
        if (tf.isreturn && !tf.isreturninferred)
        {
            dg.invoke(new ByteSlice("return"));
        }
        if (tf.isscope && !tf.isscopeinferred)
        {
            dg.invoke(new ByteSlice("scope"));
        }
        int trustAttrib = tf.trust;
        if ((trustAttrib == TRUST.default_))
        {
            if ((trustFormat == TRUSTformat.TRUSTformatSystem))
            {
                trustAttrib = TRUST.system;
            }
            else
            {
                return ;
            }
        }
        dg.invoke(trustToString(trustAttrib));
    }

    // defaulted all parameters starting with #3
    public static void attributesApply(TypeFunction tf, Function1<ByteSlice,Void> dg) {
        attributesApply(tf, dg, TRUSTformat.TRUSTformatDefault);
    }

    public static AggregateDeclaration isAggregate(Type t) {
        t = t.toBasetype();
        if (((t.ty & 0xFF) == ENUMTY.Tclass))
        {
            return ((TypeClass)t).sym;
        }
        if (((t.ty & 0xFF) == ENUMTY.Tstruct))
        {
            return ((TypeStruct)t).sym;
        }
        return null;
    }

    public static boolean isIndexableNonAggregate(Type t) {
        t = t.toBasetype();
        return ((t.ty & 0xFF) == ENUMTY.Tpointer) || ((t.ty & 0xFF) == ENUMTY.Tsarray) || ((t.ty & 0xFF) == ENUMTY.Tarray) || ((t.ty & 0xFF) == ENUMTY.Taarray) || ((t.ty & 0xFF) == ENUMTY.Ttuple) || ((t.ty & 0xFF) == ENUMTY.Tvector);
    }

    public static boolean isCopyable(Type t) {
        {
            TypeStruct ts = t.isTypeStruct();
            if ((ts) != null)
            {
                if ((ts.sym.postblit != null) && ((ts.sym.postblit.storage_class & 137438953472L) != 0))
                {
                    return false;
                }
            }
        }
        return true;
    }

}
