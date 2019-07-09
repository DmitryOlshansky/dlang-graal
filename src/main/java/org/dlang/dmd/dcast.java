package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.aliasthis.*;
import static org.dlang.dmd.arrayop.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.escape.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.impcnvtab.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.intrange.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.opover.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.utf.*;
import static org.dlang.dmd.visitor.*;

public class dcast {
    private static class ImplicitCastTo extends Visitor
    {
        private Type t;
        private Scope sc;
        private Expression result;
        public  ImplicitCastTo(Scope sc, Type t) {
            this.sc = sc;
            this.t = t;
        }
        public  void visit(Expression e) {
            int match = e.implicitConvTo(this.t);
            if (match != 0)
            {
                if ((match == MATCH.constant) && (e.type.constConv(this.t) != 0) || !e.isLvalue() && e.type.equivalent(this.t))
                {
                    this.result = e.copy();
                    this.result.type = this.t;
                    return ;
                }
                AggregateDeclaration ad = isAggregate(e.type);
                if ((ad != null) && (ad.aliasthis != null))
                {
                    int adMatch = MATCH.nomatch;
                    if (((ad.type.ty & 0xFF) == ENUMTY.Tstruct))
                        adMatch = ((TypeStruct)ad.type).implicitConvToWithoutAliasThis(this.t);
                    else
                        adMatch = ((TypeClass)ad.type).implicitConvToWithoutAliasThis(this.t);
                    if (adMatch == 0)
                    {
                        Type tob = this.t.toBasetype();
                        Type t1b = e.type.toBasetype();
                        AggregateDeclaration toad = isAggregate(tob);
                        if ((!pequals(ad, toad)))
                        {
                            if (((t1b.ty & 0xFF) == ENUMTY.Tclass) && ((tob.ty & 0xFF) == ENUMTY.Tclass))
                            {
                                ClassDeclaration t1cd = t1b.isClassHandle();
                                ClassDeclaration tocd = tob.isClassHandle();
                                IntRef offset = ref(0);
                                if (tocd.isBaseOf(t1cd, ptr(offset)))
                                {
                                    this.result = new CastExp(e.loc, e, this.t);
                                    this.result.type = this.t;
                                    return ;
                                }
                            }
                            this.result = resolveAliasThis(this.sc, e, false);
                            this.result = this.result.castTo(this.sc, this.t);
                            return ;
                        }
                    }
                }
                this.result = e.castTo(this.sc, this.t);
                return ;
            }
            this.result = e.optimize(0, false);
            if ((!pequals(this.result, e)))
            {
                this.result.accept(this);
                return ;
            }
            if (((this.t.ty & 0xFF) != ENUMTY.Terror) && ((e.type.ty & 0xFF) != ENUMTY.Terror))
            {
                if (this.t.deco == null)
                {
                    e.error(new BytePtr("forward reference to type `%s`"), this.t.toChars());
                }
                else
                {
                    Slice<BytePtr> ts = toAutoQualChars(e.type, this.t);
                    e.error(new BytePtr("cannot implicitly convert expression `%s` of type `%s` to `%s`"), e.toChars(), ts.get(0), ts.get(1));
                }
            }
            this.result = new ErrorExp();
        }
        public  void visit(StringExp e) {
            this.visit((Expression)e);
            if (((this.result.op & 0xFF) == 121))
            {
                ((StringExp)this.result).committed = e.committed;
            }
        }
        public  void visit(ErrorExp e) {
            this.result = e;
        }
        public  void visit(FuncExp e) {
            Ref<FuncExp> fe = ref(null);
            if ((e.matchType(this.t, this.sc, ptr(fe), 0) > MATCH.nomatch))
            {
                this.result = fe.value;
                return ;
            }
            this.visit((Expression)e);
        }
        public  void visit(ArrayLiteralExp e) {
            this.visit((Expression)e);
            Type tb = this.result.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tarray) && global.params.useTypeInfo && (Type.dtypeinfo != null))
                semanticTypeInfo(this.sc, ((TypeDArray)tb).next);
        }
        public  void visit(SliceExp e) {
            this.visit((Expression)e);
            if (((this.result.op & 0xFF) != 31))
                return ;
            e = (SliceExp)this.result;
            if (((e.e1.op & 0xFF) == 47))
            {
                ArrayLiteralExp ale = (ArrayLiteralExp)e.e1;
                Type tb = this.t.toBasetype();
                Type tx = null;
                if (((tb.ty & 0xFF) == ENUMTY.Tsarray))
                    tx = tb.nextOf().sarrayOf(ale.elements != null ? (long)(ale.elements).length : 0L);
                else
                    tx = tb.nextOf().arrayOf();
                e.e1 = ale.implicitCastTo(this.sc, tx);
            }
        }

        public ImplicitCastTo() {}
    }
    private static class ClassCheck
    {
        public static boolean convertible(Loc loc, ClassDeclaration cd, byte mod) {
            {
                int i = 0;
                for (; (i < cd.fields.length);i++){
                    VarDeclaration v = cd.fields.get(i);
                    Initializer _init = v._init;
                    if (_init != null)
                    {
                        if (_init.isVoidInitializer() != null)
                        {
                        }
                        else {
                            ExpInitializer ei = _init.isExpInitializer();
                            if ((ei) != null)
                            {
                                Type tb = v.type.toBasetype();
                                if ((implicitMod(ei.exp, tb, mod) == MATCH.nomatch))
                                    return false;
                            }
                            else
                            {
                                return false;
                            }
                        }
                    }
                    else if (!v.type.isZeroInit(loc))
                        return false;
                }
            }
            return cd.baseClass != null ? convertible(loc, cd.baseClass, mod) : true;
        }
        public ClassCheck(){
        }
        public ClassCheck copy(){
            ClassCheck r = new ClassCheck();
            return r;
        }
        public ClassCheck opAssign(ClassCheck that) {
            return this;
        }
    }
    private static class ImplicitConvTo extends Visitor
    {
        private Type t;
        private int result = 0;
        public  ImplicitConvTo(Type t) {
            this.t = t;
            this.result = MATCH.nomatch;
        }
        public  void visit(Expression e) {
            if ((pequals(this.t, Type.terror)))
                return ;
            if (e.type == null)
            {
                e.error(new BytePtr("`%s` is not an expression"), e.toChars());
                e.type = Type.terror;
            }
            Expression ex = e.optimize(0, false);
            if (ex.type.equals(this.t))
            {
                this.result = MATCH.exact;
                return ;
            }
            if ((!pequals(ex, e)))
            {
                this.result = ex.implicitConvTo(this.t);
                return ;
            }
            int match = e.type.implicitConvTo(this.t);
            if ((match != MATCH.nomatch))
            {
                this.result = match;
                return ;
            }
            if (e.type.isintegral() && this.t.isintegral() && (e.type.isTypeBasic() != null) && (this.t.isTypeBasic() != null))
            {
                IntRange src = getIntRange(e).copy();
                IntRange target = IntRange.fromType(this.t).copy();
                if (target.contains(src))
                {
                    this.result = MATCH.convert;
                    return ;
                }
            }
        }
        public static int implicitMod(Expression e, Type t, byte mod) {
            Type tprime = null;
            if (((t.ty & 0xFF) == ENUMTY.Tpointer))
                tprime = t.nextOf().castMod(mod).pointerTo();
            else if (((t.ty & 0xFF) == ENUMTY.Tarray))
                tprime = t.nextOf().castMod(mod).arrayOf();
            else if (((t.ty & 0xFF) == ENUMTY.Tsarray))
                tprime = t.nextOf().castMod(mod).sarrayOf(t.size() / t.nextOf().size());
            else
                tprime = t.castMod(mod);
            return e.implicitConvTo(tprime);
        }
        public static int implicitConvToAddMin(BinExp e, Type t) {
            Type tb = t.toBasetype();
            Type typeb = e.type.toBasetype();
            if (((typeb.ty & 0xFF) != ENUMTY.Tpointer) || ((tb.ty & 0xFF) != ENUMTY.Tpointer))
                return MATCH.nomatch;
            Type t1b = e.e1.type.toBasetype();
            Type t2b = e.e2.type.toBasetype();
            if (((t1b.ty & 0xFF) == ENUMTY.Tpointer) && t2b.isintegral() && t1b.equivalent(tb))
            {
                int m = e.e1.implicitConvTo(t);
                return (m > MATCH.constant) ? MATCH.constant : m;
            }
            if (((t2b.ty & 0xFF) == ENUMTY.Tpointer) && t1b.isintegral() && t2b.equivalent(tb))
            {
                int m = e.e2.implicitConvTo(t);
                return (m > MATCH.constant) ? MATCH.constant : m;
            }
            return MATCH.nomatch;
        }
        public  void visit(AddExp e) {
            this.visit((Expression)e);
            if ((this.result == MATCH.nomatch))
                this.result = implicitConvToAddMin(e, this.t);
        }
        public  void visit(MinExp e) {
            this.visit((Expression)e);
            if ((this.result == MATCH.nomatch))
                this.result = implicitConvToAddMin(e, this.t);
        }
        public  void visit(IntegerExp e) {
            int m = e.type.implicitConvTo(this.t);
            if ((m >= MATCH.constant))
            {
                this.result = m;
                return ;
            }
            byte ty = e.type.toBasetype().ty;
            byte toty = this.t.toBasetype().ty;
            byte oldty = ty;
            if ((m == MATCH.nomatch) && ((this.t.ty & 0xFF) == ENUMTY.Tenum))
                return ;
            if (((this.t.ty & 0xFF) == ENUMTY.Tvector))
            {
                TypeVector tv = (TypeVector)this.t;
                TypeBasic tb = tv.elementType();
                if (((tb.ty & 0xFF) == ENUMTY.Tvoid))
                    return ;
                toty = tb.ty;
            }
            switch ((ty & 0xFF))
            {
                case 30:
                case 13:
                case 31:
                case 14:
                case 15:
                case 16:
                case 32:
                    ty = (byte)17;
                    break;
                case 33:
                    ty = (byte)18;
                    break;
                default:
                break;
            }
            long value = e.toInteger();
            // from template isLosslesslyConvertibleToFP!(Double)
            Function0<Boolean> isLosslesslyConvertibleToFPDouble = new Function0<Boolean>(){
                public Boolean invoke() {
                    if (e.type.isunsigned())
                    {
                        double f = (double)value;
                        return (long)f == value;
                    }
                    double f = (double)(long)value;
                    return (long)f == (long)value;
                }
            };

            // from template isLosslesslyConvertibleToFP!(Double)
            // removed duplicate function, [["boolean isLosslesslyConvertibleToFPDouble"]] signature: boolean isLosslesslyConvertibleToFPDouble

            // from template isLosslesslyConvertibleToFP!(Float)
            Function0<Boolean> isLosslesslyConvertibleToFPFloat = new Function0<Boolean>(){
                public Boolean invoke() {
                    if (e.type.isunsigned())
                    {
                        float f = (float)value;
                        return (long)f == value;
                    }
                    float f = (float)(long)value;
                    return (long)f == (long)value;
                }
            };

            {
                int __dispatch1 = 0;
                dispatched_1:
                do {
                    switch (__dispatch1 != 0 ? __dispatch1 : (toty & 0xFF))
                    {
                        case 30:
                            if (((value & 1L) != value))
                                return ;
                            break;
                        case 13:
                            if (((ty & 0xFF) == ENUMTY.Tuns64) && ((value & 4294967168L) != 0))
                                return ;
                            else if (((long)((byte)value & 0xFF) != value))
                                return ;
                            break;
                        case 31:
                            if (((oldty & 0xFF) == ENUMTY.Twchar) || ((oldty & 0xFF) == ENUMTY.Tdchar) && (value > 127L))
                                return ;
                            /*goto case*/{ __dispatch1 = 14; continue dispatched_1; }
                        case 14:
                            __dispatch1 = 0;
                            if (((long)((byte)value & 0xFF) != value))
                                return ;
                            break;
                        case 15:
                            if (((ty & 0xFF) == ENUMTY.Tuns64) && ((value & 4294934528L) != 0))
                                return ;
                            else if (((long)(int)(int)value != value))
                                return ;
                            break;
                        case 32:
                            if (((oldty & 0xFF) == ENUMTY.Tdchar) && (value > 55295L) && (value < 57344L))
                                return ;
                            /*goto case*/{ __dispatch1 = 16; continue dispatched_1; }
                        case 16:
                            __dispatch1 = 0;
                            if (((long)(int)(int)value != value))
                                return ;
                            break;
                        case 17:
                            if (((ty & 0xFF) == ENUMTY.Tuns32))
                            {
                            }
                            else if (((ty & 0xFF) == ENUMTY.Tuns64) && ((value & 2147483648L) != 0))
                                return ;
                            else if (((long)(int)value != value))
                                return ;
                            break;
                        case 18:
                            if (((ty & 0xFF) == ENUMTY.Tint32))
                            {
                            }
                            else if (((long)(int)value != value))
                                return ;
                            break;
                        case 33:
                            if ((value > 1114111L))
                                return ;
                            break;
                        case 21:
                            if (!isLosslesslyConvertibleToFPFloat.invoke())
                                return ;
                            break;
                        case 22:
                            if (!isLosslesslyConvertibleToFPDouble.invoke())
                                return ;
                            break;
                        case 23:
                            if (!isLosslesslyConvertibleToFPDouble.invoke())
                                return ;
                            break;
                        case 3:
                            if (((ty & 0xFF) == ENUMTY.Tpointer) && ((e.type.toBasetype().nextOf().ty & 0xFF) == (this.t.toBasetype().nextOf().ty & 0xFF)))
                            {
                                break;
                            }
                            /*goto default*/ { __dispatch1 = -3; continue dispatched_1; }
                        default:
                        __dispatch1 = 0;
                        this.visit((Expression)e);
                        return ;
                    }
                } while(__dispatch1 != 0);
            }
            this.result = MATCH.convert;
        }
        public  void visit(ErrorExp e) {
        }
        public  void visit(NullExp e) {
            if (e.type.equals(this.t))
            {
                this.result = MATCH.exact;
                return ;
            }
            if (this.t.equivalent(e.type))
            {
                this.result = MATCH.constant;
                return ;
            }
            this.visit((Expression)e);
        }
        public  void visit(StructLiteralExp e) {
            this.visit((Expression)e);
            if ((this.result != MATCH.nomatch))
                return ;
            if (((e.type.ty & 0xFF) == (this.t.ty & 0xFF)) && ((e.type.ty & 0xFF) == ENUMTY.Tstruct) && (pequals(((TypeStruct)e.type).sym, ((TypeStruct)this.t).sym)))
            {
                this.result = MATCH.constant;
                {
                    int i = 0;
                    for (; (i < (e.elements).length);i++){
                        Expression el = (e.elements).get(i);
                        if (el == null)
                            continue;
                        Type te = e.sd.fields.get(i).type.addMod(this.t.mod);
                        int m2 = el.implicitConvTo(te);
                        if ((m2 < this.result))
                            this.result = m2;
                    }
                }
            }
        }
        public  void visit(StringExp e) {
            if ((e.committed == 0) && ((this.t.ty & 0xFF) == ENUMTY.Tpointer) && ((this.t.nextOf().ty & 0xFF) == ENUMTY.Tvoid))
                return ;
            if (!(((e.type.ty & 0xFF) == ENUMTY.Tsarray) || ((e.type.ty & 0xFF) == ENUMTY.Tarray) || ((e.type.ty & 0xFF) == ENUMTY.Tpointer)))
                this.visit((Expression)e);
                return ;
            byte tyn = e.type.nextOf().ty;
            if (!(((tyn & 0xFF) == ENUMTY.Tchar) || ((tyn & 0xFF) == ENUMTY.Twchar) || ((tyn & 0xFF) == ENUMTY.Tdchar)))
                this.visit((Expression)e);
                return ;
            switch ((this.t.ty & 0xFF))
            {
                case 1:
                    if (((e.type.ty & 0xFF) == ENUMTY.Tsarray))
                    {
                        byte tynto = this.t.nextOf().ty;
                        if (((tynto & 0xFF) == (tyn & 0xFF)))
                        {
                            if ((((TypeSArray)e.type).dim.toInteger() == ((TypeSArray)this.t).dim.toInteger()))
                            {
                                this.result = MATCH.exact;
                            }
                            return ;
                        }
                        if (((tynto & 0xFF) == ENUMTY.Tchar) || ((tynto & 0xFF) == ENUMTY.Twchar) || ((tynto & 0xFF) == ENUMTY.Tdchar))
                        {
                            if ((e.committed != 0) && ((tynto & 0xFF) != (tyn & 0xFF)))
                                return ;
                            int fromlen = e.numberOfCodeUnits((tynto & 0xFF));
                            int tolen = (int)((TypeSArray)this.t).dim.toInteger();
                            if ((tolen < fromlen))
                                return ;
                            if ((tolen != fromlen))
                            {
                                this.result = MATCH.convert;
                                return ;
                            }
                        }
                        if ((e.committed == 0) && ((tynto & 0xFF) == ENUMTY.Tchar) || ((tynto & 0xFF) == ENUMTY.Twchar) || ((tynto & 0xFF) == ENUMTY.Tdchar))
                        {
                            this.result = MATCH.exact;
                            return ;
                        }
                    }
                    else if (((e.type.ty & 0xFF) == ENUMTY.Tarray))
                    {
                        byte tynto_1 = this.t.nextOf().ty;
                        if (((tynto_1 & 0xFF) == ENUMTY.Tchar) || ((tynto_1 & 0xFF) == ENUMTY.Twchar) || ((tynto_1 & 0xFF) == ENUMTY.Tdchar))
                        {
                            if ((e.committed != 0) && ((tynto_1 & 0xFF) != (tyn & 0xFF)))
                                return ;
                            int fromlen_1 = e.numberOfCodeUnits((tynto_1 & 0xFF));
                            int tolen_1 = (int)((TypeSArray)this.t).dim.toInteger();
                            if ((tolen_1 < fromlen_1))
                                return ;
                            if ((tolen_1 != fromlen_1))
                            {
                                this.result = MATCH.convert;
                                return ;
                            }
                        }
                        if (((tynto_1 & 0xFF) == (tyn & 0xFF)))
                        {
                            this.result = MATCH.exact;
                            return ;
                        }
                        if ((e.committed == 0) && ((tynto_1 & 0xFF) == ENUMTY.Tchar) || ((tynto_1 & 0xFF) == ENUMTY.Twchar) || ((tynto_1 & 0xFF) == ENUMTY.Tdchar))
                        {
                            this.result = MATCH.exact;
                            return ;
                        }
                    }
                case 0:
                case 3:
                    Type tn = this.t.nextOf();
                    int m = MATCH.exact;
                    if (((e.type.nextOf().mod & 0xFF) != (tn.mod & 0xFF)))
                    {
                        if (!tn.isConst() && !tn.isImmutable())
                            return ;
                        m = MATCH.constant;
                    }
                    if (e.committed == 0)
                    {
                        switch ((tn.ty & 0xFF))
                        {
                            case 31:
                                if (((e.postfix & 0xFF) == 119) || ((e.postfix & 0xFF) == 100))
                                    m = MATCH.convert;
                                this.result = m;
                                return ;
                            case 32:
                                if (((e.postfix & 0xFF) != 119))
                                    m = MATCH.convert;
                                this.result = m;
                                return ;
                            case 33:
                                if (((e.postfix & 0xFF) != 100))
                                    m = MATCH.convert;
                                this.result = m;
                                return ;
                            case 9:
                                if (((TypeEnum)tn).sym.isSpecial())
                                {
                                    {
                                        TypeBasic tob = tn.toBasetype().isTypeBasic();
                                        if ((tob) != null)
                                            this.result = tn.implicitConvTo(tob);
                                    }
                                    return ;
                                }
                                break;
                            default:
                            break;
                        }
                    }
                    break;
                default:
                break;
            }
            this.visit((Expression)e);
        }
        public  void visit(ArrayLiteralExp e) {
            Type tb = this.t.toBasetype();
            Type typeb = e.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tarray) || ((tb.ty & 0xFF) == ENUMTY.Tsarray) && ((typeb.ty & 0xFF) == ENUMTY.Tarray) || ((typeb.ty & 0xFF) == ENUMTY.Tsarray))
            {
                this.result = MATCH.exact;
                Type typen = typeb.nextOf().toBasetype();
                if (((tb.ty & 0xFF) == ENUMTY.Tsarray))
                {
                    TypeSArray tsa = (TypeSArray)tb;
                    if (((long)(e.elements).length != tsa.dim.toInteger()))
                        this.result = MATCH.nomatch;
                }
                Type telement = tb.nextOf();
                if ((e.elements).length == 0)
                {
                    if (((typen.ty & 0xFF) != ENUMTY.Tvoid))
                        this.result = typen.implicitConvTo(telement);
                }
                else
                {
                    if (e.basis != null)
                    {
                        int m = e.basis.implicitConvTo(telement);
                        if ((m < this.result))
                            this.result = m;
                    }
                    {
                        int i = 0;
                        for (; (i < (e.elements).length);i++){
                            Expression el = (e.elements).get(i);
                            if ((this.result == MATCH.nomatch))
                                break;
                            if (el == null)
                                continue;
                            int m = el.implicitConvTo(telement);
                            if ((m < this.result))
                                this.result = m;
                        }
                    }
                }
                if (this.result == 0)
                    this.result = e.type.implicitConvTo(this.t);
                return ;
            }
            else if (((tb.ty & 0xFF) == ENUMTY.Tvector) && ((typeb.ty & 0xFF) == ENUMTY.Tarray) || ((typeb.ty & 0xFF) == ENUMTY.Tsarray))
            {
                this.result = MATCH.exact;
                TypeVector tv = (TypeVector)tb;
                TypeSArray tbase = (TypeSArray)tv.basetype;
                assert(((tbase.ty & 0xFF) == ENUMTY.Tsarray));
                int edim = (e.elements).length;
                long tbasedim = tbase.dim.toInteger();
                if (((long)edim > tbasedim))
                {
                    this.result = MATCH.nomatch;
                    return ;
                }
                Type telement = tv.elementType();
                if (((long)edim < tbasedim))
                {
                    Expression el = typeb.nextOf().defaultInitLiteral(e.loc);
                    int m = el.implicitConvTo(telement);
                    if ((m < this.result))
                        this.result = m;
                }
                {
                    int __key899 = 0;
                    int __limit900 = edim;
                    for (; (__key899 < __limit900);__key899 += 1) {
                        int i = __key899;
                        Expression el = (e.elements).get(i);
                        int m = el.implicitConvTo(telement);
                        if ((m < this.result))
                            this.result = m;
                        if ((this.result == MATCH.nomatch))
                            break;
                    }
                }
                return ;
            }
            this.visit((Expression)e);
        }
        public  void visit(AssocArrayLiteralExp e) {
            Type tb = this.t.toBasetype();
            Type typeb = e.type.toBasetype();
            if (!(((tb.ty & 0xFF) == ENUMTY.Taarray) && ((typeb.ty & 0xFF) == ENUMTY.Taarray)))
                this.visit((Expression)e);
                return ;
            this.result = MATCH.exact;
            {
                int i = 0;
                for (; (i < (e.keys).length);i++){
                    Expression el = (e.keys).get(i);
                    int m = el.implicitConvTo(((TypeAArray)tb).index);
                    if ((m < this.result))
                        this.result = m;
                    if ((this.result == MATCH.nomatch))
                        break;
                    el = (e.values).get(i);
                    m = el.implicitConvTo(tb.nextOf());
                    if ((m < this.result))
                        this.result = m;
                    if ((this.result == MATCH.nomatch))
                        break;
                }
            }
        }
        public  void visit(CallExp e) {
            boolean LOG = false;
            this.visit((Expression)e);
            if ((this.result != MATCH.nomatch))
                return ;
            if ((e.f != null) && e.f.isReturnIsolated() && !global.params.vsafe || (e.f.isPure() >= PURE.strong) || (pequals(e.f.ident, Id.dup)) && (pequals(e.f.toParent2(), ClassDeclaration.object.toParent())))
            {
                this.result = e.type.immutableOf().implicitConvTo(this.t);
                if ((this.result > MATCH.constant))
                    this.result = MATCH.constant;
                return ;
            }
            Type tx = e.f != null ? e.f.type : e.e1.type;
            tx = tx.toBasetype();
            if (((tx.ty & 0xFF) != ENUMTY.Tfunction))
                return ;
            TypeFunction tf = (TypeFunction)tx;
            if ((tf.purity == PURE.impure))
                return ;
            if ((e.f != null) && e.f.isNested())
                return ;
            if ((e.type.immutableOf().implicitConvTo(this.t) < MATCH.constant) && (e.type.addMod((byte)2).implicitConvTo(this.t) < MATCH.constant) && (e.type.implicitConvTo(this.t.addMod((byte)2)) < MATCH.constant))
            {
                return ;
            }
            Type tb = this.t.toBasetype();
            byte mod = tb.mod;
            if (tf.isref)
            {
            }
            else
            {
                Type ti = getIndirection(this.t);
                if (ti != null)
                    mod = ti.mod;
            }
            if (((mod & 0xFF) & MODFlags.wild) != 0)
                return ;
            int nparams = tf.parameterList.length();
            int j = (((tf.linkage == LINK.d) && (tf.parameterList.varargs == VarArg.variadic)) ? 1 : 0);
            if (((e.e1.op & 0xFF) == 27))
            {
                DotVarExp dve = (DotVarExp)e.e1;
                Type targ = dve.e1.type;
                if ((targ.constConv(targ.castMod(mod)) == MATCH.nomatch))
                    return ;
            }
            {
                int i = j;
                for (; (i < (e.arguments).length);i += 1){
                    Expression earg = (e.arguments).get(i);
                    Type targ = earg.type.toBasetype();
                    if ((i - j < nparams))
                    {
                        Parameter fparam = tf.parameterList.get(i - j);
                        if ((fparam.storageClass & 8192L) != 0)
                            return ;
                        Type tparam = fparam.type;
                        if (tparam == null)
                            continue;
                        if ((fparam.storageClass & 2101248L) != 0)
                        {
                            if ((targ.constConv(tparam.castMod(mod)) == MATCH.nomatch))
                                return ;
                            continue;
                        }
                    }
                    if ((implicitMod(earg, targ, mod) == MATCH.nomatch))
                        return ;
                }
            }
            this.result = MATCH.constant;
        }
        public  void visit(AddrExp e) {
            this.result = e.type.implicitConvTo(this.t);
            if ((this.result != MATCH.nomatch))
                return ;
            Type tb = this.t.toBasetype();
            Type typeb = e.type.toBasetype();
            if (((e.e1.op & 0xFF) == 214) && ((tb.ty & 0xFF) == ENUMTY.Tpointer) || ((tb.ty & 0xFF) == ENUMTY.Tdelegate) && ((tb.nextOf().ty & 0xFF) == ENUMTY.Tfunction))
            {
                OverExp eo = (OverExp)e.e1;
                FuncDeclaration f = null;
                {
                    int i = 0;
                    for (; (i < eo.vars.a.length);i++){
                        Dsymbol s = eo.vars.a.get(i);
                        FuncDeclaration f2 = s.isFuncDeclaration();
                        assert(f2 != null);
                        if (f2.overloadExactMatch(tb.nextOf()) != null)
                        {
                            if (f != null)
                            {
                                ScopeDsymbol.multiplyDefined(e.loc, f, f2);
                            }
                            else
                                f = f2;
                            this.result = MATCH.exact;
                        }
                    }
                }
            }
            if (((e.e1.op & 0xFF) == 26) && ((typeb.ty & 0xFF) == ENUMTY.Tpointer) && ((typeb.nextOf().ty & 0xFF) == ENUMTY.Tfunction) && ((tb.ty & 0xFF) == ENUMTY.Tpointer) && ((tb.nextOf().ty & 0xFF) == ENUMTY.Tfunction))
            {
                throw new AssertionError("Unreachable code!");
            }
        }
        public  void visit(SymOffExp e) {
            this.result = e.type.implicitConvTo(this.t);
            if ((this.result != MATCH.nomatch))
                return ;
            Type tb = this.t.toBasetype();
            Type typeb = e.type.toBasetype();
            if (((typeb.ty & 0xFF) == ENUMTY.Tpointer) && ((typeb.nextOf().ty & 0xFF) == ENUMTY.Tfunction) && ((tb.ty & 0xFF) == ENUMTY.Tpointer) || ((tb.ty & 0xFF) == ENUMTY.Tdelegate) && ((tb.nextOf().ty & 0xFF) == ENUMTY.Tfunction))
            {
                {
                    FuncDeclaration f = e.var.isFuncDeclaration();
                    if ((f) != null)
                    {
                        f = f.overloadExactMatch(tb.nextOf());
                        if (f != null)
                        {
                            if (((tb.ty & 0xFF) == ENUMTY.Tdelegate) && f.needThis() || f.isNested() || ((tb.ty & 0xFF) == ENUMTY.Tpointer) && !(f.needThis() || f.isNested()))
                            {
                                this.result = MATCH.exact;
                            }
                        }
                    }
                }
            }
        }
        public  void visit(DelegateExp e) {
            this.result = e.type.implicitConvTo(this.t);
            if ((this.result != MATCH.nomatch))
                return ;
            Type tb = this.t.toBasetype();
            Type typeb = e.type.toBasetype();
            if (((typeb.ty & 0xFF) == ENUMTY.Tdelegate) && ((tb.ty & 0xFF) == ENUMTY.Tdelegate))
            {
                if ((e.func != null) && (e.func.overloadExactMatch(tb.nextOf()) != null))
                    this.result = MATCH.exact;
            }
        }
        public  void visit(FuncExp e) {
            int m = e.matchType(this.t, null, null, 1);
            if ((m > MATCH.nomatch))
            {
                this.result = m;
                return ;
            }
            this.visit((Expression)e);
        }
        public  void visit(AndExp e) {
            this.visit((Expression)e);
            if ((this.result != MATCH.nomatch))
                return ;
            int m1 = e.e1.implicitConvTo(this.t);
            int m2 = e.e2.implicitConvTo(this.t);
            this.result = (m1 < m2) ? m1 : m2;
        }
        public  void visit(OrExp e) {
            this.visit((Expression)e);
            if ((this.result != MATCH.nomatch))
                return ;
            int m1 = e.e1.implicitConvTo(this.t);
            int m2 = e.e2.implicitConvTo(this.t);
            this.result = (m1 < m2) ? m1 : m2;
        }
        public  void visit(XorExp e) {
            this.visit((Expression)e);
            if ((this.result != MATCH.nomatch))
                return ;
            int m1 = e.e1.implicitConvTo(this.t);
            int m2 = e.e2.implicitConvTo(this.t);
            this.result = (m1 < m2) ? m1 : m2;
        }
        public  void visit(CondExp e) {
            int m1 = e.e1.implicitConvTo(this.t);
            int m2 = e.e2.implicitConvTo(this.t);
            this.result = (m1 < m2) ? m1 : m2;
        }
        public  void visit(CommaExp e) {
            e.e2.accept(this);
        }
        public  void visit(CastExp e) {
            this.result = e.type.implicitConvTo(this.t);
            if ((this.result != MATCH.nomatch))
                return ;
            if (this.t.isintegral() && e.e1.type.isintegral() && (e.e1.implicitConvTo(this.t) != MATCH.nomatch))
                this.result = MATCH.convert;
            else
                this.visit((Expression)e);
        }
        public  void visit(NewExp e) {
            this.visit((Expression)e);
            if ((this.result != MATCH.nomatch))
                return ;
            if ((e.type.immutableOf().implicitConvTo(this.t.immutableOf()) == MATCH.nomatch))
                return ;
            Type tb = this.t.toBasetype();
            byte mod = tb.mod;
            {
                Type ti = getIndirection(this.t);
                if ((ti) != null)
                    mod = ti.mod;
            }
            if (((mod & 0xFF) & MODFlags.wild) != 0)
                return ;
            if (e.thisexp != null)
            {
                Type targ = e.thisexp.type;
                if ((targ.constConv(targ.castMod(mod)) == MATCH.nomatch))
                    return ;
            }
            FuncDeclaration fd = e.allocator;
            {
                int count = 0;
                for (; (count < 2);comma(count += 1, fd = e.member)){
                    if (fd == null)
                        continue;
                    if (fd.errors || ((fd.type.ty & 0xFF) != ENUMTY.Tfunction))
                        return ;
                    TypeFunction tf = (TypeFunction)fd.type;
                    if ((tf.purity == PURE.impure))
                        return ;
                    if ((pequals(fd, e.member)))
                    {
                        if ((e.type.immutableOf().implicitConvTo(this.t) < MATCH.constant) && (e.type.addMod((byte)2).implicitConvTo(this.t) < MATCH.constant) && (e.type.implicitConvTo(this.t.addMod((byte)2)) < MATCH.constant))
                        {
                            return ;
                        }
                    }
                    DArray<Expression> args = (pequals(fd, e.allocator)) ? e.newargs : e.arguments;
                    int nparams = tf.parameterList.length();
                    int j = (((tf.linkage == LINK.d) && (tf.parameterList.varargs == VarArg.variadic)) ? 1 : 0);
                    {
                        int i = j;
                        for (; (i < (e.arguments).length);i += 1){
                            Expression earg = (args).get(i);
                            Type targ = earg.type.toBasetype();
                            if ((i - j < nparams))
                            {
                                Parameter fparam = tf.parameterList.get(i - j);
                                if ((fparam.storageClass & 8192L) != 0)
                                    return ;
                                Type tparam = fparam.type;
                                if (tparam == null)
                                    continue;
                                if ((fparam.storageClass & 2101248L) != 0)
                                {
                                    if ((targ.constConv(tparam.castMod(mod)) == MATCH.nomatch))
                                        return ;
                                    continue;
                                }
                            }
                            if ((implicitMod(earg, targ, mod) == MATCH.nomatch))
                                return ;
                        }
                    }
                }
            }
            if ((e.member == null) && (e.arguments != null))
            {
                {
                    int i = 0;
                    for (; (i < (e.arguments).length);i += 1){
                        Expression earg = (e.arguments).get(i);
                        if (earg == null)
                            continue;
                        Type targ = earg.type.toBasetype();
                        if ((implicitMod(earg, targ, mod) == MATCH.nomatch))
                            return ;
                    }
                }
            }
            Type ntb = e.newtype.toBasetype();
            if (((ntb.ty & 0xFF) == ENUMTY.Tarray))
                ntb = ntb.nextOf().toBasetype();
            if (((ntb.ty & 0xFF) == ENUMTY.Tstruct))
            {
                StructDeclaration sd = ((TypeStruct)ntb).sym;
                sd.size(e.loc);
                if (sd.isNested())
                    return ;
            }
            if (ntb.isZeroInit(e.loc))
            {
                if (((ntb.ty & 0xFF) == ENUMTY.Tclass))
                {
                    ClassDeclaration cd = ((TypeClass)ntb).sym;
                    cd.size(e.loc);
                    if (cd.isNested())
                        return ;
                    assert(cd.isInterfaceDeclaration() == null);
                    if (!ClassCheck.convertible(e.loc, cd, mod))
                        return ;
                }
            }
            else
            {
                Expression earg = e.newtype.defaultInitLiteral(e.loc);
                Type targ = e.newtype.toBasetype();
                if ((implicitMod(earg, targ, mod) == MATCH.nomatch))
                    return ;
            }
            this.result = MATCH.constant;
        }
        public  void visit(SliceExp e) {
            this.visit((Expression)e);
            if ((this.result != MATCH.nomatch))
                return ;
            Type tb = this.t.toBasetype();
            Type typeb = e.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tsarray) && ((typeb.ty & 0xFF) == ENUMTY.Tarray))
            {
                typeb = toStaticArrayType(e);
                if (typeb != null)
                    this.result = typeb.implicitConvTo(this.t);
                return ;
            }
            Type t1b = e.e1.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tarray) && typeb.equivalent(tb))
            {
                Type tbn = tb.nextOf();
                Type tx = null;
                if (((t1b.ty & 0xFF) == ENUMTY.Tarray))
                    tx = tbn.arrayOf();
                if (((t1b.ty & 0xFF) == ENUMTY.Tpointer))
                    tx = tbn.pointerTo();
                if (((t1b.ty & 0xFF) == ENUMTY.Tsarray) && !e.e1.isLvalue())
                    tx = tbn.sarrayOf(t1b.size() / tbn.size());
                if (tx != null)
                {
                    this.result = e.e1.implicitConvTo(tx);
                    if ((this.result > MATCH.constant))
                        this.result = MATCH.constant;
                }
            }
            if (((tb.ty & 0xFF) == ENUMTY.Tpointer) && ((e.e1.op & 0xFF) == 121))
                e.e1.accept(this);
        }

        public ImplicitConvTo() {}
    }
    static BytePtr visitmsg = new BytePtr("cannot form delegate due to covariant return type");
    private static class CastTo extends Visitor
    {
        private Type t;
        private Scope sc;
        private Expression result;
        public  CastTo(Scope sc, Type t) {
            this.sc = sc;
            this.t = t;
        }
        public  void visit(Expression e) {
            if (e.type.equals(this.t))
            {
                this.result = e;
                return ;
            }
            if (((e.op & 0xFF) == 26))
            {
                VarDeclaration v = ((VarExp)e).var.isVarDeclaration();
                if ((v != null) && ((v.storage_class & 8388608L) != 0))
                {
                    this.result = e.ctfeInterpret();
                    this.result.loc = e.loc.copy();
                    this.result = this.result.castTo(this.sc, this.t);
                    return ;
                }
            }
            Type tob = this.t.toBasetype();
            Type t1b = e.type.toBasetype();
            if (tob.equals(t1b))
            {
                this.result = e.copy();
                this.result.type = this.t;
                return ;
            }
            boolean tob_isFV = ((tob.ty & 0xFF) == ENUMTY.Tstruct) || ((tob.ty & 0xFF) == ENUMTY.Tsarray);
            boolean t1b_isFV = ((t1b.ty & 0xFF) == ENUMTY.Tstruct) || ((t1b.ty & 0xFF) == ENUMTY.Tsarray);
            boolean tob_isFR = ((tob.ty & 0xFF) == ENUMTY.Tarray) || ((tob.ty & 0xFF) == ENUMTY.Tdelegate);
            boolean t1b_isFR = ((t1b.ty & 0xFF) == ENUMTY.Tarray) || ((t1b.ty & 0xFF) == ENUMTY.Tdelegate);
            boolean tob_isR = tob_isFR || ((tob.ty & 0xFF) == ENUMTY.Tpointer) || ((tob.ty & 0xFF) == ENUMTY.Taarray) || ((tob.ty & 0xFF) == ENUMTY.Tclass);
            boolean t1b_isR = t1b_isFR || ((t1b.ty & 0xFF) == ENUMTY.Tpointer) || ((t1b.ty & 0xFF) == ENUMTY.Taarray) || ((t1b.ty & 0xFF) == ENUMTY.Tclass);
            boolean tob_isA = tob.isintegral() || tob.isfloating();
            boolean t1b_isA = t1b.isintegral() || t1b.isfloating();
            boolean hasAliasThis = false;
            try {
                {
                    AggregateDeclaration t1ad = isAggregate(t1b);
                    if ((t1ad) != null)
                    {
                        AggregateDeclaration toad = isAggregate(tob);
                        if ((!pequals(t1ad, toad)) && (t1ad.aliasthis != null))
                        {
                            if (((t1b.ty & 0xFF) == ENUMTY.Tclass) && ((tob.ty & 0xFF) == ENUMTY.Tclass))
                            {
                                ClassDeclaration t1cd = t1b.isClassHandle();
                                ClassDeclaration tocd = tob.isClassHandle();
                                IntRef offset = ref(0);
                                if (tocd.isBaseOf(t1cd, ptr(offset)))
                                    /*goto Lok*/throw Dispatch0.INSTANCE;
                            }
                            hasAliasThis = true;
                        }
                    }
                    else if (((tob.ty & 0xFF) == ENUMTY.Tvector) && ((t1b.ty & 0xFF) != ENUMTY.Tvector))
                    {
                        TypeVector tv = (TypeVector)tob;
                        this.result = new CastExp(e.loc, e, tv.elementType());
                        this.result = new VectorExp(e.loc, this.result, tob);
                        this.result = expressionSemantic(this.result, this.sc);
                        return ;
                    }
                    else if (((tob.ty & 0xFF) != ENUMTY.Tvector) && ((t1b.ty & 0xFF) == ENUMTY.Tvector))
                    {
                        if (((tob.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            if ((t1b.size(e.loc) == tob.size(e.loc)))
                                /*goto Lok*/throw Dispatch0.INSTANCE;
                        }
                        /*goto Lfail*//*unrolled goto*/
                    /*Lfail:*/
                        if (hasAliasThis)
                        {
                            this.result = tryAliasThisCast(e, this.sc, tob, t1b, this.t);
                            if (this.result != null)
                                return ;
                        }
                        e.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e.toChars(), e.type.toChars(), this.t.toChars());
                        this.result = new ErrorExp();
                        return ;
                    }
                    else if ((t1b.implicitConvTo(tob) == MATCH.constant) && this.t.equals(e.type.constOf()))
                    {
                        this.result = e.copy();
                        this.result.type = this.t;
                        return ;
                    }
                }
                if (tob_isA && t1b_isA || ((t1b.ty & 0xFF) == ENUMTY.Tpointer) || t1b_isA && tob_isA || ((tob.ty & 0xFF) == ENUMTY.Tpointer))
                {
                    /*goto Lok*/throw Dispatch0.INSTANCE;
                }
                if (tob_isA && t1b_isR || t1b_isFV || t1b_isA && tob_isR || tob_isFV)
                {
                    /*goto Lfail*//*unrolled goto*/
                /*Lfail:*/
                    if (hasAliasThis)
                    {
                        this.result = tryAliasThisCast(e, this.sc, tob, t1b, this.t);
                        if (this.result != null)
                            return ;
                    }
                    e.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e.toChars(), e.type.toChars(), this.t.toChars());
                    this.result = new ErrorExp();
                    return ;
                }
                if (tob_isFV && t1b_isFV)
                {
                    if (hasAliasThis)
                    {
                        this.result = tryAliasThisCast(e, this.sc, tob, t1b, this.t);
                        if (this.result != null)
                            return ;
                    }
                    if ((t1b.size(e.loc) == tob.size(e.loc)))
                        /*goto Lok*/throw Dispatch0.INSTANCE;
                    Slice<BytePtr> ts = toAutoQualChars(e.type, this.t);
                    e.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s` because of different sizes"), e.toChars(), ts.get(0), ts.get(1));
                    this.result = new ErrorExp();
                    return ;
                }
                if (tob_isFV && ((t1b.ty & 0xFF) == ENUMTY.Tnull) || t1b_isR || t1b_isFV && ((tob.ty & 0xFF) == ENUMTY.Tnull) || tob_isR)
                {
                    if (((tob.ty & 0xFF) == ENUMTY.Tpointer) && ((t1b.ty & 0xFF) == ENUMTY.Tsarray))
                    {
                        this.result = new AddrExp(e.loc, e, this.t);
                        return ;
                    }
                    if (((tob.ty & 0xFF) == ENUMTY.Tarray) && ((t1b.ty & 0xFF) == ENUMTY.Tsarray))
                    {
                        long fsize = t1b.nextOf().size();
                        long tsize = tob.nextOf().size();
                        if ((((TypeSArray)t1b).dim.toInteger() * fsize % tsize != 0L))
                        {
                            e.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s` since sizes don't line up"), e.toChars(), e.type.toChars(), this.t.toChars());
                            this.result = new ErrorExp();
                            return ;
                        }
                        /*goto Lok*/throw Dispatch0.INSTANCE;
                    }
                    /*goto Lfail*//*unrolled goto*/
                /*Lfail:*/
                    if (hasAliasThis)
                    {
                        this.result = tryAliasThisCast(e, this.sc, tob, t1b, this.t);
                        if (this.result != null)
                            return ;
                    }
                    e.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e.toChars(), e.type.toChars(), this.t.toChars());
                    this.result = new ErrorExp();
                    return ;
                }
                if (((tob.ty & 0xFF) == (t1b.ty & 0xFF)) && tob_isR && t1b_isR)
                    /*goto Lok*/throw Dispatch0.INSTANCE;
                if (((tob.ty & 0xFF) == ENUMTY.Tnull) && ((t1b.ty & 0xFF) != ENUMTY.Tnull))
                    /*goto Lfail*//*unrolled goto*/
                /*Lfail:*/
                    if (hasAliasThis)
                    {
                        this.result = tryAliasThisCast(e, this.sc, tob, t1b, this.t);
                        if (this.result != null)
                            return ;
                    }
                    e.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e.toChars(), e.type.toChars(), this.t.toChars());
                    this.result = new ErrorExp();
                    return ;
                if (((t1b.ty & 0xFF) == ENUMTY.Tnull) && ((tob.ty & 0xFF) != ENUMTY.Tnull))
                    /*goto Lok*/throw Dispatch0.INSTANCE;
                if (tob_isFR && t1b_isR || t1b_isFR && tob_isR)
                {
                    if (((tob.ty & 0xFF) == ENUMTY.Tpointer) && ((t1b.ty & 0xFF) == ENUMTY.Tarray))
                    {
                        /*goto Lok*/throw Dispatch0.INSTANCE;
                    }
                    if (((tob.ty & 0xFF) == ENUMTY.Tpointer) && ((t1b.ty & 0xFF) == ENUMTY.Tdelegate))
                    {
                        e.deprecation(new BytePtr("casting from %s to %s is deprecated"), e.type.toChars(), this.t.toChars());
                        /*goto Lok*/throw Dispatch0.INSTANCE;
                    }
                    /*goto Lfail*//*unrolled goto*/
                /*Lfail:*/
                    if (hasAliasThis)
                    {
                        this.result = tryAliasThisCast(e, this.sc, tob, t1b, this.t);
                        if (this.result != null)
                            return ;
                    }
                    e.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e.toChars(), e.type.toChars(), this.t.toChars());
                    this.result = new ErrorExp();
                    return ;
                }
                if (((t1b.ty & 0xFF) == ENUMTY.Tvoid) && ((tob.ty & 0xFF) != ENUMTY.Tvoid))
                {
                /*Lfail:*/
                    if (hasAliasThis)
                    {
                        this.result = tryAliasThisCast(e, this.sc, tob, t1b, this.t);
                        if (this.result != null)
                            return ;
                    }
                    e.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e.toChars(), e.type.toChars(), this.t.toChars());
                    this.result = new ErrorExp();
                    return ;
                }
            }
            catch(Dispatch0 __d){}
        /*Lok:*/
            this.result = new CastExp(e.loc, e, this.t);
            this.result.type = this.t;
        }
        public  void visit(ErrorExp e) {
            this.result = e;
        }
        public  void visit(RealExp e) {
            if (!e.type.equals(this.t))
            {
                if (e.type.isreal() && this.t.isreal() || e.type.isimaginary() && this.t.isimaginary())
                {
                    this.result = e.copy();
                    this.result.type = this.t;
                }
                else
                    this.visit((Expression)e);
                return ;
            }
            this.result = e;
        }
        public  void visit(ComplexExp e) {
            if (!e.type.equals(this.t))
            {
                if (e.type.iscomplex() && this.t.iscomplex())
                {
                    this.result = e.copy();
                    this.result.type = this.t;
                }
                else
                    this.visit((Expression)e);
                return ;
            }
            this.result = e;
        }
        public  void visit(NullExp e) {
            this.visit((Expression)e);
            if (((this.result.op & 0xFF) == 13))
            {
                NullExp ex = (NullExp)this.result;
                ex.committed = (byte)1;
                return ;
            }
        }
        public  void visit(StructLiteralExp e) {
            this.visit((Expression)e);
            if (((this.result.op & 0xFF) == 49))
                ((StructLiteralExp)this.result).stype = this.t;
        }
        public  void visit(StringExp e) {
            int copied = 0;
            if ((e.committed == 0) && ((this.t.ty & 0xFF) == ENUMTY.Tpointer) && ((this.t.nextOf().ty & 0xFF) == ENUMTY.Tvoid))
            {
                e.error(new BytePtr("cannot convert string literal to `void*`"));
                this.result = new ErrorExp();
                return ;
            }
            StringExp se = e;
            if (e.committed == 0)
            {
                se = (StringExp)e.copy();
                se.committed = (byte)1;
                copied = 1;
            }
            if (e.type.equals(this.t))
            {
                this.result = se;
                return ;
            }
            Type tb = this.t.toBasetype();
            Type typeb = e.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tdelegate) && ((typeb.ty & 0xFF) != ENUMTY.Tdelegate))
            {
                this.visit((Expression)e);
                return ;
            }
            if (typeb.equals(tb))
            {
                if (copied == 0)
                {
                    se = (StringExp)e.copy();
                    copied = 1;
                }
                se.type = this.t;
                this.result = se;
                return ;
            }
            if ((e.committed != 0) && ((tb.ty & 0xFF) == ENUMTY.Tsarray) && ((typeb.ty & 0xFF) == ENUMTY.Tarray))
            {
                se = (StringExp)e.copy();
                long szx = tb.nextOf().size();
                assert((szx <= 255L));
                se.sz = (byte)szx;
                se.len = (int)((TypeSArray)tb).dim.toInteger();
                se.committed = (byte)1;
                se.type = this.t;
                int fullSize = (se.len + 1) * (se.sz & 0xFF);
                if ((fullSize > (e.len + 1) * (e.sz & 0xFF)))
                {
                    Object s = pcopy(Mem.xmalloc(fullSize));
                    int srcSize = e.len * (e.sz & 0xFF);
                    memcpy((BytePtr)s, (se.string), srcSize);
                    memset(((BytePtr)s).plus(srcSize), 0, fullSize - srcSize);
                    se.string = pcopy((((BytePtr)s)));
                }
                this.result = se;
                return ;
            }
            try {
                try {
                    if (((tb.ty & 0xFF) != ENUMTY.Tsarray) && ((tb.ty & 0xFF) != ENUMTY.Tarray) && ((tb.ty & 0xFF) != ENUMTY.Tpointer))
                    {
                        if (copied == 0)
                        {
                            se = (StringExp)e.copy();
                            copied = 1;
                        }
                        /*goto Lcast*/throw Dispatch1.INSTANCE;
                    }
                    if (((typeb.ty & 0xFF) != ENUMTY.Tsarray) && ((typeb.ty & 0xFF) != ENUMTY.Tarray) && ((typeb.ty & 0xFF) != ENUMTY.Tpointer))
                    {
                        if (copied == 0)
                        {
                            se = (StringExp)e.copy();
                            copied = 1;
                        }
                        /*goto Lcast*/throw Dispatch1.INSTANCE;
                    }
                    if ((typeb.nextOf().size() == tb.nextOf().size()))
                    {
                        if (copied == 0)
                        {
                            se = (StringExp)e.copy();
                            copied = 1;
                        }
                        if (((tb.ty & 0xFF) == ENUMTY.Tsarray))
                            /*goto L2*/throw Dispatch0.INSTANCE;
                        se.type = this.t;
                        this.result = se;
                        return ;
                    }
                    if (e.committed != 0)
                        /*goto Lcast*/throw Dispatch1.INSTANCE;
                    // from template X!(IntegerInteger)
                    Function2<Integer,Integer,Integer> XIntegerInteger = new Function2<Integer,Integer,Integer>(){
                        public Integer invoke(Integer tf, Integer tt) {
                            return tf * 256 + tt;
                        }
                    };

                    // from template X!(IntegerInteger)
                    // removed duplicate function, [["int Xint, intIntegerInteger"]] signature: int Xint, intIntegerInteger

                    {
                        OutBuffer buffer = new OutBuffer();
                        try {
                            int newlen = 0;
                            int tfty = (typeb.nextOf().toBasetype().ty & 0xFF);
                            int ttty = (tb.nextOf().toBasetype().ty & 0xFF);
                            {
                                int __dispatch4 = 0;
                                dispatched_4:
                                do {
                                    switch (__dispatch4 != 0 ? __dispatch4 : XIntegerInteger.invoke(tfty, ttty))
                                    {
                                        case 7967:
                                        case 8224:
                                        case 8481:
                                            break;
                                        case 7968:
                                            {
                                                IntRef u = ref(0);
                                                for (; (u.value < e.len);){
                                                    IntRef c = ref(0x0ffff);
                                                    BytePtr p = pcopy(utf_decodeChar(se.string, e.len, u, c));
                                                    if (p != null)
                                                        e.error(new BytePtr("%s"), p);
                                                    else
                                                        buffer.writeUTF16(c.value);
                                                }
                                            }
                                            newlen = buffer.offset / 2;
                                            buffer.writeUTF16(0);
                                            /*goto L1*/{ __dispatch4 = -1; continue dispatched_4; }
                                        case 7969:
                                            {
                                                IntRef u_1 = ref(0);
                                                for (; (u_1.value < e.len);){
                                                    IntRef c_1 = ref(0x0ffff);
                                                    BytePtr p_1 = pcopy(utf_decodeChar(se.string, e.len, u_1, c_1));
                                                    if (p_1 != null)
                                                        e.error(new BytePtr("%s"), p_1);
                                                    buffer.write4(c_1.value);
                                                    newlen++;
                                                }
                                            }
                                            buffer.write4(0);
                                            /*goto L1*/{ __dispatch4 = -1; continue dispatched_4; }
                                        case 8223:
                                            {
                                                IntRef u_2 = ref(0);
                                                for (; (u_2.value < e.len);){
                                                    IntRef c_2 = ref(0x0ffff);
                                                    BytePtr p_2 = pcopy(utf_decodeWchar(se.wstring, e.len, u_2, c_2));
                                                    if (p_2 != null)
                                                        e.error(new BytePtr("%s"), p_2);
                                                    else
                                                        buffer.writeUTF8(c_2.value);
                                                }
                                            }
                                            newlen = buffer.offset;
                                            buffer.writeUTF8(0);
                                            /*goto L1*/{ __dispatch4 = -1; continue dispatched_4; }
                                        case 8225:
                                            {
                                                IntRef u_3 = ref(0);
                                                for (; (u_3.value < e.len);){
                                                    IntRef c_3 = ref(0x0ffff);
                                                    BytePtr p_3 = pcopy(utf_decodeWchar(se.wstring, e.len, u_3, c_3));
                                                    if (p_3 != null)
                                                        e.error(new BytePtr("%s"), p_3);
                                                    buffer.write4(c_3.value);
                                                    newlen++;
                                                }
                                            }
                                            buffer.write4(0);
                                            /*goto L1*/{ __dispatch4 = -1; continue dispatched_4; }
                                        case 8479:
                                            {
                                                int u_4 = 0;
                                                for (; (u_4 < e.len);u_4++){
                                                    int c_4 = se.dstring.get(u_4);
                                                    if (!utf_isValidDchar(c_4))
                                                        e.error(new BytePtr("invalid UCS-32 char \\U%08x"), c_4);
                                                    else
                                                        buffer.writeUTF8(c_4);
                                                    newlen++;
                                                }
                                            }
                                            newlen = buffer.offset;
                                            buffer.writeUTF8(0);
                                            /*goto L1*/{ __dispatch4 = -1; continue dispatched_4; }
                                        case 8480:
                                            {
                                                int u_5 = 0;
                                                for (; (u_5 < e.len);u_5++){
                                                    int c_5 = se.dstring.get(u_5);
                                                    if (!utf_isValidDchar(c_5))
                                                        e.error(new BytePtr("invalid UCS-32 char \\U%08x"), c_5);
                                                    else
                                                        buffer.writeUTF16(c_5);
                                                    newlen++;
                                                }
                                            }
                                            newlen = buffer.offset / 2;
                                            buffer.writeUTF16(0);
                                            /*goto L1*/{ __dispatch4 = -1; continue dispatched_4; }
                                        /*L1:*/
                                        case -1:
                                        __dispatch4 = 0;
                                            if (copied == 0)
                                            {
                                                se = (StringExp)e.copy();
                                                copied = 1;
                                            }
                                            se.string = pcopy(buffer.extractData());
                                            se.len = newlen;
                                            {
                                                long szx = tb.nextOf().size();
                                                assert((szx <= 255L));
                                                se.sz = (byte)szx;
                                            }
                                            break;
                                        default:
                                        assert((typeb.nextOf().size() != tb.nextOf().size()));
                                        /*goto Lcast*/throw Dispatch1.INSTANCE;
                                    }
                                } while(__dispatch4 != 0);
                            }
                        }
                        finally {
                        }
                    }
                }
                catch(Dispatch0 __d){}
            /*L2:*/
                assert(copied != 0);
                if (((tb.ty & 0xFF) == ENUMTY.Tsarray))
                {
                    int dim2 = (int)((TypeSArray)tb).dim.toInteger();
                    if ((dim2 != se.len))
                    {
                        int newsz = (se.sz & 0xFF);
                        int d = (dim2 < se.len) ? dim2 : se.len;
                        Object s = pcopy(Mem.xmalloc((dim2 + 1) * newsz));
                        memcpy((BytePtr)s, (se.string), (d * newsz));
                        memset(((BytePtr)s).plus((d * newsz)), 0, (dim2 + 1 - d) * newsz);
                        se.string = pcopy((((BytePtr)s)));
                        se.len = dim2;
                    }
                }
                se.type = this.t;
                this.result = se;
                return ;
            }
            catch(Dispatch1 __d){}
        /*Lcast:*/
            this.result = new CastExp(e.loc, se, this.t);
            this.result.type = this.t;
        }
        public  void visit(AddrExp e) {
            this.result = e;
            Type tb = this.t.toBasetype();
            Type typeb = e.type.toBasetype();
            if (tb.equals(typeb))
            {
                this.result = e.copy();
                this.result.type = this.t;
                return ;
            }
            if (((e.e1.op & 0xFF) == 214) && ((tb.ty & 0xFF) == ENUMTY.Tpointer) || ((tb.ty & 0xFF) == ENUMTY.Tdelegate) && ((tb.nextOf().ty & 0xFF) == ENUMTY.Tfunction))
            {
                OverExp eo = (OverExp)e.e1;
                FuncDeclaration f = null;
                {
                    int i = 0;
                    for (; (i < eo.vars.a.length);i++){
                        Dsymbol s = eo.vars.a.get(i);
                        FuncDeclaration f2 = s.isFuncDeclaration();
                        assert(f2 != null);
                        if (f2.overloadExactMatch(tb.nextOf()) != null)
                        {
                            if (f != null)
                            {
                                ScopeDsymbol.multiplyDefined(e.loc, f, f2);
                            }
                            else
                                f = f2;
                        }
                    }
                }
                if (f != null)
                {
                    f.tookAddressOf++;
                    SymOffExp se = new SymOffExp(e.loc, f, 0L, false);
                    expressionSemantic(se, this.sc);
                    this.visit(se);
                    return ;
                }
            }
            if (((e.e1.op & 0xFF) == 26) && ((typeb.ty & 0xFF) == ENUMTY.Tpointer) && ((typeb.nextOf().ty & 0xFF) == ENUMTY.Tfunction) && ((tb.ty & 0xFF) == ENUMTY.Tpointer) && ((tb.nextOf().ty & 0xFF) == ENUMTY.Tfunction))
            {
                VarExp ve = (VarExp)e.e1;
                FuncDeclaration f = ve.var.isFuncDeclaration();
                if (f != null)
                {
                    assert(f.isImportedSymbol());
                    f = f.overloadExactMatch(tb.nextOf());
                    if (f != null)
                    {
                        this.result = new VarExp(e.loc, f, false);
                        this.result.type = f.type;
                        this.result = new AddrExp(e.loc, this.result, this.t);
                        return ;
                    }
                }
            }
            {
                FuncDeclaration f = isFuncAddress(e, null);
                if ((f) != null)
                {
                    if (f.checkForwardRef(e.loc))
                    {
                        this.result = new ErrorExp();
                        return ;
                    }
                }
            }
            this.visit((Expression)e);
        }
        public  void visit(TupleExp e) {
            if (e.type.equals(this.t))
            {
                this.result = e;
                return ;
            }
            TupleExp te = (TupleExp)e.copy();
            te.e0 = e.e0 != null ? e.e0.copy() : null;
            te.exps = (e.exps).copy();
            {
                int i = 0;
                for (; (i < (te.exps).length);i++){
                    Expression ex = (te.exps).get(i);
                    ex = ex.castTo(this.sc, this.t);
                    te.exps.set(i, ex);
                }
            }
            this.result = te;
        }
        public  void visit(ArrayLiteralExp e) {
            ArrayLiteralExp ae = e;
            Type tb = this.t.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tarray) && global.params.vsafe)
            {
                if (checkArrayLiteralEscape(this.sc, ae, false))
                {
                    this.result = new ErrorExp();
                    return ;
                }
            }
            if ((pequals(e.type, this.t)))
            {
                this.result = e;
                return ;
            }
            Type typeb = e.type.toBasetype();
            try {
                if (((tb.ty & 0xFF) == ENUMTY.Tarray) || ((tb.ty & 0xFF) == ENUMTY.Tsarray) && ((typeb.ty & 0xFF) == ENUMTY.Tarray) || ((typeb.ty & 0xFF) == ENUMTY.Tsarray))
                {
                    if (((tb.nextOf().toBasetype().ty & 0xFF) == ENUMTY.Tvoid) && ((typeb.nextOf().toBasetype().ty & 0xFF) != ENUMTY.Tvoid))
                    {
                    }
                    else if (((typeb.ty & 0xFF) == ENUMTY.Tsarray) && ((typeb.nextOf().toBasetype().ty & 0xFF) == ENUMTY.Tvoid))
                    {
                    }
                    else
                    {
                        if (((tb.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            TypeSArray tsa = (TypeSArray)tb;
                            if (((long)(e.elements).length != tsa.dim.toInteger()))
                                /*goto L1*/throw Dispatch0.INSTANCE;
                        }
                        ae = (ArrayLiteralExp)e.copy();
                        if (e.basis != null)
                            ae.basis = e.basis.castTo(this.sc, tb.nextOf());
                        ae.elements = (e.elements).copy();
                        {
                            int i = 0;
                            for (; (i < (e.elements).length);i++){
                                Expression ex = (e.elements).get(i);
                                if (ex == null)
                                    continue;
                                ex = ex.castTo(this.sc, tb.nextOf());
                                ae.elements.set(i, ex);
                            }
                        }
                        ae.type = this.t;
                        this.result = ae;
                        return ;
                    }
                }
                else if (((tb.ty & 0xFF) == ENUMTY.Tpointer) && ((typeb.ty & 0xFF) == ENUMTY.Tsarray))
                {
                    Type tp = typeb.nextOf().pointerTo();
                    if (!tp.equals(ae.type))
                    {
                        ae = (ArrayLiteralExp)e.copy();
                        ae.type = tp;
                    }
                }
                else if (((tb.ty & 0xFF) == ENUMTY.Tvector) && ((typeb.ty & 0xFF) == ENUMTY.Tarray) || ((typeb.ty & 0xFF) == ENUMTY.Tsarray))
                {
                    TypeVector tv = (TypeVector)tb;
                    TypeSArray tbase = (TypeSArray)tv.basetype;
                    assert(((tbase.ty & 0xFF) == ENUMTY.Tsarray));
                    int edim = (e.elements).length;
                    long tbasedim = tbase.dim.toInteger();
                    if (((long)edim > tbasedim))
                        /*goto L1*/throw Dispatch0.INSTANCE;
                    ae = (ArrayLiteralExp)e.copy();
                    ae.type = tbase;
                    ae.elements = (e.elements).copy();
                    Type telement = tv.elementType();
                    {
                        int __key901 = 0;
                        int __limit902 = edim;
                        for (; (__key901 < __limit902);__key901 += 1) {
                            int i = __key901;
                            Expression ex = (e.elements).get(i);
                            ex = ex.castTo(this.sc, telement);
                            ae.elements.set(i, ex);
                        }
                    }
                    (ae.elements).setDim((int)tbasedim);
                    {
                        int __key903 = edim;
                        int __limit904 = (int)tbasedim;
                        for (; (__key903 < __limit904);__key903 += 1) {
                            int i = __key903;
                            Expression ex = typeb.nextOf().defaultInitLiteral(e.loc);
                            ex = ex.castTo(this.sc, telement);
                            ae.elements.set(i, ex);
                        }
                    }
                    Expression ev = new VectorExp(e.loc, ae, tb);
                    ev = expressionSemantic(ev, this.sc);
                    this.result = ev;
                    return ;
                }
            }
            catch(Dispatch0 __d){}
        /*L1:*/
            this.visit((Expression)ae);
        }
        public  void visit(AssocArrayLiteralExp e) {
            if ((pequals(e.type, this.t)))
            {
                this.result = e;
                return ;
            }
            Type tb = this.t.toBasetype();
            Type typeb = e.type.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Taarray) && ((typeb.ty & 0xFF) == ENUMTY.Taarray) && ((tb.nextOf().toBasetype().ty & 0xFF) != ENUMTY.Tvoid))
            {
                AssocArrayLiteralExp ae = (AssocArrayLiteralExp)e.copy();
                ae.keys = (e.keys).copy();
                ae.values = (e.values).copy();
                assert(((e.keys).length == (e.values).length));
                {
                    int i = 0;
                    for (; (i < (e.keys).length);i++){
                        Expression ex = (e.values).get(i);
                        ex = ex.castTo(this.sc, tb.nextOf());
                        ae.values.set(i, ex);
                        ex = (e.keys).get(i);
                        ex = ex.castTo(this.sc, ((TypeAArray)tb).index);
                        ae.keys.set(i, ex);
                    }
                }
                ae.type = this.t;
                this.result = ae;
                return ;
            }
            this.visit((Expression)e);
        }
        public  void visit(SymOffExp e) {
            if ((pequals(e.type, this.t)) && !e.hasOverloads)
            {
                this.result = e;
                return ;
            }
            Type tb = this.t.toBasetype();
            Type typeb = e.type.toBasetype();
            if (tb.equals(typeb))
            {
                this.result = e.copy();
                this.result.type = this.t;
                ((SymOffExp)this.result).hasOverloads = false;
                return ;
            }
            if (e.hasOverloads && ((typeb.ty & 0xFF) == ENUMTY.Tpointer) && ((typeb.nextOf().ty & 0xFF) == ENUMTY.Tfunction) && ((tb.ty & 0xFF) == ENUMTY.Tpointer) || ((tb.ty & 0xFF) == ENUMTY.Tdelegate) && ((tb.nextOf().ty & 0xFF) == ENUMTY.Tfunction))
            {
                FuncDeclaration f = e.var.isFuncDeclaration();
                f = f != null ? f.overloadExactMatch(tb.nextOf()) : null;
                if (f != null)
                {
                    if (((tb.ty & 0xFF) == ENUMTY.Tdelegate))
                    {
                        if (f.needThis() && (hasThis(this.sc) != null))
                        {
                            this.result = new DelegateExp(e.loc, new ThisExp(e.loc), f, false, null);
                            this.result = expressionSemantic(this.result, this.sc);
                        }
                        else if (f.needThis())
                        {
                            e.error(new BytePtr("no `this` to create delegate for `%s`"), f.toChars());
                            this.result = new ErrorExp();
                            return ;
                        }
                        else if (f.isNested())
                        {
                            this.result = new DelegateExp(e.loc, literal0(), f, false, null);
                            this.result = expressionSemantic(this.result, this.sc);
                        }
                        else
                        {
                            e.error(new BytePtr("cannot cast from function pointer to delegate"));
                            this.result = new ErrorExp();
                            return ;
                        }
                    }
                    else
                    {
                        this.result = new SymOffExp(e.loc, f, 0L, false);
                        this.result.type = this.t;
                    }
                    f.tookAddressOf++;
                    return ;
                }
            }
            {
                FuncDeclaration f = isFuncAddress(e, null);
                if ((f) != null)
                {
                    if (f.checkForwardRef(e.loc))
                    {
                        this.result = new ErrorExp();
                        return ;
                    }
                }
            }
            this.visit((Expression)e);
        }
        public  void visit(DelegateExp e) {
            Type tb = this.t.toBasetype();
            Type typeb = e.type.toBasetype();
            if (tb.equals(typeb) && !e.hasOverloads)
            {
                IntRef offset = ref(0);
                e.func.tookAddressOf++;
                if ((e.func.tintro != null) && e.func.tintro.nextOf().isBaseOf(e.func.type.nextOf(), ptr(offset)) && (offset.value != 0))
                    e.error(new BytePtr("%s"), dcast.visitmsg);
                this.result = e.copy();
                this.result.type = this.t;
                return ;
            }
            if (((typeb.ty & 0xFF) == ENUMTY.Tdelegate) && ((tb.ty & 0xFF) == ENUMTY.Tdelegate))
            {
                if (e.func != null)
                {
                    FuncDeclaration f = e.func.overloadExactMatch(tb.nextOf());
                    if (f != null)
                    {
                        IntRef offset = ref(0);
                        if ((f.tintro != null) && f.tintro.nextOf().isBaseOf(f.type.nextOf(), ptr(offset)) && (offset.value != 0))
                            e.error(new BytePtr("%s"), dcast.visitmsg);
                        if ((!pequals(f, e.func)))
                            f.tookAddressOf++;
                        this.result = new DelegateExp(e.loc, e.e1, f, false, e.vthis2);
                        this.result.type = this.t;
                        return ;
                    }
                    if (e.func.tintro != null)
                        e.error(new BytePtr("%s"), dcast.visitmsg);
                }
            }
            {
                FuncDeclaration f = isFuncAddress(e, null);
                if ((f) != null)
                {
                    if (f.checkForwardRef(e.loc))
                    {
                        this.result = new ErrorExp();
                        return ;
                    }
                }
            }
            this.visit((Expression)e);
        }
        public  void visit(FuncExp e) {
            Ref<FuncExp> fe = ref(null);
            if ((e.matchType(this.t, this.sc, ptr(fe), 1) > MATCH.nomatch))
            {
                this.result = fe.value;
                return ;
            }
            this.visit((Expression)e);
        }
        public  void visit(CondExp e) {
            if (!e.type.equals(this.t))
            {
                this.result = new CondExp(e.loc, e.econd, e.e1.castTo(this.sc, this.t), e.e2.castTo(this.sc, this.t));
                this.result.type = this.t;
                return ;
            }
            this.result = e;
        }
        public  void visit(CommaExp e) {
            Expression e2c = e.e2.castTo(this.sc, this.t);
            if ((!pequals(e2c, e.e2)))
            {
                this.result = new CommaExp(e.loc, e.e1, e2c, true);
                this.result.type = e2c.type;
            }
            else
            {
                this.result = e;
                this.result.type = e.e2.type;
            }
        }
        public  void visit(SliceExp e) {
            Type tb = this.t.toBasetype();
            Type typeb = e.type.toBasetype();
            if (e.type.equals(this.t) || ((typeb.ty & 0xFF) != ENUMTY.Tarray) || ((tb.ty & 0xFF) != ENUMTY.Tarray) && ((tb.ty & 0xFF) != ENUMTY.Tsarray))
            {
                this.visit((Expression)e);
                return ;
            }
            if (((tb.ty & 0xFF) == ENUMTY.Tarray))
            {
                if (typeb.nextOf().equivalent(tb.nextOf()))
                {
                    this.result = e.copy();
                    this.result.type = this.t;
                }
                else
                {
                    this.visit((Expression)e);
                }
                return ;
            }
            TypeSArray tsa = (TypeSArray)toStaticArrayType(e);
            if ((tsa != null) && (tsa.size(e.loc) == tb.size(e.loc)))
            {
                this.result = e.copy();
                this.result.type = this.t;
                return ;
            }
            if ((tsa != null) && tsa.dim.equals(((TypeSArray)tb).dim))
            {
                Type t1b = e.e1.type.toBasetype();
                if (((t1b.ty & 0xFF) == ENUMTY.Tsarray))
                    t1b = tb.nextOf().sarrayOf(((TypeSArray)t1b).dim.toInteger());
                else if (((t1b.ty & 0xFF) == ENUMTY.Tarray))
                    t1b = tb.nextOf().arrayOf();
                else if (((t1b.ty & 0xFF) == ENUMTY.Tpointer))
                    t1b = tb.nextOf().pointerTo();
                else
                    throw new AssertionError("Unreachable code!");
                if ((e.e1.implicitConvTo(t1b) > MATCH.nomatch))
                {
                    Expression e1x = e.e1.implicitCastTo(this.sc, t1b);
                    assert(((e1x.op & 0xFF) != 127));
                    e = (SliceExp)e.copy();
                    e.e1 = e1x;
                    e.type = this.t;
                    this.result = e;
                    return ;
                }
            }
            Slice<BytePtr> ts = toAutoQualChars(tsa != null ? tsa : e.type, this.t);
            e.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e.toChars(), ts.get(0), ts.get(1));
            this.result = new ErrorExp();
        }

        public CastTo() {}
    }
    private static class InferType extends Visitor
    {
        private Type t;
        private int flag = 0;
        private Expression result;
        public  InferType(Type t, int flag) {
            this.t = t;
            this.flag = flag;
        }
        public  void visit(Expression e) {
            this.result = e;
        }
        public  void visit(ArrayLiteralExp ale) {
            Type tb = this.t.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Tarray) || ((tb.ty & 0xFF) == ENUMTY.Tsarray))
            {
                Type tn = tb.nextOf();
                if (ale.basis != null)
                    ale.basis = inferType(ale.basis, tn, this.flag);
                {
                    int i = 0;
                    for (; (i < (ale.elements).length);i++){
                        Expression e = (ale.elements).get(i);
                        if (e != null)
                        {
                            e = inferType(e, tn, this.flag);
                            ale.elements.set(i, e);
                        }
                    }
                }
            }
            this.result = ale;
        }
        public  void visit(AssocArrayLiteralExp aale) {
            Type tb = this.t.toBasetype();
            if (((tb.ty & 0xFF) == ENUMTY.Taarray))
            {
                TypeAArray taa = (TypeAArray)tb;
                Type ti = taa.index;
                Type tv = taa.nextOf();
                {
                    int i = 0;
                    for (; (i < (aale.keys).length);i++){
                        Expression e = (aale.keys).get(i);
                        if (e != null)
                        {
                            e = inferType(e, ti, this.flag);
                            aale.keys.set(i, e);
                        }
                    }
                }
                {
                    int i = 0;
                    for (; (i < (aale.values).length);i++){
                        Expression e = (aale.values).get(i);
                        if (e != null)
                        {
                            e = inferType(e, tv, this.flag);
                            aale.values.set(i, e);
                        }
                    }
                }
            }
            this.result = aale;
        }
        public  void visit(FuncExp fe) {
            if (((this.t.ty & 0xFF) == ENUMTY.Tdelegate) || ((this.t.ty & 0xFF) == ENUMTY.Tpointer) && ((this.t.nextOf().ty & 0xFF) == ENUMTY.Tfunction))
            {
                fe.fd.treq = this.t;
            }
            this.result = fe;
        }
        public  void visit(CondExp ce) {
            Type tb = this.t.toBasetype();
            ce.e1 = inferType(ce.e1, tb, this.flag);
            ce.e2 = inferType(ce.e2, tb, this.flag);
            this.result = ce;
        }

        public InferType() {}
    }
    private static class IntRangeVisitor extends Visitor
    {
        private IntRange range = new IntRange();
        public  void visit(Expression e) {
            this.range = IntRange.fromType(e.type).copy();
        }
        public  void visit(IntegerExp e) {
            this.range = new IntRange(new SignExtendedNumber(e.getInteger(), false))._cast(e.type).copy();
        }
        public  void visit(CastExp e) {
            this.range = getIntRange(e.e1)._cast(e.type).copy();
        }
        public  void visit(AddExp e) {
            IntRange ir1 = getIntRange(e.e1).copy();
            IntRange ir2 = getIntRange(e.e2).copy();
            this.range = ir1.opBinary_+(ir2)._cast(e.type).copy();
        }
        public  void visit(MinExp e) {
            IntRange ir1 = getIntRange(e.e1).copy();
            IntRange ir2 = getIntRange(e.e2).copy();
            this.range = ir1.opBinary_-(ir2)._cast(e.type).copy();
        }
        public  void visit(DivExp e) {
            IntRange ir1 = getIntRange(e.e1).copy();
            IntRange ir2 = getIntRange(e.e2).copy();
            this.range = ir1.opBinary_/(ir2)._cast(e.type).copy();
        }
        public  void visit(MulExp e) {
            IntRange ir1 = getIntRange(e.e1).copy();
            IntRange ir2 = getIntRange(e.e2).copy();
            this.range = ir1.opBinary_*(ir2)._cast(e.type).copy();
        }
        public  void visit(ModExp e) {
            IntRange ir1 = getIntRange(e.e1).copy();
            IntRange ir2 = getIntRange(e.e2).copy();
            if (!ir2.absNeg().imin.negative)
            {
                this.visit((Expression)e);
                return ;
            }
            this.range = ir1.opBinary_%(ir2)._cast(e.type).copy();
        }
        public  void visit(AndExp e) {
            IntRange result = new IntRange();
            Ref<Boolean> hasResult = ref(false);
            result.unionOrAssign(getIntRange(e.e1).opBinary_&(getIntRange(e.e2)), hasResult);
            assert(hasResult.value);
            this.range = result._cast(e.type).copy();
        }
        public  void visit(OrExp e) {
            IntRange result = new IntRange();
            Ref<Boolean> hasResult = ref(false);
            result.unionOrAssign(getIntRange(e.e1).opBinary_|(getIntRange(e.e2)), hasResult);
            assert(hasResult.value);
            this.range = result._cast(e.type).copy();
        }
        public  void visit(XorExp e) {
            IntRange result = new IntRange();
            Ref<Boolean> hasResult = ref(false);
            result.unionOrAssign(getIntRange(e.e1).opBinary_^(getIntRange(e.e2)), hasResult);
            assert(hasResult.value);
            this.range = result._cast(e.type).copy();
        }
        public  void visit(ShlExp e) {
            IntRange ir1 = getIntRange(e.e1).copy();
            IntRange ir2 = getIntRange(e.e2).copy();
            this.range = ir1.opBinary_<<(ir2)._cast(e.type).copy();
        }
        public  void visit(ShrExp e) {
            IntRange ir1 = getIntRange(e.e1).copy();
            IntRange ir2 = getIntRange(e.e2).copy();
            this.range = ir1.opBinary_>>(ir2)._cast(e.type).copy();
        }
        public  void visit(UshrExp e) {
            IntRange ir1 = getIntRange(e.e1).castUnsigned(e.e1.type).copy();
            IntRange ir2 = getIntRange(e.e2).copy();
            this.range = ir1.opBinary_>>>(ir2)._cast(e.type).copy();
        }
        public  void visit(AssignExp e) {
            this.range = getIntRange(e.e2)._cast(e.type).copy();
        }
        public  void visit(CondExp e) {
            IntRange ir1 = getIntRange(e.e1).copy();
            IntRange ir2 = getIntRange(e.e2).copy();
            this.range = ir1.unionWith(ir2)._cast(e.type).copy();
        }
        public  void visit(VarExp e) {
            Expression ie = null;
            VarDeclaration vd = e.var.isVarDeclaration();
            if ((vd != null) && (vd.range != null))
                this.range = (vd.range)._cast(e.type).copy();
            else if ((vd != null) && (vd._init != null) && !vd.type.isMutable() && ((ie = vd.getConstInitializer(true)) != null))
                ie.accept(this);
            else
                this.visit((Expression)e);
        }
        public  void visit(CommaExp e) {
            e.e2.accept(this);
        }
        public  void visit(ComExp e) {
            IntRange ir = getIntRange(e.e1).copy();
            this.range = new IntRange(new SignExtendedNumber(~ir.imax.value, !ir.imax.negative), new SignExtendedNumber(~ir.imin.value, !ir.imin.negative))._cast(e.type).copy();
        }
        public  void visit(NegExp e) {
            IntRange ir = getIntRange(e.e1).copy();
            this.range = ir.opUnary_-()._cast(e.type).copy();
        }

        public IntRangeVisitor() {}
    }

    static boolean LOG = false;
    public static Expression implicitCastTo(Expression e, Scope sc, Type t) {
        ImplicitCastTo v = new ImplicitCastTo(sc, t);
        e.accept(v);
        return v.result;
    }
    public static int implicitConvTo(Expression e, Type t) {
        ImplicitConvTo v = new ImplicitConvTo(t);
        e.accept(v);
        return v.result;
    }
    public static Type toStaticArrayType(SliceExp e) {
        if ((e.lwr != null) && (e.upr != null))
        {
            Expression lwr = e.lwr.optimize(0, false);
            Expression upr = e.upr.optimize(0, false);
            if ((lwr.isConst() != 0) && (upr.isConst() != 0))
            {
                int len = (int)(upr.toUInteger() - lwr.toUInteger());
                return e.type.toBasetype().nextOf().sarrayOf((long)len);
            }
        }
        else
        {
            Type t1b = e.e1.type.toBasetype();
            if (((t1b.ty & 0xFF) == ENUMTY.Tsarray))
                return t1b;
        }
        return null;
    }
    public static Expression tryAliasThisCast(Expression e, Scope sc, Type tob, Type t1b, Type t) {
        Expression result = null;
        AggregateDeclaration t1ad = isAggregate(t1b);
        if (t1ad == null)
            return null;
        AggregateDeclaration toad = isAggregate(tob);
        if ((pequals(t1ad, toad)) || (t1ad.aliasthis == null))
            return null;
        result = resolveAliasThis(sc, e, false);
        int errors = global.startGagging();
        result = result.castTo(sc, t);
        return global.endGagging(errors) ? null : result;
    }
    public static Expression castTo(Expression e, Scope sc, Type t) {
        CastTo v = new CastTo(sc, t);
        e.accept(v);
        return v.result;
    }
    public static Expression inferType(Expression e, Type t, int flag) {
        if (t == null)
            return e;
        InferType v = new InferType(t, flag);
        e.accept(v);
        return v.result;
    }
    public static Expression scaleFactor(BinExp be, Scope sc) {
        Type t1b = be.e1.type.toBasetype();
        Type t2b = be.e2.type.toBasetype();
        Expression eoff = null;
        if (((t1b.ty & 0xFF) == ENUMTY.Tpointer) && t2b.isintegral())
        {
            Type t = Type.tptrdiff_t;
            long stride = t1b.nextOf().size(be.loc);
            if (!t.equals(t2b))
                be.e2 = be.e2.castTo(sc, t);
            eoff = be.e2;
            be.e2 = new MulExp(be.loc, be.e2, new IntegerExp(Loc.initial, stride, t));
            be.e2.type = t;
            be.type = be.e1.type;
        }
        else if (((t2b.ty & 0xFF) == ENUMTY.Tpointer) && t1b.isintegral())
        {
            Type t = Type.tptrdiff_t;
            Expression e = null;
            long stride = t2b.nextOf().size(be.loc);
            if (!t.equals(t1b))
                e = be.e1.castTo(sc, t);
            else
                e = be.e1;
            eoff = e;
            e = new MulExp(be.loc, e, new IntegerExp(Loc.initial, stride, t));
            e.type = t;
            be.type = be.e2.type;
            be.e1 = be.e2;
            be.e2 = e;
        }
        else
            throw new AssertionError("Unreachable code!");
        if (((sc).func != null) && ((sc).intypeof == 0))
        {
            eoff = eoff.optimize(0, false);
            if (((eoff.op & 0xFF) == 135) && (eoff.toInteger() == 0L))
            {
            }
            else if ((sc).func.setUnsafe())
            {
                be.error(new BytePtr("pointer arithmetic not allowed in @safe functions"));
                return new ErrorExp();
            }
        }
        return be;
    }
    public static boolean isVoidArrayLiteral(Expression e, Type other) {
        for (; ((e.op & 0xFF) == 47) && ((e.type.ty & 0xFF) == ENUMTY.Tarray) && ((((ArrayLiteralExp)e).elements).length == 1);){
            ArrayLiteralExp ale = (ArrayLiteralExp)e;
            e = ale.getElement(0);
            if (((other.ty & 0xFF) == ENUMTY.Tsarray) || ((other.ty & 0xFF) == ENUMTY.Tarray))
                other = other.nextOf();
            else
                return false;
        }
        if (((other.ty & 0xFF) != ENUMTY.Tsarray) && ((other.ty & 0xFF) != ENUMTY.Tarray))
            return false;
        Type t = e.type;
        return ((e.op & 0xFF) == 47) && ((t.ty & 0xFF) == ENUMTY.Tarray) && ((t.nextOf().ty & 0xFF) == ENUMTY.Tvoid) && ((((ArrayLiteralExp)e).elements).length == 0);
    }
    public static boolean typeMerge(Scope sc, byte op, Ptr<Type> pt, Ptr<Expression> pe1, Ptr<Expression> pe2) {
        Ref<Scope> sc_ref = ref(sc);
        Ref<Ptr<Type>> pt_ref = ref(pt);
        Ref<Ptr<Expression>> pe1_ref = ref(pe1);
        Ref<Ptr<Expression>> pe2_ref = ref(pe2);
        int m = MATCH.nomatch;
        Ref<Expression> e1 = ref(pe1_ref.value.get());
        Ref<Expression> e2 = ref(pe2_ref.value.get());
        Ref<Type> t1 = ref(e1.value.type);
        Ref<Type> t2 = ref(e2.value.type);
        Type t1b = e1.value.type.toBasetype();
        Type t2b = e2.value.type.toBasetype();
        Ref<Type> t = ref(null);
        Function0<Boolean> Lret = new Function0<Boolean>(){
            public Boolean invoke() {
                if (pt_ref.value.get() == null)
                    pt_ref.value.set(0, t.value);
                pe1_ref.value.set(0, e1.value);
                pe2_ref.value.set(0, e2.value);
                return true;
            }
        };
        Function0<Boolean> Lt1 = new Function0<Boolean>(){
            public Boolean invoke() {
                e2.value = e2.value.castTo(sc_ref.value, t1.value);
                t.value = t1.value;
                return Lret.invoke();
            }
        };
        Function0<Boolean> Lt2 = new Function0<Boolean>(){
            public Boolean invoke() {
                e1.value = e1.value.castTo(sc_ref.value, t2.value);
                t.value = t2.value;
                return Lret.invoke();
            }
        };
        Function0<Boolean> Lincompatible = new Function0<Boolean>(){
            public Boolean invoke() {
                return false;
            }
        };
        if (((op & 0xFF) != 100) || ((t1b.ty & 0xFF) != (t2b.ty & 0xFF)) && (t1b.isTypeBasic() != null) && (t2b.isTypeBasic() != null))
        {
            if (((op & 0xFF) == 100) && t1b.ischar() && t2b.ischar())
            {
                e1.value = charPromotions(e1.value, sc_ref.value);
                e2.value = charPromotions(e2.value, sc_ref.value);
            }
            else
            {
                e1.value = integralPromotions(e1.value, sc_ref.value);
                e2.value = integralPromotions(e2.value, sc_ref.value);
            }
        }
        t1.value = e1.value.type;
        t2.value = e2.value.type;
        assert(t1.value != null);
        t.value = t1.value;
        Type att1 = null;
        Type att2 = null;
        assert(t2.value != null);
        if (((t1.value.mod & 0xFF) != (t2.value.mod & 0xFF)) && ((t1.value.ty & 0xFF) == ENUMTY.Tenum) && ((t2.value.ty & 0xFF) == ENUMTY.Tenum) && (pequals(((TypeEnum)t1.value).sym, ((TypeEnum)t2.value).sym)))
        {
            byte mod = MODmerge(t1.value.mod, t2.value.mod);
            t1.value = t1.value.castMod(mod);
            t2.value = t2.value.castMod(mod);
        }
        while(true) try {
        /*Lagain:*/
            t1b = t1.value.toBasetype();
            t2b = t2.value.toBasetype();
            byte ty = impcnvResult.get((t1b.ty & 0xFF)).get((t2b.ty & 0xFF));
            if (((ty & 0xFF) != ENUMTY.Terror))
            {
                byte ty1 = impcnvType1.get((t1b.ty & 0xFF)).get((t2b.ty & 0xFF));
                byte ty2 = impcnvType2.get((t1b.ty & 0xFF)).get((t2b.ty & 0xFF));
                if (((t1b.ty & 0xFF) == (ty1 & 0xFF)))
                {
                    if (t1.value.equals(t2.value))
                    {
                        t.value = t1.value;
                        return Lret.invoke();
                    }
                    if (t1b.equals(t2b))
                    {
                        t.value = t1b;
                        return Lret.invoke();
                    }
                }
                t.value = Type.basic.get((ty & 0xFF));
                t1.value = Type.basic.get((ty1 & 0xFF));
                t2.value = Type.basic.get((ty2 & 0xFF));
                e1.value = e1.value.castTo(sc_ref.value, t1.value);
                e2.value = e2.value.castTo(sc_ref.value, t2.value);
                return Lret.invoke();
            }
            t1.value = t1b;
            t2.value = t2b;
            if (((t1.value.ty & 0xFF) == ENUMTY.Ttuple) || ((t2.value.ty & 0xFF) == ENUMTY.Ttuple))
                return Lincompatible.invoke();
            if (t1.value.equals(t2.value))
            {
                if (((t.value.ty & 0xFF) == ENUMTY.Tenum))
                    t.value = t1b;
            }
            else if (((t1.value.ty & 0xFF) == ENUMTY.Tpointer) && ((t2.value.ty & 0xFF) == ENUMTY.Tpointer) || ((t1.value.ty & 0xFF) == ENUMTY.Tdelegate) && ((t2.value.ty & 0xFF) == ENUMTY.Tdelegate))
            {
                Type t1n = t1.value.nextOf();
                Type t2n = t2.value.nextOf();
                if (t1n.equals(t2n))
                {
                }
                else if (((t1n.ty & 0xFF) == ENUMTY.Tvoid))
                    t.value = t2.value;
                else if (((t2n.ty & 0xFF) == ENUMTY.Tvoid))
                {
                }
                else if (t1.value.implicitConvTo(t2.value) != 0)
                {
                    return Lt2.invoke();
                }
                else if (t2.value.implicitConvTo(t1.value) != 0)
                {
                    return Lt1.invoke();
                }
                else if (((t1n.ty & 0xFF) == ENUMTY.Tfunction) && ((t2n.ty & 0xFF) == ENUMTY.Tfunction))
                {
                    TypeFunction tf1 = (TypeFunction)t1n;
                    TypeFunction tf2 = (TypeFunction)t2n;
                    tf1.purityLevel();
                    tf2.purityLevel();
                    TypeFunction d = (TypeFunction)tf1.syntaxCopy();
                    if ((tf1.purity != tf2.purity))
                        d.purity = PURE.impure;
                    assert((d.purity != PURE.fwdref));
                    d.isnothrow = tf1.isnothrow && tf2.isnothrow;
                    d.isnogc = tf1.isnogc && tf2.isnogc;
                    if ((tf1.trust == tf2.trust))
                        d.trust = tf1.trust;
                    else if ((tf1.trust <= TRUST.system) || (tf2.trust <= TRUST.system))
                        d.trust = TRUST.system;
                    else
                        d.trust = TRUST.trusted;
                    Type tx = null;
                    if (((t1.value.ty & 0xFF) == ENUMTY.Tdelegate))
                    {
                        tx = new TypeDelegate(d);
                    }
                    else
                        tx = d.pointerTo();
                    tx = typeSemantic(tx, e1.value.loc, sc_ref.value);
                    if ((t1.value.implicitConvTo(tx) != 0) && (t2.value.implicitConvTo(tx) != 0))
                    {
                        t.value = tx;
                        e1.value = e1.value.castTo(sc_ref.value, t.value);
                        e2.value = e2.value.castTo(sc_ref.value, t.value);
                        return Lret.invoke();
                    }
                    return Lincompatible.invoke();
                }
                else if (((t1n.mod & 0xFF) != (t2n.mod & 0xFF)))
                {
                    if (!t1n.isImmutable() && !t2n.isImmutable() && ((t1n.isShared() ? 1 : 0) != (t2n.isShared() ? 1 : 0)))
                        return Lincompatible.invoke();
                    byte mod = MODmerge(t1n.mod, t2n.mod);
                    t1.value = t1n.castMod(mod).pointerTo();
                    t2.value = t2n.castMod(mod).pointerTo();
                    t.value = t1.value;
                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                }
                else if (((t1n.ty & 0xFF) == ENUMTY.Tclass) && ((t2n.ty & 0xFF) == ENUMTY.Tclass))
                {
                    ClassDeclaration cd1 = t1n.isClassHandle();
                    ClassDeclaration cd2 = t2n.isClassHandle();
                    IntRef offset = ref(0);
                    if (cd1.isBaseOf(cd2, ptr(offset)))
                    {
                        if (offset.value != 0)
                            e2.value = e2.value.castTo(sc_ref.value, t.value);
                    }
                    else if (cd2.isBaseOf(cd1, ptr(offset)))
                    {
                        t.value = t2.value;
                        if (offset.value != 0)
                            e1.value = e1.value.castTo(sc_ref.value, t.value);
                    }
                    else
                        return Lincompatible.invoke();
                }
                else
                {
                    t1.value = t1n.constOf().pointerTo();
                    t2.value = t2n.constOf().pointerTo();
                    if (t1.value.implicitConvTo(t2.value) != 0)
                    {
                        return Lt2.invoke();
                    }
                    else if (t2.value.implicitConvTo(t1.value) != 0)
                    {
                        return Lt1.invoke();
                    }
                    return Lincompatible.invoke();
                }
            }
            else if (((t1.value.ty & 0xFF) == ENUMTY.Tsarray) || ((t1.value.ty & 0xFF) == ENUMTY.Tarray) && ((e2.value.op & 0xFF) == 13) && ((t2.value.ty & 0xFF) == ENUMTY.Tpointer) && ((t2.value.nextOf().ty & 0xFF) == ENUMTY.Tvoid) || ((e2.value.op & 0xFF) == 47) && ((t2.value.ty & 0xFF) == ENUMTY.Tsarray) && ((t2.value.nextOf().ty & 0xFF) == ENUMTY.Tvoid) && (((TypeSArray)t2.value).dim.toInteger() == 0L) || isVoidArrayLiteral(e2.value, t1.value))
            {
                /*goto Lx1*//*unrolled goto*/
            /*Lx1:*/
                t.value = t1.value.nextOf().arrayOf();
                e1.value = e1.value.castTo(sc_ref.value, t.value);
                e2.value = e2.value.castTo(sc_ref.value, t.value);
            }
            else if (((t2.value.ty & 0xFF) == ENUMTY.Tsarray) || ((t2.value.ty & 0xFF) == ENUMTY.Tarray) && ((e1.value.op & 0xFF) == 13) && ((t1.value.ty & 0xFF) == ENUMTY.Tpointer) && ((t1.value.nextOf().ty & 0xFF) == ENUMTY.Tvoid) || ((e1.value.op & 0xFF) == 47) && ((t1.value.ty & 0xFF) == ENUMTY.Tsarray) && ((t1.value.nextOf().ty & 0xFF) == ENUMTY.Tvoid) && (((TypeSArray)t1.value).dim.toInteger() == 0L) || isVoidArrayLiteral(e1.value, t2.value))
            {
                /*goto Lx2*//*unrolled goto*/
            /*Lx2:*/
                t.value = t2.value.nextOf().arrayOf();
                e1.value = e1.value.castTo(sc_ref.value, t.value);
                e2.value = e2.value.castTo(sc_ref.value, t.value);
            }
            else if (((t1.value.ty & 0xFF) == ENUMTY.Tsarray) || ((t1.value.ty & 0xFF) == ENUMTY.Tarray) && ((m = t1.value.implicitConvTo(t2.value)) != MATCH.nomatch))
            {
                if (((t1.value.ty & 0xFF) == ENUMTY.Tsarray) && ((e2.value.op & 0xFF) == 47) && ((op & 0xFF) != 70))
                    return Lt1.invoke();
                if ((m == MATCH.constant) && ((op & 0xFF) == 76) || ((op & 0xFF) == 77) || ((op & 0xFF) == 81) || ((op & 0xFF) == 82) || ((op & 0xFF) == 83) || ((op & 0xFF) == 227) || ((op & 0xFF) == 87) || ((op & 0xFF) == 88) || ((op & 0xFF) == 89))
                {
                    t.value = t2.value;
                    return Lret.invoke();
                }
                return Lt2.invoke();
            }
            else if (((t2.value.ty & 0xFF) == ENUMTY.Tsarray) || ((t2.value.ty & 0xFF) == ENUMTY.Tarray) && (t2.value.implicitConvTo(t1.value) != 0))
            {
                if (((t2.value.ty & 0xFF) == ENUMTY.Tsarray) && ((e1.value.op & 0xFF) == 47) && ((op & 0xFF) != 70))
                    return Lt2.invoke();
                return Lt1.invoke();
            }
            else if (((t1.value.ty & 0xFF) == ENUMTY.Tsarray) || ((t1.value.ty & 0xFF) == ENUMTY.Tarray) || ((t1.value.ty & 0xFF) == ENUMTY.Tpointer) && ((t2.value.ty & 0xFF) == ENUMTY.Tsarray) || ((t2.value.ty & 0xFF) == ENUMTY.Tarray) || ((t2.value.ty & 0xFF) == ENUMTY.Tpointer) && ((t1.value.nextOf().mod & 0xFF) != (t2.value.nextOf().mod & 0xFF)))
            {
                Type t1n = t1.value.nextOf();
                Type t2n = t2.value.nextOf();
                byte mod = (byte)0;
                if (((e1.value.op & 0xFF) == 13) && ((e2.value.op & 0xFF) != 13))
                    mod = t2n.mod;
                else if (((e1.value.op & 0xFF) != 13) && ((e2.value.op & 0xFF) == 13))
                    mod = t1n.mod;
                else if (!t1n.isImmutable() && !t2n.isImmutable() && ((t1n.isShared() ? 1 : 0) != (t2n.isShared() ? 1 : 0)))
                    return Lincompatible.invoke();
                else
                    mod = MODmerge(t1n.mod, t2n.mod);
                if (((t1.value.ty & 0xFF) == ENUMTY.Tpointer))
                    t1.value = t1n.castMod(mod).pointerTo();
                else
                    t1.value = t1n.castMod(mod).arrayOf();
                if (((t2.value.ty & 0xFF) == ENUMTY.Tpointer))
                    t2.value = t2n.castMod(mod).pointerTo();
                else
                    t2.value = t2n.castMod(mod).arrayOf();
                t.value = t1.value;
                /*goto Lagain*/throw Dispatch0.INSTANCE;
            }
            else if (((t1.value.ty & 0xFF) == ENUMTY.Tclass) && ((t2.value.ty & 0xFF) == ENUMTY.Tclass))
            {
                if (((t1.value.mod & 0xFF) != (t2.value.mod & 0xFF)))
                {
                    byte mod = (byte)0;
                    if (((e1.value.op & 0xFF) == 13) && ((e2.value.op & 0xFF) != 13))
                        mod = t2.value.mod;
                    else if (((e1.value.op & 0xFF) != 13) && ((e2.value.op & 0xFF) == 13))
                        mod = t1.value.mod;
                    else if (!t1.value.isImmutable() && !t2.value.isImmutable() && ((t1.value.isShared() ? 1 : 0) != (t2.value.isShared() ? 1 : 0)))
                        return Lincompatible.invoke();
                    else
                        mod = MODmerge(t1.value.mod, t2.value.mod);
                    t1.value = t1.value.castMod(mod);
                    t2.value = t2.value.castMod(mod);
                    t.value = t1.value;
                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                }
                /*goto Lcc*/throw Dispatch.INSTANCE;
            }
            else if (((t1.value.ty & 0xFF) == ENUMTY.Tclass) || ((t2.value.ty & 0xFF) == ENUMTY.Tclass))
            {
            /*Lcc:*/
                for (; 1 != 0;){
                    int i1 = e2.value.implicitConvTo(t1.value);
                    int i2 = e1.value.implicitConvTo(t2.value);
                    if ((i1 != 0) && (i2 != 0))
                    {
                        if (((t1.value.ty & 0xFF) == ENUMTY.Tpointer))
                            i1 = MATCH.nomatch;
                        else if (((t2.value.ty & 0xFF) == ENUMTY.Tpointer))
                            i2 = MATCH.nomatch;
                    }
                    if (i2 != 0)
                    {
                        e2.value = e2.value.castTo(sc_ref.value, t2.value);
                        return Lt2.invoke();
                    }
                    else if (i1 != 0)
                    {
                        e1.value = e1.value.castTo(sc_ref.value, t1.value);
                        return Lt1.invoke();
                    }
                    else if (((t1.value.ty & 0xFF) == ENUMTY.Tclass) && ((t2.value.ty & 0xFF) == ENUMTY.Tclass))
                    {
                        TypeClass tc1 = (TypeClass)t1.value;
                        TypeClass tc2 = (TypeClass)t2.value;
                        ClassDeclaration cd1 = tc1.sym.baseClass;
                        ClassDeclaration cd2 = tc2.sym.baseClass;
                        if ((cd1 != null) && (cd2 != null))
                        {
                            t1.value = cd1.type.castMod(t1.value.mod);
                            t2.value = cd2.type.castMod(t2.value.mod);
                        }
                        else if (cd1 != null)
                            t1.value = cd1.type;
                        else if (cd2 != null)
                            t2.value = cd2.type;
                        else
                            return Lincompatible.invoke();
                    }
                    else if (((t1.value.ty & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)t1.value).sym.aliasthis != null))
                    {
                        if ((att1 != null) && (pequals(e1.value.type, att1)))
                            return Lincompatible.invoke();
                        if ((att1 == null) && e1.value.type.checkAliasThisRec())
                            att1 = e1.value.type;
                        e1.value = resolveAliasThis(sc_ref.value, e1.value, false);
                        t1.value = e1.value.type;
                        continue;
                    }
                    else if (((t2.value.ty & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)t2.value).sym.aliasthis != null))
                    {
                        if ((att2 != null) && (pequals(e2.value.type, att2)))
                            return Lincompatible.invoke();
                        if ((att2 == null) && e2.value.type.checkAliasThisRec())
                            att2 = e2.value.type;
                        e2.value = resolveAliasThis(sc_ref.value, e2.value, false);
                        t2.value = e2.value.type;
                        continue;
                    }
                    else
                        return Lincompatible.invoke();
                }
            }
            else if (((t1.value.ty & 0xFF) == ENUMTY.Tstruct) && ((t2.value.ty & 0xFF) == ENUMTY.Tstruct))
            {
                if (((t1.value.mod & 0xFF) != (t2.value.mod & 0xFF)))
                {
                    if (!t1.value.isImmutable() && !t2.value.isImmutable() && ((t1.value.isShared() ? 1 : 0) != (t2.value.isShared() ? 1 : 0)))
                        return Lincompatible.invoke();
                    byte mod = MODmerge(t1.value.mod, t2.value.mod);
                    t1.value = t1.value.castMod(mod);
                    t2.value = t2.value.castMod(mod);
                    t.value = t1.value;
                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                }
                TypeStruct ts1 = (TypeStruct)t1.value;
                TypeStruct ts2 = (TypeStruct)t2.value;
                if ((!pequals(ts1.sym, ts2.sym)))
                {
                    if ((ts1.sym.aliasthis == null) && (ts2.sym.aliasthis == null))
                        return Lincompatible.invoke();
                    int i1 = MATCH.nomatch;
                    int i2 = MATCH.nomatch;
                    Expression e1b = null;
                    Expression e2b = null;
                    if (ts2.sym.aliasthis != null)
                    {
                        if ((att2 != null) && (pequals(e2.value.type, att2)))
                            return Lincompatible.invoke();
                        if ((att2 == null) && e2.value.type.checkAliasThisRec())
                            att2 = e2.value.type;
                        e2b = resolveAliasThis(sc_ref.value, e2.value, false);
                        i1 = e2b.implicitConvTo(t1.value);
                    }
                    if (ts1.sym.aliasthis != null)
                    {
                        if ((att1 != null) && (pequals(e1.value.type, att1)))
                            return Lincompatible.invoke();
                        if ((att1 == null) && e1.value.type.checkAliasThisRec())
                            att1 = e1.value.type;
                        e1b = resolveAliasThis(sc_ref.value, e1.value, false);
                        i2 = e1b.implicitConvTo(t2.value);
                    }
                    if ((i1 != 0) && (i2 != 0))
                        return Lincompatible.invoke();
                    if (i1 != 0)
                        return Lt1.invoke();
                    else if (i2 != 0)
                        return Lt2.invoke();
                    if (e1b != null)
                    {
                        e1.value = e1b;
                        t1.value = e1b.type.toBasetype();
                    }
                    if (e2b != null)
                    {
                        e2.value = e2b;
                        t2.value = e2b.type.toBasetype();
                    }
                    t.value = t1.value;
                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                }
            }
            else if (((t1.value.ty & 0xFF) == ENUMTY.Tstruct) || ((t2.value.ty & 0xFF) == ENUMTY.Tstruct))
            {
                if (((t1.value.ty & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)t1.value).sym.aliasthis != null))
                {
                    if ((att1 != null) && (pequals(e1.value.type, att1)))
                        return Lincompatible.invoke();
                    if ((att1 == null) && e1.value.type.checkAliasThisRec())
                        att1 = e1.value.type;
                    e1.value = resolveAliasThis(sc_ref.value, e1.value, false);
                    t1.value = e1.value.type;
                    t.value = t1.value;
                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                }
                if (((t2.value.ty & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)t2.value).sym.aliasthis != null))
                {
                    if ((att2 != null) && (pequals(e2.value.type, att2)))
                        return Lincompatible.invoke();
                    if ((att2 == null) && e2.value.type.checkAliasThisRec())
                        att2 = e2.value.type;
                    e2.value = resolveAliasThis(sc_ref.value, e2.value, false);
                    t2.value = e2.value.type;
                    t.value = t2.value;
                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                }
                return Lincompatible.invoke();
            }
            else if (((e1.value.op & 0xFF) == 121) || ((e1.value.op & 0xFF) == 13) && (e1.value.implicitConvTo(t2.value) != 0))
            {
                return Lt2.invoke();
            }
            else if (((e2.value.op & 0xFF) == 121) || ((e2.value.op & 0xFF) == 13) && (e2.value.implicitConvTo(t1.value) != 0))
            {
                return Lt1.invoke();
            }
            else if (((t1.value.ty & 0xFF) == ENUMTY.Tsarray) && ((t2.value.ty & 0xFF) == ENUMTY.Tsarray) && (e2.value.implicitConvTo(t1.value.nextOf().arrayOf()) != 0))
            {
            /*Lx1:*/
                t.value = t1.value.nextOf().arrayOf();
                e1.value = e1.value.castTo(sc_ref.value, t.value);
                e2.value = e2.value.castTo(sc_ref.value, t.value);
            }
            else if (((t1.value.ty & 0xFF) == ENUMTY.Tsarray) && ((t2.value.ty & 0xFF) == ENUMTY.Tsarray) && (e1.value.implicitConvTo(t2.value.nextOf().arrayOf()) != 0))
            {
            /*Lx2:*/
                t.value = t2.value.nextOf().arrayOf();
                e1.value = e1.value.castTo(sc_ref.value, t.value);
                e2.value = e2.value.castTo(sc_ref.value, t.value);
            }
            else if (((t1.value.ty & 0xFF) == ENUMTY.Tvector) && ((t2.value.ty & 0xFF) == ENUMTY.Tvector))
            {
                TypeVector tv1 = (TypeVector)t1.value;
                TypeVector tv2 = (TypeVector)t2.value;
                if (!tv1.basetype.equals(tv2.basetype))
                    return Lincompatible.invoke();
                /*goto LmodCompare*//*unrolled goto*/
            /*LmodCompare:*/
                if (!t1.value.isImmutable() && !t2.value.isImmutable() && ((t1.value.isShared() ? 1 : 0) != (t2.value.isShared() ? 1 : 0)))
                    return Lincompatible.invoke();
                byte mod = MODmerge(t1.value.mod, t2.value.mod);
                t1.value = t1.value.castMod(mod);
                t2.value = t2.value.castMod(mod);
                t.value = t1.value;
                e1.value = e1.value.castTo(sc_ref.value, t.value);
                e2.value = e2.value.castTo(sc_ref.value, t.value);
                /*goto Lagain*/throw Dispatch0.INSTANCE;
            }
            else if (((t1.value.ty & 0xFF) == ENUMTY.Tvector) && ((t2.value.ty & 0xFF) != ENUMTY.Tvector) && (e2.value.implicitConvTo(t1.value) != 0))
            {
                e2.value = e2.value.castTo(sc_ref.value, t1.value);
                t2.value = t1.value;
                t.value = t1.value;
                /*goto Lagain*/throw Dispatch0.INSTANCE;
            }
            else if (((t2.value.ty & 0xFF) == ENUMTY.Tvector) && ((t1.value.ty & 0xFF) != ENUMTY.Tvector) && (e1.value.implicitConvTo(t2.value) != 0))
            {
                e1.value = e1.value.castTo(sc_ref.value, t2.value);
                t1.value = t2.value;
                t.value = t1.value;
                /*goto Lagain*/throw Dispatch0.INSTANCE;
            }
            else if (t1.value.isintegral() && t2.value.isintegral())
            {
                if (((t1.value.ty & 0xFF) != (t2.value.ty & 0xFF)))
                {
                    if (((t1.value.ty & 0xFF) == ENUMTY.Tvector) || ((t2.value.ty & 0xFF) == ENUMTY.Tvector))
                        return Lincompatible.invoke();
                    e1.value = integralPromotions(e1.value, sc_ref.value);
                    e2.value = integralPromotions(e2.value, sc_ref.value);
                    t1.value = e1.value.type;
                    t2.value = e2.value.type;
                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                }
                assert(((t1.value.ty & 0xFF) == (t2.value.ty & 0xFF)));
            /*LmodCompare:*/
                if (!t1.value.isImmutable() && !t2.value.isImmutable() && ((t1.value.isShared() ? 1 : 0) != (t2.value.isShared() ? 1 : 0)))
                    return Lincompatible.invoke();
                byte mod = MODmerge(t1.value.mod, t2.value.mod);
                t1.value = t1.value.castMod(mod);
                t2.value = t2.value.castMod(mod);
                t.value = t1.value;
                e1.value = e1.value.castTo(sc_ref.value, t.value);
                e2.value = e2.value.castTo(sc_ref.value, t.value);
                /*goto Lagain*/throw Dispatch0.INSTANCE;
            }
            else if (((t1.value.ty & 0xFF) == ENUMTY.Tnull) && ((t2.value.ty & 0xFF) == ENUMTY.Tnull))
            {
                byte mod = MODmerge(t1.value.mod, t2.value.mod);
                t.value = t1.value.castMod(mod);
                e1.value = e1.value.castTo(sc_ref.value, t.value);
                e2.value = e2.value.castTo(sc_ref.value, t.value);
                return Lret.invoke();
            }
            else if (((t2.value.ty & 0xFF) == ENUMTY.Tnull) && ((t1.value.ty & 0xFF) == ENUMTY.Tpointer) || ((t1.value.ty & 0xFF) == ENUMTY.Taarray) || ((t1.value.ty & 0xFF) == ENUMTY.Tarray))
            {
                return Lt1.invoke();
            }
            else if (((t1.value.ty & 0xFF) == ENUMTY.Tnull) && ((t2.value.ty & 0xFF) == ENUMTY.Tpointer) || ((t2.value.ty & 0xFF) == ENUMTY.Taarray) || ((t2.value.ty & 0xFF) == ENUMTY.Tarray))
            {
                return Lt2.invoke();
            }
            else if (((t1.value.ty & 0xFF) == ENUMTY.Tarray) && isBinArrayOp(op) && isArrayOpOperand(e1.value))
            {
                if (e2.value.implicitConvTo(t1.value.nextOf()) != 0)
                {
                    e2.value = e2.value.castTo(sc_ref.value, t1.value.nextOf());
                    t.value = t1.value.nextOf().arrayOf();
                }
                else if (t1.value.nextOf().implicitConvTo(e2.value.type) != 0)
                {
                    t.value = e2.value.type.arrayOf();
                }
                else if (((t2.value.ty & 0xFF) == ENUMTY.Tarray) && isArrayOpOperand(e2.value))
                {
                    if (t1.value.nextOf().implicitConvTo(t2.value.nextOf()) != 0)
                    {
                        t.value = t2.value.nextOf().arrayOf();
                    }
                    else if (t2.value.nextOf().implicitConvTo(t1.value.nextOf()) != 0)
                    {
                        t.value = t1.value.nextOf().arrayOf();
                    }
                    else
                        return Lincompatible.invoke();
                }
                else
                    return Lincompatible.invoke();
            }
            else if (((t2.value.ty & 0xFF) == ENUMTY.Tarray) && isBinArrayOp(op) && isArrayOpOperand(e2.value))
            {
                if (e1.value.implicitConvTo(t2.value.nextOf()) != 0)
                {
                    e1.value = e1.value.castTo(sc_ref.value, t2.value.nextOf());
                    t.value = t2.value.nextOf().arrayOf();
                }
                else if (t2.value.nextOf().implicitConvTo(e1.value.type) != 0)
                {
                    t.value = e1.value.type.arrayOf();
                }
                else
                    return Lincompatible.invoke();
                e1.value = e1.value.optimize(0, false);
                if (isCommutative(op) && (e1.value.isConst() != 0))
                {
                    Expression tmp = e1.value;
                    e1.value = e2.value;
                    e2.value = tmp;
                }
            }
            else
            {
                return Lincompatible.invoke();
            }
            return Lret.invoke();
            break;
        } catch(Dispatch0 __d){}
    }
    public static Expression typeCombine(BinExp be, Scope sc) {
        Ref<BinExp> be_ref = ref(be);
        Function0<Expression> errorReturn = new Function0<Expression>(){
            public Expression invoke() {
                Expression ex = be_ref.value.incompatibleTypes();
                if (((ex.op & 0xFF) == 127))
                    return ex;
                return new ErrorExp();
            }
        };
        Type t1 = be_ref.value.e1.type.toBasetype();
        Type t2 = be_ref.value.e2.type.toBasetype();
        if (((be_ref.value.op & 0xFF) == 75) || ((be_ref.value.op & 0xFF) == 74))
        {
            if (((t1.ty & 0xFF) == ENUMTY.Tstruct) && ((t2.ty & 0xFF) == ENUMTY.Tstruct))
                return errorReturn.invoke();
            else if (((t1.ty & 0xFF) == ENUMTY.Tclass) && ((t2.ty & 0xFF) == ENUMTY.Tclass))
                return errorReturn.invoke();
            else if (((t1.ty & 0xFF) == ENUMTY.Taarray) && ((t2.ty & 0xFF) == ENUMTY.Taarray))
                return errorReturn.invoke();
        }
        if (!typeMerge(sc, be_ref.value.op, be_ref.value.type, be_ref.value.e1, be_ref.value.e2))
            return errorReturn.invoke();
        if (((be_ref.value.e1.op & 0xFF) == 127))
            return be_ref.value.e1;
        if (((be_ref.value.e2.op & 0xFF) == 127))
            return be_ref.value.e2;
        return null;
    }
    public static Expression integralPromotions(Expression e, Scope sc) {
        switch ((e.type.toBasetype().ty & 0xFF))
        {
            case 12:
                e.error(new BytePtr("void has no value"));
                return new ErrorExp();
            case 13:
            case 14:
            case 15:
            case 16:
            case 30:
            case 31:
            case 32:
                e = e.castTo(sc, Type.tint32);
                break;
            case 33:
                e = e.castTo(sc, Type.tuns32);
                break;
            default:
            break;
        }
        return e;
    }
    public static Expression charPromotions(Expression e, Scope sc) {
        switch ((e.type.toBasetype().ty & 0xFF))
        {
            case 31:
            case 32:
            case 33:
                e = e.castTo(sc, Type.tdchar);
                break;
            default:
            throw new AssertionError("Unreachable code!");
        }
        return e;
    }
    public static void fix16997(Scope sc, UnaExp ue) {
        if (global.params.fix16997)
            ue.e1 = integralPromotions(ue.e1, sc);
        else
        {
            switch ((ue.e1.type.toBasetype().ty & 0xFF))
            {
                case 13:
                case 14:
                case 15:
                case 16:
                case 31:
                case 32:
                case 33:
                    ue.deprecation(new BytePtr("integral promotion not done for `%s`, use '-preview=intpromote' switch or `%scast(int)(%s)`"), ue.toChars(), Token.toChars(ue.op), ue.e1.toChars());
                    break;
                default:
                break;
            }
        }
    }
    public static boolean arrayTypeCompatible(Loc loc, Type t1, Type t2) {
        t1 = t1.toBasetype().merge2();
        t2 = t2.toBasetype().merge2();
        if (((t1.ty & 0xFF) == ENUMTY.Tarray) || ((t1.ty & 0xFF) == ENUMTY.Tsarray) || ((t1.ty & 0xFF) == ENUMTY.Tpointer) && ((t2.ty & 0xFF) == ENUMTY.Tarray) || ((t2.ty & 0xFF) == ENUMTY.Tsarray) || ((t2.ty & 0xFF) == ENUMTY.Tpointer))
        {
            if ((t1.nextOf().implicitConvTo(t2.nextOf()) < MATCH.constant) && (t2.nextOf().implicitConvTo(t1.nextOf()) < MATCH.constant) && ((t1.nextOf().ty & 0xFF) != ENUMTY.Tvoid) && ((t2.nextOf().ty & 0xFF) != ENUMTY.Tvoid))
            {
                error(loc, new BytePtr("array equality comparison type mismatch, `%s` vs `%s`"), t1.toChars(), t2.toChars());
            }
            return true;
        }
        return false;
    }
    public static boolean arrayTypeCompatibleWithoutCasting(Type t1, Type t2) {
        t1 = t1.toBasetype();
        t2 = t2.toBasetype();
        if (((t1.ty & 0xFF) == ENUMTY.Tarray) || ((t1.ty & 0xFF) == ENUMTY.Tsarray) || ((t1.ty & 0xFF) == ENUMTY.Tpointer) && ((t2.ty & 0xFF) == (t1.ty & 0xFF)))
        {
            if ((t1.nextOf().implicitConvTo(t2.nextOf()) >= MATCH.constant) || (t2.nextOf().implicitConvTo(t1.nextOf()) >= MATCH.constant))
                return true;
        }
        return false;
    }
    public static IntRange getIntRange(Expression e) {
        IntRangeVisitor v = new IntRangeVisitor();
        e.accept(v);
        return v.range;
    }
}
