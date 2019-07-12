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
        private Type t = null;
        private Ptr<Scope> sc = null;
        private Expression result = null;
        public  ImplicitCastTo(Ptr<Scope> sc, Type t) {
            Ref<Ptr<Scope>> sc_ref = ref(sc);
            Ref<Type> t_ref = ref(t);
            this.sc = sc_ref.value;
            this.t = t_ref.value;
        }

        public  void visit(Expression e) {
            Ref<Expression> e_ref = ref(e);
            IntRef match = ref(e_ref.value.implicitConvTo(this.t));
            if (match.value != 0)
            {
                if ((match.value == MATCH.constant) && (e_ref.value.type.value.constConv(this.t) != 0) || !e_ref.value.isLvalue() && e_ref.value.type.value.equivalent(this.t))
                {
                    this.result = e_ref.value.copy();
                    this.result.type.value = this.t;
                    return ;
                }
                Ref<AggregateDeclaration> ad = ref(isAggregate(e_ref.value.type.value));
                if ((ad.value != null) && (ad.value.aliasthis != null))
                {
                    IntRef adMatch = ref(MATCH.nomatch);
                    if (((ad.value.type.ty & 0xFF) == ENUMTY.Tstruct))
                        adMatch.value = ((TypeStruct)ad.value.type).implicitConvToWithoutAliasThis(this.t);
                    else
                        adMatch.value = ((TypeClass)ad.value.type).implicitConvToWithoutAliasThis(this.t);
                    if (adMatch.value == 0)
                    {
                        Ref<Type> tob = ref(this.t.toBasetype());
                        Ref<Type> t1b = ref(e_ref.value.type.value.toBasetype());
                        Ref<AggregateDeclaration> toad = ref(isAggregate(tob.value));
                        if ((!pequals(ad.value, toad.value)))
                        {
                            if (((t1b.value.ty & 0xFF) == ENUMTY.Tclass) && ((tob.value.ty & 0xFF) == ENUMTY.Tclass))
                            {
                                Ref<ClassDeclaration> t1cd = ref(t1b.value.isClassHandle());
                                Ref<ClassDeclaration> tocd = ref(tob.value.isClassHandle());
                                IntRef offset = ref(0);
                                if (tocd.value.isBaseOf(t1cd.value, ptr(offset)))
                                {
                                    this.result = new CastExp(e_ref.value.loc, e_ref.value, this.t);
                                    this.result.type.value = this.t;
                                    return ;
                                }
                            }
                            this.result = resolveAliasThis(this.sc, e_ref.value, false);
                            this.result = this.result.castTo(this.sc, this.t);
                            return ;
                        }
                    }
                }
                this.result = e_ref.value.castTo(this.sc, this.t);
                return ;
            }
            this.result = e_ref.value.optimize(0, false);
            if ((!pequals(this.result, e_ref.value)))
            {
                this.result.accept(this);
                return ;
            }
            if (((this.t.ty & 0xFF) != ENUMTY.Terror) && ((e_ref.value.type.value.ty & 0xFF) != ENUMTY.Terror))
            {
                if (this.t.deco == null)
                {
                    e_ref.value.error(new BytePtr("forward reference to type `%s`"), this.t.toChars());
                }
                else
                {
                    Slice<BytePtr> ts = toAutoQualChars(e_ref.value.type.value, this.t);
                    e_ref.value.error(new BytePtr("cannot implicitly convert expression `%s` of type `%s` to `%s`"), e_ref.value.toChars(), ts.get(0), ts.get(1));
                }
            }
            this.result = new ErrorExp();
        }

        public  void visit(StringExp e) {
            Ref<StringExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if (((this.result.op & 0xFF) == 121))
            {
                ((StringExp)this.result).committed = e_ref.value.committed;
            }
        }

        public  void visit(ErrorExp e) {
            Ref<ErrorExp> e_ref = ref(e);
            this.result = e_ref.value;
        }

        public  void visit(FuncExp e) {
            Ref<FuncExp> e_ref = ref(e);
            Ref<FuncExp> fe = ref(null);
            if ((e_ref.value.matchType(this.t, this.sc, ptr(fe), 0) > MATCH.nomatch))
            {
                this.result = fe.value;
                return ;
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(ArrayLiteralExp e) {
            Ref<ArrayLiteralExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            Ref<Type> tb = ref(this.result.type.value.toBasetype());
            if (((tb.value.ty & 0xFF) == ENUMTY.Tarray) && global.value.params.useTypeInfo && (Type.dtypeinfo.value != null))
                semanticTypeInfo(this.sc, ((TypeDArray)tb.value).next);
        }

        public  void visit(SliceExp e) {
            Ref<SliceExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if (((this.result.op & 0xFF) != 31))
                return ;
            e_ref.value = (SliceExp)this.result;
            if (((e_ref.value.e1.op & 0xFF) == 47))
            {
                Ref<ArrayLiteralExp> ale = ref((ArrayLiteralExp)e_ref.value.e1);
                Ref<Type> tb = ref(this.t.toBasetype());
                Ref<Type> tx = ref(null);
                if (((tb.value.ty & 0xFF) == ENUMTY.Tsarray))
                    tx.value = tb.value.nextOf().sarrayOf(ale.value.elements != null ? (long)(ale.value.elements.get()).length : 0L);
                else
                    tx.value = tb.value.nextOf().arrayOf();
                e_ref.value.e1 = ale.value.implicitCastTo(this.sc, tx.value);
            }
        }


        public ImplicitCastTo() {}
    }
    private static class ClassCheck
    {
        public static boolean convertible(Loc loc, ClassDeclaration cd, byte mod) {
            Ref<Loc> loc_ref = ref(loc);
            Ref<ClassDeclaration> cd_ref = ref(cd);
            Ref<Byte> mod_ref = ref(mod);
            {
                IntRef i = ref(0);
                for (; (i.value < cd_ref.value.fields.length);i.value++){
                    Ref<VarDeclaration> v = ref(cd_ref.value.fields.get(i.value));
                    Ref<Initializer> _init = ref(v.value._init);
                    if (_init.value != null)
                    {
                        if (_init.value.isVoidInitializer() != null)
                        {
                        }
                        else {
                            Ref<ExpInitializer> ei = ref(_init.value.isExpInitializer());
                            if ((ei.value) != null)
                            {
                                Ref<Type> tb = ref(v.value.type.toBasetype());
                                if ((implicitMod(ei.value.exp, tb.value, mod_ref.value) == MATCH.nomatch))
                                    return false;
                            }
                            else
                            {
                                return false;
                            }
                        }
                    }
                    else if (!v.value.type.isZeroInit(loc_ref.value))
                        return false;
                }
            }
            return cd_ref.value.baseClass != null ? convertible(loc_ref.value, cd_ref.value.baseClass, mod_ref.value) : true;
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
        private Type t = null;
        private int result = 0;
        public  ImplicitConvTo(Type t) {
            Ref<Type> t_ref = ref(t);
            this.t = t_ref.value;
            this.result = MATCH.nomatch;
        }

        public  void visit(Expression e) {
            Ref<Expression> e_ref = ref(e);
            if ((pequals(this.t, Type.terror.value)))
                return ;
            if (e_ref.value.type.value == null)
            {
                e_ref.value.error(new BytePtr("`%s` is not an expression"), e_ref.value.toChars());
                e_ref.value.type.value = Type.terror.value;
            }
            Ref<Expression> ex = ref(e_ref.value.optimize(0, false));
            if (ex.value.type.value.equals(this.t))
            {
                this.result = MATCH.exact;
                return ;
            }
            if ((!pequals(ex.value, e_ref.value)))
            {
                this.result = ex.value.implicitConvTo(this.t);
                return ;
            }
            IntRef match = ref(e_ref.value.type.value.implicitConvTo(this.t));
            if ((match.value != MATCH.nomatch))
            {
                this.result = match.value;
                return ;
            }
            if (e_ref.value.type.value.isintegral() && this.t.isintegral() && (e_ref.value.type.value.isTypeBasic() != null) && (this.t.isTypeBasic() != null))
            {
                Ref<IntRange> src = ref(getIntRange(e_ref.value).copy());
                Ref<IntRange> target = ref(IntRange.fromType(this.t).copy());
                if (target.value.contains(src.value))
                {
                    this.result = MATCH.convert;
                    return ;
                }
            }
        }

        public static int implicitMod(Expression e, Type t, byte mod) {
            Ref<Expression> e_ref = ref(e);
            Ref<Type> t_ref = ref(t);
            Ref<Byte> mod_ref = ref(mod);
            Ref<Type> tprime = ref(null);
            if (((t_ref.value.ty & 0xFF) == ENUMTY.Tpointer))
                tprime.value = t_ref.value.nextOf().castMod(mod_ref.value).pointerTo();
            else if (((t_ref.value.ty & 0xFF) == ENUMTY.Tarray))
                tprime.value = t_ref.value.nextOf().castMod(mod_ref.value).arrayOf();
            else if (((t_ref.value.ty & 0xFF) == ENUMTY.Tsarray))
                tprime.value = t_ref.value.nextOf().castMod(mod_ref.value).sarrayOf(t_ref.value.size() / t_ref.value.nextOf().size());
            else
                tprime.value = t_ref.value.castMod(mod_ref.value);
            return e_ref.value.implicitConvTo(tprime.value);
        }

        public static int implicitConvToAddMin(BinExp e, Type t) {
            Ref<BinExp> e_ref = ref(e);
            Ref<Type> t_ref = ref(t);
            Ref<Type> tb = ref(t_ref.value.toBasetype());
            Ref<Type> typeb = ref(e_ref.value.type.value.toBasetype());
            if (((typeb.value.ty & 0xFF) != ENUMTY.Tpointer) || ((tb.value.ty & 0xFF) != ENUMTY.Tpointer))
                return MATCH.nomatch;
            Ref<Type> t1b = ref(e_ref.value.e1.value.type.value.toBasetype());
            Ref<Type> t2b = ref(e_ref.value.e2.value.type.value.toBasetype());
            if (((t1b.value.ty & 0xFF) == ENUMTY.Tpointer) && t2b.value.isintegral() && t1b.value.equivalent(tb.value))
            {
                IntRef m = ref(e_ref.value.e1.value.implicitConvTo(t_ref.value));
                return (m.value > MATCH.constant) ? MATCH.constant : m.value;
            }
            if (((t2b.value.ty & 0xFF) == ENUMTY.Tpointer) && t1b.value.isintegral() && t2b.value.equivalent(tb.value))
            {
                IntRef m = ref(e_ref.value.e2.value.implicitConvTo(t_ref.value));
                return (m.value > MATCH.constant) ? MATCH.constant : m.value;
            }
            return MATCH.nomatch;
        }

        public  void visit(AddExp e) {
            Ref<AddExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if ((this.result == MATCH.nomatch))
                this.result = implicitConvToAddMin(e_ref.value, this.t);
        }

        public  void visit(MinExp e) {
            Ref<MinExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if ((this.result == MATCH.nomatch))
                this.result = implicitConvToAddMin(e_ref.value, this.t);
        }

        public  void visit(IntegerExp e) {
            Ref<IntegerExp> e_ref = ref(e);
            IntRef m = ref(e_ref.value.type.value.implicitConvTo(this.t));
            if ((m.value >= MATCH.constant))
            {
                this.result = m.value;
                return ;
            }
            Ref<Byte> ty = ref(e_ref.value.type.value.toBasetype().ty);
            Ref<Byte> toty = ref(this.t.toBasetype().ty);
            Ref<Byte> oldty = ref(ty.value);
            if ((m.value == MATCH.nomatch) && ((this.t.ty & 0xFF) == ENUMTY.Tenum))
                return ;
            if (((this.t.ty & 0xFF) == ENUMTY.Tvector))
            {
                Ref<TypeVector> tv = ref((TypeVector)this.t);
                Ref<TypeBasic> tb = ref(tv.value.elementType());
                if (((tb.value.ty & 0xFF) == ENUMTY.Tvoid))
                    return ;
                toty.value = tb.value.ty;
            }
            switch ((ty.value & 0xFF))
            {
                case 30:
                case 13:
                case 31:
                case 14:
                case 15:
                case 16:
                case 32:
                    ty.value = (byte)17;
                    break;
                case 33:
                    ty.value = (byte)18;
                    break;
                default:
                break;
            }
            Ref<Long> value = ref(e_ref.value.toInteger());
            // from template isLosslesslyConvertibleToFP!(Double)
            Function0<Boolean> isLosslesslyConvertibleToFPDouble = new Function0<Boolean>(){
                public Boolean invoke() {
                    if (e_ref.value.type.value.isunsigned())
                    {
                        double f = (double)value.value;
                        return (long)f == value.value;
                    }
                    double f = (double)(long)value.value;
                    return (long)f == (long)value.value;
                }
            };

            // from template isLosslesslyConvertibleToFP!(Double)
            // removed duplicate function, [["boolean isLosslesslyConvertibleToFPDouble"]] signature: boolean isLosslesslyConvertibleToFPDouble

            // from template isLosslesslyConvertibleToFP!(Float)
            Function0<Boolean> isLosslesslyConvertibleToFPFloat = new Function0<Boolean>(){
                public Boolean invoke() {
                    if (e_ref.value.type.value.isunsigned())
                    {
                        float f = (float)value.value;
                        return (long)f == value.value;
                    }
                    float f = (float)(long)value.value;
                    return (long)f == (long)value.value;
                }
            };

            {
                int __dispatch1 = 0;
                dispatched_1:
                do {
                    switch (__dispatch1 != 0 ? __dispatch1 : (toty.value & 0xFF))
                    {
                        case 30:
                            if (((value.value & 1L) != value.value))
                                return ;
                            break;
                        case 13:
                            if (((ty.value & 0xFF) == ENUMTY.Tuns64) && ((value.value & 4294967168L) != 0))
                                return ;
                            else if (((long)((byte)value.value & 0xFF) != value.value))
                                return ;
                            break;
                        case 31:
                            if (((oldty.value & 0xFF) == ENUMTY.Twchar) || ((oldty.value & 0xFF) == ENUMTY.Tdchar) && (value.value > 127L))
                                return ;
                            /*goto case*/{ __dispatch1 = 14; continue dispatched_1; }
                        case 14:
                            __dispatch1 = 0;
                            if (((long)((byte)value.value & 0xFF) != value.value))
                                return ;
                            break;
                        case 15:
                            if (((ty.value & 0xFF) == ENUMTY.Tuns64) && ((value.value & 4294934528L) != 0))
                                return ;
                            else if (((long)(int)(int)value.value != value.value))
                                return ;
                            break;
                        case 32:
                            if (((oldty.value & 0xFF) == ENUMTY.Tdchar) && (value.value > 55295L) && (value.value < 57344L))
                                return ;
                            /*goto case*/{ __dispatch1 = 16; continue dispatched_1; }
                        case 16:
                            __dispatch1 = 0;
                            if (((long)(int)(int)value.value != value.value))
                                return ;
                            break;
                        case 17:
                            if (((ty.value & 0xFF) == ENUMTY.Tuns32))
                            {
                            }
                            else if (((ty.value & 0xFF) == ENUMTY.Tuns64) && ((value.value & 2147483648L) != 0))
                                return ;
                            else if (((long)(int)value.value != value.value))
                                return ;
                            break;
                        case 18:
                            if (((ty.value & 0xFF) == ENUMTY.Tint32))
                            {
                            }
                            else if (((long)(int)value.value != value.value))
                                return ;
                            break;
                        case 33:
                            if ((value.value > 1114111L))
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
                            if (((ty.value & 0xFF) == ENUMTY.Tpointer) && ((e_ref.value.type.value.toBasetype().nextOf().ty & 0xFF) == (this.t.toBasetype().nextOf().ty & 0xFF)))
                            {
                                break;
                            }
                            /*goto default*/ { __dispatch1 = -3; continue dispatched_1; }
                        default:
                        __dispatch1 = 0;
                        this.visit((Expression)e_ref);
                        return ;
                    }
                } while(__dispatch1 != 0);
            }
            this.result = MATCH.convert;
        }

        public  void visit(ErrorExp e) {
        }

        public  void visit(NullExp e) {
            Ref<NullExp> e_ref = ref(e);
            if (e_ref.value.type.value.equals(this.t))
            {
                this.result = MATCH.exact;
                return ;
            }
            if (this.t.equivalent(e_ref.value.type.value))
            {
                this.result = MATCH.constant;
                return ;
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(StructLiteralExp e) {
            Ref<StructLiteralExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if ((this.result != MATCH.nomatch))
                return ;
            if (((e_ref.value.type.value.ty & 0xFF) == (this.t.ty & 0xFF)) && ((e_ref.value.type.value.ty & 0xFF) == ENUMTY.Tstruct) && (pequals(((TypeStruct)e_ref.value.type.value).sym, ((TypeStruct)this.t).sym)))
            {
                this.result = MATCH.constant;
                {
                    IntRef i = ref(0);
                    for (; (i.value < (e_ref.value.elements.get()).length);i.value++){
                        Ref<Expression> el = ref((e_ref.value.elements.get()).get(i.value));
                        if (el.value == null)
                            continue;
                        Ref<Type> te = ref(e_ref.value.sd.fields.get(i.value).type.addMod(this.t.mod));
                        IntRef m2 = ref(el.value.implicitConvTo(te.value));
                        if ((m2.value < this.result))
                            this.result = m2.value;
                    }
                }
            }
        }

        public  void visit(StringExp e) {
            Ref<StringExp> e_ref = ref(e);
            if ((e_ref.value.committed == 0) && ((this.t.ty & 0xFF) == ENUMTY.Tpointer) && ((this.t.nextOf().ty & 0xFF) == ENUMTY.Tvoid))
                return ;
            if (!(((e_ref.value.type.value.ty & 0xFF) == ENUMTY.Tsarray) || ((e_ref.value.type.value.ty & 0xFF) == ENUMTY.Tarray) || ((e_ref.value.type.value.ty & 0xFF) == ENUMTY.Tpointer)))
                this.visit((Expression)e_ref);
                return ;
            Ref<Byte> tyn = ref(e_ref.value.type.value.nextOf().ty);
            if (!(((tyn.value & 0xFF) == ENUMTY.Tchar) || ((tyn.value & 0xFF) == ENUMTY.Twchar) || ((tyn.value & 0xFF) == ENUMTY.Tdchar)))
                this.visit((Expression)e_ref);
                return ;
            switch ((this.t.ty & 0xFF))
            {
                case 1:
                    if (((e_ref.value.type.value.ty & 0xFF) == ENUMTY.Tsarray))
                    {
                        Ref<Byte> tynto = ref(this.t.nextOf().ty);
                        if (((tynto.value & 0xFF) == (tyn.value & 0xFF)))
                        {
                            if ((((TypeSArray)e_ref.value.type.value).dim.toInteger() == ((TypeSArray)this.t).dim.toInteger()))
                            {
                                this.result = MATCH.exact;
                            }
                            return ;
                        }
                        if (((tynto.value & 0xFF) == ENUMTY.Tchar) || ((tynto.value & 0xFF) == ENUMTY.Twchar) || ((tynto.value & 0xFF) == ENUMTY.Tdchar))
                        {
                            if ((e_ref.value.committed != 0) && ((tynto.value & 0xFF) != (tyn.value & 0xFF)))
                                return ;
                            IntRef fromlen = ref(e_ref.value.numberOfCodeUnits((tynto.value & 0xFF)));
                            IntRef tolen = ref((int)((TypeSArray)this.t).dim.toInteger());
                            if ((tolen.value < fromlen.value))
                                return ;
                            if ((tolen.value != fromlen.value))
                            {
                                this.result = MATCH.convert;
                                return ;
                            }
                        }
                        if ((e_ref.value.committed == 0) && ((tynto.value & 0xFF) == ENUMTY.Tchar) || ((tynto.value & 0xFF) == ENUMTY.Twchar) || ((tynto.value & 0xFF) == ENUMTY.Tdchar))
                        {
                            this.result = MATCH.exact;
                            return ;
                        }
                    }
                    else if (((e_ref.value.type.value.ty & 0xFF) == ENUMTY.Tarray))
                    {
                        Ref<Byte> tynto_1 = ref(this.t.nextOf().ty);
                        if (((tynto_1.value & 0xFF) == ENUMTY.Tchar) || ((tynto_1.value & 0xFF) == ENUMTY.Twchar) || ((tynto_1.value & 0xFF) == ENUMTY.Tdchar))
                        {
                            if ((e_ref.value.committed != 0) && ((tynto_1.value & 0xFF) != (tyn.value & 0xFF)))
                                return ;
                            IntRef fromlen_1 = ref(e_ref.value.numberOfCodeUnits((tynto_1.value & 0xFF)));
                            IntRef tolen_1 = ref((int)((TypeSArray)this.t).dim.toInteger());
                            if ((tolen_1.value < fromlen_1.value))
                                return ;
                            if ((tolen_1.value != fromlen_1.value))
                            {
                                this.result = MATCH.convert;
                                return ;
                            }
                        }
                        if (((tynto_1.value & 0xFF) == (tyn.value & 0xFF)))
                        {
                            this.result = MATCH.exact;
                            return ;
                        }
                        if ((e_ref.value.committed == 0) && ((tynto_1.value & 0xFF) == ENUMTY.Tchar) || ((tynto_1.value & 0xFF) == ENUMTY.Twchar) || ((tynto_1.value & 0xFF) == ENUMTY.Tdchar))
                        {
                            this.result = MATCH.exact;
                            return ;
                        }
                    }
                case 0:
                case 3:
                    Ref<Type> tn = ref(this.t.nextOf());
                    IntRef m = ref(MATCH.exact);
                    if (((e_ref.value.type.value.nextOf().mod & 0xFF) != (tn.value.mod & 0xFF)))
                    {
                        if (!tn.value.isConst() && !tn.value.isImmutable())
                            return ;
                        m.value = MATCH.constant;
                    }
                    if (e_ref.value.committed == 0)
                    {
                        switch ((tn.value.ty & 0xFF))
                        {
                            case 31:
                                if (((e_ref.value.postfix & 0xFF) == 119) || ((e_ref.value.postfix & 0xFF) == 100))
                                    m.value = MATCH.convert;
                                this.result = m.value;
                                return ;
                            case 32:
                                if (((e_ref.value.postfix & 0xFF) != 119))
                                    m.value = MATCH.convert;
                                this.result = m.value;
                                return ;
                            case 33:
                                if (((e_ref.value.postfix & 0xFF) != 100))
                                    m.value = MATCH.convert;
                                this.result = m.value;
                                return ;
                            case 9:
                                if (((TypeEnum)tn.value).sym.isSpecial())
                                {
                                    {
                                        Ref<TypeBasic> tob = ref(tn.value.toBasetype().isTypeBasic());
                                        if ((tob.value) != null)
                                            this.result = tn.value.implicitConvTo(tob.value);
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
            this.visit((Expression)e_ref);
        }

        public  void visit(ArrayLiteralExp e) {
            Ref<ArrayLiteralExp> e_ref = ref(e);
            Ref<Type> tb = ref(this.t.toBasetype());
            Ref<Type> typeb = ref(e_ref.value.type.value.toBasetype());
            if (((tb.value.ty & 0xFF) == ENUMTY.Tarray) || ((tb.value.ty & 0xFF) == ENUMTY.Tsarray) && ((typeb.value.ty & 0xFF) == ENUMTY.Tarray) || ((typeb.value.ty & 0xFF) == ENUMTY.Tsarray))
            {
                this.result = MATCH.exact;
                Ref<Type> typen = ref(typeb.value.nextOf().toBasetype());
                if (((tb.value.ty & 0xFF) == ENUMTY.Tsarray))
                {
                    Ref<TypeSArray> tsa = ref((TypeSArray)tb.value);
                    if (((long)(e_ref.value.elements.get()).length != tsa.value.dim.toInteger()))
                        this.result = MATCH.nomatch;
                }
                Ref<Type> telement = ref(tb.value.nextOf());
                if ((e_ref.value.elements.get()).length == 0)
                {
                    if (((typen.value.ty & 0xFF) != ENUMTY.Tvoid))
                        this.result = typen.value.implicitConvTo(telement.value);
                }
                else
                {
                    if (e_ref.value.basis != null)
                    {
                        IntRef m = ref(e_ref.value.basis.implicitConvTo(telement.value));
                        if ((m.value < this.result))
                            this.result = m.value;
                    }
                    {
                        IntRef i = ref(0);
                        for (; (i.value < (e_ref.value.elements.get()).length);i.value++){
                            Ref<Expression> el = ref((e_ref.value.elements.get()).get(i.value));
                            if ((this.result == MATCH.nomatch))
                                break;
                            if (el.value == null)
                                continue;
                            IntRef m = ref(el.value.implicitConvTo(telement.value));
                            if ((m.value < this.result))
                                this.result = m.value;
                        }
                    }
                }
                if (this.result == 0)
                    this.result = e_ref.value.type.value.implicitConvTo(this.t);
                return ;
            }
            else if (((tb.value.ty & 0xFF) == ENUMTY.Tvector) && ((typeb.value.ty & 0xFF) == ENUMTY.Tarray) || ((typeb.value.ty & 0xFF) == ENUMTY.Tsarray))
            {
                this.result = MATCH.exact;
                Ref<TypeVector> tv = ref((TypeVector)tb.value);
                Ref<TypeSArray> tbase = ref((TypeSArray)tv.value.basetype);
                assert(((tbase.value.ty & 0xFF) == ENUMTY.Tsarray));
                IntRef edim = ref((e_ref.value.elements.get()).length);
                Ref<Long> tbasedim = ref(tbase.value.dim.toInteger());
                if (((long)edim.value > tbasedim.value))
                {
                    this.result = MATCH.nomatch;
                    return ;
                }
                Ref<Type> telement = ref(tv.value.elementType());
                if (((long)edim.value < tbasedim.value))
                {
                    Ref<Expression> el = ref(typeb.value.nextOf().defaultInitLiteral(e_ref.value.loc));
                    IntRef m = ref(el.value.implicitConvTo(telement.value));
                    if ((m.value < this.result))
                        this.result = m.value;
                }
                {
                    IntRef __key897 = ref(0);
                    IntRef __limit898 = ref(edim.value);
                    for (; (__key897.value < __limit898.value);__key897.value += 1) {
                        IntRef i = ref(__key897.value);
                        Ref<Expression> el = ref((e_ref.value.elements.get()).get(i.value));
                        IntRef m = ref(el.value.implicitConvTo(telement.value));
                        if ((m.value < this.result))
                            this.result = m.value;
                        if ((this.result == MATCH.nomatch))
                            break;
                    }
                }
                return ;
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(AssocArrayLiteralExp e) {
            Ref<AssocArrayLiteralExp> e_ref = ref(e);
            Ref<Type> tb = ref(this.t.toBasetype());
            Ref<Type> typeb = ref(e_ref.value.type.value.toBasetype());
            if (!(((tb.value.ty & 0xFF) == ENUMTY.Taarray) && ((typeb.value.ty & 0xFF) == ENUMTY.Taarray)))
                this.visit((Expression)e_ref);
                return ;
            this.result = MATCH.exact;
            {
                IntRef i = ref(0);
                for (; (i.value < (e_ref.value.keys.get()).length);i.value++){
                    Ref<Expression> el = ref((e_ref.value.keys.get()).get(i.value));
                    IntRef m = ref(el.value.implicitConvTo(((TypeAArray)tb.value).index));
                    if ((m.value < this.result))
                        this.result = m.value;
                    if ((this.result == MATCH.nomatch))
                        break;
                    el.value = (e_ref.value.values.get()).get(i.value);
                    m.value = el.value.implicitConvTo(tb.value.nextOf());
                    if ((m.value < this.result))
                        this.result = m.value;
                    if ((this.result == MATCH.nomatch))
                        break;
                }
            }
        }

        public  void visit(CallExp e) {
            Ref<CallExp> e_ref = ref(e);
            boolean LOG = false;
            this.visit((Expression)e_ref);
            if ((this.result != MATCH.nomatch))
                return ;
            if ((e_ref.value.f != null) && e_ref.value.f.isReturnIsolated() && !global.value.params.vsafe || (e_ref.value.f.isPure() >= PURE.strong) || (pequals(e_ref.value.f.ident, Id.dup.value)) && (pequals(e_ref.value.f.toParent2(), ClassDeclaration.object.value.toParent())))
            {
                this.result = e_ref.value.type.value.immutableOf().implicitConvTo(this.t);
                if ((this.result > MATCH.constant))
                    this.result = MATCH.constant;
                return ;
            }
            Ref<Type> tx = ref(e_ref.value.f != null ? e_ref.value.f.type : e_ref.value.e1.type.value);
            tx.value = tx.value.toBasetype();
            if (((tx.value.ty & 0xFF) != ENUMTY.Tfunction))
                return ;
            Ref<TypeFunction> tf = ref((TypeFunction)tx.value);
            if ((tf.value.purity == PURE.impure))
                return ;
            if ((e_ref.value.f != null) && e_ref.value.f.isNested())
                return ;
            if ((e_ref.value.type.value.immutableOf().implicitConvTo(this.t) < MATCH.constant) && (e_ref.value.type.value.addMod((byte)2).implicitConvTo(this.t) < MATCH.constant) && (e_ref.value.type.value.implicitConvTo(this.t.addMod((byte)2)) < MATCH.constant))
            {
                return ;
            }
            Ref<Type> tb = ref(this.t.toBasetype());
            Ref<Byte> mod = ref(tb.value.mod);
            if (tf.value.isref)
            {
            }
            else
            {
                Ref<Type> ti = ref(getIndirection(this.t));
                if (ti.value != null)
                    mod.value = ti.value.mod;
            }
            if (((mod.value & 0xFF) & MODFlags.wild) != 0)
                return ;
            IntRef nparams = ref(tf.value.parameterList.length());
            IntRef j = ref((((tf.value.linkage == LINK.d) && (tf.value.parameterList.varargs == VarArg.variadic)) ? 1 : 0));
            if (((e_ref.value.e1.op & 0xFF) == 27))
            {
                Ref<DotVarExp> dve = ref((DotVarExp)e_ref.value.e1);
                Ref<Type> targ = ref(dve.value.e1.type.value);
                if ((targ.value.constConv(targ.value.castMod(mod.value)) == MATCH.nomatch))
                    return ;
            }
            {
                IntRef i = ref(j.value);
                for (; (i.value < (e_ref.value.arguments.get()).length);i.value += 1){
                    Ref<Expression> earg = ref((e_ref.value.arguments.get()).get(i.value));
                    Ref<Type> targ = ref(earg.value.type.value.toBasetype());
                    if ((i.value - j.value < nparams.value))
                    {
                        Ref<Parameter> fparam = ref(tf.value.parameterList.get(i.value - j.value));
                        if ((fparam.value.storageClass & 8192L) != 0)
                            return ;
                        Ref<Type> tparam = ref(fparam.value.type);
                        if (tparam.value == null)
                            continue;
                        if ((fparam.value.storageClass & 2101248L) != 0)
                        {
                            if ((targ.value.constConv(tparam.value.castMod(mod.value)) == MATCH.nomatch))
                                return ;
                            continue;
                        }
                    }
                    if ((implicitMod(earg.value, targ.value, mod.value) == MATCH.nomatch))
                        return ;
                }
            }
            this.result = MATCH.constant;
        }

        public  void visit(AddrExp e) {
            Ref<AddrExp> e_ref = ref(e);
            this.result = e_ref.value.type.value.implicitConvTo(this.t);
            if ((this.result != MATCH.nomatch))
                return ;
            Ref<Type> tb = ref(this.t.toBasetype());
            Ref<Type> typeb = ref(e_ref.value.type.value.toBasetype());
            if (((e_ref.value.e1.op & 0xFF) == 214) && ((tb.value.ty & 0xFF) == ENUMTY.Tpointer) || ((tb.value.ty & 0xFF) == ENUMTY.Tdelegate) && ((tb.value.nextOf().ty & 0xFF) == ENUMTY.Tfunction))
            {
                Ref<OverExp> eo = ref((OverExp)e_ref.value.e1);
                Ref<FuncDeclaration> f = ref(null);
                {
                    IntRef i = ref(0);
                    for (; (i.value < eo.value.vars.a.length);i.value++){
                        Ref<Dsymbol> s = ref(eo.value.vars.a.get(i.value));
                        Ref<FuncDeclaration> f2 = ref(s.value.isFuncDeclaration());
                        assert(f2.value != null);
                        if (f2.value.overloadExactMatch(tb.value.nextOf()) != null)
                        {
                            if (f.value != null)
                            {
                                ScopeDsymbol.multiplyDefined(e_ref.value.loc, f.value, f2.value);
                            }
                            else
                                f.value = f2.value;
                            this.result = MATCH.exact;
                        }
                    }
                }
            }
            if (((e_ref.value.e1.op & 0xFF) == 26) && ((typeb.value.ty & 0xFF) == ENUMTY.Tpointer) && ((typeb.value.nextOf().ty & 0xFF) == ENUMTY.Tfunction) && ((tb.value.ty & 0xFF) == ENUMTY.Tpointer) && ((tb.value.nextOf().ty & 0xFF) == ENUMTY.Tfunction))
            {
                throw new AssertionError("Unreachable code!");
            }
        }

        public  void visit(SymOffExp e) {
            Ref<SymOffExp> e_ref = ref(e);
            this.result = e_ref.value.type.value.implicitConvTo(this.t);
            if ((this.result != MATCH.nomatch))
                return ;
            Ref<Type> tb = ref(this.t.toBasetype());
            Ref<Type> typeb = ref(e_ref.value.type.value.toBasetype());
            if (((typeb.value.ty & 0xFF) == ENUMTY.Tpointer) && ((typeb.value.nextOf().ty & 0xFF) == ENUMTY.Tfunction) && ((tb.value.ty & 0xFF) == ENUMTY.Tpointer) || ((tb.value.ty & 0xFF) == ENUMTY.Tdelegate) && ((tb.value.nextOf().ty & 0xFF) == ENUMTY.Tfunction))
            {
                {
                    Ref<FuncDeclaration> f = ref(e_ref.value.var.isFuncDeclaration());
                    if ((f.value) != null)
                    {
                        f.value = f.value.overloadExactMatch(tb.value.nextOf());
                        if (f.value != null)
                        {
                            if (((tb.value.ty & 0xFF) == ENUMTY.Tdelegate) && f.value.needThis() || f.value.isNested() || ((tb.value.ty & 0xFF) == ENUMTY.Tpointer) && !(f.value.needThis() || f.value.isNested()))
                            {
                                this.result = MATCH.exact;
                            }
                        }
                    }
                }
            }
        }

        public  void visit(DelegateExp e) {
            Ref<DelegateExp> e_ref = ref(e);
            this.result = e_ref.value.type.value.implicitConvTo(this.t);
            if ((this.result != MATCH.nomatch))
                return ;
            Ref<Type> tb = ref(this.t.toBasetype());
            Ref<Type> typeb = ref(e_ref.value.type.value.toBasetype());
            if (((typeb.value.ty & 0xFF) == ENUMTY.Tdelegate) && ((tb.value.ty & 0xFF) == ENUMTY.Tdelegate))
            {
                if ((e_ref.value.func != null) && (e_ref.value.func.overloadExactMatch(tb.value.nextOf()) != null))
                    this.result = MATCH.exact;
            }
        }

        public  void visit(FuncExp e) {
            Ref<FuncExp> e_ref = ref(e);
            IntRef m = ref(e_ref.value.matchType(this.t, null, null, 1));
            if ((m.value > MATCH.nomatch))
            {
                this.result = m.value;
                return ;
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(AndExp e) {
            Ref<AndExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if ((this.result != MATCH.nomatch))
                return ;
            IntRef m1 = ref(e_ref.value.e1.value.implicitConvTo(this.t));
            IntRef m2 = ref(e_ref.value.e2.value.implicitConvTo(this.t));
            this.result = (m1.value < m2.value) ? m1.value : m2.value;
        }

        public  void visit(OrExp e) {
            Ref<OrExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if ((this.result != MATCH.nomatch))
                return ;
            IntRef m1 = ref(e_ref.value.e1.value.implicitConvTo(this.t));
            IntRef m2 = ref(e_ref.value.e2.value.implicitConvTo(this.t));
            this.result = (m1.value < m2.value) ? m1.value : m2.value;
        }

        public  void visit(XorExp e) {
            Ref<XorExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if ((this.result != MATCH.nomatch))
                return ;
            IntRef m1 = ref(e_ref.value.e1.value.implicitConvTo(this.t));
            IntRef m2 = ref(e_ref.value.e2.value.implicitConvTo(this.t));
            this.result = (m1.value < m2.value) ? m1.value : m2.value;
        }

        public  void visit(CondExp e) {
            Ref<CondExp> e_ref = ref(e);
            IntRef m1 = ref(e_ref.value.e1.value.implicitConvTo(this.t));
            IntRef m2 = ref(e_ref.value.e2.value.implicitConvTo(this.t));
            this.result = (m1.value < m2.value) ? m1.value : m2.value;
        }

        public  void visit(CommaExp e) {
            Ref<CommaExp> e_ref = ref(e);
            e_ref.value.e2.value.accept(this);
        }

        public  void visit(CastExp e) {
            Ref<CastExp> e_ref = ref(e);
            this.result = e_ref.value.type.value.implicitConvTo(this.t);
            if ((this.result != MATCH.nomatch))
                return ;
            if (this.t.isintegral() && e_ref.value.e1.type.value.isintegral() && (e_ref.value.e1.implicitConvTo(this.t) != MATCH.nomatch))
                this.result = MATCH.convert;
            else
                this.visit((Expression)e_ref);
        }

        public  void visit(NewExp e) {
            Ref<NewExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if ((this.result != MATCH.nomatch))
                return ;
            if ((e_ref.value.type.value.immutableOf().implicitConvTo(this.t.immutableOf()) == MATCH.nomatch))
                return ;
            Ref<Type> tb = ref(this.t.toBasetype());
            Ref<Byte> mod = ref(tb.value.mod);
            {
                Ref<Type> ti = ref(getIndirection(this.t));
                if ((ti.value) != null)
                    mod.value = ti.value.mod;
            }
            if (((mod.value & 0xFF) & MODFlags.wild) != 0)
                return ;
            if (e_ref.value.thisexp != null)
            {
                Ref<Type> targ = ref(e_ref.value.thisexp.type.value);
                if ((targ.value.constConv(targ.value.castMod(mod.value)) == MATCH.nomatch))
                    return ;
            }
            Ref<FuncDeclaration> fd = ref(e_ref.value.allocator);
            {
                IntRef count = ref(0);
                for (; (count.value < 2);comma(count.value += 1, fd.value = e_ref.value.member)){
                    if (fd.value == null)
                        continue;
                    if (fd.value.errors || ((fd.value.type.ty & 0xFF) != ENUMTY.Tfunction))
                        return ;
                    Ref<TypeFunction> tf = ref((TypeFunction)fd.value.type);
                    if ((tf.value.purity == PURE.impure))
                        return ;
                    if ((pequals(fd.value, e_ref.value.member)))
                    {
                        if ((e_ref.value.type.value.immutableOf().implicitConvTo(this.t) < MATCH.constant) && (e_ref.value.type.value.addMod((byte)2).implicitConvTo(this.t) < MATCH.constant) && (e_ref.value.type.value.implicitConvTo(this.t.addMod((byte)2)) < MATCH.constant))
                        {
                            return ;
                        }
                    }
                    Ref<Ptr<DArray<Expression>>> args = ref((pequals(fd.value, e_ref.value.allocator)) ? e_ref.value.newargs : e_ref.value.arguments);
                    IntRef nparams = ref(tf.value.parameterList.length());
                    IntRef j = ref((((tf.value.linkage == LINK.d) && (tf.value.parameterList.varargs == VarArg.variadic)) ? 1 : 0));
                    {
                        IntRef i = ref(j.value);
                        for (; (i.value < (e_ref.value.arguments.get()).length);i.value += 1){
                            Ref<Expression> earg = ref((args.value.get()).get(i.value));
                            Ref<Type> targ = ref(earg.value.type.value.toBasetype());
                            if ((i.value - j.value < nparams.value))
                            {
                                Ref<Parameter> fparam = ref(tf.value.parameterList.get(i.value - j.value));
                                if ((fparam.value.storageClass & 8192L) != 0)
                                    return ;
                                Ref<Type> tparam = ref(fparam.value.type);
                                if (tparam.value == null)
                                    continue;
                                if ((fparam.value.storageClass & 2101248L) != 0)
                                {
                                    if ((targ.value.constConv(tparam.value.castMod(mod.value)) == MATCH.nomatch))
                                        return ;
                                    continue;
                                }
                            }
                            if ((implicitMod(earg.value, targ.value, mod.value) == MATCH.nomatch))
                                return ;
                        }
                    }
                }
            }
            if ((e_ref.value.member == null) && (e_ref.value.arguments != null))
            {
                {
                    IntRef i = ref(0);
                    for (; (i.value < (e_ref.value.arguments.get()).length);i.value += 1){
                        Ref<Expression> earg = ref((e_ref.value.arguments.get()).get(i.value));
                        if (earg.value == null)
                            continue;
                        Ref<Type> targ = ref(earg.value.type.value.toBasetype());
                        if ((implicitMod(earg.value, targ.value, mod.value) == MATCH.nomatch))
                            return ;
                    }
                }
            }
            Ref<Type> ntb = ref(e_ref.value.newtype.toBasetype());
            if (((ntb.value.ty & 0xFF) == ENUMTY.Tarray))
                ntb.value = ntb.value.nextOf().toBasetype();
            if (((ntb.value.ty & 0xFF) == ENUMTY.Tstruct))
            {
                Ref<StructDeclaration> sd = ref(((TypeStruct)ntb.value).sym);
                sd.value.size(e_ref.value.loc);
                if (sd.value.isNested())
                    return ;
            }
            if (ntb.value.isZeroInit(e_ref.value.loc))
            {
                if (((ntb.value.ty & 0xFF) == ENUMTY.Tclass))
                {
                    Ref<ClassDeclaration> cd = ref(((TypeClass)ntb.value).sym);
                    cd.value.size(e_ref.value.loc);
                    if (cd.value.isNested())
                        return ;
                    assert(cd.value.isInterfaceDeclaration() == null);
                    if (!ClassCheck.convertible(e_ref.value.loc, cd.value, mod.value))
                        return ;
                }
            }
            else
            {
                Ref<Expression> earg = ref(e_ref.value.newtype.defaultInitLiteral(e_ref.value.loc));
                Ref<Type> targ = ref(e_ref.value.newtype.toBasetype());
                if ((implicitMod(earg.value, targ.value, mod.value) == MATCH.nomatch))
                    return ;
            }
            this.result = MATCH.constant;
        }

        public  void visit(SliceExp e) {
            Ref<SliceExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if ((this.result != MATCH.nomatch))
                return ;
            Ref<Type> tb = ref(this.t.toBasetype());
            Ref<Type> typeb = ref(e_ref.value.type.value.toBasetype());
            if (((tb.value.ty & 0xFF) == ENUMTY.Tsarray) && ((typeb.value.ty & 0xFF) == ENUMTY.Tarray))
            {
                typeb.value = toStaticArrayType(e_ref.value);
                if (typeb.value != null)
                    this.result = typeb.value.implicitConvTo(this.t);
                return ;
            }
            Ref<Type> t1b = ref(e_ref.value.e1.type.value.toBasetype());
            if (((tb.value.ty & 0xFF) == ENUMTY.Tarray) && typeb.value.equivalent(tb.value))
            {
                Ref<Type> tbn = ref(tb.value.nextOf());
                Ref<Type> tx = ref(null);
                if (((t1b.value.ty & 0xFF) == ENUMTY.Tarray))
                    tx.value = tbn.value.arrayOf();
                if (((t1b.value.ty & 0xFF) == ENUMTY.Tpointer))
                    tx.value = tbn.value.pointerTo();
                if (((t1b.value.ty & 0xFF) == ENUMTY.Tsarray) && !e_ref.value.e1.isLvalue())
                    tx.value = tbn.value.sarrayOf(t1b.value.size() / tbn.value.size());
                if (tx.value != null)
                {
                    this.result = e_ref.value.e1.implicitConvTo(tx.value);
                    if ((this.result > MATCH.constant))
                        this.result = MATCH.constant;
                }
            }
            if (((tb.value.ty & 0xFF) == ENUMTY.Tpointer) && ((e_ref.value.e1.op & 0xFF) == 121))
                e_ref.value.e1.accept(this);
        }


        public ImplicitConvTo() {}
    }
    static Ref<BytePtr> visitmsg = ref(new BytePtr("cannot form delegate due to covariant return type"));
    private static class CastTo extends Visitor
    {
        private Type t = null;
        private Ptr<Scope> sc = null;
        private Expression result = null;
        public  CastTo(Ptr<Scope> sc, Type t) {
            Ref<Ptr<Scope>> sc_ref = ref(sc);
            Ref<Type> t_ref = ref(t);
            this.sc = sc_ref.value;
            this.t = t_ref.value;
        }

        public  void visit(Expression e) {
            Ref<Expression> e_ref = ref(e);
            if (e_ref.value.type.value.equals(this.t))
            {
                this.result = e_ref.value;
                return ;
            }
            if (((e_ref.value.op & 0xFF) == 26))
            {
                Ref<VarDeclaration> v = ref(((VarExp)e_ref.value).var.isVarDeclaration());
                if ((v.value != null) && ((v.value.storage_class & 8388608L) != 0))
                {
                    this.result = e_ref.value.ctfeInterpret();
                    this.result.loc = e_ref.value.loc.copy();
                    this.result = this.result.castTo(this.sc, this.t);
                    return ;
                }
            }
            Ref<Type> tob = ref(this.t.toBasetype());
            Ref<Type> t1b = ref(e_ref.value.type.value.toBasetype());
            if (tob.value.equals(t1b.value))
            {
                this.result = e_ref.value.copy();
                this.result.type.value = this.t;
                return ;
            }
            boolean tob_isFV = ((tob.value.ty & 0xFF) == ENUMTY.Tstruct) || ((tob.value.ty & 0xFF) == ENUMTY.Tsarray);
            boolean t1b_isFV = ((t1b.value.ty & 0xFF) == ENUMTY.Tstruct) || ((t1b.value.ty & 0xFF) == ENUMTY.Tsarray);
            boolean tob_isFR = ((tob.value.ty & 0xFF) == ENUMTY.Tarray) || ((tob.value.ty & 0xFF) == ENUMTY.Tdelegate);
            boolean t1b_isFR = ((t1b.value.ty & 0xFF) == ENUMTY.Tarray) || ((t1b.value.ty & 0xFF) == ENUMTY.Tdelegate);
            boolean tob_isR = tob_isFR || ((tob.value.ty & 0xFF) == ENUMTY.Tpointer) || ((tob.value.ty & 0xFF) == ENUMTY.Taarray) || ((tob.value.ty & 0xFF) == ENUMTY.Tclass);
            boolean t1b_isR = t1b_isFR || ((t1b.value.ty & 0xFF) == ENUMTY.Tpointer) || ((t1b.value.ty & 0xFF) == ENUMTY.Taarray) || ((t1b.value.ty & 0xFF) == ENUMTY.Tclass);
            boolean tob_isA = tob.value.isintegral() || tob.value.isfloating();
            boolean t1b_isA = t1b.value.isintegral() || t1b.value.isfloating();
            Ref<Boolean> hasAliasThis = ref(false);
            try {
                {
                    Ref<AggregateDeclaration> t1ad = ref(isAggregate(t1b.value));
                    if ((t1ad.value) != null)
                    {
                        Ref<AggregateDeclaration> toad = ref(isAggregate(tob.value));
                        if ((!pequals(t1ad.value, toad.value)) && (t1ad.value.aliasthis != null))
                        {
                            if (((t1b.value.ty & 0xFF) == ENUMTY.Tclass) && ((tob.value.ty & 0xFF) == ENUMTY.Tclass))
                            {
                                Ref<ClassDeclaration> t1cd = ref(t1b.value.isClassHandle());
                                Ref<ClassDeclaration> tocd = ref(tob.value.isClassHandle());
                                IntRef offset = ref(0);
                                if (tocd.value.isBaseOf(t1cd.value, ptr(offset)))
                                    /*goto Lok*/throw Dispatch0.INSTANCE;
                            }
                            hasAliasThis.value = true;
                        }
                    }
                    else if (((tob.value.ty & 0xFF) == ENUMTY.Tvector) && ((t1b.value.ty & 0xFF) != ENUMTY.Tvector))
                    {
                        Ref<TypeVector> tv = ref((TypeVector)tob.value);
                        this.result = new CastExp(e_ref.value.loc, e_ref.value, tv.value.elementType());
                        this.result = new VectorExp(e_ref.value.loc, this.result, tob.value);
                        this.result = expressionSemantic(this.result, this.sc);
                        return ;
                    }
                    else if (((tob.value.ty & 0xFF) != ENUMTY.Tvector) && ((t1b.value.ty & 0xFF) == ENUMTY.Tvector))
                    {
                        if (((tob.value.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            if ((t1b.value.size(e_ref.value.loc) == tob.value.size(e_ref.value.loc)))
                                /*goto Lok*/throw Dispatch0.INSTANCE;
                        }
                        /*goto Lfail*//*unrolled goto*/
                    /*Lfail:*/
                        if (hasAliasThis.value)
                        {
                            this.result = tryAliasThisCast(e_ref.value, this.sc, tob.value, t1b.value, this.t);
                            if (this.result != null)
                                return ;
                        }
                        e_ref.value.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e_ref.value.toChars(), e_ref.value.type.value.toChars(), this.t.toChars());
                        this.result = new ErrorExp();
                        return ;
                    }
                    else if ((t1b.value.implicitConvTo(tob.value) == MATCH.constant) && this.t.equals(e_ref.value.type.value.constOf()))
                    {
                        this.result = e_ref.value.copy();
                        this.result.type.value = this.t;
                        return ;
                    }
                }
                if (tob_isA && t1b_isA || ((t1b.value.ty & 0xFF) == ENUMTY.Tpointer) || t1b_isA && tob_isA || ((tob.value.ty & 0xFF) == ENUMTY.Tpointer))
                {
                    /*goto Lok*/throw Dispatch0.INSTANCE;
                }
                if (tob_isA && t1b_isR || t1b_isFV || t1b_isA && tob_isR || tob_isFV)
                {
                    /*goto Lfail*//*unrolled goto*/
                /*Lfail:*/
                    if (hasAliasThis.value)
                    {
                        this.result = tryAliasThisCast(e_ref.value, this.sc, tob.value, t1b.value, this.t);
                        if (this.result != null)
                            return ;
                    }
                    e_ref.value.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e_ref.value.toChars(), e_ref.value.type.value.toChars(), this.t.toChars());
                    this.result = new ErrorExp();
                    return ;
                }
                if (tob_isFV && t1b_isFV)
                {
                    if (hasAliasThis.value)
                    {
                        this.result = tryAliasThisCast(e_ref.value, this.sc, tob.value, t1b.value, this.t);
                        if (this.result != null)
                            return ;
                    }
                    if ((t1b.value.size(e_ref.value.loc) == tob.value.size(e_ref.value.loc)))
                        /*goto Lok*/throw Dispatch0.INSTANCE;
                    Slice<BytePtr> ts = toAutoQualChars(e_ref.value.type.value, this.t);
                    e_ref.value.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s` because of different sizes"), e_ref.value.toChars(), ts.get(0), ts.get(1));
                    this.result = new ErrorExp();
                    return ;
                }
                if (tob_isFV && ((t1b.value.ty & 0xFF) == ENUMTY.Tnull) || t1b_isR || t1b_isFV && ((tob.value.ty & 0xFF) == ENUMTY.Tnull) || tob_isR)
                {
                    if (((tob.value.ty & 0xFF) == ENUMTY.Tpointer) && ((t1b.value.ty & 0xFF) == ENUMTY.Tsarray))
                    {
                        this.result = new AddrExp(e_ref.value.loc, e_ref.value, this.t);
                        return ;
                    }
                    if (((tob.value.ty & 0xFF) == ENUMTY.Tarray) && ((t1b.value.ty & 0xFF) == ENUMTY.Tsarray))
                    {
                        Ref<Long> fsize = ref(t1b.value.nextOf().size());
                        Ref<Long> tsize = ref(tob.value.nextOf().size());
                        if ((((TypeSArray)t1b.value).dim.toInteger() * fsize.value % tsize.value != 0L))
                        {
                            e_ref.value.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s` since sizes don't line up"), e_ref.value.toChars(), e_ref.value.type.value.toChars(), this.t.toChars());
                            this.result = new ErrorExp();
                            return ;
                        }
                        /*goto Lok*/throw Dispatch0.INSTANCE;
                    }
                    /*goto Lfail*//*unrolled goto*/
                /*Lfail:*/
                    if (hasAliasThis.value)
                    {
                        this.result = tryAliasThisCast(e_ref.value, this.sc, tob.value, t1b.value, this.t);
                        if (this.result != null)
                            return ;
                    }
                    e_ref.value.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e_ref.value.toChars(), e_ref.value.type.value.toChars(), this.t.toChars());
                    this.result = new ErrorExp();
                    return ;
                }
                if (((tob.value.ty & 0xFF) == (t1b.value.ty & 0xFF)) && tob_isR && t1b_isR)
                    /*goto Lok*/throw Dispatch0.INSTANCE;
                if (((tob.value.ty & 0xFF) == ENUMTY.Tnull) && ((t1b.value.ty & 0xFF) != ENUMTY.Tnull))
                    /*goto Lfail*//*unrolled goto*/
                /*Lfail:*/
                    if (hasAliasThis.value)
                    {
                        this.result = tryAliasThisCast(e_ref.value, this.sc, tob.value, t1b.value, this.t);
                        if (this.result != null)
                            return ;
                    }
                    e_ref.value.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e_ref.value.toChars(), e_ref.value.type.value.toChars(), this.t.toChars());
                    this.result = new ErrorExp();
                    return ;
                if (((t1b.value.ty & 0xFF) == ENUMTY.Tnull) && ((tob.value.ty & 0xFF) != ENUMTY.Tnull))
                    /*goto Lok*/throw Dispatch0.INSTANCE;
                if (tob_isFR && t1b_isR || t1b_isFR && tob_isR)
                {
                    if (((tob.value.ty & 0xFF) == ENUMTY.Tpointer) && ((t1b.value.ty & 0xFF) == ENUMTY.Tarray))
                    {
                        /*goto Lok*/throw Dispatch0.INSTANCE;
                    }
                    if (((tob.value.ty & 0xFF) == ENUMTY.Tpointer) && ((t1b.value.ty & 0xFF) == ENUMTY.Tdelegate))
                    {
                        e_ref.value.deprecation(new BytePtr("casting from %s to %s is deprecated"), e_ref.value.type.value.toChars(), this.t.toChars());
                        /*goto Lok*/throw Dispatch0.INSTANCE;
                    }
                    /*goto Lfail*//*unrolled goto*/
                /*Lfail:*/
                    if (hasAliasThis.value)
                    {
                        this.result = tryAliasThisCast(e_ref.value, this.sc, tob.value, t1b.value, this.t);
                        if (this.result != null)
                            return ;
                    }
                    e_ref.value.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e_ref.value.toChars(), e_ref.value.type.value.toChars(), this.t.toChars());
                    this.result = new ErrorExp();
                    return ;
                }
                if (((t1b.value.ty & 0xFF) == ENUMTY.Tvoid) && ((tob.value.ty & 0xFF) != ENUMTY.Tvoid))
                {
                /*Lfail:*/
                    if (hasAliasThis.value)
                    {
                        this.result = tryAliasThisCast(e_ref.value, this.sc, tob.value, t1b.value, this.t);
                        if (this.result != null)
                            return ;
                    }
                    e_ref.value.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e_ref.value.toChars(), e_ref.value.type.value.toChars(), this.t.toChars());
                    this.result = new ErrorExp();
                    return ;
                }
            }
            catch(Dispatch0 __d){}
        /*Lok:*/
            this.result = new CastExp(e_ref.value.loc, e_ref.value, this.t);
            this.result.type.value = this.t;
        }

        public  void visit(ErrorExp e) {
            Ref<ErrorExp> e_ref = ref(e);
            this.result = e_ref.value;
        }

        public  void visit(RealExp e) {
            Ref<RealExp> e_ref = ref(e);
            if (!e_ref.value.type.value.equals(this.t))
            {
                if (e_ref.value.type.value.isreal() && this.t.isreal() || e_ref.value.type.value.isimaginary() && this.t.isimaginary())
                {
                    this.result = e_ref.value.copy();
                    this.result.type.value = this.t;
                }
                else
                    this.visit((Expression)e_ref);
                return ;
            }
            this.result = e_ref.value;
        }

        public  void visit(ComplexExp e) {
            Ref<ComplexExp> e_ref = ref(e);
            if (!e_ref.value.type.value.equals(this.t))
            {
                if (e_ref.value.type.value.iscomplex() && this.t.iscomplex())
                {
                    this.result = e_ref.value.copy();
                    this.result.type.value = this.t;
                }
                else
                    this.visit((Expression)e_ref);
                return ;
            }
            this.result = e_ref.value;
        }

        public  void visit(NullExp e) {
            Ref<NullExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if (((this.result.op & 0xFF) == 13))
            {
                Ref<NullExp> ex = ref((NullExp)this.result);
                ex.value.committed = (byte)1;
                return ;
            }
        }

        public  void visit(StructLiteralExp e) {
            Ref<StructLiteralExp> e_ref = ref(e);
            this.visit((Expression)e_ref);
            if (((this.result.op & 0xFF) == 49))
                ((StructLiteralExp)this.result).stype = this.t;
        }

        public  void visit(StringExp e) {
            Ref<StringExp> e_ref = ref(e);
            IntRef copied = ref(0);
            if ((e_ref.value.committed == 0) && ((this.t.ty & 0xFF) == ENUMTY.Tpointer) && ((this.t.nextOf().ty & 0xFF) == ENUMTY.Tvoid))
            {
                e_ref.value.error(new BytePtr("cannot convert string literal to `void*`"));
                this.result = new ErrorExp();
                return ;
            }
            Ref<StringExp> se = ref(e_ref.value);
            if (e_ref.value.committed == 0)
            {
                se.value = (StringExp)e_ref.value.copy();
                se.value.committed = (byte)1;
                copied.value = 1;
            }
            if (e_ref.value.type.value.equals(this.t))
            {
                this.result = se.value;
                return ;
            }
            Ref<Type> tb = ref(this.t.toBasetype());
            Ref<Type> typeb = ref(e_ref.value.type.value.toBasetype());
            if (((tb.value.ty & 0xFF) == ENUMTY.Tdelegate) && ((typeb.value.ty & 0xFF) != ENUMTY.Tdelegate))
            {
                this.visit((Expression)e_ref);
                return ;
            }
            if (typeb.value.equals(tb.value))
            {
                if (copied.value == 0)
                {
                    se.value = (StringExp)e_ref.value.copy();
                    copied.value = 1;
                }
                se.value.type.value = this.t;
                this.result = se.value;
                return ;
            }
            if ((e_ref.value.committed != 0) && ((tb.value.ty & 0xFF) == ENUMTY.Tsarray) && ((typeb.value.ty & 0xFF) == ENUMTY.Tarray))
            {
                se.value = (StringExp)e_ref.value.copy();
                Ref<Long> szx = ref(tb.value.nextOf().size());
                assert((szx.value <= 255L));
                se.value.sz = (byte)szx.value;
                se.value.len = (int)((TypeSArray)tb.value).dim.toInteger();
                se.value.committed = (byte)1;
                se.value.type.value = this.t;
                IntRef fullSize = ref((se.value.len + 1) * (se.value.sz & 0xFF));
                if ((fullSize.value > (e_ref.value.len + 1) * (e_ref.value.sz & 0xFF)))
                {
                    Ref<Object> s = ref(pcopy(Mem.xmalloc(fullSize.value)));
                    IntRef srcSize = ref(e_ref.value.len * (e_ref.value.sz & 0xFF));
                    memcpy((BytePtr)s.value, (se.value.string), srcSize.value);
                    memset(((BytePtr)s.value).plus(srcSize.value), 0, fullSize.value - srcSize.value);
                    se.value.string = pcopy((((BytePtr)s.value)));
                }
                this.result = se.value;
                return ;
            }
            try {
                try {
                    if (((tb.value.ty & 0xFF) != ENUMTY.Tsarray) && ((tb.value.ty & 0xFF) != ENUMTY.Tarray) && ((tb.value.ty & 0xFF) != ENUMTY.Tpointer))
                    {
                        if (copied.value == 0)
                        {
                            se.value = (StringExp)e_ref.value.copy();
                            copied.value = 1;
                        }
                        /*goto Lcast*/throw Dispatch1.INSTANCE;
                    }
                    if (((typeb.value.ty & 0xFF) != ENUMTY.Tsarray) && ((typeb.value.ty & 0xFF) != ENUMTY.Tarray) && ((typeb.value.ty & 0xFF) != ENUMTY.Tpointer))
                    {
                        if (copied.value == 0)
                        {
                            se.value = (StringExp)e_ref.value.copy();
                            copied.value = 1;
                        }
                        /*goto Lcast*/throw Dispatch1.INSTANCE;
                    }
                    if ((typeb.value.nextOf().size() == tb.value.nextOf().size()))
                    {
                        if (copied.value == 0)
                        {
                            se.value = (StringExp)e_ref.value.copy();
                            copied.value = 1;
                        }
                        if (((tb.value.ty & 0xFF) == ENUMTY.Tsarray))
                            /*goto L2*/throw Dispatch0.INSTANCE;
                        se.value.type.value = this.t;
                        this.result = se.value;
                        return ;
                    }
                    if (e_ref.value.committed != 0)
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
                        Ref<OutBuffer> buffer = ref(new OutBuffer());
                        try {
                            IntRef newlen = ref(0);
                            IntRef tfty = ref((typeb.value.nextOf().toBasetype().ty & 0xFF));
                            IntRef ttty = ref((tb.value.nextOf().toBasetype().ty & 0xFF));
                            {
                                int __dispatch4 = 0;
                                dispatched_4:
                                do {
                                    switch (__dispatch4 != 0 ? __dispatch4 : XIntegerInteger.invoke(tfty.value, ttty.value))
                                    {
                                        case 7967:
                                        case 8224:
                                        case 8481:
                                            break;
                                        case 7968:
                                            {
                                                IntRef u = ref(0);
                                                for (; (u.value < e_ref.value.len);){
                                                    IntRef c = ref(0x0ffff);
                                                    Ref<BytePtr> p = ref(pcopy(utf_decodeChar(se.value.string, e_ref.value.len, u, c)));
                                                    if (p.value != null)
                                                        e_ref.value.error(new BytePtr("%s"), p.value);
                                                    else
                                                        buffer.value.writeUTF16(c.value);
                                                }
                                            }
                                            newlen.value = buffer.value.offset / 2;
                                            buffer.value.writeUTF16(0);
                                            /*goto L1*/{ __dispatch4 = -1; continue dispatched_4; }
                                        case 7969:
                                            {
                                                IntRef u_1 = ref(0);
                                                for (; (u_1.value < e_ref.value.len);){
                                                    IntRef c_1 = ref(0x0ffff);
                                                    Ref<BytePtr> p_1 = ref(pcopy(utf_decodeChar(se.value.string, e_ref.value.len, u_1, c_1)));
                                                    if (p_1.value != null)
                                                        e_ref.value.error(new BytePtr("%s"), p_1.value);
                                                    buffer.value.write4(c_1.value);
                                                    newlen.value++;
                                                }
                                            }
                                            buffer.value.write4(0);
                                            /*goto L1*/{ __dispatch4 = -1; continue dispatched_4; }
                                        case 8223:
                                            {
                                                IntRef u_2 = ref(0);
                                                for (; (u_2.value < e_ref.value.len);){
                                                    IntRef c_2 = ref(0x0ffff);
                                                    Ref<BytePtr> p_2 = ref(pcopy(utf_decodeWchar(se.value.wstring, e_ref.value.len, u_2, c_2)));
                                                    if (p_2.value != null)
                                                        e_ref.value.error(new BytePtr("%s"), p_2.value);
                                                    else
                                                        buffer.value.writeUTF8(c_2.value);
                                                }
                                            }
                                            newlen.value = buffer.value.offset;
                                            buffer.value.writeUTF8(0);
                                            /*goto L1*/{ __dispatch4 = -1; continue dispatched_4; }
                                        case 8225:
                                            {
                                                IntRef u_3 = ref(0);
                                                for (; (u_3.value < e_ref.value.len);){
                                                    IntRef c_3 = ref(0x0ffff);
                                                    Ref<BytePtr> p_3 = ref(pcopy(utf_decodeWchar(se.value.wstring, e_ref.value.len, u_3, c_3)));
                                                    if (p_3.value != null)
                                                        e_ref.value.error(new BytePtr("%s"), p_3.value);
                                                    buffer.value.write4(c_3.value);
                                                    newlen.value++;
                                                }
                                            }
                                            buffer.value.write4(0);
                                            /*goto L1*/{ __dispatch4 = -1; continue dispatched_4; }
                                        case 8479:
                                            {
                                                IntRef u_4 = ref(0);
                                                for (; (u_4.value < e_ref.value.len);u_4.value++){
                                                    IntRef c_4 = ref(se.value.dstring.get(u_4.value));
                                                    if (!utf_isValidDchar(c_4.value))
                                                        e_ref.value.error(new BytePtr("invalid UCS-32 char \\U%08x"), c_4.value);
                                                    else
                                                        buffer.value.writeUTF8(c_4.value);
                                                    newlen.value++;
                                                }
                                            }
                                            newlen.value = buffer.value.offset;
                                            buffer.value.writeUTF8(0);
                                            /*goto L1*/{ __dispatch4 = -1; continue dispatched_4; }
                                        case 8480:
                                            {
                                                IntRef u_5 = ref(0);
                                                for (; (u_5.value < e_ref.value.len);u_5.value++){
                                                    IntRef c_5 = ref(se.value.dstring.get(u_5.value));
                                                    if (!utf_isValidDchar(c_5.value))
                                                        e_ref.value.error(new BytePtr("invalid UCS-32 char \\U%08x"), c_5.value);
                                                    else
                                                        buffer.value.writeUTF16(c_5.value);
                                                    newlen.value++;
                                                }
                                            }
                                            newlen.value = buffer.value.offset / 2;
                                            buffer.value.writeUTF16(0);
                                            /*goto L1*/{ __dispatch4 = -1; continue dispatched_4; }
                                        /*L1:*/
                                        case -1:
                                        __dispatch4 = 0;
                                            if (copied.value == 0)
                                            {
                                                se.value = (StringExp)e_ref.value.copy();
                                                copied.value = 1;
                                            }
                                            se.value.string = pcopy(buffer.value.extractData());
                                            se.value.len = newlen.value;
                                            {
                                                Ref<Long> szx = ref(tb.value.nextOf().size());
                                                assert((szx.value <= 255L));
                                                se.value.sz = (byte)szx.value;
                                            }
                                            break;
                                        default:
                                        assert((typeb.value.nextOf().size() != tb.value.nextOf().size()));
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
                assert(copied.value != 0);
                if (((tb.value.ty & 0xFF) == ENUMTY.Tsarray))
                {
                    IntRef dim2 = ref((int)((TypeSArray)tb.value).dim.toInteger());
                    if ((dim2.value != se.value.len))
                    {
                        IntRef newsz = ref((se.value.sz & 0xFF));
                        IntRef d = ref((dim2.value < se.value.len) ? dim2.value : se.value.len);
                        Ref<Object> s = ref(pcopy(Mem.xmalloc((dim2.value + 1) * newsz.value)));
                        memcpy((BytePtr)s.value, (se.value.string), (d.value * newsz.value));
                        memset(((BytePtr)s.value).plus((d.value * newsz.value)), 0, (dim2.value + 1 - d.value) * newsz.value);
                        se.value.string = pcopy((((BytePtr)s.value)));
                        se.value.len = dim2.value;
                    }
                }
                se.value.type.value = this.t;
                this.result = se.value;
                return ;
            }
            catch(Dispatch1 __d){}
        /*Lcast:*/
            this.result = new CastExp(e_ref.value.loc, se.value, this.t);
            this.result.type.value = this.t;
        }

        public  void visit(AddrExp e) {
            Ref<AddrExp> e_ref = ref(e);
            this.result = e_ref.value;
            Ref<Type> tb = ref(this.t.toBasetype());
            Ref<Type> typeb = ref(e_ref.value.type.value.toBasetype());
            if (tb.value.equals(typeb.value))
            {
                this.result = e_ref.value.copy();
                this.result.type.value = this.t;
                return ;
            }
            if (((e_ref.value.e1.op & 0xFF) == 214) && ((tb.value.ty & 0xFF) == ENUMTY.Tpointer) || ((tb.value.ty & 0xFF) == ENUMTY.Tdelegate) && ((tb.value.nextOf().ty & 0xFF) == ENUMTY.Tfunction))
            {
                Ref<OverExp> eo = ref((OverExp)e_ref.value.e1);
                Ref<FuncDeclaration> f = ref(null);
                {
                    IntRef i = ref(0);
                    for (; (i.value < eo.value.vars.a.length);i.value++){
                        Ref<Dsymbol> s = ref(eo.value.vars.a.get(i.value));
                        Ref<FuncDeclaration> f2 = ref(s.value.isFuncDeclaration());
                        assert(f2.value != null);
                        if (f2.value.overloadExactMatch(tb.value.nextOf()) != null)
                        {
                            if (f.value != null)
                            {
                                ScopeDsymbol.multiplyDefined(e_ref.value.loc, f.value, f2.value);
                            }
                            else
                                f.value = f2.value;
                        }
                    }
                }
                if (f.value != null)
                {
                    f.value.tookAddressOf++;
                    Ref<SymOffExp> se = ref(new SymOffExp(e_ref.value.loc, f.value, 0L, false));
                    expressionSemantic(se.value, this.sc);
                    this.visit(se.value);
                    return ;
                }
            }
            if (((e_ref.value.e1.op & 0xFF) == 26) && ((typeb.value.ty & 0xFF) == ENUMTY.Tpointer) && ((typeb.value.nextOf().ty & 0xFF) == ENUMTY.Tfunction) && ((tb.value.ty & 0xFF) == ENUMTY.Tpointer) && ((tb.value.nextOf().ty & 0xFF) == ENUMTY.Tfunction))
            {
                Ref<VarExp> ve = ref((VarExp)e_ref.value.e1);
                Ref<FuncDeclaration> f = ref(ve.value.var.isFuncDeclaration());
                if (f.value != null)
                {
                    assert(f.value.isImportedSymbol());
                    f.value = f.value.overloadExactMatch(tb.value.nextOf());
                    if (f.value != null)
                    {
                        this.result = new VarExp(e_ref.value.loc, f.value, false);
                        this.result.type.value = f.value.type;
                        this.result = new AddrExp(e_ref.value.loc, this.result, this.t);
                        return ;
                    }
                }
            }
            {
                Ref<FuncDeclaration> f = ref(isFuncAddress(e_ref.value, null));
                if ((f.value) != null)
                {
                    if (f.value.checkForwardRef(e_ref.value.loc))
                    {
                        this.result = new ErrorExp();
                        return ;
                    }
                }
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(TupleExp e) {
            Ref<TupleExp> e_ref = ref(e);
            if (e_ref.value.type.value.equals(this.t))
            {
                this.result = e_ref.value;
                return ;
            }
            Ref<TupleExp> te = ref((TupleExp)e_ref.value.copy());
            te.value.e0 = e_ref.value.e0 != null ? e_ref.value.e0.copy() : null;
            te.value.exps = (e_ref.value.exps.get()).copy();
            {
                IntRef i = ref(0);
                for (; (i.value < (te.value.exps.get()).length);i.value++){
                    Ref<Expression> ex = ref((te.value.exps.get()).get(i.value));
                    ex.value = ex.value.castTo(this.sc, this.t);
                    te.value.exps.get().set(i.value, ex.value);
                }
            }
            this.result = te.value;
        }

        public  void visit(ArrayLiteralExp e) {
            Ref<ArrayLiteralExp> e_ref = ref(e);
            Ref<ArrayLiteralExp> ae = ref(e_ref.value);
            Ref<Type> tb = ref(this.t.toBasetype());
            if (((tb.value.ty & 0xFF) == ENUMTY.Tarray) && global.value.params.vsafe)
            {
                if (checkArrayLiteralEscape(this.sc, ae.value, false))
                {
                    this.result = new ErrorExp();
                    return ;
                }
            }
            if ((pequals(e_ref.value.type.value, this.t)))
            {
                this.result = e_ref.value;
                return ;
            }
            Ref<Type> typeb = ref(e_ref.value.type.value.toBasetype());
            try {
                if (((tb.value.ty & 0xFF) == ENUMTY.Tarray) || ((tb.value.ty & 0xFF) == ENUMTY.Tsarray) && ((typeb.value.ty & 0xFF) == ENUMTY.Tarray) || ((typeb.value.ty & 0xFF) == ENUMTY.Tsarray))
                {
                    if (((tb.value.nextOf().toBasetype().ty & 0xFF) == ENUMTY.Tvoid) && ((typeb.value.nextOf().toBasetype().ty & 0xFF) != ENUMTY.Tvoid))
                    {
                    }
                    else if (((typeb.value.ty & 0xFF) == ENUMTY.Tsarray) && ((typeb.value.nextOf().toBasetype().ty & 0xFF) == ENUMTY.Tvoid))
                    {
                    }
                    else
                    {
                        if (((tb.value.ty & 0xFF) == ENUMTY.Tsarray))
                        {
                            Ref<TypeSArray> tsa = ref((TypeSArray)tb.value);
                            if (((long)(e_ref.value.elements.get()).length != tsa.value.dim.toInteger()))
                                /*goto L1*/throw Dispatch0.INSTANCE;
                        }
                        ae.value = (ArrayLiteralExp)e_ref.value.copy();
                        if (e_ref.value.basis != null)
                            ae.value.basis = e_ref.value.basis.castTo(this.sc, tb.value.nextOf());
                        ae.value.elements = (e_ref.value.elements.get()).copy();
                        {
                            IntRef i = ref(0);
                            for (; (i.value < (e_ref.value.elements.get()).length);i.value++){
                                Ref<Expression> ex = ref((e_ref.value.elements.get()).get(i.value));
                                if (ex.value == null)
                                    continue;
                                ex.value = ex.value.castTo(this.sc, tb.value.nextOf());
                                ae.value.elements.get().set(i.value, ex.value);
                            }
                        }
                        ae.value.type.value = this.t;
                        this.result = ae.value;
                        return ;
                    }
                }
                else if (((tb.value.ty & 0xFF) == ENUMTY.Tpointer) && ((typeb.value.ty & 0xFF) == ENUMTY.Tsarray))
                {
                    Ref<Type> tp = ref(typeb.value.nextOf().pointerTo());
                    if (!tp.value.equals(ae.value.type.value))
                    {
                        ae.value = (ArrayLiteralExp)e_ref.value.copy();
                        ae.value.type.value = tp.value;
                    }
                }
                else if (((tb.value.ty & 0xFF) == ENUMTY.Tvector) && ((typeb.value.ty & 0xFF) == ENUMTY.Tarray) || ((typeb.value.ty & 0xFF) == ENUMTY.Tsarray))
                {
                    Ref<TypeVector> tv = ref((TypeVector)tb.value);
                    Ref<TypeSArray> tbase = ref((TypeSArray)tv.value.basetype);
                    assert(((tbase.value.ty & 0xFF) == ENUMTY.Tsarray));
                    IntRef edim = ref((e_ref.value.elements.get()).length);
                    Ref<Long> tbasedim = ref(tbase.value.dim.toInteger());
                    if (((long)edim.value > tbasedim.value))
                        /*goto L1*/throw Dispatch0.INSTANCE;
                    ae.value = (ArrayLiteralExp)e_ref.value.copy();
                    ae.value.type.value = tbase.value;
                    ae.value.elements = (e_ref.value.elements.get()).copy();
                    Ref<Type> telement = ref(tv.value.elementType());
                    {
                        IntRef __key899 = ref(0);
                        IntRef __limit900 = ref(edim.value);
                        for (; (__key899.value < __limit900.value);__key899.value += 1) {
                            IntRef i = ref(__key899.value);
                            Ref<Expression> ex = ref((e_ref.value.elements.get()).get(i.value));
                            ex.value = ex.value.castTo(this.sc, telement.value);
                            ae.value.elements.get().set(i.value, ex.value);
                        }
                    }
                    (ae.value.elements.get()).setDim((int)tbasedim.value);
                    {
                        IntRef __key901 = ref(edim.value);
                        IntRef __limit902 = ref((int)tbasedim.value);
                        for (; (__key901.value < __limit902.value);__key901.value += 1) {
                            IntRef i = ref(__key901.value);
                            Ref<Expression> ex = ref(typeb.value.nextOf().defaultInitLiteral(e_ref.value.loc));
                            ex.value = ex.value.castTo(this.sc, telement.value);
                            ae.value.elements.get().set(i.value, ex.value);
                        }
                    }
                    Ref<Expression> ev = ref(new VectorExp(e_ref.value.loc, ae.value, tb.value));
                    ev.value = expressionSemantic(ev.value, this.sc);
                    this.result = ev.value;
                    return ;
                }
            }
            catch(Dispatch0 __d){}
        /*L1:*/
            this.visit((Expression)ae);
        }

        public  void visit(AssocArrayLiteralExp e) {
            Ref<AssocArrayLiteralExp> e_ref = ref(e);
            if ((pequals(e_ref.value.type.value, this.t)))
            {
                this.result = e_ref.value;
                return ;
            }
            Ref<Type> tb = ref(this.t.toBasetype());
            Ref<Type> typeb = ref(e_ref.value.type.value.toBasetype());
            if (((tb.value.ty & 0xFF) == ENUMTY.Taarray) && ((typeb.value.ty & 0xFF) == ENUMTY.Taarray) && ((tb.value.nextOf().toBasetype().ty & 0xFF) != ENUMTY.Tvoid))
            {
                Ref<AssocArrayLiteralExp> ae = ref((AssocArrayLiteralExp)e_ref.value.copy());
                ae.value.keys = (e_ref.value.keys.get()).copy();
                ae.value.values = (e_ref.value.values.get()).copy();
                assert(((e_ref.value.keys.get()).length == (e_ref.value.values.get()).length));
                {
                    IntRef i = ref(0);
                    for (; (i.value < (e_ref.value.keys.get()).length);i.value++){
                        Ref<Expression> ex = ref((e_ref.value.values.get()).get(i.value));
                        ex.value = ex.value.castTo(this.sc, tb.value.nextOf());
                        ae.value.values.get().set(i.value, ex.value);
                        ex.value = (e_ref.value.keys.get()).get(i.value);
                        ex.value = ex.value.castTo(this.sc, ((TypeAArray)tb.value).index);
                        ae.value.keys.get().set(i.value, ex.value);
                    }
                }
                ae.value.type.value = this.t;
                this.result = ae.value;
                return ;
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(SymOffExp e) {
            Ref<SymOffExp> e_ref = ref(e);
            if ((pequals(e_ref.value.type.value, this.t)) && !e_ref.value.hasOverloads)
            {
                this.result = e_ref.value;
                return ;
            }
            Ref<Type> tb = ref(this.t.toBasetype());
            Ref<Type> typeb = ref(e_ref.value.type.value.toBasetype());
            if (tb.value.equals(typeb.value))
            {
                this.result = e_ref.value.copy();
                this.result.type.value = this.t;
                ((SymOffExp)this.result).hasOverloads = false;
                return ;
            }
            if (e_ref.value.hasOverloads && ((typeb.value.ty & 0xFF) == ENUMTY.Tpointer) && ((typeb.value.nextOf().ty & 0xFF) == ENUMTY.Tfunction) && ((tb.value.ty & 0xFF) == ENUMTY.Tpointer) || ((tb.value.ty & 0xFF) == ENUMTY.Tdelegate) && ((tb.value.nextOf().ty & 0xFF) == ENUMTY.Tfunction))
            {
                Ref<FuncDeclaration> f = ref(e_ref.value.var.isFuncDeclaration());
                f.value = f.value != null ? f.value.overloadExactMatch(tb.value.nextOf()) : null;
                if (f.value != null)
                {
                    if (((tb.value.ty & 0xFF) == ENUMTY.Tdelegate))
                    {
                        if (f.value.needThis() && (hasThis(this.sc) != null))
                        {
                            this.result = new DelegateExp(e_ref.value.loc, new ThisExp(e_ref.value.loc), f.value, false, null);
                            this.result = expressionSemantic(this.result, this.sc);
                        }
                        else if (f.value.needThis())
                        {
                            e_ref.value.error(new BytePtr("no `this` to create delegate for `%s`"), f.value.toChars());
                            this.result = new ErrorExp();
                            return ;
                        }
                        else if (f.value.isNested())
                        {
                            this.result = new DelegateExp(e_ref.value.loc, literal_B6589FC6AB0DC82C(), f.value, false, null);
                            this.result = expressionSemantic(this.result, this.sc);
                        }
                        else
                        {
                            e_ref.value.error(new BytePtr("cannot cast from function pointer to delegate"));
                            this.result = new ErrorExp();
                            return ;
                        }
                    }
                    else
                    {
                        this.result = new SymOffExp(e_ref.value.loc, f.value, 0L, false);
                        this.result.type.value = this.t;
                    }
                    f.value.tookAddressOf++;
                    return ;
                }
            }
            {
                Ref<FuncDeclaration> f = ref(isFuncAddress(e_ref.value, null));
                if ((f.value) != null)
                {
                    if (f.value.checkForwardRef(e_ref.value.loc))
                    {
                        this.result = new ErrorExp();
                        return ;
                    }
                }
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(DelegateExp e) {
            Ref<DelegateExp> e_ref = ref(e);
            Ref<Type> tb = ref(this.t.toBasetype());
            Ref<Type> typeb = ref(e_ref.value.type.value.toBasetype());
            if (tb.value.equals(typeb.value) && !e_ref.value.hasOverloads)
            {
                IntRef offset = ref(0);
                e_ref.value.func.tookAddressOf++;
                if ((e_ref.value.func.tintro != null) && e_ref.value.func.tintro.nextOf().isBaseOf(e_ref.value.func.type.nextOf(), ptr(offset)) && (offset.value != 0))
                    e_ref.value.error(new BytePtr("%s"), dcast.visitmsg.value);
                this.result = e_ref.value.copy();
                this.result.type.value = this.t;
                return ;
            }
            if (((typeb.value.ty & 0xFF) == ENUMTY.Tdelegate) && ((tb.value.ty & 0xFF) == ENUMTY.Tdelegate))
            {
                if (e_ref.value.func != null)
                {
                    Ref<FuncDeclaration> f = ref(e_ref.value.func.overloadExactMatch(tb.value.nextOf()));
                    if (f.value != null)
                    {
                        IntRef offset = ref(0);
                        if ((f.value.tintro != null) && f.value.tintro.nextOf().isBaseOf(f.value.type.nextOf(), ptr(offset)) && (offset.value != 0))
                            e_ref.value.error(new BytePtr("%s"), dcast.visitmsg.value);
                        if ((!pequals(f.value, e_ref.value.func)))
                            f.value.tookAddressOf++;
                        this.result = new DelegateExp(e_ref.value.loc, e_ref.value.e1, f.value, false, e_ref.value.vthis2);
                        this.result.type.value = this.t;
                        return ;
                    }
                    if (e_ref.value.func.tintro != null)
                        e_ref.value.error(new BytePtr("%s"), dcast.visitmsg.value);
                }
            }
            {
                Ref<FuncDeclaration> f = ref(isFuncAddress(e_ref.value, null));
                if ((f.value) != null)
                {
                    if (f.value.checkForwardRef(e_ref.value.loc))
                    {
                        this.result = new ErrorExp();
                        return ;
                    }
                }
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(FuncExp e) {
            Ref<FuncExp> e_ref = ref(e);
            Ref<FuncExp> fe = ref(null);
            if ((e_ref.value.matchType(this.t, this.sc, ptr(fe), 1) > MATCH.nomatch))
            {
                this.result = fe.value;
                return ;
            }
            this.visit((Expression)e_ref);
        }

        public  void visit(CondExp e) {
            Ref<CondExp> e_ref = ref(e);
            if (!e_ref.value.type.value.equals(this.t))
            {
                this.result = new CondExp(e_ref.value.loc, e_ref.value.econd, e_ref.value.e1.value.castTo(this.sc, this.t), e_ref.value.e2.value.castTo(this.sc, this.t));
                this.result.type.value = this.t;
                return ;
            }
            this.result = e_ref.value;
        }

        public  void visit(CommaExp e) {
            Ref<CommaExp> e_ref = ref(e);
            Ref<Expression> e2c = ref(e_ref.value.e2.value.castTo(this.sc, this.t));
            if ((!pequals(e2c.value, e_ref.value.e2.value)))
            {
                this.result = new CommaExp(e_ref.value.loc, e_ref.value.e1.value, e2c.value, true);
                this.result.type.value = e2c.value.type.value;
            }
            else
            {
                this.result = e_ref.value;
                this.result.type.value = e_ref.value.e2.value.type.value;
            }
        }

        public  void visit(SliceExp e) {
            Ref<SliceExp> e_ref = ref(e);
            Ref<Type> tb = ref(this.t.toBasetype());
            Ref<Type> typeb = ref(e_ref.value.type.value.toBasetype());
            if (e_ref.value.type.value.equals(this.t) || ((typeb.value.ty & 0xFF) != ENUMTY.Tarray) || ((tb.value.ty & 0xFF) != ENUMTY.Tarray) && ((tb.value.ty & 0xFF) != ENUMTY.Tsarray))
            {
                this.visit((Expression)e_ref);
                return ;
            }
            if (((tb.value.ty & 0xFF) == ENUMTY.Tarray))
            {
                if (typeb.value.nextOf().equivalent(tb.value.nextOf()))
                {
                    this.result = e_ref.value.copy();
                    this.result.type.value = this.t;
                }
                else
                {
                    this.visit((Expression)e_ref);
                }
                return ;
            }
            Ref<TypeSArray> tsa = ref((TypeSArray)toStaticArrayType(e_ref.value));
            if ((tsa.value != null) && (tsa.value.size(e_ref.value.loc) == tb.value.size(e_ref.value.loc)))
            {
                this.result = e_ref.value.copy();
                this.result.type.value = this.t;
                return ;
            }
            if ((tsa.value != null) && tsa.value.dim.equals(((TypeSArray)tb.value).dim))
            {
                Ref<Type> t1b = ref(e_ref.value.e1.type.value.toBasetype());
                if (((t1b.value.ty & 0xFF) == ENUMTY.Tsarray))
                    t1b.value = tb.value.nextOf().sarrayOf(((TypeSArray)t1b.value).dim.toInteger());
                else if (((t1b.value.ty & 0xFF) == ENUMTY.Tarray))
                    t1b.value = tb.value.nextOf().arrayOf();
                else if (((t1b.value.ty & 0xFF) == ENUMTY.Tpointer))
                    t1b.value = tb.value.nextOf().pointerTo();
                else
                    throw new AssertionError("Unreachable code!");
                if ((e_ref.value.e1.implicitConvTo(t1b.value) > MATCH.nomatch))
                {
                    Ref<Expression> e1x = ref(e_ref.value.e1.implicitCastTo(this.sc, t1b.value));
                    assert(((e1x.value.op & 0xFF) != 127));
                    e_ref.value = (SliceExp)e_ref.value.copy();
                    e_ref.value.e1 = e1x.value;
                    e_ref.value.type.value = this.t;
                    this.result = e_ref.value;
                    return ;
                }
            }
            Slice<BytePtr> ts = toAutoQualChars(tsa.value != null ? tsa.value : e_ref.value.type.value, this.t);
            e_ref.value.error(new BytePtr("cannot cast expression `%s` of type `%s` to `%s`"), e_ref.value.toChars(), ts.get(0), ts.get(1));
            this.result = new ErrorExp();
        }


        public CastTo() {}
    }
    private static class InferType extends Visitor
    {
        private Type t = null;
        private int flag = 0;
        private Expression result = null;
        public  InferType(Type t, int flag) {
            Ref<Type> t_ref = ref(t);
            IntRef flag_ref = ref(flag);
            this.t = t_ref.value;
            this.flag = flag_ref.value;
        }

        public  void visit(Expression e) {
            Ref<Expression> e_ref = ref(e);
            this.result = e_ref.value;
        }

        public  void visit(ArrayLiteralExp ale) {
            Ref<ArrayLiteralExp> ale_ref = ref(ale);
            Ref<Type> tb = ref(this.t.toBasetype());
            if (((tb.value.ty & 0xFF) == ENUMTY.Tarray) || ((tb.value.ty & 0xFF) == ENUMTY.Tsarray))
            {
                Ref<Type> tn = ref(tb.value.nextOf());
                if (ale_ref.value.basis != null)
                    ale_ref.value.basis = inferType(ale_ref.value.basis, tn.value, this.flag);
                {
                    IntRef i = ref(0);
                    for (; (i.value < (ale_ref.value.elements.get()).length);i.value++){
                        Ref<Expression> e = ref((ale_ref.value.elements.get()).get(i.value));
                        if (e.value != null)
                        {
                            e.value = inferType(e.value, tn.value, this.flag);
                            ale_ref.value.elements.get().set(i.value, e.value);
                        }
                    }
                }
            }
            this.result = ale_ref.value;
        }

        public  void visit(AssocArrayLiteralExp aale) {
            Ref<AssocArrayLiteralExp> aale_ref = ref(aale);
            Ref<Type> tb = ref(this.t.toBasetype());
            if (((tb.value.ty & 0xFF) == ENUMTY.Taarray))
            {
                Ref<TypeAArray> taa = ref((TypeAArray)tb.value);
                Ref<Type> ti = ref(taa.value.index);
                Ref<Type> tv = ref(taa.value.nextOf());
                {
                    IntRef i = ref(0);
                    for (; (i.value < (aale_ref.value.keys.get()).length);i.value++){
                        Ref<Expression> e = ref((aale_ref.value.keys.get()).get(i.value));
                        if (e.value != null)
                        {
                            e.value = inferType(e.value, ti.value, this.flag);
                            aale_ref.value.keys.get().set(i.value, e.value);
                        }
                    }
                }
                {
                    IntRef i = ref(0);
                    for (; (i.value < (aale_ref.value.values.get()).length);i.value++){
                        Ref<Expression> e = ref((aale_ref.value.values.get()).get(i.value));
                        if (e.value != null)
                        {
                            e.value = inferType(e.value, tv.value, this.flag);
                            aale_ref.value.values.get().set(i.value, e.value);
                        }
                    }
                }
            }
            this.result = aale_ref.value;
        }

        public  void visit(FuncExp fe) {
            Ref<FuncExp> fe_ref = ref(fe);
            if (((this.t.ty & 0xFF) == ENUMTY.Tdelegate) || ((this.t.ty & 0xFF) == ENUMTY.Tpointer) && ((this.t.nextOf().ty & 0xFF) == ENUMTY.Tfunction))
            {
                fe_ref.value.fd.treq = this.t;
            }
            this.result = fe_ref.value;
        }

        public  void visit(CondExp ce) {
            Ref<CondExp> ce_ref = ref(ce);
            Ref<Type> tb = ref(this.t.toBasetype());
            ce_ref.value.e1.value = inferType(ce_ref.value.e1.value, tb.value, this.flag);
            ce_ref.value.e2.value = inferType(ce_ref.value.e2.value, tb.value, this.flag);
            this.result = ce_ref.value;
        }


        public InferType() {}
    }
    private static class IntRangeVisitor extends Visitor
    {
        private IntRange range = new IntRange();
        public  void visit(Expression e) {
            Ref<Expression> e_ref = ref(e);
            this.range = IntRange.fromType(e_ref.value.type.value).copy();
        }

        public  void visit(IntegerExp e) {
            Ref<IntegerExp> e_ref = ref(e);
            this.range = new IntRange(new SignExtendedNumber(e_ref.value.getInteger(), false))._cast(e_ref.value.type.value).copy();
        }

        public  void visit(CastExp e) {
            Ref<CastExp> e_ref = ref(e);
            this.range = getIntRange(e_ref.value.e1)._cast(e_ref.value.type.value).copy();
        }

        public  void visit(AddExp e) {
            Ref<AddExp> e_ref = ref(e);
            Ref<IntRange> ir1 = ref(getIntRange(e_ref.value.e1.value).copy());
            Ref<IntRange> ir2 = ref(getIntRange(e_ref.value.e2.value).copy());
            this.range = ir1.value.opBinary_plus(ir2.value)._cast(e_ref.value.type.value).copy();
        }

        public  void visit(MinExp e) {
            Ref<MinExp> e_ref = ref(e);
            Ref<IntRange> ir1 = ref(getIntRange(e_ref.value.e1.value).copy());
            Ref<IntRange> ir2 = ref(getIntRange(e_ref.value.e2.value).copy());
            this.range = ir1.value.opBinary_minus(ir2.value)._cast(e_ref.value.type.value).copy();
        }

        public  void visit(DivExp e) {
            Ref<DivExp> e_ref = ref(e);
            Ref<IntRange> ir1 = ref(getIntRange(e_ref.value.e1.value).copy());
            Ref<IntRange> ir2 = ref(getIntRange(e_ref.value.e2.value).copy());
            this.range = ir1.value.opBinary_div(ir2.value)._cast(e_ref.value.type.value).copy();
        }

        public  void visit(MulExp e) {
            Ref<MulExp> e_ref = ref(e);
            Ref<IntRange> ir1 = ref(getIntRange(e_ref.value.e1.value).copy());
            Ref<IntRange> ir2 = ref(getIntRange(e_ref.value.e2.value).copy());
            this.range = ir1.value.opBinary_mul(ir2.value)._cast(e_ref.value.type.value).copy();
        }

        public  void visit(ModExp e) {
            Ref<ModExp> e_ref = ref(e);
            Ref<IntRange> ir1 = ref(getIntRange(e_ref.value.e1.value).copy());
            Ref<IntRange> ir2 = ref(getIntRange(e_ref.value.e2.value).copy());
            if (!ir2.value.absNeg().imin.negative)
            {
                this.visit((Expression)e_ref);
                return ;
            }
            this.range = ir1.value.opBinary_mod(ir2.value)._cast(e_ref.value.type.value).copy();
        }

        public  void visit(AndExp e) {
            Ref<AndExp> e_ref = ref(e);
            Ref<IntRange> result = ref(new IntRange());
            Ref<Boolean> hasResult = ref(false);
            result.value.unionOrAssign(getIntRange(e_ref.value.e1.value).opBinary__(getIntRange(e_ref.value.e2.value)), hasResult);
            assert(hasResult.value);
            this.range = result.value._cast(e_ref.value.type.value).copy();
        }

        public  void visit(OrExp e) {
            Ref<OrExp> e_ref = ref(e);
            Ref<IntRange> result = ref(new IntRange());
            Ref<Boolean> hasResult = ref(false);
            result.value.unionOrAssign(getIntRange(e_ref.value.e1.value).opBinary__(getIntRange(e_ref.value.e2.value)), hasResult);
            assert(hasResult.value);
            this.range = result.value._cast(e_ref.value.type.value).copy();
        }

        public  void visit(XorExp e) {
            Ref<XorExp> e_ref = ref(e);
            Ref<IntRange> result = ref(new IntRange());
            Ref<Boolean> hasResult = ref(false);
            result.value.unionOrAssign(getIntRange(e_ref.value.e1.value).opBinary__(getIntRange(e_ref.value.e2.value)), hasResult);
            assert(hasResult.value);
            this.range = result.value._cast(e_ref.value.type.value).copy();
        }

        public  void visit(ShlExp e) {
            Ref<ShlExp> e_ref = ref(e);
            Ref<IntRange> ir1 = ref(getIntRange(e_ref.value.e1.value).copy());
            Ref<IntRange> ir2 = ref(getIntRange(e_ref.value.e2.value).copy());
            this.range = ir1.value.opBinary_ll(ir2.value)._cast(e_ref.value.type.value).copy();
        }

        public  void visit(ShrExp e) {
            Ref<ShrExp> e_ref = ref(e);
            Ref<IntRange> ir1 = ref(getIntRange(e_ref.value.e1.value).copy());
            Ref<IntRange> ir2 = ref(getIntRange(e_ref.value.e2.value).copy());
            this.range = ir1.value.opBinary_rr(ir2.value)._cast(e_ref.value.type.value).copy();
        }

        public  void visit(UshrExp e) {
            Ref<UshrExp> e_ref = ref(e);
            Ref<IntRange> ir1 = ref(getIntRange(e_ref.value.e1.value).castUnsigned(e_ref.value.e1.value.type.value).copy());
            Ref<IntRange> ir2 = ref(getIntRange(e_ref.value.e2.value).copy());
            this.range = ir1.value.opBinary_rrr(ir2.value)._cast(e_ref.value.type.value).copy();
        }

        public  void visit(AssignExp e) {
            Ref<AssignExp> e_ref = ref(e);
            this.range = getIntRange(e_ref.value.e2.value)._cast(e_ref.value.type.value).copy();
        }

        public  void visit(CondExp e) {
            Ref<CondExp> e_ref = ref(e);
            Ref<IntRange> ir1 = ref(getIntRange(e_ref.value.e1.value).copy());
            IntRange ir2 = getIntRange(e_ref.value.e2.value).copy();
            this.range = ir1.value.unionWith(ir2)._cast(e_ref.value.type.value).copy();
        }

        public  void visit(VarExp e) {
            Ref<VarExp> e_ref = ref(e);
            Ref<Expression> ie = ref(null);
            Ref<VarDeclaration> vd = ref(e_ref.value.var.isVarDeclaration());
            if ((vd.value != null) && (vd.value.range != null))
                this.range = (vd.value.range.get())._cast(e_ref.value.type.value).copy();
            else if ((vd.value != null) && (vd.value._init != null) && !vd.value.type.isMutable() && ((ie.value = vd.value.getConstInitializer(true)) != null))
                ie.value.accept(this);
            else
                this.visit((Expression)e_ref);
        }

        public  void visit(CommaExp e) {
            Ref<CommaExp> e_ref = ref(e);
            e_ref.value.e2.value.accept(this);
        }

        public  void visit(ComExp e) {
            Ref<ComExp> e_ref = ref(e);
            Ref<IntRange> ir = ref(getIntRange(e_ref.value.e1).copy());
            this.range = new IntRange(new SignExtendedNumber(~ir.value.imax.value, !ir.value.imax.negative), new SignExtendedNumber(~ir.value.imin.value, !ir.value.imin.negative))._cast(e_ref.value.type.value).copy();
        }

        public  void visit(NegExp e) {
            Ref<NegExp> e_ref = ref(e);
            Ref<IntRange> ir = ref(getIntRange(e_ref.value.e1).copy());
            this.range = ir.value.opUnary_minus()._cast(e_ref.value.type.value).copy();
        }


        public IntRangeVisitor() {}
    }

    static boolean LOG = false;
    public static Expression implicitCastTo(Expression e, Ptr<Scope> sc, Type t) {
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
                return e.type.value.toBasetype().nextOf().sarrayOf((long)len);
            }
        }
        else
        {
            Type t1b = e.e1.type.value.toBasetype();
            if (((t1b.ty & 0xFF) == ENUMTY.Tsarray))
                return t1b;
        }
        return null;
    }

    public static Expression tryAliasThisCast(Expression e, Ptr<Scope> sc, Type tob, Type t1b, Type t) {
        Expression result = null;
        AggregateDeclaration t1ad = isAggregate(t1b);
        if (t1ad == null)
            return null;
        AggregateDeclaration toad = isAggregate(tob);
        if ((pequals(t1ad, toad)) || (t1ad.aliasthis == null))
            return null;
        result = resolveAliasThis(sc, e, false);
        int errors = global.value.startGagging();
        result = result.castTo(sc, t);
        return global.value.endGagging(errors) ? null : result;
    }

    public static Expression castTo(Expression e, Ptr<Scope> sc, Type t) {
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

    // defaulted all parameters starting with #3
    public static Expression inferType(Expression e, Type t) {
        return inferType(e, t, 0);
    }

    public static Expression scaleFactor(BinExp be, Ptr<Scope> sc) {
        Type t1b = be.e1.value.type.value.toBasetype();
        Type t2b = be.e2.value.type.value.toBasetype();
        Expression eoff = null;
        if (((t1b.ty & 0xFF) == ENUMTY.Tpointer) && t2b.isintegral())
        {
            Type t = Type.tptrdiff_t;
            long stride = t1b.nextOf().size(be.loc);
            if (!t.equals(t2b))
                be.e2.value = be.e2.value.castTo(sc, t);
            eoff = be.e2.value;
            be.e2.value = new MulExp(be.loc, be.e2.value, new IntegerExp(Loc.initial.value, stride, t));
            be.e2.value.type.value = t;
            be.type.value = be.e1.value.type.value;
        }
        else if (((t2b.ty & 0xFF) == ENUMTY.Tpointer) && t1b.isintegral())
        {
            Type t = Type.tptrdiff_t;
            Expression e = null;
            long stride = t2b.nextOf().size(be.loc);
            if (!t.equals(t1b))
                e = be.e1.value.castTo(sc, t);
            else
                e = be.e1.value;
            eoff = e;
            e = new MulExp(be.loc, e, new IntegerExp(Loc.initial.value, stride, t));
            e.type.value = t;
            be.type.value = be.e2.value.type.value;
            be.e1.value = be.e2.value;
            be.e2.value = e;
        }
        else
            throw new AssertionError("Unreachable code!");
        if (((sc.get()).func != null) && ((sc.get()).intypeof == 0))
        {
            eoff = eoff.optimize(0, false);
            if (((eoff.op & 0xFF) == 135) && (eoff.toInteger() == 0L))
            {
            }
            else if ((sc.get()).func.setUnsafe())
            {
                be.error(new BytePtr("pointer arithmetic not allowed in @safe functions"));
                return new ErrorExp();
            }
        }
        return be;
    }

    public static boolean isVoidArrayLiteral(Expression e, Type other) {
        for (; ((e.op & 0xFF) == 47) && ((e.type.value.ty & 0xFF) == ENUMTY.Tarray) && ((((ArrayLiteralExp)e).elements.get()).length == 1);){
            ArrayLiteralExp ale = (ArrayLiteralExp)e;
            e = ale.getElement(0);
            if (((other.ty & 0xFF) == ENUMTY.Tsarray) || ((other.ty & 0xFF) == ENUMTY.Tarray))
                other = other.nextOf();
            else
                return false;
        }
        if (((other.ty & 0xFF) != ENUMTY.Tsarray) && ((other.ty & 0xFF) != ENUMTY.Tarray))
            return false;
        Type t = e.type.value;
        return ((e.op & 0xFF) == 47) && ((t.ty & 0xFF) == ENUMTY.Tarray) && ((t.nextOf().ty & 0xFF) == ENUMTY.Tvoid) && ((((ArrayLiteralExp)e).elements.get()).length == 0);
    }

    public static boolean typeMerge(Ptr<Scope> sc, byte op, Ptr<Type> pt, Ptr<Expression> pe1, Ptr<Expression> pe2) {
        Ref<Ptr<Scope>> sc_ref = ref(sc);
        Ref<Ptr<Type>> pt_ref = ref(pt);
        Ref<Ptr<Expression>> pe1_ref = ref(pe1);
        Ref<Ptr<Expression>> pe2_ref = ref(pe2);
        int m = MATCH.nomatch;
        Ref<Expression> e1 = ref(pe1_ref.value.get());
        Ref<Expression> e2 = ref(pe2_ref.value.get());
        Ref<Type> t1 = ref(e1.value.type.value);
        Ref<Type> t2 = ref(e2.value.type.value);
        Type t1b = e1.value.type.value.toBasetype();
        Type t2b = e2.value.type.value.toBasetype();
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
        t1.value = e1.value.type.value;
        t2.value = e2.value.type.value;
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
                        if ((att1 != null) && (pequals(e1.value.type.value, att1)))
                            return Lincompatible.invoke();
                        if ((att1 == null) && e1.value.type.value.checkAliasThisRec())
                            att1 = e1.value.type.value;
                        e1.value = resolveAliasThis(sc_ref.value, e1.value, false);
                        t1.value = e1.value.type.value;
                        continue;
                    }
                    else if (((t2.value.ty & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)t2.value).sym.aliasthis != null))
                    {
                        if ((att2 != null) && (pequals(e2.value.type.value, att2)))
                            return Lincompatible.invoke();
                        if ((att2 == null) && e2.value.type.value.checkAliasThisRec())
                            att2 = e2.value.type.value;
                        e2.value = resolveAliasThis(sc_ref.value, e2.value, false);
                        t2.value = e2.value.type.value;
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
                        if ((att2 != null) && (pequals(e2.value.type.value, att2)))
                            return Lincompatible.invoke();
                        if ((att2 == null) && e2.value.type.value.checkAliasThisRec())
                            att2 = e2.value.type.value;
                        e2b = resolveAliasThis(sc_ref.value, e2.value, false);
                        i1 = e2b.implicitConvTo(t1.value);
                    }
                    if (ts1.sym.aliasthis != null)
                    {
                        if ((att1 != null) && (pequals(e1.value.type.value, att1)))
                            return Lincompatible.invoke();
                        if ((att1 == null) && e1.value.type.value.checkAliasThisRec())
                            att1 = e1.value.type.value;
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
                        t1.value = e1b.type.value.toBasetype();
                    }
                    if (e2b != null)
                    {
                        e2.value = e2b;
                        t2.value = e2b.type.value.toBasetype();
                    }
                    t.value = t1.value;
                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                }
            }
            else if (((t1.value.ty & 0xFF) == ENUMTY.Tstruct) || ((t2.value.ty & 0xFF) == ENUMTY.Tstruct))
            {
                if (((t1.value.ty & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)t1.value).sym.aliasthis != null))
                {
                    if ((att1 != null) && (pequals(e1.value.type.value, att1)))
                        return Lincompatible.invoke();
                    if ((att1 == null) && e1.value.type.value.checkAliasThisRec())
                        att1 = e1.value.type.value;
                    e1.value = resolveAliasThis(sc_ref.value, e1.value, false);
                    t1.value = e1.value.type.value;
                    t.value = t1.value;
                    /*goto Lagain*/throw Dispatch0.INSTANCE;
                }
                if (((t2.value.ty & 0xFF) == ENUMTY.Tstruct) && (((TypeStruct)t2.value).sym.aliasthis != null))
                {
                    if ((att2 != null) && (pequals(e2.value.type.value, att2)))
                        return Lincompatible.invoke();
                    if ((att2 == null) && e2.value.type.value.checkAliasThisRec())
                        att2 = e2.value.type.value;
                    e2.value = resolveAliasThis(sc_ref.value, e2.value, false);
                    t2.value = e2.value.type.value;
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
                    t1.value = e1.value.type.value;
                    t2.value = e2.value.type.value;
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
                else if (t1.value.nextOf().implicitConvTo(e2.value.type.value) != 0)
                {
                    t.value = e2.value.type.value.arrayOf();
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
                else if (t2.value.nextOf().implicitConvTo(e1.value.type.value) != 0)
                {
                    t.value = e1.value.type.value.arrayOf();
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

    public static Expression typeCombine(BinExp be, Ptr<Scope> sc) {
        Ref<BinExp> be_ref = ref(be);
        Function0<Expression> errorReturn = new Function0<Expression>(){
            public Expression invoke() {
                Ref<Expression> ex = ref(be_ref.value.incompatibleTypes());
                if (((ex.value.op & 0xFF) == 127))
                    return ex.value;
                return new ErrorExp();
            }
        };
        Type t1 = be_ref.value.e1.value.type.value.toBasetype();
        Type t2 = be_ref.value.e2.value.type.value.toBasetype();
        if (((be_ref.value.op & 0xFF) == 75) || ((be_ref.value.op & 0xFF) == 74))
        {
            if (((t1.ty & 0xFF) == ENUMTY.Tstruct) && ((t2.ty & 0xFF) == ENUMTY.Tstruct))
                return errorReturn.invoke();
            else if (((t1.ty & 0xFF) == ENUMTY.Tclass) && ((t2.ty & 0xFF) == ENUMTY.Tclass))
                return errorReturn.invoke();
            else if (((t1.ty & 0xFF) == ENUMTY.Taarray) && ((t2.ty & 0xFF) == ENUMTY.Taarray))
                return errorReturn.invoke();
        }
        if (!typeMerge(sc, be_ref.value.op, ptr(be_ref.value.type.value), ptr(be_ref.value.e1.value), ptr(be_ref.value.e2.value)))
            return errorReturn.invoke();
        if (((be_ref.value.e1.value.op & 0xFF) == 127))
            return be_ref.value.e1.value;
        if (((be_ref.value.e2.value.op & 0xFF) == 127))
            return be_ref.value.e2.value;
        return null;
    }

    public static Expression integralPromotions(Expression e, Ptr<Scope> sc) {
        switch ((e.type.value.toBasetype().ty & 0xFF))
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
                e = e.castTo(sc, Type.tint32.value);
                break;
            case 33:
                e = e.castTo(sc, Type.tuns32);
                break;
            default:
            break;
        }
        return e;
    }

    public static Expression charPromotions(Expression e, Ptr<Scope> sc) {
        switch ((e.type.value.toBasetype().ty & 0xFF))
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

    public static void fix16997(Ptr<Scope> sc, UnaExp ue) {
        if (global.value.params.fix16997)
            ue.e1 = integralPromotions(ue.e1, sc);
        else
        {
            switch ((ue.e1.type.value.toBasetype().ty & 0xFF))
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
